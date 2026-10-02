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
package org.codelibs.fess.opensearch.client;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.opensearch.client.SearchEngineClient.SearchConditionBuilder;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.codelibs.fesen.opensearch.index.query.BoolQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;

/**
 * Tests that {@link SearchConditionBuilder#filterQueries(List)} reaches the keyword query as hard
 * {@code bool} filters, so they bind the result set however the query string is read.
 */
public class SearchEngineClientFilterQueryTest extends UnitFessTestCase {

    /** Stands in for the query parser: the keyword query is a fixed match query. */
    private static final QueryBuilder KEYWORD_QUERY = QueryBuilders.matchQuery("content", "fess");

    private static class StubQueryHelper extends QueryHelper {
        @Override
        public QueryContext build(final SearchRequestType searchRequestType, final String query, final Consumer<QueryContext> context) {
            final QueryContext queryContext = new QueryContext(query, true);
            queryContext.setQueryBuilder(KEYWORD_QUERY);
            context.accept(queryContext);
            return queryContext;
        }
    }

    private QueryBuilder build(final SearchConditionBuilder builder) {
        return builder.buildQueryContext(new StubQueryHelper(), null, ComponentUtil.getFessConfig()).getQueryBuilder();
    }

    @Test
    public void test_filterQueries_addedAsBoolFilters() {
        final QueryBuilder labelFilter = QueryBuilders.termsQuery("label", "public", "faq");
        final QueryBuilder query = build(SearchConditionBuilder.builder(null)
                .query("fess")
                .searchRequestType(SearchRequestType.JSON)
                .filterQueries(List.of(labelFilter)));
        assertTrue(query instanceof BoolQueryBuilder, query.toString());
        final BoolQueryBuilder bool = (BoolQueryBuilder) query;
        assertEquals(List.of(KEYWORD_QUERY), bool.must());
        assertEquals(List.of(labelFilter), bool.filter());
        assertTrue(bool.should().isEmpty(), "a filter must never become an optional clause");
    }

    @Test
    public void test_filterQueries_appliedToAdminSearchToo() {
        final QueryBuilder labelFilter = QueryBuilders.termsQuery("label", "public");
        final BoolQueryBuilder bool = (BoolQueryBuilder) build(SearchConditionBuilder.builder(null)
                .query("fess")
                .searchRequestType(SearchRequestType.ADMIN_SEARCH)
                .filterQueries(List.of(labelFilter)));
        assertEquals(List.of(labelFilter), bool.filter());
    }

    @Test
    public void test_noFilterQueries_leavesQueryUnchanged() {
        assertSame(KEYWORD_QUERY, build(SearchConditionBuilder.builder(null).query("fess").searchRequestType(SearchRequestType.JSON)));
        assertSame(KEYWORD_QUERY,
                build(SearchConditionBuilder.builder(null)
                        .query("fess")
                        .searchRequestType(SearchRequestType.JSON)
                        .filterQueries(Collections.emptyList())));
        assertSame(KEYWORD_QUERY,
                build(SearchConditionBuilder.builder(null).query("fess").searchRequestType(SearchRequestType.JSON).filterQueries(null)));
    }
}
