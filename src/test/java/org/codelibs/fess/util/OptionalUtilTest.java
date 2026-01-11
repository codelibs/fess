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
package org.codelibs.fess.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.optional.OptionalEntity;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

public class OptionalUtilTest extends UnitFessTestCase {

    @Test
    public void test_ofNullable_withNonNullString() {
        String testValue = "test string";
        OptionalEntity<String> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(testValue, result.get());
    }

    @Test
    public void test_ofNullable_withNullString() {
        String testValue = null;
        OptionalEntity<String> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    public void test_ofNullable_withEmptyString() {
        String testValue = "";
        OptionalEntity<String> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals("", result.get());
    }

    @Test
    public void test_ofNullable_withInteger() {
        Integer testValue = 42;
        OptionalEntity<Integer> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(Integer.valueOf(42), result.get());
    }

    @Test
    public void test_ofNullable_withNullInteger() {
        Integer testValue = null;
        OptionalEntity<Integer> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    public void test_ofNullable_withZeroInteger() {
        Integer testValue = 0;
        OptionalEntity<Integer> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(Integer.valueOf(0), result.get());
    }

    @Test
    public void test_ofNullable_withBoolean() {
        Boolean testValue = true;
        OptionalEntity<Boolean> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(Boolean.TRUE, result.get());
    }

    @Test
    public void test_ofNullable_withFalseBoolean() {
        Boolean testValue = false;
        OptionalEntity<Boolean> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(Boolean.FALSE, result.get());
    }

    @Test
    public void test_ofNullable_withNullBoolean() {
        Boolean testValue = null;
        OptionalEntity<Boolean> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    public void test_ofNullable_withDate() {
        Date testValue = new Date();
        OptionalEntity<Date> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(testValue, result.get());
    }

    @Test
    public void test_ofNullable_withNullDate() {
        Date testValue = null;
        OptionalEntity<Date> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    public void test_ofNullable_withLocalDateTime() {
        LocalDateTime testValue = LocalDateTime.now();
        OptionalEntity<LocalDateTime> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(testValue, result.get());
    }

    @Test
    public void test_ofNullable_withBigDecimal() {
        BigDecimal testValue = new BigDecimal("123.456");
        OptionalEntity<BigDecimal> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(testValue, result.get());
    }

    @Test
    public void test_ofNullable_withList() {
        List<String> testValue = Arrays.asList("item1", "item2", "item3");
        OptionalEntity<List<String>> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(testValue, result.get());
        assertEquals(3, result.get().size());
    }

    @Test
    public void test_ofNullable_withEmptyList() {
        List<String> testValue = new ArrayList<>();
        OptionalEntity<List<String>> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(testValue, result.get());
        assertEquals(0, result.get().size());
    }

    @Test
    public void test_ofNullable_withNullList() {
        List<String> testValue = null;
        OptionalEntity<List<String>> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    public void test_ofNullable_withMap() {
        Map<String, Object> testValue = new HashMap<>();
        testValue.put("key1", "value1");
        testValue.put("key2", 42);

        OptionalEntity<Map<String, Object>> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(testValue, result.get());
        assertEquals(2, result.get().size());
    }

    @Test
    public void test_ofNullable_withEmptyMap() {
        Map<String, Object> testValue = new HashMap<>();
        OptionalEntity<Map<String, Object>> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(testValue, result.get());
        assertEquals(0, result.get().size());
    }

    @Test
    public void test_ofNullable_withNullMap() {
        Map<String, Object> testValue = null;
        OptionalEntity<Map<String, Object>> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    public void test_ofNullable_withArray() {
        String[] testValue = { "item1", "item2", "item3" };
        OptionalEntity<String[]> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertSame(testValue, result.get());
        assertEquals(3, result.get().length);
    }

    @Test
    public void test_ofNullable_withEmptyArray() {
        String[] testValue = new String[0];
        OptionalEntity<String[]> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertSame(testValue, result.get());
        assertEquals(0, result.get().length);
    }

    @Test
    public void test_ofNullable_withNullArray() {
        String[] testValue = null;
        OptionalEntity<String[]> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    public void test_ofNullable_withCustomObject() {
        CustomTestObject testValue = new CustomTestObject("test", 123);
        OptionalEntity<CustomTestObject> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(testValue, result.get());
        assertEquals("test", result.get().getName());
        assertEquals(123, result.get().getValue());
    }

    @Test
    public void test_ofNullable_withNullCustomObject() {
        CustomTestObject testValue = null;
        OptionalEntity<CustomTestObject> result = OptionalUtil.ofNullable(testValue);

        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    public void test_ofNullable_optionalEntityBehavior() {
        // Test with present value
        String presentValue = "present";
        OptionalEntity<String> presentOptional = OptionalUtil.ofNullable(presentValue);

        assertTrue(presentOptional.isPresent());
        assertEquals("present", presentOptional.get());
        assertEquals("present", presentOptional.orElse("default"));

        // Test with null value
        String nullValue = null;
        OptionalEntity<String> emptyOptional = OptionalUtil.ofNullable(nullValue);

        assertFalse(emptyOptional.isPresent());
        assertEquals("default", emptyOptional.orElse("default"));

        // Test that empty optional returns null instead of throwing
        assertNull(emptyOptional.orElse(null));
    }

    @Test
    public void test_ofNullable_multipleTypesInSequence() {
        // Test multiple different types in sequence to ensure type safety
        OptionalEntity<String> stringOpt = OptionalUtil.ofNullable("string");
        OptionalEntity<Integer> intOpt = OptionalUtil.ofNullable(42);
        OptionalEntity<Boolean> boolOpt = OptionalUtil.ofNullable(true);
        OptionalEntity<Date> dateOpt = OptionalUtil.ofNullable(new Date());

        assertTrue(stringOpt.isPresent());
        assertTrue(intOpt.isPresent());
        assertTrue(boolOpt.isPresent());
        assertTrue(dateOpt.isPresent());

        assertEquals("string", stringOpt.get());
        assertEquals(Integer.valueOf(42), intOpt.get());
        assertEquals(Boolean.TRUE, boolOpt.get());
        assertNotNull(dateOpt.get());
    }

    @Test
    public void test_ofNullable_chainedOperations() {
        // Test chaining operations on the returned OptionalEntity
        String testValue = "HELLO WORLD";
        OptionalEntity<String> result = OptionalUtil.ofNullable(testValue);

        assertTrue(result.isPresent());

        // Test that we can chain operations
        String processed = result.map(s -> s.toLowerCase()).orElse("default");

        assertEquals("hello world", processed);

        // Test with null value
        String nullValue = null;
        OptionalEntity<String> emptyResult = OptionalUtil.ofNullable(nullValue);

        String processedEmpty = emptyResult.map(s -> s.toLowerCase()).orElse("default");

        assertEquals("default", processedEmpty);
    }

    @Test
    public void test_ofNullable_performanceAndMemory() {
        // Test creating many OptionalEntity instances
        for (int i = 0; i < 1000; i++) {
            OptionalEntity<Integer> opt = OptionalUtil.ofNullable(i);
            assertTrue(opt.isPresent());
            assertEquals(Integer.valueOf(i), opt.get());
        }

        // Test creating many empty OptionalEntity instances
        for (int i = 0; i < 1000; i++) {
            OptionalEntity<Integer> opt = OptionalUtil.ofNullable((Integer) null);
            assertFalse(opt.isPresent());
        }
    }

    @Test
    public void test_ofNullable_edgeCaseValues() {
        // Test with special numeric values
        OptionalEntity<Double> nanOpt = OptionalUtil.ofNullable(Double.NaN);
        assertTrue(nanOpt.isPresent());
        assertTrue(Double.isNaN(nanOpt.get()));

        OptionalEntity<Double> infinityOpt = OptionalUtil.ofNullable(Double.POSITIVE_INFINITY);
        assertTrue(infinityOpt.isPresent());
        assertTrue(Double.isInfinite(infinityOpt.get()));

        OptionalEntity<Float> negInfinityOpt = OptionalUtil.ofNullable(Float.NEGATIVE_INFINITY);
        assertTrue(negInfinityOpt.isPresent());
        assertTrue(Float.isInfinite(negInfinityOpt.get()));

        // Test with very large strings
        StringBuilder largeString = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeString.append("x");
        }
        OptionalEntity<String> largeStringOpt = OptionalUtil.ofNullable(largeString.toString());
        assertTrue(largeStringOpt.isPresent());
        assertEquals(10000, largeStringOpt.get().length());
    }

    // Custom test class for object testing
    private static class CustomTestObject {
        private final String name;
        private final int value;

        public CustomTestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            CustomTestObject other = (CustomTestObject) obj;
            return value == other.value && (name != null ? name.equals(other.name) : other.name == null);
        }

        @Override
        public int hashCode() {
            return (name != null ? name.hashCode() : 0) * 31 + value;
        }
    }
}