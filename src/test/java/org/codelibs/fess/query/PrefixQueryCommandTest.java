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
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.util.DfTypeUtil;
import org.opensearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.opensearch.index.query.PrefixQueryBuilder;
import org.opensearch.index.query.QueryBuilder;

public class PrefixQueryCommandTest extends QueryTestBase {
    private PrefixQueryCommand queryCommand;
    private QueryFieldConfig queryFieldConfig;

    @Override
    protected void setUpChild() throws Exception {
        // Get the queryProcessor and queryFieldConfig that were registered in parent class
        queryProcessor = ComponentUtil.getComponent("queryProcessor");
        queryFieldConfig = ComponentUtil.getComponent("queryFieldConfig");

        // Initialize and register PrefixQueryCommand
        queryCommand = new PrefixQueryCommand();
        queryCommand.register();
    }

    private void setNotAnalyzedFields(final String... fields) {
        Set<String> notAnalyzedFieldSet = new HashSet<>();
        for (String field : fields) {
            notAnalyzedFieldSet.add(field);
        }
        queryFieldConfig.notAnalyzedFieldSet = notAnalyzedFieldSet;
    }

    public void test_getQueryClassName() {
        assertEquals("PrefixQuery", queryCommand.getQueryClassName());
    }

    public void test_execute_withInvalidQuery() {
        QueryContext context = new QueryContext("test", false);
        TermQuery termQuery = new TermQuery(new Term("field", "value"));

        try {
            queryProcessor.execute(context, termQuery, 1.0f);
            fail();
        } catch (InvalidQueryException e) {
            // expected
            assertTrue(e.getMessage().contains("Unknown q:"));
        }
    }

    public void test_convertPrefixQuery_withDefaultField() throws Exception {
        QueryContext context = new QueryContext("test", false);
        PrefixQuery prefixQuery = new PrefixQuery(new Term(Constants.DEFAULT_FIELD, "test"));

        QueryBuilder builder = queryProcessor.execute(context, prefixQuery, 1.0f);

        assertNotNull(builder);
        assertTrue(builder instanceof DefaultQueryBuilder);

        // Query was processed successfully
    }

    public void test_convertPrefixQuery_withDefaultField_dismax() throws Exception {
        QueryContext context = new QueryContext("test", false);
        PrefixQuery prefixQuery = new PrefixQuery(new Term(Constants.DEFAULT_FIELD, "test"));

        QueryBuilder builder = queryProcessor.execute(context, prefixQuery, 2.0f);

        assertNotNull(builder);
        assertTrue(builder instanceof DefaultQueryBuilder);

        // Query was processed successfully with boost
    }

    public void test_convertPrefixQuery_withSearchField() throws Exception {
        QueryContext context = new QueryContext("test", false);
        PrefixQuery prefixQuery = new PrefixQuery(new Term("title", "test"));

        QueryBuilder builder = queryProcessor.execute(context, prefixQuery, 1.0f);

        assertNotNull(builder);
        assertTrue(builder instanceof MatchPhrasePrefixQueryBuilder);

        MatchPhrasePrefixQueryBuilder mpqb = (MatchPhrasePrefixQueryBuilder) builder;
        assertEquals("title", mpqb.fieldName());
        assertEquals("test", mpqb.value());

        // Query was processed successfully
    }

    public void test_convertPrefixQuery_withNotAnalyzedField() throws Exception {
        setNotAnalyzedFields("url", "site");

        QueryContext context = new QueryContext("test", false);
        PrefixQuery prefixQuery = new PrefixQuery(new Term("url", "http"));

        QueryBuilder builder = queryProcessor.execute(context, prefixQuery, 1.5f);

        assertNotNull(builder);
        assertTrue(builder instanceof PrefixQueryBuilder);

        PrefixQueryBuilder pqb = (PrefixQueryBuilder) builder;
        assertEquals("url", pqb.fieldName());
        assertEquals("http", pqb.value());

        // Query was processed with boost
        assertEquals(1.5f, pqb.boost());
    }

    public void test_convertPrefixQuery_withNonSearchField() throws Exception {
        QueryContext context = new QueryContext("test", false);
        PrefixQuery prefixQuery = new PrefixQuery(new Term("unknown_field", "test"));

        QueryBuilder builder = queryProcessor.execute(context, prefixQuery, 1.0f);

        assertNotNull(builder);
        assertTrue(builder instanceof DefaultQueryBuilder);

        // Query was processed successfully
    }

    public void test_toLowercaseWildcard_enabled() {
        queryCommand.setLowercaseWildcard(true);

        assertEquals("test", queryCommand.toLowercaseWildcard("TEST"));
        assertEquals("hello", queryCommand.toLowercaseWildcard("Hello"));
        assertEquals("mixed123", queryCommand.toLowercaseWildcard("MiXeD123"));
        assertEquals("already_lower", queryCommand.toLowercaseWildcard("already_lower"));
    }

    public void test_toLowercaseWildcard_disabled() {
        queryCommand.setLowercaseWildcard(false);

        assertEquals("TEST", queryCommand.toLowercaseWildcard("TEST"));
        assertEquals("Hello", queryCommand.toLowercaseWildcard("Hello"));
        assertEquals("MiXeD123", queryCommand.toLowercaseWildcard("MiXeD123"));
        assertEquals("already_lower", queryCommand.toLowercaseWildcard("already_lower"));
    }

    public void test_convertPrefixQuery_withUppercasePrefix_lowercaseEnabled() throws Exception {
        queryCommand.setLowercaseWildcard(true);

        QueryContext context = new QueryContext("test", false);
        PrefixQuery prefixQuery = new PrefixQuery(new Term("title", "TEST"));

        QueryBuilder builder = queryProcessor.execute(context, prefixQuery, 1.0f);

        assertNotNull(builder);
        assertTrue(builder instanceof MatchPhrasePrefixQueryBuilder);

        MatchPhrasePrefixQueryBuilder mpqb = (MatchPhrasePrefixQueryBuilder) builder;
        assertEquals("test", mpqb.value()); // Should be lowercase
    }

    public void test_convertPrefixQuery_withUppercasePrefix_lowercaseDisabled() throws Exception {
        queryCommand.setLowercaseWildcard(false);

        QueryContext context = new QueryContext("test", false);
        PrefixQuery prefixQuery = new PrefixQuery(new Term("title", "TEST"));

        QueryBuilder builder = queryProcessor.execute(context, prefixQuery, 1.0f);

        assertNotNull(builder);
        assertTrue(builder instanceof MatchPhrasePrefixQueryBuilder);

        MatchPhrasePrefixQueryBuilder mpqb = (MatchPhrasePrefixQueryBuilder) builder;
        assertEquals("TEST", mpqb.value()); // Should remain uppercase
    }

    public void test_convertPrefixQuery_withEmptyPrefix() throws Exception {
        QueryContext context = new QueryContext("test", false);
        PrefixQuery prefixQuery = new PrefixQuery(new Term("title", ""));

        QueryBuilder builder = queryProcessor.execute(context, prefixQuery, 1.0f);

        assertNotNull(builder);
        // Empty prefix should still create a valid query
        assertTrue(builder instanceof MatchPhrasePrefixQueryBuilder);

        MatchPhrasePrefixQueryBuilder mpqb = (MatchPhrasePrefixQueryBuilder) builder;
        assertEquals("", mpqb.value());
    }

    public void test_convertPrefixQuery_withSpecialCharacters() throws Exception {
        QueryContext context = new QueryContext("test", false);
        PrefixQuery prefixQuery = new PrefixQuery(new Term("title", "test-123_abc"));

        QueryBuilder builder = queryProcessor.execute(context, prefixQuery, 1.0f);

        assertNotNull(builder);
        assertTrue(builder instanceof MatchPhrasePrefixQueryBuilder);

        MatchPhrasePrefixQueryBuilder mpqb = (MatchPhrasePrefixQueryBuilder) builder;
        assertEquals("test-123_abc", mpqb.value());

        // Query was processed successfully
    }

    public void test_convertPrefixQuery_withHighBoost() throws Exception {
        QueryContext context = new QueryContext("test", false);
        PrefixQuery prefixQuery = new PrefixQuery(new Term("title", "boost"));

        QueryBuilder builder = queryProcessor.execute(context, prefixQuery, 10.0f);

        assertNotNull(builder);
        assertTrue(builder instanceof MatchPhrasePrefixQueryBuilder);

        // Query was processed with high boost
        MatchPhrasePrefixQueryBuilder mpqb = (MatchPhrasePrefixQueryBuilder) builder;
        assertEquals(10.0f, mpqb.boost());
    }

    public void test_convertPrefixQuery_withNegativeImportantContentBoost() throws Exception {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {

            public String get(String propertyKey) {
                return fessConfig.get(propertyKey);
            }

            @Override
            public BigDecimal getAsDecimal(String propertyKey) {
                return DfTypeUtil.toBigDecimal(get(propertyKey));
            }

            @Override
            public Integer getAsInteger(String propertyKey) {
                return DfTypeUtil.toInteger(get(propertyKey));
            }

            public String getQueryDefaultQueryType() {
                return "bool";
            }

            @Override
            public Integer getQueryPrefixExpansionsAsInteger() {
                return 50;
            }

            @Override
            public Integer getQueryPrefixSlopAsInteger() {
                return 0;
            }

            public String getIndexFieldTitle() {
                return "title";
            }

            public String getIndexFieldContent() {
                return "content";
            }

            public String getIndexFieldImportantContent() {
                return "important_content";
            }

            @Override
            public BigDecimal getQueryBoostTitleAsDecimal() {
                return new BigDecimal("0.5");
            }

            @Override
            public BigDecimal getQueryBoostContentAsDecimal() {
                return new BigDecimal("0.05");
            }

            @Override
            public BigDecimal getQueryBoostImportantContentAsDecimal() {
                return new BigDecimal("-1.0"); // Negative boost
            }

            @Override
            public BigDecimal getQueryBoostTitleLangAsDecimal() {
                return new BigDecimal("1.0");
            }

            @Override
            public BigDecimal getQueryBoostContentLangAsDecimal() {
                return new BigDecimal("0.5");
            }

            @Override
            public BigDecimal getQueryBoostImportantContentLangAsDecimal() {
                return new BigDecimal("2.0");
            }
        });

        QueryContext context = new QueryContext("test", false);
        PrefixQuery prefixQuery = new PrefixQuery(new Term(Constants.DEFAULT_FIELD, "test"));

        QueryBuilder builder = queryProcessor.execute(context, prefixQuery, 1.0f);

        assertNotNull(builder);
        assertTrue(builder instanceof DefaultQueryBuilder);

        // Should not include important_content field when boost is negative
        String queryString = builder.toString();
        assertFalse(queryString.contains("important_content"));
    }

    public void test_register() {
        // Save the original processor
        QueryProcessor originalProcessor = ComponentUtil.getQueryProcessor();

        try {
            // Create a new QueryProcessor and initialize it
            QueryProcessor newQueryProcessor = new QueryProcessor();
            newQueryProcessor.init();
            ComponentUtil.register(newQueryProcessor, "queryProcessor");

            // Register the command with the new processor
            PrefixQueryCommand newCommand = new PrefixQueryCommand();
            newCommand.register();

            // Verify that the command can process PrefixQuery by executing it
            PrefixQuery query = new PrefixQuery(new Term("field", "test"));
            QueryContext context = new QueryContext("test", false);

            // This should work if the command is properly registered
            QueryBuilder result = newQueryProcessor.execute(context, query, 1.0f);
            assertNotNull(result);
        } finally {
            // Reset to the original queryProcessor for other tests
            ComponentUtil.register(originalProcessor, "queryProcessor");
        }
    }
}
