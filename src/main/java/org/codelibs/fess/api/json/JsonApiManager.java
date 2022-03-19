/*
 * Copyright 2012-2022 CodeLibs Project and the Others.
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.exception.IORuntimeException;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.BaseJsonApiManager;
import org.codelibs.fess.app.service.FavoriteLogService;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.PingResponse;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.es.client.SearchEngineClient;
import org.codelibs.fess.exception.WebApiException;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.PopularWordHelper;
import org.codelibs.fess.helper.RelatedContentHelper;
import org.codelibs.fess.helper.RelatedQueryHelper;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.UserInfoHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.codelibs.fess.util.FacetResponse;
import org.codelibs.fess.util.FacetResponse.Field;
import org.dbflute.optional.OptionalThing;
import org.opensearch.script.Script;

public class JsonApiManager extends BaseJsonApiManager {

    private static final Logger logger = LogManager.getLogger(JsonApiManager.class);

    public JsonApiManager() {
        setPathPrefix("/json");
    }

    @PostConstruct
    public void register() {
        if (logger.isInfoEnabled()) {
            logger.info("Load {}", this.getClass().getSimpleName());
        }
        ComponentUtil.getWebApiManagerFactory().add(this);
    }

    @Override
    public boolean matches(final HttpServletRequest request) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isWebApiJson()) {
            switch (getFormatType(request)) {
            case SEARCH:
            case LABEL:
            case POPULARWORD:
                return false;
            default:
                break;
            }
        }

        final String servletPath = request.getServletPath();
        return servletPath.startsWith(pathPrefix);
    }

    @Override
    public void process(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        switch (getFormatType(request)) {
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
        case SCROLL:
            processScrollSearchRequest(request, response, chain);
            break;
        default:
            writeJsonResponse(99, StringUtil.EMPTY, "Not found.");
            break;
        }
    }

    protected void processScrollSearchRequest(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain chain) {
        final SearchHelper searchHelper = ComponentUtil.getSearchHelper();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        if (!fessConfig.isAcceptedSearchReferer(request.getHeader("referer"))) {
            writeJsonResponse(99, StringUtil.EMPTY, "Referer is invalid.");
            return;
        }

        if (!fessConfig.isApiSearchScroll()) {
            writeJsonResponse(99, StringUtil.EMPTY, "Scroll Search is not available.");
            return;
        }

        final StringBuilder buf = new StringBuilder(1000);
        request.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE, Constants.SEARCH_LOG_ACCESS_TYPE_JSON);
        final JsonRequestParams params = new JsonRequestParams(request, fessConfig);
        try {
            response.setContentType("application/x-ndjson; charset=UTF-8");
            final long count = searchHelper.scrollSearch(params, doc -> {
                buf.setLength(0);
                buf.append('{');
                boolean first2 = true;
                for (final Map.Entry<String, Object> entry : doc.entrySet()) {
                    final String name = entry.getKey();
                    if (StringUtil.isNotBlank(name) && entry.getValue() != null) {
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
                buf.append('\n');
                try {
                    response.getWriter().print(buf.toString());
                } catch (final IOException e) {
                    throw new IORuntimeException(e);
                }
                return true;
            }, OptionalThing.empty());
            response.flushBuffer();
            if (logger.isDebugEnabled()) {
                logger.debug("Loaded {} docs", count);
            }
        } catch (final Exception e) {
            final int status = 9;
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a ping request.", e);
            }
            writeJsonResponse(status, null, e);
        }

    }

    protected void processPingRequest(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) {
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        int status;
        Exception err = null;
        try {
            final PingResponse pingResponse = searchEngineClient.ping();
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
        final SearchHelper searchHelper = ComponentUtil.getSearchHelper();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final RelatedQueryHelper relatedQueryHelper = ComponentUtil.getRelatedQueryHelper();
        final RelatedContentHelper relatedContentHelper = ComponentUtil.getRelatedContentHelper();

        int status = 0;
        Exception err = null;
        String query = null;
        final StringBuilder buf = new StringBuilder(1000); // TODO replace response stream
        request.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE, Constants.SEARCH_LOG_ACCESS_TYPE_JSON);
        try {
            final SearchRenderData data = new SearchRenderData();
            final JsonRequestParams params = new JsonRequestParams(request, fessConfig);
            query = params.getQuery();
            searchHelper.search(params, data, OptionalThing.empty());
            final String execTime = data.getExecTime();
            final String queryTime = Long.toString(data.getQueryTime());
            final String pageSize = Integer.toString(data.getPageSize());
            final String currentPageNumber = Integer.toString(data.getCurrentPageNumber());
            final String allRecordCount = Long.toString(data.getAllRecordCount());
            final String allRecordCountRelation = data.getAllRecordCountRelation();
            final String allPageCount = Integer.toString(data.getAllPageCount());
            final List<Map<String, Object>> documentItems = data.getDocumentItems();
            final FacetResponse facetResponse = data.getFacetResponse();
            final String queryId = data.getQueryId();
            final String highlightParams = data.getAppendHighlightParams();
            final boolean nextPage = data.isExistNextPage();
            final boolean prevPage = data.isExistPrevPage();
            final long startRecordNumber = data.getCurrentStartRecordNumber();
            final long endRecordNumber = data.getCurrentEndRecordNumber();
            final List<String> pageNumbers = data.getPageNumberList();
            final boolean partial = data.isPartialResults();
            final String searchQuery = data.getSearchQuery();
            final long requestedTime = data.getRequestedTime();

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
            buf.append("\"record_count_relation\":");
            buf.append(escapeJson(allRecordCountRelation));
            buf.append(',');
            buf.append("\"page_count\":");
            buf.append(allPageCount);
            buf.append(",\"highlight_params\":");
            buf.append(escapeJson(highlightParams));
            buf.append(",\"next_page\":");
            buf.append(escapeJson(nextPage));
            buf.append(",\"prev_page\":");
            buf.append(escapeJson(prevPage));
            buf.append(",\"start_record_number\":");
            buf.append(startRecordNumber);
            buf.append(",\"end_record_number\":");
            buf.append(escapeJson(endRecordNumber));
            buf.append(",\"page_numbers\":");
            buf.append(escapeJson(pageNumbers));
            buf.append(",\"partial\":");
            buf.append(escapeJson(partial));
            buf.append(",\"search_query\":");
            buf.append(escapeJson(searchQuery));
            buf.append(",\"requested_time\":");
            buf.append(requestedTime);
            final String[] relatedQueries = relatedQueryHelper.getRelatedQueries(params.getQuery());
            buf.append(",\"related_query\":");
            buf.append(escapeJson(relatedQueries));
            final String[] relatedContents = relatedContentHelper.getRelatedContents(params.getQuery());
            buf.append(",\"related_contents\":");
            buf.append(escapeJson(relatedContents));
            buf.append(',');
            buf.append("\"result\":[");
            if (!documentItems.isEmpty()) {
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
                                && ComponentUtil.getQueryFieldConfig().isApiResponseField(name)) {
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
            }
            buf.append(']');
            if (facetResponse != null && facetResponse.hasFacetResponse()) {
                // facet field
                buf.append(',');
                buf.append("\"facet_field\":[");
                if (facetResponse.getFieldList() != null) {
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
                }
                buf.append(']');
                // facet q
                buf.append(',');
                buf.append("\"facet_query\":[");
                if (facetResponse.getQueryCountMap() != null) {
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
                }
                buf.append(']');
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

    protected String detailedMessage(final Throwable t) {
        if (t == null) {
            return "Unknown";
        }
        Throwable target = t;
        if (target.getCause() == null) {
            return target.getClass().getSimpleName() + "[" + target.getMessage() + "]";
        }
        final StringBuilder sb = new StringBuilder();
        while (target != null) {
            sb.append(target.getClass().getSimpleName());
            if (target.getMessage() != null) {
                sb.append("[");
                sb.append(target.getMessage());
                sb.append("]");
            }
            sb.append("; ");
            target = target.getCause();
            if (target != null) {
                sb.append("nested: ");
            }
        }
        return sb.toString();
    }

    protected void processLabelRequest(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) {
        final LabelTypeHelper labelTypeHelper = ComponentUtil.getLabelTypeHelper();

        int status = 0;
        Exception err = null;
        final StringBuilder buf = new StringBuilder(255); // TODO replace response stream
        try {
            final List<Map<String, String>> labelTypeItems = labelTypeHelper.getLabelTypeItemList(SearchRequestType.JSON,
                    request.getLocale() == null ? Locale.ROOT : request.getLocale());
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

    protected void processPopularWordRequest(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain chain) {
        if (!ComponentUtil.getFessConfig().isWebApiPopularWord()) {
            writeJsonResponse(9, null, "Unsupported operation.");
            return;
        }

        final String seed = request.getParameter("seed");
        final List<String> tagList = new ArrayList<>();
        final String[] tags = request.getParameterValues("labels");
        if (tags != null) {
            tagList.addAll(Arrays.asList(tags));
        }
        final String key = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
        if (StringUtil.isNotBlank(key)) {
            tagList.add(key);
        }
        final String[] fields = request.getParameterValues("fields");
        final String[] excludes = StringUtil.EMPTY_STRINGS;// TODO

        final PopularWordHelper popularWordHelper = ComponentUtil.getPopularWordHelper();

        int status = 0;
        Exception err = null;
        final StringBuilder buf = new StringBuilder(255); // TODO replace response stream
        try {
            final List<String> popularWordList = popularWordHelper.getWordList(SearchRequestType.JSON, seed,
                    tagList.toArray(new String[tagList.size()]), null, fields, excludes);

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
        final SearchHelper searchHelper = ComponentUtil.getSearchHelper();
        final FavoriteLogService favoriteLogService = ComponentUtil.getComponent(FavoriteLogService.class);
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();

        try {
            final String docId = request.getParameter("docId");
            final String queryId = request.getParameter("queryId");

            final String[] docIds = userInfoHelper.getResultDocIds(URLDecoder.decode(queryId, Constants.UTF_8));
            if (docIds == null) {
                throw new WebApiException(6, "No searched urls.");
            }

            searchHelper.getDocumentByDocId(docId, new String[] { fessConfig.getIndexFieldUrl(), fessConfig.getIndexFieldLang() },
                    OptionalThing.empty()).ifPresent(doc -> {
                        final String favoriteUrl = DocumentUtil.getValue(doc, fessConfig.getIndexFieldUrl(), String.class);
                        final String userCode = userInfoHelper.getUserCode();

                        if (StringUtil.isBlank(userCode)) {
                            throw new WebApiException(2, "No user session.");
                        }
                        if (StringUtil.isBlank(favoriteUrl)) {
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
                        searchHelper.update(id, builder -> {
                            final Script script = ComponentUtil.getLanguageHelper().createScript(doc,
                                    "ctx._source." + fessConfig.getIndexFieldFavoriteCount() + "+=1");
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
        final SearchHelper searchHelper = ComponentUtil.getSearchHelper();
        final FavoriteLogService favoriteLogService = ComponentUtil.getComponent(FavoriteLogService.class);

        int status = 0;
        String body = null;
        Exception err = null;

        try {
            final String queryId = request.getParameter("queryId");
            final String userCode = userInfoHelper.getUserCode();

            if (StringUtil.isBlank(userCode)) {
                throw new WebApiException(2, "No user session.");
            }
            if (StringUtil.isBlank(queryId)) {
                throw new WebApiException(3, "Query ID is null.");
            }

            final String[] docIds = userInfoHelper.getResultDocIds(queryId);
            final List<Map<String, Object>> docList = searchHelper.getDocumentListByDocIds(docIds, new String[] {
                    fessConfig.getIndexFieldUrl(), fessConfig.getIndexFieldDocId(), fessConfig.getIndexFieldFavoriteCount() },
                    OptionalThing.empty(), SearchRequestType.JSON);
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
            buf.append(", \"doc_ids\":[");
            if (!docIdList.isEmpty()) {
                for (int i = 0; i < docIdList.size(); i++) {
                    if (i > 0) {
                        buf.append(',');
                    }
                    buf.append(escapeJson(docIdList.get(i)));
                }
            }
            buf.append(']');
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

    protected static class JsonRequestParams extends SearchRequestParams {

        private final HttpServletRequest request;

        private final FessConfig fessConfig;

        private int startPosition = -1;

        private int pageSize = -1;

        protected JsonRequestParams(final HttpServletRequest request, final FessConfig fessConfig) {
            this.request = request;
            this.fessConfig = fessConfig;
        }

        @Override
        public String getTrackTotalHits() {
            return request.getParameter(Constants.TRACK_TOTAL_HITS);
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
        public Map<String, String[]> getConditions() {
            final Map<String, String[]> conditions = new HashMap<>();
            for (final Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                final String key = entry.getKey();
                if (key.startsWith("as.")) {
                    final String[] value = simplifyArray(entry.getValue());
                    conditions.put(key.substring("as.".length()), value);
                }
            }
            return conditions;
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

        @Override
        public String getSimilarDocHash() {
            return request.getParameter("sdh");
        }

        @Override
        public HighlightInfo getHighlightInfo() {
            return ComponentUtil.getViewHelper().createHighlightInfo();
        }
    }

    @Override
    protected void writeHeaders(final HttpServletResponse response) {
        ComponentUtil.getFessConfig().getApiJsonResponseHeaderList().forEach(e -> response.setHeader(e.getFirst(), e.getSecond()));
    }
}
