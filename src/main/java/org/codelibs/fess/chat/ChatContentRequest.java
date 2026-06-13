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

import java.util.List;
import java.util.Map;

/**
 * Request object for {@link ChatContentFetcher}. Carries the relevant document
 * ids (in search-score order), the search-phase result maps (which contain
 * {@code content_length} and highlight snippets), and the query used to
 * highlight large documents.
 */
public class ChatContentRequest {

    /** Relevant document ids, in search-score order. */
    protected final List<String> docIds;

    /** Search-phase result maps (with content_length); may be empty for the summary path. */
    protected final List<Map<String, Object>> searchResults;

    /** Query used to highlight large documents; blank/null disables highlighting (full fetch). */
    protected final String query;

    /**
     * Creates a new request.
     *
     * @param docIds relevant document ids in score order
     * @param searchResults search-phase result maps (may be empty)
     * @param query query used to highlight large documents (may be null/blank)
     */
    public ChatContentRequest(final List<String> docIds, final List<Map<String, Object>> searchResults, final String query) {
        this.docIds = docIds;
        this.searchResults = searchResults;
        this.query = query;
    }

    /**
     * Returns the relevant document ids.
     *
     * @return the document ids
     */
    public List<String> getDocIds() {
        return docIds;
    }

    /**
     * Returns the search-phase result maps.
     *
     * @return the search results (may be empty)
     */
    public List<Map<String, Object>> getSearchResults() {
        return searchResults;
    }

    /**
     * Returns the highlight query.
     *
     * @return the query (may be null/blank)
     */
    public String getQuery() {
        return query;
    }
}
