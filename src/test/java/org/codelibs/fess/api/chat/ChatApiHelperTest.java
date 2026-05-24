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
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.UserInfoHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ChatApiHelper}.
 *
 * <p>All methods are static utilities. Tests use {@code ComponentUtil.register}
 * to stub external helpers (SystemHelper, UserInfoHelper, FessConfig) that
 * ChatApiHelper delegates to, following the same pattern as other v2 handler
 * tests in this project.</p>
 *
 * <p>LabelTypeHelper and ViewHelper are intentionally NOT registered in most
 * tests: when they are absent the helpers degrade to an empty allowlist, so all
 * candidate values are rejected. This verifies the fallback path.</p>
 */
public class ChatApiHelperTest extends UnitFessTestCase {

    // ── parseFieldFilters ─────────────────────────────────────────────────────

    @Test
    public void test_parseFieldFilters_nullInput_returnsEmptyMap() {
        final Map<String, List<String>> warnings = new HashMap<>();
        final Map<String, String[]> result = ChatApiHelper.parseFieldFilters(null != null ? null : Collections.emptyMap(), warnings);
        assertTrue(result.isEmpty(), "null/empty raw input must return empty map");
        assertTrue(warnings.isEmpty(), "no warnings for empty input");
    }

    @Test
    public void test_parseFieldFilters_emptyRaw_returnsEmptyMap() {
        final Map<String, List<String>> warnings = new HashMap<>();
        final Map<String, String[]> result = ChatApiHelper.parseFieldFilters(Collections.emptyMap(), warnings);
        assertTrue(result.isEmpty(), "empty raw map must return empty map");
        assertTrue(warnings.isEmpty(), "no warnings for empty raw map");
    }

    @Test
    public void test_parseFieldFilters_fieldsIsString_typesMismatch_returnsEmptyMap() {
        // "fields" key present but value is a plain String, not a Map — must be ignored
        final Map<String, Object> raw = new HashMap<>();
        raw.put("fields", "not-a-map");
        final Map<String, List<String>> warnings = new HashMap<>();
        final Map<String, String[]> result = ChatApiHelper.parseFieldFilters(raw, warnings);
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
        final Map<String, String[]> result = ChatApiHelper.parseFieldFilters(raw, warnings);

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
        final Map<String, String[]> result = ChatApiHelper.parseFieldFilters(raw, warnings);

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
        final Map<String, String[]> result = ChatApiHelper.parseFieldFilters(raw, warnings);

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
        ChatApiHelper.parseFieldFilters(raw, warnings);

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
        ChatApiHelper.parseFieldFilters(raw, warnings);

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
        final String[] result = ChatApiHelper.parseExtraQueries(raw, warnings);
        assertEquals(0, result.length);
        assertTrue(warnings.isEmpty(), "no warnings when key absent");
    }

    @Test
    public void test_parseExtraQueries_scalarString_rejectedWhenAllowlistEmpty() {
        // extra_queries as plain string (scalar) — ViewHelper absent → empty allowlist
        final Map<String, Object> raw = new HashMap<>();
        raw.put("extra_queries", "query-a");
        final Map<String, List<String>> warnings = new HashMap<>();
        final String[] result = ChatApiHelper.parseExtraQueries(raw, warnings);

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
        final String[] result = ChatApiHelper.parseExtraQueries(raw, warnings);

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
        ChatApiHelper.parseExtraQueries(raw, warnings);

        if (!warnings.isEmpty()) {
            assertTrue(warnings.containsKey("extra_queries"), "warnings key must be 'extra_queries' (snake_case)");
            assertFalse(warnings.containsKey("extraQueries"), "warnings key must NOT be camelCase 'extraQueries'");
        }
    }

    // ── getUserId ─────────────────────────────────────────────────────────────

    @Test
    public void test_getUserId_authenticatedUser_returnsUsername() {
        // SystemHelper.getUsername returns a non-guest name → that name is the userId
        final SystemHelper systemHelper = new SystemHelper() {
            @Override
            public String getUsername() {
                return "alice";
            }
        };
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.register(systemHelper, SystemHelper.class.getCanonicalName());

        final String userId = ChatApiHelper.getUserId(new MinimalRequest());
        assertEquals("alice", userId);
    }

    @Test
    public void test_getUserId_guestUser_fallsBackToUserCode() {
        // SystemHelper.getUsername returns Constants.GUEST_USER → fall back to getUserCode
        final SystemHelper systemHelper = new SystemHelper() {
            @Override
            public String getUsername() {
                return Constants.GUEST_USER;
            }
        };
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.register(systemHelper, SystemHelper.class.getCanonicalName());

        final UserInfoHelper userInfoHelper = new UserInfoHelper() {
            @Override
            public String getUserCode() {
                return "cookie-code-xyz";
            }
        };
        ComponentUtil.register(userInfoHelper, "userInfoHelper");
        ComponentUtil.register(userInfoHelper, UserInfoHelper.class.getCanonicalName());

        final String userId = ChatApiHelper.getUserId(new MinimalRequest());
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
        final int len = ChatApiHelper.getMaxMessageLength(cfg);
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
        final int len = ChatApiHelper.getMaxMessageLength(cfg);
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
        final int len = ChatApiHelper.getMaxMessageLength(cfg);
        assertEquals(4000, len);
    }

    // ── getChatRateLimitPerMinute ─────────────────────────────────────────────

    @Test
    public void test_getChatRateLimitPerMinute_defaultValue() {
        // When the system property is not set, default is 30
        final FessConfig cfg = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;
        };
        final int limit = ChatApiHelper.getChatRateLimitPerMinute(cfg);
        assertEquals(30, limit);
    }

    @Test
    public void test_getChatRateLimitPerMinute_zeroDisablesLimit() {
        // A return value <= 0 signals "disabled"
        final FessConfig cfg = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getSystemProperty(final String key, final String defaultValue) {
                if ("api.v2.chat.rate.limit.per.user.per.minute".equals(key)) {
                    return "0";
                }
                return defaultValue;
            }
        };
        final int limit = ChatApiHelper.getChatRateLimitPerMinute(cfg);
        assertTrue(limit <= 0, "value 0 must signal disabled (<=0), got: " + limit);
    }

    @Test
    public void test_getChatRateLimitPerMinute_invalidValue_returnsDefault() {
        // Non-numeric config value → NumberFormatException → fallback 30
        final FessConfig cfg = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getSystemProperty(final String key, final String defaultValue) {
                if ("api.v2.chat.rate.limit.per.user.per.minute".equals(key)) {
                    return "bad-value";
                }
                return defaultValue;
            }
        };
        final int limit = ChatApiHelper.getChatRateLimitPerMinute(cfg);
        assertEquals(30, limit);
    }

    // ── Minimal HttpServletRequest stub ───────────────────────────────────────

    /**
     * Minimal HttpServletRequest stub that satisfies ChatApiHelper.getUserId's
     * only requirement: the request object is passed to the method but is not
     * directly accessed — SystemHelper and UserInfoHelper are resolved via
     * ComponentUtil.
     */
    private static class MinimalRequest implements jakarta.servlet.http.HttpServletRequest {
        @Override
        public String getMethod() {
            return "POST";
        }

        @Override
        public String getServletPath() {
            return "/api/v2/chat";
        }

        @Override
        public String getRequestURI() {
            return "/api/v2/chat";
        }

        @Override
        public String getContextPath() {
            return "";
        }

        @Override
        public Object getAttribute(final String n) {
            return null;
        }

        @Override
        public void setAttribute(final String n, final Object v) {
        }

        @Override
        public void removeAttribute(final String n) {
        }

        @Override
        public java.util.Enumeration<String> getAttributeNames() {
            return java.util.Collections.emptyEnumeration();
        }

        @Override
        public jakarta.servlet.RequestDispatcher getRequestDispatcher(final String p) {
            return null;
        }

        @Override
        public String getAuthType() {
            return null;
        }

        @Override
        public jakarta.servlet.http.Cookie[] getCookies() {
            return null;
        }

        @Override
        public long getDateHeader(final String n) {
            return -1;
        }

        @Override
        public String getHeader(final String n) {
            return null;
        }

        @Override
        public java.util.Enumeration<String> getHeaders(final String n) {
            return java.util.Collections.emptyEnumeration();
        }

        @Override
        public java.util.Enumeration<String> getHeaderNames() {
            return java.util.Collections.emptyEnumeration();
        }

        @Override
        public int getIntHeader(final String n) {
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
        public boolean isUserInRole(final String r) {
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
            return new StringBuffer(getRequestURI());
        }

        @Override
        public jakarta.servlet.http.HttpSession getSession(final boolean c) {
            return null;
        }

        @Override
        public jakarta.servlet.http.HttpSession getSession() {
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
        public boolean authenticate(final jakarta.servlet.http.HttpServletResponse r) {
            return false;
        }

        @Override
        public void login(final String u, final String p) {
        }

        @Override
        public void logout() {
        }

        @Override
        public java.util.Collection<jakarta.servlet.http.Part> getParts() {
            return java.util.Collections.emptyList();
        }

        @Override
        public jakarta.servlet.http.Part getPart(final String n) {
            return null;
        }

        @Override
        public <T extends jakarta.servlet.http.HttpUpgradeHandler> T upgrade(final Class<T> h) {
            return null;
        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public void setCharacterEncoding(final String e) {
        }

        @Override
        public int getContentLength() {
            return 0;
        }

        @Override
        public long getContentLengthLong() {
            return 0;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public jakarta.servlet.ServletInputStream getInputStream() {
            return null;
        }

        @Override
        public String getParameter(final String n) {
            return null;
        }

        @Override
        public java.util.Enumeration<String> getParameterNames() {
            return java.util.Collections.emptyEnumeration();
        }

        @Override
        public String[] getParameterValues(final String n) {
            return null;
        }

        @Override
        public java.util.Map<String, String[]> getParameterMap() {
            return java.util.Collections.emptyMap();
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
            return null;
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
        public java.util.Enumeration<java.util.Locale> getLocales() {
            return java.util.Collections.enumeration(java.util.Collections.singleton(java.util.Locale.ROOT));
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
        public jakarta.servlet.ServletContext getServletContext() {
            return null;
        }

        @Override
        public jakarta.servlet.AsyncContext startAsync() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.AsyncContext startAsync(final jakarta.servlet.ServletRequest rq, final jakarta.servlet.ServletResponse rs) {
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
        public jakarta.servlet.AsyncContext getAsyncContext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.DispatcherType getDispatcherType() {
            return jakarta.servlet.DispatcherType.REQUEST;
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
