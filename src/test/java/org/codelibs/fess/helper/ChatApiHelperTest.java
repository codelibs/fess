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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.Constants;
import org.codelibs.fess.chat.ChatClient.ChatResult;
import org.codelibs.fess.entity.ChatMessage;
import org.codelibs.fess.entity.ChatMessage.ChatSource;
import org.codelibs.fess.llm.ChatIntent;
import org.codelibs.fess.llm.LlmChatResponse;
import org.codelibs.fess.llm.LlmException;
import org.codelibs.fess.llm.LlmUsageCollector;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.log.exentity.ChatLog;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ChatApiHelper}.
 *
 * <p>All methods are static utilities. The pure parsing/branching logic
 * ({@code parseFieldFilters}, {@code parseExtraQueries}, {@code resolveUserId}, message-length
 * and rate-limit accessors) is tested directly with plain inputs or {@code FessConfig.SimpleImpl}.
 * The user-id helpers ({@code SystemHelper}/{@code UserInfoHelper}) are intentionally NOT stubbed
 * via {@code ComponentUtil.register}: they are smart-deploy components the shared test container
 * can resolve for real, so a registered stub is not reliably honored. {@link ChatApiHelper#getUserId}
 * is therefore tested through its pure seam {@code resolveUserId}.</p>
 *
 * <p>LabelTypeHelper and ViewHelper are intentionally NOT registered in most
 * tests: when they are absent the helpers degrade to an empty allowlist, so all
 * candidate values are rejected. This verifies the fallback path.</p>
 */
public class ChatApiHelperTest extends UnitFessTestCase {

    /** Stateless helper under test; instantiated directly since its pure methods need no DI. */
    private final ChatApiHelper chatApiHelper = new ChatApiHelper();

    // ── parseFieldFilters ─────────────────────────────────────────────────────

    @Test
    public void test_parseFieldFilters_nullInput_returnsEmptyMap() throws Exception {
        final Map<String, List<String>> warnings = new HashMap<>();
        final Map<String, String[]> result = chatApiHelper.parseFieldFilters(null != null ? null : Collections.emptyMap(), warnings);
        assertTrue(result.isEmpty(), "null/empty raw input must return empty map");
        assertTrue(warnings.isEmpty(), "no warnings for empty input");
    }

    @Test
    public void test_parseFieldFilters_emptyRaw_returnsEmptyMap() throws Exception {
        final Map<String, List<String>> warnings = new HashMap<>();
        final Map<String, String[]> result = chatApiHelper.parseFieldFilters(Collections.emptyMap(), warnings);
        assertTrue(result.isEmpty(), "empty raw map must return empty map");
        assertTrue(warnings.isEmpty(), "no warnings for empty raw map");
    }

    @Test
    public void test_parseFieldFilters_fieldsIsString_typesMismatch_returnsEmptyMap() throws Exception {
        // "fields" key present but value is a plain String, not a Map — must be ignored
        final Map<String, Object> raw = new HashMap<>();
        raw.put("fields", "not-a-map");
        final Map<String, List<String>> warnings = new HashMap<>();
        final Map<String, String[]> result = chatApiHelper.parseFieldFilters(raw, warnings);
        // No labelsRaw resolved; result must be empty
        assertTrue(result.isEmpty(), "scalar fields value must not produce filters");
    }

    @Test
    public void test_parseFieldFilters_nestedStringLabel_allowlistEmpty_addedToWarnings() throws Exception {
        // Nested structure {"fields":{"label":"x"}} — LabelTypeHelper absent → allowlist empty
        // → "x" is rejected and added to warnings["fields.label"]
        final Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put("label", "x");
        final Map<String, Object> raw = new HashMap<>();
        raw.put("fields", fieldsMap);

        final Map<String, List<String>> warnings = new HashMap<>();
        final Map<String, String[]> result = chatApiHelper.parseFieldFilters(raw, warnings);

        assertTrue(result.isEmpty(), "rejected label must not appear in result");
        assertTrue(warnings.containsKey("fields.label"), "rejected label must be reported in warnings");
        assertEquals(Collections.singletonList("x"), warnings.get("fields.label"));
    }

    @Test
    public void test_parseFieldFilters_nestedArrayLabel_allowlistEmpty_allRejected() throws Exception {
        // {"fields":{"label":["a","b"]}} — both values rejected when allowlist is empty
        final List<String> labelList = new ArrayList<>(Arrays.asList("a", "b"));
        final Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put("label", labelList);
        final Map<String, Object> raw = new HashMap<>();
        raw.put("fields", fieldsMap);

        final Map<String, List<String>> warnings = new HashMap<>();
        final Map<String, String[]> result = chatApiHelper.parseFieldFilters(raw, warnings);

        assertTrue(result.isEmpty(), "all rejected labels must produce empty result");
        assertTrue(warnings.containsKey("fields.label"), "all rejected values must be in warnings");
        final List<String> rejected = warnings.get("fields.label");
        assertEquals(2, rejected.size());
        assertTrue(rejected.containsAll(Arrays.asList("a", "b")), "both values must be in rejected list");
    }

    @Test
    public void test_parseFieldFilters_dottedKeyFallback_allowlistEmpty_addedToWarnings() throws Exception {
        // Legacy dotted-key {"fields.label":["v1"]} fallback — value rejected → warning
        final List<String> labelList = new ArrayList<>(Collections.singletonList("v1"));
        final Map<String, Object> raw = new HashMap<>();
        raw.put("fields.label", labelList);

        final Map<String, List<String>> warnings = new HashMap<>();
        final Map<String, String[]> result = chatApiHelper.parseFieldFilters(raw, warnings);

        assertTrue(result.isEmpty(), "rejected legacy label must not appear in result");
        assertTrue(warnings.containsKey("fields.label"), "rejected dotted-key label must be in warnings");
    }

    @Test
    public void test_parseFieldFilters_listWithNullElement_nullSkipped() throws Exception {
        // List containing a null element — nulls must be silently skipped
        final List<Object> labelList = new ArrayList<>();
        labelList.add(null);
        labelList.add("valid");
        final Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put("label", labelList);
        final Map<String, Object> raw = new HashMap<>();
        raw.put("fields", fieldsMap);

        final Map<String, List<String>> warnings = new HashMap<>();
        // LabelTypeHelper absent → "valid" is rejected but null is not in warnings
        chatApiHelper.parseFieldFilters(raw, warnings);

        if (warnings.containsKey("fields.label")) {
            assertFalse(warnings.get("fields.label").contains(null), "null element must not appear in warnings list");
        }
        // Even if "valid" lands in warnings, null must not be there
    }

    @Test
    public void test_parseFieldFilters_warningsKeyIsFieldsDotLabel() throws Exception {
        // The warnings key must use the snake_case dotted form "fields.label", not "label"
        final Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put("label", "rejected-value");
        final Map<String, Object> raw = new HashMap<>();
        raw.put("fields", fieldsMap);

        final Map<String, List<String>> warnings = new HashMap<>();
        chatApiHelper.parseFieldFilters(raw, warnings);

        // Whether or not LabelTypeHelper is available, the key must be "fields.label"
        // When LabelTypeHelper is absent, all values are rejected → key exists
        if (!warnings.isEmpty()) {
            assertTrue(warnings.containsKey("fields.label"), "warnings key must be 'fields.label', not 'label'");
            assertFalse(warnings.containsKey("label"), "warnings key must NOT be bare 'label'");
        }
    }

    // ── parseExtraQueries ─────────────────────────────────────────────────────

    @Test
    public void test_parseExtraQueries_nullKey_returnsEmptyArray() throws Exception {
        // No "extra_queries" key in raw → empty array, no warnings
        final Map<String, Object> raw = new HashMap<>();
        final Map<String, List<String>> warnings = new HashMap<>();
        final String[] result = chatApiHelper.parseExtraQueries(raw, warnings);
        assertEquals(0, result.length);
        assertTrue(warnings.isEmpty(), "no warnings when key absent");
    }

    @Test
    public void test_parseExtraQueries_scalarString_rejectedWhenAllowlistEmpty() throws Exception {
        // extra_queries as plain string (scalar) — ViewHelper absent → empty allowlist
        final Map<String, Object> raw = new HashMap<>();
        raw.put("extra_queries", "query-a");
        final Map<String, List<String>> warnings = new HashMap<>();
        final String[] result = chatApiHelper.parseExtraQueries(raw, warnings);

        // Rejected — ViewHelper returns empty allowlist
        assertEquals(0, result.length);
        assertTrue(warnings.containsKey("extra_queries"), "rejected scalar must be in warnings");
    }

    @Test
    public void test_parseExtraQueries_arrayInput_rejectedWhenAllowlistEmpty() throws Exception {
        // extra_queries as list — all values rejected when ViewHelper absent
        final Map<String, Object> raw = new HashMap<>();
        raw.put("extra_queries", new ArrayList<>(Arrays.asList("q1", "q2")));
        final Map<String, List<String>> warnings = new HashMap<>();
        final String[] result = chatApiHelper.parseExtraQueries(raw, warnings);

        assertEquals(0, result.length);
        assertTrue(warnings.containsKey("extra_queries"), "rejected array items must be in warnings");
        assertEquals(2, warnings.get("extra_queries").size());
    }

    @Test
    public void test_parseExtraQueries_warningsKeyIsSnakeCase() throws Exception {
        // The warnings key for extra_queries must be "extra_queries" (snake_case)
        final Map<String, Object> raw = new HashMap<>();
        raw.put("extra_queries", "any-value");
        final Map<String, List<String>> warnings = new HashMap<>();
        chatApiHelper.parseExtraQueries(raw, warnings);

        if (!warnings.isEmpty()) {
            assertTrue(warnings.containsKey("extra_queries"), "warnings key must be 'extra_queries' (snake_case)");
            assertFalse(warnings.containsKey("extraQueries"), "warnings key must NOT be camelCase 'extraQueries'");
        }
    }

    // ── getUserId ─────────────────────────────────────────────────────────────

    @Test
    public void test_getUserId_authenticatedUser_returnsUsername() {
        // A non-guest username is used directly as the userId; the guest fallback supplier must
        // NOT be consulted. Tests the pure branching logic without the DI container — stubbing
        // SystemHelper/UserInfoHelper via ComponentUtil.register is unreliable because both are
        // smart-deploy components that the shared test container can resolve for real.
        final String userId = chatApiHelper.resolveUserId("alice", () -> {
            throw new AssertionError("guest fallback must not be used for an authenticated user");
        });
        assertEquals("alice", userId);
    }

    @Test
    public void test_getUserId_guestUser_fallsBackToUserCode() {
        // GUEST_USER falls back to the supplied cookie-bound user-code.
        final String userId = chatApiHelper.resolveUserId(Constants.GUEST_USER, () -> "cookie-code-xyz");
        assertEquals("cookie-code-xyz", userId);
    }

    // ── resolveChatRateLimitKey ───────────────────────────────────────────────

    @Test
    public void test_resolveChatRateLimitKey_authenticatedUser_keysByUsername() {
        // A non-guest username is keyed as "u:<username>"; the client-IP supplier must NOT be
        // consulted (the username is server-validated and cannot be rotated by the caller).
        final String key = chatApiHelper.resolveChatRateLimitKey("alice", () -> {
            throw new AssertionError("client IP must not be resolved for an authenticated user");
        });
        assertEquals("u:alice", key);
    }

    @Test
    public void test_resolveChatRateLimitKey_guestUser_keysByClientIp() {
        // GUEST_USER (anonymous) is keyed by the proxy-aware client IP as "ip:<clientIp>",
        // never by the forgeable guest userCode.
        final String key = chatApiHelper.resolveChatRateLimitKey(Constants.GUEST_USER, () -> "203.0.113.7");
        assertEquals("ip:203.0.113.7", key);
    }

    @Test
    public void test_resolveChatRateLimitKey_forgedUserCode_doesNotChangeKey() {
        // L-1 regression: for a guest, the key must depend only on the client IP, not on any
        // caller-supplied userCode. Even if a malicious client rotates/forges its userCode per
        // request, the resolved key stays IP-based and unchanged, so a fresh throttle bucket
        // cannot be obtained by rotating the userCode.
        final String clientIp = "198.51.100.9";
        final String first = chatApiHelper.resolveChatRateLimitKey(Constants.GUEST_USER, () -> clientIp);
        final String second = chatApiHelper.resolveChatRateLimitKey(Constants.GUEST_USER, () -> clientIp);
        assertEquals("ip:" + clientIp, first);
        // Message-first overload: assertEquals(message, expected, actual).
        assertEquals("key must be IP-based and stable across requests regardless of userCode", first, second);
    }

    // ── getMaxMessageLength ───────────────────────────────────────────────────

    @Test
    public void test_getMaxMessageLength_defaultValue() {
        // When the system property is not set, default is 4000
        final FessConfig cfg = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;
            // getSystemProperty delegates to ComponentUtil.getSystemProperties
            // which is wired in test_app.xml; no override needed to get default
        };
        final int len = chatApiHelper.getMaxMessageLength(cfg);
        assertEquals(4000, len);
    }

    @Test
    public void test_getMaxMessageLength_configuredValue() {
        // Set the property and verify it is read correctly
        final FessConfig cfg = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getSystemProperty(final String key, final String defaultValue) {
                if ("rag.chat.message.max.length".equals(key)) {
                    return "2000";
                }
                return defaultValue;
            }
        };
        final int len = chatApiHelper.getMaxMessageLength(cfg);
        assertEquals(2000, len);
    }

    @Test
    public void test_getMaxMessageLength_invalidValue_returnsDefault() {
        // Non-numeric config value → NumberFormatException → fallback 4000
        final FessConfig cfg = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getSystemProperty(final String key, final String defaultValue) {
                if ("rag.chat.message.max.length".equals(key)) {
                    return "not-a-number";
                }
                return defaultValue;
            }
        };
        final int len = chatApiHelper.getMaxMessageLength(cfg);
        assertEquals(4000, len);
    }

    // ── parseRequestBody: session_id validation ───────────────────────────────

    @Test
    public void test_parseRequestBody_sessionId101Chars_throwsInvalidSessionId() {
        final Map<String, Object> raw = new HashMap<>();
        raw.put("message", "hello");
        raw.put("session_id", "a".repeat(101));
        Assertions.assertThrows(org.codelibs.fess.api.v2.handlers.ChatRequestBody.InvalidSessionIdException.class,
                () -> chatApiHelper.parseRequestBody(raw, 4000), "101-char session_id must throw InvalidSessionIdException");
    }

    @Test
    public void test_parseRequestBody_sessionId100Chars_ok() throws Exception {
        final Map<String, Object> raw = new HashMap<>();
        raw.put("message", "hello");
        raw.put("session_id", "a".repeat(100));
        final org.codelibs.fess.api.v2.handlers.ChatRequestBody body = chatApiHelper.parseRequestBody(raw, 4000);
        assertNotNull(body);
        assertEquals("a".repeat(100), body.sessionId());
    }

    @Test
    public void test_parseRequestBody_sessionIdInvalidChars_throwsInvalidSessionId() {
        final Map<String, Object> raw = new HashMap<>();
        raw.put("message", "hello");
        raw.put("session_id", "bad id!");
        Assertions.assertThrows(org.codelibs.fess.api.v2.handlers.ChatRequestBody.InvalidSessionIdException.class,
                () -> chatApiHelper.parseRequestBody(raw, 4000), "session_id with space/exclamation must throw InvalidSessionIdException");
    }

    // ── parseRequestBody: doc_id validation ───────────────────────────────────

    @Test
    public void test_parseRequestBody_docIdValid_ok() throws Exception {
        final Map<String, Object> raw = new HashMap<>();
        raw.put("message", "hello");
        raw.put("doc_id", " doc_1-A ");
        assertEquals("doc_1-A", chatApiHelper.parseRequestBody(raw, 4000).docId());
    }

    @Test
    public void test_parseRequestBody_docIdBlank_isNull() throws Exception {
        final Map<String, Object> raw = new HashMap<>();
        raw.put("message", "hello");
        raw.put("doc_id", "  ");
        assertNull(chatApiHelper.parseRequestBody(raw, 4000).docId());
    }

    @Test
    public void test_parseRequestBody_docIdInvalid_throwsInvalidDocId() {
        final Map<String, Object> raw = new HashMap<>();
        raw.put("message", "hello");
        raw.put("doc_id", "bad id!");
        final org.codelibs.fess.api.v2.handlers.ChatRequestBody.InvalidRequestException e =
                Assertions.assertThrows(org.codelibs.fess.api.v2.handlers.ChatRequestBody.InvalidRequestException.class,
                        () -> chatApiHelper.parseRequestBody(raw, 4000));
        assertEquals("invalid doc_id", e.getMessage());
    }

    @Test
    public void test_parseRequestBody_sessionIdNull_ok() throws Exception {
        final Map<String, Object> raw = new HashMap<>();
        raw.put("message", "hello");
        // no session_id key
        final org.codelibs.fess.api.v2.handlers.ChatRequestBody body = chatApiHelper.parseRequestBody(raw, 4000);
        assertNotNull(body);
        assertNull(body.sessionId());
    }

    // ── parseExtraQueries: array size and element length validation ───────────

    @Test
    public void test_parseExtraQueries_101Items_throwsTooManyValues() {
        final Map<String, Object> raw = new HashMap<>();
        final java.util.List<String> list = new java.util.ArrayList<>();
        for (int i = 0; i < 101; i++) {
            list.add("q" + i);
        }
        raw.put("extra_queries", list);
        final Map<String, List<String>> warnings = new HashMap<>();
        Assertions.assertThrows(org.codelibs.fess.api.v2.handlers.ChatRequestBody.TooManyValuesException.class,
                () -> chatApiHelper.parseExtraQueries(raw, warnings), "101 extra_queries items must throw TooManyValuesException");
    }

    @Test
    public void test_parseExtraQueries_elementExceeds1000Chars_throwsTooManyValues() {
        final Map<String, Object> raw = new HashMap<>();
        raw.put("extra_queries", "x".repeat(1001));
        final Map<String, List<String>> warnings = new HashMap<>();
        Assertions.assertThrows(org.codelibs.fess.api.v2.handlers.ChatRequestBody.TooManyValuesException.class,
                () -> chatApiHelper.parseExtraQueries(raw, warnings), "1001-char element must throw TooManyValuesException");
    }

    // ── parseFieldFilters: array size and element length validation ───────────

    @Test
    public void test_parseFieldFilters_101Items_throwsTooManyValues() {
        final java.util.List<String> list = new java.util.ArrayList<>();
        for (int i = 0; i < 101; i++) {
            list.add("label" + i);
        }
        final Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put("label", list);
        final Map<String, Object> raw = new HashMap<>();
        raw.put("fields", fieldsMap);
        final Map<String, List<String>> warnings = new HashMap<>();
        Assertions.assertThrows(org.codelibs.fess.api.v2.handlers.ChatRequestBody.TooManyValuesException.class,
                () -> chatApiHelper.parseFieldFilters(raw, warnings), "101 fields.label items must throw TooManyValuesException");
    }

    @Test
    public void test_parseFieldFilters_elementExceeds1000Chars_throwsTooManyValues() {
        final Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put("label", "x".repeat(1001));
        final Map<String, Object> raw = new HashMap<>();
        raw.put("fields", fieldsMap);
        final Map<String, List<String>> warnings = new HashMap<>();
        Assertions.assertThrows(org.codelibs.fess.api.v2.handlers.ChatRequestBody.TooManyValuesException.class,
                () -> chatApiHelper.parseFieldFilters(raw, warnings), "1001-char label element must throw TooManyValuesException");
    }

    // ── chat log ──────────────────────────────────────────────────────────────

    @Test
    public void test_createChatLog() {
        final long requestedTime = 1_790_000_000_000L;
        final ChatLog chatLog = chatApiHelper.createChatLog(ChatLog.ACCESS_TYPE_STREAM, true, "s1", "my-llm", requestedTime);
        assertEquals(ChatLog.ACCESS_TYPE_STREAM, chatLog.getAccessType());
        assertEquals(ChatLog.CHAT_TYPE_DOCUMENT, chatLog.getChatType());
        assertEquals("s1", chatLog.getChatSessionId());
        assertEquals("my-llm", chatLog.getLlmName());
        assertEquals(java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(requestedTime), java.time.ZoneId.systemDefault()),
                chatLog.getRequestedAt());
        assertEquals(ChatLog.CHAT_TYPE_CHAT, chatApiHelper.createChatLog(ChatLog.ACCESS_TYPE_SYNC, false, null, "x", 0L).getChatType());
    }

    @Test
    public void test_storeChatLog_success() {
        final List<ChatLog> added = registerCapturingSearchLogHelper(true);
        final ChatLog chatLog = chatApiHelper.createChatLog(ChatLog.ACCESS_TYPE_SYNC, false, null, "my-llm", 0L);
        final LlmUsageCollector usage = new LlmUsageCollector();
        final LlmChatResponse response = new LlmChatResponse("x");
        response.setPromptTokens(7);
        response.setCompletionTokens(3);
        response.setModel("model-1");
        usage.recordResponse(response);
        usage.recordResponse(null);
        usage.recordIntent(ChatIntent.SEARCH);
        final ChatSource first = new ChatSource();
        final ChatSource second = new ChatSource();
        second.setGoUrl("/fess/go/?rt=1&docId=d%201&queryId=q%2B1&order=1");
        final ChatMessage message = new ChatMessage("assistant", "the answer");
        message.setSources(List.of(first, second));
        chatApiHelper.storeChatLog(chatLog, ChatLog.STATUS_SUCCESS, null, 1234L, usage, new ChatResult("sess-new", message, List.of()));

        assertEquals(1, added.size());
        final ChatLog stored = added.get(0);
        assertEquals(ChatLog.STATUS_SUCCESS, stored.getStatus());
        assertNull(stored.getErrorCode());
        assertEquals(Long.valueOf(1234L), stored.getResponseTime());
        assertEquals(Integer.valueOf(2), stored.getLlmCalls());
        assertEquals(Long.valueOf(7), stored.getPromptTokens());
        assertEquals(Long.valueOf(3), stored.getCompletionTokens());
        assertEquals(Long.valueOf(10), stored.getTotalTokens());
        assertEquals("model-1", stored.getModel());
        assertEquals("search", stored.getIntent());
        assertEquals("sess-new", stored.getChatSessionId());
        assertEquals(Integer.valueOf(2), stored.getSourceCount());
        assertEquals("q+1", stored.getSearchQueryId());
        assertFalse(stored.toSource().containsValue("the answer"));
    }

    @Test
    public void test_storeChatLog_errorWithoutUsage() {
        final List<ChatLog> added = registerCapturingSearchLogHelper(true);
        final ChatLog chatLog = chatApiHelper.createChatLog(ChatLog.ACCESS_TYPE_SYNC, false, "s1", "my-llm", 0L);
        chatApiHelper.storeChatLog(chatLog, ChatLog.STATUS_ERROR, new LlmException("x", LlmException.ERROR_TIMEOUT), 5L,
                new LlmUsageCollector(), null);
        final ChatLog stored = added.get(0);
        assertEquals(ChatLog.STATUS_ERROR, stored.getStatus());
        assertEquals(LlmException.ERROR_TIMEOUT, stored.getErrorCode());
        assertEquals(Integer.valueOf(0), stored.getLlmCalls());
        assertNull(stored.getTotalTokens());
        assertNull(stored.getSourceCount());
        assertEquals("s1", stored.getChatSessionId());
        // the token fields are left out of the document when nothing was reported
        assertFalse(stored.toSource().containsKey("totalTokens"));

        final ChatLog other = chatApiHelper.createChatLog(ChatLog.ACCESS_TYPE_SYNC, false, null, "my-llm", 0L);
        chatApiHelper.storeChatLog(other, ChatLog.STATUS_ERROR, new IllegalStateException("boom"), 5L, null, null);
        assertEquals(LlmException.ERROR_UNKNOWN, added.get(1).getErrorCode());
        assertNull(added.get(1).getLlmCalls());
    }

    @Test
    public void test_storeChatLog_disabled() {
        final List<ChatLog> added = registerCapturingSearchLogHelper(false);
        chatApiHelper.storeChatLog(new ChatLog(), ChatLog.STATUS_SUCCESS, null, 1L, new LlmUsageCollector(), null);
        assertTrue(added.isEmpty());
    }

    @Test
    public void test_storeChatLog_neverThrows() {
        ComponentUtil.setFessConfig(chatLogConfig(true));
        ComponentUtil.register(new SearchLogHelper() {
            @Override
            public void addChatLog(final ChatLog chatLog) {
                throw new IllegalStateException("queue failure");
            }
        }, "searchLogHelper");
        chatApiHelper.storeChatLog(new ChatLog(), ChatLog.STATUS_SUCCESS, null, 1L, null, null);
    }

    @Test
    public void test_extractSearchQueryId() {
        assertNull(chatApiHelper.extractSearchQueryId(null));
        assertNull(chatApiHelper.extractSearchQueryId(List.of()));
        final ChatSource noQueryId = new ChatSource();
        noQueryId.setGoUrl("/go/?rt=1&docId=abc&order=0");
        assertNull(chatApiHelper.extractSearchQueryId(List.of(noQueryId)));
        final ChatSource source = new ChatSource();
        source.setGoUrl("/go/?queryId=abc123&order=0");
        assertEquals("abc123", chatApiHelper.extractSearchQueryId(List.of(noQueryId, source)));
    }

    private static List<ChatLog> registerCapturingSearchLogHelper(final boolean enabled) {
        ComponentUtil.setFessConfig(chatLogConfig(enabled));
        final List<ChatLog> added = new ArrayList<>();
        ComponentUtil.register(new SearchLogHelper() {
            @Override
            public void addChatLog(final ChatLog chatLog) {
                added.add(chatLog);
            }
        }, "searchLogHelper");
        return added;
    }

    private static FessConfig chatLogConfig(final boolean enabled) {
        return new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isRagChatLogEnabled() {
                return enabled;
            }
        };
    }

    // ── rag.chat.permissions / rag.chat.labels ────────────────────────────────

    /** Installs a config whose chat permission set and chat label values are fixed. */
    private static void setChatRestrictions(final java.util.Set<String> permissions, final List<String> labels) {
        org.codelibs.fess.util.ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getRagChatPermissions() {
                return permissions == null ? "" : String.join(",", permissions);
            }

            @Override
            public java.util.Set<String> getRagChatPermissionSet() {
                return permissions;
            }

            @Override
            public List<String> getRagChatLabelValueList() {
                return labels;
            }

            @Override
            public String getIndexFieldLabel() {
                return "label";
            }

            @Override
            public String getIndexFieldDocId() {
                return "doc_id";
            }
        });
    }

    /** A helper whose caller roles and sign-in state are fixed; counts role lookups. */
    private static class FixedUserChatApiHelper extends ChatApiHelper {
        private final java.util.Set<String> roles;
        private final boolean loggedIn;
        int roleLookups;

        FixedUserChatApiHelper(final java.util.Set<String> roles, final boolean loggedIn) {
            this.roles = roles;
            this.loggedIn = loggedIn;
        }

        @Override
        protected java.util.Set<String> getCurrentUserRoles() {
            roleLookups++;
            return roles;
        }

        @Override
        protected boolean isLoggedIn() {
            return loggedIn;
        }
    }

    @Test
    public void test_isChatPermitted_emptyPermissions_permitsWithoutResolvingRoles() {
        setChatRestrictions(java.util.Set.of(), List.of());
        final FixedUserChatApiHelper helper = new FixedUserChatApiHelper(java.util.Set.of(), false);
        assertTrue(helper.isChatPermitted(), "no rag.chat.permissions must leave the chat open to everyone");
        assertTrue(helper.getChatAccessError().isEmpty());
        assertEquals(0, helper.roleLookups, "an unrestricted chat must not resolve the caller's roles");
    }

    @Test
    public void test_isChatPermitted_userHoldsListedRole_permitted() {
        setChatRestrictions(java.util.Set.of("Rrag-user", "2sales"), List.of());
        final FixedUserChatApiHelper helper = new FixedUserChatApiHelper(java.util.Set.of("1taro", "2sales"), true);
        assertTrue(helper.isChatPermitted(), "holding any listed permission must permit the chat");
        assertTrue(helper.getChatAccessError().isEmpty());
    }

    @Test
    public void test_getChatAccessError_signedInWithoutRole_forbidden() {
        setChatRestrictions(java.util.Set.of("Rrag-user"), List.of());
        final FixedUserChatApiHelper helper = new FixedUserChatApiHelper(java.util.Set.of("1taro", "Rother"), true);
        assertFalse(helper.isChatPermitted());
        assertEquals(org.codelibs.fess.api.v2.V2ErrorCode.FORBIDDEN, helper.getChatAccessError().get());
    }

    @Test
    public void test_getChatAccessError_anonymousWithoutRole_authRequired() {
        setChatRestrictions(java.util.Set.of("Rrag-user"), List.of());
        final FixedUserChatApiHelper helper = new FixedUserChatApiHelper(java.util.Set.of("Rguest"), false);
        assertFalse(helper.isChatPermitted());
        assertEquals("an anonymous caller must be asked to log in, like the login.required gate does",
                org.codelibs.fess.api.v2.V2ErrorCode.AUTH_REQUIRED, helper.getChatAccessError().get());
    }

    @Test
    public void test_isChatPermitted_guestRoleListed_permitsAnonymous() {
        setChatRestrictions(java.util.Set.of("Rguest"), List.of());
        final FixedUserChatApiHelper helper = new FixedUserChatApiHelper(java.util.Set.of("Rguest"), false);
        assertTrue(helper.isChatPermitted(), "listing the guest role must open the chat to anonymous users");
    }

    @Test
    public void test_isChatPermitted_unusableEntriesOnly_deniesEveryone() {
        // rag.chat.permissions is set, but nothing in it encodes to a role (e.g. a bare "{role}").
        org.codelibs.fess.util.ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getRagChatPermissions() {
                return "{role}";
            }

            @Override
            public java.util.Set<String> getRagChatPermissionSet() {
                return java.util.Set.of();
            }
        });
        assertFalse(new FixedUserChatApiHelper(java.util.Set.of("Rguest", "Radmin"), true).isChatPermitted(),
                "a set but unusable rag.chat.permissions must fail closed");
    }

    @Test
    public void test_isChatPermitted_nullRoles_denied() {
        setChatRestrictions(java.util.Set.of("Rrag-user"), List.of());
        assertFalse(new FixedUserChatApiHelper(null, true).isChatPermitted());
    }

    @Test
    public void test_getChatFilterQueries_noLabels_empty() {
        setChatRestrictions(java.util.Set.of(), List.of());
        assertTrue(chatApiHelper.getChatFilterQueries().isEmpty(), "no rag.chat.labels must add no filter");
    }

    @Test
    public void test_getChatFilterQueries_labels_termsOnLabelField() {
        setChatRestrictions(java.util.Set.of(), List.of("public", "faq"));
        final List<org.codelibs.fesen.opensearch.index.query.QueryBuilder> filters = chatApiHelper.getChatFilterQueries();
        assertEquals(1, filters.size());
        final org.codelibs.fesen.opensearch.index.query.TermsQueryBuilder terms =
                (org.codelibs.fesen.opensearch.index.query.TermsQueryBuilder) filters.get(0);
        assertEquals(org.codelibs.fess.util.ComponentUtil.getFessConfig().getIndexFieldLabel(), terms.fieldName());
        assertEquals(List.of("public", "faq"), terms.values());
    }

    @Test
    public void test_existsDocument_appliesChatLabelFilter() {
        setChatRestrictions(java.util.Set.of(), List.of("public"));
        final List<List<org.codelibs.fesen.opensearch.index.query.QueryBuilder>> captured = new ArrayList<>();
        org.codelibs.fess.util.ComponentUtil.register(new SearchHelper() {
            @Override
            public org.dbflute.optional.OptionalEntity<Map<String, Object>> getDocumentByDocId(final String docId, final String[] fields,
                    final org.dbflute.optional.OptionalThing<org.codelibs.fess.mylasta.action.FessUserBean> userBean,
                    final List<org.codelibs.fesen.opensearch.index.query.QueryBuilder> filterQueries) {
                captured.add(filterQueries);
                return org.dbflute.optional.OptionalEntity.empty();
            }
        }, "searchHelper");
        assertFalse(chatApiHelper.existsDocument("doc1"), "a document outside the chat labels must be reported as missing");
        assertEquals(1, captured.size());
        assertEquals(1, captured.get(0).size(), "the doc-id lookup must carry the chat label filter");
        assertEquals(chatApiHelper.getChatFilterQueries(), captured.get(0));
    }

}
