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

import java.util.Locale;

/**
 * Static decision table for spec §7.3 CSRF rules.
 *
 * <p><strong>Default policy: CSRF token is REQUIRED for all POST/PUT/DELETE requests.</strong>
 * Exemptions must be explicitly listed in {@link #requiresCsrf}. This means that if a new
 * state-changing endpoint is added to {@code SearchApiV2Manager.process} and its sub-path is
 * not added to this class, it will inherit the safe default (CSRF required) rather than being
 * silently exempt.</p>
 *
 * <p>All GET/HEAD/OPTIONS requests are always exempt (idempotent methods carry no state change
 * risk from CSRF). {@code /auth/login} POST is additionally exempt because the client cannot
 * yet possess a token at the time of the initial login request.</p>
 *
 * <p>{@code /chat} POST and {@code /chat/stream} POST are CSRF-gated per spec §7.3.
 * GET requests on either path are exempt (the rule table only checks state-changing methods).</p>
 *
 * <p><strong>Maintenance contract:</strong> whenever a new endpoint is added to
 * {@code SearchApiV2Manager.process}, update this class with an explicit entry and extend
 * {@code CsrfRequirementCompleteCoverageTest} to pin the decision. Failing to do so is safe
 * (the new path is CSRF-required by default), but the coverage test will flag the omission.</p>
 */
public final class CsrfRequirement {

    private CsrfRequirement() {
    }

    /**
     * Returns whether the given v2 sub-path and HTTP method combination must
     * pass the CSRF token check.
     *
     * <p>Idempotent methods ({@code GET}, {@code HEAD}, {@code OPTIONS}) are
     * always exempt. {@code POST /auth/login} is exempt because the client
     * cannot yet possess a token; all other state-changing endpoints listed in
     * the class Javadoc require a valid token. Unknown sub-paths fall through
     * to the safe default (no enforcement for unknown reads, enforcement
     * implied by the {@code SearchApiV2Manager} dispatch table for known
     * writes).</p>
     *
     * @param subPath the v2 sub-path (e.g. {@code /chat}, {@code /auth/login})
     * @param method the HTTP method (case-insensitive)
     * @return {@code true} if a valid CSRF token must accompany the request
     */
    public static boolean requiresCsrf(final String subPath, final String method) {
        if (method == null) {
            return false;
        }
        final String m = method.toUpperCase(Locale.ROOT);
        if ("GET".equals(m) || "HEAD".equals(m) || "OPTIONS".equals(m)) {
            return false;
        }
        if (subPath == null) {
            return false;
        }
        if ("/auth/login".equals(subPath)) {
            return false;
        }
        if ("/auth/logout".equals(subPath)) {
            return true;
        }
        if ("/auth/password".equals(subPath)) {
            return true;
        }
        if ("/click".equals(subPath)) {
            return true;
        }
        if (subPath.startsWith("/documents/") && subPath.endsWith("/favorite")) {
            return true;
        }
        if ("/chat".equals(subPath)) {
            return true;
        }
        if ("/chat/stream".equals(subPath)) {
            return true;
        }
        if (subPath.startsWith("/chat/sessions/")) {
            return true;
        }
        return false;
    }
}
