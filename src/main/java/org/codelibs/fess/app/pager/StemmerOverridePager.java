/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

public class StemmerOverridePager implements Serializable {

    private static final long serialVersionUID = 1L;

    private int allRecordCount;

    private int allPageCount;

    private boolean existPrePage;

    private boolean existNextPage;

    private List<Integer> pageNumberList;

    private int pageSize;

    private int currentPageNumber;

    public String id;

    public void clear() {
        allRecordCount = 0;
        allPageCount = 0;
        existPrePage = false;
        existNextPage = false;
        pageSize = getDefaultPageSize();
        currentPageNumber = getDefaultCurrentPageNumber();

        id = null;
    }

    protected int getDefaultPageSize() {
        return ComponentUtil.getFessConfig().getPagingPageSizeAsInteger();
    }

    protected int getDefaultCurrentPageNumber() {
        return 1;
    }

    public int getAllRecordCount() {
        return allRecordCount;
    }

    public void setAllRecordCount(final int allRecordCount) {
        this.allRecordCount = allRecordCount;
    }

    public int getAllPageCount() {
        return allPageCount;
    }

    public void setAllPageCount(final int allPageCount) {
        this.allPageCount = allPageCount;
    }

    public boolean isExistPrePage() {
        return existPrePage;
    }

    public void setExistPrePage(final boolean existPrePage) {
        this.existPrePage = existPrePage;
    }

    public boolean isExistNextPage() {
        return existNextPage;
    }

    public void setExistNextPage(final boolean existNextPage) {
        this.existNextPage = existNextPage;
    }

    public int getPageSize() {
        if (pageSize <= 0) {
            pageSize = getDefaultPageSize();
        }
        return pageSize;
    }

    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPageNumber() {
        if (currentPageNumber <= 0) {
            currentPageNumber = getDefaultCurrentPageNumber();
        }
        return currentPageNumber;
    }

    public void setCurrentPageNumber(final int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    public List<Integer> getPageNumberList() {
        return pageNumberList;
    }

    public void setPageNumberList(final List<Integer> pageNumberList) {
        this.pageNumberList = pageNumberList;
    }
}
