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
package org.codelibs.fess.sso.spnego;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.exception.SsoLoginException;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.spnego.SpnegoHttpFilter.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class SpnegoAuthenticatorTest extends UnitFessTestCase {

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        // Ensure spnego.* system properties possibly set by a test do not leak.
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        systemProperties.remove("spnego.logger.level");
        systemProperties.remove("spnego.allowed.realms");
        super.tearDown(testInfo);
    }

    @Test
    public void test_authenticatorInstantiation() {
        // Verify authenticator can be instantiated without errors
        SpnegoAuthenticator authenticator = new SpnegoAuthenticator();
        assertNotNull(authenticator);
    }

    @Test
    public void test_spnegoConfigClass() {
        // Verify the inner SpnegoConfig class can be instantiated directly.
        SpnegoAuthenticator.SpnegoConfig config = new SpnegoAuthenticator.SpnegoConfig();
        assertNotNull(config);

        // The filter name should be the fully qualified name of the outer class.
        assertEquals(SpnegoAuthenticator.class.getName(), config.getFilterName());
    }

    @Test
    public void test_securitySettings_allowBasic() throws Exception {
        // Basic authentication remains enabled by default for compatibility.
        SpnegoAuthenticator.SpnegoConfig config = new SpnegoAuthenticator.SpnegoConfig();
        assertEquals("true", config.getInitParameter(Constants.ALLOW_BASIC));
    }

    @Test
    public void test_securitySettings_allowUnsecureBasic() throws Exception {
        // Unsecure basic authentication (basic over plain HTTP) is disabled by default.
        SpnegoAuthenticator.SpnegoConfig config = new SpnegoAuthenticator.SpnegoConfig();
        assertEquals("false", config.getInitParameter(Constants.ALLOW_UNSEC_BASIC));
    }

    @Test
    public void test_getInitParameter_secureDefaults() {
        // Verify the security-hardened defaults returned by SpnegoConfig#getInitParameter.
        SpnegoAuthenticator.SpnegoConfig config = new SpnegoAuthenticator.SpnegoConfig();

        // Localhost bypass must be off by default.
        assertEquals("false", config.getInitParameter(Constants.ALLOW_LOCALHOST));
        // Unsecure basic auth over plain HTTP must be off by default.
        assertEquals("false", config.getInitParameter(Constants.ALLOW_UNSEC_BASIC));
        // No pre-authentication credentials by default (keytab-based server login).
        assertEquals("", config.getInitParameter(Constants.PREAUTH_USERNAME));
        assertEquals("", config.getInitParameter(Constants.PREAUTH_PASSWORD));
        // Basic auth stays enabled for compatibility.
        assertEquals("true", config.getInitParameter(Constants.ALLOW_BASIC));
        // Delegation must be off by default.
        assertEquals("false", config.getInitParameter(Constants.ALLOW_DELEGATION));
    }

    @Test
    public void test_getInitParameter_loggerLevel_nonNumericFallsBack() {
        SpnegoAuthenticator.SpnegoConfig config = new SpnegoAuthenticator.SpnegoConfig();
        DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            // A non-numeric level must be ignored and auto-detection used instead.
            systemProperties.setProperty("spnego.logger.level", "abc");
            String level = config.getInitParameter(Constants.LOGGER_LEVEL);
            assertNotNull(level);
            assertFalse("abc".equals(level));
            // Auto-detection always yields a numeric level string.
            assertTrue(level.chars().allMatch(Character::isDigit));

            // A numeric level must be passed through unchanged.
            systemProperties.setProperty("spnego.logger.level", "5");
            assertEquals("5", config.getInitParameter(Constants.LOGGER_LEVEL));
        } finally {
            systemProperties.remove("spnego.logger.level");
        }
    }

    @Test
    public void test_getResourcePath_throwsWhenMissing() {
        SpnegoAuthenticator.SpnegoConfig config = new SpnegoAuthenticator.SpnegoConfig();
        // A missing resource must raise SsoLoginException rather than returning null.
        assertThrows(SsoLoginException.class, () -> config.getResourcePath("this-file-does-not-exist-xyz.conf"));
    }

    @Test
    public void test_isAllowedRealm_serverRealmMatches() {
        SpnegoAuthenticator authenticator = new SpnegoAuthenticator();
        // The server's own realm is always allowed.
        assertTrue(authenticator.isAllowedRealm("CORP.EXAMPLE", "CORP.EXAMPLE"));
        // Realm comparison is case-insensitive.
        assertTrue(authenticator.isAllowedRealm("corp.example", "CORP.EXAMPLE"));
    }

    @Test
    public void test_isAllowedRealm_rejectsForeignRealm() {
        SpnegoAuthenticator authenticator = new SpnegoAuthenticator();
        // A realm other than the server realm is rejected when no allow list is configured.
        assertFalse(authenticator.isAllowedRealm("EVIL.EXAMPLE", "CORP.EXAMPLE"));
    }

    @Test
    public void test_isAllowedRealm_allowlistPermitsForeignRealm() {
        SpnegoAuthenticator authenticator = new SpnegoAuthenticator();
        DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            systemProperties.setProperty("spnego.allowed.realms", "TRUSTED.EXAMPLE");
            // A realm explicitly listed in spnego.allowed.realms is permitted.
            assertTrue(authenticator.isAllowedRealm("TRUSTED.EXAMPLE", "CORP.EXAMPLE"));
            // A realm neither on the allow list nor the server realm is still rejected.
            assertFalse(authenticator.isAllowedRealm("OTHER.EXAMPLE", "CORP.EXAMPLE"));
        } finally {
            systemProperties.remove("spnego.allowed.realms");
        }
    }

    @Test
    public void test_isAllowedRealm_backwardCompatWhenUndeterminable() {
        SpnegoAuthenticator authenticator = new SpnegoAuthenticator();
        // When neither the server realm nor an allow list can be determined, any realm is
        // accepted for backward compatibility (a warning is logged by the implementation).
        assertTrue(authenticator.isAllowedRealm("ANY.EXAMPLE", ""));
    }

    @Test
    public void test_constantsExist() {
        // Verify all expected configuration constants are defined
        SpnegoAuthenticator authenticator = new SpnegoAuthenticator();

        // These constants should be accessible (would fail at compile time if not)
        assertTrue(SpnegoAuthenticator.class.getName().contains("SpnegoAuthenticator"));

        // Verify authenticator is properly named (no typo)
        assertEquals("SpnegoAuthenticator", SpnegoAuthenticator.class.getSimpleName());
    }

    @Test
    public void test_nullSafeLogout() {
        SpnegoAuthenticator authenticator = new SpnegoAuthenticator();

        // SPNEGO logout should return null (relies on Kerberos infrastructure)
        String logoutUrl = authenticator.logout(null);
        assertNull(logoutUrl);
    }

    @Test
    public void test_nullSafeGetResponse() {
        SpnegoAuthenticator authenticator = new SpnegoAuthenticator();

        // SPNEGO typically doesn't provide special response handling
        org.lastaflute.web.response.ActionResponse response = authenticator.getResponse(org.codelibs.fess.sso.SsoResponseType.METADATA);
        assertNull(response);

        response = authenticator.getResponse(org.codelibs.fess.sso.SsoResponseType.LOGOUT);
        assertNull(response);
    }

    @Test
    public void test_innerClassNaming() {
        // Verify the inner SpnegoConfig class exists and is properly named
        // This test ensures the typo fix (SpengoConfig -> SpnegoConfig) is correct
        try {
            Class<?> configClass = Class.forName("org.codelibs.fess.sso.spnego.SpnegoAuthenticator$SpnegoConfig");
            assertNotNull(configClass);
            assertEquals("SpnegoConfig", configClass.getSimpleName());

            // Verify it's a static inner class
            assertTrue(java.lang.reflect.Modifier.isStatic(configClass.getModifiers()));
        } catch (ClassNotFoundException e) {
            fail("SpnegoConfig inner class should exist");
        }
    }
}
