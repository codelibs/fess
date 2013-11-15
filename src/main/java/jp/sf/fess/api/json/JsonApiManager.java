/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.api.json;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.sf.fess.Constants;
import jp.sf.fess.WebApiException;
import jp.sf.fess.api.BaseApiManager;
import jp.sf.fess.api.WebApiManager;
import jp.sf.fess.api.WebApiRequest;
import jp.sf.fess.api.WebApiResponse;
import jp.sf.fess.db.allcommon.CDef;
import jp.sf.fess.entity.FieldAnalysisResponse;
import jp.sf.fess.entity.SuggestResponse;
import jp.sf.fess.entity.SuggestResponse.SuggestResponseList;
import jp.sf.fess.suggest.Suggester;
import jp.sf.fess.util.FacetResponse;
import jp.sf.fess.util.FacetResponse.Field;
import jp.sf.fess.util.MoreLikeThisResponse;
import jp.sf.fess.util.WebApiUtil;

import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonApiManager extends BaseApiManager implements WebApiManager {

    private static final Logger logger = LoggerFactory
            .getLogger(JsonApiManager.class);

    protected String jsonPathPrefix = "/json";

    @Override
    public boolean matches(final HttpServletRequest request) {
        if (Constants.FALSE.equals(getCrawlerProperties().getProperty(
                Constants.WEB_API_JSON_PROPERTY, Constants.TRUE))) {
            return false;
        }

        final String servletPath = request.getServletPath();
        return servletPath.startsWith(jsonPathPrefix);
    }

    @Override
    public void process(final HttpServletRequest request,
            final HttpServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final String formatType = request.getParameter("type");
        switch (getFormatType(formatType)) {
        case SEARCH:
            processSearchRequest(request, response, chain);
            break;
        case LABEL:
            processLabelRequest(request, response, chain);
            break;
        case SUGGEST:
            processSuggestRequest(request, response, chain);
            break;
        case ANALYSIS:
            processAnalysisRequest(request, response, chain);
            break;
        case HOTSEARCHWORD:
            processHotSearchWordRequest(request, response, chain);
            break;
        case FAVORITE:
            processFavoriteRequest(request, response, chain);
            break;
        case FAVORITES:
            processFavoritesRequest(request, response, chain);
            break;
        default:
            writeJsonResponse(99, Constants.EMPTY_STRING, "Not found.");
            break;
        }
    }

    protected void processSearchRequest(final HttpServletRequest request,
            final HttpServletResponse response, final FilterChain chain) {
        int status = 0;
        String errMsg = Constants.EMPTY_STRING;
        String query = null;
        final StringBuilder buf = new StringBuilder(1000);
        request.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE,
                CDef.AccessType.Json);
        final String queryId = request.getParameter("queryId");
        try {
            chain.doFilter(new WebApiRequest(request, SEARCH_API),
                    new WebApiResponse(response));
            WebApiUtil.validate();
            query = WebApiUtil.getObject("searchQuery");
            final String execTime = WebApiUtil.getObject("execTime");
            final String pageSize = WebApiUtil.getObject("pageSize");
            final String currentPageNumber = WebApiUtil
                    .getObject("currentPageNumber");
            final String allRecordCount = WebApiUtil
                    .getObject("allRecordCount");
            final String allPageCount = WebApiUtil.getObject("allPageCount");
            final List<Map<String, Object>> documentItems = WebApiUtil
                    .getObject("documentItems");
            final FacetResponse facetResponse = WebApiUtil
                    .getObject("facetResponse");
            final MoreLikeThisResponse moreLikeThisResponse = WebApiUtil
                    .getObject("moreLikeThisResponse");

            buf.append("\"query\":\"");
            buf.append(escapeJsonString(query));
            buf.append("\",");
            buf.append("\"execTime\":");
            buf.append(execTime);
            buf.append(',');
            if (StringUtil.isNotBlank(queryId)) {
                buf.append("\"queryId\":");
                buf.append(escapeJsonString(queryId));
                buf.append(',');
            }
            buf.append("\"pageSize\":");
            buf.append(pageSize);
            buf.append(',');
            buf.append("\"pageNumber\":");
            buf.append(currentPageNumber);
            buf.append(',');
            buf.append("\"recordCount\":");
            buf.append(allRecordCount);
            buf.append(',');
            buf.append("\"pageCount\":");
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
                    for (final Map.Entry<String, Object> entry : document
                            .entrySet()) {
                        final String name = entry.getKey();
                        if (StringUtil.isNotBlank(name)
                                && entry.getValue() != null
                                && getQueryHelper().isApiResponseField(name)) {
                            if (!first2) {
                                buf.append(',');
                            } else {
                                first2 = false;
                            }
                            buf.append('\"');
                            buf.append(escapeJsonString(name));
                            buf.append("\":\"");
                            buf.append(escapeJsonString(entry.getValue()
                                    .toString()));
                            buf.append('\"');
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
                    buf.append("\"facetField\":[");
                    boolean first1 = true;
                    for (final Field field : facetResponse.getFieldList()) {
                        if (!first1) {
                            buf.append(',');
                        } else {
                            first1 = false;
                        }
                        buf.append("{\"name\":\"");
                        buf.append(escapeJsonString(field.getName()));
                        buf.append("\",\"result\":[");
                        boolean first2 = true;
                        for (final Map.Entry<String, Long> entry : field
                                .getValueCountMap().entrySet()) {
                            if (!first2) {
                                buf.append(',');
                            } else {
                                first2 = false;
                            }
                            buf.append("{\"value\":\"");
                            buf.append(escapeJsonString(entry.getKey()));
                            buf.append("\",\"count\":");
                            buf.append(entry.getValue());
                            buf.append('}');
                        }
                        buf.append(']');
                        buf.append('}');
                    }
                    buf.append(']');
                }
                // facet query
                if (facetResponse.getQueryCountMap() != null) {
                    buf.append(',');
                    buf.append("\"facetQuery\":[");
                    boolean first1 = true;
                    for (final Map.Entry<String, Long> entry : facetResponse
                            .getQueryCountMap().entrySet()) {
                        if (!first1) {
                            buf.append(',');
                        } else {
                            first1 = false;
                        }
                        buf.append("{\"value\":\"");
                        buf.append(escapeJsonString(entry.getKey()));
                        buf.append("\",\"count\":");
                        buf.append(entry.getValue());
                        buf.append('}');
                    }
                    buf.append(']');
                }
            }
            if (moreLikeThisResponse != null && !moreLikeThisResponse.isEmpty()) {
                buf.append(',');
                buf.append("\"moreLikeThis\":[");
                boolean first = true;
                for (final Map.Entry<String, List<Map<String, Object>>> mltEntry : moreLikeThisResponse
                        .entrySet()) {
                    if (!first) {
                        buf.append(',');
                    } else {
                        first = false;
                    }
                    buf.append("{\"id\":\"");
                    buf.append(escapeJsonString(mltEntry.getKey()));
                    buf.append("\",\"result\":[");
                    boolean first1 = true;
                    for (final Map<String, Object> document : mltEntry
                            .getValue()) {
                        if (!first1) {
                            buf.append(',');
                        } else {
                            first1 = false;
                        }
                        buf.append('{');
                        boolean first2 = true;
                        for (final Map.Entry<String, Object> entry : document
                                .entrySet()) {
                            if (StringUtil.isNotBlank(entry.getKey())
                                    && entry.getValue() != null) {
                                if (!first2) {
                                    buf.append(',');
                                } else {
                                    first2 = false;
                                }
                                buf.append('\"');
                                buf.append(escapeJsonString(entry.getKey()));
                                buf.append("\":\"");
                                buf.append(escapeJsonString(entry.getValue()
                                        .toString()));
                                buf.append('\"');
                            }
                        }
                        buf.append('}');
                    }
                    buf.append("]}");
                }
                buf.append(']');
            }
        } catch (final Exception e) {
            status = 1;
            errMsg = e.getMessage();
            if (errMsg == null) {
                errMsg = e.getClass().getName();
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a search request.", e);
            }
        }

        writeJsonResponse(status, buf.toString(), errMsg);

    }

    protected void processLabelRequest(final HttpServletRequest request,
            final HttpServletResponse response, final FilterChain chain) {
        int status = 0;
        String errMsg = Constants.EMPTY_STRING;
        final StringBuilder buf = new StringBuilder(255);
        try {
            final List<Map<String, String>> labelTypeItems = getLabelTypeHelper()
                    .getLabelTypeItemList();
            buf.append("\"recordCount\":");
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
                    buf.append("{\"label\":\"");
                    buf.append(escapeJsonString(labelMap
                            .get(Constants.ITEM_LABEL)));
                    buf.append("\", \"value\":\"");
                    buf.append(escapeJsonString(labelMap
                            .get(Constants.ITEM_VALUE)));
                    buf.append("\"}");
                }
                buf.append(']');
            }
        } catch (final Exception e) {
            status = 1;
            errMsg = e.getMessage();
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a label request.", e);
            }
        }

        writeJsonResponse(status, buf.toString(), errMsg);

    }

    protected void processSuggestRequest(final HttpServletRequest request,
            final HttpServletResponse response, final FilterChain chain) {

        int status = 0;
        String errMsg = Constants.EMPTY_STRING;
        final StringBuilder buf = new StringBuilder(255);
        try {
            chain.doFilter(new WebApiRequest(request, SUGGEST_API),
                    new WebApiResponse(response));
            WebApiUtil.validate();
            final Integer suggestRecordCount = WebApiUtil
                    .getObject("suggestRecordCount");
            final List<SuggestResponse> suggestResultList = WebApiUtil
                    .getObject("suggestResultList");
            final List<String> suggestFieldName = WebApiUtil
                    .getObject("suggestFieldName");

            buf.append("\"recordCount\":");
            buf.append(suggestRecordCount);

            if (suggestResultList.size() > 0) {
                buf.append(',');
                buf.append("\"result\":[");
                boolean first1 = true;
                for (int i = 0; i < suggestResultList.size(); i++) {

                    final SuggestResponse suggestResponse = suggestResultList
                            .get(i);

                    for (final Map.Entry<String, List<String>> entry : suggestResponse
                            .entrySet()) {
                        final String fn = suggestFieldName.get(i);
                        final Suggester suggester = getSuggesterManager()
                                .getSuggester(fn);
                        if (suggester != null) {
                            if (!first1) {
                                buf.append(',');
                            } else {
                                first1 = false;
                            }

                            final SuggestResponseList srList = (SuggestResponseList) entry
                                    .getValue();

                            buf.append("{\"token\":\"");
                            buf.append(escapeJsonString(entry.getKey()));
                            buf.append("\", \"fn\":\"");
                            buf.append(escapeJsonString(fn));
                            buf.append("\", \"startOffset\":");
                            buf.append(Integer.toString(srList.getStartOffset()));
                            buf.append(", \"endOffset\":");
                            buf.append(Integer.toString(srList.getEndOffset()));
                            buf.append(", \"numFound\":");
                            buf.append(Integer.toString(srList.getNumFound()));
                            buf.append(", ");
                            buf.append("\"result\":[");
                            boolean first2 = true;
                            for (final String value : srList) {
                                if (!first2) {
                                    buf.append(',');
                                } else {
                                    first2 = false;
                                }
                                buf.append('"');
                                buf.append(escapeJsonString(suggester
                                        .convertResultString(value)));
                                buf.append('"');
                            }
                            buf.append("]}");
                        }
                    }

                }
                buf.append(']');
            }
        } catch (final Exception e) {
            if (e instanceof WebApiException) {
                status = ((WebApiException) e).getStatusCode();
            } else {
                status = 1;
            }
            errMsg = e.getMessage();
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a suggest request.", e);
            }
        }

        writeJsonResponse(status, buf.toString(), errMsg);

    }

    protected void processAnalysisRequest(final HttpServletRequest request,
            final HttpServletResponse response, final FilterChain chain) {

        int status = 0;
        String errMsg = Constants.EMPTY_STRING;
        final StringBuilder buf = new StringBuilder(255);
        try {
            chain.doFilter(new WebApiRequest(request, ANALYSIS_API),
                    new WebApiResponse(response));
            WebApiUtil.validate();
            final FieldAnalysisResponse fieldAnalysis = WebApiUtil
                    .getObject("fieldAnalysis");

            buf.append("\"recordCount\":");
            buf.append(fieldAnalysis.size());

            if (fieldAnalysis.size() > 0) {
                buf.append(',');
                buf.append("\"result\":[");
                boolean first1 = true;
                for (final Map.Entry<String, Map<String, List<Map<String, Object>>>> fEntry : fieldAnalysis
                        .entrySet()) {
                    if (first1) {
                        first1 = false;
                    } else {
                        buf.append(',');
                    }
                    buf.append("{\"field\":\"")
                            .append(escapeJsonString(fEntry.getKey()))
                            .append("\",\"analysis\":[");
                    boolean first2 = true;
                    for (final Map.Entry<String, List<Map<String, Object>>> aEntry : fEntry
                            .getValue().entrySet()) {
                        if (first2) {
                            first2 = false;
                        } else {
                            buf.append(',');
                        }
                        buf.append("{\"name\":\"")
                                .append(escapeJsonString(aEntry.getKey()))
                                .append("\",\"data\":[");
                        boolean first3 = true;
                        for (final Map<String, Object> dataMap : aEntry
                                .getValue()) {
                            if (first3) {
                                first3 = false;
                            } else {
                                buf.append(',');
                            }
                            buf.append('{');
                            boolean first4 = true;
                            for (final Map.Entry<String, Object> dEntry : dataMap
                                    .entrySet()) {
                                final String key = dEntry.getKey();
                                final Object value = dEntry.getValue();
                                if (StringUtil.isNotBlank(key) && value != null) {
                                    if (first4) {
                                        first4 = false;
                                    } else {
                                        buf.append(',');
                                    }
                                    buf.append('\"')
                                            .append(escapeJsonString(key))
                                            .append("\":")
                                            .append(escapeJson(value));
                                }
                            }
                            buf.append('}');
                        }
                        buf.append("]}");
                    }
                    buf.append("]}");
                }
                buf.append(']');
            }
        } catch (final Exception e) {
            if (e instanceof WebApiException) {
                status = ((WebApiException) e).getStatusCode();
            } else {
                status = 1;
            }
            errMsg = e.getMessage();
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a suggest request.", e);
            }
        }

        writeJsonResponse(status, buf.toString(), errMsg);

    }

    protected void processHotSearchWordRequest(
            final HttpServletRequest request,
            final HttpServletResponse response, final FilterChain chain) {

        int status = 0;
        String errMsg = Constants.EMPTY_STRING;
        final StringBuilder buf = new StringBuilder(255);
        try {
            chain.doFilter(new WebApiRequest(request, HOT_SEARCH_WORD_API),
                    new WebApiResponse(response));
            WebApiUtil.validate();
            final List<String> hotSearchWordList = WebApiUtil
                    .getObject("hotSearchWordList");

            buf.append("\"result\":[");
            boolean first1 = true;
            for (final String word : hotSearchWordList) {
                if (!first1) {
                    buf.append(',');
                } else {
                    first1 = false;
                }
                buf.append("\"");
                buf.append(escapeJsonString(word));
                buf.append("\"");
            }
            buf.append(']');
        } catch (final Exception e) {
            if (e instanceof WebApiException) {
                status = ((WebApiException) e).getStatusCode();
            } else {
                status = 1;
            }
            errMsg = e.getMessage();
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a hotSearchWord request.", e);
            }
        }

        writeJsonResponse(status, buf.toString(), errMsg);

    }

    protected void processFavoriteRequest(final HttpServletRequest request,
            final HttpServletResponse response, final FilterChain chain) {
        int status = 0;
        String body = null;
        String errMsg = null;
        try {
            chain.doFilter(new WebApiRequest(request, FAVORITE_API),
                    new WebApiResponse(response));
            WebApiUtil.validate();

            body = "\"result\":\"ok\"";

        } catch (final Exception e) {
            if (e instanceof WebApiException) {
                status = ((WebApiException) e).getStatusCode();
            } else {
                status = 1;
            }
            errMsg = e.getMessage();
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a favorite request.", e);
            }
        }

        writeJsonResponse(status, body, errMsg);
    }

    protected void processFavoritesRequest(final HttpServletRequest request,
            final HttpServletResponse response, final FilterChain chain) {
        int status = 0;
        String body = null;
        String errMsg = null;

        try {
            chain.doFilter(new WebApiRequest(request, FAVORITES_API),
                    new WebApiResponse(response));
            WebApiUtil.validate();
            final List<String> docIdList = WebApiUtil.getObject("docIdList");

            final StringBuilder buf = new StringBuilder();
            buf.append("\"num\":").append(docIdList.size());
            if (!docIdList.isEmpty()) {
                buf.append(", \"docIds\":[");
                for (int i = 0; i < docIdList.size(); i++) {
                    if (i > 0) {
                        buf.append(',');
                    }
                    buf.append('"');
                    buf.append(docIdList.get(i));
                    buf.append('"');
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
            errMsg = e.getMessage();
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a favorites request.", e);
            }
        }

        writeJsonResponse(status, body, errMsg);

    }

    protected void writeJsonResponse(final int status, final String body,
            final String errMsg) {
        final String callback = RequestUtil.getRequest().getParameter(
                "callback");
        final boolean isJsonp = StringUtil.isNotBlank(callback);

        final StringBuilder buf = new StringBuilder(1000);
        if (isJsonp) {
            buf.append(escapeCallbackName(callback));
            buf.append('(');
        }
        buf.append("{\"response\":");
        buf.append("{\"version\":");
        buf.append(Constants.WEB_API_VERSION);
        buf.append(',');
        buf.append("\"status\":");
        buf.append(status);
        buf.append(',');
        if (status == 0) {
            buf.append(body);
        } else {
            buf.append("\"message\":\"");
            buf.append(escapeJsonString(errMsg));
            buf.append('\"');
        }
        buf.append('}');
        buf.append('}');
        if (isJsonp) {
            buf.append(')');
        }
        ResponseUtil.write(buf.toString(), "text/javascript+json",
                Constants.UTF_8);

    }

    protected String escapeCallbackName(final String callbackName) {
        return callbackName.replaceAll("[^0-9a-zA-Z_\\$\\.]", "");
    }

    protected String escapeJson(final Object obj) {
        final StringBuilder buf = new StringBuilder(255);
        if (obj instanceof List<?>) {
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
                buf.append(escapeJson(entry.getKey())).append(':')
                        .append(escapeJson(entry.getValue()));
            }
            buf.append('}');
        } else if (obj instanceof Integer || obj instanceof Long
                || obj instanceof Float || obj instanceof Double
                || obj instanceof Short) {
            buf.append(obj);
        } else if (obj == null) {
            buf.append("\"\"");
        } else {
            buf.append('\"').append(escapeJsonString(obj.toString()))
                    .append('\"');
        }
        return buf.toString();
    }

    protected String escapeJsonString(final String str) {
        if (str == null) {
            return "";
        }

        final StringWriter out = new StringWriter(str.length() * 2);
        int sz;
        sz = str.length();
        for (int i = 0; i < sz; i++) {
            final char ch = str.charAt(i);

            // handle unicode
            if (ch > 0xfff) {
                out.write("\\u" + hex(ch));
            } else if (ch > 0xff) {
                out.write("\\u0" + hex(ch));
            } else if (ch > 0x7f) {
                out.write("\\u00" + hex(ch));
            } else if (ch < 32) {
                switch (ch) {
                case '\b':
                    out.write('\\');
                    out.write('b');
                    break;
                case '\n':
                    out.write('\\');
                    out.write('n');
                    break;
                case '\t':
                    out.write('\\');
                    out.write('t');
                    break;
                case '\f':
                    out.write('\\');
                    out.write('f');
                    break;
                case '\r':
                    out.write('\\');
                    out.write('r');
                    break;
                default:
                    if (ch > 0xf) {
                        out.write("\\u00" + hex(ch));
                    } else {
                        out.write("\\u000" + hex(ch));
                    }
                    break;
                }
            } else {
                switch (ch) {
                case '"':
                    out.write("\\u0022");
                    break;
                case '\\':
                    out.write("\\u005C");
                    break;
                case '/':
                    out.write("\\u002F");
                    break;
                default:
                    out.write(ch);
                    break;
                }
            }
        }
        return out.toString();
    }

    private String hex(final char ch) {
        return Integer.toHexString(ch).toUpperCase();
    }

    public String getJsonPathPrefix() {
        return jsonPathPrefix;
    }

    public void setJsonPathPrefix(final String jsonPathPrefix) {
        this.jsonPathPrefix = jsonPathPrefix;
    }

}
