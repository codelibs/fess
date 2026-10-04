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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.sun.net.httpserver.HttpServer;

public class AbstractEmbeddingClientTest extends UnitFessTestCase {

    // test_getConfigString_readsFromSystemProperties/PresentButEmptyReturnsEmptyString mutate the
    // container-managed "systemProperties" component (a JVM-lifetime singleton when the container
    // is shared); use a one-time container so no state leaks into other tests.
    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

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
        assertEquals("opensearch", AbstractEmbeddingClient.EMBEDDING_NAME_DEFAULT);
    }

    // The base-class getEmbeddingType() is the shared default resolution for every provider
    // (and mirrored by EmbeddingClientManager): with no content_chunker.embedding.name
    // configured, the built-in OpenSearch ML Commons provider must be selected.
    @Test
    public void test_getEmbeddingType_defaultsToOpensearchWhenUnset() {
        assertNull(ComponentUtil.getFessConfig().getSystemProperty(AbstractEmbeddingClient.EMBEDDING_NAME_PROPERTY),
                "precondition: content_chunker.embedding.name must be unset in the test environment");
        final TestEmbeddingClient client = new TestEmbeddingClient();
        assertEquals(AbstractEmbeddingClient.EMBEDDING_NAME_DEFAULT, client.testBaseGetEmbeddingType());
        assertEquals("opensearch", client.testBaseGetEmbeddingType());
    }

    // ========== getConfigString() ==========

    // getConfigString() must read through FessProp#getSystemProperty -- the mutable
    // "systemProperties" component, the same channel as every other content_chunker.* key --
    // not the LastaFlute fess_config.properties channel loaded once at container boot. Mutate
    // the real "systemProperties" component directly and drive a freshly constructed client
    // (not a FessConfig stub), so the assertion demonstrates the key is live-readable from
    // system properties, not merely that some getter returns a value.
    @Test
    public void test_getConfigString_readsFromSystemProperties() {
        final String key = "test.embedding.some.key";
        ComponentUtil.getSystemProperties().setProperty(key, "configured-value");
        try {
            final TestEmbeddingClient client = new TestEmbeddingClient();
            assertEquals("configured-value", client.getConfigString("some.key", "default-value"));
        } finally {
            ComponentUtil.getSystemProperties().remove(key);
        }
    }

    @Test
    public void test_getConfigString_absentKeyReturnsDefault() {
        final String key = "test.embedding.absent.key";
        assertNull(ComponentUtil.getSystemProperties().getProperty(key), "precondition: key must be unset in the test environment");
        final TestEmbeddingClient client = new TestEmbeddingClient();
        assertEquals("default-value", client.getConfigString("absent.key", "default-value"));
    }

    // A key that is present but set to an empty string must yield "", not the default -- only an
    // absent key falls back to the default. Subclasses rely on this: an explicit empty value
    // (e.g. a document/query prefix key set to "") disables a feature, distinct from the key
    // being unset entirely.
    @Test
    public void test_getConfigString_presentButEmptyReturnsEmptyString() {
        final String key = "test.embedding.empty.key";
        ComponentUtil.getSystemProperties().setProperty(key, "");
        try {
            final TestEmbeddingClient client = new TestEmbeddingClient();
            assertEquals("", client.getConfigString("empty.key", "default-value"));
        } finally {
            ComponentUtil.getSystemProperties().remove(key);
        }
    }

    // ========== Shared member tests ==========
    //
    // getDimension(), isContentChunkerEnabled() and getAvailabilityCheckInterval() are resolved
    // here rather than per provider: each reads a property this class owns, so every provider that
    // implemented them separately produced the same answer by construction. These tests pin the one
    // shared contract; a provider is free to override, but no longer has to.

    @Test
    public void test_getDimension_readsConfiguredValue() {
        ComponentUtil.getSystemProperties().setProperty(AbstractEmbeddingClient.EMBEDDING_DIMENSION_PROPERTY, " 768 ");
        try {
            assertEquals(768, new TestEmbeddingClient().testBaseGetDimension(), "a surrounding-whitespace value must still parse");
        } finally {
            ComponentUtil.getSystemProperties().remove(AbstractEmbeddingClient.EMBEDDING_DIMENSION_PROPERTY);
        }
    }

    @Test
    public void test_getDimension_rejectsUnsetBlankNonNumericAndNonPositive() {
        final TestEmbeddingClient client = new TestEmbeddingClient();
        final String key = AbstractEmbeddingClient.EMBEDDING_DIMENSION_PROPERTY;
        assertNull(ComponentUtil.getSystemProperties().getProperty(key), "precondition: dimension must be unset");
        assertThrowsEmbeddingException(client::testBaseGetDimension, "is not configured", "unset");
        try {
            for (final String bad : new String[] { "", "   ", "abc", "12.5", "0", "-1" }) {
                ComponentUtil.getSystemProperties().setProperty(key, bad);
                assertThrowsEmbeddingException(client::testBaseGetDimension, key, "value '" + bad + "'");
            }
        } finally {
            ComponentUtil.getSystemProperties().remove(key);
        }
    }

    private void assertThrowsEmbeddingException(final Runnable action, final String expectedFragment, final String label) {
        try {
            action.run();
            fail("expected EmbeddingException for " + label);
        } catch (final EmbeddingException e) {
            assertTrue(e.getMessage().contains(expectedFragment),
                    "message for " + label + " should mention '" + expectedFragment + "': " + e.getMessage());
        }
    }

    @Test
    public void test_isContentChunkerEnabled_readsSharedSystemProperty() {
        final String key = AbstractEmbeddingClient.CONTENT_CHUNKER_ENABLED_PROPERTY;
        final TestEmbeddingClient client = new TestEmbeddingClient();
        assertFalse(client.testBaseIsContentChunkerEnabled(), "must default to false when unset");
        ComponentUtil.getSystemProperties().setProperty(key, "true");
        try {
            assertTrue(client.testBaseIsContentChunkerEnabled());
        } finally {
            ComponentUtil.getSystemProperties().remove(key);
        }
    }

    @Test
    public void test_getAvailabilityCheckInterval_defaultsTo60AndHonorsConfig() {
        final String key = "test.embedding.availability.check.interval";
        final TestEmbeddingClient client = new TestEmbeddingClient();
        assertEquals(60, client.testBaseGetAvailabilityCheckInterval());
        ComponentUtil.getSystemProperties().setProperty(key, "5");
        try {
            assertEquals(5, client.testBaseGetAvailabilityCheckInterval());
        } finally {
            ComponentUtil.getSystemProperties().remove(key);
        }
    }

    @Test
    public void test_getConnectTimeout_defaultsTo5000AndHonorsConfig() {
        final String key = "test.embedding.connect.timeout";
        final TestEmbeddingClient client = new TestEmbeddingClient();
        // Deliberately independent of getTimeout() (1000 for this client): a shared value would let
        // a generous response budget bound the TCP handshake, and the first probe runs from init().
        assertEquals(5000, client.getConnectTimeout());
        ComponentUtil.getSystemProperties().setProperty(key, "250");
        try {
            assertEquals(250, client.getConnectTimeout());
        } finally {
            ComponentUtil.getSystemProperties().remove(key);
        }
    }

    // getConfigLong deliberately does NOT inherit getConfigInt's positive-only rule: it serves
    // millisecond delays, where 0 means "do not wait" and must survive the round trip.
    @Test
    public void test_getConfigLong_parsesAnyNumericIncludingZeroAndNegative() {
        final String key = "test.embedding.delay.ms";
        final TestEmbeddingClient client = new TestEmbeddingClient();
        // An absent key must fall back to the default.
        assertEquals(2000L, client.testGetConfigLong("delay.ms", 2000L));
        try {
            ComponentUtil.getSystemProperties().setProperty(key, "5000");
            assertEquals(5000L, client.testGetConfigLong("delay.ms", 2000L));
            ComponentUtil.getSystemProperties().setProperty(key, " 7000 ");
            // A surrounding-whitespace value must still parse.
            assertEquals(7000L, client.testGetConfigLong("delay.ms", 2000L));
            ComponentUtil.getSystemProperties().setProperty(key, "0");
            // 0 is a legitimate delay, not 'unset'.
            assertEquals(0L, client.testGetConfigLong("delay.ms", 2000L));
            ComponentUtil.getSystemProperties().setProperty(key, "-1");
            assertEquals(-1L, client.testGetConfigLong("delay.ms", 2000L));
        } finally {
            ComponentUtil.getSystemProperties().remove(key);
        }
    }

    @Test
    public void test_getConfigLong_invalidValueFallsBackToDefault() {
        final String key = "test.embedding.delay.ms";
        ComponentUtil.getSystemProperties().setProperty(key, "not-a-number");
        try {
            assertEquals(2000L, new TestEmbeddingClient().testGetConfigLong("delay.ms", 2000L));
        } finally {
            ComponentUtil.getSystemProperties().remove(key);
        }
    }

    // The hook exists so a provider needing default headers (OpenSearch's preemptive basic auth)
    // does not have to reimplement init() and buildHttpClient() to get them.
    @Test
    public void test_buildHttpClient_invokesConfigureHttpClientHook() throws Exception {
        final TestEmbeddingClient client = new TestEmbeddingClient();
        assertEquals(0, client.configureHttpClientCallCount());
        try (CloseableHttpClient built = client.testBuildHttpClient()) {
            assertNotNull(built);
        }
        assertEquals(1, client.configureHttpClientCallCount(), "buildHttpClient must give subclasses a shot at the builder");
    }

    // A provider that accepts requests and never answers pins one connection per request until the
    // response timeout. The pool has to hold as many of them as there are threads in the rank fusion
    // executor, plus one for the availability probe; with the HttpClient default of 5 connections per
    // route a handful of hung searches left every later request waiting for a connection long after the
    // provider had recovered.
    @Test
    public void test_buildHttpClient_hungRequestsDoNotExhaustThePool() throws Exception {
        final int fusionThreads = 12;
        final CountDownLatch release = new CountDownLatch(1);
        final AtomicInteger hung = new AtomicInteger();
        final HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/hang", exchange -> {
            hung.incrementAndGet();
            try {
                release.await(30, TimeUnit.SECONDS);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        server.createContext("/ok", exchange -> {
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        final ExecutorService serverThreads = Executors.newCachedThreadPool();
        server.setExecutor(serverThreads);
        server.start();
        final ExecutorService callers = Executors.newCachedThreadPool();
        final TestEmbeddingClient client = new TestEmbeddingClient();
        client.setTestTimeout(30000);
        client.setTestMaxConnections(fusionThreads + 1);
        final String base = "http://" + server.getAddress().getHostString() + ":" + server.getAddress().getPort();
        try (CloseableHttpClient http = client.testBuildHttpClient()) {
            for (int i = 0; i < fusionThreads; i++) {
                callers.submit(() -> {
                    try (var response = http.execute(new HttpGet(base + "/hang"))) {
                        return response.getCode();
                    }
                });
            }
            final long deadline = System.currentTimeMillis() + 10000;
            while (hung.get() < fusionThreads && System.currentTimeMillis() < deadline) {
                Thread.sleep(50);
            }
            assertEquals(fusionThreads, hung.get(), "every thread of the rank fusion executor must be able to hold a request open");

            final long start = System.currentTimeMillis();
            try (var response = http.execute(new HttpGet(base + "/ok"))) {
                assertEquals(200, response.getCode());
            }
            assertTrue(System.currentTimeMillis() - start < 3000, "the availability probe must not wait for a pooled connection");
        } finally {
            release.countDown();
            callers.shutdownNow();
            server.stop(0);
            serverThreads.shutdownNow();
        }
    }

    @Test
    public void test_getMaxConnections_coversTheRankFusionExecutorAndTheProbe() {
        final int fusionThreads = Runtime.getRuntime().availableProcessors() * 3 / 2 + 1;
        final int maxConnections = new TestEmbeddingClient().testGetMaxConnections();
        assertTrue(maxConnections >= fusionThreads + 1, "one connection per fusion thread and one for the probe: " + maxConnections);
        assertTrue(maxConnections >= 5, "never fewer than the HttpClient default: " + maxConnections);
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
    public void test_getHttpClient_rebuildsWhenTimeoutChanges() {
        final String key = "test.embedding.connect.timeout";
        final TestEmbeddingClient client = new TestEmbeddingClient();
        try {
            final CloseableHttpClient first = client.getHttpClient();
            assertSame(first, client.getHttpClient());

            // A hand edit of system.properties: the timeouts are fixed in the client when it is built.
            ComponentUtil.getSystemProperties().setProperty(key, "250");
            final CloseableHttpClient second = client.getHttpClient();
            assertFalse(first == second, "a changed timeout must build a new client");
            assertEquals(250, client.httpClientConnectTimeout);
            assertSame(second, client.getHttpClient());
        } finally {
            ComponentUtil.getSystemProperties().remove(key);
            client.destroy();
        }
    }

    // The check is a one-shot task that re-arms itself with the interval in force when it finishes,
    // so a changed interval takes effect at the next run and no task is left over.
    @Test
    public void test_runAvailabilityCheck_rearmsWithCurrentInterval() {
        final TestEmbeddingClient client = new TestEmbeddingClient();
        client.setTestContentChunkerEnabled(true);
        client.setTestAvailabilityCheckInterval(30);
        try {
            client.init();
            final TimeoutTask first = client.availabilityCheckTask;
            assertNotNull(first);
            assertFalse(first.isPermanent(), "a permanent task cannot be replaced from inside its own run");

            client.runAvailabilityCheck();
            final TimeoutTask second = client.availabilityCheckTask;
            assertFalse(first == second, "every run arms the next one");
            assertFalse(second.isPermanent());

            client.setTestAvailabilityCheckInterval(10);
            client.runAvailabilityCheck();
            assertFalse(second == client.availabilityCheckTask);
            assertFalse(client.availabilityCheckTask.isCanceled());
        } finally {
            client.destroy();
        }
    }

    @Test
    public void test_runAvailabilityCheck_afterDestroy_doesNotRearm() {
        final TestEmbeddingClient client = new TestEmbeddingClient();
        client.setTestContentChunkerEnabled(true);
        client.setTestAvailabilityCheckInterval(30);
        client.init();
        final TimeoutTask last = client.availabilityCheckTask;
        final int probes = client.availabilityProbeCount();
        client.destroy();

        client.runAvailabilityCheck();
        assertSame(last, client.availabilityCheckTask, "a check that outlives destroy() must not arm another one");
        assertTrue(last.isCanceled());
        assertEquals(probes, client.availabilityProbeCount());
    }

    // corelib's TimeoutManager calls restart() on a permanent task after every run, which undoes a cancel()
    // made from inside that run. Cancelling the running check when the interval changed therefore left it
    // running next to its replacement: after 60 -> 5 -> 60 the 5 s check never stopped. Driven by the real
    // TimeoutManager, so it takes about ten seconds.
    @Test
    public void test_availabilityCheck_intervalChangeLeavesOneCheckRunning() throws Exception {
        final TestEmbeddingClient client = new TestEmbeddingClient();
        client.setTestContentChunkerEnabled(true);
        client.setTestAvailabilityCheckInterval(1);
        try {
            client.init();
            final long deadline = System.currentTimeMillis() + 20000;
            while (client.availabilityProbeCount() < 3 && System.currentTimeMillis() < deadline) {
                Thread.sleep(100);
            }
            assertTrue(client.availabilityProbeCount() >= 3, "the check should be running every second");

            client.setTestAvailabilityCheckInterval(5);
            Thread.sleep(2000); // the pending one-second run picks the new interval up
            final int before = client.availabilityProbeCount();
            Thread.sleep(7000);
            final int probes = client.availabilityProbeCount() - before;
            assertTrue(probes >= 1, "the check must keep running: " + probes);
            assertTrue(probes <= 3, "a 5 s check runs once or twice in 7 s, but a left-over 1 s check adds about 7: " + probes);
        } finally {
            client.destroy();
        }
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

    // ========== availability-check hardening ==========

    // BLOCKER regression pin: init() runs from the container's init-method assembler, whose
    // RuntimeExceptions abort Tomcat context startup (LdiMethodUtil rethrows the cause raw ->
    // ContainerInitFailureException -> LastaPrepareFilter). checkAvailabilityNow() reads operator
    // configuration (e.g. OpenSearchEmbeddingClient.getModelId(), which rejects a malformed
    // model.id with an EmbeddingException), so a single typo in fess_config.properties must not
    // be able to stop Fess from booting. A failed probe means "unavailable", never "do not start".
    @Test
    public void test_init_doesNotPropagateAvailabilityFailure() {
        final TestEmbeddingClient client = new TestEmbeddingClient();
        client.setTestContentChunkerEnabled(true);
        client.setTestAvailabilityCheckInterval(60);
        client.setTestAvailabilityFailure(new EmbeddingException("Invalid content_chunker.embedding.opensearch.model.id: bad/id"));
        try {
            client.init();
        } catch (final RuntimeException e) {
            fail("init() must not propagate an availability-check failure -- it aborts container startup: " + e);
        } finally {
            client.destroy();
        }
        assertFalse(client.isAvailable(), "a client whose availability probe throws must report unavailable, not rethrow");
    }

    // isAvailable() is the read path used by EmbeddingClientManager.available() on every search
    // request; it must degrade to false rather than propagate a configuration failure.
    @Test
    public void test_isAvailable_doesNotPropagateAvailabilityFailure() {
        final TestEmbeddingClient client = new TestEmbeddingClient();
        client.setTestAvailabilityFailure(new EmbeddingException("boom"));
        try {
            assertFalse(client.isAvailable(), "isAvailable() must report false when the probe throws, not rethrow");
        } finally {
            client.destroy();
        }
    }

    // MAJOR perf pin: startAvailabilityCheck() early-returns while content chunking is disabled and
    // is only ever called from init(), which runs once at postConstruct. Enabling the feature after
    // boot therefore used to leave cachedAvailability null forever, making every
    // EmbeddingClientManager.available() call pay a synchronous provider probe. isAvailable() must
    // start (and memoize) the check on first use instead.
    @Test
    public void test_isAvailable_lazilyStartsCheckOnceWhenEnabledAfterInit() {
        final TestEmbeddingClient client = new TestEmbeddingClient();
        try {
            client.init();
            assertEquals(0, client.availabilityProbeCount(), "a disabled feature must not probe the provider at init()");

            // Operator enables content_chunker.enabled after boot; no init() re-run happens because
            // init() assigns httpClient before startAvailabilityCheck(), so getHttpClient() never
            // lazily re-initializes.
            client.setTestContentChunkerEnabled(true);
            client.setTestAvailabilityCheckInterval(60);

            for (int i = 0; i < 5; i++) {
                assertTrue(client.isAvailable());
            }
            assertEquals(1, client.availabilityProbeCount(),
                    "repeated isAvailable() calls must issue exactly one provider probe, not one per call");
        } finally {
            client.destroy();
        }
    }

    private static final class TestEmbeddingClient extends AbstractEmbeddingClient {
        private boolean overrideProxy = false;
        private String testProxyHost;
        private Integer testProxyPort;
        private String testProxyUsername;
        private String testProxyPassword;
        private boolean testContentChunkerEnabled = false;
        private int testAvailabilityCheckInterval = 0;
        private int testTimeout = 1000;
        private Integer testMaxConnections;
        private RuntimeException testAvailabilityFailure;
        private final AtomicInteger availabilityProbes = new AtomicInteger();
        private final AtomicInteger configureHttpClientCalls = new AtomicInteger();

        void setTestContentChunkerEnabled(final boolean enabled) {
            this.testContentChunkerEnabled = enabled;
        }

        void setTestAvailabilityCheckInterval(final int interval) {
            this.testAvailabilityCheckInterval = interval;
        }

        void setTestTimeout(final int timeout) {
            this.testTimeout = timeout;
        }

        void setTestMaxConnections(final int maxConnections) {
            this.testMaxConnections = maxConnections;
        }

        void setTestAvailabilityFailure(final RuntimeException failure) {
            this.testAvailabilityFailure = failure;
        }

        int availabilityProbeCount() {
            return availabilityProbes.get();
        }

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

        String testBaseGetEmbeddingType() {
            return super.getEmbeddingType();
        }

        int testBaseGetDimension() {
            return super.getDimension();
        }

        boolean testBaseIsContentChunkerEnabled() {
            return super.isContentChunkerEnabled();
        }

        int testBaseGetAvailabilityCheckInterval() {
            return super.getAvailabilityCheckInterval();
        }

        long testGetConfigLong(final String keySuffix, final long defaultValue) {
            return getConfigLong(keySuffix, defaultValue);
        }

        CloseableHttpClient testBuildHttpClient() {
            return buildHttpClient();
        }

        int testGetMaxConnections() {
            return super.getMaxConnections();
        }

        @Override
        protected int getMaxConnections() {
            return testMaxConnections != null ? testMaxConnections : super.getMaxConnections();
        }

        @Override
        protected void configureHttpClient(final HttpClientBuilder builder) {
            configureHttpClientCalls.incrementAndGet();
        }

        int configureHttpClientCallCount() {
            return configureHttpClientCalls.get();
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
            availabilityProbes.incrementAndGet();
            if (testAvailabilityFailure != null) {
                throw testAvailabilityFailure;
            }
            return true;
        }

        @Override
        protected int getTimeout() {
            return testTimeout;
        }

        @Override
        protected int getAvailabilityCheckInterval() {
            return testAvailabilityCheckInterval;
        }

        @Override
        protected boolean isContentChunkerEnabled() {
            return testContentChunkerEnabled;
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
