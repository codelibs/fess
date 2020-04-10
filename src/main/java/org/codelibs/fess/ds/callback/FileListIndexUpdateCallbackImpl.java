/*
 * Copyright 2012-2020 CodeLibs Project and the Others.
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
package org.codelibs.fess.ds.callback;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.SerializeUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.client.CrawlerClient;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.exception.ChildUrlsException;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.processor.ResponseProcessor;
import org.codelibs.fess.crawler.processor.impl.DefaultResponseProcessor;
import org.codelibs.fess.crawler.rule.Rule;
import org.codelibs.fess.crawler.rule.RuleManager;
import org.codelibs.fess.crawler.transformer.Transformer;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.exception.DataStoreCrawlingException;
import org.codelibs.fess.helper.IndexingHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.di.core.SingletonLaContainer;

public class FileListIndexUpdateCallbackImpl implements IndexUpdateCallback {
    private static final Logger logger = LogManager.getLogger(FileListIndexUpdateCallbackImpl.class);

    protected IndexUpdateCallback indexUpdateCallback;

    protected CrawlerClientFactory crawlerClientFactory;

    protected List<String> deleteUrlList = new ArrayList<>(100);

    protected int maxDeleteDocumentCacheSize = 100;

    protected int maxRedirectCount = 10;

    private final ExecutorService executor;

    private int executorTerminationTimeout = 300;

    public FileListIndexUpdateCallbackImpl(final IndexUpdateCallback indexUpdateCallback, final CrawlerClientFactory crawlerClientFactory,
            final int nThreads) {
        this.indexUpdateCallback = indexUpdateCallback;
        this.crawlerClientFactory = crawlerClientFactory;
        executor = newFixedThreadPool(nThreads < 1 ? 1 : nThreads);
    }

    protected ExecutorService newFixedThreadPool(final int nThreads) {
        if (logger.isDebugEnabled()) {
            logger.debug("Executor Thread Pool: {}", nThreads);
        }
        return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(nThreads),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Override
    public void store(final Map<String, String> paramMap, final Map<String, Object> dataMap) {
        executor.execute(() -> {
            final Object eventType = dataMap.remove(getParamValue(paramMap, "field.event_type", "event_type"));
            if (getParamValue(paramMap, "event.create", "create").equals(eventType)
                    || getParamValue(paramMap, "event.modify", "modify").equals(eventType)) {
                // updated file
                addDocument(paramMap, dataMap);
            } else if (getParamValue(paramMap, "event.delete", "delete").equals(eventType)) {
                // deleted file
                deleteDocument(paramMap, dataMap);
            } else {
                logger.warn("unknown event: {}, data: {}", eventType, dataMap);
            }
        });
    }

    protected String getParamValue(final Map<String, String> paramMap, final String key, final String defaultValue) {
        return paramMap.getOrDefault(key, defaultValue);
    }

    protected void addDocument(final Map<String, String> paramMap, final Map<String, Object> dataMap) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        synchronized (indexUpdateCallback) {
            // required check
            if (!dataMap.containsKey(fessConfig.getIndexFieldUrl()) || dataMap.get(fessConfig.getIndexFieldUrl()) == null) {
                logger.warn("Could not add a doc. Invalid data: {}", dataMap);
                return;
            }

            final String url = dataMap.get(fessConfig.getIndexFieldUrl()).toString();
            final CrawlerClient client = crawlerClientFactory.getClient(url);
            if (client == null) {
                logger.warn("CrawlerClient is null. Data: {}", dataMap);
                return;
            }

            String processingUrl = url;
            for (int i = 0; i < maxRedirectCount; i++) {
                processingUrl = processRequest(paramMap, dataMap, processingUrl, client);
                if (processingUrl == null) {
                    break;
                }
                dataMap.put(fessConfig.getIndexFieldUrl(), processingUrl);
            }
        }
    }

    protected String processRequest(final Map<String, String> paramMap, final Map<String, Object> dataMap, final String url,
            final CrawlerClient client) {
        final long startTime = System.currentTimeMillis();
        try (final ResponseData responseData = client.execute(RequestDataBuilder.newRequestData().get().url(url).build())) {
            if (responseData.getRedirectLocation() != null) {
                return responseData.getRedirectLocation();
            }
            responseData.setExecutionTime(System.currentTimeMillis() - startTime);
            if (dataMap.containsKey(Constants.SESSION_ID)) {
                responseData.setSessionId((String) dataMap.get(Constants.SESSION_ID));
            } else {
                responseData.setSessionId(paramMap.get(Constants.CRAWLING_INFO_ID));
            }

            final RuleManager ruleManager = SingletonLaContainer.getComponent(RuleManager.class);
            final Rule rule = ruleManager.getRule(responseData);
            if (rule == null) {
                logger.warn("No url rule. Data: {}", dataMap);
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
                    stream(ignoreFields).of(stream -> stream.map(String::trim).forEach(s -> dataMap.remove(s)));

                    indexUpdateCallback.store(paramMap, dataMap);
                } else {
                    logger.warn("The response processor is not DefaultResponseProcessor. responseProcessor: " + responseProcessor
                            + ", Data: " + dataMap);
                }
            }
            return null;
        } catch (final ChildUrlsException e) {
            throw new DataStoreCrawlingException(url, "Redirected to "
                    + e.getChildUrlList().stream().map(RequestData::getUrl).collect(Collectors.joining(", ")), e);
        } catch (final Exception e) {
            throw new DataStoreCrawlingException(url, "Failed to add: " + dataMap, e);
        }
    }

    protected boolean deleteDocument(final Map<String, String> paramMap, final Map<String, Object> dataMap) {

        if (logger.isDebugEnabled()) {
            logger.debug("Deleting {}", dataMap);
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        // required check
        if (!dataMap.containsKey(fessConfig.getIndexFieldUrl()) || dataMap.get(fessConfig.getIndexFieldUrl()) == null) {
            logger.warn("Could not delete a doc. Invalid data: {}", dataMap);
            return false;
        }

        synchronized (indexUpdateCallback) {
            deleteUrlList.add(dataMap.get(fessConfig.getIndexFieldUrl()).toString());

            if (deleteUrlList.size() >= maxDeleteDocumentCacheSize) {
                deleteDocuments();
            }

        }
        return true;
    }

    @Override
    public void commit() {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Shutting down thread executor.");
            }
            executor.shutdown();
            executor.awaitTermination(executorTerminationTimeout, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to interrupt executor.", e);
            }
        } finally {
            executor.shutdownNow();
        }

        if (!deleteUrlList.isEmpty()) {
            deleteDocuments();
        }
        indexUpdateCallback.commit();
    }

    protected void deleteDocuments() {
        final FessEsClient fessEsClient = ComponentUtil.getFessEsClient();
        final IndexingHelper indexingHelper = ComponentUtil.getIndexingHelper();
        for (final String url : deleteUrlList) {
            indexingHelper.deleteDocumentByUrl(fessEsClient, url);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Deleted {}", deleteUrlList);
        }
        deleteUrlList.clear();
    }

    @Override
    public long getDocumentSize() {
        return indexUpdateCallback.getDocumentSize();
    }

    @Override
    public long getExecuteTime() {
        return indexUpdateCallback.getExecuteTime();
    }

    public void setMaxDeleteDocumentCacheSize(final int maxDeleteDocumentCacheSize) {
        this.maxDeleteDocumentCacheSize = maxDeleteDocumentCacheSize;
    }

    public void setMaxRedirectCount(final int maxRedirectCount) {
        this.maxRedirectCount = maxRedirectCount;
    }

    public void setExecutorTerminationTimeout(final int executorTerminationTimeout) {
        this.executorTerminationTimeout = executorTerminationTimeout;
    }

}
