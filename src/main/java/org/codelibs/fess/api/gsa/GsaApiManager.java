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
package org.codelibs.fess.api.gsa;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.CoreLibConstants;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.BaseApiManager;
import org.codelibs.fess.api.WebApiRequest;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.exception.InvalidAccessTokenException;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.dbflute.optional.OptionalThing;

public class GsaApiManager extends BaseApiManager {
    private static final Logger logger = LogManager.getLogger(GsaApiManager.class);

    private static final String OUTPUT_XML = "xml"; // or xml_no_dtd
    // http://www.google.com/google.dtd.

    protected String gsaPathPrefix = "/gsa";

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
        if (!fessConfig.isWebApiGsa()) {
            return false;
        }

        final String servletPath = request.getServletPath();
        return servletPath.startsWith(gsaPathPrefix);
    }

    @Override
    public void process(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        switch (getFormatType(request)) {
        case SEARCH:
            processSearchRequest(request, response, chain);
            break;
        default:
            writeXmlResponse(99, false, StringUtil.EMPTY, "Not found.");
            break;
        }
    }

    protected void appendParam(final StringBuilder buf, final String name, final String value) throws UnsupportedEncodingException {
        appendParam(buf, name, value, URLEncoder.encode(value, Constants.UTF_8));
    }

    protected void appendParam(final StringBuilder buf, final String name, final String value, final String original) {
        buf.append("<PARAM name=\"");
        buf.append(escapeXml(name));
        buf.append("\" value=\"");
        buf.append(escapeXml(value));
        buf.append("\" original_value=\"");
        buf.append(escapeXml(original));
        buf.append("\"/>");
    }

    protected void processSearchRequest(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) {
        final SearchHelper searchHelper = ComponentUtil.getSearchHelper();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final boolean xmlDtd = OUTPUT_XML.equals(request.getParameter("output"));

        if (!fessConfig.isAcceptedSearchReferer(request.getHeader("referer"))) {
            writeXmlResponse(99, xmlDtd, StringUtil.EMPTY, "Referer is invalid.");
            return;
        }

        int status = 0;
        String errMsg = StringUtil.EMPTY;
        String query = null;
        final StringBuilder buf = new StringBuilder(1000);
        request.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE, Constants.SEARCH_LOG_ACCESS_TYPE_GSA);
        try {
            final SearchRenderData data = new SearchRenderData();
            final GsaRequestParams params = new GsaRequestParams(request, fessConfig);
            query = params.getQuery();
            searchHelper.search(params, data, OptionalThing.empty());
            final String execTime = data.getExecTime();
            final long allRecordCount = data.getAllRecordCount();
            final List<Map<String, Object>> documentItems = data.getDocumentItems();

            final List<String> getFields = new ArrayList<>();
            // meta tags should be returned
            final String getFieldsParam = request.getParameter("getfields");
            if (StringUtil.isNotBlank(getFieldsParam)) {
                getFields.addAll(Arrays.asList(getFieldsParam.split("\\.")));
            }
            final StringBuilder requestUri = new StringBuilder(request.getRequestURI());
            if (request.getQueryString() != null) {
                requestUri.append("?").append(request.getQueryString());
            }
            final String uriQueryString = requestUri.toString();
            // Input/Output encoding
            final String ie = request.getCharacterEncoding();
            final String oe = "UTF-8";
            // IP address
            final String ip = ComponentUtil.getViewHelper().getClientIp(request);

            buf.append("<TM>");
            buf.append(execTime);
            buf.append("</TM>");
            buf.append("<Q>");
            buf.append(escapeXml(query));
            buf.append("</Q>");
            for (final Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                final String[] values = entry.getValue();
                if (values == null) {
                    continue;
                }
                final String key = entry.getKey();
                if ("sort".equals(key)) {
                    continue;
                }
                for (final String value : values) {
                    appendParam(buf, key, value);
                }
            }
            appendParam(buf, "ie", ie);
            if (request.getParameter("oe") == null) {
                appendParam(buf, "oe", oe);
            }
            final String[] langs = params.getLanguages();
            if (langs.length > 0) {
                appendParam(buf, "inlang", langs[0]);
                appendParam(buf, "ulang", langs[0]);
            }
            appendParam(buf, "ip", ip);
            appendParam(buf, "access", "p");
            appendParam(buf, "sort", params.getSortParam(), params.getSortParam());
            appendParam(buf, "entqr", "3");
            appendParam(buf, "entqrm", "0");
            appendParam(buf, "wc", "200");
            appendParam(buf, "wc_mc", "1");
            if (!documentItems.isEmpty()) {
                buf.append("<RES SN=\"");
                buf.append(data.getCurrentStartRecordNumber());
                buf.append("\" EN=\"");
                buf.append(data.getCurrentEndRecordNumber());
                buf.append("\">");
                buf.append("<M>");
                buf.append(allRecordCount);
                buf.append("</M>");
                if (data.isExistPrevPage() || data.isExistNextPage()) {
                    buf.append("<NB>");
                    if (data.isExistPrevPage()) {
                        long s = data.getCurrentStartRecordNumber() - data.getPageSize() - 1;
                        if (s < 0) {
                            s = 0;
                        }
                        buf.append("<PU>");
                        buf.append(escapeXml(uriQueryString.replaceFirst("start=([0-9]+)", "start=" + s)));
                        buf.append("</PU>");
                    }
                    if (data.isExistNextPage()) {
                        buf.append("<NU>");
                        buf.append(escapeXml(uriQueryString.replaceFirst("start=([0-9]+)", "start=" + data.getCurrentEndRecordNumber())));
                        buf.append("</NU>");
                    }
                    buf.append("</NB>");
                }
                long recordNumber = data.getCurrentStartRecordNumber();
                for (final Map<String, Object> document : documentItems) {
                    buf.append("<R N=\"");
                    buf.append(recordNumber);
                    buf.append("\">");
                    final String url = DocumentUtil.getValue(document, fessConfig.getIndexFieldUrl(), String.class);
                    document.put("UE", url);
                    document.put("U", URLDecoder.decode(url, Constants.UTF_8));
                    document.put("T", DocumentUtil.getValue(document, fessConfig.getResponseFieldContentTitle(), String.class));
                    final float score = DocumentUtil.getValue(document, Constants.SCORE, Float.class, Float.NaN);
                    int rk = 10;
                    if (!Float.isNaN(score)) {
                        if (score < 0.0) {
                            rk = 0;
                        } else if (score > 1.0) {
                            rk = 10;
                        } else {
                            rk = (int) (score * 10.0);
                        }
                    }
                    document.put("RK", rk);
                    document.put("S", DocumentUtil.getValue(document, fessConfig.getResponseFieldContentDescription(), String.class,
                            StringUtil.EMPTY));
                    final String lang = DocumentUtil.getValue(document, fessConfig.getIndexFieldLang(), String.class);
                    if (StringUtil.isNotBlank(lang)) {
                        document.put("LANG", lang);
                        document.remove(fessConfig.getIndexFieldLang());
                    }
                    final String gsaMetaPrefix = fessConfig.getQueryGsaMetaPrefix();
                    for (final Map.Entry<String, Object> entry : document.entrySet()) {
                        final String name = entry.getKey();
                        if (StringUtil.isNotBlank(name) && entry.getValue() != null && fessConfig.isGsaResponseFields(name)) {
                            if (name.startsWith(gsaMetaPrefix)) {
                                final String tagName = name.replaceFirst("^" + gsaMetaPrefix, StringUtil.EMPTY);
                                if (getFields.contains(tagName)) {
                                    buf.append("<MT N=\"");
                                    buf.append(tagName);
                                    buf.append("\" V=\"");
                                    buf.append(escapeXml(entry.getValue()));
                                    buf.append("\"/>");
                                }
                            } else {
                                final String tagName = name;
                                buf.append('<');
                                buf.append(tagName);
                                buf.append('>');
                                buf.append(escapeXml(entry.getValue()));
                                buf.append("</");
                                buf.append(tagName);
                                buf.append('>');
                            }
                        }
                    }
                    buf.append("<ENT_SOURCE>").append(escapeXml("FESS")).append("</ENT_SOURCE>");
                    String lastModified = DocumentUtil.getValue(document, fessConfig.getIndexFieldLastModified(), String.class);
                    if (StringUtil.isBlank(lastModified)) {
                        lastModified = StringUtil.EMPTY;
                    }
                    lastModified = lastModified.split("T")[0];
                    buf.append("<FS NAME=\"date\" VALUE=\"").append(escapeXml(lastModified)).append("\"/>");

                    buf.append("<HAS>");
                    buf.append("<L/>");
                    buf.append("<C SZ=\"");
                    buf.append(DocumentUtil.getValue(document, fessConfig.getIndexFieldContentLength(), Long.class, (long) 0).longValue()
                            / 1000);
                    document.remove(fessConfig.getIndexFieldContentLength());
                    buf.append("k\" CID=\"");
                    buf.append(DocumentUtil.getValue(document, fessConfig.getIndexFieldDocId(), String.class));
                    document.remove(fessConfig.getIndexFieldDocId());
                    buf.append("\" ENC=\"");
                    final String charsetField = fessConfig.getQueryGsaIndexFieldCharset();
                    String charset = DocumentUtil.getValue(document, charsetField, String.class);
                    document.remove(charsetField);
                    if (StringUtil.isBlank(charset)) {
                        final String contentTypeField = fessConfig.getQueryGsaIndexFieldContentType();
                        charset = DocumentUtil.getValue(document, contentTypeField, String.class);
                        document.remove(contentTypeField);
                        if (StringUtil.isNotBlank(charset)) {
                            final Matcher m = Pattern.compile(".*;\\s*charset=(.+)").matcher(charset);
                            if (m.matches()) {
                                charset = m.group(1);
                            }
                        }
                        if (StringUtil.isBlank(charset)) {
                            charset = Constants.UTF_8;
                        }
                    }
                    buf.append(charset);
                    buf.append("\"/>");
                    buf.append("</HAS>");
                    buf.append("</R>");
                    recordNumber++;
                }
                buf.append("</RES>");
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
            if (e instanceof final InvalidAccessTokenException iate) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setHeader("WWW-Authenticate", "Bearer error=\"" + iate.getType() + "\"");
            }
        }

        writeXmlResponse(status, xmlDtd, buf.toString(), errMsg);
    }

    protected void writeXmlResponse(final int status, final boolean xmlDtd, final String body, final String errMsg) {
        final StringBuilder buf = new StringBuilder(1000);
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
        if (xmlDtd) {
            buf.append("<!DOCTYPE GSP SYSTEM \"google.dtd\">");
        }
        buf.append("<GSP VER=\"");
        buf.append(Constants.GSA_API_VERSION);
        buf.append("\">");
        if (status == 0) {
            buf.append(body);
        } else {
            buf.append("<message>");
            buf.append(escapeXml(errMsg));
            buf.append("</message>");
        }
        buf.append("</GSP>");
        write(buf.toString(), "text/xml", Constants.UTF_8);

    }

    protected String escapeXml(final Object obj) {
        final StringBuilder buf = new StringBuilder(255);
        if (obj instanceof List<?>) {
            buf.append("<list>");
            for (final Object child : (List<?>) obj) {
                buf.append("<item>").append(escapeXml(child)).append("</item>");
            }
            buf.append("</list>");
        } else if (obj instanceof Map<?, ?>) {
            buf.append("<data>");
            for (final Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {

                buf.append("<name>").append(escapeXml(entry.getKey())).append("</name><value>").append(escapeXml(entry.getValue()))
                        .append("</value>");
            }
            buf.append("</data>");
        } else if (obj instanceof Date) {
            final SimpleDateFormat sdf = new SimpleDateFormat(CoreLibConstants.DATE_FORMAT_ISO_8601_EXTEND);
            buf.append(StringEscapeUtils.escapeXml10(sdf.format(obj)));
        } else if (obj != null) {
            buf.append(StringEscapeUtils.escapeXml10(obj.toString()));
        }
        return buf.toString();
    }

    public String getXmlPathPrefix() {
        return gsaPathPrefix;
    }

    public void setXmlPathPrefix(final String xmlPathPrefix) {
        this.gsaPathPrefix = xmlPathPrefix;
    }

    static class WrappedWebApiRequest extends WebApiRequest {
        private Map<String, String[]> parameters;
        private final Map<String, String[]> extraParameters;

        public WrappedWebApiRequest(final HttpServletRequest request, final String servletPath, final Map<String, String[]> extraParams) {
            super(request, servletPath);
            extraParameters = extraParams;
        }

        @Override
        public String getParameter(final String name) {
            final String[] values = getParameterMap().get(name);
            if (values != null) {
                return values[0];
            }
            return super.getParameter(name);
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            if (parameters == null) {
                parameters = new HashMap<>();
                parameters.putAll(super.getParameterMap());
                // Parameter of the same key will be overwritten
                parameters.putAll(extraParameters);
            }
            return Collections.unmodifiableMap(parameters);
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.enumeration(getParameterMap().keySet());
        }

        @Override
        public String[] getParameterValues(final String name) {
            return getParameterMap().get(name);
        }
    }

    protected static class GsaRequestParams extends SearchRequestParams {

        private final HttpServletRequest request;

        private final FessConfig fessConfig;

        private int startPosition = -1;

        private int pageSize = -1;

        private String sortParam;

        protected GsaRequestParams(final HttpServletRequest request, final FessConfig fessConfig) {
            this.request = request;
            this.fessConfig = fessConfig;
            this.sortParam = request.getParameter("sort");
            if (this.sortParam == null) {
                this.sortParam = fessConfig.getQueryGsaDefaultSort();
            }
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
            final List<String> queryList = new ArrayList<>();
            for (final String s : getParamValueArray(request, "ex_q")) {
                queryList.add(s.trim());
            }
            final String gsaMetaPrefix = fessConfig.getQueryGsaMetaPrefix();
            final String requiredFields = request.getParameter("requiredfields");
            if (StringUtil.isNotBlank(requiredFields)) {
                queryList.add(gsaMetaPrefix + requiredFields.replace(".", " AND " + gsaMetaPrefix).replace("|", " OR " + gsaMetaPrefix));
            }
            return queryList.toArray(new String[queryList.size()]);
        }

        @Override
        public Map<String, String[]> getFields() {
            final Map<String, String[]> fields = new HashMap<>();
            for (final Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                final String key = entry.getKey();
                if (key.startsWith("fields.")) {
                    final String[] value = simplifyArray(entry.getValue());
                    fields.put(key.substring("fields.".length()), value);
                } else if ("site".equals(key)) {
                    final String[] value = simplifyArray(entry.getValue());
                    fields.put("label", value);
                }
            }
            return fields;
        }

        @Override
        public Map<String, String[]> getConditions() {
            final Map<String, String[]> conditions = new HashMap<>();
            for (final Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                final String key = entry.getKey();
                if (key.startsWith("as_")) {
                    final String[] value = simplifyArray(entry.getValue());
                    conditions.put(key.substring("as_".length()), value);
                }
            }
            return conditions;
        }

        @Override
        public String[] getLanguages() {
            final String[] langs = getParamValueArray(request, "ulang");
            if (langs.length > 0) {
                return langs;
            }
            if (request.getHeader("Accept-Language") != null) {
                return Collections.list(request.getLocales()).stream().map(Locale::getLanguage).toArray(n -> new String[n]);
            }
            return new String[] { fessConfig.getQueryGsaDefaultLang() };
        }

        @Override
        public GeoInfo getGeoInfo() {
            return createGeoInfo(request);
        }

        @Override
        public FacetInfo getFacetInfo() {
            return createFacetInfo(request);
        }

        public String getSortParam() {
            return sortParam;
        }

        @Override
        public String getSort() {
            // Sort By Relevance (Default)
            // Sort By Date: date:<direction>:<mode>:<format>
            // Sort by Metadata: meta:<name>:<direction>:<mode>:<language>:<case>:<numeric>
            if (StringUtil.isBlank(sortParam)) {
                return null;
            }

            final String[] values = sortParam.split(":");
            if (values.length > 0) {
                if ("date".equals(values[0])) {
                    final StringBuilder buf = new StringBuilder();
                    buf.append(fessConfig.getIndexFieldTimestamp());
                    if (values.length > 1) {
                        if ("A".equals(values[1])) {
                            buf.append(".asc");
                        } else if ("D".equals(values[1])) {
                            buf.append(".desc");
                        }
                    }
                    buf.append(",score.desc");
                    return buf.toString();
                }
                if ("meta".equals(values[0]) && values.length > 1) {
                    final StringBuilder buf = new StringBuilder();
                    buf.append(fessConfig.getQueryGsaMetaPrefix() + values[1]);
                    if (values.length > 2) {
                        if ("A".equals(values[2])) {
                            buf.append(".asc");
                        } else if ("D".equals(values[2])) {
                            buf.append(".desc");
                        }
                    }
                    buf.append(",score.desc");
                    return buf.toString();
                }
            }

            sortParam = "";
            return null;
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
            return SearchRequestType.GSA;
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

    public void setGsaPathPrefix(final String gsaPathPrefix) {
        this.gsaPathPrefix = gsaPathPrefix;
    }

    @Override
    protected void writeHeaders(final HttpServletResponse response) {
        ComponentUtil.getFessConfig().getApiGsaResponseHeaderList().forEach(e -> response.setHeader(e.getFirst(), e.getSecond()));
    }
}
