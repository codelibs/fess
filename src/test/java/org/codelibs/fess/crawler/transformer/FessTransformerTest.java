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
package org.codelibs.fess.crawler.transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Test class for FessTransformer interface.
 * Tests the array handling improvements and data merging functionality.
 */
public class FessTransformerTest extends UnitFessTestCase {

    // Test implementation of FessTransformer for testing
    private static class TestFessTransformer implements FessTransformer {
        private static final Logger logger = LogManager.getLogger(TestFessTransformer.class);

        @Override
        public FessConfig getFessConfig() {
            return ComponentUtil.getFessConfig();
        }

        @Override
        public Logger getLogger() {
            return logger;
        }
    }

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test putResultDataBody with Object[] arrays
     * This tests the improved array handling using java.lang.reflect.Array
     */
    @Test
    public void test_putResultDataBody_withObjectArray() {
        TestFessTransformer transformer = new TestFessTransformer();
        Map<String, Object> dataMap = new HashMap<>();

        // First add a single value
        transformer.putResultDataBody(dataMap, "key1", "value1");
        assertEquals("value1", dataMap.get("key1"));

        // Add an array - should merge with existing value
        String[] newValues = new String[] { "value2", "value3" };
        transformer.putResultDataBody(dataMap, "key1", newValues);

        Object result = dataMap.get("key1");
        assertTrue(result instanceof Object[]);
        Object[] resultArray = (Object[]) result;
        assertEquals(3, resultArray.length);
        assertEquals("value1", resultArray[0]);
        assertEquals("value2", resultArray[1]);
        assertEquals("value3", resultArray[2]);
    }

    /**
     * Test putResultDataBody with primitive int array
     * This tests that primitive arrays are handled correctly
     */
    @Test
    public void test_putResultDataBody_withPrimitiveArray() {
        TestFessTransformer transformer = new TestFessTransformer();
        Map<String, Object> dataMap = new HashMap<>();

        // First add a single value
        transformer.putResultDataBody(dataMap, "numbers", 1);
        assertEquals(1, dataMap.get("numbers"));

        // Add a primitive int array
        int[] newValues = new int[] { 2, 3, 4 };
        transformer.putResultDataBody(dataMap, "numbers", newValues);

        Object result = dataMap.get("numbers");
        assertTrue(result instanceof Object[]);
        Object[] resultArray = (Object[]) result;
        assertEquals(4, resultArray.length);
        assertEquals(1, resultArray[0]);
        assertEquals(2, resultArray[1]);
        assertEquals(3, resultArray[2]);
        assertEquals(4, resultArray[3]);
    }

    /**
     * Test putResultDataBody with Collection as existing value
     */
    @Test
    public void test_putResultDataBody_withCollectionExistingValue() {
        TestFessTransformer transformer = new TestFessTransformer();
        Map<String, Object> dataMap = new HashMap<>();

        // Add a List as initial value
        List<String> initialList = new ArrayList<>();
        initialList.add("item1");
        initialList.add("item2");
        dataMap.put("items", initialList);

        // Add a new value
        transformer.putResultDataBody(dataMap, "items", "item3");

        Object result = dataMap.get("items");
        assertTrue(result instanceof Object[]);
        Object[] resultArray = (Object[]) result;
        assertEquals(3, resultArray.length);
        assertEquals("item1", resultArray[0]);
        assertEquals("item2", resultArray[1]);
        assertEquals("item3", resultArray[2]);
    }

    /**
     * Test putResultDataBody with multiple array additions
     */
    @Test
    public void test_putResultDataBody_multipleArrayAdditions() {
        TestFessTransformer transformer = new TestFessTransformer();
        Map<String, Object> dataMap = new HashMap<>();

        // First addition
        transformer.putResultDataBody(dataMap, "tags", "tag1");

        // Second addition with array
        String[] tags2 = new String[] { "tag2", "tag3" };
        transformer.putResultDataBody(dataMap, "tags", tags2);

        // Third addition with another array
        String[] tags3 = new String[] { "tag4", "tag5" };
        transformer.putResultDataBody(dataMap, "tags", tags3);

        Object result = dataMap.get("tags");
        assertTrue(result instanceof Object[]);
        Object[] resultArray = (Object[]) result;
        assertEquals(5, resultArray.length);
        assertEquals("tag1", resultArray[0]);
        assertEquals("tag2", resultArray[1]);
        assertEquals("tag3", resultArray[2]);
        assertEquals("tag4", resultArray[3]);
        assertEquals("tag5", resultArray[4]);
    }

    /**
     * Test putResultDataBody with single value after array
     */
    @Test
    public void test_putResultDataBody_singleValueAfterArray() {
        TestFessTransformer transformer = new TestFessTransformer();
        Map<String, Object> dataMap = new HashMap<>();

        // Add array first
        String[] initialArray = new String[] { "a", "b", "c" };
        transformer.putResultDataBody(dataMap, "letters", initialArray);

        // Add single value
        transformer.putResultDataBody(dataMap, "letters", "d");

        Object result = dataMap.get("letters");
        assertTrue(result instanceof Object[]);
        Object[] resultArray = (Object[]) result;
        assertEquals(4, resultArray.length);
        assertEquals("a", resultArray[0]);
        assertEquals("b", resultArray[1]);
        assertEquals("c", resultArray[2]);
        assertEquals("d", resultArray[3]);
    }

    /**
     * Test putResultDataBody with empty array
     */
    @Test
    public void test_putResultDataBody_withEmptyArray() {
        TestFessTransformer transformer = new TestFessTransformer();
        Map<String, Object> dataMap = new HashMap<>();

        transformer.putResultDataBody(dataMap, "empty", "value1");

        String[] emptyArray = new String[] {};
        transformer.putResultDataBody(dataMap, "empty", emptyArray);

        Object result = dataMap.get("empty");
        assertTrue(result instanceof Object[]);
        Object[] resultArray = (Object[]) result;
        assertEquals(1, resultArray.length);
        assertEquals("value1", resultArray[0]);
    }

    /**
     * Test putResultDataBody with different data types in array
     */
    @Test
    public void test_putResultDataBody_mixedTypes() {
        TestFessTransformer transformer = new TestFessTransformer();
        Map<String, Object> dataMap = new HashMap<>();

        transformer.putResultDataBody(dataMap, "mixed", "string");
        transformer.putResultDataBody(dataMap, "mixed", 123);
        transformer.putResultDataBody(dataMap, "mixed", true);

        Object result = dataMap.get("mixed");
        assertTrue(result instanceof Object[]);
        Object[] resultArray = (Object[]) result;
        assertEquals(3, resultArray.length);
        assertEquals("string", resultArray[0]);
        assertEquals(123, resultArray[1]);
        assertEquals(true, resultArray[2]);
    }

    /**
     * Test putResultDataBody replaces value when key doesn't exist
     */
    @Test
    public void test_putResultDataBody_newKey() {
        TestFessTransformer transformer = new TestFessTransformer();
        Map<String, Object> dataMap = new HashMap<>();

        transformer.putResultDataBody(dataMap, "newKey", "newValue");

        assertEquals("newValue", dataMap.get("newKey"));
        assertEquals(1, dataMap.size());
    }

    /**
     * Test putResultDataBody with null handling
     */
    @Test
    public void test_putResultDataBody_withNullValues() {
        TestFessTransformer transformer = new TestFessTransformer();
        Map<String, Object> dataMap = new HashMap<>();

        // Add null value
        transformer.putResultDataBody(dataMap, "nullable", null);
        assertNull(dataMap.get("nullable"));

        // Add actual value after null
        transformer.putResultDataBody(dataMap, "nullable", "actualValue");

        Object result = dataMap.get("nullable");
        assertTrue(result instanceof Object[]);
        Object[] resultArray = (Object[]) result;
        assertEquals(2, resultArray.length);
        assertNull(resultArray[0]);
        assertEquals("actualValue", resultArray[1]);
    }

    /**
     * Test array index calculation correctness
     * This verifies the fix for the original bug where values[values.length - 1 + i]
     * would cause ArrayIndexOutOfBoundsException
     */
    @Test
    public void test_arrayIndexCalculation() {
        TestFessTransformer transformer = new TestFessTransformer();
        Map<String, Object> dataMap = new HashMap<>();

        // Add initial value
        transformer.putResultDataBody(dataMap, "test", "initial");

        // Add array with multiple elements - this should not throw ArrayIndexOutOfBoundsException
        String[] newValues = new String[] { "item1", "item2", "item3", "item4", "item5" };
        try {
            transformer.putResultDataBody(dataMap, "test", newValues);
        } catch (ArrayIndexOutOfBoundsException e) {
            fail("Array index calculation is incorrect: " + e.getMessage());
        }

        Object result = dataMap.get("test");
        assertTrue(result instanceof Object[]);
        Object[] resultArray = (Object[]) result;
        assertEquals(6, resultArray.length);

        // Verify all values are in correct positions
        assertEquals("initial", resultArray[0]);
        assertEquals("item1", resultArray[1]);
        assertEquals("item2", resultArray[2]);
        assertEquals("item3", resultArray[3]);
        assertEquals("item4", resultArray[4]);
        assertEquals("item5", resultArray[5]);
    }
}
