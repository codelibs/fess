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
package org.codelibs.fess.dict;

import org.codelibs.fess.unit.UnitFessTestCase;

public class DictionaryExceptionTest extends UnitFessTestCase {

    public void test_constructor_withMessage() {
        // Test constructor with message only
        String message = "Dictionary error occurred";
        DictionaryException exception = new DictionaryException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withMessageAndCause() {
        // Test constructor with message and cause
        String message = "Dictionary operation failed";
        Exception cause = new RuntimeException("Underlying error");
        DictionaryException exception = new DictionaryException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals(cause, exception.getCause());
        assertEquals("Underlying error", exception.getCause().getMessage());
    }

    public void test_constructor_withNullMessage() {
        // Test constructor with null message
        DictionaryException exception = new DictionaryException(null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withNullMessageAndCause() {
        // Test constructor with null message but valid cause
        Exception cause = new IllegalArgumentException("Invalid argument");
        DictionaryException exception = new DictionaryException(null, cause);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals(cause, exception.getCause());
    }

    public void test_constructor_withMessageAndNullCause() {
        // Test constructor with message and null cause
        String message = "Dictionary not found";
        DictionaryException exception = new DictionaryException(message, null);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withEmptyMessage() {
        // Test constructor with empty message
        String message = "";
        DictionaryException exception = new DictionaryException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withLongMessage() {
        // Test constructor with long message
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("Dictionary error ");
        }
        String longMessage = sb.toString();
        DictionaryException exception = new DictionaryException(longMessage);

        assertNotNull(exception);
        assertEquals(longMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withNestedCause() {
        // Test constructor with nested exception causes
        Exception rootCause = new IllegalStateException("Root cause");
        Exception middleCause = new RuntimeException("Middle cause", rootCause);
        String message = "Top level dictionary error";
        DictionaryException exception = new DictionaryException(message, middleCause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals(middleCause, exception.getCause());
        assertEquals("Middle cause", exception.getCause().getMessage());
        assertNotNull(exception.getCause().getCause());
        assertEquals(rootCause, exception.getCause().getCause());
        assertEquals("Root cause", exception.getCause().getCause().getMessage());
    }

    public void test_instanceof_FessSystemException() {
        // Test that DictionaryException is instance of FessSystemException
        DictionaryException exception = new DictionaryException("Test");

        assertTrue(exception instanceof org.codelibs.fess.exception.FessSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    public void test_stackTrace() {
        // Test that stack trace is properly set
        DictionaryException exception = new DictionaryException("Stack trace test");

        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);

        // Verify that the stack trace contains this test method
        boolean foundTestMethod = false;
        for (StackTraceElement element : exception.getStackTrace()) {
            if (element.getMethodName().equals("test_stackTrace") && element.getClassName().equals(this.getClass().getName())) {
                foundTestMethod = true;
                break;
            }
        }
        assertTrue(foundTestMethod);
    }

    public void test_serialVersionUID() {
        // Test that serialVersionUID is defined (by checking serializability)
        DictionaryException exception = new DictionaryException("Serialization test");

        assertTrue(exception instanceof java.io.Serializable);
    }

    public void test_toString() {
        // Test toString method
        String message = "Dictionary parse error";
        Exception cause = new NullPointerException("NPE occurred");
        DictionaryException exception = new DictionaryException(message, cause);

        String toStringResult = exception.toString();
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("DictionaryException"));
        assertTrue(toStringResult.contains(message));
    }
}