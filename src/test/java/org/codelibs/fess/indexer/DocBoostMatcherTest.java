/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
package org.codelibs.fess.indexer;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;

public class DocBoostMatcherTest extends UnitFessTestCase {
    public void test_integer() {
        final DocBoostMatcher docBoostMatcher = new DocBoostMatcher();
        docBoostMatcher.setBoostExpression("10");
        docBoostMatcher.setMatchExpression("data1 > 10");

        final Map<String, Object> map = new HashMap<String, Object>();

        assertTrue(0.0f == docBoostMatcher.getValue(map));

        map.put("data1", 20);
        assertTrue(docBoostMatcher.match(map));

        map.put("data1", 5);
        assertFalse(docBoostMatcher.match(map));

        map.remove("data1");
        assertFalse(docBoostMatcher.match(map));

        map.put("data2", 5);
        assertFalse(docBoostMatcher.match(map));
    }

    public void test_string() {
        final DocBoostMatcher docBoostMatcher = new DocBoostMatcher();
        docBoostMatcher.setBoostExpression("10");
        docBoostMatcher.setMatchExpression("data1 != null && data1.matches(\"test\")");

        final Map<String, Object> map = new HashMap<String, Object>();

        map.put("data1", "test");
        assertTrue(docBoostMatcher.match(map));

        map.put("data1", "aaa test bbb");
        assertFalse(docBoostMatcher.match(map));

        map.put("data1", "hoge");
        assertFalse(docBoostMatcher.match(map));

        map.remove("data1");
        assertFalse(docBoostMatcher.match(map));

        map.put("data2", "hoge");
        assertFalse(docBoostMatcher.match(map));

        docBoostMatcher.setMatchExpression("data1.matches(\".*test.*\")");
        map.put("data1", "aaa test bbb");
        assertTrue(docBoostMatcher.match(map));
    }

    public void test_boost_params() {
        final DocBoostMatcher docBoostMatcher = new DocBoostMatcher();
        docBoostMatcher.setBoostExpression("10 * boost1 + boost2");
        docBoostMatcher.setMatchExpression("data1 > 10");

        final Map<String, Object> map = new HashMap<String, Object>();

        map.put("boost1", 0);
        map.put("boost2", 0);
        assertTrue(0.0f == docBoostMatcher.getValue(map));

        map.put("boost1", 1);
        map.put("boost2", 0);
        assertTrue(10.0f == docBoostMatcher.getValue(map));

        map.put("boost1", 1);
        map.put("boost2", 2);
        assertTrue(12.0f == docBoostMatcher.getValue(map));
    }
}
