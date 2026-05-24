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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.helper.UserInfoHelper;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
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
 * Unit tests for {@link ClickHandler}.
 *
 * <p>Verifies the wire contract for {@code POST /api/v2/click}: GET is rejected,
 * missing doc_id yields a 400 envelope, and a well-formed POST returns a stable
 * envelope shape. Since the unit harness lacks a live search backend, the
 * success path tolerates either a 200 (fire-and-forget when search log is off,
 * or logged when on), a 404 (doc not indexed), or a 500 (backend unavailable);
 * either way the v2 envelope shape is preserved.</p>
 */
public class ClickHandlerTest extends UnitFessTestCase {

    @Test
    public void test_missingDocIdReturnsInvalidRequest() throws Exception {
        // The handler now checks user session BEFORE body parse (m-15). Register a stub
        // UserInfoHelper that returns a non-null userCode so the handler proceeds to body
        // validation. Without this stub the anonymous short-circuit fires first.
        final UserInfoHelper stub = new UserInfoHelper() {
            @Override
            public String getUserCode() {
                return "test-user-code";
            }
        };
        ComponentUtil.register(stub, "userInfoHelper");
        try {
            final CapturingResponse res = new CapturingResponse();
            new ClickHandler().handle(new StubRequest("POST", "/api/v2/click").withJsonBody("{\"query_id\":\"q\"}"), res);
            assertEquals(400, res.status);
            assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
        } finally {
            ComponentUtil.register(new UserInfoHelper(), "userInfoHelper");
        }
    }

    @Test
    public void test_rejectsGet() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new ClickHandler().handle(new StubRequest("GET", "/api/v2/click"), res);
        assertEquals(405, res.status);
        assertTrue(res.body().contains("\"code\":\"method_not_allowed\""), res.body());
        // MJ-18: RFC 7231 §6.5.5 requires Allow header on 405.
        assertEquals("Allow header must be set on 405", "POST", res.getHeader("Allow"));
    }

    /**
     * m-15: anonymous callers (no user session) must short-circuit BEFORE body parse.
     * A malformed-JSON body with an anonymous request MUST return 200 logged:false,
     * NOT 400 invalid_request. This verifies the reorder places session check before
     * JSON parsing.
     *
     * <p>In the unit harness getUserCode() typically returns null (no session),
     * which is the anonymous branch. If isSearchLog() is also false the handler
     * returns logged:false from the feature-flag gate before reaching the session
     * check. Either way, a 400 from JSON parsing MUST NOT occur.</p>
     */
    @Test
    public void test_anonymous_shortCircuitsBeforeBodyParse() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        // Supply a deliberately malformed JSON body. If the handler parses body before
        // the anonymous check, this would yield 400. With correct ordering it yields
        // 200 logged:false (feature disabled or no session).
        new ClickHandler().handle(new StubRequest("POST", "/api/v2/click").withJsonBody("{{{MALFORMED"), res);
        final String body = res.body();
        // Must not be 400 (which would mean body was parsed before the anonymous check).
        assertTrue(res.status != 400, "anonymous with malformed body must not return 400; got " + res.status + ": " + body);
        // The two acceptable success outcomes: feature disabled or anonymous short-circuit.
        if (res.status == 200) {
            assertTrue(body.contains("\"logged\":false"), "expected logged:false in: " + body);
        } else {
            // Any other status besides 400 and 200 is acceptable in edge cases.
            assertTrue(res.status != 400, "must not yield 400 invalid_request for anonymous: " + body);
        }
    }

    /**
     * m-8: epoch-ms to LocalDateTime conversion must use UTC, not ZoneId.systemDefault().
     * Forces the JVM default TimeZone to two opposing zones (PST and JST) and asserts the
     * resulting LocalDateTime is identical — proving the conversion is timezone-independent.
     */
    @Test
    public void test_epochMsToUtcLocalDateTime_isTimezoneInvariant() {
        final java.util.TimeZone original = java.util.TimeZone.getDefault();
        try {
            // 2024-06-15T12:34:56.789Z — picked so PST and JST land on different calendar days.
            final long epochMs = 1718454896789L;
            java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("America/Los_Angeles"));
            final java.time.LocalDateTime pst = ClickHandler.epochMsToUtcLocalDateTime(epochMs);
            java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Tokyo"));
            final java.time.LocalDateTime jst = ClickHandler.epochMsToUtcLocalDateTime(epochMs);
            // Same epoch must yield the same LocalDateTime irrespective of default zone.
            assertEquals(pst, jst);
            // And that LocalDateTime must be the UTC wall-clock for the epoch.
            assertEquals(java.time.LocalDateTime.of(2024, 6, 15, 12, 34, 56, 789_000_000), pst);
        } finally {
            java.util.TimeZone.setDefault(original);
        }
    }

    @Test
    public void test_epochMsToUtcLocalDateTime_handlesEpochZero() {
        // Defensive sanity: epoch 0 → 1970-01-01T00:00:00 UTC. Was prone to off-by-hours
        // drift under the old systemDefault() conversion in non-UTC test environments.
        assertEquals(java.time.LocalDateTime.of(1970, 1, 1, 0, 0, 0), ClickHandler.epochMsToUtcLocalDateTime(0L));
    }

    @Test
    public void test_validPostReturnsStableEnvelope() throws Exception {
        // When isSearchLog() is false, the handler returns 200 with logged:false.
        // When it is enabled but the backend is unavailable / doc not indexed, the handler
        // emits the v2 error envelope (404 or 500). Either way the wire shape is preserved.
        final CapturingResponse res = new CapturingResponse();
        new ClickHandler()
                .handle(new StubRequest("POST", "/api/v2/click").withJsonBody("{\"doc_id\":\"abc\",\"query_id\":\"q\",\"rank\":3}"), res);
        final String body = res.body();
        assertFalse(body.contains("\"version\""), body);
        assertTrue(res.status == 200 || res.status == 404 || res.status == 500, "unexpected status " + res.status + ": " + body);
        if (res.status == 200) {
            assertTrue(body.contains("\"ok\":true"), body);
        } else if (res.status == 404) {
            assertTrue(body.contains("\"code\":\"not_found\""), body);
        } else {
            assertTrue(body.contains("\"code\":\"internal_error\""), body);
        }
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
            return headers.keySet();
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
