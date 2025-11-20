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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.lucene.search.TotalHits.Relation;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.rank.fusion.SearchResult.SearchResultBuilder;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.optional.OptionalThing;

/**
 * Error handling tests for RankFusionProcessor.
 * Tests exception handling improvements including InterruptedException and ExecutionException.
 */
public class RankFusionProcessorErrorHandlingTest extends UnitFessTestCase {

    private static final String ID_FIELD = "_id";

    /**
     * Test handling of searcher that throws RuntimeException.
     */
    public void test_searcherThrowsRuntimeException() throws Exception {
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(100));
            processor.register(new ExceptionThrowingSearcher(new RuntimeException("Test exception")));
            processor.init();

            // Should handle exception gracefully and return results from working searcher
            final List<Map<String, Object>> results = processor.search("*",
                    new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());
            assertNotNull(results);
        }
    }

    /**
     * Test handling of searcher that throws FessSystemException.
     */
    public void test_searcherThrowsFessSystemException() throws Exception {
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(100));
            processor.register(new ExceptionThrowingSearcher(new FessSystemException("System error")));
            processor.init();

            // Should handle exception gracefully
            final List<Map<String, Object>> results = processor.search("*",
                    new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());
            assertNotNull(results);
        }
    }

    /**
     * Test handling of searcher that returns null documents.
     */
    public void test_searcherReturnsNullDocuments() throws Exception {
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(100));
            processor.register(new NullDocumentSearcher());
            processor.init();

            // Should handle null documents gracefully
            final List<Map<String, Object>> results = processor.search("*",
                    new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());
            assertNotNull(results);
        }
    }

    /**
     * Test handling of documents with missing ID field.
     */
    public void test_documentsWithMissingIdField() throws Exception {
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(100));
            processor.register(new NoIdFieldSearcher());
            processor.init();

            // Should skip documents without ID field
            final List<Map<String, Object>> results = processor.search("*",
                    new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());
            assertNotNull(results);
        }
    }

    /**
     * Test handling of documents with null ID field.
     */
    public void test_documentsWithNullIdField() throws Exception {
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(100));
            processor.register(new NullIdFieldSearcher());
            processor.init();

            // Should skip documents with null ID
            final List<Map<String, Object>> results = processor.search("*",
                    new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());
            assertNotNull(results);
        }
    }

    /**
     * Test handling of documents with non-string ID field.
     */
    public void test_documentsWithNonStringIdField() throws Exception {
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(100));
            processor.register(new NumericIdFieldSearcher());
            processor.init();

            // Should skip documents with non-string ID (pattern matching fails)
            final List<Map<String, Object>> results = processor.search("*",
                    new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());
            assertNotNull(results);
        }
    }

    /**
     * Test that all searchers failing still returns empty result gracefully.
     */
    public void test_allSearchersFail() throws Exception {
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new ExceptionThrowingSearcher(new RuntimeException("Main searcher failed")));
            processor.init();

            // Should return empty results without crashing
            final List<Map<String, Object>> results = processor.search("*",
                    new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());
            assertNotNull(results);
        }
    }

    /**
     * Test handling of very slow searcher (simulates timeout scenario).
     */
    public void test_slowSearcher() throws Exception {
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(100));
            processor.register(new SlowSearcher(100)); // 100ms delay
            processor.init();

            // Should complete even with slow searcher
            final long startTime = System.currentTimeMillis();
            final List<Map<String, Object>> results = processor.search("*",
                    new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());
            final long elapsed = System.currentTimeMillis() - startTime;

            assertNotNull(results);
            // Verify it doesn't take too long (parallel execution should help)
            assertTrue("Search took too long: " + elapsed + "ms", elapsed < 5000);
        }
    }

    /**
     * Searcher that throws an exception during search.
     */
    static class ExceptionThrowingSearcher extends RankFusionSearcher {
        private final RuntimeException exception;

        ExceptionThrowingSearcher(RuntimeException exception) {
            this.exception = exception;
        }

        @Override
        protected SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean) {
            throw exception;
        }
    }

    /**
     * Searcher that returns documents without ID field.
     */
    static class NoIdFieldSearcher extends RankFusionSearcher {
        @Override
        protected SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean) {
            SearchResultBuilder builder = SearchResult.create();
            for (int i = 0; i < 10; i++) {
                Map<String, Object> doc = new HashMap<>();
                // Intentionally don't add ID field
                doc.put("title", "Document " + i);
                builder.addDocument(doc);
            }
            builder.allRecordCount(10);
            builder.allRecordCountRelation(Relation.EQUAL_TO.toString());
            return builder.build();
        }
    }

    /**
     * Searcher that returns documents with null ID field.
     */
    static class NullIdFieldSearcher extends RankFusionSearcher {
        @Override
        protected SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean) {
            SearchResultBuilder builder = SearchResult.create();
            for (int i = 0; i < 10; i++) {
                Map<String, Object> doc = new HashMap<>();
                doc.put(ID_FIELD, null); // Explicitly null
                doc.put("title", "Document " + i);
                builder.addDocument(doc);
            }
            builder.allRecordCount(10);
            builder.allRecordCountRelation(Relation.EQUAL_TO.toString());
            return builder.build();
        }
    }

    /**
     * Searcher that returns documents with numeric ID field.
     */
    static class NumericIdFieldSearcher extends RankFusionSearcher {
        @Override
        protected SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean) {
            SearchResultBuilder builder = SearchResult.create();
            for (int i = 0; i < 10; i++) {
                Map<String, Object> doc = new HashMap<>();
                doc.put(ID_FIELD, Integer.valueOf(i)); // Numeric instead of String
                doc.put("title", "Document " + i);
                builder.addDocument(doc);
            }
            builder.allRecordCount(10);
            builder.allRecordCountRelation(Relation.EQUAL_TO.toString());
            return builder.build();
        }
    }

    /**
     * Searcher that returns null in document list.
     */
    static class NullDocumentSearcher extends RankFusionSearcher {
        @Override
        protected SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean) {
            SearchResultBuilder builder = SearchResult.create();
            builder.addDocument(null); // Add null document
            Map<String, Object> doc = new HashMap<>();
            doc.put(ID_FIELD, "valid_doc");
            builder.addDocument(doc);
            builder.allRecordCount(2);
            builder.allRecordCountRelation(Relation.EQUAL_TO.toString());
            return builder.build();
        }
    }

    /**
     * Searcher that simulates slow search operations.
     */
    static class SlowSearcher extends RankFusionSearcher {
        private final long delayMs;

        SlowSearcher(long delayMs) {
            this.delayMs = delayMs;
        }

        @Override
        protected SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean) {
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            SearchResultBuilder builder = SearchResult.create();
            for (int i = 0; i < 10; i++) {
                Map<String, Object> doc = new HashMap<>();
                doc.put(ID_FIELD, "slow_" + i);
                builder.addDocument(doc);
            }
            builder.allRecordCount(10);
            builder.allRecordCountRelation(Relation.EQUAL_TO.toString());
            return builder.build();
        }
    }

    /**
     * Normal test searcher for comparison.
     */
    static class TestSearcher extends RankFusionSearcher {
        private final long allRecordCount;

        TestSearcher(long allRecordCount) {
            this.allRecordCount = allRecordCount;
        }

        @Override
        protected SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean) {
            int start = params.getStartPosition();
            int size = params.getPageSize();
            SearchResultBuilder builder = SearchResult.create();
            for (int i = start; i < start + size && i < allRecordCount; i++) {
                Map<String, Object> doc = new HashMap<>();
                doc.put(ID_FIELD, Integer.toString(i));
                doc.put("score", 1.0f / (i + 1));
                builder.addDocument(doc);
            }
            builder.allRecordCount(allRecordCount);
            builder.allRecordCountRelation(Relation.EQUAL_TO.toString());
            return builder.build();
        }
    }

    /**
     * Test search request parameters implementation.
     */
    static class TestSearchRequestParams extends SearchRequestParams {
        private final int startPosition;
        private final int pageSize;
        private final int offset;

        TestSearchRequestParams(int startPosition, int pageSize, int offset) {
            this.startPosition = startPosition;
            this.pageSize = pageSize;
            this.offset = offset;
        }

        @Override
        public String getQuery() {
            return null;
        }

        @Override
        public Map<String, String[]> getFields() {
            return null;
        }

        @Override
        public Map<String, String[]> getConditions() {
            return null;
        }

        @Override
        public String[] getLanguages() {
            return null;
        }

        @Override
        public GeoInfo getGeoInfo() {
            return null;
        }

        @Override
        public FacetInfo getFacetInfo() {
            return null;
        }

        @Override
        public HighlightInfo getHighlightInfo() {
            return null;
        }

        @Override
        public String getSort() {
            return null;
        }

        @Override
        public int getStartPosition() {
            return startPosition;
        }

        @Override
        public int getOffset() {
            return offset;
        }

        @Override
        public int getPageSize() {
            return pageSize;
        }

        @Override
        public String[] getExtraQueries() {
            return null;
        }

        @Override
        public Object getAttribute(String name) {
            return null;
        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public SearchRequestType getType() {
            return null;
        }

        @Override
        public String getSimilarDocHash() {
            return null;
        }
    }
}
