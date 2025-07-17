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
 * Pager for role type management.
 */
public class RoleTypePager implements Serializable {

    /**
     * Constructor.
     */
    public RoleTypePager() {
        super();
    }

    private static final long serialVersionUID = 1L;

    /**
     * Default page size for pagination.
     */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * Default current page number for pagination.
     */
    public static final int DEFAULT_CURRENT_PAGE_NUMBER = 1;

    /**
     * Total number of records.
     */
    private int allRecordCount;

    /**
     * Total number of pages.
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
     * Number of records per page.
     */
    private int pageSize;

    /**
     * Current page number in pagination.
     */
    private int currentPageNumber;

    /**
     * ID of the role type.
     */
    public String id;

    /**
     * Name of the role type.
     */
    public String name;

    /**
     * Value of the role type.
     */
    public String value;

    /**
     * Sort order of the role type.
     */
    public String sortOrder;

    /**
     * The user who created the role type.
     */
    public String createdBy;

    /**
     * The time when the role type was created.
     */
    public String createdTime;

    /**
     * Version number of the role type.
     */
    public String versionNo;

    /**
     * Clears the pager's state.
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
        value = null;
        sortOrder = null;
        createdBy = null;
        createdTime = null;
        versionNo = null;

    }

    /**
     * Gets the default current page number.
     * @return The default current page number.
     */
    protected int getDefaultCurrentPageNumber() {
        return DEFAULT_CURRENT_PAGE_NUMBER;
    }

    /**
     * Gets the total number of records.
     * @return The total number of records.
     */
    public int getAllRecordCount() {
        return allRecordCount;
    }

    /**
     * Sets the total number of records.
     * @param allRecordCount The total number of records.
     */
    public void setAllRecordCount(final int allRecordCount) {
        this.allRecordCount = allRecordCount;
    }

    /**
     * Gets the total number of pages.
     * @return The total number of pages.
     */
    public int getAllPageCount() {
        return allPageCount;
    }

    /**
     * Sets the total number of pages.
     * @param allPageCount The total number of pages.
     */
    public void setAllPageCount(final int allPageCount) {
        this.allPageCount = allPageCount;
    }

    /**
     * Checks if a previous page exists.
     * @return true if a previous page exists, false otherwise.
     */
    public boolean isExistPrePage() {
        return existPrePage;
    }

    /**
     * Sets whether a previous page exists.
     * @param existPrePage true if a previous page exists, false otherwise.
     */
    public void setExistPrePage(final boolean existPrePage) {
        this.existPrePage = existPrePage;
    }

    /**
     * Checks if a next page exists.
     * @return true if a next page exists, false otherwise.
     */
    public boolean isExistNextPage() {
        return existNextPage;
    }

    /**
     * Sets whether a next page exists.
     * @param existNextPage true if a next page exists, false otherwise.
     */
    public void setExistNextPage(final boolean existNextPage) {
        this.existNextPage = existNextPage;
    }

    /**
     * Gets the page size.
     * @return The page size.
     */
    public int getPageSize() {
        if (pageSize <= 0) {
            pageSize = getDefaultPageSize();
        }
        return pageSize;
    }

    /**
     * Sets the page size.
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
     * Gets the list of page numbers.
     * @return The list of page numbers.
     */
    public List<Integer> getPageNumberList() {
        return pageNumberList;
    }

    /**
     * Sets the list of page numbers.
     * @param pageNumberList The list of page numbers.
     */
    public void setPageNumberList(final List<Integer> pageNumberList) {
        this.pageNumberList = pageNumberList;
    }

    /**
     * Gets the default page size from the Fess configuration.
     * @return The default page size.
     */
    protected int getDefaultPageSize() {
        return ComponentUtil.getFessConfig().getPagingPageSizeAsInteger();
    }

}
