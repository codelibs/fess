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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.cors.CorsHandlerFactory;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;

/**
 * Integration tests for the two-layer CSRF gate wired into
 * {@link SearchApiV2Manager#process}. Both layers are always applied to unsafe
 * methods (there are no enable/disable flags).
 *
 * <p>The gate fires before path dispatch, so blocked scenarios do not need the
 * downstream handler. Both layers emit the same {@code "forbidden"} envelope
 * code; the layers are distinguished by message ({@code "cross-site request
 * blocked"} for the Origin layer, {@code "invalid csrf token"} for the token
 * layer).</p>
 *
 * <p>The same-origin reconstruction for the stub request is
 * {@code http://localhost:8080} (its {@code getScheme}/{@code getServerName}/
 * {@code getServerPort} defaults); {@code getRemoteAddr} returns an untrusted
 * peer so the servlet reconstruction path is exercised without forwarded-header
 * trust. The manager's {@code originValidator} is wired by
 * {@link SearchApiV2ManagerTestSupport} with a real {@code TargetOriginResolver}
 * that drives off these servlet defaults.</p>
 */
public class SearchApiV2ManagerOriginCheckTest extends UnitFessTestCase {

    private static final String SAME_ORIGIN = "http://localhost:8080";

    @BeforeEach
    public void registerComponents() {
        final SessionCsrfTokenManager manager = new SessionCsrfTokenManager();
        ComponentUtil.register(manager, "sessionCsrfTokenManager");
        ComponentUtil.register(manager, SessionCsrfTokenManager.class.getCanonicalName());
        ComponentUtil.register(new CorsHandlerFactory(), "corsHandlerFactory");
    }

    // Origin layer: cross-site is blocked regardless of token

    @Test
    public void test_crossSiteOrigin_blockedEvenWithValidToken() throws Exception {
        final SearchApiV2Manager m = SearchApiV2ManagerTestSupport.newManagerWithHandlers();
        final StubSession session = new StubSession();
        session.setAttribute(SessionCsrfTokenManager.SESSION_ATTR, "good-token");
        final CapturingResponse res = new CapturingResponse();
        final StubRequest req = new StubRequest("/api/v2/click").withMethod("POST")
                .withSession(session)
                .withHeader("X-Fess-CSRF-Token", "good-token")
                .withHeader("Origin", "https://evil.example.com");
        m.process(req, res, new NopChain());
        assertEquals(403, res.status, res.body());
        assertTrue(res.body().contains("\"code\":\"forbidden\""), res.body());
        assertTrue(res.body().contains("cross-site request blocked"), res.body());
    }

    // Token layer: missing token on a state-changing path is blocked

    @Test
    public void test_sameOrigin_missingToken_blockedByTokenLayer() throws Exception {
        final SearchApiV2Manager m = SearchApiV2ManagerTestSupport.newManagerWithHandlers();
        final CapturingResponse res = new CapturingResponse();
        // Same-origin passes the Origin layer; the token layer still rejects the missing token.
        final StubRequest req = new StubRequest("/api/v2/click").withMethod("POST").withHeader("Origin", SAME_ORIGIN);
        m.process(req, res, new NopChain());
        assertEquals(403, res.status, res.body());
        assertTrue(res.body().contains("invalid csrf token"), res.body());
    }

    // same-origin + valid token passes both gates

    @Test
    public void test_sameOriginAndValidToken_passesGate() throws Exception {
        final SearchApiV2Manager m = SearchApiV2ManagerTestSupport.newManagerWithHandlers();
        final StubSession session = new StubSession();
        session.setAttribute(SessionCsrfTokenManager.SESSION_ATTR, "good-token");
        final CapturingResponse res = new CapturingResponse();
        final StubRequest req = new StubRequest("/api/v2/click").withMethod("POST")
                .withSession(session)
                .withHeader("X-Fess-CSRF-Token", "good-token")
                .withHeader("Origin", SAME_ORIGIN);
        m.process(req, res, new NopChain());
        // The gate must NOT emit a forbidden envelope; dispatch proceeds into the click handler.
        assertFalse(res.body().contains("cross-site request blocked"), res.body());
        assertFalse(res.body().contains("invalid csrf token"), res.body());
    }

    @Test
    public void test_missingOriginAndValidToken_passesGate() throws Exception {
        // Non-browser client (no Origin/Referer) with a valid token: Origin layer allows
        // (missing source), token layer allows (valid token).
        final SearchApiV2Manager m = SearchApiV2ManagerTestSupport.newManagerWithHandlers();
        final StubSession session = new StubSession();
        session.setAttribute(SessionCsrfTokenManager.SESSION_ATTR, "good-token");
        final CapturingResponse res = new CapturingResponse();
        final StubRequest req =
                new StubRequest("/api/v2/click").withMethod("POST").withSession(session).withHeader("X-Fess-CSRF-Token", "good-token");
        m.process(req, res, new NopChain());
        assertFalse(res.body().contains("cross-site request blocked"), res.body());
        assertFalse(res.body().contains("invalid csrf token"), res.body());
    }

    // safe method skips both layers

    @Test
    public void test_getRequest_skipsBothLayers_evenCrossSiteOrigin() throws Exception {
        final SearchApiV2Manager m = SearchApiV2ManagerTestSupport.newManagerWithHandlers();
        final CapturingResponse res = new CapturingResponse();
        // GET is a safe method: neither the Origin layer nor the token layer applies,
        // even with a cross-site Origin and no token.
        final StubRequest req = new StubRequest("/api/v2/auth/me").withHeader("Origin", "https://evil.example.com");
        m.process(req, res, new NopChain());
        assertFalse(res.body().contains("cross-site request blocked"), res.body());
        assertFalse(res.body().contains("invalid csrf token"), res.body());
    }

    @Test
    public void test_loginPost_crossSiteOrigin_blockedByOriginLayer() throws Exception {
        // /auth/login is exempt from the token layer, but the Origin layer still covers
        // it (login CSRF defense). A cross-site Origin must be blocked.
        final SearchApiV2Manager m = SearchApiV2ManagerTestSupport.newManagerWithHandlers();
        final CapturingResponse res = new CapturingResponse();
        final StubRequest req = new StubRequest("/api/v2/auth/login").withMethod("POST").withHeader("Origin", "https://evil.example.com");
        m.process(req, res, new NopChain());
        assertEquals(403, res.status, res.body());
        assertTrue(res.body().contains("cross-site request blocked"), res.body());
    }

    // stubs

    /** A FilterChain that does nothing — the v2 manager never delegates further. */
    private static class NopChain implements FilterChain {
        @Override
        public void doFilter(final ServletRequest request, final ServletResponse response) {
            // no-op
        }
    }

    /** Minimal HttpServletResponse stub capturing status and writer output. */
    private static class CapturingResponse implements HttpServletResponse {
        final StringWriter sw = new StringWriter();
        final PrintWriter writer = new PrintWriter(sw);
        int status = 200;
        String contentType;

        String body() {
            writer.flush();
            return sw.toString();
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
        public void setContentType(final String type) {
            this.contentType = type;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return writer;
        }

        @Override
        public String getCharacterEncoding() {
            return "UTF-8";
        }

        @Override
        public void setCharacterEncoding(final String s) {
        }

        @Override
        public jakarta.servlet.ServletOutputStream getOutputStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setContentLength(final int len) {
        }

        @Override
        public void setContentLengthLong(final long len) {
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
        public boolean isCommitted() {
            return false;
        }

        @Override
        public void reset() {
        }

        @Override
        public void setLocale(final java.util.Locale loc) {
        }

        @Override
        public java.util.Locale getLocale() {
            return java.util.Locale.ROOT;
        }

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
        public void sendError(final int sc, final String msg) {
        }

        @Override
        public void sendError(final int sc) {
        }

        @Override
        public void sendRedirect(final String location) {
        }

        @Override
        public void sendRedirect(final String location, final int sc) {
        }

        @Override
        public void sendRedirect(final String location, final boolean clearBuffer) {
        }

        @Override
        public void sendRedirect(final String location, final int sc, final boolean clearBuffer) {
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
    }

    /** Minimal HttpServletRequest stub with per-name headers, method, session, and servlet origin defaults. */
    private static class StubRequest implements HttpServletRequest {
        private final String uri;
        private final Map<String, Object> attrs = new HashMap<>();
        private final Map<String, String> headers = new HashMap<>();
        private String method = "GET";
        private HttpSession session;

        StubRequest(final String uri) {
            this.uri = uri;
        }

        StubRequest withMethod(final String m) {
            this.method = m;
            return this;
        }

        StubRequest withHeader(final String name, final String value) {
            this.headers.put(name, value);
            return this;
        }

        StubRequest withSession(final HttpSession s) {
            this.session = s;
            return this;
        }

        @Override
        public String getServletPath() {
            return uri;
        }

        @Override
        public String getMethod() {
            return method;
        }

        @Override
        public String getRequestURI() {
            return uri;
        }

        @Override
        public String getContextPath() {
            return "";
        }

        @Override
        public String getHeader(final String name) {
            return headers.get(name);
        }

        @Override
        public String getScheme() {
            return "http";
        }

        @Override
        public String getServerName() {
            return "localhost";
        }

        @Override
        public int getServerPort() {
            return 8080;
        }

        @Override
        public String getRemoteAddr() {
            // An untrusted peer so forwarded headers are never trusted and the servlet
            // reconstruction path (http://localhost:8080) is used for same-origin.
            return "203.0.113.9";
        }

        @Override
        public HttpSession getSession(final boolean create) {
            if (session == null && create) {
                session = new StubSession();
            }
            return session;
        }

        @Override
        public HttpSession getSession() {
            return getSession(true);
        }

        @Override
        public Object getAttribute(final String name) {
            return attrs.get(name);
        }

        @Override
        public void setAttribute(final String name, final Object value) {
            if (value == null) {
                attrs.remove(name);
            } else {
                attrs.put(name, value);
            }
        }

        @Override
        public void removeAttribute(final String name) {
            attrs.remove(name);
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return Collections.enumeration(attrs.keySet());
        }

        @Override
        public Enumeration<String> getHeaders(final String name) {
            final String v = headers.get(name);
            return v == null ? Collections.emptyEnumeration() : Collections.enumeration(Collections.singletonList(v));
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return Collections.enumeration(headers.keySet());
        }

        @Override
        public RequestDispatcher getRequestDispatcher(final String path) {
            return null;
        }

        @Override
        public String getParameter(final String name) {
            return null;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return Collections.emptyMap();
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.emptyEnumeration();
        }

        @Override
        public String[] getParameterValues(final String name) {
            return null;
        }

        @Override
        public String getQueryString() {
            return null;
        }

        // unsupported remainder

        @Override
        public String getAuthType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.http.Cookie[] getCookies() {
            return null;
        }

        @Override
        public long getDateHeader(final String name) {
            return -1L;
        }

        @Override
        public int getIntHeader(final String name) {
            return -1;
        }

        @Override
        public String getPathInfo() {
            return null;
        }

        @Override
        public String getPathTranslated() {
            return null;
        }

        @Override
        public String getRemoteUser() {
            return null;
        }

        @Override
        public boolean isUserInRole(final String role) {
            return false;
        }

        @Override
        public java.security.Principal getUserPrincipal() {
            return null;
        }

        @Override
        public String getRequestedSessionId() {
            return null;
        }

        @Override
        public StringBuffer getRequestURL() {
            return new StringBuffer("http://localhost:8080").append(uri);
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromCookie() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromURL() {
            return false;
        }

        @Override
        public boolean authenticate(final HttpServletResponse response) {
            return false;
        }

        @Override
        public void login(final String username, final String password) {
        }

        @Override
        public void logout() {
        }

        @Override
        public java.util.Collection<Part> getParts() {
            return Collections.emptyList();
        }

        @Override
        public Part getPart(final String name) {
            return null;
        }

        @Override
        public <T extends HttpUpgradeHandler> T upgrade(final Class<T> handlerClass) {
            return null;
        }

        @Override
        public String getCharacterEncoding() {
            return "UTF-8";
        }

        @Override
        public void setCharacterEncoding(final String env) {
        }

        @Override
        public int getContentLength() {
            return 0;
        }

        @Override
        public long getContentLengthLong() {
            return 0L;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public ServletInputStream getInputStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getProtocol() {
            return "HTTP/1.1";
        }

        @Override
        public java.io.BufferedReader getReader() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getRemoteHost() {
            return "client";
        }

        @Override
        public int getRemotePort() {
            return 0;
        }

        @Override
        public String getLocalName() {
            return "localhost";
        }

        @Override
        public String getLocalAddr() {
            return "127.0.0.1";
        }

        @Override
        public int getLocalPort() {
            return 8080;
        }

        @Override
        public ServletContext getServletContext() {
            return null;
        }

        @Override
        public AsyncContext startAsync() {
            throw new IllegalStateException();
        }

        @Override
        public AsyncContext startAsync(final ServletRequest req, final ServletResponse res) {
            throw new IllegalStateException();
        }

        @Override
        public boolean isAsyncStarted() {
            return false;
        }

        @Override
        public boolean isAsyncSupported() {
            return false;
        }

        @Override
        public AsyncContext getAsyncContext() {
            throw new IllegalStateException();
        }

        @Override
        public DispatcherType getDispatcherType() {
            return DispatcherType.REQUEST;
        }

        @Override
        public String getRequestId() {
            return "req";
        }

        @Override
        public String getProtocolRequestId() {
            return "";
        }

        @Override
        public jakarta.servlet.ServletConnection getServletConnection() {
            return null;
        }

        @Override
        public java.util.Locale getLocale() {
            return java.util.Locale.ROOT;
        }

        @Override
        public Enumeration<java.util.Locale> getLocales() {
            return Collections.enumeration(Collections.singletonList(java.util.Locale.ROOT));
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public String changeSessionId() {
            return null;
        }
    }

    /** Minimal HttpSession stub holding a single CSRF token attribute. */
    private static class StubSession implements HttpSession {
        private final Map<String, Object> attrs = new HashMap<>();

        @Override
        public Object getAttribute(final String name) {
            return attrs.get(name);
        }

        @Override
        public void setAttribute(final String name, final Object value) {
            attrs.put(name, value);
        }

        @Override
        public void removeAttribute(final String name) {
            attrs.remove(name);
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return Collections.enumeration(attrs.keySet());
        }

        @Override
        public long getCreationTime() {
            return 0L;
        }

        @Override
        public String getId() {
            return "stub-session";
        }

        @Override
        public long getLastAccessedTime() {
            return 0L;
        }

        @Override
        public ServletContext getServletContext() {
            return null;
        }

        @Override
        public void setMaxInactiveInterval(final int interval) {
        }

        @Override
        public int getMaxInactiveInterval() {
            return 0;
        }

        @Override
        public void invalidate() {
            attrs.clear();
        }

        @Override
        public boolean isNew() {
            return false;
        }
    }
}
