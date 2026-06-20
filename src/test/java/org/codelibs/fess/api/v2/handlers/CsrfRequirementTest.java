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
    public void test_chatTrailingSlashInheritsSecureDefault() {
        // The manager normalizes paths before dispatch so /chat/ is normalized to /chat
        // before it reaches CsrfRequirement. The trailing-slash form is therefore not an
        // exempted path and falls through to the secure default: CSRF required.
        assertTrue(CsrfRequirement.requiresCsrf("/chat/", "POST"));
    }

    @Test
    public void test_unknownStateChangingPath_requiresCsrfByDefault() {
        // Regression guard for the secure-default contract: any unknown sub-path with a
        // state-changing method (POST/PUT/DELETE) must require CSRF so that a new endpoint
        // added to SearchApiV2Manager.process without a corresponding CsrfRequirement entry
        // is CSRF-gated rather than silently exempt.
        assertTrue(CsrfRequirement.requiresCsrf("/some/future/path", "POST"));
        assertTrue(CsrfRequirement.requiresCsrf("/unknown/endpoint", "PUT"));
        assertTrue(CsrfRequirement.requiresCsrf("/another/new/path", "DELETE"));
    }

    // isUnsafeMethod: safe (GET/HEAD/OPTIONS) vs unsafe (state-changing) methods

    @Test
    public void test_isUnsafeMethod_safeMethodsAreFalse() {
        assertFalse(CsrfRequirement.isUnsafeMethod("GET"));
        assertFalse(CsrfRequirement.isUnsafeMethod("HEAD"));
        assertFalse(CsrfRequirement.isUnsafeMethod("OPTIONS"));
    }

    @Test
    public void test_isUnsafeMethod_stateChangingMethodsAreTrue() {
        assertTrue(CsrfRequirement.isUnsafeMethod("POST"));
        assertTrue(CsrfRequirement.isUnsafeMethod("PUT"));
        assertTrue(CsrfRequirement.isUnsafeMethod("DELETE"));
        assertTrue(CsrfRequirement.isUnsafeMethod("PATCH"));
    }

    @Test
    public void test_isUnsafeMethod_isCaseInsensitive() {
        assertFalse(CsrfRequirement.isUnsafeMethod("get"));
        assertFalse(CsrfRequirement.isUnsafeMethod("Head"));
        assertFalse(CsrfRequirement.isUnsafeMethod("options"));
        assertTrue(CsrfRequirement.isUnsafeMethod("post"));
        assertTrue(CsrfRequirement.isUnsafeMethod("Delete"));
    }

    @Test
    public void test_isUnsafeMethod_nullIsFalse() {
        assertFalse(CsrfRequirement.isUnsafeMethod(null));
    }
}
