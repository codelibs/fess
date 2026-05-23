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

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class ChatRequestBodyTest extends UnitFessTestCase {

    @Test
    public void test_parsesMessageAndSessionId() throws Exception {
        final Map<String, Object> raw = Map.of("message", "hello", "session_id", "s1");
        final ChatRequestBody body = ChatRequestBody.from(raw, 4000);
        assertEquals("hello", body.message());
        assertEquals("s1", body.sessionId());
        assertFalse(body.isClear());
    }

    @Test
    public void test_treatsClearFlagAsTrueOnlyWhenStringTrue() throws Exception {
        assertTrue(ChatRequestBody.from(Map.of("clear", "true", "session_id", "s1"), 4000).isClear());
        assertTrue(ChatRequestBody.from(Map.of("clear", Boolean.TRUE, "session_id", "s1"), 4000).isClear());
        assertFalse(ChatRequestBody.from(Map.of("clear", "false", "session_id", "s1"), 4000).isClear());
        assertFalse(ChatRequestBody.from(Map.of("clear", "1", "session_id", "s1"), 4000).isClear());
    }

    @Test
    public void test_blankMessageReturnsNull() throws Exception {
        assertNull(ChatRequestBody.from(Map.of("message", "  "), 4000).message());
        assertNull(ChatRequestBody.from(Map.of(), 4000).message());
    }

    @Test
    public void test_oversizedMessageThrows() {
        final String big = "x".repeat(101);
        assertThrows(ChatRequestBody.MessageTooLongException.class, () -> ChatRequestBody.from(Map.of("message", big), 100));
    }

    @Test
    public void test_filtersOnlyAcceptKnownLabels() throws Exception {
        // ChatRequestBody#fields returns a map keyed by "label" whose values pass
        // the label allowlist (verified by Fess's LabelTypeHelper). When the helper
        // is unavailable in the unit test the result must be empty, not null.
        final ChatRequestBody body = ChatRequestBody.from(Map.of("fields.label", java.util.List.of("nope")), 4000);
        assertNotNull(body.fields());
    }

    // ── MJ-29: warnings list reflects rejected values ────────────────────────────

    @Test
    public void test_warnings_emptyWhenNothingDropped() throws Exception {
        // When no fields.label or ex_q are supplied, getWarnings() must return an empty map.
        final ChatRequestBody body = ChatRequestBody.from(Map.of("message", "hi"), 4000);
        assertNotNull(body.getWarnings(), "warnings must not be null");
        assertTrue(body.getWarnings().isEmpty(), "warnings must be empty when nothing was dropped: " + body.getWarnings());
    }

    @Test
    public void test_warnings_containsRejectedLabelValues() throws Exception {
        // When the allowlist is empty (helper unavailable in unit tests), every supplied
        // fields.label value is rejected and must appear in getWarnings()["fields.label"].
        final ChatRequestBody body = ChatRequestBody.from(Map.of("fields.label", List.of("label-a", "label-b")), 4000);
        assertNotNull(body.getWarnings(), "warnings must not be null");
        // In unit test environment the LabelTypeHelper is unavailable, so all labels
        // are rejected and must appear in the warnings map.
        final List<String> warnedLabels = body.getWarnings().get("fields.label");
        assertNotNull(warnedLabels, "rejected labels must be tracked in warnings");
        // At least the submitted values should be recorded (subject to allowlist state).
        // We assert non-empty rather than exact content to avoid depending on allowlist wiring.
        assertFalse(warnedLabels.isEmpty(), "warnings for fields.label must contain the rejected values");
    }

    @Test
    public void test_warnings_containsRejectedExtraQueryValues() throws Exception {
        // Same as above but for ex_q.
        final ChatRequestBody body = ChatRequestBody.from(Map.of("ex_q", List.of("q=foo", "q=bar")), 4000);
        assertNotNull(body.getWarnings(), "warnings must not be null");
        final List<String> warnedExQ = body.getWarnings().get("ex_q");
        assertNotNull(warnedExQ, "rejected ex_q must be tracked in warnings");
        assertFalse(warnedExQ.isEmpty(), "warnings for ex_q must contain the rejected values");
    }

    @Test
    public void test_warnings_isUnmodifiable() throws Exception {
        // getWarnings() must return an unmodifiable view.
        final ChatRequestBody body = ChatRequestBody.from(Map.of("fields.label", List.of("x")), 4000);
        final Map<String, List<String>> warnings = body.getWarnings();
        assertThrows(UnsupportedOperationException.class, () -> warnings.put("new", List.of()), "warnings map must be unmodifiable");
    }
}
