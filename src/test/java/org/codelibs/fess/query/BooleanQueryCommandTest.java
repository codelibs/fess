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

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.exception.InvalidQueryException;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.util.DfTypeUtil;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.TermQueryBuilder;
import org.junit.jupiter.api.Test;

public class BooleanQueryCommandTest extends QueryTestBase {

    private BooleanQueryCommand booleanQueryCommand;

    @Override
    protected void setUpChild() throws Exception {
        // Register other query commands that might be needed
        new TermQueryCommand().register();
        new MatchAllQueryCommand().register();

        // Initialize and register BooleanQueryCommand
        booleanQueryCommand = new BooleanQueryCommand();
        booleanQueryCommand.register();
    }

    // Test getQueryClassName method
    @Test
    public void test_getQueryClassName() {
        assertEquals("BooleanQuery", booleanQueryCommand.getQueryClassName());
    }

    // Test execute method with valid BooleanQuery
    @Test
    public void test_execute_withBooleanQuery() {
        // Create a simple BooleanQuery with MUST clause
        BooleanQuery.Builder boolQueryBuilder = new BooleanQuery.Builder();
        TermQuery termQuery = new TermQuery(new Term("field", "value"));
        boolQueryBuilder.add(termQuery, BooleanClause.Occur.MUST);
        BooleanQuery booleanQuery = boolQueryBuilder.build();

        QueryContext context = new QueryContext("test", false);
        QueryBuilder result = booleanQueryCommand.execute(context, booleanQuery, 1.0f);

        assertNotNull(result);
        assertTrue(result instanceof BoolQueryBuilder);

        BoolQueryBuilder boolResult = (BoolQueryBuilder) result;
        assertTrue(boolResult.hasClauses());
    }

    // Test execute method with non-BooleanQuery (should throw exception)
    @Test
    public void test_execute_withNonBooleanQuery() {
        TermQuery termQuery = new TermQuery(new Term("field", "value"));
        QueryContext context = new QueryContext("test", false);

        try {
            booleanQueryCommand.execute(context, termQuery, 1.0f);
            fail("Should throw InvalidQueryException");
        } catch (InvalidQueryException e) {
            assertTrue(e.getMessage().contains("Unknown q:"));
        }
    }

    // Test convertBooleanQuery with MUST clause
    @Test
    public void test_convertBooleanQuery_withMustClause() {
        BooleanQuery.Builder boolQueryBuilder = new BooleanQuery.Builder();
        TermQuery termQuery = new TermQuery(new Term("field", "value"));
        boolQueryBuilder.add(termQuery, BooleanClause.Occur.MUST);
        BooleanQuery booleanQuery = boolQueryBuilder.build();

        QueryContext context = new QueryContext("test", false);
        QueryBuilder result = booleanQueryCommand.convertBooleanQuery(context, booleanQuery, 1.0f);

        assertNotNull(result);
        assertTrue(result instanceof BoolQueryBuilder);

        BoolQueryBuilder boolResult = (BoolQueryBuilder) result;
        assertTrue(boolResult.hasClauses());
        assertEquals(1, boolResult.must().size());
    }

    // Test convertBooleanQuery with SHOULD clause
    @Test
    public void test_convertBooleanQuery_withShouldClause() {
        BooleanQuery.Builder boolQueryBuilder = new BooleanQuery.Builder();
        TermQuery termQuery1 = new TermQuery(new Term("field1", "value1"));
        TermQuery termQuery2 = new TermQuery(new Term("field2", "value2"));
        boolQueryBuilder.add(termQuery1, BooleanClause.Occur.SHOULD);
        boolQueryBuilder.add(termQuery2, BooleanClause.Occur.SHOULD);
        BooleanQuery booleanQuery = boolQueryBuilder.build();

        QueryContext context = new QueryContext("test", false);
        QueryBuilder result = booleanQueryCommand.convertBooleanQuery(context, booleanQuery, 1.0f);

        assertNotNull(result);
        assertTrue(result instanceof BoolQueryBuilder);

        BoolQueryBuilder boolResult = (BoolQueryBuilder) result;
        assertTrue(boolResult.hasClauses());
        assertEquals(2, boolResult.should().size());
    }

    // Test convertBooleanQuery with MUST_NOT clause
    @Test
    public void test_convertBooleanQuery_withMustNotClause() {
        BooleanQuery.Builder boolQueryBuilder = new BooleanQuery.Builder();
        TermQuery termQuery = new TermQuery(new Term("field", "value"));
        boolQueryBuilder.add(termQuery, BooleanClause.Occur.MUST_NOT);
        // Add a MUST clause to make the query valid
        TermQuery termQuery2 = new TermQuery(new Term("field2", "value2"));
        boolQueryBuilder.add(termQuery2, BooleanClause.Occur.MUST);
        BooleanQuery booleanQuery = boolQueryBuilder.build();

        QueryContext context = new QueryContext("test", false);
        QueryBuilder result = booleanQueryCommand.convertBooleanQuery(context, booleanQuery, 1.0f);

        assertNotNull(result);
        assertTrue(result instanceof BoolQueryBuilder);

        BoolQueryBuilder boolResult = (BoolQueryBuilder) result;
        assertTrue(boolResult.hasClauses());
        assertEquals(1, boolResult.mustNot().size());
        assertEquals(1, boolResult.must().size());
    }

    // Test convertBooleanQuery with mixed clauses
    @Test
    public void test_convertBooleanQuery_withMixedClauses() {
        BooleanQuery.Builder boolQueryBuilder = new BooleanQuery.Builder();
        TermQuery termQuery1 = new TermQuery(new Term("field1", "value1"));
        TermQuery termQuery2 = new TermQuery(new Term("field2", "value2"));
        TermQuery termQuery3 = new TermQuery(new Term("field3", "value3"));

        boolQueryBuilder.add(termQuery1, BooleanClause.Occur.MUST);
        boolQueryBuilder.add(termQuery2, BooleanClause.Occur.SHOULD);
        boolQueryBuilder.add(termQuery3, BooleanClause.Occur.MUST_NOT);
        BooleanQuery booleanQuery = boolQueryBuilder.build();

        QueryContext context = new QueryContext("test", false);
        QueryBuilder result = booleanQueryCommand.convertBooleanQuery(context, booleanQuery, 1.0f);

        assertNotNull(result);
        assertTrue(result instanceof BoolQueryBuilder);

        BoolQueryBuilder boolResult = (BoolQueryBuilder) result;
        assertTrue(boolResult.hasClauses());
        assertEquals(1, boolResult.must().size());
        assertEquals(1, boolResult.should().size());
        assertEquals(1, boolResult.mustNot().size());
    }

    // Test convertBooleanQuery with empty BooleanQuery
    @Test
    public void test_convertBooleanQuery_withEmptyQuery() {
        BooleanQuery.Builder boolQueryBuilder = new BooleanQuery.Builder();
        BooleanQuery booleanQuery = boolQueryBuilder.build();

        QueryContext context = new QueryContext("test", false);
        QueryBuilder result = booleanQueryCommand.convertBooleanQuery(context, booleanQuery, 1.0f);

        assertNull(result);
    }

    // Test convertBooleanQuery with null queryBuilder from processor
    @Test
    public void test_convertBooleanQuery_withNullQueryBuilder() {
        // Create processor that returns null
        QueryProcessor nullProcessor = new QueryProcessor() {
            @Override
            public QueryBuilder execute(QueryContext context, Query query, float boost) {
                return null;
            }
        };
        ComponentUtil.register(nullProcessor, "queryProcessor");

        BooleanQuery.Builder boolQueryBuilder = new BooleanQuery.Builder();
        TermQuery termQuery = new TermQuery(new Term("field", "value"));
        boolQueryBuilder.add(termQuery, BooleanClause.Occur.MUST);
        BooleanQuery booleanQuery = boolQueryBuilder.build();

        QueryContext context = new QueryContext("test", false);
        QueryBuilder result = booleanQueryCommand.convertBooleanQuery(context, booleanQuery, 1.0f);

        // Should return null when no clauses are added
        assertNull(result);
    }

    // Test convertBooleanQuery with nested BooleanQuery
    @Test
    public void test_convertBooleanQuery_withNestedBooleanQuery() {
        // Create inner BooleanQuery
        BooleanQuery.Builder innerBoolQueryBuilder = new BooleanQuery.Builder();
        TermQuery innerTermQuery = new TermQuery(new Term("innerField", "innerValue"));
        innerBoolQueryBuilder.add(innerTermQuery, BooleanClause.Occur.MUST);
        BooleanQuery innerBooleanQuery = innerBoolQueryBuilder.build();

        // Create outer BooleanQuery
        BooleanQuery.Builder outerBoolQueryBuilder = new BooleanQuery.Builder();
        outerBoolQueryBuilder.add(innerBooleanQuery, BooleanClause.Occur.SHOULD);
        TermQuery outerTermQuery = new TermQuery(new Term("outerField", "outerValue"));
        outerBoolQueryBuilder.add(outerTermQuery, BooleanClause.Occur.MUST);
        BooleanQuery outerBooleanQuery = outerBoolQueryBuilder.build();

        QueryContext context = new QueryContext("test", false);
        QueryBuilder result = booleanQueryCommand.convertBooleanQuery(context, outerBooleanQuery, 1.0f);

        assertNotNull(result);
        assertTrue(result instanceof BoolQueryBuilder);

        BoolQueryBuilder boolResult = (BoolQueryBuilder) result;
        assertTrue(boolResult.hasClauses());
        assertEquals(1, boolResult.must().size());
        assertEquals(1, boolResult.should().size());
    }

    // Test convertBooleanQuery with boost value
    @Test
    public void test_convertBooleanQuery_withBoost() {
        BooleanQuery.Builder boolQueryBuilder = new BooleanQuery.Builder();
        TermQuery termQuery = new TermQuery(new Term("field", "value"));
        boolQueryBuilder.add(termQuery, BooleanClause.Occur.MUST);
        BooleanQuery booleanQuery = boolQueryBuilder.build();

        QueryContext context = new QueryContext("test", false);
        float boost = 2.5f;
        QueryBuilder result = booleanQueryCommand.convertBooleanQuery(context, booleanQuery, boost);

        assertNotNull(result);
        assertTrue(result instanceof BoolQueryBuilder);

        BoolQueryBuilder boolResult = (BoolQueryBuilder) result;
        assertTrue(boolResult.hasClauses());
    }

    // Test with FILTER occur type (which maps to default case)
    @Test
    public void test_convertBooleanQuery_withFilterClause() {
        BooleanQuery.Builder boolQueryBuilder = new BooleanQuery.Builder();
        TermQuery termQuery1 = new TermQuery(new Term("field1", "value1"));
        TermQuery termQuery2 = new TermQuery(new Term("field2", "value2"));

        boolQueryBuilder.add(termQuery1, BooleanClause.Occur.MUST);
        boolQueryBuilder.add(termQuery2, BooleanClause.Occur.FILTER);
        BooleanQuery booleanQuery = boolQueryBuilder.build();

        QueryContext context = new QueryContext("test", false);
        QueryBuilder result = booleanQueryCommand.convertBooleanQuery(context, booleanQuery, 1.0f);

        assertNotNull(result);
        assertTrue(result instanceof BoolQueryBuilder);

        BoolQueryBuilder boolResult = (BoolQueryBuilder) result;
        assertTrue(boolResult.hasClauses());
        // FILTER should be ignored (default case in switch)
        assertEquals(1, boolResult.must().size());
    }

    // Test execute with MatchAllDocsQuery wrapped in BooleanQuery
    @Test
    public void test_execute_withMatchAllDocsQuery() {
        BooleanQuery.Builder boolQueryBuilder = new BooleanQuery.Builder();
        MatchAllDocsQuery matchAllQuery = new MatchAllDocsQuery();
        boolQueryBuilder.add(matchAllQuery, BooleanClause.Occur.MUST);
        BooleanQuery booleanQuery = boolQueryBuilder.build();

        QueryContext context = new QueryContext("test", false);
        QueryBuilder result = booleanQueryCommand.execute(context, booleanQuery, 1.0f);

        assertNotNull(result);
        assertTrue(result instanceof BoolQueryBuilder);
    }

    // Test to ensure high coverage of edge cases
    @Test
    public void test_convertBooleanQuery_multipleShouldClauses() {
        BooleanQuery.Builder boolQueryBuilder = new BooleanQuery.Builder();

        // Add multiple SHOULD clauses
        for (int i = 0; i < 5; i++) {
            TermQuery termQuery = new TermQuery(new Term("field" + i, "value" + i));
            boolQueryBuilder.add(termQuery, BooleanClause.Occur.SHOULD);
        }

        BooleanQuery booleanQuery = boolQueryBuilder.build();
        QueryContext context = new QueryContext("test", false);
        QueryBuilder result = booleanQueryCommand.convertBooleanQuery(context, booleanQuery, 1.0f);

        assertNotNull(result);
        assertTrue(result instanceof BoolQueryBuilder);

        BoolQueryBuilder boolResult = (BoolQueryBuilder) result;
        assertEquals(5, boolResult.should().size());
    }

    // Test complex query with all clause types
    @Test
    public void test_convertBooleanQuery_complexQuery() {
        BooleanQuery.Builder boolQueryBuilder = new BooleanQuery.Builder();

        // Add multiple clauses of each type
        for (int i = 0; i < 3; i++) {
            boolQueryBuilder.add(new TermQuery(new Term("must" + i, "value" + i)), BooleanClause.Occur.MUST);
            boolQueryBuilder.add(new TermQuery(new Term("should" + i, "value" + i)), BooleanClause.Occur.SHOULD);
            boolQueryBuilder.add(new TermQuery(new Term("mustNot" + i, "value" + i)), BooleanClause.Occur.MUST_NOT);
        }

        BooleanQuery booleanQuery = boolQueryBuilder.build();
        QueryContext context = new QueryContext("test", false);
        QueryBuilder result = booleanQueryCommand.convertBooleanQuery(context, booleanQuery, 1.0f);

        assertNotNull(result);
        assertTrue(result instanceof BoolQueryBuilder);

        BoolQueryBuilder boolResult = (BoolQueryBuilder) result;
        assertEquals(3, boolResult.must().size());
        assertEquals(3, boolResult.should().size());
        assertEquals(3, boolResult.mustNot().size());
    }

}