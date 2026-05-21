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

import org.codelibs.fess.mylasta.direction.FessConfig;
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
 * Unit tests for {@link ChatStreamHandler}.
 *
 * <p>The handler emits SSE (Server-Sent Events) on every code path — including
 * error branches — because the static theme JS reads from a single SSE parser
 * regardless of outcome. Under the default {@code fess_config.properties} where
 * {@code rag.chat.enabled=false}, the disabled-feature branch fires for every
 * POST; we exercise the SSE error shape ({@code event: error\ndata: {...}}) and
 * the canonical SSE response headers across that path.</p>
 */
public class ChatStreamHandlerTest extends UnitFessTestCase {

    @Test
    public void test_rejectsGet() throws Exception {
        // Spec §7.2 lists GET; Plan 4 deliberately implements POST-only and rejects GET.
        // See §Risks (1) — POST lets us read the body shape that v2 standardizes on.
        final CapturingResponse res = new CapturingResponse();
        new ChatStreamHandler().handle(new StubRequest("GET", "/api/v2/chat/stream"), res);
        // GET rejected via SSE error event then close. The handler still sets
        // text/event-stream content type before writing; assert both.
        assertEquals("text/event-stream", contentTypeMimeOnly(res));
        assertTrue(res.body().contains("event: error"));
        assertTrue(res.body().contains("\"errorCode\":\"method_not_allowed\""));
    }

    @Test
    public void test_chatDisabledEmitsSseError() throws Exception {
        // Default fess_config.properties has rag.chat.enabled=false. Without the
        // feature enabled the stream handler emits one error SSE event and closes.
        final CapturingResponse res = new CapturingResponse();
        new ChatStreamHandler().handle(new StubRequest("POST", "/api/v2/chat/stream").withJsonBody("{\"message\":\"hi\"}"), res);
        assertEquals("text/event-stream", contentTypeMimeOnly(res));
        assertTrue(res.body().contains("event: error"));
        assertTrue(res.body().contains("\"errorCode\":\"chat_disabled\""));
    }

    @Test
    public void test_missingMessageEmitsSseError() throws Exception {
        // Enable chat so the handler proceeds past the chat-disabled gate and
        // hits the missing-message branch we want to exercise here.
        enableRagChat();
        final CapturingResponse res = new CapturingResponse();
        new ChatStreamHandler().handle(new StubRequest("POST", "/api/v2/chat/stream").withJsonBody("{}"), res);
        assertTrue(res.body().contains("event: error"));
        assertTrue(res.body().contains("\"errorCode\":\"missing_message\""));
    }

    @Test
    public void test_oversizedMessageEmitsSseError() throws Exception {
        // Enable chat so the handler proceeds past the chat-disabled gate and
        // hits the message-too-long branch we want to exercise here.
        enableRagChat();
        final String big = "x".repeat(4001);
        final CapturingResponse res = new CapturingResponse();
        new ChatStreamHandler().handle(new StubRequest("POST", "/api/v2/chat/stream").withJsonBody("{\"message\":\"" + big + "\"}"), res);
        assertTrue(res.body().contains("event: error"));
        assertTrue(res.body().contains("\"errorCode\":\"message_too_long\""));
    }

    /**
     * Overrides the default {@code rag.chat.enabled=false} via a {@link FessConfig.SimpleImpl}
     * subclass so we can reach the post-gate validation branches.
     */
    private static void enableRagChat() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isRagChatEnabled() {
                return true;
            }
        });
    }

    @Test
    public void test_sseHeadersAreSet() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new ChatStreamHandler().handle(new StubRequest("POST", "/api/v2/chat/stream").withJsonBody("{\"message\":\"hi\"}"), res);
        // Even on the chat-disabled path, the handler sets SSE headers before
        // writing the error event. The static theme JS client uses them to
        // distinguish SSE streams from JSON responses.
        assertEquals("text/event-stream", contentTypeMimeOnly(res));
        assertEquals("no-cache", res.getHeader("Cache-Control"));
        assertEquals("no", res.getHeader("X-Accel-Buffering"));
    }

    @Test
    public void test_sendSseEvent_emitsCorrectWireFormat() {
        // sendSseEvent is protected and lives in the same package; exercise it directly
        // against a StringWriter so we lock down the v1-compatible wire format the static
        // theme JS parser depends on: "event: <name>\ndata: <json>\n\n".
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        new ChatStreamHandler().sendSseEvent(pw, "phase", new java.util.LinkedHashMap<>(Map.of("phase", "retrieval", "status", "start")));
        pw.flush();
        final String out = sw.toString();
        // The event line is fixed; the data line is JSON whose key order depends on map
        // iteration so we assert on its structural pieces rather than full equality.
        assertTrue(out.startsWith("event: phase\n"), out);
        assertTrue(out.endsWith("\n\n"), out);
        assertTrue(out.contains("data: {"), out);
        assertTrue(out.contains("\"phase\":\"retrieval\""), out);
        assertTrue(out.contains("\"status\":\"start\""), out);
    }

    @Test
    public void test_oversizedBodyEmitsSseErrorAndCloses() throws Exception {
        // V2JsonBody caps the body at MAX_BODY_BYTES (32KiB for the streaming handler).
        // Any larger payload must produce a single SSE error event with errorCode
        // "invalid_request", and the stream is closed thereafter. Chat must be enabled so
        // the request gets past the disabled gate and reaches the body-parse branch.
        enableRagChat();
        final String body = "{\"message\":\"" + "x".repeat(40 * 1024) + "\"}";
        final CapturingResponse res = new CapturingResponse();
        new ChatStreamHandler().handle(new StubRequest("POST", "/api/v2/chat/stream").withJsonBody(body), res);
        assertEquals("text/event-stream", contentTypeMimeOnly(res));
        final String out = res.body();
        assertTrue(out.contains("event: error"), out);
        assertTrue(out.contains("\"errorCode\":\"invalid_request\""), out);
        // The stream must end with the SSE record terminator and contain exactly one event
        // (no second event after closing) — assert the record-terminator suffix.
        assertTrue(out.endsWith("\n\n"), out);
        org.junit.jupiter.api.Assertions.assertEquals(1, countOccurrences(out, "event: "), "expected exactly one SSE event: " + out);
    }

    private static int countOccurrences(final String haystack, final String needle) {
        int n = 0;
        int idx = 0;
        while ((idx = haystack.indexOf(needle, idx)) >= 0) {
            n++;
            idx += needle.length();
        }
        return n;
    }

    private static String contentTypeMimeOnly(final CapturingResponse res) {
        final String ct = res.contentType;
        if (ct == null) {
            return null;
        }
        final int sep = ct.indexOf(';');
        return (sep < 0 ? ct : ct.substring(0, sep)).trim();
    }

    /** Minimal HttpServletResponse stub — captures status, content type, headers and body. */
    private static class CapturingResponse implements HttpServletResponse {
        final StringWriter sw = new StringWriter();
        final PrintWriter writer = new PrintWriter(sw);
        final Map<String, String> headers = new HashMap<>();
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
            return headers.containsKey(name);
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
            headers.put(name, Integer.toString(value));
        }

        @Override
        public void addIntHeader(final String name, final int value) {
            headers.put(name, Integer.toString(value));
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
     * Minimal HttpServletRequest stub. Supports a JSON body (via {@link #withJsonBody}) — the
     * remote address defaults to "127.0.0.1" since this handler does not key off the IP.
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
