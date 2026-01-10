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

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class LdapUtilTest extends UnitFessTestCase {

    @Test
    public void test_escapeValue_null() {
        assertEquals("", LdapUtil.escapeValue(null));
    }

    @Test
    public void test_escapeValue_empty() {
        assertEquals("", LdapUtil.escapeValue(""));
    }

    @Test
    public void test_escapeValue_noSpecialChars() {
        assertEquals("admin", LdapUtil.escapeValue("admin"));
        assertEquals("john.doe", LdapUtil.escapeValue("john.doe"));
        assertEquals("user123", LdapUtil.escapeValue("user123"));
        assertEquals("Test User", LdapUtil.escapeValue("Test User"));
    }

    @Test
    public void test_escapeValue_backslash() {
        assertEquals("admin\\5ctest", LdapUtil.escapeValue("admin\\test"));
        assertEquals("\\5c", LdapUtil.escapeValue("\\"));
        assertEquals("a\\5cb\\5cc", LdapUtil.escapeValue("a\\b\\c"));
    }

    @Test
    public void test_escapeValue_asterisk() {
        assertEquals("admin\\2a", LdapUtil.escapeValue("admin*"));
        assertEquals("\\2a", LdapUtil.escapeValue("*"));
        assertEquals("\\2a\\2a\\2a", LdapUtil.escapeValue("***"));
        assertEquals("test\\2auser", LdapUtil.escapeValue("test*user"));
    }

    @Test
    public void test_escapeValue_parentheses() {
        assertEquals("\\28admin\\29", LdapUtil.escapeValue("(admin)"));
        assertEquals("\\28", LdapUtil.escapeValue("("));
        assertEquals("\\29", LdapUtil.escapeValue(")"));
        assertEquals("user\\28test\\29name", LdapUtil.escapeValue("user(test)name"));
    }

    @Test
    public void test_escapeValue_nullChar() {
        assertEquals("admin\\00test", LdapUtil.escapeValue("admin\0test"));
        assertEquals("\\00", LdapUtil.escapeValue("\0"));
    }

    @Test
    public void test_escapeValue_mixedSpecialChars() {
        // Test LDAP injection attempt: admin*
        assertEquals("admin\\2a", LdapUtil.escapeValue("admin*"));

        // Test LDAP injection attempt: )(cn=*
        assertEquals("\\29\\28cn=\\2a", LdapUtil.escapeValue(")(cn=*"));

        // Test LDAP injection attempt: *)(uid=*))(|(uid=*
        assertEquals("\\2a\\29\\28uid=\\2a\\29\\29\\28|\\28uid=\\2a", LdapUtil.escapeValue("*)(uid=*))(|(uid=*"));

        // Test complex injection: admin)(&(password=*))
        assertEquals("admin\\29\\28&\\28password=\\2a\\29\\29", LdapUtil.escapeValue("admin)(&(password=*))"));
    }

    @Test
    public void test_escapeValue_allSpecialCharsInSequence() {
        assertEquals("\\5c\\2a\\28\\29\\00", LdapUtil.escapeValue("\\*()\0"));
    }

    @Test
    public void test_escapeValue_unicodeCharacters() {
        // Unicode characters should pass through unchanged
        assertEquals("user", LdapUtil.escapeValue("user"));
        assertEquals("admin", LdapUtil.escapeValue("admin"));
    }

    @Test
    public void test_escapeValue_longString() {
        String input = "a]".repeat(1000) + "*";
        String expected = "a]".repeat(1000) + "\\2a";
        assertEquals(expected, LdapUtil.escapeValue(input));
    }

    @Test
    public void test_escapeValue_realWorldLdapInjectionAttempts() {
        // Authentication bypass attempt
        assertEquals("\\2a", LdapUtil.escapeValue("*"));

        // Filter injection to match any user
        assertEquals("\\2a\\29\\28|\\28cn=\\2a", LdapUtil.escapeValue("*)(|(cn=*"));

        // Attempt to close filter and add OR condition
        assertEquals("admin\\29\\28|\\28password=\\2a\\29", LdapUtil.escapeValue("admin)(|(password=*)"));

        // NULL byte injection
        assertEquals("admin\\00", LdapUtil.escapeValue("admin\0"));

        // Escape sequence confusion
        assertEquals("admin\\5c\\2a", LdapUtil.escapeValue("admin\\*"));
    }
}
