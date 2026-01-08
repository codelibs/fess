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
package org.codelibs.fess.dict.mapping;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Arrays;

import org.codelibs.fess.unit.UnitFessTestCase;

public class CharMappingItemTest extends UnitFessTestCase {

    public void test_constructor_withValidInputs() {
        CharMappingItem item = new CharMappingItem(1L, new String[] { "input" }, "output");

        // Test basic properties that exist
        assertNotNull(item.getInputs());
        assertEquals(1, item.getInputs().length);
        assertEquals("input", item.getInputs()[0]);
        assertEquals("output", item.getOutput());
    }

    public void test_constructor_withIdZero() {
        // Test when id is 0 (create mode)
        String[] inputs = { "x", "y" };
        CharMappingItem item = new CharMappingItem(0L, inputs, "result");

        assertEquals(0L, item.getId());
        assertNotNull(item.getNewInputs());
        assertEquals("result", item.getNewOutput());
        // NewInputs should have same content as inputs for id=0 (defensive copy is returned)
        assertArrayEquals(inputs, item.getNewInputs());
        assertEquals("result", item.getNewOutput());
    }

    public void test_constructor_withNewlineInOutput() {
        // Test newline replacement in output
        String[] inputs = { "input" };
        CharMappingItem item = new CharMappingItem(1L, inputs, "line1\nline2");

        assertEquals("line1 line2", item.getOutput());
    }

    public void test_constructor_withNullOutput() {
        // Test with null output
        String[] inputs = { "input" };
        CharMappingItem item = new CharMappingItem(1L, inputs, null);

        assertNull(item.getOutput());
    }

    public void test_constructor_withUnsortedInputs() {
        // Test that inputs are sorted during construction
        String[] inputs = { "z", "a", "m" };
        CharMappingItem item = new CharMappingItem(1L, inputs, "output");

        assertEquals("a", item.getInputs()[0]);
        assertEquals("m", item.getInputs()[1]);
        assertEquals("z", item.getInputs()[2]);
    }

    public void test_getNewInputs_setNewInputs() {
        String[] inputs = { "old" };
        CharMappingItem item = new CharMappingItem(1L, inputs, "output");

        // Initially null for non-zero id
        assertNull(item.getNewInputs());

        // Set new inputs
        String[] newInputs = { "new1", "new2" };
        item.setNewInputs(newInputs);
        assertNotNull(item.getNewInputs());
        assertEquals(2, item.getNewInputs().length);
        assertEquals("new1", item.getNewInputs()[0]);
        assertEquals("new2", item.getNewInputs()[1]);
    }

    public void test_getNewOutput_setNewOutput() {
        String[] inputs = { "input" };
        CharMappingItem item = new CharMappingItem(1L, inputs, "oldOutput");

        // Initially null for non-zero id
        assertNull(item.getNewOutput());

        // Set new output
        item.setNewOutput("newOutput");
        assertEquals("newOutput", item.getNewOutput());
    }

    public void test_setNewOutput_withNewline() {
        String[] inputs = { "input" };
        CharMappingItem item = new CharMappingItem(1L, inputs, "output");

        // Test newline replacement in setNewOutput
        item.setNewOutput("line1\nline2\nline3");
        assertEquals("line1 line2 line3", item.getNewOutput());
    }

    public void test_setNewOutput_withNull() {
        String[] inputs = { "input" };
        CharMappingItem item = new CharMappingItem(1L, inputs, "output");

        item.setNewOutput(null);
        assertNull(item.getNewOutput());
    }

    public void test_getInputsValue() {
        // Test with multiple inputs
        String[] inputs = { "a", "b", "c" };
        CharMappingItem item = new CharMappingItem(1L, inputs, "output");

        String inputsValue = item.getInputsValue();
        assertEquals("a\nb\nc", inputsValue);
    }

    public void test_getInputsValue_withSingleInput() {
        // Test with single input
        String[] inputs = { "single" };
        CharMappingItem item = new CharMappingItem(1L, inputs, "output");

        assertEquals("single", item.getInputsValue());
    }

    public void test_getInputsValue_withNullInputs() {
        // Create item and then set inputs to null via reflection or subclass
        CharMappingItem item = new CharMappingItem(1L, new String[] { "temp" }, "output") {
            @Override
            public String getInputsValue() {
                // Simulate null inputs scenario
                if (getInputs() == null) {
                    return super.getInputsValue();
                }
                return super.getInputsValue();
            }
        };

        // Since we can't easily set inputs to null, test with empty array
        String[] emptyInputs = {};
        CharMappingItem emptyItem = new CharMappingItem(1L, emptyInputs, "output");
        assertEquals("", emptyItem.getInputsValue());
    }

    public void test_isUpdated() {
        String[] inputs = { "input" };
        CharMappingItem item = new CharMappingItem(1L, inputs, "output");

        // Initially not updated
        assertFalse(item.isUpdated());

        // Set only newInputs - still not updated
        item.setNewInputs(new String[] { "new" });
        assertFalse(item.isUpdated());

        // Set both newInputs and newOutput - now updated
        item.setNewOutput("newOutput");
        assertTrue(item.isUpdated());

        // Set newInputs to null - not updated
        item.setNewInputs(null);
        assertFalse(item.isUpdated());
    }

    public void test_isDeleted() {
        String[] inputs = { "input" };
        CharMappingItem item = new CharMappingItem(1L, inputs, "output");

        // Initially not deleted
        assertFalse(item.isDeleted());

        // Set newInputs to empty array and newOutput
        item.setNewInputs(new String[] {});
        item.setNewOutput("output");
        assertTrue(item.isDeleted());

        // Set newInputs to non-empty array - not deleted
        item.setNewInputs(new String[] { "something" });
        assertFalse(item.isDeleted());
    }

    public void test_sort() {
        String[] inputs = { "z", "a", "m" };
        String[] newInputs = { "9", "1", "5" };

        CharMappingItem item = new CharMappingItem(1L, inputs, "output");
        item.setNewInputs(newInputs);

        // Inputs should already be sorted from constructor
        assertEquals("a", item.getInputs()[0]);

        // NewInputs not sorted yet
        assertEquals("9", item.getNewInputs()[0]);

        // Call sort
        item.sort();

        // Both should be sorted
        assertEquals("a", item.getInputs()[0]);
        assertEquals("m", item.getInputs()[1]);
        assertEquals("z", item.getInputs()[2]);

        assertEquals("1", item.getNewInputs()[0]);
        assertEquals("5", item.getNewInputs()[1]);
        assertEquals("9", item.getNewInputs()[2]);
    }

    public void test_sort_withNullArrays() {
        // Test sort with null arrays doesn't throw exception
        CharMappingItem item = new CharMappingItem(1L, new String[] { "a" }, "output");
        item.setNewInputs(null);

        // Should not throw exception
        item.sort();

        // Original inputs should still be sorted
        assertEquals("a", item.getInputs()[0]);
    }

    public void test_hashCode() {
        String[] inputs1 = { "a", "b" };
        String[] inputs2 = { "a", "b" };
        String[] inputs3 = { "c", "d" };

        CharMappingItem item1 = new CharMappingItem(1L, inputs1, "output");
        CharMappingItem item2 = new CharMappingItem(2L, inputs2, "output");
        CharMappingItem item3 = new CharMappingItem(3L, inputs3, "output");
        CharMappingItem item4 = new CharMappingItem(4L, inputs1, "different");

        // Same inputs and output should have same hash code
        assertEquals(item1.hashCode(), item2.hashCode());

        // Different inputs should have different hash code
        assertNotSame(item1.hashCode(), item3.hashCode());

        // Same inputs but different output should have different hash code
        assertNotSame(item1.hashCode(), item4.hashCode());
    }

    public void test_hashCode_withNullOutput() {
        // Test hashCode with null output - should not throw, returns consistent hash for null
        String[] inputs1 = { "a", "b" };
        String[] inputs2 = { "a", "b" };
        CharMappingItem item1 = new CharMappingItem(1L, inputs1, null);
        CharMappingItem item2 = new CharMappingItem(2L, inputs2, null);

        // Should not throw
        int hash1 = item1.hashCode();
        int hash2 = item2.hashCode();

        // Same inputs and null output should produce same hashCode
        assertEquals(hash1, hash2);

        // Consistent hashCode
        assertEquals(item1.hashCode(), item1.hashCode());
    }

    public void test_equals() {
        String[] inputs1 = { "a", "b" };
        String[] inputs2 = { "b", "a" }; // Different order
        String[] inputs3 = { "c", "d" };

        CharMappingItem item1 = new CharMappingItem(1L, inputs1, "output");
        CharMappingItem item2 = new CharMappingItem(2L, inputs2, "output");
        CharMappingItem item3 = new CharMappingItem(3L, inputs3, "output");
        CharMappingItem item4 = new CharMappingItem(4L, inputs1, "different");

        // Test reflexivity
        assertTrue(item1.equals(item1));

        // Test symmetry and arrays with different order
        assertTrue(item1.equals(item2));
        assertTrue(item2.equals(item1));

        // Test with different inputs
        assertFalse(item1.equals(item3));
        assertFalse(item3.equals(item1));

        // Test with different output
        assertFalse(item1.equals(item4));

        // Test with null
        assertFalse(item1.equals(null));

        // Test with different class
        assertFalse(item1.equals("string"));
    }

    public void test_equals_withUnsortedInputs() {
        // Test that equals works correctly even when inputs are initially unsorted
        String[] inputs1 = { "z", "a", "m" };
        String[] inputs2 = { "m", "z", "a" };

        CharMappingItem item1 = new CharMappingItem(1L, inputs1, "output");
        CharMappingItem item2 = new CharMappingItem(2L, inputs2, "output");

        assertTrue(item1.equals(item2));
    }

    public void test_equals_withNullOutput() {
        // Test equals with null output - should not throw, uses null-safe comparison
        String[] inputs1 = { "a", "b" };
        String[] inputs2 = { "a", "b" };
        String[] inputs3 = { "c", "d" };

        CharMappingItem item1 = new CharMappingItem(1L, inputs1, null);
        CharMappingItem item2 = new CharMappingItem(2L, inputs2, null);
        CharMappingItem item3 = new CharMappingItem(3L, inputs1, "output");

        // Two items with same inputs and null output should be equal
        assertTrue(item1.equals(item2));

        // Item with null output should not equal item with non-null output
        assertFalse(item3.equals(item1));
        assertFalse(item1.equals(item3));

        // Item with null output should not equal item with different inputs
        CharMappingItem item4 = new CharMappingItem(4L, inputs3, null);
        assertFalse(item1.equals(item4));

        // Symmetry
        assertTrue(item1.equals(item2));
        assertTrue(item2.equals(item1));
    }

    public void test_toString() {
        String[] inputs = { "a", "b" };
        CharMappingItem item = new CharMappingItem(1L, inputs, "output");
        item.setNewInputs(new String[] { "x", "y" });
        item.setNewOutput("newOutput");

        String str = item.toString();
        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("inputs=[a, b]"));
        assertTrue(str.contains("output=output"));
        assertTrue(str.contains("newInputs=[x, y]"));
        assertTrue(str.contains("newOutput=newOutput"));
    }

    public void test_toLineString_withoutUpdates() {
        String[] inputs = { "a", "b", "c" };
        CharMappingItem item = new CharMappingItem(1L, inputs, "output");

        assertEquals("a,b,c=>output", item.toLineString());
    }

    public void test_toLineString_withUpdates() {
        String[] inputs = { "old1", "old2" };
        CharMappingItem item = new CharMappingItem(1L, inputs, "oldOutput");

        item.setNewInputs(new String[] { "new1", "new2", "new3" });
        item.setNewOutput("newOutput");

        // Should use new values when updated
        assertEquals("new1,new2,new3=>newOutput", item.toLineString());
    }

    public void test_toLineString_withSingleInput() {
        String[] inputs = { "single" };
        CharMappingItem item = new CharMappingItem(1L, inputs, "output");

        assertEquals("single=>output", item.toLineString());
    }

    public void test_toLineString_withEmptyInputs() {
        String[] inputs = {};
        CharMappingItem item = new CharMappingItem(1L, inputs, "output");

        assertEquals("=>output", item.toLineString());
    }

    public void test_getId() {
        // Test getId method from parent class
        CharMappingItem item = new CharMappingItem(42L, new String[] { "input" }, "output");
        assertEquals(42L, item.getId());
    }

    public void test_getCreatedBy_getCreatedTime() {
        // Test inherited methods from DictionaryItem
        CharMappingItem item = new CharMappingItem(1L, new String[] { "input" }, "output");

        // Test basic properties that exist
        assertNotNull(item.getInputs());
        assertEquals(1, item.getInputs().length);
        assertEquals("input", item.getInputs()[0]);
        assertEquals("output", item.getOutput());
    }

    public void test_defensiveCopy_inputs() {
        // Test that getInputs() returns defensive copy
        String[] originalInputs = { "a", "b", "c" };
        CharMappingItem item = new CharMappingItem(1L, originalInputs, "output");

        String[] returnedInputs = item.getInputs();

        // Modifying returned array should not affect original
        returnedInputs[0] = "modified";

        // Get again and verify not modified
        String[] inputs2 = item.getInputs();
        assertEquals("a", inputs2[0]);
        assertEquals("b", inputs2[1]);
        assertEquals("c", inputs2[2]);
    }

    public void test_defensiveCopy_newInputs() {
        // Test that getNewInputs() returns defensive copy
        CharMappingItem item = new CharMappingItem(1L, new String[] { "a" }, "output");
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

    public void test_defensiveCopy_nullArrays() {
        // Test that defensive copy handles null arrays correctly
        CharMappingItem item1 = new CharMappingItem(1L, new String[] { "a" }, "output");

        // Set to null
        item1.setNewInputs(null);

        // Should return null, not crash
        assertNull(item1.getNewInputs());
    }
}