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
package org.codelibs.fess.util;

import java.util.List;

import org.codelibs.fess.unit.UnitFessTestCase;

public class QueryResponseListTest extends UnitFessTestCase {
    public void test_calculatePageInfo_page0() {
        QueryResponseList qrList;

        qrList = new QueryResponseList(null, 0, 20, 0) {
            @Override
            public int size() {
                return 0;
            }
        };
        qrList.calculatePageInfo();
        assertEquals(20, qrList.getPageSize());
        assertEquals(1, qrList.getCurrentPageNumber());
        assertEquals(0, qrList.getAllRecordCount());
        assertEquals(1, qrList.getAllPageCount());
        assertEquals(false, qrList.isExistPrevPage());
        assertEquals(false, qrList.isExistNextPage());
        assertEquals(0, qrList.getCurrentStartRecordNumber());
        assertEquals(0, qrList.getCurrentEndRecordNumber());
    }

    public void test_calculatePageInfo_page1() {
        QueryResponseList qrList;

        qrList = new QueryResponseList(null, 0, 20, 0) {
            @Override
            public int size() {
                return 10;
            }
        };
        qrList.allRecordCount = 10;
        qrList.calculatePageInfo();
        assertEquals(20, qrList.getPageSize());
        assertEquals(1, qrList.getCurrentPageNumber());
        assertEquals(10, qrList.getAllRecordCount());
        assertEquals(1, qrList.getAllPageCount());
        assertEquals(false, qrList.isExistPrevPage());
        assertEquals(false, qrList.isExistNextPage());
        assertEquals(1, qrList.getCurrentStartRecordNumber());
        assertEquals(10, qrList.getCurrentEndRecordNumber());

        qrList = new QueryResponseList(null, 0, 20, 0) {
            @Override
            public int size() {
                return 20;
            }
        };
        qrList.allRecordCount = 20;
        qrList.calculatePageInfo();
        assertEquals(20, qrList.getPageSize());
        assertEquals(1, qrList.getCurrentPageNumber());
        assertEquals(20, qrList.getAllRecordCount());
        assertEquals(1, qrList.getAllPageCount());
        assertEquals(false, qrList.isExistPrevPage());
        assertEquals(false, qrList.isExistNextPage());
        assertEquals(1, qrList.getCurrentStartRecordNumber());
        assertEquals(20, qrList.getCurrentEndRecordNumber());

        qrList = new QueryResponseList(null, 0, 20, 0) {
            @Override
            public int size() {
                return 20;
            }
        };
        qrList.allRecordCount = 21;
        qrList.calculatePageInfo();
        assertEquals(20, qrList.getPageSize());
        assertEquals(1, qrList.getCurrentPageNumber());
        assertEquals(21, qrList.getAllRecordCount());
        assertEquals(2, qrList.getAllPageCount());
        assertEquals(false, qrList.isExistPrevPage());
        assertEquals(true, qrList.isExistNextPage());
        assertEquals(1, qrList.getCurrentStartRecordNumber());
        assertEquals(20, qrList.getCurrentEndRecordNumber());

        qrList = new QueryResponseList(null, 0, 20, 0) {
            @Override
            public int size() {
                return 20;
            }
        };
        qrList.allRecordCount = 40;
        qrList.calculatePageInfo();
        assertEquals(20, qrList.getPageSize());
        assertEquals(1, qrList.getCurrentPageNumber());
        assertEquals(40, qrList.getAllRecordCount());
        assertEquals(2, qrList.getAllPageCount());
        assertEquals(false, qrList.isExistPrevPage());
        assertEquals(true, qrList.isExistNextPage());
        assertEquals(1, qrList.getCurrentStartRecordNumber());
        assertEquals(20, qrList.getCurrentEndRecordNumber());

        qrList = new QueryResponseList(null, 0, 20, 0) {
            @Override
            public int size() {
                return 20;
            }
        };
        qrList.allRecordCount = 41;
        qrList.calculatePageInfo();
        assertEquals(20, qrList.getPageSize());
        assertEquals(1, qrList.getCurrentPageNumber());
        assertEquals(41, qrList.getAllRecordCount());
        assertEquals(3, qrList.getAllPageCount());
        assertEquals(false, qrList.isExistPrevPage());
        assertEquals(true, qrList.isExistNextPage());
        assertEquals(1, qrList.getCurrentStartRecordNumber());
        assertEquals(20, qrList.getCurrentEndRecordNumber());
    }

    public void test_calculatePageInfo_page2() {
        QueryResponseList qrList;

        qrList = new QueryResponseList(null, 20, 20, 0) {
            @Override
            public int size() {
                return 1;
            }
        };
        qrList.allRecordCount = 21;
        qrList.calculatePageInfo();
        assertEquals(20, qrList.getPageSize());
        assertEquals(2, qrList.getCurrentPageNumber());
        assertEquals(21, qrList.getAllRecordCount());
        assertEquals(2, qrList.getAllPageCount());
        assertEquals(true, qrList.isExistPrevPage());
        assertEquals(false, qrList.isExistNextPage());
        assertEquals(21, qrList.getCurrentStartRecordNumber());
        assertEquals(21, qrList.getCurrentEndRecordNumber());

        qrList = new QueryResponseList(null, 20, 20, 0) {
            @Override
            public int size() {
                return 20;
            }
        };
        qrList.allRecordCount = 40;
        qrList.calculatePageInfo();
        assertEquals(20, qrList.getPageSize());
        assertEquals(2, qrList.getCurrentPageNumber());
        assertEquals(40, qrList.getAllRecordCount());
        assertEquals(2, qrList.getAllPageCount());
        assertEquals(true, qrList.isExistPrevPage());
        assertEquals(false, qrList.isExistNextPage());
        assertEquals(21, qrList.getCurrentStartRecordNumber());
        assertEquals(40, qrList.getCurrentEndRecordNumber());

        qrList = new QueryResponseList(null, 20, 20, 0) {
            @Override
            public int size() {
                return 20;
            }
        };
        qrList.allRecordCount = 41;
        qrList.calculatePageInfo();
        assertEquals(20, qrList.getPageSize());
        assertEquals(2, qrList.getCurrentPageNumber());
        assertEquals(41, qrList.getAllRecordCount());
        assertEquals(3, qrList.getAllPageCount());
        assertEquals(true, qrList.isExistPrevPage());
        assertEquals(true, qrList.isExistNextPage());
        assertEquals(21, qrList.getCurrentStartRecordNumber());
        assertEquals(40, qrList.getCurrentEndRecordNumber());

        qrList = new QueryResponseList(null, 20, 20, 0) {
            @Override
            public int size() {
                return 20;
            }
        };
        qrList.allRecordCount = 61;
        qrList.calculatePageInfo();
        assertEquals(20, qrList.getPageSize());
        assertEquals(2, qrList.getCurrentPageNumber());
        assertEquals(61, qrList.getAllRecordCount());
        assertEquals(4, qrList.getAllPageCount());
        assertEquals(true, qrList.isExistPrevPage());
        assertEquals(true, qrList.isExistNextPage());
        assertEquals(21, qrList.getCurrentStartRecordNumber());
        assertEquals(40, qrList.getCurrentEndRecordNumber());
    }

    public void test_calculatePageInfo_pageList() {
        QueryResponseList qrList;
        List<String> pnList;

        qrList = new QueryResponseList(null, 0, 20, 0) {
            @Override
            public int size() {
                return 20;
            }
        };
        qrList.allRecordCount = 20;
        qrList.calculatePageInfo();
        pnList = qrList.getPageNumberList();
        assertEquals(1, pnList.size());
        assertEquals("1", pnList.get(0));

        qrList = new QueryResponseList(null, 0, 20, 0) {
            @Override
            public int size() {
                return 20;
            }
        };
        qrList.allRecordCount = 61;
        qrList.calculatePageInfo();
        pnList = qrList.getPageNumberList();
        assertEquals(4, pnList.size());
        assertEquals("1", pnList.get(0));
        assertEquals("2", pnList.get(1));
        assertEquals("3", pnList.get(2));
        assertEquals("4", pnList.get(3));

        qrList = new QueryResponseList(null, 0, 20, 0) {
            @Override
            public int size() {
                return 20;
            }
        };
        qrList.allRecordCount = 200;
        qrList.calculatePageInfo();
        pnList = qrList.getPageNumberList();
        assertEquals(6, pnList.size());
        assertEquals("1", pnList.get(0));
        assertEquals("2", pnList.get(1));
        assertEquals("3", pnList.get(2));
        assertEquals("4", pnList.get(3));
        assertEquals("5", pnList.get(4));
        assertEquals("6", pnList.get(5));

        qrList = new QueryResponseList(null, 20, 20, 0) {
            @Override
            public int size() {
                return 1;
            }
        };
        qrList.allRecordCount = 21;
        qrList.calculatePageInfo();
        pnList = qrList.getPageNumberList();
        assertEquals(2, pnList.size());
        assertEquals("1", pnList.get(0));
        assertEquals("2", pnList.get(1));

        qrList = new QueryResponseList(null, 20, 20, 0) {
            @Override
            public int size() {
                return 20;
            }
        };
        qrList.allRecordCount = 61;
        qrList.calculatePageInfo();
        pnList = qrList.getPageNumberList();
        assertEquals(4, pnList.size());
        assertEquals("1", pnList.get(0));
        assertEquals("2", pnList.get(1));
        assertEquals("3", pnList.get(2));
        assertEquals("4", pnList.get(3));

        qrList = new QueryResponseList(null, 20, 20, 0) {
            @Override
            public int size() {
                return 20;
            }
        };
        qrList.allRecordCount = 200;
        qrList.calculatePageInfo();
        pnList = qrList.getPageNumberList();
        assertEquals(7, pnList.size());
        assertEquals("1", pnList.get(0));
        assertEquals("2", pnList.get(1));
        assertEquals("3", pnList.get(2));
        assertEquals("4", pnList.get(3));
        assertEquals("5", pnList.get(4));
        assertEquals("6", pnList.get(5));
        assertEquals("7", pnList.get(6));

    }

    public void test_calculatePageInfo_collapse() {
        QueryResponseList qrList;

        qrList = new QueryResponseList(null, 0, 20, 0) {
            @Override
            public int size() {
                return 0;
            }
        };
        qrList.calculatePageInfo();
        assertEquals(20, qrList.getPageSize());
        assertEquals(1, qrList.getCurrentPageNumber());
        assertEquals(0, qrList.getAllRecordCount());
        assertEquals(1, qrList.getAllPageCount());
        assertEquals(false, qrList.isExistPrevPage());
        assertEquals(false, qrList.isExistNextPage());
        assertEquals(0, qrList.getCurrentStartRecordNumber());
        assertEquals(0, qrList.getCurrentEndRecordNumber());

        qrList = new QueryResponseList(null, 0, 20, 0) {
            @Override
            public int size() {
                return 10;
            }
        };
        qrList.allRecordCount = 20;
        qrList.calculatePageInfo();
        assertEquals(20, qrList.getPageSize());
        assertEquals(1, qrList.getCurrentPageNumber());
        assertEquals(20, qrList.getAllRecordCount());
        assertEquals(1, qrList.getAllPageCount());
        assertEquals(false, qrList.isExistPrevPage());
        assertEquals(false, qrList.isExistNextPage());
        assertEquals(1, qrList.getCurrentStartRecordNumber());
        assertEquals(20, qrList.getCurrentEndRecordNumber());

        qrList = new QueryResponseList(null, 0, 20, 0) {
            @Override
            public int size() {
                return 10;
            }
        };
        qrList.allRecordCount = 21;
        qrList.calculatePageInfo();
        assertEquals(20, qrList.getPageSize());
        assertEquals(1, qrList.getCurrentPageNumber());
        assertEquals(21, qrList.getAllRecordCount());
        assertEquals(1, qrList.getAllPageCount());
        assertEquals(false, qrList.isExistPrevPage());
        assertEquals(false, qrList.isExistNextPage());
        assertEquals(1, qrList.getCurrentStartRecordNumber());
        assertEquals(20, qrList.getCurrentEndRecordNumber());

        qrList = new QueryResponseList(null, 0, 20, 0) {
            @Override
            public int size() {
                return 21;
            }
        };
        qrList.allRecordCount = 41;
        qrList.calculatePageInfo();
        assertEquals(20, qrList.getPageSize());
        assertEquals(1, qrList.getCurrentPageNumber());
        assertEquals(41, qrList.getAllRecordCount());
        assertEquals(3, qrList.getAllPageCount());
        assertEquals(false, qrList.isExistPrevPage());
        assertEquals(true, qrList.isExistNextPage());
        assertEquals(1, qrList.getCurrentStartRecordNumber());
        assertEquals(20, qrList.getCurrentEndRecordNumber());

        qrList = new QueryResponseList(null, 20, 20, 0) {
            @Override
            public int size() {
                return 1;
            }
        };
        qrList.allRecordCount = 41;
        qrList.calculatePageInfo();
        assertEquals(20, qrList.getPageSize());
        assertEquals(2, qrList.getCurrentPageNumber());
        assertEquals(41, qrList.getAllRecordCount());
        assertEquals(2, qrList.getAllPageCount());
        assertEquals(true, qrList.isExistPrevPage());
        assertEquals(false, qrList.isExistNextPage());
        assertEquals(21, qrList.getCurrentStartRecordNumber());
        assertEquals(40, qrList.getCurrentEndRecordNumber());
    }
}
