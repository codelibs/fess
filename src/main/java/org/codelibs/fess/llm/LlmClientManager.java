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
                logger.debug("LLM not available. llmType=none");
            }
            return false;
        }
        if (!isRagChatEnabled()) {
            if (logger.isDebugEnabled()) {
                logger.debug("LLM not available. ragChatEnabled=false");
            }
            return false;
        }
        final LlmClient client = getClient();
        final boolean isAvailable = client != null && client.isAvailable();
        if (logger.isDebugEnabled()) {
            logger.debug("LLM availability check. llmType={}, clientFound={}, isAvailable={}", llmType, client != null, isAvailable);
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
            return ComponentUtil.getComponent(name);
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
        if (logger.isDebugEnabled()) {
            logger.debug("Starting LLM chat request. messageCount={}", request.getMessages().size());
        }
        if (!available()) {
            logger.warn("LLM chat request failed. LLM client is not available. llmType={}", getLlmType());
            throw new LlmException("LLM client is not available");
        }
        try {
            final LlmChatResponse response = getClient().chat(request);
            if (logger.isDebugEnabled()) {
                logger.debug("LLM chat request completed. contentLength={}",
                        response.getContent() != null ? response.getContent().length() : 0);
            }
            return response;
        } catch (final LlmException e) {
            logger.warn("LLM chat request failed. error={}", e.getMessage());
            throw e;
        } catch (final Exception e) {
            logger.warn("LLM chat request failed with unexpected error. error={}", e.getMessage(), e);
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
        if (logger.isDebugEnabled()) {
            logger.debug("Starting LLM streaming chat request. messageCount={}", request.getMessages().size());
        }
        if (!available()) {
            logger.warn("LLM streaming chat request failed. LLM client is not available. llmType={}", getLlmType());
            throw new LlmException("LLM client is not available");
        }
        try {
            getClient().streamChat(request, callback);
            if (logger.isDebugEnabled()) {
                logger.debug("LLM streaming chat request completed.");
            }
        } catch (final LlmException e) {
            logger.warn("LLM streaming chat request failed. error={}", e.getMessage());
            throw e;
        } catch (final Exception e) {
            logger.warn("LLM streaming chat request failed with unexpected error. error={}", e.getMessage(), e);
            throw new LlmException("LLM streaming chat request failed", e);
        }
    }
}
