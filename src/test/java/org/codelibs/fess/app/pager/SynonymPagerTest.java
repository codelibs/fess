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

public class SynonymPagerTest extends UnitFessTestCase {

    public void test_SynonymPager() {
        SynonymPager synonymPager = new SynonymPager();

        synonymPager.clear();
        assertEquals(0, synonymPager.getAllRecordCount());
        assertEquals(0, synonymPager.getAllPageCount());
        assertEquals(false, synonymPager.isExistPrePage());
        assertEquals(false, synonymPager.isExistNextPage());
        assertEquals(25, synonymPager.getPageSize());
        assertEquals(1, synonymPager.getCurrentPageNumber());
        assertNull(synonymPager.id);

        synonymPager.setAllRecordCount(999);
        assertEquals(999, synonymPager.getAllRecordCount());
        synonymPager.setAllPageCount(999);
        assertEquals(999, synonymPager.getAllPageCount());
        synonymPager.setExistPrePage(true);
        assertTrue(synonymPager.isExistPrePage());
        synonymPager.setExistNextPage(true);
        assertTrue(synonymPager.isExistNextPage());
        synonymPager.setPageSize(0);
        assertEquals(25, synonymPager.getPageSize());
        synonymPager.setPageSize(999);
        assertEquals(999, synonymPager.getPageSize());
        synonymPager.setCurrentPageNumber(0);
        assertEquals(1, synonymPager.getCurrentPageNumber());
        synonymPager.setCurrentPageNumber(999);
        assertEquals(999, synonymPager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        synonymPager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, synonymPager.getPageNumberList());

    }

    public void test_clear() {
        SynonymPager synonymPager = new SynonymPager();
        synonymPager.id = "testId";
        synonymPager.setAllRecordCount(100);
        synonymPager.setAllPageCount(10);
        synonymPager.setExistPrePage(true);
        synonymPager.setExistNextPage(true);

        synonymPager.clear();

        assertNull(synonymPager.id);
        assertEquals(0, synonymPager.getAllRecordCount());
        assertEquals(0, synonymPager.getAllPageCount());
        assertFalse(synonymPager.isExistPrePage());
        assertFalse(synonymPager.isExistNextPage());
    }
}
