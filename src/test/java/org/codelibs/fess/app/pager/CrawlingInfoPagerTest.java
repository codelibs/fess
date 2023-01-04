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

public class CrawlingInfoPagerTest extends UnitFessTestCase {

    public void test_CrawlingInfoPage() {
        CrawlingInfoPager crawlinginfopage = new CrawlingInfoPager();

        crawlinginfopage.clear();
        assertEquals(0, crawlinginfopage.getAllRecordCount());
        assertEquals(0, crawlinginfopage.getAllPageCount());
        assertEquals(false, crawlinginfopage.isExistPrePage());
        assertEquals(false, crawlinginfopage.isExistNextPage());
        assertEquals(25, crawlinginfopage.getPageSize());
        assertEquals(1, crawlinginfopage.getCurrentPageNumber());
        assertNull(crawlinginfopage.id);
        assertNull(crawlinginfopage.sessionId);
        assertNull(crawlinginfopage.createdTime);

        assertEquals(1, crawlinginfopage.getDefaultCurrentPageNumber());
        crawlinginfopage.setAllRecordCount(999);
        assertEquals(999, crawlinginfopage.getAllRecordCount());
        crawlinginfopage.setAllPageCount(999);
        assertEquals(999, crawlinginfopage.getAllPageCount());
        crawlinginfopage.setExistPrePage(true);
        assertTrue(crawlinginfopage.isExistPrePage());
        crawlinginfopage.setExistNextPage(true);
        assertTrue(crawlinginfopage.isExistNextPage());
        crawlinginfopage.setPageSize(0);
        assertEquals(25, crawlinginfopage.getPageSize());
        crawlinginfopage.setPageSize(999);
        assertEquals(999, crawlinginfopage.getPageSize());
        crawlinginfopage.setCurrentPageNumber(0);
        assertEquals(1, crawlinginfopage.getCurrentPageNumber());
        crawlinginfopage.setCurrentPageNumber(999);
        assertEquals(999, crawlinginfopage.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        crawlinginfopage.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, crawlinginfopage.getPageNumberList());
        assertEquals(25, crawlinginfopage.getDefaultPageSize());

    }

}