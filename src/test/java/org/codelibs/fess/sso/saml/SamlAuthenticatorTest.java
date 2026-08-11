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

import java.lang.reflect.Field;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.app.web.base.login.ActionResponseCredential;
import org.codelibs.fess.exception.SsoMessageException;
import org.codelibs.fess.exception.SsoStateException;
import org.codelibs.fess.sso.SsoResponseType;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.saml2.core.settings.Saml2Settings;
import org.dbflute.utflute.mocklet.MockletHttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.StreamResponse;

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

        final Saml2Settings first = authenticator.getSettings();
        final Saml2Settings second = authenticator.getSettings();

        assertNotNull(first.getReplayCache());
        assertSame(first.getReplayCache(), second.getReplayCache());
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
        } finally {
            tearDownIdp(systemProperties);
            appender.detach();
        }
    }

    @Test
    public void test_getLoginCredential_requestWithoutResponseStartsLogin() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticator();
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
    public void test_getLoginCredential_requestWithoutResponseReplacesPendingRequestId() throws Exception {
        final SamlAuthenticator authenticator = createAuthenticator();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            setUpIdp(systemProperties);
            // a plain visit to /sso/ sends a fresh AuthnRequest and rebinds the session to its
            // ID, so a login already in flight is abandoned: only one AuthnRequest per session
            // can be answered, and the response to the older one is rejected as unmatched
            final MockletHttpServletRequest request = getMockRequest();
            request.getSession().setAttribute("SAML_STATE", "ONELOGIN_pending");

            authenticator.getLoginCredential();

            final Object requestId = request.getSession(false).getAttribute("SAML_STATE");
            assertNotNull(requestId);
            Assertions.assertNotEquals("ONELOGIN_pending", requestId);
            assertTrue(String.valueOf(requestId), String.valueOf(requestId).startsWith("ONELOGIN_"));
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
}
