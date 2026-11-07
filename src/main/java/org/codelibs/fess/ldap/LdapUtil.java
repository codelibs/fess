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
package org.codelibs.fess.ldap;

/**
 * Utility class for LDAP operations.
 */
public final class LdapUtil {

    private LdapUtil() {
    }

    /**
     * Escapes special characters in a value for use in LDAP search filters.
     * This method escapes characters that have special meaning in LDAP filter expressions
     * to prevent LDAP injection attacks.
     *
     * @param value the value to escape (null is treated as empty string)
     * @return the escaped value safe for use in LDAP search filters
     * @see <a href="https://tools.ietf.org/html/rfc4515">RFC 4515 - LDAP String Representation of Search Filters</a>
     */
    public static String escapeValue(final String value) {
        if (value == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(value.length() * 2);
        for (int i = 0; i < value.length(); i++) {
            final char c = value.charAt(i);
            switch (c) {
            case '\\' -> sb.append("\\5c");
            case '*' -> sb.append("\\2a");
            case '(' -> sb.append("\\28");
            case ')' -> sb.append("\\29");
            case '\0' -> sb.append("\\00");
            default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Escapes a value that is placed in a distinguished name (for example the {@code uid} of a
     * bind principal such as {@code uid=%s,ou=People,dc=example,dc=com}).
     *
     * <p>It escapes everything {@link #escapeValue(String)} escapes, and in addition the characters
     * that end or split a relative distinguished name ({@code , + " < > ;}), a leading {@code #}
     * or space, and a trailing space, as RFC 4514 requires. Without it a user name such as
     * {@code a,b} produces a malformed (or different) DN.</p>
     *
     * @param value the value to escape (null is treated as empty string)
     * @return the escaped value safe for use as an attribute value inside a DN
     * @see <a href="https://tools.ietf.org/html/rfc4514">RFC 4514 - LDAP String Representation of Distinguished Names</a>
     */
    public static String escapeDnValue(final String value) {
        if (value == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(value.length() * 2);
        final int last = value.length() - 1;
        for (int i = 0; i <= last; i++) {
            final char c = value.charAt(i);
            switch (c) {
            case '\\' -> sb.append("\\5c");
            case '*' -> sb.append("\\2a");
            case '(' -> sb.append("\\28");
            case ')' -> sb.append("\\29");
            case '\0' -> sb.append("\\00");
            case ',', '+', '"', '<', '>', ';' -> sb.append('\\').append(c);
            case '#' -> sb.append(i == 0 ? "\\#" : "#");
            case ' ' -> sb.append(i == 0 || i == last ? "\\ " : " ");
            default -> sb.append(c);
            }
        }
        return sb.toString();
    }
}
