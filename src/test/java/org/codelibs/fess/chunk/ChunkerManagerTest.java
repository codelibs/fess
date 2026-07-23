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
