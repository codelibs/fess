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

import org.codelibs.fess.llm.IntentDetectionResult;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class ChatClientWarningTest extends UnitFessTestCase {

    @Test
    public void test_intentResult_fallback_marker_isSet_by_factory() {
        final IntentDetectionResult fallback = IntentDetectionResult.fallbackSearch("hello");
        assertTrue("fallbackSearch() must mark the result as fallback", fallback.isFallback());
    }

    @Test
    public void test_callback_records_warning_event() {
        final List<String> events = new ArrayList<>();
        final ChatPhaseCallback cb = new ChatPhaseCallback() {
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
            public void onWarning(final String phase, final String code, final String detail) {
                events.add(phase + "|" + code + "|" + detail);
            }
        };
        cb.onWarning(ChatPhaseCallback.PHASE_INTENT, "reasoning_token_exhausted", "search");
        assertEquals(1, events.size());
        assertEquals("intent|reasoning_token_exhausted|search", events.get(0));
    }
}
