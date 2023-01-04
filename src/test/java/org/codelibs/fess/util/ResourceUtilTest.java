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
package org.codelibs.fess.util;

import org.codelibs.fess.unit.UnitFessTestCase;

public class ResourceUtilTest extends UnitFessTestCase {
    public void test_resolve() {
        String value;

        value = null;
        assertNull(ResourceUtil.resolve(value));

        value = "";
        assertEquals("", ResourceUtil.resolve(value));

        value = "a";
        assertEquals(value, ResourceUtil.resolve(value));

        value = "${a}";
        assertEquals(value, ResourceUtil.resolve(value));

        value = "$a";
        assertEquals(value, ResourceUtil.resolve(value));

        value = "${a";
        assertEquals(value, ResourceUtil.resolve(value));

        value = "$a}";
        assertEquals(value, ResourceUtil.resolve(value));

        value = "${abc}";
        assertEquals(value, ResourceUtil.resolve(value));

        value = "${abc.xyz}";
        assertEquals(value, ResourceUtil.resolve(value));

        System.setProperty("abc", "123");

        value = "${abc}";
        assertEquals("123", ResourceUtil.resolve(value));

        value = "xxx${abc}zzz";
        assertEquals("xxx123zzz", ResourceUtil.resolve(value));

        value = "${abc.xyz}";
        assertEquals(value, ResourceUtil.resolve(value));

        System.setProperty("abc.xyz", "789");

        value = "${abc.xyz}";
        assertEquals("789", ResourceUtil.resolve(value));

        value = "${abc}${abc.xyz}";
        assertEquals("123789", ResourceUtil.resolve(value));

        value = "xxx${abc.xyz}zzz";
        assertEquals("xxx789zzz", ResourceUtil.resolve(value));

        value = "${\\$}";
        assertEquals(value, ResourceUtil.resolve(value));

        System.setProperty("test.dir", "c:\\test1\\test2");

        value = "${test.dir}";
        assertEquals("c:\\test1\\test2", ResourceUtil.resolve(value));

    }

}
