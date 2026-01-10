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

public class ScriptEngineExceptionTest extends UnitFessTestCase {

    @Test
    public void test_constructor_withMessage() {
        // Test constructor with message only
        String message = "Script engine error occurred";
        ScriptEngineException exception = new ScriptEngineException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withMessageAndCause() {
        // Test constructor with message and cause
        String message = "Script execution failed";
        Exception cause = new RuntimeException("Underlying error");
        ScriptEngineException exception = new ScriptEngineException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals("Underlying error", exception.getCause().getMessage());
    }

    @Test
    public void test_constructor_withNullMessage() {
        // Test constructor with null message
        ScriptEngineException exception = new ScriptEngineException(null);

        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withNullMessageAndCause() {
        // Test constructor with null message and null cause
        ScriptEngineException exception = new ScriptEngineException(null, null);

        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withEmptyMessage() {
        // Test constructor with empty message
        String message = "";
        ScriptEngineException exception = new ScriptEngineException(message);

        assertEquals("", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withMessageAndNullCause() {
        // Test constructor with message and null cause
        String message = "Script error";
        ScriptEngineException exception = new ScriptEngineException(message, null);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_instanceOfFessSystemException() {
        // Test that ScriptEngineException is an instance of FessSystemException
        ScriptEngineException exception = new ScriptEngineException("test");

        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    public void test_serialization() {
        // Test that the exception has the correct serialVersionUID field
        ScriptEngineException exception = new ScriptEngineException("Serialization test");

        // Verify the exception can be created and basic properties work
        assertNotNull(exception);
        assertEquals("Serialization test", exception.getMessage());
    }

    @Test
    public void test_stackTrace() {
        // Test that stack trace is properly maintained
        String message = "Stack trace test";
        Exception cause = new IllegalArgumentException("Invalid argument");
        ScriptEngineException exception = new ScriptEngineException(message, cause);

        // Verify stack trace exists
        StackTraceElement[] stackTrace = exception.getStackTrace();
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);

        // Verify cause's stack trace is also available
        StackTraceElement[] causeStackTrace = exception.getCause().getStackTrace();
        assertNotNull(causeStackTrace);
        assertTrue(causeStackTrace.length > 0);
    }

    @Test
    public void test_toString() {
        // Test toString method
        String message = "Script engine test error";
        ScriptEngineException exception = new ScriptEngineException(message);

        String result = exception.toString();
        assertNotNull(result);
        assertTrue(result.contains("ScriptEngineException"));
        assertTrue(result.contains(message));
    }

    @Test
    public void test_withComplexCauseChain() {
        // Test with a chain of exceptions
        Exception rootCause = new NullPointerException("Root cause");
        Exception intermediateCause = new IllegalStateException("Intermediate cause", rootCause);
        ScriptEngineException exception = new ScriptEngineException("Top level error", intermediateCause);

        assertEquals("Top level error", exception.getMessage());
        assertEquals(intermediateCause, exception.getCause());
        assertEquals("Intermediate cause", exception.getCause().getMessage());
        assertEquals(rootCause, exception.getCause().getCause());
        assertEquals("Root cause", exception.getCause().getCause().getMessage());
    }

    @Test
    public void test_withLongMessage() {
        // Test with a very long message
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longMessage.append("Error message part ").append(i).append(" ");
        }
        String message = longMessage.toString();

        ScriptEngineException exception = new ScriptEngineException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_withSpecialCharactersInMessage() {
        // Test with special characters in message
        String message = "Error with special chars: \n\t\r\"'<>&";
        ScriptEngineException exception = new ScriptEngineException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_withUnicodeInMessage() {
        // Test with Unicode characters in message
        String message = "エラー発生: スクリプトエンジン例外 😱";
        ScriptEngineException exception = new ScriptEngineException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_throwAndCatch() {
        // Test throwing and catching the exception
        String message = "Thrown exception test";

        try {
            throw new ScriptEngineException(message);
        } catch (ScriptEngineException e) {
            assertEquals(message, e.getMessage());
            assertNull(e.getCause());
        } catch (Exception e) {
            fail("Should have caught ScriptEngineException");
        }
    }

    @Test
    public void test_throwAndCatchAsFessSystemException() {
        // Test catching as parent class
        String message = "Parent class catch test";

        try {
            throw new ScriptEngineException(message);
        } catch (FessSystemException e) {
            assertEquals(message, e.getMessage());
            assertTrue(e instanceof ScriptEngineException);
        } catch (Exception e) {
            fail("Should have caught as FessSystemException");
        }
    }
}