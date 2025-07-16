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
 * Pager class for related query management functionality.
 * Provides pagination support for related query operations including
 * search, display, and navigation through related query results.
 */
public class RelatedQueryPager implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Default current page number for pagination.
     */
    public static final int DEFAULT_CURRENT_PAGE_NUMBER = 1;

    /**
     * Total number of records in the result set.
     */
    private int allRecordCount;

    /**
     * Total number of pages available for pagination.
     */
    private int allPageCount;

    /**
     * Flag indicating whether a previous page exists.
     */
    private boolean existPrePage;

    /**
     * Flag indicating whether a next page exists.
     */
    private boolean existNextPage;

    /**
     * List of page numbers for pagination navigation.
     */
    private List<Integer> pageNumberList;

    /**
     * Number of records to display per page.
     */
    private int pageSize;

    /**
     * Current page number being displayed.
     */
    private int currentPageNumber;

    /**
     * Unique identifier for the related query.
     */
    public String id;

    /**
     * Search term for the related query.
     */
    public String term;

    /**
     * Related queries string containing associated search queries.
     */
    public String queries;

    /**
     * User who created the related query entry.
     */
    public String createdBy;

    /**
     * Timestamp when the related query was created.
     */
    public String createdTime;

    /**
     * Version number for optimistic locking.
     */
    public String versionNo;

    /**
     * Default constructor for RelatedQueryPager.
     * Initializes a new instance with default values for pagination
     * and related query fields.
     */
    public RelatedQueryPager() {
        // Default constructor - fields will be initialized to default values
    }

    /**
     * Clears all pager data and resets to default values.
     * Resets pagination state and clears all related query fields.
     */
    public void clear() {
        allRecordCount = 0;
        allPageCount = 0;
        existPrePage = false;
        existNextPage = false;
        pageSize = getDefaultPageSize();
        currentPageNumber = getDefaultCurrentPageNumber();

        id = null;
        term = null;
        queries = null;
        createdBy = null;
        createdTime = null;
        versionNo = null;

    }

    /**
     * Gets the total number of records in the result set.
     *
     * @return the total record count
     */
    public int getAllRecordCount() {
        return allRecordCount;
    }

    /**
     * Sets the total number of records in the result set.
     *
     * @param allRecordCount the total record count to set
     */
    public void setAllRecordCount(final int allRecordCount) {
        this.allRecordCount = allRecordCount;
    }

    /**
     * Gets the total number of pages available for pagination.
     *
     * @return the total page count
     */
    public int getAllPageCount() {
        return allPageCount;
    }

    /**
     * Sets the total number of pages available for pagination.
     *
     * @param allPageCount the total page count to set
     */
    public void setAllPageCount(final int allPageCount) {
        this.allPageCount = allPageCount;
    }

    /**
     * Checks if a previous page exists for pagination.
     *
     * @return true if a previous page exists, false otherwise
     */
    public boolean isExistPrePage() {
        return existPrePage;
    }

    /**
     * Sets the flag indicating whether a previous page exists.
     *
     * @param existPrePage true if a previous page exists, false otherwise
     */
    public void setExistPrePage(final boolean existPrePage) {
        this.existPrePage = existPrePage;
    }

    /**
     * Checks if a next page exists for pagination.
     *
     * @return true if a next page exists, false otherwise
     */
    public boolean isExistNextPage() {
        return existNextPage;
    }

    /**
     * Sets the flag indicating whether a next page exists.
     *
     * @param existNextPage true if a next page exists, false otherwise
     */
    public void setExistNextPage(final boolean existNextPage) {
        this.existNextPage = existNextPage;
    }

    /**
     * Gets the number of records to display per page.
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
     * Sets the number of records to display per page.
     *
     * @param pageSize the page size to set
     */
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Gets the current page number being displayed.
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
     * Sets the current page number being displayed.
     *
     * @param currentPageNumber the current page number to set
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
     * @param pageNumberList the list of page numbers to set
     */
    public void setPageNumberList(final List<Integer> pageNumberList) {
        this.pageNumberList = pageNumberList;
    }

    /**
     * Gets the default current page number from system constants.
     *
     * @return the default current page number
     */
    protected int getDefaultCurrentPageNumber() {
        return Constants.DEFAULT_ADMIN_PAGE_NUMBER;
    }

    /**
     * Gets the default page size from system configuration.
     *
     * @return the default page size
     */
    protected int getDefaultPageSize() {
        return ComponentUtil.getFessConfig().getPagingPageSizeAsInteger();
    }

}
