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
package org.codelibs.fess.chat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class IntentDetectionResultTest extends UnitFessTestCase {

    @Test
    public void test_search_withKeywords() {
        List<String> keywords = Arrays.asList("keyword1", "keyword2");
        IntentDetectionResult result = IntentDetectionResult.search(keywords, "test reasoning");

        assertEquals(ChatIntent.SEARCH, result.getIntent());
        assertEquals(keywords, result.getKeywords());
        assertNull(result.getDocumentUrl());
        assertEquals("test reasoning", result.getReasoning());
    }

    @Test
    public void test_search_withEmptyKeywords() {
        List<String> keywords = Collections.emptyList();
        IntentDetectionResult result = IntentDetectionResult.search(keywords, null);

        assertEquals(ChatIntent.SEARCH, result.getIntent());
        assertTrue(result.getKeywords().isEmpty());
    }

    @Test
    public void test_search_withNullKeywords() {
        IntentDetectionResult result = IntentDetectionResult.search(null, "test");

        assertEquals(ChatIntent.SEARCH, result.getIntent());
        assertNotNull(result.getKeywords());
        assertTrue(result.getKeywords().isEmpty());
    }

    @Test
    public void test_search_withSingleKeyword() {
        List<String> keywords = Arrays.asList("singleKeyword");
        IntentDetectionResult result = IntentDetectionResult.search(keywords, "single keyword search");

        assertEquals(ChatIntent.SEARCH, result.getIntent());
        assertEquals(1, result.getKeywords().size());
        assertEquals("singleKeyword", result.getKeywords().get(0));
    }

    @Test
    public void test_search_withManyKeywords() {
        List<String> keywords = Arrays.asList("k1", "k2", "k3", "k4", "k5");
        IntentDetectionResult result = IntentDetectionResult.search(keywords, "multiple keywords");

        assertEquals(ChatIntent.SEARCH, result.getIntent());
        assertEquals(5, result.getKeywords().size());
    }

    @Test
    public void test_summary_withDocumentUrl() {
        String documentUrl = "https://example.com/doc.pdf";
        IntentDetectionResult result = IntentDetectionResult.summary(documentUrl, "summary request");

        assertEquals(ChatIntent.SUMMARY, result.getIntent());
        assertEquals(documentUrl, result.getDocumentUrl());
        assertTrue(result.getKeywords().isEmpty());
        assertEquals("summary request", result.getReasoning());
    }

    @Test
    public void test_summary_withNullDocumentUrl() {
        IntentDetectionResult result = IntentDetectionResult.summary(null, "no doc url");

        assertEquals(ChatIntent.SUMMARY, result.getIntent());
        assertNull(result.getDocumentUrl());
    }

    @Test
    public void test_faq_withKeywords() {
        List<String> keywords = Arrays.asList("faq", "question");
        IntentDetectionResult result = IntentDetectionResult.faq(keywords, "faq intent");

        assertEquals(ChatIntent.FAQ, result.getIntent());
        assertEquals(keywords, result.getKeywords());
        assertNull(result.getDocumentUrl());
        assertEquals("faq intent", result.getReasoning());
    }

    @Test
    public void test_faq_withEmptyKeywords() {
        IntentDetectionResult result = IntentDetectionResult.faq(Collections.emptyList(), null);

        assertEquals(ChatIntent.FAQ, result.getIntent());
        assertTrue(result.getKeywords().isEmpty());
    }

    @Test
    public void test_unclear() {
        IntentDetectionResult result = IntentDetectionResult.unclear("unclear intent");

        assertEquals(ChatIntent.UNCLEAR, result.getIntent());
        assertTrue(result.getKeywords().isEmpty());
        assertNull(result.getDocumentUrl());
        assertEquals("unclear intent", result.getReasoning());
    }

    @Test
    public void test_unclear_withNullReasoning() {
        IntentDetectionResult result = IntentDetectionResult.unclear(null);

        assertEquals(ChatIntent.UNCLEAR, result.getIntent());
        assertNull(result.getReasoning());
    }

    @Test
    public void test_fallbackSearch() {
        String originalQuery = "original query text";
        IntentDetectionResult result = IntentDetectionResult.fallbackSearch(originalQuery);

        assertEquals(ChatIntent.SEARCH, result.getIntent());
        assertNotNull(result.getKeywords());
        assertEquals(1, result.getKeywords().size());
        assertEquals(originalQuery, result.getKeywords().get(0));
        assertNotNull(result.getReasoning());
        assertTrue(result.getReasoning().contains("Fallback"));
    }

    @Test
    public void test_fallbackSearch_withEmptyQuery() {
        IntentDetectionResult result = IntentDetectionResult.fallbackSearch("");

        assertEquals(ChatIntent.SEARCH, result.getIntent());
        assertNotNull(result.getKeywords());
        assertEquals(1, result.getKeywords().size());
        assertEquals("", result.getKeywords().get(0));
    }

    @Test
    public void test_keywordsAreImmutable() {
        List<String> keywords = Arrays.asList("test");
        IntentDetectionResult result = IntentDetectionResult.search(keywords, null);

        try {
            result.getKeywords().add("newKeyword");
            fail("Keywords list should be immutable");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
    }

    @Test
    public void test_toString() {
        IntentDetectionResult result = IntentDetectionResult.search(Arrays.asList("test"), "reasoning");

        String str = result.toString();
        assertNotNull(str);
        assertTrue(str.contains("IntentDetectionResult"));
        assertTrue(str.contains("SEARCH"));
        assertTrue(str.contains("test"));
    }

    @Test
    public void test_reasoning_preserved() {
        String reasoning = "This is a search query because it contains question words";
        IntentDetectionResult result = IntentDetectionResult.search(Arrays.asList("test"), reasoning);

        assertEquals(reasoning, result.getReasoning());
    }

    @Test
    public void test_documentUrl_preserved() {
        String docUrl = "https://example.com/doc-123-456.pdf";
        IntentDetectionResult result = IntentDetectionResult.summary(docUrl, "summary");

        assertEquals(docUrl, result.getDocumentUrl());
    }
}
