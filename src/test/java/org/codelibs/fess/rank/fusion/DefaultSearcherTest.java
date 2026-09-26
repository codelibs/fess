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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.client.SearchEngineClient.SearchCondition;
import org.codelibs.fess.opensearch.client.SearchEngineClient.SearchConditionBuilder;
import org.codelibs.fess.opensearch.query.HybridQueryBuilder;
import org.codelibs.fess.query.QueryFieldConfig;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.junit.jupiter.api.Test;
import org.lastaflute.core.message.UserMessages;
import org.codelibs.fesen.opensearch.OpenSearchStatusException;
import org.codelibs.fesen.opensearch.action.search.SearchAction;
import org.codelibs.fesen.opensearch.action.search.SearchRequestBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;
import org.codelibs.fesen.opensearch.core.rest.RestStatus;
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
    public void test_isEngineFusionApplicable_reportsEveryPageBeyondPaginationDepth() {
        givenConfig(true, "rrf", "");
        final DefaultSearcher searcher = new DefaultSearcher();
        final int depth = searcher.getPaginationDepth();
        final LogCapturingAppender log = LogCapturingAppender.attach(DefaultSearcher.class.getName(), Level.DEBUG);
        try {
            searcher.isEngineFusionApplicable(params(depth, 10, null), List.of(subQuery("semantic_chunk")));
            searcher.isEngineFusionApplicable(params(depth, 10, null), List.of(subQuery("semantic_chunk")));
            // an expected consequence of the configured depth, so it is reported at DEBUG, and
            // for every such search rather than only the first one since startup
            assertEquals(2, log.messagesAt(Level.DEBUG).stream().filter(m -> m.contains("rank.fusion.pagination_depth")).count());
            assertTrue(log.eventsAt(Level.INFO).isEmpty(), log.renderedEvents().toString());
        } finally {
            log.detach();
        }
    }

    @Test
    public void test_warnIfNotNormalized_reportsEveryUnnormalizedResult() {
        final DefaultSearcher searcher = new DefaultSearcher();
        final SearchResult unnormalized = SearchResult.create().addDocument(Map.of(Constants.SCORE, -9.9E8f)).build();
        final SearchResult normalized = SearchResult.create().addDocument(Map.of(Constants.SCORE, 0.5f)).build();
        final LogCapturingAppender log = LogCapturingAppender.attach(DefaultSearcher.class.getName(), Level.INFO);
        try {
            searcher.warnIfNotNormalized(unnormalized);
            searcher.warnIfNotNormalized(normalized);
            searcher.warnIfNotNormalized(unnormalized);
            // the cluster can be fixed and break again without a restart, so every occurrence is
            // reported, not only the first one since startup
            assertEquals(2, log.warnings().size(), log.renderedEvents().toString());
            assertTrue(log.errors().isEmpty(), "the search is still answered, so this is not an error");
        } finally {
            log.detach();
        }
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
    public void test_canFuse_decidesWithoutTheBranchesBeingBuilt() {
        givenConfig(true, "rrf", "");
        final DefaultSearcher searcher = new DefaultSearcher();
        final int depth = searcher.getPaginationDepth();
        assertTrue(searcher.canFuse(params(0, 10, null), List.of("semantic_chunk")));
        assertFalse(searcher.canFuse(params(depth, 10, null), List.of("semantic_chunk")),
                "a page past the depth is known not to be fused before any branch is built");
        assertFalse(searcher.canFuse(params(0, 10, "last_modified.desc"), List.of("semantic_chunk")));
    }

    @Test
    public void test_canFuse_leavesTheBranchCountToTheBuiltBranches() {
        givenConfig(true, "rrf", "");
        // some of these may decline to take part, so only the built branches can be counted
        assertTrue(new DefaultSearcher().canFuse(params(0, 10, null), List.of("a", "b", "c", "d", "e")));
    }

    @Test
    public void test_supportsEngineFusion() {
        assertTrue(new DefaultSearcher().supportsEngineFusion());
        assertFalse(new LegacySearcher().supportsEngineFusion());
    }

    @Test
    public void test_legacySearcher_refusesToFuse() {
        givenConfig(true, "rrf", "");
        // refusing is what keeps the branches from being dropped without anyone noticing
        assertTrue(new LegacySearcher()
                .searchWithSubQueries("q", params(0, 10, null), OptionalThing.empty(), List.of(subQuery("semantic_chunk")))
                .isEmpty());
    }

    @Test
    public void test_searchWithSubQueries_queryErrorPropagatesWithoutDisablingFusion() {
        givenConfig(true, "rrf", "");
        final ScriptedSearcher searcher = new ScriptedSearcher();
        searcher.failure = engineError("search_phase_execution_exception",
                "all shards failed; failed to parse date field [notadate] with format [strict_date_optional_time||epoch_millis]");
        // the caller turns this into the same error, and the same escaped retry, that a search
        // without engine-side fusion gets for this query
        assertThrows(InvalidQueryException.class,
                () -> searcher.searchWithSubQueries("q", params(0, 10, null), OptionalThing.empty(), List.of(subQuery("semantic_chunk"))));
        searcher.failure = null;
        assertTrue(searcher.searchWithSubQueries("q", params(0, 10, null), OptionalThing.empty(), List.of(subQuery("semantic_chunk")))
                .isPresent(), "one user's invalid query must not turn engine-side fusion off for everyone else");
        assertEquals(2, searcher.calls);
    }

    @Test
    public void test_searchWithSubQueries_transientErrorDoesNotDisableFusion() {
        givenConfig(true, "rrf", "");
        final ScriptedSearcher searcher = new ScriptedSearcher();
        searcher.failure = engineError("index_closed_exception", "closed");
        assertThrows(InvalidQueryException.class,
                () -> searcher.searchWithSubQueries("q", params(0, 10, null), OptionalThing.empty(), List.of(subQuery("semantic_chunk"))));
        searcher.failure = new IllegalStateException("The keyword condition did not produce a query to fuse.");
        // anything that is not the engine's answer is left to the caller, which falls back to
        // Fess for this search only
        assertThrows(IllegalStateException.class,
                () -> searcher.searchWithSubQueries("q", params(0, 10, null), OptionalThing.empty(), List.of(subQuery("semantic_chunk"))));
        searcher.failure = null;
        assertTrue(searcher.searchWithSubQueries("q", params(0, 10, null), OptionalThing.empty(), List.of(subQuery("semantic_chunk")))
                .isPresent(), "a closed index or a failed shard must not turn engine-side fusion off until restart");
        assertEquals(3, searcher.calls);
    }

    @Test
    public void test_searchWithSubQueries_hybridUnsupportedFallsBackAndRecovers() {
        givenConfig(true, "rrf", "");
        final ScriptedSearcher searcher = new ScriptedSearcher();
        final LogCapturingAppender log = LogCapturingAppender.attach(DefaultSearcher.class.getName(), Level.INFO);
        try {
            // what a cluster without the Neural Search plugin answers to a hybrid query
            searcher.failure = engineError("parsing_exception", "unknown query [hybrid]");
            assertTrue(searcher.searchWithSubQueries("q", params(0, 10, null), OptionalThing.empty(), List.of(subQuery("semantic_chunk")))
                    .isEmpty(), "a cluster that cannot run hybrid queries is answered by Fess-side fusion");
            assertTrue(searcher.searchWithSubQueries("q", params(0, 10, null), OptionalThing.empty(), List.of(subQuery("semantic_chunk")))
                    .isEmpty());
            // the plugin is installed: the next search is fused again without restarting Fess
            searcher.failure = null;
            assertTrue(searcher.searchWithSubQueries("q", params(0, 10, null), OptionalThing.empty(), List.of(subQuery("semantic_chunk")))
                    .isPresent(), "fusion must come back by itself once the cluster can run it");
            assertEquals(3, searcher.calls);

            final List<LogEvent> warnings = log.eventsAt(Level.WARN);
            assertEquals(2, warnings.size(), log.renderedEvents().toString());
            assertTrue(warnings.get(0).getMessage().getFormattedMessage().contains("unknown query [hybrid]"));
            assertNull(warnings.get(0).getThrown(), "the stack trace is for debug logging only");
            assertTrue(log.errors().isEmpty(), "falling back is not an error that needs someone to act");
        } finally {
            log.detach();
        }
    }

    @Test
    public void test_searchWithSubQueries_unsupportedCarriesTheStackTraceWhenDebugging() {
        givenConfig(true, "rrf", "");
        final ScriptedSearcher searcher = new ScriptedSearcher();
        final LogCapturingAppender log = LogCapturingAppender.attach(DefaultSearcher.class.getName(), Level.DEBUG);
        try {
            searcher.failure = engineError("parsing_exception", "unknown query [hybrid]");
            searcher.searchWithSubQueries("q", params(0, 10, null), OptionalThing.empty(), List.of(subQuery("semantic_chunk")));
            final List<LogEvent> warnings = log.eventsAt(Level.WARN);
            assertEquals(1, warnings.size(), log.renderedEvents().toString());
            assertNotNull(warnings.get(0).getThrown());
        } finally {
            log.detach();
        }
    }

    @Test
    public void test_searchWithSubQueries_missingPipelineProcessorFallsBackPerSearch() {
        givenConfig(true, "rrf", "");
        final ScriptedSearcher searcher = new ScriptedSearcher();
        // what a Neural Search plugin too old for the configured technique answers
        searcher.failure = engineError("illegal_argument_exception", "Invalid processor type score-ranker-processor");
        assertTrue(searcher.searchWithSubQueries("q", params(0, 10, null), OptionalThing.empty(), List.of(subQuery("semantic_chunk")))
                .isEmpty());
        searcher.failure = null;
        assertTrue(searcher.searchWithSubQueries("q", params(0, 10, null), OptionalThing.empty(), List.of(subQuery("semantic_chunk")))
                .isPresent(), "an upgraded plugin is used without restarting Fess");
        assertEquals(2, searcher.calls);
    }

    @Test
    public void test_searchWithSubQueries_pagePastTheLastHitFallsBackWithoutAnError() {
        givenConfig(true, "rrf", "");
        final ScriptedSearcher searcher = new ScriptedSearcher();
        final LogCapturingAppender log = LogCapturingAppender.attach(DefaultSearcher.class.getName(), Level.INFO);
        try {
            // what the engine answers to a fused request whose from is past the last fused hit
            searcher.failure = engineError("illegal_argument_exception",
                    "Reached end of search result, increase pagination_depth value to see more results");
            assertTrue(searcher.searchWithSubQueries("q", params(10, 10, null), OptionalThing.empty(), List.of(subQuery("semantic_chunk")))
                    .isEmpty(), "a page past the last hit is answered by Fess-side fusion, not reported as an invalid query");
            searcher.failure = null;
            assertTrue(searcher.searchWithSubQueries("q", params(0, 10, null), OptionalThing.empty(), List.of(subQuery("semantic_chunk")))
                    .isPresent(), "the next search is fused again");
            assertEquals(2, searcher.calls);
            assertTrue(log.eventsAt(Level.WARN).isEmpty(), log.renderedEvents().toString());
            assertTrue(log.errors().isEmpty(), log.renderedEvents().toString());
        } finally {
            log.detach();
        }
    }

    @Test
    public void test_searchWithSubQueries_laterPageFallsBackOnlyForABadRequest() {
        givenConfig(true, "rrf", "");
        final ScriptedSearcher searcher = new ScriptedSearcher();
        // the bad request is told apart by its status and the page, not by its wording
        searcher.failure = engineError("illegal_argument_exception", "any reason", RestStatus.BAD_REQUEST);
        assertTrue(searcher.searchWithSubQueries("q", params(10, 10, null), OptionalThing.empty(), List.of(subQuery("semantic_chunk")))
                .isEmpty(), "Fess-side fusion answers the page, or fails with the query's own error");
        // the first page is never rejected for its position, so its bad request is the query's
        assertThrows(InvalidQueryException.class,
                () -> searcher.searchWithSubQueries("q", params(0, 10, null), OptionalThing.empty(), List.of(subQuery("semantic_chunk"))));
        // a failure that is not a bad request is left to the caller on any page
        searcher.failure = engineError("search_phase_execution_exception", "all shards failed", RestStatus.SERVICE_UNAVAILABLE);
        assertThrows(InvalidQueryException.class,
                () -> searcher.searchWithSubQueries("q", params(10, 10, null), OptionalThing.empty(), List.of(subQuery("semantic_chunk"))));
        searcher.failure = new IllegalStateException("The keyword condition did not produce a query to fuse.");
        assertThrows(IllegalStateException.class,
                () -> searcher.searchWithSubQueries("q", params(10, 10, null), OptionalThing.empty(), List.of(subQuery("semantic_chunk"))));
        assertEquals(4, searcher.calls);
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
    public void test_buildPipelineSource_zScoreWithArithmeticMean() {
        givenConfig(true, "arithmetic_mean", "z_score", "", DEFAULT_RANK_CONSTANT);
        final Map<String, Object> pipeline = new DefaultSearcher().buildPipelineSource(List.of("default", "semantic_chunk"));
        final Map<String, Object> processor = firstProcessor(pipeline, "normalization-processor");
        @SuppressWarnings("unchecked")
        final Map<String, Object> normalization = (Map<String, Object>) processor.get("normalization");
        assertEquals("z_score", normalization.get("technique"));
    }

    @Test
    public void test_buildPipelineSource_refusesZScoreWithOtherMeans() {
        // the engine fails every request for these pairs, since z_score only combines with
        // arithmetic_mean, so they are refused here where the settings can be named
        givenConfig(true, "geometric_mean", "z_score", "", DEFAULT_RANK_CONSTANT);
        assertNull(new DefaultSearcher().buildPipelineSource(List.of("default", "semantic_chunk")));
        givenConfig(true, "harmonic_mean", "z_score", "", DEFAULT_RANK_CONSTANT);
        assertNull(new DefaultSearcher().buildPipelineSource(List.of("default", "semantic_chunk")));
    }

    @Test
    public void test_buildPipelineSource_refusesUnknownCombination() {
        // the engine rejects the whole request for a technique it does not know
        givenConfig(true, "median", "min_max", "", DEFAULT_RANK_CONSTANT);
        assertNull(new DefaultSearcher().buildPipelineSource(List.of("default", "semantic_chunk")));
    }

    @Test
    public void test_buildPipelineSource_refusesUnknownNormalization() {
        givenConfig(true, "arithmetic_mean", "sigmoid", "", DEFAULT_RANK_CONSTANT);
        assertNull(new DefaultSearcher().buildPipelineSource(List.of("default", "semantic_chunk")));
    }

    @Test
    public void test_buildPipelineSource_rrfIgnoresTheNormalization() {
        givenConfig(true, "rrf", "sigmoid", "", DEFAULT_RANK_CONSTANT);
        assertNotNull(
                firstProcessor(new DefaultSearcher().buildPipelineSource(List.of("default", "semantic_chunk")), "score-ranker-processor"));
    }

    @Test
    public void test_buildPipelineSource_sendsTechniquesInLowerCase() {
        // the engine looks the names up exactly
        givenConfig(true, " Geometric_Mean ", "L2", "", DEFAULT_RANK_CONSTANT);
        final Map<String, Object> processor =
                firstProcessor(new DefaultSearcher().buildPipelineSource(List.of("default", "semantic_chunk")), "normalization-processor");
        assertEquals(Map.of("technique", "l2"), processor.get("normalization"));
        assertEquals(Map.of("technique", "geometric_mean"), processor.get("combination"));
    }

    @Test
    public void test_buildPipelineSource_minMaxAndL2WithEveryMean() {
        for (final String normalization : List.of("min_max", "l2")) {
            for (final String technique : List.of("arithmetic_mean", "geometric_mean", "harmonic_mean")) {
                givenConfig(true, technique, normalization, "", DEFAULT_RANK_CONSTANT);
                assertNotNull(new DefaultSearcher().buildPipelineSource(List.of("default", "semantic_chunk")),
                        normalization + " with " + technique);
            }
        }
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
    public void test_canResolveWeights_refusesOnlyWeightsNoBranchesCouldMatch() {
        final DefaultSearcher searcher = new DefaultSearcher();
        givenConfig(true, "rrf", "default:0.7,semantic_chunk:0.3");
        assertTrue(searcher.canResolveWeights(List.of("semantic_chunk")));
        // multi_modal may yet decline to take part, which would leave the branches the weights name
        assertTrue(searcher.canResolveWeights(List.of("semantic_chunk", "multi_modal")));
        givenConfig(true, "rrf", "default:0.5,multi_modal:0.5");
        assertFalse(searcher.canResolveWeights(List.of("semantic_chunk")));
        givenConfig(true, "rrf", "semantic_chunk:1.0");
        assertFalse(searcher.canResolveWeights(List.of("semantic_chunk")), "the main searcher is always a branch");
        givenConfig(true, "rrf", "default:0.5,semantic_chunk:0.4");
        assertFalse(searcher.canResolveWeights(List.of("semantic_chunk")));
        givenConfig(true, "rrf", "default:half");
        assertFalse(searcher.canResolveWeights(List.of("semantic_chunk")));
        givenConfig(true, "rrf", "");
        assertTrue(searcher.canResolveWeights(List.of("semantic_chunk")));
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
        givenConfig(engineEnabled, technique, "min_max", weights, rankConstant);
    }

    private void givenConfig(final boolean engineEnabled, final String technique, final String normalization, final String weights,
            final int rankConstant) {
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
                return normalization;
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

    /**
     * Builds the exception a failed search reaches the searcher as: the engine's error, as the
     * HTTP client decodes it, wrapped by SearchEngineClient.
     */
    private static InvalidQueryException engineError(final String type, final String reason) {
        return engineError(type, reason, RestStatus.BAD_REQUEST);
    }

    private static InvalidQueryException engineError(final String type, final String reason, final RestStatus status) {
        final OpenSearchStatusException cause =
                new OpenSearchStatusException("OpenSearch exception [type=" + type + ", reason=" + reason + "]", status, null);
        return new InvalidQueryException(messages -> messages.addErrorsInvalidQueryCannotProcess(UserMessages.GLOBAL_PROPERTY_KEY),
                "Failed to process the query.", cause);
    }

    /** A searcher whose fused request fails, or succeeds, as the test says. */
    private static class ScriptedSearcher extends DefaultSearcher {
        RuntimeException failure;
        int calls;

        @Override
        protected SearchResult execute(final SearchRequestParams params, final SearchCondition<SearchRequestBuilder> condition) {
            calls++;
            if (failure != null) {
                throw failure;
            }
            return SearchResult.create().build();
        }
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
