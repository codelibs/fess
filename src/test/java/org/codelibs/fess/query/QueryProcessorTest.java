/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.query.QueryProcessor.FilterChain;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

public class QueryProcessorTest extends UnitFessTestCase {

    public void test_executeWithFilter() {
        final AtomicBoolean called = new AtomicBoolean(false);
        QueryProcessor queryProcessor = new QueryProcessor() {
            protected FilterChain createDefaultFilterChain() {
                return (context, query, boost) -> {
                    called.set(true);
                    return QueryBuilders.boolQuery();
                };
            }
        };
        queryProcessor.init();

        QueryContext context = new QueryContext(null, false);
        MatchAllDocsQuery query = new MatchAllDocsQuery();

        QueryBuilder queryBuilder = queryProcessor.execute(context, query, 1.0f);
        assertTrue(called.get());
        assertEquals(BoolQueryBuilder.class, queryBuilder.getClass());

        called.set(false);
        final AtomicBoolean calledFirst = new AtomicBoolean(false);
        queryProcessor.addFilter(new QueryProcessor.Filter() {
            @Override
            public QueryBuilder execute(QueryContext context, Query query, float boost, FilterChain chain) {
                calledFirst.set(true);
                assertFalse(called.get());
                QueryBuilder builder = chain.execute(context, query, boost);
                assertTrue(called.get());
                return builder;
            }
        });
        queryBuilder = queryProcessor.execute(context, query, 1.0f);
        assertTrue(called.get());
        assertTrue(calledFirst.get());
        assertEquals(BoolQueryBuilder.class, queryBuilder.getClass());

        called.set(false);
        calledFirst.set(false);
        final AtomicBoolean calledSecond = new AtomicBoolean(false);
        queryProcessor.addFilter(new QueryProcessor.Filter() {
            @Override
            public QueryBuilder execute(QueryContext context, Query query, float boost, FilterChain chain) {
                calledSecond.set(true);
                assertFalse(called.get());
                assertFalse(calledFirst.get());
                QueryBuilder builder = chain.execute(context, query, boost);
                assertTrue(called.get());
                assertTrue(calledFirst.get());
                return builder;
            }
        });
        queryBuilder = queryProcessor.execute(context, query, 1.0f);
        assertTrue(called.get());
        assertTrue(calledFirst.get());
        assertTrue(calledSecond.get());
        assertEquals(BoolQueryBuilder.class, queryBuilder.getClass());
    }

}
