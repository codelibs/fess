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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class DefaultChatContentFetcherTest extends UnitFessTestCase {

    private static Map<String, Object> doc(final String docId, final Object contentLength) {
        final Map<String, Object> m = new LinkedHashMap<>();
        m.put("doc_id", docId);
        if (contentLength != null) {
            m.put("content_length", contentLength);
        }
        return m;
    }

    /** Stubs the two engine-touching fetches so dispatch/order can be tested without a search engine. */
    private static class TestableFetcher extends DefaultChatContentFetcher {
        long threshold = 3000L;
        final List<String> fullCalledWith = new ArrayList<>();
        final List<String> highlightCalledWith = new ArrayList<>();

        @Override
        protected long getFulltextThreshold() {
            return threshold;
        }

        @Override
        protected List<Map<String, Object>> fetchFullContent(final List<String> docIds) {
            fullCalledWith.addAll(docIds);
            final List<Map<String, Object>> r = new ArrayList<>();
            for (final String id : docIds) {
                final Map<String, Object> m = new LinkedHashMap<>();
                m.put("doc_id", id);
                m.put("content", "FULL:" + id);
                r.add(m);
            }
            return r;
        }

        @Override
        protected List<Map<String, Object>> fetchHighlightedContent(final List<String> docIds, final String query) {
            highlightCalledWith.addAll(docIds);
            final List<Map<String, Object>> r = new ArrayList<>();
            for (final String id : docIds) {
                final Map<String, Object> m = new LinkedHashMap<>();
                m.put("doc_id", id);
                m.put("content", "HL:" + id);
                r.add(m);
            }
            return r;
        }
    }

    @Test
    public void test_decideStrategy_bySize() {
        final TestableFetcher f = new TestableFetcher();
        final ChatContentRequest req = new ChatContentRequest(List.of("a"), List.of(), "q");
        assertEquals(DefaultChatContentFetcher.Strategy.FULL, f.decideStrategy("a", 2999L, req));
        assertEquals(DefaultChatContentFetcher.Strategy.HIGHLIGHT, f.decideStrategy("a", 3001L, req));
        assertEquals(DefaultChatContentFetcher.Strategy.FULL, f.decideStrategy("a", null, req));
    }

    @Test
    public void test_decideStrategy_blankQuery_isFull() {
        final TestableFetcher f = new TestableFetcher();
        final ChatContentRequest req = new ChatContentRequest(List.of("a"), List.of(), "");
        assertEquals(DefaultChatContentFetcher.Strategy.FULL, f.decideStrategy("a", 999999L, req));
    }

    @Test
    public void test_fetchContent_dispatchAndOrder() {
        final TestableFetcher f = new TestableFetcher();
        final List<String> docIds = List.of("small", "big", "small2");
        final List<Map<String, Object>> searchResults = List.of(doc("small", 100L), doc("big", 99999L), doc("small2", 200L));
        final ChatContentRequest req = new ChatContentRequest(docIds, searchResults, "q");

        final List<Map<String, Object>> out = f.fetchContent(req);

        assertEquals(List.of("big"), f.highlightCalledWith);
        assertEquals(List.of("small", "small2"), f.fullCalledWith);
        assertEquals(3, out.size());
        assertEquals("small", out.get(0).get("doc_id"));
        assertEquals("FULL:small", out.get(0).get("content"));
        assertEquals("big", out.get(1).get("doc_id"));
        assertEquals("HL:big", out.get(1).get("content"));
        assertEquals("small2", out.get(2).get("doc_id"));
    }

    @Test
    public void test_fetchContent_emptyDocIds() {
        final TestableFetcher f = new TestableFetcher();
        assertTrue(f.fetchContent(new ChatContentRequest(List.of(), List.of(), "q")).isEmpty());
    }

    @Test
    public void test_toLong() {
        final TestableFetcher f = new TestableFetcher();
        assertEquals(Long.valueOf(5L), f.toLong(Integer.valueOf(5)));
        assertEquals(Long.valueOf(7L), f.toLong("7"));
        assertNull(f.toLong("x"));
        assertNull(f.toLong(null));
    }

    @Test
    public void test_normalizeHighlightedDocs_setsContentAndDropsBlank() {
        final TestableFetcher f = new TestableFetcher();
        final Map<String, Object> withSnippet = new LinkedHashMap<>();
        withSnippet.put("doc_id", "a");
        withSnippet.put("content_description", "snippet-a");
        final Map<String, Object> blankSnippet = new LinkedHashMap<>();
        blankSnippet.put("doc_id", "b");
        blankSnippet.put("content_description", "");
        final Map<String, Object> noSnippet = new LinkedHashMap<>();
        noSnippet.put("doc_id", "c");

        final List<Map<String, Object>> out = f.normalizeHighlightedDocs(new ArrayList<>(List.of(withSnippet, blankSnippet, noSnippet)));

        assertEquals(1, out.size());
        assertEquals("a", out.get(0).get("doc_id"));
        assertEquals("snippet-a", out.get(0).get("content"));
    }

    @Test
    public void test_fetchContent_missingReconciledToFull() {
        final TestableFetcher f = new TestableFetcher() {
            @Override
            protected List<Map<String, Object>> fetchHighlightedContent(final List<String> docIds, final String query) {
                highlightCalledWith.addAll(docIds);
                return new ArrayList<>(); // highlight had no match
            }
        };
        final ChatContentRequest req = new ChatContentRequest(List.of("big"), List.of(doc("big", 99999L)), "q");
        final List<Map<String, Object>> out = f.fetchContent(req);
        assertEquals(1, out.size());
        assertEquals("FULL:big", out.get(0).get("content"));
    }
}
