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

package jp.sf.fess.api.xml;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import org.apache.commons.lang.StringEscapeUtils;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlApiManager extends BaseApiManager implements WebApiManager {
    private static final Logger logger = LoggerFactory
            .getLogger(XmlApiManager.class);

    protected String xmlPathPrefix = "/xml";

    @Override
    public boolean matches(final HttpServletRequest request) {
        if (Constants.FALSE.equals(getCrawlerProperties().getProperty(
                Constants.WEB_API_XML_PROPERTY, Constants.TRUE))) {
            return false;
        }

        final String servletPath = request.getServletPath();
        return servletPath.startsWith(xmlPathPrefix);
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
        default:
            writeXmlResponse(-1, Constants.EMPTY_STRING, "Not found.");
            break;
        }

    }

    protected void processSearchRequest(final HttpServletRequest request,
            final HttpServletResponse response, final FilterChain chain) {
        int status = 0;
        String errMsg = Constants.EMPTY_STRING;
        final StringBuilder buf = new StringBuilder(1000);
        String query = null;
        request.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE,
                CDef.AccessType.Xml);
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

            buf.append("<query>");
            buf.append(escapeXml(query));
            buf.append("</query>");
            buf.append("<exec-time>");
            buf.append(execTime);
            buf.append("</exec-time>");
            if (StringUtil.isNotBlank(queryId)) {
                buf.append("<query-id>");
                buf.append(escapeXml(queryId));
                buf.append("</query-id>");
            }
            buf.append("<page-size>");
            buf.append(pageSize);
            buf.append("</page-size>");
            buf.append("<page-number>");
            buf.append(currentPageNumber);
            buf.append("</page-number>");
            buf.append("<record-count>");
            buf.append(allRecordCount);
            buf.append("</record-count>");
            buf.append("<page-count>");
            buf.append(allPageCount);
            buf.append("</page-count>");
            buf.append("<result>");
            for (final Map<String, Object> document : documentItems) {
                buf.append("<doc>");
                for (final Map.Entry<String, Object> entry : document
                        .entrySet()) {
                    final String name = entry.getKey();
                    if (StringUtil.isNotBlank(name) && entry.getValue() != null
                            && getQueryHelper().isApiResponseField(name)) {
                        final String tagName = convertTagName(name);
                        buf.append('<');
                        buf.append(tagName);
                        buf.append('>');
                        buf.append(escapeXml(entry.getValue()));
                        buf.append("</");
                        buf.append(tagName);
                        buf.append('>');
                    }
                }
                buf.append("</doc>");
            }
            buf.append("</result>");
            if (facetResponse != null && facetResponse.hasFacetResponse()) {
                buf.append("<facet>");
                // facet field
                if (facetResponse.getFieldList() != null) {
                    for (final Field field : facetResponse.getFieldList()) {
                        buf.append("<field name=\"");
                        buf.append(escapeXml(field.getName()));
                        buf.append("\">");
                        for (final Map.Entry<String, Long> entry : field
                                .getValueCountMap().entrySet()) {
                            buf.append("<value count=\"");
                            buf.append(escapeXml(entry.getValue()));
                            buf.append("\">");
                            buf.append(escapeXml(entry.getKey()));
                            buf.append("</value>");
                        }
                        buf.append("</field>");
                    }
                }
                // facet query
                if (facetResponse.getQueryCountMap() != null) {
                    buf.append("<query>");
                    for (final Map.Entry<String, Long> entry : facetResponse
                            .getQueryCountMap().entrySet()) {
                        buf.append("<value count=\"");
                        buf.append(escapeXml(entry.getValue()));
                        buf.append("\">");
                        buf.append(escapeXml(entry.getKey()));
                        buf.append("</value>");
                    }
                    buf.append("</query>");
                }
                buf.append("</facet>");
            }
            if (moreLikeThisResponse != null && !moreLikeThisResponse.isEmpty()) {
                buf.append("<more-like-this>");
                for (final Map.Entry<String, List<Map<String, Object>>> mltEntry : moreLikeThisResponse
                        .entrySet()) {
                    buf.append("<result id=\"");
                    buf.append(escapeXml(mltEntry.getKey()));
                    buf.append("\">");
                    for (final Map<String, Object> document : mltEntry
                            .getValue()) {
                        buf.append("<doc>");
                        for (final Map.Entry<String, Object> entry : document
                                .entrySet()) {
                            if (StringUtil.isNotBlank(entry.getKey())
                                    && entry.getValue() != null) {
                                final String tagName = convertTagName(entry
                                        .getKey());
                                buf.append('<');
                                buf.append(tagName);
                                buf.append('>');
                                buf.append(escapeXml(entry.getValue()
                                        .toString()));
                                buf.append("</");
                                buf.append(tagName);
                                buf.append('>');
                            }
                        }
                        buf.append("</doc>");
                    }
                    buf.append("</result>");
                }
                buf.append("</more-like-this>");
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

        writeXmlResponse(status, buf.toString(), errMsg);
    }

    private String convertTagName(final String name) {
        final String tagName = StringUtil.decamelize(name).replaceAll("_", "-")
                .toLowerCase();
        return tagName;
    }

    protected void processLabelRequest(final HttpServletRequest request,
            final HttpServletResponse response, final FilterChain chain) {
        int status = 0;
        String errMsg = Constants.EMPTY_STRING;
        final StringBuilder buf = new StringBuilder(255);
        try {
            final List<Map<String, String>> labelTypeItems = getLabelTypeHelper()
                    .getLabelTypeItemList();
            buf.append("<record-count>");
            buf.append(labelTypeItems.size());
            buf.append("</record-count>");
            buf.append("<result>");
            for (final Map<String, String> labelMap : labelTypeItems) {
                buf.append("<label>");
                buf.append("<name>");
                buf.append(escapeXml(labelMap.get(Constants.ITEM_LABEL)));
                buf.append("</name>");
                buf.append("<value>");
                buf.append(escapeXml(labelMap.get(Constants.ITEM_VALUE)));
                buf.append("</value>");
                buf.append("</label>");
            }
            buf.append("</result>");
        } catch (final Exception e) {
            status = 1;
            errMsg = e.getMessage();
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a label request.", e);
            }

        }

        writeXmlResponse(status, buf.toString(), errMsg);
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

            buf.append("<record-count>");
            buf.append(suggestRecordCount);
            buf.append("</record-count>");
            if (suggestResultList.size() > 0) {
                buf.append("<result>");

                for (int i = 0; i < suggestResultList.size(); i++) {

                    final SuggestResponse suggestResponse = suggestResultList
                            .get(i);

                    for (final Map.Entry<String, List<String>> entry : suggestResponse
                            .entrySet()) {
                        final SuggestResponseList srList = (SuggestResponseList) entry
                                .getValue();
                        final String fn = suggestFieldName.get(i);
                        final Suggester suggester = getSuggesterManager()
                                .getSuggester(fn);
                        if (suggester != null) {
                            buf.append("<suggest>");
                            buf.append("<token>");
                            buf.append(escapeXml(entry.getKey()));
                            buf.append("</token>");
                            buf.append("<fn>");
                            buf.append(escapeXml(fn));
                            buf.append("</fn>");
                            buf.append("<start-offset>");
                            buf.append(escapeXml(Integer.toString(srList
                                    .getStartOffset())));
                            buf.append("</start-offset>");
                            buf.append("<end-offset>");
                            buf.append(escapeXml(Integer.toString(srList
                                    .getEndOffset())));
                            buf.append("</end-offset>");
                            buf.append("<num-found>");
                            buf.append(escapeXml(Integer.toString(srList
                                    .getNumFound())));
                            buf.append("</num-found>");
                            buf.append("<result>");
                            for (final String value : srList) {
                                buf.append("<value>");
                                buf.append(escapeXml(suggester
                                        .convertResultString(value)));
                                buf.append("</value>");
                            }
                            buf.append("</result>");
                            buf.append("</suggest>");
                        }
                    }
                }
                buf.append("</result>");
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

        writeXmlResponse(status, buf.toString(), errMsg);
    }

    protected String processAnalysisRequest(final HttpServletRequest request,
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

            buf.append("<record-count>");
            buf.append(fieldAnalysis.size());
            buf.append("</record-count>");
            if (fieldAnalysis.size() > 0) {
                buf.append("<result>");
                for (final Map.Entry<String, Map<String, List<Map<String, Object>>>> fEntry : fieldAnalysis
                        .entrySet()) {

                    buf.append("<field name=\"")
                            .append(escapeXml(fEntry.getKey())).append("\">");
                    for (final Map.Entry<String, List<Map<String, Object>>> aEntry : fEntry
                            .getValue().entrySet()) {
                        buf.append("<analysis name=\"")
                                .append(escapeXml(aEntry.getKey()))
                                .append("\">");
                        for (final Map<String, Object> dataMap : aEntry
                                .getValue()) {
                            buf.append("<token>");
                            for (final Map.Entry<String, Object> dEntry : dataMap
                                    .entrySet()) {
                                final String key = dEntry.getKey();
                                final Object value = dEntry.getValue();
                                if (StringUtil.isNotBlank(key) && value != null) {
                                    buf.append("<value name=\"")
                                            .append(escapeXml(key))
                                            .append("\">")
                                            .append(escapeXml(value))
                                            .append("</value>");
                                }
                            }
                            buf.append("</token>");
                        }
                        buf.append("</analysis>");
                    }
                    buf.append("</field>");
                }
                buf.append("</result>");
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

        writeXmlResponse(status, buf.toString(), errMsg);
        return null;
    }

    protected void writeXmlResponse(final int status, final String body,
            final String errMsg) {
        final StringBuilder buf = new StringBuilder(1000);
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buf.append("<response>");
        buf.append("<version>");
        buf.append(Constants.WEB_API_VERSION);
        buf.append("</version>");
        buf.append("<status>");
        buf.append(status);
        buf.append("</status>");
        if (status == 0) {
            buf.append(body);
        } else {
            buf.append("<message>");
            buf.append(escapeXml(errMsg));
            buf.append("</message>");
        }
        buf.append("</response>");
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
                    Constants.DATE_FORMAT_ISO_8601_EXTEND);
            buf.append(StringEscapeUtils.escapeXml(sdf.format(obj)));
        } else if (obj != null) {
            buf.append(StringEscapeUtils.escapeXml(obj.toString()));
        }
        return buf.toString();
    }

    public String getXmlPathPrefix() {
        return xmlPathPrefix;
    }

    public void setXmlPathPrefix(final String xmlPathPrefix) {
        this.xmlPathPrefix = xmlPathPrefix;
    }
}
