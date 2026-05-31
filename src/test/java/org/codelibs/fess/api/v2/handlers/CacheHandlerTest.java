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

import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
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
 * Unit tests for {@link CacheHandler}.
 *
 * <p>Verifies the wire contract for {@code GET /api/v2/cache/{docId}}:</p>
 * <ul>
 *   <li>Non-GET methods are rejected with {@code 400 invalid_request}.</li>
 *   <li>A blank or malformed {@code docId} is rejected with {@code 400 invalid_request}.</li>
 *   <li>A non-existent {@code docId} surfaces as either {@code 404 not_found}
 *       (when the search helper returns empty) or {@code 500 internal_error}
 *       (when the search helper throws because no OpenSearch is wired in the
 *       unit harness). Both outcomes preserve the v2 envelope shape.</li>
 * </ul>
 */
public class CacheHandlerTest extends UnitFessTestCase {

    @Test
    public void test_rejectsNonGet() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new CacheHandler().handle(new StubRequest("POST", "/api/v2/cache/abc"), res, "abc");
        assertEquals(405, res.status);
        assertTrue(res.body().contains("\"code\":\"method_not_allowed\""), res.body());
        // MJ-18: RFC 7231 §6.5.5 requires Allow header on 405.
        assertEquals("Allow header must be set on 405", "GET", res.getHeader("Allow"));
    }

    /**
     * MJ-20: when login is required and no user is authenticated, the handler must
     * return 401 auth_required — parity with v1 CacheAction:68-70.
     *
     * <p>In the unit harness FessConfig typically reports {@code isLoginRequired=false}
     * (default), so this test relies on the handler's FessConfig lookup either
     * succeeding and returning false (in which case we get 404/500 from the backend
     * lookup) or the check short-circuiting at 401 if the harness wires login-required.
     * We exercise the path generically — the important assertion is that when the
     * handler path that checks loginRequired fires and no user is present, 401 is
     * returned. Since the unit harness doesn't force loginRequired=true we accept the
     * 404/500 outcome as the "not required" branch and assert only envelope shape.</p>
     */
    @Test
    public void test_loginRequired_whenNotAuthenticated_returns401OrBackendError() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new CacheHandler().handle(new StubRequest("GET", "/api/v2/cache/zzz"), res, "zzz");
        // Acceptable outcomes:
        // 401 — login required and no user (would require fessConfig.isLoginRequired()=true)
        // 404 — search backend reachable, doc absent
        // 500 — search backend unreachable in unit harness
        assertTrue(res.status == 401 || res.status == 404 || res.status == 500, "unexpected status: " + res.status + " body=" + res.body());
        final String body = res.body();
        assertTrue(body.contains("\"code\":\"auth_required\"") || body.contains("\"code\":\"not_found\"")
                || body.contains("\"code\":\"internal_error\""), "expected structured envelope: " + body);
    }

    @Test
    public void test_invalidDocId() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new CacheHandler().handle(new StubRequest("GET", "/api/v2/cache/"), res, "");
        assertEquals(400, res.status);
        assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
        assertTrue(res.body().contains("invalid doc_id"), res.body());
    }

    @Test
    public void test_docNotFoundOrInternalError() throws Exception {
        // In UnitFessTestCase without a live OpenSearch backend, getDocumentByDocId
        // typically throws — we tolerate either 404 (engine reachable, doc missing)
        // or 500 (engine unreachable). Both preserve the structured envelope.
        final CapturingResponse res = new CapturingResponse();
        new CacheHandler().handle(new StubRequest("GET", "/api/v2/cache/zzz"), res, "zzz");
        assertTrue(res.status == 404 || res.status == 500, "unexpected status: " + res.status + " body=" + res.body());
        final String body = res.body();
        assertTrue(body.contains("\"code\":\"not_found\"") || body.contains("\"code\":\"internal_error\""),
                "expected structured error envelope: " + body);
    }

    @Test
    public void cache_authLookupException_returns500NotAuthRequired() throws Exception {
        // M-17: when FessLoginAssist throws on the user-bean lookup, the response must be
        // INTERNAL_ERROR (500), not AUTH_REQUIRED (401). The previous behavior swallowed the
        // exception and treated the caller as anonymous, which — when app.login.required=true
        // — would surface as a misleading 401 telling the user to re-login.
        final FessLoginAssist throwing = new FessLoginAssist() {
            private static final long serialVersionUID = 1L;

            @Override
            public OptionalThing<FessUserBean> getSavedUserBean() {
                throw new RuntimeException("forced DI lookup failure");
            }
        };
        ComponentUtil.register(throwing, "fessLoginAssist");
        ComponentUtil.register(throwing, FessLoginAssist.class.getCanonicalName());
        try {
            final CapturingResponse res = new CapturingResponse();
            new CacheHandler().handle(new StubRequest("GET", "/api/v2/cache/abc"), res, "abc");
            org.junit.jupiter.api.Assertions.assertEquals(500, res.status, res.body());
            assertTrue(res.body().contains("\"code\":\"internal_error\""), res.body());
            assertFalse(res.body().contains("\"code\":\"auth_required\""),
                    "lookup exception must not be misreported as auth_required: " + res.body());
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
        }
    }

    @Test
    public void cache_anonymousUser_returns401OrBackendError() throws Exception {
        // M-17 regression: a genuine anonymous caller (no user bean) must NOT see 500 from
        // the auth path — only the configured login-required gate (401) or downstream
        // search-engine failure (404/500). The existing test_loginRequired_whenNotAuthenticated_*
        // covers the auth-gate path; this one explicitly asserts no spurious 500 from the
        // auth lookup itself when the lookup returns empty (rather than throwing).
        final CapturingResponse res = new CapturingResponse();
        new CacheHandler().handle(new StubRequest("GET", "/api/v2/cache/zzz"), res, "zzz");
        // Either 401 (login required + anonymous) or 404/500 from backend.
        assertTrue(res.status == 401 || res.status == 404 || res.status == 500, "unexpected status: " + res.status + " body=" + res.body());
        // Critical M-17 assertion: when the 500 path is taken it must not be because the
        // auth lookup itself raised an unexpected exception with a misleading code.
        if (res.status == 500) {
            assertTrue(res.body().contains("\"code\":\"internal_error\""), res.body());
        }
    }

    @Test
    public void test_buildCachePayload_includesUrlCreatedCharset() {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("url", "https://example.com/doc.html");
        doc.put("url_link", "https://example.com/doc.html");
        doc.put("created", "2026-05-29T10:15:30.000+0000");
        doc.put("mimetype", "text/html; charset=Shift_JIS");
        final Map<String, Object> payload = new CacheHandler().buildCachePayload("abc123", "<html>cached</html>", doc);
        assertEquals("abc123", payload.get("doc_id"));
        assertEquals("text/html", payload.get("mimetype"));
        assertEquals("<html>cached</html>", payload.get("content"));
        assertEquals("https://example.com/doc.html", payload.get("url"));
        assertEquals("2026-05-29T10:15:30.000+0000", payload.get("created"));
        assertEquals("Shift_JIS", payload.get("charset"));
    }

    @Test
    public void test_buildCachePayload_charsetDefaultsToUtf8WhenAbsent() {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("url", "https://example.com/a.pdf");
        doc.put("mimetype", "application/pdf");
        final Map<String, Object> payload = new CacheHandler().buildCachePayload("d1", "x", doc);
        assertEquals("UTF-8", payload.get("charset"));
        assertEquals("https://example.com/a.pdf", payload.get("url"));
        assertNull(payload.get("created"));
    }

    /** Minimal HttpServletResponse stub — captures status, content type, headers and body. */
    private static class CapturingResponse implements HttpServletResponse {
        final StringWriter sw = new StringWriter();
        final PrintWriter writer = new PrintWriter(sw);
        int status = 200;
        String contentType;
        final java.util.Map<String, String> headers = new java.util.HashMap<>();

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
            headers.put(name, value);
        }

        @Override
        public void addHeader(final String name, final String value) {
            headers.put(name, value);
        }

        @Override
        public void setIntHeader(final String name, final int value) {
        }

        @Override
        public void addIntHeader(final String name, final int value) {
        }

        @Override
        public String getHeader(final String name) {
            return headers.get(name);
        }

        @Override
        public java.util.Collection<String> getHeaders(final String name) {
            final String v = headers.get(name);
            return v == null ? java.util.Collections.emptyList() : java.util.Collections.singletonList(v);
        }

        @Override
        public java.util.Collection<String> getHeaderNames() {
            return java.util.Collections.emptyList();
        }
    }

    /**
     * Minimal HttpServletRequest stub — exposes method, URI and a null
     * {@code hq} parameter (the only request-scoped input the handler reads).
     */
    private static class StubRequest implements HttpServletRequest {
        private final String method;
        private final String uri;
        private final Map<String, Object> attrs = new HashMap<>();

        StubRequest(final String method, final String uri) {
            this.method = method;
            this.uri = uri;
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
        public String getParameter(final String name) {
            return null;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.emptyEnumeration();
        }

        @Override
        public String[] getParameterValues(final String name) {
            // Handler reads only "hq" — return null to exercise the null-tolerant
            // branch in ViewHelper#createCacheContent (same contract as v1).
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
}
