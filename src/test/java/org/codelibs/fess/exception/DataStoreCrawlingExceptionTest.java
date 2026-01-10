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

public class DataStoreCrawlingExceptionTest extends UnitFessTestCase {

    @Test
    public void test_constructor_withUrlMessageAndException() {
        // Test with URL, message, and exception (abort defaults to false)
        String url = "http://example.com/test";
        String message = "Test error message";
        Exception cause = new RuntimeException("Root cause");

        DataStoreCrawlingException exception = new DataStoreCrawlingException(url, message, cause);

        assertEquals(url, exception.getUrl());
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertFalse(exception.aborted());
    }

    @Test
    public void test_constructor_withUrlMessageExceptionAndAbortTrue() {
        // Test with URL, message, exception, and abort set to true
        String url = "http://example.com/test2";
        String message = "Critical error message";
        Exception cause = new IllegalStateException("Critical error");
        boolean abort = true;

        DataStoreCrawlingException exception = new DataStoreCrawlingException(url, message, cause, abort);

        assertEquals(url, exception.getUrl());
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertTrue(exception.aborted());
    }

    @Test
    public void test_constructor_withUrlMessageExceptionAndAbortFalse() {
        // Test with URL, message, exception, and abort explicitly set to false
        String url = "http://example.com/test3";
        String message = "Non-critical error message";
        Exception cause = new RuntimeException("Non-critical error");
        boolean abort = false;

        DataStoreCrawlingException exception = new DataStoreCrawlingException(url, message, cause, abort);

        assertEquals(url, exception.getUrl());
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertFalse(exception.aborted());
    }

    @Test
    public void test_getUrl_withNullUrl() {
        // Test with null URL
        String url = null;
        String message = "Error with null URL";
        Exception cause = new RuntimeException("Error cause");

        DataStoreCrawlingException exception = new DataStoreCrawlingException(url, message, cause);

        assertNull(exception.getUrl());
        assertEquals(message, exception.getMessage());
        assertFalse(exception.aborted());
    }

    @Test
    public void test_getUrl_withEmptyUrl() {
        // Test with empty URL
        String url = "";
        String message = "Error with empty URL";
        Exception cause = new RuntimeException("Error cause");

        DataStoreCrawlingException exception = new DataStoreCrawlingException(url, message, cause);

        assertEquals("", exception.getUrl());
        assertEquals(message, exception.getMessage());
        assertFalse(exception.aborted());
    }

    @Test
    public void test_constructor_withNullMessage() {
        // Test with null message
        String url = "http://example.com/test";
        String message = null;
        Exception cause = new RuntimeException("Error cause");

        DataStoreCrawlingException exception = new DataStoreCrawlingException(url, message, cause);

        assertEquals(url, exception.getUrl());
        assertNull(exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertFalse(exception.aborted());
    }

    @Test
    public void test_constructor_withNullException() {
        // Test with null exception
        String url = "http://example.com/test";
        String message = "Error without cause";
        Exception cause = null;

        DataStoreCrawlingException exception = new DataStoreCrawlingException(url, message, cause);

        assertEquals(url, exception.getUrl());
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertFalse(exception.aborted());
    }

    @Test
    public void test_constructor_withAllNullParameters() {
        // Test with all null parameters except abort
        String url = null;
        String message = null;
        Exception cause = null;
        boolean abort = true;

        DataStoreCrawlingException exception = new DataStoreCrawlingException(url, message, cause, abort);

        assertNull(exception.getUrl());
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception.aborted());
    }

    @Test
    public void test_aborted_multipleCallsReturnSameValue() {
        // Test that multiple calls to aborted() return the same value
        String url = "http://example.com/test";
        String message = "Test message";
        Exception cause = new RuntimeException("Test cause");
        boolean abort = true;

        DataStoreCrawlingException exception = new DataStoreCrawlingException(url, message, cause, abort);

        // Call aborted() multiple times
        assertTrue(exception.aborted());
        assertTrue(exception.aborted());
        assertTrue(exception.aborted());
    }

    @Test
    public void test_getUrl_multipleCallsReturnSameValue() {
        // Test that multiple calls to getUrl() return the same value
        String url = "http://example.com/test";
        String message = "Test message";
        Exception cause = new RuntimeException("Test cause");

        DataStoreCrawlingException exception = new DataStoreCrawlingException(url, message, cause);

        // Call getUrl() multiple times
        assertEquals(url, exception.getUrl());
        assertEquals(url, exception.getUrl());
        assertEquals(url, exception.getUrl());
    }

    @Test
    public void test_constructor_withLongUrl() {
        // Test with very long URL
        StringBuilder longUrlBuilder = new StringBuilder("http://example.com/");
        for (int i = 0; i < 1000; i++) {
            longUrlBuilder.append("path").append(i).append("/");
        }
        String longUrl = longUrlBuilder.toString();
        String message = "Error with long URL";
        Exception cause = new RuntimeException("Error cause");

        DataStoreCrawlingException exception = new DataStoreCrawlingException(longUrl, message, cause);

        assertEquals(longUrl, exception.getUrl());
        assertEquals(message, exception.getMessage());
        assertFalse(exception.aborted());
    }

    @Test
    public void test_constructor_withLongMessage() {
        // Test with very long message
        String url = "http://example.com/test";
        StringBuilder longMessageBuilder = new StringBuilder("Error: ");
        for (int i = 0; i < 1000; i++) {
            longMessageBuilder.append("detail ").append(i).append(" ");
        }
        String longMessage = longMessageBuilder.toString();
        Exception cause = new RuntimeException("Error cause");

        DataStoreCrawlingException exception = new DataStoreCrawlingException(url, longMessage, cause);

        assertEquals(url, exception.getUrl());
        assertEquals(longMessage, exception.getMessage());
        assertFalse(exception.aborted());
    }

    @Test
    public void test_constructor_withNestedExceptions() {
        // Test with nested exceptions
        String url = "http://example.com/test";
        String message = "Outer error message";
        Exception innerCause = new IllegalArgumentException("Inner cause");
        Exception middleCause = new IllegalStateException("Middle cause", innerCause);
        Exception outerCause = new RuntimeException("Outer cause", middleCause);

        DataStoreCrawlingException exception = new DataStoreCrawlingException(url, message, outerCause);

        assertEquals(url, exception.getUrl());
        assertEquals(message, exception.getMessage());
        assertEquals(outerCause, exception.getCause());
        assertEquals(middleCause, exception.getCause().getCause());
        assertEquals(innerCause, exception.getCause().getCause().getCause());
        assertFalse(exception.aborted());
    }

    @Test
    public void test_constructor_withSpecialCharactersInUrl() {
        // Test with special characters in URL
        String url = "http://example.com/test?param=value&other=value#fragment";
        String message = "Error with special URL";
        Exception cause = new RuntimeException("Error cause");

        DataStoreCrawlingException exception = new DataStoreCrawlingException(url, message, cause);

        assertEquals(url, exception.getUrl());
        assertEquals(message, exception.getMessage());
        assertFalse(exception.aborted());
    }

    @Test
    public void test_constructor_withUnicodeInMessage() {
        // Test with Unicode characters in message
        String url = "http://example.com/test";
        String message = "エラーメッセージ 错误信息 🚨";
        Exception cause = new RuntimeException("Error cause");

        DataStoreCrawlingException exception = new DataStoreCrawlingException(url, message, cause);

        assertEquals(url, exception.getUrl());
        assertEquals(message, exception.getMessage());
        assertFalse(exception.aborted());
    }

    @Test
    public void test_constructorWithThrowableCause_OutOfMemoryError() {
        // Test that constructor accepts Error as cause (verifies Throwable parameter change)
        String url = "http://example.com/large-dataset";
        String message = "Crawling failed due to memory exhaustion";
        OutOfMemoryError error = new OutOfMemoryError("Heap space exhausted");

        DataStoreCrawlingException exception = new DataStoreCrawlingException(url, message, error);

        assertEquals(url, exception.getUrl());
        assertEquals(message, exception.getMessage());
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof Error);
        assertTrue(exception.getCause() instanceof OutOfMemoryError);
        assertEquals("Heap space exhausted", exception.getCause().getMessage());
        assertFalse(exception.aborted());
    }

    @Test
    public void test_constructorWithThrowableCause_OutOfMemoryErrorWithAbort() {
        // Test Error with abort flag
        String url = "http://example.com/critical-resource";
        String message = "Critical memory error during crawling";
        OutOfMemoryError error = new OutOfMemoryError("Cannot allocate memory");

        DataStoreCrawlingException exception = new DataStoreCrawlingException(url, message, error, true);

        assertEquals(url, exception.getUrl());
        assertEquals(message, exception.getMessage());
        assertTrue(exception.getCause() instanceof OutOfMemoryError);
        assertTrue(exception.aborted());
    }

    @Test
    public void test_constructorWithThrowableCause_StackOverflowError() {
        // Test with StackOverflowError
        String url = "http://example.com/recursive-content";
        String message = "Stack overflow during content parsing";
        StackOverflowError error = new StackOverflowError("Recursive parsing detected");

        DataStoreCrawlingException exception = new DataStoreCrawlingException(url, message, error);

        assertEquals(url, exception.getUrl());
        assertTrue(exception.getCause() instanceof StackOverflowError);
        assertEquals(error, exception.getCause());
        assertFalse(exception.aborted());
    }

    @Test
    public void test_constructorWithThrowableCause_AssertionError() {
        // Test with AssertionError
        String url = "http://example.com/invalid-state";
        String message = "Assertion failed during data store access";
        AssertionError error = new AssertionError("Data integrity check failed");

        DataStoreCrawlingException exception = new DataStoreCrawlingException(url, message, error, false);

        assertEquals(url, exception.getUrl());
        assertTrue(exception.getCause() instanceof AssertionError);
        assertEquals(error, exception.getCause());
        assertFalse(exception.aborted());
    }

    @Test
    public void test_constructorWithThrowableCause_LinkageError() {
        // Test with LinkageError subclass
        String url = "http://example.com/data";
        String message = "Class loading error during crawling";
        NoClassDefFoundError error = new NoClassDefFoundError("Parser class not found");

        DataStoreCrawlingException exception = new DataStoreCrawlingException(url, message, error);

        assertEquals(url, exception.getUrl());
        assertTrue(exception.getCause() instanceof NoClassDefFoundError);
        assertTrue(exception.getCause() instanceof LinkageError);
        assertFalse(exception.aborted());
    }

    @Test
    public void test_constructorWithThrowableCause_BackwardCompatibilityWithException() {
        // Verify backward compatibility - Exception types still work
        String url = "http://example.com/test";
        String message = "Crawling error";

        // Test with IOException
        java.io.IOException ioException = new java.io.IOException("Network error");
        DataStoreCrawlingException exception1 = new DataStoreCrawlingException(url, message, ioException);
        assertTrue(exception1.getCause() instanceof java.io.IOException);

        // Test with IllegalArgumentException
        IllegalArgumentException argException = new IllegalArgumentException("Invalid URL format");
        DataStoreCrawlingException exception2 = new DataStoreCrawlingException(url, message, argException);
        assertTrue(exception2.getCause() instanceof IllegalArgumentException);

        // Test with RuntimeException
        RuntimeException runtimeException = new RuntimeException("Unexpected error");
        DataStoreCrawlingException exception3 = new DataStoreCrawlingException(url, message, runtimeException);
        assertTrue(exception3.getCause() instanceof RuntimeException);
    }

    @Test
    public void test_constructorWithThrowableCause_NullError() {
        // Test with null Error
        String url = "http://example.com/test";
        String message = "Error with null cause";
        Error nullError = null;

        DataStoreCrawlingException exception = new DataStoreCrawlingException(url, message, nullError);

        assertEquals(url, exception.getUrl());
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertFalse(exception.aborted());
    }

    @Test
    public void test_constructorWithThrowableCause_ChainedErrorsAndExceptions() {
        // Test with mixed Errors and Exceptions in cause chain
        String url = "http://example.com/complex-error";
        String message = "Complex error during crawling";

        NullPointerException innerException = new NullPointerException("Null reference");
        AssertionError middleError = new AssertionError("Assertion failed", innerException);
        RuntimeException outerException = new RuntimeException("Wrapper", middleError);

        DataStoreCrawlingException exception = new DataStoreCrawlingException(url, message, outerException, true);

        assertEquals(url, exception.getUrl());
        assertEquals(message, exception.getMessage());
        assertTrue(exception.aborted());

        // Verify the cause chain
        Throwable cause1 = exception.getCause();
        assertTrue(cause1 instanceof RuntimeException);

        Throwable cause2 = cause1.getCause();
        assertTrue(cause2 instanceof AssertionError);

        Throwable cause3 = cause2.getCause();
        assertTrue(cause3 instanceof NullPointerException);
    }

    @Test
    public void test_constructorWithThrowableCause_VirtualMachineError() {
        // Test with VirtualMachineError
        String url = "http://example.com/vm-error";
        String message = "VM error during crawling";
        InternalError error = new InternalError("JVM internal error");

        DataStoreCrawlingException exception = new DataStoreCrawlingException(url, message, error);

        assertEquals(url, exception.getUrl());
        assertTrue(exception.getCause() instanceof InternalError);
        assertTrue(exception.getCause() instanceof VirtualMachineError);
        assertFalse(exception.aborted());
    }
}