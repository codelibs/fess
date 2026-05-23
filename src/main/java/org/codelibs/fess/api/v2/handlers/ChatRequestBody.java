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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.entity.FacetQueryView;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Parsed view of a chat request body shared by {@link ChatHandler} and
 * {@link ChatStreamHandler}.
 *
 * <p>The body shape mirrors v1 chat (see {@code ChatApiManager#processChatRequest}),
 * but field names use snake_case ({@code session_id} not {@code sessionId},
 * {@code ex_q} stays as-is, {@code fields.label} is encoded as a JSON array under
 * key {@code "fields.label"} when present). The original v1 query-param names are
 * kept under the same string keys so a future tweak can switch transport without
 * client churn.</p>
 *
 * <p>Length is enforced inside {@link #from(Map, int)} so the caller does not need
 * a separate guard. Label and {@code ex_q} validation uses the same allowlist
 * helpers v1 calls — {@code LabelTypeHelper} for labels and {@code ViewHelper}
 * for facet queries — to prevent query injection.</p>
 *
 * <p>MJ-29: When supplied values are rejected by the allowlist, the rejected
 * values are tracked in the {@link #warnings} list returned by
 * {@link #getWarnings()}. Callers ({@link ChatHandler}, {@link ChatStreamHandler})
 * should surface these in the response envelope. NOTE: ChatHandler/ChatStreamHandler
 * do not yet surface the warnings field — this is a follow-up item for the wire
 * spec. The structure is ready here so callers can opt in.</p>
 *
 * <p>MJ-30 i18n note: error.message is developer-facing English. Clients MUST use
 * error.code (the V2ErrorCode token) for user-facing i18n.</p>
 */
public final class ChatRequestBody {

    private static final Logger logger = LogManager.getLogger(ChatRequestBody.class);

    private final String message;
    private final String sessionId;
    private final boolean clear;
    private final Map<String, String[]> fields;
    private final String[] extraQueries;

    /**
     * MJ-29: Tracks values that were supplied by the caller but rejected by the
     * allowlist. Indexed by field name (e.g. "fields.label", "ex_q") with a list
     * of the rejected string values. Empty when nothing was dropped.
     *
     * <p>ChatHandler and ChatStreamHandler should include this map under a
     * {@code "warnings"} key in the response envelope (follow-up item).</p>
     */
    private final Map<String, List<String>> warnings;

    private ChatRequestBody(final String message, final String sessionId, final boolean clear, final Map<String, String[]> fields,
            final String[] extraQueries, final Map<String, List<String>> warnings) {
        this.message = message;
        this.sessionId = sessionId;
        this.clear = clear;
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

    public boolean isClear() {
        return clear;
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
     * Keys are the request field names: {@code "fields.label"} and {@code "ex_q"}.
     *
     * <p>ChatHandler / ChatStreamHandler follow-up: surface this under a
     * {@code "warnings"} key in the response envelope so clients know which
     * filter values were silently dropped by the allowlist.</p>
     *
     * @return unmodifiable map of rejected field→value-list pairs; empty if nothing was dropped
     */
    public Map<String, List<String>> getWarnings() {
        return Collections.unmodifiableMap(warnings);
    }

    public static ChatRequestBody from(final Map<String, Object> raw, final int maxMessageLength) throws IOException {
        final String message = trimmedOrNull(raw.get("message"));
        final String sessionId = trimmedOrNull(raw.get("session_id"));
        final boolean clear = parseBoolean(raw.get("clear"));
        if (message != null && message.length() > maxMessageLength) {
            throw new MessageTooLongException("message exceeds max length: " + message.length() + " > " + maxMessageLength);
        }
        final Map<String, List<String>> warnings = new HashMap<>();
        final Map<String, String[]> fields = parseFields(raw, warnings);
        final String[] extraQueries = parseExtraQueries(raw, warnings);
        return new ChatRequestBody(message, sessionId, clear, fields, extraQueries, warnings);
    }

    private static String trimmedOrNull(final Object v) {
        if (v == null) {
            return null;
        }
        final String s = v.toString().trim();
        return s.isEmpty() ? null : s;
    }

    private static boolean parseBoolean(final Object v) {
        if (v == null) {
            return false;
        }
        if (v instanceof Boolean b) {
            return b;
        }
        return "true".equalsIgnoreCase(v.toString());
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String[]> parseFields(final Map<String, Object> raw, final Map<String, List<String>> warnings) {
        final Object labelsRaw = raw.get("fields.label");
        if (labelsRaw == null) {
            return Collections.emptyMap();
        }
        final List<String> labelValues = new ArrayList<>();
        if (labelsRaw instanceof List<?> l) {
            for (final Object o : l) {
                if (o != null) {
                    labelValues.add(o.toString());
                }
            }
        } else {
            labelValues.add(labelsRaw.toString());
        }
        if (labelValues.isEmpty()) {
            return Collections.emptyMap();
        }
        // Validate against the configured label type allowlist (same logic v1 uses).
        final Set<String> allowed = new HashSet<>();
        try {
            ComponentUtil.getLabelTypeHelper()
                    .getLabelTypeItemList(SearchRequestType.SEARCH, Locale.ROOT)
                    .stream()
                    .map(m -> m.get("value"))
                    .forEach(allowed::add);
        } catch (final Exception e) {
            // helper unavailable in unit tests — degrade to empty allowlist
            logger.warn("chat allowlist load failed for labels", e);
        }
        final List<String> valid = new ArrayList<>();
        final List<String> rejected = new ArrayList<>();
        for (final String v : labelValues) {
            if (v != null && allowed.contains(v)) {
                valid.add(v);
            } else if (v != null) {
                // MJ-29: track the rejected value so callers can surface it as a warning.
                rejected.add(v);
            }
        }
        if (!rejected.isEmpty()) {
            warnings.put("fields.label", Collections.unmodifiableList(rejected));
        }
        if (valid.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<String, String[]> out = new HashMap<>();
        out.put("label", valid.toArray(new String[0]));
        return out;
    }

    @SuppressWarnings("unchecked")
    private static String[] parseExtraQueries(final Map<String, Object> raw, final Map<String, List<String>> warnings) {
        final Object exqRaw = raw.get("ex_q");
        if (exqRaw == null) {
            return new String[0];
        }
        final List<String> values = new ArrayList<>();
        if (exqRaw instanceof List<?> l) {
            for (final Object o : l) {
                if (o != null) {
                    values.add(o.toString());
                }
            }
        } else {
            values.add(exqRaw.toString());
        }
        if (values.isEmpty()) {
            return new String[0];
        }
        final Set<String> allowed = new HashSet<>();
        try {
            for (final FacetQueryView view : ComponentUtil.getViewHelper().getFacetQueryViewList()) {
                allowed.addAll(view.getQueryMap().values());
            }
        } catch (final Exception e) {
            // helper unavailable in unit tests — degrade to empty allowlist
            logger.warn("chat allowlist load failed for ex_q", e);
        }
        final List<String> valid = new ArrayList<>();
        final List<String> rejected = new ArrayList<>();
        for (final String v : values) {
            if (v != null && allowed.contains(v)) {
                valid.add(v);
            } else if (v != null) {
                // MJ-29: track the rejected value so callers can surface it as a warning.
                rejected.add(v);
            }
        }
        if (!rejected.isEmpty()) {
            warnings.put("ex_q", Collections.unmodifiableList(rejected));
        }
        return valid.toArray(new String[0]);
    }

    /** Thrown when the request {@code message} exceeds {@code rag.chat.message.max.length}. */
    public static class MessageTooLongException extends IOException {
        private static final long serialVersionUID = 1L;

        public MessageTooLongException(final String m) {
            super(m);
        }
    }
}
