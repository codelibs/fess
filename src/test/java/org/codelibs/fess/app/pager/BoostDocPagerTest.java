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

public class BoostDocPagerTest extends UnitFessTestCase {

    public void test_BoostDocPage() {
        BoostDocPager boostdocpager = new BoostDocPager();

        boostdocpager.clear();
        assertEquals(0, boostdocpager.getAllRecordCount());
        assertEquals(0, boostdocpager.getAllPageCount());
        assertEquals(false, boostdocpager.isExistPrePage());
        assertEquals(false, boostdocpager.isExistNextPage());
        assertEquals(25, boostdocpager.getPageSize());
        assertEquals(1, boostdocpager.getCurrentPageNumber());
        assertNull(boostdocpager.id);
        assertNull(boostdocpager.urlExpr);
        assertNull(boostdocpager.boostExpr);
        assertNull(boostdocpager.sortOrder);
        assertNull(boostdocpager.createdBy);
        assertNull(boostdocpager.createdTime);
        assertNull(boostdocpager.versionNo);

        boostdocpager.setAllRecordCount(999);
        assertEquals(999, boostdocpager.getAllRecordCount());
        boostdocpager.setAllPageCount(999);
        assertEquals(999, boostdocpager.getAllPageCount());
        boostdocpager.setExistPrePage(true);
        assertTrue(boostdocpager.isExistPrePage());
        boostdocpager.setExistNextPage(true);
        assertTrue(boostdocpager.isExistNextPage());
        boostdocpager.setPageSize(0);
        assertEquals(25, boostdocpager.getPageSize());
        boostdocpager.setPageSize(999);
        assertEquals(999, boostdocpager.getPageSize());
        boostdocpager.setCurrentPageNumber(0);
        assertEquals(1, boostdocpager.getCurrentPageNumber());
        boostdocpager.setCurrentPageNumber(999);
        assertEquals(999, boostdocpager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        boostdocpager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, boostdocpager.getPageNumberList());
        assertEquals(1, boostdocpager.getDefaultCurrentPageNumber());
        assertEquals(25, boostdocpager.getDefaultPageSize());

    }
}