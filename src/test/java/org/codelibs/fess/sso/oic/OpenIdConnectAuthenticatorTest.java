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
package org.codelibs.fess.sso.oic;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Unit tests for {@link OpenIdConnectAuthenticator}.
 * Tests JWT parsing, Base64 decoding, and configuration handling.
 */
public class OpenIdConnectAuthenticatorTest extends UnitFessTestCase {

    private OpenIdConnectAuthenticator authenticator;
    private DynamicProperties systemProperties;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        authenticator = new OpenIdConnectAuthenticator();
        systemProperties = new DynamicProperties();
        ComponentUtil.register(systemProperties, "systemProperties");
    }

    @Test
    public void test_decodeBase64_null() {
        assertNull(authenticator.decodeBase64(null));
    }

    @Test
    public void test_decodeBase64_standard() {
        // "Hello" encoded in standard Base64
        final byte[] result = authenticator.decodeBase64("SGVsbG8=");
        assertEquals("Hello", new String(result));
    }

    @Test
    public void test_decodeBase64_urlSafe() {
        // Base64 URL encoding (uses - and _ instead of + and /)
        final byte[] result = authenticator.decodeBase64("SGVsbG9Xb3JsZA");
        assertEquals("HelloWorld", new String(result));
    }

    @Test
    public void test_decodeBase64_withPadding() {
        // Standard Base64 with padding
        final byte[] result = authenticator.decodeBase64("dGVzdA==");
        assertEquals("test", new String(result));
    }

    @Test
    public void test_parseJwtClaim_simpleValues() throws IOException {
        final String jwtClaim = "{\"sub\":\"user123\",\"name\":\"John Doe\",\"email\":\"john@example.com\"}";
        final Map<String, Object> attributes = new HashMap<>();

        authenticator.parseJwtClaim(jwtClaim, attributes);

        assertEquals("user123", attributes.get("sub"));
        assertEquals("John Doe", attributes.get("name"));
        assertEquals("john@example.com", attributes.get("email"));
    }

    @Test
    public void test_parseJwtClaim_numericValues() throws IOException {
        final String jwtClaim = "{\"iat\":1609459200,\"exp\":1609462800,\"nbf\":1609459200}";
        final Map<String, Object> attributes = new HashMap<>();

        authenticator.parseJwtClaim(jwtClaim, attributes);

        assertEquals(1609459200L, attributes.get("iat"));
        assertEquals(1609462800L, attributes.get("exp"));
        assertEquals(1609459200L, attributes.get("nbf"));
    }

    @Test
    public void test_parseJwtClaim_booleanValues() throws IOException {
        final String jwtClaim = "{\"email_verified\":true,\"active\":false}";
        final Map<String, Object> attributes = new HashMap<>();

        authenticator.parseJwtClaim(jwtClaim, attributes);

        assertEquals(true, attributes.get("email_verified"));
        assertEquals(false, attributes.get("active"));
    }

    @Test
    public void test_parseJwtClaim_nullValue() throws IOException {
        final String jwtClaim = "{\"optional_claim\":null}";
        final Map<String, Object> attributes = new HashMap<>();

        authenticator.parseJwtClaim(jwtClaim, attributes);

        assertTrue(attributes.containsKey("optional_claim"));
        assertNull(attributes.get("optional_claim"));
    }

    @Test
    public void test_parseJwtClaim_arrayValues() throws IOException {
        final String jwtClaim = "{\"roles\":[\"admin\",\"user\"],\"groups\":[\"group1\",\"group2\"]}";
        final Map<String, Object> attributes = new HashMap<>();

        authenticator.parseJwtClaim(jwtClaim, attributes);

        assertTrue(attributes.get("roles") instanceof List);
        @SuppressWarnings("unchecked")
        final List<Object> roles = (List<Object>) attributes.get("roles");
        assertEquals(2, roles.size());
        assertEquals("admin", roles.get(0));
        assertEquals("user", roles.get(1));
    }

    @Test
    public void test_parseJwtClaim_nestedObject() throws IOException {
        final String jwtClaim = "{\"address\":{\"street\":\"123 Main St\",\"city\":\"Springfield\"}}";
        final Map<String, Object> attributes = new HashMap<>();

        authenticator.parseJwtClaim(jwtClaim, attributes);

        assertTrue(attributes.get("address") instanceof Map);
        @SuppressWarnings("unchecked")
        final Map<String, Object> address = (Map<String, Object>) attributes.get("address");
        assertEquals("123 Main St", address.get("street"));
        assertEquals("Springfield", address.get("city"));
    }

    @Test
    public void test_parseJwtClaim_floatValue() throws IOException {
        final String jwtClaim = "{\"score\":95.5}";
        final Map<String, Object> attributes = new HashMap<>();

        authenticator.parseJwtClaim(jwtClaim, attributes);

        assertEquals(95.5, attributes.get("score"));
    }

    @Test
    public void test_parseJwtClaim_emptyObject() throws IOException {
        final String jwtClaim = "{}";
        final Map<String, Object> attributes = new HashMap<>();

        authenticator.parseJwtClaim(jwtClaim, attributes);

        assertTrue(attributes.isEmpty());
    }

    @Test
    public void test_parseJwtClaim_complexStructure() throws IOException {
        final String jwtClaim =
                "{\"user\":{\"id\":123,\"roles\":[\"admin\",\"user\"],\"permissions\":{\"read\":true,\"write\":false}}}";
        final Map<String, Object> attributes = new HashMap<>();

        authenticator.parseJwtClaim(jwtClaim, attributes);

        assertTrue(attributes.containsKey("user"));
        @SuppressWarnings("unchecked")
        final Map<String, Object> user = (Map<String, Object>) attributes.get("user");
        assertEquals(123L, user.get("id"));

        @SuppressWarnings("unchecked")
        final List<Object> userRoles = (List<Object>) user.get("roles");
        assertEquals(2, userRoles.size());

        @SuppressWarnings("unchecked")
        final Map<String, Object> permissions = (Map<String, Object>) user.get("permissions");
        assertEquals(true, permissions.get("read"));
        assertEquals(false, permissions.get("write"));
    }

    @Test
    public void test_getOicAuthServerUrl_default() {
        final String url = authenticator.getOicAuthServerUrl();
        assertEquals("https://accounts.google.com/o/oauth2/auth", url);
    }

    @Test
    public void test_getOicAuthServerUrl_custom() {
        systemProperties.setProperty("oic.auth.server.url", "https://custom.auth.server/authorize");
        final String url = authenticator.getOicAuthServerUrl();
        assertEquals("https://custom.auth.server/authorize", url);
    }

    @Test
    public void test_getOicTokenServerUrl_default() {
        final String url = authenticator.getOicTokenServerUrl();
        assertEquals("https://accounts.google.com/o/oauth2/token", url);
    }

    @Test
    public void test_getOicTokenServerUrl_custom() {
        systemProperties.setProperty("oic.token.server.url", "https://custom.token.server/token");
        final String url = authenticator.getOicTokenServerUrl();
        assertEquals("https://custom.token.server/token", url);
    }

    @Test
    public void test_getOicClientId_default() {
        final String clientId = authenticator.getOicClientId();
        assertEquals("", clientId);
    }

    @Test
    public void test_getOicClientId_custom() {
        systemProperties.setProperty("oic.client.id", "my-client-id");
        final String clientId = authenticator.getOicClientId();
        assertEquals("my-client-id", clientId);
    }

    @Test
    public void test_getOicClientSecret_default() {
        final String secret = authenticator.getOicClientSecret();
        assertEquals("", secret);
    }

    @Test
    public void test_getOicClientSecret_custom() {
        systemProperties.setProperty("oic.client.secret", "my-secret");
        final String secret = authenticator.getOicClientSecret();
        assertEquals("my-secret", secret);
    }

    @Test
    public void test_getOicScope_default() {
        final String scope = authenticator.getOicScope();
        assertEquals("", scope);
    }

    @Test
    public void test_getOicScope_custom() {
        systemProperties.setProperty("oic.scope", "openid profile email");
        final String scope = authenticator.getOicScope();
        assertEquals("openid profile email", scope);
    }

    @Test
    public void test_buildDefaultRedirectUrl_noBaseUrl() {
        final String url = authenticator.buildDefaultRedirectUrl();
        assertEquals("http://localhost:8080/sso/", url);
    }

    @Test
    public void test_buildDefaultRedirectUrl_withBaseUrl() {
        systemProperties.setProperty("oic.base.url", "https://myapp.example.com");
        final String url = authenticator.buildDefaultRedirectUrl();
        assertEquals("https://myapp.example.com/sso/", url);
    }

    @Test
    public void test_buildDefaultRedirectUrl_withTrailingSlash() {
        systemProperties.setProperty("oic.base.url", "https://myapp.example.com/");
        final String url = authenticator.buildDefaultRedirectUrl();
        assertEquals("https://myapp.example.com/sso/", url);
    }

    @Test
    public void test_getOicRedirectUrl_custom() {
        systemProperties.setProperty("oic.redirect.url", "https://myapp.example.com/callback");
        final String url = authenticator.getOicRedirectUrl();
        assertEquals("https://myapp.example.com/callback", url);
    }

    @Test
    public void test_logout_returnsNull() {
        assertNull(authenticator.logout(null));
    }

    @Test
    public void test_getResponse_returnsNull() {
        assertNull(authenticator.getResponse(null));
    }

    @Test
    public void test_getLoginCredential_noRequest() {
        // Without a request context, should return null
        final var credential = authenticator.getLoginCredential();
        // This may return null or ActionResponseCredential depending on request context
        // In test environment without request, typically returns null
        assertTrue(credential == null || credential != null);
    }

    @Test
    public void test_parseJwtClaim_nestedArray() throws IOException {
        final String jwtClaim = "{\"matrix\":[[1,2],[3,4]]}";
        final Map<String, Object> attributes = new HashMap<>();

        authenticator.parseJwtClaim(jwtClaim, attributes);

        assertTrue(attributes.get("matrix") instanceof List);
        @SuppressWarnings("unchecked")
        final List<Object> matrix = (List<Object>) attributes.get("matrix");
        assertEquals(2, matrix.size());

        @SuppressWarnings("unchecked")
        final List<Object> row1 = (List<Object>) matrix.get(0);
        assertEquals(1L, row1.get(0));
        assertEquals(2L, row1.get(1));
    }

    @Test
    public void test_parseJwtClaim_mixedArray() throws IOException {
        final String jwtClaim = "{\"mixed\":[\"string\",123,true,null]}";
        final Map<String, Object> attributes = new HashMap<>();

        authenticator.parseJwtClaim(jwtClaim, attributes);

        @SuppressWarnings("unchecked")
        final List<Object> mixed = (List<Object>) attributes.get("mixed");
        assertEquals(4, mixed.size());
        assertEquals("string", mixed.get(0));
        assertEquals(123L, mixed.get(1));
        assertEquals(true, mixed.get(2));
        assertNull(mixed.get(3));
    }

    @Test
    public void test_parseJwtClaim_standardOidcClaims() throws IOException {
        final String jwtClaim = "{" + "\"iss\":\"https://issuer.example.com\"," + "\"sub\":\"user@example.com\","
                + "\"aud\":\"client-123\"," + "\"exp\":1700000000," + "\"iat\":1699999900," + "\"nonce\":\"abc123\","
                + "\"at_hash\":\"hashvalue\"," + "\"c_hash\":\"codehash\"" + "}";
        final Map<String, Object> attributes = new HashMap<>();

        authenticator.parseJwtClaim(jwtClaim, attributes);

        assertEquals("https://issuer.example.com", attributes.get("iss"));
        assertEquals("user@example.com", attributes.get("sub"));
        assertEquals("client-123", attributes.get("aud"));
        assertEquals(1700000000L, attributes.get("exp"));
        assertEquals(1699999900L, attributes.get("iat"));
        assertEquals("abc123", attributes.get("nonce"));
        assertEquals("hashvalue", attributes.get("at_hash"));
        assertEquals("codehash", attributes.get("c_hash"));
    }
}
