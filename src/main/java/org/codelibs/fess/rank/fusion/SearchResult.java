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
package org.codelibs.fess.rank.fusion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.TotalHits.Relation;
import org.codelibs.fess.util.FacetResponse;

/**
 * Represents the result of a search operation in the rank fusion system.
 *
 * This class encapsulates all the information returned from a search query,
 * including the list of matching documents, total record count, query execution
 * time, facet information, and metadata about the search results.
 */
public class SearchResult {

    /** The list of documents returned by the search query. */
    protected final List<Map<String, Object>> documentList;

    /** The total number of records that match the search criteria. */
    protected final long allRecordCount;

    /** The relation type indicating how the record count should be interpreted (e.g., "eq", "gte"). */
    protected final String allRecordCountRelation;

    /** The time taken to execute the search query in milliseconds. */
    protected final long queryTime;

    /** Flag indicating whether the search results are partial due to timeout or other constraints. */
    protected final boolean partialResults;

    /** The facet response containing aggregated facet information for the search results. */
    protected final FacetResponse facetResponse;

    /**
     * Constructs a new SearchResult with the specified parameters.
     *
     * @param documentList The list of documents returned by the search
     * @param allRecordCount The total number of matching records
     * @param allRecordCountRelation The relation type for the record count
     * @param queryTime The time taken to execute the query in milliseconds
     * @param partialResults Whether the results are partial
     * @param facetResponse The facet response containing aggregated data
     */
    SearchResult(final List<Map<String, Object>> documentList, final long allRecordCount, final String allRecordCountRelation,
            final long queryTime, final boolean partialResults, final FacetResponse facetResponse) {
        this.documentList = documentList;
        this.allRecordCount = allRecordCount;
        this.allRecordCountRelation = allRecordCountRelation;
        this.queryTime = queryTime;
        this.partialResults = partialResults;
        this.facetResponse = facetResponse;
    }

    /**
     * Gets the list of documents returned by the search query.
     *
     * @return The list of search result documents
     */
    public List<Map<String, Object>> getDocumentList() {
        return documentList;
    }

    /**
     * Gets the total number of records that match the search criteria.
     *
     * @return The total record count
     */
    public long getAllRecordCount() {
        return allRecordCount;
    }

    /**
     * Gets the relation type indicating how the record count should be interpreted.
     *
     * @return The record count relation (e.g., "eq" for exact, "gte" for greater than or equal)
     */
    public String getAllRecordCountRelation() {
        return allRecordCountRelation;
    }

    /**
     * Gets the time taken to execute the search query.
     *
     * @return The query execution time in milliseconds
     */
    public long getQueryTime() {
        return queryTime;
    }

    /**
     * Checks whether the search results are partial due to timeout or other constraints.
     *
     * @return true if the results are partial, false if complete
     */
    public boolean isPartialResults() {
        return partialResults;
    }

    /**
     * Gets the facet response containing aggregated facet information.
     *
     * @return The facet response, or null if no facets were requested
     */
    public FacetResponse getFacetResponse() {
        return facetResponse;
    }

    /**
     * Creates a new SearchResultBuilder for constructing SearchResult instances.
     *
     * @return A new SearchResultBuilder instance
     */
    public static SearchResultBuilder create() {
        return new SearchResultBuilder();
    }

    @Override
    public String toString() {
        return "SearchResult [documentList=" + documentList + ", allRecordCount=" + allRecordCount + ", allRecordCountRelation="
                + allRecordCountRelation + ", queryTime=" + queryTime + ", partialResults=" + partialResults + ", facetResponse="
                + facetResponse + "]";
    }

    /**
     * Builder class for constructing SearchResult instances using the builder pattern.
     *
     * This builder provides a fluent interface for setting the various properties
     * of a SearchResult before creating the final immutable instance.
     */
    static class SearchResultBuilder {

        /** The total number of records that match the search criteria. */
        private long allRecordCount;

        /** The relation type for the record count, defaults to greater than or equal to. */
        private String allRecordCountRelation = Relation.GREATER_THAN_OR_EQUAL_TO.toString();

        /** The time taken to execute the search query in milliseconds. */
        private long queryTime;

        /** Flag indicating whether the search results are partial. */
        private boolean partialResults;

        /** The facet response containing aggregated facet information. */
        private FacetResponse facetResponse;

        /** The list of documents to be included in the search result. */
        private final List<Map<String, Object>> documentList = new ArrayList<>();

        /**
         * Sets the total number of records that match the search criteria.
         *
         * @param allRecordCount The total record count
         * @return This builder instance for method chaining
         */
        public SearchResultBuilder allRecordCount(final long allRecordCount) {
            this.allRecordCount = allRecordCount;
            return this;
        }

        /**
         * Sets the relation type for the record count.
         *
         * @param allRecordCountRelation The record count relation (e.g., "eq", "gte")
         * @return This builder instance for method chaining
         */
        public SearchResultBuilder allRecordCountRelation(final String allRecordCountRelation) {
            this.allRecordCountRelation = allRecordCountRelation;
            return this;
        }

        /**
         * Sets the time taken to execute the search query.
         *
         * @param queryTime The query execution time in milliseconds
         * @return This builder instance for method chaining
         */
        public SearchResultBuilder queryTime(final long queryTime) {
            this.queryTime = queryTime;
            return this;
        }

        /**
         * Sets whether the search results are partial.
         *
         * @param partialResults true if the results are partial, false if complete
         * @return This builder instance for method chaining
         */
        public SearchResultBuilder partialResults(final boolean partialResults) {
            this.partialResults = partialResults;
            return this;
        }

        /**
         * Adds a document to the search result.
         *
         * @param doc The document to add to the result list
         * @return This builder instance for method chaining
         */
        public SearchResultBuilder addDocument(final Map<String, Object> doc) {
            documentList.add(doc);
            return this;
        }

        /**
         * Sets the facet response containing aggregated facet information.
         *
         * @param facetResponse The facet response
         * @return This builder instance for method chaining
         */
        public SearchResultBuilder facetResponse(final FacetResponse facetResponse) {
            this.facetResponse = facetResponse;
            return this;
        }

        /**
         * Builds and returns the final SearchResult instance.
         *
         * @return A new SearchResult instance with the configured properties
         */
        public SearchResult build() {
            return new SearchResult(documentList, //
                    allRecordCount, //
                    allRecordCountRelation, //
                    queryTime, //
                    partialResults, //
                    facetResponse);
        }
    }
}
