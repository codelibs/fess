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
 * Pager for managing synonym pagination.
 * This class handles the state and logic for paginating through a list of synonyms,
 * including total record count, page size, and current page number.
 */
public class SynonymPager implements Serializable {

    /**
     * Constructs a new pager for synonyms.
     */
    public SynonymPager() {
        // do nothing
    }

    private static final long serialVersionUID = 1L;

    /** The total number of records. */
    private int allRecordCount;

    /** The total number of pages. */
    private int allPageCount;

    /** A flag indicating if a previous page exists. */
    private boolean existPrePage;

    /** A flag indicating if a next page exists. */
    private boolean existNextPage;

    /** A list of page numbers for navigation. */
    private List<Integer> pageNumberList;

    /** The number of records per page. */
    private int pageSize;

    /** The current page number. */
    private int currentPageNumber;

    /** The ID of the synonym dictionary. */
    public String id;

    /**
     * Clears the pager's state to its default values.
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
     * Gets the default page size from the Fess configuration.
     *
     * @return The default page size.
     */
    protected int getDefaultPageSize() {
        return ComponentUtil.getFessConfig().getPagingPageSizeAsInteger();
    }

    /**
     * Gets the default current page number, which is 1.
     *
     * @return The default current page number.
     */
    protected int getDefaultCurrentPageNumber() {
        return 1;
    }

    /**
     * Gets the total number of records.
     *
     * @return The total record count.
     */
    public int getAllRecordCount() {
        return allRecordCount;
    }

    /**
     * Sets the total number of records.
     *
     * @param allRecordCount The total record count.
     */
    public void setAllRecordCount(final int allRecordCount) {
        this.allRecordCount = allRecordCount;
    }

    /**
     * Gets the total number of pages.
     *
     * @return The total page count.
     */
    public int getAllPageCount() {
        return allPageCount;
    }

    /**
     * Sets the total number of pages.
     *
     * @param allPageCount The total page count.
     */
    public void setAllPageCount(final int allPageCount) {
        this.allPageCount = allPageCount;
    }

    /**
     * Checks if a previous page exists.
     *
     * @return true if a previous page exists, false otherwise.
     */
    public boolean isExistPrePage() {
        return existPrePage;
    }

    /**
     * Sets the flag for the existence of a previous page.
     *
     * @param existPrePage true if a previous page exists.
     */
    public void setExistPrePage(final boolean existPrePage) {
        this.existPrePage = existPrePage;
    }

    /**
     * Checks if a next page exists.
     *
     * @return true if a next page exists, false otherwise.
     */
    public boolean isExistNextPage() {
        return existNextPage;
    }

    /**
     * Sets the flag for the existence of a next page.
     *
     * @param existNextPage true if a next page exists.
     */
    public void setExistNextPage(final boolean existNextPage) {
        this.existNextPage = existNextPage;
    }

    /**
     * Gets the page size.
     *
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
     *
     * @param pageSize The page size.
     */
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Gets the current page number.
     *
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
     *
     * @param currentPageNumber The current page number.
     */
    public void setCurrentPageNumber(final int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    /**
     * Gets the list of page numbers for navigation.
     *
     * @return A list of page numbers.
     */
    public List<Integer> getPageNumberList() {
        return pageNumberList;
    }

    /**
     * Sets the list of page numbers for navigation.
     *
     * @param pageNumberList A list of page numbers.
     */
    public void setPageNumberList(final List<Integer> pageNumberList) {
        this.pageNumberList = pageNumberList;
    }
}