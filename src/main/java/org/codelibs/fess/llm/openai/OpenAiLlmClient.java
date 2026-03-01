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
 * LLM client implementation for OpenAI API.
 *
 * OpenAI provides cloud-based LLM services including GPT-4 and other models.
 * This client supports both synchronous and streaming chat completions.
 *
 * @author FessProject
 * @see <a href="https://platform.openai.com/docs/api-reference">OpenAI API Reference</a>
 */
public class OpenAiLlmClient extends AbstractLlmClient {

    private static final Logger logger = LogManager.getLogger(OpenAiLlmClient.class);
    /** The name identifier for the OpenAI LLM client. */
    protected static final String NAME = "openai";
    private static final String SSE_DATA_PREFIX = "data: ";
    private static final String SSE_DONE_MARKER = "[DONE]";

    /**
     * Default constructor.
     */
    public OpenAiLlmClient() {
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
                logger.debug("[LLM:OPENAI] OpenAI is not available. apiKey is blank");
            }
            return false;
        }
        final String apiUrl = getApiUrl();
        if (StringUtil.isBlank(apiUrl)) {
            if (logger.isDebugEnabled()) {
                logger.debug("[LLM:OPENAI] OpenAI is not available. apiUrl is blank");
            }
            return false;
        }
        try {
            final HttpGet request = new HttpGet(apiUrl + "/models");
            request.addHeader("Authorization", "Bearer " + apiKey);
            try (var response = getHttpClient().execute(request)) {
                final int statusCode = response.getCode();
                final boolean available = statusCode >= 200 && statusCode < 300;
                if (logger.isDebugEnabled()) {
                    logger.debug("[LLM:OPENAI] OpenAI availability check. url={}, statusCode={}, available={}", apiUrl, statusCode,
                            available);
                }
                return available;
            }
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("[LLM:OPENAI] OpenAI is not available. url={}, error={}", apiUrl, e.getMessage());
            }
            return false;
        }
    }

    @Override
    public LlmChatResponse chat(final LlmChatRequest request) {
        final String url = getApiUrl() + "/chat/completions";
        final Map<String, Object> requestBody = buildRequestBody(request, false);
        final long startTime = System.currentTimeMillis();

        if (logger.isDebugEnabled()) {
            logger.debug("[LLM:OPENAI] Sending chat request to OpenAI. url={}, model={}, messageCount={}", url, requestBody.get("model"),
                    request.getMessages().size());
        }

        try {
            final String json = objectMapper.writeValueAsString(requestBody);
            if (logger.isDebugEnabled()) {
                logger.debug("[LLM:OPENAI] requestBody={}", json);
            }
            final HttpPost httpRequest = new HttpPost(url);
            httpRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
            httpRequest.addHeader("Authorization", "Bearer " + getApiKey());

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
                    logger.warn("OpenAI API error. url={}, statusCode={}, message={}, body={}", url, statusCode, response.getReasonPhrase(),
                            errorBody);
                    throw new LlmException("OpenAI API error: " + statusCode + " " + response.getReasonPhrase());
                }

                final String responseBody = response.getEntity() != null ? EntityUtils.toString(response.getEntity()) : "";
                if (logger.isDebugEnabled()) {
                    logger.debug("[LLM:OPENAI] responseBody={}", responseBody);
                }
                final JsonNode jsonNode = objectMapper.readTree(responseBody);

                final LlmChatResponse chatResponse = new LlmChatResponse();
                if (jsonNode.has("choices") && jsonNode.get("choices").isArray() && jsonNode.get("choices").size() > 0) {
                    final JsonNode firstChoice = jsonNode.get("choices").get(0);
                    if (firstChoice.has("message") && firstChoice.get("message").has("content")) {
                        chatResponse.setContent(firstChoice.get("message").get("content").asText());
                    }
                    if (firstChoice.has("finish_reason") && !firstChoice.get("finish_reason").isNull()) {
                        chatResponse.setFinishReason(firstChoice.get("finish_reason").asText());
                    }
                }
                if (jsonNode.has("model")) {
                    chatResponse.setModel(jsonNode.get("model").asText());
                }
                if (jsonNode.has("usage")) {
                    final JsonNode usage = jsonNode.get("usage");
                    if (usage.has("prompt_tokens")) {
                        chatResponse.setPromptTokens(usage.get("prompt_tokens").asInt());
                    }
                    if (usage.has("completion_tokens")) {
                        chatResponse.setCompletionTokens(usage.get("completion_tokens").asInt());
                    }
                    if (usage.has("total_tokens")) {
                        chatResponse.setTotalTokens(usage.get("total_tokens").asInt());
                    }
                }

                if (logger.isDebugEnabled()) {
                    logger.debug(
                            "Received chat response from OpenAI. model={}, promptTokens={}, completionTokens={}, totalTokens={}, contentLength={}, elapsedTime={}ms",
                            chatResponse.getModel(), chatResponse.getPromptTokens(), chatResponse.getCompletionTokens(),
                            chatResponse.getTotalTokens(), chatResponse.getContent() != null ? chatResponse.getContent().length() : 0,
                            System.currentTimeMillis() - startTime);
                }

                return chatResponse;
            }
        } catch (final LlmException e) {
            throw e;
        } catch (final Exception e) {
            logger.warn("Failed to call OpenAI API. url={}, error={}", url, e.getMessage(), e);
            throw new LlmException("Failed to call OpenAI API", e);
        }
    }

    @Override
    public void streamChat(final LlmChatRequest request, final LlmStreamCallback callback) {
        final String url = getApiUrl() + "/chat/completions";
        final Map<String, Object> requestBody = buildRequestBody(request, true);
        final long startTime = System.currentTimeMillis();

        if (logger.isDebugEnabled()) {
            logger.debug("[LLM:OPENAI] Starting streaming chat request to OpenAI. url={}, model={}, messageCount={}", url,
                    requestBody.get("model"), request.getMessages().size());
        }

        try {
            final String json = objectMapper.writeValueAsString(requestBody);
            if (logger.isDebugEnabled()) {
                logger.debug("[LLM:OPENAI] requestBody={}", json);
            }
            final HttpPost httpRequest = new HttpPost(url);
            httpRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
            httpRequest.addHeader("Authorization", "Bearer " + getApiKey());

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
                    logger.warn("OpenAI streaming API error. url={}, statusCode={}, message={}, body={}", url, statusCode,
                            response.getReasonPhrase(), errorBody);
                    throw new LlmException("OpenAI API error: " + statusCode + " " + response.getReasonPhrase());
                }

                if (response.getEntity() == null) {
                    logger.warn("Empty response from OpenAI streaming API. url={}", url);
                    throw new LlmException("Empty response from OpenAI");
                }

                int chunkCount = 0;
                try (BufferedReader reader =
                        new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (StringUtil.isBlank(line)) {
                            continue;
                        }

                        if (!line.startsWith(SSE_DATA_PREFIX)) {
                            continue;
                        }

                        final String data = line.substring(SSE_DATA_PREFIX.length()).trim();
                        if (SSE_DONE_MARKER.equals(data)) {
                            callback.onChunk("", true);
                            break;
                        }

                        try {
                            final JsonNode jsonNode = objectMapper.readTree(data);
                            if (jsonNode.has("choices") && jsonNode.get("choices").isArray() && jsonNode.get("choices").size() > 0) {
                                final JsonNode firstChoice = jsonNode.get("choices").get(0);
                                final boolean done = firstChoice.has("finish_reason") && !firstChoice.get("finish_reason").isNull()
                                        && !"null".equals(firstChoice.get("finish_reason").asText());

                                if (firstChoice.has("delta") && firstChoice.get("delta").has("content")) {
                                    final String content = firstChoice.get("delta").get("content").asText();
                                    callback.onChunk(content, done);
                                    chunkCount++;
                                } else if (done) {
                                    callback.onChunk("", true);
                                }

                                if (done) {
                                    break;
                                }
                            }
                        } catch (final JsonProcessingException e) {
                            logger.warn("Failed to parse OpenAI streaming response. line={}", line, e);
                        }
                    }
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("[LLM:OPENAI] Completed streaming chat from OpenAI. url={}, chunkCount={}, elapsedTime={}ms", url,
                            chunkCount, System.currentTimeMillis() - startTime);
                }
            }
        } catch (final LlmException e) {
            callback.onError(e);
            throw e;
        } catch (final IOException e) {
            logger.warn("Failed to stream from OpenAI API. url={}, error={}", url, e.getMessage(), e);
            final LlmException llmException = new LlmException("Failed to stream from OpenAI API", e);
            callback.onError(llmException);
            throw llmException;
        }
    }

    /**
     * Builds the request body for the OpenAI API.
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

        if (request.getTemperature() != null) {
            body.put("temperature", request.getTemperature());
        } else {
            body.put("temperature", getTemperature());
        }

        final String maxTokensKey = useMaxCompletionTokens(model) ? "max_completion_tokens" : "max_tokens";
        if (request.getMaxTokens() != null) {
            body.put(maxTokensKey, request.getMaxTokens());
        } else {
            body.put(maxTokensKey, getMaxTokens());
        }

        return body;
    }

    /**
     * Determines whether the given model requires the "max_completion_tokens" parameter
     * instead of the legacy "max_tokens" parameter.
     *
     * @param model the model name
     * @return true if the model uses max_completion_tokens
     */
    protected boolean useMaxCompletionTokens(final String model) {
        if (StringUtil.isBlank(model)) {
            return false;
        }
        if (model.startsWith("o1") || model.startsWith("o3") || model.startsWith("o4")) {
            return true;
        }
        if (model.startsWith("gpt-5")) {
            return true;
        }
        return false;
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
     * Gets the OpenAI API key.
     *
     * @return the API key
     */
    protected String getApiKey() {
        return ComponentUtil.getFessConfig().getRagLlmOpenaiApiKey();
    }

    /**
     * Gets the OpenAI API URL.
     *
     * @return the API URL
     */
    protected String getApiUrl() {
        return ComponentUtil.getFessConfig().getRagLlmOpenaiApiUrl();
    }

    @Override
    protected String getModel() {
        return ComponentUtil.getFessConfig().getRagLlmOpenaiModel();
    }

    @Override
    protected int getTimeout() {
        return ComponentUtil.getFessConfig().getRagLlmOpenaiTimeoutAsInteger();
    }
}
