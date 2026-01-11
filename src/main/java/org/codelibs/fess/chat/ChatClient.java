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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        return llmClientManager.available();
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
        final LlmChatResponse llmResponse = llmClientManager.chat(request);
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
        llmClientManager.streamChat(request, (chunk, done) -> {
            responseContent.append(chunk);
            callback.onChunk(chunk, done);
        });

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
