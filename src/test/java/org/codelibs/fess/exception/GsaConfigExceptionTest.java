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

public class GsaConfigExceptionTest extends UnitFessTestCase {

    public void test_constructorWithMessage() {
        // Test constructor with message only
        String message = "GSA configuration error occurred";
        GsaConfigException exception = new GsaConfigException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructorWithMessageAndCause() {
        // Test constructor with message and cause
        String message = "GSA configuration error with cause";
        Throwable cause = new RuntimeException("Root cause exception");
        GsaConfigException exception = new GsaConfigException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals(cause, exception.getCause());
        assertEquals("Root cause exception", exception.getCause().getMessage());
    }

    public void test_constructorWithNullMessage() {
        // Test constructor with null message
        GsaConfigException exception = new GsaConfigException(null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructorWithNullMessageAndCause() {
        // Test constructor with null message and null cause
        GsaConfigException exception = new GsaConfigException(null, null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructorWithNullMessageAndValidCause() {
        // Test constructor with null message and valid cause
        Throwable cause = new IllegalArgumentException("Invalid argument");
        GsaConfigException exception = new GsaConfigException(null, cause);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals(cause, exception.getCause());
        assertEquals("Invalid argument", exception.getCause().getMessage());
    }

    public void test_constructorWithEmptyMessage() {
        // Test constructor with empty message
        String message = "";
        GsaConfigException exception = new GsaConfigException(message);

        assertNotNull(exception);
        assertEquals("", exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_instanceOfFessSystemException() {
        // Test that GsaConfigException is an instance of FessSystemException
        GsaConfigException exception = new GsaConfigException("Test");

        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    public void test_serialization() {
        // Test serialVersionUID is properly defined
        GsaConfigException exception = new GsaConfigException("Serialization test");

        // Verify the exception is serializable (has serialVersionUID)
        assertNotNull(exception);
        // The serialVersionUID field exists and is accessible at compile time
        assertTrue(exception instanceof java.io.Serializable);
    }

    public void test_stackTrace() {
        // Test that stack trace is properly preserved
        String message = "GSA error with stack trace";
        Throwable cause = new NullPointerException("NPE occurred");
        GsaConfigException exception = new GsaConfigException(message, cause);

        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);

        // Verify cause's stack trace is also preserved
        assertNotNull(exception.getCause().getStackTrace());
        assertTrue(exception.getCause().getStackTrace().length > 0);
    }

    public void test_multiLevelCause() {
        // Test with nested exceptions
        Throwable rootCause = new IllegalStateException("Root problem");
        Throwable intermediateCause = new RuntimeException("Intermediate problem", rootCause);
        GsaConfigException exception = new GsaConfigException("Top level GSA error", intermediateCause);

        assertNotNull(exception);
        assertEquals("Top level GSA error", exception.getMessage());

        // Check first level cause
        assertNotNull(exception.getCause());
        assertEquals("Intermediate problem", exception.getCause().getMessage());

        // Check second level cause
        assertNotNull(exception.getCause().getCause());
        assertEquals("Root problem", exception.getCause().getCause().getMessage());
    }

    public void test_messageWithSpecialCharacters() {
        // Test constructor with message containing special characters
        String message = "GSA error: config file not found at /path/to/config.xml\n" + "Details: \"file\" does not exist\t[TAB]\r\n"
                + "Unicode: \u4E2D\u6587";
        GsaConfigException exception = new GsaConfigException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    public void test_veryLongMessage() {
        // Test with very long message
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("Very long error message line ").append(i).append(". ");
        }
        String longMessage = sb.toString();

        GsaConfigException exception = new GsaConfigException(longMessage);

        assertNotNull(exception);
        assertEquals(longMessage, exception.getMessage());
    }

    public void test_differentCauseTypes() {
        // Test with different types of cause exceptions

        // Test with IOException
        Throwable ioCause = new java.io.IOException("IO error");
        GsaConfigException ioException = new GsaConfigException("GSA IO error", ioCause);
        assertNotNull(ioException);
        assertTrue(ioException.getCause() instanceof java.io.IOException);

        // Test with custom exception
        Throwable customCause = new FessSystemException("Custom Fess error");
        GsaConfigException customException = new GsaConfigException("GSA custom error", customCause);
        assertNotNull(customException);
        assertTrue(customException.getCause() instanceof FessSystemException);

        // Test with Error (not Exception)
        Throwable errorCause = new OutOfMemoryError("Out of memory");
        GsaConfigException errorException = new GsaConfigException("GSA memory error", errorCause);
        assertNotNull(errorException);
        assertTrue(errorException.getCause() instanceof OutOfMemoryError);
    }
}