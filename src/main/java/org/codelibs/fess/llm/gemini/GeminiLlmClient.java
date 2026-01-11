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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.llm.LlmChatRequest;
import org.codelibs.fess.llm.LlmChatResponse;
import org.codelibs.fess.llm.LlmClient;
import org.codelibs.fess.llm.LlmException;
import org.codelibs.fess.llm.LlmStreamCallback;
import org.codelibs.fess.util.ComponentUtil;

/**
 * LLM client implementation for Google Gemini API.
 *
 * @author FessProject
 * @see <a href="https://ai.google.dev/docs">Google AI for Developers</a>
 */
public class GeminiLlmClient implements LlmClient {

    private static final Logger logger = LogManager.getLogger(GeminiLlmClient.class);
    private static final String NAME = "gemini";

    /**
     * Default constructor.
     */
    public GeminiLlmClient() {
        // Default constructor
    }

    /**
     * Initializes the client.
     */
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialized GeminiLlmClient");
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isAvailable() {
        // Not yet implemented - return false until chat methods are properly implemented
        return false;
    }

    @Override
    public LlmChatResponse chat(final LlmChatRequest request) {
        // TODO: Implement Gemini chat
        throw new LlmException("Gemini client not yet implemented");
    }

    @Override
    public void streamChat(final LlmChatRequest request, final LlmStreamCallback callback) {
        // TODO: Implement Gemini streaming chat
        throw new LlmException("Gemini streaming not yet implemented");
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
}
