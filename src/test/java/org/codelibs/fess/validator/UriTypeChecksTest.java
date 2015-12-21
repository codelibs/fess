/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.fess.validator;

import org.codelibs.fess.unit.UnitFessTestCase;

public class UriTypeChecksTest extends UnitFessTestCase {
    public void test_check_ok() {
        String protocols;
        String values;

        protocols = "http:";
        values = "http://www.foo.com/";
        assertTrue(UriTypeChecks.check(protocols, values));

        protocols = "http:";
        values = "http://www.foo.com/\nhttp://www.bar.com/";
        assertTrue(UriTypeChecks.check(protocols, values));

        protocols = "http:";
        values = "http://www.foo.com/ \r\nhttp://www.bar.com/";
        assertTrue(UriTypeChecks.check(protocols, values));

        protocols = "http:";
        values = "http://www.foo.com/\nhttp://www.bar.com/\n http://www.baz.com/";
        assertTrue(UriTypeChecks.check(protocols, values));

        protocols = "http:,https:";
        values = "https://www.foo.com/";
        assertTrue(UriTypeChecks.check(protocols, values));

        protocols = "http:,https:";
        values = "http://www.foo.com/\r\nhttp://www.bar.com/";
        assertTrue(UriTypeChecks.check(protocols, values));

        protocols = "http:,https:";
        values = "http://www.foo.com/\nhttps://www.bar.com/";
        assertTrue(UriTypeChecks.check(protocols, values));

        protocols = "http:,https:";
        values = "http://www.foo.com/\n \nhttps://www.bar.com/";
        assertTrue(UriTypeChecks.check(protocols, values));

        protocols = "http:, https:";
        values = "http://www.foo.com/\nhttps://www.bar.com/\n http://www.baz.com/";
        assertTrue(UriTypeChecks.check(protocols, values));
    }

    public void test_check_ng() {
        String protocols;
        String values;

        protocols = "http:";
        values = "https://www.foo.com/";
        assertFalse(UriTypeChecks.check(protocols, values));

        protocols = "http:";
        values = "https://www.foo.com/\nhttps://www.bar.com/";
        assertFalse(UriTypeChecks.check(protocols, values));

        protocols = "http:";
        values = "https://www.foo.com/\n \nhttps://www.bar.com/";
        assertFalse(UriTypeChecks.check(protocols, values));

        protocols = "http:";
        values = "https://www.foo.com/\nhttps://www.bar.com/\n https://www.baz.com/";
        assertFalse(UriTypeChecks.check(protocols, values));

    }
}
