/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.lang.ThreadUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.Crawler;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.AccessResultData;
import org.codelibs.fess.crawler.entity.OpenSearchAccessResult;
import org.codelibs.fess.crawler.entity.OpenSearchUrlQueue;
import org.codelibs.fess.crawler.service.DataService;
import org.codelibs.fess.crawler.service.UrlFilterService;
import org.codelibs.fess.crawler.service.UrlQueueService;
import org.codelibs.fess.crawler.service.impl.OpenSearchDataService;
import org.codelibs.fess.crawler.transformer.Transformer;
import org.codelibs.fess.crawler.util.OpenSearchResultList;
import org.codelibs.fess.exception.ContainerNotAvailableException;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.helper.IndexingHelper;
import org.codelibs.fess.helper.IntervalControlHelper;
import org.codelibs.fess.helper.SearchLogHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.ingest.IngestFactory;
import org.codelibs.fess.ingest.Ingester;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.log.exbhv.ClickLogBhv;
import org.codelibs.fess.opensearch.log.exbhv.FavoriteLogBhv;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocList;
import org.codelibs.fess.util.MemoryUtil;
import org.codelibs.fess.util.ThreadDumpUtil;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.sort.SortOrder;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;

/**
 * IndexUpdater is responsible for updating the search index with crawled document data.
 * This class extends Thread and continuously processes access results from the crawler,
 * transforms them into indexed documents, and updates the OpenSearch index.
 *
 * <p>The updater performs the following key operations:
 * <ul>
 * <li>Retrieves crawled documents from the data service</li>
 * <li>Transforms document data using appropriate transformers</li>
 * <li>Applies document boosting rules and click/favorite count enhancements</li>
 * <li>Sends processed documents to the search engine for indexing</li>
 * <li>Manages cleanup of processed crawler session data</li>
 * </ul>
 *
 * <p>The updater runs continuously until crawling is finished and all documents are processed.
 * It includes error handling, retry logic, and performance monitoring capabilities.
 *
 */
public class IndexUpdater extends Thread {
    /** Logger for this class */
    private static final Logger logger = LogManager.getLogger(IndexUpdater.class);

    /** List of crawler session IDs to process */
    protected List<String> sessionIdList;

    /** OpenSearch client for index operations */
    @Resource
    protected SearchEngineClient searchEngineClient;

    /** Service for managing crawled document data */
    @Resource
    protected DataService<OpenSearchAccessResult> dataService;

    /** Service for managing URL crawling queue */
    @Resource
    protected UrlQueueService<OpenSearchUrlQueue> urlQueueService;

    /** Service for URL filtering operations */
    @Resource
    protected UrlFilterService urlFilterService;

    /** Behavior class for click log operations */
    @Resource
    protected ClickLogBhv clickLogBhv;

    /** Behavior class for favorite log operations */
    @Resource
    protected FavoriteLogBhv favoriteLogBhv;

    /** Helper for system-level operations */
    @Resource
    protected SystemHelper systemHelper;

    /** Helper for document indexing operations */
    @Resource
    protected IndexingHelper indexingHelper;

    /** Flag indicating if crawling should be finished */
    protected boolean finishCrawling = false;

    /** Total execution time in milliseconds */
    protected long executeTime;

    /** Total number of processed documents */
    protected long documentSize;

    /** Maximum number of indexer errors allowed */
    protected int maxIndexerErrorCount = 0;

    /** Maximum number of general errors allowed before termination */
    protected int maxErrorCount = 2;

    /** List of finished crawler session IDs for cleanup */
    protected List<String> finishedSessionIdList = new ArrayList<>();

    /** List of document boost matchers for scoring enhancement */
    private final List<DocBoostMatcher> docBoostMatcherList = new ArrayList<>();

    /** List of active crawler instances */
    private List<Crawler> crawlerList;

    /** Factory for creating document ingesters */
    private IngestFactory ingestFactory = null;

    /**
     * Default constructor for IndexUpdater.
     * Initializes a new instance with default settings.
     */
    public IndexUpdater() {
        super();
    }

    /**
     * Initializes the IndexUpdater after dependency injection.
     * Sets up the ingest factory if available in the component container.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        if (ComponentUtil.hasIngestFactory()) {
            ingestFactory = ComponentUtil.getIngestFactory();
        }
    }

    /**
     * Destroys the IndexUpdater when the container is shutting down.
     * Stops all crawler instances if crawling is still in progress.
     */
    @PreDestroy
    public void destroy() {
        if (!finishCrawling) {
            if (logger.isInfoEnabled()) {
                logger.info("Stopping all crawler.");
            }
            forceStop();
        }
    }

    /**
     * Adds a finished session ID to the cleanup list.
     * This method is thread-safe and adds the session ID to be cleaned up later.
     *
     * @param sessionId the crawler session ID that has finished processing
     */
    public void addFinishedSessionId(final String sessionId) {
        synchronized (finishedSessionIdList) {
            finishedSessionIdList.add(sessionId);
        }
    }

    /**
     * Deletes all data associated with a specific crawler session.
     * Removes URL filters, URL queues, and access result data for the session.
     *
     * @param sessionId the session ID whose data should be deleted
     */
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

    /**
     * Main execution method that runs the index updating process.
     * Continuously processes crawled documents from the data service, transforms them,
     * and updates the search index until crawling is finished and all documents are processed.
     *
     * <p>The method performs the following operations in a loop:
     * <ul>
     * <li>Retrieves access results from the data service</li>
     * <li>Processes each document through transformers</li>
     * <li>Applies document boosting and metadata enhancements</li>
     * <li>Sends processed documents to the search engine</li>
     * <li>Cleans up processed data and manages crawler sessions</li>
     * </ul>
     *
     * <p>The method includes error handling, retry logic, and will terminate
     * if too many empty results are encountered or if a system shutdown is requested.
     */
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
                        QueryBuilders.boolQuery().filter(QueryBuilders.termsQuery(OpenSearchAccessResult.SESSION_ID, sessionIdList)).filter(
                                QueryBuilders.termQuery(OpenSearchAccessResult.STATUS, org.codelibs.fess.crawler.Constants.OK_STATUS));
                builder.setQuery(queryBuilder);
                builder.setFrom(0);
                final int maxDocumentCacheSize = fessConfig.getIndexerWebfsMaxDocumentCacheSizeAsInteger();
                builder.setSize(maxDocumentCacheSize <= 0 ? 1 : maxDocumentCacheSize);
                builder.addSort(OpenSearchAccessResult.CREATE_TIME, SortOrder.ASC);
            };

            final DocList docList = new DocList();
            final List<OpenSearchAccessResult> accessResultList = new ArrayList<>();

            long updateTime = systemHelper.getCurrentTimeAsLong();
            int errorCount = 0;
            int emptyListCount = 0;
            long cleanupTime = -1;
            while (!finishCrawling || !accessResultList.isEmpty()) {
                try {
                    final int sessionIdListSize = finishedSessionIdList.size();
                    intervalControlHelper.setCrawlerRunning(true);

                    docList.clear();
                    accessResultList.clear();

                    updateTime = systemHelper.getCurrentTimeAsLong() - updateTime;

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

                    updateTime = systemHelper.getCurrentTimeAsLong();

                    List<OpenSearchAccessResult> arList = getAccessResultList(cb, cleanupTime);
                    if (arList.isEmpty()) {
                        emptyListCount++;
                    } else {
                        emptyListCount = 0; // reset
                    }
                    long hitCount = ((OpenSearchResultList<OpenSearchAccessResult>) arList).getTotalHits();
                    while (hitCount > 0) {
                        if (arList.isEmpty()) {
                            ThreadUtil.sleep(fessConfig.getIndexerWebfsCommitMarginTimeAsInteger().longValue());
                            cleanupTime = -1;
                        } else {
                            processAccessResults(docList, accessResultList, arList);
                            cleanupTime = cleanupAccessResults(accessResultList);
                        }
                        arList = getAccessResultList(cb, cleanupTime);
                        hitCount = ((OpenSearchResultList<OpenSearchAccessResult>) arList).getTotalHits();
                    }
                    if (!docList.isEmpty()) {
                        indexingHelper.sendDocuments(searchEngineClient, docList);
                    }

                    synchronized (finishedSessionIdList) {
                        if (sessionIdListSize != 0 && sessionIdListSize == finishedSessionIdList.size()) {
                            cleanupFinishedSessionData();
                        }
                    }
                    executeTime += systemHelper.getCurrentTimeAsLong() - updateTime;

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

    /**
     * Processes a list of access results and converts them into indexable documents.
     * Each access result is transformed into a document map and added to the document list.
     *
     * @param docList the document list to add processed documents to
     * @param accessResultList the list to track processed access results for cleanup
     * @param arList the list of access results to process
     */
    private void processAccessResults(final DocList docList, final List<OpenSearchAccessResult> accessResultList,
            final List<OpenSearchAccessResult> arList) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final long maxDocumentRequestSize = Long.parseLong(fessConfig.getIndexerWebfsMaxDocumentRequestSize());
        for (final OpenSearchAccessResult accessResult : arList) {
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

            final long startTime = systemHelper.getCurrentTimeAsLong();
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
                    final long processingTime = systemHelper.getCurrentTimeAsLong() - startTime;
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

    /**
     * Retrieves the access result data from an OpenSearch access result.
     * Handles exceptions that may occur during data retrieval.
     *
     * @param accessResult the access result to extract data from
     * @return the access result data, or null if retrieval fails
     */
    private AccessResultData<?> getAccessResultData(final OpenSearchAccessResult accessResult) {
        try {
            return accessResult.getAccessResultData();
        } catch (final Exception e) {
            logger.warn("Failed to get data from {}", accessResult.getUrl(), e);
        }
        return null;
    }

    /**
     * Processes a document through the ingest pipeline if an ingest factory is available.
     * Applies all configured ingesters to transform and enrich the document data.
     *
     * @param accessResult the access result containing document metadata
     * @param map the document data map to process
     * @return the processed document map after applying all ingesters
     */
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

    /**
     * Updates a document with additional metadata and enhancements.
     * Adds click counts, favorite counts, document boosting, and generates document ID.
     * Also applies language-specific updates through the language helper.
     *
     * @param map the document data map to update with additional metadata
     */
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

    /**
     * Adds a boost value to the document for search relevance scoring.
     * The boost value affects how highly the document will rank in search results.
     *
     * @param map the document data map to add the boost value to
     * @param documentBoost the boost value to apply to the document
     */
    protected void addBoostValue(final Map<String, Object> map, final float documentBoost) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        map.put(fessConfig.getIndexFieldBoost(), documentBoost);
        if (logger.isDebugEnabled()) {
            logger.debug("Set a document boost ({}).", documentBoost);
        }
    }

    /**
     * Adds a click count field to the document based on search log data.
     * The click count represents how many times users have clicked on this document in search results.
     *
     * @param doc the document data map to add the click count to
     */
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

    /**
     * Adds a favorite count field to the document based on user favorite data.
     * The favorite count represents how many users have marked this document as a favorite.
     *
     * @param map the document data map to add the favorite count to
     */
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

    /**
     * Cleans up processed access results by updating their status in the data service.
     * This marks the access results as processed and clears the list.
     *
     * @param accessResultList the list of access results to clean up
     * @return the time taken for the cleanup operation in milliseconds, or -1 if no cleanup was needed
     */
    private long cleanupAccessResults(final List<OpenSearchAccessResult> accessResultList) {
        if (!accessResultList.isEmpty()) {
            final long execTime = systemHelper.getCurrentTimeAsLong();
            final int size = accessResultList.size();
            dataService.update(accessResultList);
            accessResultList.clear();
            final long time = systemHelper.getCurrentTimeAsLong() - execTime;
            if (logger.isDebugEnabled()) {
                logger.debug("Updated {} access results. The execution time is {}ms.", size, time);
            }
            return time;
        }
        return -1;
    }

    /**
     * Retrieves a list of access results from the data service for processing.
     * Filters out results that are too recent based on commit margin time and manages crawler throttling.
     *
     * @param cb the consumer to customize the search request
     * @param cleanupTime the time taken for the last cleanup operation
     * @return the list of access results ready for processing
     */
    private List<OpenSearchAccessResult> getAccessResultList(final Consumer<SearchRequestBuilder> cb, final long cleanupTime) {
        if (logger.isDebugEnabled()) {
            logger.debug("Getting documents in IndexUpdater queue.");
        }
        final long execTime = systemHelper.getCurrentTimeAsLong();
        final List<OpenSearchAccessResult> arList = ((OpenSearchDataService) dataService).getAccessResultList(cb);
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!arList.isEmpty()) {
            final long commitMarginTime = fessConfig.getIndexerWebfsCommitMarginTimeAsInteger().longValue();
            for (final AccessResult<?> ar : arList.toArray(new AccessResult[arList.size()])) {
                if (ar.getCreateTime().longValue() > execTime - commitMarginTime) {
                    arList.remove(ar);
                }
            }
        }
        final long totalHits = ((OpenSearchResultList<OpenSearchAccessResult>) arList).getTotalHits();
        if (logger.isInfoEnabled()) {
            final StringBuilder buf = new StringBuilder(100);
            buf.append("Processing ");
            if (totalHits > 0) {
                buf.append(arList.size()).append('/').append(totalHits).append(" docs (Doc:{access ");
            } else {
                buf.append("no docs in indexing queue (Doc:{access ");
            }
            buf.append(systemHelper.getCurrentTimeAsLong() - execTime).append("ms");
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

    /**
     * Cleans up data for all finished crawler sessions.
     * Deletes URL filters, URL queues, and access result data for each finished session.
     */
    private void cleanupFinishedSessionData() {
        final long execTime = systemHelper.getCurrentTimeAsLong();
        // cleanup
        for (final String sessionId : finishedSessionIdList) {
            final long execTime2 = systemHelper.getCurrentTimeAsLong();
            if (logger.isDebugEnabled()) {
                logger.debug("Deleting document data: {}", sessionId);
            }
            deleteBySessionId(sessionId);
            if (logger.isDebugEnabled()) {
                logger.debug("Deleted {} documents. The execution time is {}ms.", sessionId,
                        systemHelper.getCurrentTimeAsLong() - execTime2);
            }
        }
        finishedSessionIdList.clear();

        if (logger.isInfoEnabled()) {
            logger.info("Deleted completed document data. The execution time is {}ms.", systemHelper.getCurrentTimeAsLong() - execTime);
        }
    }

    /**
     * Forces all crawlers to stop immediately.
     * Sets the force stop flag and stops all active crawler instances.
     */
    private void forceStop() {
        systemHelper.setForceStop(true);
        for (final Crawler crawler : crawlerList) {
            crawler.stop();
        }
    }

    /**
     * Gets the total execution time for index updates.
     *
     * @return the total execution time in milliseconds
     */
    public long getExecuteTime() {
        return executeTime;
    }

    /**
     * Gets the list of crawler session IDs being processed.
     *
     * @return the list of session IDs
     */
    public List<String> getSessionIdList() {
        return sessionIdList;
    }

    /**
     * Sets the list of crawler session IDs to process.
     *
     * @param sessionIdList the list of session IDs to set
     */
    public void setSessionIdList(final List<String> sessionIdList) {
        this.sessionIdList = sessionIdList;
    }

    /**
     * Sets the flag indicating whether crawling should be finished.
     *
     * @param finishCrawling true if crawling should be finished, false otherwise
     */
    public void setFinishCrawling(final boolean finishCrawling) {
        this.finishCrawling = finishCrawling;
    }

    /**
     * Gets the total number of documents processed.
     *
     * @return the total document count
     */
    public long getDocumentSize() {
        return documentSize;
    }

    /**
     * Sets the uncaught exception handler for this IndexUpdater thread.
     *
     * @param eh the uncaught exception handler to set
     */
    @Override
    public void setUncaughtExceptionHandler(final UncaughtExceptionHandler eh) {
        super.setUncaughtExceptionHandler(eh);
    }

    /**
     * Sets the default uncaught exception handler for all threads.
     *
     * @param eh the default uncaught exception handler to set
     */
    public static void setDefaultUncaughtExceptionHandler(final UncaughtExceptionHandler eh) {
        Thread.setDefaultUncaughtExceptionHandler(eh);
    }

    /**
     * Sets the maximum number of indexer errors allowed.
     *
     * @param maxIndexerErrorCount the maximum error count to set
     */
    public void setMaxIndexerErrorCount(final int maxIndexerErrorCount) {
        this.maxIndexerErrorCount = maxIndexerErrorCount;
    }

    /**
     * Adds a document boost matcher rule for enhancing document relevance scores.
     *
     * @param rule the document boost matcher rule to add
     */
    public void addDocBoostMatcher(final DocBoostMatcher rule) {
        docBoostMatcherList.add(rule);
    }

    /**
     * Sets the list of crawler instances that this updater will manage.
     *
     * @param crawlerList the list of crawlers to set
     */
    public void setCrawlerList(final List<Crawler> crawlerList) {
        this.crawlerList = crawlerList;
    }
}
