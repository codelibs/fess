/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

public class SearchResult {

    protected final List<Map<String, Object>> documentList;
    protected final long allRecordCount;
    protected final String allRecordCountRelation;
    protected final long queryTime;
    protected final boolean partialResults;
    protected final FacetResponse facetResponse;

    SearchResult(final List<Map<String, Object>> documentList, final long allRecordCount, final String allRecordCountRelation,
            final long queryTime, final boolean partialResults, final FacetResponse facetResponse) {
        this.documentList = documentList;
        this.allRecordCount = allRecordCount;
        this.allRecordCountRelation = allRecordCountRelation;
        this.queryTime = queryTime;
        this.partialResults = partialResults;
        this.facetResponse = facetResponse;
    }

    public List<Map<String, Object>> getDocumentList() {
        return documentList;
    }

    public long getAllRecordCount() {
        return allRecordCount;
    }

    public String getAllRecordCountRelation() {
        return allRecordCountRelation;
    }

    public long getQueryTime() {
        return queryTime;
    }

    public boolean isPartialResults() {
        return partialResults;
    }

    public FacetResponse getFacetResponse() {
        return facetResponse;
    }

    public static SearchResultBuilder create() {
        return new SearchResultBuilder();
    }

    @Override
    public String toString() {
        return "SearchResult [documentList=" + documentList + ", allRecordCount=" + allRecordCount + ", allRecordCountRelation="
                + allRecordCountRelation + ", queryTime=" + queryTime + ", partialResults=" + partialResults + ", facetResponse="
                + facetResponse + "]";
    }

    static class SearchResultBuilder {

        private long allRecordCount;
        private String allRecordCountRelation = Relation.GREATER_THAN_OR_EQUAL_TO.toString();
        private long queryTime;
        private boolean partialResults;
        private FacetResponse facetResponse;
        private final List<Map<String, Object>> documentList = new ArrayList<>();

        public SearchResultBuilder allRecordCount(final long allRecordCount) {
            this.allRecordCount = allRecordCount;
            return this;
        }

        public SearchResultBuilder allRecordCountRelation(final String allRecordCountRelation) {
            this.allRecordCountRelation = allRecordCountRelation;
            return this;
        }

        public SearchResultBuilder queryTime(final long queryTime) {
            this.queryTime = queryTime;
            return this;
        }

        public SearchResultBuilder partialResults(final boolean partialResults) {
            this.partialResults = partialResults;
            return this;
        }

        public SearchResultBuilder addDocument(final Map<String, Object> doc) {
            documentList.add(doc);
            return this;
        }

        public SearchResultBuilder facetResponse(final FacetResponse facetResponse) {
            this.facetResponse = facetResponse;
            return this;
        }

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
