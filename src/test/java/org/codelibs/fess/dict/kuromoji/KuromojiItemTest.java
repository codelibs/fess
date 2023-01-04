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
package org.codelibs.fess.dict.kuromoji;

import org.codelibs.fess.unit.UnitFessTestCase;

public class KuromojiItemTest extends UnitFessTestCase {
    public void test_new1() {
        final KuromojiItem kuromojiItem = new KuromojiItem(1, "t1", "s1", "r1", "p1");
        assertEquals(1, kuromojiItem.getId());
        assertEquals("t1", kuromojiItem.getToken());
        assertEquals("s1", kuromojiItem.getSegmentation());
        assertEquals("r1", kuromojiItem.getReading());
        assertEquals("p1", kuromojiItem.getPos());
        assertNull(kuromojiItem.getNewToken());
        assertNull(kuromojiItem.getNewSegmentation());
        assertNull(kuromojiItem.getNewReading());
        assertNull(kuromojiItem.getNewPos());
        assertFalse(kuromojiItem.isUpdated());
        assertFalse(kuromojiItem.isDeleted());

        kuromojiItem.setNewToken("T1");
        kuromojiItem.setNewSegmentation("S1");
        kuromojiItem.setNewReading("R1");
        kuromojiItem.setNewPos("P1");
        assertTrue(kuromojiItem.isUpdated());
        assertFalse(kuromojiItem.isDeleted());

        kuromojiItem.setNewToken("");
        assertTrue(kuromojiItem.isUpdated());
        assertTrue(kuromojiItem.isDeleted());
    }

    public void test_equals1() {
        final KuromojiItem kuromojiItem1 = new KuromojiItem(1, "t1", "s1", "r1", "p1");

        assertTrue(kuromojiItem1.equals(kuromojiItem1));
        assertTrue(kuromojiItem1.equals(new KuromojiItem(1, "t1", "s1", "r1", "p1")));
        assertTrue(kuromojiItem1.equals(new KuromojiItem(2, "t1", "s1", "r1", "p1")));
        assertFalse(kuromojiItem1.equals(new KuromojiItem(1, "T1", "s1", "r1", "p1")));
        assertFalse(kuromojiItem1.equals(new KuromojiItem(1, "t1", "S1", "r1", "p1")));
        assertFalse(kuromojiItem1.equals(new KuromojiItem(1, "t1", "s1", "R1", "p1")));
        assertFalse(kuromojiItem1.equals(new KuromojiItem(1, "t1", "s1", "r1", "P1")));
    }

    public void test_toLineString() {
        assertEquals("t1,s1,r1,p1", new KuromojiItem(1, "t1", "s1", "r1", "p1").toLineString());
        assertEquals("t\"\"1,s\"\"1,r\"\"1,p\"\"1", new KuromojiItem(1, "t\"1", "s\"1", "r\"1", "p\"1").toLineString());
        assertEquals("\"t,1\",\"s,1\",\"r,1\",\"p,1\"", new KuromojiItem(1, "t,1", "s,1", "r,1", "p,1").toLineString());
        assertEquals("\"t\"\",1\",\"s\"\",1\",\"r\"\",1\",\"p\"\",1\"",
                new KuromojiItem(1, "t\",1", "s\",1", "r\",1", "p\",1").toLineString());
    }
}
