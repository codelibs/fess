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
package org.codelibs.fess.job;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.LanguageHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.script.Script;
import org.opensearch.script.ScriptType;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class UpdateLabelJobTest extends UnitFessTestCase {

    private UpdateLabelJob updateLabelJob;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        updateLabelJob = new UpdateLabelJob();

        // Setup mock components
        setupMockComponents();
    }

    private void setupMockComponents() {
        // Mock SearchEngineClient
        SearchEngineClient mockSearchEngineClient = new SearchEngineClient() {
            @Override
            public long updateByQuery(String index,
                    java.util.function.Function<org.opensearch.action.search.SearchRequestBuilder, org.opensearch.action.search.SearchRequestBuilder> builder,
                    java.util.function.BiFunction<org.opensearch.action.update.UpdateRequestBuilder, org.opensearch.search.SearchHit, org.opensearch.action.update.UpdateRequestBuilder> processor) {
                // Return a fixed count for testing
                return 3L;
            }
        };
        ComponentUtil.register(mockSearchEngineClient, "searchEngineClient");

        // Mock FessConfig
        FessConfig mockFessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getIndexDocumentUpdateIndex() {
                return "fess.update";
            }

            @Override
            public String getIndexFieldUrl() {
                return "url";
            }

            @Override
            public String getIndexFieldLang() {
                return "lang";
            }

            @Override
            public String getIndexFieldLabel() {
                return "label";
            }
        };
        ComponentUtil.setFessConfig(mockFessConfig);

        // Mock LabelTypeHelper
        LabelTypeHelper mockLabelTypeHelper = new LabelTypeHelper() {
            @Override
            public Set<String> getMatchedLabelValueSet(String url) {
                Set<String> labels = new HashSet<>();
                if (url != null && url.contains("test")) {
                    labels.add("label1");
                    labels.add("label2");
                }
                return labels;
            }
        };
        ComponentUtil.register(mockLabelTypeHelper, "labelTypeHelper");

        // Mock LanguageHelper
        LanguageHelper mockLanguageHelper = new LanguageHelper() {
            @Override
            public Script createScript(Map<String, Object> doc, String scriptString) {
                return new Script(ScriptType.INLINE, "painless", scriptString, new HashMap<>());
            }
        };
        ComponentUtil.register(mockLanguageHelper, "languageHelper");
    }

    // Test execute() method with successful processing
    @Test
    public void test_execute_success() {
        String result = updateLabelJob.execute();
        assertNotNull(result);
        assertTrue(result.contains("3 documents")); // 3 documents processed
        assertFalse(result.contains("exception"));
    }

    // Test execute() method with query builder
    @Test
    public void test_execute_withQueryBuilder() {
        QueryBuilder queryBuilder = QueryBuilders.termQuery("field", "value");
        updateLabelJob.query(queryBuilder);

        String result = updateLabelJob.execute();
        assertNotNull(result);
        assertTrue(result.contains("3 documents"));
        // queryBuilder field is protected, so we can't directly assert it
    }

    // Test execute() method with exception handling
    @Test
    public void test_execute_withException() {
        // Register a mock that throws exception
        SearchEngineClient exceptionClient = new SearchEngineClient() {
            @Override
            public long updateByQuery(String index,
                    java.util.function.Function<org.opensearch.action.search.SearchRequestBuilder, org.opensearch.action.search.SearchRequestBuilder> builder,
                    java.util.function.BiFunction<org.opensearch.action.update.UpdateRequestBuilder, org.opensearch.search.SearchHit, org.opensearch.action.update.UpdateRequestBuilder> processor) {
                throw new RuntimeException("Test exception");
            }
        };
        ComponentUtil.register(exceptionClient, "searchEngineClient");

        String result = updateLabelJob.execute();
        assertNotNull(result);
        assertTrue(result.contains("Test exception"));
        assertFalse(result.contains("documents"));
    }

    // Test query() method
    @Test
    public void test_query() {
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        UpdateLabelJob returnedJob = updateLabelJob.query(queryBuilder);

        assertSame(updateLabelJob, returnedJob); // Method chaining
        assertEquals(queryBuilder, updateLabelJob.queryBuilder);
    }

    // Test query() method with null
    @Test
    public void test_query_withNull() {
        updateLabelJob.query(null);

        // queryBuilder field is protected, we can't directly access it
        // But we can verify the job still executes properly
        String result = updateLabelJob.execute();
        assertNotNull(result);
        assertTrue(result.contains("documents"));
    }

    // Test constructor
    @Test
    public void test_constructor() {
        UpdateLabelJob job = new UpdateLabelJob();
        assertNotNull(job);
        assertNull(job.queryBuilder);
    }

    // Test processing documents with various URL conditions
    @Test
    public void test_execute_variousUrlConditions() {
        String result = updateLabelJob.execute();
        assertNotNull(result);
        // The mock returns a fixed count of 3
        assertTrue(result.contains("documents"));
    }

    // Test with empty label set
    @Test
    public void test_execute_withEmptyLabelSet() {
        // Override mock to return empty label set
        LabelTypeHelper emptyLabelHelper = new LabelTypeHelper() {
            @Override
            public Set<String> getMatchedLabelValueSet(String url) {
                return new HashSet<>();
            }
        };
        ComponentUtil.register(emptyLabelHelper, "labelTypeHelper");

        // Mock SearchEngineClient that returns 0
        SearchEngineClient zeroClient = new SearchEngineClient() {
            @Override
            public long updateByQuery(String index,
                    java.util.function.Function<org.opensearch.action.search.SearchRequestBuilder, org.opensearch.action.search.SearchRequestBuilder> builder,
                    java.util.function.BiFunction<org.opensearch.action.update.UpdateRequestBuilder, org.opensearch.search.SearchHit, org.opensearch.action.update.UpdateRequestBuilder> processor) {
                return 0L;
            }
        };
        ComponentUtil.register(zeroClient, "searchEngineClient");

        String result = updateLabelJob.execute();
        assertNotNull(result);
        assertTrue(result.contains("0 documents"));
    }

    // Test script generation with multiple labels
    @Test
    public void test_execute_scriptGenerationWithMultipleLabels() {
        // This test verifies that the job executes correctly
        // Note: We can't easily test the actual script generation because the mock doesn't call the processor

        String result = updateLabelJob.execute();
        assertNotNull(result);
        assertTrue(result.contains("3 documents")); // Verify the job executes
    }

    // Test handling of processing exception
    @Test
    public void test_execute_withProcessingException() {
        // Create a mock that returns 0 to simulate processing error
        SearchEngineClient errorClient = new SearchEngineClient() {
            @Override
            public long updateByQuery(String index,
                    java.util.function.Function<org.opensearch.action.search.SearchRequestBuilder, org.opensearch.action.search.SearchRequestBuilder> builder,
                    java.util.function.BiFunction<org.opensearch.action.update.UpdateRequestBuilder, org.opensearch.search.SearchHit, org.opensearch.action.update.UpdateRequestBuilder> processor) {
                // Return 0 to simulate no documents processed due to error
                return 0L;
            }
        };
        ComponentUtil.register(errorClient, "searchEngineClient");

        String result = updateLabelJob.execute();
        assertNotNull(result);
        assertTrue(result.contains("0 documents"));
    }

}