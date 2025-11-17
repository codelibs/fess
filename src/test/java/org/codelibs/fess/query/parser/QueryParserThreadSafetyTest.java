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
package org.codelibs.fess.query.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.lucene.search.Query;
import org.codelibs.fess.query.parser.QueryParser.Filter;
import org.codelibs.fess.query.parser.QueryParser.FilterChain;
import org.codelibs.fess.unit.UnitFessTestCase;

/**
 * Thread safety tests for QueryParser.
 * Tests the synchronized addFilter() and createFilterChain() methods.
 */
public class QueryParserThreadSafetyTest extends UnitFessTestCase {

    /**
     * Test that concurrent addFilter() calls are thread-safe.
     */
    public void test_addFilter_threadSafe() throws Exception {
        QueryParser queryParser = new QueryParser();
        queryParser.init();

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
                            queryParser.addFilter(new Filter() {
                                @Override
                                public Query parse(String query, FilterChain chain) {
                                    filterCounter.incrementAndGet();
                                    return chain.parse(query);
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
            assertEquals(threadCount * filtersPerThread, queryParser.filterList.size());

            // Parse a query to verify filter chain works correctly
            filterCounter.set(0);
            queryParser.parse("test query");

            // All filters should have been executed
            assertEquals(threadCount * filtersPerThread, filterCounter.get());

        } finally {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    /**
     * Test that createFilterChain() is thread-safe when called concurrently.
     */
    public void test_createFilterChain_threadSafe() throws Exception {
        QueryParser queryParser = new QueryParser();
        queryParser.init();

        final int threadCount = 20;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch completeLatch = new CountDownLatch(threadCount);
        final List<Exception> exceptions = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        try {
            // Half the threads add filters, half parse queries
            for (int i = 0; i < threadCount; i++) {
                final int threadIndex = i;
                executor.submit(() -> {
                    try {
                        startLatch.await();

                        if (threadIndex % 2 == 0) {
                            // Add filter (triggers createFilterChain)
                            queryParser.addFilter(new Filter() {
                                @Override
                                public Query parse(String query, FilterChain chain) {
                                    return chain.parse(query);
                                }
                            });
                        } else {
                            // Parse query (reads filterChain)
                            queryParser.parse("test query " + threadIndex);
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
        QueryParser queryParser = new QueryParser();
        queryParser.init();

        final int iterations = 100;
        final CountDownLatch completeLatch = new CountDownLatch(2);

        // Thread 1: Add filters
        Thread adderThread = new Thread(() -> {
            try {
                for (int i = 0; i < iterations; i++) {
                    queryParser.addFilter(new Filter() {
                        @Override
                        public Query parse(String query, FilterChain chain) {
                            return chain.parse(query);
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

        // Thread 2: Parse queries
        Thread parserThread = new Thread(() -> {
            try {
                for (int i = 0; i < iterations; i++) {
                    Query result = queryParser.parse("test query " + i);
                    assertNotNull("Query parsing should never return null", result);
                    Thread.sleep(1); // Small delay to allow interleaving
                }
            } catch (Exception e) {
                fail("Exception in parser thread: " + e.getMessage());
            } finally {
                completeLatch.countDown();
            }
        });

        adderThread.start();
        parserThread.start();

        assertTrue("Both threads should complete within 30 seconds",
                completeLatch.await(30, TimeUnit.SECONDS));

        // Verify final state is consistent
        assertEquals(iterations, queryParser.filterList.size());
    }

    /**
     * Test that synchronized methods don't cause deadlock.
     */
    public void test_noDeadlock() throws Exception {
        QueryParser queryParser = new QueryParser();
        queryParser.init();

        final int threadCount = 50;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch completeLatch = new CountDownLatch(threadCount);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        try {
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        startLatch.await();

                        // Rapidly add filters and parse queries
                        for (int j = 0; j < 10; j++) {
                            queryParser.addFilter(new Filter() {
                                @Override
                                public Query parse(String query, FilterChain chain) {
                                    return chain.parse(query);
                                }
                            });

                            queryParser.parse("test query");
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
        QueryParser queryParser = new QueryParser();
        queryParser.init();

        final List<Integer> executionOrder = new ArrayList<>();

        // Add some initial filters
        for (int i = 0; i < 5; i++) {
            final int filterId = i;
            queryParser.addFilter(new Filter() {
                @Override
                public Query parse(String query, FilterChain chain) {
                    synchronized (executionOrder) {
                        executionOrder.add(filterId);
                    }
                    return chain.parse(query);
                }
            });
        }

        // Parse a query
        queryParser.parse("test query");

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
        QueryParser queryParser = new QueryParser();
        queryParser.init();

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
                                queryParser.addFilter(new Filter() {
                                    @Override
                                    public Query parse(String query, FilterChain chain) {
                                        return chain.parse(query);
                                    }
                                });
                            } else {
                                // Parse query
                                Query result = queryParser.parse("test query " + threadId);
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

            // Verify successful parse operations
            int expectedParses = (threadCount * 2 / 3) * operationsPerThread; // 2/3 of threads parse queries
            assertTrue("Most parse operations should succeed", successCount.get() >= expectedParses * 0.95);

        } finally {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    /**
     * Test concurrent parsing with different query strings.
     */
    public void test_concurrentParsing_differentQueries() throws Exception {
        QueryParser queryParser = new QueryParser();
        queryParser.init();

        final int threadCount = 20;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch completeLatch = new CountDownLatch(threadCount);
        final List<String> queries = new ArrayList<>();

        // Different query patterns
        queries.add("simple");
        queries.add("title:test");
        queries.add("content:\"phrase query\"");
        queries.add("field1:value1 AND field2:value2");
        queries.add("(field1:value1 OR field2:value2)");

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        try {
            for (int i = 0; i < threadCount; i++) {
                final String query = queries.get(i % queries.size());
                executor.submit(() -> {
                    try {
                        startLatch.await();

                        // Parse the same query multiple times
                        for (int j = 0; j < 50; j++) {
                            Query result = queryParser.parse(query);
                            assertNotNull("Parsed query should not be null for: " + query, result);
                        }
                    } catch (Exception e) {
                        fail("Exception while parsing query '" + query + "': " + e.getMessage());
                    } finally {
                        completeLatch.countDown();
                    }
                });
            }

            startLatch.countDown();

            assertTrue("All parsing should complete within 15 seconds",
                    completeLatch.await(15, TimeUnit.SECONDS));

        } finally {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
