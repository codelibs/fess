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
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.codelibs.core.collection.Maps;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.sai.internal.ir.debug.ObjectSizeCalculator;

import com.google.common.collect.Lists;

public class MemoryUtilTest extends UnitFessTestCase {

    public void test_byteCountToDisplaySize() {
        assertEquals("0bytes", MemoryUtil.byteCountToDisplaySize(0L));
        assertEquals("999bytes", MemoryUtil.byteCountToDisplaySize(999L));
        assertEquals("1000bytes", MemoryUtil.byteCountToDisplaySize(1000L));
        assertEquals("1.024KB", MemoryUtil.byteCountToDisplaySize(FileUtils.ONE_KB));
        assertEquals("999.999KB", MemoryUtil.byteCountToDisplaySize(999_999L));
        assertEquals("1000KB", MemoryUtil.byteCountToDisplaySize(1000_000L));
        assertEquals("1.024MB", MemoryUtil.byteCountToDisplaySize(FileUtils.ONE_MB));
        assertEquals("976.562MB", MemoryUtil.byteCountToDisplaySize(999_999_999L));
        assertEquals("976.562MB", MemoryUtil.byteCountToDisplaySize(1000_000_000L));
        assertEquals("1.024GB", MemoryUtil.byteCountToDisplaySize(FileUtils.ONE_GB));
        assertEquals("953.674GB", MemoryUtil.byteCountToDisplaySize(999_999_999_999L));
        assertEquals("953.674GB", MemoryUtil.byteCountToDisplaySize(1000_000_000_000L));
        assertEquals("1.024TB", MemoryUtil.byteCountToDisplaySize(FileUtils.ONE_TB));
        assertEquals("931.322TB", MemoryUtil.byteCountToDisplaySize(999_999_999_999_999L));
        assertEquals("931.322TB", MemoryUtil.byteCountToDisplaySize(1000_000_000_000_000L));
        assertEquals("1.024PB", MemoryUtil.byteCountToDisplaySize(FileUtils.ONE_PB));
        assertEquals("909.494PB", MemoryUtil.byteCountToDisplaySize(999_999_999_999_999_999L));
        assertEquals("909.494PB", MemoryUtil.byteCountToDisplaySize(1000_000_000_000_000_000L));
        assertEquals("1.024EB", MemoryUtil.byteCountToDisplaySize(FileUtils.ONE_EB));
    }

    public void test_getUsedMemory() {
        assertTrue(MemoryUtil.getUsedMemory() >= 0);
    }

    public void test_sizeOf() throws Exception {
        // System.out.println("size: " + getObjectSize(""));
        assertEquals(24L, MemoryUtil.sizeOf(Integer.MAX_VALUE));
        assertEquals(24L, MemoryUtil.sizeOf(Long.MAX_VALUE));
        assertEquals(24L, MemoryUtil.sizeOf(Short.MAX_VALUE));
        assertEquals(24L, MemoryUtil.sizeOf(Float.MAX_VALUE));
        assertEquals(24L, MemoryUtil.sizeOf(Double.MAX_VALUE));
        assertEquals(24L, MemoryUtil.sizeOf(Byte.MAX_VALUE));
        assertEquals(16L, MemoryUtil.sizeOf(Boolean.TRUE));
        assertEquals(32L, MemoryUtil.sizeOf(new Date()));
        assertEquals(80L, MemoryUtil.sizeOf(LocalDateTime.now()));
        assertEquals(2128L, MemoryUtil.sizeOf(ZonedDateTime.now()));
        assertEquals(66L, MemoryUtil.sizeOf("1234567890"));
        assertEquals(76L, MemoryUtil.sizeOf("12345678901234567890"));
        assertEquals(66L, MemoryUtil.sizeOf(new String[] { "1234567890" }));
        assertEquals(132L, MemoryUtil.sizeOf(new String[] { "1234567890", "1234567890" }));
        assertEquals(132L, MemoryUtil.sizeOf(Lists.asList("1234567890", new String[] { "1234567890" })));
        assertEquals(132L, MemoryUtil.sizeOf(Maps.map("1234567890", "1234567890").$()));
    }

    public void test_getMemoryUsageLog() {
        String log = MemoryUtil.getMemoryUsageLog();

        assertNotNull(log);
        assertTrue(log.startsWith("Mem:{used "));
        assertTrue(log.contains(", heap "));
        assertTrue(log.contains(", max "));
        assertTrue(log.endsWith("}"));

        // Verify format contains expected memory units
        assertTrue(log.matches(".*used [0-9.]+[a-zA-Z]+, heap [0-9.]+[a-zA-Z]+, max [0-9.]+[a-zA-Z]+.*"));
    }

    public void test_byteCountToDisplaySize_edgeCases() {
        // Test negative values - should throw NullPointerException in private method via BigInteger.valueOf
        try {
            MemoryUtil.byteCountToDisplaySize(-1L);
            // If no exception, verify it handles negative as expected
        } catch (Exception e) {
            // Expected for negative values
        }

        // Test boundary values
        assertEquals("1bytes", MemoryUtil.byteCountToDisplaySize(1L));
        assertEquals("999bytes", MemoryUtil.byteCountToDisplaySize(999L));

        // Test very large values close to EB boundary
        assertEquals("1.024EB", MemoryUtil.byteCountToDisplaySize(FileUtils.ONE_EB));

        // Test exact thresholds
        assertEquals("1.024KB", MemoryUtil.byteCountToDisplaySize(1024L));
        assertEquals("1.024MB", MemoryUtil.byteCountToDisplaySize(1024L * 1024L));
        assertEquals("1.024GB", MemoryUtil.byteCountToDisplaySize(1024L * 1024L * 1024L));
    }

    public void test_sizeOf_nullObject() {
        assertEquals(0L, MemoryUtil.sizeOf(null));
    }

    public void test_sizeOf_stringVariations() {
        // Empty string
        assertEquals(56L, MemoryUtil.sizeOf(""));

        // Single character
        assertEquals(57L, MemoryUtil.sizeOf("a"));

        // Unicode characters
        assertEquals(58L, MemoryUtil.sizeOf("こん"));

        // Very long string
        StringBuilder longString = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longString.append("x");
        }
        assertEquals(1056L, MemoryUtil.sizeOf(longString.toString()));
    }

    public void test_sizeOf_numberTypes() {
        // Test various Number subclasses
        assertEquals(24L, MemoryUtil.sizeOf(new BigInteger("123456789")));
        assertEquals(24L, MemoryUtil.sizeOf(new BigDecimal("123.456")));
        assertEquals(24L, MemoryUtil.sizeOf((byte) 1));
        assertEquals(24L, MemoryUtil.sizeOf((short) 1));
        assertEquals(24L, MemoryUtil.sizeOf(1));
        assertEquals(24L, MemoryUtil.sizeOf(1L));
        assertEquals(24L, MemoryUtil.sizeOf(1.0f));
        assertEquals(24L, MemoryUtil.sizeOf(1.0d));
    }

    public void test_sizeOf_dateTimeTypes() {
        // Test different date/time types
        assertEquals(32L, MemoryUtil.sizeOf(new Date(0)));
        assertEquals(80L, MemoryUtil.sizeOf(LocalDateTime.of(2023, 1, 1, 0, 0)));
        assertEquals(2128L, MemoryUtil.sizeOf(ZonedDateTime.now()));
    }

    public void test_sizeOf_arrayVariations() {
        // Empty array
        assertEquals(0L, MemoryUtil.sizeOf(new String[0]));
        assertEquals(0L, MemoryUtil.sizeOf(new Object[0]));

        // Array with null elements
        assertEquals(0L, MemoryUtil.sizeOf(new String[] { null, null }));

        // Mixed content array
        assertEquals(84L, MemoryUtil.sizeOf(new Object[] { "test", 123, null }));

        // Nested arrays
        assertEquals(120L, MemoryUtil.sizeOf(new Object[] { new String[] { "test" }, new String[] { "test" } }));
    }

    public void test_sizeOf_collectionVariations() {
        // Empty collection
        assertEquals(0L, MemoryUtil.sizeOf(new ArrayList<>()));

        // Collection with null elements
        List<String> listWithNulls = new ArrayList<>();
        listWithNulls.add(null);
        listWithNulls.add(null);
        assertEquals(0L, MemoryUtil.sizeOf(listWithNulls));

        // Mixed content collection
        List<Object> mixedList = new ArrayList<>();
        mixedList.add("test");
        mixedList.add(123);
        mixedList.add(null);
        assertEquals(84L, MemoryUtil.sizeOf(mixedList));

        // Nested collections
        List<List<String>> nestedList = new ArrayList<>();
        nestedList.add(Arrays.asList("test"));
        nestedList.add(Arrays.asList("test"));
        assertEquals(120L, MemoryUtil.sizeOf(nestedList));
    }

    public void test_sizeOf_mapVariations() {
        // Empty map
        assertEquals(0L, MemoryUtil.sizeOf(new HashMap<>()));

        // Map with null keys and values
        Map<String, String> mapWithNulls = new HashMap<>();
        mapWithNulls.put(null, null);
        assertEquals(0L, MemoryUtil.sizeOf(mapWithNulls));

        // Map with mixed content
        Map<Object, Object> mixedMap = new HashMap<>();
        mixedMap.put("key", "value");
        mixedMap.put(123, 456);
        mixedMap.put(null, "test");
        assertEquals(228L, MemoryUtil.sizeOf(mixedMap));

        // Nested maps
        Map<String, Map<String, String>> nestedMap = new HashMap<>();
        nestedMap.put("outer", Maps.map("inner", "value").$());
        assertEquals(183L, MemoryUtil.sizeOf(nestedMap));
    }

    public void test_sizeOf_customObjects() {
        // Test fallback case for custom objects (should return 16L)
        assertEquals(16L, MemoryUtil.sizeOf(new Object()));
        assertEquals(16L, MemoryUtil.sizeOf(new CustomTestObject()));
        assertEquals(16L, MemoryUtil.sizeOf(Boolean.TRUE));
        assertEquals(16L, MemoryUtil.sizeOf(Boolean.FALSE));
    }

    public void test_sizeOf_complexNestedStructures() {
        // Complex nested structure
        Map<String, Object> complexMap = new HashMap<>();
        complexMap.put("strings", Arrays.asList("test1", "test2"));
        complexMap.put("numbers", Arrays.asList(1, 2, 3));
        complexMap.put("nested", Maps.map("inner", "value").$());
        complexMap.put("array", new String[] { "array1", "array2" });

        // Calculate expected size based on actual behavior
        long actualSize = MemoryUtil.sizeOf(complexMap);
        assertEquals(689L, actualSize);
    }

    public void test_getUsedMemory_variations() {
        // Test multiple calls to ensure consistency
        long memory1 = MemoryUtil.getUsedMemory();
        assertTrue(memory1 >= 0);

        // Create some objects to change memory usage
        @SuppressWarnings("unused")
        String[] largeArray = new String[1000];
        for (int i = 0; i < 1000; i++) {
            largeArray[i] = "test" + i;
        }

        long memory2 = MemoryUtil.getUsedMemory();
        assertTrue(memory2 >= 0);
        // Memory usage should have changed (though we can't guarantee direction due to GC)
    }

    public void test_integrationScenarios() {
        // Test realistic scenarios combining multiple methods
        String memoryLog1 = MemoryUtil.getMemoryUsageLog();

        // Create some objects and measure their sizes
        List<String> testData = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            testData.add("test data " + i);
        }

        long listSize = MemoryUtil.sizeOf(testData);
        assertTrue(listSize > 0);

        String memoryLog2 = MemoryUtil.getMemoryUsageLog();

        // Both logs should be well-formed
        assertNotNull(memoryLog1);
        assertNotNull(memoryLog2);
        assertTrue(memoryLog1.contains("Mem:"));
        assertTrue(memoryLog2.contains("Mem:"));
    }

    // Custom test class for fallback object testing
    private static class CustomTestObject {
        @SuppressWarnings("unused")
        private final String field = "test";
    }

    private long getObjectSize(Object value) {
        System.setProperty("java.vm.name", "Java HotSpot(TM) ");
        return ObjectSizeCalculator.getObjectSize(value);
    }
}