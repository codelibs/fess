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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.v2.V2EnvelopeWriter;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.chat.ChatClient.ChatResult;
import org.codelibs.fess.chat.ChatPhaseCallback;
import org.codelibs.fess.entity.ChatMessage.ChatSource;
import org.codelibs.fess.helper.SseResponseHelper;
import org.codelibs.fess.llm.LlmException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles {@code POST /api/v2/chat/stream} — Server-Sent Events streaming RAG chat.
 *
 * <p><b>Envelope exception:</b> SSE wire format is incompatible with the
 * {@code {"response": {...}}} JSON envelope used by every other v2 endpoint
 * except {@code /api/v2/documents/all} (NDJSON). Plan 4 deliberately keeps the
 * same SSE framing as v1 ({@code event:} + {@code data: <json>\n\n}) so the
 * static theme JS can share a single SSE parser across v1 and v2. The exception
 * is documented in §Risks (Risk 2).</p>
 *
 * <p>All SSE event data keys use snake_case:</p>
 * <ul>
 *   <li>{@code event: phase} — {@code {phase, status: start|complete, message?, keywords?, hit_count?}}</li>
 *   <li>{@code event: chunk} — {@code {content}}</li>
 *   <li>{@code event: sources} — {@code {sources: [{rank, title, url, doc_id, snippet, url_link, go_url}]}}</li>
 *   <li>{@code event: done} — {@code {session_id, html_content?}}</li>
 *   <li>{@code event: retry} — {@code {phase, operation, attempt, max_attempts, sleep_ms, cause?}}</li>
 *   <li>{@code event: waiting} — {@code {phase, reason, elapsed_ms, timeout_ms}}</li>
 *   <li>{@code event: fallback} — {@code {phase, reason, original_query?, new_query?}}</li>
 *   <li>{@code event: warning} — {@code {phase, code, detail?}}</li>
 *   <li>{@code event: error} — {@code {phase?, message, error_code}}</li>
 * </ul>
 *
 * <p>Error reporting <em>before</em> the LLM is invoked (method check, feature gate,
 * body parse, rate limit) uses {@link V2EnvelopeWriter#writeError} so the HTTP status
 * and {@code Content-Type: application/json} are correct. Only after all gates pass are
 * SSE headers set; subsequent LLM-level errors are reported via {@code event: error}
 * SSE events, consistent with v1 behaviour the static theme JS parser depends on.</p>
 */
public class ChatStreamHandler {

    private static final Logger logger = LogManager.getLogger(ChatStreamHandler.class);

    /**
     * Number of threads in the shared keep-alive scheduler pool.
     *
     * <p>Two threads are sufficient for typical deployments: one can be occupied
     * delivering a ping while the second takes the next scheduled slot, preventing
     * head-of-line blocking under bursts of simultaneous SSE connections. A single
     * thread would serialise all pings — acceptable for low concurrency but a
     * risk at scale. More than two threads offer no benefit because each task is
     * O(microseconds) (a single write + flush) and the dominant cost is the
     * scheduler overhead itself, not CPU.</p>
     */
    private static final int KEEPALIVE_POOL_SIZE = 2;

    /**
     * Shared scheduler for SSE keep-alive pings. All concurrent SSE requests share this
     * pool so thread count is bounded to {@link #KEEPALIVE_POOL_SIZE} regardless of the
     * number of simultaneous connections. Threads are daemon threads so they never prevent
     * JVM shutdown.
     *
     * <p>Lifecycle: initialized in {@link #init()} ({@link PostConstruct}) and shut down
     * in {@link #destroy()} ({@link PreDestroy}). The field is {@code volatile} so the
     * write in {@code init()} is visible to request threads without requiring
     * synchronization on every {@code handle()} call.</p>
     */
    private volatile ScheduledExecutorService sharedKeepalivePool;

    /**
     * Default constructor used by the DI container. The handler holds no
     * per-request state and is safe to share across concurrent requests.
     * {@link #init()} must be called (by the DI container via {@link PostConstruct})
     * before any request is processed.
     */
    public ChatStreamHandler() {
        // no-op — pool is created in init()
    }

    /**
     * Initializes the shared keep-alive scheduler pool. Called by Lasta Di after
     * the component is bound ({@link PostConstruct}).
     *
     * <p>The pool uses daemon threads named {@code fess-sse-keepalive-N} (1-based)
     * so they are visible in thread dumps and never prevent JVM shutdown.</p>
     */
    @PostConstruct
    public void init() {
        final AtomicInteger counter = new AtomicInteger(0);
        final ThreadFactory factory = r -> {
            final Thread t = new Thread(r, "fess-sse-keepalive-" + counter.incrementAndGet());
            t.setDaemon(true);
            return t;
        };
        sharedKeepalivePool = Executors.newScheduledThreadPool(KEEPALIVE_POOL_SIZE, factory);
        if (logger.isInfoEnabled()) {
            logger.info("ChatStreamHandler keepalive pool initialized: poolSize={}", KEEPALIVE_POOL_SIZE);
        }
    }

    /**
     * Shuts down the shared keep-alive pool on container shutdown ({@link PreDestroy}).
     *
     * <p>Waits up to 1 second for in-flight tasks to complete; if they have not
     * finished by then, {@link ScheduledExecutorService#shutdownNow()} is called to
     * interrupt waiting threads. Individual per-request futures are cancelled via
     * {@link ScheduledFuture#cancel(boolean)} before this point, so only the pool
     * lifecycle itself needs termination here.</p>
     */
    @PreDestroy
    public void destroy() {
        final ScheduledExecutorService pool = sharedKeepalivePool;
        if (pool == null) {
            return;
        }
        pool.shutdown();
        try {
            if (!pool.awaitTermination(1L, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (final InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        if (logger.isInfoEnabled()) {
            logger.info("ChatStreamHandler keepalive pool shut down");
        }
    }

    /** Max raw body bytes the handler will read. Same generous buffer as ChatHandler. */
    private static final int MAX_BODY_BYTES = 32 * 1024;

    /** Payload keys reserved by the SSE protocol; must not be overridden by phase callback metadata. */
    private static final Set<String> RESERVED_PAYLOAD_KEYS = Set.of("phase", "status");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Processes one {@code POST /api/v2/chat/stream} request.
     *
     * <p>All pre-stream validation (method, feature gate, body parse, rate
     * limit) runs <strong>before</strong> any SSE headers are committed so that
     * failures can be reported as a normal JSON error envelope. Once every gate
     * passes the response is switched to {@code text/event-stream} and the LLM
     * is invoked; subsequent failures surface via an {@code event: error} SSE
     * event rather than an HTTP error code.</p>
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @throws IOException if writing the envelope or reading the body fails
     */
    public void handle(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        // --- Gate checks: ALL validation runs before any SSE headers are set. ---
        // Pre-stream failures return proper HTTP status via ComponentUtil.getV2EnvelopeWriter().writeError
        // (application/json). SSE headers are only committed after every gate passes.

        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            res.setHeader("Allow", "POST");
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isRagChatEnabled()) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INVALID_REQUEST, "chat is not enabled");
            return;
        }

        final Map<String, Object> raw;
        try {
            raw = ComponentUtil.getV2JsonBody().read(req, MAX_BODY_BYTES);
        } catch (final V2JsonBody.PayloadTooLargeException e) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.PAYLOAD_TOO_LARGE, e.getMessage());
            return;
        } catch (final V2JsonBody.UnsupportedMediaTypeException e) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.UNSUPPORTED_MEDIA_TYPE, e.getMessage());
            return;
        } catch (final V2JsonBody.MalformedJsonException e) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INVALID_REQUEST, e.getMessage());
            return;
        }

        final int maxLen = ComponentUtil.getChatApiHelper().getMaxMessageLength(fessConfig);
        final ChatRequestBody body;
        try {
            body = ComponentUtil.getChatApiHelper().parseRequestBody(raw, maxLen);
        } catch (final ChatRequestBody.MessageTooLongException e) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INVALID_REQUEST, e.getMessage());
            return;
        }

        if (StringUtil.isBlank(body.message())) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INVALID_REQUEST, "message is required");
            return;
        }

        final String userId = getUserId(req);
        // Per-user chat rate limit. Pre-stream failure returns a proper 429 JSON envelope.
        final LoginRateLimiter limiter;
        try {
            limiter = ComponentUtil.getLoginRateLimiter();
        } catch (final RuntimeException e) {
            // Limiter DI not available (e.g. slim test harness); log and surface as 500.
            logger.warn("[RAG] /api/v2/chat/stream rate-limit lookup failed", e);
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INTERNAL_ERROR, "internal error");
            return;
        }
        final int chatLimit = fessConfig.getChatRateLimitPerMinute();
        // Skip the per-user throttle for anonymous callers with no resolvable user id
        // (e.g. a guest whose session is not yet established): a null/blank key would
        // otherwise hit the limiter's null-key deny path and 429 the first request.
        if (limiter != null && chatLimit > 0 && StringUtil.isNotBlank(userId)
                && !limiter.allow(LoginRateLimiter.Scope.CHAT, userId, chatLimit, 60)) {
            res.setHeader("Retry-After", "60");
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.RATE_LIMITED, "too many chat requests");
            return;
        }

        // --- All gates passed: now commit to SSE framing. ---
        // Acquire the writer exactly once. The servlet container manages its lifecycle for
        // the duration of the request; using try-with-resources here would prematurely close
        // the writer and prevent the outer error path from emitting a final SSE event.
        setSseHeaders(res);
        final PrintWriter writer = res.getWriter();
        // Tracks whether the inner phase callback has already emitted an "error" SSE event.
        // The outer catch consults it to avoid emitting a duplicate error event.
        final boolean[] errorEmittedHolder = { false };
        // Shared monitor that serializes ALL writes to the response writer — phase events,
        // chunks, keep-alive pings and the final done/error. Without this, the scheduler
        // thread could interleave a ":keepalive" comment in the middle of an event:/data:
        // pair from the request thread, corrupting the SSE wire format.
        final Object writeLock = new Object();

        // Tag the request for the search-log access-type column, same as v1.
        req.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE, fessConfig.getSystemProperty("rag.llm.name", "ollama"));

        // Schedule periodic keep-alive pings to defeat idle-connection timeouts on
        // intermediaries (nginx default proxy_read_timeout = 60s) during long LLM phases.
        // The future is cancelled in finally — the shared pool itself is NOT shut down here.
        final long keepaliveMs = resolveKeepaliveIntervalMs(fessConfig);
        final ScheduledFuture<?> pingerFuture = startKeepalivePinger(writer, writeLock, keepaliveMs);

        try {
            final ChatPhaseCallback phaseCallback = newPhaseCallback(writer, errorEmittedHolder, writeLock);

            final ChatResult result;
            if (body.fields().isEmpty() && body.extraQueries().length == 0) {
                result = ComponentUtil.getChatClient().streamChatEnhanced(body.sessionId(), body.message(), userId, phaseCallback);
            } else {
                result = ComponentUtil.getChatClient()
                        .streamChatEnhanced(body.sessionId(), body.message(), userId, body.fields(), body.extraQueries(), phaseCallback);
            }

            final List<ChatSource> sources = result.getMessage().getSources();
            if (sources != null && !sources.isEmpty()) {
                synchronized (writeLock) {
                    sendSseEvent(writer, "sources", Map.of("sources", ComponentUtil.getChatApiHelper().toSourceMaps(sources)));
                }
            }

            final Map<String, Object> doneData = new LinkedHashMap<>();
            doneData.put("session_id", result.getSessionId());
            final String htmlContent = result.getMessage().getHtmlContent();
            if (htmlContent != null) {
                doneData.put("html_content", htmlContent);
            }
            synchronized (writeLock) {
                sendSseEvent(writer, "done", doneData);
            }
        } catch (final LlmException e) {
            // The callback already emitted onError to the SSE stream; do not double-send.
            logger.warn("[RAG] /api/v2/chat/stream LLM error. sessionId={}, errorCode={}", body.sessionId(), e.getErrorCode());
        } catch (final ClientDisconnectedException e) {
            // Client closed the TCP connection mid-stream; no point writing an error event
            // to a dead socket. Log at DEBUG to avoid noise in normal operation.
            logger.debug("[RAG] /api/v2/chat/stream aborted: client disconnected mid-stream. sessionId={}", body.sessionId());
        } catch (final Exception e) {
            logger.warn("[RAG] /api/v2/chat/stream failed. error={}", e.getMessage(), e);
            // Avoid double-emitting an error event when the callback already wrote one,
            // and avoid writing to a closed response body. Reuse the same writer we
            // acquired at the top — do NOT call res.getWriter() again here because the
            // container may have already closed it.
            if (!errorEmittedHolder[0] && !res.isCommitted()) {
                try {
                    synchronized (writeLock) {
                        sendSseEvent(writer, "error", Map.of("message", "internal error", "error_code", LlmException.ERROR_UNKNOWN));
                    }
                } catch (final Exception ioe) {
                    logger.warn("Failed to send SSE error after exception", ioe);
                }
            }
        } finally {
            stopKeepalivePinger(pingerFuture);
        }
    }

    /**
     * Resolves the effective chat user id. Exposed as a seam so unit tests can override the user
     * identity directly. {@code SystemHelper}/{@code UserInfoHelper} are smart-deploy components,
     * so stubbing them via {@code ComponentUtil.register} is not reliable once the shared test
     * container has resolved the real ones; overriding this method avoids that fragility.
     *
     * @param req the incoming HTTP request
     * @return the user identifier (never null)
     */
    protected String getUserId(final HttpServletRequest req) {
        return ComponentUtil.getChatApiHelper().getUserId();
    }

    /**
     * Resolves the keep-alive interval from configuration with a safe default. Returns the
     * configured value when valid, or {@code 15000} ms when the config key is missing,
     * malformed, or negative. A value of {@code 0} disables the pinger.
     *
     * @param fessConfig active configuration
     * @return interval in milliseconds; {@code 0} disables the pinger
     */
    protected long resolveKeepaliveIntervalMs(final FessConfig fessConfig) {
        try {
            final Integer v = fessConfig.getApiV2ChatStreamKeepaliveIntervalMsAsInteger();
            if (v == null) {
                return 15000L;
            }
            return v.longValue() < 0 ? 0L : v.longValue();
        } catch (final RuntimeException e) {
            // Config key missing in slim test harnesses or malformed value: fall back to default.
            return 15000L;
        }
    }

    /**
     * Schedules a periodic keep-alive ping task on the shared {@link #sharedKeepalivePool}.
     * Emits {@code ": keepalive\n\n"} (SSE comment line — ignored by EventSource clients)
     * on a fixed delay. Returns {@code null} when {@code intervalMs} is {@code <=0} so the
     * pinger is fully disabled.
     *
     * <p>All emissions are guarded by {@code writeLock} which is also held by the
     * request thread while writing event/data pairs, so the pinger can never interleave
     * inside an event frame.</p>
     *
     * <p>The returned {@link ScheduledFuture} must be cancelled (not the pool) when the
     * request completes — call {@link #stopKeepalivePinger(ScheduledFuture)}.</p>
     *
     * @param writer servlet writer
     * @param writeLock shared monitor serializing writes to {@code writer}
     * @param intervalMs ping interval; {@code <=0} disables pinging
     * @return a future representing the scheduled ping task, or {@code null} when disabled
     */
    protected ScheduledFuture<?> startKeepalivePinger(final PrintWriter writer, final Object writeLock, final long intervalMs) {
        if (intervalMs <= 0L) {
            return null;
        }
        final ScheduledExecutorService pool = sharedKeepalivePool;
        if (pool == null || pool.isShutdown()) {
            // Pool not yet initialized (e.g. slim test harness that did not call init())
            // or already shut down — degrade gracefully without pinging.
            logger.debug("ChatStreamHandler: keepalive pool unavailable; pinger disabled for this request");
            return null;
        }
        // Use fixed-delay (not fixed-rate) scheduling: a heartbeat needs no catch-up
        // semantics, and fixed-rate would queue burst pings if a write is delayed by
        // writeLock contention during a long chunk flush.
        final ScheduledFuture<?> future = pool.scheduleWithFixedDelay(() -> {
            try {
                synchronized (writeLock) {
                    writer.write(": keepalive\n\n");
                    writer.flush();
                }
            } catch (final Exception e) {
                // Most likely the client disconnected — don't spam logs; let the
                // request thread surface the error on its next write.
                logger.debug("SSE keep-alive write failed", e);
            }
        }, intervalMs, intervalMs, TimeUnit.MILLISECONDS);
        // scheduleWithFixedDelay never returns null, so no null-check/assert is needed here.
        return future;
    }

    /**
     * Cancels a keep-alive ping future previously returned by
     * {@link #startKeepalivePinger(PrintWriter, Object, long)}. The shared pool is
     * NOT shut down — only this request's scheduled task is cancelled.
     * Safe to call with {@code null}.
     *
     * @param future the ping future to cancel; may be null
     */
    protected void stopKeepalivePinger(final ScheduledFuture<?> future) {
        if (future == null) {
            return;
        }
        try {
            // mayInterruptIfRunning=false: let a ping that already started (write + flush)
            // finish naturally; the cost is bounded by one flush cycle.
            future.cancel(false);
        } catch (final RuntimeException e) {
            logger.debug("SSE keep-alive future cancel failed", e);
        }
    }

    /**
     * Same SSE headers as v1. Delegates to {@link SseResponseHelper#applySseHeaders} so the
     * baseline (including the critical {@code X-Accel-Buffering: no} header that disables
     * nginx response buffering) stays in sync across v1 and v2.
     *
     * @param res the HTTP response to set headers on
     */
    protected void setSseHeaders(final HttpServletResponse res) {
        ComponentUtil.getSseResponseHelper().applySseHeaders(res);
    }

    /**
     * Builds a {@link ChatPhaseCallback} that emits SSE events with snake_case keys.
     *
     * @param writer the per-request servlet writer to emit events to
     * @return a callback bound to the writer
     */
    protected ChatPhaseCallback newPhaseCallback(final PrintWriter writer) {
        return newPhaseCallback(writer, new boolean[1], new Object());
    }

    /**
     * Backward-compatible overload that synthesises a private write lock. Prefer
     * {@link #newPhaseCallback(PrintWriter, boolean[], Object)} from the request
     * thread so the lock is shared with the keep-alive pinger.
     *
     * @param writer the per-request servlet writer to emit events to
     * @param errorEmittedHolder single-element boolean array; element 0 is set to true
     *                           after the first onError emission
     * @return a callback bound to the writer
     */
    protected ChatPhaseCallback newPhaseCallback(final PrintWriter writer, final boolean[] errorEmittedHolder) {
        return newPhaseCallback(writer, errorEmittedHolder, new Object());
    }

    /**
     * Builds a {@link ChatPhaseCallback} that emits SSE events with snake_case keys.
     * The supplied {@code errorEmittedHolder} flag is set whenever onError is
     * invoked so the surrounding handler can avoid double-emitting an error
     * event from its outer catch. All emissions are serialized on {@code writeLock}
     * so they cannot interleave with concurrent keep-alive pings.
     *
     * @param writer the per-request servlet writer to emit events to
     * @param errorEmittedHolder single-element boolean array; element 0 is set to true
     *                           after the first onError emission
     * @param writeLock shared monitor serializing writes to {@code writer}
     * @return a callback bound to the writer
     */
    protected ChatPhaseCallback newPhaseCallback(final PrintWriter writer, final boolean[] errorEmittedHolder, final Object writeLock) {
        return new ChatPhaseCallback() {
            @Override
            public void onPhaseStart(final String phase, final String message) {
                onPhaseStart(phase, message, null);
            }

            @Override
            public void onPhaseStart(final String phase, final String message, final String keywords) {
                final Map<String, Object> data = new HashMap<>();
                data.put("phase", phase);
                data.put("status", "start");
                data.put("message", message);
                putIfNotNull(data, "keywords", keywords);
                emitSafely(writer, "phase", data, writeLock);
            }

            @Override
            public void onPhaseComplete(final String phase) {
                onPhaseComplete(phase, Collections.emptyMap());
            }

            @Override
            public void onPhaseComplete(final String phase, final Map<String, Object> payload) {
                final Map<String, Object> data = new HashMap<>();
                data.put("phase", phase);
                data.put("status", "complete");
                if (payload != null) {
                    payload.forEach((k, v) -> {
                        if (v != null && !RESERVED_PAYLOAD_KEYS.contains(k)) {
                            // Rename hitCount → hit_count in phase completion payloads.
                            final String outKey = "hitCount".equals(k) ? "hit_count" : k;
                            data.put(outKey, v);
                        }
                    });
                }
                emitSafely(writer, "phase", data, writeLock);
            }

            @Override
            public void onChunk(final String content, final boolean done) {
                // Client-disconnect detection: PrintWriter.checkError() flips to true
                // when the underlying stream has encountered an error (typically because
                // the client closed the TCP connection mid-stream). Abort the emission
                // loop early rather than keep generating tokens into a dead socket.
                if (writer.checkError()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("[v2/chat/stream] client disconnected; aborting emission");
                    }
                    throw new ClientDisconnectedException();
                }
                if (content != null && !content.isEmpty()) {
                    emitSafely(writer, "chunk", Map.of("content", content), writeLock);
                }
            }

            @Override
            public void onError(final String phase, final String errorCode) {
                errorEmittedHolder[0] = true;
                // Map.of rejects null values with NPE — build the payload defensively so a
                // future caller passing phase=null does not crash the error event itself.
                // errorCode is guaranteed non-null by the wire contract.
                final Map<String, Object> data = new LinkedHashMap<>();
                putIfNotNull(data, "phase", phase);
                data.put("message", errorCode);
                data.put("error_code", errorCode);
                emitSafely(writer, "error", data, writeLock);
            }

            @Override
            public void onRetry(final String phase, final String operation, final int attempt, final int maxAttempts, final long sleepMs,
                    final String cause) {
                final Map<String, Object> data = new HashMap<>();
                data.put("phase", phase);
                data.put("operation", operation);
                data.put("attempt", attempt);
                data.put("max_attempts", maxAttempts);
                data.put("sleep_ms", sleepMs);
                putIfNotNull(data, "cause", cause);
                emitSafely(writer, "retry", data, writeLock);
            }

            @Override
            public void onWaiting(final String phase, final String reason, final long elapsedMs, final long timeoutMs) {
                final Map<String, Object> data = new HashMap<>();
                data.put("phase", phase);
                data.put("reason", reason);
                data.put("elapsed_ms", elapsedMs);
                data.put("timeout_ms", timeoutMs);
                emitSafely(writer, "waiting", data, writeLock);
            }

            @Override
            public void onFallback(final String phase, final String reason, final String originalQuery, final String newQuery) {
                final Map<String, Object> data = new HashMap<>();
                data.put("phase", phase);
                data.put("reason", reason);
                putIfNotNull(data, "original_query", originalQuery);
                putIfNotNull(data, "new_query", newQuery);
                emitSafely(writer, "fallback", data, writeLock);
            }

            @Override
            public void onWarning(final String phase, final String code, final String detail) {
                final Map<String, Object> data = new HashMap<>();
                data.put("phase", phase);
                data.put("code", code);
                putIfNotNull(data, "detail", detail);
                emitSafely(writer, "warning", data, writeLock);
            }
        };
    }

    /**
     * Thrown by {@link ChatPhaseCallback#onChunk} when {@link PrintWriter#checkError()}
     * detects that the client has disconnected mid-stream. This is a control-flow
     * signal only — it carries no message payload and is caught by the outer handler
     * to abort token generation without emitting a redundant SSE error event.
     */
    static final class ClientDisconnectedException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        ClientDisconnectedException() {
            super("client disconnected", null, true, false); // suppress stacktrace fill-in
        }
    }

    /**
     * Wraps {@link #sendSseEvent} so a failure inside the phase callback (typically an
     * IOException from a disconnected client) does not propagate and abort the
     * surrounding {@code streamChatEnhanced} call.
     *
     * @param writer servlet writer
     * @param event SSE event name
     * @param data event payload to serialize as JSON
     */
    protected void emitSafely(final PrintWriter writer, final String event, final Map<String, Object> data) {
        try {
            sendSseEvent(writer, event, data);
        } catch (final Exception e) {
            logger.warn("Failed to emit SSE event. event={}", event, e);
        }
    }

    /**
     * Lock-aware variant of {@link #emitSafely(PrintWriter, String, Map)} that
     * serializes the write on a shared monitor — used so phase events cannot
     * interleave with concurrent keep-alive pings inside an event/data frame.
     *
     * @param writer servlet writer
     * @param event SSE event name
     * @param data event payload to serialize as JSON
     * @param writeLock shared monitor serializing writes to {@code writer}
     */
    protected void emitSafely(final PrintWriter writer, final String event, final Map<String, Object> data, final Object writeLock) {
        try {
            synchronized (writeLock) {
                sendSseEvent(writer, event, data);
            }
        } catch (final Exception e) {
            logger.warn("Failed to emit SSE event. event={}", event, e);
        }
    }

    /**
     * Helper to add a key/value pair to a map only when the value is non-null.
     *
     * @param data target map
     * @param key key
     * @param value value to add when non-null
     */
    protected void putIfNotNull(final Map<String, Object> data, final String key, final Object value) {
        if (value != null) {
            data.put(key, value);
        }
    }

    /**
     * Writes one SSE event using v1's wire format: {@code event: <name>\ndata: <json>\n\n}.
     *
     * @param writer servlet writer
     * @param event SSE event name
     * @param data event payload to serialize as JSON
     */
    protected void sendSseEvent(final PrintWriter writer, final String event, final Map<String, Object> data) {
        try {
            writer.write("event: " + event + "\n");
            writer.write("data: " + MAPPER.writeValueAsString(data) + "\n\n");
            writer.flush();
        } catch (final JsonProcessingException e) {
            logger.warn("[RAG] failed to serialize SSE data. event={}", event, e);
        }
    }
}
