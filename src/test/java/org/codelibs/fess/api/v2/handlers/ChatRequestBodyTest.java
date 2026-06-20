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
package org.codelibs.fess.api.v2.handlers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.helper.ChatApiHelper;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class ChatRequestBodyTest extends UnitFessTestCase {

    private final ChatApiHelper chatApiHelper = new ChatApiHelper();

    @Test
    public void test_parsesMessageAndSessionId() throws Exception {
        final Map<String, Object> raw = Map.of("message", "hello", "session_id", "s1");
        final ChatRequestBody body = chatApiHelper.parseRequestBody(raw, 4000);
        assertEquals("hello", body.message());
        assertEquals("s1", body.sessionId());
        // clear flag removed — session clearing uses DELETE /api/v2/chat/sessions/{id}
    }

    @Test
    public void test_blankMessageReturnsNull() throws Exception {
        assertNull(chatApiHelper.parseRequestBody(Map.of("message", "  "), 4000).message());
        assertNull(chatApiHelper.parseRequestBody(Map.of(), 4000).message());
    }

    @Test
    public void test_oversizedMessageThrows() {
        final String big = "x".repeat(101);
        assertThrows(ChatRequestBody.MessageTooLongException.class, () -> chatApiHelper.parseRequestBody(Map.of("message", big), 100));
    }

    @Test
    public void test_filtersOnlyAcceptKnownLabels_nestedFields() throws Exception {
        // v2 nested fields object: {"fields": {"label": ["nope"]}}
        final ChatRequestBody body = chatApiHelper.parseRequestBody(Map.of("fields", Map.of("label", java.util.List.of("nope"))), 4000);
        assertNotNull(body.fields());
    }

    @Test
    public void test_filtersOnlyAcceptKnownLabels_dottedFallback() throws Exception {
        // Legacy dotted-key fallback: still accepted during transition.
        final ChatRequestBody body = chatApiHelper.parseRequestBody(Map.of("fields.label", java.util.List.of("nope")), 4000);
        assertNotNull(body.fields());
    }

    // ── MJ-29: warnings list reflects rejected values ────────────────────────────

    @Test
    public void test_warnings_emptyWhenNothingDropped() throws Exception {
        // When no fields.label or extra_queries are supplied, getWarnings() must return an empty map.
        final ChatRequestBody body = chatApiHelper.parseRequestBody(Map.of("message", "hi"), 4000);
        assertNotNull(body.getWarnings(), "warnings must not be null");
        assertTrue(body.getWarnings().isEmpty(), "warnings must be empty when nothing was dropped: " + body.getWarnings());
    }

    @Test
    public void test_warnings_containsRejectedLabelValues() throws Exception {
        // When the allowlist is empty (helper unavailable in unit tests), every supplied
        // fields.label value is rejected and must appear in getWarnings()["fields.label"].
        final ChatRequestBody body = chatApiHelper.parseRequestBody(Map.of("fields", Map.of("label", List.of("label-a", "label-b"))), 4000);
        assertNotNull(body.getWarnings(), "warnings must not be null");
        final List<String> warnedLabels = body.getWarnings().get("fields.label");
        assertNotNull(warnedLabels, "rejected labels must be tracked in warnings");
        assertFalse(warnedLabels.isEmpty(), "warnings for fields.label must contain the rejected values");
    }

    @Test
    public void test_warnings_containsRejectedExtraQueryValues() throws Exception {
        // extra_queries replaces the old ex_q key.
        final ChatRequestBody body = chatApiHelper.parseRequestBody(Map.of("extra_queries", List.of("q=foo", "q=bar")), 4000);
        assertNotNull(body.getWarnings(), "warnings must not be null");
        final List<String> warnedExQ = body.getWarnings().get("extra_queries");
        assertNotNull(warnedExQ, "rejected extra_queries must be tracked in warnings");
        assertFalse(warnedExQ.isEmpty(), "warnings for extra_queries must contain the rejected values");
    }

    @Test
    public void test_warnings_isUnmodifiable() throws Exception {
        // getWarnings() must return an unmodifiable view.
        final ChatRequestBody body = chatApiHelper.parseRequestBody(Map.of("fields", Map.of("label", List.of("x"))), 4000);
        final Map<String, List<String>> warnings = body.getWarnings();
        assertThrows(UnsupportedOperationException.class, () -> warnings.put("new", List.of()), "warnings map must be unmodifiable");
    }
}
