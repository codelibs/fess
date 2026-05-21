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
import org.junit.jupiter.api.Test;

public class CsrfRequirementTest {

    @Test
    public void test_getNeverRequiresCsrf() {
        assertFalse(CsrfRequirement.requiresCsrf("/search", "GET"));
        assertFalse(CsrfRequirement.requiresCsrf("/auth/me", "GET"));
        assertFalse(CsrfRequirement.requiresCsrf("/documents/abc/favorite", "GET"));
    }

    @Test
    public void test_loginIsExemptFromCsrf() {
        assertFalse(CsrfRequirement.requiresCsrf("/auth/login", "POST"));
    }

    @Test
    public void test_logoutRequiresCsrf() {
        assertTrue(CsrfRequirement.requiresCsrf("/auth/logout", "POST"));
    }

    @Test
    public void test_passwordRequiresCsrf() {
        assertTrue(CsrfRequirement.requiresCsrf("/auth/password", "POST"));
    }

    @Test
    public void test_favoritePostRequiresCsrf() {
        assertTrue(CsrfRequirement.requiresCsrf("/documents/abc/favorite", "POST"));
    }

    @Test
    public void test_clickRequiresCsrf() {
        assertTrue(CsrfRequirement.requiresCsrf("/click", "POST"));
    }

    @Test
    public void test_chatPostRequiresCsrf() {
        assertTrue(CsrfRequirement.requiresCsrf("/chat", "POST"));
    }

    @Test
    public void test_chatStreamPostRequiresCsrf() {
        assertTrue(CsrfRequirement.requiresCsrf("/chat/stream", "POST"));
    }

    @Test
    public void test_chatGetIsExemptFromCsrf() {
        assertFalse(CsrfRequirement.requiresCsrf("/chat", "GET"));
        assertFalse(CsrfRequirement.requiresCsrf("/chat/stream", "GET"));
    }

    @Test
    public void test_chatTrailingSlashNotMatched() {
        // Defensive: the manager normalizes paths so /chat/ would not reach here,
        // but verify the rule table doesn't accidentally CSRF-gate the trailing slash.
        assertFalse(CsrfRequirement.requiresCsrf("/chat/", "POST"));
    }
}
