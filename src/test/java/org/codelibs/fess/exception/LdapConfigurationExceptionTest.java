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

public class LdapConfigurationExceptionTest extends UnitFessTestCase {

    public void test_constructor_withMessage() {
        // Test with a normal message
        String message = "LDAP configuration error occurred";
        LdapConfigurationException exception = new LdapConfigurationException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withNullMessage() {
        // Test with null message
        LdapConfigurationException exception = new LdapConfigurationException(null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withEmptyMessage() {
        // Test with empty message
        String message = "";
        LdapConfigurationException exception = new LdapConfigurationException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withLongMessage() {
        // Test with a long message
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("LDAP configuration error ").append(i).append(" ");
        }
        String longMessage = sb.toString();

        LdapConfigurationException exception = new LdapConfigurationException(longMessage);

        assertNotNull(exception);
        assertEquals(longMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_inheritance() {
        // Test that LdapConfigurationException extends FessSystemException
        LdapConfigurationException exception = new LdapConfigurationException("test");

        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    public void test_stackTrace() {
        // Test that stack trace is generated properly
        LdapConfigurationException exception = new LdapConfigurationException("Stack trace test");

        StackTraceElement[] stackTrace = exception.getStackTrace();
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);

        // The first element should be from this test method
        StackTraceElement firstElement = stackTrace[0];
        assertEquals("test_stackTrace", firstElement.getMethodName());
        assertEquals(this.getClass().getName(), firstElement.getClassName());
    }

    public void test_serialization() {
        // Test that the exception is serializable
        String message = "Serialization test";
        LdapConfigurationException exception = new LdapConfigurationException(message);

        // Test that serialVersionUID is accessible (inherited from parent)
        assertNotNull(exception);

        // Create a new instance to verify it can be created multiple times
        LdapConfigurationException exception2 = new LdapConfigurationException(message);
        assertEquals(exception.getMessage(), exception2.getMessage());
    }

    public void test_toString() {
        // Test the toString method
        String message = "LDAP configuration is invalid";
        LdapConfigurationException exception = new LdapConfigurationException(message);

        String toStringResult = exception.toString();
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains(LdapConfigurationException.class.getName()));
        assertTrue(toStringResult.contains(message));
    }

    public void test_multipleInstances() {
        // Test creating multiple instances with different messages
        LdapConfigurationException exception1 = new LdapConfigurationException("Error 1");
        LdapConfigurationException exception2 = new LdapConfigurationException("Error 2");
        LdapConfigurationException exception3 = new LdapConfigurationException("Error 3");

        assertEquals("Error 1", exception1.getMessage());
        assertEquals("Error 2", exception2.getMessage());
        assertEquals("Error 3", exception3.getMessage());

        // Ensure they are different instances
        assertNotSame(exception1, exception2);
        assertNotSame(exception2, exception3);
        assertNotSame(exception1, exception3);
    }

    public void test_throwAndCatch() {
        // Test throwing and catching the exception
        String errorMessage = "LDAP server not reachable";

        try {
            throw new LdapConfigurationException(errorMessage);
        } catch (LdapConfigurationException e) {
            assertEquals(errorMessage, e.getMessage());
            assertNull(e.getCause());
        } catch (Exception e) {
            fail("Should have caught LdapConfigurationException");
        }
    }

    public void test_throwAndCatchAsFessSystemException() {
        // Test catching as parent exception type
        String errorMessage = "LDAP bind DN is invalid";

        try {
            throw new LdapConfigurationException(errorMessage);
        } catch (FessSystemException e) {
            assertEquals(errorMessage, e.getMessage());
            assertTrue(e instanceof LdapConfigurationException);
        } catch (Exception e) {
            fail("Should have caught as FessSystemException");
        }
    }

    public void test_throwAndCatchAsRuntimeException() {
        // Test catching as RuntimeException
        String errorMessage = "LDAP search base is missing";

        try {
            throw new LdapConfigurationException(errorMessage);
        } catch (RuntimeException e) {
            assertEquals(errorMessage, e.getMessage());
            assertTrue(e instanceof LdapConfigurationException);
        }
    }

    public void test_specialCharactersInMessage() {
        // Test with special characters in message
        String message = "LDAP error: \"Invalid <config>\" & 'bad chars' @ #$%^&*()";
        LdapConfigurationException exception = new LdapConfigurationException(message);

        assertEquals(message, exception.getMessage());
    }

    public void test_unicodeCharactersInMessage() {
        // Test with Unicode characters in message
        String message = "LDAP設定エラー: 接続失敗 🚫 ñ é ü ß";
        LdapConfigurationException exception = new LdapConfigurationException(message);

        assertEquals(message, exception.getMessage());
    }

    public void test_newlineCharactersInMessage() {
        // Test with newline characters in message
        String message = "LDAP error:\n- Connection failed\n- Invalid credentials\n- Timeout";
        LdapConfigurationException exception = new LdapConfigurationException(message);

        assertEquals(message, exception.getMessage());
        assertTrue(exception.getMessage().contains("\n"));
    }
}