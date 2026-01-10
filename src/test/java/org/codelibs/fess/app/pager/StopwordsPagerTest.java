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

public class StopwordsPagerTest extends UnitFessTestCase {

    public void test_StopwordsPager() {
        StopwordsPager stopwordsPager = new StopwordsPager();

        stopwordsPager.clear();
        assertEquals(0, stopwordsPager.getAllRecordCount());
        assertEquals(0, stopwordsPager.getAllPageCount());
        assertEquals(false, stopwordsPager.isExistPrePage());
        assertEquals(false, stopwordsPager.isExistNextPage());
        assertEquals(25, stopwordsPager.getPageSize());
        assertEquals(1, stopwordsPager.getCurrentPageNumber());
        assertNull(stopwordsPager.id);

        stopwordsPager.setAllRecordCount(999);
        assertEquals(999, stopwordsPager.getAllRecordCount());
        stopwordsPager.setAllPageCount(999);
        assertEquals(999, stopwordsPager.getAllPageCount());
        stopwordsPager.setExistPrePage(true);
        assertTrue(stopwordsPager.isExistPrePage());
        stopwordsPager.setExistNextPage(true);
        assertTrue(stopwordsPager.isExistNextPage());
        stopwordsPager.setPageSize(0);
        assertEquals(25, stopwordsPager.getPageSize());
        stopwordsPager.setPageSize(999);
        assertEquals(999, stopwordsPager.getPageSize());
        stopwordsPager.setCurrentPageNumber(0);
        assertEquals(1, stopwordsPager.getCurrentPageNumber());
        stopwordsPager.setCurrentPageNumber(999);
        assertEquals(999, stopwordsPager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        stopwordsPager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, stopwordsPager.getPageNumberList());

    }

    public void test_clear() {
        StopwordsPager stopwordsPager = new StopwordsPager();
        stopwordsPager.id = "testId";
        stopwordsPager.setAllRecordCount(100);
        stopwordsPager.setAllPageCount(10);
        stopwordsPager.setExistPrePage(true);
        stopwordsPager.setExistNextPage(true);

        stopwordsPager.clear();

        assertNull(stopwordsPager.id);
        assertEquals(0, stopwordsPager.getAllRecordCount());
        assertEquals(0, stopwordsPager.getAllPageCount());
        assertFalse(stopwordsPager.isExistPrePage());
        assertFalse(stopwordsPager.isExistNextPage());
    }
}
