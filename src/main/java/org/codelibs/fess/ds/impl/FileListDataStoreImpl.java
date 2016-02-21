/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import org.codelibs.core.collection.LruHashMap;
import org.codelibs.core.io.SerializeUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.client.CrawlerClient;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.processor.ResponseProcessor;
import org.codelibs.fess.crawler.processor.impl.DefaultResponseProcessor;
import org.codelibs.fess.crawler.rule.Rule;
import org.codelibs.fess.crawler.rule.RuleManager;
import org.codelibs.fess.crawler.transformer.Transformer;
import org.codelibs.fess.ds.DataStoreCrawlingException;
import org.codelibs.fess.ds.DataStoreException;
import org.codelibs.fess.ds.IndexUpdateCallback;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.es.config.exentity.DataConfig;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.helper.IndexingHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.di.core.SingletonLaContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orangesignal.csv.CsvConfig;

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

    protected CrawlerClientFactory crawlerClientFactory;

    protected CrawlingInfoHelper crawlingInfoHelper;

    public Map<String, String> parentEncodingMap = Collections.synchronizedMap(new LruHashMap<>(1000));

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
    public void store(final DataConfig config, final IndexUpdateCallback callback, final Map<String, String> initParamMap) {

        crawlerClientFactory = SingletonLaContainer.getComponent(CrawlerClientFactory.class);

        config.initializeClientFactory(crawlerClientFactory);

        super.store(config, callback, initParamMap);
    }

    @Override
    protected void storeData(final DataConfig dataConfig, final IndexUpdateCallback callback, final Map<String, String> paramMap,
            final Map<String, String> scriptMap, final Map<String, Object> defaultDataMap) {

        final FileListIndexUpdateCallback fileListIndexUpdateCallback = new FileListIndexUpdateCallback(callback);
        super.storeData(dataConfig, fileListIndexUpdateCallback, paramMap, scriptMap, defaultDataMap);
        fileListIndexUpdateCallback.commit();
    }

    @Override
    protected void processCsv(final DataConfig dataConfig, final IndexUpdateCallback callback, final Map<String, String> paramMap,
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
        public boolean store(final Map<String, String> paramMap, final Map<String, Object> dataMap) {
            final Object eventType = dataMap.remove(eventTypeField);

            if (createEventName.equals(eventType) || modifyEventName.equals(eventType)) {
                // updated file
                return addDocument(paramMap, dataMap);
            } else if (deleteEventName.equals(eventType)) {
                // deleted file
                return deleteDocument(paramMap, dataMap);
            }

            logger.warn("unknown event: " + eventType + ", data: " + dataMap);
            return false;
        }

        protected boolean addDocument(final Map<String, String> paramMap, final Map<String, Object> dataMap) {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            synchronized (indexUpdateCallback) {
                // required check
                if (!dataMap.containsKey(fessConfig.getIndexFieldUrl()) || dataMap.get(fessConfig.getIndexFieldUrl()) == null) {
                    logger.warn("Could not add a doc. Invalid data: " + dataMap);
                    return false;
                }

                final String url = dataMap.get(fessConfig.getIndexFieldUrl()).toString();
                try {
                    final CrawlerClient client = crawlerClientFactory.getClient(url);
                    if (client == null) {
                        logger.warn("CrawlerClient is null. Data: " + dataMap);
                        return false;
                    }

                    final long startTime = System.currentTimeMillis();
                    final ResponseData responseData = client.execute(RequestDataBuilder.newRequestData().get().url(url).build());
                    responseData.setExecutionTime(System.currentTimeMillis() - startTime);
                    if (dataMap.containsKey(Constants.SESSION_ID)) {
                        responseData.setSessionId((String) dataMap.get(Constants.SESSION_ID));
                    } else {
                        responseData.setSessionId((String) paramMap.get(Constants.CRAWLING_INFO_ID));
                    }

                    final RuleManager ruleManager = SingletonLaContainer.getComponent(RuleManager.class);
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
                                    throw new CrawlerSystemException("Could not create an instance from bytes.", e);
                                }
                            }

                            // remove
                            for (final String fieldName : ignoreFieldNames) {
                                dataMap.remove(fieldName);
                            }

                            return indexUpdateCallback.store(paramMap, dataMap);
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

        protected boolean deleteDocument(final Map<String, String> paramMap, final Map<String, Object> dataMap) {

            if (logger.isDebugEnabled()) {
                logger.debug("Deleting " + dataMap);
            }

            final FessConfig fessConfig = ComponentUtil.getFessConfig();

            // required check
            if (!dataMap.containsKey(fessConfig.getIndexFieldUrl()) || dataMap.get(fessConfig.getIndexFieldUrl()) == null) {
                logger.warn("Could not delete a doc. Invalid data: " + dataMap);
                return false;
            }

            synchronized (indexUpdateCallback) {
                deleteIdList.add(crawlingInfoHelper.generateId(dataMap));

                if (deleteIdList.size() >= maxDeleteDocumentCacheSize) {
                    final FessEsClient fessEsClient = ComponentUtil.getElasticsearchClient();
                    final IndexingHelper indexingHelper = ComponentUtil.getIndexingHelper();
                    for (final String id : deleteIdList) {
                        indexingHelper.deleteDocument(fessEsClient, id);
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
        public void commit() {
            if (!deleteIdList.isEmpty()) {
                final FessEsClient fessEsClient = ComponentUtil.getElasticsearchClient();
                final IndexingHelper indexingHelper = ComponentUtil.getIndexingHelper();
                for (final String id : deleteIdList) {
                    indexingHelper.deleteDocument(fessEsClient, id);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Deleted " + deleteIdList);
                }
            }
            indexUpdateCallback.commit();
        }

        @Override
        public long getDocumentSize() {
            return indexUpdateCallback.getDocumentSize();
        }

        @Override
        public long getExecuteTime() {
            return indexUpdateCallback.getExecuteTime();
        }

    }
}
