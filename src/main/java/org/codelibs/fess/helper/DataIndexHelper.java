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
package org.codelibs.fess.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.lang.ThreadUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.FailureUrlService;
import org.codelibs.fess.ds.DataStore;
import org.codelibs.fess.ds.DataStoreFactory;
import org.codelibs.fess.ds.callback.IndexUpdateCallback;
import org.codelibs.fess.entity.DataStoreParams;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.config.exentity.DataConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

/**
 * Helper class for managing data crawling operations in Fess.
 * This class coordinates the execution of data store crawling processes,
 * managing multiple concurrent crawling threads and handling the indexing
 * of crawled documents into the search engine.
 *
 * <p>The DataIndexHelper supports:</p>
 * <ul>
 *   <li>Concurrent crawling of multiple data configurations</li>
 *   <li>Thread pool management for crawler execution</li>
 *   <li>Session-based crawling with cleanup operations</li>
 *   <li>Old document deletion after successful crawling</li>
 *   <li>Crawling execution monitoring and timing</li>
 * </ul>
 */
public class DataIndexHelper {

    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(DataIndexHelper.class);

    /** Parameter key for controlling deletion of old documents */
    private static final String DELETE_OLD_DOCS = "delete_old_docs";

    /** Parameter key for controlling retention of expired documents */
    private static final String KEEP_EXPIRES_DOCS = "keep_expires_docs";

    /**
     * Interval in milliseconds between crawler thread executions.
     * Used to control the rate at which new crawler threads are started
     * and the frequency of status checks.
     */
    protected long crawlingExecutionInterval = Constants.DEFAULT_CRAWLING_EXECUTION_INTERVAL;

    /**
     * Thread priority for crawler threads.
     * Defaults to normal thread priority.
     */
    protected int crawlerPriority = Thread.NORM_PRIORITY;

    /**
     * Thread-safe list of active data crawling threads.
     * Used to track and manage all currently running crawler threads.
     */
    protected final List<DataCrawlingThread> dataCrawlingThreadList = Collections.synchronizedList(new ArrayList<>());

    /**
     * Creates a new instance of DataIndexHelper.
     * This constructor initializes the helper for managing data crawling operations,
     * including thread pool management and session-based crawling coordination.
     */
    public DataIndexHelper() {
        // Default constructor with explicit documentation
    }

    /**
     * Initiates crawling for all configured data stores.
     * This method retrieves all available data configurations and
     * starts the crawling process for each one.
     *
     * @param sessionId unique identifier for this crawling session
     */
    public void crawl(final String sessionId) {
        final List<DataConfig> configList = ComponentUtil.getCrawlingConfigHelper().getAllDataConfigList();

        if (configList.isEmpty()) {
            // nothing
            if (logger.isInfoEnabled()) {
                logger.info("No crawling target data.");
            }
            return;
        }

        doCrawl(sessionId, configList);
    }

    /**
     * Initiates crawling for specific data configurations.
     * This method starts crawling only for the data configurations
     * specified in the configIdList parameter.
     *
     * @param sessionId unique identifier for this crawling session
     * @param configIdList list of data configuration IDs to crawl
     */
    public void crawl(final String sessionId, final List<String> configIdList) {
        final List<DataConfig> configList = ComponentUtil.getCrawlingConfigHelper().getDataConfigListByIds(configIdList);

        if (configList.isEmpty()) {
            // nothing
            if (logger.isInfoEnabled()) {
                logger.info("No crawling target data configs.");
            }
            return;
        }

        doCrawl(sessionId, configList);
    }

    /**
     * Performs the actual crawling operation for the provided data configurations.
     * This method manages the creation and execution of crawler threads,
     * monitors their progress, and handles cleanup operations.
     *
     * <p>The method:</p>
     * <ul>
     *   <li>Creates crawler threads for each data configuration</li>
     *   <li>Manages concurrent execution based on thread count limits</li>
     *   <li>Monitors thread completion and handles cleanup</li>
     *   <li>Records execution timing and statistics</li>
     * </ul>
     *
     * @param sessionId unique identifier for this crawling session
     * @param configList list of data configurations to crawl
     */
    protected void doCrawl(final String sessionId, final List<DataConfig> configList) {
        final int multiprocessCrawlingCount = ComponentUtil.getFessConfig().getCrawlingThreadCount();

        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final long startTime = systemHelper.getCurrentTimeAsLong();

        final IndexUpdateCallback indexUpdateCallback = ComponentUtil.getComponent(IndexUpdateCallback.class);

        final List<String> sessionIdList = new ArrayList<>();
        dataCrawlingThreadList.clear();
        final List<String> dataCrawlingThreadStatusList = new ArrayList<>();
        for (final DataConfig dataConfig : configList) {
            final DataStoreParams initParamMap = new DataStoreParams();
            final String sid = ComponentUtil.getCrawlingConfigHelper().store(sessionId, dataConfig);
            sessionIdList.add(sid);

            initParamMap.put(Constants.SESSION_ID, sessionId);
            initParamMap.put(Constants.CRAWLING_INFO_ID, sid);

            final DataCrawlingThread dataCrawlingThread = new DataCrawlingThread(dataConfig, indexUpdateCallback, initParamMap);
            dataCrawlingThread.setPriority(crawlerPriority);
            dataCrawlingThread.setName(sid);
            dataCrawlingThread.setDaemon(true);

            dataCrawlingThreadList.add(dataCrawlingThread);
            dataCrawlingThreadStatusList.add(Constants.READY);

        }

        int startedCrawlerNum = 0;
        int activeCrawlerNum = 0;
        while (startedCrawlerNum < dataCrawlingThreadList.size()) {
            // Force to stop crawl
            if (systemHelper.isForceStop()) {
                for (final DataCrawlingThread crawlerThread : dataCrawlingThreadList) {
                    crawlerThread.stopCrawling();
                }
                break;
            }

            if (activeCrawlerNum < multiprocessCrawlingCount) {
                // start crawling
                dataCrawlingThreadList.get(startedCrawlerNum).start();
                dataCrawlingThreadStatusList.set(startedCrawlerNum, Constants.RUNNING);
                startedCrawlerNum++;
                activeCrawlerNum++;
                ThreadUtil.sleep(crawlingExecutionInterval);
                continue;
            }

            // check status
            for (int i = 0; i < startedCrawlerNum; i++) {
                if (!dataCrawlingThreadList.get(i).isRunning() && Constants.RUNNING.equals(dataCrawlingThreadStatusList.get(i))) {
                    dataCrawlingThreadList.get(i).awaitTermination();
                    dataCrawlingThreadStatusList.set(i, Constants.DONE);
                    activeCrawlerNum--;
                }
            }
            ThreadUtil.sleep(crawlingExecutionInterval);
        }

        boolean finishedAll = false;
        while (!finishedAll) {
            finishedAll = true;
            for (int i = 0; i < dataCrawlingThreadList.size(); i++) {
                dataCrawlingThreadList.get(i).awaitTermination(crawlingExecutionInterval);
                if (!dataCrawlingThreadList.get(i).isRunning() && Constants.RUNNING.equals(dataCrawlingThreadStatusList.get(i))) {
                    dataCrawlingThreadStatusList.set(i, Constants.DONE);
                }
                if (!Constants.DONE.equals(dataCrawlingThreadStatusList.get(i))) {
                    finishedAll = false;
                }
            }
        }
        dataCrawlingThreadList.clear();
        dataCrawlingThreadStatusList.clear();

        // put cralwing info
        final CrawlingInfoHelper crawlingInfoHelper = ComponentUtil.getCrawlingInfoHelper();

        final long execTime = systemHelper.getCurrentTimeAsLong() - startTime;
        crawlingInfoHelper.putToInfoMap(Constants.DATA_CRAWLING_EXEC_TIME, Long.toString(execTime));
        if (logger.isInfoEnabled()) {
            logger.info("[EXEC TIME] crawling time: {}ms", execTime);
        }

        crawlingInfoHelper.putToInfoMap(Constants.DATA_INDEX_EXEC_TIME, Long.toString(indexUpdateCallback.getExecuteTime()));
        crawlingInfoHelper.putToInfoMap(Constants.DATA_INDEX_SIZE, Long.toString(indexUpdateCallback.getDocumentSize()));

        for (final String sid : sessionIdList) {
            // remove config
            ComponentUtil.getCrawlingConfigHelper().remove(sid);
        }

    }

    /**
     * Inner thread class for executing data store crawling operations.
     * Each thread handles crawling for a single data configuration,
     * processing documents and updating the search index.
     *
     * <p>The thread manages:</p>
     * <ul>
     *   <li>Data store initialization and document processing</li>
     *   <li>Index update operations through callbacks</li>
     *   <li>Error handling and failure logging</li>
     *   <li>Cleanup of old documents after successful crawling</li>
     * </ul>
     */
    protected static class DataCrawlingThread extends Thread {

        /** Configuration for the data store being crawled */
        private final DataConfig dataConfig;

        /** Callback for handling document indexing operations */
        private final IndexUpdateCallback indexUpdateCallback;

        /** Initialization parameters for the data store */
        private final DataStoreParams initParamMap;

        /** Flag indicating whether the crawling thread has finished execution */
        protected boolean finished = false;

        /** Flag indicating whether the crawling thread is currently running */
        protected boolean running = false;

        /** The data store instance used for crawling operations */
        private DataStore dataStore;

        /**
         * Constructs a new data crawling thread.
         *
         * @param dataConfig configuration for the data store to crawl
         * @param indexUpdateCallback callback for handling document indexing
         * @param initParamMap initialization parameters for the data store
         */
        protected DataCrawlingThread(final DataConfig dataConfig, final IndexUpdateCallback indexUpdateCallback,
                final DataStoreParams initParamMap) {
            this.dataConfig = dataConfig;
            this.indexUpdateCallback = indexUpdateCallback;
            this.initParamMap = initParamMap;
        }

        /**
         * Executes the crawling thread.
         * Sets the running flag, processes the data store, and ensures
         * proper cleanup regardless of success or failure.
         */
        @Override
        public void run() {
            running = true;
            try {
                process();
            } finally {
                running = false;
                finished = true;
            }
        }

        /**
         * Processes the data store crawling operation.
         * This method initializes the data store, performs the crawling,
         * handles any errors, and ensures cleanup operations are executed.
         * After successful crawling, it commits the index updates and
         * deletes old documents if configured to do so.
         */
        protected void process() {
            final DataStoreFactory dataStoreFactory = ComponentUtil.getDataStoreFactory();
            dataStore = dataStoreFactory.getDataStore(dataConfig.getHandlerName());
            if (dataStore == null) {
                logger.error("DataStore({}) is not found.", dataConfig.getHandlerName());
            } else {
                try {
                    dataStore.store(dataConfig, indexUpdateCallback, initParamMap);
                } catch (final Throwable e) {
                    logger.error("Failed to process a data crawling: {}", dataConfig.getName(), e);
                    ComponentUtil.getComponent(FailureUrlService.class)
                            .store(dataConfig, e.getClass().getCanonicalName(), dataConfig.getConfigId() + ":" + dataConfig.getName(), e);
                } finally {
                    indexUpdateCallback.commit();
                    deleteOldDocs();
                }
            }
        }

        /**
         * Deletes old documents from the search index.
         * This method removes documents that were indexed in previous
         * crawling sessions for the same data configuration, keeping
         * only the documents from the current session.
         *
         * <p>The deletion process:</p>
         * <ul>
         *   <li>Checks if old document deletion is enabled</li>
         *   <li>Builds a query to find old documents for this configuration</li>
         *   <li>Optionally preserves expired documents based on configuration</li>
         *   <li>Executes the deletion query against the search engine</li>
         * </ul>
         */
        private void deleteOldDocs() {
            if (Constants.FALSE.equals(initParamMap.getAsString(DELETE_OLD_DOCS))) {
                return;
            }
            final String sessionId = initParamMap.getAsString(Constants.SESSION_ID);
            if (StringUtil.isBlank(sessionId)) {
                logger.warn("[{}] Cannot delete stale documents: sessionId is not set.", dataConfig.getName());
                return;
            }
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()//
                    .must(QueryBuilders.termQuery(fessConfig.getIndexFieldConfigId(), dataConfig.getConfigId()))//
                    .mustNot(QueryBuilders.termQuery(fessConfig.getIndexFieldSegment(), sessionId));
            if (!Constants.FALSE.equals(initParamMap.getAsString(KEEP_EXPIRES_DOCS))) {
                final QueryBuilder expiresCheckQuery = QueryBuilders.boolQuery()//
                        .mustNot(QueryBuilders.rangeQuery(fessConfig.getIndexFieldExpires()).gt("now"))//
                        .mustNot(QueryBuilders.existsQuery(fessConfig.getIndexFieldExpires()));
                queryBuilder.must(expiresCheckQuery);
            }

            try {
                final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
                final String index = fessConfig.getIndexDocumentUpdateIndex();
                searchEngineClient.admin().indices().prepareRefresh(index).execute().actionGet();
                final long numOfDeleted = searchEngineClient.deleteByQuery(index, queryBuilder);
                logger.info("[{}] Deleted {} stale documents.", dataConfig.getName(), numOfDeleted);
            } catch (final Exception e) {
                logger.error("[{}] Failed to delete stale documents.", dataConfig.getName(), e);
            }
        }

        /**
         * Checks if the crawling thread has finished execution.
         *
         * @return true if the thread has completed its crawling operation
         */
        public boolean isFinished() {
            return finished;
        }

        /**
         * Stops the crawling operation gracefully.
         * If a data store is currently active, this method calls
         * its stop method to halt the crawling process.
         */
        public void stopCrawling() {
            if (dataStore != null) {
                dataStore.stop();
            }
        }

        /**
         * Gets the crawling information ID for this thread.
         *
         * @return the crawling info ID from the initialization parameters
         */
        public String getCrawlingInfoId() {
            return initParamMap.getAsString(Constants.CRAWLING_INFO_ID);
        }

        /**
         * Checks if the crawling thread is currently running.
         *
         * @return true if the thread is actively executing
         */
        public boolean isRunning() {
            return running;
        }

        /**
         * Waits for the crawling thread to terminate.
         * This method blocks until the thread completes its execution.
         * Interrupted exceptions are caught and logged at debug level.
         */
        public void awaitTermination() {
            try {
                join();
            } catch (final InterruptedException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Interrupted.", e);
                }
            }
        }

        /**
         * Waits for the crawling thread to terminate within a specified time limit.
         * This method blocks until the thread completes or the timeout expires.
         *
         * @param mills maximum time to wait in milliseconds
         */
        public void awaitTermination(final long mills) {
            try {
                join(mills);
            } catch (final InterruptedException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Interrupted.", e);
                }
            }
        }
    }

    /**
     * Sets the crawling execution interval.
     * This interval controls the delay between starting new crawler threads
     * and the frequency of status checks during crawling operations.
     *
     * @param crawlingExecutionInterval interval in milliseconds
     */
    public void setCrawlingExecutionInterval(final long crawlingExecutionInterval) {
        this.crawlingExecutionInterval = crawlingExecutionInterval;
    }

    /**
     * Sets the thread priority for crawler threads.
     * This priority will be applied to all newly created crawler threads.
     *
     * @param crawlerPriority thread priority (typically Thread.MIN_PRIORITY to Thread.MAX_PRIORITY)
     */
    public void setCrawlerPriority(final int crawlerPriority) {
        this.crawlerPriority = crawlerPriority;
    }
}
