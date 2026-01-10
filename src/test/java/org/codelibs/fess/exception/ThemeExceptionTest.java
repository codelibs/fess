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

public class ThemeExceptionTest extends UnitFessTestCase {

    @Test
    public void test_constructorWithMessage() {
        // Test constructor with message only
        String message = "Theme installation failed";
        ThemeException exception = new ThemeException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructorWithMessageAndCause() {
        // Test constructor with message and cause
        String message = "Theme configuration error";
        RuntimeException cause = new RuntimeException("Config file not found");
        ThemeException exception = new ThemeException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals("Config file not found", exception.getCause().getMessage());
    }

    @Test
    public void test_constructorWithNullMessage() {
        // Test constructor with null message
        ThemeException exception = new ThemeException(null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructorWithNullMessageAndCause() {
        // Test constructor with null message and cause
        ThemeException exception = new ThemeException(null, null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructorWithEmptyMessage() {
        // Test constructor with empty message
        String message = "";
        ThemeException exception = new ThemeException(message);

        assertNotNull(exception);
        assertEquals("", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructorWithMessageAndNullCause() {
        // Test constructor with message and null cause
        String message = "Theme uninstallation failed";
        ThemeException exception = new ThemeException(message, null);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_inheritance() {
        // Test that ThemeException properly extends FessSystemException
        ThemeException exception = new ThemeException("Test");

        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    public void test_stackTrace() {
        // Test that stack trace is properly captured
        ThemeException exception = new ThemeException("Stack trace test");

        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);

        // Verify that this test method appears in the stack trace
        boolean foundTestMethod = false;
        for (StackTraceElement element : exception.getStackTrace()) {
            if ("test_stackTrace".equals(element.getMethodName())) {
                foundTestMethod = true;
                break;
            }
        }
        assertTrue(foundTestMethod);
    }

    @Test
    public void test_serialVersionUID() {
        // Test that the exception is serializable
        ThemeException exception = new ThemeException("Serialization test");

        // Verify that the exception can be created and has the expected structure
        assertNotNull(exception);
        assertTrue(exception instanceof java.io.Serializable);
    }

    @Test
    public void test_nestedExceptions() {
        // Test nested exception handling
        Exception innerCause = new IllegalArgumentException("Invalid parameter");
        RuntimeException middleCause = new RuntimeException("Processing failed", innerCause);
        ThemeException exception = new ThemeException("Theme operation failed", middleCause);

        assertNotNull(exception);
        assertEquals("Theme operation failed", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals(middleCause, exception.getCause());
        assertEquals("Processing failed", exception.getCause().getMessage());

        // Check nested cause
        Throwable nestedCause = exception.getCause().getCause();
        assertNotNull(nestedCause);
        assertEquals(innerCause, nestedCause);
        assertEquals("Invalid parameter", nestedCause.getMessage());
    }

    @Test
    public void test_messageWithSpecialCharacters() {
        // Test message with special characters
        String message = "Theme error: 日本語 & special <characters> \"quoted\" 'text'";
        ThemeException exception = new ThemeException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void test_veryLongMessage() {
        // Test with very long message
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("Long message part ").append(i).append(" ");
        }
        String longMessage = sb.toString();

        ThemeException exception = new ThemeException(longMessage);

        assertNotNull(exception);
        assertEquals(longMessage, exception.getMessage());
    }

    @Test
    public void test_throwAndCatch() {
        // Test throwing and catching the exception
        String expectedMessage = "Theme loading error";

        try {
            throw new ThemeException(expectedMessage);
        } catch (ThemeException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertNull(e.getCause());
        } catch (Exception e) {
            fail("Should have caught ThemeException, but caught: " + e.getClass());
        }
    }

    @Test
    public void test_throwAndCatchWithCause() {
        // Test throwing and catching exception with cause
        String expectedMessage = "Theme validation failed";
        Exception expectedCause = new IllegalStateException("Invalid state");

        try {
            throw new ThemeException(expectedMessage, expectedCause);
        } catch (ThemeException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedCause, e.getCause());
        } catch (Exception e) {
            fail("Should have caught ThemeException, but caught: " + e.getClass());
        }
    }

    @Test
    public void test_catchAsFessSystemException() {
        // Test catching as parent exception type
        String message = "Theme error";

        try {
            throw new ThemeException(message);
        } catch (FessSystemException e) {
            assertEquals(message, e.getMessage());
            assertTrue(e instanceof ThemeException);
        } catch (Exception e) {
            fail("Should have caught as FessSystemException, but caught: " + e.getClass());
        }
    }

    @Test
    public void test_rethrowWithWrapping() {
        // Test rethrowing with wrapping
        String originalMessage = "Original theme error";
        String wrapperMessage = "Failed to process theme";

        try {
            try {
                throw new ThemeException(originalMessage);
            } catch (ThemeException e) {
                throw new ThemeException(wrapperMessage, e);
            }
        } catch (ThemeException e) {
            assertEquals(wrapperMessage, e.getMessage());
            assertNotNull(e.getCause());
            assertTrue(e.getCause() instanceof ThemeException);
            assertEquals(originalMessage, e.getCause().getMessage());
        }
    }
}