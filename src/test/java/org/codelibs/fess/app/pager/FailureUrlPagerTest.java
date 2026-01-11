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

public class FailureUrlPagerTest extends UnitFessTestCase {

    public void test_FailureUrlPager() {
        FailureUrlPager failureUrlPager = new FailureUrlPager();

        failureUrlPager.clear();
        assertEquals(0, failureUrlPager.getAllRecordCount());
        assertEquals(0, failureUrlPager.getAllPageCount());
        assertEquals(false, failureUrlPager.isExistPrePage());
        assertEquals(false, failureUrlPager.isExistNextPage());
        assertEquals(25, failureUrlPager.getPageSize());
        assertEquals(1, failureUrlPager.getCurrentPageNumber());
        assertNull(failureUrlPager.id);
        assertNull(failureUrlPager.url);
        assertNull(failureUrlPager.threadName);
        assertNull(failureUrlPager.errorCount);
        assertNull(failureUrlPager.errorName);

        failureUrlPager.setAllRecordCount(999);
        assertEquals(999, failureUrlPager.getAllRecordCount());
        failureUrlPager.setAllPageCount(999);
        assertEquals(999, failureUrlPager.getAllPageCount());
        failureUrlPager.setExistPrePage(true);
        assertTrue(failureUrlPager.isExistPrePage());
        failureUrlPager.setExistNextPage(true);
        assertTrue(failureUrlPager.isExistNextPage());
        failureUrlPager.setPageSize(0);
        assertEquals(25, failureUrlPager.getPageSize());
        failureUrlPager.setPageSize(999);
        assertEquals(999, failureUrlPager.getPageSize());
        failureUrlPager.setCurrentPageNumber(0);
        assertEquals(1, failureUrlPager.getCurrentPageNumber());
        failureUrlPager.setCurrentPageNumber(999);
        assertEquals(999, failureUrlPager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        failureUrlPager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, failureUrlPager.getPageNumberList());

    }

    public void test_clear() {
        FailureUrlPager failureUrlPager = new FailureUrlPager();
        failureUrlPager.id = "testId";
        failureUrlPager.url = "http://test.example.com";
        failureUrlPager.threadName = "crawler-1";
        failureUrlPager.errorCount = "5";
        failureUrlPager.errorName = "ConnectionTimeout";
        failureUrlPager.setAllRecordCount(100);
        failureUrlPager.setAllPageCount(10);
        failureUrlPager.setExistPrePage(true);
        failureUrlPager.setExistNextPage(true);

        failureUrlPager.clear();

        assertNull(failureUrlPager.id);
        assertNull(failureUrlPager.url);
        assertNull(failureUrlPager.threadName);
        assertNull(failureUrlPager.errorCount);
        assertNull(failureUrlPager.errorName);
        assertEquals(0, failureUrlPager.getAllRecordCount());
        assertEquals(0, failureUrlPager.getAllPageCount());
        assertFalse(failureUrlPager.isExistPrePage());
        assertFalse(failureUrlPager.isExistNextPage());
    }
}
