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
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.join.ScoreMode;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.embedding.EmbeddingClientManager;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.helper.ChunkVectorHelper;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient.SearchCondition;
import org.codelibs.fess.opensearch.query.KnnQueryBuilder;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.opensearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.opensearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.cluster.metadata.MappingMetadata;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.script.Script;
import org.opensearch.script.ScriptType;

import jakarta.annotation.PostConstruct;

/**
 * Rank-fusion searcher that scores documents by the cosine similarity between the
 * query embedding and the per-chunk vectors ingested by {@link ChunkVectorHelper}
 * ({@code content_chunk_vector}), fusing semantic hits with the default BM25
 * searcher via reciprocal rank fusion.
 *
 * <p>The query mode is selected automatically per request from the live index:</p>
 * <ul>
 * <li><b>ann</b> — when the index was created with {@code index.knn=true} (see
 * {@link ChunkVectorHelper}'s setting/mapping rewrite rules), an approximate-kNN
 * {@code knn} query runs against the HNSW graph; search cost stays sub-linear in
 * the number of stored chunk vectors, so this is the mode for large indexes.
 * Enabling it on an existing index requires recreating/reindexing the index with
 * {@code content_chunker.search.enabled=true} so the setting and the ANN method
 * are baked in.</li>
 * <li><b>exact</b> — otherwise, a {@code nested} + {@code script_score} (Painless
 * {@code cosineSimilarity}) full scan over stored chunk vectors is used. It needs
 * no index recreation but scales linearly, so it is only suitable for
 * small-to-medium indexes.</li>
 * </ul>
 *
 * <p>Activation requires {@code content_chunker.search.enabled=true} (system
 * property channel) at startup in addition to the chunking pipeline being enabled.
 * The searcher degrades to a no-op (BM25-only fusion result) when the embedding
 * provider is unavailable, when the query is blank, or when the query uses Fess
 * query syntax (field filters, boolean operators, wildcards) — only plain keyword
 * or natural-language queries take the semantic branch.</p>
 */
public class SemanticChunkSearcher extends DefaultSearcher {

    private static final Logger logger = LogManager.getLogger(SemanticChunkSearcher.class);

    /** System property key enabling this searcher (owned by {@link ChunkVectorHelper}). */
    public static final String SEARCH_ENABLED_PROPERTY = ChunkVectorHelper.SEMANTIC_SEARCH_ENABLED_PROPERTY;

    /** System property key for the number of nearest neighbors requested per shard in ann mode. */
    public static final String KNN_K_PROPERTY = "content_chunker.search.knn.k";

    /** System property key for the optional per-query HNSW ef_search override in ann mode. */
    public static final String KNN_PARAM_EF_SEARCH_PROPERTY = "content_chunker.search.knn.param.ef_search";

    /** Default value of {@link #KNN_K_PROPERTY}. */
    protected static final int DEFAULT_KNN_K = 100;

    /** How long a live index.knn readiness answer is cached. */
    protected static final long KNN_READY_CHECK_INTERVAL_MS = 60_000L;

    /**
     * System property key for the minimum cosine similarity (0..1 range) a document's
     * best chunk must reach to be returned by the semantic branch. Unset means no cutoff.
     */
    public static final String SEARCH_MIN_SCORE_PROPERTY = "content_chunker.search.min_score";

    /**
     * Matches queries that use Fess/Lucene query syntax; such queries skip the
     * semantic branch because the whole query string is embedded as one text.
     */
    protected static final Pattern QUERY_SYNTAX_PATTERN =
            Pattern.compile("[\"():\\[\\]{}^~*?\\\\]|&&|\\|\\||(?:^|\\s)[+\\-]\\S|\\b(?:AND|OR|NOT|TO)\\b");

    /**
     * The offset added to the cosine similarity inside the Painless script so scores
     * stay positive (OpenSearch rejects negative script scores).
     */
    protected static final float SCORE_OFFSET = 1.0f;

    /** One-time warn latch for provider unavailability, reset when it recovers. */
    private final AtomicBoolean embeddingUnavailableWarned = new AtomicBoolean(false);

    /** One-time warn latch for a min_score cutoff skipped due to a non-cosine space type. */
    private final AtomicBoolean minScoreSkippedWarned = new AtomicBoolean(false);

    /** Holds the query vector between {@link #search} and {@link #createSearchCondition}. */
    protected final ThreadLocal<float[]> queryVectorHolder = new ThreadLocal<>();

    /** Holds the resolved query mode (ann vs exact) between {@link #search} and {@link #createSearchCondition}. */
    protected final ThreadLocal<Boolean> annModeHolder = new ThreadLocal<>();

    /** Timestamp of the last {@link #isKnnIndexReady()} probe. */
    private volatile long knnReadyCheckedAt;

    /** Cached result of the last {@link #isKnnIndexReady()} probe. */
    private volatile boolean knnReady;

    /**
     * Default constructor.
     */
    public SemanticChunkSearcher() {
        super();
    }

    /**
     * Registers this searcher with the rank fusion processor when
     * {@code content_chunker.search.enabled=true}. Called by the DI container.
     */
    @PostConstruct
    public void register() {
        if (!isSearchEnabled()) {
            if (logger.isDebugEnabled()) {
                logger.debug("SemanticChunkSearcher is disabled ({}=false).", SEARCH_ENABLED_PROPERTY);
            }
            return;
        }
        logger.info("Load {}", getClass().getSimpleName());
        ComponentUtil.getRankFusionProcessor().register(this);
    }

    @Override
    protected SearchResult search(final String query, final SearchRequestParams params, final OptionalThing<FessUserBean> userBean) {
        if (!isSearchEnabled() || StringUtil.isBlank(query) || !isPlainQuery(query)) {
            return emptyResult();
        }
        // Geo and similar-document constraints are hard filters on the default path; the
        // semantic branch does not apply them, so skip it rather than fuse unfiltered hits.
        if (params.getGeoInfo() != null && params.getGeoInfo().toQueryBuilder() != null
                || StringUtil.isNotBlank(params.getSimilarDocHash())) {
            return emptyResult();
        }
        final EmbeddingClientManager embeddingClientManager = getEmbeddingClientManager();
        if (!embeddingClientManager.available()) {
            if (embeddingUnavailableWarned.compareAndSet(false, true)) {
                logger.warn("Embedding provider is unavailable; semantic chunk search falls back to keyword-only results "
                        + "until the provider recovers.");
            } else if (logger.isDebugEnabled()) {
                logger.debug("Embedding provider still unavailable; skipping semantic chunk search.");
            }
            return emptyResult();
        }
        embeddingUnavailableWarned.set(false);
        final float[] queryVector;
        try {
            queryVector = embeddingClientManager.embedQuery(query);
        } catch (final Exception e) {
            logger.warn("Failed to embed query for semantic chunk search; falling back to keyword-only results. query={}", query, e);
            return emptyResult();
        }
        if (queryVector == null || queryVector.length == 0) {
            return emptyResult();
        }
        final boolean annMode = isKnnIndexReady();
        queryVectorHolder.set(queryVector);
        annModeHolder.set(annMode);
        try {
            return super.search(query, new SemanticSearchRequestParams(params), userBean);
        } catch (final Exception e) {
            // This searcher is auxiliary: never let its failure (e.g. a knn query hitting an
            // index that was reindexed without index.knn while the readiness cache was still
            // warm) escape as a search error -- RankFusionProcessor rethrows InvalidQueryException
            // and the escape-retry cannot change a plain query. Degrade to keyword-only results
            // and re-probe the index on the next request.
            if (annMode) {
                knnReadyCheckedAt = 0;
            }
            logger.warn("Semantic chunk search failed (mode={}); falling back to keyword-only results. query={}", annMode ? "ann" : "exact",
                    query, e);
            return emptyResult();
        } finally {
            queryVectorHolder.remove();
            annModeHolder.remove();
        }
    }

    @Override
    protected SearchCondition<SearchRequestBuilder> createSearchCondition(final String query, final SearchRequestParams params,
            final OptionalThing<FessUserBean> userBean) {
        final float[] queryVector = queryVectorHolder.get();
        if (queryVector == null) {
            return super.createSearchCondition(query, params, userBean);
        }
        return searchRequestBuilder -> {
            final QueryHelper queryHelper = ComponentUtil.getQueryHelper();
            queryHelper.processSearchPreference(searchRequestBuilder, userBean, query);
            final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            if (params.getType() != SearchRequestType.ADMIN_SEARCH) {
                final Set<String> roleSet = ComponentUtil.getRoleQueryHelper().build(params.getType());
                if (!roleSet.isEmpty()) {
                    queryHelper.buildRoleQuery(roleSet, boolQuery);
                }
                final String virtualHostKey = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
                if (StringUtil.isNotBlank(virtualHostKey)) {
                    boolQuery.filter(QueryBuilders.termQuery(ComponentUtil.getFessConfig().getIndexFieldVirtualHost(), virtualHostKey));
                }
            }
            final boolean annMode = Boolean.TRUE.equals(annModeHolder.get());
            searchRequestBuilder
                    .setQuery(boolQuery.must(annMode ? buildKnnChunkQuery(queryVector, params) : buildExactChunkQuery(queryVector)))
                    .setFrom(params.getStartPosition())
                    .setSize(params.getPageSize())
                    .setFetchSource(params.getResponseFields(), null)
                    .setTrackTotalHits(true);
            getMinScore().ifPresent(minScore -> resolveEngineMinScore(minScore, annMode).ifPresent(searchRequestBuilder::setMinScore));
            if (logger.isDebugEnabled()) {
                logger.debug("Semantic chunk search mode: {}", annMode ? "ann" : "exact");
            }
            return true;
        };
    }

    /**
     * Builds the approximate-kNN (HNSW) query for indexes created with
     * {@code index.knn=true} and an ANN method on the chunk vector field. This is
     * the scalable path: search cost stays sub-linear in the number of stored
     * chunk vectors.
     *
     * @param queryVector the query embedding
     * @param params the request params (used to size k against the requested window)
     * @return the nested knn query
     */
    protected QueryBuilder buildKnnChunkQuery(final float[] queryVector, final SearchRequestParams params) {
        final String vectorField = Constants.CONTENT_CHUNK_VECTOR_FIELD + "." + ChunkVectorHelper.VECTOR_SUBFIELD;
        final int k = Math.max(getKnnK(), params.getStartPosition() + params.getPageSize());
        final KnnQueryBuilder knnQuery = new KnnQueryBuilder(vectorField, queryVector, k);
        getKnnEfSearch().ifPresent(knnQuery::efSearch);
        return QueryBuilders.nestedQuery(Constants.CONTENT_CHUNK_VECTOR_FIELD, knnQuery, ScoreMode.Max).ignoreUnmapped(true);
    }

    /**
     * Builds the exact-kNN query scoring each document by the cosine similarity of
     * its best chunk vector. This works on any index carrying chunk vectors — even
     * one created without {@code index.knn} — but scans every stored vector, so it
     * is only suitable for small-to-medium indexes; ANN-ready indexes use
     * {@link #buildKnnChunkQuery(float[], SearchRequestParams)} instead.
     *
     * @param queryVector the query embedding
     * @return the nested script_score query
     */
    protected QueryBuilder buildExactChunkQuery(final float[] queryVector) {
        final List<Float> vector = new ArrayList<>(queryVector.length);
        for (final float v : queryVector) {
            vector.add(v);
        }
        final Map<String, Object> scriptParams = new HashMap<>();
        scriptParams.put("query_vector", vector);
        final String vectorField = Constants.CONTENT_CHUNK_VECTOR_FIELD + "." + ChunkVectorHelper.VECTOR_SUBFIELD;
        final Script script = new Script(ScriptType.INLINE, "painless",
                "cosineSimilarity(params.query_vector, doc['" + vectorField + "']) + " + SCORE_OFFSET, scriptParams);
        return QueryBuilders
                .nestedQuery(Constants.CONTENT_CHUNK_VECTOR_FIELD, QueryBuilders.scriptScoreQuery(QueryBuilders.matchAllQuery(), script),
                        ScoreMode.Max)
                .ignoreUnmapped(true);
    }

    /**
     * Converts the configured cosine cutoff (0..1) to the engine score scale of the
     * active mode. The exact script adds {@link #SCORE_OFFSET} to the cosine. In ann
     * mode the k-NN plugin's score depends on the engine and space type baked into
     * the mapping: {@code lucene}+{@code cosinesimil} scores as {@code (1 + cos) / 2},
     * {@code faiss}/{@code nmslib}+{@code cosinesimil} as {@code 1 / (2 - cos)}. For
     * any other space type a cosine cutoff is not well-defined, so the cutoff is
     * skipped with a warning rather than silently mis-filtering.
     *
     * @param minCosine the configured minimum cosine similarity
     * @param annMode whether the ann (knn query) mode is active
     * @return the score cutoff on the engine's scale, or empty to skip the cutoff
     */
    protected OptionalThing<Float> resolveEngineMinScore(final float minCosine, final boolean annMode) {
        if (!annMode) {
            return OptionalThing.of(minCosine + SCORE_OFFSET);
        }
        final ChunkVectorHelper chunkVectorHelper = ComponentUtil.getComponent(ChunkVectorHelper.class);
        final String spaceType = chunkVectorHelper.getKnnSpaceType();
        if (!"cosinesimil".equals(spaceType)) {
            if (minScoreSkippedWarned.compareAndSet(false, true)) {
                logger.warn("{} is cosine-based and cannot be applied to space_type={}; the cutoff is skipped in ann mode.",
                        SEARCH_MIN_SCORE_PROPERTY, spaceType);
            }
            return OptionalThing.empty();
        }
        if ("lucene".equals(chunkVectorHelper.getKnnEngine())) {
            return OptionalThing.of((1.0f + minCosine) / 2.0f);
        }
        return OptionalThing.of(1.0f / (2.0f - minCosine));
    }

    /**
     * Checks (with a short-lived cache) whether the live document index can serve
     * the ann mode: the index was created with {@code index.knn=true} AND the
     * {@code content_chunk_vector.vector} field is mapped as a {@code knn_vector}
     * with an explicit ANN {@code method} block (the settings flag alone is not
     * enough — e.g. another feature may have enabled {@code index.knn} while the
     * chunk field was mapped method-less, whose engine defaults would score on a
     * different scale). Both are baked at index creation, so a cached answer only
     * changes when the index is recreated; the cache avoids the probes per search,
     * and probing is serialized so concurrent expiries do not stampede the engine.
     *
     * @return true if the ann mode can be used
     */
    protected boolean isKnnIndexReady() {
        final long now = System.currentTimeMillis();
        if (now - knnReadyCheckedAt < KNN_READY_CHECK_INTERVAL_MS) {
            return knnReady;
        }
        synchronized (this) {
            if (now - knnReadyCheckedAt < KNN_READY_CHECK_INTERVAL_MS) {
                return knnReady;
            }
            boolean ready = false;
            try {
                ready = hasKnnIndexSetting() && hasAnnChunkVectorMapping();
            } catch (final Exception e) {
                logger.warn("Failed to probe the live index for ann readiness; using the exact vector scan for now.", e);
            }
            knnReady = ready;
            knnReadyCheckedAt = System.currentTimeMillis();
            return ready;
        }
    }

    /**
     * Checks whether any live index behind the search alias has {@code index.knn=true}.
     *
     * @return true if the setting is enabled
     */
    protected boolean hasKnnIndexSetting() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final GetSettingsResponse response = ComponentUtil.getSearchEngineClient()
                .admin()
                .indices()
                .prepareGetSettings(fessConfig.getIndexDocumentSearchIndex())
                .execute()
                .actionGet(fessConfig.getIndexIndicesTimeout());
        for (final String index : response.getIndexToSettings().keySet()) {
            if (response.getIndexToSettings().get(index).getAsBoolean("index.knn", false)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the live {@code content_chunk_vector.vector} mapping is a
     * {@code knn_vector} with an explicit ANN {@code method} block.
     *
     * @return true if the chunk vector field is ANN-ready
     */
    protected boolean hasAnnChunkVectorMapping() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final GetMappingsResponse response = ComponentUtil.getSearchEngineClient()
                .admin()
                .indices()
                .prepareGetMappings(fessConfig.getIndexDocumentSearchIndex())
                .execute()
                .actionGet(fessConfig.getIndexIndicesTimeout());
        final ChunkVectorHelper chunkVectorHelper = ComponentUtil.getComponent(ChunkVectorHelper.class);
        for (final MappingMetadata metadata : response.mappings().values()) {
            final Map<?, ?> propertiesMap = chunkVectorHelper.resolveMappingFields(metadata);
            if (propertiesMap == null) {
                continue;
            }
            final Object chunkVector = propertiesMap.get(Constants.CONTENT_CHUNK_VECTOR_FIELD);
            if (!(chunkVector instanceof final Map<?, ?> chunkVectorMap)
                    || !(chunkVectorMap.get("properties") instanceof final Map<?, ?> subPropertiesMap)
                    || !(subPropertiesMap.get(ChunkVectorHelper.VECTOR_SUBFIELD) instanceof final Map<?, ?> vectorMap)) {
                continue;
            }
            if ("knn_vector".equals(vectorMap.get("type")) && vectorMap.get("method") instanceof Map) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the semantic branch should handle the query: plain keyword or
     * natural-language text only, no Fess/Lucene query syntax.
     *
     * @param query the assembled query string
     * @return true if the query is plain text
     */
    protected boolean isPlainQuery(final String query) {
        return !QUERY_SYNTAX_PATTERN.matcher(query).find();
    }

    /**
     * Checks the {@value #SEARCH_ENABLED_PROPERTY} system property.
     *
     * @return true if semantic chunk search is enabled
     */
    protected boolean isSearchEnabled() {
        return ComponentUtil.getComponent(ChunkVectorHelper.class).isSemanticSearchEnabled();
    }

    /**
     * Reads the configured k for the ann mode.
     *
     * @return the number of nearest neighbors requested per shard
     */
    protected int getKnnK() {
        final String value = ComponentUtil.getFessConfig().getSystemProperty(KNN_K_PROPERTY, null);
        if (StringUtil.isBlank(value)) {
            return DEFAULT_KNN_K;
        }
        try {
            final int parsed = Integer.parseInt(value.trim());
            if (parsed > 0) {
                return parsed;
            }
        } catch (final NumberFormatException e) {
            // fall through
        }
        logger.warn("Invalid integer for {}: {}", KNN_K_PROPERTY, value);
        return DEFAULT_KNN_K;
    }

    /**
     * Reads the optional per-query HNSW ef_search override for the ann mode.
     *
     * @return the ef_search value, or empty when unset or unparsable
     */
    protected OptionalThing<Integer> getKnnEfSearch() {
        final String value = ComponentUtil.getFessConfig().getSystemProperty(KNN_PARAM_EF_SEARCH_PROPERTY, null);
        if (StringUtil.isBlank(value)) {
            return OptionalThing.empty();
        }
        try {
            final int parsed = Integer.parseInt(value.trim());
            if (parsed > 0) {
                return OptionalThing.of(parsed);
            }
        } catch (final NumberFormatException e) {
            // fall through
        }
        logger.warn("Invalid integer for {}: {}", KNN_PARAM_EF_SEARCH_PROPERTY, value);
        return OptionalThing.empty();
    }

    /**
     * Reads the optional minimum cosine similarity cutoff.
     *
     * @return the cutoff, or empty when unset or unparsable
     */
    protected OptionalThing<Float> getMinScore() {
        final String value = ComponentUtil.getFessConfig().getSystemProperty(SEARCH_MIN_SCORE_PROPERTY, null);
        if (StringUtil.isBlank(value)) {
            return OptionalThing.empty();
        }
        try {
            return OptionalThing.of(Float.valueOf(value));
        } catch (final NumberFormatException e) {
            logger.warn("Invalid float for {}: {}", SEARCH_MIN_SCORE_PROPERTY, value);
            return OptionalThing.empty();
        }
    }

    /**
     * Gets the embedding client manager component.
     *
     * @return the embedding client manager
     */
    protected EmbeddingClientManager getEmbeddingClientManager() {
        return ComponentUtil.getComponent(EmbeddingClientManager.class);
    }

    /**
     * Builds an empty search result, degrading the fused response to the other
     * searchers' results.
     *
     * @return an empty result
     */
    protected SearchResult emptyResult() {
        return SearchResult.create().build();
    }

    /**
     * Wraps the caller's params to strip facet/geo/highlight from the semantic
     * request: those aggregations belong to the main searcher only (rank fusion
     * uses the main searcher's facets), so requesting them here would be wasted
     * work on a query that scores by vectors.
     */
    protected static class SemanticSearchRequestParams extends SearchRequestParams {
        private final SearchRequestParams parent;

        /**
         * Creates a wrapper around the caller's params.
         *
         * @param parent the original request params
         */
        protected SemanticSearchRequestParams(final SearchRequestParams parent) {
            this.parent = parent;
        }

        @Override
        public String getQuery() {
            return parent.getQuery();
        }

        @Override
        public Map<String, String[]> getFields() {
            return parent.getFields();
        }

        @Override
        public Map<String, String[]> getConditions() {
            return parent.getConditions();
        }

        @Override
        public String[] getLanguages() {
            return parent.getLanguages();
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
            return parent.getSort();
        }

        @Override
        public int getStartPosition() {
            return parent.getStartPosition();
        }

        @Override
        public int getPageSize() {
            return parent.getPageSize();
        }

        @Override
        public int getOffset() {
            return parent.getOffset();
        }

        @Override
        public String[] getExtraQueries() {
            return parent.getExtraQueries();
        }

        @Override
        public Object getAttribute(final String name) {
            return parent.getAttribute(name);
        }

        @Override
        public Locale getLocale() {
            return parent.getLocale();
        }

        @Override
        public SearchRequestType getType() {
            return parent.getType();
        }

        @Override
        public String getSimilarDocHash() {
            return parent.getSimilarDocHash();
        }

        @Override
        public String getTrackTotalHits() {
            return parent.getTrackTotalHits();
        }

        @Override
        public Float getMinScore() {
            return parent.getMinScore();
        }
    }
}
