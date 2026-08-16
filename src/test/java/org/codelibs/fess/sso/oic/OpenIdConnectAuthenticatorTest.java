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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.codelibs.core.io.FileUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.app.web.base.login.ActionResponseCredential;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.utflute.mocklet.MockletHttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.web.login.credential.LoginCredential;

import com.google.api.client.auth.oauth2.TokenResponse;

import jakarta.servlet.http.HttpServletRequest;

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
        final File propFile = File.createTempFile("oic_test", ".properties");
        propFile.deleteOnExit();
        FileUtil.writeBytes(propFile.getAbsolutePath(), "".getBytes("UTF-8"));
        systemProperties = new DynamicProperties(propFile);
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
        final String jwtClaim = "{\"user\":{\"id\":123,\"roles\":[\"admin\",\"user\"],\"permissions\":{\"read\":true,\"write\":false}}}";
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
    public void test_getOicTokenServerUrl_default() {
        final String url = authenticator.getOicTokenServerUrl();
        assertEquals("https://accounts.google.com/o/oauth2/token", url);
    }

    @Test
    public void test_getOicClientId_default() {
        final String clientId = authenticator.getOicClientId();
        assertEquals("", clientId);
    }

    @Test
    public void test_getOicClientSecret_default() {
        final String secret = authenticator.getOicClientSecret();
        assertEquals("", secret);
    }

    @Test
    public void test_getOicScope_default() {
        final String scope = authenticator.getOicScope();
        assertEquals("", scope);
    }

    @Test
    public void test_buildDefaultRedirectUrl_noBaseUrl() {
        final String url = authenticator.buildDefaultRedirectUrl();
        assertEquals("http://localhost:8080/sso/", url);
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
    public void test_getLoginCredential_withRequest() {
        // With a request context, should return ActionResponseCredential for OAuth redirect
        final var credential = authenticator.getLoginCredential();
        assertNotNull(credential);
        assertTrue(credential instanceof ActionResponseCredential);
    }

    @Test
    public void test_getAuthUrl_issuesAnUnguessableState() {
        // The state is the only thing standing between a login and a forged callback
        // (RFC 6749 section 10.12), and org.codelibs.core.net.UuidUtil -- which getAuthUrl used
        // to call -- is hex(localIP) + hex(identityHashCode(RANDOM)) +
        // hex((int) (currentTimeMillis() >> 32)) + hex(SecureRandom.nextInt()): the first 16 hex
        // characters never change within a JVM and the timestamp word moves every ~49.7 days, so
        // under 32 bits actually varied per call.
        final Set<String> states = new HashSet<>();
        final Set<String> prefixes = new HashSet<>();
        for (int i = 0; i < 200; i++) {
            final HttpServletRequest request = getMockRequest();
            authenticator.getAuthUrl(request);
            // getAuthUrl stashes the same value it puts in the URL, and getLoginCredential only
            // ever compares the two with equals(), so nothing depends on its length or format.
            final String state = (String) request.getSession().getAttribute(OpenIdConnectAuthenticator.OIC_STATE);
            assertNotNull(state, "no state was stored in the session");
            states.add(state);
            prefixes.add(state.replace("-", "").substring(0, 16));
        }

        assertEquals(200, states.size());
        // The whole point: a fixed leading half is what UuidUtil produced.
        assertTrue("distinct prefixes: " + prefixes.size(), prefixes.size() > 190);
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

    // ===================================================================================
    //                                                        Callback failure handling
    //                                                        =========================

    private static String segment(final String json) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    private static String jwtOf(final String claimJson) {
        return segment("{\"alg\":\"RS256\"}") + "." + segment(claimJson) + "." + segment("signature");
    }

    private static TokenResponse tokenResponseWith(final Object idToken) {
        final TokenResponse tr = new TokenResponse();
        tr.setAccessToken("access-token");
        tr.setTokenType("Bearer");
        tr.setExpiresInSeconds(300L);
        if (idToken != null) {
            tr.set("id_token", idToken);
        }
        return tr;
    }

    private OpenIdConnectAuthenticator authenticatorReturning(final TokenResponse tr) {
        return new OpenIdConnectAuthenticator() {
            @Override
            protected TokenResponse getTokenUrl(final String code) {
                return tr;
            }
        };
    }

    private LoginCredential callbackWith(final Object idToken) {
        return authenticatorReturning(tokenResponseWith(idToken)).processCallback(getMockRequest(), "the-code");
    }

    @Test
    public void test_processCallback_acceptsAWellFormedIdToken() {
        final LoginCredential credential = callbackWith(jwtOf("{\"email\":\"user@example.com\"}"));
        assertNotNull(credential);
        assertEquals("{user@example.com}", credential.toString());
    }

    @Test
    public void test_processCallback_withoutIdToken() {
        // A token response that carries no id_token used to reach ((String) null).split and throw.
        assertNull(callbackWith(null));
    }

    @Test
    public void test_processCallback_withNonStringIdToken() {
        assertNull(callbackWith(Long.valueOf(42)));
    }

    @Test
    public void test_processCallback_withBlankIdToken() {
        assertNull(callbackWith(""));
    }

    @Test
    public void test_processCallback_withTwoSegmentIdToken() {
        // jwt[2] used to throw ArrayIndexOutOfBoundsException, which no caller catches.
        assertNull(callbackWith("header.claim"));
    }

    @Test
    public void test_processCallback_withFourSegmentIdToken() {
        // A JWE compact serialisation has five segments and is not a signed JWT either.
        assertNull(callbackWith("a.b.c.d"));
    }

    @Test
    public void test_processCallback_withUndecodableSegment() {
        // decodeBase64 throws IllegalArgumentException, which only the IOException catch used to cover.
        assertNull(callbackWith("aGVhZGVy.!!!not-base64!!!.c2ln"));
    }

    @Test
    public void test_processCallback_withNonJsonClaim() {
        assertNull(callbackWith(segment("{\"alg\":\"RS256\"}") + "." + segment("not json at all") + "." + segment("s")));
    }

    @Test
    public void test_processCallback_withoutEmailClaim() {
        // The email claim is the user id. A credential without one logs in as a null-named user and
        // then fails on every later request, so it must not become a session at all.
        assertNull(callbackWith(jwtOf("{\"sub\":\"1234\",\"groups\":[\"dev\"]}")));
    }

    @Test
    public void test_processCallback_withBlankEmailClaim() {
        assertNull(callbackWith(jwtOf("{\"email\":\"\"}")));
    }

    @Test
    public void test_getLoginCredential_withProviderErrorResponse() {
        // error=access_denied with the state we issued means the provider refused this login. Starting
        // another authorization request would loop against a provider that keeps refusing, and would
        // override the user's own refusal against one that does not.
        final MockletHttpServletRequest request = getMockRequest();
        request.getSession().setAttribute(OpenIdConnectAuthenticator.OIC_STATE, "the-state");
        request.setParameter("state", "the-state");
        request.setParameter("error", "access_denied");
        request.setParameter("error_description", "The user declined");

        assertNull(authenticator.getLoginCredential());
        assertNull(request.getSession().getAttribute(OpenIdConnectAuthenticator.OIC_STATE));
    }

    @Test
    public void test_getLoginCredential_withErrorForAnotherState() {
        // A state that is not the one in the session is not this login's error response, so the
        // existing behaviour -- start a fresh authorization request -- is kept.
        final MockletHttpServletRequest request = getMockRequest();
        request.getSession().setAttribute(OpenIdConnectAuthenticator.OIC_STATE, "the-state");
        request.setParameter("state", "a-different-state");
        request.setParameter("error", "access_denied");

        final LoginCredential credential = authenticator.getLoginCredential();
        assertNotNull(credential);
        assertTrue(credential instanceof ActionResponseCredential);
    }

    @Test
    public void test_getLoginCredential_withoutCodeOrError() {
        // A bare callback with a matching state and neither parameter still restarts the flow.
        final MockletHttpServletRequest request = getMockRequest();
        request.getSession().setAttribute(OpenIdConnectAuthenticator.OIC_STATE, "the-state");
        request.setParameter("state", "the-state");

        final LoginCredential credential = authenticator.getLoginCredential();
        assertNotNull(credential);
        assertTrue(credential instanceof ActionResponseCredential);
    }

    // ===================================================================================
    //                                                            Debug log confidentiality
    //                                                            =========================

    private static String segment(final byte[] raw) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
    }

    /**
     * Drives processCallback with a token response the test controls and returns everything this
     * class logged while doing it.
     */
    private String debugOutputOfCallback(final TokenResponse tr) {
        final LogCapturingAppender appender = LogCapturingAppender.attach(OpenIdConnectAuthenticator.class.getName(), Level.DEBUG);
        try {
            authenticatorReturning(tr).processCallback(getMockRequest(), "the-code");
        } finally {
            appender.detach();
        }
        final StringBuilder buf = new StringBuilder();
        for (final LogEvent event : appender.events()) {
            buf.append(event.getMessage().getFormattedMessage()).append('\n');
        }
        return buf.toString();
    }

    private static TokenResponse secretCarryingTokenResponse() {
        final TokenResponse tr = new TokenResponse();
        tr.setAccessToken("ACCESS-TOKEN-MUST-NOT-BE-LOGGED");
        tr.setRefreshToken("REFRESH-TOKEN-MUST-NOT-BE-LOGGED");
        tr.setTokenType("Bearer");
        tr.setExpiresInSeconds(300L);
        // A signature is raw bytes; these are not valid UTF-8 text.
        final byte[] signature = { 0x00, 0x01, (byte) 0xC3, (byte) 0x28, (byte) 0xA0, (byte) 0xA1, 0x07 };
        tr.set("id_token", segment("{\"alg\":\"RS256\"}") + "." + segment("{\"email\":\"user@example.com\"}") + "." + segment(signature));
        return tr;
    }

    @Test
    public void test_processCallback_doesNotLogTheAccessOrRefreshToken() {
        // The documentation tells administrators to raise this logger to debug when a login
        // misbehaves, so anything it prints reaches log files, issue reports and log collectors.
        final String output = debugOutputOfCallback(secretCarryingTokenResponse());

        assertFalse(output.contains("ACCESS-TOKEN-MUST-NOT-BE-LOGGED"), "the access token was logged");
        assertFalse(output.contains("REFRESH-TOKEN-MUST-NOT-BE-LOGGED"), "the refresh token was logged");
        // What is actually needed to diagnose a login is still there.
        assertTrue(output.contains("user@example.com"), "the claim set was not logged");
        assertTrue(output.contains("Bearer"), "the token type was not logged");
    }

    @Test
    public void test_processCallback_doesNotLogRawSignatureBytes() {
        final String output = debugOutputOfCallback(secretCarryingTokenResponse());

        // A single invalid byte in fess.log makes the whole file count as binary, and grep and the
        // rest of the usual log tooling then skip it without saying so.
        assertFalse(output.contains("\u0000"), "a NUL byte reached the log");
        assertFalse(output.contains("\u0007"), "a control byte reached the log");
        assertFalse(output.contains("\ufffd"), "an undecodable byte reached the log");
    }
}
