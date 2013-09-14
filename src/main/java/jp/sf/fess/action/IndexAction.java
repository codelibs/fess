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

package jp.sf.fess.action;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jp.sf.fess.Constants;
import jp.sf.fess.InvalidQueryException;
import jp.sf.fess.ResultOffsetExceededException;
import jp.sf.fess.UnsupportedSearchException;
import jp.sf.fess.db.allcommon.CDef;
import jp.sf.fess.db.exentity.ClickLog;
import jp.sf.fess.db.exentity.SearchLog;
import jp.sf.fess.db.exentity.UserInfo;
import jp.sf.fess.entity.FieldAnalysisResponse;
import jp.sf.fess.entity.SuggestResponse;
import jp.sf.fess.entity.SuggestResponse.SuggestResponseList;
import jp.sf.fess.form.IndexForm;
import jp.sf.fess.helper.BrowserTypeHelper;
import jp.sf.fess.helper.HotSearchWordHelper;
import jp.sf.fess.helper.HotSearchWordHelper.Range;
import jp.sf.fess.helper.LabelTypeHelper;
import jp.sf.fess.helper.OpenSearchHelper;
import jp.sf.fess.helper.QueryHelper;
import jp.sf.fess.helper.SearchLogHelper;
import jp.sf.fess.helper.UserInfoHelper;
import jp.sf.fess.helper.ViewHelper;
import jp.sf.fess.screenshot.ScreenShotManager;
import jp.sf.fess.service.FavoriteLogService;
import jp.sf.fess.service.SearchService;
import jp.sf.fess.suggest.Suggester;
import jp.sf.fess.suggest.SuggesterManager;
import jp.sf.fess.util.FacetResponse;
import jp.sf.fess.util.FacetResponse.Field;
import jp.sf.fess.util.MoreLikeThisResponse;
import jp.sf.fess.util.QueryResponseList;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.codelibs.core.util.DynamicProperties;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.codelibs.solr.lib.exception.SolrLibQueryException;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.framework.exception.IORuntimeException;
import org.seasar.framework.util.InputStreamUtil;
import org.seasar.framework.util.OutputStreamUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.taglib.S2Functions;
import org.seasar.struts.util.MessageResourcesUtil;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.ResponseUtil;
import org.seasar.struts.util.URLEncoderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexAction {
    private static final String REDIRECT_TO_INDEX = "index?redirect=true";

    private static final String LABEL_FIELD = "label";

    private static final Logger logger = LoggerFactory
            .getLogger(IndexAction.class);

    protected static final long DEFAULT_START_COUNT = 0;

    protected static final int DEFAULT_PAGE_SIZE = 20;

    protected static final int DEFAULT_SUGGEST_PAGE_SIZE = 5;

    protected static final int MAX_PAGE_SIZE = 100;

    protected static final Pattern FIELD_EXTRACTION_PATTERN = Pattern
            .compile("^([a-zA-Z0-9_]+):.*");

    @ActionForm
    @Resource
    protected IndexForm indexForm;

    @Resource
    protected SearchService searchService;

    @Resource
    protected FavoriteLogService favoriteLogService;

    @Binding(bindingType = BindingType.MAY)
    @Resource
    protected ScreenShotManager screenShotManager;

    @Resource
    protected BrowserTypeHelper browserTypeHelper;

    @Resource
    protected LabelTypeHelper labelTypeHelper;

    @Resource
    protected ViewHelper viewHelper;

    @Resource
    protected QueryHelper queryHelper;

    @Resource
    protected UserInfoHelper userInfoHelper;

    @Resource
    protected OpenSearchHelper openSearchHelper;

    @Resource
    protected DynamicProperties crawlerProperties;

    @Resource
    protected HttpServletRequest request;

    @Resource
    protected HttpServletResponse response;

    @Resource
    protected SuggesterManager suggesterManager;

    public List<Map<String, Object>> documentItems;

    public FacetResponse facetResponse;

    public MoreLikeThisResponse moreLikeThisResponse;

    public String pageSize;

    public String currentPageNumber;

    public String allRecordCount;

    public String allPageCount;

    public boolean existNextPage;

    public boolean existPrevPage;

    public String currentStartRecordNumber;

    public String currentEndRecordNumber;

    public List<String> pageNumberList;

    public String execTime;

    public boolean partialResults;

    public List<Map<String, String>> labelTypeItems;

    public String errorMessage;

    protected String pagingQuery = null;

    public boolean favoriteSupport;

    public boolean screenShotSupport;

    public String getPagingQuery() {
        if (pagingQuery == null) {
            final StringBuilder buf = new StringBuilder();
            if (indexForm.additional != null) {
                final Set<String> fieldSet = new HashSet<String>();
                for (final String additional : indexForm.additional) {
                    if (StringUtil.isNotBlank(additional)
                            && additional.length() < 1000
                            && !hasFieldInQuery(fieldSet, additional)) {
                        buf.append("&additional=").append(
                                S2Functions.u(additional));
                    }
                }
            }
            if (StringUtil.isNotBlank(indexForm.sort)) {
                buf.append("&sort=").append(S2Functions.u(indexForm.sort));
            }
            if (!indexForm.fields.isEmpty()) {
                for (final Map.Entry<String, String[]> entry : indexForm.fields
                        .entrySet()) {
                    final String[] values = entry.getValue();
                    if (values != null) {
                        for (final String v : values) {
                            if (StringUtil.isNotBlank(v)) {
                                buf.append("&fields.")
                                        .append(S2Functions.u(entry.getKey()))
                                        .append('=').append(S2Functions.u(v));
                            }
                        }
                    }
                }
            }

            pagingQuery = buf.toString();
        }
        return pagingQuery;
    }

    @Execute(validator = false, input = "index.jsp")
    public String index() {
        if (isMobile()) {
            return "/mobile/?redirect=true";
        }

        buildViewParams();
        buildInitParams();

        return "index.jsp";
    }

    protected String doSearch() {
        if (isMobile()) {
            return "/mobile/?redirect=true";
        }

        if (StringUtil.isBlank(indexForm.query)) {
            try {
                final String optionQuery = queryHelper
                        .buildOptionQuery(indexForm.options);
                indexForm.query = optionQuery;
            } catch (final InvalidQueryException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug(e.getMessage(), e);
                }
                throw new SSCActionMessagesException(e, e.getMessageCode());
            }
        }

        if (StringUtil.isBlank(indexForm.query) && indexForm.fields.isEmpty()) {
            // redirect to index page
            indexForm.query = null;
            return REDIRECT_TO_INDEX;
        }

        updateSearchParams(false);
        doSearchInternal();
        buildViewParams();

        return "search.jsp";
    }

    @Execute(validator = true, input = "index")
    public String go() throws IOException {
        if (Constants.TRUE.equals(crawlerProperties.getProperty(
                Constants.SEARCH_LOG_PROPERTY, Constants.TRUE))) {
            final String userSessionId = userInfoHelper.getUserCode();
            if (userSessionId != null) {
                final SearchLogHelper searchLogHelper = SingletonS2Container
                        .getComponent(SearchLogHelper.class);
                final ClickLog clickLog = new ClickLog();
                clickLog.setUrl(indexForm.u);
                clickLog.setRequestedTime(new Timestamp(System
                        .currentTimeMillis()));
                clickLog.setQueryRequestedTime(new Timestamp(Long
                        .parseLong(indexForm.rt)));
                clickLog.setUserSessionId(userSessionId);
                searchLogHelper.addClickLog(clickLog);
            }
        }
        if (indexForm.u.startsWith("file:")) {
            if (Constants.TRUE.equals(crawlerProperties.getProperty(
                    Constants.SEARCH_DESKTOP_PROPERTY, Constants.FALSE))) {
                final String path = indexForm.u.replaceFirst("file:/+", "//");
                final File file = new File(path);
                if (!file.exists()) {
                    errorMessage = MessageResourcesUtil.getMessage(RequestUtil
                            .getRequest().getLocale(),
                            "errors.not_found_on_file_system", indexForm.u);
                    return "error.jsp";
                }
                final Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.open(file);
                } catch (final Exception e) {
                    errorMessage = MessageResourcesUtil.getMessage(RequestUtil
                            .getRequest().getLocale(),
                            "errors.could_not_open_on_system", indexForm.u);
                    logger.warn("Could not open " + path, e);
                    return "error.jsp";
                }

                ResponseUtil.getResponse().setStatus(
                        HttpServletResponse.SC_NO_CONTENT);
                return null;
            } else if (Constants.TRUE.equals(crawlerProperties.getProperty(
                    Constants.SEARCH_FILE_LAUNCHER_PROPERTY, Constants.TRUE))) {
                ResponseUtil.getResponse().sendRedirect(
                        RequestUtil.getRequest().getContextPath()
                                + "/applet/launcher?uri="
                                + S2Functions.u(indexForm.u));
            } else {
                ResponseUtil.getResponse().sendRedirect(indexForm.u);
            }
        } else {
            ResponseUtil.getResponse().sendRedirect(indexForm.u);
        }
        return null;
    }

    protected String doSearchInternal() {
        final StringBuilder queryBuf = new StringBuilder(255);
        if (StringUtil.isNotBlank(indexForm.query)) {
            queryBuf.append(indexForm.query);
        }
        if (!indexForm.fields.isEmpty()) {
            for (final Map.Entry<String, String[]> entry : indexForm.fields
                    .entrySet()) {
                final List<String> valueList = new ArrayList<String>();
                final String[] values = entry.getValue();
                if (values != null) {
                    for (final String v : values) {
                        valueList.add(v);
                    }
                }
                if (valueList.size() == 1) {
                    queryBuf.append(' ').append(entry.getKey()).append(":\"")
                            .append(valueList.get(0)).append('\"');
                } else if (valueList.size() > 1) {
                    queryBuf.append(" (");
                    for (int i = 0; i < valueList.size(); i++) {
                        if (i != 0) {
                            queryBuf.append(" OR");
                        }
                        queryBuf.append(' ').append(entry.getKey())
                                .append(":\"").append(valueList.get(i))
                                .append('\"');
                    }
                    queryBuf.append(')');
                }

            }
        }
        if (StringUtil.isNotBlank(indexForm.sort)) {
            queryBuf.append(" sort:").append(indexForm.sort);
        }
        if (indexForm.additional != null) {
            final Set<String> fieldSet = new HashSet<String>();
            for (final String additional : indexForm.additional) {
                if (StringUtil.isNotBlank(additional)
                        && additional.length() < 1000
                        && !hasFieldInQuery(fieldSet, additional)) {
                    queryBuf.append(' ').append(additional);
                }
            }
        }

        final String query = queryBuf.toString().trim();

        // init pager
        if (StringUtil.isBlank(indexForm.start)) {
            indexForm.start = String.valueOf(DEFAULT_START_COUNT);
        } else {
            try {
                Long.parseLong(indexForm.start);
            } catch (final NumberFormatException e) {
                indexForm.start = String.valueOf(DEFAULT_START_COUNT);
            }
        }
        if (StringUtil.isBlank(indexForm.num)) {
            if (viewHelper.isUseSession()) {
                final HttpSession session = request.getSession(false);
                if (session != null) {
                    final Object resultsPerPage = session
                            .getAttribute(Constants.RESULTS_PER_PAGE);
                    if (resultsPerPage != null) {
                        indexForm.num = resultsPerPage.toString();
                    }
                }
            } else {
                indexForm.num = String.valueOf(getDefaultPageSize());
            }
        }
        normalizePageNum();

        final int pageStart = Integer.parseInt(indexForm.start);
        final int pageNum = Integer.parseInt(indexForm.num);
        try {
            documentItems = searchService.selectList(query, indexForm.facet,
                    pageStart, pageNum, indexForm.geo, indexForm.mlt);
        } catch (final SolrLibQueryException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            throw new SSCActionMessagesException(e,
                    "errors.invalid_query_unknown");
        } catch (final InvalidQueryException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            throw new SSCActionMessagesException(e, e.getMessageCode());
        } catch (final ResultOffsetExceededException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            throw new SSCActionMessagesException(e,
                    "errors.result_size_exceeded");
        }
        // search
        final QueryResponseList queryResponseList = (QueryResponseList) documentItems;
        facetResponse = queryResponseList.getFacetResponse();
        moreLikeThisResponse = queryResponseList.getMoreLikeThisResponse();
        final NumberFormat nf = NumberFormat.getInstance(RequestUtil
                .getRequest().getLocale());
        nf.setMaximumIntegerDigits(2);
        nf.setMaximumFractionDigits(2);
        try {
            execTime = nf
                    .format((double) queryResponseList.getExecTime() / 1000);
        } catch (final Exception e) {
            // ignore
        }

        final long rt = System.currentTimeMillis();
        indexForm.rt = Long.toString(rt);

        // favorite
        favoriteSupport = Constants.TRUE.equals(crawlerProperties.getProperty(
                Constants.USER_FAVORITE_PROPERTY, Constants.FALSE));
        if (favoriteSupport || screenShotManager != null) {
            indexForm.queryId = userInfoHelper.generateQueryId(query,
                    documentItems);
            if (screenShotManager != null) {
                screenShotManager
                        .storeRequest(indexForm.queryId, documentItems);
                screenShotSupport = true;
            }
        }

        // search log
        if (Constants.TRUE.equals(crawlerProperties.getProperty(
                Constants.SEARCH_LOG_PROPERTY, Constants.TRUE))) {
            final Timestamp now = new Timestamp(rt);

            final SearchLogHelper searchLogHelper = SingletonS2Container
                    .getComponent(SearchLogHelper.class);
            final SearchLog searchLog = new SearchLog();

            String userCode = null;
            if (Constants.TRUE.equals(crawlerProperties.getProperty(
                    Constants.USER_INFO_PROPERTY, Constants.TRUE))) {
                userCode = userInfoHelper.getUserCode();
                if (StringUtil.isNotBlank(userCode)) {
                    final UserInfo userInfo = new UserInfo();
                    userInfo.setCode(userCode);
                    userInfo.setCreatedTime(now);
                    userInfo.setUpdatedTime(now);
                    searchLog.setUserInfo(userInfo);
                }
            }

            searchLog.setHitCount(queryResponseList.getAllRecordCount());
            searchLog.setResponseTime(Integer.valueOf((int) queryResponseList
                    .getExecTime()));
            searchLog.setSearchWord(StringUtils.abbreviate(query, 1000));
            searchLog.setSearchQuery(StringUtils.abbreviate(
                    queryResponseList.getSearchQuery(), 1000));
            searchLog.setSolrQuery(StringUtils.abbreviate(
                    queryResponseList.getSolrQuery(), 1000));
            searchLog.setRequestedTime(now);
            searchLog.setQueryOffset(pageStart);
            searchLog.setQueryPageSize(pageNum);

            searchLog.setClientIp(StringUtils.abbreviate(
                    request.getRemoteAddr(), 50));
            searchLog.setReferer(StringUtils.abbreviate(
                    request.getHeader("referer"), 1000));
            searchLog.setUserAgent(StringUtils.abbreviate(
                    request.getHeader("user-agent"), 255));
            if (userCode != null) {
                searchLog.setUserSessionId(userCode);
            }
            final Object accessType = request
                    .getAttribute(Constants.SEARCH_LOG_ACCESS_TYPE);
            if (accessType instanceof CDef.AccessType) {
                searchLog.setAccessType(((CDef.AccessType) accessType).code());
            } else {
                searchLog.setAccessType(CDef.AccessType.Web.code());
            }

            @SuppressWarnings("unchecked")
            final Map<String, List<String>> fieldLogMap = (Map<String, List<String>>) request
                    .getAttribute(Constants.FIELD_LOGS);
            if (fieldLogMap != null) {
                for (final Map.Entry<String, List<String>> logEntry : fieldLogMap
                        .entrySet()) {
                    for (final String value : logEntry.getValue()) {
                        searchLog.addSearchFieldLogValue(logEntry.getKey(),
                                StringUtils.abbreviate(value, 1000));
                    }
                }
            }

            searchLogHelper.addSearchLog(searchLog);
        }

        Beans.copy(documentItems, this)
                .includes("pageSize", "currentPageNumber", "allRecordCount",
                        "allPageCount", "existNextPage", "existPrevPage",
                        "currentStartRecordNumber", "currentEndRecordNumber",
                        "pageNumberList", "partialResults").execute();

        return query;
    }

    protected void updateSearchParams(final boolean isAPI) {
        if (indexForm.facet == null && !isAPI) {
            indexForm.facet = queryHelper.getDefaultFacetInfo();
        }

        if (indexForm.mlt == null && !isAPI) {
            indexForm.mlt = queryHelper.getDefaultMoreLikeThisInfo();
        }

        if (indexForm.geo == null && !isAPI) {
            indexForm.geo = queryHelper.getDefaultGeoInfo();
        }
    }

    @Execute(validator = false, input = "index")
    public String search() {
        if (viewHelper.isUseSession() && StringUtil.isNotBlank(indexForm.num)) {
            normalizePageNum();
            final HttpSession session = request.getSession();
            if (session != null) {
                session.setAttribute(Constants.RESULTS_PER_PAGE, indexForm.num);
            }
        }

        return doSearch();
    }

    @Execute(validator = false, input = "index")
    public String prev() {
        return doMove(-1);
    }

    @Execute(validator = false, input = "index")
    public String next() {
        return doMove(1);
    }

    @Execute(validator = false, input = "index")
    public String move() {
        return doMove(0);
    }

    protected String doMove(final int move) {
        int pageNum = getDefaultPageSize();
        if (StringUtil.isBlank(indexForm.num)) {
            indexForm.num = String.valueOf(getDefaultPageSize());
        } else {
            try {
                pageNum = Integer.parseInt(indexForm.num);
            } catch (final NumberFormatException e) {
                indexForm.num = String.valueOf(getDefaultPageSize());
            }
        }

        if (StringUtil.isBlank(indexForm.pn)) {
            indexForm.start = String.valueOf(DEFAULT_START_COUNT);
        } else {
            Integer pageNumber = Integer.parseInt(indexForm.pn);
            if (pageNumber != null && pageNumber > 0) {
                pageNumber = pageNumber + move;
                if (pageNumber < 1) {
                    pageNumber = 1;
                }
                indexForm.start = String.valueOf((pageNumber - 1) * pageNum);
            } else {
                indexForm.start = String.valueOf(DEFAULT_START_COUNT);
            }
        }

        return doSearch();
    }

    @Execute(validator = false)
    public String xml() {
        if (Constants.FALSE.equals(crawlerProperties.getProperty(
                Constants.WEB_API_XML_PROPERTY, Constants.TRUE))) {
            return REDIRECT_TO_INDEX;
        }

        switch (getFormatType()) {
        case SEARCH:
            return searchByXml();
        case LABEL:
            return labelByXml();
        case SUGGEST:
            return suggestByXml();
        case ANALYSIS:
            return analysisByXml();
        default:
            writeXmlResponse(-1, Constants.EMPTY_STRING, "Not found.");
            return null;
        }
    }

    protected String searchByXml() {
        int status = 0;
        String errMsg = Constants.EMPTY_STRING;
        final StringBuilder buf = new StringBuilder(1000);
        String query = null;
        request.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE,
                CDef.AccessType.Xml);
        updateSearchParams(true);
        try {
            query = doSearchInternal();
            buf.append("<query>");
            buf.append(StringEscapeUtils.escapeXml(query));
            buf.append("</query>");
            buf.append("<exec-time>");
            buf.append(execTime);
            buf.append("</exec-time>");
            if (StringUtil.isNotBlank(indexForm.queryId)) {
                buf.append("<query-id>");
                buf.append(indexForm.queryId);
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
                    if (StringUtil.isNotBlank(entry.getKey())
                            && entry.getValue() != null) {
                        final String tagName = StringUtil
                                .decamelize(entry.getKey())
                                .replaceAll("_", "-").toLowerCase();
                        buf.append('<');
                        buf.append(tagName);
                        buf.append('>');
                        buf.append(StringEscapeUtils.escapeXml(entry.getValue()
                                .toString()));
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
                        buf.append(StringEscapeUtils.escapeXml(field.getName()));
                        buf.append("\">");
                        for (final Map.Entry<String, Long> entry : field
                                .getValueCountMap().entrySet()) {
                            buf.append("<value count=\"");
                            buf.append(entry.getValue());
                            buf.append("\">");
                            buf.append(StringEscapeUtils.escapeXml(entry
                                    .getKey()));
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
                        buf.append(entry.getValue());
                        buf.append("\">");
                        buf.append(StringEscapeUtils.escapeXml(entry.getKey()));
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
                    buf.append(StringEscapeUtils.escapeXml(mltEntry.getKey()));
                    buf.append("\">");
                    for (final Map<String, Object> document : mltEntry
                            .getValue()) {
                        buf.append("<doc>");
                        for (final Map.Entry<String, Object> entry : document
                                .entrySet()) {
                            if (StringUtil.isNotBlank(entry.getKey())
                                    && entry.getValue() != null) {
                                final String tagName = StringUtil
                                        .decamelize(entry.getKey())
                                        .replaceAll("_", "-").toLowerCase();
                                buf.append('<');
                                buf.append(tagName);
                                buf.append('>');
                                buf.append(StringEscapeUtils.escapeXml(entry
                                        .getValue().toString()));
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
        }

        writeXmlResponse(status, buf.toString(), errMsg);
        return null;
    }

    protected String labelByXml() {
        int status = 0;
        String errMsg = Constants.EMPTY_STRING;
        final StringBuilder buf = new StringBuilder(255);
        try {
            labelTypeItems = labelTypeHelper.getLabelTypeItemList();
            buf.append("<record-count>");
            buf.append(labelTypeItems.size());
            buf.append("</record-count>");
            buf.append("<result>");
            for (final Map<String, String> labelMap : labelTypeItems) {
                buf.append("<label>");
                buf.append("<name>");
                buf.append(StringEscapeUtils.escapeXml(labelMap
                        .get(Constants.ITEM_LABEL)));
                buf.append("</name>");
                buf.append("<value>");
                buf.append(StringEscapeUtils.escapeXml(labelMap
                        .get(Constants.ITEM_VALUE)));
                buf.append("</value>");
                buf.append("</label>");
            }
            buf.append("</result>");
        } catch (final Exception e) {
            status = 1;
            errMsg = e.getMessage();
        }

        writeXmlResponse(status, buf.toString(), errMsg);
        return null;
    }

    protected String suggestByXml() {
        if (Constants.FALSE.equals(crawlerProperties.getProperty(
                Constants.WEB_API_SUGGEST_PROPERTY, Constants.TRUE))) {
            writeXmlResponse(9, null, "Unsupported operation.");
            return null;
        }

        if (indexForm.fn == null || indexForm.fn.length == 0) {
            writeXmlResponse(2, null, "The field name is empty.");
            return null;
        }

        if (StringUtil.isBlank(indexForm.query)) {
            writeXmlResponse(3, null, "Your query is empty.");
            return null;
        }

        if (StringUtil.isBlank(indexForm.num)) {
            indexForm.num = String.valueOf(DEFAULT_SUGGEST_PAGE_SIZE);
        }

        int status = 0;
        String errMsg = Constants.EMPTY_STRING;
        final StringBuilder buf = new StringBuilder(255);
        try {
            int num = Integer.parseInt(indexForm.num);
            if (num > getMaxPageSize()) {
                num = getMaxPageSize();
            }

            final String[] fieldNames = indexForm.fn;

            int suggestRecordCount = 0;
            final List<SuggestResponse> suggestResultList = new ArrayList<SuggestResponse>();
            final List<String> suggestFieldName = new ArrayList<String>();

            for (final String fn : fieldNames) {
                final Suggester suggester = suggesterManager.getSuggester(fn);
                if (suggester != null) {
                    final String suggestQuery = suggester
                            .convertQuery(indexForm.query);
                    final SuggestResponse suggestResponse = searchService
                            .getSuggestResponse(fn, suggestQuery, num);

                    if (!suggestResponse.isEmpty()) {
                        suggestRecordCount += suggestResponse.size();

                        suggestResultList.add(suggestResponse);
                        suggestFieldName.add(fn);
                    }
                }
            }

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
                        final Suggester suggester = suggesterManager
                                .getSuggester(fn);
                        if (suggester != null) {
                            buf.append("<suggest>");
                            buf.append("<token>");
                            buf.append(StringEscapeUtils.escapeXml(entry
                                    .getKey()));
                            buf.append("</token>");
                            buf.append("<fn>");
                            buf.append(StringEscapeUtils.escapeXml(fn));
                            buf.append("</fn>");
                            buf.append("<start-offset>");
                            buf.append(StringEscapeUtils.escapeXml(Integer
                                    .toString(srList.getStartOffset())));
                            buf.append("</start-offset>");
                            buf.append("<end-offset>");
                            buf.append(StringEscapeUtils.escapeXml(Integer
                                    .toString(srList.getEndOffset())));
                            buf.append("</end-offset>");
                            buf.append("<num-found>");
                            buf.append(StringEscapeUtils.escapeXml(Integer
                                    .toString(srList.getNumFound())));
                            buf.append("</num-found>");
                            buf.append("<result>");
                            for (final String value : srList) {
                                buf.append("<value>");
                                buf.append(StringEscapeUtils
                                        .escapeXml(suggester
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
            status = 1;
            errMsg = e.getMessage();
        }

        writeXmlResponse(status, buf.toString(), errMsg);
        return null;
    }

    protected String analysisByXml() {
        if (Constants.FALSE.equals(crawlerProperties.getProperty(
                Constants.WEB_API_ANALYSIS_PROPERTY, Constants.TRUE))) {
            writeXmlResponse(9, null, "Unsupported operation.");
            return null;
        }

        if (indexForm.fn == null || indexForm.fn.length == 0) {
            writeXmlResponse(2, null, "The field name is empty.");
            return null;
        }

        if (StringUtil.isBlank(indexForm.query)) {
            writeXmlResponse(3, null, "Your query is empty.");
            return null;
        }

        int status = 0;
        String errMsg = Constants.EMPTY_STRING;
        final StringBuilder buf = new StringBuilder(255);
        try {

            final String[] fieldNames = indexForm.fn;
            final FieldAnalysisResponse fieldAnalysis = searchService
                    .getFieldAnalysisResponse(fieldNames, indexForm.query);

            buf.append("<record-count>");
            buf.append(fieldAnalysis.size());
            buf.append("</record-count>");
            if (fieldAnalysis.size() > 0) {
                buf.append("<result>");
                for (final Map.Entry<String, Map<String, List<Map<String, Object>>>> fEntry : fieldAnalysis
                        .entrySet()) {

                    buf.append("<field name=\"")
                            .append(StringEscapeUtils.escapeXml(fEntry.getKey()))
                            .append("\">");
                    for (final Map.Entry<String, List<Map<String, Object>>> aEntry : fEntry
                            .getValue().entrySet()) {
                        buf.append("<analysis name=\"")
                                .append(StringEscapeUtils.escapeXml(aEntry
                                        .getKey())).append("\">");
                        for (final Map<String, Object> dataMap : aEntry
                                .getValue()) {
                            buf.append("<token>");
                            for (final Map.Entry<String, Object> dEntry : dataMap
                                    .entrySet()) {
                                final String key = dEntry.getKey();
                                final Object value = dEntry.getValue();
                                if (StringUtil.isNotBlank(key) && value != null) {
                                    buf.append("<value name=\"")
                                            .append(StringEscapeUtils
                                                    .escapeXml(key))
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
            status = 1;
            errMsg = e.getMessage();
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
            buf.append(StringEscapeUtils.escapeXml(errMsg));
            buf.append("</message>");
        }
        buf.append("</response>");
        ResponseUtil.write(buf.toString(), "text/xml", Constants.UTF_8);

    }

    @Execute(validator = false)
    public String json() {
        if (Constants.FALSE.equals(crawlerProperties.getProperty(
                Constants.WEB_API_JSON_PROPERTY, Constants.TRUE))) {
            return REDIRECT_TO_INDEX;
        }

        switch (getFormatType()) {
        case SEARCH:
            return searchByJson();
        case LABEL:
            return labelByJson();
        case SUGGEST:
            return suggestByJson();
        case ANALYSIS:
            return analysisByJson();
        default:
            writeJsonResponse(-1, Constants.EMPTY_STRING, "Not found.");
            return null;
        }
    }

    protected String searchByJson() {
        int status = 0;
        String errMsg = Constants.EMPTY_STRING;
        String query = null;
        final StringBuilder buf = new StringBuilder(1000);
        request.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE,
                CDef.AccessType.Json);
        updateSearchParams(true);
        try {
            query = doSearchInternal();
            buf.append("\"query\":\"");
            buf.append(escapeJsonString(query));
            buf.append("\",");
            buf.append("\"execTime\":");
            buf.append(execTime);
            buf.append(',');
            if (StringUtil.isNotBlank(indexForm.queryId)) {
                buf.append("\"queryId\":");
                buf.append(indexForm.queryId);
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
        }

        writeJsonResponse(status, buf.toString(), errMsg);

        return null;
    }

    protected String labelByJson() {
        int status = 0;
        String errMsg = Constants.EMPTY_STRING;
        final StringBuilder buf = new StringBuilder(255);
        try {
            labelTypeItems = labelTypeHelper.getLabelTypeItemList();
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
        }

        writeJsonResponse(status, buf.toString(), errMsg);

        return null;
    }

    protected String suggestByJson() {
        if (Constants.FALSE.equals(crawlerProperties.getProperty(
                Constants.WEB_API_SUGGEST_PROPERTY, Constants.TRUE))) {
            writeJsonResponse(9, null, "Unsupported operation.");
            return null;
        }

        if (indexForm.fn == null || indexForm.fn.length == 0) {
            writeJsonResponse(2, null, "The field name is empty.");
            return null;
        }

        if (StringUtil.isBlank(indexForm.query)) {
            writeJsonResponse(3, null, "Your query is empty.");
            return null;
        }

        if (StringUtil.isBlank(indexForm.num)) {
            indexForm.num = String.valueOf(DEFAULT_SUGGEST_PAGE_SIZE);
        }

        int status = 0;
        String errMsg = Constants.EMPTY_STRING;
        final StringBuilder buf = new StringBuilder(255);
        try {
            int num = Integer.parseInt(indexForm.num);
            if (num > getMaxPageSize()) {
                num = getMaxPageSize();
            }

            final String[] fieldNames = indexForm.fn;

            int suggestRecordCount = 0;
            final List<SuggestResponse> suggestResultList = new ArrayList<SuggestResponse>();
            final List<String> suggestFieldName = new ArrayList<String>();

            for (final String fn : fieldNames) {
                final Suggester suggester = suggesterManager.getSuggester(fn);
                if (suggester != null) {
                    final String suggestQuery = suggester
                            .convertQuery(indexForm.query);
                    final SuggestResponse suggestResponse = searchService
                            .getSuggestResponse(fn, suggestQuery, num);

                    if (!suggestResponse.isEmpty()) {
                        suggestRecordCount += suggestResponse.size();

                        suggestResultList.add(suggestResponse);
                        suggestFieldName.add(fn);
                    }
                }
            }

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
                        final Suggester suggester = suggesterManager
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
            status = 1;
            errMsg = e.getMessage();
        }

        writeJsonResponse(status, buf.toString(), errMsg);

        return null;
    }

    protected String analysisByJson() {
        if (Constants.FALSE.equals(crawlerProperties.getProperty(
                Constants.WEB_API_ANALYSIS_PROPERTY, Constants.TRUE))) {
            writeJsonResponse(9, null, "Unsupported operation.");
            return null;
        }

        if (indexForm.fn == null || indexForm.fn.length == 0) {
            writeJsonResponse(2, null, "The field name is empty.");
            return null;
        }

        if (StringUtil.isBlank(indexForm.query)) {
            writeJsonResponse(3, null, "Your query is empty.");
            return null;
        }

        int status = 0;
        String errMsg = Constants.EMPTY_STRING;
        final StringBuilder buf = new StringBuilder(255);
        try {

            final String[] fieldNames = indexForm.fn;
            final FieldAnalysisResponse fieldAnalysis = searchService
                    .getFieldAnalysisResponse(fieldNames, indexForm.query);

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
            status = 1;
            errMsg = e.getMessage();
        }

        writeJsonResponse(status, buf.toString(), errMsg);

        return null;
    }

    protected void writeJsonResponse(final int status, final String body,
            final String errMsg) {
        final boolean isJsonp = StringUtil.isNotBlank(indexForm.callback);

        final StringBuilder buf = new StringBuilder(1000);
        if (isJsonp) {
            buf.append(escapeCallbackName(indexForm.callback));
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

    protected String escapeXml(final Object obj) {
        final StringBuilder buf = new StringBuilder(255);
        if (obj instanceof List<?>) {
            buf.append("<list>");
            for (final Object child : (List<?>) obj) {
                buf.append("<item>").append(escapeJson(child))
                        .append("</item>");
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
        } else if (obj != null) {
            buf.append(StringEscapeUtils.escapeXml(obj.toString()));
        }
        return buf.toString();
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

    protected boolean isMobile() {
        final String supportedSearch = crawlerProperties.getProperty(
                Constants.SUPPORTED_SEARCH_FEATURE_PROPERTY,
                Constants.SUPPORTED_SEARCH_WEB_MOBILE);
        if (Constants.SUPPORTED_SEARCH_MOBILE.equals(supportedSearch)) {
            return true;
        } else if (Constants.SUPPORTED_SEARCH_NONE.equals(supportedSearch)) {
            throw new UnsupportedSearchException("A search is not supported: "
                    + RequestUtil.getRequest().getRequestURL());
        }

        if (browserTypeHelper == null) {
            return false;
        }

        return browserTypeHelper.isMobile();
    }

    public List<Map<String, String>> getLabelTypeItems() {
        return labelTypeItems;
    }

    public boolean isDisplayLabelTypeItems() {
        if (labelTypeItems != null) {
            return !labelTypeItems.isEmpty();
        } else {
            return false;
        }
    }

    public String getDisplayQuery() {
        final StringBuilder buf = new StringBuilder(100);
        buf.append(indexForm.query);
        if (!indexForm.fields.isEmpty()
                && indexForm.fields.containsKey(LABEL_FIELD)) {
            final String[] values = indexForm.fields.get(LABEL_FIELD);
            final List<String> labelList = new ArrayList<String>();
            if (values != null) {
                for (final String v : values) {
                    labelList.add(v);
                }
            }
            for (final String labelTypeValue : labelList) {
                for (final Map<String, String> map : labelTypeItems) {
                    if (map.get(Constants.ITEM_VALUE).equals(labelTypeValue)) {
                        buf.append(' ');
                        buf.append(map.get(Constants.ITEM_LABEL));
                        break;
                    }
                }
            }
        }
        return buf.toString();
    }

    protected void buildViewParams() {
        // label
        labelTypeItems = labelTypeHelper.getLabelTypeItemList();

        if (!labelTypeItems.isEmpty()
                && !indexForm.fields.containsKey(LABEL_FIELD)) {
            final String[] values = crawlerProperties.getProperty(
                    Constants.DEFAULT_LABEL_VALUE_PROPERTY,
                    Constants.EMPTY_STRING).split("\n");
            if (values != null && values.length > 0) {
                indexForm.fields.put(LABEL_FIELD, values);
            }
        }

        final Map<String, String> labelMap = new LinkedHashMap<String, String>();
        if (!labelTypeItems.isEmpty()) {
            for (final Map<String, String> map : labelTypeItems) {
                labelMap.put(map.get(Constants.ITEM_VALUE),
                        map.get(Constants.ITEM_LABEL));
            }
        }
        request.setAttribute(Constants.LABEL_VALUE_MAP, labelMap);

        if (viewHelper.isUseSession()) {
            final HttpSession session = request.getSession(false);
            if (session != null) {
                final Object resultsPerPage = session
                        .getAttribute(Constants.RESULTS_PER_PAGE);
                if (resultsPerPage != null) {
                    indexForm.num = resultsPerPage.toString();
                }
            }
        }
    }

    protected void buildInitParams() {
        buildInitParamMap(viewHelper.getInitFacetParamMap(),
                Constants.FACET_QUERY, Constants.FACET_FORM);
        buildInitParamMap(viewHelper.getInitMltParamMap(), Constants.MLT_QUERY,
                Constants.MLT_FORM);
        buildInitParamMap(viewHelper.getInitGeoParamMap(), Constants.GEO_QUERY,
                Constants.GEO_FORM);
    }

    protected void buildInitParamMap(final Map<String, String> paramMap,
            final String queryKey, final String formKey) {
        if (!paramMap.isEmpty()) {
            final StringBuilder queryBuf = new StringBuilder(100);
            final StringBuilder formBuf = new StringBuilder(100);
            for (final Map.Entry<String, String> entry : paramMap.entrySet()) {
                queryBuf.append('&');
                queryBuf.append(URLEncoderUtil.encode(entry.getValue()));
                queryBuf.append('=');
                queryBuf.append(URLEncoderUtil.encode(entry.getKey()));
                formBuf.append("<input type=\"hidden\" name=\"");
                formBuf.append(StringEscapeUtils.escapeHtml(entry.getValue()));
                formBuf.append("\" value=\"");
                formBuf.append(StringEscapeUtils.escapeHtml(entry.getKey()));
                formBuf.append("\"/>");
            }
            request.setAttribute(queryKey, queryBuf.toString());
            request.setAttribute(formKey, formBuf.toString());
        }
    }

    protected FormatType getFormatType() {
        if (indexForm.type == null) {
            return FormatType.SEARCH;
        }
        final String type = indexForm.type.toUpperCase();
        if (FormatType.SEARCH.name().equals(type)) {
            return FormatType.SEARCH;
        } else if (FormatType.LABEL.name().equals(type)) {
            return FormatType.LABEL;
        } else if (FormatType.SUGGEST.name().equals(type)) {
            return FormatType.SUGGEST;
        } else if (FormatType.ANALYSIS.name().equals(type)) {
            return FormatType.ANALYSIS;
        } else {
            // default
            return FormatType.SEARCH;
        }
    }

    protected void normalizePageNum() {
        try {
            final int num = Integer.parseInt(indexForm.num);
            if (num > getMaxPageSize()) {
                // max page size
                indexForm.num = String.valueOf(getMaxPageSize());
            } else if (num <= 0) {
                indexForm.num = String.valueOf(getDefaultPageSize());
            }
        } catch (final NumberFormatException e) {
            indexForm.num = String.valueOf(getDefaultPageSize());
        }
    }

    protected int getDefaultPageSize() {
        return DEFAULT_PAGE_SIZE;
    }

    protected int getMaxPageSize() {
        return MAX_PAGE_SIZE;
    }

    private static enum FormatType {
        SEARCH, LABEL, SUGGEST, ANALYSIS;
    }

    @Execute(validator = false)
    public String hotsearchword() {
        if (Constants.FALSE.equals(crawlerProperties.getProperty(
                Constants.WEB_API_HOT_SEARCH_WORD_PROPERTY, Constants.TRUE))) {
            return REDIRECT_TO_INDEX;
        }

        Range range;
        if (indexForm.range == null) {
            range = Range.ENTIRE;
        } else if ("day".equals(indexForm.range) || "1".equals(indexForm.range)) {
            range = Range.ONE_DAY;
        } else if ("week".equals(indexForm.range)
                || "7".equals(indexForm.range)) {
            range = Range.ONE_DAY;
        } else if ("month".equals(indexForm.range)
                || "30".equals(indexForm.range)) {
            range = Range.ONE_DAY;
        } else if ("year".equals(indexForm.range)
                || "365".equals(indexForm.range)) {
            range = Range.ONE_DAY;
        } else {
            range = Range.ENTIRE;
        }

        int status = 0;
        String errMsg = Constants.EMPTY_STRING;
        final StringBuilder buf = new StringBuilder(255);
        try {
            final HotSearchWordHelper hotSearchWordHelper = SingletonS2Container
                    .getComponent(HotSearchWordHelper.class);
            final List<String> wordList = hotSearchWordHelper
                    .getHotSearchWordList(range);
            buf.append("\"result\":[");
            boolean first1 = true;
            for (final String word : wordList) {
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
            status = 1;
            errMsg = e.getMessage();
        }

        writeJsonResponse(status, buf.toString(), errMsg);

        return null;
    }

    @Execute(validator = false)
    public String favorite() {
        int status = 0;
        String body = null;
        String errMsg = null;

        if (Constants.FALSE.equals(crawlerProperties.getProperty(
                Constants.USER_FAVORITE_PROPERTY, Constants.FALSE))) {
            errMsg = "Not supported.";
            status = 10;
        } else {
            final String userCode = userInfoHelper.getUserCode();
            final String favoriteUrl = indexForm.u;

            try {
                if (StringUtil.isBlank(userCode)) {
                    errMsg = "No user session.";
                    status = 2;
                } else if (StringUtil.isBlank(favoriteUrl)) {
                    errMsg = "URL is null.";
                    status = 3;
                } else {
                    final String[] urls = userInfoHelper
                            .getResultUrls(URLDecoder.decode(indexForm.queryId,
                                    Constants.UTF_8));
                    if (urls != null) {
                        String targetUrl = null;
                        final String matchUrl = favoriteUrl.replaceFirst(":/+",
                                ":/");
                        for (final String url : urls) {
                            if (url.replaceFirst(":/+", ":/").equals(matchUrl)) {
                                targetUrl = url;
                                break;
                            }
                        }
                        if (targetUrl != null) {
                            if (favoriteLogService.addUrl(userCode, targetUrl)) {
                                body = "\"result\":\"ok\"";
                            } else {
                                errMsg = "Failed to add url.";
                                status = 4;
                            }
                        } else {
                            errMsg = "Not found: " + favoriteUrl;
                            status = 5;
                        }
                    } else {
                        errMsg = "No searched urls.";
                        status = 6;
                    }
                }
            } catch (final Exception e) {
                errMsg = e.getMessage();
                status = 1;
            }
        }

        writeJsonResponse(status, body, errMsg);

        return null;
    }

    @Execute(validator = false)
    public String favorites() {
        int status = 0;
        String body = null;
        String errMsg = null;

        if (Constants.FALSE.equals(crawlerProperties.getProperty(
                Constants.USER_FAVORITE_PROPERTY, Constants.FALSE))) {
            errMsg = "Not supported.";
            status = 10;
        } else {
            final String userCode = userInfoHelper.getUserCode();

            try {
                if (StringUtil.isBlank(userCode)) {
                    errMsg = "No user session.";
                    status = 2;
                } else if (StringUtil.isBlank(indexForm.queryId)) {
                    errMsg = "Query ID is null.";
                    status = 3;
                } else {
                    String[] urls = userInfoHelper.getResultUrls(URLDecoder
                            .decode(indexForm.queryId, Constants.UTF_8));
                    urls = favoriteLogService.getUrls(userCode, urls);
                    final StringBuilder buf = new StringBuilder();
                    buf.append("\"num\":").append(urls.length);
                    if (urls.length > 0) {
                        buf.append(", \"urls\":[");
                        for (int i = 0; i < urls.length; i++) {
                            if (i > 0) {
                                buf.append(',');
                            }
                            buf.append('"');
                            buf.append(escapeJsonString(urls[i]));
                            buf.append('"');
                        }
                        buf.append(']');
                    }
                    body = buf.toString();
                }
            } catch (final Exception e) {
                errMsg = e.getMessage();
                status = 1;
            }
        }

        writeJsonResponse(status, body, errMsg);

        return null;
    }

    @Execute(validator = false)
    public String screenshot() {
        if (StringUtil.isBlank(indexForm.queryId)
                || StringUtil.isBlank(indexForm.u) || screenShotManager == null) {
            // 404
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (final IOException e) {
                throw new IORuntimeException(e);
            }
            return null;
        }

        final File screenShotFile = screenShotManager.getScreenShotFile(
                indexForm.queryId, indexForm.u);
        if (screenShotFile == null) {
            // 404
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (final IOException e) {
                throw new IORuntimeException(e);
            }
            return null;
        }

        response.setContentType(getImageMimeType(screenShotFile));

        OutputStream out = null;
        BufferedInputStream in = null;
        try {
            out = response.getOutputStream();
            in = new BufferedInputStream(new FileInputStream(screenShotFile));
            InputStreamUtil.copy(in, out);
            OutputStreamUtil.flush(out);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }

        return null;
    }

    protected String getImageMimeType(final File imageFile) {
        final String path = imageFile.getAbsolutePath();
        if (path.endsWith(".png")) {
            return "image/png";
        } else if (path.endsWith(".gif")) {
            return "image/gif";
        } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return "image/jpeg";
        } else {
            return "application/octet-stream";
        }
    }

    @Execute(validator = false)
    public String osdd() {
        openSearchHelper.write(ResponseUtil.getResponse());
        return null;
    }

    public boolean isOsddLink() {
        return openSearchHelper.hasOpenSearchFile();
    }

    public String getHelpPage() {
        return viewHelper.getPagePath("common/help");
    }

    @Execute(validator = false, input = "index")
    public String help() {
        buildViewParams();
        buildInitParams();
        return "help.jsp";
    }

    protected boolean hasFieldInQuery(final Set<String> fieldSet,
            final String query) {
        final Matcher matcher = FIELD_EXTRACTION_PATTERN.matcher(query);
        if (matcher.matches()) {
            final String field = matcher.replaceFirst("$1");
            if (fieldSet.contains(field)) {
                return true;
            }
            fieldSet.add(field);
        }
        return false;
    }

}