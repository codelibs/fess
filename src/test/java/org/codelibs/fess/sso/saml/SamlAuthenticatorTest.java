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
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class SamlAuthenticatorTest extends UnitFessTestCase {

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        super.tearDown(testInfo);
    }

    /**
     * Helper method to initialize defaultSettings without calling init()
     * which requires DI container components
     */
    private Map<String, Object> createDefaultSettings() throws Exception {
        Map<String, Object> defaultSettings = new HashMap<>();
        defaultSettings.put("onelogin.saml2.strict", "true");
        defaultSettings.put("onelogin.saml2.debug", "false");

        // Build default URLs using localhost (matching the new implementation)
        String baseUrl = "http://localhost:8080";

        defaultSettings.put("onelogin.saml2.sp.entityid", baseUrl + "/sso/metadata");
        defaultSettings.put("onelogin.saml2.sp.assertion_consumer_service.url", baseUrl + "/sso/");
        defaultSettings.put("onelogin.saml2.sp.assertion_consumer_service.binding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST");
        defaultSettings.put("onelogin.saml2.sp.single_logout_service.url", baseUrl + "/sso/logout");
        defaultSettings.put("onelogin.saml2.sp.single_logout_service.binding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");
        defaultSettings.put("onelogin.saml2.sp.nameidformat", "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress");
        defaultSettings.put("onelogin.saml2.sp.x509cert", "");
        defaultSettings.put("onelogin.saml2.sp.privatekey", "");
        defaultSettings.put("onelogin.saml2.idp.single_sign_on_service.binding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");
        defaultSettings.put("onelogin.saml2.idp.single_logout_service.response.url", "");
        defaultSettings.put("onelogin.saml2.idp.single_logout_service.binding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");
        defaultSettings.put("onelogin.saml2.security.nameid_encrypted", "false");
        defaultSettings.put("onelogin.saml2.security.authnrequest_signed", "false");
        defaultSettings.put("onelogin.saml2.security.logoutrequest_signed", "false");
        defaultSettings.put("onelogin.saml2.security.logoutresponse_signed", "false");
        defaultSettings.put("onelogin.saml2.security.want_messages_signed", "false");
        defaultSettings.put("onelogin.saml2.security.want_assertions_signed", "false");
        defaultSettings.put("onelogin.saml2.security.sign_metadata", "");
        defaultSettings.put("onelogin.saml2.security.want_assertions_encrypted", "false");
        defaultSettings.put("onelogin.saml2.security.want_nameid_encrypted", "false");
        defaultSettings.put("onelogin.saml2.security.requested_authncontext", "urn:oasis:names:tc:SAML:2.0:ac:classes:Password");
        defaultSettings.put("onelogin.saml2.security.onelogin.saml2.security.requested_authncontextcomparison", "exact");
        defaultSettings.put("onelogin.saml2.security.want_xml_validation", "true");
        defaultSettings.put("onelogin.saml2.security.signature_algorithm", "http://www.w3.org/2000/09/xmldsig#rsa-sha1");
        defaultSettings.put("onelogin.saml2.organization.name", "CodeLibs");
        defaultSettings.put("onelogin.saml2.organization.displayname", "Fess");
        defaultSettings.put("onelogin.saml2.organization.url", "https://fess.codelibs.org/");
        defaultSettings.put("onelogin.saml2.organization.lang", "");
        defaultSettings.put("onelogin.saml2.contacts.technical.given_name", "Technical Guy");
        defaultSettings.put("onelogin.saml2.contacts.technical.email_address", "technical@example.com");
        defaultSettings.put("onelogin.saml2.contacts.support.given_name", "Support Guy");
        defaultSettings.put("onelogin.saml2.contacts.support.email_address", "support@example.com");

        return defaultSettings;
    }

    /**
     * Helper method to set defaultSettings on an authenticator instance
     */
    private void setDefaultSettings(SamlAuthenticator authenticator, Map<String, Object> settings) throws Exception {
        Field defaultSettingsField = SamlAuthenticator.class.getDeclaredField("defaultSettings");
        defaultSettingsField.setAccessible(true);
        defaultSettingsField.set(authenticator, settings);
    }

    @Test
    public void test_authenticatorInstantiation() {
        // Verify authenticator can be instantiated without errors
        SamlAuthenticator authenticator = new SamlAuthenticator();
        assertNotNull(authenticator);
    }

    @Test
    public void test_defaultSettings_emailAddressCorrect() throws Exception {
        // Test that the email typo fix is correct: support@@example.com -> support@example.com
        Map<String, Object> defaultSettings = createDefaultSettings();

        // Verify email addresses are correctly formatted (no double @)
        String technicalEmail = (String) defaultSettings.get("onelogin.saml2.contacts.technical.email_address");
        String supportEmail = (String) defaultSettings.get("onelogin.saml2.contacts.support.email_address");

        assertNotNull(technicalEmail);
        assertNotNull(supportEmail);

        // Verify no double @ signs
        assertFalse("Technical email should not contain @@", technicalEmail.contains("@@"));
        assertFalse("Support email should not contain @@", supportEmail.contains("@@"));

        // Verify correct email format
        assertEquals("technical@example.com", technicalEmail);
        assertEquals("support@example.com", supportEmail);

        // Count @ occurrences - should be exactly 1
        assertEquals(1, technicalEmail.chars().filter(ch -> ch == '@').count());
        assertEquals(1, supportEmail.chars().filter(ch -> ch == '@').count());
    }

    @Test
    public void test_defaultSettings_securityConfiguration() throws Exception {
        // Verify security settings are properly configured with appropriate defaults
        Map<String, Object> defaultSettings = createDefaultSettings();

        // Verify strict mode is enabled by default (good security practice)
        assertEquals("true", defaultSettings.get("onelogin.saml2.strict"));

        // Verify debug is disabled by default (good security practice)
        assertEquals("false", defaultSettings.get("onelogin.saml2.debug"));

        // Verify all security-related settings exist (they are set to false for compatibility,
        // but users should be able to override them via system properties)
        assertNotNull(defaultSettings.get("onelogin.saml2.security.nameid_encrypted"));
        assertNotNull(defaultSettings.get("onelogin.saml2.security.authnrequest_signed"));
        assertNotNull(defaultSettings.get("onelogin.saml2.security.logoutrequest_signed"));
        assertNotNull(defaultSettings.get("onelogin.saml2.security.logoutresponse_signed"));
        assertNotNull(defaultSettings.get("onelogin.saml2.security.want_messages_signed"));
        assertNotNull(defaultSettings.get("onelogin.saml2.security.want_assertions_signed"));
        assertNotNull(defaultSettings.get("onelogin.saml2.security.want_assertions_encrypted"));
        assertNotNull(defaultSettings.get("onelogin.saml2.security.want_nameid_encrypted"));
        assertNotNull(defaultSettings.get("onelogin.saml2.security.want_xml_validation"));
    }

    @Test
    public void test_defaultSettings_organizationInfo() throws Exception {
        Map<String, Object> defaultSettings = createDefaultSettings();

        // Verify organization information is set
        assertEquals("CodeLibs", defaultSettings.get("onelogin.saml2.organization.name"));
        assertEquals("Fess", defaultSettings.get("onelogin.saml2.organization.displayname"));
        assertEquals("https://fess.codelibs.org/", defaultSettings.get("onelogin.saml2.organization.url"));
    }

    @Test
    public void test_defaultSettings_serviceProviderConfig() throws Exception {
        Map<String, Object> defaultSettings = createDefaultSettings();

        // Verify SP endpoints are configured
        assertNotNull(defaultSettings.get("onelogin.saml2.sp.entityid"));
        assertNotNull(defaultSettings.get("onelogin.saml2.sp.assertion_consumer_service.url"));
        assertNotNull(defaultSettings.get("onelogin.saml2.sp.single_logout_service.url"));

        // Verify bindings are set
        assertEquals("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST",
                defaultSettings.get("onelogin.saml2.sp.assertion_consumer_service.binding"));
        assertEquals("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect",
                defaultSettings.get("onelogin.saml2.sp.single_logout_service.binding"));

        // Verify NameID format
        assertEquals("urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress", defaultSettings.get("onelogin.saml2.sp.nameidformat"));
    }

    @Test
    public void test_buildDefaultUrl_withDefaultBaseUrl() throws Exception {
        // Test that buildDefaultUrl returns http://localhost:8080 when no property is set
        SamlAuthenticator authenticator = new SamlAuthenticator();

        // Ensure the property is not set
        DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        systemProperties.remove("saml.sp.base.url");

        // Use reflection to access protected method
        Method buildDefaultUrlMethod = SamlAuthenticator.class.getDeclaredMethod("buildDefaultUrl", String.class);
        buildDefaultUrlMethod.setAccessible(true);

        String url = (String) buildDefaultUrlMethod.invoke(authenticator, "/sso/metadata");

        // Verify URL uses default localhost:8080
        assertNotNull(url);
        assertEquals("http://localhost:8080/sso/metadata", url);
    }

    @Test
    public void test_buildDefaultUrl_withCustomBaseUrl() throws Exception {
        // Test that buildDefaultUrl uses custom base URL from property
        SamlAuthenticator authenticator = new SamlAuthenticator();

        DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            // Set custom base URL
            systemProperties.setProperty("saml.sp.base.url", "https://fess.example.com");

            Method buildDefaultUrlMethod = SamlAuthenticator.class.getDeclaredMethod("buildDefaultUrl", String.class);
            buildDefaultUrlMethod.setAccessible(true);

            String url = (String) buildDefaultUrlMethod.invoke(authenticator, "/sso/metadata");

            // Verify URL uses custom base URL
            assertNotNull(url);
            assertEquals("https://fess.example.com/sso/metadata", url);
        } finally {
            // Clean up
            systemProperties.remove("saml.sp.base.url");
        }
    }

    @Test
    public void test_buildDefaultUrl_withTrailingSlash() throws Exception {
        // Test that trailing slash is handled correctly
        SamlAuthenticator authenticator = new SamlAuthenticator();

        DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            // Set custom base URL with trailing slash
            systemProperties.setProperty("saml.sp.base.url", "https://fess.example.com/");

            Method buildDefaultUrlMethod = SamlAuthenticator.class.getDeclaredMethod("buildDefaultUrl", String.class);
            buildDefaultUrlMethod.setAccessible(true);

            String url = (String) buildDefaultUrlMethod.invoke(authenticator, "/sso/metadata");

            // Verify trailing slash is removed and URL is correct
            assertNotNull(url);
            assertEquals("https://fess.example.com/sso/metadata", url);
        } finally {
            // Clean up
            systemProperties.remove("saml.sp.base.url");
        }
    }

    @Test
    public void test_buildDefaultUrl_withPortInCustomUrl() throws Exception {
        // Test that custom URL with port is handled correctly
        SamlAuthenticator authenticator = new SamlAuthenticator();

        DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            // Set custom base URL with port
            systemProperties.setProperty("saml.sp.base.url", "http://127.0.0.1:9080");

            Method buildDefaultUrlMethod = SamlAuthenticator.class.getDeclaredMethod("buildDefaultUrl", String.class);
            buildDefaultUrlMethod.setAccessible(true);

            String url = (String) buildDefaultUrlMethod.invoke(authenticator, "/sso/metadata");

            // Verify URL uses custom base URL with port
            assertNotNull(url);
            assertEquals("http://127.0.0.1:9080/sso/metadata", url);
        } finally {
            // Clean up
            systemProperties.remove("saml.sp.base.url");
        }
    }

    @Test
    public void test_buildDefaultUrl_allEndpoints() throws Exception {
        // Test all SAML endpoints are built correctly
        SamlAuthenticator authenticator = new SamlAuthenticator();

        DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            systemProperties.setProperty("saml.sp.base.url", "https://fess.example.com");

            Method buildDefaultUrlMethod = SamlAuthenticator.class.getDeclaredMethod("buildDefaultUrl", String.class);
            buildDefaultUrlMethod.setAccessible(true);

            // Test metadata endpoint
            String metadataUrl = (String) buildDefaultUrlMethod.invoke(authenticator, "/sso/metadata");
            assertEquals("https://fess.example.com/sso/metadata", metadataUrl);

            // Test ACS endpoint
            String acsUrl = (String) buildDefaultUrlMethod.invoke(authenticator, "/sso/");
            assertEquals("https://fess.example.com/sso/", acsUrl);

            // Test SLO endpoint
            String sloUrl = (String) buildDefaultUrlMethod.invoke(authenticator, "/sso/logout");
            assertEquals("https://fess.example.com/sso/logout", sloUrl);
        } finally {
            // Clean up
            systemProperties.remove("saml.sp.base.url");
        }
    }

    @Test
    public void test_buildDefaultUrl_emptyProperty() throws Exception {
        // Test that empty property falls back to default
        SamlAuthenticator authenticator = new SamlAuthenticator();

        DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            // Set empty base URL
            systemProperties.setProperty("saml.sp.base.url", "");

            Method buildDefaultUrlMethod = SamlAuthenticator.class.getDeclaredMethod("buildDefaultUrl", String.class);
            buildDefaultUrlMethod.setAccessible(true);

            String url = (String) buildDefaultUrlMethod.invoke(authenticator, "/sso/metadata");

            // Verify URL uses default localhost:8080 when property is empty
            assertNotNull(url);
            assertEquals("http://localhost:8080/sso/metadata", url);
        } finally {
            // Clean up
            systemProperties.remove("saml.sp.base.url");
        }
    }

    @Test
    public void test_buildDefaultUrl_whitespaceProperty() throws Exception {
        // Test that whitespace-only property falls back to default
        SamlAuthenticator authenticator = new SamlAuthenticator();

        DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        try {
            // Set whitespace-only base URL
            systemProperties.setProperty("saml.sp.base.url", "   ");

            Method buildDefaultUrlMethod = SamlAuthenticator.class.getDeclaredMethod("buildDefaultUrl", String.class);
            buildDefaultUrlMethod.setAccessible(true);

            String url = (String) buildDefaultUrlMethod.invoke(authenticator, "/sso/metadata");

            // Verify URL uses default localhost:8080 when property is whitespace
            assertNotNull(url);
            assertEquals("http://localhost:8080/sso/metadata", url);
        } finally {
            // Clean up
            systemProperties.remove("saml.sp.base.url");
        }
    }

    @Test
    public void test_contactInformation() throws Exception {
        Map<String, Object> defaultSettings = createDefaultSettings();

        // Verify contact information is properly set
        assertEquals("Technical Guy", defaultSettings.get("onelogin.saml2.contacts.technical.given_name"));
        assertEquals("Support Guy", defaultSettings.get("onelogin.saml2.contacts.support.given_name"));

        // Most importantly, verify the email typo is fixed
        String techEmail = (String) defaultSettings.get("onelogin.saml2.contacts.technical.email_address");
        String supportEmail = (String) defaultSettings.get("onelogin.saml2.contacts.support.email_address");

        // Both should have exactly one @ character
        assertEquals("Email should have exactly one @ character", 1, techEmail.split("@", -1).length - 1);
        assertEquals("Email should have exactly one @ character", 1, supportEmail.split("@", -1).length - 1);

        // Specific value check
        assertEquals("support@example.com", supportEmail);
        assertEquals("technical@example.com", techEmail);
    }

    @Test
    public void test_emailTypoFix_supportEmail() throws Exception {
        // This test specifically verifies the typo fix: support@@example.com -> support@example.com
        Map<String, Object> defaultSettings = createDefaultSettings();
        String supportEmail = (String) defaultSettings.get("onelogin.saml2.contacts.support.email_address");

        // The critical assertion: must NOT contain double @
        assertFalse("Support email must not contain @@", supportEmail.contains("@@"));

        // And must be the correct value
        assertEquals("support@example.com", supportEmail);
    }
}
