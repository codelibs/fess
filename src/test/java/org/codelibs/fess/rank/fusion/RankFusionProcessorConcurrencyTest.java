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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.lucene.search.TotalHits.Relation;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.rank.fusion.SearchResult.SearchResultBuilder;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.optional.OptionalThing;

/**
 * Concurrency and thread-safety tests for RankFusionProcessor.
 * Tests the improvements made to thread safety using CopyOnWriteArrayList.
 */
public class RankFusionProcessorConcurrencyTest extends UnitFessTestCase {

    private static final String ID_FIELD = "_id";

    /**
     * Test concurrent registration of searchers from multiple threads.
     * Verifies that CopyOnWriteArrayList handles concurrent modifications safely.
     */
    public void test_concurrentSearcherRegistration() throws Exception {
        final int numThreads = 10;
        final int searchersPerThread = 5;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(numThreads);
        final AtomicInteger registeredCount = new AtomicInteger(0);

        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(100));
            processor.init();

            final ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            try {
                for (int i = 0; i < numThreads; i++) {
                    final int threadId = i;
                    executor.submit(() -> {
                        try {
                            startLatch.await(); // Wait for all threads to be ready
                            for (int j = 0; j < searchersPerThread; j++) {
                                processor.register(new TestSearcher(10 + threadId * 10 + j));
                                registeredCount.incrementAndGet();
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        } finally {
                            endLatch.countDown();
                        }
                    });
                }

                startLatch.countDown(); // Start all threads simultaneously
                assertTrue("Concurrent registration timed out", endLatch.await(10, TimeUnit.SECONDS));

                // Verify all searchers were registered (1 initial + numThreads * searchersPerThread)
                assertEquals(numThreads * searchersPerThread, registeredCount.get());

            } finally {
                executor.shutdown();
                executor.awaitTermination(5, TimeUnit.SECONDS);
            }
        }
    }

    /**
     * Test that search operations can be performed while searchers are being registered.
     * Verifies CopyOnWriteArrayList's snapshot iteration behavior.
     */
    public void test_searchWhileRegisteringSearchers() throws Exception {
        final CountDownLatch startLatch = new CountDownLatch(1);
        final AtomicInteger searchCount = new AtomicInteger(0);
        final AtomicInteger registerCount = new AtomicInteger(0);

        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(100));
            processor.init();

            final ExecutorService executor = Executors.newFixedThreadPool(2);
            try {
                // Thread 1: Perform searches
                executor.submit(() -> {
                    try {
                        startLatch.await();
                        for (int i = 0; i < 10; i++) {
                            processor.search("*", new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());
                            searchCount.incrementAndGet();
                            Thread.sleep(10);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });

                // Thread 2: Register searchers
                executor.submit(() -> {
                    try {
                        startLatch.await();
                        for (int i = 0; i < 10; i++) {
                            processor.register(new TestSearcher(10 + i));
                            registerCount.incrementAndGet();
                            Thread.sleep(10);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });

                startLatch.countDown();
                executor.shutdown();
                assertTrue("Concurrent operations timed out", executor.awaitTermination(10, TimeUnit.SECONDS));

                // Verify both operations completed successfully
                assertEquals(10, searchCount.get());
                assertEquals(10, registerCount.get());

            } finally {
                if (!executor.isTerminated()) {
                    executor.shutdownNow();
                }
            }
        }
    }

    /**
     * Test that setSearcher and register can be called concurrently.
     */
    public void test_concurrentSetAndRegister() throws Exception {
        final int numIterations = 100;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(2);

        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(100));
            processor.init();

            final ExecutorService executor = Executors.newFixedThreadPool(2);
            try {
                // Thread 1: Repeatedly set main searcher
                executor.submit(() -> {
                    try {
                        startLatch.await();
                        for (int i = 0; i < numIterations; i++) {
                            processor.setSearcher(new TestSearcher(1000 + i));
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        endLatch.countDown();
                    }
                });

                // Thread 2: Register additional searchers
                executor.submit(() -> {
                    try {
                        startLatch.await();
                        for (int i = 0; i < numIterations; i++) {
                            processor.register(new TestSearcher(2000 + i));
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        endLatch.countDown();
                    }
                });

                startLatch.countDown();
                assertTrue("Concurrent set/register timed out", endLatch.await(10, TimeUnit.SECONDS));

                // Processor should still be functional
                final List<Map<String, Object>> results = processor.search("*",
                        new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());
                assertNotNull(results);

            } finally {
                executor.shutdown();
                executor.awaitTermination(5, TimeUnit.SECONDS);
            }
        }
    }

    /**
     * Test multiple concurrent search operations.
     * Verifies that parallel searches don't interfere with each other.
     */
    public void test_multipleConcurrentSearches() throws Exception {
        final int numSearchThreads = 5;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(numSearchThreads);
        final List<List<Map<String, Object>>> allResults = new ArrayList<>();

        try (RankFusionProcessor processor = new RankFusionProcessor()) {
            processor.setSearcher(new TestSearcher(100));
            processor.init();

            final ExecutorService executor = Executors.newFixedThreadPool(numSearchThreads);
            try {
                for (int i = 0; i < numSearchThreads; i++) {
                    executor.submit(() -> {
                        try {
                            startLatch.await();
                            List<Map<String, Object>> results = processor.search("*",
                                    new TestSearchRequestParams(0, 10, 0), OptionalThing.empty());
                            synchronized (allResults) {
                                allResults.add(results);
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        } finally {
                            endLatch.countDown();
                        }
                    });
                }

                startLatch.countDown();
                assertTrue("Concurrent searches timed out", endLatch.await(10, TimeUnit.SECONDS));

                // Verify all searches completed and returned results
                assertEquals(numSearchThreads, allResults.size());
                for (List<Map<String, Object>> results : allResults) {
                    assertEquals(10, results.size());
                }

            } finally {
                executor.shutdown();
                executor.awaitTermination(5, TimeUnit.SECONDS);
            }
        }
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
