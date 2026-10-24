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

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Level;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.helper.ChunkVectorHelper;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.helper.RelatedQueryHelper;
import org.codelibs.fess.helper.RoleQueryHelper;
import org.codelibs.fess.helper.VirtualHostHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.query.StructuredQuerySplitter;
import org.codelibs.fess.query.StubProcessorSplitter;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.client.SearchEngineClient.SearchCondition;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.QueryStringBuilder;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.dbflute.utflute.mocklet.MockletHttpServletRequestImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.di.core.ExternalContext;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.codelibs.fesen.opensearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.codelibs.fesen.opensearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.codelibs.fesen.opensearch.action.search.SearchAction;
import org.codelibs.fesen.opensearch.action.search.SearchRequestBuilder;
import org.codelibs.fesen.opensearch.common.xcontent.XContentHelper;
import org.codelibs.fesen.opensearch.common.xcontent.json.JsonXContent;
import org.codelibs.fesen.opensearch.index.query.BoolQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;
import org.codelibs.fesen.opensearch.action.search.SearchResponse;
import org.codelibs.fesen.opensearch.cluster.metadata.MappingMetadata;
import org.codelibs.fesen.opensearch.common.settings.Settings;

public class SemanticChunkSearcherTest extends UnitFessTestCase {

    /**
     * Request attribute {@code RoleQueryHelper} short-circuits on ({@code RoleQueryHelper.USER_ROLES},
     * which is protected). Setting it makes the role set of a request deterministic in tests.
     */
    private static final String USER_ROLES_ATTRIBUTE = "userRoles";

    private SemanticChunkSearcher searcher;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        // resolveEngineMinScore/hasAnnChunkVectorMapping resolve it by class from the container,
        // which test_app.xml does not define
        ComponentUtil.register(new ChunkVectorHelper(), ChunkVectorHelper.class.getCanonicalName());
        searcher = new SemanticChunkSearcher();
    }

    @Test
    public void test_isPlainQuery_plain() {
        assertTrue(searcher.isPlainQuery("fess"));
        assertTrue(searcher.isPlainQuery("systemd 自動起動"));
        assertTrue(searcher.isPlainQuery("how to install fess on linux"));
        assertTrue(searcher.isPlainQuery("Fessをサービスとして自動起動する方法"));
    }

    @Test
    public void test_isPlainQuery_syntax() {
        assertFalse(searcher.isPlainQuery("title:fess"));
        assertFalse(searcher.isPlainQuery("\"exact phrase\""));
        assertFalse(searcher.isPlainQuery("(fess OR opensearch)"));
        assertFalse(searcher.isPlainQuery("fess AND linux"));
        assertFalse(searcher.isPlainQuery("fess OR linux"));
        assertFalse(searcher.isPlainQuery("NOT fess"));
        assertFalse(searcher.isPlainQuery("+fess -windows"));
        assertFalse(searcher.isPlainQuery("fess -windows"));
        assertFalse(searcher.isPlainQuery("fes*"));
        assertFalse(searcher.isPlainQuery("fess~2"));
        assertFalse(searcher.isPlainQuery("timestamp:[now-1d TO now]"));
        assertFalse(searcher.isPlainQuery("content_length:{0 TO 100}"));
        assertFalse(searcher.isPlainQuery("a\\:b"));
        assertFalse(searcher.isPlainQuery("foo && bar"));
        assertFalse(searcher.isPlainQuery("foo || bar"));
    }

    @Test
    public void test_isPlainQuery_hyphenInsideWord() {
        // an in-word hyphen is not an exclusion operator
        assertTrue(searcher.isPlainQuery("fess-crawler"));
        assertTrue(searcher.isPlainQuery("real-time search"));
        assertTrue(searcher.isPlainQuery("10-20"));
        assertTrue(searcher.isPlainQuery("trailing -"));
    }

    @Test
    public void test_buildExactChunkQuery() {
        final String json = searcher.buildExactChunkQuery(new float[] { 0.1f, 0.2f }).toString().replaceAll("\\s", "");
        assertTrue(json.contains("\"nested\""));
        assertTrue(json.contains("\"path\":\"content_chunk_vector\""));
        assertTrue(json.contains("script_score"));
        assertTrue(json.contains("cosineSimilarity"));
        assertTrue(json.contains("content_chunk_vector.vector"));
        assertTrue(json.contains("\"score_mode\":\"max\""));
        // an unmapped content_chunk_vector (chunk job never ran) must yield no hits, not a query error
        assertTrue(json.contains("\"ignore_unmapped\":true"));
    }

    @Test
    public void test_emptyResult() {
        final SearchResult result = searcher.emptyResult();
        assertEquals(0, result.getAllRecordCount());
        assertTrue(result.getDocumentList().isEmpty());
    }

    @Test
    public void test_buildChunkVectorQuery_annShape() {
        final String json = searcher.buildKnnChunkQuery(new float[] { 0.1f, 0.2f }, new StubSearchRequestParams(0, 10))
                .toString()
                .replaceAll("\\s", "");
        assertTrue(json.contains("\"nested\""));
        assertTrue(json.contains("\"path\":\"content_chunk_vector\""));
        assertTrue(json.contains("\"knn\""));
        assertTrue(json.contains("\"content_chunk_vector.vector\""));
        assertTrue(json.contains("\"k\":100"));
        assertTrue(json.contains("\"ignore_unmapped\":true"));
        assertFalse(json.contains("script_score"), "ann mode must not use script_score: " + json);
    }

    @Test
    public void test_buildKnnChunkQuery_kGrowsWithWindow() {
        // a deep page must not request fewer neighbors than the window it has to fill
        final String json =
                searcher.buildKnnChunkQuery(new float[] { 0.1f }, new StubSearchRequestParams(190, 20)).toString().replaceAll("\\s", "");
        assertTrue(json.contains("\"k\":210"), "k must cover startPosition + pageSize: " + json);
    }

    @Test
    public void test_buildKnnChunkQuery_engineFusedBranchUsesTheConfiguredK() {
        // the branch of a search fused in the engine is built with no window of its own, whatever
        // page is requested, so k stays content_chunker.search.knn.k per shard
        final String json = searcher
                .buildKnnChunkQuery(new float[] { 0.1f },
                        new RankFusionProcessor.SearchRequestParamsWrapper(new StubSearchRequestParams(990, 10), 0, 0))
                .toString()
                .replaceAll("\\s", "");
        assertTrue(json.contains("\"k\":100"), json);
    }

    @Test
    public void test_resolveEngineMinScore() {
        org.codelibs.fess.util.ComponentUtil.register(new org.codelibs.fess.helper.ChunkVectorHelper(),
                org.codelibs.fess.helper.ChunkVectorHelper.class.getCanonicalName());
        // exact mode: script emits cosine + 1.0
        assertTrue(Math.abs(searcher.resolveEngineMinScore(0.4f, false).get() - 1.4f) < 0.0001f);
        // ann mode with default lucene + cosinesimil: engine emits (1 + cosine) / 2
        assertTrue(Math.abs(searcher.resolveEngineMinScore(0.4f, true).get() - 0.7f) < 0.0001f);
    }

    @Test
    public void test_resolveEngineMinScore_faissCosinesimilScoresLikeLucene() {
        // The k-NN plugin translates the cosinesimil distance d = 1 - cos to (2 - d) / 2 =
        // (1 + cos) / 2 for every engine, so a faiss index needs the same cutoff as a lucene one.
        // (Measured on OpenSearch 3.9.0: cos 0.244 scores 0.622 on faiss, not 1 / (2 - 0.244).)
        org.codelibs.fess.util.ComponentUtil.register(new org.codelibs.fess.helper.ChunkVectorHelper() {
            @Override
            public String getKnnEngine() {
                return "faiss";
            }
        }, org.codelibs.fess.helper.ChunkVectorHelper.class.getCanonicalName());
        assertTrue(Math.abs(searcher.resolveEngineMinScore(0.4f, true).get() - 0.7f) < 0.0001f);
        assertTrue(Math.abs(searcher.resolveEngineMinScore(0.35f, true).get() - 0.675f) < 0.0001f);
        assertTrue(Math.abs(searcher.resolveEngineMinScore(0.0f, true).get() - 0.5f) < 0.0001f);
        assertTrue(Math.abs(searcher.resolveEngineMinScore(1.0f, true).get() - 1.0f) < 0.0001f);
    }

    @Test
    public void test_resolveEngineMinScore_faissInnerproductIsSkipped() {
        org.codelibs.fess.util.ComponentUtil.register(new org.codelibs.fess.helper.ChunkVectorHelper() {
            @Override
            public String getKnnEngine() {
                return "faiss";
            }

            @Override
            public String getKnnSpaceType() {
                return "innerproduct";
            }
        }, org.codelibs.fess.helper.ChunkVectorHelper.class.getCanonicalName());
        assertFalse(searcher.resolveEngineMinScore(0.4f, true).isPresent());
    }

    @Test
    public void test_search_skipsWhenSimilarDocHashPresent() {
        final GuardedSearcher guarded = new GuardedSearcher();
        final StubSearchRequestParams params = new StubSearchRequestParams(0, 10) {
            @Override
            public String getSimilarDocHash() {
                return "somehash";
            }
        };
        final SearchResult result = guarded.search("plain query", params, org.dbflute.optional.OptionalThing.empty());
        assertEquals(0, result.getAllRecordCount());
        assertFalse(guarded.managerTouched, "the semantic branch must be skipped before consulting the embedding provider");
    }

    @Test
    public void test_search_skipsWhenGeoInfoPresent() {
        final org.dbflute.utflute.mocklet.MockletHttpServletRequest request = getMockRequest();
        request.setParameter("geo.location.point", "34,150");
        request.setParameter("geo.location.distance", "10km");
        final org.codelibs.fess.entity.GeoInfo geoInfo = new org.codelibs.fess.entity.GeoInfo(request);
        final GuardedSearcher guarded = new GuardedSearcher();
        final StubSearchRequestParams params = new StubSearchRequestParams(0, 10) {
            @Override
            public org.codelibs.fess.entity.GeoInfo getGeoInfo() {
                return geoInfo;
            }
        };
        final SearchResult result = guarded.search("plain query", params, org.dbflute.optional.OptionalThing.empty());
        assertEquals(0, result.getAllRecordCount());
        assertFalse(guarded.managerTouched, "a geo-constrained request must skip the semantic branch");
    }

    @Test
    public void test_search_skipsWhenSyntaxQuery() {
        final GuardedSearcher guarded = new GuardedSearcher();
        final SearchResult result =
                guarded.search("title:fess", new StubSearchRequestParams(0, 10), org.dbflute.optional.OptionalThing.empty());
        assertEquals(0, result.getAllRecordCount());
        assertFalse(guarded.managerTouched);
    }

    @Test
    public void test_search_fallsBackWhenEmbeddingUnavailable() {
        final GuardedSearcher guarded = new GuardedSearcher();
        guarded.available = false;
        final LogCapturingAppender appender = LogCapturingAppender.attach(SemanticChunkSearcher.class);
        try {
            for (int i = 0; i < 2; i++) {
                final SearchResult result =
                        guarded.search("plain query", new StubSearchRequestParams(0, 10), org.dbflute.optional.OptionalThing.empty());
                assertEquals(0, result.getAllRecordCount());
            }
            // No latch: every request that finds the provider unavailable reports it.
            final List<String> warnings =
                    appender.messagesAt(Level.WARN).stream().filter(m -> m.contains("Embedding provider is unavailable")).toList();
            assertEquals(2, warnings.size(), appender.messagesAt(Level.WARN).toString());
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_resolveEngineMinScore_warnsOncePerSpaceType() {
        registerSpaceType("l2");
        final LogCapturingAppender appender = LogCapturingAppender.attach(SemanticChunkSearcher.class);
        try {
            // resolved on every ann-mode search: the same skipped cutoff is reported once, not per query
            for (int i = 0; i < 3; i++) {
                assertFalse(searcher.resolveEngineMinScore(0.4f, true).isPresent());
            }
            List<String> warnings =
                    appender.messagesAt(Level.WARN).stream().filter(m -> m.contains("cannot be applied to space_type=")).toList();
            assertEquals(1, warnings.size(), appender.messagesAt(Level.WARN).toString());
            assertTrue(warnings.get(0).contains("space_type=l2"), warnings.get(0));

            // a different space type is a new condition and is reported; one already reported is not
            registerSpaceType("innerproduct");
            assertFalse(searcher.resolveEngineMinScore(0.4f, true).isPresent());
            assertFalse(searcher.resolveEngineMinScore(0.4f, true).isPresent());
            registerSpaceType("l2");
            assertFalse(searcher.resolveEngineMinScore(0.4f, true).isPresent());
            warnings = appender.messagesAt(Level.WARN).stream().filter(m -> m.contains("cannot be applied to space_type=")).toList();
            assertEquals(2, warnings.size(), appender.messagesAt(Level.WARN).toString());
            assertTrue(warnings.get(1).contains("space_type=innerproduct"), warnings.get(1));
        } finally {
            appender.detach();
            org.codelibs.fess.util.ComponentUtil.register(new org.codelibs.fess.helper.ChunkVectorHelper(),
                    org.codelibs.fess.helper.ChunkVectorHelper.class.getCanonicalName());
        }
    }

    private static void registerSpaceType(final String spaceType) {
        org.codelibs.fess.util.ComponentUtil.register(new org.codelibs.fess.helper.ChunkVectorHelper() {
            @Override
            public String getKnnSpaceType() {
                return spaceType;
            }
        }, org.codelibs.fess.helper.ChunkVectorHelper.class.getCanonicalName());
    }

    @Test
    public void test_search_degradesToEmptyWhenEngineCallFails() {
        final GuardedSearcher guarded = new GuardedSearcher() {
            @Override
            protected OptionalEntity<SearchResponse> sendRequest(final SearchRequestParams params,
                    final SearchCondition<SearchRequestBuilder> condition) {
                throw new RuntimeException("engine boom");
            }
        };
        guarded.available = true;
        final SearchResult result =
                guarded.search("plain query", new StubSearchRequestParams(0, 10), org.dbflute.optional.OptionalThing.empty());
        // an engine failure must degrade to an empty semantic result, not propagate
        assertEquals(0, result.getAllRecordCount());
    }

    @Test
    public void test_register_gatedByEnabledFlag() {
        final CapturingRankFusionProcessor processor = new CapturingRankFusionProcessor();
        org.codelibs.fess.util.ComponentUtil.register(processor, "rankFusionProcessor");
        final GuardedSearcher disabled = new GuardedSearcher();
        disabled.enabled = false;
        disabled.register();
        assertFalse(processor.registered, "a disabled searcher must not register with the rank fusion processor");
        final GuardedSearcher enabled = new GuardedSearcher();
        enabled.register();
        assertTrue(processor.registered);
    }

    // -------------------------------------------------------------------------------------
    //                                                      createSemanticSearchCondition
    //                                                              ----------------------

    @Test
    public void test_createSemanticSearchCondition_annCarriesPermissionFilterIntoKnn() {
        givenPermissionContext();
        final String json = buildQueryJson(new GuardedSearcher(), true, new StubSearchRequestParams(0, 10));
        final int knnIndex = json.indexOf("\"knn\"");
        assertTrue(knnIndex >= 0, "ann mode must emit a knn query: " + json);
        final String knnPart = json.substring(knnIndex);
        // Without a filter inside the knn query the ANN top-k is collected globally and the
        // sibling role/virtual-host clauses only post-filter it, so a narrowly-permissioned
        // user gets a near-empty page. The knn body itself has to carry the constraint.
        assertTrue(knnPart.contains("\"filter\""), "the knn query must carry the permission filter: " + json);
        final String filterPart = knnPart.substring(knnPart.indexOf("\"filter\""));
        assertTrue(filterPart.contains("\"terms\":{\"role\":[\"Rguest\"]"), "the knn filter must carry the role terms: " + json);
        assertTrue(filterPart.contains("\"virtual_host\":{\"value\":\"vhost1\""),
                "the knn filter must carry the virtual host term: " + json);
        // ... and the outer query must keep enforcing it (the knn filter is a recall aid, not
        // the security boundary), so the role term is serialized twice.
        assertEquals(2, countOccurrences(json, "\"terms\":{\"role\":[\"Rguest\"]"), json);
        assertEquals(2, countOccurrences(json, "\"virtual_host\":{\"value\":\"vhost1\""), json);
    }

    @Test
    public void test_createSemanticSearchCondition_exactKeepsPermissionOnOuterQueryOnly() {
        givenPermissionContext();
        final String json = buildQueryJson(new GuardedSearcher(), false, new StubSearchRequestParams(0, 10));
        assertTrue(json.contains("script_score"), json);
        assertFalse(json.contains("\"knn\""), json);
        // exact mode has no in-query top-k truncation, so a single outer permission clause is enough
        assertEquals(1, countOccurrences(json, "\"terms\":{\"role\":[\"Rguest\"]"), json);
        assertEquals(1, countOccurrences(json, "\"virtual_host\":{\"value\":\"vhost1\""), json);
    }

    @Test
    public void test_createSemanticSearchCondition_doesNotTrackTotalHits() {
        givenPermissionContext();
        final SearchRequestBuilder builder = buildRequest(new GuardedSearcher(), false, new StubSearchRequestParams(0, 10));
        // RankFusionProcessor derives allRecordCount from the main searcher only, so the
        // semantic total is computed and thrown away.
        assertTrue(builder.request().source().trackTotalHitsUpTo() == null,
                "track_total_hits must not be requested: " + builder.request().source());
        assertFalse(builder.request().source().toString().replaceAll("\\s", "").contains("track_total_hits"),
                builder.request().source().toString());
    }

    // -------------------------------------------------------------------------------------
    //                                                                     conditions in a query
    //                                                                     ---------------------

    @Test
    public void test_prepare_narrowedQueryStillReachesTheVectorBranch() {
        final GuardedSearcher searcher = new GuardedSearcher();
        // Clicking a facet appends label:"news" to the query. Refusing the whole string for
        // carrying syntax is what used to make the vector branch disappear the moment a user
        // narrowed a search.
        final OptionalThing<SemanticChunkSearcher.SemanticQueryContext> context =
                searcher.prepare("opensearch label:\"news\"", new StubSearchRequestParams(0, 10));
        assertTrue(context.isPresent(), "a narrowed search must still reach the vector branch");
        assertNotNull(context.get().getConditionFilter(), "the narrowing must survive as a filter");
        assertEquals(1, searcher.splitter.converted.size(), searcher.splitter.converted.toString());
    }

    @Test
    public void test_prepare_splitsTheQueryAsTheRunningSearchRequestType() {
        // The conditions are built with the type of the search that is running, as the keyword
        // branch of that search is, so the two branches select the same documents for a tag
        // condition: an admin search is not limited to the visible tags, the others are.
        for (final SearchRequestParams.SearchRequestType type : SearchRequestParams.SearchRequestType.values()) {
            final GuardedSearcher searcher = new GuardedSearcher();
            final StubSearchRequestParams params = new StubSearchRequestParams(0, 10) {
                @Override
                public SearchRequestParams.SearchRequestType getType() {
                    return type;
                }
            };
            assertTrue(searcher.prepare("opensearch label:\"news\"", params).isPresent());
            assertEquals(List.of(type), searcher.splitter.convertedTypes);
        }
    }

    @Test
    public void test_prepare_embedsTheWordsWithoutTheConditions() {
        final GuardedSearcher searcher = new GuardedSearcher();
        searcher.prepare("opensearch label:\"news\"", new StubSearchRequestParams(0, 10));
        // label:"news" is noise to an embedding model
        assertEquals("opensearch", searcher.embeddedQuery);
    }

    @Test
    public void test_prepare_embedsTheUserQueryWithoutRelatedQueries() {
        // A related query widens keyword recall. Expanded into the assembled query it becomes
        // (password OR "password reset"), which the splitter rejects - the vector branch used to
        // disappear for the very term the administrator had tuned.
        ComponentUtil.register(new QueryStringBuilder(), "queryStringBuilder");
        ComponentUtil.register(new RelatedQueryHelper() {
            @Override
            public String[] getRelatedQueries(final String query) {
                return "password".equals(query) ? new String[] { "password reset" } : new String[0];
            }
        }, "relatedQueryHelper");
        final StubSearchRequestParams params = new StubSearchRequestParams(0, 10) {
            @Override
            public String getQuery() {
                return "password";
            }

            @Override
            public java.util.Map<String, String[]> getFields() {
                return java.util.Collections.singletonMap("label", new String[] { "news" });
            }
        };
        final String assembled = ComponentUtil.getQueryStringBuilder().params(params).build();
        assertEquals("(password OR \"password reset\") label:\"news\"", assembled);

        final GuardedSearcher searcher = new GuardedSearcher();
        final OptionalThing<SemanticChunkSearcher.SemanticQueryContext> context = searcher.prepare(assembled, params);
        assertTrue(context.isPresent(), "a related query must not take the vector branch away");
        assertEquals("password", searcher.embeddedQuery);
        assertNotNull(context.get().getConditionFilter(), "the label must still narrow the semantic branch");
        assertEquals(1, searcher.splitter.converted.size(), searcher.splitter.converted.toString());
        assertTrue(searcher.splitter.converted.get(0).contains("news"), searcher.splitter.converted.toString());
    }

    @Test
    public void test_prepare_aSimilarDocHashIsAHardFilterOnlyWhereTheEngineHasTheSignature() {
        final StubSearchRequestParams withHash = new StubSearchRequestParams(0, 10) {
            @Override
            public String getSimilarDocHash() {
                return "0101";
            }
        };
        for (final String type : new String[] { "default", "opensearch" }) {
            useSearchEngineType(type);
            // the semantic branch cannot apply the filter, so it steps aside rather than fuse unfiltered hits
            assertFalse(type, new GuardedSearcher().prepare("opensearch", withHash).isPresent());
            assertTrue(type, new GuardedSearcher().prepare("opensearch", new StubSearchRequestParams(0, 10)).isPresent());
        }
        for (final String type : new String[] { "vanilla", "aws", "cloud" }) {
            useSearchEngineType(type);
            // the plugin-less search drops the parameter, so there is no filter to honour
            assertTrue(type, new GuardedSearcher().prepare("opensearch", withHash).isPresent());
        }
    }

    private static void useSearchEngineType(final String type) {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getSearchEngineType() {
                return type;
            }
        });
    }

    @Test
    public void test_prepare_skipsWhatCannotBeSplit() {
        final GuardedSearcher searcher = new GuardedSearcher();
        // allintitle: narrows to one field, and a chunk vector covers the document rather than a
        // field, so the branch cannot honour it and must not run without it
        assertFalse(searcher.prepare("allintitle:opensearch", new StubSearchRequestParams(0, 10)).isPresent());
    }

    // -------------------------------------------------------------------------------------
    //                                                              one embedding per request
    //                                                              -------------------------

    @Test
    public void test_prepare_embedsTheSameWordsOnceForTheRequest() {
        final GuardedSearcher searcher = new GuardedSearcher();
        final OptionalThing<SemanticChunkSearcher.SemanticQueryContext> first =
                searcher.prepare("opensearch", new StubSearchRequestParams(0, 10));
        // a second branch of the same request - the Fess-side fusion that follows a refused fused
        // request, or the retry of a rejected query - asks for the same words again
        final OptionalThing<SemanticChunkSearcher.SemanticQueryContext> second =
                searcher.prepare("opensearch", new StubSearchRequestParams(0, 10));
        assertEquals(1, searcher.embedCount, "the words of a request must be embedded once");
        assertTrue(first.isPresent() && second.isPresent());
        assertEquals(first.get().getQueryVector()[0], second.get().getQueryVector()[0]);
    }

    @Test
    public void test_prepare_neverReusesTheEmbeddingOfOtherWords() {
        final GuardedSearcher searcher = new GuardedSearcher();
        final float[] first = searcher.prepare("opensearch", new StubSearchRequestParams(0, 10)).get().getQueryVector();
        final float[] other = searcher.prepare("fess", new StubSearchRequestParams(0, 10)).get().getQueryVector();
        assertEquals(2, searcher.embedCount);
        assertEquals("fess", searcher.embeddedQuery);
        assertTrue(first[0] == "opensearch".length());
        assertTrue(other[0] == "fess".length(), "the vector of other words must be computed for them");
        // the words of the request are now the other ones
        assertTrue(searcher.prepare("opensearch", new StubSearchRequestParams(0, 10)).get().getQueryVector()[0] == "opensearch".length());
    }

    @Test
    public void test_prepare_doesNotShareTheEmbeddingBetweenRequests() {
        final GuardedSearcher searcher = new GuardedSearcher();
        searcher.prepare("opensearch", new StubSearchRequestParams(0, 10));
        assertEquals(1, searcher.embedCount);

        // another request - another user - asking for the same words
        final HttpServletRequest other = new MockletHttpServletRequestImpl(getMockRequest().getServletContext(), "/search");
        final ExternalContext externalContext = SingletonLaContainerFactory.getExternalContext();
        final Object current = externalContext.getRequest();
        externalContext.setRequest(other);
        try {
            searcher.prepare("opensearch", new StubSearchRequestParams(0, 10));
        } finally {
            externalContext.setRequest(current);
        }
        assertEquals(2, searcher.embedCount, "an embedding must never outlive the request it was computed for");
    }

    @Test
    public void test_prepare_withoutARequestEmbedsEveryTime() {
        final GuardedSearcher searcher = new GuardedSearcher();
        final ExternalContext externalContext = SingletonLaContainerFactory.getExternalContext();
        final Object current = externalContext.getRequest();
        externalContext.setRequest(null);
        try {
            assertTrue(searcher.prepare("opensearch", new StubSearchRequestParams(0, 10)).isPresent());
            assertTrue(searcher.prepare("opensearch", new StubSearchRequestParams(0, 10)).isPresent());
        } finally {
            externalContext.setRequest(current);
        }
        // nothing ties the embedding to a request, so nothing is kept
        assertEquals(2, searcher.embedCount);
    }

    @Test
    public void test_prepare_aFailedEmbeddingIsNotRemembered() {
        final GuardedSearcher searcher = new GuardedSearcher();
        searcher.embedFailure = new RuntimeException("provider down");
        assertFalse(searcher.prepare("opensearch", new StubSearchRequestParams(0, 10)).isPresent());
        searcher.embedFailure = null;
        assertTrue(searcher.prepare("opensearch", new StubSearchRequestParams(0, 10)).isPresent(),
                "a provider that answered once it recovered must be asked again");
        assertEquals(2, searcher.embedCount);
    }

    @Test
    public void test_prepare_escapedRetryOfAPlainQueryReusesTheEmbedding() {
        ComponentUtil.register(new QueryStringBuilder(), "queryStringBuilder");
        final StubSearchRequestParams params = new StubSearchRequestParams(0, 10) {
            @Override
            public String getQuery() {
                return "opensearch cluster";
            }
        };
        final String query = ComponentUtil.getQueryStringBuilder().params(params).build();
        // what SearchHelper runs again when the first attempt is rejected as an invalid query
        final String escaped = ComponentUtil.getQueryStringBuilder().params(params).escape(true).build();

        final GuardedSearcher searcher = new GuardedSearcher();
        assertTrue(searcher.prepare(query, params).isPresent());
        assertTrue(searcher.prepare(escaped, params).isPresent());
        assertEquals("opensearch cluster", searcher.embeddedQuery);
        assertEquals(1, searcher.embedCount, "escaping a query without reserved characters leaves the words alone");
    }

    /**
     * A question is typed with its question mark. The query syntax reads that as a wildcard, which the
     * splitter cannot embed, so the vector branch used to be skipped for exactly the questions it is for.
     */
    @Test
    public void test_prepare_aQuestionWithItsQuestionMarkRunsTheVectorBranch() {
        ComponentUtil.register(new QueryStringBuilder(), "queryStringBuilder");
        ComponentUtil.register(new RelatedQueryHelper() {
            @Override
            public String[] getRelatedQueries(final String query) {
                return new String[0];
            }
        }, "relatedQueryHelper");
        final StubSearchRequestParams params = new StubSearchRequestParams(0, 10) {
            @Override
            public String getQuery() {
                return "I forgot my password?";
            }
        };
        final String query = ComponentUtil.getQueryStringBuilder().params(params).build();

        final GuardedSearcher searcher = new GuardedSearcher();
        assertTrue(searcher.prepare(query, params).isPresent(), query);
        assertEquals("I forgot my password", searcher.embeddedQuery);
    }

    /**
     * What reads as a wildcard, prefix or fuzzy term is still words the user typed, so the vector
     * branch embeds them as typed. A question in a language written without spaces cannot even be
     * told from a wildcard: the whole sentence is one word, and its closing question mark is the
     * only syntax in it.
     */
    @Test
    public void test_prepare_aWildcardIsEmbeddedAsTyped() {
        ComponentUtil.register(new QueryStringBuilder(), "queryStringBuilder");
        ComponentUtil.register(new RelatedQueryHelper() {
            @Override
            public String[] getRelatedQueries(final String query) {
                return new String[0];
            }
        }, "relatedQueryHelper");
        final String[][] cases = { { "password?", "password?" }, { "pas?word", "pas?word" }, { "pass*", "pass*" },
                { "reset pass*", "reset pass*" }, { "reset pas?word", "reset pas?word" }, { "reset~ password", "reset~ password" },
                { "パスワードを忘れた場合はどうすればいいですか?", "パスワードを忘れた場合はどうすればいいですか?" }, { "密码忘记了怎么办?", "密码忘记了怎么办?" } };
        for (final String[] typedAndEmbedded : cases) {
            final StubSearchRequestParams params = new StubSearchRequestParams(0, 10) {
                @Override
                public String getQuery() {
                    return typedAndEmbedded[0];
                }
            };
            final String query = ComponentUtil.getQueryStringBuilder().params(params).build();

            final GuardedSearcher searcher = new GuardedSearcher();
            assertTrue(searcher.prepare(query, params).isPresent(), query);
            assertEquals(typedAndEmbedded[1], searcher.embeddedQuery);
        }
    }

    /**
     * Core reads a clause on a field it does not know as words of the default field, so a question
     * that starts with a label such as "Q:" is a question: its first word is "Q:how", not a
     * condition on a field called Q that no document has.
     */
    @Test
    public void test_prepare_anUnknownFieldIsWordsToEmbedNotACondition() {
        ComponentUtil.register(new QueryStringBuilder(), "queryStringBuilder");
        final StubSearchRequestParams params = new StubSearchRequestParams(0, 10) {
            @Override
            public String getQuery() {
                return "Q: how do I reset my password";
            }
        };
        final String query = ComponentUtil.getQueryStringBuilder().params(params).build();

        final GuardedSearcher searcher = new GuardedSearcher();
        final OptionalThing<SemanticChunkSearcher.SemanticQueryContext> context = searcher.prepare(query, params);
        assertTrue(context.isPresent(), query);
        assertEquals("Q:how do I reset my password", searcher.embeddedQuery);
        assertNull(context.get().getConditionFilter(), "no document has a field called Q to filter on");
    }

    /** What the keyword branch is asked to keep, the vector branch keeps: a wildcard beside a facet is still words beside a filter. */
    @Test
    public void test_prepare_aWildcardBesideAFacetKeepsTheFacetAsAFilter() {
        ComponentUtil.register(new QueryStringBuilder(), "queryStringBuilder");
        final StubSearchRequestParams params = new StubSearchRequestParams(0, 10) {
            @Override
            public String getQuery() {
                return "forgot my pass*";
            }

            @Override
            public java.util.Map<String, String[]> getFields() {
                return java.util.Collections.singletonMap("label", new String[] { "news" });
            }
        };
        final String query = ComponentUtil.getQueryStringBuilder().params(params).build();

        final GuardedSearcher searcher = new GuardedSearcher();
        final OptionalThing<SemanticChunkSearcher.SemanticQueryContext> context = searcher.prepare(query, params);
        assertTrue(context.isPresent(), query);
        assertEquals("forgot my pass*", searcher.embeddedQuery);
        assertNotNull(context.get().getConditionFilter());
    }

    /**
     * SearchHelper runs a query the engine rejected again with its reserved characters escaped,
     * and both branches of that second run see the escaped words: the same words the keyword
     * branch looks for, the conditions typed into the query among them, since escaped they are
     * words. The first run is not embedded when the query cannot be parsed.
     */
    @Test
    public void test_prepare_theEscapedRetryEmbedsTheWordsTheKeywordBranchSearchesFor() {
        ComponentUtil.register(new QueryStringBuilder(), "queryStringBuilder");
        final String[][] cases = { { "really?!", "really?!" }, { "site:example.com reset (password", "site:example.com reset (password" },
                { "and/or [2026]", "and/or [2026]" }, { "C++ (Docker)", "C++ (Docker)" } };
        for (final String[] typedAndEmbedded : cases) {
            final StubSearchRequestParams params = new StubSearchRequestParams(0, 10) {
                @Override
                public String getQuery() {
                    return typedAndEmbedded[0];
                }
            };
            final String escaped = ComponentUtil.getQueryStringBuilder().params(params).escape(true).build();

            final GuardedSearcher searcher = new GuardedSearcher();
            final OptionalThing<SemanticChunkSearcher.SemanticQueryContext> context = searcher.prepare(escaped, params);
            assertTrue(context.isPresent(), escaped);
            assertEquals(typedAndEmbedded[1], searcher.embeddedQuery);
            assertNull(context.get().getConditionFilter(), escaped);
        }
        // the query itself does not parse, so the vector branch has nothing to embed on the first run
        final StubSearchRequestParams unbalanced = new StubSearchRequestParams(0, 10) {
            @Override
            public String getQuery() {
                return "site:example.com reset (password";
            }
        };
        final GuardedSearcher searcher = new GuardedSearcher();
        // the registered builder is one shared instance, which remembers the escape(true) above
        final String raw = ComponentUtil.getQueryStringBuilder().params(unbalanced).escape(false).build();
        assertFalse(searcher.prepare(raw, unbalanced).isPresent(), raw + " -> " + searcher.embeddedQuery);
        assertFalse(searcher.managerTouched);
    }

    /** What the vector branch cannot keep still skips it: it would search for something the user excluded or required. */
    @Test
    public void test_prepare_restrictiveSyntaxStillSkipsTheVectorBranch() {
        ComponentUtil.register(new QueryStringBuilder(), "queryStringBuilder");
        for (final String typed : new String[] { "reset -password", "\"reset password\"", "reset NOT password", "cat^2 dog", "[a TO c]",
                "allintitle:reset password", "reset password sort:filename.asc" }) {
            final StubSearchRequestParams params = new StubSearchRequestParams(0, 10) {
                @Override
                public String getQuery() {
                    return typed;
                }
            };
            final GuardedSearcher searcher = new GuardedSearcher();
            assertFalse(searcher.prepare(ComponentUtil.getQueryStringBuilder().params(params).build(), params).isPresent(), typed);
            assertFalse(searcher.managerTouched, typed);
        }
    }

    /**
     * A stray double quote fails the keyword branch on both runs unless the retry escapes it, and
     * the vector branch embeds whatever words the retry leaves: the quote is one of them.
     */
    @Test
    public void test_prepare_aStrayQuoteIsEmbeddedAfterTheEscapedRetry() {
        ComponentUtil.register(new QueryStringBuilder(), "queryStringBuilder");
        final StubSearchRequestParams params = new StubSearchRequestParams(0, 10) {
            @Override
            public String getQuery() {
                return "how to cut a 5\" pipe";
            }
        };
        final String raw = new QueryStringBuilder().params(params).build();
        final String escaped = new QueryStringBuilder().params(params).escape(true).build();

        final GuardedSearcher searcher = new GuardedSearcher();
        assertFalse(searcher.prepare(raw, params).isPresent(), "the first run cannot be parsed: " + raw);
        assertTrue(searcher.prepare(escaped, params).isPresent(), escaped);
        assertEquals("how to cut a 5\" pipe", searcher.embeddedQuery);
    }

    /** A facet value with a quote or a backslash narrows the vector branch by exactly that value. */
    @Test
    public void test_prepare_aFacetValueWithAQuoteIsOneCondition() {
        ComponentUtil.register(new QueryStringBuilder(), "queryStringBuilder");
        final StubSearchRequestParams params = new StubSearchRequestParams(0, 10) {
            @Override
            public String getQuery() {
                return "opensearch cluster";
            }

            @Override
            public java.util.Map<String, String[]> getFields() {
                return java.util.Collections.singletonMap("label", new String[] { "a\"b", "c\\d" });
            }
        };
        final String query = new QueryStringBuilder().params(params).build();

        final GuardedSearcher searcher = new GuardedSearcher();
        final OptionalThing<SemanticChunkSearcher.SemanticQueryContext> context = searcher.prepare(query, params);
        assertTrue(context.isPresent(), query);
        assertEquals("opensearch cluster", searcher.embeddedQuery);
        assertNotNull(context.get().getConditionFilter());
        assertEquals(1, searcher.splitter.converted.size(), searcher.splitter.converted.toString());
        assertTrue(searcher.splitter.converted.get(0).contains("a\"b") && searcher.splitter.converted.get(0).contains("c\\d"),
                searcher.splitter.converted.toString());
    }

    @Test
    public void test_prepare_escapedRetryThatChangesTheWordsEmbedsAgain() {
        ComponentUtil.register(new QueryStringBuilder(), "queryStringBuilder");
        final StubSearchRequestParams params = new StubSearchRequestParams(0, 10) {
            @Override
            public String getQuery() {
                return "opensearch label:news";
            }
        };
        final String query = ComponentUtil.getQueryStringBuilder().params(params).build();
        final String escaped = ComponentUtil.getQueryStringBuilder().params(params).escape(true).build();

        final GuardedSearcher searcher = new GuardedSearcher();
        assertTrue(searcher.prepare(query, params).isPresent());
        assertEquals("opensearch", searcher.embeddedQuery);
        // escaped, the condition the user typed is words to search for: not the embedding of the first attempt
        assertTrue(searcher.prepare(escaped, params).isPresent());
        assertEquals(2, searcher.embedCount);
        assertFalse("opensearch".equals(searcher.embeddedQuery), searcher.embeddedQuery);
    }

    @Test
    public void test_buildSemanticQuery_appliesConditionsToBothPlaces() {
        givenPermissionContext();
        final GuardedSearcher searcher = new GuardedSearcher();
        final QueryBuilder conditionFilter = QueryBuilders.termQuery("label", "news");
        final SemanticChunkSearcher.SemanticQueryContext context =
                new SemanticChunkSearcher.SemanticQueryContext(new float[] { 0.1f, 0.2f }, true, conditionFilter);
        final String json = searcher.buildSemanticQuery(context, new StubSearchRequestParams(0, 10)).toString().replaceAll("\\s", "");
        // the outer clause enforces the narrowing; the copy inside the knn query keeps the
        // approximate search from spending its k on documents that are about to be discarded
        assertEquals(2, countOccurrences(json, "\"label\":{\"value\":\"news\""), json);
    }

    @Test
    public void test_buildSemanticQuery_leavesTheFilterAloneWithoutConditions() {
        givenPermissionContext();
        final GuardedSearcher searcher = new GuardedSearcher();
        final SemanticChunkSearcher.SemanticQueryContext context =
                new SemanticChunkSearcher.SemanticQueryContext(new float[] { 0.1f, 0.2f }, true, null);
        final String json = searcher.buildSemanticQuery(context, new StubSearchRequestParams(0, 10)).toString().replaceAll("\\s", "");
        assertFalse(json.contains("\"label\""), json);
        assertEquals(2, countOccurrences(json, "\"terms\":{\"role\":[\"Rguest\"]"), json);
    }

    @Test
    public void test_buildSemanticQuery_appliesRequestFilterQueriesToBothPlaces() {
        givenPermissionContext();
        final GuardedSearcher searcher = new GuardedSearcher();
        final StubSearchRequestParams params = new StubSearchRequestParams(0, 10) {
            @Override
            public java.util.List<QueryBuilder> getFilterQueries() {
                return java.util.List.of(QueryBuilders.termsQuery("label", "public"));
            }
        };
        final SemanticChunkSearcher.SemanticQueryContext annContext =
                new SemanticChunkSearcher.SemanticQueryContext(new float[] { 0.1f, 0.2f }, true, null);
        final String annJson = searcher.buildSemanticQuery(annContext, params).toString().replaceAll("\\s", "");
        // a hard bool filter on the outer query, plus the recall copy inside the knn query
        assertEquals(2, countOccurrences(annJson, "\"terms\":{\"label\":[\"public\"]"), annJson);
        assertEquals(2, countOccurrences(annJson, "\"terms\":{\"role\":[\"Rguest\"]"), annJson);

        final SemanticChunkSearcher.SemanticQueryContext exactContext =
                new SemanticChunkSearcher.SemanticQueryContext(new float[] { 0.1f, 0.2f }, false, null);
        final String exactJson = searcher.buildSemanticQuery(exactContext, params).toString().replaceAll("\\s", "");
        assertEquals(1, countOccurrences(exactJson, "\"terms\":{\"label\":[\"public\"]"), exactJson);
    }

    @Test
    public void test_buildSemanticQuery_noFilterQueriesByDefault() {
        givenPermissionContext();
        final GuardedSearcher searcher = new GuardedSearcher();
        final SemanticChunkSearcher.SemanticQueryContext context =
                new SemanticChunkSearcher.SemanticQueryContext(new float[] { 0.1f, 0.2f }, true, null);
        final String json = searcher.buildSemanticQuery(context, new StubSearchRequestParams(0, 10)).toString().replaceAll("\\s", "");
        // the role filter is a terms query as well, so look for the request's own filter field
        assertFalse(json.contains("\"terms\":{\"label\""), json);
    }

    @Test
    public void test_semanticSearchRequestParams_delegatesFilterQueries() {
        final java.util.List<QueryBuilder> filters = java.util.List.of(QueryBuilders.termsQuery("label", "public"));
        final StubSearchRequestParams params = new StubSearchRequestParams(0, 10) {
            @Override
            public java.util.List<QueryBuilder> getFilterQueries() {
                return filters;
            }
        };
        assertEquals(filters, new SemanticChunkSearcher.SemanticSearchRequestParams(params).getFilterQueries());
        assertEquals(filters, new RankFusionProcessor.SearchRequestParamsWrapper(params, 0, 5).getFilterQueries());
    }

    @Test
    public void test_semanticSearchRequestParams_delegatesResponseFields() {
        // a request that adds a field to its response (the v2 search adds tag) must get it on hits of the vector leg too
        final String[] fields = { "title", "url", "tag" };
        final StubSearchRequestParams params = new StubSearchRequestParams(0, 10) {
            @Override
            public String[] getResponseFields() {
                return fields;
            }
        };
        org.junit.jupiter.api.Assertions.assertArrayEquals(fields,
                new SemanticChunkSearcher.SemanticSearchRequestParams(params).getResponseFields());
    }

    // -------------------------------------------------------------------------------------
    //                                                                               min_score
    //                                                                               ---------

    @Test
    public void test_applyMinScore_leavesTheQueryAloneWhenUnset() {
        final GuardedSearcher searcher = new GuardedSearcher();
        final QueryBuilder chunkQuery = QueryBuilders.matchAllQuery();
        assertSame(chunkQuery, searcher.applyMinScore(chunkQuery, false));
    }

    @Test
    public void test_applyMinScore_boundsTheQueryRatherThanTheRequest() {
        final GuardedSearcher searcher = new GuardedSearcher();
        searcher.minScore = Float.valueOf(0.4f);
        // The cutoff has to travel with the branch it belongs to: a request that fuses several
        // searchers has one min_score for the combined result, applied after normalization.
        final String json = searcher.applyMinScore(QueryBuilders.matchAllQuery(), false).toString().replaceAll("\\s", "");
        assertTrue(json.contains("\"function_score\""), json);
        // exact mode scores cosine + 1
        assertTrue(json.contains("\"min_score\":1.4"), json);
    }

    @Test
    public void test_applyMinScore_usesTheEngineScale() {
        final GuardedSearcher searcher = new GuardedSearcher();
        searcher.minScore = Float.valueOf(0.4f);
        // ann mode on lucene/cosinesimil scores (1 + cos) / 2
        final String json = searcher.applyMinScore(QueryBuilders.matchAllQuery(), true).toString().replaceAll("\\s", "");
        assertTrue(json.contains("\"min_score\":0.7"), json);
    }

    @Test
    public void test_createSemanticSearchCondition_carriesTheCutoffInTheQuery() {
        givenPermissionContext();
        final GuardedSearcher searcher = new GuardedSearcher();
        searcher.minScore = Float.valueOf(0.4f);
        final SearchRequestBuilder builder = buildRequest(searcher, false, new StubSearchRequestParams(0, 10));
        assertNull(builder.request().source().minScore(), "the cutoff no longer rides on the request");
        assertTrue(builder.request().source().query().toString().replaceAll("\\s", "").contains("\"min_score\":1.4"),
                builder.request().source().query().toString());
    }

    @Test
    public void test_buildSubQuery_takesPartEvenWithACutoffConfigured() {
        givenPermissionContext();
        final GuardedSearcher searcher = new GuardedSearcher();
        searcher.minScore = Float.valueOf(0.4f);
        final Optional<QueryBuilder> subQuery =
                searcher.buildSubQuery("plain query", new StubSearchRequestParams(0, 10), OptionalThing.empty());
        assertTrue(subQuery.isPresent(), "a configured cutoff must not keep the branch out of a fused request");
        assertTrue(subQuery.get().toString().replaceAll("\\s", "").contains("\"min_score\":1.4"), subQuery.get().toString());
    }

    // -------------------------------------------------------------------------------------
    //                                                                            permission
    //                                                                            ----------
    //
    // The role, denied-role, virtual-host and request-filter clauses are what keep a user from
    // reading documents they may not. Both vector modes, both entry points, with and without a
    // condition: the outer bool is what enforces them, the copy inside the knn query is what keeps
    // the approximate search from spending its k on documents about to be discarded. A test that
    // only greps the JSON for the role terms passes when the clause is in the wrong place (an
    // optional should), so these read the clause's position.

    private static final QueryBuilder REQUEST_FILTER = QueryBuilders.termQuery("tenant", "t1");

    /** The permission clause {@code buildPermissionQuery} must produce for {@code Rguest} on {@code vhost1}. */
    private BoolQueryBuilder expectedPermission(final boolean withRequestFilter, final boolean withRole) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final BoolQueryBuilder permission = QueryBuilders.boolQuery();
        if (withRequestFilter) {
            permission.filter(REQUEST_FILTER);
        }
        if (withRole) {
            permission.filter(QueryBuilders.boolQuery()
                    .should(QueryBuilders.termsQuery(fessConfig.getIndexFieldRole(), Set.of("Rguest")))
                    .mustNot(QueryBuilders.termsQuery(fessConfig.getIndexFieldRole(),
                            List.of(fessConfig.getRoleSearchDeniedPrefix() + "Rguest"))));
            permission.filter(QueryBuilders.termQuery(fessConfig.getIndexFieldVirtualHost(), "vhost1"));
        }
        return permission;
    }

    private StubSearchRequestParams paramsWithRequestFilter(final SearchRequestParams.SearchRequestType type) {
        return new StubSearchRequestParams(0, 10) {
            @Override
            public List<QueryBuilder> getFilterQueries() {
                return List.of(REQUEST_FILTER);
            }

            @Override
            public SearchRequestParams.SearchRequestType getType() {
                return type;
            }
        };
    }

    private static void same(final Object expected, final Object actual, final String message) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual, message);
    }

    private static void same(final Object expected, final Object actual) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
    }

    private static Map<String, Object> toMap(final QueryBuilder query) {
        return XContentHelper.convertToMap(JsonXContent.jsonXContent,
                new java.io.ByteArrayInputStream(query.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8)), false);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(final Object value) {
        return (Map<String, Object>) value;
    }

    /** The body of the knn query of a semantic query: {@code bool.must[0] -> (function_score ->) nested -> knn}. */
    private static Map<String, Object> knnBodyOf(final QueryBuilder semanticQuery) {
        final Map<String, Object> bool = asMap(toMap(semanticQuery).get("bool"));
        Map<String, Object> chunk = asMap(((List<?>) bool.get("must")).get(0));
        if (chunk.containsKey("function_score")) {
            chunk = asMap(asMap(chunk.get("function_score")).get("query"));
        }
        final Map<String, Object> knn = asMap(asMap(asMap(chunk.get("nested")).get("query")).get("knn"));
        // keyed by the vector field
        return asMap(knn.values().iterator().next());
    }

    /** Every semantic query the two entry points build for the same request, labelled for the failure message. */
    private Map<String, QueryBuilder> semanticQueries(final boolean ann, final SearchRequestParams params) {
        final Map<String, QueryBuilder> queries = new java.util.LinkedHashMap<>();
        final GuardedSearcher searcher = new GuardedSearcher();
        searcher.ann = ann;
        final Optional<QueryBuilder> subQuery = searcher.buildSubQuery("plain query", params, OptionalThing.empty());
        assertTrue(subQuery.isPresent());
        queries.put("buildSubQuery", subQuery.get());
        // the search() path hands the request through the facet-stripping wrapper
        final SemanticChunkSearcher.SemanticQueryContext context =
                new SemanticChunkSearcher.SemanticQueryContext(new float[] { 0.1f, 0.2f }, ann);
        final SearchRequestBuilder builder = new SearchRequestBuilder(new SearchEngineClient(), SearchAction.INSTANCE);
        assertTrue(searcher.createSemanticSearchCondition(context, "plain query",
                new SemanticChunkSearcher.SemanticSearchRequestParams(params), OptionalThing.empty()).build(builder));
        queries.put("createSemanticSearchCondition", builder.request().source().query());
        return queries;
    }

    @Test
    public void test_permission_outerFilterEnforcesItInEveryModeAndEntryPoint() {
        givenPermissionContext();
        for (final boolean ann : new boolean[] { true, false }) {
            for (final Map.Entry<String, QueryBuilder> entry : semanticQueries(ann,
                    paramsWithRequestFilter(SearchRequestParams.SearchRequestType.SEARCH)).entrySet()) {
                final String label = (ann ? "ann/" : "exact/") + entry.getKey();
                final BoolQueryBuilder outer = (BoolQueryBuilder) entry.getValue();
                // enforcement is a filter of the outermost bool: request filter, role, virtual host
                same(List.of(expectedPermission(true, true)), outer.filter(), label);
                // and nothing else of the permission leaks into scoring or into an optional clause
                assertTrue(outer.should().isEmpty(), label);
                assertTrue(outer.mustNot().isEmpty(), label);
                assertEquals(1, outer.must().size(), label);
            }
        }
    }

    /**
     * The role clause is its own bool, under filter, whose only positive clause is a should. A bool
     * with a should and no must or filter requires one of them (minimum_should_match defaults to
     * 1); the same should next to a filter would be optional. It must never be put on the permission
     * bool itself, which carries the virtual host and request filters as filters.
     */
    @Test
    public void test_permission_roleClauseIsRequiredNotOptional() {
        givenPermissionContext();
        final BoolQueryBuilder permission =
                new GuardedSearcher().buildPermissionQuery(paramsWithRequestFilter(SearchRequestParams.SearchRequestType.SEARCH));
        assertTrue(permission.should().isEmpty(), "a should beside filters is optional: " + permission);
        assertTrue(permission.mustNot().isEmpty(), permission.toString());
        final BoolQueryBuilder role =
                (BoolQueryBuilder) permission.filter().stream().filter(f -> f instanceof BoolQueryBuilder).findFirst().get();
        assertEquals(1, role.should().size());
        assertEquals(1, role.mustNot().size());
        assertTrue(role.filter().isEmpty(), "a filter in the role bool makes its should optional: " + role);
        assertTrue(role.must().isEmpty(), "a must in the role bool makes its should optional: " + role);
        assertNull(role.minimumShouldMatch(), role.toString());
    }

    @Test
    public void test_permission_knnFilterCarriesTheSameClauseAsTheOuterFilter() {
        givenPermissionContext();
        for (final Map.Entry<String, QueryBuilder> entry : semanticQueries(true,
                paramsWithRequestFilter(SearchRequestParams.SearchRequestType.SEARCH)).entrySet()) {
            final Map<String, Object> knn = knnBodyOf(entry.getValue());
            same(toMap(expectedPermission(true, true)), knn.get("filter"), entry.getKey());
            final BoolQueryBuilder outer = (BoolQueryBuilder) entry.getValue();
            same(toMap(outer.filter().get(0)), knn.get("filter"), entry.getKey() + ": the recall copy must be the enforced clause");
        }
    }

    @Test
    public void test_permission_exactModeHasNoKnnToCarryAFilter() {
        givenPermissionContext();
        for (final Map.Entry<String, QueryBuilder> entry : semanticQueries(false, new StubSearchRequestParams(0, 10)).entrySet()) {
            assertFalse(entry.getValue().toString().contains("\"knn\""), entry.getKey());
            // so the outer filter is the only enforcement, and it is there
            same(List.of(expectedPermission(false, true)), ((BoolQueryBuilder) entry.getValue()).filter(), entry.getKey());
        }
    }

    @Test
    public void test_permission_conditionsAreEnforcedOutsideAndFilteredInsideBesideThePermission() {
        givenPermissionContext();
        final GuardedSearcher searcher = new GuardedSearcher();
        final QueryBuilder condition = QueryBuilders.termQuery("label", "news");
        final StubSearchRequestParams params = paramsWithRequestFilter(SearchRequestParams.SearchRequestType.SEARCH);
        for (final boolean ann : new boolean[] { true, false }) {
            final SemanticChunkSearcher.SemanticQueryContext context =
                    new SemanticChunkSearcher.SemanticQueryContext(new float[] { 0.1f, 0.2f }, ann, condition);
            final BoolQueryBuilder outer = (BoolQueryBuilder) searcher.buildSemanticQuery(context, params);
            same(List.of(expectedPermission(true, true), condition), outer.filter(), "ann=" + ann);
            if (ann) {
                same(toMap(QueryBuilders.boolQuery().filter(expectedPermission(true, true)).filter(condition)),
                        knnBodyOf(outer).get("filter"));
            }
        }
    }

    @Test
    public void test_permission_theMinScoreCutoffDoesNotMoveTheFilter() {
        givenPermissionContext();
        final GuardedSearcher searcher = new GuardedSearcher();
        searcher.ann = true;
        searcher.minScore = Float.valueOf(0.4f);
        final QueryBuilder query = searcher
                .buildSubQuery("plain query", paramsWithRequestFilter(SearchRequestParams.SearchRequestType.SEARCH), OptionalThing.empty())
                .get();
        same(List.of(expectedPermission(true, true)), ((BoolQueryBuilder) query).filter());
        same(toMap(expectedPermission(true, true)), knnBodyOf(query).get("filter"));
    }

    /** An admin search skips the role and the virtual host, never the request's own filters. */
    @Test
    public void test_permission_adminSearchKeepsTheRequestFilters() {
        givenPermissionContext();
        final StubSearchRequestParams params = paramsWithRequestFilter(SearchRequestParams.SearchRequestType.ADMIN_SEARCH);
        for (final Map.Entry<String, QueryBuilder> entry : semanticQueries(true, params).entrySet()) {
            same(List.of(expectedPermission(true, false)), ((BoolQueryBuilder) entry.getValue()).filter(), entry.getKey());
            same(toMap(expectedPermission(true, false)), knnBodyOf(entry.getValue()).get("filter"), entry.getKey());
        }
    }

    /** Several roles stay one terms clause, and every denied role is spelled with the denied prefix. */
    @Test
    public void test_permission_severalRolesStayOneTermsClausePerSide() {
        givenPermissionContext();
        getMockRequest().setAttribute(USER_ROLES_ATTRIBUTE, Set.of("Ralice", "Rbob"));
        final String prefix = ComponentUtil.getFessConfig().getRoleSearchDeniedPrefix();
        final BoolQueryBuilder permission = new GuardedSearcher().buildPermissionQuery(new StubSearchRequestParams(0, 10));
        final BoolQueryBuilder role = (BoolQueryBuilder) permission.filter().get(0);
        final String json = role.toString().replaceAll("\\s", "");
        assertEquals(1, role.should().size(), json);
        assertTrue(json.contains("\"Ralice\"") && json.contains("\"Rbob\""), json);
        assertEquals(1, role.mustNot().size(), json);
        assertTrue(json.contains("\"" + prefix + "Ralice\"") && json.contains("\"" + prefix + "Rbob\""), json);
    }

    /** What the vector branch sends for the same request must not depend on what the keyword branch is told to search. */
    @Test
    public void test_permission_theVectorBranchIgnoresTheQueryTextForPermission() {
        givenPermissionContext();
        final GuardedSearcher searcher = new GuardedSearcher();
        searcher.ann = true;
        final StubSearchRequestParams params = paramsWithRequestFilter(SearchRequestParams.SearchRequestType.SEARCH);
        final Object first = toMap(searcher.buildSubQuery("plain query", params, OptionalThing.empty()).get()).get("bool");
        final Object second = toMap(searcher.buildSubQuery("another query", params, OptionalThing.empty()).get()).get("bool");
        same(asMap(first).get("filter"), asMap(second).get("filter"));
    }

    /** Every request type but the admin search is bound by the role clause, in both places. */
    @Test
    public void test_permission_everyRequestTypeButAdminSearchIsBoundByTheRole() {
        givenPermissionContext();
        for (final SearchRequestParams.SearchRequestType type : SearchRequestParams.SearchRequestType.values()) {
            final boolean admin = type == SearchRequestParams.SearchRequestType.ADMIN_SEARCH;
            final StubSearchRequestParams params = paramsWithRequestFilter(type);
            for (final Map.Entry<String, QueryBuilder> entry : semanticQueries(true, params).entrySet()) {
                final String label = type + "/" + entry.getKey();
                final BoolQueryBuilder expected = expectedPermission(true, !admin);
                same(List.of(expected), ((BoolQueryBuilder) entry.getValue()).filter(), label);
                same(toMap(expected), knnBodyOf(entry.getValue()).get("filter"), label);
            }
        }
    }

    /** With no virtual host active the role still binds, and no virtual host term is invented. */
    @Test
    public void test_permission_withoutAVirtualHostTheRoleStillBinds() {
        givenPermissionContext();
        getMockRequest().setAttribute(FessConfig.VIRTUAL_HOST_VALUE, "");
        for (final Map.Entry<String, QueryBuilder> entry : semanticQueries(true, new StubSearchRequestParams(0, 10)).entrySet()) {
            final BoolQueryBuilder permission = (BoolQueryBuilder) ((BoolQueryBuilder) entry.getValue()).filter().get(0);
            assertEquals(1, permission.filter().size(), entry.getKey() + ": " + permission);
            assertFalse(permission.toString().contains(ComponentUtil.getFessConfig().getIndexFieldVirtualHost()), permission.toString());
            assertTrue(permission.toString().contains("\"Rguest\""), permission.toString());
            same(toMap(permission), knnBodyOf(entry.getValue()).get("filter"), entry.getKey());
        }
    }

    /**
     * An admin search with nothing else to bind it has no permission clause at all: the outer bool
     * has no filter, and the knn query is sent with no filter rather than with an empty one. With a
     * condition, the knn filter is that condition alone.
     */
    @Test
    public void test_permission_adminSearchWithNothingToBindIt() {
        givenPermissionContext();
        final StubSearchRequestParams admin = new StubSearchRequestParams(0, 10) {
            @Override
            public SearchRequestType getType() {
                return SearchRequestType.ADMIN_SEARCH;
            }
        };
        for (final Map.Entry<String, QueryBuilder> entry : semanticQueries(true, admin).entrySet()) {
            assertTrue(((BoolQueryBuilder) entry.getValue()).filter().isEmpty(), entry.getKey());
            assertFalse(knnBodyOf(entry.getValue()).containsKey("filter"), entry.getKey());
        }
        final QueryBuilder condition = QueryBuilders.termQuery("label", "news");
        final SemanticChunkSearcher.SemanticQueryContext context =
                new SemanticChunkSearcher.SemanticQueryContext(new float[] { 0.1f, 0.2f }, true, condition);
        final QueryBuilder query = new GuardedSearcher().buildSemanticQuery(context, admin);
        same(List.of(condition), ((BoolQueryBuilder) query).filter());
        same(toMap(condition), knnBodyOf(query).get("filter"));
    }

    /**
     * The role is read from the request when the query is built, not remembered from an earlier
     * one: two users on one searcher get their own clause.
     */
    @Test
    public void test_permission_theRoleOfEachRequestIsUsed() {
        givenPermissionContext();
        final GuardedSearcher searcher = new GuardedSearcher();
        searcher.ann = true;
        final StubSearchRequestParams params = new StubSearchRequestParams(0, 10);
        getMockRequest().setAttribute(USER_ROLES_ATTRIBUTE, Set.of("Ralice"));
        final String alice = searcher.buildSubQuery("plain query", params, OptionalThing.empty()).get().toString();
        getMockRequest().setAttribute(USER_ROLES_ATTRIBUTE, Set.of("Rbob"));
        final String bob = searcher.buildSubQuery("plain query", params, OptionalThing.empty()).get().toString();
        assertTrue(alice.contains("\"Ralice\"") && !alice.contains("\"Rbob\""), alice);
        assertTrue(bob.contains("\"Rbob\"") && !bob.contains("\"Ralice\""), bob);
    }

    /**
     * Whatever the user types, the permission clause is built from the request and never from the
     * query text: queries that try to name a role, a virtual host or "everything", that close a
     * group early, that carry a sort or a stray quote, in the typed query, in ex_q or in a facet
     * value, on the first run and on the escaped retry, either skip the branch or produce the same
     * permission clause as a plain query, as the first filter of the outer bool, with no should.
     */
    @Test
    public void test_permission_hostileInputNeverChangesThePermissionClause() {
        givenPermissionContext();
        ComponentUtil.register(new QueryStringBuilder(), "queryStringBuilder");
        final String[] typed = { "role:Radmin reset", "-role:Rguest reset", "reset OR *:*", "reset *:*", "virtual_host:vh2 reset",
                "reset) OR (role:Radmin", "reset sort:filename.asc", "reset \"", "reset role:* OR label:x", "DRguest Radmin", "reset\\" };
        final String[][] extraQueries = { {}, { "role:Radmin" }, { "a OR *:*" }, { "-label:x" }, { "x \"" } };
        final String[][] facetValues = { {}, { "a\"b" }, { "x\" OR role:\"Radmin" }, { "c\\d" } };
        final BoolQueryBuilder expected = expectedPermission(false, true);
        int built = 0;
        for (final String q : typed) {
            for (final String[] ex : extraQueries) {
                for (final String[] fv : facetValues) {
                    final StubSearchRequestParams params = new StubSearchRequestParams(0, 10) {
                        @Override
                        public String getQuery() {
                            return q;
                        }

                        @Override
                        public String[] getExtraQueries() {
                            return ex;
                        }

                        @Override
                        public java.util.Map<String, String[]> getFields() {
                            return fv.length == 0 ? java.util.Collections.emptyMap() : java.util.Collections.singletonMap("label", fv);
                        }
                    };
                    for (final boolean retry : new boolean[] { false, true }) {
                        final String assembled = new QueryStringBuilder().params(params).escape(retry).build();
                        for (final boolean ann : new boolean[] { true, false }) {
                            final GuardedSearcher searcher = new GuardedSearcher();
                            searcher.ann = ann;
                            final OptionalThing<SemanticChunkSearcher.SemanticQueryContext> context = searcher.prepare(assembled, params);
                            if (!context.isPresent()) {
                                continue; // the branch is skipped: nothing reaches the index
                            }
                            built++;
                            final String label = assembled + " ann=" + ann;
                            final BoolQueryBuilder outer = (BoolQueryBuilder) searcher.buildSemanticQuery(context.get(), params);
                            same(expected, outer.filter().get(0), label);
                            assertTrue(outer.should().isEmpty() && outer.mustNot().isEmpty(), label);
                            assertEquals(1, outer.must().size(), label);
                            assertEquals(context.get().getConditionFilter() == null ? 1 : 2, outer.filter().size(), label);
                            if (ann) {
                                final Object knnFilter = knnBodyOf(outer).get("filter");
                                same(toMap(context.get().getConditionFilter() == null ? expected
                                        : QueryBuilders.boolQuery().filter(expected).filter(context.get().getConditionFilter())), knnFilter,
                                        label);
                            }
                        }
                    }
                }
            }
        }
        // the loop is not vacuous: a good share of these inputs do reach the vector branch
        assertTrue(built > 100, "built=" + built);
    }

    // -------------------------------------------------------------------------------------
    //                                                                      mode diagnostics
    //                                                                      ----------------

    @Test
    public void test_search_warnsOnceWhenExactModeIsUsed() {
        final LogCapturingAppender appender = LogCapturingAppender.attach(SemanticChunkSearcher.class);
        try {
            final GuardedSearcher guarded = new EmptyResponseSearcher();
            for (int i = 0; i < 3; i++) {
                guarded.search("plain query", new StubSearchRequestParams(0, 10), OptionalThing.empty());
            }
            final List<String> exactWarnings =
                    appender.messagesAt(Level.WARN).stream().filter(m -> m.contains("exact vector scan")).toList();
            assertEquals(1, exactWarnings.size(), "the exact-mode fallback must warn exactly once: " + appender.messagesAt(Level.WARN));
            assertTrue(exactWarnings.get(0).contains("content_chunker.search.enabled"),
                    "the warning must name the remedy: " + exactWarnings.get(0));
            // the queries after the first stay visible at DEBUG
            final List<String> exactNotices =
                    appender.messagesAt(Level.DEBUG).stream().filter(m -> m.contains("still using the exact vector scan")).toList();
            assertEquals(2, exactNotices.size(), appender.messagesAt(Level.DEBUG).toString());
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_warnExactModeOnce_isNotRearmedWhenAnnModeReturns() {
        final LogCapturingAppender appender = LogCapturingAppender.attach(SemanticChunkSearcher.class);
        try {
            searcher.warnExactModeOnce(true);
            assertTrue(appender.events().isEmpty(), "ann mode says nothing: " + appender.renderedEvents());
            searcher.warnExactModeOnce(false);
            assertEquals(1, appender.eventsAt(Level.WARN).size(), appender.renderedEvents().toString());
            // warnOnce has no way to report a key again: a regression after the ann mode was
            // available again is reported at DEBUG only
            searcher.warnExactModeOnce(true);
            searcher.warnExactModeOnce(false);
            assertEquals(1, appender.eventsAt(Level.WARN).size(), appender.renderedEvents().toString());
            assertEquals(1, appender.messagesAt(Level.DEBUG).stream().filter(m -> m.contains("still using the exact vector scan")).count());
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_search_doesNotWarnWhenAnnModeIsUsed() {
        final LogCapturingAppender appender = LogCapturingAppender.attach(SemanticChunkSearcher.class);
        try {
            final GuardedSearcher guarded = new EmptyResponseSearcher() {
                @Override
                protected boolean isKnnIndexReady() {
                    return true;
                }
            };
            guarded.search("plain query", new StubSearchRequestParams(0, 10), OptionalThing.empty());
            assertTrue(appender.messagesAt(Level.WARN).stream().noneMatch(m -> m.contains("exact vector scan")),
                    appender.messagesAt(Level.WARN).toString());
            assertTrue(appender.messagesAt(Level.DEBUG).stream().noneMatch(m -> m.contains("exact vector scan")),
                    appender.messagesAt(Level.DEBUG).toString());
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_register_logsRestartRequirementWhenDisabled() {
        final LogCapturingAppender appender = LogCapturingAppender.attach(SemanticChunkSearcher.class);
        try {
            final GuardedSearcher disabled = new GuardedSearcher();
            disabled.enabled = false;
            disabled.register();
            // register() is the only src/main caller of RankFusionProcessor#register and it runs
            // once at @PostConstruct, so flipping the property at runtime can never take effect.
            // That has to be stated at INFO, not hidden behind DEBUG.
            final List<String> infos = appender.messagesAt(Level.INFO);
            assertTrue(infos.stream().anyMatch(m -> m.contains(SemanticChunkSearcher.SEARCH_ENABLED_PROPERTY) && m.contains("restart")),
                    "a disabled searcher must state the restart requirement at INFO: " + infos);
        } finally {
            appender.detach();
        }
    }

    // -------------------------------------------------------------------------------------
    //                                                                  ann readiness probing
    //                                                                  ---------------------

    @Test
    public void test_hasKnnIndexSetting() {
        assertTrue(new ProbeSearcher().withSettings(Settings.builder().put("index.knn", true).build()).hasKnnIndexSetting());
        assertFalse(new ProbeSearcher().withSettings(Settings.builder().put("index.knn", false).build()).hasKnnIndexSetting());
        assertFalse(new ProbeSearcher().withSettings(Settings.EMPTY).hasKnnIndexSetting());
    }

    @Test
    public void test_hasAnnChunkVectorMapping_requiresKnnVectorTypeAndMethodBlock() {
        ComponentUtil.register(new ChunkVectorHelper(), ChunkVectorHelper.class.getCanonicalName());
        // the whole point of the design: index.knn alone is not enough, the chunk vector field
        // must be a knn_vector carrying an explicit ANN method block
        assertTrue(new ProbeSearcher().withVectorMapping(Map.of("type", "knn_vector", "dimension", 3, "method", Map.of("name", "hnsw")))
                .hasAnnChunkVectorMapping());
        assertFalse(new ProbeSearcher().withVectorMapping(Map.of("type", "knn_vector", "dimension", 3)).hasAnnChunkVectorMapping(),
                "a method-less knn_vector scores on a different scale and must not enable ann mode");
        assertFalse(
                new ProbeSearcher().withVectorMapping(Map.of("type", "float", "method", Map.of("name", "hnsw"))).hasAnnChunkVectorMapping(),
                "a non knn_vector field must not enable ann mode");
        assertFalse(new ProbeSearcher().withVectorMapping(Map.of("type", "knn_vector", "method", "hnsw")).hasAnnChunkVectorMapping(),
                "a scalar method value is not an ANN method block");
        assertFalse(new ProbeSearcher().withNoChunkVectorField().hasAnnChunkVectorMapping());
    }

    @Test
    public void test_isKnnIndexReady_requiresBothSettingAndMapping() {
        assertTrue(new ProbeSearcher().settingReady(true).mappingReady(true).isKnnIndexReady());
        assertFalse(new ProbeSearcher().settingReady(true).mappingReady(false).isKnnIndexReady(),
                "index.knn without an ANN mapping must not select ann mode");
        assertFalse(new ProbeSearcher().settingReady(false).mappingReady(true).isKnnIndexReady(),
                "an ANN mapping without index.knn must not select ann mode");
        assertFalse(new ProbeSearcher().settingReady(false).mappingReady(false).isKnnIndexReady());
    }

    @Test
    public void test_isKnnIndexReady_cachesTheProbe() {
        final ProbeSearcher probe = new ProbeSearcher().settingReady(true).mappingReady(true);
        assertTrue(probe.isKnnIndexReady());
        assertTrue(probe.isKnnIndexReady());
        assertEquals(1, probe.settingProbeCount, "the readiness answer must be cached between requests");
        assertEquals(1, probe.mappingProbeCount);
    }

    @Test
    public void test_isKnnIndexReady_swallowsProbeFailure() {
        final ProbeSearcher probe = new ProbeSearcher().settingReady(true).mappingReady(true);
        probe.probeFailure = new RuntimeException("probe boom");
        assertFalse(probe.isKnnIndexReady(), "a failed probe must degrade to the exact mode, not propagate");
    }

    @Test
    public void test_search_invalidatesKnnReadyCacheWhenAnnSearchFails() {
        final ProbeSearcher probe = new ProbeSearcher().settingReady(true).mappingReady(true);
        probe.failSendRequest = true;
        for (int i = 0; i < 2; i++) {
            assertEquals(0, probe.search("plain query", new StubSearchRequestParams(0, 10), OptionalThing.empty()).getAllRecordCount());
        }
        // an ann query that blew up (e.g. the index was reindexed without index.knn while the
        // cache was warm) must force a re-probe instead of failing for the next 60 seconds
        assertEquals(2, probe.settingProbeCount, "a failed ann search must invalidate the readiness cache");
    }

    // -------------------------------------------------------------------------------------
    //                                                                          test helpers
    //                                                                          ------------

    private void givenPermissionContext() {
        ComponentUtil.register(new QueryHelper(), "queryHelper");
        ComponentUtil.register(new RoleQueryHelper(), "roleQueryHelper");
        ComponentUtil.register(new VirtualHostHelper(), "virtualHostHelper");
        getMockRequest().setAttribute(USER_ROLES_ATTRIBUTE, Set.of("Rguest"));
        getMockRequest().setAttribute(FessConfig.VIRTUAL_HOST_VALUE, "vhost1");
    }

    private SearchRequestBuilder buildRequest(final SemanticChunkSearcher target, final boolean annMode, final SearchRequestParams params) {
        final SemanticChunkSearcher.SemanticQueryContext context =
                new SemanticChunkSearcher.SemanticQueryContext(new float[] { 0.1f, 0.2f }, annMode);
        final SearchCondition<SearchRequestBuilder> condition =
                target.createSemanticSearchCondition(context, "plain query", params, OptionalThing.empty());
        final SearchRequestBuilder builder = new SearchRequestBuilder(new SearchEngineClient(), SearchAction.INSTANCE);
        assertTrue(condition.build(builder));
        return builder;
    }

    private String buildQueryJson(final SemanticChunkSearcher target, final boolean annMode, final SearchRequestParams params) {
        return buildRequest(target, annMode, params).request().source().query().toString().replaceAll("\\s", "");
    }

    private static int countOccurrences(final String text, final String token) {
        int count = 0;
        int index = text.indexOf(token);
        while (index >= 0) {
            count++;
            index = text.indexOf(token, index + token.length());
        }
        return count;
    }

    /**
     * Searcher exercising the real readiness probing: the settings/mappings round trips are
     * replaced by canned responses, everything above them (parsing, the {@code &&}, the cache)
     * stays production code.
     */
    private static class ProbeSearcher extends SemanticChunkSearcher {
        int settingProbeCount = 0;
        int mappingProbeCount = 0;
        boolean failSendRequest = false;
        RuntimeException probeFailure;
        private Settings settings = Settings.EMPTY;
        private Map<String, Object> properties = Map.of();
        private final StubProcessorSplitter splitter = new StubProcessorSplitter();

        @Override
        protected StructuredQuerySplitter getQuerySplitter() {
            return splitter;
        }

        ProbeSearcher withSettings(final Settings settings) {
            this.settings = settings;
            return this;
        }

        ProbeSearcher settingReady(final boolean ready) {
            return withSettings(Settings.builder().put("index.knn", ready).build());
        }

        ProbeSearcher mappingReady(final boolean ready) {
            return ready ? withVectorMapping(Map.of("type", "knn_vector", "dimension", 3, "method", Map.of("name", "hnsw")))
                    : withNoChunkVectorField();
        }

        ProbeSearcher withVectorMapping(final Map<String, Object> vectorMapping) {
            properties = Map.of(Constants.CONTENT_CHUNK_VECTOR_FIELD,
                    Map.of("type", "nested", "properties", Map.of(ChunkVectorHelper.VECTOR_SUBFIELD, vectorMapping)));
            return this;
        }

        ProbeSearcher withNoChunkVectorField() {
            properties = Map.of("content", Map.of("type", "text"));
            return this;
        }

        @Override
        protected boolean isSearchEnabled() {
            return true;
        }

        @Override
        protected GetSettingsResponse readLiveIndexSettings() {
            settingProbeCount++;
            if (probeFailure != null) {
                throw probeFailure;
            }
            return new GetSettingsResponse(Map.of("fess.001", settings), Map.of());
        }

        @Override
        protected GetMappingsResponse readLiveIndexMappings() {
            mappingProbeCount++;
            if (probeFailure != null) {
                throw probeFailure;
            }
            return new GetMappingsResponse(Map.of("fess.001", new MappingMetadata("_doc", Map.of("properties", properties))));
        }

        @Override
        protected org.codelibs.fess.embedding.EmbeddingClientManager getEmbeddingClientManager() {
            return new org.codelibs.fess.embedding.EmbeddingClientManager() {
                @Override
                public boolean available() {
                    return true;
                }

                @Override
                public float[] embedQuery(final String query) {
                    return new float[] { 0.1f, 0.2f };
                }
            };
        }

        @Override
        protected OptionalEntity<SearchResponse> sendRequest(final SearchRequestParams params,
                final SearchCondition<SearchRequestBuilder> condition) {
            if (failSendRequest) {
                throw new RuntimeException("engine boom");
            }
            return OptionalEntity.empty();
        }
    }

    /** Guarded searcher whose engine round-trip is short-circuited to an empty response. */
    private static class EmptyResponseSearcher extends GuardedSearcher {
        @Override
        protected OptionalEntity<SearchResponse> sendRequest(final SearchRequestParams params,
                final SearchCondition<SearchRequestBuilder> condition) {
            return OptionalEntity.empty();
        }
    }

    private static class CapturingRankFusionProcessor extends RankFusionProcessor {
        boolean registered = false;

        @Override
        public void register(final RankFusionSearcher searcher) {
            registered = true;
        }
    }

    /**
     * Searcher stub: enabled by default, provider available by default, ann probe off,
     * and flags when the embedding provider is consulted.
     */
    private static class GuardedSearcher extends SemanticChunkSearcher {
        boolean enabled = true;
        boolean available = true;
        boolean managerTouched = false;
        Float minScore = null;
        final StubProcessorSplitter splitter = new StubProcessorSplitter();
        String embeddedQuery;
        int embedCount = 0;
        RuntimeException embedFailure;

        @Override
        protected boolean isSearchEnabled() {
            return enabled;
        }

        @Override
        protected OptionalThing<Float> getMinScore() {
            return minScore == null ? OptionalThing.empty() : OptionalThing.of(minScore);
        }

        @Override
        protected StructuredQuerySplitter getQuerySplitter() {
            // the real one needs the query command container; the split itself is covered by
            // StructuredQuerySplitterTest
            return splitter;
        }

        boolean ann = false;

        @Override
        protected boolean isKnnIndexReady() {
            return ann;
        }

        @Override
        protected org.codelibs.fess.embedding.EmbeddingClientManager getEmbeddingClientManager() {
            managerTouched = true;
            return new org.codelibs.fess.embedding.EmbeddingClientManager() {
                @Override
                public boolean available() {
                    return available;
                }

                @Override
                public float[] embedQuery(final String query) {
                    embedCount++;
                    embeddedQuery = query;
                    if (embedFailure != null) {
                        throw embedFailure;
                    }
                    // what the model answers depends on the words
                    return new float[] { query.length(), 0.2f };
                }
            };
        }
    }

    private static class StubSearchRequestParams extends org.codelibs.fess.entity.SearchRequestParams {
        private final int start;
        private final int size;

        StubSearchRequestParams(final int start, final int size) {
            this.start = start;
            this.size = size;
        }

        @Override
        public String getQuery() {
            return null;
        }

        @Override
        public java.util.Map<String, String[]> getFields() {
            return java.util.Collections.emptyMap();
        }

        @Override
        public java.util.Map<String, String[]> getConditions() {
            return java.util.Collections.emptyMap();
        }

        @Override
        public String[] getLanguages() {
            return new String[0];
        }

        @Override
        public org.codelibs.fess.entity.GeoInfo getGeoInfo() {
            return null;
        }

        @Override
        public org.codelibs.fess.entity.FacetInfo getFacetInfo() {
            return null;
        }

        @Override
        public org.codelibs.fess.entity.HighlightInfo getHighlightInfo() {
            return null;
        }

        @Override
        public String getSort() {
            return null;
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
            // the real implementation resolves queryFieldConfig from the container, which
            // test_app.xml does not define
            return new String[] { "doc_id" };
        }

        @Override
        public Object getAttribute(final String name) {
            return null;
        }

        @Override
        public java.util.Locale getLocale() {
            return java.util.Locale.ROOT;
        }

        @Override
        public SearchRequestType getType() {
            return SearchRequestType.SEARCH;
        }

        @Override
        public String getSimilarDocHash() {
            return null;
        }
    }
}
