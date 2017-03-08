/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.service;

import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.es.client.FessEsClient.SearchConditionBuilder;
import org.codelibs.fess.es.client.FessEsClientException;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.QueryResponseList;
import org.codelibs.fess.util.QueryStringBuilder;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.dbflute.util.DfTypeUtil;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.lastaflute.taglib.function.LaFunctions;

public class SearchService {

    // ===================================================================================
    //                                                                            Constant
    //

    // ===================================================================================
    //                                                                           Attribute
    //

    @Resource
    protected FessEsClient fessEsClient;

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected FessConfig fessConfig;

    @Resource
    protected QueryHelper queryHelper;

    // ===================================================================================
    //                                                                              Method
    //                                                                      ==============

    public void search(final SearchRequestParams params, final SearchRenderData data, final OptionalThing<FessUserBean> userBean) {
        final long requestedTime = systemHelper.getCurrentTimeAsLong();

        final long startTime = System.currentTimeMillis();
        final boolean searchLogSupport = fessConfig.isSearchLog();

        final String query =
                QueryStringBuilder.query(params.getQuery()).extraQueries(params.getExtraQueries()).fields(params.getFields()).build();

        final int pageStart = params.getStartPosition();
        final int pageSize = params.getPageSize();
        final String sortField = params.getSort();
        final List<Map<String, Object>> documentItems =
                fessEsClient.search(
                        fessConfig.getIndexDocumentSearchIndex(),
                        fessConfig.getIndexDocumentType(),
                        searchRequestBuilder -> {
                            fessConfig.processSearchPreference(searchRequestBuilder, userBean);
                            return SearchConditionBuilder.builder(searchRequestBuilder)
                                    .query(StringUtil.isBlank(sortField) ? query : query + " sort:" + sortField).offset(pageStart)
                                    .size(pageSize).facetInfo(params.getFacetInfo()).geoInfo(params.getGeoInfo())
                                    .responseFields(queryHelper.getResponseFields()).searchRequestType(params.getType()).build();
                        }, (searchRequestBuilder, execTime, searchResponse) -> {
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
        if (searchLogSupport) {
            ComponentUtil.getSearchLogHelper().addSearchLog(params, DfTypeUtil.toLocalDateTime(requestedTime), queryId, query, pageStart,
                    pageSize, queryResponseList);
        }
    }

    public int deleteByQuery(final HttpServletRequest request, final SearchRequestParams params) {

        final String query =
                QueryStringBuilder.query(params.getQuery()).extraQueries(params.getExtraQueries()).fields(params.getFields()).build();

        final QueryContext queryContext = queryHelper.build(params.getType(), query, context -> {
            context.skipRoleQuery();
        });
        return fessEsClient.deleteByQuery(fessConfig.getIndexDocumentUpdateIndex(), fessConfig.getIndexDocumentType(),
                queryContext.getQueryBuilder());
    }

    public String[] getLanguages(final HttpServletRequest request, final SearchRequestParams params) {
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
        } else if (fessConfig.isBrowserLocaleForSearchUsed()) {
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
        return fessEsClient.getDocument(
                fessConfig.getIndexDocumentSearchIndex(),
                fessConfig.getIndexDocumentType(),
                builder -> {
                    final BoolQueryBuilder boolQuery =
                            QueryBuilders.boolQuery().must(QueryBuilders.termQuery(fessConfig.getIndexFieldDocId(), docId));
                    final Set<String> roleSet = ComponentUtil.getRoleQueryHelper().build(SearchRequestType.JSON); // TODO SearchRequestType?
                    if (!roleSet.isEmpty()) {
                        final BoolQueryBuilder roleQuery = QueryBuilders.boolQuery();
                        roleSet.stream().forEach(name -> {
                            roleQuery.should(QueryBuilders.termQuery(fessConfig.getIndexFieldRole(), name));
                        });
                        boolQuery.filter(roleQuery);
                    }
                    builder.setQuery(boolQuery);
                    builder.setFetchSource(fields, null);
                    fessConfig.processSearchPreference(builder, userBean);
                    return true;
                });

    }

    public List<Map<String, Object>> getDocumentListByDocIds(final String[] docIds, final String[] fields,
            final OptionalThing<FessUserBean> userBean, final SearchRequestType searchRequestType) {
        return fessEsClient.getDocumentList(
                fessConfig.getIndexDocumentSearchIndex(),
                fessConfig.getIndexDocumentType(),
                builder -> {
                    final BoolQueryBuilder boolQuery =
                            QueryBuilders.boolQuery().must(QueryBuilders.termsQuery(fessConfig.getIndexFieldDocId(), docIds));
                    if (searchRequestType != SearchRequestType.ADMIN_SEARCH) {
                        final Set<String> roleSet = ComponentUtil.getRoleQueryHelper().build(searchRequestType);
                        if (!roleSet.isEmpty()) {
                            final BoolQueryBuilder roleQuery = QueryBuilders.boolQuery();
                            roleSet.stream().forEach(name -> {
                                roleQuery.should(QueryBuilders.termQuery(fessConfig.getIndexFieldRole(), name));
                            });
                            boolQuery.filter(roleQuery);
                        }
                    }
                    builder.setQuery(boolQuery);
                    builder.setSize(fessConfig.getPagingSearchPageMaxSizeAsInteger().intValue());
                    builder.setFetchSource(fields, null);
                    fessConfig.processSearchPreference(builder, userBean);
                    return true;
                });
    }

    public boolean update(final String id, final String field, final Object value) {
        return fessEsClient.update(fessConfig.getIndexDocumentUpdateIndex(), fessConfig.getIndexDocumentType(), id, field, value);
    }

    public boolean update(final String id, final Consumer<UpdateRequestBuilder> builderLambda) {
        try {
            final UpdateRequestBuilder builder =
                    fessEsClient.prepareUpdate(fessConfig.getIndexDocumentUpdateIndex(), fessConfig.getIndexDocumentType(), id);
            builderLambda.accept(builder);
            final UpdateResponse response = builder.execute().actionGet(fessConfig.getIndexIndexTimeout());
            return response.getResult() == Result.CREATED || response.getResult() == Result.UPDATED;
        } catch (final ElasticsearchException e) {
            throw new FessEsClientException("Failed to update doc  " + id, e);
        }
    }

    public boolean bulkUpdate(final Consumer<BulkRequestBuilder> consumer) {
        final BulkRequestBuilder builder = fessEsClient.prepareBulk();
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
