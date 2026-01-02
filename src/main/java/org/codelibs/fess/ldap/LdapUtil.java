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
            case '\\':
                sb.append("\\5c");
                break;
            case '*':
                sb.append("\\2a");
                break;
            case '(':
                sb.append("\\28");
                break;
            case ')':
                sb.append("\\29");
                break;
            case '\0':
                sb.append("\\00");
                break;
            default:
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
