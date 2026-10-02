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
import org.junit.jupiter.api.Test;

public class SearchLogPagerTest extends UnitFessTestCase {

    @Test
    public void test_SearchLogPager() {
        SearchLogPager searchLogPager = new SearchLogPager();

        searchLogPager.clear();
        assertEquals(0, searchLogPager.getAllRecordCount());
        assertEquals(0, searchLogPager.getAllPageCount());
        assertEquals(false, searchLogPager.isExistPrePage());
        assertEquals(false, searchLogPager.isExistNextPage());
        assertEquals(25, searchLogPager.getPageSize());
        assertEquals(1, searchLogPager.getCurrentPageNumber());
        assertNull(searchLogPager.queryId);
        assertNull(searchLogPager.userSessionId);
        assertNull(searchLogPager.requestedTimeRange);
        assertNull(searchLogPager.accessType);
        assertEquals(SearchLogPager.LOG_TYPE_SEARCH, searchLogPager.logType);

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

    @Test
    public void test_clear() {
        SearchLogPager searchLogPager = new SearchLogPager();
        searchLogPager.queryId = "testQueryId";
        searchLogPager.userSessionId = "testSessionId";
        searchLogPager.requestedTimeRange = "2025-01-01 00:00 - 2025-01-02 00:00";
        searchLogPager.accessType = "web";
        searchLogPager.searchWord = "w";
        searchLogPager.hitCount = SearchLogPager.HIT_COUNT_ZERO;
        searchLogPager.logType = SearchLogPager.LOG_TYPE_CLICK;
        searchLogPager.setAllRecordCount(100);
        searchLogPager.setAllPageCount(10);
        searchLogPager.setExistPrePage(true);
        searchLogPager.setExistNextPage(true);

        searchLogPager.clear();

        assertNull(searchLogPager.queryId);
        assertNull(searchLogPager.userSessionId);
        assertNull(searchLogPager.requestedTimeRange);
        assertNull(searchLogPager.accessType);
        assertNull(searchLogPager.searchWord, "cleared");
        assertNull(searchLogPager.hitCount, "cleared");
        assertEquals(SearchLogPager.LOG_TYPE_SEARCH, searchLogPager.logType);
        assertEquals(0, searchLogPager.getAllRecordCount());
        assertEquals(0, searchLogPager.getAllPageCount());
        assertFalse(searchLogPager.isExistPrePage());
        assertFalse(searchLogPager.isExistNextPage());
    }

    @Test
    public void test_logTypeConstants() {
        assertEquals("search", SearchLogPager.LOG_TYPE_SEARCH);
        assertEquals("click", SearchLogPager.LOG_TYPE_CLICK);
        assertEquals("favorite", SearchLogPager.LOG_TYPE_FAVORITE);
        assertEquals("user_info", SearchLogPager.LOG_TYPE_USERINFO);
    }

    @Test
    public void test_normalizeLogType() {
        assertEquals("search", SearchLogPager.normalizeLogType("search"));
        assertEquals("click", SearchLogPager.normalizeLogType("CLICK"));
        assertEquals("favorite", SearchLogPager.normalizeLogType("Favorite"));
        assertEquals("user_info", SearchLogPager.normalizeLogType("USER_INFO"));
        assertEquals("search", SearchLogPager.normalizeLogType("search_keyword_agg"));
        assertEquals("search", SearchLogPager.normalizeLogType(null));
    }
}
