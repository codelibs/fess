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
 * Pager class for handling user list pagination.
 * This class manages pagination state and user search criteria for the user management interface.
 */
public class UserPager implements Serializable {

    /**
     * Default constructor for UserPager.
     */
    public UserPager() {
        // Default constructor
    }

    private static final long serialVersionUID = 1L;

    /** Default page size for user list pagination */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * Default current page number for pagination.
     */
    public static final int DEFAULT_CURRENT_PAGE_NUMBER = 1;

    /** The total number of records */
    private int allRecordCount;

    /** The total number of pages */
    private int allPageCount;

    /** Whether there is a previous page available */
    private boolean existPrePage;

    /** Whether there is a next page available */
    private boolean existNextPage;

    /** List of page numbers for navigation */
    private List<Integer> pageNumberList;

    /** Number of records per page */
    private int pageSize;

    /** Current page number */
    private int currentPageNumber;

    /** User ID for search filtering */
    public String id;

    /** User name for search filtering */
    public String name;

    /** User roles for search filtering */
    public String[] roles;

    /** User groups for search filtering */
    public String[] groups;

    /** Version number for optimistic locking */
    public String versionNo;

    /**
     * Clears all pagination state and search criteria.
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
        roles = null;
        groups = null;
        versionNo = null;

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
     * Gets the total number of records.
     *
     * @return the total record count
     */
    public int getAllRecordCount() {
        return allRecordCount;
    }

    /**
     * Sets the total number of records.
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
     * @return true if previous page exists, false otherwise
     */
    public boolean isExistPrePage() {
        return existPrePage;
    }

    /**
     * Sets whether there is a previous page available.
     *
     * @param existPrePage true if previous page exists, false otherwise
     */
    public void setExistPrePage(final boolean existPrePage) {
        this.existPrePage = existPrePage;
    }

    /**
     * Checks if there is a next page available.
     *
     * @return true if next page exists, false otherwise
     */
    public boolean isExistNextPage() {
        return existNextPage;
    }

    /**
     * Sets whether there is a next page available.
     *
     * @param existNextPage true if next page exists, false otherwise
     */
    public void setExistNextPage(final boolean existNextPage) {
        this.existNextPage = existNextPage;
    }

    /**
     * Gets the number of records per page.
     * Returns the default page size if not set or invalid.
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
     * Returns the default page number if not set or invalid.
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
     * @return the list of page numbers
     */
    public List<Integer> getPageNumberList() {
        return pageNumberList;
    }

    /**
     * Sets the list of page numbers for navigation.
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
