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

public class PluginExceptionTest extends UnitFessTestCase {

    public void test_constructor_withMessage() {
        // Test constructor with message only
        String message = "Plugin error occurred";
        PluginException exception = new PluginException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withMessageAndCause() {
        // Test constructor with message and cause
        String message = "Plugin initialization failed";
        Throwable cause = new RuntimeException("Underlying error");
        PluginException exception = new PluginException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    public void test_constructor_withNullMessage() {
        // Test constructor with null message
        PluginException exception = new PluginException(null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withNullMessageAndCause() {
        // Test constructor with null message and null cause
        PluginException exception = new PluginException(null, null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withEmptyMessage() {
        // Test constructor with empty message
        String message = "";
        PluginException exception = new PluginException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withMessageAndNullCause() {
        // Test constructor with message and null cause
        String message = "Plugin error with null cause";
        PluginException exception = new PluginException(message, null);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_exceptionChaining() {
        // Test exception chaining
        Throwable rootCause = new IllegalArgumentException("Invalid argument");
        Throwable intermediateCause = new RuntimeException("Processing failed", rootCause);
        String message = "Plugin operation failed";
        PluginException exception = new PluginException(message, intermediateCause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(intermediateCause, exception.getCause());
        assertEquals(rootCause, exception.getCause().getCause());
    }

    public void test_instanceOfFessSystemException() {
        // Test that PluginException is an instance of FessSystemException
        PluginException exception = new PluginException("Test message");

        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    public void test_throwAndCatch() {
        // Test throwing and catching PluginException
        String expectedMessage = "Plugin loading failed";

        try {
            throw new PluginException(expectedMessage);
        } catch (PluginException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertNull(e.getCause());
        }
    }

    public void test_throwAndCatchWithCause() {
        // Test throwing and catching PluginException with cause
        String expectedMessage = "Plugin configuration error";
        Throwable expectedCause = new IllegalStateException("Invalid state");

        try {
            throw new PluginException(expectedMessage, expectedCause);
        } catch (PluginException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedCause, e.getCause());
        }
    }

    public void test_stackTrace() {
        // Test that stack trace is properly set
        PluginException exception = new PluginException("Stack trace test");

        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);

        // Verify that the first stack trace element is from this test method
        StackTraceElement firstElement = exception.getStackTrace()[0];
        assertEquals(this.getClass().getName(), firstElement.getClassName());
        assertEquals("test_stackTrace", firstElement.getMethodName());
    }

    public void test_serialVersionUID() {
        // Test that the exception is serializable
        PluginException exception = new PluginException("Serialization test");

        // The exception should be serializable since it extends RuntimeException
        assertTrue(exception instanceof java.io.Serializable);
    }

    public void test_longMessage() {
        // Test with a very long message
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("Long message part ").append(i).append(" ");
        }
        String longMessage = sb.toString();

        PluginException exception = new PluginException(longMessage);

        assertNotNull(exception);
        assertEquals(longMessage, exception.getMessage());
    }

    public void test_multiLevelExceptionChain() {
        // Test multi-level exception chain
        Throwable level3 = new NullPointerException("Null value");
        Throwable level2 = new IllegalStateException("Invalid state", level3);
        Throwable level1 = new RuntimeException("Runtime error", level2);
        PluginException exception = new PluginException("Plugin failure", level1);

        // Verify the complete chain
        assertEquals("Plugin failure", exception.getMessage());
        assertEquals(level1, exception.getCause());
        assertEquals("Runtime error", exception.getCause().getMessage());
        assertEquals(level2, exception.getCause().getCause());
        assertEquals("Invalid state", exception.getCause().getCause().getMessage());
        assertEquals(level3, exception.getCause().getCause().getCause());
        assertEquals("Null value", exception.getCause().getCause().getCause().getMessage());
    }

    public void test_toStringMethod() {
        // Test toString method behavior
        String message = "Plugin toString test";
        PluginException exception = new PluginException(message);

        String toStringResult = exception.toString();
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains(PluginException.class.getName()));
        assertTrue(toStringResult.contains(message));
    }

    public void test_toStringMethodWithCause() {
        // Test toString method behavior with cause
        String message = "Plugin error with cause";
        Throwable cause = new RuntimeException("Root cause");
        PluginException exception = new PluginException(message, cause);

        String toStringResult = exception.toString();
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains(PluginException.class.getName()));
        assertTrue(toStringResult.contains(message));
    }
}