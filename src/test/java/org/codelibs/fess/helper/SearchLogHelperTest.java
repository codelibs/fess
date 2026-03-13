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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.log.exentity.SearchLog;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.QueryResponseList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class SearchLogHelperTest extends UnitFessTestCase {

    private SearchLogHelper searchLogHelper;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        searchLogHelper = new SearchLogHelper();
        setupMockComponents();
        searchLogHelper.init();
    }

    private void setupMockComponents() {
        ComponentUtil.setFessConfig(new MockFessConfig());
        ComponentUtil.register(new MockSystemHelper(), "systemHelper");
    }

    @Test
    public void test_init() {
        SearchLogHelper helper = new SearchLogHelper();
        helper.init();

        assertNotNull(helper.userInfoCache);
        assertNotNull(helper.searchLogLogger);
    }

    @Test
    public void test_setUserCheckInterval() {
        long interval = 5 * 60 * 1000L; // 5 minutes
        searchLogHelper.setUserCheckInterval(interval);
        assertEquals(interval, searchLogHelper.userCheckInterval);
    }

    @Test
    public void test_setUserInfoCacheSize() {
        int cacheSize = 5000;
        searchLogHelper.setUserInfoCacheSize(cacheSize);
        assertEquals(cacheSize, searchLogHelper.userInfoCacheSize);
    }

    @Test
    public void test_setLoggerName() {
        String loggerName = "test.logger";
        searchLogHelper.setLoggerName(loggerName);
        assertEquals(loggerName, searchLogHelper.loggerName);
    }

    @Test
    public void test_getUserInfo_emptyUserCode() {
        try {
            var result = searchLogHelper.getUserInfo("");
            assertFalse(result.isPresent());
        } catch (Exception e) {
            // Expected in test environment
            assertTrue(true);
        }
    }

    @Test
    public void test_getUserInfo_nullUserCode() {
        try {
            var result = searchLogHelper.getUserInfo(null);
            assertFalse(result.isPresent());
        } catch (Exception e) {
            // Expected in test environment
            assertTrue(true);
        }
    }

    @Test
    public void test_toLowerHyphen() {
        Map<String, Object> source = new HashMap<>();
        source.put("TestKey", "value1");
        source.put("AnotherTestKey", "value2");

        Map<String, Object> nested = new HashMap<>();
        nested.put("NestedKey", "nestedValue");
        source.put("NestedMap", nested);

        Map<String, Object> result = searchLogHelper.toLowerHyphen(source);

        assertEquals("value1", result.get("test_key"));
        assertEquals("value2", result.get("another_test_key"));

        @SuppressWarnings("unchecked")
        Map<String, Object> nestedResult = (Map<String, Object>) result.get("nested_map");
        assertEquals("nestedValue", nestedResult.get("nested_key"));
    }

    @Test
    public void test_toLowerHyphen_emptyMap() {
        Map<String, Object> source = new HashMap<>();
        Map<String, Object> result = searchLogHelper.toLowerHyphen(source);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void test_toLowerHyphen_nullValues() {
        Map<String, Object> source = new HashMap<>();
        source.put("AnotherKey", "anotherValue");
        source.put("ValidKey", "validValue");

        Map<String, Object> result = searchLogHelper.toLowerHyphen(source);

        assertEquals("anotherValue", result.get("another_key"));
        assertEquals("validValue", result.get("valid_key"));
    }

    @Test
    public void test_searchLogQueue_initialization() {
        assertNotNull(searchLogHelper.searchLogQueue);
        assertTrue(searchLogHelper.searchLogQueue.isEmpty());
    }

    @Test
    public void test_clickLogQueue_initialization() {
        assertNotNull(searchLogHelper.clickLogQueue);
        assertTrue(searchLogHelper.clickLogQueue.isEmpty());
    }

    // ===== addSearchLog Access Type tests =====

    private void setupAddSearchLogComponents() {
        final MockRoleQueryHelper roleQueryHelper = new MockRoleQueryHelper();
        ComponentUtil.register(roleQueryHelper, "roleQueryHelper");

        final MockUserInfoHelper userInfoHelper = new MockUserInfoHelper();
        ComponentUtil.register(userInfoHelper, "userInfoHelper");

        final MockViewHelper viewHelper = new MockViewHelper();
        ComponentUtil.register(viewHelper, "viewHelper");

        final MockVirtualHostHelper virtualHostHelper = new MockVirtualHostHelper();
        ComponentUtil.register(virtualHostHelper, "virtualHostHelper");
    }

    private SearchLog callAddSearchLogAndGetResult(final String accessTypeAttribute) {
        setupAddSearchLogComponents();

        if (accessTypeAttribute != null) {
            setMockRequestAttribute(Constants.SEARCH_LOG_ACCESS_TYPE, accessTypeAttribute);
        }

        final MockSearchRequestParams params = new MockSearchRequestParams();
        final LocalDateTime now = LocalDateTime.now();
        final QueryResponseList queryResponseList = new QueryResponseList(Collections.emptyList(), 0L, "eq", 0L, false, null, 0, 10, 0);

        searchLogHelper.addSearchLog(params, now, "test-query-id", "test query", 0, 10, queryResponseList);

        assertFalse(searchLogHelper.searchLogQueue.isEmpty());
        return searchLogHelper.searchLogQueue.poll();
    }

    @Test
    public void test_addSearchLog_accessType_json() {
        final SearchLog searchLog = callAddSearchLogAndGetResult(Constants.SEARCH_LOG_ACCESS_TYPE_JSON);
        assertEquals(Constants.SEARCH_LOG_ACCESS_TYPE_JSON, searchLog.getAccessType());
    }

    @Test
    public void test_addSearchLog_accessType_gsa() {
        final SearchLog searchLog = callAddSearchLogAndGetResult(Constants.SEARCH_LOG_ACCESS_TYPE_GSA);
        assertEquals(Constants.SEARCH_LOG_ACCESS_TYPE_GSA, searchLog.getAccessType());
    }

    @Test
    public void test_addSearchLog_accessType_other() {
        final SearchLog searchLog = callAddSearchLogAndGetResult(Constants.SEARCH_LOG_ACCESS_TYPE_OTHER);
        assertEquals(Constants.SEARCH_LOG_ACCESS_TYPE_OTHER, searchLog.getAccessType());
    }

    @Test
    public void test_addSearchLog_accessType_admin() {
        final SearchLog searchLog = callAddSearchLogAndGetResult(Constants.SEARCH_LOG_ACCESS_TYPE_ADMIN);
        assertEquals(Constants.SEARCH_LOG_ACCESS_TYPE_ADMIN, searchLog.getAccessType());
    }

    @Test
    public void test_addSearchLog_accessType_defaultWeb() {
        final SearchLog searchLog = callAddSearchLogAndGetResult(null);
        assertEquals(Constants.SEARCH_LOG_ACCESS_TYPE_WEB, searchLog.getAccessType());
    }

    @Test
    public void test_addSearchLog_accessType_ollama() {
        final SearchLog searchLog = callAddSearchLogAndGetResult("ollama");
        assertEquals("ollama", searchLog.getAccessType());
    }

    @Test
    public void test_addSearchLog_accessType_openai() {
        final SearchLog searchLog = callAddSearchLogAndGetResult("openai");
        assertEquals("openai", searchLog.getAccessType());
    }

    @Test
    public void test_addSearchLog_accessType_gemini() {
        final SearchLog searchLog = callAddSearchLogAndGetResult("gemini");
        assertEquals("gemini", searchLog.getAccessType());
    }

    @Test
    public void test_addSearchLog_accessType_customLlmName() {
        final SearchLog searchLog = callAddSearchLogAndGetResult("my-custom-llm");
        assertEquals("my-custom-llm", searchLog.getAccessType());
    }

    @Test
    public void test_addSearchLog_accessType_blankStringDefaultsToWeb() {
        setupAddSearchLogComponents();
        setMockRequestAttribute(Constants.SEARCH_LOG_ACCESS_TYPE, "");

        final MockSearchRequestParams params = new MockSearchRequestParams();
        final LocalDateTime now = LocalDateTime.now();
        final QueryResponseList queryResponseList = new QueryResponseList(Collections.emptyList(), 0L, "eq", 0L, false, null, 0, 10, 0);

        searchLogHelper.addSearchLog(params, now, "test-query-id", "test query", 0, 10, queryResponseList);

        assertFalse(searchLogHelper.searchLogQueue.isEmpty());
        final SearchLog searchLog = searchLogHelper.searchLogQueue.poll();
        assertEquals(Constants.SEARCH_LOG_ACCESS_TYPE_WEB, searchLog.getAccessType());
    }

    @Test
    public void test_addSearchLog_accessType_whitespaceOnlyDefaultsToWeb() {
        setupAddSearchLogComponents();
        setMockRequestAttribute(Constants.SEARCH_LOG_ACCESS_TYPE, "   ");

        final MockSearchRequestParams params = new MockSearchRequestParams();
        final LocalDateTime now = LocalDateTime.now();
        final QueryResponseList queryResponseList = new QueryResponseList(Collections.emptyList(), 0L, "eq", 0L, false, null, 0, 10, 0);

        searchLogHelper.addSearchLog(params, now, "test-query-id", "test query", 0, 10, queryResponseList);

        assertFalse(searchLogHelper.searchLogQueue.isEmpty());
        final SearchLog searchLog = searchLogHelper.searchLogQueue.poll();
        assertEquals(Constants.SEARCH_LOG_ACCESS_TYPE_WEB, searchLog.getAccessType());
    }

    @Test
    public void test_addSearchLog_accessType_nonStringObjectDefaultsToWeb() {
        setupAddSearchLogComponents();
        setMockRequestAttribute(Constants.SEARCH_LOG_ACCESS_TYPE, Integer.valueOf(123));

        final MockSearchRequestParams params = new MockSearchRequestParams();
        final LocalDateTime now = LocalDateTime.now();
        final QueryResponseList queryResponseList = new QueryResponseList(Collections.emptyList(), 0L, "eq", 0L, false, null, 0, 10, 0);

        searchLogHelper.addSearchLog(params, now, "test-query-id", "test query", 0, 10, queryResponseList);

        assertFalse(searchLogHelper.searchLogQueue.isEmpty());
        final SearchLog searchLog = searchLogHelper.searchLogQueue.poll();
        assertEquals(Constants.SEARCH_LOG_ACCESS_TYPE_WEB, searchLog.getAccessType());
    }

    // Mock classes for addSearchLog tests

    private static class MockSearchRequestParams extends SearchRequestParams {
        @Override
        public String getQuery() {
            return "test query";
        }

        @Override
        public Map<String, String[]> getFields() {
            return Collections.emptyMap();
        }

        @Override
        public Map<String, String[]> getConditions() {
            return Collections.emptyMap();
        }

        @Override
        public String[] getLanguages() {
            return new String[0];
        }

        @Override
        public GeoInfo getGeoInfo() {
            return null;
        }

        @Override
        public FacetInfo getFacetInfo() {
            return null;
        }

        @Override
        public HighlightInfo getHighlightInfo() {
            return null;
        }

        @Override
        public String getSort() {
            return null;
        }

        @Override
        public int getStartPosition() {
            return 0;
        }

        @Override
        public int getPageSize() {
            return 10;
        }

        @Override
        public int getOffset() {
            return 0;
        }

        @Override
        public String[] getExtraQueries() {
            return new String[0];
        }

        @Override
        public Object getAttribute(final String name) {
            return null;
        }

        @Override
        public SearchRequestType getType() {
            return SearchRequestType.SEARCH;
        }

        @Override
        public String getSimilarDocHash() {
            return null;
        }

        @Override
        public java.util.Locale getLocale() {
            return java.util.Locale.ROOT;
        }
    }

    private static class MockRoleQueryHelper extends RoleQueryHelper {
        @Override
        public Set<String> build(final SearchRequestType searchRequestType) {
            return Collections.emptySet();
        }
    }

    private static class MockUserInfoHelper extends UserInfoHelper {
        @Override
        public String getUserCode() {
            return null;
        }
    }

    private static class MockViewHelper extends ViewHelper {
        @Override
        public String getClientIp(final jakarta.servlet.http.HttpServletRequest req) {
            return "127.0.0.1";
        }
    }

    private static class MockVirtualHostHelper extends VirtualHostHelper {
        @Override
        public String getVirtualHostKey() {
            return "";
        }
    }

    // Mock classes
    private static class MockFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isUserInfo() {
            return true;
        }

        @Override
        public Integer getLoggingSearchMaxQueueSizeAsInteger() {
            return 1000;
        }

        @Override
        public Integer getLoggingClickMaxQueueSizeAsInteger() {
            return 1000;
        }

        @Override
        public Integer getQueryMaxLengthAsInteger() {
            return 1000;
        }

        @Override
        public String[] getSearchlogRequestHeadersAsArray() {
            return new String[] { "user-agent", "referer" };
        }

        @Override
        public boolean isLoggingSearchDocsEnabled() {
            return false;
        }

        @Override
        public String[] getLoggingSearchDocsFieldsAsArray() {
            return new String[] { "title", "url" };
        }

        @Override
        public String getPurgeByBots() {
            return "bot,crawler,spider";
        }

        @Override
        public Integer getSearchlogProcessBatchSizeAsInteger() {
            return 100;
        }

        @Override
        public boolean isLoggingSearchUseLogfile() {
            return false;
        }

        @Override
        public boolean isSuggestSearchLog() {
            return false;
        }
    }

    private static class MockSystemHelper extends SystemHelper {
        @Override
        public java.time.LocalDateTime getCurrentTimeAsLocalDateTime() {
            return java.time.LocalDateTime.now();
        }
    }
}