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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.lucene.search.TotalHits.Relation;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient.SearchCondition;
import org.codelibs.fess.rank.fusion.SearchResult.SearchResultBuilder;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.QueryResponseList;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.codelibs.fesen.opensearch.action.search.SearchRequestBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;

public class RankFusionProcessorTest extends UnitFessTestCase {

    private static final String ID_FIELD = "_id";

    @Test
    public void test_warnsWhenLegacySemanticIsAllowedButChunkIsNot() {
        final RankFusionProcessor processor = new RankFusionProcessor();

        assertTrue(processor.isLegacySemanticAllowlist(new String[] { "default", "semantic" }));
        assertFalse(processor.isLegacySemanticAllowlist(new String[] { "default", "semantic", "semantic_chunk" }));
        assertFalse(processor.isLegacySemanticAllowlist(new String[] { "default" }));
        assertFalse(processor.isLegacySemanticAllowlist(new String[0]));
    }

    @Test
    public void test_default_1000docs_10size() throws Exception {
        String query = "*";
        int allRecordCount = 1000;
        int pageSize = 10;
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSearcher(new TestMainSearcher(allRecordCount));
            rankFusionProcessor.init();

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(allRecordCount, list.getAllRecordCount());
                assertEquals(100, list.getAllPageCount());
                assertEquals(10, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(1, list.getCurrentStartRecordNumber());
                assertEquals(0, list.getOffset());
                assertEquals(10, list.getPageSize());
                assertEquals(0, list.getStart());
                assertEquals("0", list.get(0).get(ID_FIELD));
                assertEquals("9", list.get(9).get(ID_FIELD));
            } else {
                fail();
            }

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(10, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(allRecordCount, list.getAllRecordCount());
                assertEquals(100, list.getAllPageCount());
                assertEquals(20, list.getCurrentEndRecordNumber());
                assertEquals(2, list.getCurrentPageNumber());
                assertEquals(11, list.getCurrentStartRecordNumber());
                assertEquals(0, list.getOffset());
                assertEquals(10, list.getPageSize());
                assertEquals(10, list.getStart());
                assertEquals("10", list.get(0).get(ID_FIELD));
                assertEquals("19", list.get(9).get(ID_FIELD));
            } else {
                fail();
            }

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(990, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(allRecordCount, list.getAllRecordCount());
                assertEquals(100, list.getAllPageCount());
                assertEquals(1000, list.getCurrentEndRecordNumber());
                assertEquals(100, list.getCurrentPageNumber());
                assertEquals(991, list.getCurrentStartRecordNumber());
                assertEquals(0, list.getOffset());
                assertEquals(10, list.getPageSize());
                assertEquals(990, list.getStart());
                assertEquals("990", list.get(0).get(ID_FIELD));
                assertEquals("999", list.get(9).get(ID_FIELD));
            } else {
                fail();
            }
        }
    }

    @Test
    public void test_1searcher10_45_45_1000docs_100size() throws Exception {
        String query = "*";
        int allRecordCount = 1000;
        int pageSize = 100;
        int offset = 45;
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSearcher(new TestMainSearcher(allRecordCount));
            rankFusionProcessor.register(new TestSubSearcher(10, 45, 45));
            rankFusionProcessor.init();

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(allRecordCount + offset, list.getAllRecordCount());
                assertEquals(11, list.getAllPageCount());
                assertEquals(100, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(1, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(0, list.getStart());
                assertEquals("0", list.get(0).get(ID_FIELD));
                assertEquals("9", list.get(1).get(ID_FIELD));
                assertEquals("1", list.get(2).get(ID_FIELD));
                assertEquals("8", list.get(3).get(ID_FIELD));
                assertEquals("2", list.get(4).get(ID_FIELD));
                assertEquals("7", list.get(5).get(ID_FIELD));
                assertEquals("3", list.get(6).get(ID_FIELD));
                assertEquals("6", list.get(7).get(ID_FIELD));
                assertEquals("4", list.get(8).get(ID_FIELD));
                assertEquals("5", list.get(9).get(ID_FIELD));
            } else {
                fail();
            }

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, offset),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(allRecordCount + offset, list.getAllRecordCount());
                assertEquals(11, list.getAllPageCount());
                assertEquals(100, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(1, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(0, list.getStart());
                assertEquals("0", list.get(0).get(ID_FIELD));
                assertEquals("9", list.get(1).get(ID_FIELD));
                assertEquals("1", list.get(2).get(ID_FIELD));
                assertEquals("8", list.get(3).get(ID_FIELD));
                assertEquals("2", list.get(4).get(ID_FIELD));
                assertEquals("7", list.get(5).get(ID_FIELD));
                assertEquals("3", list.get(6).get(ID_FIELD));
                assertEquals("6", list.get(7).get(ID_FIELD));
                assertEquals("4", list.get(8).get(ID_FIELD));
                assertEquals("5", list.get(9).get(ID_FIELD));
            } else {
                fail();
            }

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(100, pageSize, offset),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(allRecordCount + offset, list.getAllRecordCount());
                assertEquals(11, list.getAllPageCount());
                assertEquals(200, list.getCurrentEndRecordNumber());
                assertEquals(2, list.getCurrentPageNumber());
                assertEquals(101, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(100, list.getStart());
                assertEquals("55", list.get(0).get(ID_FIELD));
                assertEquals("154", list.get(99).get(ID_FIELD));
            } else {
                fail();
            }

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(900, pageSize, offset),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(allRecordCount + offset, list.getAllRecordCount());
                assertEquals(11, list.getAllPageCount());
                assertEquals(1000, list.getCurrentEndRecordNumber());
                assertEquals(10, list.getCurrentPageNumber());
                assertEquals(901, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(900, list.getStart());
                assertEquals("855", list.get(0).get(ID_FIELD));
                assertEquals("954", list.get(99).get(ID_FIELD));
            } else {
                fail();
            }

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(1000, pageSize, offset),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(offset, list.size());
                assertEquals(allRecordCount + offset, list.getAllRecordCount());
                assertEquals(11, list.getAllPageCount());
                assertEquals(1045, list.getCurrentEndRecordNumber());
                assertEquals(11, list.getCurrentPageNumber());
                assertEquals(1001, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(1000, list.getStart());
                assertEquals("955", list.get(0).get(ID_FIELD));
                assertEquals("999", list.get(44).get(ID_FIELD));
            } else {
                fail();
            }
        }
    }

    @Test
    public void test_1searcher0_0docs_100size() throws Exception {
        String query = "*";
        int allRecordCount = 0;
        int pageSize = 100;
        int offset = 0;
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSearcher(new TestMainSearcher(allRecordCount));
            rankFusionProcessor.register(new TestSubSearcher(0, 0, 0));
            rankFusionProcessor.init();

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(0, list.size());
                assertEquals(0, list.getAllRecordCount());
                assertEquals(1, list.getAllPageCount());
                assertEquals(0, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(0, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(0, list.getStart());
            } else {
                fail();
            }
        }
    }

    @Test
    public void test_1searcher0_10docs_100size() throws Exception {
        String query = "*";
        int allRecordCount = 10;
        int pageSize = 100;
        int offset = 0;
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSearcher(new TestMainSearcher(allRecordCount));
            rankFusionProcessor.register(new TestSubSearcher(0, 0, 0));
            rankFusionProcessor.init();

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(10, list.size());
                assertEquals(10, list.getAllRecordCount());
                assertEquals(1, list.getAllPageCount());
                assertEquals(10, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(1, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(0, list.getStart());
            } else {
                fail();
            }
        }
    }

    @Test
    public void test_1searcher0_1000docs_100size() throws Exception {
        String query = "*";
        int allRecordCount = 1000;
        int pageSize = 100;
        int offset = 0;
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSearcher(new TestMainSearcher(allRecordCount));
            rankFusionProcessor.register(new TestSubSearcher(0, 0, 0));
            rankFusionProcessor.init();

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(1000, list.getAllRecordCount());
                assertEquals(10, list.getAllPageCount());
                assertEquals(100, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(1, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(0, list.getStart());
            } else {
                fail();
            }
        }
    }

    @Test
    public void test_1searcher10_0docs_100size() throws Exception {
        String query = "*";
        int allRecordCount = 0;
        int pageSize = 100;
        int offset = 10;
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSearcher(new TestMainSearcher(allRecordCount));
            rankFusionProcessor.register(new TestSubSearcher(10, 0, 0));
            rankFusionProcessor.init();

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(10, list.size());
                assertEquals(10, list.getAllRecordCount());
                assertEquals(1, list.getAllPageCount());
                assertEquals(10, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(1, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(0, list.getStart());
            } else {
                fail();
            }
        }
    }

    @Test
    public void test_1searcher10_10docs_100size() throws Exception {
        String query = "*";
        int allRecordCount = 10;
        int pageSize = 100;
        int offset = 0;
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSearcher(new TestMainSearcher(allRecordCount));
            rankFusionProcessor.register(new TestSubSearcher(10, 0, 0));
            rankFusionProcessor.init();

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(10, list.size());
                assertEquals(10, list.getAllRecordCount());
                assertEquals(1, list.getAllPageCount());
                assertEquals(10, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(1, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(0, list.getStart());
            } else {
                fail();
            }
        }
    }

    @Test
    public void test_1searcher10_1000docs_100size() throws Exception {
        String query = "*";
        int allRecordCount = 1000;
        int pageSize = 100;
        int offset = 0;
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSearcher(new TestMainSearcher(allRecordCount));
            rankFusionProcessor.register(new TestSubSearcher(10, 0, 0));
            rankFusionProcessor.init();

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(1000, list.getAllRecordCount());
                assertEquals(10, list.getAllPageCount());
                assertEquals(100, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(1, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(0, list.getStart());
            } else {
                fail();
            }
        }
    }

    @Test
    public void test_1searcher1000_0docs_100size() throws Exception {
        String query = "*";
        int allRecordCount = 0;
        int pageSize = 100;
        int offset = 100;
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSearcher(new TestMainSearcher(allRecordCount));
            rankFusionProcessor.register(new TestSubSearcher(0, 0, 1000));
            rankFusionProcessor.init();

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(100, list.getAllRecordCount());
                assertEquals(1, list.getAllPageCount());
                assertEquals(100, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(1, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(0, list.getStart());
            } else {
                fail();
            }
        }
    }

    @Test
    public void test_1searcher1000_10docs_100size() throws Exception {
        String query = "*";
        int allRecordCount = 10;
        int pageSize = 100;
        int offset = 90;
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSearcher(new TestMainSearcher(allRecordCount));
            rankFusionProcessor.register(new TestSubSearcher(0, 0, 1000));
            rankFusionProcessor.init();

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(100, list.getAllRecordCount());
                assertEquals(1, list.getAllPageCount());
                assertEquals(100, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(1, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(0, list.getStart());
            } else {
                fail();
            }
        }
    }

    @Test
    public void test_1searcher1000_1000docs_100size() throws Exception {
        String query = "*";
        int allRecordCount = 1000;
        int pageSize = 100;
        int offset = 50;
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSearcher(new TestMainSearcher(allRecordCount));
            rankFusionProcessor.register(new TestSubSearcher(0, 0, 1000));
            rankFusionProcessor.init();

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(1050, list.getAllRecordCount());
                assertEquals(11, list.getAllPageCount());
                assertEquals(100, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(1, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(0, list.getStart());
            } else {
                fail();
            }
        }
    }

    @Test
    public void test_mainSearcher_propagatesInvalidAccessToken() throws Exception {
        // RoleQueryHelper raises this while the query is being built. The generic catch used to
        // swallow it and return an empty list, which reports a refused request as a successful
        // search of nothing and logs a stack trace for a caller-supplied credential every time.
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSearcher(new RankFusionSearcher() {
                @Override
                protected SearchResult search(final String query, final SearchRequestParams params,
                        final OptionalThing<FessUserBean> userBean) {
                    throw new org.codelibs.fess.exception.InvalidAccessTokenException("invalid_token",
                            "The access token is not registered.");
                }
            });
            rankFusionProcessor.init();
            try {
                rankFusionProcessor.search("*", new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());
                fail();
            } catch (final org.codelibs.fess.exception.InvalidAccessTokenException e) {
                // expected
            }
        }
    }

    @Test
    public void test_engineFusion_buildsTheSubQueryOnceWhenFused() throws Exception {
        givenEngineFusion("");
        final FusingMainSearcher main = new FusingMainSearcher();
        final CountingSubSearcher sub = new CountingSubSearcher();
        try (RankFusionProcessor processor = newEngineFusionProcessor(main, sub)) {
            processor.search("q", fusionParams(0), OptionalThing.empty());
        }
        assertEquals(1, main.fusedCount.get());
        assertEquals(1, sub.buildCount.get());
        assertEquals(0, sub.searchCount.get());
    }

    @Test
    public void test_engineFusion_doesNotBuildTheSubQueryPastThePaginationDepth() throws Exception {
        givenEngineFusion("");
        final FusingMainSearcher main = new FusingMainSearcher();
        final CountingSubSearcher sub = new CountingSubSearcher();
        try (RankFusionProcessor processor = newEngineFusionProcessor(main, sub)) {
            // a second page of 10 reaches past the depth of 10, so the search is fused in Fess
            processor.search("q", fusionParams(10), OptionalThing.empty());
        }
        assertEquals(0, main.fusedCount.get());
        assertEquals(0, sub.buildCount.get(), "a branch built for a search that is not fused embeds the query twice");
        assertEquals(1, sub.searchCount.get());
    }

    @Test
    public void test_engineFusion_doesNotBuildTheSubQueryWithUnusableWeights() throws Exception {
        givenEngineFusion("fusing_main:0.5,counting_sub:0.4");
        final FusingMainSearcher main = new FusingMainSearcher();
        final CountingSubSearcher sub = new CountingSubSearcher();
        try (RankFusionProcessor processor = newEngineFusionProcessor(main, sub)) {
            processor.search("q", fusionParams(0), OptionalThing.empty());
        }
        assertEquals(0, main.fusedCount.get());
        assertEquals(0, sub.buildCount.get(), "weights that can never be used must be refused before a branch is built");
        assertEquals(1, sub.searchCount.get());
    }

    @Test
    public void test_engineFusion_aSearchThatIsNotFusedDoesNotWarn() throws Exception {
        givenEngineFusion("");
        final LogCapturingAppender appender = LogCapturingAppender.attach(RankFusionProcessor.class);
        try (RankFusionProcessor processor = newEngineFusionProcessor(new FusingMainSearcher(), new CountingSubSearcher())) {
            processor.search("q", fusionParams(10), OptionalThing.empty());
            // a page past the depth is a property of this search, not of the main searcher
            assertTrue(appender.warnings().isEmpty(), appender.warnings().toString());
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_engineFusion_warnsOnEverySearchWhenTheMainSearcherCannotFuse() throws Exception {
        givenEngineFusion("");
        final CountingSubSearcher sub = new CountingSubSearcher();
        final LogCapturingAppender appender = LogCapturingAppender.attach(RankFusionProcessor.class);
        try (RankFusionProcessor processor = newEngineFusionProcessor(new TestMainSearcher(100), sub)) {
            processor.search("q", fusionParams(0), OptionalThing.empty());
            processor.search("q", fusionParams(0), OptionalThing.empty());
            // reported for every search, so it is not lost after the first one since startup
            assertEquals(2, appender.warnings().size(), appender.warnings().toString());
        } finally {
            appender.detach();
        }
        assertEquals(0, sub.buildCount.get());
    }

    private static SearchRequestParams fusionParams(final int start) {
        return new TestSearchRequestParams(start, 10, 0) {
            @Override
            public Map<String, String[]> getConditions() {
                return Map.of();
            }
        };
    }

    private RankFusionProcessor newEngineFusionProcessor(final RankFusionSearcher main, final RankFusionSearcher sub) {
        final RankFusionProcessor processor = new RankFusionProcessor();
        processor.setSearcher(main);
        processor.register(sub);
        processor.init();
        return processor;
    }

    private void givenEngineFusion(final String weights) {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isRankFusionEngineEnabled() {
                return true;
            }

            @Override
            public String getRankFusionCombinationTechnique() {
                return "rrf";
            }

            @Override
            public String getRankFusionCombinationWeights() {
                return weights;
            }

            @Override
            public Integer getRankFusionPaginationDepthAsInteger() {
                return Integer.valueOf(10);
            }

            @Override
            public Integer getRankFusionTimeoutAsInteger() {
                return Integer.valueOf(10000);
            }

            @Override
            public Integer getPagingSearchPageMaxSizeAsInteger() {
                return Integer.valueOf(100);
            }

            @Override
            public Integer getRankFusionWindowSizeAsInteger() {
                return Integer.valueOf(200);
            }

            @Override
            public Integer getRankFusionRankConstantAsInteger() {
                return Integer.valueOf(20);
            }

            @Override
            public Integer getRankFusionThreadsAsInteger() {
                return Integer.valueOf(-1);
            }

            @Override
            public String getRankFusionScoreField() {
                return "rf_score";
            }

            @Override
            public String getIndexFieldId() {
                return ID_FIELD;
            }
        });
    }

    /**
     * A main searcher that fuses without a search engine: the fused request is answered by a
     * canned result instead of being sent.
     */
    static class FusingMainSearcher extends DefaultSearcher {

        final AtomicInteger fusedCount = new AtomicInteger();

        FusingMainSearcher() {
            name = "fusing_main";
        }

        @Override
        protected SearchResult execute(final SearchRequestParams params, final SearchCondition<SearchRequestBuilder> condition) {
            return new TestMainSearcher(100).search(null, params, OptionalThing.empty());
        }

        @Override
        protected Optional<SearchResult> searchWithSubQueries(final String query, final SearchRequestParams params,
                final OptionalThing<FessUserBean> userBean, final List<QueryBuilder> subQueries) {
            final Optional<SearchResult> result = super.searchWithSubQueries(query, params, userBean, subQueries);
            result.ifPresent(r -> fusedCount.incrementAndGet());
            return result;
        }
    }

    /**
     * A branch that counts how often its query is built - for the semantic branch, each build
     * embeds the query - and how often it runs a search of its own.
     */
    static class CountingSubSearcher extends TestMainSearcher {

        final AtomicInteger buildCount = new AtomicInteger();

        final AtomicInteger searchCount = new AtomicInteger();

        CountingSubSearcher() {
            super(100);
            name = "counting_sub";
        }

        @Override
        protected Optional<QueryBuilder> buildSubQuery(final String query, final SearchRequestParams params,
                final OptionalThing<FessUserBean> userBean) {
            buildCount.incrementAndGet();
            return Optional.of(QueryBuilders.matchAllQuery());
        }

        @Override
        protected SearchResult search(final String query, final SearchRequestParams params, final OptionalThing<FessUserBean> userBean) {
            searchCount.incrementAndGet();
            return super.search(query, params, userBean);
        }
    }

    static class TestMainSearcher extends RankFusionSearcher {

        private long allRecordCount;

        TestMainSearcher(int allRecordCount) {
            this.allRecordCount = allRecordCount;
        }

        @Override
        protected SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean) {
            int start = params.getStartPosition();
            int size = params.getPageSize();
            SearchResultBuilder builder = SearchResult.create();
            for (int i = start; i < start + size && i < allRecordCount; i++) {
                Map<String, Object> doc = new HashMap<>();
                doc.put(ID_FIELD, Integer.toString(i));
                doc.put("score", 1.0f / (i + 1));
                builder.addDocument(doc);
            }
            builder.allRecordCount(allRecordCount);
            if (allRecordCount < 10000) {
                builder.allRecordCountRelation(Relation.EQUAL_TO.toString());
            }
            return builder.build();
        }
    }

    static class TestSubSearcher extends RankFusionSearcher {

        private int mainSize;
        private int inSize;
        private int outSize;

        TestSubSearcher(int mainSize, int inSize, int outSize) {
            this.mainSize = mainSize;
            this.inSize = inSize;
            this.outSize = outSize;
        }

        @Override
        protected SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean) {
            SearchResultBuilder builder = SearchResult.create();
            for (int i = 0; i < mainSize; i++) {
                Map<String, Object> doc = new HashMap<>();
                doc.put(ID_FIELD, Integer.toString(mainSize - i - 1));
                doc.put("score", 1.0f / (i + 2));
                builder.addDocument(doc);
            }
            for (int i = 100; i < inSize + 100; i++) {
                Map<String, Object> doc = new HashMap<>();
                doc.put(ID_FIELD, Integer.toString(i));
                doc.put("score", 1.0f / (mainSize + i + 2));
                builder.addDocument(doc);
            }
            for (int i = 200; i < outSize + 200; i++) {
                Map<String, Object> doc = new HashMap<>();
                doc.put(ID_FIELD, Integer.toString(i));
                doc.put("score", 1.0f / (mainSize + i + 3));
                builder.addDocument(doc);
            }
            builder.allRecordCount(mainSize + inSize + outSize);
            return builder.build();
        }
    }

    static class TestSearchRequestParams extends SearchRequestParams {

        private int startPosition;

        private int pageSize;

        private int offset;

        TestSearchRequestParams(int startPosition, int pageSize, int offset) {
            this.startPosition = startPosition;
            this.pageSize = pageSize;
            this.offset = offset;
        }

        @Override
        public String getQuery() {
            return null;
        }

        @Override
        public Map<String, String[]> getFields() {
            return null;
        }

        @Override
        public Map<String, String[]> getConditions() {
            return null;
        }

        @Override
        public String[] getLanguages() {
            return null;
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
            return null;
        }

        @Override
        public String getSort() {
            return null;
        }

        @Override
        public int getStartPosition() {
            return startPosition;
        }

        @Override
        public int getOffset() {
            return offset;
        }

        @Override
        public int getPageSize() {
            return pageSize;
        }

        @Override
        public String[] getExtraQueries() {
            return null;
        }

        @Override
        public Object getAttribute(String name) {
            return null;
        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public SearchRequestType getType() {
            return null;
        }

        @Override
        public String getSimilarDocHash() {
            return null;
        }

    }
}
