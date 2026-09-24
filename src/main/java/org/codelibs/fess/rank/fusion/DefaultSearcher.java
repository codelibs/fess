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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient.SearchCondition;
import org.codelibs.fess.opensearch.query.HybridQueryBuilder;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.codelibs.fesen.opensearch.action.search.SearchRequestBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;
import org.codelibs.fesen.opensearch.search.builder.SearchSourceBuilder;

/**
 * The searcher Fess uses by default for document searches.
 *
 * <p>On its own it is the keyword searcher it inherits from {@link AbstractDocumentSearcher}. It
 * additionally knows how to fold other searchers' queries into a single request, so that the
 * search engine ranks the combined result set instead of Fess merging separate result lists.
 * That is what makes facets, total hits, sorting and highlighting describe the whole result
 * rather than the keyword branch of it.</p>
 *
 * <p>The fused request is a {@code hybrid} query carrying every branch as a subquery, plus an
 * inline search pipeline that normalizes and combines the per-branch scores. Both are written in
 * {@link #fuse}, together, because a hybrid query sent without its pipeline does not fail - it
 * returns duplicated hits and sentinel scores.</p>
 */
public class DefaultSearcher extends AbstractDocumentSearcher {

    private static final Logger logger = LogManager.getLogger(DefaultSearcher.class);

    /** Combination technique that selects the score-ranker (reciprocal rank fusion) processor. */
    protected static final String TECHNIQUE_RRF = "rrf";

    /** Normalization technique the search engine combines with arithmetic_mean only. */
    protected static final String NORMALIZATION_Z_SCORE = "z_score";

    /** The only combination technique the search engine accepts after z_score normalization. */
    protected static final String TECHNIQUE_ARITHMETIC_MEAN = "arithmetic_mean";

    /** Combination techniques the search engine knows. */
    protected static final Set<String> COMBINATION_TECHNIQUES =
            Set.of(TECHNIQUE_RRF, TECHNIQUE_ARITHMETIC_MEAN, "geometric_mean", "harmonic_mean");

    /** Normalization techniques the search engine's normalization processor knows. */
    protected static final Set<String> NORMALIZATION_TECHNIQUES = Set.of("min_max", "l2", NORMALIZATION_Z_SCORE, TECHNIQUE_RRF);

    /** Lowest rank constant the score-ranker processor accepts. */
    protected static final int MIN_RANK_CONSTANT = 1;

    /** Highest rank constant the score-ranker processor accepts. */
    protected static final int MAX_RANK_CONSTANT = 10000;

    /** How far a weight sum may drift from 1.0 before it is rejected. */
    protected static final float WEIGHT_SUM_TOLERANCE = 0.001f;

    /** Set once the search engine has refused a fused request, so it is not attempted again. */
    protected final AtomicBoolean engineFusionDisabled = new AtomicBoolean(false);

    /** One-time notice latch for a request that cannot be fused because it pages too deep. */
    protected final AtomicBoolean paginationDepthNoticed = new AtomicBoolean(false);

    /** One-time error latch for a fused result the search engine never normalized. */
    protected final AtomicBoolean notNormalizedWarned = new AtomicBoolean(false);

    /**
     * Creates a new instance.
     */
    public DefaultSearcher() {
    }

    @Override
    protected Optional<SearchResult> searchWithSubQueries(final String query, final SearchRequestParams params,
            final OptionalThing<FessUserBean> userBean, final List<QueryBuilder> subQueries) {
        if (!isEngineFusionApplicable(params, subQueries)) {
            return Optional.empty();
        }
        final List<String> names = new ArrayList<>(subQueries.size() + 1);
        names.add(getName());
        subQueries.forEach(subQuery -> names.add(subQuery.queryName()));
        final Map<String, Object> pipeline = buildPipelineSource(names);
        if (pipeline == null) {
            return Optional.empty();
        }
        try {
            final SearchResult searchResult = execute(params, fuse(createSearchCondition(query, params, userBean), subQueries, pipeline));
            warnIfNotNormalized(searchResult);
            return Optional.of(searchResult);
        } catch (final Exception e) {
            // The likeliest cause is a cluster without the Neural Search plugin, where every
            // fused request fails the same way. Retrying one per search would double the load
            // for no benefit, so stop trying until the next restart and say why once.
            if (engineFusionDisabled.compareAndSet(false, true)) {
                logger.error("The search engine refused a fused request, so rank fusion falls back to Fess for the rest of this run. "
                        + "Check that the cluster has the Neural Search plugin installed and that hybrid queries are not disabled "
                        + "(plugins.neural_search.hybrid_search_disabled). query={}", query, e);
            }
            return Optional.empty();
        }
    }

    /**
     * Reports a fused result that the search engine never normalized.
     *
     * <p>A hybrid query whose pipeline did not run comes back with duplicated hits and large
     * negative sentinel scores rather than an error, so the only way to notice is to look at what
     * came back. The scores of a normalized result are never negative.</p>
     *
     * @param searchResult the fused result
     */
    protected void warnIfNotNormalized(final SearchResult searchResult) {
        for (final Map<String, Object> doc : searchResult.getDocumentList()) {
            if (doc.get(Constants.SCORE) instanceof final Number score && score.floatValue() < 0.0f) {
                if (notNormalizedWarned.compareAndSet(false, true)) {
                    logger.error("A fused search came back with negative scores, which is what the search engine returns when a "
                            + "hybrid query runs without the pipeline that normalizes it. These results are unranked and may repeat "
                            + "documents. Check that the cluster accepts an inline search_pipeline carrying phase_results_processors.");
                }
                return;
            }
        }
    }

    /**
     * Wraps the keyword condition so that the request carries every branch and the pipeline that
     * combines them.
     *
     * <p>The keyword query is read back out of the request the base condition just built, rather
     * than rebuilt here, so that everything else that condition set up - facets, highlighting,
     * sorting, collapsing, the offset guard - stays exactly as it was.</p>
     *
     * @param base the keyword search condition
     * @param subQueries the other searchers' queries
     * @param pipeline the inline search pipeline that normalizes and combines the branches
     * @return the fused search condition
     */
    protected SearchCondition<SearchRequestBuilder> fuse(final SearchCondition<SearchRequestBuilder> base,
            final List<QueryBuilder> subQueries, final Map<String, Object> pipeline) {
        return searchRequestBuilder -> {
            if (!base.build(searchRequestBuilder)) {
                return false;
            }
            final SearchSourceBuilder source = searchRequestBuilder.request().source();
            if (source == null || source.query() == null) {
                // Fusing means wrapping the keyword query, so there has to be one. Failing here
                // sends the search back to Fess-side fusion instead of quietly issuing a request
                // that has lost every other branch.
                throw new IllegalStateException("The keyword condition did not produce a query to fuse.");
            }
            final HybridQueryBuilder hybridQuery = new HybridQueryBuilder().add(source.query().queryName(getName()));
            subQueries.forEach(hybridQuery::add);
            source.query(hybridQuery.paginationDepth(getPaginationDepth()));
            // Written here rather than by the caller: a hybrid query without this pipeline comes
            // back with duplicated hits and sentinel scores instead of an error.
            source.searchPipelineSource(pipeline);
            return true;
        };
    }

    /**
     * Decides whether this request can be fused in the search engine.
     *
     * @param params the search request parameters
     * @param subQueries the other searchers' queries
     * @return true when the request can be fused
     */
    protected boolean isEngineFusionApplicable(final SearchRequestParams params, final List<QueryBuilder> subQueries) {
        if (engineFusionDisabled.get() || !ComponentUtil.getFessConfig().isRankFusionEngineEnabled()) {
            return false;
        }
        if (subQueries.isEmpty() || subQueries.size() + 1 > HybridQueryBuilder.MAX_SUB_QUERIES) {
            return false;
        }
        // Sorting by anything but the score makes the engine report null scores for every hit,
        // which leaves nothing for the fusion to have decided. Advanced search assembles clauses
        // the vector branches cannot mirror.
        if (StringUtil.isNotBlank(params.getSort()) || params.hasConditionQuery()) {
            return false;
        }
        if (params.getGeoInfo() != null && params.getGeoInfo().toQueryBuilder() != null
                || StringUtil.isNotBlank(params.getSimilarDocHash())) {
            return false;
        }
        final int depth = getPaginationDepth();
        if (params.getStartPosition() + params.getPageSize() > depth) {
            // Beyond the depth the engine ranks to, a fused page would silently be built from a
            // truncated set. Fall back rather than return a page nobody can explain.
            if (paginationDepthNoticed.compareAndSet(false, true)) {
                logger.info("A search reached past rank.fusion.pagination_depth ({}), so it was answered without fusing in the "
                        + "search engine. Raise the property to page deeper into fused results.", depth);
            }
            return false;
        }
        return true;
    }

    /**
     * Builds the inline search pipeline that normalizes and combines the branch scores.
     *
     * @param names the branch names, in the order the branches appear in the request
     * @return the pipeline source, or null when the configuration is unusable
     */
    protected Map<String, Object> buildPipelineSource(final List<String> names) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String configuredTechnique = fessConfig.getRankFusionCombinationTechnique();
        // The engine looks the names up exactly, so they are checked and sent in lower case
        final String technique = toTechniqueName(configuredTechnique);
        if (!COMBINATION_TECHNIQUES.contains(technique)) {
            return rejectTechnique(FessConfig.RANK_FUSION_COMBINATION_TECHNIQUE, configuredTechnique, COMBINATION_TECHNIQUES);
        }
        // rrf ranks rather than normalizes, so the normalization is neither read nor sent for it
        String normalization = null;
        if (!TECHNIQUE_RRF.equals(technique)) {
            final String configuredNormalization = fessConfig.getRankFusionNormalizationTechnique();
            normalization = toTechniqueName(configuredNormalization);
            if (!NORMALIZATION_TECHNIQUES.contains(normalization)) {
                return rejectTechnique(FessConfig.RANK_FUSION_NORMALIZATION_TECHNIQUE, configuredNormalization, NORMALIZATION_TECHNIQUES);
            }
            if (NORMALIZATION_Z_SCORE.equals(normalization) && !TECHNIQUE_ARITHMETIC_MEAN.equals(technique)) {
                // The engine fails every request for this pair, so name the settings here instead
                logger.error(
                        "{}='{}' cannot be used with {}='{}': the search engine combines z_score only with {}. "
                                + "Rank fusion falls back to Fess for this search.",
                        FessConfig.RANK_FUSION_COMBINATION_TECHNIQUE, configuredTechnique, FessConfig.RANK_FUSION_NORMALIZATION_TECHNIQUE,
                        configuredNormalization, TECHNIQUE_ARITHMETIC_MEAN);
                return null;
            }
        }
        final Map<String, Object> combination = new LinkedHashMap<>();
        combination.put("technique", technique);
        if (TECHNIQUE_RRF.equals(technique)) {
            combination.put("rank_constant", Integer.valueOf(getEngineRankConstant()));
        }
        final List<Float> weights = resolveWeights(names);
        if (weights == null) {
            return null;
        }
        if (!weights.isEmpty()) {
            combination.put("parameters", Map.of("weights", weights));
        }
        final Map<String, Object> processorBody = new LinkedHashMap<>();
        if (normalization != null) {
            processorBody.put("normalization", Map.of("technique", normalization));
        }
        processorBody.put("combination", combination);
        final String processorName = TECHNIQUE_RRF.equals(technique) ? "score-ranker-processor" : "normalization-processor";
        return Map.of("phase_results_processors", List.of(Map.of(processorName, processorBody)));
    }

    /**
     * Converts a configured technique to the name the search engine looks up.
     *
     * @param configured the configured value
     * @return the trimmed, lower-case name, or an empty string when unset
     */
    protected String toTechniqueName(final String configured) {
        return configured == null ? "" : configured.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * Reports a technique the search engine does not know and refuses to fuse.
     *
     * <p>The engine rejects the whole request for an unknown technique, so this is caught here to
     * name the property instead of failing every search.</p>
     *
     * @param key the property key
     * @param configured the configured value
     * @param supported the techniques the engine knows
     * @return null, meaning the request must not be fused
     */
    protected Map<String, Object> rejectTechnique(final String key, final String configured, final Set<String> supported) {
        logger.error("{}='{}' cannot be used: the search engine supports only {}. Rank fusion falls back to Fess for this search.", key,
                configured, new TreeSet<>(supported));
        return null;
    }

    /**
     * Resolves the configured weights into the order the branches appear in the request.
     *
     * <p>The property names each branch rather than relying on its position, because the set of
     * searchers taking part depends on which plugins are installed and on
     * {@code rank.fusion.searchers}. A positional list would silently start weighting the wrong
     * branch, or be rejected outright by the engine, as soon as that set changed.</p>
     *
     * @param names the branch names, in request order
     * @return the weights in request order, an empty list when none are configured, or null when
     *         the configuration does not match the branches taking part
     */
    protected List<Float> resolveWeights(final List<String> names) {
        final String configured = ComponentUtil.getFessConfig().getRankFusionCombinationWeights();
        if (StringUtil.isBlank(configured)) {
            return List.of();
        }
        final Map<String, Float> byName = new LinkedHashMap<>();
        for (final String entry : configured.split(",")) {
            final String pair = entry.trim();
            if (pair.isEmpty()) {
                continue;
            }
            final int index = pair.lastIndexOf(':');
            if (index <= 0 || index == pair.length() - 1) {
                return rejectWeights(configured, "expected name:weight pairs");
            }
            final Float weight;
            try {
                weight = Float.valueOf(pair.substring(index + 1).trim());
            } catch (final NumberFormatException e) {
                return rejectWeights(configured, "the weight of '" + pair.substring(0, index).trim() + "' is not a number");
            }
            if (weight.floatValue() < 0.0f || weight.floatValue() > 1.0f) {
                return rejectWeights(configured, "weights must be between 0.0 and 1.0");
            }
            byName.put(pair.substring(0, index).trim(), weight);
        }
        if (byName.size() != names.size() || !byName.keySet().containsAll(names)) {
            return rejectWeights(configured, "it must name exactly the searchers taking part: " + names);
        }
        float sum = 0.0f;
        final List<Float> weights = new ArrayList<>(names.size());
        for (final String name : names) {
            final Float weight = byName.get(name);
            weights.add(weight);
            sum += weight.floatValue();
        }
        if (Math.abs(sum - 1.0f) > WEIGHT_SUM_TOLERANCE) {
            return rejectWeights(configured, "the weights must sum to 1.0 but sum to " + sum);
        }
        return weights;
    }

    /**
     * Reports an unusable weight configuration and refuses to fuse.
     *
     * <p>The engine fails the whole request when the weights do not match the branches, so this
     * is caught here to name the property and the reason instead of surfacing a phase failure.</p>
     *
     * @param configured the configured value
     * @param reason why it cannot be used
     * @return null, meaning the request must not be fused
     */
    protected List<Float> rejectWeights(final String configured, final String reason) {
        logger.error("{}='{}' cannot be used: {}. Rank fusion falls back to Fess for this search.",
                FessConfig.RANK_FUSION_COMBINATION_WEIGHTS, configured, reason);
        return null;
    }

    /**
     * Converts the configured rank constant to the one the search engine needs.
     *
     * <p>Fess ranks from zero and scores a document {@code 1 / (K + rank)}; the engine ranks from
     * one and scores it {@code 1 / (k + rank)}. Passing {@code K - 1} makes the two formulas
     * identical, so turning engine-side fusion on does not change what the constant means.</p>
     *
     * @return the rank constant to send
     */
    protected int getEngineRankConstant() {
        final int configured = ComponentUtil.getFessConfig().getRankFusionRankConstantAsInteger().intValue() - 1;
        if (configured < MIN_RANK_CONSTANT) {
            return MIN_RANK_CONSTANT;
        }
        if (configured > MAX_RANK_CONSTANT) {
            return MAX_RANK_CONSTANT;
        }
        return configured;
    }

    /**
     * Returns how many results each branch contributes per shard.
     *
     * @return the pagination depth
     */
    protected int getPaginationDepth() {
        return ComponentUtil.getFessConfig().getRankFusionPaginationDepthAsInteger().intValue();
    }

}
