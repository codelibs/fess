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

public class FessSystemExceptionTest extends UnitFessTestCase {

    public void test_constructor_withMessage() {
        // Test constructor with message only
        String message = "Test exception message";
        FessSystemException exception = new FessSystemException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withMessageAndCause() {
        // Test constructor with message and cause
        String message = "Test exception message with cause";
        Throwable cause = new RuntimeException("Root cause");
        FessSystemException exception = new FessSystemException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    public void test_constructor_withCause() {
        // Test constructor with cause only
        Throwable cause = new IllegalArgumentException("Cause exception");
        FessSystemException exception = new FessSystemException(cause);

        assertEquals(cause, exception.getCause());
        // When constructed with cause only, message contains the cause's toString()
        assertTrue(exception.getMessage().contains(cause.getClass().getName()));
        assertTrue(exception.getMessage().contains("Cause exception"));
    }

    public void test_constructor_withNullMessage() {
        // Test constructor with null message
        FessSystemException exception = new FessSystemException((String) null);

        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withNullCause() {
        // Test constructor with null cause
        String message = "Test with null cause";
        FessSystemException exception = new FessSystemException(message, null);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withNullMessageAndCause() {
        // Test constructor with both null message and cause
        FessSystemException exception = new FessSystemException(null, null);

        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withCauseOnly_nullCause() {
        // Test constructor with null cause only
        FessSystemException exception = new FessSystemException((Throwable) null);

        assertNull(exception.getCause());
        assertNull(exception.getMessage());
    }

    public void test_constructor_withSuppression() {
        // Test protected constructor with suppression enabled
        String message = "Test with suppression";
        TestFessSystemException exception = new TestFessSystemException(message, true, true);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());

        // Test that suppression is enabled
        RuntimeException suppressed = new RuntimeException("Suppressed exception");
        exception.addSuppressed(suppressed);
        assertEquals(1, exception.getSuppressed().length);
        assertEquals(suppressed, exception.getSuppressed()[0]);
    }

    public void test_constructor_withSuppressionDisabled() {
        // Test protected constructor with suppression disabled
        String message = "Test without suppression";
        TestFessSystemException exception = new TestFessSystemException(message, false, true);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());

        // Test that suppression is disabled
        RuntimeException suppressed = new RuntimeException("Suppressed exception");
        exception.addSuppressed(suppressed);
        assertEquals(0, exception.getSuppressed().length);
    }

    public void test_constructor_withWritableStackTrace() {
        // Test protected constructor with writable stack trace
        String message = "Test with writable stack trace";
        TestFessSystemException exception = new TestFessSystemException(message, true, true);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());

        // Stack trace should be populated
        assertTrue(exception.getStackTrace().length > 0);
    }

    public void test_constructor_withNonWritableStackTrace() {
        // Test protected constructor with non-writable stack trace
        String message = "Test without writable stack trace";
        TestFessSystemException exception = new TestFessSystemException(message, true, false);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());

        // Stack trace should be empty
        assertEquals(0, exception.getStackTrace().length);
    }

    public void test_exceptionChaining() {
        // Test exception chaining with multiple levels
        Exception rootCause = new Exception("Root cause");
        RuntimeException middleCause = new RuntimeException("Middle cause", rootCause);
        FessSystemException topException = new FessSystemException("Top level", middleCause);

        assertEquals("Top level", topException.getMessage());
        assertEquals(middleCause, topException.getCause());
        assertEquals(rootCause, topException.getCause().getCause());
    }

    public void test_serialVersionUID() {
        // Test that serialVersionUID is properly defined
        FessSystemException exception1 = new FessSystemException("Test");
        FessSystemException exception2 = new FessSystemException("Test");

        // Both instances should be of the same class
        assertEquals(exception1.getClass(), exception2.getClass());
    }

    public void test_throwAndCatch() {
        // Test throwing and catching the exception
        String expectedMessage = "Expected error";
        try {
            throw new FessSystemException(expectedMessage);
        } catch (FessSystemException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertNull(e.getCause());
        }
    }

    public void test_throwAndCatchWithCause() {
        // Test throwing and catching the exception with cause
        String expectedMessage = "System error occurred";
        Exception expectedCause = new IllegalStateException("Invalid state");

        try {
            throw new FessSystemException(expectedMessage, expectedCause);
        } catch (FessSystemException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedCause, e.getCause());
        }
    }

    // Test helper class to access protected constructor
    private static class TestFessSystemException extends FessSystemException {
        public TestFessSystemException(String message, boolean enableSuppression, boolean writableStackTrace) {
            super(message, enableSuppression, writableStackTrace);
        }
    }
}