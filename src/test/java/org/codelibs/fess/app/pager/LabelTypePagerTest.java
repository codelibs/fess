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

public class LabelTypePagerTest extends UnitFessTestCase {

    public void test_LabelTypePager() {
        LabelTypePager labelTypePager = new LabelTypePager();

        labelTypePager.clear();
        assertEquals(0, labelTypePager.getAllRecordCount());
        assertEquals(0, labelTypePager.getAllPageCount());
        assertEquals(false, labelTypePager.isExistPrePage());
        assertEquals(false, labelTypePager.isExistNextPage());
        assertEquals(25, labelTypePager.getPageSize());
        assertEquals(1, labelTypePager.getCurrentPageNumber());
        assertNull(labelTypePager.id);
        assertNull(labelTypePager.name);
        assertNull(labelTypePager.versionNo);

        labelTypePager.setAllRecordCount(999);
        assertEquals(999, labelTypePager.getAllRecordCount());
        labelTypePager.setAllPageCount(999);
        assertEquals(999, labelTypePager.getAllPageCount());
        labelTypePager.setExistPrePage(true);
        assertTrue(labelTypePager.isExistPrePage());
        labelTypePager.setExistNextPage(true);
        assertTrue(labelTypePager.isExistNextPage());
        labelTypePager.setPageSize(0);
        assertEquals(25, labelTypePager.getPageSize());
        labelTypePager.setPageSize(999);
        assertEquals(999, labelTypePager.getPageSize());
        labelTypePager.setCurrentPageNumber(0);
        assertEquals(1, labelTypePager.getCurrentPageNumber());
        labelTypePager.setCurrentPageNumber(999);
        assertEquals(999, labelTypePager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        labelTypePager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, labelTypePager.getPageNumberList());

    }

    public void test_clear() {
        LabelTypePager labelTypePager = new LabelTypePager();
        labelTypePager.id = "testId";
        labelTypePager.name = "testName";
        labelTypePager.versionNo = "1";
        labelTypePager.setAllRecordCount(100);
        labelTypePager.setAllPageCount(10);
        labelTypePager.setExistPrePage(true);
        labelTypePager.setExistNextPage(true);

        labelTypePager.clear();

        assertNull(labelTypePager.id);
        assertNull(labelTypePager.name);
        assertNull(labelTypePager.versionNo);
        assertEquals(0, labelTypePager.getAllRecordCount());
        assertEquals(0, labelTypePager.getAllPageCount());
        assertFalse(labelTypePager.isExistPrePage());
        assertFalse(labelTypePager.isExistNextPage());
    }
}
