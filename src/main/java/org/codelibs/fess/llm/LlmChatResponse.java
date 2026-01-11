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
 * Response object for LLM chat completion.
 *
 * @author FessProject
 */
public class LlmChatResponse {

    private String content;
    private String finishReason;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;
    private String model;

    /**
     * Default constructor.
     */
    public LlmChatResponse() {
    }

    /**
     * Creates a response with the specified content.
     *
     * @param content the response content
     */
    public LlmChatResponse(final String content) {
        this.content = content;
    }

    /**
     * Gets the response content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the response content.
     *
     * @param content the content
     */
    public void setContent(final String content) {
        this.content = content;
    }

    /**
     * Gets the finish reason.
     *
     * @return the finish reason
     */
    public String getFinishReason() {
        return finishReason;
    }

    /**
     * Sets the finish reason.
     *
     * @param finishReason the finish reason
     */
    public void setFinishReason(final String finishReason) {
        this.finishReason = finishReason;
    }

    /**
     * Gets the number of prompt tokens.
     *
     * @return the prompt tokens count
     */
    public Integer getPromptTokens() {
        return promptTokens;
    }

    /**
     * Sets the number of prompt tokens.
     *
     * @param promptTokens the prompt tokens count
     */
    public void setPromptTokens(final Integer promptTokens) {
        this.promptTokens = promptTokens;
    }

    /**
     * Gets the number of completion tokens.
     *
     * @return the completion tokens count
     */
    public Integer getCompletionTokens() {
        return completionTokens;
    }

    /**
     * Sets the number of completion tokens.
     *
     * @param completionTokens the completion tokens count
     */
    public void setCompletionTokens(final Integer completionTokens) {
        this.completionTokens = completionTokens;
    }

    /**
     * Gets the total number of tokens.
     *
     * @return the total tokens count
     */
    public Integer getTotalTokens() {
        return totalTokens;
    }

    /**
     * Sets the total number of tokens.
     *
     * @param totalTokens the total tokens count
     */
    public void setTotalTokens(final Integer totalTokens) {
        this.totalTokens = totalTokens;
    }

    /**
     * Gets the model name.
     *
     * @return the model name
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the model name.
     *
     * @param model the model name
     */
    public void setModel(final String model) {
        this.model = model;
    }
}
