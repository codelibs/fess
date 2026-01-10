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

public class ElevateWordPagerTest extends UnitFessTestCase {

    public void test_ElevateWordPager() {
        ElevateWordPager elevateWordPager = new ElevateWordPager();

        elevateWordPager.clear();
        assertEquals(0, elevateWordPager.getAllRecordCount());
        assertEquals(0, elevateWordPager.getAllPageCount());
        assertEquals(false, elevateWordPager.isExistPrePage());
        assertEquals(false, elevateWordPager.isExistNextPage());
        assertEquals(25, elevateWordPager.getPageSize());
        assertEquals(1, elevateWordPager.getCurrentPageNumber());
        assertNull(elevateWordPager.id);
        assertNull(elevateWordPager.suggestWord);
        assertNull(elevateWordPager.versionNo);

        elevateWordPager.setAllRecordCount(999);
        assertEquals(999, elevateWordPager.getAllRecordCount());
        elevateWordPager.setAllPageCount(999);
        assertEquals(999, elevateWordPager.getAllPageCount());
        elevateWordPager.setExistPrePage(true);
        assertTrue(elevateWordPager.isExistPrePage());
        elevateWordPager.setExistNextPage(true);
        assertTrue(elevateWordPager.isExistNextPage());
        elevateWordPager.setPageSize(0);
        assertEquals(25, elevateWordPager.getPageSize());
        elevateWordPager.setPageSize(999);
        assertEquals(999, elevateWordPager.getPageSize());
        elevateWordPager.setCurrentPageNumber(0);
        assertEquals(1, elevateWordPager.getCurrentPageNumber());
        elevateWordPager.setCurrentPageNumber(999);
        assertEquals(999, elevateWordPager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        elevateWordPager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, elevateWordPager.getPageNumberList());

    }

    public void test_clear() {
        ElevateWordPager elevateWordPager = new ElevateWordPager();
        elevateWordPager.id = "testId";
        elevateWordPager.suggestWord = "testWord";
        elevateWordPager.versionNo = "1";
        elevateWordPager.setAllRecordCount(100);
        elevateWordPager.setAllPageCount(10);
        elevateWordPager.setExistPrePage(true);
        elevateWordPager.setExistNextPage(true);

        elevateWordPager.clear();

        assertNull(elevateWordPager.id);
        assertNull(elevateWordPager.suggestWord);
        assertNull(elevateWordPager.versionNo);
        assertEquals(0, elevateWordPager.getAllRecordCount());
        assertEquals(0, elevateWordPager.getAllPageCount());
        assertFalse(elevateWordPager.isExistPrePage());
        assertFalse(elevateWordPager.isExistNextPage());
    }
}
