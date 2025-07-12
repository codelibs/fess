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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codelibs.fess.opensearch.user.bsentity.BsUser;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.lastaflute.web.response.render.RenderData;

public class RenderDataUtilTest extends UnitFessTestCase {

    public void test_register_null() {
        RenderData data = new RenderData();
        RenderDataUtil.register(data, "key1", null);
        assertNull(data.getDataMap().get("key1"));
    }

    public void test_register_string() {
        RenderData data = new RenderData();
        RenderDataUtil.register(data, "key1", "test value");
        assertEquals("test value", data.getDataMap().get("key1"));

        RenderDataUtil.register(data, "key2", "");
        assertEquals("", data.getDataMap().get("key2"));
    }

    public void test_register_primitives() {
        RenderData data = new RenderData();

        RenderDataUtil.register(data, "int", 123);
        assertEquals(123, data.getDataMap().get("int"));

        RenderDataUtil.register(data, "long", 456L);
        assertEquals(456L, data.getDataMap().get("long"));

        RenderDataUtil.register(data, "boolean", true);
        assertEquals(true, data.getDataMap().get("boolean"));

        RenderDataUtil.register(data, "double", 3.14);
        assertEquals(3.14, data.getDataMap().get("double"));
    }

    public void test_register_entity() {
        RenderData data = new RenderData();
        BsUser entity = new BsUser();
        entity.setName("test name");

        RenderDataUtil.register(data, "entity", entity);

        Object result = data.getDataMap().get("entity");
        assertNotNull(result);
        assertTrue(result instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) result;
        assertEquals("test name", resultMap.get("name"));
    }

    public void test_register_entityList() {
        RenderData data = new RenderData();

        BsUser entity1 = new BsUser();
        entity1.setName("name1");

        BsUser entity2 = new BsUser();
        entity2.setName("name2");

        List<BsUser> entityList = Arrays.asList(entity1, entity2);

        RenderDataUtil.register(data, "entities", entityList);

        Object result = data.getDataMap().get("entities");
        assertNotNull(result);
        assertTrue(result instanceof List);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) result;
        assertEquals(2, resultList.size());

        Map<String, Object> map1 = resultList.get(0);
        assertEquals("name1", map1.get("name"));

        Map<String, Object> map2 = resultList.get(1);
        assertEquals("name2", map2.get("name"));
    }

    public void test_register_entitySet() {
        RenderData data = new RenderData();

        BsUser entity1 = new BsUser();
        entity1.setName("name1");

        BsUser entity2 = new BsUser();
        entity2.setName("name2");

        Set<BsUser> entitySet = new HashSet<>();
        entitySet.add(entity1);
        entitySet.add(entity2);

        RenderDataUtil.register(data, "entities", entitySet);

        Object result = data.getDataMap().get("entities");
        assertNotNull(result);
        assertTrue(result instanceof List);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) result;
        assertEquals(2, resultList.size());
    }

    public void test_register_emptyEntityList() {
        RenderData data = new RenderData();
        List<BsUser> emptyList = new ArrayList<>();

        RenderDataUtil.register(data, "empty", emptyList);

        Object result = data.getDataMap().get("empty");
        assertEquals(emptyList, result); // Empty collections are registered as-is
    }

    public void test_register_nonEntityList() {
        RenderData data = new RenderData();
        List<String> stringList = Arrays.asList("item1", "item2", "item3");

        RenderDataUtil.register(data, "strings", stringList);

        Object result = data.getDataMap().get("strings");
        assertEquals(stringList, result); // Non-entity collections are registered as-is
    }

    public void test_register_nonEntitySet() {
        RenderData data = new RenderData();
        Set<Integer> intSet = new HashSet<>(Arrays.asList(1, 2, 3));

        RenderDataUtil.register(data, "numbers", intSet);

        Object result = data.getDataMap().get("numbers");
        assertEquals(intSet, result); // Non-entity collections are registered as-is
    }

    public void test_register_mixedList() {
        RenderData data = new RenderData();

        BsUser entity = new BsUser();
        entity.setName("entity");

        // List with first element being Entity
        List<Object> mixedList = new ArrayList<>();
        mixedList.add(entity);
        mixedList.add("string");
        mixedList.add(123);

        RenderDataUtil.register(data, "mixed", mixedList);

        Object result = data.getDataMap().get("mixed");
        assertNotNull(result);
        assertTrue(result instanceof List);
        @SuppressWarnings("unchecked")
        List<Object> resultList = (List<Object>) result;
        assertEquals(3, resultList.size());

        // First element should be converted to Map since it's an Entity
        assertTrue(resultList.get(0) instanceof Map);
        // All elements are converted by BeanUtil.copyBeanToNewMap when first element is Entity
        assertTrue(resultList.get(1) instanceof Map);
        assertTrue(resultList.get(2) instanceof Map);
    }

    public void test_register_listWithNonEntityFirst() {
        RenderData data = new RenderData();

        BsUser entity = new BsUser();
        entity.setName("entity");

        // List with first element NOT being Entity
        List<Object> mixedList = new ArrayList<>();
        mixedList.add("string first");
        mixedList.add(entity);

        RenderDataUtil.register(data, "mixed", mixedList);

        Object result = data.getDataMap().get("mixed");
        assertEquals(mixedList, result); // Should be registered as-is since first element is not Entity
    }

    public void test_register_complexObject() {
        RenderData data = new RenderData();

        ComplexObject obj = new ComplexObject();
        obj.setValue("complex value");

        RenderDataUtil.register(data, "complex", obj);

        Object result = data.getDataMap().get("complex");
        assertEquals(obj, result); // Non-entity objects are registered as-is
    }

    public void test_register_arrayList() {
        RenderData data = new RenderData();

        BsUser entity1 = new BsUser();
        entity1.setName("user1");

        // Test ArrayList specifically (not just List interface)
        ArrayList<BsUser> arrayList = new ArrayList<>();
        arrayList.add(entity1);

        RenderDataUtil.register(data, "arrayList", arrayList);

        Object result = data.getDataMap().get("arrayList");
        assertNotNull(result);
        assertTrue(result instanceof List);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) result;
        assertEquals(1, resultList.size());
        assertEquals("user1", resultList.get(0).get("name"));
    }

    public void test_register_collectionPerformanceOptimization() {
        RenderData data = new RenderData();

        BsUser entity1 = new BsUser();
        entity1.setName("user1");
        BsUser entity2 = new BsUser();
        entity2.setName("user2");

        // Test List interface (should use get(0) for performance)
        List<BsUser> list = Arrays.asList(entity1, entity2);
        RenderDataUtil.register(data, "list", list);

        Object result = data.getDataMap().get("list");
        assertNotNull(result);
        assertTrue(result instanceof List);

        // Test Set interface (should use iterator().next())
        Set<BsUser> set = new HashSet<>();
        set.add(entity1);
        set.add(entity2);
        RenderDataUtil.register(data, "set", set);

        Object setResult = data.getDataMap().get("set");
        assertNotNull(setResult);
        assertTrue(setResult instanceof List);
    }

    // Test helper class
    public static class ComplexObject {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}