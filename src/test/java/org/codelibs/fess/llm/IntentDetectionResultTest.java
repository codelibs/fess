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

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class IntentDetectionResultTest extends UnitFessTestCase {

    @Test
    public void test_search_withQuery() {
        final String query = "title:\"Fess\"^2 OR \"Fess\"";
        final IntentDetectionResult result = IntentDetectionResult.search(query, "test reasoning");

        assertEquals(ChatIntent.SEARCH, result.getIntent());
        assertEquals(query, result.getQuery());
        assertNull(result.getDocumentUrl());
        assertEquals("test reasoning", result.getReasoning());
    }

    @Test
    public void test_search_withEmptyQuery() {
        final IntentDetectionResult result = IntentDetectionResult.search("", "test");

        assertEquals(ChatIntent.SEARCH, result.getIntent());
        assertEquals("", result.getQuery());
    }

    @Test
    public void test_search_withNullQuery() {
        final IntentDetectionResult result = IntentDetectionResult.search(null, "test");

        assertEquals(ChatIntent.SEARCH, result.getIntent());
        assertNull(result.getQuery());
    }

    @Test
    public void test_search_withSimpleQuery() {
        final String query = "singleKeyword";
        final IntentDetectionResult result = IntentDetectionResult.search(query, "single keyword search");

        assertEquals(ChatIntent.SEARCH, result.getIntent());
        assertEquals("singleKeyword", result.getQuery());
    }

    @Test
    public void test_search_withLuceneQuery() {
        final String query = "+Fess +Docker (tutorial OR guide)";
        final IntentDetectionResult result = IntentDetectionResult.search(query, "Lucene query");

        assertEquals(ChatIntent.SEARCH, result.getIntent());
        assertEquals(query, result.getQuery());
    }

    @Test
    public void test_summary_withDocumentUrl() {
        final String documentUrl = "https://example.com/doc.pdf";
        final IntentDetectionResult result = IntentDetectionResult.summary(documentUrl, "summary request");

        assertEquals(ChatIntent.SUMMARY, result.getIntent());
        assertEquals(documentUrl, result.getDocumentUrl());
        assertNull(result.getQuery());
        assertEquals("summary request", result.getReasoning());
    }

    @Test
    public void test_summary_withNullDocumentUrl() {
        final IntentDetectionResult result = IntentDetectionResult.summary(null, "no doc url");

        assertEquals(ChatIntent.SUMMARY, result.getIntent());
        assertNull(result.getDocumentUrl());
    }

    @Test
    public void test_faq_withQuery() {
        final String query = "+Docker (install OR setup)";
        final IntentDetectionResult result = IntentDetectionResult.faq(query, "faq intent");

        assertEquals(ChatIntent.FAQ, result.getIntent());
        assertEquals(query, result.getQuery());
        assertNull(result.getDocumentUrl());
        assertEquals("faq intent", result.getReasoning());
    }

    @Test
    public void test_faq_withEmptyQuery() {
        final IntentDetectionResult result = IntentDetectionResult.faq("", null);

        assertEquals(ChatIntent.FAQ, result.getIntent());
        assertEquals("", result.getQuery());
    }

    @Test
    public void test_unclear() {
        final IntentDetectionResult result = IntentDetectionResult.unclear("unclear intent");

        assertEquals(ChatIntent.UNCLEAR, result.getIntent());
        assertNull(result.getQuery());
        assertNull(result.getDocumentUrl());
        assertEquals("unclear intent", result.getReasoning());
    }

    @Test
    public void test_unclear_withNullReasoning() {
        final IntentDetectionResult result = IntentDetectionResult.unclear(null);

        assertEquals(ChatIntent.UNCLEAR, result.getIntent());
        assertNull(result.getReasoning());
    }

    @Test
    public void test_fallbackSearch() {
        final String originalQuery = "original query text";
        final IntentDetectionResult result = IntentDetectionResult.fallbackSearch(originalQuery);

        assertEquals(ChatIntent.SEARCH, result.getIntent());
        assertEquals(originalQuery, result.getQuery());
        assertNotNull(result.getReasoning());
        assertTrue(result.getReasoning().contains("Fallback"));
    }

    @Test
    public void test_fallbackSearch_withEmptyQuery() {
        final IntentDetectionResult result = IntentDetectionResult.fallbackSearch("");

        assertEquals(ChatIntent.SEARCH, result.getIntent());
        assertEquals("", result.getQuery());
    }

    @Test
    public void test_toString() {
        final IntentDetectionResult result = IntentDetectionResult.search("test query", "reasoning");

        final String str = result.toString();
        assertNotNull(str);
        assertTrue(str.contains("IntentDetectionResult"));
        assertTrue(str.contains("SEARCH"));
        assertTrue(str.contains("test query"));
    }

    @Test
    public void test_reasoning_preserved() {
        final String reasoning = "This is a search query because it contains question words";
        final IntentDetectionResult result = IntentDetectionResult.search("test", reasoning);

        assertEquals(reasoning, result.getReasoning());
    }

    @Test
    public void test_documentUrl_preserved() {
        final String docUrl = "https://example.com/doc-123-456.pdf";
        final IntentDetectionResult result = IntentDetectionResult.summary(docUrl, "summary");

        assertEquals(docUrl, result.getDocumentUrl());
    }

    @Test
    public void test_search_withComplexLuceneQuery() {
        final String query = "title:\"Fess\"^2 OR \"Fess\"";
        final IntentDetectionResult result = IntentDetectionResult.search(query, "Lucene query");

        assertEquals(ChatIntent.SEARCH, result.getIntent());
        assertEquals(query, result.getQuery());
        assertEquals("Lucene query", result.getReasoning());
    }

    @Test
    public void test_summary_hasNoQuery() {
        final IntentDetectionResult result = IntentDetectionResult.summary("https://example.com", "summary");

        assertEquals(ChatIntent.SUMMARY, result.getIntent());
        assertNull(result.getQuery());
    }

    @Test
    public void test_unclear_hasNoQuery() {
        final IntentDetectionResult result = IntentDetectionResult.unclear("unclear");

        assertEquals(ChatIntent.UNCLEAR, result.getIntent());
        assertNull(result.getQuery());
    }

    @Test
    public void test_fallbackSearch_usesOriginalMessageAsQuery() {
        final IntentDetectionResult result = IntentDetectionResult.fallbackSearch("original query");

        assertEquals(ChatIntent.SEARCH, result.getIntent());
        assertEquals("original query", result.getQuery());
    }

    @Test
    public void test_toString_includesQuery() {
        final String query = "+Fess +Docker";
        final IntentDetectionResult result = IntentDetectionResult.search(query, "test");

        final String str = result.toString();
        assertNotNull(str);
        assertTrue(str.contains("query"));
        assertTrue(str.contains(query));
    }

    @Test
    public void test_query_complexLuceneSyntax() {
        final String query = "title:\"security policy\"^2 OR (+security +policy (guide OR document))";
        final IntentDetectionResult result = IntentDetectionResult.search(query, "complex query");

        assertEquals(query, result.getQuery());
    }

    @Test
    public void test_faq_complexLuceneSyntax() {
        final String query = "+\"OpenSearch\" (configuration OR settings OR config)";
        final IntentDetectionResult result = IntentDetectionResult.faq(query, "FAQ with Lucene");

        assertEquals(ChatIntent.FAQ, result.getIntent());
        assertEquals(query, result.getQuery());
    }
}
