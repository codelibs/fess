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

public class FileAuthPagerTest extends UnitFessTestCase {

    public void test_FileAuthPager() {
        FileAuthPager fileAuthPager = new FileAuthPager();

        fileAuthPager.clear();
        assertEquals(0, fileAuthPager.getAllRecordCount());
        assertEquals(0, fileAuthPager.getAllPageCount());
        assertEquals(false, fileAuthPager.isExistPrePage());
        assertEquals(false, fileAuthPager.isExistNextPage());
        assertEquals(25, fileAuthPager.getPageSize());
        assertEquals(1, fileAuthPager.getCurrentPageNumber());
        assertNull(fileAuthPager.id);
        assertNull(fileAuthPager.hostname);
        assertNull(fileAuthPager.versionNo);

        fileAuthPager.setAllRecordCount(999);
        assertEquals(999, fileAuthPager.getAllRecordCount());
        fileAuthPager.setAllPageCount(999);
        assertEquals(999, fileAuthPager.getAllPageCount());
        fileAuthPager.setExistPrePage(true);
        assertTrue(fileAuthPager.isExistPrePage());
        fileAuthPager.setExistNextPage(true);
        assertTrue(fileAuthPager.isExistNextPage());
        fileAuthPager.setPageSize(0);
        assertEquals(25, fileAuthPager.getPageSize());
        fileAuthPager.setPageSize(999);
        assertEquals(999, fileAuthPager.getPageSize());
        fileAuthPager.setCurrentPageNumber(0);
        assertEquals(1, fileAuthPager.getCurrentPageNumber());
        fileAuthPager.setCurrentPageNumber(999);
        assertEquals(999, fileAuthPager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        fileAuthPager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, fileAuthPager.getPageNumberList());

    }

    public void test_clear() {
        FileAuthPager fileAuthPager = new FileAuthPager();
        fileAuthPager.id = "testId";
        fileAuthPager.hostname = "test.example.com";
        fileAuthPager.versionNo = "1";
        fileAuthPager.setAllRecordCount(100);
        fileAuthPager.setAllPageCount(10);
        fileAuthPager.setExistPrePage(true);
        fileAuthPager.setExistNextPage(true);

        fileAuthPager.clear();

        assertNull(fileAuthPager.id);
        assertNull(fileAuthPager.hostname);
        assertNull(fileAuthPager.versionNo);
        assertEquals(0, fileAuthPager.getAllRecordCount());
        assertEquals(0, fileAuthPager.getAllPageCount());
        assertFalse(fileAuthPager.isExistPrePage());
        assertFalse(fileAuthPager.isExistNextPage());
    }
}
