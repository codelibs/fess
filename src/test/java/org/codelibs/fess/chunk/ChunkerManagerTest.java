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

import java.util.Collections;
import java.util.List;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class ChunkerManagerTest extends UnitFessTestCase {

    private TestableChunkerManager manager;

    @Override
    public void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        manager = new TestableChunkerManager();
    }

    // Literal pin: this system-property key is external operator configuration; the raw VALUE is
    // pinned so silent drift reddens a test instead of orphaning existing config.
    @Test
    public void test_externalContractLiterals() {
        assertEquals("content_chunker.chunker.name", ChunkerManager.CHUNKER_NAME_PROPERTY);
    }

    @Test
    public void test_getChunker_resolvesViaComponentByConvention() {
        final FakeChunker fake = new FakeChunker("length");
        ComponentUtil.register(fake, "lengthChunker");
        manager.setTestChunkerType("length");
        assertSame(fake, manager.getChunker());
    }

    @Test
    public void test_getChunker_fallsBackToRegisteredList() {
        final FakeChunker fake = new FakeChunker("custom");
        manager.register(fake);
        manager.setTestChunkerType("custom");
        assertSame(fake, manager.getChunker());
    }

    @Test
    public void test_getChunker_returnsNullWhenNotFound() {
        manager.setTestChunkerType("missing");
        assertNull(manager.getChunker(), "should return null when no chunker matches");
    }

    @Test
    public void test_split_delegatesToResolvedChunker() {
        final FakeChunker fake = new FakeChunker("length");
        fake.result = List.of("a", "b");
        manager.register(fake);
        manager.setTestChunkerType("length");
        assertEquals("delegated result", List.of("a", "b"), manager.split("irrelevant"));
    }

    @Test
    public void test_split_returnsEmptyListWhenNoChunkerResolved() {
        manager.setTestChunkerType("missing");
        assertTrue(manager.split("x").isEmpty(), "should return empty list when unresolved");
    }

    // ===================================================================================
    //                                        bounded chunk production
    //                                        ================================================

    @Test
    public void test_splitWithLimit_stopsProducingAtTheLimit() {
        // A 10x oversized document: the chunker must PRODUCE at most `limit` chunks, not produce
        // all 1000 and let the caller discard 900 of them.
        final CountingChunker counting = new CountingChunker("counting");
        counting.availableChunks = 1000;
        manager.register(counting);
        manager.setTestChunkerType("counting");

        final List<String> chunks = manager.split("irrelevant", 100);

        assertEquals(100, chunks.size(), "the result must be capped at the limit");
        assertEquals(100, counting.producedChunks,
                "chunk PRODUCTION must stop at the limit -- an oversized document must never materialize its full chunk list");
    }

    @Test
    public void test_splitWithLimit_nonPositiveLimit_producesNothing() {
        final CountingChunker counting = new CountingChunker("counting");
        counting.availableChunks = 10;
        manager.register(counting);
        manager.setTestChunkerType("counting");

        assertTrue(manager.split("irrelevant", 0).isEmpty(), "limit=0 must produce nothing");
        assertEquals(0, counting.producedChunks, "no chunk may be produced for a non-positive limit");
    }

    @Test
    public void test_splitWithLimit_defaultSpiImplementation_truncatesForThirdPartyChunkers() {
        // A third-party Chunker that does not override the bounded overload still gets a correct
        // (if not memory-bounded) result from the SPI default.
        final FakeChunker fake = new FakeChunker("plain");
        fake.result = List.of("a", "b", "c", "d");
        manager.register(fake);
        manager.setTestChunkerType("plain");

        assertEquals(List.of("a", "b"), manager.split("irrelevant", 2));
        assertEquals(List.of("a", "b", "c", "d"), manager.split("irrelevant", 99));
    }

    @Test
    public void test_splitWithLimit_returnsEmptyListWhenNoChunkerResolved() {
        manager.setTestChunkerType("missing");
        assertTrue(manager.split("x", 5).isEmpty(), "should return empty list when unresolved");
    }

    /**
     * A {@link Chunker} that natively honours the production bound and counts every chunk it
     * actually creates, so a "split everything then truncate" implementation is distinguishable
     * from one that stops early.
     */
    private static final class CountingChunker implements Chunker {
        private final String name;
        int availableChunks;
        int producedChunks;

        CountingChunker(final String name) {
            this.name = name;
        }

        @Override
        public List<String> split(final String content) {
            return produce(availableChunks);
        }

        @Override
        public List<String> split(final String content, final int limit) {
            return produce(Math.min(availableChunks, Math.max(0, limit)));
        }

        private List<String> produce(final int count) {
            final List<String> chunks = new java.util.ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                producedChunks++;
                chunks.add("chunk-" + i);
            }
            return chunks;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void register() {
            // no-op for test fake
        }
    }

    private static final class FakeChunker implements Chunker {
        private final String name;
        List<String> result = Collections.emptyList();

        FakeChunker(final String name) {
            this.name = name;
        }

        @Override
        public List<String> split(final String content) {
            return result;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void register() {
            // no-op for test fake
        }
    }

    private static final class TestableChunkerManager extends ChunkerManager {
        private String testChunkerType = "length";

        void setTestChunkerType(final String type) {
            this.testChunkerType = type;
        }

        @Override
        protected String getChunkerType() {
            return testChunkerType;
        }
    }
}
