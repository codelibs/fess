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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.chat.ChatClient;
import org.codelibs.fess.chat.ChatClient.ChatResult;
import org.codelibs.fess.entity.ChatMessage.ChatSource;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles {@code POST /api/v2/chat} — non-streaming RAG chat.
 *
 * <p>Thin adapter: parses the v2 JSON body, validates the message length, then
 * delegates to {@link org.codelibs.fess.chat.ChatClient#chat}. The response is
 * wrapped in the v2 envelope as
 * {@code {response: {status:0, session_id, content, sources}}}.</p>
 *
 * <p>Session clearing has been moved to the dedicated DELETE endpoint
 * {@code /api/v2/chat/sessions/{session_id}} handled by
 * {@link ChatSessionClearHandler}.</p>
 *
 * <p>Anonymous users are supported the same way v1 supports them — the user is
 * identified by {@code UserInfoHelper#getUserCode()} when no logged-in bean is
 * present. See §Risks (3) in the plan for the auth-posture rationale.</p>
 */
public class ChatHandler {

    private static final Logger logger = LogManager.getLogger(ChatHandler.class);

    /**
     * Default constructor used by the DI container. The handler holds no
     * per-request state and is safe to share across concurrent requests.
     */
    public ChatHandler() {
        // no-op
    }

    /**
     * Max raw body bytes the handler will read. Chat messages are bounded by
     * {@code rag.chat.message.max.length} (default 4000) but we add a generous
     * buffer for filter arrays.
     */
    private static final int MAX_BODY_BYTES = 32 * 1024;

    /**
     * Processes one {@code POST /api/v2/chat} request.
     *
     * <p>Rejects non-{@code POST} methods with
     * {@link V2ErrorCode#METHOD_NOT_ALLOWED}. Validates the feature flag, parses
     * the JSON body, enforces per-user chat rate limiting, then delegates to
     * {@link org.codelibs.fess.chat.ChatClient#chat} and writes the result as a
     * v2 success envelope. Pre-call failures (parse errors, gate failures, DI
     * lookup failures) are reported as structured error envelopes.</p>
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @throws IOException if writing the envelope or reading the body fails
     */
    public void handle(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
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
        // Per-user chat rate limit. Pre-stream failure returns a proper 500 JSON envelope —
        // skipping rate limiting silently would be a security-affecting behavior, so we
        // match ChatStreamHandler and surface DI failures as INTERNAL_ERROR.
        final LoginRateLimiter limiter;
        try {
            limiter = ComponentUtil.getLoginRateLimiter();
        } catch (final RuntimeException e) {
            // Limiter DI not available (e.g. slim test harness); log and surface as 500.
            logger.warn("[RAG] /api/v2/chat rate-limit lookup failed", e);
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

        // Tag the request for the search-log access-type column, same as v1.
        req.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE, fessConfig.getSystemProperty("rag.llm.name", "ollama"));

        try {
            final ChatResult result;
            final ChatClient chatClient = getChatClient();
            if (body.fields().isEmpty() && body.extraQueries().length == 0) {
                result = chatClient.chat(body.sessionId(), body.message(), userId);
            } else {
                result = chatClient.chat(body.sessionId(), body.message(), userId, body.fields(), body.extraQueries());
            }

            final Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("session_id", result.getSessionId());
            payload.put("content", result.getMessage().getContent());
            final List<ChatSource> sources = result.getMessage().getSources();
            if (sources != null) {
                payload.put("sources", ComponentUtil.getChatApiHelper().toSourceMaps(sources));
            }
            ComponentUtil.getV2EnvelopeWriter().writeSuccess(res, payload);
        } catch (final Exception e) {
            logger.warn("[RAG] /api/v2/chat failed. error={}", e.getMessage(), e);
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INTERNAL_ERROR, "chat failed");
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
     * Resolves the RAG {@link ChatClient}. Exposed as a seam so unit tests can substitute a stub by
     * overriding this method rather than registering into the DI container — {@code chatClient} is
     * a named/smart-deploy component whose {@code ComponentUtil.register} fallback is order-sensitive
     * across the shared test container (same rationale as {@link #getUserId}).
     *
     * @return the chat client component (never null in production)
     */
    protected ChatClient getChatClient() {
        return ComponentUtil.getChatClient();
    }

}
