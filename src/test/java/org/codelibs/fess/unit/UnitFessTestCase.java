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
package org.codelibs.fess.unit;

import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.utflute.lastaflute.WebContainerTestCase;
import org.junit.jupiter.api.Assertions;

public abstract class UnitFessTestCase extends WebContainerTestCase {
    @Override
    protected String prepareConfigFile() {
        return "test_app.xml";
    }

    @Override
    public void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    // JUnit 5 assertion wrapper methods

    protected static void assertEquals(Object expected, Object actual) {
        Assertions.assertEquals(expected, actual);
    }

    protected static void assertEquals(Object expected, Object actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    protected static void assertEquals(int expected, int actual) {
        Assertions.assertEquals(expected, actual);
    }

    protected static void assertEquals(long expected, long actual) {
        Assertions.assertEquals(expected, actual);
    }

    protected static void assertEquals(double expected, double actual) {
        Assertions.assertEquals(expected, actual);
    }

    protected static void assertEquals(double expected, double actual, double delta) {
        Assertions.assertEquals(expected, actual, delta);
    }

    protected static void assertEquals(float expected, float actual) {
        Assertions.assertEquals(expected, actual);
    }

    protected static void assertEquals(float expected, float actual, float delta) {
        Assertions.assertEquals(expected, actual, delta);
    }

    protected static void assertTrue(boolean condition) {
        Assertions.assertTrue(condition);
    }

    protected static void assertTrue(boolean condition, String message) {
        Assertions.assertTrue(condition, message);
    }

    protected static void assertFalse(boolean condition) {
        Assertions.assertFalse(condition);
    }

    protected static void assertFalse(boolean condition, String message) {
        Assertions.assertFalse(condition, message);
    }

    protected static void assertNull(Object actual) {
        Assertions.assertNull(actual);
    }

    protected static void assertNull(Object actual, String message) {
        Assertions.assertNull(actual, message);
    }

    protected static void assertNotNull(Object actual) {
        Assertions.assertNotNull(actual);
    }

    protected static void assertNotNull(Object actual, String message) {
        Assertions.assertNotNull(actual, message);
    }

    protected static void assertSame(Object expected, Object actual) {
        Assertions.assertSame(expected, actual);
    }

    protected static void assertSame(Object expected, Object actual, String message) {
        Assertions.assertSame(expected, actual, message);
    }

    protected static void assertNotSame(Object unexpected, Object actual) {
        Assertions.assertNotSame(unexpected, actual);
    }

    protected static void assertNotSame(Object unexpected, Object actual, String message) {
        Assertions.assertNotSame(unexpected, actual, message);
    }

    protected static void fail() {
        Assertions.fail();
    }

    protected static void fail(String message) {
        Assertions.fail(message);
    }

    protected static <T extends Throwable> T assertThrows(Class<T> expectedType, org.junit.jupiter.api.function.Executable executable) {
        return Assertions.assertThrows(expectedType, executable);
    }

    protected static void assertArrayEquals(Object[] expected, Object[] actual) {
        Assertions.assertArrayEquals(expected, actual);
    }

    protected static void assertArrayEquals(int[] expected, int[] actual) {
        Assertions.assertArrayEquals(expected, actual);
    }

    protected static void assertArrayEquals(long[] expected, long[] actual) {
        Assertions.assertArrayEquals(expected, actual);
    }

    protected static void assertArrayEquals(double[] expected, double[] actual) {
        Assertions.assertArrayEquals(expected, actual);
    }

    protected static void assertArrayEquals(byte[] expected, byte[] actual) {
        Assertions.assertArrayEquals(expected, actual);
    }
}
