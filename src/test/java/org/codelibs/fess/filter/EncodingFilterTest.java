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
package org.codelibs.fess.filter;

import java.io.IOException;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;

public class EncodingFilterTest extends UnitFessTestCase {
    public void test_normal() throws IOException {
        final EncodingFilter filter = new EncodingFilter();
        Map<String, String[]> paramMap;

        paramMap = filter.parseQueryString("a=1", "UTF-8");
        assertEquals(1, paramMap.size());
        assertEquals("1", paramMap.get("a")[0]);

        paramMap = filter.parseQueryString("a=1&b=2", "UTF-8");
        assertEquals(2, paramMap.size());
        assertEquals("1", paramMap.get("a")[0]);
        assertEquals("2", paramMap.get("b")[0]);

        paramMap = filter.parseQueryString("a=111&b=222&c=333", "UTF-8");
        assertEquals(3, paramMap.size());
        assertEquals("111", paramMap.get("a")[0]);
        assertEquals("222", paramMap.get("b")[0]);
        assertEquals("333", paramMap.get("c")[0]);

        paramMap = filter.parseQueryString("a=1&b=2&c=3&a=2", "UTF-8");
        assertEquals(3, paramMap.size());
        assertEquals("1", paramMap.get("a")[0]);
        assertEquals("2", paramMap.get("a")[1]);
        assertEquals("2", paramMap.get("b")[0]);
        assertEquals("3", paramMap.get("c")[0]);

    }

    public void test_missing() throws IOException {
        final EncodingFilter filter = new EncodingFilter();
        Map<String, String[]> paramMap;

        paramMap = filter.parseQueryString("a=", "UTF-8");
        assertEquals(1, paramMap.size());
        assertEquals("", paramMap.get("a")[0]);

        paramMap = filter.parseQueryString("a", "UTF-8");
        assertEquals(1, paramMap.size());
        assertEquals("", paramMap.get("a")[0]);

        paramMap = filter.parseQueryString("=1", "UTF-8");
        assertEquals(1, paramMap.size());
        assertEquals("1", paramMap.get("")[0]);

        paramMap = filter.parseQueryString("=", "UTF-8");
        assertEquals(1, paramMap.size());
        assertEquals("", paramMap.get("")[0]);

    }

    public void test_decode() throws IOException {
        final EncodingFilter filter = new EncodingFilter();
        Map<String, String[]> paramMap;

        paramMap = filter.parseQueryString("a=%E3%83%86%E3%82%B9%E3%83%88", "UTF-8");
        assertEquals(1, paramMap.size());
        assertEquals("テスト", paramMap.get("a")[0]);

        paramMap = filter.parseQueryString("a=%A5%C6%A5%B9%A5%C8", "EUC-JP");
        assertEquals(1, paramMap.size());
        assertEquals("テスト", paramMap.get("a")[0]);

        paramMap = filter.parseQueryString("a=%83e%83X%83g", "Shift_JIS");
        assertEquals(1, paramMap.size());
        assertEquals("テスト", paramMap.get("a")[0]);

    }

}
