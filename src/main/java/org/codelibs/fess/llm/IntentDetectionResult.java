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
 * Represents the result of intent detection from user input.
 * Contains the detected intent type, Lucene query, and other metadata.
 */
public class IntentDetectionResult {

    private final ChatIntent intent;
    private final String query;
    private final String documentUrl;
    private final String reasoning;

    private IntentDetectionResult(final ChatIntent intent, final String query, final String documentUrl, final String reasoning) {
        this.intent = intent;
        this.query = query;
        this.documentUrl = documentUrl;
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
     * Returns the Lucene query string for search.
     *
     * @return the Lucene query string, or null if not available
     */
    public String getQuery() {
        return query;
    }

    /**
     * Returns the document URL for summary intent.
     *
     * @return the document URL
     */
    public String getDocumentUrl() {
        return documentUrl;
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
     * Creates a search intent result with a Lucene query.
     *
     * @param query the Lucene query string
     * @param reasoning the detection reasoning
     * @return the search intent result
     */
    public static IntentDetectionResult search(final String query, final String reasoning) {
        return new IntentDetectionResult(ChatIntent.SEARCH, query, null, reasoning);
    }

    /**
     * Creates a summary intent result for a specific document.
     *
     * @param documentUrl the document URL to summarize
     * @param reasoning the detection reasoning
     * @return the summary intent result
     */
    public static IntentDetectionResult summary(final String documentUrl, final String reasoning) {
        return new IntentDetectionResult(ChatIntent.SUMMARY, null, documentUrl, reasoning);
    }

    /**
     * Creates a FAQ intent result with a Lucene query.
     *
     * @param query the Lucene query string
     * @param reasoning the detection reasoning
     * @return the FAQ intent result
     */
    public static IntentDetectionResult faq(final String query, final String reasoning) {
        return new IntentDetectionResult(ChatIntent.FAQ, query, null, reasoning);
    }

    /**
     * Creates an unclear intent result when intent cannot be determined.
     *
     * @param reasoning the detection reasoning
     * @return the unclear intent result
     */
    public static IntentDetectionResult unclear(final String reasoning) {
        return new IntentDetectionResult(ChatIntent.UNCLEAR, null, null, reasoning);
    }

    /**
     * Creates a fallback search intent when intent detection fails.
     *
     * @param originalMessage the original user message
     * @return the fallback search intent result
     */
    public static IntentDetectionResult fallbackSearch(final String originalMessage) {
        return new IntentDetectionResult(ChatIntent.SEARCH, originalMessage, null, "Fallback: using original message as query");
    }

    @Override
    public String toString() {
        return "IntentDetectionResult{intent=" + intent + ", query=" + query + ", documentUrl=" + documentUrl + ", reasoning=" + reasoning
                + "}";
    }
}
