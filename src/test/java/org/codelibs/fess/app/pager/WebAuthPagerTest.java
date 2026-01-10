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

public class WebAuthPagerTest extends UnitFessTestCase {

    public void test_WebAuthPager() {
        WebAuthPager webAuthPager = new WebAuthPager();

        webAuthPager.clear();
        assertEquals(0, webAuthPager.getAllRecordCount());
        assertEquals(0, webAuthPager.getAllPageCount());
        assertEquals(false, webAuthPager.isExistPrePage());
        assertEquals(false, webAuthPager.isExistNextPage());
        assertEquals(25, webAuthPager.getPageSize());
        assertEquals(1, webAuthPager.getCurrentPageNumber());
        assertNull(webAuthPager.id);
        assertNull(webAuthPager.hostname);
        assertNull(webAuthPager.versionNo);

        webAuthPager.setAllRecordCount(999);
        assertEquals(999, webAuthPager.getAllRecordCount());
        webAuthPager.setAllPageCount(999);
        assertEquals(999, webAuthPager.getAllPageCount());
        webAuthPager.setExistPrePage(true);
        assertTrue(webAuthPager.isExistPrePage());
        webAuthPager.setExistNextPage(true);
        assertTrue(webAuthPager.isExistNextPage());
        webAuthPager.setPageSize(0);
        assertEquals(25, webAuthPager.getPageSize());
        webAuthPager.setPageSize(999);
        assertEquals(999, webAuthPager.getPageSize());
        webAuthPager.setCurrentPageNumber(0);
        assertEquals(1, webAuthPager.getCurrentPageNumber());
        webAuthPager.setCurrentPageNumber(999);
        assertEquals(999, webAuthPager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        webAuthPager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, webAuthPager.getPageNumberList());

    }

    public void test_clear() {
        WebAuthPager webAuthPager = new WebAuthPager();
        webAuthPager.id = "testId";
        webAuthPager.hostname = "test.example.com";
        webAuthPager.versionNo = "1";
        webAuthPager.setAllRecordCount(100);
        webAuthPager.setAllPageCount(10);
        webAuthPager.setExistPrePage(true);
        webAuthPager.setExistNextPage(true);

        webAuthPager.clear();

        assertNull(webAuthPager.id);
        assertNull(webAuthPager.hostname);
        assertNull(webAuthPager.versionNo);
        assertEquals(0, webAuthPager.getAllRecordCount());
        assertEquals(0, webAuthPager.getAllPageCount());
        assertFalse(webAuthPager.isExistPrePage());
        assertFalse(webAuthPager.isExistNextPage());
    }
}
