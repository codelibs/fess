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
package org.codelibs.fess.rank.fusion;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.client.SearchEngineClient.SearchCondition;
import org.codelibs.fess.opensearch.client.SearchEngineClient.SearchConditionBuilder;
import org.codelibs.fess.opensearch.query.HybridQueryBuilder;
import org.codelibs.fess.query.QueryFieldConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.codelibs.fesen.opensearch.action.search.SearchAction;
import org.codelibs.fesen.opensearch.action.search.SearchRequestBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;

public class DefaultSearcherTest extends UnitFessTestCase {

    /** The shipped rank.fusion.rank_constant. */
    private static final int DEFAULT_RANK_CONSTANT = 20;

    /** The shipped rank.fusion.pagination_depth. */
    private static final int DEFAULT_PAGINATION_DEPTH = 200;

    // -------------------------------------------------------------------------------------
    //                                                                    fusion applicability
    //                                                                    --------------------

    @Test
    public void test_searchWithSubQueries_declinesWhenDisabled() {
        givenConfig(false, "rrf", "");
        final DefaultSearcher searcher = new DefaultSearcher();
        assertTrue(searcher.searchWithSubQueries("q", params(0, 10, null), OptionalThing.empty(), List.of(subQuery("semantic_chunk")))
                .isEmpty(), "a searcher must not fuse while the property is off");
    }

    @Test
    public void test_searchWithSubQueries_declinesWithoutSubQueries() {
        givenConfig(true, "rrf", "");
        assertTrue(new DefaultSearcher().searchWithSubQueries("q", params(0, 10, null), OptionalThing.empty(), List.of()).isEmpty(),
                "there is nothing to fuse without a branch");
    }

    @Test
    public void test_isEngineFusionApplicable_declinesExplicitSort() {
        givenConfig(true, "rrf", "");
        // sorting by anything but the score makes the engine report null scores, so the fusion
        // would have decided nothing
        assertFalse(
                new DefaultSearcher().isEngineFusionApplicable(params(0, 10, "last_modified.desc"), List.of(subQuery("semantic_chunk"))));
    }

    @Test
    public void test_isEngineFusionApplicable_declinesBeyondPaginationDepth() {
        givenConfig(true, "rrf", "");
        final DefaultSearcher searcher = new DefaultSearcher();
        final int depth = searcher.getPaginationDepth();
        assertTrue(searcher.isEngineFusionApplicable(params(depth - 10, 10, null), List.of(subQuery("semantic_chunk"))));
        assertFalse(searcher.isEngineFusionApplicable(params(depth, 10, null), List.of(subQuery("semantic_chunk"))),
                "a page the engine does not rank that deep must not be answered from a fused request");
    }

    @Test
    public void test_isEngineFusionApplicable_declinesTooManyBranches() {
        givenConfig(true, "rrf", "");
        final DefaultSearcher searcher = new DefaultSearcher();
        assertTrue(searcher.isEngineFusionApplicable(params(0, 10, null),
                List.of(subQuery("a"), subQuery("b"), subQuery("c"), subQuery("d"))));
        assertFalse(
                searcher.isEngineFusionApplicable(params(0, 10, null),
                        List.of(subQuery("a"), subQuery("b"), subQuery("c"), subQuery("d"), subQuery("e"))),
                "the engine accepts at most " + HybridQueryBuilder.MAX_SUB_QUERIES + " subqueries including the main one");
    }

    @Test
    public void test_legacySearcher_refusesToFuse() {
        givenConfig(true, "rrf", "");
        // refusing is what keeps the branches from being dropped without anyone noticing
        assertTrue(new LegacySearcher()
                .searchWithSubQueries("q", params(0, 10, null), OptionalThing.empty(), List.of(subQuery("semantic_chunk")))
                .isEmpty());
    }

    // -------------------------------------------------------------------------------------
    //                                                                          fused request
    //                                                                          -------------

    @Test
    public void test_fuse_wrapsTheKeywordQueryAndAttachesThePipeline() {
        givenConfig(true, "rrf", "");
        final DefaultSearcher searcher = new DefaultSearcher();
        final SearchRequestBuilder builder = new SearchRequestBuilder(new SearchEngineClient(), SearchAction.INSTANCE);
        final SearchCondition<SearchRequestBuilder> base = requestBuilder -> {
            requestBuilder.setQuery(QueryBuilders.matchQuery("content", "opensearch"));
            requestBuilder.setFrom(0).setSize(20);
            return true;
        };
        final Map<String, Object> pipeline = searcher.buildPipelineSource(List.of("default", "semantic_chunk"));
        assertTrue(searcher.fuse(base, List.of(subQuery("semantic_chunk")), pipeline).build(builder));

        final String json = builder.request().source().toString().replaceAll("\\s", "");
        // the keyword query is not rebuilt, it is wrapped, so everything else the base condition
        // set up survives
        assertTrue(json.contains("\"hybrid\":{"), json);
        assertTrue(json.contains("\"match\":{\"content\""), json);
        assertTrue(json.contains("\"_name\":\"default\""), json);
        assertTrue(json.contains("\"_name\":\"semantic_chunk\""), json);
        assertTrue(json.contains("\"pagination_depth\":" + DEFAULT_PAGINATION_DEPTH), json);
        // a hybrid query without this comes back with duplicated hits and sentinel scores rather
        // than an error, so the two are written together and asserted together
        assertTrue(json.contains("\"search_pipeline\":"), json);
        assertTrue(json.contains("score-ranker-processor"), json);
        assertNotNull(builder.request().source().searchPipelineSource());
    }

    @Test
    public void test_fuse_propagatesARefusalFromTheBaseCondition() {
        givenConfig(true, "rrf", "");
        final DefaultSearcher searcher = new DefaultSearcher();
        final SearchRequestBuilder builder = new SearchRequestBuilder(new SearchEngineClient(), SearchAction.INSTANCE);
        final Map<String, Object> pipeline = searcher.buildPipelineSource(List.of("default", "semantic_chunk"));
        assertFalse(searcher.fuse(requestBuilder -> false, List.of(subQuery("semantic_chunk")), pipeline).build(builder));
        assertNull(builder.request().source(), "a refused request must not be built up any further");
    }

    @Test
    public void test_fuse_highlightsWithTheHighlightQuery() {
        // no givenConfig: the keyword condition reads far more of the configuration than rank
        // fusion does, so these run on the shipped one
        givenKeywordQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("content", "opensearch"))
                .filter(QueryBuilders.termQuery("role", "guest")));
        final DefaultSearcher searcher = new DefaultSearcher();
        final SearchRequestBuilder builder = new SearchRequestBuilder(new SearchEngineClient(), SearchAction.INSTANCE);
        final SearchCondition<SearchRequestBuilder> base = requestBuilder -> SearchConditionBuilder.builder(requestBuilder)
                .query("opensearch")
                .highlightInfo(new HighlightInfo())
                .highlightQuery(QueryBuilders.matchPhraseQuery("content", "opensearch"))
                .build();
        final Map<String, Object> pipeline = searcher.buildPipelineSource(List.of("default", "semantic_chunk"));
        assertTrue(searcher.fuse(base, List.of(subQuery("semantic_chunk")), pipeline).build(builder));

        final String json = builder.request().source().toString().replaceAll("\\s", "");
        assertTrue(json.contains("\"hybrid\":{"), json);
        // the fast vector highlighter reads no terms out of a hybrid query, so the fused request
        // names the terms to highlight, and only those: no permission filter
        final String highlight = json.substring(json.indexOf("\"highlight\":{"));
        assertTrue(highlight.contains("\"highlight_query\":{\"match_phrase\":{\"content\""), json);
        assertFalse(highlight.contains("\"role\""), json);
    }

    @Test
    public void test_build_namesNoHighlightQueryByDefault() {
        givenKeywordQuery(QueryBuilders.matchQuery("content", "opensearch"));
        final SearchRequestBuilder builder = new SearchRequestBuilder(new SearchEngineClient(), SearchAction.INSTANCE);
        assertTrue(SearchConditionBuilder.builder(builder).query("opensearch").highlightInfo(new HighlightInfo()).build());

        // the search engine highlights with the request's own query, already parsed, so naming
        // it again would only add work
        final String json = builder.request().source().toString().replaceAll("\\s", "");
        assertTrue(json.contains("\"highlight\":{"), json);
        assertFalse(json.contains("\"highlight_query\""), json);
    }

    @Test
    public void test_searchWithSubQueries_buildsTheHighlightQueryOnlyWhenHighlighting() {
        // made before givenConfig, which leaves out the highlight settings
        final HighlightInfo highlightInfo = new HighlightInfo();
        givenConfig(true, "rrf", "");
        givenKeywordQuery(QueryBuilders.matchQuery("content", "opensearch"));
        final List<QueryBuilder> highlightQueries = new ArrayList<>();
        final DefaultSearcher searcher = new DefaultSearcher() {
            @Override
            protected SearchCondition<SearchRequestBuilder> createSearchCondition(final String query, final SearchRequestParams params,
                    final OptionalThing<FessUserBean> userBean, final QueryBuilder highlightQuery) {
                highlightQueries.add(highlightQuery);
                return requestBuilder -> true;
            }

            @Override
            protected SearchResult execute(final SearchRequestParams params, final SearchCondition<SearchRequestBuilder> condition) {
                return SearchResult.create().build();
            }
        };

        searcher.searchWithSubQueries("opensearch", params(0, 10, null), OptionalThing.empty(), List.of(subQuery("semantic_chunk")));
        assertNull(highlightQueries.get(0));
        assertNull(highlightQueryString, "nothing is highlighted, so there is nothing to build");

        searcher.searchWithSubQueries("opensearch", params(0, 10, null, highlightInfo), OptionalThing.empty(),
                List.of(subQuery("semantic_chunk")));
        assertEquals("opensearch", highlightQueryString);
        assertEquals(QueryBuilders.matchPhraseQuery("content", "opensearch"), highlightQueries.get(1));
    }

    @Test
    public void test_fuse_addsNoHighlighterWhenNoneWasRequested() {
        givenKeywordQuery(QueryBuilders.matchQuery("content", "opensearch"));
        final DefaultSearcher searcher = new DefaultSearcher();
        final SearchRequestBuilder builder = new SearchRequestBuilder(new SearchEngineClient(), SearchAction.INSTANCE);
        final SearchCondition<SearchRequestBuilder> base =
                requestBuilder -> SearchConditionBuilder.builder(requestBuilder).query("opensearch").build();
        final Map<String, Object> pipeline = searcher.buildPipelineSource(List.of("default", "semantic_chunk"));
        assertTrue(searcher.fuse(base, List.of(subQuery("semantic_chunk")), pipeline).build(builder));

        final String json = builder.request().source().toString().replaceAll("\\s", "");
        assertFalse(json.contains("\"highlight\""), json);
    }

    // -------------------------------------------------------------------------------------
    //                                                                                pipeline
    //                                                                                --------

    @Test
    public void test_buildPipelineSource_rrf() {
        givenConfig(true, "rrf", "");
        final Map<String, Object> pipeline = new DefaultSearcher().buildPipelineSource(List.of("default", "semantic_chunk"));
        final Map<String, Object> processor = firstProcessor(pipeline, "score-ranker-processor");
        @SuppressWarnings("unchecked")
        final Map<String, Object> combination = (Map<String, Object>) processor.get("combination");
        assertEquals("rrf", combination.get("technique"));
        // Fess ranks from zero and the engine ranks from one, so the constant it is given has to
        // be one lower for 1/(k+rank) to mean the same thing on both sides
        assertEquals(Integer.valueOf(DEFAULT_RANK_CONSTANT - 1), combination.get("rank_constant"));
        assertNull(combination.get("parameters"), "no weights are configured, so none are sent");
        assertNull(processor.get("normalization"), "rrf ranks, it does not normalize");
    }

    @Test
    public void test_buildPipelineSource_normalization() {
        givenConfig(true, "arithmetic_mean", "");
        final Map<String, Object> pipeline = new DefaultSearcher().buildPipelineSource(List.of("default", "semantic_chunk"));
        final Map<String, Object> processor = firstProcessor(pipeline, "normalization-processor");
        @SuppressWarnings("unchecked")
        final Map<String, Object> normalization = (Map<String, Object>) processor.get("normalization");
        assertEquals(ComponentUtil.getFessConfig().getRankFusionNormalizationTechnique(), normalization.get("technique"));
        @SuppressWarnings("unchecked")
        final Map<String, Object> combination = (Map<String, Object>) processor.get("combination");
        assertEquals("arithmetic_mean", combination.get("technique"));
        assertNull(combination.get("rank_constant"), "the rank constant belongs to rrf only");
    }

    @Test
    public void test_buildPipelineSource_rankConstantIsClampedToTheEngineRange() {
        // K - 1 would be 0, which the engine rejects
        givenConfig(true, "rrf", "", 1);
        assertEquals(Integer.valueOf(1), Integer.valueOf(new DefaultSearcher().getEngineRankConstant()));
    }

    // -------------------------------------------------------------------------------------
    //                                                                                 weights
    //                                                                                 -------

    @Test
    public void test_resolveWeights_byName() {
        givenConfig(true, "rrf", "semantic_chunk:0.3, default:0.7");
        // named rather than positional: which searchers take part depends on the plugins
        // installed, so a positional list would start weighting the wrong branch
        assertEquals(List.of(Float.valueOf(0.7f), Float.valueOf(0.3f)),
                new DefaultSearcher().resolveWeights(List.of("default", "semantic_chunk")));
    }

    @Test
    public void test_resolveWeights_emptyWhenUnset() {
        givenConfig(true, "rrf", "");
        assertEquals(List.of(), new DefaultSearcher().resolveWeights(List.of("default", "semantic_chunk")));
    }

    @Test
    public void test_resolveWeights_refusesWhenNamesDoNotMatch() {
        givenConfig(true, "rrf", "default:0.5,multi_modal:0.5");
        // the engine fails the whole request when the weights do not line up with the branches,
        // so this is caught here where the reason can be named
        assertNull(new DefaultSearcher().resolveWeights(List.of("default", "semantic_chunk")));
    }

    @Test
    public void test_resolveWeights_refusesWhenSumIsNotOne() {
        givenConfig(true, "rrf", "default:0.5,semantic_chunk:0.4");
        assertNull(new DefaultSearcher().resolveWeights(List.of("default", "semantic_chunk")));
    }

    @Test
    public void test_resolveWeights_refusesMalformedValues() {
        givenConfig(true, "rrf", "default;0.5");
        assertNull(new DefaultSearcher().resolveWeights(List.of("default")));
        givenConfig(true, "rrf", "default:half");
        assertNull(new DefaultSearcher().resolveWeights(List.of("default")));
        givenConfig(true, "rrf", "default:1.5");
        assertNull(new DefaultSearcher().resolveWeights(List.of("default")));
    }

    // -------------------------------------------------------------------------------------
    //                                                                            test helpers
    //                                                                            ------------

    private void givenConfig(final boolean engineEnabled, final String technique, final String weights) {
        givenConfig(engineEnabled, technique, weights, DEFAULT_RANK_CONSTANT);
    }

    private void givenConfig(final boolean engineEnabled, final String technique, final String weights, final int rankConstant) {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isRankFusionEngineEnabled() {
                return engineEnabled;
            }

            @Override
            public String getRankFusionCombinationTechnique() {
                return technique;
            }

            @Override
            public String getRankFusionCombinationWeights() {
                return weights;
            }

            @Override
            public String getRankFusionNormalizationTechnique() {
                return "min_max";
            }

            @Override
            public Integer getRankFusionPaginationDepthAsInteger() {
                return Integer.valueOf(DEFAULT_PAGINATION_DEPTH);
            }

            @Override
            public Integer getRankFusionRankConstantAsInteger() {
                return Integer.valueOf(rankConstant);
            }
        });
    }

    private String highlightQueryString;

    /**
     * Stands in for the query parser, so that the keyword condition builds the given query
     * without the query parser's own configuration.
     */
    private void givenKeywordQuery(final QueryBuilder keywordQuery) {
        ComponentUtil.register(new QueryHelper() {
            @Override
            public QueryContext build(final SearchRequestType searchRequestType, final String query, final Consumer<QueryContext> context) {
                final QueryContext queryContext = new QueryContext(query, false);
                queryContext.setQueryBuilder(keywordQuery);
                return queryContext;
            }

            @Override
            public QueryBuilder buildHighlightQuery(final String query) {
                highlightQueryString = query;
                return QueryBuilders.matchPhraseQuery("content", query);
            }
        }, "queryHelper");
        final QueryFieldConfig queryFieldConfig = new QueryFieldConfig();
        queryFieldConfig.setHighlightedFields(new String[] { "content" });
        ComponentUtil.register(queryFieldConfig, "queryFieldConfig");
    }

    private static QueryBuilder subQuery(final String name) {
        return QueryBuilders.matchAllQuery().queryName(name);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> firstProcessor(final Map<String, Object> pipeline, final String expectedName) {
        final List<Map<String, Object>> processors = (List<Map<String, Object>>) pipeline.get("phase_results_processors");
        assertEquals(1, processors.size());
        final Map<String, Object> processor = processors.get(0);
        assertTrue(processor.containsKey(expectedName), "expected " + expectedName + " but got " + processor.keySet());
        return (Map<String, Object>) processor.get(expectedName);
    }

    private static SearchRequestParams params(final int start, final int size, final String sort) {
        return params(start, size, sort, null);
    }

    private static SearchRequestParams params(final int start, final int size, final String sort, final HighlightInfo highlightInfo) {
        return new SearchRequestParams() {
            @Override
            public String getQuery() {
                return "q";
            }

            @Override
            public Map<String, String[]> getFields() {
                return Map.of();
            }

            @Override
            public Map<String, String[]> getConditions() {
                return Map.of();
            }

            @Override
            public String[] getLanguages() {
                return new String[0];
            }

            @Override
            public GeoInfo getGeoInfo() {
                return null;
            }

            @Override
            public FacetInfo getFacetInfo() {
                return null;
            }

            @Override
            public HighlightInfo getHighlightInfo() {
                return highlightInfo;
            }

            @Override
            public String getSort() {
                return sort;
            }

            @Override
            public int getStartPosition() {
                return start;
            }

            @Override
            public int getPageSize() {
                return size;
            }

            @Override
            public int getOffset() {
                return 0;
            }

            @Override
            public String[] getExtraQueries() {
                return new String[0];
            }

            @Override
            public String[] getResponseFields() {
                return null;
            }

            @Override
            public Object getAttribute(final String name) {
                return null;
            }

            @Override
            public Locale getLocale() {
                return Locale.ROOT;
            }

            @Override
            public SearchRequestType getType() {
                return SearchRequestType.SEARCH;
            }

            @Override
            public String getSimilarDocHash() {
                return null;
            }
        };
    }
}
