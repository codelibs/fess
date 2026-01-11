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
import java.util.Map;

/**
 * Represents a message in a chat conversation.
 *
 * @author FessProject
 */
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The role identifier for user messages. */
    public static final String ROLE_USER = "user";

    /** The role identifier for assistant messages. */
    public static final String ROLE_ASSISTANT = "assistant";

    /** The unique identifier for this message. */
    private String id;

    /** The role of the message sender (user or assistant). */
    private String role;

    /** The content of the message. */
    private String content;

    /** The timestamp when the message was created. */
    private LocalDateTime timestamp;

    /** The list of sources referenced in this message. */
    private List<ChatSource> sources;

    /**
     * Default constructor.
     */
    public ChatMessage() {
        this.timestamp = LocalDateTime.now();
        this.sources = new ArrayList<>();
    }

    /**
     * Creates a new chat message with the specified role and content.
     *
     * @param role the message role
     * @param content the message content
     */
    public ChatMessage(final String role, final String content) {
        this();
        this.role = role;
        this.content = content;
    }

    /**
     * Creates a user message with the specified content.
     *
     * @param content the message content
     * @return a new user message
     */
    public static ChatMessage userMessage(final String content) {
        return new ChatMessage(ROLE_USER, content);
    }

    /**
     * Creates an assistant message with the specified content.
     *
     * @param content the message content
     * @return a new assistant message
     */
    public static ChatMessage assistantMessage(final String content) {
        return new ChatMessage(ROLE_ASSISTANT, content);
    }

    /**
     * Gets the message ID.
     *
     * @return the message ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the message ID.
     *
     * @param id the message ID
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Gets the message role.
     *
     * @return the message role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the message role.
     *
     * @param role the message role
     */
    public void setRole(final String role) {
        this.role = role;
    }

    /**
     * Gets the message content.
     *
     * @return the message content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the message content.
     *
     * @param content the message content
     */
    public void setContent(final String content) {
        this.content = content;
    }

    /**
     * Gets the message timestamp.
     *
     * @return the message timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the message timestamp.
     *
     * @param timestamp the message timestamp
     */
    public void setTimestamp(final LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the list of sources referenced in the message.
     *
     * @return the list of sources
     */
    public List<ChatSource> getSources() {
        return sources;
    }

    /**
     * Sets the list of sources referenced in the message.
     *
     * @param sources the list of sources
     */
    public void setSources(final List<ChatSource> sources) {
        this.sources = sources;
    }

    /**
     * Adds a source to the message.
     *
     * @param source the source to add
     */
    public void addSource(final ChatSource source) {
        if (sources == null) {
            sources = new ArrayList<>();
        }
        sources.add(source);
    }

    /**
     * Checks if this is a user message.
     *
     * @return true if this is a user message
     */
    public boolean isUser() {
        return ROLE_USER.equals(role);
    }

    /**
     * Checks if this is an assistant message.
     *
     * @return true if this is an assistant message
     */
    public boolean isAssistant() {
        return ROLE_ASSISTANT.equals(role);
    }

    /**
     * Represents a source document referenced in the chat response.
     */
    public static class ChatSource implements Serializable {

        private static final long serialVersionUID = 1L;

        /** The index of this source in the result list. */
        private int index;

        /** The title of the source document. */
        private String title;

        /** The URL of the source document. */
        private String url;

        /** The document ID. */
        private String docId;

        /** A snippet from the source document. */
        private String snippet;

        /**
         * Default constructor.
         */
        public ChatSource() {
        }

        /**
         * Creates a new chat source from a document map.
         *
         * @param index the source index
         * @param doc the document map containing source data
         */
        public ChatSource(final int index, final Map<String, Object> doc) {
            this.index = index;
            this.title = (String) doc.get("title");
            this.url = (String) doc.get("url");
            this.docId = (String) doc.get("doc_id");
            this.snippet = (String) doc.get("content_description");
        }

        /**
         * Gets the source index.
         *
         * @return the source index
         */
        public int getIndex() {
            return index;
        }

        /**
         * Sets the source index.
         *
         * @param index the source index
         */
        public void setIndex(final int index) {
            this.index = index;
        }

        /**
         * Gets the source title.
         *
         * @return the source title
         */
        public String getTitle() {
            return title;
        }

        /**
         * Sets the source title.
         *
         * @param title the source title
         */
        public void setTitle(final String title) {
            this.title = title;
        }

        /**
         * Gets the source URL.
         *
         * @return the source URL
         */
        public String getUrl() {
            return url;
        }

        /**
         * Sets the source URL.
         *
         * @param url the source URL
         */
        public void setUrl(final String url) {
            this.url = url;
        }

        /**
         * Gets the document ID.
         *
         * @return the document ID
         */
        public String getDocId() {
            return docId;
        }

        /**
         * Sets the document ID.
         *
         * @param docId the document ID
         */
        public void setDocId(final String docId) {
            this.docId = docId;
        }

        /**
         * Gets the source snippet.
         *
         * @return the source snippet
         */
        public String getSnippet() {
            return snippet;
        }

        /**
         * Sets the source snippet.
         *
         * @param snippet the source snippet
         */
        public void setSnippet(final String snippet) {
            this.snippet = snippet;
        }
    }
}
