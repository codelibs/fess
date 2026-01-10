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
package org.codelibs.fess.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class QueryResponseListTest extends UnitFessTestCase {
    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
    public void test_constructor_full() {
        List<Map<String, Object>> documentList = new ArrayList<>();
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("title", "Test Document 1");
        documentList.add(doc1);

        FacetResponse facetResponse = null;

        QueryResponseList qrList = new QueryResponseList(documentList, 100L, "gte", 500L, true, facetResponse, 0, 10, 0);

        assertEquals(documentList, qrList.parent);
        assertEquals(100L, qrList.getAllRecordCount());
        assertEquals("gte", qrList.getAllRecordCountRelation());
        assertEquals(500L, qrList.getQueryTime());
        assertTrue(qrList.isPartialResults());
        assertEquals(facetResponse, qrList.getFacetResponse());
        assertEquals(0, qrList.getStart());
        assertEquals(10, qrList.getPageSize());
        assertEquals(0, qrList.getOffset());

        // Page info should be calculated (allRecordCount=100, pageSize=10 => 10 pages)
        // But since size() < pageSize, collapsing logic applies and allPageCount becomes currentPageNumber
        assertEquals(1, qrList.getAllPageCount()); // Collapsed to current page due to size() = 1 < pageSize = 10
        assertEquals(1, qrList.getCurrentPageNumber());
    }

    @Test
    public void test_constructor_withZeroPageSize() {
        List<Map<String, Object>> documentList = new ArrayList<>();
        FacetResponse facetResponse = null;

        QueryResponseList qrList = new QueryResponseList(documentList, 100L, "eq", 300L, false, facetResponse, 0, 0, 0);

        assertEquals(100L, qrList.getAllRecordCount());
        assertEquals("eq", qrList.getAllRecordCountRelation());
        assertEquals(300L, qrList.getQueryTime());
        assertFalse(qrList.isPartialResults());
        assertEquals(0, qrList.getPageSize());

        // Page info should not be calculated when pageSize is 0
        assertEquals(0, qrList.getAllPageCount());
        assertEquals(0, qrList.getCurrentPageNumber());
    }

    @Test
    public void test_listOperations_add() {
        List<Map<String, Object>> documentList = new ArrayList<>();
        QueryResponseList qrList = new QueryResponseList(documentList, 0, 10, 0);

        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("title", "Test Document");

        assertTrue(qrList.add(doc1));
        assertEquals(1, qrList.size());
        assertEquals(doc1, qrList.get(0));

        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("title", "Test Document 2");
        qrList.add(0, doc2);
        assertEquals(2, qrList.size());
        assertEquals(doc2, qrList.get(0));
        assertEquals(doc1, qrList.get(1));
    }

    @Test
    public void test_listOperations_addAll() {
        List<Map<String, Object>> documentList = new ArrayList<>();
        QueryResponseList qrList = new QueryResponseList(documentList, 0, 10, 0);

        List<Map<String, Object>> newDocs = new ArrayList<>();
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("title", "Doc1");
        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("title", "Doc2");
        newDocs.add(doc1);
        newDocs.add(doc2);

        assertTrue(qrList.addAll(newDocs));
        assertEquals(2, qrList.size());
        assertEquals(doc1, qrList.get(0));
        assertEquals(doc2, qrList.get(1));

        List<Map<String, Object>> moreDocs = new ArrayList<>();
        Map<String, Object> doc3 = new HashMap<>();
        doc3.put("title", "Doc3");
        moreDocs.add(doc3);

        assertTrue(qrList.addAll(1, moreDocs));
        assertEquals(3, qrList.size());
        assertEquals(doc1, qrList.get(0));
        assertEquals(doc3, qrList.get(1));
        assertEquals(doc2, qrList.get(2));
    }

    @Test
    public void test_listOperations_remove() {
        List<Map<String, Object>> documentList = new ArrayList<>();
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("title", "Doc1");
        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("title", "Doc2");
        documentList.add(doc1);
        documentList.add(doc2);

        QueryResponseList qrList = new QueryResponseList(documentList, 0, 10, 0);

        assertEquals(doc1, qrList.remove(0));
        assertEquals(1, qrList.size());
        assertEquals(doc2, qrList.get(0));

        assertTrue(qrList.remove(doc2));
        assertEquals(0, qrList.size());
        assertTrue(qrList.isEmpty());
    }

    @Test
    public void test_listOperations_contains() {
        List<Map<String, Object>> documentList = new ArrayList<>();
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("title", "Doc1");
        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("title", "Doc2");
        documentList.add(doc1);

        QueryResponseList qrList = new QueryResponseList(documentList, 0, 10, 0);

        assertTrue(qrList.contains(doc1));
        assertFalse(qrList.contains(doc2));

        List<Map<String, Object>> checkList = Arrays.asList(doc1);
        assertTrue(qrList.containsAll(checkList));

        checkList = Arrays.asList(doc1, doc2);
        assertFalse(qrList.containsAll(checkList));
    }

    @Test
    public void test_listOperations_indexOperations() {
        List<Map<String, Object>> documentList = new ArrayList<>();
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("title", "Doc1");
        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("title", "Doc2");
        documentList.add(doc1);
        documentList.add(doc2);
        documentList.add(doc1); // duplicate

        QueryResponseList qrList = new QueryResponseList(documentList, 0, 10, 0);

        assertEquals(0, qrList.indexOf(doc1));
        assertEquals(1, qrList.indexOf(doc2));
        assertEquals(2, qrList.lastIndexOf(doc1));

        Map<String, Object> doc3 = new HashMap<>();
        doc3.put("title", "Doc3");
        assertEquals(-1, qrList.indexOf(doc3));
    }

    @Test
    public void test_listOperations_iterators() {
        List<Map<String, Object>> documentList = new ArrayList<>();
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("title", "Doc1");
        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("title", "Doc2");
        documentList.add(doc1);
        documentList.add(doc2);

        QueryResponseList qrList = new QueryResponseList(documentList, 0, 10, 0);

        // Test iterator
        Iterator<Map<String, Object>> iterator = qrList.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(doc1, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(doc2, iterator.next());
        assertFalse(iterator.hasNext());

        // Test listIterator
        ListIterator<Map<String, Object>> listIterator = qrList.listIterator();
        assertTrue(listIterator.hasNext());
        assertEquals(doc1, listIterator.next());

        ListIterator<Map<String, Object>> listIterator1 = qrList.listIterator(1);
        assertTrue(listIterator1.hasNext());
        assertEquals(doc2, listIterator1.next());
    }

    @Test
    public void test_listOperations_setAndClear() {
        List<Map<String, Object>> documentList = new ArrayList<>();
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("title", "Doc1");
        documentList.add(doc1);

        QueryResponseList qrList = new QueryResponseList(documentList, 0, 10, 0);

        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("title", "Doc2");

        assertEquals(doc1, qrList.set(0, doc2));
        assertEquals(doc2, qrList.get(0));

        qrList.clear();
        assertEquals(0, qrList.size());
        assertTrue(qrList.isEmpty());
    }

    @Test
    public void test_listOperations_subList() {
        List<Map<String, Object>> documentList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Map<String, Object> doc = new HashMap<>();
            doc.put("title", "Doc" + i);
            documentList.add(doc);
        }

        QueryResponseList qrList = new QueryResponseList(documentList, 0, 10, 0);

        List<Map<String, Object>> subList = qrList.subList(1, 4);
        assertEquals(3, subList.size());
        assertEquals("Doc1", subList.get(0).get("title"));
        assertEquals("Doc2", subList.get(1).get("title"));
        assertEquals("Doc3", subList.get(2).get("title"));
    }

    @Test
    public void test_listOperations_toArray() {
        List<Map<String, Object>> documentList = new ArrayList<>();
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("title", "Doc1");
        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("title", "Doc2");
        documentList.add(doc1);
        documentList.add(doc2);

        QueryResponseList qrList = new QueryResponseList(documentList, 0, 10, 0);

        Object[] array = qrList.toArray();
        assertEquals(2, array.length);
        assertEquals(doc1, array[0]);
        assertEquals(doc2, array[1]);

        @SuppressWarnings("unchecked")
        Map<String, Object>[] typedArray = qrList.toArray(new Map[0]);
        assertEquals(2, typedArray.length);
        assertEquals(doc1, typedArray[0]);
        assertEquals(doc2, typedArray[1]);
    }

    @Test
    public void test_listOperations_removeAll_retainAll() {
        List<Map<String, Object>> documentList = new ArrayList<>();
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("title", "Doc1");
        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("title", "Doc2");
        Map<String, Object> doc3 = new HashMap<>();
        doc3.put("title", "Doc3");
        documentList.add(doc1);
        documentList.add(doc2);
        documentList.add(doc3);

        QueryResponseList qrList = new QueryResponseList(documentList, 0, 10, 0);

        Collection<Map<String, Object>> toRemove = Arrays.asList(doc2);
        assertTrue(qrList.removeAll(toRemove));
        assertEquals(2, qrList.size());
        assertFalse(qrList.contains(doc2));

        Collection<Map<String, Object>> toRetain = Arrays.asList(doc1);
        assertTrue(qrList.retainAll(toRetain));
        assertEquals(1, qrList.size());
        assertTrue(qrList.contains(doc1));
        assertFalse(qrList.contains(doc3));
    }

    @Test
    public void test_equalsAndHashCode() {
        List<Map<String, Object>> documentList1 = new ArrayList<>();
        List<Map<String, Object>> documentList2 = new ArrayList<>();
        Map<String, Object> doc = new HashMap<>();
        doc.put("title", "Test");
        documentList1.add(doc);
        documentList2.add(doc);

        QueryResponseList qrList1 = new QueryResponseList(documentList1, 0, 10, 0);
        QueryResponseList qrList2 = new QueryResponseList(documentList2, 0, 10, 0);

        assertTrue(qrList1.equals(qrList2));
        assertEquals(qrList1.hashCode(), qrList2.hashCode());

        // Test with different content
        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("title", "Different");
        documentList2.add(doc2);

        assertFalse(qrList1.equals(qrList2));
    }

    @Test
    public void test_gettersAndSetters() {
        List<Map<String, Object>> documentList = new ArrayList<>();
        FacetResponse facetResponse = null;

        QueryResponseList qrList = new QueryResponseList(documentList, 100L, "gte", 500L, true, facetResponse, 20, 10, 5);

        // Test getters
        assertEquals(20, qrList.getStart());
        assertEquals(5, qrList.getOffset());
        assertEquals(10, qrList.getPageSize());
        assertEquals(100L, qrList.getAllRecordCount());
        assertEquals("gte", qrList.getAllRecordCountRelation());
        assertEquals(500L, qrList.getQueryTime());
        assertTrue(qrList.isPartialResults());
        assertEquals(facetResponse, qrList.getFacetResponse());

        // Test setters
        qrList.setSearchQuery("test query");
        assertEquals("test query", qrList.getSearchQuery());

        qrList.setExecTime(1000L);
        assertEquals(1000L, qrList.getExecTime());
    }

    @Test
    public void test_calculatePageInfo_withOffset() {
        QueryResponseList qrList = new QueryResponseList(null, 10, 20, 5) {
            @Override
            public int size() {
                return 20;
            }
        };
        qrList.allRecordCount = 100;
        qrList.calculatePageInfo();

        // start=10, offset=5, so startWithOffset = 10-5 = 5
        // currentPageNumber = start / pageSize + 1 = 10 / 20 + 1 = 1
        // existPrevPage = startWithOffset > 0 = 5 > 0 = true
        assertEquals(1, qrList.getCurrentPageNumber());
        assertTrue(qrList.isExistNextPage());
        assertTrue(qrList.isExistPrevPage());
        assertEquals(11, qrList.getCurrentStartRecordNumber());
        assertEquals(30, qrList.getCurrentEndRecordNumber());
    }

    @Test
    public void test_calculatePageInfo_withNegativeOffset() {
        QueryResponseList qrList = new QueryResponseList(null, 5, 20, 10) {
            @Override
            public int size() {
                return 20;
            }
        };
        qrList.allRecordCount = 100;
        qrList.calculatePageInfo();

        // start=5, offset=10, so startWithOffset = 5-10 = -5, should be adjusted to 0
        assertEquals(1, qrList.getCurrentPageNumber());
        assertTrue(qrList.isExistNextPage());
        assertFalse(qrList.isExistPrevPage());
    }

    @Test
    public void test_calculatePageInfo_pageNumberList_edgeCases() {
        // Test when current page is at the beginning
        QueryResponseList qrList = new QueryResponseList(null, 0, 10, 0) {
            @Override
            public int size() {
                return 10;
            }
        };
        qrList.allRecordCount = 200; // 20 pages
        qrList.calculatePageInfo();

        List<String> pageList = qrList.getPageNumberList();
        assertEquals(6, pageList.size()); // Pages 1-6 (current page 1 + range 5)
        assertEquals("1", pageList.get(0));
        assertEquals("6", pageList.get(5));

        // Test when current page is at the end
        qrList = new QueryResponseList(null, 190, 10, 0) {
            @Override
            public int size() {
                return 10;
            }
        };
        qrList.allRecordCount = 200; // 20 pages, current page 20
        qrList.calculatePageInfo();

        pageList = qrList.getPageNumberList();
        assertEquals(6, pageList.size()); // Pages 15-20 (current page 20 - range 5)
        assertEquals("15", pageList.get(0));
        assertEquals("20", pageList.get(5));

        // Test when current page is in the middle
        qrList = new QueryResponseList(null, 100, 10, 0) {
            @Override
            public int size() {
                return 10;
            }
        };
        qrList.allRecordCount = 200; // 20 pages, current page 11
        qrList.calculatePageInfo();

        pageList = qrList.getPageNumberList();
        assertEquals(11, pageList.size()); // Pages 6-16 (current page 11 ± range 5)
        assertEquals("6", pageList.get(0));
        assertEquals("16", pageList.get(10));
    }

    @Test
    public void test_toString() {
        List<Map<String, Object>> documentList = new ArrayList<>();
        Map<String, Object> doc = new HashMap<>();
        doc.put("title", "Test");
        documentList.add(doc);

        QueryResponseList qrList = new QueryResponseList(documentList, 100L, "eq", 500L, false, null, 0, 10, 0);
        qrList.setSearchQuery("test query");
        qrList.setExecTime(1000L);

        String toStringResult = qrList.toString();
        assertTrue(toStringResult.contains("QueryResponseList"));
        assertTrue(toStringResult.contains("start=0"));
        assertTrue(toStringResult.contains("pageSize=10"));
        assertTrue(toStringResult.contains("allRecordCount=100"));
        assertTrue(toStringResult.contains("allRecordCountRelation=eq"));
        assertTrue(toStringResult.contains("searchQuery=test query"));
        assertTrue(toStringResult.contains("execTime=1000"));
        assertTrue(toStringResult.contains("queryTime=500"));
        assertTrue(toStringResult.contains("partialResults=false"));
    }

    @Test
    public void test_calculatePageInfo_extremeValues() {
        // Test with very large record count
        QueryResponseList qrList = new QueryResponseList(null, 0, 100, 0) {
            @Override
            public int size() {
                return 100;
            }
        };
        qrList.allRecordCount = Long.MAX_VALUE;
        qrList.calculatePageInfo();

        assertTrue(qrList.getAllPageCount() > 0);
        assertEquals(1, qrList.getCurrentPageNumber());
        assertEquals(1, qrList.getCurrentStartRecordNumber());
        assertEquals(100, qrList.getCurrentEndRecordNumber());

        // Test with record count 1
        qrList = new QueryResponseList(null, 0, 10, 0) {
            @Override
            public int size() {
                return 1;
            }
        };
        qrList.allRecordCount = 1;
        qrList.calculatePageInfo();

        assertEquals(1, qrList.getAllPageCount());
        assertEquals(1, qrList.getCurrentPageNumber());
        assertEquals(1, qrList.getCurrentStartRecordNumber());
        assertEquals(1, qrList.getCurrentEndRecordNumber());
        assertFalse(qrList.isExistNextPage());
        assertFalse(qrList.isExistPrevPage());
    }

    @Test
    public void test_calculatePageInfo_currentEndRecordNumber_boundary() {
        // Test when currentEndRecordNumber would exceed allRecordCount
        QueryResponseList qrList = new QueryResponseList(null, 95, 10, 0) {
            @Override
            public int size() {
                return 5; // Only 5 records returned
            }
        };
        qrList.allRecordCount = 100; // Total 100 records
        qrList.calculatePageInfo();

        assertEquals(96, qrList.getCurrentStartRecordNumber()); // start + 1
        assertEquals(100, qrList.getCurrentEndRecordNumber()); // Should be capped at allRecordCount

        // Test normal case where currentEndRecordNumber doesn't exceed
        qrList = new QueryResponseList(null, 10, 10, 0) {
            @Override
            public int size() {
                return 10;
            }
        };
        qrList.allRecordCount = 100;
        qrList.calculatePageInfo();

        assertEquals(11, qrList.getCurrentStartRecordNumber());
        assertEquals(20, qrList.getCurrentEndRecordNumber()); // start + pageSize
    }

    @Test
    public void test_constructor_defaultValues() {
        List<Map<String, Object>> documentList = new ArrayList<>();
        QueryResponseList qrList = new QueryResponseList(documentList, 0, 10, 0);

        // Test default values
        assertEquals(0, qrList.getCurrentPageNumber());
        assertEquals(0, qrList.getAllRecordCount());
        assertNull(qrList.getAllRecordCountRelation());
        assertEquals(0, qrList.getAllPageCount());
        assertFalse(qrList.isExistNextPage());
        assertFalse(qrList.isExistPrevPage());
        assertEquals(0, qrList.getCurrentStartRecordNumber());
        assertEquals(0, qrList.getCurrentEndRecordNumber());
        assertNull(qrList.getPageNumberList());
        assertNull(qrList.getSearchQuery());
        assertEquals(0, qrList.getExecTime());
        assertNull(qrList.getFacetResponse());
        assertFalse(qrList.isPartialResults());
        assertEquals(0, qrList.getQueryTime());
    }

    @Test
    public void test_constructor_testingConstructor() {
        List<Map<String, Object>> documentList = new ArrayList<>();
        Map<String, Object> doc = new HashMap<>();
        doc.put("title", "Test");
        documentList.add(doc);

        QueryResponseList qrList = new QueryResponseList(documentList, 5, 10, 2);

        assertEquals(documentList, qrList.parent);
        assertEquals(5, qrList.getStart());
        assertEquals(10, qrList.getPageSize());
        assertEquals(2, qrList.getOffset());
        assertEquals(1, qrList.size());
        assertEquals(doc, qrList.get(0));
    }

    @Test
    public void test_calculatePageInfo_zeroAllRecordCount() {
        QueryResponseList qrList = new QueryResponseList(null, 0, 10, 0) {
            @Override
            public int size() {
                return 0;
            }
        };
        qrList.allRecordCount = 0;
        qrList.calculatePageInfo();

        assertEquals(1, qrList.getAllPageCount());
        assertEquals(1, qrList.getCurrentPageNumber());
        assertEquals(0, qrList.getCurrentStartRecordNumber());
        assertEquals(0, qrList.getCurrentEndRecordNumber());
        assertFalse(qrList.isExistNextPage());
        assertFalse(qrList.isExistPrevPage());
    }

    @Test
    public void test_listOperations_emptyOperations() {
        List<Map<String, Object>> documentList = new ArrayList<>();
        QueryResponseList qrList = new QueryResponseList(documentList, 0, 10, 0);

        // Test operations on empty list
        assertTrue(qrList.isEmpty());
        assertEquals(0, qrList.size());

        assertFalse(qrList.remove(new HashMap<>()));
        assertFalse(qrList.contains(new HashMap<>()));
        assertFalse(qrList.containsAll(Arrays.asList(new HashMap<>())));

        assertEquals(-1, qrList.indexOf(new HashMap<>()));
        assertEquals(-1, qrList.lastIndexOf(new HashMap<>()));

        // Test iterators on empty list
        Iterator<Map<String, Object>> iterator = qrList.iterator();
        assertFalse(iterator.hasNext());

        ListIterator<Map<String, Object>> listIterator = qrList.listIterator();
        assertFalse(listIterator.hasNext());
        assertFalse(qrList.listIterator(0).hasNext());
    }

    @Test
    public void test_listOperations_addAllEmpty() {
        List<Map<String, Object>> documentList = new ArrayList<>();
        QueryResponseList qrList = new QueryResponseList(documentList, 0, 10, 0);

        // Test addAll with empty collection
        assertFalse(qrList.addAll(new ArrayList<>()));
        assertEquals(0, qrList.size());

        assertFalse(qrList.addAll(0, new ArrayList<>()));
        assertEquals(0, qrList.size());
    }

    @Test
    public void test_listOperations_removeAllEmpty() {
        List<Map<String, Object>> documentList = new ArrayList<>();
        Map<String, Object> doc = new HashMap<>();
        doc.put("title", "Test");
        documentList.add(doc);

        QueryResponseList qrList = new QueryResponseList(documentList, 0, 10, 0);

        // Test removeAll and retainAll with empty collection
        assertFalse(qrList.removeAll(new ArrayList<>()));
        assertEquals(1, qrList.size());

        assertTrue(qrList.retainAll(new ArrayList<>()));
        assertEquals(0, qrList.size());
    }

    @Test
    public void test_facetResponse_constructor() {
        FacetResponse facetResponse = null;
        List<Map<String, Object>> documentList = new ArrayList<>();

        QueryResponseList qrList = new QueryResponseList(documentList, 50L, "lte", 200L, false, facetResponse, 10, 20, 0);

        assertEquals(facetResponse, qrList.getFacetResponse());
        assertFalse(qrList.isPartialResults());
        assertEquals("lte", qrList.getAllRecordCountRelation());
    }

    @Test
    public void test_calculatePageInfo_pageNumberList_singlePage() {
        QueryResponseList qrList = new QueryResponseList(null, 0, 50, 0) {
            @Override
            public int size() {
                return 10;
            }
        };
        qrList.allRecordCount = 10;
        qrList.calculatePageInfo();

        List<String> pageList = qrList.getPageNumberList();
        assertEquals(1, pageList.size());
        assertEquals("1", pageList.get(0));
    }

    @Test
    public void test_calculatePageInfo_largePageRange() {
        // Test when we have more than 11 pages (pageRangeSize * 2 + 1)
        QueryResponseList qrList = new QueryResponseList(null, 50, 10, 0) {
            @Override
            public int size() {
                return 10;
            }
        };
        qrList.allRecordCount = 300; // 30 pages, current page 6
        qrList.calculatePageInfo();

        List<String> pageList = qrList.getPageNumberList();
        assertEquals(11, pageList.size()); // Pages 1-11 (page 6 ± 5)
        assertEquals("1", pageList.get(0));
        assertEquals("11", pageList.get(10));
    }

    @Test
    public void test_equals_differentTypes() {
        List<Map<String, Object>> documentList = new ArrayList<>();
        QueryResponseList qrList = new QueryResponseList(documentList, 0, 10, 0);

        assertFalse(qrList.equals(null));
        assertFalse(qrList.equals("string"));
        assertTrue(qrList.equals(documentList)); // QueryResponseList equals delegates to parent list
    }

    @Test
    public void test_listOperations_boundaryIndexes() {
        List<Map<String, Object>> documentList = new ArrayList<>();
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("title", "Doc1");
        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("title", "Doc2");
        documentList.add(doc1);
        documentList.add(doc2);

        QueryResponseList qrList = new QueryResponseList(documentList, 0, 10, 0);

        // Test boundary indexes
        assertEquals(doc1, qrList.get(0));
        assertEquals(doc2, qrList.get(1));

        // Test subList with boundary indexes
        List<Map<String, Object>> subList = qrList.subList(0, 2);
        assertEquals(2, subList.size());

        subList = qrList.subList(1, 1);
        assertEquals(0, subList.size());
    }

    @Test
    public void test_toString_withNullValues() {
        List<Map<String, Object>> documentList = new ArrayList<>();
        QueryResponseList qrList = new QueryResponseList(documentList, 0, 10, 0);
        qrList.allRecordCount = 0;
        qrList.allRecordCountRelation = null;
        qrList.queryTime = 0;
        qrList.partialResults = false;
        qrList.facetResponse = null;

        String toStringResult = qrList.toString();
        assertTrue(toStringResult.contains("QueryResponseList"));
        assertTrue(toStringResult.contains("allRecordCountRelation=null"));
        assertTrue(toStringResult.contains("facetResponse=null"));
        assertTrue(toStringResult.contains("searchQuery=null"));
    }
}
