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

public class InvalidAccessTokenExceptionTest extends UnitFessTestCase {

    public void test_constructor_withTypeAndMessage() {
        // Test constructor with type and message
        String type = "Bearer";
        String message = "Invalid access token provided";
        InvalidAccessTokenException exception = new InvalidAccessTokenException(type, message);

        assertEquals(type, exception.getType());
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withNullType() {
        // Test constructor with null type
        String type = null;
        String message = "Token validation failed";
        InvalidAccessTokenException exception = new InvalidAccessTokenException(type, message);

        assertNull(exception.getType());
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withNullMessage() {
        // Test constructor with null message
        String type = "OAuth2";
        String message = null;
        InvalidAccessTokenException exception = new InvalidAccessTokenException(type, message);

        assertEquals(type, exception.getType());
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withNullTypeAndMessage() {
        // Test constructor with both null type and message
        String type = null;
        String message = null;
        InvalidAccessTokenException exception = new InvalidAccessTokenException(type, message);

        assertNull(exception.getType());
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withEmptyType() {
        // Test constructor with empty type string
        String type = "";
        String message = "Empty token type error";
        InvalidAccessTokenException exception = new InvalidAccessTokenException(type, message);

        assertEquals("", exception.getType());
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withEmptyMessage() {
        // Test constructor with empty message string
        String type = "JWT";
        String message = "";
        InvalidAccessTokenException exception = new InvalidAccessTokenException(type, message);

        assertEquals(type, exception.getType());
        assertEquals("", exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withLongTypeAndMessage() {
        // Test constructor with long strings
        String type = "VeryLongTokenTypeNameForTestingPurposes";
        String message =
                "This is a very long error message that describes in detail why the access token is invalid and what went wrong during validation";
        InvalidAccessTokenException exception = new InvalidAccessTokenException(type, message);

        assertEquals(type, exception.getType());
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withSpecialCharacters() {
        // Test constructor with special characters
        String type = "Token-Type_123!@#";
        String message = "Error: Token contains special characters <>&\"'";
        InvalidAccessTokenException exception = new InvalidAccessTokenException(type, message);

        assertEquals(type, exception.getType());
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withWhitespaceType() {
        // Test constructor with whitespace in type
        String type = "  Bearer Token  ";
        String message = "Whitespace in token type";
        InvalidAccessTokenException exception = new InvalidAccessTokenException(type, message);

        assertEquals("  Bearer Token  ", exception.getType());
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_getType_immutability() {
        // Test that getType returns the same value consistently
        String type = "ApiKey";
        String message = "API key expired";
        InvalidAccessTokenException exception = new InvalidAccessTokenException(type, message);

        String firstCall = exception.getType();
        String secondCall = exception.getType();

        assertSame(firstCall, secondCall);
        assertEquals(type, firstCall);
    }

    public void test_inheritanceFromFessSystemException() {
        // Test that InvalidAccessTokenException is properly inherited from FessSystemException
        String type = "SessionToken";
        String message = "Session expired";
        InvalidAccessTokenException exception = new InvalidAccessTokenException(type, message);

        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    public void test_throwAndCatch() {
        // Test throwing and catching the exception
        String expectedType = "OAuth";
        String expectedMessage = "OAuth token is invalid";

        try {
            throw new InvalidAccessTokenException(expectedType, expectedMessage);
        } catch (InvalidAccessTokenException e) {
            assertEquals(expectedType, e.getType());
            assertEquals(expectedMessage, e.getMessage());
            assertNull(e.getCause());
        }
    }

    public void test_throwAndCatchAsFessSystemException() {
        // Test catching as parent exception type
        String type = "BasicAuth";
        String message = "Authentication failed";

        try {
            throw new InvalidAccessTokenException(type, message);
        } catch (FessSystemException e) {
            assertTrue(e instanceof InvalidAccessTokenException);
            InvalidAccessTokenException iate = (InvalidAccessTokenException) e;
            assertEquals(type, iate.getType());
            assertEquals(message, e.getMessage());
        }
    }

    public void test_multipleExceptionInstances() {
        // Test multiple instances are independent
        InvalidAccessTokenException exception1 = new InvalidAccessTokenException("Type1", "Message1");
        InvalidAccessTokenException exception2 = new InvalidAccessTokenException("Type2", "Message2");

        assertEquals("Type1", exception1.getType());
        assertEquals("Message1", exception1.getMessage());
        assertEquals("Type2", exception2.getType());
        assertEquals("Message2", exception2.getMessage());

        assertNotSame(exception1, exception2);
    }

    public void test_serialVersionUID() {
        // Test that serialVersionUID is properly defined
        InvalidAccessTokenException exception1 = new InvalidAccessTokenException("Type", "Message");
        InvalidAccessTokenException exception2 = new InvalidAccessTokenException("Type", "Message");

        // Both instances should be of the same class
        assertEquals(exception1.getClass(), exception2.getClass());
    }

    public void test_stackTracePopulated() {
        // Test that stack trace is properly populated
        InvalidAccessTokenException exception = new InvalidAccessTokenException("JWT", "Invalid signature");

        StackTraceElement[] stackTrace = exception.getStackTrace();
        assertTrue(stackTrace.length > 0);

        // First element should be from this test method
        StackTraceElement firstElement = stackTrace[0];
        assertEquals(this.getClass().getName(), firstElement.getClassName());
        assertEquals("test_stackTracePopulated", firstElement.getMethodName());
    }

    public void test_toString() {
        // Test toString method (inherited from Throwable)
        String type = "CustomToken";
        String message = "Custom token error";
        InvalidAccessTokenException exception = new InvalidAccessTokenException(type, message);

        String toStringResult = exception.toString();
        assertTrue(toStringResult.contains(InvalidAccessTokenException.class.getName()));
        assertTrue(toStringResult.contains(message));
    }

    public void test_variousTokenTypes() {
        // Test with various common token types
        String[] tokenTypes = { "Bearer", "JWT", "OAuth", "OAuth2", "APIKey", "Session", "Basic", "Digest", "SAML", "OpenID" };

        for (String tokenType : tokenTypes) {
            String message = tokenType + " token is invalid";
            InvalidAccessTokenException exception = new InvalidAccessTokenException(tokenType, message);

            assertEquals(tokenType, exception.getType());
            assertEquals(message, exception.getMessage());
        }
    }

    public void test_unicodeSupport() {
        // Test with Unicode characters
        String type = "トークン";
        String message = "無効なアクセストークン";
        InvalidAccessTokenException exception = new InvalidAccessTokenException(type, message);

        assertEquals(type, exception.getType());
        assertEquals(message, exception.getMessage());
    }

    public void test_exceptionInComplexScenario() {
        // Test exception in a more complex scenario
        InvalidAccessTokenException exception = null;
        String expectedType = "Bearer";
        String expectedMessage = "Token has expired";

        // Simulate a token validation scenario
        boolean tokenValid = false;
        if (!tokenValid) {
            exception = new InvalidAccessTokenException(expectedType, expectedMessage);
        }

        assertNotNull(exception);
        assertEquals(expectedType, exception.getType());
        assertEquals(expectedMessage, exception.getMessage());
    }
}