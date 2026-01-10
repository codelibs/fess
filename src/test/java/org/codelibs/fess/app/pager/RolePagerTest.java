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

public class RolePagerTest extends UnitFessTestCase {

    public void test_RolePager() {
        RolePager rolePager = new RolePager();

        rolePager.clear();
        assertEquals(0, rolePager.getAllRecordCount());
        assertEquals(0, rolePager.getAllPageCount());
        assertEquals(false, rolePager.isExistPrePage());
        assertEquals(false, rolePager.isExistNextPage());
        assertEquals(25, rolePager.getPageSize());
        assertEquals(1, rolePager.getCurrentPageNumber());
        assertNull(rolePager.id);
        assertNull(rolePager.name);
        assertNull(rolePager.versionNo);

        rolePager.setAllRecordCount(999);
        assertEquals(999, rolePager.getAllRecordCount());
        rolePager.setAllPageCount(999);
        assertEquals(999, rolePager.getAllPageCount());
        rolePager.setExistPrePage(true);
        assertTrue(rolePager.isExistPrePage());
        rolePager.setExistNextPage(true);
        assertTrue(rolePager.isExistNextPage());
        rolePager.setPageSize(0);
        assertEquals(25, rolePager.getPageSize());
        rolePager.setPageSize(999);
        assertEquals(999, rolePager.getPageSize());
        rolePager.setCurrentPageNumber(0);
        assertEquals(1, rolePager.getCurrentPageNumber());
        rolePager.setCurrentPageNumber(999);
        assertEquals(999, rolePager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        rolePager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, rolePager.getPageNumberList());

    }

    public void test_clear() {
        RolePager rolePager = new RolePager();
        rolePager.id = "testId";
        rolePager.name = "testName";
        rolePager.versionNo = "1";
        rolePager.setAllRecordCount(100);
        rolePager.setAllPageCount(10);
        rolePager.setExistPrePage(true);
        rolePager.setExistNextPage(true);

        rolePager.clear();

        assertNull(rolePager.id);
        assertNull(rolePager.name);
        assertNull(rolePager.versionNo);
        assertEquals(0, rolePager.getAllRecordCount());
        assertEquals(0, rolePager.getAllPageCount());
        assertFalse(rolePager.isExistPrePage());
        assertFalse(rolePager.isExistNextPage());
    }

    public void test_defaultValues() {
        RolePager rolePager = new RolePager();
        assertEquals(RolePager.DEFAULT_PAGE_SIZE, 20);
        assertEquals(RolePager.DEFAULT_CURRENT_PAGE_NUMBER, 1);
    }
}
