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
package org.codelibs.fess.helper;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.v2.handlers.ChatRequestBody;
import org.codelibs.fess.chat.ChatClient.ChatResult;
import org.codelibs.fess.entity.ChatMessage.ChatSource;
import org.codelibs.fess.entity.FacetQueryView;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.llm.LlmException;
import org.codelibs.fess.llm.LlmUsageCollector;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.log.exentity.ChatLog;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;

/**
 * Shared utilities for v2 chat API handlers.
 *
 * <p>Extracted common logic from {@link org.codelibs.fess.api.v2.handlers.ChatHandler}
 * and {@link org.codelibs.fess.api.v2.handlers.ChatStreamHandler} so it can be reused
 * across chat-related handlers without duplication. (Historically these utilities
 * were also used to mirror the v1 {@code ChatApiManager} behavior; the v1
 * implementation has since been extracted into the {@code fess-webapp-v1-api}
 * plugin.)</p>
 *
 * <p>This is a DI-managed singleton helper (registered as {@code chatApiHelper}); obtain it via
 * {@link org.codelibs.fess.util.ComponentUtil#getChatApiHelper()} and call the instance methods.
 * Helpers in Fess are singletons accessed through {@code ComponentUtil}, not static utility
 * classes.</p>
 */
public class ChatApiHelper {

    private static final Logger logger = LogManager.getLogger(ChatApiHelper.class);

    private static final Pattern SESSION_ID_PATTERN = Pattern.compile("^[A-Za-z0-9._-]+$");

    /** Matches the query ID parameter of a go URL. */
    private static final Pattern QUERY_ID_PATTERN = Pattern.compile("[?&]queryId=([^&#]+)");

    /**
     * Default constructor for ChatApiHelper.
     */
    public ChatApiHelper() {
        // Default constructor
    }

    /**
     * Parses the {@code fields} object (nested structure) or legacy {@code fields.label}
     * dotted-key from a raw request map into the map shape accepted by ChatClient.
     *
     * <p>The v2 wire format accepts either:</p>
     * <ul>
     *   <li>{@code "fields": {"label": "value"}} — nested object (preferred v2 form)</li>
     *   <li>{@code "fields": {"label": ["v1", "v2"]}} — nested object with array</li>
     * </ul>
     *
     * <p>Values that are not present in the {@link org.codelibs.fess.helper.LabelTypeHelper}
     * allowlist are dropped silently; rejected values are appended to {@code warnings}
     * under the key {@code "fields.label"}.</p>
     *
     * @param raw the raw request body map
     * @param warnings mutable map to collect rejected values (keyed by field name)
     * @return a map of validated field filters, or an empty map when none are present
     * @throws IOException if {@code fields.label} exceeds the configured maximum array size or per-element length
     */
    public Map<String, String[]> parseFieldFilters(final Map<String, Object> raw, final Map<String, List<String>> warnings)
            throws IOException {
        // Support nested fields object: {"fields": {"label": ... }}
        Object labelsRaw = null;
        final Object fieldsObj = raw.get("fields");
        if (fieldsObj instanceof final Map<?, ?> fieldsMap) {
            labelsRaw = fieldsMap.get("label");
        }
        // Fallback: legacy dotted-key "fields.label" (kept for backward compat during transition)
        if (labelsRaw == null) {
            labelsRaw = raw.get("fields.label");
        }

        if (labelsRaw == null) {
            return Collections.emptyMap();
        }

        final List<String> labelValues = toStringList(labelsRaw);
        final org.codelibs.fess.mylasta.direction.FessConfig cfg = ComponentUtil.getFessConfig();
        final int maxArraySize = cfg.getApiParamMaxArraySizeOrDefault();
        final int maxElementLen = cfg.getApiParamMaxLengthOrDefault();
        if (labelValues.size() > maxArraySize) {
            throw new ChatRequestBody.TooManyValuesException("fields.label exceeds the maximum number of values: " + maxArraySize);
        }
        for (final String v : labelValues) {
            if (v != null && v.length() > maxElementLen) {
                throw new ChatRequestBody.TooManyValuesException("fields.label element exceeds the maximum length: " + maxElementLen);
            }
        }
        if (labelValues.isEmpty()) {
            return Collections.emptyMap();
        }

        // Validate against the configured label type allowlist (same logic v1 uses).
        final Set<String> allowed = new HashSet<>();
        try {
            ComponentUtil.getLabelTypeHelper()
                    .getLabelTypeItemList(SearchRequestType.SEARCH, Locale.ROOT)
                    .stream()
                    .map(m -> m.get("value"))
                    .forEach(allowed::add);
        } catch (final Exception e) {
            // Helper unavailable in unit tests — degrade to empty allowlist.
            logger.warn("chat allowlist load failed for labels", e);
        }

        final List<String> valid = partitionAllowed(labelValues, allowed, "fields.label", warnings);
        if (valid.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<String, String[]> out = new HashMap<>();
        out.put("label", valid.toArray(new String[0]));
        return out;
    }

    /**
     * Parses the {@code extra_queries} array (or scalar) from the raw request map.
     *
     * <p>Values are validated against the {@link org.codelibs.fess.helper.ViewHelper}
     * facet-query allowlist. Rejected values are appended to {@code warnings} under
     * the key {@code "extra_queries"}.</p>
     *
     * @param raw the raw request body map
     * @param warnings mutable map to collect rejected values (keyed by field name)
     * @return array of validated extra query strings; empty array when none present
     * @throws IOException if {@code extra_queries} exceeds the configured maximum array size or per-element length
     */
    public String[] parseExtraQueries(final Map<String, Object> raw, final Map<String, List<String>> warnings) throws IOException {
        final Object exqRaw = raw.get("extra_queries");
        if (exqRaw == null) {
            return new String[0];
        }

        final List<String> values = toStringList(exqRaw);
        final org.codelibs.fess.mylasta.direction.FessConfig cfg2 = ComponentUtil.getFessConfig();
        final int maxArraySize2 = cfg2.getApiParamMaxArraySizeOrDefault();
        final int maxElementLen2 = cfg2.getApiParamMaxLengthOrDefault();
        if (values.size() > maxArraySize2) {
            throw new ChatRequestBody.TooManyValuesException("extra_queries exceeds the maximum number of values: " + maxArraySize2);
        }
        for (final String v : values) {
            if (v != null && v.length() > maxElementLen2) {
                throw new ChatRequestBody.TooManyValuesException("extra_queries element exceeds the maximum length: " + maxElementLen2);
            }
        }
        if (values.isEmpty()) {
            return new String[0];
        }

        final Set<String> allowed = new HashSet<>();
        try {
            for (final FacetQueryView view : ComponentUtil.getViewHelper().getFacetQueryViewList()) {
                allowed.addAll(view.getQueryMap().values());
            }
        } catch (final Exception e) {
            // Helper unavailable in unit tests — degrade to empty allowlist.
            logger.warn("chat allowlist load failed for extra_queries", e);
        }

        final List<String> valid = partitionAllowed(values, allowed, "extra_queries", warnings);
        return valid.toArray(new String[0]);
    }

    /**
     * Parses a raw v2 chat JSON body into a validated {@link ChatRequestBody}.
     *
     * @param raw the parsed JSON request body
     * @param maxMessageLength upper bound on the {@code message} length, in characters
     * @return a validated request body
     * @throws ChatRequestBody.InvalidSessionIdException if {@code session_id} exceeds 100 characters or contains invalid characters
     * @throws ChatRequestBody.MessageTooLongException if {@code message} exceeds {@code maxMessageLength}
     * @throws ChatRequestBody.TooManyValuesException if {@code extra_queries} or {@code fields.label} exceeds the count or per-element length limit
     * @throws ChatRequestBody.InvalidRequestException if {@code doc_id} is not a valid document id
     * @throws IOException if validation reports an unrecoverable error
     */
    public ChatRequestBody parseRequestBody(final Map<String, Object> raw, final int maxMessageLength) throws IOException {
        final String message = trimmedOrNull(raw.get("message"));
        final String sessionId = trimmedOrNull(raw.get("session_id"));
        if (sessionId != null) {
            if (sessionId.length() > 100) {
                throw new ChatRequestBody.InvalidSessionIdException("session_id exceeds the maximum length of 100");
            }
            if (!SESSION_ID_PATTERN.matcher(sessionId).matches()) {
                throw new ChatRequestBody.InvalidSessionIdException("session_id contains invalid characters");
            }
        }
        if (message != null && message.length() > maxMessageLength) {
            throw new ChatRequestBody.MessageTooLongException("message exceeds max length: " + message.length() + " > " + maxMessageLength);
        }
        final Map<String, List<String>> warnings = new HashMap<>();
        final Map<String, String[]> fields = parseFieldFilters(raw, warnings);
        final String[] extraQueries = parseExtraQueries(raw, warnings);
        final String docId = trimmedOrNull(raw.get("doc_id"));
        if (docId != null && !ComponentUtil.getV2DocIdValidator().isValid(docId)) {
            throw new ChatRequestBody.InvalidRequestException("invalid doc_id");
        }
        return new ChatRequestBody(message, sessionId, fields, extraQueries, docId, warnings);
    }

    /**
     * Checks that a document exists and is visible to the caller. The lookup is filtered by the
     * caller's roles, so a document the caller may not see is reported as missing.
     *
     * @param docId the validated document id
     * @return true if the document exists and the caller may see it
     */
    public boolean existsDocument(final String docId) {
        return ComponentUtil.getSearchHelper()
                .getDocumentByDocId(docId, new String[] { ComponentUtil.getFessConfig().getIndexFieldDocId() }, OptionalThing.empty())
                .isPresent();
    }

    /**
     * Converts chat sources to the v2 API response shape.
     *
     * @param sources list of chat sources to convert
     * @return list of snake_case maps, one per source
     */
    public List<Map<String, Object>> toSourceMaps(final List<ChatSource> sources) {
        final List<Map<String, Object>> result = new ArrayList<>(sources.size());
        for (final ChatSource src : sources) {
            final Map<String, Object> m = new LinkedHashMap<>();
            m.put("rank", src.getIndex());
            putIfNotNull(m, "title", src.getTitle());
            putIfNotNull(m, "url", src.getUrl());
            putIfNotNull(m, "doc_id", src.getDocId());
            putIfNotNull(m, "snippet", src.getSnippet());
            putIfNotNull(m, "url_link", src.getUrlLink());
            putIfNotNull(m, "go_url", src.getGoUrl());
            result.add(m);
        }
        return result;
    }

    /**
     * Coerces a raw request value into a list of strings: each element of a {@link List} is
     * converted via {@code toString()} (skipping nulls); any other non-null value yields a
     * single-element list. A {@code null} input yields an empty list.
     *
     * @param raw the raw value (a {@code List}, a scalar, or {@code null})
     * @return the coerced string values (never {@code null})
     */
    private List<String> toStringList(final Object raw) {
        final List<String> values = new ArrayList<>();
        if (raw instanceof final List<?> l) {
            for (final Object o : l) {
                if (o != null) {
                    values.add(o.toString());
                }
            }
        } else if (raw != null) {
            values.add(raw.toString());
        }
        return values;
    }

    private String trimmedOrNull(final Object v) {
        if (v == null) {
            return null;
        }
        final String s = v.toString().trim();
        return s.isEmpty() ? null : s;
    }

    private void putIfNotNull(final Map<String, Object> map, final String key, final Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    /**
     * Partitions {@code values} against an allowlist, returning the accepted values and recording
     * rejected ones under {@code warningKey} in {@code warnings} (only when non-empty).
     *
     * @param values the candidate values
     * @param allowed the set of permitted values
     * @param warningKey the key under which rejected values are recorded
     * @param warnings mutable map collecting rejected values
     * @return the values present in {@code allowed} (never {@code null})
     */
    private List<String> partitionAllowed(final List<String> values, final Set<String> allowed, final String warningKey,
            final Map<String, List<String>> warnings) {
        final List<String> valid = new ArrayList<>();
        final List<String> rejected = new ArrayList<>();
        for (final String v : values) {
            if (v != null && allowed.contains(v)) {
                valid.add(v);
            } else if (v != null) {
                rejected.add(v);
            }
        }
        if (!rejected.isEmpty()) {
            warnings.put(warningKey, Collections.unmodifiableList(rejected));
        }
        return valid;
    }

    /**
     * Resolves the effective user identifier for a chat request.
     *
     * <p>Mirrors the logic in v1 {@code ChatApiManager#getUserId}: prefer the
     * authenticated username, fall back to the cookie-bound userCode for guests.
     * This keeps chat usable for anonymous SPA visitors when login is not required.</p>
     *
     * @return the user identifier (never null)
     */
    public String getUserId() {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        return resolveUserId(systemHelper.getUsername(), () -> ComponentUtil.getUserInfoHelper().getUserCode());
    }

    /**
     * Pure resolution logic behind {@link #getUserId()}, separated so the
     * branching can be unit-tested without the DI container. Prefers a non-guest username;
     * otherwise falls back to the guest user-code provided by the supplier (evaluated lazily so
     * the cookie-bound code is only resolved for guest visitors).
     *
     * @param username the authenticated username (may be {@link Constants#GUEST_USER})
     * @param guestUserCodeSupplier supplies the fallback user-code for guest visitors
     * @return the effective user identifier (never null)
     */
    String resolveUserId(final String username, final java.util.function.Supplier<String> guestUserCodeSupplier) {
        if (!Constants.GUEST_USER.equals(username)) {
            return username;
        }
        return guestUserCodeSupplier.get();
    }

    /**
     * Resolves a rate-limit key for the chat endpoints that an anonymous caller cannot rotate.
     *
     * <p>Unlike {@link #getUserId()} (which keys chat-session continuity and intentionally falls
     * back to the cookie-bound, client-supplied userCode for guests), this key must be robust
     * against abuse: the guest userCode is only validated by regex/length, so a malicious client
     * could forge and rotate it per request to obtain a fresh throttle bucket and bypass the limit.
     * Authenticated (non-guest) callers are keyed by their server-validated username
     * ({@code "u:"+username}); all other callers (guest / anonymous / forged / rotated / blank
     * userCode) are keyed by the proxy-aware client IP ({@code "ip:"+clientIp}), which an anonymous
     * attacker cannot freely rotate. The client-IP supplier is evaluated lazily so the IP is only
     * resolved for guest visitors.</p>
     *
     * <p>The returned key is namespaced ({@code "u:"} for usernames, {@code "ip:"} for client IPs)
     * so the two key spaces never collide, and is always non-blank so the chat throttle always
     * applies.</p>
     *
     * <p>The client IP is resolved by the caller (the v2 chat handlers, via
     * {@code RateLimitHelper#getClientIp}) rather than here, so this helper carries no servlet-API
     * dependency and stays loadable by non-web Fess processes (e.g. the crawler) whose DI container
     * also instantiates it. Behind a reverse proxy the per-IP key is only as trustworthy as the
     * proxy setup: {@code rate.limit.trusted.proxies} gates which proxies' {@code X-Forwarded-For} /
     * {@code X-Real-IP} headers are honored, so the fronting proxy must be trusted and must sanitize
     * any inbound forwarded headers for the per-IP key to resist rotation.</p>
     *
     * @param username the authenticated username (may be {@link Constants#GUEST_USER})
     * @param clientIpSupplier supplies the proxy-aware client IP for guest visitors
     * @return the namespaced chat rate-limit key (never null/blank)
     */
    public String resolveChatRateLimitKey(final String username, final java.util.function.Supplier<String> clientIpSupplier) {
        if (!Constants.GUEST_USER.equals(username)) {
            return "u:" + username;
        }
        return "ip:" + clientIpSupplier.get();
    }

    /**
     * Resolves {@code rag.chat.message.max.length}, defaulting to {@code 4000} when it is
     * unset or unparseable.
     *
     * <p>This is a system property: it is read from {@code conf/system.properties}, then
     * {@code -Dfess.system.rag.chat.message.max.length}. It is deliberately not declared in
     * {@code fess_config.properties}, which this channel never consults.
     *
     * @param fessConfig active Fess config
     * @return max chat message length in characters
     */
    public int getMaxMessageLength(final FessConfig fessConfig) {
        try {
            return Integer.parseInt(fessConfig.getSystemProperty("rag.chat.message.max.length", "4000"));
        } catch (final NumberFormatException e) {
            return 4000;
        }
    }

    /**
     * Creates the chat log of a chat request that is about to call the chat client. Only the
     * metadata of the request is recorded, never the question or the answer.
     *
     * @param accessType {@link ChatLog#ACCESS_TYPE_SYNC} or {@link ChatLog#ACCESS_TYPE_STREAM}
     * @param documentChat true for a chat about one document
     * @param sessionId the chat session ID of the request (null for a new session)
     * @param llmName the name of the configured LLM ({@code rag.llm.name})
     * @param requestedTime the time the chat client is called, in epoch milliseconds
     * @return the chat log with the request time set
     */
    public ChatLog createChatLog(final String accessType, final boolean documentChat, final String sessionId, final String llmName,
            final long requestedTime) {
        final ChatLog chatLog = new ChatLog();
        chatLog.setRequestedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(requestedTime), ZoneId.systemDefault()));
        chatLog.setAccessType(accessType);
        chatLog.setChatType(documentChat ? ChatLog.CHAT_TYPE_DOCUMENT : ChatLog.CHAT_TYPE_CHAT);
        chatLog.setChatSessionId(sessionId);
        chatLog.setLlmName(llmName);
        return chatLog;
    }

    /**
     * Completes a chat log with the outcome of the request and the LLM usage, and queues it for
     * storing. Does nothing when {@code rag.chat.log.enabled} is false. Never throws: a failure is
     * only logged, so recording the usage cannot break a chat response. Must be called on the
     * request thread, which resolves the user.
     *
     * @param chatLog the chat log created by {@link #createChatLog(String, boolean, String, String, long)}
     * @param status {@link ChatLog#STATUS_SUCCESS}, {@link ChatLog#STATUS_ERROR} or {@link ChatLog#STATUS_CANCELLED}
     * @param error the failure of the request (null unless it failed)
     * @param responseTime the time the request took, in milliseconds
     * @param usage the LLM usage collected for the request (may be null)
     * @param result the chat result (null unless the request succeeded)
     */
    public void storeChatLog(final ChatLog chatLog, final String status, final Throwable error, final long responseTime,
            final LlmUsageCollector usage, final ChatResult result) {
        try {
            if (!ComponentUtil.getFessConfig().isRagChatLogEnabled()) {
                return;
            }
            chatLog.setStatus(status);
            if (error != null) {
                chatLog.setErrorCode(error instanceof final LlmException e ? e.getErrorCode() : LlmException.ERROR_UNKNOWN);
            }
            chatLog.setResponseTime(responseTime);
            if (usage != null) {
                chatLog.setLlmCalls(usage.getLlmCalls());
                chatLog.setPromptTokens(usage.getPromptTokens());
                chatLog.setCompletionTokens(usage.getCompletionTokens());
                chatLog.setTotalTokens(usage.getTotalTokens());
                chatLog.setModel(usage.getModel());
                chatLog.setIntent(usage.getIntent());
            }
            if (result != null) {
                chatLog.setChatSessionId(result.getSessionId());
                final List<ChatSource> sources = result.getMessage() != null ? result.getMessage().getSources() : null;
                chatLog.setSourceCount(sources != null ? sources.size() : 0);
                chatLog.setSearchQueryId(extractSearchQueryId(sources));
            }
            ComponentUtil.getSearchLogHelper().addChatLog(chatLog);
        } catch (final Exception e) {
            logger.warn("Failed to record the chat log. chatSessionId={}, status={}", chatLog.getChatSessionId(), status, e);
        }
    }

    /**
     * Extracts the ID of the search the sources of an answer came from, which is the query ID of
     * the search log entry of that search. The sources carry it in their go URLs.
     *
     * @param sources the sources of the answer (may be null)
     * @return the query ID, or null if no source has one
     */
    public String extractSearchQueryId(final List<ChatSource> sources) {
        if (sources == null) {
            return null;
        }
        for (final ChatSource source : sources) {
            final String goUrl = source != null ? source.getGoUrl() : null;
            if (goUrl == null) {
                continue;
            }
            final Matcher matcher = QUERY_ID_PATTERN.matcher(goUrl);
            if (matcher.find()) {
                return URLDecoder.decode(matcher.group(1), StandardCharsets.UTF_8);
            }
        }
        return null;
    }

}
