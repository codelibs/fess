/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.helper;

import org.seasar.extension.unit.S2TestCase;

public class OverlappingHostHelperTest extends S2TestCase {
    public OverlappingHostHelper overlappingHostHelper;

    @Override
    protected String getRootDicon() throws Throwable {
        return "jp/sf/fess/helper/overlappingHost.dicon";
    }

    public void test_convert() {
        String url;
        String result;

        url = "http://hoge.com";
        result = "http://www.hoge.com";
        assertEquals(result, overlappingHostHelper.convert(url));

        url = "http://hoge.com/";
        result = "http://www.hoge.com/";
        assertEquals(result, overlappingHostHelper.convert(url));

        url = "http://hoge.com:8080/";
        result = "http://www.hoge.com:8080/";
        assertEquals(result, overlappingHostHelper.convert(url));

        url = "http://mail.fuga.com/";
        result = "http://www.fuga.com/";
        assertEquals(result, overlappingHostHelper.convert(url));

    }

    public void test_convert_skip() {
        String url;
        String result;

        url = "http://www.hoge.com";
        result = "http://www.hoge.com";
        assertEquals(result, overlappingHostHelper.convert(url));

        url = "http://www.hoge.com/";
        result = "http://www.hoge.com/";
        assertEquals(result, overlappingHostHelper.convert(url));

        url = "http://www.hoge.com:8080/";
        result = "http://www.hoge.com:8080/";
        assertEquals(result, overlappingHostHelper.convert(url));

        url = "http://www.fuga.com/";
        result = "http://www.fuga.com/";
        assertEquals(result, overlappingHostHelper.convert(url));

        url = "http://www.fuga.com:8080/";
        result = "http://www.fuga.com:8080/";
        assertEquals(result, overlappingHostHelper.convert(url));

    }
}
