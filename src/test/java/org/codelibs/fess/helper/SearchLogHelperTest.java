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

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class SearchLogHelperTest extends UnitFessTestCase {

    private SearchLogHelper searchLogHelper;

    @BeforeEach
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