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

public class StemmerOverridePagerTest extends UnitFessTestCase {

    public void test_StemmerOverridePager() {
        StemmerOverridePager stemmerOverridePager = new StemmerOverridePager();

        stemmerOverridePager.clear();
        assertEquals(0, stemmerOverridePager.getAllRecordCount());
        assertEquals(0, stemmerOverridePager.getAllPageCount());
        assertEquals(false, stemmerOverridePager.isExistPrePage());
        assertEquals(false, stemmerOverridePager.isExistNextPage());
        assertEquals(25, stemmerOverridePager.getPageSize());
        assertEquals(1, stemmerOverridePager.getCurrentPageNumber());
        assertNull(stemmerOverridePager.id);

        stemmerOverridePager.setAllRecordCount(999);
        assertEquals(999, stemmerOverridePager.getAllRecordCount());
        stemmerOverridePager.setAllPageCount(999);
        assertEquals(999, stemmerOverridePager.getAllPageCount());
        stemmerOverridePager.setExistPrePage(true);
        assertTrue(stemmerOverridePager.isExistPrePage());
        stemmerOverridePager.setExistNextPage(true);
        assertTrue(stemmerOverridePager.isExistNextPage());
        stemmerOverridePager.setPageSize(0);
        assertEquals(25, stemmerOverridePager.getPageSize());
        stemmerOverridePager.setPageSize(999);
        assertEquals(999, stemmerOverridePager.getPageSize());
        stemmerOverridePager.setCurrentPageNumber(0);
        assertEquals(1, stemmerOverridePager.getCurrentPageNumber());
        stemmerOverridePager.setCurrentPageNumber(999);
        assertEquals(999, stemmerOverridePager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        stemmerOverridePager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, stemmerOverridePager.getPageNumberList());

    }

    public void test_clear() {
        StemmerOverridePager stemmerOverridePager = new StemmerOverridePager();
        stemmerOverridePager.id = "testId";
        stemmerOverridePager.setAllRecordCount(100);
        stemmerOverridePager.setAllPageCount(10);
        stemmerOverridePager.setExistPrePage(true);
        stemmerOverridePager.setExistNextPage(true);

        stemmerOverridePager.clear();

        assertNull(stemmerOverridePager.id);
        assertEquals(0, stemmerOverridePager.getAllRecordCount());
        assertEquals(0, stemmerOverridePager.getAllPageCount());
        assertFalse(stemmerOverridePager.isExistPrePage());
        assertFalse(stemmerOverridePager.isExistNextPage());
    }
}
