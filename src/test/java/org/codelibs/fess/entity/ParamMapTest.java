/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import org.codelibs.fess.entity.ParamMap;
import org.codelibs.fess.unit.UnitFessTestCase;

public class ParamMapTest extends UnitFessTestCase {

    private ParamMap<Object, Object> createSnakeMap() {
        final Map<Object, Object> map = new HashMap<>();
        map.put("aaa", "111");
        map.put("aaa_bbb", "222");
        map.put("aaa_bbb_ccc", "333");
        map.put("ccc.ddd", "444");
        return new ParamMap<>(map);
    }

    private ParamMap<Object, Object> createCamelMap() {
        final Map<Object, Object> map = new HashMap<>();
        map.put("aaa", "111");
        map.put("aaaBbb", "222");
        map.put("aaaBbbCcc", "333");
        map.put("ccc.ddd", "444");
        return new ParamMap<>(map);
    }

    public void test_snake_get() {
        Map<Object, Object> paramMap = createSnakeMap();

        assertNull(paramMap.get("xxx"));
        assertEquals("111", paramMap.get("aaa"));
        assertEquals("222", paramMap.get("aaa_bbb"));
        assertEquals("222", paramMap.get("aaaBbb"));
        assertEquals("222", paramMap.get("AaaBbb"));
        assertNull(paramMap.get("aaabbb"));
        assertEquals("333", paramMap.get("aaa_bbb_ccc"));
        assertEquals("333", paramMap.get("aaaBbbCcc"));
        assertEquals("333", paramMap.get("AaaBbbCcc"));
        assertNull(paramMap.get("aaabbbcc"));
        assertEquals("444", paramMap.get("ccc.ddd"));
        assertNull(paramMap.get("cccDdd"));
        assertNull(paramMap.get("DccDdd"));
    }

    public void test_snake_containsKey() {
        Map<Object, Object> paramMap = createSnakeMap();

        assertFalse(paramMap.containsKey("xxx"));
        assertTrue(paramMap.containsKey("aaa"));
        assertTrue(paramMap.containsKey("aaa_bbb"));
        assertTrue(paramMap.containsKey("aaaBbb"));
        assertTrue(paramMap.containsKey("AaaBbb"));
        assertFalse(paramMap.containsKey("aaabbb"));
        assertTrue(paramMap.containsKey("aaa_bbb_ccc"));
        assertTrue(paramMap.containsKey("aaaBbbCcc"));
        assertTrue(paramMap.containsKey("AaaBbbCcc"));
        assertFalse(paramMap.containsKey("aaabbbcc"));
        assertTrue(paramMap.containsKey("ccc.ddd"));
        assertFalse(paramMap.containsKey("cccDdd"));
        assertFalse(paramMap.containsKey("DccDdd"));
    }

    public void test_camel_get() {
        Map<Object, Object> paramMap = createCamelMap();

        assertNull(paramMap.get("xxx"));
        assertEquals("111", paramMap.get("aaa"));
        assertEquals("222", paramMap.get("aaa_bbb"));
        assertEquals("222", paramMap.get("aaaBbb"));
        assertNull(paramMap.get("AaaBbb")); // null
        assertNull(paramMap.get("aaabbb"));
        assertEquals("333", paramMap.get("aaa_bbb_ccc"));
        assertEquals("333", paramMap.get("aaaBbbCcc"));
        assertNull(paramMap.get("AaaBbbCcc")); // null
        assertNull(paramMap.get("aaabbbcc"));
        assertEquals("444", paramMap.get("ccc.ddd"));
        assertNull(paramMap.get("cccDdd"));
        assertNull(paramMap.get("DccDdd"));
    }

    public void test_camel_containsKey() {
        Map<Object, Object> paramMap = createCamelMap();

        assertFalse(paramMap.containsKey("xxx"));
        assertTrue(paramMap.containsKey("aaa"));
        assertTrue(paramMap.containsKey("aaa_bbb"));
        assertTrue(paramMap.containsKey("aaaBbb"));
        assertFalse(paramMap.containsKey("AaaBbb")); // false
        assertFalse(paramMap.containsKey("aaabbb"));
        assertTrue(paramMap.containsKey("aaa_bbb_ccc"));
        assertTrue(paramMap.containsKey("aaaBbbCcc"));
        assertFalse(paramMap.containsKey("AaaBbbCcc")); // false
        assertFalse(paramMap.containsKey("aaabbbcc"));
        assertTrue(paramMap.containsKey("ccc.ddd"));
        assertFalse(paramMap.containsKey("cccDdd"));
        assertFalse(paramMap.containsKey("DccDdd"));
    }
}
