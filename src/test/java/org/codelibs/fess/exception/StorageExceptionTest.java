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

public class StorageExceptionTest extends UnitFessTestCase {

    @Test
    public void test_constructorWithMessage() {
        // Test constructor with message only
        String message = "Storage error occurred";
        StorageException exception = new StorageException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructorWithMessageAndCause() {
        // Test constructor with message and cause
        String message = "Storage operation failed";
        Exception cause = new RuntimeException("Underlying error");
        StorageException exception = new StorageException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void test_constructorWithNullMessage() {
        // Test constructor with null message
        StorageException exception = new StorageException(null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructorWithNullMessageAndCause() {
        // Test constructor with null message and null cause
        StorageException exception = new StorageException(null, null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructorWithEmptyMessage() {
        // Test constructor with empty message
        String message = "";
        StorageException exception = new StorageException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructorWithMessageAndNullCause() {
        // Test constructor with message and null cause
        String message = "Storage error";
        StorageException exception = new StorageException(message, null);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_inheritanceFromFessSystemException() {
        // Test that StorageException extends FessSystemException
        StorageException exception = new StorageException("Test");

        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    public void test_stackTraceWithCause() {
        // Test that stack trace is properly preserved with cause
        String innerMessage = "Inner exception";
        String outerMessage = "Outer exception";
        RuntimeException innerException = new RuntimeException(innerMessage);
        StorageException exception = new StorageException(outerMessage, innerException);

        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
        assertEquals(innerException, exception.getCause());
        assertEquals(innerMessage, exception.getCause().getMessage());
    }

    @Test
    public void test_multiLevelExceptionChaining() {
        // Test multi-level exception chaining
        Exception level1 = new Exception("Level 1");
        RuntimeException level2 = new RuntimeException("Level 2", level1);
        StorageException level3 = new StorageException("Level 3", level2);

        assertEquals("Level 3", level3.getMessage());
        assertEquals(level2, level3.getCause());
        assertEquals("Level 2", level3.getCause().getMessage());
        assertEquals(level1, level3.getCause().getCause());
        assertEquals("Level 1", level3.getCause().getCause().getMessage());
    }

    @Test
    public void test_throwAndCatchException() {
        // Test throwing and catching the exception
        String expectedMessage = "Storage failure";

        try {
            throw new StorageException(expectedMessage);
        } catch (StorageException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertNull(e.getCause());
        }
    }

    @Test
    public void test_throwAndCatchExceptionWithCause() {
        // Test throwing and catching the exception with cause
        String expectedMessage = "Storage write error";
        Exception expectedCause = new IllegalStateException("Invalid state");

        try {
            throw new StorageException(expectedMessage, expectedCause);
        } catch (StorageException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedCause, e.getCause());
        }
    }

    @Test
    public void test_serialVersionUID() {
        // Test that the exception is serializable
        StorageException exception = new StorageException("Test serialization");

        // Verify that the exception is serializable by checking it extends RuntimeException
        // which implements Serializable
        assertTrue(exception instanceof java.io.Serializable);
    }

    @Test
    public void test_longMessageHandling() {
        // Test with very long message
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longMessage.append("Storage error ").append(i).append(" ");
        }
        String message = longMessage.toString();

        StorageException exception = new StorageException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void test_specialCharactersInMessage() {
        // Test with special characters in message
        String message = "Storage error: \n\t\r\"'<>&";
        StorageException exception = new StorageException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    public void test_differentCauseTypes() {
        // Test with different types of causes
        StorageException exception1 = new StorageException("IO Error", new java.io.IOException("File not found"));
        assertTrue(exception1.getCause() instanceof java.io.IOException);

        StorageException exception2 = new StorageException("NPE", new NullPointerException("Null reference"));
        assertTrue(exception2.getCause() instanceof NullPointerException);

        StorageException exception3 = new StorageException("Custom", new FessSystemException("System error"));
        assertTrue(exception3.getCause() instanceof FessSystemException);
    }
}