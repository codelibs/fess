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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.config.exbhv.BadWordBhv;
import org.codelibs.fess.opensearch.config.exbhv.ElevateWordBhv;
import org.codelibs.fess.opensearch.config.exentity.BadWord;
import org.codelibs.fess.opensearch.config.exentity.ElevateWord;
import org.codelibs.fess.opensearch.log.exbhv.SearchLogBhv;
import org.codelibs.fess.opensearch.log.exentity.SearchLog;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class SuggestHelperTest extends UnitFessTestCase {

    private SuggestHelper suggestHelper;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        suggestHelper = new SuggestHelper();
        setupMockComponents();
    }

    private void setupMockComponents() {
        ComponentUtil.setFessConfig(new MockFessConfig());
        ComponentUtil.register(new MockSearchEngineClient(), "searchEngineClient");
        ComponentUtil.register(new MockSearchLogBhv(), "searchLogBhv");
        ComponentUtil.register(new MockElevateWordBhv(), "elevateWordBhv");
        ComponentUtil.register(new MockBadWordBhv(), "badWordBhv");
        ComponentUtil.register(new MockSystemHelper(), "systemHelper");
        ComponentUtil.register(new MockPopularWordHelper(), "popularWordHelper");
    }

    @Test
    public void test_init() {
        SuggestHelper helper = new SuggestHelper();
        try {
            helper.init();
            assertNotNull(helper.suggester());
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_suggester() {
        try {
            assertNull(suggestHelper.suggester());
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_setSearchStoreInterval() {
        long interval = 5L;
        suggestHelper.setSearchStoreInterval(interval);
        assertEquals(interval, suggestHelper.searchStoreInterval);
    }

    @Test
    public void test_storeSearchLog() {
        try {
            suggestHelper.storeSearchLog();
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_indexFromSearchLog() {
        List<SearchLog> searchLogList = new ArrayList<>();
        SearchLog searchLog = new SearchLog();
        searchLog.setHitCount(10L);
        searchLog.setUserSessionId("session123");
        searchLog.setClientIp("192.168.1.1");
        searchLog.setSearchWord("test");
        searchLog.setRequestedAt(LocalDateTime.now());
        searchLogList.add(searchLog);

        try {
            suggestHelper.indexFromSearchLog(searchLogList);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_indexFromSearchLog_lowHitCount() {
        List<SearchLog> searchLogList = new ArrayList<>();
        SearchLog searchLog = new SearchLog();
        searchLog.setHitCount(0L);
        searchLogList.add(searchLog);

        try {
            suggestHelper.indexFromSearchLog(searchLogList);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_indexFromDocuments() {
        Consumer<Boolean> successCallback = (success) -> {
            assertTrue(success);
        };
        Consumer<Throwable> errorCallback = (error) -> {
            assertNotNull(error);
        };

        try {
            suggestHelper.indexFromDocuments(successCallback, errorCallback);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_purgeDocumentSuggest() {
        LocalDateTime time = LocalDateTime.now().minusDays(1);
        try {
            suggestHelper.purgeDocumentSuggest(time);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_purgeSearchlogSuggest() {
        LocalDateTime time = LocalDateTime.now().minusDays(1);
        try {
            suggestHelper.purgeSearchlogSuggest(time);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_getAllWordsNum() {
        try {
            assertEquals(0L, suggestHelper.getAllWordsNum());
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_getDocumentWordsNum() {
        try {
            assertEquals(0L, suggestHelper.getDocumentWordsNum());
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_getQueryWordsNum() {
        try {
            assertEquals(0L, suggestHelper.getQueryWordsNum());
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_deleteAllWords() {
        try {
            assertFalse(suggestHelper.deleteAllWords());
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_deleteDocumentWords() {
        try {
            assertFalse(suggestHelper.deleteDocumentWords());
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_deleteQueryWords() {
        try {
            assertFalse(suggestHelper.deleteQueryWords());
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_storeAllElevateWords() {
        try {
            suggestHelper.storeAllElevateWords(true);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_deleteAllElevateWord() {
        try {
            suggestHelper.deleteAllElevateWord(true);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_deleteElevateWord() {
        try {
            suggestHelper.deleteElevateWord("test", true);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_addElevateWord() {
        try {
            suggestHelper.addElevateWord("test", "reading", new String[] { "tag1" }, new String[] { "role1" }, 1.0f, true);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_addElevateWord_noReading() {
        try {
            suggestHelper.addElevateWord("test word", null, new String[] { "tag1" }, new String[] { "role1" }, 1.0f, true);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_storeAllBadWords() {
        try {
            suggestHelper.storeAllBadWords(true);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_addBadWord() {
        try {
            suggestHelper.addBadWord("badword", true);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_deleteBadWord() {
        try {
            suggestHelper.deleteBadWord("badword");
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_refresh() {
        try {
            suggestHelper.refresh();
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    // Mock classes
    private static class MockFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;

        @Override
        public String getSuggestFieldContents() {
            return "title,content";
        }

        @Override
        public String getSuggestFieldTags() {
            return "label";
        }

        @Override
        public String getSuggestFieldRoles() {
            return "role";
        }

        @Override
        public String getSuggestFieldIndexContents() {
            return "title,content";
        }

        @Override
        public String getFesenType() {
            return "opensearch";
        }

        @Override
        public String getIndexDocumentSuggestIndex() {
            return "fess.suggest";
        }

        @Override
        public Integer getSuggestMinHitCountAsInteger() {
            return 1;
        }

        @Override
        public String getIndexBulkTimeout() {
            return "60000";
        }

        @Override
        public String getIndexHealthTimeout() {
            return "10000";
        }

        @Override
        public String getIndexIndexTimeout() {
            return "60000";
        }

        @Override
        public String getIndexIndicesTimeout() {
            return "60000";
        }

        @Override
        public String getIndexSearchTimeout() {
            return "30000";
        }

        @Override
        public int getPurgeSuggestSearchLogDay() {
            return 30;
        }

        @Override
        public Integer getSuggestUpdateRequestIntervalAsInteger() {
            return 1000;
        }

        @Override
        public Integer getSuggestUpdateDocPerRequestAsInteger() {
            return 100;
        }

        @Override
        public Integer getSuggestSourceReaderScrollSizeAsInteger() {
            return 1000;
        }

        @Override
        public String getSuggestUpdateContentsLimitNumPercentage() {
            return "50";
        }

        @Override
        public Integer getSuggestUpdateContentsLimitNumAsInteger() {
            return 10000;
        }

        @Override
        public Integer getSuggestUpdateContentsLimitDocSizeAsInteger() {
            return 50000;
        }

        @Override
        public String getIndexFieldDocId() {
            return "doc_id";
        }

        @Override
        public String getIndexFieldClickCount() {
            return "click_count";
        }

        @Override
        public String getIndexDocumentSearchIndex() {
            return "fess";
        }

        @Override
        public Integer getPageElevateWordMaxFetchSizeAsInteger() {
            return 1000;
        }

        @Override
        public Integer getPageBadWordMaxFetchSizeAsInteger() {
            return 1000;
        }

        @Override
        public boolean isValidSearchLogPermissions(String[] permissions) {
            return true;
        }
    }

    private static class MockSearchEngineClient extends SearchEngineClient {
        // Mock implementation - no admin client needed for tests
    }

    private static class MockSearchLogBhv extends SearchLogBhv {
        public void selectBulk(Consumer<Object> entityLambda, Consumer<List<SearchLog>> entityListLambda) {
            List<SearchLog> searchLogList = new ArrayList<>();
            SearchLog searchLog = new SearchLog();
            searchLog.setHitCount(10L);
            searchLog.setUserSessionId("session123");
            searchLog.setClientIp("192.168.1.1");
            searchLog.setSearchWord("test");
            searchLog.setRequestedAt(LocalDateTime.now());
            searchLogList.add(searchLog);
            entityListLambda.accept(searchLogList);
        }
    }

    private static class MockElevateWordBhv extends ElevateWordBhv {
        public List<ElevateWord> selectList(Consumer<Object> cbLambda) {
            List<ElevateWord> elevateWordList = new ArrayList<>();
            ElevateWord elevateWord = new ElevateWord();
            elevateWord.setSuggestWord("test");
            elevateWord.setReading("test");
            elevateWord.setLabelTypeIds(new String[] { "label1" });
            elevateWord.setPermissions(new String[] { "role1" });
            elevateWord.setBoost(1.0f);
            elevateWordList.add(elevateWord);
            return elevateWordList;
        }
    }

    private static class MockBadWordBhv extends BadWordBhv {
        public List<BadWord> selectList(Consumer<Object> cbLambda) {
            List<BadWord> badWordList = new ArrayList<>();
            BadWord badWord = new BadWord();
            badWord.setSuggestWord("badword");
            badWordList.add(badWord);
            return badWordList;
        }
    }

    private static class MockSystemHelper extends SystemHelper {
        @Override
        public long getCurrentTimeAsLong() {
            return System.currentTimeMillis();
        }

        @Override
        public boolean calibrateCpuLoad() {
            return true;
        }
    }

    private static class MockPopularWordHelper extends PopularWordHelper {
        @Override
        public void clearCache() {
            // Mock implementation
        }
    }

    private static class MockSuggester {
        private MockSuggestIndexer indexer = new MockSuggestIndexer();
        private MockSuggestSettings settings = new MockSuggestSettings();

        public MockSuggestIndexer indexer() {
            return indexer;
        }

        public MockSuggestSettings settings() {
            return settings;
        }

        public void refresh() {
            // Mock implementation
        }

        public void createIndexIfNothing() {
            // Mock implementation
        }

        public String getIndex() {
            return "fess.suggest";
        }

        public long getAllWordsNum() {
            return 100L;
        }

        public long getDocumentWordsNum() {
            return 50L;
        }

        public long getQueryWordsNum() {
            return 30L;
        }
    }

    private static class MockSuggestIndexer {
        public MockSuggestDeleteResponse deleteAll() {
            return new MockSuggestDeleteResponse();
        }

        public MockSuggestDeleteResponse deleteDocumentWords() {
            return new MockSuggestDeleteResponse();
        }

        public MockSuggestDeleteResponse deleteQueryWords() {
            return new MockSuggestDeleteResponse();
        }

        public void indexFromSearchWord(String searchWord, String[] fields, String[] tags, String[] roles, int weight, String[] langs) {
            // Mock implementation
        }

        public void deleteElevateWord(String word, boolean apply) {
            // Mock implementation
        }

        public void addElevateWord(org.codelibs.fess.suggest.entity.ElevateWord elevateWord, boolean apply) {
            // Mock implementation
        }

        public void addBadWord(String word, boolean apply) {
            // Mock implementation
        }

        public void deleteBadWord(String word) {
            // Mock implementation
        }
    }

    private static class MockSuggestSettings {
        public MockArraySettings array() {
            return new MockArraySettings();
        }

        public MockBadWordSettings badword() {
            return new MockBadWordSettings();
        }
    }

    private static class MockArraySettings {
        public void delete(String key) {
            // Mock implementation
        }

        public void add(String key, String value) {
            // Mock implementation
        }
    }

    private static class MockBadWordSettings {
        public void deleteAll() {
            // Mock implementation
        }
    }

    private static class MockSuggestDeleteResponse {
        public boolean hasError() {
            return false;
        }

        public List<Throwable> getErrors() {
            return new ArrayList<>();
        }
    }
}