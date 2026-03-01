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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.llm.AbstractLlmClient;
import org.codelibs.fess.llm.LlmChatRequest;
import org.codelibs.fess.llm.LlmChatResponse;
import org.codelibs.fess.llm.LlmException;
import org.codelibs.fess.llm.LlmMessage;
import org.codelibs.fess.llm.LlmStreamCallback;
import org.codelibs.fess.util.ComponentUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * LLM client implementation for Google Gemini API.
 *
 * Google Gemini provides cloud-based LLM services including Gemini models.
 * This client supports both synchronous and streaming chat completions.
 *
 * @author FessProject
 * @see <a href="https://ai.google.dev/docs">Google AI for Developers</a>
 */
public class GeminiLlmClient extends AbstractLlmClient {

    private static final Logger logger = LogManager.getLogger(GeminiLlmClient.class);
    /** The name identifier for the Gemini LLM client. */
    protected static final String NAME = "gemini";

    /** Gemini role for model responses (equivalent to "assistant" in OpenAI). */
    protected static final String ROLE_MODEL = "model";

    /**
     * Default constructor.
     */
    public GeminiLlmClient() {
        // Default constructor
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected boolean checkAvailabilityNow() {
        final String apiKey = getApiKey();
        if (StringUtil.isBlank(apiKey)) {
            if (logger.isDebugEnabled()) {
                logger.debug("[LLM:GEMINI] Gemini is not available. apiKey is blank");
            }
            return false;
        }
        final String apiUrl = getApiUrl();
        if (StringUtil.isBlank(apiUrl)) {
            if (logger.isDebugEnabled()) {
                logger.debug("[LLM:GEMINI] Gemini is not available. apiUrl is blank");
            }
            return false;
        }
        try {
            final String url = apiUrl + "/models?key=" + apiKey;
            final HttpGet request = new HttpGet(url);
            try (var response = getHttpClient().execute(request)) {
                final int statusCode = response.getCode();
                final boolean available = statusCode >= 200 && statusCode < 300;
                if (logger.isDebugEnabled()) {
                    logger.debug("[LLM:GEMINI] Gemini availability check. url={}, statusCode={}, available={}", apiUrl, statusCode,
                            available);
                }
                return available;
            }
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("[LLM:GEMINI] Gemini is not available. url={}, error={}", apiUrl, e.getMessage());
            }
            return false;
        }
    }

    @Override
    public LlmChatResponse chat(final LlmChatRequest request) {
        final String model = getModelName(request);
        final String url = buildApiUrl(model, false);
        final Map<String, Object> requestBody = buildRequestBody(request);
        final long startTime = System.currentTimeMillis();

        if (logger.isDebugEnabled()) {
            logger.debug("[LLM:GEMINI] Sending chat request to Gemini. url={}, model={}, messageCount={}", url, model,
                    request.getMessages().size());
        }

        try {
            final String json = objectMapper.writeValueAsString(requestBody);
            if (logger.isDebugEnabled()) {
                logger.debug("[LLM:GEMINI] requestBody={}", json);
            }
            final HttpPost httpRequest = new HttpPost(url);
            httpRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

            try (var response = getHttpClient().execute(httpRequest)) {
                final int statusCode = response.getCode();
                if (statusCode < 200 || statusCode >= 300) {
                    String errorBody = "";
                    if (response.getEntity() != null) {
                        try {
                            errorBody = EntityUtils.toString(response.getEntity());
                        } catch (final IOException e) {
                            // ignore
                        }
                    }
                    logger.warn("Gemini API error. url={}, statusCode={}, message={}, body={}", url, statusCode, response.getReasonPhrase(),
                            errorBody);
                    throw new LlmException("Gemini API error: " + statusCode + " " + response.getReasonPhrase());
                }

                final String responseBody = response.getEntity() != null ? EntityUtils.toString(response.getEntity()) : "";
                if (logger.isDebugEnabled()) {
                    logger.debug("[LLM:GEMINI] responseBody={}", responseBody);
                }
                final JsonNode jsonNode = objectMapper.readTree(responseBody);

                final LlmChatResponse chatResponse = new LlmChatResponse();
                if (jsonNode.has("candidates") && jsonNode.get("candidates").isArray() && jsonNode.get("candidates").size() > 0) {
                    final JsonNode firstCandidate = jsonNode.get("candidates").get(0);
                    if (firstCandidate.has("content") && firstCandidate.get("content").has("parts")) {
                        final JsonNode parts = firstCandidate.get("content").get("parts");
                        if (parts.isArray() && parts.size() > 0 && parts.get(0).has("text")) {
                            chatResponse.setContent(parts.get(0).get("text").asText());
                        }
                    }
                    if (firstCandidate.has("finishReason") && !firstCandidate.get("finishReason").isNull()) {
                        chatResponse.setFinishReason(firstCandidate.get("finishReason").asText());
                    }
                }
                if (jsonNode.has("modelVersion")) {
                    chatResponse.setModel(jsonNode.get("modelVersion").asText());
                } else {
                    chatResponse.setModel(model);
                }
                if (jsonNode.has("usageMetadata")) {
                    final JsonNode usage = jsonNode.get("usageMetadata");
                    if (usage.has("promptTokenCount")) {
                        chatResponse.setPromptTokens(usage.get("promptTokenCount").asInt());
                    }
                    if (usage.has("candidatesTokenCount")) {
                        chatResponse.setCompletionTokens(usage.get("candidatesTokenCount").asInt());
                    }
                    if (usage.has("totalTokenCount")) {
                        chatResponse.setTotalTokens(usage.get("totalTokenCount").asInt());
                    }
                }

                if (logger.isDebugEnabled()) {
                    logger.debug(
                            "Received chat response from Gemini. model={}, promptTokens={}, completionTokens={}, totalTokens={}, contentLength={}, elapsedTime={}ms",
                            chatResponse.getModel(), chatResponse.getPromptTokens(), chatResponse.getCompletionTokens(),
                            chatResponse.getTotalTokens(), chatResponse.getContent() != null ? chatResponse.getContent().length() : 0,
                            System.currentTimeMillis() - startTime);
                }

                return chatResponse;
            }
        } catch (final LlmException e) {
            throw e;
        } catch (final Exception e) {
            logger.warn("Failed to call Gemini API. url={}, error={}", url, e.getMessage(), e);
            throw new LlmException("Failed to call Gemini API", e);
        }
    }

    @Override
    public void streamChat(final LlmChatRequest request, final LlmStreamCallback callback) {
        final String model = getModelName(request);
        final String url = buildApiUrl(model, true);
        final Map<String, Object> requestBody = buildRequestBody(request);
        final long startTime = System.currentTimeMillis();

        if (logger.isDebugEnabled()) {
            logger.debug("[LLM:GEMINI] Starting streaming chat request to Gemini. url={}, model={}, messageCount={}", url, model,
                    request.getMessages().size());
        }

        try {
            final String json = objectMapper.writeValueAsString(requestBody);
            if (logger.isDebugEnabled()) {
                logger.debug("[LLM:GEMINI] requestBody={}", json);
            }
            final HttpPost httpRequest = new HttpPost(url);
            httpRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

            try (var response = getHttpClient().execute(httpRequest)) {
                final int statusCode = response.getCode();
                if (statusCode < 200 || statusCode >= 300) {
                    String errorBody = "";
                    if (response.getEntity() != null) {
                        try {
                            errorBody = EntityUtils.toString(response.getEntity());
                        } catch (final IOException | ParseException e) {
                            // ignore
                        }
                    }
                    logger.warn("Gemini streaming API error. url={}, statusCode={}, message={}, body={}", url, statusCode,
                            response.getReasonPhrase(), errorBody);
                    throw new LlmException("Gemini API error: " + statusCode + " " + response.getReasonPhrase());
                }

                if (response.getEntity() == null) {
                    logger.warn("Empty response from Gemini streaming API. url={}", url);
                    throw new LlmException("Empty response from Gemini");
                }

                int chunkCount = 0;
                try (BufferedReader reader =
                        new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (StringUtil.isBlank(line)) {
                            continue;
                        }

                        String trimmedLine = line.trim();
                        if (trimmedLine.equals("[") || trimmedLine.equals("]") || trimmedLine.equals(",")) {
                            continue;
                        }
                        if (trimmedLine.startsWith(",")) {
                            trimmedLine = trimmedLine.substring(1).trim();
                        }

                        try {
                            final JsonNode jsonNode = objectMapper.readTree(trimmedLine);

                            boolean done = false;
                            if (jsonNode.has("candidates") && jsonNode.get("candidates").isArray()
                                    && jsonNode.get("candidates").size() > 0) {
                                final JsonNode firstCandidate = jsonNode.get("candidates").get(0);
                                if (firstCandidate.has("finishReason") && !firstCandidate.get("finishReason").isNull()
                                        && !"null".equals(firstCandidate.get("finishReason").asText())) {
                                    done = true;
                                }

                                if (firstCandidate.has("content") && firstCandidate.get("content").has("parts")) {
                                    final JsonNode parts = firstCandidate.get("content").get("parts");
                                    if (parts.isArray() && parts.size() > 0 && parts.get(0).has("text")) {
                                        final String content = parts.get(0).get("text").asText();
                                        callback.onChunk(content, done);
                                        chunkCount++;
                                    } else if (done) {
                                        callback.onChunk("", true);
                                    }
                                } else if (done) {
                                    callback.onChunk("", true);
                                }

                                if (done) {
                                    break;
                                }
                            }
                        } catch (final JsonProcessingException e) {
                            logger.warn("Failed to parse Gemini streaming response. line={}", line, e);
                        }
                    }
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("[LLM:GEMINI] Completed streaming chat from Gemini. url={}, chunkCount={}, elapsedTime={}ms", url,
                            chunkCount, System.currentTimeMillis() - startTime);
                }
            }
        } catch (final LlmException e) {
            callback.onError(e);
            throw e;
        } catch (final IOException e) {
            logger.warn("Failed to stream from Gemini API. url={}, error={}", url, e.getMessage(), e);
            final LlmException llmException = new LlmException("Failed to stream from Gemini API", e);
            callback.onError(llmException);
            throw llmException;
        }
    }

    /**
     * Gets the model name from the request or config.
     *
     * @param request the chat request
     * @return the model name
     */
    protected String getModelName(final LlmChatRequest request) {
        String model = request.getModel();
        if (StringUtil.isBlank(model)) {
            model = getModel();
        }
        return model;
    }

    /**
     * Builds the API URL for the specified model.
     *
     * @param model the model name
     * @param stream whether this is a streaming request
     * @return the complete API URL
     */
    protected String buildApiUrl(final String model, final boolean stream) {
        final String apiUrl = getApiUrl();
        final String apiKey = getApiKey();
        final String action = stream ? "streamGenerateContent" : "generateContent";
        return apiUrl + "/models/" + model + ":" + action + "?key=" + apiKey;
    }

    /**
     * Builds the request body for the Gemini API.
     *
     * @param request the chat request
     * @return the request body as a map
     */
    protected Map<String, Object> buildRequestBody(final LlmChatRequest request) {
        final Map<String, Object> body = new HashMap<>();

        String systemMessage = null;
        final List<LlmMessage> conversationMessages = new ArrayList<>();
        for (final LlmMessage message : request.getMessages()) {
            if (LlmMessage.ROLE_SYSTEM.equals(message.getRole())) {
                if (systemMessage == null) {
                    systemMessage = message.getContent();
                } else {
                    systemMessage = systemMessage + "\n" + message.getContent();
                }
            } else {
                conversationMessages.add(message);
            }
        }

        if (systemMessage != null) {
            final Map<String, Object> systemInstruction = new HashMap<>();
            final List<Map<String, String>> systemParts = new ArrayList<>();
            final Map<String, String> textPart = new HashMap<>();
            textPart.put("text", systemMessage);
            systemParts.add(textPart);
            systemInstruction.put("parts", systemParts);
            body.put("systemInstruction", systemInstruction);
        }

        final List<Map<String, Object>> contents = conversationMessages.stream().map(this::convertMessage).collect(Collectors.toList());
        body.put("contents", contents);

        final Map<String, Object> generationConfig = new HashMap<>();
        if (request.getTemperature() != null) {
            generationConfig.put("temperature", request.getTemperature());
        } else {
            generationConfig.put("temperature", getTemperature());
        }
        if (request.getMaxTokens() != null) {
            generationConfig.put("maxOutputTokens", request.getMaxTokens());
        } else {
            generationConfig.put("maxOutputTokens", getMaxTokens());
        }
        if (!generationConfig.isEmpty()) {
            body.put("generationConfig", generationConfig);
        }

        return body;
    }

    /**
     * Converts an LlmMessage to a map for the Gemini API request.
     *
     * @param message the message to convert
     * @return the message as a map
     */
    protected Map<String, Object> convertMessage(final LlmMessage message) {
        final Map<String, Object> map = new HashMap<>();

        String role = message.getRole();
        if (LlmMessage.ROLE_ASSISTANT.equals(role)) {
            role = ROLE_MODEL;
        }
        map.put("role", role);

        final List<Map<String, String>> parts = new ArrayList<>();
        final Map<String, String> textPart = new HashMap<>();
        textPart.put("text", message.getContent());
        parts.add(textPart);
        map.put("parts", parts);

        return map;
    }

    /**
     * Gets the Gemini API key.
     *
     * @return the API key
     */
    protected String getApiKey() {
        return ComponentUtil.getFessConfig().getRagLlmGeminiApiKey();
    }

    /**
     * Gets the Gemini API URL.
     *
     * @return the API URL
     */
    protected String getApiUrl() {
        return ComponentUtil.getFessConfig().getRagLlmGeminiApiUrl();
    }

    @Override
    protected String getModel() {
        return ComponentUtil.getFessConfig().getRagLlmGeminiModel();
    }

    @Override
    protected int getTimeout() {
        return ComponentUtil.getFessConfig().getRagLlmGeminiTimeoutAsInteger();
    }
}
