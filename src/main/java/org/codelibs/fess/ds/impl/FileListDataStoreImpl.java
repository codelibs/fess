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

package org.codelibs.fess.ds.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jp.sf.orangesignal.csv.CsvConfig;

import org.codelibs.fess.Constants;
import org.codelibs.fess.db.exentity.DataCrawlingConfig;
import org.codelibs.fess.ds.DataStoreCrawlingException;
import org.codelibs.fess.ds.DataStoreException;
import org.codelibs.fess.ds.IndexUpdateCallback;
import org.codelibs.fess.helper.CrawlingSessionHelper;
import org.codelibs.fess.helper.FieldHelper;
import org.codelibs.fess.helper.IndexingHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.robot.RobotSystemException;
import org.codelibs.robot.builder.RequestDataBuilder;
import org.codelibs.robot.client.S2RobotClient;
import org.codelibs.robot.client.S2RobotClientFactory;
import org.codelibs.robot.entity.ResponseData;
import org.codelibs.robot.entity.ResultData;
import org.codelibs.robot.processor.ResponseProcessor;
import org.codelibs.robot.processor.impl.DefaultResponseProcessor;
import org.codelibs.robot.rule.Rule;
import org.codelibs.robot.rule.RuleManager;
import org.codelibs.robot.transformer.Transformer;
import org.codelibs.robot.util.LruHashMap;
import org.codelibs.solr.lib.SolrGroup;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.util.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileListDataStoreImpl extends CsvDataStoreImpl {

    private static final Logger logger = LoggerFactory.getLogger(FileListDataStoreImpl.class);

    public boolean deleteProcessedFile = true;

    public long csvFileTimestampMargin = 60 * 1000;// 1min

    public boolean ignoreDataStoreException = true;

    public String createEventName = "create";

    public String modifyEventName = "modify";

    public String deleteEventName = "delete";

    public String eventTypeField = "event_type";

    public int maxDeleteDocumentCacheSize = 100;

    protected S2RobotClientFactory robotClientFactory;

    protected CrawlingSessionHelper crawlingSessionHelper;

    public Map<String, String> parentEncodingMap = Collections.synchronizedMap(new LruHashMap<String, String>(1000));

    public String[] ignoreFieldNames = new String[] { Constants.INDEXING_TARGET, Constants.SESSION_ID };

    @Override
    protected boolean isCsvFile(final File parentFile, final String filename) {
        if (super.isCsvFile(parentFile, filename)) {
            final File file = new File(parentFile, filename);
            final long now = System.currentTimeMillis();
            return now - file.lastModified() > csvFileTimestampMargin;
        }
        return false;
    }

    @Override
    public void store(final DataCrawlingConfig config, final IndexUpdateCallback callback, final Map<String, String> initParamMap) {

        robotClientFactory = SingletonS2Container.getComponent(S2RobotClientFactory.class);

        config.initializeClientFactory(robotClientFactory);

        super.store(config, callback, initParamMap);
    }

    @Override
    protected void storeData(final DataCrawlingConfig dataConfig, final IndexUpdateCallback callback, final Map<String, String> paramMap,
            final Map<String, String> scriptMap, final Map<String, Object> defaultDataMap) {

        super.storeData(dataConfig, new FileListIndexUpdateCallback(callback), paramMap, scriptMap, defaultDataMap);
    }

    @Override
    protected void processCsv(final DataCrawlingConfig dataConfig, final IndexUpdateCallback callback, final Map<String, String> paramMap,
            final Map<String, String> scriptMap, final Map<String, Object> defaultDataMap, final CsvConfig csvConfig, final File csvFile,
            final long readInterval, final String csvFileEncoding, final boolean hasHeaderLine) {
        try {
            super.processCsv(dataConfig, callback, paramMap, scriptMap, defaultDataMap, csvConfig, csvFile, readInterval, csvFileEncoding,
                    hasHeaderLine);

            // delete csv file
            if (deleteProcessedFile && !csvFile.delete()) {
                logger.warn("Failed to delete {}", csvFile.getAbsolutePath());
            }
        } catch (final DataStoreException e) {
            if (ignoreDataStoreException) {
                logger.error("Failed to process " + csvFile.getAbsolutePath(), e);
                // rename csv file, or delete it if failed
                if (!csvFile.renameTo(new File(csvFile.getParent(), csvFile.getName() + ".txt")) && !csvFile.delete()) {
                    logger.warn("Failed to delete {}", csvFile.getAbsolutePath());
                }
            } else {
                throw e;
            }
        }
    }

    protected class FileListIndexUpdateCallback implements IndexUpdateCallback {
        protected IndexUpdateCallback indexUpdateCallback;

        protected List<String> deleteIdList = new ArrayList<String>();

        protected FileListIndexUpdateCallback(final IndexUpdateCallback indexUpdateCallback) {
            this.indexUpdateCallback = indexUpdateCallback;

        }

        @Override
        public boolean store(final Map<String, Object> dataMap) {
            final Object eventType = dataMap.remove(eventTypeField);

            if (createEventName.equals(eventType) || modifyEventName.equals(eventType)) {
                // updated file
                return addDocument(dataMap);
            } else if (deleteEventName.equals(eventType)) {
                // deleted file
                return deleteDocument(dataMap);
            }

            logger.warn("unknown event: " + eventType + ", data: " + dataMap);
            return false;
        }

        protected boolean addDocument(final Map<String, Object> dataMap) {
            final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
            synchronized (indexUpdateCallback) {
                //   required check
                if (!dataMap.containsKey(fieldHelper.urlField) || dataMap.get(fieldHelper.urlField) == null) {
                    logger.warn("Could not add a doc. Invalid data: " + dataMap);
                    return false;
                }

                final String url = dataMap.get(fieldHelper.urlField).toString();
                try {
                    final S2RobotClient client = robotClientFactory.getClient(url);
                    if (client == null) {
                        logger.warn("S2RobotClient is null. Data: " + dataMap);
                        return false;
                    }

                    final long startTime = System.currentTimeMillis();
                    final ResponseData responseData = client.execute(RequestDataBuilder.newRequestData().get().url(url).build());
                    responseData.setExecutionTime(System.currentTimeMillis() - startTime);
                    responseData.setSessionId((String) dataMap.get(Constants.SESSION_ID));

                    final RuleManager ruleManager = SingletonS2Container.getComponent(RuleManager.class);
                    final Rule rule = ruleManager.getRule(responseData);
                    if (rule == null) {
                        logger.warn("No url rule. Data: " + dataMap);
                        return false;
                    } else {
                        responseData.setRuleId(rule.getRuleId());
                        final ResponseProcessor responseProcessor = rule.getResponseProcessor();
                        if (responseProcessor instanceof DefaultResponseProcessor) {
                            final Transformer transformer = ((DefaultResponseProcessor) responseProcessor).getTransformer();
                            final ResultData resultData = transformer.transform(responseData);
                            final byte[] data = resultData.getData();
                            if (data != null) {
                                try {
                                    @SuppressWarnings("unchecked")
                                    final Map<String, Object> responseDataMap =
                                            (Map<String, Object>) SerializeUtil.fromBinaryToObject(data);
                                    dataMap.putAll(responseDataMap);
                                } catch (final Exception e) {
                                    throw new RobotSystemException("Could not create an instance from bytes.", e);
                                }
                            }

                            // remove
                            for (final String fieldName : ignoreFieldNames) {
                                dataMap.remove(fieldName);
                            }

                            return indexUpdateCallback.store(dataMap);
                        } else {
                            logger.warn("The response processor is not DefaultResponseProcessor. responseProcessor: " + responseProcessor
                                    + ", Data: " + dataMap);
                            return false;
                        }
                    }
                } catch (final Exception e) {
                    throw new DataStoreCrawlingException(url, "Failed to add: " + dataMap, e);
                }
            }
        }

        protected boolean deleteDocument(final Map<String, Object> dataMap) {

            if (logger.isDebugEnabled()) {
                logger.debug("Deleting " + dataMap);
            }

            final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();

            //   required check
            if (!dataMap.containsKey(fieldHelper.urlField) || dataMap.get(fieldHelper.urlField) == null) {
                logger.warn("Could not delete a doc. Invalid data: " + dataMap);
                return false;
            }

            synchronized (indexUpdateCallback) {
                deleteIdList.add(crawlingSessionHelper.generateId(dataMap));

                if (deleteIdList.size() >= maxDeleteDocumentCacheSize) {
                    final IndexingHelper indexingHelper = ComponentUtil.getIndexingHelper();
                    for (final String id : deleteIdList) {
                        indexingHelper.deleteDocument(indexUpdateCallback.getSolrGroup(), id);
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("Deleted " + deleteIdList);
                    }
                    deleteIdList.clear();
                }

            }
            return true;
        }

        @Override
        public void setSolrGroup(final SolrGroup solrGroup) {
            indexUpdateCallback.setSolrGroup(solrGroup);
        }

        @Override
        public void setCommitPerCount(final long commitPerCount) {
            indexUpdateCallback.setCommitPerCount(commitPerCount);
        }

        @Override
        public long getDocumentSize() {
            return indexUpdateCallback.getDocumentSize();
        }

        @Override
        public long getExecuteTime() {
            return indexUpdateCallback.getExecuteTime();
        }

        @Override
        public void commit() {
            if (!deleteIdList.isEmpty()) {
                final IndexingHelper indexingHelper = ComponentUtil.getIndexingHelper();
                for (final String id : deleteIdList) {
                    indexingHelper.deleteDocument(indexUpdateCallback.getSolrGroup(), id);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Deleted " + deleteIdList);
                }
            }
            indexUpdateCallback.commit();
        }

        @Override
        public SolrGroup getSolrGroup() {
            return indexUpdateCallback.getSolrGroup();
        }
    }
}
