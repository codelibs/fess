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

package org.codelibs.fess.dict.userdict;

import org.seasar.extension.unit.S2TestCase;

public class UserDictItemTest extends S2TestCase {

    public void test_new1() {
        final UserDictItem userDictItem = new UserDictItem(1, "t1", "s1", "r1",
                "p1");
        assertEquals(1, userDictItem.getId());
        assertEquals("t1", userDictItem.getToken());
        assertEquals("s1", userDictItem.getSegmentation());
        assertEquals("r1", userDictItem.getReading());
        assertEquals("p1", userDictItem.getPos());
        assertNull(userDictItem.getNewToken());
        assertNull(userDictItem.getNewSegmentation());
        assertNull(userDictItem.getNewReading());
        assertNull(userDictItem.getNewPos());
        assertFalse(userDictItem.isUpdated());
        assertFalse(userDictItem.isDeleted());

        userDictItem.setNewToken("T1");
        userDictItem.setNewSegmentation("S1");
        userDictItem.setNewReading("R1");
        userDictItem.setNewPos("P1");
        assertTrue(userDictItem.isUpdated());
        assertFalse(userDictItem.isDeleted());

        userDictItem.setNewToken("");
        assertTrue(userDictItem.isUpdated());
        assertTrue(userDictItem.isDeleted());
    }

    public void test_equals1() {
        final UserDictItem userDictItem1 = new UserDictItem(1, "t1", "s1",
                "r1", "p1");

        assertTrue(userDictItem1.equals(userDictItem1));
        assertTrue(userDictItem1.equals(new UserDictItem(1, "t1", "s1", "r1",
                "p1")));
        assertTrue(userDictItem1.equals(new UserDictItem(2, "t1", "s1", "r1",
                "p1")));
        assertFalse(userDictItem1.equals(new UserDictItem(1, "T1", "s1", "r1",
                "p1")));
        assertFalse(userDictItem1.equals(new UserDictItem(1, "t1", "S1", "r1",
                "p1")));
        assertFalse(userDictItem1.equals(new UserDictItem(1, "t1", "s1", "R1",
                "p1")));
        assertFalse(userDictItem1.equals(new UserDictItem(1, "t1", "s1", "r1",
                "P1")));
    }

    public void test_toLineString() {
        assertEquals("t1,s1,r1,p1",
                new UserDictItem(1, "t1", "s1", "r1", "p1").toLineString());
        assertEquals("t\"\"1,s\"\"1,r\"\"1,p\"\"1", new UserDictItem(1, "t\"1",
                "s\"1", "r\"1", "p\"1").toLineString());
        assertEquals("\"t,1\",\"s,1\",\"r,1\",\"p,1\"", new UserDictItem(1,
                "t,1", "s,1", "r,1", "p,1").toLineString());
        assertEquals("\"t\"\",1\",\"s\"\",1\",\"r\"\",1\",\"p\"\",1\"",
                new UserDictItem(1, "t\",1", "s\",1", "r\",1", "p\",1")
                        .toLineString());
    }
}
