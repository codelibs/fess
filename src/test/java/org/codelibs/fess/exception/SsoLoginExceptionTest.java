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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.codelibs.fess.unit.UnitFessTestCase;

public class SsoLoginExceptionTest extends UnitFessTestCase {

    public void test_constructor_withMessage() {
        // Test constructor with message only
        String message = "SSO login failed";
        SsoLoginException exception = new SsoLoginException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withMessageAndCause() {
        // Test constructor with message and cause
        String message = "SSO authentication error";
        Exception cause = new RuntimeException("Token validation failed");
        SsoLoginException exception = new SsoLoginException(message, cause);

        assertEquals(message, exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals(cause, exception.getCause());
        assertEquals("Token validation failed", exception.getCause().getMessage());
    }

    public void test_constructor_withNullMessage() {
        // Test constructor with null message
        SsoLoginException exception = new SsoLoginException(null);

        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withNullMessageAndCause() {
        // Test constructor with null message and null cause
        SsoLoginException exception = new SsoLoginException(null, null);

        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withEmptyMessage() {
        // Test constructor with empty message
        String message = "";
        SsoLoginException exception = new SsoLoginException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withMessageAndNullCause() {
        // Test constructor with message and null cause
        String message = "SSO configuration error";
        SsoLoginException exception = new SsoLoginException(message, null);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withDifferentExceptionTypes() {
        // Test with different exception types as cause
        String message = "SSO provider communication error";

        // Test with IOException
        IOException ioException = new IOException("Network error");
        SsoLoginException exception1 = new SsoLoginException(message, ioException);
        assertEquals(message, exception1.getMessage());
        assertTrue(exception1.getCause() instanceof IOException);

        // Test with IllegalArgumentException
        IllegalArgumentException illegalArgException = new IllegalArgumentException("Invalid parameter");
        SsoLoginException exception2 = new SsoLoginException(message, illegalArgException);
        assertEquals(message, exception2.getMessage());
        assertTrue(exception2.getCause() instanceof IllegalArgumentException);

        // Test with NullPointerException
        NullPointerException nullPointerException = new NullPointerException("Null reference");
        SsoLoginException exception3 = new SsoLoginException(message, nullPointerException);
        assertEquals(message, exception3.getMessage());
        assertTrue(exception3.getCause() instanceof NullPointerException);
    }

    public void test_inheritance() {
        // Test that SsoLoginException extends FessSystemException
        SsoLoginException exception = new SsoLoginException("Test");

        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    public void test_stackTrace() {
        // Test that stack trace is properly captured
        SsoLoginException exception = new SsoLoginException("Stack trace test");
        StackTraceElement[] stackTrace = exception.getStackTrace();

        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);

        // Verify the top of the stack trace is from this test class
        StackTraceElement topElement = stackTrace[0];
        assertEquals(this.getClass().getName(), topElement.getClassName());
    }

    public void test_nestedExceptions() {
        // Test nested exception handling
        Exception level3 = new RuntimeException("Level 3 error");
        Exception level2 = new IllegalStateException("Level 2 error", level3);
        Exception level1 = new IOException("Level 1 error", level2);
        SsoLoginException exception = new SsoLoginException("Top level SSO error", level1);

        assertEquals("Top level SSO error", exception.getMessage());

        // Verify the exception chain
        Throwable cause1 = exception.getCause();
        assertNotNull(cause1);
        assertTrue(cause1 instanceof IOException);
        assertEquals("Level 1 error", cause1.getMessage());

        Throwable cause2 = cause1.getCause();
        assertNotNull(cause2);
        assertTrue(cause2 instanceof IllegalStateException);
        assertEquals("Level 2 error", cause2.getMessage());

        Throwable cause3 = cause2.getCause();
        assertNotNull(cause3);
        assertTrue(cause3 instanceof RuntimeException);
        assertEquals("Level 3 error", cause3.getMessage());

        assertNull(cause3.getCause());
    }

    public void test_serialization() throws Exception {
        // Test serialization and deserialization
        String message = "Serializable SSO exception";
        Exception cause = new RuntimeException("Serializable cause");
        SsoLoginException originalException = new SsoLoginException(message, cause);

        // Serialize the exception
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(originalException);
        oos.close();

        // Deserialize the exception
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        SsoLoginException deserializedException = (SsoLoginException) ois.readObject();
        ois.close();

        // Verify the deserialized exception
        assertEquals(originalException.getMessage(), deserializedException.getMessage());
        assertNotNull(deserializedException.getCause());
        assertEquals(originalException.getCause().getMessage(), deserializedException.getCause().getMessage());
    }

    public void test_getMessage() {
        // Test getMessage() method
        String message = "Custom SSO error message";
        SsoLoginException exception = new SsoLoginException(message);

        assertEquals(message, exception.getMessage());

        // Test with special characters
        String specialMessage = "SSO error: 特殊文字 & symbols <>\n\t";
        SsoLoginException specialException = new SsoLoginException(specialMessage);
        assertEquals(specialMessage, specialException.getMessage());
    }

    public void test_toString() {
        // Test toString() method
        String message = "SSO toString test";
        SsoLoginException exception = new SsoLoginException(message);

        String toStringResult = exception.toString();
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains(SsoLoginException.class.getName()));
        assertTrue(toStringResult.contains(message));
    }

    public void test_longMessage() {
        // Test with very long message
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longMessage.append("Long SSO error message part ").append(i).append(" ");
        }
        String message = longMessage.toString();

        SsoLoginException exception = new SsoLoginException(message);
        assertEquals(message, exception.getMessage());
    }

    public void test_getCause() {
        // Test getCause() method explicitly
        Exception cause = new IllegalArgumentException("Invalid SSO token");
        SsoLoginException exception = new SsoLoginException("SSO error", cause);

        Throwable retrievedCause = exception.getCause();
        assertNotNull(retrievedCause);
        assertSame(cause, retrievedCause);
        assertEquals("Invalid SSO token", retrievedCause.getMessage());
    }

    public void test_multipleInstances() {
        // Test creating multiple instances
        SsoLoginException exception1 = new SsoLoginException("Error 1");
        SsoLoginException exception2 = new SsoLoginException("Error 2");
        SsoLoginException exception3 = new SsoLoginException("Error 3", new RuntimeException());

        assertNotSame(exception1, exception2);
        assertNotSame(exception2, exception3);
        assertNotSame(exception1, exception3);

        assertEquals("Error 1", exception1.getMessage());
        assertEquals("Error 2", exception2.getMessage());
        assertEquals("Error 3", exception3.getMessage());
    }

    public void test_constructorWithThrowableCause_OutOfMemoryError() {
        // Test that constructor accepts Error as cause (verifies Throwable parameter change)
        String message = "SSO login failed due to memory error";
        OutOfMemoryError error = new OutOfMemoryError("Not enough memory for SSO login");
        SsoLoginException exception = new SsoLoginException(message, error);

        assertEquals(message, exception.getMessage());
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof Error);
        assertTrue(exception.getCause() instanceof OutOfMemoryError);
        assertEquals("Not enough memory for SSO login", exception.getCause().getMessage());
    }

    public void test_constructorWithThrowableCause_StackOverflowError() {
        // Test with StackOverflowError as cause
        String message = "SSO login recursive overflow";
        StackOverflowError error = new StackOverflowError("Stack overflow in authentication chain");
        SsoLoginException exception = new SsoLoginException(message, error);

        assertEquals(message, exception.getMessage());
        assertTrue(exception.getCause() instanceof StackOverflowError);
        assertEquals(error, exception.getCause());
    }

    public void test_constructorWithThrowableCause_AssertionError() {
        // Test with AssertionError as cause
        String message = "SSO login assertion violation";
        AssertionError error = new AssertionError("Security assertion failed");
        SsoLoginException exception = new SsoLoginException(message, error);

        assertEquals(message, exception.getMessage());
        assertTrue(exception.getCause() instanceof AssertionError);
        assertEquals(error, exception.getCause());
    }

    public void test_constructorWithThrowableCause_LinkageError() {
        // Test with LinkageError subclass
        String message = "SSO login class loading error";
        NoClassDefFoundError error = new NoClassDefFoundError("SSO provider class not found");
        SsoLoginException exception = new SsoLoginException(message, error);

        assertEquals(message, exception.getMessage());
        assertTrue(exception.getCause() instanceof NoClassDefFoundError);
        assertTrue(exception.getCause() instanceof LinkageError);
        assertEquals(error, exception.getCause());
    }

    public void test_constructorWithThrowableCause_BackwardCompatibility() {
        // Verify backward compatibility with Exception parameter
        String message = "SSO login failed";

        // Test with various Exception types
        IOException ioException = new IOException("Connection failed");
        SsoLoginException exception1 = new SsoLoginException(message, ioException);
        assertTrue(exception1.getCause() instanceof IOException);

        IllegalArgumentException argException = new IllegalArgumentException("Invalid token");
        SsoLoginException exception2 = new SsoLoginException(message, argException);
        assertTrue(exception2.getCause() instanceof IllegalArgumentException);

        RuntimeException runtimeException = new RuntimeException("Runtime error");
        SsoLoginException exception3 = new SsoLoginException(message, runtimeException);
        assertTrue(exception3.getCause() instanceof RuntimeException);
    }

    public void test_constructorWithThrowableCause_NullError() {
        // Test with null Error (should work same as null Exception)
        String message = "SSO login error with null cause";
        Error nullError = null;
        SsoLoginException exception = new SsoLoginException(message, nullError);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructorWithThrowableCause_ChainedErrorsAndExceptions() {
        // Test with chain containing both Errors and Exceptions
        String message = "SSO login complex error chain";
        IllegalStateException innerException = new IllegalStateException("Bad state");
        AssertionError middleError = new AssertionError("Assertion failed", innerException);
        RuntimeException outerException = new RuntimeException("Wrapper exception", middleError);
        SsoLoginException exception = new SsoLoginException(message, outerException);

        assertEquals(message, exception.getMessage());
        Throwable cause1 = exception.getCause();
        assertTrue(cause1 instanceof RuntimeException);

        Throwable cause2 = cause1.getCause();
        assertTrue(cause2 instanceof AssertionError);

        Throwable cause3 = cause2.getCause();
        assertTrue(cause3 instanceof IllegalStateException);
    }
}