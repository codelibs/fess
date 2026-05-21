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
 */
public final class ChatRequestBody {

    private final String message;
    private final String sessionId;
    private final boolean clear;
    private final Map<String, String[]> fields;
    private final String[] extraQueries;

    private ChatRequestBody(final String message, final String sessionId, final boolean clear, final Map<String, String[]> fields,
            final String[] extraQueries) {
        this.message = message;
        this.sessionId = sessionId;
        this.clear = clear;
        this.fields = fields;
        this.extraQueries = extraQueries;
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

    public static ChatRequestBody from(final Map<String, Object> raw, final int maxMessageLength) throws IOException {
        final String message = trimmedOrNull(raw.get("message"));
        final String sessionId = trimmedOrNull(raw.get("session_id"));
        final boolean clear = parseBoolean(raw.get("clear"));
        if (message != null && message.length() > maxMessageLength) {
            throw new MessageTooLongException("message exceeds max length: " + message.length() + " > " + maxMessageLength);
        }
        final Map<String, String[]> fields = parseFields(raw);
        final String[] extraQueries = parseExtraQueries(raw);
        return new ChatRequestBody(message, sessionId, clear, fields, extraQueries);
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
    private static Map<String, String[]> parseFields(final Map<String, Object> raw) {
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
        }
        final List<String> valid = new ArrayList<>();
        for (final String v : labelValues) {
            if (v != null && allowed.contains(v)) {
                valid.add(v);
            }
        }
        if (valid.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<String, String[]> out = new HashMap<>();
        out.put("label", valid.toArray(new String[0]));
        return out;
    }

    @SuppressWarnings("unchecked")
    private static String[] parseExtraQueries(final Map<String, Object> raw) {
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
        }
        final List<String> valid = new ArrayList<>();
        for (final String v : values) {
            if (v != null && allowed.contains(v)) {
                valid.add(v);
            }
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
