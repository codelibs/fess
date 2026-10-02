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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
import org.codelibs.fess.llm.LlmClient;
import org.codelibs.fess.llm.LlmClientManager;
import org.codelibs.fess.llm.LlmException;
import org.codelibs.fess.llm.LlmMessage;
import org.codelibs.fess.llm.LlmStreamCallback;
import org.codelibs.fess.llm.RelevanceEvaluationResult;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.util.LaRequestUtil;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

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

    /** Smallest part size, in characters, a long document is split into. */
    protected static final int MIN_DOCUMENT_PART_CHARS = 256;

    /** Characters reserved in the summary context budget for the document header, labels and escaping. */
    protected static final int DOCUMENT_PROMPT_OVERHEAD_CHARS = 128;

    /** Characters reserved per part summary for its {@code Part i/N:} header and separator. */
    protected static final int PART_SUMMARY_HEADER_CHARS = 16;

    /** Query regeneration reason: the search returned no documents. */
    protected static final String REASON_NO_RESULTS = "no_results";

    /** Query regeneration reason: the search returned documents but none was judged relevant. */
    protected static final String REASON_NO_RELEVANT_RESULTS = "no_relevant_results";

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
        if (logger.isTraceEnabled()) {
            logger.trace("[RAG] ChatClient availability check. available={}", available);
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
        return chat(sessionId, userMessage, userId, Collections.emptyMap(), new String[0]);
    }

    /**
     * Performs a chat request with RAG and search filters.
     *
     * @param sessionId the session ID (can be null for new sessions)
     * @param userMessage the user's message
     * @param userId the user ID (can be null for anonymous users)
     * @param fields the field filters (e.g., label)
     * @param extraQueries the extra query filters (e.g., filetype, timestamp)
     * @return the chat response including session info and sources
     */
    public ChatResult chat(final String sessionId, final String userMessage, final String userId, final Map<String, String[]> fields,
            final String[] extraQueries) {
        final Map<String, String[]> safeFields = fields != null ? fields : Collections.emptyMap();
        final String[] safeExtraQueries = extraQueries != null ? extraQueries : new String[0];
        final long startTime = System.currentTimeMillis();
        final String contextPath = resolveContextPath();
        if (logger.isDebugEnabled()) {
            logger.debug("[RAG] Starting chat request. sessionId={}, userId={}, userMessage={}", sessionId, userId, userMessage);
        }

        final ChatSession session = chatSessionManager.getOrCreateSession(sessionId, userId);
        // Extract phase-specific history snapshots before adding current user message.
        final List<LlmMessage> historyForIntent = extractHistoryForIntent(session);
        final List<LlmMessage> historyForAnswer = extractHistoryForAnswer(session);
        // Add user message immediately for session integrity under concurrent access
        final ChatMessage userChatMessage = ChatMessage.userMessage(userMessage);
        session.addMessage(userChatMessage);

        try {
            String finalSearchQuery = null;
            // Intent detection
            final IntentDetectionResult intentResult = llmClientManager.detectIntent(userMessage, historyForIntent);
            if (logger.isDebugEnabled()) {
                logger.debug("[RAG] Intent detected. intent={}, query={}", intentResult.getIntent(), intentResult.getQuery());
            }

            if (intentResult.getIntent() == ChatIntent.UNCLEAR) {
                // Unclear intent - generate answer with empty documents to ask for clarification
                final LlmChatResponse llmResponse = llmClientManager.generateAnswer(userMessage, Collections.emptyList(), historyForAnswer);
                final ChatMessage assistantMessage = ChatMessage.assistantMessage(llmResponse.getContent());
                session.addMessage(assistantMessage);
                logger.info("[RAG] Chat completed (unclear). sessionId={}, elapsedTime={}ms", session.getSessionId(),
                        System.currentTimeMillis() - startTime);
                return new ChatResult(session.getSessionId(), assistantMessage, Collections.emptyList());
            }

            // For SUMMARY intent, search by URL; for SEARCH/FAQ, search with query
            ChatSearchResult searchResult;
            if (intentResult.getIntent() == ChatIntent.SUMMARY && StringUtil.isNotBlank(intentResult.getDocumentUrl())) {
                searchResult = searchByUrl(intentResult.getDocumentUrl());
            } else {
                String query = StringUtil.isBlank(intentResult.getQuery()) ? userMessage : intentResult.getQuery();
                final Set<String> triedQueries = newTriedQueries(query);
                searchResult = searchWithQueryAndMetadata(query, safeFields, safeExtraQueries);
                finalSearchQuery = query;

                // Refine the query while the search finds nothing, up to the regeneration limit.
                // This path does not evaluate relevance, so only a search with no hits is retried.
                final int maxRegenerations = getMaxQueryRegenerations();
                int regenerations = 0;
                while (searchResult.getDocuments().isEmpty() && regenerations < maxRegenerations) {
                    regenerations++;
                    logger.info("[RAG] Search returned 0 results, regenerating query. query={}, attempt={}", query, regenerations);
                    final String newQuery = regenerateUntriedQuery(userMessage, query, REASON_NO_RESULTS, historyForIntent, triedQueries);
                    if (newQuery == null) {
                        break;
                    }
                    searchResult = searchWithQueryAndMetadata(newQuery, safeFields, safeExtraQueries);
                    query = newQuery;
                    finalSearchQuery = newQuery;
                }
            }

            final List<Map<String, Object>> searchResults = searchResult.getDocuments();
            final List<Map<String, Object>> answerDocs = fetchContentForAnswer(searchResults, finalSearchQuery);
            final LlmChatResponse llmResponse = llmClientManager.generateAnswer(userMessage, answerDocs, historyForAnswer);

            final ChatMessage assistantMessage = ChatMessage.assistantMessage(llmResponse.getContent());
            // The sources are built from the SEARCH-phase maps, not from answerDocs: content_title
            // and content_description do not exist in the index (see fess_indices/fess/doc.json) --
            // they are injected at render time by the rank-fusion searcher via
            // ViewHelper#getContentDescription. fetchContentForAnswer re-reads documents through
            // SearchHelper#getDocumentListByDocIds, a pure _source projection, so those keys can
            // never come back and sources[].snippet would silently vanish from the API response.
            // answerDocs stays the LLM answer context (that is the whole point of the fetch).
            for (final Map<String, Object> doc : searchResults) {
                populateUrlLink(doc);
            }
            addSourcesToMessage(assistantMessage, searchResults, contextPath, searchResult.getQueryId(), searchResult.getRequestedTime());
            assistantMessage.setSearchQuery(finalSearchQuery);

            session.addMessage(assistantMessage);

            logger.info("[RAG] Chat completed. sessionId={}, intent={}, sourcesCount={}, answerDocsCount={}, elapsedTime={}ms",
                    session.getSessionId(), intentResult.getIntent(), searchResults.size(), answerDocs.size(),
                    System.currentTimeMillis() - startTime);

            return new ChatResult(session.getSessionId(), assistantMessage, searchResults);
        } catch (final Exception e) {
            if (e instanceof LlmException) {
                logger.warn("[RAG] LLM error during chat. sessionId={}, error={}", session.getSessionId(), e.getMessage());
            } else {
                logger.warn("[RAG] Unexpected error during chat. sessionId={}, error={}", session.getSessionId(), e.getMessage(), e);
            }
            throw e;
        } finally {
            session.trimHistory(getMaxHistoryMessages());
        }
    }

    /**
     * Performs an enhanced streaming chat request with multi-phase RAG flow.
     *
     * @param sessionId the session ID (can be null for new sessions)
     * @param userMessage the user's message
     * @param userId the user ID (can be null for anonymous users)
     * @param callback the callback to receive phase notifications and streaming chunks
     * @return the chat result with session info, sources, and HTML content
     */
    public ChatResult streamChatEnhanced(final String sessionId, final String userMessage, final String userId,
            final ChatPhaseCallback callback) {
        return streamChatEnhanced(sessionId, userMessage, userId, Collections.emptyMap(), new String[0], callback);
    }

    /**
     * Performs an enhanced streaming chat request with multi-phase RAG flow and search filters.
     * This flow includes: intent detection, keyword search, result evaluation,
     * content retrieval, answer generation, and markdown rendering.
     *
     * @param sessionId the session ID (can be null for new sessions)
     * @param userMessage the user's message
     * @param userId the user ID (can be null for anonymous users)
     * @param fields the field filters (e.g., label)
     * @param extraQueries the extra query filters (e.g., filetype, timestamp)
     * @param callback the callback to receive phase notifications and streaming chunks
     * @return the chat result with session info, sources, and HTML content
     */
    public ChatResult streamChatEnhanced(final String sessionId, final String userMessage, final String userId,
            final Map<String, String[]> fields, final String[] extraQueries, final ChatPhaseCallback callback) {
        final Map<String, String[]> safeFields = fields != null ? fields : Collections.emptyMap();
        final String[] safeExtraQueries = extraQueries != null ? extraQueries : new String[0];
        final long startTime = System.currentTimeMillis();
        // Capture context path early before request context may become unavailable during SSE processing
        final String contextPath = resolveContextPath();
        // Note: Locale is resolved via LaRequestUtil in LlmClient. During long SSE processing,
        // the request context may become unavailable, falling back to Locale.getDefault().
        if (logger.isDebugEnabled()) {
            logger.debug("[RAG] Starting enhanced streaming chat request. sessionId={}, userId={}, userMessage={}", sessionId, userId,
                    userMessage);
        }

        final ChatSession session = chatSessionManager.getOrCreateSession(sessionId, userId);
        // Extract phase-specific history snapshots before adding current user message.
        // Intent phase uses minimal context; answer phase pairs user/assistant turns.
        final List<LlmMessage> historyForIntent = extractHistoryForIntent(session);
        final List<LlmMessage> historyForAnswer = extractHistoryForAnswer(session);
        // Add user message immediately for session integrity under concurrent access
        final ChatMessage userChatMessage = ChatMessage.userMessage(userMessage);
        session.addMessage(userChatMessage);
        final StringBuilder fullResponse = new StringBuilder();
        List<Map<String, Object>> sources = new ArrayList<>();
        String searchQueryId = null;
        long searchRequestedTime = 0L;
        String finalSearchQuery = null;

        try {
            // Phase 1: Intent Detection
            long phaseStartTime = System.currentTimeMillis();
            callback.onPhaseStart(ChatPhaseCallback.PHASE_INTENT, "Analyzing your question...");
            final IntentDetectionResult intentResult = llmClientManager.detectIntent(userMessage, historyForIntent);
            if (intentResult.isFallback()) {
                callback.onWarning(ChatPhaseCallback.PHASE_INTENT, "reasoning_token_exhausted", "search");
            }
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
                final LlmStreamCallback rawUnclearCallback = (chunk, done) -> {
                    fullResponse.append(chunk);
                    callback.onChunk(chunk, done);
                };
                final LlmStreamCallback unclearCallback =
                        new PhaseAwareStreamCallback(ChatPhaseCallback.PHASE_ANSWER, callback, rawUnclearCallback);
                llmClientManager.generateUnclearIntentResponse(userMessage, historyForAnswer, unclearCallback);
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
                final ChatSearchResult urlSearchResult = searchByUrl(documentUrl);
                final List<Map<String, Object>> urlResults = urlSearchResult.getDocuments();
                searchQueryId = urlSearchResult.getQueryId();
                searchRequestedTime = urlSearchResult.getRequestedTime();
                callback.onPhaseComplete(ChatPhaseCallback.PHASE_SEARCH, Map.of("hitCount", urlResults.size()));
                if (logger.isDebugEnabled()) {
                    logger.debug("[RAG] Phase {} completed. documentUrl={}, resultCount={}, phaseElapsedTime={}ms",
                            ChatPhaseCallback.PHASE_SEARCH, documentUrl, urlResults.size(), System.currentTimeMillis() - phaseStartTime);
                }

                if (urlResults.isEmpty()) {
                    // URL not found - inform user
                    phaseStartTime = System.currentTimeMillis();
                    callback.onPhaseStart(ChatPhaseCallback.PHASE_ANSWER, "Generating response...");
                    final LlmStreamCallback rawDocNotFoundCallback = (chunk, done) -> {
                        fullResponse.append(chunk);
                        callback.onChunk(chunk, done);
                    };
                    final LlmStreamCallback docNotFoundCallback =
                            new PhaseAwareStreamCallback(ChatPhaseCallback.PHASE_ANSWER, callback, rawDocNotFoundCallback);
                    llmClientManager.generateDocumentNotFoundResponse(userMessage, documentUrl, historyForAnswer, docNotFoundCallback);
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
                    // fullDocs stays the LLM context; the sources are resolved back to the
                    // search-phase maps, which alone carry content_description/content_title.
                    sources = resolveSourcesFromSearchResults(fullDocs, urlResults);
                    if (logger.isDebugEnabled()) {
                        logger.debug("[RAG] Phase {} completed. docIds={}, fetchedCount={}, phaseElapsedTime={}ms",
                                ChatPhaseCallback.PHASE_FETCH, docIds, fullDocs.size(), System.currentTimeMillis() - phaseStartTime);
                    }

                    phaseStartTime = System.currentTimeMillis();
                    callback.onPhaseStart(ChatPhaseCallback.PHASE_ANSWER, "Generating summary...");
                    final LlmStreamCallback rawSummaryCallback = (chunk, done) -> {
                        fullResponse.append(chunk);
                        callback.onChunk(chunk, done);
                    };
                    final LlmStreamCallback summaryCallback =
                            new PhaseAwareStreamCallback(ChatPhaseCallback.PHASE_ANSWER, callback, rawSummaryCallback);
                    llmClientManager.generateSummaryResponse(userMessage, fullDocs, historyForAnswer, summaryCallback);
                }
                callback.onPhaseComplete(ChatPhaseCallback.PHASE_ANSWER);
                if (logger.isDebugEnabled()) {
                    logger.debug("[RAG] Phase {} completed. responseLength={}, phaseElapsedTime={}ms", ChatPhaseCallback.PHASE_ANSWER,
                            fullResponse.length(), System.currentTimeMillis() - phaseStartTime);
                }
            } else {
                // Phase 2-3: Search, then evaluate the hits. While the search finds nothing or the
                // model judges none of the hits relevant, the query is regenerated and the search and
                // evaluation run again, up to the regeneration limit. Each round evaluates its own
                // query's hits; a regenerated query that was already tried ends the loop.
                //
                // The model judges every hit from the passages the answer would be generated from --
                // the fetcher's chunk-selected or highlighted content, the same answer context the
                // non-streaming chat uses -- not from the search-result content_description. For a hit
                // found only by the vector branch that description is the opening of the page, so a
                // document whose answer sits in a later chunk was always judged irrelevant and the
                // stream ended without sources.
                String query = StringUtil.isBlank(intentResult.getQuery()) ? userMessage : intentResult.getQuery();
                final Set<String> triedQueries = newTriedQueries(query);
                final int maxRegenerations = getMaxQueryRegenerations();
                int regenerations = 0;
                List<Map<String, Object>> searchResults;
                List<Map<String, Object>> candidateDocs = Collections.emptyList();
                RelevanceEvaluationResult evalResult = RelevanceEvaluationResult.noRelevantResults();
                String searchPhaseMessage = "Searching documents...";
                while (true) {
                    finalSearchQuery = query;
                    phaseStartTime = System.currentTimeMillis();
                    callback.onPhaseStart(ChatPhaseCallback.PHASE_SEARCH, searchPhaseMessage, query);
                    final ChatSearchResult querySearchResult = searchWithQueryAndMetadata(query, safeFields, safeExtraQueries);
                    searchResults = querySearchResult.getDocuments();
                    searchQueryId = querySearchResult.getQueryId();
                    searchRequestedTime = querySearchResult.getRequestedTime();
                    callback.onPhaseComplete(ChatPhaseCallback.PHASE_SEARCH, Map.of("hitCount", searchResults.size()));

                    logger.info("[RAG] Search completed. query={}, resultCount={}, elapsedTime={}ms", query, searchResults.size(),
                            System.currentTimeMillis() - phaseStartTime);
                    if (logger.isDebugEnabled()) {
                        logger.debug("[RAG] Phase {} completed. query={}, resultCount={}, phaseElapsedTime={}ms",
                                ChatPhaseCallback.PHASE_SEARCH, query, searchResults.size(), System.currentTimeMillis() - phaseStartTime);
                    }

                    final String failureReason;
                    if (searchResults.isEmpty()) {
                        candidateDocs = Collections.emptyList();
                        evalResult = RelevanceEvaluationResult.noRelevantResults();
                        failureReason = REASON_NO_RESULTS;
                    } else {
                        phaseStartTime = System.currentTimeMillis();
                        callback.onPhaseStart(ChatPhaseCallback.PHASE_EVALUATE, "Evaluating relevance...");
                        candidateDocs = fetchContentForAnswer(searchResults, query);
                        evalResult = llmClientManager.evaluateResults(userMessage, query, candidateDocs);
                        callback.onPhaseComplete(ChatPhaseCallback.PHASE_EVALUATE);

                        if (logger.isDebugEnabled()) {
                            logger.debug("[RAG] Phase {} completed. hasRelevant={}, relevantDocIds={}, phaseElapsedTime={}ms",
                                    ChatPhaseCallback.PHASE_EVALUATE, evalResult.isHasRelevantResults(), evalResult.getRelevantDocIds(),
                                    System.currentTimeMillis() - phaseStartTime);
                        }
                        if (evalResult.isHasRelevantResults()) {
                            break;
                        }
                        failureReason = REASON_NO_RELEVANT_RESULTS;
                    }

                    if (regenerations >= maxRegenerations) {
                        if (maxRegenerations > 0) {
                            logger.info("[RAG] Query regeneration limit reached. query={}, reason={}, regenerations={}", query,
                                    failureReason, regenerations);
                        }
                        break;
                    }
                    regenerations++;
                    logger.info("[RAG] Regenerating query. query={}, reason={}, attempt={}", query, failureReason, regenerations);
                    final String newQuery = regenerateUntriedQuery(userMessage, query, failureReason, historyForIntent, triedQueries);
                    if (newQuery == null) {
                        break;
                    }
                    callback.onFallback(ChatPhaseCallback.PHASE_SEARCH, failureReason, query, newQuery);
                    query = newQuery;
                    searchPhaseMessage = "Searching with refined query...";
                }

                if (!evalResult.isHasRelevantResults()) {
                    // Nothing relevant was found, even after refining the query
                    phaseStartTime = System.currentTimeMillis();
                    callback.onPhaseStart(ChatPhaseCallback.PHASE_ANSWER, "Generating response...");
                    final LlmStreamCallback rawNoResultsCallback = (chunk, done) -> {
                        fullResponse.append(chunk);
                        callback.onChunk(chunk, done);
                    };
                    final LlmStreamCallback noResultsCallback =
                            new PhaseAwareStreamCallback(ChatPhaseCallback.PHASE_ANSWER, callback, rawNoResultsCallback);
                    llmClientManager.generateNoResultsResponse(userMessage, historyForAnswer, noResultsCallback);
                    callback.onPhaseComplete(ChatPhaseCallback.PHASE_ANSWER);
                    if (logger.isDebugEnabled()) {
                        logger.debug("[RAG] Phase {} completed. responseLength={}, phaseElapsedTime={}ms", ChatPhaseCallback.PHASE_ANSWER,
                                fullResponse.length(), System.currentTimeMillis() - phaseStartTime);
                    }
                } else {
                    // Phase 4: Narrow the content fetched for the evaluation down to the relevant documents
                    phaseStartTime = System.currentTimeMillis();
                    callback.onPhaseStart(ChatPhaseCallback.PHASE_FETCH, "Retrieving document content...");
                    final List<Map<String, Object>> fullDocs = selectDocsByIds(candidateDocs, evalResult.getRelevantDocIds());
                    callback.onPhaseComplete(ChatPhaseCallback.PHASE_FETCH);
                    // fullDocs stays the LLM context; the sources are resolved back to the
                    // search-phase maps, which alone carry content_description/content_title.
                    sources = resolveSourcesFromSearchResults(fullDocs, searchResults);

                    if (logger.isDebugEnabled()) {
                        logger.debug("[RAG] Phase {} completed. docIds={}, fetchedCount={}, phaseElapsedTime={}ms",
                                ChatPhaseCallback.PHASE_FETCH, evalResult.getRelevantDocIds(), fullDocs.size(),
                                System.currentTimeMillis() - phaseStartTime);
                    }

                    // Phase 5: Generate answer
                    phaseStartTime = System.currentTimeMillis();
                    callback.onPhaseStart(ChatPhaseCallback.PHASE_ANSWER, "Generating response...");
                    final LlmStreamCallback rawAnswerCallback = (chunk, done) -> {
                        fullResponse.append(chunk);
                        callback.onChunk(chunk, done);
                    };
                    final LlmStreamCallback answerCallback =
                            new PhaseAwareStreamCallback(ChatPhaseCallback.PHASE_ANSWER, callback, rawAnswerCallback);
                    if (intentResult.getIntent() == ChatIntent.FAQ) {
                        llmClientManager.generateFaqAnswerResponse(userMessage, fullDocs, historyForAnswer, answerCallback);
                    } else {
                        llmClientManager.streamGenerateAnswer(userMessage, fullDocs, historyForAnswer, answerCallback);
                    }
                    callback.onPhaseComplete(ChatPhaseCallback.PHASE_ANSWER);
                    if (logger.isDebugEnabled()) {
                        logger.debug("[RAG] Phase {} completed. responseLength={}, sourceCount={}, phaseElapsedTime={}ms",
                                ChatPhaseCallback.PHASE_ANSWER, fullResponse.length(), fullDocs.size(),
                                System.currentTimeMillis() - phaseStartTime);
                    }
                }
            }

            // Phase 6: Render markdown to safe HTML and save the assistant message
            // (user message was already added at the start)
            final ChatMessage assistantMessage =
                    createAssistantMessage(fullResponse, sources, contextPath, searchQueryId, searchRequestedTime, finalSearchQuery);
            session.addMessage(assistantMessage);

            logger.info(
                    "[RAG] Enhanced chat completed. sessionId={}, userId={}, intent={}, sourcesCount={}, responseLength={}, elapsedTime={}ms",
                    session.getSessionId(), userId, intentResult.getIntent(), sources.size(), fullResponse.length(),
                    System.currentTimeMillis() - startTime);

            return new ChatResult(session.getSessionId(), assistantMessage, sources);

        } catch (final LlmException e) {
            logger.warn("[RAG] LLM error during enhanced chat. sessionId={}, errorCode={}, error={}, elapsedTime={}ms",
                    session.getSessionId(), e.getErrorCode(), e.getMessage(), System.currentTimeMillis() - startTime, e);
            callback.onError("llm", e.getErrorCode());
            throw e;
        } catch (final Exception e) {
            logger.warn("[RAG] Unexpected error during enhanced chat. sessionId={}, error={}, elapsedTime={}ms", session.getSessionId(),
                    e.getMessage(), System.currentTimeMillis() - startTime, e);
            callback.onError("unknown", LlmException.ERROR_UNKNOWN);
            throw e;
        } finally {
            session.trimHistory(getMaxHistoryMessages());
        }
    }

    /**
     * Performs a chat request about a single document, returning the whole answer at once.
     * Intent detection, search and relevance evaluation are skipped: the answer is generated
     * from the content of the document identified by {@code docId} only.
     *
     * @param sessionId the session ID (can be null for new sessions)
     * @param userMessage the user's message
     * @param userId the user ID (can be null for anonymous users)
     * @param docId the document ID to chat about
     * @return the chat result with session info and the document as the only source
     */
    public ChatResult chatAboutDocument(final String sessionId, final String userMessage, final String userId, final String docId) {
        return streamChatAboutDocument(sessionId, userMessage, userId, docId, ChatPhaseCallback.noOp());
    }

    /**
     * Performs a streaming chat request about a single document.
     *
     * <p>The complete content of the document is fetched (no query-driven chunk selection and no
     * {@code rag.chat.content.fulltext.max.length} truncation). When it fits the summary context budget
     * of the LLM client, the answer is streamed from it directly. Otherwise the document is split into
     * parts, every part is summarized with a separate non-streaming LLM call, and the answer is streamed
     * from the combined part summaries. At most {@code rag.chat.document.max.parts} parts are used; when
     * the document needs more, a {@code document_truncated} warning is reported and only its leading
     * part is used.</p>
     *
     * <p>Phases reported: {@code fetch} (loading the document, then once per part while a long document
     * is summarized) and {@code answer}.</p>
     *
     * @param sessionId the session ID (can be null for new sessions)
     * @param userMessage the user's message
     * @param userId the user ID (can be null for anonymous users)
     * @param docId the document ID to chat about
     * @param callback the callback to receive phase notifications and streaming chunks
     * @return the chat result with session info and the document as the only source
     */
    public ChatResult streamChatAboutDocument(final String sessionId, final String userMessage, final String userId, final String docId,
            final ChatPhaseCallback callback) {
        final long startTime = System.currentTimeMillis();
        // Capture context path early before request context may become unavailable during SSE processing
        final String contextPath = resolveContextPath();
        if (logger.isDebugEnabled()) {
            logger.debug("[RAG] Starting document chat request. sessionId={}, userId={}, docId={}, userMessage={}", sessionId, userId,
                    docId, userMessage);
        }

        final ChatSession session = chatSessionManager.getOrCreateSession(sessionId, userId);
        final List<LlmMessage> historyForAnswer = extractHistoryForAnswer(session);
        // Add user message immediately for session integrity under concurrent access
        session.addMessage(ChatMessage.userMessage(userMessage));
        final StringBuilder fullResponse = new StringBuilder();

        try {
            long phaseStartTime = System.currentTimeMillis();
            callback.onPhaseStart(ChatPhaseCallback.PHASE_FETCH, "Retrieving document content...");
            final Map<String, Object> doc = fetchWholeDocument(docId);
            if (logger.isDebugEnabled()) {
                logger.debug("[RAG] Document fetched. docId={}, elapsedTime={}ms", docId, System.currentTimeMillis() - phaseStartTime);
            }
            // The source is a copy: the answer context below may replace the content of doc.
            final Map<String, Object> sourceDoc = new HashMap<>(doc);
            // Completes the fetch phase started above.
            final List<Map<String, Object>> answerDocs = buildDocumentAnswerContext(userMessage, doc, callback);

            phaseStartTime = System.currentTimeMillis();
            callback.onPhaseStart(ChatPhaseCallback.PHASE_ANSWER, "Generating response...");
            final LlmStreamCallback answerCallback =
                    new PhaseAwareStreamCallback(ChatPhaseCallback.PHASE_ANSWER, callback, (chunk, done) -> {
                        fullResponse.append(chunk);
                        callback.onChunk(chunk, done);
                    });
            llmClientManager.generateSummaryResponse(userMessage, answerDocs, historyForAnswer, answerCallback);
            callback.onPhaseComplete(ChatPhaseCallback.PHASE_ANSWER);
            if (logger.isDebugEnabled()) {
                logger.debug("[RAG] Phase {} completed. responseLength={}, phaseElapsedTime={}ms", ChatPhaseCallback.PHASE_ANSWER,
                        fullResponse.length(), System.currentTimeMillis() - phaseStartTime);
            }

            // go_url needs a search query id; a document chat has none.
            final List<Map<String, Object>> sources = new ArrayList<>(Collections.singletonList(sourceDoc));
            final ChatMessage assistantMessage = createAssistantMessage(fullResponse, sources, contextPath, null, 0L, null);
            session.addMessage(assistantMessage);

            logger.info("[RAG] Document chat completed. sessionId={}, userId={}, docId={}, responseLength={}, elapsedTime={}ms",
                    session.getSessionId(), userId, docId, fullResponse.length(), System.currentTimeMillis() - startTime);

            return new ChatResult(session.getSessionId(), assistantMessage, sources);
        } catch (final LlmException e) {
            logger.warn("[RAG] LLM error during document chat. sessionId={}, docId={}, errorCode={}, error={}, elapsedTime={}ms",
                    session.getSessionId(), docId, e.getErrorCode(), e.getMessage(), System.currentTimeMillis() - startTime, e);
            callback.onError("llm", e.getErrorCode());
            throw e;
        } catch (final Exception e) {
            logger.warn("[RAG] Unexpected error during document chat. sessionId={}, docId={}, error={}, elapsedTime={}ms",
                    session.getSessionId(), docId, e.getMessage(), System.currentTimeMillis() - startTime, e);
            callback.onError("unknown", LlmException.ERROR_UNKNOWN);
            throw e;
        } finally {
            session.trimHistory(getMaxHistoryMessages());
        }
    }

    /**
     * Fetches the complete content of a document for a document chat, through the same field set as
     * {@link ChatContentFetcher} uses but without any query-driven selection or truncation. The
     * lookup is role-filtered, so a document the caller may not see is reported as missing.
     *
     * @param docId the document ID
     * @return the document map; {@code content} is a list of chunk texts for a chunked document and
     *         a string otherwise
     * @throws IllegalStateException if the document could not be fetched
     */
    protected Map<String, Object> fetchWholeDocument(final String docId) {
        return ComponentUtil.getChatContentFetcher()
                .fetchWholeDocument(docId)
                .orElseThrow(() -> new IllegalStateException("Document not found. docId=" + docId));
    }

    /**
     * Builds the document list the final answer is generated from. A document that fits the summary
     * context budget of the LLM client is passed on as it is. A longer one is split into parts,
     * every part is summarized by its own LLM call (reported as one {@code fetch} phase per part), and the
     * combined summaries stand in for its content.
     *
     * <p>Completes the {@code fetch} phase the caller started for loading the document. For a long
     * document that completion carries {@code parts} (the number of parts to summarize), and each part's
     * completion carries {@code part} and {@code parts}, so that a client can show its own localized
     * progress.</p>
     *
     * @param userMessage the user's message the part summaries have to serve
     * @param doc the fetched document
     * @param callback the callback to receive progress and warning notifications
     * @return the documents to generate the answer from
     */
    protected List<Map<String, Object>> buildDocumentAnswerContext(final String userMessage, final Map<String, Object> doc,
            final ChatPhaseCallback callback) {
        final String title = doc.get("title") != null ? doc.get("title").toString() : null;
        final String url = doc.get("url") != null ? doc.get("url").toString() : null;
        final Object content = doc.get("content");
        final List<String> chunks = content instanceof final List<?> list ? list.stream().map(String::valueOf).toList() : null;
        final String text =
                chunks != null ? String.join(DocumentPartSplitter.CHUNK_SEPARATOR, chunks) : content != null ? content.toString() : "";

        final int partBudget = Math.max(MIN_DOCUMENT_PART_CHARS, llmClientManager.getSummaryContextMaxChars()
                - (title != null ? title.length() : 0) - (url != null ? url.length() : 0) - DOCUMENT_PROMPT_OVERHEAD_CHARS);
        if (text.length() <= partBudget) {
            callback.onPhaseComplete(ChatPhaseCallback.PHASE_FETCH);
            return Collections.singletonList(doc);
        }

        List<String> parts =
                chunks != null ? DocumentPartSplitter.splitChunks(chunks, partBudget) : DocumentPartSplitter.splitText(text, partBudget);
        if (parts.isEmpty()) {
            // Nothing but whitespace: there is nothing to summarize.
            callback.onPhaseComplete(ChatPhaseCallback.PHASE_FETCH);
            return Collections.singletonList(doc);
        }
        final int maxParts = getDocumentMaxParts();
        if (parts.size() > maxParts) {
            logger.warn("[RAG] Document is longer than the part limit; using the leading parts only. docId={}, parts={}, maxParts={}",
                    doc.get("doc_id"), parts.size(), maxParts);
            callback.onWarning(ChatPhaseCallback.PHASE_FETCH, "document_truncated", "only the leading part of the document was used");
            parts = parts.subList(0, maxParts);
        }

        final int partCount = parts.size();
        callback.onPhaseComplete(ChatPhaseCallback.PHASE_FETCH, Map.of("parts", partCount));
        // The combined summaries must fit the budget as well, or generateSummaryResponse would cut off the last ones.
        final int summaryMaxChars = Math.max(1, partBudget / partCount - PART_SUMMARY_HEADER_CHARS);
        final StringBuilder combined = new StringBuilder();
        for (int i = 0; i < partCount; i++) {
            final long partStartTime = System.currentTimeMillis();
            callback.onPhaseStart(ChatPhaseCallback.PHASE_FETCH, "Summarizing document part " + (i + 1) + " of " + partCount + "...");
            String summary =
                    llmClientManager.summarizeDocumentPart(userMessage, title, url, parts.get(i), i + 1, partCount, summaryMaxChars);
            if (summary == null) {
                summary = "";
            }
            callback.onPhaseComplete(ChatPhaseCallback.PHASE_FETCH, Map.of("part", i + 1, "parts", partCount));
            if (summary.length() > summaryMaxChars) {
                summary = summary.substring(0, summaryMaxChars) + "...";
            }
            if (combined.length() > 0) {
                combined.append("\n\n");
            }
            combined.append("Part ").append(i + 1).append('/').append(partCount).append(":\n").append(summary);
            if (logger.isDebugEnabled()) {
                logger.debug("[RAG] Document part summarized. docId={}, part={}/{}, partLength={}, summaryLength={}, elapsedTime={}ms",
                        doc.get("doc_id"), i + 1, partCount, parts.get(i).length(), summary.length(),
                        System.currentTimeMillis() - partStartTime);
            }
        }

        final Map<String, Object> reduced = new HashMap<>(doc);
        reduced.put("content", combined.toString());
        return Collections.singletonList(reduced);
    }

    /**
     * Gets the maximum number of parts a long document is split into for a document chat.
     *
     * @return the maximum number of parts (at least 1)
     */
    protected int getDocumentMaxParts() {
        return Math.max(1, ComponentUtil.getFessConfig().getRagChatDocumentMaxPartsAsInteger());
    }

    /**
     * Returns how many times a chat request may regenerate its search query when the search finds
     * nothing or nothing relevant, from {@code rag.chat.query.regeneration.max.count}.
     *
     * @return the maximum number of query regenerations; 0 disables regeneration
     */
    protected int getMaxQueryRegenerations() {
        return Math.max(0, ComponentUtil.getFessConfig().getRagChatQueryRegenerationMaxCountAsInteger());
    }

    /**
     * Creates the set of queries searched in one chat request, starting with the first query.
     * Queries are compared trimmed, as {@link #regenerateUntriedQuery} trims the regenerated ones.
     *
     * @param query the first search query
     * @return a mutable set holding the trimmed query
     */
    private static Set<String> newTriedQueries(final String query) {
        final Set<String> triedQueries = new HashSet<>();
        if (query != null) {
            triedQueries.add(query.trim());
        }
        return triedQueries;
    }

    /**
     * Asks the LLM for a new search query and accepts it only if it has not been tried in this request.
     * Searching a query again would only repeat its result, so a repeated, blank or failed regeneration
     * (the LLM client returns the failed query on error) ends the refinement.
     *
     * @param userMessage the user's message
     * @param failedQuery the query whose search failed
     * @param failureReason {@value #REASON_NO_RESULTS} or {@value #REASON_NO_RELEVANT_RESULTS}
     * @param history the conversation history for the intent phase
     * @param triedQueries the queries already searched in this request; the new query is added to it
     * @return the new query, or null if there is no untried query to search
     */
    protected String regenerateUntriedQuery(final String userMessage, final String failedQuery, final String failureReason,
            final List<LlmMessage> history, final Set<String> triedQueries) {
        final String newQuery = llmClientManager.regenerateQuery(userMessage, failedQuery, failureReason, history);
        if (StringUtil.isBlank(newQuery)) {
            logger.info("[RAG] Query regeneration returned no query. failedQuery={}, reason={}", failedQuery, failureReason);
            return null;
        }
        final String trimmedQuery = newQuery.trim();
        if (!triedQueries.add(trimmedQuery)) {
            logger.info("[RAG] Regenerated query was already searched. query={}, reason={}", trimmedQuery, failureReason);
            return null;
        }
        logger.info("[RAG] Regenerated query. newQuery={}, reason={}", trimmedQuery, failureReason);
        return trimmedQuery;
    }

    /**
     * Renders the streamed answer to HTML and builds the assistant message with its sources.
     *
     * @param fullResponse the complete markdown answer
     * @param sources the source documents
     * @param contextPath the application context path
     * @param queryId the query ID of the search the sources came from (null if there was none)
     * @param requestedTime the requested time of that search
     * @param searchQuery the final search query (null if there was none)
     * @return the assistant message, not yet added to a session
     */
    protected ChatMessage createAssistantMessage(final StringBuilder fullResponse, final List<Map<String, Object>> sources,
            final String contextPath, final String queryId, final long requestedTime, final String searchQuery) {
        final long renderStartTime = System.currentTimeMillis();
        final String htmlContent = renderMarkdownToHtml(fullResponse.toString());
        if (logger.isDebugEnabled()) {
            logger.debug("[RAG] Markdown rendering completed. markdownLength={}, htmlLength={}, renderElapsedTime={}ms",
                    fullResponse.length(), htmlContent.length(), System.currentTimeMillis() - renderStartTime);
        }

        final ChatMessage assistantMessage = ChatMessage.assistantMessage(fullResponse.toString());
        assistantMessage.setHtmlContent(htmlContent);

        for (final Map<String, Object> element : sources) {
            populateUrlLink(element);
        }
        addSourcesToMessage(assistantMessage, sources, contextPath, queryId, requestedTime);

        assistantMessage.setSearchQuery(searchQuery);
        return assistantMessage;
    }

    /**
     * Extracts conversation history shaped for the Intent Detection prompt.
     * For {@code smart_summary} mode each assistant turn is rendered as a single
     * {@code searched: "..." -> found: [...]} line. For other modes, this delegates
     * to the per-message {@link #buildAssistantHistoryContent} logic.
     *
     * @param session the chat session
     * @return the list of LlmMessages for Intent Detection
     */
    protected List<LlmMessage> extractHistoryForIntent(final ChatSession session) {
        return extractHistoryWithMode(session, /* forIntent */ true);
    }

    /**
     * Extracts conversation history shaped for the Answer Generation prompt.
     * For {@code smart_summary} mode each (user, assistant) turn is rendered as a single
     * {@code Q: "..." (searched: "...", refs: [...])} line. For other modes, this delegates
     * to the per-message {@link #buildAssistantHistoryContent} logic.
     *
     * @param session the chat session
     * @return the list of LlmMessages for Answer Generation
     */
    protected List<LlmMessage> extractHistoryForAnswer(final ChatSession session) {
        return extractHistoryWithMode(session, /* forIntent */ false);
    }

    private List<LlmMessage> extractHistoryWithMode(final ChatSession session, final boolean forIntent) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String assistantContentMode = fessConfig.getOrDefault("rag.chat.history.assistant.content", "smart_summary");

        if ("smart_summary".equals(assistantContentMode)) {
            return extractHistorySmartSummary(session, forIntent, getHistoryTitlesMaxCount(fessConfig));
        }

        // Other modes: per-message rendering, identical for both phases.
        final LlmClient client = llmClientManager != null ? llmClientManager.getClient() : null;
        final int assistantMaxChars = client != null ? client.getHistoryAssistantMaxChars() : 800;
        final int summaryMaxChars = client != null ? client.getHistoryAssistantSummaryMaxChars() : 800;
        final List<LlmMessage> history = new ArrayList<>();
        for (final ChatMessage msg : session.getMessages()) {
            if (msg.isUser()) {
                history.add(LlmMessage.user(msg.getContent()));
            } else if (msg.isAssistant()) {
                final String content = buildAssistantHistoryContent(msg, assistantContentMode, assistantMaxChars, summaryMaxChars);
                if (content != null) {
                    history.add(LlmMessage.assistant(content));
                }
            }
        }
        return history;
    }

    private List<LlmMessage> extractHistorySmartSummary(final ChatSession session, final boolean forIntent, final int titlesMaxCount) {
        final List<LlmMessage> history = new ArrayList<>();
        final List<ChatMessage> messages = session.getMessages();
        if (forIntent) {
            // Intent phase: include only completed (user, assistant) pairs; skip trailing user without response.
            String pendingUser = null;
            for (final ChatMessage msg : messages) {
                if (msg.isUser()) {
                    pendingUser = msg.getContent();
                } else if (msg.isAssistant()) {
                    if (pendingUser != null) {
                        history.add(LlmMessage.user(pendingUser));
                        pendingUser = null;
                    }
                    final String line = renderIntentHistoryTurn(msg, titlesMaxCount);
                    if (line != null) {
                        history.add(LlmMessage.assistant(line));
                    }
                }
            }
        } else {
            // Answer phase: pair each assistant with its preceding user, emit one rendered line per pair.
            String pendingUser = null;
            for (final ChatMessage msg : messages) {
                if (msg.isUser()) {
                    pendingUser = msg.getContent();
                } else if (msg.isAssistant()) {
                    if (pendingUser == null) {
                        // Orphan assistant — skip defensively.
                        continue;
                    }
                    final String line = renderAnswerHistoryTurn(pendingUser, msg, titlesMaxCount);
                    if (line != null) {
                        history.add(LlmMessage.assistant(line));
                    }
                    pendingUser = null;
                }
            }
        }
        return history;
    }

    private int getHistoryTitlesMaxCount(final FessConfig fessConfig) {
        final String value = fessConfig.getOrDefault("rag.chat.history.titles.max.count", "5");
        if (value == null) {
            return 5;
        }
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            return 5;
        }
    }

    /**
     * Builds the assistant message content for history based on the specified mode.
     *
     * @param msg the assistant chat message
     * @param mode the content mode (full, smart_summary, source_titles, source_titles_and_urls, truncated, none)
     * @param assistantMaxChars the maximum characters for truncated mode
     * @param summaryMaxChars the maximum characters for summary modes
     * @return the content string for history, or null if the message should be excluded
     */
    protected String buildAssistantHistoryContent(final ChatMessage msg, final String mode, final int assistantMaxChars,
            final int summaryMaxChars) {
        return switch (mode) {
        case "full" -> msg.getContent();
        case "smart_summary" -> // smart_summary is handled by extractHistoryForIntent / extractHistoryForAnswer;
                // it must never reach this per-message renderer.
                throw new IllegalStateException("smart_summary mode is not handled per-message");
        case "source_titles" -> buildSourceTitlesContent(msg, summaryMaxChars);
        case "source_titles_and_urls" -> buildSourceTitlesAndUrlsContent(msg);
        case "truncated" -> buildTruncatedContent(msg, assistantMaxChars);
        case "none" -> null;
        default -> msg.getContent();
        };
    }

    /**
     * Builds a summary string from source document titles.
     *
     * @param msg the assistant chat message
     * @param summaryMaxChars the maximum characters for the content summary
     * @return a string listing referenced document titles
     */
    protected String buildSourceTitlesContent(final ChatMessage msg, final int summaryMaxChars) {
        final List<ChatSource> sources = msg.getSources();
        if (sources == null || sources.isEmpty()) {
            return buildTruncatedContent(msg, summaryMaxChars);
        }
        final int maxSuffixLen = Math.max(0, summaryMaxChars / 4);
        final String suffix = buildSourceTitlesSuffix(sources, maxSuffixLen);
        if (suffix.isEmpty()) {
            return buildTruncatedContent(msg, summaryMaxChars);
        }
        final String content = msg.getContent();
        if (content == null || content.isEmpty()) {
            return suffix;
        }
        final String truncMarker = "... [truncated]";
        final int bodyBudget = Math.max(0, summaryMaxChars - suffix.length() - truncMarker.length());
        if (content.length() <= bodyBudget) {
            return content + suffix;
        }
        if (bodyBudget <= 0) {
            return suffix;
        }
        return content.substring(0, bodyBudget) + truncMarker + suffix;
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
            }
            if (title != null && !title.isEmpty()) {
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
     *
     * @param msg the assistant chat message
     * @param maxChars the maximum characters for the content
     * @return the truncated content
     */
    protected String buildTruncatedContent(final ChatMessage msg, final int maxChars) {
        final String content = msg.getContent();
        if (content == null) {
            return null;
        }
        if (content.length() <= maxChars) {
            return content;
        }
        return content.substring(0, maxChars) + "...";
    }

    private String buildSourceTitlesSuffix(final List<ChatSource> sources, final int maxSuffixLength) {
        if (sources == null || sources.isEmpty()) {
            return "";
        }
        final String titles =
                sources.stream().map(ChatSource::getTitle).filter(t -> t != null && !t.isEmpty()).collect(Collectors.joining(", "));
        if (titles.isEmpty()) {
            return "";
        }
        final String suffix = "\n[Referenced documents: " + titles + "]";
        if (suffix.length() <= maxSuffixLength) {
            return suffix;
        }
        if (maxSuffixLength <= 0) {
            return "";
        }
        return suffix.substring(0, maxSuffixLength);
    }

    /**
     * Renders a single assistant turn for the Intent Detection prompt history.
     * Format: {@code searched: "<query>" -> found: [Title1, Title2, ... (+N more)]}.
     * Returns null when there is neither a search query nor any titles.
     *
     * @param assistantMsg the assistant message
     * @param titlesMaxCount the maximum number of titles to include
     * @return the rendered line or null
     */
    protected String renderIntentHistoryTurn(final ChatMessage assistantMsg, final int titlesMaxCount) {
        final String escapedQuery = escapeForLine(assistantMsg.getSearchQuery());
        final String titles = renderTitlesList(assistantMsg.getSources(), titlesMaxCount);
        final boolean hasQuery = StringUtil.isNotBlank(escapedQuery);
        final boolean hasTitles = !titles.isEmpty();
        if (!hasQuery && !hasTitles) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        if (hasQuery) {
            sb.append("searched: \"").append(escapedQuery).append("\"");
        }
        if (hasTitles) {
            if (hasQuery) {
                sb.append(" -> ");
            }
            sb.append("found: [").append(titles).append("]");
        }
        return sb.toString();
    }

    /**
     * Renders a single (user, assistant) turn for the Answer Generation prompt history.
     * Format: {@code Q: "<userQuery>" (searched: "<query>", refs: [Title1, Title2])}.
     *
     * @param userQuery the user's question for that turn
     * @param assistantMsg the assistant message
     * @param titlesMaxCount the maximum number of titles to include
     * @return the rendered line, or null if userQuery is blank
     */
    protected String renderAnswerHistoryTurn(final String userQuery, final ChatMessage assistantMsg, final int titlesMaxCount) {
        if (StringUtil.isBlank(userQuery)) {
            return null;
        }
        final String escapedUser = escapeForLine(userQuery);
        final String escapedQuery = escapeForLine(assistantMsg.getSearchQuery());
        final String titles = renderTitlesList(assistantMsg.getSources(), titlesMaxCount);
        final StringBuilder sb = new StringBuilder();
        sb.append("Q: \"").append(escapedUser).append("\"");
        final boolean hasQuery = StringUtil.isNotBlank(escapedQuery);
        final boolean hasTitles = !titles.isEmpty();
        if (hasQuery || hasTitles) {
            sb.append(" (");
            if (hasQuery) {
                sb.append("searched: \"").append(escapedQuery).append("\"");
                if (hasTitles) {
                    sb.append(", ");
                }
            }
            if (hasTitles) {
                sb.append("refs: [").append(titles).append("]");
            }
            sb.append(")");
        }
        return sb.toString();
    }

    private String renderTitlesList(final List<ChatSource> sources, final int titlesMaxCount) {
        if (sources == null || sources.isEmpty() || titlesMaxCount <= 0) {
            return "";
        }
        final List<String> titles =
                sources.stream().map(ChatSource::getTitle).filter(t -> t != null && !t.isEmpty()).collect(Collectors.toList());
        if (titles.isEmpty()) {
            return "";
        }
        if (titles.size() <= titlesMaxCount) {
            return String.join(", ", titles);
        }
        final List<String> head = titles.subList(0, titlesMaxCount);
        final int remaining = titles.size() - titlesMaxCount;
        return String.join(", ", head) + ", ... (+" + remaining + " more)";
    }

    private String escapeForLine(final String value) {
        if (value == null) {
            return null;
        }
        return value.replace('"', '\'').replace('\n', ' ').replace('\r', ' ');
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
        return searchWithQueryAndMetadata(query).getDocuments();
    }

    private ChatSearchResult searchWithQueryAndMetadata(final String query) {
        return searchWithQueryAndMetadata(query, Collections.emptyMap(), new String[0]);
    }

    /**
     * Searches documents using a Fess query with filters.
     *
     * @param query the Fess query string
     * @param fields the field filters (e.g., label)
     * @param extraQueries the extra query filters (e.g., filetype, timestamp)
     * @return the list of search result documents
     */
    protected List<Map<String, Object>> searchWithQuery(final String query, final Map<String, String[]> fields,
            final String[] extraQueries) {
        return searchWithQueryAndMetadata(query, fields, extraQueries).getDocuments();
    }

    private ChatSearchResult searchWithQueryAndMetadata(final String query, final Map<String, String[]> fields,
            final String[] extraQueries) {
        final ChatSearchResult rejected = validateQuery(query);
        if (rejected != null) {
            return rejected;
        }
        return searchDocuments(query, fields, extraQueries);
    }

    /**
     * Fetches full document content for the given doc ids.
     *
     * <p>Delegates to {@link ChatContentFetcher}. With a blank query the fetcher
     * resolves every document to its full content, preserving the original
     * behavior of this method.</p>
     *
     * @param docIds the document IDs to fetch
     * @return list of documents with full content, in docIds order
     */
    protected List<Map<String, Object>> fetchFullContent(final List<String> docIds) {
        return ComponentUtil.getChatContentFetcher().fetchContent(new ChatContentRequest(docIds, Collections.emptyList(), null));
    }

    /**
     * Resolves the answer-context documents through {@link ChatContentFetcher}, so chunked
     * documents ({@code content_chunk_status=done/chunked}) get semantic/keyword chunk selection
     * instead of feeding the raw search-result content to the LLM. The non-streaming chat answers
     * from the result; the streaming chat evaluates relevance on it and then answers from the
     * relevant subset, so both paths show the model the same passages.
     *
     * <p>Falls back to the raw {@code searchResults} when no doc ids can be extracted or the
     * fetcher returns nothing, so an OpenSearch hiccup degrades to the previous behavior
     * rather than an empty context.</p>
     *
     * <p>The returned maps are for the LLM answer context ONLY -- never for the API
     * {@code sources[]}. They come back from a {@code _source} projection, so the render-time-only
     * fields the sources need ({@code content_description}, {@code content_title}) are not in them.
     * The caller builds its sources from the search-phase maps.</p>
     *
     * @param searchResults the search result documents
     * @param query the final search query (may be null for the SUMMARY intent)
     * @return the documents to build the LLM answer context from
     */
    protected List<Map<String, Object>> fetchContentForAnswer(final List<Map<String, Object>> searchResults, final String query) {
        if (searchResults.isEmpty()) {
            return searchResults;
        }
        final String docIdField = ComponentUtil.getFessConfig().getIndexFieldDocId();
        final List<String> docIds = searchResults.stream()
                .map(doc -> doc.get(docIdField))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .collect(Collectors.toList());
        if (docIds.isEmpty()) {
            return searchResults;
        }
        try {
            final List<Map<String, Object>> fullDocs =
                    ComponentUtil.getChatContentFetcher().fetchContent(new ChatContentRequest(docIds, searchResults, query));
            return fullDocs.isEmpty() ? searchResults : fullDocs;
        } catch (final Exception e) {
            // The fetcher is an enrichment step: a failure must degrade this chat to the raw
            // search-result content (previous behavior), not fail the whole request.
            // DefaultChatContentFetcher already catches and degrades every engine-level failure
            // internally, so reaching here means a programming error (NPE/CCE) in the fetcher --
            // exactly the case where the stack trace is the entire diagnostic, so log the
            // Throwable itself, not only its (possibly null) message.
            logger.warn("[RAG] Failed to fetch answer content; using raw search results. docIds={}, error={}", docIds, e.getMessage(), e);
            return searchResults;
        }
    }

    /**
     * Picks the documents named by {@code docIds} out of {@code docs}, in {@code docIds} order.
     * Used by the streaming chat to keep only the documents the relevance evaluation accepted,
     * from the content that evaluation was shown. An id with no matching document, or one named
     * twice, contributes nothing (more).
     *
     * @param docs the documents the evaluation was run on
     * @param docIds the relevant document ids
     * @return the relevant documents, in {@code docIds} order
     */
    protected List<Map<String, Object>> selectDocsByIds(final List<Map<String, Object>> docs, final List<String> docIds) {
        final String docIdField = ComponentUtil.getFessConfig().getIndexFieldDocId();
        final Map<String, Map<String, Object>> docsByDocId = new HashMap<>();
        for (final Map<String, Object> doc : docs) {
            if (doc.get(docIdField) instanceof final String docId) {
                docsByDocId.putIfAbsent(docId, doc);
            }
        }
        final List<Map<String, Object>> selected = new ArrayList<>(docIds.size());
        for (final String docId : docIds) {
            final Map<String, Object> doc = docsByDocId.remove(docId);
            if (doc != null) {
                selected.add(doc);
            }
        }
        return selected;
    }

    /**
     * Resolves the API {@code sources[]} maps for a fetched (LLM-context) document list, by
     * mapping every fetched document back to its search-phase map.
     *
     * <p>{@code content_title} and {@code content_description} do not exist in the index (see
     * {@code fess_indices/fess/doc.json}); they are injected at render time by the rank-fusion
     * searcher via {@code ViewHelper#getContentDescription}, as is {@code score}. The fetch phase
     * re-reads documents through {@code SearchHelper#getDocumentListByDocIds} -- a pure
     * {@code _source} projection -- or through a doc_id-restricted highlight search, so those keys
     * are never present on the fetched maps. Publishing the fetched maps as {@code sources[]}
     * therefore silently drops {@code snippet} (and {@code title}, for a document whose indexed
     * {@code title} is blank) from the streaming API response.</p>
     *
     * <p>Only the maps are swapped: the fetched list keeps feeding the LLM answer context, so the
     * chunk-selected {@code content} the fetcher produced is unaffected. The returned list keeps
     * the fetched list's order and cardinality -- i.e. exactly the documents the fetch phase
     * resolved, not every search hit -- and a fetched document with no search-phase counterpart
     * (the evaluation phase is LLM-driven and could name a doc id outside the result set) keeps
     * its fetched map, preserving the previous behavior for that document.</p>
     *
     * @param fetchedDocs the fetched (LLM-context) documents
     * @param searchResults the search-phase result maps the fetch was derived from
     * @return the source maps, in {@code fetchedDocs} order
     */
    protected List<Map<String, Object>> resolveSourcesFromSearchResults(final List<Map<String, Object>> fetchedDocs,
            final List<Map<String, Object>> searchResults) {
        if (fetchedDocs.isEmpty() || searchResults == null || searchResults.isEmpty()) {
            return fetchedDocs;
        }
        final String docIdField = ComponentUtil.getFessConfig().getIndexFieldDocId();
        final Map<String, Map<String, Object>> searchDocsByDocId = new HashMap<>();
        for (final Map<String, Object> doc : searchResults) {
            final Object docId = doc.get(docIdField);
            if (docId instanceof String) {
                searchDocsByDocId.putIfAbsent((String) docId, doc);
            }
        }
        final List<Map<String, Object>> resolved = new ArrayList<>(fetchedDocs.size());
        for (final Map<String, Object> fetchedDoc : fetchedDocs) {
            final Object docId = fetchedDoc.get(docIdField);
            final Map<String, Object> searchDoc = docId instanceof String ? searchDocsByDocId.get(docId) : null;
            resolved.add(searchDoc != null ? searchDoc : fetchedDoc);
        }
        return resolved;
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
     * Delegates to the multi-argument variant with empty filters.
     *
     * @param query the search query
     * @return a ChatSearchResult with documents and search metadata
     */
    protected ChatSearchResult searchDocuments(final String query) {
        return searchDocuments(query, Collections.emptyMap(), new String[0]);
    }

    /**
     * Searches for documents by URL.
     *
     * @param url the URL to search for
     * @return a ChatSearchResult with documents and search metadata
     */
    protected ChatSearchResult searchByUrl(final String url) {
        if (StringUtil.isBlank(url)) {
            return new ChatSearchResult(Collections.emptyList(), null, 0L);
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int maxDocs = fessConfig.getRagChatContextMaxDocumentsAsInteger();

        try {
            final SearchRenderData data = new SearchRenderData();
            final ChatSearchRequestParams params =
                    new ChatSearchRequestParams("url:\"" + escapeQueryValue(url) + "\"", maxDocs, fessConfig);

            ComponentUtil.getSearchHelper().search(params, data, OptionalThing.empty());

            @SuppressWarnings("unchecked")
            final List<Map<String, Object>> docs = data.getDocumentItems();
            if (docs != null) {
                return new ChatSearchResult(docs, data.getQueryId(), data.getRequestedTime());
            }
        } catch (final Exception e) {
            logger.warn("Failed to search documents by URL: url={}", url, e);
        }

        return new ChatSearchResult(Collections.emptyList(), null, 0L);
    }

    /**
     * Validates a query and returns an empty result if invalid, or null if validation passed.
     */
    private ChatSearchResult validateQuery(final String query) {
        if (StringUtil.isBlank(query)) {
            return new ChatSearchResult(Collections.emptyList(), null, 0L);
        }
        if (query.length() > MAX_QUERY_LENGTH) {
            logger.warn("[RAG] Rejected LLM-generated query exceeding max length. length={}", query.length());
            return new ChatSearchResult(Collections.emptyList(), null, 0L);
        }
        if (DANGEROUS_QUERY_PATTERN.matcher(query).find()) {
            logger.warn("[RAG] Rejected LLM-generated query with dangerous pattern. query={}", query);
            return new ChatSearchResult(Collections.emptyList(), null, 0L);
        }
        return null;
    }

    /**
     * Searches for documents relevant to the user's query.
     * SearchHelper applies role-based access control filtering through
     * SearchRequestType.JSON and the role filter mechanism, ensuring
     * users only see documents they are authorized to access.
     * <p>
     * This is the primary extension point for subclasses to customize search behavior.
     *
     * @param query the search query
     * @param fields the field filters (e.g., label)
     * @param extraQueries the extra query filters (e.g., filetype, timestamp)
     * @return a ChatSearchResult with documents and search metadata
     */
    protected ChatSearchResult searchDocuments(final String query, final Map<String, String[]> fields, final String[] extraQueries) {
        final long startTime = System.currentTimeMillis();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int maxDocs = fessConfig.getRagChatContextMaxDocumentsAsInteger();

        if (logger.isDebugEnabled()) {
            logger.debug("[RAG] Starting document search. query={}, maxDocs={}", query, maxDocs);
        }

        try {
            final SearchRenderData data = new SearchRenderData();
            final ChatSearchRequestParams params = new ChatSearchRequestParams(query, maxDocs, fessConfig, fields, extraQueries);

            ComponentUtil.getSearchHelper().search(params, data, OptionalThing.empty());

            @SuppressWarnings("unchecked")
            final List<Map<String, Object>> docs = data.getDocumentItems();
            if (docs != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("[RAG] Document search completed. query={}, resultCount={}, elapsedTime={}ms", query, docs.size(),
                            System.currentTimeMillis() - startTime);
                }
                return new ChatSearchResult(docs, data.getQueryId(), data.getRequestedTime());
            }
        } catch (final Exception e) {
            logger.warn("Failed to search documents for RAG: query={}, elapsedTime={}ms", query, System.currentTimeMillis() - startTime, e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("[RAG] Document search returned no results. query={}, elapsedTime={}ms", query,
                    System.currentTimeMillis() - startTime);
        }
        return new ChatSearchResult(new ArrayList<>(), null, 0L);
    }

    /**
     * Resolves the context path from the current request, or empty string if unavailable.
     *
     * @return the context path
     */
    protected String resolveContextPath() {
        return LaRequestUtil.getOptionalRequest().map(HttpServletRequest::getContextPath).orElse("");
    }

    /**
     * Builds a go URL for the given document.
     *
     * @param contextPath the application context path
     * @param docId the document ID
     * @param queryId the query ID from the search
     * @param requestedTime the requested time from the search
     * @param order the order index of the document
     * @return the go URL, or null if docId or queryId is null
     */
    protected String buildGoUrl(final String contextPath, final String docId, final String queryId, final long requestedTime,
            final int order) {
        if (docId == null || queryId == null) {
            return null;
        }
        return contextPath + "/go/?rt=" + requestedTime + "&docId=" + URLEncoder.encode(docId, StandardCharsets.UTF_8) + "&queryId="
                + URLEncoder.encode(queryId, StandardCharsets.UTF_8) + "&order=" + order;
    }

    /**
     * Creates ChatSource objects from search results and adds them to the assistant message.
     *
     * @param assistantMessage the message to add sources to
     * @param sourceList the search result documents
     * @param contextPath the application context path
     * @param queryId the query ID from the search
     * @param requestedTime the requested time from the search
     */
    protected void addSourcesToMessage(final ChatMessage assistantMessage, final List<Map<String, Object>> sourceList,
            final String contextPath, final String queryId, final long requestedTime) {
        for (int i = 0; i < sourceList.size(); i++) {
            final ChatSource source = new ChatSource(i + 1, sourceList.get(i));
            source.setGoUrl(buildGoUrl(contextPath, source.getDocId(), queryId, requestedTime, i));
            assistantMessage.addSource(source);
        }
    }

    /**
     * Populates the url_link field in the document map if not already present.
     *
     * @param doc the document map
     */
    protected void populateUrlLink(final Map<String, Object> doc) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (doc.get(fessConfig.getResponseFieldUrlLink()) == null) {
            doc.put(fessConfig.getResponseFieldUrlLink(), ComponentUtil.getViewHelper().getUrlLink(doc));
        }
    }

    /**
     * Result of a search operation, including queryId and requestedTime.
     */
    protected static class ChatSearchResult {
        private final List<Map<String, Object>> documents;
        private final String queryId;
        private final long requestedTime;

        /**
         * Creates a new chat search result.
         *
         * @param documents the search result documents
         * @param queryId the query ID
         * @param requestedTime the requested time
         */
        public ChatSearchResult(final List<Map<String, Object>> documents, final String queryId, final long requestedTime) {
            this.documents = documents;
            this.queryId = queryId;
            this.requestedTime = requestedTime;
        }

        /**
         * Gets the search result documents.
         *
         * @return the list of documents
         */
        public List<Map<String, Object>> getDocuments() {
            return documents;
        }

        /**
         * Gets the query ID.
         *
         * @return the query ID
         */
        public String getQueryId() {
            return queryId;
        }

        /**
         * Gets the requested time.
         *
         * @return the requested time
         */
        public long getRequestedTime() {
            return requestedTime;
        }
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
        private final Map<String, String[]> fields;
        private final String[] extraQueries;

        /**
         * Creates new chat search request parameters.
         *
         * @param query the search query
         * @param pageSize the page size
         * @param fessConfig the Fess configuration
         */
        public ChatSearchRequestParams(final String query, final int pageSize, final FessConfig fessConfig) {
            this(query, pageSize, fessConfig, Collections.emptyMap(), new String[0]);
        }

        /**
         * Creates new chat search request parameters with filter support.
         *
         * @param query the search query
         * @param pageSize the page size
         * @param fessConfig the Fess configuration
         * @param fields the field filters (e.g., label)
         * @param extraQueries the extra query filters (e.g., filetype, timestamp)
         */
        public ChatSearchRequestParams(final String query, final int pageSize, final FessConfig fessConfig,
                final Map<String, String[]> fields, final String[] extraQueries) {
            this.query = query;
            this.pageSize = pageSize;
            this.fessConfig = fessConfig;
            this.fields = fields;
            this.extraQueries = extraQueries;
        }

        @Override
        public String getQuery() {
            return query;
        }

        @Override
        public Map<String, String[]> getFields() {
            return fields;
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
            return extraQueries;
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
