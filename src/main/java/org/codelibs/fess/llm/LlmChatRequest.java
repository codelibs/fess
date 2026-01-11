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

import java.util.ArrayList;
import java.util.List;

/**
 * Request object for LLM chat completion.
 *
 * @author FessProject
 */
public class LlmChatRequest {

    private List<LlmMessage> messages = new ArrayList<>();
    private String model;
    private Integer maxTokens;
    private Double temperature;
    private boolean stream;

    /**
     * Default constructor.
     */
    public LlmChatRequest() {
    }

    /**
     * Adds a message to the request.
     *
     * @param message the message to add
     * @return this request for method chaining
     */
    public LlmChatRequest addMessage(final LlmMessage message) {
        messages.add(message);
        return this;
    }

    /**
     * Adds a system message to the request.
     *
     * @param content the message content
     * @return this request for method chaining
     */
    public LlmChatRequest addSystemMessage(final String content) {
        messages.add(LlmMessage.system(content));
        return this;
    }

    /**
     * Adds a user message to the request.
     *
     * @param content the message content
     * @return this request for method chaining
     */
    public LlmChatRequest addUserMessage(final String content) {
        messages.add(LlmMessage.user(content));
        return this;
    }

    /**
     * Adds an assistant message to the request.
     *
     * @param content the message content
     * @return this request for method chaining
     */
    public LlmChatRequest addAssistantMessage(final String content) {
        messages.add(LlmMessage.assistant(content));
        return this;
    }

    /**
     * Gets the messages in this request.
     *
     * @return the list of messages
     */
    public List<LlmMessage> getMessages() {
        return messages;
    }

    /**
     * Sets the messages in this request.
     *
     * @param messages the list of messages
     */
    public void setMessages(final List<LlmMessage> messages) {
        this.messages = messages;
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
     * @return this request for method chaining
     */
    public LlmChatRequest setModel(final String model) {
        this.model = model;
        return this;
    }

    /**
     * Gets the maximum tokens for the response.
     *
     * @return the maximum tokens
     */
    public Integer getMaxTokens() {
        return maxTokens;
    }

    /**
     * Sets the maximum tokens for the response.
     *
     * @param maxTokens the maximum tokens
     * @return this request for method chaining
     */
    public LlmChatRequest setMaxTokens(final Integer maxTokens) {
        this.maxTokens = maxTokens;
        return this;
    }

    /**
     * Gets the temperature parameter.
     *
     * @return the temperature
     */
    public Double getTemperature() {
        return temperature;
    }

    /**
     * Sets the temperature parameter.
     *
     * @param temperature the temperature
     * @return this request for method chaining
     */
    public LlmChatRequest setTemperature(final Double temperature) {
        this.temperature = temperature;
        return this;
    }

    /**
     * Checks if streaming is enabled.
     *
     * @return true if streaming is enabled
     */
    public boolean isStream() {
        return stream;
    }

    /**
     * Sets whether streaming is enabled.
     *
     * @param stream true to enable streaming
     * @return this request for method chaining
     */
    public LlmChatRequest setStream(final boolean stream) {
        this.stream = stream;
        return this;
    }
}
