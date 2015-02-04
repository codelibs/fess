/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package jp.sf.fess.validator;

import org.seasar.extension.unit.S2TestCase;

public class UriTypeChecksTest extends S2TestCase {
    public void test_check_ok() {
        String protocols;
        String values;

        protocols = "http:";
        values = "http://www.hoge.com/";
        assertTrue(UriTypeChecks.check(protocols, values));

        protocols = "http:";
        values = "http://www.hoge.com/\nhttp://www.fuga.com/";
        assertTrue(UriTypeChecks.check(protocols, values));

        protocols = "http:";
        values = "http://www.hoge.com/ \r\nhttp://www.fuga.com/";
        assertTrue(UriTypeChecks.check(protocols, values));

        protocols = "http:";
        values = "http://www.hoge.com/\nhttp://www.fuga.com/\n http://www.bar.com/";
        assertTrue(UriTypeChecks.check(protocols, values));

        protocols = "http:,https:";
        values = "https://www.hoge.com/";
        assertTrue(UriTypeChecks.check(protocols, values));

        protocols = "http:,https:";
        values = "http://www.hoge.com/\r\nhttp://www.fuga.com/";
        assertTrue(UriTypeChecks.check(protocols, values));

        protocols = "http:,https:";
        values = "http://www.hoge.com/\nhttps://www.fuga.com/";
        assertTrue(UriTypeChecks.check(protocols, values));

        protocols = "http:,https:";
        values = "http://www.hoge.com/\n \nhttps://www.fuga.com/";
        assertTrue(UriTypeChecks.check(protocols, values));

        protocols = "http:, https:";
        values = "http://www.hoge.com/\nhttps://www.fuga.com/\n http://www.bar.com/";
        assertTrue(UriTypeChecks.check(protocols, values));
    }

    public void test_check_ng() {
        String protocols;
        String values;

        protocols = "http:";
        values = "https://www.hoge.com/";
        assertFalse(UriTypeChecks.check(protocols, values));

        protocols = "http:";
        values = "https://www.hoge.com/\nhttps://www.fuga.com/";
        assertFalse(UriTypeChecks.check(protocols, values));

        protocols = "http:";
        values = "https://www.hoge.com/\n \nhttps://www.fuga.com/";
        assertFalse(UriTypeChecks.check(protocols, values));

        protocols = "http:";
        values = "https://www.hoge.com/\nhttps://www.fuga.com/\n https://www.bar.com/";
        assertFalse(UriTypeChecks.check(protocols, values));

    }
}
