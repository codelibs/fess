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
package org.codelibs.fess.dict;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.codelibs.fess.unit.UnitFessTestCase;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

public class DictionaryExpiredExceptionTest extends UnitFessTestCase {

    @Test
    public void test_constructor() {
        // Test default constructor
        DictionaryExpiredException exception = new DictionaryExpiredException();
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_instanceOf() {
        // Test that exception is instance of RuntimeException
        DictionaryExpiredException exception = new DictionaryExpiredException();
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    public void test_throwAndCatch() {
        // Test throwing and catching the exception
        try {
            throw new DictionaryExpiredException();
        } catch (DictionaryExpiredException e) {
            assertNotNull(e);
            assertNull(e.getMessage());
        } catch (Exception e) {
            fail("Should have caught DictionaryExpiredException");
        }
    }

    @Test
    public void test_throwAsRuntimeException() {
        // Test that it can be caught as RuntimeException
        try {
            throw new DictionaryExpiredException();
        } catch (RuntimeException e) {
            assertNotNull(e);
            assertTrue(e instanceof DictionaryExpiredException);
        } catch (Exception e) {
            fail("Should have caught as RuntimeException");
        }
    }

    @Test
    public void test_serialization() throws Exception {
        // Test that the exception is serializable
        DictionaryExpiredException original = new DictionaryExpiredException();

        // Serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(original);
        oos.close();

        // Deserialize
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        DictionaryExpiredException deserialized = (DictionaryExpiredException) ois.readObject();
        ois.close();

        // Verify
        assertNotNull(deserialized);
        assertNull(deserialized.getMessage());
        assertNull(deserialized.getCause());
    }

    @Test
    public void test_stackTrace() {
        // Test that stack trace is properly maintained
        DictionaryExpiredException exception = new DictionaryExpiredException();
        StackTraceElement[] stackTrace = exception.getStackTrace();

        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);

        // First element should be from this test method
        assertEquals("test_stackTrace", stackTrace[0].getMethodName());
        assertEquals(this.getClass().getName(), stackTrace[0].getClassName());
    }

    @Test
    public void test_multipleCatches() {
        // Test with multiple catch blocks
        boolean caughtSpecific = false;
        boolean caughtRuntime = false;

        try {
            throw new DictionaryExpiredException();
        } catch (DictionaryExpiredException e) {
            caughtSpecific = true;
        } catch (RuntimeException e) {
            caughtRuntime = true;
        }

        assertTrue("Should have caught DictionaryExpiredException", caughtSpecific);
        assertFalse("Should not have caught as RuntimeException", caughtRuntime);
    }

    @Test
    public void test_rethrow() {
        // Test rethrowing the exception
        DictionaryExpiredException originalException = null;
        DictionaryExpiredException rethrownException = null;

        try {
            try {
                throw new DictionaryExpiredException();
            } catch (DictionaryExpiredException e) {
                originalException = e;
                throw e;
            }
        } catch (DictionaryExpiredException e) {
            rethrownException = e;
        }

        assertNotNull(originalException);
        assertNotNull(rethrownException);
        assertSame(originalException, rethrownException);
    }

    @Test
    public void test_equals() {
        // Test that different instances are not equal
        DictionaryExpiredException exception1 = new DictionaryExpiredException();
        DictionaryExpiredException exception2 = new DictionaryExpiredException();

        assertNotSame(exception1, exception2);
        assertFalse(exception1.equals(exception2));
        assertTrue(exception1.equals(exception1));
    }

    @Test
    public void test_hashCode() {
        // Test hashCode consistency
        DictionaryExpiredException exception = new DictionaryExpiredException();
        int hashCode1 = exception.hashCode();
        int hashCode2 = exception.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void test_toString() {
        // Test toString method
        DictionaryExpiredException exception = new DictionaryExpiredException();
        String str = exception.toString();

        assertNotNull(str);
        assertTrue(str.contains("DictionaryExpiredException"));
    }

    @Test
    public void test_getLocalizedMessage() {
        // Test getLocalizedMessage method
        DictionaryExpiredException exception = new DictionaryExpiredException();
        String localizedMessage = exception.getLocalizedMessage();

        assertNull(localizedMessage);
    }

    @Test
    public void test_fillInStackTrace() {
        // Test fillInStackTrace method
        DictionaryExpiredException exception = new DictionaryExpiredException();
        Throwable result = exception.fillInStackTrace();

        assertNotNull(result);
        assertSame(exception, result);

        StackTraceElement[] stackTrace = result.getStackTrace();
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);
    }

    @Test
    public void test_printStackTrace() {
        // Test printStackTrace doesn't throw exception
        DictionaryExpiredException exception = new DictionaryExpiredException();

        try {
            // Redirect to ByteArrayOutputStream to avoid console output
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            java.io.PrintStream ps = new java.io.PrintStream(baos);
            exception.printStackTrace(ps);

            String output = baos.toString();
            assertNotNull(output);
            assertTrue(output.contains("DictionaryExpiredException"));
        } catch (Exception e) {
            fail("printStackTrace should not throw exception");
        }
    }
}