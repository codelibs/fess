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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.lastaflute.web.util.LaRequestUtil;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Abstract base class for LLM client implementations.
 *
 * Provides shared infrastructure (HTTP client, availability checking) and
 * default implementations of RAG workflow methods with injectable prompt templates.
 * Subclasses implement provider-specific chat/streamChat and checkAvailabilityNow.
 */
public abstract class AbstractLlmClient implements LlmClient {

    private static final Logger logger = LogManager.getLogger(AbstractLlmClient.class);

    /** Shared ObjectMapper instance for JSON processing. */
    protected static final ObjectMapper objectMapper = new ObjectMapper();

    /** The HTTP client used for API communication. */
    protected CloseableHttpClient httpClient;

    /** Cached availability status of the LLM provider. */
    protected volatile Boolean cachedAvailability = null;

    /** The scheduled task for periodic availability checks. */
    protected TimeoutTask availabilityCheckTask;

    /** Custom prompt template for intent detection. Use {@code ${userMessage}} as placeholder. */
    protected String intentDetectionPrompt;

    /** Custom system prompt for unclear intent responses. */
    protected String unclearIntentSystemPrompt;

    /** Custom system prompt for no-results responses. */
    protected String noResultsSystemPrompt;

    /** Custom system prompt for document-not-found responses. Use {@code ${documentUrl}} as placeholder. */
    protected String documentNotFoundSystemPrompt;

    /** Custom system prompt suffix for summary generation. */
    protected String summarySystemPromptSuffix;

    /** Custom prompt template for relevance evaluation. Uses {@code {{maxRelevantDocs}}}, {@code {{userMessage}}}, {@code {{query}}}, {@code {{searchResults}}} placeholders. */
    protected String evaluationPrompt;

    /** Custom system prompt template for answer generation. Uses {@code {{systemPrompt}}}, {@code {{context}}} placeholders. */
    protected String answerGenerationSystemPrompt;

    /** Custom system prompt template for summary generation. Uses {@code {{systemPrompt}}}, {@code {{documentContent}}} placeholders. */
    protected String summarySystemPrompt;

    /** Custom system prompt template for FAQ answer generation. Uses {@code {{systemPrompt}}}, {@code {{context}}} placeholders. */
    protected String faqAnswerSystemPrompt;

    /** Custom system prompt for direct answer generation (without document search). */
    protected String directAnswerSystemPrompt;

    /**
     * Default constructor.
     */
    public AbstractLlmClient() {
        // Default constructor
    }

    // --- Shared infrastructure ---

    /**
     * Initializes the HTTP client and starts availability checking.
     * Should be called from subclass init() methods.
     */
    public void init() {
        if (!getName().equals(getLlmType())) {
            if (logger.isDebugEnabled()) {
                logger.debug("Skipping availability check. llmType={}, name={}", getLlmType(), getName());
            }
            return;
        }

        final int timeout = getTimeout();
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(timeout))
                .setResponseTimeout(Timeout.ofMilliseconds(timeout))
                .build();
        httpClient = HttpClients.custom()
                .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                        .setDefaultConnectionConfig(ConnectionConfig.custom().setConnectTimeout(Timeout.ofMilliseconds(timeout)).build())
                        .build())
                .setDefaultRequestConfig(requestConfig)
                .disableAutomaticRetries()
                .build();
        if (logger.isDebugEnabled()) {
            logger.debug("Initialized {} with timeout: {}ms", getClass().getSimpleName(), timeout);
        }

        startAvailabilityCheck();
    }

    /**
     * Cleans up resources.
     */
    public void destroy() {
        if (availabilityCheckTask != null && !availabilityCheckTask.isCanceled()) {
            availabilityCheckTask.cancel();
            if (logger.isDebugEnabled()) {
                logger.debug("Cancelled {} availability check task", getName());
            }
        }
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (final IOException e) {
                logger.warn("Failed to close HTTP client", e);
            }
            httpClient = null;
        }
    }

    /**
     * Starts periodic availability checking if RAG chat is enabled.
     */
    protected void startAvailabilityCheck() {
        if (!isRagChatEnabled()) {
            if (logger.isDebugEnabled()) {
                logger.debug("RAG chat is disabled. Skipping availability check.");
            }
            return;
        }

        final int checkInterval = getAvailabilityCheckInterval();
        if (checkInterval <= 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("Availability check is disabled for {}", getName());
            }
            return;
        }

        updateAvailability();

        availabilityCheckTask = TimeoutManager.getInstance().addTimeoutTarget(this::updateAvailability, checkInterval, true);

        if (logger.isDebugEnabled()) {
            logger.debug("Started {} availability check with interval: {}s", getName(), checkInterval);
        }
    }

    /**
     * Updates the cached availability state.
     */
    protected void updateAvailability() {
        final boolean previousState = cachedAvailability != null ? cachedAvailability : false;
        final boolean currentState = checkAvailabilityNow();
        cachedAvailability = currentState;

        if (previousState != currentState) {
            logger.info("{} availability changed: {} -> {}", getName(), previousState, currentState);
        } else if (logger.isDebugEnabled()) {
            logger.debug("{} availability check completed. available={}", getName(), currentState);
        }
    }

    @Override
    public boolean isAvailable() {
        if (cachedAvailability != null) {
            return cachedAvailability;
        }
        return checkAvailabilityNow();
    }

    /**
     * Gets the HTTP client, initializing it if necessary.
     *
     * @return the HTTP client
     */
    public CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            init();
        }
        return httpClient;
    }

    // --- Abstract methods for subclasses ---

    /**
     * Performs the actual availability check against the LLM provider.
     *
     * @return true if the provider is available
     */
    protected abstract boolean checkAvailabilityNow();

    /**
     * Gets the request timeout in milliseconds.
     *
     * @return the timeout in milliseconds
     */
    protected abstract int getTimeout();

    /**
     * Gets the model name.
     *
     * @return the model name
     */
    protected abstract String getModel();

    /**
     * Gets the availability check interval in seconds.
     *
     * @return the interval in seconds
     */
    protected int getAvailabilityCheckInterval() {
        return ComponentUtil.getFessConfig().getRagLlmAvailabilityCheckIntervalAsInteger();
    }

    /**
     * Checks if RAG chat feature is enabled.
     *
     * @return true if RAG chat is enabled
     */
    protected boolean isRagChatEnabled() {
        return ComponentUtil.getFessConfig().isRagChatEnabled();
    }

    /**
     * Gets the configured LLM type.
     *
     * @return the LLM type from configuration
     */
    protected String getLlmType() {
        return ComponentUtil.getFessConfig().getRagLlmType();
    }

    /**
     * Gets the temperature parameter.
     *
     * @return the temperature
     */
    protected double getTemperature() {
        return ComponentUtil.getFessConfig().getRagChatTemperatureAsDecimal().doubleValue();
    }

    /**
     * Gets the maximum tokens for the response.
     *
     * @return the maximum tokens
     */
    protected int getMaxTokens() {
        return ComponentUtil.getFessConfig().getRagChatMaxTokensAsInteger();
    }

    // --- Prompt template setters (for fess_llm.xml injection) ---

    /**
     * Sets the custom prompt template for intent detection.
     *
     * @param intentDetectionPrompt the prompt template with {@code {{userMessage}}} placeholder
     */
    public void setIntentDetectionPrompt(final String intentDetectionPrompt) {
        this.intentDetectionPrompt = intentDetectionPrompt;
    }

    /**
     * Sets the custom system prompt for unclear intent responses.
     *
     * @param unclearIntentSystemPrompt the system prompt
     */
    public void setUnclearIntentSystemPrompt(final String unclearIntentSystemPrompt) {
        this.unclearIntentSystemPrompt = unclearIntentSystemPrompt;
    }

    /**
     * Sets the custom system prompt for no-results responses.
     *
     * @param noResultsSystemPrompt the system prompt
     */
    public void setNoResultsSystemPrompt(final String noResultsSystemPrompt) {
        this.noResultsSystemPrompt = noResultsSystemPrompt;
    }

    /**
     * Sets the custom system prompt for document-not-found responses.
     *
     * @param documentNotFoundSystemPrompt the system prompt with {@code {{documentUrl}}} placeholder
     */
    public void setDocumentNotFoundSystemPrompt(final String documentNotFoundSystemPrompt) {
        this.documentNotFoundSystemPrompt = documentNotFoundSystemPrompt;
    }

    /**
     * Sets the custom system prompt suffix for summary generation.
     *
     * @param summarySystemPromptSuffix the prompt suffix
     */
    public void setSummarySystemPromptSuffix(final String summarySystemPromptSuffix) {
        this.summarySystemPromptSuffix = summarySystemPromptSuffix;
    }

    /**
     * Sets the custom evaluation prompt for relevance checking.
     *
     * @param evaluationPrompt the evaluation prompt template
     */
    public void setEvaluationPrompt(final String evaluationPrompt) {
        this.evaluationPrompt = evaluationPrompt;
    }

    /**
     * Sets the custom system prompt for answer generation.
     *
     * @param answerGenerationSystemPrompt the answer generation system prompt template
     */
    public void setAnswerGenerationSystemPrompt(final String answerGenerationSystemPrompt) {
        this.answerGenerationSystemPrompt = answerGenerationSystemPrompt;
    }

    /**
     * Sets the custom system prompt for summary generation.
     *
     * @param summarySystemPrompt the summary system prompt template
     */
    public void setSummarySystemPrompt(final String summarySystemPrompt) {
        this.summarySystemPrompt = summarySystemPrompt;
    }

    /**
     * Sets the custom system prompt for FAQ answer generation.
     *
     * @param faqAnswerSystemPrompt the FAQ answer system prompt template
     */
    public void setFaqAnswerSystemPrompt(final String faqAnswerSystemPrompt) {
        this.faqAnswerSystemPrompt = faqAnswerSystemPrompt;
    }

    /**
     * Sets the custom system prompt for direct answer generation.
     *
     * @param directAnswerSystemPrompt the direct answer system prompt
     */
    public void setDirectAnswerSystemPrompt(final String directAnswerSystemPrompt) {
        this.directAnswerSystemPrompt = directAnswerSystemPrompt;
    }

    // --- Locale support methods ---

    /**
     * Gets the user's locale from the current request context.
     *
     * @return the user's locale, or default locale if not in request context
     */
    protected Locale getUserLocale() {
        try {
            return LaRequestUtil.getOptionalRequest().map(request -> request.getLocale()).orElse(Locale.getDefault());
        } catch (final Exception e) {
            return Locale.getDefault();
        }
    }

    /**
     * Gets the language instruction based on the user's locale.
     *
     * @return the language instruction string, or empty string if locale is English
     */
    protected String getLanguageInstruction() {
        final Locale locale = getUserLocale();
        final String language = locale.getLanguage();
        if ("en".equals(language)) {
            return StringUtil.EMPTY;
        }
        return "IMPORTANT: You MUST respond in " + locale.getDisplayLanguage(Locale.ENGLISH) + ".";
    }

    /**
     * Resolves the {{languageInstruction}} placeholder in a prompt.
     *
     * @param prompt the prompt template
     * @return the prompt with language instruction resolved
     */
    protected String resolveLanguageInstruction(final String prompt) {
        if (prompt == null) {
            return null;
        }
        final String languageInstruction = getLanguageInstruction();
        if (logger.isDebugEnabled()) {
            logger.debug("[RAG] languageInstruction={}", languageInstruction);
        }
        return prompt.replace("{{languageInstruction}}", languageInstruction);
    }

    // --- Default RAG method implementations ---

    @Override
    public IntentDetectionResult detectIntent(final String userMessage) {
        final long startTime = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("[RAG:INTENT] Starting intent detection. userMessage={}", userMessage);
        }

        try {
            final String prompt = buildIntentDetectionPrompt(userMessage);
            if (logger.isDebugEnabled()) {
                logger.debug("[RAG:INTENT] prompt={}", prompt);
            }
            final LlmChatRequest request = new LlmChatRequest();
            request.addUserMessage(prompt);
            request.setMaxTokens(500);
            request.setTemperature(0.3);

            final LlmChatResponse response = chat(request);
            final IntentDetectionResult result = parseIntentResponse(response.getContent());

            if (logger.isDebugEnabled()) {
                logger.debug("[RAG:INTENT] Intent detection completed. intent={}, query={}, reasoning={}, elapsedTime={}ms",
                        result.getIntent(), result.getQuery(), result.getReasoning(), System.currentTimeMillis() - startTime);
            }

            return result;
        } catch (final Exception e) {
            logger.warn("Failed to detect intent, falling back to search. error={}, elapsedTime={}ms", e.getMessage(),
                    System.currentTimeMillis() - startTime);
            return IntentDetectionResult.fallbackSearch(userMessage);
        }
    }

    @Override
    public RelevanceEvaluationResult evaluateResults(final String userMessage, final String query,
            final List<Map<String, Object>> searchResults) {
        final long startTime = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("[RAG:EVAL] Starting result evaluation. userMessage={}, query={}, resultCount={}", userMessage, query,
                    searchResults.size());
        }

        try {
            final String prompt = buildEvaluationPrompt(userMessage, query, searchResults);
            if (logger.isDebugEnabled()) {
                logger.debug("[RAG:EVAL] prompt={}", prompt);
            }
            final LlmChatRequest request = new LlmChatRequest();
            request.addUserMessage(prompt);
            request.setMaxTokens(500);
            request.setTemperature(0.3);

            final LlmChatResponse response = chat(request);
            final RelevanceEvaluationResult result = parseEvaluationResponse(response.getContent(), searchResults);

            if (logger.isDebugEnabled()) {
                logger.debug("[RAG:EVAL] Result evaluation completed. hasRelevant={}, relevantDocIds={}, elapsedTime={}ms",
                        result.isHasRelevantResults(), result.getRelevantDocIds(), System.currentTimeMillis() - startTime);
            }

            return result;
        } catch (final Exception e) {
            logger.warn("Failed to evaluate results, using all results. error={}, elapsedTime={}ms", e.getMessage(),
                    System.currentTimeMillis() - startTime);
            final List<String> allDocIds = searchResults.stream()
                    .map(doc -> getStringValue(doc, "doc_id"))
                    .filter(StringUtil::isNotBlank)
                    .collect(Collectors.toList());
            return RelevanceEvaluationResult.fallbackAllRelevant(allDocIds);
        }
    }

    @Override
    public LlmChatResponse generateAnswer(final String userMessage, final List<Map<String, Object>> documents,
            final List<LlmMessage> history) {
        if (logger.isDebugEnabled()) {
            logger.debug("[RAG:ANSWER] generateAnswer. userMessage={}, documentCount={}, historySize={}", userMessage, documents.size(),
                    history.size());
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String context = buildContext(documents, fessConfig);
        final LlmChatRequest request = buildStreamingRequest(fessConfig, userMessage, context, history);

        return chat(request);
    }

    @Override
    public void streamGenerateAnswer(final String userMessage, final List<Map<String, Object>> documents, final List<LlmMessage> history,
            final LlmStreamCallback callback) {
        if (logger.isDebugEnabled()) {
            logger.debug("[RAG:ANSWER] streamGenerateAnswer. userMessage={}, documentCount={}, historySize={}", userMessage,
                    documents.size(), history.size());
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String context = buildContext(documents, fessConfig);
        final LlmChatRequest request = buildStreamingRequest(fessConfig, userMessage, context, history);
        request.setStream(true);

        streamChat(request, callback);
    }

    @Override
    public void generateUnclearIntentResponse(final String userMessage, final List<LlmMessage> history, final LlmStreamCallback callback) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final LlmChatRequest request = new LlmChatRequest();

        final String systemPrompt;
        if (unclearIntentSystemPrompt != null) {
            systemPrompt = unclearIntentSystemPrompt;
        } else {
            systemPrompt = "You are a helpful assistant for a document search system. "
                    + "The user's question is too vague to determine what documents to search for. "
                    + "Generate a polite message asking for clarification. Ask them:\n"
                    + "- What specific topic or document are they looking for?\n" + "- Can they provide more details or keywords?\n"
                    + "- What kind of information would be helpful?\n\n" + "IMPORTANT: Do NOT provide any answers from your own knowledge. "
                    + "Only ask for clarification to help with document search.";
        }
        final String resolvedPrompt = resolveLanguageInstruction(systemPrompt);
        if (logger.isDebugEnabled()) {
            logger.debug("[RAG:ANSWER] generateUnclearIntentResponse. resolvedPrompt={}, userMessage={}, historySize={}", resolvedPrompt,
                    userMessage, history.size());
        }
        request.addSystemMessage(resolvedPrompt);

        addHistory(request, history);
        request.addUserMessage(userMessage);
        request.setMaxTokens(fessConfig.getRagChatMaxTokensAsInteger());
        request.setTemperature(fessConfig.getRagChatTemperatureAsDecimal().doubleValue());
        request.setStream(true);

        streamChat(request, callback);
    }

    @Override
    public void generateNoResultsResponse(final String userMessage, final List<LlmMessage> history, final LlmStreamCallback callback) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final LlmChatRequest request = new LlmChatRequest();

        final String systemPrompt;
        if (noResultsSystemPrompt != null) {
            systemPrompt = noResultsSystemPrompt;
        } else {
            systemPrompt = "You are a helpful assistant for a document search system. "
                    + "The search for relevant documents returned no results matching the user's query. "
                    + "Generate a polite message informing the user that no documents matching their query were found. "
                    + "Suggest ways they could refine their search, such as:\n" + "- Using different keywords\n"
                    + "- Being more specific or more general\n" + "- Checking for spelling errors\n" + "- Trying related terms\n\n"
                    + "IMPORTANT: Do NOT provide any answers from your own knowledge. "
                    + "Only inform them about the search results and offer suggestions for refining their search.";
        }
        final String resolvedPrompt = resolveLanguageInstruction(systemPrompt);
        if (logger.isDebugEnabled()) {
            logger.debug("[RAG:ANSWER] generateNoResultsResponse. resolvedPrompt={}, userMessage={}, historySize={}", resolvedPrompt,
                    userMessage, history.size());
        }
        request.addSystemMessage(resolvedPrompt);

        addHistory(request, history);
        request.addUserMessage(userMessage);
        request.setMaxTokens(fessConfig.getRagChatMaxTokensAsInteger());
        request.setTemperature(fessConfig.getRagChatTemperatureAsDecimal().doubleValue());
        request.setStream(true);

        streamChat(request, callback);
    }

    @Override
    public void generateDocumentNotFoundResponse(final String userMessage, final String documentUrl, final List<LlmMessage> history,
            final LlmStreamCallback callback) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final LlmChatRequest request = new LlmChatRequest();

        final String systemPrompt;
        if (documentNotFoundSystemPrompt != null) {
            systemPrompt = documentNotFoundSystemPrompt.replace("{{documentUrl}}", documentUrl);
        } else {
            systemPrompt = "You are a helpful assistant for a document search system. "
                    + "The user requested a summary of a document, but the specified URL was not found in the system. " + "URL searched: "
                    + documentUrl + "\n\n" + "Generate a polite message informing the user that:\n"
                    + "- The specified document could not be found\n" + "- The URL might be incorrect or the document may not be indexed\n"
                    + "- They can try searching with keywords instead\n\n"
                    + "IMPORTANT: Do NOT provide any information from your own knowledge. " + "Only inform them about the search result.";
        }
        final String resolvedPrompt = resolveLanguageInstruction(systemPrompt);
        if (logger.isDebugEnabled()) {
            logger.debug("[RAG:ANSWER] generateDocumentNotFoundResponse. resolvedPrompt={}, documentUrl={}, userMessage={}, historySize={}",
                    resolvedPrompt, documentUrl, userMessage, history.size());
        }
        request.addSystemMessage(resolvedPrompt);

        addHistory(request, history);
        request.addUserMessage(userMessage);
        request.setMaxTokens(fessConfig.getRagChatMaxTokensAsInteger());
        request.setTemperature(fessConfig.getRagChatTemperatureAsDecimal().doubleValue());
        request.setStream(true);

        streamChat(request, callback);
    }

    @Override
    public void generateSummaryResponse(final String userMessage, final List<Map<String, Object>> documents, final List<LlmMessage> history,
            final LlmStreamCallback callback) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final LlmChatRequest request = new LlmChatRequest();

        final StringBuilder documentContent = new StringBuilder();
        for (final Map<String, Object> doc : documents) {
            final String title = (String) doc.get("title");
            final String content = (String) doc.get("content");
            final String url = (String) doc.get("url");

            documentContent.append("=== Document ===\n");
            if (title != null) {
                documentContent.append("Title: ").append(title).append("\n");
            }
            if (url != null) {
                documentContent.append("URL: ").append(url).append("\n");
            }
            if (content != null) {
                documentContent.append("Content:\n").append(content).append("\n\n");
            }
        }

        final String baseSystemPrompt = fessConfig.getRagChatSystemPrompt();
        final String systemPrompt;
        if (summarySystemPrompt != null) {
            systemPrompt = summarySystemPrompt.replace("{{systemPrompt}}", baseSystemPrompt)
                    .replace("{{documentContent}}", documentContent.toString());
        } else if (summarySystemPromptSuffix != null) {
            // Deprecated fallback for backward compatibility
            final String suffix = "\n\n" + summarySystemPromptSuffix + "\n\nDocument content:\n" + documentContent.toString();
            systemPrompt = baseSystemPrompt + suffix;
        } else {
            // Hardcoded fallback
            final String instructionText = "You are summarizing specific documents for the user. "
                    + "Base your summary ONLY on the provided document content. " + "Do NOT add information from your own knowledge.";
            final String suffix = "\n\n" + instructionText + "\n\nDocument content:\n" + documentContent.toString();
            systemPrompt = baseSystemPrompt + suffix;
        }
        final String resolvedPrompt = resolveLanguageInstruction(systemPrompt);
        if (logger.isDebugEnabled()) {
            logger.debug("[RAG:ANSWER] generateSummaryResponse. resolvedPrompt={}, userMessage={}, documentCount={}, historySize={}",
                    resolvedPrompt, userMessage, documents.size(), history.size());
        }
        request.addSystemMessage(resolvedPrompt);

        addHistory(request, history);
        request.addUserMessage(userMessage);
        request.setMaxTokens(fessConfig.getRagChatMaxTokensAsInteger());
        request.setTemperature(fessConfig.getRagChatTemperatureAsDecimal().doubleValue());
        request.setStream(true);

        streamChat(request, callback);
    }

    @Override
    public void generateFaqAnswerResponse(final String userMessage, final List<Map<String, Object>> documents,
            final List<LlmMessage> history, final LlmStreamCallback callback) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String context = buildContext(documents, fessConfig);

        final String baseSystemPrompt = fessConfig.getRagChatSystemPrompt();
        final String systemPrompt;
        if (faqAnswerSystemPrompt != null) {
            systemPrompt = faqAnswerSystemPrompt.replace("{{systemPrompt}}", baseSystemPrompt)
                    .replace("{{context}}", StringUtil.isNotBlank(context) ? context : "");
        } else {
            // Default FAQ prompt
            systemPrompt = baseSystemPrompt + "\n\n" + "The user is asking a frequently asked question. "
                    + "Provide a direct, concise answer based solely on the following documents. "
                    + "If the answer is clearly stated in the documents, provide it without unnecessary elaboration. "
                    + "Always cite your sources using [1], [2], etc.\n\n" + (StringUtil.isNotBlank(context) ? context : "");
        }
        final String resolvedPrompt = resolveLanguageInstruction(systemPrompt);
        if (logger.isDebugEnabled()) {
            logger.debug("[RAG:ANSWER] generateFaqAnswerResponse. resolvedPrompt={}, contextLength={}, userMessage={}, historySize={}",
                    resolvedPrompt, context != null ? context.length() : 0, userMessage, history.size());
        }

        final LlmChatRequest request = new LlmChatRequest();
        request.addSystemMessage(resolvedPrompt);
        addHistory(request, history);
        request.addUserMessage(userMessage);
        request.setMaxTokens(fessConfig.getRagChatMaxTokensAsInteger());
        request.setTemperature(fessConfig.getRagChatTemperatureAsDecimal().doubleValue());
        request.setStream(true);

        streamChat(request, callback);
    }

    @Override
    public void generateDirectAnswer(final String userMessage, final List<LlmMessage> history, final LlmStreamCallback callback) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final LlmChatRequest request = new LlmChatRequest();

        final String systemPrompt;
        if (directAnswerSystemPrompt != null) {
            systemPrompt = directAnswerSystemPrompt.replace("{{systemPrompt}}", fessConfig.getRagChatSystemPrompt());
        } else {
            systemPrompt = fessConfig.getRagChatSystemPrompt();
        }
        final String resolvedPrompt = resolveLanguageInstruction(systemPrompt);
        if (logger.isDebugEnabled()) {
            logger.debug("[RAG:ANSWER] generateDirectAnswer. resolvedPrompt={}, userMessage={}, historySize={}", resolvedPrompt,
                    userMessage, history.size());
        }
        request.addSystemMessage(resolvedPrompt);

        addHistory(request, history);
        request.addUserMessage(userMessage);
        request.setMaxTokens(fessConfig.getRagChatMaxTokensAsInteger());
        request.setTemperature(fessConfig.getRagChatTemperatureAsDecimal().doubleValue());
        request.setStream(true);

        streamChat(request, callback);
    }

    // --- Prompt building methods ---

    /**
     * Builds the intent detection prompt.
     *
     * @param userMessage the user's message
     * @return the constructed prompt string
     */
    protected String buildIntentDetectionPrompt(final String userMessage) {
        if (intentDetectionPrompt != null) {
            return resolveLanguageInstruction(intentDetectionPrompt.replace("{{userMessage}}", userMessage));
        }

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
                + "Input: \"How to use Fess with Docker\"\n"
                + "Output: {\"intent\":\"search\",\"query\":\"+\\\"Fess\\\" +Docker (usage OR howto OR tutorial)\",\"reasoning\":\"Tutorial query\"}\n\n"
                + resolveLanguageInstruction("{{languageInstruction}}") + "\n" + "Question: " + userMessage + "\n\n"
                + "Response (JSON only):";
    }

    /**
     * Builds the evaluation prompt for relevance checking.
     *
     * @param userMessage the user's message
     * @param query the search query
     * @param searchResults the search results to evaluate
     * @return the evaluation prompt
     */
    protected String buildEvaluationPrompt(final String userMessage, final String query, final List<Map<String, Object>> searchResults) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        // Build search results formatted text
        final StringBuilder searchResultsText = new StringBuilder();
        for (int i = 0; i < searchResults.size(); i++) {
            final Map<String, Object> doc = searchResults.get(i);
            searchResultsText.append("[").append(i + 1).append("] ");
            searchResultsText.append("Title: ").append(getStringValue(doc, "title")).append("\n");
            searchResultsText.append("Description: ").append(getStringValue(doc, "content_description")).append("\n\n");
        }

        if (evaluationPrompt != null) {
            return evaluationPrompt
                    .replace("{{maxRelevantDocs}}", String.valueOf(fessConfig.getRagChatEvaluationMaxRelevantDocsAsInteger()))
                    .replace("{{userMessage}}", userMessage)
                    .replace("{{query}}", query)
                    .replace("{{searchResults}}", searchResultsText.toString());
        }

        // Fallback: hardcoded default
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
        sb.append(searchResultsText);
        sb.append("Response (JSON only):");
        return sb.toString();
    }

    /**
     * Builds context from document content for the LLM prompt.
     *
     * @param documents the search result documents
     * @param fessConfig the Fess configuration
     * @return the context string
     */
    protected String buildContext(final List<Map<String, Object>> documents, final FessConfig fessConfig) {
        final int maxChars = fessConfig.getRagChatContextMaxCharsAsInteger();
        if (logger.isDebugEnabled()) {
            logger.debug("[RAG:CONTEXT] Building context. documentCount={}, maxChars={}", documents.size(), maxChars);
        }
        final StringBuilder context = new StringBuilder();
        context.append("The following are documents that contain information to answer the question:\n\n");

        int totalChars = context.length();
        int index = 1;
        boolean truncated = false;

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
                truncated = true;
                break;
            }

            context.append(docContext);
            totalChars += docContext.length();
            index++;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("[RAG:CONTEXT] Context built. contextLength={}, documentsIncluded={}, truncated={}", context.length(), index - 1,
                    truncated);
        }

        return context.toString();
    }

    /**
     * Builds a streaming LLM chat request with conversation history.
     *
     * @param fessConfig the Fess configuration
     * @param userMessage the user's message
     * @param context the context from search results
     * @param history the conversation history
     * @return the LLM chat request
     */
    protected LlmChatRequest buildStreamingRequest(final FessConfig fessConfig, final String userMessage, final String context,
            final List<LlmMessage> history) {
        final LlmChatRequest request = new LlmChatRequest();

        final String baseSystemPrompt = fessConfig.getRagChatSystemPrompt();
        final String systemPrompt;
        if (answerGenerationSystemPrompt != null) {
            systemPrompt = answerGenerationSystemPrompt.replace("{{systemPrompt}}", baseSystemPrompt)
                    .replace("{{context}}", StringUtil.isNotBlank(context) ? context : "");
        } else {
            // Fallback: original logic
            if (StringUtil.isNotBlank(context)) {
                systemPrompt = baseSystemPrompt + "\n\n" + context;
            } else {
                systemPrompt = baseSystemPrompt;
            }
        }
        final String resolvedPrompt = resolveLanguageInstruction(systemPrompt);
        if (logger.isDebugEnabled()) {
            logger.debug("[RAG:ANSWER] buildStreamingRequest. resolvedPrompt={}, contextLength={}, userMessage={}, historySize={}",
                    resolvedPrompt, context != null ? context.length() : 0, userMessage, history.size());
        }
        request.addSystemMessage(resolvedPrompt);

        addHistory(request, history);
        request.addUserMessage(userMessage);

        request.setMaxTokens(fessConfig.getRagChatMaxTokensAsInteger());
        request.setTemperature(fessConfig.getRagChatTemperatureAsDecimal().doubleValue());

        return request;
    }

    // --- JSON parsing utilities ---

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
     * Extracts a boolean value from JSON response.
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
            final String pattern = "\"" + key + "\"\\s*:\\s*(true|false)";
            final java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.CASE_INSENSITIVE);
            final java.util.regex.Matcher m = p.matcher(stripCodeFences(json));
            return m.find() && "true".equalsIgnoreCase(m.group(1));
        }
        return false;
    }

    /**
     * Extracts a string array from JSON response.
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
     * Extracts an integer array from JSON response.
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

    // --- Utility methods ---

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
     * Adds conversation history to the request.
     *
     * @param request the LLM chat request
     * @param history the conversation history
     */
    protected void addHistory(final LlmChatRequest request, final List<LlmMessage> history) {
        for (final LlmMessage msg : history) {
            request.addMessage(msg);
        }
    }
}
