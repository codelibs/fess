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
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.helper.ChunkVectorHelper;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.helper.RoleQueryHelper;
import org.codelibs.fess.helper.VirtualHostHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.client.SearchEngineClient.SearchCondition;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.opensearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.opensearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.opensearch.action.search.SearchAction;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.cluster.metadata.MappingMetadata;
import org.opensearch.common.settings.Settings;

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
    public void test_resolveEngineMinScore() {
        org.codelibs.fess.util.ComponentUtil.register(new org.codelibs.fess.helper.ChunkVectorHelper(),
                org.codelibs.fess.helper.ChunkVectorHelper.class.getCanonicalName());
        // exact mode: script emits cosine + 1.0
        assertTrue(Math.abs(searcher.resolveEngineMinScore(0.4f, false).get() - 1.4f) < 0.0001f);
        // ann mode with default lucene + cosinesimil: engine emits (1 + cosine) / 2
        assertTrue(Math.abs(searcher.resolveEngineMinScore(0.4f, true).get() - 0.7f) < 0.0001f);
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
        // twice: the warn latch must not change the degradation behavior
        for (int i = 0; i < 2; i++) {
            final SearchResult result =
                    guarded.search("plain query", new StubSearchRequestParams(0, 10), org.dbflute.optional.OptionalThing.empty());
            assertEquals(0, result.getAllRecordCount());
        }
    }

    @Test
    public void test_search_degradesToEmptyWhenEngineCallFails() {
        final GuardedSearcher guarded = new GuardedSearcher() {
            @Override
            protected org.dbflute.optional.OptionalEntity<org.opensearch.action.search.SearchResponse> sendRequest(final String query,
                    final org.codelibs.fess.entity.SearchRequestParams params,
                    final org.dbflute.optional.OptionalThing<org.codelibs.fess.mylasta.action.FessUserBean> userBean) {
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
    //                                                              createSearchCondition
    //                                                              ----------------------

    @Test
    public void test_createSearchCondition_annCarriesPermissionFilterIntoKnn() {
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
        assertTrue(filterPart.contains("\"role\":{\"value\":\"Rguest\""), "the knn filter must carry the role terms: " + json);
        assertTrue(filterPart.contains("\"virtual_host\":{\"value\":\"vhost1\""),
                "the knn filter must carry the virtual host term: " + json);
        // ... and the outer query must keep enforcing it (the knn filter is a recall aid, not
        // the security boundary), so the role term is serialized twice.
        assertEquals(2, countOccurrences(json, "\"role\":{\"value\":\"Rguest\""), json);
        assertEquals(2, countOccurrences(json, "\"virtual_host\":{\"value\":\"vhost1\""), json);
    }

    @Test
    public void test_createSearchCondition_exactKeepsPermissionOnOuterQueryOnly() {
        givenPermissionContext();
        final String json = buildQueryJson(new GuardedSearcher(), false, new StubSearchRequestParams(0, 10));
        assertTrue(json.contains("script_score"), json);
        assertFalse(json.contains("\"knn\""), json);
        // exact mode has no in-query top-k truncation, so a single outer permission clause is enough
        assertEquals(1, countOccurrences(json, "\"role\":{\"value\":\"Rguest\""), json);
        assertEquals(1, countOccurrences(json, "\"virtual_host\":{\"value\":\"vhost1\""), json);
    }

    @Test
    public void test_createSearchCondition_doesNotTrackTotalHits() {
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
    //                                                                      mode diagnostics
    //                                                                      ----------------

    @Test
    public void test_search_warnsOnceWhenExactModeIsUsed() {
        final LogCapturingAppender appender = LogCapturingAppender.attach(SemanticChunkSearcher.class);
        try {
            final GuardedSearcher guarded = new EmptyResponseSearcher();
            for (int i = 0; i < 2; i++) {
                guarded.search("plain query", new StubSearchRequestParams(0, 10), OptionalThing.empty());
            }
            final List<String> exactWarnings =
                    appender.messagesAt(Level.WARN).stream().filter(m -> m.contains("exact vector scan")).toList();
            assertEquals(1, exactWarnings.size(), "the exact-mode fallback must warn exactly once: " + appender.messagesAt(Level.WARN));
            assertTrue(exactWarnings.get(0).contains("content_chunker.search.enabled"),
                    "the warning must name the remedy: " + exactWarnings.get(0));
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
        target.queryVectorHolder.set(new float[] { 0.1f, 0.2f });
        target.annModeHolder.set(annMode);
        try {
            final SearchCondition<SearchRequestBuilder> condition =
                    target.createSearchCondition("plain query", params, OptionalThing.empty());
            final SearchRequestBuilder builder = new SearchRequestBuilder(new SearchEngineClient(), SearchAction.INSTANCE);
            assertTrue(condition.build(builder));
            return builder;
        } finally {
            target.queryVectorHolder.remove();
            target.annModeHolder.remove();
        }
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
        protected OptionalEntity<SearchResponse> sendRequest(final String query, final SearchRequestParams params,
                final OptionalThing<FessUserBean> userBean) {
            if (failSendRequest) {
                throw new RuntimeException("engine boom");
            }
            return OptionalEntity.empty();
        }
    }

    /** Guarded searcher whose engine round-trip is short-circuited to an empty response. */
    private static class EmptyResponseSearcher extends GuardedSearcher {
        @Override
        protected OptionalEntity<SearchResponse> sendRequest(final String query, final SearchRequestParams params,
                final OptionalThing<FessUserBean> userBean) {
            return OptionalEntity.empty();
        }
    }

    /** Minimal in-memory log4j2 appender for asserting on emitted log messages. */
    static final class LogCapturingAppender extends AbstractAppender {
        private final List<LogEvent> events = new CopyOnWriteArrayList<>();
        private final org.apache.logging.log4j.core.Logger boundLogger;

        private LogCapturingAppender(final org.apache.logging.log4j.core.Logger logger) {
            super("LogCapturingAppender-" + UUID.randomUUID(), null, null, true, Property.EMPTY_ARRAY);
            this.boundLogger = logger;
        }

        static LogCapturingAppender attach(final Class<?> targetClass) {
            final org.apache.logging.log4j.core.Logger logger = (org.apache.logging.log4j.core.Logger) LogManager.getLogger(targetClass);
            final LogCapturingAppender appender = new LogCapturingAppender(logger);
            appender.start();
            logger.addAppender(appender);
            return appender;
        }

        void detach() {
            boundLogger.removeAppender(this);
            stop();
        }

        @Override
        public void append(final LogEvent event) {
            events.add(event.toImmutable());
        }

        List<String> messagesAt(final Level level) {
            return events.stream().filter(e -> e.getLevel() == level).map(e -> e.getMessage().getFormattedMessage()).toList();
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

        @Override
        protected boolean isSearchEnabled() {
            return enabled;
        }

        @Override
        protected boolean isKnnIndexReady() {
            return false;
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
                    return new float[] { 0.1f, 0.2f };
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
