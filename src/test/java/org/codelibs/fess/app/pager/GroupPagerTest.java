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

public class GroupPagerTest extends UnitFessTestCase {

    public void test_GroupPager() {
        GroupPager groupPager = new GroupPager();

        groupPager.clear();
        assertEquals(0, groupPager.getAllRecordCount());
        assertEquals(0, groupPager.getAllPageCount());
        assertEquals(false, groupPager.isExistPrePage());
        assertEquals(false, groupPager.isExistNextPage());
        assertEquals(25, groupPager.getPageSize());
        assertEquals(1, groupPager.getCurrentPageNumber());
        assertNull(groupPager.id);
        assertNull(groupPager.name);
        assertNull(groupPager.versionNo);

        groupPager.setAllRecordCount(999);
        assertEquals(999, groupPager.getAllRecordCount());
        groupPager.setAllPageCount(999);
        assertEquals(999, groupPager.getAllPageCount());
        groupPager.setExistPrePage(true);
        assertTrue(groupPager.isExistPrePage());
        groupPager.setExistNextPage(true);
        assertTrue(groupPager.isExistNextPage());
        groupPager.setPageSize(0);
        assertEquals(25, groupPager.getPageSize());
        groupPager.setPageSize(999);
        assertEquals(999, groupPager.getPageSize());
        groupPager.setCurrentPageNumber(0);
        assertEquals(1, groupPager.getCurrentPageNumber());
        groupPager.setCurrentPageNumber(999);
        assertEquals(999, groupPager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        groupPager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, groupPager.getPageNumberList());

    }

    public void test_clear() {
        GroupPager groupPager = new GroupPager();
        groupPager.id = "testId";
        groupPager.name = "testName";
        groupPager.versionNo = "1";
        groupPager.setAllRecordCount(100);
        groupPager.setAllPageCount(10);
        groupPager.setExistPrePage(true);
        groupPager.setExistNextPage(true);

        groupPager.clear();

        assertNull(groupPager.id);
        assertNull(groupPager.name);
        assertNull(groupPager.versionNo);
        assertEquals(0, groupPager.getAllRecordCount());
        assertEquals(0, groupPager.getAllPageCount());
        assertFalse(groupPager.isExistPrePage());
        assertFalse(groupPager.isExistNextPage());
    }

    public void test_defaultValues() {
        GroupPager groupPager = new GroupPager();
        assertEquals(GroupPager.DEFAULT_PAGE_SIZE, 20);
        assertEquals(GroupPager.DEFAULT_CURRENT_PAGE_NUMBER, 1);
    }
}
