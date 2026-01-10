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

import java.util.ArrayList;
import java.util.List;

import org.codelibs.fess.unit.UnitFessTestCase;

public class SearchLogPagerTest extends UnitFessTestCase {

    public void test_SearchLogPager() {
        SearchLogPager searchLogPager = new SearchLogPager();

        searchLogPager.clear();
        assertEquals(0, searchLogPager.getAllRecordCount());
        assertEquals(0, searchLogPager.getAllPageCount());
        assertEquals(false, searchLogPager.isExistPrePage());
        assertEquals(false, searchLogPager.isExistNextPage());
        assertEquals(25, searchLogPager.getPageSize());
        assertEquals(1, searchLogPager.getCurrentPageNumber());
        assertNull(searchLogPager.id);
        assertNull(searchLogPager.searchWord);
        assertNull(searchLogPager.versionNo);

        searchLogPager.setAllRecordCount(999);
        assertEquals(999, searchLogPager.getAllRecordCount());
        searchLogPager.setAllPageCount(999);
        assertEquals(999, searchLogPager.getAllPageCount());
        searchLogPager.setExistPrePage(true);
        assertTrue(searchLogPager.isExistPrePage());
        searchLogPager.setExistNextPage(true);
        assertTrue(searchLogPager.isExistNextPage());
        searchLogPager.setPageSize(0);
        assertEquals(25, searchLogPager.getPageSize());
        searchLogPager.setPageSize(999);
        assertEquals(999, searchLogPager.getPageSize());
        searchLogPager.setCurrentPageNumber(0);
        assertEquals(1, searchLogPager.getCurrentPageNumber());
        searchLogPager.setCurrentPageNumber(999);
        assertEquals(999, searchLogPager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        searchLogPager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, searchLogPager.getPageNumberList());

    }

    public void test_clear() {
        SearchLogPager searchLogPager = new SearchLogPager();
        searchLogPager.id = "testId";
        searchLogPager.searchWord = "testSearchWord";
        searchLogPager.versionNo = "1";
        searchLogPager.setAllRecordCount(100);
        searchLogPager.setAllPageCount(10);
        searchLogPager.setExistPrePage(true);
        searchLogPager.setExistNextPage(true);

        searchLogPager.clear();

        assertNull(searchLogPager.id);
        assertNull(searchLogPager.searchWord);
        assertNull(searchLogPager.versionNo);
        assertEquals(0, searchLogPager.getAllRecordCount());
        assertEquals(0, searchLogPager.getAllPageCount());
        assertFalse(searchLogPager.isExistPrePage());
        assertFalse(searchLogPager.isExistNextPage());
    }
}
