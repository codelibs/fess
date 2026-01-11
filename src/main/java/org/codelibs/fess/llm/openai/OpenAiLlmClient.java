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
 * LLM client implementation for OpenAI API.
 *
 * @author FessProject
 * @see <a href="https://platform.openai.com/docs/api-reference">OpenAI API Reference</a>
 */
public class OpenAiLlmClient implements LlmClient {

    private static final Logger logger = LogManager.getLogger(OpenAiLlmClient.class);
    private static final String NAME = "openai";

    /**
     * Default constructor.
     */
    public OpenAiLlmClient() {
        // Default constructor
    }

    /**
     * Initializes the client.
     */
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialized OpenAiLlmClient");
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
        // TODO: Implement OpenAI chat
        throw new LlmException("OpenAI client not yet implemented");
    }

    @Override
    public void streamChat(final LlmChatRequest request, final LlmStreamCallback callback) {
        // TODO: Implement OpenAI streaming chat
        throw new LlmException("OpenAI streaming not yet implemented");
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

    /**
     * Gets the OpenAI model name.
     *
     * @return the model name
     */
    protected String getModel() {
        return ComponentUtil.getFessConfig().getRagLlmOpenaiModel();
    }

    /**
     * Gets the request timeout in milliseconds.
     *
     * @return the timeout in milliseconds
     */
    protected int getTimeout() {
        return ComponentUtil.getFessConfig().getRagLlmOpenaiTimeoutAsInteger();
    }
}
