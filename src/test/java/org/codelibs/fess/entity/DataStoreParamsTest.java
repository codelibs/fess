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
package org.codelibs.fess.entity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;

public class DataStoreParamsTest extends UnitFessTestCase {

    private DataStoreParams dataStoreParams;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        dataStoreParams = new DataStoreParams();
    }

    // Test default constructor
    public void test_defaultConstructor() {
        DataStoreParams params = new DataStoreParams();
        assertNotNull(params);
        assertNotNull(params.params);
    }

    // Test put and get operations
    public void test_putAndGet() {
        dataStoreParams.put("key1", "value1");
        assertEquals("value1", dataStoreParams.get("key1"));

        dataStoreParams.put("key2", 123);
        assertEquals(123, dataStoreParams.get("key2"));

        dataStoreParams.put("key3", null);
        assertNull(dataStoreParams.get("key3"));
    }

    // Test get for non-existent key
    public void test_getNonExistentKey() {
        assertNull(dataStoreParams.get("nonexistent"));
    }

    // Test getAsString with String value
    public void test_getAsStringWithStringValue() {
        dataStoreParams.put("stringKey", "stringValue");
        assertEquals("stringValue", dataStoreParams.getAsString("stringKey"));
    }

    // Test getAsString with non-String value
    public void test_getAsStringWithNonStringValue() {
        dataStoreParams.put("intKey", 123);
        assertEquals("123", dataStoreParams.getAsString("intKey"));

        dataStoreParams.put("longKey", 456L);
        assertEquals("456", dataStoreParams.getAsString("longKey"));

        dataStoreParams.put("boolKey", true);
        assertEquals("true", dataStoreParams.getAsString("boolKey"));
    }

    // Test getAsString with null value
    public void test_getAsStringWithNullValue() {
        dataStoreParams.put("nullKey", null);
        assertNull(dataStoreParams.getAsString("nullKey"));
    }

    // Test getAsString for non-existent key
    public void test_getAsStringNonExistentKey() {
        assertNull(dataStoreParams.getAsString("nonexistent"));
    }

    // Test getAsString with default value
    public void test_getAsStringWithDefaultValue() {
        dataStoreParams.put("existingKey", "existingValue");
        assertEquals("existingValue", dataStoreParams.getAsString("existingKey", "defaultValue"));

        assertNull(dataStoreParams.getAsString("nonExistentKey"));
        assertEquals("defaultValue", dataStoreParams.getAsString("nonExistentKey", "defaultValue"));

        dataStoreParams.put("nullKey", null);
        assertEquals("defaultValue", dataStoreParams.getAsString("nullKey", "defaultValue"));
    }

    // Test getAsString with default value and non-String value
    public void test_getAsStringWithDefaultValueAndNonStringValue() {
        dataStoreParams.put("intKey", 999);
        assertEquals("999", dataStoreParams.getAsString("intKey", "defaultValue"));
    }

    // Test containsKey
    public void test_containsKey() {
        assertFalse(dataStoreParams.containsKey("key1"));

        dataStoreParams.put("key1", "value1");
        assertTrue(dataStoreParams.containsKey("key1"));

        dataStoreParams.put("key2", null);
        assertTrue(dataStoreParams.containsKey("key2"));
    }

    // Test putAll
    public void test_putAll() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");

        dataStoreParams.putAll(map);

        assertEquals("value1", dataStoreParams.get("key1"));
        assertEquals("value2", dataStoreParams.get("key2"));
        assertEquals("value3", dataStoreParams.get("key3"));
    }

    // Test putAll with empty map
    public void test_putAllEmptyMap() {
        dataStoreParams.put("existingKey", "existingValue");

        Map<String, String> emptyMap = new HashMap<>();
        dataStoreParams.putAll(emptyMap);

        assertEquals("existingValue", dataStoreParams.get("existingKey"));
    }

    // Test newInstance
    public void test_newInstance() {
        dataStoreParams.put("key1", "value1");
        dataStoreParams.put("key2", 123);

        DataStoreParams newParams = dataStoreParams.newInstance();

        assertNotNull(newParams);
        assertNotSame(dataStoreParams, newParams);
        assertEquals("value1", newParams.get("key1"));
        assertEquals(123, newParams.get("key2"));

        // Verify that modifications to new instance don't affect original
        newParams.put("key3", "value3");
        assertTrue(newParams.containsKey("key3"));
        assertFalse(dataStoreParams.containsKey("key3"));

        // Verify that modifications to original don't affect new instance
        dataStoreParams.put("key4", "value4");
        assertTrue(dataStoreParams.containsKey("key4"));
        assertFalse(newParams.containsKey("key4"));
    }

    // Test asMap
    public void test_asMap() {
        dataStoreParams.put("key1", "value1");
        dataStoreParams.put("key2", 123);
        dataStoreParams.put("key3", null);

        Map<String, Object> map = dataStoreParams.asMap();

        assertNotNull(map);
        assertEquals(3, map.size());
        assertEquals("value1", map.get("key1"));
        assertEquals(123, map.get("key2"));
        assertNull(map.get("key3"));

        // Verify that returned map is a copy
        map.put("key4", "value4");
        assertFalse(dataStoreParams.containsKey("key4"));
    }

    // Test asMap with empty params
    public void test_asMapEmpty() {
        Map<String, Object> map = dataStoreParams.asMap();
        assertNotNull(map);
        assertTrue(map.isEmpty());
    }

    // Test getDataMap with ParamMap
    public void test_getDataMapWithParamMap() {
        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put("key1", "value1");
        ParamMap<String, Object> paramMap = new ParamMap<>(innerMap);

        Map<String, Object> result = DataStoreParams.getDataMap(paramMap);
        assertSame(innerMap, result);
    }

    // Test getDataMap with regular Map
    public void test_getDataMapWithRegularMap() {
        Map<String, Object> regularMap = new HashMap<>();
        regularMap.put("key1", "value1");

        Map<String, Object> result = DataStoreParams.getDataMap(regularMap);
        assertSame(regularMap, result);
    }

    // Test protected constructor with Map parameter
    public void test_protectedConstructor() {
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("key1", "value1");
        inputMap.put("key2", 456);

        TestDataStoreParams testParams = new TestDataStoreParams(inputMap);

        assertEquals("value1", testParams.get("key1"));
        assertEquals(456, testParams.get("key2"));

        // Verify that modifications to original map don't affect created instance
        inputMap.put("key3", "value3");
        assertFalse(testParams.containsKey("key3"));
    }

    // Test protected constructor with ParamMap
    public void test_protectedConstructorWithParamMap() {
        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put("key1", "value1");
        ParamMap<String, Object> paramMap = new ParamMap<>(innerMap);

        TestDataStoreParams testParams = new TestDataStoreParams(paramMap);

        assertEquals("value1", testParams.get("key1"));
    }

    // Test case conversion through ParamMap (camelCase to snake_case)
    public void test_caseConversion() {
        dataStoreParams.put("camelCaseKey", "value1");
        // ParamMap should handle case conversion
        assertNotNull(dataStoreParams.get("camelCaseKey"));

        dataStoreParams.put("snake_case_key", "value2");
        assertNotNull(dataStoreParams.get("snake_case_key"));
    }

    // Test with complex object values
    public void test_complexObjectValues() {
        Map<String, String> complexObject = new HashMap<>();
        complexObject.put("nested", "value");

        dataStoreParams.put("complexKey", complexObject);
        assertEquals(complexObject, dataStoreParams.get("complexKey"));

        String asString = dataStoreParams.getAsString("complexKey");
        assertNotNull(asString);
        assertTrue(asString.contains("nested"));
    }

    // Test overwriting existing values
    public void test_overwriteValues() {
        dataStoreParams.put("key1", "originalValue");
        assertEquals("originalValue", dataStoreParams.get("key1"));

        dataStoreParams.put("key1", "newValue");
        assertEquals("newValue", dataStoreParams.get("key1"));

        dataStoreParams.put("key1", null);
        assertNull(dataStoreParams.get("key1"));
    }

    // Test multiple operations in sequence
    public void test_multipleOperations() {
        // Add initial values
        dataStoreParams.put("key1", "value1");
        dataStoreParams.put("key2", 123);

        // Add more values using putAll
        Map<String, String> additionalParams = new HashMap<>();
        additionalParams.put("key3", "value3");
        additionalParams.put("key4", "value4");
        dataStoreParams.putAll(additionalParams);

        // Verify all values exist
        assertTrue(dataStoreParams.containsKey("key1"));
        assertTrue(dataStoreParams.containsKey("key2"));
        assertTrue(dataStoreParams.containsKey("key3"));
        assertTrue(dataStoreParams.containsKey("key4"));

        // Create a copy
        DataStoreParams copy = dataStoreParams.newInstance();

        // Modify original
        dataStoreParams.put("key5", "value5");

        // Verify copy is independent
        assertTrue(dataStoreParams.containsKey("key5"));
        assertFalse(copy.containsKey("key5"));

        // Get as map and verify
        Map<String, Object> map = dataStoreParams.asMap();
        assertEquals(5, map.size());
    }

    // Helper class to test protected constructor
    private static class TestDataStoreParams extends DataStoreParams {
        public TestDataStoreParams(Map<String, Object> params) {
            super(params);
        }
    }
}