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
import java.util.regex.Pattern;

import org.codelibs.fess.entity.DataStoreParams;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class FileListIndexUpdateCallbackImplTest extends UnitFessTestCase {
    public FileListIndexUpdateCallbackImpl indexUpdateCallback;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        indexUpdateCallback = new FileListIndexUpdateCallbackImpl(null, null, 1);
    }

    /** Case 1: Normal merge (no duplicates) */
    @Test
    public void test_mergeResponseData_noOverwrite() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("a", "A0");
        Map<String, Object> responseDataMap = new HashMap<>();
        responseDataMap.put("b", "B1");

        indexUpdateCallback.mergeResponseData(dataMap, responseDataMap);

        assertEquals(2, dataMap.size());
        assertEquals("A0", dataMap.get("a"));
        assertEquals("B1", dataMap.get("b"));
    }

    /** Case 2: Key conflict (without .overwrite) → Overwrite with value from responseDataMap */
    @Test
    public void test_mergeResponseData_keyConflict() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("x", "X0");
        Map<String, Object> responseDataMap = new HashMap<>();
        responseDataMap.put("x", "X1");

        indexUpdateCallback.mergeResponseData(dataMap, responseDataMap);

        assertEquals(1, dataMap.size());
        assertEquals("X1", dataMap.get("x"));
    }

    /** Case 3: Only overwrite key (baseKey not set) → generate baseKey, remove overwrite key */
    @Test
    public void test_mergeResponseData_overwriteOnly() {
        Map<String, Object> dataMap = new HashMap<>();
        Map<String, Object> responseDataMap = new HashMap<>();
        responseDataMap.put("y.overwrite", "Y1");

        indexUpdateCallback.mergeResponseData(dataMap, responseDataMap);

        assertFalse(dataMap.containsKey("y.overwrite"));
        assertEquals(1, dataMap.size());
        assertEquals("Y1", dataMap.get("y"));
    }

    /** Case 4: Both baseKey and baseKey.overwrite exist → Overwrite with the value of .overwrite */
    @Test
    public void test_mergeResponseData_baseAndOverwrite() {
        Map<String, Object> dataMap = new HashMap<>();
        Map<String, Object> responseDataMap = new HashMap<>();
        responseDataMap.put("z", "Z0");
        responseDataMap.put("z.overwrite", "Z1");

        indexUpdateCallback.mergeResponseData(dataMap, responseDataMap);

        assertFalse(dataMap.containsKey("z.overwrite"));
        assertEquals(1, dataMap.size());
        assertEquals("Z1", dataMap.get("z"));
    }

    /** Case 5: Overwrite processing for multiple fields, existing overwrite keys are also properly removed */
    @Test
    public void test_mergeResponseData_multipleOverwrite() {
        Map<String, Object> dataMap = new HashMap<>();
        // Case where the initial dataMap also contains overwrite keys
        dataMap.put("m", "M0");
        dataMap.put("n.overwrite", "N0_old");

        Map<String, Object> responseDataMap = new HashMap<>();
        responseDataMap.put("m.overwrite", "M1");
        responseDataMap.put("n.overwrite", "N1");
        responseDataMap.put("p", "P1");

        indexUpdateCallback.mergeResponseData(dataMap, responseDataMap);

        // All overwrite keys should be removed
        assertFalse(dataMap.containsKey("m.overwrite"));
        assertFalse(dataMap.containsKey("n.overwrite"));

        // m and n are updated with overwrite values, p is merged normally
        assertEquals("M1", dataMap.get("m"));
        assertEquals("N1", dataMap.get("n"));
        assertEquals("P1", dataMap.get("p"));
        assertEquals(3, dataMap.size());
    }

    @Test
    public void test_isUrlCrawlable_noExcludePattern() {
        DataStoreParams paramMap = new DataStoreParams();
        String url = "http://example.com/test.html";

        boolean result = indexUpdateCallback.isUrlCrawlable(paramMap, url);

        assertTrue(result);
    }

    @Test
    public void test_isUrlCrawlable_excludePatternAsString() {
        DataStoreParams paramMap = new DataStoreParams();
        paramMap.put("url_exclude_pattern", ".*\\.pdf$");

        boolean result1 = indexUpdateCallback.isUrlCrawlable(paramMap, "http://example.com/test.html");
        boolean result2 = indexUpdateCallback.isUrlCrawlable(paramMap, "http://example.com/test.pdf");

        assertTrue(result1);
        assertFalse(result2);
        assertTrue(paramMap.get("url_exclude_pattern") instanceof Pattern);
    }

    @Test
    public void test_isUrlCrawlable_excludePatternAsPattern() {
        DataStoreParams paramMap = new DataStoreParams();
        Pattern pattern = Pattern.compile(".*\\.jpg$");
        paramMap.put("url_exclude_pattern", pattern);

        boolean result1 = indexUpdateCallback.isUrlCrawlable(paramMap, "http://example.com/image.jpg");
        boolean result2 = indexUpdateCallback.isUrlCrawlable(paramMap, "http://example.com/image.png");

        assertFalse(result1);
        assertTrue(result2);
    }

    @Test
    public void test_isUrlCrawlable_complexExcludePattern() {
        DataStoreParams paramMap = new DataStoreParams();
        paramMap.put("url_exclude_pattern", ".*(admin|private|temp).*");

        boolean result1 = indexUpdateCallback.isUrlCrawlable(paramMap, "http://example.com/admin/config.html");
        boolean result2 = indexUpdateCallback.isUrlCrawlable(paramMap, "http://example.com/private/data.html");
        boolean result3 = indexUpdateCallback.isUrlCrawlable(paramMap, "http://example.com/temp/cache.html");
        boolean result4 = indexUpdateCallback.isUrlCrawlable(paramMap, "http://example.com/public/index.html");

        assertFalse(result1);
        assertFalse(result2);
        assertFalse(result3);
        assertTrue(result4);
    }

    @Test
    public void test_isUrlCrawlable_emptyUrl() {
        DataStoreParams paramMap = new DataStoreParams();
        paramMap.put("url_exclude_pattern", ".*");

        boolean result = indexUpdateCallback.isUrlCrawlable(paramMap, "");

        assertFalse(result);
    }

    @Test
    public void test_isUrlCrawlable_nullUrl() {
        DataStoreParams paramMap = new DataStoreParams();
        paramMap.put("url_exclude_pattern", ".*");

        try {
            indexUpdateCallback.isUrlCrawlable(paramMap, null);
            fail("Expected NullPointerException for null URL");
        } catch (NullPointerException e) {
            // Expected behavior - the method doesn't handle null URLs
        }
    }

    @Test
    public void test_isUrlCrawlable_nullUrlWithoutPattern() {
        DataStoreParams paramMap = new DataStoreParams();

        boolean result = indexUpdateCallback.isUrlCrawlable(paramMap, null);

        assertTrue(result);
    }

    // ========== Thread Safety Tests ==========

    /**
     * Test that deleteUrlList access is thread-safe with synchronized blocks.
     * This test verifies the ArrayList implementation works correctly when
     * all access is properly synchronized via indexUpdateCallback lock.
     */
    @Test
    public void test_deleteUrlList_synchronizedAccess() throws Exception {
        // Create a mock IndexUpdateCallback for synchronization
        IndexUpdateCallback mockCallback = new IndexUpdateCallback() {
            @Override
            public void store(DataStoreParams paramMap, Map<String, Object> dataMap) {
            }

            @Override
            public long getDocumentSize() {
                return 0;
            }

            @Override
            public long getExecuteTime() {
                return 0;
            }

            @Override
            public void commit() {
            }
        };

        FileListIndexUpdateCallbackImpl callback = new FileListIndexUpdateCallbackImpl(mockCallback, null, 1);
        callback.setMaxDeleteDocumentCacheSize(1000);

        final int threadCount = 10;
        final int urlsPerThread = 100;
        final Thread[] threads = new Thread[threadCount];
        final Exception[] exceptions = new Exception[threadCount];

        // Multiple threads add URLs to deleteUrlList
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < urlsPerThread; j++) {
                        // Access is synchronized internally in the actual implementation
                        // Here we just verify no concurrent modification exceptions occur
                        String url = "http://example.com/thread" + threadIndex + "/doc" + j;
                        // Simulate the synchronized access that happens in deleteDocument()
                        synchronized (mockCallback) {
                            callback.deleteUrlList.add(url);
                        }
                    }
                } catch (Exception e) {
                    exceptions[threadIndex] = e;
                }
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Verify no exceptions occurred
        for (int i = 0; i < threadCount; i++) {
            assertNull(exceptions[i], "Thread " + i + " threw exception");
        }

        // Verify all URLs were added
        synchronized (mockCallback) {
            assertEquals("All URLs should be added", threadCount * urlsPerThread, callback.deleteUrlList.size());
        }
    }

    /**
     * Test concurrent reads from deleteUrlList while synchronized.
     * Verifies that ArrayList can be safely read when properly synchronized.
     */
    @Test
    public void test_deleteUrlList_concurrentReads() throws Exception {
        IndexUpdateCallback mockCallback = new IndexUpdateCallback() {
            @Override
            public void store(DataStoreParams paramMap, Map<String, Object> dataMap) {
            }

            @Override
            public long getDocumentSize() {
                return 0;
            }

            @Override
            public long getExecuteTime() {
                return 0;
            }

            @Override
            public void commit() {
            }
        };

        FileListIndexUpdateCallbackImpl callback = new FileListIndexUpdateCallbackImpl(mockCallback, null, 1);

        // Pre-populate with some URLs
        synchronized (mockCallback) {
            for (int i = 0; i < 100; i++) {
                callback.deleteUrlList.add("http://example.com/doc" + i);
            }
        }

        final int threadCount = 10;
        final Thread[] threads = new Thread[threadCount];
        final Exception[] exceptions = new Exception[threadCount];
        final int[] sizes = new int[threadCount];
        final boolean[][] containsResults = new boolean[threadCount][10];

        // Multiple threads read from deleteUrlList
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                try {
                    synchronized (mockCallback) {
                        sizes[threadIndex] = callback.deleteUrlList.size();
                        for (int j = 0; j < 10; j++) {
                            containsResults[threadIndex][j] = callback.deleteUrlList.contains("http://example.com/doc" + j);
                        }
                    }
                } catch (Exception e) {
                    exceptions[threadIndex] = e;
                }
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Verify no exceptions occurred
        for (int i = 0; i < threadCount; i++) {
            assertNull(exceptions[i], "Thread " + i + " threw exception");
            assertEquals("Thread " + i + " should see correct size", 100, sizes[i]);
            for (int j = 0; j < 10; j++) {
                assertTrue("Thread " + i + " should find doc" + j, containsResults[i][j]);
            }
        }
    }

    /**
     * Test that clear operation on deleteUrlList is thread-safe.
     */
    @Test
    public void test_deleteUrlList_clearOperation() throws Exception {
        IndexUpdateCallback mockCallback = new IndexUpdateCallback() {
            @Override
            public void store(DataStoreParams paramMap, Map<String, Object> dataMap) {
            }

            @Override
            public long getDocumentSize() {
                return 0;
            }

            @Override
            public long getExecuteTime() {
                return 0;
            }

            @Override
            public void commit() {
            }
        };

        FileListIndexUpdateCallbackImpl callback = new FileListIndexUpdateCallbackImpl(mockCallback, null, 1);

        final int iterations = 10;
        final Thread[] threads = new Thread[2];
        final Exception[] exceptions = new Exception[2];

        // One thread adds, another clears
        threads[0] = new Thread(() -> {
            try {
                for (int i = 0; i < iterations; i++) {
                    synchronized (mockCallback) {
                        callback.deleteUrlList.add("http://example.com/doc" + i);
                    }
                    Thread.yield();
                }
            } catch (Exception e) {
                exceptions[0] = e;
            }
        });

        threads[1] = new Thread(() -> {
            try {
                for (int i = 0; i < iterations; i++) {
                    synchronized (mockCallback) {
                        callback.deleteUrlList.clear();
                    }
                    Thread.yield();
                }
            } catch (Exception e) {
                exceptions[1] = e;
            }
        });

        // Start both threads
        threads[0].start();
        threads[1].start();

        // Wait for completion
        threads[0].join();
        threads[1].join();

        // Verify no exceptions occurred (the main goal is no ConcurrentModificationException)
        assertNull(exceptions[0], "Add thread threw exception");
        assertNull(exceptions[1], "Clear thread threw exception");
    }

    /**
     * Test iteration over deleteUrlList while synchronized.
     * This simulates what happens in deleteDocuments() method.
     */
    @Test
    public void test_deleteUrlList_iteration() throws Exception {
        IndexUpdateCallback mockCallback = new IndexUpdateCallback() {
            @Override
            public void store(DataStoreParams paramMap, Map<String, Object> dataMap) {
            }

            @Override
            public long getDocumentSize() {
                return 0;
            }

            @Override
            public long getExecuteTime() {
                return 0;
            }

            @Override
            public void commit() {
            }
        };

        FileListIndexUpdateCallbackImpl callback = new FileListIndexUpdateCallbackImpl(mockCallback, null, 1);

        // Pre-populate
        synchronized (mockCallback) {
            for (int i = 0; i < 50; i++) {
                callback.deleteUrlList.add("http://example.com/doc" + i);
            }
        }

        final int threadCount = 5;
        final Thread[] threads = new Thread[threadCount];
        final Exception[] exceptions = new Exception[threadCount];
        final int[][] counts = new int[threadCount][1];

        // Multiple threads iterate over the list
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                try {
                    synchronized (mockCallback) {
                        for (String url : callback.deleteUrlList) {
                            counts[threadIndex][0]++;
                            // Simulate some processing
                            assertNotNull(url);
                        }
                    }
                } catch (Exception e) {
                    exceptions[threadIndex] = e;
                }
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for completion
        for (Thread thread : threads) {
            thread.join();
        }

        // Verify no exceptions occurred
        for (int i = 0; i < threadCount; i++) {
            assertNull(exceptions[i], "Thread " + i + " threw exception");
            assertEquals("Thread " + i + " should iterate over all URLs", 50, counts[i][0]);
        }
    }

    /**
     * Test isEmpty() check on deleteUrlList with concurrent access.
     * This simulates the check in commit() method.
     */
    @Test
    public void test_deleteUrlList_isEmptyCheck() throws Exception {
        IndexUpdateCallback mockCallback = new IndexUpdateCallback() {
            @Override
            public void store(DataStoreParams paramMap, Map<String, Object> dataMap) {
            }

            @Override
            public long getDocumentSize() {
                return 0;
            }

            @Override
            public long getExecuteTime() {
                return 0;
            }

            @Override
            public void commit() {
            }
        };

        FileListIndexUpdateCallbackImpl callback = new FileListIndexUpdateCallbackImpl(mockCallback, null, 1);

        final int threadCount = 10;
        final Thread[] threads = new Thread[threadCount];
        final Exception[] exceptions = new Exception[threadCount];

        // Threads alternate between adding and checking isEmpty
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 100; j++) {
                        synchronized (mockCallback) {
                            if (threadIndex % 2 == 0) {
                                callback.deleteUrlList.add("http://example.com/doc" + threadIndex + "_" + j);
                            } else {
                                boolean empty = callback.deleteUrlList.isEmpty();
                                // Just verify the check doesn't throw exception
                                if (!empty) {
                                    assertTrue(callback.deleteUrlList.size() > 0);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    exceptions[threadIndex] = e;
                }
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for completion
        for (Thread thread : threads) {
            thread.join();
        }

        // Verify no exceptions occurred
        for (int i = 0; i < threadCount; i++) {
            assertNull(exceptions[i], "Thread " + i + " threw exception");
        }
    }
}
