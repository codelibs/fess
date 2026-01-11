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

public class KuromojiPagerTest extends UnitFessTestCase {

    public void test_KuromojiPager() {
        KuromojiPager kuromojiPager = new KuromojiPager();

        kuromojiPager.clear();
        assertEquals(0, kuromojiPager.getAllRecordCount());
        assertEquals(0, kuromojiPager.getAllPageCount());
        assertEquals(false, kuromojiPager.isExistPrePage());
        assertEquals(false, kuromojiPager.isExistNextPage());
        assertEquals(25, kuromojiPager.getPageSize());
        assertEquals(1, kuromojiPager.getCurrentPageNumber());
        assertNull(kuromojiPager.id);

        kuromojiPager.setAllRecordCount(999);
        assertEquals(999, kuromojiPager.getAllRecordCount());
        kuromojiPager.setAllPageCount(999);
        assertEquals(999, kuromojiPager.getAllPageCount());
        kuromojiPager.setExistPrePage(true);
        assertTrue(kuromojiPager.isExistPrePage());
        kuromojiPager.setExistNextPage(true);
        assertTrue(kuromojiPager.isExistNextPage());
        kuromojiPager.setPageSize(0);
        assertEquals(25, kuromojiPager.getPageSize());
        kuromojiPager.setPageSize(999);
        assertEquals(999, kuromojiPager.getPageSize());
        kuromojiPager.setCurrentPageNumber(0);
        assertEquals(1, kuromojiPager.getCurrentPageNumber());
        kuromojiPager.setCurrentPageNumber(999);
        assertEquals(999, kuromojiPager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        kuromojiPager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, kuromojiPager.getPageNumberList());

    }

    public void test_clear() {
        KuromojiPager kuromojiPager = new KuromojiPager();
        kuromojiPager.id = "testId";
        kuromojiPager.setAllRecordCount(100);
        kuromojiPager.setAllPageCount(10);
        kuromojiPager.setExistPrePage(true);
        kuromojiPager.setExistNextPage(true);

        kuromojiPager.clear();

        assertNull(kuromojiPager.id);
        assertEquals(0, kuromojiPager.getAllRecordCount());
        assertEquals(0, kuromojiPager.getAllPageCount());
        assertFalse(kuromojiPager.isExistPrePage());
        assertFalse(kuromojiPager.isExistNextPage());
    }
}
