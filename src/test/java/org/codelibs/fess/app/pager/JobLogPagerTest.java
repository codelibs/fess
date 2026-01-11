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

public class JobLogPagerTest extends UnitFessTestCase {

    public void test_JobLogPager() {
        JobLogPager jobLogPager = new JobLogPager();

        jobLogPager.clear();
        assertEquals(0, jobLogPager.getAllRecordCount());
        assertEquals(0, jobLogPager.getAllPageCount());
        assertEquals(false, jobLogPager.isExistPrePage());
        assertEquals(false, jobLogPager.isExistNextPage());
        assertEquals(25, jobLogPager.getPageSize());
        assertEquals(1, jobLogPager.getCurrentPageNumber());
        assertNull(jobLogPager.id);
        assertNull(jobLogPager.jobName);
        assertNull(jobLogPager.jobStatus);
        assertNull(jobLogPager.target);
        assertNull(jobLogPager.scriptType);

        jobLogPager.setAllRecordCount(999);
        assertEquals(999, jobLogPager.getAllRecordCount());
        jobLogPager.setAllPageCount(999);
        assertEquals(999, jobLogPager.getAllPageCount());
        jobLogPager.setExistPrePage(true);
        assertTrue(jobLogPager.isExistPrePage());
        jobLogPager.setExistNextPage(true);
        assertTrue(jobLogPager.isExistNextPage());
        jobLogPager.setPageSize(0);
        assertEquals(25, jobLogPager.getPageSize());
        jobLogPager.setPageSize(999);
        assertEquals(999, jobLogPager.getPageSize());
        jobLogPager.setCurrentPageNumber(0);
        assertEquals(1, jobLogPager.getCurrentPageNumber());
        jobLogPager.setCurrentPageNumber(999);
        assertEquals(999, jobLogPager.getCurrentPageNumber());
        List<Integer> pageNumberList = new ArrayList<Integer>(1);
        jobLogPager.setPageNumberList(pageNumberList);
        assertEquals(pageNumberList, jobLogPager.getPageNumberList());

    }

    public void test_clear() {
        JobLogPager jobLogPager = new JobLogPager();
        jobLogPager.id = "testId";
        jobLogPager.jobName = "testJobName";
        jobLogPager.jobStatus = "running";
        jobLogPager.target = "all";
        jobLogPager.scriptType = "groovy";
        jobLogPager.setAllRecordCount(100);
        jobLogPager.setAllPageCount(10);
        jobLogPager.setExistPrePage(true);
        jobLogPager.setExistNextPage(true);

        jobLogPager.clear();

        assertNull(jobLogPager.id);
        assertNull(jobLogPager.jobName);
        assertNull(jobLogPager.jobStatus);
        assertNull(jobLogPager.target);
        assertNull(jobLogPager.scriptType);
        assertEquals(0, jobLogPager.getAllRecordCount());
        assertEquals(0, jobLogPager.getAllPageCount());
        assertFalse(jobLogPager.isExistPrePage());
        assertFalse(jobLogPager.isExistNextPage());
    }
}
