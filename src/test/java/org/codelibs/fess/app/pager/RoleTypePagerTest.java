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

public class RoleTypePagerTest extends UnitFessTestCase {

    public void test_RoleTypePager() {
        RoleTypePager roleTypePager = new RoleTypePager();

        roleTypePager.clear();
        assertEquals(0, roleTypePager.getAllRecordCount());
        assertEquals(0, roleTypePager.getAllPageCount());
        assertEquals(false, roleTypePager.isExistPrePage());
        assertEquals(false, roleTypePager.isExistNextPage());
        assertEquals(25, roleTypePager.getPageSize());
        assertEquals(1, roleTypePager.getCurrentPageNumber());
        assertNull(roleTypePager.id);
        assertNull(roleTypePager.name);
        assertNull(roleTypePager.versionNo);

        roleTypePager.setAllRecordCount(999);
        assertEquals(999, roleTypePager.getAllRecordCount());
        roleTypePager.setAllPageCount(999);
        assertEquals(999, roleTypePager.getAllPageCount());
        roleTypePager.setExistPrePage(true);
        assertTrue(roleTypePager.isExistPrePage());
        roleTypePager.setExistNextPage(true);
        assertTrue(roleTypePager.isExistNextPage());
        roleTypePager.setPageSize(0);
        assertEquals(25, roleTypePager.getPageSize());
        roleTypePager.setPageSize(999);
        assertEquals(999, roleTypePager.getPageSize());
        roleTypePager.setCurrentPageNumber(0);
        assertEquals(1, roleTypePager.getCurrentPageNumber());
        roleTypePager.setCurrentPageNumber(999);
        assertEquals(999, roleTypePager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        roleTypePager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, roleTypePager.getPageNumberList());

    }

    public void test_clear() {
        RoleTypePager roleTypePager = new RoleTypePager();
        roleTypePager.id = "testId";
        roleTypePager.name = "testName";
        roleTypePager.versionNo = "1";
        roleTypePager.setAllRecordCount(100);
        roleTypePager.setAllPageCount(10);
        roleTypePager.setExistPrePage(true);
        roleTypePager.setExistNextPage(true);

        roleTypePager.clear();

        assertNull(roleTypePager.id);
        assertNull(roleTypePager.name);
        assertNull(roleTypePager.versionNo);
        assertEquals(0, roleTypePager.getAllRecordCount());
        assertEquals(0, roleTypePager.getAllPageCount());
        assertFalse(roleTypePager.isExistPrePage());
        assertFalse(roleTypePager.isExistNextPage());
    }
}
