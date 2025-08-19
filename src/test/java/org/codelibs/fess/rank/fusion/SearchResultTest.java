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
package org.codelibs.fess.rank.fusion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.TotalHits.Relation;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.FacetResponse;
import org.opensearch.search.aggregations.Aggregations;
import org.opensearch.search.aggregations.InternalAggregations;

public class SearchResultTest extends UnitFessTestCase {

    public void test_constructor() {
        // Test with normal values
        List<Map<String, Object>> documentList = new ArrayList<>();
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("title", "Test Document 1");
        doc1.put("content", "This is test content");
        documentList.add(doc1);

        long allRecordCount = 100L;
        String allRecordCountRelation = "eq";
        long queryTime = 50L;
        boolean partialResults = false;
        Aggregations aggregations = InternalAggregations.EMPTY;
        FacetResponse facetResponse = new FacetResponse(aggregations);

        SearchResult result =
                new SearchResult(documentList, allRecordCount, allRecordCountRelation, queryTime, partialResults, facetResponse);

        assertEquals(documentList, result.getDocumentList());
        assertEquals(allRecordCount, result.getAllRecordCount());
        assertEquals(allRecordCountRelation, result.getAllRecordCountRelation());
        assertEquals(queryTime, result.getQueryTime());
        assertEquals(partialResults, result.isPartialResults());
        assertEquals(facetResponse, result.getFacetResponse());
    }

    public void test_constructor_withNullValues() {
        // Test with null values
        SearchResult result = new SearchResult(null, 0L, null, 0L, true, null);

        assertNull(result.getDocumentList());
        assertEquals(0L, result.getAllRecordCount());
        assertNull(result.getAllRecordCountRelation());
        assertEquals(0L, result.getQueryTime());
        assertTrue(result.isPartialResults());
        assertNull(result.getFacetResponse());
    }

    public void test_constructor_withEmptyList() {
        // Test with empty document list
        List<Map<String, Object>> emptyList = new ArrayList<>();
        SearchResult result = new SearchResult(emptyList, 0L, "eq", 10L, false, null);

        assertNotNull(result.getDocumentList());
        assertTrue(result.getDocumentList().isEmpty());
        assertEquals(0L, result.getAllRecordCount());
        assertEquals("eq", result.getAllRecordCountRelation());
        assertEquals(10L, result.getQueryTime());
        assertFalse(result.isPartialResults());
        assertNull(result.getFacetResponse());
    }

    public void test_builder_basicUsage() {
        // Test basic builder usage
        SearchResult result =
                SearchResult.create().allRecordCount(200L).allRecordCountRelation("gte").queryTime(100L).partialResults(true).build();

        assertNotNull(result.getDocumentList());
        assertTrue(result.getDocumentList().isEmpty());
        assertEquals(200L, result.getAllRecordCount());
        assertEquals("gte", result.getAllRecordCountRelation());
        assertEquals(100L, result.getQueryTime());
        assertTrue(result.isPartialResults());
        assertNull(result.getFacetResponse());
    }

    public void test_builder_withDocuments() {
        // Test builder with adding documents
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("id", "1");
        doc1.put("title", "Document 1");

        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("id", "2");
        doc2.put("title", "Document 2");

        SearchResult result = SearchResult.create().addDocument(doc1).addDocument(doc2).allRecordCount(2L).queryTime(25L).build();

        assertEquals(2, result.getDocumentList().size());
        assertEquals(doc1, result.getDocumentList().get(0));
        assertEquals(doc2, result.getDocumentList().get(1));
        assertEquals(2L, result.getAllRecordCount());
        assertEquals(25L, result.getQueryTime());
    }

    public void test_builder_withFacetResponse() {
        // Test builder with facet response
        Aggregations aggregations = InternalAggregations.EMPTY;
        FacetResponse facetResponse = new FacetResponse(aggregations);

        SearchResult result = SearchResult.create().facetResponse(facetResponse).allRecordCount(50L).build();

        assertEquals(facetResponse, result.getFacetResponse());
        assertEquals(50L, result.getAllRecordCount());
    }

    public void test_builder_defaultValues() {
        // Test builder default values
        SearchResult result = SearchResult.create().build();

        assertNotNull(result.getDocumentList());
        assertTrue(result.getDocumentList().isEmpty());
        assertEquals(0L, result.getAllRecordCount());
        assertEquals(Relation.GREATER_THAN_OR_EQUAL_TO.toString(), result.getAllRecordCountRelation());
        assertEquals(0L, result.getQueryTime());
        assertFalse(result.isPartialResults());
        assertNull(result.getFacetResponse());
    }

    public void test_builder_methodChaining() {
        // Test method chaining in builder
        Map<String, Object> doc = new HashMap<>();
        doc.put("test", "value");
        Aggregations aggregations = InternalAggregations.EMPTY;
        FacetResponse facetResponse = new FacetResponse(aggregations);

        SearchResult result = SearchResult.create()
                .allRecordCount(1000L)
                .allRecordCountRelation("eq")
                .queryTime(150L)
                .partialResults(true)
                .addDocument(doc)
                .facetResponse(facetResponse)
                .build();

        assertEquals(1000L, result.getAllRecordCount());
        assertEquals("eq", result.getAllRecordCountRelation());
        assertEquals(150L, result.getQueryTime());
        assertTrue(result.isPartialResults());
        assertEquals(1, result.getDocumentList().size());
        assertEquals(doc, result.getDocumentList().get(0));
        assertEquals(facetResponse, result.getFacetResponse());
    }

    public void test_builder_multipleDocuments() {
        // Test adding multiple documents to builder
        SearchResult.SearchResultBuilder builder = SearchResult.create();

        for (int i = 0; i < 10; i++) {
            Map<String, Object> doc = new HashMap<>();
            doc.put("id", String.valueOf(i));
            doc.put("index", i);
            builder.addDocument(doc);
        }

        SearchResult result = builder.allRecordCount(10L).build();

        assertEquals(10, result.getDocumentList().size());
        for (int i = 0; i < 10; i++) {
            Map<String, Object> doc = result.getDocumentList().get(i);
            assertEquals(String.valueOf(i), doc.get("id"));
            assertEquals(i, doc.get("index"));
        }
    }

    public void test_toString() {
        // Test toString method
        Map<String, Object> doc = new HashMap<>();
        doc.put("key", "value");
        List<Map<String, Object>> documentList = new ArrayList<>();
        documentList.add(doc);

        Aggregations aggregations = InternalAggregations.EMPTY;
        FacetResponse facetResponse = new FacetResponse(aggregations);

        SearchResult result = new SearchResult(documentList, 100L, "eq", 50L, true, facetResponse);

        String str = result.toString();
        assertNotNull(str);
        assertTrue(str.contains("SearchResult"));
        assertTrue(str.contains("documentList="));
        assertTrue(str.contains("allRecordCount=100"));
        assertTrue(str.contains("allRecordCountRelation=eq"));
        assertTrue(str.contains("queryTime=50"));
        assertTrue(str.contains("partialResults=true"));
        assertTrue(str.contains("facetResponse="));
    }

    public void test_largeRecordCount() {
        // Test with large record count
        long largeCount = Long.MAX_VALUE;
        SearchResult result = SearchResult.create().allRecordCount(largeCount).build();

        assertEquals(largeCount, result.getAllRecordCount());
    }

    public void test_negativeQueryTime() {
        // Test with negative query time (edge case)
        SearchResult result = SearchResult.create().queryTime(-1L).build();

        assertEquals(-1L, result.getQueryTime());
    }

    public void test_documentListImmutability() {
        // Test that document list is not modified after creation
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("id", "1");

        SearchResult.SearchResultBuilder builder = SearchResult.create();
        builder.addDocument(doc1);
        SearchResult result = builder.build();

        // Try to modify the original document
        doc1.put("id", "modified");

        // The document in the result should still have the modified value
        // (shallow copy behavior)
        assertEquals("modified", result.getDocumentList().get(0).get("id"));

        // Document list size should remain the same
        assertEquals(1, result.getDocumentList().size());
    }

    public void test_multipleBuilds() {
        // Test that each builder creates independent results
        // Create first result with one document
        SearchResult.SearchResultBuilder builder1 = SearchResult.create();
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("id", "1");
        builder1.addDocument(doc1);
        builder1.allRecordCount(1L);
        SearchResult result1 = builder1.build();

        // Create second result with two documents using a new builder
        SearchResult.SearchResultBuilder builder2 = SearchResult.create();
        Map<String, Object> doc2a = new HashMap<>();
        doc2a.put("id", "2a");
        builder2.addDocument(doc2a);
        Map<String, Object> doc2b = new HashMap<>();
        doc2b.put("id", "2b");
        builder2.addDocument(doc2b);
        builder2.allRecordCount(2L);
        SearchResult result2 = builder2.build();

        // First result should have 1 document
        assertEquals(1, result1.getDocumentList().size());
        assertEquals(1L, result1.getAllRecordCount());

        // Second result should have 2 documents
        assertEquals(2, result2.getDocumentList().size());
        assertEquals(2L, result2.getAllRecordCount());
    }

    public void test_emptyRelationType() {
        // Test with empty relation type string
        SearchResult result = SearchResult.create().allRecordCountRelation("").build();

        assertEquals("", result.getAllRecordCountRelation());
    }

    public void test_specialCharactersInDocuments() {
        // Test with special characters in document content
        Map<String, Object> doc = new HashMap<>();
        doc.put("special", "!@#$%^&*()_+-=[]{}|;':\",./<>?");
        doc.put("unicode", "こんにちは世界");
        doc.put("emoji", "😀🎉");

        SearchResult result = SearchResult.create().addDocument(doc).build();

        assertEquals(1, result.getDocumentList().size());
        Map<String, Object> retrievedDoc = result.getDocumentList().get(0);
        assertEquals("!@#$%^&*()_+-=[]{}|;':\",./<>?", retrievedDoc.get("special"));
        assertEquals("こんにちは世界", retrievedDoc.get("unicode"));
        assertEquals("😀🎉", retrievedDoc.get("emoji"));
    }

    public void test_nullDocumentInList() {
        // Test adding null document
        SearchResult result = SearchResult.create().addDocument(null).build();

        assertEquals(1, result.getDocumentList().size());
        assertNull(result.getDocumentList().get(0));
    }

    public void test_complexDocumentStructure() {
        // Test with complex nested document structure
        Map<String, Object> doc = new HashMap<>();
        doc.put("id", "complex");

        Map<String, Object> nested = new HashMap<>();
        nested.put("level2", "value");
        doc.put("nested", nested);

        List<String> list = new ArrayList<>();
        list.add("item1");
        list.add("item2");
        doc.put("list", list);

        SearchResult result = SearchResult.create().addDocument(doc).allRecordCount(1L).build();

        assertEquals(1, result.getDocumentList().size());
        Map<String, Object> retrievedDoc = result.getDocumentList().get(0);
        assertEquals("complex", retrievedDoc.get("id"));
        assertTrue(retrievedDoc.get("nested") instanceof Map);
        assertTrue(retrievedDoc.get("list") instanceof List);
    }
}