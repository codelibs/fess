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
package org.codelibs.fess.dict.protwords;

import org.codelibs.fess.unit.UnitFessTestCase;

public class ProtwordsItemTest extends UnitFessTestCase {

    public void test_constructor_withIdZero() {
        // Test constructor with id = 0 (create mode)
        ProtwordsItem item = new ProtwordsItem(0, "testword");

        assertEquals(0, item.getId());
        assertEquals("testword", item.getInput());
        assertEquals("testword", item.getNewInput());
    }

    public void test_constructor_withNonZeroId() {
        // Test constructor with id != 0 (existing item)
        ProtwordsItem item = new ProtwordsItem(123, "existingword");

        assertEquals(123, item.getId());
        assertEquals("existingword", item.getInput());
        assertNull(item.getNewInput());
    }

    public void test_getNewInput() {
        // Test getNewInput method
        ProtwordsItem item = new ProtwordsItem(1, "word");
        assertNull(item.getNewInput());

        item.setNewInput("newword");
        assertEquals("newword", item.getNewInput());
    }

    public void test_setNewInput() {
        // Test setNewInput method
        ProtwordsItem item = new ProtwordsItem(1, "word");

        item.setNewInput("updatedword");
        assertEquals("updatedword", item.getNewInput());

        item.setNewInput(null);
        assertNull(item.getNewInput());

        item.setNewInput("");
        assertEquals("", item.getNewInput());
    }

    public void test_getInput() {
        // Test getInput method
        ProtwordsItem item1 = new ProtwordsItem(1, "word1");
        assertEquals("word1", item1.getInput());

        ProtwordsItem item2 = new ProtwordsItem(2, "");
        assertEquals("", item2.getInput());

        ProtwordsItem item3 = new ProtwordsItem(3, null);
        assertNull(item3.getInput());
    }

    public void test_getInputValue() {
        // Test getInputValue method with non-null input
        ProtwordsItem item1 = new ProtwordsItem(1, "word");
        assertEquals("word", item1.getInputValue());

        // Test with empty string
        ProtwordsItem item2 = new ProtwordsItem(2, "");
        assertEquals("", item2.getInputValue());

        // Test with null input - should return empty string
        ProtwordsItem item3 = new ProtwordsItem(3, null);
        assertEquals("", item3.getInputValue());
    }

    public void test_isUpdated() {
        // Test isUpdated method
        ProtwordsItem item = new ProtwordsItem(1, "word");
        assertFalse(item.isUpdated());

        item.setNewInput("newword");
        assertTrue(item.isUpdated());

        item.setNewInput("");
        assertTrue(item.isUpdated());

        item.setNewInput(null);
        assertFalse(item.isUpdated());
    }

    public void test_isDeleted() {
        // Test isDeleted method
        ProtwordsItem item = new ProtwordsItem(1, "word");
        assertFalse(item.isDeleted());

        item.setNewInput("newword");
        assertFalse(item.isDeleted());

        item.setNewInput("");
        assertTrue(item.isDeleted());

        item.setNewInput(null);
        assertFalse(item.isDeleted());
    }

    public void test_hashCode() {
        // Test hashCode method
        ProtwordsItem item1 = new ProtwordsItem(1, "word");
        ProtwordsItem item2 = new ProtwordsItem(2, "word");
        ProtwordsItem item3 = new ProtwordsItem(1, "different");

        // Same input should have same hashCode regardless of id
        assertEquals(item1.hashCode(), item2.hashCode());

        // Different input should have different hashCode
        assertNotSame(item1.hashCode(), item3.hashCode());

        // Consistent hashCode
        assertEquals(item1.hashCode(), item1.hashCode());
    }

    public void test_hashCode_withNullInput() {
        // Test hashCode with null input should throw NullPointerException
        ProtwordsItem item = new ProtwordsItem(1, null);
        try {
            item.hashCode();
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    public void test_equals() {
        // Test equals method
        ProtwordsItem item1 = new ProtwordsItem(1, "word");
        ProtwordsItem item2 = new ProtwordsItem(2, "word");
        ProtwordsItem item3 = new ProtwordsItem(1, "different");
        ProtwordsItem item4 = new ProtwordsItem(1, "word");

        // Same object
        assertTrue(item1.equals(item1));

        // Same input, different id
        assertTrue(item1.equals(item2));

        // Different input
        assertFalse(item1.equals(item3));

        // Same id and input
        assertTrue(item1.equals(item4));

        // Null object
        assertFalse(item1.equals(null));

        // Different class
        assertFalse(item1.equals("word"));

        // Symmetry
        assertTrue(item1.equals(item2));
        assertTrue(item2.equals(item1));
    }

    public void test_equals_withNullInput() {
        // Test equals with null input
        ProtwordsItem item1 = new ProtwordsItem(1, null);
        ProtwordsItem item2 = new ProtwordsItem(2, null);
        ProtwordsItem item3 = new ProtwordsItem(1, "word");

        try {
            item1.equals(item2);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        // equals with null input may return false instead of throwing NPE
        assertFalse(item3.equals(item1));
    }

    public void test_toString() {
        // Test toString method
        ProtwordsItem item = new ProtwordsItem(123, "testword");
        assertEquals("ProtwordsItem [id=123, inputs=testword, newInputs=null]", item.toString());

        item.setNewInput("newword");
        assertEquals("ProtwordsItem [id=123, inputs=testword, newInputs=newword]", item.toString());

        ProtwordsItem item2 = new ProtwordsItem(0, "createword");
        assertEquals("ProtwordsItem [id=0, inputs=createword, newInputs=createword]", item2.toString());
    }

    public void test_toLineString() {
        // Test toLineString method with non-updated item
        ProtwordsItem item = new ProtwordsItem(1, "word");
        assertEquals("word", item.toLineString());

        // Test with updated item
        item.setNewInput("updatedword");
        assertEquals("updatedword", item.toLineString());

        // Test with deleted item (empty newInput)
        item.setNewInput("");
        assertEquals("", item.toLineString());

        // Test with item created with id=0
        ProtwordsItem newItem = new ProtwordsItem(0, "newword");
        assertEquals("newword", newItem.toLineString());
    }

    public void test_toLineString_withEmptyInput() {
        // Test toLineString with empty input
        ProtwordsItem item = new ProtwordsItem(1, "");
        assertEquals("", item.toLineString());

        item.setNewInput("word");
        assertEquals("word", item.toLineString());
    }

    public void test_toLineString_withNullNewInput() {
        // Test toLineString when newInput is set back to null
        ProtwordsItem item = new ProtwordsItem(1, "originalword");
        item.setNewInput("tempword");
        assertEquals("tempword", item.toLineString());

        item.setNewInput(null);
        assertEquals("originalword", item.toLineString());
    }

    public void test_getId() {
        // Test getId method inherited from DictionaryItem
        ProtwordsItem item1 = new ProtwordsItem(0, "word");
        assertEquals(0, item1.getId());

        ProtwordsItem item2 = new ProtwordsItem(999, "word");
        assertEquals(999, item2.getId());

        ProtwordsItem item3 = new ProtwordsItem(-1, "word");
        assertEquals(-1, item3.getId());
    }

    public void test_multipleUpdates() {
        // Test multiple updates to newInput
        ProtwordsItem item = new ProtwordsItem(1, "original");

        assertFalse(item.isUpdated());
        assertFalse(item.isDeleted());

        item.setNewInput("first");
        assertTrue(item.isUpdated());
        assertFalse(item.isDeleted());
        assertEquals("first", item.toLineString());

        item.setNewInput("second");
        assertTrue(item.isUpdated());
        assertFalse(item.isDeleted());
        assertEquals("second", item.toLineString());

        item.setNewInput("");
        assertTrue(item.isUpdated());
        assertTrue(item.isDeleted());
        assertEquals("", item.toLineString());

        item.setNewInput("third");
        assertTrue(item.isUpdated());
        assertFalse(item.isDeleted());
        assertEquals("third", item.toLineString());
    }

    public void test_specialCharacters() {
        // Test with special characters in input
        String specialInput = "test@#$%^&*()_+-=[]{}|;':\",./<>?";
        ProtwordsItem item = new ProtwordsItem(1, specialInput);

        assertEquals(specialInput, item.getInput());
        assertEquals(specialInput, item.getInputValue());
        assertEquals(specialInput, item.toLineString());

        String newSpecialInput = "new!@#$%^&*()";
        item.setNewInput(newSpecialInput);
        assertEquals(newSpecialInput, item.toLineString());
    }

    public void test_whitespaceHandling() {
        // Test with various whitespace scenarios
        ProtwordsItem item1 = new ProtwordsItem(1, "  word  ");
        assertEquals("  word  ", item1.getInput());
        assertEquals("  word  ", item1.toLineString());

        ProtwordsItem item2 = new ProtwordsItem(2, "\tword\n");
        assertEquals("\tword\n", item2.getInput());
        assertEquals("\tword\n", item2.toLineString());

        ProtwordsItem item3 = new ProtwordsItem(3, " ");
        assertEquals(" ", item3.getInput());
        assertEquals(" ", item3.toLineString());
    }
}