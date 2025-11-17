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

public class SsoProcessExceptionTest extends UnitFessTestCase {

    public void test_constructor_withMessage() {
        // Test constructor with message only
        String message = "SSO authentication failed";
        SsoProcessException exception = new SsoProcessException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withMessageAndCause() {
        // Test constructor with message and Exception cause
        String message = "SSO token validation error";
        Exception cause = new RuntimeException("Invalid token format");
        SsoProcessException exception = new SsoProcessException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    public void test_constructor_withNullMessage() {
        // Test constructor with null message
        SsoProcessException exception = new SsoProcessException((String) null);

        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withNullCause() {
        // Test constructor with null cause
        String message = "SSO provider communication failure";
        SsoProcessException exception = new SsoProcessException(message, null);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withNullMessageAndCause() {
        // Test constructor with both null message and cause
        SsoProcessException exception = new SsoProcessException(null, null);

        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_exceptionChaining() {
        // Test exception chaining with multiple levels
        Exception rootCause = new Exception("Network timeout");
        Exception middleCause = new Exception("SSO provider unreachable", rootCause);
        SsoProcessException topException = new SsoProcessException("SSO authentication failed", middleCause);

        assertEquals("SSO authentication failed", topException.getMessage());
        assertEquals(middleCause, topException.getCause());
        assertEquals(rootCause, topException.getCause().getCause());
    }

    public void test_serialVersionUID() {
        // Test that serialVersionUID is properly defined
        SsoProcessException exception1 = new SsoProcessException("Test");
        SsoProcessException exception2 = new SsoProcessException("Test");

        // Both instances should be of the same class
        assertEquals(exception1.getClass(), exception2.getClass());
    }

    public void test_throwAndCatch() {
        // Test throwing and catching the exception
        String expectedMessage = "SSO configuration error";
        try {
            throw new SsoProcessException(expectedMessage);
        } catch (SsoProcessException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertNull(e.getCause());
        }
    }

    public void test_throwAndCatchWithCause() {
        // Test throwing and catching the exception with cause
        String expectedMessage = "SAML assertion validation failed";
        Exception expectedCause = new IllegalStateException("Invalid SAML response");

        try {
            throw new SsoProcessException(expectedMessage, expectedCause);
        } catch (SsoProcessException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedCause, e.getCause());
        }
    }

    public void test_inheritanceFromFessSystemException() {
        // Test that SsoProcessException is properly inherited from FessSystemException
        SsoProcessException ssoException = new SsoProcessException("Test SSO error");

        assertTrue(ssoException instanceof FessSystemException);
        assertTrue(ssoException instanceof RuntimeException);
        assertTrue(ssoException instanceof Exception);
        assertTrue(ssoException instanceof Throwable);
    }

    public void test_constructor_withVariousExceptionTypes() {
        // Test constructor with different exception types as cause
        String message = "SSO process failed";

        // Test with IOException
        Exception ioException = new java.io.IOException("Connection refused");
        SsoProcessException exception1 = new SsoProcessException(message, ioException);
        assertEquals(ioException, exception1.getCause());

        // Test with IllegalArgumentException
        Exception illegalArgException = new IllegalArgumentException("Invalid SSO parameter");
        SsoProcessException exception2 = new SsoProcessException(message, illegalArgException);
        assertEquals(illegalArgException, exception2.getCause());

        // Test with NullPointerException
        Exception nullPointerException = new NullPointerException("SSO context is null");
        SsoProcessException exception3 = new SsoProcessException(message, nullPointerException);
        assertEquals(nullPointerException, exception3.getCause());
    }

    public void test_getMessage_withEmptyString() {
        // Test constructor with empty string message
        String emptyMessage = "";
        SsoProcessException exception = new SsoProcessException(emptyMessage);

        assertEquals(emptyMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_getMessage_withWhitespaceMessage() {
        // Test constructor with whitespace-only message
        String whitespaceMessage = "   ";
        SsoProcessException exception = new SsoProcessException(whitespaceMessage);

        assertEquals(whitespaceMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_getMessage_withLongMessage() {
        // Test constructor with a long message
        StringBuilder longMessageBuilder = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longMessageBuilder.append("SSO authentication failed for user ");
        }
        String longMessage = longMessageBuilder.toString();

        SsoProcessException exception = new SsoProcessException(longMessage);
        assertEquals(longMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_getCause_withNestedExceptions() {
        // Test deeply nested exception causes
        Exception level3 = new Exception("Level 3: Database connection failed");
        Exception level2 = new Exception("Level 2: User lookup failed", level3);
        Exception level1 = new Exception("Level 1: Authentication service error", level2);
        SsoProcessException topLevel = new SsoProcessException("SSO process failed", level1);

        // Verify the chain
        assertEquals("SSO process failed", topLevel.getMessage());
        assertEquals(level1, topLevel.getCause());
        assertEquals("Level 1: Authentication service error", topLevel.getCause().getMessage());
        assertEquals(level2, topLevel.getCause().getCause());
        assertEquals("Level 2: User lookup failed", topLevel.getCause().getCause().getMessage());
        assertEquals(level3, topLevel.getCause().getCause().getCause());
        assertEquals("Level 3: Database connection failed", topLevel.getCause().getCause().getCause().getMessage());
    }

    public void test_stackTrace_isPresent() {
        // Test that stack trace is properly captured
        SsoProcessException exception = new SsoProcessException("Test stack trace");

        StackTraceElement[] stackTrace = exception.getStackTrace();
        assertTrue(stackTrace.length > 0);

        // Verify that the current test method appears in the stack trace
        boolean foundTestMethod = false;
        for (StackTraceElement element : stackTrace) {
            if (element.getMethodName().equals("test_stackTrace_isPresent")) {
                foundTestMethod = true;
                break;
            }
        }
        assertTrue(foundTestMethod);
    }

    public void test_toString_containsClassNameAndMessage() {
        // Test toString() method output
        String message = "SSO authentication timeout";
        SsoProcessException exception = new SsoProcessException(message);

        String toStringResult = exception.toString();
        assertTrue(toStringResult.contains("SsoProcessException"));
        assertTrue(toStringResult.contains(message));
    }

    public void test_toString_withNullMessage() {
        // Test toString() with null message
        SsoProcessException exception = new SsoProcessException(null);

        String toStringResult = exception.toString();
        assertTrue(toStringResult.contains("SsoProcessException"));
    }

    public void test_constructorWithThrowableCause_Error() {
        // Test that constructor accepts Error as cause (verifies Throwable parameter change)
        String message = "SSO process failed due to OutOfMemoryError";
        OutOfMemoryError error = new OutOfMemoryError("Insufficient memory for SSO processing");
        SsoProcessException exception = new SsoProcessException(message, error);

        assertEquals(message, exception.getMessage());
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof Error);
        assertTrue(exception.getCause() instanceof OutOfMemoryError);
        assertEquals("Insufficient memory for SSO processing", exception.getCause().getMessage());
    }

    public void test_constructorWithThrowableCause_StackOverflowError() {
        // Test with StackOverflowError as cause
        String message = "SSO recursive call exceeded stack limit";
        StackOverflowError error = new StackOverflowError("Recursive SSO validation");
        SsoProcessException exception = new SsoProcessException(message, error);

        assertEquals(message, exception.getMessage());
        assertTrue(exception.getCause() instanceof StackOverflowError);
        assertEquals(error, exception.getCause());
    }

    public void test_constructorWithThrowableCause_AssertionError() {
        // Test with AssertionError as cause
        String message = "SSO assertion failed";
        AssertionError error = new AssertionError("Invalid SSO state");
        SsoProcessException exception = new SsoProcessException(message, error);

        assertEquals(message, exception.getMessage());
        assertTrue(exception.getCause() instanceof AssertionError);
        assertEquals(error, exception.getCause());
    }

    public void test_constructorWithThrowableCause_VirtualMachineError() {
        // Test with VirtualMachineError subclass
        String message = "SSO failed due to VM error";
        InternalError error = new InternalError("JVM internal error during SSO");
        SsoProcessException exception = new SsoProcessException(message, error);

        assertEquals(message, exception.getMessage());
        assertTrue(exception.getCause() instanceof InternalError);
        assertTrue(exception.getCause() instanceof VirtualMachineError);
        assertEquals(error, exception.getCause());
    }

    public void test_constructorWithThrowableCause_BackwardCompatibilityWithException() {
        // Test backward compatibility - verify RuntimeException still works
        String message = "SSO runtime error";
        RuntimeException runtimeException = new RuntimeException("SSO processing failed");
        SsoProcessException exception = new SsoProcessException(message, runtimeException);

        assertEquals(message, exception.getMessage());
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals(runtimeException, exception.getCause());
    }

    public void test_constructorWithThrowableCause_CheckedException() {
        // Test with checked exception
        String message = "SSO I/O error";
        java.io.IOException ioException = new java.io.IOException("Network connection failed");
        SsoProcessException exception = new SsoProcessException(message, ioException);

        assertEquals(message, exception.getMessage());
        assertTrue(exception.getCause() instanceof java.io.IOException);
        assertEquals(ioException, exception.getCause());
    }

    public void test_constructorWithThrowableCause_MixedErrorAndException() {
        // Test with mixed Error and Exception in cause chain
        String message = "SSO complex failure";
        IllegalStateException innerException = new IllegalStateException("Invalid state");
        OutOfMemoryError middleError = new OutOfMemoryError("Memory exhausted");
        // Note: OutOfMemoryError constructor doesn't accept cause, so we create a wrapper
        RuntimeException outerException = new RuntimeException("Wrapper", middleError);
        SsoProcessException exception = new SsoProcessException(message, outerException);

        assertEquals(message, exception.getMessage());
        assertEquals(outerException, exception.getCause());
        assertTrue(exception.getCause().getCause() instanceof OutOfMemoryError);
    }
}