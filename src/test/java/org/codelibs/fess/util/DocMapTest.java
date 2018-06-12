/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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

import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.Iterator;

import org.codelibs.fess.unit.UnitFessTestCase;

public class DocMapTest extends UnitFessTestCase {
    public void test_DocList() {
        Map<String, Object> value = new LinkedHashMap<>();
        DocMap docMap = new DocMap(value);
        assertTrue(docMap.isEmpty());

        value.clear();
        List<String> keys = Arrays.asList("test_2", "test_0", "lang", "test_1");

        value.put(keys[0], true);
        value.put(keys[1], 1000);
        value.put(keys[2], "ja");
        value.put(keys[3], "str");
        docMap = new DocMap(value);

        Set<Map.Entry<String, Object>> actual = docMap.entrySet();
        Iterator<Map.Entry<String, Object>> iterator = actual.iterator();

        int i = 0;
        for (Map.Entry<String, Object> entry : actual) {
            i++;
        }
        docMap.clear();
    }
}