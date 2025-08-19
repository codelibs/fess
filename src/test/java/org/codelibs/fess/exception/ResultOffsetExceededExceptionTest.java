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

public class ResultOffsetExceededExceptionTest extends UnitFessTestCase {

    public void test_constructor_withMessage() {
        // Test with normal message
        String message = "Offset exceeded the limit";
        ResultOffsetExceededException exception = new ResultOffsetExceededException(message);

        assertEquals(message, exception.getMessage());
        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
        assertNull(exception.getCause());
    }

    public void test_constructor_withNullMessage() {
        // Test with null message
        ResultOffsetExceededException exception = new ResultOffsetExceededException(null);

        assertNull(exception.getMessage());
        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
        assertNull(exception.getCause());
    }

    public void test_constructor_withEmptyMessage() {
        // Test with empty message
        String message = "";
        ResultOffsetExceededException exception = new ResultOffsetExceededException(message);

        assertEquals(message, exception.getMessage());
        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
        assertNull(exception.getCause());
    }

    public void test_constructor_withLongMessage() {
        // Test with long message
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("Long message content ");
        }
        String longMessage = sb.toString();
        ResultOffsetExceededException exception = new ResultOffsetExceededException(longMessage);

        assertEquals(longMessage, exception.getMessage());
        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
        assertNull(exception.getCause());
    }

    public void test_inheritance() {
        // Test that ResultOffsetExceededException is a subclass of FessSystemException
        ResultOffsetExceededException exception = new ResultOffsetExceededException("Test message");

        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    public void test_serialization() {
        // Test serialVersionUID exists
        ResultOffsetExceededException exception = new ResultOffsetExceededException("Test serialization");

        // The exception should be serializable since it extends RuntimeException
        assertTrue(exception instanceof java.io.Serializable);
    }

    public void test_stackTrace() {
        // Test stack trace is properly set
        ResultOffsetExceededException exception = new ResultOffsetExceededException("Stack trace test");

        StackTraceElement[] stackTrace = exception.getStackTrace();
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);

        // Check that the current test method is in the stack trace
        boolean foundTestMethod = false;
        for (StackTraceElement element : stackTrace) {
            if (element.getMethodName().equals("test_stackTrace")) {
                foundTestMethod = true;
                break;
            }
        }
        assertTrue(foundTestMethod);
    }

    public void test_throwAndCatch() {
        // Test throwing and catching the exception
        String expectedMessage = "Offset limit exceeded: 10000";

        try {
            throw new ResultOffsetExceededException(expectedMessage);
        } catch (ResultOffsetExceededException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertNull(e.getCause());
        } catch (Exception e) {
            fail("Should have caught ResultOffsetExceededException");
        }
    }

    public void test_throwAndCatchAsFessSystemException() {
        // Test catching as parent class FessSystemException
        String expectedMessage = "Caught as FessSystemException";

        try {
            throw new ResultOffsetExceededException(expectedMessage);
        } catch (FessSystemException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertTrue(e instanceof ResultOffsetExceededException);
        } catch (Exception e) {
            fail("Should have caught as FessSystemException");
        }
    }

    public void test_throwAndCatchAsRuntimeException() {
        // Test catching as RuntimeException
        String expectedMessage = "Caught as RuntimeException";

        try {
            throw new ResultOffsetExceededException(expectedMessage);
        } catch (RuntimeException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertTrue(e instanceof ResultOffsetExceededException);
        } catch (Exception e) {
            fail("Should have caught as RuntimeException");
        }
    }

    public void test_toString() {
        // Test toString method
        String message = "Test toString method";
        ResultOffsetExceededException exception = new ResultOffsetExceededException(message);

        String result = exception.toString();
        assertNotNull(result);
        assertTrue(result.contains(ResultOffsetExceededException.class.getName()));
        assertTrue(result.contains(message));
    }

    public void test_multipleInstances() {
        // Test creating multiple instances
        ResultOffsetExceededException exception1 = new ResultOffsetExceededException("First instance");
        ResultOffsetExceededException exception2 = new ResultOffsetExceededException("Second instance");
        ResultOffsetExceededException exception3 = new ResultOffsetExceededException("Third instance");

        assertNotSame(exception1, exception2);
        assertNotSame(exception2, exception3);
        assertNotSame(exception1, exception3);

        assertEquals("First instance", exception1.getMessage());
        assertEquals("Second instance", exception2.getMessage());
        assertEquals("Third instance", exception3.getMessage());
    }

    public void test_specialCharactersInMessage() {
        // Test with special characters in message
        String messageWithSpecialChars = "Error: offset > 1000 && offset < 2000 | \"quotes\" 'single' \n\t tab";
        ResultOffsetExceededException exception = new ResultOffsetExceededException(messageWithSpecialChars);

        assertEquals(messageWithSpecialChars, exception.getMessage());
    }

    public void test_unicodeInMessage() {
        // Test with Unicode characters in message
        String unicodeMessage = "オフセットが制限を超えました: 結果 範囲外 😱 ⚠️";
        ResultOffsetExceededException exception = new ResultOffsetExceededException(unicodeMessage);

        assertEquals(unicodeMessage, exception.getMessage());
    }

    public void test_getMessage() {
        // Test getMessage method explicitly
        String testMessage = "Testing getMessage method";
        ResultOffsetExceededException exception = new ResultOffsetExceededException(testMessage);

        String retrievedMessage = exception.getMessage();
        assertEquals(testMessage, retrievedMessage);

        // Test that multiple calls return the same message
        assertEquals(retrievedMessage, exception.getMessage());
        assertEquals(retrievedMessage, exception.getMessage());
    }

    public void test_fillInStackTrace() {
        // Test fillInStackTrace method
        ResultOffsetExceededException exception = new ResultOffsetExceededException("Stack trace test");

        StackTraceElement[] originalStackTrace = exception.getStackTrace();
        Throwable result = exception.fillInStackTrace();
        StackTraceElement[] newStackTrace = exception.getStackTrace();

        assertSame(exception, result);
        assertNotNull(newStackTrace);
        assertTrue(newStackTrace.length > 0);
    }

    public void test_getLocalizedMessage() {
        // Test getLocalizedMessage method
        String message = "Localized message test";
        ResultOffsetExceededException exception = new ResultOffsetExceededException(message);

        // By default, getLocalizedMessage returns the same as getMessage
        assertEquals(message, exception.getLocalizedMessage());
        assertEquals(exception.getMessage(), exception.getLocalizedMessage());
    }

    public void test_suppressedExceptions() {
        // Test suppressed exceptions functionality
        ResultOffsetExceededException mainException = new ResultOffsetExceededException("Main exception");
        ResultOffsetExceededException suppressedException = new ResultOffsetExceededException("Suppressed exception");

        mainException.addSuppressed(suppressedException);

        Throwable[] suppressed = mainException.getSuppressed();
        assertNotNull(suppressed);
        assertEquals(1, suppressed.length);
        assertEquals(suppressedException, suppressed[0]);
        assertEquals("Suppressed exception", suppressed[0].getMessage());
    }
}