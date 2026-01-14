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
package org.codelibs.fess.timer;

import java.io.IOException;
import java.util.Date;
import java.util.function.Supplier;

import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.taglib.FessFunctions;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class MonitorTargetTest extends UnitFessTestCase {

    private TestMonitorTarget monitorTarget;
    private SystemHelper systemHelper;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        monitorTarget = new TestMonitorTarget();

        // Setup SystemHelper with a fixed timestamp for testing
        systemHelper = new SystemHelper() {
            @Override
            public Date getCurrentTime() {
                return new Date(1609459200000L); // 2021-01-01 00:00:00 UTC
            }
        };
        ComponentUtil.register(systemHelper, "systemHelper");
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        super.tearDown(testInfo);
    }

    // Test append method with null value
    @Test
    public void test_append_nullValue() {
        StringBuilder buf = new StringBuilder();
        Supplier<Object> supplier = () -> null;

        monitorTarget.append(buf, "testKey", supplier);
        assertEquals("\"testKey\":null", buf.toString());
    }

    // Test append method with Integer value
    @Test
    public void test_append_integerValue() {
        StringBuilder buf = new StringBuilder();
        Supplier<Object> supplier = () -> Integer.valueOf(123);

        monitorTarget.append(buf, "intKey", supplier);
        assertEquals("\"intKey\":123", buf.toString());
    }

    // Test append method with Long value
    @Test
    public void test_append_longValue() {
        StringBuilder buf = new StringBuilder();
        Supplier<Object> supplier = () -> Long.valueOf(9876543210L);

        monitorTarget.append(buf, "longKey", supplier);
        assertEquals("\"longKey\":9876543210", buf.toString());
    }

    // Test append method with Short value
    @Test
    public void test_append_shortValue() {
        StringBuilder buf = new StringBuilder();
        Supplier<Object> supplier = () -> Short.valueOf((short) 42);

        monitorTarget.append(buf, "shortKey", supplier);
        assertEquals("\"shortKey\":42", buf.toString());
    }

    // Test append method with double array
    @Test
    public void test_append_doubleArray() {
        StringBuilder buf = new StringBuilder();
        double[] values = { 1.5, 2.3, 3.7 };
        Supplier<Object> supplier = () -> values;

        monitorTarget.append(buf, "arrayKey", supplier);
        assertEquals("\"arrayKey\":[1.5, 2.3, 3.7]", buf.toString());
    }

    // Test append method with empty double array
    @Test
    public void test_append_emptyDoubleArray() {
        StringBuilder buf = new StringBuilder();
        double[] values = {};
        Supplier<Object> supplier = () -> values;

        monitorTarget.append(buf, "emptyArrayKey", supplier);
        assertEquals("\"emptyArrayKey\":[]", buf.toString());
    }

    // Test append method with String value
    @Test
    public void test_append_stringValue() {
        StringBuilder buf = new StringBuilder();
        Supplier<Object> supplier = () -> "test string";

        monitorTarget.append(buf, "stringKey", supplier);
        assertEquals("\"stringKey\":\"test string\"", buf.toString());
    }

    // Test append method with String containing special characters
    @Test
    public void test_append_stringWithSpecialChars() {
        StringBuilder buf = new StringBuilder();
        Supplier<Object> supplier = () -> "test \"quotes\" and \n newline";

        monitorTarget.append(buf, "specialKey", supplier);
        assertEquals("\"specialKey\":\"test \\\"quotes\\\" and \\n newline\"", buf.toString());
    }

    // Test append method with String containing backslash
    @Test
    public void test_append_stringWithBackslash() {
        StringBuilder buf = new StringBuilder();
        Supplier<Object> supplier = () -> "path\\to\\file";

        monitorTarget.append(buf, "pathKey", supplier);
        assertEquals("\"pathKey\":\"path\\\\to\\\\file\"", buf.toString());
    }

    // Test append method with exception in supplier
    @Test
    public void test_append_exceptionInSupplier() {
        StringBuilder buf = new StringBuilder();
        Supplier<Object> supplier = () -> {
            throw new RuntimeException("Test exception");
        };

        monitorTarget.append(buf, "errorKey", supplier);
        assertEquals("\"errorKey\":null", buf.toString());
    }

    // Test append method with Boolean value (other object type)
    @Test
    public void test_append_booleanValue() {
        StringBuilder buf = new StringBuilder();
        Supplier<Object> supplier = () -> Boolean.TRUE;

        monitorTarget.append(buf, "boolKey", supplier);
        assertEquals("\"boolKey\":\"true\"", buf.toString());
    }

    // Test append method with Date value (other object type)
    @Test
    public void test_append_dateValue() {
        StringBuilder buf = new StringBuilder();
        Date date = new Date(1609459200000L);
        Supplier<Object> supplier = () -> date;

        monitorTarget.append(buf, "dateKey", supplier);
        String result = buf.toString();
        assertTrue(result.startsWith("\"dateKey\":\""));
        // Check that the date was converted to string and properly escaped
        assertTrue(result.contains("2021") || result.contains("Jan") || result.contains("1609459200000"));
    }

    // Test appendTimestamp method
    @Test
    public void test_appendTimestamp() {
        StringBuilder buf = new StringBuilder();

        monitorTarget.appendTimestamp(buf);
        String result = buf.toString();
        assertTrue(result.startsWith("\"timestamp\":\""));
        assertTrue(result.endsWith("\""));
        // Verify a timestamp was properly formatted
        assertNotNull(result);
        assertTrue(result.length() > "\"timestamp\":\"\"".length());
    }

    // Test appendException method with simple exception
    @Test
    public void test_appendException_simpleException() {
        StringBuilder buf = new StringBuilder();
        Exception exception = new RuntimeException("Test error message");

        monitorTarget.appendException(buf, exception);
        assertTrue(buf.toString().startsWith("\"exception\":\""));
        assertTrue(buf.toString().contains("Test error message"));
        assertTrue(buf.toString().contains("RuntimeException"));
    }

    // Test appendException method with nested exception
    @Test
    public void test_appendException_nestedException() {
        StringBuilder buf = new StringBuilder();
        Exception cause = new IllegalArgumentException("Root cause");
        Exception exception = new RuntimeException("Wrapper exception", cause);

        monitorTarget.appendException(buf, exception);
        assertTrue(buf.toString().contains("Wrapper exception"));
        assertTrue(buf.toString().contains("Root cause"));
        assertTrue(buf.toString().contains("IllegalArgumentException"));
    }

    // Test appendException method with IOException during processing
    @Test
    public void test_appendException_ioExceptionDuringProcessing() {
        StringBuilder buf = new StringBuilder();
        // Create an exception that might cause issues during processing
        Exception exception = new Exception() {
            @Override
            public void printStackTrace(java.io.PrintWriter writer) {
                // Simulate a normal stack trace output
                writer.println("Special test exception");
                writer.flush();
            }
        };

        monitorTarget.appendException(buf, exception);
        assertTrue(buf.toString().contains("Special test exception"));
    }

    // Test multiple appends to the same buffer
    @Test
    public void test_multipleAppends() {
        StringBuilder buf = new StringBuilder();

        monitorTarget.append(buf, "key1", () -> "value1");
        buf.append(",");
        monitorTarget.append(buf, "key2", () -> 123);
        buf.append(",");
        monitorTarget.append(buf, "key3", () -> null);

        assertEquals("\"key1\":\"value1\",\"key2\":123,\"key3\":null", buf.toString());
    }

    // Test append with empty string
    @Test
    public void test_append_emptyString() {
        StringBuilder buf = new StringBuilder();
        Supplier<Object> supplier = () -> "";

        monitorTarget.append(buf, "emptyKey", supplier);
        assertEquals("\"emptyKey\":\"\"", buf.toString());
    }

    // Test append with very long string
    @Test
    public void test_append_veryLongString() {
        StringBuilder buf = new StringBuilder();
        String longString = "a".repeat(10000);
        Supplier<Object> supplier = () -> longString;

        monitorTarget.append(buf, "longKey", supplier);
        assertTrue(buf.toString().startsWith("\"longKey\":\""));
        assertTrue(buf.toString().contains("aaaaaaaaaa"));
        assertEquals(10000 + "\"longKey\":\"\"".length(), buf.toString().length());
    }

    // Test append with Unicode characters
    @Test
    public void test_append_unicodeCharacters() {
        StringBuilder buf = new StringBuilder();
        Supplier<Object> supplier = () -> "テスト 测试 테스트 🎉";

        monitorTarget.append(buf, "unicodeKey", supplier);
        // Unicode characters are escaped in JSON format
        assertEquals("\"unicodeKey\":\"\\u30C6\\u30B9\\u30C8 \\u6D4B\\u8BD5 \\uD14C\\uC2A4\\uD2B8 \\uD83C\\uDF89\"", buf.toString());
    }

    // Test append returns the same buffer instance
    @Test
    public void test_append_returnsSameBuffer() {
        StringBuilder buf = new StringBuilder();
        Supplier<Object> supplier = () -> "test";

        StringBuilder result = monitorTarget.append(buf, "key", supplier);
        assertSame(buf, result);
    }

    // Test appendTimestamp returns the same buffer instance
    @Test
    public void test_appendTimestamp_returnsSameBuffer() {
        StringBuilder buf = new StringBuilder();

        StringBuilder result = monitorTarget.appendTimestamp(buf);
        assertSame(buf, result);
    }

    // Test appendException returns the same buffer instance
    @Test
    public void test_appendException_returnsSameBuffer() {
        StringBuilder buf = new StringBuilder();
        Exception exception = new RuntimeException("test");

        StringBuilder result = monitorTarget.appendException(buf, exception);
        assertSame(buf, result);
    }

    // Concrete implementation of MonitorTarget for testing
    private static class TestMonitorTarget extends MonitorTarget {
        @Override
        public void expired() {
            // Implementation not needed for these tests
        }
    }
}