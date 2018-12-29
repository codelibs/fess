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

        assertEquals(
                "c1f123e7545bf787a40d75141d965910d75df78f2fa170b26ebdbf2285e39ce77de503e4ef769fcfe3d6dbec1cc488818c66416ed647472bd252b564a38c6aef",
                crawlingInfoHelper.generateId(dataMap));

        final List<String> browserTypeList = new ArrayList<String>();
        dataMap.put("type", browserTypeList);
        final List<String> roleTypeList = new ArrayList<String>();
        dataMap.put("role", roleTypeList);

        assertEquals(
                "c1f123e7545bf787a40d75141d965910d75df78f2fa170b26ebdbf2285e39ce77de503e4ef769fcfe3d6dbec1cc488818c66416ed647472bd252b564a38c6aef",
                crawlingInfoHelper.generateId(dataMap));
    }

    public void test_generateId_roleType() {
        final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("url", "http://example.com/");
        final List<String> roleTypeList = new ArrayList<String>();
        roleTypeList.add("admin");
        dataMap.put("role", roleTypeList);

        assertEquals(
                "4d9303e238ab15c4fde19b1e5ae09b272f418bd0a33694f8c57eaff1e8a999e2123751040e023b1cb085cc5d2bfe8311113e48e84c41bbf01b009011640269be",
                crawlingInfoHelper.generateId(dataMap));

        roleTypeList.add("guest");

        assertEquals(
                "ad6cdfb1257db74e8e12df16cf8672ca11e8ac8a2b172a529dd2470252b43dbf136f5a55658c141b8bd5eb47586c9affa30a7ff3db104dace5fe12f03e1d35c7",
                crawlingInfoHelper.generateId(dataMap));

        final List<String> browserTypeList = new ArrayList<String>();
        dataMap.put("type", browserTypeList);

        assertEquals(
                "ad6cdfb1257db74e8e12df16cf8672ca11e8ac8a2b172a529dd2470252b43dbf136f5a55658c141b8bd5eb47586c9affa30a7ff3db104dace5fe12f03e1d35c7",
                crawlingInfoHelper.generateId(dataMap));
    }

    public void test_generateId_virtualHost() {
        final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("url", "http://example.com/");
        final List<String> roleTypeList = new ArrayList<String>();
        roleTypeList.add("admin");
        dataMap.put("role", roleTypeList);
        final List<String> virtualHostList = new ArrayList<String>();
        virtualHostList.add("site1");
        dataMap.put("virtual_host", virtualHostList);

        assertEquals(
                "6823bba6ee0f41db9f69063af4d0e074b9515b32a493d248e8dce830926e11344eb79f841deac0758d46a071e453246c1a758508f1e5e5a3bcea2e47d1979f02",
                crawlingInfoHelper.generateId(dataMap));

        virtualHostList.add("site2");

        assertEquals(
                "18189fc1f83634e5201affc8c4f1971cfa1f1ea22c1c9d7eb239c31badcdb679fd15348eb6538c9b344e4a4c299f2eb5779591f44d4e29e17f9ef40fdbe1749b",
                crawlingInfoHelper.generateId(dataMap));

        final List<String> browserTypeList = new ArrayList<String>();
        dataMap.put("type", browserTypeList);

        assertEquals(
                "18189fc1f83634e5201affc8c4f1971cfa1f1ea22c1c9d7eb239c31badcdb679fd15348eb6538c9b344e4a4c299f2eb5779591f44d4e29e17f9ef40fdbe1749b",
                crawlingInfoHelper.generateId(dataMap));
    }

    public void test_generateId_long() {
        for (int i = 0; i < 1000; i++) {
            final String value = RandomStringUtils.randomAlphabetic(550);
            assertEquals(128, crawlingInfoHelper.generateId(value.substring(0, 440)).length());
            assertEquals(128, crawlingInfoHelper.generateId(value.substring(0, 450)).length());
            assertEquals(128, crawlingInfoHelper.generateId(value.substring(0, 460)).length());
            assertEquals(128, crawlingInfoHelper.generateId(value.substring(0, 470)).length());
            assertEquals(128, crawlingInfoHelper.generateId(value.substring(0, 480)).length());
            assertEquals(128, crawlingInfoHelper.generateId(value.substring(0, 490)).length());
            assertEquals(128, crawlingInfoHelper.generateId(value.substring(0, 500)).length());
            assertEquals(128, crawlingInfoHelper.generateId(value.substring(0, 510)).length());
            assertEquals(128, crawlingInfoHelper.generateId(value.substring(0, 520)).length());
            assertEquals(128, crawlingInfoHelper.generateId(value.toString()).length());
        }
        for (int i = 0; i < 1000; i++) {
            final String value = RandomStringUtils.randomAscii(550);
            assertEquals(128, crawlingInfoHelper.generateId(value.substring(0, 450)).length());
            assertEquals(128, crawlingInfoHelper.generateId(value.substring(0, 460)).length());
            assertEquals(128, crawlingInfoHelper.generateId(value.substring(0, 470)).length());
            assertEquals(128, crawlingInfoHelper.generateId(value.substring(0, 480)).length());
            assertEquals(128, crawlingInfoHelper.generateId(value.substring(0, 490)).length());
            assertEquals(128, crawlingInfoHelper.generateId(value.substring(0, 500)).length());
            assertEquals(128, crawlingInfoHelper.generateId(value.substring(0, 510)).length());
            assertEquals(128, crawlingInfoHelper.generateId(value.substring(0, 520)).length());
            assertEquals(128, crawlingInfoHelper.generateId(value.toString()).length());
        }

        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < 550; i++) {
            buf.append('a');
        }
        assertEquals(
                "c11e9ec4fa5ccd0c37c78e7e43555c7061a4dfe8d250a66cad3abaf190e4519cae5b0dce520fb94667cb37e5bbd29b226638cc6172169706a8ae36b000a6900c",
                crawlingInfoHelper.generateId(buf.substring(0, 500)));
        assertEquals(
                "9f11520b58e7d703c02e7987ed2f44c89b4d5f6aea56c034208837d701824dcd0e1ddbb757e6f86dab0cdbbf3871989dfd4a86c153089a7e872ad081b6862a2e",
                crawlingInfoHelper.generateId(buf.substring(0, 510)));
        assertEquals(
                "6be41c30a9e5e7724f87134cecc6e5aa6fe771773b8845867ce41b1a6d46d50b25f504259c8e9a6657f6ada8865d06ab3bcee5abd306cdd1f43d60c451b34eb6",
                crawlingInfoHelper.generateId(buf.substring(0, 520)));
    }
}
