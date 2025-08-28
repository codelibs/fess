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

public class JobProcessingExceptionTest extends UnitFessTestCase {

    public void test_constructor_withCause() {
        // Test constructor with Throwable cause
        final Exception cause = new RuntimeException("Root cause");
        final JobProcessingException exception = new JobProcessingException(cause);

        assertNotNull(exception);
        assertEquals(cause, exception.getCause());
        assertEquals("java.lang.RuntimeException: Root cause", exception.getMessage());
    }

    public void test_constructor_withMessageAndCause() {
        // Test constructor with message and cause
        final String message = "Job processing failed";
        final Exception cause = new IllegalStateException("Invalid state");
        final JobProcessingException exception = new JobProcessingException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    public void test_constructor_withMessage() {
        // Test constructor with message only
        final String message = "Job processing error occurred";
        final JobProcessingException exception = new JobProcessingException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withNullCause() {
        // Test constructor with null cause
        final JobProcessingException exception = new JobProcessingException((Throwable) null);

        assertNotNull(exception);
        assertNull(exception.getCause());
        assertNull(exception.getMessage());
    }

    public void test_constructor_withNullMessageAndCause() {
        // Test constructor with null message and cause
        final JobProcessingException exception = new JobProcessingException(null, null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withNullMessage() {
        // Test constructor with null message
        final JobProcessingException exception = new JobProcessingException((String) null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withEmptyMessage() {
        // Test constructor with empty message
        final String message = "";
        final JobProcessingException exception = new JobProcessingException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withCauseChain() {
        // Test constructor with chained exceptions
        final Exception rootCause = new IllegalArgumentException("Invalid argument");
        final Exception middleCause = new RuntimeException("Runtime error", rootCause);
        final JobProcessingException exception = new JobProcessingException(middleCause);

        assertNotNull(exception);
        assertEquals(middleCause, exception.getCause());
        assertEquals(rootCause, exception.getCause().getCause());
    }

    public void test_inheritance() {
        // Test that JobProcessingException is a FessSystemException
        final JobProcessingException exception = new JobProcessingException("Test");

        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    public void test_stackTrace() {
        // Test that stack trace is properly captured
        final JobProcessingException exception = new JobProcessingException("Stack trace test");

        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);

        // Verify the top of the stack trace points to this test method
        final StackTraceElement topElement = exception.getStackTrace()[0];
        assertEquals("test_stackTrace", topElement.getMethodName());
        assertEquals(this.getClass().getName(), topElement.getClassName());
    }

    public void test_serialization() {
        // Test that serialVersionUID is properly defined
        final JobProcessingException exception = new JobProcessingException("Serialization test");

        // The exception should be serializable since it extends RuntimeException
        assertTrue(exception instanceof java.io.Serializable);
    }

    public void test_getMessage_withComplexCause() {
        // Test getMessage behavior with complex cause structure
        final Exception innerCause = new NullPointerException("NPE occurred");
        final Exception outerCause = new IllegalStateException("State error", innerCause);
        final JobProcessingException exception = new JobProcessingException(outerCause);

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("IllegalStateException"));
        assertTrue(exception.getMessage().contains("State error"));
    }

    public void test_constructor_withLongMessage() {
        // Test constructor with a very long message
        final StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longMessage.append("Long message part ").append(i).append(" ");
        }
        final String message = longMessage.toString();
        final JobProcessingException exception = new JobProcessingException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    public void test_constructor_withSpecialCharactersInMessage() {
        // Test constructor with special characters in message
        final String message = "Job failed with special chars: \n\t\r\"'<>&";
        final JobProcessingException exception = new JobProcessingException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    public void test_constructor_withUnicodeMessage() {
        // Test constructor with Unicode characters in message
        final String message = "ジョブ処理エラー: 失敗しました 😱";
        final JobProcessingException exception = new JobProcessingException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }
}