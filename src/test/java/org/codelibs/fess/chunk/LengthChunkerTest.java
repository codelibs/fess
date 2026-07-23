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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.codelibs.fess.unit.UnitFessTestCase;
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

    private static final class TestableLengthChunker extends LengthChunker {
        private int testChunkSize = 800;
        private int testOverlap = 0;

        void setTestChunkSize(final int chunkSize) {
            this.testChunkSize = chunkSize;
        }

        void setTestOverlap(final int overlap) {
            this.testOverlap = overlap;
        }

        @Override
        protected int getChunkSize() {
            return testChunkSize;
        }

        @Override
        protected int getOverlap() {
            return testOverlap;
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
