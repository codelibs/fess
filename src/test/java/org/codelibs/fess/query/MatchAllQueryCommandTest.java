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
package org.codelibs.fess.query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.query.parser.QueryParser;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.index.query.MatchAllQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class MatchAllQueryCommandTest extends UnitFessTestCase {
    private static final Logger logger = LogManager.getLogger(MatchAllQueryCommandTest.class);

    private MatchAllQueryCommand matchAllQueryCommand;

    private void setupMockFessConfig() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {

            private static final long serialVersionUID = 1L;

            @Override
            public String get(String key) {
                // Override to return empty string instead of null to avoid NullPointerException
                return "";
            }

            @Override
            public String getIndexFieldTitle() {
                return "title";
            }

            @Override
            public String getIndexFieldContent() {
                return "content";
            }

            @Override
            public String getIndexFieldId() {
                return "_id";
            }

            @Override
            public String getIndexFieldDocId() {
                return "doc_id";
            }

            @Override
            public String getIndexFieldBoost() {
                return "boost";
            }

            @Override
            public String getIndexFieldContentLength() {
                return "content_length";
            }

            @Override
            public String getIndexFieldHost() {
                return "host";
            }

            @Override
            public String getIndexFieldSite() {
                return "site";
            }

            @Override
            public String getIndexFieldLastModified() {
                return "last_modified";
            }

            @Override
            public String getIndexFieldTimestamp() {
                return "timestamp";
            }

            @Override
            public String getIndexFieldMimetype() {
                return "mimetype";
            }

            @Override
            public String getIndexFieldFiletype() {
                return "filetype";
            }

            @Override
            public String getIndexFieldFilename() {
                return "filename";
            }

            @Override
            public String getIndexFieldCreated() {
                return "created";
            }

            @Override
            public String getIndexFieldDigest() {
                return "digest";
            }

            @Override
            public String getIndexFieldUrl() {
                return "url";
            }

            @Override
            public String getIndexFieldThumbnail() {
                return "thumbnail";
            }

            @Override
            public String getIndexFieldClickCount() {
                return "click_count";
            }

            @Override
            public String getIndexFieldFavoriteCount() {
                return "favorite_count";
            }

            @Override
            public String getIndexFieldConfigId() {
                return "config_id";
            }

            @Override
            public String getIndexFieldLang() {
                return "lang";
            }

            @Override
            public String getIndexFieldHasCache() {
                return "has_cache";
            }

            @Override
            public String getQueryDefaultLanguages() {
                return "en,ja";
            }

            @Override
            public String getQueryLanguageMapping() {
                return "en:en,ja:ja";
            }

            @Override
            public String getQueryAdditionalDefaultFields() {
                return "";
            }

            @Override
            public String getQueryAdditionalSearchFields() {
                return "";
            }

            @Override
            public String getQueryAdditionalFacetFields() {
                return "";
            }

            @Override
            public String getQueryAdditionalSortFields() {
                return "";
            }

            @Override
            public String getQueryAdditionalAnalyzedFields() {
                return "";
            }

            @Override
            public String getQueryAdditionalNotAnalyzedFields() {
                return "";
            }

            @Override
            public String[] getQueryAdditionalResponseFields(String... fields) {
                return fields;
            }

            @Override
            public String[] getQueryAdditionalScrollResponseFields(String... fields) {
                return fields;
            }

            @Override
            public String[] getQueryAdditionalCacheResponseFields(String... fields) {
                return fields;
            }

            @Override
            public String[] getQueryAdditionalHighlightedFields(String... fields) {
                return fields;
            }

            @Override
            public String[] getQueryAdditionalSearchFields(String... fields) {
                return fields;
            }

            @Override
            public String[] getQueryAdditionalFacetFields(String... fields) {
                return fields;
            }

            @Override
            public String[] getQueryAdditionalSortFields(String... fields) {
                return fields;
            }

            @Override
            public String[] getQueryAdditionalApiResponseFields(String... fields) {
                return fields;
            }
        });
    }

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);

        // Setup FessConfig
        setupMockFessConfig();

        // Initialize QueryFieldConfig
        QueryFieldConfig queryFieldConfig = new QueryFieldConfig();
        queryFieldConfig.init();
        ComponentUtil.register(queryFieldConfig, "queryFieldConfig");

        // Initialize QueryParser
        QueryParser queryParser = new QueryParser();
        queryParser.init();
        ComponentUtil.register(queryParser, "queryParser");

        // Initialize QueryProcessor
        QueryProcessor queryProcessor = new QueryProcessor();
        queryProcessor.init();
        ComponentUtil.register(queryProcessor, "queryProcessor");

        // Initialize MatchAllQueryCommand
        matchAllQueryCommand = new MatchAllQueryCommand();
        matchAllQueryCommand.register();
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        super.tearDown(testInfo);
    }

    @Test
    public void test_getQueryClassName() {
        // Test that getQueryClassName returns the correct class name
        String className = matchAllQueryCommand.getQueryClassName();
        assertEquals("MatchAllDocsQuery", className);
        assertEquals(MatchAllDocsQuery.class.getSimpleName(), className);
    }

    @Test
    public void test_execute() {
        // Test execute method with normal parameters
        QueryContext context = new QueryContext("*:*", false);
        MatchAllDocsQuery query = new MatchAllDocsQuery();
        float boost = 1.0f;

        QueryBuilder result = matchAllQueryCommand.execute(context, query, boost);

        assertNotNull(result);
        assertTrue(result instanceof MatchAllQueryBuilder);

        // Verify the query builder generates correct JSON
        String json = result.toString().replaceAll("[\\s\\n]", "");
        assertEquals("{\"match_all\":{\"boost\":1.0}}", json);
    }

    @Test
    public void test_execute_withDifferentBoost() {
        // Test execute method with different boost value
        QueryContext context = new QueryContext("*:*", false);
        MatchAllDocsQuery query = new MatchAllDocsQuery();
        float boost = 2.5f;

        QueryBuilder result = matchAllQueryCommand.execute(context, query, boost);

        assertNotNull(result);
        assertTrue(result instanceof MatchAllQueryBuilder);

        // MatchAllQueryBuilder should apply the boost value
        String json = result.toString().replaceAll("[\\s\\n]", "");
        assertEquals("{\"match_all\":{\"boost\":2.5}}", json);
    }

    @Test
    public void test_execute_withNullContext() {
        // Test execute method with null context
        MatchAllDocsQuery query = new MatchAllDocsQuery();
        float boost = 1.0f;

        QueryBuilder result = matchAllQueryCommand.execute(null, query, boost);

        assertNotNull(result);
        assertTrue(result instanceof MatchAllQueryBuilder);
    }

    @Test
    public void test_execute_withNullQuery() {
        // Test execute method with null query
        QueryContext context = new QueryContext("*:*", false);
        float boost = 1.0f;

        QueryBuilder result = matchAllQueryCommand.execute(context, null, boost);

        assertNotNull(result);
        assertTrue(result instanceof MatchAllQueryBuilder);
    }

    @Test
    public void test_execute_withZeroBoost() {
        // Test execute method with zero boost
        QueryContext context = new QueryContext("*:*", false);
        MatchAllDocsQuery query = new MatchAllDocsQuery();
        float boost = 0.0f;

        QueryBuilder result = matchAllQueryCommand.execute(context, query, boost);

        assertNotNull(result);
        assertTrue(result instanceof MatchAllQueryBuilder);
    }

    @Test
    public void test_execute_withNegativeBoost() {
        // Test execute method with negative boost - should throw exception
        QueryContext context = new QueryContext("*:*", false);
        MatchAllDocsQuery query = new MatchAllDocsQuery();
        float boost = -1.0f;

        try {
            matchAllQueryCommand.execute(context, query, boost);
            fail("Should throw IllegalArgumentException for negative boost");
        } catch (IllegalArgumentException e) {
            // Expected exception
            assertTrue(e.getMessage().contains("negative [boost] are not allowed"));
        }
    }

    @Test
    public void test_execute_multipleInvocations() {
        // Test that multiple invocations return consistent results
        QueryContext context = new QueryContext("*:*", false);
        MatchAllDocsQuery query = new MatchAllDocsQuery();
        float boost = 1.0f;

        QueryBuilder result1 = matchAllQueryCommand.execute(context, query, boost);
        QueryBuilder result2 = matchAllQueryCommand.execute(context, query, boost);

        assertNotNull(result1);
        assertNotNull(result2);

        // Both results should be of the same type
        assertTrue(result1 instanceof MatchAllQueryBuilder);
        assertTrue(result2 instanceof MatchAllQueryBuilder);

        // Both should generate the same JSON
        String json1 = result1.toString().replaceAll("[\\s\\n]", "");
        String json2 = result2.toString().replaceAll("[\\s\\n]", "");
        assertEquals(json1, json2);
    }

    @Test
    public void test_execute_withVeryLargeBoost() {
        // Test execute method with very large boost value
        QueryContext context = new QueryContext("*:*", false);
        MatchAllDocsQuery query = new MatchAllDocsQuery();
        float boost = Float.MAX_VALUE;

        QueryBuilder result = matchAllQueryCommand.execute(context, query, boost);

        assertNotNull(result);
        assertTrue(result instanceof MatchAllQueryBuilder);
    }

    @Test
    public void test_execute_withFloatInfinityBoost() {
        // Test execute method with infinity boost value
        QueryContext context = new QueryContext("*:*", false);
        MatchAllDocsQuery query = new MatchAllDocsQuery();
        float boost = Float.POSITIVE_INFINITY;

        QueryBuilder result = matchAllQueryCommand.execute(context, query, boost);

        assertNotNull(result);
        assertTrue(result instanceof MatchAllQueryBuilder);
    }

    @Test
    public void test_execute_withNaNBoost() {
        // Test execute method with NaN boost value
        QueryContext context = new QueryContext("*:*", false);
        MatchAllDocsQuery query = new MatchAllDocsQuery();
        float boost = Float.NaN;

        QueryBuilder result = matchAllQueryCommand.execute(context, query, boost);

        assertNotNull(result);
        assertTrue(result instanceof MatchAllQueryBuilder);
    }

    @Test
    public void test_constructor() {
        // Test that constructor creates a valid instance
        MatchAllQueryCommand command = new MatchAllQueryCommand();
        assertNotNull(command);
    }

    @Test
    public void test_execute_logOutput() {
        // Test that execute method logs debug output when debug is enabled
        QueryContext context = new QueryContext("*:*", false);
        MatchAllDocsQuery query = new MatchAllDocsQuery();
        float boost = 1.5f;

        // Execute and ensure no exceptions are thrown
        QueryBuilder result = matchAllQueryCommand.execute(context, query, boost);

        assertNotNull(result);

        // Note: Actual log output verification would require a log appender mock
        // but we can at least ensure the method executes without errors
        logger.info("Execute method called with query: {} and boost: {}", query, boost);
    }

    @Test
    public void test_execute_withHighBoost() {
        // Test execute with high boost value
        QueryContext context = new QueryContext("test", false);
        MatchAllDocsQuery query = new MatchAllDocsQuery();
        float boost = 10.0f;

        QueryBuilder result = matchAllQueryCommand.execute(context, query, boost);

        assertNotNull(result);
        assertTrue(result instanceof MatchAllQueryBuilder);

        String json = result.toString().replaceAll("[\\s\\n]", "");
        assertEquals("{\"match_all\":{\"boost\":10.0}}", json);
    }

    @Test
    public void test_execute_withSmallBoost() {
        // Test execute with small boost value
        QueryContext context = new QueryContext("test", false);
        MatchAllDocsQuery query = new MatchAllDocsQuery();
        float boost = 0.001f;

        QueryBuilder result = matchAllQueryCommand.execute(context, query, boost);

        assertNotNull(result);
        assertTrue(result instanceof MatchAllQueryBuilder);
    }

    @Test
    public void test_execute_withNormalizedBoost() {
        // Test execute with normalized boost value
        QueryContext context = new QueryContext("test", false);
        MatchAllDocsQuery query = new MatchAllDocsQuery();
        float boost = 1.0f;

        QueryBuilder result = matchAllQueryCommand.execute(context, query, boost);

        assertNotNull(result);
        assertTrue(result instanceof MatchAllQueryBuilder);

        // Verify the default normalized boost
        String json = result.toString().replaceAll("[\\s\\n]", "");
        assertEquals("{\"match_all\":{\"boost\":1.0}}", json);
    }

    @Test
    public void test_execute_withEmptyContext() {
        // Test execute with empty query string context
        QueryContext context = new QueryContext("", false);
        MatchAllDocsQuery query = new MatchAllDocsQuery();
        float boost = 1.0f;

        QueryBuilder result = matchAllQueryCommand.execute(context, query, boost);

        assertNotNull(result);
        assertTrue(result instanceof MatchAllQueryBuilder);
    }

    @Test
    public void test_execute_withWhitespaceContext() {
        // Test execute with whitespace query string context
        QueryContext context = new QueryContext("   ", false);
        MatchAllDocsQuery query = new MatchAllDocsQuery();
        float boost = 1.0f;

        QueryBuilder result = matchAllQueryCommand.execute(context, query, boost);

        assertNotNull(result);
        assertTrue(result instanceof MatchAllQueryBuilder);
    }

    @Test
    public void test_execute_withContextHavingSpecialCharacters() {
        // Test execute with context having special characters
        QueryContext context = new QueryContext("!@#$%^&*()", false);
        MatchAllDocsQuery query = new MatchAllDocsQuery();
        float boost = 1.0f;

        QueryBuilder result = matchAllQueryCommand.execute(context, query, boost);

        assertNotNull(result);
        assertTrue(result instanceof MatchAllQueryBuilder);
    }
}