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

        assertEquals("http://example.com/", crawlingInfoHelper.generateId(dataMap));

        final List<String> browserTypeList = new ArrayList<String>();
        dataMap.put("type", browserTypeList);
        final List<String> roleTypeList = new ArrayList<String>();
        dataMap.put("role", roleTypeList);

        assertEquals("http://example.com/", crawlingInfoHelper.generateId(dataMap));
    }

    public void test_generateId_roleType() {
        final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("url", "http://example.com/");
        final List<String> roleTypeList = new ArrayList<String>();
        roleTypeList.add("admin");
        dataMap.put("role", roleTypeList);

        assertEquals("http://example.com/;role=admin", crawlingInfoHelper.generateId(dataMap));

        roleTypeList.add("guest");

        assertEquals("http://example.com/;role=admin,guest", crawlingInfoHelper.generateId(dataMap));

        final List<String> browserTypeList = new ArrayList<String>();
        dataMap.put("type", browserTypeList);

        assertEquals("http://example.com/;role=admin,guest", crawlingInfoHelper.generateId(dataMap));
    }
}
