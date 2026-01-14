/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
import java.util.List;

import org.codelibs.fess.opensearch.config.exentity.DuplicateHost;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class DuplicateHostHelperTest extends UnitFessTestCase {
    private DuplicateHostHelper duplicateHostHelper;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        duplicateHostHelper = new DuplicateHostHelper();
        duplicateHostHelper.duplicateHostList = new ArrayList<DuplicateHost>();

        DuplicateHost foo = new DuplicateHost();
        foo.setRegularName("www.foo.com");
        foo.setDuplicateHostName("foo.com");
        duplicateHostHelper.duplicateHostList.add(foo);
        DuplicateHost bar = new DuplicateHost();
        bar.setRegularName("www.bar.com");
        bar.setDuplicateHostName("mail.bar.com");
        duplicateHostHelper.duplicateHostList.add(bar);
        DuplicateHost hoge = new DuplicateHost();
        hoge.setRegularName("www.foo.com");
        hoge.setDuplicateHostName("www.foo.com:99");
        duplicateHostHelper.duplicateHostList.add(hoge);
    }

    @Test
    public void test_convert() {
        String url;
        String result;

        url = "http://foo.com";
        result = "http://www.foo.com";
        assertEquals(result, duplicateHostHelper.convert(url));

        url = "http://foo.com/";
        result = "http://www.foo.com/";
        assertEquals(result, duplicateHostHelper.convert(url));

        url = "http://foo.com:8080/";
        result = "http://www.foo.com:8080/";
        assertEquals(result, duplicateHostHelper.convert(url));

        url = "http://mail.bar.com/";
        result = "http://www.bar.com/";
        assertEquals(result, duplicateHostHelper.convert(url));

        url = "http://www.foo.com:99/";
        result = "http://www.foo.com/";
        assertEquals(result, duplicateHostHelper.convert(url));

    }

    @Test
    public void test_convert_skip() {
        String url;
        String result;

        url = "http://www.foo.com";
        result = "http://www.foo.com";
        assertEquals(result, duplicateHostHelper.convert(url));

        url = "http://www.foo.com/";
        result = "http://www.foo.com/";
        assertEquals(result, duplicateHostHelper.convert(url));

        url = "http://www.foo.com:8080/";
        result = "http://www.foo.com:8080/";
        assertEquals(result, duplicateHostHelper.convert(url));

        url = "http://www.bar.com/";
        result = "http://www.bar.com/";
        assertEquals(result, duplicateHostHelper.convert(url));

        url = "http://www.bar.com:8080/";
        result = "http://www.bar.com:8080/";
        assertEquals(result, duplicateHostHelper.convert(url));
    }

    @Test
    public void test_init() {
        DuplicateHostHelper helper = new DuplicateHostHelper();

        try {
            helper.init();
            assertNotNull(helper.duplicateHostList);
        } catch (Exception e) {
            fail("init() should not throw an exception: " + e.getMessage());
        }
    }

    @Test
    public void test_setDuplicateHostList() {
        DuplicateHostHelper helper = new DuplicateHostHelper();
        List<DuplicateHost> testList = new ArrayList<>();

        DuplicateHost testHost = new DuplicateHost();
        testHost.setRegularName("www.test.com");
        testHost.setDuplicateHostName("test.com");
        testList.add(testHost);

        helper.setDuplicateHostList(testList);

        assertEquals(testList, helper.duplicateHostList);
        assertEquals(1, helper.duplicateHostList.size());
        assertEquals("www.test.com", helper.duplicateHostList.get(0).getRegularName());
    }

    @Test
    public void test_add() {
        DuplicateHostHelper helper = new DuplicateHostHelper();

        DuplicateHost testHost = new DuplicateHost();
        testHost.setRegularName("www.test.com");
        testHost.setDuplicateHostName("test.com");

        helper.add(testHost);

        assertNotNull(helper.duplicateHostList);
        assertEquals(1, helper.duplicateHostList.size());
        assertEquals("www.test.com", helper.duplicateHostList.get(0).getRegularName());
    }

    @Test
    public void test_add_withNullList() {
        DuplicateHostHelper helper = new DuplicateHostHelper();
        helper.duplicateHostList = null;

        DuplicateHost testHost = new DuplicateHost();
        testHost.setRegularName("www.test.com");
        testHost.setDuplicateHostName("test.com");

        helper.add(testHost);

        assertNotNull(helper.duplicateHostList);
        assertEquals(1, helper.duplicateHostList.size());
        assertEquals("www.test.com", helper.duplicateHostList.get(0).getRegularName());
    }

    @Test
    public void test_convert_nullUrl() {
        assertNull(duplicateHostHelper.convert(null));
    }

    @Test
    public void test_convert_withNullList() {
        DuplicateHostHelper helper = new DuplicateHostHelper();
        helper.duplicateHostList = null;

        String url = "http://test.com";
        String result = helper.convert(url);

        assertNotNull(result);
        assertNotNull(helper.duplicateHostList);
    }

    @Test
    public void test_convert_emptyList() {
        DuplicateHostHelper helper = new DuplicateHostHelper();
        helper.duplicateHostList = new ArrayList<>();

        String url = "http://test.com";
        String result = helper.convert(url);

        assertEquals(url, result);
    }

    @Test
    public void test_convert_multipleTransformations() {
        DuplicateHostHelper helper = new DuplicateHostHelper();
        helper.duplicateHostList = new ArrayList<>();

        DuplicateHost host1 = new DuplicateHost();
        host1.setRegularName("www.example.com");
        host1.setDuplicateHostName("example.com");
        helper.duplicateHostList.add(host1);

        DuplicateHost host2 = new DuplicateHost();
        host2.setRegularName("secure.example.com");
        host2.setDuplicateHostName("www.example.com");
        helper.duplicateHostList.add(host2);

        String url = "http://example.com/test";
        String result = helper.convert(url);

        assertEquals("http://secure.example.com/test", result);
    }

}
