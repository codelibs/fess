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

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import jp.sf.fess.Constants;
import jp.sf.fess.InvalidQueryException;
import jp.sf.fess.ResultOffsetExceededException;
import jp.sf.fess.UnsupportedSearchException;
import jp.sf.fess.db.allcommon.CDef;
import jp.sf.fess.db.exentity.SearchLog;
import jp.sf.fess.db.exentity.UserInfo;
import jp.sf.fess.form.MobileForm;
import jp.sf.fess.helper.QueryHelper;
import jp.sf.fess.helper.SearchLogHelper;
import jp.sf.fess.helper.UserInfoHelper;
import jp.sf.fess.service.SearchService;
import jp.sf.fess.util.QueryResponseList;

import org.apache.commons.lang.StringUtils;
import org.codelibs.core.util.DynamicProperties;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MobileAction {
    private static final Logger logger = LoggerFactory
            .getLogger(MobileAction.class);

    protected static final int DEFAULT_PAGE_SIZE = 10;

    protected static final int MAX_PAGE_SIZE = 100;

    protected static final long DEFAULT_START_COUNT = 0;

    @ActionForm
    @Resource
    protected MobileForm mobileForm;

    @Resource
    protected SearchService searchService;

    @Resource
    protected UserInfoHelper userInfoHelper;

    @Resource
    protected QueryHelper queryHelper;

    @Resource
    protected DynamicProperties crawlerProperties;

    @Resource
    protected HttpServletRequest request;

    public List<Map<String, Object>> documentItems;

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

    @Execute(validator = false, input = "index.jsp")
    public String index() {
        if (isWeb()) {
            return "/index?redirect=true";
        }

        return "index.jsp";
    }

    protected String doSearch() {
        if (isWeb()) {
            return "/index?redirect=true";
        }
        if (StringUtil.isBlank(mobileForm.query)) {
            // redirect to index page
            mobileForm.query = null;
            return "index?redirect=true";
        }

        // init pager
        if (StringUtil.isBlank(mobileForm.start)) {
            mobileForm.start = String.valueOf(DEFAULT_START_COUNT);
        } else {
            try {
                Long.parseLong(mobileForm.start);
            } catch (final NumberFormatException e) {
                mobileForm.start = String.valueOf(DEFAULT_START_COUNT);
            }
        }
        if (StringUtil.isBlank(mobileForm.num)) {
            mobileForm.num = String.valueOf(getDefaultPageSize());
        } else {
            try {
                final int num = Integer.parseInt(mobileForm.num);
                if (num > getMaxPageSize()) {
                    // max page size
                    mobileForm.num = String.valueOf(getMaxPageSize());
                }
            } catch (final NumberFormatException e) {
                mobileForm.num = String.valueOf(getDefaultPageSize());
            }
        }

        final int pageStart = Integer.parseInt(mobileForm.start);
        final int pageNum = Integer.parseInt(mobileForm.num);
        // TODO add GeoInfo if needed...
        try {
            documentItems = searchService.getDocumentList(mobileForm.query,
                    pageStart, pageNum, null, null, null,
                    queryHelper.getResponseDocValuesFields());
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

        final QueryResponseList queryResponseList = (QueryResponseList) documentItems;
        final NumberFormat nf = NumberFormat.getInstance(RequestUtil
                .getRequest().getLocale());
        nf.setMaximumIntegerDigits(2);
        nf.setMaximumFractionDigits(2);
        try {
            execTime = nf
                    .format((double) queryResponseList.getExecTime() / 1000);
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
        }

        // search log
        if (Constants.TRUE.equals(crawlerProperties.getProperty(
                Constants.SEARCH_LOG_PROPERTY, Constants.TRUE))) {
            final SearchLogHelper searchLogHelper = SingletonS2Container
                    .getComponent(SearchLogHelper.class);
            final Timestamp now = new Timestamp(System.currentTimeMillis());
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
            searchLog.setSearchWord(mobileForm.query);
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
            searchLog.setAccessType(CDef.AccessType.Mobile.code());

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
                        "pageNumberList").execute();

        return "search.jsp";
    }

    protected boolean isWeb() {
        final String supportedSearch = crawlerProperties.getProperty(
                Constants.SUPPORTED_SEARCH_FEATURE_PROPERTY,
                Constants.SUPPORTED_SEARCH_WEB_MOBILE);
        if (Constants.SUPPORTED_SEARCH_WEB.equals(supportedSearch)) {
            return true;
        } else if (Constants.SUPPORTED_SEARCH_NONE.equals(supportedSearch)) {
            throw new UnsupportedSearchException("A search is not supported: "
                    + RequestUtil.getRequest().getRequestURL());
        }
        return false;
    }

    @Execute(validator = false, input = "index")
    public String search() {
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
        if (StringUtil.isBlank(mobileForm.num)) {
            mobileForm.num = String.valueOf(getDefaultPageSize());
        } else {
            try {
                pageNum = Integer.parseInt(mobileForm.num);
            } catch (final NumberFormatException e) {
                mobileForm.num = String.valueOf(getDefaultPageSize());
            }
        }

        if (StringUtil.isBlank(mobileForm.pn)) {
            mobileForm.start = String.valueOf(DEFAULT_START_COUNT);
        } else {
            Integer pageNumber = Integer.parseInt(mobileForm.pn);
            if (pageNumber != null && pageNumber > 0) {
                pageNumber = pageNumber + move;
                if (pageNumber < 1) {
                    pageNumber = 1;
                }
                mobileForm.start = String.valueOf((pageNumber - 1) * pageNum);
            } else {
                mobileForm.start = String.valueOf(DEFAULT_START_COUNT);
            }
        }

        return doSearch();
    }

    protected int getDefaultPageSize() {
        return DEFAULT_PAGE_SIZE;
    }

    protected int getMaxPageSize() {
        return MAX_PAGE_SIZE;
    }
}