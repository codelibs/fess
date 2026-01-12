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

import java.util.Collections;
import java.util.List;

/**
 * Result of intent detection from a user's chat message.
 */
public class IntentDetectionResult {

    private final ChatIntent intent;
    private final List<String> keywords;
    private final String documentId;
    private final String reasoning;

    private IntentDetectionResult(final ChatIntent intent, final List<String> keywords, final String documentId, final String reasoning) {
        this.intent = intent;
        this.keywords = keywords != null ? Collections.unmodifiableList(keywords) : Collections.emptyList();
        this.documentId = documentId;
        this.reasoning = reasoning;
    }

    /**
     * Returns the detected intent type.
     *
     * @return the intent type
     */
    public ChatIntent getIntent() {
        return intent;
    }

    /**
     * Returns the extracted keywords for search.
     *
     * @return the keywords list
     */
    public List<String> getKeywords() {
        return keywords;
    }

    /**
     * Returns the document ID for summary intent.
     *
     * @return the document ID
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * Returns the reasoning for the detected intent.
     *
     * @return the reasoning
     */
    public String getReasoning() {
        return reasoning;
    }

    /**
     * Creates a search intent result with extracted keywords.
     *
     * @param keywords the search keywords
     * @param reasoning the detection reasoning
     * @return the search intent result
     */
    public static IntentDetectionResult search(final List<String> keywords, final String reasoning) {
        return new IntentDetectionResult(ChatIntent.SEARCH, keywords, null, reasoning);
    }

    /**
     * Creates a summary intent result for a specific document.
     *
     * @param documentId the document ID to summarize
     * @param reasoning the detection reasoning
     * @return the summary intent result
     */
    public static IntentDetectionResult summary(final String documentId, final String reasoning) {
        return new IntentDetectionResult(ChatIntent.SUMMARY, null, documentId, reasoning);
    }

    /**
     * Creates a FAQ intent result with potential keywords.
     *
     * @param keywords the search keywords
     * @param reasoning the detection reasoning
     * @return the FAQ intent result
     */
    public static IntentDetectionResult faq(final List<String> keywords, final String reasoning) {
        return new IntentDetectionResult(ChatIntent.FAQ, keywords, null, reasoning);
    }

    /**
     * Creates a general chat intent result.
     *
     * @param reasoning the detection reasoning
     * @return the chat intent result
     */
    public static IntentDetectionResult chat(final String reasoning) {
        return new IntentDetectionResult(ChatIntent.CHAT, null, null, reasoning);
    }

    /**
     * Creates a fallback search intent when intent detection fails.
     *
     * @param originalMessage the original user message
     * @return the fallback search intent result
     */
    public static IntentDetectionResult fallbackSearch(final String originalMessage) {
        return new IntentDetectionResult(ChatIntent.SEARCH, List.of(originalMessage), null, "Fallback: using original message as keyword");
    }

    @Override
    public String toString() {
        return "IntentDetectionResult{intent=" + intent + ", keywords=" + keywords + ", documentId=" + documentId + ", reasoning="
                + reasoning + "}";
    }
}
