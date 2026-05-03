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

import java.util.Map;

/**
 * Callback interface for receiving notifications about chat processing phases.
 * Used for SSE streaming to notify clients about the current processing state.
 */
public interface ChatPhaseCallback {

    /** Phase name for intent detection */
    String PHASE_INTENT = "intent";

    /** Phase name for document search */
    String PHASE_SEARCH = "search";

    /** Phase name for result evaluation */
    String PHASE_EVALUATE = "evaluate";

    /** Phase name for content retrieval */
    String PHASE_FETCH = "fetch";

    /** Phase name for answer generation */
    String PHASE_ANSWER = "answer";

    /**
     * Called when a processing phase starts.
     *
     * @param phase the phase name (e.g., "intent", "search", "evaluate", "fetch", "answer")
     * @param message a human-readable message describing what's happening
     */
    void onPhaseStart(String phase, String message);

    /**
     * Called when a processing phase starts with additional context data.
     *
     * @param phase the phase name (e.g., "intent", "search", "evaluate", "fetch", "answer")
     * @param message a human-readable message describing what's happening
     * @param keywords the search keywords (for search phase)
     */
    default void onPhaseStart(final String phase, final String message, final String keywords) {
        onPhaseStart(phase, message);
    }

    /**
     * Called when a processing phase completes.
     *
     * @param phase the phase name that completed
     */
    void onPhaseComplete(String phase);

    /**
     * Called when a processing phase completes with metadata payload (e.g., hit counts).
     * Default implementation delegates to {@link #onPhaseComplete(String)} ignoring the payload.
     *
     * @param phase the phase name that completed
     * @param payload optional metadata such as {@code hitCount}; never null
     */
    default void onPhaseComplete(final String phase, final Map<String, Object> payload) {
        onPhaseComplete(phase);
    }

    /**
     * Called when a chunk of the response is available during streaming.
     *
     * @param content the content chunk
     * @param done true if this is the final chunk
     */
    void onChunk(String content, boolean done);

    /**
     * Called when an error occurs during processing.
     *
     * @param phase the phase where the error occurred
     * @param error the error message
     */
    void onError(String phase, String error);

    /**
     * Called when the underlying LLM call is being retried.
     *
     * @param phase the phase where the retry occurs
     * @param operation log label for the LLM operation (e.g., "streamChat")
     * @param attempt 1-based attempt index that just failed
     * @param maxAttempts total attempts including the first
     * @param sleepMs milliseconds the client will sleep before the next attempt
     * @param cause short cause description (e.g., "status:429" or "exception:IOException")
     */
    default void onRetry(final String phase, final String operation, final int attempt, final int maxAttempts, final long sleepMs,
            final String cause) {
        // Default implementation does nothing
    }

    /**
     * Called when the request is waiting for a concurrency permit.
     *
     * @param phase the phase where waiting occurs
     * @param reason short reason code (currently always "concurrency_limit")
     * @param elapsedMs milliseconds already spent waiting
     * @param timeoutMs maximum milliseconds the client will wait
     */
    default void onWaiting(final String phase, final String reason, final long elapsedMs, final long timeoutMs) {
        // Default implementation does nothing
    }

    /**
     * Called when the search query is regenerated due to no or no-relevant results.
     *
     * @param phase the phase where fallback occurs (typically "search")
     * @param reason short reason code (e.g., "no_results", "no_relevant_results")
     * @param originalQuery the query that produced no useful results
     * @param newQuery the regenerated query that will be retried
     */
    default void onFallback(final String phase, final String reason, final String originalQuery, final String newQuery) {
        // Default implementation does nothing
    }

    /**
     * Called when an internal silent fallback occurs (e.g., reasoning model token exhaustion).
     *
     * @param phase the phase where the warning occurred
     * @param code short machine-readable warning code (e.g., "reasoning_token_exhausted")
     * @param detail short human-readable detail or related fallback action
     */
    default void onWarning(final String phase, final String code, final String detail) {
        // Default implementation does nothing
    }

    /**
     * Returns a no-op callback implementation.
     *
     * @return a callback that does nothing
     */
    static ChatPhaseCallback noOp() {
        return new ChatPhaseCallback() {
            @Override
            public void onPhaseStart(final String phase, final String message) {
                // no-op
            }

            @Override
            public void onPhaseComplete(final String phase) {
                // no-op
            }

            @Override
            public void onChunk(final String content, final boolean done) {
                // no-op
            }

            @Override
            public void onError(final String phase, final String error) {
                // no-op
            }
        };
    }
}
