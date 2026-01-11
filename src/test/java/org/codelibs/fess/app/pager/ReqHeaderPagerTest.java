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

public class ReqHeaderPagerTest extends UnitFessTestCase {

    public void test_ReqHeaderPager() {
        ReqHeaderPager reqHeaderPager = new ReqHeaderPager();

        reqHeaderPager.clear();
        assertEquals(0, reqHeaderPager.getAllRecordCount());
        assertEquals(0, reqHeaderPager.getAllPageCount());
        assertEquals(false, reqHeaderPager.isExistPrePage());
        assertEquals(false, reqHeaderPager.isExistNextPage());
        assertEquals(25, reqHeaderPager.getPageSize());
        assertEquals(1, reqHeaderPager.getCurrentPageNumber());
        assertNull(reqHeaderPager.id);
        assertNull(reqHeaderPager.name);
        assertNull(reqHeaderPager.versionNo);

        reqHeaderPager.setAllRecordCount(999);
        assertEquals(999, reqHeaderPager.getAllRecordCount());
        reqHeaderPager.setAllPageCount(999);
        assertEquals(999, reqHeaderPager.getAllPageCount());
        reqHeaderPager.setExistPrePage(true);
        assertTrue(reqHeaderPager.isExistPrePage());
        reqHeaderPager.setExistNextPage(true);
        assertTrue(reqHeaderPager.isExistNextPage());
        reqHeaderPager.setPageSize(0);
        assertEquals(25, reqHeaderPager.getPageSize());
        reqHeaderPager.setPageSize(999);
        assertEquals(999, reqHeaderPager.getPageSize());
        reqHeaderPager.setCurrentPageNumber(0);
        assertEquals(1, reqHeaderPager.getCurrentPageNumber());
        reqHeaderPager.setCurrentPageNumber(999);
        assertEquals(999, reqHeaderPager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        reqHeaderPager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, reqHeaderPager.getPageNumberList());

    }

    public void test_clear() {
        ReqHeaderPager reqHeaderPager = new ReqHeaderPager();
        reqHeaderPager.id = "testId";
        reqHeaderPager.name = "testName";
        reqHeaderPager.versionNo = "1";
        reqHeaderPager.setAllRecordCount(100);
        reqHeaderPager.setAllPageCount(10);
        reqHeaderPager.setExistPrePage(true);
        reqHeaderPager.setExistNextPage(true);

        reqHeaderPager.clear();

        assertNull(reqHeaderPager.id);
        assertNull(reqHeaderPager.name);
        assertNull(reqHeaderPager.versionNo);
        assertEquals(0, reqHeaderPager.getAllRecordCount());
        assertEquals(0, reqHeaderPager.getAllPageCount());
        assertFalse(reqHeaderPager.isExistPrePage());
        assertFalse(reqHeaderPager.isExistNextPage());
    }
}
