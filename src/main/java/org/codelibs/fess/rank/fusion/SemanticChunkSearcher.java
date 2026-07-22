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
import org.codelibs.fess.opensearch.client.SearchEngineClient.SearchCondition;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.opensearch.action.search.SearchRequestBuilder;
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
 * <p>This searcher runs in "exact" mode: it issues a {@code nested} +
 * {@code script_score} (Painless {@code cosineSimilarity}) query, which works on the
 * existing {@code knn_vector} mapping without {@code index.knn=true} and therefore
 * without recreating the index. The cost is a full scan over stored chunk vectors,
 * which is fine for small-to-medium indexes; an ANN mode can be layered on later.</p>
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

    /** System property key enabling this searcher (read once at registration). */
    public static final String SEARCH_ENABLED_PROPERTY = "content_chunker.search.enabled";

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

    /** Holds the query vector between {@link #search} and {@link #createSearchCondition}. */
    protected final ThreadLocal<float[]> queryVectorHolder = new ThreadLocal<>();

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
        queryVectorHolder.set(queryVector);
        try {
            return super.search(query, new SemanticSearchRequestParams(params), userBean);
        } finally {
            queryVectorHolder.remove();
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
            final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().must(buildChunkVectorQuery(queryVector));
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
            searchRequestBuilder.setQuery(boolQuery)
                    .setFrom(params.getStartPosition())
                    .setSize(params.getPageSize())
                    .setFetchSource(params.getResponseFields(), null)
                    .setTrackTotalHits(true);
            getMinScore().ifPresent(minScore -> searchRequestBuilder.setMinScore(minScore + SCORE_OFFSET));
            return true;
        };
    }

    /**
     * Builds the nested exact-kNN query scoring each document by the cosine
     * similarity of its best chunk vector.
     *
     * @param queryVector the query embedding
     * @return the nested script_score query
     */
    protected QueryBuilder buildChunkVectorQuery(final float[] queryVector) {
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
        return Constants.TRUE.equalsIgnoreCase(ComponentUtil.getFessConfig().getSystemProperty(SEARCH_ENABLED_PROPERTY, Constants.FALSE));
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
