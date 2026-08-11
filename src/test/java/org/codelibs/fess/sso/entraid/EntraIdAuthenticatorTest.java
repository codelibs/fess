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
package org.codelibs.fess.sso.entraid;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.core.misc.Pair;
import org.codelibs.curl.Curl;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.app.web.base.login.ActionResponseCredential;
import org.codelibs.fess.app.web.base.login.EntraIdCredential.EntraIdUser;
import org.codelibs.fess.app.web.base.login.EntraIdCredential;
import org.codelibs.fess.exception.SsoLoginException;
import org.codelibs.fess.exception.SsoStateException;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.utflute.mocklet.MockletHttpServletRequest;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.login.exception.LoginFailureException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.ITenantProfile;
import com.sun.net.httpserver.HttpServer;

public class EntraIdAuthenticatorTest extends UnitFessTestCase {

    /**
     * These tests reset a setting by writing {@code ""} rather than removing it, and
     * {@code ComponentUtil.getSystemProperties()} is held by the container for the life of the
     * surefire fork. Without its own container this class would leave entraid.state.ttl,
     * entraid.default.groups, entraid.response.mode, aad.authority and aad.response.mode
     * permanently present-but-empty for every test class that runs after it -- which is the exact
     * state {@code test_getAuthority_fallsBackWhenTheLegacyKeyIsPresentButBlank} and
     * {@code test_getResponseMode_ignoresABlankLegacyKey} exist to characterise.
     *
     * @return true to create the container for each test
     */
    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

    private void setEntraIdConfig(final String clientId, final String clientSecret, final String tenant) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        fessConfig.setSystemProperty("entraid.client.id", clientId);
        fessConfig.setSystemProperty("entraid.client.secret", clientSecret);
        fessConfig.setSystemProperty("entraid.tenant", tenant);
    }

    @Test
    public void test_getClientApplication_isReusedSoItsTokenCacheSurvives() {
        // A fresh ConfidentialClientApplication starts with an empty TokenCache, and
        // acquireTokenSilently throws NO_TOKEN_IN_CACHE on a miss. Building one per call therefore
        // made silent refresh impossible; the tokens acquired at login have to stay reachable.
        try {
            setEntraIdConfig("11111111-1111-1111-1111-111111111111", "secret-1", "contoso.onmicrosoft.com");
            final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();

            assertSame(authenticator.getClientApplication(), authenticator.getClientApplication());
        } finally {
            setEntraIdConfig("", "", "");
        }
    }

    @Test
    public void test_getClientApplication_publishesTheApplicationAndItsKeyTogether() {
        // The application and the configuration it was built from used to be two separate
        // volatile fields, read one after the other. A reader that landed between the two writes
        // paired the old application with the new key and kept returning the stale one. One
        // reference makes that unrepresentable, and the key it carries is the one the published
        // application was actually built from.
        try {
            setEntraIdConfig("11111111-1111-1111-1111-111111111111", "secret-1", "contoso.onmicrosoft.com");
            final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();

            final ConfidentialClientApplication application = authenticator.getClientApplication();

            assertNotNull(authenticator.clientApplicationHolder);
            assertSame(application, authenticator.clientApplicationHolder.getApplication());
            assertEquals(authenticator.buildClientApplicationKey(), authenticator.clientApplicationHolder.getKey());
        } finally {
            setEntraIdConfig("", "", "");
        }
    }

    @Test
    public void test_getClientApplication_isRebuiltWhenTheConfigurationChanges() {
        // The client id, secret and tenant are editable from the admin screen at runtime.
        try {
            setEntraIdConfig("11111111-1111-1111-1111-111111111111", "secret-1", "contoso.onmicrosoft.com");
            final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
            final ConfidentialClientApplication first = authenticator.getClientApplication();

            setEntraIdConfig("11111111-1111-1111-1111-111111111111", "secret-2", "contoso.onmicrosoft.com");
            final ConfidentialClientApplication afterSecretChange = authenticator.getClientApplication();
            assertTrue("secret change must rebuild", first != afterSecretChange);

            setEntraIdConfig("22222222-2222-2222-2222-222222222222", "secret-2", "contoso.onmicrosoft.com");
            assertTrue("config change must rebuild", afterSecretChange != authenticator.getClientApplication());

            setEntraIdConfig("22222222-2222-2222-2222-222222222222", "secret-2", "fabrikam.onmicrosoft.com");
            assertTrue("config change must rebuild", afterSecretChange != authenticator.getClientApplication());
        } finally {
            setEntraIdConfig("", "", "");
        }
    }

    @Test
    public void test_logout_evictsTheUsersTokensFromTheSharedCache() {
        // MSAL4J's TokenCache is five unbounded LinkedHashMaps with no eviction; the only way
        // anything leaves is removeAccount(). Now that one application is shared for the whole
        // server, never calling it would keep every user who ever logged in resident until restart.
        final List<IAccount> removed = new ArrayList<>();
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator() {
            @Override
            protected void removeAccount(final IAccount account) {
                removed.add(account);
            }

            @Override
            public void updateMemberOf(final EntraIdUser user) {
                // keep the constructor off Microsoft Graph
            }
        };
        ComponentUtil.register(authenticator, EntraIdAuthenticator.class.getCanonicalName());
        final TestAccount account = new TestAccount();
        final EntraIdUser user = new EntraIdCredential(new TestAuthenticationResult(account)).getUser();

        assertNull(authenticator.logout(new FessUserBean(user)));

        assertEquals(1, removed.size());
        assertSame(account, removed.get(0));
    }

    @Test
    public void test_logout_ignoresAUserThatIsNotAnEntraIdUser() {
        // SPNEGO, SAML and LDAP users reach the same logout hook.
        final List<IAccount> removed = new ArrayList<>();
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator() {
            @Override
            protected void removeAccount(final IAccount account) {
                removed.add(account);
            }
        };

        assertNull(authenticator.logout(new FessUserBean(new TestFessUser())));
        assertTrue(removed.isEmpty());
    }

    /** A FessUser that is not an EntraIdUser, standing in for the other authenticators. */
    private static class TestFessUser implements org.codelibs.fess.entity.FessUser {
        private static final long serialVersionUID = 1L;

        @Override
        public String getName() {
            return "not-an-entraid-user";
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

    private static class TestAccount implements IAccount {
        private static final long serialVersionUID = 1L;

        @Override
        public String homeAccountId() {
            return "home-account-id";
        }

        @Override
        public String environment() {
            return "login.microsoftonline.com";
        }

        @Override
        public String username() {
            return "taro@contoso.onmicrosoft.com";
        }

        @Override
        public Map<String, ITenantProfile> getTenantProfiles() {
            return Collections.emptyMap();
        }
    }

    private static class TestAuthenticationResult implements IAuthenticationResult {
        private static final long serialVersionUID = 1L;
        private final IAccount account;
        private final Date expiresOn;
        private final String accessToken;
        private final String idToken;

        TestAuthenticationResult(final IAccount account) {
            this(account, new Date(Long.MAX_VALUE));
        }

        TestAuthenticationResult(final IAccount account, final Date expiresOn) {
            this(account, expiresOn, "access-token", "id-token");
        }

        TestAuthenticationResult(final IAccount account, final Date expiresOn, final String accessToken) {
            this(account, expiresOn, accessToken, "id-token");
        }

        TestAuthenticationResult(final IAccount account, final String idToken) {
            this(account, new Date(Long.MAX_VALUE), "access-token", idToken);
        }

        TestAuthenticationResult(final IAccount account, final Date expiresOn, final String accessToken, final String idToken) {
            this.account = account;
            this.expiresOn = expiresOn;
            this.accessToken = accessToken;
            this.idToken = idToken;
        }

        @Override
        public String accessToken() {
            return accessToken;
        }

        @Override
        public String idToken() {
            return idToken;
        }

        @Override
        public IAccount account() {
            return account;
        }

        @Override
        public ITenantProfile tenantProfile() {
            return null;
        }

        @Override
        public String environment() {
            return "login.microsoftonline.com";
        }

        @Override
        public String scopes() {
            return "https://graph.microsoft.com/.default";
        }

        @Override
        public Date expiresOnDate() {
            return expiresOn;
        }
    }

    @Test
    public void test_getClientApplication_isBuiltOnceUnderConcurrentAccess() throws Exception {
        try {
            setEntraIdConfig("11111111-1111-1111-1111-111111111111", "secret-1", "contoso.onmicrosoft.com");
            final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
            final int threads = 8;
            final CountDownLatch start = new CountDownLatch(1);
            final List<ConfidentialClientApplication> seen = Collections.synchronizedList(new ArrayList<>());
            final List<Thread> workers = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                final Thread t = new Thread(() -> {
                    try {
                        start.await();
                    } catch (final InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    seen.add(authenticator.getClientApplication());
                });
                workers.add(t);
                t.start();
            }
            start.countDown();
            for (final Thread t : workers) {
                t.join(10000L);
            }

            assertEquals(threads, seen.size());
            // Two instances means two token caches, and a login cached in one is invisible to the
            // other when its refresh comes round.
            seen.forEach(app -> assertSame(seen.get(0), app));
        } finally {
            setEntraIdConfig("", "", "");
        }
    }

    /** Lets a test move the clock that state expiry is measured against. */
    private final AtomicLong clock = new AtomicLong(1_000_000L);

    private EntraIdAuthenticator newAuthenticatorWithControlledClock() {
        ComponentUtil.register(new SystemHelper() {
            @Override
            public long getCurrentTimeAsLong() {
                return clock.get();
            }
        }, "systemHelper");
        return new EntraIdAuthenticator();
    }

    @Test
    public void test_maskSecret() {
        // Long values keep a short prefix so two different codes can still be told apart
        // in a log, without the value itself being usable.
        assertEquals("abcdefgh***", EntraIdAuthenticator.maskSecret("abcdefghijklmnopqrstuvwxyz"));
        // Values shorter than the prefix are not padded out.
        assertEquals("abc***", EntraIdAuthenticator.maskSecret("abc"));
        assertEquals("abcdefgh***", EntraIdAuthenticator.maskSecret("abcdefgh"));
        // Null and empty must not blow up: these are logged on paths where the identity
        // provider may simply not have sent the field.
        assertNull(EntraIdAuthenticator.maskSecret(null));
        assertEquals("", EntraIdAuthenticator.maskSecret(""));
    }

    @Test
    public void test_maskParams_masksCredentialsAndKeepsDiagnostics() {
        final Map<String, List<String>> params = new LinkedHashMap<>();
        params.put("code", List.of("0.AXkAauthorizationcodevalue"));
        params.put("id_token", List.of("eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.payload.sig"));
        params.put("state", List.of("2b1f5c3e-0000-0000-0000-000000000000"));
        params.put("error", List.of("access_denied"));
        params.put("error_description", List.of("AADSTS65004: User declined to consent."));

        final Map<String, List<String>> masked = EntraIdAuthenticator.maskParams(params);

        // Credentials are truncated.
        assertEquals(List.of("0.AXkAau***"), masked.get("code"));
        assertEquals(List.of("eyJ0eXAi***"), masked.get("id_token"));
        // Everything needed to diagnose a failed login is kept verbatim.
        assertEquals(List.of("2b1f5c3e-0000-0000-0000-000000000000"), masked.get("state"));
        assertEquals(List.of("access_denied"), masked.get("error"));
        assertEquals(List.of("AADSTS65004: User declined to consent."), masked.get("error_description"));
        // The key set is unchanged, so the log still shows which artifacts arrived.
        assertEquals(params.keySet(), masked.keySet());
        // The caller's map is not modified.
        assertEquals(List.of("0.AXkAauthorizationcodevalue"), params.get("code"));
    }

    @Test
    public void test_maskParams_isCaseInsensitiveOnKeys() {
        final Map<String, List<String>> params = new LinkedHashMap<>();
        params.put("Code", List.of("0.AXkAauthorizationcodevalue"));
        params.put("ACCESS_TOKEN", List.of("accesstokenvalue12345"));

        final Map<String, List<String>> masked = EntraIdAuthenticator.maskParams(params);

        assertEquals(List.of("0.AXkAau***"), masked.get("Code"));
        assertEquals(List.of("accessto***"), masked.get("ACCESS_TOKEN"));
    }

    @Test
    public void test_getAuthUrl_requestsQueryResponseMode() {
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();

        final String authUrl = authenticator.getAuthUrl(getMockRequest());

        // form_post makes Entra ID reply with a cross-site POST, which does not carry a
        // SameSite=Lax session cookie -- and Fess sets SameSite=Lax on JSESSIONID by default
        // (tomcat.sameSiteCookies). Without the session there is no stored state, so the
        // callback can never be validated. query mode replies with a top-level GET instead.
        assertTrue(authUrl.contains("response_mode=query"));
        assertFalse(authUrl.contains("response_mode=form_post"));
        // The rest of the authorization request is unchanged.
        assertTrue(authUrl.contains("response_type=code"));
        assertTrue(authUrl.contains("&state="));
        assertTrue(authUrl.contains("&nonce="));
    }

    @Test
    public void test_getAuthUrl_alwaysUsesTheV2Endpoint() {
        // msal4j hardcodes oauth2/v2.0/token as the token endpoint of an AAD authority, so a code
        // minted at the v1.0 /oauth2/authorize could never be redeemed. The setter is kept so an
        // out-of-tree fess_sso+entraidAuthenticator.xml still loads, but it no longer selects a
        // login that always fails.
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        authenticator.setUseV2Endpoint(false);

        final String authUrl = authenticator.getAuthUrl(getMockRequest());

        assertTrue(authUrl.contains("/oauth2/v2.0/authorize?"));
        assertFalse(authUrl.contains("resource=https%3a%2f%2fgraph.microsoft.com"));
        assertTrue(authUrl.contains("response_mode=query"));
    }

    @Test
    public void test_getAuthUrl_requestsTheOidcScopesUpFront() {
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();

        final String authUrl = authenticator.getAuthUrl(getMockRequest());

        // msal4j already prepends these to the token request (OAuthAuthorizationGrant's
        // COMMON_SCOPES), so asking for them at the authorization endpoint too keeps consent and
        // the token exchange asking for the same thing.
        final String scope = URLDecoder.decode(authUrl.replaceFirst("(?s).*[?&]scope=([^&]*).*", "$1"), StandardCharsets.UTF_8);
        assertEquals("openid profile offline_access https://graph.microsoft.com/.default", scope);
    }

    @Test
    public void test_getAuthUrl_encodesTheScopeParameter() {
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();

        final String authUrl = authenticator.getAuthUrl(getMockRequest());

        // A multi-valued scope is space separated, which cannot be sent raw.
        assertFalse(authUrl.contains("scope=openid profile"));
        assertTrue(authUrl.contains("&client_id="));
    }

    @Test
    public void test_containsAuthenticationData_acceptsQueryModeCallback() {
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        final MockletHttpServletRequest request = getMockRequest();
        request.setMethod("GET");
        request.setParameter("code", "0.AXkAauthorizationcodevalue");
        request.setParameter("state", "2b1f5c3e-0000-0000-0000-000000000000");

        assertTrue(authenticator.containsAuthenticationData(request));
    }

    @Test
    public void test_containsAuthenticationData_acceptsFormPostCallback() {
        // An existing deployment that already set tomcat.sameSiteCookies=none keeps working.
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        final MockletHttpServletRequest request = getMockRequest();
        request.setMethod("POST");
        request.setParameter("code", "0.AXkAauthorizationcodevalue");

        assertTrue(authenticator.containsAuthenticationData(request));
    }

    @Test
    public void test_containsAuthenticationData_acceptsErrorCallback() {
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        final MockletHttpServletRequest request = getMockRequest();
        request.setMethod("GET");
        request.setParameter("error", "access_denied");

        assertTrue(authenticator.containsAuthenticationData(request));
    }

    @Test
    public void test_getLoginCredential_surfacesUnexpectedFailures() {
        // A swallowed failure leaves the operator with nothing above DEBUG to work from.
        // SsoAction logs SsoLoginException at WARN and shows the SSO error message, which is
        // what the OpenID Connect authenticator already relies on.
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator() {
            @Override
            protected LoginCredential processAuthenticationData(final HttpServletRequest request) {
                throw new IllegalStateException("graph is down");
            }
        };
        final MockletHttpServletRequest request = getMockRequest();
        request.setMethod("GET");
        request.setParameter("code", "0.AXkAauthorizationcodevalue");
        request.getSession();

        try {
            authenticator.getLoginCredential();
            fail("expected SsoLoginException");
        } catch (final SsoLoginException e) {
            assertEquals("graph is down", e.getCause().getMessage());
        }
    }

    @Test
    public void test_getLoginCredential_propagatesSsoLoginExceptionUnwrapped() {
        final SsoLoginException thrown = new SsoLoginException("could not validate state");
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator() {
            @Override
            protected LoginCredential processAuthenticationData(final HttpServletRequest request) {
                throw thrown;
            }
        };
        final MockletHttpServletRequest request = getMockRequest();
        request.setMethod("GET");
        request.setParameter("code", "0.AXkAauthorizationcodevalue");
        request.getSession();

        try {
            authenticator.getLoginCredential();
            fail("expected SsoLoginException");
        } catch (final SsoLoginException e) {
            assertSame(thrown, e);
        }
    }

    @Test
    public void test_getLoginCredential_doesNotRedirectACallbackThatLostItsSession() {
        // Redirecting a callback that arrived without a session sends the user straight back
        // here without a session again, which is the infinite loop this change is about.
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        final MockletHttpServletRequest request = getMockRequest();
        request.setMethod("GET");
        request.setParameter("code", "0.AXkAauthorizationcodevalue");
        request.setParameter("state", "2b1f5c3e-0000-0000-0000-000000000000");
        assertNull(request.getSession(false));

        assertNull(authenticator.getLoginCredential());
    }

    @Test
    public void test_getLoginCredential_restartsTheLoginWhenTheSessionMerelyExpired() {
        // The browser did send a session id, so it stores and returns cookies; the container just
        // no longer knows that session. 15.7 recovered by bouncing back to Entra ID, and dropping
        // the user on the local login form instead leaves them stuck -- that form has no SSO link.
        // This cannot loop: getAuthUrl creates a session, so the next callback either finds it or
        // arrives with no session id at all, which is the branch the sibling test pins.
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        final MockletHttpServletRequest request = getMockRequest();
        request.setMethod("GET");
        request.setParameter("code", "0.AXkAauthorizationcodevalue");
        request.setParameter("state", "2b1f5c3e-0000-0000-0000-000000000000");
        request.addCookie(new Cookie("jsessionid", "AB1C2D3E4F5061728394A5B6C7D8E9F0"));
        assertNull(request.getSession(false));
        assertNotNull(request.getRequestedSessionId());
        assertFalse(request.isRequestedSessionIdValid());

        try {
            setEntraIdConfig("11111111-1111-1111-1111-111111111111", "secret-1", "contoso.onmicrosoft.com");

            final LoginCredential credential = authenticator.getLoginCredential();

            assertTrue(credential instanceof ActionResponseCredential);
        } finally {
            setEntraIdConfig("", "", "");
        }
    }

    @Test
    public void test_getLoginCredential_reportsAnUnconfiguredTenantInsteadOfRedirecting() {
        // Unconfigured, Fess used to redirect to
        // https://login.microsoftonline.com//oauth2/v2.0/authorize?...&client_id= and log nothing,
        // so the only symptom was a Microsoft error page.
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        final MockletHttpServletRequest request = getMockRequest();
        request.setMethod("GET");
        setEntraIdConfig("", "", "");

        try {
            authenticator.getLoginCredential();
            fail("expected SsoLoginException");
        } catch (final SsoLoginException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("entraid.tenant"));
            assertTrue(e.getMessage(), e.getMessage().contains("entraid.client.id"));
            assertTrue(e.getMessage(), e.getMessage().contains("entraid.client.secret"));
        }
    }

    @Test
    public void test_getLoginCredential_reportsThePartiallyConfiguredKeysOnly() {
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        final MockletHttpServletRequest request = getMockRequest();
        request.setMethod("GET");

        try {
            setEntraIdConfig("11111111-1111-1111-1111-111111111111", "secret-1", "");

            authenticator.getLoginCredential();
            fail("expected SsoLoginException");
        } catch (final SsoLoginException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("entraid.tenant"));
            assertFalse(e.getMessage(), e.getMessage().contains("entraid.client.id"));
        } finally {
            setEntraIdConfig("", "", "");
        }
    }

    @Test
    public void test_getLoginCredential_thrownEagerlyRatherThanFromTheRedirectSupplier() {
        // SsoAction runs the ActionResponseCredential supplier outside the block that catches
        // SsoLoginException, so a throw from inside the lambda reaches the generic error page
        // instead of the SSO error message. The check therefore has to happen before the
        // credential is built, not when it is executed.
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        final MockletHttpServletRequest request = getMockRequest();
        request.setMethod("GET");
        setEntraIdConfig("", "", "");

        try {
            authenticator.getLoginCredential();
            fail("expected SsoLoginException before any credential was returned");
        } catch (final SsoLoginException e) {
            // expected
        }
    }

    @Test
    public void test_getAuthUrl_issuesAnUnguessableState() {
        // org.codelibs.core.net.UuidUtil is hex(localIP) + hex(identityHashCode(RANDOM)) +
        // hex((int) (currentTimeMillis() >> 32)) + hex(SecureRandom.nextInt()): the first 16 hex
        // characters never change within a JVM and the timestamp word moves every ~49.7 days, so
        // under 32 bits actually vary per call. RFC 6749 section 10.12 wants the state
        // unguessable.
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        final Set<String> states = new HashSet<>();
        final Set<String> prefixes = new HashSet<>();
        for (int i = 0; i < 200; i++) {
            final String state = URLDecoder.decode(
                    authenticator.getAuthUrl(newAuthUrlRequest()).replaceFirst("(?s).*[&?]state=([^&]*).*", "$1"), StandardCharsets.UTF_8);
            states.add(state);
            prefixes.add(state.replace("-", "").substring(0, 16));
        }

        assertEquals(200, states.size());
        // The whole point: a fixed leading half is what UuidUtil produced.
        assertTrue("distinct prefixes: " + prefixes.size(), prefixes.size() > 190);
    }

    @Test
    public void test_getAuthUrl_issuesADistinctNoncePerRequest() {
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        final Set<String> nonces = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            nonces.add(authenticator.getAuthUrl(newAuthUrlRequest()).replaceFirst("(?s).*&nonce=([^&]*).*", "$1"));
        }

        assertEquals(50, nonces.size());
    }

    @Test
    public void test_createGroupCache_isBounded() {
        // Every sibling cache in Fess caps its size; this one only had an expiry, so a tenant with
        // many groups grew it without bound until the TTL came round.
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        assertTrue(authenticator.maxGroupCacheSize > 0);
        authenticator.setMaxGroupCacheSize(2);

        final Cache<String, Pair<String[], String[]>> cache = authenticator.createGroupCache();
        for (int i = 0; i < 10; i++) {
            cache.put("group-" + i, new Pair<>(new String[0], new String[0]));
        }
        cache.cleanUp();

        assertTrue("size=" + cache.size(), cache.size() <= 2);
    }

    @Test
    public void test_containsAuthenticationData_ignoresRequestWithoutArtifacts() {
        // A plain visit to /sso must still start a fresh login instead of being treated
        // as a callback.
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        final MockletHttpServletRequest request = getMockRequest();
        request.setMethod("GET");

        assertFalse(authenticator.containsAuthenticationData(request));
    }

    @Test
    public void test_graphTimeouts_haveBoundedDefaults() {
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        // curl4j leaves both timeouts at -1 unless told otherwise, and -1 means "never give up".
        // Any non-positive default would put an unbounded Graph call back on the login path.
        assertTrue(authenticator.graphConnectTimeout > 0);
        assertTrue(authenticator.graphReadTimeout > 0);
    }

    @Test
    public void test_createGraphRequest_stopsWaitingOnAnUnresponsiveEndpoint() throws Exception {
        // A server that accepts the connection and then never answers. Without a read timeout
        // this call never returns, and on the login path that blocks the request thread.
        try (ServerSocket server = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
            final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
            authenticator.setGraphReadTimeout(300);
            final String url = "http://" + server.getInetAddress().getHostAddress() + ":" + server.getLocalPort() + "/v1.0/me/memberOf";

            final long start = System.currentTimeMillis();
            try (CurlResponse response = authenticator.createGraphRequest(Curl.get(url), "access-token").execute()) {
                fail("expected the request to time out");
            } catch (final Exception expected) {
                // curl4j wraps the SocketTimeoutException
            }
            final long elapsed = System.currentTimeMillis() - start;
            // Well under the 30s default, so an ignored setter fails here instead of just being slow.
            assertTrue("took " + elapsed + "ms", elapsed < 5000L);
        }
    }

    @Test
    public void test_storeStateInSession_dropsExpiredStatesOnWrite() {
        // Expired states used to be cleared only when a callback arrived. A user who keeps
        // starting logins without finishing one grew the session map without bound.
        final EntraIdAuthenticator authenticator = newAuthenticatorWithControlledClock();
        final HttpSession session = getMockRequest().getSession();

        authenticator.storeStateInSession(session, "state-1", "nonce-1");
        assertEquals(1, authenticator.getStateMap(session).size());

        // getStateTtl() defaults to 3600 and is compared in seconds.
        clock.addAndGet(3601L * 1000L);
        authenticator.storeStateInSession(session, "state-2", "nonce-2");

        final Map<String, EntraIdAuthenticator.StateData> stateMap = authenticator.getStateMap(session);
        assertEquals(1, stateMap.size());
        assertTrue(stateMap.containsKey("state-2"));
    }

    @Test
    public void test_storeStateInSession_capsTheNumberOfLiveStates() {
        final EntraIdAuthenticator authenticator = newAuthenticatorWithControlledClock();
        authenticator.setMaxStates(3);
        final HttpSession session = getMockRequest().getSession();

        for (int i = 0; i < 20; i++) {
            clock.addAndGet(1000L);
            authenticator.storeStateInSession(session, "state-" + i, "nonce-" + i);
        }

        final Map<String, EntraIdAuthenticator.StateData> stateMap = authenticator.getStateMap(session);
        assertEquals(3, stateMap.size());
        // The most recent attempts are the ones a user can still complete.
        assertTrue(stateMap.containsKey("state-19"));
        assertTrue(stateMap.containsKey("state-18"));
        assertTrue(stateMap.containsKey("state-17"));
    }

    @Test
    public void test_getStateMap_migratesALegacyHashMap() {
        // A session created before this change holds a plain HashMap under the same key.
        final EntraIdAuthenticator authenticator = newAuthenticatorWithControlledClock();
        final HttpSession session = getMockRequest().getSession();
        final Map<String, EntraIdAuthenticator.StateData> legacy = new HashMap<>();
        legacy.put("legacy-state", new EntraIdAuthenticator.StateData("legacy-nonce", clock.get()));
        session.setAttribute("entraidStates", legacy);

        final Map<String, EntraIdAuthenticator.StateData> stateMap = authenticator.getStateMap(session);

        assertTrue(stateMap instanceof ConcurrentHashMap);
        assertEquals("legacy-nonce", stateMap.get("legacy-state").getNonce());
        // The migrated map is the one stored back on the session, so later writes are not lost.
        assertSame(stateMap, session.getAttribute("entraidStates"));
    }

    @Test
    public void test_getStateMap_isCreatedOnceUnderConcurrentAccess() throws Exception {
        final EntraIdAuthenticator authenticator = newAuthenticatorWithControlledClock();
        final HttpSession session = getMockRequest().getSession();
        final int threads = 8;
        final CountDownLatch start = new CountDownLatch(1);
        final List<Map<String, EntraIdAuthenticator.StateData>> seen = Collections.synchronizedList(new ArrayList<>());
        final List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            final Thread t = new Thread(() -> {
                try {
                    start.await();
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                seen.add(authenticator.getStateMap(session));
            });
            workers.add(t);
            t.start();
        }
        start.countDown();
        for (final Thread t : workers) {
            t.join(10000L);
        }

        assertEquals(threads, seen.size());
        // Every caller must share one map, otherwise a state stored by one request is invisible
        // to the request that has to validate it.
        seen.forEach(m -> assertSame(seen.get(0), m));
    }

    @Test
    public void test_removeStateFromSession_stillDropsExpiredStates() {
        final EntraIdAuthenticator authenticator = newAuthenticatorWithControlledClock();
        final HttpSession session = getMockRequest().getSession();
        authenticator.storeStateInSession(session, "state-1", "nonce-1");

        clock.addAndGet(3601L * 1000L);

        assertNull(authenticator.removeStateFromSession(session, "state-1"));
        assertEquals(0, authenticator.getStateMap(session).size());
    }

    @Test
    public void test_removeStateFromSession_returnsAndConsumesALiveState() {
        final EntraIdAuthenticator authenticator = newAuthenticatorWithControlledClock();
        final HttpSession session = getMockRequest().getSession();
        authenticator.storeStateInSession(session, "state-1", "nonce-1");

        final EntraIdAuthenticator.StateData stateData = authenticator.removeStateFromSession(session, "state-1");

        assertNotNull(stateData);
        assertEquals("nonce-1", stateData.getNonce());
        // A state is single use.
        assertNull(authenticator.removeStateFromSession(session, "state-1"));
    }

    /**
     * An authenticator whose Graph access is replaced by a scripted one, so the caching and
     * recursion around it can be exercised without a tenant.
     */
    private static class ScriptedAuthenticator extends EntraIdAuthenticator {
        private final Map<String, String[]> parents = new HashMap<>();
        private final List<String> lookups = new ArrayList<>();
        private final Set<String> failing = new HashSet<>();

        @Override
        protected String[] getMemberGroupIds(final EntraIdUser user, final String id) throws IOException {
            lookups.add(id);
            if (failing.contains(id)) {
                throw new IOException("simulated Graph failure for " + id);
            }
            return parents.getOrDefault(id, new String[0]);
        }

        @Override
        protected void processGroup(final EntraIdUser user, final List<String> groupList, final List<String> roleList, final String id) {
            groupList.add(id);
        }
    }

    private ScriptedAuthenticator newScriptedAuthenticator() {
        final ScriptedAuthenticator authenticator = new ScriptedAuthenticator();
        authenticator.groupCache = CacheBuilder.newBuilder().build();
        return authenticator;
    }

    @Test
    public void test_getParentGroup_cachesASuccessfulLookup() {
        final ScriptedAuthenticator authenticator = newScriptedAuthenticator();
        authenticator.parents.put("group-a", new String[] { "group-b" });

        final Pair<String[], String[]> first = authenticator.getParentGroup(null, "group-a", 0);
        final Pair<String[], String[]> second = authenticator.getParentGroup(null, "group-a", 0);

        assertEquals(1, first.getFirst().length);
        assertEquals("group-b", first.getFirst()[0]);
        assertEquals(1, second.getFirst().length);
        // "group-a" is looked up once; "group-b" is walked once while loading it.
        assertEquals(1, authenticator.lookups.stream().filter("group-a"::equals).count());
    }

    @Test
    public void test_getParentGroup_doesNotCacheAFailedLookup() {
        // A throttled or briefly unreachable Graph used to leave an empty result in the cache,
        // so the user silently lost their parent-group permissions for the whole cache TTL.
        final ScriptedAuthenticator authenticator = newScriptedAuthenticator();
        authenticator.failing.add("group-a");

        final Pair<String[], String[]> failed = authenticator.getParentGroup(null, "group-a", 0);
        assertEquals(0, failed.getFirst().length);
        assertNull(authenticator.groupCache.getIfPresent("group-a"));

        authenticator.failing.remove("group-a");
        authenticator.parents.put("group-a", new String[] { "group-b" });

        final Pair<String[], String[]> recovered = authenticator.getParentGroup(null, "group-a", 0);
        assertEquals(1, recovered.getFirst().length);
        assertEquals("group-b", recovered.getFirst()[0]);
    }

    @Test
    public void test_getParentGroup_cachesAGenuineEmptyResult() {
        // A group with no parents is a real answer, not a transient failure, so it is cached.
        // getMemberGroupIds maps Graph's Request_ResourceNotFound onto this same empty result.
        final ScriptedAuthenticator authenticator = newScriptedAuthenticator();

        final Pair<String[], String[]> result = authenticator.getParentGroup(null, "group-a", 0);

        assertEquals(0, result.getFirst().length);
        assertNotNull(authenticator.groupCache.getIfPresent("group-a"));
    }

    @Test
    public void test_getParentGroup_doesNotRecurseOnceTheParentWasResolved() {
        // Characterisation: processGroup() unconditionally adds the id it was asked about, so the
        // `!groupList.contains(value)` guard in loadParentGroup is false on every success path and
        // the nested-group recursion never runs. maxGroupDepth therefore has no effect here. This
        // is consistent with Graph's getMemberGroups already being transitive, but it means the
        // recursion is not what resolves nested groups -- pinning it so a change is deliberate.
        final ScriptedAuthenticator authenticator = newScriptedAuthenticator();
        authenticator.parents.put("group-a", new String[] { "group-b" });
        authenticator.parents.put("group-b", new String[] { "group-c" });

        final Pair<String[], String[]> result = authenticator.getParentGroup(null, "group-a", 0);

        assertEquals(1, result.getFirst().length);
        assertEquals("group-b", result.getFirst()[0]);
        assertFalse(authenticator.lookups.contains("group-b"));
    }

    @Test
    public void test_getParentGroup_survivesAnUncheckedFailureFromTheLoader() {
        // Guava wraps an unchecked exception from the loader in UncheckedExecutionException,
        // which is not an ExecutionException, so it used to escape the catch entirely. The Graph
        // JSON parser throws CurlException, a RuntimeException, on a non-JSON error body.
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator() {
            @Override
            protected String[] getMemberGroupIds(final EntraIdUser user, final String id) {
                throw new IllegalStateException("non-JSON error body");
            }
        };
        authenticator.groupCache = CacheBuilder.newBuilder().build();

        final Pair<String[], String[]> result = authenticator.getParentGroup(null, "group-a", 0);

        assertEquals(0, result.getFirst().length);
        assertEquals(0, result.getSecond().length);
        assertNull(authenticator.groupCache.getIfPresent("group-a"));
    }

    @Test
    public void test_applyGraphThrottle_honoursTheRetryAfterGraphSent() {
        // curl4j does not throw on a non-2xx response -- CurlRequest hands back the error stream --
        // so a Graph 429 arrives as an ordinary parsed body and the status code is the only place
        // the throttling is visible.
        final EntraIdAuthenticator authenticator = newAuthenticatorWithControlledClock();
        final CurlResponse response = new CurlResponse();
        response.setHttpStatusCode(429);
        response.setHeaders(Map.of("Retry-After", List.of("120")));

        authenticator.applyGraphThrottle(response);

        assertEquals(clock.get() + 120_000L, authenticator.graphThrottledUntil);
        assertTrue(authenticator.isGraphThrottled());

        clock.addAndGet(120_000L);
        assertFalse("the backoff has to lapse on its own", authenticator.isGraphThrottled());
    }

    @Test
    public void test_applyGraphThrottle_alsoBacksOffOnServiceUnavailable() {
        final EntraIdAuthenticator authenticator = newAuthenticatorWithControlledClock();
        final CurlResponse response = new CurlResponse();
        response.setHttpStatusCode(503);

        authenticator.applyGraphThrottle(response);

        // No Retry-After: the default backoff applies rather than none at all.
        assertEquals(clock.get() + 60_000L, authenticator.graphThrottledUntil);
    }

    @Test
    public void test_applyGraphThrottle_ignoresAnOrdinaryResponse() {
        final EntraIdAuthenticator authenticator = newAuthenticatorWithControlledClock();
        final CurlResponse ok = new CurlResponse();
        ok.setHttpStatusCode(200);
        final CurlResponse forbidden = new CurlResponse();
        forbidden.setHttpStatusCode(403);

        authenticator.applyGraphThrottle(ok);
        authenticator.applyGraphThrottle(forbidden);

        assertEquals(0L, authenticator.graphThrottledUntil);
        assertFalse(authenticator.isGraphThrottled());
    }

    @Test
    public void test_parseRetryAfterSeconds_fallsBackAndStaysBounded() {
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();

        assertEquals(30L, authenticator.parseRetryAfterSeconds(" 30 "));
        assertEquals(60L, authenticator.parseRetryAfterSeconds(null));
        assertEquals(60L, authenticator.parseRetryAfterSeconds(""));
        assertEquals(60L, authenticator.parseRetryAfterSeconds("0"));
        // RFC 9110 also allows an HTTP-date; Graph sends delay-seconds, so this is a fallback.
        assertEquals(60L, authenticator.parseRetryAfterSeconds("Wed, 21 Oct 2026 07:28:00 GMT"));
        // An unbounded value would leave nested groups unresolved for the rest of the day.
        assertEquals(3600L, authenticator.parseRetryAfterSeconds("999999"));
    }

    @Test
    public void test_getParentGroup_skipsTheWalkWhileGraphIsThrottling() {
        // 15.7 cached an empty result for the cache TTL, which #3223 removed because it silently
        // took the parent group permissions away for ten minutes. The backoff replaces it: the
        // walk is skipped while Graph asked us to wait, nothing is written to the cache, and it
        // resumes by itself.
        final ScriptedAuthenticator authenticator = newScriptedAuthenticator();
        ComponentUtil.register(new SystemHelper() {
            @Override
            public long getCurrentTimeAsLong() {
                return clock.get();
            }
        }, "systemHelper");
        authenticator.parents.put("group-a", new String[] { "group-b" });
        authenticator.graphThrottledUntil = clock.get() + 60_000L;

        final Pair<String[], String[]> throttled = authenticator.getParentGroup(null, "group-a", 0);

        assertEquals(0, throttled.getFirst().length);
        assertTrue(authenticator.lookups.isEmpty());
        assertNull(authenticator.groupCache.getIfPresent("group-a"), "a skipped walk must not be cached");

        clock.addAndGet(60_000L);
        final Pair<String[], String[]> recovered = authenticator.getParentGroup(null, "group-a", 0);

        assertEquals(1, recovered.getFirst().length);
        assertEquals("group-b", recovered.getFirst()[0]);
    }

    @Test
    public void test_getParentGroup_stillServesACachedAnswerWhileThrottling() {
        // The backoff exists to stop Graph being called, not to throw away memberships that were
        // already resolved.
        final ScriptedAuthenticator authenticator = newScriptedAuthenticator();
        ComponentUtil.register(new SystemHelper() {
            @Override
            public long getCurrentTimeAsLong() {
                return clock.get();
            }
        }, "systemHelper");
        authenticator.parents.put("group-a", new String[] { "group-b" });
        assertEquals(1, authenticator.getParentGroup(null, "group-a", 0).getFirst().length);

        authenticator.graphThrottledUntil = clock.get() + 60_000L;

        assertEquals(1, authenticator.getParentGroup(null, "group-a", 0).getFirst().length);
    }

    @Test
    public void test_getStateTtl_defaultsToOneHourInSeconds() {
        // removeExpiredStates compares (now - created) / 1000 against this value, so the unit is
        // seconds. The javadoc used to say milliseconds.
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        assertEquals(3600L, authenticator.getStateTtl());
    }

    @Test
    public void test_getStateTtl_fallsBackWhenTheConfiguredValueIsNotANumber() {
        // A typo in conf/system.properties used to fail the login with a NumberFormatException
        // rather than a message anyone could act on.
        ComponentUtil.getFessConfig().setSystemProperty("entraid.state.ttl", "one hour");
        try {
            final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
            assertEquals(3600L, authenticator.getStateTtl());
        } finally {
            ComponentUtil.getFessConfig().setSystemProperty("entraid.state.ttl", "");
        }
    }

    @Test
    public void test_addGroupOrRoleName() {
        EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        List<String> list = new ArrayList<>();

        list.clear();
        authenticator.addGroupOrRoleName(list, "test", true);
        assertEquals(1, list.size());
        assertEquals("test", list.get(0));

        list.clear();
        authenticator.addGroupOrRoleName(list, "test", false);
        assertEquals(1, list.size());
        assertEquals("test", list.get(0));

        list.clear();
        authenticator.addGroupOrRoleName(list, "test@codelibs.org", true);
        assertEquals(2, list.size());
        assertEquals("test@codelibs.org", list.get(0));
        assertEquals("test", list.get(1));

        list.clear();
        authenticator.addGroupOrRoleName(list, "test@codelibs.org", false);
        assertEquals(1, list.size());
        assertEquals("test@codelibs.org", list.get(0));

        list.clear();
        authenticator.addGroupOrRoleName(list, "test@codelibs.org@hoge.com", true);
        assertEquals(2, list.size());
        assertEquals("test@codelibs.org@hoge.com", list.get(0));
        assertEquals("test", list.get(1));

    }

    @Test
    public void test_setGroupCacheExpiry_isHonouredByTheCacheItBuilds() {
        // The setter is what a fess_sso+entraidAuthenticator.xml writes, so it has to reach the
        // builder rather than just a field nothing reads.
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        authenticator.setGroupCacheExpiry(0L);

        final Cache<String, Pair<String[], String[]>> expiringCache = authenticator.createGroupCache();
        expiringCache.put("group-a", new Pair<>(new String[0], new String[0]));
        expiringCache.cleanUp();
        assertNull(expiringCache.getIfPresent("group-a"));

        authenticator.setGroupCacheExpiry(600L);
        final Cache<String, Pair<String[], String[]>> livingCache = authenticator.createGroupCache();
        livingCache.put("group-a", new Pair<>(new String[0], new String[0]));
        livingCache.cleanUp();
        assertNotNull(livingCache.getIfPresent("group-a"));
    }

    @Test
    public void test_getParentGroup_withDepthLimit() {
        EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        authenticator.setMaxGroupDepth(2);

        // Test that depth limit returns empty arrays when depth is exceeded
        // With depth limit set to 2, depth 10 should return empty arrays
        Pair<String[], String[]> result = authenticator.getParentGroup(null, "test-id", 10);
        assertNotNull(result);
        assertEquals(0, result.getFirst().length);
        assertEquals(0, result.getSecond().length);
    }

    @Test
    public void test_getParentGroup_exactlyAtDepthLimit() {
        EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        authenticator.setMaxGroupDepth(5);

        // Test with depth exactly at the limit - should return empty arrays
        Pair<String[], String[]> result = authenticator.getParentGroup(null, "test-id", 5);
        assertNotNull(result);
        assertEquals(0, result.getFirst().length);
        assertEquals(0, result.getSecond().length);
    }

    @Test
    public void test_getParentGroup_oneBeforeDepthLimit() {
        // The sibling tests above pin the depth check returning early. This one has to prove the
        // opposite -- that one below the limit the lookup really is attempted -- so it needs a
        // cache and a scripted Graph. Without them getParentGroup throws NullPointerException on
        // the null groupCache, which the version of this test inherited from 15.7 swallowed along
        // with everything else.
        final ScriptedAuthenticator authenticator = newScriptedAuthenticator();
        authenticator.setMaxGroupDepth(5);
        authenticator.parents.put("test-id", new String[] { "parent-id" });

        final Pair<String[], String[]> result = authenticator.getParentGroup(null, "test-id", 4);

        assertEquals(1, result.getFirst().length);
        assertEquals("parent-id", result.getFirst()[0]);
        assertTrue(authenticator.lookups.contains("test-id"));
    }

    @Test
    public void test_processParentGroup_callsOverloadWithDepth() {
        // maxGroupDepth is 1, so the walk happens only if the overload starts at depth 0. Passing
        // anything else through would make this silently do nothing.
        final ScriptedAuthenticator authenticator = newScriptedAuthenticator();
        authenticator.setMaxGroupDepth(1);
        authenticator.parents.put("test-id", new String[] { "parent-id" });
        final List<String> groupList = new ArrayList<>();
        final List<String> roleList = new ArrayList<>();

        authenticator.processParentGroup(null, groupList, roleList, "test-id");

        assertTrue(authenticator.lookups.contains("test-id"));
        assertEquals(1, groupList.size());
        assertEquals("parent-id", groupList.get(0));
        assertEquals(0, roleList.size());
    }

    @Test
    public void test_processParentGroup_respectsDepthLimit() {
        EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        authenticator.setMaxGroupDepth(2);

        List<String> groupList = new ArrayList<>();
        List<String> roleList = new ArrayList<>();

        // Test with depth exceeding limit - should return immediately
        authenticator.processParentGroup(null, groupList, roleList, "test-id", 5);

        // Lists should remain empty as depth limit prevents processing
        assertEquals(0, groupList.size());
        assertEquals(0, roleList.size());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void test_setUseV2Endpoint_isKeptAsANoOpForOutOfTreeDiFiles() {
        // Removing the public setter would break a fess_sso+entraidAuthenticator.xml that still
        // sets the property, so it stays -- but it no longer changes anything.
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();

        authenticator.setUseV2Endpoint(true);
        final String afterTrue = authenticator.getAuthUrl(getMockRequest());
        authenticator.setUseV2Endpoint(false);
        final String afterFalse = authenticator.getAuthUrl(getMockRequest());

        assertTrue(afterTrue.contains("/oauth2/v2.0/authorize?"));
        assertTrue(afterFalse.contains("/oauth2/v2.0/authorize?"));
    }

    @Test
    public void test_defaultMaxGroupDepth() {
        EntraIdAuthenticator authenticator = new EntraIdAuthenticator();

        // Test that default max depth (10) prevents deep recursion
        // Depth 100 should exceed default and return empty
        Pair<String[], String[]> result = authenticator.getParentGroup(null, "test-id", 100);
        assertNotNull(result);
        assertEquals(0, result.getFirst().length);
        assertEquals(0, result.getSecond().length);
    }

    // ========== Tests for lazy loading implementation ==========

    /**
     * Test that processDirectMemberOf method exists with correct signature.
     */
    @Test
    public void test_processDirectMemberOf_methodExists() throws Exception {
        Method method = EntraIdAuthenticator.class.getDeclaredMethod("processDirectMemberOf", EntraIdUser.class, List.class, List.class,
                List.class, String.class);
        assertNotNull(method, "processDirectMemberOf method should exist");
    }

    /**
     * Test that scheduleParentGroupLookup method exists with correct signature.
     */
    @Test
    public void test_scheduleParentGroupLookup_methodExists() throws Exception {
        Method method = EntraIdAuthenticator.class.getDeclaredMethod("scheduleParentGroupLookup", EntraIdUser.class, List.class, List.class,
                List.class);
        assertNotNull(method, "scheduleParentGroupLookup method should exist");
    }

    /**
     * Test that updateMemberOf still exists and is public.
     */
    @Test
    public void test_updateMemberOf_methodExists() throws Exception {
        Method method = EntraIdAuthenticator.class.getMethod("updateMemberOf", EntraIdUser.class);
        assertNotNull(method, "updateMemberOf method should exist");
        assertTrue("updateMemberOf should be public", java.lang.reflect.Modifier.isPublic(method.getModifiers()));
    }

    /**
     * Test processDirectMemberOf collects group IDs for parent lookup.
     */
    @Test
    public void test_processDirectMemberOf_collectsGroupIds() throws Exception {
        // The parsing loop had no coverage at all: the version of this test inherited from 15.7
        // pointed at an unreachable URL and then asserted that three lists it had just created
        // were not null.
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        final EntraIdUser user = newUserWithoutGraph();
        final List<String> groupList = new ArrayList<>();
        final List<String> roleList = new ArrayList<>();
        final List<String> groupIdsForParentLookup = new ArrayList<>();

        try (GraphStub graph = new GraphStub(200, Map.of(),
                "{\"value\":[{\"@odata.type\":\"#microsoft.graph.group\",\"id\":\"group-1\",\"mail\":\"sales@contoso.com\"},"
                        + "{\"@odata.type\":\"#microsoft.graph.directoryRole\",\"id\":\"role-1\"}]}")) {
            assertTrue(authenticator.processDirectMemberOf(user, groupList, roleList, groupIdsForParentLookup, graph.url()));
        }

        // entraid.permission.fields defaults to "mail" and entraid.use.ds to true, so the mail
        // address contributes both itself and its local part.
        assertEquals(List.of("group-1", "sales@contoso.com", "sales"), groupList);
        assertEquals(List.of("role-1"), roleList);
        // Only groups are walked for parents; a directory role has none.
        assertEquals(List.of("group-1"), groupIdsForParentLookup);
    }

    /**
     * Test that the parent group lookup is scheduled only when there is something to look up.
     */
    @Test
    public void test_updateMemberOf_schedulesTheParentLookupOnlyWhenThereAreGroupIds() {
        final TestableEntraIdAuthenticator empty = new TestableEntraIdAuthenticator();
        empty.updateMemberOf(newUserWithoutGraph());
        assertFalse(empty.scheduleParentGroupLookupCalled.get());
        assertTrue(empty.processDirectMemberOfCalled.get());

        final TestableEntraIdAuthenticator collecting = new TestableEntraIdAuthenticator() {
            @Override
            protected boolean processDirectMemberOf(final EntraIdUser user, final List<String> groupList, final List<String> roleList,
                    final List<String> groupIdsForParentLookup, final String url) {
                super.processDirectMemberOf(user, groupList, roleList, groupIdsForParentLookup, url);
                groupIdsForParentLookup.add("group-1");
                return true;
            }
        };
        collecting.updateMemberOf(newUserWithoutGraph());
        assertTrue(collecting.scheduleParentGroupLookupCalled.get());
    }

    /**
     * Test that default groups and roles are read from configuration, new key first.
     */
    @Test
    public void test_defaultGroupsAndRoles_readTheLegacyAzureAdKeys() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        try {
            assertEquals(Collections.emptyList(), authenticator.getDefaultGroupList());
            assertEquals(Collections.emptyList(), authenticator.getDefaultRoleList());

            // Blank entries are dropped and the rest trimmed, so a hand-edited list still works.
            fessConfig.setSystemProperty("aad.default.groups", " everyone , , guests ");
            fessConfig.setSystemProperty("aad.default.roles", " guest ");
            assertEquals(List.of("everyone", "guests"), authenticator.getDefaultGroupList());
            assertEquals(List.of("guest"), authenticator.getDefaultRoleList());

            // The renamed key wins over the legacy one.
            fessConfig.setSystemProperty("entraid.default.groups", "staff");
            fessConfig.setSystemProperty("entraid.default.roles", "member");
            assertEquals(List.of("staff"), authenticator.getDefaultGroupList());
            assertEquals(List.of("member"), authenticator.getDefaultRoleList());
        } finally {
            fessConfig.setSystemProperty("aad.default.groups", "");
            fessConfig.setSystemProperty("aad.default.roles", "");
            fessConfig.setSystemProperty("entraid.default.groups", "");
            fessConfig.setSystemProperty("entraid.default.roles", "");
        }
    }

    /**
     * Test that the client application settings fall back to the legacy Azure AD keys.
     */
    @Test
    public void test_clientConfiguration_fallsBackToTheLegacyAzureAdKeys() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        try {
            fessConfig.setSystemProperty("aad.client.id", "legacy-client-id");
            fessConfig.setSystemProperty("aad.client.secret", "legacy-secret");
            fessConfig.setSystemProperty("aad.tenant", "legacy-tenant");
            fessConfig.setSystemProperty("aad.state.ttl", "120");
            fessConfig.setSystemProperty("aad.authority", "https://login.microsoftonline.us/");

            assertEquals("legacy-client-id", authenticator.getClientId());
            assertEquals("legacy-secret", authenticator.getClientSecret());
            assertEquals("legacy-tenant", authenticator.getTenant());
            assertEquals(120L, authenticator.getStateTtl());
            assertEquals("https://login.microsoftonline.us/", authenticator.getAuthority());
        } finally {
            fessConfig.setSystemProperty("aad.client.id", "");
            fessConfig.setSystemProperty("aad.client.secret", "");
            fessConfig.setSystemProperty("aad.tenant", "");
            fessConfig.setSystemProperty("aad.state.ttl", "");
            fessConfig.setSystemProperty("aad.authority", "");
        }
    }

    /**
     * Test that processParentGroup handles null user gracefully when depth limit is reached.
     */
    @Test
    public void test_processParentGroup_nullUser_depthExceeded() {
        EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        authenticator.setMaxGroupDepth(5);

        List<String> groupList = new ArrayList<>();
        List<String> roleList = new ArrayList<>();

        // With depth >= maxGroupDepth, should return immediately without error
        authenticator.processParentGroup(null, groupList, roleList, "test-id", 10);

        assertEquals("groupList should remain empty", 0, groupList.size());
        assertEquals("roleList should remain empty", 0, roleList.size());
    }

    /**
     * Test addGroupOrRoleName with null value handling.
     */
    @Test
    public void test_addGroupOrRoleName_withEmptyValue() {
        EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        List<String> list = new ArrayList<>();

        // Empty string should still be added
        authenticator.addGroupOrRoleName(list, "", true);
        assertEquals(1, list.size());
        assertEquals("", list.get(0));
    }

    /**
     * Testable subclass of EntraIdAuthenticator for testing purposes.
     */
    private static class TestableEntraIdAuthenticator extends EntraIdAuthenticator {
        AtomicBoolean scheduleParentGroupLookupCalled = new AtomicBoolean(false);
        AtomicBoolean processDirectMemberOfCalled = new AtomicBoolean(false);

        @Override
        protected void scheduleParentGroupLookup(EntraIdUser user, List<String> initialGroups, List<String> initialRoles,
                List<String> groupIds) {
            scheduleParentGroupLookupCalled.set(true);
            // Don't call super to avoid actual scheduling in tests
        }

        @Override
        protected boolean processDirectMemberOf(EntraIdUser user, List<String> groupList, List<String> roleList,
                List<String> groupIdsForParentLookup, String url) {
            processDirectMemberOfCalled.set(true);
            // Don't call super to avoid actual API calls in tests
            return true;
        }

        // Expose protected methods for testing
        @Override
        public List<String> getDefaultGroupList() {
            return super.getDefaultGroupList();
        }

        @Override
        public List<String> getDefaultRoleList() {
            return super.getDefaultRoleList();
        }
    }

    // ===================================================================================
    //                                                        Regressions guarded from 15.7
    //                                                        ==============================

    @Test
    public void test_updateMemberOf_keepsTheResolvedGroupsWhenGraphReportsAnError() {
        // godHandPrologue refreshes the user on every action request, so updateMemberOf can run
        // long after login. A throttled or newly unauthorised Graph used to leave the user with
        // the configured defaults alone, silently taking away every permission they logged in
        // with, until some later call happened to succeed.
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator() {
            @Override
            protected boolean processDirectMemberOf(final EntraIdUser user, final List<String> groupList, final List<String> roleList,
                    final List<String> groupIdsForParentLookup, final String url) {
                return false;
            }
        };
        final EntraIdUser user = newUserWithoutGraph();
        user.setGroups(new String[] { "group-a", "group-b" });
        user.setRoles(new String[] { "role-a" });

        authenticator.updateMemberOf(user);

        assertEquals(2, user.getGroupNames().length);
        assertEquals("group-a", user.getGroupNames()[0]);
        assertEquals(1, user.getRoleNames().length);
    }

    @Test
    public void test_updateMemberOf_failsTheLoginWhenTheFirstLookupFailsAndMembershipIsRequired() {
        // At login there is nothing to keep, and curl4j does not throw on a non-2xx response, so a
        // Graph 429/403/401 on /me/memberOf parses cleanly, logs a WARN and hands the user a
        // working session carrying the configured defaults alone -- silently truncated search
        // results for its whole lifetime, with refresh() not retrying until the token is nearly
        // expired. A deployment that would rather hand out no session at all opts in to this.
        //
        // LoginFailureException specifically: TypicalLoginAssist calls this resolver directly
        // without wrapping anything, and SsoAction only catches LoginFailureException around
        // loginRedirect(), so any other type would reach the generic error page instead of the
        // standard SSO error message.
        final EntraIdAuthenticator authenticator = newAuthenticatorWhoseLookupFails();
        final EntraIdUser user = newUserWithoutGraph();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        try {
            fessConfig.setSystemProperty("entraid.require.membership", "true");
            fessConfig.setSystemProperty("entraid.default.groups", "everyone");

            try {
                authenticator.updateMemberOf(user);
                fail("expected LoginFailureException");
            } catch (final LoginFailureException e) {
                assertTrue(e.getMessage(), e.getMessage().contains(user.getName()));
            }

            assertNull(user.getGroupNames(), "a half-permissioned session must not be handed out");
        } finally {
            fessConfig.setSystemProperty("entraid.require.membership", "");
            fessConfig.setSystemProperty("entraid.default.groups", "");
        }
    }

    @Test
    public void test_updateMemberOf_degradesToTheDefaultsWhenTheFirstLookupFails() {
        // The default, and what 15.7 did. Failing the login instead turns a Graph 429/503, a
        // transport failure, the 15.8 graphConnectTimeout/graphReadTimeout expiring, or a
        // GroupMember.Read.All permission that was never granted, into a refusal for every user
        // in the tenant for as long as the condition lasts.
        final EntraIdAuthenticator authenticator = newAuthenticatorWhoseLookupFails();
        final EntraIdUser user = newUserWithoutGraph();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final LogCapturingAppender logs = LogCapturingAppender.attach(EntraIdAuthenticator.class);
        try {
            fessConfig.setSystemProperty("entraid.default.groups", "everyone");
            fessConfig.setSystemProperty("entraid.default.roles", "guest");

            authenticator.updateMemberOf(user);

            assertEquals(1, user.getGroupNames().length);
            assertEquals("everyone", user.getGroupNames()[0]);
            assertEquals(1, user.getRoleNames().length);
            assertEquals("guest", user.getRoleNames()[0]);
            // Degrading silently would leave the operator with a tenant of under-permissioned
            // sessions and nothing in the log to explain them.
            assertTrue(logs.warnings().toString(),
                    logs.warnings().stream().anyMatch(m -> m.contains(user.getName()) && m.contains("entraid.require.membership")));
        } finally {
            logs.detach();
            fessConfig.setSystemProperty("entraid.default.groups", "");
            fessConfig.setSystemProperty("entraid.default.roles", "");
        }
    }

    @Test
    public void test_updateMemberOf_degradesWhenRequireMembershipIsPresentButEmpty() {
        // getSystemProperty substitutes a default only when the key is absent, and the admin
        // screen stores an empty string rather than removing a key, so "" has to mean the default
        // rather than being handed to Boolean.parseBoolean by accident.
        final EntraIdAuthenticator authenticator = newAuthenticatorWhoseLookupFails();
        final EntraIdUser user = newUserWithoutGraph();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        try {
            fessConfig.setSystemProperty("entraid.require.membership", "");
            fessConfig.setSystemProperty("entraid.default.groups", "everyone");

            authenticator.updateMemberOf(user);

            assertEquals(1, user.getGroupNames().length);
            assertEquals("everyone", user.getGroupNames()[0]);
        } finally {
            fessConfig.setSystemProperty("entraid.require.membership", "");
            fessConfig.setSystemProperty("entraid.default.groups", "");
        }
    }

    @Test
    public void test_updateMemberOf_keepsWhatWasCollectedBeforeTheFailure() {
        // processDirectMemberOf pages through /me/memberOf and adds as it goes, so a failure on a
        // later page leaves the earlier ones in the list. The result is always a superset of the
        // configured defaults, which are seeded before the lookup runs.
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator() {
            @Override
            protected boolean processDirectMemberOf(final EntraIdUser user, final List<String> groupList, final List<String> roleList,
                    final List<String> groupIdsForParentLookup, final String url) {
                groupList.add("group-from-the-first-page");
                return false;
            }
        };
        final EntraIdUser user = newUserWithoutGraph();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        try {
            fessConfig.setSystemProperty("entraid.default.groups", "everyone");

            authenticator.updateMemberOf(user);

            assertEquals(List.of("everyone", "group-from-the-first-page"), List.of(user.getGroupNames()));
        } finally {
            fessConfig.setSystemProperty("entraid.default.groups", "");
        }
    }

    @Test
    public void test_isRequireMembership_readsTheKeyAndHasNoLegacyAzureAdAlias() {
        // The key is new in 15.8, so no aad.* value can predate it. Reading one would let a key
        // nobody ever wrote for this purpose start refusing logins.
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        try {
            assertFalse(authenticator.isRequireMembership());

            fessConfig.setSystemProperty("entraid.require.membership", " TRUE ");
            assertTrue(authenticator.isRequireMembership());

            fessConfig.setSystemProperty("entraid.require.membership", "false");
            assertFalse(authenticator.isRequireMembership());

            fessConfig.setSystemProperty("entraid.require.membership", "");
            fessConfig.setSystemProperty("aad.require.membership", "true");
            assertFalse(authenticator.isRequireMembership());
        } finally {
            fessConfig.setSystemProperty("entraid.require.membership", "");
            fessConfig.setSystemProperty("aad.require.membership", "");
        }
    }

    /** An authenticator whose direct membership lookup always reports a failure. */
    private EntraIdAuthenticator newAuthenticatorWhoseLookupFails() {
        return new EntraIdAuthenticator() {
            @Override
            protected boolean processDirectMemberOf(final EntraIdUser user, final List<String> groupList, final List<String> roleList,
                    final List<String> groupIdsForParentLookup, final String url) {
                return false;
            }
        };
    }

    @Test
    public void test_updateMemberOf_appliesTheDefaultsWhenTheLookupFindsNoMemberships() {
        // A user who genuinely belongs to nothing is a successful answer, not a failure, and the
        // configured defaults are exactly what they are meant to get.
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator() {
            @Override
            protected boolean processDirectMemberOf(final EntraIdUser user, final List<String> groupList, final List<String> roleList,
                    final List<String> groupIdsForParentLookup, final String url) {
                return true;
            }
        };
        final EntraIdUser user = newUserWithoutGraph();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        try {
            fessConfig.setSystemProperty("entraid.default.groups", "everyone");
            fessConfig.setSystemProperty("entraid.default.roles", "guest");

            authenticator.updateMemberOf(user);

            assertEquals(1, user.getGroupNames().length);
            assertEquals("everyone", user.getGroupNames()[0]);
            assertEquals(1, user.getRoleNames().length);
            assertEquals("guest", user.getRoleNames()[0]);
        } finally {
            fessConfig.setSystemProperty("entraid.default.groups", "");
            fessConfig.setSystemProperty("entraid.default.roles", "");
        }
    }

    @Test
    public void test_processDirectMemberOf_reportsATransportFailureInsteadOfLettingItEscape() {
        // curl4j reports a transport failure as CurlException, which is unchecked, so
        // catch (IOException) alone let it escape as an unhandled RuntimeException -- out of the
        // EntraIdUser constructor and past every caller. The same one-line widening is applied to
        // processGroup, whose Graph URL is not injectable and so is covered by inspection only.
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        authenticator.setGraphConnectTimeout(500);
        authenticator.setGraphReadTimeout(500);
        final EntraIdUser user = newUserWithoutGraph();
        final int deadPort;
        try (ServerSocket socket = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
            deadPort = socket.getLocalPort();
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }

        final boolean resolved = authenticator.processDirectMemberOf(user, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                "http://127.0.0.1:" + deadPort + "/v1.0/me/memberOf");

        assertFalse(resolved);
    }

    @Test
    public void test_processDirectMemberOf_reportsAHostileBodyInsteadOfLettingItEscape() throws Exception {
        // "value" is cast to List<Map<String, Object>>, so a body whose value is an object throws
        // ClassCastException. catch (IOException | CurlException) did not cover it, and it escaped
        // updateMemberOf and the EntraIdUser constructor to the generic error page instead of the
        // outcome entraid.require.membership selects.
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        final EntraIdUser user = newUserWithoutGraph();

        try (GraphStub graph = new GraphStub(200, Map.of(), "{\"value\":{\"id\":\"group-1\"}}")) {
            assertFalse(authenticator.processDirectMemberOf(user, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), graph.url()));
        }
    }

    @Test
    public void test_processDirectMemberOf_recordsTheBackoffAThrottledGraphAskedFor() throws Exception {
        // applyGraphThrottle had exactly one call site, inside getMemberGroupIds -- the
        // asynchronous parent group walk. A 429 answered to the synchronous login lookup was
        // therefore neither recorded nor honoured, and the walk went on issuing one request, and
        // one stack trace, per group against a Graph that had already asked us to wait.
        final EntraIdAuthenticator authenticator = newAuthenticatorWithControlledClock();
        final EntraIdUser user = newUserWithoutGraph();

        try (GraphStub graph = new GraphStub(429, Map.of("Retry-After", "120"), "{\"error\":{\"code\":\"TooManyRequests\"}}")) {
            assertFalse(authenticator.processDirectMemberOf(user, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), graph.url()));
        }

        assertEquals(clock.get() + 120_000L, authenticator.graphThrottledUntil);
        assertTrue(authenticator.isGraphThrottled());
    }

    @Test
    public void test_processDirectMemberOf_stillAsksWhileGraphIsThrottling() throws Exception {
        // The backoff keeps the asynchronous parent group walk off a throttled Graph. A login has
        // no such luxury: skipping the lookup would hand out the configured defaults alone for the
        // whole backoff, so it has to try -- the tenant may well have recovered already.
        final EntraIdAuthenticator authenticator = newAuthenticatorWithControlledClock();
        final EntraIdUser user = newUserWithoutGraph();
        authenticator.graphThrottledUntil = clock.get() + 60_000L;
        assertTrue(authenticator.isGraphThrottled());
        final List<String> groupList = new ArrayList<>();

        try (GraphStub graph =
                new GraphStub(200, Map.of(), "{\"value\":[{\"@odata.type\":\"#microsoft.graph.group\",\"id\":\"group-1\"}]}")) {
            assertTrue(authenticator.processDirectMemberOf(user, groupList, new ArrayList<>(), new ArrayList<>(), graph.url()));
        }

        assertEquals(List.of("group-1"), groupList);
    }

    /**
     * A local stand-in for the Microsoft Graph endpoint. curl4j does not throw on a non-2xx
     * response, so the status code and the headers are only observable through a real request.
     */
    private static final class GraphStub implements AutoCloseable {
        private final HttpServer server;
        private final String url;

        GraphStub(final int statusCode, final Map<String, String> headers, final String body) throws IOException {
            server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
            server.createContext("/v1.0/me/memberOf", exchange -> {
                final byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                headers.forEach((name, value) -> exchange.getResponseHeaders().set(name, value));
                exchange.sendResponseHeaders(statusCode, bytes.length);
                try (OutputStream out = exchange.getResponseBody()) {
                    out.write(bytes);
                }
            });
            server.start();
            url = "http://" + server.getAddress().getAddress().getHostAddress() + ":" + server.getAddress().getPort() + "/v1.0/me/memberOf";
        }

        String url() {
            return url;
        }

        @Override
        public void close() {
            server.stop(0);
        }
    }

    @Test
    public void test_updateMemberOf_replacesTheGroupsWhenGraphAnswers() {
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator() {
            @Override
            protected boolean processDirectMemberOf(final EntraIdUser user, final List<String> groupList, final List<String> roleList,
                    final List<String> groupIdsForParentLookup, final String url) {
                groupList.add("group-c");
                return true;
            }
        };
        final EntraIdUser user = newUserWithoutGraph();
        user.setGroups(new String[] { "group-a" });

        authenticator.updateMemberOf(user);

        assertEquals(1, user.getGroupNames().length);
        assertEquals("group-c", user.getGroupNames()[0]);
    }

    /**
     * Builds a user without letting its constructor reach Microsoft Graph. The tests below drive
     * {@code updateMemberOf} directly, so the registered component only has to stay quiet.
     */
    private EntraIdUser newUserWithoutGraph() {
        ComponentUtil.register(new EntraIdAuthenticator() {
            @Override
            public void updateMemberOf(final EntraIdUser user) {
                // keep the constructor off Microsoft Graph
            }
        }, EntraIdAuthenticator.class.getCanonicalName());
        return new EntraIdCredential(new TestAuthenticationResult(new TestAccount())).getUser();
    }

    @Test
    public void test_toMemberGroupIds_treatsADeniedPermissionAsAnAnswerSoItCanBeCached() throws Exception {
        // A Graph permission that was never granted will not appear within the cache TTL.
        // Throwing left nothing cached, so every login re-issued one failing request, and one
        // stack trace, per direct group.
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        final Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("error", Map.of("code", "Authorization_RequestDenied", "message", "Insufficient privileges"));

        assertEquals(0, authenticator.toMemberGroupIds(contentMap, "group-a").length);
    }

    @Test
    public void test_toMemberGroupIds_treatsAMissingGroupAsAnAnswer() throws Exception {
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        final Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("error", Map.of("code", "Request_ResourceNotFound", "message", "not found"));

        assertEquals(0, authenticator.toMemberGroupIds(contentMap, "group-a").length);
    }

    @Test
    public void test_toMemberGroupIds_throwsOnATransientFailure() {
        // Throttling must stay uncached so the parents are resolved on the next attempt.
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        final Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("error", Map.of("code", "TooManyRequests", "message", "throttled"));

        try {
            authenticator.toMemberGroupIds(contentMap, "group-a");
            fail("a throttled response must not be mistaken for an answer");
        } catch (final IOException e) {
            assertTrue(e.getMessage().contains("group-a"));
        }
    }

    @Test
    public void test_toMemberGroupIds_throwsWhenTheErrorIsNotAnObject() {
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        final Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("error", "invalid_grant");

        try {
            authenticator.toMemberGroupIds(contentMap, "group-a");
            fail("an unparsable error must not be mistaken for an answer");
        } catch (final IOException e) {
            assertTrue(e.getMessage().contains("group-a"));
        }
    }

    @Test
    public void test_toMemberGroupIds_returnsTheValues() throws Exception {
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        final Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("value", List.of("parent-a", "parent-b"));

        assertEquals(2, authenticator.toMemberGroupIds(contentMap, "group-a").length);
    }

    @Test
    public void test_refresh_doesNotTouchGraphWhileTheTokenIsStillFresh() {
        // FessBaseAction.godHandPrologue calls refresh() on every action request. Once the MSAL4J
        // application became shared, acquireTokenSilently started succeeding, and every success
        // ran updateMemberOf -- a synchronous Microsoft Graph call on the request thread.
        final AtomicBoolean touched = new AtomicBoolean(false);
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator() {
            @Override
            public void updateMemberOf(final EntraIdUser user) {
                touched.set(true);
            }

            @Override
            public IAuthenticationResult refreshTokenSilently(final EntraIdUser user) {
                touched.set(true);
                return null;
            }
        };
        ComponentUtil.register(authenticator, EntraIdAuthenticator.class.getCanonicalName());
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        final EntraIdUser user =
                new EntraIdCredential(new TestAuthenticationResult(new TestAccount(), new Date(now + 60 * 60 * 1000L))).getUser();
        touched.set(false);

        assertTrue(user.refresh());
        assertFalse(touched.get());
    }

    @Test
    public void test_refresh_acquiresSilentlyOnceTheTokenIsCloseToExpiring() {
        final AtomicBoolean refreshed = new AtomicBoolean(false);
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator() {
            @Override
            public void updateMemberOf(final EntraIdUser user) {
                // keep the constructor and the refresh off Microsoft Graph
            }

            @Override
            public IAuthenticationResult refreshTokenSilently(final EntraIdUser user) {
                refreshed.set(true);
                return null;
            }
        };
        ComponentUtil.register(authenticator, EntraIdAuthenticator.class.getCanonicalName());
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        final EntraIdUser user =
                new EntraIdCredential(new TestAuthenticationResult(new TestAccount(), new Date(now + 30 * 1000L))).getUser();

        assertTrue(user.refresh());
        assertTrue(refreshed.get());
    }

    @Test
    public void test_refresh_reportsAnExpiredToken() {
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator() {
            @Override
            public void updateMemberOf(final EntraIdUser user) {
                // keep the constructor off Microsoft Graph
            }
        };
        ComponentUtil.register(authenticator, EntraIdAuthenticator.class.getCanonicalName());
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        final EntraIdUser user = new EntraIdCredential(new TestAuthenticationResult(new TestAccount(), new Date(now - 1000L))).getUser();

        assertFalse(user.refresh());
    }

    @Test
    public void test_getAuthUrl_usesTheConfiguredResponseMode() {
        // 15.7 hard-coded form_post and 15.8 hard-codes query. A deployment that sets
        // tomcat.sameSiteCookies=none may prefer form_post to keep the authorization code out of
        // the callback URL, so the mode has to be selectable rather than compiled in.
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        final HttpServletRequest request = newAuthUrlRequest();
        try {
            assertTrue(authenticator.getAuthUrl(request).contains("&response_mode=query&"));

            fessConfig.setSystemProperty("entraid.response.mode", "form_post");
            assertTrue(authenticator.getAuthUrl(request).contains("&response_mode=form_post&"));
        } finally {
            fessConfig.setSystemProperty("entraid.response.mode", "");
        }
    }

    @Test
    public void test_getResponseMode_fallsBackWhenTheConfiguredValueIsNotAResponseMode() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        try {
            fessConfig.setSystemProperty("entraid.response.mode", "fragment");
            assertEquals("query", authenticator.getResponseMode());
        } finally {
            fessConfig.setSystemProperty("entraid.response.mode", "");
        }
    }

    @Test
    public void test_getResponseMode_readsTheLegacyAzureAdKey() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        try {
            fessConfig.setSystemProperty("aad.response.mode", " form_post ");
            assertEquals("form_post", authenticator.getResponseMode());
        } finally {
            fessConfig.setSystemProperty("aad.response.mode", "");
        }
    }

    @Test
    public void test_validateState_reportsACallbackThatMatchesNoLogin() {
        // The SSO endpoint is anonymous, so anyone can send a state this server never issued.
        // SsoAction logs SsoStateException without a stack trace so that cannot fill the log.
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        final HttpSession session = newAuthUrlRequest().getSession();
        try {
            authenticator.validateState(session, "never-issued");
            fail("expected SsoStateException");
        } catch (final SsoStateException e) {
            assertEquals("could not validate state", e.getMessage());
        }
    }

    @Test
    public void test_validateNonce_reportsAMismatchAsAStateFailure() {
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        try {
            authenticator.validateNonce(new EntraIdAuthenticator.StateData("expected-nonce", 0L),
                    new TestAuthenticationResult(new TestAccount(), plainIdToken("some-other-nonce")));
            fail("expected SsoStateException");
        } catch (final SsoStateException e) {
            assertEquals("could not validate nonce", e.getMessage());
        }
    }

    @Test
    public void test_validateNonce_acceptsTheNonceItIssued() throws Exception {
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        authenticator.validateNonce(new EntraIdAuthenticator.StateData("expected-nonce", 0L),
                new TestAuthenticationResult(new TestAccount(), plainIdToken("expected-nonce")));
    }

    @Test
    public void test_validateNonce_keepsTheStackTraceOfAnUnreadableIdToken() {
        // Only reachable once the authorization code was redeemed, so this is a fault an operator
        // has to be able to diagnose -- not a callback someone sent us. It must not be reduced to
        // the stack-free SsoStateException log line.
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        try {
            authenticator.validateNonce(new EntraIdAuthenticator.StateData("expected-nonce", 0L),
                    new TestAuthenticationResult(new TestAccount(), "not-a-jwt"));
            fail("expected SsoLoginException");
        } catch (final SsoStateException e) {
            fail("an unreadable ID token must keep its cause: " + e);
        } catch (final SsoLoginException e) {
            assertNotNull(e.getCause());
        }
    }

    /** Builds an unsigned JWT carrying the given nonce; validateNonce only reads the claims. */
    private String plainIdToken(final String nonce) {
        final java.util.Base64.Encoder encoder = java.util.Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString("{\"alg\":\"none\"}".getBytes(StandardCharsets.UTF_8)) + "."
                + encoder.encodeToString(("{\"nonce\":\"" + nonce + "\"}").getBytes(StandardCharsets.UTF_8)) + ".";
    }

    @Test
    public void test_getAuthority_fallsBackWhenTheLegacyKeyIsPresentButBlank() {
        // getSystemProperty returns the default only when the key is absent, so an aad.authority
        // that exists and is empty came back as "". getAuthUrl then built a scheme-less, and
        // therefore relative, URL that redirected the browser back inside Fess.
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        try {
            fessConfig.setSystemProperty("aad.authority", "");

            assertEquals("https://login.microsoftonline.com/", authenticator.getAuthority());
        } finally {
            fessConfig.setSystemProperty("aad.authority", "");
        }
    }

    @Test
    public void test_getResponseMode_ignoresABlankLegacyKey() {
        // getSystemProperty only applies the default when the key is absent, so a key left empty
        // by the admin screen would otherwise warn about entraid.response.mode on every redirect.
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        try {
            fessConfig.setSystemProperty("aad.response.mode", "");
            assertEquals("query", authenticator.getResponseMode());
        } finally {
            fessConfig.setSystemProperty("aad.response.mode", "");
        }
    }

    @Test
    public void test_refresh_doesNotReReadTheDirectoryForAnUnchangedToken() {
        // MSAL4J rounds its expiry buffer down to whole seconds, so around REFRESH_MARGIN it
        // returns the token it already had. Treating that as a renewal would restore the
        // per-request Microsoft Graph call.
        final AtomicBoolean updated = new AtomicBoolean(false);
        final AtomicReference<IAuthenticationResult> current = new AtomicReference<>();
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator() {
            @Override
            public void updateMemberOf(final EntraIdUser user) {
                updated.set(true);
            }

            @Override
            public IAuthenticationResult refreshTokenSilently(final EntraIdUser user) {
                return current.get();
            }
        };
        ComponentUtil.register(authenticator, EntraIdAuthenticator.class.getCanonicalName());
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        final TestAuthenticationResult sameToken = new TestAuthenticationResult(new TestAccount(), new Date(now + 30 * 1000L));
        current.set(sameToken);
        final EntraIdUser user = new EntraIdCredential(sameToken).getUser();
        updated.set(false);

        assertTrue(user.refresh());
        assertFalse(updated.get());

        current.set(new TestAuthenticationResult(new TestAccount(), new Date(now + 30 * 1000L), "renewed-access-token"));
        assertTrue(user.refresh());
        assertTrue(updated.get());
    }

    private HttpServletRequest newAuthUrlRequest() {
        final MockletHttpServletRequest request = getMockRequest();
        request.setMethod("GET");
        return request;
    }
}
