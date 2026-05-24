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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.chat.ChatApiHelper;
import org.codelibs.fess.api.v2.V2EnvelopeWriter;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.chat.ChatClient.ChatResult;
import org.codelibs.fess.chat.ChatPhaseCallback;
import org.codelibs.fess.entity.ChatMessage.ChatSource;
import org.codelibs.fess.llm.LlmException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    /** Max raw body bytes the handler will read. Same generous buffer as ChatHandler. */
    private static final int MAX_BODY_BYTES = 32 * 1024;

    /** Payload keys reserved by the SSE protocol; must not be overridden by phase callback metadata. */
    private static final Set<String> RESERVED_PAYLOAD_KEYS = Set.of("phase", "status");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public void handle(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        // --- Gate checks: ALL validation runs before any SSE headers are set. ---
        // Pre-stream failures return proper HTTP status via V2EnvelopeWriter.writeError
        // (application/json). SSE headers are only committed after every gate passes.

        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            res.setHeader("Allow", "POST");
            V2EnvelopeWriter.writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isRagChatEnabled()) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, "chat is not enabled");
            return;
        }

        final Map<String, Object> raw;
        try {
            raw = V2JsonBody.read(req, MAX_BODY_BYTES);
        } catch (final V2JsonBody.PayloadTooLargeException | V2JsonBody.MalformedJsonException
                | V2JsonBody.UnsupportedMediaTypeException e) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, e.getMessage());
            return;
        }

        final int maxLen = ChatApiHelper.getMaxMessageLength(fessConfig);
        final ChatRequestBody body;
        try {
            body = ChatRequestBody.from(raw, maxLen);
        } catch (final ChatRequestBody.MessageTooLongException e) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, e.getMessage());
            return;
        }

        if (StringUtil.isBlank(body.message())) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, "message is required");
            return;
        }

        final String userId = ChatApiHelper.getUserId(req);
        // Per-user chat rate limit. Pre-stream failure returns a proper 429 JSON envelope.
        final LoginRateLimiter limiter;
        try {
            limiter = ComponentUtil.getLoginRateLimiter();
        } catch (final RuntimeException e) {
            // Limiter DI not available (e.g. slim test harness); log and surface as 500.
            logger.warn("[RAG] /api/v2/chat/stream rate-limit lookup failed", e);
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INTERNAL_ERROR, "internal error");
            return;
        }
        final int chatLimit = ChatApiHelper.getChatRateLimitPerMinute(fessConfig);
        if (limiter != null && chatLimit > 0 && !limiter.allow(LoginRateLimiter.Scope.CHAT, userId, chatLimit, 60)) {
            res.setHeader("Retry-After", "60");
            V2EnvelopeWriter.writeError(res, V2ErrorCode.RATE_LIMITED, "too many chat requests");
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

        // Tag the request for the search-log access-type column, same as v1.
        req.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE, fessConfig.getSystemProperty("rag.llm.name", "ollama"));

        try {
            final ChatPhaseCallback phaseCallback = newPhaseCallback(writer, errorEmittedHolder);

            final ChatResult result;
            if (body.fields().isEmpty() && body.extraQueries().length == 0) {
                result = ComponentUtil.getChatClient().streamChatEnhanced(body.sessionId(), body.message(), userId, phaseCallback);
            } else {
                result = ComponentUtil.getChatClient()
                        .streamChatEnhanced(body.sessionId(), body.message(), userId, body.fields(), body.extraQueries(), phaseCallback);
            }

            final List<ChatSource> sources = result.getMessage().getSources();
            if (sources != null && !sources.isEmpty()) {
                sendSseEvent(writer, "sources", Map.of("sources", ChatHandler.toSourceMaps(sources)));
            }

            final Map<String, Object> doneData = new LinkedHashMap<>();
            doneData.put("session_id", result.getSessionId());
            final String htmlContent = result.getMessage().getHtmlContent();
            if (htmlContent != null) {
                doneData.put("html_content", htmlContent);
            }
            sendSseEvent(writer, "done", doneData);
        } catch (final LlmException e) {
            // The callback already emitted onError to the SSE stream; do not double-send.
            logger.warn("[RAG] /api/v2/chat/stream LLM error. sessionId={}, errorCode={}", body.sessionId(), e.getErrorCode());
        } catch (final Exception e) {
            logger.warn("[RAG] /api/v2/chat/stream failed. error={}", e.getMessage(), e);
            // Avoid double-emitting an error event when the callback already wrote one,
            // and avoid writing to a closed response body. Reuse the same writer we
            // acquired at the top — do NOT call res.getWriter() again here because the
            // container may have already closed it.
            if (!errorEmittedHolder[0] && !res.isCommitted()) {
                try {
                    sendSseEvent(writer, "error", Map.of("message", "internal error", "error_code", LlmException.ERROR_UNKNOWN));
                } catch (final Exception ioe) {
                    logger.warn("Failed to send SSE error after exception", ioe);
                }
            }
        }
    }

    /**
     * Same SSE headers as v1. The {@code X-Accel-Buffering: no} header is critical when
     * running behind nginx — without it the entire stream gets buffered until
     * close, which destroys the SSE UX.
     *
     * @param res the HTTP response to set headers on
     */
    protected void setSseHeaders(final HttpServletResponse res) {
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/event-stream; charset=UTF-8");
        res.setHeader("Cache-Control", "no-cache");
        res.setHeader("Connection", "keep-alive");
        res.setHeader("X-Accel-Buffering", "no");
    }

    /**
     * Builds a {@link ChatPhaseCallback} that emits SSE events with snake_case keys.
     *
     * @param writer the per-request servlet writer to emit events to
     * @return a callback bound to the writer
     */
    protected ChatPhaseCallback newPhaseCallback(final PrintWriter writer) {
        return newPhaseCallback(writer, new boolean[1]);
    }

    /**
     * Builds a {@link ChatPhaseCallback} that emits SSE events with snake_case keys.
     * The supplied {@code errorEmittedHolder} flag is set whenever onError is
     * invoked so the surrounding handler can avoid double-emitting an error
     * event from its outer catch.
     *
     * @param writer the per-request servlet writer to emit events to
     * @param errorEmittedHolder single-element boolean array; element 0 is set to true
     *                           after the first onError emission
     * @return a callback bound to the writer
     */
    protected ChatPhaseCallback newPhaseCallback(final PrintWriter writer, final boolean[] errorEmittedHolder) {
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
                emitSafely(writer, "phase", data);
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
                emitSafely(writer, "phase", data);
            }

            @Override
            public void onChunk(final String content, final boolean done) {
                if (content != null && !content.isEmpty()) {
                    emitSafely(writer, "chunk", Map.of("content", content));
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
                emitSafely(writer, "error", data);
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
                emitSafely(writer, "retry", data);
            }

            @Override
            public void onWaiting(final String phase, final String reason, final long elapsedMs, final long timeoutMs) {
                final Map<String, Object> data = new HashMap<>();
                data.put("phase", phase);
                data.put("reason", reason);
                data.put("elapsed_ms", elapsedMs);
                data.put("timeout_ms", timeoutMs);
                emitSafely(writer, "waiting", data);
            }

            @Override
            public void onFallback(final String phase, final String reason, final String originalQuery, final String newQuery) {
                final Map<String, Object> data = new HashMap<>();
                data.put("phase", phase);
                data.put("reason", reason);
                putIfNotNull(data, "original_query", originalQuery);
                putIfNotNull(data, "new_query", newQuery);
                emitSafely(writer, "fallback", data);
            }

            @Override
            public void onWarning(final String phase, final String code, final String detail) {
                final Map<String, Object> data = new HashMap<>();
                data.put("phase", phase);
                data.put("code", code);
                putIfNotNull(data, "detail", detail);
                emitSafely(writer, "warning", data);
            }
        };
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
     * Helper to add a key/value pair to a map only when the value is non-null.
     *
     * @param data target map
     * @param key key
     * @param value value to add when non-null
     */
    protected static void putIfNotNull(final Map<String, Object> data, final String key, final Object value) {
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
