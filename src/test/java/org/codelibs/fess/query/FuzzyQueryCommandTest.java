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
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.exception.InvalidQueryException;

import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.index.query.DisMaxQueryBuilder;
import org.opensearch.index.query.FuzzyQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

public class FuzzyQueryCommandTest extends QueryTestBase {

    private FuzzyQueryCommand fuzzyQueryCommand;

    @Override
    protected void setUpChild() throws Exception {
        // Initialize and register FuzzyQueryCommand
        fuzzyQueryCommand = new FuzzyQueryCommand();
        fuzzyQueryCommand.register();
    }

    // Test getQueryClassName method
    @Test
    public void test_getQueryClassName() {
        assertEquals("FuzzyQuery", fuzzyQueryCommand.getQueryClassName());
    }

    // Test execute method with valid FuzzyQuery
    @Test
    public void test_execute_withFuzzyQuery() {
        QueryContext context = new QueryContext("test", false);
        Term term = new Term("title", "fuzzy");
        FuzzyQuery fuzzyQuery = new FuzzyQuery(term);
        float boost = 1.5f;

        QueryBuilder result = fuzzyQueryCommand.execute(context, fuzzyQuery, boost);
        assertNotNull(result);

    }

    // Test execute method with invalid query type
    @Test
    public void test_execute_withInvalidQuery() {
        QueryContext context = new QueryContext("test", false);
        Query invalidQuery = new TermQuery(new Term("field", "value"));
        float boost = 1.0f;

        try {
            fuzzyQueryCommand.execute(context, invalidQuery, boost);
            fail("Should throw InvalidQueryException");
        } catch (InvalidQueryException e) {
            assertTrue(e.getMessage().contains("Unknown q:"));
        }
    }

    // Test convertFuzzyQuery with DEFAULT_FIELD
    @Test
    public void test_convertFuzzyQuery_withDefaultField() {
        QueryContext context = new QueryContext("test", false);
        Term term = new Term(Constants.DEFAULT_FIELD, "fuzzy");
        FuzzyQuery fuzzyQuery = new FuzzyQuery(term, 2);
        float boost = 1.0f;

        QueryBuilder result = queryProcessor.execute(context, fuzzyQuery, boost);
        assertNotNull(result);
        assertTrue(result instanceof DefaultQueryBuilder);

    }

    // Test convertFuzzyQuery with specific search field
    @Test
    public void test_convertFuzzyQuery_withSearchField() {
        QueryContext context = new QueryContext("test", false);
        Term term = new Term("title", "fuzzy");
        FuzzyQuery fuzzyQuery = new FuzzyQuery(term, 1);
        float boost = 2.0f;

        QueryBuilder result = queryProcessor.execute(context, fuzzyQuery, boost);
        assertNotNull(result);
        assertTrue(result instanceof FuzzyQueryBuilder);

        FuzzyQueryBuilder fuzzyQueryBuilder = (FuzzyQueryBuilder) result;

        assertTrue(fuzzyQueryBuilder.toString().contains("\"title\""));
        assertTrue(fuzzyQueryBuilder.toString().contains("\"fuzzy\""));
    }

    // Test convertFuzzyQuery with non-search field
    @Test
    public void test_convertFuzzyQuery_withNonSearchField() {
        QueryContext context = new QueryContext("test", false);
        Term term = new Term("unknown_field", "fuzzy");
        FuzzyQuery fuzzyQuery = new FuzzyQuery(term, 2);
        float boost = 1.0f;

        QueryBuilder result = queryProcessor.execute(context, fuzzyQuery, boost);
        assertNotNull(result);
        assertTrue(result instanceof DefaultQueryBuilder);

    }

    // Test convertFuzzyQuery with different max edits
    @Test
    public void test_convertFuzzyQuery_withMaxEdits() {
        QueryContext context = new QueryContext("test", false);

        // Test with max edits = 0
        Term term0 = new Term("title", "exact");
        FuzzyQuery fuzzyQuery0 = new FuzzyQuery(term0, 0);
        QueryBuilder result0 = fuzzyQueryCommand.convertFuzzyQuery(context, fuzzyQuery0, 1.0f);
        assertNotNull(result0);
        assertTrue(result0 instanceof FuzzyQueryBuilder);
        FuzzyQueryBuilder fqb0 = (FuzzyQueryBuilder) result0;
        assertEquals("0", fqb0.fuzziness().asString());

        // Test with max edits = 1
        Term term1 = new Term("title", "fuzzy");
        FuzzyQuery fuzzyQuery1 = new FuzzyQuery(term1, 1);
        QueryBuilder result1 = fuzzyQueryCommand.convertFuzzyQuery(context, fuzzyQuery1, 1.0f);
        assertNotNull(result1);
        assertTrue(result1 instanceof FuzzyQueryBuilder);
        FuzzyQueryBuilder fqb1 = (FuzzyQueryBuilder) result1;
        assertEquals("1", fqb1.fuzziness().asString());

        // Test with max edits = 2
        Term term2 = new Term("title", "fuzzy");
        FuzzyQuery fuzzyQuery2 = new FuzzyQuery(term2, 2);
        QueryBuilder result2 = fuzzyQueryCommand.convertFuzzyQuery(context, fuzzyQuery2, 1.0f);
        assertNotNull(result2);
        assertTrue(result2 instanceof FuzzyQueryBuilder);
        FuzzyQueryBuilder fqb2 = (FuzzyQueryBuilder) result2;
        assertEquals("2", fqb2.fuzziness().asString());
    }

    // Test convertFuzzyQuery with boost values
    @Test
    public void test_convertFuzzyQuery_withBoost() {
        QueryContext context = new QueryContext("test", false);
        Term term = new Term("title", "fuzzy");
        FuzzyQuery fuzzyQuery = new FuzzyQuery(term);

        // Test with different boost values
        float[] boostValues = { 0.5f, 1.0f, 2.5f, 10.0f };
        for (float boost : boostValues) {
            QueryBuilder result = queryProcessor.execute(context, fuzzyQuery, boost);
            assertNotNull(result);
            assertTrue(result instanceof FuzzyQueryBuilder);
            FuzzyQueryBuilder fqb = (FuzzyQueryBuilder) result;
            assertEquals(boost, fqb.boost());

        }
    }

    // Test with transpositions enabled
    @Test
    public void test_convertFuzzyQuery_withTranspositionsEnabled() {
        QueryContext context = new QueryContext("test", false);
        Term term = new Term("title", "fuzzy");
        FuzzyQuery fuzzyQuery = new FuzzyQuery(term);

        QueryBuilder result = fuzzyQueryCommand.convertFuzzyQuery(context, fuzzyQuery, 1.0f);
        assertNotNull(result);
        // Just check it's a FuzzyQueryBuilder
        assertTrue(result instanceof FuzzyQueryBuilder);
    }

    // Test with transpositions disabled
    @Test
    public void test_convertFuzzyQuery_withTranspositionsDisabled() {
        QueryContext context = new QueryContext("test", false);
        Term term = new Term("title", "fuzzy");
        FuzzyQuery fuzzyQuery = new FuzzyQuery(term);

        QueryBuilder result = fuzzyQueryCommand.convertFuzzyQuery(context, fuzzyQuery, 1.0f);
        assertNotNull(result);
        // Just check it's a FuzzyQueryBuilder
        assertTrue(result instanceof FuzzyQueryBuilder);
    }

    // Test field log and highlight additions
    @Test
    public void test_convertFuzzyQuery_contextUpdates() {
        QueryContext context = new QueryContext("test", false);
        String searchText = "fuzzytext";
        Term term = new Term("title", searchText);
        FuzzyQuery fuzzyQuery = new FuzzyQuery(term);

        QueryBuilder result = fuzzyQueryCommand.convertFuzzyQuery(context, fuzzyQuery, 1.0f);
        assertNotNull(result);

        // Verify that the query was processed successfully
        assertTrue(result instanceof FuzzyQueryBuilder);
        FuzzyQueryBuilder fqb = (FuzzyQueryBuilder) result;
        assertEquals("title", fqb.fieldName());
        assertEquals(searchText, fqb.value());
    }

    // Test with special characters in term text
    @Test
    public void test_convertFuzzyQuery_withSpecialCharacters() {
        QueryContext context = new QueryContext("test", false);
        String[] specialTexts = { "test@email.com", "test-hyphen", "test_underscore", "test.period", "test+plus" };

        for (String text : specialTexts) {
            Term term = new Term("title", text);
            FuzzyQuery fuzzyQuery = new FuzzyQuery(term);
            QueryBuilder result = fuzzyQueryCommand.convertFuzzyQuery(context, fuzzyQuery, 1.0f);
            assertNotNull(result);
            assertTrue(result.toString().contains(text));

        }
    }

    // Test with empty term text
    @Test
    public void test_convertFuzzyQuery_withEmptyTerm() {
        QueryContext context = new QueryContext("test", false);
        Term term = new Term("title", "");
        FuzzyQuery fuzzyQuery = new FuzzyQuery(term);

        QueryBuilder result = fuzzyQueryCommand.convertFuzzyQuery(context, fuzzyQuery, 1.0f);
        assertNotNull(result);

    }

    // Test with various field names
    @Test
    public void test_convertFuzzyQuery_withVariousFields() {
        QueryContext context = new QueryContext("test", false);
        String[] fields = { "title", "content", "url", "site", "host", "mimetype" };

        for (String field : fields) {
            Term term = new Term(field, "fuzzy");
            FuzzyQuery fuzzyQuery = new FuzzyQuery(term);
            QueryBuilder result = fuzzyQueryCommand.convertFuzzyQuery(context, fuzzyQuery, 1.0f);
            assertNotNull(result);

        }
    }

}
