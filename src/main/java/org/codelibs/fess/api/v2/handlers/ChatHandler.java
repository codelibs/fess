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
import org.codelibs.fess.llm.LlmUsageCollector;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.log.exentity.ChatLog;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles {@code POST /api/v2/chat} — non-streaming RAG chat.
 *
 * <p>Thin adapter: parses the v2 JSON body, validates the message length, then
 * delegates to {@link org.codelibs.fess.chat.ChatClient#chat}, or to
 * {@link org.codelibs.fess.chat.ChatClient#chatAboutDocument} when the body carries a {@code doc_id}
 * (answering from that one document; {@code 404 not_found} when it is missing or not visible). The response is
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
        } catch (final ChatRequestBody.InvalidRequestException e) {
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
        // Throttle by a key an anonymous caller cannot rotate (see ChatApiHelper#resolveChatRateLimitKey);
        // the key is always non-blank so the throttle always applies. userId above stays for chat-session binding.
        final String rateLimitKey = getRateLimitKey(req);
        if (limiter != null && chatLimit > 0 && StringUtil.isNotBlank(rateLimitKey)
                && !limiter.allow(LoginRateLimiter.Scope.CHAT, rateLimitKey, chatLimit, 60)) {
            res.setHeader("Retry-After", "60");
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.RATE_LIMITED, "too many chat requests");
            return;
        }

        // A document chat needs a document the caller may see; check before any model work starts.
        final String docId = body.docId();
        if (docId != null) {
            try {
                if (!documentExists(docId)) {
                    ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.NOT_FOUND, "doc not found: " + docId);
                    return;
                }
            } catch (final RuntimeException e) {
                ComponentUtil.getV2EnvelopeWriter().writeInternalError(res, e, logger, "/api/v2/chat doc_id=" + docId);
                return;
            }
        }

        // Tag the request for the search-log access-type column, same as v1.
        final String llmName = fessConfig.getSystemProperty("rag.llm.name", "ollama");
        req.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE, llmName);

        // Usage of this request (LLM calls and tokens) for the chat log; never the question or the answer.
        final long startTime = System.currentTimeMillis();
        final ChatLog chatLog = ComponentUtil.getChatApiHelper()
                .createChatLog(ChatLog.ACCESS_TYPE_SYNC, docId != null, body.sessionId(), llmName, startTime);
        final LlmUsageCollector usage = LlmUsageCollector.start();
        ChatResult result = null;
        Exception failure = null;
        try {
            final ChatClient chatClient = getChatClient();
            if (docId != null) {
                // fields and extra_queries are ignored: the answer comes from this one document only.
                result = chatClient.chatAboutDocument(body.sessionId(), body.message(), userId, docId);
            } else if (body.fields().isEmpty() && body.extraQueries().length == 0) {
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
            if (result == null) {
                failure = e;
            }
            logger.warn("[RAG] /api/v2/chat failed. error={}", e.getMessage(), e);
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INTERNAL_ERROR, "chat failed");
        } finally {
            usage.close();
            recordChatLog(chatLog, result != null ? ChatLog.STATUS_SUCCESS : ChatLog.STATUS_ERROR, failure,
                    System.currentTimeMillis() - startTime, usage, result);
        }
    }

    /**
     * Records the usage of a chat request in the chat log. Exposed as a seam so unit tests can
     * capture what is recorded without a search engine.
     *
     * @param chatLog the chat log created before the chat client was called
     * @param status the outcome of the request
     * @param error the failure of the request (null unless it failed)
     * @param responseTime the time the request took, in milliseconds
     * @param usage the LLM usage collected for the request
     * @param result the chat result (null unless the request succeeded)
     */
    protected void recordChatLog(final ChatLog chatLog, final String status, final Throwable error, final long responseTime,
            final LlmUsageCollector usage, final ChatResult result) {
        ComponentUtil.getChatApiHelper().storeChatLog(chatLog, status, error, responseTime, usage, result);
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
     * Resolves the chat rate-limit key (server-validated username for authenticated callers, else the
     * proxy-aware client IP). Exposed as a seam so unit tests can pin the throttle key deterministically;
     * the underlying {@code SystemHelper}/{@code RateLimitHelper} are smart-deploy components that are
     * unreliable to stub via {@code ComponentUtil.register} once the shared test container has resolved
     * the real ones (same rationale as {@link #getUserId}).
     *
     * @param req the incoming HTTP request
     * @return the rate-limit key (never null/blank)
     */
    protected String getRateLimitKey(final HttpServletRequest req) {
        final String username = ComponentUtil.getSystemHelper().getUsername();
        return ComponentUtil.getChatApiHelper()
                .resolveChatRateLimitKey(username, () -> ComponentUtil.getRateLimitHelper().getClientIp(req));
    }

    /**
     * Checks that the document of a document chat exists and is visible to the caller. Exposed as a
     * seam so unit tests can decide the outcome without a search engine.
     *
     * @param docId the validated document id
     * @return true if the document exists and the caller may see it
     */
    protected boolean documentExists(final String docId) {
        return ComponentUtil.getChatApiHelper().existsDocument(docId);
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
