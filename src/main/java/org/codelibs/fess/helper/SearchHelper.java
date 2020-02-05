/*
 * Copyright 2012-2020 CodeLibs Project and the Others.
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

import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.es.client.FessEsClient.SearchConditionBuilder;
import org.codelibs.fess.es.client.FessEsClientException;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.BooleanFunction;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.QueryResponseList;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.dbflute.util.DfTypeUtil;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.lastaflute.taglib.function.LaFunctions;
import org.lastaflute.web.util.LaRequestUtil;

public class SearchHelper {

    // ===================================================================================
    //                                                                            Constant
    //

    private static final Logger logger = LogManager.getLogger(SearchHelper.class);

    // ===================================================================================
    //                                                                              Method
    //                                                                      ==============

    public void search(final SearchRequestParams params, final SearchRenderData data, final OptionalThing<FessUserBean> userBean) {
        final long requestedTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        final long startTime = System.currentTimeMillis();

        LaRequestUtil.getOptionalRequest().ifPresent(request -> {
            request.setAttribute(Constants.REQUEST_LANGUAGES, params.getLanguages());
            request.setAttribute(Constants.REQUEST_QUERIES, params.getQuery());
        });

        final int pageStart = params.getStartPosition();
        final int pageSize = params.getPageSize();
        final String sortField = params.getSort();
        final String query;
        if (StringUtil.isBlank(sortField)) {
            query = ComponentUtil.getQueryStringBuilder().params(params).build();
        } else {
            query = ComponentUtil.getQueryStringBuilder().params(params).build() + " sort:" + sortField;
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final QueryHelper queryHelper = ComponentUtil.getQueryHelper();
        final List<Map<String, Object>> documentItems =
                ComponentUtil.getFessEsClient().search(
                        fessConfig.getIndexDocumentSearchIndex(),
                        searchRequestBuilder -> {
                            queryHelper.processSearchPreference(searchRequestBuilder, userBean, query);
                            return SearchConditionBuilder.builder(searchRequestBuilder).query(query).offset(pageStart).size(pageSize)
                                    .facetInfo(params.getFacetInfo()).geoInfo(params.getGeoInfo()).highlightInfo(params.getHighlightInfo())
                                    .similarDocHash(params.getSimilarDocHash()).responseFields(queryHelper.getResponseFields())
                                    .searchRequestType(params.getType()).trackTotalHits(params.getTrackTotalHits()).build();
                        }, (searchRequestBuilder, execTime, searchResponse) -> {
                            searchResponse.ifPresent(r -> {
                                if (r.getTotalShards() != r.getSuccessfulShards() && fessConfig.isQueryTimeoutLogging()) {
                                    // partial results
                                    final StringBuilder buf = new StringBuilder(1000);
                                    buf.append("[SEARCH TIMEOUT] {\"exec_time\":").append(execTime)//
                                            .append(",\"request\":").append(searchRequestBuilder.toString())//
                                            .append(",\"response\":").append(r.toString()).append('}');
                                    logger.warn(buf.toString());
                                }
                            });
                            final QueryResponseList queryResponseList = ComponentUtil.getQueryResponseList();
                            queryResponseList.init(searchResponse, pageStart, pageSize);
                            return queryResponseList;
                        });
        data.setDocumentItems(documentItems);

        // search
        final QueryResponseList queryResponseList = (QueryResponseList) documentItems;
        data.setFacetResponse(queryResponseList.getFacetResponse());

        @SuppressWarnings("unchecked")
        final Set<String> highlightQueries = (Set<String>) params.getAttribute(Constants.HIGHLIGHT_QUERIES);
        if (highlightQueries != null) {
            final StringBuilder buf = new StringBuilder(100);
            highlightQueries.stream().forEach(q -> {
                buf.append("&hq=").append(LaFunctions.u(q));
            });
            data.setAppendHighlightParams(buf.toString());
        }

        queryResponseList.setExecTime(System.currentTimeMillis() - startTime);
        final NumberFormat nf = NumberFormat.getInstance(params.getLocale());
        nf.setMaximumIntegerDigits(2);
        nf.setMaximumFractionDigits(2);
        String execTime;
        try {
            execTime = nf.format((double) queryResponseList.getExecTime() / 1000);
        } catch (final Exception e) {
            execTime = StringUtil.EMPTY;
        }
        data.setExecTime(execTime);

        final String queryId = queryHelper.generateId();

        data.setPageSize(queryResponseList.getPageSize());
        data.setCurrentPageNumber(queryResponseList.getCurrentPageNumber());
        data.setAllRecordCount(queryResponseList.getAllRecordCount());
        data.setAllRecordCountRelation(queryResponseList.getAllRecordCountRelation());
        data.setAllPageCount(queryResponseList.getAllPageCount());
        data.setExistNextPage(queryResponseList.isExistNextPage());
        data.setExistPrevPage(queryResponseList.isExistPrevPage());
        data.setCurrentStartRecordNumber(queryResponseList.getCurrentStartRecordNumber());
        data.setCurrentEndRecordNumber(queryResponseList.getCurrentEndRecordNumber());
        data.setPageNumberList(queryResponseList.getPageNumberList());
        data.setPartialResults(queryResponseList.isPartialResults());
        data.setQueryTime(queryResponseList.getQueryTime());
        data.setSearchQuery(query);
        data.setRequestedTime(requestedTime);
        data.setQueryId(queryId);

        // search log
        if (fessConfig.isSearchLog()) {
            ComponentUtil.getSearchLogHelper().addSearchLog(params, DfTypeUtil.toLocalDateTime(requestedTime), queryId, query, pageStart,
                    pageSize, queryResponseList);
        }

        // favorite
        if (fessConfig.isUserFavorite()) {
            ComponentUtil.getUserInfoHelper().storeQueryId(queryId, documentItems);
        }

    }

    public long scrollSearch(final SearchRequestParams params, final BooleanFunction<Map<String, Object>> cursor,
            final OptionalThing<FessUserBean> userBean) {
        LaRequestUtil.getOptionalRequest().ifPresent(request -> {
            request.setAttribute(Constants.REQUEST_LANGUAGES, params.getLanguages());
            request.setAttribute(Constants.REQUEST_QUERIES, params.getQuery());
        });

        final int pageSize = params.getPageSize();
        final String sortField = params.getSort();
        final String query;
        if (StringUtil.isBlank(sortField)) {
            query = ComponentUtil.getQueryStringBuilder().params(params).build();
        } else {
            query = ComponentUtil.getQueryStringBuilder().params(params).build() + " sort:" + sortField;
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return ComponentUtil.getFessEsClient().<Map<String, Object>> scrollSearch(
                fessConfig.getIndexDocumentSearchIndex(),
                searchRequestBuilder -> {
                    final QueryHelper queryHelper = ComponentUtil.getQueryHelper();
                    queryHelper.processSearchPreference(searchRequestBuilder, userBean, query);
                    return SearchConditionBuilder.builder(searchRequestBuilder).scroll().query(query).size(pageSize)
                            .responseFields(queryHelper.getScrollResponseFields()).searchRequestType(params.getType()).build();
                },
                (searchResponse, hit) -> {
                    final Map<String, Object> docMap = new HashMap<>();
                    final Map<String, Object> source = hit.getSourceAsMap();
                    if (source != null) {
                        docMap.putAll(source);
                    }
                    final Map<String, DocumentField> fields = hit.getFields();
                    if (fields != null) {
                        docMap.putAll(fields.entrySet().stream()
                                .collect(Collectors.toMap(e -> e.getKey(), e -> (Object) e.getValue().getValues())));
                    }

                    final ViewHelper viewHelper = ComponentUtil.getViewHelper();
                    if (viewHelper != null && !docMap.isEmpty()) {
                        docMap.put(fessConfig.getResponseFieldContentTitle(), viewHelper.getContentTitle(docMap));
                        docMap.put(fessConfig.getResponseFieldContentDescription(), viewHelper.getContentDescription(docMap));
                        docMap.put(fessConfig.getResponseFieldUrlLink(), viewHelper.getUrlLink(docMap));
                        docMap.put(fessConfig.getResponseFieldSitePath(), viewHelper.getSitePath(docMap));
                    }

                    if (!docMap.containsKey(Constants.SCORE)) {
                        docMap.put(Constants.SCORE, hit.getScore());
                    }

                    docMap.put(fessConfig.getIndexFieldId(), hit.getId());
                    docMap.put(fessConfig.getIndexFieldVersion(), hit.getVersion());
                    docMap.put(fessConfig.getIndexFieldSeqNo(), hit.getSeqNo());
                    docMap.put(fessConfig.getIndexFieldPrimaryTerm(), hit.getPrimaryTerm());
                    return docMap;
                }, cursor);
    }

    public long deleteByQuery(final HttpServletRequest request, final SearchRequestParams params) {
        final String query = ComponentUtil.getQueryStringBuilder().params(params).build();

        final QueryContext queryContext = ComponentUtil.getQueryHelper().build(params.getType(), query, context -> {
            context.skipRoleQuery();
        });
        return ComponentUtil.getFessEsClient().deleteByQuery(ComponentUtil.getFessConfig().getIndexDocumentUpdateIndex(),
                queryContext.getQueryBuilder());
    }

    public String[] getLanguages(final HttpServletRequest request, final SearchRequestParams params) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        if (params.getLanguages() != null) {
            final Set<String> langSet = new HashSet<>();
            for (final String lang : params.getLanguages()) {
                if (StringUtil.isNotBlank(lang) && lang.length() < 1000) {
                    if (Constants.ALL_LANGUAGES.equalsIgnoreCase(lang)) {
                        langSet.add(Constants.ALL_LANGUAGES);
                    } else {
                        final String normalizeLang = systemHelper.normalizeLang(lang);
                        if (normalizeLang != null) {
                            langSet.add(normalizeLang);
                        }
                    }
                }
            }
            if (langSet.size() > 1 && langSet.contains(Constants.ALL_LANGUAGES)) {
                return new String[] { Constants.ALL_LANGUAGES };
            } else {
                langSet.remove(Constants.ALL_LANGUAGES);
            }
            return langSet.toArray(new String[langSet.size()]);
        } else if (ComponentUtil.getFessConfig().isBrowserLocaleForSearchUsed()) {
            final Set<String> langSet = new HashSet<>();
            final Enumeration<Locale> locales = request.getLocales();
            if (locales != null) {
                while (locales.hasMoreElements()) {
                    final Locale locale = locales.nextElement();
                    final String normalizeLang = systemHelper.normalizeLang(locale.toString());
                    if (normalizeLang != null) {
                        langSet.add(normalizeLang);
                    }
                }
                if (!langSet.isEmpty()) {
                    return langSet.toArray(new String[langSet.size()]);
                }
            }
        }
        return StringUtil.EMPTY_STRINGS;
    }

    public OptionalEntity<Map<String, Object>> getDocumentByDocId(final String docId, final String[] fields,
            final OptionalThing<FessUserBean> userBean) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return ComponentUtil.getFessEsClient().getDocument(
                fessConfig.getIndexDocumentSearchIndex(),
                builder -> {
                    final BoolQueryBuilder boolQuery =
                            QueryBuilders.boolQuery().must(QueryBuilders.termQuery(fessConfig.getIndexFieldDocId(), docId));
                    final Set<String> roleSet = ComponentUtil.getRoleQueryHelper().build(SearchRequestType.JSON); // TODO SearchRequestType?
                    final QueryHelper queryHelper = ComponentUtil.getQueryHelper();
                    if (!roleSet.isEmpty()) {
                        queryHelper.buildRoleQuery(roleSet, boolQuery);
                    }
                    builder.setQuery(boolQuery);
                    builder.setFetchSource(fields, null);
                    queryHelper.processSearchPreference(builder, userBean, docId);
                    return true;
                });

    }

    public List<Map<String, Object>> getDocumentListByDocIds(final String[] docIds, final String[] fields,
            final OptionalThing<FessUserBean> userBean, final SearchRequestType searchRequestType) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return ComponentUtil.getFessEsClient().getDocumentList(
                fessConfig.getIndexDocumentSearchIndex(),
                builder -> {
                    final BoolQueryBuilder boolQuery =
                            QueryBuilders.boolQuery().must(QueryBuilders.termsQuery(fessConfig.getIndexFieldDocId(), docIds));
                    final QueryHelper queryHelper = ComponentUtil.getQueryHelper();
                    if (searchRequestType != SearchRequestType.ADMIN_SEARCH) {
                        final Set<String> roleSet = ComponentUtil.getRoleQueryHelper().build(searchRequestType);
                        if (!roleSet.isEmpty()) {
                            queryHelper.buildRoleQuery(roleSet, boolQuery);
                        }
                    }
                    builder.setQuery(boolQuery);
                    builder.setSize(fessConfig.getPagingSearchPageMaxSizeAsInteger().intValue());
                    builder.setFetchSource(fields, null);
                    queryHelper.processSearchPreference(builder, userBean, String.join(StringUtil.EMPTY, docIds));
                    return true;
                });
    }

    public boolean update(final String id, final String field, final Object value) {
        return ComponentUtil.getFessEsClient().update(ComponentUtil.getFessConfig().getIndexDocumentUpdateIndex(), id, field, value);
    }

    public boolean update(final String id, final Consumer<UpdateRequestBuilder> builderLambda) {
        try {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final UpdateRequestBuilder builder =
                    ComponentUtil.getFessEsClient().prepareUpdate().setIndex(fessConfig.getIndexDocumentUpdateIndex()).setId(id);
            builderLambda.accept(builder);
            final UpdateResponse response = builder.execute().actionGet(fessConfig.getIndexIndexTimeout());
            return response.getResult() == Result.CREATED || response.getResult() == Result.UPDATED;
        } catch (final ElasticsearchException e) {
            throw new FessEsClientException("Failed to update doc  " + id, e);
        }
    }

    public boolean bulkUpdate(final Consumer<BulkRequestBuilder> consumer) {
        final BulkRequestBuilder builder = ComponentUtil.getFessEsClient().prepareBulk();
        consumer.accept(builder);
        try {
            final BulkResponse response = builder.execute().get();
            if (response.hasFailures()) {
                throw new FessEsClientException(response.buildFailureMessage());
            } else {
                return true;
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new FessEsClientException("Failed to update bulk data.", e);
        }
    }
}
