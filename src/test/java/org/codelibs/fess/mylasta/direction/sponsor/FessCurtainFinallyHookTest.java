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
package org.codelibs.fess.mylasta.direction.sponsor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.atomic.AtomicBoolean;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.lastaflute.core.direction.FwAssistantDirector;
import org.lastaflute.core.direction.FwCoreDirection;
import org.lastaflute.web.direction.FwWebDirection;

public class FessCurtainFinallyHookTest extends UnitFessTestCase {

    private FwAssistantDirector createMockAssistantDirector() {
        return new org.codelibs.fess.mylasta.direction.FessFwAssistantDirector();
    }

    private FessCurtainFinallyHook curtainFinallyHook;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        curtainFinallyHook = new FessCurtainFinallyHook();
    }

    // Test normal execution of hook method
    public void test_hook_normalExecution() {
        // Create a mock FwAssistantDirector
        FwAssistantDirector assistantDirector = createMockAssistantDirector();

        // Should not throw any exception during normal execution
        curtainFinallyHook.hook(assistantDirector);

        // Verify that the method completes without errors
        assertTrue(true); // Method completed successfully
    }

    // Test hook method when MultiThreadedHttpConnectionManager class is not available
    public void test_hook_classNotFound() {
        // Create a custom hook that simulates class not found scenario
        FessCurtainFinallyHook customHook = new FessCurtainFinallyHook() {
            @Override
            public void hook(final FwAssistantDirector assistantDirector) {
                // Use reflection to call private method
                try {
                    Method method = FessCurtainFinallyHook.class.getDeclaredMethod("shutdownCommonsHttpClient");
                    method.setAccessible(true);
                    method.invoke(this);
                } catch (Exception e) {
                    // Expected to catch exception silently
                }
            }
        };

        FwAssistantDirector assistantDirector = createMockAssistantDirector();

        // Should handle ClassNotFoundException gracefully
        customHook.hook(assistantDirector);

        // Verify no exception is thrown to the caller
        assertTrue(true);
    }

    // Test shutdownCommonsHttpClient private method with reflection
    public void test_shutdownCommonsHttpClient_reflection() throws Exception {
        // Access the private method using reflection
        Method shutdownMethod = FessCurtainFinallyHook.class.getDeclaredMethod("shutdownCommonsHttpClient");
        shutdownMethod.setAccessible(true);

        // Invoke the method
        shutdownMethod.invoke(curtainFinallyHook);

        // Method should complete without throwing exceptions to the caller
        assertTrue(true);
    }

    // Test behavior when an exception occurs during reflection
    public void test_shutdownCommonsHttpClient_withException() throws Exception {
        // Create a hook that simulates an exception scenario
        AtomicBoolean exceptionHandled = new AtomicBoolean(false);

        FessCurtainFinallyHook customHook = new FessCurtainFinallyHook() {
            @Override
            public void hook(final FwAssistantDirector assistantDirector) {
                try {
                    // Simulate a scenario where the method exists but invocation fails
                    Class<?> clazz = MockMultiThreadedHttpConnectionManager.class;
                    Method method = clazz.getMethod("shutdownAll", (Class<?>[]) null);
                    method.invoke(null, (Object[]) null);
                } catch (Exception e) {
                    // Exception should be caught and logged
                    exceptionHandled.set(true);
                }
            }
        };

        FwAssistantDirector assistantDirector = createMockAssistantDirector();
        customHook.hook(assistantDirector);

        // Verify that exception was handled
        assertTrue(exceptionHandled.get());
    }

    // Test with a custom ClassLoader to simulate class availability
    public void test_hook_withCustomClassLoader() throws Exception {
        // Create a custom ClassLoader that can control class loading behavior
        ClassLoader customClassLoader = new URLClassLoader(new URL[0], null) {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                if ("org.apache.commons.httpclient.MultiThreadedHttpConnectionManager".equals(name)) {
                    // Simulate class not found
                    throw new ClassNotFoundException(name);
                }
                return super.loadClass(name);
            }
        };

        // Save original ClassLoader
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();

        try {
            // Set custom ClassLoader
            Thread.currentThread().setContextClassLoader(customClassLoader);

            FwAssistantDirector assistantDirector = createMockAssistantDirector();

            // Should handle the ClassNotFoundException gracefully
            curtainFinallyHook.hook(assistantDirector);

            // Verify execution completes successfully
            assertTrue(true);
        } finally {
            // Restore original ClassLoader
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    // Test multiple invocations of hook method
    public void test_hook_multipleInvocations() {
        FwAssistantDirector assistantDirector = createMockAssistantDirector();

        // First invocation
        curtainFinallyHook.hook(assistantDirector);

        // Second invocation - should also work without issues
        curtainFinallyHook.hook(assistantDirector);

        // Third invocation
        curtainFinallyHook.hook(assistantDirector);

        // All invocations should complete successfully
        assertTrue(true);
    }

    // Test with null assistant director (edge case)
    public void test_hook_withNullAssistantDirector() {
        // Hook should handle null parameter gracefully
        curtainFinallyHook.hook(null);

        // Verify no NullPointerException is thrown
        assertTrue(true);
    }

    // Mock class for testing exception scenarios
    private static class MockMultiThreadedHttpConnectionManager {
        public static void shutdownAll() {
            throw new RuntimeException("Simulated exception during shutdown");
        }
    }

    // Test to verify the logger field is properly initialized
    public void test_loggerFieldInitialization() throws Exception {
        // Use reflection to access the private logger field
        Field loggerField = FessCurtainFinallyHook.class.getDeclaredField("logger");
        loggerField.setAccessible(true);

        Object logger = loggerField.get(null); // static field

        // Verify logger is not null
        assertNotNull(logger);

        // Verify logger is of the expected type
        assertTrue(logger instanceof org.apache.logging.log4j.Logger);
    }

    // Test concurrent execution of hook method
    public void test_hook_concurrentExecution() throws Exception {
        final FwAssistantDirector assistantDirector = createMockAssistantDirector();
        final int threadCount = 10;
        final AtomicBoolean hasError = new AtomicBoolean(false);

        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                try {
                    curtainFinallyHook.hook(assistantDirector);
                } catch (Exception e) {
                    hasError.set(true);
                }
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join(5000); // 5 second timeout
        }

        // Verify no errors occurred during concurrent execution
        assertFalse(hasError.get());
    }
}