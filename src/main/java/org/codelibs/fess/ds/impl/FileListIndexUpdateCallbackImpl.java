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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.codelibs.fess.ds.IndexUpdateCallback;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.helper.IndexingHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.StreamUtil;
import org.lastaflute.di.core.SingletonLaContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileListIndexUpdateCallbackImpl implements IndexUpdateCallback {
    private static final Logger logger = LoggerFactory.getLogger(FileListIndexUpdateCallbackImpl.class);

    protected IndexUpdateCallback indexUpdateCallback;

    protected CrawlerClientFactory crawlerClientFactory;

    protected List<String> deleteIdList = new ArrayList<String>(100);

    protected int maxDeleteDocumentCacheSize = 100;

    protected FileListIndexUpdateCallbackImpl(final IndexUpdateCallback indexUpdateCallback, final CrawlerClientFactory crawlerClientFactory) {
        this.indexUpdateCallback = indexUpdateCallback;
        this.crawlerClientFactory = crawlerClientFactory;
    }

    @Override
    public boolean store(final Map<String, String> paramMap, final Map<String, Object> dataMap) {
        final Object eventType = dataMap.remove(getParamValue(paramMap, "field.event_type", "event_type"));

        if (getParamValue(paramMap, "event.create", "create").equals(eventType)
                || getParamValue(paramMap, "event.modify", "modify").equals(eventType)) {
            // updated file
            return addDocument(paramMap, dataMap);
        } else if (getParamValue(paramMap, "event.delete", "delete").equals(eventType)) {
            // deleted file
            return deleteDocument(paramMap, dataMap);
        }

        logger.warn("unknown event: " + eventType + ", data: " + dataMap);
        // don't stop crawling
        return true;
    }

    protected String getParamValue(Map<String, String> paramMap, String key, String defaultValue) {
        return paramMap.getOrDefault(key, defaultValue);
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
                                final Map<String, Object> responseDataMap = (Map<String, Object>) SerializeUtil.fromBinaryToObject(data);
                                dataMap.putAll(responseDataMap);
                            } catch (final Exception e) {
                                throw new CrawlerSystemException("Could not create an instance from bytes.", e);
                            }
                        }

                        // remove
                        String[] ignoreFields;
                        if (paramMap.containsKey("ignore.field.names")) {
                            ignoreFields = paramMap.get("ignore.field.names").split(",");
                        } else {
                            ignoreFields = new String[] { Constants.INDEXING_TARGET, Constants.SESSION_ID };
                        }
                        StreamUtil.of(ignoreFields).map(s -> s.trim()).forEach(s -> dataMap.remove(s));

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
            deleteIdList.add(ComponentUtil.getCrawlingInfoHelper().generateId(dataMap));

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

    public void setMaxDeleteDocumentCacheSize(int maxDeleteDocumentCacheSize) {
        this.maxDeleteDocumentCacheSize = maxDeleteDocumentCacheSize;
    }
}
