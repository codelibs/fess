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
package org.codelibs.fess.api.gsa;

import java.io.IOException;
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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.codelibs.core.CoreLibConstants;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.BaseApiManager;
import org.codelibs.fess.api.WebApiManager;
import org.codelibs.fess.api.WebApiRequest;
import org.codelibs.fess.app.service.SearchService;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.exception.InvalidAccessTokenException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.dbflute.optional.OptionalThing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GsaApiManager extends BaseApiManager implements WebApiManager {
    private static final Logger logger = LoggerFactory.getLogger(GsaApiManager.class);

    private static final String GSA_META_SUFFIX = "_s";

    protected String gsaPathPrefix = "/gsa";

    protected String gsaMetaPrefix = "MT_";

    protected String charsetField = "charset";

    protected String contentTypeField = "content_type";

    @Override
    public boolean matches(final HttpServletRequest request) {
        if (!ComponentUtil.getFessConfig().isWebApiGsa()) {
            return false;
        }
        final String servletPath = request.getServletPath();
        return servletPath.startsWith(gsaPathPrefix);
    }

    @Override
    public void process(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException,
            ServletException {
        final String formatType = request.getParameter("type");
        switch (getFormatType(formatType)) {
        case SEARCH:
            processSearchRequest(request, response, chain);
            break;
        default:
            writeXmlResponse(-1, false, StringUtil.EMPTY, "Not found.");
            break;
        }
    }

    protected void processSearchRequest(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) {
        final SearchService searchService = ComponentUtil.getComponent(SearchService.class);
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        int status = 0;
        String errMsg = StringUtil.EMPTY;
        String query = null;
        final StringBuilder buf = new StringBuilder(1000);
        request.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE, Constants.SEARCH_LOG_ACCESS_TYPE_XML);
        boolean xmlDtd = false;
        try {
            final SearchRenderData data = new SearchRenderData();
            final GsaRequestParams params = new GsaRequestParams(request, fessConfig);
            query = params.getQuery();
            searchService.search(params, data, OptionalThing.empty());
            final String execTime = data.getExecTime();
            final long allRecordCount = data.getAllRecordCount();
            final List<Map<String, Object>> documentItems = data.getDocumentItems();

            final List<String> getFields = new ArrayList<>();
            // meta tags should be returned
            final String getFieldsParam = request.getParameter("getfields");
            if (StringUtil.isNotBlank(getFieldsParam)) {
                getFields.addAll(Arrays.asList(getFieldsParam.split("\\.")));
            }
            // DTD
            if ("xml".equals(request.getParameter("output"))) {
                xmlDtd = true;
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
            final String start = request.getParameter("start");
            long startNumber = 1;
            if (StringUtil.isNotBlank(start)) {
                startNumber = Long.parseLong(start) + 1;
            }
            long endNumber = startNumber + data.getPageSize() - 1;
            if (endNumber > allRecordCount) {
                endNumber = allRecordCount;
            }

            buf.append("<Q>");
            buf.append(escapeXml(query));
            buf.append("</Q>");
            buf.append("<TM>");
            buf.append(execTime);
            buf.append("</TM>");
            for (final Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                final String[] values = entry.getValue();
                if (values == null) {
                    continue;
                }
                final String key = entry.getKey();
                for (final String value : values) {
                    buf.append("<PARAM name=\"");
                    buf.append(key);
                    buf.append("\" value=\"");
                    buf.append(value);
                    buf.append("\" original_value=\"");
                    buf.append(URLEncoder.encode(value, Constants.UTF_8));
                    buf.append("\"/>");
                }
            }
            buf.append("<PARAM name=\"ie\" value=\"");
            buf.append(ie);
            buf.append("\" original_value=\"");
            buf.append(URLEncoder.encode(ie, Constants.UTF_8));
            buf.append("\"/>");
            buf.append("<PARAM name=\"oe\" value=\"");
            buf.append(oe);
            buf.append("\" original_value=\"");
            buf.append(URLEncoder.encode(ie, Constants.UTF_8));
            buf.append("\"/>");
            buf.append("<PARAM name=\"ip\" value=\"");
            buf.append(ip);
            buf.append("\" original_value=\"");
            buf.append(URLEncoder.encode(ie, Constants.UTF_8));
            buf.append("\"/>");
            if (!documentItems.isEmpty()) {
                buf.append("<RES SN=\"");
                buf.append(startNumber);
                buf.append("\" EN=\"");
                buf.append(endNumber);
                buf.append("\">");
                buf.append("<M>");
                buf.append(allRecordCount);
                buf.append("</M>");
                if (endNumber < allRecordCount) {
                    buf.append("<NB>");
                    buf.append("<NU>");
                    buf.append(escapeXml(uriQueryString.replaceFirst("start=([^&]+)", "start=" + endNumber)));
                    buf.append("</NU>");
                    buf.append("</NB>");
                }
                long recordNumber = startNumber;
                for (final Map<String, Object> document : documentItems) {
                    buf.append("<R N=\"");
                    buf.append(recordNumber);
                    buf.append("\">");
                    final String url = DocumentUtil.getValue(document, fessConfig.getIndexFieldUrl(), String.class);
                    document.remove(fessConfig.getIndexFieldUrl());
                    document.put("UE", url);
                    document.put("U", URLDecoder.decode(url, Constants.UTF_8));
                    document.put("T", DocumentUtil.getValue(document, fessConfig.getIndexFieldTitle(), String.class));
                    document.remove(fessConfig.getIndexFieldTitle());
                    final float score = DocumentUtil.getValue(document, fessConfig.getIndexFieldBoost(), Float.class, Float.valueOf(0));
                    document.remove(fessConfig.getIndexFieldBoost());
                    document.put("RK", (int) (score * 10));
                    document.put("S",
                            DocumentUtil
                                    .getValue(document, fessConfig.getResponseFieldContentDescription(), String.class, StringUtil.EMPTY)
                                    .replaceAll("<(/*)em>", "<$1b>"));
                    document.remove(fessConfig.getResponseFieldContentDescription());
                    final String lang = DocumentUtil.getValue(document, fessConfig.getIndexFieldLang(), String.class);
                    if (StringUtil.isNotBlank(lang)) {
                        document.put("LANG", lang);
                        document.remove(fessConfig.getIndexFieldLang());
                    }
                    for (final Map.Entry<String, Object> entry : document.entrySet()) {
                        final String name = entry.getKey();
                        if (StringUtil.isNotBlank(name) && entry.getValue() != null && fessConfig.isGsaResponseFields(name)) {
                            if (name.startsWith(gsaMetaPrefix)) {
                                final String tagName =
                                        name.replaceFirst("^" + gsaMetaPrefix, StringUtil.EMPTY).replaceAll(GSA_META_SUFFIX + "\\z",
                                                StringUtil.EMPTY);
                                if (getFields != null && getFields.contains(tagName)) {
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
                    buf.append("<HAS>");
                    buf.append("<L/>");
                    buf.append("<C SZ=\"");
                    buf.append(DocumentUtil.getValue(document, fessConfig.getIndexFieldContentLength(), Long.class, Long.valueOf(0))
                            .longValue() / 1000);
                    document.remove(fessConfig.getIndexFieldContentLength());
                    buf.append("k\" CID=\"");
                    buf.append(DocumentUtil.getValue(document, fessConfig.getIndexFieldDocId(), String.class));
                    document.remove(fessConfig.getIndexFieldDocId());
                    buf.append("\" ENC=\"");
                    String charset = DocumentUtil.getValue(document, charsetField, String.class);
                    document.remove(charsetField);
                    if (StringUtil.isBlank(charset)) {
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
            if (e instanceof InvalidAccessTokenException) {
                final InvalidAccessTokenException iate = (InvalidAccessTokenException) e;
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

    protected static class GsaRequestParams implements SearchRequestParams {

        private final HttpServletRequest request;

        private final FessConfig fessConfig;

        private int startPosition = -1;

        private int pageSize = -1;

        protected GsaRequestParams(final HttpServletRequest request, final FessConfig fessConfig) {
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
            return SearchRequestType.GSA;
        }

        @Override
        public String getSimilarDocHash() {
            return request.getParameter("sdh");
        }

    }

    public void setGsaPathPrefix(final String gsaPathPrefix) {
        this.gsaPathPrefix = gsaPathPrefix;
    }

    public void setGsaMetaPrefix(final String gsaMetaPrefix) {
        this.gsaMetaPrefix = gsaMetaPrefix;
    }

    public void setCharsetField(String charsetField) {
        this.charsetField = charsetField;
    }

    public void setContentTypeField(String contentTypeField) {
        this.contentTypeField = contentTypeField;
    }
}
