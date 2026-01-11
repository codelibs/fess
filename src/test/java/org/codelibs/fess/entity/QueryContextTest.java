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
package org.codelibs.fess.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.MatchAllQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.TermQueryBuilder;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder;
import org.opensearch.index.query.functionscore.ScoreFunctionBuilders;
import org.opensearch.search.sort.SortBuilder;
import org.opensearch.search.sort.SortBuilders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class QueryContextTest extends UnitFessTestCase {

    private QueryContext queryContext;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        // Set up FessConfig mock
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getIndexFieldUrl() {
                return "url";
            }

            @Override
            public String getIndexFieldTitle() {
                return "title";
            }
        });
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    // Test constructor with null query string
    @Test
    public void test_constructor_nullQueryString() {
        queryContext = new QueryContext(null, false);
        assertEquals("*", queryContext.getQueryString());
        assertNull(queryContext.getDefaultField());
    }

    // Test constructor with empty query string
    @Test
    public void test_constructor_emptyQueryString() {
        queryContext = new QueryContext("", false);
        assertEquals("*", queryContext.getQueryString());
        assertNull(queryContext.getDefaultField());
    }

    // Test constructor with blank query string
    @Test
    public void test_constructor_blankQueryString() {
        queryContext = new QueryContext("   ", false);
        assertEquals("*", queryContext.getQueryString());
        assertNull(queryContext.getDefaultField());
    }

    // Test constructor with normal query string
    @Test
    public void test_constructor_normalQueryString() {
        queryContext = new QueryContext("test query", false);
        assertEquals("test query", queryContext.getQueryString());
        assertNull(queryContext.getDefaultField());
    }

    // Test constructor with allinurl prefix
    @Test
    public void test_constructor_allinurlPrefix() {
        queryContext = new QueryContext("allinurl:test query", false);
        assertEquals("test query", queryContext.getQueryString());
        assertEquals("url", queryContext.getDefaultField());
    }

    // Test constructor with allinurl prefix and empty remainder
    @Test
    public void test_constructor_allinurlPrefixEmptyRemainder() {
        queryContext = new QueryContext("allinurl:", false);
        assertEquals("*", queryContext.getQueryString());
        assertEquals("url", queryContext.getDefaultField());
    }

    // Test constructor with allintitle prefix
    @Test
    public void test_constructor_allintitlePrefix() {
        queryContext = new QueryContext("allintitle:test query", false);
        assertEquals("test query", queryContext.getQueryString());
        assertEquals("title", queryContext.getDefaultField());
    }

    // Test constructor with allintitle prefix and empty remainder
    @Test
    public void test_constructor_allintitlePrefixEmptyRemainder() {
        queryContext = new QueryContext("allintitle:", false);
        assertEquals("*", queryContext.getQueryString());
        assertEquals("title", queryContext.getDefaultField());
    }

    // Test constructor with isQuery true (with request context)
    @Test
    public void test_constructor_isQueryTrue() {
        getMockRequest().setAttribute(Constants.FIELD_LOGS, new HashMap<String, List<String>>());
        queryContext = new QueryContext("test", true);
        assertEquals("test", queryContext.getQueryString());

        // Verify that highlighting and field log are initialized
        assertNotNull(getMockRequest().getAttribute(Constants.HIGHLIGHT_QUERIES));
        assertNotNull(getMockRequest().getAttribute(Constants.FIELD_LOGS));
    }

    // Test constructor with isQuery true and no existing field logs
    @Test
    public void test_constructor_isQueryTrueNoExistingFieldLogs() {
        queryContext = new QueryContext("test", true);
        assertEquals("test", queryContext.getQueryString());

        // Verify that field logs are created
        Object fieldLogs = getMockRequest().getAttribute(Constants.FIELD_LOGS);
        assertNotNull(fieldLogs);
        assertTrue(fieldLogs instanceof Map);
    }

    // Test setQueryBuilder and getQueryBuilder
    @Test
    public void test_setAndGetQueryBuilder() {
        queryContext = new QueryContext("test", false);
        assertNull(queryContext.getQueryBuilder());

        QueryBuilder queryBuilder = QueryBuilders.termQuery("field", "value");
        queryContext.setQueryBuilder(queryBuilder);
        assertEquals(queryBuilder, queryContext.getQueryBuilder());
    }

    // Test addFunctionScore
    @Test
    public void test_addFunctionScore() {
        queryContext = new QueryContext("test", false);
        QueryBuilder initialQuery = QueryBuilders.termQuery("field", "value");
        queryContext.setQueryBuilder(initialQuery);

        queryContext.addFunctionScore(list -> {
            list.add(new FilterFunctionBuilder(QueryBuilders.termQuery("status", "active"),
                    ScoreFunctionBuilders.weightFactorFunction(2.0f)));
        });

        QueryBuilder result = queryContext.getQueryBuilder();
        assertNotNull(result);
        assertTrue(result instanceof FunctionScoreQueryBuilder);
    }

    // Test addQuery with MatchAllQueryBuilder
    @Test
    public void test_addQuery_withMatchAllQuery() {
        queryContext = new QueryContext("test", false);
        queryContext.setQueryBuilder(QueryBuilders.matchAllQuery());

        queryContext.addQuery(boolQuery -> {
            boolQuery.must(QueryBuilders.termQuery("field", "value"));
        });

        QueryBuilder result = queryContext.getQueryBuilder();
        assertNotNull(result);
        assertTrue(result instanceof BoolQueryBuilder);
        assertTrue(((BoolQueryBuilder) result).hasClauses());
    }

    // Test addQuery with existing query
    @Test
    public void test_addQuery_withExistingQuery() {
        queryContext = new QueryContext("test", false);
        QueryBuilder initialQuery = QueryBuilders.termQuery("field1", "value1");
        queryContext.setQueryBuilder(initialQuery);

        queryContext.addQuery(boolQuery -> {
            boolQuery.must(QueryBuilders.termQuery("field2", "value2"));
        });

        QueryBuilder result = queryContext.getQueryBuilder();
        assertNotNull(result);
        assertTrue(result instanceof BoolQueryBuilder);
        BoolQueryBuilder boolResult = (BoolQueryBuilder) result;
        assertTrue(boolResult.hasClauses());
    }

    // Test addQuery with no clauses added
    @Test
    public void test_addQuery_withNoClauses() {
        queryContext = new QueryContext("test", false);
        QueryBuilder initialQuery = QueryBuilders.termQuery("field", "value");
        queryContext.setQueryBuilder(initialQuery);

        queryContext.addQuery(boolQuery -> {
            // Don't add any clauses
        });

        // When no clauses are added to a non-MatchAllQuery, it still wraps in BoolQuery
        QueryBuilder result = queryContext.getQueryBuilder();
        assertTrue(result instanceof BoolQueryBuilder);
        BoolQueryBuilder boolResult = (BoolQueryBuilder) result;
        assertTrue(boolResult.hasClauses());
        // Verify the original query is preserved as a must clause
        assertEquals(1, boolResult.must().size());
        assertEquals(initialQuery, boolResult.must().get(0));
    }

    // Test addSorts
    @Test
    public void test_addSorts() {
        queryContext = new QueryContext("test", false);
        assertFalse(queryContext.hasSorts());

        SortBuilder<?> sort1 = SortBuilders.fieldSort("field1");
        SortBuilder<?> sort2 = SortBuilders.fieldSort("field2");

        queryContext.addSorts(sort1, sort2);

        assertTrue(queryContext.hasSorts());
        List<SortBuilder<?>> sorts = queryContext.sortBuilders();
        assertEquals(2, sorts.size());
        assertEquals(sort1, sorts.get(0));
        assertEquals(sort2, sorts.get(1));
    }

    // Test addSorts with no sorts
    @Test
    public void test_addSorts_empty() {
        queryContext = new QueryContext("test", false);
        queryContext.addSorts();

        assertFalse(queryContext.hasSorts());
        assertTrue(queryContext.sortBuilders().isEmpty());
    }

    // Test hasSorts
    @Test
    public void test_hasSorts() {
        queryContext = new QueryContext("test", false);
        assertFalse(queryContext.hasSorts());

        queryContext.addSorts(SortBuilders.fieldSort("field"));
        assertTrue(queryContext.hasSorts());
    }

    // Test sortBuilders
    @Test
    public void test_sortBuilders() {
        queryContext = new QueryContext("test", false);
        List<SortBuilder<?>> sorts = queryContext.sortBuilders();
        assertNotNull(sorts);
        assertTrue(sorts.isEmpty());

        SortBuilder<?> sort = SortBuilders.fieldSort("field");
        queryContext.addSorts(sort);

        sorts = queryContext.sortBuilders();
        assertEquals(1, sorts.size());
        assertEquals(sort, sorts.get(0));
    }

    // Test addFieldLog with null fieldLogMap
    @Test
    public void test_addFieldLog_nullFieldLogMap() {
        queryContext = new QueryContext("test", false);
        // fieldLogMap is null when isQuery is false
        queryContext.addFieldLog("field", "text");
        // Should not throw exception
    }

    // Test addFieldLog with existing field
    @Test
    public void test_addFieldLog_existingField() {
        Map<String, List<String>> fieldLogMap = new HashMap<>();
        List<String> existingList = new ArrayList<>();
        existingList.add("existing");
        fieldLogMap.put("field", existingList);
        getMockRequest().setAttribute(Constants.FIELD_LOGS, fieldLogMap);

        queryContext = new QueryContext("test", true);
        queryContext.addFieldLog("field", "new");

        List<String> list = fieldLogMap.get("field");
        assertEquals(2, list.size());
        assertEquals("existing", list.get(0));
        assertEquals("new", list.get(1));
    }

    // Test addFieldLog with new field
    @Test
    public void test_addFieldLog_newField() {
        getMockRequest().setAttribute(Constants.FIELD_LOGS, new HashMap<String, List<String>>());

        queryContext = new QueryContext("test", true);
        queryContext.addFieldLog("field", "text");

        Map<String, List<String>> fieldLogMap = (Map<String, List<String>>) getMockRequest().getAttribute(Constants.FIELD_LOGS);
        assertTrue(fieldLogMap.containsKey("field"));
        List<String> list = fieldLogMap.get("field");
        assertEquals(1, list.size());
        assertEquals("text", list.get(0));
    }

    // Test getDefaultKeyword with null fieldLogMap
    @Test
    public void test_getDefaultKeyword_nullFieldLogMap() {
        queryContext = new QueryContext("test", false);
        List<String> keywords = queryContext.getDefaultKeyword();
        assertNotNull(keywords);
        assertTrue(keywords.isEmpty());
    }

    // Test getDefaultKeyword with existing default field
    @Test
    public void test_getDefaultKeyword_withDefaultField() {
        Map<String, List<String>> fieldLogMap = new HashMap<>();
        List<String> defaultList = new ArrayList<>();
        defaultList.add("keyword1");
        defaultList.add("keyword2");
        fieldLogMap.put(Constants.DEFAULT_FIELD, defaultList);
        getMockRequest().setAttribute(Constants.FIELD_LOGS, fieldLogMap);

        queryContext = new QueryContext("test", true);
        List<String> keywords = queryContext.getDefaultKeyword();
        assertEquals(2, keywords.size());
        assertEquals("keyword1", keywords.get(0));
        assertEquals("keyword2", keywords.get(1));
    }

    // Test getDefaultKeyword with no default field
    @Test
    public void test_getDefaultKeyword_noDefaultField() {
        getMockRequest().setAttribute(Constants.FIELD_LOGS, new HashMap<String, List<String>>());

        queryContext = new QueryContext("test", true);
        List<String> keywords = queryContext.getDefaultKeyword();
        assertNotNull(keywords);
        assertTrue(keywords.isEmpty());
    }

    // Test addHighlightedQuery with null highlightedQuerySet
    @Test
    public void test_addHighlightedQuery_nullSet() {
        queryContext = new QueryContext("test", false);
        queryContext.addHighlightedQuery("text");
        // Should not throw exception
    }

    // Test addHighlightedQuery with valid set
    @Test
    public void test_addHighlightedQuery_validSet() {
        queryContext = new QueryContext("test", true);
        queryContext.addHighlightedQuery("text1");
        queryContext.addHighlightedQuery("text2");
        queryContext.addHighlightedQuery("text1"); // Duplicate

        Set<String> highlightedSet = (Set<String>) getMockRequest().getAttribute(Constants.HIGHLIGHT_QUERIES);
        assertNotNull(highlightedSet);
        assertEquals(2, highlightedSet.size());
        assertTrue(highlightedSet.contains("text1"));
        assertTrue(highlightedSet.contains("text2"));
    }

    // Test roleQueryEnabled and skipRoleQuery
    @Test
    public void test_roleQuery() {
        queryContext = new QueryContext("test", false);

        // Default should be enabled
        assertTrue(queryContext.roleQueryEnabled());

        // Skip role query
        queryContext.skipRoleQuery();
        assertFalse(queryContext.roleQueryEnabled());
    }

    // Test getDefaultField and setDefaultField
    @Test
    public void test_defaultField() {
        queryContext = new QueryContext("test", false);
        assertNull(queryContext.getDefaultField());

        queryContext.setDefaultField("custom_field");
        assertEquals("custom_field", queryContext.getDefaultField());

        queryContext.setDefaultField(null);
        assertNull(queryContext.getDefaultField());
    }

    // Test complex scenario with multiple operations
    @Test
    public void test_complexScenario() {
        getMockRequest().setAttribute(Constants.FIELD_LOGS, new HashMap<String, List<String>>());

        queryContext = new QueryContext("allintitle:search term", true);

        // Verify initial state
        assertEquals("search term", queryContext.getQueryString());
        assertEquals("title", queryContext.getDefaultField());
        assertTrue(queryContext.roleQueryEnabled());

        // Set query builder
        QueryBuilder initialQuery = QueryBuilders.matchQuery("content", "search term");
        queryContext.setQueryBuilder(initialQuery);

        // Add boolean query
        queryContext.addQuery(boolQuery -> {
            boolQuery.filter(QueryBuilders.termQuery("type", "document"));
            boolQuery.must(QueryBuilders.rangeQuery("date").gte("2024-01-01"));
        });

        // Add function score
        queryContext.addFunctionScore(list -> {
            list.add(new FilterFunctionBuilder(QueryBuilders.termQuery("boost", "true"), ScoreFunctionBuilders.weightFactorFunction(3.0f)));
        });

        // Add sorts
        queryContext.addSorts(SortBuilders.fieldSort("_score"), SortBuilders.fieldSort("date").unmappedType("date"));

        // Add field logs
        queryContext.addFieldLog("title", "search");
        queryContext.addFieldLog("title", "term");
        queryContext.addFieldLog(Constants.DEFAULT_FIELD, "search term");

        // Add highlighted queries
        queryContext.addHighlightedQuery("search");
        queryContext.addHighlightedQuery("term");

        // Skip role query
        queryContext.skipRoleQuery();

        // Verify final state
        assertNotNull(queryContext.getQueryBuilder());
        assertTrue(queryContext.getQueryBuilder() instanceof FunctionScoreQueryBuilder);
        assertTrue(queryContext.hasSorts());
        assertEquals(2, queryContext.sortBuilders().size());
        assertFalse(queryContext.roleQueryEnabled());

        List<String> defaultKeywords = queryContext.getDefaultKeyword();
        assertEquals(1, defaultKeywords.size());
        assertEquals("search term", defaultKeywords.get(0));

        Map<String, List<String>> fieldLogMap = (Map<String, List<String>>) getMockRequest().getAttribute(Constants.FIELD_LOGS);
        assertEquals(2, fieldLogMap.get("title").size());

        Set<String> highlightedSet = (Set<String>) getMockRequest().getAttribute(Constants.HIGHLIGHT_QUERIES);
        assertEquals(2, highlightedSet.size());
    }
}