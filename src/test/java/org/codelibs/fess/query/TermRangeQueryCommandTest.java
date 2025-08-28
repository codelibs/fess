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
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.exception.InvalidQueryException;

import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.RangeQueryBuilder;

public class TermRangeQueryCommandTest extends QueryTestBase {
    private TermRangeQueryCommand queryCommand;

    @Override
    protected void setUpChild() throws Exception {
        // Initialize and register TermRangeQueryCommand
        queryCommand = new TermRangeQueryCommand();
        queryCommand.register();
    }

    public void test_getQueryClassName() {
        assertEquals("TermRangeQuery", queryCommand.getQueryClassName());
    }

    public void test_execute_withInvalidQuery() {
        try {
            QueryContext context = new QueryContext("test", false);
            Query invalidQuery = new TermQuery(new Term("field", "value"));
            queryCommand.execute(context, invalidQuery, 1.0f);
            fail();
        } catch (InvalidQueryException e) {
            // expected
            assertTrue(e.getMessage().contains("Unknown q:"));
        }
    }

    public void test_convertTermRangeQuery_searchField_inclusive() throws Exception {
        QueryContext context = new QueryContext("test", false);
        TermRangeQuery query = new TermRangeQuery("title", new BytesRef("aaa"), new BytesRef("zzz"), true, true);

        QueryBuilder builder = queryCommand.convertTermRangeQuery(context, query, 1.0f);
        assertNotNull(builder);
        assertTrue(builder instanceof RangeQueryBuilder);

        // Query was processed successfully
        RangeQueryBuilder rqb = (RangeQueryBuilder) builder;
        assertEquals("title", rqb.fieldName());
    }
}
