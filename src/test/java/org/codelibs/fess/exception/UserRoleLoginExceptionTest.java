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

    // Mock subclass of RootAction for testing generic type safety
    private static class TestAction extends RootAction {
        // Test subclass
    }

    // Another mock subclass for testing
    private static class AdminAction extends RootAction {
        // Test admin subclass
    }

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

    public void test_genericTypeSafety_withSubclass() {
        // Test that the exception accepts RootAction subclasses
        // This verifies the change from Class<RootAction> to Class<? extends RootAction>
        UserRoleLoginException exception = new UserRoleLoginException(TestAction.class);
        assertNotNull(exception);
        assertEquals(TestAction.class, exception.getActionClass());
    }

    public void test_genericTypeSafety_withAnotherSubclass() {
        // Test with another RootAction subclass
        UserRoleLoginException exception = new UserRoleLoginException(AdminAction.class);
        assertNotNull(exception);
        assertEquals(AdminAction.class, exception.getActionClass());
    }

    public void test_genericTypeSafety_getActionClassReturnType() {
        // Test that getActionClass() returns the correct generic type
        UserRoleLoginException exceptionWithRootAction = new UserRoleLoginException(RootAction.class);
        Class<? extends RootAction> actionClass1 = exceptionWithRootAction.getActionClass();
        assertNotNull(actionClass1);
        assertEquals(RootAction.class, actionClass1);

        UserRoleLoginException exceptionWithSubclass = new UserRoleLoginException(TestAction.class);
        Class<? extends RootAction> actionClass2 = exceptionWithSubclass.getActionClass();
        assertNotNull(actionClass2);
        assertEquals(TestAction.class, actionClass2);
        assertTrue(RootAction.class.isAssignableFrom(actionClass2));
    }

    public void test_genericTypeSafety_multipleDifferentSubclasses() {
        // Test that different instances can hold different subclass types
        UserRoleLoginException exception1 = new UserRoleLoginException(RootAction.class);
        UserRoleLoginException exception2 = new UserRoleLoginException(TestAction.class);
        UserRoleLoginException exception3 = new UserRoleLoginException(AdminAction.class);

        assertEquals(RootAction.class, exception1.getActionClass());
        assertEquals(TestAction.class, exception2.getActionClass());
        assertEquals(AdminAction.class, exception3.getActionClass());

        // Verify they are all RootAction or subclasses
        assertTrue(RootAction.class.isAssignableFrom(exception1.getActionClass()));
        assertTrue(RootAction.class.isAssignableFrom(exception2.getActionClass()));
        assertTrue(RootAction.class.isAssignableFrom(exception3.getActionClass()));
    }

    public void test_genericTypeSafety_throwAndCatchWithSubclass() {
        // Test throwing and catching with a subclass
        try {
            throw new UserRoleLoginException(TestAction.class);
        } catch (UserRoleLoginException e) {
            Class<? extends RootAction> actionClass = e.getActionClass();
            assertEquals(TestAction.class, actionClass);
            assertTrue(RootAction.class.isAssignableFrom(actionClass));
        }
    }

    public void test_genericTypeSafety_consistencyBetweenConstructorAndGetter() {
        // Verify consistency between constructor parameter type and getter return type
        // This test ensures the generic type change is consistent throughout the class

        // Test with RootAction.class
        Class<? extends RootAction> inputClass1 = RootAction.class;
        UserRoleLoginException exception1 = new UserRoleLoginException(inputClass1);
        Class<? extends RootAction> outputClass1 = exception1.getActionClass();
        assertSame(inputClass1, outputClass1);

        // Test with TestAction.class
        Class<? extends RootAction> inputClass2 = TestAction.class;
        UserRoleLoginException exception2 = new UserRoleLoginException(inputClass2);
        Class<? extends RootAction> outputClass2 = exception2.getActionClass();
        assertSame(inputClass2, outputClass2);

        // Test with AdminAction.class
        Class<? extends RootAction> inputClass3 = AdminAction.class;
        UserRoleLoginException exception3 = new UserRoleLoginException(inputClass3);
        Class<? extends RootAction> outputClass3 = exception3.getActionClass();
        assertSame(inputClass3, outputClass3);
    }

    public void test_genericTypeSafety_noClassCastException() {
        // Verify that no ClassCastException occurs when using subclasses
        try {
            UserRoleLoginException exception1 = new UserRoleLoginException(TestAction.class);
            Class<? extends RootAction> actionClass1 = exception1.getActionClass();
            assertNotNull(actionClass1);

            UserRoleLoginException exception2 = new UserRoleLoginException(AdminAction.class);
            Class<? extends RootAction> actionClass2 = exception2.getActionClass();
            assertNotNull(actionClass2);

            // No ClassCastException should occur
        } catch (ClassCastException e) {
            fail("ClassCastException should not occur: " + e.getMessage());
        }
    }

    public void test_genericTypeSafety_verifyInheritance() {
        // Verify that TestAction and AdminAction are indeed subclasses of RootAction
        assertTrue(RootAction.class.isAssignableFrom(TestAction.class));
        assertTrue(RootAction.class.isAssignableFrom(AdminAction.class));

        // Create exceptions with subclasses
        UserRoleLoginException exception1 = new UserRoleLoginException(TestAction.class);
        UserRoleLoginException exception2 = new UserRoleLoginException(AdminAction.class);

        // Verify the action classes are properly stored
        assertTrue(RootAction.class.isAssignableFrom(exception1.getActionClass()));
        assertTrue(RootAction.class.isAssignableFrom(exception2.getActionClass()));
        assertTrue(TestAction.class.isAssignableFrom(exception1.getActionClass()));
        assertTrue(AdminAction.class.isAssignableFrom(exception2.getActionClass()));
    }
}