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

import java.util.Collections;
import java.util.List;

/**
 * Result of relevance evaluation for search results.
 */
public class RelevanceEvaluationResult {

    private final List<String> relevantDocIds;
    private final List<Integer> relevantIndexes;
    private final boolean hasRelevantResults;

    private RelevanceEvaluationResult(final List<String> relevantDocIds, final List<Integer> relevantIndexes,
            final boolean hasRelevantResults) {
        this.relevantDocIds = relevantDocIds != null ? Collections.unmodifiableList(relevantDocIds) : Collections.emptyList();
        this.relevantIndexes = relevantIndexes != null ? Collections.unmodifiableList(relevantIndexes) : Collections.emptyList();
        this.hasRelevantResults = hasRelevantResults;
    }

    /**
     * Returns the list of relevant document IDs.
     *
     * @return the relevant document IDs
     */
    public List<String> getRelevantDocIds() {
        return relevantDocIds;
    }

    /**
     * Returns the list of relevant indexes (1-based) from search results.
     *
     * @return the relevant indexes
     */
    public List<Integer> getRelevantIndexes() {
        return relevantIndexes;
    }

    /**
     * Returns whether relevant results were found.
     *
     * @return true if relevant results exist
     */
    public boolean isHasRelevantResults() {
        return hasRelevantResults;
    }

    /**
     * Creates a result with relevant documents found.
     *
     * @param relevantDocIds list of relevant document IDs
     * @param relevantIndexes list of relevant indexes (1-based) from search results
     * @return evaluation result
     */
    public static RelevanceEvaluationResult withRelevantDocs(final List<String> relevantDocIds, final List<Integer> relevantIndexes) {
        final boolean hasRelevant = relevantDocIds != null && !relevantDocIds.isEmpty();
        return new RelevanceEvaluationResult(relevantDocIds, relevantIndexes, hasRelevant);
    }

    /**
     * Creates a result with no relevant documents found.
     *
     * @return evaluation result with no relevant documents
     */
    public static RelevanceEvaluationResult noRelevantResults() {
        return new RelevanceEvaluationResult(Collections.emptyList(), Collections.emptyList(), false);
    }

    /**
     * Creates a fallback result that includes all documents as relevant.
     * Used when evaluation fails and we want to include all search results.
     *
     * @param allDocIds all document IDs from search results
     * @return evaluation result with all documents marked as relevant
     */
    public static RelevanceEvaluationResult fallbackAllRelevant(final List<String> allDocIds) {
        return new RelevanceEvaluationResult(allDocIds, Collections.emptyList(), !allDocIds.isEmpty());
    }

    @Override
    public String toString() {
        return "RelevanceEvaluationResult{relevantDocIds=" + relevantDocIds + ", relevantIndexes=" + relevantIndexes
                + ", hasRelevantResults=" + hasRelevantResults + "}";
    }
}
