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
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.collection.ArrayUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient.SearchCondition;
import org.codelibs.fess.opensearch.client.SearchEngineClient.SearchConditionBuilder;
import org.codelibs.fess.rank.fusion.SearchResult.SearchResultBuilder;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.codelibs.fess.util.FacetResponse;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.util.LaRequestUtil;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.common.document.DocumentField;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import org.opensearch.search.aggregations.Aggregations;
import org.opensearch.search.fetch.subphase.highlight.HighlightField;

/**
 * Base implementation of {@link RankFusionSearcher} that runs a single OpenSearch request and
 * turns its response into a {@link SearchResult}. It owns the keyword search condition, the
 * request round-trip, and the hit-to-document conversion (highlighting, provenance, scores).
 *
 * <p>The class exists so that every searcher in this package - the keyword default, the legacy
 * keyword-only variant, and the vector branches - can share that machinery without depending on
 * each other. Subclasses customize the request by overriding
 * {@link #createSearchCondition(String, SearchRequestParams, OptionalThing)} and, when they need
 * to build the condition themselves, by calling
 * {@link #execute(SearchRequestParams, SearchCondition)} directly. That seam is what lets a
 * subclass compute request state (a query embedding, a set of sub-queries) in its own
 * {@code search} and hand it to the condition without smuggling it through a thread local.</p>
 */
public abstract class AbstractDocumentSearcher extends RankFusionSearcher {

    /** Logger for this class. */
    private static final Logger logger = LogManager.getLogger(AbstractDocumentSearcher.class);

    /**
     * Creates a new instance.
     */
    protected AbstractDocumentSearcher() {
    }

    /**
     * Performs a search operation using the specified query and parameters.
     *
     * @param query the search query string
     * @param params the search request parameters
     * @param userBean the optional user bean for access control
     * @return the search result containing documents and metadata
     */
    @Override
    protected SearchResult search(final String query, final SearchRequestParams params, final OptionalThing<FessUserBean> userBean) {
        return execute(params, createSearchCondition(query, params, userBean));
    }

    /**
     * Runs the given search condition and converts the response into a {@link SearchResult}.
     *
     * <p>This is the seam between building a request and issuing it. A subclass that has to
     * compute state before the condition can be built - a query embedding, the sub-queries of a
     * fused request - builds its own condition and calls this method, instead of passing that
     * state to {@link #createSearchCondition(String, SearchRequestParams, OptionalThing)} out of
     * band.</p>
     *
     * @param params the search request parameters
     * @param condition the condition describing the request to issue
     * @return the processed search result
     */
    protected SearchResult execute(final SearchRequestParams params, final SearchCondition<SearchRequestBuilder> condition) {
        final int pageSize = params.getPageSize();
        LaRequestUtil.getOptionalRequest().ifPresent(request -> {
            request.setAttribute(Constants.REQUEST_PAGE_SIZE, pageSize);
        });
        return processResponse(sendRequest(params, condition));
    }

    /**
     * Processes the OpenSearch response and converts it to a SearchResult.
     *
     * @param searchResponseOpt the optional search response from OpenSearch
     * @return the processed search result
     */
    protected SearchResult processResponse(final OptionalEntity<SearchResponse> searchResponseOpt) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final SearchResultBuilder builder = SearchResult.create();
        searchResponseOpt.ifPresent(searchResponse -> {
            final SearchHits searchHits = searchResponse.getHits();
            builder.allRecordCount(searchHits.getTotalHits().value());
            builder.allRecordCountRelation(searchHits.getTotalHits().relation().toString());
            builder.queryTime(searchResponse.getTook().millis());

            if (searchResponse.getTotalShards() != searchResponse.getSuccessfulShards()) {
                builder.partialResults(true);
            }

            // build highlighting fields
            final String hlPrefix = ComponentUtil.getQueryHelper().getHighlightPrefix();
            for (final SearchHit searchHit : searchHits.getHits()) {
                final Map<String, Object> docMap = parseSearchHit(fessConfig, hlPrefix, searchHit);

                if (fessConfig.isResultCollapsed()) {
                    final Map<String, SearchHits> innerHits = searchHit.getInnerHits();
                    if (innerHits != null) {
                        final SearchHits innerSearchHits = innerHits.get(fessConfig.getQueryCollapseInnerHitsName());
                        if (innerSearchHits != null) {
                            final long totalHits = innerSearchHits.getTotalHits().value();
                            if (totalHits > 1) {
                                docMap.put(fessConfig.getQueryCollapseInnerHitsName() + "_count", totalHits);
                                final DocumentField bitsField = searchHit.getFields().get(fessConfig.getIndexFieldContentMinhashBits());
                                if (bitsField != null && !bitsField.getValues().isEmpty()) {
                                    docMap.put(fessConfig.getQueryCollapseInnerHitsName() + "_hash", bitsField.getValues().get(0));
                                }
                                docMap.put(fessConfig.getQueryCollapseInnerHitsName(), StreamUtil.stream(innerSearchHits.getHits())
                                        .get(stream -> stream.map(hit -> parseSearchHit(fessConfig, hlPrefix, hit)).toArray(Map[]::new)));
                            }
                        }
                    }
                }

                builder.addDocument(docMap);
            }

            // facet
            final Aggregations aggregations = searchResponse.getAggregations();
            if (aggregations != null) {
                builder.facetResponse(new FacetResponse(aggregations));
            }

        });
        return builder.build();
    }

    /**
     * Sends a search request to OpenSearch for the given condition.
     *
     * @param params the search request parameters
     * @param condition the condition describing the request to issue
     * @return the optional search response from OpenSearch
     */
    protected OptionalEntity<SearchResponse> sendRequest(final SearchRequestParams params,
            final SearchCondition<SearchRequestBuilder> condition) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return ComponentUtil.getSearchEngineClient()
                .search(fessConfig.getIndexDocumentSearchIndex(), condition, (searchRequestBuilder, execTime, searchResponse) -> {
                    searchResponse.ifPresent(r -> {
                        if (r.getTotalShards() != r.getSuccessfulShards() && fessConfig.isQueryTimeoutLogging()) {
                            // partial results
                            final StringBuilder buf = new StringBuilder(1000);
                            buf.append("[SEARCH TIMEOUT] {\"exec_time\":")
                                    .append(execTime)//
                                    .append(",\"request\":")
                                    .append(searchRequestBuilder.toString())//
                                    .append(",\"response\":")
                                    .append(r.toString())
                                    .append('}');
                            logger.warn(buf.toString());
                        }
                    });
                    return searchResponse;
                });
    }

    /**
     * Creates a search condition for the OpenSearch request.
     *
     * @param query the search query string
     * @param params the search request parameters
     * @param userBean the optional user bean for access control
     * @return the search condition for the request
     */
    protected SearchCondition<SearchRequestBuilder> createSearchCondition(final String query, final SearchRequestParams params,
            final OptionalThing<FessUserBean> userBean) {
        return searchRequestBuilder -> {
            ComponentUtil.getQueryHelper().processSearchPreference(searchRequestBuilder, userBean, query);
            return SearchConditionBuilder.builder(searchRequestBuilder)
                    .query(query)
                    .offset(params.getStartPosition())
                    .size(params.getPageSize())
                    .facetInfo(params.getFacetInfo())
                    .geoInfo(params.getGeoInfo())
                    .highlightInfo(params.getHighlightInfo())
                    .similarDocHash(params.getSimilarDocHash())
                    .responseFields(params.getResponseFields())
                    .searchRequestType(params.getType())
                    .trackTotalHits(params.getTrackTotalHits())
                    .minScore(params.getMinScore())
                    .build();
        };
    }

    /**
     * Builds the constraint that decides which documents this request is allowed to see: the
     * caller's roles and, when one is active, the virtual host.
     *
     * <p>It lives here so that every searcher applies the same clause, however it retrieves.
     * A branch that bypasses {@code SearchConditionBuilder} - the vector branches do, because
     * they replace the whole query - must still apply this, and it must be the same clause the
     * keyword branch gets from {@code QueryHelper}, not a re-derivation of it.</p>
     *
     * @param params the search request parameters
     * @return the permission query, with no clauses when the request is exempt
     */
    protected BoolQueryBuilder buildPermissionQuery(final SearchRequestParams params) {
        final BoolQueryBuilder permissionQuery = QueryBuilders.boolQuery();
        if (params.getType() == SearchRequestType.ADMIN_SEARCH) {
            return permissionQuery;
        }
        final QueryHelper queryHelper = ComponentUtil.getQueryHelper();
        final Set<String> roleSet = ComponentUtil.getRoleQueryHelper().build(params.getType());
        if (!roleSet.isEmpty()) {
            queryHelper.buildRoleQuery(roleSet, permissionQuery);
        }
        final String virtualHostKey = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
        if (StringUtil.isNotBlank(virtualHostKey)) {
            permissionQuery.filter(QueryBuilders.termQuery(ComponentUtil.getFessConfig().getIndexFieldVirtualHost(), virtualHostKey));
        }
        return permissionQuery;
    }

    /**
     * Parses a search hit from OpenSearch and converts it to a document map.
     *
     * @param fessConfig the Fess configuration
     * @param hlPrefix the highlight prefix for field names
     * @param searchHit the search hit to parse
     * @return the parsed document as a map
     */
    protected Map<String, Object> parseSearchHit(final FessConfig fessConfig, final String hlPrefix, final SearchHit searchHit) {
        final Map<String, Object> docMap = new HashMap<>(32);
        if (searchHit.getSourceAsMap() == null) {
            searchHit.getFields().forEach((key, value) -> {
                docMap.put(key, value.getValue());
            });
        } else {
            docMap.putAll(searchHit.getSourceAsMap());
        }

        final ViewHelper viewHelper = ComponentUtil.getViewHelper();

        final Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
        try {
            if (highlightFields != null) {
                highlightFields.values().stream().forEach(highlightField -> {
                    final String text = viewHelper.createHighlightText(highlightField);
                    if (text != null) {
                        docMap.put(hlPrefix + highlightField.getName(), text);
                    }
                });
                if (Constants.TEXT_FRAGMENT_TYPE_HIGHLIGHT.equals(fessConfig.getQueryHighlightTextFragmentType())) {
                    docMap.put(Constants.TEXT_FRAGMENTS,
                            viewHelper.createTextFragmentsByHighlight(highlightFields.values().toArray(HighlightField[]::new)));
                }
            }
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Could not create a highlighting value: {}", docMap, e);
            }
        }

        if (Constants.TEXT_FRAGMENT_TYPE_QUERY.equals(fessConfig.getQueryHighlightTextFragmentType())) {
            docMap.put(Constants.TEXT_FRAGMENTS, viewHelper.createTextFragmentsByQuery());
        }

        // ContentTitle
        if (viewHelper != null) {
            docMap.put(fessConfig.getResponseFieldContentTitle(), viewHelper.getContentTitle(docMap));
            docMap.put(fessConfig.getResponseFieldContentDescription(), viewHelper.getContentDescription(docMap));
            docMap.put(fessConfig.getResponseFieldUrlLink(), viewHelper.getUrlLink(docMap));
            docMap.put(fessConfig.getResponseFieldSitePath(), viewHelper.getSitePath(docMap));
        }

        if (!docMap.containsKey(Constants.SCORE)) {
            final float score = searchHit.getScore();
            if (Float.isFinite(score)) {
                docMap.put(Constants.SCORE, score);
            }
        }

        if (!docMap.containsKey(fessConfig.getIndexFieldId())) {
            docMap.put(fessConfig.getIndexFieldId(), searchHit.getId());
        }

        String[] searchers = DocumentUtil.getValue(docMap, Constants.SEARCHER, String[].class);
        if (searchers == null) {
            searchers = new String[0];
        }
        for (final String searcherName : resolveSearcherNames(searchHit)) {
            if (!ArrayUtil.contains(searchers, searcherName)) {
                searchers = ArrayUtil.add(searchers, searcherName);
            }
        }
        docMap.put(Constants.SEARCHER, searchers);

        return docMap;
    }

    /**
     * Returns which searchers found this hit.
     *
     * <p>In a request that fuses several searchers there is only one response, so the hit itself
     * has to say which branches matched it. Each branch is sent as a named query, and the engine
     * reports the names that matched in {@code matched_queries}; without them - an ordinary
     * single-searcher response - the answer is simply this searcher.</p>
     *
     * @param searchHit the search hit
     * @return the searcher names that matched this hit
     */
    protected String[] resolveSearcherNames(final SearchHit searchHit) {
        final String[] matchedQueries = searchHit.getMatchedQueries();
        if (matchedQueries != null && matchedQueries.length > 0) {
            return matchedQueries;
        }
        return new String[] { getName() };
    }

}
