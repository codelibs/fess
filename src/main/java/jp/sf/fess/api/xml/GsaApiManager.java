/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package jp.sf.fess.api.xml;

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
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.sf.fess.Constants;
import jp.sf.fess.api.BaseApiManager;
import jp.sf.fess.api.WebApiManager;
import jp.sf.fess.api.WebApiRequest;
import jp.sf.fess.api.WebApiResponse;
import jp.sf.fess.db.allcommon.CDef;
import jp.sf.fess.util.ComponentUtil;
import jp.sf.fess.util.WebApiUtil;

import org.apache.commons.lang.StringEscapeUtils;
import org.codelibs.core.CoreLibConstants;
import org.codelibs.core.util.StringUtil;
import org.seasar.struts.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GsaApiManager extends BaseApiManager implements WebApiManager {
    private static final Logger logger = LoggerFactory
            .getLogger(GsaApiManager.class);

    public String gsaPathPrefix = "/gsa";

    public String gsaMetaPrefix = "MT_";

    private static final String GSA_META_SUFFIX = "_s";

    @Override
    public boolean matches(final HttpServletRequest request) {
        if (Constants.FALSE.equals(ComponentUtil.getCrawlerProperties()
                .getProperty(Constants.WEB_API_XML_PROPERTY, Constants.TRUE))) {
            return false;
        }

        final String servletPath = request.getServletPath();
        return servletPath.startsWith(gsaPathPrefix);
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
            default:
                writeXmlResponse(-1, false, StringUtil.EMPTY, "Not found.");
                break;
        }
    }

    protected void processSearchRequest(final HttpServletRequest request,
            final HttpServletResponse response, final FilterChain chain) {
        int status = 0;
        String errMsg = StringUtil.EMPTY;
        final StringBuilder buf = new StringBuilder(1000);
        String query = null;
        request.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE,
                CDef.AccessType.Xml);
        boolean xmlDtd = false;

        try {
            Map<String, String[]> extraParams = new TreeMap<String, String[]>();
            List<String> additional = new ArrayList<String>();
            final List<String> getFields = new ArrayList<String>();
            StringBuilder requestUri = new StringBuilder(request.getRequestURI());
            if (request.getQueryString() != null) {
                requestUri.append("?").append(request.getQueryString());
            }
            final String uriQueryString = requestUri.toString();
            // DTD
            if ("xml".equals(request.getParameter("output"))) {
                xmlDtd = true;
            }
            // keywords
            query = request.getParameter("q");
            extraParams.put("query", new String[]{request.getParameter("q")});
            // collections
            final String site = (String) request.getParameter("site");
            if (StringUtil.isNotBlank(site)) {
                additional.add(" (label:"
                        + site.replace(".", " AND label:").replace("|",
                                " OR label:") + ")");
            }
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
                    extraParams.put("sort", new String[] { "lastModified."
                            + sortDirection });
                }
            }
            // dynmic fields
            final String requiredFields = (String) request
                    .getParameter("requiredfields");
            if (StringUtil.isNotBlank(requiredFields)) {
                additional.add(" (" + gsaMetaPrefix
                        + requiredFields.replace(":", "_s:")
                                .replace(".", " AND " + gsaMetaPrefix)
                                .replace("|", " OR " + gsaMetaPrefix) + ")");
            }
            if (additional.size() > 0) {
                extraParams.put("additional", (String[]) additional
                        .toArray(new String[additional.size()]));
            }
            // meta tags should be returned
            final String getFieldsParam = (String) request
                    .getParameter("getfields");
            if (StringUtil.isNotBlank(getFieldsParam)) {
                getFields.addAll(Arrays.asList(getFieldsParam.split("\\.")));
            }
            // Input/Output encoding
            final String ie = request.getCharacterEncoding();
            final String oe = "UTF-8";
            // IP address
            final String ip = request.getRemoteAddr();

            chain.doFilter(new WrappedWebApiRequest(request, SEARCH_API, extraParams),
                    new WebApiResponse(response));

            WebApiUtil.validate();
            final String execTime = WebApiUtil.getObject("execTime");
            final int pageSize = Integer.parseInt((String) WebApiUtil.getObject("pageSize"));
            final int allRecordCount = Integer.parseInt((String) WebApiUtil
                    .getObject("allRecordCount"));
            final List<Map<String, Object>> documentItems = WebApiUtil
                    .getObject("documentItems");
            final String start = request.getParameter("start");
            int startNumber = 1;
            if (StringUtil.isNotBlank(start)) {
                startNumber = Integer.parseInt(start) + 1;
            }
            int endNumber = startNumber + pageSize -1;
            if (endNumber > allRecordCount) {
            	endNumber = allRecordCount;
            }

            buf.append("<Q>");
            buf.append(escapeXml(query));
            buf.append("</Q>");
            buf.append("<TM>");
            buf.append(execTime);
            buf.append("</TM>");
            for (final Entry<String, String[]> entry : request
                    .getParameterMap().entrySet()) {
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
            if (documentItems.size() > 0) {
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
                    buf.append(escapeXml(uriQueryString.replaceFirst(
                            "start=([^&]+)",
                            "start=" + endNumber)));
                    buf.append("</NU>");
                    buf.append("</NB>");
                }
                int recordNumber = startNumber;
                for (Map<String, Object> document : documentItems) {
                    buf.append("<R N=\"");
                    buf.append(recordNumber);
                    buf.append("\">");
                    final String url = (String) document.remove("url");
                    document.put("UE", url);
                    document.put("U", URLDecoder.decode(url, Constants.UTF_8));
                    document.put("T", document.remove("title"));
                    float score = (float) document.remove("score");
                    document.put("RK", (int)(score * 10));
                    document.put("S", ((String) document
                            .remove("contentDescription")).replaceAll(
                            "<(/*)em>", "<$1b>"));
                    document.put("LANG", document.remove("lang_s"));
                    for (final Map.Entry<String, Object> entry : document
                            .entrySet()) {
                        final String name = entry.getKey();
                        if (StringUtil.isNotBlank(name)
                                && entry.getValue() != null
                                && ComponentUtil.getQueryHelper()
                                        .isApiResponseField(name)) {
                            if (name.startsWith(gsaMetaPrefix)) {
                                final String tagName = name.replaceAll(
                                        "^" + gsaMetaPrefix, "").replaceAll(
                                        GSA_META_SUFFIX + "\\z", "");
                                if (getFields != null
                                        && getFields.contains(tagName)) {
                                    buf.append("<MT N=\"");
                                    buf.append(tagName);
                                    buf.append("\" V=\"");
                                    buf.append(escapeXml(entry.getValue()
                                            .toString()));
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
                    buf.append((long)document.remove("contentLength")/1000);
                    buf.append("k\" CID=\"");
                    buf.append(document.remove("docId"));
                    buf.append("\" ENC=\"");
                    String charset = (String) document.remove("charset_s");
                    if (StringUtil.isNotBlank(charset)) {
                        buf.append(charset);
                    } else {
                        charset = (String) document.remove("contentType_s");
                        if (StringUtil.isNotBlank(charset)) {
                            Matcher m = Pattern.compile(".*;\\s*charset=(.+)")
                                    .matcher(charset);
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
    
    private String convertTagName(final String name) {
        final String tagName = StringUtil.decamelize(name).replaceAll("_", "-")
                .toLowerCase();
        return tagName;
    }

    protected void writeXmlResponse(final int status, final boolean xmlDtd,
            final String body, final String errMsg) {
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
        ResponseUtil.write(buf.toString(), "text/xml", Constants.UTF_8);

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

                buf.append("<name>").append(escapeXml(entry.getKey()))
                        .append("</name><value>")
                        .append(escapeXml(entry.getValue())).append("</value>");
            }
            buf.append("</data>");
        } else if (obj instanceof Date) {
            final SimpleDateFormat sdf = new SimpleDateFormat(
                    CoreLibConstants.DATE_FORMAT_ISO_8601_EXTEND);
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

        public WrappedWebApiRequest(final HttpServletRequest request,
                final String servletPath,
                final Map<String, String[]> extraParams) {
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
}
