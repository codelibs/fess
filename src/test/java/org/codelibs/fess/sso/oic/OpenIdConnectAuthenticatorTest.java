/*
 * Copyright 2012-2024 CodeLibs Project and the Others.
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
package org.codelibs.fess.sso.oic;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;

public class OpenIdConnectAuthenticatorTest extends UnitFessTestCase {
    public void test_parseJwtClaim() throws IOException {
        // Setup
        OpenIdConnectAuthenticator authenticator = new OpenIdConnectAuthenticator();
        final Map<String, Object> attributes = new HashMap<>();
        String jwtClaim = "{\"sub\":\"1234567890\",\"name\":\"John Doe\",\"groups\":[\"group1\",\"group2\"]}";

        // Execute
        authenticator.parseJwtClaim(jwtClaim, attributes);

        // Verify
        assertEquals("1234567890", attributes.get("sub"));
        assertEquals("John Doe", attributes.get("name"));

        // Check groups array
        assertTrue(attributes.get("groups") instanceof String[]);
        String[] groupArray = (String[]) attributes.get("groups");
        assertArrayEquals(new String[] { "group1", "group2" }, groupArray);

    }
}
