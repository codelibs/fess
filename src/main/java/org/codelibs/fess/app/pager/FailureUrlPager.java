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
 * A pager class for managing pagination of failure URL records.
 * This class provides functionality to handle pagination of failed crawler URLs
 * with search filters and navigation capabilities.
 */
public class FailureUrlPager implements Serializable {

    /** Serial version UID for serialization compatibility. */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public FailureUrlPager() {
        // Default constructor
    }

    /** URL filter for searching failure URLs. */
    //@Maxbytelength(maxbytelength = 1000)
    public String url;

    /** Minimum error count filter for searching failure URLs. */
    //@IntRange(min = 0, max = 2147483647)
    public String errorCountMin;

    /** Maximum error count filter for searching failure URLs. */
    //@IntRange(min = 0, max = 2147483647)
    public String errorCountMax;

    /** Error name filter for searching failure URLs. */
    //@Maxbytelength(maxbytelength = 1000)
    public String errorName;

    /** Default number of records per page. */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** Default current page number. */
    public static final int DEFAULT_CURRENT_PAGE_NUMBER = 1;

    /** Total number of records across all pages. */
    private int allRecordCount;

    /** Total number of pages. */
    private int allPageCount;

    /** Flag indicating whether a previous page exists. */
    private boolean existPrePage;

    /** Flag indicating whether a next page exists. */
    private boolean existNextPage;

    /** List of page numbers for navigation. */
    private List<Integer> pageNumberList;

    /** Number of records per page. */
    private int pageSize;

    /** Current page number. */
    private int currentPageNumber;

    /** ID of the failure URL record. */
    public String id;

    /** Name of the thread that encountered the failure. */
    public String threadName;

    /** Number of errors encountered for this URL. */
    public String errorCount;

    /** Last time this URL was accessed. */
    public String lastAccessTime;

    /**
     * Clears all pager data and resets to default values.
     * This method resets pagination state and search filters.
     */
    public void clear() {
        allRecordCount = 0;
        allPageCount = 0;
        existPrePage = false;
        existNextPage = false;
        pageSize = getDefaultPageSize();
        currentPageNumber = getDefaultCurrentPageNumber();

        id = null;
        url = null;
        threadName = null;
        errorCount = null;
        lastAccessTime = null;
        errorCountMin = null;
        errorCountMax = null;
        errorName = null;

    }

    /**
     * Gets the default current page number.
     *
     * @return the default current page number
     */
    protected int getDefaultCurrentPageNumber() {
        return DEFAULT_CURRENT_PAGE_NUMBER;
    }

    /**
     * Gets the total number of records across all pages.
     *
     * @return the total record count
     */
    public int getAllRecordCount() {
        return allRecordCount;
    }

    /**
     * Sets the total number of records across all pages.
     *
     * @param allRecordCount the total record count
     */
    public void setAllRecordCount(final int allRecordCount) {
        this.allRecordCount = allRecordCount;
    }

    /**
     * Gets the total number of pages.
     *
     * @return the total page count
     */
    public int getAllPageCount() {
        return allPageCount;
    }

    /**
     * Sets the total number of pages.
     *
     * @param allPageCount the total page count
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
     * @param existPrePage true if a previous page exists, false otherwise
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
     * @param existNextPage true if a next page exists, false otherwise
     */
    public void setExistNextPage(final boolean existNextPage) {
        this.existNextPage = existNextPage;
    }

    /**
     * Gets the number of records per page.
     * If not set or invalid, returns the default page size.
     *
     * @return the page size
     */
    public int getPageSize() {
        if (pageSize <= 0) {
            pageSize = getDefaultPageSize();
        }
        return pageSize;
    }

    /**
     * Sets the number of records per page.
     *
     * @param pageSize the page size
     */
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Gets the current page number.
     * If not set or invalid, returns the default current page number.
     *
     * @return the current page number
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
     * @param currentPageNumber the current page number
     */
    public void setCurrentPageNumber(final int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    /**
     * Gets the list of page numbers for navigation.
     *
     * @return the page number list
     */
    public List<Integer> getPageNumberList() {
        return pageNumberList;
    }

    /**
     * Sets the list of page numbers for navigation.
     *
     * @param pageNumberList the page number list
     */
    public void setPageNumberList(final List<Integer> pageNumberList) {
        this.pageNumberList = pageNumberList;
    }

    /**
     * Gets the default page size from the Fess configuration.
     *
     * @return the default page size
     */
    protected int getDefaultPageSize() {
        return ComponentUtil.getFessConfig().getPagingPageSizeAsInteger();
    }

}
