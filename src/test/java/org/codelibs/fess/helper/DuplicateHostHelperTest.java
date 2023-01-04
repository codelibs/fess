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

import org.codelibs.fess.es.config.exentity.DuplicateHost;
import org.codelibs.fess.unit.UnitFessTestCase;

public class DuplicateHostHelperTest extends UnitFessTestCase {
    private DuplicateHostHelper duplicateHostHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
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

}
