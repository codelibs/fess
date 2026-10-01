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

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

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
     * Formats an HTTP-date (IMF-fixdate of RFC 9110, e.g. {@code Sun, 06 Nov 1994 08:49:37 GMT}).
     * {@link DateTimeFormatter#RFC_1123_DATE_TIME} is not used because it does not zero-pad the day.
     */
    private static final DateTimeFormatter HTTP_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH).withZone(ZoneOffset.UTC);

    /**
     * The validators sent with a conditional GET.
     *
     * @param ifNoneMatch the value of the If-None-Match header (the indexed ETag), or null
     * @param ifModifiedSince the value of the If-Modified-Since header (the indexed last_modified as an HTTP-date), or null
     */
    protected record ConditionalHeaders(String ifNoneMatch, String ifModifiedSince) {
    }

    /**
     * The conditional GET that {@link #isContentUpdated(CrawlerClient, UrlQueue)} prepared for a URL.
     *
     * @param url the URL the conditional GET is for
     * @param document the indexed document of the URL
     * @param id the id of the indexed document
     * @param headers the validators to send
     */
    protected record ConditionalGetState(String url, Map<String, Object> document, String id, ConditionalHeaders headers) {
    }

    /**
     * The conditional GET pending for the URL this thread is crawling, or null. It is cleared at the
     * start of each {@link #isContentUpdated(CrawlerClient, UrlQueue)} call, when the response of the
     * URL is processed, and when the crawl of the URL ends ({@link #finishCrawling()}), and it applies
     * only to the URL it was prepared for.
     */
    protected ConditionalGetState conditionalGetState;

    /**
     * Determines whether the content at the given URL has been updated since the last crawl.
     * This method implements incremental crawling by comparing timestamps and checking document
     * expiration. It also handles special cases for different URL schemes (SMB, file, FTP).
     *
     * <p>For an http or https URL whose indexed document cannot be judged by a HEAD request and its
     * Last-Modified (no indexed last_modified, no Last-Modified in the HEAD response, or a HEAD status
     * other than 200 and 404), the GET is sent as a conditional request with the indexed ETag and
     * last_modified; see {@link #createRequestData(UrlQueue)} and
     * {@link #processResponse(UrlQueue, ResponseData)}.</p>
     *
     * @param client the crawler client to use for accessing the URL
     * @param urlQueue the URL queue item containing the URL to check
     * @return true if the content has been updated and should be crawled, false otherwise
     */
    @Override
    protected boolean isContentUpdated(final CrawlerClient client, final UrlQueue<?> urlQueue) {
        conditionalGetState = null;
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (fessConfig.isIncrementalCrawling()) {

            final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
            final long startTime = systemHelper.getCurrentTimeAsLong();

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
                final String[] permissions = crawlingConfig.getPermissions();
                if (permissions != null) {
                    Collections.addAll(roleTypeList, permissions);
                }
                if (ComponentUtil.getProtocolHelper().isFilePathProtocol(url)) {
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
                                fessConfig.getIndexFieldClickCount(), fessConfig.getIndexFieldFavoriteCount(),
                                fessConfig.getIndexFieldEtag() });
                if (document == null) {
                    storeChildUrlsToQueue(urlQueue, getChildUrlSet(searchEngineClient, id));
                    return true;
                }

                final Date expires = DocumentUtil.getValue(document, fessConfig.getIndexFieldExpires(), Date.class);
                if (expires != null && expires.getTime() < systemHelper.getCurrentTimeAsLong()) {
                    final Object idValue = document.get(fessConfig.getIndexFieldId());
                    if (idValue != null && !indexingHelper.deleteDocument(searchEngineClient, idValue.toString())
                            && logger.isDebugEnabled()) {
                        logger.debug("Failed to delete expired document: {}", url);
                    }
                    return true;
                }

                final Date lastModified = DocumentUtil.getValue(document, fessConfig.getIndexFieldLastModified(), Date.class);
                if (lastModified == null) {
                    prepareConditionalGet(url, document, id, null);
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
                    logger.debug("Accessing document: url={}, status={}", url, httpStatusCode);
                }
                if (httpStatusCode == Constants.NOT_FOUND_STATUS_CODE) {
                    storeChildUrlsToQueue(urlQueue, getAnchorSet(document.get(fessConfig.getIndexFieldAnchor())));
                    if (!indexingHelper.deleteDocument(searchEngineClient, id) && logger.isDebugEnabled()) {
                        logger.debug("Failed to delete document: status={}, url={}", Constants.NOT_FOUND_STATUS_CODE, url);
                    }
                    return false;
                }
                final Date responseLastModified = responseData.getLastModified();
                if (responseLastModified == null) {
                    prepareConditionalGet(url, document, id, responseData);
                    return true;
                }
                if (responseLastModified.getTime() <= lastModified.getTime() && httpStatusCode == Constants.OK_STATUS_CODE) {
                    responseData.setExecutionTime(systemHelper.getCurrentTimeAsLong() - startTime);
                    handleUnchangedDocument(urlQueue, document, id, responseData);
                    return false;
                }
                prepareConditionalGet(url, document, id, responseData);
            } finally {
                if (responseData != null) {
                    CloseableUtil.closeQuietly(responseData);
                }
            }
        }
        return true;
    }

    /**
     * Handles an indexed document whose content has not changed, either because the Last-Modified of a
     * HEAD response is not newer than the indexed one, or because a conditional GET returned 304. The
     * response is processed as not modified, the anchors of the indexed document are queued, and the
     * expiration of the indexed document is extended.
     *
     * @param urlQueue the URL queue item of the document
     * @param document the indexed document
     * @param id the id of the indexed document
     * @param responseData the HEAD response, or the 304 response of the conditional GET
     */
    protected void handleUnchangedDocument(final UrlQueue<?> urlQueue, final Map<String, Object> document, final String id,
            final ResponseData responseData) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        log(logHelper, LogType.NOT_MODIFIED, crawlerContext, urlQueue);

        responseData.setParentUrl(urlQueue.getParentUrl());
        responseData.setSessionId(crawlerContext.getSessionId());
        responseData.setHttpStatusCode(Constants.NOT_MODIFIED_STATUS);
        processResponse(urlQueue, responseData);

        storeChildUrlsToQueue(urlQueue, getAnchorSet(document.get(fessConfig.getIndexFieldAnchor())));

        final CrawlingConfig crawlingConfig = ComponentUtil.getCrawlingConfigHelper().get(crawlerContext.getSessionId());
        final Date documentExpires = ComponentUtil.getCrawlingInfoHelper().getDocumentExpires(crawlingConfig);
        if (documentExpires != null
                && !ComponentUtil.getIndexingHelper()
                        .updateDocument(ComponentUtil.getSearchEngineClient(), id, fessConfig.getIndexFieldExpires(), documentExpires)
                && logger.isDebugEnabled()) {
            logger.debug("Failed to update field: field={}, url={}", fessConfig.getIndexFieldExpires(), urlQueue.getUrl());
        }
    }

    /**
     * Keeps a conditional GET for an http or https URL when the indexed document has a validator.
     *
     * @param url the URL to fetch
     * @param document the indexed document
     * @param id the id of the indexed document
     * @param headResponse the HEAD response, or null if no HEAD request was sent
     */
    private void prepareConditionalGet(final String url, final Map<String, Object> document, final String id,
            final ResponseData headResponse) {
        if (!url.startsWith("http:") && !url.startsWith("https:")) {
            return;
        }
        final ConditionalHeaders headers = resolveConditionalHeaders(document, headResponse);
        if (headers != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Sending a conditional GET: url={}, headers={}", url, headers);
            }
            conditionalGetState = new ConditionalGetState(url, document, id, headers);
        }
    }

    /**
     * Resolves the validators of a conditional GET for an indexed document. A conditional GET is used
     * when the HEAD comparison cannot decide: the document has no last_modified, the HEAD response has
     * no Last-Modified, or the HEAD status is neither 200 nor 404.
     *
     * @param document the indexed document
     * @param headResponse the HEAD response, or null if no HEAD request was sent
     * @return the validators to send, or null for a plain GET (the HEAD comparison decides, or the
     *         document has neither an ETag nor a last_modified)
     */
    protected ConditionalHeaders resolveConditionalHeaders(final Map<String, Object> document, final ResponseData headResponse) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Date lastModified = DocumentUtil.getValue(document, fessConfig.getIndexFieldLastModified(), Date.class);
        if (lastModified != null && headResponse != null && headResponse.getLastModified() != null) {
            final int httpStatusCode = headResponse.getHttpStatusCode();
            if (httpStatusCode == Constants.OK_STATUS_CODE || httpStatusCode == Constants.NOT_FOUND_STATUS_CODE) {
                return null;
            }
        }
        final String etag = DocumentUtil.getValue(document, fessConfig.getIndexFieldEtag(), String.class);
        final String ifNoneMatch = StringUtil.isNotBlank(etag) ? etag : null;
        final String ifModifiedSince = lastModified != null ? HTTP_DATE_FORMATTER.format(lastModified.toInstant()) : null;
        if (ifNoneMatch == null && ifModifiedSince == null) {
            return null;
        }
        return new ConditionalHeaders(ifNoneMatch, ifModifiedSince);
    }

    /**
     * Creates the GET request for the URL, adding If-None-Match and If-Modified-Since when a
     * conditional GET was prepared for this URL.
     *
     * @param urlQueue the URL queue item to fetch
     * @return the request data
     */
    @Override
    protected RequestData createRequestData(final UrlQueue<?> urlQueue) {
        final RequestData requestData = super.createRequestData(urlQueue);
        final ConditionalGetState state = conditionalGetState;
        if (state != null && state.url().equals(urlQueue.getUrl())) {
            final ConditionalHeaders headers = state.headers();
            if (headers.ifNoneMatch() != null) {
                requestData.addHeader("If-None-Match", headers.ifNoneMatch());
            }
            if (headers.ifModifiedSince() != null) {
                requestData.addHeader("If-Modified-Since", headers.ifModifiedSince());
            }
        }
        return requestData;
    }

    /**
     * Clears the pending conditional GET when the crawl of a URL ends, so that it is not kept when
     * the GET threw or redirected and {@link #processResponse(UrlQueue, ResponseData)} was not reached.
     */
    @Override
    protected void finishCrawling() {
        conditionalGetState = null;
        super.finishCrawling();
    }

    /**
     * Stores child URLs from the given set into the crawling queue for future processing.
     * The crawling depth is incremented by 1 from the parent URL's depth.
     *
     * @param urlQueue the parent URL queue item
     * @param childUrlSet the set of child URLs to be queued for crawling
     */
    protected void storeChildUrlsToQueue(final UrlQueue<?> urlQueue, final Set<RequestData> childUrlSet) {
        if (childUrlSet != null) {
            try {
                storeChildUrls(childUrlSet, urlQueue.getUrl(), urlQueue.getDepth() != null ? urlQueue.getDepth() + 1 : 1);
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
        if (obj == null) {
            return null;
        }

        List<String> anchorList;
        if (obj instanceof final String s) {
            anchorList = List.of(s);
        } else if (obj instanceof final List<?> l) {
            anchorList = l.stream().filter(item -> item != null).map(String::valueOf).toList();
        } else {
            return null;
        }

        if (anchorList.isEmpty()) {
            return null;
        }

        final Set<RequestData> childUrlSet = new LinkedHashSet<>();
        for (final String anchor : anchorList) {
            if (StringUtil.isNotBlank(anchor)) {
                childUrlSet.add(RequestDataBuilder.newRequestData().get().url(anchor).build());
            }
        }
        return childUrlSet.isEmpty() ? null : childUrlSet;
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
     * URL tracking when certain HTTP status codes are encountered. A 304 response to the conditional
     * GET prepared for the URL is handled by {@link #handleUnchangedDocument}.
     *
     * @param urlQueue the URL queue item that was processed
     * @param responseData the response data from the crawl operation
     */
    @Override
    protected void processResponse(final UrlQueue<?> urlQueue, final ResponseData responseData) {
        final ConditionalGetState state = conditionalGetState;
        if (state != null && state.url().equals(urlQueue.getUrl())) {
            conditionalGetState = null;
            if (responseData.getHttpStatusCode() == Constants.NOT_MODIFIED_STATUS_CODE) {
                handleUnchangedDocument(urlQueue, state.document(), state.id(), responseData);
                return;
            }
        }

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
     * The crawling sessions and client names already reported by {@link #getNamedClient}, as
     * {@code sessionId:clientName}. Static because a crawl runs one FessCrawlerThread instance per
     * thread, and the report is meant once per crawling configuration: the session id is the one
     * {@link CrawlingConfigHelper#store} gives each configuration of a crawl.
     */
    protected static final Set<String> missingClientReports = ConcurrentHashMap.newKeySet();

    /**
     * Retrieves the appropriate crawler client for the given URL based on configured rules.
     * This method uses client rules to determine which specific client implementation
     * should be used for crawling the URL, falling back to the default client if no
     * specific rule matches, or if the client a rule names is not registered.
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
        })
                .filter(StringUtil::isNotBlank)
                .findFirst()//
                .map(s -> getNamedClient(crawlingConfig, s, url))//
                .orElseGet(() -> clientFactory.getClient(url));
        if (logger.isDebugEnabled()) {
            logger.debug("CrawlerClient: class={}", client.getClass().getCanonicalName());
        }
        return client;
    }

    /**
     * Returns the client registered under {@code clientName} for the URL, or null, in which case
     * {@link #getClient} falls back to the client for the URL's protocol.
     *
     * <p>That fallback leaves no trace in the crawl: the page is fetched and indexed, just not by the
     * client the configuration named. With playwright that means no JavaScript is run, so text only
     * a script produces is missing from the index while the job succeeds. Up to 15.8 the playwright
     * client was bundled and always found; from 15.9 it comes from the fess-crawler-playwright plugin,
     * and an installation without it lands here. The warning is logged once per crawling
     * configuration and client name, not per URL.</p>
     *
     * @param crawlingConfig the crawling configuration whose rule named the client
     * @param clientName the client name from {@code client.crawlerClients}
     * @param url the URL to get a client for
     * @return the named client, or null if none is registered for the URL
     */
    protected CrawlerClient getNamedClient(final CrawlingConfig crawlingConfig, final String clientName, final String url) {
        final CrawlerClient client = clientFactory.getClient(clientName + ":" + url);
        if (client == null && missingClientReports.add(crawlerContext.getSessionId() + ":" + clientName)) {
            final String hint = "playwright".equals(clientName)
                    ? " If the fess-crawler-playwright plugin is not installed, install it and Node.js with bin/fess-setup install plugin fess-crawler-playwright and bin/fess-setup install nodejs, then restart Fess."
                    : " The plugin that provides this client may not be installed.";
            logger.warn(
                    "[{}] No crawler client {} is registered for {}, which client.crawlerClients assigns to it. It is crawled with the client for its protocol instead.{} Other URLs of this crawling configuration for {} are not reported.",
                    crawlingConfig.getName(), clientName, url, hint, clientName);
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
                }))
                .filter(Objects::nonNull)
                .toList());
    }
}
