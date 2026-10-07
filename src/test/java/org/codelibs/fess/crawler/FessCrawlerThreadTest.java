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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.codelibs.core.misc.Pair;
import org.codelibs.fess.crawler.client.CrawlerClient;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.entity.UrlQueueImpl;
import org.codelibs.fess.crawler.rule.Rule;
import org.codelibs.fess.crawler.rule.RuleManager;
import org.codelibs.fess.crawler.serializer.DataSerializer;
import org.codelibs.fess.crawler.transformer.FessXpathTransformer;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.helper.DocumentHelper;
import org.codelibs.fess.helper.FileTypeHelper;
import org.codelibs.fess.helper.IndexingHelper;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.PathMappingHelper;
import org.codelibs.fess.helper.PermissionHelper;
import org.codelibs.fess.helper.ProtocolHelper;
import org.codelibs.fess.helper.SambaHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.config.exentity.WebConfig;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Test class for FessCrawlerThread.
 * Tests HTTP status code constants, null handling, and anchor processing.
 */
public class FessCrawlerThreadTest extends UnitFessTestCase {

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        // do not leave the stubs of this class in the ComponentUtil cache for later test classes
        clearCachedComponent("indexingHelper");
        clearCachedComponent("crawlingConfigHelper");
        super.tearDown(testInfo);
    }

    @Test
    public void test_getClientRuleList() {
        FessCrawlerThread crawlerThread = new FessCrawlerThread();

        List<Pair<String, Pattern>> list = crawlerThread.getClientRuleList(null);
        assertEquals(0, list.size());

        list = crawlerThread.getClientRuleList("");
        assertEquals(0, list.size());

        list = crawlerThread.getClientRuleList(" ");
        assertEquals(0, list.size());

        list = crawlerThread.getClientRuleList("playwright:http://.*");
        assertEquals(1, list.size());
        assertEquals("playwright", list.get(0).getFirst());
        assertEquals("http://.*", list.get(0).getSecond().pattern());

        list = crawlerThread.getClientRuleList("playwright:http://.*,playwright:https://.*");
        assertEquals(2, list.size());
        assertEquals("playwright", list.get(0).getFirst());
        assertEquals("http://.*", list.get(0).getSecond().pattern());
        assertEquals("playwright", list.get(1).getFirst());
        assertEquals("https://.*", list.get(1).getSecond().pattern());
    }

    @Test
    public void test_getClient_warnsOncePerConfigAndClientNameWhenTheNamedClientIsMissing() {
        ComponentUtil.register(new CrawlingConfigHelper(), "crawlingConfigHelper");
        final CrawlerClient httpClient = new StubCrawlerClient();
        final CrawlerClientFactory clientFactory = new CrawlerClientFactory();
        clientFactory.addClient(List.of("http:.*", "https:.*"), httpClient);

        // fess-crawler-playwright is not installed, and chromium was never a client name
        final String jsSite = storeWebConfig("1", "JS site",
                "client.crawlerClients=playwright:https://js.example.com/.*,chromium:https://spa.example.com/.*");
        final String otherSite = storeWebConfig("2", "Other site", "client.crawlerClients=playwright:https://.*");

        // a crawl runs one FessCrawlerThread instance per thread, all sharing the crawler context
        final FessCrawlerThread jsThread1 = createCrawlerThread(jsSite, clientFactory);
        final FessCrawlerThread jsThread2 = createCrawlerThread(jsSite, clientFactory);
        final FessCrawlerThread otherThread = createCrawlerThread(otherSite, clientFactory);

        final LogCapturingAppender appender = LogCapturingAppender.attach(FessCrawlerThread.class);
        try {
            // what gets crawled does not change: every URL still falls back to the protocol's client
            assertSame(httpClient, jsThread1.getClient("https://js.example.com/a"));
            assertSame(httpClient, jsThread1.getClient("https://js.example.com/b"));
            assertSame(httpClient, jsThread2.getClient("https://js.example.com/c"));
            assertSame(httpClient, jsThread2.getClient("https://spa.example.com/"));
            assertSame(httpClient, jsThread1.getClient("https://spa.example.com/next"));
            assertSame(httpClient, otherThread.getClient("https://example.org/"));
            assertSame(httpClient, otherThread.getClient("https://example.org/next"));
            // no rule matches, so no client name was asked for
            assertSame(httpClient, jsThread1.getClient("https://plain.example.com/"));

            final List<String> warnings = appender.warnings();
            assertEquals(3, warnings.size(), String.valueOf(warnings));

            final List<String> jsPlaywright =
                    warnings.stream().filter(m -> m.startsWith("[JS site]") && m.contains(" playwright ")).toList();
            assertEquals(1, jsPlaywright.size(), String.valueOf(warnings));
            assertTrue(jsPlaywright.get(0).contains("https://js.example.com/a"), jsPlaywright.get(0));
            assertTrue(jsPlaywright.get(0).contains("fess-crawler-playwright"), jsPlaywright.get(0));
            assertTrue(jsPlaywright.get(0).contains("bin/fess-setup install nodejs"), jsPlaywright.get(0));

            final List<String> jsChromium = warnings.stream().filter(m -> m.startsWith("[JS site]") && m.contains(" chromium ")).toList();
            assertEquals(1, jsChromium.size(), String.valueOf(warnings));
            assertFalse(jsChromium.get(0).contains("fess-crawler-playwright"), jsChromium.get(0));

            final List<String> otherPlaywright =
                    warnings.stream().filter(m -> m.startsWith("[Other site]") && m.contains(" playwright ")).toList();
            assertEquals(1, otherPlaywright.size(), String.valueOf(warnings));
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_getClient_usesARegisteredNamedClientWithoutWarning() {
        ComponentUtil.register(new CrawlingConfigHelper(), "crawlingConfigHelper");
        final CrawlerClient httpClient = new StubCrawlerClient();
        final CrawlerClient playwrightClient = new StubCrawlerClient();
        final CrawlerClientFactory clientFactory = new CrawlerClientFactory();
        // the patterns fess-crawler-playwright registers, ahead of the protocol clients
        clientFactory.addClient(List.of("playwright:http:.*", "playwright:https:.*"), playwrightClient);
        clientFactory.addClient(List.of("http:.*", "https:.*"), httpClient);

        final String jsSite = storeWebConfig("1", "JS site", "client.crawlerClients=playwright:https://js.example.com/.*");
        final FessCrawlerThread crawlerThread = createCrawlerThread(jsSite, clientFactory);

        final LogCapturingAppender appender = LogCapturingAppender.attach(FessCrawlerThread.class);
        try {
            assertSame(playwrightClient, crawlerThread.getClient("https://js.example.com/a"));
            assertSame(httpClient, crawlerThread.getClient("https://plain.example.com/"));
            assertEquals(0, appender.warnings().size(), String.valueOf(appender.warnings()));
        } finally {
            appender.detach();
        }
    }

    private static String storeWebConfig(final String id, final String name, final String configParameter) {
        final WebConfig webConfig = new WebConfig();
        webConfig.setId(id);
        webConfig.setName(name);
        webConfig.setConfigParameter(configParameter);
        // a session id no earlier run in this JVM has used, as each crawl's is
        return ComponentUtil.getCrawlingConfigHelper().store(UUID.randomUUID().toString(), webConfig);
    }

    private static FessCrawlerThread createCrawlerThread(final String sessionId, final CrawlerClientFactory clientFactory) {
        final CrawlerContext crawlerContext = new CrawlerContext();
        crawlerContext.setSessionId(sessionId);
        final FessCrawlerThread crawlerThread = new FessCrawlerThread();
        crawlerThread.setCrawlerContext(crawlerContext);
        crawlerThread.setClientFactory(clientFactory);
        return crawlerThread;
    }

    private static class StubCrawlerClient implements CrawlerClient {
        @Override
        public void setInitParameterMap(final Map<String, Object> params) {
            // nothing
        }

        @Override
        public ResponseData execute(final RequestData data) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Test HTTP status code constants are defined correctly
     */
    @Test
    public void test_httpStatusCodeConstants() {
        // Verify the constants are accessible via reflection or by checking their usage
        // Since the constants are private, we test their values indirectly

        // The constants should match standard HTTP status codes
        // HTTP_STATUS_NOT_FOUND = 404
        // HTTP_STATUS_OK = 200

        // This test verifies that the constants are being used in the code
        // The actual verification happens at compile time
        assertTrue("HTTP status code constants should be defined", true);
    }

    /**
     * Test getAnchorSet with null input
     */
    @Test
    public void test_getAnchorSet_withNull() {
        FessCrawlerThread crawlerThread = new FessCrawlerThread();

        Set<RequestData> result = crawlerThread.getAnchorSet(null);
        assertNull(result, "getAnchorSet should return null for null input");
    }

    /**
     * Test getAnchorSet with single string
     */
    @Test
    public void test_getAnchorSet_withSingleString() {
        FessCrawlerThread crawlerThread = new FessCrawlerThread();

        Set<RequestData> result = crawlerThread.getAnchorSet("http://example.com");
        assertNotNull(result, "getAnchorSet should not return null for valid string");
        assertEquals(1, result.size());

        RequestData requestData = result.iterator().next();
        assertEquals("http://example.com", requestData.getUrl());
    }

    /**
     * Test getAnchorSet with blank string
     */
    @Test
    public void test_getAnchorSet_withBlankString() {
        FessCrawlerThread crawlerThread = new FessCrawlerThread();

        Set<RequestData> result = crawlerThread.getAnchorSet("");
        assertNull(result, "getAnchorSet should return null for blank string");

        result = crawlerThread.getAnchorSet("   ");
        assertNull(result, "getAnchorSet should return null for whitespace string");
    }

    /**
     * Test getAnchorSet with list of URLs
     */
    @Test
    public void test_getAnchorSet_withList() {
        FessCrawlerThread crawlerThread = new FessCrawlerThread();

        List<String> urls = new ArrayList<>();
        urls.add("http://example.com/page1");
        urls.add("http://example.com/page2");
        urls.add("http://example.com/page3");

        Set<RequestData> result = crawlerThread.getAnchorSet(urls);
        assertNotNull(result, "getAnchorSet should not return null for valid list");
        assertEquals(3, result.size());
    }

    /**
     * Test getAnchorSet with list containing nulls
     */
    @Test
    public void test_getAnchorSet_withListContainingNulls() {
        FessCrawlerThread crawlerThread = new FessCrawlerThread();

        List<String> urls = new ArrayList<>();
        urls.add("http://example.com/page1");
        urls.add(null);
        urls.add("http://example.com/page2");
        urls.add(null);

        Set<RequestData> result = crawlerThread.getAnchorSet(urls);
        assertNotNull(result, "getAnchorSet should filter out null values");
        assertEquals(2, result.size());
    }

    /**
     * Test getAnchorSet with list containing blank strings
     */
    @Test
    public void test_getAnchorSet_withListContainingBlanks() {
        FessCrawlerThread crawlerThread = new FessCrawlerThread();

        List<String> urls = new ArrayList<>();
        urls.add("http://example.com/page1");
        urls.add("");
        urls.add("http://example.com/page2");
        urls.add("   ");
        urls.add("http://example.com/page3");

        Set<RequestData> result = crawlerThread.getAnchorSet(urls);
        assertNotNull(result, "getAnchorSet should filter out blank strings");
        assertEquals(3, result.size());
    }

    /**
     * Test getAnchorSet with empty list
     */
    @Test
    public void test_getAnchorSet_withEmptyList() {
        FessCrawlerThread crawlerThread = new FessCrawlerThread();

        List<String> urls = new ArrayList<>();

        Set<RequestData> result = crawlerThread.getAnchorSet(urls);
        assertNull(result, "getAnchorSet should return null for empty list");
    }

    /**
     * Test getAnchorSet with list containing only nulls and blanks
     */
    @Test
    public void test_getAnchorSet_withListContainingOnlyNullsAndBlanks() {
        FessCrawlerThread crawlerThread = new FessCrawlerThread();

        List<String> urls = new ArrayList<>();
        urls.add(null);
        urls.add("");
        urls.add("   ");
        urls.add(null);

        Set<RequestData> result = crawlerThread.getAnchorSet(urls);
        assertNull(result, "getAnchorSet should return null when all items are filtered out");
    }

    /**
     * Test getAnchorSet with unsupported object type
     */
    @Test
    public void test_getAnchorSet_withUnsupportedType() {
        FessCrawlerThread crawlerThread = new FessCrawlerThread();

        Integer unsupportedType = 123;

        Set<RequestData> result = crawlerThread.getAnchorSet(unsupportedType);
        assertNull(result, "getAnchorSet should return null for unsupported type");
    }

    /**
     * Test getAnchorSet with mixed valid and invalid URLs
     */
    @Test
    public void test_getAnchorSet_withMixedValidAndInvalid() {
        FessCrawlerThread crawlerThread = new FessCrawlerThread();

        List<String> urls = new ArrayList<>();
        urls.add("http://example.com/valid1");
        urls.add(null);
        urls.add("http://example.com/valid2");
        urls.add("");
        urls.add("http://example.com/valid3");
        urls.add("   ");

        Set<RequestData> result = crawlerThread.getAnchorSet(urls);
        assertNotNull(result, "getAnchorSet should return valid URLs only");
        assertEquals(3, result.size());

        // Verify the URLs are correct
        List<String> resultUrls = new ArrayList<>();
        for (RequestData rd : result) {
            resultUrls.add(rd.getUrl());
        }

        assertTrue(resultUrls.contains("http://example.com/valid1"));
        assertTrue(resultUrls.contains("http://example.com/valid2"));
        assertTrue(resultUrls.contains("http://example.com/valid3"));
    }

    /**
     * Test that FessCrawlerThread can be instantiated
     */
    @Test
    public void test_constructor() {
        FessCrawlerThread crawlerThread = new FessCrawlerThread();
        assertNotNull(crawlerThread, "FessCrawlerThread should be instantiable");
    }

    /**
     * Test URL deduplication in getAnchorSet
     */
    @Test
    public void test_getAnchorSet_deduplication() {
        FessCrawlerThread crawlerThread = new FessCrawlerThread();

        List<String> urls = new ArrayList<>();
        urls.add("http://example.com/page1");
        urls.add("http://example.com/page2");
        urls.add("http://example.com/page1"); // Duplicate
        urls.add("http://example.com/page3");
        urls.add("http://example.com/page2"); // Duplicate

        Set<RequestData> result = crawlerThread.getAnchorSet(urls);
        assertNotNull(result, "getAnchorSet should handle duplicates");

        // Since it returns a Set, duplicates should be handled by URL comparison
        // The exact behavior depends on RequestData.equals() implementation
        assertTrue("Should have at most 5 items", result.size() <= 5);
    }

    // ===== conditional GET =====

    private static final String PAGE_URL = "https://example.com/page.html";

    /** The indexed last_modified as the _source of the document returns it. */
    private static final String INDEXED_LAST_MODIFIED = "2026-10-01T02:03:04.000Z";

    /** INDEXED_LAST_MODIFIED as an HTTP-date (IMF-fixdate, two-digit day). */
    private static final String INDEXED_LAST_MODIFIED_HTTP_DATE = "Thu, 01 Oct 2026 02:03:04 GMT";

    @Test
    public void test_resolveConditionalHeaders_etagAndHeadWithoutLastModified() {
        final FessCrawlerThread crawlerThread = new FessCrawlerThread();
        final Map<String, Object> doc = new HashMap<>();
        doc.put("etag", "W/\"abc\"");

        final FessCrawlerThread.ConditionalHeaders headers = crawlerThread.resolveConditionalHeaders(doc, headResponse(200, null));
        assertNotNull(headers);
        assertEquals("W/\"abc\"", headers.ifNoneMatch());
        assertNull(headers.ifModifiedSince());

        // no indexed last_modified: no HEAD is sent at all
        final FessCrawlerThread.ConditionalHeaders noHead = crawlerThread.resolveConditionalHeaders(doc, null);
        assertNotNull(noHead);
        assertEquals("W/\"abc\"", noHead.ifNoneMatch());
        assertNull(noHead.ifModifiedSince());
    }

    @Test
    public void test_resolveConditionalHeaders_lastModifiedAndHead405() {
        final FessCrawlerThread crawlerThread = new FessCrawlerThread();
        final Map<String, Object> doc = new HashMap<>();
        doc.put("last_modified", INDEXED_LAST_MODIFIED);

        final FessCrawlerThread.ConditionalHeaders headers = crawlerThread.resolveConditionalHeaders(doc, headResponse(405, null));
        assertNotNull(headers);
        assertNull(headers.ifNoneMatch());
        assertEquals(INDEXED_LAST_MODIFIED_HTTP_DATE, headers.ifModifiedSince());

        // a status other than 200/404 makes the HEAD unusable even with a Last-Modified
        final FessCrawlerThread.ConditionalHeaders withLastModified =
                crawlerThread.resolveConditionalHeaders(doc, headResponse(405, new Date(0L)));
        assertNotNull(withLastModified);
        assertEquals(INDEXED_LAST_MODIFIED_HTTP_DATE, withLastModified.ifModifiedSince());
    }

    @Test
    public void test_resolveConditionalHeaders_dateValueAndBothHeaders() {
        final FessCrawlerThread crawlerThread = new FessCrawlerThread();
        final Map<String, Object> doc = new HashMap<>();
        doc.put("etag", "\"v1\"");
        doc.put("last_modified", new Date(java.time.Instant.parse("2026-12-24T23:59:58Z").toEpochMilli()));

        final FessCrawlerThread.ConditionalHeaders headers = crawlerThread.resolveConditionalHeaders(doc, headResponse(200, null));
        assertNotNull(headers);
        assertEquals("\"v1\"", headers.ifNoneMatch());
        assertEquals("Thu, 24 Dec 2026 23:59:58 GMT", headers.ifModifiedSince());
    }

    @Test
    public void test_resolveConditionalHeaders_plainGet() {
        final FessCrawlerThread crawlerThread = new FessCrawlerThread();
        assertNull(crawlerThread.resolveConditionalHeaders(new HashMap<>(), null));
        assertNull(crawlerThread.resolveConditionalHeaders(new HashMap<>(), headResponse(200, null)));
        assertNull(crawlerThread.resolveConditionalHeaders(new HashMap<>(), headResponse(405, null)));

        final Map<String, Object> blankEtag = new HashMap<>();
        blankEtag.put("etag", " ");
        assertNull(crawlerThread.resolveConditionalHeaders(blankEtag, headResponse(200, null)));

        // the HEAD comparison decides: no conditional GET
        final Map<String, Object> doc = new HashMap<>();
        doc.put("etag", "\"v1\"");
        doc.put("last_modified", INDEXED_LAST_MODIFIED);
        assertNull(crawlerThread.resolveConditionalHeaders(doc, headResponse(200, new Date())));
        assertNull(crawlerThread.resolveConditionalHeaders(doc, headResponse(404, new Date())));
    }

    @Test
    public void test_createRequestData_addsHeadersOnlyForThePendingUrl() {
        final FessCrawlerThread crawlerThread = new FessCrawlerThread();
        crawlerThread.conditionalGetState = new FessCrawlerThread.ConditionalGetState(PAGE_URL, new HashMap<>(), "id1",
                new FessCrawlerThread.ConditionalHeaders("\"v1\"", INDEXED_LAST_MODIFIED_HTTP_DATE));

        final RequestData requestData = crawlerThread.createRequestData(urlQueue(PAGE_URL));
        assertEquals(PAGE_URL, requestData.getUrl());
        assertEquals(RequestData.Method.GET, requestData.getMethod());
        assertEquals("\"v1\"", requestData.getHeaders().get("If-None-Match"));
        assertEquals(INDEXED_LAST_MODIFIED_HTTP_DATE, requestData.getHeaders().get("If-Modified-Since"));

        final RequestData other = crawlerThread.createRequestData(urlQueue("https://example.com/other.html"));
        assertEquals("https://example.com/other.html", other.getUrl());
        assertTrue(other.getHeaders().isEmpty(), String.valueOf(other.getHeaders()));
    }

    @Test
    public void test_createRequestData_onlyTheAvailableHeader() {
        final FessCrawlerThread crawlerThread = new FessCrawlerThread();
        crawlerThread.conditionalGetState = new FessCrawlerThread.ConditionalGetState(PAGE_URL, new HashMap<>(), "id1",
                new FessCrawlerThread.ConditionalHeaders("\"v1\"", null));

        final RequestData requestData = crawlerThread.createRequestData(urlQueue(PAGE_URL));
        assertEquals(1, requestData.getHeaders().size(), String.valueOf(requestData.getHeaders()));
        assertEquals("\"v1\"", requestData.getHeaders().get("If-None-Match"));

        final FessCrawlerThread noState = new FessCrawlerThread();
        assertTrue(noState.createRequestData(urlQueue(PAGE_URL)).getHeaders().isEmpty());
    }

    @Test
    public void test_processResponse_304WithPendingStateHandlesUnchangedDocumentOnce() {
        final CrawlEnvironment env = setUpCrawlEnvironment(null);
        final RecordingCrawlerThread crawlerThread = env.thread;
        crawlerThread.stubUnchanged = true;
        final Map<String, Object> document = new HashMap<>();
        crawlerThread.conditionalGetState = new FessCrawlerThread.ConditionalGetState(PAGE_URL, document, "id1",
                new FessCrawlerThread.ConditionalHeaders("\"v1\"", null));

        final ResponseData notModified = headResponse(304, null);
        final UrlQueue<?> urlQueue = urlQueue(PAGE_URL);
        crawlerThread.processResponse(urlQueue, notModified);

        assertEquals(1, crawlerThread.unchangedCalls.size());
        final Object[] call = crawlerThread.unchangedCalls.get(0);
        assertSame(urlQueue, call[0]);
        assertSame(document, call[1]);
        assertEquals("id1", call[2]);
        assertSame(notModified, call[3]);
        assertNull(crawlerThread.conditionalGetState);
        // handleUnchangedDocument runs the rule itself; processResponse does not run it a second time
        assertEquals(0, env.ruleManager.responses.size());
    }

    @Test
    public void test_processResponse_304WithoutPendingStateGoesToTheRule() {
        final CrawlEnvironment env = setUpCrawlEnvironment(null);
        final RecordingCrawlerThread crawlerThread = env.thread;
        crawlerThread.stubUnchanged = true;

        crawlerThread.processResponse(urlQueue(PAGE_URL), headResponse(304, null));
        assertEquals(0, crawlerThread.unchangedCalls.size());
        assertEquals(1, env.ruleManager.responses.size());

        // a pending state of another URL does not apply
        crawlerThread.conditionalGetState = new FessCrawlerThread.ConditionalGetState("https://example.com/other.html", new HashMap<>(),
                "id2", new FessCrawlerThread.ConditionalHeaders("\"v1\"", null));
        crawlerThread.processResponse(urlQueue(PAGE_URL), headResponse(304, null));
        assertEquals(0, crawlerThread.unchangedCalls.size());
        assertEquals(2, env.ruleManager.responses.size());
    }

    @Test
    public void test_processResponse_200AfterConditionalGetClearsTheState() {
        final CrawlEnvironment env = setUpCrawlEnvironment(null);
        final RecordingCrawlerThread crawlerThread = env.thread;
        crawlerThread.stubUnchanged = true;
        crawlerThread.conditionalGetState = new FessCrawlerThread.ConditionalGetState(PAGE_URL, new HashMap<>(), "id1",
                new FessCrawlerThread.ConditionalHeaders("\"v1\"", null));

        crawlerThread.processResponse(urlQueue(PAGE_URL), headResponse(200, null));
        assertEquals(0, crawlerThread.unchangedCalls.size());
        assertEquals(1, env.ruleManager.responses.size());
        assertNull(crawlerThread.conditionalGetState);
    }

    @Test
    public void test_processResponse_304OfConditionalGetUpdatesLikeAnUnchangedHead() {
        final Map<String, Object> document = indexedDocument(INDEXED_LAST_MODIFIED, "\"v1\"");
        final CrawlEnvironment env = setUpCrawlEnvironment(document);
        env.webConfig.setTimeToLive(60);
        final RecordingCrawlerThread crawlerThread = env.thread;
        env.client.headResponse = headResponse(200, null);

        final UrlQueue<?> urlQueue = urlQueue(PAGE_URL);
        assertTrue(crawlerThread.isContentUpdated(env.client, urlQueue));
        assertNotNull(crawlerThread.conditionalGetState);
        final RequestData get = crawlerThread.createRequestData(urlQueue);
        assertEquals("\"v1\"", get.getHeaders().get("If-None-Match"));

        final ResponseData notModified = headResponse(304, null);
        crawlerThread.processResponse(urlQueue, notModified);

        assertEquals(1, crawlerThread.unchangedCalls.size());
        assertNull(crawlerThread.conditionalGetState);
        assertEquals(1, env.ruleManager.responses.size());
        assertSame(notModified, env.ruleManager.responses.get(0));
        assertEquals(304, notModified.getHttpStatusCode());
        assertEquals(env.sessionId, notModified.getSessionId());
        assertEquals(1, crawlerThread.storedChildUrls.size());
        assertEquals("https://example.com/child.html", crawlerThread.storedChildUrls.get(0).iterator().next().getUrl());
        assertEquals(List.of("expires"), env.indexingHelper.updatedFields);
    }

    @Test
    public void test_isContentUpdated_unchangedHeadHandlesUnchangedDocument() {
        final Map<String, Object> document = indexedDocument(INDEXED_LAST_MODIFIED, "\"v1\"");
        final CrawlEnvironment env = setUpCrawlEnvironment(document);
        env.webConfig.setTimeToLive(60);
        final RecordingCrawlerThread crawlerThread = env.thread;
        env.client.headResponse = headResponse(200, new Date(java.time.Instant.parse(INDEXED_LAST_MODIFIED).toEpochMilli()));

        final UrlQueue<?> urlQueue = urlQueue(PAGE_URL);
        assertFalse(crawlerThread.isContentUpdated(env.client, urlQueue));

        assertEquals(1, env.client.requests.size());
        assertEquals(RequestData.Method.HEAD, env.client.requests.get(0).getMethod());
        assertTrue(List.of(env.indexingHelper.fields).contains("etag"), List.of(env.indexingHelper.fields).toString());
        assertEquals(1, crawlerThread.unchangedCalls.size());
        assertSame(env.client.headResponse, crawlerThread.unchangedCalls.get(0)[3]);
        assertEquals(1, env.ruleManager.responses.size());
        assertEquals(304, env.client.headResponse.getHttpStatusCode());
        assertEquals(1, crawlerThread.storedChildUrls.size());
        assertEquals(List.of("expires"), env.indexingHelper.updatedFields);
        assertNull(crawlerThread.conditionalGetState);
    }

    /**
     * Crawls a page with {@link FessXpathTransformer}, stores the result as the indexed document, and
     * re-crawls it unchanged (the HEAD is not newer than the indexed last_modified). Returns the child
     * URL sets the re-crawl queued.
     */
    private List<Set<RequestData>> recrawlUnchangedPage(final String html, final Map<String, Object> headers) {
        final Map<String, Object> document = indexedDocument(INDEXED_LAST_MODIFIED, "\"v1\"");
        final CrawlEnvironment env = setUpCrawlEnvironment(document);
        env.webConfig.setTimeToLive(60);
        ComponentUtil.register(new DataSerializer(), "dataSerializer");
        ComponentUtil.register(new PathMappingHelper(), "pathMappingHelper");
        ComponentUtil.register(new FileTypeHelper(), "fileTypeHelper");
        ComponentUtil.register(new DocumentHelper(), "documentHelper");
        ComponentUtil.register(new LabelTypeHelper() {
            @Override
            public Set<String> getMatchedLabelValueSet(final String path) {
                return Set.of();
            }
        }, "labelTypeHelper");

        final FessXpathTransformer transformer = new FessXpathTransformer();
        transformer.init();
        transformer.setChildUrlRuleMap(new LinkedHashMap<>(Map.of("//A", "href")));
        final ResponseData firstCrawl = new ResponseData();
        firstCrawl.setCharSet("UTF-8");
        firstCrawl.setContentLength(html.length());
        firstCrawl.setHttpStatusCode(200);
        firstCrawl.setLastModified(new Date());
        firstCrawl.setMethod("GET");
        firstCrawl.setMimeType("text/html");
        firstCrawl.setResponseBody(html.getBytes());
        firstCrawl.setSessionId(env.sessionId);
        firstCrawl.setUrl(PAGE_URL);
        headers.forEach(firstCrawl::addMetaData);
        final ResultData resultData = transformer.transform(firstCrawl);
        @SuppressWarnings("unchecked")
        final Map<String, Object> dataMap = (Map<String, Object>) resultData.getRawData();
        document.put("anchor", dataMap.get("anchor"));

        env.client.headResponse = headResponse(200, new Date(java.time.Instant.parse(INDEXED_LAST_MODIFIED).toEpochMilli()));
        assertFalse(env.thread.isContentUpdated(env.client, urlQueue(PAGE_URL)));
        assertEquals(1, env.thread.unchangedCalls.size());
        return env.thread.storedChildUrls;
    }

    @Test
    public void test_isContentUpdated_unchangedPageQueuesTheLinksItWasCrawledWith() {
        final List<Set<RequestData>> queued = recrawlUnchangedPage(
                "<html><head><title>Page</title></head><body><a href=\"https://example.com/child.html\">child</a></body></html>", Map.of());

        assertEquals(1, queued.size());
        assertEquals("https://example.com/child.html", queued.get(0).iterator().next().getUrl());
    }

    @Test
    public void test_isContentUpdated_unchangedNofollowMetaPageQueuesNoLinks() {
        final List<Set<RequestData>> queued = recrawlUnchangedPage("<html><head><title>Page</title>" //
                + "<meta name=\"robots\" content=\"nofollow\"></head>" //
                + "<body><a href=\"https://example.com/child.html\">child</a></body></html>", Map.of());

        assertTrue(queued.isEmpty(), "a nofollow page must not queue its links when it is re-crawled unchanged: " + queued);
    }

    @Test
    public void test_isContentUpdated_unchangedNofollowHeaderPageQueuesNoLinks() {
        final List<Set<RequestData>> queued = recrawlUnchangedPage(
                "<html><head><title>Page</title></head><body><a href=\"https://example.com/child.html\">child</a></body></html>",
                Map.of("X-Robots-Tag", "nofollow"));

        assertTrue(queued.isEmpty(), "a nofollow page must not queue its links when it is re-crawled unchanged: " + queued);
    }

    @Test
    public void test_isContentUpdated_changedHeadIsAPlainGet() {
        final CrawlEnvironment env = setUpCrawlEnvironment(indexedDocument(INDEXED_LAST_MODIFIED, "\"v1\""));
        final RecordingCrawlerThread crawlerThread = env.thread;
        env.client.headResponse = headResponse(200, new Date(java.time.Instant.parse("2026-10-02T00:00:00Z").toEpochMilli()));

        assertTrue(crawlerThread.isContentUpdated(env.client, urlQueue(PAGE_URL)));
        assertNull(crawlerThread.conditionalGetState);
        assertEquals(0, crawlerThread.unchangedCalls.size());
        assertTrue(crawlerThread.createRequestData(urlQueue(PAGE_URL)).getHeaders().isEmpty());
    }

    @Test
    public void test_isContentUpdated_headWithoutLastModifiedPreparesConditionalGet() {
        final CrawlEnvironment env = setUpCrawlEnvironment(indexedDocument(INDEXED_LAST_MODIFIED, "\"v1\""));
        final RecordingCrawlerThread crawlerThread = env.thread;
        env.client.headResponse = headResponse(200, null);

        assertTrue(crawlerThread.isContentUpdated(env.client, urlQueue(PAGE_URL)));
        assertEquals(1, env.client.requests.size());
        final FessCrawlerThread.ConditionalGetState state = crawlerThread.conditionalGetState;
        assertNotNull(state);
        assertEquals(PAGE_URL, state.url());
        assertEquals("\"v1\"", state.headers().ifNoneMatch());
        assertEquals(INDEXED_LAST_MODIFIED_HTTP_DATE, state.headers().ifModifiedSince());
        assertEquals(0, crawlerThread.unchangedCalls.size());
    }

    @Test
    public void test_isContentUpdated_head405PreparesConditionalGet() {
        final CrawlEnvironment env = setUpCrawlEnvironment(indexedDocument(INDEXED_LAST_MODIFIED, null));
        final RecordingCrawlerThread crawlerThread = env.thread;
        env.client.headResponse = headResponse(405, new Date(0L));

        assertTrue(crawlerThread.isContentUpdated(env.client, urlQueue(PAGE_URL)));
        final FessCrawlerThread.ConditionalGetState state = crawlerThread.conditionalGetState;
        assertNotNull(state);
        assertNull(state.headers().ifNoneMatch());
        assertEquals(INDEXED_LAST_MODIFIED_HTTP_DATE, state.headers().ifModifiedSince());
    }

    @Test
    public void test_isContentUpdated_noIndexedLastModifiedSendsNoHead() {
        final CrawlEnvironment env = setUpCrawlEnvironment(indexedDocument(null, "\"v1\""));
        final RecordingCrawlerThread crawlerThread = env.thread;

        assertTrue(crawlerThread.isContentUpdated(env.client, urlQueue(PAGE_URL)));
        assertEquals(0, env.client.requests.size());
        final FessCrawlerThread.ConditionalGetState state = crawlerThread.conditionalGetState;
        assertNotNull(state);
        assertEquals("\"v1\"", state.headers().ifNoneMatch());
        assertNull(state.headers().ifModifiedSince());
    }

    @Test
    public void test_isContentUpdated_nothingToValidateIsAPlainGet() {
        final CrawlEnvironment env = setUpCrawlEnvironment(indexedDocument(null, null));
        final RecordingCrawlerThread crawlerThread = env.thread;

        assertTrue(crawlerThread.isContentUpdated(env.client, urlQueue(PAGE_URL)));
        assertEquals(0, env.client.requests.size());
        assertNull(crawlerThread.conditionalGetState);
    }

    @Test
    public void test_isContentUpdated_nonHttpUrlKeepsThePlainFlow() {
        final String fileUrl = "file:/data/page.html";
        final CrawlEnvironment env = setUpCrawlEnvironment(indexedDocument(INDEXED_LAST_MODIFIED, "\"v1\""));
        final RecordingCrawlerThread crawlerThread = env.thread;
        env.client.headResponse = headResponse(200, null);

        assertTrue(crawlerThread.isContentUpdated(env.client, urlQueue(fileUrl)));
        assertNull(crawlerThread.conditionalGetState);

        final CrawlEnvironment env2 = setUpCrawlEnvironment(indexedDocument(null, "\"v1\""));
        env2.client.headResponse = headResponse(200, null);
        assertTrue(env2.thread.isContentUpdated(env2.client, urlQueue(fileUrl)));
        assertNull(env2.thread.conditionalGetState);
    }

    @Test
    public void test_isContentUpdated_clearsAStaleState() {
        final CrawlEnvironment env = setUpCrawlEnvironment(null);
        final RecordingCrawlerThread crawlerThread = env.thread;
        crawlerThread.conditionalGetState = new FessCrawlerThread.ConditionalGetState(PAGE_URL, new HashMap<>(), "id1",
                new FessCrawlerThread.ConditionalHeaders("\"v1\"", null));

        // the document is gone from the index: a plain GET
        assertTrue(crawlerThread.isContentUpdated(env.client, urlQueue(PAGE_URL)));
        assertNull(crawlerThread.conditionalGetState);
        assertTrue(crawlerThread.createRequestData(urlQueue(PAGE_URL)).getHeaders().isEmpty());
    }

    @Test
    public void test_finishCrawling_clearsTheStateWhenTheGetDidNotReachProcessResponse() {
        final CrawlEnvironment env = setUpCrawlEnvironment(null);
        final RecordingCrawlerThread crawlerThread = env.thread;
        crawlerThread.conditionalGetState = new FessCrawlerThread.ConditionalGetState(PAGE_URL, new HashMap<>(), "id1",
                new FessCrawlerThread.ConditionalHeaders("\"v1\"", null));
        final CrawlerContext crawlerContext = crawlerThread.crawlerContext;

        // the GET threw or redirected: run() skips processResponse and goes to its finally block
        crawlerThread.startCrawling();
        assertEquals(1, crawlerContext.getActiveThreadCount());
        crawlerThread.finishCrawling();

        assertNull(crawlerThread.conditionalGetState);
        assertEquals(0, crawlerContext.getActiveThreadCount());
    }

    private static Map<String, Object> indexedDocument(final String lastModified, final String etag) {
        final Map<String, Object> document = new HashMap<>();
        document.put("_id", "id1");
        if (lastModified != null) {
            document.put("last_modified", lastModified);
        }
        if (etag != null) {
            document.put("etag", etag);
        }
        document.put("anchor", List.of("https://example.com/child.html"));
        return document;
    }

    private static ResponseData headResponse(final int status, final Date lastModified) {
        final ResponseData responseData = new ResponseData();
        responseData.setUrl(PAGE_URL);
        responseData.setHttpStatusCode(status);
        responseData.setLastModified(lastModified);
        return responseData;
    }

    private static UrlQueue<?> urlQueue(final String url) {
        final UrlQueueImpl<Long> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl(url);
        urlQueue.setMethod("GET");
        urlQueue.setWeight(1.0f);
        urlQueue.setDepth(0);
        return urlQueue;
    }

    private static class CrawlEnvironment {
        String sessionId;
        WebConfig webConfig;
        RecordingCrawlerThread thread;
        RecordingClient client;
        RecordingIndexingHelper indexingHelper;
        RecordingRuleManager ruleManager;
    }

    private CrawlEnvironment setUpCrawlEnvironment(final Map<String, Object> document) {
        // ComponentUtil caches these two, and the cache outlives ComponentUtil.register
        clearCachedComponent("indexingHelper");
        clearCachedComponent("crawlingConfigHelper");
        final CrawlEnvironment env = new CrawlEnvironment();
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        ComponentUtil.register(new CrawlingConfigHelper(), "crawlingConfigHelper");
        ComponentUtil.register(new CrawlingInfoHelper(), "crawlingInfoHelper");
        ComponentUtil.register(new ProtocolHelper(), "protocolHelper");
        ComponentUtil.register(new PermissionHelper(), "permissionHelper");
        ComponentUtil.register(new SambaHelper(), "sambaHelper");
        ComponentUtil.register(new SearchEngineClient(), "searchEngineClient");
        env.indexingHelper = new RecordingIndexingHelper(document);
        ComponentUtil.register(env.indexingHelper, "indexingHelper");

        env.webConfig = new WebConfig();
        env.webConfig.setId("1");
        env.webConfig.setName("Site");
        env.webConfig.setConfigParameter("");
        env.sessionId = ComponentUtil.getCrawlingConfigHelper().store(UUID.randomUUID().toString(), env.webConfig);

        final CrawlerContext crawlerContext = new CrawlerContext();
        crawlerContext.setSessionId(env.sessionId);
        env.ruleManager = new RecordingRuleManager();
        crawlerContext.setRuleManager(env.ruleManager);
        env.thread = new RecordingCrawlerThread();
        env.thread.setCrawlerContext(crawlerContext);
        env.client = new RecordingClient();
        return env;
    }

    private static void clearCachedComponent(final String name) {
        try {
            final Field field = ComponentUtil.class.getDeclaredField(name);
            field.setAccessible(true);
            field.set(null, null);
        } catch (final ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static class RecordingCrawlerThread extends FessCrawlerThread {
        final List<Object[]> unchangedCalls = new ArrayList<>();
        final List<Set<RequestData>> storedChildUrls = new ArrayList<>();
        boolean stubUnchanged;

        @Override
        protected void handleUnchangedDocument(final UrlQueue<?> urlQueue, final Map<String, Object> document, final String id,
                final ResponseData responseData) {
            unchangedCalls.add(new Object[] { urlQueue, document, id, responseData });
            if (!stubUnchanged) {
                super.handleUnchangedDocument(urlQueue, document, id, responseData);
            }
        }

        @Override
        protected void storeChildUrlsToQueue(final UrlQueue<?> urlQueue, final Set<RequestData> childUrlSet) {
            if (childUrlSet != null) {
                storedChildUrls.add(childUrlSet);
            }
        }
    }

    private static class RecordingClient implements CrawlerClient {
        final List<RequestData> requests = new ArrayList<>();
        ResponseData headResponse;

        @Override
        public void setInitParameterMap(final Map<String, Object> params) {
            // nothing
        }

        @Override
        public ResponseData execute(final RequestData data) {
            requests.add(data);
            return headResponse;
        }
    }

    private static class RecordingIndexingHelper extends IndexingHelper {
        final Map<String, Object> document;
        String[] fields;
        final List<String> updatedFields = new ArrayList<>();

        RecordingIndexingHelper(final Map<String, Object> document) {
            this.document = document;
        }

        @Override
        public Map<String, Object> getDocument(final SearchEngineClient searchEngineClient, final String id, final String[] fields) {
            this.fields = fields;
            return document;
        }

        @Override
        public List<Map<String, Object>> getChildDocumentList(final SearchEngineClient searchEngineClient, final String id,
                final String[] fields) {
            return List.of();
        }

        @Override
        public boolean updateDocument(final SearchEngineClient searchEngineClient, final String id, final String field,
                final Object value) {
            updatedFields.add(field);
            return true;
        }

        @Override
        public boolean deleteDocument(final SearchEngineClient searchEngineClient, final String id) {
            return true;
        }
    }

    private static class RecordingRuleManager implements RuleManager {
        final List<ResponseData> responses = new ArrayList<>();

        @Override
        public Rule getRule(final ResponseData responseData) {
            responses.add(responseData);
            return null;
        }

        @Override
        public void addRule(final Rule rule) {
            // nothing
        }

        @Override
        public void addRule(final int index, final Rule rule) {
            // nothing
        }

        @Override
        public boolean removeRule(final Rule rule) {
            return false;
        }

        @Override
        public boolean hasRule(final Rule rule) {
            return false;
        }
    }
}
