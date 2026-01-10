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
package org.codelibs.fess.opensearch.client;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class SearchEngineClientExceptionTest extends UnitFessTestCase {

    @Test
    public void test_constructor_withMessage() {
        // Test constructor with message only
        String message = "Test error message";
        SearchEngineClientException exception = new SearchEngineClientException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withMessageAndCause() {
        // Test constructor with message and cause
        String message = "Test error message";
        Throwable cause = new RuntimeException("Underlying cause");
        SearchEngineClientException exception = new SearchEngineClientException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void test_constructor_withNullMessage() {
        // Test constructor with null message
        SearchEngineClientException exception = new SearchEngineClientException(null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withNullMessageAndCause() {
        // Test constructor with null message and null cause
        SearchEngineClientException exception = new SearchEngineClientException(null, null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withEmptyMessage() {
        // Test constructor with empty message
        String message = "";
        SearchEngineClientException exception = new SearchEngineClientException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withMessageAndNullCause() {
        // Test constructor with message and null cause
        String message = "Test error message";
        SearchEngineClientException exception = new SearchEngineClientException(message, null);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withNullMessageAndValidCause() {
        // Test constructor with null message and valid cause
        Throwable cause = new IllegalArgumentException("Invalid argument");
        SearchEngineClientException exception = new SearchEngineClientException(null, cause);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void test_constructor_withLongMessage() {
        // Test constructor with long message
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("Long message content ");
        }
        String message = sb.toString();
        SearchEngineClientException exception = new SearchEngineClientException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withNestedCause() {
        // Test constructor with nested exceptions
        Throwable rootCause = new IllegalStateException("Root cause");
        Throwable intermediateCause = new RuntimeException("Intermediate cause", rootCause);
        String message = "Top level error";
        SearchEngineClientException exception = new SearchEngineClientException(message, intermediateCause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(intermediateCause, exception.getCause());
        assertEquals(rootCause, exception.getCause().getCause());
    }

    @Test
    public void test_constructor_withSpecialCharactersInMessage() {
        // Test constructor with special characters in message
        String message = "Error with special chars: \n\t\r\"'<>&";
        SearchEngineClientException exception = new SearchEngineClientException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_serialization() {
        // Test that the exception has serialVersionUID defined
        String message = "Serialization test";
        SearchEngineClientException exception = new SearchEngineClientException(message);

        // Verify the exception can be created (serialVersionUID is defined in the class)
        assertNotNull(exception);
        assertTrue(exception instanceof java.io.Serializable);
    }

    @Test
    public void test_stackTrace() {
        // Test that stack trace is properly captured
        String message = "Stack trace test";
        SearchEngineClientException exception = new SearchEngineClientException(message);

        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);

        // Verify the stack trace contains this test method
        boolean foundTestMethod = false;
        for (StackTraceElement element : exception.getStackTrace()) {
            if (element.getMethodName().equals("test_stackTrace")) {
                foundTestMethod = true;
                break;
            }
        }
        assertTrue(foundTestMethod);
    }

    @Test
    public void test_throwAndCatch() {
        // Test throwing and catching the exception
        String message = "Throw and catch test";

        try {
            throw new SearchEngineClientException(message);
        } catch (SearchEngineClientException e) {
            assertEquals(message, e.getMessage());
            assertNull(e.getCause());
        }
    }

    @Test
    public void test_throwAndCatchWithCause() {
        // Test throwing and catching the exception with cause
        String message = "Throw and catch with cause test";
        Throwable cause = new RuntimeException("Original error");

        try {
            throw new SearchEngineClientException(message, cause);
        } catch (SearchEngineClientException e) {
            assertEquals(message, e.getMessage());
            assertEquals(cause, e.getCause());
        }
    }
}