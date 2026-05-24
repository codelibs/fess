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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Coverage-completeness test for {@link CsrfRequirement}.
 *
 * <p>The purpose of this test is to enumerate every dispatched sub-path in
 * {@code SearchApiV2Manager.process} and assert that each one has a deliberate,
 * documented CSRF decision in {@link CsrfRequirement}. The test is intentionally
 * pinned (hand-maintained list) rather than reflection-based because the dispatch
 * table lives in a switch statement and a dynamically extracted list would replicate
 * the same surface without adding independent coverage.</p>
 *
 * <p><strong>Maintenance contract:</strong> whenever a new endpoint is added to
 * {@code SearchApiV2Manager.process}, add it to the {@code ENDPOINT_DECISIONS} table
 * below with an explicit {@code true} (CSRF required) or {@code false} (exempt) entry.
 * The test will fail if the table size does not match {@code EXPECTED_ENTRY_COUNT},
 * which forces a reviewer to update this test when the endpoint set changes.</p>
 *
 * <p>Note: GET-only endpoints are always exempt (the rule "GET/HEAD/OPTIONS are always
 * exempt" is tested separately in {@link CsrfRequirementTest}). Only state-changing
 * methods (POST, PUT, DELETE) need deliberate entries — so the table here uses POST
 * for all mutating paths.</p>
 */
public class CsrfRequirementCompleteCoverageTest {

    /**
     * Exhaustive table of every sub-path dispatched by {@code SearchApiV2Manager.process}
     * paired with a boolean indicating the correct CSRF requirement for a POST request on
     * that path.
     *
     * <p>Entries for GET-only endpoints use POST intentionally — the base "GET is always
     * exempt" rule is tested elsewhere; here we document what would happen if someone sent
     * a POST to a currently-GET-only endpoint (the default is CSRF required because no
     * explicit exemption exists). This is the safe default per {@link CsrfRequirement}'s
     * documented policy.</p>
     *
     * <p>When updating this table, also update {@link #EXPECTED_ENTRY_COUNT}.</p>
     */
    private static final Map<String, Boolean> ENDPOINT_DECISIONS;

    /**
     * Must equal {@code ENDPOINT_DECISIONS.size()}. Increment when adding a new endpoint
     * so that the mismatch causes a deliberate compile-time / test-time notice.
     */
    private static final int EXPECTED_ENTRY_COUNT = 16;

    static {
        // LinkedHashMap preserves insertion order for readable failure messages.
        final Map<String, Boolean> m = new LinkedHashMap<>();

        // --- READ / GET-ONLY endpoints (secure default: CSRF required if called via POST) ---
        // These paths only support GET; a POST would reach the method-not-allowed branch
        // before any CSRF check. However, CsrfRequirement's secure default is CSRF required
        // for unknown/unlisted state-changing methods, so a POST to these paths returns true.
        // This is intentional: any path not explicitly exempted falls through to the safe default.
        m.put("/health", true); // GET only — no explicit POST exemption → secure default (CSRF required)
        m.put("/search", true); // GET only — secure default
        m.put("/suggest-words", true); // GET only — secure default
        m.put("/labels", true); // GET only — secure default
        m.put("/popular-words", true); // GET only — secure default

        // --- AUTH endpoints ---
        m.put("/auth/me", true); // GET only — secure default
        m.put("/auth/login", false); // POST, but explicitly CSRF-exempt (no token yet)
        m.put("/auth/logout", true); // POST, CSRF required
        m.put("/auth/password", true); // POST, CSRF required

        // --- UI endpoint ---
        m.put("/ui/config", true); // GET only — secure default

        // --- CLICK / FAVORITE endpoints ---
        m.put("/click", true); // POST, CSRF required
        // /documents/{id}/favorite is a pattern — tested via representative docId
        m.put("/documents/abc123/favorite", true); // POST, CSRF required

        // --- CHAT endpoints ---
        m.put("/chat", true); // POST, CSRF required
        m.put("/chat/stream", true); // POST, CSRF required

        // --- CHAT SESSION endpoints (DELETE /chat/sessions/{session_id}) ---
        // DELETE is a state-changing method; the subPath.startsWith("/chat/sessions/")
        // rule in CsrfRequirement applies regardless of the trailing session_id value.
        m.put("/chat/sessions/abc", true); // DELETE, CSRF required
        m.put("/chat/sessions/def", true); // DELETE, CSRF required (second representative)

        ENDPOINT_DECISIONS = java.util.Collections.unmodifiableMap(m);
    }

    @Test
    public void test_entryCounts_matchExpectedEntryCount() {
        // If this assertion fails, a new endpoint was added to ENDPOINT_DECISIONS without
        // updating EXPECTED_ENTRY_COUNT. Fix by updating the constant above.
        assertTrue(ENDPOINT_DECISIONS.size() == EXPECTED_ENTRY_COUNT, "ENDPOINT_DECISIONS has " + ENDPOINT_DECISIONS.size()
                + " entries but EXPECTED_ENTRY_COUNT=" + EXPECTED_ENTRY_COUNT + "; update the constant when adding or removing endpoints");
    }

    @Test
    public void test_everyEndpointHasDeliberateCsrfDecision() {
        // Enumerate every entry and assert the decision matches CsrfRequirement.requiresCsrf.
        final List<String> failures = new java.util.ArrayList<>();
        for (final Map.Entry<String, Boolean> entry : ENDPOINT_DECISIONS.entrySet()) {
            final String subPath = entry.getKey();
            final boolean expectedCsrf = entry.getValue();
            final boolean actualCsrf = CsrfRequirement.requiresCsrf(subPath, "POST");
            if (actualCsrf != expectedCsrf) {
                failures.add(String.format("  subPath=%-35s POST: expected csrf=%b, got=%b", subPath, expectedCsrf, actualCsrf));
            }
        }
        if (!failures.isEmpty()) {
            fail("CsrfRequirement decision mismatch for the following endpoints:\n" + String.join("\n", failures)
                    + "\n\nFor each mismatch, either:\n"
                    + "  (a) update CsrfRequirement.requiresCsrf() to reflect the intended decision, or\n"
                    + "  (b) update this test's ENDPOINT_DECISIONS table if the intended decision changed.");
        }
    }

    @Test
    public void test_stateChangingEndpointsAreAllCsrfRequired() {
        // Secondary assertion: all paths that POST/DELETE actual state changes (auth/logout,
        // auth/password, click, favorite, chat, chat/stream, chat/sessions) must require CSRF.
        final List<String> stateChangingPost =
                Arrays.asList("/auth/logout", "/auth/password", "/click", "/documents/abc123/favorite", "/chat", "/chat/stream");
        for (final String path : stateChangingPost) {
            assertTrue(CsrfRequirement.requiresCsrf(path, "POST"), "State-changing endpoint must require CSRF token: POST " + path);
        }
        // DELETE /chat/sessions/{session_id} is state-changing — CSRF required
        assertTrue(CsrfRequirement.requiresCsrf("/chat/sessions/abc", "DELETE"),
                "DELETE /chat/sessions/{session_id} must require CSRF token");
    }

    @Test
    public void test_deleteChatSessions_requiresCsrf() {
        // DELETE /chat/sessions/{session_id} was added as a state-mutating endpoint.
        // Verify several representative session_id values all require CSRF.
        assertTrue(CsrfRequirement.requiresCsrf("/chat/sessions/abc", "DELETE"), "DELETE /chat/sessions/abc must require CSRF");
        assertTrue(CsrfRequirement.requiresCsrf("/chat/sessions/def", "DELETE"), "DELETE /chat/sessions/def must require CSRF");
        assertTrue(CsrfRequirement.requiresCsrf("/chat/sessions/", "DELETE"), "DELETE /chat/sessions/ (trailing slash) must require CSRF");
    }

    @Test
    public void test_getChatSessions_isExemptByGetRule() {
        // GET on chat/sessions path must be exempt — GET is always idempotent
        assertFalse(CsrfRequirement.requiresCsrf("/chat/sessions/abc", "GET"), "GET /chat/sessions/{session_id} must be CSRF-exempt");
    }

    @Test
    public void test_loginIsExplicitlyExempt() {
        // /auth/login must be CSRF-exempt because the client has no token yet.
        assertFalse(CsrfRequirement.requiresCsrf("/auth/login", "POST"), "/auth/login POST must be CSRF-exempt");
    }

    @Test
    public void test_getMethodIsAlwaysExemptForAllKnownPaths() {
        // All known endpoints: GET must never require CSRF regardless of path.
        for (final String subPath : ENDPOINT_DECISIONS.keySet()) {
            assertFalse(CsrfRequirement.requiresCsrf(subPath, "GET"), "GET must always be CSRF-exempt, including: " + subPath);
        }
    }
}
