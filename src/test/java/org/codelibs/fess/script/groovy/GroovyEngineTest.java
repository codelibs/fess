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
package org.codelibs.fess.script.groovy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.exception.JobProcessingException;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;

import groovy.lang.GroovyClassLoader;

public class GroovyEngineTest extends UnitFessTestCase {
    public GroovyEngine groovyEngine;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        groovyEngine = new GroovyEngine();
    }

    @Override
    public void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    public void test_evaluate() {
        final Map<String, Object> params = new HashMap<>();
        assertNull(groovyEngine.evaluate("", params));
        assertEquals("", groovyEngine.evaluate("return ''", params));
        assertEquals(1, groovyEngine.evaluate("return 1", params));

        params.put("test", "123");
        assertEquals("123", groovyEngine.evaluate("return test", params));
    }

    public void test_getName() {
        assertEquals("groovy", groovyEngine.getName());
    }

    // ===== Null-Safety Tests =====

    /**
     * Test that evaluate returns null for null template
     */
    public void test_evaluate_nullTemplate() {
        final Map<String, Object> params = new HashMap<>();
        params.put("test", "value");
        assertNull(groovyEngine.evaluate(null, params));
    }

    /**
     * Test that evaluate returns null for empty string template
     */
    public void test_evaluate_emptyTemplate() {
        final Map<String, Object> params = new HashMap<>();
        params.put("test", "value");
        assertNull(groovyEngine.evaluate("", params));
    }

    /**
     * Test that evaluate returns null for whitespace-only template
     */
    public void test_evaluate_whitespaceTemplate() {
        final Map<String, Object> params = new HashMap<>();
        params.put("test", "value");
        assertNull(groovyEngine.evaluate("   ", params));
        assertNull(groovyEngine.evaluate("\t", params));
        assertNull(groovyEngine.evaluate("\n", params));
        assertNull(groovyEngine.evaluate("  \t\n  ", params));
    }

    /**
     * Test that evaluate handles null paramMap safely
     */
    public void test_evaluate_nullParamMap() {
        assertEquals("test", groovyEngine.evaluate("return 'test'", null));
        assertEquals(123, groovyEngine.evaluate("return 123", null));
    }

    /**
     * Test that evaluate handles empty paramMap
     */
    public void test_evaluate_emptyParamMap() {
        final Map<String, Object> emptyMap = Collections.emptyMap();
        assertEquals("result", groovyEngine.evaluate("return 'result'", emptyMap));
    }

    /**
     * Test that evaluate handles paramMap with null values
     */
    public void test_evaluate_paramMapWithNullValues() {
        final Map<String, Object> params = new HashMap<>();
        params.put("key1", null);
        params.put("key2", "value");
        assertEquals("value", groovyEngine.evaluate("return key2", params));
        assertNull(groovyEngine.evaluate("return key1", params));
    }

    // ===== Parameter Binding Tests =====

    /**
     * Test that parameters are correctly bound to the script
     */
    public void test_evaluate_parameterBinding() {
        final Map<String, Object> params = new HashMap<>();
        params.put("x", 10);
        params.put("y", 20);
        assertEquals(30, groovyEngine.evaluate("return x + y", params));
    }

    /**
     * Test that multiple parameters of different types are bound correctly
     */
    public void test_evaluate_multipleParameterTypes() {
        final Map<String, Object> params = new HashMap<>();
        params.put("str", "hello");
        params.put("num", 42);
        params.put("bool", true);
        params.put("dbl", 3.14);

        assertEquals("hello", groovyEngine.evaluate("return str", params));
        assertEquals(42, groovyEngine.evaluate("return num", params));
        assertEquals(true, groovyEngine.evaluate("return bool", params));
        assertEquals(3.14, groovyEngine.evaluate("return dbl", params));
    }

    /**
     * Test that complex objects can be passed as parameters
     */
    public void test_evaluate_complexObjectParameters() {
        final Map<String, Object> params = new HashMap<>();
        final Map<String, String> nestedMap = new HashMap<>();
        nestedMap.put("name", "John");
        nestedMap.put("role", "Developer");
        params.put("user", nestedMap);

        assertEquals("John", groovyEngine.evaluate("return user.name", params));
        assertEquals("Developer", groovyEngine.evaluate("return user.role", params));
    }

    /**
     * Test that arrays can be passed and manipulated
     */
    public void test_evaluate_arrayParameters() {
        final Map<String, Object> params = new HashMap<>();
        final String[] items = { "apple", "banana", "cherry" };
        params.put("items", items);

        assertEquals(3, groovyEngine.evaluate("return items.length", params));
        assertEquals("banana", groovyEngine.evaluate("return items[1]", params));
    }

    // ===== DI Container Integration Tests =====

    /**
     * Test that DI container is available in the script
     */
    public void test_evaluate_containerAvailable() {
        final Map<String, Object> params = new HashMap<>();
        assertNotNull(groovyEngine.evaluate("return container", params));
    }

    /**
     * Test that container is accessible even with null paramMap
     */
    public void test_evaluate_containerAvailableWithNullParams() {
        assertNotNull(groovyEngine.evaluate("return container", null));
    }

    // ===== Exception Handling Tests =====

    /**
     * Test that JobProcessingException is propagated
     */
    public void test_evaluate_jobProcessingExceptionPropagates() {
        final Map<String, Object> params = new HashMap<>();
        try {
            groovyEngine.evaluate("throw new org.codelibs.fess.exception.JobProcessingException('test error')", params);
            fail("Expected JobProcessingException to be thrown");
        } catch (final JobProcessingException e) {
            assertEquals("test error", e.getMessage());
        }
    }

    /**
     * Test that generic exceptions are caught and null is returned
     */
    public void test_evaluate_genericExceptionReturnsNull() {
        final Map<String, Object> params = new HashMap<>();
        // Invalid script that will cause an exception
        assertNull(groovyEngine.evaluate("return nonExistentVariable", params));
    }

    /**
     * Test that syntax errors return null
     */
    public void test_evaluate_syntaxErrorReturnsNull() {
        final Map<String, Object> params = new HashMap<>();
        assertNull(groovyEngine.evaluate("this is not valid groovy code {{{", params));
    }

    /**
     * Test that runtime exceptions in scripts return null
     */
    public void test_evaluate_runtimeExceptionReturnsNull() {
        final Map<String, Object> params = new HashMap<>();
        // Division by zero
        assertNull(groovyEngine.evaluate("return 1 / 0", params));
    }

    // ===== Script Execution Tests =====

    /**
     * Test that simple arithmetic works
     */
    public void test_evaluate_arithmetic() {
        final Map<String, Object> params = new HashMap<>();
        params.put("a", 5);
        params.put("b", 3);

        assertEquals(8, groovyEngine.evaluate("return a + b", params));
        assertEquals(2, groovyEngine.evaluate("return a - b", params));
        assertEquals(15, groovyEngine.evaluate("return a * b", params));
    }

    /**
     * Test that string operations work
     */
    public void test_evaluate_stringOperations() {
        final Map<String, Object> params = new HashMap<>();
        params.put("first", "Hello");
        params.put("second", "World");

        assertEquals("HelloWorld", groovyEngine.evaluate("return first + second", params));
        assertEquals("HELLO", groovyEngine.evaluate("return first.toUpperCase()", params));
        assertEquals(5, groovyEngine.evaluate("return first.length()", params));
    }

    /**
     * Test that conditional logic works
     */
    public void test_evaluate_conditionalLogic() {
        final Map<String, Object> params = new HashMap<>();
        params.put("score", 85);

        assertEquals("pass", groovyEngine.evaluate("return score >= 60 ? 'pass' : 'fail'", params));

        params.put("score", 45);
        assertEquals("fail", groovyEngine.evaluate("return score >= 60 ? 'pass' : 'fail'", params));
    }

    /**
     * Test that loops work
     */
    public void test_evaluate_loops() {
        final Map<String, Object> params = new HashMap<>();
        params.put("n", 5);

        final String script = "def sum = 0; for (int i = 1; i <= n; i++) { sum += i }; return sum";
        assertEquals(15, groovyEngine.evaluate(script, params));
    }

    /**
     * Test that closures work
     */
    public void test_evaluate_closures() {
        final Map<String, Object> params = new HashMap<>();
        final String script = "def multiply = { x, y -> x * y }; return multiply(6, 7)";
        assertEquals(42, groovyEngine.evaluate(script, params));
    }

    /**
     * Test that list operations work
     */
    public void test_evaluate_listOperations() {
        final Map<String, Object> params = new HashMap<>();
        final String script = "def list = [1, 2, 3, 4, 5]; return list.sum()";
        assertEquals(15, groovyEngine.evaluate(script, params));
    }

    /**
     * Test that map operations work
     */
    public void test_evaluate_mapOperations() {
        final Map<String, Object> params = new HashMap<>();
        final String script = "def map = [name: 'Alice', age: 30]; return map.name + ' is ' + map.age";
        assertEquals("Alice is 30", groovyEngine.evaluate(script, params));
    }

    // ===== Multi-line Script Tests =====

    /**
     * Test that multi-line scripts work
     */
    public void test_evaluate_multilineScript() {
        final Map<String, Object> params = new HashMap<>();
        params.put("x", 10);

        final String script = "def result = x * 2\n" + "result = result + 5\n" + "return result";

        assertEquals(25, groovyEngine.evaluate(script, params));
    }

    /**
     * Test that complex multi-line logic works
     */
    public void test_evaluate_complexMultilineLogic() {
        final Map<String, Object> params = new HashMap<>();
        params.put("items", new int[] { 1, 2, 3, 4, 5 });

        final String script = "def sum = 0\n" + "items.each { item ->\n" + "  if (item % 2 == 0) {\n" + "    sum += item\n"
                + "  }\n" + "}\n" + "return sum";

        assertEquals(6, groovyEngine.evaluate(script, params)); // 2 + 4 = 6
    }

    // ===== Return Value Tests =====

    /**
     * Test that various return types work correctly
     */
    public void test_evaluate_variousReturnTypes() {
        final Map<String, Object> params = new HashMap<>();

        // String
        assertEquals("text", groovyEngine.evaluate("return 'text'", params));

        // Integer
        assertEquals(42, groovyEngine.evaluate("return 42", params));

        // Long
        assertEquals(1000000L, groovyEngine.evaluate("return 1000000L", params));

        // Double
        assertEquals(3.14159, groovyEngine.evaluate("return 3.14159", params));

        // Boolean
        assertEquals(true, groovyEngine.evaluate("return true", params));
        assertEquals(false, groovyEngine.evaluate("return false", params));

        // Null
        assertNull(groovyEngine.evaluate("return null", params));
    }

    /**
     * Test that collections can be returned
     */
    public void test_evaluate_returnCollections() {
        final Map<String, Object> params = new HashMap<>();

        // List
        final Object listResult = groovyEngine.evaluate("return [1, 2, 3]", params);
        assertNotNull(listResult);
        assertTrue(listResult instanceof java.util.List);

        // Map
        final Object mapResult = groovyEngine.evaluate("return [key: 'value']", params);
        assertNotNull(mapResult);
        assertTrue(mapResult instanceof java.util.Map);
    }

    // ===== Edge Cases =====

    /**
     * Test very long scripts
     */
    public void test_evaluate_longScript() {
        final Map<String, Object> params = new HashMap<>();
        final StringBuilder sb = new StringBuilder();
        sb.append("def result = 0\n");
        for (int i = 0; i < 100; i++) {
            sb.append("result += 1\n");
        }
        sb.append("return result");

        assertEquals(100, groovyEngine.evaluate(sb.toString(), params));
    }

    /**
     * Test script with special characters
     */
    public void test_evaluate_specialCharacters() {
        final Map<String, Object> params = new HashMap<>();
        params.put("text", "Hello \"World\" with 'quotes'");
        assertEquals("Hello \"World\" with 'quotes'", groovyEngine.evaluate("return text", params));
    }

    /**
     * Test script with unicode characters
     */
    public void test_evaluate_unicodeCharacters() {
        final Map<String, Object> params = new HashMap<>();
        params.put("japanese", "こんにちは");
        params.put("emoji", "😀");

        assertEquals("こんにちは", groovyEngine.evaluate("return japanese", params));
        assertEquals("😀", groovyEngine.evaluate("return emoji", params));
    }

    /**
     * Test that empty return statement returns null
     */
    public void test_evaluate_emptyReturnStatement() {
        final Map<String, Object> params = new HashMap<>();
        assertNull(groovyEngine.evaluate("def x = 1; return", params));
    }

    /**
     * Test script without explicit return (implicit return)
     */
    public void test_evaluate_implicitReturn() {
        final Map<String, Object> params = new HashMap<>();
        params.put("x", 5);
        // In Groovy, the last expression is implicitly returned
        assertEquals(10, groovyEngine.evaluate("x * 2", params));
    }
}
