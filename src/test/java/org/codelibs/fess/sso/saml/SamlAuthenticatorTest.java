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
import org.codelibs.fess.exception.SsoMessageException;
import org.codelibs.fess.sso.SsoResponseType;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.saml2.core.settings.Saml2Settings;
import org.junit.jupiter.api.Test;

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
        try {
            authenticator.getResponse(SsoResponseType.LOGOUT);
            fail("SsoMessageException should be thrown");
        } catch (final SsoMessageException e) {
            assertNotNull(e.getCause());
            assertTrue(e.getCause().getMessage(), e.getCause().getMessage().contains("single logout service URL"));
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
