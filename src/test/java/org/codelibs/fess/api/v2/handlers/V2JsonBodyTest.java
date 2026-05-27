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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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
 * Unit tests for {@link V2JsonBody}.
 *
 * <p>Each scenario builds a minimal {@link HttpServletRequest} stub via
 * {@link #stub(String, String)} that exposes only the surface area the
 * helper actually touches: {@code getContentType()} and
 * {@code getInputStream()}. The stub is a local copy of the pattern in
 * {@code SearchApiV2ManagerTest} extended with a byte-backed
 * {@link ServletInputStream}; the plan defers extracting it to a shared
 * fixture until Plan 4.</p>
 */
public class V2JsonBodyTest {

    @Test
    public void test_parsesJsonObject() throws Exception {
        final HttpServletRequest req = stub("{\"username\":\"alice\",\"password\":\"x\"}", "application/json");
        final Map<String, Object> body = V2JsonBody.read(req, 1024);
        assertEquals("alice", body.get("username"));
    }

    @Test
    public void test_emptyBodyReturnsEmptyMap() throws Exception {
        final HttpServletRequest req = stub("", "application/json");
        assertTrue(V2JsonBody.read(req, 1024).isEmpty());
    }

    @Test
    public void test_rejectsOversizedBody() {
        final byte[] big = new byte[2048];
        final HttpServletRequest req = stub(new String(big), "application/json");
        assertThrows(V2JsonBody.PayloadTooLargeException.class, () -> V2JsonBody.read(req, 1024));
    }

    @Test
    public void test_rejectsNonJsonContentType() {
        final HttpServletRequest req = stub("{\"k\":1}", "text/plain");
        assertThrows(V2JsonBody.UnsupportedMediaTypeException.class, () -> V2JsonBody.read(req, 1024));
    }

    @Test
    public void test_rejectsMalformedJson() {
        final HttpServletRequest req = stub("{not json", "application/json");
        assertThrows(V2JsonBody.MalformedJsonException.class, () -> V2JsonBody.read(req, 1024));
    }

    @Test
    public void test_rejectsNullContentType() {
        // A null Content-Type (header absent) must be rejected — callers cannot assume JSON.
        final HttpServletRequest req = stub("{\"k\":1}", null);
        assertThrows(V2JsonBody.UnsupportedMediaTypeException.class, () -> V2JsonBody.read(req, 1024));
    }

    @Test
    public void test_rejectsNonUtf8Charset() {
        // application/json with an explicit non-UTF-8 charset must be rejected.
        final HttpServletRequest req = stub("{\"k\":1}", "application/json; charset=Shift_JIS");
        assertThrows(V2JsonBody.UnsupportedMediaTypeException.class, () -> V2JsonBody.read(req, 1024));
    }

    @Test
    public void test_acceptsExplicitUtf8Charset() throws Exception {
        // Explicit charset=utf-8 is acceptable (common from curl/Postman).
        final HttpServletRequest req = stub("{\"k\":\"v\"}", "application/json; charset=utf-8");
        final Map<String, Object> body = V2JsonBody.read(req, 1024);
        assertEquals("v", body.get("k"));
    }

    @Test
    public void test_stripsLeadingBom() throws Exception {
        // A body prefixed with the UTF-8 BOM (EF BB BF) should parse successfully after stripping.
        final byte[] json = "{\"bom\":true}".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        final byte[] withBom = new byte[json.length + 3];
        withBom[0] = (byte) 0xEF;
        withBom[1] = (byte) 0xBB;
        withBom[2] = (byte) 0xBF;
        System.arraycopy(json, 0, withBom, 3, json.length);
        final HttpServletRequest req = stubBytes(withBom, "application/json");
        final Map<String, Object> body = V2JsonBody.read(req, 1024);
        assertEquals(Boolean.TRUE, body.get("bom"));
    }

    // ── Precise Content-Type charset parameter parsing ───────────────────────────

    @Test
    public void test_acceptsJsonWithUtf8Charset() throws Exception {
        // application/json; charset=utf-8 → accepted (the standard case).
        final HttpServletRequest req = stub("{\"k\":\"v\"}", "application/json; charset=utf-8");
        final Map<String, Object> body = V2JsonBody.read(req, 1024);
        assertEquals("v", body.get("k"));
    }

    @Test
    public void test_rejectsUtf16Charset() {
        // application/json; charset=utf-16 → rejected (non-UTF-8 charset).
        final HttpServletRequest req = stub("{\"k\":1}", "application/json; charset=utf-16");
        assertThrows(V2JsonBody.UnsupportedMediaTypeException.class, () -> V2JsonBody.read(req, 1024));
    }

    @Test
    public void test_rejectsIso88591Charset() {
        // application/json; charset=iso-8859-1 → rejected.
        final HttpServletRequest req = stub("{\"k\":1}", "application/json; charset=iso-8859-1");
        assertThrows(V2JsonBody.UnsupportedMediaTypeException.class, () -> V2JsonBody.read(req, 1024));
    }

    @Test
    public void test_acceptsXcharsetParamThatContainsCharsetSubstring() throws Exception {
        // The fix regression test: a parameter whose NAME is "x-charset" (not "charset")
        // must NOT trigger the charset validation — only an exact name match on "charset"
        // should do so. The old indexOf("charset=") scan would falsely reject this.
        final HttpServletRequest req = stub("{\"k\":\"v\"}", "application/json; x-charset=utf-16");
        // Must NOT throw UnsupportedMediaTypeException — "x-charset" is a different param.
        final Map<String, Object> body = V2JsonBody.read(req, 1024);
        assertEquals("v", body.get("k"));
    }

    @Test
    public void test_acceptsQuotedUtf8Charset() throws Exception {
        // charset="utf-8" (quoted value) is acceptable; the parser strips quotes.
        final HttpServletRequest req = stub("{\"k\":\"v\"}", "application/json; charset=\"utf-8\"");
        final Map<String, Object> body = V2JsonBody.read(req, 1024);
        assertEquals("v", body.get("k"));
    }

    @Test
    public void test_acceptsUppercaseCharsetUtf8() throws Exception {
        // charset=UTF-8 (uppercase) must be accepted because the code lower-cases the
        // full Content-Type before splitting on ';'.
        final HttpServletRequest req = stub("{\"k\":\"v\"}", "application/json; charset=UTF-8");
        final Map<String, Object> body = V2JsonBody.read(req, 1024);
        assertEquals("v", body.get("k"));
    }

    @Test
    public void test_paramWithoutEqualsSign_doesNotCrashAndDoesNotReject() throws Exception {
        // A parameter with no '=' (e.g. "something") must be silently skipped —
        // no crash, no false rejection.
        final HttpServletRequest req = stub("{\"k\":\"v\"}", "application/json; something");
        final Map<String, Object> body = V2JsonBody.read(req, 1024);
        assertEquals("v", body.get("k"));
    }

    @Test
    public void test_multipleParamsIncludingCharset_accepted() throws Exception {
        // Multi-param ordering: charset appears after another param — must still be found.
        final HttpServletRequest req = stub("{\"k\":\"v\"}", "application/json; boundary=x; charset=utf-8");
        final Map<String, Object> body = V2JsonBody.read(req, 1024);
        assertEquals("v", body.get("k"));
    }

    @Test
    public void test_multipleParamsIncludingCharset_rejected() {
        // Multi-param ordering: non-UTF-8 charset after another param — must be rejected.
        final HttpServletRequest req = stub("{\"k\":1}", "application/json; boundary=x; charset=utf-16");
        assertThrows(V2JsonBody.UnsupportedMediaTypeException.class, () -> V2JsonBody.read(req, 1024));
    }

    @Test
    public void test_rejectsDeeplyNestedJson() {
        // A 33-deep nesting exceeds the maxNestingDepth(32) constraint in the MAPPER.
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 33; i++) {
            sb.append("{\"a\":");
        }
        sb.append("1");
        for (int i = 0; i < 33; i++) {
            sb.append("}");
        }
        final HttpServletRequest req = stub(sb.toString(), "application/json");
        assertThrows(V2JsonBody.MalformedJsonException.class, () -> V2JsonBody.read(req, 1 << 20));
    }

    private static HttpServletRequest stubBytes(final byte[] body, final String contentType) {
        return new StubBytesRequest(body, contentType);
    }

    private static HttpServletRequest stub(final String body, final String contentType) {
        return new StubRequest(body, contentType);
    }

    /**
     * Adapted from {@code SearchApiV2ManagerTest.StubRequest} — exposes a
     * byte-backed body + content type so {@link V2JsonBody} can read the
     * request stream without dragging in a servlet container.
     */
    private static class StubRequest implements HttpServletRequest {
        private final byte[] body;
        private final String contentType;
        private final Map<String, Object> attrs = new HashMap<>();

        StubRequest(final String body, final String contentType) {
            this.body = body == null ? new byte[0] : body.getBytes(StandardCharsets.UTF_8);
            this.contentType = contentType;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public ServletInputStream getInputStream() {
            final ByteArrayInputStream bais = new ByteArrayInputStream(body);
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

        // ----- unused HttpServletRequest surface (throw or return defaults) -----

        @Override
        public String getServletPath() {
            return "/api/v2/test";
        }

        @Override
        public String getMethod() {
            return "POST";
        }

        @Override
        public String getRequestURI() {
            return getServletPath();
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
            return new StringBuffer(getServletPath());
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
            return body.length;
        }

        @Override
        public long getContentLengthLong() {
            return body.length;
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
     * A variant of {@link StubRequest} that accepts a raw {@code byte[]} body directly,
     * used for BOM-stripping tests where the bytes cannot be round-tripped through a String.
     */
    private static class StubBytesRequest extends StubRequest {
        private final byte[] rawBytes;

        StubBytesRequest(final byte[] rawBytes, final String contentType) {
            super("", contentType); // delegate to superclass but override body via stream
            this.rawBytes = rawBytes;
        }

        @Override
        public ServletInputStream getInputStream() {
            final ByteArrayInputStream bais = new ByteArrayInputStream(rawBytes);
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
                }
            };
        }
    }
}
