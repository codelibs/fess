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
package org.codelibs.fess.ds.callback;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.codelibs.fess.entity.DataStoreParams;
import org.codelibs.fess.unit.UnitFessTestCase;

public class IndexUpdateCallbackTest extends UnitFessTestCase {

    public void test_interface_methods() {
        // Test with anonymous implementation
        IndexUpdateCallback callback = new IndexUpdateCallback() {
            private final AtomicLong docSize = new AtomicLong(0);
            private final AtomicLong execTime = new AtomicLong(0);

            @Override
            public void store(DataStoreParams paramMap, Map<String, Object> dataMap) {
                assertNotNull(paramMap);
                assertNotNull(dataMap);
                docSize.incrementAndGet();
                execTime.addAndGet(100);
            }

            @Override
            public long getDocumentSize() {
                return docSize.get();
            }

            @Override
            public long getExecuteTime() {
                return execTime.get();
            }

            @Override
            public void commit() {
                // No-op for test
            }
        };

        // Test initial state
        assertEquals(0L, callback.getDocumentSize());
        assertEquals(0L, callback.getExecuteTime());

        // Test store method
        DataStoreParams params = new DataStoreParams();
        Map<String, Object> data = new HashMap<>();
        data.put("key1", "value1");
        callback.store(params, data);

        assertEquals(1L, callback.getDocumentSize());
        assertEquals(100L, callback.getExecuteTime());

        // Test multiple stores
        callback.store(params, data);
        callback.store(params, data);

        assertEquals(3L, callback.getDocumentSize());
        assertEquals(300L, callback.getExecuteTime());

        // Test commit (should not throw exception)
        callback.commit();
    }

    public void test_multiple_implementations() {
        // Test with different implementations to ensure interface contract
        IndexUpdateCallback callback1 = createMockCallback(10, 500);
        IndexUpdateCallback callback2 = createMockCallback(20, 1000);

        assertEquals(10L, callback1.getDocumentSize());
        assertEquals(500L, callback1.getExecuteTime());

        assertEquals(20L, callback2.getDocumentSize());
        assertEquals(1000L, callback2.getExecuteTime());

        // Test store operations
        DataStoreParams params = new DataStoreParams();
        Map<String, Object> data = new HashMap<>();

        callback1.store(params, data);
        assertEquals(11L, callback1.getDocumentSize());

        callback2.store(params, data);
        assertEquals(21L, callback2.getDocumentSize());

        // Test commit on both
        callback1.commit();
        callback2.commit();
    }

    public void test_store_with_null_parameters() {
        // Test handling of null parameters
        IndexUpdateCallback callback = new IndexUpdateCallback() {
            private boolean storeCalledWithNull = false;

            @Override
            public void store(DataStoreParams paramMap, Map<String, Object> dataMap) {
                if (paramMap == null || dataMap == null) {
                    storeCalledWithNull = true;
                }
            }

            @Override
            public long getDocumentSize() {
                return storeCalledWithNull ? -1L : 0L;
            }

            @Override
            public long getExecuteTime() {
                return 0L;
            }

            @Override
            public void commit() {
                // No-op
            }
        };

        // Test with null dataMap
        callback.store(new DataStoreParams(), null);
        assertEquals(-1L, callback.getDocumentSize());
    }

    public void test_store_with_empty_map() {
        // Test with empty data map
        final AtomicInteger storeCount = new AtomicInteger(0);
        IndexUpdateCallback callback = new IndexUpdateCallback() {
            @Override
            public void store(DataStoreParams paramMap, Map<String, Object> dataMap) {
                assertNotNull(paramMap);
                assertNotNull(dataMap);
                assertTrue(dataMap.isEmpty());
                storeCount.incrementAndGet();
            }

            @Override
            public long getDocumentSize() {
                return storeCount.get();
            }

            @Override
            public long getExecuteTime() {
                return 0L;
            }

            @Override
            public void commit() {
                // No-op
            }
        };

        callback.store(new DataStoreParams(), new HashMap<>());
        assertEquals(1L, callback.getDocumentSize());
    }

    public void test_commit_behavior() {
        // Test commit behavior with state tracking
        final AtomicInteger commitCount = new AtomicInteger(0);
        final AtomicLong documentsBeforeCommit = new AtomicLong(0);

        IndexUpdateCallback callback = new IndexUpdateCallback() {
            private long docCount = 0;

            @Override
            public void store(DataStoreParams paramMap, Map<String, Object> dataMap) {
                docCount++;
            }

            @Override
            public long getDocumentSize() {
                return docCount;
            }

            @Override
            public long getExecuteTime() {
                return 0L;
            }

            @Override
            public void commit() {
                documentsBeforeCommit.set(docCount);
                commitCount.incrementAndGet();
            }
        };

        // Store some documents
        DataStoreParams params = new DataStoreParams();
        Map<String, Object> data = new HashMap<>();

        callback.store(params, data);
        callback.store(params, data);
        callback.store(params, data);

        assertEquals(3L, callback.getDocumentSize());
        assertEquals(0, commitCount.get());

        // Commit
        callback.commit();

        assertEquals(1, commitCount.get());
        assertEquals(3L, documentsBeforeCommit.get());

        // Store more and commit again
        callback.store(params, data);
        callback.store(params, data);

        assertEquals(5L, callback.getDocumentSize());

        callback.commit();

        assertEquals(2, commitCount.get());
        assertEquals(5L, documentsBeforeCommit.get());
    }

    public void test_execution_time_tracking() {
        // Test execution time tracking
        IndexUpdateCallback callback = new IndexUpdateCallback() {
            private long startTime = System.currentTimeMillis();
            private long totalTime = 0;

            @Override
            public void store(DataStoreParams paramMap, Map<String, Object> dataMap) {
                try {
                    Thread.sleep(10); // Simulate processing time
                } catch (InterruptedException e) {
                    // Ignore
                }
                totalTime = System.currentTimeMillis() - startTime;
            }

            @Override
            public long getDocumentSize() {
                return 1L;
            }

            @Override
            public long getExecuteTime() {
                return totalTime;
            }

            @Override
            public void commit() {
                // No-op
            }
        };

        // Initial execution time should be 0
        assertEquals(0L, callback.getExecuteTime());

        // Store a document
        callback.store(new DataStoreParams(), new HashMap<>());

        // Execution time should be greater than 0
        assertTrue(callback.getExecuteTime() >= 10L);
    }

    public void test_concurrent_stores() {
        // Test thread-safe implementation
        final AtomicLong counter = new AtomicLong(0);

        IndexUpdateCallback callback = new IndexUpdateCallback() {
            @Override
            public void store(DataStoreParams paramMap, Map<String, Object> dataMap) {
                counter.incrementAndGet();
            }

            @Override
            public long getDocumentSize() {
                return counter.get();
            }

            @Override
            public long getExecuteTime() {
                return 0L;
            }

            @Override
            public void commit() {
                // No-op
            }
        };

        // Create multiple threads to store documents
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    callback.store(new DataStoreParams(), new HashMap<>());
                }
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                fail("Thread interrupted");
            }
        }

        // Verify all stores were counted
        assertEquals(1000L, callback.getDocumentSize());

        // Test commit after concurrent operations
        callback.commit();
    }

    public void test_store_with_various_data_types() {
        // Test storing various data types
        final AtomicInteger callCount = new AtomicInteger(0);

        IndexUpdateCallback callback = new IndexUpdateCallback() {
            @Override
            public void store(DataStoreParams paramMap, Map<String, Object> dataMap) {
                assertNotNull(dataMap);
                callCount.incrementAndGet();
            }

            @Override
            public long getDocumentSize() {
                return callCount.get();
            }

            @Override
            public long getExecuteTime() {
                return 0L;
            }

            @Override
            public void commit() {
                // No-op
            }
        };

        DataStoreParams params = new DataStoreParams();

        // Test with String data
        Map<String, Object> stringData = new HashMap<>();
        stringData.put("text", "test value");
        callback.store(params, stringData);

        // Test with numeric data
        Map<String, Object> numericData = new HashMap<>();
        numericData.put("integer", 123);
        numericData.put("long", 456L);
        numericData.put("double", 789.0);
        callback.store(params, numericData);

        // Test with boolean data
        Map<String, Object> booleanData = new HashMap<>();
        booleanData.put("flag", true);
        callback.store(params, booleanData);

        // Test with nested map
        Map<String, Object> nestedData = new HashMap<>();
        Map<String, String> innerMap = new HashMap<>();
        innerMap.put("inner", "value");
        nestedData.put("nested", innerMap);
        callback.store(params, nestedData);

        assertEquals(4L, callback.getDocumentSize());
    }

    // Helper method to create mock callbacks with preset values
    private IndexUpdateCallback createMockCallback(final long docSize, final long execTime) {
        return new IndexUpdateCallback() {
            private final AtomicLong documentCount = new AtomicLong(docSize);

            @Override
            public void store(DataStoreParams paramMap, Map<String, Object> dataMap) {
                documentCount.incrementAndGet();
            }

            @Override
            public long getDocumentSize() {
                return documentCount.get();
            }

            @Override
            public long getExecuteTime() {
                return execTime;
            }

            @Override
            public void commit() {
                // No-op
            }
        };
    }
}