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

public class ThumbnailGenerationExceptionTest extends UnitFessTestCase {

    public void test_constructorWithMessage() {
        // Test constructor with message only
        String message = "Thumbnail generation failed";
        ThumbnailGenerationException exception = new ThumbnailGenerationException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructorWithMessageAndCause() {
        // Test constructor with message and cause
        String message = "Failed to generate thumbnail for document";
        Exception cause = new RuntimeException("Image processing error");
        ThumbnailGenerationException exception = new ThumbnailGenerationException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals("Image processing error", exception.getCause().getMessage());
    }

    public void test_constructorWithNullMessage() {
        // Test constructor with null message
        ThumbnailGenerationException exception = new ThumbnailGenerationException(null);

        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructorWithNullMessageAndCause() {
        // Test constructor with null message and null cause
        ThumbnailGenerationException exception = new ThumbnailGenerationException(null, null);

        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructorWithEmptyMessage() {
        // Test constructor with empty message
        String message = "";
        ThumbnailGenerationException exception = new ThumbnailGenerationException(message);

        assertEquals("", exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructorWithMessageAndNullCause() {
        // Test constructor with message and null cause
        String message = "Thumbnail error";
        ThumbnailGenerationException exception = new ThumbnailGenerationException(message, null);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_instanceOfFessSystemException() {
        // Test that ThumbnailGenerationException is an instance of FessSystemException
        ThumbnailGenerationException exception = new ThumbnailGenerationException("Test");

        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    public void test_stackTraceForMessageOnlyConstructor() {
        // Test that stack trace is not writable when using message-only constructor
        ThumbnailGenerationException exception = new ThumbnailGenerationException("Test message");

        // The message-only constructor uses super(message, false, false) which disables stack trace
        assertEquals(0, exception.getStackTrace().length);
    }

    public void test_stackTraceForMessageAndCauseConstructor() {
        // Test that stack trace is available when using message and cause constructor
        ThumbnailGenerationException exception = new ThumbnailGenerationException("Test", new Exception());

        // The message and cause constructor uses normal super call, so stack trace should be available
        assertTrue(exception.getStackTrace().length > 0);
    }

    public void test_multipleNestedExceptions() {
        // Test with multiple nested exceptions
        Exception innerCause = new IllegalArgumentException("Invalid image format");
        Exception middleCause = new RuntimeException("Processing failed", innerCause);
        ThumbnailGenerationException exception = new ThumbnailGenerationException("Thumbnail generation error", middleCause);

        assertEquals("Thumbnail generation error", exception.getMessage());
        assertEquals(middleCause, exception.getCause());
        assertEquals("Processing failed", exception.getCause().getMessage());
        assertEquals(innerCause, exception.getCause().getCause());
        assertEquals("Invalid image format", exception.getCause().getCause().getMessage());
    }

    public void test_serialization() {
        // Test that the exception has serialVersionUID
        ThumbnailGenerationException exception = new ThumbnailGenerationException("Test serialization");

        // The exception should be serializable as it extends RuntimeException
        assertTrue(exception instanceof java.io.Serializable);
    }

    public void test_toString() {
        // Test toString method
        String message = "Thumbnail generation failed for file.jpg";
        ThumbnailGenerationException exception = new ThumbnailGenerationException(message);

        String toStringResult = exception.toString();
        assertTrue(toStringResult.contains(ThumbnailGenerationException.class.getName()));
        assertTrue(toStringResult.contains(message));
    }

    public void test_toStringWithCause() {
        // Test toString method with cause
        String message = "Failed to generate thumbnail";
        Exception cause = new RuntimeException("IO Error");
        ThumbnailGenerationException exception = new ThumbnailGenerationException(message, cause);

        String toStringResult = exception.toString();
        assertTrue(toStringResult.contains(ThumbnailGenerationException.class.getName()));
        assertTrue(toStringResult.contains(message));
    }
}