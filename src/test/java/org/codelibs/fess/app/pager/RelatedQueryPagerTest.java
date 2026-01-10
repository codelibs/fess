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

public class RelatedQueryPagerTest extends UnitFessTestCase {

    public void test_RelatedQueryPager() {
        RelatedQueryPager relatedQueryPager = new RelatedQueryPager();

        relatedQueryPager.clear();
        assertEquals(0, relatedQueryPager.getAllRecordCount());
        assertEquals(0, relatedQueryPager.getAllPageCount());
        assertEquals(false, relatedQueryPager.isExistPrePage());
        assertEquals(false, relatedQueryPager.isExistNextPage());
        assertEquals(25, relatedQueryPager.getPageSize());
        assertEquals(1, relatedQueryPager.getCurrentPageNumber());
        assertNull(relatedQueryPager.id);
        assertNull(relatedQueryPager.term);
        assertNull(relatedQueryPager.versionNo);

        relatedQueryPager.setAllRecordCount(999);
        assertEquals(999, relatedQueryPager.getAllRecordCount());
        relatedQueryPager.setAllPageCount(999);
        assertEquals(999, relatedQueryPager.getAllPageCount());
        relatedQueryPager.setExistPrePage(true);
        assertTrue(relatedQueryPager.isExistPrePage());
        relatedQueryPager.setExistNextPage(true);
        assertTrue(relatedQueryPager.isExistNextPage());
        relatedQueryPager.setPageSize(0);
        assertEquals(25, relatedQueryPager.getPageSize());
        relatedQueryPager.setPageSize(999);
        assertEquals(999, relatedQueryPager.getPageSize());
        relatedQueryPager.setCurrentPageNumber(0);
        assertEquals(1, relatedQueryPager.getCurrentPageNumber());
        relatedQueryPager.setCurrentPageNumber(999);
        assertEquals(999, relatedQueryPager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        relatedQueryPager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, relatedQueryPager.getPageNumberList());

    }

    public void test_clear() {
        RelatedQueryPager relatedQueryPager = new RelatedQueryPager();
        relatedQueryPager.id = "testId";
        relatedQueryPager.term = "testTerm";
        relatedQueryPager.versionNo = "1";
        relatedQueryPager.setAllRecordCount(100);
        relatedQueryPager.setAllPageCount(10);
        relatedQueryPager.setExistPrePage(true);
        relatedQueryPager.setExistNextPage(true);

        relatedQueryPager.clear();

        assertNull(relatedQueryPager.id);
        assertNull(relatedQueryPager.term);
        assertNull(relatedQueryPager.versionNo);
        assertEquals(0, relatedQueryPager.getAllRecordCount());
        assertEquals(0, relatedQueryPager.getAllPageCount());
        assertFalse(relatedQueryPager.isExistPrePage());
        assertFalse(relatedQueryPager.isExistNextPage());
    }
}
