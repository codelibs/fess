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
import okhttp3.mockwebserver.RecordedRequest;

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
        if (client != null) {
            client.destroy();
        }
        if (mockServer != null) {
            mockServer.shutdown();
        }
        super.tearDown(testInfo);
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
        assertEquals(4096, body.get("max_completion_tokens"));

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

        assertEquals(1000, body.get("max_completion_tokens"));
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

    // ========== destroy() tests ==========

    @Test
    public void test_destroy_closesHttpClient() {
        client.setTestTimeout(30000);
        client.init();
        assertNotNull(client.getHttpClient());
        client.destroy();
        // After destroy, calling getHttpClient() triggers re-init
        // Verify no exception is thrown during destroy
    }

    @Test
    public void test_destroy_beforeInit() {
        // destroy before init should not throw
        client.destroy();
    }

    // ========== Request format & Authorization header verification tests ==========

    @Test
    public void test_chat_verifyRequestFormat() throws Exception {
        final String responseJson = """
                {
                    "choices": [{
                        "message": {
                            "content": "Response"
                        }
                    }]
                }
                """;

        mockServer.enqueue(new MockResponse().setBody(responseJson).addHeader("Content-Type", "application/json"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");
        client.chat(request);

        final RecordedRequest recorded = mockServer.takeRequest();
        assertEquals("POST", recorded.getMethod());
        assertEquals("/chat/completions", recorded.getPath());
        assertEquals("application/json; charset=UTF-8", recorded.getHeader("Content-Type"));

        // Verify Authorization header
        assertEquals("Bearer sk-test-key", recorded.getHeader("Authorization"));

        // Verify body contains expected structure
        final String body = recorded.getBody().readUtf8();
        assertTrue(body.contains("\"model\""));
        assertTrue(body.contains("\"messages\""));
        assertTrue(body.contains("\"stream\":false"));
        assertTrue(body.contains("Hello"));
    }

    @Test
    public void test_chat_verifyAuthorizationHeader() throws Exception {
        final String responseJson = """
                {
                    "choices": [{
                        "message": {
                            "content": "Test"
                        }
                    }]
                }
                """;

        mockServer.enqueue(new MockResponse().setBody(responseJson).addHeader("Content-Type", "application/json"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");
        client.chat(request);

        final RecordedRequest recorded = mockServer.takeRequest();
        final String authHeader = recorded.getHeader("Authorization");
        assertNotNull(authHeader);
        assertTrue(authHeader.startsWith("Bearer "));
        assertEquals("Bearer sk-test-key", authHeader);
    }

    @Test
    public void test_streamChat_verifyRequestFormat() throws Exception {
        final String sseResponse = """
                data: {"choices":[{"delta":{"content":"Test"}}]}

                data: [DONE]

                """;

        mockServer.enqueue(new MockResponse().setBody(sseResponse).addHeader("Content-Type", "text/event-stream"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");
        client.streamChat(request, new LlmStreamCallback() {
            @Override
            public void onChunk(final String content, final boolean done) {
            }

            @Override
            public void onError(final Throwable error) {
            }
        });

        final RecordedRequest recorded = mockServer.takeRequest();
        assertEquals("POST", recorded.getMethod());
        assertEquals("/chat/completions", recorded.getPath());

        // Verify Authorization header is present for streaming requests
        assertEquals("Bearer sk-test-key", recorded.getHeader("Authorization"));

        // Verify body has stream=true
        final String body = recorded.getBody().readUtf8();
        assertTrue(body.contains("\"stream\":true"));
    }

    // ========== checkAvailabilityNow() tests ==========

    @Test
    public void test_checkAvailabilityNow_success() throws Exception {
        mockServer.enqueue(new MockResponse().setBody("{\"data\":[]}").addHeader("Content-Type", "application/json"));

        setupClientForMockServer();

        assertTrue(client.checkAvailabilityNow());

        final RecordedRequest recorded = mockServer.takeRequest();
        assertEquals("GET", recorded.getMethod());
        assertEquals("/models", recorded.getPath());

        // Verify Authorization header is present for availability check
        assertEquals("Bearer sk-test-key", recorded.getHeader("Authorization"));
    }

    @Test
    public void test_checkAvailabilityNow_serverError() throws IOException {
        mockServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Server Error"));

        setupClientForMockServer();

        assertFalse(client.checkAvailabilityNow());
    }

    @Test
    public void test_isAvailable_serverError() throws IOException {
        mockServer.enqueue(new MockResponse().setResponseCode(401).setBody("Unauthorized"));

        setupClientForMockServer();

        assertFalse(client.isAvailable());
    }

    @Test
    public void test_streamChat_serviceUnavailable() throws IOException {
        mockServer.enqueue(new MockResponse().setResponseCode(503).setBody("Service Unavailable"));

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
            assertTrue(error.getMessage().contains("503"));
            assertTrue(errorReceived.get());
        }
    }

    @Test
    public void test_chat_serviceUnavailable() throws IOException {
        mockServer.enqueue(new MockResponse().setResponseCode(503).setBody("Service Unavailable"));

        setupClientForMockServer();

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");

        try {
            client.chat(request);
            fail("Expected LlmException to be thrown");
        } catch (final LlmException error) {
            assertTrue(error.getMessage().contains("503"));
        }
    }

    // ========== useMaxCompletionTokens tests ==========

    @Test
    public void test_useMaxCompletionTokens_legacyModels() {
        assertFalse(client.useMaxCompletionTokens("gpt-3.5-turbo"));
        assertFalse(client.useMaxCompletionTokens("gpt-4"));
        assertFalse(client.useMaxCompletionTokens("gpt-4o"));
        assertFalse(client.useMaxCompletionTokens("gpt-4o-mini"));
        assertFalse(client.useMaxCompletionTokens("gpt-4-turbo"));
    }

    @Test
    public void test_useMaxCompletionTokens_newerModels() {
        assertTrue(client.useMaxCompletionTokens("o1"));
        assertTrue(client.useMaxCompletionTokens("o1-mini"));
        assertTrue(client.useMaxCompletionTokens("o1-preview"));
        assertTrue(client.useMaxCompletionTokens("o3"));
        assertTrue(client.useMaxCompletionTokens("o3-mini"));
        assertTrue(client.useMaxCompletionTokens("o4-mini"));
        assertTrue(client.useMaxCompletionTokens("gpt-5"));
        assertTrue(client.useMaxCompletionTokens("gpt-5-mini"));
        assertTrue(client.useMaxCompletionTokens("gpt-5.1"));
        assertTrue(client.useMaxCompletionTokens("gpt-5.2"));
    }

    @Test
    public void test_useMaxCompletionTokens_blankOrNull() {
        assertFalse(client.useMaxCompletionTokens(null));
        assertFalse(client.useMaxCompletionTokens(""));
        assertFalse(client.useMaxCompletionTokens("  "));
    }

    @Test
    public void test_buildRequestBody_legacyModel_usesMaxTokens() {
        client.setTestModel("gpt-4");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, false);

        assertEquals("gpt-4", body.get("model"));
        assertEquals(4096, body.get("max_tokens"));
        assertNull(body.get("max_completion_tokens"));
    }

    @Test
    public void test_buildRequestBody_requestModelOverride() {
        client.setTestModel("gpt-4");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().setModel("o3-mini").setMaxTokens(2048).addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, false);

        assertEquals("o3-mini", body.get("model"));
        assertEquals(2048, body.get("max_completion_tokens"));
        assertNull(body.get("max_tokens"));
    }

    // ========== useMaxCompletionTokens: boundary/edge cases ==========

    @Test
    public void test_useMaxCompletionTokens_o1Variants() {
        assertTrue(client.useMaxCompletionTokens("o1"));
        assertTrue(client.useMaxCompletionTokens("o1-mini"));
        assertTrue(client.useMaxCompletionTokens("o1-preview"));
        assertTrue(client.useMaxCompletionTokens("o1-2024-12-17"));
        assertTrue(client.useMaxCompletionTokens("o1-pro"));
    }

    @Test
    public void test_useMaxCompletionTokens_o3Variants() {
        assertTrue(client.useMaxCompletionTokens("o3"));
        assertTrue(client.useMaxCompletionTokens("o3-mini"));
        assertTrue(client.useMaxCompletionTokens("o3-mini-2025-01-31"));
        assertTrue(client.useMaxCompletionTokens("o3-pro"));
    }

    @Test
    public void test_useMaxCompletionTokens_o4Variants() {
        assertTrue(client.useMaxCompletionTokens("o4-mini"));
        assertTrue(client.useMaxCompletionTokens("o4-mini-2025-04-16"));
    }

    @Test
    public void test_useMaxCompletionTokens_gpt5Variants() {
        assertTrue(client.useMaxCompletionTokens("gpt-5"));
        assertTrue(client.useMaxCompletionTokens("gpt-5-mini"));
        assertTrue(client.useMaxCompletionTokens("gpt-5-turbo"));
        assertTrue(client.useMaxCompletionTokens("gpt-5.1"));
        assertTrue(client.useMaxCompletionTokens("gpt-5.2"));
        assertTrue(client.useMaxCompletionTokens("gpt-5-2025-06-01"));
    }

    @Test
    public void test_useMaxCompletionTokens_gpt4FamilyReturnsFalse() {
        assertFalse(client.useMaxCompletionTokens("gpt-4"));
        assertFalse(client.useMaxCompletionTokens("gpt-4o"));
        assertFalse(client.useMaxCompletionTokens("gpt-4o-mini"));
        assertFalse(client.useMaxCompletionTokens("gpt-4-turbo"));
        assertFalse(client.useMaxCompletionTokens("gpt-4-turbo-2024-04-09"));
        assertFalse(client.useMaxCompletionTokens("gpt-4-0613"));
        assertFalse(client.useMaxCompletionTokens("gpt-4-1106-preview"));
    }

    @Test
    public void test_useMaxCompletionTokens_gpt35ReturnsFalse() {
        assertFalse(client.useMaxCompletionTokens("gpt-3.5-turbo"));
        assertFalse(client.useMaxCompletionTokens("gpt-3.5-turbo-0125"));
        assertFalse(client.useMaxCompletionTokens("gpt-3.5-turbo-16k"));
    }

    @Test
    public void test_useMaxCompletionTokens_chatgptModelReturnsFalse() {
        assertFalse(client.useMaxCompletionTokens("chatgpt-4o-latest"));
    }

    @Test
    public void test_useMaxCompletionTokens_unknownModelReturnsFalse() {
        assertFalse(client.useMaxCompletionTokens("some-custom-model"));
        assertFalse(client.useMaxCompletionTokens("my-fine-tuned-model"));
        assertFalse(client.useMaxCompletionTokens("llama-3"));
    }

    // ========== buildRequestBody: max_tokens key selection integration ==========

    @Test
    public void test_buildRequestBody_o1Model_usesMaxCompletionTokens() {
        client.setTestModel("o1");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(8192);

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, false);

        assertEquals("o1", body.get("model"));
        assertEquals(8192, body.get("max_completion_tokens"));
        assertNull(body.get("max_tokens"));
    }

    @Test
    public void test_buildRequestBody_o3MiniModel_usesMaxCompletionTokens() {
        client.setTestModel("o3-mini");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, false);

        assertEquals("o3-mini", body.get("model"));
        assertEquals(4096, body.get("max_completion_tokens"));
        assertNull(body.get("max_tokens"));
    }

    @Test
    public void test_buildRequestBody_o4MiniModel_usesMaxCompletionTokens() {
        client.setTestModel("o4-mini");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, false);

        assertEquals("o4-mini", body.get("model"));
        assertEquals(4096, body.get("max_completion_tokens"));
        assertNull(body.get("max_tokens"));
    }

    @Test
    public void test_buildRequestBody_gpt4oModel_usesMaxTokens() {
        client.setTestModel("gpt-4o");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, false);

        assertEquals("gpt-4o", body.get("model"));
        assertEquals(4096, body.get("max_tokens"));
        assertNull(body.get("max_completion_tokens"));
    }

    @Test
    public void test_buildRequestBody_gpt35Model_usesMaxTokens() {
        client.setTestModel("gpt-3.5-turbo");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(2048);

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, false);

        assertEquals("gpt-3.5-turbo", body.get("model"));
        assertEquals(2048, body.get("max_tokens"));
        assertNull(body.get("max_completion_tokens"));
    }

    @Test
    public void test_buildRequestBody_legacyDefaultModel_requestOverridesToNewer() {
        client.setTestModel("gpt-4");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().setModel("gpt-5-mini").addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, false);

        assertEquals("gpt-5-mini", body.get("model"));
        assertEquals(4096, body.get("max_completion_tokens"));
        assertNull(body.get("max_tokens"));
    }

    @Test
    public void test_buildRequestBody_newerDefaultModel_requestOverridesToLegacy() {
        client.setTestModel("gpt-5-mini");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().setModel("gpt-4o").addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, false);

        assertEquals("gpt-4o", body.get("model"));
        assertEquals(4096, body.get("max_tokens"));
        assertNull(body.get("max_completion_tokens"));
    }

    @Test
    public void test_buildRequestBody_newerModel_withRequestMaxTokens() {
        client.setTestModel("o3-mini");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().setMaxTokens(1024).addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, false);

        assertEquals("o3-mini", body.get("model"));
        assertEquals(1024, body.get("max_completion_tokens"));
        assertNull(body.get("max_tokens"));
    }

    @Test
    public void test_buildRequestBody_legacyModel_withRequestMaxTokens() {
        client.setTestModel("gpt-4");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().setMaxTokens(512).addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, false);

        assertEquals("gpt-4", body.get("model"));
        assertEquals(512, body.get("max_tokens"));
        assertNull(body.get("max_completion_tokens"));
    }

    @Test
    public void test_buildRequestBody_streaming_newerModel_usesMaxCompletionTokens() {
        client.setTestModel("gpt-5");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, true);

        assertEquals(true, body.get("stream"));
        assertEquals("gpt-5", body.get("model"));
        assertEquals(4096, body.get("max_completion_tokens"));
        assertNull(body.get("max_tokens"));
    }

    @Test
    public void test_buildRequestBody_streaming_legacyModel_usesMaxTokens() {
        client.setTestModel("gpt-4");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, true);

        assertEquals(true, body.get("stream"));
        assertEquals("gpt-4", body.get("model"));
        assertEquals(4096, body.get("max_tokens"));
        assertNull(body.get("max_completion_tokens"));
    }

    @Test
    public void test_buildRequestBody_blankRequestModel_fallsBackToDefaultKeySelection() {
        client.setTestModel("o1");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().setModel("").addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, false);

        assertEquals("o1", body.get("model"));
        assertEquals(4096, body.get("max_completion_tokens"));
        assertNull(body.get("max_tokens"));
    }

    @Test
    public void test_buildRequestBody_nullRequestModel_fallsBackToDefaultKeySelection() {
        client.setTestModel("o3-mini");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(2048);

        final LlmChatRequest request = new LlmChatRequest().setModel(null).addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, false);

        assertEquals("o3-mini", body.get("model"));
        assertEquals(2048, body.get("max_completion_tokens"));
        assertNull(body.get("max_tokens"));
    }

    @Test
    public void test_buildRequestBody_bodyContainsExactlyOneMaxTokensKey() {
        client.setTestModel("gpt-5-mini");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request, false);

        assertTrue(body.containsKey("max_completion_tokens"));
        assertFalse(body.containsKey("max_tokens"));

        // Legacy model should have the opposite
        client.setTestModel("gpt-4");
        final Map<String, Object> body2 = client.buildRequestBody(request, false);

        assertTrue(body2.containsKey("max_tokens"));
        assertFalse(body2.containsKey("max_completion_tokens"));
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

        @Override
        protected boolean isRagChatEnabled() {
            return false;
        }

        @Override
        protected int getAvailabilityCheckInterval() {
            return 0;
        }
    }
}
