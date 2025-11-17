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
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.rank.fusion.SearchResult.SearchResultBuilder;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.QueryResponseList;
import org.dbflute.optional.OptionalThing;

/**
 * Edge case tests for RankFusionProcessor.
 * Tests boundary conditions, empty states, and error scenarios.
 */
public class RankFusionProcessorEdgeCaseTest extends UnitFessTestCase {

    private static final String ID_FIELD = "_id";

    /**
     * Test behavior when no searchers are registered (empty list).
     */
    public void test_emptySearcherList() throws Exception {
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.init();

            // Should handle empty searcher list gracefully
            final List<Map<String, Object>> results = processor.search("*",
                    new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());
            assertNotNull(results);
        }
    }

    /**
     * Test setSearcher when list is initially empty.
     */
    public void test_setSearcherOnEmptyList() throws Exception {
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            // Set searcher before init
            processor.setSearcher(new TestSearcher(100));
            processor.init();

            final List<Map<String, Object>> results = processor.search("*",
                    new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());
            assertNotNull(results);
            assertEquals(10, results.size());
        }
    }

    /**
     * Test setSearcher replaces existing searcher.
     */
    public void test_setSearcherReplacesExisting() throws Exception {
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(50));
            processor.init();

            // Replace with new searcher
            processor.setSearcher(new TestSearcher(100));

            final List<Map<String, Object>> results = processor.search("*",
                    new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());
            assertNotNull(results);
            assertEquals(10, results.size());
        }
    }

    /**
     * Test search with zero page size.
     */
    public void test_searchWithZeroPageSize() throws Exception {
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(100));
            processor.init();

            final List<Map<String, Object>> results = processor.search("*",
                    new TestSearchRequestParams(0, 0, 0), OptionalThing.empty());
            assertNotNull(results);
            assertEquals(0, results.size());
        }
    }

    /**
     * Test search with very large page size.
     */
    public void test_searchWithLargePageSize() throws Exception {
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(100));
            processor.init();

            final List<Map<String, Object>> results = processor.search("*",
                    new TestSearchRequestParams(0, 10000, 0), OptionalThing.empty());
            assertNotNull(results);
            // Should return only available documents
            assertEquals(100, results.size());
        }
    }

    /**
     * Test search with start position beyond available documents.
     */
    public void test_searchBeyondAvailableDocuments() throws Exception {
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(50));
            processor.init();

            final List<Map<String, Object>> results = processor.search("*",
                    new TestSearchRequestParams(100, 10, 0), OptionalThing.empty());
            assertNotNull(results);
            // Should return empty or minimal results
            assertTrue(results.size() <= 10);
        }
    }

    /**
     * Test search with negative start position (should be handled gracefully).
     */
    public void test_searchWithNegativeStartPosition() throws Exception {
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(100));
            processor.init();

            // Implementation should handle this gracefully
            final List<Map<String, Object>> results = processor.search("*",
                    new TestSearchRequestParams(-10, 10, 0), OptionalThing.empty());
            assertNotNull(results);
        }
    }

    /**
     * Test with searcher that returns empty results.
     */
    public void test_searcherReturnsNoResults() throws Exception {
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(0)); // No documents
            processor.init();

            final List<Map<String, Object>> results = processor.search("*",
                    new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());
            assertNotNull(results);
            assertEquals(0, results.size());
        }
    }

    /**
     * Test with searcher that returns single document.
     */
    public void test_searcherReturnsSingleDocument() throws Exception {
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(1));
            processor.init();

            if (processor.search("*", new TestSearchRequestParams(0, 10, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(1, list.size());
                assertEquals(1, list.getAllRecordCount());
                assertEquals("0", list.get(0).get(ID_FIELD));
            } else {
                fail();
            }
        }
    }

    /**
     * Test multiple searchers with mixed result counts.
     */
    public void test_multipleSearchersWithMixedResults() throws Exception {
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(100));
            processor.register(new TestSearcher(0)); // Empty searcher
            processor.register(new TestSearcher(50)); // Half-full searcher
            processor.init();

            final List<Map<String, Object>> results = processor.search("*",
                    new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());
            assertNotNull(results);
            assertTrue(results.size() <= 10);
        }
    }

    /**
     * Test search with null query (should be handled by implementation).
     */
    public void test_searchWithNullQuery() throws Exception {
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(100));
            processor.init();

            // Should handle null query gracefully
            final List<Map<String, Object>> results = processor.search(null,
                    new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());
            assertNotNull(results);
        }
    }

    /**
     * Test search with empty query string.
     */
    public void test_searchWithEmptyQuery() throws Exception {
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(100));
            processor.init();

            final List<Map<String, Object>> results = processor.search("",
                    new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());
            assertNotNull(results);
        }
    }

    /**
     * Test registering same searcher multiple times.
     */
    public void test_registerSameSearcherMultipleTimes() throws Exception {
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(100));
            final TestSearcher searcher = new TestSearcher(50);
            processor.register(searcher);
            processor.register(searcher); // Register same instance again
            processor.init();

            // Should still work without errors
            final List<Map<String, Object>> results = processor.search("*",
                    new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());
            assertNotNull(results);
        }
    }

    /**
     * Test with documents having duplicate IDs across searchers.
     */
    public void test_duplicateDocumentIds() throws Exception {
        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcherWithFixedIds("doc1", "doc2", "doc3"));
            processor.register(new TestSearcherWithFixedIds("doc2", "doc3", "doc4")); // Some duplicates
            processor.init();

            final List<Map<String, Object>> results = processor.search("*",
                    new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());
            assertNotNull(results);
            // RRF should merge duplicates and boost their scores
        }
    }

    /**
     * Test that close can be called multiple times safely.
     */
    public void test_multipleCloseCallsSafe() throws Exception {
        RankFusionProcessor processor = new RankFusionProcessor();
        processor.setSearcher(new TestSearcher(100));
        processor.init();

        // First close
        processor.close();
        // Second close - should not throw exception
        processor.close();
    }

    /**
     * Test searcher that returns configurable number of documents.
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
     * Test searcher that returns documents with fixed IDs.
     */
    static class TestSearcherWithFixedIds extends RankFusionSearcher {
        private final String[] fixedIds;

        TestSearcherWithFixedIds(String... ids) {
            this.fixedIds = ids;
        }

        @Override
        protected SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean) {
            SearchResultBuilder builder = SearchResult.create();
            for (int i = 0; i < fixedIds.length; i++) {
                Map<String, Object> doc = new HashMap<>();
                doc.put(ID_FIELD, fixedIds[i]);
                doc.put("score", 1.0f / (i + 1));
                builder.addDocument(doc);
            }
            builder.allRecordCount(fixedIds.length);
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
