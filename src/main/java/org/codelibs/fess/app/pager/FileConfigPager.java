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
 * Pager class for file crawler configuration management.
 * This class provides pagination functionality for file configuration listings
 * and contains form fields for file crawler configuration parameters.
 */
public class FileConfigPager implements Serializable {

    /** Serial version UID for serialization */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor for file configuration pager.
     * Creates a new instance with default values.
     */
    public FileConfigPager() {
        // Default constructor
    }

    /** Default page size for pagination */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** Default current page number for pagination */
    public static final int DEFAULT_CURRENT_PAGE_NUMBER = 1;

    /** Total number of records across all pages */
    private int allRecordCount;

    /** Total number of pages */
    private int allPageCount;

    /** Flag indicating if a previous page exists */
    private boolean existPrePage;

    /** Flag indicating if a next page exists */
    private boolean existNextPage;

    /** List of page numbers for pagination display */
    private List<Integer> pageNumberList;

    /** Number of records per page */
    private int pageSize;

    /** Current page number */
    private int currentPageNumber;

    /** Configuration ID */
    public String id;

    /** Configuration name */
    public String name;

    /** File system paths to crawl */
    public String paths;

    /** Number of threads for crawling */
    public String numOfThread;

    /** Interval time between crawling operations */
    public String intervalTime;

    /** Boost value for search ranking */
    public String boost;

    /** Availability status of the configuration */
    public String available;

    /** Sort order for the configuration */
    public String sortOrder;

    /** User who created the configuration */
    public String createdBy;

    /** Creation timestamp of the configuration */
    public String createdTime;

    /** Version number of the configuration */
    public String versionNo;

    /** Description of the configuration */
    public String description;

    /**
     * Clears all pagination and configuration data, resetting to default values.
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
        paths = null;
        numOfThread = null;
        intervalTime = null;
        boost = null;
        available = null;
        sortOrder = null;
        createdBy = null;
        createdTime = null;
        versionNo = null;
        description = null;

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
     * Sets the flag indicating if a previous page exists.
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
     * Sets the flag indicating if a next page exists.
     *
     * @param existNextPage true if a next page exists, false otherwise
     */
    public void setExistNextPage(final boolean existNextPage) {
        this.existNextPage = existNextPage;
    }

    /**
     * Gets the number of records per page.
     * If page size is not set or is invalid, returns the default page size.
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
     * If current page number is not set or is invalid, returns the default current page number.
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
     * Gets the default page size from the Fess configuration.
     *
     * @return the default page size
     */
    protected int getDefaultPageSize() {
        return ComponentUtil.getFessConfig().getPagingPageSizeAsInteger();
    }

}
