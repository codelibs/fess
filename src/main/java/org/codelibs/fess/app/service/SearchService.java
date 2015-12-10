/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
import java.time.LocalDateTime;
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

import org.apache.commons.lang3.StringUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.es.client.FessEsClient.SearchConditionBuilder;
import org.codelibs.fess.es.client.FessEsClientException;
import org.codelibs.fess.es.log.exentity.SearchLog;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.helper.SearchLogHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.UserInfoHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.QueryResponseList;
import org.codelibs.fess.util.QueryStringBuilder;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.util.DfTypeUtil;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

public class SearchService {

    // ===================================================================================
    //                                                                            Constant
    //

    // ===================================================================================
    //                                                                           Attribute
    //
    @Resource
    protected DynamicProperties crawlerProperties;

    @Resource
    protected FessEsClient fessEsClient;

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected FessConfig fessConfig;

    @Resource
    protected QueryHelper queryHelper;

    @Resource
    protected UserInfoHelper userInfoHelper;

    // ===================================================================================
    //                                                                              Method
    //                                                                      ==============

    public void search(final HttpServletRequest request, final SearchRequestParams params, final SearchRenderData data) {
        final long requestedTime = systemHelper.getCurrentTimeAsLong();

        final long startTime = System.currentTimeMillis();
        final boolean searchLogSupport =
                Constants.TRUE.equals(crawlerProperties.getProperty(Constants.SEARCH_LOG_PROPERTY, Constants.TRUE));

        final String query =
                QueryStringBuilder.query(params.getQuery()).extraQueries(params.getExtraQueries()).fields(params.getFields()).build();

        final int pageStart = params.getStartPosition();
        final int pageSize = params.getPageSize();
        final String sortField = params.getSort();
        final List<Map<String, Object>> documentItems =
                fessEsClient.search(
                        fessConfig.getIndexDocumentIndex(),
                        fessConfig.getIndexDocumentType(),
                        searchRequestBuilder -> {
                            if (StringUtil.isNotBlank(sortField)) {
                                String[] sort = sortField.split("\\.");
                                SortBuilder sortBuilder = SortBuilders.fieldSort(sort[0]);
                                if ("asc".equals(sort[1])) {
                                    sortBuilder.order(SortOrder.ASC);
                                } else if ("desc".equals(sort[1])) {
                                    sortBuilder.order(SortOrder.DESC);
                                }
                                searchRequestBuilder.addSort(sortBuilder);
                            }
                            return SearchConditionBuilder.builder(searchRequestBuilder).query(query).offset(pageStart).size(pageSize)
                                    .facetInfo(params.getFacetInfo()).geoInfo(params.getGeoInfo())
                                    .responseFields(queryHelper.getResponseFields()).administrativeAccess(params.isAdministrativeAccess())
                                    .build();
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
        final Set<String> highlightQueries = (Set<String>) request.getAttribute(Constants.HIGHLIGHT_QUERIES);
        if (highlightQueries != null) {
            final StringBuilder buf = new StringBuilder(100);
            highlightQueries.stream().forEach(q -> {
                buf.append("&hq=").append(q);
            });
            data.setAppendHighlightParams(buf.toString());
        }

        queryResponseList.setExecTime(System.currentTimeMillis() - startTime);
        final NumberFormat nf = NumberFormat.getInstance(request.getLocale());
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
            storeSearchLog(request, DfTypeUtil.toLocalDateTime(requestedTime), queryId, query, pageStart, pageSize, queryResponseList);
        }
    }

    public int deleteByQuery(final HttpServletRequest request, final SearchRequestParams params) {

        final String query =
                QueryStringBuilder.query(params.getQuery()).extraQueries(params.getExtraQueries()).fields(params.getFields()).build();

        final QueryContext queryContext = queryHelper.build(query, context -> {
            context.skipRoleQuery();
        });
        return fessEsClient.deleteByQuery(fessConfig.getIndexDocumentIndex(), fessConfig.getIndexDocumentType(),
                queryContext.getQueryBuilder());
    }

    protected void storeSearchLog(final HttpServletRequest request, final LocalDateTime requestedTime, final String queryId,
            final String query, final int pageStart, final int pageSize, final QueryResponseList queryResponseList) {

        final SearchLogHelper searchLogHelper = ComponentUtil.getSearchLogHelper();
        final SearchLog searchLog = new SearchLog();

        if (Constants.TRUE.equals(crawlerProperties.getProperty(Constants.USER_INFO_PROPERTY, Constants.TRUE))) {
            final String userCode = userInfoHelper.getUserCode();
            if (userCode != null) {
                searchLog.setUserSessionId(userCode);
            }
        }

        searchLog.setQueryId(queryId);
        searchLog.setHitCount(queryResponseList.getAllRecordCount());
        searchLog.setResponseTime(queryResponseList.getExecTime());
        searchLog.setQueryTime(queryResponseList.getQueryTime());
        searchLog.setSearchWord(StringUtils.abbreviate(query, 1000));
        searchLog.setSearchQuery(StringUtils.abbreviate(queryResponseList.getSearchQuery(), 1000));
        searchLog.setRequestedAt(requestedTime);
        searchLog.setQueryOffset(pageStart);
        searchLog.setQueryPageSize(pageSize);
        ComponentUtil.getComponent(FessLoginAssist.class).getSessionUserBean().ifPresent(user -> {
            searchLog.setUser(user.getUserId());
        });

        searchLog.setClientIp(StringUtils.abbreviate(request.getRemoteAddr(), 50));
        searchLog.setReferer(StringUtils.abbreviate(request.getHeader("referer"), 1000));
        searchLog.setUserAgent(StringUtils.abbreviate(request.getHeader("user-agent"), 255));
        final Object accessType = request.getAttribute(Constants.SEARCH_LOG_ACCESS_TYPE);
        if (Constants.SEARCH_LOG_ACCESS_TYPE_JSON.equals(accessType)) {
            searchLog.setAccessType(Constants.SEARCH_LOG_ACCESS_TYPE_JSON);
        } else if (Constants.SEARCH_LOG_ACCESS_TYPE_XML.equals(accessType)) {
            searchLog.setAccessType(Constants.SEARCH_LOG_ACCESS_TYPE_XML);
        } else if (Constants.SEARCH_LOG_ACCESS_TYPE_OTHER.equals(accessType)) {
            searchLog.setAccessType(Constants.SEARCH_LOG_ACCESS_TYPE_OTHER);
        } else {
            searchLog.setAccessType(Constants.SEARCH_LOG_ACCESS_TYPE_WEB);
        }

        @SuppressWarnings("unchecked")
        final Map<String, List<String>> fieldLogMap = (Map<String, List<String>>) request.getAttribute(Constants.FIELD_LOGS);
        if (fieldLogMap != null) {
            for (final Map.Entry<String, List<String>> logEntry : fieldLogMap.entrySet()) {
                for (final String value : logEntry.getValue()) {
                    searchLog.addSearchFieldLogValue(logEntry.getKey(), StringUtils.abbreviate(value, 1000));
                }
            }
        }

        searchLogHelper.addSearchLog(searchLog);
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
        } else if (Constants.TRUE.equals(crawlerProperties.getProperty(Constants.USE_BROWSER_LOCALE_FOR_SEARCH_PROPERTY, Constants.FALSE))) {
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

    public OptionalEntity<Map<String, Object>> getDocumentByDocId(final String docId, final String[] fields) {
        return fessEsClient.getDocument(fessConfig.getIndexDocumentIndex(), fessConfig.getIndexDocumentType(), builder -> {
            builder.setQuery(QueryBuilders.termQuery(fessConfig.getIndexFieldDocId(), docId));
            builder.addFields(fields);
            return true;
        });
    }

    public List<Map<String, Object>> getDocumentListByDocIds(final String[] docIds, final String[] fields) {
        return fessEsClient.getDocumentList(fessConfig.getIndexDocumentIndex(), fessConfig.getIndexDocumentType(), builder -> {
            builder.setQuery(QueryBuilders.termsQuery(fessConfig.getIndexFieldDocId(), docIds));
            builder.setSize(fessConfig.getPagingSearchPageMaxSizeAsInteger().intValue());
            builder.addFields(fields);
            return true;
        });
    }

    public boolean update(final String id, final String field, final Object value) {
        return fessEsClient.update(fessConfig.getIndexDocumentIndex(), fessConfig.getIndexDocumentType(), id, field, value);
    }

    public boolean update(final String id, final Consumer<UpdateRequestBuilder> builderLambda) {
        try {
            final UpdateRequestBuilder builder =
                    fessEsClient.prepareUpdate(fessConfig.getIndexDocumentIndex(), fessConfig.getIndexDocumentType(), id);
            builderLambda.accept(builder);
            final UpdateResponse response = builder.execute().actionGet();
            return response.isCreated();
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
