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
 * Pager for related content management that provides pagination functionality
 * for displaying related content items in the admin interface.
 * This class handles pagination state including current page number, page size,
 * total record count, and navigation controls.
 */
public class RelatedContentPager implements Serializable {

    /**
     * Default constructor.
     */
    public RelatedContentPager() {
        // Default constructor
    }

    private static final long serialVersionUID = 1L;

    /** Default current page number constant. */
    public static final int DEFAULT_CURRENT_PAGE_NUMBER = 1;

    /** Total number of records across all pages. */
    private int allRecordCount;

    /** Total number of pages calculated from record count and page size. */
    private int allPageCount;

    /** Flag indicating whether a previous page exists. */
    private boolean existPrePage;

    /** Flag indicating whether a next page exists. */
    private boolean existNextPage;

    /** List of page numbers for pagination navigation. */
    private List<Integer> pageNumberList;

    /** Number of records to display per page. */
    private int pageSize;

    /** Current page number being displayed. */
    private int currentPageNumber;

    /** Related content ID for filtering. */
    public String id;

    /** Search term for filtering related content. */
    public String term;

    /** Content text for filtering related content. */
    public String content;

    /** User who created the related content. */
    public String createdBy;

    /** Creation time of the related content. */
    public String createdTime;

    /** Version number for optimistic locking. */
    public String versionNo;

    /**
     * Clears all pagination state and filter parameters.
     * Resets pagination counters to default values and clears all filter fields.
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
        content = null;
        createdBy = null;
        createdTime = null;
        versionNo = null;

    }

    /**
     * Gets the total number of records across all pages.
     *
     * @return total record count
     */
    public int getAllRecordCount() {
        return allRecordCount;
    }

    /**
     * Sets the total number of records across all pages.
     *
     * @param allRecordCount total record count
     */
    public void setAllRecordCount(final int allRecordCount) {
        this.allRecordCount = allRecordCount;
    }

    /**
     * Gets the total number of pages calculated from record count and page size.
     *
     * @return total page count
     */
    public int getAllPageCount() {
        return allPageCount;
    }

    /**
     * Sets the total number of pages.
     *
     * @param allPageCount total page count
     */
    public void setAllPageCount(final int allPageCount) {
        this.allPageCount = allPageCount;
    }

    /**
     * Checks if a previous page exists.
     *
     * @return true if previous page exists, false otherwise
     */
    public boolean isExistPrePage() {
        return existPrePage;
    }

    /**
     * Sets whether a previous page exists.
     *
     * @param existPrePage true if previous page exists, false otherwise
     */
    public void setExistPrePage(final boolean existPrePage) {
        this.existPrePage = existPrePage;
    }

    /**
     * Checks if a next page exists.
     *
     * @return true if next page exists, false otherwise
     */
    public boolean isExistNextPage() {
        return existNextPage;
    }

    /**
     * Sets whether a next page exists.
     *
     * @param existNextPage true if next page exists, false otherwise
     */
    public void setExistNextPage(final boolean existNextPage) {
        this.existNextPage = existNextPage;
    }

    /**
     * Gets the number of records to display per page.
     * If page size is not set or is invalid, returns the default page size.
     *
     * @return page size
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
     * @param pageSize page size
     */
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Gets the current page number being displayed.
     * If current page number is not set or is invalid, returns the default current page number.
     *
     * @return current page number
     */
    public int getCurrentPageNumber() {
        if (currentPageNumber <= 0) {
            currentPageNumber = getDefaultCurrentPageNumber();
        }
        return currentPageNumber;
    }

    /**
     * Sets the current page number to display.
     *
     * @param currentPageNumber current page number
     */
    public void setCurrentPageNumber(final int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    /**
     * Gets the list of page numbers for pagination navigation.
     *
     * @return list of page numbers
     */
    public List<Integer> getPageNumberList() {
        return pageNumberList;
    }

    /**
     * Sets the list of page numbers for pagination navigation.
     *
     * @param pageNumberList list of page numbers
     */
    public void setPageNumberList(final List<Integer> pageNumberList) {
        this.pageNumberList = pageNumberList;
    }

    /**
     * Gets the default current page number from system constants.
     *
     * @return default current page number
     */
    protected int getDefaultCurrentPageNumber() {
        return Constants.DEFAULT_ADMIN_PAGE_NUMBER;
    }

    /**
     * Gets the default page size from system configuration.
     *
     * @return default page size
     */
    protected int getDefaultPageSize() {
        return ComponentUtil.getFessConfig().getPagingPageSizeAsInteger();
    }

}
