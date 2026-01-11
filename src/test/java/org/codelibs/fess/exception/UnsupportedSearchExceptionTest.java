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
package org.codelibs.fess.exception;

import org.codelibs.fess.unit.UnitFessTestCase;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

public class UnsupportedSearchExceptionTest extends UnitFessTestCase {

    @Test
    public void test_constructor_withMessage() {
        // Test with normal message
        String message = "Search operation not supported";
        UnsupportedSearchException exception = new UnsupportedSearchException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withEmptyMessage() {
        // Test with empty message
        String message = "";
        UnsupportedSearchException exception = new UnsupportedSearchException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withNullMessage() {
        // Test with null message
        UnsupportedSearchException exception = new UnsupportedSearchException(null);

        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withLongMessage() {
        // Test with long message
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("a");
        }
        String message = sb.toString();
        UnsupportedSearchException exception = new UnsupportedSearchException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withSpecialCharacters() {
        // Test with special characters in message
        String message = "Search not supported: \n\t!@#$%^&*(){}[]|\\:;\"'<>,.?/~`";
        UnsupportedSearchException exception = new UnsupportedSearchException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withUnicodeCharacters() {
        // Test with Unicode characters
        String message = "検索操作はサポートされていません 🔍";
        UnsupportedSearchException exception = new UnsupportedSearchException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_instanceOf() {
        // Test inheritance hierarchy
        UnsupportedSearchException exception = new UnsupportedSearchException("test");

        assertTrue(exception instanceof UnsupportedSearchException);
        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    public void test_stackTrace() {
        // Test that stack trace is populated
        UnsupportedSearchException exception = new UnsupportedSearchException("test message");

        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);

        // Verify the stack trace contains this test method
        boolean foundTestMethod = false;
        for (StackTraceElement element : exception.getStackTrace()) {
            if (element.getMethodName().equals("test_stackTrace") && element.getClassName().equals(this.getClass().getName())) {
                foundTestMethod = true;
                break;
            }
        }
        assertTrue(foundTestMethod);
    }

    @Test
    public void test_serialization() {
        // Test that the exception has serialVersionUID
        // This test verifies the exception is serializable
        String message = "Serialization test";
        UnsupportedSearchException exception = new UnsupportedSearchException(message);

        // Verify basic properties are preserved
        assertEquals(message, exception.getMessage());

        // The presence of serialVersionUID is verified at compile time
        // and the exception extends RuntimeException which is Serializable
        assertTrue(exception instanceof java.io.Serializable);
    }

    @Test
    public void test_toString() {
        // Test toString method
        String message = "Test exception message";
        UnsupportedSearchException exception = new UnsupportedSearchException(message);

        String toStringResult = exception.toString();
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains(UnsupportedSearchException.class.getName()));
        assertTrue(toStringResult.contains(message));
    }

    @Test
    public void test_getMessage_consistency() {
        // Test that getMessage returns the same value consistently
        String message = "Consistent message";
        UnsupportedSearchException exception = new UnsupportedSearchException(message);

        String firstCall = exception.getMessage();
        String secondCall = exception.getMessage();
        String thirdCall = exception.getMessage();

        assertEquals(firstCall, secondCall);
        assertEquals(secondCall, thirdCall);
        assertEquals(message, firstCall);
    }

    @Test
    public void test_getLocalizedMessage() {
        // Test getLocalizedMessage (should be same as getMessage for this exception)
        String message = "Localized message test";
        UnsupportedSearchException exception = new UnsupportedSearchException(message);

        assertEquals(exception.getMessage(), exception.getLocalizedMessage());
    }

    @Test
    public void test_fillInStackTrace() {
        // Test fillInStackTrace method
        UnsupportedSearchException exception = new UnsupportedSearchException("Stack trace test");

        StackTraceElement[] originalStackTrace = exception.getStackTrace();
        assertNotNull(originalStackTrace);

        Throwable filled = exception.fillInStackTrace();
        assertSame(exception, filled);

        StackTraceElement[] newStackTrace = exception.getStackTrace();
        assertNotNull(newStackTrace);
    }

    @Test
    public void test_multipleInstances() {
        // Test that multiple instances are independent
        String message1 = "First exception";
        String message2 = "Second exception";

        UnsupportedSearchException exception1 = new UnsupportedSearchException(message1);
        UnsupportedSearchException exception2 = new UnsupportedSearchException(message2);

        assertNotSame(exception1, exception2);
        assertEquals(message1, exception1.getMessage());
        assertEquals(message2, exception2.getMessage());

        // Verify they have different stack traces
        assertNotSame(exception1.getStackTrace(), exception2.getStackTrace());
    }

    @Test
    public void test_throwAndCatch() {
        // Test throwing and catching the exception
        String expectedMessage = "Thrown exception";

        try {
            throw new UnsupportedSearchException(expectedMessage);
            // fail("Exception was not thrown");
        } catch (UnsupportedSearchException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertNotNull(e.getStackTrace());
        }
    }

    @Test
    public void test_throwAndCatchAsFessSystemException() {
        // Test catching as parent exception type
        String expectedMessage = "Parent catch test";

        try {
            throw new UnsupportedSearchException(expectedMessage);
            // fail("Exception was not thrown");
        } catch (FessSystemException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertTrue(e instanceof UnsupportedSearchException);
        }
    }

    @Test
    public void test_throwAndCatchAsRuntimeException() {
        // Test catching as RuntimeException
        String expectedMessage = "Runtime catch test";

        try {
            throw new UnsupportedSearchException(expectedMessage);
            // fail("Exception was not thrown");
        } catch (RuntimeException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertTrue(e instanceof UnsupportedSearchException);
        }
    }
}