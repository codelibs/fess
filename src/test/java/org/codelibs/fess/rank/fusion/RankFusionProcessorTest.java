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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.lucene.search.TotalHits.Relation;
import org.codelibs.fess.embedding.EmbeddingClientManager;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient.SearchCondition;
import org.codelibs.fess.query.StructuredQuerySplitter;
import org.codelibs.fess.query.StubProcessorSplitter;
import org.codelibs.fess.rank.fusion.SearchResult.SearchResultBuilder;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.QueryResponseList;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.dbflute.utflute.mocklet.MockletHttpServletRequestImpl;
import org.junit.jupiter.api.Test;
import org.lastaflute.core.message.UserMessages;
import org.lastaflute.di.core.ExternalContext;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.codelibs.fesen.opensearch.OpenSearchStatusException;
import org.codelibs.fesen.opensearch.action.search.SearchRequestBuilder;
import org.codelibs.fesen.opensearch.action.search.SearchResponse;
import org.codelibs.fesen.opensearch.core.rest.RestStatus;
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
    public void test_availableSearchers_followRankFusionSearchersChangedAfterInit() throws Exception {
        final String before = System.getProperty("rank.fusion.searchers");
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSearcher(new TestMainSearcher(10));
            rankFusionProcessor.register(new TestSubSearcher(0, 0, 0) {
                @Override
                public String getName() {
                    return "sub";
                }
            });
            System.clearProperty("rank.fusion.searchers");
            rankFusionProcessor.init();
            assertEquals(2, rankFusionProcessor.getAvailableSearchers().length);

            // Set at runtime through the admin screen's System Property field, without update().
            System.setProperty("rank.fusion.searchers", "sub");
            final RankFusionSearcher[] searchers = rankFusionProcessor.getAvailableSearchers();
            assertEquals(1, searchers.length);
            assertEquals("sub", searchers[0].getName());

            System.clearProperty("rank.fusion.searchers");
            assertEquals(2, rankFusionProcessor.getAvailableSearchers().length);
        } finally {
            if (before == null) {
                System.clearProperty("rank.fusion.searchers");
            } else {
                System.setProperty("rank.fusion.searchers", before);
            }
        }
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
    public void test_engineFusion_refusesAPagePastThePaginationDepth() throws Exception {
        givenEngineFusion("");
        final FusingMainSearcher main = new FusingMainSearcher();
        final CountingSubSearcher sub = new CountingSubSearcher();
        try (RankFusionProcessor processor = newEngineFusionProcessor(main, sub)) {
            // a second page of 10 starts past the depth of 10: it is refused the way a page past
            // index.max_result_window is, not answered with keyword-only results
            assertThrows(InvalidQueryException.class, () -> processor.search("q", fusionParams(10), OptionalThing.empty()));
        }
        assertEquals(0, main.fusedCount.get());
        assertEquals(0, sub.searchCount.get(), "a page past the depth must not be answered by Fess-side fusion");
    }

    @Test
    public void test_engineFusion_buildsTheBranchWithoutAWindowOfItsOwn() throws Exception {
        givenEngineFusion("", 1000, 10000);
        final CountingSubSearcher sub = new CountingSubSearcher();
        try (RankFusionProcessor processor = newEngineFusionProcessor(new FusingMainSearcher(), sub)) {
            processor.search("q", fusionParams(990), OptionalThing.empty());
        }
        // the engine pages the fused list; sized to nothing, the semantic branch asks each shard
        // for content_chunker.search.knn.k neighbours instead of the pagination depth
        assertEquals(1, sub.buildCount.get());
        assertEquals(0, sub.builtWith.getStartPosition());
        assertEquals(0, sub.builtWith.getPageSize());
    }

    @Test
    public void test_engineFusion_stopsThePagerAtThePaginationDepth() throws Exception {
        givenEngineFusion("", 1000, 10000);
        // the fused list of one shard, and of five shards whose union is larger: the pager stops
        // at the depth either way
        for (final int total : new int[] { 1500, 5000 }) {
            try (RankFusionProcessor processor = newEngineFusionProcessor(new FusingMainSearcher(total), new CountingSubSearcher())) {
                final QueryResponseList first = (QueryResponseList) processor.search("q", fusionParams(0), OptionalThing.empty());
                // the count is reported as the engine counted it
                assertEquals(total, (int) first.getAllRecordCount());
                assertEquals(Relation.GREATER_THAN_OR_EQUAL_TO.toString(), first.getAllRecordCountRelation());
                assertEquals(100, first.getAllPageCount());
                assertTrue(first.isExistNextPage());

                final QueryResponseList last = (QueryResponseList) processor.search("q", fusionParams(990), OptionalThing.empty());
                assertEquals(10, last.size());
                assertEquals(100, last.getAllPageCount());
                assertEquals(100, last.getCurrentPageNumber());
                assertFalse(last.isExistNextPage(), "no page is offered past the depth");
                assertEquals("100", last.getPageNumberList().get(last.getPageNumberList().size() - 1));
                assertEquals(1000, last.getCurrentEndRecordNumber());

                assertThrows(InvalidQueryException.class, () -> processor.search("q", fusionParams(1000), OptionalThing.empty()));
            }
        }
    }

    @Test
    public void test_engineFusion_countBelowTheDepthIsExact() throws Exception {
        givenEngineFusion("", 1000, 10000);
        try (RankFusionProcessor processor = newEngineFusionProcessor(new FusingMainSearcher(300), new CountingSubSearcher())) {
            final QueryResponseList list = (QueryResponseList) processor.search("q", fusionParams(0), OptionalThing.empty());
            assertEquals(300, list.getAllRecordCount());
            assertEquals(Relation.EQUAL_TO.toString(), list.getAllRecordCountRelation());
            assertEquals(30, list.getAllPageCount());
        }
    }

    @Test
    public void test_engineFusion_countAtTheDepthIsALowerBound() throws Exception {
        givenEngineFusion("", 1000, 10000);
        try (RankFusionProcessor processor = newEngineFusionProcessor(new FusingMainSearcher(1000), new CountingSubSearcher())) {
            final QueryResponseList list = (QueryResponseList) processor.search("q", fusionParams(0), OptionalThing.empty());
            // once a branch matches the depth, the engine can count fewer hits than match and
            // still call the count exact
            assertEquals(1000, list.getAllRecordCount());
            assertEquals(Relation.GREATER_THAN_OR_EQUAL_TO.toString(), list.getAllRecordCountRelation());
            assertEquals(100, list.getAllPageCount());
        }
    }

    @Test
    public void test_engineFusion_pagerNeverPassesTheResultWindow() throws Exception {
        givenEngineFusion("", 20000, 10000);
        try (RankFusionProcessor processor = newEngineFusionProcessor(new FusingMainSearcher(50000), new CountingSubSearcher())) {
            final QueryResponseList list = (QueryResponseList) processor.search("q", fusionParams(0), OptionalThing.empty());
            assertEquals(1000, list.getAllPageCount());
            assertThrows(InvalidQueryException.class, () -> processor.search("q", fusionParams(10000), OptionalThing.empty()));
        }
    }

    @Test
    public void test_searchesThatAreNotFusedInTheEngineKeepTheirPager() throws Exception {
        // engine-side fusion off: Fess fuses, and pages as far as the index lets it
        givenEngineFusion("", 1000, 10000, false);
        try (RankFusionProcessor processor = newEngineFusionProcessor(new TestMainSearcher(5000), new TestSubSearcher(0, 0, 0))) {
            final QueryResponseList list = (QueryResponseList) processor.search("q", fusionParams(990), OptionalThing.empty());
            assertEquals(500, list.getAllPageCount());
            assertTrue(list.isExistNextPage());
            assertEquals(Relation.EQUAL_TO.toString(), list.getAllRecordCountRelation());
            final QueryResponseList past = (QueryResponseList) processor.search("q", fusionParams(1000), OptionalThing.empty());
            assertEquals(10, past.size(), "a page past the depth is answered as before");
        }
        // on, but the search cannot be fused in the engine
        givenEngineFusion("", 1000, 10000);
        try (RankFusionProcessor processor = newEngineFusionProcessor(new FusingMainSearcher(5000), new CountingSubSearcher())) {
            final QueryResponseList list = (QueryResponseList) processor.search("q", sortedParams(990), OptionalThing.empty());
            assertEquals(500, list.getAllPageCount());
            assertTrue(list.isExistNextPage());
        }
        // a single searcher: no fusion at all
        final RankFusionProcessor single = new RankFusionProcessor();
        single.setSearcher(new TestMainSearcher(5000));
        single.init();
        try (single) {
            final QueryResponseList list = (QueryResponseList) single.search("q", fusionParams(990), OptionalThing.empty());
            assertEquals(500, list.getAllPageCount());
            assertEquals(Relation.EQUAL_TO.toString(), list.getAllRecordCountRelation());
        }
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
            processor.search("q", sortedParams(0), OptionalThing.empty());
            // a sorted search is a property of this search, not of the main searcher
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

    // -------------------------------------------------------------------------------------
    //                                        the query is embedded once, however the search ends
    //                                        ----------------------------------------------------

    @Test
    public void test_engineFusion_embedsTheQueryOnceWhenFused() throws Exception {
        givenEngineFusion("fusing_main:0.5,semantic_chunk:0.5");
        final RefusingMainSearcher main = new RefusingMainSearcher();
        final EmbeddingSemanticSearcher semantic = new EmbeddingSemanticSearcher();
        try (RankFusionProcessor processor = newEngineFusionProcessor(main, semantic)) {
            processor.search("q", fusionParams(0), OptionalThing.empty());
        }
        assertEquals(1, main.fusedCount.get());
        assertEquals(0, semantic.searchCount.get());
        assertEquals(1, semantic.embedCount.get());
    }

    @Test
    public void test_engineFusion_embedsTheQueryOnceWhenFusedByFess() throws Exception {
        givenEngineFusion("", 10, 10000, false);
        final EmbeddingSemanticSearcher semantic = new EmbeddingSemanticSearcher();
        try (RankFusionProcessor processor = newEngineFusionProcessor(new RefusingMainSearcher(), semantic)) {
            processor.search("q", fusionParams(0), OptionalThing.empty());
        }
        assertEquals(1, semantic.searchCount.get());
        assertEquals(1, semantic.embedCount.get());
    }

    @Test
    public void test_engineFusion_embedsTheQueryOnceWhenTheWeightsAreRefusedBeforeABranchIsBuilt() throws Exception {
        givenEngineFusion("fusing_main:0.5,semantic_chunk:0.6");
        final EmbeddingSemanticSearcher semantic = new EmbeddingSemanticSearcher();
        try (RankFusionProcessor processor = newEngineFusionProcessor(new RefusingMainSearcher(), semantic)) {
            processor.search("q", fusionParams(0), OptionalThing.empty());
        }
        assertEquals(0, semantic.buildCount.get());
        assertEquals(1, semantic.searchCount.get());
        assertEquals(1, semantic.embedCount.get());
    }

    @Test
    public void test_engineFusion_embedsTheQueryOnceWhenTheCombinationTechniqueIsUnknown() throws Exception {
        givenEngineFusion("fusing_main:0.5,semantic_chunk:0.5");
        combinationTechnique = "median";
        assertFallsBackToFessWithOneEmbedding(new RefusingMainSearcher(), fusionParams(0));
    }

    @Test
    public void test_engineFusion_embedsTheQueryOnceWhenZScoreIsCombinedWithAMeanTheEngineRejects() throws Exception {
        givenEngineFusion("");
        combinationTechnique = "geometric_mean";
        normalizationTechnique = "z_score";
        assertFallsBackToFessWithOneEmbedding(new RefusingMainSearcher(), fusionParams(0));
    }

    @Test
    public void test_engineFusion_embedsTheQueryOnceWhenTheWeightsNameOnlySomeOfTheBranches() throws Exception {
        // sums to 1.0 and names this searcher, so it passes the check made before a branch is built,
        // and then does not match the two branches that take part
        givenEngineFusion("fusing_main:1.0");
        assertFallsBackToFessWithOneEmbedding(new RefusingMainSearcher(), fusionParams(0));
    }

    @Test
    public void test_engineFusion_embedsTheQueryOnceWhenTheEngineRejectsALaterPage() throws Exception {
        givenEngineFusion("fusing_main:0.5,semantic_chunk:0.5", 1000, 10000);
        final RefusingMainSearcher main = new RefusingMainSearcher();
        main.refusal = engineError("a page past the last fused hit");
        assertFallsBackToFessWithOneEmbedding(main, fusionParams(50));
    }

    @Test
    public void test_engineFusion_embedsTheQueryOnceWhenTheSearchEngineDoesNotKnowTheHybridQuery() throws Exception {
        givenEngineFusion("fusing_main:0.5,semantic_chunk:0.5");
        final RefusingMainSearcher main = new RefusingMainSearcher();
        main.refusal = engineError("unknown query [hybrid]");
        assertFallsBackToFessWithOneEmbedding(main, fusionParams(0));
    }

    @Test
    public void test_engineFusion_embedsTheQueryOnceWhenAPageAtTheDepthIsRetried() throws Exception {
        givenEngineFusion("fusing_main:0.5,semantic_chunk:0.5", 1000, 10000);
        final EmbeddingSemanticSearcher semantic = new EmbeddingSemanticSearcher();
        try (RankFusionProcessor processor = newEngineFusionProcessor(new RefusingMainSearcher(), semantic)) {
            // the page starts at the depth: refused once the branch is built, and SearchHelper
            // runs the search a second time with the query escaped
            assertThrows(InvalidQueryException.class, () -> processor.search("q", fusionParams(1000), OptionalThing.empty()));
            assertThrows(InvalidQueryException.class, () -> processor.search("q", fusionParams(1000), OptionalThing.empty()));
        }
        assertEquals(2, semantic.buildCount.get());
        assertEquals(1, semantic.embedCount.get());
    }

    @Test
    public void test_engineFusion_embedsTheQueryOfEachRequestOnce() throws Exception {
        givenEngineFusion("fusing_main:0.5,semantic_chunk:0.5");
        final EmbeddingSemanticSearcher semantic = new EmbeddingSemanticSearcher();
        try (RankFusionProcessor processor = newEngineFusionProcessor(new RefusingMainSearcher(), semantic)) {
            processor.search("q", fusionParams(0), OptionalThing.empty());
            final ExternalContext externalContext = SingletonLaContainerFactory.getExternalContext();
            final Object current = externalContext.getRequest();
            externalContext.setRequest(new MockletHttpServletRequestImpl(getMockRequest().getServletContext(), "/search"));
            try {
                processor.search("q", fusionParams(0), OptionalThing.empty());
            } finally {
                externalContext.setRequest(current);
            }
        }
        assertEquals(2, semantic.embedCount.get(), "the embedding of one request is not another request's");
    }

    @Test
    public void test_capacityRejection_ofTheMainSearcher_isRethrown() throws Exception {
        // The search engine is out of capacity, not the searcher broken: answering a degraded 200
        // would hide it from a caller that can retry.
        final RuntimeException rejection = capacityRejection();
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(searcherThrowing(rejection));
            processor.init();
            assertRejectionReachesTheCaller(processor, rejection, new TestSearchRequestParams(0, 10, 0));
        }
    }

    @Test
    public void test_capacityRejection_ofTheMainSearcher_isRethrownWithAnotherSearcher() throws Exception {
        final RuntimeException rejection = capacityRejection();
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(searcherThrowing(rejection));
            processor.register(new TestSubSearcher(0, 0, 0));
            processor.init();
            assertRejectionReachesTheCaller(processor, rejection, new TestSearchRequestParams(0, 10, 0));
        }
    }

    @Test
    public void test_capacityRejection_ofAnotherSearcher_isRethrown() throws Exception {
        final RuntimeException rejection = capacityRejection();
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestMainSearcher(10));
            processor.register(searcherThrowing(rejection));
            processor.init();
            assertRejectionReachesTheCaller(processor, rejection, new TestSearchRequestParams(0, 10, 0));
        }
    }

    @Test
    public void test_capacityRejection_ofTheFusedSearch_isRethrownNotRetriedByFess() throws Exception {
        // Falling back to fusing in Fess would send the search engine the same work again.
        givenEngineFusion("");
        final RuntimeException rejection = capacityRejection();
        final RefusingMainSearcher main = new RefusingMainSearcher();
        main.refusal = rejection;
        final CountingSubSearcher sub = new CountingSubSearcher();
        try (RankFusionProcessor processor = newEngineFusionProcessor(main, sub)) {
            assertRejectionReachesTheCaller(processor, rejection, fusionParams(0));
        }
        assertEquals(0, sub.searchCount.get(), "Fess must not have fused the search itself");
    }

    /** A search engine that refuses a search for lack of capacity, as the search engine client reports it. */
    private static RuntimeException capacityRejection() {
        return new org.codelibs.fess.exception.SearchEngineUnavailableException("The search engine is out of capacity.",
                new OpenSearchStatusException("OpenSearch exception [type=circuit_breaking_exception, reason=[parent] Data too large]",
                        RestStatus.TOO_MANY_REQUESTS, null));
    }

    private static RankFusionSearcher searcherThrowing(final RuntimeException failure) {
        return new RankFusionSearcher() {
            {
                name = "throwing";
            }

            @Override
            protected SearchResult search(final String query, final SearchRequestParams params,
                    final OptionalThing<FessUserBean> userBean) {
                throw failure;
            }
        };
    }

    private void assertRejectionReachesTheCaller(final RankFusionProcessor processor, final RuntimeException rejection,
            final SearchRequestParams params) {
        try {
            processor.search("q", params, OptionalThing.empty());
            fail("a capacity rejection must reach the caller instead of becoming a degraded answer");
        } catch (final RuntimeException e) {
            assertEquals(rejection, e);
        }
    }

    /** A bad request of the search engine, as the search engine client reports it. */
    private static InvalidQueryException engineError(final String reason) {
        final OpenSearchStatusException cause =
                new OpenSearchStatusException("OpenSearch exception [reason=" + reason + "]", RestStatus.BAD_REQUEST, null);
        return new InvalidQueryException(messages -> messages.addErrorsInvalidQueryCannotProcess(UserMessages.GLOBAL_PROPERTY_KEY),
                "Failed to process the query.", cause);
    }

    /** Runs a search that the search engine side cannot fuse and checks that Fess fused it with one embedding. */
    private void assertFallsBackToFessWithOneEmbedding(final RefusingMainSearcher main, final SearchRequestParams params) throws Exception {
        final EmbeddingSemanticSearcher semantic = new EmbeddingSemanticSearcher();
        try (RankFusionProcessor processor = newEngineFusionProcessor(main, semantic)) {
            processor.search("q", params, OptionalThing.empty());
        }
        assertEquals(0, main.fusedCount.get(), "the search must not have been fused by the engine");
        assertEquals(1, semantic.searchCount.get(), "the search must have been fused by Fess");
        assertEquals(1, semantic.embedCount.get(), "the query must be embedded once");
    }

    private static SearchRequestParams fusionParams(final int start) {
        return new TestSearchRequestParams(start, 10, 0) {
            @Override
            public Map<String, String[]> getConditions() {
                return Map.of();
            }
        };
    }

    private static SearchRequestParams sortedParams(final int start) {
        return new TestSearchRequestParams(start, 10, 0) {
            @Override
            public Map<String, String[]> getConditions() {
                return Map.of();
            }

            @Override
            public String getSort() {
                return "last_modified.desc";
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

    private String combinationTechnique = "rrf";

    private String normalizationTechnique = "min_max";

    private void givenEngineFusion(final String weights) {
        givenEngineFusion(weights, 10, 10000);
    }

    private void givenEngineFusion(final String weights, final int paginationDepth, final int maxResultWindow) {
        givenEngineFusion(weights, paginationDepth, maxResultWindow, true);
    }

    private void givenEngineFusion(final String weights, final int paginationDepth, final int maxResultWindow,
            final boolean engineEnabled) {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isRankFusionEngineEnabled() {
                return engineEnabled;
            }

            @Override
            public Integer getIndexerMaxResultWindowSizeAsInteger() {
                return Integer.valueOf(maxResultWindow);
            }

            @Override
            public String getRankFusionCombinationTechnique() {
                return combinationTechnique;
            }

            @Override
            public String getRankFusionNormalizationTechnique() {
                return normalizationTechnique;
            }

            @Override
            public String getRankFusionCombinationWeights() {
                return weights;
            }

            @Override
            public Integer getRankFusionPaginationDepthAsInteger() {
                return Integer.valueOf(paginationDepth);
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

        private final int allRecordCount;

        FusingMainSearcher() {
            this(100);
        }

        FusingMainSearcher(final int allRecordCount) {
            name = "fusing_main";
            this.allRecordCount = allRecordCount;
        }

        @Override
        protected SearchResult execute(final SearchRequestParams params, final SearchCondition<SearchRequestBuilder> condition) {
            return new TestMainSearcher(allRecordCount).search(null, params, OptionalThing.empty());
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
     * A main searcher that fuses without a search engine, and refuses the fused request - the first
     * request it is asked to run - with the given failure.
     */
    static class RefusingMainSearcher extends FusingMainSearcher {

        RuntimeException refusal;

        private final AtomicInteger executed = new AtomicInteger();

        @Override
        protected SearchResult execute(final SearchRequestParams params, final SearchCondition<SearchRequestBuilder> condition) {
            if (executed.getAndIncrement() == 0 && refusal != null) {
                throw refusal;
            }
            return super.execute(params, condition);
        }
    }

    /**
     * The built-in semantic branch over a stub embedding provider, counting the queries it embeds,
     * the branches it builds and the searches it runs on its own.
     */
    static class EmbeddingSemanticSearcher extends SemanticChunkSearcher {

        final AtomicInteger embedCount = new AtomicInteger();

        final AtomicInteger buildCount = new AtomicInteger();

        final AtomicInteger searchCount = new AtomicInteger();

        private final StubProcessorSplitter splitter = new StubProcessorSplitter();

        EmbeddingSemanticSearcher() {
            name = "semantic_chunk";
        }

        @Override
        protected boolean isSearchEnabled() {
            return true;
        }

        @Override
        protected boolean isKnnIndexReady() {
            return false;
        }

        @Override
        protected StructuredQuerySplitter getQuerySplitter() {
            return splitter;
        }

        @Override
        protected EmbeddingClientManager getEmbeddingClientManager() {
            return new EmbeddingClientManager() {
                @Override
                public boolean available() {
                    return true;
                }

                @Override
                public float[] embedQuery(final String query) {
                    embedCount.incrementAndGet();
                    return new float[] { 0.1f, 0.2f };
                }
            };
        }

        @Override
        protected Optional<QueryBuilder> buildSubQuery(final String query, final SearchRequestParams params,
                final OptionalThing<FessUserBean> userBean) {
            buildCount.incrementAndGet();
            return super.buildSubQuery(query, params, userBean);
        }

        @Override
        protected SearchResult search(final String query, final SearchRequestParams params, final OptionalThing<FessUserBean> userBean) {
            searchCount.incrementAndGet();
            return super.search(query, params, userBean);
        }

        @Override
        protected QueryBuilder buildSemanticQuery(final SemanticQueryContext context, final SearchRequestParams params) {
            return QueryBuilders.matchAllQuery();
        }

        @Override
        protected OptionalEntity<SearchResponse> sendRequest(final SearchRequestParams params,
                final SearchCondition<SearchRequestBuilder> condition) {
            return OptionalEntity.empty();
        }
    }

    /**
     * A branch that counts how often its query is built - for the semantic branch, each build
     * embeds the query - and how often it runs a search of its own.
     */
    static class CountingSubSearcher extends TestMainSearcher {

        final AtomicInteger buildCount = new AtomicInteger();

        final AtomicInteger searchCount = new AtomicInteger();

        SearchRequestParams builtWith;

        CountingSubSearcher() {
            super(100);
            name = "counting_sub";
        }

        @Override
        protected Optional<QueryBuilder> buildSubQuery(final String query, final SearchRequestParams params,
                final OptionalThing<FessUserBean> userBean) {
            buildCount.incrementAndGet();
            builtWith = params;
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
