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
 * Callback interface for receiving streaming chat responses from LLM.
 */
@FunctionalInterface
public interface LlmStreamCallback {

    /**
     * Called for each chunk of the streaming response.
     *
     * @param chunk the text chunk from the LLM response
     * @param done true if this is the final chunk
     */
    void onChunk(String chunk, boolean done);

    /**
     * Called when an error occurs during streaming.
     *
     * @param error the error that occurred
     */
    default void onError(final Throwable error) {
        // Default implementation does nothing
    }

    /**
     * Called when the LLM HTTP call is being retried.
     *
     * @param operation log label for the operation (e.g., "chat", "streamChat")
     * @param attempt 1-based current attempt index that just failed
     * @param maxAttempts total attempts including the first
     * @param sleepMs milliseconds the client will sleep before the next attempt
     * @param cause the cause of the retry (RetryableHttpException or IOException)
     */
    default void onRetry(final String operation, final int attempt, final int maxAttempts, final long sleepMs, final Throwable cause) {
        // Default implementation does nothing
    }

    /**
     * Called when the request is waiting for a concurrency permit.
     *
     * @param reason short reason code, currently always "concurrency_limit"
     * @param elapsedMs milliseconds already spent waiting (0 on the first call)
     * @param timeoutMs maximum milliseconds the client will wait before giving up
     */
    default void onWaiting(final String reason, final long elapsedMs, final long timeoutMs) {
        // Default implementation does nothing
    }

    /**
     * Called when an internal silent fallback occurs (e.g., reasoning model token exhaustion).
     *
     * @param code short machine-readable warning code (e.g., "reasoning_token_exhausted")
     * @param detail human-readable detail (free-form)
     */
    default void onWarning(final String code, final String detail) {
        // Default implementation does nothing
    }
}
