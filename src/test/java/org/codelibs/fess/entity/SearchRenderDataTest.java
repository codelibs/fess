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
package org.codelibs.fess.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.FacetResponse;
import org.opensearch.search.aggregations.Aggregation;
import org.opensearch.search.aggregations.Aggregations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class SearchRenderDataTest extends UnitFessTestCase {

    private SearchRenderData searchRenderData;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        searchRenderData = new SearchRenderData();
    }

    @Test
    public void test_constructor() {
        assertNotNull(searchRenderData);
        assertNull(searchRenderData.getDocumentItems());
        assertNull(searchRenderData.getFacetResponse());
        assertNull(searchRenderData.getAppendHighlightParams());
        assertNull(searchRenderData.getExecTime());
        assertEquals(0, searchRenderData.getPageSize());
        assertEquals(0, searchRenderData.getCurrentPageNumber());
        assertEquals(0L, searchRenderData.getAllRecordCount());
        assertNull(searchRenderData.getAllRecordCountRelation());
        assertEquals(0, searchRenderData.getAllPageCount());
        assertFalse(searchRenderData.isExistNextPage());
        assertFalse(searchRenderData.isExistPrevPage());
        assertEquals(0L, searchRenderData.getCurrentStartRecordNumber());
        assertEquals(0L, searchRenderData.getCurrentEndRecordNumber());
        assertNull(searchRenderData.getPageNumberList());
        assertFalse(searchRenderData.isPartialResults());
        assertNull(searchRenderData.getSearchQuery());
        assertEquals(0L, searchRenderData.getQueryTime());
        assertEquals(0L, searchRenderData.getRequestedTime());
        assertNull(searchRenderData.getQueryId());
    }

    @Test
    public void test_setAndGetDocumentItems() {
        // Test with null
        searchRenderData.setDocumentItems(null);
        assertNull(searchRenderData.getDocumentItems());

        // Test with empty list
        List<Map<String, Object>> emptyList = new ArrayList<>();
        searchRenderData.setDocumentItems(emptyList);
        assertEquals(emptyList, searchRenderData.getDocumentItems());

        // Test with populated list
        List<Map<String, Object>> documentItems = new ArrayList<>();
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("title", "Test Document 1");
        doc1.put("content", "Test content 1");
        documentItems.add(doc1);

        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("title", "Test Document 2");
        doc2.put("content", "Test content 2");
        documentItems.add(doc2);

        searchRenderData.setDocumentItems(documentItems);
        assertEquals(documentItems, searchRenderData.getDocumentItems());
        assertEquals(2, searchRenderData.getDocumentItems().size());
        assertEquals("Test Document 1", searchRenderData.getDocumentItems().get(0).get("title"));
    }

    @Test
    public void test_setAndGetFacetResponse() {
        // Test with null
        searchRenderData.setFacetResponse(null);
        assertNull(searchRenderData.getFacetResponse());

        // Test with FacetResponse object created from empty Aggregations
        List<Aggregation> aggregationList = new ArrayList<>();
        Aggregations aggregations = new Aggregations(aggregationList);
        FacetResponse facetResponse = new FacetResponse(aggregations);
        searchRenderData.setFacetResponse(facetResponse);
        assertEquals(facetResponse, searchRenderData.getFacetResponse());
        assertNotNull(facetResponse.getQueryCountMap());
        assertTrue(facetResponse.getQueryCountMap().isEmpty());
    }

    @Test
    public void test_setAndGetAppendHighlightParams() {
        // Test with null
        searchRenderData.setAppendHighlightParams(null);
        assertNull(searchRenderData.getAppendHighlightParams());

        // Test with empty string
        searchRenderData.setAppendHighlightParams("");
        assertEquals("", searchRenderData.getAppendHighlightParams());

        // Test with parameter string
        String params = "&highlight=true&fields=title,content";
        searchRenderData.setAppendHighlightParams(params);
        assertEquals(params, searchRenderData.getAppendHighlightParams());
    }

    @Test
    public void test_setAndGetExecTime() {
        // Test with null
        searchRenderData.setExecTime(null);
        assertNull(searchRenderData.getExecTime());

        // Test with empty string
        searchRenderData.setExecTime("");
        assertEquals("", searchRenderData.getExecTime());

        // Test with formatted time string
        searchRenderData.setExecTime("0.123 sec");
        assertEquals("0.123 sec", searchRenderData.getExecTime());
    }

    @Test
    public void test_setAndGetPageSize() {
        // Test with zero
        searchRenderData.setPageSize(0);
        assertEquals(0, searchRenderData.getPageSize());

        // Test with positive values
        searchRenderData.setPageSize(10);
        assertEquals(10, searchRenderData.getPageSize());

        searchRenderData.setPageSize(100);
        assertEquals(100, searchRenderData.getPageSize());

        // Test with negative value
        searchRenderData.setPageSize(-1);
        assertEquals(-1, searchRenderData.getPageSize());
    }

    @Test
    public void test_setAndGetCurrentPageNumber() {
        // Test with zero
        searchRenderData.setCurrentPageNumber(0);
        assertEquals(0, searchRenderData.getCurrentPageNumber());

        // Test with positive values
        searchRenderData.setCurrentPageNumber(1);
        assertEquals(1, searchRenderData.getCurrentPageNumber());

        searchRenderData.setCurrentPageNumber(999);
        assertEquals(999, searchRenderData.getCurrentPageNumber());

        // Test with negative value
        searchRenderData.setCurrentPageNumber(-5);
        assertEquals(-5, searchRenderData.getCurrentPageNumber());
    }

    @Test
    public void test_setAndGetAllRecordCount() {
        // Test with zero
        searchRenderData.setAllRecordCount(0L);
        assertEquals(0L, searchRenderData.getAllRecordCount());

        // Test with positive values
        searchRenderData.setAllRecordCount(100L);
        assertEquals(100L, searchRenderData.getAllRecordCount());

        searchRenderData.setAllRecordCount(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, searchRenderData.getAllRecordCount());

        // Test with negative value
        searchRenderData.setAllRecordCount(-10L);
        assertEquals(-10L, searchRenderData.getAllRecordCount());
    }

    @Test
    public void test_setAndGetAllRecordCountRelation() {
        // Test with null
        searchRenderData.setAllRecordCountRelation(null);
        assertNull(searchRenderData.getAllRecordCountRelation());

        // Test with empty string
        searchRenderData.setAllRecordCountRelation("");
        assertEquals("", searchRenderData.getAllRecordCountRelation());

        // Test with relation types
        searchRenderData.setAllRecordCountRelation("eq");
        assertEquals("eq", searchRenderData.getAllRecordCountRelation());

        searchRenderData.setAllRecordCountRelation("gte");
        assertEquals("gte", searchRenderData.getAllRecordCountRelation());
    }

    @Test
    public void test_setAndGetAllPageCount() {
        // Test with zero
        searchRenderData.setAllPageCount(0);
        assertEquals(0, searchRenderData.getAllPageCount());

        // Test with positive values
        searchRenderData.setAllPageCount(5);
        assertEquals(5, searchRenderData.getAllPageCount());

        searchRenderData.setAllPageCount(1000);
        assertEquals(1000, searchRenderData.getAllPageCount());

        // Test with negative value
        searchRenderData.setAllPageCount(-2);
        assertEquals(-2, searchRenderData.getAllPageCount());
    }

    @Test
    public void test_setAndIsExistNextPage() {
        // Test with false
        searchRenderData.setExistNextPage(false);
        assertFalse(searchRenderData.isExistNextPage());

        // Test with true
        searchRenderData.setExistNextPage(true);
        assertTrue(searchRenderData.isExistNextPage());

        // Toggle back to false
        searchRenderData.setExistNextPage(false);
        assertFalse(searchRenderData.isExistNextPage());
    }

    @Test
    public void test_setAndIsExistPrevPage() {
        // Test with false
        searchRenderData.setExistPrevPage(false);
        assertFalse(searchRenderData.isExistPrevPage());

        // Test with true
        searchRenderData.setExistPrevPage(true);
        assertTrue(searchRenderData.isExistPrevPage());

        // Toggle back to false
        searchRenderData.setExistPrevPage(false);
        assertFalse(searchRenderData.isExistPrevPage());
    }

    @Test
    public void test_setAndGetCurrentStartRecordNumber() {
        // Test with zero
        searchRenderData.setCurrentStartRecordNumber(0L);
        assertEquals(0L, searchRenderData.getCurrentStartRecordNumber());

        // Test with positive values
        searchRenderData.setCurrentStartRecordNumber(1L);
        assertEquals(1L, searchRenderData.getCurrentStartRecordNumber());

        searchRenderData.setCurrentStartRecordNumber(9999L);
        assertEquals(9999L, searchRenderData.getCurrentStartRecordNumber());

        // Test with negative value
        searchRenderData.setCurrentStartRecordNumber(-1L);
        assertEquals(-1L, searchRenderData.getCurrentStartRecordNumber());
    }

    @Test
    public void test_setAndGetCurrentEndRecordNumber() {
        // Test with zero
        searchRenderData.setCurrentEndRecordNumber(0L);
        assertEquals(0L, searchRenderData.getCurrentEndRecordNumber());

        // Test with positive values
        searchRenderData.setCurrentEndRecordNumber(10L);
        assertEquals(10L, searchRenderData.getCurrentEndRecordNumber());

        searchRenderData.setCurrentEndRecordNumber(10000L);
        assertEquals(10000L, searchRenderData.getCurrentEndRecordNumber());

        // Test with negative value
        searchRenderData.setCurrentEndRecordNumber(-100L);
        assertEquals(-100L, searchRenderData.getCurrentEndRecordNumber());
    }

    @Test
    public void test_setAndGetPageNumberList() {
        // Test with null
        searchRenderData.setPageNumberList(null);
        assertNull(searchRenderData.getPageNumberList());

        // Test with empty list
        List<String> emptyList = new ArrayList<>();
        searchRenderData.setPageNumberList(emptyList);
        assertEquals(emptyList, searchRenderData.getPageNumberList());
        assertTrue(searchRenderData.getPageNumberList().isEmpty());

        // Test with populated list
        List<String> pageNumbers = Arrays.asList("1", "2", "3", "4", "5");
        searchRenderData.setPageNumberList(pageNumbers);
        assertEquals(pageNumbers, searchRenderData.getPageNumberList());
        assertEquals(5, searchRenderData.getPageNumberList().size());
        assertEquals("1", searchRenderData.getPageNumberList().get(0));
        assertEquals("5", searchRenderData.getPageNumberList().get(4));

        // Test with list containing special values
        List<String> specialPages = Arrays.asList("...", "10", "11", "12", "...");
        searchRenderData.setPageNumberList(specialPages);
        assertEquals(specialPages, searchRenderData.getPageNumberList());
    }

    @Test
    public void test_setAndIsPartialResults() {
        // Test with false
        searchRenderData.setPartialResults(false);
        assertFalse(searchRenderData.isPartialResults());

        // Test with true
        searchRenderData.setPartialResults(true);
        assertTrue(searchRenderData.isPartialResults());

        // Toggle back to false
        searchRenderData.setPartialResults(false);
        assertFalse(searchRenderData.isPartialResults());
    }

    @Test
    public void test_setAndGetQueryTime() {
        // Test with zero
        searchRenderData.setQueryTime(0L);
        assertEquals(0L, searchRenderData.getQueryTime());

        // Test with positive values
        searchRenderData.setQueryTime(123L);
        assertEquals(123L, searchRenderData.getQueryTime());

        searchRenderData.setQueryTime(999999L);
        assertEquals(999999L, searchRenderData.getQueryTime());

        // Test with negative value
        searchRenderData.setQueryTime(-50L);
        assertEquals(-50L, searchRenderData.getQueryTime());
    }

    @Test
    public void test_setAndGetSearchQuery() {
        // Test with null
        searchRenderData.setSearchQuery(null);
        assertNull(searchRenderData.getSearchQuery());

        // Test with empty string
        searchRenderData.setSearchQuery("");
        assertEquals("", searchRenderData.getSearchQuery());

        // Test with simple query
        searchRenderData.setSearchQuery("test query");
        assertEquals("test query", searchRenderData.getSearchQuery());

        // Test with complex query
        String complexQuery = "title:\"test document\" AND content:search OR category:technology";
        searchRenderData.setSearchQuery(complexQuery);
        assertEquals(complexQuery, searchRenderData.getSearchQuery());

        // Test with special characters
        String specialQuery = "test + query - exclude \"exact phrase\" *wildcard?";
        searchRenderData.setSearchQuery(specialQuery);
        assertEquals(specialQuery, searchRenderData.getSearchQuery());
    }

    @Test
    public void test_setAndGetRequestedTime() {
        // Test with zero
        searchRenderData.setRequestedTime(0L);
        assertEquals(0L, searchRenderData.getRequestedTime());

        // Test with current time
        long currentTime = System.currentTimeMillis();
        searchRenderData.setRequestedTime(currentTime);
        assertEquals(currentTime, searchRenderData.getRequestedTime());

        // Test with specific timestamp
        long timestamp = 1609459200000L; // 2021-01-01 00:00:00 UTC
        searchRenderData.setRequestedTime(timestamp);
        assertEquals(timestamp, searchRenderData.getRequestedTime());

        // Test with negative value
        searchRenderData.setRequestedTime(-1000L);
        assertEquals(-1000L, searchRenderData.getRequestedTime());
    }

    @Test
    public void test_setAndGetQueryId() {
        // Test with null
        searchRenderData.setQueryId(null);
        assertNull(searchRenderData.getQueryId());

        // Test with empty string
        searchRenderData.setQueryId("");
        assertEquals("", searchRenderData.getQueryId());

        // Test with UUID-like string
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        searchRenderData.setQueryId(uuid);
        assertEquals(uuid, searchRenderData.getQueryId());

        // Test with simple ID
        searchRenderData.setQueryId("query-001");
        assertEquals("query-001", searchRenderData.getQueryId());
    }

    @Test
    public void test_toString() {
        // Test with default values
        String defaultString = searchRenderData.toString();
        assertNotNull(defaultString);
        assertTrue(defaultString.startsWith("SearchRenderData ["));
        assertTrue(defaultString.endsWith("]"));
        assertTrue(defaultString.contains("documentItems=null"));
        assertTrue(defaultString.contains("facetResponse=null"));
        assertTrue(defaultString.contains("pageSize=0"));

        // Test with populated values
        List<Map<String, Object>> docs = new ArrayList<>();
        Map<String, Object> doc = new HashMap<>();
        doc.put("title", "Test");
        docs.add(doc);
        searchRenderData.setDocumentItems(docs);
        searchRenderData.setPageSize(20);
        searchRenderData.setCurrentPageNumber(2);
        searchRenderData.setAllRecordCount(100L);
        searchRenderData.setExecTime("0.5 sec");
        searchRenderData.setSearchQuery("test query");
        searchRenderData.setQueryId("test-id");
        searchRenderData.setExistNextPage(true);
        searchRenderData.setExistPrevPage(true);
        searchRenderData.setPartialResults(false);
        searchRenderData.setQueryTime(500L);
        searchRenderData.setRequestedTime(1000000L);

        String populatedString = searchRenderData.toString();
        assertNotNull(populatedString);
        assertTrue(populatedString.contains("documentItems=[{title=Test}]"));
        assertTrue(populatedString.contains("pageSize=20"));
        assertTrue(populatedString.contains("currentPageNumber=2"));
        assertTrue(populatedString.contains("allRecordCount=100"));
        assertTrue(populatedString.contains("execTime=0.5 sec"));
        assertTrue(populatedString.contains("searchQuery=test query"));
        assertTrue(populatedString.contains("queryId=test-id"));
        assertTrue(populatedString.contains("existNextPage=true"));
        assertTrue(populatedString.contains("existPrevPage=true"));
        assertTrue(populatedString.contains("partialResults=false"));
        assertTrue(populatedString.contains("queryTime=500"));
        assertTrue(populatedString.contains("requestedTime=1000000"));
    }

    @Test
    public void test_complexScenario() {
        // Simulate a typical search result scenario
        // Page 2 of search results with 10 items per page
        searchRenderData.setPageSize(10);
        searchRenderData.setCurrentPageNumber(2);
        searchRenderData.setAllRecordCount(45L);
        searchRenderData.setAllRecordCountRelation("eq");
        searchRenderData.setAllPageCount(5);
        searchRenderData.setExistNextPage(true);
        searchRenderData.setExistPrevPage(true);
        searchRenderData.setCurrentStartRecordNumber(11L);
        searchRenderData.setCurrentEndRecordNumber(20L);

        List<String> pageNumbers = Arrays.asList("1", "2", "3", "4", "5");
        searchRenderData.setPageNumberList(pageNumbers);

        List<Map<String, Object>> documents = new ArrayList<>();
        for (int i = 11; i <= 20; i++) {
            Map<String, Object> doc = new HashMap<>();
            doc.put("id", String.valueOf(i));
            doc.put("title", "Document " + i);
            doc.put("score", 1.0 - (i * 0.01));
            documents.add(doc);
        }
        searchRenderData.setDocumentItems(documents);

        searchRenderData.setSearchQuery("complex search query");
        searchRenderData.setQueryTime(150L);
        searchRenderData.setExecTime("0.15 sec");
        searchRenderData.setRequestedTime(System.currentTimeMillis());
        searchRenderData.setQueryId("complex-query-id");
        searchRenderData.setAppendHighlightParams("&hl=true&hl.fl=title,content");
        searchRenderData.setPartialResults(false);

        // Verify the complex scenario
        assertEquals(10, searchRenderData.getPageSize());
        assertEquals(2, searchRenderData.getCurrentPageNumber());
        assertEquals(45L, searchRenderData.getAllRecordCount());
        assertEquals("eq", searchRenderData.getAllRecordCountRelation());
        assertEquals(5, searchRenderData.getAllPageCount());
        assertTrue(searchRenderData.isExistNextPage());
        assertTrue(searchRenderData.isExistPrevPage());
        assertEquals(11L, searchRenderData.getCurrentStartRecordNumber());
        assertEquals(20L, searchRenderData.getCurrentEndRecordNumber());
        assertEquals(5, searchRenderData.getPageNumberList().size());
        assertEquals(10, searchRenderData.getDocumentItems().size());
        assertEquals("Document 11", searchRenderData.getDocumentItems().get(0).get("title"));
        assertEquals("Document 20", searchRenderData.getDocumentItems().get(9).get("title"));
        assertEquals("complex search query", searchRenderData.getSearchQuery());
        assertEquals(150L, searchRenderData.getQueryTime());
        assertEquals("0.15 sec", searchRenderData.getExecTime());
        assertEquals("complex-query-id", searchRenderData.getQueryId());
        assertFalse(searchRenderData.isPartialResults());
    }

    @Test
    public void test_edgeCases() {
        // Test with maximum values
        searchRenderData.setAllRecordCount(Long.MAX_VALUE);
        searchRenderData.setCurrentStartRecordNumber(Long.MAX_VALUE - 10);
        searchRenderData.setCurrentEndRecordNumber(Long.MAX_VALUE);
        searchRenderData.setPageSize(Integer.MAX_VALUE);
        searchRenderData.setAllPageCount(Integer.MAX_VALUE);
        searchRenderData.setCurrentPageNumber(Integer.MAX_VALUE);

        assertEquals(Long.MAX_VALUE, searchRenderData.getAllRecordCount());
        assertEquals(Long.MAX_VALUE - 10, searchRenderData.getCurrentStartRecordNumber());
        assertEquals(Long.MAX_VALUE, searchRenderData.getCurrentEndRecordNumber());
        assertEquals(Integer.MAX_VALUE, searchRenderData.getPageSize());
        assertEquals(Integer.MAX_VALUE, searchRenderData.getAllPageCount());
        assertEquals(Integer.MAX_VALUE, searchRenderData.getCurrentPageNumber());

        // Test with minimum values
        searchRenderData.setAllRecordCount(Long.MIN_VALUE);
        searchRenderData.setCurrentStartRecordNumber(Long.MIN_VALUE);
        searchRenderData.setCurrentEndRecordNumber(Long.MIN_VALUE + 10);
        searchRenderData.setPageSize(Integer.MIN_VALUE);
        searchRenderData.setAllPageCount(Integer.MIN_VALUE);
        searchRenderData.setCurrentPageNumber(Integer.MIN_VALUE);

        assertEquals(Long.MIN_VALUE, searchRenderData.getAllRecordCount());
        assertEquals(Long.MIN_VALUE, searchRenderData.getCurrentStartRecordNumber());
        assertEquals(Long.MIN_VALUE + 10, searchRenderData.getCurrentEndRecordNumber());
        assertEquals(Integer.MIN_VALUE, searchRenderData.getPageSize());
        assertEquals(Integer.MIN_VALUE, searchRenderData.getAllPageCount());
        assertEquals(Integer.MIN_VALUE, searchRenderData.getCurrentPageNumber());
    }

    @Test
    public void test_nullSafety() {
        // Ensure all getters return appropriate values when setters haven't been called
        SearchRenderData freshData = new SearchRenderData();

        // These should return null
        assertNull(freshData.getDocumentItems());
        assertNull(freshData.getFacetResponse());
        assertNull(freshData.getAppendHighlightParams());
        assertNull(freshData.getExecTime());
        assertNull(freshData.getAllRecordCountRelation());
        assertNull(freshData.getPageNumberList());
        assertNull(freshData.getSearchQuery());
        assertNull(freshData.getQueryId());

        // These should return default primitive values
        assertEquals(0, freshData.getPageSize());
        assertEquals(0, freshData.getCurrentPageNumber());
        assertEquals(0L, freshData.getAllRecordCount());
        assertEquals(0, freshData.getAllPageCount());
        assertFalse(freshData.isExistNextPage());
        assertFalse(freshData.isExistPrevPage());
        assertEquals(0L, freshData.getCurrentStartRecordNumber());
        assertEquals(0L, freshData.getCurrentEndRecordNumber());
        assertFalse(freshData.isPartialResults());
        assertEquals(0L, freshData.getQueryTime());
        assertEquals(0L, freshData.getRequestedTime());

        // toString should not throw exception
        assertNotNull(freshData.toString());
    }
}