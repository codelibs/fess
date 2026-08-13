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

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@code login.required} decision table.
 */
public class LoginRequirementTest extends UnitFessTestCase {

    private final LoginRequirement requirement = new LoginRequirement();

    @Test
    public void test_searchIsGated() {
        assertTrue(requirement.requiresLogin("/search"));
    }

    @Test
    public void test_scrollSearchIsGated() {
        assertTrue(requirement.requiresLogin("/documents/all"));
    }

    @Test
    public void test_indexDerivedEndpointsAreGated() {
        assertTrue(requirement.requiresLogin("/labels"));
        assertTrue(requirement.requiresLogin("/popular-words"));
        assertTrue(requirement.requiresLogin("/suggest-words"));
        assertTrue(requirement.requiresLogin("/related-queries"));
        assertTrue(requirement.requiresLogin("/related-content"));
    }

    @Test
    public void test_healthIsExempt() {
        assertFalse(requirement.requiresLogin("/health"));
    }

    @Test
    public void test_endpointsTheLoginPageNeedsAreExempt() {
        assertFalse(requirement.requiresLogin("/auth/me"));
        assertFalse(requirement.requiresLogin("/auth/login"));
        assertFalse(requirement.requiresLogin("/auth/logout"));
        assertFalse(requirement.requiresLogin("/ui/config"));
    }

    @Test
    public void test_passwordChangeIsGated() {
        // Changing a password is an authenticated operation; it is deliberately NOT part of
        // the anonymous /auth/* subset.
        assertTrue(requirement.requiresLogin("/auth/password"));
    }

    @Test
    public void test_unknownPathFallsBackToGated() {
        // A path added to the dispatcher without an entry here must inherit the secure default
        // rather than becoming anonymously reachable.
        assertTrue(requirement.requiresLogin("/some-endpoint-added-later"));
    }

    @Test
    public void test_nullPathIsGated() {
        assertTrue(requirement.requiresLogin(null));
    }

    @Test
    public void test_exemptionsAreExactMatchesNotPrefixes() {
        // "/health" is exempt but "/healthz" or "/auth/mel" must not inherit the exemption.
        assertTrue(requirement.requiresLogin("/healthz"));
        assertTrue(requirement.requiresLogin("/auth/mel"));
        assertTrue(requirement.requiresLogin("/ui/config/extra"));
    }
}
