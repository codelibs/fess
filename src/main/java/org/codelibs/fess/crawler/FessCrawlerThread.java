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
package org.codelibs.fess.crawler;

import static org.codelibs.core.stream.StreamUtil.split;
import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.fess.app.service.FailureUrlService;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.client.CrawlerClient;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.log.LogType;
import org.codelibs.fess.exception.ContainerNotAvailableException;
import org.codelibs.fess.exception.ContentNotFoundException;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.helper.DuplicateHostHelper;
import org.codelibs.fess.helper.IndexingHelper;
import org.codelibs.fess.helper.PermissionHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;

/**
 * FessCrawlerThread is a specialized crawler thread implementation for the Fess search engine.
 * This class extends the base CrawlerThread and provides Fess-specific functionality for
 * crawling and indexing documents, including incremental crawling capabilities, content
 * modification checking, and integration with the Fess search engine backend.
 *
 * <p>Key features include:</p>
 * <ul>
 * <li>Incremental crawling support with last-modified timestamp checking</li>
 * <li>Document expiration handling</li>
 * <li>Child URL extraction and queueing</li>
 * <li>Integration with Fess configuration and permission systems</li>
 * <li>Client selection based on URL patterns</li>
 * </ul>
 *
 * @see CrawlerThread
 * @see org.codelibs.fess.crawler.client.CrawlerClient
 */
public class FessCrawlerThread extends CrawlerThread {

    /**
     * Default constructor.
     */
    public FessCrawlerThread() {
        super();
    }

    private static final Logger logger = LogManager.getLogger(FessCrawlerThread.class);

    /** Configuration key for crawler clients used in parameter maps */
    protected static final String CRAWLER_CLIENTS = "crawlerClients";

    /**
     * Cache for client rules mapping client names to their corresponding URL patterns.
     * This cache improves performance by avoiding repeated parsing of client configuration rules.
     * The key is the rule string, and the value is a pair containing the client name and compiled pattern.
     */
    protected ConcurrentHashMap<String, Pair<String, Pattern>> clientRuleCache = new ConcurrentHashMap<>();

    /**
     * Determines whether the content at the given URL has been updated since the last crawl.
     * This method implements incremental crawling by comparing timestamps and checking document
     * expiration. It also handles special cases for different URL schemes (SMB, file, FTP).
     *
     * @param client the crawler client to use for accessing the URL
     * @param urlQueue the URL queue item containing the URL to check
     * @return true if the content has been updated and should be crawled, false otherwise
     */
    @Override
    protected boolean isContentUpdated(final CrawlerClient client, final UrlQueue<?> urlQueue) {
        if (ComponentUtil.getFessConfig().isIncrementalCrawling()) {

            final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
            final long startTime = systemHelper.getCurrentTimeAsLong();

            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final CrawlingConfigHelper crawlingConfigHelper = ComponentUtil.getCrawlingConfigHelper();
            final CrawlingInfoHelper crawlingInfoHelper = ComponentUtil.getCrawlingInfoHelper();
            final IndexingHelper indexingHelper = ComponentUtil.getIndexingHelper();
            final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();

            final String url = urlQueue.getUrl();
            ResponseData responseData = null;
            try {
                final CrawlingConfig crawlingConfig = crawlingConfigHelper.get(crawlerContext.getSessionId());
                final Map<String, Object> dataMap = new HashMap<>();
                dataMap.put(fessConfig.getIndexFieldUrl(), url);
                final List<String> roleTypeList = new ArrayList<>();
                stream(crawlingConfig.getPermissions()).of(stream -> stream.forEach(p -> roleTypeList.add(p)));
                if (url.startsWith("smb:") || url.startsWith("smb1:") || url.startsWith("file:") || url.startsWith("ftp:")) {
                    if (url.endsWith("/")) {
                        // directory
                        return true;
                    }
                    final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
                    if (fessConfig.isSmbRoleFromFile() || fessConfig.isFileRoleFromFile() || fessConfig.isFtpRoleFromFile()) {
                        // head method
                        responseData =
                                client.execute(RequestDataBuilder.newRequestData().head().url(url).weight(urlQueue.getWeight()).build());
                        if (responseData == null) {
                            return true;
                        }

                        roleTypeList.addAll(permissionHelper.getSmbRoleTypeList(responseData));
                        roleTypeList.addAll(permissionHelper.getFileRoleTypeList(responseData));
                        roleTypeList.addAll(permissionHelper.getFtpRoleTypeList(responseData));
                    }
                }
                dataMap.put(fessConfig.getIndexFieldRole(), roleTypeList);
                final String id = crawlingInfoHelper.generateId(dataMap);

                if (logger.isDebugEnabled()) {
                    logger.debug("Searching indexed document: {}", id);
                }
                final Map<String, Object> document = indexingHelper.getDocument(searchEngineClient, id,
                        new String[] { fessConfig.getIndexFieldId(), fessConfig.getIndexFieldLastModified(),
                                fessConfig.getIndexFieldAnchor(), fessConfig.getIndexFieldSegment(), fessConfig.getIndexFieldExpires(),
                                fessConfig.getIndexFieldClickCount(), fessConfig.getIndexFieldFavoriteCount() });
                if (document == null) {
                    storeChildUrlsToQueue(urlQueue, getChildUrlSet(searchEngineClient, id));
                    return true;
                }

                final Date expires = DocumentUtil.getValue(document, fessConfig.getIndexFieldExpires(), Date.class);
                if (expires != null && expires.getTime() < systemHelper.getCurrentTimeAsLong()) {
                    final Object idValue = document.get(fessConfig.getIndexFieldId());
                    if (idValue != null && !indexingHelper.deleteDocument(searchEngineClient, idValue.toString())) {
                        logger.debug("Failed to delete expired document: {}", url);
                    }
                    return true;
                }

                final Date lastModified = DocumentUtil.getValue(document, fessConfig.getIndexFieldLastModified(), Date.class);
                if (lastModified == null) {
                    return true;
                }
                urlQueue.setLastModified(lastModified.getTime());
                log(logHelper, LogType.CHECK_LAST_MODIFIED, crawlerContext, urlQueue);

                if (responseData == null) {
                    // head method
                    responseData = client.execute(RequestDataBuilder.newRequestData().head().url(url).build());
                    if (responseData == null) {
                        return true;
                    }
                }

                final int httpStatusCode = responseData.getHttpStatusCode();
                if (logger.isDebugEnabled()) {
                    logger.debug("Accessing document: {}, status: {}", url, httpStatusCode);
                }
                if (httpStatusCode == 404) {
                    storeChildUrlsToQueue(urlQueue, getAnchorSet(document.get(fessConfig.getIndexFieldAnchor())));
                    if (!indexingHelper.deleteDocument(searchEngineClient, id)) {
                        logger.debug("Failed to delete 404 document: {}", url);
                    }
                    return false;
                }
                if (responseData.getLastModified() == null) {
                    return true;
                }
                if (responseData.getLastModified().getTime() <= lastModified.getTime() && httpStatusCode == 200) {

                    log(logHelper, LogType.NOT_MODIFIED, crawlerContext, urlQueue);

                    responseData.setExecutionTime(systemHelper.getCurrentTimeAsLong() - startTime);
                    responseData.setParentUrl(urlQueue.getParentUrl());
                    responseData.setSessionId(crawlerContext.getSessionId());
                    responseData.setHttpStatusCode(org.codelibs.fess.crawler.Constants.NOT_MODIFIED_STATUS);
                    processResponse(urlQueue, responseData);

                    storeChildUrlsToQueue(urlQueue, getAnchorSet(document.get(fessConfig.getIndexFieldAnchor())));

                    final Date documentExpires = crawlingInfoHelper.getDocumentExpires(crawlingConfig);
                    if (documentExpires != null
                            && !indexingHelper.updateDocument(searchEngineClient, id, fessConfig.getIndexFieldExpires(), documentExpires)) {
                        logger.debug("Failed to update {} at {}", fessConfig.getIndexFieldExpires(), url);
                    }

                    return false;
                }
            } finally {
                if (responseData != null) {
                    CloseableUtil.closeQuietly(responseData);
                }
            }
        }
        return true;
    }

    /**
     * Stores child URLs from the given set into the crawling queue for future processing.
     * This method filters out blank URLs and increments the depth for child URLs.
     *
     * @param urlQueue the parent URL queue item
     * @param childUrlSet the set of child URLs to be queued for crawling
     */
    protected void storeChildUrlsToQueue(final UrlQueue<?> urlQueue, final Set<RequestData> childUrlSet) {
        if (childUrlSet != null) {
            // add an url
            try {
                storeChildUrls(childUrlSet.stream().filter(rd -> StringUtil.isNotBlank(rd.getUrl())).collect(Collectors.toSet()),
                        urlQueue.getUrl(), urlQueue.getDepth() != null ? urlQueue.getDepth() + 1 : 1);
            } catch (final Throwable t) {
                if (!ComponentUtil.available()) {
                    throw new ContainerNotAvailableException(t);
                }
                throw t;
            }
        }
    }

    /**
     * Extracts anchor URLs from the given object and converts them to RequestData objects.
     * The input object can be either a single string or a list of strings representing URLs.
     *
     * @param obj the object containing anchor URLs (String or List of Strings)
     * @return a set of RequestData objects for the anchor URLs, or null if no valid URLs found
     */
    protected Set<RequestData> getAnchorSet(final Object obj) {
        List<String> anchorList;
        if (obj instanceof final String s) {
            anchorList = List.of(s);
        } else if (obj instanceof final List<?> l) {
            anchorList = l.stream().map(String::valueOf).toList();
        } else {
            return null;
        }

        if (anchorList.isEmpty()) {
            return null;
        }

        final Set<RequestData> childUrlSet = new LinkedHashSet<>();
        for (final String anchor : anchorList) {
            childUrlSet.add(RequestDataBuilder.newRequestData().get().url(anchor).build());
        }
        return childUrlSet;
    }

    /**
     * Retrieves child URLs for a given document ID from the search engine index.
     * This method queries the search engine for child documents and extracts their URLs.
     *
     * @param searchEngineClient the search engine client to query
     * @param id the parent document ID to find children for
     * @return a set of RequestData objects for the child URLs, or null if no children found
     */
    protected Set<RequestData> getChildUrlSet(final SearchEngineClient searchEngineClient, final String id) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final IndexingHelper indexingHelper = ComponentUtil.getIndexingHelper();
        final List<Map<String, Object>> docList =
                indexingHelper.getChildDocumentList(searchEngineClient, id, new String[] { fessConfig.getIndexFieldUrl() });
        if (docList.isEmpty()) {
            return null;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Found documents: {}", docList);
        }
        final Set<RequestData> urlSet = new HashSet<>(docList.size());
        for (final Map<String, Object> doc : docList) {
            final String url = DocumentUtil.getValue(doc, fessConfig.getIndexFieldUrl(), String.class);
            if (StringUtil.isNotBlank(url)) {
                urlSet.add(RequestDataBuilder.newRequestData().get().url(url).build());
            }
        }
        return urlSet;
    }

    /**
     * Processes the response data from a crawled URL, including failure handling.
     * This method extends the base response processing to handle Fess-specific failure
     * URL tracking when certain HTTP status codes are encountered.
     *
     * @param urlQueue the URL queue item that was processed
     * @param responseData the response data from the crawl operation
     */
    @Override
    protected void processResponse(final UrlQueue<?> urlQueue, final ResponseData responseData) {
        super.processResponse(urlQueue, responseData);

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (fessConfig.isCrawlerFailureUrlStatusCodes(responseData.getHttpStatusCode())) {
            final String sessionId = crawlerContext.getSessionId();
            final CrawlingConfig crawlingConfig = ComponentUtil.getCrawlingConfigHelper().get(sessionId);
            final String url = urlQueue.getUrl();

            final FailureUrlService failureUrlService = ComponentUtil.getComponent(FailureUrlService.class);
            failureUrlService.store(crawlingConfig, ContentNotFoundException.class.getCanonicalName(), url,
                    new ContentNotFoundException(urlQueue.getParentUrl(), url));
        }
    }

    /**
     * Stores a child URL in the crawling queue with duplicate host handling.
     * This method applies duplicate host conversion before storing the URL.
     *
     * @param childUrl the child URL to store
     * @param parentUrl the parent URL that referenced this child URL
     * @param weight the weight/priority of the child URL
     * @param depth the crawling depth of the child URL
     */
    @Override
    protected void storeChildUrl(final String childUrl, final String parentUrl, final float weight, final int depth) {
        if (StringUtil.isNotBlank(childUrl)) {
            final DuplicateHostHelper duplicateHostHelper = ComponentUtil.getDuplicateHostHelper();
            final String url = duplicateHostHelper.convert(childUrl);
            super.storeChildUrl(url, parentUrl, weight, depth);
        }
    }

    /**
     * Retrieves the appropriate crawler client for the given URL based on configured rules.
     * This method uses client rules to determine which specific client implementation
     * should be used for crawling the URL, falling back to the default client if no
     * specific rule matches.
     *
     * @param url the URL to get a client for
     * @return the crawler client instance to use for the URL
     */
    @Override
    protected CrawlerClient getClient(final String url) {
        final CrawlingConfigHelper crawlingConfigHelper = ComponentUtil.getCrawlingConfigHelper();
        final CrawlingConfig crawlingConfig = crawlingConfigHelper.get(crawlerContext.getSessionId());
        final Map<String, String> clientConfigMap = crawlingConfig.getConfigParameterMap(ConfigName.CLIENT);
        final String value = clientConfigMap.get(CRAWLER_CLIENTS);
        final CrawlerClient client = getClientRuleList(value).stream().map(e -> {
            if (e.getSecond().matcher(url).matches()) {
                return e.getFirst();
            }
            return null;
        }).filter(StringUtil::isNotBlank).findFirst()//
                .map(s -> clientFactory.getClient(s + ":" + url))//
                .orElseGet(() -> clientFactory.getClient(url));
        if (logger.isDebugEnabled()) {
            logger.debug("CrawlerClient: {}", client.getClass().getCanonicalName());
        }
        return client;
    }

    /**
     * Parses client rule configuration string into a list of client name and pattern pairs.
     * The configuration string format is "clientName:pattern,clientName:pattern,..."
     * Results are cached to improve performance on subsequent calls.
     *
     * @param value the client rule configuration string
     * @return a list of pairs containing client names and their corresponding compiled patterns
     */
    protected List<Pair<String, Pattern>> getClientRuleList(final String value) {
        if (StringUtil.isBlank(value)) {
            return Collections.emptyList();
        }
        return split(value, ",").get(stream -> stream.map(String::trim)//
                .map(s -> clientRuleCache.computeIfAbsent(s, t -> {
                    final String[] values = t.split(":", 2);
                    if (values.length != 2) {
                        return null;
                    }
                    return new Pair<>(values[0], Pattern.compile(values[1]));
                })).toList());
    }
}
