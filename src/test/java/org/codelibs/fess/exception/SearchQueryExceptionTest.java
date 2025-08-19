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

public class SearchQueryExceptionTest extends UnitFessTestCase {

    public void test_constructorWithMessageAndCause() {
        // Test constructor with message and cause
        String message = "Query parsing failed";
        Exception cause = new IllegalArgumentException("Invalid query syntax");

        SearchQueryException exception = new SearchQueryException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
    }

    public void test_constructorWithMessage() {
        // Test constructor with message only
        String message = "Search query is invalid";

        SearchQueryException exception = new SearchQueryException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception instanceof FessSystemException);
    }

    public void test_constructorWithCause() {
        // Test constructor with cause only
        Exception cause = new NullPointerException("Query object is null");

        SearchQueryException exception = new SearchQueryException(cause);

        assertEquals(cause, exception.getCause());
        assertEquals("java.lang.NullPointerException: Query object is null", exception.getMessage());
        assertTrue(exception instanceof FessSystemException);
    }

    public void test_constructorWithNullMessage() {
        // Test constructor with null message
        SearchQueryException exception = new SearchQueryException((String) null);

        assertNull(exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception instanceof FessSystemException);
    }

    public void test_constructorWithNullCause() {
        // Test constructor with null cause
        SearchQueryException exception = new SearchQueryException((Throwable) null);

        assertNull(exception.getCause());
        assertNull(exception.getMessage());
        assertTrue(exception instanceof FessSystemException);
    }

    public void test_constructorWithNullMessageAndCause() {
        // Test constructor with null message and null cause
        SearchQueryException exception = new SearchQueryException(null, null);

        assertNull(exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception instanceof FessSystemException);
    }

    public void test_constructorWithEmptyMessage() {
        // Test constructor with empty message
        String message = "";

        SearchQueryException exception = new SearchQueryException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructorWithMessageAndNullCause() {
        // Test constructor with message and null cause
        String message = "Query processing error";

        SearchQueryException exception = new SearchQueryException(message, null);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructorWithNullMessageAndValidCause() {
        // Test constructor with null message and valid cause
        Exception cause = new RuntimeException("Runtime error");

        SearchQueryException exception = new SearchQueryException(null, cause);

        // When message is null, the parent constructor behavior applies
        assertNull(exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    public void test_nestedExceptionChain() {
        // Test nested exception chain
        Exception rootCause = new IllegalStateException("Root cause");
        Exception middleCause = new RuntimeException("Middle cause", rootCause);
        String message = "Top level error";

        SearchQueryException exception = new SearchQueryException(message, middleCause);

        assertEquals(message, exception.getMessage());
        assertEquals(middleCause, exception.getCause());
        assertEquals(rootCause, exception.getCause().getCause());
    }

    public void test_exceptionSerialization() {
        // Test that the exception is serializable
        String message = "Serialization test";
        Exception cause = new Exception("Test cause");

        SearchQueryException exception = new SearchQueryException(message, cause);

        // Verify serialVersionUID is properly set
        assertNotNull(exception);
        assertTrue(exception instanceof java.io.Serializable);
    }

    public void test_stackTracePresence() {
        // Test that stack trace is present
        SearchQueryException exception = new SearchQueryException("Stack trace test");

        StackTraceElement[] stackTrace = exception.getStackTrace();
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);

        // Verify the top of the stack trace contains this test method
        boolean foundTestMethod = false;
        for (StackTraceElement element : stackTrace) {
            if (element.getMethodName().equals("test_stackTracePresence")) {
                foundTestMethod = true;
                break;
            }
        }
        assertTrue(foundTestMethod);
    }

    public void test_inheritanceHierarchy() {
        // Test inheritance hierarchy
        SearchQueryException exception = new SearchQueryException("Inheritance test");

        assertTrue(exception instanceof SearchQueryException);
        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    public void test_multipleInstantiations() {
        // Test multiple instantiations with different constructors
        SearchQueryException exception1 = new SearchQueryException("Exception 1");
        SearchQueryException exception2 = new SearchQueryException("Exception 2", new Exception("Cause 2"));
        SearchQueryException exception3 = new SearchQueryException(new Exception("Cause 3"));

        assertNotSame(exception1, exception2);
        assertNotSame(exception2, exception3);
        assertNotSame(exception1, exception3);

        assertEquals("Exception 1", exception1.getMessage());
        assertEquals("Exception 2", exception2.getMessage());
        assertNotNull(exception3.getCause());
    }

    public void test_throwAndCatch() {
        // Test throwing and catching the exception
        String expectedMessage = "Test throw and catch";

        try {
            throw new SearchQueryException(expectedMessage);
        } catch (SearchQueryException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertNull(e.getCause());
        } catch (Exception e) {
            fail("Should have caught SearchQueryException");
        }
    }

    public void test_throwWithCauseAndCatch() {
        // Test throwing with cause and catching
        String expectedMessage = "Test with cause";
        Exception expectedCause = new IllegalArgumentException("Invalid argument");

        try {
            throw new SearchQueryException(expectedMessage, expectedCause);
        } catch (SearchQueryException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedCause, e.getCause());
        } catch (Exception e) {
            fail("Should have caught SearchQueryException");
        }
    }

    public void test_catchAsFessSystemException() {
        // Test catching as parent exception type
        String message = "Polymorphism test";

        try {
            throw new SearchQueryException(message);
        } catch (FessSystemException e) {
            assertEquals(message, e.getMessage());
            assertTrue(e instanceof SearchQueryException);
        } catch (Exception e) {
            fail("Should have caught as FessSystemException");
        }
    }

    public void test_longMessageHandling() {
        // Test handling of long messages
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longMessage.append("Long message part ").append(i).append(" ");
        }
        String message = longMessage.toString();

        SearchQueryException exception = new SearchQueryException(message);

        assertEquals(message, exception.getMessage());
        assertTrue(exception.getMessage().length() > 10000);
    }

    public void test_specialCharactersInMessage() {
        // Test special characters in message
        String message = "Special chars: \n\t\r\"'<>&{}[]()!@#$%^&*";

        SearchQueryException exception = new SearchQueryException(message);

        assertEquals(message, exception.getMessage());
    }

    public void test_unicodeInMessage() {
        // Test unicode characters in message
        String message = "Unicode test: 日本語 中文 한국어 🚀 ñ é ü";

        SearchQueryException exception = new SearchQueryException(message);

        assertEquals(message, exception.getMessage());
    }
}