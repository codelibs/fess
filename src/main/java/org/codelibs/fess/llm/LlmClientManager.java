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
package org.codelibs.fess.llm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Manager class for coordinating LLM (Large Language Model) client operations.
 *
 * This class serves as the central coordinator for LLM operations in Fess.
 * It manages registered LLM clients and provides access to the configured
 * LLM provider based on the current configuration.
 */
public class LlmClientManager {

    private static final Logger logger = LogManager.getLogger(LlmClientManager.class);

    /** The list of registered LLM clients. */
    protected final List<LlmClient> clientList = new ArrayList<>();

    /**
     * Default constructor.
     */
    public LlmClientManager() {
        // Default constructor
    }

    /**
     * Checks whether LLM chat functionality is available and configured.
     *
     * @return true if LLM chat is configured and available, false otherwise
     */
    public boolean available() {
        final String llmType = getLlmType();
        if (Constants.NONE.equals(llmType)) {
            if (logger.isDebugEnabled()) {
                logger.debug("[LLM] LLM not available. llmType=none");
            }
            return false;
        }
        if (!isRagChatEnabled()) {
            if (logger.isDebugEnabled()) {
                logger.debug("[LLM] LLM not available. ragChatEnabled=false");
            }
            return false;
        }
        final LlmClient client = getClient();
        final boolean isAvailable = client != null && client.isAvailable();
        if (logger.isDebugEnabled()) {
            logger.debug("[LLM] LLM availability check. llmType={}, clientFound={}, isAvailable={}", llmType, client != null, isAvailable);
        }
        return isAvailable;
    }

    /**
     * Gets the LLM client instance for the configured LLM type.
     *
     * @return The LLM client instance, or null if not found
     */
    public LlmClient getClient() {
        final String llmType = getLlmType();
        final String name = llmType + "LlmClient";
        if (ComponentUtil.hasComponent(name)) {
            final LlmClient client = ComponentUtil.getComponent(name);
            if (logger.isDebugEnabled()) {
                logger.debug("[LLM] LlmClient found. componentName={}, clientName={}, available={}", name, client.getName(),
                        client.isAvailable());
            }
            return client;
        }
        logger.warn("LlmClient not found: {}", name);
        return null;
    }

    /**
     * Gets the configured LLM type from the system configuration.
     *
     * @return The LLM type string from configuration (e.g., "ollama", "openai", "gemini")
     */
    protected String getLlmType() {
        return ComponentUtil.getFessConfig().getRagLlmType();
    }

    /**
     * Checks if RAG chat feature is enabled.
     *
     * @return true if RAG chat is enabled, false otherwise
     */
    protected boolean isRagChatEnabled() {
        return ComponentUtil.getFessConfig().isRagChatEnabled();
    }

    /**
     * Gets all registered LLM clients.
     *
     * @return Array of all registered LLM clients
     */
    public LlmClient[] getClients() {
        return clientList.toArray(new LlmClient[clientList.size()]);
    }

    /**
     * Registers an LLM client with this manager.
     *
     * @param client The LLM client to register
     */
    public void register(final LlmClient client) {
        if (logger.isInfoEnabled()) {
            logger.info("Loaded LlmClient: {}", client.getClass().getSimpleName());
        }
        clientList.add(client);
    }

    /**
     * Performs a chat completion request using the configured LLM client.
     *
     * @param request the chat request
     * @return the chat response
     * @throws LlmException if LLM is not available or an error occurs
     */
    public LlmChatResponse chat(final LlmChatRequest request) {
        final long startTime = System.currentTimeMillis();
        final String llmType = getLlmType();
        if (logger.isDebugEnabled()) {
            logger.debug("[LLM] Starting LLM chat request. llmType={}, messageCount={}", llmType, request.getMessages().size());
            for (final LlmMessage msg : request.getMessages()) {
                logger.debug("[LLM] message: role={}, content={}", msg.getRole(), msg.getContent());
            }
        }
        if (!available()) {
            logger.warn("LLM chat request failed. LLM client is not available. llmType={}", llmType);
            throw new LlmException("LLM client is not available");
        }
        try {
            final LlmClient client = getClient();
            if (logger.isDebugEnabled()) {
                logger.debug("[LLM] Using LLM client. clientName={}", client != null ? client.getName() : "null");
            }
            final LlmChatResponse response = client.chat(request);
            if (logger.isDebugEnabled()) {
                logger.debug("[LLM] LLM chat request completed. llmType={}, contentLength={}, elapsedTime={}ms", llmType,
                        response.getContent() != null ? response.getContent().length() : 0, System.currentTimeMillis() - startTime);
            }
            return response;
        } catch (final LlmException e) {
            logger.warn("LLM chat request failed. llmType={}, error={}, elapsedTime={}ms", llmType, e.getMessage(),
                    System.currentTimeMillis() - startTime);
            throw e;
        } catch (final Exception e) {
            logger.warn("LLM chat request failed with unexpected error. llmType={}, error={}, elapsedTime={}ms", llmType, e.getMessage(),
                    System.currentTimeMillis() - startTime, e);
            throw new LlmException("LLM chat request failed", e);
        }
    }

    /**
     * Performs a streaming chat completion request using the configured LLM client.
     *
     * @param request the chat request
     * @param callback the callback to receive streaming chunks
     * @throws LlmException if LLM is not available or an error occurs
     */
    public void streamChat(final LlmChatRequest request, final LlmStreamCallback callback) {
        final long startTime = System.currentTimeMillis();
        final String llmType = getLlmType();
        if (logger.isDebugEnabled()) {
            logger.debug("[LLM] Starting LLM streaming chat request. llmType={}, messageCount={}", llmType, request.getMessages().size());
            for (final LlmMessage msg : request.getMessages()) {
                logger.debug("[LLM] message: role={}, content={}", msg.getRole(), msg.getContent());
            }
        }
        if (!available()) {
            logger.warn("LLM streaming chat request failed. LLM client is not available. llmType={}", llmType);
            throw new LlmException("LLM client is not available");
        }
        try {
            final LlmClient client = getClient();
            if (logger.isDebugEnabled()) {
                logger.debug("[LLM] Using LLM client for streaming. clientName={}", client != null ? client.getName() : "null");
            }
            client.streamChat(request, callback);
            if (logger.isDebugEnabled()) {
                logger.debug("[LLM] LLM streaming chat request completed. llmType={}, elapsedTime={}ms", llmType,
                        System.currentTimeMillis() - startTime);
            }
        } catch (final LlmException e) {
            logger.warn("LLM streaming chat request failed. llmType={}, error={}, elapsedTime={}ms", llmType, e.getMessage(),
                    System.currentTimeMillis() - startTime);
            throw e;
        } catch (final Exception e) {
            logger.warn("LLM streaming chat request failed with unexpected error. llmType={}, error={}, elapsedTime={}ms", llmType,
                    e.getMessage(), System.currentTimeMillis() - startTime, e);
            throw new LlmException("LLM streaming chat request failed", e);
        }
    }

    // RAG workflow delegation methods

    /**
     * Detects the intent of a user message using the configured LLM client.
     *
     * @param userMessage the user's message
     * @return the detected intent with extracted keywords
     * @throws LlmException if LLM is not available
     */
    public IntentDetectionResult detectIntent(final String userMessage) {
        if (logger.isDebugEnabled()) {
            logger.debug("[LLM] Delegating detectIntent. llmType={}", getLlmType());
        }
        if (!available()) {
            throw new LlmException("LLM client is not available");
        }
        return getClient().detectIntent(userMessage);
    }

    /**
     * Evaluates search results for relevance using the configured LLM client.
     *
     * @param userMessage the original user message
     * @param query the search query used
     * @param searchResults the search results to evaluate
     * @return evaluation result with relevant document IDs
     * @throws LlmException if LLM is not available
     */
    public RelevanceEvaluationResult evaluateResults(final String userMessage, final String query,
            final List<Map<String, Object>> searchResults) {
        if (logger.isDebugEnabled()) {
            logger.debug("[LLM] Delegating evaluateResults. llmType={}", getLlmType());
        }
        if (!available()) {
            throw new LlmException("LLM client is not available");
        }
        return getClient().evaluateResults(userMessage, query, searchResults);
    }

    /**
     * Generates an answer using document content (synchronous).
     *
     * @param userMessage the user's message
     * @param documents the documents with content
     * @param history the conversation history
     * @return the chat response
     * @throws LlmException if LLM is not available
     */
    public LlmChatResponse generateAnswer(final String userMessage, final List<Map<String, Object>> documents,
            final List<LlmMessage> history) {
        if (logger.isDebugEnabled()) {
            logger.debug("[LLM] Delegating generateAnswer. llmType={}", getLlmType());
        }
        if (!available()) {
            throw new LlmException("LLM client is not available");
        }
        return getClient().generateAnswer(userMessage, documents, history);
    }

    /**
     * Generates an answer using document content (streaming).
     *
     * @param userMessage the user's message
     * @param documents the documents with content
     * @param history the conversation history
     * @param callback the streaming callback
     * @throws LlmException if LLM is not available
     */
    public void streamGenerateAnswer(final String userMessage, final List<Map<String, Object>> documents, final List<LlmMessage> history,
            final LlmStreamCallback callback) {
        if (logger.isDebugEnabled()) {
            logger.debug("[LLM] Delegating streamGenerateAnswer. llmType={}", getLlmType());
        }
        if (!available()) {
            throw new LlmException("LLM client is not available");
        }
        getClient().streamGenerateAnswer(userMessage, documents, history, callback);
    }

    /**
     * Generates a response asking user for clarification.
     *
     * @param userMessage the user's message
     * @param history the conversation history
     * @param callback the streaming callback
     * @throws LlmException if LLM is not available
     */
    public void generateUnclearIntentResponse(final String userMessage, final List<LlmMessage> history, final LlmStreamCallback callback) {
        if (logger.isDebugEnabled()) {
            logger.debug("[LLM] Delegating generateUnclearIntentResponse. llmType={}", getLlmType());
        }
        if (!available()) {
            throw new LlmException("LLM client is not available");
        }
        getClient().generateUnclearIntentResponse(userMessage, history, callback);
    }

    /**
     * Generates a response when no relevant documents are found.
     *
     * @param userMessage the user's message
     * @param history the conversation history
     * @param callback the streaming callback
     * @throws LlmException if LLM is not available
     */
    public void generateNoResultsResponse(final String userMessage, final List<LlmMessage> history, final LlmStreamCallback callback) {
        if (logger.isDebugEnabled()) {
            logger.debug("[LLM] Delegating generateNoResultsResponse. llmType={}", getLlmType());
        }
        if (!available()) {
            throw new LlmException("LLM client is not available");
        }
        getClient().generateNoResultsResponse(userMessage, history, callback);
    }

    /**
     * Generates a response when the specified document URL is not found.
     *
     * @param userMessage the user's message
     * @param documentUrl the URL that was not found
     * @param history the conversation history
     * @param callback the streaming callback
     * @throws LlmException if LLM is not available
     */
    public void generateDocumentNotFoundResponse(final String userMessage, final String documentUrl, final List<LlmMessage> history,
            final LlmStreamCallback callback) {
        if (logger.isDebugEnabled()) {
            logger.debug("[LLM] Delegating generateDocumentNotFoundResponse. llmType={}", getLlmType());
        }
        if (!available()) {
            throw new LlmException("LLM client is not available");
        }
        getClient().generateDocumentNotFoundResponse(userMessage, documentUrl, history, callback);
    }

    /**
     * Generates a summary of the specified documents.
     *
     * @param userMessage the user's message
     * @param documents the documents to summarize
     * @param history the conversation history
     * @param callback the streaming callback
     * @throws LlmException if LLM is not available
     */
    public void generateSummaryResponse(final String userMessage, final List<Map<String, Object>> documents, final List<LlmMessage> history,
            final LlmStreamCallback callback) {
        if (logger.isDebugEnabled()) {
            logger.debug("[LLM] Delegating generateSummaryResponse. llmType={}", getLlmType());
        }
        if (!available()) {
            throw new LlmException("LLM client is not available");
        }
        getClient().generateSummaryResponse(userMessage, documents, history, callback);
    }

    /**
     * Generates an FAQ answer using document content (streaming).
     *
     * @param userMessage the user's message
     * @param documents the documents with content
     * @param history the conversation history
     * @param callback the streaming callback
     * @throws LlmException if LLM is not available
     */
    public void generateFaqAnswerResponse(final String userMessage, final List<Map<String, Object>> documents,
            final List<LlmMessage> history, final LlmStreamCallback callback) {
        if (logger.isDebugEnabled()) {
            logger.debug("[LLM] Delegating generateFaqAnswerResponse. llmType={}", getLlmType());
        }
        if (!available()) {
            throw new LlmException("LLM client is not available");
        }
        getClient().generateFaqAnswerResponse(userMessage, documents, history, callback);
    }

    /**
     * Generates a direct answer without document search.
     *
     * @param userMessage the user's message
     * @param history the conversation history
     * @param callback the streaming callback
     * @throws LlmException if LLM is not available
     */
    public void generateDirectAnswer(final String userMessage, final List<LlmMessage> history, final LlmStreamCallback callback) {
        if (logger.isDebugEnabled()) {
            logger.debug("[LLM] Delegating generateDirectAnswer. llmType={}", getLlmType());
        }
        if (!available()) {
            throw new LlmException("LLM client is not available");
        }
        getClient().generateDirectAnswer(userMessage, history, callback);
    }
}
