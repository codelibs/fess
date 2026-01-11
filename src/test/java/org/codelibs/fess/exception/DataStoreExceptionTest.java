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
import org.junit.jupiter.api.Test;

public class DataStoreExceptionTest extends UnitFessTestCase {

    @Test
    public void test_constructor_withMessage() {
        // Test with message only
        String message = "Data store error occurred";
        DataStoreException exception = new DataStoreException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void test_constructor_withCause() {
        // Test with cause only
        Exception cause = new IllegalStateException("Underlying error");
        DataStoreException exception = new DataStoreException(cause);

        assertNotNull(exception);
        assertEquals(cause, exception.getCause());
        assertEquals("java.lang.IllegalStateException: Underlying error", exception.getMessage());
        assertTrue(exception instanceof FessSystemException);
    }

    @Test
    public void test_constructor_withMessageAndCause() {
        // Test with both message and cause
        String message = "Data store connection failed";
        Exception cause = new RuntimeException("Connection timeout");
        DataStoreException exception = new DataStoreException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertTrue(exception instanceof FessSystemException);
    }

    @Test
    public void test_constructor_withNullMessage() {
        // Test with null message
        DataStoreException exception = new DataStoreException((String) null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withNullCause() {
        // Test with null cause
        DataStoreException exception = new DataStoreException((Throwable) null);

        assertNotNull(exception);
        assertNull(exception.getCause());
        assertNull(exception.getMessage());
    }

    @Test
    public void test_constructor_withNullMessageAndCause() {
        // Test with both null message and cause
        DataStoreException exception = new DataStoreException(null, null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withEmptyMessage() {
        // Test with empty string message
        String message = "";
        DataStoreException exception = new DataStoreException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withNestedCause() {
        // Test with nested exception causes
        Exception rootCause = new IllegalArgumentException("Root cause");
        Exception middleCause = new IllegalStateException("Middle cause", rootCause);
        DataStoreException exception = new DataStoreException("Top level error", middleCause);

        assertNotNull(exception);
        assertEquals("Top level error", exception.getMessage());
        assertEquals(middleCause, exception.getCause());
        assertEquals(rootCause, exception.getCause().getCause());
    }

    @Test
    public void test_stackTrace() {
        // Test that stack trace is properly set
        DataStoreException exception = new DataStoreException("Test error");
        StackTraceElement[] stackTrace = exception.getStackTrace();

        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);
        // The first element should be from this test method
        assertEquals("test_stackTrace", stackTrace[0].getMethodName());
    }

    @Test
    public void test_serialization() {
        // Test that the exception has proper serialVersionUID
        DataStoreException exception = new DataStoreException("Serialization test");

        // Check that it's a RuntimeException and can be thrown
        try {
            throw exception;
        } catch (DataStoreException e) {
            assertEquals("Serialization test", e.getMessage());
        } catch (Exception e) {
            fail("Should have caught DataStoreException");
        }
    }

    @Test
    public void test_inheritance() {
        // Test inheritance hierarchy
        DataStoreException exception = new DataStoreException("Test");

        assertTrue(exception instanceof DataStoreException);
        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    public void test_throwAndCatch() {
        // Test throwing and catching the exception
        final String errorMessage = "Data store operation failed";

        try {
            throw new DataStoreException(errorMessage);
        } catch (DataStoreException e) {
            assertEquals(errorMessage, e.getMessage());
            assertNull(e.getCause());
        } catch (Exception e) {
            fail("Should have caught DataStoreException specifically");
        }
    }

    @Test
    public void test_throwAndCatchWithCause() {
        // Test throwing and catching the exception with cause
        final String errorMessage = "Data store write error";
        final Exception cause = new RuntimeException("IO error");

        try {
            throw new DataStoreException(errorMessage, cause);
        } catch (DataStoreException e) {
            assertEquals(errorMessage, e.getMessage());
            assertEquals(cause, e.getCause());
            assertEquals("IO error", e.getCause().getMessage());
        } catch (Exception e) {
            fail("Should have caught DataStoreException specifically");
        }
    }

    @Test
    public void test_multipleExceptionTypes() {
        // Test with different types of causes
        DataStoreException withIOException = new DataStoreException("IO Error", new java.io.IOException("File not found"));
        DataStoreException withNPE = new DataStoreException("NPE Error", new NullPointerException("Null value"));
        DataStoreException withCustom = new DataStoreException("Custom Error", new FessSystemException("System error"));

        assertTrue(withIOException.getCause() instanceof java.io.IOException);
        assertTrue(withNPE.getCause() instanceof NullPointerException);
        assertTrue(withCustom.getCause() instanceof FessSystemException);
    }

    @Test
    public void test_getLongMessage() {
        // Test with very long message
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longMessage.append("Error ").append(i).append(" ");
        }
        String message = longMessage.toString();

        DataStoreException exception = new DataStoreException(message);
        assertEquals(message, exception.getMessage());
        assertTrue(exception.getMessage().length() > 5000);
    }

    @Test
    public void test_getLocalizedMessage() {
        // Test getLocalizedMessage method (inherited from Throwable)
        String message = "Localized error message";
        DataStoreException exception = new DataStoreException(message);

        // By default, getLocalizedMessage returns the same as getMessage
        assertEquals(message, exception.getLocalizedMessage());
    }

    @Test
    public void test_toString() {
        // Test toString method
        String message = "Test exception";
        DataStoreException exception = new DataStoreException(message);

        String toString = exception.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("DataStoreException"));
        assertTrue(toString.contains(message));
    }

    @Test
    public void test_fillInStackTrace() {
        // Test fillInStackTrace method
        DataStoreException exception = new DataStoreException("Stack trace test");
        Throwable filled = exception.fillInStackTrace();

        assertNotNull(filled);
        assertEquals(exception, filled);
        assertNotNull(filled.getStackTrace());
        assertTrue(filled.getStackTrace().length > 0);
    }
}