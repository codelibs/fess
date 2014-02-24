/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
import jp.sf.fess.form.IndexForm;
import jp.sf.fess.helper.BrowserTypeHelper;
import jp.sf.fess.helper.CrawlingConfigHelper;
import jp.sf.fess.helper.DocumentHelper;
import jp.sf.fess.helper.HotSearchWordHelper;
import jp.sf.fess.helper.HotSearchWordHelper.Range;
import jp.sf.fess.helper.LabelTypeHelper;
import jp.sf.fess.helper.OpenSearchHelper;
import jp.sf.fess.helper.QueryHelper;
import jp.sf.fess.helper.SearchLogHelper;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.helper.UserInfoHelper;
import jp.sf.fess.helper.ViewHelper;
import jp.sf.fess.screenshot.ScreenShotManager;
import jp.sf.fess.service.FavoriteLogService;
import jp.sf.fess.service.SearchService;
import jp.sf.fess.suggest.Suggester;
import jp.sf.fess.util.ComponentUtil;
import jp.sf.fess.util.FacetResponse;
import jp.sf.fess.util.MoreLikeThisResponse;
import jp.sf.fess.util.QueryResponseList;
import jp.sf.fess.util.WebApiUtil;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.codelibs.core.util.DynamicProperties;
import org.codelibs.core.util.StringUtil;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.codelibs.solr.lib.exception.SolrLibQueryException;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.framework.util.InputStreamUtil;
import org.seasar.framework.util.OutputStreamUtil;
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

    private static final Logger logger = LoggerFactory
            .getLogger(IndexAction.class);

    private static final String REDIRECT_TO_INDEX = "index?redirect=true";

    private static final String LABEL_FIELD = "label";

    protected static final long DEFAULT_START_COUNT = 0;

    protected static final int DEFAULT_PAGE_SIZE = 20;

    protected static final int MAX_PAGE_SIZE = 100;

    protected static final int DEFAULT_SUGGEST_PAGE_SIZE = 5;

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
    protected SystemHelper systemHelper;

    @Resource
    protected Suggester suggester;

    @Resource
    protected DynamicProperties crawlerProperties;

    @Resource
    protected HttpServletRequest request;

    @Resource
    protected HttpServletResponse response;

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

    public List<Map<String, String>> langItems;

    public String errorMessage;

    protected String pagingQuery = null;

    public boolean searchLogSupport;

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
            if (indexForm.lang != null) {
                final Set<String> langSet = new HashSet<String>();
                for (final String lang : indexForm.lang) {
                    if (StringUtil.isNotBlank(lang) && lang.length() < 1000) {
                        if (Constants.ALL_LANGUAGES.equals(lang)) {
                            langSet.clear();
                            break;
                        }
                        final String normalizeLang = systemHelper
                                .normalizeLang(lang);
                        if (normalizeLang != null) {
                            langSet.add(normalizeLang);
                        }
                    }
                }
                if (!langSet.isEmpty()) {
                    for (final String lang : langSet) {
                        buf.append("&lang=").append(S2Functions.u(lang));
                    }
                }
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

        updateSearchParams();
        buildViewParams();
        doSearchInternal();

        return "search.jsp";
    }

    @Execute(validator = true, input = "index")
    public String cache() {
        Map<String, Object> doc = null;
        try {
            doc = searchService.getDocument("docId:" + indexForm.docId,
                    queryHelper.getCacheResponseFields(), null);
        } catch (final Exception e) {
            logger.warn("Failed to request: " + indexForm.docId, e);
        }
        if (doc == null) {
            errorMessage = MessageResourcesUtil.getMessage(RequestUtil
                    .getRequest().getLocale(), "errors.docid_not_found",
                    indexForm.docId);
            return "error.jsp";
        }

        final String content = viewHelper.createCacheContent(doc);
        if (content == null) {
            errorMessage = MessageResourcesUtil.getMessage(RequestUtil
                    .getRequest().getLocale(), "errors.docid_not_found",
                    indexForm.docId);
            return "error.jsp";
        }
        ResponseUtil.write(content, "text/html", Constants.UTF_8);

        return null;
    }

    @Execute(validator = true, input = "index")
    public String go() throws IOException {
        Map<String, Object> doc = null;
        try {
            doc = searchService.getDocument("docId:" + indexForm.docId,
                    queryHelper.getResponseFields(),
                    new String[] { systemHelper.clickCountField });
        } catch (final Exception e) {
            logger.warn("Failed to request: " + indexForm.docId, e);
        }
        if (doc == null) {
            errorMessage = MessageResourcesUtil.getMessage(RequestUtil
                    .getRequest().getLocale(), "errors.docid_not_found",
                    indexForm.docId);
            return "error.jsp";
        }
        final Object urlObj = doc.get("url");
        if (urlObj == null) {
            errorMessage = MessageResourcesUtil.getMessage(RequestUtil
                    .getRequest().getLocale(), "errors.document_not_found",
                    indexForm.docId);
            return "error.jsp";
        }
        final String url = urlObj.toString();

        if (Constants.TRUE.equals(crawlerProperties.getProperty(
                Constants.SEARCH_LOG_PROPERTY, Constants.TRUE))) {
            final String userSessionId = userInfoHelper.getUserCode();
            if (userSessionId != null) {
                final SearchLogHelper searchLogHelper = ComponentUtil
                        .getSearchLogHelper();
                final ClickLog clickLog = new ClickLog();
                clickLog.setUrl(url);
                clickLog.setRequestedTime(new Timestamp(System
                        .currentTimeMillis()));
                clickLog.setQueryRequestedTime(new Timestamp(Long
                        .parseLong(indexForm.rt)));
                clickLog.setUserSessionId(userSessionId);
                clickLog.setDocId(indexForm.docId);
                long clickCount = 0;
                final Object count = doc.get(systemHelper.clickCountField);
                if (count instanceof Long) {
                    clickCount = ((Long) count).longValue();
                }
                clickLog.setClickCount(clickCount);
                searchLogHelper.addClickLog(clickLog);
            }
        }

        if (isFileSystemPath(url)) {
            if (Constants.TRUE.equals(crawlerProperties.getProperty(
                    Constants.SEARCH_FILE_PROXY_PROPERTY, Constants.TRUE))) {
                final CrawlingConfigHelper crawlingConfigHelper = ComponentUtil
                        .getCrawlingConfigHelper();
                try {
                    crawlingConfigHelper.writeContent(doc);
                    return null;
                } catch (final Exception e) {
                    logger.error("Failed to load: " + doc, e);
                    errorMessage = MessageResourcesUtil.getMessage(RequestUtil
                            .getRequest().getLocale(),
                            "errors.not_load_from_server", url);
                    return "error.jsp";
                }
            } else if (Constants.TRUE.equals(crawlerProperties.getProperty(
                    Constants.SEARCH_DESKTOP_PROPERTY, Constants.FALSE))) {
                final String path = url.replaceFirst("file:/+", "//");
                final File file = new File(path);
                if (!file.exists()) {
                    errorMessage = MessageResourcesUtil.getMessage(RequestUtil
                            .getRequest().getLocale(),
                            "errors.not_found_on_file_system", url);
                    return "error.jsp";
                }
                final Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.open(file);
                } catch (final Exception e) {
                    errorMessage = MessageResourcesUtil.getMessage(RequestUtil
                            .getRequest().getLocale(),
                            "errors.could_not_open_on_system", url);
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
                                + "/applet/launcher?uri=" + S2Functions.u(url));
            } else {
                ResponseUtil.getResponse().sendRedirect(url);
            }
        } else {
            ResponseUtil.getResponse().sendRedirect(url);
        }
        return null;
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

    @Execute(validator = false)
    public String screenshot() {
        OutputStream out = null;
        BufferedInputStream in = null;
        try {
            final Map<String, Object> doc = searchService.getDocument("docId:"
                    + indexForm.docId);
            final String url = doc == null ? null : (String) doc.get("url");
            if (StringUtil.isBlank(indexForm.queryId)
                    || StringUtil.isBlank(url) || screenShotManager == null) {
                // 404
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }

            final File screenShotFile = screenShotManager.getScreenShotFile(
                    indexForm.queryId, url);
            if (screenShotFile == null) {
                // 404
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }

            response.setContentType(getImageMimeType(screenShotFile));

            out = response.getOutputStream();
            in = new BufferedInputStream(new FileInputStream(screenShotFile));
            InputStreamUtil.copy(in, out);
            OutputStreamUtil.flush(out);
        } catch (final Exception e) {
            logger.error("Failed to response: " + indexForm.docId, e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }

        return null;
    }

    @Execute(validator = false)
    public String searchApi() {
        try {
            WebApiUtil.setObject("searchQuery", doSearchInternal());
            WebApiUtil.setObject("execTime", execTime);
            WebApiUtil.setObject("pageSize", pageSize);
            WebApiUtil.setObject("currentPageNumber", currentPageNumber);
            WebApiUtil.setObject("allRecordCount", allRecordCount);
            WebApiUtil.setObject("allPageCount", allPageCount);
            WebApiUtil.setObject("documentItems", documentItems);
            WebApiUtil.setObject("facetResponse", facetResponse);
            WebApiUtil.setObject("moreLikeThisResponse", moreLikeThisResponse);
        } catch (final Exception e) {
            WebApiUtil.setError(1, e);
        }
        return null;
    }

    @Execute(validator = false)
    public String suggestApi() {
        if (Constants.FALSE.equals(crawlerProperties.getProperty(
                Constants.WEB_API_SUGGEST_PROPERTY, Constants.TRUE))) {
            WebApiUtil.setError(9, "Unsupported operation.");
            return null;
        }

        if (indexForm.fn == null || indexForm.fn.length == 0) {
            WebApiUtil.setError(2, "The field name is empty.");
            return null;
        }

        if (StringUtil.isBlank(indexForm.query)) {
            WebApiUtil.setError(3, "Your query is empty.");
            return null;
        }

        if (StringUtil.isBlank(indexForm.num)) {
            indexForm.num = String.valueOf(DEFAULT_SUGGEST_PAGE_SIZE);
        }

        int num = Integer.parseInt(indexForm.num);
        if (num > getMaxPageSize()) {
            num = getMaxPageSize();
        }

        final String[] fieldNames = indexForm.fn;
        final String[] labels = indexForm.fields.get("label");

        final List<SuggestResponse> suggestResultList = new ArrayList<SuggestResponse>();
        WebApiUtil.setObject("suggestResultList", suggestResultList);

        final List<String> suggestFieldName = Arrays.asList(fieldNames);
        WebApiUtil.setObject("suggestFieldName", suggestFieldName);

        final List<String> labelList;
        if (labels == null) {
            labelList = new ArrayList<String>();
        } else {
            labelList = Arrays.asList(labels);
        }

        try {
            final SuggestResponse suggestResponse = searchService
                    .getSuggestResponse(indexForm.query, suggestFieldName,
                            labelList, num);

            if (!suggestResponse.isEmpty()) {
                suggestResultList.add(suggestResponse);
            }

            WebApiUtil.setObject("suggestRecordCount", 1);
        } catch (final Exception e) {
            WebApiUtil.setError(1, e);
        }
        return null;
    }

    @Execute(validator = false)
    public String analysisApi() {
        if (Constants.FALSE.equals(crawlerProperties.getProperty(
                Constants.WEB_API_ANALYSIS_PROPERTY, Constants.TRUE))) {
            WebApiUtil.setError(9, "Unsupported operation.");
            return null;
        }

        if (indexForm.fn == null || indexForm.fn.length == 0) {
            WebApiUtil.setError(2, "The field name is empty.");
            return null;
        }

        if (StringUtil.isBlank(indexForm.query)) {
            WebApiUtil.setError(3, "Your query is empty.");
            return null;
        }

        try {
            final String[] fieldNames = indexForm.fn;
            final FieldAnalysisResponse fieldAnalysis = searchService
                    .getFieldAnalysisResponse(fieldNames, indexForm.query);
            WebApiUtil.setObject("fieldAnalysis", fieldAnalysis);
        } catch (final Exception e) {
            WebApiUtil.setError(1, e);
        }
        return null;
    }

    @Execute(validator = false)
    public String hotSearchWordApi() {
        if (Constants.FALSE.equals(crawlerProperties.getProperty(
                Constants.WEB_API_HOT_SEARCH_WORD_PROPERTY, Constants.TRUE))) {
            WebApiUtil.setError(9, "Unsupported operation.");
            return null;
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

        try {
            final HotSearchWordHelper hotSearchWordHelper = ComponentUtil
                    .getHotSearchWordHelper();
            final List<String> hotSearchWordList = hotSearchWordHelper
                    .getHotSearchWordList(range);
            WebApiUtil.setObject("hotSearchWordList", hotSearchWordList);
        } catch (final Exception e) {
            WebApiUtil.setError(1, e);
        }
        return null;

    }

    @Execute(validator = false)
    public String favoriteApi() {
        if (Constants.FALSE.equals(crawlerProperties.getProperty(
                Constants.USER_FAVORITE_PROPERTY, Constants.FALSE))) {
            WebApiUtil.setError(9, "Unsupported operation.");
            return null;
        }

        try {
            final Map<String, Object> doc = indexForm.docId == null ? null
                    : searchService.getDocument("docId:" + indexForm.docId,
                            queryHelper.getResponseFields(),
                            new String[] { systemHelper.favoriteCountField });
            final String userCode = userInfoHelper.getUserCode();
            final String favoriteUrl = doc == null ? null : (String) doc
                    .get("url");

            if (StringUtil.isBlank(userCode)) {
                WebApiUtil.setError(2, "No user session.");
                return null;
            } else if (StringUtil.isBlank(favoriteUrl)) {
                WebApiUtil.setError(2, "URL is null.");
                return null;
            }

            final String[] docIds = userInfoHelper.getResultDocIds(URLDecoder
                    .decode(indexForm.queryId, Constants.UTF_8));
            if (docIds == null) {
                WebApiUtil.setError(6, "No searched urls.");
                return null;
            }

            boolean found = false;
            for (final String docId : docIds) {
                if (indexForm.docId.equals(docId)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                WebApiUtil.setError(5, "Not found: " + favoriteUrl);
                return null;
            }

            if (!favoriteLogService.addUrl(userCode, favoriteUrl)) {
                WebApiUtil.setError(4, "Failed to add url: " + favoriteUrl);
                return null;
            }

            final DocumentHelper documentHelper = ComponentUtil
                    .getDocumentHelper();
            final Object count = doc.get(systemHelper.favoriteCountField);
            if (count instanceof Long) {
                documentHelper.update(indexForm.docId,
                        systemHelper.favoriteCountField,
                        ((Long) count).longValue() + 1);
            } else {
                WebApiUtil
                        .setError(7, "Failed to update count: " + favoriteUrl);
                return null;
            }
        } catch (final Exception e) {
            WebApiUtil.setError(1, e);
        }
        return null;

    }

    @Execute(validator = false)
    public String favoritesApi() {
        if (Constants.FALSE.equals(crawlerProperties.getProperty(
                Constants.USER_FAVORITE_PROPERTY, Constants.FALSE))) {
            WebApiUtil.setError(9, "Unsupported operation.");
            return null;
        }

        try {
            final String userCode = userInfoHelper.getUserCode();

            if (StringUtil.isBlank(userCode)) {
                WebApiUtil.setError(2, "No user session.");
                return null;
            } else if (StringUtil.isBlank(indexForm.queryId)) {
                WebApiUtil.setError(3, "Query ID is null.");
                return null;
            }

            final String[] docIds = userInfoHelper
                    .getResultDocIds(indexForm.queryId);
            final List<Map<String, Object>> docList = searchService
                    .getDocumentListByDocIds(docIds,
                            queryHelper.getResponseFields(),
                            new String[] { systemHelper.favoriteCountField },
                            MAX_PAGE_SIZE);
            List<String> urlList = new ArrayList<String>(docList.size());
            for (final Map<String, Object> doc : docList) {
                final Object urlObj = doc.get("url");
                if (urlObj != null) {
                    urlList.add(urlObj.toString());
                }
            }
            urlList = favoriteLogService.getUrlList(userCode, urlList);
            final List<String> docIdList = new ArrayList<String>(urlList.size());
            for (final Map<String, Object> doc : docList) {
                final Object urlObj = doc.get("url");
                if (urlObj != null && urlList.contains(urlObj.toString())) {
                    final Object docIdObj = doc.get(Constants.DOC_ID);
                    if (docIdObj != null) {
                        docIdList.add(docIdObj.toString());
                    }
                }
            }

            WebApiUtil.setObject("docIdList", docIdList);
        } catch (final Exception e) {
            WebApiUtil.setError(1, e);
        }
        return null;

    }

    @Execute(validator = false)
    public String osdd() {
        openSearchHelper.write(ResponseUtil.getResponse());
        return null;
    }

    @Execute(validator = false, input = "index")
    public String help() {
        buildViewParams();
        buildInitParams();
        return "help.jsp";
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
        if (indexForm.lang != null) {
            final Set<String> langSet = new HashSet<>();
            for (final String lang : indexForm.lang) {
                if (StringUtil.isNotBlank(lang) && lang.length() < 1000) {
                    final String normalizeLang = systemHelper
                            .normalizeLang(lang);
                    if (normalizeLang != null) {
                        langSet.add(normalizeLang);
                    }
                }
            }
            appendLangQuery(queryBuf, langSet);
        } else if (Constants.TRUE.equals(crawlerProperties.getProperty(
                Constants.USE_BROWSER_LOCALE_FOR_SEARCH_PROPERTY,
                Constants.FALSE))) {
            final Set<String> langSet = new HashSet<>();
            final Enumeration<Locale> locales = request.getLocales();
            if (locales != null) {
                while (locales.hasMoreElements()) {
                    final Locale locale = locales.nextElement();
                    final String normalizeLang = systemHelper
                            .normalizeLang(locale.toString());
                    if (normalizeLang != null) {
                        langSet.add(normalizeLang);
                    }
                }
                if (!langSet.isEmpty()) {
                    appendLangQuery(queryBuf, langSet);
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
            indexForm.num = String.valueOf(getDefaultPageSize());
        }
        normalizePageNum();

        final int pageStart = Integer.parseInt(indexForm.start);
        final int pageNum = Integer.parseInt(indexForm.num);
        try {
            documentItems = searchService.getDocumentList(query, pageStart,
                    pageNum, indexForm.facet, indexForm.geo, indexForm.mlt,
                    queryHelper.getResponseFields(),
                    queryHelper.getResponseDocValuesFields());
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
        if (searchLogSupport) {
            final Timestamp now = new Timestamp(rt);

            final SearchLogHelper searchLogHelper = ComponentUtil
                    .getSearchLogHelper();
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

    private void appendLangQuery(final StringBuilder queryBuf,
            final Set<String> langSet) {
        if (langSet.size() == 1) {
            queryBuf.append(' ').append(systemHelper.langField).append(':')
                    .append(langSet.iterator().next());
        } else if (langSet.size() > 1) {
            boolean first = true;
            for (final String lang : langSet) {
                if (first) {
                    queryBuf.append(" (");
                    first = false;
                } else {
                    queryBuf.append(" OR ");
                }
                queryBuf.append(systemHelper.langField).append(':')
                        .append(lang);
            }
            queryBuf.append(')');
        }
    }

    protected void updateSearchParams() {
        if (indexForm.facet == null) {
            indexForm.facet = queryHelper.getDefaultFacetInfo();
        }

        if (indexForm.mlt == null) {
            indexForm.mlt = queryHelper.getDefaultMoreLikeThisInfo();
        }

        if (indexForm.geo == null) {
            indexForm.geo = queryHelper.getDefaultGeoInfo();
        }
    }

    protected boolean isFileSystemPath(final String url) {
        return url.startsWith("file:") || url.startsWith("smb:");
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
            final String defaultLabelValue = crawlerProperties.getProperty(
                    Constants.DEFAULT_LABEL_VALUE_PROPERTY, StringUtil.EMPTY);
            if (StringUtil.isNotBlank(defaultLabelValue)) {
                final String[] values = defaultLabelValue.split("\n");
                if (values != null && values.length > 0) {
                    final List<String> list = new ArrayList<String>(
                            values.length);
                    for (final String value : values) {
                        if (StringUtil.isNotBlank(value)) {
                            list.add(value);
                        }
                    }
                    if (!list.isEmpty()) {
                        indexForm.fields.put(LABEL_FIELD,
                                list.toArray(new String[list.size()]));
                    }
                }
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

        Locale locale = request.getLocale();
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        langItems = systemHelper.getLanguageItems(locale);

        searchLogSupport = Constants.TRUE.equals(crawlerProperties.getProperty(
                Constants.SEARCH_LOG_PROPERTY, Constants.TRUE));
        favoriteSupport = Constants.TRUE.equals(crawlerProperties.getProperty(
                Constants.USER_FAVORITE_PROPERTY, Constants.FALSE));

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

    public boolean isOsddLink() {
        return openSearchHelper.hasOpenSearchFile();
    }

    public String getHelpPage() {
        return viewHelper.getPagePath("common/help");
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

}