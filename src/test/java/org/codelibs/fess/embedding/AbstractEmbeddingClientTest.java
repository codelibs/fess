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
package org.codelibs.fess.embedding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class AbstractEmbeddingClientTest extends UnitFessTestCase {

    @Override
    public void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }

    // Literal pin: these system-property keys are external operator configuration. Renaming a
    // constant compiles cleanly while silently orphaning existing config, so the raw VALUES are
    // pinned here and any drift must redden a test.
    @Test
    public void test_externalContractLiterals() {
        assertEquals("content_chunker.enabled", AbstractEmbeddingClient.CONTENT_CHUNKER_ENABLED_PROPERTY);
        assertEquals("content_chunker.embedding.name", AbstractEmbeddingClient.EMBEDDING_NAME_PROPERTY);
        assertEquals("content_chunker.embedding.dimension", AbstractEmbeddingClient.EMBEDDING_DIMENSION_PROPERTY);
    }

    // ========== Proxy configuration tests ==========

    @Test
    public void test_proxyGetters_defaultDelegatesToFessConfig() {
        final TestEmbeddingClient client = new TestEmbeddingClient();
        // Default impls read from FessConfig. In the test environment, http.proxy.host
        // is empty (the property exists with a default empty value), so the getters
        // return empty/default values without throwing.
        assertNotNull(client.getProxyHost(), "getProxyHost() should not return null");
        // Empty by default in test FessConfig.
        assertEquals("", client.getProxyHost());
        // http.proxy.port has a default of 8080.
        assertEquals(Integer.valueOf(8080), client.getProxyPort());
        assertNotNull(client.getProxyUsername(), "getProxyUsername() should not return null");
        assertEquals("", client.getProxyUsername());
        assertNotNull(client.getProxyPassword(), "getProxyPassword() should not return null");
        assertEquals("", client.getProxyPassword());
    }

    @Test
    public void test_configureProxy_noOpWhenHostBlank() {
        // Blank host -> no-op (no proxy applied, no exception). Build the client to confirm.
        final TestEmbeddingClient client = new TestEmbeddingClient();
        client.setTestProxy("", 8080, "", "");
        final HttpClientBuilder builder = HttpClients.custom();
        client.testConfigureProxy(builder);
        try (CloseableHttpClient http = builder.build()) {
            assertNotNull(http);
        } catch (final Exception e) {
            fail("Building client without proxy should not throw: " + e);
        }
    }

    @Test
    public void test_configureProxy_noOpWhenHostNull() {
        final TestEmbeddingClient client = new TestEmbeddingClient();
        client.setTestProxy(null, 8080, null, null);
        final HttpClientBuilder builder = HttpClients.custom();
        client.testConfigureProxy(builder);
        try (CloseableHttpClient http = builder.build()) {
            assertNotNull(http);
        } catch (final Exception e) {
            fail("Building client with null host should not throw: " + e);
        }
    }

    @Test
    public void test_configureProxy_noOpWhenPortNull() {
        // Host set but port null -> still no-op (both required).
        final TestEmbeddingClient client = new TestEmbeddingClient();
        client.setTestProxy("proxy.example.com", null, "", "");
        final HttpClientBuilder builder = HttpClients.custom();
        client.testConfigureProxy(builder);
        try (CloseableHttpClient http = builder.build()) {
            assertNotNull(http);
        } catch (final Exception e) {
            fail("Building client with null port should not throw: " + e);
        }
    }

    @Test
    public void test_configureProxy_appliesProxyWithoutAuth() {
        final TestEmbeddingClient client = new TestEmbeddingClient();
        client.setTestProxy("proxy.example.com", 8080, "", "");
        final HttpClientBuilder builder = HttpClients.custom();
        client.testConfigureProxy(builder);
        try (CloseableHttpClient http = builder.build()) {
            assertNotNull(http);
        } catch (final Exception e) {
            fail("Building client with proxy should not throw: " + e);
        }
    }

    @Test
    public void test_configureProxy_appliesProxyWithBasicAuth() {
        final TestEmbeddingClient client = new TestEmbeddingClient();
        client.setTestProxy("proxy.example.com", 8080, "user", "pass");
        final HttpClientBuilder builder = HttpClients.custom();
        client.testConfigureProxy(builder);
        try (CloseableHttpClient http = builder.build()) {
            assertNotNull(http);
        } catch (final Exception e) {
            fail("Building client with authenticated proxy should not throw: " + e);
        }
    }

    @Test
    public void test_configureProxy_handlesNullPasswordWithUsername() {
        // Null password but non-blank username -> uses empty password char[], no NPE.
        final TestEmbeddingClient client = new TestEmbeddingClient();
        client.setTestProxy("proxy.example.com", 8080, "user", null);
        final HttpClientBuilder builder = HttpClients.custom();
        client.testConfigureProxy(builder);
        try (CloseableHttpClient http = builder.build()) {
            assertNotNull(http);
        } catch (final Exception e) {
            fail("Building client with null password should not throw: " + e);
        }
    }

    // ========== getHttpClient() lifecycle/race-hardening tests ==========

    @Test
    public void test_getHttpClient_lazilyInitializesWhenNull() {
        final TestEmbeddingClient client = new TestEmbeddingClient();
        assertNull(client.httpClient, "httpClient must not be built until first requested");
        final CloseableHttpClient http = client.getHttpClient();
        assertNotNull(http, "getHttpClient() should lazily call init() when httpClient is null");
        assertSame(http, client.getHttpClient(), "a second call must reuse the same client, not build a new one");
    }

    @Test
    public void test_getHttpClient_afterDestroy_throwsIllegalStateException() {
        final TestEmbeddingClient client = new TestEmbeddingClient();
        client.init();
        client.destroy();
        try {
            client.getHttpClient();
            fail("getHttpClient() must throw IllegalStateException once the client has been destroyed, "
                    + "not silently recreate an HTTP client");
        } catch (final IllegalStateException e) {
            // expected
        }
    }

    @Test
    public void test_getHttpClient_concurrentFirstCalls_shareSingleClientInstance() throws Exception {
        // Simulates the reachable race this hardening guards against: a client whose init() was
        // skipped at postConstruct time (embedding-type name mismatch) becomes active via a live
        // content_chunker.embedding.name config switch, and multiple concurrent request threads
        // (e.g. ChunkVectorJob's batch worker pool, or concurrent RAG chat requests) call
        // getHttpClient() for the first time simultaneously.
        final TestEmbeddingClient client = new TestEmbeddingClient();
        final int threadCount = 8;
        final ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        final CyclicBarrier barrier = new CyclicBarrier(threadCount);
        try {
            final List<Future<CloseableHttpClient>> futures = new ArrayList<>();
            for (int i = 0; i < threadCount; i++) {
                futures.add(pool.submit(() -> {
                    barrier.await();
                    return client.getHttpClient();
                }));
            }
            final Set<CloseableHttpClient> observed = Collections.newSetFromMap(new IdentityHashMap<>());
            for (final Future<CloseableHttpClient> future : futures) {
                observed.add(future.get());
            }
            assertEquals(1, observed.size(), "concurrent first calls to getHttpClient() must share exactly one HTTP client instance, "
                    + "never race-build (and leak) two");
        } finally {
            pool.shutdown();
        }
    }

    private static final class TestEmbeddingClient extends AbstractEmbeddingClient {
        private boolean overrideProxy = false;
        private String testProxyHost;
        private Integer testProxyPort;
        private String testProxyUsername;
        private String testProxyPassword;

        void setTestProxy(final String host, final Integer port, final String username, final String password) {
            this.overrideProxy = true;
            this.testProxyHost = host;
            this.testProxyPort = port;
            this.testProxyUsername = username;
            this.testProxyPassword = password;
        }

        @Override
        protected String getProxyHost() {
            return overrideProxy ? testProxyHost : super.getProxyHost();
        }

        @Override
        protected Integer getProxyPort() {
            return overrideProxy ? testProxyPort : super.getProxyPort();
        }

        @Override
        protected String getProxyUsername() {
            return overrideProxy ? testProxyUsername : super.getProxyUsername();
        }

        @Override
        protected String getProxyPassword() {
            return overrideProxy ? testProxyPassword : super.getProxyPassword();
        }

        void testConfigureProxy(final HttpClientBuilder builder) {
            configureProxy(builder);
        }

        @Override
        public List<float[]> embedDocuments(final List<String> texts) {
            return List.of(new float[] { 0f });
        }

        @Override
        public List<float[]> embedQuery(final List<String> texts) {
            return List.of(new float[] { 0f });
        }

        @Override
        public int getDimension() {
            return 1;
        }

        @Override
        public String getName() {
            return "test";
        }

        @Override
        protected boolean checkAvailabilityNow() {
            return true;
        }

        @Override
        protected int getTimeout() {
            return 1000;
        }

        @Override
        protected int getAvailabilityCheckInterval() {
            return 0;
        }

        @Override
        protected boolean isContentChunkerEnabled() {
            return false;
        }

        @Override
        protected String getEmbeddingType() {
            return "test";
        }

        @Override
        protected String getConfigPrefix() {
            return "test.embedding";
        }
    }
}
