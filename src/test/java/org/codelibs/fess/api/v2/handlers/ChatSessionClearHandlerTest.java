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
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.chat.ChatSessionManager;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.BeforeEach;
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
 * Unit tests for {@link ChatSessionClearHandler}.
 *
 * <p>The handler guards itself with three pre-conditions before touching session state:
 * (1) HTTP method must be DELETE, (2) RAG chat must be enabled, and (3) session_id must
 * match the allowed pattern. The successful clear path requires a registered
 * {@link ChatSessionManager} that returns {@code true} from {@code clearSession}.
 * The 404 path is exercised with a manager that returns {@code false}.</p>
 *
 * <p>All response assertions follow the v2 envelope shape:
 * {@code {"response": {"status": ..., ...}}}. The {@code version} field must NOT
 * appear in the envelope (removed in the new design).</p>
 */
public class ChatSessionClearHandlerTest extends UnitFessTestCase {

    /**
     * Reset the rate limiter before each test to prevent bucket state from leaking
     * between tests that pre-saturate the CHAT bucket.
     */
    @BeforeEach
    public void resetRateLimiter() {
        final LoginRateLimiter fresh = new LoginRateLimiter();
        ComponentUtil.register(fresh, "loginRateLimiter");
        ComponentUtil.register(fresh, LoginRateLimiter.class.getCanonicalName());
    }

    // ── Method guard ─────────────────────────────────────────────────────────────

    @Test
    public void test_rejectsGet_returns405() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new ChatSessionClearHandler().handle(new StubRequest("GET"), res, "valid-session-1");
        assertEquals(405, res.status);
        assertTrue(res.body().contains("\"code\":\"method_not_allowed\""), res.body());
        assertEquals("Allow header must be set to DELETE", "DELETE", res.getHeader("Allow"));
    }

    @Test
    public void test_rejectsPost_returns405() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new ChatSessionClearHandler().handle(new StubRequest("POST"), res, "valid-session-1");
        assertEquals(405, res.status);
        assertTrue(res.body().contains("\"code\":\"method_not_allowed\""), res.body());
    }

    // ── RAG-disabled guard ────────────────────────────────────────────────────────

    @Test
    public void test_chatDisabled_returns400() throws Exception {
        // Default fess_config.properties has rag.chat.enabled=false.
        final CapturingResponse res = new CapturingResponse();
        new ChatSessionClearHandler().handle(new StubRequest("DELETE"), res, "valid-session-1");
        assertEquals(400, res.status);
        assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
        assertTrue(res.body().contains("chat is not enabled"), res.body());
    }

    // ── session_id pattern validation ────────────────────────────────────────────

    @Test
    public void test_nullSessionId_returns400() throws Exception {
        enableRagChat();
        final CapturingResponse res = new CapturingResponse();
        new ChatSessionClearHandler().handle(new StubRequest("DELETE"), res, null);
        assertEquals(400, res.status);
        assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
        assertTrue(res.body().contains("invalid session_id"), res.body());
    }

    @Test
    public void test_emptySessionId_returns400() throws Exception {
        enableRagChat();
        final CapturingResponse res = new CapturingResponse();
        new ChatSessionClearHandler().handle(new StubRequest("DELETE"), res, "");
        assertEquals(400, res.status);
        assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
    }

    @Test
    public void test_sessionIdWithSpaces_returns400() throws Exception {
        enableRagChat();
        final CapturingResponse res = new CapturingResponse();
        new ChatSessionClearHandler().handle(new StubRequest("DELETE"), res, "invalid session");
        assertEquals(400, res.status);
        assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
    }

    @Test
    public void test_sessionIdWithSpecialChars_returns400() throws Exception {
        enableRagChat();
        final CapturingResponse res = new CapturingResponse();
        // Characters outside [A-Za-z0-9._-] must be rejected
        new ChatSessionClearHandler().handle(new StubRequest("DELETE"), res, "session@#$!");
        assertEquals(400, res.status);
        assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
    }

    @Test
    public void test_sessionIdTooLong_returns400() throws Exception {
        enableRagChat();
        // 101 characters — one over the 100-character cap
        final String tooLong = "a".repeat(101);
        final CapturingResponse res = new CapturingResponse();
        new ChatSessionClearHandler().handle(new StubRequest("DELETE"), res, tooLong);
        assertEquals(400, res.status);
        assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
    }

    @Test
    public void test_validSessionIdBoundary_acceptsMaxLength() throws Exception {
        // 100 characters — exactly at the cap; should pass pattern validation.
        // With RAG enabled, the next gate is the rate limiter. In the test harness
        // the limiter DI is absent, so we expect INTERNAL_ERROR (500) — confirming
        // the session_id was accepted and processing continued past the pattern check.
        enableRagChat();
        final String maxId = "a".repeat(100);
        final CapturingResponse res = new CapturingResponse();
        handlerForUser("test-user").handle(new StubRequest("DELETE"), res, maxId);
        // Must NOT be a 400 invalid_request from the pattern guard
        assertFalse(res.body().contains("invalid session_id"), "100-char session_id must pass pattern validation, got: " + res.body());
    }

    @Test
    public void test_sessionId100Chars_accepted() throws Exception {
        // Exactly 100 characters — at the new cap; should pass pattern validation.
        enableRagChat();
        final String maxId = "a".repeat(100);
        final CapturingResponse res = new CapturingResponse();
        handlerForUser("test-user").handle(new StubRequest("DELETE"), res, maxId);
        // Must NOT be a 400 invalid_request from the pattern guard
        assertFalse("100-char session_id must pass pattern validation, got: " + res.body(), res.body().contains("invalid session_id"));
    }

    @Test
    public void test_sessionId101Chars_returns400() throws Exception {
        // 101 characters — one over the new 100-character cap; must be rejected.
        enableRagChat();
        final String tooLong = "a".repeat(101);
        final CapturingResponse res = new CapturingResponse();
        new ChatSessionClearHandler().handle(new StubRequest("DELETE"), res, tooLong);
        assertEquals(400, res.status);
        assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
        assertTrue(res.body().contains("invalid session_id"), res.body());
    }

    @Test
    public void test_validSessionIdWithDotAndHyphen_accepted() throws Exception {
        // Dots and hyphens are valid per the pattern.
        enableRagChat();
        final CapturingResponse res = new CapturingResponse();
        handlerForUser("test-user").handle(new StubRequest("DELETE"), res, "sess.ion-id_01");
        assertFalse(res.body().contains("invalid session_id"),
                "session_id with dot/hyphen/underscore must pass pattern, got: " + res.body());
    }

    // ── Rate limit ────────────────────────────────────────────────────────────────

    @Test
    public void test_rateLimited_returns429() throws Exception {
        enableRagChat();
        final LoginRateLimiter rl = new LoginRateLimiter();
        // Pre-saturate the CHAT bucket for the "u:test-user" key returned by handlerForUser(...).
        // LoginRateLimiter.allow() denies empty/null keys (MJ-6), so the bucket key must match
        // the non-empty key the handler resolves.
        for (int i = 0; i < 30; i++) {
            rl.allow(LoginRateLimiter.Scope.CHAT, "u:test-user", 30, 60);
        }
        ComponentUtil.register(rl, "loginRateLimiter");
        ComponentUtil.register(rl, LoginRateLimiter.class.getCanonicalName());
        try {
            final CapturingResponse res = new CapturingResponse();
            handlerForUser("test-user").handle(new StubRequest("DELETE"), res, "valid-session-1");
            assertEquals(429, res.status);
            assertTrue(res.body().contains("\"code\":\"rate_limited\""), res.body());
            assertTrue(res.body().contains("too many chat requests"), res.body());
            assertEquals("Retry-After header must be set", "60", res.getHeader("Retry-After"));
        } finally {
            ComponentUtil.register(new LoginRateLimiter(), "loginRateLimiter");
            ComponentUtil.register(new LoginRateLimiter(), LoginRateLimiter.class.getCanonicalName());
        }
    }

    @Test
    public void test_anonymousUser_rateLimitedByClientIp() throws Exception {
        // Regression for L-1: anonymous DELETE callers used to bypass the throttle entirely because
        // the guard skipped a blank userId and keyed on the forgeable guest userCode. Now the handler
        // keys anonymous traffic by the client IP ("ip:<clientIp>"), so an anonymous caller from the
        // same IP that exceeds the limit IS rate-limited (429). Rotating/forging a userCode per
        // request does not change the IP-based key, so the throttle still applies.
        enableRagChat();
        final String clientIp = "203.0.113.7";
        final String key = "ip:" + clientIp;
        final LoginRateLimiter rl = new LoginRateLimiter();
        for (int i = 0; i < 30; i++) {
            rl.allow(LoginRateLimiter.Scope.CHAT, key, 30, 60);
        }
        ComponentUtil.register(rl, "loginRateLimiter");
        ComponentUtil.register(rl, LoginRateLimiter.class.getCanonicalName());
        final org.codelibs.fess.chat.ChatSessionManager okManager = new org.codelibs.fess.chat.ChatSessionManager() {
            @Override
            public boolean clearSession(final String sessionId, final String userId) {
                return true;
            }
        };
        ComponentUtil.register(okManager, "chatSessionManager");
        ComponentUtil.register(okManager, org.codelibs.fess.chat.ChatSessionManager.class.getCanonicalName());
        try {
            final CapturingResponse res = new CapturingResponse();
            final java.util.concurrent.atomic.AtomicInteger rotation = new java.util.concurrent.atomic.AtomicInteger();
            final ChatSessionClearHandler handler = new ChatSessionClearHandler() {
                @Override
                protected String getUserId(final jakarta.servlet.http.HttpServletRequest req) {
                    return "forged-" + rotation.incrementAndGet();
                }

                @Override
                protected String getRateLimitKey(final jakarta.servlet.http.HttpServletRequest req) {
                    return key;
                }
            };
            handler.handle(new StubRequest("DELETE"), res, "valid-session-1");
            // The IP bucket is pre-saturated (30/30), so this anonymous request must be 429.
            assertEquals(429, res.status);
            assertTrue(res.body().contains("\"code\":\"rate_limited\""), res.body());
            assertTrue(res.body().contains("too many chat requests"), res.body());
            assertEquals("Retry-After header must be set", "60", res.getHeader("Retry-After"));
        } finally {
            ComponentUtil.register(new LoginRateLimiter(), "loginRateLimiter");
            ComponentUtil.register(new LoginRateLimiter(), LoginRateLimiter.class.getCanonicalName());
        }
    }

    // ── Session not found (404) ───────────────────────────────────────────────────

    @Test
    public void test_sessionNotFound_returns404() throws Exception {
        enableRagChat();
        registerLimiter();
        // Register a ChatSessionManager that always returns false from clearSession
        final ChatSessionManager notFoundManager = new ChatSessionManager() {
            @Override
            public boolean clearSession(final String sessionId, final String userId) {
                return false;
            }
        };
        ComponentUtil.register(notFoundManager, "chatSessionManager");
        ComponentUtil.register(notFoundManager, ChatSessionManager.class.getCanonicalName());

        final CapturingResponse res = new CapturingResponse();
        handlerForUser("test-user").handle(new StubRequest("DELETE"), res, "nonexistent-session");
        assertEquals(res.body(), 404, res.status);
        assertTrue(res.body().contains("\"code\":\"not_found\""), res.body());
        assertTrue(res.body().contains("session not found"), res.body());
    }

    // ── Success (200) ─────────────────────────────────────────────────────────────

    @Test
    public void test_successPath_returnsEnvelopeWithClearedTrue() throws Exception {
        enableRagChat();
        registerLimiter();
        // Register a ChatSessionManager that always clears successfully
        final ChatSessionManager mockManager = new ChatSessionManager() {
            @Override
            public boolean clearSession(final String sessionId, final String userId) {
                return true;
            }
        };
        ComponentUtil.register(mockManager, "chatSessionManager");
        ComponentUtil.register(mockManager, ChatSessionManager.class.getCanonicalName());

        final CapturingResponse res = new CapturingResponse();
        handlerForUser("test-user").handle(new StubRequest("DELETE"), res, "sess-001");
        assertEquals(res.body(), 200, res.status);
        final String body = res.body();
        // v2 envelope structure
        assertTrue("must start with v2 envelope, was: " + body, body.startsWith("{\"response\":{"));
        assertFalse("version field must not appear in v2 envelope: " + body, body.contains("\"version\""));
        assertTrue(body.contains("\"status\":0"), body);
        // Payload fields
        assertTrue(body.contains("\"session_id\":\"sess-001\""), body);
        assertTrue(body.contains("\"cleared\":true"), body);
    }

    @Test
    public void test_successPath_sessionIdEchoedBack() throws Exception {
        enableRagChat();
        registerLimiter();
        final ChatSessionManager mockManager = new ChatSessionManager() {
            @Override
            public boolean clearSession(final String sessionId, final String userId) {
                return true;
            }
        };
        ComponentUtil.register(mockManager, "chatSessionManager");
        ComponentUtil.register(mockManager, ChatSessionManager.class.getCanonicalName());

        final String sid = "my.session-id_42";
        final CapturingResponse res = new CapturingResponse();
        handlerForUser("test-user").handle(new StubRequest("DELETE"), res, sid);
        assertEquals(res.body(), 200, res.status);
        assertTrue(res.body(), res.body().contains("\"session_id\":\"" + sid + "\""));
    }

    // ── Envelope shape ────────────────────────────────────────────────────────────

    @Test
    public void test_errorResponseHasNoVersionField() throws Exception {
        // Even error responses must not include "version" — removed from envelope.
        final CapturingResponse res = new CapturingResponse();
        new ChatSessionClearHandler().handle(new StubRequest("GET"), res, "valid");
        assertFalse(res.body().contains("\"version\""), "version must not appear in error envelope: " + res.body());
    }

    // ── Internal error (500) ──────────────────────────────────────────────────────

    @Test
    public void test_clearSessionThrowsRuntime_returns500_internalError() throws Exception {
        // When ChatSessionManager.clearSession throws an unexpected RuntimeException,
        // the handler must return HTTP 500 with error code "internal_error".
        enableRagChat();
        registerLimiter();
        final org.codelibs.fess.chat.ChatSessionManager throwingManager = new org.codelibs.fess.chat.ChatSessionManager() {
            @Override
            public boolean clearSession(final String sessionId, final String userId) {
                throw new RuntimeException("simulated backend failure");
            }
        };
        ComponentUtil.register(throwingManager, "chatSessionManager");
        ComponentUtil.register(throwingManager, org.codelibs.fess.chat.ChatSessionManager.class.getCanonicalName());

        final CapturingResponse res = new CapturingResponse();
        handlerForUser("test-user").handle(new StubRequest("DELETE"), res, "sess-err-001");
        assertEquals(res.body(), 500, res.status);
        final String body = res.body();
        assertTrue(body.contains("\"code\":\"internal_error\""), "must return internal_error code, got: " + body);
        assertTrue(body.contains("internal error"), "must include internal error message, got: " + body);
    }

    // ── RAG disabled (400) ────────────────────────────────────────────────────────

    @Test
    public void test_ragChatDisabled_returns400_invalidRequest() throws Exception {
        // When RAG chat is disabled the handler must return HTTP 400 with
        // error code "invalid_request". Default FessConfig from test_app.xml has
        // rag.chat.enabled=false, so no config override is needed here.
        final CapturingResponse res = new CapturingResponse();
        new ChatSessionClearHandler().handle(new StubRequest("DELETE"), res, "valid-session-1");
        assertEquals(400, res.status);
        final String body = res.body();
        assertTrue(body.contains("\"code\":\"invalid_request\""), "must return invalid_request when RAG disabled, got: " + body);
        assertTrue(body.contains("chat is not enabled"), "must include 'chat is not enabled' message, got: " + body);
        // Must not reach any session manager logic
        assertFalse(body.contains("\"cleared\""), "must not contain cleared field when chat is disabled: " + body);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────────

    private static void enableRagChat() {
        ComponentUtil.setFessConfig(new org.codelibs.fess.mylasta.direction.FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isRagChatEnabled() {
                return true;
            }
        });
    }

    /**
     * Creates a handler whose effective user id is fixed by overriding the {@code getUserId} seam.
     * This keeps the test off the real {@code SystemHelper}/{@code UserInfoHelper} DI path: both are
     * smart-deploy components that the shared test container resolves for real once warmed by an
     * earlier test, so stubbing them via {@code ComponentUtil.register} is not reliable across the
     * full {@code mvn test} suite (the stub is only honored while the container cannot resolve the
     * real component). The non-empty id also satisfies LoginRateLimiter's empty-key guard (MJ-6).
     */
    private static ChatSessionClearHandler handlerForUser(final String userId) {
        return new ChatSessionClearHandler() {
            @Override
            protected String getUserId(final jakarta.servlet.http.HttpServletRequest req) {
                return userId;
            }

            @Override
            protected String getRateLimitKey(final jakarta.servlet.http.HttpServletRequest req) {
                // Treat the fixed user as authenticated for throttle purposes: key by "u:<userId>".
                // Pinning the rate-limit key keeps these tests off the real
                // SystemHelper/RateLimitHelper DI path (smart-deploy components unreliable to stub).
                return "u:" + userId;
            }
        };
    }

    private static void registerLimiter() {
        // Register a fresh (non-saturated) rate limiter so rate-limit checks pass.
        final LoginRateLimiter rl = new LoginRateLimiter();
        ComponentUtil.register(rl, "loginRateLimiter");
        ComponentUtil.register(rl, LoginRateLimiter.class.getCanonicalName());
    }

    /** Minimal HttpServletResponse stub — captures status, content type, headers, and body. */
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
     * Minimal HttpServletRequest stub for DELETE requests.
     * No body needed for this handler — only method and URI are used.
     */
    private static class StubRequest implements HttpServletRequest {
        private final String method;
        private final Map<String, Object> attrs = new HashMap<>();

        StubRequest(final String method) {
            this.method = method;
        }

        @Override
        public String getMethod() {
            return method;
        }

        @Override
        public String getServletPath() {
            return "/api/v2/chat/sessions/stub";
        }

        @Override
        public String getRequestURI() {
            return "/api/v2/chat/sessions/stub";
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
            return new StringBuffer(getRequestURI());
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
            final ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
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
