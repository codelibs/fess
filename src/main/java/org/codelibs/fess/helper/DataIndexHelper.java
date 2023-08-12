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
import org.codelibs.fess.es.client.SearchEngineClient;
import org.codelibs.fess.es.config.exentity.DataConfig;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

public class DataIndexHelper {

    private static final Logger logger = LogManager.getLogger(DataIndexHelper.class);

    private static final String DELETE_OLD_DOCS = "delete_old_docs";

    private static final String KEEP_EXPIRES_DOCS = "keep_expires_docs";

    protected long crawlingExecutionInterval = Constants.DEFAULT_CRAWLING_EXECUTION_INTERVAL;

    protected int crawlerPriority = Thread.NORM_PRIORITY;

    protected final List<DataCrawlingThread> dataCrawlingThreadList = Collections.synchronizedList(new ArrayList<>());

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

    public void crawl(final String sessionId, final List<String> configIdList) {
        final List<DataConfig> configList = ComponentUtil.getCrawlingConfigHelper().getDataConfigListByIds(configIdList);

        if (configList.isEmpty()) {
            // nothing
            if (logger.isInfoEnabled()) {
                logger.info("No crawling target urls.");
            }
            return;
        }

        doCrawl(sessionId, configList);
    }

    protected void doCrawl(final String sessionId, final List<DataConfig> configList) {
        final int multiprocessCrawlingCount = ComponentUtil.getFessConfig().getCrawlingThreadCount();

        final long startTime = System.currentTimeMillis();

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

        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();

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

        final long execTime = System.currentTimeMillis() - startTime;
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

    protected static class DataCrawlingThread extends Thread {

        private final DataConfig dataConfig;

        private final IndexUpdateCallback indexUpdateCallback;

        private final DataStoreParams initParamMap;

        protected boolean finished = false;

        protected boolean running = false;

        private DataStore dataStore;

        protected DataCrawlingThread(final DataConfig dataConfig, final IndexUpdateCallback indexUpdateCallback,
                final DataStoreParams initParamMap) {
            this.dataConfig = dataConfig;
            this.indexUpdateCallback = indexUpdateCallback;
            this.initParamMap = initParamMap;
        }

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
                    ComponentUtil.getComponent(FailureUrlService.class).store(dataConfig, e.getClass().getCanonicalName(),
                            dataConfig.getConfigId() + ":" + dataConfig.getName(), e);
                } finally {
                    indexUpdateCallback.commit();
                    deleteOldDocs();
                }
            }
        }

        private void deleteOldDocs() {
            if (Constants.FALSE.equals(initParamMap.getAsString(DELETE_OLD_DOCS))) {
                return;
            }
            final String sessionId = initParamMap.getAsString(Constants.SESSION_ID);
            if (StringUtil.isBlank(sessionId)) {
                logger.warn("Invalid sessionId at {}", dataConfig);
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
                logger.info("Deleted {} old docs.", numOfDeleted);
            } catch (final Exception e) {
                logger.error("Could not delete old docs at {}", dataConfig, e);
            }
        }

        public boolean isFinished() {
            return finished;
        }

        public void stopCrawling() {
            if (dataStore != null) {
                dataStore.stop();
            }
        }

        public String getCrawlingInfoId() {
            return initParamMap.getAsString(Constants.CRAWLING_INFO_ID);
        }

        public boolean isRunning() {
            return running;
        }

        public void awaitTermination() {
            try {
                join();
            } catch (final InterruptedException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Interrupted.", e);
                }
            }
        }

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

    public void setCrawlingExecutionInterval(final long crawlingExecutionInterval) {
        this.crawlingExecutionInterval = crawlingExecutionInterval;
    }

    public void setCrawlerPriority(final int crawlerPriority) {
        this.crawlerPriority = crawlerPriority;
    }
}
