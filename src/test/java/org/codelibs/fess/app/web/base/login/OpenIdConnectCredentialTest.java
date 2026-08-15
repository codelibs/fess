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
package org.codelibs.fess.app.web.base.login;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Unit tests for {@link OpenIdConnectCredential}, covering the shapes an OpenID provider can give
 * the {@code groups} claim and how {@code oic.default.groups} applies to each of them.
 */
public class OpenIdConnectCredentialTest extends UnitFessTestCase {

    private static final String DEFAULT_GROUPS_KEY = "oic.default.groups";

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        // The system properties component outlives a single test class, so the key is set on the one
        // the code actually reads and removed again below rather than swapped for a fresh instance.
        ComponentUtil.getSystemProperties().setProperty(DEFAULT_GROUPS_KEY, "fallback");
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        ComponentUtil.getSystemProperties().remove(DEFAULT_GROUPS_KEY);
        super.tearDown(testInfo);
    }

    private static String[] groupsOf(final Object claim) {
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "user@example.com");
        if (claim != null) {
            attributes.put("groups", claim);
        }
        return new OpenIdConnectCredential(attributes).getUserGroups();
    }

    @Test
    public void test_getUserGroups_fromArrayClaim() {
        assertEquals(List.of("dev", "sales"), List.of(groupsOf(List.of("dev", "sales"))));
    }

    @Test
    public void test_getUserGroups_fromSingleValuedStringClaim() {
        // Some providers emit a single-valued claim as a bare string rather than a one-element array.
        // That group used to be dropped and oic.default.groups substituted for it.
        assertEquals(List.of("dev"), List.of(groupsOf("dev")));
    }

    @Test
    public void test_getUserGroups_fromSingleValuedStringClaim_isTrimmed() {
        assertEquals(List.of("dev"), List.of(groupsOf("  dev  ")));
    }

    @Test
    public void test_getUserGroups_fromBlankStringClaim() {
        // The claim was sent, so the default does not apply -- the same rule as an empty array.
        assertEquals(0, groupsOf("").length);
        assertEquals(0, groupsOf("   ").length);
    }

    @Test
    public void test_getUserGroups_fromEmptyArrayClaim() {
        assertEquals(0, groupsOf(List.of()).length);
    }

    @Test
    public void test_getUserGroups_withoutClaimUsesTheDefault() {
        assertEquals(List.of("fallback"), List.of(groupsOf(null)));
    }

    @Test
    public void test_getUserId_fromEmailClaim() {
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "user@example.com");
        assertEquals("user@example.com", new OpenIdConnectCredential(attributes).getUserId());
    }

    @Test
    public void test_getUserId_withoutEmailClaim() {
        assertNull(new OpenIdConnectCredential(new HashMap<>()).getUserId());
    }
}
