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
package org.codelibs.fess.opensearch.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fesen.client.EngineInfo;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.BooleanFunction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.opensearch.action.search.SearchAction;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.common.xcontent.json.JsonXContent;
import org.opensearch.core.xcontent.DeprecationHandler;
import org.opensearch.core.xcontent.NamedXContentRegistry;
import org.opensearch.core.xcontent.XContentParser;

public class SearchEngineClientTest extends UnitFessTestCase {

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        super.tearDown(testInfo);
    }

    // Basic test to verify test framework is working
    @Test
    public void test_basicAssertion() {
        assertTrue(true);
        assertFalse(false);
        assertNotNull("test");
        assertEquals(1, 1);
    }

    // Test placeholder for future implementation
    @Test
    public void test_placeholder() {
        // This test verifies the test class can be instantiated and run
        String testValue = "test";
        assertNotNull(testValue);
        assertEquals("test", testValue);
    }

    /**
     * A client reporting the given backend, recording whether the unsupported-engine report fired.
     */
    private SearchEngineClient clientReporting(final String distribution, final String number, final AtomicInteger reports) {
        return new SearchEngineClient() {
            @Override
            public EngineInfo getEngineInfo() {
                return new EngineInfo(Map.of("version", Map.of("number", number, "distribution", distribution)));
            }

            @Override
            protected void reportUnsupportedEngine(final EngineInfo.EngineType engineType) {
                reports.incrementAndGet();
            }
        };
    }

    /**
     * OpenSearch 3.x is what Fess needs, so startup must stay quiet.
     */
    @Test
    public void test_warnUnlessOpenSearch3_quietOnOpenSearch3() {
        final AtomicInteger reports = new AtomicInteger(0);
        clientReporting("opensearch", "3.8.0", reports).warnUnlessOpenSearch3();
        assertEquals(0, reports.get());
    }

    /**
     * OpenSearch 2.x cannot sort by _shard_doc, so the mismatch must be reported.
     */
    @Test
    public void test_warnUnlessOpenSearch3_reportsOpenSearch2() {
        final AtomicInteger reports = new AtomicInteger(0);
        clientReporting("opensearch", "2.19.4", reports).warnUnlessOpenSearch3();
        assertEquals(1, reports.get());
    }

    /**
     * Elasticsearch has no _shard_doc sort either, so it is reported the same way.
     */
    @Test
    public void test_warnUnlessOpenSearch3_reportsElasticsearch() {
        final AtomicInteger reports = new AtomicInteger(0);
        clientReporting("elasticsearch", "8.11.0", reports).warnUnlessOpenSearch3();
        assertEquals(1, reports.get());
    }

    /**
     * A client that cannot report its engine must not break startup.
     */
    @Test
    public void test_warnUnlessOpenSearch3_survivesUndetectableEngine() {
        final AtomicInteger reports = new AtomicInteger(0);
        new SearchEngineClient() {
            @Override
            public EngineInfo getEngineInfo() {
                throw new IllegalStateException("client is not HttpClient.");
            }

            @Override
            protected void reportUnsupportedEngine(final EngineInfo.EngineType engineType) {
                reports.incrementAndGet();
            }
        }.warnUnlessOpenSearch3();
        assertEquals(0, reports.get());
    }

    /**
     * Builds a SearchResponse holding one hit per given id, as if it came back over HTTP. Each hit
     * carries a sort value so that the pager can derive its next search_after.
     */
    private SearchResponse responseWithIds(final String... ids) throws Exception {
        final StringBuilder buf = new StringBuilder();
        buf.append("{\"took\":1,\"timed_out\":false,\"_shards\":{\"total\":1,\"successful\":1,\"skipped\":0,\"failed\":0},");
        buf.append("\"hits\":{\"total\":{\"value\":").append(ids.length).append(",\"relation\":\"eq\"},\"max_score\":null,\"hits\":[");
        for (int i = 0; i < ids.length; i++) {
            if (i > 0) {
                buf.append(',');
            }
            buf.append("{\"_index\":\"test\",\"_id\":\"").append(ids[i]).append("\",\"_score\":null,");
            buf.append("\"_source\":{\"doc_id\":\"").append(ids[i]).append("\"},\"sort\":[").append(i + 1).append("]}");
        }
        buf.append("]}}");
        try (XContentParser parser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY,
                DeprecationHandler.IGNORE_DEPRECATIONS, buf.toString())) {
            return SearchResponse.fromXContent(parser);
        }
    }

    /**
     * A cursor returning false must end the whole walk, not just the current page. Before this was
     * fixed the pager kept fetching further pages after the cursor had asked to stop.
     */
    @Test
    public void test_scrollSearch_cursorReturningFalseStopsTheWalk() throws Exception {
        final AtomicInteger pagesOffered = new AtomicInteger(0);
        final SearchResponse page1 = responseWithIds("1", "2");
        final SearchResponse page2 = responseWithIds("3", "4");
        final SearchEngineClient client = new SearchEngineClient() {
            {
                // scrollSearch builds its request through the delegate; point it back at this
                // instance so the overridden prepareSearch below is the one that answers.
                this.client = this;
            }

            @Override
            public SearchRequestBuilder prepareSearch(final String... indices) {
                // The pager is stubbed out below, so the builder is never executed.
                return new SearchRequestBuilder(null, SearchAction.INSTANCE);
            }

            @Override
            protected void pitSearch(final String index, final String keepAlive, final String searchTimeout,
                    final SearchRequestBuilder builder, final BooleanFunction<SearchResponse> pageHandler) {
                for (final SearchResponse page : new SearchResponse[] { page1, page2 }) {
                    pagesOffered.incrementAndGet();
                    if (!pageHandler.apply(page)) {
                        return;
                    }
                }
            }
        };

        final List<String> seen = new ArrayList<>();
        final long count = client.scrollSearch("test", requestBuilder -> true, (response, hit) -> hit.getSourceAsMap(),
                (final Map<String, Object> source) -> {
                    seen.add((String) source.get("doc_id"));
                    return false;
                });

        assertEquals(1, pagesOffered.get());
        assertEquals(1, seen.size());
        assertEquals("1", seen.get(0));
        assertEquals(1L, count);
    }

    /**
     * A cursor that keeps returning true must be offered every page.
     */
    @Test
    public void test_scrollSearch_cursorReturningTrueWalksEveryPage() throws Exception {
        final AtomicInteger pagesOffered = new AtomicInteger(0);
        final SearchResponse page1 = responseWithIds("1", "2");
        final SearchResponse page2 = responseWithIds("3", "4");
        final SearchEngineClient client = new SearchEngineClient() {
            {
                // scrollSearch builds its request through the delegate; point it back at this
                // instance so the overridden prepareSearch below is the one that answers.
                this.client = this;
            }

            @Override
            public SearchRequestBuilder prepareSearch(final String... indices) {
                // The pager is stubbed out below, so the builder is never executed.
                return new SearchRequestBuilder(null, SearchAction.INSTANCE);
            }

            @Override
            protected void pitSearch(final String index, final String keepAlive, final String searchTimeout,
                    final SearchRequestBuilder builder, final BooleanFunction<SearchResponse> pageHandler) {
                for (final SearchResponse page : new SearchResponse[] { page1, page2 }) {
                    pagesOffered.incrementAndGet();
                    if (!pageHandler.apply(page)) {
                        return;
                    }
                }
            }
        };

        final List<String> seen = new ArrayList<>();
        final long count = client.scrollSearch("test", requestBuilder -> true, (response, hit) -> hit.getSourceAsMap(),
                (final Map<String, Object> source) -> {
                    seen.add((String) source.get("doc_id"));
                    return true;
                });

        assertEquals(2, pagesOffered.get());
        assertEquals(List.of("1", "2", "3", "4"), seen);
        assertEquals(4L, count);
    }

    @Test
    public void test_clampFacetSize() {
        assertEquals(0, SearchEngineClient.clampFacetSize(-5, 1000));
        assertEquals(500, SearchEngineClient.clampFacetSize(500, 1000));
        assertEquals(1000, SearchEngineClient.clampFacetSize(5000, 1000));
    }

    @Test
    public void test_clampMinDocCount_withMax() {
        assertEquals(0L, SearchEngineClient.clampMinDocCount(-1L, 100L));
        assertEquals(50L, SearchEngineClient.clampMinDocCount(50L, 100L));
        assertEquals(100L, SearchEngineClient.clampMinDocCount(5000L, 100L));
        assertEquals(100L, SearchEngineClient.clampMinDocCount(100L, 100L));
    }

    @Test
    public void test_isStartupBulkReloadTarget() {
        final SearchEngineClient client = new SearchEngineClient();

        assertTrue(client.isStartupBulkReloadTarget("fess_config.scheduled_job"));
        assertFalse(client.isStartupBulkReloadTarget("fess_config.label_type"));
        assertFalse(client.isStartupBulkReloadTarget("fess_config.web_config"));
        assertFalse(client.isStartupBulkReloadTarget("fess_user.user"));
        assertFalse(client.isStartupBulkReloadTarget("fess_log.click_log"));
        assertFalse(client.isStartupBulkReloadTarget(null));
    }

    @Test
    public void test_isUnsupportedEmbeddedEngine() {
        final SearchEngineClient client = new SearchEngineClient();

        assertTrue(client.isUnsupportedEmbeddedEngine(true, "faiss"));
        assertTrue(client.isUnsupportedEmbeddedEngine(true, "nmslib"));
        assertFalse(client.isUnsupportedEmbeddedEngine(true, "lucene"));
        assertFalse(client.isUnsupportedEmbeddedEngine(false, "faiss"));
        assertFalse(client.isUnsupportedEmbeddedEngine(false, "lucene"));
    }

    @Test
    public void test_isWebappProcess_trueWhenNoJobProcessMarkerIsSet() {
        final SearchEngineClient client = new SearchEngineClient();

        assertTrue(client.isWebappProcess(), "the webapp process itself sets none of the fess.<type>.process markers");
    }

    @Test
    public void test_isWebappProcess_falseWhenAnyJobProcessMarkerIsSet() {
        // IMPORTANT 3 regression test: each of the four child-process types that also boot app.xml
        // (and therefore also run SearchEngineClient#open) must be recognized so the startup bulk
        // reload (isStartupBulkReloadTarget) does not re-run in them -- most notably
        // ThumbnailGenerator, whose job is scheduled every minute by default.
        final SearchEngineClient client = new SearchEngineClient();
        final String[] processProperties =
                { "fess.crawler.process", "fess.thumbnail.process", "fess.suggest.process", "fess.chunk.process" };
        for (final String property : processProperties) {
            System.setProperty(property, "true");
            try {
                assertFalse(client.isWebappProcess(), property + "=true must not be treated as the webapp process");
            } finally {
                System.clearProperty(property);
            }
        }
    }

    @Test
    public void test_isWebappProcess_trueWhenJobProcessMarkerIsNotLiterallyTrue() {
        // addSystemProperty only ever writes "true" for these markers, but the check itself
        // must not accidentally treat any non-blank value as truthy.
        final SearchEngineClient client = new SearchEngineClient();
        System.setProperty("fess.thumbnail.process", "false");
        try {
            assertTrue(client.isWebappProcess());
        } finally {
            System.clearProperty("fess.thumbnail.process");
        }
    }

    @Test
    public void test_isMissingKnnPluginError_detectsUnknownIndexKnnSetting() {
        final SearchEngineClient client = new SearchEngineClient();

        assertTrue(client.isMissingKnnPluginError(new IllegalStateException("unknown setting [index.knn] for index [fess.20260101]")));
    }

    @Test
    public void test_isMissingKnnPluginError_detectsMissingKnnVectorTypeHandler() {
        final SearchEngineClient client = new SearchEngineClient();

        assertTrue(client.isMissingKnnPluginError(new IllegalArgumentException("No handler for type [knn_vector] declared on field")));
    }

    @Test
    public void test_isMissingKnnPluginError_walksCauseChain() {
        final SearchEngineClient client = new SearchEngineClient();
        final Exception wrapped = new RuntimeException("index creation failed",
                new IllegalStateException("unknown setting [index.knn] for index [fess.20260101]"));

        assertTrue(client.isMissingKnnPluginError(wrapped), "the diagnostic must inspect the full cause chain, not just the top exception");
    }

    @Test
    public void test_isMissingKnnPluginError_falseForUnrelatedFailure() {
        final SearchEngineClient client = new SearchEngineClient();

        assertFalse(client.isMissingKnnPluginError(new RuntimeException("connection refused")));
    }
}
