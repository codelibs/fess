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

import java.util.List;
import java.util.function.Consumer;

import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.helper.DocumentHelper;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient.SearchConditionBuilder;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fesen.opensearch.index.query.BoolQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;
import org.junit.jupiter.api.Test;

/**
 * Tests that {@link SearchConditionBuilder#similarDocHash(String)} filters on the content signature
 * only when the search engine has it. The plugin-less index does not index the field, so the term
 * query would fail the whole search with HTTP 400.
 */
public class SearchEngineClientSimilarDocHashTest extends UnitFessTestCase {

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

    private static FessConfig useSearchEngineType(final String type) {
        final FessConfig fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getSearchEngineType() {
                return type;
            }

            @Override
            public String getIndexFieldContentMinhashBits() {
                return "content_minhash_bits";
            }
        };
        ComponentUtil.setFessConfig(fessConfig);
        return fessConfig;
    }

    private static SearchConditionBuilder builderWithHash(final String hash) {
        return SearchConditionBuilder.builder(null).query("fess").searchRequestType(SearchRequestType.JSON).similarDocHash(hash);
    }

    @Test
    public void test_similarDocHash_filtersOnTheContentSignatureWhenThePluginsAreAvailable() {
        ComponentUtil.register(new DocumentHelper(), "documentHelper");
        for (final String type : new String[] { "default", "opensearch" }) {
            final FessConfig fessConfig = useSearchEngineType(type);

            final QueryBuilder query = builderWithHash("0101").buildQueryContext(new StubQueryHelper(), null, fessConfig).getQueryBuilder();

            assertTrue(type + ": " + query, query instanceof BoolQueryBuilder);
            final BoolQueryBuilder bool = (BoolQueryBuilder) query;
            assertEquals(type, List.of(KEYWORD_QUERY), bool.must());
            assertEquals(type, List.of(QueryBuilders.termQuery("content_minhash_bits", "0101")), bool.filter());
        }
    }

    @Test
    public void test_similarDocHash_isIgnoredWithoutThePlugins() {
        ComponentUtil.register(new DocumentHelper(), "documentHelper");
        for (final String type : new String[] { "vanilla", "aws", "cloud" }) {
            final FessConfig fessConfig = useSearchEngineType(type);
            final SearchConditionBuilder builder = builderWithHash("0101");

            assertNull(builder.similarDocHash, type);
            assertSame(KEYWORD_QUERY, builder.buildQueryContext(new StubQueryHelper(), null, fessConfig).getQueryBuilder());
        }
    }
}
