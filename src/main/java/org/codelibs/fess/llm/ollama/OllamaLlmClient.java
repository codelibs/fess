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
package org.codelibs.fess.llm.ollama;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
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
 * LLM client implementation for Ollama.
 *
 * Ollama provides a local LLM server that can run various models
 * like Llama, Mistral, etc. on your own hardware.
 *
 * @see <a href="https://ollama.ai/">Ollama</a>
 */
public class OllamaLlmClient extends AbstractLlmClient {

    private static final Logger logger = LogManager.getLogger(OllamaLlmClient.class);
    /** The name identifier for the Ollama LLM client. */
    protected static final String NAME = "ollama";

    /**
     * Default constructor.
     */
    public OllamaLlmClient() {
        // Default constructor
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected boolean checkAvailabilityNow() {
        final String apiUrl = getApiUrl();
        if (StringUtil.isBlank(apiUrl)) {
            if (logger.isDebugEnabled()) {
                logger.debug("[LLM:OLLAMA] Ollama is not available. apiUrl is blank");
            }
            return false;
        }
        try {
            final HttpGet request = new HttpGet(apiUrl + "/api/tags");
            try (var response = getHttpClient().execute(request)) {
                final int statusCode = response.getCode();
                if (statusCode < 200 || statusCode >= 300) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("[LLM:OLLAMA] Ollama availability check failed. url={}, statusCode={}", apiUrl, statusCode);
                    }
                    return false;
                }

                final String responseBody = response.getEntity() != null ? EntityUtils.toString(response.getEntity()) : "";
                return isModelAvailable(responseBody);
            }
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("[LLM:OLLAMA] Ollama is not available. url={}, error={}", apiUrl, e.getMessage());
            }
            return false;
        }
    }

    /**
     * Checks if the configured model is available in Ollama.
     *
     * @param responseBody the response body from /api/tags endpoint
     * @return true if the configured model is available
     */
    protected boolean isModelAvailable(final String responseBody) {
        final String configuredModel = getModel();
        if (StringUtil.isBlank(configuredModel)) {
            return true;
        }

        try {
            final JsonNode jsonNode = objectMapper.readTree(responseBody);
            if (jsonNode.has("models")) {
                final JsonNode models = jsonNode.get("models");
                for (final JsonNode model : models) {
                    if (model.has("name")) {
                        final String modelName = model.get("name").asText();
                        if (configuredModel.equals(modelName)) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("[LLM:OLLAMA] Model found. configured={}, found={}", configuredModel, modelName);
                            }
                            return true;
                        }
                    }
                }
            }
            logger.warn("Configured model not found in Ollama. model={}", configuredModel);
            return false;
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("[LLM:OLLAMA] Failed to parse Ollama models response. error={}", e.getMessage());
            }
            return true;
        }
    }

    @Override
    public LlmChatResponse chat(final LlmChatRequest request) {
        final String url = getApiUrl() + "/api/chat";
        final Map<String, Object> requestBody = buildRequestBody(request, false);
        final long startTime = System.currentTimeMillis();

        if (logger.isDebugEnabled()) {
            logger.debug("[LLM:OLLAMA] Sending chat request to Ollama. url={}, model={}, messageCount={}", url, requestBody.get("model"),
                    request.getMessages().size());
        }

        try {
            final String json = objectMapper.writeValueAsString(requestBody);
            if (logger.isDebugEnabled()) {
                logger.debug("[LLM:OLLAMA] requestBody={}", json);
            }
            final HttpPost httpRequest = new HttpPost(url);
            httpRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

            try (var response = getHttpClient().execute(httpRequest)) {
                final int statusCode = response.getCode();
                if (statusCode < 200 || statusCode >= 300) {
                    logger.warn("Ollama API error. url={}, statusCode={}, message={}", url, statusCode, response.getReasonPhrase());
                    throw new LlmException("Ollama API error: " + statusCode + " " + response.getReasonPhrase());
                }

                final String responseBody = response.getEntity() != null ? EntityUtils.toString(response.getEntity()) : "";
                if (logger.isDebugEnabled()) {
                    logger.debug("[LLM:OLLAMA] responseBody={}", responseBody);
                }
                final JsonNode jsonNode = objectMapper.readTree(responseBody);

                final LlmChatResponse chatResponse = new LlmChatResponse();
                if (jsonNode.has("message") && jsonNode.get("message").has("content")) {
                    chatResponse.setContent(jsonNode.get("message").get("content").asText());
                }
                if (jsonNode.has("done_reason")) {
                    chatResponse.setFinishReason(jsonNode.get("done_reason").asText());
                }
                if (jsonNode.has("model")) {
                    chatResponse.setModel(jsonNode.get("model").asText());
                }
                if (jsonNode.has("prompt_eval_count")) {
                    chatResponse.setPromptTokens(jsonNode.get("prompt_eval_count").asInt());
                }
                if (jsonNode.has("eval_count")) {
                    chatResponse.setCompletionTokens(jsonNode.get("eval_count").asInt());
                }

                if (logger.isDebugEnabled()) {
                    logger.debug(
                            "Received chat response from Ollama. model={}, promptTokens={}, completionTokens={}, contentLength={}, elapsedTime={}ms",
                            chatResponse.getModel(), chatResponse.getPromptTokens(), chatResponse.getCompletionTokens(),
                            chatResponse.getContent() != null ? chatResponse.getContent().length() : 0,
                            System.currentTimeMillis() - startTime);
                }

                return chatResponse;
            }
        } catch (final LlmException e) {
            throw e;
        } catch (final Exception e) {
            logger.warn("Failed to call Ollama API. url={}, error={}", url, e.getMessage(), e);
            throw new LlmException("Failed to call Ollama API", e);
        }
    }

    @Override
    public void streamChat(final LlmChatRequest request, final LlmStreamCallback callback) {
        final String url = getApiUrl() + "/api/chat";
        final Map<String, Object> requestBody = buildRequestBody(request, true);
        final long startTime = System.currentTimeMillis();

        if (logger.isDebugEnabled()) {
            logger.debug("[LLM:OLLAMA] Starting streaming chat request to Ollama. url={}, model={}, messageCount={}", url,
                    requestBody.get("model"), request.getMessages().size());
        }

        try {
            final String json = objectMapper.writeValueAsString(requestBody);
            if (logger.isDebugEnabled()) {
                logger.debug("[LLM:OLLAMA] requestBody={}", json);
            }
            final HttpPost httpRequest = new HttpPost(url);
            httpRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

            try (var response = getHttpClient().execute(httpRequest)) {
                final int statusCode = response.getCode();
                if (statusCode < 200 || statusCode >= 300) {
                    logger.warn("Ollama streaming API error. url={}, statusCode={}, message={}", url, statusCode,
                            response.getReasonPhrase());
                    throw new LlmException("Ollama API error: " + statusCode + " " + response.getReasonPhrase());
                }

                if (response.getEntity() == null) {
                    logger.warn("Empty response from Ollama streaming API. url={}", url);
                    throw new LlmException("Empty response from Ollama");
                }

                int chunkCount = 0;
                try (BufferedReader reader =
                        new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (StringUtil.isBlank(line)) {
                            continue;
                        }
                        try {
                            final JsonNode jsonNode = objectMapper.readTree(line);
                            final boolean done = jsonNode.has("done") && jsonNode.get("done").asBoolean();

                            if (jsonNode.has("message") && jsonNode.get("message").has("content")) {
                                final String content = jsonNode.get("message").get("content").asText();
                                callback.onChunk(content, done);
                                chunkCount++;
                            } else if (done) {
                                callback.onChunk("", true);
                            }

                            if (done) {
                                break;
                            }
                        } catch (final JsonProcessingException e) {
                            logger.warn("Failed to parse Ollama streaming response. line={}", line, e);
                        }
                    }
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("[LLM:OLLAMA] Completed streaming chat from Ollama. url={}, chunkCount={}, elapsedTime={}ms", url,
                            chunkCount, System.currentTimeMillis() - startTime);
                }
            }
        } catch (final LlmException e) {
            callback.onError(e);
            throw e;
        } catch (final IOException e) {
            logger.warn("Failed to stream from Ollama API. url={}, error={}", url, e.getMessage(), e);
            final LlmException llmException = new LlmException("Failed to stream from Ollama API", e);
            callback.onError(llmException);
            throw llmException;
        }
    }

    /**
     * Builds the request body for the Ollama API.
     *
     * @param request the chat request
     * @param stream whether to enable streaming
     * @return the request body as a map
     */
    protected Map<String, Object> buildRequestBody(final LlmChatRequest request, final boolean stream) {
        final Map<String, Object> body = new HashMap<>();

        String model = request.getModel();
        if (StringUtil.isBlank(model)) {
            model = getModel();
        }
        body.put("model", model);

        final List<Map<String, String>> messages = request.getMessages().stream().map(this::convertMessage).collect(Collectors.toList());
        body.put("messages", messages);

        body.put("stream", stream);

        final Map<String, Object> options = new HashMap<>();
        if (request.getTemperature() != null) {
            options.put("temperature", request.getTemperature());
        } else {
            options.put("temperature", getTemperature());
        }
        if (request.getMaxTokens() != null) {
            options.put("num_predict", request.getMaxTokens());
        } else {
            options.put("num_predict", getMaxTokens());
        }
        if (!options.isEmpty()) {
            body.put("options", options);
        }

        return body;
    }

    /**
     * Converts an LlmMessage to a map for the API request.
     *
     * @param message the message to convert
     * @return the message as a map
     */
    protected Map<String, String> convertMessage(final LlmMessage message) {
        final Map<String, String> map = new HashMap<>();
        map.put("role", message.getRole());
        map.put("content", message.getContent());
        return map;
    }

    /**
     * Gets the Ollama API URL.
     *
     * @return the API URL
     */
    protected String getApiUrl() {
        return ComponentUtil.getFessConfig().getRagLlmOllamaApiUrl();
    }

    @Override
    protected String getModel() {
        return ComponentUtil.getFessConfig().getRagLlmOllamaModel();
    }

    @Override
    protected int getTimeout() {
        return ComponentUtil.getFessConfig().getRagLlmOllamaTimeoutAsInteger();
    }
}
