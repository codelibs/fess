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
        assertFalse(new CsrfRequirement().requiresCsrf("/search", "GET"));
        assertFalse(new CsrfRequirement().requiresCsrf("/auth/me", "GET"));
        assertFalse(new CsrfRequirement().requiresCsrf("/documents/abc/favorite", "GET"));
    }

    @Test
    public void test_loginIsExemptFromCsrf() {
        assertFalse(new CsrfRequirement().requiresCsrf("/auth/login", "POST"));
    }

    @Test
    public void test_logoutRequiresCsrf() {
        assertTrue(new CsrfRequirement().requiresCsrf("/auth/logout", "POST"));
    }

    @Test
    public void test_passwordRequiresCsrf() {
        assertTrue(new CsrfRequirement().requiresCsrf("/auth/password", "POST"));
    }

    @Test
    public void test_favoritePostRequiresCsrf() {
        assertTrue(new CsrfRequirement().requiresCsrf("/documents/abc/favorite", "POST"));
    }

    @Test
    public void test_clickRequiresCsrf() {
        assertTrue(new CsrfRequirement().requiresCsrf("/click", "POST"));
    }

    @Test
    public void test_chatPostRequiresCsrf() {
        assertTrue(new CsrfRequirement().requiresCsrf("/chat", "POST"));
    }

    @Test
    public void test_chatStreamPostRequiresCsrf() {
        assertTrue(new CsrfRequirement().requiresCsrf("/chat/stream", "POST"));
    }

    @Test
    public void test_chatGetIsExemptFromCsrf() {
        assertFalse(new CsrfRequirement().requiresCsrf("/chat", "GET"));
        assertFalse(new CsrfRequirement().requiresCsrf("/chat/stream", "GET"));
    }

    @Test
    public void test_chatTrailingSlashInheritsSecureDefault() {
        // The manager normalizes paths before dispatch so /chat/ is normalized to /chat
        // before it reaches CsrfRequirement. The trailing-slash form is therefore not an
        // exempted path and falls through to the secure default: CSRF required.
        assertTrue(new CsrfRequirement().requiresCsrf("/chat/", "POST"));
    }

    @Test
    public void test_unknownStateChangingPath_requiresCsrfByDefault() {
        // Regression guard for the secure-default contract: any unknown sub-path with a
        // state-changing method (POST/PUT/DELETE) must require CSRF so that a new endpoint
        // added to SearchApiV2Manager.process without a corresponding CsrfRequirement entry
        // is CSRF-gated rather than silently exempt.
        assertTrue(new CsrfRequirement().requiresCsrf("/some/future/path", "POST"));
        assertTrue(new CsrfRequirement().requiresCsrf("/unknown/endpoint", "PUT"));
        assertTrue(new CsrfRequirement().requiresCsrf("/another/new/path", "DELETE"));
    }
}
