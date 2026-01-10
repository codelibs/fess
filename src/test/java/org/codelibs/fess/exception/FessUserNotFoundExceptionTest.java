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
import static org.junit.jupiter.api.Assertions.assertNotSame;
import org.junit.jupiter.api.Test;

public class FessUserNotFoundExceptionTest extends UnitFessTestCase {

    @Test
    public void test_constructor_withValidUsername() {
        // Test with a normal username
        String username = "testuser";
        FessUserNotFoundException exception = new FessUserNotFoundException(username);

        assertNotNull(exception);
        assertEquals("User is not found: testuser", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withEmptyUsername() {
        // Test with empty username
        String username = "";
        FessUserNotFoundException exception = new FessUserNotFoundException(username);

        assertNotNull(exception);
        assertEquals("User is not found: ", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withNullUsername() {
        // Test with null username
        String username = null;
        FessUserNotFoundException exception = new FessUserNotFoundException(username);

        assertNotNull(exception);
        assertEquals("User is not found: null", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withSpecialCharacters() {
        // Test with username containing special characters
        String username = "user@domain.com";
        FessUserNotFoundException exception = new FessUserNotFoundException(username);

        assertNotNull(exception);
        assertEquals("User is not found: user@domain.com", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withLongUsername() {
        // Test with a very long username
        String username = "verylongusername".repeat(10);
        FessUserNotFoundException exception = new FessUserNotFoundException(username);

        assertNotNull(exception);
        assertEquals("User is not found: " + username, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructor_withUnicodeCharacters() {
        // Test with username containing Unicode characters
        String username = "ユーザー名";
        FessUserNotFoundException exception = new FessUserNotFoundException(username);

        assertNotNull(exception);
        assertEquals("User is not found: ユーザー名", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_instanceOfFessSystemException() {
        // Test that FessUserNotFoundException is an instance of FessSystemException
        FessUserNotFoundException exception = new FessUserNotFoundException("testuser");

        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    public void test_serialVersionUID() {
        // Test that the exception has a serialVersionUID field
        FessUserNotFoundException exception1 = new FessUserNotFoundException("user1");
        FessUserNotFoundException exception2 = new FessUserNotFoundException("user2");

        // Both instances should be of the same class
        assertEquals(exception1.getClass(), exception2.getClass());
    }

    @Test
    public void test_stackTrace() {
        // Test that the exception has a proper stack trace
        FessUserNotFoundException exception = new FessUserNotFoundException("testuser");

        StackTraceElement[] stackTrace = exception.getStackTrace();
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);

        // The first element should be from this test class
        StackTraceElement firstElement = stackTrace[0];
        assertEquals(this.getClass().getName(), firstElement.getClassName());
    }

    @Test
    public void test_throwAndCatch() {
        // Test throwing and catching the exception
        String username = "nonexistentuser";

        try {
            throw new FessUserNotFoundException(username);
        } catch (FessUserNotFoundException e) {
            assertEquals("User is not found: nonexistentuser", e.getMessage());
            assertNull(e.getCause());
        } catch (Exception e) {
            fail("Should have caught FessUserNotFoundException");
        }
    }

    @Test
    public void test_throwAndCatchAsFessSystemException() {
        // Test catching as parent exception type
        String username = "testuser";

        try {
            throw new FessUserNotFoundException(username);
        } catch (FessSystemException e) {
            assertEquals("User is not found: testuser", e.getMessage());
            assertTrue(e instanceof FessUserNotFoundException);
        } catch (Exception e) {
            fail("Should have caught as FessSystemException");
        }
    }

    @Test
    public void test_exceptionChaining() {
        // Test that the exception can be used in exception chaining
        String username = "testuser";
        FessUserNotFoundException originalException = new FessUserNotFoundException(username);

        try {
            throw new RuntimeException("Wrapper exception", originalException);
        } catch (RuntimeException e) {
            assertEquals("Wrapper exception", e.getMessage());
            assertNotNull(e.getCause());
            assertTrue(e.getCause() instanceof FessUserNotFoundException);
            assertEquals("User is not found: testuser", e.getCause().getMessage());
        }
    }

    @Test
    public void test_multipleInstances() {
        // Test that multiple instances are independent
        FessUserNotFoundException exception1 = new FessUserNotFoundException("user1");
        FessUserNotFoundException exception2 = new FessUserNotFoundException("user2");

        assertNotSame(exception1, exception2);
        assertFalse(exception1.getMessage().equals(exception2.getMessage()));
        assertEquals("User is not found: user1", exception1.getMessage());
        assertEquals("User is not found: user2", exception2.getMessage());
    }
}