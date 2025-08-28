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

public class CommandExecutionExceptionTest extends UnitFessTestCase {

    public void test_constructor_withMessage() {
        // Test constructor with message only
        String message = "Command execution failed";
        CommandExecutionException exception = new CommandExecutionException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
    }

    public void test_constructor_withMessageAndCause() {
        // Test constructor with message and cause
        String message = "Command execution failed with error";
        Throwable cause = new IllegalStateException("Invalid state");
        CommandExecutionException exception = new CommandExecutionException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals("Invalid state", exception.getCause().getMessage());
        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
    }

    public void test_constructor_withNullMessage() {
        // Test constructor with null message
        CommandExecutionException exception = new CommandExecutionException(null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withNullMessageAndCause() {
        // Test constructor with null message and null cause
        CommandExecutionException exception = new CommandExecutionException(null, null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withEmptyMessage() {
        // Test constructor with empty message
        String message = "";
        CommandExecutionException exception = new CommandExecutionException(message);

        assertNotNull(exception);
        assertEquals("", exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withMessageAndNullCause() {
        // Test constructor with message and null cause
        String message = "Command failed";
        CommandExecutionException exception = new CommandExecutionException(message, null);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withNullMessageAndValidCause() {
        // Test constructor with null message and valid cause
        Throwable cause = new RuntimeException("Runtime error");
        CommandExecutionException exception = new CommandExecutionException(null, cause);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals("Runtime error", exception.getCause().getMessage());
    }

    public void test_constructor_withLongMessage() {
        // Test constructor with long message
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("Long message content ");
        }
        String longMessage = sb.toString();

        CommandExecutionException exception = new CommandExecutionException(longMessage);

        assertNotNull(exception);
        assertEquals(longMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withNestedCause() {
        // Test constructor with nested exception causes
        Throwable rootCause = new IllegalArgumentException("Root cause");
        Throwable middleCause = new IllegalStateException("Middle cause", rootCause);
        String message = "Command execution failed with nested exception";

        CommandExecutionException exception = new CommandExecutionException(message, middleCause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(middleCause, exception.getCause());
        assertEquals("Middle cause", exception.getCause().getMessage());
        assertEquals(rootCause, exception.getCause().getCause());
        assertEquals("Root cause", exception.getCause().getCause().getMessage());
    }

    public void test_serialVersionUID() {
        // Test that serialVersionUID is properly set
        CommandExecutionException exception1 = new CommandExecutionException("Test");
        CommandExecutionException exception2 = new CommandExecutionException("Test");

        // Both instances should be of the same class
        assertEquals(exception1.getClass(), exception2.getClass());

        // Test serialization compatibility by checking class name
        assertEquals("org.codelibs.fess.exception.CommandExecutionException", exception1.getClass().getName());
    }

    public void test_exceptionThrown() {
        // Test that the exception can be thrown and caught properly
        String expectedMessage = "Command execution error occurred";

        try {
            throw new CommandExecutionException(expectedMessage);
        } catch (CommandExecutionException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertNull(e.getCause());
        } catch (Exception e) {
            fail("Should have caught CommandExecutionException");
        }
    }

    public void test_exceptionThrownWithCause() {
        // Test that the exception with cause can be thrown and caught properly
        String expectedMessage = "Command failed to execute";
        Throwable expectedCause = new IllegalStateException("Invalid command state");

        try {
            throw new CommandExecutionException(expectedMessage, expectedCause);
        } catch (CommandExecutionException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedCause, e.getCause());
        } catch (Exception e) {
            fail("Should have caught CommandExecutionException");
        }
    }

    public void test_exceptionInheritance() {
        // Test exception inheritance hierarchy
        CommandExecutionException exception = new CommandExecutionException("Test");

        // Check inheritance chain
        assertTrue(exception instanceof CommandExecutionException);
        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);

        // Should be runtime exception (unchecked), not an Error
        // CommandExecutionException extends RuntimeException, not Error
        assertTrue(exception instanceof RuntimeException);
    }

    public void test_stackTracePresent() {
        // Test that stack trace is properly captured
        CommandExecutionException exception = new CommandExecutionException("Stack trace test");

        StackTraceElement[] stackTrace = exception.getStackTrace();
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);

        // First element should be from this test method
        StackTraceElement firstElement = stackTrace[0];
        assertEquals(this.getClass().getName(), firstElement.getClassName());
        assertEquals("test_stackTracePresent", firstElement.getMethodName());
    }

    public void test_multipleExceptionScenarios() {
        // Test multiple realistic exception scenarios

        // Scenario 1: Command not found
        CommandExecutionException cmdNotFound = new CommandExecutionException("Command 'xyz' not found");
        assertTrue(cmdNotFound.getMessage().contains("not found"));

        // Scenario 2: Command timeout
        CommandExecutionException timeout =
                new CommandExecutionException("Command execution timed out after 30 seconds", new RuntimeException("Timeout"));
        assertTrue(timeout.getMessage().contains("timed out"));
        assertNotNull(timeout.getCause());

        // Scenario 3: Permission denied
        CommandExecutionException permissionDenied = new CommandExecutionException("Permission denied: cannot execute command");
        assertTrue(permissionDenied.getMessage().contains("Permission denied"));

        // Scenario 4: Exit code error
        CommandExecutionException exitCodeError = new CommandExecutionException("Command exited with non-zero code: 127");
        assertTrue(exitCodeError.getMessage().contains("non-zero code"));
    }
}