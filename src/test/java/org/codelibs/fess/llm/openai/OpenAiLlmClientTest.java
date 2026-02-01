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
package org.codelibs.fess.llm.openai;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.llm.LlmChatRequest;
import org.codelibs.fess.llm.LlmChatResponse;
import org.codelibs.fess.llm.LlmException;
import org.codelibs.fess.llm.LlmMessage;
import org.codelibs.fess.llm.LlmStreamCallback;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class OpenAiLlmClientTest extends UnitFessTestCase {

    private TestableOpenAiLlmClient client;
    private MockWebServer mockServer;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        client = new TestableOpenAiLlmClient();
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        super.tearDown(testInfo);
        if (mockServer != null) {
            mockServer.shutdown();
        }
    }

    @Test
    public void test_getName() {
        assertEquals("openai", client.getName());
    }

    @Test
    public void test_isAvailable_noApiKey() {
        client.setTestApiKey("");
        client.setTestApiUrl("https://api.openai.com/v1");
        assertFalse(client.isAvailable());
    }

    @Test
    public void test_isAvailable_nullApiKey() {
        client.setTestApiKey(null);
        client.setTestApiUrl("https://api.openai.com/v1");
        assertFalse(client.isAvailable());
    }

    @Test
    public void test_isAvailable_noApiUrl() {
        client.setTestApiKey("sk-test-key");
        client.setTestApiUrl("");
        assertFalse(client.isAvailable());
    }

    @Test
    public void test_isAvailable_nullApiUrl() {
        client.setTestApiKey("sk-test-key");
        client.setTestApiUrl(null);
        assertFalse(client.isAvailable());
    }

    @Test
    public void test_isAvailable_valid() throws IOException {
        // Mock the /models endpoint for availability check
        mockServer.enqueue(new MockResponse().setBody("{\"data\":[]}").addHeader("Content-Type", "application/json"));
        setupClientForMockServer();
        assertTrue(client.isAvailable());
    }

    @Test
    public void test_convertMessage_user() {
        final LlmMessage message = LlmMessage.user("Hello, how are you?");
        final Map<String, String> result = client.convertMessage(message);

        assertEquals("user", result.get("role"));
        assertEquals("Hello, how are you?", result.get("content"));
    }

    @Test
    public void test_convertMessage_assistant() {
        final LlmMessage message = LlmMessage.assistant("I'm doing well, thank you!");
        final Map<String, String> result = client.convertMessage(message);

        assertEquals("assistant", result.get("role"));
        assertEquals("I'm doing well, thank you!", result.get("content"));
    }

    @Test
    public void test_convertMessage_system() {
        final LlmMessage message = LlmMessage.system("You are a helpful assistant.");
        final Map<String, String> result = client.convertMessage(message);

        assertEquals("system", result.get("role"));
        assertEquals("You are a helpful assistant.", result.get("content"));
    }

    @Test
    public void test_buildRequestBody_defaultValues() {
        client.setTestModel("gpt-5-mini");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, false);

        assertEquals("gpt-5-mini", body.get("model"));
        assertEquals(false, body.get("stream"));
        assertEquals(0.7, body.get("temperature"));
        assertEquals(4096, body.get("max_tokens"));

        @SuppressWarnings("unchecked")
        final List<Map<String, String>> messages = (List<Map<String, String>>) body.get("messages");
        assertEquals(1, messages.size());
        assertEquals("user", messages.get(0).get("role"));
        assertEquals("Hello", messages.get(0).get("content"));
    }

    @Test
    public void test_buildRequestBody_withRequestModel() {
        client.setTestModel("gpt-5-mini");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().setModel("gpt-3.5-turbo").addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, false);

        assertEquals("gpt-3.5-turbo", body.get("model"));
    }

    @Test
    public void test_buildRequestBody_withRequestTemperature() {
        client.setTestModel("gpt-5-mini");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().setTemperature(0.5).addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, false);

        assertEquals(0.5, body.get("temperature"));
    }

    @Test
    public void test_buildRequestBody_withRequestMaxTokens() {
        client.setTestModel("gpt-5-mini");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().setMaxTokens(1000).addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, false);

        assertEquals(1000, body.get("max_tokens"));
    }

    @Test
    public void test_buildRequestBody_streaming() {
        client.setTestModel("gpt-5-mini");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, true);

        assertEquals(true, body.get("stream"));
    }

    @Test
    public void test_buildRequestBody_multipleMessages() {
        client.setTestModel("gpt-5-mini");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().addSystemMessage("You are a helpful assistant.")
                .addUserMessage("What is the weather?")
                .addAssistantMessage("I cannot access weather information.")
                .addUserMessage("OK");

        final Map<String, Object> body = client.buildRequestBody(request, false);

        @SuppressWarnings("unchecked")
        final List<Map<String, String>> messages = (List<Map<String, String>>) body.get("messages");
        assertEquals(4, messages.size());

        assertEquals("system", messages.get(0).get("role"));
        assertEquals("You are a helpful assistant.", messages.get(0).get("content"));

        assertEquals("user", messages.get(1).get("role"));
        assertEquals("What is the weather?", messages.get(1).get("content"));

        assertEquals("assistant", messages.get(2).get("role"));
        assertEquals("I cannot access weather information.", messages.get(2).get("content"));

        assertEquals("user", messages.get(3).get("role"));
        assertEquals("OK", messages.get(3).get("content"));
    }

    @Test
    public void test_buildRequestBody_blankModelUsesDefault() {
        client.setTestModel("gpt-5-mini");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().setModel("").addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, false);

        assertEquals("gpt-5-mini", body.get("model"));
    }

    @Test
    public void test_buildRequestBody_nullModelUsesDefault() {
        client.setTestModel("gpt-5-mini");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().setModel(null).addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, false);

        assertEquals("gpt-5-mini", body.get("model"));
    }

    @Test
    public void test_init() {
        client.setTestTimeout(30000);
        client.init();
        assertNotNull(client.getHttpClient());
    }

    @Test
    public void test_getHttpClient_lazyInitialization() {
        client.setTestTimeout(60000);
        // First call should initialize the client
        assertNotNull(client.getHttpClient());
        // Second call should return the same client
        assertNotNull(client.getHttpClient());
    }

    // ========== chat() method tests ==========

    @Test
    public void test_chat_success() throws IOException {
        final String responseJson = """
                {
                    "id": "chatcmpl-123",
                    "object": "chat.completion",
                    "created": 1677652288,
                    "model": "gpt-4",
                    "choices": [{
                        "index": 0,
                        "message": {
                            "role": "assistant",
                            "content": "Hello! How can I help you today?"
                        },
                        "finish_reason": "stop"
                    }],
                    "usage": {
                        "prompt_tokens": 10,
                        "completion_tokens": 20,
                        "total_tokens": 30
                    }
                }
                """;

        mockServer.enqueue(new MockResponse().setBody(responseJson).addHeader("Content-Type", "application/json"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");
        final LlmChatResponse response = client.chat(request);

        assertEquals("Hello! How can I help you today?", response.getContent());
        assertEquals("stop", response.getFinishReason());
        assertEquals("gpt-4", response.getModel());
        assertEquals(10, response.getPromptTokens());
        assertEquals(20, response.getCompletionTokens());
        assertEquals(30, response.getTotalTokens());
    }

    @Test
    public void test_chat_successWithMinimalResponse() throws IOException {
        final String responseJson = """
                {
                    "choices": [{
                        "message": {
                            "content": "Response text"
                        }
                    }]
                }
                """;

        mockServer.enqueue(new MockResponse().setBody(responseJson).addHeader("Content-Type", "application/json"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");
        final LlmChatResponse response = client.chat(request);

        assertEquals("Response text", response.getContent());
    }

    @Test
    public void test_chat_errorResponse_withBody() throws IOException {
        final String errorJson = """
                {
                    "error": {
                        "message": "Invalid API key provided",
                        "type": "invalid_request_error",
                        "code": "invalid_api_key"
                    }
                }
                """;

        mockServer.enqueue(new MockResponse().setResponseCode(401).setBody(errorJson).addHeader("Content-Type", "application/json"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");

        try {
            client.chat(request);
            fail("Expected LlmException to be thrown");
        } catch (final LlmException error) {
            assertTrue(error.getMessage().contains("401"));
        }
    }

    @Test
    public void test_chat_errorResponse_rateLimitExceeded() throws IOException {
        final String errorJson = """
                {
                    "error": {
                        "message": "Rate limit exceeded",
                        "type": "rate_limit_error",
                        "code": "rate_limit_exceeded"
                    }
                }
                """;

        mockServer.enqueue(new MockResponse().setResponseCode(429).setBody(errorJson).addHeader("Content-Type", "application/json"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");

        try {
            client.chat(request);
            fail("Expected LlmException to be thrown");
        } catch (final LlmException error) {
            assertTrue(error.getMessage().contains("429"));
        }
    }

    @Test
    public void test_chat_errorResponse_serverError() throws IOException {
        final String errorJson = """
                {
                    "error": {
                        "message": "Internal server error",
                        "type": "server_error"
                    }
                }
                """;

        mockServer.enqueue(new MockResponse().setResponseCode(500).setBody(errorJson).addHeader("Content-Type", "application/json"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");

        try {
            client.chat(request);
            fail("Expected LlmException to be thrown");
        } catch (final LlmException error) {
            assertTrue(error.getMessage().contains("500"));
        }
    }

    @Test
    public void test_chat_errorResponse_emptyBody() throws IOException {
        mockServer.enqueue(new MockResponse().setResponseCode(503).setBody("").addHeader("Content-Type", "application/json"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");

        try {
            client.chat(request);
            fail("Expected LlmException to be thrown");
        } catch (final LlmException error) {
            assertTrue(error.getMessage().contains("503"));
        }
    }

    @Test
    public void test_chat_emptyChoices() throws IOException {
        final String responseJson = """
                {
                    "choices": []
                }
                """;

        mockServer.enqueue(new MockResponse().setBody(responseJson).addHeader("Content-Type", "application/json"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");
        final LlmChatResponse response = client.chat(request);

        assertNull(response.getContent());
    }

    @Test
    public void test_chat_nullFinishReason() throws IOException {
        final String responseJson = """
                {
                    "choices": [{
                        "message": {
                            "content": "Test"
                        },
                        "finish_reason": null
                    }]
                }
                """;

        mockServer.enqueue(new MockResponse().setBody(responseJson).addHeader("Content-Type", "application/json"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");
        final LlmChatResponse response = client.chat(request);

        assertEquals("Test", response.getContent());
        assertNull(response.getFinishReason());
    }

    @Test
    public void test_chat_partialUsage() throws IOException {
        final String responseJson = """
                {
                    "choices": [{
                        "message": {
                            "content": "Test"
                        }
                    }],
                    "usage": {
                        "prompt_tokens": 5
                    }
                }
                """;

        mockServer.enqueue(new MockResponse().setBody(responseJson).addHeader("Content-Type", "application/json"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");
        final LlmChatResponse response = client.chat(request);

        assertEquals(5, response.getPromptTokens());
        assertNull(response.getCompletionTokens());
        assertNull(response.getTotalTokens());
    }

    // ========== streamChat() method tests ==========

    @Test
    public void test_streamChat_success() throws IOException {
        final String sseResponse = """
                data: {"id":"chatcmpl-123","choices":[{"delta":{"content":"Hello"}}]}

                data: {"id":"chatcmpl-123","choices":[{"delta":{"content":" World"}}]}

                data: {"id":"chatcmpl-123","choices":[{"delta":{},"finish_reason":"stop"}]}

                data: [DONE]

                """;

        mockServer.enqueue(new MockResponse().setBody(sseResponse).addHeader("Content-Type", "text/event-stream"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");
        final List<String> chunks = new ArrayList<>();
        final AtomicBoolean doneReceived = new AtomicBoolean(false);

        client.streamChat(request, new LlmStreamCallback() {
            @Override
            public void onChunk(final String content, final boolean done) {
                chunks.add(content);
                if (done) {
                    doneReceived.set(true);
                }
            }

            @Override
            public void onError(final Throwable error) {
                fail("Unexpected error: " + error.getMessage());
            }
        });

        assertEquals(3, chunks.size());
        assertEquals("Hello", chunks.get(0));
        assertEquals(" World", chunks.get(1));
        assertTrue(doneReceived.get());
    }

    @Test
    public void test_streamChat_multipleChunks() throws IOException {
        final String sseResponse = """
                data: {"choices":[{"delta":{"content":"A"}}]}

                data: {"choices":[{"delta":{"content":"B"}}]}

                data: {"choices":[{"delta":{"content":"C"}}]}

                data: {"choices":[{"delta":{"content":"D"}}]}

                data: {"choices":[{"delta":{},"finish_reason":"stop"}]}

                data: [DONE]

                """;

        mockServer.enqueue(new MockResponse().setBody(sseResponse).addHeader("Content-Type", "text/event-stream"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");
        final StringBuilder content = new StringBuilder();
        final AtomicInteger chunkCount = new AtomicInteger(0);

        client.streamChat(request, new LlmStreamCallback() {
            @Override
            public void onChunk(final String chunk, final boolean done) {
                content.append(chunk);
                chunkCount.incrementAndGet();
            }

            @Override
            public void onError(final Throwable error) {
                fail("Unexpected error: " + error.getMessage());
            }
        });

        assertEquals("ABCD", content.toString());
        assertTrue(chunkCount.get() >= 4);
    }

    @Test
    public void test_streamChat_errorResponse_withBody() throws IOException {
        final String errorJson = """
                {
                    "error": {
                        "message": "Insufficient quota",
                        "type": "insufficient_quota",
                        "code": "insufficient_quota"
                    }
                }
                """;

        mockServer.enqueue(new MockResponse().setResponseCode(429).setBody(errorJson).addHeader("Content-Type", "application/json"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");
        final AtomicBoolean errorReceived = new AtomicBoolean(false);

        try {
            client.streamChat(request, new LlmStreamCallback() {
                @Override
                public void onChunk(final String content, final boolean done) {
                    fail("Should not receive chunks on error");
                }

                @Override
                public void onError(final Throwable error) {
                    errorReceived.set(true);
                }
            });
            fail("Expected LlmException to be thrown");
        } catch (final LlmException error) {
            assertTrue(error.getMessage().contains("429"));
            assertTrue(errorReceived.get());
        }
    }

    @Test
    public void test_streamChat_errorResponse_serverError() throws IOException {
        final String errorJson = """
                {
                    "error": {
                        "message": "The server had an error while processing your request"
                    }
                }
                """;

        mockServer.enqueue(new MockResponse().setResponseCode(500).setBody(errorJson).addHeader("Content-Type", "application/json"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");
        final AtomicBoolean errorReceived = new AtomicBoolean(false);

        try {
            client.streamChat(request, new LlmStreamCallback() {
                @Override
                public void onChunk(final String content, final boolean done) {
                    fail("Should not receive chunks on error");
                }

                @Override
                public void onError(final Throwable error) {
                    errorReceived.set(true);
                }
            });
            fail("Expected LlmException to be thrown");
        } catch (final LlmException error) {
            assertTrue(error.getMessage().contains("500"));
            assertTrue(errorReceived.get());
        }
    }

    @Test
    public void test_streamChat_emptyBody() throws IOException {
        // Empty body with MockWebServer doesn't result in null body
        // Just verify that no chunks are received
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("").addHeader("Content-Type", "text/event-stream"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");
        final List<String> chunks = new ArrayList<>();

        client.streamChat(request, new LlmStreamCallback() {
            @Override
            public void onChunk(final String content, final boolean done) {
                chunks.add(content);
            }

            @Override
            public void onError(final Throwable error) {
                fail("Unexpected error: " + error.getMessage());
            }
        });

        // No chunks should be received for empty body
        assertEquals(0, chunks.size());
    }

    @Test
    public void test_streamChat_doneMarkerOnly() throws IOException {
        final String sseResponse = """
                data: [DONE]

                """;

        mockServer.enqueue(new MockResponse().setBody(sseResponse).addHeader("Content-Type", "text/event-stream"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");
        final AtomicBoolean doneReceived = new AtomicBoolean(false);

        client.streamChat(request, new LlmStreamCallback() {
            @Override
            public void onChunk(final String content, final boolean done) {
                if (done) {
                    doneReceived.set(true);
                }
            }

            @Override
            public void onError(final Throwable error) {
                fail("Unexpected error: " + error.getMessage());
            }
        });

        assertTrue(doneReceived.get());
    }

    @Test
    public void test_streamChat_finishReasonWithoutDone() throws IOException {
        final String sseResponse = """
                data: {"choices":[{"delta":{"content":"Test"}}]}

                data: {"choices":[{"delta":{},"finish_reason":"length"}]}

                """;

        mockServer.enqueue(new MockResponse().setBody(sseResponse).addHeader("Content-Type", "text/event-stream"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");
        final AtomicBoolean doneReceived = new AtomicBoolean(false);
        final List<String> chunks = new ArrayList<>();

        client.streamChat(request, new LlmStreamCallback() {
            @Override
            public void onChunk(final String content, final boolean done) {
                chunks.add(content);
                if (done) {
                    doneReceived.set(true);
                }
            }

            @Override
            public void onError(final Throwable error) {
                fail("Unexpected error: " + error.getMessage());
            }
        });

        assertEquals(2, chunks.size());
        assertEquals("Test", chunks.get(0));
        assertTrue(doneReceived.get());
    }

    @Test
    public void test_streamChat_malformedJson() throws IOException {
        final String sseResponse = """
                data: {"choices":[{"delta":{"content":"Hello"}}]}

                data: {invalid json}

                data: {"choices":[{"delta":{"content":" World"}}]}

                data: [DONE]

                """;

        mockServer.enqueue(new MockResponse().setBody(sseResponse).addHeader("Content-Type", "text/event-stream"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");
        final List<String> chunks = new ArrayList<>();

        client.streamChat(request, new LlmStreamCallback() {
            @Override
            public void onChunk(final String content, final boolean done) {
                if (!content.isEmpty()) {
                    chunks.add(content);
                }
            }

            @Override
            public void onError(final Throwable error) {
                // Malformed JSON is logged but doesn't stop streaming
            }
        });

        // Should still receive valid chunks
        assertTrue(chunks.size() >= 2);
        assertTrue(chunks.contains("Hello"));
        assertTrue(chunks.contains(" World"));
    }

    @Test
    public void test_streamChat_ignoresBlankLines() throws IOException {
        final String sseResponse = """

                data: {"choices":[{"delta":{"content":"Test"}}]}



                data: [DONE]

                """;

        mockServer.enqueue(new MockResponse().setBody(sseResponse).addHeader("Content-Type", "text/event-stream"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");
        final List<String> chunks = new ArrayList<>();

        client.streamChat(request, new LlmStreamCallback() {
            @Override
            public void onChunk(final String content, final boolean done) {
                chunks.add(content);
            }

            @Override
            public void onError(final Throwable error) {
                fail("Unexpected error: " + error.getMessage());
            }
        });

        assertEquals(2, chunks.size());
        assertEquals("Test", chunks.get(0));
    }

    @Test
    public void test_streamChat_ignoresNonDataLines() throws IOException {
        final String sseResponse = """
                event: message
                data: {"choices":[{"delta":{"content":"Test"}}]}

                : this is a comment
                data: [DONE]

                """;

        mockServer.enqueue(new MockResponse().setBody(sseResponse).addHeader("Content-Type", "text/event-stream"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");
        final List<String> chunks = new ArrayList<>();

        client.streamChat(request, new LlmStreamCallback() {
            @Override
            public void onChunk(final String content, final boolean done) {
                chunks.add(content);
            }

            @Override
            public void onError(final Throwable error) {
                fail("Unexpected error: " + error.getMessage());
            }
        });

        assertEquals(2, chunks.size());
        assertEquals("Test", chunks.get(0));
    }

    // ========== Helper methods ==========

    private void setupClientForMockServer() {
        final String baseUrl = mockServer.url("").toString();
        // Remove trailing slash
        final String apiUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        client.setTestApiUrl(apiUrl);
        client.setTestApiKey("sk-test-key");
        client.setTestModel("gpt-4");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);
        client.setTestTimeout(30000);
        client.init();
    }

    /**
     * Testable subclass of OpenAiLlmClient that allows setting configuration values
     * directly without depending on FessConfig.
     */
    private static class TestableOpenAiLlmClient extends OpenAiLlmClient {
        private String testApiKey = "";
        private String testApiUrl = "https://api.openai.com/v1";
        private String testModel = "gpt-5-mini";
        private int testTimeout = 60000;
        private double testTemperature = 0.7;
        private int testMaxTokens = 4096;

        void setTestApiKey(final String apiKey) {
            this.testApiKey = apiKey;
        }

        void setTestApiUrl(final String apiUrl) {
            this.testApiUrl = apiUrl;
        }

        void setTestModel(final String model) {
            this.testModel = model;
        }

        void setTestTimeout(final int timeout) {
            this.testTimeout = timeout;
        }

        void setTestTemperature(final double temperature) {
            this.testTemperature = temperature;
        }

        void setTestMaxTokens(final int maxTokens) {
            this.testMaxTokens = maxTokens;
        }

        @Override
        protected String getApiKey() {
            return testApiKey;
        }

        @Override
        protected String getApiUrl() {
            return testApiUrl;
        }

        @Override
        protected String getModel() {
            return testModel;
        }

        @Override
        protected int getTimeout() {
            return testTimeout;
        }

        @Override
        protected double getTemperature() {
            return testTemperature;
        }

        @Override
        protected int getMaxTokens() {
            return testMaxTokens;
        }

        @Override
        protected String getLlmType() {
            return NAME;
        }
    }
}
