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
import org.codelibs.fess.api.v2.V2EnvelopeWriter;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.chat.ChatClient.ChatResult;
import org.codelibs.fess.entity.ChatMessage.ChatSource;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles {@code POST /api/v2/chat} — non-streaming RAG chat.
 *
 * <p>Thin adapter: parses the v2 JSON body, validates the message length, then
 * delegates to {@link org.codelibs.fess.chat.ChatClient#chat} or
 * {@link org.codelibs.fess.chat.ChatSessionManager#clearSession} for the {@code clear}
 * branch. The response is wrapped in the v2 envelope as
 * {@code {response: {status:0, version:"v2", session_id, content, sources}}}.</p>
 *
 * <p>Anonymous users are supported the same way v1 supports them — the user is
 * identified by {@code UserInfoHelper#getUserCode()} when no logged-in bean is
 * present. See §Risks (3) in the plan for the auth-posture rationale.</p>
 */
public class ChatHandler {

    private static final Logger logger = LogManager.getLogger(ChatHandler.class);

    /**
     * Max raw body bytes the handler will read. Chat messages are bounded by
     * {@code rag.chat.message.max.length} (default 4000) but we add a generous
     * buffer for filter arrays.
     */
    private static final int MAX_BODY_BYTES = 32 * 1024;

    public void handle(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
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

        final int maxLen = getMaxMessageLength(fessConfig);
        final ChatRequestBody body;
        try {
            body = ChatRequestBody.from(raw, maxLen);
        } catch (final ChatRequestBody.MessageTooLongException e) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, e.getMessage());
            return;
        }

        // Clear-session branch — same shape as v1's processChatRequest "clear" path.
        if (body.isClear()) {
            if (StringUtil.isBlank(body.sessionId())) {
                V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, "session_id is required to clear a session");
                return;
            }
            final String userId = getUserId(req);
            final boolean cleared = ComponentUtil.getChatSessionManager().clearSession(body.sessionId(), userId);
            if (!cleared) {
                V2EnvelopeWriter.writeError(res, V2ErrorCode.NOT_FOUND, "session not found");
                return;
            }
            final Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("session_id", body.sessionId());
            payload.put("cleared", true);
            V2EnvelopeWriter.writeSuccess(res, payload);
            return;
        }

        if (StringUtil.isBlank(body.message())) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, "message is required");
            return;
        }

        final String userId = getUserId(req);
        // Per-user chat rate limit. We resolve the limiter lazily so the slim test harness
        // (no DI binding for it) degrades gracefully.
        LoginRateLimiter limiter = null;
        try {
            limiter = ComponentUtil.getLoginRateLimiter();
        } catch (final RuntimeException e) {
            // Limiter DI not available; skip rate limiting rather than failing the request.
            // Production wires it via app.xml.
            logger.warn("LoginRateLimiter unavailable; skipping rate limit", e);
        }
        final int chatLimit = getChatRateLimitPerMinute(fessConfig);
        if (limiter != null && chatLimit > 0 && !limiter.allow(LoginRateLimiter.Scope.CHAT, userId, chatLimit, 60)) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.RATE_LIMITED, "too many chat requests");
            return;
        }

        // Tag the request for the search-log access-type column, same as v1.
        req.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE, fessConfig.getSystemProperty("rag.llm.name", "ollama"));

        try {
            final ChatResult result;
            if (body.fields().isEmpty() && body.extraQueries().length == 0) {
                result = ComponentUtil.getChatClient().chat(body.sessionId(), body.message(), userId);
            } else {
                result = ComponentUtil.getChatClient().chat(body.sessionId(), body.message(), userId, body.fields(), body.extraQueries());
            }

            final Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("session_id", result.getSessionId());
            payload.put("content", result.getMessage().getContent());
            final List<ChatSource> sources = result.getMessage().getSources();
            if (sources != null) {
                payload.put("sources", sources);
            }
            V2EnvelopeWriter.writeSuccess(res, payload);
        } catch (final Exception e) {
            logger.warn("[RAG] /api/v2/chat failed. error={}", e.getMessage(), e);
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INTERNAL_ERROR, "chat failed");
        }
    }

    /**
     * Mirrors v1 {@code ChatApiManager#getUserId}: prefer the authenticated
     * username, fall back to the cookie-bound userCode for guests. This keeps
     * /api/v2/chat usable for anonymous SPA visitors when login is not required.
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
     * Resolve {@code rag.chat.message.max.length} from fess_config system
     * properties, defaulting to 4000 on parse failure.
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

    /**
     * Resolve {@code api.v2.chat.rate.limit.per.user.per.minute} from fess_config system
     * properties, defaulting to 30 on parse failure. A return value &lt;= 0 disables the
     * rate limit entirely. The system-property indirection avoids regenerating the
     * LastaFlute-managed FessConfig accessors for a single value.
     *
     * @param fessConfig active Fess config
     * @return max chat requests per minute per user, or {@code <= 0} to disable
     */
    protected int getChatRateLimitPerMinute(final FessConfig fessConfig) {
        try {
            return Integer.parseInt(fessConfig.getSystemProperty("api.v2.chat.rate.limit.per.user.per.minute", "30"));
        } catch (final NumberFormatException e) {
            return 30;
        }
    }
}
