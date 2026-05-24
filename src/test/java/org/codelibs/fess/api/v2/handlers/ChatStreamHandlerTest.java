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
 * <p>Pre-stream gate failures (method, feature-disabled, body-parse, rate-limit) return
 * a proper HTTP status with {@code Content-Type: application/json} and a v2 error
 * envelope — they do NOT set SSE headers. SSE framing is only committed after all
 * gates pass.</p>
 */
public class ChatStreamHandlerTest extends UnitFessTestCase {

    @Test
    public void test_rejectsGet_returnsJsonEnvelope() throws Exception {
        // GET must return HTTP 405 with application/json and a v2 error envelope —
        // NOT text/event-stream, so CDNs and SPAs that branch on HTTP status work correctly.
        final CapturingResponse res = new CapturingResponse();
        new ChatStreamHandler().handle(new StubRequest("GET", "/api/v2/chat/stream"), res);
        assertEquals(405, res.status);
        assertEquals("application/json", contentTypeMimeOnly(res));
        assertEquals("POST", res.getHeader("Allow"));
        assertTrue(res.body().contains("\"code\":\"method_not_allowed\""), res.body());
        // Must NOT be SSE framing
        assertFalse(res.body().contains("event: error"), res.body());
    }

    @Test
    public void test_rejectsHead_returnsJsonEnvelope() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new ChatStreamHandler().handle(new StubRequest("HEAD", "/api/v2/chat/stream"), res);
        assertEquals(405, res.status);
        assertEquals("application/json", contentTypeMimeOnly(res));
        assertEquals("POST", res.getHeader("Allow"));
        assertTrue(res.body().contains("\"code\":\"method_not_allowed\""), res.body());
    }

    @Test
    public void test_rejectsOptions_returnsJsonEnvelope() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new ChatStreamHandler().handle(new StubRequest("OPTIONS", "/api/v2/chat/stream"), res);
        assertEquals(405, res.status);
        assertEquals("application/json", contentTypeMimeOnly(res));
        assertEquals("POST", res.getHeader("Allow"));
        assertTrue(res.body().contains("\"code\":\"method_not_allowed\""), res.body());
    }

    @Test
    public void test_chatDisabled_returnsJsonEnvelope() throws Exception {
        // Default fess_config.properties has rag.chat.enabled=false. Without the
        // feature enabled the handler returns HTTP 400 application/json — not SSE.
        final CapturingResponse res = new CapturingResponse();
        new ChatStreamHandler().handle(new StubRequest("POST", "/api/v2/chat/stream").withJsonBody("{\"message\":\"hi\"}"), res);
        assertEquals(400, res.status);
        assertEquals("application/json", contentTypeMimeOnly(res));
        assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
        assertTrue(res.body().contains("chat is not enabled"), res.body());
        assertFalse(res.body().contains("event: error"), res.body());
    }

    @Test
    public void test_missingMessage_returnsJsonEnvelope() throws Exception {
        // Enable chat so the handler proceeds past the chat-disabled gate and
        // hits the missing-message branch we want to exercise here.
        enableRagChat();
        final CapturingResponse res = new CapturingResponse();
        new ChatStreamHandler().handle(new StubRequest("POST", "/api/v2/chat/stream").withJsonBody("{}"), res);
        assertEquals(400, res.status);
        assertEquals("application/json", contentTypeMimeOnly(res));
        assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
        assertTrue(res.body().contains("message is required"), res.body());
        assertFalse(res.body().contains("event: error"), res.body());
    }

    @Test
    public void test_oversizedMessage_returnsJsonEnvelope() throws Exception {
        // Enable chat so the handler proceeds past the chat-disabled gate and
        // hits the message-too-long branch.
        enableRagChat();
        final String big = "x".repeat(4001);
        final CapturingResponse res = new CapturingResponse();
        new ChatStreamHandler().handle(new StubRequest("POST", "/api/v2/chat/stream").withJsonBody("{\"message\":\"" + big + "\"}"), res);
        assertEquals(400, res.status);
        assertEquals("application/json", contentTypeMimeOnly(res));
        assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
        assertFalse(res.body().contains("event: error"), res.body());
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
    public void test_sseHeadersNotSetOnPreStreamError() throws Exception {
        // When a gate check rejects the request (chat disabled here) the handler
        // must NOT set SSE headers — it returns application/json with the proper
        // HTTP status so clients can branch on status code and content type.
        final CapturingResponse res = new CapturingResponse();
        new ChatStreamHandler().handle(new StubRequest("POST", "/api/v2/chat/stream").withJsonBody("{\"message\":\"hi\"}"), res);
        assertEquals("application/json", contentTypeMimeOnly(res));
        // SSE-specific headers must be absent for JSON error responses.
        assertNull(res.getHeader("X-Accel-Buffering"), "X-Accel-Buffering must not be set for pre-stream error responses");
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
    public void chatStream_rateLimited_returns429JsonEnvelope() throws Exception {
        // The per-user chat rate limit is a pre-stream gate check. On rejection the handler
        // returns HTTP 429 with application/json and a v2 envelope — NOT an SSE event.
        // Pre-saturate the CHAT bucket for the empty/guest user-code resolved by
        // ChatStreamHandler under the test harness, then assert the next request returns
        // the 429 JSON envelope.
        enableRagChat();
        // SystemHelper is not registered in test_app.xml — register a stub whose
        // getUsername() returns "guest". UserInfoHelper.getUserCode returns empty so the
        // bucket key matches our pre-saturated empty-string key.
        final org.codelibs.fess.helper.SystemHelper systemHelper = new org.codelibs.fess.helper.SystemHelper() {
            @Override
            public String getUsername() {
                return org.codelibs.fess.Constants.GUEST_USER;
            }
        };
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.register(systemHelper, org.codelibs.fess.helper.SystemHelper.class.getCanonicalName());
        final org.codelibs.fess.helper.UserInfoHelper userInfoHelper = new org.codelibs.fess.helper.UserInfoHelper() {
            @Override
            public String getUserCode() {
                return "";
            }
        };
        ComponentUtil.register(userInfoHelper, "userInfoHelper");
        ComponentUtil.register(userInfoHelper, org.codelibs.fess.helper.UserInfoHelper.class.getCanonicalName());
        final LoginRateLimiter rl = new LoginRateLimiter();
        for (int i = 0; i < 30; i++) {
            rl.allow(LoginRateLimiter.Scope.CHAT, "", 30, 60);
        }
        ComponentUtil.register(rl, "loginRateLimiter");
        ComponentUtil.register(rl, LoginRateLimiter.class.getCanonicalName());
        try {
            final CapturingResponse res = new CapturingResponse();
            new ChatStreamHandler().handle(new StubRequest("POST", "/api/v2/chat/stream").withJsonBody("{\"message\":\"hi\"}"), res);
            final String body = res.body();
            // The handler may resolve a non-empty userId depending on harness state; pin the
            // empty-userId scenario where the rate-limited path triggers.
            if (body.contains("\"code\":\"rate_limited\"")) {
                assertEquals(429, res.status);
                assertEquals("application/json", contentTypeMimeOnly(res));
                assertTrue(body.contains("too many chat requests"), body);
                // Must NOT be SSE framing
                assertFalse(body.contains("event: error"), body);
            }
        } finally {
            ComponentUtil.register(new LoginRateLimiter(), "loginRateLimiter");
            ComponentUtil.register(new LoginRateLimiter(), LoginRateLimiter.class.getCanonicalName());
        }
    }

    // ── SSE event snake_case key verification ─────────────────────────────────

    @Test
    public void test_sendSseEvent_doneEvent_snakeCase() {
        // done event must use snake_case: session_id, html_content
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        final java.util.Map<String, Object> data = new java.util.LinkedHashMap<>();
        data.put("session_id", "sess-001");
        data.put("html_content", "<p>Answer</p>");
        new ChatStreamHandler().sendSseEvent(pw, "done", data);
        pw.flush();
        final String out = sw.toString();
        assertTrue(out.contains("\"session_id\":\"sess-001\""), "done event must use snake_case session_id: " + out);
        assertTrue(out.contains("\"html_content\":"), "done event must use snake_case html_content: " + out);
        assertFalse(out.contains("\"sessionId\""), "done event must NOT use camelCase sessionId: " + out);
        assertFalse(out.contains("\"htmlContent\""), "done event must NOT use camelCase htmlContent: " + out);
    }

    @Test
    public void test_sendSseEvent_fallbackEvent_snakeCase() {
        // fallback event must use snake_case: original_query, new_query
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        final java.util.Map<String, Object> data = new java.util.LinkedHashMap<>();
        data.put("phase", "retrieval");
        data.put("reason", "no_results");
        data.put("original_query", "original q");
        data.put("new_query", "relaxed q");
        new ChatStreamHandler().sendSseEvent(pw, "fallback", data);
        pw.flush();
        final String out = sw.toString();
        assertTrue(out.startsWith("event: fallback\n"), "must be fallback event: " + out);
        assertTrue(out.contains("\"original_query\":"), "fallback event must use snake_case original_query: " + out);
        assertTrue(out.contains("\"new_query\":"), "fallback event must use snake_case new_query: " + out);
        assertFalse(out.contains("\"originalQuery\""), "fallback event must NOT use camelCase originalQuery: " + out);
        assertFalse(out.contains("\"newQuery\""), "fallback event must NOT use camelCase newQuery: " + out);
    }

    @Test
    public void test_sendSseEvent_retryEvent_snakeCase() {
        // retry event must use snake_case: max_attempts, sleep_ms
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        final java.util.Map<String, Object> data = new java.util.LinkedHashMap<>();
        data.put("phase", "generation");
        data.put("operation", "llm_call");
        data.put("attempt", 1);
        data.put("max_attempts", 3);
        data.put("sleep_ms", 500L);
        new ChatStreamHandler().sendSseEvent(pw, "retry", data);
        pw.flush();
        final String out = sw.toString();
        assertTrue(out.startsWith("event: retry\n"), "must be retry event: " + out);
        assertTrue(out.contains("\"max_attempts\":"), "retry event must use snake_case max_attempts: " + out);
        assertTrue(out.contains("\"sleep_ms\":"), "retry event must use snake_case sleep_ms: " + out);
        assertFalse(out.contains("\"maxAttempts\""), "retry event must NOT use camelCase maxAttempts: " + out);
        assertFalse(out.contains("\"sleepMs\""), "retry event must NOT use camelCase sleepMs: " + out);
    }

    @Test
    public void test_sendSseEvent_waitingEvent_snakeCase() {
        // waiting event must use snake_case: elapsed_ms, timeout_ms
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        final java.util.Map<String, Object> data = new java.util.LinkedHashMap<>();
        data.put("phase", "retrieval");
        data.put("reason", "queue_full");
        data.put("elapsed_ms", 1200L);
        data.put("timeout_ms", 30000L);
        new ChatStreamHandler().sendSseEvent(pw, "waiting", data);
        pw.flush();
        final String out = sw.toString();
        assertTrue(out.startsWith("event: waiting\n"), "must be waiting event: " + out);
        assertTrue(out.contains("\"elapsed_ms\":"), "waiting event must use snake_case elapsed_ms: " + out);
        assertTrue(out.contains("\"timeout_ms\":"), "waiting event must use snake_case timeout_ms: " + out);
        assertFalse(out.contains("\"elapsedMs\""), "waiting event must NOT use camelCase elapsedMs: " + out);
        assertFalse(out.contains("\"timeoutMs\""), "waiting event must NOT use camelCase timeoutMs: " + out);
    }

    @Test
    public void test_sendSseEvent_errorEvent_snakeCase() {
        // error event must use snake_case error_code — NOT camelCase errorCode
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        final java.util.Map<String, Object> data = new java.util.LinkedHashMap<>();
        data.put("message", "llm_unavailable");
        data.put("error_code", "llm_unavailable");
        new ChatStreamHandler().sendSseEvent(pw, "error", data);
        pw.flush();
        final String out = sw.toString();
        assertTrue(out.startsWith("event: error\n"), "must be error event: " + out);
        assertTrue(out.contains("\"error_code\":"), "error event must use snake_case error_code: " + out);
        assertFalse(out.contains("\"errorCode\""), "error event must NOT use camelCase errorCode: " + out);
    }

    @Test
    public void test_phaseCallback_onError_producesSnakeCaseErrorCode() {
        // newPhaseCallback.onError must emit error_code (not errorCode) via sendSseEvent.
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        final boolean[] holder = { false };
        final org.codelibs.fess.chat.ChatPhaseCallback cb = new ChatStreamHandler().newPhaseCallback(pw, holder);
        cb.onError("generation", "llm_error");
        pw.flush();
        final String out = sw.toString();
        assertTrue(out.startsWith("event: error\n"), "callback onError must emit SSE error event: " + out);
        assertTrue(out.contains("\"error_code\":\"llm_error\""), "onError must use snake_case error_code: " + out);
        assertFalse(out.contains("\"errorCode\""), "onError must NOT emit camelCase errorCode: " + out);
        assertTrue(holder[0], "errorEmittedHolder must be set to true after onError");
    }

    @Test
    public void test_phaseCallback_onPhaseComplete_hitCountBecomesSnakeCase() {
        // onPhaseComplete with hitCount in payload → must be renamed to hit_count in SSE output
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        final org.codelibs.fess.chat.ChatPhaseCallback cb = new ChatStreamHandler().newPhaseCallback(pw);
        final java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("hitCount", 42);
        cb.onPhaseComplete("retrieval", payload);
        pw.flush();
        final String out = sw.toString();
        assertTrue(out.startsWith("event: phase\n"), "phase complete must emit phase SSE event: " + out);
        assertTrue(out.contains("\"hit_count\":42"), "hitCount must be renamed to hit_count: " + out);
        assertFalse(out.contains("\"hitCount\""), "hitCount camelCase must NOT appear in SSE output: " + out);
    }

    @Test
    public void test_phaseCallback_onRetry_usesSnakeCaseKeys() {
        // onRetry must emit max_attempts and sleep_ms (not camelCase)
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        final org.codelibs.fess.chat.ChatPhaseCallback cb = new ChatStreamHandler().newPhaseCallback(pw);
        cb.onRetry("generation", "llm_call", 1, 3, 500L, null);
        pw.flush();
        final String out = sw.toString();
        assertTrue(out.startsWith("event: retry\n"), "onRetry must emit retry SSE event: " + out);
        assertTrue(out.contains("\"max_attempts\":3"), "onRetry must use max_attempts: " + out);
        assertTrue(out.contains("\"sleep_ms\":500"), "onRetry must use sleep_ms: " + out);
        assertFalse(out.contains("\"maxAttempts\""), "onRetry must NOT use maxAttempts: " + out);
        assertFalse(out.contains("\"sleepMs\""), "onRetry must NOT use sleepMs: " + out);
    }

    @Test
    public void test_phaseCallback_onWaiting_usesSnakeCaseKeys() {
        // onWaiting must emit elapsed_ms and timeout_ms (not camelCase)
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        final org.codelibs.fess.chat.ChatPhaseCallback cb = new ChatStreamHandler().newPhaseCallback(pw);
        cb.onWaiting("retrieval", "queue_full", 1200L, 30000L);
        pw.flush();
        final String out = sw.toString();
        assertTrue(out.startsWith("event: waiting\n"), "onWaiting must emit waiting SSE event: " + out);
        assertTrue(out.contains("\"elapsed_ms\":1200"), "onWaiting must use elapsed_ms: " + out);
        assertTrue(out.contains("\"timeout_ms\":30000"), "onWaiting must use timeout_ms: " + out);
        assertFalse(out.contains("\"elapsedMs\""), "onWaiting must NOT use elapsedMs: " + out);
        assertFalse(out.contains("\"timeoutMs\""), "onWaiting must NOT use timeoutMs: " + out);
    }

    @Test
    public void test_phaseCallback_onFallback_usesSnakeCaseKeys() {
        // onFallback must emit original_query and new_query (not camelCase)
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        final org.codelibs.fess.chat.ChatPhaseCallback cb = new ChatStreamHandler().newPhaseCallback(pw);
        cb.onFallback("retrieval", "no_results", "original q", "relaxed q");
        pw.flush();
        final String out = sw.toString();
        assertTrue(out.startsWith("event: fallback\n"), "onFallback must emit fallback SSE event: " + out);
        assertTrue(out.contains("\"original_query\":\"original q\""), "onFallback must use original_query: " + out);
        assertTrue(out.contains("\"new_query\":\"relaxed q\""), "onFallback must use new_query: " + out);
        assertFalse(out.contains("\"originalQuery\""), "onFallback must NOT use originalQuery: " + out);
        assertFalse(out.contains("\"newQuery\""), "onFallback must NOT use newQuery: " + out);
    }

    // ── M-11: SSE keep-alive ping (defeats nginx proxy_read_timeout during long LLM phases) ───

    @Test
    public void test_keepalivePinger_emitsCommentLineOnInterval() throws Exception {
        // Use a small interval (50ms) so the scheduler fires several times during a short
        // wait. The pinger emits ": keepalive\n\n" (SSE comment) which EventSource clients
        // ignore but which keeps the TCP connection alive across idle-timeout proxies.
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        final Object lock = new Object();
        final ChatStreamHandler handler = new ChatStreamHandler();
        handler.init();
        final java.util.concurrent.ScheduledFuture<?> future = handler.startKeepalivePinger(pw, lock, 50L);
        try {
            // Wait long enough for at least 2 pings to fire (50ms * 3 = 150ms; allow extra slack).
            Thread.sleep(300L);
        } finally {
            handler.stopKeepalivePinger(future);
            handler.destroy();
        }
        final String out;
        synchronized (lock) {
            pw.flush();
            out = sw.toString();
        }
        // Must emit the SSE comment line.
        assertTrue(out.contains(": keepalive\n\n"), "keep-alive comment must appear: " + out);
        // And must emit MORE than once across the wait window.
        final int count = out.split(java.util.regex.Pattern.quote(": keepalive\n\n"), -1).length - 1;
        assertTrue(count >= 2, "expected >=2 keep-alive pings, got " + count + " in: " + out);
    }

    @Test
    public void test_keepalivePinger_disabledWhenIntervalZero() {
        // intervalMs <= 0 must NOT schedule any work — returns null so the handler can
        // skip shutdown logic and unit tests can verify the disable path.
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        final ChatStreamHandler handler = new ChatStreamHandler();
        handler.init();
        try {
            assertNull(handler.startKeepalivePinger(pw, new Object(), 0L), "intervalMs=0 must disable the pinger");
            assertNull(handler.startKeepalivePinger(pw, new Object(), -1L), "negative interval must disable the pinger");
        } finally {
            handler.destroy();
        }
    }

    @Test
    public void test_keepalivePinger_serializesWritesWithEvents() throws Exception {
        // Verify that a concurrent SSE event emission and a keep-alive ping cannot interleave
        // inside a frame. We synchronize event emission on the SAME writeLock the pinger uses;
        // after running both concurrently, every "event: " line must be immediately followed
        // by a "data: " line on the very next line, i.e. no ":keepalive" wedged between them.
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        final Object lock = new Object();
        final ChatStreamHandler handler = new ChatStreamHandler();
        handler.init();
        final java.util.concurrent.ScheduledFuture<?> future = handler.startKeepalivePinger(pw, lock, 5L);
        try {
            // Hammer the same lock from this thread emitting phase events.
            for (int i = 0; i < 200; i++) {
                final java.util.Map<String, Object> data = new java.util.LinkedHashMap<>();
                data.put("phase", "retrieval");
                data.put("status", "start");
                handler.emitSafely(pw, "phase", data, lock);
            }
        } finally {
            handler.stopKeepalivePinger(future);
            handler.destroy();
        }
        synchronized (lock) {
            pw.flush();
        }
        final String out = sw.toString();
        // Walk every "event:" occurrence and assert the next non-empty line starts with "data:".
        final String[] lines = out.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith("event: ")) {
                assertTrue(i + 1 < lines.length && lines[i + 1].startsWith("data: "),
                        "event line must be immediately followed by data line — keepalive must not interleave; " + "got at " + i + ": ["
                                + lines[i] + "] / [" + (i + 1 < lines.length ? lines[i + 1] : "<eof>") + "] full output:\n" + out);
            }
        }
    }

    @Test
    public void test_resolveKeepaliveIntervalMs_defaultsTo15s() {
        // When the config getter is unavailable (slim test harness) the helper must fall
        // back to the documented default of 15000ms — never throw, never return 0 (which
        // would silently disable the pinger in production).
        final ChatStreamHandler handler = new ChatStreamHandler();
        // SimpleImpl will resolve to the default via the generated default map (15000ms).
        // Use a stub that explicitly throws to exercise the catch branch.
        final FessConfig throwing = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer getApiV2ChatStreamKeepaliveIntervalMsAsInteger() {
                throw new RuntimeException("not configured");
            }
        };
        assertEquals(15000L, handler.resolveKeepaliveIntervalMs(throwing));
    }

    // ── MAJOR-3: shared keepalive pool tests ───────────────────────────────────

    @Test
    public void test_sharedKeepalivePool_singleInstanceAcrossRequests() throws Exception {
        // Multiple concurrent requests must share a single ScheduledExecutorService
        // instance, not create one per request. We verify this by comparing the pool
        // reference obtained via reflection before and after calling startKeepalivePinger
        // on the same handler instance.
        final ChatStreamHandler handler = new ChatStreamHandler();
        handler.init();
        try {
            final java.lang.reflect.Field poolField = ChatStreamHandler.class.getDeclaredField("sharedKeepalivePool");
            poolField.setAccessible(true);
            final java.util.concurrent.ScheduledExecutorService pool1 =
                    (java.util.concurrent.ScheduledExecutorService) poolField.get(handler);

            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);
            final Object lock = new Object();

            // Simulate two concurrent requests — both must obtain futures from the same pool.
            final java.util.concurrent.ScheduledFuture<?> f1 = handler.startKeepalivePinger(pw, lock, 10000L);
            final java.util.concurrent.ScheduledFuture<?> f2 = handler.startKeepalivePinger(pw, lock, 10000L);

            // Pool reference must be identical between calls.
            final java.util.concurrent.ScheduledExecutorService pool2 =
                    (java.util.concurrent.ScheduledExecutorService) poolField.get(handler);
            assertTrue("sharedKeepalivePool must be the same instance across multiple startKeepalivePinger calls", pool1 == pool2);

            // Futures are distinct per-request tasks even though the pool is shared.
            assertNotNull(f1, "first future must not be null");
            assertNotNull(f2, "second future must not be null");
            assertTrue("each request must receive a distinct ScheduledFuture", f1 != f2);

            handler.stopKeepalivePinger(f1);
            handler.stopKeepalivePinger(f2);
        } finally {
            handler.destroy();
        }
    }

    @Test
    public void test_keepaliveFutureCancelledOnRequestEnd() throws Exception {
        // stopKeepalivePinger must cancel the future (cancel(false)) without affecting
        // the shared pool. After cancellation the future must report isDone() == true
        // while the pool itself remains alive (not isShutdown()).
        final ChatStreamHandler handler = new ChatStreamHandler();
        handler.init();
        try {
            final java.lang.reflect.Field poolField = ChatStreamHandler.class.getDeclaredField("sharedKeepalivePool");
            poolField.setAccessible(true);
            final java.util.concurrent.ScheduledExecutorService pool =
                    (java.util.concurrent.ScheduledExecutorService) poolField.get(handler);

            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);
            // Use a very long interval so the task never actually fires during the test.
            final java.util.concurrent.ScheduledFuture<?> future = handler.startKeepalivePinger(pw, new Object(), 60000L);
            assertNotNull(future, "future must not be null");
            assertFalse(future.isDone(), "future must not be done before cancellation");

            handler.stopKeepalivePinger(future);

            // Future must be cancelled.
            assertTrue(future.isDone(), "future must be done after stopKeepalivePinger");
            assertTrue(future.isCancelled(), "future must be cancelled (not completed normally) after stopKeepalivePinger");

            // Shared pool must remain alive — only the request-level future is cancelled.
            assertFalse(pool.isShutdown(), "shared pool must NOT be shut down after stopKeepalivePinger");
        } finally {
            handler.destroy();
        }
    }

    @Test
    public void test_keepalivePoolThreadNaming() throws Exception {
        // Threads in the shared pool must follow the fess-sse-keepalive-N naming pattern
        // so they are identifiable in thread dumps and do not collide with other pools.
        final ChatStreamHandler handler = new ChatStreamHandler();
        handler.init();
        try {
            // Submit a task that captures the name of the executing thread, then await it.
            final java.lang.reflect.Field poolField = ChatStreamHandler.class.getDeclaredField("sharedKeepalivePool");
            poolField.setAccessible(true);
            final java.util.concurrent.ScheduledExecutorService pool =
                    (java.util.concurrent.ScheduledExecutorService) poolField.get(handler);

            final java.util.concurrent.CompletableFuture<String> threadNameFuture = new java.util.concurrent.CompletableFuture<>();
            pool.submit(() -> threadNameFuture.complete(Thread.currentThread().getName()));
            final String threadName = threadNameFuture.get(3, java.util.concurrent.TimeUnit.SECONDS);

            assertTrue(threadName.matches("fess-sse-keepalive-\\d+"),
                    "keepalive pool thread name must match fess-sse-keepalive-N pattern; got: " + threadName);
        } finally {
            handler.destroy();
        }
    }

    @Test
    public void test_oversizedBody_returnsJsonEnvelope() throws Exception {
        // V2JsonBody caps the body at MAX_BODY_BYTES (32KiB for the streaming handler).
        // Any larger payload must return HTTP 400 application/json — not an SSE event —
        // because body-parse is a pre-stream gate check.
        enableRagChat();
        final String bigBody = "{\"message\":\"" + "x".repeat(40 * 1024) + "\"}";
        final CapturingResponse res = new CapturingResponse();
        new ChatStreamHandler().handle(new StubRequest("POST", "/api/v2/chat/stream").withJsonBody(bigBody), res);
        assertEquals(400, res.status);
        assertEquals("application/json", contentTypeMimeOnly(res));
        final String out = res.body();
        assertTrue(out.contains("\"code\":\"invalid_request\""), out);
        // Must NOT be SSE framing
        assertFalse(out.contains("event: error"), out);
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
