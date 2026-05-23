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

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.helper.SessionCsrfTokenManager;
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
 * Tests the manager-level CSRF gate for {@code /api/v2/chat}.
 *
 * <p>The streaming variant {@code /api/v2/chat/stream} is intentionally deferred
 * to Batch 4.C alongside the streaming handler itself — only the non-streaming
 * {@code /chat} path is asserted here.</p>
 */
public class SearchApiV2ManagerChatTest extends UnitFessTestCase {

    /**
     * The production wiring lives in {@code app.xml} but the test container loads
     * {@code test_app.xml} which boots from {@code convention.xml} alone. Register
     * a fresh manager under both the bean name and the class canonical name so
     * {@code ComponentUtil.getComponent(SessionCsrfTokenManager.class)} resolves
     * through the fallback path in {@code ComponentUtil}.
     */
    @BeforeEach
    public void registerCsrfManager() {
        final SessionCsrfTokenManager manager = new SessionCsrfTokenManager();
        ComponentUtil.register(manager, "sessionCsrfTokenManager");
        ComponentUtil.register(manager, SessionCsrfTokenManager.class.getCanonicalName());
    }

    @Test
    public void test_chatPostWithoutCsrf_returns403() throws Exception {
        final SearchApiV2Manager m = new SearchApiV2Manager();
        final CapturingResponse res = new CapturingResponse();
        m.process(new StubRequest("/api/v2/chat").withMethod("POST"), res, new NopChain());
        assertEquals(403, res.status);
        assertTrue(res.body().contains("\"code\":\"forbidden\""), res.body());
    }

    @Test
    public void test_chatGetIsExemptFromCsrf() throws Exception {
        // GETs are CSRF-exempt regardless of subpath. /chat GET will then be
        // rejected by ChatHandler with invalid_request (method not allowed), but
        // we only assert the manager does NOT 403 here.
        final SearchApiV2Manager m = new SearchApiV2Manager();
        final CapturingResponse res = new CapturingResponse();
        m.process(new StubRequest("/api/v2/chat"), res, new NopChain());
        assertNotEquals(403, res.status, res.body());
    }

    @Test
    public void test_chatStreamPostWithoutCsrf_returns403() throws Exception {
        final SearchApiV2Manager manager = new SearchApiV2Manager();
        final CapturingResponse res = new CapturingResponse();
        manager.process(new StubRequest("POST", "/api/v2/chat/stream"), res, null);
        assertEquals(403, res.status);
        assertTrue(res.body().contains("\"code\":\"forbidden\""));
    }

    @Test
    public void test_chatStreamGetIsExemptFromCsrf() throws Exception {
        // GET /api/v2/chat/stream passes the CSRF gate (then handler rejects via SSE error event).
        final SearchApiV2Manager manager = new SearchApiV2Manager();
        final CapturingResponse res = new CapturingResponse();
        manager.process(new StubRequest("GET", "/api/v2/chat/stream"), res, null);
        assertNotEquals(403, res.status);
    }

    /** A FilterChain that does nothing — the v2 manager never delegates further. */
    private static class NopChain implements FilterChain {
        @Override
        public void doFilter(final ServletRequest request, final ServletResponse response) {
            // no-op
        }
    }

    /** Minimal HttpServletResponse stub that captures setContentType/setStatus/getWriter output. */
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

    /** Mirror of the SearchApiV2ManagerCsrfTest stub — supports header + session attachment. */
    private static class StubRequest implements HttpServletRequest {
        private final String uri;
        private final Map<String, Object> attrs = new HashMap<>();
        private final Map<String, String[]> params;
        private final Map<String, String> headers = new HashMap<>();
        private String method = "GET";
        private HttpSession session;

        StubRequest(final String uri) {
            this(uri, Collections.emptyMap());
        }

        StubRequest(final String uri, final Map<String, String[]> params) {
            this.uri = uri;
            this.params = params == null ? Collections.emptyMap() : params;
        }

        /**
         * Convenience constructor that takes method first so call sites can
         * read {@code new StubRequest("POST", "/api/v2/chat/stream")} the same
         * way the handler unit tests do.
         */
        StubRequest(final String method, final String uri) {
            this(uri, Collections.emptyMap());
            this.method = method;
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
        public RequestDispatcher getRequestDispatcher(final String path) {
            return null;
        }

        @Override
        public String getAuthType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.http.Cookie[] getCookies() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getDateHeader(final String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getHeader(final String name) {
            return headers.get(name);
        }

        @Override
        public Enumeration<String> getHeaders(final String name) {
            final String v = headers.get(name);
            return v == null ? Collections.emptyEnumeration() : Collections.enumeration(java.util.Collections.singleton(v));
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return Collections.enumeration(headers.keySet());
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
        public String getQueryString() {
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
            return new StringBuffer(uri);
        }

        @Override
        public HttpSession getSession(final boolean create) {
            return session;
        }

        @Override
        public HttpSession getSession() {
            return session;
        }

        @Override
        public String changeSessionId() {
            return null;
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
            return null;
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
            return 0;
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
        public String getParameter(final String name) {
            final String[] vals = params.get(name);
            return vals == null || vals.length == 0 ? null : vals[0];
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.enumeration(params.keySet());
        }

        @Override
        public String[] getParameterValues(final String name) {
            return params.get(name);
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return Collections.unmodifiableMap(params);
        }

        @Override
        public String getProtocol() {
            return "HTTP/1.1";
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
        public java.io.BufferedReader getReader() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getRemoteAddr() {
            return "127.0.0.1";
        }

        @Override
        public String getRemoteHost() {
            return "localhost";
        }

        @Override
        public java.util.Locale getLocale() {
            return java.util.Locale.ROOT;
        }

        @Override
        public Enumeration<java.util.Locale> getLocales() {
            return Collections.enumeration(java.util.Collections.singleton(java.util.Locale.ROOT));
        }

        @Override
        public boolean isSecure() {
            return false;
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
            throw new UnsupportedOperationException();
        }

        @Override
        public AsyncContext startAsync() {
            throw new UnsupportedOperationException();
        }

        @Override
        public AsyncContext startAsync(final ServletRequest req, final ServletResponse resp) {
            throw new UnsupportedOperationException();
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
            throw new UnsupportedOperationException();
        }

        @Override
        public DispatcherType getDispatcherType() {
            return DispatcherType.REQUEST;
        }

        @Override
        public String getRequestId() {
            return "";
        }

        @Override
        public String getProtocolRequestId() {
            return "";
        }

        @Override
        public jakarta.servlet.ServletConnection getServletConnection() {
            return null;
        }
    }
}
