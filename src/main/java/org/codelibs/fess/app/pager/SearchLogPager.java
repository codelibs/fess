/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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

/**
 * Pager class for search log pagination and filtering.
 *
 * This class provides pagination functionality for various types of search logs
 * including search logs, click logs, favorite logs, and user information logs.
 * It also supports different aggregation types for analytics and reporting.
 */
public class SearchLogPager implements Serializable {

    /** Serial version UID for serialization. */
    private static final long serialVersionUID = 1L;

    /** Log type constant for search logs. */
    public static final String LOG_TYPE_SEARCH = "search";

    /** Log type constant for hourly search count aggregation. */
    public static final String LOG_TYPE_SEARCH_COUNT_HOUR = "search_count_hour_agg";

    /** Log type constant for daily search count aggregation. */
    public static final String LOG_TYPE_SEARCH_COUNT_DAY = "search_count_day_agg";

    /** Log type constant for hourly unique user aggregation. */
    public static final String LOG_TYPE_SEARCH_USER_HOUR = "search_user_hour_agg";

    /** Log type constant for daily unique user aggregation. */
    public static final String LOG_TYPE_SEARCH_USER_DAY = "search_user_day_agg";

    /** Log type constant for hourly average request time aggregation. */
    public static final String LOG_TYPE_SEARCH_REQTIMEAVG_HOUR = "search_reqtimeavg_hour_agg";

    /** Log type constant for daily average request time aggregation. */
    public static final String LOG_TYPE_SEARCH_REQTIMEAVG_DAY = "search_reqtimeavg_day_agg";

    /** Log type constant for search keyword aggregation. */
    public static final String LOG_TYPE_SEARCH_KEYWORD = "search_keyword_agg";

    /** Log type constant for zero-hit search aggregation. */
    public static final String LOG_TYPE_SEARCH_ZEROHIT = "search_zerohit_agg";

    /** Log type constant for zero-click search aggregation. */
    public static final String LOG_TYPE_SEARCH_ZEROCLICK = "search_zeroclick_agg";

    /** Log type constant for click logs. */
    public static final String LOG_TYPE_CLICK = "click";

    /** Log type constant for click count aggregation. */
    public static final String LOG_TYPE_CLICK_COUNT = "click_count_agg";

    /** Log type constant for favorite logs. */
    public static final String LOG_TYPE_FAVORITE = "favorite";

    /** Log type constant for favorite count aggregation. */
    public static final String LOG_TYPE_FAVORITE_COUNT = "favorite_count_agg";

    /** Log type constant for user information logs. */
    public static final String LOG_TYPE_USERINFO = "user_info";

    /** Default page size for pagination. */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** Default current page number for pagination. */
    public static final int DEFAULT_CURRENT_PAGE_NUMBER = 1;

    /** Total number of records matching the search criteria. */
    private int allRecordCount;

    /** Total number of pages based on record count and page size. */
    private int allPageCount;

    /** Flag indicating if a previous page exists. */
    private boolean existPrePage;

    /** Flag indicating if a next page exists. */
    private boolean existNextPage;

    /** List of page numbers for pagination navigation. */
    private List<Integer> pageNumberList;

    /** Number of records to display per page. */
    private int pageSize;

    /** Current page number being displayed. */
    private int currentPageNumber;

    /** Type of log being displayed (default: search). */
    public String logType = LOG_TYPE_SEARCH;

    /** Query ID filter for search logs. */
    public String queryId;

    /** User session ID filter for search logs. */
    public String userSessionId;

    /** Time range filter for search logs (format: "yyyy-MM-dd HH:mm - yyyy-MM-dd HH:mm"). */
    public String requestedTimeRange;

    /** Access type filter for search logs (web, json, gsa, admin, other). */
    public String accessType;

    /**
     * Default constructor for creating a new SearchLogPager instance.
     */
    public SearchLogPager() {
        // Default constructor
    }

    /**
     * Clears all filter criteria and resets pagination to default values.
     */
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

    /**
     * Gets the default current page number.
     *
     * @return The default current page number
     */
    protected int getDefaultCurrentPageNumber() {
        return DEFAULT_CURRENT_PAGE_NUMBER;
    }

    /**
     * Gets the total number of records matching the search criteria.
     *
     * @return The total record count
     */
    public int getAllRecordCount() {
        return allRecordCount;
    }

    /**
     * Sets the total number of records matching the search criteria.
     *
     * @param allRecordCount The total record count
     */
    public void setAllRecordCount(final int allRecordCount) {
        this.allRecordCount = allRecordCount;
    }

    /**
     * Gets the total number of pages based on record count and page size.
     *
     * @return The total page count
     */
    public int getAllPageCount() {
        return allPageCount;
    }

    /**
     * Sets the total number of pages.
     *
     * @param allPageCount The total page count
     */
    public void setAllPageCount(final int allPageCount) {
        this.allPageCount = allPageCount;
    }

    /**
     * Checks if a previous page exists.
     *
     * @return true if a previous page exists, false otherwise
     */
    public boolean isExistPrePage() {
        return existPrePage;
    }

    /**
     * Sets whether a previous page exists.
     *
     * @param existPrePage true if a previous page exists
     */
    public void setExistPrePage(final boolean existPrePage) {
        this.existPrePage = existPrePage;
    }

    /**
     * Checks if a next page exists.
     *
     * @return true if a next page exists, false otherwise
     */
    public boolean isExistNextPage() {
        return existNextPage;
    }

    /**
     * Sets whether a next page exists.
     *
     * @param existNextPage true if a next page exists
     */
    public void setExistNextPage(final boolean existNextPage) {
        this.existNextPage = existNextPage;
    }

    /**
     * Gets the number of records to display per page.
     *
     * @return The page size
     */
    public int getPageSize() {
        if (pageSize <= 0) {
            pageSize = getDefaultPageSize();
        }
        return pageSize;
    }

    /**
     * Sets the number of records to display per page.
     *
     * @param pageSize The page size
     */
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Gets the current page number being displayed.
     *
     * @return The current page number
     */
    public int getCurrentPageNumber() {
        if (currentPageNumber <= 0) {
            currentPageNumber = getDefaultCurrentPageNumber();
        }
        return currentPageNumber;
    }

    /**
     * Sets the current page number.
     *
     * @param currentPageNumber The current page number
     */
    public void setCurrentPageNumber(final int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    /**
     * Gets the list of page numbers for pagination navigation.
     *
     * @return The page number list
     */
    public List<Integer> getPageNumberList() {
        return pageNumberList;
    }

    /**
     * Sets the list of page numbers for pagination navigation.
     *
     * @param pageNumberList The page number list
     */
    public void setPageNumberList(final List<Integer> pageNumberList) {
        this.pageNumberList = pageNumberList;
    }

    /**
     * Gets the default page size from configuration.
     *
     * @return The default page size
     */
    protected int getDefaultPageSize() {
        return ComponentUtil.getFessConfig().getPagingPageSizeAsInteger();
    }

}
