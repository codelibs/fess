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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;

/**
 * Test class for DataSerializer.
 * Tests serialization and deserialization with Kryo and JavaBin.
 */
public class DataSerializerTest extends UnitFessTestCase {

    private DataSerializer serializer;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        serializer = new DataSerializer();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        serializer = null;
    }

    /**
     * Test DataSerializer can be instantiated
     */
    public void test_constructor() {
        DataSerializer ds = new DataSerializer();
        assertNotNull("DataSerializer should be instantiable", ds);
    }

    /**
     * Test serialization and deserialization of simple String
     */
    public void test_serializeDeserialize_string() {
        String original = "Hello, World!";

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull("Serialized data should not be null", serialized);
        assertTrue("Serialized data should not be empty", serialized.length > 0);

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull("Deserialized object should not be null", deserialized);
        assertEquals("Deserialized string should match original", original, deserialized);
    }

    /**
     * Test serialization and deserialization of Integer
     */
    public void test_serializeDeserialize_integer() {
        Integer original = 12345;

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull("Serialized data should not be null", serialized);

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull("Deserialized object should not be null", deserialized);
        assertEquals("Deserialized integer should match original", original, deserialized);
    }

    /**
     * Test serialization and deserialization of List
     */
    public void test_serializeDeserialize_list() {
        List<String> original = new ArrayList<>();
        original.add("item1");
        original.add("item2");
        original.add("item3");

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull("Serialized data should not be null", serialized);

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull("Deserialized object should not be null", deserialized);
        assertTrue("Deserialized object should be a List", deserialized instanceof List);

        @SuppressWarnings("unchecked")
        List<String> deserializedList = (List<String>) deserialized;
        assertEquals("List size should match", original.size(), deserializedList.size());
        assertEquals("List contents should match", original, deserializedList);
    }

    /**
     * Test serialization and deserialization of Map
     */
    public void test_serializeDeserialize_map() {
        Map<String, Object> original = new HashMap<>();
        original.put("key1", "value1");
        original.put("key2", 123);
        original.put("key3", true);

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull("Serialized data should not be null", serialized);

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull("Deserialized object should not be null", deserialized);
        assertTrue("Deserialized object should be a Map", deserialized instanceof Map);

        @SuppressWarnings("unchecked")
        Map<String, Object> deserializedMap = (Map<String, Object>) deserialized;
        assertEquals("Map size should match", original.size(), deserializedMap.size());
        assertEquals("Map contents should match", original, deserializedMap);
    }

    /**
     * Test serialization and deserialization of complex nested object
     */
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
        assertNotNull("Serialized data should not be null", serialized);

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull("Deserialized object should not be null", deserialized);
        assertTrue("Deserialized object should be a Map", deserialized instanceof Map);

        @SuppressWarnings("unchecked")
        Map<String, Object> deserializedMap = (Map<String, Object>) deserialized;
        assertEquals("Map size should match", original.size(), deserializedMap.size());
    }

    /**
     * Test serialization and deserialization of array
     */
    public void test_serializeDeserialize_array() {
        String[] original = new String[] { "a", "b", "c", "d" };

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull("Serialized data should not be null", serialized);

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull("Deserialized object should not be null", deserialized);
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
    public void test_serializeDeserialize_null() {
        byte[] serialized = serializer.fromObjectToBinary(null);
        assertNotNull("Serialized data should not be null even for null input", serialized);

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNull("Deserialized object should be null", deserialized);
    }

    /**
     * Test serialization of empty string
     */
    public void test_serializeDeserialize_emptyString() {
        String original = "";

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull("Serialized data should not be null", serialized);

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull("Deserialized object should not be null", deserialized);
        assertEquals("Deserialized empty string should match", original, deserialized);
    }

    /**
     * Test serialization of empty collection
     */
    public void test_serializeDeserialize_emptyList() {
        List<String> original = new ArrayList<>();

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull("Serialized data should not be null", serialized);

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull("Deserialized object should not be null", deserialized);
        assertTrue("Deserialized object should be a List", deserialized instanceof List);

        @SuppressWarnings("unchecked")
        List<String> deserializedList = (List<String>) deserialized;
        assertTrue("Deserialized list should be empty", deserializedList.isEmpty());
    }

    /**
     * Test that multiple serializations produce consistent results
     */
    public void test_serializationConsistency() {
        String original = "Consistency Test";

        byte[] serialized1 = serializer.fromObjectToBinary(original);
        byte[] serialized2 = serializer.fromObjectToBinary(original);

        // Note: Serialized bytes may not be identical due to metadata,
        // but deserialization should produce the same result
        Object deserialized1 = serializer.fromBinaryToObject(serialized1);
        Object deserialized2 = serializer.fromBinaryToObject(serialized2);

        assertEquals("Multiple deserializations should produce identical results",
                deserialized1, deserialized2);
    }

    /**
     * Test that ThreadLocal Kryo instances work correctly
     * This verifies that the ThreadLocal implementation doesn't cause issues
     */
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
    public void test_serializeDeserialize_largeData() {
        List<String> original = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            original.add("Item number " + i);
        }

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull("Serialized data should not be null", serialized);
        assertTrue("Serialized data should have reasonable size", serialized.length > 0);

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull("Deserialized object should not be null", deserialized);

        @SuppressWarnings("unchecked")
        List<String> deserializedList = (List<String>) deserialized;
        assertEquals("Large list size should match", original.size(), deserializedList.size());
    }

    /**
     * Test that serializer handles special characters correctly
     */
    public void test_serializeDeserialize_specialCharacters() {
        String original = "Special: !@#$%^&*(){}[]<>?/\\|~`±§";

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull("Serialized data should not be null", serialized);

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull("Deserialized object should not be null", deserialized);
        assertEquals("Special characters should be preserved", original, deserialized);
    }

    /**
     * Test that serializer handles Unicode correctly
     */
    public void test_serializeDeserialize_unicode() {
        String original = "Unicode: 日本語 中文 한국어 العربية";

        byte[] serialized = serializer.fromObjectToBinary(original);
        assertNotNull("Serialized data should not be null", serialized);

        Object deserialized = serializer.fromBinaryToObject(serialized);
        assertNotNull("Deserialized object should not be null", deserialized);
        assertEquals("Unicode characters should be preserved", original, deserialized);
    }
}
