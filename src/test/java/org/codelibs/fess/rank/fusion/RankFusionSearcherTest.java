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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.FacetResponse;
import org.opensearch.search.aggregations.Aggregations;
import org.opensearch.search.aggregations.InternalAggregations;
import org.dbflute.optional.OptionalThing;

public class RankFusionSearcherTest extends UnitFessTestCase {

    private SearchRequestParams createTestSearchRequestParams() {
        return new SearchRequestParams() {
            @Override
            public String getQuery() {
                return "test query";
            }

            @Override
            public String getSimilarDocHash() {
                return null;
            }

            @Override
            public SearchRequestType getType() {
                return SearchRequestType.SEARCH;
            }

            @Override
            public Locale getLocale() {
                return Locale.ENGLISH;
            }

            @Override
            public Object getAttribute(String name) {
                return null;
            }

            @Override
            public String[] getExtraQueries() {
                return new String[0];
            }

            @Override
            public int getOffset() {
                return 0;
            }

            @Override
            public int getPageSize() {
                return 10;
            }

            @Override
            public int getStartPosition() {
                return 0;
            }

            @Override
            public String getSort() {
                return null;
            }

            @Override
            public Map<String, String[]> getFields() {
                return new HashMap<>();
            }

            @Override
            public Map<String, String[]> getConditions() {
                return new HashMap<>();
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
                return null;
            }

            @Override
            public String getTrackTotalHits() {
                return null;
            }

            @Override
            public Float getMinScore() {
                return null;
            }

            @Override
            public String[] getResponseFields() {
                return new String[0];
            }
        };
    }

    private RankFusionSearcher rankFusionSearcher;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        // Create a concrete implementation for testing
        rankFusionSearcher = new TestRankFusionSearcher();
    }

    public void test_getName_defaultBehavior() {
        // Test that getName() properly converts class name to lowercase without "Searcher" suffix
        assertEquals("test_rank_fusion", rankFusionSearcher.getName());
    }

    public void test_getName_cachesBehavior() {
        // Test that getName() caches the result after first call
        String firstName = rankFusionSearcher.getName();
        String secondName = rankFusionSearcher.getName();
        assertSame(firstName, secondName);
        assertEquals("test_rank_fusion", firstName);
    }

    public void test_getName_customSearcherClass() {
        // Test with different searcher implementation
        RankFusionSearcher customSearcher = new CustomSearcher();
        assertEquals("custom", customSearcher.getName());
    }

    public void test_getName_simpleSearcherClass() {
        // Test with simple searcher name
        RankFusionSearcher simpleSearcher = new SimpleSearcher();
        assertEquals("simple", simpleSearcher.getName());
    }

    public void test_getName_complexCamelCaseSearcher() {
        // Test with complex camel case naming
        RankFusionSearcher complexSearcher = new VeryComplexRankFusionSearcher();
        assertEquals("very_complex_rank_fusion", complexSearcher.getName());
    }

    public void test_getName_searcherWithoutSuffixSearcher() {
        // Test with class that doesn't end with "Searcher"
        RankFusionSearcher noSuffixSearcher = new NoSuffixSearcherImpl();
        assertEquals("no_suffix_impl", noSuffixSearcher.getName());
    }

    public void test_search_withValidParameters() {
        // Test that search method is properly called with valid parameters
        TestRankFusionSearcher testSearcher = new TestRankFusionSearcher();
        String query = "test query";
        SearchRequestParams params = createTestSearchRequestParams();
        OptionalThing<FessUserBean> userBean = OptionalThing.empty();

        SearchResult result = testSearcher.search(query, params, userBean);

        assertNotNull(result);
        assertEquals(query, testSearcher.lastQuery);
        assertEquals(params, testSearcher.lastParams);
        assertEquals(userBean, testSearcher.lastUserBean);
    }

    public void test_search_withUserBean() {
        // Test search with user bean present
        TestRankFusionSearcher testSearcher = new TestRankFusionSearcher();
        String query = "user query";
        SearchRequestParams params = createTestSearchRequestParams();
        FessUserBean user = FessUserBean.empty();
        OptionalThing<FessUserBean> userBean = OptionalThing.of(user);

        SearchResult result = testSearcher.search(query, params, userBean);

        assertNotNull(result);
        assertEquals(query, testSearcher.lastQuery);
        assertEquals(params, testSearcher.lastParams);
        assertTrue(testSearcher.lastUserBean.isPresent());
        assertEquals(user, testSearcher.lastUserBean.get());
    }

    public void test_search_withNullQuery() {
        // Test search with null query
        TestRankFusionSearcher testSearcher = new TestRankFusionSearcher();
        SearchRequestParams params = createTestSearchRequestParams();
        OptionalThing<FessUserBean> userBean = OptionalThing.empty();

        SearchResult result = testSearcher.search(null, params, userBean);

        assertNotNull(result);
        assertNull(testSearcher.lastQuery);
        assertEquals(params, testSearcher.lastParams);
        assertEquals(userBean, testSearcher.lastUserBean);
    }

    public void test_search_withEmptyQuery() {
        // Test search with empty query
        TestRankFusionSearcher testSearcher = new TestRankFusionSearcher();
        String query = "";
        SearchRequestParams params = createTestSearchRequestParams();
        OptionalThing<FessUserBean> userBean = OptionalThing.empty();

        SearchResult result = testSearcher.search(query, params, userBean);

        assertNotNull(result);
        assertEquals("", testSearcher.lastQuery);
        assertEquals(params, testSearcher.lastParams);
        assertEquals(userBean, testSearcher.lastUserBean);
    }

    // Test implementation of RankFusionSearcher for testing purposes
    private static class TestRankFusionSearcher extends RankFusionSearcher {
        String lastQuery;
        SearchRequestParams lastParams;
        OptionalThing<FessUserBean> lastUserBean;

        @Override
        protected SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean) {
            this.lastQuery = query;
            this.lastParams = params;
            this.lastUserBean = userBean;

            // Return a dummy SearchResult
            List<Map<String, Object>> documentList = new ArrayList<>();
            Map<String, Object> doc = new HashMap<>();
            doc.put("title", "Test Document");
            documentList.add(doc);

            // Create empty aggregations instead of null
            Aggregations emptyAggregations = InternalAggregations.EMPTY;
            return new SearchResult(documentList, 1L, "eq", 100L, false, new FacetResponse(emptyAggregations));
        }
    }

    // Additional test searcher classes for getName() testing
    private static class CustomSearcher extends RankFusionSearcher {
        @Override
        protected SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean) {
            return new SearchResult(new ArrayList<>(), 0L, "eq", 0L, false, new FacetResponse(InternalAggregations.EMPTY));
        }
    }

    private static class SimpleSearcher extends RankFusionSearcher {
        @Override
        protected SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean) {
            return new SearchResult(new ArrayList<>(), 0L, "eq", 0L, false, new FacetResponse(InternalAggregations.EMPTY));
        }
    }

    private static class VeryComplexRankFusionSearcher extends RankFusionSearcher {
        @Override
        protected SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean) {
            return new SearchResult(new ArrayList<>(), 0L, "eq", 0L, false, new FacetResponse(InternalAggregations.EMPTY));
        }
    }

    private static class NoSuffixSearcherImpl extends RankFusionSearcher {
        @Override
        protected SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean) {
            return new SearchResult(new ArrayList<>(), 0L, "eq", 0L, false, new FacetResponse(InternalAggregations.EMPTY));
        }
    }
}