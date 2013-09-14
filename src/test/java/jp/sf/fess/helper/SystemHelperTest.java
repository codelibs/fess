/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

public class SystemHelperTest extends S2TestCase {
    public SystemHelper systemHelper;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_encodeUrlFilter() {
        String path = null;
        assertNull(systemHelper.encodeUrlFilter(path));

        path = "abc";
        assertEquals(path, systemHelper.encodeUrlFilter(path));

        path = "あいう";
        assertEquals("%E3%81%82%E3%81%84%E3%81%86",
                systemHelper.encodeUrlFilter(path));

        path = "[]^$.*+?,{}|%\\";
        assertEquals(path, systemHelper.encodeUrlFilter(path));
    }
}
