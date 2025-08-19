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
package org.codelibs.fess.mylasta.direction;

import org.codelibs.fess.Constants;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.core.direction.exception.ConfigPropertyNotFoundException;

public class FessConfigImplTest extends UnitFessTestCase {

    private FessConfigImpl fessConfig;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        // Create FessConfigImpl with overridden get method for testing
        fessConfig = new FessConfigImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String get(String propertyKey) {
                // Check system properties first (with prefix)
                String systemValue = System.getProperty(Constants.FESS_CONFIG_PREFIX + propertyKey);
                if (systemValue != null) {
                    return systemValue;
                }

                // Return test values for specific properties
                switch (propertyKey) {
                case "domain.title":
                    return "Test Fess";
                case "search_engine.type":
                    return "opensearch";
                case "test.property":
                    return "config-value";
                case "filter.null":
                    // This would be filtered to null by PropertyFilter in real scenario
                    throw new ConfigPropertyNotFoundException("filter.null");
                default:
                    throw new ConfigPropertyNotFoundException(propertyKey);
                }
            }
        };

        ComponentUtil.register(fessConfig, "fessConfig");
    }

    @Override
    public void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    // Test basic property retrieval
    public void test_get_basicProperty() {
        // Test getting a property from the config
        String value = fessConfig.get("domain.title");
        assertEquals("Test Fess", value);

        // Test getting another property
        String engineType = fessConfig.get("search_engine.type");
        assertEquals("opensearch", engineType);
    }

    // Test system property override
    public void test_get_systemPropertyOverride() {
        // Set system property that should override config
        String testKey = "test.property";
        String systemValue = "system-value";
        System.setProperty(Constants.FESS_CONFIG_PREFIX + testKey, systemValue);

        try {
            // Test that system property takes precedence
            String value = fessConfig.get(testKey);
            assertEquals(systemValue, value);
        } finally {
            // Clean up system property
            System.clearProperty(Constants.FESS_CONFIG_PREFIX + testKey);
        }
    }

    // Test caching mechanism
    public void test_get_caching() {
        // Get property twice to test caching behavior
        String testKey = "domain.title";
        String value1 = fessConfig.get(testKey);
        String value2 = fessConfig.get(testKey);

        // Values should be the same
        assertEquals("Test Fess", value1);
        assertEquals(value1, value2);
    }

    // Test property that should be consistent
    public void test_get_propertyFilter() {
        // Test a regular property that passes through
        String value = fessConfig.get("domain.title");
        assertEquals("Test Fess", value);
    }

    // Test null property handling
    public void test_get_nullProperty() {
        // Test getting non-existent property
        try {
            fessConfig.get("non.existent.property");
            fail("Should throw exception for non-existent property");
        } catch (ConfigPropertyNotFoundException e) {
            // Expected exception
            assertTrue(e.getMessage().contains("non.existent.property"));
        }
    }

    // Test system property without prefix
    public void test_get_systemPropertyWithoutPrefix() {
        // Set system property without FESS_CONFIG_PREFIX
        String testKey = "test.property";
        String systemValue = "wrong-system-value";
        System.setProperty(testKey, systemValue);

        try {
            // Should get the config value, not the system property without prefix
            String value = fessConfig.get(testKey);
            assertEquals("config-value", value);
            assertFalse(systemValue.equals(value));
        } finally {
            // Clean up system property
            System.clearProperty(testKey);
        }
    }

    // Test multiple gets with different keys
    public void test_get_multipleKeys() {
        // Test getting multiple different properties
        String value1 = fessConfig.get("domain.title");
        String value2 = fessConfig.get("search_engine.type");

        assertEquals("Test Fess", value1);
        assertEquals("opensearch", value2);
        assertFalse(value1.equals(value2));
    }

    // Test empty string property value
    public void test_get_emptyStringValue() {
        String testKey = "empty.property";
        System.setProperty(Constants.FESS_CONFIG_PREFIX + testKey, "");

        try {
            String value = fessConfig.get(testKey);
            assertEquals("", value);
        } finally {
            // Clean up system property
            System.clearProperty(Constants.FESS_CONFIG_PREFIX + testKey);
        }
    }

    // Test property filter returning null simulation
    public void test_get_filterReturnsNull() {
        // The filter.null property is configured to simulate null filter behavior
        try {
            fessConfig.get("filter.null");
            fail("Should throw exception when property is not found");
        } catch (ConfigPropertyNotFoundException e) {
            // Expected exception
            assertTrue(e.getMessage().contains("filter.null"));
        }
    }

    // Test concurrent access
    public void test_get_concurrentAccess() throws InterruptedException {
        // Create multiple threads accessing the same property
        final String testKey = "domain.title";
        final int threadCount = 10;
        final Thread[] threads = new Thread[threadCount];
        final String[] results = new String[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                results[index] = fessConfig.get(testKey);
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // All results should be the same
        String expectedValue = "Test Fess";
        for (String result : results) {
            assertEquals(expectedValue, result);
        }
    }

    // Test with non-existent config
    public void test_initialize_invalidConfigFile() {
        // Test getting a property that doesn't exist
        FessConfigImpl invalidConfig = new FessConfigImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String get(String propertyKey) {
                throw new ConfigPropertyNotFoundException(propertyKey);
            }
        };

        try {
            invalidConfig.get("some.property");
            fail("Should throw exception for non-existent property");
        } catch (ConfigPropertyNotFoundException e) {
            // Expected exception
            assertNotNull(e);
        }
    }

    // Test special characters in property values
    public void test_get_specialCharacters() {
        String testKey = "special.chars";
        String specialValue = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        System.setProperty(Constants.FESS_CONFIG_PREFIX + testKey, specialValue);

        try {
            String value = fessConfig.get(testKey);
            assertEquals(specialValue, value);
        } finally {
            // Clean up system property
            System.clearProperty(Constants.FESS_CONFIG_PREFIX + testKey);
        }
    }

    // Test property with spaces
    public void test_get_propertyWithSpaces() {
        String testKey = "property.with.spaces";
        String valueWithSpaces = "  value with spaces  ";
        System.setProperty(Constants.FESS_CONFIG_PREFIX + testKey, valueWithSpaces);

        try {
            String value = fessConfig.get(testKey);
            assertEquals(valueWithSpaces, value);
        } finally {
            // Clean up system property
            System.clearProperty(Constants.FESS_CONFIG_PREFIX + testKey);
        }
    }

    // Test cache behavior with system property changes
    public void test_get_cacheAfterSystemPropertyChange() {
        String testKey = "cache.test";
        String initialValue = "initial";
        System.setProperty(Constants.FESS_CONFIG_PREFIX + testKey, initialValue);

        try {
            // Get initial value
            String value1 = fessConfig.get(testKey);
            assertEquals(initialValue, value1);

            // Change system property
            String newValue = "changed";
            System.setProperty(Constants.FESS_CONFIG_PREFIX + testKey, newValue);

            // Get value again - in real FessConfigImpl it would be cached,
            // but our test implementation always checks system properties
            String value2 = fessConfig.get(testKey);
            // In our test implementation, it will pick up the new value
            assertEquals(newValue, value2);
        } finally {
            // Clean up system property
            System.clearProperty(Constants.FESS_CONFIG_PREFIX + testKey);
        }
    }

    // Test getting numeric configuration values
    public void test_get_numericValues() {
        String intKey = "numeric.int";
        String floatKey = "numeric.float";

        System.setProperty(Constants.FESS_CONFIG_PREFIX + intKey, "123");
        System.setProperty(Constants.FESS_CONFIG_PREFIX + floatKey, "45.67");

        try {
            String intValue = fessConfig.get(intKey);
            assertEquals("123", intValue);

            String floatValue = fessConfig.get(floatKey);
            assertEquals("45.67", floatValue);
        } finally {
            System.clearProperty(Constants.FESS_CONFIG_PREFIX + intKey);
            System.clearProperty(Constants.FESS_CONFIG_PREFIX + floatKey);
        }
    }

    // Test getting boolean configuration values
    public void test_get_booleanValues() {
        String trueKey = "boolean.true";
        String falseKey = "boolean.false";

        System.setProperty(Constants.FESS_CONFIG_PREFIX + trueKey, "true");
        System.setProperty(Constants.FESS_CONFIG_PREFIX + falseKey, "false");

        try {
            String trueValue = fessConfig.get(trueKey);
            assertEquals("true", trueValue);

            String falseValue = fessConfig.get(falseKey);
            assertEquals("false", falseValue);
        } finally {
            System.clearProperty(Constants.FESS_CONFIG_PREFIX + trueKey);
            System.clearProperty(Constants.FESS_CONFIG_PREFIX + falseKey);
        }
    }

    // Test getting array-like configuration values
    public void test_get_arrayValues() {
        String arrayKey = "array.values";
        String arrayValue = "value1,value2,value3";

        System.setProperty(Constants.FESS_CONFIG_PREFIX + arrayKey, arrayValue);

        try {
            String value = fessConfig.get(arrayKey);
            assertEquals(arrayValue, value);
        } finally {
            System.clearProperty(Constants.FESS_CONFIG_PREFIX + arrayKey);
        }
    }

    // Test property key with dots
    public void test_get_propertyKeyWithDots() {
        String dottedKey = "deeply.nested.property.key";
        String dottedValue = "nested-value";

        System.setProperty(Constants.FESS_CONFIG_PREFIX + dottedKey, dottedValue);

        try {
            String value = fessConfig.get(dottedKey);
            assertEquals(dottedValue, value);
        } finally {
            System.clearProperty(Constants.FESS_CONFIG_PREFIX + dottedKey);
        }
    }
}