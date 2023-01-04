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
package org.codelibs.fess.validation;

import org.codelibs.fess.unit.UnitFessTestCase;

public class UriTypeValidatorTest extends UnitFessTestCase {
    public void test_check_ok() {
        String[] protocols;
        String values;

        protocols = new String[] { "http:" };
        values = "http://www.foo.com/";
        assertTrue(UriTypeValidator.check(protocols, values));

        protocols = new String[] { "http:" };
        values = "http://www.foo.com/\nhttp://www.bar.com/";
        assertTrue(UriTypeValidator.check(protocols, values));

        protocols = new String[] { "http:" };
        values = "http://www.foo.com/ \r\nhttp://www.bar.com/";
        assertTrue(UriTypeValidator.check(protocols, values));

        protocols = new String[] { "http:" };
        values = "http://www.foo.com/\nhttp://www.bar.com/\n http://www.baz.com/";
        assertTrue(UriTypeValidator.check(protocols, values));

        protocols = new String[] { "http:", "https:" };
        values = "https://www.foo.com/";
        assertTrue(UriTypeValidator.check(protocols, values));

        protocols = new String[] { "http:", "https:" };
        values = "http://www.foo.com/\r\nhttp://www.bar.com/";
        assertTrue(UriTypeValidator.check(protocols, values));

        protocols = new String[] { "http:", "https:" };
        values = "http://www.foo.com/\nhttps://www.bar.com/";
        assertTrue(UriTypeValidator.check(protocols, values));

        protocols = new String[] { "http:", "https:" };
        values = "http://www.foo.com/\n \nhttps://www.bar.com/";
        assertTrue(UriTypeValidator.check(protocols, values));

        protocols = new String[] { "http:", "https:" };
        values = "http://www.foo.com/\nhttps://www.bar.com/\n http://www.baz.com/";
        assertTrue(UriTypeValidator.check(protocols, values));
    }

    public void test_check_ng() {
        String[] protocols;
        String values;

        protocols = new String[] { "http:" };
        values = "https://www.foo.com/";
        assertFalse(UriTypeValidator.check(protocols, values));

        protocols = new String[] { "http:" };
        values = "https://www.foo.com/\nhttps://www.bar.com/";
        assertFalse(UriTypeValidator.check(protocols, values));

        protocols = new String[] { "http:" };
        values = "https://www.foo.com/\n \nhttps://www.bar.com/";
        assertFalse(UriTypeValidator.check(protocols, values));

        protocols = new String[] { "http:" };
        values = "https://www.foo.com/\nhttps://www.bar.com/\n https://www.baz.com/";
        assertFalse(UriTypeValidator.check(protocols, values));

    }
}
