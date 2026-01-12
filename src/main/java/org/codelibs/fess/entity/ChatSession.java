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
package org.codelibs.fess.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a chat session containing conversation history.
 *
 * @author FessProject
 */
public class ChatSession implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The unique session identifier. */
    private String sessionId;

    /** The user ID associated with this session. */
    private String userId;

    /** The timestamp when the session was created. */
    private LocalDateTime createdAt;

    /** The timestamp when the session was last accessed. */
    private LocalDateTime lastAccessedAt;

    /** The list of messages in this session. */
    private List<ChatMessage> messages;

    /**
     * Default constructor.
     */
    public ChatSession() {
        this.sessionId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.lastAccessedAt = this.createdAt;
        this.messages = new ArrayList<>();
    }

    /**
     * Creates a new chat session for the specified user.
     *
     * @param userId the user ID
     */
    public ChatSession(final String userId) {
        this();
        this.userId = userId;
    }

    /**
     * Gets the session ID.
     *
     * @return the session ID
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the session ID.
     *
     * @param sessionId the session ID
     */
    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Gets the user ID.
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId the user ID
     */
    public void setUserId(final String userId) {
        this.userId = userId;
    }

    /**
     * Gets the creation timestamp.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     *
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the last accessed timestamp.
     *
     * @return the last accessed timestamp
     */
    public LocalDateTime getLastAccessedAt() {
        return lastAccessedAt;
    }

    /**
     * Sets the last accessed timestamp.
     *
     * @param lastAccessedAt the last accessed timestamp
     */
    public void setLastAccessedAt(final LocalDateTime lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    /**
     * Gets the list of messages in this session.
     *
     * @return the list of messages
     */
    public List<ChatMessage> getMessages() {
        return messages;
    }

    /**
     * Sets the list of messages in this session.
     *
     * @param messages the list of messages
     */
    public void setMessages(final List<ChatMessage> messages) {
        this.messages = messages;
    }

    /**
     * Adds a message to this session.
     *
     * @param message the message to add
     */
    public void addMessage(final ChatMessage message) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(message);
        this.lastAccessedAt = LocalDateTime.now();
    }

    /**
     * Adds a user message to this session.
     *
     * @param content the message content
     */
    public void addUserMessage(final String content) {
        addMessage(ChatMessage.userMessage(content));
    }

    /**
     * Adds an assistant message to this session.
     *
     * @param content the message content
     */
    public void addAssistantMessage(final String content) {
        addMessage(ChatMessage.assistantMessage(content));
    }

    /**
     * Updates the last accessed timestamp to the current time.
     */
    public void touch() {
        this.lastAccessedAt = LocalDateTime.now();
    }

    /**
     * Gets the number of messages in this session.
     *
     * @return the message count
     */
    public int getMessageCount() {
        return messages != null ? messages.size() : 0;
    }

    /**
     * Clears all messages from this session.
     */
    public void clearMessages() {
        if (messages != null) {
            messages.clear();
        }
        this.lastAccessedAt = LocalDateTime.now();
    }

    /**
     * Trims the message history to keep only the most recent messages.
     *
     * @param maxMessages the maximum number of messages to keep
     */
    public void trimHistory(final int maxMessages) {
        if (messages != null && messages.size() > maxMessages) {
            final int toRemove = messages.size() - maxMessages;
            messages.subList(0, toRemove).clear();
        }
    }
}
