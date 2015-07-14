/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.ds.DataStore;
import org.codelibs.fess.ds.DataStoreFactory;
import org.codelibs.fess.ds.IndexUpdateCallback;
import org.codelibs.fess.es.exentity.DataConfig;
import org.codelibs.fess.service.DataConfigService;
import org.codelibs.fess.util.ComponentUtil;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.seasar.framework.container.SingletonS2Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataIndexHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(DataIndexHelper.class);

    private static final String DELETE_OLD_DOCS = "delete_old_docs";

    @Resource
    protected DynamicProperties crawlerProperties;

    @Resource
    public DataConfigService dataConfigService;

    @Resource
    protected CrawlingConfigHelper crawlingConfigHelper;

    public long crawlingExecutionInterval = Constants.DEFAULT_CRAWLING_EXECUTION_INTERVAL;

    public int crawlerPriority = Thread.NORM_PRIORITY;

    private final List<DataCrawlingThread> dataCrawlingThreadList = Collections.synchronizedList(new ArrayList<DataCrawlingThread>());

    public void crawl(final String sessionId) {
        final List<DataConfig> configList = dataConfigService.getAllDataConfigList();

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
        final List<DataConfig> configList = dataConfigService.getDataConfigListByIds(configIdList);

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
        int multiprocessCrawlingCount = 5;
        final String value = crawlerProperties.getProperty(Constants.CRAWLING_THREAD_COUNT_PROPERTY, "5");
        try {
            multiprocessCrawlingCount = Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            // NOP
        }

        final long startTime = System.currentTimeMillis();

        final IndexUpdateCallback indexUpdateCallback = SingletonS2Container.getComponent(IndexUpdateCallback.class);

        final List<String> sessionIdList = new ArrayList<String>();
        final Map<String, String> initParamMap = new HashMap<String, String>();
        dataCrawlingThreadList.clear();
        final List<String> dataCrawlingThreadStatusList = new ArrayList<String>();
        for (final DataConfig dataConfig : configList) {
            final String sid = crawlingConfigHelper.store(sessionId, dataConfig);
            sessionIdList.add(sid);

            initParamMap.put(Constants.SESSION_ID, sessionId);
            initParamMap.put(Constants.CRAWLING_SESSION_ID, sid);

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
                for (final DataCrawlingThread s2Robot : dataCrawlingThreadList) {
                    s2Robot.stopCrawling();
                }
                break;
            }

            if (activeCrawlerNum < multiprocessCrawlingCount) {
                // start crawling
                dataCrawlingThreadList.get(startedCrawlerNum).start();
                dataCrawlingThreadStatusList.set(startedCrawlerNum, Constants.RUNNING);
                startedCrawlerNum++;
                activeCrawlerNum++;
                try {
                    Thread.sleep(crawlingExecutionInterval);
                } catch (final InterruptedException e) {
                    // NOP
                }
                continue;
            }

            // check status
            for (int i = 0; i < startedCrawlerNum; i++) {
                if (!dataCrawlingThreadList.get(i).isRunning() && dataCrawlingThreadStatusList.get(i).equals(Constants.RUNNING)) {
                    dataCrawlingThreadList.get(i).awaitTermination();
                    dataCrawlingThreadStatusList.set(i, Constants.DONE);
                    activeCrawlerNum--;
                }
            }
            try {
                Thread.sleep(crawlingExecutionInterval);
            } catch (final InterruptedException e) {
                // NOP
            }
        }

        boolean finishedAll = false;
        while (!finishedAll) {
            finishedAll = true;
            for (int i = 0; i < dataCrawlingThreadList.size(); i++) {
                dataCrawlingThreadList.get(i).awaitTermination(crawlingExecutionInterval);
                if (!dataCrawlingThreadList.get(i).isRunning() && dataCrawlingThreadStatusList.get(i).equals(Constants.RUNNING)) {
                    dataCrawlingThreadStatusList.set(i, Constants.DONE);
                }
                if (!dataCrawlingThreadStatusList.get(i).equals(Constants.DONE)) {
                    finishedAll = false;
                }
            }
        }
        dataCrawlingThreadList.clear();
        dataCrawlingThreadStatusList.clear();

        // put cralwing info
        final CrawlingSessionHelper crawlingSessionHelper = ComponentUtil.getCrawlingSessionHelper();

        final long execTime = System.currentTimeMillis() - startTime;
        crawlingSessionHelper.putToInfoMap(Constants.DATA_CRAWLING_EXEC_TIME, Long.toString(execTime));
        if (logger.isInfoEnabled()) {
            logger.info("[EXEC TIME] crawling time: " + execTime + "ms");
        }

        crawlingSessionHelper.putToInfoMap(Constants.DATA_INDEX_EXEC_TIME, Long.toString(indexUpdateCallback.getExecuteTime()));
        crawlingSessionHelper.putToInfoMap(Constants.DATA_INDEX_SIZE, Long.toString(indexUpdateCallback.getDocumentSize()));

        for (final String sid : sessionIdList) {
            // remove config
            crawlingConfigHelper.remove(sid);
        }

    }

    protected static class DataCrawlingThread extends Thread {

        private final DataConfig dataConfig;

        private final IndexUpdateCallback indexUpdateCallback;

        private final Map<String, String> initParamMap;

        protected boolean finished = false;

        protected boolean running = false;

        private DataStore dataStore;

        protected DataCrawlingThread(final DataConfig dataConfig, final IndexUpdateCallback indexUpdateCallback,
                final Map<String, String> initParamMap) {
            this.dataConfig = dataConfig;
            this.indexUpdateCallback = indexUpdateCallback;
            this.initParamMap = initParamMap;
        }

        @Override
        public void run() {
            running = true;
            final DataStoreFactory dataStoreFactory = ComponentUtil.getDataStoreFactory();
            dataStore = dataStoreFactory.getDataStore(dataConfig.getHandlerName());
            if (dataStore == null) {
                logger.error("DataStore(" + dataConfig.getHandlerName() + ") is not found.");
            } else {
                try {
                    dataStore.store(dataConfig, indexUpdateCallback, initParamMap);
                } catch (final Exception e) {
                    logger.error("Failed to process a data crawling: " + dataConfig.getName(), e);
                } finally {
                    indexUpdateCallback.commit();
                    deleteOldDocs();
                }
            }
            running = false;
            finished = true;
        }

        private void deleteOldDocs() {
            if (Constants.FALSE.equals(initParamMap.get(DELETE_OLD_DOCS))) {
                return;
            }
            final String sessionId = initParamMap.get(Constants.SESSION_ID);
            if (StringUtil.isBlank(sessionId)) {
                logger.warn("Invalid sessionId at " + dataConfig);
                return;
            }
            final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
            final QueryBuilder queryBuilder =
                    QueryBuilders.boolQuery().must(QueryBuilders.termQuery(fieldHelper.configIdField, dataConfig.getConfigId()))
                            .mustNot(QueryBuilders.termQuery(fieldHelper.segmentField, sessionId));
            try {
                ComponentUtil.getElasticsearchClient().deleteByQuery(fieldHelper.docIndex, fieldHelper.docType, queryBuilder);
            } catch (final Exception e) {
                logger.error("Could not delete old docs at " + dataConfig, e);
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

        public String getCrawlingSessionId() {
            return initParamMap.get(Constants.CRAWLING_SESSION_ID);
        }

        public boolean isRunning() {
            return running;
        }

        public void awaitTermination() {
            try {
                join();
            } catch (final InterruptedException e) {}
        }

        public void awaitTermination(final long mills) {
            try {
                join(mills);
            } catch (final InterruptedException e) {}
        }
    }
}
