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

    private static Map<String, Object> contentDoc(final String docId, final String content) {
        final Map<String, Object> m = new LinkedHashMap<>();
        m.put("doc_id", docId);
        m.put("content", content);
        return m;
    }

    /**
     * Stubs the two engine-touching fetches so dispatch/order can be tested without a search engine.
     * Behavior is configurable via fields so individual tests need no anonymous subclasses:
     * <ul>
     * <li>{@code highlightResult} / {@code fullResult}: when set, returned verbatim (e.g. an empty
     * list to simulate a highlight miss or an empty full fetch).</li>
     * <li>{@code fullContent}: when set (and {@code fullResult} is null), generated full docs carry
     * this content instead of the default {@code "FULL:<id>"} (used to exercise truncation).</li>
     * </ul>
     */
    private static class TestableFetcher extends DefaultChatContentFetcher {
        long threshold = 3000L;
        final List<String> fullCalledWith = new ArrayList<>();
        final List<String> highlightCalledWith = new ArrayList<>();
        List<Map<String, Object>> highlightResult;
        List<Map<String, Object>> fullResult;
        String fullContent;

        @Override
        protected long getFulltextThreshold() {
            return threshold;
        }

        @Override
        protected String getHighlightContentField() {
            return "hl_content";
        }

        @Override
        protected List<Map<String, Object>> fetchFullContent(final List<String> docIds) {
            fullCalledWith.addAll(docIds);
            if (fullResult != null) {
                return new ArrayList<>(fullResult);
            }
            final List<Map<String, Object>> r = new ArrayList<>();
            for (final String id : docIds) {
                r.add(contentDoc(id, fullContent != null ? fullContent : "FULL:" + id));
            }
            return r;
        }

        @Override
        protected List<Map<String, Object>> fetchHighlightedContent(final List<String> docIds, final String query) {
            highlightCalledWith.addAll(docIds);
            if (highlightResult != null) {
                return new ArrayList<>(highlightResult);
            }
            final List<Map<String, Object>> r = new ArrayList<>();
            for (final String id : docIds) {
                r.add(contentDoc(id, "HL:" + id));
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
    public void test_decideStrategy_atThresholdIsFull() {
        final TestableFetcher f = new TestableFetcher();
        f.threshold = 3000L;
        final ChatContentRequest req = new ChatContentRequest(List.of("a"), List.of(), "q");
        // The dispatch uses a strict ">" comparison, so content_length equal to the
        // threshold is treated as a small document (FULL), not a large one (HIGHLIGHT).
        assertEquals(DefaultChatContentFetcher.Strategy.FULL, f.decideStrategy("a", 3000L, req));
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
    public void test_fetchContent_nullDocIdsReturnsEmpty() {
        final TestableFetcher f = new TestableFetcher();
        final List<Map<String, Object>> out = f.fetchContent(new ChatContentRequest(null, List.of(), "q"));
        assertTrue(out.isEmpty());
        assertTrue(f.fullCalledWith.isEmpty());
        assertTrue(f.highlightCalledWith.isEmpty());
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
    public void test_truncateContent() {
        final TestableFetcher f = new TestableFetcher();
        final Map<String, Object> longDoc = new LinkedHashMap<>();
        longDoc.put("content", "0123456789");
        f.truncateContent(longDoc, 4L);
        assertEquals("0123", longDoc.get("content"));

        final Map<String, Object> shortDoc = new LinkedHashMap<>();
        shortDoc.put("content", "ab");
        f.truncateContent(shortDoc, 4L);
        assertEquals("ab", shortDoc.get("content")); // shorter than max -> unchanged

        final Map<String, Object> noContent = new LinkedHashMap<>();
        f.truncateContent(noContent, 4L);
        assertNull(noContent.get("content")); // missing content -> no-op
    }

    @Test
    public void test_truncateContent_lengthEqualsMaxIsUnchanged() {
        final TestableFetcher f = new TestableFetcher();
        final Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("content", "0123"); // length 4 == maxLength
        f.truncateContent(doc, 4L);
        // Only content strictly longer than maxLength is cut; equal length stays intact.
        assertEquals("0123", doc.get("content"));
    }

    @Test
    public void test_truncateContent_nonStringContentIsNoOp() {
        final TestableFetcher f = new TestableFetcher();
        final Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("content", Integer.valueOf(123456789)); // non-String value
        f.truncateContent(doc, 4L);
        // A non-String content value is left untouched.
        assertEquals(Integer.valueOf(123456789), doc.get("content"));
    }

    @Test
    public void test_truncateContent_zeroMaxLengthYieldsEmpty() {
        final TestableFetcher f = new TestableFetcher();
        final Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("content", "abc");
        f.truncateContent(doc, 0L);
        // maxLength 0 truncates a non-empty string to the empty string.
        assertEquals("", doc.get("content"));
    }

    @Test
    public void test_normalizeHighlightedDocs_setsContentAndDropsBlank() {
        final TestableFetcher f = new TestableFetcher();
        final Map<String, Object> withSnippet = new LinkedHashMap<>();
        withSnippet.put("doc_id", "a");
        withSnippet.put("hl_content", "snippet-a");
        final Map<String, Object> blankSnippet = new LinkedHashMap<>();
        blankSnippet.put("doc_id", "b");
        blankSnippet.put("hl_content", "");
        final Map<String, Object> noSnippet = new LinkedHashMap<>();
        noSnippet.put("doc_id", "c");
        // No highlight passage, but content_description falls back to the short digest at the
        // search layer. The digest must NOT be treated as a passage; this doc must be dropped so
        // the caller falls back to its truncated full content.
        noSnippet.put("content_description", "digest-c");

        final List<Map<String, Object>> out = f.normalizeHighlightedDocs(new ArrayList<>(List.of(withSnippet, blankSnippet, noSnippet)));

        assertEquals(1, out.size());
        assertEquals("a", out.get(0).get("doc_id"));
        assertEquals("snippet-a", out.get(0).get("content"));
    }

    @Test
    public void test_normalizeHighlightedDocs_multipleKeepsOrderOfPassagesOnly() {
        final TestableFetcher f = new TestableFetcher();
        final Map<String, Object> a = new LinkedHashMap<>();
        a.put("doc_id", "a");
        a.put("hl_content", "snippet-a");
        final Map<String, Object> b = new LinkedHashMap<>();
        b.put("doc_id", "b");
        b.put("hl_content", ""); // blank -> dropped
        final Map<String, Object> c = new LinkedHashMap<>();
        c.put("doc_id", "c");
        c.put("hl_content", "snippet-c");
        final Map<String, Object> d = new LinkedHashMap<>();
        d.put("doc_id", "d"); // no hl_content -> dropped

        final List<Map<String, Object>> out = f.normalizeHighlightedDocs(new ArrayList<>(List.of(a, b, c, d)));

        // Only docs with a non-blank passage survive, in their original order.
        assertEquals(2, out.size());
        assertEquals("a", out.get(0).get("doc_id"));
        assertEquals("snippet-a", out.get(0).get("content"));
        assertEquals("c", out.get(1).get("doc_id"));
        assertEquals("snippet-c", out.get(1).get("content"));
    }

    @Test
    public void test_fetchContent_highlightMissFallsBackToTruncatedFull() {
        final TestableFetcher f = new TestableFetcher();
        f.threshold = 4L;
        f.highlightResult = new ArrayList<>(); // highlight had no match
        f.fullContent = "0123456789"; // length 10, exceeds the threshold
        final ChatContentRequest req = new ChatContentRequest(List.of("big"), List.of(doc("big", 99999L)), "q");
        final List<Map<String, Object>> out = f.fetchContent(req);
        // A large doc with no passage falls back to full content truncated to the threshold.
        assertEquals(1, out.size());
        assertEquals("big", out.get(0).get("doc_id"));
        assertEquals("0123", out.get(0).get("content"));
        assertEquals(List.of("big"), f.highlightCalledWith);
        assertEquals(List.of("big"), f.fullCalledWith);
    }

    @Test
    public void test_fetchContent_mixedHighlightMissFallsBackToFull() {
        final TestableFetcher f = new TestableFetcher();
        f.highlightResult = new ArrayList<>(); // highlight had no match for the large doc
        final List<String> docIds = List.of("small", "big");
        final List<Map<String, Object>> searchResults = List.of(doc("small", 100L), doc("big", 99999L));
        final List<Map<String, Object>> out = f.fetchContent(new ChatContentRequest(docIds, searchResults, "q"));
        // Small doc kept; large doc falls back to full content (short here, so not truncated).
        assertEquals(2, out.size());
        assertEquals("small", out.get(0).get("doc_id"));
        assertEquals("FULL:small", out.get(0).get("content"));
        assertEquals("big", out.get(1).get("doc_id"));
        assertEquals("FULL:big", out.get(1).get("content"));
    }

    @Test
    public void test_fetchContent_unknownContentLengthIsFull() {
        final TestableFetcher f = new TestableFetcher();
        // doc_id "x" is absent from searchResults, so its content_length is unknown (null).
        final List<Map<String, Object>> searchResults = List.of(doc("other", 100L));
        final List<Map<String, Object>> out = f.fetchContent(new ChatContentRequest(List.of("x"), searchResults, "q"));

        // Unknown size falls back to FULL; highlight is never attempted.
        assertEquals(1, out.size());
        assertEquals("x", out.get(0).get("doc_id"));
        assertEquals("FULL:x", out.get(0).get("content"));
        assertEquals(List.of("x"), f.fullCalledWith);
        assertTrue(f.highlightCalledWith.isEmpty());
    }

    @Test
    public void test_fetchContent_allLargeWithPassagesNoFullFallback() {
        final TestableFetcher f = new TestableFetcher();
        final List<String> docIds = List.of("big1", "big2");
        final List<Map<String, Object>> searchResults = List.of(doc("big1", 99999L), doc("big2", 88888L));
        final List<Map<String, Object>> out = f.fetchContent(new ChatContentRequest(docIds, searchResults, "q"));

        // Every large doc produced a passage, so no full-content fallback is fetched.
        assertEquals(List.of("big1", "big2"), f.highlightCalledWith);
        assertTrue(f.fullCalledWith.isEmpty());
        assertEquals(2, out.size());
        assertEquals("big1", out.get(0).get("doc_id"));
        assertEquals("HL:big1", out.get(0).get("content"));
        assertEquals("big2", out.get(1).get("doc_id"));
        assertEquals("HL:big2", out.get(1).get("content"));
    }

    @Test
    public void test_fetchContent_partialHighlightHitMixesPassageAndTruncatedFull() {
        // Highlight returns a passage for "big1" only; "big2" misses and must fall back
        // to its truncated full content while preserving the original docIds order.
        final TestableFetcher f = new TestableFetcher();
        f.threshold = 4L;
        f.highlightResult = new ArrayList<>(List.of(contentDoc("big1", "HL:big1"))); // only big1 returned
        f.fullContent = "0123456789"; // length 10, exceeds the threshold
        final List<String> docIds = List.of("big1", "big2");
        final List<Map<String, Object>> searchResults = List.of(doc("big1", 99999L), doc("big2", 88888L));
        final List<Map<String, Object>> out = f.fetchContent(new ChatContentRequest(docIds, searchResults, "q"));

        assertEquals(List.of("big1", "big2"), f.highlightCalledWith);
        assertEquals(List.of("big2"), f.fullCalledWith); // only the miss is reconciled via full
        assertEquals(2, out.size());
        assertEquals("big1", out.get(0).get("doc_id"));
        assertEquals("HL:big1", out.get(0).get("content")); // hit keeps its passage
        assertEquals("big2", out.get(1).get("doc_id"));
        assertEquals("0123", out.get(1).get("content")); // miss -> truncated full
    }

    @Test
    public void test_fetchContent_allLargeMissFallBackToTruncatedFull() {
        final TestableFetcher f = new TestableFetcher();
        f.threshold = 4L;
        f.highlightResult = new ArrayList<>(); // every large doc misses
        f.fullContent = "0123456789"; // length 10, exceeds the threshold
        final List<String> docIds = List.of("big1", "big2");
        final List<Map<String, Object>> searchResults = List.of(doc("big1", 99999L), doc("big2", 88888L));
        final List<Map<String, Object>> out = f.fetchContent(new ChatContentRequest(docIds, searchResults, "q"));

        assertEquals(List.of("big1", "big2"), f.highlightCalledWith);
        assertEquals(List.of("big1", "big2"), f.fullCalledWith); // all misses reconciled via full
        assertEquals(2, out.size());
        assertEquals("big1", out.get(0).get("doc_id"));
        assertEquals("0123", out.get(0).get("content"));
        assertEquals("big2", out.get(1).get("doc_id"));
        assertEquals("0123", out.get(1).get("content"));
    }

    @Test
    public void test_fetchContent_reconciledDocKeepsOriginalPosition() {
        // The large doc sits in the middle of the docIds order. After missing the
        // highlight phase it is reconciled via full content, and must still land at its
        // original middle position in the output (not appended at the end).
        final TestableFetcher f = new TestableFetcher();
        f.highlightResult = new ArrayList<>(); // the large doc misses
        final List<String> docIds = List.of("small1", "big", "small2");
        final List<Map<String, Object>> searchResults = List.of(doc("small1", 100L), doc("big", 99999L), doc("small2", 200L));
        final List<Map<String, Object>> out = f.fetchContent(new ChatContentRequest(docIds, searchResults, "q"));

        assertEquals(List.of("big"), f.highlightCalledWith);
        // Full is called once for the small docs and once for the reconciled large doc.
        assertEquals(List.of("small1", "small2", "big"), f.fullCalledWith);
        assertEquals(3, out.size());
        assertEquals("small1", out.get(0).get("doc_id"));
        assertEquals("FULL:small1", out.get(0).get("content"));
        assertEquals("big", out.get(1).get("doc_id")); // reconciled doc stays in the middle
        assertEquals("FULL:big", out.get(1).get("content"));
        assertEquals("small2", out.get(2).get("doc_id"));
        assertEquals("FULL:small2", out.get(2).get("content"));
    }

    @Test
    public void test_fetchContent_blankQueryAllFull() {
        final TestableFetcher f = new TestableFetcher();
        final List<String> docIds = List.of("big1", "big2");
        final List<Map<String, Object>> searchResults = List.of(doc("big1", 99999L), doc("big2", 88888L));
        // Blank query disables highlighting, so even large docs are fetched in full.
        final List<Map<String, Object>> out = f.fetchContent(new ChatContentRequest(docIds, searchResults, ""));

        assertTrue(f.highlightCalledWith.isEmpty());
        assertEquals(List.of("big1", "big2"), f.fullCalledWith);
        assertEquals(2, out.size());
        assertEquals("FULL:big1", out.get(0).get("content"));
        assertEquals("FULL:big2", out.get(1).get("content"));
    }

    @Test
    public void test_fetchContent_unresolvedSmallDocIsExcludedNotRetried() {
        // A small doc whose full fetch returns nothing is excluded. Reconciliation is scoped to
        // large docs (highlight misses) only, so a failed small-doc fetch is not retried.
        final TestableFetcher f = new TestableFetcher();
        f.fullResult = new ArrayList<>(); // engine returned nothing for the requested doc
        final List<String> docIds = List.of("small");
        final List<Map<String, Object>> searchResults = List.of(doc("small", 100L));
        final List<Map<String, Object>> out = f.fetchContent(new ChatContentRequest(docIds, searchResults, "q"));

        assertTrue(out.isEmpty());
        assertEquals(List.of("small"), f.fullCalledWith); // fetched once, not retried
        assertTrue(f.highlightCalledWith.isEmpty());
    }

    @Test
    public void test_fetchContent_emptySearchResultsAndNullQueryAllFull() {
        final TestableFetcher f = new TestableFetcher();
        final List<String> docIds = List.of("a", "b");
        // Mirrors the ChatClient.fetchFullContent path: no search results (unknown sizes)
        // and a null query. Every doc resolves to FULL with no highlight attempt.
        final List<Map<String, Object>> out = f.fetchContent(new ChatContentRequest(docIds, List.of(), null));

        assertTrue(f.highlightCalledWith.isEmpty());
        assertEquals(List.of("a", "b"), f.fullCalledWith);
        assertEquals(2, out.size());
        assertEquals("a", out.get(0).get("doc_id"));
        assertEquals("FULL:a", out.get(0).get("content"));
        assertEquals("b", out.get(1).get("doc_id"));
        assertEquals("FULL:b", out.get(1).get("content"));
    }
}
