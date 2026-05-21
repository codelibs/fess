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
import org.codelibs.fess.chat.ChatClient.ChatResult;
import org.codelibs.fess.chat.ChatPhaseCallback;
import org.codelibs.fess.entity.ChatMessage.ChatSource;
import org.codelibs.fess.helper.SystemHelper;
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
 * <p>Phase callback events mirror v1:</p>
 * <ul>
 *   <li>{@code event: phase} — {@code {phase, status: start|complete, message?, keywords?, hitCount?}}</li>
 *   <li>{@code event: chunk} — {@code {content}}</li>
 *   <li>{@code event: sources} — {@code {sources: [...]}}</li>
 *   <li>{@code event: done} — {@code {sessionId, htmlContent?}}</li>
 *   <li>{@code event: retry|waiting|fallback|warning|error} — diagnostic events (same v1 shape)</li>
 * </ul>
 *
 * <p>Error reporting before the LLM is invoked goes through a single
 * {@code event: error} SSE event followed by stream close — never a v2 envelope —
 * so the SPA's SSE reader doesn't have to switch parser mid-flight.</p>
 */
public class ChatStreamHandler {

    private static final Logger logger = LogManager.getLogger(ChatStreamHandler.class);

    /** Max raw body bytes the handler will read. Same generous buffer as ChatHandler. */
    private static final int MAX_BODY_BYTES = 32 * 1024;

    /** Payload keys reserved by the SSE protocol; must not be overridden by phase callback metadata. */
    private static final Set<String> RESERVED_PAYLOAD_KEYS = Set.of("phase", "status");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public void handle(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        // Always set SSE headers before any writes so error events also arrive
        // over the same channel the SPA is listening on.
        setSseHeaders(res);

        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            try (final PrintWriter writer = res.getWriter()) {
                sendSseEvent(writer, "error", Map.of("message", "method not allowed", "errorCode", "method_not_allowed"));
            }
            return;
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isRagChatEnabled()) {
            try (final PrintWriter writer = res.getWriter()) {
                sendSseEvent(writer, "error", Map.of("message", "chat is not enabled", "errorCode", "chat_disabled"));
            }
            return;
        }

        final Map<String, Object> raw;
        try {
            raw = V2JsonBody.read(req, MAX_BODY_BYTES);
        } catch (final V2JsonBody.PayloadTooLargeException | V2JsonBody.MalformedJsonException
                | V2JsonBody.UnsupportedMediaTypeException e) {
            try (final PrintWriter writer = res.getWriter()) {
                sendSseEvent(writer, "error", Map.of("message", e.getMessage(), "errorCode", "invalid_request"));
            }
            return;
        }

        final int maxLen = getMaxMessageLength(fessConfig);
        final ChatRequestBody body;
        try {
            body = ChatRequestBody.from(raw, maxLen);
        } catch (final ChatRequestBody.MessageTooLongException e) {
            try (final PrintWriter writer = res.getWriter()) {
                sendSseEvent(writer, "error", Map.of("message", e.getMessage(), "errorCode", "message_too_long"));
            }
            return;
        }

        if (StringUtil.isBlank(body.message())) {
            try (final PrintWriter writer = res.getWriter()) {
                sendSseEvent(writer, "error", Map.of("message", "message is required", "errorCode", "missing_message"));
            }
            return;
        }

        // Tag the request for the search-log access-type column, same as v1.
        req.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE, fessConfig.getSystemProperty("rag.llm.name", "ollama"));

        try (final PrintWriter writer = res.getWriter()) {
            final String userId = getUserId(req);
            final ChatPhaseCallback phaseCallback = newPhaseCallback(writer);

            final ChatResult result;
            if (body.fields().isEmpty() && body.extraQueries().length == 0) {
                result = ComponentUtil.getChatClient().streamChatEnhanced(body.sessionId(), body.message(), userId, phaseCallback);
            } else {
                result = ComponentUtil.getChatClient()
                        .streamChatEnhanced(body.sessionId(), body.message(), userId, body.fields(), body.extraQueries(), phaseCallback);
            }

            final List<ChatSource> sources = result.getMessage().getSources();
            if (sources != null && !sources.isEmpty()) {
                sendSseEvent(writer, "sources", Map.of("sources", sources));
            }

            final Map<String, Object> doneData = new LinkedHashMap<>();
            doneData.put("sessionId", result.getSessionId());
            final String htmlContent = result.getMessage().getHtmlContent();
            if (htmlContent != null) {
                doneData.put("htmlContent", htmlContent);
            }
            sendSseEvent(writer, "done", doneData);
        } catch (final LlmException e) {
            // The callback already emitted onError to the SSE stream; do not double-send.
            logger.warn("[RAG] /api/v2/chat/stream LLM error. sessionId={}, errorCode={}", body.sessionId(), e.getErrorCode());
        } catch (final Exception e) {
            logger.warn("[RAG] /api/v2/chat/stream failed. error={}", e.getMessage(), e);
            if (!res.isCommitted()) {
                try (final PrintWriter writer = res.getWriter()) {
                    sendSseEvent(writer, "error", Map.of("message", "internal error", "errorCode", LlmException.ERROR_UNKNOWN));
                } catch (final Exception ioe) {
                    logger.warn("Failed to send SSE error after exception. cause={}", ioe.getMessage());
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
        res.setContentType("text/event-stream");
        res.setCharacterEncoding("UTF-8");
        res.setHeader("Cache-Control", "no-cache");
        res.setHeader("Connection", "keep-alive");
        res.setHeader("X-Accel-Buffering", "no");
    }

    /**
     * Builds a {@link ChatPhaseCallback} that emits SSE events using the same
     * wire shape v1 emits, so the static theme JS can share a single parser.
     *
     * @param writer the per-request servlet writer to emit events to
     * @return a callback bound to the writer
     */
    protected ChatPhaseCallback newPhaseCallback(final PrintWriter writer) {
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
                            data.put(k, v);
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
                emitSafely(writer, "error", Map.of("phase", phase, "message", errorCode, "errorCode", errorCode));
            }

            @Override
            public void onRetry(final String phase, final String operation, final int attempt, final int maxAttempts, final long sleepMs,
                    final String cause) {
                final Map<String, Object> data = new HashMap<>();
                data.put("phase", phase);
                data.put("operation", operation);
                data.put("attempt", attempt);
                data.put("maxAttempts", maxAttempts);
                data.put("sleepMs", sleepMs);
                putIfNotNull(data, "cause", cause);
                emitSafely(writer, "retry", data);
            }

            @Override
            public void onWaiting(final String phase, final String reason, final long elapsedMs, final long timeoutMs) {
                final Map<String, Object> data = new HashMap<>();
                data.put("phase", phase);
                data.put("reason", reason);
                data.put("elapsedMs", elapsedMs);
                data.put("timeoutMs", timeoutMs);
                emitSafely(writer, "waiting", data);
            }

            @Override
            public void onFallback(final String phase, final String reason, final String originalQuery, final String newQuery) {
                final Map<String, Object> data = new HashMap<>();
                data.put("phase", phase);
                data.put("reason", reason);
                putIfNotNull(data, "originalQuery", originalQuery);
                putIfNotNull(data, "newQuery", newQuery);
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
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to emit SSE event. event={}, error={}", event, e.getMessage());
            }
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

    /**
     * Mirrors v1 {@code ChatApiManager#getUserId}: prefer the authenticated
     * username, fall back to the cookie-bound userCode for guests.
     *
     * @param req the incoming HTTP request
     * @return the user identifier
     */
    protected String getUserId(final HttpServletRequest req) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final String username = systemHelper.getUsername();
        if (!Constants.GUEST_USER.equals(username)) {
            return username;
        }
        return ComponentUtil.getUserInfoHelper().getUserCode();
    }

    /**
     * Resolve {@code rag.chat.message.max.length} from fess_config system properties,
     * defaulting to 4000 on parse failure. Uses {@code getSystemProperty} to match the
     * pattern established by {@link ChatHandler}.
     *
     * @param fessConfig active Fess config
     * @return max chat message length in characters
     */
    protected int getMaxMessageLength(final FessConfig fessConfig) {
        try {
            return Integer.parseInt(fessConfig.getSystemProperty("rag.chat.message.max.length", "4000"));
        } catch (final NumberFormatException e) {
            return 4000;
        }
    }
}
