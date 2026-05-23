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
package org.codelibs.fess.filter;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.codelibs.fess.app.web.theme.ThemeViewAction;
import org.codelibs.fess.theme.Theme;
import org.codelibs.fess.theme.ThemeRegistry;
import org.codelibs.fess.theme.ThemeType;
import org.codelibs.fess.unit.UnitFessTestCase;
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

public class StaticThemeFilterTest extends UnitFessTestCase {

    @Test
    public void test_passesThroughWhenNoActiveStaticTheme() throws Exception {
        final StubRegistry reg = new StubRegistry(null);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubRequest req = new StubRequest("GET", "/search");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertTrue(chain.called);
    }

    @Test
    public void test_passesThroughForAdminPath() throws Exception {
        final Theme staticTheme = new Theme(ThemeType.STATIC, "t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubRequest req = new StubRequest("GET", "/admin/dashboard");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertTrue(chain.called);
    }

    @Test
    public void test_passesThroughForApi() throws Exception {
        final Theme staticTheme = new Theme(ThemeType.STATIC, "t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubRequest req = new StubRequest("GET", "/api/v1/health");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertTrue(chain.called);
    }

    @Test
    public void test_passesThroughPostRequests() throws Exception {
        final Theme staticTheme = new Theme(ThemeType.STATIC, "t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubRequest req = new StubRequest("POST", "/login");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertTrue(chain.called);
    }

    @Test
    public void test_forwardsIndexForUiPath() throws Exception {
        final Theme staticTheme = new Theme(ThemeType.STATIC, "t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubRequest req = new StubRequest("GET", "/search");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertEquals("/theme-view", req.forwardedTo);
        assertEquals("INDEX", req.getAttribute(ThemeViewAction.REQ_ATTR_MODE));
        // chain should NOT have been called
        assertEquals(false, chain.called);
    }

    @Test
    public void test_forwardsAssetForMatchingThemeAssetPath() throws Exception {
        final Theme staticTheme = new Theme(ThemeType.STATIC, "alpha", Paths.get("/tmp/alpha"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubRequest req = new StubRequest("GET", "/themes/alpha/assets/app.js");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertEquals("/theme-view", req.forwardedTo);
        assertEquals("ASSET", req.getAttribute(ThemeViewAction.REQ_ATTR_MODE));
        assertEquals("assets/app.js", req.getAttribute(ThemeViewAction.REQ_ATTR_ASSET_PATH));
        assertEquals(false, chain.called);
    }

    @Test
    public void test_passesThroughForNonMatchingThemeAssetPath() throws Exception {
        // Active theme is "alpha"; request is for "beta" assets — pass through
        final Theme staticTheme = new Theme(ThemeType.STATIC, "alpha", Paths.get("/tmp/alpha"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubRequest req = new StubRequest("GET", "/themes/beta/assets/app.js");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertTrue(chain.called);
        assertNull(req.forwardedTo);
    }

    @Test
    public void test_passesThroughForJspThemeAssetPaths() throws Exception {
        final Theme staticTheme = new Theme(ThemeType.STATIC, "t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        for (final String uri : new String[] { "/css/simple/style.css", "/js/simple/app.js", "/images/simple/logo.png" }) {
            final StubRequest req = new StubRequest("GET", uri);
            final StubChain chain = new StubChain();
            f.doFilter(req, new StubResponse(), chain);
            assertTrue(chain.called, "Expected pass-through for " + uri);
        }
    }

    @Test
    public void test_nonHttpRequestPassesThroughWithoutClassCastException() throws Exception {
        // Guard: non-HttpServletRequest must be forwarded to the chain without
        // triggering a ClassCastException. This exercises the defensive cast guard
        // added for cross-context or non-HTTP dispatch scenarios.
        final Theme staticTheme = new Theme(ThemeType.STATIC, "t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);

        // A plain ServletRequest/ServletResponse that is NOT an HttpServletRequest.
        final StubChain chain = new StubChain();
        f.doFilter(new NonHttpStubRequest(), new NonHttpStubResponse(), chain);
        assertTrue(chain.called, "Non-HTTP request must pass through without ClassCastException");
    }

    // ===== Stubs =====

    static class StubRegistry extends ThemeRegistry {
        private final Theme theme;

        StubRegistry(final Theme theme) {
            this.theme = theme;
        }

        @Override
        public Optional<Theme> resolveActiveTheme(final String hostKey) {
            return Optional.ofNullable(theme);
        }
    }

    static class StubChain implements FilterChain {
        boolean called = false;

        @Override
        public void doFilter(final ServletRequest req, final ServletResponse res) {
            called = true;
        }
    }

    static class StubResponse implements HttpServletResponse {
        // Implement the bare minimum; methods we don't call throw UnsupportedOperationException.
        @Override
        public String getCharacterEncoding() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getContentType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.ServletOutputStream getOutputStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.io.PrintWriter getWriter() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCharacterEncoding(final String s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setContentLength(final int len) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setContentLengthLong(final long len) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setContentType(final String type) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setBufferSize(final int size) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getBufferSize() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void flushBuffer() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void resetBuffer() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isCommitted() {
            return false;
        }

        @Override
        public void reset() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setLocale(final java.util.Locale loc) {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.util.Locale getLocale() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addCookie(final jakarta.servlet.http.Cookie cookie) {
            throw new UnsupportedOperationException();
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
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendError(final int sc) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendRedirect(final String location) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendRedirect(final String location, final int sc, final boolean clearBuffer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendRedirect(final String location, final int sc) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendRedirect(final String location, final boolean clearBuffer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setDateHeader(final String name, final long date) {
            /* no-op */
        }

        @Override
        public void addDateHeader(final String name, final long date) {
            /* no-op */
        }

        @Override
        public void setHeader(final String name, final String value) {
            /* no-op */
        }

        @Override
        public void addHeader(final String name, final String value) {
            /* no-op */
        }

        @Override
        public void setIntHeader(final String name, final int value) {
            /* no-op */
        }

        @Override
        public void addIntHeader(final String name, final int value) {
            /* no-op */
        }

        @Override
        public void setStatus(final int sc) {
            /* no-op */
        }

        @Override
        public int getStatus() {
            return 200;
        }

        @Override
        public String getHeader(final String name) {
            return null;
        }

        @Override
        public java.util.Collection<String> getHeaders(final String name) {
            return Collections.emptyList();
        }

        @Override
        public java.util.Collection<String> getHeaderNames() {
            return Collections.emptyList();
        }
    }

    static class StubRequest implements HttpServletRequest {
        private final String method;
        private final String uri;
        private final Map<String, Object> attrs = new HashMap<>();
        String forwardedTo;

        StubRequest(final String method, final String uri) {
            this.method = method;
            this.uri = uri;
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
            return new RequestDispatcher() {
                @Override
                public void forward(final ServletRequest request, final ServletResponse response) {
                    forwardedTo = path;
                }

                @Override
                public void include(final ServletRequest request, final ServletResponse response) {
                    /* no-op */
                }
            };
        }

        // Below: every other HttpServletRequest method throws UnsupportedOperationException —
        // narrow the surface to what the filter actually calls. If a new test fails because
        // the filter calls something here, implement it then.
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
            return null;
        }

        @Override
        public Enumeration<String> getHeaders(final String name) {
            return Collections.emptyEnumeration();
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return Collections.emptyEnumeration();
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
        public String getServletPath() {
            return uri;
        }

        @Override
        public HttpSession getSession(final boolean create) {
            return null;
        }

        @Override
        public HttpSession getSession() {
            return null;
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
            return null;
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
        public Map<String, String[]> getParameterMap() {
            return Collections.emptyMap();
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

        public String getRealPath(final String path) {
            return path;
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

    /**
     * A plain {@link jakarta.servlet.ServletRequest} that does NOT implement
     * {@link HttpServletRequest}, used to exercise the defensive cast guard in
     * {@link StaticThemeFilter#doFilter}.
     */
    static class NonHttpStubRequest implements jakarta.servlet.ServletRequest {
        @Override
        public Object getAttribute(final String name) {
            return null;
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return Collections.emptyEnumeration();
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
        public jakarta.servlet.ServletInputStream getInputStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getParameter(final String name) {
            return null;
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
        public java.util.Map<String, String[]> getParameterMap() {
            return Collections.emptyMap();
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
        public void setAttribute(final String name, final Object o) {
        }

        @Override
        public void removeAttribute(final String name) {
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
        public RequestDispatcher getRequestDispatcher(final String path) {
            return null;
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

    /**
     * A plain {@link jakarta.servlet.ServletResponse} that does NOT implement
     * {@link HttpServletResponse}, used alongside {@link NonHttpStubRequest}.
     */
    static class NonHttpStubResponse implements jakarta.servlet.ServletResponse {
        @Override
        public String getCharacterEncoding() {
            return "UTF-8";
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public jakarta.servlet.ServletOutputStream getOutputStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.io.PrintWriter getWriter() {
            throw new UnsupportedOperationException();
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
    }
}
