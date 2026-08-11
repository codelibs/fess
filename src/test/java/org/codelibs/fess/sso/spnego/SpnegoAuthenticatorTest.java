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

import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.exception.SsoLoginException;
import org.codelibs.fess.exception.SsoStateException;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.spnego.SpnegoHttpFilter.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import jakarta.servlet.http.HttpServletRequest;

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
        systemProperties.remove("spnego.login.client.module");
        systemProperties.remove("spnego.exclude.dirs");
        super.tearDown(testInfo);
    }

    /** Builds a request stub that answers only getHeader(), which is all the check reads. */
    private HttpServletRequest requestWithAuthz(final String value) {
        return (HttpServletRequest) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { HttpServletRequest.class },
                (proxy, method, args) -> "getHeader".equals(method.getName()) ? value : null);
    }

    /**
     * Encodes credentials only. Header separator tests must build their header literally, because
     * {@link #basic(String)} hardcodes a single space and would hide the very divergence they check.
     */
    private static String token(final String credentials) {
        return Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    private static String basic(final String credentials) {
        return "Basic " + token(credentials);
    }

    /** Builds a one-character string without a source escape, keeping raw break characters out of this file. */
    private static String ch(final int codePoint) {
        return String.valueOf((char) codePoint);
    }

    @Test
    public void test_getBasicRealm() {
        // Only a Basic header carrying a realm yields one.
        assertEquals("FOREIGN.EXAMPLE", SpnegoAuthenticator.getBasicRealm(basic("alice@FOREIGN.EXAMPLE:secret")));
        // The library strips a NetBIOS domain prefix before authenticating, so the realm is what
        // follows '@', not the prefix.
        assertEquals("FOREIGN.EXAMPLE", SpnegoAuthenticator.getBasicRealm(basic("CORP\\alice@FOREIGN.EXAMPLE:secret")));
        // A password containing '@' or ':' must not be mistaken for a realm.
        assertEquals("FOREIGN.EXAMPLE", SpnegoAuthenticator.getBasicRealm(basic("alice@FOREIGN.EXAMPLE:p@ss:word")));

        // No realm to check.
        assertNull(SpnegoAuthenticator.getBasicRealm(null));
        assertNull(SpnegoAuthenticator.getBasicRealm(basic("alice:secret")));
        assertNull(SpnegoAuthenticator.getBasicRealm(basic("CORP\\alice:secret")));
        assertNull(SpnegoAuthenticator.getBasicRealm(basic("alice@:secret")));
        // Other schemes carry the realm in the principal instead and are validated after the
        // handshake; they must not be decoded here.
        assertNull(SpnegoAuthenticator.getBasicRealm("Negotiate YIIFoAYGKwYBBQUCoIIF"));
        assertNull(SpnegoAuthenticator.getBasicRealm("Basic"));
        // A malformed token belongs to the library to reject, not to this check.
        assertNull(SpnegoAuthenticator.getBasicRealm("Basic !!!not-base64!!!"));
    }

    @Test
    public void test_getBasicRealm_separatorMatchesLibraryParsing() {
        // SpnegoProvider#parseAuthHeader matches the scheme case-insensitively and then skips a run
        // of any whitespace, possibly empty. Every header below is authenticated by the library, so
        // each one has to yield its realm here as well; a header this check cannot read is a header
        // the spnego.allowed.realms list cannot govern. These are built literally on purpose.
        final String credentials = token("alice@PARTNER.COM:secret");
        assertEquals("PARTNER.COM", SpnegoAuthenticator.getBasicRealm("Basic " + credentials));
        assertEquals("PARTNER.COM", SpnegoAuthenticator.getBasicRealm("Basic\t" + credentials));
        assertEquals("PARTNER.COM", SpnegoAuthenticator.getBasicRealm("Basic\t " + credentials));
        // No separator at all: the library takes everything after the scheme as the token.
        assertEquals("PARTNER.COM", SpnegoAuthenticator.getBasicRealm("Basic" + credentials));
        assertEquals("PARTNER.COM", SpnegoAuthenticator.getBasicRealm("basic\t" + credentials));
        assertEquals("PARTNER.COM", SpnegoAuthenticator.getBasicRealm("BASIC" + credentials));

        // A user name without a realm names nothing to check, whichever separator was used.
        assertNull(SpnegoAuthenticator.getBasicRealm("Basic\t" + token("alice:secret")));
        assertNull(SpnegoAuthenticator.getBasicRealm("Basic" + token("CORP\\alice:secret")));
    }

    @Test
    public void test_getBasicRealm_ignoresNonBasicAndTokenlessHeaders() {
        // A Negotiate token is not Basic credentials; decoding it here would invent a realm from
        // arbitrary bytes. The realm of that path comes from the principal after the handshake.
        assertNull(SpnegoAuthenticator.getBasicRealm("Negotiate " + token("alice@PARTNER.COM:secret")));
        // A scheme with no token authenticates nobody, so there is no realm to reject.
        assertNull(SpnegoAuthenticator.getBasicRealm("Basic"));
        assertNull(SpnegoAuthenticator.getBasicRealm("Basic   "));
        assertNull(SpnegoAuthenticator.getBasicRealm("Basic\t"));
        assertNull(SpnegoAuthenticator.getBasicRealm(""));
        assertNull(SpnegoAuthenticator.getBasicRealm("Basi"));
    }

    @Test
    public void test_sanitizeForLog() {
        assertEquals("CORP.EXAMPLE", SpnegoAuthenticator.sanitizeForLog("CORP.EXAMPLE"));
        // A newline would otherwise let an unauthenticated client forge a log line.
        assertEquals("EVIL??WARN forged", SpnegoAuthenticator.sanitizeForLog("EVIL\r\nWARN forged"));
        final String bounded = SpnegoAuthenticator.sanitizeForLog("R".repeat(200));
        assertEquals(SpnegoAuthenticator.MAX_LOGGED_REALM_LENGTH + 3, bounded.length());
        assertTrue(bounded.endsWith("..."));
    }

    @Test
    public void test_rejectDisallowedBasicRealm_rejectsForeignRealm() {
        final SpnegoAuthenticator authenticator = new SpnegoAuthenticator() {
            @Override
            protected boolean isAllowedRealm(final String realm) {
                return false;
            }
        };
        final SsoLoginException e = assertThrows(SsoLoginException.class,
                () -> authenticator.rejectDisallowedBasicRealm(requestWithAuthz(basic("alice@FOREIGN.EXAMPLE:secret"))));
        assertTrue(e.getMessage().contains("FOREIGN.EXAMPLE"));
        assertTrue(e.getMessage().contains("spnego.allowed.realms"));
        // The decoded token holds the password; it must never reach the message or the log.
        assertFalse(e.getMessage().contains("secret"));
    }

    @Test
    public void test_rejectDisallowedBasicRealm_rejectsForeignRealmBehindNonSpaceSeparator() {
        final SpnegoAuthenticator authenticator = new SpnegoAuthenticator() {
            @Override
            protected boolean isAllowedRealm(final String realm) {
                return false;
            }
        };
        // The library authenticates a tab-separated header, so the allow list has to reach it too.
        // SsoStateException, not a plain SsoLoginException: /sso is anonymous, and SsoAction logs a
        // full stack trace for anything else, which one crafted header per request would exploit.
        final SsoStateException e = assertThrows(SsoStateException.class,
                () -> authenticator.rejectDisallowedBasicRealm(requestWithAuthz("Basic\t" + token("alice@FOREIGN.EXAMPLE:secret"))));
        assertTrue(e.getMessage().contains("FOREIGN.EXAMPLE"));
        assertFalse(e.getMessage().contains("secret"));
    }

    @Test
    public void test_rejectDisallowedBasicRealm_acceptsAllowedRealm() {
        final SpnegoAuthenticator authenticator = new SpnegoAuthenticator() {
            @Override
            protected boolean isAllowedRealm(final String realm) {
                return "PARTNER.EXAMPLE".equals(realm);
            }
        };
        authenticator.rejectDisallowedBasicRealm(requestWithAuthz(basic("alice@PARTNER.EXAMPLE:secret")));
    }

    @Test
    public void test_rejectDisallowedBasicRealm_skipsCheckWhenNoRealmIsNamed() {
        // A plain user name and a non-Basic scheme must not reach the allow list, because
        // isAllowedRealm resolves the server realm through the library and would otherwise force
        // SPNEGO initialization on every Negotiate handshake. Any such attempt fails here, since
        // this authenticator has no usable SPNEGO configuration.
        final SpnegoAuthenticator authenticator = new SpnegoAuthenticator();
        authenticator.rejectDisallowedBasicRealm(requestWithAuthz(null));
        authenticator.rejectDisallowedBasicRealm(requestWithAuthz("Negotiate YIIFoAYGKwYBBQUCoIIF"));
        authenticator.rejectDisallowedBasicRealm(requestWithAuthz(basic("alice:secret")));
        authenticator.rejectDisallowedBasicRealm(requestWithAuthz(basic("CORP\\alice:secret")));
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
    public void test_isSupportedLoggerLevel() {
        // The library switches on 1-7 and maps everything else, including 0, to INFO.
        assertTrue(SpnegoAuthenticator.SpnegoConfig.isSupportedLoggerLevel("0"));
        assertTrue(SpnegoAuthenticator.SpnegoConfig.isSupportedLoggerLevel("7"));
        // Outside the documented range the value carries no meaning.
        assertFalse(SpnegoAuthenticator.SpnegoConfig.isSupportedLoggerLevel("8"));
        assertFalse(SpnegoAuthenticator.SpnegoConfig.isSupportedLoggerLevel("-1"));
        // All-digit strings are not automatically parseable: the library uses Integer.parseInt.
        assertFalse(SpnegoAuthenticator.SpnegoConfig.isSupportedLoggerLevel("99999999999"));
        assertFalse(SpnegoAuthenticator.SpnegoConfig.isSupportedLoggerLevel("abc"));
    }

    @Test
    public void test_getInitParameter_loggerLevel_outOfRangeFallsBack() {
        SpnegoAuthenticator.SpnegoConfig config = new SpnegoAuthenticator.SpnegoConfig();
        DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            // An all-digit value that overflows an int used to be passed through and made the
            // library fail with NumberFormatException while building its configuration.
            systemProperties.setProperty("spnego.logger.level", "99999999999");
            String level = config.getInitParameter(Constants.LOGGER_LEVEL);
            assertFalse("99999999999".equals(level));
            assertTrue(SpnegoAuthenticator.SpnegoConfig.isSupportedLoggerLevel(level));

            // A value above the documented range is ignored as well.
            systemProperties.setProperty("spnego.logger.level", "8");
            assertFalse("8".equals(config.getInitParameter(Constants.LOGGER_LEVEL)));

            // The quietest documented level is still passed through unchanged.
            systemProperties.setProperty("spnego.logger.level", "7");
            assertEquals("7", config.getInitParameter(Constants.LOGGER_LEVEL));
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
    public void test_getProperty_blankFallsBackToDefault() {
        SpnegoAuthenticator.SpnegoConfig config = new SpnegoAuthenticator.SpnegoConfig();
        DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            // The admin screen stores an empty string when an input is cleared, so a present but
            // blank key must behave like an absent one instead of reaching the library as "".
            systemProperties.setProperty("spnego.login.client.module", "");
            assertEquals("spnego-client", config.getInitParameter(Constants.CLIENT_MODULE));

            systemProperties.setProperty("spnego.login.client.module", "   ");
            assertEquals("spnego-client", config.getInitParameter(Constants.CLIENT_MODULE));

            // A real value is still passed through.
            systemProperties.setProperty("spnego.login.client.module", "custom-client");
            assertEquals("custom-client", config.getInitParameter(Constants.CLIENT_MODULE));
        } finally {
            systemProperties.remove("spnego.login.client.module");
        }
    }

    @Test
    public void test_getInitParameter_excludeDirsIsNotMapped() {
        SpnegoAuthenticator.SpnegoConfig config = new SpnegoAuthenticator.SpnegoConfig();
        DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            // spnego.exclude.dirs is only honored by SpnegoHttpFilter, which Fess does not install,
            // so the value must not be forwarded as if the exclusion were effective.
            systemProperties.setProperty("spnego.exclude.dirs", "/api/");
            assertNull(config.getInitParameter(Constants.EXCLUDE_DIRS));
        } finally {
            systemProperties.remove("spnego.exclude.dirs");
        }
    }

    @Test
    public void test_maskAuthzHeader() {
        // Absent header.
        assertEquals("null", SpnegoAuthenticator.maskAuthzHeader(null));
        // Negotiate token must be reduced to its scheme.
        assertEquals("Negotiate ***", SpnegoAuthenticator.maskAuthzHeader("Negotiate YIIFxQYGKwYBBQUCoIIFuTCCBbW"));
        // Basic credentials must not leak: even four base64 characters decode to three plain bytes
        // of "user:password".
        assertEquals("Basic ***", SpnegoAuthenticator.maskAuthzHeader("Basic dXNlcjpwYXNzd29yZA=="));
        // A header without a scheme separator is fully masked.
        assertEquals("***", SpnegoAuthenticator.maskAuthzHeader("dXNlcjpwYXNzd29yZA=="));
        assertEquals("***", SpnegoAuthenticator.maskAuthzHeader(""));
        assertEquals("***", SpnegoAuthenticator.maskAuthzHeader(" leading-space"));
    }

    @Test
    public void test_sanitizeForLog_unicodeLineBreaks() {
        // \p{Cntrl} without the UNICODE flag covers ASCII only, so these three would otherwise reach
        // the log intact and let an unauthenticated client forge a line in it.
        assertEquals("EVIL?WARN forged", SpnegoAuthenticator.sanitizeForLog("EVIL" + ch(0x0085) + "WARN forged"));
        assertEquals("EVIL?WARN forged", SpnegoAuthenticator.sanitizeForLog("EVIL" + ch(0x2028) + "WARN forged"));
        assertEquals("EVIL?WARN forged", SpnegoAuthenticator.sanitizeForLog("EVIL" + ch(0x2029) + "WARN forged"));
    }

    @Test
    public void test_maskAuthzHeader_boundsAndSanitizesTheScheme() {
        // The surviving scheme is client-controlled and ends up in an exception message that is
        // logged, so it is bounded like the realm instead of echoing up to the container's header
        // limit.
        final String masked = SpnegoAuthenticator.maskAuthzHeader("S".repeat(8192) + " dXNlcjpwYXNzd29yZA==");
        assertEquals(SpnegoAuthenticator.MAX_LOGGED_REALM_LENGTH + 3 + 4, masked.length());
        assertTrue(masked.endsWith("... ***"));

        // Neither NUL nor NEL is whitespace, so both survive the scheme scan and have to be
        // stripped before the value is logged.
        assertEquals("Ba?ic ***", SpnegoAuthenticator.maskAuthzHeader("Ba" + ch(0x0000) + "ic dXNlcjpwYXNzd29yZA=="));
        assertEquals("Ba?ic ***", SpnegoAuthenticator.maskAuthzHeader("Ba" + ch(0x0085) + "ic dXNlcjpwYXNzd29yZA=="));

        // A tab-separated header names its scheme rather than degrading to a bare mask.
        assertEquals("Basic ***", SpnegoAuthenticator.maskAuthzHeader("Basic\tdXNlcjpwYXNzd29yZA=="));
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
    public void test_unsupportedOperations() {
        SpnegoAuthenticator.SpnegoConfig config = new SpnegoAuthenticator.SpnegoConfig();
        // These two FilterConfig methods are never called by the library; make sure they fail loudly
        // and name the class that actually threw.
        UnsupportedOperationException e = assertThrows(UnsupportedOperationException.class, () -> config.getServletContext());
        assertTrue(e.getMessage().contains("SpnegoConfig"));
        e = assertThrows(UnsupportedOperationException.class, () -> config.getInitParameterNames());
        assertTrue(e.getMessage().contains("SpnegoConfig"));
    }
}
