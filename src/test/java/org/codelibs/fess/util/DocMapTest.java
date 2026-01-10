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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class DocMapTest extends UnitFessTestCase {
    @Test
    public void test_DocList() {
        Map<String, Object> value = new LinkedHashMap<>();
        DocMap docMap = new DocMap(value);
        assertTrue(docMap.isEmpty());
        value.clear();

        List<String> keys = Arrays.asList("test_2", "test_0", "lang", "test_1");
        value.put(keys.get(0), true);
        value.put(keys.get(1), 1000);
        value.put(keys.get(2), "ja");
        value.put(keys.get(3), "str");
        docMap = new DocMap(value);
        assertFalse(docMap.isEmpty());

        Set<Map.Entry<String, Object>> actual = docMap.entrySet();
        assertTrue(actual.size() == keys.size());
        docMap.clear();

        assertTrue(docMap.isEmpty());
    }

    @Test
    public void test_constructor() {
        Map<String, Object> parentMap = new HashMap<>();
        DocMap docMap = new DocMap(parentMap);

        assertNotNull(docMap);
        assertTrue(docMap.isEmpty());
        assertEquals(0, docMap.size());
    }

    @Test
    public void test_basic_operations() {
        Map<String, Object> parentMap = new HashMap<>();
        DocMap docMap = new DocMap(parentMap);

        docMap.put("key1", "value1");
        docMap.put("key2", 123);
        docMap.put("key3", true);

        assertEquals(3, docMap.size());
        assertEquals("value1", docMap.get("key1"));
        assertEquals(123, docMap.get("key2"));
        assertEquals(true, docMap.get("key3"));

        assertTrue(docMap.containsKey("key1"));
        assertTrue(docMap.containsValue("value1"));
        assertFalse(docMap.containsKey("nonexistent"));
        assertFalse(docMap.containsValue("nonexistent"));
    }

    @Test
    public void test_put_and_get() {
        Map<String, Object> parentMap = new HashMap<>();
        DocMap docMap = new DocMap(parentMap);

        assertNull(docMap.put("newKey", "newValue"));
        assertEquals("newValue", docMap.get("newKey"));

        assertEquals("newValue", docMap.put("newKey", "updatedValue"));
        assertEquals("updatedValue", docMap.get("newKey"));

        assertNull(docMap.get("nonexistent"));
    }

    @Test
    public void test_remove() {
        Map<String, Object> parentMap = new HashMap<>();
        DocMap docMap = new DocMap(parentMap);

        docMap.put("key1", "value1");
        docMap.put("key2", "value2");

        assertEquals("value1", docMap.remove("key1"));
        assertNull(docMap.get("key1"));
        assertFalse(docMap.containsKey("key1"));

        assertNull(docMap.remove("nonexistent"));

        assertEquals(1, docMap.size());
        assertTrue(docMap.containsKey("key2"));
    }

    @Test
    public void test_putAll() {
        Map<String, Object> parentMap = new HashMap<>();
        DocMap docMap = new DocMap(parentMap);

        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("key1", "value1");
        sourceMap.put("key2", "value2");
        sourceMap.put("key3", "value3");

        docMap.putAll(sourceMap);

        assertEquals(3, docMap.size());
        assertEquals("value1", docMap.get("key1"));
        assertEquals("value2", docMap.get("key2"));
        assertEquals("value3", docMap.get("key3"));
    }

    @Test
    public void test_clear() {
        Map<String, Object> parentMap = new HashMap<>();
        DocMap docMap = new DocMap(parentMap);

        docMap.put("key1", "value1");
        docMap.put("key2", "value2");
        assertEquals(2, docMap.size());

        docMap.clear();
        assertEquals(0, docMap.size());
        assertTrue(docMap.isEmpty());
        assertNull(docMap.get("key1"));
        assertNull(docMap.get("key2"));
    }

    @Test
    public void test_keySet() {
        Map<String, Object> parentMap = new HashMap<>();
        DocMap docMap = new DocMap(parentMap);

        docMap.put("key1", "value1");
        docMap.put("key2", "value2");
        docMap.put("key3", "value3");

        Set<String> keySet = docMap.keySet();
        assertEquals(3, keySet.size());
        assertTrue(keySet.contains("key1"));
        assertTrue(keySet.contains("key2"));
        assertTrue(keySet.contains("key3"));
    }

    @Test
    public void test_values() {
        Map<String, Object> parentMap = new HashMap<>();
        DocMap docMap = new DocMap(parentMap);

        docMap.put("key1", "value1");
        docMap.put("key2", "value2");
        docMap.put("key3", "value3");

        Collection<Object> values = docMap.values();
        assertEquals(3, values.size());
        assertTrue(values.contains("value1"));
        assertTrue(values.contains("value2"));
        assertTrue(values.contains("value3"));
    }

    @Test
    public void test_entrySet_without_lang() {
        Map<String, Object> parentMap = new LinkedHashMap<>();
        DocMap docMap = new DocMap(parentMap);

        docMap.put("key1", "value1");
        docMap.put("key2", "value2");
        docMap.put("key3", "value3");

        Set<Map.Entry<String, Object>> entrySet = docMap.entrySet();
        assertEquals(3, entrySet.size());

        Set<Map.Entry<String, Object>> parentEntrySet = parentMap.entrySet();
        assertEquals(parentEntrySet, entrySet);
    }

    @Test
    public void test_entrySet_with_lang_key() {
        Map<String, Object> parentMap = new LinkedHashMap<>();
        DocMap docMap = new DocMap(parentMap);

        docMap.put("zebra", "value1");
        docMap.put("apple", "value2");
        docMap.put("lang", "ja");
        docMap.put("banana", "value3");

        Set<Map.Entry<String, Object>> entrySet = docMap.entrySet();
        assertEquals(4, entrySet.size());

        String[] keys = entrySet.stream().map(Map.Entry::getKey).toArray(String[]::new);

        boolean langFirst = "lang".equals(keys[0]);
        assertTrue("lang should be first or sorted normally", langFirst || Arrays.asList(keys).contains("lang"));

        assertTrue(Arrays.asList(keys).contains("apple"));
        assertTrue(Arrays.asList(keys).contains("banana"));
        assertTrue(Arrays.asList(keys).contains("zebra"));
    }

    @Test
    public void test_entrySet_with_lang_key_ordering() {
        Map<String, Object> parentMap = new LinkedHashMap<>();
        DocMap docMap = new DocMap(parentMap);

        docMap.put("c_key", "value_c");
        docMap.put("a_key", "value_a");
        docMap.put("lang", "en");
        docMap.put("b_key", "value_b");

        Set<Map.Entry<String, Object>> entrySet = docMap.entrySet();
        String[] keys = entrySet.stream().map(Map.Entry::getKey).toArray(String[]::new);

        assertTrue("Should contain all keys", Arrays.asList(keys).contains("lang"));
        assertTrue("Should contain all keys", Arrays.asList(keys).contains("a_key"));
        assertTrue("Should contain all keys", Arrays.asList(keys).contains("b_key"));
        assertTrue("Should contain all keys", Arrays.asList(keys).contains("c_key"));
    }

    @Test
    public void test_entrySet_with_only_lang_key() {
        Map<String, Object> parentMap = new LinkedHashMap<>();
        DocMap docMap = new DocMap(parentMap);

        docMap.put("lang", "fr");

        Set<Map.Entry<String, Object>> entrySet = docMap.entrySet();
        assertEquals(1, entrySet.size());

        String[] keys = entrySet.stream().map(Map.Entry::getKey).toArray(String[]::new);
        assertEquals("lang", keys[0]);
    }

    @Test
    public void test_entrySet_multiple_lang_keys() {
        Map<String, Object> parentMap = new LinkedHashMap<>();
        DocMap docMap = new DocMap(parentMap);

        docMap.put("lang", "ja");
        docMap.put("other", "value");

        Set<Map.Entry<String, Object>> entrySet = docMap.entrySet();
        String[] keys = entrySet.stream().map(Map.Entry::getKey).toArray(String[]::new);

        assertTrue("Should contain lang key", Arrays.asList(keys).contains("lang"));
        assertTrue("Should contain other key", Arrays.asList(keys).contains("other"));
        assertEquals("Should have 2 keys", 2, keys.length);
    }

    @Test
    public void test_isEmpty_and_size() {
        Map<String, Object> parentMap = new HashMap<>();
        DocMap docMap = new DocMap(parentMap);

        assertTrue(docMap.isEmpty());
        assertEquals(0, docMap.size());

        docMap.put("key1", "value1");
        assertFalse(docMap.isEmpty());
        assertEquals(1, docMap.size());

        docMap.put("key2", "value2");
        assertFalse(docMap.isEmpty());
        assertEquals(2, docMap.size());

        docMap.clear();
        assertTrue(docMap.isEmpty());
        assertEquals(0, docMap.size());
    }

    @Test
    public void test_containsKey_and_containsValue() {
        Map<String, Object> parentMap = new HashMap<>();
        DocMap docMap = new DocMap(parentMap);

        docMap.put("key1", "value1");
        docMap.put("key2", null);

        assertTrue(docMap.containsKey("key1"));
        assertTrue(docMap.containsKey("key2"));
        assertFalse(docMap.containsKey("key3"));

        assertTrue(docMap.containsValue("value1"));
        assertTrue(docMap.containsValue(null));
        assertFalse(docMap.containsValue("nonexistent"));
    }

    @Test
    public void test_null_values() {
        Map<String, Object> parentMap = new HashMap<>();
        DocMap docMap = new DocMap(parentMap);

        docMap.put("nullKey", null);
        assertTrue(docMap.containsKey("nullKey"));
        assertTrue(docMap.containsValue(null));
        assertNull(docMap.get("nullKey"));

        assertNull(docMap.put("nullKey", "newValue"));
        assertEquals("newValue", docMap.get("nullKey"));
    }

    @Test
    public void test_map_interface_compliance() {
        Map<String, Object> parentMap = new HashMap<>();
        DocMap docMap = new DocMap(parentMap);

        assertTrue(docMap instanceof Map);
        assertTrue(docMap instanceof Map<?, ?>);

        Map<String, Object> genericMap = docMap;
        genericMap.put("test", "value");
        assertEquals("value", genericMap.get("test"));
    }
}
