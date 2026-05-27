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
 * Tests the CSRF gate wired into {@link SearchApiV2Manager#process}.
 *
 * <p>The gate fires <em>before</em> path dispatch, so test scenarios that exercise
 * blocked requests do not require the eventual handler to be wired up. For
 * passing-token scenarios the dispatch falls through to whichever case is
 * available — we only assert that the gate did <em>not</em> emit a
 * {@code "forbidden"} envelope, not that the downstream handler succeeded.</p>
 *
 * <p>TODO: the spec §7.3 "csrf-required=false" scenario is intentionally deferred
 * to an integration test. {@link UnitFessTestCase}'s {@code FessConfig} is loaded
 * from {@code fess_config.properties} on disk and overriding
 * {@code theme.api.csrf.required} requires injecting a fake config component,
 * which is heavier than the value this case adds at the unit level.</p>
 */
public class SearchApiV2ManagerCsrfTest extends UnitFessTestCase {

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
    public void test_postLogoutWithoutTokenReturnsForbidden() throws Exception {
        final SearchApiV2Manager m = SearchApiV2ManagerTestSupport.newManagerWithHandlers();
        final CapturingResponse res = new CapturingResponse();
        m.process(new StubRequest("/api/v2/auth/logout").withMethod("POST"), res, new NopChain());
        assertEquals(403, res.status);
        final String body = res.body();
        assertTrue(body.contains("\"code\":\"forbidden\""), body);
    }

    @Test
    public void test_postLogoutWithWrongTokenReturnsForbidden() throws Exception {
        final SearchApiV2Manager m = SearchApiV2ManagerTestSupport.newManagerWithHandlers();
        final CapturingResponse res = new CapturingResponse();
        final StubSession session = new StubSession();
        session.setAttribute(SessionCsrfTokenManager.SESSION_ATTR, "the-valid-token");
        final StubRequest req =
                new StubRequest("/api/v2/auth/logout").withMethod("POST").withSession(session).withHeader("X-Fess-CSRF-Token", "wrong");
        m.process(req, res, new NopChain());
        assertEquals(403, res.status);
        assertTrue(res.body().contains("\"code\":\"forbidden\""), res.body());
    }

    @Test
    public void test_postLogoutWithValidTokenPassesGate() throws Exception {
        final SearchApiV2Manager m = SearchApiV2ManagerTestSupport.newManagerWithHandlers();
        final CapturingResponse res = new CapturingResponse();
        final StubSession session = new StubSession();
        session.setAttribute(SessionCsrfTokenManager.SESSION_ATTR, "the-valid-token");
        final StubRequest req = new StubRequest("/api/v2/auth/logout").withMethod("POST")
                .withSession(session)
                .withHeader("X-Fess-CSRF-Token", "the-valid-token");
        m.process(req, res, new NopChain());
        // The /auth/logout dispatch is not yet wired (Batch 3.C adds it), so the request
        // falls through to the default branch (NOT_FOUND). The key assertion is that the
        // CSRF gate did NOT block — i.e. no "forbidden" envelope was emitted.
        final String body = res.body();
        assertFalse(body.contains("\"code\":\"forbidden\""), body);
    }

    @Test
    public void test_postLoginWithoutTokenIsExempt() throws Exception {
        final SearchApiV2Manager m = SearchApiV2ManagerTestSupport.newManagerWithHandlers();
        final CapturingResponse res = new CapturingResponse();
        m.process(new StubRequest("/api/v2/auth/login").withMethod("POST"), res, new NopChain());
        // Login is the only way to obtain a token, so it is CSRF-exempt. The handler is not
        // yet wired, so we accept any status as long as it is not the gate's 403.
        assertNotEquals(403, res.status, res.body());
        assertFalse(res.body().contains("\"code\":\"forbidden\""), res.body());
    }

    @Test
    public void test_postClickRejectedWithoutCsrfToken_returns403() throws Exception {
        final SearchApiV2Manager m = SearchApiV2ManagerTestSupport.newManagerWithHandlers();
        final CapturingResponse res = new CapturingResponse();
        m.process(new StubRequest("/api/v2/click").withMethod("POST"), res, new NopChain());
        assertEquals(403, res.status);
        final String body = res.body();
        assertTrue(body.contains("\"code\":\"forbidden\""), body);
    }

    @Test
    public void test_postFavoriteRejectedWithoutCsrfToken_returns403() throws Exception {
        final SearchApiV2Manager m = SearchApiV2ManagerTestSupport.newManagerWithHandlers();
        final CapturingResponse res = new CapturingResponse();
        m.process(new StubRequest("/api/v2/documents/abc123/favorite").withMethod("POST"), res, new NopChain());
        assertEquals(403, res.status);
        final String body = res.body();
        assertTrue(body.contains("\"code\":\"forbidden\""), body);
    }

    @Test
    public void test_postChatRejectedWithoutCsrfToken_returns403() throws Exception {
        final SearchApiV2Manager m = SearchApiV2ManagerTestSupport.newManagerWithHandlers();
        final CapturingResponse res = new CapturingResponse();
        m.process(new StubRequest("/api/v2/chat").withMethod("POST"), res, new NopChain());
        assertEquals(403, res.status);
        final String body = res.body();
        assertTrue(body.contains("\"code\":\"forbidden\""), body);
    }

    @Test
    public void test_postChatStreamRejectedWithoutCsrfToken_returns403() throws Exception {
        final SearchApiV2Manager m = SearchApiV2ManagerTestSupport.newManagerWithHandlers();
        final CapturingResponse res = new CapturingResponse();
        m.process(new StubRequest("/api/v2/chat/stream").withMethod("POST"), res, new NopChain());
        assertEquals(403, res.status);
        final String body = res.body();
        assertTrue(body.contains("\"code\":\"forbidden\""), body);
    }

    @Test
    public void test_getAuthMeWithoutTokenIsExempt() throws Exception {
        final SearchApiV2Manager m = SearchApiV2ManagerTestSupport.newManagerWithHandlers();
        final CapturingResponse res = new CapturingResponse();
        m.process(new StubRequest("/api/v2/auth/me"), res, new NopChain());
        // GETs are always CSRF-exempt regardless of subpath. /auth/me is not yet wired,
        // so the default NOT_FOUND envelope is acceptable — just not a 403/forbidden.
        assertNotEquals(403, res.status, res.body());
        assertFalse(res.body().contains("\"code\":\"forbidden\""), res.body());
    }

    @Test
    public void searchApiV2Manager_handlerThrows_doesNotLeakMessage() throws Exception {
        // Verify the outer catch's sanitization by directly inspecting the source. The
        // fix replaced `V2EnvelopeWriter.writeError(response, V2ErrorCode.INTERNAL_ERROR, e.getMessage())`
        // with `V2EnvelopeWriter.writeError(response, V2ErrorCode.INTERNAL_ERROR, "internal error")`.
        // The simplest verifiable invariant from a unit test: read the source file (under
        // src/main) and confirm the broad catch no longer passes `e.getMessage()`. This is
        // a static-assertion-style test that protects against future regressions where a
        // developer adds the leak back.
        final java.nio.file.Path src = java.nio.file.Paths.get("src/main/java/org/codelibs/fess/api/v2/SearchApiV2Manager.java");
        org.junit.jupiter.api.Assumptions.assumeTrue(java.nio.file.Files.exists(src),
                "skipping: source file not present at runtime cwd=" + java.nio.file.Paths.get(".").toAbsolutePath());
        final String source = java.nio.file.Files.readString(src);
        final int catchIdx = source.indexOf("/api/v2 handler failed for");
        assertTrue(catchIdx >= 0, "broad-catch log statement not found — has the file been restructured?");
        // Inspect the small window of source immediately after the log call.
        final String window = source.substring(catchIdx, Math.min(source.length(), catchIdx + 2000));
        // Find the actual V2EnvelopeWriter.writeError call inside the broad catch and assert
        // its arguments — the window also contains a "do not leak e.getMessage()" comment we
        // intentionally keep, so scope the leak check to the function-call expression.
        final int writeErrorCall = window.indexOf("V2EnvelopeWriter.writeError(response, V2ErrorCode.INTERNAL_ERROR");
        assertTrue(writeErrorCall >= 0, "broad-catch writeError call missing: " + window);
        final int callEnd = window.indexOf(");", writeErrorCall);
        final String call = window.substring(writeErrorCall, callEnd >= 0 ? callEnd : window.length());
        assertFalse(call.contains("e.getMessage()"),
                "SearchApiV2Manager broad catch still leaks e.getMessage() in writeError args: " + call);
        assertTrue(call.contains("\"internal error\""), "expected sanitized message constant in broad catch writeError args: " + call);
        assertTrue(window.contains("isCommitted()"), "expected isCommitted() short-circuit in broad catch: " + window);
    }

    @Test
    public void searchApiV2Manager_handlerThrowsAfterCommit_doesNotWriteEnvelope() throws Exception {
        // Companion to the message-leak test: assert that when the response is already
        // committed, the broad catch performs an early-return rather than calling writeError
        // on top of the in-flight body. Source-level assertion for the same reason as the
        // sibling test: every reachable runtime trigger goes through a handler-level catch
        // first, so a behavior-level test can't reliably observe the manager's branch.
        final java.nio.file.Path src = java.nio.file.Paths.get("src/main/java/org/codelibs/fess/api/v2/SearchApiV2Manager.java");
        org.junit.jupiter.api.Assumptions.assumeTrue(java.nio.file.Files.exists(src),
                "skipping: source file not present at runtime cwd=" + java.nio.file.Paths.get(".").toAbsolutePath());
        final String source = java.nio.file.Files.readString(src);
        // Anchor specifically on the broad `catch (final Exception e)` block — Wave 1 added a
        // dedicated IOException catch earlier in the same try/catch that re-throws on commit
        // (semantically different from the broad catch's early-return), so we cannot use the
        // first occurrence of the shared log message as an anchor.
        final int catchIdx = source.indexOf("} catch (final Exception e) {");
        assertTrue(catchIdx >= 0, "broad Exception catch block not found");
        // The fix uses `if (response.isCommitted()) { return; }` BEFORE writeError. Verify
        // ordering invariants by checking string positions in the window starting at the
        // broad-catch declaration.
        final String window = source.substring(catchIdx, Math.min(source.length(), catchIdx + 2000));
        final int committedIdx = window.indexOf("isCommitted()");
        final int writeErrorIdx = window.indexOf("V2EnvelopeWriter.writeError");
        assertTrue(committedIdx > 0, "isCommitted() guard missing: " + window);
        assertTrue(writeErrorIdx > 0, "writeError call missing: " + window);
        assertTrue(committedIdx < writeErrorIdx, "isCommitted() guard must precede writeError() in the broad catch: " + window);
        // The guard must early-return; assert the keyword "return" appears between the
        // isCommitted check and writeError.
        final String betweenGuardAndWrite = window.substring(committedIdx, writeErrorIdx);
        assertTrue(betweenGuardAndWrite.contains("return"),
                "isCommitted() branch must early-return before reaching writeError: " + betweenGuardAndWrite);
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

    /**
     * Adapted from SearchApiV2ManagerTest.StubRequest with per-name header storage
     * and an attached {@link HttpSession} for CSRF gate tests.
     */
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

    /**
     * Minimal HttpSession used only to back attribute storage for the CSRF gate.
     * All other surface throws so accidental usage shows up loudly.
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
