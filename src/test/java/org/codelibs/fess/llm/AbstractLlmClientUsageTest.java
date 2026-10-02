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
import java.util.concurrent.Semaphore;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * Tests that {@link AbstractLlmClient} records each LLM call, and the usage it reports, in the
 * {@link LlmUsageCollector} bound to the calling thread.
 */
public class AbstractLlmClientUsageTest extends UnitFessTestCase {

    @Test
    public void test_chat_recordsCallAndUsage() {
        final UsageTestClient client = new UsageTestClient();
        client.response = new LlmChatResponse("answer");
        client.response.setPromptTokens(30);
        client.response.setCompletionTokens(12);
        client.response.setTotalTokens(42);
        client.response.setModel("model-x");
        try (LlmUsageCollector usage = LlmUsageCollector.start()) {
            assertSame(client.response, client.chatWithConcurrencyControl(new LlmChatRequest()));
            // with a concurrency limiter, too
            client.setSemaphore(new Semaphore(1));
            client.chatWithConcurrencyControl(new LlmChatRequest());
            assertEquals(2, usage.getLlmCalls());
            assertEquals(Long.valueOf(60), usage.getPromptTokens());
            assertEquals(Long.valueOf(24), usage.getCompletionTokens());
            assertEquals(Long.valueOf(84), usage.getTotalTokens());
            assertEquals("model-x", usage.getModel());
        }
    }

    @Test
    public void test_chat_countsFailedCall() {
        final UsageTestClient client = new UsageTestClient();
        client.failure = new LlmException("down", LlmException.ERROR_SERVICE_UNAVAILABLE);
        try (LlmUsageCollector usage = LlmUsageCollector.start()) {
            try {
                client.chatWithConcurrencyControl(new LlmChatRequest());
                fail("expected LlmException");
            } catch (final LlmException e) {
                assertSame(client.failure, e);
            }
            assertEquals(1, usage.getLlmCalls());
            assertNull(usage.getTotalTokens());
        }
    }

    @Test
    public void test_chat_withoutCollector() {
        final UsageTestClient client = new UsageTestClient();
        client.response = new LlmChatResponse("answer");
        assertNull(LlmUsageCollector.current());
        assertSame(client.response, client.chatWithConcurrencyControl(new LlmChatRequest()));
    }

    @Test
    public void test_chat_rejectedByConcurrencyLimitIsNotCounted() {
        final UsageTestClient client = new UsageTestClient();
        client.setSemaphore(new Semaphore(0));
        try (LlmUsageCollector usage = LlmUsageCollector.start()) {
            try {
                client.chatWithConcurrencyControl(new LlmChatRequest());
                fail("expected LlmException");
            } catch (final LlmException e) {
                assertEquals(LlmException.ERROR_RATE_LIMIT, e.getErrorCode());
            }
            assertEquals(0, usage.getLlmCalls());
        }
    }

    @Test
    public void test_stream_recordsUsageReportedFromAnotherThread() throws Exception {
        final UsageTestClient client = new UsageTestClient();
        client.streamUsage = new LlmUsage(200, 50, 250, "model-s");
        client.streamOnOtherThread = true;
        final List<String> events = new ArrayList<>();
        final LlmStreamCallback callback = new LlmStreamCallback() {
            @Override
            public void onChunk(final String chunk, final boolean done) {
                events.add("chunk:" + chunk + ":" + done);
            }

            @Override
            public void onWarning(final String code, final String detail) {
                events.add("warning:" + code);
            }

            @Override
            public void onUsage(final LlmUsage usage) {
                events.add("usage:" + usage.totalTokens());
            }
        };
        try (LlmUsageCollector usage = LlmUsageCollector.start()) {
            client.streamChatWithConcurrencyControl(new LlmChatRequest(), callback);
            client.setSemaphore(new Semaphore(1));
            client.streamChatWithConcurrencyControl(new LlmChatRequest(), callback);
            assertEquals(2, usage.getLlmCalls());
            assertEquals(Long.valueOf(400), usage.getPromptTokens());
            assertEquals(Long.valueOf(100), usage.getCompletionTokens());
            assertEquals(Long.valueOf(500), usage.getTotalTokens());
            assertEquals("model-s", usage.getModel());
        }
        // every event still reaches the caller's callback
        assertEquals(
                List.of("chunk:a:false", "warning:w", "usage:250", "chunk::true", "chunk:a:false", "warning:w", "usage:250", "chunk::true"),
                events);
    }

    @Test
    public void test_stream_countsCallWithoutUsage() {
        final UsageTestClient client = new UsageTestClient();
        final List<String> chunks = new ArrayList<>();
        try (LlmUsageCollector usage = LlmUsageCollector.start()) {
            client.streamChatWithConcurrencyControl(new LlmChatRequest(), (chunk, done) -> chunks.add(chunk));
            assertEquals(1, usage.getLlmCalls());
            assertNull(usage.getPromptTokens());
            assertNull(usage.getTotalTokens());
        }
        assertEquals(List.of("a", ""), chunks);
    }

    @Test
    public void test_stream_nullCallback() {
        final UsageTestClient client = new UsageTestClient();
        client.streamUsage = new LlmUsage(1, 1, 2, null);
        try (LlmUsageCollector usage = LlmUsageCollector.start()) {
            client.streamChatWithConcurrencyControl(new LlmChatRequest(), null);
            assertEquals(Long.valueOf(2), usage.getTotalTokens());
        }
    }

    @Test
    public void test_stream_withoutCollectorPassesCallbackThrough() {
        final UsageTestClient client = new UsageTestClient();
        final LlmStreamCallback callback = (chunk, done) -> {};
        client.streamChatWithConcurrencyControl(new LlmChatRequest(), callback);
        assertSame(callback, client.lastCallback);
    }

    @Test
    public void test_managerRecordsIntent() {
        final LlmClientManager manager = new LlmClientManager();
        try (LlmUsageCollector usage = LlmUsageCollector.start()) {
            final IntentDetectionResult result = IntentDetectionResult.faq("q", "r");
            assertSame(result, manager.recordIntent(result));
            assertNull(manager.recordIntent(null));
            assertEquals("faq", usage.getIntent());
        }
        // no collector bound: nothing to record into
        assertNotNull(new LlmClientManager().recordIntent(IntentDetectionResult.unclear("r")));
    }

    /** A client whose chat and streamChat behave as configured. */
    static class UsageTestClient extends AbstractLlmClientWaitingTest.TestLlmClient {
        LlmChatResponse response;
        LlmException failure;
        LlmUsage streamUsage;
        boolean streamOnOtherThread;
        LlmStreamCallback lastCallback;

        @Override
        public LlmChatResponse chat(final LlmChatRequest request) {
            if (failure != null) {
                throw failure;
            }
            return response;
        }

        @Override
        public void streamChat(final LlmChatRequest request, final LlmStreamCallback callback) {
            lastCallback = callback;
            if (callback == null) {
                return;
            }
            callback.onChunk("a", false);
            callback.onWarning("w", "detail");
            if (streamUsage != null) {
                if (streamOnOtherThread) {
                    final Thread thread = new Thread(() -> callback.onUsage(streamUsage));
                    thread.start();
                    try {
                        thread.join();
                    } catch (final InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    callback.onUsage(streamUsage);
                }
            }
            callback.onChunk("", true);
        }
    }
}
