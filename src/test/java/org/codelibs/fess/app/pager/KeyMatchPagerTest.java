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

public class KeyMatchPagerTest extends UnitFessTestCase {

    public void test_KeyMatchPager() {
        KeyMatchPager keyMatchPager = new KeyMatchPager();

        keyMatchPager.clear();
        assertEquals(0, keyMatchPager.getAllRecordCount());
        assertEquals(0, keyMatchPager.getAllPageCount());
        assertEquals(false, keyMatchPager.isExistPrePage());
        assertEquals(false, keyMatchPager.isExistNextPage());
        assertEquals(25, keyMatchPager.getPageSize());
        assertEquals(1, keyMatchPager.getCurrentPageNumber());
        assertNull(keyMatchPager.id);
        assertNull(keyMatchPager.term);
        assertNull(keyMatchPager.query);
        assertNull(keyMatchPager.versionNo);

        keyMatchPager.setAllRecordCount(999);
        assertEquals(999, keyMatchPager.getAllRecordCount());
        keyMatchPager.setAllPageCount(999);
        assertEquals(999, keyMatchPager.getAllPageCount());
        keyMatchPager.setExistPrePage(true);
        assertTrue(keyMatchPager.isExistPrePage());
        keyMatchPager.setExistNextPage(true);
        assertTrue(keyMatchPager.isExistNextPage());
        keyMatchPager.setPageSize(0);
        assertEquals(25, keyMatchPager.getPageSize());
        keyMatchPager.setPageSize(999);
        assertEquals(999, keyMatchPager.getPageSize());
        keyMatchPager.setCurrentPageNumber(0);
        assertEquals(1, keyMatchPager.getCurrentPageNumber());
        keyMatchPager.setCurrentPageNumber(999);
        assertEquals(999, keyMatchPager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        keyMatchPager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, keyMatchPager.getPageNumberList());

    }

    public void test_clear() {
        KeyMatchPager keyMatchPager = new KeyMatchPager();
        keyMatchPager.id = "testId";
        keyMatchPager.term = "testTerm";
        keyMatchPager.query = "testQuery";
        keyMatchPager.versionNo = "1";
        keyMatchPager.setAllRecordCount(100);
        keyMatchPager.setAllPageCount(10);
        keyMatchPager.setExistPrePage(true);
        keyMatchPager.setExistNextPage(true);

        keyMatchPager.clear();

        assertNull(keyMatchPager.id);
        assertNull(keyMatchPager.term);
        assertNull(keyMatchPager.query);
        assertNull(keyMatchPager.versionNo);
        assertEquals(0, keyMatchPager.getAllRecordCount());
        assertEquals(0, keyMatchPager.getAllPageCount());
        assertFalse(keyMatchPager.isExistPrePage());
        assertFalse(keyMatchPager.isExistNextPage());
    }
}
