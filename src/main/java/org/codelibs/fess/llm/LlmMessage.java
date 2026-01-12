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

import java.io.Serializable;

/**
 * Represents a message in a chat conversation.
 *
 * @author FessProject
 */
public class LlmMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The role identifier for system messages. */
    public static final String ROLE_SYSTEM = "system";

    /** The role identifier for user messages. */
    public static final String ROLE_USER = "user";

    /** The role identifier for assistant messages. */
    public static final String ROLE_ASSISTANT = "assistant";

    /** The role of the message sender. */
    private String role;

    /** The content of the message. */
    private String content;

    /**
     * Default constructor.
     */
    public LlmMessage() {
    }

    /**
     * Creates a new message with the specified role and content.
     *
     * @param role the message role
     * @param content the message content
     */
    public LlmMessage(final String role, final String content) {
        this.role = role;
        this.content = content;
    }

    /**
     * Creates a system message with the specified content.
     *
     * @param content the message content
     * @return a new system message
     */
    public static LlmMessage system(final String content) {
        return new LlmMessage(ROLE_SYSTEM, content);
    }

    /**
     * Creates a user message with the specified content.
     *
     * @param content the message content
     * @return a new user message
     */
    public static LlmMessage user(final String content) {
        return new LlmMessage(ROLE_USER, content);
    }

    /**
     * Creates an assistant message with the specified content.
     *
     * @param content the message content
     * @return a new assistant message
     */
    public static LlmMessage assistant(final String content) {
        return new LlmMessage(ROLE_ASSISTANT, content);
    }

    /**
     * Gets the message role.
     *
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the message role.
     *
     * @param role the role
     */
    public void setRole(final String role) {
        this.role = role;
    }

    /**
     * Gets the message content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the message content.
     *
     * @param content the content
     */
    public void setContent(final String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "LlmMessage{role='" + role + "', content='"
                + (content != null && content.length() > 50 ? content.substring(0, 50) + "..." : content) + "'}";
    }
}
