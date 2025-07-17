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
 * Pager class for stemmer override dictionary management.
 *
 * This class provides pagination functionality for displaying stemmer override
 * dictionary entries in the administrative interface. It manages page state,
 * navigation controls, and provides methods for calculating page boundaries
 * and navigation elements.
 */
public class StemmerOverridePager implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The total number of records across all pages. */
    private int allRecordCount;

    /** The total number of pages. */
    private int allPageCount;

    /** Flag indicating whether a previous page exists. */
    private boolean existPrePage;

    /** Flag indicating whether a next page exists. */
    private boolean existNextPage;

    /** List of page numbers for navigation display. */
    private List<Integer> pageNumberList;

    /** The number of records to display per page. */
    private int pageSize;

    /** The current page number (1-based). */
    private int currentPageNumber;

    /** The ID of the stemmer override dictionary. */
    public String id;

    /**
     * Default constructor.
     */
    public StemmerOverridePager() {
        // Default constructor
    }

    /**
     * Clears all pagination state and resets to default values.
     */
    public void clear() {
        allRecordCount = 0;
        allPageCount = 0;
        existPrePage = false;
        existNextPage = false;
        pageSize = getDefaultPageSize();
        currentPageNumber = getDefaultCurrentPageNumber();

        id = null;
    }

    /**
     * Gets the default page size from configuration.
     *
     * @return The default number of records per page
     */
    protected int getDefaultPageSize() {
        return ComponentUtil.getFessConfig().getPagingPageSizeAsInteger();
    }

    /**
     * Gets the default current page number.
     *
     * @return The default current page number (1)
     */
    protected int getDefaultCurrentPageNumber() {
        return 1;
    }

    /**
     * Gets the total number of records across all pages.
     *
     * @return The total record count
     */
    public int getAllRecordCount() {
        return allRecordCount;
    }

    /**
     * Sets the total number of records across all pages.
     *
     * @param allRecordCount The total record count to set
     */
    public void setAllRecordCount(final int allRecordCount) {
        this.allRecordCount = allRecordCount;
    }

    /**
     * Gets the total number of pages.
     *
     * @return The total page count
     */
    public int getAllPageCount() {
        return allPageCount;
    }

    /**
     * Sets the total number of pages.
     *
     * @param allPageCount The total page count to set
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
     * Gets the number of records to display per page.
     * If not set or invalid, returns the default page size.
     *
     * @return The page size
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
     * @param pageSize The page size to set
     */
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Gets the current page number (1-based).
     * If not set or invalid, returns the default current page number.
     *
     * @return The current page number
     */
    public int getCurrentPageNumber() {
        if (currentPageNumber <= 0) {
            currentPageNumber = getDefaultCurrentPageNumber();
        }
        return currentPageNumber;
    }

    /**
     * Sets the current page number (1-based).
     *
     * @param currentPageNumber The current page number to set
     */
    public void setCurrentPageNumber(final int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    /**
     * Gets the list of page numbers for navigation display.
     *
     * @return The list of page numbers
     */
    public List<Integer> getPageNumberList() {
        return pageNumberList;
    }

    /**
     * Sets the list of page numbers for navigation display.
     *
     * @param pageNumberList The list of page numbers to set
     */
    public void setPageNumberList(final List<Integer> pageNumberList) {
        this.pageNumberList = pageNumberList;
    }
}
