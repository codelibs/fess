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
package org.codelibs.fess.entity;

import org.codelibs.fess.unit.UnitFessTestCase;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

public class RequestParameterTest extends UnitFessTestCase {

    @Test
    public void test_constructor_withValidNameAndValues() {
        // Test with normal name and values
        String name = "paramName";
        String[] values = { "value1", "value2", "value3" };
        RequestParameter param = new RequestParameter(name, values);

        assertEquals(name, param.getName());
        assertNotNull(param.getValues());
        assertEquals(3, param.getValues().length);
        assertEquals("value1", param.getValues()[0]);
        assertEquals("value2", param.getValues()[1]);
        assertEquals("value3", param.getValues()[2]);
    }

    @Test
    public void test_constructor_withNullName() {
        // Test with null name
        String[] values = { "value1" };
        RequestParameter param = new RequestParameter(null, values);

        assertNull(param.getName());
        assertNotNull(param.getValues());
        assertEquals(1, param.getValues().length);
    }

    @Test
    public void test_constructor_withEmptyName() {
        // Test with empty name
        String name = "";
        String[] values = { "value1", "value2" };
        RequestParameter param = new RequestParameter(name, values);

        assertEquals("", param.getName());
        assertNotNull(param.getValues());
        assertEquals(2, param.getValues().length);
    }

    @Test
    public void test_constructor_withNullValues() {
        // Test with null values array
        String name = "paramName";
        RequestParameter param = new RequestParameter(name, null);

        assertEquals(name, param.getName());
        assertNull(param.getValues());
    }

    @Test
    public void test_constructor_withEmptyValues() {
        // Test with empty values array
        String name = "paramName";
        String[] values = {};
        RequestParameter param = new RequestParameter(name, values);

        assertEquals(name, param.getName());
        assertNotNull(param.getValues());
        assertEquals(0, param.getValues().length);
    }

    @Test
    public void test_constructor_withSingleValue() {
        // Test with single value
        String name = "singleParam";
        String[] values = { "onlyValue" };
        RequestParameter param = new RequestParameter(name, values);

        assertEquals(name, param.getName());
        assertNotNull(param.getValues());
        assertEquals(1, param.getValues().length);
        assertEquals("onlyValue", param.getValues()[0]);
    }

    @Test
    public void test_constructor_withNullAndNonNullValues() {
        // Test with mix of null and non-null values
        String name = "mixedParam";
        String[] values = { "value1", null, "value3", null };
        RequestParameter param = new RequestParameter(name, values);

        assertEquals(name, param.getName());
        assertNotNull(param.getValues());
        assertEquals(4, param.getValues().length);
        assertEquals("value1", param.getValues()[0]);
        assertNull(param.getValues()[1]);
        assertEquals("value3", param.getValues()[2]);
        assertNull(param.getValues()[3]);
    }

    @Test
    public void test_constructor_withSpecialCharacters() {
        // Test with special characters in name and values
        String name = "param-name_123!@#";
        String[] values = { "value with spaces", "value@special#chars", "日本語" };
        RequestParameter param = new RequestParameter(name, values);

        assertEquals(name, param.getName());
        assertNotNull(param.getValues());
        assertEquals(3, param.getValues().length);
        assertEquals("value with spaces", param.getValues()[0]);
        assertEquals("value@special#chars", param.getValues()[1]);
        assertEquals("日本語", param.getValues()[2]);
    }

    @Test
    public void test_toString_withValidData() {
        // Test toString with normal data
        String name = "testParam";
        String[] values = { "value1", "value2" };
        RequestParameter param = new RequestParameter(name, values);

        String expected = "[testParam, [value1, value2]]";
        assertEquals(expected, param.toString());
    }

    @Test
    public void test_toString_withNullName() {
        // Test toString with null name
        String[] values = { "value1" };
        RequestParameter param = new RequestParameter(null, values);

        String expected = "[null, [value1]]";
        assertEquals(expected, param.toString());
    }

    @Test
    public void test_toString_withNullValues() {
        // Test toString with null values
        String name = "testParam";
        RequestParameter param = new RequestParameter(name, null);

        String expected = "[testParam, null]";
        assertEquals(expected, param.toString());
    }

    @Test
    public void test_toString_withEmptyValues() {
        // Test toString with empty values
        String name = "emptyParam";
        String[] values = {};
        RequestParameter param = new RequestParameter(name, values);

        String expected = "[emptyParam, []]";
        assertEquals(expected, param.toString());
    }

    @Test
    public void test_toString_withNullInValues() {
        // Test toString with null elements in values
        String name = "mixedParam";
        String[] values = { "value1", null, "value3" };
        RequestParameter param = new RequestParameter(name, values);

        String expected = "[mixedParam, [value1, null, value3]]";
        assertEquals(expected, param.toString());
    }

    @Test
    public void test_toString_withSpecialCharacters() {
        // Test toString with special characters
        String name = "special[]param";
        String[] values = { "[value]", ",comma,", "quote\"test" };
        RequestParameter param = new RequestParameter(name, values);

        String expected = "[special[]param, [[value], ,comma,, quote\"test]]";
        assertEquals(expected, param.toString());
    }

    @Test
    public void test_immutability() {
        // Test that modifying the original values array doesn't affect the parameter
        String name = "immutableParam";
        String[] originalValues = { "original1", "original2" };
        RequestParameter param = new RequestParameter(name, originalValues);

        // Get values and verify they match
        String[] retrievedValues = param.getValues();
        assertEquals("original1", retrievedValues[0]);
        assertEquals("original2", retrievedValues[1]);

        // Modify retrieved array
        retrievedValues[0] = "modified1";

        // Verify the parameter's values can still be modified (not deeply immutable)
        assertEquals("modified1", param.getValues()[0]);

        // This shows that the class returns the same array reference
        assertSame(retrievedValues, param.getValues());
    }

    @Test
    public void test_largeValueArray() {
        // Test with large array of values
        String name = "largeParam";
        String[] values = new String[1000];
        for (int i = 0; i < values.length; i++) {
            values[i] = "value" + i;
        }
        RequestParameter param = new RequestParameter(name, values);

        assertEquals(name, param.getName());
        assertNotNull(param.getValues());
        assertEquals(1000, param.getValues().length);
        assertEquals("value0", param.getValues()[0]);
        assertEquals("value999", param.getValues()[999]);
    }

    @Test
    public void test_veryLongStrings() {
        // Test with very long strings
        StringBuilder longNameBuilder = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longNameBuilder.append("a");
        }
        String longName = longNameBuilder.toString();

        StringBuilder longValueBuilder = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longValueBuilder.append("b");
        }
        String longValue = longValueBuilder.toString();

        String[] values = { longValue };
        RequestParameter param = new RequestParameter(longName, values);

        assertEquals(longName, param.getName());
        assertNotNull(param.getValues());
        assertEquals(1, param.getValues().length);
        assertEquals(longValue, param.getValues()[0]);
    }

    @Test
    public void test_getName() {
        // Test getName method explicitly
        String name = "getName";
        String[] values = { "test" };
        RequestParameter param = new RequestParameter(name, values);

        assertEquals(name, param.getName());
        // Call multiple times to ensure consistency
        assertEquals(name, param.getName());
        assertEquals(name, param.getName());
    }

    @Test
    public void test_getValues() {
        // Test getValues method explicitly
        String name = "getValues";
        String[] values = { "val1", "val2", "val3" };
        RequestParameter param = new RequestParameter(name, values);

        String[] retrievedValues = param.getValues();
        assertNotNull(retrievedValues);
        assertEquals(3, retrievedValues.length);
        assertEquals("val1", retrievedValues[0]);
        assertEquals("val2", retrievedValues[1]);
        assertEquals("val3", retrievedValues[2]);

        // Call multiple times to ensure consistency
        assertSame(retrievedValues, param.getValues());
        assertSame(param.getValues(), param.getValues());
    }
}