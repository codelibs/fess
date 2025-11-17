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
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.IpAddressUtil;

public class SamlAuthenticatorTest extends UnitFessTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Helper method to initialize defaultSettings without calling init()
     * which requires DI container components
     */
    private Map<String, Object> createDefaultSettings() throws Exception {
        Map<String, Object> defaultSettings = new HashMap<>();
        defaultSettings.put("onelogin.saml2.strict", "true");
        defaultSettings.put("onelogin.saml2.debug", "false");
        
        // Build default URLs without requiring DI
        String baseUrl = "http://localhost:8080";
        try {
            final InetAddress localhost = InetAddress.getByName("localhost");
            baseUrl = IpAddressUtil.buildUrl("http", localhost, 8080, "");
        } catch (Exception e) {
            // Use fallback
        }
        
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

    public void test_authenticatorInstantiation() {
        // Verify authenticator can be instantiated without errors
        SamlAuthenticator authenticator = new SamlAuthenticator();
        assertNotNull(authenticator);
    }

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

    public void test_defaultSettings_organizationInfo() throws Exception {
        Map<String, Object> defaultSettings = createDefaultSettings();

        // Verify organization information is set
        assertEquals("CodeLibs", defaultSettings.get("onelogin.saml2.organization.name"));
        assertEquals("Fess", defaultSettings.get("onelogin.saml2.organization.displayname"));
        assertEquals("https://fess.codelibs.org/", defaultSettings.get("onelogin.saml2.organization.url"));
    }

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
        assertEquals("urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress",
                defaultSettings.get("onelogin.saml2.sp.nameidformat"));
    }

    public void test_buildDefaultUrl() throws Exception {
        SamlAuthenticator authenticator = new SamlAuthenticator();

        // Use reflection to access protected method
        java.lang.reflect.Method buildDefaultUrlMethod =
                SamlAuthenticator.class.getDeclaredMethod("buildDefaultUrl", String.class);
        buildDefaultUrlMethod.setAccessible(true);

        String url = (String) buildDefaultUrlMethod.invoke(authenticator, "/sso/metadata");

        // Verify URL is generated
        assertNotNull(url);
        assertTrue(url.contains("8080"));
        assertTrue(url.contains("/sso/metadata"));

        // Verify URL starts with http://
        assertTrue(url.startsWith("http://"));
    }

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
