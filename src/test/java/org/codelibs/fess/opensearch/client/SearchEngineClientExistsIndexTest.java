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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fesen.opensearch.action.ActionRequest;
import org.codelibs.fesen.opensearch.action.ActionType;
import org.codelibs.fesen.opensearch.action.admin.indices.exists.indices.IndicesExistsAction;
import org.codelibs.fesen.opensearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.codelibs.fesen.opensearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.codelibs.fesen.opensearch.action.support.PlainActionFuture;
import org.codelibs.fesen.opensearch.common.action.ActionFuture;
import org.codelibs.fesen.opensearch.core.action.ActionResponse;
import org.codelibs.fesen.opensearch.transport.client.AdminClient;
import org.codelibs.fesen.opensearch.transport.client.ClusterAdminClient;
import org.codelibs.fesen.opensearch.transport.client.IndicesAdminClient;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Tests that {@link SearchEngineClient#existsIndex(String)} answers only what the search engine
 * answered, so that a failed check is never taken for a missing index.
 */
public class SearchEngineClientExistsIndexTest extends UnitFessTestCase {

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
    public void test_existsIndex_existingIndex() {
        final ExistsClient client = new ExistsClient();
        client.engine.exists = true;

        assertTrue(client.existsIndex("fess.update"));
    }

    @Test
    public void test_existsIndex_missingIndexIsFalse() {
        final ExistsClient client = new ExistsClient();
        client.engine.exists = false;

        assertFalse(client.existsIndex("fess.update"));
    }

    @Test
    public void test_existsIndex_invalidTimeoutIsNotAMissingIndex() {
        fessConfig.searchTimeout = "3mm";
        final ExistsClient client = new ExistsClient();
        client.engine.exists = true;

        final FessSystemException e = assertThrows(FessSystemException.class, () -> client.existsIndex("fess.update"));
        assertTrue(e.getMessage(), e.getMessage().contains("indexName=fess.update"));
        assertTrue(e.getMessage(), e.getMessage().contains("3mm"));
        assertNotNull(e.getCause());
    }

    @Test
    public void test_existsIndex_engineFailureIsNotAMissingIndex() {
        final ExistsClient client = new ExistsClient();
        client.engine.failure = new IllegalStateException("engine is not reachable");

        final FessSystemException e = assertThrows(FessSystemException.class, () -> client.existsIndex("fess.update"));
        assertTrue(e.getMessage(), e.getMessage().contains("engine is not reachable"));
        assertSame(client.engine.failure, e.getCause());
    }

    @Test
    public void test_setUpDocumentIndex_doesNotCreateIndexWhenTheCheckFails() {
        fessConfig.searchTimeout = "3mm";
        final ExistsClient client = new ExistsClient();

        assertThrows(FessSystemException.class, () -> client.setUpDocumentIndex(fessConfig, "fess"));
        assertTrue(client.createCalls.toString(), client.createCalls.isEmpty());
    }

    @Test
    public void test_setUpDocumentIndex_createsIndexWhenItIsMissing() {
        final ExistsClient client = new ExistsClient();
        client.engine.exists = false;

        assertEquals("fess.20260101000000000", client.setUpDocumentIndex(fessConfig, "fess"));
        assertEquals(List.of("fess.20260101000000000"), client.createCalls);
    }

    /** A search engine client whose delegate answers the existence check and records index creation. */
    private static class ExistsClient extends SearchEngineClient {
        final ExistsEngine engine = new ExistsEngine();
        final List<String> createCalls = new ArrayList<>();

        ExistsClient() {
            this.client = engine;
        }

        @Override
        protected String generateNewIndexName(final String configIndex) {
            return configIndex + ".20260101000000000";
        }

        @Override
        protected boolean createIndex(final String index, final String indexName, final String numberOfShards,
                final String autoExpandReplicas, final boolean uploadConfig, final Map<String, Object> aliases) {
            createCalls.add(indexName);
            return true;
        }
    }

    /** Plays the HTTP client: it answers the index existence request, and nothing else. */
    private static class ExistsEngine extends SearchEngineClient {
        /** What the existence request answers. */
        boolean exists = true;

        /** When set, the existence request fails with it. */
        RuntimeException failure;

        private final IndicesAdminClient indices = (IndicesAdminClient) Proxy.newProxyInstance(IndicesAdminClient.class.getClassLoader(),
                new Class<?>[] { IndicesAdminClient.class }, (proxy, method, args) -> {
                    if ("prepareExists".equals(method.getName())) {
                        return new IndicesExistsRequestBuilder(this, IndicesExistsAction.INSTANCE, (String[]) args[0]);
                    }
                    throw new UnsupportedOperationException(method.getName());
                });

        private final AdminClient admin = new AdminClient() {
            @Override
            public ClusterAdminClient cluster() {
                throw new UnsupportedOperationException("cluster");
            }

            @Override
            public IndicesAdminClient indices() {
                return indices;
            }
        };

        @Override
        public AdminClient admin() {
            return admin;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <Request extends ActionRequest, Response extends ActionResponse> ActionFuture<Response> execute(
                final ActionType<Response> action, final Request request) {
            final PlainActionFuture<Response> future = PlainActionFuture.newFuture();
            if (failure != null) {
                future.onFailure(failure);
            } else {
                future.onResponse((Response) new IndicesExistsResponse(exists));
            }
            return future;
        }
    }

    private static class TestFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;

        String searchTimeout = "3m";

        @Override
        public String getIndexSearchTimeout() {
            return searchTimeout;
        }

        @Override
        public String getIndexDocumentSearchIndex() {
            return "fess.search";
        }

        @Override
        public String getIndexDocumentUpdateIndex() {
            return "fess.update";
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
