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

public class UserPagerTest extends UnitFessTestCase {

    public void test_UserPager() {
        UserPager userPager = new UserPager();

        userPager.clear();
        assertEquals(0, userPager.getAllRecordCount());
        assertEquals(0, userPager.getAllPageCount());
        assertEquals(false, userPager.isExistPrePage());
        assertEquals(false, userPager.isExistNextPage());
        assertEquals(25, userPager.getPageSize());
        assertEquals(1, userPager.getCurrentPageNumber());
        assertNull(userPager.id);
        assertNull(userPager.roles);
        assertNull(userPager.groups);
        assertNull(userPager.versionNo);

        userPager.setAllRecordCount(999);
        assertEquals(999, userPager.getAllRecordCount());
        userPager.setAllPageCount(999);
        assertEquals(999, userPager.getAllPageCount());
        userPager.setExistPrePage(true);
        assertTrue(userPager.isExistPrePage());
        userPager.setExistNextPage(true);
        assertTrue(userPager.isExistNextPage());
        userPager.setPageSize(0);
        assertEquals(25, userPager.getPageSize());
        userPager.setPageSize(999);
        assertEquals(999, userPager.getPageSize());
        userPager.setCurrentPageNumber(0);
        assertEquals(1, userPager.getCurrentPageNumber());
        userPager.setCurrentPageNumber(999);
        assertEquals(999, userPager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        userPager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, userPager.getPageNumberList());

    }

    public void test_clear() {
        UserPager userPager = new UserPager();
        userPager.id = "testId";
        userPager.name = "testName";
        userPager.roles = new String[] { "role1", "role2" };
        userPager.groups = new String[] { "group1" };
        userPager.versionNo = "1";
        userPager.setAllRecordCount(100);
        userPager.setAllPageCount(10);
        userPager.setExistPrePage(true);
        userPager.setExistNextPage(true);

        userPager.clear();

        assertNull(userPager.id);
        // Note: name is not cleared by clear() method
        assertNull(userPager.roles);
        assertNull(userPager.groups);
        assertNull(userPager.versionNo);
        assertEquals(0, userPager.getAllRecordCount());
        assertEquals(0, userPager.getAllPageCount());
        assertFalse(userPager.isExistPrePage());
        assertFalse(userPager.isExistNextPage());
    }

    public void test_defaultValues() {
        assertEquals(20, UserPager.DEFAULT_PAGE_SIZE);
        assertEquals(1, UserPager.DEFAULT_CURRENT_PAGE_NUMBER);
    }
}
