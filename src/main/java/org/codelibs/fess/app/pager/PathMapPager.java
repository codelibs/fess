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
 * Pager for path mapping.
 */
public class PathMapPager implements Serializable {

    /**
     * Default constructor.
     */
    public PathMapPager() {
        // Default constructor
    }

    private static final long serialVersionUID = 1L;

    /** Default page size. */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** Default current page number. */
    public static final int DEFAULT_CURRENT_PAGE_NUMBER = 1;

    /** The total number of records. */
    private int allRecordCount;

    /** The total number of pages. */
    private int allPageCount;

    /** Indicates if a previous page exists. */
    private boolean existPrePage;

    /** Indicates if a next page exists. */
    private boolean existNextPage;

    /** The list of page numbers. */
    private List<Integer> pageNumberList;

    /** The number of records per page. */
    private int pageSize;

    /** The current page number. */
    private int currentPageNumber;

    /** The ID. */
    public String id;

    /** The regex pattern. */
    public String regex;

    /** The replacement string. */
    public String replacement;

    /** The process type. */
    public String processType;

    /** The sort order. */
    public String sortOrder;

    /** The creator. */
    public String createdBy;

    /** The creation time. */
    public String createdTime;

    /** The version number. */
    public String versionNo;

    /**
     * Clears all fields.
     */
    public void clear() {
        allRecordCount = 0;
        allPageCount = 0;
        existPrePage = false;
        existNextPage = false;
        pageSize = getDefaultPageSize();
        currentPageNumber = getDefaultCurrentPageNumber();

        id = null;
        regex = null;
        replacement = null;
        processType = null;
        sortOrder = null;
        createdBy = null;
        createdTime = null;
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
     * Gets the total record count.
     *
     * @return the total record count
     */
    public int getAllRecordCount() {
        return allRecordCount;
    }

    /**
     * Sets the total record count.
     *
     * @param allRecordCount the total record count
     */
    public void setAllRecordCount(final int allRecordCount) {
        this.allRecordCount = allRecordCount;
    }

    /**
     * Gets the total page count.
     *
     * @return the total page count
     */
    public int getAllPageCount() {
        return allPageCount;
    }

    /**
     * Sets the total page count.
     *
     * @param allPageCount the total page count
     */
    public void setAllPageCount(final int allPageCount) {
        this.allPageCount = allPageCount;
    }

    /**
     * Checks if previous page exists.
     *
     * @return true if previous page exists
     */
    public boolean isExistPrePage() {
        return existPrePage;
    }

    /**
     * Sets whether previous page exists.
     *
     * @param existPrePage true if previous page exists
     */
    public void setExistPrePage(final boolean existPrePage) {
        this.existPrePage = existPrePage;
    }

    /**
     * Checks if next page exists.
     *
     * @return true if next page exists
     */
    public boolean isExistNextPage() {
        return existNextPage;
    }

    /**
     * Sets whether next page exists.
     *
     * @param existNextPage true if next page exists
     */
    public void setExistNextPage(final boolean existNextPage) {
        this.existNextPage = existNextPage;
    }

    /**
     * Gets the page size.
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
     * Sets the page size.
     *
     * @param pageSize the page size
     */
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Gets the current page number.
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
     * Gets the page number list.
     *
     * @return the page number list
     */
    public List<Integer> getPageNumberList() {
        return pageNumberList;
    }

    /**
     * Sets the page number list.
     *
     * @param pageNumberList the page number list
     */
    public void setPageNumberList(final List<Integer> pageNumberList) {
        this.pageNumberList = pageNumberList;
    }

    /**
     * Gets the default page size.
     *
     * @return the default page size
     */
    protected int getDefaultPageSize() {
        return ComponentUtil.getFessConfig().getPagingPageSizeAsInteger();
    }

}
