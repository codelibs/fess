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

public class PathMapPagerTest extends UnitFessTestCase {

    public void test_PathMapPager() {
        PathMapPager pathMapPager = new PathMapPager();

        pathMapPager.clear();
        assertEquals(0, pathMapPager.getAllRecordCount());
        assertEquals(0, pathMapPager.getAllPageCount());
        assertEquals(false, pathMapPager.isExistPrePage());
        assertEquals(false, pathMapPager.isExistNextPage());
        assertEquals(25, pathMapPager.getPageSize());
        assertEquals(1, pathMapPager.getCurrentPageNumber());
        assertNull(pathMapPager.id);
        assertNull(pathMapPager.regex);
        assertNull(pathMapPager.replacement);
        assertNull(pathMapPager.versionNo);

        pathMapPager.setAllRecordCount(999);
        assertEquals(999, pathMapPager.getAllRecordCount());
        pathMapPager.setAllPageCount(999);
        assertEquals(999, pathMapPager.getAllPageCount());
        pathMapPager.setExistPrePage(true);
        assertTrue(pathMapPager.isExistPrePage());
        pathMapPager.setExistNextPage(true);
        assertTrue(pathMapPager.isExistNextPage());
        pathMapPager.setPageSize(0);
        assertEquals(25, pathMapPager.getPageSize());
        pathMapPager.setPageSize(999);
        assertEquals(999, pathMapPager.getPageSize());
        pathMapPager.setCurrentPageNumber(0);
        assertEquals(1, pathMapPager.getCurrentPageNumber());
        pathMapPager.setCurrentPageNumber(999);
        assertEquals(999, pathMapPager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        pathMapPager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, pathMapPager.getPageNumberList());

    }

    public void test_clear() {
        PathMapPager pathMapPager = new PathMapPager();
        pathMapPager.id = "testId";
        pathMapPager.regex = "testRegex";
        pathMapPager.replacement = "testReplacement";
        pathMapPager.versionNo = "1";
        pathMapPager.setAllRecordCount(100);
        pathMapPager.setAllPageCount(10);
        pathMapPager.setExistPrePage(true);
        pathMapPager.setExistNextPage(true);

        pathMapPager.clear();

        assertNull(pathMapPager.id);
        assertNull(pathMapPager.regex);
        assertNull(pathMapPager.replacement);
        assertNull(pathMapPager.versionNo);
        assertEquals(0, pathMapPager.getAllRecordCount());
        assertEquals(0, pathMapPager.getAllPageCount());
        assertFalse(pathMapPager.isExistPrePage());
        assertFalse(pathMapPager.isExistNextPage());
    }
}
