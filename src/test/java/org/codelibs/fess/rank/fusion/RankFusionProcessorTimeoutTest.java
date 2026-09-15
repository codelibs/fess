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
import java.util.Map;

import org.apache.lucene.search.TotalHits.Relation;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.rank.fusion.RankFusionProcessorErrorHandlingTest.SlowSearcher;
import org.codelibs.fess.rank.fusion.RankFusionProcessorErrorHandlingTest.TestSearchRequestParams;
import org.codelibs.fess.rank.fusion.RankFusionProcessorErrorHandlingTest.TestSearcher;
import org.codelibs.fess.rank.fusion.SearchResult.SearchResultBuilder;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.QueryResponseList;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;

/**
 * How long rank fusion waits for its searchers ({@code rank.fusion.timeout}).
 */
public class RankFusionProcessorTimeoutTest extends UnitFessTestCase {

    private static final String ID_FIELD = "_id";

    /**
     * A searcher that does not answer - an embedding provider that accepted the query and never
     * replied - must not hold the search: the main searcher's results come back once the timeout
     * elapses, flagged as partial and timed out.
     */
    @Test
    public void test_searcherThatDoesNotAnswer_isLeftOutAfterTimeout() throws Exception {
        givenTimeout(200);
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(100));
            final HangingSearcher hanging = new HangingSearcher(5000);
            processor.register(hanging);
            processor.init();

            final long start = System.currentTimeMillis();
            final List<Map<String, Object>> results = processor.search("*", new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());
            final long elapsed = System.currentTimeMillis() - start;

            assertTrue("search waited " + elapsed + "ms for a searcher that does not answer", elapsed < 3000);
            assertEquals(10, results.size());
            for (final Map<String, Object> doc : results) {
                assertFalse("unexpected document from the hanging searcher: " + doc, doc.get(ID_FIELD).toString().startsWith("hang_"));
            }
            final QueryResponseList response = (QueryResponseList) results;
            assertTrue("results without a searcher must be flagged partial", response.isPartialResults());
            assertTrue("a searcher left out for the timeout must be reported as a timeout", response.isTimedOut());
            assertFalse(response.isShardFailed());
        }
    }

    /**
     * A searcher that answers within the timeout is fused as before.
     */
    @Test
    public void test_searcherWithinTimeout_isFused() throws Exception {
        givenTimeout(5000);
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(100));
            processor.register(new SlowSearcher(50));
            processor.init();

            final List<Map<String, Object>> results = processor.search("*", new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());

            assertTrue("the searcher that answered in time is missing: " + results, containsIdPrefix(results, "slow_"));
            final QueryResponseList response = (QueryResponseList) results;
            assertFalse(response.isPartialResults());
            assertFalse(response.isTimedOut());
        }
    }

    /**
     * The timeout bounds the other searchers, not the main one: a slow main searcher is still
     * waited for, and the other searchers that finished meanwhile are fused with it.
     */
    @Test
    public void test_mainSearcherIsNotCutByTimeout() throws Exception {
        givenTimeout(100);
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new SlowSearcher(500));
            processor.register(new TestSearcher(100));
            processor.init();

            final List<Map<String, Object>> results = processor.search("*", new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());

            assertTrue("the main searcher's results are missing: " + results, containsIdPrefix(results, "slow_"));
            final QueryResponseList response = (QueryResponseList) results;
            assertFalse(response.isPartialResults());
            assertFalse(response.isTimedOut());
        }
    }

    /**
     * Zero or less waits for every searcher without a limit.
     */
    @Test
    public void test_nonPositiveTimeout_waitsForEverySearcher() throws Exception {
        givenTimeout(0);
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(100));
            processor.register(new SlowSearcher(300));
            processor.init();

            final List<Map<String, Object>> results = processor.search("*", new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());

            assertTrue("the slow searcher's results are missing: " + results, containsIdPrefix(results, "slow_"));
            final QueryResponseList response = (QueryResponseList) results;
            assertFalse(response.isPartialResults());
            assertFalse(response.isTimedOut());
        }
    }

    private static boolean containsIdPrefix(final List<Map<String, Object>> results, final String prefix) {
        return results.stream().anyMatch(doc -> doc.get(ID_FIELD).toString().startsWith(prefix));
    }

    private void givenTimeout(final int timeoutMillis) {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer getRankFusionTimeoutAsInteger() {
                return Integer.valueOf(timeoutMillis);
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
            public boolean isRankFusionEngineEnabled() {
                return false;
            }

            @Override
            public String getIndexFieldId() {
                return ID_FIELD;
            }
        });
    }

    /**
     * Searcher that blocks like a provider that accepted the request and never answered.
     */
    static class HangingSearcher extends RankFusionSearcher {
        private final long blockMs;

        HangingSearcher(final long blockMs) {
            this.blockMs = blockMs;
        }

        @Override
        protected SearchResult search(final String query, final SearchRequestParams params, final OptionalThing<FessUserBean> userBean) {
            try {
                Thread.sleep(blockMs);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            final SearchResultBuilder builder = SearchResult.create();
            for (int i = 0; i < 10; i++) {
                final Map<String, Object> doc = new HashMap<>();
                doc.put(ID_FIELD, "hang_" + i);
                builder.addDocument(doc);
            }
            builder.allRecordCount(10);
            builder.allRecordCountRelation(Relation.EQUAL_TO.toString());
            return builder.build();
        }
    }
}
