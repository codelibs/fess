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

public class DataConfigPagerTest extends UnitFessTestCase {

    public void test_DataConfigPager() {
        DataConfigPager dataconfigpager = new DataConfigPager();

        dataconfigpager.clear();
        assertEquals(0, dataconfigpager.getAllRecordCount());
        assertEquals(0, dataconfigpager.getAllPageCount());
        assertEquals(false, dataconfigpager.isExistPrePage());
        assertEquals(false, dataconfigpager.isExistNextPage());
        assertEquals(25, dataconfigpager.getPageSize());
        assertEquals(1, dataconfigpager.getCurrentPageNumber());
        assertNull(dataconfigpager.id);
        assertNull(dataconfigpager.name);
        assertNull(dataconfigpager.handlerName);
        assertNull(dataconfigpager.boost);
        assertNull(dataconfigpager.available);
        assertNull(dataconfigpager.sortOrder);
        assertNull(dataconfigpager.createdBy);
        assertNull(dataconfigpager.createdTime);
        assertNull(dataconfigpager.versionNo);

        assertEquals(1, dataconfigpager.getDefaultCurrentPageNumber());
        dataconfigpager.setAllRecordCount(999);
        assertEquals(999, dataconfigpager.getAllRecordCount());
        dataconfigpager.setAllPageCount(999);
        assertEquals(999, dataconfigpager.getAllPageCount());
        dataconfigpager.setExistPrePage(true);
        assertTrue(dataconfigpager.isExistPrePage());
        dataconfigpager.setExistNextPage(true);
        assertTrue(dataconfigpager.isExistNextPage());
        dataconfigpager.setPageSize(0);
        assertEquals(25, dataconfigpager.getPageSize());
        dataconfigpager.setPageSize(999);
        assertEquals(999, dataconfigpager.getPageSize());
        dataconfigpager.setCurrentPageNumber(0);
        assertEquals(1, dataconfigpager.getCurrentPageNumber());
        dataconfigpager.setCurrentPageNumber(999);
        assertEquals(999, dataconfigpager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        dataconfigpager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, dataconfigpager.getPageNumberList());
        assertEquals(25, dataconfigpager.getDefaultPageSize());

    }
}