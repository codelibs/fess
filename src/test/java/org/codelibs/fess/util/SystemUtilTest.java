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

import org.codelibs.fess.Constants;
import org.codelibs.fess.unit.UnitFessTestCase;

public class SystemUtilTest extends UnitFessTestCase {

    public void test_getSearchEngineHttpAddress_null() {
        // Clear the system property to test null case
        String originalValue = System.getProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);
        System.clearProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);

        try {
            String result = SystemUtil.getSearchEngineHttpAddress();
            assertNull(result);
        } finally {
            // Restore original value if it existed
            if (originalValue != null) {
                System.setProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS, originalValue);
            }
        }
    }

    public void test_getSearchEngineHttpAddress_withValue() {
        String testAddress = "http://localhost:9200";
        String originalValue = System.getProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);

        try {
            System.setProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS, testAddress);
            String result = SystemUtil.getSearchEngineHttpAddress();
            assertEquals(testAddress, result);
        } finally {
            // Restore original value
            if (originalValue != null) {
                System.setProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS, originalValue);
            } else {
                System.clearProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);
            }
        }
    }

    public void test_getSearchEngineHttpAddress_withEmptyString() {
        String originalValue = System.getProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);

        try {
            System.setProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS, "");
            String result = SystemUtil.getSearchEngineHttpAddress();
            assertEquals("", result);
        } finally {
            // Restore original value
            if (originalValue != null) {
                System.setProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS, originalValue);
            } else {
                System.clearProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);
            }
        }
    }

    public void test_getSearchEngineHttpAddress_withSpecialCharacters() {
        String testAddress = "https://search-engine.example.com:9200/path?param=value&other=test";
        String originalValue = System.getProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);

        try {
            System.setProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS, testAddress);
            String result = SystemUtil.getSearchEngineHttpAddress();
            assertEquals(testAddress, result);
        } finally {
            // Restore original value
            if (originalValue != null) {
                System.setProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS, originalValue);
            } else {
                System.clearProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);
            }
        }
    }

    public void test_getSearchEngineHttpAddress_withWhitespace() {
        String testAddress = "  http://localhost:9200  ";
        String originalValue = System.getProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);

        try {
            System.setProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS, testAddress);
            String result = SystemUtil.getSearchEngineHttpAddress();
            assertEquals(testAddress, result); // Should return exactly what's in the property
        } finally {
            // Restore original value
            if (originalValue != null) {
                System.setProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS, originalValue);
            } else {
                System.clearProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);
            }
        }
    }

    public void test_getSearchEngineHttpAddress_multipleChanges() {
        String originalValue = System.getProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);

        try {
            // Test multiple value changes
            String[] testValues = { "http://localhost:9200", "https://elasticsearch.example.com", "http://127.0.0.1:9201",
                    "https://search.domain.com:443" };

            for (String testValue : testValues) {
                System.setProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS, testValue);
                String result = SystemUtil.getSearchEngineHttpAddress();
                assertEquals(testValue, result);
            }
        } finally {
            // Restore original value
            if (originalValue != null) {
                System.setProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS, originalValue);
            } else {
                System.clearProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);
            }
        }
    }

    public void test_getSearchEngineHttpAddress_withUnicodeCharacters() {
        String testAddress = "http://テスト.example.com:9200";
        String originalValue = System.getProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);

        try {
            System.setProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS, testAddress);
            String result = SystemUtil.getSearchEngineHttpAddress();
            assertEquals(testAddress, result);
        } finally {
            // Restore original value
            if (originalValue != null) {
                System.setProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS, originalValue);
            } else {
                System.clearProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);
            }
        }
    }

    public void test_constructor_isPrivate() {
        // Verify that SystemUtil has a private constructor (utility class pattern)
        try {
            Constructor<SystemUtil> constructor = SystemUtil.class.getDeclaredConstructor();
            assertTrue("Constructor should be private", java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));

            // Test that constructor is accessible when made accessible
            constructor.setAccessible(true);
            SystemUtil instance = constructor.newInstance();
            assertNotNull(instance);

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            fail("Should be able to access private constructor: " + e.getMessage());
        }
    }

    public void test_inheritance() {
        // Verify that SystemUtil extends org.codelibs.core.lang.SystemUtil
        Class<?> superClass = SystemUtil.class.getSuperclass();
        assertEquals("Should extend org.codelibs.core.lang.SystemUtil", "org.codelibs.core.lang.SystemUtil", superClass.getName());
    }

    public void test_constants_reference() {
        // Test that the constant used is accessible and has expected value
        assertEquals("fess.search_engine.http_address", Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);
    }

    public void test_static_method_signature() {
        // Verify method signature is correct
        try {
            java.lang.reflect.Method method = SystemUtil.class.getMethod("getSearchEngineHttpAddress");

            // Check return type
            assertEquals("Return type should be String", String.class, method.getReturnType());

            // Check that it's static
            assertTrue("Method should be static", java.lang.reflect.Modifier.isStatic(method.getModifiers()));

            // Check that it's public
            assertTrue("Method should be public", java.lang.reflect.Modifier.isPublic(method.getModifiers()));

            // Check parameter count
            assertEquals("Method should have no parameters", 0, method.getParameterCount());

        } catch (NoSuchMethodException e) {
            fail("getSearchEngineHttpAddress method should exist");
        }
    }

    public void test_systemProperty_independence() {
        // Test that method correctly reads from system properties each time
        String originalValue = System.getProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);

        try {
            // Set initial value
            System.setProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS, "initial");
            assertEquals("initial", SystemUtil.getSearchEngineHttpAddress());

            // Change value and verify it's read fresh
            System.setProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS, "changed");
            assertEquals("changed", SystemUtil.getSearchEngineHttpAddress());

            // Clear property
            System.clearProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);
            assertNull(SystemUtil.getSearchEngineHttpAddress());

        } finally {
            // Restore original value
            if (originalValue != null) {
                System.setProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS, originalValue);
            } else {
                System.clearProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);
            }
        }
    }

    public void test_thread_safety() {
        // Test concurrent access to the method
        String originalValue = System.getProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);

        try {
            System.setProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS, "thread-test");

            final String[] results = new String[10];
            Thread[] threads = new Thread[10];

            for (int i = 0; i < 10; i++) {
                final int index = i;
                threads[i] = new Thread(() -> {
                    results[index] = SystemUtil.getSearchEngineHttpAddress();
                });
            }

            // Start all threads
            for (Thread thread : threads) {
                thread.start();
            }

            // Wait for all threads to complete
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    fail("Thread interrupted");
                }
            }

            // Verify all results are consistent
            for (String result : results) {
                assertEquals("thread-test", result);
            }

        } finally {
            // Restore original value
            if (originalValue != null) {
                System.setProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS, originalValue);
            } else {
                System.clearProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);
            }
        }
    }

    public void test_getSearchEngineHttpAddress_withLongValue() {
        // Test with a very long URL
        StringBuilder longUrl = new StringBuilder("http://");
        for (int i = 0; i < 1000; i++) {
            longUrl.append("a");
        }
        longUrl.append(".example.com:9200");

        String originalValue = System.getProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);

        try {
            System.setProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS, longUrl.toString());
            String result = SystemUtil.getSearchEngineHttpAddress();
            assertEquals(longUrl.toString(), result);
        } finally {
            // Restore original value
            if (originalValue != null) {
                System.setProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS, originalValue);
            } else {
                System.clearProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);
            }
        }
    }

    public void test_utilityClass_pattern() {
        // Verify utility class design pattern compliance

        // 1. Class should be final (or have private constructor)
        Constructor<?>[] constructors = SystemUtil.class.getDeclaredConstructors();
        assertEquals("Should have exactly one constructor", 1, constructors.length);
        assertTrue("Constructor should be private", java.lang.reflect.Modifier.isPrivate(constructors[0].getModifiers()));

        // 2. All methods should be static
        java.lang.reflect.Method[] methods = SystemUtil.class.getDeclaredMethods();
        for (java.lang.reflect.Method method : methods) {
            if (!method.isSynthetic()) { // Ignore compiler-generated methods
                assertTrue("Method " + method.getName() + " should be static", java.lang.reflect.Modifier.isStatic(method.getModifiers()));
            }
        }
    }

    public void test_maskSensitiveValue_withPassword() {
        assertEquals("********", SystemUtil.maskSensitiveValue("DB_PASSWORD", "secret123"));
        assertEquals("********", SystemUtil.maskSensitiveValue("password", "secret123"));
        assertEquals("********", SystemUtil.maskSensitiveValue("mysql_password", "secret123"));
        assertEquals("********", SystemUtil.maskSensitiveValue("SMTP_PASSWORD", "secret123"));
    }

    public void test_maskSensitiveValue_withSecret() {
        assertEquals("********", SystemUtil.maskSensitiveValue("AWS_SECRET_ACCESS_KEY", "AKIAIOSFODNN7EXAMPLE"));
        assertEquals("********", SystemUtil.maskSensitiveValue("secret", "mySecret"));
        assertEquals("********", SystemUtil.maskSensitiveValue("CLIENT_SECRET", "abc123"));
    }

    public void test_maskSensitiveValue_withKey() {
        assertEquals("********", SystemUtil.maskSensitiveValue("API_KEY", "abc123"));
        assertEquals("********", SystemUtil.maskSensitiveValue("apikey", "abc123"));
        assertEquals("********", SystemUtil.maskSensitiveValue("PRIVATE_KEY", "-----BEGIN RSA PRIVATE KEY-----"));
    }

    public void test_maskSensitiveValue_withToken() {
        assertEquals("********", SystemUtil.maskSensitiveValue("AUTH_TOKEN", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"));
        assertEquals("********", SystemUtil.maskSensitiveValue("access_token", "ya29.a0ARrdaM8"));
        assertEquals("********", SystemUtil.maskSensitiveValue("REFRESH_TOKEN", "1//0eXyz"));
    }

    public void test_maskSensitiveValue_withCredential() {
        assertEquals("********", SystemUtil.maskSensitiveValue("GOOGLE_CREDENTIALS", "{}"));
        assertEquals("********", SystemUtil.maskSensitiveValue("credential", "user:pass"));
    }

    public void test_maskSensitiveValue_withAuth() {
        assertEquals("********", SystemUtil.maskSensitiveValue("BASIC_AUTH", "dXNlcjpwYXNz"));
        assertEquals("********", SystemUtil.maskSensitiveValue("auth_header", "Bearer xyz"));
        assertEquals("********", SystemUtil.maskSensitiveValue("OAUTH_CLIENT_ID", "123456"));
    }

    public void test_maskSensitiveValue_withPrivate() {
        assertEquals("********", SystemUtil.maskSensitiveValue("PRIVATE_DATA", "sensitive"));
        assertEquals("********", SystemUtil.maskSensitiveValue("private_config", "internal"));
    }

    public void test_maskSensitiveValue_safeValues() {
        // These should NOT be masked
        assertEquals("localhost:9200", SystemUtil.maskSensitiveValue("HOST", "localhost:9200"));
        assertEquals("8080", SystemUtil.maskSensitiveValue("PORT", "8080"));
        assertEquals("/var/log/fess", SystemUtil.maskSensitiveValue("LOG_DIR", "/var/log/fess"));
        assertEquals("production", SystemUtil.maskSensitiveValue("ENVIRONMENT", "production"));
        assertEquals("admin", SystemUtil.maskSensitiveValue("USER_NAME", "admin"));
        assertEquals("true", SystemUtil.maskSensitiveValue("DEBUG_MODE", "true"));
    }

    public void test_maskSensitiveValue_nullHandling() {
        assertNull(SystemUtil.maskSensitiveValue(null, "value"));
        assertNull(SystemUtil.maskSensitiveValue("key", null));
        assertNull(SystemUtil.maskSensitiveValue(null, null));
    }

    public void test_maskSensitiveValue_caseInsensitive() {
        // Should work regardless of case
        assertEquals("********", SystemUtil.maskSensitiveValue("password", "secret"));
        assertEquals("********", SystemUtil.maskSensitiveValue("PASSWORD", "secret"));
        assertEquals("********", SystemUtil.maskSensitiveValue("Password", "secret"));
        assertEquals("********", SystemUtil.maskSensitiveValue("PaSsWoRd", "secret"));
    }

    public void test_maskSensitiveValue_emptyValue() {
        assertEquals("********", SystemUtil.maskSensitiveValue("PASSWORD", ""));
        assertEquals("", SystemUtil.maskSensitiveValue("HOST", ""));
    }
}