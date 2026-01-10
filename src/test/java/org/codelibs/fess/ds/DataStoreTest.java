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
package org.codelibs.fess.ds;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.fess.ds.callback.IndexUpdateCallback;
import org.codelibs.fess.entity.DataStoreParams;
import org.codelibs.fess.opensearch.config.exentity.DataConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class DataStoreTest extends UnitFessTestCase {

    private DataStore dataStore;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }

    @Test
    public void test_store_basic() {
        // Create a simple DataStore implementation for testing
        final AtomicBoolean storeCalled = new AtomicBoolean(false);
        final AtomicReference<DataConfig> capturedConfig = new AtomicReference<>();
        final AtomicReference<IndexUpdateCallback> capturedCallback = new AtomicReference<>();
        final AtomicReference<DataStoreParams> capturedParams = new AtomicReference<>();

        dataStore = new DataStore() {
            @Override
            public void store(DataConfig config, IndexUpdateCallback callback, DataStoreParams initParamMap) {
                storeCalled.set(true);
                capturedConfig.set(config);
                capturedCallback.set(callback);
                capturedParams.set(initParamMap);
            }

            @Override
            public void stop() {
                // Do nothing
            }
        };

        // Create test data
        DataConfig config = new DataConfig();
        config.setName("test-config");

        DataStoreParams params = new DataStoreParams();
        params.put("key1", "value1");
        params.put("key2", "value2");

        IndexUpdateCallback callback = new TestIndexUpdateCallback();

        // Execute store method
        dataStore.store(config, callback, params);

        // Verify store was called
        assertTrue(storeCalled.get());
        assertNotNull(capturedConfig.get());
        assertEquals("test-config", capturedConfig.get().getName());
        assertNotNull(capturedCallback.get());
        assertNotNull(capturedParams.get());
        assertEquals("value1", capturedParams.get().getAsString("key1"));
        assertEquals("value2", capturedParams.get().getAsString("key2"));
    }

    @Test
    public void test_store_withNullConfig() {
        // Test store with null config
        final AtomicBoolean storeCalled = new AtomicBoolean(false);

        dataStore = new DataStore() {
            @Override
            public void store(DataConfig config, IndexUpdateCallback callback, DataStoreParams initParamMap) {
                storeCalled.set(true);
                assertNull(config);
            }

            @Override
            public void stop() {
                // Do nothing
            }
        };

        DataStoreParams params = new DataStoreParams();
        IndexUpdateCallback callback = new TestIndexUpdateCallback();

        // Execute with null config
        dataStore.store(null, callback, params);

        assertTrue(storeCalled.get());
    }

    @Test
    public void test_store_withNullCallback() {
        // Test store with null callback
        final AtomicBoolean storeCalled = new AtomicBoolean(false);

        dataStore = new DataStore() {
            @Override
            public void store(DataConfig config, IndexUpdateCallback callback, DataStoreParams initParamMap) {
                storeCalled.set(true);
                assertNull(callback);
            }

            @Override
            public void stop() {
                // Do nothing
            }
        };

        DataConfig config = new DataConfig();
        DataStoreParams params = new DataStoreParams();

        // Execute with null callback
        dataStore.store(config, null, params);

        assertTrue(storeCalled.get());
    }

    @Test
    public void test_store_withNullParams() {
        // Test store with null params
        final AtomicBoolean storeCalled = new AtomicBoolean(false);

        dataStore = new DataStore() {
            @Override
            public void store(DataConfig config, IndexUpdateCallback callback, DataStoreParams initParamMap) {
                storeCalled.set(true);
                assertNull(initParamMap);
            }

            @Override
            public void stop() {
                // Do nothing
            }
        };

        DataConfig config = new DataConfig();
        IndexUpdateCallback callback = new TestIndexUpdateCallback();

        // Execute with null params
        dataStore.store(config, callback, null);

        assertTrue(storeCalled.get());
    }

    @Test
    public void test_stop() {
        // Test stop method
        final AtomicBoolean stopCalled = new AtomicBoolean(false);

        dataStore = new DataStore() {
            @Override
            public void store(DataConfig config, IndexUpdateCallback callback, DataStoreParams initParamMap) {
                // Do nothing
            }

            @Override
            public void stop() {
                stopCalled.set(true);
            }
        };

        // Execute stop
        dataStore.stop();

        assertTrue(stopCalled.get());
    }

    @Test
    public void test_stop_multipleCalls() {
        // Test multiple stop calls
        final AtomicInteger stopCallCount = new AtomicInteger(0);

        dataStore = new DataStore() {
            @Override
            public void store(DataConfig config, IndexUpdateCallback callback, DataStoreParams initParamMap) {
                // Do nothing
            }

            @Override
            public void stop() {
                stopCallCount.incrementAndGet();
            }
        };

        // Execute stop multiple times
        dataStore.stop();
        dataStore.stop();
        dataStore.stop();

        assertEquals(3, stopCallCount.get());
    }

    @Test
    public void test_store_and_stop_lifecycle() {
        // Test full lifecycle: store then stop
        final AtomicBoolean storeCalled = new AtomicBoolean(false);
        final AtomicBoolean stopCalled = new AtomicBoolean(false);
        final AtomicBoolean isActive = new AtomicBoolean(false);

        dataStore = new DataStore() {
            @Override
            public void store(DataConfig config, IndexUpdateCallback callback, DataStoreParams initParamMap) {
                storeCalled.set(true);
                isActive.set(true);
            }

            @Override
            public void stop() {
                stopCalled.set(true);
                isActive.set(false);
            }
        };

        DataConfig config = new DataConfig();
        DataStoreParams params = new DataStoreParams();
        IndexUpdateCallback callback = new TestIndexUpdateCallback();

        // Execute lifecycle
        dataStore.store(config, callback, params);
        assertTrue(storeCalled.get());
        assertTrue(isActive.get());

        dataStore.stop();
        assertTrue(stopCalled.get());
        assertFalse(isActive.get());
    }

    @Test
    public void test_store_withComplexParams() {
        // Test store with complex parameters
        final AtomicReference<DataStoreParams> capturedParams = new AtomicReference<>();

        dataStore = new DataStore() {
            @Override
            public void store(DataConfig config, IndexUpdateCallback callback, DataStoreParams initParamMap) {
                capturedParams.set(initParamMap);
            }

            @Override
            public void stop() {
                // Do nothing
            }
        };

        DataConfig config = new DataConfig();
        DataStoreParams params = new DataStoreParams();

        // Add various types of parameters
        params.put("string", "test-value");
        params.put("integer", 42);
        params.put("long", 123456789L);
        params.put("boolean", true);
        params.put("double", 3.14);

        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("nested-key", "nested-value");
        params.put("map", nestedMap);

        IndexUpdateCallback callback = new TestIndexUpdateCallback();

        // Execute store
        dataStore.store(config, callback, params);

        // Verify all parameters were passed correctly
        DataStoreParams captured = capturedParams.get();
        assertNotNull(captured);
        assertEquals("test-value", captured.getAsString("string"));
        assertEquals(42, captured.get("integer"));
        assertEquals(123456789L, captured.get("long"));
        assertEquals(true, captured.get("boolean"));
        assertEquals(3.14, captured.get("double"));
        assertNotNull(captured.get("map"));
    }

    @Test
    public void test_store_withCallbackInteraction() {
        // Test store with callback interaction
        final TestIndexUpdateCallback callback = new TestIndexUpdateCallback();

        dataStore = new DataStore() {
            @Override
            public void store(DataConfig config, IndexUpdateCallback callback, DataStoreParams initParamMap) {
                // Simulate storing documents
                Map<String, Object> doc1 = new HashMap<>();
                doc1.put("id", "1");
                doc1.put("title", "Document 1");
                callback.store(initParamMap, doc1);

                Map<String, Object> doc2 = new HashMap<>();
                doc2.put("id", "2");
                doc2.put("title", "Document 2");
                callback.store(initParamMap, doc2);

                // Commit the changes
                callback.commit();
            }

            @Override
            public void stop() {
                // Do nothing
            }
        };

        DataConfig config = new DataConfig();
        DataStoreParams params = new DataStoreParams();

        // Execute store
        dataStore.store(config, callback, params);

        // Verify callback was used correctly
        assertEquals(2, callback.getDocumentSize());
        assertTrue(callback.isCommitted());
    }

    @Test
    public void test_multiple_datastore_instances() {
        // Test multiple DataStore instances working independently
        final AtomicInteger store1CallCount = new AtomicInteger(0);
        final AtomicInteger store2CallCount = new AtomicInteger(0);

        DataStore dataStore1 = new DataStore() {
            @Override
            public void store(DataConfig config, IndexUpdateCallback callback, DataStoreParams initParamMap) {
                store1CallCount.incrementAndGet();
            }

            @Override
            public void stop() {
                // Do nothing
            }
        };

        DataStore dataStore2 = new DataStore() {
            @Override
            public void store(DataConfig config, IndexUpdateCallback callback, DataStoreParams initParamMap) {
                store2CallCount.incrementAndGet();
            }

            @Override
            public void stop() {
                // Do nothing
            }
        };

        DataConfig config = new DataConfig();
        DataStoreParams params = new DataStoreParams();
        IndexUpdateCallback callback = new TestIndexUpdateCallback();

        // Execute on both instances
        dataStore1.store(config, callback, params);
        dataStore2.store(config, callback, params);
        dataStore2.store(config, callback, params);

        // Verify independent operation
        assertEquals(1, store1CallCount.get());
        assertEquals(2, store2CallCount.get());
    }

    // Test implementation of IndexUpdateCallback for testing purposes
    private static class TestIndexUpdateCallback implements IndexUpdateCallback {
        private final AtomicLong documentCount = new AtomicLong(0);
        private final AtomicLong executeTime = new AtomicLong(0);
        private final AtomicBoolean committed = new AtomicBoolean(false);
        private final long startTime = System.currentTimeMillis();

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
            executeTime.set(System.currentTimeMillis() - startTime);
            return executeTime.get();
        }

        @Override
        public void commit() {
            committed.set(true);
        }

        public boolean isCommitted() {
            return committed.get();
        }
    }
}