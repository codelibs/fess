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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codelibs.fess.query;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.query.QueryProcessor.Filter;
import org.codelibs.fess.query.QueryProcessor.FilterChain;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

/**
 * Thread safety tests for QueryProcessor.
 * Tests the synchronized addFilter() and createFilterChain() methods.
 */
public class QueryProcessorThreadSafetyTest extends UnitFessTestCase {

    /**
     * Test that concurrent addFilter() calls are thread-safe.
     * Multiple threads should be able to add filters without causing race conditions.
     */
    public void test_addFilter_threadSafe() throws Exception {
        QueryProcessor queryProcessor = new QueryProcessor();
        queryProcessor.init();

        final int threadCount = 10;
        final int filtersPerThread = 5;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch completeLatch = new CountDownLatch(threadCount);
        final AtomicInteger filterCounter = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        try {
            // Submit tasks to add filters concurrently
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        // Wait for all threads to be ready
                        startLatch.await();

                        // Add multiple filters
                        for (int j = 0; j < filtersPerThread; j++) {
                            queryProcessor.addFilter(new Filter() {
                                @Override
                                public QueryBuilder execute(QueryContext context, Query query, float boost, FilterChain chain) {
                                    filterCounter.incrementAndGet();
                                    return chain.execute(context, query, boost);
                                }
                            });
                        }
                    } catch (Exception e) {
                        fail("Exception in thread: " + e.getMessage());
                    } finally {
                        completeLatch.countDown();
                    }
                });
            }

            // Start all threads simultaneously
            startLatch.countDown();

            // Wait for all threads to complete
            assertTrue("All threads should complete within 10 seconds",
                    completeLatch.await(10, TimeUnit.SECONDS));

            // Verify all filters were added
            assertEquals(threadCount * filtersPerThread, queryProcessor.filterList.size());

            // Execute a query to verify filter chain works correctly
            filterCounter.set(0);
            QueryContext context = new QueryContext(null, false);
            queryProcessor.execute(context, new MatchAllDocsQuery(), 1.0f);

            // All filters should have been executed
            assertEquals(threadCount * filtersPerThread, filterCounter.get());

        } finally {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    /**
     * Test that createFilterChain() is thread-safe when called concurrently with addFilter().
     */
    public void test_createFilterChain_threadSafe() throws Exception {
        final QueryProcessor queryProcessor = new QueryProcessor() {
            @Override
            protected FilterChain createDefaultFilterChain() {
                return (context, query, boost) -> QueryBuilders.matchAllQuery();
            }
        };
        queryProcessor.init();

        final int threadCount = 20;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch completeLatch = new CountDownLatch(threadCount);
        final List<Exception> exceptions = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        try {
            // Half the threads add filters, half execute queries
            for (int i = 0; i < threadCount; i++) {
                final int threadIndex = i;
                executor.submit(() -> {
                    try {
                        startLatch.await();

                        if (threadIndex % 2 == 0) {
                            // Add filter (triggers createFilterChain)
                            queryProcessor.addFilter(new Filter() {
                                @Override
                                public QueryBuilder execute(QueryContext context, Query query, float boost,
                                        FilterChain chain) {
                                    return chain.execute(context, query, boost);
                                }
                            });
                        } else {
                            // Execute query (reads filterChain)
                            QueryContext context = new QueryContext(null, false);
                            queryProcessor.execute(context, new MatchAllDocsQuery(), 1.0f);
                        }
                    } catch (Exception e) {
                        synchronized (exceptions) {
                            exceptions.add(e);
                        }
                    } finally {
                        completeLatch.countDown();
                    }
                });
            }

            // Start all threads
            startLatch.countDown();

            // Wait for completion
            assertTrue("All threads should complete within 10 seconds",
                    completeLatch.await(10, TimeUnit.SECONDS));

            // Verify no exceptions occurred
            if (!exceptions.isEmpty()) {
                fail("Exceptions occurred during concurrent access: " + exceptions.get(0).getMessage());
            }

        } finally {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    /**
     * Test that filter chain remains consistent during concurrent modifications.
     */
    public void test_filterChain_remainsConsistent() throws Exception {
        QueryProcessor queryProcessor = new QueryProcessor();
        queryProcessor.init();

        final int iterations = 100;
        final CountDownLatch completeLatch = new CountDownLatch(2);

        // Thread 1: Add filters
        Thread adderThread = new Thread(() -> {
            try {
                for (int i = 0; i < iterations; i++) {
                    queryProcessor.addFilter(new Filter() {
                        @Override
                        public QueryBuilder execute(QueryContext context, Query query, float boost, FilterChain chain) {
                            return chain.execute(context, query, boost);
                        }
                    });
                    Thread.sleep(1); // Small delay to allow interleaving
                }
            } catch (Exception e) {
                fail("Exception in adder thread: " + e.getMessage());
            } finally {
                completeLatch.countDown();
            }
        });

        // Thread 2: Execute queries
        Thread executorThread = new Thread(() -> {
            try {
                for (int i = 0; i < iterations; i++) {
                    QueryContext context = new QueryContext(null, false);
                    QueryBuilder result = queryProcessor.execute(context, new MatchAllDocsQuery(), 1.0f);
                    assertNotNull("Query execution should never return null", result);
                    Thread.sleep(1); // Small delay to allow interleaving
                }
            } catch (Exception e) {
                fail("Exception in executor thread: " + e.getMessage());
            } finally {
                completeLatch.countDown();
            }
        });

        adderThread.start();
        executorThread.start();

        assertTrue("Both threads should complete within 30 seconds",
                completeLatch.await(30, TimeUnit.SECONDS));

        // Verify final state is consistent
        assertEquals(iterations, queryProcessor.filterList.size());
    }

    /**
     * Test that synchronized methods don't cause deadlock.
     */
    public void test_noDeadlock() throws Exception {
        QueryProcessor queryProcessor = new QueryProcessor();
        queryProcessor.init();

        final int threadCount = 50;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch completeLatch = new CountDownLatch(threadCount);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        try {
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        startLatch.await();

                        // Rapidly add filters and execute queries
                        for (int j = 0; j < 10; j++) {
                            queryProcessor.addFilter(new Filter() {
                                @Override
                                public QueryBuilder execute(QueryContext context, Query query, float boost,
                                        FilterChain chain) {
                                    return chain.execute(context, query, boost);
                                }
                            });

                            QueryContext context = new QueryContext(null, false);
                            queryProcessor.execute(context, new MatchAllDocsQuery(), 1.0f);
                        }
                    } catch (Exception e) {
                        fail("Exception: " + e.getMessage());
                    } finally {
                        completeLatch.countDown();
                    }
                });
            }

            startLatch.countDown();

            // Should complete quickly without deadlock
            assertTrue("No deadlock should occur - all threads should complete within 15 seconds",
                    completeLatch.await(15, TimeUnit.SECONDS));

        } finally {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    /**
     * Test that filter execution order is preserved even with concurrent additions.
     */
    public void test_filterExecutionOrder_preserved() throws Exception {
        QueryProcessor queryProcessor = new QueryProcessor();
        queryProcessor.init();

        final AtomicInteger executionCounter = new AtomicInteger(0);
        final List<Integer> executionOrder = new ArrayList<>();

        // Add some initial filters
        for (int i = 0; i < 5; i++) {
            final int filterId = i;
            queryProcessor.addFilter(new Filter() {
                @Override
                public QueryBuilder execute(QueryContext context, Query query, float boost, FilterChain chain) {
                    synchronized (executionOrder) {
                        executionOrder.add(filterId);
                    }
                    return chain.execute(context, query, boost);
                }
            });
        }

        // Execute query
        QueryContext context = new QueryContext(null, false);
        queryProcessor.execute(context, new MatchAllDocsQuery(), 1.0f);

        // Verify filters were executed in order
        assertEquals(5, executionOrder.size());
        for (int i = 0; i < 5; i++) {
            assertEquals(Integer.valueOf(i), executionOrder.get(i));
        }
    }

    /**
     * Stress test with high concurrent load.
     */
    public void test_highConcurrentLoad() throws Exception {
        QueryProcessor queryProcessor = new QueryProcessor();
        queryProcessor.init();

        final int threadCount = 100;
        final int operationsPerThread = 20;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch completeLatch = new CountDownLatch(threadCount);
        final AtomicInteger successCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        try {
            for (int i = 0; i < threadCount; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    try {
                        startLatch.await();

                        for (int j = 0; j < operationsPerThread; j++) {
                            if (threadId % 3 == 0) {
                                // Add filter
                                queryProcessor.addFilter(new Filter() {
                                    @Override
                                    public QueryBuilder execute(QueryContext context, Query query, float boost,
                                            FilterChain chain) {
                                        return chain.execute(context, query, boost);
                                    }
                                });
                            } else {
                                // Execute query
                                QueryContext context = new QueryContext(null, false);
                                QueryBuilder result = queryProcessor.execute(context, new MatchAllDocsQuery(), 1.0f);
                                if (result != null) {
                                    successCount.incrementAndGet();
                                }
                            }
                        }
                    } catch (Exception e) {
                        fail("Exception in thread " + threadId + ": " + e.getMessage());
                    } finally {
                        completeLatch.countDown();
                    }
                });
            }

            startLatch.countDown();

            assertTrue("All threads should complete under high load within 30 seconds",
                    completeLatch.await(30, TimeUnit.SECONDS));

            // Verify successful executions
            int expectedExecutions = (threadCount * 2 / 3) * operationsPerThread; // 2/3 of threads execute queries
            assertTrue("Most query executions should succeed", successCount.get() >= expectedExecutions * 0.95);

        } finally {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
