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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codelibs.fess.query;

import org.apache.lucene.search.Query;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

/**
 * Test class for QueryCommand template methods and Set-based field lookups.
 * Tests the convertWithFieldCheck() method and isSearchField() improvements.
 */
public class QueryCommandTemplateMethodTest extends QueryTestBase {

    private TestQueryCommand queryCommand;
    private QueryFieldConfig queryFieldConfig;

    @Override
    protected void setUpChild() throws Exception {
        queryCommand = new TestQueryCommand();
        queryFieldConfig = ComponentUtil.getQueryFieldConfig();

        // Set up search fields
        String[] searchFields = { "title", "content", "url", "host", "filetype" };
        queryFieldConfig.setSearchFields(searchFields);
    }

    /**
     * Test isSearchField() with Set-based lookup.
     */
    public void test_isSearchField_withSetBasedLookup() {
        // Test valid search fields
        assertTrue(queryCommand.isSearchField("title"));
        assertTrue(queryCommand.isSearchField("content"));
        assertTrue(queryCommand.isSearchField("url"));
        assertTrue(queryCommand.isSearchField("host"));
        assertTrue(queryCommand.isSearchField("filetype"));

        // Test invalid search fields
        assertFalse(queryCommand.isSearchField("invalid"));
        assertFalse(queryCommand.isSearchField("unknown"));
        assertFalse(queryCommand.isSearchField(""));
    }

    /**
     * Test isSearchField() with null field.
     */
    public void test_isSearchField_withNull() {
        assertFalse(queryCommand.isSearchField(null));
    }

    /**
     * Test isSearchField() with empty search field set.
     */
    public void test_isSearchField_withEmptySet() {
        queryFieldConfig.setSearchFields(new String[] {});

        assertFalse(queryCommand.isSearchField("title"));
        assertFalse(queryCommand.isSearchField("content"));
        assertFalse(queryCommand.isSearchField("anyfield"));
    }

    /**
     * Test isSearchField() performance with large field set.
     */
    public void test_isSearchField_performance() {
        // Create a large set of search fields
        int fieldCount = 1000;
        String[] fields = new String[fieldCount];
        for (int i = 0; i < fieldCount; i++) {
            fields[i] = "field" + i;
        }
        queryFieldConfig.setSearchFields(fields);

        // Test field at the end (worst case for array lookup, but same for Set)
        String testField = "field999";

        // Warm up
        for (int i = 0; i < 100; i++) {
            queryCommand.isSearchField(testField);
        }

        // Measure Set-based lookup
        long startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            queryCommand.isSearchField(testField);
        }
        long duration = System.nanoTime() - startTime;

        // Should complete quickly (this is mainly a sanity check)
        assertTrue("Set-based lookup should complete quickly", duration < 100_000_000); // 100ms
    }

    /**
     * Test convertWithFieldCheck() with DEFAULT_FIELD.
     */
    public void test_convertWithFieldCheck_withDefaultField() {
        FessConfig fessConfig = ComponentUtil.getFessConfig();
        QueryContext context = new QueryContext("test", false);

        QueryBuilder result = queryCommand.convertWithFieldCheck(
                fessConfig,
                context,
                Constants.DEFAULT_FIELD,
                "test text",
                1.0f,
                (field, boost) -> QueryBuilders.matchQuery(field, "test text").boost(boost),
                (field, text, boost) -> QueryBuilders.matchQuery(field, text).boost(boost));

        // Should build default query
        assertNotNull(result);
        assertTrue(result instanceof DefaultQueryBuilder);

        // Context should have field log
        assertTrue(context.getFieldLogMap().size() > 0);
        assertTrue(context.getHighlightedQuerySet().contains("test text"));
    }

    /**
     * Test convertWithFieldCheck() with search field.
     */
    public void test_convertWithFieldCheck_withSearchField() {
        FessConfig fessConfig = ComponentUtil.getFessConfig();
        QueryContext context = new QueryContext("test", false);

        QueryBuilder result = queryCommand.convertWithFieldCheck(
                fessConfig,
                context,
                "title",  // Valid search field
                "test text",
                2.0f,
                (field, boost) -> QueryBuilders.matchQuery(field, "test text").boost(boost),
                (field, text, boost) -> QueryBuilders.matchQuery(field, text).boost(boost));

        // Should use field builder
        assertNotNull(result);

        // Context should have field log
        assertEquals(1, context.getFieldLogMap().size());
        assertTrue(context.getHighlightedQuerySet().contains("test text"));
    }

    /**
     * Test convertWithFieldCheck() with non-search field (fallback to default).
     */
    public void test_convertWithFieldCheck_withNonSearchField() {
        FessConfig fessConfig = ComponentUtil.getFessConfig();
        QueryContext context = new QueryContext("test", false);

        QueryBuilder result = queryCommand.convertWithFieldCheck(
                fessConfig,
                context,
                "unsupported_field",  // Not a search field
                "test text",
                1.0f,
                (field, boost) -> QueryBuilders.matchQuery(field, "test text").boost(boost),
                (field, text, boost) -> QueryBuilders.matchQuery(field, text).boost(boost));

        // Should fall back to default query builder
        assertNotNull(result);
        assertTrue(result instanceof DefaultQueryBuilder);

        // Context should have field log with both original field and DEFAULT_FIELD
        assertTrue(context.getFieldLogMap().size() >= 2);
        assertTrue(context.getHighlightedQuerySet().contains("test text"));
    }

    /**
     * Test convertWithFieldCheck() adds query text to context.
     */
    public void test_convertWithFieldCheck_addsToContext() {
        FessConfig fessConfig = ComponentUtil.getFessConfig();
        QueryContext context = new QueryContext("test", false);

        String queryText = "search term";
        queryCommand.convertWithFieldCheck(
                fessConfig,
                context,
                "title",
                queryText,
                1.0f,
                (field, boost) -> QueryBuilders.matchQuery(field, queryText).boost(boost),
                (field, text, boost) -> QueryBuilders.matchQuery(field, text).boost(boost));

        // Verify context was updated
        assertTrue("Query text should be in highlighted query set",
                context.getHighlightedQuerySet().contains(queryText));

        assertTrue("Field log should contain the field",
                context.getFieldLogMap().size() > 0);
    }

    /**
     * Test convertWithFieldCheck() with different boost values.
     */
    public void test_convertWithFieldCheck_withDifferentBoosts() {
        FessConfig fessConfig = ComponentUtil.getFessConfig();

        float[] boostValues = { 0.5f, 1.0f, 2.0f, 5.0f, 10.0f };

        for (float boost : boostValues) {
            QueryContext context = new QueryContext("test", false);

            QueryBuilder result = queryCommand.convertWithFieldCheck(
                    fessConfig,
                    context,
                    "title",
                    "test",
                    boost,
                    (field, b) -> QueryBuilders.matchQuery(field, "test").boost(b),
                    (field, text, b) -> QueryBuilders.matchQuery(field, text).boost(b));

            assertNotNull("Result should not be null for boost " + boost, result);
        }
    }

    /**
     * Test convertWithFieldCheck() with empty text.
     */
    public void test_convertWithFieldCheck_withEmptyText() {
        FessConfig fessConfig = ComponentUtil.getFessConfig();
        QueryContext context = new QueryContext("test", false);

        QueryBuilder result = queryCommand.convertWithFieldCheck(
                fessConfig,
                context,
                "title",
                "",  // Empty text
                1.0f,
                (field, boost) -> QueryBuilders.matchQuery(field, "").boost(boost),
                (field, text, boost) -> QueryBuilders.matchQuery(field, text).boost(boost));

        assertNotNull(result);
    }

    /**
     * Test convertWithFieldCheck() with null field (should use DEFAULT_FIELD).
     */
    public void test_convertWithFieldCheck_withNullField() {
        FessConfig fessConfig = ComponentUtil.getFessConfig();
        QueryContext context = new QueryContext("test", false);

        QueryBuilder result = queryCommand.convertWithFieldCheck(
                fessConfig,
                context,
                null,
                "test text",
                1.0f,
                (field, boost) -> QueryBuilders.matchQuery(field, "test text").boost(boost),
                (field, text, boost) -> QueryBuilders.matchQuery(field, text).boost(boost));

        // Should fall back to default query builder since null != DEFAULT_FIELD and null is not a search field
        assertNotNull(result);
    }

    /**
     * Test FieldQueryBuilder functional interface.
     */
    public void test_fieldQueryBuilder_interface() {
        QueryCommand.FieldQueryBuilder builder = (field, text, boost) ->
            QueryBuilders.termQuery(field, text).boost(boost);

        QueryBuilder result = builder.buildQuery("title", "test", 2.0f);
        assertNotNull(result);
    }

    /**
     * Test DefaultQueryBuilderFunction functional interface.
     */
    public void test_defaultQueryBuilderFunction_interface() {
        QueryCommand.DefaultQueryBuilderFunction builder = (field, boost) ->
            QueryBuilders.matchQuery(field, "test").boost(boost);

        QueryBuilder result = builder.apply("title", 3.0f);
        assertNotNull(result);
    }

    /**
     * Test that convertWithFieldCheck() pattern reduces code duplication.
     * This test demonstrates how the template method can be used to replace
     * repetitive field checking logic.
     */
    public void test_convertWithFieldCheck_reducesCodeDuplication() {
        FessConfig fessConfig = ComponentUtil.getFessConfig();
        QueryContext context1 = new QueryContext("test", false);
        QueryContext context2 = new QueryContext("test", false);

        // Using template method
        QueryBuilder result1 = queryCommand.convertWithFieldCheck(
                fessConfig,
                context1,
                "title",
                "test",
                1.0f,
                (field, boost) -> QueryBuilders.matchQuery(field, "test").boost(boost),
                (field, text, boost) -> QueryBuilders.matchQuery(field, text).boost(boost));

        // Manual implementation (for comparison)
        QueryBuilder result2;
        context2.addFieldLog("title", "test");
        context2.addHighlightedQuery("test");
        if (Constants.DEFAULT_FIELD.equals("title")) {
            result2 = queryCommand.buildDefaultQueryBuilder(fessConfig, context2,
                    (field, boost) -> QueryBuilders.matchQuery(field, "test").boost(boost));
        } else if (queryCommand.isSearchField("title")) {
            result2 = QueryBuilders.matchQuery("title", "test").boost(1.0f);
        } else {
            context2.addFieldLog(Constants.DEFAULT_FIELD, "test");
            result2 = queryCommand.buildDefaultQueryBuilder(fessConfig, context2,
                    (field, boost) -> QueryBuilders.matchQuery(field, "test").boost(boost));
        }

        // Both should produce similar results
        assertNotNull(result1);
        assertNotNull(result2);
    }

    /**
     * Test query command implementation for testing purposes.
     */
    private static class TestQueryCommand extends QueryCommand {

        @Override
        public QueryBuilder execute(QueryContext context, Query query, float boost) {
            return QueryBuilders.matchAllQuery();
        }

        @Override
        protected String getQueryClassName() {
            return "TestQuery";
        }

        // Expose protected methods for testing
        @Override
        public boolean isSearchField(String field) {
            return super.isSearchField(field);
        }

        @Override
        public QueryBuilder convertWithFieldCheck(FessConfig fessConfig, QueryContext context,
                String field, String text, float boost,
                DefaultQueryBuilderFunction defaultBuilder,
                FieldQueryBuilder fieldBuilder) {
            return super.convertWithFieldCheck(fessConfig, context, field, text, boost, defaultBuilder, fieldBuilder);
        }
    }
}
