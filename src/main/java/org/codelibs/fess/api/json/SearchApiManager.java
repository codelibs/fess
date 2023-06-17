/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import static org.codelibs.core.stream.StreamUtil.stream;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.CoreLibConstants;
import org.codelibs.core.exception.IORuntimeException;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.BaseApiManager;
import org.codelibs.fess.app.service.FavoriteLogService;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.PingResponse;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.es.client.SearchEngineClient;
import org.codelibs.fess.exception.InvalidAccessTokenException;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.exception.ResultOffsetExceededException;
import org.codelibs.fess.exception.WebApiException;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.PopularWordHelper;
import org.codelibs.fess.helper.RelatedContentHelper;
import org.codelibs.fess.helper.RelatedQueryHelper;
import org.codelibs.fess.helper.RoleQueryHelper;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.helper.SuggestHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.UserInfoHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.suggest.entity.SuggestItem;
import org.codelibs.fess.suggest.request.suggest.SuggestRequestBuilder;
import org.codelibs.fess.suggest.request.suggest.SuggestResponse;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.codelibs.fess.util.FacetResponse;
import org.codelibs.fess.util.FacetResponse.Field;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaResponseUtil;
import org.opensearch.script.Script;

public class SearchApiManager extends BaseApiManager {

    private static final Logger logger = LogManager.getLogger(SearchApiManager.class);

    protected static final String MESSAGE_FIELD = "message";

    protected static final String RESULT_FIELD = "result";

    private static final String DOC_ID_FIELD = "doc_id";

    protected static final String GET = "GET";

    protected static final String POST = "POST";

    protected String mimeType = "application/json";

    public SearchApiManager() {
        setPathPrefix("/api/v1");
    }

    @PostConstruct
    public void register() {
        if (logger.isInfoEnabled()) {
            logger.info("Load {}", this.getClass().getSimpleName());
        }
        ComponentUtil.getWebApiManagerFactory().add(this);
    }

    @Override
    protected FormatType detectFormatType(final HttpServletRequest request) {
        final String servletPath = request.getServletPath();
        final String[] values = servletPath.replaceAll("/+", "/").split("/");
        final String value = values.length > 3 ? values[3] : null;
        if (value == null) {
            return FormatType.SEARCH;
        }
        final String type = value.toLowerCase(Locale.ROOT);
        if ("documents".equals(type)) {
            if (values.length > 5 && "favorite".equals(values[5])) {
                request.setAttribute(DOC_ID_FIELD, values[4]);
                return FormatType.FAVORITE;
            }
            if (values.length > 4 && "all".equals(values[4])) {
                return FormatType.SCROLL;
            }
            return FormatType.SEARCH;
        }
        if ("labels".equals(type)) {
            return FormatType.LABEL;
        }
        if ("popular-words".equals(type)) {
            return FormatType.POPULARWORD;
        }
        if ("favorites".equals(type)) {
            return FormatType.FAVORITES;
        }
        if ("health".equals(type)) {
            return FormatType.PING;
        }
        if ("suggest-words".equals(type)) {
            return FormatType.SUGGEST;
        }
        // default
        return FormatType.OTHER;
    }

    @Override
    public boolean matches(final HttpServletRequest request) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isWebApiJson()) {
            return false;
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
        case SUGGEST:
            processSuggestRequest(request, response, chain);
            break;
        default:
            writeJsonResponse(HttpServletResponse.SC_NOT_FOUND, escapeJsonKeyValue(MESSAGE_FIELD, "Not found."));
            break;
        }
    }

    protected boolean acceptHttpMethod(final HttpServletRequest request, final String... methods) {
        final String method = request.getMethod();
        for (final String m : methods) {
            if (m.equals(method)) {
                return true;
            }
        }
        writeJsonResponse(HttpServletResponse.SC_METHOD_NOT_ALLOWED, escapeJsonKeyValue(MESSAGE_FIELD, method + " is not allowed."));
        return false;
    }

    protected void processScrollSearchRequest(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain chain) {
        if (!acceptHttpMethod(request, GET)) {
            return;
        }
        final SearchHelper searchHelper = ComponentUtil.getSearchHelper();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        if (!fessConfig.isAcceptedSearchReferer(request.getHeader("referer"))) {
            writeJsonResponse(HttpServletResponse.SC_BAD_REQUEST, escapeJsonKeyValue(MESSAGE_FIELD, "Referer is invalid."));
            return;
        }

        if (!fessConfig.isApiSearchScroll()) {
            writeJsonResponse(HttpServletResponse.SC_BAD_REQUEST, escapeJsonKeyValue(MESSAGE_FIELD, "Scroll Search is not available."));
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
        } catch (final InvalidQueryException | ResultOffsetExceededException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a scroll request.", e);
            }
            writeJsonResponse(HttpServletResponse.SC_BAD_REQUEST, e);
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a scroll request.", e);
            }
            writeJsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        }

    }

    protected void processPingRequest(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) {
        if (!acceptHttpMethod(request, GET)) {
            return;
        }

        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        try {
            final PingResponse pingResponse = searchEngineClient.ping();
            writeJsonResponse(pingResponse.getStatus() == 0 ? HttpServletResponse.SC_OK : HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "\"data\":" + pingResponse.getMessage());
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a ping request.", e);
            }
            writeJsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        }
    }

    protected void processSearchRequest(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) {
        if (!acceptHttpMethod(request, GET)) {
            return;
        }

        final SearchHelper searchHelper = ComponentUtil.getSearchHelper();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final RelatedQueryHelper relatedQueryHelper = ComponentUtil.getRelatedQueryHelper();
        final RelatedContentHelper relatedContentHelper = ComponentUtil.getRelatedContentHelper();

        String query = null;
        final StringBuilder buf = new StringBuilder(1000);
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
            buf.append("\"data\":[");
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
            writeJsonResponse(HttpServletResponse.SC_OK, buf.toString());
        } catch (final InvalidQueryException | ResultOffsetExceededException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a search request.", e);
            }
            writeJsonResponse(HttpServletResponse.SC_BAD_REQUEST, e);
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a search request.", e);
            }
            writeJsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        }
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
        if (!acceptHttpMethod(request, GET)) {
            return;
        }

        final LabelTypeHelper labelTypeHelper = ComponentUtil.getLabelTypeHelper();

        final StringBuilder buf = new StringBuilder(255);
        try {
            final List<Map<String, String>> labelTypeItems = labelTypeHelper.getLabelTypeItemList(SearchRequestType.JSON,
                    request.getLocale() == null ? Locale.ROOT : request.getLocale());
            buf.append("\"record_count\":");
            buf.append(labelTypeItems.size());
            if (!labelTypeItems.isEmpty()) {
                buf.append(',');
                buf.append("\"data\":[");
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

            writeJsonResponse(HttpServletResponse.SC_OK, buf.toString());
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a label request.", e);
            }
            writeJsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        }
    }

    protected void processPopularWordRequest(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain chain) {
        if (!acceptHttpMethod(request, GET)) {
            return;
        }

        if (!ComponentUtil.getFessConfig().isWebApiPopularWord()) {
            writeJsonResponse(HttpServletResponse.SC_BAD_REQUEST, escapeJsonKeyValue(MESSAGE_FIELD, "Unsupported operation."));
            return;
        }

        final String seed = request.getParameter("seed");
        String[] tags = SearchRequestParams.getParamValueArray(request, "label");
        final String key = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
        if (StringUtil.isNotBlank(key)) {
            tags = ArrayUtils.addAll(tags, key);
        }
        final String[] fields = request.getParameterValues("field");

        final PopularWordHelper popularWordHelper = ComponentUtil.getPopularWordHelper();

        final StringBuilder buf = new StringBuilder(255);
        try {
            final List<String> popularWordList =
                    popularWordHelper.getWordList(SearchRequestType.JSON, seed, tags, null, fields, StringUtil.EMPTY_STRINGS);

            buf.append("\"data\":[");
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
            writeJsonResponse(HttpServletResponse.SC_OK, buf.toString());
        } catch (final WebApiException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a popularWord request.", e);
            }
            writeJsonResponse(e.getStatusCode(), e);
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a popularWord request.", e);
            }
            writeJsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        }
    }

    protected void processFavoriteRequest(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) {
        if (!acceptHttpMethod(request, POST)) {
            return;
        }

        if (!ComponentUtil.getFessConfig().isUserFavorite()) {
            writeJsonResponse(HttpServletResponse.SC_BAD_REQUEST, escapeJsonKeyValue(MESSAGE_FIELD, "Unsupported operation."));
            return;
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final UserInfoHelper userInfoHelper = ComponentUtil.getUserInfoHelper();
        final SearchHelper searchHelper = ComponentUtil.getSearchHelper();
        final FavoriteLogService favoriteLogService = ComponentUtil.getComponent(FavoriteLogService.class);
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();

        try {
            final Object docIdObj = request.getAttribute(DOC_ID_FIELD);
            if (docIdObj == null) {
                throw new WebApiException(HttpServletResponse.SC_BAD_REQUEST, "docId is empty.");
            }
            final String docId = docIdObj.toString();
            final String queryId = request.getParameter("queryId");

            final String[] docIds = userInfoHelper.getResultDocIds(URLDecoder.decode(queryId, Constants.UTF_8));
            if (docIds == null) {
                throw new WebApiException(HttpServletResponse.SC_BAD_REQUEST, "No searched urls.");
            }

            searchHelper.getDocumentByDocId(docId, new String[] { fessConfig.getIndexFieldUrl(), fessConfig.getIndexFieldLang() },
                    OptionalThing.empty()).ifPresent(doc -> {
                        final String favoriteUrl = DocumentUtil.getValue(doc, fessConfig.getIndexFieldUrl(), String.class);
                        final String userCode = userInfoHelper.getUserCode();

                        if (StringUtil.isBlank(userCode)) {
                            throw new WebApiException(HttpServletResponse.SC_BAD_REQUEST, "No user session.");
                        }
                        if (StringUtil.isBlank(favoriteUrl)) {
                            throw new WebApiException(HttpServletResponse.SC_BAD_REQUEST, "URL is null.");
                        }

                        boolean found = false;
                        for (final String id : docIds) {
                            if (docId.equals(id)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            throw new WebApiException(HttpServletResponse.SC_NOT_FOUND, "Not found: " + favoriteUrl);
                        }

                        if (!favoriteLogService.addUrl(userCode, (userInfo, favoriteLog) -> {
                            favoriteLog.setUserInfoId(userInfo.getId());
                            favoriteLog.setUrl(favoriteUrl);
                            favoriteLog.setDocId(docId);
                            favoriteLog.setQueryId(queryId);
                            favoriteLog.setCreatedAt(systemHelper.getCurrentTimeAsLocalDateTime());
                        })) {
                            throw new WebApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to add url: " + favoriteUrl);
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

                        writeJsonResponse(HttpServletResponse.SC_CREATED, escapeJsonKeyValue(RESULT_FIELD, "created"));

                    }).orElse(() -> {
                        throw new WebApiException(HttpServletResponse.SC_NOT_FOUND, "Not found: " + docId);
                    });

        } catch (final WebApiException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a favorite request.", e);
            }
            writeJsonResponse(e.getStatusCode(), e);
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a favorite request.", e);
            }
            writeJsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        }

    }

    protected void processFavoritesRequest(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) {
        if (!acceptHttpMethod(request, GET)) {
            return;
        }

        if (!ComponentUtil.getFessConfig().isUserFavorite()) {
            writeJsonResponse(HttpServletResponse.SC_BAD_REQUEST, escapeJsonKeyValue(MESSAGE_FIELD, "Unsupported operation."));
            return;
        }

        final UserInfoHelper userInfoHelper = ComponentUtil.getUserInfoHelper();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final SearchHelper searchHelper = ComponentUtil.getSearchHelper();
        final FavoriteLogService favoriteLogService = ComponentUtil.getComponent(FavoriteLogService.class);

        String body = null;
        try {
            final String queryId = request.getParameter("queryId");
            final String userCode = userInfoHelper.getUserCode();

            if (StringUtil.isBlank(userCode)) {
                throw new WebApiException(HttpServletResponse.SC_BAD_REQUEST, "No user session.");
            }
            if (StringUtil.isBlank(queryId)) {
                throw new WebApiException(HttpServletResponse.SC_BAD_REQUEST, "Query ID is null.");
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

            final StringBuilder buf = new StringBuilder(255);
            buf.append("\"record_count\":").append(docIdList.size());
            buf.append(", \"data\":[");
            if (!docIdList.isEmpty()) {
                for (int i = 0; i < docIdList.size(); i++) {
                    if (i > 0) {
                        buf.append(',');
                    }
                    buf.append('{').append(escapeJsonKeyValue(DOC_ID_FIELD, docIdList.get(i))).append('}');
                }
            }
            buf.append(']');
            body = buf.toString();
            writeJsonResponse(HttpServletResponse.SC_OK, body);
        } catch (final WebApiException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a favorites request.", e);
            }
            writeJsonResponse(e.getStatusCode(), e);
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a favorites request.", e);
            }
            writeJsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        }
    }

    protected void processSuggestRequest(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        if (!acceptHttpMethod(request, GET)) {
            return;
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isAcceptedSearchReferer(request.getHeader("referer"))) {
            writeJsonResponse(HttpServletResponse.SC_BAD_REQUEST, escapeJsonKeyValue(MESSAGE_FIELD, "Referer is invalid."));
            return;
        }

        final RoleQueryHelper roleQueryHelper = ComponentUtil.getRoleQueryHelper();
        final SearchHelper searchHelper = ComponentUtil.getSearchHelper();

        final StringBuilder buf = new StringBuilder(255);
        try {
            final RequestParameter parameter = RequestParameter.parse(request);
            final String[] langs = searchHelper.getLanguages(request, parameter);

            final SuggestHelper suggestHelper = ComponentUtil.getSuggestHelper();
            final SuggestRequestBuilder builder = suggestHelper.suggester().suggest();
            builder.setQuery(parameter.getQuery());
            stream(parameter.getSuggestFields()).of(stream -> stream.forEach(builder::addField));
            roleQueryHelper.build(SearchRequestType.SUGGEST).stream().forEach(builder::addRole);
            builder.setSize(parameter.getNum());
            stream(langs).of(stream -> stream.forEach(builder::addLang));

            stream(parameter.getTags()).of(stream -> stream.forEach(builder::addTag));
            final String key = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
            if (StringUtil.isNotBlank(key)) {
                builder.addTag(key);
            }

            builder.addKind(SuggestItem.Kind.USER.toString());
            if (ComponentUtil.getFessConfig().isSuggestSearchLog()) {
                builder.addKind(SuggestItem.Kind.QUERY.toString());
            }
            if (ComponentUtil.getFessConfig().isSuggestDocuments()) {
                builder.addKind(SuggestItem.Kind.DOCUMENT.toString());
            }

            final SuggestResponse suggestResponse = builder.execute().getResponse();

            buf.append("\"query_time\":").append(suggestResponse.getTookMs());
            buf.append(",\"record_count\":").append(suggestResponse.getTotal());
            buf.append(",\"page_size\":").append(suggestResponse.getNum());

            if (!suggestResponse.getItems().isEmpty()) {
                buf.append(",\"data\":[");

                boolean first = true;
                for (final SuggestItem item : suggestResponse.getItems()) {
                    if (!first) {
                        buf.append(',');
                    }
                    first = false;

                    buf.append("{\"text\":\"").append(StringEscapeUtils.escapeJson(item.getText())).append('\"');
                    buf.append(",\"labels\":[");
                    for (int i = 0; i < item.getTags().length; i++) {
                        if (i > 0) {
                            buf.append(',');
                        }
                        buf.append('\"').append(StringEscapeUtils.escapeJson(item.getTags()[i])).append('\"');
                    }
                    buf.append(']');
                    buf.append('}');
                }
                buf.append(']');
            }

            writeJsonResponse(HttpServletResponse.SC_OK, buf.toString());
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a suggest request.", e);
            }
            writeJsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        }
    }

    protected static class JsonRequestParams extends SearchRequestParams {

        private final HttpServletRequest request;

        private final FessConfig fessConfig;

        private int startPosition = -1;

        private int offset = -1;

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
        public int getOffset() {
            if (offset != -1) {
                return offset;
            }

            final String value = request.getParameter("offset");
            if (StringUtil.isBlank(value)) {
                offset = 0;
            } else {
                try {
                    offset = Integer.parseInt(value);
                } catch (final NumberFormatException e) {
                    offset = 0;
                }
            }
            return offset;
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

    protected static class RequestParameter extends SearchRequestParams {
        private final String query;

        private final String[] fields;

        private final int num;

        private final HttpServletRequest request;

        private final String[] tags;

        protected RequestParameter(final HttpServletRequest request, final String query, final String[] tags, final String[] fields,
                final int num) {
            this.query = query;
            this.tags = tags;
            this.fields = fields;
            this.num = num;
            this.request = request;
        }

        protected static RequestParameter parse(final HttpServletRequest request) {
            final String query = request.getParameter("q");
            final String[] tags = getParamValueArray(request, "label");
            final String[] fields = getParamValueArray(request, "field");

            final String numStr = request.getParameter("num");
            final int num;
            if (StringUtil.isNotBlank(numStr) && StringUtils.isNumeric(numStr)) {
                num = Integer.parseInt(numStr);
            } else {
                num = 10;
            }

            return new RequestParameter(request, query, tags, fields, num);
        }

        @Override
        public String getQuery() {
            return query;
        }

        protected String[] getSuggestFields() {
            return fields;
        }

        protected int getNum() {
            return num;
        }

        @Override
        public Map<String, String[]> getFields() {
            return Collections.emptyMap();
        }

        @Override
        public Map<String, String[]> getConditions() {
            return Collections.emptyMap();
        }

        public String[] getTags() {
            return tags;
        }

        @Override
        public String[] getLanguages() {
            return getParamValueArray(request, "lang");
        }

        @Override
        public GeoInfo getGeoInfo() {
            throw new UnsupportedOperationException();
        }

        @Override
        public FacetInfo getFacetInfo() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getSort() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getStartPosition() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getOffset() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getPageSize() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String[] getExtraQueries() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getAttribute(final String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Locale getLocale() {
            throw new UnsupportedOperationException();
        }

        @Override
        public SearchRequestType getType() {
            return SearchRequestType.SUGGEST;
        }

        @Override
        public String getSimilarDocHash() {
            throw new UnsupportedOperationException();
        }

        @Override
        public HighlightInfo getHighlightInfo() {
            return new HighlightInfo();
        }
    }

    @Override
    protected void writeHeaders(final HttpServletResponse response) {
        ComponentUtil.getFessConfig().getApiJsonResponseHeaderList().forEach(e -> response.setHeader(e.getFirst(), e.getSecond()));
    }

    protected void writeJsonResponse(final int status, final Throwable t) {
        final Supplier<String> stacktraceString = () -> {
            final StringBuilder buf = new StringBuilder(100);
            if (StringUtil.isBlank(t.getMessage())) {
                buf.append(t.getClass().getName());
            } else {
                buf.append(t.getMessage());
            }
            try (final StringWriter sw = new StringWriter(); final PrintWriter pw = new PrintWriter(sw)) {
                t.printStackTrace(pw);
                pw.flush();
                buf.append(" [ ").append(sw.toString()).append(" ]");
            } catch (final IOException ignore) {}
            return buf.toString();
        };
        final String message;
        if (Constants.TRUE.equalsIgnoreCase(ComponentUtil.getFessConfig().getApiJsonResponseExceptionIncluded())) {
            message = escapeJsonKeyValue(MESSAGE_FIELD, stacktraceString.get());
        } else {
            final String errorCode = UUID.randomUUID().toString();
            message = escapeJsonKeyValue("error_code:", errorCode);
            if (logger.isDebugEnabled()) {
                logger.debug("[{}] {}", errorCode, stacktraceString.get().replace("\n", "\\n"));
            } else {
                logger.warn("[{}] {}", errorCode, t.getMessage());
            }
        }
        final HttpServletResponse response = LaResponseUtil.getResponse();
        if (t instanceof final InvalidAccessTokenException e) {
            response.setHeader("WWW-Authenticate", "Bearer error=\"" + e.getType() + "\"");
            writeJsonResponse(HttpServletResponse.SC_UNAUTHORIZED, message);
        } else {
            writeJsonResponse(status, message);
        }
    }

    protected String escapeJsonKeyValue(final String key, final String value) {
        return "\"" + key + "\":" + escapeJson(value);
    }

    protected void writeJsonResponse(final int status, final String body) {
        final String callback = LaRequestUtil.getOptionalRequest().map(req -> req.getParameter("callback")).orElse(null);
        final boolean isJsonp = ComponentUtil.getFessConfig().isApiJsonpEnabled() && StringUtil.isNotBlank(callback);

        final HttpServletResponse response = LaResponseUtil.getResponse();
        response.setStatus(status);

        final StringBuilder buf = new StringBuilder(1000);
        if (isJsonp) {
            buf.append(escapeCallbackName(callback));
            buf.append('(');
        }
        buf.append('{');
        if (StringUtil.isNotBlank(body)) {
            buf.append(body);
        }
        buf.append('}');
        if (isJsonp) {
            buf.append(')');
        }
        write(buf.toString(), mimeType, Constants.UTF_8);
    }

    protected String escapeCallbackName(final String callbackName) {
        return "/**/" + callbackName.replaceAll("[^0-9a-zA-Z_\\$\\.]", StringUtil.EMPTY);
    }

    protected String escapeJson(final Object obj) {
        if (obj == null) {
            return "null";
        }

        final StringBuilder buf = new StringBuilder(255);
        if (obj instanceof String[]) {
            buf.append('[');
            boolean first = true;
            for (final Object child : (String[]) obj) {
                if (first) {
                    first = false;
                } else {
                    buf.append(',');
                }
                buf.append(escapeJson(child));
            }
            buf.append(']');
        } else if (obj instanceof List<?>) {
            buf.append('[');
            boolean first = true;
            for (final Object child : (List<?>) obj) {
                if (first) {
                    first = false;
                } else {
                    buf.append(',');
                }
                buf.append(escapeJson(child));
            }
            buf.append(']');
        } else if (obj instanceof Map<?, ?>) {
            buf.append('{');
            boolean first = true;
            for (final Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
                if (first) {
                    first = false;
                } else {
                    buf.append(',');
                }
                buf.append(escapeJson(entry.getKey())).append(':').append(escapeJson(entry.getValue()));
            }
            buf.append('}');
        } else if ((obj instanceof Integer) || (obj instanceof Long) || (obj instanceof Float) || (obj instanceof Double)) {
            buf.append((obj));
        } else if (obj instanceof Boolean) {
            buf.append(obj.toString());
        } else if (obj instanceof Date) {
            final SimpleDateFormat sdf = new SimpleDateFormat(CoreLibConstants.DATE_FORMAT_ISO_8601_EXTEND, Locale.ROOT);
            buf.append('\"').append(StringEscapeUtils.escapeJson(sdf.format(obj))).append('\"');
        } else {
            buf.append('\"').append(StringEscapeUtils.escapeJson(obj.toString())).append('\"');
        }
        return buf.toString();
    }

    public void setMimeType(final String mimeType) {
        this.mimeType = mimeType;
    }

}
