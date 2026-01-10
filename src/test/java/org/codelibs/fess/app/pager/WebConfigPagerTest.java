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

public class WebConfigPagerTest extends UnitFessTestCase {

    public void test_WebConfigPager() {
        WebConfigPager webConfigPager = new WebConfigPager();

        webConfigPager.clear();
        assertEquals(0, webConfigPager.getAllRecordCount());
        assertEquals(0, webConfigPager.getAllPageCount());
        assertEquals(false, webConfigPager.isExistPrePage());
        assertEquals(false, webConfigPager.isExistNextPage());
        assertEquals(25, webConfigPager.getPageSize());
        assertEquals(1, webConfigPager.getCurrentPageNumber());
        assertNull(webConfigPager.id);
        assertNull(webConfigPager.name);
        assertNull(webConfigPager.versionNo);

        webConfigPager.setAllRecordCount(999);
        assertEquals(999, webConfigPager.getAllRecordCount());
        webConfigPager.setAllPageCount(999);
        assertEquals(999, webConfigPager.getAllPageCount());
        webConfigPager.setExistPrePage(true);
        assertTrue(webConfigPager.isExistPrePage());
        webConfigPager.setExistNextPage(true);
        assertTrue(webConfigPager.isExistNextPage());
        webConfigPager.setPageSize(0);
        assertEquals(25, webConfigPager.getPageSize());
        webConfigPager.setPageSize(999);
        assertEquals(999, webConfigPager.getPageSize());
        webConfigPager.setCurrentPageNumber(0);
        assertEquals(1, webConfigPager.getCurrentPageNumber());
        webConfigPager.setCurrentPageNumber(999);
        assertEquals(999, webConfigPager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        webConfigPager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, webConfigPager.getPageNumberList());

    }

    public void test_clear() {
        WebConfigPager webConfigPager = new WebConfigPager();
        webConfigPager.id = "testId";
        webConfigPager.name = "testName";
        webConfigPager.versionNo = "1";
        webConfigPager.setAllRecordCount(100);
        webConfigPager.setAllPageCount(10);
        webConfigPager.setExistPrePage(true);
        webConfigPager.setExistNextPage(true);

        webConfigPager.clear();

        assertNull(webConfigPager.id);
        assertNull(webConfigPager.name);
        assertNull(webConfigPager.versionNo);
        assertEquals(0, webConfigPager.getAllRecordCount());
        assertEquals(0, webConfigPager.getAllPageCount());
        assertFalse(webConfigPager.isExistPrePage());
        assertFalse(webConfigPager.isExistNextPage());
    }
}
