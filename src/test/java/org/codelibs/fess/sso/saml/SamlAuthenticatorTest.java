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
package org.codelibs.fess.sso.saml;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.app.web.base.login.ActionResponseCredential;
import org.codelibs.fess.app.web.base.login.SamlCredential.SamlUser;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.exception.SsoMessageException;
import org.codelibs.fess.exception.SsoStateException;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.sso.SsoResponseType;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.saml2.core.exception.SAMLException;
import org.codelibs.saml2.core.exception.ValidationException;
import org.codelibs.saml2.core.exception.XMLParsingException;
import org.codelibs.saml2.core.settings.Saml2Settings;
import org.dbflute.optional.OptionalThing;
import org.dbflute.utflute.mocklet.MockletHttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.StreamResponse;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class SamlAuthenticatorTest extends UnitFessTestCase {

    private static final String BASE_URL_KEY = "saml.sp.base.url";

    /**
     * Builds an authenticator whose defaultSettings come from the production code.
     * init() is not usable here because it registers the instance with the SsoManager.
     */
    private SamlAuthenticator createAuthenticator() throws Exception {
        final SamlAuthenticator authenticator = new SamlAuthenticator();
        final Field field = SamlAuthenticator.class.getDeclaredField("defaultSettings");
        field.setAccessible(true);
        field.set(authenticator, authenticator.createDefaultSettings());
        return authenticator;
    }

    @Test
    public void test_createDefaultSettings_security() throws Exception {
        final Map<String, Object> settings = new SamlAuthenticator().createDefaultSettings();

        assertEquals("true", settings.get("onelogin.saml2.strict"));
        assertEquals("false", settings.get("onelogin.saml2.debug"));
        assertEquals("true", settings.get("onelogin.saml2.security.want_xml_validation"));
        assertEquals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", settings.get("onelogin.saml2.security.signature_algorithm"));
        // the key must not carry a duplicated prefix, otherwise it is silently ignored
        assertEquals("exact", settings.get("onelogin.saml2.security.requested_authncontextcomparison"));
    }

    @Test
    public void test_createDefaultSettings_hasNoBlankValues() throws Exception {
        // SettingsBuilder treats blank values as absent, so a blank default is dead weight
        new SamlAuthenticator().createDefaultSettings().forEach((key, value) -> {
            assertTrue(key + " must not have a blank default", StringUtil.isNotBlank((String) value));
        });
    }

    @Test
    public void test_createDefaultSettings_omitsSpUrls() throws Exception {
        // SP URLs depend on saml.sp.base.url and are therefore built per request
        final Map<String, Object> settings = new SamlAuthenticator().createDefaultSettings();

        assertFalse(settings.containsKey("onelogin.saml2.sp.entityid"));
        assertFalse(settings.containsKey("onelogin.saml2.sp.assertion_consumer_service.url"));
        assertFalse(settings.containsKey("onelogin.saml2.sp.single_logout_service.url"));
    }

    @Test
    public void test_getSettings_spUrlsUseDefaultBaseUrl() throws Exception {
        final Saml2Settings settings = createAuthenticator().getSettings();

        assertEquals("http://localhost:8080/sso/metadata", settings.getSpEntityId());
        assertEquals("http://localhost:8080/sso/", settings.getSpAssertionConsumerServiceUrl().toString());
        assertEquals("http://localhost:8080/sso/logout", settings.getSpSingleLogoutServiceUrl().toString());
    }

    @Test
    public void test_getSettings_spUrlsFollowBaseUrlChangedAfterStartup() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            // the property is changed after the authenticator was built, as the admin UI does
            systemProperties.setProperty(BASE_URL_KEY, "https://fess.example.com");

            final Saml2Settings settings = authenticator.getSettings();

            assertEquals("https://fess.example.com/sso/metadata", settings.getSpEntityId());
            assertEquals("https://fess.example.com/sso/", settings.getSpAssertionConsumerServiceUrl().toString());
            assertEquals("https://fess.example.com/sso/logout", settings.getSpSingleLogoutServiceUrl().toString());
        } finally {
            systemProperties.remove(BASE_URL_KEY);
        }
    }

    @Test
    public void test_getSettings_blankPropertyKeepsDefault() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            systemProperties.setProperty("saml.sp.entityid", StringUtil.EMPTY);
            systemProperties.setProperty("saml.sp.assertion_consumer_service.url", "  ");

            final Saml2Settings settings = authenticator.getSettings();

            assertEquals("http://localhost:8080/sso/metadata", settings.getSpEntityId());
            assertEquals("http://localhost:8080/sso/", settings.getSpAssertionConsumerServiceUrl().toString());
            assertTrue(settings.checkSPSettings().isEmpty());
        } finally {
            systemProperties.remove("saml.sp.entityid");
            systemProperties.remove("saml.sp.assertion_consumer_service.url");
        }
    }

    @Test
    public void test_getSettings_blankPropertyFallsBackToLibraryDefault() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            // clearing the key is the documented way of not constraining the authentication
            // method; falling back to the Fess default instead would keep sending
            // RequestedAuthnContext=Password and break an IdP that enforces MFA
            systemProperties.setProperty("saml.security.requested_authncontext", StringUtil.EMPTY);
            systemProperties.setProperty("saml.sp.nameidformat", "  ");

            final Saml2Settings settings = authenticator.getSettings();

            assertTrue(String.valueOf(settings.getRequestedAuthnContext()), settings.getRequestedAuthnContext().isEmpty());
            assertEquals("urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified", settings.getSpNameIDFormat());
        } finally {
            systemProperties.remove("saml.security.requested_authncontext");
            systemProperties.remove("saml.sp.nameidformat");
        }
    }

    @Test
    public void test_getSettings_cachedUntilPropertiesChange() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
        try {
            final Saml2Settings first = authenticator.getSettings();
            final Saml2Settings second = authenticator.getSettings();

            // rebuilding re-parses the IdP certificate and makes the library re-emit one warn
            // line per security warning, so unchanged properties must reuse the instance
            assertSame(first, second);
            assertEquals(1, appender.warnings().size());

            systemProperties.setProperty("saml.security.want_assertions_signed", "true");
            final Saml2Settings rebuilt = authenticator.getSettings();

            Assertions.assertNotSame(first, rebuilt);
            assertTrue(rebuilt.getWantAssertionsSigned());
        } finally {
            systemProperties.remove("saml.security.want_assertions_signed");
            appender.detach();
        }
    }

    @Test
    public void test_getSettings_cacheFollowsBaseUrlChangedAfterStartup() throws Exception {
        // the computed SP URLs are not system properties, so the cache key has to include them
        final SamlAuthenticator authenticator = createAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            assertEquals("http://localhost:8080/sso/metadata", authenticator.getSettings().getSpEntityId());

            systemProperties.setProperty(BASE_URL_KEY, "https://fess.example.com");

            assertEquals("https://fess.example.com/sso/metadata", authenticator.getSettings().getSpEntityId());
        } finally {
            systemProperties.remove(BASE_URL_KEY);
        }
    }

    /**
     * Generates a throwaway SP private key in the shape
     * {@code onelogin.saml2.sp.privatekey} expects: base64 PKCS#8, no PEM header.
     *
     * <p>The key is generated rather than checked in so that no private key material
     * lives in the repository.</p>
     *
     * @return the encoded private key
     * @throws Exception if the key cannot be generated
     */
    private static String generateSpPrivateKey() throws Exception {
        final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return java.util.Base64.getEncoder().encodeToString(generator.generateKeyPair().getPrivate().getEncoded());
    }

    @Test
    public void test_logSecurityWarnings_reportsUnrestrictedKeyTransport() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
        try {
            // no SP private key: nothing can be decrypted, so the allow-list is moot
            authenticator.getSettings();
            assertEquals(1, appender.warnings().size());
            assertFalse(appender.warnings().get(0), appender.warnings().get(0).contains("key_transport_algorithms_not_restricted"));

            systemProperties.setProperty("saml.sp.privatekey", generateSpPrivateKey());
            authenticator.getSettings();

            // a key is configured and every key transport algorithm is accepted
            assertEquals(2, appender.warnings().size());
            assertTrue(appender.warnings().get(1), appender.warnings().get(1).contains("key_transport_algorithms_not_restricted"));

            systemProperties.setProperty("saml.security.allowed_key_transport_algorithms", "http://www.w3.org/2009/xmlenc11#rsa-oaep");
            authenticator.getSettings();

            assertEquals(3, appender.warnings().size());
            assertFalse(appender.warnings().get(2), appender.warnings().get(2).contains("key_transport_algorithms_not_restricted"));

            // a blank value is not a restriction: the library treats an empty set as "accept
            // everything", so the warning has to come back
            systemProperties.setProperty("saml.security.allowed_key_transport_algorithms", "");
            authenticator.getSettings();

            assertEquals(4, appender.warnings().size());
            assertTrue(appender.warnings().get(3), appender.warnings().get(3).contains("key_transport_algorithms_not_restricted"));
        } finally {
            systemProperties.remove("saml.sp.privatekey");
            systemProperties.remove("saml.security.allowed_key_transport_algorithms");
            appender.detach();
        }
    }

    @Test
    public void test_logSecurityWarnings_reportsUnsignedLogoutRequests() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
        try {
            // without a single logout service there is nothing to send a LogoutRequest to
            authenticator.getSettings();
            assertEquals(1, appender.warnings().size());
            assertFalse(appender.warnings().get(0), appender.warnings().get(0).contains("unsigned_logoutrequest_accepted"));

            systemProperties.setProperty("saml.idp.single_logout_service.url", "https://idp.example.com/slo");
            authenticator.getSettings();

            // an unsigned LogoutRequest whose NameID is never checked ends any session
            assertEquals(2, appender.warnings().size());
            assertTrue(appender.warnings().get(1), appender.warnings().get(1).contains("unsigned_logoutrequest_accepted"));

            systemProperties.setProperty("saml.security.want_messages_signed", "true");
            authenticator.getSettings();

            assertEquals(3, appender.warnings().size());
            assertFalse(appender.warnings().get(2), appender.warnings().get(2).contains("unsigned_logoutrequest_accepted"));
        } finally {
            systemProperties.remove("saml.idp.single_logout_service.url");
            systemProperties.remove("saml.security.want_messages_signed");
            appender.detach();
        }
    }

    @Test
    public void test_getSettings_propertyOverridesDefault() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            systemProperties.setProperty("saml.sp.entityid", "https://sp.example.com/metadata");
            systemProperties.setProperty("saml.security.want_assertions_signed", "true");

            final Saml2Settings settings = authenticator.getSettings();

            assertEquals("https://sp.example.com/metadata", settings.getSpEntityId());
            assertTrue(settings.getWantAssertionsSigned());
        } finally {
            systemProperties.remove("saml.sp.entityid");
            systemProperties.remove("saml.security.want_assertions_signed");
        }
    }

    @Test
    public void test_getSettings_logsSecurityWarningsUntilTheyChange() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
        try {
            authenticator.getSettings();
            authenticator.getSettings();

            // the permissive defaults are reported, but only once
            assertEquals(1, appender.warnings().size());
            assertTrue(appender.warnings().get(0), appender.warnings().get(0).contains("assertions_and_messages_not_required_signed"));

            systemProperties.setProperty("saml.security.want_assertions_signed", "true");
            authenticator.getSettings();

            // the remaining warnings differ, so they are reported again
            assertEquals(2, appender.warnings().size());
            assertFalse(appender.warnings().get(1).contains("assertions_and_messages_not_required_signed"));
        } finally {
            systemProperties.remove("saml.security.want_assertions_signed");
            appender.detach();
        }
    }

    @Test
    public void test_getSettings_sharesReplayCacheAcrossRequests() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            // the properties have to change between the two calls, otherwise the settings cache
            // hands back the same instance and comparing its replay cache with itself asserts
            // nothing. What matters is that the cache survives a *rebuild*: it is the only thing
            // that would otherwise be discarded, letting an assertion already seen be replayed
            // whenever an administrator saves a SAML setting.
            final Saml2Settings first = authenticator.getSettings();
            systemProperties.setProperty("saml.security.want_assertions_signed", "true");
            final Saml2Settings second = authenticator.getSettings();

            Assertions.assertNotSame(first, second);
            assertNotNull(first.getReplayCache());
            assertSame(first.getReplayCache(), second.getReplayCache());
        } finally {
            systemProperties.remove("saml.security.want_assertions_signed");
        }
    }

    @Test
    public void test_getLogoutResponse_withoutIdpSingleLogoutServiceUrl() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticator();
        // a real logout message, so that the missing configuration is what fails
        getMockRequest().setParameter("SAMLRequest", "PHNhbWxwOkxvZ291dFJlcXVlc3QgLz4=");
        try {
            authenticator.getResponse(SsoResponseType.LOGOUT);
            fail("SsoMessageException should be thrown");
        } catch (final SsoMessageException e) {
            assertNotNull(e.getCause());
            assertTrue(e.getCause().getMessage(), e.getCause().getMessage().contains("single logout service URL"));
        }
    }

    @Test
    public void test_getLogoutResponse_withoutSamlLogoutMessageAndWithoutSlo() throws Exception {
        // single logout is optional, so the anonymous visit below reaches a deployment that never
        // configured it. It must still be rejected as a request rather than reported as a fault,
        // or /sso/logout writes a stack trace per anonymous hit on every such deployment.
        final SamlAuthenticator authenticator = createAuthenticator();
        try {
            authenticator.getResponse(SsoResponseType.LOGOUT);
            fail("SsoMessageException should be thrown");
        } catch (final SsoMessageException e) {
            assertTrue(String.valueOf(e.getCause()), e.getCause() instanceof SsoStateException);
            assertEquals("This endpoint expects a SAML logout message from the IdP.", e.getCause().getMessage());
        }
    }

    // ===================================================================================
    //                                                             Assertion Consumer Service
    //                                                             ==========================

    /** Minimal IdP settings, so that an AuthnRequest can actually be built. */
    private void setUpIdp(final DynamicProperties systemProperties) {
        systemProperties.setProperty("saml.idp.entityid", "https://idp.example.com/metadata");
        systemProperties.setProperty("saml.idp.single_sign_on_service.url", "https://idp.example.com/sso");
        systemProperties.setProperty("saml.idp.certfingerprint", "afe71c28ef740bc87425be13a2263d37971da1f9");
    }

    private void tearDownIdp(final DynamicProperties systemProperties) {
        systemProperties.remove("saml.idp.entityid");
        systemProperties.remove("saml.idp.single_sign_on_service.url");
        systemProperties.remove("saml.idp.certfingerprint");
    }

    /** Lets a test move the clock that pending AuthnRequest ID expiry is measured against. */
    private final AtomicLong clock = new AtomicLong(1_000_000L);

    private SamlAuthenticator createAuthenticatorWithControlledClock() throws Exception {
        ComponentUtil.register(new SystemHelper() {
            @Override
            public long getCurrentTimeAsLong() {
                return clock.get();
            }
        }, "systemHelper");
        return createAuthenticator();
    }

    /**
     * Reads the pending AuthnRequest IDs out of the session without assuming the shape of the
     * attribute, so that the same assertion can be run against the build that stored a single ID
     * as a bare String.
     */
    private Set<String> pendingRequestIds(final HttpSession session) {
        final Object value = session == null ? null : session.getAttribute("SAML_STATE");
        if (value instanceof final Map<?, ?> requestIdMap) {
            return requestIdMap.keySet().stream().map(String::valueOf).collect(Collectors.toCollection(LinkedHashSet::new));
        }
        if (value instanceof final String requestId) {
            return new LinkedHashSet<>(List.of(requestId));
        }
        return new LinkedHashSet<>();
    }

    /**
     * Puts a SAML response on the request that is well formed enough to reach the InResponseTo
     * comparison and is rejected right after it. It carries no signature, so it can never
     * authenticate; what it makes observable is which pending AuthnRequest ID it was compared
     * with, without a test having to sign an assertion.
     *
     * @param request The request the IdP is pretending to post to.
     * @param inResponseTo The AuthnRequest ID the response claims to answer.
     */
    private void postSamlResponse(final MockletHttpServletRequest request, final String inResponseTo) {
        final String xml = "<samlp:Response xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\""
                + " xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"_response\" Version=\"2.0\""
                + " IssueInstant=\"2026-01-01T00:00:00Z\" InResponseTo=\"" + inResponseTo + "\">"
                + "<samlp:Status><samlp:StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:Success\"/></samlp:Status>"
                + "<saml:Assertion ID=\"_assertion\" Version=\"2.0\" IssueInstant=\"2026-01-01T00:00:00Z\">"
                + "<saml:Issuer>https://idp.example.com/metadata</saml:Issuer></saml:Assertion></samlp:Response>";
        request.setMethod("POST");
        request.setParameter("SAMLResponse", Base64.getEncoder().encodeToString(xml.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void test_containsSamlResponse() throws Exception {
        final SamlAuthenticator authenticator = new SamlAuthenticator();

        assertFalse(authenticator.containsSamlResponse(getMockRequest()));

        final MockletHttpServletRequest blank = getMockRequest();
        blank.setParameter("SAMLResponse", "  ");
        assertFalse(authenticator.containsSamlResponse(blank));

        final MockletHttpServletRequest posted = getMockRequest();
        posted.setParameter("SAMLResponse", "PHNhbWxwOlJlc3BvbnNlIC8+");
        assertTrue(authenticator.containsSamlResponse(posted));
    }

    @Test
    public void test_getLoginCredential_unmatchedResponseFailsInsteadOfRedirecting() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
        try {
            setUpIdp(systemProperties);
            // the IdP posts the assertion cross-site, so a SameSite=Lax cookie is not sent back
            // and the session holding the AuthnRequest ID is unreachable
            final MockletHttpServletRequest request = getMockRequest();
            request.setMethod("POST");
            request.setParameter("SAMLResponse", "PHNhbWxwOlJlc3BvbnNlIC8+");

            // redirecting to the IdP again would come straight back in the same state
            assertNull(authenticator.getLoginCredential());
            assertEquals(1, appender.warnings().size());
            assertTrue(appender.warnings().get(0), appender.warnings().get(0).contains("no matching AuthnRequest ID"));
            // there is no session at all, which is the one situation the cookie really explains,
            // so this is where that guidance has to stay
            assertTrue(appender.warnings().get(0), appender.warnings().get(0).contains("tomcat.sameSiteCookies"));
        } finally {
            tearDownIdp(systemProperties);
            appender.detach();
        }
    }

    @Test
    public void test_getLoginCredential_requestWithoutResponseStartsLogin() throws Exception {
        // recording the AuthnRequest ID stamps it with SystemHelper's clock, which test_app.xml
        // does not register on its own
        final SamlAuthenticator authenticator = createAuthenticatorWithControlledClock();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpIdp(systemProperties);
            final MockletHttpServletRequest request = getMockRequest();

            final LoginCredential credential = authenticator.getLoginCredential();

            assertTrue(String.valueOf(credential), credential instanceof ActionResponseCredential);
            assertNotNull(request.getSession(false).getAttribute("SAML_STATE"));
        } finally {
            tearDownIdp(systemProperties);
        }
    }

    @Test
    public void test_getLoginCredential_requestWithoutResponseKeepsPendingRequestIds() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticatorWithControlledClock();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpIdp(systemProperties);
            // FessSearchAction redirects every unauthenticated page hit to /sso/, so a session
            // that expires with two tabs open sends two AuthnRequests. A single slot made the
            // second visit abandon the first login, and the first assertion back then consumed
            // the slot, failed the InResponseTo comparison and took both tabs down with it.
            final MockletHttpServletRequest request = getMockRequest();

            authenticator.getLoginCredential();
            final Set<String> afterFirstVisit = pendingRequestIds(request.getSession(false));
            clock.addAndGet(1000L);
            authenticator.getLoginCredential();
            final Set<String> afterSecondVisit = pendingRequestIds(request.getSession(false));

            assertEquals(1, afterFirstVisit.size(), String.valueOf(afterFirstVisit));
            assertEquals(2, afterSecondVisit.size(), String.valueOf(afterSecondVisit));
            assertTrue(String.valueOf(afterSecondVisit), afterSecondVisit.containsAll(afterFirstVisit));
            afterSecondVisit.forEach(requestId -> assertTrue(requestId, requestId.startsWith("ONELOGIN_")));
        } finally {
            tearDownIdp(systemProperties);
        }
    }

    @Test
    public void test_getLoginCredential_requestWithoutResponseCarriesOverALegacyRequestId() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticatorWithControlledClock();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpIdp(systemProperties);
            // A session created before this change holds the single pending ID as a bare String.
            // Casting it to the map would end the login with a ClassCastException, and dropping
            // it would abandon a login that was already in flight over the upgrade.
            final MockletHttpServletRequest request = getMockRequest();
            request.getSession().setAttribute("SAML_STATE", "ONELOGIN_legacy");

            final LoginCredential credential = authenticator.getLoginCredential();

            assertTrue(String.valueOf(credential), credential instanceof ActionResponseCredential);
            final Set<String> pending = pendingRequestIds(request.getSession(false));
            assertEquals(2, pending.size(), String.valueOf(pending));
            assertTrue(String.valueOf(pending), pending.contains("ONELOGIN_legacy"));
        } finally {
            tearDownIdp(systemProperties);
        }
    }

    @Test
    public void test_getRequestIdMap_replacesAnUnusableSessionValue() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticatorWithControlledClock();
        final HttpSession session = getMockRequest().getSession();
        session.setAttribute("SAML_STATE", Integer.valueOf(42));

        final Map<String, Long> requestIdMap = authenticator.getRequestIdMap(session);

        // Anything that is not a live map and not a legacy ID is discarded rather than cast.
        assertTrue(requestIdMap.toString(), requestIdMap.isEmpty());
        assertTrue(String.valueOf(requestIdMap), requestIdMap instanceof ConcurrentHashMap);
        // The map that was handed out is the one stored back, so later writes are not lost.
        assertSame(requestIdMap, session.getAttribute("SAML_STATE"));
    }

    @Test
    public void test_getLoginCredential_capsThePendingRequestIds() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticatorWithControlledClock();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpIdp(systemProperties);
            // /sso/ is anonymous and answers GET, so a page embedding it as a sub-resource would
            // otherwise grow the session attribute for as long as the session lives.
            final MockletHttpServletRequest request = getMockRequest();
            final List<String> issuedRequestIds = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                clock.addAndGet(1000L);
                authenticator.getLoginCredential();
                pendingRequestIds(request.getSession(false)).stream()
                        .filter(requestId -> !issuedRequestIds.contains(requestId))
                        .forEach(issuedRequestIds::add);
            }

            final Set<String> pending = pendingRequestIds(request.getSession(false));

            assertEquals(12, issuedRequestIds.size(), String.valueOf(issuedRequestIds));
            assertEquals(10, pending.size(), String.valueOf(pending));
            // The two oldest were evicted; the most recent are the ones a user can still finish.
            assertFalse(String.valueOf(pending), pending.contains(issuedRequestIds.get(0)));
            assertFalse(String.valueOf(pending), pending.contains(issuedRequestIds.get(1)));
            assertTrue(String.valueOf(pending), pending.contains(issuedRequestIds.get(2)));
            assertTrue(String.valueOf(pending), pending.contains(issuedRequestIds.get(11)));
        } finally {
            tearDownIdp(systemProperties);
        }
    }

    @Test
    public void test_getLoginCredential_expiredRequestIdCannotBeAnswered() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticatorWithControlledClock();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpIdp(systemProperties);
            final MockletHttpServletRequest request = getMockRequest();
            authenticator.getLoginCredential();
            final String requestId = pendingRequestIds(request.getSession(false)).iterator().next();

            // attached only now, so that the insecure-settings warning the first getSettings()
            // emits is not one of the messages asserted on below
            final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
            try {
                // getRequestIdTtl() defaults to 3600 and is compared in seconds
                clock.addAndGet(3601L * 1000L);
                postSamlResponse(request, requestId);

                assertNull(authenticator.getLoginCredential());
                assertEquals(1, appender.warnings().size(), String.valueOf(appender.warnings()));
                // The session was found and it did hold the ID until this very request pruned it,
                // so the cookie demonstrably arrived. Reporting this as the SameSite case sends an
                // operator whose cookie settings are already right off to change them, and this is
                // the ordinary outcome of a user who walks away mid-login.
                assertTrue(appender.warnings().get(0), appender.warnings().get(0).contains("had expired"));
                assertTrue(appender.warnings().get(0), appender.warnings().get(0).contains("all 1 pending"));
                assertTrue(appender.warnings().get(0), appender.warnings().get(0).contains("saml.request.id.ttl"));
                assertFalse(appender.warnings().get(0), appender.warnings().get(0).contains("tomcat.sameSiteCookies"));
                assertTrue(pendingRequestIds(request.getSession(false)).toString(), pendingRequestIds(request.getSession(false)).isEmpty());
            } finally {
                appender.detach();
            }
        } finally {
            tearDownIdp(systemProperties);
        }
    }

    @Test
    public void test_getLoginCredential_responseMatchingNoPendingRequestIdFails() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticatorWithControlledClock();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpIdp(systemProperties);
            final MockletHttpServletRequest request = getMockRequest();
            authenticator.getLoginCredential();
            final Set<String> pending = pendingRequestIds(request.getSession(false));

            final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
            try {
                postSamlResponse(request, "ONELOGIN_never-sent");

                // Answering it with a fresh AuthnRequest would bounce off an IdP that is already
                // authenticated and come straight back here, forever.
                assertNull(authenticator.getLoginCredential());
                assertEquals(1, appender.warnings().size(), String.valueOf(appender.warnings()));
                assertTrue(appender.warnings().get(0), appender.warnings().get(0).contains("no matching AuthnRequest ID"));
                assertTrue(appender.warnings().get(0), appender.warnings().get(0).contains("(1 pending)"));
                // A response nobody asked for must not be able to burn a login that is in flight:
                // the assertion consumer service is reachable cross-site, since SAML needs
                // SameSite=none, so consuming an ID here would be a denial of service per request.
                Assertions.assertEquals(pending, pendingRequestIds(request.getSession(false)));
            } finally {
                appender.detach();
            }
        } finally {
            tearDownIdp(systemProperties);
        }
    }

    @Test
    public void test_getLoginCredential_reportsWhyTheResponseWasRefused() throws Exception {
        // getErrors() answers a category: "invalid_response" covers a bad signature, an expired
        // assertion, a foreign audience, a replay and a rewritten destination alike. On its own it
        // tells an administrator only that the login failed, so the reason has to reach the log
        // too -- and not only when saml.debug is on, which is off in every shipped configuration.
        final SamlAuthenticator authenticator = createAuthenticatorWithControlledClock();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpIdp(systemProperties);
            systemProperties.setProperty("saml.security.want_xml_validation", "false");
            assertNull(systemProperties.getProperty("saml.debug"));
            final MockletHttpServletRequest request = getMockRequest();
            authenticator.getLoginCredential();
            final String requestId = pendingRequestIds(request.getSession(false)).iterator().next();

            final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
            try {
                postSamlResponse(request, requestId);
                assertNull(authenticator.getLoginCredential());

                assertEquals(1, appender.warnings().size(), String.valueOf(appender.warnings()));
                final String failure = appender.warnings().get(0);
                // measured: "... - Reason: The Assertion must include a Conditions element", which
                // is the part an administrator can act on. The wording belongs to java-saml, so the
                // assertion is that a reason follows the category rather than what it says.
                final String prefix = "Authentication Failure: invalid_response - Reason: ";
                assertTrue(failure, failure.startsWith(prefix));
                assertTrue(failure, failure.length() > prefix.length());
            } finally {
                appender.detach();
            }
        } finally {
            systemProperties.remove("saml.security.want_xml_validation");
            tearDownIdp(systemProperties);
        }
    }

    @Test
    public void test_getLoginCredential_answersTheOlderOfTwoPendingRequestIds() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticatorWithControlledClock();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpIdp(systemProperties);
            // schema validation is not what this test is about, and switching it off keeps the
            // response below down to the elements the InResponseTo comparison needs
            systemProperties.setProperty("saml.security.want_xml_validation", "false");
            final MockletHttpServletRequest request = getMockRequest();
            authenticator.getLoginCredential();
            final String olderRequestId = pendingRequestIds(request.getSession(false)).iterator().next();
            clock.addAndGet(1000L);
            authenticator.getLoginCredential();
            final Set<String> pending = pendingRequestIds(request.getSession(false));
            assertEquals(2, pending.size(), String.valueOf(pending));

            final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
            try {
                // the first tab's assertion comes back while the second tab's AuthnRequest is the
                // most recent one, which is the case that used to fail both logins
                postSamlResponse(request, olderRequestId);
                assertNull(authenticator.getLoginCredential());

                assertEquals(1, appender.warnings().size(), String.valueOf(appender.warnings()));
                final String failure = appender.warnings().get(0);
                // reaching the failure line at all means the loop went past the newest pending ID:
                // a response matching none of them is reported by logUnmatchedSamlResponse instead
                assertTrue(failure, failure.startsWith("Authentication Failure:"));
                assertFalse(failure, failure.contains("no matching AuthnRequest ID"));
                // and the reason reported is the older candidate's own rejection rather than the
                // InResponseTo mismatch the newest candidate was ruled out on, which is what shows
                // the response was tried against more than one. Read from our own log line rather
                // than java-saml's, so that the test does not depend on the level that library
                // reports a ruled-out candidate at.
                assertTrue(failure, failure.contains("- Reason:"));
                assertFalse(failure, failure.contains("does not match the ID of the AuthNRequest"));
                assertFalse(failure, failure.contains(olderRequestId));
            } finally {
                appender.detach();
            }
        } finally {
            systemProperties.remove("saml.security.want_xml_validation");
            tearDownIdp(systemProperties);
        }
    }

    /**
     * Wraps an authenticator whose response processing always fails with {@code failure}, so that
     * a test can choose the exception {@code getLoginCredential} has to classify. The instance is
     * deliberately left without {@code defaultSettings}: the SAMLResponse branch reaches the
     * override before anything asks for settings, so a test that needed them would be testing a
     * different path than the one it names.
     */
    private SamlAuthenticator failingAuthenticator(final RuntimeException failure) {
        return new SamlAuthenticator() {
            @Override
            protected LoginCredential processSamlResponse(final HttpServletRequest request, final HttpServletResponse response,
                    final Map<String, Long> requestIdMap) {
                throw failure;
            }
        };
    }

    /**
     * Seeds a session with one pending AuthnRequest ID and puts {@code samlResponse} on the
     * request, which is the state a malformed callback arrives in.
     *
     * @param authenticator The authenticator that sends the AuthnRequest.
     * @param samlResponse The raw value of the SAMLResponse parameter.
     * @return The request, now carrying both the session and the response.
     */
    private MockletHttpServletRequest postRawSamlResponse(final SamlAuthenticator authenticator, final String samlResponse) {
        final MockletHttpServletRequest request = getMockRequest();
        authenticator.getLoginCredential();
        request.setMethod("POST");
        request.setParameter("SAMLResponse", samlResponse);
        return request;
    }

    /** A SAMLResponse that is not base64 at all, the cheapest payload an anonymous client can send. */
    private static final String NOT_BASE64 = "!!! not base64 at all !!!";

    /** A SAMLResponse that decodes cleanly and is then not parsable as XML. */
    private static final String BROKEN_XML = Base64.getEncoder().encodeToString("<samlp:Response".getBytes(StandardCharsets.UTF_8));

    @Test
    public void test_getLoginCredential_unparsableResponseIsWarnedWithoutAStackTrace() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticatorWithControlledClock();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpIdp(systemProperties);
            final MockletHttpServletRequest request = postRawSamlResponse(authenticator, NOT_BASE64);

            final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
            try {
                assertNull(authenticator.getLoginCredential());

                // /sso/ is anonymous, so this is a rejected request rather than a fault of this
                // server: one line, and no stack trace an unauthenticated client can repeat.
                final List<LogEvent> warnEvents = appender.eventsAt(Level.WARN);
                assertEquals(1, warnEvents.size(), String.valueOf(appender.warnings()));
                assertEquals("Authentication failed: ValidationException: SAML Response could not be processed",
                        appender.warnings().get(0));
                assertNull(warnEvents.get(0).getThrown(), String.valueOf(warnEvents.get(0).getThrown()));
                // the pending ID is not consumed by the failure, which is what makes the same
                // request repeatable for the whole TTL and therefore worth not tracing
                assertEquals(1, pendingRequestIds(request.getSession(false)).size());
            } finally {
                appender.detach();
            }
        } finally {
            tearDownIdp(systemProperties);
        }
    }

    @Test
    public void test_getLoginCredential_brokenXmlResponseIsWarnedWithoutAStackTrace() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticatorWithControlledClock();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpIdp(systemProperties);
            // base64 that decodes fine and is then not XML reaches the same throw by a different
            // route, so neither a stricter decoder nor a stricter parser alone closes this
            postRawSamlResponse(authenticator, BROKEN_XML);

            final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
            try {
                assertNull(authenticator.getLoginCredential());

                final List<LogEvent> warnEvents = appender.eventsAt(Level.WARN);
                assertEquals(1, warnEvents.size(), String.valueOf(appender.warnings()));
                assertEquals("Authentication failed: ValidationException: SAML Response could not be processed",
                        appender.warnings().get(0));
                assertNull(warnEvents.get(0).getThrown(), String.valueOf(warnEvents.get(0).getThrown()));
            } finally {
                appender.detach();
            }
        } finally {
            tearDownIdp(systemProperties);
        }
    }

    @Test
    public void test_getLoginCredential_unparsableResponseKeepsTheStackTraceAtDebug() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticatorWithControlledClock();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpIdp(systemProperties);
            postRawSamlResponse(authenticator, NOT_BASE64);

            final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
            try {
                assertNull(authenticator.getLoginCredential());

                // dropping the trace from the WARN must not drop it from the build: an operator
                // chasing a real IdP problem raises the level and gets everything back
                final List<LogEvent> traced = appender.eventsAt(Level.DEBUG)
                        .stream()
                        .filter(e -> "Authentication failed.".equals(e.getMessage().getFormattedMessage()))
                        .toList();
                assertEquals(1, traced.size(), String.valueOf(appender.messagesAt(Level.DEBUG)));
                assertTrue(String.valueOf(traced.get(0).getThrown()), traced.get(0).getThrown() instanceof ValidationException);
            } finally {
                appender.detach();
            }
        } finally {
            tearDownIdp(systemProperties);
        }
    }

    @Test
    public void test_getLoginCredential_unparsableResponseCanBeRepeatedWithThePendingRequestId() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticatorWithControlledClock();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpIdp(systemProperties);
            final MockletHttpServletRequest request = postRawSamlResponse(authenticator, NOT_BASE64);
            final Set<String> pending = pendingRequestIds(request.getSession(false));
            assertEquals(1, pending.size(), String.valueOf(pending));

            final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
            try {
                assertNull(authenticator.getLoginCredential());
                assertNull(authenticator.getLoginCredential());
                assertNull(authenticator.getLoginCredential());

                // this is the whole reason the trace has to go: the failure leaves the pending ID
                // in place, so the very same anonymous request answers again and again
                assertEquals(3, appender.warnings().size(), String.valueOf(appender.warnings()));
                Assertions.assertEquals(pending, pendingRequestIds(request.getSession(false)));
                appender.eventsAt(Level.WARN).forEach(e -> assertNull(e.getThrown(), e.getMessage().getFormattedMessage()));
            } finally {
                appender.detach();
            }
        } finally {
            tearDownIdp(systemProperties);
        }
    }

    @Test
    public void test_getLoginCredential_unparsableResponseWarningNamesTheNestedCause() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticatorWithControlledClock();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpIdp(systemProperties);
            final MockletHttpServletRequest request = postRawSamlResponse(authenticator, BROKEN_XML);
            // java-saml's own wrapper for a parse failure, whose message names neither what
            // failed nor where: dropping the cause chain here would leave the WARN useless
            final RuntimeException failure = new XMLParsingException("Failed to load XML data.",
                    new IOException("Stream closed", new IllegalStateException("underlying detail")));

            final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
            try {
                assertNull(failingAuthenticator(failure).getLoginCredential());

                assertEquals(1, appender.warnings().size(), String.valueOf(appender.warnings()));
                assertEquals("Authentication failed: XMLParsingException: Failed to load XML data."
                        + " <- IOException: Stream closed <- IllegalStateException: underlying detail", appender.warnings().get(0));
            } finally {
                appender.detach();
            }
            assertEquals(1, pendingRequestIds(request.getSession(false)).size());
        } finally {
            tearDownIdp(systemProperties);
        }
    }

    @Test
    public void test_getLoginCredential_nonSamlFailureKeepsItsStackTrace() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticatorWithControlledClock();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpIdp(systemProperties);
            postRawSamlResponse(authenticator, BROKEN_XML);
            // anything that is not a SAML failure is a bug in this server rather than a payload an
            // anonymous client chose, so it keeps the trace it always had
            final RuntimeException failure = new IllegalStateException("something this server got wrong");

            final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
            try {
                assertNull(failingAuthenticator(failure).getLoginCredential());

                final List<LogEvent> warnEvents = appender.eventsAt(Level.WARN);
                assertEquals(1, warnEvents.size(), String.valueOf(appender.warnings()));
                assertEquals("Authentication failed.", appender.warnings().get(0));
                assertSame(failure, warnEvents.get(0).getThrown());
            } finally {
                appender.detach();
            }
        } finally {
            tearDownIdp(systemProperties);
        }
    }

    @Test
    public void test_describeSamlFailure_stopsOnACyclicCauseChain() throws Exception {
        final SAMLException first = new SAMLException("first");
        final SAMLException second = new SAMLException("second", first);
        first.initCause(second);

        // a chain that points back at itself must end the rendering, not the request
        assertEquals("SAMLException: first <- SAMLException: second", new SamlAuthenticator().describeSamlFailure(first));
    }

    @Test
    public void test_getRequestIdTtl_defaultsToOneHourInSeconds() throws Exception {
        // removeExpiredRequestIds compares (now - created) / 1000 against this value
        assertEquals(3600L, new SamlAuthenticator().getRequestIdTtl());
    }

    @Test
    public void test_getRequestIdTtl_fallsBackWhenTheConfiguredValueIsNotANumber() throws Exception {
        // A typo in conf/system.properties must not fail every login with a
        // NumberFormatException nobody can act on.
        final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
        ComponentUtil.getFessConfig().setSystemProperty("saml.request.id.ttl", "one hour");
        try {
            assertEquals(3600L, new SamlAuthenticator().getRequestIdTtl());
            assertEquals(1, appender.warnings().size(), String.valueOf(appender.warnings()));
            assertTrue(appender.warnings().get(0), appender.warnings().get(0).contains("saml.request.id.ttl"));
        } finally {
            ComponentUtil.getFessConfig().setSystemProperty("saml.request.id.ttl", "");
            appender.detach();
        }
    }

    @Test
    public void test_getRequestIdTtl_blankValueIsNotReportedAsInvalid() throws Exception {
        // A blank property means "unset" everywhere else in this class, so it must not warn.
        final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
        ComponentUtil.getFessConfig().setSystemProperty("saml.request.id.ttl", " ");
        try {
            assertEquals(3600L, new SamlAuthenticator().getRequestIdTtl());
            assertTrue(String.valueOf(appender.warnings()), appender.warnings().isEmpty());
        } finally {
            ComponentUtil.getFessConfig().setSystemProperty("saml.request.id.ttl", "");
            appender.detach();
        }
    }

    @Test
    public void test_getRequestIdTtl_fallsBackWhenTheConfiguredValueIsNotPositive() throws Exception {
        // Both values parse, so nothing fails here, but removeExpiredRequestIds compares
        // (now - created) / 1000 against the result: 0 drops the AuthnRequest ID one second after
        // the IdP was sent to, and -1 drops it at once, so every SAML login in the deployment
        // fails. 0 is not a far-fetched thing to write, either: it reads as "no expiry".
        final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
        try {
            ComponentUtil.getFessConfig().setSystemProperty("saml.request.id.ttl", "0");
            assertEquals(3600L, new SamlAuthenticator().getRequestIdTtl());

            ComponentUtil.getFessConfig().setSystemProperty("saml.request.id.ttl", "-1");
            assertEquals(3600L, new SamlAuthenticator().getRequestIdTtl());

            assertEquals(2, appender.warnings().size(), String.valueOf(appender.warnings()));
            assertTrue(appender.warnings().get(0), appender.warnings().get(0).contains("saml.request.id.ttl"));
            // the value that was configured, not only the default that replaced it
            assertTrue(appender.warnings().get(0), appender.warnings().get(0).contains(": 0."));
            assertTrue(appender.warnings().get(1), appender.warnings().get(1).contains(": -1."));
        } finally {
            ComponentUtil.getFessConfig().setSystemProperty("saml.request.id.ttl", "");
            appender.detach();
        }
    }

    @Test
    public void test_getRequestIdTtl_reportsANonPositiveValueDifferentlyFromANonNumericOne() throws Exception {
        // The two mistakes need different corrections, and 0 is a number, so reporting it as
        // invalid would send an administrator hunting for a typo that is not there.
        final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
        try {
            ComponentUtil.getFessConfig().setSystemProperty("saml.request.id.ttl", "0");
            new SamlAuthenticator().getRequestIdTtl();

            ComponentUtil.getFessConfig().setSystemProperty("saml.request.id.ttl", "one hour");
            new SamlAuthenticator().getRequestIdTtl();

            assertEquals(2, appender.warnings().size(), String.valueOf(appender.warnings()));
            final String nonPositive = appender.warnings().get(0);
            final String nonNumeric = appender.warnings().get(1);
            assertTrue(nonPositive, nonPositive.contains("positive"));
            assertFalse(nonPositive, nonPositive.contains("Invalid"));
            assertTrue(nonNumeric, nonNumeric.contains("Invalid"));
            assertFalse(nonNumeric, nonNumeric.contains("positive"));
        } finally {
            ComponentUtil.getFessConfig().setSystemProperty("saml.request.id.ttl", "");
            appender.detach();
        }
    }

    @Test
    public void test_setMaxRequestIds_honoursAPositiveValue() throws Exception {
        // fess_sso++.xml offers 10 as the line to uncomment, so that is what an untouched
        // deployment has to be running with.
        final SamlAuthenticator authenticator = new SamlAuthenticator();
        assertEquals(10, authenticator.maxRequestIds);

        final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
        try {
            authenticator.setMaxRequestIds(1);
            assertEquals(1, authenticator.maxRequestIds);

            authenticator.setMaxRequestIds(50);
            assertEquals(50, authenticator.maxRequestIds);

            assertTrue(String.valueOf(appender.warnings()), appender.warnings().isEmpty());
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_setMaxRequestIds_fallsBackWhenTheValueIsNotPositive() throws Exception {
        // Both values are applied without failing here, but getCandidateRequestIds hands the cap
        // to limit(): 0 leaves processSamlResponse no candidate to try and -1 makes limit() throw,
        // so either way every SAML login in the deployment fails. 0 is not a far-fetched thing to
        // write, either: it reads as "no limit".
        final SamlAuthenticator authenticator = new SamlAuthenticator();
        final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
        try {
            authenticator.setMaxRequestIds(0);
            assertEquals(10, authenticator.maxRequestIds);

            authenticator.setMaxRequestIds(-1);
            assertEquals(10, authenticator.maxRequestIds);

            assertEquals(2, appender.warnings().size(), String.valueOf(appender.warnings()));
            assertTrue(appender.warnings().get(0), appender.warnings().get(0).contains("maxRequestIds"));
            // the value that was configured, not only the default that replaced it
            assertTrue(appender.warnings().get(0), appender.warnings().get(0).contains(": 0."));
            assertTrue(appender.warnings().get(1), appender.warnings().get(1).contains(": -1."));
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_getCandidateRequestIds_isNotEmptiedByANonPositiveCap() throws Exception {
        // The end the guard protects. Taken literally, the cap discards the pending ID the
        // session does hold before processSamlResponse ever sees it, and the login is then
        // reported as a session cookie that never arrived.
        final Map<String, Long> requestIdMap = new ConcurrentHashMap<>();
        requestIdMap.put("_pending", 1000L);
        final SamlAuthenticator authenticator = new SamlAuthenticator();
        final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
        try {
            authenticator.setMaxRequestIds(0);
            Assertions.assertEquals(List.of("_pending"), authenticator.getCandidateRequestIds(requestIdMap));

            // -1 does not merely return nothing: limit() throws, and processSamlResponse's caller
            // logs that as "Authentication failed." with no hint of the cap.
            authenticator.setMaxRequestIds(-1);
            Assertions.assertEquals(List.of("_pending"), authenticator.getCandidateRequestIds(requestIdMap));
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_getLoginCredential_nonPositiveRequestIdTtlStillAnswersAResponse() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticatorWithControlledClock();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpIdp(systemProperties);
            // Taken literally, this expires the AuthnRequest ID one second after the browser was
            // sent to the IdP, which no round trip can beat: the deployment would answer every
            // login attempt with a warning and no session would ever be created.
            ComponentUtil.getFessConfig().setSystemProperty("saml.request.id.ttl", "0");
            final MockletHttpServletRequest request = getMockRequest();
            authenticator.getLoginCredential();
            final String requestId = pendingRequestIds(request.getSession(false)).iterator().next();

            final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
            try {
                clock.addAndGet(1000L);
                postSamlResponse(request, requestId);

                // The response is unsigned, so it cannot authenticate; what this pins is that it
                // was compared with the pending ID at all rather than finding it already pruned.
                assertNull(authenticator.getLoginCredential());
                final List<String> warnings = appender.warnings();
                assertTrue(String.valueOf(warnings), warnings.stream().anyMatch(w -> w.startsWith("Authentication Failure:")));
                assertTrue(String.valueOf(warnings), warnings.stream().noneMatch(w -> w.contains("had expired")));
                assertTrue(String.valueOf(warnings), warnings.stream().noneMatch(w -> w.contains("no matching AuthnRequest ID")));
                // and a rejected response does not consume the ID, so the login is still live
                final Set<String> pending = pendingRequestIds(request.getSession(false));
                assertTrue(String.valueOf(pending), pending.contains(requestId));
            } finally {
                appender.detach();
            }
        } finally {
            ComponentUtil.getFessConfig().setSystemProperty("saml.request.id.ttl", "");
            tearDownIdp(systemProperties);
        }
    }

    @Test
    public void test_logUnmatchedSamlResponse_separatesAnExpiredLoginFromAMissingSessionCookie() throws Exception {
        // Both situations reach the log with nothing pending, so the text is the only thing that
        // can tell them apart, and only one of them is a misconfiguration.
        final SamlAuthenticator authenticator = new SamlAuthenticator();
        final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
        try {
            authenticator.logUnmatchedSamlResponse(0);
            authenticator.logUnmatchedSamlResponseAfterExpiry(1);

            assertEquals(2, appender.warnings().size(), String.valueOf(appender.warnings()));
            final String missingCookie = appender.warnings().get(0);
            final String expired = appender.warnings().get(1);
            assertTrue(missingCookie, missingCookie.contains("tomcat.sameSiteCookies"));
            assertFalse(missingCookie, missingCookie.contains("saml.request.id.ttl"));
            assertTrue(expired, expired.contains("saml.request.id.ttl"));
            assertFalse(expired, expired.contains("tomcat.sameSiteCookies"));
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_getLoginCredential_duplicatedAttributeNameNamesTheSettingThatAcceptsIt() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticatorWithControlledClock();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpIdp(systemProperties);
            final MockletHttpServletRequest request = postRawSamlResponse(authenticator, BROKEN_XML);
            // what an IdP that emits one <Attribute> element per value produces; the library
            // raises it from getAttributes(), after the assertion has already validated
            final RuntimeException failure = new ValidationException("Found an Attribute element with duplicated Name",
                    ValidationException.DUPLICATED_ATTRIBUTE_NAME_FOUND);

            final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
            try {
                assertNull(failingAuthenticator(failure).getLoginCredential());

                assertEquals(1, appender.warnings().size(), String.valueOf(appender.warnings()));
                final String warning = appender.warnings().get(0);
                // the setting is the whole point of the line: without it an administrator is told
                // a fact about the XML and has nothing to change
                assertTrue(warning, warning.contains("saml.security.allow_duplicated_attribute_name"));
                // and it must not be mistaken for the two cookie/timing diagnoses
                assertFalse(warning, warning.contains("tomcat.sameSiteCookies"));
                assertFalse(warning, warning.contains("saml.request.id.ttl"));
                appender.eventsAt(Level.WARN).forEach(e -> assertNull(e.getThrown(), e.getMessage().getFormattedMessage()));
            } finally {
                appender.detach();
            }
            // the ID is still there, so the login works as soon as either side is reconfigured
            assertEquals(1, pendingRequestIds(request.getSession(false)).size());
        } finally {
            tearDownIdp(systemProperties);
        }
    }

    @Test
    public void test_isDuplicatedAttributeName_matchesOnlyThatErrorCode() throws Exception {
        final SamlAuthenticator authenticator = new SamlAuthenticator();

        assertTrue(authenticator
                .isDuplicatedAttributeName(new ValidationException("duplicated", ValidationException.DUPLICATED_ATTRIBUTE_NAME_FOUND)));
        // the code a retried candidate produces, which processSamlResponse handles on its own
        assertFalse(authenticator.isDuplicatedAttributeName(new ValidationException("mismatch", ValidationException.WRONG_INRESPONSETO)));
        assertFalse(authenticator.isDuplicatedAttributeName(new SAMLException("not a validation failure")));
    }

    @Test
    public void test_hasExpiredSession_needsASessionIdThatIsNoLongerValid() throws Exception {
        final SamlAuthenticator authenticator = new SamlAuthenticator();
        // one request, walked through the three states, because getMockRequest() hands out the
        // same instance for the whole test method
        final MockletHttpServletRequest request = getMockRequest();

        // a browser that is not sending the cookie sends no session id, so nothing was lost
        assertNull(request.getRequestedSessionId());
        assertFalse(authenticator.hasExpiredSession(request));

        // an id that came back with nothing behind it is the case worth naming
        request.addCookie(new Cookie("jsessionid", "AB1C2D3E4F5061728394A5B6C7D8E9F0"));
        assertNotNull(request.getRequestedSessionId());
        assertFalse(request.isRequestedSessionIdValid());
        assertTrue(authenticator.hasExpiredSession(request));

        // an id the container still knows is a live session, not an expired one
        request.getSession();
        assertTrue(request.isRequestedSessionIdValid());
        assertFalse(authenticator.hasExpiredSession(request));
    }

    @Test
    public void test_getLoginCredential_responseWithoutASessionIdBlamesTheCookie() throws Exception {
        // Nothing came back at all, which really is what a SameSite=Lax cookie on a cross-site
        // POST looks like, so this is the one case that keeps that guidance.
        final SamlAuthenticator authenticator = createAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
        try {
            setUpIdp(systemProperties);
            final MockletHttpServletRequest request = getMockRequest();
            request.setMethod("POST");
            request.setParameter("SAMLResponse", "PHNhbWxwOlJlc3BvbnNlIC8+");
            assertNull(request.getRequestedSessionId());

            assertNull(authenticator.getLoginCredential());
            assertEquals(1, appender.warnings().size(), String.valueOf(appender.warnings()));
            final String warning = appender.warnings().get(0);
            assertTrue(warning, warning.contains("tomcat.sameSiteCookies"));
            assertFalse(warning, warning.contains("session it belongs to had expired"));
        } finally {
            tearDownIdp(systemProperties);
            appender.detach();
        }
    }

    @Test
    public void test_getLoginCredential_expiredSessionIsNotReportedAsABlockedCookie() throws Exception {
        // The realistic "walked away at the IdP" case arrives exactly like this, because the
        // container reaps the session (30 minutes by default) long before saml.request.id.ttl
        // (3600 seconds) can prune an ID, so the AuthnRequest IDs go with it and the branch that
        // counts pruned IDs is never reached. Reported as the SameSite case it sends an operator
        // whose cookie settings are already right off to change them.
        final SamlAuthenticator authenticator = createAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
        try {
            setUpIdp(systemProperties);
            final MockletHttpServletRequest request = getMockRequest();
            request.setMethod("POST");
            request.setParameter("SAMLResponse", "PHNhbWxwOlJlc3BvbnNlIC8+");
            request.addCookie(new Cookie("jsessionid", "AB1C2D3E4F5061728394A5B6C7D8E9F0"));
            // the cookie demonstrably arrived; there is simply no session behind it any more
            assertNotNull(request.getRequestedSessionId());
            assertNull(request.getSession(false));

            // still refused: bouncing back to an IdP that is already authenticated would only
            // post the same unmatched assertion straight back
            assertNull(authenticator.getLoginCredential());
            assertEquals(1, appender.warnings().size(), String.valueOf(appender.warnings()));
            final String warning = appender.warnings().get(0);
            assertTrue(warning, warning.contains("session it belongs to had expired"));
            assertFalse(warning, warning.contains("tomcat.sameSiteCookies"));
            // raising the TTL cannot extend a session that is already the shorter of the two
            assertTrue(warning, warning.contains("raising that value does not help"));
        } finally {
            tearDownIdp(systemProperties);
            appender.detach();
        }
    }

    @Test
    public void test_getLoginCredential_prunedRequestIdsStillNameTheTtl() throws Exception {
        // The third case: the session is alive and its cookie is valid, and only the IDs it held
        // ran out of time. That one really is about saml.request.id.ttl, so the session-expiry
        // wording must not take it over.
        final SamlAuthenticator authenticator = createAuthenticatorWithControlledClock();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpIdp(systemProperties);
            final MockletHttpServletRequest request = getMockRequest();
            request.addCookie(new Cookie("jsessionid", "AB1C2D3E4F5061728394A5B6C7D8E9F0"));
            authenticator.getLoginCredential();
            final String requestId = pendingRequestIds(request.getSession(false)).iterator().next();
            assertTrue(request.isRequestedSessionIdValid());

            final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
            try {
                clock.addAndGet(3601L * 1000L);
                postSamlResponse(request, requestId);

                assertNull(authenticator.getLoginCredential());
                assertEquals(1, appender.warnings().size(), String.valueOf(appender.warnings()));
                final String warning = appender.warnings().get(0);
                assertTrue(warning, warning.contains("all 1 pending AuthnRequest ID(s) of the session had expired"));
                assertFalse(warning, warning.contains("session it belongs to had expired"));
                assertFalse(warning, warning.contains("tomcat.sameSiteCookies"));
            } finally {
                appender.detach();
            }
        } finally {
            tearDownIdp(systemProperties);
        }
    }

    @Test
    public void test_getLogoutResponse_withoutSamlLogoutMessage() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpIdp(systemProperties);
            systemProperties.setProperty("saml.idp.single_logout_service.url", "https://idp.example.com/slo");

            // /sso/logout is anonymous, so it also receives plain visits carrying no SAML message
            authenticator.getResponse(SsoResponseType.LOGOUT);
            fail("SsoMessageException should be thrown");
        } catch (final SsoMessageException e) {
            // a rejected request, not a fault: the cause decides that SsoAction logs it without a
            // stack trace, and the user-facing text is ours rather than the library's binding note
            assertTrue(String.valueOf(e.getCause()), e.getCause() instanceof SsoStateException);
            assertEquals("This endpoint expects a SAML logout message from the IdP.", e.getCause().getMessage());
        } finally {
            systemProperties.remove("saml.idp.single_logout_service.url");
            tearDownIdp(systemProperties);
        }
    }

    @Test
    public void test_getLogoutResponse_rejectsAnUnusableLogoutMessageWithoutAStackTrace() throws Exception {
        // The message here is one the sender supplied and java-saml refused. /sso/logout is
        // anonymous and, because SAML requires SameSite=none, reachable cross-site, so an
        // unauthenticated client can repeat this at will; a stack trace per attempt would let it
        // fill the log. The cause is what SsoAction branches on to log the message alone.
        final SamlAuthenticator authenticator = createAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpIdp(systemProperties);
            systemProperties.setProperty("saml.idp.single_logout_service.url", "https://idp.example.com/slo");
            final MockletHttpServletRequest request = getMockRequest();
            // a LogoutRequest with no NameID: java-saml parses it and then refuses it
            final String xml = "<samlp:LogoutRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\""
                    + " xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"_nonameid\" Version=\"2.0\""
                    + " IssueInstant=\"2026-01-01T00:00:00Z\"/>";
            request.setParameter("SAMLRequest", Base64.getEncoder().encodeToString(xml.getBytes(StandardCharsets.UTF_8)));

            authenticator.getResponse(SsoResponseType.LOGOUT);
            fail("SsoMessageException should be thrown");
        } catch (final SsoMessageException e) {
            assertTrue(String.valueOf(e.getCause()), e.getCause() instanceof SsoStateException);
        } finally {
            systemProperties.remove("saml.idp.single_logout_service.url");
            tearDownIdp(systemProperties);
        }
    }

    @Test
    public void test_containsSamlLogoutMessage() throws Exception {
        final SamlAuthenticator authenticator = new SamlAuthenticator();

        assertFalse(authenticator.containsSamlLogoutMessage(getMockRequest()));

        final MockletHttpServletRequest blank = getMockRequest();
        blank.setParameter("SAMLRequest", "  ");
        assertFalse(authenticator.containsSamlLogoutMessage(blank));

        final MockletHttpServletRequest logoutRequest = getMockRequest();
        logoutRequest.setParameter("SAMLRequest", "PHNhbWxwOkxvZ291dFJlcXVlc3QgLz4=");
        assertTrue(authenticator.containsSamlLogoutMessage(logoutRequest));

        final MockletHttpServletRequest logoutResponse = getMockRequest();
        logoutResponse.setParameter("SAMLResponse", "PHNhbWxwOkxvZ291dFJlc3BvbnNlIC8+");
        assertTrue(authenticator.containsSamlLogoutMessage(logoutResponse));
    }

    @Test
    public void test_getMetadataResponse_withoutIdpSettings() throws Exception {
        // the SP metadata is what the IdP is registered from, so it must not require saml.idp.*
        final SamlAuthenticator authenticator = createAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            systemProperties.setProperty(BASE_URL_KEY, "https://fess.example.com");

            final ActionResponse response = authenticator.getResponse(SsoResponseType.METADATA);

            assertTrue(String.valueOf(response), response instanceof StreamResponse);
            assertEquals("metadata.xml", ((StreamResponse) response).getFileName());
        } finally {
            systemProperties.remove(BASE_URL_KEY);
        }
    }

    @Test
    public void test_getMetadataResponse_reportsInvalidSpSettings() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            // an SP entity ID that is not a URL leaves the ACS URL unset
            systemProperties.setProperty("saml.sp.assertion_consumer_service.url", "not a url");

            authenticator.getResponse(SsoResponseType.METADATA);
            fail("SsoMessageException should be thrown");
        } catch (final SsoMessageException e) {
            assertNotNull(e.getCause());
            assertTrue(e.getCause().getMessage(), e.getCause().getMessage().contains("sp_acs_not_found"));
        } finally {
            systemProperties.remove("saml.sp.assertion_consumer_service.url");
        }
    }

    // ===================================================================================
    //                                                                Single Logout Service
    //                                                                =====================

    /** The session attribute a test watches to tell an invalidated session from a kept one. */
    private static final String SESSION_MARKER = "SLO_TEST_MARKER";

    /** IdP settings that also make the single logout service reachable. */
    private void setUpSlo(final DynamicProperties systemProperties) {
        setUpIdp(systemProperties);
        systemProperties.setProperty("saml.idp.single_logout_service.url", "https://idp.example.com/slo");
    }

    private void tearDownSlo(final DynamicProperties systemProperties) {
        systemProperties.remove("saml.idp.single_logout_service.url");
        tearDownIdp(systemProperties);
    }

    /**
     * Builds an authenticator that reports the given user as logged in. The real
     * {@code FessLoginAssist} cannot be resolved here because it injects the user index, which is
     * exactly why reading the session user sits behind an overridable method.
     */
    private SamlAuthenticator createAuthenticatorLoggedInAs(final OptionalThing<FessUserBean> userBean) throws Exception {
        final SamlAuthenticator authenticator = new SamlAuthenticator() {
            @Override
            protected OptionalThing<FessUserBean> getSavedUserBean() {
                return userBean;
            }
        };
        final Field field = SamlAuthenticator.class.getDeclaredField("defaultSettings");
        field.setAccessible(true);
        field.set(authenticator, authenticator.createDefaultSettings());
        return authenticator;
    }

    /** The session bean a SAML login leaves behind; SamlUser.getName() is the NameID. */
    private OptionalThing<FessUserBean> samlUserBean(final String nameId) {
        return OptionalThing.of(new FessUserBean(new SamlUser(nameId, "_sessionIndex", null, null, null, new String[0], new String[0])));
    }

    /**
     * Puts an IdP-initiated LogoutRequest on the request, shaped the way one that nobody
     * authenticated looks: no signature, and neither {@code NotOnOrAfter} nor {@code Destination},
     * both of which java-saml checks only when the attribute is present.
     *
     * @param request The request the IdP is pretending to send.
     * @param id The LogoutRequest ID, which the replay cache keys on.
     * @param nameId The NameID the LogoutRequest asks to log out.
     */
    private void sendLogoutRequest(final MockletHttpServletRequest request, final String id, final String nameId) {
        final String xml = "<samlp:LogoutRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\""
                + " xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"" + id + "\" Version=\"2.0\""
                + " IssueInstant=\"2026-01-01T00:00:00Z\">" + "<saml:Issuer>https://idp.example.com/metadata</saml:Issuer>"
                + "<saml:NameID>" + nameId + "</saml:NameID></samlp:LogoutRequest>";
        request.setParameter("SAMLRequest", Base64.getEncoder().encodeToString(xml.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void test_getLogoutResponse_keepsTheSessionWhenTheLogoutRequestNamesAnotherUser() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticatorLoggedInAs(samlUserBean("victim@example.com"));
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpSlo(systemProperties);
            // /sso/logout is anonymous and SAML forces SameSite=none, so this request reaches the
            // endpoint cross-site with the victim's session cookie on it
            final MockletHttpServletRequest request = getMockRequest();
            request.getSession().setAttribute(SESSION_MARKER, "kept");
            sendLogoutRequest(request, "_crafted", "attacker@example.com");
            // primed so that the insecure-settings warning is not one of the lines asserted below
            authenticator.getSettings();

            final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
            try {
                final ActionResponse response = authenticator.getResponse(SsoResponseType.LOGOUT);

                assertEquals("kept", request.getSession(false).getAttribute(SESSION_MARKER));
                // the IdP still gets an ordinary LogoutResponse: an error would tell the sender
                // whether it guessed a live session, and would strand a confused-but-real IdP
                assertTrue(String.valueOf(response), String.valueOf(response).contains("https://idp.example.com/slo?SAMLResponse="));
                assertEquals(1, appender.warnings().size(), String.valueOf(appender.warnings()));
                assertTrue(appender.warnings().get(0), appender.warnings().get(0).contains("attacker@example.com"));
                assertTrue(appender.warnings().get(0), appender.warnings().get(0).contains("victim@example.com"));
            } finally {
                appender.detach();
            }
        } finally {
            tearDownSlo(systemProperties);
        }
    }

    @Test
    public void test_getLogoutResponse_endsTheSessionWhenTheLogoutRequestNamesTheSessionUser() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticatorLoggedInAs(samlUserBean("victim@example.com"));
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpSlo(systemProperties);
            final MockletHttpServletRequest request = getMockRequest();
            request.getSession().setAttribute(SESSION_MARKER, "kept");
            sendLogoutRequest(request, "_slo", "victim@example.com");
            authenticator.getSettings();

            final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
            try {
                final ActionResponse response = authenticator.getResponse(SsoResponseType.LOGOUT);

                // the whole point of single logout: the IdP says so, so the session ends
                assertNull(request.getSession(false).getAttribute(SESSION_MARKER), "the session must not survive its own logout");
                assertTrue(String.valueOf(response), String.valueOf(response).contains("https://idp.example.com/slo?SAMLResponse="));
                assertTrue(String.valueOf(appender.warnings()), appender.warnings().isEmpty());
            } finally {
                appender.detach();
            }
        } finally {
            tearDownSlo(systemProperties);
        }
    }

    @Test
    public void test_getLogoutResponse_endsTheSessionWhenTheNameIdDiffersOnlyInFormatting() throws Exception {
        // The two NameIDs are read from the text content of two different XML documents, and
        // java-saml trims neither unless saml.parsing.trim_name_ids is turned on, which Fess
        // leaves off. An IdP that pretty-prints its LogoutRequest but not its assertion, or that
        // normalises the case of a UPN in one and not the other, must not end up unable to log
        // anyone out -- that failure is silent and looks like the session refusing to die.
        final SamlAuthenticator authenticator = createAuthenticatorLoggedInAs(samlUserBean("Victim@Example.com"));
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpSlo(systemProperties);
            final MockletHttpServletRequest request = getMockRequest();
            request.getSession().setAttribute(SESSION_MARKER, "kept");
            sendLogoutRequest(request, "_formatted", "\n            victim@example.com\n        ");
            authenticator.getSettings();

            final LogCapturingAppender appender = LogCapturingAppender.attach(SamlAuthenticator.class);
            try {
                authenticator.getResponse(SsoResponseType.LOGOUT);

                assertNull(request.getSession(false).getAttribute(SESSION_MARKER), "a reformatted NameID is still the same user");
                assertTrue(String.valueOf(appender.warnings()), appender.warnings().isEmpty());
            } finally {
                appender.detach();
            }
        } finally {
            tearDownSlo(systemProperties);
        }
    }

    @Test
    public void test_getLogoutResponse_endsTheSessionWhenNobodyIsLoggedIn() throws Exception {
        // With no session user there is no NameID to compare against, so the LogoutRequest keeps
        // the effect it always had rather than being refused on a comparison that cannot be made.
        final SamlAuthenticator authenticator = createAuthenticatorLoggedInAs(OptionalThing.empty());
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpSlo(systemProperties);
            final MockletHttpServletRequest request = getMockRequest();
            request.getSession().setAttribute(SESSION_MARKER, "kept");
            sendLogoutRequest(request, "_anonymous", "someone@example.com");

            final ActionResponse response = authenticator.getResponse(SsoResponseType.LOGOUT);

            assertNull(request.getSession(false).getAttribute(SESSION_MARKER), "the session must still be invalidated");
            assertTrue(String.valueOf(response), String.valueOf(response).contains("https://idp.example.com/slo?SAMLResponse="));
        } finally {
            tearDownSlo(systemProperties);
        }
    }

    @Test
    public void test_getLogoutResponse_keepsTheSessionWhenTheLogoutRequestNamesNobody() throws Exception {
        // java-saml insists on the <saml:NameID> element being there but never on it carrying
        // anything, so an empty one parses and names nobody. Treating that as "cannot tell" would
        // hand back the whole bypass: the sender picks the value, and an empty element costs it
        // nothing, so every session would be one crafted URL away from ending again.
        for (final String nameId : new String[] { "", "   ", "\n" }) {
            final SamlAuthenticator authenticator = createAuthenticatorLoggedInAs(samlUserBean("victim@example.com"));
            final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
            try {
                setUpSlo(systemProperties);
                final MockletHttpServletRequest request = getMockRequest();
                request.getSession().setAttribute(SESSION_MARKER, "kept");
                sendLogoutRequest(request, "_empty" + nameId.length(), nameId);
                authenticator.getSettings();

                final ActionResponse response = authenticator.getResponse(SsoResponseType.LOGOUT);

                assertEquals("a LogoutRequest that names nobody must not end a session", "kept",
                        request.getSession(false).getAttribute(SESSION_MARKER));
                // the IdP still gets an ordinary LogoutResponse: an error would tell an
                // unauthenticated sender whether it guessed a live session
                assertTrue(String.valueOf(response), String.valueOf(response).contains("https://idp.example.com/slo?SAMLResponse="));
            } finally {
                tearDownSlo(systemProperties);
            }
        }
    }

    @Test
    public void test_getLogoutRequestNameId_tellsAnEmptyNameIdApartFromAnUnreadableOne() throws Exception {
        // isLogoutRequestForAnotherUser branches on null, not on blank, so the two have to stay
        // distinguishable here: null means java-saml is about to fail on the same bytes, while ""
        // means the message parsed and simply named nobody.
        final SamlAuthenticator authenticator = createAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpSlo(systemProperties);
            final Saml2Settings settings = authenticator.getSettings();

            final MockletHttpServletRequest empty = getMockRequest();
            sendLogoutRequest(empty, "_emptyname", "");
            assertEquals("", authenticator.getLogoutRequestNameId(empty, settings));

            final MockletHttpServletRequest notXml = getMockRequest();
            notXml.setParameter("SAMLRequest", "................");
            assertNull(authenticator.getLogoutRequestNameId(notXml, settings));
        } finally {
            tearDownSlo(systemProperties);
        }
    }

    @Test
    public void test_isLogoutRequestForAnotherUser_leavesALogoutResponseAlone() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticatorLoggedInAs(samlUserBean("victim@example.com"));
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpSlo(systemProperties);
            // the IdP answering a LogoutRequest this SP sent. Handing such a request to the
            // LogoutRequest parser would not parse anything: with no SAMLRequest parameter it
            // builds a fresh outgoing message instead, whose NameID defaults to the IdP entity ID
            // and therefore never matches the session user, leaving every SP-initiated logout with
            // a session that refuses to end.
            final MockletHttpServletRequest request = getMockRequest();
            request.setParameter("SAMLResponse", "PHNhbWxwOkxvZ291dFJlc3BvbnNlIC8+");

            assertFalse(authenticator.isLogoutRequestForAnotherUser(request, authenticator.getSettings()));
        } finally {
            tearDownSlo(systemProperties);
        }
    }

    @Test
    public void test_getLogoutRequestNameId_returnsNullWhenTheNameIdCannotBeRead() throws Exception {
        // A NameID this check cannot read must mean "cannot tell", which leaves the previous
        // behaviour in place; throwing here would turn an unreadable message into a broken logout.
        final SamlAuthenticator authenticator = createAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpSlo(systemProperties);
            final Saml2Settings settings = authenticator.getSettings();

            final MockletHttpServletRequest wellFormed = getMockRequest();
            sendLogoutRequest(wellFormed, "_readable", "victim@example.com");
            assertEquals("victim@example.com", authenticator.getLogoutRequestNameId(wellFormed, settings));

            final MockletHttpServletRequest notXml = getMockRequest();
            notXml.setParameter("SAMLRequest", "................");
            assertNull(authenticator.getLogoutRequestNameId(notXml, settings), "an undecodable message has no NameID");

            final MockletHttpServletRequest withoutNameId = getMockRequest();
            final String xml = "<samlp:LogoutRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\""
                    + " xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"_bare\" Version=\"2.0\""
                    + " IssueInstant=\"2026-01-01T00:00:00Z\"/>";
            withoutNameId.setParameter("SAMLRequest", Base64.getEncoder().encodeToString(xml.getBytes(StandardCharsets.UTF_8)));
            assertNull(authenticator.getLogoutRequestNameId(withoutNameId, settings), "java-saml rejects this one on its own later");
        } finally {
            tearDownSlo(systemProperties);
        }
    }

    @Test
    public void test_getLogoutRequestNameId_parsesTheMessageOnce() throws Exception {
        // A message that does not parse is not free to look at: java-saml answers null and logs
        // the failure with its stack trace, so every parse of one writes about ninety lines. The
        // endpoint is anonymous and, because SAML requires SameSite=none, reachable cross-site
        // with a victim's cookie attached, which is the only case in which this method runs at
        // all -- so a second parse would land on precisely the sessions an attacker aims at.
        final SamlAuthenticator authenticator = createAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        final LogCapturingAppender library = LogCapturingAppender.attach("org.codelibs.saml2.core.util.Util");
        try {
            setUpSlo(systemProperties);
            final Saml2Settings settings = authenticator.getSettings();

            final MockletHttpServletRequest notXml = getMockRequest();
            notXml.setParameter("SAMLRequest", "................");

            assertNull(authenticator.getLogoutRequestNameId(notXml, settings));
            // one failed parse, one warning: reaching the XML through a LogoutRequest instead
            // parses it a second time and doubles this
            assertEquals(1, library.warnings().size());
        } finally {
            library.detach();
            tearDownSlo(systemProperties);
        }
    }

    @Test
    public void test_getSessionSamlNameId_ignoresAUserThatDidNotComeFromSaml() throws Exception {
        // A local or LDAP login carries a user name, not a NameID, and comparing the two would
        // reject every legitimate single logout on a mixed-authentication deployment.
        assertEquals("victim@example.com", createAuthenticatorLoggedInAs(samlUserBean("victim@example.com")).getSessionSamlNameId());
        assertNull(createAuthenticatorLoggedInAs(OptionalThing.of(new FessUserBean(new LocalUser("victim@example.com"))))
                .getSessionSamlNameId(), "a non-SAML user has no NameID to compare");
        assertNull(createAuthenticatorLoggedInAs(OptionalThing.empty()).getSessionSamlNameId(), "nobody is logged in");
    }

    @Test
    public void test_isSameNameId_toleratesFormattingButNotADifferentUser() throws Exception {
        final SamlAuthenticator authenticator = new SamlAuthenticator();

        // the two sides come from different XML documents, which the IdP may format differently
        assertTrue(authenticator.isSameNameId("victim@example.com", "  victim@example.com\n"));
        // an IdP that normalises the case of a UPN in one message and not the other is a real
        // deployment; a sender that does not know the NameID fails whatever case it picks
        assertTrue(authenticator.isSameNameId("Victim@Example.com", "victim@example.com"));
        assertFalse(authenticator.isSameNameId("victim@example.com", "attacker@example.com"));
        // a NameID that names nobody is not the session user either, whatever it is padded with
        assertFalse(authenticator.isSameNameId("victim@example.com", ""));
        assertFalse(authenticator.isSameNameId("victim@example.com", "   "));
        assertFalse(authenticator.isSameNameId("victim@example.com", "\n"));
    }

    @Test
    public void test_sanitizeForLog_keepsAnUnauthenticatedNameIdFromForgingLogLines() throws Exception {
        // the NameID of the LogoutRequest is written to the log before anything has authenticated
        // the message, and it is XML text content, so it can carry a line break
        assertEquals("victim@example.com? ERROR forged", SamlAuthenticator.sanitizeForLog("victim@example.com\n ERROR forged"));
        assertEquals("victim@example.com? ERROR forged", SamlAuthenticator.sanitizeForLog("victim@example.com\r ERROR forged"));
        // \p{Cntrl} is ASCII-only, so the Unicode break characters a log viewer still renders as
        // a new line have to be covered separately
        assertEquals("a?b?c", SamlAuthenticator.sanitizeForLog("a\u0085b\u2028c"));

        final int max = SamlAuthenticator.MAX_LOGGED_NAME_ID_LENGTH;
        assertEquals("x".repeat(max) + "...", SamlAuthenticator.sanitizeForLog("x".repeat(max + 10)));
        // an ordinary NameID is passed through untouched
        assertEquals("victim@example.com", SamlAuthenticator.sanitizeForLog("victim@example.com"));

        // a rejection reason quotes the message it objected to, so it is sender-supplied in the
        // same way and gets the same treatment at a bound that leaves the sentence readable
        final int reasonMax = SamlAuthenticator.MAX_LOGGED_FAILURE_REASON_LENGTH;
        assertTrue(String.valueOf(reasonMax), reasonMax > max);
        assertEquals("Invalid issuer in the Assertion/Response. Was '?ERROR forged'",
                SamlAuthenticator.sanitizeForLog("Invalid issuer in the Assertion/Response. Was '\nERROR forged'", reasonMax));
        assertEquals("y".repeat(reasonMax) + "...", SamlAuthenticator.sanitizeForLog("y".repeat(reasonMax + 1), reasonMax));
    }

    @Test
    public void test_buildDefaultUrl_withDefaultBaseUrl() throws Exception {
        assertEquals("http://localhost:8080/sso/metadata", new SamlAuthenticator().buildDefaultUrl("/sso/metadata"));
    }

    @Test
    public void test_buildDefaultUrl_withCustomBaseUrl() throws Exception {
        final SamlAuthenticator authenticator = new SamlAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            systemProperties.setProperty(BASE_URL_KEY, "https://fess.example.com:8443");

            assertEquals("https://fess.example.com:8443/sso/metadata", authenticator.buildDefaultUrl("/sso/metadata"));
        } finally {
            systemProperties.remove(BASE_URL_KEY);
        }
    }

    @Test
    public void test_buildDefaultUrl_withTrailingSlash() throws Exception {
        final SamlAuthenticator authenticator = new SamlAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            systemProperties.setProperty(BASE_URL_KEY, "https://fess.example.com/");

            assertEquals("https://fess.example.com/sso/", authenticator.buildDefaultUrl("/sso/"));
        } finally {
            systemProperties.remove(BASE_URL_KEY);
        }
    }

    @Test
    public void test_buildDefaultUrl_withBlankProperty() throws Exception {
        final SamlAuthenticator authenticator = new SamlAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            systemProperties.setProperty(BASE_URL_KEY, "   ");

            assertEquals("http://localhost:8080/sso/logout", authenticator.buildDefaultUrl("/sso/logout"));
        } finally {
            systemProperties.remove(BASE_URL_KEY);
        }
    }

    /** A user that did not authenticate through SAML, as a local or LDAP login leaves behind. */
    private static class LocalUser implements FessUser {

        private static final long serialVersionUID = 1L;

        private final String name;

        LocalUser(final String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String[] getRoleNames() {
            return new String[0];
        }

        @Override
        public String[] getGroupNames() {
            return new String[0];
        }

        @Override
        public String[] getPermissions() {
            return new String[0];
        }
    }
}
