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
package org.codelibs.fess.dict;

import org.codelibs.fess.unit.UnitFessTestCase;

public class DictionaryItemTest extends UnitFessTestCase {

    private TestDictionaryItem dictionaryItem;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        dictionaryItem = new TestDictionaryItem();
    }

    public void test_getId_defaultValue() {
        // Test that default ID value is 0
        assertEquals(0L, dictionaryItem.getId());
    }

    public void test_getId_afterSetting() {
        // Test getting ID after setting it
        dictionaryItem.id = 123L;
        assertEquals(123L, dictionaryItem.getId());
    }

    public void test_getId_negativeValue() {
        // Test with negative ID value
        dictionaryItem.id = -456L;
        assertEquals(-456L, dictionaryItem.getId());
    }

    public void test_getId_maxValue() {
        // Test with maximum Long value
        dictionaryItem.id = Long.MAX_VALUE;
        assertEquals(Long.MAX_VALUE, dictionaryItem.getId());
    }

    public void test_getId_minValue() {
        // Test with minimum Long value
        dictionaryItem.id = Long.MIN_VALUE;
        assertEquals(Long.MIN_VALUE, dictionaryItem.getId());
    }

    public void test_multipleInstances() {
        // Test that different instances have independent ID values
        TestDictionaryItem item1 = new TestDictionaryItem();
        TestDictionaryItem item2 = new TestDictionaryItem();

        item1.id = 100L;
        item2.id = 200L;

        assertEquals(100L, item1.getId());
        assertEquals(200L, item2.getId());

        // Verify they don't affect each other
        item1.id = 300L;
        assertEquals(300L, item1.getId());
        assertEquals(200L, item2.getId());
    }

    public void test_constructor() {
        // Test that constructor creates a valid instance
        TestDictionaryItem newItem = new TestDictionaryItem();
        assertNotNull(newItem);
        assertEquals(0L, newItem.getId());
    }

    public void test_idFieldAccess() {
        // Test direct field access through inheritance
        TestDictionaryItem item = new TestDictionaryItem();

        // Direct field assignment
        item.id = 999L;

        // Verify through getter
        assertEquals(999L, item.getId());

        // Verify direct field access
        assertEquals(999L, item.id);
    }

    public void test_sequentialIdAssignment() {
        // Test sequential ID assignment pattern
        TestDictionaryItem[] items = new TestDictionaryItem[5];

        for (int i = 0; i < items.length; i++) {
            items[i] = new TestDictionaryItem();
            items[i].id = i + 1;
        }

        for (int i = 0; i < items.length; i++) {
            assertEquals(i + 1, items[i].getId());
        }
    }

    public void test_idModification() {
        // Test modifying ID value multiple times
        TestDictionaryItem item = new TestDictionaryItem();

        // Initial value
        assertEquals(0L, item.getId());

        // First modification
        item.id = 50L;
        assertEquals(50L, item.getId());

        // Second modification
        item.id = 100L;
        assertEquals(100L, item.getId());

        // Third modification back to 0
        item.id = 0L;
        assertEquals(0L, item.getId());
    }

    /**
     * Concrete implementation of DictionaryItem for testing purposes.
     * Since DictionaryItem is abstract, we need a concrete class to test it.
     */
    private static class TestDictionaryItem extends DictionaryItem {
        // Simple concrete implementation for testing
    }
}