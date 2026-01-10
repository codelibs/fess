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

public class ProtwordsPagerTest extends UnitFessTestCase {

    public void test_ProtwordsPager() {
        ProtwordsPager protwordsPager = new ProtwordsPager();

        protwordsPager.clear();
        assertEquals(0, protwordsPager.getAllRecordCount());
        assertEquals(0, protwordsPager.getAllPageCount());
        assertEquals(false, protwordsPager.isExistPrePage());
        assertEquals(false, protwordsPager.isExistNextPage());
        assertEquals(25, protwordsPager.getPageSize());
        assertEquals(1, protwordsPager.getCurrentPageNumber());
        assertNull(protwordsPager.id);

        protwordsPager.setAllRecordCount(999);
        assertEquals(999, protwordsPager.getAllRecordCount());
        protwordsPager.setAllPageCount(999);
        assertEquals(999, protwordsPager.getAllPageCount());
        protwordsPager.setExistPrePage(true);
        assertTrue(protwordsPager.isExistPrePage());
        protwordsPager.setExistNextPage(true);
        assertTrue(protwordsPager.isExistNextPage());
        protwordsPager.setPageSize(0);
        assertEquals(25, protwordsPager.getPageSize());
        protwordsPager.setPageSize(999);
        assertEquals(999, protwordsPager.getPageSize());
        protwordsPager.setCurrentPageNumber(0);
        assertEquals(1, protwordsPager.getCurrentPageNumber());
        protwordsPager.setCurrentPageNumber(999);
        assertEquals(999, protwordsPager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        protwordsPager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, protwordsPager.getPageNumberList());

    }

    public void test_clear() {
        ProtwordsPager protwordsPager = new ProtwordsPager();
        protwordsPager.id = "testId";
        protwordsPager.setAllRecordCount(100);
        protwordsPager.setAllPageCount(10);
        protwordsPager.setExistPrePage(true);
        protwordsPager.setExistNextPage(true);

        protwordsPager.clear();

        assertNull(protwordsPager.id);
        assertEquals(0, protwordsPager.getAllRecordCount());
        assertEquals(0, protwordsPager.getAllPageCount());
        assertFalse(protwordsPager.isExistPrePage());
        assertFalse(protwordsPager.isExistNextPage());
    }
}
