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

public class ChatPhaseCallbackTest extends UnitFessTestCase {

    @Test
    public void test_phaseConstants() {
        assertEquals("intent", ChatPhaseCallback.PHASE_INTENT);
        assertEquals("search", ChatPhaseCallback.PHASE_SEARCH);
        assertEquals("evaluate", ChatPhaseCallback.PHASE_EVALUATE);
        assertEquals("fetch", ChatPhaseCallback.PHASE_FETCH);
        assertEquals("answer", ChatPhaseCallback.PHASE_ANSWER);
    }

    @Test
    public void test_phaseConstants_notNull() {
        assertNotNull(ChatPhaseCallback.PHASE_INTENT);
        assertNotNull(ChatPhaseCallback.PHASE_SEARCH);
        assertNotNull(ChatPhaseCallback.PHASE_EVALUATE);
        assertNotNull(ChatPhaseCallback.PHASE_FETCH);
        assertNotNull(ChatPhaseCallback.PHASE_ANSWER);
    }

    @Test
    public void test_phaseConstants_unique() {
        String[] phases = { ChatPhaseCallback.PHASE_INTENT, ChatPhaseCallback.PHASE_SEARCH, ChatPhaseCallback.PHASE_EVALUATE,
                ChatPhaseCallback.PHASE_FETCH, ChatPhaseCallback.PHASE_ANSWER };

        // Check all phases are unique
        for (int i = 0; i < phases.length; i++) {
            for (int j = i + 1; j < phases.length; j++) {
                assertFalse("Phase constants should be unique", phases[i].equals(phases[j]));
            }
        }
    }

    @Test
    public void test_callbackImplementation() {
        // Test that a callback implementation can be created
        final List<String> events = new ArrayList<>();

        ChatPhaseCallback callback = new ChatPhaseCallback() {
            @Override
            public void onPhaseStart(String phase, String message) {
                events.add("start:" + phase + ":" + message);
            }

            @Override
            public void onPhaseComplete(String phase) {
                events.add("complete:" + phase);
            }

            @Override
            public void onChunk(String content, boolean done) {
                events.add("chunk:" + content + ":" + done);
            }

            @Override
            public void onError(String phase, String errorMessage) {
                events.add("error:" + phase + ":" + errorMessage);
            }
        };

        // Test onPhaseStart
        callback.onPhaseStart(ChatPhaseCallback.PHASE_INTENT, "Analyzing...");
        assertEquals(1, events.size());
        assertEquals("start:intent:Analyzing...", events.get(0));

        // Test onPhaseComplete
        callback.onPhaseComplete(ChatPhaseCallback.PHASE_INTENT);
        assertEquals(2, events.size());
        assertEquals("complete:intent", events.get(1));

        // Test onChunk
        callback.onChunk("test content", false);
        assertEquals(3, events.size());
        assertEquals("chunk:test content:false", events.get(2));

        callback.onChunk("final content", true);
        assertEquals(4, events.size());
        assertEquals("chunk:final content:true", events.get(3));

        // Test onError
        callback.onError(ChatPhaseCallback.PHASE_SEARCH, "Search failed");
        assertEquals(5, events.size());
        assertEquals("error:search:Search failed", events.get(4));
    }

    @Test
    public void test_callbackWithNullValues() {
        final List<String> events = new ArrayList<>();

        ChatPhaseCallback callback = new ChatPhaseCallback() {
            @Override
            public void onPhaseStart(String phase, String message) {
                events.add("start:" + phase + ":" + message);
            }

            @Override
            public void onPhaseComplete(String phase) {
                events.add("complete:" + phase);
            }

            @Override
            public void onChunk(String content, boolean done) {
                events.add("chunk:" + content);
            }

            @Override
            public void onError(String phase, String errorMessage) {
                events.add("error:" + phase + ":" + errorMessage);
            }
        };

        // Test with null values
        callback.onPhaseStart(null, null);
        assertEquals("start:null:null", events.get(0));

        callback.onChunk(null, false);
        assertEquals("chunk:null", events.get(1));

        callback.onError(null, null);
        assertEquals("error:null:null", events.get(2));
    }

    @Test
    public void test_callbackWithEmptyStrings() {
        final List<String> events = new ArrayList<>();

        ChatPhaseCallback callback = new ChatPhaseCallback() {
            @Override
            public void onPhaseStart(String phase, String message) {
                events.add("start:" + phase.length() + ":" + message.length());
            }

            @Override
            public void onPhaseComplete(String phase) {
                events.add("complete:" + phase.length());
            }

            @Override
            public void onChunk(String content, boolean done) {
                events.add("chunk:" + content.length());
            }

            @Override
            public void onError(String phase, String errorMessage) {
                events.add("error:" + phase.length() + ":" + errorMessage.length());
            }
        };

        callback.onPhaseStart("", "");
        assertEquals("start:0:0", events.get(0));

        callback.onChunk("", true);
        assertEquals("chunk:0", events.get(1));
    }

    @Test
    public void test_allPhases_inOrder() {
        final List<String> phases = new ArrayList<>();

        ChatPhaseCallback callback = new ChatPhaseCallback() {
            @Override
            public void onPhaseStart(String phase, String message) {
                phases.add(phase);
            }

            @Override
            public void onPhaseComplete(String phase) {
            }

            @Override
            public void onChunk(String content, boolean done) {
            }

            @Override
            public void onError(String phase, String errorMessage) {
            }
        };

        // Simulate typical flow
        callback.onPhaseStart(ChatPhaseCallback.PHASE_INTENT, "msg");
        callback.onPhaseStart(ChatPhaseCallback.PHASE_SEARCH, "msg");
        callback.onPhaseStart(ChatPhaseCallback.PHASE_EVALUATE, "msg");
        callback.onPhaseStart(ChatPhaseCallback.PHASE_FETCH, "msg");
        callback.onPhaseStart(ChatPhaseCallback.PHASE_ANSWER, "msg");

        assertEquals(5, phases.size());
        assertEquals("intent", phases.get(0));
        assertEquals("search", phases.get(1));
        assertEquals("evaluate", phases.get(2));
        assertEquals("fetch", phases.get(3));
        assertEquals("answer", phases.get(4));
    }
}
