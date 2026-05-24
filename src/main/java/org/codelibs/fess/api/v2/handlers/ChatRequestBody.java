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

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.api.chat.ChatApiHelper;

/**
 * Parsed view of a chat request body shared by {@link ChatHandler} and
 * {@link ChatStreamHandler}.
 *
 * <p>The v2 body shape uses snake_case keys: {@code session_id}, {@code message},
 * {@code extra_queries}. Label filters are passed as a nested object:
 * {@code "fields": {"label": "value"}} (string or array). The legacy dotted-key
 * {@code "fields.label"} is also accepted for backward compatibility during
 * transition. Session clearing has been moved to the dedicated DELETE endpoint
 * {@code /api/v2/chat/sessions/{session_id}}; the {@code clear} flag is no
 * longer accepted here.</p>
 *
 * <p>Length is enforced inside {@link #from(Map, int)} so the caller does not need
 * a separate guard. Label and {@code extra_queries} validation uses the same allowlist
 * helpers v1 calls — {@code LabelTypeHelper} for labels and {@code ViewHelper}
 * for facet queries — to prevent query injection, delegated to
 * {@link ChatApiHelper}.</p>
 *
 * <p>MJ-29: Filter values that fail validation ({@code fields.label},
 * {@code extra_queries}) are dropped silently from the resolved request. Rejected
 * values are tracked in the {@link #warnings} map (see {@link #getWarnings()})
 * for diagnostics only and are not emitted on the wire by {@link ChatHandler}
 * or {@link ChatStreamHandler}.</p>
 *
 * <p>MJ-30 i18n note: error.message is developer-facing English. Clients MUST use
 * error.code (the V2ErrorCode token) for user-facing i18n.</p>
 */
public final class ChatRequestBody {

    private final String message;
    private final String sessionId;
    private final Map<String, String[]> fields;
    private final String[] extraQueries;

    /**
     * MJ-29: Tracks values that were supplied by the caller but rejected by the
     * allowlist. Indexed by field name (e.g. "fields.label", "extra_queries") with a
     * list of the rejected string values. Empty when nothing was dropped.
     *
     * <p>Diagnostics-only: not emitted on the wire today. Surfacing rejected
     * values to clients is a planned follow-up.</p>
     */
    private final Map<String, List<String>> warnings;

    private ChatRequestBody(final String message, final String sessionId, final Map<String, String[]> fields, final String[] extraQueries,
            final Map<String, List<String>> warnings) {
        this.message = message;
        this.sessionId = sessionId;
        this.fields = fields;
        this.extraQueries = extraQueries;
        this.warnings = warnings;
    }

    public String message() {
        return message;
    }

    public String sessionId() {
        return sessionId;
    }

    public Map<String, String[]> fields() {
        return fields;
    }

    public String[] extraQueries() {
        return extraQueries;
    }

    /**
     * MJ-29: Returns a map of field names to rejected values.
     * The map is empty (never null) when no values were dropped.
     * Keys are the request field names: {@code "fields.label"} and {@code "extra_queries"}.
     *
     * <p>Diagnostics-only: handlers ({@link ChatHandler}, {@link ChatStreamHandler})
     * do not surface this map on the wire today. Exposing rejected filter values
     * to clients is a planned follow-up.</p>
     *
     * @return unmodifiable map of rejected field→value-list pairs; empty if nothing was dropped
     */
    public Map<String, List<String>> getWarnings() {
        return Collections.unmodifiableMap(warnings);
    }

    public static ChatRequestBody from(final Map<String, Object> raw, final int maxMessageLength) throws IOException {
        final String message = trimmedOrNull(raw.get("message"));
        final String sessionId = trimmedOrNull(raw.get("session_id"));
        if (message != null && message.length() > maxMessageLength) {
            throw new MessageTooLongException("message exceeds max length: " + message.length() + " > " + maxMessageLength);
        }
        final Map<String, List<String>> warnings = new HashMap<>();
        final Map<String, String[]> fields = ChatApiHelper.parseFieldFilters(raw, warnings);
        final String[] extraQueries = ChatApiHelper.parseExtraQueries(raw, warnings);
        return new ChatRequestBody(message, sessionId, fields, extraQueries, warnings);
    }

    private static String trimmedOrNull(final Object v) {
        if (v == null) {
            return null;
        }
        final String s = v.toString().trim();
        return s.isEmpty() ? null : s;
    }

    /** Thrown when the request {@code message} exceeds {@code rag.chat.message.max.length}. */
    public static class MessageTooLongException extends IOException {
        private static final long serialVersionUID = 1L;

        public MessageTooLongException(final String m) {
            super(m);
        }
    }
}
