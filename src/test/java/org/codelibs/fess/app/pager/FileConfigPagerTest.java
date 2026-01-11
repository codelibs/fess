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

public class FileConfigPagerTest extends UnitFessTestCase {

    public void test_FileConfigPager() {
        FileConfigPager fileConfigPager = new FileConfigPager();

        fileConfigPager.clear();
        assertEquals(0, fileConfigPager.getAllRecordCount());
        assertEquals(0, fileConfigPager.getAllPageCount());
        assertEquals(false, fileConfigPager.isExistPrePage());
        assertEquals(false, fileConfigPager.isExistNextPage());
        assertEquals(25, fileConfigPager.getPageSize());
        assertEquals(1, fileConfigPager.getCurrentPageNumber());
        assertNull(fileConfigPager.id);
        assertNull(fileConfigPager.name);
        assertNull(fileConfigPager.versionNo);

        fileConfigPager.setAllRecordCount(999);
        assertEquals(999, fileConfigPager.getAllRecordCount());
        fileConfigPager.setAllPageCount(999);
        assertEquals(999, fileConfigPager.getAllPageCount());
        fileConfigPager.setExistPrePage(true);
        assertTrue(fileConfigPager.isExistPrePage());
        fileConfigPager.setExistNextPage(true);
        assertTrue(fileConfigPager.isExistNextPage());
        fileConfigPager.setPageSize(0);
        assertEquals(25, fileConfigPager.getPageSize());
        fileConfigPager.setPageSize(999);
        assertEquals(999, fileConfigPager.getPageSize());
        fileConfigPager.setCurrentPageNumber(0);
        assertEquals(1, fileConfigPager.getCurrentPageNumber());
        fileConfigPager.setCurrentPageNumber(999);
        assertEquals(999, fileConfigPager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        fileConfigPager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, fileConfigPager.getPageNumberList());

    }

    public void test_clear() {
        FileConfigPager fileConfigPager = new FileConfigPager();
        fileConfigPager.id = "testId";
        fileConfigPager.name = "testName";
        fileConfigPager.versionNo = "1";
        fileConfigPager.setAllRecordCount(100);
        fileConfigPager.setAllPageCount(10);
        fileConfigPager.setExistPrePage(true);
        fileConfigPager.setExistNextPage(true);

        fileConfigPager.clear();

        assertNull(fileConfigPager.id);
        assertNull(fileConfigPager.name);
        assertNull(fileConfigPager.versionNo);
        assertEquals(0, fileConfigPager.getAllRecordCount());
        assertEquals(0, fileConfigPager.getAllPageCount());
        assertFalse(fileConfigPager.isExistPrePage());
        assertFalse(fileConfigPager.isExistNextPage());
    }
}
