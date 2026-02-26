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

import java.util.List;
import java.util.Map;

/**
 * Interface for LLM (Large Language Model) clients.
 * Implementations provide integration with different LLM providers
 * such as Ollama, OpenAI, and Google Gemini.
 *
 * In addition to low-level chat operations, this interface defines
 * high-level RAG workflow methods that allow each provider to optimize
 * prompt construction, parameter tuning, and response parsing.
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

    // RAG workflow methods

    /**
     * Detects the intent of a user message.
     *
     * @param userMessage the user's message
     * @return the detected intent with extracted keywords
     */
    IntentDetectionResult detectIntent(String userMessage);

    /**
     * Evaluates search results for relevance to the user's question.
     *
     * @param userMessage the original user message
     * @param query the search query used
     * @param searchResults the search results to evaluate
     * @return evaluation result with relevant document IDs
     */
    RelevanceEvaluationResult evaluateResults(String userMessage, String query, List<Map<String, Object>> searchResults);

    /**
     * Generates an answer using document content (synchronous version for non-enhanced flow).
     *
     * @param userMessage the user's message
     * @param documents the documents with content
     * @param history the conversation history
     * @return the chat response
     */
    LlmChatResponse generateAnswer(String userMessage, List<Map<String, Object>> documents, List<LlmMessage> history);

    /**
     * Generates an answer using document content (streaming version for enhanced flow).
     *
     * @param userMessage the user's message
     * @param documents the documents with content
     * @param history the conversation history
     * @param callback the streaming callback
     */
    void streamGenerateAnswer(String userMessage, List<Map<String, Object>> documents, List<LlmMessage> history,
            LlmStreamCallback callback);

    /**
     * Generates a response asking user for clarification when intent is unclear.
     *
     * @param userMessage the user's message
     * @param history the conversation history
     * @param callback the streaming callback
     */
    void generateUnclearIntentResponse(String userMessage, List<LlmMessage> history, LlmStreamCallback callback);

    /**
     * Generates a response when no relevant documents are found.
     *
     * @param userMessage the user's message
     * @param history the conversation history
     * @param callback the streaming callback
     */
    void generateNoResultsResponse(String userMessage, List<LlmMessage> history, LlmStreamCallback callback);

    /**
     * Generates a response when the specified document URL is not found.
     *
     * @param userMessage the user's message
     * @param documentUrl the URL that was not found
     * @param history the conversation history
     * @param callback the streaming callback
     */
    void generateDocumentNotFoundResponse(String userMessage, String documentUrl, List<LlmMessage> history, LlmStreamCallback callback);

    /**
     * Generates a summary of the specified documents.
     *
     * @param userMessage the user's message
     * @param documents the documents to summarize
     * @param history the conversation history
     * @param callback the streaming callback
     */
    void generateSummaryResponse(String userMessage, List<Map<String, Object>> documents, List<LlmMessage> history,
            LlmStreamCallback callback);

    /**
     * Generates an FAQ answer using document content (streaming).
     * Uses a prompt optimized for direct, concise FAQ-style answers.
     *
     * @param userMessage the user's message
     * @param documents the documents with content
     * @param history the conversation history
     * @param callback the streaming callback
     */
    void generateFaqAnswerResponse(String userMessage, List<Map<String, Object>> documents, List<LlmMessage> history,
            LlmStreamCallback callback);

    /**
     * Generates a direct answer without document search.
     *
     * @param userMessage the user's message
     * @param history the conversation history
     * @param callback the streaming callback
     */
    void generateDirectAnswer(String userMessage, List<LlmMessage> history, LlmStreamCallback callback);
}
