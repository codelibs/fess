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
package org.codelibs.fess.llm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class RelevanceEvaluationResultTest extends UnitFessTestCase {

    @Test
    public void test_withRelevantDocs() {
        List<String> docIds = Arrays.asList("doc1", "doc2", "doc3");
        List<Integer> indexes = Arrays.asList(1, 2, 3);
        RelevanceEvaluationResult result = RelevanceEvaluationResult.withRelevantDocs(docIds, indexes);

        assertTrue(result.isHasRelevantResults());
        assertEquals(docIds, result.getRelevantDocIds());
        assertEquals(indexes, result.getRelevantIndexes());
    }

    @Test
    public void test_withRelevantDocs_singleDoc() {
        List<String> docIds = Arrays.asList("doc1");
        List<Integer> indexes = Arrays.asList(1);
        RelevanceEvaluationResult result = RelevanceEvaluationResult.withRelevantDocs(docIds, indexes);

        assertTrue(result.isHasRelevantResults());
        assertEquals(1, result.getRelevantDocIds().size());
        assertEquals("doc1", result.getRelevantDocIds().get(0));
        assertEquals(1, result.getRelevantIndexes().size());
        assertEquals(Integer.valueOf(1), result.getRelevantIndexes().get(0));
    }

    @Test
    public void test_withRelevantDocs_emptyLists() {
        List<String> docIds = Collections.emptyList();
        List<Integer> indexes = Collections.emptyList();
        RelevanceEvaluationResult result = RelevanceEvaluationResult.withRelevantDocs(docIds, indexes);

        assertFalse(result.isHasRelevantResults());
        assertTrue(result.getRelevantDocIds().isEmpty());
        assertTrue(result.getRelevantIndexes().isEmpty());
    }

    @Test
    public void test_withRelevantDocs_nullDocIds() {
        List<Integer> indexes = Arrays.asList(1, 2);
        RelevanceEvaluationResult result = RelevanceEvaluationResult.withRelevantDocs(null, indexes);

        assertFalse(result.isHasRelevantResults());
        assertNotNull(result.getRelevantDocIds());
        assertTrue(result.getRelevantDocIds().isEmpty());
    }

    @Test
    public void test_withRelevantDocs_nullIndexes() {
        List<String> docIds = Arrays.asList("doc1", "doc2");
        RelevanceEvaluationResult result = RelevanceEvaluationResult.withRelevantDocs(docIds, null);

        assertTrue(result.isHasRelevantResults());
        assertEquals(2, result.getRelevantDocIds().size());
        assertNotNull(result.getRelevantIndexes());
        assertTrue(result.getRelevantIndexes().isEmpty());
    }

    @Test
    public void test_noRelevantResults() {
        RelevanceEvaluationResult result = RelevanceEvaluationResult.noRelevantResults();

        assertFalse(result.isHasRelevantResults());
        assertNotNull(result.getRelevantDocIds());
        assertTrue(result.getRelevantDocIds().isEmpty());
        assertNotNull(result.getRelevantIndexes());
        assertTrue(result.getRelevantIndexes().isEmpty());
    }

    @Test
    public void test_fallbackAllRelevant() {
        List<String> docIds = Arrays.asList("doc1", "doc2", "doc3", "doc4", "doc5");
        RelevanceEvaluationResult result = RelevanceEvaluationResult.fallbackAllRelevant(docIds);

        assertTrue(result.isHasRelevantResults());
        assertEquals(docIds, result.getRelevantDocIds());
        assertNotNull(result.getRelevantIndexes());
        assertTrue(result.getRelevantIndexes().isEmpty());
    }

    @Test
    public void test_fallbackAllRelevant_emptyList() {
        List<String> docIds = Collections.emptyList();
        RelevanceEvaluationResult result = RelevanceEvaluationResult.fallbackAllRelevant(docIds);

        assertFalse(result.isHasRelevantResults());
        assertTrue(result.getRelevantDocIds().isEmpty());
        assertTrue(result.getRelevantIndexes().isEmpty());
    }

    @Test
    public void test_fallbackAllRelevant_singleDoc() {
        List<String> docIds = Arrays.asList("singleDoc");
        RelevanceEvaluationResult result = RelevanceEvaluationResult.fallbackAllRelevant(docIds);

        assertTrue(result.isHasRelevantResults());
        assertEquals(1, result.getRelevantDocIds().size());
        assertEquals("singleDoc", result.getRelevantDocIds().get(0));
    }

    @Test
    public void test_docIdsAreImmutable() {
        List<String> docIds = Arrays.asList("doc1", "doc2");
        List<Integer> indexes = Arrays.asList(1, 2);
        RelevanceEvaluationResult result = RelevanceEvaluationResult.withRelevantDocs(docIds, indexes);

        try {
            result.getRelevantDocIds().add("newDoc");
            fail("DocIds list should be immutable");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
    }

    @Test
    public void test_indexesAreImmutable() {
        List<String> docIds = Arrays.asList("doc1", "doc2");
        List<Integer> indexes = Arrays.asList(1, 2);
        RelevanceEvaluationResult result = RelevanceEvaluationResult.withRelevantDocs(docIds, indexes);

        try {
            result.getRelevantIndexes().add(3);
            fail("Indexes list should be immutable");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
    }

    @Test
    public void test_withRelevantDocs_manyDocs() {
        List<String> docIds = Arrays.asList("doc1", "doc2", "doc3", "doc4", "doc5", "doc6", "doc7", "doc8", "doc9", "doc10");
        List<Integer> indexes = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        RelevanceEvaluationResult result = RelevanceEvaluationResult.withRelevantDocs(docIds, indexes);

        assertTrue(result.isHasRelevantResults());
        assertEquals(10, result.getRelevantDocIds().size());
        assertEquals(10, result.getRelevantIndexes().size());
    }

    @Test
    public void test_docIds_withSpecialCharacters() {
        List<String> docIds = Arrays.asList("doc-with-dash", "doc_with_underscore", "doc.with.dot");
        List<Integer> indexes = Arrays.asList(1, 2, 3);
        RelevanceEvaluationResult result = RelevanceEvaluationResult.withRelevantDocs(docIds, indexes);

        assertTrue(result.isHasRelevantResults());
        assertEquals("doc-with-dash", result.getRelevantDocIds().get(0));
        assertEquals("doc_with_underscore", result.getRelevantDocIds().get(1));
        assertEquals("doc.with.dot", result.getRelevantDocIds().get(2));
    }

    @Test
    public void test_toString() {
        List<String> docIds = Arrays.asList("doc1", "doc2");
        List<Integer> indexes = Arrays.asList(1, 2);
        RelevanceEvaluationResult result = RelevanceEvaluationResult.withRelevantDocs(docIds, indexes);

        String str = result.toString();
        assertNotNull(str);
        assertTrue(str.contains("RelevanceEvaluationResult"));
        assertTrue(str.contains("doc1"));
        assertTrue(str.contains("doc2"));
    }

    @Test
    public void test_noRelevantResults_toString() {
        RelevanceEvaluationResult result = RelevanceEvaluationResult.noRelevantResults();

        String str = result.toString();
        assertNotNull(str);
        assertTrue(str.contains("RelevanceEvaluationResult"));
        assertTrue(str.contains("false"));
    }
}
