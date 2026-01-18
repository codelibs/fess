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
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.ChatMessage;
import org.codelibs.fess.entity.ChatMessage.ChatSource;
import org.codelibs.fess.entity.ChatSession;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.helper.MarkdownRenderer;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.llm.LlmChatRequest;
import org.codelibs.fess.llm.LlmChatResponse;
import org.codelibs.fess.llm.LlmClientManager;
import org.codelibs.fess.llm.LlmMessage;
import org.codelibs.fess.llm.LlmStreamCallback;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Resource;

/**
 * Client class for RAG (Retrieval-Augmented Generation) chat functionality.
 *
 * @author FessProject
 */
public class ChatClient {

    private static final Logger logger = LogManager.getLogger(ChatClient.class);

    /** ObjectMapper for JSON parsing. */
    private static final ObjectMapper objectMapper = new ObjectMapper();

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
                logger.debug("Intent detected. intent={}, query={}", intentResult.getIntent(), intentResult.getQuery());
            }

            if (intentResult.getIntent() == ChatIntent.UNCLEAR) {
                // Intent is unclear - ask user for clarification
                callback.onPhaseStart(ChatPhaseCallback.PHASE_ANSWER, "Generating response...");
                generateUnclearIntentResponse(userMessage, session, (chunk, done) -> {
                    fullResponse.append(chunk);
                    callback.onChunk(chunk, done);
                });
                callback.onPhaseComplete(ChatPhaseCallback.PHASE_ANSWER);
            } else if (intentResult.getIntent() == ChatIntent.SUMMARY) {
                // Summary intent - search by URL and generate summary
                final String documentUrl = intentResult.getDocumentUrl();
                callback.onPhaseStart(ChatPhaseCallback.PHASE_SEARCH, "Searching for document...", documentUrl);
                final List<Map<String, Object>> urlResults = searchByUrl(documentUrl);
                callback.onPhaseComplete(ChatPhaseCallback.PHASE_SEARCH);

                if (urlResults.isEmpty()) {
                    // URL not found - inform user
                    callback.onPhaseStart(ChatPhaseCallback.PHASE_ANSWER, "Generating response...");
                    generateDocumentNotFoundResponse(userMessage, documentUrl, session, (chunk, done) -> {
                        fullResponse.append(chunk);
                        callback.onChunk(chunk, done);
                    });
                    callback.onPhaseComplete(ChatPhaseCallback.PHASE_ANSWER);
                } else {
                    // Fetch full content and generate summary
                    callback.onPhaseStart(ChatPhaseCallback.PHASE_FETCH, "Retrieving document content...");
                    final List<String> docIds = urlResults.stream()
                            .map(doc -> (String) doc.get("doc_id"))
                            .filter(id -> id != null)
                            .collect(Collectors.toList());
                    final List<Map<String, Object>> fullDocs = fetchFullContent(docIds);
                    callback.onPhaseComplete(ChatPhaseCallback.PHASE_FETCH);
                    sources = fullDocs;

                    callback.onPhaseStart(ChatPhaseCallback.PHASE_ANSWER, "Generating summary...");
                    generateSummaryResponse(userMessage, fullDocs, session, (chunk, done) -> {
                        fullResponse.append(chunk);
                        callback.onChunk(chunk, done);
                    });
                    callback.onPhaseComplete(ChatPhaseCallback.PHASE_ANSWER);
                }
            } else {
                // Phase 2: Search with query
                final String query = StringUtil.isBlank(intentResult.getQuery()) ? userMessage : intentResult.getQuery();
                callback.onPhaseStart(ChatPhaseCallback.PHASE_SEARCH, "Searching documents...", query);
                final List<Map<String, Object>> searchResults = searchWithQuery(query);
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
                    final RelevanceEvaluationResult evalResult = evaluateResults(userMessage, query, searchResults);
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
     * Builds a prompt for intent detection from user message.
     *
     * @param userMessage the user's input message
     * @param fessConfig the Fess configuration
     * @return the constructed prompt string for intent detection
     */
    protected String buildIntentDetectionPrompt(final String userMessage, final FessConfig fessConfig) {
        return "Analyze the following user question and determine the intent.\n" + "Return a JSON object with:\n"
                + "- \"intent\": one of:\n" + "  - \"search\": user wants to search for documents\n"
                + "  - \"summary\": user wants a summary of a specific document (extract URL from question)\n"
                + "  - \"faq\": user is asking a FAQ-type question\n"
                + "  - \"unclear\": cannot determine what documents to search (question is too vague)\n"
                + "- \"query\": Lucene query string for search (required for search/faq intents)\n"
                + "- \"url\": the document URL to summarize (required for summary intent)\n"
                + "- \"reasoning\": brief explanation of your decision\n\n" + "LUCENE QUERY GUIDELINES:\n"
                + "- Proper nouns/product names: use quotation marks (e.g., \"Fess\")\n"
                + "- Title boosting: for important terms, use title:\"term\"^2\n" + "- Required terms: use + prefix (e.g., +Fess +Docker)\n"
                + "- Optional/synonym terms: use OR grouping (e.g., (tutorial OR guide OR howto))\n"
                + "- Multi-word phrases: use quotation marks\n\n" + "IMPORTANT RULES:\n"
                + "1. ALWAYS generate a Lucene query for search/faq intents. Use \"unclear\" only if truly ambiguous.\n"
                + "2. Do NOT answer from your own knowledge. All responses must be based on document search.\n"
                + "3. If user mentions a specific URL or document path, use \"summary\" intent.\n\n" + "EXAMPLES:\n" + "Input: \"Fess\"\n"
                + "Output: {\"intent\":\"search\",\"query\":\"title:\\\"Fess\\\"^2 OR \\\"Fess\\\"\",\"reasoning\":\"Product name search\"}\n\n"
                + "Input: \"FessのDockerの使い方\"\n"
                + "Output: {\"intent\":\"search\",\"query\":\"+\\\"Fess\\\" +Docker (使い方 OR 方法 OR tutorial)\",\"reasoning\":\"Tutorial query\"}\n\n"
                + "Question: " + userMessage + "\n\n" + "Response (JSON only):";
    }

    /**
     * Parses the LLM response and extracts intent detection result.
     *
     * @param response the JSON response from LLM
     * @return the parsed intent detection result
     */
    protected IntentDetectionResult parseIntentResponse(final String response) {
        try {
            final String intentStr = extractJsonString(response, "intent");
            final ChatIntent intent = ChatIntent.fromValue(intentStr);
            final String query = extractJsonString(response, "query");
            final String reasoning = extractJsonString(response, "reasoning");

            if (intent == ChatIntent.SEARCH) {
                return IntentDetectionResult.search(query, reasoning);
            } else if (intent == ChatIntent.FAQ) {
                return IntentDetectionResult.faq(query, reasoning);
            } else if (intent == ChatIntent.SUMMARY) {
                final String docUrl = extractJsonString(response, "url");
                return IntentDetectionResult.summary(docUrl, reasoning);
            } else {
                return IntentDetectionResult.unclear(reasoning);
            }
        } catch (final Exception e) {
            logger.warn("Failed to parse intent response. response={}", response, e);
            return IntentDetectionResult.unclear("Parse error: " + e.getMessage());
        }
    }

    /**
     * Searches documents using a Lucene query.
     *
     * @param query the Lucene query string
     * @return the list of search result documents
     */
    protected List<Map<String, Object>> searchWithQuery(final String query) {
        if (StringUtil.isBlank(query)) {
            return Collections.emptyList();
        }
        return searchDocuments(query);
    }

    /**
     * Evaluates search results to identify the most relevant documents.
     *
     * @param userMessage the original user message
     * @param query the search query
     * @param searchResults the search results to evaluate
     * @return evaluation result with relevant document IDs
     */
    protected RelevanceEvaluationResult evaluateResults(final String userMessage, final String query,
            final List<Map<String, Object>> searchResults) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        try {
            final String prompt = buildEvaluationPrompt(userMessage, query, searchResults, fessConfig);
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
     * @param query the search query
     * @param searchResults the search results to evaluate
     * @param fessConfig the Fess configuration
     * @return the evaluation prompt
     */
    protected String buildEvaluationPrompt(final String userMessage, final String query, final List<Map<String, Object>> searchResults,
            final FessConfig fessConfig) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Given the user question and search results, identify the most relevant documents.\n");
        sb.append("Return a JSON object with:\n");
        sb.append("- \"relevant_indexes\": array of 1-based indexes of relevant documents (max ");
        sb.append(fessConfig.getRagChatEvaluationMaxRelevantDocsAsInteger());
        sb.append(")\n");
        sb.append("- \"has_relevant\": boolean indicating if any results are relevant\n\n");
        sb.append("Question: ").append(userMessage).append("\n");
        sb.append("Query: ").append(query).append("\n\n");
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
     * Generates a response asking user for clarification when intent is unclear.
     *
     * @param userMessage the user's message
     * @param session the chat session
     * @param callback the streaming callback
     */
    protected void generateUnclearIntentResponse(final String userMessage, final ChatSession session, final LlmStreamCallback callback) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final LlmChatRequest request = new LlmChatRequest();

        final String systemPrompt = "You are a helpful assistant for a document search system. "
                + "The user's question is too vague to determine what documents to search for. "
                + "Generate a polite message asking for clarification. Ask them:\n"
                + "- What specific topic or document are they looking for?\n" + "- Can they provide more details or keywords?\n"
                + "- What kind of information would be helpful?\n\n" + "IMPORTANT: Do NOT provide any answers from your own knowledge. "
                + "Only ask for clarification to help with document search.";
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
     * Searches for documents by URL.
     *
     * @param url the URL to search for
     * @return list of documents matching the URL
     */
    protected List<Map<String, Object>> searchByUrl(final String url) {
        if (StringUtil.isBlank(url)) {
            return Collections.emptyList();
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int maxDocs = fessConfig.getRagChatContextMaxDocumentsAsInteger();

        try {
            // Search using url field
            final SearchRenderData data = new SearchRenderData();
            final ChatSearchRequestParams params = new ChatSearchRequestParams("url:\"" + url + "\"", maxDocs, fessConfig);

            ComponentUtil.getSearchHelper().search(params, data, OptionalThing.empty());

            @SuppressWarnings("unchecked")
            final List<Map<String, Object>> docs = (List<Map<String, Object>>) data.getDocumentItems();
            if (docs != null) {
                return docs;
            }
        } catch (final Exception e) {
            logger.warn("Failed to search documents by URL: url={}", url, e);
        }

        return Collections.emptyList();
    }

    /**
     * Generates a response when the specified document URL is not found.
     *
     * @param userMessage the user's message
     * @param documentUrl the URL that was not found
     * @param session the chat session
     * @param callback the streaming callback
     */
    protected void generateDocumentNotFoundResponse(final String userMessage, final String documentUrl, final ChatSession session,
            final LlmStreamCallback callback) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final LlmChatRequest request = new LlmChatRequest();

        final String systemPrompt = "You are a helpful assistant for a document search system. "
                + "The user requested a summary of a document, but the specified URL was not found in the system. " + "URL searched: "
                + documentUrl + "\n\n" + "Generate a polite message informing the user that:\n"
                + "- The specified document could not be found\n" + "- The URL might be incorrect or the document may not be indexed\n"
                + "- They can try searching with keywords instead\n\n"
                + "IMPORTANT: Do NOT provide any information from your own knowledge. " + "Only inform them about the search result.";
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
     * Generates a summary of the specified documents.
     *
     * @param userMessage the user's message
     * @param documents the documents to summarize
     * @param session the chat session
     * @param callback the streaming callback
     */
    protected void generateSummaryResponse(final String userMessage, final List<Map<String, Object>> documents, final ChatSession session,
            final LlmStreamCallback callback) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final LlmChatRequest request = new LlmChatRequest();

        // Build context from documents
        final StringBuilder context = new StringBuilder();
        for (final Map<String, Object> doc : documents) {
            final String title = (String) doc.get("title");
            final String content = (String) doc.get("content");
            final String url = (String) doc.get("url");

            context.append("=== Document ===\n");
            if (title != null) {
                context.append("Title: ").append(title).append("\n");
            }
            if (url != null) {
                context.append("URL: ").append(url).append("\n");
            }
            if (content != null) {
                context.append("Content:\n").append(content).append("\n\n");
            }
        }

        final String systemPrompt = fessConfig.getRagChatSystemPrompt() + "\n\nYou are summarizing specific documents for the user. "
                + "Base your summary ONLY on the provided document content. " + "Do NOT add information from your own knowledge.\n\n"
                + "Document content:\n" + context.toString();
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
     * Generates a response when no relevant documents are found.
     *
     * @param userMessage the user's message
     * @param session the chat session
     * @param callback the streaming callback
     */
    protected void generateNoResultsResponse(final String userMessage, final ChatSession session, final LlmStreamCallback callback) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final LlmChatRequest request = new LlmChatRequest();

        final String systemPrompt = "You are a helpful assistant for a document search system. "
                + "The search for relevant documents returned no results matching the user's query. "
                + "Generate a polite message informing the user that no documents matching their query were found. "
                + "Suggest ways they could refine their search, such as:\n" + "- Using different keywords\n"
                + "- Being more specific or more general\n" + "- Checking for spelling errors\n" + "- Trying related terms\n\n"
                + "IMPORTANT: Do NOT provide any answers from your own knowledge. "
                + "Only inform them about the search results and offer suggestions for refining their search.";
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
     * Strips code fence markers from JSON response.
     *
     * @param response the response that may contain code fences
     * @return the response with code fences removed
     */
    protected String stripCodeFences(final String response) {
        if (response == null) {
            return "";
        }
        String stripped = response.trim();
        if (stripped.startsWith("```json")) {
            stripped = stripped.substring(7);
        } else if (stripped.startsWith("```")) {
            stripped = stripped.substring(3);
        }
        if (stripped.endsWith("```")) {
            stripped = stripped.substring(0, stripped.length() - 3);
        }
        return stripped.trim();
    }

    /**
     * Extracts a string value from JSON response using Jackson parser.
     *
     * @param json the JSON response
     * @param key the key to extract
     * @return the extracted string value
     */
    protected String extractJsonString(final String json, final String key) {
        try {
            final String cleanJson = stripCodeFences(json);
            final JsonNode root = objectMapper.readTree(cleanJson);
            final JsonNode node = root.get(key);
            if (node != null && node.isTextual()) {
                return node.asText();
            }
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to parse JSON for key={}. error={}", key, e.getMessage());
            }
            // Fallback to improved regex
            return extractJsonStringFallback(json, key);
        }
        return "";
    }

    /**
     * Fallback regex-based extraction for string values.
     *
     * @param json the JSON response
     * @param key the key to extract
     * @return the extracted string value
     */
    protected String extractJsonStringFallback(final String json, final String key) {
        final String pattern = "\"" + key + "\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"";
        final java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        final java.util.regex.Matcher m = p.matcher(stripCodeFences(json));
        if (m.find()) {
            return m.group(1).replace("\\\"", "\"").replace("\\\\", "\\");
        }
        return "";
    }

    /**
     * Extracts a boolean value from JSON response using Jackson parser.
     *
     * @param json the JSON response
     * @param key the key to extract
     * @return the extracted boolean value
     */
    protected boolean extractJsonBoolean(final String json, final String key) {
        try {
            final String cleanJson = stripCodeFences(json);
            final JsonNode root = objectMapper.readTree(cleanJson);
            final JsonNode node = root.get(key);
            if (node != null && node.isBoolean()) {
                return node.asBoolean();
            }
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to parse JSON for key={}. error={}", key, e.getMessage());
            }
            // Fallback to regex
            final String pattern = "\"" + key + "\"\\s*:\\s*(true|false)";
            final java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.CASE_INSENSITIVE);
            final java.util.regex.Matcher m = p.matcher(stripCodeFences(json));
            return m.find() && "true".equalsIgnoreCase(m.group(1));
        }
        return false;
    }

    /**
     * Extracts a string array from JSON response using Jackson parser.
     *
     * @param json the JSON response
     * @param key the key to extract
     * @return the extracted string array
     */
    protected List<String> extractJsonArray(final String json, final String key) {
        try {
            final String cleanJson = stripCodeFences(json);
            final JsonNode root = objectMapper.readTree(cleanJson);
            final JsonNode node = root.get(key);
            if (node != null && node.isArray()) {
                return StreamSupport.stream(node.spliterator(), false)
                        .filter(JsonNode::isTextual)
                        .map(JsonNode::asText)
                        .filter(StringUtil::isNotBlank)
                        .collect(Collectors.toList());
            }
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to parse JSON for key={}. error={}", key, e.getMessage());
            }
            // Fallback to regex
            final String pattern = "\"" + key + "\"\\s*:\\s*\\[([^\\]]*)\\]";
            final java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            final java.util.regex.Matcher m = p.matcher(stripCodeFences(json));
            if (m.find()) {
                final String arrayContent = m.group(1);
                return Arrays.stream(arrayContent.split(","))
                        .map(s -> s.trim().replaceAll("^\"|\"$", ""))
                        .filter(StringUtil::isNotBlank)
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    /**
     * Extracts an integer array from JSON response using Jackson parser.
     *
     * @param json the JSON response
     * @param key the key to extract
     * @return the extracted integer array
     */
    protected List<Integer> extractJsonIntArray(final String json, final String key) {
        try {
            final String cleanJson = stripCodeFences(json);
            final JsonNode root = objectMapper.readTree(cleanJson);
            final JsonNode node = root.get(key);
            if (node != null && node.isArray()) {
                return StreamSupport.stream(node.spliterator(), false)
                        .filter(JsonNode::isInt)
                        .map(JsonNode::asInt)
                        .collect(Collectors.toList());
            }
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to parse JSON for key={}. error={}", key, e.getMessage());
            }
            // Fallback to regex
            final String pattern = "\"" + key + "\"\\s*:\\s*\\[([^\\]]*)\\]";
            final java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            final java.util.regex.Matcher m = p.matcher(stripCodeFences(json));
            if (m.find()) {
                final String arrayContent = m.group(1);
                return Arrays.stream(arrayContent.split(","))
                        .map(String::trim)
                        .filter(s -> s.matches("\\d+"))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
            }
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
