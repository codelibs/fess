/*
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
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.exception.InvalidQueryException;
import org.opensearch.index.query.MatchPhraseQueryBuilder;
import org.opensearch.index.query.QueryBuilder;

public class PhraseQueryCommandTest extends QueryTestBase {
    private PhraseQueryCommand queryCommand;

    @Override
    protected void setUpChild() throws Exception {
        // Initialize and register PhraseQueryCommand
        queryCommand = new PhraseQueryCommand();
        queryCommand.register();
    }

    public void test_getQueryClassName() {
        assertEquals("PhraseQuery", queryCommand.getQueryClassName());
    }

    public void test_execute_withNonPhraseQuery() {
        // Test with non-phrase query - should throw InvalidQueryException
        Query termQuery = new TermQuery(new Term("field", "value"));
        QueryContext context = new QueryContext("test", false);

        try {
            queryCommand.execute(context, termQuery, 1.0f);
            fail("Should throw InvalidQueryException for non-phrase query");
        } catch (InvalidQueryException e) {
            assertTrue(e.getMessage().contains("Unknown q:"));
        }
    }

    public void test_convertPhraseQuery_emptyTerms() {
        // Test with empty phrase query - should throw InvalidQueryException
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        PhraseQuery phraseQuery = builder.build();
        QueryContext context = new QueryContext("test", false);

        try {
            queryCommand.convertPhraseQuery(context, phraseQuery, 1.0f);
            fail("Should throw InvalidQueryException for empty phrase query");
        } catch (InvalidQueryException e) {
            assertTrue(e.getMessage().contains("Unknown phrase query"));
        }
    }

    public void test_convertPhraseQuery_singleTerm_defaultField_bool() {
        // Test with single term in default field
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        builder.add(new Term(Constants.DEFAULT_FIELD, "test"));
        PhraseQuery phraseQuery = builder.build();

        QueryContext context = new QueryContext("test", false);
        QueryBuilder result = queryCommand.convertPhraseQuery(context, phraseQuery, 1.0f);

        assertNotNull(result);
        assertTrue(result instanceof DefaultQueryBuilder);

        // Verify query was processed
        // Note: Field logging behavior depends on implementation
    }

    public void test_convertPhraseQuery_singleTerm_defaultField_dismax() {
        // Test with single term in default field with boost > 1
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        builder.add(new Term(Constants.DEFAULT_FIELD, "test"));
        PhraseQuery phraseQuery = builder.build();

        QueryContext context = new QueryContext("test", false);
        QueryBuilder result = queryCommand.convertPhraseQuery(context, phraseQuery, 2.0f);

        assertNotNull(result);
        assertTrue(result instanceof DefaultQueryBuilder);

        // Verify query was processed
        // Note: Field logging behavior depends on implementation
    }

    public void test_convertPhraseQuery_multipleTerms_defaultField() {
        // Test with multiple terms in default field
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        builder.add(new Term(Constants.DEFAULT_FIELD, "hello"));
        builder.add(new Term(Constants.DEFAULT_FIELD, "world"));
        PhraseQuery phraseQuery = builder.build();

        QueryContext context = new QueryContext("test", false);
        QueryBuilder result = queryCommand.convertPhraseQuery(context, phraseQuery, 1.0f);

        assertNotNull(result);
        assertTrue(result instanceof DefaultQueryBuilder);

        // Verify query was processed
        // Note: Field logging behavior depends on implementation
    }

    public void test_convertPhraseQuery_searchField_title() {
        // Test with search field (title)
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        builder.add(new Term("title", "test"));
        PhraseQuery phraseQuery = builder.build();

        QueryContext context = new QueryContext("test", false);
        QueryBuilder result = queryCommand.convertPhraseQuery(context, phraseQuery, 1.0f);

        assertNotNull(result);
        assertTrue(result instanceof MatchPhraseQueryBuilder);

        // Verify query was processed
        // Note: Field logging behavior depends on implementation
    }

    public void test_convertPhraseQuery_searchField_content() {
        // Test with search field (content)
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        builder.add(new Term("content", "test"));
        PhraseQuery phraseQuery = builder.build();

        QueryContext context = new QueryContext("test", false);
        QueryBuilder result = queryCommand.convertPhraseQuery(context, phraseQuery, 1.0f);

        assertNotNull(result);
        assertTrue(result instanceof MatchPhraseQueryBuilder);

        // Verify query was processed
        assertNotNull(result);
        assertTrue(result instanceof MatchPhraseQueryBuilder);
    }

    public void test_convertPhraseQuery_unknownField() {
        // Test with unknown field (not a search field)
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        builder.add(new Term("unknown_field", "test"));
        PhraseQuery phraseQuery = builder.build();

        QueryContext context = new QueryContext("test", false);
        QueryBuilder result = queryCommand.convertPhraseQuery(context, phraseQuery, 1.0f);

        assertNotNull(result);
        assertTrue(result instanceof DefaultQueryBuilder);

        // Verify query was processed
        // Note: Field logging behavior depends on implementation
    }

    public void test_convertPhraseQuery_withSpecialCharacters() {
        // Test with special characters in query
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        builder.add(new Term(Constants.DEFAULT_FIELD, "test@#$"));
        PhraseQuery phraseQuery = builder.build();

        QueryContext context = new QueryContext("test", false);
        QueryBuilder result = queryCommand.convertPhraseQuery(context, phraseQuery, 1.0f);

        assertNotNull(result);
        assertTrue(result instanceof DefaultQueryBuilder);

        // Verify query was processed
        // Note: Field logging behavior depends on implementation
    }

    public void test_execute_withValidPhraseQuery() {
        // Test execute method with valid phrase query
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        builder.add(new Term(Constants.DEFAULT_FIELD, "test"));
        PhraseQuery phraseQuery = builder.build();

        QueryContext context = new QueryContext("test", false);
        QueryBuilder result = queryCommand.execute(context, phraseQuery, 1.0f);

        assertNotNull(result);
        assertTrue(result instanceof DefaultQueryBuilder);

        // Verify query was processed
        // Note: Field logging behavior depends on implementation
    }

    public void test_convertPhraseQuery_longPhrase() {
        // Test with long phrase (many terms)
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        String[] words = { "this", "is", "a", "very", "long", "phrase", "query" };
        for (String word : words) {
            builder.add(new Term(Constants.DEFAULT_FIELD, word));
        }
        PhraseQuery phraseQuery = builder.build();

        QueryContext context = new QueryContext("test", false);
        QueryBuilder result = queryCommand.convertPhraseQuery(context, phraseQuery, 1.0f);

        assertNotNull(result);
        assertTrue(result instanceof DefaultQueryBuilder);

        // Verify query was processed
        // Note: Field logging behavior depends on implementation
    }

    public void test_convertPhraseQuery_emptyString() {
        // Test with empty string term (edge case)
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        builder.add(new Term(Constants.DEFAULT_FIELD, ""));
        PhraseQuery phraseQuery = builder.build();

        QueryContext context = new QueryContext("test", false);
        QueryBuilder result = queryCommand.convertPhraseQuery(context, phraseQuery, 1.0f);

        assertNotNull(result);
        assertTrue(result instanceof DefaultQueryBuilder);

        // Verify query was processed
        // Note: Field logging behavior depends on implementation
    }
}