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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.collection.ArrayUtil;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.SearchRequestParams;
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
import org.opensearch.common.document.DocumentField;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import org.opensearch.search.aggregations.Aggregations;
import org.opensearch.search.fetch.subphase.highlight.HighlightField;

/**
 * Default implementation of RankFusionSearcher that performs standard OpenSearch queries.
 * This searcher handles query execution, response processing, and document highlighting.
 */
public class DefaultSearcher extends RankFusionSearcher {

    /** Logger for this class. */
    private static final Logger logger = LogManager.getLogger(DefaultSearcher.class);

    /**
     * Creates a new instance of DefaultSearcher.
     * This constructor initializes the default rank fusion searcher for performing
     * standard OpenSearch queries with response processing and document highlighting.
     */
    public DefaultSearcher() {
        super();
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
        final int pageSize = params.getPageSize();
        LaRequestUtil.getOptionalRequest().ifPresent(request -> {
            request.setAttribute(Constants.REQUEST_PAGE_SIZE, pageSize);
        });
        final OptionalEntity<SearchResponse> searchResponseOpt = sendRequest(query, params, userBean);
        return processResponse(searchResponseOpt);
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
     * Sends a search request to OpenSearch with the specified parameters.
     *
     * @param query the search query string
     * @param params the search request parameters
     * @param userBean the optional user bean for access control
     * @return the optional search response from OpenSearch
     */
    protected OptionalEntity<SearchResponse> sendRequest(final String query, final SearchRequestParams params,
            final OptionalThing<FessUserBean> userBean) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return ComponentUtil.getSearchEngineClient()
                .search(fessConfig.getIndexDocumentSearchIndex(), createSearchCondition(query, params, userBean),
                        (searchRequestBuilder, execTime, searchResponse) -> {
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

        final String[] searchers = DocumentUtil.getValue(docMap, Constants.SEARCHER, String[].class);
        if (searchers != null) {
            docMap.put(Constants.SEARCHER, ArrayUtil.add(searchers, getName()));
        } else {
            docMap.put(Constants.SEARCHER, new String[] { getName() });
        }

        return docMap;
    }

}
