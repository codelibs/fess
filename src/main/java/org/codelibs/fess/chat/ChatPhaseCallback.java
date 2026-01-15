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
