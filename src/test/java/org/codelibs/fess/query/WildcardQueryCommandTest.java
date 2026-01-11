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

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.exception.InvalidQueryException;

import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.WildcardQueryBuilder;
import org.junit.jupiter.api.Test;

public class WildcardQueryCommandTest extends QueryTestBase {
    private WildcardQueryCommand queryCommand;

    @Override
    protected void setUpChild() throws Exception {
        // Initialize and register WildcardQueryCommand
        queryCommand = new WildcardQueryCommand();
        queryCommand.register();
    }

    @Test
    public void test_convertWildcardQuery_defaultField() throws Exception {
        // Test with default field
        QueryContext queryContext = new QueryContext("test*", false);
        WildcardQuery wildcardQuery = new WildcardQuery(new Term(Constants.DEFAULT_FIELD, "test*"));

        QueryBuilder queryBuilder = queryCommand.convertWildcardQuery(queryContext, wildcardQuery, 1.0f);
        assertNotNull(queryBuilder);
        assertTrue(queryBuilder instanceof DefaultQueryBuilder);

        // Query was processed successfully
    }

    @Test
    public void test_convertWildcardQuery_defaultField_dismax() throws Exception {
        // Test with default field
        QueryContext queryContext = new QueryContext("*test", false);
        WildcardQuery wildcardQuery = new WildcardQuery(new Term(Constants.DEFAULT_FIELD, "*test"));

        QueryBuilder queryBuilder = queryCommand.convertWildcardQuery(queryContext, wildcardQuery, 2.0f);
        assertNotNull(queryBuilder);
        assertTrue(queryBuilder instanceof DefaultQueryBuilder);

        // Query was processed successfully
    }

    @Test
    public void test_convertWildcardQuery_searchField() throws Exception {
        // Test with search field (title)
        QueryContext queryContext = new QueryContext("title:test*", false);
        WildcardQuery wildcardQuery = new WildcardQuery(new Term("title", "test*"));

        QueryBuilder queryBuilder = queryCommand.convertWildcardQuery(queryContext, wildcardQuery, 1.5f);
        assertNotNull(queryBuilder);
        assertTrue(queryBuilder instanceof WildcardQueryBuilder);

        // Query was processed successfully

        // Query was processed successfully
        WildcardQueryBuilder wqb = (WildcardQueryBuilder) queryBuilder;
        assertEquals("title", wqb.fieldName());
    }

    @Test
    public void test_convertWildcardQuery_nonSearchField() throws Exception {
        // Test with DEFAULT_FIELD when field is not a search field
        QueryContext queryContext = new QueryContext("test", false);
        WildcardQuery wildcardQuery = new WildcardQuery(new Term(Constants.DEFAULT_FIELD, "test"));

        QueryBuilder queryBuilder = queryCommand.convertWildcardQuery(queryContext, wildcardQuery, 1.0f);
        assertNotNull(queryBuilder);
        assertTrue(queryBuilder instanceof DefaultQueryBuilder);

        // Query was processed successfully

        // Query was processed successfully
    }

    @Test
    public void test_convertWildcardQuery_withBothWildcards() throws Exception {
        // Test with both wildcards already present
        QueryContext queryContext = new QueryContext("*test*", false);
        WildcardQuery wildcardQuery = new WildcardQuery(new Term(Constants.DEFAULT_FIELD, "*test*"));

        QueryBuilder queryBuilder = queryCommand.convertWildcardQuery(queryContext, wildcardQuery, 1.0f);
        assertNotNull(queryBuilder);

        // Query was processed successfully
    }

    @Test
    public void test_convertWildcardQuery_emptyHighlight() throws Exception {
        // Test with only wildcards (no highlight text)
        QueryContext queryContext = new QueryContext("***", false);
        WildcardQuery wildcardQuery = new WildcardQuery(new Term(Constants.DEFAULT_FIELD, "***"));

        QueryBuilder queryBuilder = queryCommand.convertWildcardQuery(queryContext, wildcardQuery, 1.0f);
        assertNotNull(queryBuilder);

        // Should not add empty highlight
        assertTrue(queryContext.getHighlightedQuerySet().isEmpty());
    }

    @Test
    public void test_execute_withWildcardQuery() throws Exception {
        QueryContext queryContext = new QueryContext("test*", false);
        WildcardQuery wildcardQuery = new WildcardQuery(new Term("title", "test*"));

        QueryBuilder queryBuilder = queryCommand.execute(queryContext, wildcardQuery, 1.0f);
        assertNotNull(queryBuilder);
    }

    @Test
    public void test_execute_withNonWildcardQuery() {
        QueryContext queryContext = new QueryContext("test", false);
        TermQuery termQuery = new TermQuery(new Term("title", "test"));

        try {
            queryCommand.execute(queryContext, termQuery, 1.0f);
            fail("Should throw InvalidQueryException");
        } catch (InvalidQueryException e) {
            // Expected exception
            assertTrue(e.getMessage().contains("Unknown q:"));
        }
    }

    @Test
    public void test_toLowercaseWildcard_enabled() {
        queryCommand.setLowercaseWildcard(true);

        assertEquals("test", queryCommand.toLowercaseWildcard("TEST"));
        assertEquals("test*", queryCommand.toLowercaseWildcard("TEST*"));
        assertEquals("*test*", queryCommand.toLowercaseWildcard("*TEST*"));
        assertEquals("te?t", queryCommand.toLowercaseWildcard("TE?T"));
        assertEquals("test123", queryCommand.toLowercaseWildcard("TEST123"));
        assertEquals("", queryCommand.toLowercaseWildcard(""));
    }

    @Test
    public void test_toLowercaseWildcard_disabled() {
        queryCommand.setLowercaseWildcard(false);

        assertEquals("TEST", queryCommand.toLowercaseWildcard("TEST"));
        assertEquals("TEST*", queryCommand.toLowercaseWildcard("TEST*"));
        assertEquals("*TEST*", queryCommand.toLowercaseWildcard("*TEST*"));
        assertEquals("TE?T", queryCommand.toLowercaseWildcard("TE?T"));
        assertEquals("TEST123", queryCommand.toLowercaseWildcard("TEST123"));
        assertEquals("", queryCommand.toLowercaseWildcard(""));
    }

    @Test
    public void test_toLowercaseWildcard_mixedCase() {
        queryCommand.setLowercaseWildcard(true);

        assertEquals("testcase", queryCommand.toLowercaseWildcard("TestCase"));
        assertEquals("test_case", queryCommand.toLowercaseWildcard("Test_Case"));
        assertEquals("test-case", queryCommand.toLowercaseWildcard("Test-Case"));
    }

    @Test
    public void test_toLowercaseWildcard_internationalCharacters() {
        queryCommand.setLowercaseWildcard(true);

        // Test with various international characters
        assertEquals("äöü", queryCommand.toLowercaseWildcard("ÄÖÜ"));
        assertEquals("日本語", queryCommand.toLowercaseWildcard("日本語")); // Japanese doesn't change
        assertEquals("test日本語", queryCommand.toLowercaseWildcard("TEST日本語"));
    }

    @Test
    public void test_getQueryClassName() {
        assertEquals("WildcardQuery", queryCommand.getQueryClassName());
    }

    @Test
    public void test_setLowercaseWildcard() {
        // Test default value
        assertTrue(queryCommand.lowercaseWildcard);

        // Test setting to false
        queryCommand.setLowercaseWildcard(false);
        assertFalse(queryCommand.lowercaseWildcard);

        // Test setting back to true
        queryCommand.setLowercaseWildcard(true);
        assertTrue(queryCommand.lowercaseWildcard);
    }

    @Test
    public void test_convertWildcardQuery_withBoost() throws Exception {
        // Test with different boost values
        QueryContext queryContext = new QueryContext("test*", false);
        WildcardQuery wildcardQuery = new WildcardQuery(new Term("title", "test*"));

        // Test with boost = 0.5
        QueryBuilder queryBuilder1 = queryCommand.convertWildcardQuery(queryContext, wildcardQuery, 0.5f);
        assertNotNull(queryBuilder1);

        // Test with boost = 2.0
        QueryBuilder queryBuilder2 = queryCommand.convertWildcardQuery(queryContext, wildcardQuery, 2.0f);
        assertNotNull(queryBuilder2);

        // Test with boost = 0
        QueryBuilder queryBuilder3 = queryCommand.convertWildcardQuery(queryContext, wildcardQuery, 0.0f);
        assertNotNull(queryBuilder3);
    }

    @Test
    public void test_convertWildcardQuery_withSpecialCharacters() throws Exception {
        // Test with special wildcard characters
        QueryContext queryContext1 = new QueryContext("te?t*", false);
        WildcardQuery wildcardQuery1 = new WildcardQuery(new Term("title", "te?t*"));

        QueryBuilder queryBuilder1 = queryCommand.convertWildcardQuery(queryContext1, wildcardQuery1, 1.0f);
        assertNotNull(queryBuilder1);
        assertTrue(queryBuilder1 instanceof WildcardQueryBuilder);
        // Query was processed successfully

        // Test with multiple wildcards
        QueryContext queryContext2 = new QueryContext("*te*st*", false);
        WildcardQuery wildcardQuery2 = new WildcardQuery(new Term("content", "*te*st*"));

        QueryBuilder queryBuilder2 = queryCommand.convertWildcardQuery(queryContext2, wildcardQuery2, 1.0f);
        assertNotNull(queryBuilder2);
        assertTrue(queryBuilder2 instanceof WildcardQueryBuilder);
        // Query was processed successfully
    }

    @Test
    public void test_convertWildcardQuery_caseInsensitive() throws Exception {
        // Test lowercase conversion with wildcard enabled (default)
        QueryContext queryContext1 = new QueryContext("TEST*", false);
        WildcardQuery wildcardQuery1 = new WildcardQuery(new Term("title", "TEST*"));

        queryCommand.setLowercaseWildcard(true);
        QueryBuilder queryBuilder1 = queryCommand.convertWildcardQuery(queryContext1, wildcardQuery1, 1.0f);
        assertNotNull(queryBuilder1);

        // Query was processed successfully
        WildcardQueryBuilder wqb1 = (WildcardQueryBuilder) queryBuilder1;
        assertEquals("test*", wqb1.value());

        // Test with lowercase disabled
        QueryContext queryContext2 = new QueryContext("TEST*", false);
        WildcardQuery wildcardQuery2 = new WildcardQuery(new Term("title", "TEST*"));

        queryCommand.setLowercaseWildcard(false);
        QueryBuilder queryBuilder2 = queryCommand.convertWildcardQuery(queryContext2, wildcardQuery2, 1.0f);
        assertNotNull(queryBuilder2);

        // The term should remain uppercase
        String queryString2 = queryBuilder2.toString();
        assertTrue(queryString2.contains("TEST*") || queryString2.contains("TEST"));
    }
}
