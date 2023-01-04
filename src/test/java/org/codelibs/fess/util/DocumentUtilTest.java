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
}
