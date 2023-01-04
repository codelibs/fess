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

public class AccessTokenPagerTest extends UnitFessTestCase {

    public void test_AccessTokenPager() {
        AccessTokenPager accesstokenpager = new AccessTokenPager();

        accesstokenpager.clear();
        assertEquals(0, accesstokenpager.getAllRecordCount());
        assertEquals(0, accesstokenpager.getAllPageCount());
        assertEquals(false, accesstokenpager.isExistPrePage());
        assertEquals(false, accesstokenpager.isExistNextPage());
        assertEquals(25, accesstokenpager.getPageSize());
        assertEquals(1, accesstokenpager.getCurrentPageNumber());
        assertNull(accesstokenpager.id);
        assertNull(accesstokenpager.name);
        assertNull(accesstokenpager.createdBy);
        assertNull(accesstokenpager.createdTime);
        assertNull(accesstokenpager.versionNo);

        accesstokenpager.setAllRecordCount(999);
        assertEquals(999, accesstokenpager.getAllRecordCount());
        accesstokenpager.setAllPageCount(999);
        assertEquals(999, accesstokenpager.getAllPageCount());
        accesstokenpager.setExistPrePage(true);
        assertTrue(accesstokenpager.isExistPrePage());
        accesstokenpager.setExistNextPage(true);
        assertTrue(accesstokenpager.isExistNextPage());
        accesstokenpager.setPageSize(0);
        assertEquals(25, accesstokenpager.getPageSize());
        accesstokenpager.setPageSize(999);
        assertEquals(999, accesstokenpager.getPageSize());
        accesstokenpager.setCurrentPageNumber(0);
        assertEquals(1, accesstokenpager.getCurrentPageNumber());
        accesstokenpager.setCurrentPageNumber(999);
        assertEquals(999, accesstokenpager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        accesstokenpager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, accesstokenpager.getPageNumberList());
        assertEquals(1, accesstokenpager.getDefaultCurrentPageNumber());
        assertEquals(25, accesstokenpager.getDefaultPageSize());

    }
}