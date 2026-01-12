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
package org.codelibs.fess.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.ChatMessage;
import org.codelibs.fess.entity.ChatMessage.ChatSource;
import org.codelibs.fess.helper.MarkdownRenderer;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.entity.ChatSession;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.llm.LlmChatRequest;
import org.codelibs.fess.llm.LlmChatResponse;
import org.codelibs.fess.llm.LlmClientManager;
import org.codelibs.fess.llm.LlmMessage;
import org.codelibs.fess.llm.LlmStreamCallback;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;

import jakarta.annotation.Resource;

/**
 * Client class for RAG (Retrieval-Augmented Generation) chat functionality.
 *
 * @author FessProject
 */
public class ChatClient {

    private static final Logger logger = LogManager.getLogger(ChatClient.class);

    /** The session manager for managing chat sessions. */
    @Resource
    protected ChatSessionManager chatSessionManager;

    /** The LLM client manager for language model interactions. */
    @Resource
    protected LlmClientManager llmClientManager;

    /** The markdown renderer for converting markdown to safe HTML. */
    @Resource
    protected MarkdownRenderer markdownRenderer;

    /**
     * Default constructor.
     */
    public ChatClient() {
        // Default constructor
    }

    /**
     * Checks if RAG chat is available.
     *
     * @return true if RAG chat is available
     */
    public boolean isAvailable() {
        final boolean available = llmClientManager.available();
        if (logger.isDebugEnabled()) {
            logger.debug("ChatClient availability check. available={}", available);
        }
        return available;
    }

    /**
     * Performs a chat request with RAG.
     *
     * @param sessionId the session ID (can be null for new sessions)
     * @param userMessage the user's message
     * @param userId the user ID (can be null for anonymous users)
     * @return the chat response including session info and sources
     */
    public ChatResult chat(final String sessionId, final String userMessage, final String userId) {
        if (logger.isDebugEnabled()) {
            logger.debug("Starting chat request. sessionId={}, userId={}, messageLength={}", sessionId, userId,
                    userMessage != null ? userMessage.length() : 0);
        }

        // Get or create session
        final ChatSession session = chatSessionManager.getOrCreateSession(sessionId, userId);
        if (logger.isDebugEnabled()) {
            logger.debug("Session retrieved. sessionId={}, historySize={}", session.getSessionId(), session.getMessages().size());
        }

        // Search for relevant documents
        final List<Map<String, Object>> searchResults = searchDocuments(userMessage);
        if (logger.isDebugEnabled()) {
            logger.debug("Document search completed. query={}, resultCount={}", userMessage, searchResults.size());
        }

        // Build context from search results
        final String context = buildContext(searchResults);

        // Build LLM request with conversation history
        final LlmChatRequest request = buildChatRequest(session, userMessage, context);
        if (logger.isDebugEnabled()) {
            logger.debug("LLM request built. messageCount={}, contextLength={}", request.getMessages().size(), context.length());
        }

        // Call LLM
        final LlmChatResponse llmResponse;
        try {
            llmResponse = llmClientManager.chat(request);
        } catch (final Exception e) {
            logger.warn("Failed to get response from LLM. sessionId={}, error={}", session.getSessionId(), e.getMessage(), e);
            throw e;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("LLM response received. responseLength={}",
                    llmResponse.getContent() != null ? llmResponse.getContent().length() : 0);
        }

        // Create chat messages
        final ChatMessage userChatMessage = ChatMessage.userMessage(userMessage);
        final ChatMessage assistantMessage = ChatMessage.assistantMessage(llmResponse.getContent());

        // Add sources to assistant message
        for (int i = 0; i < searchResults.size(); i++) {
            assistantMessage.addSource(new ChatSource(i + 1, searchResults.get(i)));
        }

        // Save to session and trim history
        session.addMessage(userChatMessage);
        session.addMessage(assistantMessage);
        session.trimHistory(getMaxHistoryMessages());

        if (logger.isDebugEnabled()) {
            logger.debug("Chat request completed. sessionId={}, sourcesCount={}", session.getSessionId(), searchResults.size());
        }

        return new ChatResult(session.getSessionId(), assistantMessage, searchResults);
    }

    /**
     * Performs a streaming chat request with RAG.
     *
     * @param sessionId the session ID (can be null for new sessions)
     * @param userMessage the user's message
     * @param userId the user ID (can be null for anonymous users)
     * @param callback the callback to receive streaming chunks
     * @return the chat result with session info and sources
     */
    public ChatResult streamChat(final String sessionId, final String userMessage, final String userId, final LlmStreamCallback callback) {
        if (logger.isDebugEnabled()) {
            logger.debug("Starting streaming chat request. sessionId={}, userId={}, messageLength={}", sessionId, userId,
                    userMessage != null ? userMessage.length() : 0);
        }

        // Get or create session
        final ChatSession session = chatSessionManager.getOrCreateSession(sessionId, userId);
        if (logger.isDebugEnabled()) {
            logger.debug("Session retrieved. sessionId={}, historySize={}", session.getSessionId(), session.getMessages().size());
        }

        // Search for relevant documents
        final List<Map<String, Object>> searchResults = searchDocuments(userMessage);
        if (logger.isDebugEnabled()) {
            logger.debug("Document search completed. query={}, resultCount={}", userMessage, searchResults.size());
        }

        // Build context from search results
        final String context = buildContext(searchResults);

        // Build LLM request with conversation history
        final LlmChatRequest request = buildChatRequest(session, userMessage, context);
        request.setStream(true);
        if (logger.isDebugEnabled()) {
            logger.debug("LLM streaming request built. messageCount={}, contextLength={}", request.getMessages().size(), context.length());
        }

        // Stream the response and collect content
        final StringBuilder responseContent = new StringBuilder();
        try {
            llmClientManager.streamChat(request, (chunk, done) -> {
                responseContent.append(chunk);
                callback.onChunk(chunk, done);
            });
        } catch (final Exception e) {
            logger.warn("Failed to stream response from LLM. sessionId={}, error={}", session.getSessionId(), e.getMessage(), e);
            throw e;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("LLM streaming completed. responseLength={}", responseContent.length());
        }

        // Create chat messages
        final ChatMessage userChatMessage = ChatMessage.userMessage(userMessage);
        final ChatMessage assistantMessage = ChatMessage.assistantMessage(responseContent.toString());

        // Add sources to assistant message
        for (int i = 0; i < searchResults.size(); i++) {
            assistantMessage.addSource(new ChatSource(i + 1, searchResults.get(i)));
        }

        // Save to session and trim history
        session.addMessage(userChatMessage);
        session.addMessage(assistantMessage);
        session.trimHistory(getMaxHistoryMessages());

        if (logger.isDebugEnabled()) {
            logger.debug("Streaming chat request completed. sessionId={}, sourcesCount={}", session.getSessionId(), searchResults.size());
        }

        return new ChatResult(session.getSessionId(), assistantMessage, searchResults);
    }

    /**
     * Performs an enhanced streaming chat request with multi-phase RAG flow.
     * This flow includes: intent detection, keyword search, result evaluation,
     * content retrieval, answer generation, and markdown rendering.
     *
     * @param sessionId the session ID (can be null for new sessions)
     * @param userMessage the user's message
     * @param userId the user ID (can be null for anonymous users)
     * @param callback the callback to receive phase notifications and streaming chunks
     * @return the chat result with session info, sources, and HTML content
     */
    public ChatResult streamChatEnhanced(final String sessionId, final String userMessage, final String userId,
            final ChatPhaseCallback callback) {
        if (logger.isDebugEnabled()) {
            logger.debug("Starting enhanced streaming chat request. sessionId={}, userId={}, messageLength={}", sessionId, userId,
                    userMessage != null ? userMessage.length() : 0);
        }

        final ChatSession session = chatSessionManager.getOrCreateSession(sessionId, userId);
        final StringBuilder fullResponse = new StringBuilder();
        List<Map<String, Object>> sources = new ArrayList<>();

        try {
            // Phase 1: Intent Detection
            callback.onPhaseStart(ChatPhaseCallback.PHASE_INTENT, "Analyzing your question...");
            final IntentDetectionResult intentResult = detectIntent(userMessage);
            callback.onPhaseComplete(ChatPhaseCallback.PHASE_INTENT);

            if (logger.isDebugEnabled()) {
                logger.debug("Intent detected. intent={}, keywords={}", intentResult.getIntent(), intentResult.getKeywords());
            }

            if (intentResult.getIntent() == ChatIntent.CHAT) {
                // No search needed - direct LLM response
                callback.onPhaseStart(ChatPhaseCallback.PHASE_ANSWER, "Generating response...");
                generateDirectAnswer(userMessage, session, (chunk, done) -> {
                    fullResponse.append(chunk);
                    callback.onChunk(chunk, done);
                });
                callback.onPhaseComplete(ChatPhaseCallback.PHASE_ANSWER);
            } else {
                // Phase 2: Search with keywords
                callback.onPhaseStart(ChatPhaseCallback.PHASE_SEARCH, "Searching documents...");
                final List<String> keywords = intentResult.getKeywords().isEmpty() ? List.of(userMessage) : intentResult.getKeywords();
                final List<Map<String, Object>> searchResults = searchWithKeywords(keywords);
                callback.onPhaseComplete(ChatPhaseCallback.PHASE_SEARCH);

                if (logger.isDebugEnabled()) {
                    logger.debug("Search completed. resultCount={}", searchResults.size());
                }

                if (searchResults.isEmpty()) {
                    // No results - generate fallback response
                    callback.onPhaseStart(ChatPhaseCallback.PHASE_ANSWER, "Generating response...");
                    generateNoResultsResponse(userMessage, session, (chunk, done) -> {
                        fullResponse.append(chunk);
                        callback.onChunk(chunk, done);
                    });
                    callback.onPhaseComplete(ChatPhaseCallback.PHASE_ANSWER);
                } else {
                    // Phase 3: Evaluate results
                    callback.onPhaseStart(ChatPhaseCallback.PHASE_EVALUATE, "Evaluating relevance...");
                    final RelevanceEvaluationResult evalResult = evaluateResults(userMessage, keywords, searchResults);
                    callback.onPhaseComplete(ChatPhaseCallback.PHASE_EVALUATE);

                    if (logger.isDebugEnabled()) {
                        logger.debug("Evaluation completed. hasRelevant={}, relevantCount={}", evalResult.isHasRelevantResults(),
                                evalResult.getRelevantDocIds().size());
                    }

                    if (!evalResult.isHasRelevantResults()) {
                        // No relevant results - generate fallback response
                        callback.onPhaseStart(ChatPhaseCallback.PHASE_ANSWER, "Generating response...");
                        generateNoResultsResponse(userMessage, session, (chunk, done) -> {
                            fullResponse.append(chunk);
                            callback.onChunk(chunk, done);
                        });
                        callback.onPhaseComplete(ChatPhaseCallback.PHASE_ANSWER);
                    } else {
                        // Phase 4: Fetch full content
                        callback.onPhaseStart(ChatPhaseCallback.PHASE_FETCH, "Retrieving document content...");
                        final List<Map<String, Object>> fullDocs = fetchFullContent(evalResult.getRelevantDocIds());
                        callback.onPhaseComplete(ChatPhaseCallback.PHASE_FETCH);
                        sources = fullDocs;

                        if (logger.isDebugEnabled()) {
                            logger.debug("Full content fetched. docCount={}", fullDocs.size());
                        }

                        // Phase 5: Generate answer
                        callback.onPhaseStart(ChatPhaseCallback.PHASE_ANSWER, "Generating response...");
                        generateAnswerWithContent(userMessage, fullDocs, session, (chunk, done) -> {
                            fullResponse.append(chunk);
                            callback.onChunk(chunk, done);
                        });
                        callback.onPhaseComplete(ChatPhaseCallback.PHASE_ANSWER);
                    }
                }
            }

            // Phase 6: Render markdown to safe HTML
            final String htmlContent = renderMarkdownToHtml(fullResponse.toString());

            // Create and save messages
            final ChatMessage userChatMessage = ChatMessage.userMessage(userMessage);
            final ChatMessage assistantMessage = ChatMessage.assistantMessage(fullResponse.toString());
            assistantMessage.setHtmlContent(htmlContent);

            for (int i = 0; i < sources.size(); i++) {
                assistantMessage.addSource(new ChatSource(i + 1, sources.get(i)));
            }

            session.addMessage(userChatMessage);
            session.addMessage(assistantMessage);
            session.trimHistory(getMaxHistoryMessages());

            if (logger.isDebugEnabled()) {
                logger.debug("Enhanced chat request completed. sessionId={}, sourcesCount={}", session.getSessionId(), sources.size());
            }

            return new ChatResult(session.getSessionId(), assistantMessage, sources);

        } catch (final Exception e) {
            logger.warn("Error during enhanced chat. sessionId={}, error={}", session.getSessionId(), e.getMessage(), e);
            callback.onError("unknown", e.getMessage());
            throw e;
        }
    }

    /**
     * Detects the intent of a user message using LLM.
     *
     * @param userMessage the user's message
     * @return the detected intent with extracted keywords
     */
    protected IntentDetectionResult detectIntent(final String userMessage) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        try {
            final String prompt = buildIntentDetectionPrompt(userMessage, fessConfig);
            final LlmChatRequest request = new LlmChatRequest();
            request.addUserMessage(prompt);
            request.setMaxTokens(500);
            request.setTemperature(0.3);

            final LlmChatResponse response = llmClientManager.chat(request);
            return parseIntentResponse(response.getContent());
        } catch (final Exception e) {
            logger.warn("Failed to detect intent, falling back to search. error={}", e.getMessage());
            return IntentDetectionResult.fallbackSearch(userMessage);
        }
    }

    /**
     * Builds the intent detection prompt.
     *
     * @param userMessage the user's message
     * @param fessConfig the Fess configuration
     * @return the intent detection prompt
     */
    protected String buildIntentDetectionPrompt(final String userMessage, final FessConfig fessConfig) {
        return "Analyze the following user question and determine the intent.\n" + "Return a JSON object with:\n"
                + "- \"intent\": one of \"search\" (needs document search), \"summary\" (document summary), \"faq\" (FAQ), \"chat\" (general conversation)\n"
                + "- \"keywords\": array of search keywords (if intent is \"search\")\n"
                + "- \"reasoning\": brief explanation of your decision\n\n" + "Question: " + userMessage + "\n\n" + "Response (JSON only):";
    }

    /**
     * Parses the intent detection response from LLM.
     *
     * @param response the LLM response
     * @return the parsed intent detection result
     */
    protected IntentDetectionResult parseIntentResponse(final String response) {
        try {
            // Simple JSON parsing - look for intent and keywords
            final String intentStr = extractJsonString(response, "intent");
            final ChatIntent intent = ChatIntent.fromValue(intentStr);
            final List<String> keywords = extractJsonArray(response, "keywords");
            final String reasoning = extractJsonString(response, "reasoning");

            if (intent == ChatIntent.SEARCH || intent == ChatIntent.FAQ) {
                return IntentDetectionResult.search(keywords, reasoning);
            } else if (intent == ChatIntent.SUMMARY) {
                final String docId = extractJsonString(response, "document_id");
                return IntentDetectionResult.summary(docId, reasoning);
            } else {
                return IntentDetectionResult.chat(reasoning);
            }
        } catch (final Exception e) {
            logger.warn("Failed to parse intent response. response={}", response, e);
            return IntentDetectionResult.chat("Parse error: " + e.getMessage());
        }
    }

    /**
     * Searches for documents using extracted keywords.
     *
     * @param keywords the search keywords
     * @return list of search results
     */
    protected List<Map<String, Object>> searchWithKeywords(final List<String> keywords) {
        final String query = String.join(" ", keywords);
        return searchDocuments(query);
    }

    /**
     * Evaluates search results to identify the most relevant documents.
     *
     * @param userMessage the original user message
     * @param keywords the search keywords
     * @param searchResults the search results to evaluate
     * @return evaluation result with relevant document IDs
     */
    protected RelevanceEvaluationResult evaluateResults(final String userMessage, final List<String> keywords,
            final List<Map<String, Object>> searchResults) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        try {
            final String prompt = buildEvaluationPrompt(userMessage, keywords, searchResults, fessConfig);
            final LlmChatRequest request = new LlmChatRequest();
            request.addUserMessage(prompt);
            request.setMaxTokens(500);
            request.setTemperature(0.3);

            final LlmChatResponse response = llmClientManager.chat(request);
            return parseEvaluationResponse(response.getContent(), searchResults);
        } catch (final Exception e) {
            logger.warn("Failed to evaluate results, using all results. error={}", e.getMessage());
            final List<String> allDocIds = searchResults.stream()
                    .map(doc -> getStringValue(doc, "doc_id"))
                    .filter(StringUtil::isNotBlank)
                    .collect(Collectors.toList());
            return RelevanceEvaluationResult.fallbackAllRelevant(allDocIds);
        }
    }

    /**
     * Builds the evaluation prompt for LLM.
     *
     * @param userMessage the user's message
     * @param keywords the search keywords
     * @param searchResults the search results to evaluate
     * @param fessConfig the Fess configuration
     * @return the evaluation prompt
     */
    protected String buildEvaluationPrompt(final String userMessage, final List<String> keywords,
            final List<Map<String, Object>> searchResults, final FessConfig fessConfig) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Given the user question and search results, identify the most relevant documents.\n");
        sb.append("Return a JSON object with:\n");
        sb.append("- \"relevant_indexes\": array of 1-based indexes of relevant documents (max ");
        sb.append(fessConfig.getRagChatEvaluationMaxRelevantDocsAsInteger());
        sb.append(")\n");
        sb.append("- \"has_relevant\": boolean indicating if any results are relevant\n\n");
        sb.append("Question: ").append(userMessage).append("\n");
        sb.append("Keywords: ").append(String.join(", ", keywords)).append("\n\n");
        sb.append("Search Results:\n");

        for (int i = 0; i < searchResults.size(); i++) {
            final Map<String, Object> doc = searchResults.get(i);
            sb.append("[").append(i + 1).append("] ");
            sb.append("Title: ").append(getStringValue(doc, "title")).append("\n");
            sb.append("Description: ").append(getStringValue(doc, "content_description")).append("\n\n");
        }

        sb.append("Response (JSON only):");
        return sb.toString();
    }

    /**
     * Parses the evaluation response from LLM.
     *
     * @param response the LLM response
     * @param searchResults the search results
     * @return the parsed evaluation result
     */
    protected RelevanceEvaluationResult parseEvaluationResponse(final String response, final List<Map<String, Object>> searchResults) {
        try {
            final boolean hasRelevant = extractJsonBoolean(response, "has_relevant");
            if (!hasRelevant) {
                return RelevanceEvaluationResult.noRelevantResults();
            }

            final List<Integer> indexes = extractJsonIntArray(response, "relevant_indexes");
            final List<String> docIds = indexes.stream()
                    .filter(i -> i > 0 && i <= searchResults.size())
                    .map(i -> getStringValue(searchResults.get(i - 1), "doc_id"))
                    .filter(StringUtil::isNotBlank)
                    .collect(Collectors.toList());

            return RelevanceEvaluationResult.withRelevantDocs(docIds, indexes);
        } catch (final Exception e) {
            logger.warn("Failed to parse evaluation response. response={}", response, e);
            return RelevanceEvaluationResult.noRelevantResults();
        }
    }

    /**
     * Fetches full document content for the given document IDs.
     *
     * @param docIds the document IDs to fetch
     * @return list of documents with full content
     */
    protected List<Map<String, Object>> fetchFullContent(final List<String> docIds) {
        if (docIds.isEmpty()) {
            return Collections.emptyList();
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String[] fields = fessConfig.getRagChatContentFields().split(",");
        final SearchHelper searchHelper = ComponentUtil.getSearchHelper();

        try {
            return searchHelper.getDocumentListByDocIds(docIds.toArray(new String[0]), fields, OptionalThing.empty(),
                    SearchRequestParams.SearchRequestType.JSON);
        } catch (final Exception e) {
            logger.warn("Failed to fetch full content for docIds={}. error={}", docIds, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Generates an answer using full document content.
     *
     * @param userMessage the user's message
     * @param documents the documents with full content
     * @param session the chat session
     * @param callback the streaming callback
     */
    protected void generateAnswerWithContent(final String userMessage, final List<Map<String, Object>> documents, final ChatSession session,
            final LlmStreamCallback callback) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String context = buildFullContentContext(documents, fessConfig);
        final LlmChatRequest request = buildChatRequest(session, userMessage, context);
        request.setStream(true);

        llmClientManager.streamChat(request, callback);
    }

    /**
     * Builds context from full document content.
     *
     * @param documents the documents with full content
     * @param fessConfig the Fess configuration
     * @return the context string
     */
    protected String buildFullContentContext(final List<Map<String, Object>> documents, final FessConfig fessConfig) {
        final int maxChars = fessConfig.getRagChatContextMaxCharsAsInteger();
        final StringBuilder context = new StringBuilder();
        context.append("The following are documents that contain information to answer the question:\n\n");

        int totalChars = context.length();
        int index = 1;

        for (final Map<String, Object> doc : documents) {
            final String title = getStringValue(doc, "title");
            final String url = getStringValue(doc, "url");
            final String content = getStringValue(doc, "content");
            final String description = getStringValue(doc, "content_description");

            final StringBuilder docContext = new StringBuilder();
            docContext.append("[").append(index).append("] ");
            if (StringUtil.isNotBlank(title)) {
                docContext.append(title).append("\n");
            }
            if (StringUtil.isNotBlank(url)) {
                docContext.append("URL: ").append(url).append("\n");
            }
            // Prefer full content, fallback to description
            final String docContent = StringUtil.isNotBlank(content) ? content : description;
            if (StringUtil.isNotBlank(docContent)) {
                docContext.append(docContent).append("\n");
            }
            docContext.append("\n");

            if (totalChars + docContext.length() > maxChars) {
                // Truncate content to fit
                final int remaining = maxChars - totalChars - 100;
                if (remaining > 0 && docContext.length() > remaining) {
                    docContext.setLength(remaining);
                    docContext.append("...\n\n");
                    context.append(docContext);
                }
                break;
            }

            context.append(docContext);
            totalChars += docContext.length();
            index++;
        }

        return context.toString();
    }

    /**
     * Generates a direct answer without document search (for chat intent).
     *
     * @param userMessage the user's message
     * @param session the chat session
     * @param callback the streaming callback
     */
    protected void generateDirectAnswer(final String userMessage, final ChatSession session, final LlmStreamCallback callback) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final LlmChatRequest request = new LlmChatRequest();

        request.addSystemMessage(fessConfig.getRagChatSystemPrompt());

        for (final ChatMessage msg : session.getMessages()) {
            if (msg.isUser()) {
                request.addMessage(LlmMessage.user(msg.getContent()));
            } else if (msg.isAssistant()) {
                request.addMessage(LlmMessage.assistant(msg.getContent()));
            }
        }

        request.addUserMessage(userMessage);
        request.setMaxTokens(fessConfig.getRagChatMaxTokensAsInteger());
        request.setTemperature(fessConfig.getRagChatTemperatureAsDecimal().doubleValue());
        request.setStream(true);

        llmClientManager.streamChat(request, callback);
    }

    /**
     * Generates a response when no relevant documents are found.
     *
     * @param userMessage the user's message
     * @param session the chat session
     * @param callback the streaming callback
     */
    protected void generateNoResultsResponse(final String userMessage, final ChatSession session, final LlmStreamCallback callback) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final LlmChatRequest request = new LlmChatRequest();

        final String systemPrompt = fessConfig.getRagChatSystemPrompt() + "\n\nNote: No relevant documents were found in the search. "
                + "Please provide a helpful response based on your general knowledge, "
                + "and let the user know that no specific documents were found.";
        request.addSystemMessage(systemPrompt);

        for (final ChatMessage msg : session.getMessages()) {
            if (msg.isUser()) {
                request.addMessage(LlmMessage.user(msg.getContent()));
            } else if (msg.isAssistant()) {
                request.addMessage(LlmMessage.assistant(msg.getContent()));
            }
        }

        request.addUserMessage(userMessage);
        request.setMaxTokens(fessConfig.getRagChatMaxTokensAsInteger());
        request.setTemperature(fessConfig.getRagChatTemperatureAsDecimal().doubleValue());
        request.setStream(true);

        llmClientManager.streamChat(request, callback);
    }

    /**
     * Renders markdown text to sanitized HTML.
     *
     * @param markdown the markdown text
     * @return sanitized HTML
     */
    protected String renderMarkdownToHtml(final String markdown) {
        if (markdownRenderer == null || !markdownRenderer.isInitialized()) {
            logger.warn("MarkdownRenderer is not initialized, returning escaped text");
            return escapeHtml(markdown);
        }
        return markdownRenderer.render(markdown);
    }

    /**
     * Escapes HTML special characters.
     *
     * @param text the text to escape
     * @return the escaped text
     */
    protected String escapeHtml(final String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }

    // JSON parsing helper methods

    /**
     * Extracts a string value from JSON response.
     *
     * @param json the JSON response
     * @param key the key to extract
     * @return the extracted string value
     */
    protected String extractJsonString(final String json, final String key) {
        final String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
        final java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        final java.util.regex.Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : "";
    }

    /**
     * Extracts a boolean value from JSON response.
     *
     * @param json the JSON response
     * @param key the key to extract
     * @return the extracted boolean value
     */
    protected boolean extractJsonBoolean(final String json, final String key) {
        final String pattern = "\"" + key + "\"\\s*:\\s*(true|false)";
        final java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.CASE_INSENSITIVE);
        final java.util.regex.Matcher m = p.matcher(json);
        return m.find() && "true".equalsIgnoreCase(m.group(1));
    }

    /**
     * Extracts a string array from JSON response.
     *
     * @param json the JSON response
     * @param key the key to extract
     * @return the extracted string array
     */
    protected List<String> extractJsonArray(final String json, final String key) {
        final String pattern = "\"" + key + "\"\\s*:\\s*\\[([^\\]]*)\\]";
        final java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        final java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            final String arrayContent = m.group(1);
            return Arrays.stream(arrayContent.split(","))
                    .map(s -> s.trim().replaceAll("^\"|\"$", ""))
                    .filter(StringUtil::isNotBlank)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * Extracts an integer array from JSON response.
     *
     * @param json the JSON response
     * @param key the key to extract
     * @return the extracted integer array
     */
    protected List<Integer> extractJsonIntArray(final String json, final String key) {
        final String pattern = "\"" + key + "\"\\s*:\\s*\\[([^\\]]*)\\]";
        final java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        final java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            final String arrayContent = m.group(1);
            return Arrays.stream(arrayContent.split(","))
                    .map(String::trim)
                    .filter(s -> s.matches("\\d+"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * Gets the maximum number of history messages to retain.
     *
     * @return the maximum number of history messages
     */
    protected int getMaxHistoryMessages() {
        return ComponentUtil.getFessConfig().getRagChatHistoryMaxMessagesAsInteger();
    }

    /**
     * Searches for documents relevant to the user's query.
     *
     * @param query the search query
     * @return a list of documents matching the query
     */
    protected List<Map<String, Object>> searchDocuments(final String query) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int maxDocs = fessConfig.getRagChatContextMaxDocumentsAsInteger();

        try {
            final SearchRenderData data = new SearchRenderData();
            final ChatSearchRequestParams params = new ChatSearchRequestParams(query, maxDocs, fessConfig);

            ComponentUtil.getSearchHelper().search(params, data, OptionalThing.empty());

            @SuppressWarnings("unchecked")
            final List<Map<String, Object>> docs = (List<Map<String, Object>>) data.getDocumentItems();
            if (docs != null) {
                return docs;
            }
        } catch (final Exception e) {
            logger.warn("Failed to search documents for RAG: query={}", query, e);
        }

        return new ArrayList<>();
    }

    /**
     * Builds context from search results for the LLM prompt.
     *
     * @param searchResults the search results to build context from
     * @return the context string for the LLM prompt
     */
    protected String buildContext(final List<Map<String, Object>> searchResults) {
        if (searchResults.isEmpty()) {
            return "";
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int maxChars = fessConfig.getRagChatContextMaxCharsAsInteger();

        final StringBuilder context = new StringBuilder();
        context.append("The following are search results that may help answer the question:\n\n");

        int totalChars = context.length();
        int index = 1;

        for (final Map<String, Object> doc : searchResults) {
            final String title = getStringValue(doc, "title");
            final String url = getStringValue(doc, "url");
            final String content = getStringValue(doc, "content_description");

            final StringBuilder docContext = new StringBuilder();
            docContext.append("[").append(index).append("] ");
            if (StringUtil.isNotBlank(title)) {
                docContext.append(title).append("\n");
            }
            if (StringUtil.isNotBlank(url)) {
                docContext.append("URL: ").append(url).append("\n");
            }
            if (StringUtil.isNotBlank(content)) {
                docContext.append(content).append("\n");
            }
            docContext.append("\n");

            if (totalChars + docContext.length() > maxChars) {
                break;
            }

            context.append(docContext);
            totalChars += docContext.length();
            index++;
        }

        return context.toString();
    }

    /**
     * Builds the LLM chat request with conversation history.
     *
     * @param session the chat session containing conversation history
     * @param userMessage the user's message
     * @param context the context from search results
     * @return the LLM chat request
     */
    protected LlmChatRequest buildChatRequest(final ChatSession session, final String userMessage, final String context) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final LlmChatRequest request = new LlmChatRequest();

        // System prompt
        String systemPrompt = fessConfig.getRagChatSystemPrompt();
        if (StringUtil.isNotBlank(context)) {
            systemPrompt = systemPrompt + "\n\n" + context;
        }
        request.addSystemMessage(systemPrompt);

        // Add conversation history
        for (final ChatMessage msg : session.getMessages()) {
            if (msg.isUser()) {
                request.addMessage(LlmMessage.user(msg.getContent()));
            } else if (msg.isAssistant()) {
                request.addMessage(LlmMessage.assistant(msg.getContent()));
            }
        }

        // Add current user message
        request.addUserMessage(userMessage);

        // Set parameters
        request.setMaxTokens(fessConfig.getRagChatMaxTokensAsInteger());
        request.setTemperature(fessConfig.getRagChatTemperatureAsDecimal().doubleValue());

        return request;
    }

    /**
     * Gets a string value from a map.
     *
     * @param map the map to get the value from
     * @param key the key to look up
     * @return the string value, or an empty string if not found
     */
    protected String getStringValue(final Map<String, Object> map, final String key) {
        final Object value = map.get(key);
        return value != null ? value.toString() : "";
    }

    /**
     * Result of a chat request.
     */
    public static class ChatResult {
        private final String sessionId;
        private final ChatMessage message;
        private final List<Map<String, Object>> sources;

        /**
         * Creates a new chat result.
         *
         * @param sessionId the session ID
         * @param message the chat message
         * @param sources the list of source documents
         */
        public ChatResult(final String sessionId, final ChatMessage message, final List<Map<String, Object>> sources) {
            this.sessionId = sessionId;
            this.message = message;
            this.sources = sources;
        }

        /**
         * Gets the session ID.
         *
         * @return the session ID
         */
        public String getSessionId() {
            return sessionId;
        }

        /**
         * Gets the chat message.
         *
         * @return the chat message
         */
        public ChatMessage getMessage() {
            return message;
        }

        /**
         * Gets the source documents.
         *
         * @return the list of source documents
         */
        public List<Map<String, Object>> getSources() {
            return sources;
        }
    }

    /**
     * Search request parameters for RAG chat context retrieval.
     */
    protected static class ChatSearchRequestParams extends SearchRequestParams {
        private final String query;
        private final int pageSize;
        private final FessConfig fessConfig;

        /**
         * Creates new chat search request parameters.
         *
         * @param query the search query
         * @param pageSize the page size
         * @param fessConfig the Fess configuration
         */
        public ChatSearchRequestParams(final String query, final int pageSize, final FessConfig fessConfig) {
            this.query = query;
            this.pageSize = pageSize;
            this.fessConfig = fessConfig;
        }

        @Override
        public String getQuery() {
            return query;
        }

        @Override
        public Map<String, String[]> getFields() {
            return Collections.emptyMap();
        }

        @Override
        public Map<String, String[]> getConditions() {
            return Collections.emptyMap();
        }

        @Override
        public String[] getLanguages() {
            return new String[0];
        }

        @Override
        public GeoInfo getGeoInfo() {
            return null;
        }

        @Override
        public FacetInfo getFacetInfo() {
            return null;
        }

        @Override
        public HighlightInfo getHighlightInfo() {
            return ComponentUtil.getViewHelper().createHighlightInfo();
        }

        @Override
        public String getSort() {
            return null;
        }

        @Override
        public int getStartPosition() {
            return fessConfig.getPagingSearchPageStartAsInteger();
        }

        @Override
        public int getPageSize() {
            return pageSize;
        }

        @Override
        public int getOffset() {
            return 0;
        }

        @Override
        public String[] getExtraQueries() {
            return new String[0];
        }

        @Override
        public Object getAttribute(final String name) {
            return null;
        }

        @Override
        public Locale getLocale() {
            return Locale.getDefault();
        }

        @Override
        public SearchRequestType getType() {
            return SearchRequestType.JSON;
        }

        @Override
        public String getSimilarDocHash() {
            return null;
        }
    }
}
