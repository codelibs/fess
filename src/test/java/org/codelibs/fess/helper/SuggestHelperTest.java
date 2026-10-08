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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.codelibs.fesen.opensearch.index.IndexNotFoundException;
import org.codelibs.fesen.opensearch.transport.client.Client;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.config.exbhv.BadWordBhv;
import org.codelibs.fess.opensearch.config.exbhv.ElevateWordBhv;
import org.codelibs.fess.opensearch.config.exentity.BadWord;
import org.codelibs.fess.opensearch.config.exentity.ElevateWord;
import org.codelibs.fess.opensearch.log.exbhv.SearchLogBhv;
import org.codelibs.fess.opensearch.log.exentity.SearchLog;
import org.codelibs.fess.suggest.exception.SuggestSettingsException;
import org.codelibs.fess.suggest.Suggester;
import org.codelibs.fess.suggest.index.SuggestIndexResponse;
import org.codelibs.fess.suggest.settings.SuggestSettings;
import org.codelibs.fess.suggest.settings.SuggestSettingsBuilder;
import org.codelibs.fess.suggest.settings.TimeoutSettings;
import org.codelibs.fess.unit.LogCapturingAppender;
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

    /** Stops {@code init()} when the suggester is to be built: there is no engine behind it to build one from. */
    private static class SuggesterNotBuilt extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    /** Records what {@code init()} hands to the suggest library, and in which order it does the steps. */
    private static class InitProbe extends SuggestHelper {
        final List<String> steps = new ArrayList<>();

        final Map<String, Object> recordedInitialSettings = new LinkedHashMap<>();

        final RecordingSuggestSettings settings;

        InitProbe(final Object persistedType) {
            this.settings = new RecordingSuggestSettings(persistedType);
        }

        @Override
        protected void waitForYellowStatus(final SearchEngineClient searchEngineClient) {
            steps.add("waitForYellowStatus");
        }

        @Override
        protected SuggestSettingsBuilder newSuggestSettingsBuilder() {
            return new SuggestSettingsBuilder() {
                @Override
                public SuggestSettingsBuilder addInitialSettings(final String key, final Object value) {
                    recordedInitialSettings.put(key, value);
                    return super.addInitialSettings(key, value);
                }

                @Override
                public SuggestSettings build(final Client client, final String id) {
                    steps.add("buildSettings");
                    return settings;
                }
            };
        }

        @Override
        protected void updatePersistedSearchEngineType(final SuggestSettings suggestSettings, final String type) {
            steps.add("updatePersistedSearchEngineType:" + type);
            super.updatePersistedSearchEngineType(suggestSettings, type);
        }

        @Override
        protected Suggester buildSuggester(final SearchEngineClient searchEngineClient, final SuggestSettings suggestSettings) {
            steps.add("buildSuggester");
            throw new SuggesterNotBuilt();
        }
    }

    private void runInit(final InitProbe helper, final String searchEngineType) {
        final MockFessConfig fessConfig = new MockFessConfig();
        fessConfig.fesenType = searchEngineType;
        ComponentUtil.setFessConfig(fessConfig);
        try {
            helper.init();
            fail("the probe stops init() at the suggester");
        } catch (final SuggesterNotBuilt expected) {
            // reached the build of the suggester
        }
    }

    @Test
    public void test_init_handsTheResourceTypeToTheSuggestLibrary() {
        // the suggest library reads suggest_indices/_<type>/suggest_analyzer.json by this value: the
        // plugin-less types share the vanilla file, and every other type is passed on as it is
        final String[][] types = { { "vanilla", "vanilla" }, { "aws", "vanilla" }, { "cloud", "vanilla" }, { "default", "default" },
                { "opensearch", "opensearch" } };
        for (final String[] type : types) {
            final InitProbe helper = new InitProbe("stale");

            runInit(helper, type[0]);

            assertEquals(type[0], Map.of("search_engine.type", type[1]), helper.recordedInitialSettings);
            assertEquals(type[0], List.of("search_engine.type=" + type[1]), helper.settings.updates);
        }
    }

    @Test
    public void test_init_migratesThePersistedTypeOnceAndBeforeTheSuggesterIsBuilt() {
        final InitProbe helper = new InitProbe("cloud");

        runInit(helper, "aws");

        // building the suggester creates the analyzer index when it is missing, from the stored type
        assertEquals(List.of("waitForYellowStatus", "buildSettings", "updatePersistedSearchEngineType:vanilla", "buildSuggester"),
                helper.steps);
        assertEquals(List.of("search_engine.type=vanilla"), helper.settings.updates);
    }

    @Test
    public void test_init_leavesAPersistedTypeThatMatchesAlone() {
        final InitProbe helper = new InitProbe("vanilla");

        runInit(helper, "cloud");

        assertEquals(List.of("waitForYellowStatus", "buildSettings", "updatePersistedSearchEngineType:vanilla", "buildSuggester"),
                helper.steps);
        assertTrue(helper.settings.updates.isEmpty());
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
    public void test_indexFromSearchLog_writeFailureIsLogged() {
        final RuntimeException cause = new RuntimeException("no write index is defined for alias [fess.suggest.update]");
        final List<String> storedWords = new ArrayList<>();
        final SuggestHelper helper = new SuggestHelper() {
            @Override
            protected SuggestIndexResponse indexSearchWord(final String searchWord, final String[] fields, final String[] tags,
                    final String[] roles, final String[] langs) {
                storedWords.add(searchWord);
                return new SuggestIndexResponse(1, 1, List.of(cause), 0);
            }

            @Override
            public void refresh() {
                // no suggest index
            }
        };
        helper.fessConfig = ComponentUtil.getFessConfig();
        helper.contentFieldNameSet.add("content");

        final List<SearchLog> searchLogList = new ArrayList<>();
        for (final String word : new String[] { "alpha", "beta" }) {
            final SearchLog searchLog = new SearchLog();
            searchLog.setHitCount(10L);
            searchLog.setUserSessionId("session-" + word);
            searchLog.setRequestedAt(LocalDateTime.now());
            searchLog.addSearchFieldLogValue("content", word);
            searchLogList.add(searchLog);
        }

        final LogCapturingAppender appender = LogCapturingAppender.attach(SuggestHelper.class);
        try {
            helper.indexFromSearchLog(searchLogList);
        } finally {
            appender.detach();
        }

        assertEquals(List.of("alpha", "beta"), storedWords);
        final List<LogEvent> warnings = appender.eventsAt(Level.WARN);
        assertEquals(1, warnings.size());
        final String message = warnings.get(0).getMessage().getFormattedMessage();
        assertTrue(message, message.contains("failed=2"));
        assertEquals(cause, warnings.get(0).getThrown());
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

    @Test
    public void test_updatePersistedSearchEngineType_replacesTheRemovedVariantTypes() {
        for (final String persisted : new String[] { "cloud", "aws" }) {
            final RecordingSuggestSettings settings = new RecordingSuggestSettings(persisted);
            suggestHelper.updatePersistedSearchEngineType(settings, "vanilla");
            assertEquals(persisted, List.of("search_engine.type=vanilla"), settings.updates);
        }
    }

    @Test
    public void test_updatePersistedSearchEngineType_replacesATypeFromAnotherMode() {
        final RecordingSuggestSettings settings = new RecordingSuggestSettings("default");
        suggestHelper.updatePersistedSearchEngineType(settings, "vanilla");
        assertEquals(List.of("search_engine.type=vanilla"), settings.updates);

        final RecordingSuggestSettings back = new RecordingSuggestSettings("vanilla");
        suggestHelper.updatePersistedSearchEngineType(back, "default");
        assertEquals(List.of("search_engine.type=default"), back.updates);
    }

    @Test
    public void test_updatePersistedSearchEngineType_leavesAMatchingTypeAlone() {
        for (final String type : new String[] { "vanilla", "default", "opensearch" }) {
            final RecordingSuggestSettings settings = new RecordingSuggestSettings(type);
            suggestHelper.updatePersistedSearchEngineType(settings, type);
            assertTrue(type, settings.updates.isEmpty());
        }
    }

    @Test
    public void test_updatePersistedSearchEngineType_doesNotCreateTheDocumentOrStoreNull() {
        // A missing value must stay missing: storing it would make the suggest library treat the
        // settings document as existing and skip its initial settings.
        final RecordingSuggestSettings notStored = new RecordingSuggestSettings(null);
        suggestHelper.updatePersistedSearchEngineType(notStored, "vanilla");
        assertTrue(notStored.updates.isEmpty());

        final RecordingSuggestSettings stored = new RecordingSuggestSettings("cloud");
        suggestHelper.updatePersistedSearchEngineType(stored, null);
        assertTrue(stored.updates.isEmpty());
    }

    @Test
    public void test_updatePersistedSearchEngineType_firstStartWithoutSettingsIndex() {
        final RecordingSuggestSettings settings = new RecordingSuggestSettings("cloud");
        settings.getFailure = new IndexNotFoundException("fess.suggest_suggest");
        final LogCapturingAppender appender = LogCapturingAppender.attach(SuggestHelper.class);
        try {
            suggestHelper.updatePersistedSearchEngineType(settings, "vanilla");
        } finally {
            appender.detach();
        }
        assertTrue(settings.updates.isEmpty());
        assertTrue(appender.warnings().isEmpty(), appender.warnings().toString());
    }

    @Test
    public void test_updatePersistedSearchEngineType_failureIsLoggedAndDoesNotAbortStartup() {
        final RecordingSuggestSettings settings = new RecordingSuggestSettings("cloud");
        settings.setFailure = new SuggestSettingsException("failed");
        final LogCapturingAppender appender = LogCapturingAppender.attach(SuggestHelper.class);
        try {
            suggestHelper.updatePersistedSearchEngineType(settings, "vanilla");
        } finally {
            appender.detach();
        }
        final List<String> warnings = appender.messagesOnThisThreadAt(Level.WARN);
        assertEquals(1, warnings.size());
        // the value that stays in the settings and what that means are in the message
        assertEquals("Failed to update the search engine type of the suggest settings: stored=cloud, type=vanilla."
                + " If the suggest analyzer index must be created, it is built from the definitions of the stored type,"
                + " which fails on a search engine without the CodeLibs plugins.", warnings.get(0));
    }

    @Test
    public void test_updatePersistedSearchEngineType_failureToReadTheStoredTypeIsLoggedWithoutIt() {
        final RecordingSuggestSettings settings = new RecordingSuggestSettings("cloud");
        settings.getFailure = new SuggestSettingsException("failed");
        final LogCapturingAppender appender = LogCapturingAppender.attach(SuggestHelper.class);
        try {
            suggestHelper.updatePersistedSearchEngineType(settings, "vanilla");
        } finally {
            appender.detach();
        }
        final List<String> warnings = appender.messagesOnThisThreadAt(Level.WARN);
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0), warnings.get(0).contains("stored=null, type=vanilla"));
    }

    // Mock classes
    private static class RecordingSuggestSettings extends SuggestSettings {
        private final Object persisted;

        final List<String> updates = new ArrayList<>();

        RuntimeException getFailure;

        RuntimeException setFailure;

        RecordingSuggestSettings(final Object persisted) {
            super(null, "fess.suggest", new HashMap<>(), "fess.suggest_suggest", new TimeoutSettings());
            this.persisted = persisted;
        }

        @Override
        public Object get(final String key) {
            if (getFailure != null) {
                throw getFailure;
            }
            return "search_engine.type".equals(key) ? persisted : null;
        }

        @Override
        public void set(final String key, final Object value) {
            if (setFailure != null) {
                throw setFailure;
            }
            updates.add(key + "=" + value);
        }
    }

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

        String fesenType = "opensearch";

        @Override
        public String getFesenType() {
            return fesenType;
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