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

package org.codelibs.fess.app.web.search;

import java.text.NumberFormat;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.InvalidQueryException;
import org.codelibs.fess.ResultOffsetExceededException;
import org.codelibs.fess.app.web.RootAction;
import org.codelibs.fess.app.web.RootForm;
import org.codelibs.fess.app.web.base.FessSearchAction;
import org.codelibs.fess.client.FessEsClient.SearchConditionBuilder;
import org.codelibs.fess.es.exentity.SearchLog;
import org.codelibs.fess.es.exentity.UserInfo;
import org.codelibs.fess.helper.SearchLogHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.QueryResponseList;
import org.dbflute.optional.OptionalEntity;
import org.lastaflute.taglib.function.LaFunctions;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.util.LaRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchAction extends FessSearchAction {

    // ===================================================================================
    //                                                                            Constant
    //     
    private static final Logger logger = LoggerFactory.getLogger(SearchAction.class);

    protected static final long DEFAULT_START_COUNT = 0;

    protected static final int MAX_PAGE_SIZE = 100;

    private static final int DEFAULT_PAGE_SIZE = 20;

    protected static final Pattern FIELD_EXTRACTION_PATTERN = Pattern.compile("^([a-zA-Z0-9_]+):.*");

    // ===================================================================================
    //                                                                           Attribute
    //     

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final SearchForm form) {
        return search(form);
    }

    @Execute
    public HtmlResponse search(final SearchForm form) {
        if (viewHelper.isUseSession() && StringUtil.isNotBlank(form.num)) {
            normalizePageNum(form);
            final HttpSession session = request.getSession();
            if (session != null) {
                session.setAttribute(Constants.RESULTS_PER_PAGE, form.num);
            }
        }

        return doSearch(form);
    }

    @Execute
    public HtmlResponse prev(final SearchForm form) {
        return doMove(form, -1);
    }

    @Execute
    public HtmlResponse next(final SearchForm form) {
        return doMove(form, 1);
    }

    @Execute
    public HtmlResponse move(final SearchForm form) {
        return doMove(form, 0);
    }

    protected HtmlResponse doSearch(final SearchForm form) {
        searchAvailable();

        if (viewHelper.isUseSession()) {
            final HttpSession session = request.getSession(false);
            if (session != null) {
                final Object resultsPerPage = session.getAttribute(Constants.RESULTS_PER_PAGE);
                if (resultsPerPage != null) {
                    form.num = resultsPerPage.toString();
                }
            }
        }

        if (StringUtil.isBlank(form.query)) {
            try {
                final String optionQuery = queryHelper.buildOptionQuery(form.options);
                form.query = optionQuery;
            } catch (final InvalidQueryException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug(e.getMessage(), e);
                }
                throwValidationError(e.getMessageCode(), () -> asHtml(path_ErrorJsp));
            }
        }

        if (StringUtil.isBlank(form.query) && form.fields.isEmpty()) {
            // redirect to index page
            form.query = null;
            return redirect(RootAction.class);
        }

        return asHtml(path_SearchJsp).renderWith(data -> {
            updateSearchParams(form);
            buildLabelParams(form.fields);
            doSearchInternal(data, form);
            data.register("displayQuery", getDisplayQuery(form, labelTypeHelper.getLabelTypeItemList()));
            data.register("pagingQuery", getPagingQuery(form));
        });
    }

    protected HtmlResponse doMove(final SearchForm form, final int move) {
        int pageNum = getDefaultPageSize();
        if (StringUtil.isBlank(form.num)) {
            form.num = String.valueOf(getDefaultPageSize());
        } else {
            try {
                pageNum = Integer.parseInt(form.num);
            } catch (final NumberFormatException e) {
                form.num = String.valueOf(getDefaultPageSize());
            }
        }

        if (StringUtil.isBlank(form.pn)) {
            form.start = String.valueOf(DEFAULT_START_COUNT);
        } else {
            Integer pageNumber = Integer.parseInt(form.pn);
            if (pageNumber != null && pageNumber > 0) {
                pageNumber = pageNumber + move;
                if (pageNumber < 1) {
                    pageNumber = 1;
                }
                form.start = String.valueOf((pageNumber - 1) * pageNum);
            } else {
                form.start = String.valueOf(DEFAULT_START_COUNT);
            }
        }

        return doSearch(form);
    }

    protected String doSearchInternal(final RenderData data, final SearchForm form) {
        final StringBuilder queryBuf = new StringBuilder(255);
        if (StringUtil.isNotBlank(form.query)) {
            queryBuf.append(form.query);
        }
        if (StringUtil.isNotBlank(form.op)) {
            request.setAttribute(Constants.DEFAULT_OPERATOR, form.op);
        }
        if (queryBuf.indexOf(" OR ") >= 0) {
            queryBuf.insert(0, '(').append(')');
        }
        if (form.additional != null) {
            final Set<String> fieldSet = new HashSet<String>();
            for (final String additional : form.additional) {
                if (StringUtil.isNotBlank(additional) && additional.length() < 1000 && !hasFieldInQuery(fieldSet, additional)) {
                    queryBuf.append(' ').append(additional);
                }
            }
        }
        if (!form.fields.isEmpty()) {
            for (final Map.Entry<String, String[]> entry : form.fields.entrySet()) {
                final List<String> valueList = new ArrayList<String>();
                final String[] values = entry.getValue();
                if (values != null) {
                    for (final String v : values) {
                        valueList.add(v);
                    }
                }
                if (valueList.size() == 1) {
                    queryBuf.append(' ').append(entry.getKey()).append(":\"").append(valueList.get(0)).append('\"');
                } else if (valueList.size() > 1) {
                    queryBuf.append(" (");
                    for (int i = 0; i < valueList.size(); i++) {
                        if (i != 0) {
                            queryBuf.append(" OR");
                        }
                        queryBuf.append(' ').append(entry.getKey()).append(":\"").append(valueList.get(i)).append('\"');
                    }
                    queryBuf.append(')');
                }

            }
        }
        if (StringUtil.isNotBlank(form.sort)) {
            queryBuf.append(" sort:").append(form.sort);
        }
        if (form.lang != null) {
            final Set<String> langSet = new HashSet<>();
            for (final String lang : form.lang) {
                if (StringUtil.isNotBlank(lang) && lang.length() < 1000) {
                    if (Constants.ALL_LANGUAGES.equalsIgnoreCase(lang)) {
                        langSet.add(Constants.ALL_LANGUAGES);
                    } else {
                        final String normalizeLang = systemHelper.normalizeLang(lang);
                        if (normalizeLang != null) {
                            langSet.add(normalizeLang);
                        }
                    }
                }
            }
            if (langSet.size() > 1 && langSet.contains(Constants.ALL_LANGUAGES)) {
                langSet.clear();
                form.lang = new String[] { Constants.ALL_LANGUAGES };
            } else {
                langSet.remove(Constants.ALL_LANGUAGES);
            }
            appendLangQuery(queryBuf, langSet);
        } else if (Constants.TRUE.equals(crawlerProperties.getProperty(Constants.USE_BROWSER_LOCALE_FOR_SEARCH_PROPERTY, Constants.FALSE))) {
            final Set<String> langSet = new HashSet<>();
            final Enumeration<Locale> locales = request.getLocales();
            if (locales != null) {
                while (locales.hasMoreElements()) {
                    final Locale locale = locales.nextElement();
                    final String normalizeLang = systemHelper.normalizeLang(locale.toString());
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
        if (StringUtil.isBlank(form.start)) {
            form.start = String.valueOf(DEFAULT_START_COUNT);
        } else {
            try {
                Integer.parseInt(form.start);
            } catch (final NumberFormatException e) {
                form.start = String.valueOf(DEFAULT_START_COUNT);
            }
        }
        if (StringUtil.isBlank(form.num)) {
            form.num = String.valueOf(getDefaultPageSize());
        }
        normalizePageNum(form);

        final int pageStart = Integer.parseInt(form.start);
        final int pageNum = Integer.parseInt(form.num);
        List<Map<String, Object>> documentItems = null;
        try {
            documentItems =
                    fessEsClient.search(fieldHelper.docIndex, fieldHelper.docType,
                            searchRequestBuilder -> {
                                return SearchConditionBuilder.builder(searchRequestBuilder).query(query).offset(pageStart).size(pageNum)
                                        .facetInfo(form.facet).geoInfo(form.geo).responseFields(queryHelper.getResponseFields()).build();
                            }, (searchRequestBuilder, execTime, searchResponse) -> {
                                final QueryResponseList queryResponseList = ComponentUtil.getQueryResponseList();
                                queryResponseList.init(searchResponse, pageStart, pageNum);
                                return queryResponseList;
                            });
        } catch (final InvalidQueryException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            throwValidationError(e.getMessageCode(), () -> asHtml(path_ErrorJsp));
        } catch (final ResultOffsetExceededException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            throwValidationError(messages -> {
                messages.addErrorsResultSizeExceeded(GLOBAL);
            }, () -> asHtml(path_ErrorJsp));
        }
        data.register("documentItems", documentItems);

        // search
        final QueryResponseList queryResponseList = (QueryResponseList) documentItems;
        data.register("facetResponse", queryResponseList.getFacetResponse());
        final NumberFormat nf = NumberFormat.getInstance(LaRequestUtil.getRequest().getLocale());
        nf.setMaximumIntegerDigits(2);
        nf.setMaximumFractionDigits(2);
        String execTime;
        try {
            execTime = nf.format((double) queryResponseList.getExecTime() / 1000);
        } catch (final Exception e) {
            execTime = StringUtil.EMPTY;
        }
        data.register("execTime", execTime);

        final Clock clock = Clock.systemDefaultZone();
        form.rt = Long.toString(clock.millis());

        // favorite
        if (favoriteSupport || screenShotManager != null) {
            form.queryId = userInfoHelper.generateQueryId(query, documentItems);
            if (screenShotManager != null) {
                screenShotManager.storeRequest(form.queryId, documentItems);
                data.register("screenShotSupport", true);
            }
        }

        // search log
        if (searchLogSupport) {
            final long now = systemHelper.getCurrentTimeAsLong();

            final SearchLogHelper searchLogHelper = ComponentUtil.getSearchLogHelper();
            final SearchLog searchLog = new SearchLog();

            String userCode = null;
            if (Constants.TRUE.equals(crawlerProperties.getProperty(Constants.USER_INFO_PROPERTY, Constants.TRUE))) {
                userCode = userInfoHelper.getUserCode();
                if (StringUtil.isNotBlank(userCode)) {
                    final UserInfo userInfo = new UserInfo();
                    userInfo.setCode(userCode);
                    userInfo.setCreatedTime(now);
                    userInfo.setUpdatedTime(now);
                    searchLog.setUserInfo(OptionalEntity.of(userInfo));
                }
            }

            searchLog.setHitCount(queryResponseList.getAllRecordCount());
            searchLog.setResponseTime(Integer.valueOf((int) queryResponseList.getExecTime()));
            searchLog.setSearchWord(StringUtils.abbreviate(query, 1000));
            searchLog.setSearchQuery(StringUtils.abbreviate(queryResponseList.getSearchQuery(), 1000));
            searchLog.setSolrQuery(StringUtils.abbreviate(queryResponseList.getSolrQuery(), 1000));
            searchLog.setRequestedTime(now);
            searchLog.setQueryOffset(pageStart);
            searchLog.setQueryPageSize(pageNum);

            searchLog.setClientIp(StringUtils.abbreviate(request.getRemoteAddr(), 50));
            searchLog.setReferer(StringUtils.abbreviate(request.getHeader("referer"), 1000));
            searchLog.setUserAgent(StringUtils.abbreviate(request.getHeader("user-agent"), 255));
            if (userCode != null) {
                searchLog.setUserSessionId(userCode);
            }
            final Object accessType = request.getAttribute(Constants.SEARCH_LOG_ACCESS_TYPE);
            if (Constants.SEARCH_LOG_ACCESS_TYPE_JSON.equals(accessType)) {
                searchLog.setAccessType(Constants.SEARCH_LOG_ACCESS_TYPE_JSON);
            } else if (Constants.SEARCH_LOG_ACCESS_TYPE_XML.equals(accessType)) {
                searchLog.setAccessType(Constants.SEARCH_LOG_ACCESS_TYPE_XML);
            } else if (Constants.SEARCH_LOG_ACCESS_TYPE_OTHER.equals(accessType)) {
                searchLog.setAccessType(Constants.SEARCH_LOG_ACCESS_TYPE_OTHER);
            } else {
                searchLog.setAccessType(Constants.SEARCH_LOG_ACCESS_TYPE_WEB);
            }

            @SuppressWarnings("unchecked")
            final Map<String, List<String>> fieldLogMap = (Map<String, List<String>>) request.getAttribute(Constants.FIELD_LOGS);
            if (fieldLogMap != null) {
                for (final Map.Entry<String, List<String>> logEntry : fieldLogMap.entrySet()) {
                    for (final String value : logEntry.getValue()) {
                        searchLog.addSearchFieldLogValue(logEntry.getKey(), StringUtils.abbreviate(value, 1000));
                    }
                }
            }

            searchLogHelper.addSearchLog(searchLog);
        }

        final String[] highlightQueries = (String[]) request.getAttribute(Constants.HIGHLIGHT_QUERIES);
        if (highlightQueries != null) {
            final StringBuilder buf = new StringBuilder(100);
            for (final String q : highlightQueries) {
                buf.append("&hq=").append(q);
            }
            data.register("appendHighlightQueries", buf.toString());
        }

        data.register("pageSize", queryResponseList.getPageSize());
        data.register("currentPageNumber", queryResponseList.getCurrentPageNumber());
        data.register("allRecordCount", queryResponseList.getAllRecordCount());
        data.register("allPageCount", queryResponseList.getAllPageCount());
        data.register("existNextPage", queryResponseList.isExistNextPage());
        data.register("existPrevPage", queryResponseList.isExistPrevPage());
        data.register("currentStartRecordNumber", queryResponseList.getCurrentStartRecordNumber());
        data.register("currentEndRecordNumber", queryResponseList.getCurrentEndRecordNumber());
        data.register("pageNumberList", queryResponseList.getPageNumberList());
        data.register("partialResults", queryResponseList.isPartialResults());
        // TODO
        //        data.register("queryTime", queryResponseList.get);
        //        data.register("searchTime", queryResponseList.get);

        return query;
    }

    protected void appendLangQuery(final StringBuilder queryBuf, final Set<String> langSet) {
        if (langSet.size() == 1) {
            queryBuf.append(' ').append(fieldHelper.langField).append(':').append(langSet.iterator().next());
        } else if (langSet.size() > 1) {
            boolean first = true;
            for (final String lang : langSet) {
                if (first) {
                    queryBuf.append(" (");
                    first = false;
                } else {
                    queryBuf.append(" OR ");
                }
                queryBuf.append(fieldHelper.langField).append(':').append(lang);
            }
            queryBuf.append(')');
        }
    }

    protected void updateSearchParams(final SearchForm form) {
        if (form.facet == null) {
            form.facet = queryHelper.getDefaultFacetInfo();
        }

        if (form.geo == null) {
            form.geo = queryHelper.getDefaultGeoInfo();
        }
    }

    protected String getDisplayQuery(final RootForm form, final List<Map<String, String>> labelTypeItems) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append(form.query);
        if (!form.fields.isEmpty() && form.fields.containsKey(LABEL_FIELD)) {
            final String[] values = form.fields.get(LABEL_FIELD);
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

    protected void normalizePageNum(final RootForm form) {
        try {
            final int num = Integer.parseInt(form.num);
            if (num > getMaxPageSize()) {
                // max page size
                form.num = String.valueOf(getMaxPageSize());
            } else if (num <= 0) {
                form.num = String.valueOf(getDefaultPageSize());
            }
        } catch (final NumberFormatException e) {
            form.num = String.valueOf(getDefaultPageSize());
        }
    }

    protected int getDefaultPageSize() {
        return DEFAULT_PAGE_SIZE;
    }

    protected int getMaxPageSize() {
        final Object maxPageSize = crawlerProperties.get(Constants.SEARCH_RESULT_MAX_PAGE_SIZE);
        if (maxPageSize == null) {
            return MAX_PAGE_SIZE;
        }
        try {
            return Integer.parseInt(maxPageSize.toString());
        } catch (final NumberFormatException e) {
            return MAX_PAGE_SIZE;
        }
    }

    protected boolean hasFieldInQuery(final Set<String> fieldSet, final String query) {
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

    protected String getPagingQuery(final SearchForm form) {
        final StringBuilder buf = new StringBuilder(200);
        if (form.additional != null) {
            final Set<String> fieldSet = new HashSet<String>();
            for (final String additional : form.additional) {
                if (StringUtil.isNotBlank(additional) && additional.length() < 1000 && !hasFieldInQuery(fieldSet, additional)) {
                    buf.append("&additional=").append(LaFunctions.u(additional));
                }
            }
        }
        if (StringUtil.isNotBlank(form.sort)) {
            buf.append("&sort=").append(LaFunctions.u(form.sort));
        }
        if (StringUtil.isNotBlank(form.op)) {
            buf.append("&op=").append(LaFunctions.u(form.op));
        }
        if (form.lang != null) {
            final Set<String> langSet = new HashSet<String>();
            for (final String lang : form.lang) {
                if (StringUtil.isNotBlank(lang) && lang.length() < 1000) {
                    if (Constants.ALL_LANGUAGES.equals(lang)) {
                        langSet.clear();
                        break;
                    }
                    final String normalizeLang = systemHelper.normalizeLang(lang);
                    if (normalizeLang != null) {
                        langSet.add(normalizeLang);
                    }
                }
            }
            if (!langSet.isEmpty()) {
                for (final String lang : langSet) {
                    buf.append("&lang=").append(LaFunctions.u(lang));
                }
            }
        }
        if (!form.fields.isEmpty()) {
            for (final Map.Entry<String, String[]> entry : form.fields.entrySet()) {
                final String[] values = entry.getValue();
                if (values != null) {
                    for (final String v : values) {
                        if (StringUtil.isNotBlank(v)) {
                            buf.append("&fields.").append(LaFunctions.u(entry.getKey())).append('=').append(LaFunctions.u(v));
                        }
                    }
                }
            }
        }

        return buf.toString();
    }

}