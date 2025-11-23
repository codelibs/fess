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
package org.codelibs.fess.ingest;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.entity.DataStoreParams;
import org.codelibs.fess.unit.UnitFessTestCase;

public class IngesterTest extends UnitFessTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void test_defaultPriority() {
        TestIngester ingester = new TestIngester();
        assertEquals(Ingester.DEFAULT_PRIORITY, ingester.getPriority());
        assertEquals(99, ingester.getPriority());
    }

    public void test_setPriority_valid() {
        TestIngester ingester = new TestIngester();
        ingester.setPriority(50);
        assertEquals(50, ingester.getPriority());

        ingester.setPriority(Ingester.MIN_PRIORITY);
        assertEquals(0, ingester.getPriority());

        ingester.setPriority(Ingester.MAX_PRIORITY);
        assertEquals(999, ingester.getPriority());
    }

    public void test_setPriority_invalid_tooLow() {
        TestIngester ingester = new TestIngester();
        try {
            ingester.setPriority(-1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Priority must be between"));
        }
    }

    public void test_setPriority_invalid_tooHigh() {
        TestIngester ingester = new TestIngester();
        try {
            ingester.setPriority(1000);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Priority must be between"));
        }
    }

    public void test_process_resultData() {
        TestIngester ingester = new TestIngester();
        ResultData resultData = new ResultData();
        ResponseData responseData = new ResponseData();

        ResultData processed = ingester.process(resultData, responseData);
        assertNotNull(processed);
        assertSame(resultData, processed); // Default implementation returns same object
    }

    public void test_process_mapWithAccessResult() {
        TestIngester ingester = new TestIngester();
        Map<String, Object> target = new HashMap<>();
        target.put("url", "http://example.com");

        // AccessResult is abstract, so we create a simple concrete implementation for testing
        @SuppressWarnings("unchecked")
        AccessResult<String> accessResult = new AccessResult<String>() {
            @Override
            public void setLastModified(Long lastModified) {
                // Stub implementation for testing
            }

            @Override
            public Long getLastModified() {
                // Stub implementation for testing
                return null;
            }

            @Override
            public void setContentLength(Long contentLength) {
                // Stub implementation for testing
            }

            @Override
            public Long getContentLength() {
                // Stub implementation for testing
                return null;
            }

            @Override
            public void setAccessResultData(org.codelibs.fess.crawler.entity.AccessResultData<String> data) {
                // Stub implementation for testing
            }

            @Override
            public org.codelibs.fess.crawler.entity.AccessResultData<String> getAccessResultData() {
                // Stub implementation for testing
                return null;
            }

            @Override
            public void setExecutionTime(Integer executionTime) {
                // Stub implementation for testing
            }
        };

        Map<String, Object> processed = ingester.process(target, accessResult);
        assertNotNull(processed);
        assertSame(target, processed); // Default delegates to basic process
    }

    public void test_process_mapWithDataStoreParams() {
        TestIngester ingester = new TestIngester();
        Map<String, Object> target = new HashMap<>();
        target.put("id", "123");

        DataStoreParams params = new DataStoreParams();

        Map<String, Object> processed = ingester.process(target, params);
        assertNotNull(processed);
        assertSame(target, processed); // Default delegates to basic process
    }

    public void test_equals_sameInstance() {
        TestIngester ingester = new TestIngester();
        assertTrue(ingester.equals(ingester));
    }

    public void test_equals_sameClass() {
        TestIngester ingester1 = new TestIngester();
        TestIngester ingester2 = new TestIngester();
        assertTrue(ingester1.equals(ingester2));
        assertTrue(ingester2.equals(ingester1));
    }

    public void test_equals_differentClass() {
        TestIngester ingester1 = new TestIngester();
        AnotherTestIngester ingester2 = new AnotherTestIngester();
        assertFalse(ingester1.equals(ingester2));
    }

    public void test_equals_null() {
        TestIngester ingester = new TestIngester();
        assertFalse(ingester.equals(null));
    }

    public void test_hashCode_differentClass() {
        TestIngester ingester1 = new TestIngester();
        AnotherTestIngester ingester2 = new AnotherTestIngester();
        // Different classes should have different hash codes
        assertFalse(ingester1.hashCode() == ingester2.hashCode());
    }

    public void test_toString() {
        TestIngester ingester = new TestIngester();
        ingester.setPriority(42);
        String str = ingester.toString();
        assertNotNull(str);
        assertTrue(str.contains("TestIngester"));
        assertTrue(str.contains("42"));
    }

    public void test_customProcess() {
        CustomProcessIngester ingester = new CustomProcessIngester();
        Map<String, Object> target = new HashMap<>();

        Map<String, Object> processed = ingester.process(target, (DataStoreParams) null);
        assertNotNull(processed);
        assertTrue(processed.containsKey("processed"));
        assertEquals("true", processed.get("processed"));
    }

    public void test_setPriority_boundaryValues() {
        TestIngester ingester = new TestIngester();

        // Test minimum boundary
        ingester.setPriority(Ingester.MIN_PRIORITY);
        assertEquals(Ingester.MIN_PRIORITY, ingester.getPriority());

        // Test maximum boundary
        ingester.setPriority(Ingester.MAX_PRIORITY);
        assertEquals(Ingester.MAX_PRIORITY, ingester.getPriority());

        // Test middle value
        ingester.setPriority(500);
        assertEquals(500, ingester.getPriority());
    }

    public void test_setPriority_extremeInvalidValues() {
        TestIngester ingester = new TestIngester();

        // Test very large negative value
        try {
            ingester.setPriority(Integer.MIN_VALUE);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Priority must be between"));
        }

        // Test very large positive value
        try {
            ingester.setPriority(Integer.MAX_VALUE);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Priority must be between"));
        }
    }

    public void test_equals_symmetry() {
        TestIngester ingester1 = new TestIngester();
        TestIngester ingester2 = new TestIngester();

        // Test symmetry: if a.equals(b), then b.equals(a)
        assertTrue(ingester1.equals(ingester2));
        assertTrue(ingester2.equals(ingester1));
    }

    public void test_equals_transitivity() {
        TestIngester ingester1 = new TestIngester();
        TestIngester ingester2 = new TestIngester();
        TestIngester ingester3 = new TestIngester();

        // Test transitivity: if a.equals(b) and b.equals(c), then a.equals(c)
        assertTrue(ingester1.equals(ingester2));
        assertTrue(ingester2.equals(ingester3));
        assertTrue(ingester1.equals(ingester3));
    }

    public void test_equals_consistency() {
        TestIngester ingester1 = new TestIngester();
        TestIngester ingester2 = new TestIngester();

        // Multiple invocations should return same result
        assertTrue(ingester1.equals(ingester2));
        assertTrue(ingester1.equals(ingester2));
        assertTrue(ingester1.equals(ingester2));
    }

    public void test_equals_differentPrioritiesSameClass() {
        TestIngester ingester1 = new TestIngester();
        ingester1.setPriority(10);

        TestIngester ingester2 = new TestIngester();
        ingester2.setPriority(20);

        // Should still be equal since same class
        assertTrue(ingester1.equals(ingester2));
    }

    public void test_hashCode_equalObjectsSameHashCode() {
        TestIngester ingester1 = new TestIngester();
        ingester1.setPriority(10);

        TestIngester ingester2 = new TestIngester();
        ingester2.setPriority(20);

        // Equal objects must have same hash code
        assertTrue(ingester1.equals(ingester2));
        assertEquals(ingester1.hashCode(), ingester2.hashCode());
    }

    public void test_hashCode_consistency() {
        TestIngester ingester = new TestIngester();

        int hash1 = ingester.hashCode();
        int hash2 = ingester.hashCode();
        int hash3 = ingester.hashCode();

        // hashCode should be consistent across invocations
        assertEquals(hash1, hash2);
        assertEquals(hash2, hash3);
    }

    public void test_hashCode_notAffectedByPriority() {
        TestIngester ingester = new TestIngester();
        ingester.setPriority(10);
        int hash1 = ingester.hashCode();

        ingester.setPriority(20);
        int hash2 = ingester.hashCode();

        // Hash code should not change when priority changes
        assertEquals(hash1, hash2);
    }

    public void test_toString_includesPriorityAndClass() {
        TestIngester ingester = new TestIngester();
        ingester.setPriority(123);

        String str = ingester.toString();

        assertNotNull(str);
        assertTrue(str.contains("TestIngester"));
        assertTrue(str.contains("123"));
        assertTrue(str.contains("priority"));
    }

    public void test_toString_differentPriorities() {
        TestIngester ingester1 = new TestIngester();
        ingester1.setPriority(10);

        TestIngester ingester2 = new TestIngester();
        ingester2.setPriority(20);

        String str1 = ingester1.toString();
        String str2 = ingester2.toString();

        assertNotNull(str1);
        assertNotNull(str2);
        assertFalse(str1.equals(str2)); // Should be different due to different priorities
    }

    public void test_process_chainedCalls() {
        ChainedProcessIngester ingester = new ChainedProcessIngester();
        Map<String, Object> target = new HashMap<>();
        target.put("initial", "value");

        // Process multiple times with different params
        Map<String, Object> result1 = ingester.process(target, (DataStoreParams) null);
        Map<String, Object> result2 = ingester.process(result1, (AccessResult<String>) null);

        assertNotNull(result2);
        assertTrue(result2.containsKey("initial"));
        assertTrue(result2.containsKey("chained"));
        assertEquals(2, result2.get("count"));
    }

    public void test_process_nullTarget() {
        NullSafeIngester ingester = new NullSafeIngester();

        // Process with null target - implementation should handle gracefully
        Map<String, Object> result = ingester.process(null, (DataStoreParams) null);
        // Implementation might return null or empty map
        // This tests that no exception is thrown
    }

    public void test_process_emptyTarget() {
        CustomProcessIngester ingester = new CustomProcessIngester();
        Map<String, Object> target = new HashMap<>();

        Map<String, Object> result = ingester.process(target, (DataStoreParams) null);
        assertNotNull(result);
        assertTrue(result.containsKey("processed"));
    }

    public void test_process_largeTarget() {
        CustomProcessIngester ingester = new CustomProcessIngester();
        Map<String, Object> target = new HashMap<>();

        // Add many fields
        for (int i = 0; i < 1000; i++) {
            target.put("field" + i, "value" + i);
        }

        Map<String, Object> result = ingester.process(target, (DataStoreParams) null);
        assertNotNull(result);
        assertTrue(result.size() >= 1000);
        assertTrue(result.containsKey("processed"));
    }

    public void test_defaultConstructor() {
        TestIngester ingester = new TestIngester();
        assertNotNull(ingester);
        assertEquals(Ingester.DEFAULT_PRIORITY, ingester.getPriority());
    }

    public void test_priorityConstants() {
        assertEquals(0, Ingester.MIN_PRIORITY);
        assertEquals(999, Ingester.MAX_PRIORITY);
        assertEquals(99, Ingester.DEFAULT_PRIORITY);

        // Verify logical ordering
        assertTrue(Ingester.MIN_PRIORITY < Ingester.DEFAULT_PRIORITY);
        assertTrue(Ingester.DEFAULT_PRIORITY < Ingester.MAX_PRIORITY);
    }

    // Test helper classes
    private static class TestIngester extends Ingester {
        public TestIngester() {
            super();
        }
    }

    private static class AnotherTestIngester extends Ingester {
        public AnotherTestIngester() {
            super();
        }
    }

    private static class CustomProcessIngester extends Ingester {
        @Override
        protected Map<String, Object> process(Map<String, Object> target) {
            target.put("processed", "true");
            return target;
        }
    }

    private static class ChainedProcessIngester extends Ingester {
        private int processCount = 0;

        @Override
        protected Map<String, Object> process(Map<String, Object> target) {
            processCount++;
            target.put("chained", "true");
            target.put("count", processCount);
            return target;
        }
    }

    private static class NullSafeIngester extends Ingester {
        @Override
        protected Map<String, Object> process(Map<String, Object> target) {
            if (target == null) {
                return new HashMap<>();
            }
            target.put("nullsafe", "true");
            return target;
        }
    }
}