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

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

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
