/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
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
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.StreamUtil;
import org.dbflute.optional.OptionalThing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GsaApiManager extends BaseApiManager implements WebApiManager {
    private static final Logger logger = LoggerFactory.getLogger(GsaApiManager.class);

    public String gsaPathPrefix = "/gsa";

    public String gsaMetaPrefix = "MT_";

    private static final String GSA_META_SUFFIX = "_s";

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
        final StringBuilder buf = new StringBuilder(1000); // TODO replace response stream
        request.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE, Constants.SEARCH_LOG_ACCESS_TYPE_XML);
        boolean xmlDtd = false;
        try {
            final SearchRenderData data = new SearchRenderData();
            final SearchApiRequestParams params = new SearchApiRequestParams(request, fessConfig);
            searchService.search(request, params, data, OptionalThing.empty());
            query = params.getQuery();
            final String execTime = data.getExecTime();
            final long allRecordCount = data.getAllRecordCount();
            final List<Map<String, Object>> documentItems = data.getDocumentItems();
            
            final List<String> getFields = new ArrayList<String>();
            // meta tags should be returned
            final String getFieldsParam = (String) request.getParameter("getfields");
            if (StringUtil.isNotBlank(getFieldsParam)) {
                getFields.addAll(Arrays.asList(getFieldsParam.split("\\.")));
            }
            // DTD
            if ("xml".equals(request.getParameter("output"))) {
                xmlDtd = true;
            }
            StringBuilder requestUri = new StringBuilder(request.getRequestURI());
            if (request.getQueryString() != null) {
                requestUri.append("?").append(request.getQueryString());
            }
            final String uriQueryString = requestUri.toString();
            // Input/Output encoding
            final String ie = request.getCharacterEncoding();
            final String oe = "UTF-8";
            // IP address
            final String ip = request.getRemoteAddr();
            final String start = request.getParameter("start");
            long startNumber = 1;
            if (StringUtil.isNotBlank(start)) {
                startNumber = Integer.parseInt(start) + 1;
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
                for (Map<String, Object> document : documentItems) {
                    buf.append("<R N=\"");
                    buf.append(recordNumber);
                    buf.append("\">");
                    final String url = (String) document.remove("url");
                    document.put("UE", url);
                    document.put("U", URLDecoder.decode(url, Constants.UTF_8));
                    document.put("T", document.remove("title"));
                    float score = Float.parseFloat((String) document.remove("boost"));
                    document.put("RK", (int) (score * 10));
                    document.put("S", ((String) document.remove("content_description")).replaceAll("<(/*)em>", "<$1b>"));
                    document.put("LANG", document.remove("lang"));
                    for (final Map.Entry<String, Object> entry : document.entrySet()) {
                        final String name = entry.getKey();
                        if (StringUtil.isNotBlank(name) && entry.getValue() != null
                                && ComponentUtil.getQueryHelper().isApiResponseField(name)) {
                            if (name.startsWith(gsaMetaPrefix)) {
                                final String tagName = name.replaceAll("^" + gsaMetaPrefix, "").replaceAll(GSA_META_SUFFIX + "\\z", "");
                                if (getFields != null && getFields.contains(tagName)) {
                                    buf.append("<MT N=\"");
                                    buf.append(tagName);
                                    buf.append("\" V=\"");
                                    buf.append(escapeXml(entry.getValue().toString()));
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
                    buf.append(Long.parseLong((String) document.remove("content_length")) / 1000);
                    buf.append("k\" CID=\"");
                    buf.append(document.remove("doc_id"));
                    buf.append("\" ENC=\"");
                    String charset = (String) document.remove("charset_s");
                    if (StringUtil.isNotBlank(charset)) {
                        buf.append(charset);
                    } else {
                        charset = (String) document.remove("contentType_s");
                        if (StringUtil.isNotBlank(charset)) {
                            Matcher m = Pattern.compile(".*;\\s*charset=(.+)").matcher(charset);
                            if (m.matches()) {
                                charset = m.group(1);
                                buf.append(charset);
                            }
                        }
                    }
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
        //        buf.append("<status>");
        //        buf.append(status);
        //        buf.append("</status>");
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
            buf.append(StringEscapeUtils.escapeXml(sdf.format(obj)));
        } else if (obj != null) {
            buf.append(StringEscapeUtils.escapeXml(obj.toString()));
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
        private Map<String, String[]> extraParameters;

        public WrappedWebApiRequest(final HttpServletRequest request, final String servletPath, final Map<String, String[]> extraParams) {
            super(request, servletPath);
            extraParameters = extraParams;
        }

        @Override
        public String getParameter(final String name) {
            String[] values = getParameterMap().get(name);
            if (values != null) {
                return values[0];
            }
            return super.getParameter(name);
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            if (parameters == null) {
                parameters = new HashMap<String, String[]>();
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
    protected static class SearchApiRequestParams implements SearchRequestParams {

        private final HttpServletRequest request;

        private final FessConfig fessConfig;

        private int startPosition = -1;

        private int pageSize = -1;

        protected SearchApiRequestParams(final HttpServletRequest request, final FessConfig fessConfig) {
            this.request = request;
            this.fessConfig = fessConfig;
        }

        private String[] simplifyArray(String[] values) {
            return StreamUtil.of(values).filter(q -> StringUtil.isNotBlank(q)).distinct().toArray(n -> new String[n]);
        }

        private String[] getParamValueArray(String param) {
            return simplifyArray(request.getParameterValues(param));
        }

        @Override
        public String getQuery() {
            return request.getParameter("q");
        }

        @Override
        public String[] getExtraQueries() {
            /*
            List<String> additional = new ArrayList<String>();
            // collections
            final String site = (String) request.getParameter("site");
            if (StringUtil.isNotBlank(site)) {
                additional.add(" (label:" + site.replace(".", " AND label:").replace("|", " OR label:") + ")");
            }
            // dynmic fields
            final String requiredFields = (String) request.getParameter("requiredfields");
            if (StringUtil.isNotBlank(requiredFields)) {
                additional.add(" (" + gsaMetaPrefix
                        + requiredFields.replace(":", "_s:").replace(".", " AND " + gsaMetaPrefix).replace("|", " OR " + gsaMetaPrefix)
                        + ")");
            }
            if (!additional.isEmpty()) {
                extraParams.put("additional", (String[]) additional.toArray(new String[additional.size()]));
            }
            */
            return getParamValueArray("ex_q");
        }

        @Override
        public Map<String, String[]> getFields() {
            Map<String, String[]> fields = new HashMap<>();
            for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("fields.")) {
                    String[] value = simplifyArray(entry.getValue());
                    fields.put(key.substring("fields.".length()), value);
                }
            }
            return fields;
        }

        @Override
        public String[] getLanguages() {
            return getParamValueArray("lang");
        }

        @Override
        public GeoInfo getGeoInfo() {
            return null;
        }

        @Override
        public FacetInfo getFacetInfo() {
            FacetInfo facetInfo = new FacetInfo();
            facetInfo.field = getParamValueArray("facet.field");
            facetInfo.query = getParamValueArray("facet.query");
            return facetInfo;
        }

        @Override
        public String getSort() {
            /*
            // sorting
            final String sort = request.getParameter("sort");
            if (StringUtil.isNotBlank(sort)) {
                final String[] sortParams = sort.split(":");
                String sortDirection = "desc";
                if ("date".equals(sortParams[0])) {
                    if ("A".equals(sortParams[1])) {
                        sortDirection = "asc";
                    } else if ("D".equals(sortParams[1])) {
                        sortDirection = "desc";
                    }
                    // TODO: Now ignore sort mode
                    //                    if ("S".equals(sortParams[2])) {
                    //                    } else if ("R".equals(sortParams[2])) {
                    //                    } else if ("L".equals(sortParams[2])) {
                    //                    }
                    // sortParams[3]=<mode> is fixed as "d1"
                    extraParams.put("sort", new String[] { "lastModified." + sortDirection });
                }
            }
            */
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
        public boolean isAdministrativeAccess() {
            return false;
        }

    }
}
