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
 * Collects the LLM usage of one chat request: the number of LLM calls, the token counts the
 * provider reported for them, the model and the detected intent.
 *
 * <p>A collector is bound to the request thread with {@link #start()} and unbound by
 * {@link #close()}, so it is used with try-with-resources around the chat call:</p>
 *
 * <pre>
 * try (LlmUsageCollector usage = LlmUsageCollector.start()) {
 *     chatClient.chat(...);
 *     ... usage.getTotalTokens() ...
 * }
 * </pre>
 *
 * <p>{@link AbstractLlmClient} records each LLM call into the collector bound to the calling
 * thread. A streaming call may report its usage from another thread, so the collector is
 * captured on the calling thread and the recording methods are thread-safe.</p>
 *
 * <p>Token counts are only available when the LLM client reports them; a call without a
 * reported usage is still counted as a call.</p>
 */
public class LlmUsageCollector implements AutoCloseable {

    private static final ThreadLocal<LlmUsageCollector> CURRENT = new ThreadLocal<>();

    /** The collector that was bound before this one, restored on close. */
    private final LlmUsageCollector previous;

    private int calls;

    private long promptTokens;

    private long completionTokens;

    private long totalTokens;

    private boolean promptTokensReported;

    private boolean completionTokensReported;

    private boolean totalTokensReported;

    private String model;

    private String intent;

    /**
     * Creates a collector that is not bound to any thread.
     */
    public LlmUsageCollector() {
        this(null);
    }

    private LlmUsageCollector(final LlmUsageCollector previous) {
        this.previous = previous;
    }

    /**
     * Creates a collector and binds it to the current thread until it is closed.
     *
     * @return the new collector
     */
    public static LlmUsageCollector start() {
        final LlmUsageCollector collector = new LlmUsageCollector(CURRENT.get());
        CURRENT.set(collector);
        return collector;
    }

    /**
     * Gets the collector bound to the current thread.
     *
     * @return the collector, or null if none is bound
     */
    public static LlmUsageCollector current() {
        return CURRENT.get();
    }

    /**
     * Unbinds this collector from the current thread and restores the collector bound before it.
     * The collected values stay readable.
     */
    @Override
    public void close() {
        if (CURRENT.get() != this) {
            return;
        }
        if (previous != null) {
            CURRENT.set(previous);
        } else {
            CURRENT.remove();
        }
    }

    /**
     * Counts one LLM call.
     */
    public synchronized void recordCall() {
        calls++;
    }

    /**
     * Adds the usage of one LLM call. The call itself is counted by {@link #recordCall()}.
     * A total that is not reported is taken as the sum of the prompt and completion tokens
     * when both are reported.
     *
     * @param usage the usage (ignored if null)
     */
    public synchronized void recordUsage(final LlmUsage usage) {
        if (usage == null) {
            return;
        }
        final Integer prompt = valid(usage.promptTokens());
        final Integer completion = valid(usage.completionTokens());
        Long total = usage.totalTokens() != null && usage.totalTokens() >= 0 ? Long.valueOf(usage.totalTokens()) : null;
        if (total == null && prompt != null && completion != null) {
            total = (long) prompt + completion;
        }
        if (prompt != null) {
            promptTokens += prompt;
            promptTokensReported = true;
        }
        if (completion != null) {
            completionTokens += completion;
            completionTokensReported = true;
        }
        if (total != null) {
            totalTokens += total;
            totalTokensReported = true;
        }
        if (usage.model() != null && !usage.model().isBlank()) {
            model = usage.model();
        }
    }

    /**
     * Counts one synchronous LLM call and adds the usage its response reports.
     *
     * @param response the response of the call (may be null)
     */
    public void recordResponse(final LlmChatResponse response) {
        recordCall();
        recordUsage(LlmUsage.of(response));
    }

    /**
     * Records the intent detected for the request. Only the first intent is kept.
     *
     * @param intent the intent (ignored if null)
     */
    public synchronized void recordIntent(final ChatIntent intent) {
        if (this.intent == null && intent != null) {
            this.intent = intent.getValue();
        }
    }

    private static Integer valid(final Integer value) {
        return value != null && value >= 0 ? value : null;
    }

    /**
     * Gets the number of LLM calls.
     *
     * @return the number of calls
     */
    public synchronized int getLlmCalls() {
        return calls;
    }

    /**
     * Gets the sum of the reported prompt tokens.
     *
     * @return the sum, or null if no call reported prompt tokens
     */
    public synchronized Long getPromptTokens() {
        return promptTokensReported ? promptTokens : null;
    }

    /**
     * Gets the sum of the reported completion tokens.
     *
     * @return the sum, or null if no call reported completion tokens
     */
    public synchronized Long getCompletionTokens() {
        return completionTokensReported ? completionTokens : null;
    }

    /**
     * Gets the sum of the reported total tokens.
     *
     * @return the sum, or null if no call reported a total (or both prompt and completion tokens)
     */
    public synchronized Long getTotalTokens() {
        return totalTokensReported ? totalTokens : null;
    }

    /**
     * Gets the model reported by the last call that reported one.
     *
     * @return the model, or null if no call reported it
     */
    public synchronized String getModel() {
        return model;
    }

    /**
     * Gets the intent detected for the request.
     *
     * @return the intent value (e.g. "search"), or null if no intent was detected
     */
    public synchronized String getIntent() {
        return intent;
    }
}
