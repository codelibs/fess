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

import org.codelibs.fess.unit.UnitFessTestCase;

public class SamlAuthenticatorTest extends UnitFessTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void test_authenticatorInstantiation() {
        // Verify authenticator can be instantiated without errors
        SamlAuthenticator authenticator = new SamlAuthenticator();
        assertNotNull(authenticator);
    }

    public void test_defaultSettings_emailAddressCorrect() throws Exception {
        // Test that the email typo fix is correct: support@@example.com -> support@example.com
        SamlAuthenticator authenticator = new SamlAuthenticator();
        authenticator.init();

        // Use reflection to access defaultSettings
        Field defaultSettingsField = SamlAuthenticator.class.getDeclaredField("defaultSettings");
        defaultSettingsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Object> defaultSettings = (Map<String, Object>) defaultSettingsField.get(authenticator);

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
        SamlAuthenticator authenticator = new SamlAuthenticator();
        authenticator.init();

        Field defaultSettingsField = SamlAuthenticator.class.getDeclaredField("defaultSettings");
        defaultSettingsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Object> defaultSettings = (Map<String, Object>) defaultSettingsField.get(authenticator);

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
        SamlAuthenticator authenticator = new SamlAuthenticator();
        authenticator.init();

        Field defaultSettingsField = SamlAuthenticator.class.getDeclaredField("defaultSettings");
        defaultSettingsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Object> defaultSettings = (Map<String, Object>) defaultSettingsField.get(authenticator);

        // Verify organization information is set
        assertEquals("CodeLibs", defaultSettings.get("onelogin.saml2.organization.name"));
        assertEquals("Fess", defaultSettings.get("onelogin.saml2.organization.displayname"));
        assertEquals("https://fess.codelibs.org/", defaultSettings.get("onelogin.saml2.organization.url"));
    }

    public void test_defaultSettings_serviceProviderConfig() throws Exception {
        SamlAuthenticator authenticator = new SamlAuthenticator();
        authenticator.init();

        Field defaultSettingsField = SamlAuthenticator.class.getDeclaredField("defaultSettings");
        defaultSettingsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Object> defaultSettings = (Map<String, Object>) defaultSettingsField.get(authenticator);

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
        SamlAuthenticator authenticator = new SamlAuthenticator();
        authenticator.init();

        Field defaultSettingsField = SamlAuthenticator.class.getDeclaredField("defaultSettings");
        defaultSettingsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Object> defaultSettings = (Map<String, Object>) defaultSettingsField.get(authenticator);

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
}
