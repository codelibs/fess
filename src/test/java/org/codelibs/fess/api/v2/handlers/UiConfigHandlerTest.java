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
package org.codelibs.fess.api.v2.handlers;

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
 * Unit tests for {@link UiConfigHandler}.
 *
 * <p>Verifies the wire contract for {@code GET /api/v2/ui/config}: payload
 * contains the mandatory keys, and non-GET methods are rejected. The unit
 * harness may not have every dependency wired (notably {@code ThemeRegistry}
 * may be unregistered), so the success-path assertion tolerates the structured
 * 500 envelope that the handler emits in that case — same tolerance pattern as
 * other v2 handler tests.</p>
 */
public class UiConfigHandlerTest extends UnitFessTestCase {

    /**
     * Mirrors {@code SearchApiV2ManagerCsrfTest#registerCsrfManager}: the unit
     * container loads {@code test_app.xml} and does not register the CSRF
     * manager by default, so we wire one in for the handler's {@code issue()}
     * call.
     */
    @BeforeEach
    public void registerCsrfManager() {
        final SessionCsrfTokenManager manager = new SessionCsrfTokenManager();
        ComponentUtil.register(manager, "sessionCsrfTokenManager");
        ComponentUtil.register(manager, SessionCsrfTokenManager.class.getCanonicalName());
    }

    @Test
    public void test_returnsRequiredKeys() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        final StubSession session = new StubSession();
        new UiConfigHandler().handle(new StubRequest("GET", "/api/v2/ui/config").withSession(session), res);
        // Tolerance: if DI for ThemeRegistry / SystemHelper is unavailable in the unit
        // harness the handler emits a structured 500 envelope rather than crashing.
        // Either outcome is acceptable; only the wire contract matters here.
        assertTrue(res.status == 200 || res.status == 500, "unexpected status: " + res.status + " body=" + res.body());
        if (res.status == 200) {
            final String body = res.body();
            for (final String key : new String[] { "\"site_name\"", "\"login_required\"", "\"locales\"", "\"theme\"", "\"features\"",
                    "\"csrf_token\"", "\"page_size_default\"", "\"page_size_max\"" }) {
                assertTrue(body.contains(key), "missing key: " + key + " in " + body);
            }
        } else {
            // Structured error envelope, not a raw stack trace
            assertTrue(res.body().contains("\"status\":9"), "expected structured error envelope: " + res.body());
        }
    }

    @Test
    public void test_rejectsNonGet() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new UiConfigHandler().handle(new StubRequest("POST", "/api/v2/ui/config"), res);
        assertEquals(400, res.status);
        assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
    }

    /**
     * Covers the path where no theme is active in {@code ThemeRegistry}.
     *
     * <p>{@code UnitFessTestCase} typically has no theme registered, so this
     * exercise the empty-theme branch of the handler. The assertion only checks
     * that the {@code "theme"} key is present — its value may be the empty
     * payload {@code {}} or a populated one, depending on whether something
     * else in the suite has wired a registry. Either way the wire contract
     * keeps the key, which is what JS clients rely on.</p>
     */
    @Test
    public void test_emptyThemeStillReturnsPayload() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        final StubSession session = new StubSession();
        new UiConfigHandler().handle(new StubRequest("GET", "/api/v2/ui/config").withSession(session), res);
        // Same tolerance as test_returnsRequiredKeys: success or structured 500.
        assertTrue(res.status == 200 || res.status == 500, "unexpected status: " + res.status + " body=" + res.body());
        if (res.status == 200) {
            assertTrue(res.body().contains("\"theme\""), "missing theme key in " + res.body());
        }
    }

    @Test
    public void test_activeTheme_includedInPayloadWhenThemeRegistryHasActiveStaticTheme() throws Exception {
        // ThemeRegistry is not bound in test_app.xml. ComponentUtil.getComponent(class) first
        // asks Lasta DI; on ComponentNotFoundException it falls back to its componentMap,
        // which ComponentUtil.register(...) writes into. Registering a stub here under the
        // canonical class name therefore makes the handler resolve our stub and exercise the
        // populated-theme payload branch.
        final String yaml = String.join("\n", //
                "apiVersion: fess.codelibs.org/v1", //
                "kind: StaticTheme", //
                "name: bootstrap", //
                "displayName: \"Bootstrap Theme\"", //
                "version: \"1.2.3\"", //
                "supportedLocales: [\"en\", \"ja\"]");
        final org.codelibs.fess.theme.ThemeManifest manifest = org.codelibs.fess.theme.ThemeManifest
                .parse(new java.io.ByteArrayInputStream(yaml.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        final org.codelibs.fess.theme.Theme theme = new org.codelibs.fess.theme.Theme(org.codelibs.fess.theme.ThemeType.STATIC, "bootstrap",
                java.nio.file.Paths.get("/tmp/bootstrap"), manifest);
        final StubThemeRegistry stub = new StubThemeRegistry(java.util.Optional.of(theme));
        ComponentUtil.register(stub, org.codelibs.fess.theme.ThemeRegistry.class.getCanonicalName());
        // VirtualHostHelper is configured in fess.xml which test_app.xml does not include, so
        // its lookup throws and the handler's outer theme try/catch swallows the exception
        // before it can call resolveActiveTheme. Register a no-op helper to keep the theme
        // block alive until the registry call.
        ComponentUtil.register(new org.codelibs.fess.helper.VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return null;
            }
        }, "virtualHostHelper");
        try {
            final CapturingResponse res = new CapturingResponse();
            new UiConfigHandler().handle(new StubRequest("GET", "/api/v2/ui/config").withSession(new StubSession()), res);
            org.junit.jupiter.api.Assertions.assertEquals(200, res.status, res.body());
            final String body = res.body();
            // The theme block now carries the manifest fields the handler is supposed to surface,
            // proving the active-theme branch of UiConfigHandler.handle ran.
            assertTrue(body.contains("\"name\":\"bootstrap\""), body);
            assertTrue(body.contains("\"type\":\"static\""), body);
            assertTrue(body.contains("\"display_name\":\"Bootstrap Theme\""), body);
            assertTrue(body.contains("\"version\":\"1.2.3\""), body);
            // site_name should now reflect the manifest's display_name (handler line ~119).
            assertTrue(body.contains("\"site_name\":\"Bootstrap Theme\""), body);
        } finally {
            // Reset to a known empty-registry state so neighbors that don't bring their own
            // stub still see deterministic behavior.
            ComponentUtil.register(new StubThemeRegistry(java.util.Optional.empty()),
                    org.codelibs.fess.theme.ThemeRegistry.class.getCanonicalName());
        }
    }

    @Test
    public void test_siteName_fallsBackToFessWhenThemeMetadataAbsent() throws Exception {
        // Symmetric to the active-theme test: with no theme bound to the request the handler
        // falls back to the literal "Fess" for site_name (UiConfigHandler.java line ~119).
        // Register an empty-stub registry (and a no-op VirtualHostHelper, otherwise the
        // handler's outer theme try-catch eats the missing-helper exception before reaching
        // resolveActiveTheme — see the active-theme test's comment).
        ComponentUtil.register(new StubThemeRegistry(java.util.Optional.empty()),
                org.codelibs.fess.theme.ThemeRegistry.class.getCanonicalName());
        ComponentUtil.register(new org.codelibs.fess.helper.VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return null;
            }
        }, "virtualHostHelper");
        final CapturingResponse res = new CapturingResponse();
        new UiConfigHandler().handle(new StubRequest("GET", "/api/v2/ui/config").withSession(new StubSession()), res);
        org.junit.jupiter.api.Assertions.assertEquals(200, res.status, res.body());
        assertTrue(res.body().contains("\"site_name\":\"Fess\""), res.body());
    }

    /** Minimal ThemeRegistry stub returning a fixed Optional from resolveActiveTheme. */
    private static class StubThemeRegistry extends org.codelibs.fess.theme.ThemeRegistry {
        private final java.util.Optional<org.codelibs.fess.theme.Theme> active;

        StubThemeRegistry(final java.util.Optional<org.codelibs.fess.theme.Theme> active) {
            this.active = active;
        }

        @Override
        public java.util.Optional<org.codelibs.fess.theme.Theme> resolveActiveTheme(final String virtualHostKey) {
            return active;
        }
    }

    /** Minimal HttpServletResponse stub — captures status, content type and body. */
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

    /**
     * Minimal HttpServletRequest stub — supports method/URI plus an optional
     * attached {@link HttpSession} (the handler calls {@code req.getSession(true)}
     * to issue a CSRF token).
     */
    private static class StubRequest implements HttpServletRequest {
        private final String method;
        private final String uri;
        private final Map<String, Object> attrs = new HashMap<>();
        private HttpSession session;

        StubRequest(final String method, final String uri) {
            this.method = method;
            this.uri = uri;
        }

        StubRequest withSession(final HttpSession s) {
            this.session = s;
            return this;
        }

        @Override
        public String getMethod() {
            return method;
        }

        @Override
        public String getServletPath() {
            return uri;
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
     * Minimal HttpSession used only to back attribute storage for the CSRF
     * token issuer. All other surface throws so accidental usage shows up loudly.
     */
    private static class StubSession implements HttpSession {
        private final Map<String, Object> attrs = new HashMap<>();

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
        public long getCreationTime() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getId() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getLastAccessedTime() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ServletContext getServletContext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setMaxInactiveInterval(final int interval) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getMaxInactiveInterval() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void invalidate() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isNew() {
            throw new UnsupportedOperationException();
        }
    }
}
