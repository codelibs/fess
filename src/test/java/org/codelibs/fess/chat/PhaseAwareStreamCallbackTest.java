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
import java.util.List;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class PhaseAwareStreamCallbackTest extends UnitFessTestCase {

    @Test
    public void test_forwardsRetryWithPhase() {
        final List<String> captured = new ArrayList<>();
        final ChatPhaseCallback phaseCallback = new ChatPhaseCallback() {
            @Override
            public void onPhaseStart(final String phase, final String message) {
            }

            @Override
            public void onPhaseComplete(final String phase) {
            }

            @Override
            public void onChunk(final String content, final boolean done) {
            }

            @Override
            public void onError(final String phase, final String error) {
            }

            @Override
            public void onRetry(final String phase, final String operation, final int attempt, final int max, final long sleepMs,
                    final String cause) {
                captured.add(phase + ":" + operation + ":" + attempt + "/" + max + ":" + sleepMs + ":" + cause);
            }
        };
        final PhaseAwareStreamCallback cb =
                new PhaseAwareStreamCallback(ChatPhaseCallback.PHASE_ANSWER, phaseCallback, (chunk, done) -> {});
        cb.onRetry("streamChat", 2, 5, 4000L, new RuntimeException("status:429"));
        assertEquals(1, captured.size());
        assertEquals("answer:streamChat:2/5:4000:RuntimeException", captured.get(0));
    }

    @Test
    public void test_forwardsChunkToInner() {
        final List<String> chunks = new ArrayList<>();
        final PhaseAwareStreamCallback cb = new PhaseAwareStreamCallback(ChatPhaseCallback.PHASE_ANSWER, ChatPhaseCallback.noOp(),
                (chunk, done) -> chunks.add(chunk + ":" + done));
        cb.onChunk("hello", false);
        cb.onChunk("", true);
        assertEquals(2, chunks.size());
        assertEquals("hello:false", chunks.get(0));
        assertEquals(":true", chunks.get(1));
    }

    @Test
    public void test_forwardsWaitingAndWarningWithPhase() {
        final List<String> events = new ArrayList<>();
        final ChatPhaseCallback phaseCallback = new ChatPhaseCallback() {
            @Override
            public void onPhaseStart(final String p, final String m) {
            }

            @Override
            public void onPhaseComplete(final String p) {
            }

            @Override
            public void onChunk(final String c, final boolean d) {
            }

            @Override
            public void onError(final String p, final String e) {
            }

            @Override
            public void onWaiting(final String phase, final String reason, final long elapsedMs, final long timeoutMs) {
                events.add("wait:" + phase + ":" + reason + ":" + timeoutMs);
            }

            @Override
            public void onWarning(final String phase, final String code, final String detail) {
                events.add("warn:" + phase + ":" + code + ":" + detail);
            }
        };
        final PhaseAwareStreamCallback cb =
                new PhaseAwareStreamCallback(ChatPhaseCallback.PHASE_INTENT, phaseCallback, (chunk, done) -> {});
        cb.onWaiting("concurrency_limit", 0L, 30000L);
        cb.onWarning("reasoning_token_exhausted", "search");
        assertEquals(2, events.size());
        assertEquals("wait:intent:concurrency_limit:30000", events.get(0));
        assertEquals("warn:intent:reasoning_token_exhausted:search", events.get(1));
    }

    @Test
    public void test_nullPhaseCallback_doesNotThrow() {
        final PhaseAwareStreamCallback cb = new PhaseAwareStreamCallback(ChatPhaseCallback.PHASE_ANSWER, null, (chunk, done) -> {});
        // Should not throw — internal noOp fallback should be used
        cb.onRetry("op", 1, 3, 1000L, new RuntimeException());
        cb.onWaiting("concurrency_limit", 0L, 30000L);
        cb.onWarning("code", "detail");
    }
}
