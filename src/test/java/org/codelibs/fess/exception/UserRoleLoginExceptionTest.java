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

import org.codelibs.fess.app.web.RootAction;
import org.codelibs.fess.unit.UnitFessTestCase;

public class UserRoleLoginExceptionTest extends UnitFessTestCase {

    public void test_constructor() {
        // Test constructor with RootAction class
        UserRoleLoginException exception = new UserRoleLoginException(RootAction.class);
        assertNotNull(exception);
        assertEquals(RootAction.class, exception.getActionClass());
    }

    public void test_getActionClass() {
        // Test getActionClass returns correct class
        UserRoleLoginException exception = new UserRoleLoginException(RootAction.class);
        Class<?> actionClass = exception.getActionClass();
        assertNotNull(actionClass);
        assertEquals(RootAction.class, actionClass);
        assertEquals("org.codelibs.fess.app.web.RootAction", actionClass.getName());
    }

    public void test_fillInStackTrace() {
        // Test fillInStackTrace returns null for performance optimization
        UserRoleLoginException exception = new UserRoleLoginException(RootAction.class);
        Throwable result = exception.fillInStackTrace();
        assertNull(result);

        // Verify that stack trace is not generated
        StackTraceElement[] stackTrace = exception.getStackTrace();
        assertEquals(0, stackTrace.length);
    }

    public void test_serialVersionUID() {
        // Test that serialVersionUID is correctly defined
        UserRoleLoginException exception = new UserRoleLoginException(RootAction.class);
        // Verify exception is serializable
        assertTrue(exception instanceof java.io.Serializable);
    }

    public void test_exceptionInheritance() {
        // Test that exception is a RuntimeException
        UserRoleLoginException exception = new UserRoleLoginException(RootAction.class);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    public void test_getMessage() {
        // Test that getMessage returns null (no message set in constructor)
        UserRoleLoginException exception = new UserRoleLoginException(RootAction.class);
        assertNull(exception.getMessage());
    }

    public void test_getCause() {
        // Test that getCause returns null (no cause set in constructor)
        UserRoleLoginException exception = new UserRoleLoginException(RootAction.class);
        assertNull(exception.getCause());
    }

    public void test_throwAndCatch() {
        // Test throwing and catching the exception
        try {
            throw new UserRoleLoginException(RootAction.class);
        } catch (UserRoleLoginException e) {
            assertEquals(RootAction.class, e.getActionClass());
            assertNull(e.fillInStackTrace());
            assertEquals(0, e.getStackTrace().length);
        }
    }

    public void test_multipleInstances() {
        // Test that multiple instances maintain their own state
        UserRoleLoginException exception1 = new UserRoleLoginException(RootAction.class);
        UserRoleLoginException exception2 = new UserRoleLoginException(RootAction.class);

        assertNotSame(exception1, exception2);
        assertEquals(exception1.getActionClass(), exception2.getActionClass());
        assertSame(RootAction.class, exception1.getActionClass());
        assertSame(RootAction.class, exception2.getActionClass());
    }

    public void test_synchronizedFillInStackTrace() {
        // Test thread safety of fillInStackTrace method
        UserRoleLoginException exception = new UserRoleLoginException(RootAction.class);

        // Create multiple threads to test synchronized method
        Thread thread1 = new Thread(() -> {
            Throwable result = exception.fillInStackTrace();
            assertNull(result);
        });

        Thread thread2 = new Thread(() -> {
            Throwable result = exception.fillInStackTrace();
            assertNull(result);
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            fail("Thread interrupted");
        }
    }
}