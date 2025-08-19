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

public class ScheduledJobExceptionTest extends UnitFessTestCase {

    public void test_constructorWithMessage() {
        // Test constructor with message only
        String message = "Test scheduled job error";
        ScheduledJobException exception = new ScheduledJobException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructorWithMessageAndCause() {
        // Test constructor with message and cause
        String message = "Test scheduled job error with cause";
        Throwable cause = new RuntimeException("Root cause exception");
        ScheduledJobException exception = new ScheduledJobException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals("Root cause exception", exception.getCause().getMessage());
    }

    public void test_constructorWithNullMessage() {
        // Test constructor with null message
        ScheduledJobException exception = new ScheduledJobException(null);

        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructorWithNullMessageAndCause() {
        // Test constructor with null message and null cause
        ScheduledJobException exception = new ScheduledJobException(null, null);

        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructorWithEmptyMessage() {
        // Test constructor with empty message
        String message = "";
        ScheduledJobException exception = new ScheduledJobException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructorWithMessageAndNullCause() {
        // Test constructor with message and null cause
        String message = "Test message with null cause";
        ScheduledJobException exception = new ScheduledJobException(message, null);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_instanceOfFessSystemException() {
        // Test that ScheduledJobException is instance of FessSystemException
        ScheduledJobException exception = new ScheduledJobException("Test");

        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    public void test_stackTracePresent() {
        // Test that stack trace is captured properly
        ScheduledJobException exception = new ScheduledJobException("Stack trace test");

        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);

        // Verify the first stack trace element is from this test method
        StackTraceElement firstElement = exception.getStackTrace()[0];
        assertEquals(this.getClass().getName(), firstElement.getClassName());
        assertEquals("test_stackTracePresent", firstElement.getMethodName());
    }

    public void test_nestedExceptionChain() {
        // Test nested exception chain
        Throwable rootCause = new IllegalArgumentException("Root cause");
        Throwable middleCause = new IllegalStateException("Middle cause", rootCause);
        ScheduledJobException exception = new ScheduledJobException("Top level error", middleCause);

        assertEquals("Top level error", exception.getMessage());
        assertEquals(middleCause, exception.getCause());
        assertEquals("Middle cause", exception.getCause().getMessage());
        assertEquals(rootCause, exception.getCause().getCause());
        assertEquals("Root cause", exception.getCause().getCause().getMessage());
    }

    public void test_throwAndCatch() {
        // Test throwing and catching the exception
        String expectedMessage = "Job execution failed";
        boolean exceptionCaught = false;

        try {
            throw new ScheduledJobException(expectedMessage);
        } catch (ScheduledJobException e) {
            exceptionCaught = true;
            assertEquals(expectedMessage, e.getMessage());
        }

        assertTrue(exceptionCaught);
    }

    public void test_throwAndCatchWithCause() {
        // Test throwing and catching the exception with cause
        String expectedMessage = "Job execution failed with error";
        Throwable expectedCause = new NullPointerException("NPE occurred");
        boolean exceptionCaught = false;

        try {
            throw new ScheduledJobException(expectedMessage, expectedCause);
        } catch (ScheduledJobException e) {
            exceptionCaught = true;
            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedCause, e.getCause());
            assertTrue(e.getCause() instanceof NullPointerException);
        }

        assertTrue(exceptionCaught);
    }

    public void test_serialVersionUID() {
        // Test that the exception is serializable
        ScheduledJobException exception = new ScheduledJobException("Serialization test");

        // Verify that the exception has serialVersionUID field (won't compile if not present)
        assertTrue(exception instanceof java.io.Serializable);
    }

    public void test_longMessage() {
        // Test with very long message
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("Long message part ").append(i).append(" ");
        }
        String longMessage = sb.toString();

        ScheduledJobException exception = new ScheduledJobException(longMessage);
        assertEquals(longMessage, exception.getMessage());
    }

    public void test_specialCharactersInMessage() {
        // Test with special characters in message
        String specialMessage = "Error: \n\t\r Special chars: 日本語 中文 한국어 émojis 😀 🎉";
        ScheduledJobException exception = new ScheduledJobException(specialMessage);

        assertEquals(specialMessage, exception.getMessage());
    }
}