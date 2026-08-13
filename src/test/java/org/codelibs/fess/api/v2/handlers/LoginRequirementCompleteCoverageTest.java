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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Coverage-completeness test for {@link LoginRequirement}.
 *
 * <p>Enumerates every sub-path dispatched by {@code SearchApiV2Manager.process} and pins
 * whether it may be served to an anonymous caller while {@code login.required} is enabled.
 * The list is hand-maintained rather than reflection-derived: the dispatch table is a switch
 * statement, and extracting it dynamically would restate the implementation instead of
 * checking it.</p>
 *
 * <p><strong>Maintenance contract:</strong> when a new endpoint is added to
 * {@code SearchApiV2Manager.process}, add it here with an explicit decision and update
 * {@link #EXPECTED_ENTRY_COUNT}. Forgetting to do so is safe — the endpoint inherits the
 * default-deny policy — but this test makes the omission visible.</p>
 */
public class LoginRequirementCompleteCoverageTest {

    /**
     * Every dispatched sub-path paired with whether it requires an authenticated user
     * while {@code login.required} is enabled.
     */
    private static final Map<String, Boolean> ENDPOINT_DECISIONS;

    /** Must equal {@code ENDPOINT_DECISIONS.size()}. */
    private static final int EXPECTED_ENTRY_COUNT = 18;

    static {
        final Map<String, Boolean> m = new LinkedHashMap<>();

        // --- Reachable before login: the probe, the login flow, and the configuration the
        // single-page application reads in order to render the login form. ---
        m.put("/health", false);
        m.put("/auth/me", false);
        m.put("/auth/login", false);
        m.put("/auth/logout", false);
        m.put("/ui/config", false);

        // --- Search and everything derived from the index. ---
        m.put("/search", true);
        m.put("/documents/all", true);
        m.put("/suggest-words", true);
        m.put("/labels", true);
        m.put("/popular-words", true);
        m.put("/related-queries", true);
        m.put("/related-content", true);
        m.put("/cache/abc123", true);

        // --- Per-user state and generative endpoints. ---
        m.put("/auth/password", true);
        m.put("/click", true);
        m.put("/favorites", true);
        m.put("/documents/abc123/favorite", true);
        m.put("/chat", true);

        ENDPOINT_DECISIONS = Collections.unmodifiableMap(m);
    }

    @Test
    public void test_entryCounts_matchExpectedEntryCount() {
        assertTrue(ENDPOINT_DECISIONS.size() == EXPECTED_ENTRY_COUNT, "ENDPOINT_DECISIONS has " + ENDPOINT_DECISIONS.size()
                + " entries but EXPECTED_ENTRY_COUNT=" + EXPECTED_ENTRY_COUNT + "; update the constant when adding or removing endpoints");
    }

    @Test
    public void test_everyEndpointHasDeliberateLoginDecision() {
        final List<String> failures = new ArrayList<>();
        for (final Map.Entry<String, Boolean> entry : ENDPOINT_DECISIONS.entrySet()) {
            final boolean actual = new LoginRequirement().requiresLogin(entry.getKey());
            if (actual != entry.getValue().booleanValue()) {
                failures.add(String.format("  subPath=%-35s expected requiresLogin=%b, got=%b", entry.getKey(), entry.getValue(), actual));
            }
        }
        if (!failures.isEmpty()) {
            fail("LoginRequirement decision mismatch for the following endpoints:\n" + String.join("\n", failures)
                    + "\n\nFor each mismatch, either:\n"
                    + "  (a) update LoginRequirement.requiresLogin() to reflect the intended decision, or\n"
                    + "  (b) update this test's ENDPOINT_DECISIONS table if the intended decision changed.");
        }
    }

    @Test
    public void test_exemptSetIsExactlyTheLoginFlowAndTheProbe() {
        // A regression here means an endpoint became anonymously reachable. Any addition to
        // this set widens what an unauthenticated caller can read and must be deliberate.
        final List<String> exempt = new ArrayList<>();
        for (final Map.Entry<String, Boolean> entry : ENDPOINT_DECISIONS.entrySet()) {
            if (!entry.getValue().booleanValue()) {
                exempt.add(entry.getKey());
            }
        }
        Collections.sort(exempt);
        assertEquals(List.of("/auth/login", "/auth/logout", "/auth/me", "/health", "/ui/config"), exempt);
    }
}
