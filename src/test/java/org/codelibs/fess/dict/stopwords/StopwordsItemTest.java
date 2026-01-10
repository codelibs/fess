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
package org.codelibs.fess.dict.stopwords;

import org.codelibs.fess.unit.UnitFessTestCase;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import org.junit.jupiter.api.Test;

public class StopwordsItemTest extends UnitFessTestCase {

    @Test
    public void test_constructor_withIdZero() {
        // Test when id is 0 (create mode)
        StopwordsItem item = new StopwordsItem(0, "the");
        assertEquals(0, item.getId());
        assertEquals("the", item.getInput());
        assertEquals("the", item.getNewInput());
    }

    @Test
    public void test_constructor_withNonZeroId() {
        // Test when id is not 0 (existing item)
        StopwordsItem item = new StopwordsItem(123, "and");
        assertEquals(123, item.getId());
        assertEquals("and", item.getInput());
        assertNull(item.getNewInput());
    }

    @Test
    public void test_getNewInput_setNewInput() {
        // Test getter and setter for newInput
        StopwordsItem item = new StopwordsItem(1, "or");
        assertNull(item.getNewInput());

        item.setNewInput("nor");
        assertEquals("nor", item.getNewInput());

        item.setNewInput("");
        assertEquals("", item.getNewInput());

        item.setNewInput(null);
        assertNull(item.getNewInput());
    }

    @Test
    public void test_getInput() {
        // Test getInput method
        StopwordsItem item1 = new StopwordsItem(1, "test");
        assertEquals("test", item1.getInput());

        StopwordsItem item2 = new StopwordsItem(2, "");
        assertEquals("", item2.getInput());

        StopwordsItem item3 = new StopwordsItem(3, null);
        assertNull(item3.getInput());
    }

    @Test
    public void test_getInputValue() {
        // Test getInputValue method with various inputs
        StopwordsItem item1 = new StopwordsItem(1, "word");
        assertEquals("word", item1.getInputValue());

        StopwordsItem item2 = new StopwordsItem(2, "");
        assertEquals("", item2.getInputValue());

        // Test with null input - should return empty string
        StopwordsItem item3 = new StopwordsItem(3, null);
        assertEquals("", item3.getInputValue());
    }

    @Test
    public void test_isUpdated() {
        // Test isUpdated method
        StopwordsItem item = new StopwordsItem(1, "original");
        assertFalse(item.isUpdated());

        item.setNewInput("updated");
        assertTrue(item.isUpdated());

        item.setNewInput("");
        assertTrue(item.isUpdated());

        item.setNewInput(null);
        assertFalse(item.isUpdated());
    }

    @Test
    public void test_isDeleted() {
        // Test isDeleted method
        StopwordsItem item = new StopwordsItem(1, "word");
        assertFalse(item.isDeleted());

        item.setNewInput("new");
        assertFalse(item.isDeleted());

        // Mark for deletion with empty string
        item.setNewInput("");
        assertTrue(item.isDeleted());

        item.setNewInput(null);
        assertFalse(item.isDeleted());
    }

    @Test
    public void test_hashCode() {
        // Test hashCode method
        StopwordsItem item1 = new StopwordsItem(1, "test");
        StopwordsItem item2 = new StopwordsItem(1, "test");
        StopwordsItem item3 = new StopwordsItem(2, "test");
        StopwordsItem item4 = new StopwordsItem(1, "different");

        // Same input should have same hashCode regardless of id
        assertEquals(item1.hashCode(), item2.hashCode());
        assertEquals(item1.hashCode(), item3.hashCode());

        // Different input should have different hashCode
        assertNotSame(item1.hashCode(), item4.hashCode());
    }

    @Test
    public void test_hashCode_withNullInput() {
        // Test hashCode with null input - should not throw, returns consistent hash for null
        StopwordsItem item1 = new StopwordsItem(1, null);
        StopwordsItem item2 = new StopwordsItem(2, null);

        // Should not throw
        int hash1 = item1.hashCode();
        int hash2 = item2.hashCode();

        // Same null input should produce same hashCode
        assertEquals(hash1, hash2);

        // Consistent hashCode
        assertEquals(item1.hashCode(), item1.hashCode());
    }

    @Test
    public void test_equals() {
        // Test equals method
        StopwordsItem item1 = new StopwordsItem(1, "test");
        StopwordsItem item2 = new StopwordsItem(1, "test");
        StopwordsItem item3 = new StopwordsItem(2, "test");
        StopwordsItem item4 = new StopwordsItem(1, "different");

        // Same object
        assertTrue(item1.equals(item1));

        // Same input value (id doesn't matter)
        assertTrue(item1.equals(item2));
        assertTrue(item1.equals(item3));

        // Different input value
        assertFalse(item1.equals(item4));

        // Null and different class
        assertFalse(item1.equals(null));
        assertFalse(item1.equals("string"));
        assertFalse(item1.equals(new Object()));
    }

    @Test
    public void test_equals_withNewInput() {
        // Test equals method with newInput set
        StopwordsItem item1 = new StopwordsItem(1, "test");
        StopwordsItem item2 = new StopwordsItem(1, "test");

        item1.setNewInput("updated");
        item2.setNewInput("different");

        // Should still be equal based on original input
        assertTrue(item1.equals(item2));
    }

    @Test
    public void test_equals_withNullInput() {
        // Test equals with null input - should not throw, uses null-safe comparison
        StopwordsItem item1 = new StopwordsItem(1, null);
        StopwordsItem item2 = new StopwordsItem(2, null);
        StopwordsItem item3 = new StopwordsItem(1, "word");

        // Two items with null input should be equal
        assertTrue(item1.equals(item2));

        // Item with null input should not equal item with non-null input
        assertFalse(item3.equals(item1));
        assertFalse(item1.equals(item3));

        // Symmetry
        assertTrue(item1.equals(item2));
        assertTrue(item2.equals(item1));
    }

    @Test
    public void test_toString() {
        // Test toString method
        StopwordsItem item = new StopwordsItem(123, "word");
        assertEquals("StopwordsItem [id=123, inputs=word, newInputs=null]", item.toString());

        item.setNewInput("updated");
        assertEquals("StopwordsItem [id=123, inputs=word, newInputs=updated]", item.toString());

        item.setNewInput("");
        assertEquals("StopwordsItem [id=123, inputs=word, newInputs=]", item.toString());
    }

    @Test
    public void test_toLineString() {
        // Test toLineString method
        StopwordsItem item = new StopwordsItem(1, "original");
        assertEquals("original", item.toLineString());

        // When updated, should return newInput
        item.setNewInput("updated");
        assertEquals("updated", item.toLineString());

        // When marked for deletion
        item.setNewInput("");
        assertEquals("", item.toLineString());

        // When newInput is set back to null
        item.setNewInput(null);
        assertEquals("original", item.toLineString());
    }

    @Test
    public void test_toLineString_withEmptyInput() {
        // Test toLineString with empty original input
        StopwordsItem item = new StopwordsItem(1, "");
        assertEquals("", item.toLineString());

        item.setNewInput("new");
        assertEquals("new", item.toLineString());
    }

    @Test
    public void test_getId() {
        // Test getId method from parent class
        StopwordsItem item1 = new StopwordsItem(0, "word1");
        assertEquals(0, item1.getId());

        StopwordsItem item2 = new StopwordsItem(999, "word2");
        assertEquals(999, item2.getId());

        StopwordsItem item3 = new StopwordsItem(-1, "word3");
        assertEquals(-1, item3.getId());
    }

    @Test
    public void test_createMode() {
        // Test special behavior when id is 0 (create mode)
        StopwordsItem createItem = new StopwordsItem(0, "newword");
        assertTrue(createItem.isUpdated());
        assertEquals("newword", createItem.getNewInput());
        assertEquals("newword", createItem.toLineString());

        // Non-zero id should not auto-set newInput
        StopwordsItem existingItem = new StopwordsItem(1, "existingword");
        assertFalse(existingItem.isUpdated());
        assertNull(existingItem.getNewInput());
        assertEquals("existingword", existingItem.toLineString());
    }

    @Test
    public void test_multipleUpdates() {
        // Test multiple updates to newInput
        StopwordsItem item = new StopwordsItem(1, "original");

        item.setNewInput("first");
        assertEquals("first", item.toLineString());
        assertTrue(item.isUpdated());
        assertFalse(item.isDeleted());

        item.setNewInput("second");
        assertEquals("second", item.toLineString());
        assertTrue(item.isUpdated());
        assertFalse(item.isDeleted());

        item.setNewInput("");
        assertEquals("", item.toLineString());
        assertTrue(item.isUpdated());
        assertTrue(item.isDeleted());

        item.setNewInput("third");
        assertEquals("third", item.toLineString());
        assertTrue(item.isUpdated());
        assertFalse(item.isDeleted());
    }

    @Test
    public void test_specialCharacters() {
        // Test with special characters
        String specialInput = "test@#$%^&*()_+-=[]{}|;':\",./<>?";
        StopwordsItem item = new StopwordsItem(1, specialInput);
        assertEquals(specialInput, item.getInput());
        assertEquals(specialInput, item.getInputValue());
        assertEquals(specialInput, item.toLineString());

        String newSpecialInput = "new!@#$%";
        item.setNewInput(newSpecialInput);
        assertEquals(newSpecialInput, item.toLineString());
    }

    @Test
    public void test_whitespaceHandling() {
        // Test with various whitespace
        StopwordsItem item1 = new StopwordsItem(1, " ");
        assertEquals(" ", item1.getInput());
        assertEquals(" ", item1.toLineString());

        StopwordsItem item2 = new StopwordsItem(2, "\t\n\r");
        assertEquals("\t\n\r", item2.getInput());
        assertEquals("\t\n\r", item2.toLineString());

        StopwordsItem item3 = new StopwordsItem(3, "  word  ");
        assertEquals("  word  ", item3.getInput());
        assertEquals("  word  ", item3.toLineString());
    }

    @Test
    public void test_longStrings() {
        // Test with long strings
        StringBuilder longString = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longString.append("word");
        }
        String longInput = longString.toString();

        StopwordsItem item = new StopwordsItem(1, longInput);
        assertEquals(longInput, item.getInput());
        assertEquals(longInput, item.toLineString());

        String newLongInput = longInput + "extra";
        item.setNewInput(newLongInput);
        assertEquals(newLongInput, item.toLineString());
    }
}