/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

public class CharMappingPagerTest extends UnitFessTestCase {

    public void test_CharMappingPagerTest() {
        CharMappingPager charmappingpager = new CharMappingPager();

        charmappingpager.clear();
        assertEquals(0, charmappingpager.getAllRecordCount());
        assertEquals(0, charmappingpager.getAllPageCount());
        assertEquals(false, charmappingpager.isExistPrePage());
        assertEquals(false, charmappingpager.isExistNextPage());
        assertEquals(25, charmappingpager.getPageSize());
        assertEquals(1, charmappingpager.getCurrentPageNumber());
        assertNull(charmappingpager.id);

        assertEquals(25, charmappingpager.getDefaultPageSize());
        assertEquals(1, charmappingpager.getDefaultCurrentPageNumber());

        charmappingpager.setAllRecordCount(999);
        assertEquals(999, charmappingpager.getAllRecordCount());
        charmappingpager.setAllPageCount(999);
        assertEquals(999, charmappingpager.getAllRecordCount());
        charmappingpager.setExistPrePage(true);
        assertTrue(charmappingpager.isExistPrePage());
        charmappingpager.setExistNextPage(true);
        assertTrue(charmappingpager.isExistNextPage());
        charmappingpager.setPageSize(0);
        assertEquals(25, charmappingpager.getPageSize());
        charmappingpager.setPageSize(999);
        assertEquals(999, charmappingpager.getPageSize());
        charmappingpager.setCurrentPageNumber(0);
        assertEquals(1, charmappingpager.getCurrentPageNumber());
        charmappingpager.setCurrentPageNumber(999);
        assertEquals(999, charmappingpager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        charmappingpager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, charmappingpager.getPageNumberList());
    }
}