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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import org.codelibs.fess.llm.ChatIntent;
import org.codelibs.fess.llm.IntentDetectionResult;
import org.codelibs.fess.llm.LlmChatResponse;
import org.codelibs.fess.llm.LlmException;
import org.codelibs.fess.llm.LlmClientManager;
import org.codelibs.fess.llm.LlmMessage;
import org.codelibs.fess.llm.LlmStreamCallback;
import org.codelibs.fess.llm.RelevanceEvaluationResult;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;

import jakarta.annotation.Resource;

/**
 * Client class for RAG (Retrieval-Augmented Generation) chat functionality.
 *
 * Orchestrates the multi-phase RAG workflow including session management,
 * document search, and delegation to LlmClientManager for LLM operations.
 * Prompt construction and LLM-specific logic is handled by LlmClient implementations.
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
            logger.debug("[RAG] ChatClient availability check. available={}", available);
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
        final long startTime = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("[RAG] Starting chat request. sessionId={}, userId={}, userMessage={}", sessionId, userId, userMessage);
        }

        final ChatSession session = chatSessionManager.getOrCreateSession(sessionId, userId);
        // Extract history snapshot before adding current user message to avoid duplication
        final List<LlmMessage> history = extractHistory(session);
        // Add user message immediately for session integrity under concurrent access
        final ChatMessage userChatMessage = ChatMessage.userMessage(userMessage);
        session.addMessage(userChatMessage);
        final List<Map<String, Object>> searchResults = searchDocuments(userMessage);

        try {
            final LlmChatResponse llmResponse = llmClientManager.generateAnswer(userMessage, searchResults, history);

            final ChatMessage assistantMessage = ChatMessage.assistantMessage(llmResponse.getContent());

            for (int i = 0; i < searchResults.size(); i++) {
                assistantMessage.addSource(new ChatSource(i + 1, searchResults.get(i)));
            }

            session.addMessage(assistantMessage);

            if (logger.isDebugEnabled()) {
                logger.debug("[RAG] Chat request completed. sessionId={}, sourcesCount={}, elapsedTime={}ms", session.getSessionId(),
                        searchResults.size(), System.currentTimeMillis() - startTime);
            }

            return new ChatResult(session.getSessionId(), assistantMessage, searchResults);
        } catch (final Exception e) {
            logger.warn("Failed to get response from LLM. sessionId={}, error={}", session.getSessionId(), e.getMessage(), e);
            throw e;
        } finally {
            session.trimHistory(getMaxHistoryMessages());
        }
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
        final long startTime = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("[RAG] Starting streaming chat request. sessionId={}, userId={}, userMessage={}", sessionId, userId, userMessage);
        }

        final ChatSession session = chatSessionManager.getOrCreateSession(sessionId, userId);
        // Extract history snapshot before adding current user message to avoid duplication
        final List<LlmMessage> history = extractHistory(session);
        // Add user message immediately for session integrity under concurrent access
        final ChatMessage userChatMessage = ChatMessage.userMessage(userMessage);
        session.addMessage(userChatMessage);
        final List<Map<String, Object>> searchResults = searchDocuments(userMessage);

        final StringBuilder responseContent = new StringBuilder();
        try {
            llmClientManager.streamGenerateAnswer(userMessage, searchResults, history, (chunk, done) -> {
                responseContent.append(chunk);
                callback.onChunk(chunk, done);
            });

            final ChatMessage assistantMessage = ChatMessage.assistantMessage(responseContent.toString());

            for (int i = 0; i < searchResults.size(); i++) {
                assistantMessage.addSource(new ChatSource(i + 1, searchResults.get(i)));
            }

            session.addMessage(assistantMessage);

            if (logger.isDebugEnabled()) {
                logger.debug("[RAG] Streaming chat request completed. sessionId={}, sourcesCount={}, elapsedTime={}ms",
                        session.getSessionId(), searchResults.size(), System.currentTimeMillis() - startTime);
            }

            return new ChatResult(session.getSessionId(), assistantMessage, searchResults);
        } catch (final Exception e) {
            logger.warn("Failed to stream response from LLM. sessionId={}, error={}", session.getSessionId(), e.getMessage(), e);
            throw e;
        } finally {
            session.trimHistory(getMaxHistoryMessages());
        }
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
        final long startTime = System.currentTimeMillis();
        // Note: Locale is resolved via LaRequestUtil in LlmClient. During long SSE processing,
        // the request context may become unavailable, falling back to Locale.getDefault().
        if (logger.isDebugEnabled()) {
            logger.debug("[RAG] Starting enhanced streaming chat request. sessionId={}, userId={}, userMessage={}", sessionId, userId,
                    userMessage);
        }

        final ChatSession session = chatSessionManager.getOrCreateSession(sessionId, userId);
        // Extract history snapshot before adding current user message to avoid duplication
        final List<LlmMessage> history = extractHistory(session);
        // Add user message immediately for session integrity under concurrent access
        final ChatMessage userChatMessage = ChatMessage.userMessage(userMessage);
        session.addMessage(userChatMessage);
        final StringBuilder fullResponse = new StringBuilder();
        List<Map<String, Object>> sources = new ArrayList<>();

        try {
            // Phase 1: Intent Detection
            long phaseStartTime = System.currentTimeMillis();
            callback.onPhaseStart(ChatPhaseCallback.PHASE_INTENT, "Analyzing your question...");
            final IntentDetectionResult intentResult = llmClientManager.detectIntent(userMessage, history);
            callback.onPhaseComplete(ChatPhaseCallback.PHASE_INTENT);

            if (logger.isDebugEnabled()) {
                logger.debug("[RAG] Phase {} completed. intent={}, query={}, reasoning={}, phaseElapsedTime={}ms",
                        ChatPhaseCallback.PHASE_INTENT, intentResult.getIntent(), intentResult.getQuery(), intentResult.getReasoning(),
                        System.currentTimeMillis() - phaseStartTime);
            }

            if (intentResult.getIntent() == ChatIntent.UNCLEAR) {
                // Intent is unclear - ask user for clarification
                phaseStartTime = System.currentTimeMillis();
                callback.onPhaseStart(ChatPhaseCallback.PHASE_ANSWER, "Generating response...");
                llmClientManager.generateUnclearIntentResponse(userMessage, history, (chunk, done) -> {
                    fullResponse.append(chunk);
                    callback.onChunk(chunk, done);
                });
                callback.onPhaseComplete(ChatPhaseCallback.PHASE_ANSWER);
                if (logger.isDebugEnabled()) {
                    logger.debug("[RAG] Phase {} completed. responseLength={}, phaseElapsedTime={}ms", ChatPhaseCallback.PHASE_ANSWER,
                            fullResponse.length(), System.currentTimeMillis() - phaseStartTime);
                }
            } else if (intentResult.getIntent() == ChatIntent.SUMMARY) {
                // Summary intent - search by URL and generate summary
                final String documentUrl = intentResult.getDocumentUrl();
                phaseStartTime = System.currentTimeMillis();
                callback.onPhaseStart(ChatPhaseCallback.PHASE_SEARCH, "Searching for document...", documentUrl);
                final List<Map<String, Object>> urlResults = searchByUrl(documentUrl);
                callback.onPhaseComplete(ChatPhaseCallback.PHASE_SEARCH);
                if (logger.isDebugEnabled()) {
                    logger.debug("[RAG] Phase {} completed. documentUrl={}, resultCount={}, phaseElapsedTime={}ms",
                            ChatPhaseCallback.PHASE_SEARCH, documentUrl, urlResults.size(), System.currentTimeMillis() - phaseStartTime);
                }

                if (urlResults.isEmpty()) {
                    // URL not found - inform user
                    phaseStartTime = System.currentTimeMillis();
                    callback.onPhaseStart(ChatPhaseCallback.PHASE_ANSWER, "Generating response...");
                    llmClientManager.generateDocumentNotFoundResponse(userMessage, documentUrl, history, (chunk, done) -> {
                        fullResponse.append(chunk);
                        callback.onChunk(chunk, done);
                    });
                    callback.onPhaseComplete(ChatPhaseCallback.PHASE_ANSWER);
                    if (logger.isDebugEnabled()) {
                        logger.debug("[RAG] Phase {} completed. responseLength={}, phaseElapsedTime={}ms", ChatPhaseCallback.PHASE_ANSWER,
                                fullResponse.length(), System.currentTimeMillis() - phaseStartTime);
                    }
                } else {
                    // Fetch full content and generate summary
                    phaseStartTime = System.currentTimeMillis();
                    callback.onPhaseStart(ChatPhaseCallback.PHASE_FETCH, "Retrieving document content...");
                    final List<String> docIds = urlResults.stream()
                            .map(doc -> (String) doc.get("doc_id"))
                            .filter(id -> id != null)
                            .collect(Collectors.toList());
                    final List<Map<String, Object>> fullDocs = fetchFullContent(docIds);
                    callback.onPhaseComplete(ChatPhaseCallback.PHASE_FETCH);
                    sources = fullDocs;
                    if (logger.isDebugEnabled()) {
                        logger.debug("[RAG] Phase {} completed. docIds={}, fetchedCount={}, phaseElapsedTime={}ms",
                                ChatPhaseCallback.PHASE_FETCH, docIds, fullDocs.size(), System.currentTimeMillis() - phaseStartTime);
                    }

                    phaseStartTime = System.currentTimeMillis();
                    callback.onPhaseStart(ChatPhaseCallback.PHASE_ANSWER, "Generating summary...");
                    llmClientManager.generateSummaryResponse(userMessage, fullDocs, history, (chunk, done) -> {
                        fullResponse.append(chunk);
                        callback.onChunk(chunk, done);
                    });
                    callback.onPhaseComplete(ChatPhaseCallback.PHASE_ANSWER);
                    if (logger.isDebugEnabled()) {
                        logger.debug("[RAG] Phase {} completed. responseLength={}, phaseElapsedTime={}ms", ChatPhaseCallback.PHASE_ANSWER,
                                fullResponse.length(), System.currentTimeMillis() - phaseStartTime);
                    }
                }
            } else {
                // Phase 2: Search with query
                final String query = StringUtil.isBlank(intentResult.getQuery()) ? userMessage : intentResult.getQuery();
                phaseStartTime = System.currentTimeMillis();
                callback.onPhaseStart(ChatPhaseCallback.PHASE_SEARCH, "Searching documents...", query);
                final List<Map<String, Object>> searchResults = searchWithQuery(query);
                callback.onPhaseComplete(ChatPhaseCallback.PHASE_SEARCH);

                if (logger.isDebugEnabled()) {
                    logger.debug("[RAG] Phase {} completed. query={}, resultCount={}, phaseElapsedTime={}ms",
                            ChatPhaseCallback.PHASE_SEARCH, query, searchResults.size(), System.currentTimeMillis() - phaseStartTime);
                }

                if (searchResults.isEmpty()) {
                    // No results - generate fallback response
                    phaseStartTime = System.currentTimeMillis();
                    callback.onPhaseStart(ChatPhaseCallback.PHASE_ANSWER, "Generating response...");
                    llmClientManager.generateNoResultsResponse(userMessage, history, (chunk, done) -> {
                        fullResponse.append(chunk);
                        callback.onChunk(chunk, done);
                    });
                    callback.onPhaseComplete(ChatPhaseCallback.PHASE_ANSWER);
                    if (logger.isDebugEnabled()) {
                        logger.debug("[RAG] Phase {} completed. responseLength={}, phaseElapsedTime={}ms", ChatPhaseCallback.PHASE_ANSWER,
                                fullResponse.length(), System.currentTimeMillis() - phaseStartTime);
                    }
                } else {
                    // Phase 3: Evaluate results
                    phaseStartTime = System.currentTimeMillis();
                    callback.onPhaseStart(ChatPhaseCallback.PHASE_EVALUATE, "Evaluating relevance...");
                    final RelevanceEvaluationResult evalResult = llmClientManager.evaluateResults(userMessage, query, searchResults);
                    callback.onPhaseComplete(ChatPhaseCallback.PHASE_EVALUATE);

                    if (logger.isDebugEnabled()) {
                        logger.debug("[RAG] Phase {} completed. hasRelevant={}, relevantDocIds={}, phaseElapsedTime={}ms",
                                ChatPhaseCallback.PHASE_EVALUATE, evalResult.isHasRelevantResults(), evalResult.getRelevantDocIds(),
                                System.currentTimeMillis() - phaseStartTime);
                    }

                    if (!evalResult.isHasRelevantResults()) {
                        // No relevant results - generate fallback response
                        phaseStartTime = System.currentTimeMillis();
                        callback.onPhaseStart(ChatPhaseCallback.PHASE_ANSWER, "Generating response...");
                        llmClientManager.generateNoResultsResponse(userMessage, history, (chunk, done) -> {
                            fullResponse.append(chunk);
                            callback.onChunk(chunk, done);
                        });
                        callback.onPhaseComplete(ChatPhaseCallback.PHASE_ANSWER);
                        if (logger.isDebugEnabled()) {
                            logger.debug("[RAG] Phase {} completed. responseLength={}, phaseElapsedTime={}ms",
                                    ChatPhaseCallback.PHASE_ANSWER, fullResponse.length(), System.currentTimeMillis() - phaseStartTime);
                        }
                    } else {
                        // Phase 4: Fetch full content
                        phaseStartTime = System.currentTimeMillis();
                        callback.onPhaseStart(ChatPhaseCallback.PHASE_FETCH, "Retrieving document content...");
                        final List<Map<String, Object>> fullDocs = fetchFullContent(evalResult.getRelevantDocIds());
                        callback.onPhaseComplete(ChatPhaseCallback.PHASE_FETCH);
                        sources = fullDocs;

                        if (logger.isDebugEnabled()) {
                            logger.debug("[RAG] Phase {} completed. docIds={}, fetchedCount={}, phaseElapsedTime={}ms",
                                    ChatPhaseCallback.PHASE_FETCH, evalResult.getRelevantDocIds(), fullDocs.size(),
                                    System.currentTimeMillis() - phaseStartTime);
                        }

                        // Phase 5: Generate answer
                        phaseStartTime = System.currentTimeMillis();
                        callback.onPhaseStart(ChatPhaseCallback.PHASE_ANSWER, "Generating response...");
                        final LlmStreamCallback answerCallback = (chunk, done) -> {
                            fullResponse.append(chunk);
                            callback.onChunk(chunk, done);
                        };
                        if (intentResult.getIntent() == ChatIntent.FAQ) {
                            llmClientManager.generateFaqAnswerResponse(userMessage, fullDocs, history, answerCallback);
                        } else {
                            llmClientManager.streamGenerateAnswer(userMessage, fullDocs, history, answerCallback);
                        }
                        callback.onPhaseComplete(ChatPhaseCallback.PHASE_ANSWER);
                        if (logger.isDebugEnabled()) {
                            logger.debug("[RAG] Phase {} completed. responseLength={}, sourceCount={}, phaseElapsedTime={}ms",
                                    ChatPhaseCallback.PHASE_ANSWER, fullResponse.length(), fullDocs.size(),
                                    System.currentTimeMillis() - phaseStartTime);
                        }
                    }
                }
            }

            // Phase 6: Render markdown to safe HTML
            final long renderStartTime = System.currentTimeMillis();
            final String htmlContent = renderMarkdownToHtml(fullResponse.toString());
            if (logger.isDebugEnabled()) {
                logger.debug("[RAG] Markdown rendering completed. markdownLength={}, htmlLength={}, renderElapsedTime={}ms",
                        fullResponse.length(), htmlContent.length(), System.currentTimeMillis() - renderStartTime);
            }

            // Create and save assistant message (user message was already added at the start)
            final ChatMessage assistantMessage = ChatMessage.assistantMessage(fullResponse.toString());
            assistantMessage.setHtmlContent(htmlContent);

            for (int i = 0; i < sources.size(); i++) {
                assistantMessage.addSource(new ChatSource(i + 1, sources.get(i)));
            }

            session.addMessage(assistantMessage);

            if (logger.isDebugEnabled()) {
                logger.debug("[RAG] Enhanced chat request completed. sessionId={}, sourcesCount={}, responseLength={}, elapsedTime={}ms",
                        session.getSessionId(), sources.size(), fullResponse.length(), System.currentTimeMillis() - startTime);
            }

            return new ChatResult(session.getSessionId(), assistantMessage, sources);

        } catch (final LlmException e) {
            logger.warn("LLM error during enhanced chat. sessionId={}, errorCode={}, error={}, elapsedTime={}ms", session.getSessionId(),
                    e.getErrorCode(), e.getMessage(), System.currentTimeMillis() - startTime, e);
            callback.onError("llm", e.getErrorCode());
            throw e;
        } catch (final Exception e) {
            logger.warn("Error during enhanced chat. sessionId={}, error={}, elapsedTime={}ms", session.getSessionId(), e.getMessage(),
                    System.currentTimeMillis() - startTime, e);
            callback.onError("unknown", LlmException.ERROR_UNKNOWN);
            throw e;
        } finally {
            session.trimHistory(getMaxHistoryMessages());
        }
    }

    /**
     * Extracts conversation history from a chat session as LlmMessage list.
     * The assistant message content in history is controlled by the
     * {@code rag.chat.history.assistant.content} configuration property.
     *
     * @param session the chat session
     * @return the list of LlmMessages representing the conversation history
     */
    protected List<LlmMessage> extractHistory(final ChatSession session) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String assistantContentMode = fessConfig.getOrDefault("rag.chat.history.assistant.content", "source_titles");

        final List<LlmMessage> history = new ArrayList<>();
        for (final ChatMessage msg : session.getMessages()) {
            if (msg.isUser()) {
                history.add(LlmMessage.user(msg.getContent()));
            } else if (msg.isAssistant()) {
                final String content = buildAssistantHistoryContent(msg, assistantContentMode);
                if (content != null) {
                    history.add(LlmMessage.assistant(content));
                }
            }
        }
        return history;
    }

    /**
     * Builds the assistant message content for history based on the specified mode.
     *
     * @param msg the assistant chat message
     * @param mode the content mode (full, source_titles, source_titles_and_urls, truncated, none)
     * @return the content string for history, or null if the message should be excluded
     */
    protected String buildAssistantHistoryContent(final ChatMessage msg, final String mode) {
        switch (mode) {
        case "full":
            return msg.getContent();
        case "source_titles":
            return buildSourceTitlesContent(msg);
        case "source_titles_and_urls":
            return buildSourceTitlesAndUrlsContent(msg);
        case "truncated":
            return buildTruncatedContent(msg);
        case "none":
            return null;
        default:
            return msg.getContent();
        }
    }

    /**
     * Builds a summary string from source document titles.
     *
     * @param msg the assistant chat message
     * @return a string listing referenced document titles
     */
    protected String buildSourceTitlesContent(final ChatMessage msg) {
        final List<ChatSource> sources = msg.getSources();
        if (sources == null || sources.isEmpty()) {
            return buildTruncatedContent(msg);
        }
        final String titles =
                sources.stream().map(ChatSource::getTitle).filter(t -> t != null && !t.isEmpty()).collect(Collectors.joining(", "));
        if (titles.isEmpty()) {
            return buildTruncatedContent(msg);
        }
        final String sourceSuffix = "\n[Referenced documents: " + titles + "]";
        final String content = msg.getContent();
        if (content == null || content.isEmpty()) {
            return sourceSuffix;
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int maxChars = Integer.parseInt(fessConfig.getOrDefault("rag.chat.history.assistant.summary.max.chars", "500"));
        if (content.length() <= maxChars) {
            return content + sourceSuffix;
        }
        return content.substring(0, maxChars) + "... [truncated]" + sourceSuffix;
    }

    /**
     * Builds a summary string from source document titles and URLs.
     *
     * @param msg the assistant chat message
     * @return a string listing referenced document titles and URLs
     */
    protected String buildSourceTitlesAndUrlsContent(final ChatMessage msg) {
        final List<ChatSource> sources = msg.getSources();
        if (sources == null || sources.isEmpty()) {
            return msg.getContent();
        }
        final String refs = sources.stream().map(s -> {
            final String title = s.getTitle();
            final String url = s.getUrl();
            if (title != null && !title.isEmpty() && url != null && !url.isEmpty()) {
                return title + " (" + url + ")";
            } else if (title != null && !title.isEmpty()) {
                return title;
            } else if (url != null && !url.isEmpty()) {
                return url;
            }
            return null;
        }).filter(s -> s != null).collect(Collectors.joining(", "));
        if (refs.isEmpty()) {
            return msg.getContent();
        }
        return "[References: " + refs + "]";
    }

    /**
     * Builds a truncated version of the assistant message content.
     * The maximum length is controlled by {@code rag.chat.history.assistant.max.chars}.
     *
     * @param msg the assistant chat message
     * @return the truncated content
     */
    protected String buildTruncatedContent(final ChatMessage msg) {
        final String content = msg.getContent();
        if (content == null) {
            return null;
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int maxChars = Integer.parseInt(fessConfig.getOrDefault("rag.chat.history.assistant.max.chars", "500"));
        if (content.length() <= maxChars) {
            return content;
        }
        return content.substring(0, maxChars) + "...";
    }

    private static final int MAX_QUERY_LENGTH = 1000;

    private static final Pattern DANGEROUS_QUERY_PATTERN = Pattern.compile("\\*:\\*");

    /**
     * Searches documents using a Fess query.
     *
     * @param query the Fess query string
     * @return the list of search result documents
     */
    protected List<Map<String, Object>> searchWithQuery(final String query) {
        if (StringUtil.isBlank(query)) {
            return Collections.emptyList();
        }

        if (query.length() > MAX_QUERY_LENGTH) {
            logger.warn("[RAG] Rejected LLM-generated query exceeding max length. length={}", query.length());
            return Collections.emptyList();
        }

        if (DANGEROUS_QUERY_PATTERN.matcher(query).find()) {
            logger.warn("[RAG] Rejected LLM-generated query with dangerous pattern. query={}", query);
            return Collections.emptyList();
        }

        return searchDocuments(query);
    }

    /**
     * Fetches full document content for the given document IDs.
     *
     * @param docIds the document IDs to fetch
     * @return list of documents with full content
     */
    protected List<Map<String, Object>> fetchFullContent(final List<String> docIds) {
        if (docIds.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("[RAG] Fetch full content called with empty docIds.");
            }
            return Collections.emptyList();
        }

        final long startTime = System.currentTimeMillis();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String[] fields = fessConfig.getRagChatContentFields().split(",");

        if (logger.isDebugEnabled()) {
            logger.debug("[RAG] Fetching full content. docIds={}, fields={}", docIds, String.join(",", fields));
        }

        try {
            final List<Map<String, Object>> results = ComponentUtil.getSearchHelper()
                    .getDocumentListByDocIds(docIds.toArray(new String[0]), fields, OptionalThing.empty(),
                            SearchRequestParams.SearchRequestType.JSON);
            if (logger.isDebugEnabled()) {
                logger.debug("[RAG] Full content fetched. docIdCount={}, fetchedCount={}, elapsedTime={}ms", docIds.size(), results.size(),
                        System.currentTimeMillis() - startTime);
            }
            return results;
        } catch (final Exception e) {
            logger.warn("Failed to fetch full content for docIds={}. error={}, elapsedTime={}ms", docIds, e.getMessage(),
                    System.currentTimeMillis() - startTime);
            return Collections.emptyList();
        }
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
            final SearchRenderData data = new SearchRenderData();
            final ChatSearchRequestParams params =
                    new ChatSearchRequestParams("url:\"" + escapeQueryValue(url) + "\"", maxDocs, fessConfig);

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
     * Escapes special characters in the value for use in Fess queries.
     *
     * @param value the value to escape
     * @return the escaped value
     */
    protected String escapeQueryValue(final String value) {
        if (value == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(value.length() + 16);
        for (int i = 0; i < value.length(); i++) {
            final char c = value.charAt(i);
            if (c == '\0') {
                continue; // Skip NULL characters
            }
            if (c == '\\' || c == '"') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
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
     * SearchHelper applies role-based access control filtering through
     * SearchRequestType.JSON and the role filter mechanism, ensuring
     * users only see documents they are authorized to access.
     *
     * @param query the search query
     * @return a list of documents matching the query
     */
    protected List<Map<String, Object>> searchDocuments(final String query) {
        final long startTime = System.currentTimeMillis();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int maxDocs = fessConfig.getRagChatContextMaxDocumentsAsInteger();

        if (logger.isDebugEnabled()) {
            logger.debug("[RAG] Starting document search. query={}, maxDocs={}", query, maxDocs);
        }

        try {
            final SearchRenderData data = new SearchRenderData();
            final ChatSearchRequestParams params = new ChatSearchRequestParams(query, maxDocs, fessConfig);

            ComponentUtil.getSearchHelper().search(params, data, OptionalThing.empty());

            @SuppressWarnings("unchecked")
            final List<Map<String, Object>> docs = (List<Map<String, Object>>) data.getDocumentItems();
            if (docs != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("[RAG] Document search completed. query={}, resultCount={}, elapsedTime={}ms", query, docs.size(),
                            System.currentTimeMillis() - startTime);
                }
                return docs;
            }
        } catch (final Exception e) {
            logger.warn("Failed to search documents for RAG: query={}, elapsedTime={}ms", query, System.currentTimeMillis() - startTime, e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("[RAG] Document search returned no results. query={}, elapsedTime={}ms", query,
                    System.currentTimeMillis() - startTime);
        }
        return new ArrayList<>();
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
            return new HighlightInfo().fragmentSize(Integer.parseInt(fessConfig.getOrDefault("rag.chat.highlight.fragment.size", "500")))
                    .numOfFragments(Integer.parseInt(fessConfig.getOrDefault("rag.chat.highlight.number.of.fragments", "3")))
                    .preTags(StringUtil.EMPTY)
                    .postTags(StringUtil.EMPTY);
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
