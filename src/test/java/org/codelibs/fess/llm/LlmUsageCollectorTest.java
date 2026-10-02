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

import java.util.ArrayList;
import java.util.List;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class LlmUsageCollectorTest extends UnitFessTestCase {

    @Test
    public void test_startAndClose() {
        assertNull(LlmUsageCollector.current());
        final LlmUsageCollector collector = LlmUsageCollector.start();
        try {
            assertSame(collector, LlmUsageCollector.current());
        } finally {
            collector.close();
        }
        assertNull(LlmUsageCollector.current());
        // closing twice is harmless
        collector.close();
        assertNull(LlmUsageCollector.current());
    }

    @Test
    public void test_nestedRestoresPrevious() {
        try (LlmUsageCollector outer = LlmUsageCollector.start()) {
            try (LlmUsageCollector inner = LlmUsageCollector.start()) {
                assertSame(inner, LlmUsageCollector.current());
            }
            assertSame(outer, LlmUsageCollector.current());
        }
        assertNull(LlmUsageCollector.current());
    }

    @Test
    public void test_otherThreadSeesNoCollector() throws Exception {
        try (LlmUsageCollector collector = LlmUsageCollector.start()) {
            final List<LlmUsageCollector> seen = new ArrayList<>();
            final Thread thread = new Thread(() -> seen.add(LlmUsageCollector.current()));
            thread.start();
            thread.join();
            assertNull(seen.get(0));
            assertSame(collector, LlmUsageCollector.current());
        }
    }

    @Test
    public void test_nothingReported() {
        final LlmUsageCollector collector = new LlmUsageCollector();
        collector.recordCall();
        collector.recordUsage(null);
        collector.recordResponse(new LlmChatResponse("no usage"));
        assertEquals(2, collector.getLlmCalls());
        assertNull(collector.getPromptTokens());
        assertNull(collector.getCompletionTokens());
        assertNull(collector.getTotalTokens());
        assertNull(collector.getModel());
        assertNull(collector.getIntent());
    }

    @Test
    public void test_sumsUsage() {
        final LlmUsageCollector collector = new LlmUsageCollector();
        final LlmChatResponse first = new LlmChatResponse("a");
        first.setPromptTokens(100);
        first.setCompletionTokens(20);
        first.setTotalTokens(125);
        first.setModel("model-a");
        collector.recordResponse(first);
        // no total: prompt + completion
        collector.recordCall();
        collector.recordUsage(new LlmUsage(10, 5, null, "model-b"));
        // only a total
        collector.recordCall();
        collector.recordUsage(new LlmUsage(null, null, 7, null));
        assertEquals(3, collector.getLlmCalls());
        assertEquals(Long.valueOf(110), collector.getPromptTokens());
        assertEquals(Long.valueOf(25), collector.getCompletionTokens());
        assertEquals(Long.valueOf(147), collector.getTotalTokens());
        // the last reported model
        assertEquals("model-b", collector.getModel());
    }

    @Test
    public void test_ignoresNegativeAndBlank() {
        final LlmUsageCollector collector = new LlmUsageCollector();
        collector.recordUsage(new LlmUsage(-1, 3, -5, " "));
        assertNull(collector.getPromptTokens());
        assertEquals(Long.valueOf(3), collector.getCompletionTokens());
        assertNull(collector.getTotalTokens(), "no total and no prompt to add up");
        assertNull(collector.getModel());
    }

    @Test
    public void test_largeTotalsDoNotOverflow() {
        final LlmUsageCollector collector = new LlmUsageCollector();
        collector.recordUsage(new LlmUsage(Integer.MAX_VALUE, Integer.MAX_VALUE, null, null));
        assertEquals(Long.valueOf(2L * Integer.MAX_VALUE), collector.getTotalTokens());
    }

    @Test
    public void test_keepsFirstIntent() {
        final LlmUsageCollector collector = new LlmUsageCollector();
        collector.recordIntent(null);
        collector.recordIntent(ChatIntent.SEARCH);
        collector.recordIntent(ChatIntent.FAQ);
        assertEquals("search", collector.getIntent());
    }

    @Test
    public void test_concurrentRecording() throws Exception {
        final LlmUsageCollector collector = new LlmUsageCollector();
        final List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            final Thread thread = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    collector.recordCall();
                    collector.recordUsage(new LlmUsage(1, 1, 2, null));
                }
            });
            threads.add(thread);
            thread.start();
        }
        for (final Thread thread : threads) {
            thread.join();
        }
        assertEquals(8000, collector.getLlmCalls());
        assertEquals(Long.valueOf(16000), collector.getTotalTokens());
    }

    @Test
    public void test_usageOf() {
        assertNull(LlmUsage.of(null));
        assertNull(LlmUsage.of(new LlmChatResponse("content only")));
        final LlmChatResponse response = new LlmChatResponse("x");
        response.setModel("m");
        final LlmUsage modelOnly = LlmUsage.of(response);
        assertNotNull(modelOnly);
        assertEquals("m", modelOnly.model());
        assertNull(modelOnly.totalTokens());
        response.setPromptTokens(3);
        assertEquals(Integer.valueOf(3), LlmUsage.of(response).promptTokens());
        assertTrue(new LlmUsage(null, null, null, null).isEmpty());
        assertFalse(new LlmUsage(0, null, null, null).isEmpty());
    }
}
