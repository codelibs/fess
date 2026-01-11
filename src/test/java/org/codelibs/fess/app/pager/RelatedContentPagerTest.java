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

public class RelatedContentPagerTest extends UnitFessTestCase {

    public void test_RelatedContentPager() {
        RelatedContentPager relatedContentPager = new RelatedContentPager();

        relatedContentPager.clear();
        assertEquals(0, relatedContentPager.getAllRecordCount());
        assertEquals(0, relatedContentPager.getAllPageCount());
        assertEquals(false, relatedContentPager.isExistPrePage());
        assertEquals(false, relatedContentPager.isExistNextPage());
        assertEquals(25, relatedContentPager.getPageSize());
        assertEquals(1, relatedContentPager.getCurrentPageNumber());
        assertNull(relatedContentPager.id);
        assertNull(relatedContentPager.term);
        assertNull(relatedContentPager.versionNo);

        relatedContentPager.setAllRecordCount(999);
        assertEquals(999, relatedContentPager.getAllRecordCount());
        relatedContentPager.setAllPageCount(999);
        assertEquals(999, relatedContentPager.getAllPageCount());
        relatedContentPager.setExistPrePage(true);
        assertTrue(relatedContentPager.isExistPrePage());
        relatedContentPager.setExistNextPage(true);
        assertTrue(relatedContentPager.isExistNextPage());
        relatedContentPager.setPageSize(0);
        assertEquals(25, relatedContentPager.getPageSize());
        relatedContentPager.setPageSize(999);
        assertEquals(999, relatedContentPager.getPageSize());
        relatedContentPager.setCurrentPageNumber(0);
        assertEquals(1, relatedContentPager.getCurrentPageNumber());
        relatedContentPager.setCurrentPageNumber(999);
        assertEquals(999, relatedContentPager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        relatedContentPager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, relatedContentPager.getPageNumberList());

    }

    public void test_clear() {
        RelatedContentPager relatedContentPager = new RelatedContentPager();
        relatedContentPager.id = "testId";
        relatedContentPager.term = "testTerm";
        relatedContentPager.versionNo = "1";
        relatedContentPager.setAllRecordCount(100);
        relatedContentPager.setAllPageCount(10);
        relatedContentPager.setExistPrePage(true);
        relatedContentPager.setExistNextPage(true);

        relatedContentPager.clear();

        assertNull(relatedContentPager.id);
        assertNull(relatedContentPager.term);
        assertNull(relatedContentPager.versionNo);
        assertEquals(0, relatedContentPager.getAllRecordCount());
        assertEquals(0, relatedContentPager.getAllPageCount());
        assertFalse(relatedContentPager.isExistPrePage());
        assertFalse(relatedContentPager.isExistNextPage());
    }
}
