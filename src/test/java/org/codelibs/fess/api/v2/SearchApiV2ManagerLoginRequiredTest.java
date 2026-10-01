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
package org.codelibs.fess.api.v2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.v2.handlers.LoginRequirement;
import org.codelibs.fess.api.v2.handlers.SearchHandler;
import org.codelibs.fess.app.service.AccessTokenService;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.helper.AccessTokenHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.opensearch.config.exentity.AccessToken;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Pins the {@code login.required} gate on the {@code /api/v2} surface.
 *
 * <p>Before this gate existed, {@code GET /api/v2/search} answered anonymously even with
 * {@code login.required} enabled, while the v1 {@code SearchAction} redirected the same caller
 * to the login page. These tests drive {@code SearchApiV2Manager.process} directly and assert
 * the decision the gate reaches, rather than accepting any of several outcomes — the property
 * is set explicitly so that a regression cannot hide behind a permissive assertion.</p>
 */
public class SearchApiV2ManagerLoginRequiredTest extends UnitFessTestCase {

    @Override
    protected boolean isUseOneTimeContainer() {
        // login.required lives in DynamicProperties, which is a JVM-lifetime singleton in the
        // test container; a one-time container keeps this test from leaking the flag.
        return true;
    }

    @BeforeEach
    public void registerComponents() {
        final LoginRequirement requirement = new LoginRequirement();
        ComponentUtil.register(requirement, "v2LoginRequirement");
        ComponentUtil.register(requirement, LoginRequirement.class.getCanonicalName());
        // The test container has no HTTP session, so resolve the caller through a stub that
        // reports "nobody is signed in" — the state an anonymous request actually produces.
        final FessLoginAssist anonymous = new FessLoginAssist() {
            private static final long serialVersionUID = 1L;

            @Override
            public OptionalThing<FessUserBean> getSavedUserBean() {
                return OptionalThing.empty();
            }
        };
        ComponentUtil.register(anonymous, "fessLoginAssist");
        ComponentUtil.setFessLoginAssist(anonymous);
        if (!ComponentUtil.hasComponent("accessTokenHelper")) {
            ComponentUtil.register(new AccessTokenHelper(), "accessTokenHelper");
        }
        // AccessTokenService#collectPermissions compares the expiry against the system clock.
        ComponentUtil.register(new SystemHelper(), "systemHelper");
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        ComponentUtil.getSystemProperties().remove(Constants.LOGIN_REQUIRED_PROPERTY);
        super.tearDown(testInfo);
    }

    private static void setLoginRequired(final boolean value) {
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        systemProperties.setProperty(Constants.LOGIN_REQUIRED_PROPERTY, Boolean.toString(value));
    }

    @Test
    public void test_search_isRejectedForAnonymousWhenLoginRequired() throws Exception {
        setLoginRequired(true);
        final CapturingResponse res = process("/api/v2/search");
        assertEquals(401, res.status);
        assertTrue(res.body().contains("\"code\":\"auth_required\""), res.body());
    }

    @Test
    public void test_scrollSearch_isRejectedForAnonymousWhenLoginRequired() throws Exception {
        setLoginRequired(true);
        final CapturingResponse res = process("/api/v2/documents/all");
        assertEquals(401, res.status);
        assertTrue(res.body().contains("\"code\":\"auth_required\""), res.body());
    }

    @Test
    public void test_indexDerivedEndpoints_areRejectedForAnonymousWhenLoginRequired() throws Exception {
        setLoginRequired(true);
        for (final String path : new String[] { "/api/v2/labels", "/api/v2/popular-words", "/api/v2/suggest-words",
                "/api/v2/related-queries", "/api/v2/related-content" }) {
            final CapturingResponse res = process(path);
            assertEquals(401, res.status, path);
            assertTrue(res.body().contains("\"code\":\"auth_required\""), path + " -> " + res.body());
        }
    }

    @Test
    public void test_health_staysReachableWhenLoginRequired() throws Exception {
        setLoginRequired(true);
        final CapturingResponse res = process("/api/v2/health");
        assertFalse(res.body().contains("\"code\":\"auth_required\""), res.body());
    }

    @Test
    public void test_loginFlowEndpoints_stayReachableWhenLoginRequired() throws Exception {
        setLoginRequired(true);
        for (final String path : new String[] { "/api/v2/auth/me", "/api/v2/ui/config" }) {
            final CapturingResponse res = process(path);
            assertFalse(res.body().contains("\"code\":\"auth_required\""), path + " -> " + res.body());
        }
    }

    @Test
    public void test_search_isNotGatedWhenLoginIsNotRequired() throws Exception {
        setLoginRequired(false);
        final CapturingResponse res = process("/api/v2/search");
        // The handler itself needs a search engine that the unit container does not provide,
        // so the request may still fail — but never with the authentication decision.
        assertFalse(res.body().contains("\"code\":\"auth_required\""), res.body());
    }

    @Test
    public void test_search_isServedWithValidAccessTokenWhenLoginRequired() throws Exception {
        setLoginRequired(true);
        final boolean[] called = { false };
        final SearchApiV2Manager manager = newManagerWithTokens();
        manager.searchHandler = new SearchHandler() {
            @Override
            public void handle(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
                called[0] = true;
                response.getWriter().write("{\"stub\":\"search\"}");
            }
        };
        final CapturingResponse res = new CapturingResponse();
        manager.process(request("/api/v2/search", "Bearer " + VALID_TOKEN), res, nopChain());
        assertTrue(called[0], res.body());
        assertEquals(200, res.status);
        assertFalse(res.body().contains("\"code\":\"auth_required\""), res.body());
    }

    @Test
    public void test_tokenEndpoints_passTheGateWithValidAccessTokenWhenLoginRequired() throws Exception {
        setLoginRequired(true);
        for (final String path : new String[] { "/api/v2/documents/all", "/api/v2/suggest-words", "/api/v2/labels",
                "/api/v2/popular-words" }) {
            final CapturingResponse res = processWithTokens(path, "Bearer " + VALID_TOKEN);
            // The handler needs a search engine the unit container does not provide, so it may
            // still fail -- but never with the authentication decision.
            assertFalse(res.body().contains("\"code\":\"auth_required\""), path + " -> " + res.body());
        }
    }

    @Test
    public void test_search_isRejectedWithoutAccessTokenWhenLoginRequired() throws Exception {
        setLoginRequired(true);
        final CapturingResponse res = processWithTokens("/api/v2/search", null);
        assertEquals(401, res.status);
        assertTrue(res.body().contains("\"code\":\"auth_required\""), res.body());
        assertTrue(res.body().contains("login required"), res.body());
    }

    @Test
    public void test_search_isRejectedWithUnregisteredAccessTokenWhenLoginRequired() throws Exception {
        setLoginRequired(true);
        final CapturingResponse res = processWithTokens("/api/v2/search", "Bearer unregistered-token");
        assertEquals(401, res.status);
        assertTrue(res.body().contains("\"code\":\"auth_required\""), res.body());
        assertTrue(res.body().contains("invalid access token"), res.body());
    }

    @Test
    public void test_search_isRejectedWithExpiredAccessTokenWhenLoginRequired() throws Exception {
        setLoginRequired(true);
        final CapturingResponse res = processWithTokens("/api/v2/search", "Bearer " + EXPIRED_TOKEN);
        assertEquals(401, res.status);
        assertTrue(res.body().contains("\"code\":\"auth_required\""), res.body());
        assertTrue(res.body().contains("invalid access token"), res.body());
    }

    @Test
    public void test_sessionOnlyEndpoints_stayGatedWithValidAccessTokenWhenLoginRequired() throws Exception {
        setLoginRequired(true);
        for (final String path : new String[] { "/api/v2/chat", "/api/v2/favorites", "/api/v2/search-history", "/api/v2/click",
                "/api/v2/cache/abc123", "/api/v2/related-queries", "/api/v2/related-content", "/api/v2/auth/password" }) {
            final CapturingResponse res = processWithTokens(path, "Bearer " + VALID_TOKEN);
            assertEquals(401, res.status, path);
            assertTrue(res.body().contains("login required"), path + " -> " + res.body());
        }
    }

    @Test
    public void test_search_isNotGatedByAccessTokenWhenLoginIsNotRequired() throws Exception {
        setLoginRequired(false);
        final boolean[] consulted = { false };
        final SearchApiV2Manager manager = new SearchApiV2Manager() {
            @Override
            protected boolean hasValidAccessToken(final HttpServletRequest request) {
                consulted[0] = true;
                return false;
            }
        };
        copyHandlers(manager);
        final CapturingResponse res = new CapturingResponse();
        manager.process(request("/api/v2/search", "Bearer " + VALID_TOKEN), res, nopChain());
        assertFalse(consulted[0], "the gate must not look at the token when login.required is off");
        assertFalse(res.body().contains("\"code\":\"auth_required\""), res.body());
    }

    private static final String VALID_TOKEN = "valid-token";

    private static final String EXPIRED_TOKEN = "expired-token";

    /**
     * Stands in for the token index: resolves the two known tokens and refuses any other one the
     * way {@link AccessTokenService} does. Expiry is decided by the real
     * {@link AccessTokenService#collectPermissions}.
     */
    private static final AccessTokenService TOKEN_SERVICE = new AccessTokenService() {
        @Override
        protected OptionalEntity<Set<String>> resolvePermissions(final HttpServletRequest request, final boolean withRequestParameter) {
            final String token = ComponentUtil.getAccessTokenHelper().getAccessTokenFromRequest(request);
            if (token == null) {
                return OptionalEntity.empty();
            }
            final AccessToken accessToken = new AccessToken();
            accessToken.setPermissions(new String[] { "Rtoken-role" });
            if (VALID_TOKEN.equals(token)) {
                return OptionalEntity.of(collectPermissions(accessToken, request, withRequestParameter));
            }
            if (EXPIRED_TOKEN.equals(token)) {
                accessToken.setExpiredTime(1L);
                return OptionalEntity.of(collectPermissions(accessToken, request, withRequestParameter));
            }
            throw new org.codelibs.fess.exception.InvalidAccessTokenException("invalid_token", "The access token is not registered.");
        }
    };

    private static SearchApiV2Manager newManagerWithTokens() {
        final SearchApiV2Manager manager = new SearchApiV2Manager() {
            @Override
            protected boolean hasValidAccessToken(final HttpServletRequest request) {
                return TOKEN_SERVICE.getTokenPermissions(request).isPresent();
            }
        };
        copyHandlers(manager);
        return manager;
    }

    private static void copyHandlers(final SearchApiV2Manager target) {
        final SearchApiV2Manager source = SearchApiV2ManagerTestSupport.newManagerWithHandlers();
        for (final java.lang.reflect.Field field : SearchApiV2Manager.class.getDeclaredFields()) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            try {
                field.setAccessible(true);
                field.set(target, field.get(source));
            } catch (final ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private CapturingResponse processWithTokens(final String servletPath, final String authorization) throws Exception {
        final CapturingResponse res = new CapturingResponse();
        newManagerWithTokens().process(request(servletPath, authorization), res, nopChain());
        return res;
    }

    private CapturingResponse process(final String servletPath) throws Exception {
        final SearchApiV2Manager manager = SearchApiV2ManagerTestSupport.newManagerWithHandlers();
        final CapturingResponse res = new CapturingResponse();
        manager.process(request(servletPath), res, nopChain());
        return res;
    }

    private static HttpServletRequest request(final String servletPath) {
        return request(servletPath, null);
    }

    private static HttpServletRequest request(final String servletPath, final String authorization) {
        final InvocationHandler h = (proxy, method, args) -> switch (method.getName()) {
        case "getHeader" -> "Authorization".equalsIgnoreCase((String) args[0]) ? authorization : null;
        case "getServletPath" -> servletPath;
        case "getMethod" -> "GET";
        case "getRequestURI" -> servletPath;
        case "getParameterMap" -> java.util.Collections.emptyMap();
        case "getParameterNames" -> java.util.Collections.enumeration(java.util.Collections.emptyList());
        case "getRemoteAddr" -> "127.0.0.1";
        case "toString" -> "StubRequest[" + servletPath + "]";
        case "hashCode" -> System.identityHashCode(proxy);
        case "equals" -> proxy == args[0];
        default -> defaultValue(method.getReturnType());
        };
        return (HttpServletRequest) Proxy.newProxyInstance(SearchApiV2ManagerLoginRequiredTest.class.getClassLoader(),
                new Class<?>[] { HttpServletRequest.class }, h);
    }

    private static Object defaultValue(final Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == boolean.class) {
            return Boolean.FALSE;
        }
        if (type == int.class) {
            return Integer.valueOf(0);
        }
        if (type == long.class) {
            return Long.valueOf(0L);
        }
        return null;
    }

    private static FilterChain nopChain() {
        return new FilterChain() {
            @Override
            public void doFilter(final ServletRequest req, final ServletResponse res) {
                // no downstream filter in these tests
            }
        };
    }

    /** Minimal response that records the status and buffers whatever the envelope writer emits. */
    private static class CapturingResponse implements HttpServletResponse {

        int status = 200;

        private final StringWriter writer = new StringWriter();

        private final PrintWriter printWriter = new PrintWriter(writer);

        String body() {
            printWriter.flush();
            return writer.toString();
        }

        @Override
        public void setStatus(final int sc) {
            this.status = sc;
        }

        @Override
        public int getStatus() {
            return status;
        }

        @Override
        public PrintWriter getWriter() {
            return printWriter;
        }

        @Override
        public void sendError(final int sc, final String msg) {
            this.status = sc;
        }

        @Override
        public void sendError(final int sc) {
            this.status = sc;
        }

        @Override
        public boolean isCommitted() {
            return false;
        }

        @Override
        public ServletOutputStream getOutputStream() {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            return new ServletOutputStream() {
                @Override
                public void write(final int b) {
                    buffer.write(b);
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setWriteListener(final WriteListener listener) {
                    // not used
                }
            };
        }

        // --- everything below is unused by these tests ---

        @Override
        public void addCookie(final jakarta.servlet.http.Cookie cookie) {
        }

        @Override
        public boolean containsHeader(final String name) {
            return false;
        }

        @Override
        public String encodeURL(final String url) {
            return url;
        }

        @Override
        public String encodeRedirectURL(final String url) {
            return url;
        }

        @Override
        public void sendRedirect(final String location) throws IOException {
        }

        @Override
        public void sendRedirect(final String location, final int sc, final boolean clearBuffer) throws IOException {
        }

        @Override
        public void setDateHeader(final String name, final long date) {
        }

        @Override
        public void addDateHeader(final String name, final long date) {
        }

        @Override
        public void setHeader(final String name, final String value) {
        }

        @Override
        public void addHeader(final String name, final String value) {
        }

        @Override
        public void setIntHeader(final String name, final int value) {
        }

        @Override
        public void addIntHeader(final String name, final int value) {
        }

        @Override
        public String getHeader(final String name) {
            return null;
        }

        @Override
        public java.util.Collection<String> getHeaders(final String name) {
            return java.util.Collections.emptyList();
        }

        @Override
        public java.util.Collection<String> getHeaderNames() {
            return java.util.Collections.emptyList();
        }

        @Override
        public String getCharacterEncoding() {
            return "UTF-8";
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public void setCharacterEncoding(final String charset) {
        }

        @Override
        public void setContentLength(final int len) {
        }

        @Override
        public void setContentLengthLong(final long len) {
        }

        @Override
        public void setContentType(final String type) {
        }

        @Override
        public void setBufferSize(final int size) {
        }

        @Override
        public int getBufferSize() {
            return 0;
        }

        @Override
        public void flushBuffer() {
        }

        @Override
        public void resetBuffer() {
        }

        @Override
        public void reset() {
        }

        @Override
        public void setLocale(final java.util.Locale loc) {
        }

        @Override
        public java.util.Locale getLocale() {
            return java.util.Locale.getDefault();
        }
    }
}
