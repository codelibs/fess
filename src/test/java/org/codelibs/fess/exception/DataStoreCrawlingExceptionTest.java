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

public class DataStoreCrawlingExceptionTest extends UnitFessTestCase {

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
}