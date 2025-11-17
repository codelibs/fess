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

        @SuppressWarnings("unchecked")
        AccessResult<String> accessResult = new AccessResult<>();

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

    public void test_hashCode_consistency() {
        TestIngester ingester1 = new TestIngester();
        TestIngester ingester2 = new TestIngester();
        assertEquals(ingester1.hashCode(), ingester2.hashCode());
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
}