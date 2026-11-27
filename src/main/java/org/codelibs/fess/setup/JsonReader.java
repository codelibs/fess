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
package org.codelibs.fess.setup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads a JSON document into maps, lists and strings.
 *
 * <p>The setup jar depends on the JDK only, and the JDK has no JSON parser. Some engine answers
 * have no plain-text form: the index settings are one of them. Picking values out of such an
 * answer with a regular expression breaks on the first escaped quote or backslash, which is
 * exactly what a Windows path in a setting holds, so this reads the grammar instead.</p>
 *
 * <p>An object becomes a {@link Map} in document order, an array a {@link List}, a string a
 * {@link String}. Numbers and {@code true}/{@code false} are kept as their text, and
 * {@code null} as {@code null}: nothing here does arithmetic with them.</p>
 */
final class JsonReader {

    private final String text;

    private int pos;

    private JsonReader(final String text) {
        this.text = text;
    }

    /**
     * Parses a JSON document.
     *
     * @param text the document
     * @return the value it holds
     * @throws SetupException if the text is not one JSON value
     */
    static Object parse(final String text) throws SetupException {
        final JsonReader reader = new JsonReader(text);
        try {
            final Object value = reader.value();
            reader.skipWhitespace();
            if (reader.pos != text.length()) {
                throw reader.error("unexpected text after the value");
            }
            return value;
        } catch (final IllegalStateException e) {
            throw new SetupException("Not a JSON document: " + e.getMessage());
        }
    }

    private Object value() {
        skipWhitespace();
        if (pos >= text.length()) {
            throw error("unexpected end");
        }
        final char c = text.charAt(pos);
        return switch (c) {
        case '{' -> object();
        case '[' -> array();
        case '"' -> string();
        default -> literal();
        };
    }

    private Map<String, Object> object() {
        final Map<String, Object> map = new LinkedHashMap<>();
        pos++;
        skipWhitespace();
        if (peek() == '}') {
            pos++;
            return map;
        }
        while (true) {
            skipWhitespace();
            if (peek() != '"') {
                throw error("expected a member name");
            }
            final String name = string();
            skipWhitespace();
            expect(':');
            map.put(name, value());
            skipWhitespace();
            if (peek() == ',') {
                pos++;
            } else {
                expect('}');
                return map;
            }
        }
    }

    private List<Object> array() {
        final List<Object> list = new ArrayList<>();
        pos++;
        skipWhitespace();
        if (peek() == ']') {
            pos++;
            return list;
        }
        while (true) {
            list.add(value());
            skipWhitespace();
            if (peek() == ',') {
                pos++;
            } else {
                expect(']');
                return list;
            }
        }
    }

    private String string() {
        final StringBuilder sb = new StringBuilder();
        pos++;
        while (true) {
            if (pos >= text.length()) {
                throw error("unterminated string");
            }
            final char c = text.charAt(pos++);
            if (c == '"') {
                return sb.toString();
            }
            if (c != '\\') {
                sb.append(c);
                continue;
            }
            if (pos >= text.length()) {
                throw error("unterminated string");
            }
            final char escaped = text.charAt(pos++);
            switch (escaped) {
            case '"', '\\', '/' -> sb.append(escaped);
            case 'b' -> sb.append('\b');
            case 'f' -> sb.append('\f');
            case 'n' -> sb.append('\n');
            case 'r' -> sb.append('\r');
            case 't' -> sb.append('\t');
            case 'u' -> {
                if (pos + 4 > text.length()) {
                    throw error("truncated \\u escape");
                }
                try {
                    sb.append((char) Integer.parseInt(text.substring(pos, pos + 4), 16));
                } catch (final NumberFormatException e) {
                    throw error("invalid \\u escape");
                }
                pos += 4;
            }
            default -> throw error("invalid escape \\" + escaped);
            }
        }
    }

    private String literal() {
        final int start = pos;
        while (pos < text.length() && "{}[],:\" \t\r\n".indexOf(text.charAt(pos)) < 0) {
            pos++;
        }
        final String literal = text.substring(start, pos);
        if (literal.isEmpty()) {
            throw error("unexpected '" + text.charAt(pos) + "'");
        }
        return "null".equals(literal) ? null : literal;
    }

    private void expect(final char c) {
        if (peek() != c) {
            throw error("expected '" + c + "'");
        }
        pos++;
    }

    private char peek() {
        return pos < text.length() ? text.charAt(pos) : '\0';
    }

    private void skipWhitespace() {
        while (pos < text.length() && " \t\r\n".indexOf(text.charAt(pos)) >= 0) {
            pos++;
        }
    }

    private IllegalStateException error(final String message) {
        return new IllegalStateException(message + " at offset " + pos);
    }
}
