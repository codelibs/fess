/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
import org.junit.jupiter.api.Test;

public class SynonymItemTest extends UnitFessTestCase {

    @Test
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

    @Test
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

    @Test
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

    @Test
    public void test_equals2() {
        final SynonymItem synonymItem1 = new SynonymItem(1, new String[] { "a" }, new String[] { "b" });

        assertTrue(synonymItem1.equals(synonymItem1));
        assertTrue(synonymItem1.equals(new SynonymItem(1, new String[] { "a" }, new String[] { "b" })));
        assertFalse(synonymItem1.equals(new SynonymItem(2, new String[] { "a" }, new String[] { "B", })));
        assertFalse(synonymItem1.equals(new SynonymItem(2, new String[] { "A" }, new String[] { "b" })));
    }

    @Test
    public void test_toLineString() {
        assertEquals("a1,a2,a3=>b1,b2,b3",
                new SynonymItem(1, new String[] { "a1", "a2", "a3" }, new String[] { "b1", "b2", "b3" }).toLineString());
        assertEquals("a=>b", new SynonymItem(1, new String[] { "a" }, new String[] { "b" }).toLineString());
        assertEquals("A,a=>B,b", new SynonymItem(1, new String[] { "A", "a" }, new String[] { "B", "b" }).toLineString());
        assertEquals("A,a", new SynonymItem(1, new String[] { "A", "a" }, new String[] { "A", "a" }).toLineString());
        assertEquals("a", new SynonymItem(1, new String[] { "a" }, new String[] { "a" }).toLineString());
    }

    @Test
    public void test_defensiveCopy_inputs() {
        // Test that getInputs() returns defensive copy
        String[] originalInputs = { "a", "b", "c" };
        SynonymItem item = new SynonymItem(1, originalInputs, new String[] { "x" });

        String[] returnedInputs = item.getInputs();

        // Modifying returned array should not affect original
        returnedInputs[0] = "modified";

        // Get again and verify not modified
        String[] inputs2 = item.getInputs();
        assertEquals("a", inputs2[0]);
        assertEquals("b", inputs2[1]);
        assertEquals("c", inputs2[2]);
    }

    @Test
    public void test_defensiveCopy_outputs() {
        // Test that getOutputs() returns defensive copy
        String[] originalOutputs = { "x", "y", "z" };
        SynonymItem item = new SynonymItem(1, new String[] { "a" }, originalOutputs);

        String[] returnedOutputs = item.getOutputs();

        // Modifying returned array should not affect original
        returnedOutputs[0] = "modified";

        // Get again and verify not modified
        String[] outputs2 = item.getOutputs();
        assertEquals("x", outputs2[0]);
        assertEquals("y", outputs2[1]);
        assertEquals("z", outputs2[2]);
    }

    @Test
    public void test_defensiveCopy_newInputs() {
        // Test that getNewInputs() returns defensive copy
        SynonymItem item = new SynonymItem(1, new String[] { "a" }, new String[] { "x" });
        item.setNewInputs(new String[] { "p", "q", "r" });

        String[] returnedInputs = item.getNewInputs();

        // Modifying returned array should not affect original
        returnedInputs[0] = "modified";

        // Get again and verify not modified
        String[] inputs2 = item.getNewInputs();
        assertEquals("p", inputs2[0]);
        assertEquals("q", inputs2[1]);
        assertEquals("r", inputs2[2]);
    }

    @Test
    public void test_defensiveCopy_newOutputs() {
        // Test that getNewOutputs() returns defensive copy
        SynonymItem item = new SynonymItem(1, new String[] { "a" }, new String[] { "x" });
        item.setNewOutputs(new String[] { "p", "q", "r" });

        String[] returnedOutputs = item.getNewOutputs();

        // Modifying returned array should not affect original
        returnedOutputs[0] = "modified";

        // Get again and verify not modified
        String[] outputs2 = item.getNewOutputs();
        assertEquals("p", outputs2[0]);
        assertEquals("q", outputs2[1]);
        assertEquals("r", outputs2[2]);
    }

    @Test
    public void test_defensiveCopy_nullArrays() {
        // Test that defensive copy handles null arrays correctly
        SynonymItem item1 = new SynonymItem(1, new String[] { "a" }, new String[] { "x" });

        // Set to null
        item1.setNewInputs(null);
        item1.setNewOutputs(null);

        // Should return null, not crash
        assertNull(item1.getNewInputs());
        assertNull(item1.getNewOutputs());
    }
}
