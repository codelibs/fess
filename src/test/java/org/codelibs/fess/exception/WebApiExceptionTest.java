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

public class WebApiExceptionTest extends UnitFessTestCase {

    @Test
    public void test_constructor_withStatusCodeAndMessage() {
        // Test constructor with status code and message
        int statusCode = 404;
        String message = "Resource not found";

        WebApiException exception = new WebApiException(statusCode, message);

        assertEquals(statusCode, exception.getStatusCode());
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withStatusCodeMessageAndCause() {
        // Test constructor with status code, message, and cause
        int statusCode = 500;
        String message = "Internal server error";
        Exception cause = new RuntimeException("Database connection failed");

        WebApiException exception = new WebApiException(statusCode, message, cause);

        assertEquals(statusCode, exception.getStatusCode());
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void test_constructor_withStatusCodeAndException() {
        // Test constructor with status code and exception
        int statusCode = 503;
        String causeMessage = "Service temporarily unavailable";
        Exception cause = new IllegalStateException(causeMessage);

        WebApiException exception = new WebApiException(statusCode, cause);

        assertEquals(statusCode, exception.getStatusCode());
        assertEquals(causeMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void test_constructor_withStatusCodeAndNullException() {
        // Test constructor with status code and null exception
        int statusCode = 400;
        NullPointerException cause = new NullPointerException();

        WebApiException exception = new WebApiException(statusCode, cause);

        assertEquals(statusCode, exception.getStatusCode());
        assertNull(exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void test_statusCode_variousHttpCodes() {
        // Test with various HTTP status codes
        int[] statusCodes = { 200, 201, 400, 401, 403, 404, 500, 502, 503 };

        for (int statusCode : statusCodes) {
            WebApiException exception = new WebApiException(statusCode, "Test message");
            assertEquals(statusCode, exception.getStatusCode());
        }
    }

    @Test
    public void test_statusCode_negativeValue() {
        // Test with negative status code
        int statusCode = -1;
        String message = "Invalid status";

        WebApiException exception = new WebApiException(statusCode, message);

        assertEquals(statusCode, exception.getStatusCode());
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void test_statusCode_zero() {
        // Test with zero status code
        int statusCode = 0;
        String message = "Zero status";

        WebApiException exception = new WebApiException(statusCode, message);

        assertEquals(statusCode, exception.getStatusCode());
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void test_message_emptyString() {
        // Test with empty message string
        int statusCode = 404;
        String message = "";

        WebApiException exception = new WebApiException(statusCode, message);

        assertEquals(statusCode, exception.getStatusCode());
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void test_message_nullString() {
        // Test with null message string
        int statusCode = 500;
        String message = null;

        WebApiException exception = new WebApiException(statusCode, message);

        assertEquals(statusCode, exception.getStatusCode());
        assertNull(exception.getMessage());
    }

    @Test
    public void test_inheritance_fromFessSystemException() {
        // Test that WebApiException is a subclass of FessSystemException
        WebApiException exception = new WebApiException(500, "Test");

        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    public void test_serializable() {
        // Test that the exception has serialVersionUID
        WebApiException exception = new WebApiException(404, "Not Found");

        // This test confirms the exception can be created and has standard exception properties
        assertNotNull(exception);
        assertTrue(exception instanceof java.io.Serializable);
    }

    @Test
    public void test_stackTrace() {
        // Test that stack trace is preserved
        int statusCode = 500;
        String message = "Error occurred";

        WebApiException exception = new WebApiException(statusCode, message);

        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
    }

    @Test
    public void test_nestedExceptions() {
        // Test nested exceptions
        Exception innerCause = new IllegalArgumentException("Invalid argument");
        Exception middleCause = new RuntimeException("Runtime error", innerCause);
        WebApiException exception = new WebApiException(400, middleCause);

        assertEquals(400, exception.getStatusCode());
        assertEquals("Runtime error", exception.getMessage());
        assertEquals(middleCause, exception.getCause());
        assertEquals(innerCause, exception.getCause().getCause());
    }

    @Test
    public void test_throwAndCatch() {
        // Test throwing and catching the exception
        int expectedStatusCode = 403;
        String expectedMessage = "Access denied";

        try {
            throw new WebApiException(expectedStatusCode, expectedMessage);
        } catch (WebApiException e) {
            assertEquals(expectedStatusCode, e.getStatusCode());
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    public void test_throwWithCauseAndCatch() {
        // Test throwing with cause and catching
        int expectedStatusCode = 500;
        String expectedMessage = "Database error";
        Exception expectedCause = new RuntimeException("Connection timeout");

        try {
            throw new WebApiException(expectedStatusCode, expectedMessage, expectedCause);
        } catch (WebApiException e) {
            assertEquals(expectedStatusCode, e.getStatusCode());
            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedCause, e.getCause());
        }
    }

    @Test
    public void test_multipleExceptions_differentStatusCodes() {
        // Test creating multiple exceptions with different status codes
        WebApiException exception1 = new WebApiException(400, "Bad Request");
        WebApiException exception2 = new WebApiException(401, "Unauthorized");
        WebApiException exception3 = new WebApiException(500, "Internal Error");

        assertEquals(400, exception1.getStatusCode());
        assertEquals(401, exception2.getStatusCode());
        assertEquals(500, exception3.getStatusCode());

        assertTrue(exception1.getStatusCode() != exception2.getStatusCode());
        assertTrue(exception2.getStatusCode() != exception3.getStatusCode());
    }

    @Test
    public void test_exceptionWithLongMessage() {
        // Test with very long message
        int statusCode = 413;
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longMessage.append("Very long error message content. ");
        }
        String message = longMessage.toString();

        WebApiException exception = new WebApiException(statusCode, message);

        assertEquals(statusCode, exception.getStatusCode());
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void test_exceptionWithSpecialCharactersInMessage() {
        // Test with special characters in message
        int statusCode = 400;
        String message = "Error: \"Invalid JSON\" - {key: 'value'} \n\t@#$%^&*()";

        WebApiException exception = new WebApiException(statusCode, message);

        assertEquals(statusCode, exception.getStatusCode());
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void test_exceptionWithUnicodeMessage() {
        // Test with unicode characters in message
        int statusCode = 422;
        String message = "エラー: 無効なリクエスト 错误：无效请求 오류: 잘못된 요청";

        WebApiException exception = new WebApiException(statusCode, message);

        assertEquals(statusCode, exception.getStatusCode());
        assertEquals(message, exception.getMessage());
    }
}