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
 * Pager for data configuration management with standard paging functionality.
 * This class provides pagination support for data configuration listings in the admin interface,
 * including navigation controls and search/filter parameters.
 */
public class DataConfigPager implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Default page size for pagination. */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** Default current page number (first page). */
    public static final int DEFAULT_CURRENT_PAGE_NUMBER = 1;

    /** Total number of records across all pages. */
    private int allRecordCount;

    /** Total number of pages. */
    private int allPageCount;

    /** Flag indicating if there is a previous page. */
    private boolean existPrePage;

    /** Flag indicating if there is a next page. */
    private boolean existNextPage;

    /** List of page numbers for pagination navigation. */
    private List<Integer> pageNumberList;

    /** Number of records to display per page. */
    private int pageSize;

    /** Current page number being displayed. */
    private int currentPageNumber;

    /** Search/filter parameter for data configuration ID. */
    public String id;

    /** Search/filter parameter for data configuration name. */
    public String name;

    /** Search/filter parameter for data configuration handler name. */
    public String handlerName;

    /** Search/filter parameter for data configuration boost value. */
    public String boost;

    /** Search/filter parameter for data configuration availability status. */
    public String available;

    /** Search/filter parameter for data configuration sort order. */
    public String sortOrder;

    /** Search/filter parameter for data configuration creator. */
    public String createdBy;

    /** Search/filter parameter for data configuration creation time. */
    public String createdTime;

    /** Search/filter parameter for data configuration version number. */
    public String versionNo;

    /** Search/filter parameter for data configuration description. */
    public String description;

    /**
     * Creates a new DataConfigPager with default values.
     * Initializes pagination settings and clears all search parameters.
     */
    public DataConfigPager() {
        // Default constructor with explicit documentation
    }

    /**
     * Clears all paging state and search/filter parameters.
     * Resets the pager to its initial state with default values.
     */
    public void clear() {
        allRecordCount = 0;
        allPageCount = 0;
        existPrePage = false;
        existNextPage = false;
        pageSize = getDefaultPageSize();
        currentPageNumber = getDefaultCurrentPageNumber();

        id = null;
        name = null;
        handlerName = null;
        boost = null;
        available = null;
        sortOrder = null;
        createdBy = null;
        createdTime = null;
        versionNo = null;
        description = null;

    }

    /**
     * Returns the default current page number.
     *
     * @return the default current page number (1)
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
     * Checks if there is a previous page available.
     *
     * @return true if there is a previous page, false otherwise
     */
    public boolean isExistPrePage() {
        return existPrePage;
    }

    /**
     * Sets whether there is a previous page available.
     *
     * @param existPrePage true if there is a previous page, false otherwise
     */
    public void setExistPrePage(final boolean existPrePage) {
        this.existPrePage = existPrePage;
    }

    /**
     * Checks if there is a next page available.
     *
     * @return true if there is a next page, false otherwise
     */
    public boolean isExistNextPage() {
        return existNextPage;
    }

    /**
     * Sets whether there is a next page available.
     *
     * @param existNextPage true if there is a next page, false otherwise
     */
    public void setExistNextPage(final boolean existNextPage) {
        this.existNextPage = existNextPage;
    }

    /**
     * Gets the number of records to display per page.
     * If the page size is not set or is invalid, returns the default page size.
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
     * Sets the number of records to display per page.
     *
     * @param pageSize the page size
     */
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Gets the current page number being displayed.
     * If the current page number is not set or is invalid, returns the default page number.
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
     * Sets the current page number being displayed.
     *
     * @param currentPageNumber the current page number
     */
    public void setCurrentPageNumber(final int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    /**
     * Gets the list of page numbers for pagination navigation.
     *
     * @return the list of page numbers
     */
    public List<Integer> getPageNumberList() {
        return pageNumberList;
    }

    /**
     * Sets the list of page numbers for pagination navigation.
     *
     * @param pageNumberList the list of page numbers
     */
    public void setPageNumberList(final List<Integer> pageNumberList) {
        this.pageNumberList = pageNumberList;
    }

    /**
     * Returns the default page size from the system configuration.
     *
     * @return the default page size configured in the system
     */
    protected int getDefaultPageSize() {
        return ComponentUtil.getFessConfig().getPagingPageSizeAsInteger();
    }

}
