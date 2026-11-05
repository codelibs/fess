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
package org.codelibs.fess.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.query.QueryFieldConfig;
import org.codelibs.fess.query.StructuredQuerySplitter;
import org.codelibs.fess.query.StubProcessorSplitter;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.QueryResponseList;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.codelibs.fesen.opensearch.action.search.SearchAction;
import org.codelibs.fesen.opensearch.action.search.SearchRequestBuilder;
import org.codelibs.fesen.opensearch.action.search.SearchResponse;
import org.codelibs.fesen.opensearch.core.common.bytes.BytesArray;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;
import org.codelibs.fesen.opensearch.search.SearchHit;
import org.codelibs.fesen.opensearch.search.builder.SearchSourceBuilder;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Tests the similar-document search of {@link SearchHelper}: the source is looked up through the
 * role filter first, and the documents with the same content signature are an exact term inside the
 * role, virtual host and additional query restrictions.
 */
public class SearchHelperSimilarDocumentsTest extends UnitFessTestCase {

    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    private static final String HASH = "content_minhash_bits";

    private static final String SIGNATURE = "0110".repeat(32);

    private static final String[] FIELDS = { "title", "url", "digest" };

    private final List<SearchRequestBuilder> requests = new ArrayList<>();

    private final QueryHelper queryHelper = new QueryHelper() {
        @Override
        public void processSearchPreference(final SearchRequestBuilder searchRequestBuilder, final OptionalThing<FessUserBean> userBean,
                final String query) {
            // no preference in this test
        }
    };

    private StructuredQuerySplitter splitter;

    /** What the search engine answers; null for nothing. */
    private SearchResponse response;

    private final SearchHelper searchHelper = new SearchHelper() {
        @Override
        protected StructuredQuerySplitter getQuerySplitter() {
            return splitter != null ? splitter : super.getQuerySplitter();
        }
    };

    /** What the seams of {@link #scripted} answer. */
    private SearchHelper.SimilarSource source;

    private int sourceLookups;

    private final List<Integer> searchSizes = new ArrayList<>();

    private final List<QueryBuilder> searchQueries = new ArrayList<>();

    /** A helper whose two searches are scripted, to test what is asked without a search engine. */
    private final SearchHelper scripted = new SearchHelper() {
        @Override
        protected StructuredQuerySplitter getQuerySplitter() {
            return splitter != null ? splitter : super.getQuerySplitter();
        }

        @Override
        protected OptionalEntity<SimilarSource> findSimilarSource(final String docId, final OptionalThing<FessUserBean> userBean) {
            sourceLookups++;
            return source == null ? OptionalEntity.empty() : OptionalEntity.of(source);
        }

        @Override
        protected QueryResponseList searchSimilarDocuments(final QueryBuilder query, final String docId, final int size,
                final String[] responseFields, final OptionalThing<FessUserBean> userBean) {
            searchSizes.add(size);
            searchQueries.add(query);
            final List<Map<String, Object>> docs = new ArrayList<>();
            docs.add(new HashMap<>(Map.of("doc_id", "found")));
            return new QueryResponseList(docs, 1, "eq", 1L, false, false, false, null, 0, size, 0);
        }
    };

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.register(queryHelper, "queryHelper");
        ComponentUtil.register(new VirtualHostHelper(), "virtualHostHelper");
        ComponentUtil.register(new RoleQueryHelper() {
            @Override
            public Set<String> build(final SearchRequestType searchRequestType) {
                return Set.of("Rguest");
            }
        }, "roleQueryHelper");
        final QueryFieldConfig queryFieldConfig = new QueryFieldConfig();
        queryFieldConfig.setResponseFields(new String[] { "title", "url", "digest" });
        ComponentUtil.register(queryFieldConfig, "queryFieldConfig");
        ComponentUtil.register(new SearchEngineClient() {
            @Override
            public <T> T search(final String index, final SearchCondition<SearchRequestBuilder> condition,
                    final SearchResult<T, SearchRequestBuilder, SearchResponse> searchResult) {
                final SearchRequestBuilder builder = new SearchRequestBuilder(this, SearchAction.INSTANCE);
                // as the client does: a condition that refuses sends no request and answers nothing
                if (!condition.build(builder)) {
                    return searchResult.build(builder, 5, OptionalEntity.empty());
                }
                requests.add(builder);
                return searchResult.build(builder, 5, response == null ? OptionalEntity.empty() : OptionalEntity.of(response));
            }
        }, "searchEngineClient");
    }

    private JsonNode json(final Object queryBuilder) throws Exception {
        return MAPPER.readTree(queryBuilder.toString());
    }

    private JsonNode similarQuery(final String url) throws Exception {
        return json(searchHelper.buildSimilarDocumentsQuery("doc1", new SearchHelper.SimilarSource(SIGNATURE, url)));
    }

    // ---- the signature -----------------------------------------------------------------------

    @Test
    public void test_signatureUsable() {
        assertTrue(SearchHelper.isSignatureUsable(SIGNATURE));
        assertTrue(SearchHelper.isSignatureUsable("1".repeat(127) + "0"));
        // a document without a single word has every bit set, as every other such document
        assertFalse(SearchHelper.isSignatureUsable("1".repeat(128)));
        assertFalse(SearchHelper.isSignatureUsable("1"));
        assertFalse(SearchHelper.isSignatureUsable(null));
        assertFalse(SearchHelper.isSignatureUsable(""));
        assertFalse(SearchHelper.isSignatureUsable("  "));
    }

    // ---- the query ---------------------------------------------------------------------------

    @Test
    public void test_query_isAnExactTermOfTheSignatureInAFilter() throws Exception {
        final JsonNode bool = similarQuery("https://example.com/a").get("bool");
        assertFalse(bool.has("must"), bool.toString());
        assertFalse(bool.has("should"), bool.toString());
        boolean found = false;
        for (final JsonNode clause : bool.get("filter")) {
            if (clause.has("term") && clause.get("term").has(HASH)) {
                assertEquals(SIGNATURE, clause.get("term").get(HASH).get("value").asString());
                found = true;
            }
        }
        assertTrue(found, bool.toString());
        // an exact value, never a pattern or a prefix
        assertFalse(bool.toString().contains("regexp"), bool.toString());
        assertFalse(bool.toString().contains("prefix"), bool.toString());
    }

    @Test
    public void test_query_excludesTheSourceAndItsUrl() throws Exception {
        final JsonNode mustNot = similarQuery("https://example.com/a").get("bool").get("must_not");
        assertEquals(2, mustNot.size());
        final String text = mustNot.toString();
        assertTrue(text.contains("\"doc_id\":{\"value\":\"doc1\""), text);
        assertTrue(text.contains("\"url\":{\"value\":\"https://example.com/a\""), text);
    }

    @Test
    public void test_query_withoutTheSourceUrl_hasNoUrlExclusion() throws Exception {
        assertEquals(1, similarQuery(null).get("bool").get("must_not").size());
    }

    @Test
    public void test_query_roleFilterIsAFilter() throws Exception {
        final String filter = similarQuery("https://example.com/a").get("bool").get("filter").toString();
        assertTrue(filter.contains("\"terms\":{\"role\":[\"Rguest\"]"), filter);
    }

    @Test
    public void test_query_virtualHostIsAFilter() throws Exception {
        getMockRequest().setAttribute(FessConfig.VIRTUAL_HOST_VALUE, "vhostA");
        assertTrue(similarQuery("https://example.com/a").get("bool").get("filter").toString().contains("vhostA"));
    }

    @Test
    public void test_query_additionalQueryIsAFilter() throws Exception {
        queryHelper.setAdditionalQuery("-label:internal");
        splitter = new StubProcessorSplitter();
        assertTrue(similarQuery("https://example.com/a").get("bool").get("filter").toString().contains("converted"));
    }

    @Test
    public void test_query_additionalQueryThatIsNotConditionsGivesNoQuery() {
        queryHelper.setAdditionalQuery("secret");
        splitter = new StubProcessorSplitter();
        assertNull(searchHelper.buildSimilarDocumentsQuery("doc1", new SearchHelper.SimilarSource(SIGNATURE, null)));
    }

    // ---- the requests ------------------------------------------------------------------------

    @Test
    public void test_sourceLookup_isRoleFilteredAndReadsTheSignature() {
        final OptionalEntity<SearchHelper.SimilarSource> result = searchHelper.findSimilarSource("doc1", OptionalThing.empty());
        assertFalse(result.isPresent());
        assertEquals(1, requests.size());
        final SearchSourceBuilder source = requests.get(0).request().source();
        final String query = source.query().toString();
        assertTrue(query.contains("Rguest"), query);
        assertTrue(query.contains("doc1"), query);
        assertEquals(1, source.size());
        assertEquals(List.of("url"), List.of(source.fetchSource().includes()));
        assertEquals(List.of(HASH), source.storedFields().fieldNames());
    }

    @Test
    public void test_sourceLookup_appliesTheAdditionalQuery() {
        queryHelper.setAdditionalQuery("-label:internal");
        splitter = new StubProcessorSplitter();
        searchHelper.findSimilarSource("doc1", OptionalThing.empty());
        assertEquals(1, requests.size());
        final String query = requests.get(0).request().source().query().toString();
        assertTrue(query.contains("converted"), query);
        assertTrue(query.contains("Rguest"), query);
    }

    @Test
    public void test_sourceLookup_additionalQueryThatIsNotConditions_isNotFoundWithoutASearch() {
        queryHelper.setAdditionalQuery("secret");
        splitter = new StubProcessorSplitter();
        assertFalse(searchHelper.findSimilarSource("doc1", OptionalThing.empty()).isPresent());
        assertTrue(requests.isEmpty());
    }

    @Test
    public void test_search_isOnePageOfTheRequestedSizeInAStableOrderWithTheTotalCountedAsASearchDoes() throws Exception {
        final QueryResponseList result = searchHelper.searchSimilarDocuments(
                org.codelibs.fesen.opensearch.index.query.QueryBuilders.matchAllQuery(), "doc1", 7, FIELDS, OptionalThing.empty());
        assertEquals(0, result.size());
        final SearchSourceBuilder source = requests.get(0).request().source();
        assertEquals(7, source.size());
        assertTrue(source.from() <= 0, "no paging: " + source.from());
        // query.track.total.hits, as in a search
        assertEquals(10000, source.trackTotalHitsUpTo().intValue());
        assertEquals(List.of("title", "url", "digest"), List.of(source.fetchSource().includes()));
        // every hit has the same score, so the order is by URL, then document ID
        final JsonNode sort = MAPPER.readTree(source.toString()).get("sort");
        assertEquals(2, sort.size());
        assertEquals("asc", sort.get(0).get("url").get("order").asString());
        assertEquals("asc", sort.get(1).get("doc_id").get("order").asString());
    }

    // ---- the flow ----------------------------------------------------------------------------

    private List<String> similarIds(final int size) {
        final OptionalEntity<QueryResponseList> result = scripted.getSimilarDocuments("doc1", size, FIELDS, OptionalThing.empty());
        assertTrue(result.isPresent());
        final List<String> ids = new ArrayList<>();
        result.get().forEach(doc -> ids.add((String) doc.get("doc_id")));
        return ids;
    }

    @Test
    public void test_flow_searchesTheDocumentsWithTheSignatureOfTheSource() throws Exception {
        source = new SearchHelper.SimilarSource(SIGNATURE, "https://example.com/a");
        assertEquals(List.of("found"), similarIds(12));
        assertEquals(List.of(12), searchSizes);
        assertTrue(searchQueries.get(0).toString().contains(SIGNATURE));
    }

    @Test
    public void test_flow_hiddenOrMissingSource_isEmptyAndNothingElseIsSearched() {
        source = null;
        assertFalse(scripted.getSimilarDocuments("doc1", 20, FIELDS, OptionalThing.empty()).isPresent());
        assertEquals(1, sourceLookups);
        assertTrue(searchSizes.isEmpty());
    }

    @Test
    public void test_flow_sourceWithoutAUsableSignature_hasNoDocumentsAndSearchesNothing() {
        for (final String signature : new String[] { null, "", " ", "1".repeat(128) }) {
            source = new SearchHelper.SimilarSource(signature, null);
            org.junit.jupiter.api.Assertions.assertEquals(Collections.emptyList(), similarIds(20), "signature=" + signature);
        }
        assertTrue(searchSizes.isEmpty());
    }

    @Test
    public void test_flow_withoutThePlugin_thereAreNoDocuments() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isFesenPluginless() {
                return true;
            }
        });
        source = new SearchHelper.SimilarSource(SIGNATURE, null);
        assertEquals(Collections.emptyList(), similarIds(20));
        assertTrue(searchSizes.isEmpty());
    }

    @Test
    public void test_flow_additionalQueryThatIsNotConditions_hasNoDocumentsAndSearchesNothing() {
        queryHelper.setAdditionalQuery("secret");
        splitter = new StubProcessorSplitter();
        source = new SearchHelper.SimilarSource(SIGNATURE, null);
        assertEquals(Collections.emptyList(), similarIds(20));
        assertTrue(searchSizes.isEmpty());
    }

    @Test
    public void test_aHitIsConvertedAsASearchConvertsIt() {
        ComponentUtil.register(new ViewHelper() {
            @Override
            public String getContentTitle(final Map<String, Object> document) {
                return "title of " + document.get("title");
            }

            @Override
            public String getContentDescription(final Map<String, Object> document) {
                return "description of " + document.get("digest");
            }

            @Override
            public String getUrlLink(final Map<String, Object> document) {
                return "link of " + document.get("url");
            }

            @Override
            public String getSitePath(final Map<String, Object> document) {
                return "site";
            }
        }, "viewHelper");
        final SearchHit hit = new SearchHit(1, "es-id-2", null, null);
        hit.sourceRef(new BytesArray("{\"title\":\"A title\",\"url\":\"https://example.com/b\",\"digest\":\"d\","
                + "\"thumbnail\":\"t\",\"click_count\":3,\"favorite_count\":2,\"doc_id\":\"doc-2\"}"));
        // a sorted search has no score, as the search for similar documents
        hit.score(Float.NaN);
        final SearchResponse searchResponse = new SearchResponse(
                new org.codelibs.fesen.opensearch.search.internal.InternalSearchResponse(
                        new org.codelibs.fesen.opensearch.search.SearchHits(new SearchHit[] { hit },
                                new org.apache.lucene.search.TotalHits(1, org.apache.lucene.search.TotalHits.Relation.EQUAL_TO), 1f),
                        null, null, null, false, null, 1),
                null, 1, 1, 0, 5, org.codelibs.fesen.opensearch.action.search.ShardSearchFailure.EMPTY_ARRAY,
                SearchResponse.Clusters.EMPTY);
        response = searchResponse;

        // the document a search makes of it ...
        final List<Map<String, Object>> searched = new org.codelibs.fess.rank.fusion.DefaultSearcher() {
            List<Map<String, Object>> run() {
                return processResponse(OptionalEntity.of(searchResponse)).getDocumentList();
            }
        }.run();
        // ... and the one the search for similar documents makes
        final QueryResponseList similar = searchHelper.searchSimilarDocuments(
                org.codelibs.fesen.opensearch.index.query.QueryBuilders.matchAllQuery(), "doc1", 5, FIELDS, OptionalThing.empty());

        assertEquals(1, searched.size());
        assertEquals(1, similar.size());
        // the same fields, and the same values
        assertEquals("A title", searched.get(0).get("title"));
        assertEquals("description of d", searched.get(0).get(ComponentUtil.getFessConfig().getResponseFieldContentDescription()));
        assertEquals(searched.get(0).keySet(), similar.get(0).keySet());
        final Map<String, Object> expected = new HashMap<>(searched.get(0));
        final Map<String, Object> actual = new HashMap<>(similar.get(0));
        // arrays, and the name of the searcher that found the hit, which is not an API field
        for (final String key : new String[] { Constants.TEXT_FRAGMENTS, Constants.SEARCHER }) {
            expected.remove(key);
            actual.remove(key);
        }
        assertEquals(expected, actual);
        assertEquals(1L, similar.getAllRecordCount());
        assertEquals("EQUAL_TO", similar.getAllRecordCountRelation());
    }
}
