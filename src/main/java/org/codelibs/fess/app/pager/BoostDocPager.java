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

import org.codelibs.fess.Constants;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Pager class for boost document management with pagination support.
 * Provides pagination functionality for boost document rule lists.
 */
public class BoostDocPager implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Default current page number. */
    public static final int DEFAULT_CURRENT_PAGE_NUMBER = 1;

    /** Total number of records. */
    private int allRecordCount;

    /** Total number of pages. */
    private int allPageCount;

    /** Indicates if a previous page exists. */
    private boolean existPrePage;

    /** Indicates if a next page exists. */
    private boolean existNextPage;

    /** List of page numbers for pagination navigation. */
    private List<Integer> pageNumberList;

    /** Page size for pagination. */
    private int pageSize;

    /** Current page number for pagination. */
    private int currentPageNumber;

    /** Boost document ID for search filtering. */
    public String id;

    /** URL expression for search filtering. */
    public String urlExpr;

    /** Boost expression for search filtering. */
    public String boostExpr;

    /** Sort order for search filtering. */
    public String sortOrder;

    /** Creator username for search filtering. */
    public String createdBy;

    /** Creation time for search filtering. */
    public String createdTime;

    /** Version number for search filtering. */
    public String versionNo;

    /**
     * Default constructor for BoostDocPager.
     */
    public BoostDocPager() {
        // Default constructor
    }

    /**
     * Clears all search criteria and resets pagination settings.
     */
    public void clear() {
        allRecordCount = 0;
        allPageCount = 0;
        existPrePage = false;
        existNextPage = false;
        pageSize = getDefaultPageSize();
        currentPageNumber = getDefaultCurrentPageNumber();

        id = null;
        urlExpr = null;
        boostExpr = null;
        sortOrder = null;
        createdBy = null;
        createdTime = null;
        versionNo = null;

    }

    /**
     * Gets the total number of records.
     * @return The total record count.
     */
    public int getAllRecordCount() {
        return allRecordCount;
    }

    /**
     * Sets the total number of records.
     * @param allRecordCount The total record count.
     */
    public void setAllRecordCount(final int allRecordCount) {
        this.allRecordCount = allRecordCount;
    }

    /**
     * Gets the total number of pages.
     * @return The total page count.
     */
    public int getAllPageCount() {
        return allPageCount;
    }

    /**
     * Sets the total number of pages.
     * @param allPageCount The total page count.
     */
    public void setAllPageCount(final int allPageCount) {
        this.allPageCount = allPageCount;
    }

    /**
     * Checks if there is a previous page.
     * @return True if a previous page exists, false otherwise.
     */
    public boolean isExistPrePage() {
        return existPrePage;
    }

    /**
     * Sets the existence of a previous page.
     * @param existPrePage True if a previous page exists.
     */
    public void setExistPrePage(final boolean existPrePage) {
        this.existPrePage = existPrePage;
    }

    /**
     * Checks if there is a next page.
     * @return True if a next page exists, false otherwise.
     */
    public boolean isExistNextPage() {
        return existNextPage;
    }

    /**
     * Sets the existence of a next page.
     * @param existNextPage True if a next page exists.
     */
    public void setExistNextPage(final boolean existNextPage) {
        this.existNextPage = existNextPage;
    }

    /**
     * Gets the page size for pagination.
     * @return The page size.
     */
    public int getPageSize() {
        if (pageSize <= 0) {
            pageSize = getDefaultPageSize();
        }
        return pageSize;
    }

    /**
     * Sets the page size for pagination.
     * @param pageSize The page size.
     */
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Gets the current page number.
     * @return The current page number.
     */
    public int getCurrentPageNumber() {
        if (currentPageNumber <= 0) {
            currentPageNumber = getDefaultCurrentPageNumber();
        }
        return currentPageNumber;
    }

    /**
     * Sets the current page number.
     * @param currentPageNumber The current page number.
     */
    public void setCurrentPageNumber(final int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    /**
     * Gets the list of page numbers for pagination navigation.
     * @return The list of page numbers.
     */
    public List<Integer> getPageNumberList() {
        return pageNumberList;
    }

    /**
     * Sets the list of page numbers for pagination navigation.
     * @param pageNumberList The list of page numbers.
     */
    public void setPageNumberList(final List<Integer> pageNumberList) {
        this.pageNumberList = pageNumberList;
    }

    /**
     * Gets the default current page number from constants.
     * @return The default current page number.
     */
    protected int getDefaultCurrentPageNumber() {
        return Constants.DEFAULT_ADMIN_PAGE_NUMBER;
    }

    /**
     * Gets the default page size from configuration.
     * @return The default page size.
     */
    protected int getDefaultPageSize() {
        return ComponentUtil.getFessConfig().getPagingPageSizeAsInteger();
    }

}
