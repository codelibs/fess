/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.pager;

import java.io.Serializable;
import java.util.List;

import org.codelibs.fess.util.ComponentUtil;

public class SearchLogPager implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String LOG_TYPE_SEARCH = "search";

    public static final String LOG_TYPE_SEARCH_COUNT_HOUR = "search_count_hour_agg";

    public static final String LOG_TYPE_SEARCH_COUNT_DAY = "search_count_day_agg";

    public static final String LOG_TYPE_SEARCH_USER_HOUR = "search_user_hour_agg";

    public static final String LOG_TYPE_SEARCH_USER_DAY = "search_user_day_agg";

    public static final String LOG_TYPE_SEARCH_REQTIMEAVG_HOUR = "search_reqtimeavg_hour_agg";

    public static final String LOG_TYPE_SEARCH_REQTIMEAVG_DAY = "search_reqtimeavg_day_agg";

    public static final String LOG_TYPE_SEARCH_KEYWORD = "search_keyword_agg";

    public static final String LOG_TYPE_SEARCH_ZEROHIT = "search_zerohit_agg";

    public static final String LOG_TYPE_SEARCH_ZEROCLICK = "search_zeroclick_agg";

    public static final String LOG_TYPE_CLICK = "click";

    public static final String LOG_TYPE_CLICK_COUNT = "click_count_agg";

    public static final String LOG_TYPE_FAVORITE = "favorite";

    public static final String LOG_TYPE_FAVORITE_COUNT = "favorite_count_agg";

    public static final String LOG_TYPE_USERINFO = "user_info";

    public static final int DEFAULT_PAGE_SIZE = 20;

    public static final int DEFAULT_CURRENT_PAGE_NUMBER = 1;

    private int allRecordCount;

    private int allPageCount;

    private boolean existPrePage;

    private boolean existNextPage;

    private List<Integer> pageNumberList;

    private int pageSize;

    private int currentPageNumber;

    public String logType = LOG_TYPE_SEARCH;

    public String queryId;

    public String userSessionId;

    public String requestedTimeRange;

    public String accessType;

    public void clear() {
        allRecordCount = 0;
        allPageCount = 0;
        existPrePage = false;
        existNextPage = false;
        pageSize = getDefaultPageSize();
        currentPageNumber = getDefaultCurrentPageNumber();

        queryId = null;
        userSessionId = null;
        requestedTimeRange = null;
        accessType = null;
        logType = LOG_TYPE_SEARCH;

    }

    protected int getDefaultCurrentPageNumber() {
        return DEFAULT_CURRENT_PAGE_NUMBER;
    }

    public int getAllRecordCount() {
        return allRecordCount;
    }

    public void setAllRecordCount(final int allRecordCount) {
        this.allRecordCount = allRecordCount;
    }

    public int getAllPageCount() {
        return allPageCount;
    }

    public void setAllPageCount(final int allPageCount) {
        this.allPageCount = allPageCount;
    }

    public boolean isExistPrePage() {
        return existPrePage;
    }

    public void setExistPrePage(final boolean existPrePage) {
        this.existPrePage = existPrePage;
    }

    public boolean isExistNextPage() {
        return existNextPage;
    }

    public void setExistNextPage(final boolean existNextPage) {
        this.existNextPage = existNextPage;
    }

    public int getPageSize() {
        if (pageSize <= 0) {
            pageSize = getDefaultPageSize();
        }
        return pageSize;
    }

    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPageNumber() {
        if (currentPageNumber <= 0) {
            currentPageNumber = getDefaultCurrentPageNumber();
        }
        return currentPageNumber;
    }

    public void setCurrentPageNumber(final int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    public List<Integer> getPageNumberList() {
        return pageNumberList;
    }

    public void setPageNumberList(final List<Integer> pageNumberList) {
        this.pageNumberList = pageNumberList;
    }

    protected int getDefaultPageSize() {
        return ComponentUtil.getFessConfig().getPagingPageSizeAsInteger();
    }

}
