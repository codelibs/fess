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
package org.codelibs.fess.llm.gemini;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.llm.LlmChatRequest;
import org.codelibs.fess.llm.LlmMessage;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class GeminiLlmClientTest extends UnitFessTestCase {

    private TestableGeminiLlmClient client;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        client = new TestableGeminiLlmClient();
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        super.tearDown(testInfo);
    }

    @Test
    public void test_getName() {
        assertEquals("gemini", client.getName());
    }

    @Test
    public void test_isAvailable_noApiKey() {
        client.setTestApiKey("");
        client.setTestApiUrl("https://generativelanguage.googleapis.com/v1beta");
        assertFalse(client.isAvailable());
    }

    @Test
    public void test_isAvailable_nullApiKey() {
        client.setTestApiKey(null);
        client.setTestApiUrl("https://generativelanguage.googleapis.com/v1beta");
        assertFalse(client.isAvailable());
    }

    @Test
    public void test_isAvailable_noApiUrl() {
        client.setTestApiKey("test-api-key");
        client.setTestApiUrl("");
        assertFalse(client.isAvailable());
    }

    @Test
    public void test_isAvailable_nullApiUrl() {
        client.setTestApiKey("test-api-key");
        client.setTestApiUrl(null);
        assertFalse(client.isAvailable());
    }

    @Test
    public void test_convertMessage_user() {
        final LlmMessage message = LlmMessage.user("Hello, how are you?");
        final Map<String, Object> result = client.convertMessage(message);

        assertEquals("user", result.get("role"));
        @SuppressWarnings("unchecked")
        final List<Map<String, String>> parts = (List<Map<String, String>>) result.get("parts");
        assertEquals(1, parts.size());
        assertEquals("Hello, how are you?", parts.get(0).get("text"));
    }

    @Test
    public void test_convertMessage_assistant() {
        final LlmMessage message = LlmMessage.assistant("I'm doing well, thank you!");
        final Map<String, Object> result = client.convertMessage(message);

        // Assistant should be converted to "model" for Gemini
        assertEquals("model", result.get("role"));
        @SuppressWarnings("unchecked")
        final List<Map<String, String>> parts = (List<Map<String, String>>) result.get("parts");
        assertEquals(1, parts.size());
        assertEquals("I'm doing well, thank you!", parts.get(0).get("text"));
    }

    @Test
    public void test_buildRequestBody_defaultValues() {
        client.setTestModel("gemini-2.5-flash");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request);

        // Check contents
        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> contents = (List<Map<String, Object>>) body.get("contents");
        assertEquals(1, contents.size());
        assertEquals("user", contents.get(0).get("role"));

        @SuppressWarnings("unchecked")
        final List<Map<String, String>> parts = (List<Map<String, String>>) contents.get(0).get("parts");
        assertEquals("Hello", parts.get(0).get("text"));

        // Check generation config
        @SuppressWarnings("unchecked")
        final Map<String, Object> generationConfig = (Map<String, Object>) body.get("generationConfig");
        assertEquals(0.7, generationConfig.get("temperature"));
        assertEquals(4096, generationConfig.get("maxOutputTokens"));

        // No system instruction for this request
        assertNull(body.get("systemInstruction"));
    }

    @Test
    public void test_buildRequestBody_withSystemMessage() {
        client.setTestModel("gemini-2.5-flash");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().addSystemMessage("You are a helpful assistant.").addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request);

        // Check system instruction
        @SuppressWarnings("unchecked")
        final Map<String, Object> systemInstruction = (Map<String, Object>) body.get("systemInstruction");
        assertNotNull(systemInstruction);

        @SuppressWarnings("unchecked")
        final List<Map<String, String>> systemParts = (List<Map<String, String>>) systemInstruction.get("parts");
        assertEquals(1, systemParts.size());
        assertEquals("You are a helpful assistant.", systemParts.get(0).get("text"));

        // Check contents (should not include system message)
        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> contents = (List<Map<String, Object>>) body.get("contents");
        assertEquals(1, contents.size());
        assertEquals("user", contents.get(0).get("role"));
    }

    @Test
    public void test_buildRequestBody_withMultipleSystemMessages() {
        client.setTestModel("gemini-2.5-flash");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().addSystemMessage("You are a helpful assistant.")
                .addSystemMessage("Always be polite.")
                .addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request);

        // Check system instruction (should be combined)
        @SuppressWarnings("unchecked")
        final Map<String, Object> systemInstruction = (Map<String, Object>) body.get("systemInstruction");
        assertNotNull(systemInstruction);

        @SuppressWarnings("unchecked")
        final List<Map<String, String>> systemParts = (List<Map<String, String>>) systemInstruction.get("parts");
        assertEquals(1, systemParts.size());
        assertEquals("You are a helpful assistant.\nAlways be polite.", systemParts.get(0).get("text"));
    }

    @Test
    public void test_buildRequestBody_withRequestTemperature() {
        client.setTestModel("gemini-2.5-flash");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().setTemperature(0.5).addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request);

        @SuppressWarnings("unchecked")
        final Map<String, Object> generationConfig = (Map<String, Object>) body.get("generationConfig");
        assertEquals(0.5, generationConfig.get("temperature"));
    }

    @Test
    public void test_buildRequestBody_withRequestMaxTokens() {
        client.setTestModel("gemini-2.5-flash");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().setMaxTokens(1000).addUserMessage("Hello");

        final Map<String, Object> body = client.buildRequestBody(request);

        @SuppressWarnings("unchecked")
        final Map<String, Object> generationConfig = (Map<String, Object>) body.get("generationConfig");
        assertEquals(1000, generationConfig.get("maxOutputTokens"));
    }

    @Test
    public void test_buildRequestBody_multipleMessages() {
        client.setTestModel("gemini-2.5-flash");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request = new LlmChatRequest().addSystemMessage("You are a helpful assistant.")
                .addUserMessage("What is the weather?")
                .addAssistantMessage("I cannot access weather information.")
                .addUserMessage("OK");

        final Map<String, Object> body = client.buildRequestBody(request);

        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> contents = (List<Map<String, Object>>) body.get("contents");
        assertEquals(3, contents.size());

        // First message: user
        assertEquals("user", contents.get(0).get("role"));
        @SuppressWarnings("unchecked")
        final List<Map<String, String>> parts0 = (List<Map<String, String>>) contents.get(0).get("parts");
        assertEquals("What is the weather?", parts0.get(0).get("text"));

        // Second message: model (converted from assistant)
        assertEquals("model", contents.get(1).get("role"));
        @SuppressWarnings("unchecked")
        final List<Map<String, String>> parts1 = (List<Map<String, String>>) contents.get(1).get("parts");
        assertEquals("I cannot access weather information.", parts1.get(0).get("text"));

        // Third message: user
        assertEquals("user", contents.get(2).get("role"));
        @SuppressWarnings("unchecked")
        final List<Map<String, String>> parts2 = (List<Map<String, String>>) contents.get(2).get("parts");
        assertEquals("OK", parts2.get(0).get("text"));
    }

    @Test
    public void test_buildApiUrl_nonStreaming() {
        client.setTestApiUrl("https://generativelanguage.googleapis.com/v1beta");
        client.setTestApiKey("test-key");

        final String url = client.buildApiUrl("gemini-2.5-flash", false);

        assertEquals("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=test-key", url);
    }

    @Test
    public void test_buildApiUrl_streaming() {
        client.setTestApiUrl("https://generativelanguage.googleapis.com/v1beta");
        client.setTestApiKey("test-key");

        final String url = client.buildApiUrl("gemini-2.5-flash", true);

        assertEquals("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:streamGenerateContent?key=test-key", url);
    }

    @Test
    public void test_getModelName_fromRequest() {
        client.setTestModel("gemini-2.5-flash");

        final LlmChatRequest request = new LlmChatRequest().setModel("gemini-1.5-pro").addUserMessage("Hello");

        assertEquals("gemini-1.5-pro", client.getModelName(request));
    }

    @Test
    public void test_getModelName_fromConfig() {
        client.setTestModel("gemini-2.5-flash");

        final LlmChatRequest request = new LlmChatRequest().addUserMessage("Hello");

        assertEquals("gemini-2.5-flash", client.getModelName(request));
    }

    @Test
    public void test_getModelName_blankModelUsesDefault() {
        client.setTestModel("gemini-2.5-flash");

        final LlmChatRequest request = new LlmChatRequest().setModel("").addUserMessage("Hello");

        assertEquals("gemini-2.5-flash", client.getModelName(request));
    }

    @Test
    public void test_getModelName_nullModelUsesDefault() {
        client.setTestModel("gemini-2.5-flash");

        final LlmChatRequest request = new LlmChatRequest().setModel(null).addUserMessage("Hello");

        assertEquals("gemini-2.5-flash", client.getModelName(request));
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

    @Test
    public void test_buildRequestBody_noSystemMessage() {
        client.setTestModel("gemini-2.5-flash");
        client.setTestTemperature(0.7);
        client.setTestMaxTokens(4096);

        final LlmChatRequest request =
                new LlmChatRequest().addUserMessage("Hello").addAssistantMessage("Hi there!").addUserMessage("How are you?");

        final Map<String, Object> body = client.buildRequestBody(request);

        // No system instruction
        assertNull(body.get("systemInstruction"));

        // Check contents
        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> contents = (List<Map<String, Object>>) body.get("contents");
        assertEquals(3, contents.size());
    }

    @Test
    public void test_convertMessage_preservesUserRole() {
        final LlmMessage message = LlmMessage.user("Test message");
        final Map<String, Object> result = client.convertMessage(message);

        assertEquals("user", result.get("role"));
    }

    @Test
    public void test_convertMessage_convertsAssistantToModel() {
        final LlmMessage message = LlmMessage.assistant("Test response");
        final Map<String, Object> result = client.convertMessage(message);

        // "assistant" should be converted to "model" for Gemini API
        assertEquals("model", result.get("role"));
    }

    /**
     * Testable subclass of GeminiLlmClient that allows setting configuration values
     * directly without depending on FessConfig.
     */
    private static class TestableGeminiLlmClient extends GeminiLlmClient {
        private String testApiKey = "";
        private String testApiUrl = "https://generativelanguage.googleapis.com/v1beta";
        private String testModel = "gemini-2.5-flash";
        private int testTimeout = 60000;
        private double testTemperature = 0.7;
        private int testMaxTokens = 4096;

        void setTestApiKey(String apiKey) {
            this.testApiKey = apiKey;
        }

        void setTestApiUrl(String apiUrl) {
            this.testApiUrl = apiUrl;
        }

        void setTestModel(String model) {
            this.testModel = model;
        }

        void setTestTimeout(int timeout) {
            this.testTimeout = timeout;
        }

        void setTestTemperature(double temperature) {
            this.testTemperature = temperature;
        }

        void setTestMaxTokens(int maxTokens) {
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
