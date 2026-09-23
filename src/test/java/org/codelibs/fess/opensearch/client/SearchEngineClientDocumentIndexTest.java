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
import java.util.Map;
import java.util.Set;

import org.codelibs.fesen.opensearch.OpenSearchStatusException;
import org.codelibs.fesen.opensearch.action.admin.indices.alias.Alias;
import org.codelibs.fesen.opensearch.action.admin.indices.create.CreateIndexRequest;
import org.codelibs.fesen.opensearch.core.rest.RestStatus;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Tests the first-boot creation of the document index when several instances that share index
 * names start at the same time.
 */
public class SearchEngineClientDocumentIndexTest extends UnitFessTestCase {

    private TestFessConfig fessConfig;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        fessConfig = new TestFessConfig();
        ComponentUtil.setFessConfig(fessConfig);
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    @Test
    public void test_getDocumentIndexAliases_marksUpdateAliasAsWriteIndex() {
        final Map<String, Object> aliases = new SearchEngineClient().getDocumentIndexAliases("fess");

        assertEquals(Set.of("fess.search", "fess.update"), aliases.keySet());
        assertEquals(Map.of("is_write_index", true), aliases.get("fess.update"));
        assertEquals(Map.of(), aliases.get("fess.search"));
    }

    @Test
    public void test_getDocumentIndexAliases_usesConfiguredAliasNames() {
        fessConfig.searchIndex = "site1.search";
        fessConfig.updateIndex = "site1.update";

        final Map<String, Object> aliases = new SearchEngineClient().getDocumentIndexAliases("fess");

        assertEquals(Set.of("site1.search", "site1.update"), aliases.keySet());
        assertEquals(Map.of("is_write_index", true), aliases.get("site1.update"));
        assertEquals(Map.of(), aliases.get("site1.search"));
    }

    @Test
    public void test_getDocumentIndexAliases_isAcceptedByCreateIndexRequest() {
        final Map<String, Object> aliases = new SearchEngineClient().getDocumentIndexAliases("fess");

        final CreateIndexRequest request = new CreateIndexRequest("fess.20260101000000000").aliases(aliases);

        // Alias has no getters; its JSON form is what the HTTP client sends
        final List<String> sent = new ArrayList<>();
        for (final Alias alias : request.aliases()) {
            sent.add(alias.toString().replaceAll("\\s", ""));
        }
        assertEquals(2, sent.size(), sent.toString());
        assertTrue(sent.contains("{\"fess.update\":{\"is_write_index\":true}}"), sent.toString());
        assertTrue(sent.contains("{\"fess.search\":{}}"), sent.toString());
    }

    @Test
    public void test_resolveAliasName() {
        final SearchEngineClient client = new SearchEngineClient();
        assertEquals("fess.search", client.resolveAliasName("fess", "fess.search"));
        assertEquals("fess.update", client.resolveAliasName("fess", "fess.update"));
        assertEquals("fess_config", client.resolveAliasName("fess_config.web_config", "fess_config"));
        assertEquals("fess_basic_config", client.resolveAliasName("fess_config.web_config", "fess_basic_config"));
        assertEquals("fess_user", client.resolveAliasName("fess_user.user", "fess_user"));
        assertEquals("fess_log", client.resolveAliasName("fess_log.search_log", "fess_log"));

        fessConfig.searchIndex = "site1.search";
        fessConfig.updateIndex = "site1.update";
        fessConfig.configIndex = "site1_config";
        fessConfig.userIndex = "site1_user";
        fessConfig.logIndex = "site1_log";
        assertEquals("site1.search", client.resolveAliasName("fess", "fess.search"));
        assertEquals("site1.update", client.resolveAliasName("fess", "fess.update"));
        assertEquals("site1_config", client.resolveAliasName("fess_config.web_config", "fess_config"));
        assertEquals("basic_site1_config", client.resolveAliasName("fess_config.web_config", "fess_basic_config"));
        assertEquals("site1_user", client.resolveAliasName("fess_user.user", "fess_user"));
        assertEquals("site1_log", client.resolveAliasName("fess_log.search_log", "fess_log"));
    }

    @Test
    public void test_isIndexCreatedByAnotherProcess() {
        final SearchEngineClient client = new SearchEngineClient();
        assertTrue(client.isIndexCreatedByAnotherProcess(new OpenSearchStatusException(
                "OpenSearch exception [type=resource_already_exists_exception, reason=index [fess_config.web_config/abc] already exists]",
                RestStatus.BAD_REQUEST, null)));
        assertTrue(client.isIndexCreatedByAnotherProcess(new OpenSearchStatusException(
                "OpenSearch exception [type=illegal_state_exception, reason=alias [fess.update] has more than one write index [fess.20260101000000001,fess.20260101000000000]]",
                RestStatus.INTERNAL_SERVER_ERROR, null)));
        assertTrue(client.isIndexCreatedByAnotherProcess(new FessSystemException("wrapped",
                new OpenSearchStatusException(
                        "OpenSearch exception [type=resource_already_exists_exception, reason=index [fess.x/abc] already exists]",
                        RestStatus.BAD_REQUEST, null))));

        assertFalse(client.isIndexCreatedByAnotherProcess(
                new OpenSearchStatusException("OpenSearch exception [type=invalid_index_name_exception, reason=Invalid index name [FESS]]",
                        RestStatus.BAD_REQUEST, null)));
        assertFalse(client.isIndexCreatedByAnotherProcess(
                new OpenSearchStatusException("OpenSearch exception [type=illegal_argument_exception, reason=unknown setting [index.knn]]",
                        RestStatus.BAD_REQUEST, null)));
        assertFalse(client.isIndexCreatedByAnotherProcess(new RuntimeException((String) null)));
    }

    @Test
    public void test_setUpDocumentIndex_existingAliasIsReused() {
        final TestSearchEngineClient client = new TestSearchEngineClient();
        client.existingIndices.add("fess.update");

        assertEquals("fess.20250101000000000", client.setUpDocumentIndex(fessConfig, "fess"));
        assertTrue(client.createCalls.isEmpty());
    }

    @Test
    public void test_setUpDocumentIndex_createsIndexWithWriteAlias() {
        final TestSearchEngineClient client = new TestSearchEngineClient();

        assertEquals("fess.20260101000000000", client.setUpDocumentIndex(fessConfig, "fess"));
        assertEquals(1, client.createCalls.size());
        assertEquals("fess.20260101000000000", client.createCalls.get(0));
        assertEquals(Map.of("is_write_index", true), client.createAliases.get("fess.update"));
        assertEquals(0, client.resolveCalls);
    }

    @Test
    public void test_setUpDocumentIndex_adoptsIndexCreatedByAnotherProcess() {
        final TestSearchEngineClient client = new TestSearchEngineClient();
        client.createResult = false;
        // another instance created its index and aliases between the existence check and the create request
        client.onCreate = () -> client.existingIndices.add("fess.update");

        assertEquals("fess.20250101000000000", client.setUpDocumentIndex(fessConfig, "fess"));
        assertEquals(1, client.createCalls.size());
        assertEquals(1, client.resolveCalls);
    }

    @Test
    public void test_setUpDocumentIndex_createFailureKeepsNewIndexName() {
        final TestSearchEngineClient client = new TestSearchEngineClient();
        client.createResult = false;

        assertEquals("fess.20260101000000000", client.setUpDocumentIndex(fessConfig, "fess"));
        assertEquals(0, client.resolveCalls);
    }

    private static class TestSearchEngineClient extends SearchEngineClient {
        final Set<String> existingIndices = new HashSet<>();
        final List<String> createCalls = new ArrayList<>();
        Map<String, Object> createAliases;
        boolean createResult = true;
        Runnable onCreate = () -> {};
        int resolveCalls = 0;

        @Override
        public boolean existsIndex(final String indexName) {
            return existingIndices.contains(indexName);
        }

        @Override
        protected String generateNewIndexName(final String configIndex) {
            return configIndex + ".20260101000000000";
        }

        @Override
        protected boolean createIndex(final String index, final String indexName, final String numberOfShards,
                final String autoExpandReplicas, final boolean uploadConfig, final Map<String, Object> aliases) {
            createCalls.add(indexName);
            createAliases = aliases;
            onCreate.run();
            return createResult;
        }

        @Override
        protected String getDocumentIndexName(final FessConfig fessConfig, final String configIndex) {
            resolveCalls++;
            return configIndex + ".20250101000000000";
        }
    }

    private static class TestFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;

        String searchIndex = "fess.search";
        String updateIndex = "fess.update";
        String configIndex = "fess_config";
        String userIndex = "fess_user";
        String logIndex = "fess_log";

        @Override
        public String getIndexDocumentSearchIndex() {
            return searchIndex;
        }

        @Override
        public String getIndexDocumentUpdateIndex() {
            return updateIndex;
        }

        @Override
        public String getIndexConfigIndex() {
            return configIndex;
        }

        @Override
        public String getIndexUserIndex() {
            return userIndex;
        }

        @Override
        public String getIndexLogIndex() {
            return logIndex;
        }

        @Override
        public String getFesenType() {
            return "opensearch";
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
