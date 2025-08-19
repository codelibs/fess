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

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;

public class AbstractScriptEngineTest extends UnitFessTestCase {

    private TestScriptEngine testScriptEngine;
    private ScriptEngineFactory scriptEngineFactory;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        scriptEngineFactory = new ScriptEngineFactory();
        ComponentUtil.register(scriptEngineFactory, "scriptEngineFactory");
        testScriptEngine = new TestScriptEngine();
    }

    @Override
    public void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    // Test default constructor
    public void test_constructor() {
        assertNotNull(testScriptEngine);
    }

    // Test register method
    public void test_register() {
        testScriptEngine.register();

        // Verify that the engine was registered with the factory
        ScriptEngine retrievedEngine = scriptEngineFactory.getScriptEngine("testEngine");
        assertNotNull(retrievedEngine);
        assertEquals(testScriptEngine, retrievedEngine);

        // Verify that the engine was also registered with its class name
        ScriptEngine retrievedByClassName = scriptEngineFactory.getScriptEngine("testscriptengine");
        assertNotNull(retrievedByClassName);
        assertEquals(testScriptEngine, retrievedByClassName);
    }

    // Test register method with multiple engines
    public void test_register_multipleEngines() {
        TestScriptEngine engine1 = new TestScriptEngine("engine1");
        TestScriptEngine engine2 = new TestScriptEngine("engine2");

        engine1.register();
        engine2.register();

        // Verify both engines are registered
        ScriptEngine retrieved1 = scriptEngineFactory.getScriptEngine("engine1");
        ScriptEngine retrieved2 = scriptEngineFactory.getScriptEngine("engine2");

        assertNotNull(retrieved1);
        assertNotNull(retrieved2);
        assertEquals(engine1, retrieved1);
        assertEquals(engine2, retrieved2);
        assertNotSame(retrieved1, retrieved2);
    }

    // Test register method with case insensitive name
    public void test_register_caseInsensitive() {
        testScriptEngine.register();

        // Test various case combinations
        ScriptEngine lowerCase = scriptEngineFactory.getScriptEngine("testengine");
        ScriptEngine upperCase = scriptEngineFactory.getScriptEngine("TESTENGINE");
        ScriptEngine mixedCase = scriptEngineFactory.getScriptEngine("TestEngine");

        assertNotNull(lowerCase);
        assertNotNull(upperCase);
        assertNotNull(mixedCase);
        assertEquals(testScriptEngine, lowerCase);
        assertEquals(testScriptEngine, upperCase);
        assertEquals(testScriptEngine, mixedCase);
    }

    // Test getName abstract method implementation
    public void test_getName() {
        assertEquals("testEngine", testScriptEngine.getName());
    }

    // Test evaluate method implementation
    public void test_evaluate() {
        Map<String, Object> params = new HashMap<>();
        params.put("key", "value");

        Object result = testScriptEngine.evaluate("template", params);
        assertEquals("processed: template with params", result);
    }

    // Test register with null factory throws exception
    public void test_register_nullFactory() {
        ComponentUtil.register(null, "scriptEngineFactory");

        try {
            testScriptEngine.register();
            fail("Should throw exception when factory is null");
        } catch (NullPointerException e) {
            // Expected exception
        }
    }

    // Test concrete implementation with different name
    public void test_differentNameImplementation() {
        TestScriptEngine customEngine = new TestScriptEngine("customName");
        assertEquals("customName", customEngine.getName());

        customEngine.register();
        ScriptEngine retrieved = scriptEngineFactory.getScriptEngine("customName");
        assertNotNull(retrieved);
        assertEquals(customEngine, retrieved);
    }

    // Test multiple registrations of the same engine
    public void test_multipleRegistrations() {
        testScriptEngine.register();
        testScriptEngine.register(); // Register again

        // Should still work without errors
        ScriptEngine retrieved = scriptEngineFactory.getScriptEngine("testEngine");
        assertNotNull(retrieved);
        assertEquals(testScriptEngine, retrieved);
    }

    // Test evaluate with null parameters
    public void test_evaluate_nullParams() {
        Object result = testScriptEngine.evaluate("template", null);
        assertEquals("processed: template with params", result);
    }

    // Test evaluate with empty parameters
    public void test_evaluate_emptyParams() {
        Map<String, Object> emptyParams = new HashMap<>();
        Object result = testScriptEngine.evaluate("template", emptyParams);
        assertEquals("processed: template with params", result);
    }

    // Test evaluate with null template
    public void test_evaluate_nullTemplate() {
        Map<String, Object> params = new HashMap<>();
        Object result = testScriptEngine.evaluate(null, params);
        assertEquals("processed: null with params", result);
    }

    // Test implementation class
    private static class TestScriptEngine extends AbstractScriptEngine {
        private final String name;

        public TestScriptEngine() {
            this("testEngine");
        }

        public TestScriptEngine(String name) {
            super();
            this.name = name;
        }

        @Override
        protected String getName() {
            return name;
        }

        @Override
        public Object evaluate(String template, Map<String, Object> paramMap) {
            // Simple implementation for testing
            return "processed: " + template + " with params";
        }
    }
}