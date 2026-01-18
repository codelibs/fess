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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents a chat session containing conversation history.
 *
 * @author FessProject
 */
public class ChatSession {

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

    /** Lock object for thread-safe message operations. */
    private final Object messagesLock = new Object();

    /**
     * Default constructor.
     */
    public ChatSession() {
        this.sessionId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.lastAccessedAt = this.createdAt;
        this.messages = new CopyOnWriteArrayList<>();
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
     * Returns a copy of the message list in this session.
     *
     * @return a new list containing all messages
     */
    public List<ChatMessage> getMessages() {
        synchronized (messagesLock) {
            if (messages == null) {
                return new ArrayList<>();
            }
            return new ArrayList<>(messages);
        }
    }

    /**
     * Sets the message list for this session.
     *
     * @param messages the messages to set
     */
    public void setMessages(final List<ChatMessage> messages) {
        synchronized (messagesLock) {
            if (messages == null) {
                this.messages = null;
            } else if (messages instanceof CopyOnWriteArrayList) {
                this.messages = messages;
            } else {
                this.messages = new CopyOnWriteArrayList<>(messages);
            }
        }
    }

    /**
     * Adds a message to this session and updates the last accessed timestamp.
     *
     * @param message the message to add
     */
    public void addMessage(final ChatMessage message) {
        synchronized (messagesLock) {
            if (messages == null) {
                messages = new CopyOnWriteArrayList<>();
            }
            messages.add(message);
        }
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
     * Returns the number of messages in this session.
     *
     * @return the message count
     */
    public int getMessageCount() {
        synchronized (messagesLock) {
            return messages != null ? messages.size() : 0;
        }
    }

    /**
     * Clears all messages in this session and updates the last accessed timestamp.
     */
    public void clearMessages() {
        synchronized (messagesLock) {
            if (messages != null) {
                messages.clear();
            }
        }
        this.lastAccessedAt = LocalDateTime.now();
    }

    /**
     * Trims the message history to keep only the most recent messages.
     *
     * @param maxMessages the maximum number of messages to retain
     */
    public void trimHistory(final int maxMessages) {
        synchronized (messagesLock) {
            if (messages != null && messages.size() > maxMessages) {
                final List<ChatMessage> trimmed = new ArrayList<>(messages.subList(messages.size() - maxMessages, messages.size()));
                messages.clear();
                messages.addAll(trimmed);
            }
        }
    }
}
