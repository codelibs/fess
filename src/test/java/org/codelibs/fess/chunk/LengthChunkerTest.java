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
package org.codelibs.fess.chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.codelibs.fess.Constants;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class LengthChunkerTest extends UnitFessTestCase {

    private TestableLengthChunker chunker;

    @Override
    public void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        chunker = new TestableLengthChunker();
    }

    @Test
    public void test_getName() {
        assertEquals("length", chunker.getName());
    }

    // Literal pin: these system-property keys are external operator configuration; the raw VALUES
    // are pinned so silent drift reddens a test instead of orphaning existing config.
    @Test
    public void test_externalContractLiterals() {
        assertEquals("content_chunker.length.chunk_size", LengthChunker.CHUNK_SIZE_PROPERTY);
        assertEquals("content_chunker.length.overlap", LengthChunker.OVERLAP_PROPERTY);
        assertEquals("content_chunker.length.boundary.enabled", LengthChunker.BOUNDARY_ENABLED_PROPERTY);
        assertEquals("content_chunker.length.boundary.lookback_percent", LengthChunker.LOOKBACK_PERCENT_PROPERTY);
        assertEquals("content_chunker.length.boundary.lookahead_percent", LengthChunker.LOOKAHEAD_PERCENT_PROPERTY);
        assertEquals("chunkBoundaryFinder", LengthChunker.BOUNDARY_FINDER_COMPONENT);
    }

    // ===================================================================================
    //                                                                boundary configuration
    //                                                                ======================

    @Test
    public void test_boundaryDefaults() {
        assertTrue(LengthChunker.DEFAULT_BOUNDARY_ENABLED, "boundary adjustment ships enabled");
        assertEquals(20, LengthChunker.DEFAULT_LOOKBACK_PERCENT);
        assertEquals(5, LengthChunker.DEFAULT_LOOKAHEAD_PERCENT);
        assertEquals(50, LengthChunker.MAX_LOOKBACK_PERCENT);
        assertEquals(25, LengthChunker.MAX_LOOKAHEAD_PERCENT);
    }

    @Test
    public void test_getBoundaryFinder_fallsBackWhenComponentMissing() {
        // test_app.xml does not include fess_chunk.xml, so the component is absent by default.
        assertFalse(ComponentUtil.hasComponent(LengthChunker.BOUNDARY_FINDER_COMPONENT),
                "precondition: chunkBoundaryFinder must not be registered");
        final ChunkBoundaryFinder fallback = chunker.exposedBoundaryFinder();
        assertNotNull(fallback, "must fall back to a default finder");
        // The documented contract is a SHARED stateless singleton, not merely "non-null": the
        // finder is resolved once per split() call, so handing back a fresh instance every time
        // would allocate one per document for no reason.
        assertSame(fallback, chunker.exposedBoundaryFinder(), "the fallback finder must be a shared singleton");
    }

    @Test
    public void test_getBoundaryFinder_usesRegisteredComponent() {
        // The DI seam: a deployment swaps the boundary rules by registering its own finder.
        final ChunkBoundaryFinder stub = new ChunkBoundaryFinder();
        try {
            ComponentUtil.register(stub, LengthChunker.BOUNDARY_FINDER_COMPONENT);
            assertSame(stub, chunker.exposedBoundaryFinder());
        } finally {
            // The UTFlute container is shared across test classes in the same JVM (CI runs
            // -Dparallel=classes -DreuseForks=true), so the stub must not leak.
            ComponentUtil.register(new ChunkBoundaryFinder(), LengthChunker.BOUNDARY_FINDER_COMPONENT);
        }
    }

    @Test
    public void test_warnOnOverlapSideEffect_warnsWhenOverlapPositive() {
        // Overlapped text is duplicated inside the searchable content array (BM25
        // term-frequency inflation + repeated highlights); a positive overlap must
        // emit a one-time WARN at registration so operators enable it knowingly.
        chunker.setTestOverlap(3);
        final LogCapturingAppender capture = LogCapturingAppender.attach(LengthChunker.class);
        try {
            chunker.warnOnOverlapSideEffect();
            assertTrue(capture.warnings().stream().anyMatch(m -> m.contains(LengthChunker.OVERLAP_PROPERTY) && m.contains("BM25")),
                    "a positive overlap must WARN about the BM25/highlight duplication side effect: " + capture.warnings());
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_warnOnOverlapSideEffect_silentWhenOverlapZero() {
        chunker.setTestOverlap(0);
        final LogCapturingAppender capture = LogCapturingAppender.attach(LengthChunker.class);
        try {
            chunker.warnOnOverlapSideEffect();
            assertTrue(capture.warnings().isEmpty(), "no WARN expected for the default overlap of 0: " + capture.warnings());
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_split_nullContent_returnsEmptyList() {
        assertTrue(chunker.split(null).isEmpty(), "null content should yield empty list");
    }

    @Test
    public void test_split_blankContent_returnsEmptyList() {
        assertTrue(chunker.split("   ").isEmpty(), "blank content should yield empty list");
    }

    @Test
    public void test_split_shorterThanChunkSize_returnsSingleChunk() {
        chunker.setTestChunkSize(800);
        chunker.setTestOverlap(0);
        final List<String> chunks = chunker.split("short content");
        assertEquals(1, chunks.size());
        assertEquals("short content", chunks.get(0));
    }

    @Test
    public void test_split_exactMultipleOfChunkSize_noTrailingEmptyChunk() {
        chunker.setTestChunkSize(10);
        chunker.setTestOverlap(0);
        final String content = "a".repeat(20);
        final List<String> chunks = chunker.split(content);
        assertEquals(2, chunks.size());
        assertEquals("a".repeat(10), chunks.get(0));
        assertEquals("a".repeat(10), chunks.get(1));
    }

    @Test
    public void test_split_noOverlap_reconstructsOriginalExactly() {
        chunker.setTestChunkSize(7);
        chunker.setTestOverlap(0);
        final String content = "abcdefghijklmnopqrstuvwxyz";
        final List<String> chunks = chunker.split(content);
        assertEquals(content, String.join("", chunks));
    }

    @Test
    public void test_split_withOverlap_producesOverlappingChunks() {
        chunker.setTestChunkSize(10);
        chunker.setTestOverlap(3);
        final String content = "a".repeat(20);
        final List<String> chunks = chunker.split(content);
        assertEquals(3, chunks.size());
        assertEquals("a".repeat(10), chunks.get(0));
        assertEquals("a".repeat(10), chunks.get(1));
        assertEquals("a".repeat(6), chunks.get(2));
    }

    @Test
    public void test_split_overlapGreaterThanOrEqualToChunkSize_clampsToZeroAndTerminates() {
        chunker.setTestChunkSize(5);
        chunker.setTestOverlap(5);
        final String content = "a".repeat(17);
        final List<String> chunks = chunker.split(content);
        // Clamped to overlap=0 behavior: ceil(17/5) = 4 chunks, no infinite loop.
        assertEquals(4, chunks.size());
        assertEquals(content, String.join("", chunks));
    }

    @Test
    public void test_split_doesNotSplitSurrogatePairAtNaturalBoundary() {
        chunker.setTestChunkSize(10);
        chunker.setTestOverlap(0);
        // 9 'A's + a surrogate pair (U+1F600) straddling index 9/10 + 'B'.
        final String emoji = new String(Character.toChars(0x1F600));
        final String content = "A".repeat(9) + emoji + "B";
        final List<String> chunks = chunker.split(content);
        // Lossless reconstruction is the strongest possible assertion: if any
        // chunk boundary fell inside the surrogate pair, substring() would have
        // thrown, or the pair's two chars would land in different chunks such
        // that concatenation still equals the original (Java allows lone
        // surrogates in a String) -- so we additionally assert no chunk starts
        // or ends with a lone half of the pair.
        assertEquals(content, String.join("", chunks));
        for (final String chunk : chunks) {
            if (!chunk.isEmpty()) {
                assertFalse(Character.isLowSurrogate(chunk.charAt(0)), "chunk must not start with a lone low surrogate: " + chunk);
                assertFalse(Character.isHighSurrogate(chunk.charAt(chunk.length() - 1)),
                        "chunk must not end with a lone high surrogate: " + chunk);
            }
        }
    }

    @Test
    public void test_split_chunkSizeOne_flooredToMinimum_doesNotDropSurrogatePairChar() {
        chunker.setTestChunkSize(1);
        chunker.setTestOverlap(0);
        // Regression for a confirmed bug: chunk_size=1 made the surrogate-pair
        // decrement (end--) collide with the "guarantee forward progress"
        // fallback (end = start + 1), re-landing the boundary exactly on the
        // split it was meant to avoid and silently dropping the low surrogate
        // (joined length 3 instead of 4). chunk_size is now floored at 2,
        // which makes that collision mathematically unreachable.
        final String emoji = new String(Character.toChars(0x1F600));
        final String content = emoji + "XY";
        final List<String> chunks = chunker.split(content);
        assertEquals(content, String.join("", chunks));
        for (final String chunk : chunks) {
            if (!chunk.isEmpty()) {
                assertFalse(Character.isLowSurrogate(chunk.charAt(0)), "chunk must not start with a lone low surrogate: " + chunk);
                assertFalse(Character.isHighSurrogate(chunk.charAt(chunk.length() - 1)),
                        "chunk must not end with a lone high surrogate: " + chunk);
            }
        }
    }

    @Test
    public void test_split_chunkSizeOne_withBackToBackSurrogatePairs_reconstructsLosslessly() {
        chunker.setTestChunkSize(1);
        chunker.setTestOverlap(0);
        // Adjacent surrogate pairs (no plain chars between them) are the
        // worst case for the chunk_size floor; verify it still holds.
        final String emoji = new String(Character.toChars(0x1F600));
        final String content = emoji.repeat(3);
        final List<String> chunks = chunker.split(content);
        assertEquals(content, String.join("", chunks));
    }

    @Test
    public void test_split_chunkSizeAboveMaximum_clampsToMaximum() {
        // A pathological config (e.g. content_chunker.length.chunk_size far larger than any
        // realistic value) must be clamped to MAX_CHUNK_SIZE rather than allocating an absurdly
        // large chunk substring (OOM risk). With the ceiling, an oversized chunk size is capped
        // at MAX_CHUNK_SIZE, so content just over that size spills into a second chunk instead of
        // being swallowed whole.
        chunker.setTestChunkSize(LengthChunker.MAX_CHUNK_SIZE * 2);
        chunker.setTestOverlap(0);
        final String content = "a".repeat(LengthChunker.MAX_CHUNK_SIZE + 10);
        final List<String> chunks = chunker.split(content);
        assertEquals(2, chunks.size());
        assertEquals(LengthChunker.MAX_CHUNK_SIZE, chunks.get(0).length());
        assertEquals(10, chunks.get(1).length());
        assertEquals(content, String.join("", chunks));
    }

    // ===================================================================================
    //                                        bounded chunk production (split(String, int))
    //                                        ================================================
    // ChunkVectorHelper only needs to know whether a document exceeds
    // content_chunker.max_chunks_per_document, so it asks for cap+1 chunks. LengthChunker must
    // stop PRODUCING substrings there rather than splitting the whole document and discarding the
    // excess -- an oversized document about to be marked "skipped" must never materialize its
    // full chunk list in the chunk-indexer child JVM's small heap.

    @Test
    public void test_splitWithLimit_returnsExactlyTheUnboundedPrefix() {
        chunker.setTestChunkSize(4);
        chunker.setTestOverlap(0);
        final String content = "abcdefghijklmnopqrst"; // 20 chars -> 5 chunks of 4
        final List<String> unbounded = chunker.split(content);
        assertEquals(5, unbounded.size());
        for (int limit = 1; limit <= 5; limit++) {
            final List<String> bounded = chunker.split(content, limit);
            assertEquals(limit, bounded.size(), "limit=" + limit);
            assertEquals("bounded split must equal the unbounded prefix. limit=" + limit, unbounded.subList(0, limit), bounded);
        }
    }

    @Test
    public void test_splitWithLimit_limitAboveChunkCount_returnsEveryChunk() {
        chunker.setTestChunkSize(4);
        chunker.setTestOverlap(0);
        final String content = "abcdefgh"; // 2 chunks
        assertEquals(List.of("abcd", "efgh"), chunker.split(content, 100));
    }

    @Test
    public void test_splitWithLimit_nonPositiveLimit_returnsEmptyList() {
        chunker.setTestChunkSize(4);
        assertTrue(chunker.split("abcdefgh", 0).isEmpty(), "limit=0 must produce nothing");
        assertTrue(chunker.split("abcdefgh", -1).isEmpty(), "a negative limit must produce nothing");
    }

    @Test
    public void test_splitWithLimit_blankContent_returnsEmptyList() {
        assertTrue(chunker.split("   ", 5).isEmpty());
        assertTrue(chunker.split(null, 5).isEmpty());
    }

    @Test
    public void test_splitWithLimit_withOverlap_matchesUnboundedPrefix() {
        chunker.setTestChunkSize(5);
        chunker.setTestOverlap(2);
        final String content = "abcdefghijklmnopqrstuvwxyz";
        final List<String> unbounded = chunker.split(content);
        assertTrue(unbounded.size() > 3, "precondition: the unbounded split must produce more than the limit");
        assertEquals(unbounded.subList(0, 3), chunker.split(content, 3));
    }

    @Test
    public void test_splitWithLimit_neverSplitsSurrogatePair() {
        // The bounded path must reuse the same surrogate-pair boundary adjustment as the unbounded
        // one: a truncated result must still contain only well-formed UTF-16.
        chunker.setTestChunkSize(3);
        chunker.setTestOverlap(0);
        final String content = "a😀b😀c😀d";
        final List<String> unbounded = chunker.split(content);
        final List<String> bounded = chunker.split(content, 2);
        assertEquals(unbounded.subList(0, 2), bounded);
        for (final String chunk : bounded) {
            assertFalse(Character.isHighSurrogate(chunk.charAt(chunk.length() - 1)), "trailing unpaired high surrogate in: " + chunk);
            assertFalse(Character.isLowSurrogate(chunk.charAt(0)), "leading unpaired low surrogate in: " + chunk);
        }
    }

    // ===================================================================================
    //                                                             boundary-aware splitting
    //                                                             ========================

    @Test
    public void test_split_japaneseTextBreaksAtFullStop() {
        chunker.setTestChunkSize(20);
        chunker.setTestOverlap(0);
        final String content = "これは最初の文です。これは二番目の文です。これは三番目の文です。";
        final List<String> chunks = chunker.split(content);
        assertEquals(content, String.join("", chunks));
        for (int i = 0; i < chunks.size() - 1; i++) {
            assertTrue(chunks.get(i).endsWith("。"), "chunk " + i + " should end at a sentence: " + chunks.get(i));
        }
    }

    @Test
    public void test_split_englishTextDoesNotCutWordsInHalf() {
        chunker.setTestChunkSize(30);
        chunker.setTestOverlap(0);
        final String content = "The quick brown fox jumps over the lazy dog while the elephant watches quietly nearby";
        final List<String> chunks = chunker.split(content);
        assertEquals(content, String.join("", chunks));
        for (int i = 0; i < chunks.size() - 1; i++) {
            final String chunk = chunks.get(i);
            assertTrue(chunk.endsWith(" "), "chunk " + i + " must end on a space boundary: [" + chunk + "]");
        }
    }

    @Test
    public void test_split_boundaryEnabled_reconstructsOriginalExactly() {
        chunker.setTestChunkSize(24);
        chunker.setTestOverlap(0);
        final String content = "Hello world. これはテストです、大丈夫ですか？ 東京Tokyo 2026\n\n次の段落。😀 The end.";
        assertEquals(content, String.join("", chunker.split(content)));
    }

    @Test
    public void test_split_boundaryEnabled_boundedMatchesUnboundedPrefix() {
        chunker.setTestChunkSize(16);
        chunker.setTestOverlap(0);
        final String content = "これは最初の文です。これは二番目の文です。alpha beta gamma delta epsilon。おわり。";
        final List<String> unbounded = chunker.split(content);
        assertTrue(unbounded.size() > 2, "precondition: more chunks than the limit");
        for (int limit = 1; limit <= unbounded.size(); limit++) {
            assertEquals("limit=" + limit, unbounded.subList(0, limit), chunker.split(content, limit));
        }
    }

    @Test
    public void test_split_boundaryDisabled_matchesLegacyFixedLength() {
        chunker.setTestBoundaryEnabled(false);
        chunker.setTestChunkSize(10);
        chunker.setTestOverlap(0);
        final String content = "これは最初の文です。これは二番目の文です。";
        final List<String> chunks = chunker.split(content);
        assertEquals(3, chunks.size());
        assertEquals("これは最初の文です。", chunks.get(0));
        // A pure chunk_size=10 fixed-length cut on this 21-char string lands at 10/20, not 10/19:
        // "これは二番目の文です" (10 chars) + "。" (1 char).
        assertEquals("これは二番目の文です", chunks.get(1));
        assertEquals("。", chunks.get(2));
    }

    @Test
    public void test_split_zeroPercents_matchLegacyFixedLength() {
        chunker.setTestLookbackPercent(0);
        chunker.setTestLookaheadPercent(0);
        chunker.setTestChunkSize(10);
        chunker.setTestOverlap(0);
        final String content = "これは最初の文です。これは二番目の文です。";
        // Same fixed-length cut as test_split_boundaryDisabled_matchesLegacyFixedLength: 10/20, not 10/19.
        assertEquals(List.of("これは最初の文です。", "これは二番目の文です", "。"), chunker.split(content));
    }

    @Test
    public void test_split_forwardSearchTakesSentenceEndBeyondChunkSize() {
        // Note: the per-chunk bound below (chunk_size + lookahead = 25) never actually binds on
        // this content -- the largest chunk produced is well under it -- so this test does not
        // exercise that ceiling being reached. What it does pin: the forward search overshoots
        // chunk_size to keep the "。" sentence end whole (chunks.get(0) below), round-trips
        // losslessly, and stays under the ceiling with margin to spare.
        chunker.setTestChunkSize(20);
        chunker.setTestOverlap(0);
        chunker.setTestLookbackPercent(10); // 2 chars back: not enough to reach the previous 。
        chunker.setTestLookaheadPercent(25); // 5 chars ahead
        final String content = "あいうえおかきくけこさしすせそたちつてとなに。つづきの文章がここにあります。";
        final List<String> chunks = chunker.split(content);
        assertEquals(content, String.join("", chunks));
        assertEquals("あいうえおかきくけこさしすせそたちつてとなに。", chunks.get(0));
        for (final String chunk : chunks) {
            // chunk_size + lookahead is not a universal ceiling (the grapheme-cluster escape is
            // ungoverned by lookahead and can push a chunk further, see LengthChunker's class
            // Javadoc), but this content has no grapheme clusters for that escape to act on, so
            // chunk_size + lookahead is the applicable bound for this specific input.
            assertTrue(chunk.length() <= 25,
                    "a chunk must not exceed chunk_size + lookahead for this grapheme-cluster-free content: " + chunk.length());
        }
    }

    @Test
    public void test_split_withOverlap_boundaryEnabled_isContiguousAndTerminates() {
        chunker.setTestChunkSize(20);
        chunker.setTestOverlap(6);
        final String content = "これは最初の文です。これは二番目の文です。これは三番目の文です。おしまい。";
        final List<String> chunks = chunker.split(content);
        assertTrue(chunks.size() >= 2, "precondition: multiple chunks");
        // Every chunk must start at the offset the previous chunk's own contract guarantees it
        // can (snapOverlapStart: start < result <= end) -- not merely "wherever content.indexOf
        // happens to match". This content repeats "これは...の文です。" three times, so a search
        // with a loosely-computed lower bound (e.g. cursor - chunk.length()) could silently accept
        // a stale earlier occurrence of the same text and still look contiguous. Track each
        // chunk's real, verified start and only search strictly after it (never at or before it,
        // per the "start < result" half of the contract) for the next one, so an earlier
        // occurrence of the same substring can never be mistaken for the real next chunk.
        int expectedStart = 0;
        for (int i = 0; i < chunks.size(); i++) {
            final String chunk = chunks.get(i);
            assertTrue(content.startsWith(chunk, expectedStart),
                    "chunk " + i + " does not start at the expected offset " + expectedStart + ": " + chunk);
            final int end = expectedStart + chunk.length();
            if (i + 1 < chunks.size()) {
                final int nextAt = content.indexOf(chunks.get(i + 1), expectedStart + 1);
                assertTrue(nextAt >= 0, "chunk " + (i + 1) + " not found strictly after offset " + expectedStart);
                assertTrue(nextAt <= end, "text was skipped between chunk " + i + " and chunk " + (i + 1));
                expectedStart = nextAt;
            } else {
                assertEquals(content.length(), end, "the chunks must cover the whole content");
            }
        }
    }

    @Test
    public void test_split_allSpaces_terminates() {
        chunker.setTestChunkSize(4);
        chunker.setTestOverlap(0);
        // Blank content short-circuits, so mix in one non-space character.
        final String content = "x" + "　".repeat(30);
        final List<String> chunks = chunker.split(content);
        assertEquals(content, String.join("", chunks));
        assertTrue(chunks.size() <= content.length(), "must not produce more chunks than characters");
    }

    @Test
    public void test_split_allNewlines_terminates() {
        chunker.setTestChunkSize(4);
        chunker.setTestOverlap(0);
        final String content = "x" + "\n".repeat(30);
        final String joined = String.join("", chunker.split(content));
        assertEquals(content, joined);
    }

    @Test
    public void test_split_largeDocument_staysLinear() {
        // A guard against an accidental O(N^2): re-scanning the skippable run at every candidate,
        // or losing the `ideal - lookback` floor so every cut rescans the whole prefix, would turn
        // this into minutes. An ABSOLUTE threshold cannot see that -- the real time here is a few
        // milliseconds, so a 1000x regression still fits inside any bound loose enough not to be
        // flaky. The ratio between 4N and N is the only assertion that actually measures the
        // growth curve; the absolute bound below is kept only as a coarse "nothing hung" cap.
        chunker.setTestChunkSize(800);
        chunker.setTestOverlap(0);
        // Han text with no space, no punctuation and no script change: every cut must walk the
        // whole lookback window and the whole lookahead window before giving up, so the scan is
        // never cut short by an early hit.
        final String unit = "本日快晴無風";
        final String small = unit.repeat(SMALL_DOCUMENT_UNITS);
        final String large = unit.repeat(4 * SMALL_DOCUMENT_UNITS);
        // Warm up first: the first passes measure the JIT, not the algorithm.
        for (int i = 0; i < 2; i++) {
            chunker.split(small);
            chunker.split(large);
        }
        final long smallNanos = fastestSplitNanos(small, 7);
        final long largeNanos = fastestSplitNanos(large, 7);
        assertEquals(large, String.join("", chunker.split(large)));
        final double ratio = (double) largeNanos / Math.max(1L, smallNanos);
        assertTrue(ratio < 6.0, "quadrupling the document multiplied the time by " + ratio + " (small=" + smallNanos / 1_000_000L
                + "ms, large=" + largeNanos / 1_000_000L + "ms); the scan is not linear");
        assertTrue(largeNanos < 10_000_000_000L, "splitting " + large.length() + " characters took " + largeNanos / 1_000_000L + "ms");
    }

    /** 250,002 characters; the ratio assertion above compares this against four times as many. */
    private static final int SMALL_DOCUMENT_UNITS = 41_667;

    @Test
    public void test_split_minimumChunkSize_boundaryEnabled_isLossless() {
        chunker.setTestChunkSize(1); // floored to MIN_CHUNK_SIZE
        chunker.setTestOverlap(0);
        final String content = "あ。い、う えTokyo😀";
        assertEquals(content, String.join("", chunker.split(content)));
    }

    @Test
    public void test_boundaryPercent_clampedAboveMaximum() {
        final LogCapturingAppender appender = LogCapturingAppender.attach(LengthChunker.class);
        try {
            chunker.setTestLookbackPercent(999);
            chunker.setTestChunkSize(100);
            chunker.setTestOverlap(0);
            chunker.split("a".repeat(300));
            assertTrue(appender.warnings().stream().anyMatch(w -> w.contains("lookback_percent")),
                    "an out-of-range percent must warn, got " + appender.warnings());
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_boundaryPercent_negativeTreatedAsDisabled() {
        final LogCapturingAppender appender = LogCapturingAppender.attach(LengthChunker.class);
        try {
            chunker.setTestLookbackPercent(-1);
            chunker.setTestChunkSize(10);
            chunker.setTestOverlap(0);
            assertEquals(2, chunker.split("a".repeat(20)).size());
            assertTrue(appender.warnings().stream().anyMatch(w -> w.contains("lookback_percent")),
                    "a negative percent must warn, got " + appender.warnings());
        } finally {
            appender.detach();
        }
    }

    // ===================================================================================
    //                                                   the boundary search is not a no-op
    //                                                   =================================
    // "concatenating the chunks reproduces the content" is a TAUTOLOGY at overlap=0: the loop
    // sets start = end every iteration, so any partition round-trips. Deleting the whole boundary
    // search (ChunkBoundaryFinder#findChunkEnd => return base) leaves every round-trip test green.
    // The tests below assert WHERE the cuts land, so a no-op search reddens them.

    @Test
    public void test_split_prose_everyCutLandsOnASentenceEndOrASpace() {
        chunker.setTestChunkSize(60);
        chunker.setTestOverlap(0);
        final List<String> chunks = chunker.split(PROSE_CORPUS);
        assertTrue(chunks.size() >= 10, "precondition: the corpus must produce at least ten chunks, got " + chunks.size());
        assertEquals(PROSE_CORPUS, String.join("", chunks));
        for (int i = 0; i < chunks.size() - 1; i++) {
            final String chunk = chunks.get(i);
            final char last = chunk.charAt(chunk.length() - 1);
            assertTrue(last == ' ' || last == '。' || last == '.' || last == '!' || last == '?',
                    "chunk " + i + " was cut mid-word at [" + last + "] (U+" + Integer.toHexString(last) + "): " + chunk);
        }
    }

    @Test
    public void test_split_randomizedProse_boundarySearchMovesEveryCutAndLosesNothing() {
        // A differential property test: the boundary-enabled output must differ from the
        // boundary-disabled one on content that HAS boundaries. A no-op search makes the two
        // identical, because the disabled path is exactly "cut at `base`".
        final int[] chunkSizes = { 17, 40, 96, 250 };
        int cases = 0;
        int differing = 0;
        for (long seed = 1L; seed <= 8L; seed++) {
            final String content = randomBoundaryRichContent(seed, 4000);
            for (final int chunkSize : chunkSizes) {
                chunker.setTestChunkSize(chunkSize);
                chunker.setTestOverlap(0);
                chunker.setTestBoundaryEnabled(true);
                final List<String> enabled = chunker.split(content);
                chunker.setTestBoundaryEnabled(false);
                final List<String> disabled = chunker.split(content);
                assertEquals(content, String.join("", enabled));
                assertEquals(content, String.join("", disabled));
                cases++;
                if (!enabled.equals(disabled)) {
                    differing++;
                }
            }
        }
        assertEquals(cases, differing, "boundary-aware splitting produced the same cuts as the fixed-length one; the search is a no-op");
    }

    @Test
    public void test_split_graphemeRichContent_neverCutsInsideACluster() {
        // The existing round-trip corpora contain no combining marks, no ZWJ sequences and (apart
        // from one lone emoji) no astral characters, so the "a candidate that would strand a
        // combining mark is rejected" contract was never exercised end to end.
        chunker.setTestOverlap(0);
        for (long seed = 1L; seed <= 4L; seed++) {
            final String content = randomClusterRichContent(seed, 2000);
            for (final int chunkSize : new int[] { 16, 40, 100 }) {
                chunker.setTestChunkSize(chunkSize);
                final List<String> chunks = chunker.split(content);
                assertEquals(content, String.join("", chunks));
                for (int i = 0; i < chunks.size(); i++) {
                    final String chunk = chunks.get(i);
                    final int firstCp = chunk.codePointAt(0);
                    final int lastCp = chunk.codePointBefore(chunk.length());
                    assertFalse(Character.isLowSurrogate(chunk.charAt(0)), "chunk " + i + " starts with a lone low surrogate");
                    assertFalse(Character.isHighSurrogate(chunk.charAt(chunk.length() - 1)),
                            "chunk " + i + " ends with a lone high surrogate");
                    if (i > 0) {
                        assertFalse(isClusterContinuationCodePoint(firstCp),
                                "chunk " + i + " starts with a combining mark/joiner stranded from its base: U+"
                                        + Integer.toHexString(firstCp) + " seed=" + seed + " chunkSize=" + chunkSize);
                    }
                    if (i < chunks.size() - 1) {
                        assertFalse(lastCp == 0x200D,
                                "chunk " + i + " ends on a zero-width joiner, splitting the sequence it joins. seed=" + seed);
                    }
                }
            }
        }
    }

    // ===================================================================================
    //                                                          overlap restart point (snap)
    //                                                          ==========================
    // The existing overlap tests use all-'a' content, where snapping the restart point is a
    // no-op: replacing the snapOverlapStart() call with the raw `end - overlap` left them green.

    @Test
    public void test_split_withOverlap_restartSnapsEarlierThanTheRawOffset() {
        final int overlap = 10;
        chunker.setTestChunkSize(100);
        chunker.setTestOverlap(overlap);
        final List<String> chunks = chunker.split(PROSE_CORPUS);
        assertTrue(chunks.size() >= 5, "precondition: the corpus must produce several overlapping chunks, got " + chunks.size());
        final int[] starts = chunkStartOffsets(PROSE_CORPUS, chunks);
        for (int i = 1; i < chunks.size(); i++) {
            final int previousEnd = starts[i - 1] + chunks.get(i - 1).length();
            assertTrue(starts[i] < previousEnd - overlap, "chunk " + i + " restarted at the raw end-overlap offset "
                    + (previousEnd - overlap) + " instead of snapping back to a boundary (actual " + starts[i] + ")");
        }
    }

    @Test
    public void test_split_withOverlap_effectiveOverlapStaysWithinTwiceTheConfiguredValue() {
        // Snapping only ever moves the restart point EARLIER, so the effective overlap can only
        // grow. split() caps the snap window at the configured overlap (Math.min(lookback,
        // overlap)); without that cap the window is derived from chunk_size and the effective
        // overlap reaches overlap + lookback, silently multiplying the index duplication that
        // warnOnOverlapSideEffect() exists to warn about.
        for (final int overlap : new int[] { 5, 10, 20 }) {
            chunker.setTestChunkSize(100);
            chunker.setTestOverlap(overlap);
            final List<String> chunks = chunker.split(PROSE_CORPUS);
            assertTrue(chunks.size() >= 5, "precondition: several chunks for overlap=" + overlap);
            final int[] starts = chunkStartOffsets(PROSE_CORPUS, chunks);
            for (int i = 1; i < chunks.size(); i++) {
                final int previousEnd = starts[i - 1] + chunks.get(i - 1).length();
                final int actualOverlap = previousEnd - starts[i];
                assertTrue(actualOverlap >= overlap,
                        "configured overlap=" + overlap + " but chunk " + i + " only overlapped " + actualOverlap + " characters");
                assertTrue(actualOverlap <= 2 * overlap, "configured overlap=" + overlap + " but chunk " + i + " overlapped "
                        + actualOverlap + " characters; the snap window is not capped at the configured overlap");
            }
        }
    }

    // ===================================================================================
    //                                                            the real configuration channel
    //                                                            ==============================
    // TestableLengthChunker overrides all five getters, so the whole getConfigBoolean/getConfigInt
    // channel was dead in tests: making both throw on entry left every test green. These tests use
    // a plain LengthChunker and drive FessProp#getSystemProperty's documented -D fallback
    // (`fess.system.<key>`), which the shared conf/system.properties store does not define for any
    // content_chunker.length.* key -- so nothing is written to that JVM-lifetime singleton.

    @Test
    public void test_realConfigChannel_boundaryDisabledProducesFixedLengthOutput() {
        final String content = "これは最初の文です。これは二番目の文です。";
        try {
            setChunkerProperty(LengthChunker.CHUNK_SIZE_PROPERTY, "10");
            setChunkerProperty(LengthChunker.OVERLAP_PROPERTY, "0");
            final LengthChunker real = new LengthChunker();
            // Control: with the kill switch unset the very same instance splits at boundaries,
            // so the difference below can only come from reading the property.
            assertEquals(List.of("これは最初の文です。", "これは二番目の文です。"), real.split(content));
            setChunkerProperty(LengthChunker.BOUNDARY_ENABLED_PROPERTY, "false");
            assertEquals(List.of("これは最初の文です。", "これは二番目の文です", "。"), real.split(content));
            assertEquals(referenceFixedLengthSplit(content, 10, 0), real.split(content));
        } finally {
            clearChunkerProperties();
        }
    }

    @Test
    public void test_realConfigChannel_invalidBooleanFallsBackToTheDefaultAndWarns() {
        final String content = "これは最初の文です。これは二番目の文です。";
        final LogCapturingAppender capture = LogCapturingAppender.attach(LengthChunker.class);
        try {
            setChunkerProperty(LengthChunker.CHUNK_SIZE_PROPERTY, "10");
            setChunkerProperty(LengthChunker.BOUNDARY_ENABLED_PROPERTY, "yes-please");
            final List<String> chunks = new LengthChunker().split(content);
            assertEquals(List.of("これは最初の文です。", "これは二番目の文です。"), chunks);
            assertTrue(
                    capture.warnings()
                            .stream()
                            .anyMatch(w -> w.contains(LengthChunker.BOUNDARY_ENABLED_PROPERTY) && w.contains("yes-please")),
                    "an unparseable boolean must WARN and fall back to the default: " + capture.warnings());
        } finally {
            capture.detach();
            clearChunkerProperties();
        }
    }

    @Test
    public void test_realConfigChannel_percentsAreReadFromTheChannel() {
        try {
            setChunkerProperty(LengthChunker.CHUNK_SIZE_PROPERTY, "100");
            setChunkerProperty(LengthChunker.OVERLAP_PROPERTY, "0");
            final LengthChunker real = new LengthChunker();
            final List<String> fixedLength = referenceFixedLengthSplit(PROSE_CORPUS, 100, 0);

            setChunkerProperty(LengthChunker.LOOKBACK_PERCENT_PROPERTY, "0");
            setChunkerProperty(LengthChunker.LOOKAHEAD_PERCENT_PROPERTY, "0");
            assertEquals(fixedLength, real.split(PROSE_CORPUS));

            setChunkerProperty(LengthChunker.LOOKBACK_PERCENT_PROPERTY, "20");
            assertFalse(fixedLength.equals(real.split(PROSE_CORPUS)), "lookback_percent=20 must be read and change the cuts");

            setChunkerProperty(LengthChunker.LOOKBACK_PERCENT_PROPERTY, "0");
            setChunkerProperty(LengthChunker.LOOKAHEAD_PERCENT_PROPERTY, "25");
            assertFalse(fixedLength.equals(real.split(PROSE_CORPUS)), "lookahead_percent=25 must be read and change the cuts");
        } finally {
            clearChunkerProperties();
        }
    }

    @Test
    public void test_realConfigChannel_invalidPercentFallsBackToTheDefaultAndWarns() {
        final LogCapturingAppender capture = LogCapturingAppender.attach(LengthChunker.class);
        try {
            setChunkerProperty(LengthChunker.CHUNK_SIZE_PROPERTY, "100");
            setChunkerProperty(LengthChunker.OVERLAP_PROPERTY, "0");
            setChunkerProperty(LengthChunker.LOOKAHEAD_PERCENT_PROPERTY, "0");
            final LengthChunker real = new LengthChunker();
            setChunkerProperty(LengthChunker.LOOKBACK_PERCENT_PROPERTY, String.valueOf(LengthChunker.DEFAULT_LOOKBACK_PERCENT));
            final List<String> withExplicitDefault = real.split(PROSE_CORPUS);
            setChunkerProperty(LengthChunker.LOOKBACK_PERCENT_PROPERTY, "twenty");
            final List<String> withInvalidValue = real.split(PROSE_CORPUS);
            // Pins the fallback VALUE, not merely "something sensible happened".
            assertEquals(withExplicitDefault, withInvalidValue);
            assertFalse(referenceFixedLengthSplit(PROSE_CORPUS, 100, 0).equals(withInvalidValue),
                    "the default lookback must still drive a real backward search");
            assertTrue(
                    capture.warnings().stream().anyMatch(w -> w.contains(LengthChunker.LOOKBACK_PERCENT_PROPERTY) && w.contains("twenty")),
                    "an unparseable integer must WARN and fall back to the default: " + capture.warnings());
        } finally {
            capture.detach();
            clearChunkerProperties();
        }
    }

    // ===================================================================================
    //                                       legacy fixed-length equivalence (differential)
    //                                       =====================================================
    // The two existing legacy-equivalence tests use content with no grapheme clusters, so removing
    // the `lookback > 0 || lookahead > 0` gate in split() -- which is what keeps the cluster escape
    // from firing at lookback=lookahead=0 -- left them green. referenceFixedLengthSplit below is an
    // independent substring loop; it never calls split(), so the expectation cannot drift with it.

    @Test
    public void test_split_boundaryDisabled_matchesIndependentFixedLengthReference() {
        chunker.setTestBoundaryEnabled(false);
        assertFixedLengthReferenceEquivalence();
    }

    @Test
    public void test_split_zeroPercents_matchIndependentFixedLengthReference() {
        chunker.setTestLookbackPercent(0);
        chunker.setTestLookaheadPercent(0);
        assertFixedLengthReferenceEquivalence();
    }

    private void assertFixedLengthReferenceEquivalence() {
        for (long seed = 1L; seed <= 4L; seed++) {
            final String content = randomClusterRichContent(seed, 1500);
            for (final int chunkSize : new int[] { 5, 13, 64, 200 }) {
                for (final int overlap : new int[] { 0, 3 }) {
                    chunker.setTestChunkSize(chunkSize);
                    chunker.setTestOverlap(overlap);
                    final String where = "seed=" + seed + " chunkSize=" + chunkSize + " overlap=" + overlap;
                    final List<String> expected = referenceFixedLengthSplit(content, chunkSize, overlap);
                    final List<String> actual = chunker.split(content);
                    // Compared chunk by chunk rather than list to list: these corpora run to
                    // hundreds of chunks, and a whole-list mismatch message is unreadable.
                    for (int i = 0; i < Math.min(expected.size(), actual.size()); i++) {
                        // Qualified: UnitFessTestCase deliberately omits assertEquals(Object,
                        // Object, String), so this is the only way to keep the JUnit 5 argument
                        // order when all three arguments are Strings.
                        org.junit.jupiter.api.Assertions.assertEquals(expected.get(i), actual.get(i), where + " chunk=" + i);
                    }
                    assertEquals(expected.size(), actual.size(), where + " produced a different number of chunks");
                }
            }
        }
    }

    // ===================================================================================
    //                                                          clamp values, not just WARNs
    //                                                          ============================

    @Test
    public void test_boundaryPercent_aboveMaximum_searchesExactlyTheMaximumWindow() {
        // MAX_LOOKBACK_PERCENT=50 of chunk_size=100 is a 50-character backward window. Both halves
        // are needed: the first fails if the clamp yields 0 (no search at all), the second fails if
        // the out-of-range value is used unclamped (a 999-character window).
        chunker.setTestChunkSize(100);
        chunker.setTestOverlap(0);
        chunker.setTestLookbackPercent(999);
        chunker.setTestLookaheadPercent(0);
        // A sentence end 45 characters before the ideal cut: inside a 50-character window.
        final String inWindow = "a".repeat(53) + ". " + "b".repeat(120);
        assertEquals(55, chunker.split(inWindow).get(0).length(), "a boundary 45 characters back must be found within the clamped window");
        // A sentence end 70 characters before the ideal cut: outside a 50-character window.
        final String outOfWindow = "a".repeat(28) + ". " + "b".repeat(150);
        assertEquals(100, chunker.split(outOfWindow).get(0).length(),
                "a boundary 70 characters back is outside the clamped window and must not be reached");
    }

    @Test
    public void test_boundaryPercent_negative_disablesThatDirectionEntirely() {
        // Pins the VALUE the negative percent normalizes to: 0, not the maximum. With lookahead
        // also 0 the finder is never resolved at all, so the output is the legacy fixed-length one.
        chunker.setTestChunkSize(10);
        chunker.setTestOverlap(0);
        chunker.setTestLookbackPercent(-1);
        chunker.setTestLookaheadPercent(0);
        final String content = "これは最初の文です。これは二番目の文です。";
        assertEquals(List.of("これは最初の文です。", "これは二番目の文です", "。"), chunker.split(content));
        assertEquals(referenceFixedLengthSplit(content, 10, 0), chunker.split(content));
    }

    // ===================================================================================
    //                                                                     test helpers
    //                                                                     ============

    private static final String PROSE_CORPUS = "Fess is an open source enterprise search server. "
            + "It crawls web sites, file systems and data stores, then indexes the extracted text. " + "これは日本語の文章です。全文検索エンジンとして動作します。"
            + "The administration UI lets an operator schedule crawls, review logs and tune relevance. " + "クローラは HTTP と SMB の両方に対応しています。"
            + "Documents in Office, PDF and plain text formats are all supported out of the box. " + "検索結果はスコア順に並び替えられ、ハイライトが付与されます。"
            + "A plugin can replace the tokenizer, the ranking model or the storage backend entirely. " + "設定ファイルを編集すると、動作を細かく調整できます。"
            + "Finally, the REST API exposes every feature the browser UI offers, and a little more. ";

    /** Prose-like fragments: plenty of spaces, sentence ends and script changes to snap onto. */
    private static final String[] BOUNDARY_RICH_TOKENS = { "search", "index", "crawler", "document", "relevance", " ", " ", " ", ". ", ", ",
            "。", "、", "検索", "文書", "クローラ", "全文検索", "設定", "\n", "Fess", "OpenSearch", "1234", "😀" };

    /** Deliberately hostile to a fixed-length cut: combining marks, ZWJ sequences and astral chars. */
    private static final String[] CLUSTER_RICH_TOKENS =
            { "é", "à́", "ñ", "👩‍💻", "👨‍👩‍👦", "😀", "❤️", "🇯🇵", "が", "abc", " ", "。", "नि" };

    private static String randomBoundaryRichContent(final long seed, final int approximateLength) {
        return randomContent(seed, approximateLength, BOUNDARY_RICH_TOKENS);
    }

    private static String randomClusterRichContent(final long seed, final int approximateLength) {
        return randomContent(seed, approximateLength, CLUSTER_RICH_TOKENS);
    }

    private static String randomContent(final long seed, final int approximateLength, final String[] tokens) {
        final Random random = new Random(seed);
        final StringBuilder buf = new StringBuilder(approximateLength + 32);
        while (buf.length() < approximateLength) {
            buf.append(tokens[random.nextInt(tokens.length)]);
        }
        return buf.toString();
    }

    /**
     * An independent fixed-length splitter: a plain substring loop with the surrogate-pair
     * adjustment, deliberately NOT derived from {@link LengthChunker#split(String, int)} so that
     * comparing against it is a real differential test rather than a restatement.
     *
     * @param content the text to split
     * @param chunkSize the (already normalized) chunk size
     * @param overlap the (already normalized) overlap
     * @return the fixed-length chunks
     */
    private static List<String> referenceFixedLengthSplit(final String content, final int chunkSize, final int overlap) {
        final List<String> chunks = new ArrayList<>();
        final int length = content.length();
        int start = 0;
        while (start < length) {
            if (splitsSurrogatePair(content, start)) {
                start++;
            }
            if (start >= length) {
                break;
            }
            int end = Math.min(start + chunkSize, length);
            if (splitsSurrogatePair(content, end)) {
                end--;
            }
            if (end <= start) {
                end = start + Character.charCount(content.codePointAt(start));
            }
            chunks.add(content.substring(start, end));
            if (end >= length) {
                break;
            }
            int nextStart = end - overlap;
            if (nextStart <= start) {
                nextStart = end;
            }
            start = nextStart;
        }
        return chunks;
    }

    private static boolean splitsSurrogatePair(final String content, final int index) {
        return index > 0 && index < content.length() && Character.isLowSurrogate(content.charAt(index))
                && Character.isHighSurrogate(content.charAt(index - 1));
    }

    private static boolean isClusterContinuationCodePoint(final int cp) {
        if (cp == 0x200D || cp >= 0xFE00 && cp <= 0xFE0F || cp >= 0xE0100 && cp <= 0xE01EF) {
            return true;
        }
        final int type = Character.getType(cp);
        return type == Character.NON_SPACING_MARK || type == Character.COMBINING_SPACING_MARK || type == Character.ENCLOSING_MARK;
    }

    /**
     * Recovers the offset each chunk really started at. The chunks are only strings, so the
     * restart points an overlap produces are otherwise unobservable.
     *
     * @param content the text that was split
     * @param chunks the produced chunks
     * @return the start offset of each chunk
     */
    private int[] chunkStartOffsets(final String content, final List<String> chunks) {
        final int[] starts = new int[chunks.size()];
        for (int i = 1; i < chunks.size(); i++) {
            final String chunk = chunks.get(i);
            final int at = content.indexOf(chunk, starts[i - 1] + 1);
            assertTrue(at >= 0, "chunk " + i + " was not found after offset " + starts[i - 1]);
            if (chunk.length() >= 16) {
                // Guards the search above: a repeated fragment could otherwise resolve to a stale
                // earlier occurrence and make the offsets fiction.
                assertEquals(at, content.lastIndexOf(chunk), "chunk " + i + " is not unique in the corpus; the offsets are unreliable");
            }
            final int previousEnd = starts[i - 1] + chunks.get(i - 1).length();
            assertTrue(at <= previousEnd, "text was skipped between chunk " + (i - 1) + " and chunk " + i);
            starts[i] = at;
        }
        final int lastIndex = chunks.size() - 1;
        assertEquals(content.length(), starts[lastIndex] + chunks.get(lastIndex).length(), "the chunks must cover the whole content");
        return starts;
    }

    private long fastestSplitNanos(final String content, final int runs) {
        long best = Long.MAX_VALUE;
        for (int i = 0; i < runs; i++) {
            final long startedAt = System.nanoTime();
            final List<String> chunks = chunker.split(content);
            best = Math.min(best, System.nanoTime() - startedAt);
            assertFalse(chunks.isEmpty(), "the split must produce chunks");
        }
        return best;
    }

    private static void setChunkerProperty(final String key, final String value) {
        System.setProperty(Constants.SYSTEM_PROP_PREFIX + key, value);
    }

    private static void clearChunkerProperties() {
        for (final String key : new String[] { LengthChunker.CHUNK_SIZE_PROPERTY, LengthChunker.OVERLAP_PROPERTY,
                LengthChunker.BOUNDARY_ENABLED_PROPERTY, LengthChunker.LOOKBACK_PERCENT_PROPERTY,
                LengthChunker.LOOKAHEAD_PERCENT_PROPERTY }) {
            System.clearProperty(Constants.SYSTEM_PROP_PREFIX + key);
        }
    }

    // ===================================================================================
    //                                                  Config warnings and boundary metrics
    //                                                  -----------------------------------

    @Test
    public void test_configWarning_isEmittedOncePerDistinctProblem() {
        final LogCapturingAppender appender = LogCapturingAppender.attach(LengthChunker.class);
        try {
            chunker.setTestChunkSize(0); // invalid -> falls back to the default, with a WARN
            for (int i = 0; i < 25; i++) {
                chunker.split("some content to split into chunks", 100);
            }
            assertEquals(1, countMatching(appender, "Invalid chunk_size"), "a misconfigured instance must not emit one WARN per document");
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_configWarning_isEmittedAgainWhenTheProblemChanges() {
        final LogCapturingAppender appender = LogCapturingAppender.attach(LengthChunker.class);
        try {
            chunker.setTestChunkSize(0);
            chunker.split("some content to split into chunks", 100);
            chunker.setTestChunkSize(-7); // a DIFFERENT bad value must be reported
            chunker.split("some content to split into chunks", 100);
            assertEquals(2, countMatching(appender, "Invalid chunk_size"));
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_configWarning_theSameProblemAfterACleanRunIsNotRepeated() {
        // Pinned semantics: the sink remembers the last problem REPORTED, not the last
        // configuration seen, so a clean run in between does not re-arm it. The WARN describes a
        // standing misconfiguration, so reporting it once is the point.
        final LogCapturingAppender appender = LogCapturingAppender.attach(LengthChunker.class);
        try {
            chunker.setTestChunkSize(0);
            chunker.split("some content to split into chunks", 100);
            chunker.setTestChunkSize(40);
            chunker.split("some content to split into chunks", 100);
            chunker.setTestChunkSize(0);
            chunker.split("some content to split into chunks", 100);
            assertEquals(1, countMatching(appender, "Invalid chunk_size"));
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_boundaryOutcome_reportsHardCutsSeparatelyFromMovedCuts() {
        final List<int[]> observed = new java.util.ArrayList<>();
        final TestableLengthChunker recording = new TestableLengthChunker() {
            @Override
            protected void logBoundaryOutcome(final int chunks, final int movedBack, final int movedForward, final int hardCut) {
                observed.add(new int[] { chunks, movedBack, movedForward, hardCut });
            }
        };
        recording.setTestChunkSize(20);
        recording.setTestLookbackPercent(30);
        recording.setTestLookaheadPercent(0);

        // Content with no boundary anywhere: every cut must be reported as a hard cut.
        observed.clear();
        recording.split("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", 100);
        assertEquals(1, observed.size());
        assertEquals(observed.get(0)[0], observed.get(0)[3], "an unbreakable document must be 100% hard cut");
        assertEquals(0, observed.get(0)[1]);

        // Prose with spaces: the backward tier must move cuts instead.
        observed.clear();
        recording.split("alpha beta gamma delta epsilon zeta eta theta iota kappa lambda mu nu xi", 100);
        assertEquals(1, observed.size());
        assertTrue(observed.get(0)[1] > 0, "prose must produce backward-moved cuts, got " + observed.get(0)[1]);
        assertTrue(observed.get(0)[3] < observed.get(0)[0], "prose must not be 100% hard cut");
    }

    @Test
    public void test_boundaryOutcome_isNotReportedWhenBoundarySearchIsDisabled() {
        final List<int[]> observed = new java.util.ArrayList<>();
        final TestableLengthChunker recording = new TestableLengthChunker() {
            @Override
            protected void logBoundaryOutcome(final int chunks, final int movedBack, final int movedForward, final int hardCut) {
                observed.add(new int[] { chunks, movedBack, movedForward, hardCut });
            }
        };
        recording.setTestChunkSize(20);
        recording.setTestBoundaryEnabled(false);
        recording.split("alpha beta gamma delta epsilon zeta eta theta iota kappa", 100);
        assertTrue(observed.isEmpty(), "nothing to report when the finder never runs");
    }

    private static int countMatching(final LogCapturingAppender appender, final String needle) {
        return (int) appender.warnings().stream().filter(m -> m.contains(needle)).count();
    }

    private static class TestableLengthChunker extends LengthChunker {
        private int testChunkSize = 800;
        private int testOverlap = 0;
        private boolean testBoundaryEnabled = DEFAULT_BOUNDARY_ENABLED;
        private int testLookbackPercent = DEFAULT_LOOKBACK_PERCENT;
        private int testLookaheadPercent = DEFAULT_LOOKAHEAD_PERCENT;

        void setTestChunkSize(final int chunkSize) {
            this.testChunkSize = chunkSize;
        }

        void setTestOverlap(final int overlap) {
            this.testOverlap = overlap;
        }

        void setTestBoundaryEnabled(final boolean enabled) {
            this.testBoundaryEnabled = enabled;
        }

        void setTestLookbackPercent(final int percent) {
            this.testLookbackPercent = percent;
        }

        void setTestLookaheadPercent(final int percent) {
            this.testLookaheadPercent = percent;
        }

        ChunkBoundaryFinder exposedBoundaryFinder() {
            return getBoundaryFinder();
        }

        @Override
        protected int getChunkSize() {
            return testChunkSize;
        }

        @Override
        protected int getOverlap() {
            return testOverlap;
        }

        @Override
        protected boolean isBoundaryEnabled() {
            return testBoundaryEnabled;
        }

        @Override
        protected int getLookbackPercent() {
            return testLookbackPercent;
        }

        @Override
        protected int getLookaheadPercent() {
            return testLookaheadPercent;
        }
    }

    /**
     * Minimal in-memory log4j2 appender for asserting on emitted log messages.
     * Mirrors {@code OpenSearchEmbeddingClientTest.LogCapturingAppender}.
     */
    static final class LogCapturingAppender extends AbstractAppender {
        private final List<LogEvent> events = new CopyOnWriteArrayList<>();
        private final Logger boundLogger;

        private LogCapturingAppender(final Logger logger) {
            super("LogCapturingAppender-" + UUID.randomUUID(), null, null, true, Property.EMPTY_ARRAY);
            this.boundLogger = logger;
        }

        static LogCapturingAppender attach(final Class<?> targetClass) {
            final Logger logger = (Logger) LogManager.getLogger(targetClass);
            final LogCapturingAppender appender = new LogCapturingAppender(logger);
            appender.start();
            logger.addAppender(appender);
            return appender;
        }

        void detach() {
            boundLogger.removeAppender(this);
            stop();
        }

        @Override
        public void append(final LogEvent event) {
            events.add(event.toImmutable());
        }

        List<String> warnings() {
            return events.stream().filter(e -> e.getLevel() == Level.WARN).map(e -> e.getMessage().getFormattedMessage()).toList();
        }
    }
}
