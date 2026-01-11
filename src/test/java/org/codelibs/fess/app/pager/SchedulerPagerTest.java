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

public class SchedulerPagerTest extends UnitFessTestCase {

    public void test_SchedulerPager() {
        SchedulerPager schedulerPager = new SchedulerPager();

        schedulerPager.clear();
        assertEquals(0, schedulerPager.getAllRecordCount());
        assertEquals(0, schedulerPager.getAllPageCount());
        assertEquals(false, schedulerPager.isExistPrePage());
        assertEquals(false, schedulerPager.isExistNextPage());
        assertEquals(25, schedulerPager.getPageSize());
        assertEquals(1, schedulerPager.getCurrentPageNumber());
        assertNull(schedulerPager.id);
        assertNull(schedulerPager.name);
        assertNull(schedulerPager.versionNo);

        schedulerPager.setAllRecordCount(999);
        assertEquals(999, schedulerPager.getAllRecordCount());
        schedulerPager.setAllPageCount(999);
        assertEquals(999, schedulerPager.getAllPageCount());
        schedulerPager.setExistPrePage(true);
        assertTrue(schedulerPager.isExistPrePage());
        schedulerPager.setExistNextPage(true);
        assertTrue(schedulerPager.isExistNextPage());
        schedulerPager.setPageSize(0);
        assertEquals(25, schedulerPager.getPageSize());
        schedulerPager.setPageSize(999);
        assertEquals(999, schedulerPager.getPageSize());
        schedulerPager.setCurrentPageNumber(0);
        assertEquals(1, schedulerPager.getCurrentPageNumber());
        schedulerPager.setCurrentPageNumber(999);
        assertEquals(999, schedulerPager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        schedulerPager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, schedulerPager.getPageNumberList());

    }

    public void test_clear() {
        SchedulerPager schedulerPager = new SchedulerPager();
        schedulerPager.id = "testId";
        schedulerPager.name = "testName";
        schedulerPager.versionNo = "1";
        schedulerPager.setAllRecordCount(100);
        schedulerPager.setAllPageCount(10);
        schedulerPager.setExistPrePage(true);
        schedulerPager.setExistNextPage(true);

        schedulerPager.clear();

        assertNull(schedulerPager.id);
        assertNull(schedulerPager.name);
        assertNull(schedulerPager.versionNo);
        assertEquals(0, schedulerPager.getAllRecordCount());
        assertEquals(0, schedulerPager.getAllPageCount());
        assertFalse(schedulerPager.isExistPrePage());
        assertFalse(schedulerPager.isExistNextPage());
    }
}
