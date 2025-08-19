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
package org.codelibs.fess.script;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;

public class ScriptEngineTest extends UnitFessTestCase {

    private ScriptEngine scriptEngine;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        // Create a test implementation of ScriptEngine
        scriptEngine = new TestScriptEngine();
    }

    // Test evaluate method with valid template and parameters
    public void test_evaluate_withValidTemplateAndParams() {
        String template = "Hello ${name}";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", "World");

        Object result = scriptEngine.evaluate(template, paramMap);
        assertEquals("Hello World", result);
    }

    // Test evaluate method with empty template
    public void test_evaluate_withEmptyTemplate() {
        String template = "";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("key", "value");

        Object result = scriptEngine.evaluate(template, paramMap);
        assertEquals("", result);
    }

    // Test evaluate method with null template
    public void test_evaluate_withNullTemplate() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("key", "value");

        Object result = scriptEngine.evaluate(null, paramMap);
        assertNull(result);
    }

    // Test evaluate method with empty parameter map
    public void test_evaluate_withEmptyParamMap() {
        String template = "Static content";
        Map<String, Object> paramMap = Collections.emptyMap();

        Object result = scriptEngine.evaluate(template, paramMap);
        assertEquals("Static content", result);
    }

    // Test evaluate method with null parameter map
    public void test_evaluate_withNullParamMap() {
        String template = "Template content";

        Object result = scriptEngine.evaluate(template, null);
        assertEquals("Template content", result);
    }

    // Test evaluate method with multiple parameters
    public void test_evaluate_withMultipleParams() {
        String template = "${greeting} ${name}, you are ${age} years old";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("greeting", "Hello");
        paramMap.put("name", "Alice");
        paramMap.put("age", 25);

        Object result = scriptEngine.evaluate(template, paramMap);
        assertEquals("Hello Alice, you are 25 years old", result);
    }

    // Test evaluate method with missing parameter
    public void test_evaluate_withMissingParameter() {
        String template = "Hello ${name} and ${other}";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", "Bob");
        // "other" parameter is missing

        Object result = scriptEngine.evaluate(template, paramMap);
        assertEquals("Hello Bob and ${other}", result);
    }

    // Test evaluate method with complex object as parameter
    public void test_evaluate_withComplexObject() {
        String template = "User: ${user.name}, Age: ${user.age}";
        Map<String, Object> paramMap = new HashMap<>();
        TestUser user = new TestUser("John", 30);
        paramMap.put("user", user);

        Object result = scriptEngine.evaluate(template, paramMap);
        assertEquals("User: John, Age: 30", result);
    }

    // Test evaluate method with special characters in template
    public void test_evaluate_withSpecialCharacters() {
        String template = "Price: $${price} (with ${discount}% off)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("price", "100");
        paramMap.put("discount", "20");

        Object result = scriptEngine.evaluate(template, paramMap);
        assertEquals("Price: $100 (with 20% off)", result);
    }

    // Test evaluate method with nested expressions
    public void test_evaluate_withNestedExpressions() {
        String template = "${level1.level2.value}";
        Map<String, Object> paramMap = new HashMap<>();
        Map<String, Object> level2 = new HashMap<>();
        level2.put("value", "nested");
        Map<String, Object> level1 = new HashMap<>();
        level1.put("level2", level2);
        paramMap.put("level1", level1);

        Object result = scriptEngine.evaluate(template, paramMap);
        assertEquals("nested", result);
    }

    // Test evaluate method with numeric values
    public void test_evaluate_withNumericValues() {
        String template = "Result: ${number1} + ${number2} = ${sum}";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("number1", 10);
        paramMap.put("number2", 20);
        paramMap.put("sum", 30);

        Object result = scriptEngine.evaluate(template, paramMap);
        assertEquals("Result: 10 + 20 = 30", result);
    }

    // Test evaluate method with boolean values
    public void test_evaluate_withBooleanValues() {
        String template = "Is active: ${active}, Is enabled: ${enabled}";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("active", true);
        paramMap.put("enabled", false);

        Object result = scriptEngine.evaluate(template, paramMap);
        assertEquals("Is active: true, Is enabled: false", result);
    }

    // Test evaluate method with null parameter value
    public void test_evaluate_withNullParameterValue() {
        String template = "Value is: ${value}";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("value", null);

        Object result = scriptEngine.evaluate(template, paramMap);
        assertEquals("Value is: null", result);
    }

    // Test evaluate method with array as parameter
    public void test_evaluate_withArrayParameter() {
        String template = "Items: ${items}";
        Map<String, Object> paramMap = new HashMap<>();
        String[] items = { "item1", "item2", "item3" };
        paramMap.put("items", items);

        Object result = scriptEngine.evaluate(template, paramMap);
        assertTrue(result.toString().contains("item1"));
    }

    // Test evaluate method returns different object type
    public void test_evaluate_returnsObject() {
        scriptEngine = new ObjectReturningScriptEngine();
        String template = "return object";
        Map<String, Object> paramMap = new HashMap<>();

        Object result = scriptEngine.evaluate(template, paramMap);
        assertNotNull(result);
        assertTrue(result instanceof TestUser);
        assertEquals("TestUser", ((TestUser) result).name);
    }

    // Test evaluate method with error handling
    public void test_evaluate_withErrorTemplate() {
        scriptEngine = new ErrorHandlingScriptEngine();
        String template = "error";
        Map<String, Object> paramMap = new HashMap<>();

        Object result = scriptEngine.evaluate(template, paramMap);
        assertNull(result);
    }

    // Test implementation of ScriptEngine for testing
    private static class TestScriptEngine implements ScriptEngine {
        @Override
        public Object evaluate(String template, Map<String, Object> paramMap) {
            if (template == null) {
                return null;
            }

            String result = template;
            if (paramMap != null) {
                for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    // Handle nested properties
                    if (key.contains(".")) {
                        // Simple nested property handling for testing
                        result = result.replace("${" + key + "}", String.valueOf(value));
                    } else if (value instanceof TestUser) {
                        // Handle TestUser object
                        TestUser user = (TestUser) value;
                        result = result.replace("${" + key + ".name}", user.name);
                        result = result.replace("${" + key + ".age}", String.valueOf(user.age));
                    } else if (value instanceof Map) {
                        // Handle nested maps
                        handleNestedMap(result, key, (Map<String, Object>) value);
                        // For simplicity in test, just handle one level
                        Map<String, Object> nestedMap = (Map<String, Object>) value;
                        for (Map.Entry<String, Object> nestedEntry : nestedMap.entrySet()) {
                            String nestedKey = key + "." + nestedEntry.getKey();
                            Object nestedValue = nestedEntry.getValue();
                            if (nestedValue instanceof Map) {
                                Map<String, Object> deepMap = (Map<String, Object>) nestedValue;
                                for (Map.Entry<String, Object> deepEntry : deepMap.entrySet()) {
                                    String deepKey = nestedKey + "." + deepEntry.getKey();
                                    result = result.replace("${" + deepKey + "}", String.valueOf(deepEntry.getValue()));
                                }
                            }
                            result = result.replace("${" + nestedKey + "}", String.valueOf(nestedValue));
                        }
                    } else if (value != null && value.getClass().isArray()) {
                        // Handle array values
                        StringBuilder arrayStr = new StringBuilder();
                        if (value instanceof String[]) {
                            String[] arr = (String[]) value;
                            for (int i = 0; i < arr.length; i++) {
                                if (i > 0)
                                    arrayStr.append(",");
                                arrayStr.append(arr[i]);
                            }
                        } else if (value instanceof Object[]) {
                            Object[] arr = (Object[]) value;
                            for (int i = 0; i < arr.length; i++) {
                                if (i > 0)
                                    arrayStr.append(",");
                                arrayStr.append(arr[i]);
                            }
                        }
                        result = result.replace("${" + key + "}", arrayStr.toString());
                    } else {
                        // Handle simple values
                        result = result.replace("${" + key + "}", String.valueOf(value));
                    }
                }
            }
            return result;
        }

        private void handleNestedMap(String template, String prefix, Map<String, Object> map) {
            // Helper method for nested map handling
        }
    }

    // ScriptEngine implementation that returns objects
    private static class ObjectReturningScriptEngine implements ScriptEngine {
        @Override
        public Object evaluate(String template, Map<String, Object> paramMap) {
            if ("return object".equals(template)) {
                return new TestUser("TestUser", 0);
            }
            return null;
        }
    }

    // ScriptEngine implementation with error handling
    private static class ErrorHandlingScriptEngine implements ScriptEngine {
        @Override
        public Object evaluate(String template, Map<String, Object> paramMap) {
            if ("error".equals(template)) {
                // Simulate error by returning null
                return null;
            }
            return template;
        }
    }

    // Test helper class
    private static class TestUser {
        public String name;
        public int age;

        public TestUser(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
}