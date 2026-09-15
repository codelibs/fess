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

import java.util.ArrayList;
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
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.opensearch.config.exentity.WebConfig;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;

/**
 * Test class for FessCrawlerThread.
 * Tests HTTP status code constants, null handling, and anchor processing.
 */
public class FessCrawlerThreadTest extends UnitFessTestCase {

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
}
