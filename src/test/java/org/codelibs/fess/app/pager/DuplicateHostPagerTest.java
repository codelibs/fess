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

public class DuplicateHostPagerTest extends UnitFessTestCase {

    public void test_DuplicateHostPager() {
        DuplicateHostPager duplicatehostpager = new DuplicateHostPager();

        duplicatehostpager.clear();
        assertEquals(0, duplicatehostpager.getAllRecordCount());
        assertEquals(0, duplicatehostpager.getAllPageCount());
        assertEquals(false, duplicatehostpager.isExistPrePage());
        assertEquals(false, duplicatehostpager.isExistNextPage());
        assertEquals(25, duplicatehostpager.getPageSize());
        assertEquals(1, duplicatehostpager.getCurrentPageNumber());
        assertNull(duplicatehostpager.id);
        assertNull(duplicatehostpager.regularName);
        assertNull(duplicatehostpager.duplicateHostName);
        assertNull(duplicatehostpager.sortOrder);
        assertNull(duplicatehostpager.createdBy);
        assertNull(duplicatehostpager.createdTime);
        assertNull(duplicatehostpager.versionNo);

        assertEquals(1, duplicatehostpager.getDefaultCurrentPageNumber());
        duplicatehostpager.setAllRecordCount(999);
        assertEquals(999, duplicatehostpager.getAllRecordCount());
        duplicatehostpager.setAllPageCount(999);
        assertEquals(999, duplicatehostpager.getAllPageCount());
        duplicatehostpager.setExistPrePage(true);
        assertTrue(duplicatehostpager.isExistPrePage());
        duplicatehostpager.setExistNextPage(true);
        assertTrue(duplicatehostpager.isExistNextPage());
        duplicatehostpager.setPageSize(0);
        assertEquals(25, duplicatehostpager.getPageSize());
        duplicatehostpager.setPageSize(999);
        assertEquals(999, duplicatehostpager.getPageSize());
        duplicatehostpager.setCurrentPageNumber(0);
        assertEquals(1, duplicatehostpager.getCurrentPageNumber());
        duplicatehostpager.setCurrentPageNumber(999);
        assertEquals(999, duplicatehostpager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        duplicatehostpager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, duplicatehostpager.getPageNumberList());
        assertEquals(25, duplicatehostpager.getDefaultPageSize());
    }
}
