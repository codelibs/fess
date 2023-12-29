/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.indexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.lang.ThreadUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.Crawler;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.AccessResultData;
import org.codelibs.fess.crawler.entity.EsAccessResult;
import org.codelibs.fess.crawler.entity.EsUrlQueue;
import org.codelibs.fess.crawler.service.DataService;
import org.codelibs.fess.crawler.service.UrlFilterService;
import org.codelibs.fess.crawler.service.UrlQueueService;
import org.codelibs.fess.crawler.service.impl.EsDataService;
import org.codelibs.fess.crawler.transformer.Transformer;
import org.codelibs.fess.crawler.util.EsResultList;
import org.codelibs.fess.es.client.SearchEngineClient;
import org.codelibs.fess.es.log.exbhv.ClickLogBhv;
import org.codelibs.fess.es.log.exbhv.FavoriteLogBhv;
import org.codelibs.fess.exception.ContainerNotAvailableException;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.helper.IndexingHelper;
import org.codelibs.fess.helper.IntervalControlHelper;
import org.codelibs.fess.helper.SearchLogHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.ingest.IngestFactory;
import org.codelibs.fess.ingest.Ingester;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocList;
import org.codelibs.fess.util.MemoryUtil;
import org.codelibs.fess.util.ThreadDumpUtil;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.sort.SortOrder;

public class IndexUpdater extends Thread {
    private static final Logger logger = LogManager.getLogger(IndexUpdater.class);

    protected List<String> sessionIdList;

    @Resource
    protected SearchEngineClient searchEngineClient;

    @Resource
    protected DataService<EsAccessResult> dataService;

    @Resource
    protected UrlQueueService<EsUrlQueue> urlQueueService;

    @Resource
    protected UrlFilterService urlFilterService;

    @Resource
    protected ClickLogBhv clickLogBhv;

    @Resource
    protected FavoriteLogBhv favoriteLogBhv;

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected IndexingHelper indexingHelper;

    protected boolean finishCrawling = false;

    protected long executeTime;

    protected long documentSize;

    protected int maxIndexerErrorCount = 0;

    protected int maxErrorCount = 2;

    protected List<String> finishedSessionIdList = new ArrayList<>();

    private final List<DocBoostMatcher> docBoostMatcherList = new ArrayList<>();

    private List<Crawler> crawlerList;

    private IngestFactory ingestFactory = null;

    public IndexUpdater() {
        // nothing
    }

    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        if (ComponentUtil.hasIngestFactory()) {
            ingestFactory = ComponentUtil.getIngestFactory();
        }
    }

    @PreDestroy
    public void destroy() {
        if (!finishCrawling) {
            if (logger.isInfoEnabled()) {
                logger.info("Stopping all crawler.");
            }
            forceStop();
        }
    }

    public void addFinishedSessionId(final String sessionId) {
        synchronized (finishedSessionIdList) {
            finishedSessionIdList.add(sessionId);
        }
    }

    private void deleteBySessionId(final String sessionId) {
        try {
            urlFilterService.delete(sessionId);
        } catch (final Exception e) {
            logger.warn("Failed to delete url filters: {}", sessionId, e);
        }
        try {
            urlQueueService.delete(sessionId);
        } catch (final Exception e) {
            logger.warn("Failed to delete url queues: {}", sessionId, e);
        }
        try {
            dataService.delete(sessionId);
        } catch (final Exception e) {
            logger.warn("Failed to delete data: {}", sessionId, e);
        }
    }

    @Override
    public void run() {
        if (dataService == null) {
            throw new FessSystemException("DataService is null.");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Starting indexUpdater.");
        }

        executeTime = 0;
        documentSize = 0;

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final long updateInterval = fessConfig.getIndexerWebfsUpdateIntervalAsInteger().longValue();
        final int maxEmptyListCount = fessConfig.getIndexerWebfsMaxEmptyListCountAsInteger();
        final IntervalControlHelper intervalControlHelper = ComponentUtil.getIntervalControlHelper();
        try {
            final Consumer<SearchRequestBuilder> cb = builder -> {
                final QueryBuilder queryBuilder =
                        QueryBuilders.boolQuery().filter(QueryBuilders.termsQuery(EsAccessResult.SESSION_ID, sessionIdList))
                                .filter(QueryBuilders.termQuery(EsAccessResult.STATUS, org.codelibs.fess.crawler.Constants.OK_STATUS));
                builder.setQuery(queryBuilder);
                builder.setFrom(0);
                final int maxDocumentCacheSize = fessConfig.getIndexerWebfsMaxDocumentCacheSizeAsInteger();
                builder.setSize(maxDocumentCacheSize <= 0 ? 1 : maxDocumentCacheSize);
                builder.addSort(EsAccessResult.CREATE_TIME, SortOrder.ASC);
            };

            final DocList docList = new DocList();
            final List<EsAccessResult> accessResultList = new ArrayList<>();

            long updateTime = System.currentTimeMillis();
            int errorCount = 0;
            int emptyListCount = 0;
            long cleanupTime = -1;
            while (!finishCrawling || !accessResultList.isEmpty()) {
                try {
                    final int sessionIdListSize = finishedSessionIdList.size();
                    intervalControlHelper.setCrawlerRunning(true);

                    docList.clear();
                    accessResultList.clear();

                    updateTime = System.currentTimeMillis() - updateTime;

                    final long interval = updateInterval - updateTime;
                    if (interval > 0) {
                        // sleep
                        ThreadUtil.sleep(interval); // 10 sec (default)
                    }

                    systemHelper.calibrateCpuLoad();
                    systemHelper.waitForNoWaitingThreads();

                    intervalControlHelper.delayByRules();

                    if (logger.isDebugEnabled()) {
                        logger.debug("Processing documents in IndexUpdater queue.");
                    }

                    updateTime = System.currentTimeMillis();

                    List<EsAccessResult> arList = getAccessResultList(cb, cleanupTime);
                    if (arList.isEmpty()) {
                        emptyListCount++;
                    } else {
                        emptyListCount = 0; // reset
                    }
                    long hitCount = ((EsResultList<EsAccessResult>) arList).getTotalHits();
                    while (hitCount > 0) {
                        if (arList.isEmpty()) {
                            ThreadUtil.sleep(fessConfig.getIndexerWebfsCommitMarginTimeAsInteger().longValue());
                            cleanupTime = -1;
                        } else {
                            processAccessResults(docList, accessResultList, arList);
                            cleanupTime = cleanupAccessResults(accessResultList);
                        }
                        arList = getAccessResultList(cb, cleanupTime);
                        hitCount = ((EsResultList<EsAccessResult>) arList).getTotalHits();
                    }
                    if (!docList.isEmpty()) {
                        indexingHelper.sendDocuments(searchEngineClient, docList);
                    }

                    synchronized (finishedSessionIdList) {
                        if (sessionIdListSize != 0 && sessionIdListSize == finishedSessionIdList.size()) {
                            cleanupFinishedSessionData();
                        }
                    }
                    executeTime += System.currentTimeMillis() - updateTime;

                    if (logger.isDebugEnabled()) {
                        logger.debug("Processed documents in IndexUpdater queue.");
                    }

                    // reset count
                    errorCount = 0;
                } catch (final Exception e) {
                    if (errorCount > maxErrorCount) {
                        throw e;
                    }
                    errorCount++;
                    logger.warn("Failed to access data. Retry to access it {} times.", errorCount, e);
                } finally {
                    if (systemHelper.isForceStop()) {
                        finishCrawling = true;
                        if (logger.isDebugEnabled()) {
                            logger.debug("Stopped indexUpdater.");
                        }
                    }
                }

                if (emptyListCount >= maxEmptyListCount) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Terminating indexUpdater. emptyListCount is over {}.", maxEmptyListCount);
                    }
                    // terminate crawling
                    finishCrawling = true;
                    forceStop();
                    if (fessConfig.getIndexerThreadDumpEnabledAsBoolean()) {
                        ThreadDumpUtil.printThreadDump();
                    }
                    org.codelibs.fess.exec.Crawler.addError("QueueTimeout");
                }

                if (!ComponentUtil.available()) {
                    logger.info("IndexUpdater is terminated.");
                    forceStop();
                    break;
                }
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Finished indexUpdater.");
            }
        } catch (final ContainerNotAvailableException e) {
            if (logger.isDebugEnabled()) {
                logger.error("IndexUpdater is terminated.", e);
            } else if (logger.isInfoEnabled()) {
                logger.info("IndexUpdater is terminated.");
            }
            forceStop();
        } catch (final Throwable t) {
            if (ComponentUtil.available()) {
                logger.error("IndexUpdater is terminated.", t);
            } else if (logger.isDebugEnabled()) {
                logger.error("IndexUpdater is terminated.", t);
                org.codelibs.fess.exec.Crawler.addError(t.getClass().getSimpleName());
            } else if (logger.isInfoEnabled()) {
                logger.info("IndexUpdater is terminated.");
                org.codelibs.fess.exec.Crawler.addError(t.getClass().getSimpleName());
            }
            forceStop();
        } finally {
            intervalControlHelper.setCrawlerRunning(true);
        }

        if (logger.isInfoEnabled()) {
            logger.info("[EXEC TIME] index update time: {}ms", executeTime);
        }

    }

    private void processAccessResults(final DocList docList, final List<EsAccessResult> accessResultList,
            final List<EsAccessResult> arList) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final long maxDocumentRequestSize = Long.parseLong(fessConfig.getIndexerWebfsMaxDocumentRequestSize());
        for (final EsAccessResult accessResult : arList) {
            if (logger.isDebugEnabled()) {
                logger.debug("Indexing {}", accessResult.getUrl());
            }
            accessResult.setStatus(Constants.DONE_STATUS);
            accessResultList.add(accessResult);

            if (accessResult.getHttpStatusCode() != 200) {
                // invalid page
                if (logger.isDebugEnabled()) {
                    logger.debug("Skipped. The response code is {}.", accessResult.getHttpStatusCode());
                }
                continue;
            }

            final long startTime = System.currentTimeMillis();
            final AccessResultData<?> accessResultData = getAccessResultData(accessResult);
            if (accessResultData != null) {
                accessResult.setAccessResultData(null);
                try {
                    final Transformer transformer = ComponentUtil.getComponent(accessResultData.getTransformerName());
                    if (transformer == null) {
                        // no transformer
                        logger.warn("No transformer: {}", accessResultData.getTransformerName());
                        continue;
                    }
                    @SuppressWarnings("unchecked")
                    final Map<String, Object> map = (Map<String, Object>) transformer.getData(accessResultData);
                    if (map.isEmpty()) {
                        // no transformer
                        logger.warn("No data: {}", accessResult.getUrl());
                        continue;
                    }

                    if (Constants.FALSE.equals(map.get(Constants.INDEXING_TARGET))) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Skipped. This document is not a index target. ");
                        }
                        continue;
                    }
                    map.remove(Constants.INDEXING_TARGET);

                    updateDocument(map);

                    docList.add(ingest(accessResult, map));
                    final long contentSize = indexingHelper.calculateDocumentSize(map);
                    docList.addContentSize(contentSize);
                    final long processingTime = System.currentTimeMillis() - startTime;
                    docList.addProcessingTime(processingTime);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Added the document({}, {}ms). The number of a document cache is {} (size: {}).",
                                MemoryUtil.byteCountToDisplaySize(contentSize), processingTime, docList.size(), docList.getContentSize());
                    }

                    if (docList.getContentSize() >= maxDocumentRequestSize) {
                        indexingHelper.sendDocuments(searchEngineClient, docList);
                    }
                    documentSize++;
                    if (logger.isDebugEnabled()) {
                        logger.debug("The number of an added document is {}.", documentSize);
                    }
                } catch (final Exception e) {
                    logger.warn("Could not add a doc: {}", accessResult.getUrl(), e);
                }
            } else if (logger.isDebugEnabled()) {
                logger.debug("Skipped. No content. ");
            }

        }
    }

    private AccessResultData<?> getAccessResultData(final EsAccessResult accessResult) {
        try {
            return accessResult.getAccessResultData();
        } catch (final Exception e) {
            logger.warn("Failed to get data from {}", accessResult.getUrl(), e);
        }
        return null;
    }

    protected Map<String, Object> ingest(final AccessResult<String> accessResult, final Map<String, Object> map) {
        if (ingestFactory == null) {
            return map;
        }
        Map<String, Object> target = map;
        for (final Ingester ingester : ingestFactory.getIngesters()) {
            try {
                target = ingester.process(target, accessResult);
            } catch (final Exception e) {
                logger.warn("Failed to process Ingest[{}]", ingester.getClass().getSimpleName(), e);
            }
        }
        return target;
    }

    protected void updateDocument(final Map<String, Object> map) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        if (fessConfig.getIndexerClickCountEnabledAsBoolean()) {
            addClickCountField(map);
        }

        if (fessConfig.getIndexerFavoriteCountEnabledAsBoolean()) {
            addFavoriteCountField(map);
        }

        float documentBoost = 0.0f;
        for (final DocBoostMatcher docBoostMatcher : docBoostMatcherList) {
            if (docBoostMatcher.match(map)) {
                documentBoost = docBoostMatcher.getValue(map);
                break;
            }
        }

        if (documentBoost > 0) {
            addBoostValue(map, documentBoost);
        }

        if (!map.containsKey(fessConfig.getIndexFieldDocId())) {
            map.put(fessConfig.getIndexFieldDocId(), systemHelper.generateDocId(map));
        }

        ComponentUtil.getLanguageHelper().updateDocument(map);
    }

    protected void addBoostValue(final Map<String, Object> map, final float documentBoost) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        map.put(fessConfig.getIndexFieldBoost(), documentBoost);
        if (logger.isDebugEnabled()) {
            logger.debug("Set a document boost ({}).", documentBoost);
        }
    }

    protected void addClickCountField(final Map<String, Object> doc) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String url = (String) doc.get(fessConfig.getIndexFieldUrl());
        if (StringUtil.isNotBlank(url)) {
            final SearchLogHelper searchLogHelper = ComponentUtil.getSearchLogHelper();
            final int count = searchLogHelper.getClickCount(url);
            doc.put(fessConfig.getIndexFieldClickCount(), count);
            if (logger.isDebugEnabled()) {
                logger.debug("Click Count: {}, url: {}", count, url);
            }
        }
    }

    protected void addFavoriteCountField(final Map<String, Object> map) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String url = (String) map.get(fessConfig.getIndexFieldUrl());
        if (StringUtil.isNotBlank(url)) {
            final SearchLogHelper searchLogHelper = ComponentUtil.getSearchLogHelper();
            final long count = searchLogHelper.getFavoriteCount(url);
            map.put(fessConfig.getIndexFieldFavoriteCount(), count);
            if (logger.isDebugEnabled()) {
                logger.debug("Favorite Count: {}, url: {}", count, url);
            }
        }
    }

    private long cleanupAccessResults(final List<EsAccessResult> accessResultList) {
        if (!accessResultList.isEmpty()) {
            final long execTime = System.currentTimeMillis();
            final int size = accessResultList.size();
            dataService.update(accessResultList);
            accessResultList.clear();
            final long time = System.currentTimeMillis() - execTime;
            if (logger.isDebugEnabled()) {
                logger.debug("Updated {} access results. The execution time is {}ms.", size, time);
            }
            return time;
        }
        return -1;
    }

    private List<EsAccessResult> getAccessResultList(final Consumer<SearchRequestBuilder> cb, final long cleanupTime) {
        if (logger.isDebugEnabled()) {
            logger.debug("Getting documents in IndexUpdater queue.");
        }
        final long execTime = System.currentTimeMillis();
        final List<EsAccessResult> arList = ((EsDataService) dataService).getAccessResultList(cb);
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!arList.isEmpty()) {
            final long commitMarginTime = fessConfig.getIndexerWebfsCommitMarginTimeAsInteger().longValue();
            for (final AccessResult<?> ar : arList.toArray(new AccessResult[arList.size()])) {
                if (ar.getCreateTime().longValue() > execTime - commitMarginTime) {
                    arList.remove(ar);
                }
            }
        }
        final long totalHits = ((EsResultList<EsAccessResult>) arList).getTotalHits();
        if (logger.isInfoEnabled()) {
            final StringBuilder buf = new StringBuilder(100);
            buf.append("Processing ");
            if (totalHits > 0) {
                buf.append(arList.size()).append('/').append(totalHits).append(" docs (Doc:{access ");
            } else {
                buf.append("no docs in indexing queue (Doc:{access ");
            }
            buf.append(System.currentTimeMillis() - execTime).append("ms");
            if (cleanupTime >= 0) {
                buf.append(", cleanup ").append(cleanupTime).append("ms");
            }
            buf.append("}, ");
            buf.append(MemoryUtil.getMemoryUsageLog());
            buf.append(')');
            logger.info(buf.toString());
        }
        final long unprocessedDocumentSize = fessConfig.getIndexerUnprocessedDocumentSizeAsInteger().longValue();
        final IntervalControlHelper intervalControlHelper = ComponentUtil.getIntervalControlHelper();
        if (totalHits > unprocessedDocumentSize && intervalControlHelper.isCrawlerRunning()) {
            if (logger.isInfoEnabled()) {
                logger.info("Stopped all crawler threads. You have {} (>{}) unprocessed docs.", totalHits, unprocessedDocumentSize);
            }
            intervalControlHelper.setCrawlerRunning(false);
        }
        return arList;
    }

    private void cleanupFinishedSessionData() {
        final long execTime = System.currentTimeMillis();
        // cleanup
        for (final String sessionId : finishedSessionIdList) {
            final long execTime2 = System.currentTimeMillis();
            if (logger.isDebugEnabled()) {
                logger.debug("Deleting document data: {}", sessionId);
            }
            deleteBySessionId(sessionId);
            if (logger.isDebugEnabled()) {
                logger.debug("Deleted {} documents. The execution time is {}ms.", sessionId, (System.currentTimeMillis() - execTime2));
            }
        }
        finishedSessionIdList.clear();

        if (logger.isInfoEnabled()) {
            logger.info("Deleted completed document data. The execution time is {}ms.", (System.currentTimeMillis() - execTime));
        }
    }

    private void forceStop() {
        systemHelper.setForceStop(true);
        for (final Crawler crawler : crawlerList) {
            crawler.stop();
        }
    }

    public long getExecuteTime() {
        return executeTime;
    }

    public List<String> getSessionIdList() {
        return sessionIdList;
    }

    public void setSessionIdList(final List<String> sessionIdList) {
        this.sessionIdList = sessionIdList;
    }

    public void setFinishCrawling(final boolean finishCrawling) {
        this.finishCrawling = finishCrawling;
    }

    public long getDocumentSize() {
        return documentSize;
    }

    @Override
    public void setUncaughtExceptionHandler(final UncaughtExceptionHandler eh) {
        super.setUncaughtExceptionHandler(eh);
    }

    public static void setDefaultUncaughtExceptionHandler(final UncaughtExceptionHandler eh) {
        Thread.setDefaultUncaughtExceptionHandler(eh);
    }

    public void setMaxIndexerErrorCount(final int maxIndexerErrorCount) {
        this.maxIndexerErrorCount = maxIndexerErrorCount;
    }

    public void addDocBoostMatcher(final DocBoostMatcher rule) {
        docBoostMatcherList.add(rule);
    }

    public void setCrawlerList(final List<Crawler> crawlerList) {
        this.crawlerList = crawlerList;
    }
}
