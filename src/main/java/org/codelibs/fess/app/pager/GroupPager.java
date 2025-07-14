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
 * GroupPager provides pagination functionality for group management operations in Fess.
 * This class manages the state and metadata required for paginated display of group lists,
 * including page navigation, record counts, and search criteria.
 *
 * @author FessProject
 */
public class GroupPager implements Serializable {

    /** Serial version UID for serialization compatibility. */
    private static final long serialVersionUID = 1L;

    /** Default number of records to display per page. */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** Default current page number when pagination starts. */
    public static final int DEFAULT_CURRENT_PAGE_NUMBER = 1;

    /** Total number of records available across all pages. */
    private int allRecordCount;

    /** Total number of pages available for pagination. */
    private int allPageCount;

    /** Flag indicating whether a previous page exists. */
    private boolean existPrePage;

    /** Flag indicating whether a next page exists. */
    private boolean existNextPage;

    /** List of page numbers to display in pagination navigation. */
    private List<Integer> pageNumberList;

    /** Number of records to display per page. */
    private int pageSize;

    /** Current page number being displayed. */
    private int currentPageNumber;

    /** Group ID for search filtering. */
    public String id;

    /** Group name for search filtering. */
    public String name;

    /** Version number for optimistic locking. */
    public String versionNo;

    /**
     * Clears all pagination data and search criteria, resetting the pager to its initial state.
     * This method resets record counts, pagination flags, and search parameters to their default values.
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
        versionNo = null;

    }

    /**
     * Returns the default current page number for pagination.
     *
     * @return the default current page number (typically 1)
     */
    protected int getDefaultCurrentPageNumber() {
        return DEFAULT_CURRENT_PAGE_NUMBER;
    }

    /**
     * Gets the total number of records available across all pages.
     *
     * @return the total record count
     */
    public int getAllRecordCount() {
        return allRecordCount;
    }

    /**
     * Sets the total number of records available across all pages.
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
     * Checks whether a previous page exists in the pagination.
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
     * Checks whether a next page exists in the pagination.
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
     * If the page size is not set or is invalid, returns the default page size.
     *
     * @return the number of records per page
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
     * @param pageSize the number of records per page to set
     */
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Gets the current page number being displayed.
     * If the current page number is not set or is invalid, returns the default current page number.
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
     * Gets the list of page numbers to display in pagination navigation.
     *
     * @return the list of page numbers for navigation
     */
    public List<Integer> getPageNumberList() {
        return pageNumberList;
    }

    /**
     * Sets the list of page numbers to display in pagination navigation.
     *
     * @param pageNumberList the list of page numbers to set
     */
    public void setPageNumberList(final List<Integer> pageNumberList) {
        this.pageNumberList = pageNumberList;
    }

    /**
     * Gets the default page size from the Fess configuration.
     *
     * @return the default page size configured in the system
     */
    protected int getDefaultPageSize() {
        return ComponentUtil.getFessConfig().getPagingPageSizeAsInteger();
    }

}
