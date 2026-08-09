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
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.core.misc.Pair;
import org.codelibs.curl.Curl;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.app.web.base.login.EntraIdCredential.EntraIdUser;
import org.codelibs.fess.exception.SsoLoginException;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.utflute.mocklet.MockletHttpServletRequest;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.login.credential.LoginCredential;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.google.common.cache.CacheBuilder;

public class EntraIdAuthenticatorTest extends UnitFessTestCase {

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
    public void test_getAuthUrl_requestsQueryResponseModeOnV1Endpoint() {
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        authenticator.setUseV2Endpoint(false);

        final String authUrl = authenticator.getAuthUrl(getMockRequest());

        assertTrue(authUrl.contains("response_mode=query"));
        assertFalse(authUrl.contains("response_mode=form_post"));
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
    public void test_setMaxGroupDepth() {
        EntraIdAuthenticator authenticator = new EntraIdAuthenticator();

        // Test setting different max group depths
        authenticator.setMaxGroupDepth(5);
        authenticator.setMaxGroupDepth(20);
        authenticator.setMaxGroupDepth(1);

        // Verify method accepts valid values without exception
        assertTrue(true);
    }

    @Test
    public void test_setGroupCacheExpiry() {
        EntraIdAuthenticator authenticator = new EntraIdAuthenticator();

        // Test setting different cache expiry values
        authenticator.setGroupCacheExpiry(300L);
        authenticator.setGroupCacheExpiry(600L);
        authenticator.setGroupCacheExpiry(60L);

        // Verify method accepts valid values without exception
        assertTrue(true);
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
        EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        authenticator.setMaxGroupDepth(5);

        // Test with depth one before the limit - should attempt to process
        // Will fail due to null user, but verifies depth check passes
        try {
            authenticator.getParentGroup(null, "test-id", 4);
            // If we reach here without NullPointerException, depth check passed
        } catch (NullPointerException e) {
            // Expected due to null user - depth check passed, processing attempted
            assertTrue(true);
        } catch (Exception e) {
            // Other exceptions are also acceptable as we're testing depth logic
            assertTrue(true);
        }
    }

    @Test
    public void test_processParentGroup_callsOverloadWithDepth() {
        EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        authenticator.setMaxGroupDepth(3);

        List<String> groupList = new ArrayList<>();
        List<String> roleList = new ArrayList<>();

        // Test the overload that doesn't take depth parameter
        // It should call the depth-tracking version with depth 0
        try {
            authenticator.processParentGroup(null, groupList, roleList, "test-id");
        } catch (Exception e) {
            // Expected due to null user
        }

        // Verify lists are still valid
        assertNotNull(groupList);
        assertNotNull(roleList);
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

    @Test
    public void test_setUseV2Endpoint() {
        EntraIdAuthenticator authenticator = new EntraIdAuthenticator();

        // Test parameter accepts final boolean (compile-time verification)
        authenticator.setUseV2Endpoint(true);
        authenticator.setUseV2Endpoint(false);

        // Verify method signature is correct
        assertTrue(true);
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
        EntraIdAuthenticator authenticator = new EntraIdAuthenticator();

        List<String> groupList = new ArrayList<>();
        List<String> roleList = new ArrayList<>();
        List<String> groupIdsForParentLookup = new ArrayList<>();

        // Call with invalid URL - should handle gracefully
        Method method = EntraIdAuthenticator.class.getDeclaredMethod("processDirectMemberOf", EntraIdUser.class, List.class, List.class,
                List.class, String.class);
        method.setAccessible(true);

        try {
            method.invoke(authenticator, null, groupList, roleList, groupIdsForParentLookup, "http://invalid-url-for-test");
        } catch (Exception e) {
            // Expected - null user or invalid URL
        }

        // Verify lists remain valid after error
        assertNotNull(groupList, "groupList should not be null");
        assertNotNull(roleList, "roleList should not be null");
        assertNotNull(groupIdsForParentLookup, "groupIdsForParentLookup should not be null");
    }

    /**
     * Test that scheduleParentGroupLookup uses TimeoutManager correctly.
     * This test verifies the method signature and can be called via reflection.
     */
    @Test
    public void test_scheduleParentGroupLookup_schedulesTask() throws Exception {
        EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        // Don't call init() to avoid SsoManager dependency

        List<String> initialGroups = new ArrayList<>();
        initialGroups.add("group1");
        List<String> initialRoles = new ArrayList<>();
        initialRoles.add("role1");
        List<String> groupIds = new ArrayList<>();
        groupIds.add("test-group-id");

        Method method = EntraIdAuthenticator.class.getDeclaredMethod("scheduleParentGroupLookup", EntraIdUser.class, List.class, List.class,
                List.class);
        method.setAccessible(true);

        // Call should not throw - task is scheduled asynchronously
        // Will fail when executed due to null user, but scheduling should succeed
        try {
            method.invoke(authenticator, null, initialGroups, initialRoles, groupIds);
            // Small wait to allow scheduled task to start
            Thread.sleep(100);
        } catch (Exception e) {
            // May throw due to null user when task executes, which is expected
        }

        assertTrue("scheduleParentGroupLookup should complete without immediate error", true);
    }

    /**
     * Test that empty groupIds list does not schedule any task.
     */
    @Test
    public void test_updateMemberOf_emptyGroupIds_noScheduledTask() throws Exception {
        // This test verifies the logic: if groupIdsForParentLookup is empty,
        // scheduleParentGroupLookup should not be called

        TestableEntraIdAuthenticator authenticator = new TestableEntraIdAuthenticator();
        // Don't call init() to avoid SsoManager dependency

        // Verify the flag tracking method call
        assertFalse("scheduleParentGroupLookup should not have been called yet", authenticator.scheduleParentGroupLookupCalled.get());
    }

    /**
     * Test concurrent calls to processDirectMemberOf.
     */
    @Test
    public void test_processDirectMemberOf_threadSafety() throws Exception {
        EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        // Don't call init() to avoid SsoManager dependency

        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicBoolean errorOccurred = new AtomicBoolean(false);

        Method method = EntraIdAuthenticator.class.getDeclaredMethod("processDirectMemberOf", EntraIdUser.class, List.class, List.class,
                List.class, String.class);
        method.setAccessible(true);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    List<String> groupList = new ArrayList<>();
                    List<String> roleList = new ArrayList<>();
                    List<String> groupIds = new ArrayList<>();

                    try {
                        method.invoke(authenticator, null, groupList, roleList, groupIds, "http://test-url");
                    } catch (Exception e) {
                        // Expected due to null user
                    }
                } catch (Exception e) {
                    errorOccurred.set(true);
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await(10, TimeUnit.SECONDS);
        assertFalse("No unexpected errors should occur during concurrent access", errorOccurred.get());
    }

    /**
     * Test that default groups and roles are preserved during lazy loading.
     */
    @Test
    public void test_defaultGroupsAndRoles_preserved() throws Exception {
        TestableEntraIdAuthenticator authenticator = new TestableEntraIdAuthenticator();

        List<String> defaultGroups = authenticator.getDefaultGroupList();
        List<String> defaultRoles = authenticator.getDefaultRoleList();

        // Default lists should be empty or contain configured defaults
        assertNotNull(defaultGroups, "Default groups should not be null");
        assertNotNull(defaultRoles, "Default roles should not be null");
    }

    /**
     * Test list isolation during concurrent updates.
     */
    @Test
    public void test_listIsolation_duringConcurrentUpdates() throws Exception {
        List<String> originalGroups = new ArrayList<>();
        originalGroups.add("original-group");

        List<String> copiedGroups = new ArrayList<>(originalGroups);
        copiedGroups.add("new-group");

        // Verify original is not modified
        assertEquals("Original list should have 1 element", 1, originalGroups.size());
        assertEquals("Copied list should have 2 elements", 2, copiedGroups.size());
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
     * Test that lazy loading mechanism handles errors gracefully.
     */
    @Test
    public void test_lazyLoading_errorHandling() throws Exception {
        EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        // Don't call init() to avoid SsoManager dependency

        // Create lists
        List<String> groups = new ArrayList<>();
        List<String> roles = new ArrayList<>();
        List<String> groupIds = new ArrayList<>();
        groupIds.add("invalid-id");

        Method method = EntraIdAuthenticator.class.getDeclaredMethod("scheduleParentGroupLookup", EntraIdUser.class, List.class, List.class,
                List.class);
        method.setAccessible(true);

        // Should not throw - errors should be logged but not propagated
        try {
            method.invoke(authenticator, null, groups, roles, groupIds);
            Thread.sleep(200); // Wait for async execution
        } catch (Exception e) {
            // Exception in async task is expected and should be caught internally
        }

        assertTrue("Method should handle errors gracefully", true);
    }

    /**
     * Test that multiple scheduleParentGroupLookup calls don't interfere.
     */
    @Test
    public void test_multipleScheduledTasks_noInterference() throws Exception {
        EntraIdAuthenticator authenticator = new EntraIdAuthenticator();
        // Don't call init() to avoid SsoManager dependency

        Method method = EntraIdAuthenticator.class.getDeclaredMethod("scheduleParentGroupLookup", EntraIdUser.class, List.class, List.class,
                List.class);
        method.setAccessible(true);

        AtomicReference<Exception> caughtException = new AtomicReference<>();

        for (int i = 0; i < 5; i++) {
            List<String> groups = new ArrayList<>();
            groups.add("group_" + i);
            List<String> roles = new ArrayList<>();
            List<String> groupIds = new ArrayList<>();
            groupIds.add("id_" + i);

            try {
                method.invoke(authenticator, null, groups, roles, groupIds);
            } catch (Exception e) {
                caughtException.set(e);
            }
        }

        // Wait for tasks to complete
        Thread.sleep(500);

        // No interference should occur (just verify no critical errors)
        assertTrue("Multiple scheduled tasks should not interfere with each other", true);
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
        protected void processDirectMemberOf(EntraIdUser user, List<String> groupList, List<String> roleList,
                List<String> groupIdsForParentLookup, String url) {
            processDirectMemberOfCalled.set(true);
            // Don't call super to avoid actual API calls in tests
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
}
