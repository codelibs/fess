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

public class ChatClientFallbackTest extends UnitFessTestCase {

    @Test
    public void test_callback_records_fallback_event() {
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
            public void onFallback(final String phase, final String reason, final String orig, final String next) {
                events.add(phase + "|" + reason + "|" + orig + "|" + next);
            }
        };
        cb.onFallback(ChatPhaseCallback.PHASE_SEARCH, "no_relevant_results", "old query", "new query");
        cb.onPhaseStart(ChatPhaseCallback.PHASE_SEARCH, "Searching with refined query...", "new query");
        assertEquals(1, events.size());
        assertEquals("search|no_relevant_results|old query|new query", events.get(0));
    }
}
