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

public class BadWordPagerTest extends UnitFessTestCase {

    public void test_badWordPager() {
        BadWordPager badwordpager = new BadWordPager();

        badwordpager.clear();
        assertEquals(0, badwordpager.getAllRecordCount());
        assertEquals(0, badwordpager.getAllPageCount());
        assertEquals(false, badwordpager.isExistPrePage());
        assertEquals(false, badwordpager.isExistNextPage());
        assertEquals(25, badwordpager.getPageSize());
        assertEquals(1, badwordpager.getCurrentPageNumber());
        assertNull(badwordpager.id);
        assertNull(badwordpager.suggestWord);
        assertNull(badwordpager.createdBy);
        assertNull(badwordpager.createdTime);
        assertNull(badwordpager.versionNo);

        assertEquals(1, badwordpager.getDefaultCurrentPageNumber());
        badwordpager.setAllRecordCount(999);
        assertEquals(999, badwordpager.getAllRecordCount());
        badwordpager.setAllPageCount(999);
        assertEquals(999, badwordpager.getAllPageCount());
        badwordpager.setExistPrePage(true);
        assertTrue(badwordpager.isExistPrePage());
        badwordpager.setExistNextPage(true);
        assertTrue(badwordpager.isExistNextPage());
        badwordpager.setPageSize(0);
        assertEquals(25, badwordpager.getPageSize());
        badwordpager.setPageSize(999);
        assertEquals(999, badwordpager.getPageSize());
        badwordpager.setCurrentPageNumber(0);
        assertEquals(1, badwordpager.getCurrentPageNumber());
        badwordpager.setCurrentPageNumber(999);
        assertEquals(999, badwordpager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        badwordpager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, badwordpager.getPageNumberList());
        assertEquals(25, badwordpager.getDefaultPageSize());
    }
}