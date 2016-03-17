/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.codelibs.fess.unit.UnitFessTestCase;

public class StreamUtilTest extends UnitFessTestCase {

    public void test_ofValues() {
        String[] values = { "value1", "value2" };
        Stream<String> stream = StreamUtil.of(values[0], values[1]);
        Object[] array = stream.toArray();
        for (int i = 0; i < 2; i++) {
            assertEquals(values[i], array[i]);
        }
    }

    public void test_ofNull() {
        assertEquals(0, StreamUtil.of().toArray().length);
        Object[] o = {};
        assertEquals(0, StreamUtil.of(o).toArray().length);
        Map<Object, Object> map = new HashMap<Object, Object>();
        assertEquals(0, StreamUtil.of(map).toArray().length);
    }

    public void test_ofMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        Stream<Map.Entry<String, String>> stream = StreamUtil.of(map);
        stream.forEach(m -> assertEquals(map.get(m.getKey()), m.getValue()));
    }

}
