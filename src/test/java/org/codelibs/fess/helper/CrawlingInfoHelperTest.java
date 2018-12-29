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
                "6b02bcff48a4e3935efece48eaed0ec4048ab8ccad720ee44bb7f0b66bc68acec40c1dce53737a3f3d800d4316f2ba3d3449b55cb97f090f034ace4ba8a39ef9",
                crawlingInfoHelper.generateId(dataMap));

        roleTypeList.add("guest");

        assertEquals(
                "cce703dfb6988fe8e3be6439673d8154e67811f1653551026f72ac41b9acb6e37510e2a557742e058a18cb1745d11badf651f8d2c568a8c67e20a9d5392618e8",
                crawlingInfoHelper.generateId(dataMap));

        final List<String> browserTypeList = new ArrayList<String>();
        dataMap.put("type", browserTypeList);

        assertEquals(
                "cce703dfb6988fe8e3be6439673d8154e67811f1653551026f72ac41b9acb6e37510e2a557742e058a18cb1745d11badf651f8d2c568a8c67e20a9d5392618e8",
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
