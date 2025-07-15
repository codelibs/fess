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
 * Pager for Kuromoji.
 */
public class KuromojiPager implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public KuromojiPager() {
        // no-op
    }

    private int allRecordCount;

    private int allPageCount;

    private boolean existPrePage;

    private boolean existNextPage;

    private List<Integer> pageNumberList;

    private int pageSize;

    private int currentPageNumber;

    /** The ID of the Kuromoji dictionary. */
    public String id;

    /**
     * Clears the pager fields.
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
     * Returns the default page size.
     *
     * @return The default page size.
     */
    protected int getDefaultPageSize() {
        return ComponentUtil.getFessConfig().getPagingPageSizeAsInteger();
    }

    /**
     * Returns the default current page number.
     *
     * @return The default current page number.
     */
    protected int getDefaultCurrentPageNumber() {
        return 1;
    }

    /**
     * Returns the total number of records.
     *
     * @return The total number of records.
     */
    public int getAllRecordCount() {
        return allRecordCount;
    }

    /**
     * Sets the total number of records.
     *
     * @param allRecordCount The total number of records.
     */
    public void setAllRecordCount(final int allRecordCount) {
        this.allRecordCount = allRecordCount;
    }

    /**
     * Returns the total number of pages.
     *
     * @return The total number of pages.
     */
    public int getAllPageCount() {
        return allPageCount;
    }

    /**
     * Sets the total number of pages.
     *
     * @param allPageCount The total number of pages.
     */
    public void setAllPageCount(final int allPageCount) {
        this.allPageCount = allPageCount;
    }

    /**
     * Returns true if a previous page exists.
     *
     * @return True if a previous page exists.
     */
    public boolean isExistPrePage() {
        return existPrePage;
    }

    /**
     * Sets whether a previous page exists.
     *
     * @param existPrePage True if a previous page exists.
     */
    public void setExistPrePage(final boolean existPrePage) {
        this.existPrePage = existPrePage;
    }

    /**
     * Returns true if a next page exists.
     *
     * @return True if a next page exists.
     */
    public boolean isExistNextPage() {
        return existNextPage;
    }

    /**
     * Sets whether a next page exists.
     *
     * @param existNextPage True if a next page exists.
     */
    public void setExistNextPage(final boolean existNextPage) {
        this.existNextPage = existNextPage;
    }

    /**
     * Returns the page size.
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
     * Returns the current page number.
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
     * Returns the list of page numbers.
     *
     * @return The list of page numbers.
     */
    public List<Integer> getPageNumberList() {
        return pageNumberList;
    }

    /**
     * Sets the list of page numbers.
     *
     * @param pageNumberList The list of page numbers.
     */
    public void setPageNumberList(final List<Integer> pageNumberList) {
        this.pageNumberList = pageNumberList;
    }
}
