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
import java.util.List;
import java.util.Map;

import org.codelibs.fess.chat.ChatClient;
import org.codelibs.fess.chat.ChatClient.ChatResult;
import org.codelibs.fess.entity.ChatMessage;
import org.codelibs.fess.entity.ChatMessage.ChatSource;
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
 * Unit tests for {@link ChatHandler}.
 *
 * <p>The handler relies on the production {@code fess_config.properties} default
 * {@code rag.chat.enabled=false}; under that default every POST falls into the
 * "chat is not enabled" branch and we observe the v2 envelope shape. The
 * happy-path test enables RAG via {@link #enableRagChat()} and registers a stub
 * {@code chatClient} into the DI container to drive the success envelope.</p>
 */
public class ChatHandlerTest extends UnitFessTestCase {

    @Test
    public void test_rejectsGet() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new ChatHandler().handle(new StubRequest("GET", "/api/v2/chat"), res);
        assertEquals(405, res.status);
        assertTrue(res.body().contains("\"code\":\"method_not_allowed\""), res.body());
    }

    @Test
    public void test_chatDisabledReturnsInvalidRequest() throws Exception {
        // Default fess_config.properties has rag.chat.enabled=false, so the
        // disabled branch fires. SPA branches on the code field, not the message
        // text, so we don't pin the message string here.
        final CapturingResponse res = new CapturingResponse();
        new ChatHandler().handle(new StubRequest("POST", "/api/v2/chat").withJsonBody("{\"message\":\"hi\"}"), res);
        assertEquals(400, res.status);
        assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
    }

    @Test
    public void test_missingMessageReturnsInvalidRequest() throws Exception {
        // With rag.chat.enabled=false in the test config, the chat-disabled
        // branch fires first — observable outcome is the same 400/invalid_request.
        final CapturingResponse res = new CapturingResponse();
        new ChatHandler().handle(new StubRequest("POST", "/api/v2/chat").withJsonBody("{}"), res);
        assertEquals(400, res.status);
    }

    @Test
    public void test_oversizedMessageReturnsInvalidRequest() throws Exception {
        // rag.chat.message.max.length default = 4000. Send 4001 characters.
        final String big = "x".repeat(4001);
        final CapturingResponse res = new CapturingResponse();
        new ChatHandler().handle(new StubRequest("POST", "/api/v2/chat").withJsonBody("{\"message\":\"" + big + "\"}"), res);
        assertEquals(400, res.status);
    }

    @Test
    public void test_envelopeShapeContainsResponseAndStatusFields() throws Exception {
        // Even on the error path, the v2 envelope shape is the same — assert it
        // so a future shape regression in V2EnvelopeWriter fails this test loud.
        // The "version" field was removed from the v2 envelope in the new design.
        final CapturingResponse res = new CapturingResponse();
        new ChatHandler().handle(new StubRequest("POST", "/api/v2/chat").withJsonBody("{\"message\":\"hi\"}"), res);
        final String body = res.body();
        assertTrue(body.startsWith("{\"response\":{"), "body should start with v2 envelope, was: " + body);
        assertFalse(body.contains("\"version\""), "version field must not appear in v2 envelope: " + body);
        assertTrue(body.contains("\"status\":"), body);
    }

    @Test
    public void test_toSourceMaps_snakeCaseKeys() {
        // Verify that toSourceMaps converts ChatSource to snake_case wire keys.
        // rank is 1-based (src.getIndex()), url_link / go_url / doc_id are snake_case.
        final org.codelibs.fess.entity.ChatMessage.ChatSource src = new org.codelibs.fess.entity.ChatMessage.ChatSource();
        src.setIndex(1);
        src.setTitle("My Title");
        src.setUrl("https://example.com/doc");
        src.setDocId("doc-123");
        src.setSnippet("some snippet");
        src.setUrlLink("https://example.com/link");
        src.setGoUrl("https://go.example.com");
        final java.util.List<java.util.Map<String, Object>> maps = ChatHandler.toSourceMaps(java.util.List.of(src));
        assertEquals(1, maps.size());
        final java.util.Map<String, Object> m = maps.get(0);
        assertEquals("rank must be getIndex() value", 1, m.get("rank"));
        assertEquals("My Title", m.get("title"));
        assertEquals("https://example.com/doc", m.get("url"));
        assertEquals("doc_id must be snake_case", "doc-123", m.get("doc_id"));
        assertEquals("some snippet", m.get("snippet"));
        assertEquals("url_link must be snake_case", "https://example.com/link", m.get("url_link"));
        assertEquals("go_url must be snake_case", "https://go.example.com", m.get("go_url"));
        // Ensure no camelCase keys leaked through
        assertNull(m.get("docId"), "camelCase docId must not appear");
        assertNull(m.get("urlLink"), "camelCase urlLink must not appear");
        assertNull(m.get("goUrl"), "camelCase goUrl must not appear");
    }

    @Test
    public void test_toSourceMaps_nullFieldsOmitted() {
        // Null optional fields (title, url, doc_id, snippet, url_link, go_url) must
        // not appear in the output map — putIfNotNull skips them.
        final org.codelibs.fess.entity.ChatMessage.ChatSource src = new org.codelibs.fess.entity.ChatMessage.ChatSource();
        src.setIndex(2);
        // All optional fields left null
        final java.util.List<java.util.Map<String, Object>> maps = ChatHandler.toSourceMaps(java.util.List.of(src));
        final java.util.Map<String, Object> m = maps.get(0);
        assertEquals(2, m.get("rank"));
        assertFalse(m.containsKey("title"), "null title must be omitted");
        assertFalse(m.containsKey("url"), "null url must be omitted");
        assertFalse(m.containsKey("doc_id"), "null doc_id must be omitted");
        assertFalse(m.containsKey("snippet"), "null snippet must be omitted");
        assertFalse(m.containsKey("url_link"), "null url_link must be omitted");
        assertFalse(m.containsKey("go_url"), "null go_url must be omitted");
    }

    @Test
    public void test_handle_payloadTooLarge_returns413() throws Exception {
        // A body exceeding MAX_BODY_BYTES (32 KiB) must return HTTP 413 with the
        // payload_too_large error code — not 400 invalid_request.
        // Enable RAG chat so the handler proceeds past the feature-gate to body parsing.
        enableRagChat();
        final String bigBody = "{\"message\":\"" + "x".repeat(33 * 1024) + "\"}";
        final CapturingResponse res = new CapturingResponse();
        new ChatHandler().handle(new StubRequest("POST", "/api/v2/chat").withJsonBody(bigBody), res);
        assertEquals(413, res.status);
        assertTrue(res.body().contains("\"code\":\"payload_too_large\""), res.body());
    }

    @Test
    public void test_handle_unsupportedMediaType_returns415() throws Exception {
        // A POST with Content-Type text/plain must return HTTP 415 with the
        // unsupported_media_type error code — not 400 invalid_request.
        // Enable RAG chat so the handler proceeds past the feature-gate to body parsing.
        enableRagChat();
        final StubRequest req = new StubRequest("POST", "/api/v2/chat") {
            @Override
            public String getContentType() {
                return "text/plain";
            }
        };
        final CapturingResponse res = new CapturingResponse();
        new ChatHandler().handle(req, res);
        assertEquals(415, res.status);
        assertTrue(res.body().contains("\"code\":\"unsupported_media_type\""), res.body());
    }

    @Test
    public void test_happyPath_returnsSessionIdContentAndSources() throws Exception {
        // Full success path: enable RAG, register a fresh (empty) rate limiter so the first call
        // is allowed, and override the getChatClient seam to return a fixed ChatResult. The handler
        // must emit a v2 success envelope whose payload carries session_id, content, and
        // snake_case-mapped sources. (Previously @Disabled — the getChatClient seam is exactly the
        // scaffolding that rationale waited on; overriding it avoids the order-sensitive
        // ComponentUtil.register chatClient fallback.)
        enableRagChat();

        final ChatSource source = new ChatSource();
        source.setIndex(1);
        source.setTitle("Doc Title");
        source.setUrl("https://example.com/doc");
        source.setDocId("doc-1");
        final ChatMessage assistant = new ChatMessage("assistant", "hello from rag");
        // The handler reads sources from result.getMessage().getSources(), not the third
        // ChatResult arg — so set them on the ChatMessage and pass an empty list below.
        assistant.setSources(List.of(source));
        final ChatResult result = new ChatResult("sess-123", assistant, Collections.emptyList());
        final ChatClient stubClient = new ChatClient() {
            @Override
            public ChatResult chat(final String sessionId, final String userMessage, final String userId) {
                return result;
            }
        };

        // A fresh limiter with an empty CHAT bucket allows the first request for any user id.
        ComponentUtil.register(new LoginRateLimiter(), "loginRateLimiter");
        ComponentUtil.register(new LoginRateLimiter(), LoginRateLimiter.class.getCanonicalName());
        try {
            final CapturingResponse res = new CapturingResponse();
            final ChatHandler handler = new ChatHandler() {
                @Override
                protected String getUserId(final HttpServletRequest req) {
                    return "happy-user";
                }

                @Override
                protected ChatClient getChatClient() {
                    return stubClient;
                }
            };
            handler.handle(new StubRequest("POST", "/api/v2/chat").withJsonBody("{\"message\":\"hi\"}"), res);

            assertEquals(200, res.status);
            final String body = res.body();
            assertTrue(body.startsWith("{\"response\":{"), body);
            assertTrue(body.contains("\"session_id\":\"sess-123\""), body);
            assertTrue(body.contains("\"content\":\"hello from rag\""), body);
            assertTrue(body.contains("\"sources\":["), body);
            assertTrue(body.contains("\"rank\":1"), body);
            assertTrue(body.contains("\"doc_id\":\"doc-1\""), body);
            assertTrue(body.contains("\"title\":\"Doc Title\""), body);
        } finally {
            ComponentUtil.register(new LoginRateLimiter(), "loginRateLimiter");
            ComponentUtil.register(new LoginRateLimiter(), LoginRateLimiter.class.getCanonicalName());
        }
    }

    @Test
    public void chat_rateLimited_returns429() throws Exception {
        // Drive the chat handler past the chat-disabled gate by enabling RAG, then exhaust
        // the per-user CHAT bucket via a fresh limiter registered into DI. The 31st call
        // (default limit is 30/min) should be rejected with 429/rate_limited regardless
        // of the eventual ChatClient state — the handler must short-circuit before any
        // ChatClient dispatch.
        enableRagChat();
        // Fix the user id via the getUserId seam instead of stubbing SystemHelper/UserInfoHelper
        // (smart-deploy components whose ComponentUtil.register stubs are not reliably honored once
        // the shared test container has resolved the real ones across the full suite).
        final String userId = "rate-limited-user";
        final LoginRateLimiter rl = new LoginRateLimiter();
        // Saturate the CHAT bucket for the fixed user id the handler will resolve.
        for (int i = 0; i < 30; i++) {
            rl.allow(LoginRateLimiter.Scope.CHAT, userId, 30, 60);
        }
        ComponentUtil.register(rl, "loginRateLimiter");
        ComponentUtil.register(rl, LoginRateLimiter.class.getCanonicalName());
        try {
            final CapturingResponse res = new CapturingResponse();
            final ChatHandler handler = new ChatHandler() {
                @Override
                protected String getUserId(final jakarta.servlet.http.HttpServletRequest req) {
                    return userId;
                }
            };
            handler.handle(new StubRequest("POST", "/api/v2/chat").withJsonBody("{\"message\":\"hi\"}"), res);
            // The bucket for userId is pre-saturated (30/30), so this request is the 31st and must
            // be rejected deterministically before any ChatClient dispatch.
            assertEquals(429, res.status);
            assertTrue(res.body().contains("\"code\":\"rate_limited\""), res.body());
            assertTrue(res.body().contains("too many chat requests"), res.body());
        } finally {
            // Reset to a fresh limiter so neighbors don't see our saturated bucket.
            ComponentUtil.register(new LoginRateLimiter(), "loginRateLimiter");
            ComponentUtil.register(new LoginRateLimiter(), LoginRateLimiter.class.getCanonicalName());
        }
    }

    @Test
    public void chat_anonymousUser_nullUserId_notRateLimited() throws Exception {
        // When getUserId returns null, StringUtil.isNotBlank(null) is false, so the per-user
        // throttle is skipped entirely — even with a saturated limiter and a positive chat limit.
        // The handler must NOT return 429/rate_limited; it proceeds past the rate-limit gate.
        // Without a real ChatClient the subsequent chat() call fails with INTERNAL_ERROR (500),
        // which proves the rate-limit branch was bypassed.
        enableRagChat();
        final LoginRateLimiter rl = new LoginRateLimiter();
        // Saturate the null-key bucket — this would 429 if null reached the limiter.
        for (int i = 0; i < 30; i++) {
            rl.allow(LoginRateLimiter.Scope.CHAT, "some-user", 30, 60);
        }
        ComponentUtil.register(rl, "loginRateLimiter");
        ComponentUtil.register(rl, LoginRateLimiter.class.getCanonicalName());
        try {
            final CapturingResponse res = new CapturingResponse();
            final ChatHandler handler = new ChatHandler() {
                @Override
                protected String getUserId(final jakarta.servlet.http.HttpServletRequest req) {
                    return null;
                }
            };
            handler.handle(new StubRequest("POST", "/api/v2/chat").withJsonBody("{\"message\":\"hi\"}"), res);
            // Must NOT be a 429 rate_limited response — the anonymous bypass must apply.
            assertFalse("anonymous null userId must not yield 429, got: " + res.body(), res.body().contains("\"code\":\"rate_limited\""));
            assertTrue("anonymous null userId must not yield 429 status, was: " + res.status, res.status != 429);
        } finally {
            ComponentUtil.register(new LoginRateLimiter(), "loginRateLimiter");
            ComponentUtil.register(new LoginRateLimiter(), LoginRateLimiter.class.getCanonicalName());
        }
    }

    @Test
    public void chat_anonymousUser_blankUserId_notRateLimited() throws Exception {
        // When getUserId returns a blank string ("" or whitespace), StringUtil.isNotBlank is false,
        // so the per-user throttle is skipped — the handler must NOT return 429/rate_limited.
        enableRagChat();
        final LoginRateLimiter rl = new LoginRateLimiter();
        for (int i = 0; i < 30; i++) {
            rl.allow(LoginRateLimiter.Scope.CHAT, "some-user", 30, 60);
        }
        ComponentUtil.register(rl, "loginRateLimiter");
        ComponentUtil.register(rl, LoginRateLimiter.class.getCanonicalName());
        try {
            final CapturingResponse res = new CapturingResponse();
            final ChatHandler handler = new ChatHandler() {
                @Override
                protected String getUserId(final jakarta.servlet.http.HttpServletRequest req) {
                    return "   ";
                }
            };
            handler.handle(new StubRequest("POST", "/api/v2/chat").withJsonBody("{\"message\":\"hi\"}"), res);
            assertFalse("anonymous blank userId must not yield 429, got: " + res.body(), res.body().contains("\"code\":\"rate_limited\""));
            assertTrue("anonymous blank userId must not yield 429 status, was: " + res.status, res.status != 429);
        } finally {
            ComponentUtil.register(new LoginRateLimiter(), "loginRateLimiter");
            ComponentUtil.register(new LoginRateLimiter(), LoginRateLimiter.class.getCanonicalName());
        }
    }

    @Test
    public void chat_anonymousUser_emptyStringUserId_notRateLimited() throws Exception {
        // When getUserId returns an empty string, StringUtil.isNotBlank("") is false,
        // so the per-user throttle is skipped — the handler must NOT return 429/rate_limited.
        enableRagChat();
        final LoginRateLimiter rl = new LoginRateLimiter();
        for (int i = 0; i < 30; i++) {
            rl.allow(LoginRateLimiter.Scope.CHAT, "some-user", 30, 60);
        }
        ComponentUtil.register(rl, "loginRateLimiter");
        ComponentUtil.register(rl, LoginRateLimiter.class.getCanonicalName());
        try {
            final CapturingResponse res = new CapturingResponse();
            final ChatHandler handler = new ChatHandler() {
                @Override
                protected String getUserId(final jakarta.servlet.http.HttpServletRequest req) {
                    return "";
                }
            };
            handler.handle(new StubRequest("POST", "/api/v2/chat").withJsonBody("{\"message\":\"hi\"}"), res);
            assertFalse("anonymous empty userId must not yield 429, got: " + res.body(), res.body().contains("\"code\":\"rate_limited\""));
            assertTrue("anonymous empty userId must not yield 429 status, was: " + res.status, res.status != 429);
        } finally {
            ComponentUtil.register(new LoginRateLimiter(), "loginRateLimiter");
            ComponentUtil.register(new LoginRateLimiter(), LoginRateLimiter.class.getCanonicalName());
        }
    }

    /** Enables RAG chat by registering a fess-config subclass that returns true. */
    private static void enableRagChat() {
        ComponentUtil.setFessConfig(new org.codelibs.fess.mylasta.direction.FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isRagChatEnabled() {
                return true;
            }
        });
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
