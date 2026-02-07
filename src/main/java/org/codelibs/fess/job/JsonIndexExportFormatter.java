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
package org.codelibs.fess.job;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Formatter that outputs index documents as JSON files.
 */
public class JsonIndexExportFormatter implements IndexExportFormatter {

    /**
     * Creates a new JsonIndexExportFormatter instance.
     */
    public JsonIndexExportFormatter() {
        // default constructor
    }

    @Override
    public String getFileExtension() {
        return ".json";
    }

    @Override
    public String getIndexFileName() {
        return "index.json";
    }

    @Override
    public String format(final Map<String, Object> source, final Set<String> excludeFields) {
        final StringBuilder json = new StringBuilder();
        json.append("{\n");

        boolean first = true;
        for (final Map.Entry<String, Object> entry : source.entrySet()) {
            final String field = entry.getKey();
            if (excludeFields.contains(field)) {
                continue;
            }

            final Object value = entry.getValue();
            if (value == null) {
                continue;
            }

            if (!first) {
                json.append(",\n");
            }
            first = false;

            json.append("  \"").append(escapeJson(field)).append("\": ");
            appendJsonValue(json, value);
        }

        json.append("\n}\n");
        return json.toString();
    }

    private void appendJsonValue(final StringBuilder json, final Object value) {
        if (value instanceof Collection) {
            json.append("[");
            boolean first = true;
            for (final Object item : (Collection<?>) value) {
                if (!first) {
                    json.append(", ");
                }
                first = false;
                appendJsonValue(json, item);
            }
            json.append("]");
        } else if (value instanceof Map) {
            json.append("{");
            boolean first = true;
            for (final Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                if (!first) {
                    json.append(", ");
                }
                first = false;
                json.append("\"").append(escapeJson(String.valueOf(entry.getKey()))).append("\": ");
                appendJsonValue(json, entry.getValue());
            }
            json.append("}");
        } else if (value instanceof Number) {
            json.append(value);
        } else if (value instanceof Boolean) {
            json.append(value);
        } else {
            json.append("\"").append(escapeJson(String.valueOf(value))).append("\"");
        }
    }

    private String escapeJson(final String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            final char c = text.charAt(i);
            switch (c) {
            case '"':
                sb.append("\\\"");
                break;
            case '\\':
                sb.append("\\\\");
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\r':
                sb.append("\\r");
                break;
            case '\t':
                sb.append("\\t");
                break;
            default:
                if (c < 0x20) {
                    sb.append(String.format("\\u%04x", (int) c));
                } else {
                    sb.append(c);
                }
                break;
            }
        }
        return sb.toString();
    }
}
