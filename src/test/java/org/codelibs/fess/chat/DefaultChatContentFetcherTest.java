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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.codelibs.fess.Constants;
import org.codelibs.fess.embedding.AbstractEmbeddingClient;
import org.codelibs.fess.embedding.EmbeddingClientManager;
import org.codelibs.fess.helper.ChunkVectorHelper;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class DefaultChatContentFetcherTest extends UnitFessTestCase {

    @Override
    public void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        // DefaultChatContentFetcher#isContentChunkerEnabled (and AnswerHighlightSearchParams#
        // getResponseFields) now delegate to the ChunkVectorHelper component; the minimal
        // test_app.xml DI set does not wire it, so register a real instance under its canonical
        // class name for ComponentUtil.getComponent(ChunkVectorHelper.class)'s fallback lookup.
        ComponentUtil.register(new ChunkVectorHelper(), ChunkVectorHelper.class.getCanonicalName());
    }

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
     * Builds a chunked ({@code content_chunk_status=done}) document map. {@code vectors}, if
     * non-null, must have one entry per chunk, each a {@code List<Double>} -- the actual shape a
     * {@code knn_vector} nested field deserializes to from an OpenSearch {@code _source} (not a
     * directly-injected {@code float[]}).
     */
    private static Map<String, Object> chunkedDoc(final String docId, final List<String> chunks, final List<List<Double>> vectors) {
        final Map<String, Object> m = new LinkedHashMap<>();
        m.put("doc_id", docId);
        m.put("content", new ArrayList<Object>(chunks));
        m.put(Constants.CONTENT_CHUNK_STATUS_FIELD, Constants.DONE);
        if (vectors != null) {
            final List<Map<String, Object>> vectorEntries = new ArrayList<>();
            for (final List<Double> v : vectors) {
                final Map<String, Object> entry = new LinkedHashMap<>();
                entry.put(ChunkVectorHelper.VECTOR_SUBFIELD, v);
                vectorEntries.add(entry);
            }
            m.put(Constants.CONTENT_CHUNK_VECTOR_FIELD, vectorEntries);
        }
        return m;
    }

    /**
     * Builds a chunk-only ({@code content_chunk_status=chunked}) document map -- a chunk array
     * written WITHOUT vectors (the write mode used when embedding is configured off).
     */
    private static Map<String, Object> chunkOnlyDoc(final String docId, final List<String> chunks) {
        final Map<String, Object> m = chunkedDoc(docId, chunks, null);
        m.put(Constants.CONTENT_CHUNK_STATUS_FIELD, Constants.CHUNKED);
        return m;
    }

    /** Fake {@link EmbeddingClientManager} seam for tests; counts embedQuery() invocations. */
    private static class FakeEmbeddingClientManager extends EmbeddingClientManager {
        boolean available = true;
        float[] queryVector;
        RuntimeException throwOnEmbed;
        int embedQueryCallCount = 0;

        @Override
        public boolean available() {
            return available;
        }

        @Override
        public float[] embedQuery(final String query) {
            embedQueryCallCount++;
            if (throwOnEmbed != null) {
                throw throwOnEmbed;
            }
            return queryVector;
        }
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
        EmbeddingClientManager embeddingManager;
        Integer chatTopKOverride;
        Boolean contentChunkerEnabledOverride;

        @Override
        protected long getFulltextThreshold() {
            return threshold;
        }

        @Override
        protected String getHighlightContentField() {
            return "hl_content";
        }

        @Override
        protected EmbeddingClientManager getEmbeddingClientManager() {
            return embeddingManager;
        }

        @Override
        protected int getChatTopK() {
            return chatTopKOverride != null ? chatTopKOverride : super.getChatTopK();
        }

        @Override
        protected boolean isContentChunkerEnabled() {
            return contentChunkerEnabledOverride != null ? contentChunkerEnabledOverride : super.isContentChunkerEnabled();
        }

        @Override
        protected List<Map<String, Object>> fetchFullContent(final List<String> docIds) {
            if (docIds.isEmpty()) {
                return new ArrayList<>(); // mirror the real method's empty-docIds early return
            }
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
            if (docIds.isEmpty()) {
                return new ArrayList<>(); // mirror the real method's empty-docIds early return
            }
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

    // Literal pin: this system-property key is external operator configuration; the raw VALUE is
    // pinned so silent drift reddens a test instead of orphaning existing config.
    @Test
    public void test_externalContractLiterals() {
        assertEquals("content_chunker.chat.top_k", DefaultChatContentFetcher.CHAT_TOP_K_PROPERTY);
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
    public void test_truncateContent_listContentIsJoinedAndTruncated() {
        final TestableFetcher f = new TestableFetcher();
        final Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("content", List.of("chunk one", "chunk two", "chunk three"));
        f.truncateContent(doc, 12L);

        final Object result = doc.get("content");
        assertTrue(result instanceof String, "List content exceeding maxLength must be joined into a truncated String");
        final String joined = "chunk one\n\nchunk two\n\nchunk three";
        assertEquals(joined.substring(0, 12), result);
    }

    @Test
    public void test_truncateContent_shortListContentIsUnchanged() {
        final TestableFetcher f = new TestableFetcher();
        final Map<String, Object> doc = new LinkedHashMap<>();
        final List<String> chunks = List.of("a", "b");
        doc.put("content", chunks);
        f.truncateContent(doc, 100L);

        // Joined length ("a\n\nb" = 5 chars) is within maxLength -> left as the original List.
        assertEquals(chunks, doc.get("content"));
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

    // ===== Chunk-selection fallback chain (content_chunk_status=done) =====

    @Test
    public void test_selectBySemanticSimilarity_picksTopKByCosineSimilarity() {
        final TestableFetcher f = new TestableFetcher();
        f.chatTopKOverride = 2;
        final List<String> chunks = List.of("chunk0", "chunk1", "chunk2", "chunk3");
        final Map<String, Object> doc = chunkedDoc("d1", chunks, List.of(List.of(0.0, 1.0), // chunk0: orthogonal to query -> sim 0
                List.of(1.0, 0.0), // chunk1: identical to query -> sim 1.0
                List.of(0.9, 0.1), // chunk2: close -> high sim
                List.of(-1.0, 0.0))); // chunk3: opposite -> sim -1
        final String result = f.selectBySemanticSimilarity(chunks, doc, new float[] { 1.0f, 0.0f });
        // Top 2 by similarity are chunk1 and chunk2, joined in original array order.
        assertEquals("chunk1\n\nchunk2", result);
    }

    @Test
    public void test_selectBySemanticSimilarity_zeroNormVectorScoresZeroNotNaN() {
        final TestableFetcher f = new TestableFetcher();
        f.chatTopKOverride = 1;
        final List<String> chunks = List.of("zero-chunk", "real-chunk");
        final Map<String, Object> doc = chunkedDoc("d1", chunks, List.of(List.of(0.0, 0.0), // zero-norm vector
                List.of(1.0, 0.0)));
        final String result = f.selectBySemanticSimilarity(chunks, doc, new float[] { 1.0f, 0.0f });
        assertEquals("real-chunk", result);
    }

    @Test
    public void test_selectBySemanticSimilarity_nanVectorComponentScoresZeroNotNaN() {
        final TestableFetcher f = new TestableFetcher();
        f.chatTopKOverride = 1;
        final List<String> chunks = List.of("corrupt-chunk", "real-chunk");
        final Map<String, Object> doc = chunkedDoc("d1", chunks, List.of(List.of(Double.NaN, 0.0), // corrupted embedding
                List.of(1.0, 0.0)));
        final String result = f.selectBySemanticSimilarity(chunks, doc, new float[] { 1.0f, 0.0f });
        // A NaN-corrupted vector must never dominate the top-K ranking (it must not win over a
        // genuinely-similar chunk, nor produce a NaN that would sort unpredictably).
        assertEquals("real-chunk", result);
    }

    @Test
    public void test_selectBySemanticSimilarity_mismatchedLengthVectorScoresZeroNotTruncated() {
        final TestableFetcher f = new TestableFetcher();
        f.chatTopKOverride = 1;
        final List<String> chunks = List.of("mismatched-chunk", "real-chunk");
        // mismatched-chunk's vector has length 3 vs. the length-2 query; naive Math.min-truncation
        // would compare only the first 2 components ([1.0, 0.0]) against the query ([1.0, 0.0]) and
        // score a perfect 1.0 -- beating the genuinely close, same-length real-chunk match below.
        // The fix must score the mismatch 0.0 instead, so real-chunk still wins.
        final Map<String, Object> doc = chunkedDoc("d1", chunks, List.of(List.of(1.0, 0.0, 5.0), // length-3: mismatched
                List.of(0.9, 0.1))); // length-2: genuinely close to the query
        final String result = f.selectBySemanticSimilarity(chunks, doc, new float[] { 1.0f, 0.0f });
        assertEquals("a length-mismatched vector must score 0.0 and lose to a same-length real match", "real-chunk", result);
    }

    @Test
    public void test_selectBySemanticSimilarity_returnsNullWhenNoVectorField() {
        final TestableFetcher f = new TestableFetcher();
        final List<String> chunks = List.of("a", "b");
        final Map<String, Object> doc = new LinkedHashMap<>();
        assertNull(f.selectBySemanticSimilarity(chunks, doc, new float[] { 1.0f }));
    }

    @Test
    public void test_selectByHighlightMatch_matchesFragmentsInOriginalArrayOrder() {
        final TestableFetcher f = new TestableFetcher();
        final List<String> chunks = List.of("intro text", "middle apple text", "banana text", "conclusion text");
        final Map<String, Object> doc = new LinkedHashMap<>();
        // Fragment appearance order (banana, then apple) is reversed vs. the chunk array order.
        doc.put("hl_content", "banana text...middle apple text");
        final String result = f.selectByHighlightMatch(chunks, doc);
        assertEquals("middle apple text\n\nbanana text", result);
    }

    @Test
    public void test_selectByHighlightMatch_noFragmentMatchesReturnsNull() {
        final TestableFetcher f = new TestableFetcher();
        final List<String> chunks = List.of("a", "b");
        final Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("hl_content", "nothing matches here");
        assertNull(f.selectByHighlightMatch(chunks, doc));
    }

    @Test
    public void test_selectByHighlightMatch_blankSnippetReturnsNull() {
        final TestableFetcher f = new TestableFetcher();
        final List<String> chunks = List.of("a", "b");
        final Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("hl_content", "");
        assertNull(f.selectByHighlightMatch(chunks, doc));
    }

    /**
     * Regression test for a bug found via live OpenSearch integration testing: when an earlier
     * highlight fragment already ends with a sentence-terminating period, {@code ViewHelper}'s
     * "..." join produces a run of 4 consecutive dots at that boundary (the fragment's own
     * trailing "." plus the 3-dot separator). Splitting on an exact 3-dot literal only consumed
     * the first 3 of those 4 dots, leaving a stray leading "." glued onto the next recovered
     * fragment -- so its {@code contains()} check against the real chunk text (which has no such
     * leading period) failed, silently dropping that chunk from RAG selection even though
     * OpenSearch's highlighter matched it. Data below mirrors the exact fragments confirmed
     * against a real OpenSearch 3.7.0 instance.
     */
    @Test
    public void test_selectByHighlightMatch_fourDotBoundaryAfterSentenceEndingPeriod_matchesBothFragments() {
        final TestableFetcher f = new TestableFetcher();
        final String chunk1 = "chunk-BBB-1 discusses gadgets, unique-marker-token appears here for testing purposes.";
        final String chunk3 = "chunk-DDD-3 explains different gadgets, unique-marker-token shows up again in this text.";
        final List<String> chunks = List.of("chunk-AAA-0 talks about the weather with no matching terms.", chunk1,
                "chunk-CCC-2 covers travel destinations with no matching terms.", chunk3);
        final Map<String, Object> doc = new LinkedHashMap<>();
        // Exactly as ViewHelper#createHighlightText would join two highlight fragments: chunk1 ends
        // in "." (a fullstop char), so joining with the 3-dot separator yields 4 consecutive dots.
        doc.put("hl_content", chunk1 + "..." + chunk3);
        final String result = f.selectByHighlightMatch(chunks, doc);
        // Before the fix: only chunk1 (index 1) was found; chunk3 (index 3) was silently dropped
        // because the stray leading "." glued onto its recovered fragment broke the contains() match.
        assertEquals(chunk1 + "\n\n" + chunk3, result);
    }

    @Test
    public void test_selectByHighlightMatch_threeDotBoundaryNoPeriod_stillMatchesBothFragments() {
        // Control case: neither fragment ends in a fullstop char, so the boundary is the normal
        // exact 3-dot separator. Confirms the fix does not regress the pre-existing working case.
        final TestableFetcher f = new TestableFetcher();
        final String chunk1 = "chunk-BBB-1 discusses gadgets, unique-marker-token appears here for testing";
        final String chunk3 = "chunk-DDD-3 explains different gadgets, unique-marker-token shows up again";
        final List<String> chunks = List.of("chunk-AAA-0 unrelated", chunk1, "chunk-CCC-2 unrelated", chunk3);
        final Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("hl_content", chunk1 + "..." + chunk3);
        final String result = f.selectByHighlightMatch(chunks, doc);
        assertEquals(chunk1 + "\n\n" + chunk3, result);
    }

    @Test
    public void test_selectByHighlightMatch_lastFragmentEndingInPeriod_isHarmless() {
        // Edge case: the fragment ending in "." is the last (and only) one, so there is no next
        // fragment to corrupt. ViewHelper#createHighlightText also does not append a trailing
        // "..." after a fragment that already ends with a fullstop char, so no extra dots appear.
        final TestableFetcher f = new TestableFetcher();
        final String chunk = "chunk-BBB-1 discusses gadgets, unique-marker-token appears here for testing purposes.";
        final List<String> chunks = List.of("chunk-AAA-0 unrelated", chunk, "chunk-CCC-2 unrelated");
        final Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("hl_content", chunk);
        final String result = f.selectByHighlightMatch(chunks, doc);
        assertEquals(chunk, result);
    }

    @Test
    public void test_selectByHighlightMatch_fragmentStartingWithLeadingDot_stillMatches() {
        // A chunk whose real text legitimately starts with a single dot (e.g. ".NET") is not part
        // of a 3+ dot run, so the \.{3,} boundary regex must not swallow it.
        final TestableFetcher f = new TestableFetcher();
        final String chunk = ".NET framework overview with a unique-marker-token for matching.";
        final List<String> chunks = List.of("chunk-AAA-0 unrelated", chunk);
        final Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("hl_content", chunk);
        final String result = f.selectByHighlightMatch(chunks, doc);
        assertEquals(chunk, result);
    }

    @Test
    public void test_selectRelevantChunks_semanticTakesPriorityOverKeyword() {
        final TestableFetcher f = new TestableFetcher();
        f.chatTopKOverride = 1;
        final List<String> chunks = List.of("apple pie recipe", "banana bread recipe", "cherry tart recipe");
        final Map<String, Object> doc = chunkedDoc("d1", chunks, List.of(List.of(0.0, 1.0), List.of(1.0, 0.0), // chunk1 has the highest similarity to [1,0]
                List.of(0.0, -1.0)));
        // Would match chunk2 if the keyword step ran -- proves semantic selection wins when it succeeds.
        doc.put("hl_content", "cherry tart recipe");
        f.selectRelevantChunks(doc, "some query", new float[] { 1.0f, 0.0f });
        assertEquals("banana bread recipe", doc.get("content"));
    }

    @Test
    public void test_selectRelevantChunks_semanticUnavailableFallsToKeywordMatch() {
        final TestableFetcher f = new TestableFetcher();
        final List<String> chunks = List.of("apple pie recipe", "banana bread recipe", "cherry tart recipe");
        final Map<String, Object> doc = chunkedDoc("d1", chunks, null);
        doc.put("hl_content", "banana bread recipe");
        f.selectRelevantChunks(doc, "banana", null); // no queryVector -> semantic step skipped
        assertEquals("banana bread recipe", doc.get("content"));
    }

    @Test
    public void test_selectRelevantChunks_noMatchFallsToWholeArrayFinalFallback() {
        final TestableFetcher f = new TestableFetcher();
        final List<String> chunks = List.of("chunk-a", "chunk-b");
        final Map<String, Object> doc = chunkedDoc("d1", chunks, null);
        doc.put("hl_content", "something-not-present-in-any-chunk");
        f.selectRelevantChunks(doc, "query", null);
        // Short content stays under the fulltext threshold, so truncateContent leaves the List as-is
        // (it only joins+truncates when the joined length exceeds maxLength -- see truncateContent).
        assertEquals(List.of("chunk-a", "chunk-b"), doc.get("content"));
    }

    @Test
    public void test_selectRelevantChunks_blankQueryExplicitlySkipsKeywordStep() {
        final TestableFetcher f = new TestableFetcher();
        final List<String> chunks = List.of("chunk-a", "chunk-b", "chunk-c");
        final Map<String, Object> doc = chunkedDoc("d1", chunks, null);
        // hl_content WOULD match chunk-b alone if the keyword step ran -- proves the blank-query
        // branch is an explicit, intentional skip (SUMMARY-intent path), not an incidental
        // "found nothing" fallthrough.
        doc.put("hl_content", "chunk-b");
        f.selectRelevantChunks(doc, "", null);
        // Short content stays under the fulltext threshold, so truncateContent leaves the List as-is.
        assertEquals(List.of("chunk-a", "chunk-b", "chunk-c"), doc.get("content"));
    }

    @Test
    public void test_selectRelevantChunks_respectsFulltextThresholdAfterSelection() {
        final TestableFetcher f = new TestableFetcher();
        f.threshold = 5L;
        final List<String> chunks = List.of("chunk-a", "chunk-b");
        final Map<String, Object> doc = chunkedDoc("d1", chunks, null);
        f.selectRelevantChunks(doc, "", null); // step 3: whole array, then truncated to 5 chars
        assertEquals("chunk", doc.get("content"));
    }

    @Test
    public void test_resolveQueryVector_blankOrNullQueryReturnsNull() {
        final TestableFetcher f = new TestableFetcher();
        assertNull(f.resolveQueryVector(""));
        assertNull(f.resolveQueryVector(null));
    }

    @Test
    public void test_resolveQueryVector_unavailableManagerReturnsNull() {
        final TestableFetcher f = new TestableFetcher();
        final FakeEmbeddingClientManager manager = new FakeEmbeddingClientManager();
        manager.available = false;
        f.embeddingManager = manager;
        assertNull(f.resolveQueryVector("query"));
    }

    // --- Fix: WARN-once (not silent, not spammy) when the embedding provider is unavailable ---

    @Test
    public void test_resolveQueryVector_unavailableManager_warnsOnceAcrossMultipleCalls() {
        final TestableFetcher f = new TestableFetcher();
        final FakeEmbeddingClientManager manager = new FakeEmbeddingClientManager();
        manager.available = false;
        f.embeddingManager = manager;

        final String loggerName = DefaultChatContentFetcher.class.getName();
        final LoggerContext ctx = (LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
        final org.apache.logging.log4j.core.config.Configuration cfg = ctx.getConfiguration();
        final org.apache.logging.log4j.core.config.LoggerConfig loggerCfg = cfg.getLoggerConfig(loggerName);
        final Level originalLevel = loggerCfg.getLevel();
        final List<LogEvent> captured = new ArrayList<>();
        final AbstractAppender listAppender =
                new AbstractAppender("test-list-appender-2", null, PatternLayout.createDefaultLayout(), true, Property.EMPTY_ARRAY) {
                    @Override
                    public void append(final LogEvent event) {
                        if (event.getLevel().isMoreSpecificThan(Level.WARN)) {
                            captured.add(event.toImmutable());
                        }
                    }
                };
        listAppender.start();
        loggerCfg.addAppender(listAppender, Level.WARN, null);
        loggerCfg.setLevel(Level.WARN);
        ctx.updateLoggers();
        try {
            // A persistently unavailable embedding provider must not silently return null forever --
            // exactly one WARN across repeated calls during the same outage, not zero and not one per call.
            for (int i = 0; i < 5; i++) {
                assertNull(f.resolveQueryVector("query"));
            }
            final long warnCount = captured.stream()
                    .filter(e -> loggerName.equals(e.getLoggerName()))
                    .filter(e -> Level.WARN.equals(e.getLevel()))
                    .filter(e -> e.getMessage().getFormattedMessage().contains("Embedding client unavailable"))
                    .count();
            org.junit.jupiter.api.Assertions.assertEquals(1L, warnCount,
                    "exactly 1 WARN must be emitted across 5 calls during the same outage; got " + warnCount);
        } finally {
            loggerCfg.removeAppender("test-list-appender-2");
            loggerCfg.setLevel(originalLevel);
            ctx.updateLoggers();
            listAppender.stop();
        }
    }

    @Test
    public void test_resolveQueryVector_unavailableManager_warnsAgainAfterRecovery() {
        final TestableFetcher f = new TestableFetcher();
        final FakeEmbeddingClientManager manager = new FakeEmbeddingClientManager();
        manager.available = false;
        manager.queryVector = new float[] { 1.0f };
        f.embeddingManager = manager;

        final String loggerName = DefaultChatContentFetcher.class.getName();
        final LoggerContext ctx = (LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
        final org.apache.logging.log4j.core.config.Configuration cfg = ctx.getConfiguration();
        final org.apache.logging.log4j.core.config.LoggerConfig loggerCfg = cfg.getLoggerConfig(loggerName);
        final Level originalLevel = loggerCfg.getLevel();
        final List<LogEvent> captured = new ArrayList<>();
        final AbstractAppender listAppender =
                new AbstractAppender("test-list-appender-3", null, PatternLayout.createDefaultLayout(), true, Property.EMPTY_ARRAY) {
                    @Override
                    public void append(final LogEvent event) {
                        if (event.getLevel().isMoreSpecificThan(Level.WARN)) {
                            captured.add(event.toImmutable());
                        }
                    }
                };
        listAppender.start();
        loggerCfg.addAppender(listAppender, Level.WARN, null);
        loggerCfg.setLevel(Level.WARN);
        ctx.updateLoggers();
        try {
            assertNull(f.resolveQueryVector("query")); // outage #1 -> 1st WARN
            manager.available = true;
            f.resolveQueryVector("query"); // recovery -> resets the latch
            manager.available = false;
            assertNull(f.resolveQueryVector("query")); // outage #2 -> must WARN again, not degrade to DEBUG forever

            final long warnCount = captured.stream()
                    .filter(e -> loggerName.equals(e.getLoggerName()))
                    .filter(e -> Level.WARN.equals(e.getLevel()))
                    .filter(e -> e.getMessage().getFormattedMessage().contains("Embedding client unavailable"))
                    .count();
            org.junit.jupiter.api.Assertions.assertEquals(2L, warnCount,
                    "a distinct later outage must surface its own WARN; got " + warnCount);
        } finally {
            loggerCfg.removeAppender("test-list-appender-3");
            loggerCfg.setLevel(originalLevel);
            ctx.updateLoggers();
            listAppender.stop();
        }
    }

    @Test
    public void test_resolveQueryVector_returnsVectorWhenAvailable() {
        final TestableFetcher f = new TestableFetcher();
        final FakeEmbeddingClientManager manager = new FakeEmbeddingClientManager();
        manager.available = true;
        manager.queryVector = new float[] { 1.0f, 2.0f };
        f.embeddingManager = manager;
        org.junit.jupiter.api.Assertions.assertArrayEquals(new float[] { 1.0f, 2.0f }, f.resolveQueryVector("query"), 0.0f);
    }

    @Test
    public void test_resolveQueryVector_embedQueryThrowsReturnsNull() {
        final TestableFetcher f = new TestableFetcher();
        final FakeEmbeddingClientManager manager = new FakeEmbeddingClientManager();
        manager.available = true;
        manager.throwOnEmbed = new RuntimeException("provider error");
        f.embeddingManager = manager;
        assertNull(f.resolveQueryVector("query"));
    }

    @Test
    public void test_applyChunkSelection_noChunkedDocsNeverEmbeds() {
        final TestableFetcher f = new TestableFetcher();
        final FakeEmbeddingClientManager manager = new FakeEmbeddingClientManager();
        manager.available = true;
        manager.queryVector = new float[] { 1.0f };
        f.embeddingManager = manager;
        final Map<String, Map<String, Object>> resultMap = new LinkedHashMap<>();
        resultMap.put("a", contentDoc("a", "plain content"));
        f.applyChunkSelection(resultMap, "query");
        assertEquals(0, manager.embedQueryCallCount);
        assertEquals("plain content", resultMap.get("a").get("content"));
    }

    @Test
    public void test_applyChunkSelection_embedsExactlyOnceAcrossMultipleChunkedDocs() {
        final TestableFetcher f = new TestableFetcher();
        f.contentChunkerEnabledOverride = true;
        f.chatTopKOverride = 1;
        final FakeEmbeddingClientManager manager = new FakeEmbeddingClientManager();
        manager.available = true;
        manager.queryVector = new float[] { 1.0f, 0.0f };
        f.embeddingManager = manager;
        final Map<String, Map<String, Object>> resultMap = new LinkedHashMap<>();
        resultMap.put("d1", chunkedDoc("d1", List.of("c1a", "c1b"), List.of(List.of(1.0, 0.0), List.of(0.0, 1.0))));
        resultMap.put("d2", chunkedDoc("d2", List.of("c2a", "c2b"), List.of(List.of(0.0, 1.0), List.of(1.0, 0.0))));
        f.applyChunkSelection(resultMap, "query");
        assertEquals(1, manager.embedQueryCallCount);
        assertEquals("c1a", resultMap.get("d1").get("content"));
        assertEquals("c2b", resultMap.get("d2").get("content"));
    }

    @Test
    public void test_applyChunkSelection_ignoresNonDoneStatus() {
        final TestableFetcher f = new TestableFetcher();
        f.contentChunkerEnabledOverride = true;
        final Map<String, Map<String, Object>> resultMap = new LinkedHashMap<>();
        final Map<String, Object> failedDoc = chunkedDoc("a", List.of("x", "y"), null);
        failedDoc.put(Constants.CONTENT_CHUNK_STATUS_FIELD, Constants.FAIL);
        resultMap.put("a", failedDoc);
        f.applyChunkSelection(resultMap, "query");
        assertEquals(List.of("x", "y"), resultMap.get("a").get("content")); // untouched -- not STATUS_DONE
    }

    @Test
    public void test_applyChunkSelection_disabledSkipsEvenStaleDoneStatusDoc() {
        // A document chunked while content_chunker.enabled=true earlier can retain a stale
        // content_chunk_status=done after the feature is later disabled. applyChunkSelection()
        // must not use its chunk data in that case -- defense-in-depth beyond the (today
        // sufficient) field-omission gate in buildContentFields()/getResponseFields().
        final TestableFetcher f = new TestableFetcher();
        f.contentChunkerEnabledOverride = false;
        final FakeEmbeddingClientManager manager = new FakeEmbeddingClientManager();
        manager.available = true;
        manager.queryVector = new float[] { 1.0f, 0.0f };
        f.embeddingManager = manager;
        final Map<String, Map<String, Object>> resultMap = new LinkedHashMap<>();
        resultMap.put("a", chunkedDoc("a", List.of("x", "y"), List.of(List.of(1.0, 0.0), List.of(0.0, 1.0))));
        f.applyChunkSelection(resultMap, "query");
        assertEquals("must not embed the query when chunking is disabled", 0, manager.embedQueryCallCount);
        assertEquals("stale chunk data must not be selected while disabled", List.of("x", "y"), resultMap.get("a").get("content"));
    }

    @Test
    public void test_normalizeHighlightedDocs_preservesChunkedDocRawEvenWithoutSnippet() {
        final TestableFetcher f = new TestableFetcher();
        final Map<String, Object> chunkedNoSnippet = chunkedDoc("a", List.of("x", "y"), null);
        // No hl_content at all -- would be DROPPED by the pre-existing (non-chunked) logic, but a
        // chunked document must be preserved raw so the chunk-selection pass can still choose its content.
        final List<Map<String, Object>> out = f.normalizeHighlightedDocs(new ArrayList<>(List.of(chunkedNoSnippet)));
        assertEquals(1, out.size());
        assertEquals(List.of("x", "y"), out.get(0).get("content"));
    }

    @Test
    public void test_normalizeHighlightedDocs_preservesChunkedDocRawEvenWithSnippet() {
        final TestableFetcher f = new TestableFetcher();
        final Map<String, Object> chunked = chunkedDoc("a", List.of("x", "y"), null);
        chunked.put("hl_content", "some snippet");
        final List<Map<String, Object>> out = f.normalizeHighlightedDocs(new ArrayList<>(List.of(chunked)));
        assertEquals(1, out.size());
        // "content" must NOT be overwritten with the hl_content snippet for a chunked document.
        assertEquals(List.of("x", "y"), out.get(0).get("content"));
    }

    @Test
    public void test_toFloatArray_handlesListOfDoubleAndFloatArrayAndInvalid() {
        final TestableFetcher f = new TestableFetcher();
        org.junit.jupiter.api.Assertions.assertArrayEquals(new float[] { 1.0f, 2.5f }, f.toFloatArray(List.of(1.0, 2.5)), 0.0f);
        org.junit.jupiter.api.Assertions.assertArrayEquals(new float[] { 3.0f, 4.0f }, f.toFloatArray(new float[] { 3.0f, 4.0f }), 0.0f);
        assertNull(f.toFloatArray("not-a-vector"));
        assertNull(f.toFloatArray(List.of("not", "numbers")));
    }

    @Test
    public void test_buildContentFields_disabledLeavesConfiguredFieldsUnchanged() {
        final TestableFetcher f = new TestableFetcher();
        f.contentChunkerEnabledOverride = false;
        final org.codelibs.fess.mylasta.direction.FessConfig fessConfig = org.codelibs.fess.util.ComponentUtil.getFessConfig();
        final String[] fields = f.buildContentFields(fessConfig);
        assertEquals(List.of(fessConfig.getRagChatContentFields().split(",")), List.of(fields));
    }

    @Test
    public void test_buildContentFields_enabledAppendsChunkFields() {
        final TestableFetcher f = new TestableFetcher();
        f.contentChunkerEnabledOverride = true;
        final org.codelibs.fess.mylasta.direction.FessConfig fessConfig = org.codelibs.fess.util.ComponentUtil.getFessConfig();
        final List<String> fields = List.of(f.buildContentFields(fessConfig));
        assertTrue(fields.contains(Constants.CONTENT_CHUNK_VECTOR_FIELD));
        assertTrue(fields.contains(Constants.CONTENT_CHUNK_STATUS_FIELD));
    }

    @Test
    public void test_fetchContent_chunkedDocInFullBucketGetsSemanticSelection() {
        final TestableFetcher f = new TestableFetcher();
        f.contentChunkerEnabledOverride = true;
        f.chatTopKOverride = 1;
        final FakeEmbeddingClientManager manager = new FakeEmbeddingClientManager();
        manager.available = true;
        manager.queryVector = new float[] { 1.0f, 0.0f };
        f.embeddingManager = manager;
        final Map<String, Object> chunked =
                chunkedDoc("small", List.of("about cats", "about dogs"), List.of(List.of(0.0, 1.0), List.of(1.0, 0.0))); // "about dogs" matches the query vector
        f.fullResult = new ArrayList<>(List.of(chunked));
        final ChatContentRequest req = new ChatContentRequest(List.of("small"), List.of(doc("small", 100L)), "tell me about dogs");

        final List<Map<String, Object>> out = f.fetchContent(req);

        assertEquals(1, out.size());
        assertEquals("about dogs", out.get(0).get("content"));
    }

    @Test
    public void test_fetchContent_nonChunkedDocsUnaffectedByChunkSelection() {
        final TestableFetcher f = new TestableFetcher();
        final FakeEmbeddingClientManager manager = new FakeEmbeddingClientManager();
        manager.available = true;
        manager.queryVector = new float[] { 1.0f };
        f.embeddingManager = manager;
        final List<String> docIds = List.of("small", "big");
        final List<Map<String, Object>> searchResults = List.of(doc("small", 100L), doc("big", 99999L));
        final List<Map<String, Object>> out = f.fetchContent(new ChatContentRequest(docIds, searchResults, "q"));

        assertEquals(0, manager.embedQueryCallCount); // no chunked docs present -> never embeds
        assertEquals("FULL:small", out.get(0).get("content"));
        assertEquals("HL:big", out.get(1).get("content"));
    }

    // ===== Vector-less chunked docs (content_chunk_status=chunked / done-without-vectors) =====

    @Test
    public void test_fetchContent_smallChunkOnlyDoc_selectsByHighlightViaSecondaryFetch() {
        // State 2, FULL path: a small chunk-array doc without vectors has no hl_content after the
        // full fetch; one additional highlight fetch supplies the snippet for keyword selection.
        final TestableFetcher f = new TestableFetcher();
        f.contentChunkerEnabledOverride = true;
        final FakeEmbeddingClientManager manager = new FakeEmbeddingClientManager();
        manager.available = true;
        manager.queryVector = new float[] { 1.0f, 0.0f };
        f.embeddingManager = manager;
        f.fullResult = new ArrayList<>(List.of(chunkOnlyDoc("small", List.of("about apples", "about bananas"))));
        final Map<String, Object> hlDoc = new LinkedHashMap<>();
        hlDoc.put("doc_id", "small");
        hlDoc.put("hl_content", "about bananas");
        f.highlightResult = new ArrayList<>(List.of(hlDoc));
        final ChatContentRequest req = new ChatContentRequest(List.of("small"), List.of(doc("small", 100L)), "bananas");

        final List<Map<String, Object>> out = f.fetchContent(req);

        assertEquals(1, out.size());
        assertEquals("about bananas", out.get(0).get("content")); // highlight-based chunk selection
        assertEquals(List.of("small"), f.highlightCalledWith); // exactly one secondary highlight fetch
        assertEquals("a vector-less batch must not embed the query", 0, manager.embedQueryCallCount);
    }

    @Test
    public void test_fetchContent_largeChunkOnlyDoc_selectsByExistingHlContentWithoutSecondaryFetch() {
        // State 2, HIGHLIGHT path: a large chunk-only doc comes back from the primary highlight
        // fetch preserved raw WITH its hl_content (normalizeHighlightedDocs preserve branch); the
        // existing snippet drives keyword selection -- no additional highlight fetch is issued.
        final TestableFetcher f = new TestableFetcher();
        f.contentChunkerEnabledOverride = true;
        final FakeEmbeddingClientManager manager = new FakeEmbeddingClientManager();
        manager.available = true;
        manager.queryVector = new float[] { 1.0f, 0.0f };
        f.embeddingManager = manager;
        final Map<String, Object> chunked = chunkOnlyDoc("big", List.of("about apples", "about bananas"));
        chunked.put("hl_content", "about bananas");
        f.highlightResult = new ArrayList<>(List.of(chunked));
        final ChatContentRequest req = new ChatContentRequest(List.of("big"), List.of(doc("big", 99999L)), "bananas");

        final List<Map<String, Object>> out = f.fetchContent(req);

        assertEquals(1, out.size());
        assertEquals("about bananas", out.get(0).get("content"));
        assertEquals(List.of("big"), f.highlightCalledWith); // primary fetch only, no secondary
        assertTrue(f.fullCalledWith.isEmpty());
        assertEquals(0, manager.embedQueryCallCount);
    }

    @Test
    public void test_fetchContent_largeChunkOnlyDocHighlightMiss_noSecondaryFetch_wholeArrayTruncate() {
        // A large chunk-only doc entirely missing from the primary highlight results is reconciled
        // via the full-fetch fallback WITHOUT being truncated there (isChunkedStatus guard), and is
        // NOT re-queried (an identical re-query would miss again); it falls to the whole-array
        // join+truncate last resort.
        final TestableFetcher f = new TestableFetcher();
        f.contentChunkerEnabledOverride = true;
        f.threshold = 5L;
        f.highlightResult = new ArrayList<>(); // primary highlight miss
        f.fullResult = new ArrayList<>(List.of(chunkOnlyDoc("big", List.of("chunk-a", "chunk-b"))));
        final FakeEmbeddingClientManager manager = new FakeEmbeddingClientManager();
        manager.available = true;
        manager.queryVector = new float[] { 1.0f };
        f.embeddingManager = manager;
        final ChatContentRequest req = new ChatContentRequest(List.of("big"), List.of(doc("big", 99999L)), "q");

        final List<Map<String, Object>> out = f.fetchContent(req);

        assertEquals(1, out.size());
        assertEquals("chunk", out.get(0).get("content")); // "chunk-a\n\nchunk-b" truncated to 5
        assertEquals("no secondary highlight fetch for an already-missed doc", List.of("big"), f.highlightCalledWith);
        assertEquals(0, manager.embedQueryCallCount);
    }

    @Test
    public void test_fetchContent_smallDoneDocWithVectors_semanticSelectionWithoutSecondaryFetch() {
        // State 3, FULL path: DONE with usable vectors keeps the existing cosine top-K selection;
        // the secondary highlight fetch must not fire for it.
        final TestableFetcher f = new TestableFetcher();
        f.contentChunkerEnabledOverride = true;
        f.chatTopKOverride = 1;
        final FakeEmbeddingClientManager manager = new FakeEmbeddingClientManager();
        manager.available = true;
        manager.queryVector = new float[] { 1.0f, 0.0f };
        f.embeddingManager = manager;
        final Map<String, Object> chunked =
                chunkedDoc("small", List.of("about cats", "about dogs"), List.of(List.of(0.0, 1.0), List.of(1.0, 0.0)));
        f.fullResult = new ArrayList<>(List.of(chunked));
        final ChatContentRequest req = new ChatContentRequest(List.of("small"), List.of(doc("small", 100L)), "tell me about dogs");

        final List<Map<String, Object>> out = f.fetchContent(req);

        assertEquals(1, out.size());
        assertEquals("about dogs", out.get(0).get("content"));
        assertEquals(1, manager.embedQueryCallCount);
        assertTrue("no secondary highlight fetch when vectors are usable", f.highlightCalledWith.isEmpty());
    }

    @Test
    public void test_fetchContent_largeDoneDocWithVectors_semanticSelectionUnchanged() {
        // State 3, HIGHLIGHT path: a large DONE doc preserved raw by the highlight phase still
        // selects by cosine similarity; no secondary fetch.
        final TestableFetcher f = new TestableFetcher();
        f.contentChunkerEnabledOverride = true;
        f.chatTopKOverride = 1;
        final FakeEmbeddingClientManager manager = new FakeEmbeddingClientManager();
        manager.available = true;
        manager.queryVector = new float[] { 1.0f, 0.0f };
        f.embeddingManager = manager;
        final Map<String, Object> chunked =
                chunkedDoc("big", List.of("about cats", "about dogs"), List.of(List.of(0.0, 1.0), List.of(1.0, 0.0)));
        f.highlightResult = new ArrayList<>(List.of(chunked));
        final ChatContentRequest req = new ChatContentRequest(List.of("big"), List.of(doc("big", 99999L)), "tell me about dogs");

        final List<Map<String, Object>> out = f.fetchContent(req);

        assertEquals(1, out.size());
        assertEquals("about dogs", out.get(0).get("content"));
        assertEquals(1, manager.embedQueryCallCount);
        assertEquals(List.of("big"), f.highlightCalledWith); // primary fetch only
    }

    @Test
    public void test_fetchContent_doneDocWithoutUsableVectors_fallsBackToKeywordWithoutEmbedding() {
        // Defensive state: status=done but the vector field is missing/unusable. The secondary
        // highlight fetch supplies the snippet for keyword selection, and the query is NOT
        // embedded (no doc in the batch can use the vector).
        final TestableFetcher f = new TestableFetcher();
        f.contentChunkerEnabledOverride = true;
        final FakeEmbeddingClientManager manager = new FakeEmbeddingClientManager();
        manager.available = true;
        manager.queryVector = new float[] { 1.0f, 0.0f };
        f.embeddingManager = manager;
        f.fullResult = new ArrayList<>(List.of(chunkedDoc("small", List.of("about apples", "about bananas"), null))); // done, no vectors
        final Map<String, Object> hlDoc = new LinkedHashMap<>();
        hlDoc.put("doc_id", "small");
        hlDoc.put("hl_content", "about apples");
        f.highlightResult = new ArrayList<>(List.of(hlDoc));
        final ChatContentRequest req = new ChatContentRequest(List.of("small"), List.of(doc("small", 100L)), "apples");

        final List<Map<String, Object>> out = f.fetchContent(req);

        assertEquals(1, out.size());
        assertEquals("about apples", out.get(0).get("content"));
        assertEquals(List.of("small"), f.highlightCalledWith);
        assertEquals("done-without-vectors must not embed the query", 0, manager.embedQueryCallCount);
    }

    @Test
    public void test_fetchContent_nullQueryChunkOnlyDoc_wholeArrayTruncateWithoutSecondaryFetch() {
        // Null-query summary path: chunked docs fall straight to the whole-array truncate; no
        // secondary highlight fetch, no embedding, no NPE.
        final TestableFetcher f = new TestableFetcher();
        f.contentChunkerEnabledOverride = true;
        f.threshold = 5L;
        final FakeEmbeddingClientManager manager = new FakeEmbeddingClientManager();
        manager.available = true;
        manager.queryVector = new float[] { 1.0f };
        f.embeddingManager = manager;
        f.fullResult = new ArrayList<>(List.of(chunkOnlyDoc("small", List.of("chunk-a", "chunk-b"))));

        final List<Map<String, Object>> out = f.fetchContent(new ChatContentRequest(List.of("small"), List.of(), null));

        assertEquals(1, out.size());
        assertEquals("chunk", out.get(0).get("content")); // "chunk-a\n\nchunk-b" truncated to 5
        assertTrue("blank query must skip the secondary highlight fetch", f.highlightCalledWith.isEmpty());
        assertEquals(0, manager.embedQueryCallCount);
    }

    @Test
    public void test_fetchContent_chunkerDisabled_noChunkProcessingForChunkOnlyDoc() {
        // Chunker disabled: no chunk selection and no secondary fetch, even for a doc that still
        // carries a stale chunk array + status from when chunking was enabled.
        final TestableFetcher f = new TestableFetcher();
        f.contentChunkerEnabledOverride = false;
        final FakeEmbeddingClientManager manager = new FakeEmbeddingClientManager();
        manager.available = true;
        manager.queryVector = new float[] { 1.0f };
        f.embeddingManager = manager;
        f.fullResult = new ArrayList<>(List.of(chunkOnlyDoc("small", List.of("chunk-a", "chunk-b"))));

        final List<Map<String, Object>> out = f.fetchContent(new ChatContentRequest(List.of("small"), List.of(doc("small", 100L)), "q"));

        assertEquals(1, out.size());
        assertEquals(List.of("chunk-a", "chunk-b"), out.get(0).get("content")); // untouched
        assertTrue(f.highlightCalledWith.isEmpty());
        assertEquals(0, manager.embedQueryCallCount);
    }

    @Test
    public void test_applyChunkSelection_chunkedStatusSelectsByKeywordWithoutEmbedding() {
        final TestableFetcher f = new TestableFetcher();
        f.contentChunkerEnabledOverride = true;
        final FakeEmbeddingClientManager manager = new FakeEmbeddingClientManager();
        manager.available = true;
        manager.queryVector = new float[] { 1.0f, 0.0f };
        f.embeddingManager = manager;
        final Map<String, Map<String, Object>> resultMap = new LinkedHashMap<>();
        final Map<String, Object> chunkOnly = chunkOnlyDoc("a", List.of("about apples", "about bananas"));
        chunkOnly.put("hl_content", "about bananas");
        resultMap.put("a", chunkOnly);
        f.applyChunkSelection(resultMap, "bananas");
        assertEquals("no embedding round-trip when no doc has usable vectors", 0, manager.embedQueryCallCount);
        assertEquals("about bananas", resultMap.get("a").get("content"));
    }

    @Test
    public void test_applyChunkSelection_mixedVectorAndVectorlessDocs() {
        // One done-with-vectors doc and one chunk-only doc in the same batch: the query is
        // embedded once (for the vector doc); the vector-less doc still selects by keyword.
        final TestableFetcher f = new TestableFetcher();
        f.contentChunkerEnabledOverride = true;
        f.chatTopKOverride = 1;
        final FakeEmbeddingClientManager manager = new FakeEmbeddingClientManager();
        manager.available = true;
        manager.queryVector = new float[] { 1.0f, 0.0f };
        f.embeddingManager = manager;
        final Map<String, Map<String, Object>> resultMap = new LinkedHashMap<>();
        resultMap.put("d1", chunkedDoc("d1", List.of("c1a", "c1b"), List.of(List.of(0.0, 1.0), List.of(1.0, 0.0))));
        final Map<String, Object> chunkOnly = chunkOnlyDoc("d2", List.of("c2a", "c2b"));
        chunkOnly.put("hl_content", "c2a");
        resultMap.put("d2", chunkOnly);
        f.applyChunkSelection(resultMap, "query");
        assertEquals(1, manager.embedQueryCallCount);
        assertEquals("c1b", resultMap.get("d1").get("content")); // semantic
        assertEquals("c2a", resultMap.get("d2").get("content")); // keyword
    }

    @Test
    public void test_normalizeHighlightedDocs_preservesChunkOnlyDocRawKeepingSnippetField() {
        final TestableFetcher f = new TestableFetcher();
        final Map<String, Object> chunkOnly = chunkOnlyDoc("a", List.of("x", "y"));
        chunkOnly.put("hl_content", "some snippet");
        final Map<String, Object> chunkOnlyNoSnippet = chunkOnlyDoc("b", List.of("p", "q"));
        final List<Map<String, Object>> out = f.normalizeHighlightedDocs(new ArrayList<>(List.of(chunkOnly, chunkOnlyNoSnippet)));
        // status=chunked docs are preserved raw like status=done ones: content is never overwritten
        // with the snippet, the raw hl_content stays available for keyword chunk selection, and a
        // highlight miss does not drop the doc.
        assertEquals(2, out.size());
        assertEquals(List.of("x", "y"), out.get(0).get("content"));
        assertEquals("some snippet", out.get(0).get("hl_content"));
        assertEquals(List.of("p", "q"), out.get(1).get("content"));
    }

    @Test
    public void test_hasUsableChunkVectors() {
        final TestableFetcher f = new TestableFetcher();
        assertTrue(f.hasUsableChunkVectors(chunkedDoc("a", List.of("x"), List.of(List.of(1.0, 0.0)))));
        assertFalse(f.hasUsableChunkVectors(chunkedDoc("a", List.of("x"), null))); // no vector field
        assertFalse(f.hasUsableChunkVectors(chunkOnlyDoc("a", List.of("x"))));
        final Map<String, Object> malformed = chunkedDoc("a", List.of("x"), null);
        malformed.put(Constants.CONTENT_CHUNK_VECTOR_FIELD, "not-a-list");
        assertFalse(f.hasUsableChunkVectors(malformed));
        final Map<String, Object> badEntries = chunkedDoc("a", List.of("x"), null);
        badEntries.put(Constants.CONTENT_CHUNK_VECTOR_FIELD, List.of("not-a-map"));
        assertFalse(f.hasUsableChunkVectors(badEntries));
    }

    @Test
    public void test_isChunkedStatus() {
        final TestableFetcher f = new TestableFetcher();
        assertTrue(f.isChunkedStatus(chunkedDoc("a", List.of("x"), null))); // done
        assertTrue(f.isChunkedStatus(chunkOnlyDoc("a", List.of("x")))); // chunked
        assertFalse(f.isChunkedStatus(contentDoc("a", "raw"))); // no status
        final Map<String, Object> skipped = contentDoc("a", "raw");
        skipped.put(Constants.CONTENT_CHUNK_STATUS_FIELD, Constants.SKIPPED);
        assertFalse(f.isChunkedStatus(skipped));
        final Map<String, Object> failed = contentDoc("a", "raw");
        failed.put(Constants.CONTENT_CHUNK_STATUS_FIELD, Constants.FAIL);
        assertFalse(f.isChunkedStatus(failed));
    }

    @Test
    public void test_answerHighlightSearchParams_getResponseFields_disabledByDefault() {
        final org.codelibs.fess.mylasta.direction.FessConfig fessConfig = org.codelibs.fess.util.ComponentUtil.getFessConfig();
        registerQueryFieldConfig();
        final DefaultChatContentFetcher.AnswerHighlightSearchParams params =
                new DefaultChatContentFetcher.AnswerHighlightSearchParams("q", 1, fessConfig, new String[0]);
        final List<String> base = List.of(org.codelibs.fess.util.ComponentUtil.getQueryFieldConfig().getResponseFields());
        assertEquals(base, List.of(params.getResponseFields()));
    }

    @Test
    public void test_answerHighlightSearchParams_getResponseFields_appendsChunkFieldsWhenEnabled() {
        final org.codelibs.fess.mylasta.direction.FessConfig fessConfig = org.codelibs.fess.util.ComponentUtil.getFessConfig();
        registerQueryFieldConfig();
        fessConfig.setSystemProperty(AbstractEmbeddingClient.CONTENT_CHUNKER_ENABLED_PROPERTY, "true");
        try {
            final DefaultChatContentFetcher.AnswerHighlightSearchParams params =
                    new DefaultChatContentFetcher.AnswerHighlightSearchParams("q", 1, fessConfig, new String[0]);
            final List<String> fields = List.of(params.getResponseFields());
            assertTrue(fields.contains(fessConfig.getIndexFieldContent()));
            assertTrue(fields.contains(Constants.CONTENT_CHUNK_VECTOR_FIELD));
            assertTrue(fields.contains(Constants.CONTENT_CHUNK_STATUS_FIELD));
        } finally {
            fessConfig.setSystemProperty(AbstractEmbeddingClient.CONTENT_CHUNKER_ENABLED_PROPERTY, null);
        }
    }

    /**
     * {@code queryFieldConfig} is not wired in the minimal {@code test_app.xml} DI set; register a
     * real instance (and run its @PostConstruct init) so {@code SearchRequestParams#getResponseFields}
     * (the super call {@link DefaultChatContentFetcher.AnswerHighlightSearchParams#getResponseFields}
     * delegates to) resolves against real config-derived field names instead of throwing.
     */
    private static void registerQueryFieldConfig() {
        if (org.codelibs.fess.util.ComponentUtil.hasComponent("queryFieldConfig")) {
            return;
        }
        final org.codelibs.fess.query.QueryFieldConfig queryFieldConfig = new org.codelibs.fess.query.QueryFieldConfig();
        queryFieldConfig.init();
        org.codelibs.fess.util.ComponentUtil.register(queryFieldConfig, "queryFieldConfig");
    }
}
