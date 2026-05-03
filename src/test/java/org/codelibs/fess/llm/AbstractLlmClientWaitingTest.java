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

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class AbstractLlmClientWaitingTest extends UnitFessTestCase {

    @Test
    public void test_streamChatWithConcurrencyControl_firesOnWaitingWhenNoPermit() throws Exception {
        final TestLlmClient client = new TestLlmClient();
        client.setSemaphore(new Semaphore(1));
        // pre-acquire the only permit
        assertTrue(client.semaphore().tryAcquire());

        final AtomicReference<String> capturedReason = new AtomicReference<>();
        final AtomicReference<Long> capturedTimeout = new AtomicReference<>();
        final LlmStreamCallback callback = new LlmStreamCallback() {
            @Override
            public void onChunk(final String chunk, final boolean done) {
            }

            @Override
            public void onWaiting(final String reason, final long elapsedMs, final long timeoutMs) {
                capturedReason.set(reason);
                capturedTimeout.set(timeoutMs);
            }
        };
        try {
            client.invokeStreamWithConcurrency(new LlmChatRequest(), callback);
            fail("expected LlmException due to permit timeout");
        } catch (final LlmException expected) {
            assertEquals(LlmException.ERROR_RATE_LIMIT, expected.getErrorCode());
        }
        assertEquals("concurrency_limit", capturedReason.get());
        assertNotNull(capturedTimeout.get());
        assertTrue(capturedTimeout.get() > 0);
    }

    @Test
    public void test_streamChatWithConcurrencyControl_doesNotFireOnWaitingWhenPermitFree() throws Exception {
        final TestLlmClient client = new TestLlmClient();
        client.setSemaphore(new Semaphore(1)); // one free permit

        final boolean[] waitingFired = { false };
        final LlmStreamCallback callback = new LlmStreamCallback() {
            @Override
            public void onChunk(final String chunk, final boolean done) {
            }

            @Override
            public void onWaiting(final String reason, final long elapsedMs, final long timeoutMs) {
                waitingFired[0] = true;
            }
        };
        client.invokeStreamWithConcurrency(new LlmChatRequest(), callback);
        assertFalse("onWaiting should not fire when a permit is freely available", waitingFired[0]);
    }

    /**
     * Test subclass that exposes hooks needed by tests.
     * Override only the bare minimum abstract methods of AbstractLlmClient to construct successfully.
     */
    static class TestLlmClient extends AbstractLlmClient {

        @Override
        public String getName() {
            return "test";
        }

        @Override
        protected boolean checkAvailabilityNow() {
            return true;
        }

        @Override
        protected int getTimeout() {
            return 0;
        }

        @Override
        protected String getModel() {
            return "test-model";
        }

        @Override
        protected int getAvailabilityCheckInterval() {
            return 0;
        }

        @Override
        protected boolean isRagChatEnabled() {
            return true;
        }

        @Override
        protected String getLlmType() {
            return "test";
        }

        @Override
        protected String getConfigPrefix() {
            return "rag.llm.test";
        }

        @Override
        protected int getContextMaxChars(final String promptType) {
            return 0;
        }

        @Override
        protected int getEvaluationMaxRelevantDocs() {
            return 0;
        }

        @Override
        protected int getEvaluationDescriptionMaxChars() {
            return 0;
        }

        @Override
        protected long getConcurrencyWaitTimeoutMs() {
            return 50L; // make the test fast
        }

        @Override
        public void streamChat(final LlmChatRequest request, final LlmStreamCallback callback) {
            // no-op: we are testing the wrapper, not the underlying call
        }

        @Override
        public LlmChatResponse chat(final LlmChatRequest request) {
            return new LlmChatResponse();
        }

        void setSemaphore(final Semaphore s) {
            this.concurrencyLimiter = s;
        }

        Semaphore semaphore() {
            return this.concurrencyLimiter;
        }

        void invokeStreamWithConcurrency(final LlmChatRequest req, final LlmStreamCallback cb) {
            streamChatWithConcurrencyControl(req, cb);
        }
    }
}
