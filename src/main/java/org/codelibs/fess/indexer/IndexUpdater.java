/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
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
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.es.log.exbhv.ClickLogBhv;
import org.codelibs.fess.es.log.exbhv.FavoriteLogBhv;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.helper.IndexingHelper;
import org.codelibs.fess.helper.IntervalControlHelper;
import org.codelibs.fess.helper.SearchLogHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.lastaflute.di.core.SingletonLaContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexUpdater extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(IndexUpdater.class);

    protected List<String> sessionIdList;

    @Resource
    protected FessEsClient fessEsClient;

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

    public int maxDocumentCacheSize = 5;

    public int maxInvalidDocumentSize = 100;

    protected boolean finishCrawling = false;

    public long updateInterval = 60000; // 1 min

    protected long executeTime;

    protected long documentSize;

    protected int maxIndexerErrorCount = 0;

    protected int maxErrorCount = 2;

    protected int unprocessedDocumentSize = 100;

    protected List<String> finishedSessionIdList = new ArrayList<String>();

    public long commitMarginTime = 10000; // 10ms

    public int maxEmptyListCount = 60; // 1hour

    public boolean threadDump = false;

    public boolean clickCountEnabled = true;

    public boolean favoriteCountEnabled = true;

    private final List<BoostDocumentRule> boostRuleList = new ArrayList<BoostDocumentRule>();

    private final Map<String, Object> docValueMap = new HashMap<String, Object>();

    private List<Crawler> crawlerList;

    public IndexUpdater() {
        // nothing
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
            logger.warn("Failed to delete url filters: " + sessionId, e);
        }
        try {
            urlQueueService.delete(sessionId);
        } catch (final Exception e) {
            logger.warn("Failed to delete url queues: " + sessionId, e);
        }
        try {
            dataService.delete(sessionId);
        } catch (final Exception e) {
            logger.warn("Failed to delete data: " + sessionId, e);
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

        final IntervalControlHelper intervalControlHelper = ComponentUtil.getIntervalControlHelper();
        try {
            final Consumer<SearchRequestBuilder> cb =
                    builder -> {
                        final QueryBuilder queryBuilder =
                                QueryBuilders
                                        .boolQuery()
                                        .filter(QueryBuilders.termsQuery(EsAccessResult.SESSION_ID, sessionIdList))
                                        .filter(QueryBuilders.termQuery(EsAccessResult.STATUS,
                                                org.codelibs.fess.crawler.Constants.OK_STATUS));
                        builder.setQuery(queryBuilder);
                        builder.setFrom(0);
                        if (maxDocumentCacheSize <= 0) {
                            maxDocumentCacheSize = 1;
                        }
                        builder.setSize(maxDocumentCacheSize);
                        builder.addSort(EsAccessResult.CREATE_TIME, SortOrder.ASC);
                    };

            final List<Map<String, Object>> docList = new ArrayList<>();
            final List<EsAccessResult> accessResultList = new ArrayList<>();

            long updateTime = System.currentTimeMillis();
            int errorCount = 0;
            int emptyListCount = 0;
            while (!finishCrawling || !accessResultList.isEmpty()) {
                try {
                    final int sessionIdListSize = finishedSessionIdList.size();
                    intervalControlHelper.setCrawlerRunning(true);

                    updateTime = System.currentTimeMillis() - updateTime;

                    final long interval = updateInterval - updateTime;
                    if (interval > 0) {
                        // sleep
                        try {
                            Thread.sleep(interval); // 1 min (default)
                        } catch (final InterruptedException e) {
                            logger.warn("Interrupted index update.", e);
                        }
                    }

                    docList.clear();
                    accessResultList.clear();

                    intervalControlHelper.delayByRules();

                    if (logger.isDebugEnabled()) {
                        logger.debug("Processing documents in IndexUpdater queue.");
                    }

                    updateTime = System.currentTimeMillis();

                    List<EsAccessResult> arList = getAccessResultList(cb);
                    if (arList.isEmpty()) {
                        emptyListCount++;
                    } else {
                        emptyListCount = 0; // reset
                    }
                    while (!arList.isEmpty()) {
                        processAccessResults(docList, accessResultList, arList);

                        cleanupAccessResults(accessResultList);

                        if (logger.isDebugEnabled()) {
                            logger.debug("Getting documents in IndexUpdater queue.");
                        }
                        arList = getAccessResultList(cb);
                    }

                    if (!docList.isEmpty()) {
                        indexingHelper.sendDocuments(fessEsClient, docList);
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
                    logger.warn("Failed to access data. Retry to access.. " + errorCount, e);
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
                        logger.info("Terminating indexUpdater. " + "emptyListCount is over " + maxEmptyListCount + ".");
                    }
                    // terminate crawling
                    finishCrawling = true;
                    forceStop();
                    if (threadDump) {
                        printThreadDump();
                    }

                }
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Finished indexUpdater.");
            }
        } catch (final Throwable t) { // NOPMD
            logger.error("IndexUpdater is terminated.", t);
            forceStop();
        } finally {
            intervalControlHelper.setCrawlerRunning(true);
        }

        if (logger.isInfoEnabled()) {
            logger.info("[EXEC TIME] index update time: " + executeTime + "ms");
        }

    }

    private void printThreadDump() {
        for (final Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
            logger.info("Thread: " + entry.getKey());
            final StackTraceElement[] trace = entry.getValue();
            for (final StackTraceElement element : trace) {
                logger.info("\tat " + element);
            }
        }
    }

    private void processAccessResults(final List<Map<String, Object>> docList, final List<EsAccessResult> accessResultList,
            final List<EsAccessResult> arList) {
        for (final EsAccessResult accessResult : arList) {
            if (logger.isDebugEnabled()) {
                logger.debug("Indexing " + accessResult.getUrl());
            }
            accessResult.setStatus(Constants.DONE_STATUS);
            accessResultList.add(accessResult);

            if (accessResult.getHttpStatusCode() != 200) {
                // invalid page
                if (logger.isDebugEnabled()) {
                    logger.debug("Skipped. The response code is " + accessResult.getHttpStatusCode() + ".");
                }
                continue;
            }

            final AccessResultData accessResultData = accessResult.getAccessResultData();
            if (accessResultData != null) {
                accessResult.setAccessResultData((AccessResultData) null);
                try {
                    final Transformer transformer = SingletonLaContainer.getComponent(accessResultData.getTransformerName());
                    if (transformer == null) {
                        // no transformer
                        logger.warn("No transformer: " + accessResultData.getTransformerName());
                        continue;
                    }
                    @SuppressWarnings("unchecked")
                    final Map<String, Object> map = (Map<String, Object>) transformer.getData(accessResultData);
                    if (map.isEmpty()) {
                        // no transformer
                        logger.warn("No data: " + accessResult.getUrl());
                        continue;
                    }

                    if (Constants.FALSE.equals(map.get(Constants.INDEXING_TARGET))) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Skipped. " + "This document is not a index target. ");
                        }
                        continue;
                    } else {
                        map.remove(Constants.INDEXING_TARGET);
                    }

                    updateDocument(map);

                    docList.add(map);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Added the document. " + "The number of a document cache is " + docList.size() + ".");
                    }

                    if (docList.size() >= maxDocumentCacheSize) {
                        indexingHelper.sendDocuments(fessEsClient, docList);
                    }
                    documentSize++;
                    if (logger.isDebugEnabled()) {
                        logger.debug("The number of an added document is " + documentSize + ".");
                    }
                } catch (final Exception e) {
                    logger.warn("Could not add a doc: " + accessResult.getUrl(), e);
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Skipped. No content. ");
                }
            }

        }
    }

    protected void updateDocument(final Map<String, Object> map) {
        if (clickCountEnabled) {
            addClickCountField(map);
        }

        if (favoriteCountEnabled) {
            addFavoriteCountField(map);
        }

        // default values
        for (final Map.Entry<String, Object> entry : docValueMap.entrySet()) {
            final String key = entry.getKey();
            final Object obj = map.get(key);
            if (obj == null) {
                map.put(key, entry.getValue());
            }
        }

        float documentBoost = 0.0f;
        for (final BoostDocumentRule rule : boostRuleList) {
            if (rule.match(map)) {
                documentBoost = rule.getValue(map);
                break;
            }
        }

        if (documentBoost > 0) {
            addBoostValue(map, documentBoost);
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!map.containsKey(fessConfig.getIndexFieldDocId())) {
            map.put(fessConfig.getIndexFieldDocId(), systemHelper.generateDocId(map));
        }
    }

    protected void addBoostValue(final Map<String, Object> map, final float documentBoost) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        map.put(fessConfig.getIndexFieldBoost(), documentBoost);
        if (logger.isDebugEnabled()) {
            logger.debug("Set a document boost (" + documentBoost + ").");
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
                logger.debug("Click Count: " + count + ", url: " + url);
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
                logger.debug("Favorite Count: " + count + ", url: " + url);
            }
        }
    }

    private void cleanupAccessResults(final List<EsAccessResult> accessResultList) {
        if (!accessResultList.isEmpty()) {
            final long execTime = System.currentTimeMillis();
            final int size = accessResultList.size();
            dataService.update(accessResultList);
            accessResultList.clear();
            if (logger.isDebugEnabled()) {
                logger.debug("Updated " + size + " access results. The execution time is " + (System.currentTimeMillis() - execTime)
                        + "ms.");
            }
        }

    }

    private List<EsAccessResult> getAccessResultList(final Consumer<SearchRequestBuilder> cb) {
        final long execTime = System.currentTimeMillis();
        final List<EsAccessResult> arList = ((EsDataService) dataService).getAccessResultList(cb);
        if (!arList.isEmpty()) {
            for (final AccessResult ar : arList.toArray(new AccessResult[arList.size()])) {
                if (ar.getCreateTime().longValue() > execTime - commitMarginTime) {
                    arList.remove(ar);
                }
            }
        }
        final long totalHits = ((EsResultList<EsAccessResult>) arList).getTotalHits();
        if (logger.isInfoEnabled()) {
            logger.info("Processing " + arList.size() + "/" + totalHits + " docs (" + (System.currentTimeMillis() - execTime) + "ms)");
        }
        if (totalHits > unprocessedDocumentSize) {
            if (logger.isInfoEnabled()) {
                logger.info("Stopped all crawler threads. " + " You have " + totalHits + " (>" + unprocessedDocumentSize + ") "
                        + " unprocessed docs.");
            }
            final IntervalControlHelper intervalControlHelper = ComponentUtil.getIntervalControlHelper();
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
                logger.debug("Deleting document data: " + sessionId);
            }
            deleteBySessionId(sessionId);
            if (logger.isDebugEnabled()) {
                logger.debug("Deleted " + sessionId + " documents. The execution time is " + (System.currentTimeMillis() - execTime2)
                        + "ms.");
            }
        }
        finishedSessionIdList.clear();

        if (logger.isInfoEnabled()) {
            logger.info("Deleted completed document data. " + "The execution time is " + (System.currentTimeMillis() - execTime) + "ms.");
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

    public void setUnprocessedDocumentSize(final int unprocessedDocumentSize) {
        this.unprocessedDocumentSize = unprocessedDocumentSize;
    }

    public void addBoostDocumentRule(final BoostDocumentRule rule) {
        boostRuleList.add(rule);
    }

    public void addDefaultDocValue(final String fieldName, final Object value) {
        docValueMap.put(fieldName, value);
    }

    public void setCrawlerList(final List<Crawler> crawlerList) {
        this.crawlerList = crawlerList;
    }
}
