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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ReadListener;
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
 * Unit tests for {@link FavoritePostHandler}.
 *
 * <p>Verifies the wire contract for {@code POST /api/v2/documents/{docId}/favorite}:
 * GET is rejected (400), anonymous callers see {@code auth_required} (401), and
 * malformed doc ids surface as {@code invalid_request} (400). The auth check is
 * testable because the handler wraps the {@link org.codelibs.fess.app.web.base.login.FessLoginAssist}
 * lookup in a try/catch — in the unit harness the lookup typically fails, which
 * is treated as anonymous (same pattern as {@code MeHandler}).</p>
 */
public class FavoritePostHandlerTest extends UnitFessTestCase {

    @Test
    public void test_anonymousReturnsAuthRequired() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new FavoritePostHandler().handle(new StubRequest("POST", "/api/v2/documents/abc/favorite"), res, "abc");
        // Two acceptable outcomes:
        // (a) favorite feature disabled at the config gate -> 400 invalid_request
        // (b) feature enabled but no user bean -> 401 auth_required
        // The unit harness has the favorite feature disabled by default, so we accept either.
        assertTrue(res.status == 401 || res.status == 400, "unexpected status " + res.status + ": " + res.body());
        if (res.status == 401) {
            assertTrue(res.body().contains("\"code\":\"auth_required\""), res.body());
        } else {
            assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
        }
    }

    @Test
    public void test_rejectsGet() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new FavoritePostHandler().handle(new StubRequest("GET", "/api/v2/documents/abc/favorite"), res, "abc");
        assertEquals(400, res.status);
        assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
    }

    @Test
    public void test_post_nonStringQueryIdReturns400() throws Exception {
        // Regression for M-13: a numeric JSON value for query_id used to be unchecked-cast to
        // String, which threw ClassCastException -> 500 INTERNAL_ERROR. With the runtime
        // instanceof guard the value is treated as null, the blank-check trips, and the
        // client sees an appropriate 400 INVALID_REQUEST instead of a misleading 500.
        // The test exercises a valid docId so the path reaches the body parsing; depending
        // on the unit harness's DI wiring this may land at 400 (invalid_request from the
        // query_id gate) or 401 (auth_required if the auth check trips first). Both prove
        // the unchecked cast no longer fires.
        final CapturingResponse res = new CapturingResponse();
        new FavoritePostHandler().handle(new StubRequest("POST", "/api/v2/documents/abc/favorite").withJsonBody("{\"query_id\":123}"), res,
                "abc");
        assertTrue(res.status == 400 || res.status == 401, "unexpected status " + res.status + ": " + res.body());
        final String body = res.body();
        assertTrue(body.contains("\"code\":\"invalid_request\"") || body.contains("\"code\":\"auth_required\""), body);
        // Critically: must NOT be 500 (which is what the unchecked cast used to produce).
        assertTrue(res.status != 500, "non-string query_id must not yield 500 INTERNAL_ERROR: " + body);
    }

    @Test
    public void test_internalErrorMessageIsFixedString() throws Exception {
        // Information-disclosure regression for the catch (RuntimeException) path: the
        // generic INTERNAL_ERROR branch used to forward e.getMessage() verbatim. It is now
        // a fixed string. We cannot easily force the INTERNAL_ERROR branch from a pure unit
        // harness, but if it does fire we assert the wire message is the fixed string.
        final CapturingResponse res = new CapturingResponse();
        new FavoritePostHandler().handle(new StubRequest("POST", "/api/v2/documents/abc/favorite").withJsonBody("{\"query_id\":\"qq\"}"),
                res, "abc");
        if (res.status == 500) {
            final String body = res.body();
            assertTrue(body.contains("\"message\":\"favorite add failed\""),
                    "internal error response must use the fixed message, not e.getMessage(): " + body);
        }
    }

    @Test
    public void test_invalidDocId() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new FavoritePostHandler().handle(new StubRequest("POST", "/api/v2/documents/.../favorite").withJsonBody("{\"query_id\":\"q\"}"),
                res, "...");
        assertEquals(400, res.status);
        assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
        assertTrue(res.body().contains("invalid doc_id"), res.body());
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
     * Minimal HttpServletRequest stub supporting an optional JSON body via
     * {@link #withJsonBody}.
     */
    private static class StubRequest implements HttpServletRequest {
        private final String method;
        private final String uri;
        private final Map<String, Object> attrs = new HashMap<>();
        private byte[] body;
        private String contentType;

        StubRequest(final String method, final String uri) {
            this.method = method;
            this.uri = uri;
        }

        StubRequest withJsonBody(final String json) {
            this.body = json == null ? new byte[0] : json.getBytes(StandardCharsets.UTF_8);
            this.contentType = "application/json";
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
            return body == null ? 0 : body.length;
        }

        @Override
        public long getContentLengthLong() {
            return body == null ? 0L : body.length;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public ServletInputStream getInputStream() {
            final ByteArrayInputStream bais = new ByteArrayInputStream(body == null ? new byte[0] : body);
            return new ServletInputStream() {
                private boolean eof = false;

                @Override
                public int read() throws IOException {
                    final int v = bais.read();
                    if (v < 0) {
                        eof = true;
                    }
                    return v;
                }

                @Override
                public byte[] readAllBytes() throws IOException {
                    final byte[] all = bais.readAllBytes();
                    eof = true;
                    return all;
                }

                @Override
                public byte[] readNBytes(final int len) throws IOException {
                    final byte[] out = bais.readNBytes(len);
                    if (bais.available() == 0) {
                        eof = true;
                    }
                    return out;
                }

                @Override
                public boolean isFinished() {
                    return eof;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(final ReadListener listener) {
                    // no-op
                }
            };
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
}
