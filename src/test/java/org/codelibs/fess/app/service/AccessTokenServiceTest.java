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
package org.codelibs.fess.app.service;

import java.util.Set;

import org.codelibs.fess.opensearch.config.exentity.AccessToken;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The two permission sets a request carries: the one the token was issued with, and the one the
 * caller added through the token's request parameter.
 */
public class AccessTokenServiceTest extends UnitFessTestCase {

    private AccessToken createToken(final String[] permissions, final String parameterName) {
        final AccessToken accessToken = new AccessToken();
        accessToken.setPermissions(permissions);
        accessToken.setParameterName(parameterName);
        return accessToken;
    }

    /**
     * The request parameter exists so that an application embedding Fess can pass the end user's
     * permissions per request instead of issuing a token each, so the search-filtering set has to
     * carry them.
     */
    @Test
    public void test_collectPermissions_searchSetCarriesTheRequestParameter() {
        final AccessTokenService service = new AccessTokenService();
        final HttpServletRequest request = getMockRequest();
        getMockRequest().setParameter("fessPermissions", "2sales");

        final Set<String> permissions =
                service.collectPermissions(createToken(new String[] { "1guest" }, "fessPermissions"), request, true);

        assertTrue(permissions.contains("1guest"));
        assertTrue(permissions.contains("2sales"));
    }

    /**
     * The set that decides whether the caller may reach the administration API must not, or naming
     * api.admin.access.permissions in that same parameter raises any token to an administrative one.
     */
    @Test
    public void test_collectPermissions_tokenSetIgnoresTheRequestParameter() {
        final AccessTokenService service = new AccessTokenService();
        final HttpServletRequest request = getMockRequest();
        getMockRequest().setParameter("fessPermissions", "Radmin-api");

        final Set<String> permissions =
                service.collectPermissions(createToken(new String[] { "1guest" }, "fessPermissions"), request, false);

        assertTrue(permissions.contains("1guest"));
        assertFalse(permissions.contains("Radmin-api"));
        assertEquals(1, permissions.size());
    }

    /**
     * A token issued WITH the administrative permission still carries it either way: the fix
     * narrows where the permission may come from, not which permissions exist.
     */
    @Test
    public void test_collectPermissions_tokenSetKeepsItsOwnPermissions() {
        final AccessTokenService service = new AccessTokenService();
        final HttpServletRequest request = getMockRequest();

        final Set<String> permissions = service.collectPermissions(createToken(new String[] { "Radmin-api" }, null), request, false);

        assertTrue(permissions.contains("Radmin-api"));
    }

    /**
     * A token that names no parameter is unaffected by whatever the caller sends.
     */
    @Test
    public void test_collectPermissions_noParameterName() {
        final AccessTokenService service = new AccessTokenService();
        final HttpServletRequest request = getMockRequest();
        getMockRequest().setParameter("fessPermissions", "Radmin-api");

        final Set<String> permissions = service.collectPermissions(createToken(new String[] { "1guest" }, null), request, true);

        assertEquals(1, permissions.size());
        assertTrue(permissions.contains("1guest"));
    }
}
