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
import java.util.List;
import java.util.Map;

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
 * <p>Length is enforced inside {@link org.codelibs.fess.helper.ChatApiHelper#parseRequestBody(Map, int)}
 * so the caller does not need
 * a separate guard. Label and {@code extra_queries} validation uses the same allowlist
 * helpers v1 calls — {@code LabelTypeHelper} for labels and {@code ViewHelper}
 * for facet queries — to prevent query injection, delegated to the
 * {@code chatApiHelper} component (obtained via {@code ComponentUtil.getChatApiHelper()}).</p>
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

    /**
     * Creates a parsed chat request body. Instances are normally produced by
     * {@link org.codelibs.fess.helper.ChatApiHelper#parseRequestBody(Map, int)} after the
     * raw body has been validated, so callers rarely invoke this constructor directly.
     *
     * @param message the trimmed {@code message} value, or {@code null} when omitted or blank
     * @param sessionId the trimmed {@code session_id} value, or {@code null} for a fresh session
     * @param fields the validated label-filter map; never {@code null}
     * @param extraQueries the validated {@code extra_queries} array; never {@code null}
     * @param warnings the map of rejected field values tracked for diagnostics; never {@code null}
     */
    public ChatRequestBody(final String message, final String sessionId, final Map<String, String[]> fields, final String[] extraQueries,
            final Map<String, List<String>> warnings) {
        this.message = message;
        this.sessionId = sessionId;
        this.fields = fields;
        this.extraQueries = extraQueries;
        this.warnings = warnings;
    }

    /**
     * Returns the trimmed {@code message} value supplied by the caller.
     *
     * @return the chat message, or {@code null} if the caller omitted it or sent only whitespace
     */
    public String message() {
        return message;
    }

    /**
     * Returns the trimmed {@code session_id} value supplied by the caller.
     *
     * @return the chat session id, or {@code null} when the caller is starting a fresh session
     */
    public String sessionId() {
        return sessionId;
    }

    /**
     * Returns the validated label-filter map ({@code fields.label} entries).
     *
     * @return map of label field name to allowlisted value arrays; never {@code null}
     */
    public Map<String, String[]> fields() {
        return fields;
    }

    /**
     * Returns the validated {@code extra_queries} array supplied by the caller.
     *
     * @return allowlisted facet queries, or an empty array when none were supplied
     */
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

    /** Base for chat request body validation errors that map to HTTP 400. */
    public static class InvalidRequestException extends IOException {
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new exception describing the validation violation.
         *
         * @param m the developer-facing detail message
         */
        public InvalidRequestException(final String m) {
            super(m);
        }
    }

    /** Thrown when the request {@code message} exceeds {@code rag.chat.message.max.length}. */
    public static class MessageTooLongException extends InvalidRequestException {
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new exception describing the length violation.
         *
         * @param m the developer-facing detail message
         */
        public MessageTooLongException(final String m) {
            super(m);
        }
    }

    /** Thrown when session_id exceeds the maximum length or contains invalid characters. */
    public static class InvalidSessionIdException extends InvalidRequestException {
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new exception describing the session_id violation.
         *
         * @param m the developer-facing detail message
         */
        public InvalidSessionIdException(final String m) {
            super(m);
        }
    }

    /** Thrown when an array parameter (extra_queries, fields.label) exceeds the count or per-element length limit. */
    public static class TooManyValuesException extends InvalidRequestException {
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new exception describing the array size or element length violation.
         *
         * @param m the developer-facing detail message
         */
        public TooManyValuesException(final String m) {
            super(m);
        }
    }
}
