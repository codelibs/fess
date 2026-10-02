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
 * Token usage of one LLM call, as reported by the provider.
 *
 * <p>Every value is optional: a provider may report only some of the counts, or none. A synchronous
 * call reports its usage through {@link LlmChatResponse}; a streaming call reports it through
 * {@link LlmStreamCallback#onUsage(LlmUsage)}.</p>
 *
 * @param promptTokens the number of tokens in the prompt, or null if not reported
 * @param completionTokens the number of tokens in the completion, or null if not reported
 * @param totalTokens the total number of tokens, or null if not reported
 * @param model the name of the model that served the call, or null if not reported
 */
public record LlmUsage(Integer promptTokens, Integer completionTokens, Integer totalTokens, String model) {

    /**
     * Creates the usage reported by a synchronous chat response.
     *
     * @param response the chat response (may be null)
     * @return the usage, or null if the response reports neither a token count nor a model
     */
    public static LlmUsage of(final LlmChatResponse response) {
        if (response == null) {
            return null;
        }
        final LlmUsage usage =
                new LlmUsage(response.getPromptTokens(), response.getCompletionTokens(), response.getTotalTokens(), response.getModel());
        return usage.isEmpty() ? null : usage;
    }

    /**
     * Checks whether this usage reports nothing.
     *
     * @return true if no token count and no model is reported
     */
    public boolean isEmpty() {
        return promptTokens == null && completionTokens == null && totalTokens == null && (model == null || model.isBlank());
    }
}
