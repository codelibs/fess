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

import java.util.Collections;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class ChatPhaseCallbackTest extends UnitFessTestCase {

    @Test
    public void test_noOp_supportsAllNewMethods() {
        final ChatPhaseCallback cb = ChatPhaseCallback.noOp();
        cb.onPhaseStart("intent", "msg");
        cb.onPhaseStart("search", "msg", "kw");
        cb.onPhaseComplete("intent");
        cb.onPhaseComplete("search", Map.of("hitCount", 12));
        cb.onChunk("c", false);
        cb.onError("intent", "err");
        cb.onRetry("intent", "streamChat", 1, 3, 1000L, "status:429");
        cb.onWaiting("intent", "concurrency_limit", 0L, 30000L);
        cb.onFallback("search", "no_relevant_results", "orig", "new");
        cb.onWarning("intent", "reasoning_token_exhausted", "search");
    }

    @Test
    public void test_onPhaseComplete_withPayload_defaultDelegates() {
        final boolean[] called = { false };
        final ChatPhaseCallback cb = new ChatPhaseCallback() {
            @Override
            public void onPhaseStart(final String phase, final String message) {
            }

            @Override
            public void onPhaseComplete(final String phase) {
                called[0] = true;
            }

            @Override
            public void onChunk(final String content, final boolean done) {
            }

            @Override
            public void onError(final String phase, final String error) {
            }
        };
        cb.onPhaseComplete("search", Collections.emptyMap());
        assertTrue(called[0]);
    }
}
