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

import java.util.Objects;

import org.codelibs.fess.llm.LlmStreamCallback;

/**
 * Bridges {@link LlmStreamCallback} events from the LLM layer to a phase-aware
 * {@link ChatPhaseCallback}. Forwards onChunk/onError to an inner LlmStreamCallback
 * and forwards onRetry/onWaiting/onWarning to the phase callback together with
 * the phase name supplied at construction.
 */
public class PhaseAwareStreamCallback implements LlmStreamCallback {

    private final String phase;
    private final ChatPhaseCallback phaseCallback;
    private final LlmStreamCallback inner;

    /**
     * Creates a callback that forwards chunk/error events to {@code inner} and
     * retry/waiting/warning events to {@code phaseCallback} tagged with {@code phase}.
     *
     * @param phase the phase name to attach to phase callback events
     * @param phaseCallback the phase callback to forward retry/waiting/warning events to (may be null)
     * @param inner the LlmStreamCallback to forward chunk/error events to (never null)
     */
    public PhaseAwareStreamCallback(final String phase, final ChatPhaseCallback phaseCallback, final LlmStreamCallback inner) {
        this.phase = phase;
        this.phaseCallback = phaseCallback != null ? phaseCallback : ChatPhaseCallback.noOp();
        this.inner = Objects.requireNonNull(inner, "inner must not be null");
    }

    @Override
    public void onChunk(final String chunk, final boolean done) {
        inner.onChunk(chunk, done);
    }

    @Override
    public void onError(final Throwable error) {
        inner.onError(error);
    }

    @Override
    public void onRetry(final String operation, final int attempt, final int maxAttempts, final long sleepMs, final Throwable cause) {
        phaseCallback.onRetry(phase, operation, attempt, maxAttempts, sleepMs,
                cause != null ? cause.getClass().getSimpleName() : "unknown");
    }

    @Override
    public void onWaiting(final String reason, final long elapsedMs, final long timeoutMs) {
        phaseCallback.onWaiting(phase, reason, elapsedMs, timeoutMs);
    }

    @Override
    public void onWarning(final String code, final String detail) {
        phaseCallback.onWarning(phase, code, detail);
    }
}
