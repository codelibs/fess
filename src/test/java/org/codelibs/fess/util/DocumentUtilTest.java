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
package org.codelibs.fess.util;

import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;

public class DocumentUtilTest extends UnitFessTestCase {

    public void test_string() {
        Map<String, Object> doc = new HashMap<>();

        String expected = "1";
        doc.put("key1", expected);
        assertEquals(expected, DocumentUtil.getValue(doc, "key1", String.class));
        assertEquals(Integer.parseInt(expected), DocumentUtil.getValue(doc, "key1", Integer.class).intValue());
        assertEquals(Long.parseLong(expected), DocumentUtil.getValue(doc, "key1", Long.class).longValue());
        assertEquals(Float.parseFloat(expected), DocumentUtil.getValue(doc, "key1", Float.class).floatValue());
        assertEquals(Double.parseDouble(expected), DocumentUtil.getValue(doc, "key1", Double.class).doubleValue());

        assertNull(DocumentUtil.getValue(doc, "key2", String.class));
    }

    public void test_strings() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("key1", new String[] { "aaa", "bbb" });
        assertArrayEquals(new String[] { "aaa", "bbb" }, DocumentUtil.getValue(doc, "key1", String[].class));
        assertEquals(Arrays.asList("aaa", "bbb"), (List<String>) DocumentUtil.getValue(doc, "key1", List.class));
    }

    public void test_list() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("key1", Arrays.asList("aaa", "bbb"));
        assertArrayEquals(new String[] { "aaa", "bbb" }, DocumentUtil.getValue(doc, "key1", String[].class));
        assertEquals(Arrays.asList("aaa", "bbb"), (List<String>) DocumentUtil.getValue(doc, "key1", List.class));
    }

    public void test_integer() {
        Map<String, Object> doc = new HashMap<>();

        int expected3 = 999;
        doc.put("key3", expected3);
        assertEquals(expected3, DocumentUtil.getValue(doc, "key3", Integer.class));

        doc.put("key9", new ArrayList<Integer>(Arrays.asList(777, 888, 999)));
        assertEquals(777, DocumentUtil.getValue(doc, "key9", Integer.class));
    }

    public void test_date() {
        Map<String, Object> doc = new HashMap<>();

        Date expected4 = new Date();
        doc.put("key4", expected4);
        assertEquals(expected4, DocumentUtil.getValue(doc, "key4", Date.class));
    }

    public void test_long() {
        Map<String, Object> doc = new HashMap<>();

        long expected5 = 999999999999999999L;
        doc.put("key5", expected5);
        assertEquals(expected5, DocumentUtil.getValue(doc, "key5", Long.class).longValue());
    }

    public void test_double() {
        Map<String, Object> doc = new HashMap<>();

        double expected6 = 999.999;
        doc.put("key6", expected6);
        assertEquals(expected6, DocumentUtil.getValue(doc, "key6", Double.class));
    }

    public void test_float() {
        Map<String, Object> doc = new HashMap<>();

        float expected7 = 999.999f;
        doc.put("key7", expected7);
        assertEquals(expected7, DocumentUtil.getValue(doc, "key7", Float.class));
    }

    public void test_boolean() {
        Map<String, Object> doc = new HashMap<>();

        boolean expected8 = true;
        doc.put("key8", expected8);
        assertEquals(expected8, DocumentUtil.getValue(doc, "key8", Boolean.class).booleanValue());
    }

    public void test_getValue_with_null_map() {
        assertNull(DocumentUtil.getValue(null, "key", String.class));
        assertNull(DocumentUtil.getValue(null, "key", Integer.class));
    }

    public void test_getValue_with_null_key() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("key", "value");
        assertNull(DocumentUtil.getValue(doc, null, String.class));
        assertNull(DocumentUtil.getValue(doc, null, Integer.class));
    }

    public void test_getValue_with_null_value() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("key", null);
        assertNull(DocumentUtil.getValue(doc, "key", String.class));
        assertNull(DocumentUtil.getValue(doc, "key", Integer.class));
    }

    public void test_getValue_with_default_value() {
        Map<String, Object> doc = new HashMap<>();

        assertEquals("default", DocumentUtil.getValue(doc, "nonexistent", String.class, "default"));
        assertEquals(Integer.valueOf(42), DocumentUtil.getValue(doc, "nonexistent", Integer.class, 42));
        assertEquals(Long.valueOf(123L), DocumentUtil.getValue(doc, "nonexistent", Long.class, 123L));

        doc.put("key", "value");
        assertEquals("value", DocumentUtil.getValue(doc, "key", String.class, "default"));

        doc.put("key", null);
        assertEquals("default", DocumentUtil.getValue(doc, "key", String.class, "default"));
    }

    public void test_list_with_null_elements() {
        Map<String, Object> doc = new HashMap<>();
        List<String> listWithNulls = Arrays.asList("a", null, "b", null, "c");
        doc.put("key", listWithNulls);

        String[] result = DocumentUtil.getValue(doc, "key", String[].class);
        assertArrayEquals(new String[] { "a", "b", "c" }, result);
    }

    public void test_empty_list() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("key", new ArrayList<>());

        assertNull(DocumentUtil.getValue(doc, "key", String.class));
        assertNull(DocumentUtil.getValue(doc, "key", Integer.class));

        List<String> result = DocumentUtil.getValue(doc, "key", List.class);
        assertEquals(new ArrayList<>(), result);
    }

    public void test_empty_string_array() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("key", new String[0]);

        assertNull(DocumentUtil.getValue(doc, "key", String.class));
        assertNull(DocumentUtil.getValue(doc, "key", Integer.class));

        String[] result = DocumentUtil.getValue(doc, "key", String[].class);
        assertArrayEquals(new String[0], result);
    }

    public void test_type_conversion_from_string() {
        Map<String, Object> doc = new HashMap<>();

        doc.put("intStr", "123");
        assertEquals(Integer.valueOf(123), DocumentUtil.getValue(doc, "intStr", Integer.class));

        doc.put("longStr", "123456789");
        assertEquals(Long.valueOf(123456789L), DocumentUtil.getValue(doc, "longStr", Long.class));

        doc.put("doubleStr", "123.45");
        assertEquals(Double.valueOf(123.45), DocumentUtil.getValue(doc, "doubleStr", Double.class));

        doc.put("floatStr", "123.45");
        assertEquals(Float.valueOf(123.45f), DocumentUtil.getValue(doc, "floatStr", Float.class));

        doc.put("boolStr", "true");
        assertEquals(Boolean.TRUE, DocumentUtil.getValue(doc, "boolStr", Boolean.class));

        doc.put("boolStr2", "false");
        assertEquals(Boolean.FALSE, DocumentUtil.getValue(doc, "boolStr2", Boolean.class));
    }

    public void test_boolean_conversion() {
        Map<String, Object> doc = new HashMap<>();

        doc.put("boolTrue", true);
        assertEquals(Boolean.TRUE, DocumentUtil.getValue(doc, "boolTrue", Boolean.class));

        doc.put("boolFalse", false);
        assertEquals(Boolean.FALSE, DocumentUtil.getValue(doc, "boolFalse", Boolean.class));

        doc.put("boolStr", "anything");
        assertEquals(Boolean.FALSE, DocumentUtil.getValue(doc, "boolStr", Boolean.class));
    }

    public void test_unsupported_type_conversion() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("key", "value");

        assertNull(DocumentUtil.getValue(doc, "key", StringBuilder.class));

        assertEquals("value", DocumentUtil.getValue(doc, "key", String.class));
    }

    public void test_list_to_string_array_conversion() {
        Map<String, Object> doc = new HashMap<>();
        List<Object> mixedList = Arrays.asList("string", 123, true, null, 45.67);
        doc.put("key", mixedList);

        String[] result = DocumentUtil.getValue(doc, "key", String[].class);
        assertArrayEquals(new String[] { "string", "123", "true", "45.67" }, result);
    }

    public void test_string_array_to_list_conversion() {
        Map<String, Object> doc = new HashMap<>();
        String[] array = { "a", "b", "c" };
        doc.put("key", array);

        List<String> result = DocumentUtil.getValue(doc, "key", List.class);
        assertEquals(Arrays.asList("a", "b", "c"), result);
    }

    public void test_first_element_from_list() {
        Map<String, Object> doc = new HashMap<>();
        List<String> list = Arrays.asList("first", "second", "third");
        doc.put("key", list);

        assertEquals("first", DocumentUtil.getValue(doc, "key", String.class));

        List<String> numList = Arrays.asList("123", "456", "789");
        doc.put("numKey", numList);
        assertEquals(Integer.valueOf(123), DocumentUtil.getValue(doc, "numKey", Integer.class));
    }

    public void test_first_element_from_string_array() {
        Map<String, Object> doc = new HashMap<>();
        String[] array = { "first", "second", "third" };
        doc.put("key", array);

        assertEquals("first", DocumentUtil.getValue(doc, "key", String.class));
    }

    public void test_encodeUrl_basic() {
        assertEquals("hello", DocumentUtil.encodeUrl("hello"));
        assertEquals("hello+world", DocumentUtil.encodeUrl("hello world"));
        assertEquals("test+value", DocumentUtil.encodeUrl("test+value"));
    }

    public void test_encodeUrl_special_characters() {
        assertEquals("test&value", DocumentUtil.encodeUrl("test&value"));
        assertEquals("test=value", DocumentUtil.encodeUrl("test=value"));
        assertEquals("test?value", DocumentUtil.encodeUrl("test?value"));
        assertEquals("test#value", DocumentUtil.encodeUrl("test#value"));
    }

    public void test_encodeUrl_already_encoded() {
        assertEquals("hello", DocumentUtil.encodeUrl("hello"));
        assertEquals("hello-world", DocumentUtil.encodeUrl("hello-world"));
        assertEquals("hello_world", DocumentUtil.encodeUrl("hello_world"));
        assertEquals("hello.world", DocumentUtil.encodeUrl("hello.world"));
    }

    public void test_encodeUrl_empty_and_null() {
        assertEquals("", DocumentUtil.encodeUrl(""));
        try {
            DocumentUtil.encodeUrl(null);
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    public void test_encodeUrl_unicode() {
        assertEquals("%3F", DocumentUtil.encodeUrl("あ"));
        assertEquals("%3F%3F%3F", DocumentUtil.encodeUrl("あいう"));
    }

    public void test_complex_document_structure() {
        Map<String, Object> doc = new HashMap<>();

        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("nestedKey", "nestedValue");
        doc.put("nested", nestedMap);

        List<Map<String, Object>> listOfMaps = new ArrayList<>();
        listOfMaps.add(nestedMap);
        doc.put("listOfMaps", listOfMaps);

        assertEquals("nestedValue", DocumentUtil.getValue(nestedMap, "nestedKey", String.class));

        List<Map<String, Object>> result = DocumentUtil.getValue(doc, "listOfMaps", List.class);
        assertEquals(listOfMaps, result);
    }
}
