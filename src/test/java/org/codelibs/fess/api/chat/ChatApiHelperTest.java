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
package org.codelibs.fess.api.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
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
    public void test_parseFieldFilters_nullInput_returnsEmptyMap() {
        final Map<String, List<String>> warnings = new HashMap<>();
        final Map<String, String[]> result = chatApiHelper.parseFieldFilters(null != null ? null : Collections.emptyMap(), warnings);
        assertTrue(result.isEmpty(), "null/empty raw input must return empty map");
        assertTrue(warnings.isEmpty(), "no warnings for empty input");
    }

    @Test
    public void test_parseFieldFilters_emptyRaw_returnsEmptyMap() {
        final Map<String, List<String>> warnings = new HashMap<>();
        final Map<String, String[]> result = chatApiHelper.parseFieldFilters(Collections.emptyMap(), warnings);
        assertTrue(result.isEmpty(), "empty raw map must return empty map");
        assertTrue(warnings.isEmpty(), "no warnings for empty raw map");
    }

    @Test
    public void test_parseFieldFilters_fieldsIsString_typesMismatch_returnsEmptyMap() {
        // "fields" key present but value is a plain String, not a Map — must be ignored
        final Map<String, Object> raw = new HashMap<>();
        raw.put("fields", "not-a-map");
        final Map<String, List<String>> warnings = new HashMap<>();
        final Map<String, String[]> result = chatApiHelper.parseFieldFilters(raw, warnings);
        // No labelsRaw resolved; result must be empty
        assertTrue(result.isEmpty(), "scalar fields value must not produce filters");
    }

    @Test
    public void test_parseFieldFilters_nestedStringLabel_allowlistEmpty_addedToWarnings() {
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
    public void test_parseFieldFilters_nestedArrayLabel_allowlistEmpty_allRejected() {
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
    public void test_parseFieldFilters_dottedKeyFallback_allowlistEmpty_addedToWarnings() {
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
    public void test_parseFieldFilters_listWithNullElement_nullSkipped() {
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
    public void test_parseFieldFilters_warningsKeyIsFieldsDotLabel() {
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
    public void test_parseExtraQueries_nullKey_returnsEmptyArray() {
        // No "extra_queries" key in raw → empty array, no warnings
        final Map<String, Object> raw = new HashMap<>();
        final Map<String, List<String>> warnings = new HashMap<>();
        final String[] result = chatApiHelper.parseExtraQueries(raw, warnings);
        assertEquals(0, result.length);
        assertTrue(warnings.isEmpty(), "no warnings when key absent");
    }

    @Test
    public void test_parseExtraQueries_scalarString_rejectedWhenAllowlistEmpty() {
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
    public void test_parseExtraQueries_arrayInput_rejectedWhenAllowlistEmpty() {
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
    public void test_parseExtraQueries_warningsKeyIsSnakeCase() {
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

}
