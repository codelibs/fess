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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.exception.SsoLoginException;
import org.codelibs.fess.exception.SsoStateException;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.spnego.SpnegoHttpFilter.Constants;
import org.codelibs.spnego.SpnegoProvider;
import org.ietf.jgss.GSSException;
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

    /**
     * {@code /sso} is anonymous, so whatever one rejected handshake writes to the log is what an
     * unbounded loop of them writes. Every failure the client's own token decides must therefore be
     * reported by message ({@code SsoStateException}) rather than by stack trace
     * ({@code SsoLoginException}).
     *
     * <p>{@code GSSException} is the one that used to be reported by trace, and it is the cheapest
     * of the three to provoke: a token of three Base64 characters decodes and then fails inside
     * {@code acceptSecContext}. A replayed authenticator lands here too.
     */
    @Test
    public void test_isHandshakeRefusal_coversEveryClientChosenFailure() {
        final SpnegoAuthenticator authenticator = new SpnegoAuthenticator();

        // The header the library refuses to try at all.
        assertTrue(authenticator.isHandshakeRefusal(new UnsupportedOperationException("NTLM not supported")));
        // The token the strict Base64 decoder rejects.
        assertTrue(authenticator.isHandshakeRefusal(new IllegalArgumentException("Illegal base64 character")));
        // The token that decodes but the acceptor will not take.
        assertTrue(authenticator.isHandshakeRefusal(new GSSException(GSSException.DEFECTIVE_TOKEN)));
        assertTrue(authenticator.isHandshakeRefusal(new GSSException(GSSException.FAILURE, -1, "Request is a replay (34)")));

        // A fault of this server keeps its stack trace: initialization failures are wrapped in a
        // plain SsoLoginException, and anything unforeseen from the library is not one of the three.
        assertFalse(authenticator.isHandshakeRefusal(new SsoLoginException("Failed to initialize SPNEGO.")));
        assertFalse(authenticator.isHandshakeRefusal(new NullPointerException()));
        assertFalse(authenticator.isHandshakeRefusal(new RuntimeException("unexpected")));
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
    public void test_getBasicRealm_realmFollowsTheLastAtSign() {
        // Kerberos takes the realm after the LAST '@': KerberosPrincipal("alice@a@PARTNER.EXAMPLE")
        // normalizes to name "alice@PARTNER.EXAMPLE" and realm "PARTNER.EXAMPLE", and the library
        // hands the typed name straight to the login module, so that is the realm an AS-REQ would
        // reach. Reading the first '@' instead names a realm that exists nowhere, which the allow
        // list can only ever refuse. Built literally so the header is visible at the call site.
        assertEquals("PARTNER.EXAMPLE", SpnegoAuthenticator.getBasicRealm("Basic " + token("alice@a@PARTNER.EXAMPLE:secret")));
        // A name ending in '@' names an empty realm, which KerberosPrincipal rejects outright, so
        // there is nothing for the allow list to decide.
        assertNull(SpnegoAuthenticator.getBasicRealm("Basic " + token("alice@a@:secret")));
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
    public void test_getLoginCredential_rejectedAuthorizationHeaderIsAStateException() {
        // "Negotiate or Basic Only" is what SpnegoProvider#getAuthScheme raises for a header whose
        // scheme is neither Negotiate nor Basic, and for a Basic header carrying no token. The
        // library also raises UnsupportedOperationException for Basic once basicSupported is false
        // and for an NTLM token it cannot downgrade. All three are decided by the client, and /sso
        // is anonymous, so a stack trace per attempt would let an unauthenticated client fill the
        // log -- SsoStateException, which SsoAction logs message-only.
        addMockRequestHeader(Constants.AUTHZ_HEADER, "Bearer " + token("alice@PARTNER.EXAMPLE"));
        final SpnegoAuthenticator authenticator = new SpnegoAuthenticator() {
            @Override
            protected org.codelibs.spnego.SpnegoAuthenticator getAuthenticator() {
                throw new UnsupportedOperationException("Negotiate or Basic Only");
            }
        };
        final SsoStateException e = assertThrows(SsoStateException.class, authenticator::getLoginCredential);
        assertTrue(e.getMessage().contains("Negotiate or Basic Only"));
        // The header is echoed masked, so the scheme survives and the credential does not.
        assertTrue(e.getMessage().contains("Bearer ***"));
        assertFalse(e.getMessage().contains(token("alice@PARTNER.EXAMPLE")));
    }

    @Test
    public void test_getLoginCredential_initializationFaultKeepsItsStackTrace() {
        // The boundary the case above must not cross, and the reason it tests the thrown type
        // rather than the cause chain. SpnegoFilterConfig raises UnsupportedOperationException for
        // an invalid login module too -- no storeKey, a login module class it does not support, a
        // control flag other than REQUIRED -- and those are server-side faults the operator needs
        // the trace for. They are harmless here only because getAuthenticator() has already wrapped
        // them in a plain SsoLoginException, so the thrown type is no longer the one being matched.
        // Matching on the cause instead would find the nested UnsupportedOperationException and
        // silently demote every initialization failure to a message-only log; this goes red first.
        addMockRequestHeader(Constants.AUTHZ_HEADER, "Negotiate YIIFoAYGKwYBBQUCoIIF");
        final SpnegoAuthenticator authenticator = new SpnegoAuthenticator() {
            @Override
            protected org.codelibs.spnego.SpnegoAuthenticator getAuthenticator() {
                throw new SsoLoginException("Failed to initialize SPNEGO.",
                        new UnsupportedOperationException("Login Module for server does not have the storeKey option."));
            }
        };
        final SsoLoginException e = assertThrows(SsoLoginException.class, authenticator::getLoginCredential);
        assertFalse(e instanceof SsoStateException);
    }

    /**
     * Drives the library's own header parser and token decoder, so the exception type the mapping
     * below relies on is pinned against the shipping spnego jar rather than assumed.
     *
     * <p>
     * {@code SpnegoProvider#getAuthScheme} is public, but the {@code SpnegoAuthScheme} it returns is
     * a package-private final class whose {@code getToken()} is package private too, so both are
     * reached by reflection. Library and Fess both load from the class path (the unnamed module),
     * so {@code setAccessible(true)} succeeds without any {@code --add-opens}.
     * </p>
     *
     * @param header the raw Authorization header value
     * @throws Throwable whatever the library raises, unwrapped from the reflective call
     */
    private void decodeLibraryToken(final String header) throws Throwable {
        final Method getAuthScheme = SpnegoProvider.class.getMethod("getAuthScheme", String.class);
        final Object scheme;
        try {
            scheme = getAuthScheme.invoke(null, header);
        } catch (final InvocationTargetException e) {
            throw e.getCause();
        }
        assertNotNull(scheme);
        final Method getToken = scheme.getClass().getDeclaredMethod("getToken");
        getToken.setAccessible(true);
        try {
            getToken.invoke(scheme);
        } catch (final InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @Test
    public void test_libraryRaisesIllegalArgumentExceptionForAMalformedToken() {
        // SpnegoProvider#parseAuthHeader does not validate the token -- it accepts any non-empty
        // trimmed remainder -- so the strict decoder behind SpnegoAuthScheme#getToken is what
        // rejects it. SpnegoProvider#negotiate evaluates that decode at the top of the method,
        // before any scheme dispatch and before the 401 is written, so every shape below reaches it
        // whatever the spnego.allow.* settings say. This is the contract the mapping in
        // getLoginCredential depends on; if a library upgrade changed the type, this goes red
        // first instead of the demotion silently ceasing to apply.
        assertThrows(IllegalArgumentException.class, () -> decodeLibraryToken("Negotiate ###"));
        assertThrows(IllegalArgumentException.class, () -> decodeLibraryToken("Basic ###"));
        // Valid base64 alphabet, but not a valid length.
        assertThrows(IllegalArgumentException.class, () -> decodeLibraryToken("Negotiate a"));
        // The scheme is matched case-insensitively and the separator is any run of whitespace, so a
        // lower-case scheme behind a tab decodes -- and fails -- exactly the same way.
        assertThrows(IllegalArgumentException.class, () -> decodeLibraryToken("negotiate" + ch(0x09) + "###"));
        // The control: a scheme the library refuses outright is the other family, and it is raised
        // by getAuthScheme before any token exists. Both families are demoted, but keeping the
        // distinction visible here documents why the mapping names two types rather than one.
        assertThrows(UnsupportedOperationException.class, () -> decodeLibraryToken("Digest abc"));
    }

    @Test
    public void test_getLoginCredential_malformedTokenIsAStateException() {
        // The client chose the token, /sso is anonymous, and the header is echoed masked, so this
        // is a rejected request rather than a fault: SsoStateException, which SsoAction logs
        // message-only. Before this mapping existed the same request produced a full stack trace
        // per attempt, which let an unauthenticated client fill the log.
        addMockRequestHeader(Constants.AUTHZ_HEADER, "Negotiate ###");
        final SpnegoAuthenticator authenticator = new SpnegoAuthenticator() {
            @Override
            protected org.codelibs.spnego.SpnegoAuthenticator getAuthenticator() {
                throw new IllegalArgumentException("Illegal base64 character 23");
            }
        };
        final SsoStateException e = assertThrows(SsoStateException.class, authenticator::getLoginCredential);
        assertTrue(e.getMessage().contains("Illegal base64 character 23"));
        assertTrue(e.getMessage().contains("Negotiate ***"));
    }

    @Test
    public void test_getLoginCredential_initializationFaultWithAnIllegalArgumentCauseKeepsItsStackTrace() {
        // The same boundary as the UnsupportedOperationException case above, for the type added
        // alongside it. SpnegoFilterConfig raises IllegalArgumentException for a missing
        // spnego.krb5.conf, a missing spnego.login.conf and an exclude-dirs pattern it rejects, and
        // those are server-side faults the operator needs the trace for. They are harmless here
        // only because getAuthenticator() has already wrapped them in a plain SsoLoginException --
        // a FessSystemException, so no longer the type being matched. Matching on the cause instead
        // would find the nested IllegalArgumentException and silently demote every one of them.
        addMockRequestHeader(Constants.AUTHZ_HEADER, "Negotiate YIIFoAYGKwYBBQUCoIIF");
        final SpnegoAuthenticator authenticator = new SpnegoAuthenticator() {
            @Override
            protected org.codelibs.spnego.SpnegoAuthenticator getAuthenticator() {
                throw new SsoLoginException("Failed to initialize SPNEGO.",
                        new IllegalArgumentException("Must specify a username and password or a keyTab."));
            }
        };
        final SsoLoginException e = assertThrows(SsoLoginException.class, authenticator::getLoginCredential);
        assertFalse(e instanceof SsoStateException);
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
