/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
package org.codelibs.fess.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.codelibs.fess.unit.UnitFessTestCase;

public class CrawlingInfoHelperTest extends UnitFessTestCase {
    private CrawlingInfoHelper crawlingInfoHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        crawlingInfoHelper = new CrawlingInfoHelper();
    }

    public void test_generateId() {
        final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("url", "http://example.com/");

        assertEquals("http:%2F%2Fexample.com%2F", crawlingInfoHelper.generateId(dataMap));

        final List<String> browserTypeList = new ArrayList<String>();
        dataMap.put("type", browserTypeList);
        final List<String> roleTypeList = new ArrayList<String>();
        dataMap.put("role", roleTypeList);

        assertEquals("http:%2F%2Fexample.com%2F", crawlingInfoHelper.generateId(dataMap));
    }

    public void test_generateId_roleType() {
        final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("url", "http://example.com/");
        final List<String> roleTypeList = new ArrayList<String>();
        roleTypeList.add("admin");
        dataMap.put("role", roleTypeList);

        assertEquals("http:%2F%2Fexample.com%2F;role=admin", crawlingInfoHelper.generateId(dataMap));

        roleTypeList.add("guest");

        assertEquals("http:%2F%2Fexample.com%2F;role=admin,guest", crawlingInfoHelper.generateId(dataMap));

        final List<String> browserTypeList = new ArrayList<String>();
        dataMap.put("type", browserTypeList);

        assertEquals("http:%2F%2Fexample.com%2F;role=admin,guest", crawlingInfoHelper.generateId(dataMap));
    }

    public void test_generateId_long() {
        for (int i = 0; i < 1000; i++) {
            final String value = RandomStringUtils.randomAlphabetic(550);
            assertEquals(440, crawlingInfoHelper.generateId(value.substring(0, 440), null).length());
            assertEquals(509, crawlingInfoHelper.generateId(value.substring(0, 450), null).length());
            assertEquals(509, crawlingInfoHelper.generateId(value.substring(0, 460), null).length());
            assertEquals(509, crawlingInfoHelper.generateId(value.substring(0, 470), null).length());
            assertEquals(509, crawlingInfoHelper.generateId(value.substring(0, 480), null).length());
            assertEquals(509, crawlingInfoHelper.generateId(value.substring(0, 490), null).length());
            assertEquals(509, crawlingInfoHelper.generateId(value.substring(0, 500), null).length());
            assertEquals(509, crawlingInfoHelper.generateId(value.substring(0, 510), null).length());
            assertEquals(509, crawlingInfoHelper.generateId(value.substring(0, 520), null).length());
            assertEquals(509, crawlingInfoHelper.generateId(value.toString(), null).length());
        }
        for (int i = 0; i < 1000; i++) {
            final String value = RandomStringUtils.randomAscii(550);
            assertEquals(509, crawlingInfoHelper.generateId(value.substring(0, 450), null).length());
            assertEquals(509, crawlingInfoHelper.generateId(value.substring(0, 460), null).length());
            assertEquals(509, crawlingInfoHelper.generateId(value.substring(0, 470), null).length());
            assertEquals(509, crawlingInfoHelper.generateId(value.substring(0, 480), null).length());
            assertEquals(509, crawlingInfoHelper.generateId(value.substring(0, 490), null).length());
            assertEquals(509, crawlingInfoHelper.generateId(value.substring(0, 500), null).length());
            assertEquals(509, crawlingInfoHelper.generateId(value.substring(0, 510), null).length());
            assertEquals(509, crawlingInfoHelper.generateId(value.substring(0, 520), null).length());
            assertEquals(509, crawlingInfoHelper.generateId(value.toString(), null).length());
        }
    }
}
