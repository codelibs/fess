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
 * Pager class for managing job log pagination in the admin interface.
 * This class handles pagination functionality for job log listings and provides
 * search criteria for filtering job logs.
 */
public class JobLogPager implements Serializable {

    /** Serial version UID for serialization */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public JobLogPager() {
        // Default constructor
    }

    /** Default page size for pagination */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** Default current page number */
    public static final int DEFAULT_CURRENT_PAGE_NUMBER = 1;

    /** Total number of records */
    private int allRecordCount;

    /** Total number of pages */
    private int allPageCount;

    /** Whether a previous page exists */
    private boolean existPrePage;

    /** Whether a next page exists */
    private boolean existNextPage;

    /** List of page numbers for pagination display */
    private List<Integer> pageNumberList;

    /** Number of items per page */
    private int pageSize;

    /** Current page number */
    private int currentPageNumber;

    /** Search criteria: job ID */
    public String id;

    /** Search criteria: job name */
    public String jobName;

    /** Search criteria: job status */
    public String jobStatus;

    /** Search criteria: target */
    public String target;

    /** Search criteria: script type */
    public String scriptType;

    /** Search criteria: start time */
    public String startTime;

    /**
     * Clears all pagination data and search criteria.
     */
    public void clear() {
        allRecordCount = 0;
        allPageCount = 0;
        existPrePage = false;
        existNextPage = false;
        pageSize = getDefaultPageSize();
        currentPageNumber = getDefaultCurrentPageNumber();

        id = null;
        jobName = null;
        jobStatus = null;
        target = null;
        scriptType = null;
        startTime = null;

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
     * @return the total number of records
     */
    public int getAllRecordCount() {
        return allRecordCount;
    }

    /**
     * Sets the total number of records across all pages.
     *
     * @param allRecordCount the total number of records
     */
    public void setAllRecordCount(final int allRecordCount) {
        this.allRecordCount = allRecordCount;
    }

    /**
     * Gets the total number of pages.
     *
     * @return the total number of pages
     */
    public int getAllPageCount() {
        return allPageCount;
    }

    /**
     * Sets the total number of pages.
     *
     * @param allPageCount the total number of pages
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
     * Gets the number of items per page.
     * If the page size is not set or is zero or negative, returns the default page size.
     *
     * @return the number of items per page
     */
    public int getPageSize() {
        if (pageSize <= 0) {
            pageSize = getDefaultPageSize();
        }
        return pageSize;
    }

    /**
     * Sets the number of items per page.
     *
     * @param pageSize the number of items per page
     */
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Gets the current page number.
     * If the current page number is not set or is zero or negative, returns the default current page number.
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
     * Gets the list of page numbers for pagination display.
     * This list is typically used to render pagination controls in the UI.
     *
     * @return the list of page numbers
     */
    public List<Integer> getPageNumberList() {
        return pageNumberList;
    }

    /**
     * Sets the list of page numbers for pagination display.
     *
     * @param pageNumberList the list of page numbers
     */
    public void setPageNumberList(final List<Integer> pageNumberList) {
        this.pageNumberList = pageNumberList;
    }

    /**
     * Gets the default page size from configuration.
     *
     * @return the default page size
     */
    protected int getDefaultPageSize() {
        return ComponentUtil.getFessConfig().getPagingPageSizeAsInteger();
    }

}
