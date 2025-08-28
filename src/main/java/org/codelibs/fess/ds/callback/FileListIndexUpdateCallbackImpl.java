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
package org.codelibs.fess.ds.callback;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.util.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.client.CrawlerClient;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.exception.ChildUrlsException;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.processor.ResponseProcessor;
import org.codelibs.fess.crawler.processor.impl.DefaultResponseProcessor;
import org.codelibs.fess.crawler.rule.Rule;
import org.codelibs.fess.crawler.rule.RuleManager;
import org.codelibs.fess.crawler.serializer.DataSerializer;
import org.codelibs.fess.crawler.transformer.Transformer;
import org.codelibs.fess.entity.DataStoreParams;
import org.codelibs.fess.exception.DataStoreCrawlingException;
import org.codelibs.fess.helper.CrawlerStatsHelper;
import org.codelibs.fess.helper.CrawlerStatsHelper.StatsAction;
import org.codelibs.fess.helper.CrawlerStatsHelper.StatsKeyObject;
import org.codelibs.fess.helper.IndexingHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.di.core.SingletonLaContainer;
import org.opensearch.index.query.QueryBuilders;

/**
 * Implementation of IndexUpdateCallback that handles file list index updates with concurrent processing.
 * This callback processes file events (create, modify, delete) and manages document indexing and deletion
 * operations in the search engine. It supports recursive crawling with configurable depth and access count limits.
 *
 * <p>The implementation uses an executor service for concurrent processing of file operations and maintains
 * a cache of URLs to be deleted for batch processing. It handles redirect following and child URL discovery
 * during the crawling process.</p>
 */
public class FileListIndexUpdateCallbackImpl implements IndexUpdateCallback {

    /** Logger for this class. */
    private static final Logger logger = LogManager.getLogger(FileListIndexUpdateCallbackImpl.class);

    /**
     * The key used to specify the pattern for excluding URLs.
     * This constant can be used to retrieve or set the URL exclusion pattern
     * in configuration or processing logic.
     */
    protected static final String URL_EXCLUDE_PATTERN = "url_exclude_pattern";

    /** The underlying index update callback to delegate operations to. */
    protected IndexUpdateCallback indexUpdateCallback;

    /** Factory for creating crawler clients to handle different URL schemes. */
    protected CrawlerClientFactory crawlerClientFactory;

    /** List of URLs to be deleted, cached for batch processing. */
    protected List<String> deleteUrlList = new ArrayList<>(100);

    /** Maximum size of the delete URL cache before batch deletion is triggered. */
    protected int maxDeleteDocumentCacheSize;

    /** Maximum number of redirects to follow when processing URLs. */
    protected int maxRedirectCount;

    /** Executor service for concurrent processing of file operations. */
    private final ExecutorService executor;

    /** Timeout in seconds for executor service termination during shutdown. */
    private int executorTerminationTimeout = 300;

    /**
     * Constructs a new FileListIndexUpdateCallbackImpl with the specified parameters.
     *
     * @param indexUpdateCallback the underlying index update callback to delegate to
     * @param crawlerClientFactory the factory for creating crawler clients
     * @param nThreads the number of threads for the executor service (minimum 1)
     */
    public FileListIndexUpdateCallbackImpl(final IndexUpdateCallback indexUpdateCallback, final CrawlerClientFactory crawlerClientFactory,
            final int nThreads) {
        this.indexUpdateCallback = indexUpdateCallback;
        this.crawlerClientFactory = crawlerClientFactory;
        executor = newFixedThreadPool(nThreads < 1 ? 1 : nThreads);
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        maxDeleteDocumentCacheSize = fessConfig.getIndexerDataMaxDeleteCacheSizeAsInteger();
        maxRedirectCount = fessConfig.getIndexerDataMaxRedirectCountAsInteger();
    }

    /**
     * Creates a new fixed thread pool executor with the specified number of threads.
     *
     * @param nThreads the number of threads in the pool
     * @return a new ThreadPoolExecutor configured for this callback
     */
    protected ExecutorService newFixedThreadPool(final int nThreads) {
        if (logger.isDebugEnabled()) {
            logger.debug("Executor Thread Pool: {}", nThreads);
        }
        return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(nThreads),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Override
    public void store(final DataStoreParams paramMap, final Map<String, Object> dataMap) {
        final CrawlerStatsHelper crawlerStatsHelper = ComponentUtil.getCrawlerStatsHelper();
        final StatsKeyObject keyObj = paramMap.get(Constants.CRAWLER_STATS_KEY) instanceof final StatsKeyObject sko ? sko : null;
        if (keyObj != null) {
            crawlerStatsHelper.runOnThread(keyObj);
        }
        final DataStoreParams localParams = paramMap.newInstance();
        executor.execute(() -> {
            try {
                final Object eventType = dataMap.remove(getParamValue(localParams, "field.event_type", "event_type"));
                if (getParamValue(localParams, "event.create", "create").equals(eventType)
                        || getParamValue(localParams, "event.modify", "modify").equals(eventType)) {
                    // updated file
                    addDocument(localParams, dataMap);
                } else if (getParamValue(localParams, "event.delete", "delete").equals(eventType)) {
                    // deleted file
                    deleteDocument(localParams, dataMap);
                } else {
                    logger.warn("unknown event: {}, data: {}", eventType, dataMap);
                }
            } finally {
                if (keyObj != null) {
                    crawlerStatsHelper.done(keyObj);
                }
            }
        });
    }

    /**
     * Retrieves a parameter value from the data store parameters map.
     *
     * @param paramMap the parameter map to search
     * @param key the parameter key to look up
     * @param defaultValue the default value to return if key is not found
     * @return the parameter value as a string, or the default value if not found
     */
    protected String getParamValue(final DataStoreParams paramMap, final String key, final String defaultValue) {
        return paramMap.getAsString(key, defaultValue);
    }

    /**
     * Adds a document to the search index by crawling the specified URL and processing the content.
     * This method handles recursive crawling with depth and access count limits, follows redirects,
     * and processes child URLs discovered during crawling.
     *
     * @param paramMap the data store parameters containing crawling configuration
     * @param dataMap the data map containing the document information including the URL
     */
    protected void addDocument(final DataStoreParams paramMap, final Map<String, Object> dataMap) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final CrawlerStatsHelper crawlerStatsHelper = ComponentUtil.getCrawlerStatsHelper();
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

            final StatsKeyObject keyObj = paramMap.get(Constants.CRAWLER_STATS_KEY) instanceof final StatsKeyObject sko ? sko : null;

            final long maxAccessCount = getMaxAccessCount(paramMap, dataMap);
            final int maxDepth = getMaxDepth(paramMap, dataMap);
            long counter = 0;
            final Deque<CrawlRequest> requestQueue = new LinkedList<>();
            final Set<String> processedUrls = new HashSet<>();
            requestQueue.offer(new CrawlRequest(url, 0));
            while (!requestQueue.isEmpty() && (maxAccessCount < 0 || counter < maxAccessCount)) {
                final CrawlRequest crawlRequest = requestQueue.poll();
                if ((maxDepth != -1 && crawlRequest.getDepth() > maxDepth) || processedUrls.contains(crawlRequest.getUrl())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Skipping crawl request for url='{}' at depth={} (maxDepth={}, alreadyProcessed={})",
                                crawlRequest.getUrl(), crawlRequest.getDepth(), maxDepth, processedUrls.contains(crawlRequest.getUrl()));
                    }
                    continue;
                }
                if (!isUrlCrawlable(paramMap, crawlRequest.getUrl())) {
                    continue;
                }
                counter++;
                final Map<String, Object> localDataMap =
                        dataMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                if (deleteUrlList.contains(crawlRequest.getUrl())) {
                    deleteDocuments(); // delete before indexing
                }
                try {
                    String currentUrl = crawlRequest.getUrl();
                    for (int i = 0; i < maxRedirectCount; i++) {
                        processedUrls.add(currentUrl);
                        if (keyObj != null) {
                            keyObj.setUrl(currentUrl);
                        }
                        crawlerStatsHelper.record(keyObj, StatsAction.PREPARED);
                        currentUrl = processRequest(paramMap, localDataMap, currentUrl, client);
                        if (currentUrl == null) {
                            break;
                        }
                        localDataMap.put(fessConfig.getIndexFieldUrl(), currentUrl);
                        crawlerStatsHelper.record(keyObj, StatsAction.REDIRECTED);
                    }
                } catch (final ChildUrlsException e) {
                    crawlerStatsHelper.record(keyObj, StatsAction.CHILD_URLS);
                    if (maxDepth == -1 || crawlRequest.getDepth() < maxDepth) {
                        e.getChildUrlList()
                                .stream() //
                                .filter(data -> !processedUrls.contains(data.getUrl())) //
                                .map(data -> new CrawlRequest(data.getUrl(), crawlRequest.getDepth() + 1)) //
                                .forEach(requestQueue::offer);
                    }
                } catch (final DataStoreCrawlingException e) {
                    crawlerStatsHelper.record(keyObj, StatsAction.ACCESS_EXCEPTION);
                    final Throwable cause = e.getCause();
                    if (cause instanceof ChildUrlsException) {
                        if (maxDepth == -1 || crawlRequest.getDepth() < maxDepth) {
                            ((ChildUrlsException) cause).getChildUrlList()
                                    .stream() //
                                    .filter(data -> !processedUrls.contains(data.getUrl())) //
                                    .map(data -> new CrawlRequest(data.getUrl(), crawlRequest.getDepth() + 1)) //
                                    .forEach(requestQueue::offer);
                        }
                    } else {
                        logger.warn("Failed to access {}.", crawlRequest, e);
                    }
                }
            }
        }
    }

    /**
     * Determines whether the specified URL is crawlable based on the exclusion pattern
     * provided in the {@code paramMap}. If the {@code URL_EXCLUDE_PATTERN} key exists in
     * the parameter map, its value is used as a regular expression pattern to match against
     * the given URL. If the URL matches the exclusion pattern, the method returns {@code false},
     * indicating that the URL should not be crawled. Otherwise, it returns {@code true}.
     *
     * @param paramMap the parameter map containing potential exclusion patterns
     * @param url the URL to be checked for crawlability
     * @return {@code true} if the URL is crawlable; {@code false} if it matches the exclusion pattern
     */
    protected boolean isUrlCrawlable(final DataStoreParams paramMap, final String url) {
        if (paramMap.containsKey(URL_EXCLUDE_PATTERN)) {
            if (paramMap.get(URL_EXCLUDE_PATTERN) instanceof final String value) {
                final Pattern pattern = Pattern.compile(value);
                paramMap.put(URL_EXCLUDE_PATTERN, pattern);
                if (logger.isDebugEnabled()) {
                    logger.debug("Using exclude pattern: {}", pattern);
                }
            }
            if (paramMap.get(URL_EXCLUDE_PATTERN) instanceof final Pattern pattern) {
                if (pattern.matcher(url).matches()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Skipping URL {} due to exclude pattern: {}", url, pattern);
                    }
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Represents a crawl request containing a URL and its depth in the crawling hierarchy.
     * Used for managing recursive crawling operations.
     */
    private static class CrawlRequest {
        /** The URL to be crawled. */
        private final String url;
        /** The depth of this URL in the crawling hierarchy. */
        private final int depth;

        /**
         * Constructs a new crawl request.
         *
         * @param url the URL to crawl
         * @param depth the depth of this URL in the crawling hierarchy
         */
        CrawlRequest(final String url, final int depth) {
            this.url = url;
            this.depth = depth;
        }

        /**
         * Gets the URL of this crawl request.
         *
         * @return the URL to be crawled
         */
        public String getUrl() {
            return url;
        }

        /**
         * Gets the depth of this crawl request in the crawling hierarchy.
         *
         * @return the crawling depth
         */
        public int getDepth() {
            return depth;
        }

        @Override
        public int hashCode() {
            return Objects.hash(url);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final CrawlRequest other = (CrawlRequest) obj;
            return Objects.equals(url, other.url);
        }

        @Override
        public String toString() {
            return "url=" + url + " depth=" + depth;
        }
    }

    /**
     * Determines the maximum number of URLs to access during crawling.
     * This method checks for explicit max_access_count parameter or recursive flag.
     *
     * @param paramMap the data store parameters
     * @param dataMap the data map containing crawling configuration
     * @return the maximum access count (-1 for unlimited, 1 for single access, or specified count)
     */
    protected long getMaxAccessCount(final DataStoreParams paramMap, final Map<String, Object> dataMap) {
        if (dataMap.remove(getParamValue(paramMap, "field.max_access_count", "max_access_count")) instanceof final String maxAccessCount
                && StringUtil.isNotBlank(maxAccessCount)) {
            try {
                return Long.parseLong(maxAccessCount);
            } catch (final NumberFormatException e) {
                if (logger.isDebugEnabled()) {
                    logger.warn("Failed to parse {}", maxAccessCount, e);
                } else {
                    logger.warn("Failed to parse {}", maxAccessCount);
                }
            }
        }

        final Object recursive = dataMap.remove(getParamValue(paramMap, "field.recursive", "recursive"));
        if (recursive == null || Constants.FALSE.equalsIgnoreCase(recursive.toString())) {
            return 1L;
        }
        if (Constants.TRUE.equalsIgnoreCase(recursive.toString())) {
            return -1L;
        }

        return 1L;
    }

    /**
     * Determines the maximum crawling depth from the configuration parameters.
     *
     * @param paramMap the data store parameters
     * @param dataMap the data map containing crawling configuration
     * @return the maximum crawling depth (-1 for unlimited depth)
     */
    protected int getMaxDepth(final DataStoreParams paramMap, final Map<String, Object> dataMap) {
        if (dataMap.remove(getParamValue(paramMap, "field.max_depth", "max_depth")) instanceof final String maxDepth
                && StringUtil.isNotBlank(maxDepth)) {
            try {
                return Integer.parseInt(maxDepth);
            } catch (final NumberFormatException e) {
                if (logger.isDebugEnabled()) {
                    logger.warn("Failed to parse {}", maxDepth, e);
                } else {
                    logger.warn("Failed to parse {}", maxDepth);
                }
            }
        }

        return -1;
    }

    /**
     * Processes a single crawl request by executing the HTTP request, handling redirects,
     * transforming the response data, and indexing the document.
     *
     * @param paramMap the data store parameters
     * @param dataMap the data map to be updated with response data
     * @param url the URL to process
     * @param client the crawler client to use for the request
     * @return the redirect URL if a redirect occurred, null otherwise
     * @throws ChildUrlsException if child URLs are discovered during processing
     * @throws DataStoreCrawlingException if an error occurs during crawling
     */
    protected String processRequest(final DataStoreParams paramMap, final Map<String, Object> dataMap, final String url,
            final CrawlerClient client) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final long startTime = systemHelper.getCurrentTimeAsLong();
        final CrawlerStatsHelper crawlerStatsHelper = ComponentUtil.getCrawlerStatsHelper();
        final StatsKeyObject keyObj = paramMap.get(Constants.CRAWLER_STATS_KEY) instanceof final StatsKeyObject sko ? sko : null;
        try (final ResponseData responseData = client.execute(RequestDataBuilder.newRequestData().get().url(url).build())) {
            if (responseData.getRedirectLocation() != null) {
                return responseData.getRedirectLocation();
            }
            responseData.setExecutionTime(systemHelper.getCurrentTimeAsLong() - startTime);
            if (dataMap.containsKey(Constants.SESSION_ID)) {
                responseData.setSessionId((String) dataMap.get(Constants.SESSION_ID));
            } else {
                responseData.setSessionId((String) paramMap.get(Constants.CRAWLING_INFO_ID));
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
                    final Object rawData = resultData.getRawData();
                    if (rawData != null) {
                        @SuppressWarnings("unchecked")
                        final Map<String, Object> responseDataMap = (Map<String, Object>) rawData;
                        mergeResponseData(dataMap, responseDataMap);
                    } else {
                        final byte[] data = resultData.getData();
                        if (data != null) {
                            try {
                                final DataSerializer dataSerializer = ComponentUtil.getComponent("dataSerializer");
                                @SuppressWarnings("unchecked")
                                final Map<String, Object> responseDataMap = (Map<String, Object>) dataSerializer.fromBinaryToObject(data);
                                mergeResponseData(dataMap, responseDataMap);
                            } catch (final Exception e) {
                                throw new CrawlerSystemException("Could not create an instance from bytes.", e);
                            }
                        }
                    }
                    crawlerStatsHelper.record(keyObj, StatsAction.ACCESSED);

                    // remove
                    String[] ignoreFields;
                    if (paramMap.containsKey("ignore.field.names")) {
                        ignoreFields = ((String) paramMap.get("ignore.field.names")).split(",");
                    } else {
                        ignoreFields = new String[] { Constants.INDEXING_TARGET, Constants.SESSION_ID };
                    }
                    stream(ignoreFields).of(stream -> stream.map(String::trim).forEach(s -> dataMap.remove(s)));

                    indexUpdateCallback.store(paramMap, dataMap);
                    crawlerStatsHelper.record(keyObj, StatsAction.PROCESSED);
                } else {
                    logger.warn("The response processor is not DefaultResponseProcessor. responseProcessor: {}, Data: {}",
                            responseProcessor, dataMap);
                }
            }
            return null;
        } catch (final ChildUrlsException | DataStoreCrawlingException e) {
            throw e;
        } catch (final Exception e) {
            throw new DataStoreCrawlingException(url, "Failed to add: " + dataMap, e);
        }
    }

    /**
     * Merges response data from the crawler into the original data map.
     * Handles special ".overwrite" suffix fields by removing the suffix and overwriting the base field.
     *
     * @param dataMap the original data map to merge into
     * @param responseDataMap the response data map from the crawler
     */
    protected void mergeResponseData(final Map<String, Object> dataMap, final Map<String, Object> responseDataMap) {
        dataMap.putAll(responseDataMap);
        dataMap.keySet()
                .stream()
                .filter(key -> key.endsWith(".overwrite")) //
                .collect(Collectors.toList())
                .forEach(key -> {
                    final String baseKey = key.substring(0, key.length() - ".overwrite".length());
                    final Object value = dataMap.remove(key);
                    dataMap.put(baseKey, value);
                });
    }

    /**
     * Deletes a document from the search index based on the URL in the data map.
     * For recursive operations, performs immediate deletion. For single documents,
     * adds to the delete cache for batch processing.
     *
     * @param paramMap the data store parameters
     * @param dataMap the data map containing the URL to delete
     * @return true if the deletion was processed successfully, false otherwise
     */
    protected boolean deleteDocument(final DataStoreParams paramMap, final Map<String, Object> dataMap) {

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
            final long maxAccessCount = getMaxAccessCount(paramMap, dataMap);
            final String url = dataMap.get(fessConfig.getIndexFieldUrl()).toString();
            if (maxAccessCount != 1L) {
                final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
                final IndexingHelper indexingHelper = ComponentUtil.getIndexingHelper();
                final long count = indexingHelper.deleteDocumentByQuery(searchEngineClient,
                        QueryBuilders.prefixQuery(fessConfig.getIndexFieldUrl(), url));
                if (logger.isDebugEnabled()) {
                    logger.debug("Deleted {} docs for {}*", count, url);
                }
            } else {
                deleteUrlList.add(url);

                if (deleteUrlList.size() >= maxDeleteDocumentCacheSize) {
                    deleteDocuments();
                }
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

    /**
     * Performs batch deletion of all URLs in the delete cache.
     * Clears the delete URL list after processing.
     */
    protected void deleteDocuments() {
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        final IndexingHelper indexingHelper = ComponentUtil.getIndexingHelper();
        for (final String url : deleteUrlList) {
            indexingHelper.deleteDocumentByUrl(searchEngineClient, url);
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

    /**
     * Sets the maximum size of the delete document cache.
     *
     * @param maxDeleteDocumentCacheSize the maximum cache size before batch deletion is triggered
     */
    public void setMaxDeleteDocumentCacheSize(final int maxDeleteDocumentCacheSize) {
        this.maxDeleteDocumentCacheSize = maxDeleteDocumentCacheSize;
    }

    /**
     * Sets the maximum number of redirects to follow when processing URLs.
     *
     * @param maxRedirectCount the maximum redirect count
     */
    public void setMaxRedirectCount(final int maxRedirectCount) {
        this.maxRedirectCount = maxRedirectCount;
    }

    /**
     * Sets the timeout for executor service termination during shutdown.
     *
     * @param executorTerminationTimeout the timeout in seconds
     */
    public void setExecutorTerminationTimeout(final int executorTerminationTimeout) {
        this.executorTerminationTimeout = executorTerminationTimeout;
    }

}
