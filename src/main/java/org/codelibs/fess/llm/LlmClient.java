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

/**
 * Interface for LLM (Large Language Model) clients.
 * Implementations provide integration with different LLM providers
 * such as Ollama, OpenAI, and Google Gemini.
 */
public interface LlmClient {

    /**
     * Performs a chat completion request.
     *
     * @param request the chat request containing messages and parameters
     * @return the chat response from the LLM
     * @throws LlmException if an error occurs during the request
     */
    LlmChatResponse chat(LlmChatRequest request);

    /**
     * Performs a streaming chat completion request.
     * The callback is invoked for each chunk of the response.
     *
     * @param request the chat request containing messages and parameters
     * @param callback the callback to receive streaming chunks
     * @throws LlmException if an error occurs during the request
     */
    void streamChat(LlmChatRequest request, LlmStreamCallback callback);

    /**
     * Returns the name of this LLM client.
     *
     * @return the client name (e.g., "ollama", "openai", "gemini")
     */
    String getName();

    /**
     * Checks if this LLM client is available and properly configured.
     *
     * @return true if the client is available, false otherwise
     */
    boolean isAvailable();
}
