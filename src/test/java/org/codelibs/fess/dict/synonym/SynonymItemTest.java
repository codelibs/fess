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
package org.codelibs.fess.dict.synonym;

import org.codelibs.fess.unit.UnitFessTestCase;

public class SynonymItemTest extends UnitFessTestCase {

    public void test_new1() {
        final SynonymItem synonymItem = new SynonymItem(1, new String[] { "a", "A" }, new String[] { "b", "B" });
        assertEquals(1, synonymItem.getId());
        assertEquals(2, synonymItem.getInputs().length);
        assertEquals("a", synonymItem.getInputs()[0]);
        assertEquals("A", synonymItem.getInputs()[1]);
        assertEquals(2, synonymItem.getOutputs().length);
        assertEquals("b", synonymItem.getOutputs()[0]);
        assertEquals("B", synonymItem.getOutputs()[1]);
        assertNull(synonymItem.getNewInputs());
        assertNull(synonymItem.getNewOutputs());
        assertFalse(synonymItem.isUpdated());
        assertFalse(synonymItem.isDeleted());

        synonymItem.setNewInputs(new String[] { "1", "2" });
        synonymItem.setNewOutputs(new String[] { "3", "4" });
        assertTrue(synonymItem.isUpdated());
        assertFalse(synonymItem.isDeleted());

        synonymItem.setNewInputs(new String[0]);
        synonymItem.setNewOutputs(new String[0]);
        assertTrue(synonymItem.isUpdated());
        assertTrue(synonymItem.isDeleted());
    }

    public void test_new2() {
        final SynonymItem synonymItem = new SynonymItem(1, new String[] { "A", "a" }, new String[] { "B", "b" });
        assertEquals(1, synonymItem.getId());
        assertEquals(2, synonymItem.getInputs().length);
        assertEquals("A", synonymItem.getInputs()[0]);
        assertEquals("a", synonymItem.getInputs()[1]);
        assertEquals(2, synonymItem.getOutputs().length);
        assertEquals("B", synonymItem.getOutputs()[0]);
        assertEquals("b", synonymItem.getOutputs()[1]);
        assertNull(synonymItem.getNewInputs());
        assertNull(synonymItem.getNewOutputs());
        assertFalse(synonymItem.isUpdated());
        assertFalse(synonymItem.isDeleted());

        synonymItem.setNewInputs(new String[] { "2", "1" });
        synonymItem.setNewOutputs(new String[] { "4", "3" });
        assertTrue(synonymItem.isUpdated());
        assertFalse(synonymItem.isDeleted());

        synonymItem.setNewInputs(new String[0]);
        synonymItem.setNewOutputs(new String[0]);
        assertTrue(synonymItem.isUpdated());
        assertTrue(synonymItem.isDeleted());
    }

    public void test_equals1() {
        final SynonymItem synonymItem1 = new SynonymItem(1, new String[] { "a", "A" }, new String[] { "b", "B" });

        assertTrue(synonymItem1.equals(synonymItem1));
        assertTrue(synonymItem1.equals(new SynonymItem(1, new String[] { "A", "a" }, new String[] { "B", "b" })));
        assertTrue(synonymItem1.equals(new SynonymItem(2, new String[] { "A", "a" }, new String[] { "B", "b" })));
        assertFalse(synonymItem1.equals(new SynonymItem(2, new String[] { "A", "a" }, new String[] { "B", })));
        assertFalse(synonymItem1.equals(new SynonymItem(2, new String[] { "A" }, new String[] { "B", "b" })));
        assertFalse(synonymItem1.equals(new SynonymItem(1, new String[] { "A", "a" }, new String[] { "B", "c" })));
        assertFalse(synonymItem1.equals(new SynonymItem(1, new String[] { "A", "c" }, new String[] { "B", "b" })));
    }

    public void test_equals2() {
        final SynonymItem synonymItem1 = new SynonymItem(1, new String[] { "a" }, new String[] { "b" });

        assertTrue(synonymItem1.equals(synonymItem1));
        assertTrue(synonymItem1.equals(new SynonymItem(1, new String[] { "a" }, new String[] { "b" })));
        assertFalse(synonymItem1.equals(new SynonymItem(2, new String[] { "a" }, new String[] { "B", })));
        assertFalse(synonymItem1.equals(new SynonymItem(2, new String[] { "A" }, new String[] { "b" })));
    }

    public void test_toLineString() {
        assertEquals("a1,a2,a3=>b1,b2,b3",
                new SynonymItem(1, new String[] { "a1", "a2", "a3" }, new String[] { "b1", "b2", "b3" }).toLineString());
        assertEquals("a=>b", new SynonymItem(1, new String[] { "a" }, new String[] { "b" }).toLineString());
        assertEquals("A,a=>B,b", new SynonymItem(1, new String[] { "A", "a" }, new String[] { "B", "b" }).toLineString());
        assertEquals("A,a", new SynonymItem(1, new String[] { "A", "a" }, new String[] { "A", "a" }).toLineString());
        assertEquals("a", new SynonymItem(1, new String[] { "a" }, new String[] { "a" }).toLineString());
    }
}
