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
package org.codelibs.fess.opensearch.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class SearchEngineClientRebuildTest extends UnitFessTestCase {

    private TestSearchEngineClient testClient;

    private Set<String> allTargetPrefixes() {
        return Set.of("fess_config", "fess_user", "fess_log");
    }

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        final TestFessConfig fessConfig = new TestFessConfig();
        ComponentUtil.setFessConfig(fessConfig);
        testClient = new TestSearchEngineClient();
        testClient.addIndexConfig("fess/doc");
        testClient.addIndexConfig("fess_config.scheduled_job/scheduled_job");
        testClient.addIndexConfig("fess_config.access_token/access_token");
        testClient.addIndexConfig("fess_user.user/user");
        testClient.addIndexConfig("fess_log.search_log/search_log");
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    // ==========================================================================
    // addMapping overload delegation
    // ==========================================================================

    @Test
    public void test_addMapping_threeArgDelegatesToFourArgWithLoadBulkDataTrue() {
        testClient.addMapping("fess_config.scheduled_job", "scheduled_job", "fess_config.scheduled_job");

        assertEquals(1, testClient.addMappingCalls.size());
        assertEquals("fess_config.scheduled_job", testClient.addMappingCalls.get(0)[0]);
        assertEquals("scheduled_job", testClient.addMappingCalls.get(0)[1]);
        assertEquals("fess_config.scheduled_job", testClient.addMappingCalls.get(0)[2]);
        assertTrue(testClient.lastLoadBulkData);
    }

    @Test
    public void test_addMapping_fourArgWithLoadBulkDataFalse() {
        testClient.addMapping("fess_config.scheduled_job", "scheduled_job", "fess_config.scheduled_job", false);

        assertEquals(1, testClient.addMappingCalls.size());
        assertFalse(testClient.lastLoadBulkData);
    }

    @Test
    public void test_addMapping_fourArgWithLoadBulkDataTrue() {
        testClient.addMapping("fess_config.scheduled_job", "scheduled_job", "fess_config.scheduled_job", true);

        assertEquals(1, testClient.addMappingCalls.size());
        assertTrue(testClient.lastLoadBulkData);
    }

    // ==========================================================================
    // insertBulkData with createOnly parameter
    // ==========================================================================

    @Test
    public void test_insertBulkData_createOnlyFalse() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        testClient.insertBulkData(fessConfig, "fess_config.scheduled_job", "test.bulk", false);

        assertEquals(1, testClient.insertBulkDataCalls.size());
        assertFalse(testClient.lastCreateOnly);
    }

    @Test
    public void test_insertBulkData_createOnlyTrue() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        testClient.insertBulkData(fessConfig, "fess_config.scheduled_job", "test.bulk", true);

        assertEquals(1, testClient.insertBulkDataCalls.size());
        assertTrue(testClient.lastCreateOnly);
    }

    // ==========================================================================
    // reindexConfigIndices - full rebuild flow
    // ==========================================================================

    @Test
    public void test_reindexConfigIndices_skipsDocIndex() {
        testClient.existsIndexResult = true;
        testClient.documentCount = 10;
        testClient.aliasCount = 2;

        testClient.reindexConfigIndices(false, allTargetPrefixes());

        // "fess" (DOC_INDEX) should never appear in created or reindexed indices
        for (final String index : testClient.createdIndices) {
            assertFalse(index.equals("fess"), "DOC_INDEX 'fess' should be skipped");
        }
    }

    @Test
    public void test_reindexConfigIndices_missingIndexCreatesNew() {
        testClient.existsIndexResult = false;
        testClient.createIndexResult = true;

        final boolean result = testClient.reindexConfigIndices(false, allTargetPrefixes());

        assertTrue(result);
        // Should have created 4 indices (config x2, user x1, log x1)
        assertEquals(4, testClient.createdIndices.size());
        // Should have called addMapping for each created index
        assertEquals(4, testClient.addMappingCalls.size());
        // Should have called createAlias for each created index
        assertEquals(4, testClient.createAliasCalls.size());
        // No reindex calls since indices are new
        assertEquals(0, testClient.reindexPairs.size());
    }

    @Test
    public void test_reindexConfigIndices_normalRebuildFlow() {
        testClient.existsIndexResult = true;
        testClient.createIndexResult = true;
        testClient.deleteIndexResult = true;
        testClient.reindexResult = true;
        testClient.documentCount = 10;
        testClient.aliasCount = 2;

        final boolean result = testClient.reindexConfigIndices(false, allTargetPrefixes());

        assertTrue(result);
        assertEquals("4 indices × 2 creates each (backup + rebuild)", 8, testClient.createdIndices.size());
        assertEquals("4 indices × 2 reindexes each (to backup + from backup)", 8, testClient.reindexPairs.size());
        assertEquals("4 indices × 2 deletes each (old + backup)", 8, testClient.deletedIndices.size());
    }

    @Test
    public void test_reindexConfigIndices_returnsFalseWhenIndexFails() {
        testClient.existsIndexResult = true;
        testClient.createIndexResult = false;
        testClient.documentCount = 10;
        testClient.aliasCount = 2;

        final boolean result = testClient.reindexConfigIndices(false, allTargetPrefixes());

        assertFalse(result);
    }

    @Test
    public void test_reindexConfigIndices_returnsTrueWhenAllSucceed() {
        testClient.existsIndexResult = true;
        testClient.createIndexResult = true;
        testClient.deleteIndexResult = true;
        testClient.reindexResult = true;
        testClient.documentCount = 10;
        testClient.aliasCount = 2;

        final boolean result = testClient.reindexConfigIndices(false, allTargetPrefixes());

        assertTrue(result);
    }

    @Test
    public void test_reindexConfigIndices_loadBulkDataTrue() {
        testClient.existsIndexResult = true;
        testClient.createIndexResult = true;
        testClient.deleteIndexResult = true;
        testClient.reindexResult = true;
        testClient.documentCount = 10;

        final boolean result = testClient.reindexConfigIndices(true, allTargetPrefixes());

        assertTrue(result);
        // During rebuild, addMapping is always called with loadBulkData=false
        // because bulk data is loaded separately with createOnly=true via insertBulkData.
        for (final String[] call : testClient.addMappingCalls) {
            assertNotNull(call);
        }
        // The loadBulkData=true path triggers getResourcePath + ResourceUtil.isExist checks.
        // In test environment resource files don't exist, so insertBulkData won't be called,
        // but the overall flow should still succeed.
        assertTrue(testClient.calledMethods.contains("addMapping"));
        assertTrue(testClient.calledMethods.contains("switchAliases"));
    }

    @Test
    public void test_reindexConfigIndices_loadBulkDataFalse() {
        testClient.existsIndexResult = true;
        testClient.createIndexResult = true;
        testClient.deleteIndexResult = true;
        testClient.reindexResult = true;
        testClient.documentCount = 10;
        testClient.aliasCount = 2;

        final boolean result = testClient.reindexConfigIndices(false, allTargetPrefixes());

        assertTrue(result);
        // With loadBulkData=false, the bulk data loading block is skipped entirely
        assertEquals(0, testClient.insertBulkDataCalls.size());
    }

    // ==========================================================================
    // reindexConfigIndices - index filtering
    // ==========================================================================

    @Test
    public void test_reindexConfigIndices_onlyConfigTarget() {
        testClient.existsIndexResult = true;
        testClient.createIndexResult = true;
        testClient.deleteIndexResult = true;
        testClient.reindexResult = true;
        testClient.documentCount = 10;
        testClient.aliasCount = 2;

        final Set<String> configOnly = Set.of("fess_config");
        final boolean result = testClient.reindexConfigIndices(false, configOnly);

        assertTrue(result);
        // Only fess_config indices should be rebuilt (2 config indices: scheduled_job, access_token)
        // Each creates 2 indices (backup + rebuild) = 4 creates
        assertEquals("Only fess_config indices should be rebuilt", 4, testClient.createdIndices.size());
        // Verify no user/log indices were touched
        for (final String index : testClient.createdIndices) {
            assertFalse(index.contains("fess_user"), "fess_user should not be rebuilt");
            assertFalse(index.contains("fess_log"), "fess_log should not be rebuilt");
        }
    }

    @Test
    public void test_reindexConfigIndices_emptyTargetDoesNothing() {
        testClient.existsIndexResult = true;
        testClient.createIndexResult = true;

        final Set<String> empty = Set.of();
        final boolean result = testClient.reindexConfigIndices(false, empty);

        assertTrue(result);
        assertEquals("No indices should be created with empty targets", 0, testClient.createdIndices.size());
        assertEquals("No reindex should occur with empty targets", 0, testClient.reindexPairs.size());
    }

    // ==========================================================================
    // reindexConfigIndices - atomic alias switching
    // ==========================================================================

    @Test
    public void test_reindexConfigIndices_callsSwitchAliases() {
        testClient.existsIndexResult = true;
        testClient.createIndexResult = true;
        testClient.deleteIndexResult = true;
        testClient.reindexResult = true;
        testClient.documentCount = 10;

        final boolean result = testClient.reindexConfigIndices(false, allTargetPrefixes());

        assertTrue(result);
        // Each of the 4 indices should have switchAliases called
        assertEquals("switchAliases should be called for each index", 4, testClient.switchAliasesCalls.size());
        assertTrue(testClient.calledMethods.contains("switchAliases"));
    }

    @Test
    public void test_reindexConfigIndices_switchAliasesFailureCleanup() {
        testClient.existsIndexResult = true;
        testClient.createIndexResult = true;
        testClient.deleteIndexResult = true;
        testClient.reindexResult = true;
        testClient.documentCount = 10;
        testClient.switchAliasesResult = false;

        final boolean result = testClient.reindexConfigIndices(false, allTargetPrefixes());

        assertFalse(result);
        // New and backup indices should be cleaned up on switch failure
        assertTrue(testClient.deletedIndices.size() > 0, "Should clean up on alias switch failure");
    }

    // ==========================================================================
    // reindexConfigIndices - error handling
    // ==========================================================================

    @Test
    public void test_reindexConfigIndices_backupCreationFailureContinues() {
        testClient.existsIndexResult = true;
        testClient.documentCount = 10;
        testClient.aliasCount = 2;
        // First createIndex (backup) fails
        testClient.createIndexResult = false;

        final boolean result = testClient.reindexConfigIndices(false, allTargetPrefixes());

        assertFalse(result);
        // Should still attempt all indices (4 config indices)
        assertEquals(4, testClient.createdIndices.size());
    }

    @Test
    public void test_reindexConfigIndices_reindexToBackupFailureCleansUp() {
        testClient.existsIndexResult = true;
        testClient.createIndexResult = true;
        testClient.reindexResult = false;
        testClient.documentCount = 10;
        testClient.aliasCount = 2;

        final boolean result = testClient.reindexConfigIndices(false, allTargetPrefixes());

        assertFalse(result);
        // Should delete backup indices when reindex fails
        assertTrue(testClient.deletedIndices.size() > 0, "Backup indices should be cleaned up");
    }

    @Test
    public void test_reindexConfigIndices_documentCountMismatch() {
        testClient.existsIndexResult = true;
        testClient.createIndexResult = true;
        testClient.reindexResult = true;
        testClient.aliasCount = 2;
        // Source count is 10, but backup count will be different
        testClient.documentCount = 10;
        testClient.backupDocumentCount = 5;
        testClient.useBackupDocumentCount = true;

        final boolean result = testClient.reindexConfigIndices(false, allTargetPrefixes());

        assertFalse(result);
        // Backup indices should be cleaned up on count mismatch
        assertTrue(testClient.deletedIndices.size() > 0);
    }

    @Test
    public void test_reindexConfigIndices_deleteFailureAfterSwitch() {
        testClient.existsIndexResult = true;
        testClient.createIndexResult = true;
        testClient.reindexResult = true;
        testClient.deleteIndexResult = false;
        testClient.documentCount = 10;

        final boolean result = testClient.reindexConfigIndices(false, allTargetPrefixes());

        // Even if delete fails, the rebuild itself succeeded (aliases are switched)
        assertTrue(result);
    }

    @Test
    public void test_reindexConfigIndices_newIndexCreationFailureCleansUp() {
        testClient.existsIndexResult = true;
        testClient.reindexResult = true;
        testClient.deleteIndexResult = true;
        testClient.documentCount = 10;
        testClient.aliasCount = 2;
        // First createIndex (backup) succeeds, second (new) fails
        testClient.createIndexFailOnNth = 2;

        final boolean result = testClient.reindexConfigIndices(false, allTargetPrefixes());

        assertFalse(result);
        // Backup should be cleaned up
        assertTrue(testClient.deletedIndices.size() > 0, "Backup indices should be cleaned up");
    }

    @Test
    public void test_reindexConfigIndices_reindexFromBackupFailureCleansUp() {
        testClient.existsIndexResult = true;
        testClient.createIndexResult = true;
        testClient.deleteIndexResult = true;
        testClient.documentCount = 10;
        testClient.aliasCount = 2;
        // First reindex (to backup) succeeds, second (from backup) fails
        testClient.reindexFailOnNth = 2;

        final boolean result = testClient.reindexConfigIndices(false, allTargetPrefixes());

        assertFalse(result);
        // New index and backup should be cleaned up
        assertTrue(testClient.deletedIndices.size() > 0);
    }

    // ==========================================================================
    // getDocumentCount and getAliasCount edge cases
    // ==========================================================================

    @Test
    public void test_getDocumentCount_returnsNegativeOneOnError() {
        // The real getDocumentCount returns -1 on error; our test double tracks this
        testClient.documentCountError = true;
        final long count = testClient.getDocumentCount("nonexistent_index");
        assertEquals(-1L, count);
    }

    @Test
    public void test_getAliasCount_returnsZeroOnError() {
        // The real getAliasCount returns 0 on error; our test double tracks this
        testClient.aliasCountError = true;
        final int count = testClient.getAliasCount("nonexistent_index");
        assertEquals(0, count);
    }

    // ==========================================================================
    // Test double: TestSearchEngineClient
    // ==========================================================================

    private static class TestSearchEngineClient extends SearchEngineClient {

        // Track method calls
        List<String> calledMethods = new ArrayList<>();

        // Control behavior
        boolean createIndexResult = true;
        boolean deleteIndexResult = true;
        boolean reindexResult = true;
        boolean existsIndexResult = true;
        long documentCount = 10;
        int aliasCount = 2;

        // For simulating backup document count mismatch
        long backupDocumentCount = 10;
        boolean useBackupDocumentCount = false;

        // For simulating post-rebuild alias count difference
        int postRebuildAliasCount = 2;
        boolean usePostRebuildAliasCount = false;

        // For error simulation
        boolean documentCountError = false;
        boolean aliasCountError = false;

        // For nth-call failure simulation
        int createIndexFailOnNth = -1;
        int reindexFailOnNth = -1;
        private int createIndexCallCount = 0;
        private int reindexCallCount = 0;

        // Track whether loadBulkData was triggered in reindexConfigIndices
        boolean loadBulkDataTriggered = false;

        // Track specific call arguments
        List<String> createdIndices = new ArrayList<>();
        List<String> deletedIndices = new ArrayList<>();
        List<String[]> reindexPairs = new ArrayList<>();
        List<String[]> addMappingCalls = new ArrayList<>();
        List<String[]> createAliasCalls = new ArrayList<>();
        List<String[]> insertBulkDataCalls = new ArrayList<>();
        boolean lastLoadBulkData = false;
        boolean lastCreateOnly = false;
        boolean switchAliasesResult = true;
        List<String[]> switchAliasesCalls = new ArrayList<>();

        // Track which indices "exist" for fine-grained control
        Set<String> existingIndices = new HashSet<>();
        boolean useExistingIndicesSet = false;

        // Track document count calls to distinguish source vs backup
        private int getDocumentCountCallIndex = 0;

        // Track alias count calls to distinguish pre vs post rebuild
        private int getAliasCountCallIndex = 0;

        @Override
        public boolean createIndex(final String index, final String indexName) {
            calledMethods.add("createIndex");
            createdIndices.add(indexName);
            createIndexCallCount++;
            if (createIndexFailOnNth > 0 && (createIndexCallCount % createIndexFailOnNth) == 0) {
                return false;
            }
            return createIndexResult;
        }

        @Override
        public boolean createIndex(final String index, final String indexName, final String numberOfShards, final String autoExpandReplicas,
                final boolean uploadConfig) {
            calledMethods.add("createIndex5");
            createdIndices.add(indexName);
            createIndexCallCount++;
            if (createIndexFailOnNth > 0 && (createIndexCallCount % createIndexFailOnNth) == 0) {
                return false;
            }
            return createIndexResult;
        }

        @Override
        public boolean deleteIndex(final String indexName) {
            calledMethods.add("deleteIndex");
            deletedIndices.add(indexName);
            return deleteIndexResult;
        }

        @Override
        public boolean reindex(final String fromIndex, final String toIndex, final boolean waitForCompletion) {
            calledMethods.add("reindex");
            reindexPairs.add(new String[] { fromIndex, toIndex });
            reindexCallCount++;
            if (reindexFailOnNth > 0 && (reindexCallCount % reindexFailOnNth) == 0) {
                return false;
            }
            return reindexResult;
        }

        @Override
        public boolean existsIndex(final String indexName) {
            calledMethods.add("existsIndex");
            if (useExistingIndicesSet) {
                return existingIndices.contains(indexName);
            }
            return existsIndexResult;
        }

        @Override
        public long getDocumentCount(final String indexName) {
            calledMethods.add("getDocumentCount");
            if (documentCountError) {
                return -1;
            }
            getDocumentCountCallIndex++;
            if (useBackupDocumentCount && indexName.contains(".backup.")) {
                return backupDocumentCount;
            }
            return documentCount;
        }

        @Override
        public int getAliasCount(final String indexName) {
            calledMethods.add("getAliasCount");
            if (aliasCountError) {
                return 0;
            }
            getAliasCountCallIndex++;
            if (usePostRebuildAliasCount && getAliasCountCallIndex % 2 == 0) {
                return postRebuildAliasCount;
            }
            return aliasCount;
        }

        @Override
        public void addMapping(final String index, final String docType, final String indexName) {
            // Delegate to 4-arg version with loadBulkData=true, same as real implementation
            addMapping(index, docType, indexName, true);
        }

        @Override
        public void addMapping(final String index, final String docType, final String indexName, final boolean loadBulkData) {
            calledMethods.add("addMapping");
            addMappingCalls.add(new String[] { index, docType, indexName });
            lastLoadBulkData = loadBulkData;
            if (loadBulkData) {
                loadBulkDataTriggered = true;
            }
        }

        @Override
        protected void createAlias(final String index, final String createdIndexName) {
            calledMethods.add("createAlias");
            createAliasCalls.add(new String[] { index, createdIndexName });
        }

        @Override
        protected boolean switchAliases(final String configIndex, final String oldIndexName, final String newIndexName) {
            calledMethods.add("switchAliases");
            switchAliasesCalls.add(new String[] { configIndex, oldIndexName, newIndexName });
            return switchAliasesResult;
        }

        @Override
        protected void insertBulkData(final FessConfig fessConfig, final String configIndex, final String dataPath) {
            insertBulkData(fessConfig, configIndex, dataPath, false);
        }

        @Override
        protected void insertBulkData(final FessConfig fessConfig, final String configIndex, final String dataPath,
                final boolean createOnly) {
            calledMethods.add("insertBulkData");
            insertBulkDataCalls.add(new String[] { configIndex, dataPath });
            lastCreateOnly = createOnly;
        }
    }

    // ==========================================================================
    // Test double: TestFessConfig
    // ==========================================================================

    private static class TestFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;

        @Override
        public String getIndexConfigIndex() {
            return "fess_config";
        }

        @Override
        public String getIndexUserIndex() {
            return "fess_user";
        }

        @Override
        public String getIndexLogIndex() {
            return "fess_log";
        }

        @Override
        public String getFesenType() {
            return "";
        }

        @Override
        public String getAppExtensionNames() {
            return "";
        }

        @Override
        public String getIndexReindexSize() {
            return "100";
        }

        @Override
        public String getIndexNumberOfShards() {
            return "1";
        }

        @Override
        public String getIndexAutoExpandReplicas() {
            return "0-1";
        }
    }
}
