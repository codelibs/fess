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
 * The pager for access token.
 */
public class AccessTokenPager implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public AccessTokenPager() {
        // nothing
    }

    /**
     * The default current page number.
     */
    public static final int DEFAULT_CURRENT_PAGE_NUMBER = 1;

    private int allRecordCount;

    private int allPageCount;

    private boolean existPrePage;

    private boolean existNextPage;

    private List<Integer> pageNumberList;

    private int pageSize;

    private int currentPageNumber;

    /**
     * The ID.
     */
    public String id;

    /**
     * The name.
     */
    public String name;

    /**
     * The created by.
     */
    public String createdBy;

    /**
     * The created time.
     */
    public String createdTime;

    /**
     * The version number.
     */
    public String versionNo;

    /**
     * Clear the pager.
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
        createdBy = null;
        createdTime = null;
        versionNo = null;

    }

    /**
     * Get the all record count.
     * @return The all record count.
     */
    public int getAllRecordCount() {
        return allRecordCount;
    }

    /**
     * Set the all record count.
     * @param allRecordCount The all record count.
     */
    public void setAllRecordCount(final int allRecordCount) {
        this.allRecordCount = allRecordCount;
    }

    /**
     * Get the all page count.
     * @return The all page count.
     */
    public int getAllPageCount() {
        return allPageCount;
    }

    /**
     * Set the all page count.
     * @param allPageCount The all page count.
     */
    public void setAllPageCount(final int allPageCount) {
        this.allPageCount = allPageCount;
    }

    /**
     * Check if the previous page exists.
     * @return true if the previous page exists.
     */
    public boolean isExistPrePage() {
        return existPrePage;
    }

    /**
     * Set if the previous page exists.
     * @param existPrePage true if the previous page exists.
     */
    public void setExistPrePage(final boolean existPrePage) {
        this.existPrePage = existPrePage;
    }

    /**
     * Check if the next page exists.
     * @return true if the next page exists.
     */
    public boolean isExistNextPage() {
        return existNextPage;
    }

    /**
     * Set if the next page exists.
     * @param existNextPage true if the next page exists.
     */
    public void setExistNextPage(final boolean existNextPage) {
        this.existNextPage = existNextPage;
    }

    /**
     * Get the page size.
     * @return The page size.
     */
    public int getPageSize() {
        if (pageSize <= 0) {
            pageSize = getDefaultPageSize();
        }
        return pageSize;
    }

    /**
     * Set the page size.
     * @param pageSize The page size.
     */
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Get the current page number.
     * @return The current page number.
     */
    public int getCurrentPageNumber() {
        if (currentPageNumber <= 0) {
            currentPageNumber = getDefaultCurrentPageNumber();
        }
        return currentPageNumber;
    }

    /**
     * Set the current page number.
     * @param currentPageNumber The current page number.
     */
    public void setCurrentPageNumber(final int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    /**
     * Get the page number list.
     * @return The page number list.
     */
    public List<Integer> getPageNumberList() {
        return pageNumberList;
    }

    /**
     * Set the page number list.
     * @param pageNumberList The page number list.
     */
    public void setPageNumberList(final List<Integer> pageNumberList) {
        this.pageNumberList = pageNumberList;
    }

    /**
     * Get the default current page number.
     * @return The default current page number.
     */
    protected int getDefaultCurrentPageNumber() {
        return Constants.DEFAULT_ADMIN_PAGE_NUMBER;
    }

    /**
     * Get the default page size.
     * @return The default page size.
     */
    protected int getDefaultPageSize() {
        return ComponentUtil.getFessConfig().getPagingPageSizeAsInteger();
    }

}
