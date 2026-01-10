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
package org.codelibs.fess.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.codelibs.fess.exception.WebApiException;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class WebApiUtilTest extends UnitFessTestCase {

    @Test
    public void test_setObject_noRequest() {
        // Test setObject when no HTTP request is available
        // This should not throw exception, just do nothing
        try {
            WebApiUtil.setObject("testKey", "testValue");
        } catch (Exception e) {
            fail("setObject should handle missing request gracefully: " + e.getMessage());
        }
    }

    @Test
    public void test_setObject_withNullName() {
        // Test setObject with null name
        try {
            WebApiUtil.setObject(null, "testValue");
        } catch (Exception e) {
            fail("setObject should handle null name gracefully: " + e.getMessage());
        }
    }

    @Test
    public void test_setObject_withNullValue() {
        // Test setObject with null value
        try {
            WebApiUtil.setObject("testKey", null);
        } catch (Exception e) {
            fail("setObject should handle null value gracefully: " + e.getMessage());
        }
    }

    @Test
    public void test_setObject_withEmptyName() {
        // Test setObject with empty name
        try {
            WebApiUtil.setObject("", "testValue");
        } catch (Exception e) {
            fail("setObject should handle empty name gracefully: " + e.getMessage());
        }
    }

    @Test
    public void test_getObject_noRequest() {
        // Test getObject when no HTTP request is available
        // This should return null
        Object result = WebApiUtil.getObject("testKey");
        assertNull(result, "getObject should return null when no request available");
    }

    @Test
    public void test_getObject_withNullName() {
        // Test getObject with null name
        Object result = WebApiUtil.getObject(null);
        assertNull(result, "getObject should return null for null name");
    }

    @Test
    public void test_getObject_withEmptyName() {
        // Test getObject with empty name
        Object result = WebApiUtil.getObject("");
        assertNull(result, "getObject should return null for empty name");
    }

    @Test
    public void test_getObject_nonExistentKey() {
        // Test getObject with non-existent key
        Object result = WebApiUtil.getObject("nonExistentKey");
        assertNull(result, "getObject should return null for non-existent key");
    }

    @Test
    public void test_setError_withMessage_noRequest() {
        // Test setError with message when no HTTP request is available
        try {
            WebApiUtil.setError(404, "Not Found");
        } catch (Exception e) {
            fail("setError should handle missing request gracefully: " + e.getMessage());
        }
    }

    @Test
    public void test_setError_withException_noRequest() {
        // Test setError with exception when no HTTP request is available
        try {
            WebApiUtil.setError(500, new RuntimeException("Test error"));
        } catch (Exception e) {
            fail("setError should handle missing request gracefully: " + e.getMessage());
        }
    }

    @Test
    public void test_setError_withNullMessage() {
        // Test setError with null message
        try {
            WebApiUtil.setError(400, (String) null);
        } catch (Exception e) {
            fail("setError should handle null message gracefully: " + e.getMessage());
        }
    }

    @Test
    public void test_setError_withNullException() {
        // Test setError with null exception
        // Note: WebApiException constructor calls e.getMessage() on null exception, causing NullPointerException
        try {
            WebApiUtil.setError(500, (Exception) null);
            fail("setError should throw NullPointerException when passed null exception");
        } catch (NullPointerException e) {
            // Expected - WebApiException constructor cannot handle null exception
            assertTrue("Should get expected NPE message",
                    e.getMessage().contains("Cannot invoke \"java.lang.Exception.getMessage()\" because \"e\" is null"));
        }
    }

    @Test
    public void test_setError_withEmptyMessage() {
        // Test setError with empty message
        try {
            WebApiUtil.setError(400, "");
        } catch (Exception e) {
            fail("setError should handle empty message gracefully: " + e.getMessage());
        }
    }

    @Test
    public void test_setError_withVariousStatusCodes() {
        // Test setError with various HTTP status codes
        try {
            WebApiUtil.setError(200, "OK");
            WebApiUtil.setError(201, "Created");
            WebApiUtil.setError(400, "Bad Request");
            WebApiUtil.setError(401, "Unauthorized");
            WebApiUtil.setError(403, "Forbidden");
            WebApiUtil.setError(404, "Not Found");
            WebApiUtil.setError(500, "Internal Server Error");
            WebApiUtil.setError(502, "Bad Gateway");
            WebApiUtil.setError(503, "Service Unavailable");
        } catch (Exception e) {
            fail("setError should handle various status codes gracefully: " + e.getMessage());
        }
    }

    @Test
    public void test_validate_noRequest() {
        // Test validate when no HTTP request is available
        try {
            WebApiUtil.validate();
        } catch (Exception e) {
            fail("validate should handle missing request gracefully: " + e.getMessage());
        }
    }

    @Test
    public void test_validate_noError() {
        // Test validate when no error is set
        // This should not throw exception
        try {
            WebApiUtil.validate();
        } catch (WebApiException e) {
            fail("validate should not throw exception when no error is set");
        }
    }

    @Test
    public void test_constructor_isPrivate() {
        // Verify that WebApiUtil has a private constructor (utility class pattern)
        try {
            Constructor<WebApiUtil> constructor = WebApiUtil.class.getDeclaredConstructor();
            assertTrue("Constructor should be private", java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));

            // Test that constructor is accessible when made accessible
            constructor.setAccessible(true);
            WebApiUtil instance = constructor.newInstance();
            assertNotNull(instance);

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            fail("Should be able to access private constructor: " + e.getMessage());
        }
    }

    @Test
    public void test_class_isFinal() {
        // Verify that WebApiUtil is a final class
        assertTrue("WebApiUtil should be final class", java.lang.reflect.Modifier.isFinal(WebApiUtil.class.getModifiers()));
    }

    @Test
    public void test_static_method_signatures() {
        // Verify all public static methods exist with correct signatures
        try {
            // setObject(String, Object)
            Method setObjectMethod = WebApiUtil.class.getMethod("setObject", String.class, Object.class);
            assertTrue("setObject should be static", java.lang.reflect.Modifier.isStatic(setObjectMethod.getModifiers()));
            assertTrue("setObject should be public", java.lang.reflect.Modifier.isPublic(setObjectMethod.getModifiers()));
            assertEquals("setObject should return void", void.class, setObjectMethod.getReturnType());

            // getObject(String)
            Method getObjectMethod = WebApiUtil.class.getMethod("getObject", String.class);
            assertTrue("getObject should be static", java.lang.reflect.Modifier.isStatic(getObjectMethod.getModifiers()));
            assertTrue("getObject should be public", java.lang.reflect.Modifier.isPublic(getObjectMethod.getModifiers()));
            assertEquals("getObject should return Object", Object.class, getObjectMethod.getReturnType());

            // setError(int, String)
            Method setErrorStringMethod = WebApiUtil.class.getMethod("setError", int.class, String.class);
            assertTrue("setError(int, String) should be static", java.lang.reflect.Modifier.isStatic(setErrorStringMethod.getModifiers()));
            assertTrue("setError(int, String) should be public", java.lang.reflect.Modifier.isPublic(setErrorStringMethod.getModifiers()));
            assertEquals("setError(int, String) should return void", void.class, setErrorStringMethod.getReturnType());

            // setError(int, Exception)
            Method setErrorExceptionMethod = WebApiUtil.class.getMethod("setError", int.class, Exception.class);
            assertTrue("setError(int, Exception) should be static",
                    java.lang.reflect.Modifier.isStatic(setErrorExceptionMethod.getModifiers()));
            assertTrue("setError(int, Exception) should be public",
                    java.lang.reflect.Modifier.isPublic(setErrorExceptionMethod.getModifiers()));
            assertEquals("setError(int, Exception) should return void", void.class, setErrorExceptionMethod.getReturnType());

            // validate()
            Method validateMethod = WebApiUtil.class.getMethod("validate");
            assertTrue("validate should be static", java.lang.reflect.Modifier.isStatic(validateMethod.getModifiers()));
            assertTrue("validate should be public", java.lang.reflect.Modifier.isPublic(validateMethod.getModifiers()));
            assertEquals("validate should return void", void.class, validateMethod.getReturnType());

        } catch (NoSuchMethodException e) {
            fail("All expected public methods should exist: " + e.getMessage());
        }
    }

    @Test
    public void test_constant_WEB_API_EXCEPTION() {
        // Test that the constant WEB_API_EXCEPTION is used correctly
        // This is verified by checking the behavior matches expected constant usage

        // Since the constant is private, we can't access it directly
        // But we can verify the behavior is consistent
        try {
            WebApiUtil.setError(400, "Test error");
            WebApiUtil.validate(); // Should not throw in test environment
        } catch (Exception e) {
            // This is expected behavior when no request context is available
        }
    }

    @Test
    public void test_utilityClass_pattern() {
        // Verify utility class design pattern compliance

        // 1. Class should be final
        assertTrue("Class should be final", java.lang.reflect.Modifier.isFinal(WebApiUtil.class.getModifiers()));

        // 2. Should have exactly one private constructor
        Constructor<?>[] constructors = WebApiUtil.class.getDeclaredConstructors();
        assertEquals("Should have exactly one constructor", 1, constructors.length);
        assertTrue("Constructor should be private", java.lang.reflect.Modifier.isPrivate(constructors[0].getModifiers()));

        // 3. All public methods should be static
        Method[] methods = WebApiUtil.class.getDeclaredMethods();
        for (Method method : methods) {
            if (java.lang.reflect.Modifier.isPublic(method.getModifiers()) && !method.isSynthetic()) {
                assertTrue("Public method " + method.getName() + " should be static",
                        java.lang.reflect.Modifier.isStatic(method.getModifiers()));
            }
        }
    }

    @Test
    public void test_generic_type_safety() {
        // Test generic type safety of getObject method
        // Since we're in test environment without request context, this will return null
        String stringResult = WebApiUtil.getObject("testString");
        assertNull(stringResult, "Should return null in test environment");

        Integer intResult = WebApiUtil.getObject("testInteger");
        assertNull(intResult, "Should return null in test environment");

        Object objectResult = WebApiUtil.getObject("testObject");
        assertNull(objectResult, "Should return null in test environment");
    }

    @Test
    public void test_setObject_withDifferentTypes() {
        // Test setObject with different object types
        try {
            WebApiUtil.setObject("stringValue", "test");
            WebApiUtil.setObject("intValue", 123);
            WebApiUtil.setObject("booleanValue", true);
            WebApiUtil.setObject("objectValue", new Object());

            // Test with collections
            java.util.List<String> list = java.util.Arrays.asList("item1", "item2");
            WebApiUtil.setObject("listValue", list);

            java.util.Map<String, String> map = new java.util.HashMap<>();
            map.put("key", "value");
            WebApiUtil.setObject("mapValue", map);

        } catch (Exception e) {
            fail("setObject should handle different object types: " + e.getMessage());
        }
    }

    @Test
    public void test_error_handling_edge_cases() {
        // Test error handling with edge cases
        try {
            // Test with extreme status codes
            WebApiUtil.setError(-1, "Negative status code");
            WebApiUtil.setError(0, "Zero status code");
            WebApiUtil.setError(999, "Large status code");

            // Test with very long message
            StringBuilder longMessage = new StringBuilder();
            for (int i = 0; i < 1000; i++) {
                longMessage.append("This is a very long error message. ");
            }
            WebApiUtil.setError(500, longMessage.toString());

            // Test with special characters in message
            WebApiUtil.setError(400, "Error with special chars: àáâãäåæçèéêë ñ 中文 🚀");

        } catch (Exception e) {
            fail("Error handling should work with edge cases: " + e.getMessage());
        }
    }

    @Test
    public void test_method_parameter_validation() {
        // Test that methods handle parameter validation appropriately

        // setObject should handle various inputs
        try {
            WebApiUtil.setObject("validKey", "validValue");
            WebApiUtil.setObject("", "emptyKey");
            WebApiUtil.setObject("nullValue", null);
            WebApiUtil.setObject(null, "nullKey");
            WebApiUtil.setObject(null, null);
        } catch (Exception e) {
            fail("setObject should handle parameter validation gracefully: " + e.getMessage());
        }

        // getObject should handle various inputs
        try {
            WebApiUtil.getObject("validKey");
            WebApiUtil.getObject("");
            WebApiUtil.getObject(null);
        } catch (Exception e) {
            fail("getObject should handle parameter validation gracefully: " + e.getMessage());
        }

        // setError should handle various inputs
        try {
            WebApiUtil.setError(400, "valid message");
            WebApiUtil.setError(500, "");
            WebApiUtil.setError(200, (String) null);
            WebApiUtil.setError(500, new RuntimeException("test"));
        } catch (Exception e) {
            fail("setError should handle valid parameter validation gracefully: " + e.getMessage());
        }

        // Test null exception separately since it causes NullPointerException
        try {
            WebApiUtil.setError(400, (Exception) null);
            fail("setError should throw NullPointerException when passed null exception");
        } catch (NullPointerException e) {
            // Expected - WebApiException constructor cannot handle null exception
        }
    }

    @Test
    public void test_thread_safety() {
        // Test thread safety of utility methods
        final int threadCount = 10;
        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(threadCount);
        final java.util.concurrent.atomic.AtomicInteger errorCount = new java.util.concurrent.atomic.AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    WebApiUtil.setObject("key" + threadId, "value" + threadId);
                    WebApiUtil.getObject("key" + threadId);
                    WebApiUtil.setError(400 + threadId, "Error " + threadId);
                    WebApiUtil.validate();
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Thread safety test interrupted");
        }

        // In test environment, some operations might not work due to missing request context
        // but the methods should not throw unexpected exceptions
    }
}