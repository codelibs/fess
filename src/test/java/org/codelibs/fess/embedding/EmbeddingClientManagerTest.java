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

import java.util.Collections;
import java.util.List;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class EmbeddingClientManagerTest extends UnitFessTestCase {

    private TestableEmbeddingClientManager manager;

    @Override
    public void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        manager = new TestableEmbeddingClientManager();
    }

    @Test
    public void test_getClient_resolvesViaComponentByConvention() {
        final FakeEmbeddingClient fake = new FakeEmbeddingClient("ollama");
        ComponentUtil.register(fake, "ollamaEmbeddingClient");
        manager.setTestEmbeddingType("ollama");
        assertSame(fake, manager.getClient());
    }

    @Test
    public void test_getClient_fallsBackToRegisteredList() {
        final FakeEmbeddingClient fake = new FakeEmbeddingClient("custom");
        manager.register(fake);
        manager.setTestEmbeddingType("custom");
        assertSame(fake, manager.getClient());
    }

    @Test
    public void test_getClient_returnsNullWhenNotFound() {
        manager.setTestEmbeddingType("missing");
        assertNull(manager.getClient(), "should return null when no client matches");
    }

    // ===================================================================================
    //                                        hasConfiguredClient (quiet probe, Phase C)
    //                                        ================================================
    // The chunk-only mode resolution probes for a configured client on EVERY run, so the probe
    // must never route through getClient() (which WARNs on each miss).

    @Test
    public void test_hasConfiguredClient_falseWhenNameIsNone() {
        final FakeEmbeddingClient fake = new FakeEmbeddingClient("none");
        manager.register(fake);
        manager.setTestEmbeddingType("none");
        assertFalse(manager.hasConfiguredClient(),
                "content_chunker.embedding.name=none means embedding is configured off, even if a client named \"none\" exists");
    }

    @Test
    public void test_hasConfiguredClient_falseWhenNoClientRegistered_withoutRoutingThroughWarningGetClient() {
        final TestableEmbeddingClientManager quietManager = new TestableEmbeddingClientManager() {
            @Override
            public EmbeddingClient getClient() {
                fail("hasConfiguredClient() must not route through getClient(), which WARNs on every miss "
                        + "-- a chunk-only run probing every execution would spam the log");
                return null;
            }
        };
        quietManager.setTestEmbeddingType("missing");
        assertFalse(quietManager.hasConfiguredClient(), "no matching client registered means embedding is not configured");
    }

    @Test
    public void test_hasConfiguredClient_trueWhenClientRegistered_evenIfUnavailable() {
        // Availability (the liveness ping) is deliberately excluded: a registered-but-unreachable
        // client means "configured but transiently down" (skip the run), NOT "configured off"
        // (chunk-only) -- a transient outage must never flip processing into chunk-only mode.
        final FakeEmbeddingClient fake = new FakeEmbeddingClient("ollama");
        fake.available = false;
        manager.register(fake);
        manager.setTestEmbeddingType("ollama");
        assertTrue(manager.hasConfiguredClient(), "a registered client counts as configured regardless of its liveness");
    }

    @Test
    public void test_available_falseWhenDisabled() {
        final FakeEmbeddingClient fake = new FakeEmbeddingClient("ollama");
        fake.available = true;
        manager.register(fake);
        manager.setTestEmbeddingType("ollama");
        manager.setTestEnabled(false);
        assertFalse(manager.available(), "should be false when content_chunker.enabled=false");
    }

    @Test
    public void test_available_trueWhenEnabledAndClientAvailable() {
        final FakeEmbeddingClient fake = new FakeEmbeddingClient("ollama");
        fake.available = true;
        manager.register(fake);
        manager.setTestEmbeddingType("ollama");
        manager.setTestEnabled(true);
        assertTrue(manager.available(), "should be true when enabled and client is available");
    }

    @Test
    public void test_embedDocuments_delegatesToAvailableClient() {
        final FakeEmbeddingClient fake = new FakeEmbeddingClient("ollama");
        fake.available = true;
        fake.result = List.of(new float[] { 1.0f, 2.0f });
        manager.register(fake);
        manager.setTestEmbeddingType("ollama");
        manager.setTestEnabled(true);
        final List<float[]> result = manager.embedDocuments(List.of("chunk"));
        assertEquals(1, result.size());
    }

    @Test
    public void test_embedDocuments_throwsWhenNotAvailable() {
        manager.setTestEmbeddingType("missing");
        manager.setTestEnabled(true);
        try {
            manager.embedDocuments(List.of("chunk"));
            fail("expected EmbeddingException");
        } catch (final EmbeddingException e) {
            // expected
        }
    }

    @Test
    public void test_embedQuery_delegatesToAvailableClientAndUnwrapsSingleResult() {
        final FakeEmbeddingClient fake = new FakeEmbeddingClient("ollama");
        fake.available = true;
        fake.queryResult = List.of(new float[] { 5.0f, 6.0f });
        manager.register(fake);
        manager.setTestEmbeddingType("ollama");
        manager.setTestEnabled(true);
        final float[] result = manager.embedQuery("what is fess?");
        org.junit.jupiter.api.Assertions.assertArrayEquals(new float[] { 5.0f, 6.0f }, result, 0.0f);
    }

    @Test
    public void test_embedQuery_throwsWhenNotAvailable() {
        manager.setTestEmbeddingType("missing");
        manager.setTestEnabled(true);
        try {
            manager.embedQuery("what is fess?");
            fail("expected EmbeddingException");
        } catch (final EmbeddingException e) {
            // expected
        }
    }

    @Test
    public void test_embedQuery_throwsClearExceptionWhenClientReturnsEmptyList() {
        // A buggy EmbeddingClient returning an empty list must not surface a raw
        // IndexOutOfBoundsException from the unwrapping .get(0) call.
        final FakeEmbeddingClient fake = new FakeEmbeddingClient("ollama");
        fake.available = true;
        fake.queryResult = Collections.emptyList();
        manager.register(fake);
        manager.setTestEmbeddingType("ollama");
        manager.setTestEnabled(true);
        try {
            manager.embedQuery("what is fess?");
            fail("expected EmbeddingException");
        } catch (final EmbeddingException e) {
            // expected
        } catch (final IndexOutOfBoundsException e) {
            fail("expected EmbeddingException, not a raw IndexOutOfBoundsException");
        }
    }

    @Test
    public void test_embedQuery_nullQuery_throwsEmbeddingExceptionNotNpe() {
        // embedQuery(null) must not surface a raw NullPointerException from the internal
        // List.of(query) call (whose immutable-list factory rejects null elements). It is a caller
        // misuse and must be reported the same way as the class's other misuse: an EmbeddingException.
        final FakeEmbeddingClient fake = new FakeEmbeddingClient("ollama");
        fake.available = true;
        manager.register(fake);
        manager.setTestEmbeddingType("ollama");
        manager.setTestEnabled(true);
        try {
            manager.embedQuery(null);
            fail("expected EmbeddingException");
        } catch (final EmbeddingException e) {
            // expected
        } catch (final NullPointerException e) {
            fail("expected EmbeddingException, not a raw NullPointerException from List.of(null)");
        }
    }

    @Test
    public void test_embedQuery_blankQuery_throwsEmbeddingException() {
        // The client is wired to return a perfectly good vector, so a blank query must be rejected by
        // embedQuery's own guard BEFORE the client is called -- not slip through to embed whitespace.
        final FakeEmbeddingClient fake = new FakeEmbeddingClient("ollama");
        fake.available = true;
        fake.queryResult = List.of(new float[] { 7.0f });
        manager.register(fake);
        manager.setTestEmbeddingType("ollama");
        manager.setTestEnabled(true);
        try {
            manager.embedQuery("   ");
            fail("expected EmbeddingException for a blank query");
        } catch (final EmbeddingException e) {
            // expected
        }
    }

    private static final class FakeEmbeddingClient implements EmbeddingClient {
        private final String name;
        boolean available = true;
        List<float[]> result = Collections.emptyList();
        List<float[]> queryResult = Collections.emptyList();

        FakeEmbeddingClient(final String name) {
            this.name = name;
        }

        @Override
        public List<float[]> embedDocuments(final List<String> texts) {
            return result;
        }

        @Override
        public List<float[]> embedQuery(final List<String> texts) {
            return queryResult;
        }

        @Override
        public int getDimension() {
            return 4;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isAvailable() {
            return available;
        }
    }

    private static class TestableEmbeddingClientManager extends EmbeddingClientManager {
        private String testEmbeddingType = "ollama";
        private boolean testEnabled = true;

        void setTestEmbeddingType(final String type) {
            this.testEmbeddingType = type;
        }

        void setTestEnabled(final boolean enabled) {
            this.testEnabled = enabled;
        }

        @Override
        protected String getEmbeddingType() {
            return testEmbeddingType;
        }

        @Override
        protected boolean isContentChunkerEnabled() {
            return testEnabled;
        }
    }
}
