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
package org.codelibs.fess.dict.stemmeroverride;

import org.codelibs.fess.unit.UnitFessTestCase;

public class StemmerOverrideItemTest extends UnitFessTestCase {

    public void test_new1() {
        final StemmerOverrideItem stemmerOverrideItem = new StemmerOverrideItem(1, "aaa", "a");
        assertEquals(1, stemmerOverrideItem.getId());
        assertEquals("aaa", stemmerOverrideItem.getInput());
        assertEquals("a", stemmerOverrideItem.getOutput());
        assertNull(stemmerOverrideItem.getNewInput());
        assertNull(stemmerOverrideItem.getNewOutput());
        assertFalse(stemmerOverrideItem.isUpdated());
        assertFalse(stemmerOverrideItem.isDeleted());

        stemmerOverrideItem.setNewInput("bbb");
        stemmerOverrideItem.setNewOutput("b");
        assertTrue(stemmerOverrideItem.isUpdated());
        assertFalse(stemmerOverrideItem.isDeleted());

        stemmerOverrideItem.setNewInput("");
        stemmerOverrideItem.setNewOutput("");
        assertTrue(stemmerOverrideItem.isUpdated());
        assertTrue(stemmerOverrideItem.isDeleted());
    }

    public void test_equals1() {
        final StemmerOverrideItem stemmerOverrideItem1 = new StemmerOverrideItem(1, "aaa", "a");

        assertTrue(stemmerOverrideItem1.equals(stemmerOverrideItem1));
        assertTrue(stemmerOverrideItem1.equals(new StemmerOverrideItem(1, "aaa", "a")));
        assertTrue(stemmerOverrideItem1.equals(new StemmerOverrideItem(2, "aaa", "a")));
        assertFalse(stemmerOverrideItem1.equals(new StemmerOverrideItem(1, "aaa", "b")));
        assertFalse(stemmerOverrideItem1.equals(new StemmerOverrideItem(2, "bbb", "a")));
        assertFalse(stemmerOverrideItem1.equals(new StemmerOverrideItem(2, "aaa", "b")));
    }

    public void test_toLineString() {
        assertEquals("aaa=>a", new StemmerOverrideItem(1, "aaa", "a").toLineString());
        assertEquals("=>", new StemmerOverrideItem(1, "", "").toLineString());
    }
}
