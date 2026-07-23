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
package org.codelibs.fess.embedding.opensearch;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.codelibs.fess.embedding.EmbeddingException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class OpenSearchEmbeddingClientTest extends UnitFessTestCase {

    // This test mutates the container-managed "systemProperties" component (a
    // JVM-lifetime singleton when the container is shared) and swaps FessConfig;
    // use a one-time container so no state leaks into other tests.
    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

    /** The real config key read by the production (non-overridden) {@link OpenSearchEmbeddingClient#getDimension()}. */
    private static final String DIMENSION_CONFIG_KEY = "content_chunker.embedding.dimension";

    /** The model id used by the testable client; part of the predict/model-get request paths. */
    private static final String TEST_MODEL_ID = "test-model";

    /** Predict endpoint path for {@link #TEST_MODEL_ID}. */
    private static final String PREDICT_PATH = "/_plugins/_ml/models/" + TEST_MODEL_ID + "/_predict";

    /** Model get endpoint path for {@link #TEST_MODEL_ID}, used by the availability check. */
    private static final String MODEL_GET_PATH = "/_plugins/_ml/models/" + TEST_MODEL_ID;

    /** Captured live from OpenSearch 3.7.0 when the ML memory circuit breaker is open. */
    private static final String CIRCUIT_BREAKER_429_BODY =
            "{\"error\":{\"root_cause\":[{\"type\":\"circuit_breaking_exception\",\"reason\":\"Memory Circuit Breaker is open, "
                    + "please check your resources!\",\"bytes_wanted\":0,\"bytes_limit\":0,\"durability\":\"TRANSIENT\"}],"
                    + "\"type\":\"circuit_breaking_exception\",\"reason\":\"Memory Circuit Breaker is open, please check your "
                    + "resources!\",\"bytes_wanted\":0,\"bytes_limit\":0,\"durability\":\"TRANSIENT\"},\"status\":429}";

    private TestableOpenSearchEmbeddingClient client;

    @Override
    public void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        client = new TestableOpenSearchEmbeddingClient();
    }

    @Override
    public void tearDown(final TestInfo testInfo) throws Exception {
        if (client != null) {
            client.destroy();
        }
        super.tearDown(testInfo);
    }

    @Test
    public void test_getName() {
        assertEquals("opensearch", client.getName());
    }

    @Test
    public void test_embedDocuments_success() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            final String responseJson = "{\"inference_results\":["
                    + "{\"output\":[{\"name\":\"sentence_embedding\",\"data_type\":\"FLOAT32\",\"shape\":[3],\"data\":[0.1,0.2,0.3]}]},"
                    + "{\"output\":[{\"name\":\"sentence_embedding\",\"data_type\":\"FLOAT32\",\"shape\":[3],\"data\":[0.4,0.5,0.6]}]}]}";
            server.enqueue(new MockResponse().setBody(responseJson).setHeader("Content-Type", "application/json"));
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.initHttpClient();

            final List<float[]> result = client.embedDocuments(List.of("chunk one", "chunk two"));

            assertEquals(2, result.size());
            assertEquals(3, result.get(0).length);
            assertEquals(0.1f, result.get(0)[0]);
            assertEquals(0.2f, result.get(0)[1]);
            assertEquals(0.6f, result.get(1)[2]);

            final RecordedRequest recordedRequest = takeRequest(server);
            assertEquals(PREDICT_PATH, recordedRequest.getPath());
            assertEquals("POST", recordedRequest.getMethod());
            final String body = recordedRequest.getBody().readUtf8();
            assertTrue(body.contains("\"text_docs\":[\"chunk one\",\"chunk two\"]"), "request body should carry both text_docs: " + body);
            assertTrue(body.contains("\"return_number\":true"), "request body should carry return_number=true: " + body);
            assertTrue(body.contains("\"target_response\":[\"sentence_embedding\"]"),
                    "request body should carry the sentence_embedding target_response: " + body);
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_embedQuery_success() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            final String responseJson = "{\"inference_results\":["
                    + "{\"output\":[{\"name\":\"sentence_embedding\",\"data_type\":\"FLOAT32\",\"shape\":[3],\"data\":[0.1,0.2,0.3]}]}]}";
            server.enqueue(new MockResponse().setBody(responseJson).setHeader("Content-Type", "application/json"));
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.initHttpClient();

            final List<float[]> result = client.embedQuery(List.of("what is fess?"));

            assertEquals(1, result.size());
            assertEquals(3, result.get(0).length);

            final RecordedRequest recordedRequest = takeRequest(server);
            assertEquals(PREDICT_PATH, recordedRequest.getPath());
            assertEquals("POST", recordedRequest.getMethod());
            final String body = recordedRequest.getBody().readUtf8();
            assertTrue(body.contains("\"text_docs\":[\"what is fess?\"]"), "request body should carry the query text: " + body);
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_embedDocuments_defaultPrefixIsEmpty() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            // Unlike the Ollama sibling (nomic-style "search_document: " default),
            // pretrained OpenSearch models use no prefix convention, so the default
            // document prefix is empty and inputs must be sent verbatim.
            final String responseJson = "{\"inference_results\":["
                    + "{\"output\":[{\"name\":\"sentence_embedding\",\"data_type\":\"FLOAT32\",\"shape\":[3],\"data\":[0.1,0.2,0.3]}]}]}";
            server.enqueue(new MockResponse().setBody(responseJson).setHeader("Content-Type", "application/json"));
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.initHttpClient();

            client.embedDocuments(List.of("chunk one"));

            final RecordedRequest recordedRequest = takeRequest(server);
            final String body = recordedRequest.getBody().readUtf8();
            assertTrue(body.contains("\"text_docs\":[\"chunk one\"]"), "request body should carry the unprefixed input: " + body);
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_embedDocuments_appliesConfiguredDocumentPrefix() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            final String responseJson = "{\"inference_results\":["
                    + "{\"output\":[{\"name\":\"sentence_embedding\",\"data_type\":\"FLOAT32\",\"shape\":[3],\"data\":[0.1,0.2,0.3]}]}]}";
            server.enqueue(new MockResponse().setBody(responseJson).setHeader("Content-Type", "application/json"));
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.setTestDocumentPrefix("passage: ");
            client.initHttpClient();

            client.embedDocuments(List.of("chunk one"));

            final RecordedRequest recordedRequest = takeRequest(server);
            final String body = recordedRequest.getBody().readUtf8();
            assertTrue(body.contains("\"text_docs\":[\"passage: chunk one\"]"),
                    "request body should carry the document-prefixed input: " + body);
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_embedQuery_appliesConfiguredQueryPrefix() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            final String responseJson = "{\"inference_results\":["
                    + "{\"output\":[{\"name\":\"sentence_embedding\",\"data_type\":\"FLOAT32\",\"shape\":[3],\"data\":[0.1,0.2,0.3]}]}]}";
            server.enqueue(new MockResponse().setBody(responseJson).setHeader("Content-Type", "application/json"));
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.setTestQueryPrefix("query: ");
            client.initHttpClient();

            client.embedQuery(List.of("what is fess?"));

            final RecordedRequest recordedRequest = takeRequest(server);
            final String body = recordedRequest.getBody().readUtf8();
            assertTrue(body.contains("\"text_docs\":[\"query: what is fess?\"]"),
                    "request body should carry the query-prefixed input: " + body);
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_embedDocuments_countMismatch_throws() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            // Two inputs but only one inference result returned: a count mismatch,
            // distinct from a per-vector dimension mismatch.
            final String responseJson = "{\"inference_results\":["
                    + "{\"output\":[{\"name\":\"sentence_embedding\",\"data_type\":\"FLOAT32\",\"shape\":[3],\"data\":[0.1,0.2,0.3]}]}]}";
            server.enqueue(new MockResponse().setBody(responseJson).setHeader("Content-Type", "application/json"));
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.initHttpClient();

            try {
                client.embedDocuments(List.of("chunk one", "chunk two"));
                fail("expected EmbeddingException on inference result count mismatch");
            } catch (final EmbeddingException e) {
                assertTrue(e.getMessage().contains("count mismatch"),
                        "message should mention count mismatch, not dimension: " + e.getMessage());
            }
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_embedDocuments_dimensionMismatch_throws() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            // Server returns 3-dim vectors but the configured dimension is 4.
            final String responseJson = "{\"inference_results\":["
                    + "{\"output\":[{\"name\":\"sentence_embedding\",\"data_type\":\"FLOAT32\",\"shape\":[3],\"data\":[0.1,0.2,0.3]}]}]}";
            server.enqueue(new MockResponse().setBody(responseJson).setHeader("Content-Type", "application/json"));
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(4);
            client.initHttpClient();

            try {
                client.embedDocuments(List.of("chunk one"));
                fail("expected EmbeddingException on dimension mismatch");
            } catch (final EmbeddingException e) {
                assertTrue(e.getMessage().contains("dimension"), "message should mention dimension: " + e.getMessage());
            }
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_embedDocuments_nonNumericVectorComponent_throws() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            // The vector's second element is a JSON null instead of a number. A naive
            // Jackson asDouble() call would silently coerce this to 0.0 and corrupt the
            // stored vector instead of surfacing a clear error.
            final String responseJson = "{\"inference_results\":["
                    + "{\"output\":[{\"name\":\"sentence_embedding\",\"data_type\":\"FLOAT32\",\"shape\":[3],\"data\":[0.1,null,0.3]}]}]}";
            server.enqueue(new MockResponse().setBody(responseJson).setHeader("Content-Type", "application/json"));
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.initHttpClient();

            try {
                client.embedDocuments(List.of("chunk one"));
                fail("expected EmbeddingException on non-numeric vector component");
            } catch (final EmbeddingException e) {
                assertTrue(e.getMessage().contains("not numeric"), "message should mention non-numeric component: " + e.getMessage());
            }
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_embedDocuments_nonFiniteVectorComponent_throws() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            // 1e999 overflows the double range and parses as a NUMBER node whose value is
            // Infinity: isNumber() returns true, so the non-numeric guard alone lets it
            // through and casting to float would store a non-finite (Infinity) component,
            // silently corrupting the vector. It must be rejected with a clear error.
            final String responseJson = "{\"inference_results\":["
                    + "{\"output\":[{\"name\":\"sentence_embedding\",\"data_type\":\"FLOAT32\",\"shape\":[3],\"data\":[0.1,1e999,0.3]}]}]}";
            server.enqueue(new MockResponse().setBody(responseJson).setHeader("Content-Type", "application/json"));
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.initHttpClient();

            try {
                client.embedDocuments(List.of("chunk one"));
                fail("expected EmbeddingException on non-finite vector component");
            } catch (final EmbeddingException e) {
                assertTrue(e.getMessage().contains("not finite"), "message should mention non-finite component: " + e.getMessage());
            }
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_embedDocuments_malformedJsonResponse_throws() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            // A 2xx response whose body is not valid JSON must surface as a clear
            // EmbeddingException from parsePredictResponse's readTree failure.
            server.enqueue(new MockResponse().setBody("not json{").setHeader("Content-Type", "application/json"));
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.initHttpClient();

            try {
                client.embedDocuments(List.of("chunk one"));
                fail("expected EmbeddingException on malformed JSON response");
            } catch (final EmbeddingException e) {
                assertTrue(e.getMessage().contains("Failed to parse OpenSearch ML predict response"),
                        "message should indicate a parse failure: " + e.getMessage());
            }
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_embedDocuments_missingSentenceEmbeddingOutput_throws() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            // The output array carries only other tensors (as when target_response is
            // ignored/absent server-side); the defensive select-by-name must fail clearly
            // instead of reading the wrong tensor's data.
            final String responseJson = "{\"inference_results\":["
                    + "{\"output\":[{\"name\":\"input_ids\",\"data_type\":\"INT64\",\"shape\":[3],\"data\":[101,102,103]}]}]}";
            server.enqueue(new MockResponse().setBody(responseJson).setHeader("Content-Type", "application/json"));
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.initHttpClient();

            try {
                client.embedDocuments(List.of("chunk one"));
                fail("expected EmbeddingException when no sentence_embedding output is present");
            } catch (final EmbeddingException e) {
                assertTrue(e.getMessage().contains("sentence_embedding"),
                        "message should mention the missing sentence_embedding output: " + e.getMessage());
            }
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_embedDocuments_doesNotRetryOn404() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            // Captured live: unknown model_id is a 404 status_exception ("Failed to find
            // model") - a configuration error that must never be retried.
            server.enqueue(new MockResponse().setResponseCode(404)
                    .setBody("{\"error\":{\"type\":\"status_exception\",\"reason\":\"Failed to find model\"},\"status\":404}"));
            server.start();

            client.setTestApiUrl(server.url("/").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.setTestRetryMax(5);
            client.setTestRetryBaseDelayMs(1L);
            client.initHttpClient();

            try {
                client.embedDocuments(List.of("chunk"));
                fail("expected EmbeddingException");
            } catch (final EmbeddingException e) {
                // expected
            }
            assertEquals("404 must not be retried", 1, server.getRequestCount());
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_embedDocuments_retriesOn429CircuitBreaker() throws Exception {
        final MockWebServer server = new MockWebServer();
        // The ML memory circuit breaker is the primary transient failure mode of a
        // heap-pressured co-located cluster; its 429 must be retried and then succeed.
        server.enqueue(
                new MockResponse().setResponseCode(429).setHeader("Content-Type", "application/json").setBody(CIRCUIT_BREAKER_429_BODY));
        final String successBody = "{\"inference_results\":["
                + "{\"output\":[{\"name\":\"sentence_embedding\",\"data_type\":\"FLOAT32\",\"shape\":[3],\"data\":[0.1,0.2,0.3]}]}]}";
        server.enqueue(new MockResponse().setHeader("Content-Type", "application/json").setBody(successBody));
        server.start();
        try {
            client.setTestApiUrl(server.url("/").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.setTestRetryMax(3);
            client.setTestRetryBaseDelayMs(1L);
            client.initHttpClient();

            final List<float[]> result = client.embedDocuments(List.of("chunk"));

            assertEquals(1, result.size());
            assertEquals("the circuit-breaker 429 should be retried once and then succeed", 2, server.getRequestCount());
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_embedDocuments_retriesOn503() throws Exception {
        final MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(503));
        final String successBody = "{\"inference_results\":["
                + "{\"output\":[{\"name\":\"sentence_embedding\",\"data_type\":\"FLOAT32\",\"shape\":[3],\"data\":[0.1,0.2,0.3]}]}]}";
        server.enqueue(new MockResponse().setHeader("Content-Type", "application/json").setBody(successBody));
        server.start();
        try {
            client.setTestApiUrl(server.url("/").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.setTestRetryMax(3);
            client.setTestRetryBaseDelayMs(1L);
            client.initHttpClient();

            final List<float[]> result = client.embedDocuments(List.of("chunk"));

            assertEquals(1, result.size());
            assertEquals(2, server.getRequestCount());
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_embedDocuments_retryExhaustion_throwsEmbeddingException() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            // Every attempt returns a retryable 503; once the retry budget is exhausted
            // the failure must surface as EmbeddingException (wrapping the IOException),
            // never a raw IOException leaking out of executeWithRetry.
            server.enqueue(new MockResponse().setResponseCode(503));
            server.enqueue(new MockResponse().setResponseCode(503));
            server.enqueue(new MockResponse().setResponseCode(503));
            server.start();

            client.setTestApiUrl(server.url("/").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.setTestRetryMax(3);
            client.setTestRetryBaseDelayMs(1L);
            client.initHttpClient();

            try {
                client.embedDocuments(List.of("chunk"));
                fail("expected EmbeddingException after retry exhaustion");
            } catch (final EmbeddingException e) {
                assertTrue(e.getCause() instanceof java.io.IOException,
                        "cause should be the IOException from exhausted retries: " + e.getCause());
            }
            assertEquals("all retry attempts should have been made", 3, server.getRequestCount());
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_embedDocuments_emptyInput_returnsEmptyWithNoApiCall() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.initHttpClient();

            final List<float[]> result = client.embedDocuments(List.of());

            assertTrue(result.isEmpty(), "empty input should return an empty list");
            assertEquals("empty input must not hit the API (server rejects empty text_docs with 400)", 0, server.getRequestCount());
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_embedDocuments_blankModelId_throws() {
        // No server: the model-id guard must fire before any HTTP interaction.
        client.setTestModelId("");
        try {
            client.embedDocuments(List.of("chunk"));
            fail("expected EmbeddingException when model.id is not configured");
        } catch (final EmbeddingException e) {
            assertTrue(e.getMessage().contains("model.id is not configured"),
                    "message should name the missing model.id key: " + e.getMessage());
        }
    }

    // ========== Sub-batch item cap ==========
    //
    // OpenSearchEmbeddingClient.MAX_BATCH_ITEMS is a private constant (128). These
    // tests mirror that value locally: an input larger than the cap must be split
    // into contiguous sub-batches, each sent as its own _predict request, with
    // the per-sub-batch vectors concatenated back in input order.
    private static final int MAX_BATCH_ITEMS = 128;

    @Test
    public void test_embedDocuments_splitsIntoSubBatchesPreservingOrder() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            // 130 inputs with a cap of 128 -> two sub-batches sized 128 and 2.
            // Each sub-batch response tags its vectors with a distinct first
            // component (1.0 vs 2.0) so the concatenation order is observable.
            final int total = MAX_BATCH_ITEMS + 2;
            server.enqueue(
                    new MockResponse().setBody(predictResponse(MAX_BATCH_ITEMS, 3, 1.0f)).setHeader("Content-Type", "application/json"));
            server.enqueue(new MockResponse().setBody(predictResponse(2, 3, 2.0f)).setHeader("Content-Type", "application/json"));
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.initHttpClient();

            final List<String> inputs = new ArrayList<>(total);
            for (int i = 0; i < total; i++) {
                inputs.add("text-" + i);
            }

            final List<float[]> result = client.embedDocuments(inputs);

            // Count and order preserved across the concatenation.
            assertEquals(total, result.size());
            assertEquals(1.0f, result.get(0)[0]);
            assertEquals(1.0f, result.get(MAX_BATCH_ITEMS - 1)[0]);
            assertEquals(2.0f, result.get(MAX_BATCH_ITEMS)[0]);
            assertEquals(2.0f, result.get(total - 1)[0]);

            // Exactly one request per sub-batch, carrying the expected slice sizes.
            assertEquals("input exceeding the cap must be split into two sub-batch requests", 2, server.getRequestCount());
            final String firstBody = takeRequest(server).getBody().readUtf8();
            final String secondBody = takeRequest(server).getBody().readUtf8();
            assertEquals("first sub-batch should carry MAX_BATCH_ITEMS inputs", MAX_BATCH_ITEMS, countOccurrences(firstBody, "\"text-"));
            assertEquals("second sub-batch should carry the remaining inputs", 2, countOccurrences(secondBody, "\"text-"));
            assertTrue(firstBody.contains("\"text-0\"") && firstBody.contains("\"text-127\""),
                    "first sub-batch should carry the leading slice: " + firstBody.substring(0, Math.min(200, firstBody.length())));
            assertTrue(secondBody.contains("\"text-128\"") && secondBody.contains("\"text-129\""),
                    "second sub-batch should carry the trailing slice: " + secondBody);
        } finally {
            server.shutdown();
        }
    }

    // ========== checkAvailabilityNow() / isModelDeployed() coverage ==========

    @Test
    public void test_checkAvailabilityNow_deployed_returnsTrue() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            server.enqueue(new MockResponse().setBody(modelResponse("DEPLOYED", "TEXT_EMBEDDING", 3))
                    .setHeader("Content-Type", "application/json"));
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.initHttpClient();

            assertTrue(client.checkAvailabilityNow(), "a DEPLOYED model should be available");

            final RecordedRequest recordedRequest = takeRequest(server);
            assertEquals(MODEL_GET_PATH, recordedRequest.getPath());
            assertEquals("GET", recordedRequest.getMethod());
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_checkAvailabilityNow_partiallyDeployed_returnsTrue() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            // PARTIALLY_DEPLOYED = deployed on a subset of eligible nodes; predict still works.
            server.enqueue(new MockResponse().setBody(modelResponse("PARTIALLY_DEPLOYED", "TEXT_EMBEDDING", 3))
                    .setHeader("Content-Type", "application/json"));
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.initHttpClient();

            assertTrue(client.checkAvailabilityNow(), "a PARTIALLY_DEPLOYED model should be available");
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_checkAvailabilityNow_registered_returnsFalse() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            // Verified live on 3.7: predict on a merely-REGISTERED local model is a hard
            // 400 ("Model not ready yet"), so REGISTERED must report unavailable.
            server.enqueue(new MockResponse().setBody(modelResponse("REGISTERED", "TEXT_EMBEDDING", 3))
                    .setHeader("Content-Type", "application/json"));
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.initHttpClient();

            assertFalse(client.checkAvailabilityNow(), "a REGISTERED (undeployed) model should be unavailable");
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_checkAvailabilityNow_deployFailed_returnsFalse() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            server.enqueue(new MockResponse().setBody(modelResponse("DEPLOY_FAILED", "TEXT_EMBEDDING", 3))
                    .setHeader("Content-Type", "application/json"));
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.initHttpClient();

            assertFalse(client.checkAvailabilityNow(), "a DEPLOY_FAILED model should be unavailable");
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_checkAvailabilityNow_non2xxResponse_returnsFalse() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            // Captured live: unknown model_id is a 404 status_exception.
            server.enqueue(new MockResponse().setResponseCode(404)
                    .setBody("{\"error\":{\"type\":\"status_exception\",\"reason\":\"Failed to find model\"},\"status\":404}"));
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.initHttpClient();

            assertFalse(client.checkAvailabilityNow(), "a non-2xx model get response should be treated as unavailable");
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_checkAvailabilityNow_garbageBody_returnsFalse() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            // A 2xx response whose body is not the expected model JSON must degrade to
            // unavailable instead of throwing out of the availability check.
            server.enqueue(new MockResponse().setResponseCode(200).setBody("this is not json"));
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.initHttpClient();

            assertFalse(client.checkAvailabilityNow(), "a 2xx garbage body should be treated as unavailable");
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_getModelId_rejectsPathBreakingCharacters() {
        client.setTestModelIdRaw("abc/def");
        try {
            client.getModelId();
            fail("a model id containing a path separator must be rejected");
        } catch (final org.codelibs.fess.embedding.EmbeddingException e) {
            assertTrue(e.getMessage().contains("model.id"));
        }
    }

    @Test
    public void test_getModelId_acceptsUrlSafeTokens() {
        client.setTestModelIdRaw("sSjdiJ8BWmtE7BFD9V0M");
        assertEquals("sSjdiJ8BWmtE7BFD9V0M", client.getModelId());
        client.setTestModelIdRaw("model_id-123");
        assertEquals("model_id-123", client.getModelId());
    }

    @Test
    public void test_checkAvailabilityNow_connectionFailure_returnsFalse() throws Exception {
        // Start then immediately shut down the server so the port refuses connections,
        // driving checkAvailabilityNow()'s catch block deterministically.
        final MockWebServer server = new MockWebServer();
        server.start();
        final String url = server.url("").toString().replaceAll("/$", "");
        server.shutdown();

        client.setTestApiUrl(url);
        client.setTestDimension(3);
        client.initHttpClient();

        assertFalse(client.checkAvailabilityNow(), "a connection failure during the check should return false, not throw");
    }

    @Test
    public void test_checkAvailabilityNow_blankModelId_returnsFalseWithoutRequest() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestModelId("");
            client.setTestDimension(3);
            client.initHttpClient();

            assertFalse(client.checkAvailabilityNow(), "a blank model.id should be unavailable");
            assertEquals("a blank model.id must short-circuit without a network call", 0, server.getRequestCount());
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_checkAvailabilityNow_warnsOnAlgorithmMismatch() throws Exception {
        final MockWebServer server = new MockWebServer();
        final LogCapturingAppender capture = LogCapturingAppender.attach(OpenSearchEmbeddingClient.class);
        try {
            // A deployed model of the wrong algorithm still reports available (predict
            // may work), but a WARN diagnostic must surface the likely misconfiguration.
            server.enqueue(new MockResponse().setBody(modelResponse("DEPLOYED", "SPARSE_ENCODING", 3))
                    .setHeader("Content-Type", "application/json"));
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.initHttpClient();

            assertTrue(client.checkAvailabilityNow(), "the algorithm diagnostic must not fail the availability check");
            assertTrue(capture.warnings().stream().anyMatch(m -> m.contains("SPARSE_ENCODING") && m.contains("TEXT_EMBEDDING")),
                    "a non-TEXT_EMBEDDING algorithm must emit a WARN naming both algorithms: " + capture.warnings());
        } finally {
            capture.detach();
            server.shutdown();
        }
    }

    @Test
    public void test_checkAvailabilityNow_warnsOnDimensionMismatch() throws Exception {
        final MockWebServer server = new MockWebServer();
        final LogCapturingAppender capture = LogCapturingAppender.attach(OpenSearchEmbeddingClient.class);
        try {
            // The model reports 384 dims but the configured dimension is 3: still
            // available (diagnostic only), but the mismatch must be WARNed at check time
            // instead of surfacing later as a per-embed dimension-mismatch failure.
            server.enqueue(new MockResponse().setBody(modelResponse("DEPLOYED", "TEXT_EMBEDDING", 384))
                    .setHeader("Content-Type", "application/json"));
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.initHttpClient();

            assertTrue(client.checkAvailabilityNow(), "the dimension diagnostic must not fail the availability check");
            assertTrue(capture.warnings().stream().anyMatch(m -> m.contains("dimension") && m.contains("384") && m.contains("3")),
                    "an embedding_dimension mismatch must emit a WARN carrying both values: " + capture.warnings());
        } finally {
            capture.detach();
            server.shutdown();
        }
    }

    // ========== Basic authentication ==========

    @Test
    public void test_basicAuthHeader_sentWhenCredentialsConfigured() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            final String responseJson = "{\"inference_results\":["
                    + "{\"output\":[{\"name\":\"sentence_embedding\",\"data_type\":\"FLOAT32\",\"shape\":[3],\"data\":[0.1,0.2,0.3]}]}]}";
            server.enqueue(new MockResponse().setBody(responseJson).setHeader("Content-Type", "application/json"));
            server.enqueue(new MockResponse().setBody(modelResponse("DEPLOYED", "TEXT_EMBEDDING", 3))
                    .setHeader("Content-Type", "application/json"));
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.setTestUsername("admin");
            client.setTestPassword("secret");
            client.initHttpClient();

            client.embedDocuments(List.of("chunk one"));
            assertTrue(client.checkAvailabilityNow(), "availability with credentials should succeed");

            // base64("admin:secret") = YWRtaW46c2VjcmV0; the header must be preemptive
            // (sent on the first request, no 401 challenge round-trip) on both endpoints.
            final RecordedRequest predictRequest = takeRequest(server);
            assertEquals(PREDICT_PATH, predictRequest.getPath());
            assertEquals("Basic YWRtaW46c2VjcmV0", predictRequest.getHeader("Authorization"));
            final RecordedRequest modelGetRequest = takeRequest(server);
            assertEquals(MODEL_GET_PATH, modelGetRequest.getPath());
            assertEquals("Basic YWRtaW46c2VjcmV0", modelGetRequest.getHeader("Authorization"));
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_basicAuthHeader_absentWhenNoCredentials() throws Exception {
        final MockWebServer server = new MockWebServer();
        try {
            final String responseJson = "{\"inference_results\":["
                    + "{\"output\":[{\"name\":\"sentence_embedding\",\"data_type\":\"FLOAT32\",\"shape\":[3],\"data\":[0.1,0.2,0.3]}]}]}";
            server.enqueue(new MockResponse().setBody(responseJson).setHeader("Content-Type", "application/json"));
            server.start();

            client.setTestApiUrl(server.url("").toString().replaceAll("/$", ""));
            client.setTestDimension(3);
            client.initHttpClient();

            client.embedDocuments(List.of("chunk one"));

            final RecordedRequest recordedRequest = takeRequest(server);
            assertNull(recordedRequest.getHeader("Authorization"), "no Authorization header should be sent without credentials");
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void test_getDimension_throwsWhenUnconfigured() {
        client.setTestDimension(null);
        try {
            client.getDimension();
            fail("expected EmbeddingException when dimension is unconfigured");
        } catch (final EmbeddingException e) {
            // expected
        }
    }

    // ========== Real (non-overridden) getApiUrl() coverage ==========
    //
    // The tests above exercise TestableOpenSearchEmbeddingClient's getApiUrl()
    // override. These tests use a plain `new OpenSearchEmbeddingClient()` with a
    // FessConfig.SimpleImpl stub to drive the real resolution chain: configured
    // content_chunker.embedding.opensearch.api.url -> the
    // fess.search_engine.http_address system property -> http://localhost:9200,
    // with trailing-slash-only normalization (no Ollama-style /api stripping).

    /** The real config key read by the production {@link OpenSearchEmbeddingClient#getApiUrl()}. */
    private static final String API_URL_CONFIG_KEY = "content_chunker.embedding.opensearch.api.url";

    /** The system property fess core sets to its search engine address (Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS). */
    private static final String SEARCH_ENGINE_ADDRESS_PROPERTY = "fess.search_engine.http_address";

    /**
     * Installs a FessConfig stub whose getOrDefault returns {@code apiUrlValue} for the
     * api.url key and the caller-supplied default for everything else.
     */
    private FessConfig installFessConfigStub(final String apiUrlValue) {
        final FessConfig original = ComponentUtil.getFessConfig();
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getOrDefault(final String key, final String defaultValue) {
                if (API_URL_CONFIG_KEY.equals(key) && apiUrlValue != null) {
                    return apiUrlValue;
                }
                return defaultValue;
            }
        });
        return original;
    }

    @Test
    public void test_getApiUrl_real_configuredValueWins_trailingSlashStripped() {
        final FessConfig original = installFessConfigStub("http://configured:9200///");
        final String oldAddress = System.getProperty(SEARCH_ENGINE_ADDRESS_PROPERTY);
        System.setProperty(SEARCH_ENGINE_ADDRESS_PROPERTY, "http://engine:9200");
        try {
            final OpenSearchEmbeddingClient realClient = new OpenSearchEmbeddingClient();
            assertEquals("http://configured:9200", realClient.getApiUrl());
        } finally {
            restoreSystemProperty(SEARCH_ENGINE_ADDRESS_PROPERTY, oldAddress);
            ComponentUtil.setFessConfig(original);
        }
    }

    @Test
    public void test_getApiUrl_real_fallsBackToSearchEngineHttpAddress() {
        final FessConfig original = installFessConfigStub(null);
        final String oldAddress = System.getProperty(SEARCH_ENGINE_ADDRESS_PROPERTY);
        System.setProperty(SEARCH_ENGINE_ADDRESS_PROPERTY, "http://engine:9200/");
        try {
            final OpenSearchEmbeddingClient realClient = new OpenSearchEmbeddingClient();
            assertEquals("http://engine:9200", realClient.getApiUrl());
        } finally {
            restoreSystemProperty(SEARCH_ENGINE_ADDRESS_PROPERTY, oldAddress);
            ComponentUtil.setFessConfig(original);
        }
    }

    @Test
    public void test_getApiUrl_real_defaultsToLocalhost() {
        final FessConfig original = installFessConfigStub(null);
        final String oldAddress = System.getProperty(SEARCH_ENGINE_ADDRESS_PROPERTY);
        System.clearProperty(SEARCH_ENGINE_ADDRESS_PROPERTY);
        try {
            final OpenSearchEmbeddingClient realClient = new OpenSearchEmbeddingClient();
            assertEquals("http://localhost:9200", realClient.getApiUrl());
        } finally {
            restoreSystemProperty(SEARCH_ENGINE_ADDRESS_PROPERTY, oldAddress);
            ComponentUtil.setFessConfig(original);
        }
    }

    @Test
    public void test_getApiUrl_real_doesNotStripApiSegment() {
        // The Ollama sibling strips a trailing "/api" segment; that normalization is
        // Ollama-specific and must NOT be applied to an OpenSearch base URL.
        final FessConfig original = installFessConfigStub("http://configured:9200/api/");
        try {
            final OpenSearchEmbeddingClient realClient = new OpenSearchEmbeddingClient();
            assertEquals("http://configured:9200/api", realClient.getApiUrl());
        } finally {
            ComponentUtil.setFessConfig(original);
        }
    }

    /** Restores a system property to its pre-test value ({@code null} = clear it). */
    private static void restoreSystemProperty(final String key, final String oldValue) {
        if (oldValue == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, oldValue);
        }
    }

    // ========== Real (non-overridden) getDimension() coverage ==========
    //
    // The tests above exercise TestableOpenSearchEmbeddingClient's own hand-written
    // getDimension() override, never the production method. These tests use a
    // plain `new OpenSearchEmbeddingClient()` (no subclass) to drive the real
    // ComponentUtil.getFessConfig().getSystemProperty("content_chunker.embedding.dimension", ...)
    // config-read seam directly, via the "systemProperties" test component
    // registered in test_app.xml (org.codelibs.fess.unit.TestSystemProperties).
    // That component instance is not guaranteed to be recreated per test method,
    // so each test explicitly sets/removes the key it needs and restores it in a
    // finally block to stay order-independent.

    @Test
    public void test_getDimension_real_throwsWhenUnconfigured() {
        ComponentUtil.getSystemProperties().remove(DIMENSION_CONFIG_KEY);
        try {
            final OpenSearchEmbeddingClient realClient = new OpenSearchEmbeddingClient();
            try {
                realClient.getDimension();
                fail("expected EmbeddingException when dimension is unconfigured");
            } catch (final EmbeddingException e) {
                assertTrue(e.getMessage().contains("not configured"), "message should mention not configured: " + e.getMessage());
            }
        } finally {
            ComponentUtil.getSystemProperties().remove(DIMENSION_CONFIG_KEY);
        }
    }

    @Test
    public void test_getDimension_real_throwsOnNonNumericValue() {
        ComponentUtil.getSystemProperties().setProperty(DIMENSION_CONFIG_KEY, "not-a-number");
        try {
            final OpenSearchEmbeddingClient realClient = new OpenSearchEmbeddingClient();
            try {
                realClient.getDimension();
                fail("expected EmbeddingException on non-numeric dimension value");
            } catch (final EmbeddingException e) {
                assertTrue(e.getMessage().contains("Invalid"), "message should mention the invalid value: " + e.getMessage());
                assertTrue(e.getCause() instanceof NumberFormatException, "cause should be NumberFormatException: " + e.getCause());
            }
        } finally {
            ComponentUtil.getSystemProperties().remove(DIMENSION_CONFIG_KEY);
        }
    }

    @Test
    public void test_getDimension_real_returnsConfiguredValue() {
        ComponentUtil.getSystemProperties().setProperty(DIMENSION_CONFIG_KEY, "384");
        try {
            final OpenSearchEmbeddingClient realClient = new OpenSearchEmbeddingClient();
            assertEquals(384, realClient.getDimension());
        } finally {
            ComponentUtil.getSystemProperties().remove(DIMENSION_CONFIG_KEY);
        }
    }

    @Test
    public void test_getDimension_real_throwsOnZero() {
        // A parseable but non-positive value must be rejected up front with a clear
        // EmbeddingException, not returned as 0 (which later surfaces as a misleading
        // "dimension mismatch" or a NegativeArraySizeException in parsePredictResponse).
        ComponentUtil.getSystemProperties().setProperty(DIMENSION_CONFIG_KEY, "0");
        try {
            final OpenSearchEmbeddingClient realClient = new OpenSearchEmbeddingClient();
            try {
                realClient.getDimension();
                fail("expected EmbeddingException on zero dimension value");
            } catch (final EmbeddingException e) {
                assertTrue(e.getMessage().contains("positive"), "message should mention it must be positive: " + e.getMessage());
                assertTrue(e.getMessage().contains("0"), "message should echo the offending value: " + e.getMessage());
            }
        } finally {
            ComponentUtil.getSystemProperties().remove(DIMENSION_CONFIG_KEY);
        }
    }

    @Test
    public void test_getDimension_real_throwsOnNegative() {
        ComponentUtil.getSystemProperties().setProperty(DIMENSION_CONFIG_KEY, "-5");
        try {
            final OpenSearchEmbeddingClient realClient = new OpenSearchEmbeddingClient();
            try {
                realClient.getDimension();
                fail("expected EmbeddingException on negative dimension value");
            } catch (final EmbeddingException e) {
                assertTrue(e.getMessage().contains("positive"), "message should mention it must be positive: " + e.getMessage());
                assertTrue(e.getMessage().contains("-5"), "message should echo the offending value: " + e.getMessage());
            }
        } finally {
            ComponentUtil.getSystemProperties().remove(DIMENSION_CONFIG_KEY);
        }
    }

    // ========== Real (non-overridden) getRetryBaseDelayMs() coverage ==========
    //
    // The TestableOpenSearchEmbeddingClient overrides getRetryBaseDelayMs(), so these
    // tests drive the production method directly via a plain OpenSearchEmbeddingClient
    // whose config read is redirected through a FessConfig.SimpleImpl stub. A typo'd
    // (non-numeric) value must fall back to the 2000ms default AND emit a WARN so the
    // misconfiguration is visible to operators.

    /** The real config key read by the production {@link OpenSearchEmbeddingClient#getRetryBaseDelayMs()}. */
    private static final String RETRY_BASE_DELAY_CONFIG_KEY = "content_chunker.embedding.opensearch.retry.base.delay.ms";

    @Test
    public void test_getRetryBaseDelayMs_real_invalidValue_warnsAndReturnsDefault() {
        final FessConfig original = ComponentUtil.getFessConfig();
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getOrDefault(final String key, final String defaultValue) {
                if (RETRY_BASE_DELAY_CONFIG_KEY.equals(key)) {
                    return "not-a-number";
                }
                return defaultValue;
            }
        });
        final LogCapturingAppender capture = LogCapturingAppender.attach(OpenSearchEmbeddingClient.class);
        try {
            final OpenSearchEmbeddingClient realClient = new OpenSearchEmbeddingClient();
            assertEquals(2000L, realClient.getRetryBaseDelayMs());
            assertTrue(capture.warnings().stream().anyMatch(m -> m.contains("retry.base.delay.ms") && m.contains("not-a-number")),
                    "an invalid retry.base.delay.ms must emit a WARN naming the key and value: " + capture.warnings());
        } finally {
            capture.detach();
            ComponentUtil.setFessConfig(original);
        }
    }

    @Test
    public void test_getRetryBaseDelayMs_real_validValue_isParsed() {
        final FessConfig original = ComponentUtil.getFessConfig();
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getOrDefault(final String key, final String defaultValue) {
                if (RETRY_BASE_DELAY_CONFIG_KEY.equals(key)) {
                    return "500";
                }
                return defaultValue;
            }
        });
        try {
            final OpenSearchEmbeddingClient realClient = new OpenSearchEmbeddingClient();
            assertEquals(500L, realClient.getRetryBaseDelayMs());
        } finally {
            ComponentUtil.setFessConfig(original);
        }
    }

    // ========== helpers ==========

    /** Takes the next recorded request with a timeout so a missing request fails fast instead of hanging the suite. */
    private static RecordedRequest takeRequest(final MockWebServer server) throws InterruptedException {
        final RecordedRequest request = server.takeRequest(10, TimeUnit.SECONDS);
        if (request == null) {
            throw new AssertionError("no request recorded within 10 seconds");
        }
        return request;
    }

    /**
     * Builds a {@code _predict} response of {@code count} inference results whose
     * {@code sentence_embedding} tensor has {@code dim} components, each vector's
     * first component set to {@code firstComponent}.
     */
    private static String predictResponse(final int count, final int dim, final float firstComponent) {
        final StringBuilder sb = new StringBuilder("{\"inference_results\":[");
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append("{\"output\":[{\"name\":\"sentence_embedding\",\"data_type\":\"FLOAT32\",\"shape\":[")
                    .append(dim)
                    .append("],\"data\":[");
            for (int j = 0; j < dim; j++) {
                if (j > 0) {
                    sb.append(',');
                }
                sb.append(j == 0 ? firstComponent : 0.5f);
            }
            sb.append("]}]}");
        }
        return sb.append("]}").toString();
    }

    /**
     * Builds a {@code GET /_plugins/_ml/models/{id}} response body with the given
     * {@code model_state}, {@code algorithm}, and {@code model_config.embedding_dimension},
     * shaped like the response captured live from OpenSearch 3.7.0.
     */
    private static String modelResponse(final String modelState, final String algorithm, final int embeddingDimension) {
        return "{\"name\":\"huggingface/sentence-transformers/all-MiniLM-L6-v2\",\"model_group_id\":\"g\",\"algorithm\":\"" + algorithm
                + "\",\"model_version\":\"1\",\"model_format\":\"TORCH_SCRIPT\",\"model_state\":\"" + modelState
                + "\",\"model_config\":{\"model_type\":\"bert\",\"embedding_dimension\":" + embeddingDimension
                + ",\"framework_type\":\"SENTENCE_TRANSFORMERS\"},\"current_worker_node_count\":1,\"planning_worker_node_count\":1}";
    }

    /** Counts non-overlapping occurrences of {@code needle} in {@code haystack}. */
    private static int countOccurrences(final String haystack, final String needle) {
        int count = 0;
        int idx = 0;
        while ((idx = haystack.indexOf(needle, idx)) >= 0) {
            count++;
            idx += needle.length();
        }
        return count;
    }

    static class TestableOpenSearchEmbeddingClient extends OpenSearchEmbeddingClient {

        private String testApiUrl = "http://localhost:9200";
        private String testModelId = TEST_MODEL_ID;
        private int testTimeout = 30000;
        private int testConnectTimeout = 5000;
        private int testRetryMax = 3;
        private long testRetryBaseDelayMs = 2000L;
        private Integer testDimension = 384;
        private String testDocumentPrefix = DEFAULT_DOCUMENT_PREFIX;
        private String testQueryPrefix = DEFAULT_QUERY_PREFIX;
        private String testUsername = "";
        private String testPassword = "";

        void setTestApiUrl(final String apiUrl) {
            this.testApiUrl = apiUrl;
        }

        void setTestModelId(final String modelId) {
            this.testModelId = modelId;
        }

        void setTestDimension(final Integer dimension) {
            this.testDimension = dimension;
        }

        void setTestRetryMax(final int max) {
            this.testRetryMax = max;
        }

        void setTestRetryBaseDelayMs(final long ms) {
            this.testRetryBaseDelayMs = ms;
        }

        void setTestDocumentPrefix(final String prefix) {
            this.testDocumentPrefix = prefix;
        }

        void setTestQueryPrefix(final String prefix) {
            this.testQueryPrefix = prefix;
        }

        void setTestUsername(final String username) {
            this.testUsername = username;
        }

        void setTestPassword(final String password) {
            this.testPassword = password;
        }

        @Override
        protected String getApiUrl() {
            return testApiUrl;
        }

        @Override
        protected String getModelId() {
            if (testRawModelId != null) {
                // route through the real implementation so its validation runs
                return super.getModelId();
            }
            return testModelId;
        }

        /** When non-null, {@link #getModelId()} exercises the real validating path with this raw config value. */
        private String testRawModelId;

        void setTestModelIdRaw(final String raw) {
            this.testRawModelId = raw;
        }

        @Override
        protected String getConfigString(final String keySuffix, final String defaultValue) {
            if (testRawModelId != null && "model.id".equals(keySuffix)) {
                return testRawModelId;
            }
            return super.getConfigString(keySuffix, defaultValue);
        }

        @Override
        public int getDimension() {
            if (testDimension == null) {
                throw new EmbeddingException("content_chunker.embedding.dimension is not configured");
            }
            return testDimension;
        }

        @Override
        protected int getTimeout() {
            return testTimeout;
        }

        @Override
        protected int getConnectTimeout() {
            return testConnectTimeout;
        }

        @Override
        protected int getRetryMaxAttempts() {
            return testRetryMax;
        }

        @Override
        protected long getRetryBaseDelayMs() {
            return testRetryBaseDelayMs;
        }

        @Override
        protected String getDocumentPrefix() {
            return testDocumentPrefix;
        }

        @Override
        protected String getQueryPrefix() {
            return testQueryPrefix;
        }

        @Override
        protected String getUsername() {
            return testUsername;
        }

        @Override
        protected String getPassword() {
            return testPassword;
        }

        void initHttpClient() {
            httpClient = buildHttpClient();
        }
    }

    /**
     * Minimal in-memory log4j2 appender for asserting on emitted log messages.
     * Mirrors {@code OllamaEmbeddingClientTest.LogCapturingAppender}.
     */
    static final class LogCapturingAppender extends AbstractAppender {
        private final List<LogEvent> events = new CopyOnWriteArrayList<>();
        private final Logger boundLogger;

        private LogCapturingAppender(final Logger logger) {
            super("LogCapturingAppender-" + UUID.randomUUID(), null, null, true, Property.EMPTY_ARRAY);
            this.boundLogger = logger;
        }

        static LogCapturingAppender attach(final Class<?> targetClass) {
            final Logger logger = (Logger) LogManager.getLogger(targetClass);
            final LogCapturingAppender appender = new LogCapturingAppender(logger);
            appender.start();
            logger.addAppender(appender);
            return appender;
        }

        void detach() {
            boundLogger.removeAppender(this);
            stop();
        }

        @Override
        public void append(final LogEvent event) {
            events.add(event.toImmutable());
        }

        List<String> messagesAt(final Level level) {
            return events.stream().filter(e -> e.getLevel() == level).map(e -> e.getMessage().getFormattedMessage()).toList();
        }

        List<String> warnings() {
            return messagesAt(Level.WARN);
        }
    }
}
