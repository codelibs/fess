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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class ChatClientHitCountTest extends UnitFessTestCase {

    @Test
    public void test_payload_passed_to_phase_complete() {
        final AtomicReference<Map<String, Object>> capturedPayload = new AtomicReference<>();
        final ChatPhaseCallback cb = new ChatPhaseCallback() {
            @Override
            public void onPhaseStart(final String p, final String m) {
            }

            @Override
            public void onPhaseComplete(final String p) {
                fail("legacy onPhaseComplete should not be called when payload variant is invoked directly");
            }

            @Override
            public void onPhaseComplete(final String phase, final Map<String, Object> payload) {
                capturedPayload.set(payload);
            }

            @Override
            public void onChunk(final String c, final boolean d) {
            }

            @Override
            public void onError(final String p, final String e) {
            }
        };
        cb.onPhaseComplete(ChatPhaseCallback.PHASE_SEARCH, Map.of("hitCount", 12));
        assertNotNull(capturedPayload.get());
        assertEquals(Integer.valueOf(12), capturedPayload.get().get("hitCount"));
    }

    @Test
    public void test_payload_default_delegates_when_not_overridden() {
        final boolean[] legacyCalled = { false };
        final ChatPhaseCallback cb = new ChatPhaseCallback() {
            @Override
            public void onPhaseStart(final String p, final String m) {
            }

            @Override
            public void onPhaseComplete(final String p) {
                legacyCalled[0] = true;
            }

            @Override
            public void onChunk(final String c, final boolean d) {
            }

            @Override
            public void onError(final String p, final String e) {
            }
        };
        cb.onPhaseComplete(ChatPhaseCallback.PHASE_SEARCH, new HashMap<>());
        assertTrue("legacy onPhaseComplete should be called via default delegation", legacyCalled[0]);
    }
}
