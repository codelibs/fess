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
package org.codelibs.fess.api.json;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.BaseJsonApiManager;
import org.codelibs.fess.app.service.FavoriteLogService;
import org.codelibs.fess.app.service.SearchService;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.PingResponse;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.exception.WebApiException;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.PopularWordHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.UserInfoHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.codelibs.fess.util.FacetResponse;
import org.codelibs.fess.util.FacetResponse.Field;
import org.dbflute.optional.OptionalThing;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonApiManager extends BaseJsonApiManager {

    private static final Logger logger = LoggerFactory.getLogger(JsonApiManager.class);

    public JsonApiManager() {
        setPathPrefix("/json");
    }

    @Override
    public boolean matches(final HttpServletRequest request) {
        if (!ComponentUtil.getFessConfig().isWebApiJson()) {
            return false;
        }

        final String servletPath = request.getServletPath();
        return servletPath.startsWith(pathPrefix);
    }

    @Override
    public void process(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException,
            ServletException {
        final String formatType = request.getParameter("type");
        switch (getFormatType(formatType)) {
        case SEARCH:
            processSearchRequest(request, response, chain);
            break;
        case LABEL:
            processLabelRequest(request, response, chain);
            break;
        case POPULARWORD:
            processPopularWordRequest(request, response, chain);
            break;
        case FAVORITE:
            processFavoriteRequest(request, response, chain);
            break;
        case FAVORITES:
            processFavoritesRequest(request, response, chain);
            break;
        case PING:
            processPingRequest(request, response, chain);
            break;
        default:
            writeJsonResponse(99, StringUtil.EMPTY, "Not found.");
            break;
        }
    }

    protected void processPingRequest(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) {
        final FessEsClient fessEsClient = ComponentUtil.getFessEsClient();
        int status;
        Exception err = null;
        try {
            final PingResponse pingResponse = fessEsClient.ping();
            status = pingResponse.getStatus();
            writeJsonResponse(status, "\"message\":" + pingResponse.getMessage());
        } catch (final Exception e) {
            status = 9;
            err = e;
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a ping request.", e);
            }
            writeJsonResponse(status, null, err);
        }
    }

    protected void processSearchRequest(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) {
        final SearchService searchService = ComponentUtil.getComponent(SearchService.class);
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        int status = 0;
        Exception err = null;
        String query = null;
        final StringBuilder buf = new StringBuilder(1000); // TODO replace response stream
        request.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE, Constants.SEARCH_LOG_ACCESS_TYPE_JSON);
        try {
            final SearchRenderData data = new SearchRenderData();
            final JsonRequestParams params = new JsonRequestParams(request, fessConfig);
            query = params.getQuery();
            request.setAttribute(Constants.REQUEST_QUERIES, query);
            searchService.search(params, data, OptionalThing.empty());
            final String execTime = data.getExecTime();
            final String queryTime = Long.toString(data.getQueryTime());
            final String pageSize = Integer.toString(data.getPageSize());
            final String currentPageNumber = Integer.toString(data.getCurrentPageNumber());
            final String allRecordCount = Long.toString(data.getAllRecordCount());
            final String allPageCount = Integer.toString(data.getAllPageCount());
            final List<Map<String, Object>> documentItems = data.getDocumentItems();
            final FacetResponse facetResponse = data.getFacetResponse();
            final GeoInfo geoInfo = params.getGeoInfo();
            final String queryId = data.getQueryId();

            buf.append("\"q\":");
            buf.append(escapeJson(query));
            buf.append(",\"query_id\":");
            buf.append(escapeJson(queryId));
            buf.append(",\"exec_time\":");
            buf.append(execTime);
            buf.append(",\"query_time\":");
            buf.append(queryTime);
            buf.append(',');
            buf.append("\"page_size\":");
            buf.append(pageSize);
            buf.append(',');
            buf.append("\"page_number\":");
            buf.append(currentPageNumber);
            buf.append(',');
            buf.append("\"record_count\":");
            buf.append(allRecordCount);
            buf.append(',');
            buf.append("\"page_count\":");
            buf.append(allPageCount);
            if (!documentItems.isEmpty()) {
                buf.append(',');
                buf.append("\"result\":[");
                boolean first1 = true;
                for (final Map<String, Object> document : documentItems) {
                    if (!first1) {
                        buf.append(',');
                    } else {
                        first1 = false;
                    }
                    buf.append('{');
                    boolean first2 = true;
                    for (final Map.Entry<String, Object> entry : document.entrySet()) {
                        final String name = entry.getKey();
                        if (StringUtil.isNotBlank(name) && entry.getValue() != null
                                && ComponentUtil.getQueryHelper().isApiResponseField(name)) {
                            if (!first2) {
                                buf.append(',');
                            } else {
                                first2 = false;
                            }
                            buf.append(escapeJson(name));
                            buf.append(':');
                            buf.append(escapeJson(entry.getValue()));
                        }
                    }
                    buf.append('}');
                }
                buf.append(']');
            }
            if (facetResponse != null && facetResponse.hasFacetResponse()) {
                // facet field
                if (facetResponse.getFieldList() != null) {
                    buf.append(',');
                    buf.append("\"facet_field\":[");
                    boolean first1 = true;
                    for (final Field field : facetResponse.getFieldList()) {
                        if (!first1) {
                            buf.append(',');
                        } else {
                            first1 = false;
                        }
                        buf.append("{\"name\":");
                        buf.append(escapeJson(field.getName()));
                        buf.append(",\"result\":[");
                        boolean first2 = true;
                        for (final Map.Entry<String, Long> entry : field.getValueCountMap().entrySet()) {
                            if (!first2) {
                                buf.append(',');
                            } else {
                                first2 = false;
                            }
                            buf.append("{\"value\":");
                            buf.append(escapeJson(entry.getKey()));
                            buf.append(",\"count\":");
                            buf.append(entry.getValue());
                            buf.append('}');
                        }
                        buf.append(']');
                        buf.append('}');
                    }
                    buf.append(']');
                }
                // facet q
                if (facetResponse.getQueryCountMap() != null) {
                    buf.append(',');
                    buf.append("\"facet_query\":[");
                    boolean first1 = true;
                    for (final Map.Entry<String, Long> entry : facetResponse.getQueryCountMap().entrySet()) {
                        if (!first1) {
                            buf.append(',');
                        } else {
                            first1 = false;
                        }
                        buf.append("{\"value\":");
                        buf.append(escapeJson(entry.getKey()));
                        buf.append(",\"count\":");
                        buf.append(entry.getValue());
                        buf.append('}');
                    }
                    buf.append(']');
                }
                if (geoInfo != null && geoInfo.toQueryBuilder() != null) {
                    buf.append(',');
                    buf.append("\"geo\":");
                    buf.append(toGeoRequestString(geoInfo));
                }
            }
        } catch (final Exception e) {
            status = 1;
            err = e;
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a search request.", e);
            }
        }

        writeJsonResponse(status, buf.toString(), err);

    }

    protected String toGeoRequestString(final GeoInfo geoInfo) {
        try {
            final XContentBuilder builder = XContentFactory.jsonBuilder();
            return geoInfo.toQueryBuilder().toXContent(builder, ToXContent.EMPTY_PARAMS).string();
        } catch (final Exception e) {
            return "{\"error\":\"" + detailedMessage(e) + "\"}";
        }
    }

    protected String detailedMessage(Throwable t) {
        if (t == null) {
            return "Unknown";
        }
        if (t.getCause() != null) {
            final StringBuilder sb = new StringBuilder();
            while (t != null) {
                sb.append(t.getClass().getSimpleName());
                if (t.getMessage() != null) {
                    sb.append("[");
                    sb.append(t.getMessage());
                    sb.append("]");
                }
                sb.append("; ");
                t = t.getCause();
                if (t != null) {
                    sb.append("nested: ");
                }
            }
            return sb.toString();
        } else {
            return t.getClass().getSimpleName() + "[" + t.getMessage() + "]";
        }
    }

    protected void processLabelRequest(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) {
        final LabelTypeHelper labelTypeHelper = ComponentUtil.getLabelTypeHelper();

        int status = 0;
        Exception err = null;
        final StringBuilder buf = new StringBuilder(255); // TODO replace response stream
        try {
            final List<Map<String, String>> labelTypeItems = labelTypeHelper.getLabelTypeItemList(SearchRequestType.JSON);
            buf.append("\"record_count\":");
            buf.append(labelTypeItems.size());
            if (!labelTypeItems.isEmpty()) {
                buf.append(',');
                buf.append("\"result\":[");
                boolean first1 = true;
                for (final Map<String, String> labelMap : labelTypeItems) {
                    if (!first1) {
                        buf.append(',');
                    } else {
                        first1 = false;
                    }
                    buf.append("{\"label\":");
                    buf.append(escapeJson(labelMap.get(Constants.ITEM_LABEL)));
                    buf.append(", \"value\":");
                    buf.append(escapeJson(labelMap.get(Constants.ITEM_VALUE)));
                    buf.append('}');
                }
                buf.append(']');
            }
        } catch (final Exception e) {
            status = 1;
            err = e;
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a label request.", e);
            }
        }

        writeJsonResponse(status, buf.toString(), err);

    }

    protected void processPopularWordRequest(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) {
        if (!ComponentUtil.getFessConfig().isWebApiPopularWord()) {
            writeJsonResponse(9, null, "Unsupported operation.");
            return;
        }

        final String seed = request.getParameter("seed");
        final String[] tags = request.getParameterValues("labels");
        final String[] fields = request.getParameterValues("fields");
        final String[] excludes = StringUtil.EMPTY_STRINGS;// TODO

        final PopularWordHelper popularWordHelper = ComponentUtil.getPopularWordHelper();

        int status = 0;
        Exception err = null;
        final StringBuilder buf = new StringBuilder(255); // TODO replace response stream
        try {
            final List<String> popularWordList = popularWordHelper.getWordList(SearchRequestType.JSON, seed, tags, null, fields, excludes);

            buf.append("\"result\":[");
            boolean first1 = true;
            for (final String word : popularWordList) {
                if (!first1) {
                    buf.append(',');
                } else {
                    first1 = false;
                }
                buf.append(escapeJson(word));
            }
            buf.append(']');
        } catch (final Exception e) {
            if (e instanceof WebApiException) {
                status = ((WebApiException) e).getStatusCode();
            } else {
                status = 1;
            }
            err = e;
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a popularWord request.", e);
            }
        }

        writeJsonResponse(status, buf.toString(), err);

    }

    protected void processFavoriteRequest(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) {
        if (!ComponentUtil.getFessConfig().isUserFavorite()) {
            writeJsonResponse(9, null, "Unsupported operation.");
            return;
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final UserInfoHelper userInfoHelper = ComponentUtil.getUserInfoHelper();
        final SearchService searchService = ComponentUtil.getComponent(SearchService.class);
        final FavoriteLogService favoriteLogService = ComponentUtil.getComponent(FavoriteLogService.class);
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();

        try {
            final String docId = request.getParameter("docId");
            final String queryId = request.getParameter("queryId");

            final String[] docIds = userInfoHelper.getResultDocIds(URLDecoder.decode(queryId, Constants.UTF_8));
            if (docIds == null) {
                throw new WebApiException(6, "No searched urls.");
            }

            searchService.getDocumentByDocId(docId, new String[] { fessConfig.getIndexFieldUrl() }, OptionalThing.empty())
                    .ifPresent(doc -> {
                        final String favoriteUrl = DocumentUtil.getValue(doc, fessConfig.getIndexFieldUrl(), String.class);
                        final String userCode = userInfoHelper.getUserCode();

                        if (StringUtil.isBlank(userCode)) {
                            throw new WebApiException(2, "No user session.");
                        } else if (StringUtil.isBlank(favoriteUrl)) {
                            throw new WebApiException(2, "URL is null.");
                        }

                        boolean found = false;
                        for (final String id : docIds) {
                            if (docId.equals(id)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            throw new WebApiException(5, "Not found: " + favoriteUrl);
                        }

                        if (!favoriteLogService.addUrl(userCode, (userInfo, favoriteLog) -> {
                            favoriteLog.setUserInfoId(userInfo.getId());
                            favoriteLog.setUrl(favoriteUrl);
                            favoriteLog.setDocId(docId);
                            favoriteLog.setQueryId(queryId);
                            favoriteLog.setCreatedAt(systemHelper.getCurrentTimeAsLocalDateTime());
                        })) {
                            throw new WebApiException(4, "Failed to add url: " + favoriteUrl);
                        }

                        final String id = DocumentUtil.getValue(doc, fessConfig.getIndexFieldId(), String.class);
                        searchService.update(id, builder -> {
                            final Script script = new Script("ctx._source." + fessConfig.getIndexFieldFavoriteCount() + "+=1");
                            builder.setScript(script);
                            final Map<String, Object> upsertMap = new HashMap<>();
                            upsertMap.put(fessConfig.getIndexFieldFavoriteCount(), 1);
                            builder.setUpsert(upsertMap);
                            builder.setRefreshPolicy(Constants.TRUE);
                        });

                        writeJsonResponse(0, "\"result\":\"ok\"", (String) null);

                    }).orElse(() -> {
                        throw new WebApiException(6, "Not found: " + docId);
                    });

        } catch (final Exception e) {
            int status;
            if (e instanceof WebApiException) {
                status = ((WebApiException) e).getStatusCode();
            } else {
                status = 1;
            }
            writeJsonResponse(status, null, e);
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a favorite request.", e);
            }
        }

    }

    protected void processFavoritesRequest(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) {
        if (!ComponentUtil.getFessConfig().isUserFavorite()) {
            writeJsonResponse(9, null, "Unsupported operation.");
            return;
        }

        final UserInfoHelper userInfoHelper = ComponentUtil.getUserInfoHelper();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final SearchService searchService = ComponentUtil.getComponent(SearchService.class);
        final FavoriteLogService favoriteLogService = ComponentUtil.getComponent(FavoriteLogService.class);

        int status = 0;
        String body = null;
        Exception err = null;

        try {
            final String queryId = request.getParameter("queryId");
            final String userCode = userInfoHelper.getUserCode();

            if (StringUtil.isBlank(userCode)) {
                throw new WebApiException(2, "No user session.");
            } else if (StringUtil.isBlank(queryId)) {
                throw new WebApiException(3, "Query ID is null.");
            }

            final String[] docIds = userInfoHelper.getResultDocIds(queryId);
            final List<Map<String, Object>> docList =
                    searchService.getDocumentListByDocIds(
                            docIds,
                            new String[] { fessConfig.getIndexFieldUrl(), fessConfig.getIndexFieldDocId(),
                                    fessConfig.getIndexFieldFavoriteCount() }, OptionalThing.empty(), SearchRequestType.JSON);
            List<String> urlList = new ArrayList<>(docList.size());
            for (final Map<String, Object> doc : docList) {
                final String urlObj = DocumentUtil.getValue(doc, fessConfig.getIndexFieldUrl(), String.class);
                if (urlObj != null) {
                    urlList.add(urlObj);
                }
            }
            urlList = favoriteLogService.getUrlList(userCode, urlList);
            final List<String> docIdList = new ArrayList<>(urlList.size());
            for (final Map<String, Object> doc : docList) {
                final String urlObj = DocumentUtil.getValue(doc, fessConfig.getIndexFieldUrl(), String.class);
                if (urlObj != null && urlList.contains(urlObj)) {
                    final String docIdObj = DocumentUtil.getValue(doc, fessConfig.getIndexFieldDocId(), String.class);
                    if (docIdObj != null) {
                        docIdList.add(docIdObj);
                    }
                }
            }

            final StringBuilder buf = new StringBuilder(255); // TODO replace response stream
            buf.append("\"num\":").append(docIdList.size());
            if (!docIdList.isEmpty()) {
                buf.append(", \"doc_ids\":[");
                for (int i = 0; i < docIdList.size(); i++) {
                    if (i > 0) {
                        buf.append(',');
                    }
                    buf.append(escapeJson(docIdList.get(i)));
                }
                buf.append(']');
            }
            body = buf.toString();
        } catch (final Exception e) {
            if (e instanceof WebApiException) {
                status = ((WebApiException) e).getStatusCode();
            } else {
                status = 1;
            }

            err = e;
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a favorites request.", e);
            }
        }

        writeJsonResponse(status, body, err);

    }

    protected static class JsonRequestParams implements SearchRequestParams {

        private final HttpServletRequest request;

        private final FessConfig fessConfig;

        private int startPosition = -1;

        private int pageSize = -1;

        protected JsonRequestParams(final HttpServletRequest request, final FessConfig fessConfig) {
            this.request = request;
            this.fessConfig = fessConfig;
        }

        @Override
        public String getQuery() {
            return request.getParameter("q");
        }

        @Override
        public String[] getExtraQueries() {
            return getParamValueArray(request, "ex_q");
        }

        @Override
        public Map<String, String[]> getFields() {
            final Map<String, String[]> fields = new HashMap<>();
            for (final Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                final String key = entry.getKey();
                if (key.startsWith("fields.")) {
                    final String[] value = simplifyArray(entry.getValue());
                    fields.put(key.substring("fields.".length()), value);
                }
            }
            return fields;
        }

        @Override
        public String[] getLanguages() {
            return getParamValueArray(request, "lang");
        }

        @Override
        public GeoInfo getGeoInfo() {
            return createGeoInfo(request);
        }

        @Override
        public FacetInfo getFacetInfo() {
            return createFacetInfo(request);
        }

        @Override
        public String getSort() {
            return request.getParameter("sort");
        }

        @Override
        public int getStartPosition() {
            if (startPosition != -1) {
                return startPosition;
            }

            final String start = request.getParameter("start");
            if (StringUtil.isBlank(start)) {
                startPosition = fessConfig.getPagingSearchPageStartAsInteger();
            } else {
                try {
                    startPosition = Integer.parseInt(start);
                } catch (final NumberFormatException e) {
                    startPosition = fessConfig.getPagingSearchPageStartAsInteger();
                }
            }
            return startPosition;
        }

        @Override
        public int getPageSize() {
            if (pageSize != -1) {
                return pageSize;
            }

            final String num = request.getParameter("num");
            if (StringUtil.isBlank(num)) {
                pageSize = fessConfig.getPagingSearchPageSizeAsInteger();
            } else {
                try {
                    pageSize = Integer.parseInt(num);
                    if (pageSize > fessConfig.getPagingSearchPageMaxSizeAsInteger().intValue() || pageSize <= 0) {
                        pageSize = fessConfig.getPagingSearchPageMaxSizeAsInteger();
                    }
                } catch (final NumberFormatException e) {
                    pageSize = fessConfig.getPagingSearchPageSizeAsInteger();
                }
            }
            return pageSize;
        }

        @Override
        public Object getAttribute(final String name) {
            return request.getAttribute(name);
        }

        @Override
        public Locale getLocale() {
            return Locale.ROOT;
        }

        @Override
        public SearchRequestType getType() {
            return SearchRequestType.JSON;
        }

    }
}
