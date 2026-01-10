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
package org.codelibs.fess.crawler.serializer;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Test class for DataSerializer.
 * Tests serialization and deserialization with Kryo and JavaBin.
 */
public class DataSerializerTest extends UnitFessTestCase {

    private DataSerializer serializer;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        serializer = new DataSerializer();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        serializer = null;
    }

    /**
     * Test DataSerializer can be instantiated
     */
    @Test
    public void test_constructor() {
        DataSerializer ds = new DataSerializer();
        assertNotNull(ds, "DataSerializer should be instantiable");
    }

    /**
     * Test serialization and deserialization of simple String
     */
    @Test
    public void test_serializeDeserialize_string() {
        String original = "Hello, World!";

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull(serialized, "Serialized data should not be null");
        assertTrue("Serialized data should not be empty", serialized.length > 0);

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals("Deserialized string should match original", original, deserialized);
    }

    /**
     * Test serialization and deserialization of Integer
     */
    @Test
    public void test_serializeDeserialize_integer() {
        Integer original = 12345;

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull(serialized, "Serialized data should not be null");

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals("Deserialized integer should match original", original, deserialized);
    }

    /**
     * Test serialization and deserialization of List
     */
    @Test
    public void test_serializeDeserialize_list() {
        List<String> original = new ArrayList<>();
        original.add("item1");
        original.add("item2");
        original.add("item3");

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull(serialized, "Serialized data should not be null");

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertTrue("Deserialized object should be a List", deserialized instanceof List);

        @SuppressWarnings("unchecked")
        List<String> deserializedList = (List<String>) deserialized;
        assertEquals("List size should match", original.size(), deserializedList.size());
        assertEquals("List contents should match", original, deserializedList);
    }

    /**
     * Test serialization and deserialization of Map
     */
    @Test
    public void test_serializeDeserialize_map() {
        Map<String, Object> original = new HashMap<>();
        original.put("key1", "value1");
        original.put("key2", 123);
        original.put("key3", true);

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull(serialized, "Serialized data should not be null");

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertTrue("Deserialized object should be a Map", deserialized instanceof Map);

        @SuppressWarnings("unchecked")
        Map<String, Object> deserializedMap = (Map<String, Object>) deserialized;
        assertEquals("Map size should match", original.size(), deserializedMap.size());
        assertEquals("Map contents should match", original, deserializedMap);
    }

    /**
     * Test serialization and deserialization of complex nested object
     */
    @Test
    public void test_serializeDeserialize_complexObject() {
        Map<String, Object> original = new HashMap<>();
        original.put("string", "test");
        original.put("number", 42);

        List<String> nestedList = new ArrayList<>();
        nestedList.add("nested1");
        nestedList.add("nested2");
        original.put("list", nestedList);

        Map<String, String> nestedMap = new HashMap<>();
        nestedMap.put("nested_key", "nested_value");
        original.put("map", nestedMap);

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull(serialized, "Serialized data should not be null");

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertTrue("Deserialized object should be a Map", deserialized instanceof Map);

        @SuppressWarnings("unchecked")
        Map<String, Object> deserializedMap = (Map<String, Object>) deserialized;
        assertEquals("Map size should match", original.size(), deserializedMap.size());
    }

    /**
     * Test serialization and deserialization of array
     */
    @Test
    public void test_serializeDeserialize_array() {
        String[] original = new String[] { "a", "b", "c", "d" };

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull(serialized, "Serialized data should not be null");

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertTrue("Deserialized object should be an array", deserialized.getClass().isArray());

        String[] deserializedArray = (String[]) deserialized;
        assertEquals("Array length should match", original.length, deserializedArray.length);
        for (int i = 0; i < original.length; i++) {
            assertEquals("Array element should match", original[i], deserializedArray[i]);
        }
    }

    /**
     * Test serialization of null value
     */
    @Test
    public void test_serializeDeserialize_null() {
        byte[] serialized = serializer.fromObjectToBinary(null);
        assertNotNull(serialized, "Serialized data should not be null even for null input");

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNull(deserialized, "Deserialized object should be null");
    }

    /**
     * Test serialization of empty string
     */
    @Test
    public void test_serializeDeserialize_emptyString() {
        String original = "";

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull(serialized, "Serialized data should not be null");

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals("Deserialized empty string should match", original, deserialized);
    }

    /**
     * Test serialization of empty collection
     */
    @Test
    public void test_serializeDeserialize_emptyList() {
        List<String> original = new ArrayList<>();

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull(serialized, "Serialized data should not be null");

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertTrue("Deserialized object should be a List", deserialized instanceof List);

        @SuppressWarnings("unchecked")
        List<String> deserializedList = (List<String>) deserialized;
        assertTrue("Deserialized list should be empty", deserializedList.isEmpty());
    }

    /**
     * Test that multiple serializations produce consistent results
     */
    @Test
    public void test_serializationConsistency() {
        String original = "Consistency Test";

        byte[] serialized1 = serializer.fromObjectToBinary(original);
        byte[] serialized2 = serializer.fromObjectToBinary(original);

        // Note: Serialized bytes may not be identical due to metadata,
        // but deserialization should produce the same result
        Object deserialized1 = serializer.fromBinaryToObject(serialized1);
        Object deserialized2 = serializer.fromBinaryToObject(serialized2);

        assertEquals("Multiple deserializations should produce identical results", deserialized1, deserialized2);
    }

    /**
     * Test that ThreadLocal Kryo instances work correctly
     * This verifies that the ThreadLocal implementation doesn't cause issues
     */
    @Test
    public void test_threadLocalKryo() {
        // Perform multiple serializations to ensure ThreadLocal works
        for (int i = 0; i < 10; i++) {
            String original = "Test " + i;
            byte[] serialized = serializer.fromObjectToBinary(original);
            Object deserialized = serializer.fromBinaryToObject(serialized);
            assertEquals("ThreadLocal Kryo should work correctly", original, deserialized);
        }
    }

    /**
     * Test serialization with large data
     */
    @Test
    public void test_serializeDeserialize_largeData() {
        List<String> original = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            original.add("Item number " + i);
        }

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull(serialized, "Serialized data should not be null");
        assertTrue("Serialized data should have reasonable size", serialized.length > 0);

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull(deserialized, "Deserialized object should not be null");

        @SuppressWarnings("unchecked")
        List<String> deserializedList = (List<String>) deserialized;
        assertEquals("Large list size should match", original.size(), deserializedList.size());
    }

    /**
     * Test that serializer handles special characters correctly
     */
    @Test
    public void test_serializeDeserialize_specialCharacters() {
        String original = "Special: !@#$%^&*(){}[]<>?/\\|~`±§";

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull(serialized, "Serialized data should not be null");

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals("Special characters should be preserved", original, deserialized);
    }

    /**
     * Test that serializer handles Unicode correctly
     */
    @Test
    public void test_serializeDeserialize_unicode() {
        String original = "Unicode: 日本語 中文 한국어 العربية";

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull(serialized, "Serialized data should not be null");

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals("Unicode characters should be preserved", original, deserialized);
    }

    /**
     * Test serialization of Date objects
     */
    @Test
    public void test_serializeDeserialize_date() {
        Date original = new Date();

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull(serialized, "Serialized data should not be null");

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertTrue("Deserialized object should be a Date", deserialized instanceof Date);
        assertEquals("Date should match", original, deserialized);
    }

    /**
     * Test serialization of LinkedHashMap (preserves order)
     */
    @Test
    public void test_serializeDeserialize_linkedHashMap() {
        LinkedHashMap<String, Object> original = new LinkedHashMap<>();
        original.put("first", 1);
        original.put("second", 2);
        original.put("third", 3);

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull(serialized, "Serialized data should not be null");

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertTrue("Deserialized object should be a Map", deserialized instanceof Map);
        assertEquals("Map contents should match", original, deserialized);
    }

    /**
     * Test serialization of LinkedList
     */
    @Test
    public void test_serializeDeserialize_linkedList() {
        LinkedList<String> original = new LinkedList<>();
        original.add("first");
        original.add("second");
        original.add("third");

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull(serialized, "Serialized data should not be null");

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertTrue("Deserialized object should be a List", deserialized instanceof List);
        assertEquals("List contents should match", original, deserialized);
    }

    /**
     * Test serialization of HashSet
     */
    @Test
    public void test_serializeDeserialize_hashSet() {
        HashSet<String> original = new HashSet<>();
        original.add("one");
        original.add("two");
        original.add("three");

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull(serialized, "Serialized data should not be null");

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertTrue("Deserialized object should be a Set", deserialized instanceof Set);
        assertEquals("Set contents should match", original, deserialized);
    }

    /**
     * Test serialization of TreeMap
     */
    @Test
    public void test_serializeDeserialize_treeMap() {
        TreeMap<String, Integer> original = new TreeMap<>();
        original.put("alpha", 1);
        original.put("beta", 2);
        original.put("gamma", 3);

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull(serialized, "Serialized data should not be null");

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertTrue("Deserialized object should be a Map", deserialized instanceof Map);
        assertEquals("Map contents should match", original, deserialized);
    }

    /**
     * Test serialization of Long values
     */
    @Test
    public void test_serializeDeserialize_long() {
        Long original = 9876543210L;

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull(serialized, "Serialized data should not be null");

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals("Long value should match", original, deserialized);
    }

    /**
     * Test serialization of Double values
     */
    @Test
    public void test_serializeDeserialize_double() {
        Double original = 3.14159265359;

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull(serialized, "Serialized data should not be null");

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals("Double value should match", original, deserialized);
    }

    /**
     * Test serialization of Boolean values
     */
    @Test
    public void test_serializeDeserialize_boolean() {
        Boolean original = Boolean.TRUE;

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull(serialized, "Serialized data should not be null");

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals("Boolean value should match", original, deserialized);
    }

    // ========== Security Tests ==========

    /**
     * Security test: Verify that unregistered classes are rejected during serialization.
     * This test ensures that the Kryo registration requirement is working correctly.
     * Unregistered classes should throw an exception to prevent potential RCE attacks.
     */
    @Test
    public void test_security_unregisteredClassRejected() {
        // File class is intentionally not registered to test security
        File unregisteredObject = new File("/tmp/test");

        try {
            serializer.fromObjectToBinary(unregisteredObject);
            fail("Should have thrown an exception for unregistered class");
        } catch (Exception e) {
            // Find the root cause - it should be related to unregistered class
            Throwable rootCause = e;
            while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
                rootCause = rootCause.getCause();
            }
            String message = rootCause.getMessage();
            assertTrue("Exception message should mention unregistered class: " + message,
                    message != null && (message.contains("not registered") || message.contains("Class is not registered")
                            || message.contains("unregistered")));
        }
    }

    /**
     * Security test: Custom class should be rejected.
     * This test verifies that arbitrary user-defined classes cannot be serialized,
     * which is important for preventing gadget chain attacks.
     */
    @Test
    public void test_security_customClassRejected() {
        // Create a simple custom object that is not registered
        CustomTestClass customObject = new CustomTestClass("test");

        try {
            serializer.fromObjectToBinary(customObject);
            fail("Should have thrown an exception for unregistered custom class");
        } catch (Exception e) {
            // Find the root cause - it should be related to unregistered class
            Throwable rootCause = e;
            while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
                rootCause = rootCause.getCause();
            }
            String message = rootCause.getMessage();
            assertTrue("Exception message should mention unregistered class: " + message,
                    message != null && (message.contains("not registered") || message.contains("Class is not registered")
                            || message.contains("unregistered")));
        }
    }

    /**
     * Test that all registered types work correctly.
     * This ensures our registration covers the common use cases.
     */
    @Test
    public void test_allRegisteredTypesWork() {
        // Test various registered types
        Map<String, Object> testData = new HashMap<>();
        testData.put("string", "test");
        testData.put("integer", 42);
        testData.put("long", 123456789L);
        testData.put("double", 3.14);
        testData.put("boolean", true);
        testData.put("date", new Date());

        List<String> list = new ArrayList<>();
        list.add("item1");
        list.add("item2");
        testData.put("list", list);

        Map<String, Integer> nestedMap = new HashMap<>();
        nestedMap.put("nested", 100);
        testData.put("nestedMap", nestedMap);

        byte[] serialized = serializer.fromObjectToBinary(testData);
        assertNotNull(serialized, "Serialized data should not be null");

        @SuppressWarnings("unchecked")
        Map<String, Object> deserialized = (Map<String, Object>) serializer.fromBinaryToObject(serialized);
        assertNotNull(deserialized, "Deserialized data should not be null");
        assertEquals("String should match", "test", deserialized.get("string"));
        assertEquals("Integer should match", 42, deserialized.get("integer"));
        assertEquals("Long should match", 123456789L, deserialized.get("long"));
        assertEquals("Double should match", 3.14, deserialized.get("double"));
        assertEquals("Boolean should match", true, deserialized.get("boolean"));
        assertNotNull(deserialized.get("date"), "Date should not be null");
        assertNotNull(deserialized.get("list"), "List should not be null");
        assertNotNull(deserialized.get("nestedMap"), "Nested map should not be null");
    }

    /**
     * Helper class for testing that unregistered custom classes are rejected.
     */
    private static class CustomTestClass {
        @SuppressWarnings("unused")
        private final String value;

        CustomTestClass(String value) {
            this.value = value;
        }
    }
}
