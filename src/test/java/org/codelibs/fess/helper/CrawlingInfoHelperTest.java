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
package org.codelibs.fess.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
                "6b2d3770573e53f9f2d743e0598fad397c34968566001329c436f041871fd8af950b32ce77da6cc4a5561a6ccf4d2d7741269209ac254c234a972029ec92110e",
                crawlingInfoHelper.generateId(dataMap));

        final List<String> browserTypeList = new ArrayList<String>();
        dataMap.put("type", browserTypeList);
        final List<String> roleTypeList = new ArrayList<String>();
        dataMap.put("role", roleTypeList);

        assertEquals(
                "6b2d3770573e53f9f2d743e0598fad397c34968566001329c436f041871fd8af950b32ce77da6cc4a5561a6ccf4d2d7741269209ac254c234a972029ec92110e",
                crawlingInfoHelper.generateId(dataMap));
    }

    public void test_generateId_roleType() {
        final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("url", "http://example.com/");
        final List<String> roleTypeList = new ArrayList<String>();
        roleTypeList.add("admin");
        dataMap.put("role", roleTypeList);

        assertEquals(
                "e6cc9514d288fff4553187be8800b54efe3229054b1acbf64d3132e1982162576113939c790e8d6f56e2fa0d6655ba520c44ad0b71adc4061cf6b42e975b4904",
                crawlingInfoHelper.generateId(dataMap));

        roleTypeList.add("guest");

        assertEquals(
                "dce588fe68813d59cce9a5b087f81d63406123492cb120027ca9ed765ba6501f7b07bb36569afd2fb7b10ac53c4a9a3764b0d7619a8059c071b1a20f8ae42fa3",
                crawlingInfoHelper.generateId(dataMap));

        final List<String> browserTypeList = new ArrayList<String>();
        dataMap.put("type", browserTypeList);

        assertEquals(
                "dce588fe68813d59cce9a5b087f81d63406123492cb120027ca9ed765ba6501f7b07bb36569afd2fb7b10ac53c4a9a3764b0d7619a8059c071b1a20f8ae42fa3",
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
                "87e7e4a2d5e2e24147ffd8820b70877a4cee15abdd48f7702e8233f0d17d7369f6d861124f106b541e6652a3e7a94cd51a332a2500fd065de6920559458cd3de",
                crawlingInfoHelper.generateId(dataMap));

        virtualHostList.add("site2");

        assertEquals(
                "a0211e285c585e981cb36a57dc03c88c69f3a4880c64fb30619d747c188c977b45f3f19b35e6f62d1b7c91eb06d0e64717e3e1195c2d511f1bb2b95bb01a67af",
                crawlingInfoHelper.generateId(dataMap));

        final List<String> browserTypeList = new ArrayList<String>();
        dataMap.put("type", browserTypeList);

        assertEquals(
                "a0211e285c585e981cb36a57dc03c88c69f3a4880c64fb30619d747c188c977b45f3f19b35e6f62d1b7c91eb06d0e64717e3e1195c2d511f1bb2b95bb01a67af",
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

    public void test_generateId_multithread() throws Exception {
        final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("url", "http://example.com/");
        final List<String> list = new ArrayList<>();
        for (int i = 100; i > 0; i--) {
            list.add(String.valueOf(i));
        }
        dataMap.put("role", list);
        dataMap.put("virtual_host", list);
        final String result =
                "f8240bbae62b99960056c3a382844836c547c2ec73e019491bb7bbb02d92d98e876c8204b67a59ca8123b82d20986516b7d451f68dd634b39004c0d36c0eeca4";
        assertEquals(result, crawlingInfoHelper.generateId(dataMap));

        final AtomicInteger counter = new AtomicInteger(0);
        final ForkJoinPool pool = new ForkJoinPool(10);
        for (int i = 0; i < 1000; i++) {
            pool.execute(() -> {
                assertEquals(result, crawlingInfoHelper.generateId(dataMap));
                counter.incrementAndGet();
            });
        }
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
        assertEquals(1000, counter.get());
    }
}
