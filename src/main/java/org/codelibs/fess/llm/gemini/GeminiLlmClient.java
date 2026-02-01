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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.llm.LlmChatRequest;
import org.codelibs.fess.llm.LlmChatResponse;
import org.codelibs.fess.llm.LlmClient;
import org.codelibs.fess.llm.LlmException;
import org.codelibs.fess.llm.LlmMessage;
import org.codelibs.fess.llm.LlmStreamCallback;
import org.codelibs.fess.util.ComponentUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * LLM client implementation for Google Gemini API.
 *
 * Google Gemini provides cloud-based LLM services including Gemini models.
 * This client supports both synchronous and streaming chat completions.
 *
 * @author FessProject
 * @see <a href="https://ai.google.dev/docs">Google AI for Developers</a>
 */
public class GeminiLlmClient implements LlmClient {

    private static final Logger logger = LogManager.getLogger(GeminiLlmClient.class);
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    /** The name identifier for the Gemini LLM client. */
    protected static final String NAME = "gemini";

    /** Gemini role for model responses (equivalent to "assistant" in OpenAI). */
    protected static final String ROLE_MODEL = "model";

    private OkHttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private volatile Boolean cachedAvailability = null;
    private TimeoutTask availabilityCheckTask;

    /**
     * Default constructor.
     */
    public GeminiLlmClient() {
        // Default constructor
    }

    /**
     * Initializes the HTTP client and starts availability checking.
     */
    public void init() {
        // Skip if rag.llm.type does not match this client's NAME
        if (!NAME.equals(getLlmType())) {
            if (logger.isDebugEnabled()) {
                logger.debug("Skipping availability check. llmType={}, name={}", getLlmType(), NAME);
            }
            return;
        }

        final int timeout = getTimeout();
        httpClient = new OkHttpClient.Builder().connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .readTimeout(timeout, TimeUnit.MILLISECONDS)
                .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();
        if (logger.isDebugEnabled()) {
            logger.debug("Initialized GeminiLlmClient with timeout: {}ms", timeout);
        }

        // Start periodic availability checking
        startAvailabilityCheck();
    }

    /**
     * Cleans up resources.
     */
    public void destroy() {
        if (availabilityCheckTask != null && !availabilityCheckTask.isCanceled()) {
            availabilityCheckTask.cancel();
            if (logger.isDebugEnabled()) {
                logger.debug("Cancelled Gemini availability check task");
            }
        }
    }

    /**
     * Starts periodic availability checking if RAG chat is enabled.
     */
    protected void startAvailabilityCheck() {
        if (!isRagChatEnabled()) {
            if (logger.isDebugEnabled()) {
                logger.debug("RAG chat is disabled. Skipping availability check.");
            }
            return;
        }

        final int checkInterval = getAvailabilityCheckInterval();
        if (checkInterval <= 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("Availability check is disabled for Gemini");
            }
            return;
        }

        // Perform initial check
        updateAvailability();

        // Register periodic check
        availabilityCheckTask = TimeoutManager.getInstance().addTimeoutTarget(this::updateAvailability, checkInterval, true);

        if (logger.isDebugEnabled()) {
            logger.debug("Started Gemini availability check with interval: {}s", checkInterval);
        }
    }

    /**
     * Updates the cached availability state.
     */
    protected void updateAvailability() {
        final boolean previousState = cachedAvailability != null ? cachedAvailability : false;
        final boolean currentState = checkAvailabilityNow();
        cachedAvailability = currentState;

        if (previousState != currentState) {
            logger.info("Gemini availability changed: {} -> {}", previousState, currentState);
        } else if (logger.isDebugEnabled()) {
            logger.debug("Gemini availability check completed. available={}", currentState);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isAvailable() {
        if (cachedAvailability != null) {
            return cachedAvailability;
        }
        // Fallback to direct check if cache not initialized
        return checkAvailabilityNow();
    }

    /**
     * Performs the actual availability check against Gemini API.
     *
     * @return true if Gemini is available
     */
    protected boolean checkAvailabilityNow() {
        final String apiKey = getApiKey();
        if (StringUtil.isBlank(apiKey)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Gemini is not available. apiKey is blank");
            }
            return false;
        }
        final String apiUrl = getApiUrl();
        if (StringUtil.isBlank(apiUrl)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Gemini is not available. apiUrl is blank");
            }
            return false;
        }
        try {
            // Check availability by listing models
            final String url = apiUrl + "/models?key=" + apiKey;
            final Request request = new Request.Builder().url(url).get().build();
            try (Response response = getHttpClient().newCall(request).execute()) {
                final boolean available = response.isSuccessful();
                if (logger.isDebugEnabled()) {
                    logger.debug("Gemini availability check. url={}, statusCode={}, available={}", apiUrl, response.code(), available);
                }
                return available;
            }
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Gemini is not available. url={}, error={}", apiUrl, e.getMessage());
            }
            return false;
        }
    }

    @Override
    public LlmChatResponse chat(final LlmChatRequest request) {
        final String model = getModelName(request);
        final String url = buildApiUrl(model, false);
        final Map<String, Object> requestBody = buildRequestBody(request);

        if (logger.isDebugEnabled()) {
            logger.debug("Sending chat request to Gemini. url={}, model={}, messageCount={}", url, model, request.getMessages().size());
        }

        try {
            final String json = objectMapper.writeValueAsString(requestBody);
            final Request httpRequest = new Request.Builder().url(url)
                    .post(RequestBody.create(json, JSON_MEDIA_TYPE))
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = getHttpClient().newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = "";
                    if (response.body() != null) {
                        try {
                            errorBody = response.body().string();
                        } catch (final IOException e) {
                            // ignore
                        }
                    }
                    logger.warn("Gemini API error. url={}, statusCode={}, message={}, body={}", url, response.code(), response.message(),
                            errorBody);
                    throw new LlmException("Gemini API error: " + response.code() + " " + response.message());
                }

                final String responseBody = response.body() != null ? response.body().string() : "";
                final JsonNode jsonNode = objectMapper.readTree(responseBody);

                final LlmChatResponse chatResponse = new LlmChatResponse();
                // Parse candidates[0].content.parts[0].text
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
                // Parse model version
                if (jsonNode.has("modelVersion")) {
                    chatResponse.setModel(jsonNode.get("modelVersion").asText());
                } else {
                    chatResponse.setModel(model);
                }
                // Parse usage metadata
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
                            "Received chat response from Gemini. model={}, promptTokens={}, completionTokens={}, totalTokens={}, contentLength={}",
                            chatResponse.getModel(), chatResponse.getPromptTokens(), chatResponse.getCompletionTokens(),
                            chatResponse.getTotalTokens(), chatResponse.getContent() != null ? chatResponse.getContent().length() : 0);
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

        if (logger.isDebugEnabled()) {
            logger.debug("Starting streaming chat request to Gemini. url={}, model={}, messageCount={}", url, model,
                    request.getMessages().size());
        }

        try {
            final String json = objectMapper.writeValueAsString(requestBody);
            final Request httpRequest = new Request.Builder().url(url)
                    .post(RequestBody.create(json, JSON_MEDIA_TYPE))
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = getHttpClient().newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = "";
                    if (response.body() != null) {
                        try {
                            errorBody = response.body().string();
                        } catch (final IOException e) {
                            // ignore
                        }
                    }
                    logger.warn("Gemini streaming API error. url={}, statusCode={}, message={}, body={}", url, response.code(),
                            response.message(), errorBody);
                    throw new LlmException("Gemini API error: " + response.code() + " " + response.message());
                }

                if (response.body() == null) {
                    logger.warn("Empty response from Gemini streaming API. url={}", url);
                    throw new LlmException("Empty response from Gemini");
                }

                int chunkCount = 0;
                try (BufferedReader reader =
                        new BufferedReader(new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (StringUtil.isBlank(line)) {
                            continue;
                        }

                        // Gemini streaming returns JSON objects (may have array wrapping)
                        // Handle lines that start with [ or ] or , as part of JSON array
                        String trimmedLine = line.trim();
                        if (trimmedLine.equals("[") || trimmedLine.equals("]") || trimmedLine.equals(",")) {
                            continue;
                        }
                        // Remove leading comma if present
                        if (trimmedLine.startsWith(",")) {
                            trimmedLine = trimmedLine.substring(1).trim();
                        }

                        try {
                            final JsonNode jsonNode = objectMapper.readTree(trimmedLine);

                            // Check for finish reason
                            boolean done = false;
                            if (jsonNode.has("candidates") && jsonNode.get("candidates").isArray()
                                    && jsonNode.get("candidates").size() > 0) {
                                final JsonNode firstCandidate = jsonNode.get("candidates").get(0);
                                if (firstCandidate.has("finishReason") && !firstCandidate.get("finishReason").isNull()
                                        && !"null".equals(firstCandidate.get("finishReason").asText())) {
                                    done = true;
                                }

                                // Extract content text
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
                    logger.debug("Completed streaming chat from Gemini. url={}, chunkCount={}", url, chunkCount);
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

        // Extract system message and other messages
        String systemMessage = null;
        final List<LlmMessage> conversationMessages = new ArrayList<>();
        for (final LlmMessage message : request.getMessages()) {
            if (LlmMessage.ROLE_SYSTEM.equals(message.getRole())) {
                // Combine multiple system messages
                if (systemMessage == null) {
                    systemMessage = message.getContent();
                } else {
                    systemMessage = systemMessage + "\n" + message.getContent();
                }
            } else {
                conversationMessages.add(message);
            }
        }

        // System instruction
        if (systemMessage != null) {
            final Map<String, Object> systemInstruction = new HashMap<>();
            final List<Map<String, String>> systemParts = new ArrayList<>();
            final Map<String, String> textPart = new HashMap<>();
            textPart.put("text", systemMessage);
            systemParts.add(textPart);
            systemInstruction.put("parts", systemParts);
            body.put("systemInstruction", systemInstruction);
        }

        // Contents (conversation messages)
        final List<Map<String, Object>> contents = conversationMessages.stream().map(this::convertMessage).collect(Collectors.toList());
        body.put("contents", contents);

        // Generation config
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
     * Gemini uses "user" and "model" roles, and "parts" array for content.
     *
     * @param message the message to convert
     * @return the message as a map
     */
    protected Map<String, Object> convertMessage(final LlmMessage message) {
        final Map<String, Object> map = new HashMap<>();

        // Convert role: "assistant" -> "model" for Gemini
        String role = message.getRole();
        if (LlmMessage.ROLE_ASSISTANT.equals(role)) {
            role = ROLE_MODEL;
        }
        map.put("role", role);

        // Gemini uses parts array for content
        final List<Map<String, String>> parts = new ArrayList<>();
        final Map<String, String> textPart = new HashMap<>();
        textPart.put("text", message.getContent());
        parts.add(textPart);
        map.put("parts", parts);

        return map;
    }

    /**
     * Gets the HTTP client, initializing it if necessary.
     *
     * @return the HTTP client
     */
    protected OkHttpClient getHttpClient() {
        if (httpClient == null) {
            init();
        }
        return httpClient;
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

    /**
     * Gets the Gemini model name.
     *
     * @return the model name
     */
    protected String getModel() {
        return ComponentUtil.getFessConfig().getRagLlmGeminiModel();
    }

    /**
     * Gets the request timeout in milliseconds.
     *
     * @return the timeout in milliseconds
     */
    protected int getTimeout() {
        return ComponentUtil.getFessConfig().getRagLlmGeminiTimeoutAsInteger();
    }

    /**
     * Gets the temperature parameter.
     *
     * @return the temperature
     */
    protected double getTemperature() {
        return ComponentUtil.getFessConfig().getRagChatTemperatureAsDecimal().doubleValue();
    }

    /**
     * Gets the maximum tokens for the response.
     *
     * @return the maximum tokens
     */
    protected int getMaxTokens() {
        return ComponentUtil.getFessConfig().getRagChatMaxTokensAsInteger();
    }

    /**
     * Gets the availability check interval in seconds.
     *
     * @return the interval in seconds
     */
    protected int getAvailabilityCheckInterval() {
        return ComponentUtil.getFessConfig().getRagLlmAvailabilityCheckIntervalAsInteger();
    }

    /**
     * Checks if RAG chat feature is enabled.
     *
     * @return true if RAG chat is enabled
     */
    protected boolean isRagChatEnabled() {
        return ComponentUtil.getFessConfig().isRagChatEnabled();
    }

    /**
     * Gets the configured LLM type.
     *
     * @return the LLM type from configuration
     */
    protected String getLlmType() {
        return ComponentUtil.getFessConfig().getRagLlmType();
    }
}
