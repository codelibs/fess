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

public class LdapOperationExceptionTest extends UnitFessTestCase {

    public void test_constructor_withMessage() {
        // Test constructor with message only
        String message = "LDAP operation failed";
        LdapOperationException exception = new LdapOperationException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withMessageAndCause() {
        // Test constructor with message and cause
        String message = "LDAP operation failed with error";
        Exception cause = new RuntimeException("Connection timeout");
        LdapOperationException exception = new LdapOperationException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals("Connection timeout", exception.getCause().getMessage());
    }

    public void test_constructor_withNullMessage() {
        // Test constructor with null message
        LdapOperationException exception = new LdapOperationException(null);

        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withNullMessageAndCause() {
        // Test constructor with null message and null cause
        LdapOperationException exception = new LdapOperationException(null, null);

        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withEmptyMessage() {
        // Test constructor with empty message
        String message = "";
        LdapOperationException exception = new LdapOperationException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withMessageAndNullCause() {
        // Test constructor with message and null cause
        String message = "LDAP error occurred";
        LdapOperationException exception = new LdapOperationException(message, null);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_inheritanceFromFessSystemException() {
        // Test that LdapOperationException is properly inherited from FessSystemException
        LdapOperationException exception = new LdapOperationException("Test");

        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    public void test_serialVersionUID() {
        // Test that exception is serializable
        String message = "Serialization test";
        LdapOperationException exception = new LdapOperationException(message);

        // Verify the exception can be created (serialVersionUID is present)
        assertNotNull(exception);
    }

    public void test_stackTrace() {
        // Test that stack trace is properly captured
        LdapOperationException exception = new LdapOperationException("Stack trace test");
        StackTraceElement[] stackTrace = exception.getStackTrace();

        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);
        // Verify that current test method appears in stack trace
        boolean foundTestMethod = false;
        for (StackTraceElement element : stackTrace) {
            if (element.getMethodName().equals("test_stackTrace")) {
                foundTestMethod = true;
                break;
            }
        }
        assertTrue(foundTestMethod);
    }

    public void test_nestedExceptions() {
        // Test nested exception handling
        Exception innerCause = new IllegalArgumentException("Invalid LDAP parameter");
        RuntimeException middleCause = new RuntimeException("LDAP configuration error", innerCause);
        LdapOperationException exception = new LdapOperationException("LDAP operation failed", middleCause);

        assertEquals("LDAP operation failed", exception.getMessage());
        assertEquals(middleCause, exception.getCause());
        assertEquals("LDAP configuration error", exception.getCause().getMessage());
        assertEquals(innerCause, exception.getCause().getCause());
        assertEquals("Invalid LDAP parameter", exception.getCause().getCause().getMessage());
    }

    public void test_throwAndCatch() {
        // Test throwing and catching the exception
        String expectedMessage = "LDAP authentication failed";
        try {
            throw new LdapOperationException(expectedMessage);
        } catch (LdapOperationException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertNull(e.getCause());
        } catch (Exception e) {
            fail("Should have caught LdapOperationException");
        }
    }

    public void test_throwAndCatchWithCause() {
        // Test throwing and catching the exception with cause
        String expectedMessage = "LDAP connection error";
        Exception expectedCause = new IOException("Network unreachable");

        try {
            throw new LdapOperationException(expectedMessage, expectedCause);
        } catch (LdapOperationException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedCause, e.getCause());
            assertEquals("Network unreachable", e.getCause().getMessage());
        } catch (Exception e) {
            fail("Should have caught LdapOperationException");
        }
    }

    public void test_getMessage_consistency() {
        // Test message consistency across different scenarios
        String message1 = "LDAP bind failed";
        String message2 = "LDAP search failed";
        Exception cause = new Exception("Timeout");

        LdapOperationException exception1 = new LdapOperationException(message1);
        LdapOperationException exception2 = new LdapOperationException(message2, cause);

        // Messages should remain as provided
        assertEquals(message1, exception1.getMessage());
        assertEquals(message2, exception2.getMessage());

        // Messages should not be affected by cause
        assertFalse(exception1.getMessage().equals(exception2.getMessage()));
    }

    public void test_longMessage() {
        // Test with very long message
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longMessage.append("LDAP error ").append(i).append(" ");
        }
        String message = longMessage.toString();

        LdapOperationException exception = new LdapOperationException(message);

        assertEquals(message, exception.getMessage());
        assertTrue(exception.getMessage().length() > 10000);
    }

    public void test_specialCharactersInMessage() {
        // Test with special characters in message
        String message = "LDAP error: \n\t\r Special chars: @#$%^&*(){}[]|\\:;\"'<>,.?/~`";
        LdapOperationException exception = new LdapOperationException(message);

        assertEquals(message, exception.getMessage());
    }

    public void test_unicodeMessage() {
        // Test with Unicode characters in message
        String message = "LDAP エラー: 接続失敗 🔒 ñ ü é";
        LdapOperationException exception = new LdapOperationException(message);

        assertEquals(message, exception.getMessage());
    }

    private static class IOException extends Exception {
        public IOException(String message) {
            super(message);
        }
    }
}