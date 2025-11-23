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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.TotalHits.Relation;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.FacetResponse;

/**
 * Tests for SearchResult class and its builder pattern.
 */
public class SearchResultTest extends UnitFessTestCase {

    /**
     * Test basic SearchResult creation with builder.
     */
    public void test_basicSearchResultCreation() {
        final SearchResult result = SearchResult.create()
                .allRecordCount(100)
                .allRecordCountRelation(Relation.EQUAL_TO.toString())
                .queryTime(123)
                .partialResults(false)
                .build();

        assertNotNull(result);
        assertEquals(100, result.getAllRecordCount());
        assertEquals(Relation.EQUAL_TO.toString(), result.getAllRecordCountRelation());
        assertEquals(123, result.getQueryTime());
        assertFalse(result.isPartialResults());
        assertNull(result.getFacetResponse());
        assertNotNull(result.getDocumentList());
        assertTrue(result.getDocumentList().isEmpty());
    }

    /**
     * Test SearchResult with documents.
     */
    public void test_searchResultWithDocuments() {
        final Map<String, Object> doc1 = new HashMap<>();
        doc1.put("_id", "doc1");
        doc1.put("title", "Document 1");

        final Map<String, Object> doc2 = new HashMap<>();
        doc2.put("_id", "doc2");
        doc2.put("title", "Document 2");

        final SearchResult result = SearchResult.create().addDocument(doc1).addDocument(doc2).allRecordCount(2).build();

        assertNotNull(result);
        assertEquals(2, result.getDocumentList().size());
        assertEquals("doc1", result.getDocumentList().get(0).get("_id"));
        assertEquals("doc2", result.getDocumentList().get(1).get("_id"));
    }

    /**
     * Test SearchResult with partial results flag.
     */
    public void test_searchResultWithPartialResults() {
        final SearchResult result = SearchResult.create().allRecordCount(1000).partialResults(true).build();

        assertTrue(result.isPartialResults());
    }

    /**
     * Test SearchResult with facet response.
     */
    public void test_searchResultWithFacetResponse() {
        final FacetResponse facetResponse = new FacetResponse(null);
        final SearchResult result = SearchResult.create().allRecordCount(100).facetResponse(facetResponse).build();

        assertNotNull(result.getFacetResponse());
        assertEquals(facetResponse, result.getFacetResponse());
    }

    /**
     * Test SearchResult default relation is GREATER_THAN_OR_EQUAL_TO.
     */
    public void test_defaultRelation() {
        final SearchResult result = SearchResult.create().allRecordCount(100).build();

        assertEquals(Relation.GREATER_THAN_OR_EQUAL_TO.toString(), result.getAllRecordCountRelation());
    }

    /**
     * Test SearchResult with zero record count.
     */
    public void test_zeroRecordCount() {
        final SearchResult result = SearchResult.create().allRecordCount(0).build();

        assertEquals(0, result.getAllRecordCount());
        assertTrue(result.getDocumentList().isEmpty());
    }

    /**
     * Test SearchResult toString method.
     */
    public void test_toString() {
        final SearchResult result = SearchResult.create().allRecordCount(100).queryTime(50).build();

        final String str = result.toString();
        assertNotNull(str);
        assertTrue(str.contains("allRecordCount=100"));
        assertTrue(str.contains("queryTime=50"));
    }

    /**
     * Test SearchResult with all fields populated.
     */
    public void test_allFieldsPopulated() {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("_id", "doc1");

        final FacetResponse facetResponse = new FacetResponse(null);

        final SearchResult result = SearchResult.create()
                .addDocument(doc)
                .allRecordCount(1000)
                .allRecordCountRelation(Relation.EQUAL_TO.toString())
                .queryTime(150)
                .partialResults(true)
                .facetResponse(facetResponse)
                .build();

        assertEquals(1, result.getDocumentList().size());
        assertEquals(1000, result.getAllRecordCount());
        assertEquals(Relation.EQUAL_TO.toString(), result.getAllRecordCountRelation());
        assertEquals(150, result.getQueryTime());
        assertTrue(result.isPartialResults());
        assertNotNull(result.getFacetResponse());
    }
}
