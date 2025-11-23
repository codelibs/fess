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

import org.codelibs.fess.exception.ScriptEngineException;
import org.codelibs.fess.unit.UnitFessTestCase;

public class ScriptEngineFactoryTest extends UnitFessTestCase {

    private ScriptEngineFactory scriptEngineFactory;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        scriptEngineFactory = new ScriptEngineFactory();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    // Test constructor
    public void test_constructor() {
        ScriptEngineFactory factory = new ScriptEngineFactory();
        assertNotNull(factory);
        assertNotNull(factory.scriptEngineMap);
        assertTrue(factory.scriptEngineMap.isEmpty());
    }

    // Test add method with valid parameters
    public void test_add_validParameters() {
        TestScriptEngine engine = new TestScriptEngine();
        scriptEngineFactory.add("testEngine", engine);

        // Verify engine is accessible by name
        ScriptEngine retrieved = scriptEngineFactory.getScriptEngine("testengine");
        assertNotNull(retrieved);
        assertEquals(engine, retrieved);

        // Verify engine is accessible by class simple name
        ScriptEngine retrievedByClass = scriptEngineFactory.getScriptEngine("testscriptengine");
        assertNotNull(retrievedByClass);
        assertEquals(engine, retrievedByClass);
    }

    // Test add method with uppercase name
    public void test_add_uppercaseName() {
        TestScriptEngine engine = new TestScriptEngine();
        scriptEngineFactory.add("TESTENGINE", engine);

        // Should be accessible with lowercase
        ScriptEngine retrieved = scriptEngineFactory.getScriptEngine("testengine");
        assertNotNull(retrieved);
        assertEquals(engine, retrieved);
    }

    // Test add method with mixed case name
    public void test_add_mixedCaseName() {
        TestScriptEngine engine = new TestScriptEngine();
        scriptEngineFactory.add("TestEngine", engine);

        // Should be accessible with lowercase
        ScriptEngine retrieved = scriptEngineFactory.getScriptEngine("testengine");
        assertNotNull(retrieved);
        assertEquals(engine, retrieved);
    }

    // Test add method with null name
    public void test_add_nullName() {
        TestScriptEngine engine = new TestScriptEngine();
        try {
            scriptEngineFactory.add(null, engine);
            fail("Should throw IllegalArgumentException for null name");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Both name and scriptEngine parameters are required"));
            assertTrue(e.getMessage().contains("name: null"));
        }
    }

    // Test add method with null engine
    public void test_add_nullEngine() {
        try {
            scriptEngineFactory.add("test", null);
            fail("Should throw IllegalArgumentException for null engine");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Both name and scriptEngine parameters are required"));
            assertTrue(e.getMessage().contains("scriptEngine: null"));
        }
    }

    // Test add method with both null parameters
    public void test_add_bothNull() {
        try {
            scriptEngineFactory.add(null, null);
            fail("Should throw IllegalArgumentException for null parameters");
        } catch (IllegalArgumentException e) {
            assertEquals("Both name and scriptEngine parameters are required. name: null, scriptEngine: null", e.getMessage());
        }
    }

    // Test add multiple engines
    public void test_add_multipleEngines() {
        TestScriptEngine engine1 = new TestScriptEngine("engine1");
        TestScriptEngine engine2 = new TestScriptEngine("engine2");
        CustomScriptEngine customEngine = new CustomScriptEngine();

        scriptEngineFactory.add("engine1", engine1);
        scriptEngineFactory.add("engine2", engine2);
        scriptEngineFactory.add("custom", customEngine);

        // Verify all engines are accessible by their registered names
        assertEquals(engine1, scriptEngineFactory.getScriptEngine("engine1"));
        assertEquals(engine2, scriptEngineFactory.getScriptEngine("engine2"));
        assertEquals(customEngine, scriptEngineFactory.getScriptEngine("custom"));

        // Verify class name access - last added engine of same class wins
        assertEquals(engine2, scriptEngineFactory.getScriptEngine("testscriptengine"));
        assertEquals(customEngine, scriptEngineFactory.getScriptEngine("customscriptengine"));
    }

    // Test add with overwriting existing engine
    public void test_add_overwriteExisting() {
        TestScriptEngine engine1 = new TestScriptEngine("first");
        TestScriptEngine engine2 = new TestScriptEngine("second");

        scriptEngineFactory.add("test", engine1);
        scriptEngineFactory.add("test", engine2);

        // Should get the second engine
        ScriptEngine retrieved = scriptEngineFactory.getScriptEngine("test");
        assertEquals(engine2, retrieved);
    }

    // Test getScriptEngine with valid name
    public void test_getScriptEngine_validName() {
        TestScriptEngine engine = new TestScriptEngine();
        scriptEngineFactory.add("myEngine", engine);

        ScriptEngine retrieved = scriptEngineFactory.getScriptEngine("myengine");
        assertNotNull(retrieved);
        assertEquals(engine, retrieved);
    }

    // Test getScriptEngine with uppercase query
    public void test_getScriptEngine_uppercaseQuery() {
        TestScriptEngine engine = new TestScriptEngine();
        scriptEngineFactory.add("test", engine);

        ScriptEngine retrieved = scriptEngineFactory.getScriptEngine("TEST");
        assertNotNull(retrieved);
        assertEquals(engine, retrieved);
    }

    // Test getScriptEngine with mixed case query
    public void test_getScriptEngine_mixedCaseQuery() {
        TestScriptEngine engine = new TestScriptEngine();
        scriptEngineFactory.add("test", engine);

        ScriptEngine retrieved = scriptEngineFactory.getScriptEngine("TeSt");
        assertNotNull(retrieved);
        assertEquals(engine, retrieved);
    }

    // Test getScriptEngine with null name
    public void test_getScriptEngine_nullName() {
        try {
            scriptEngineFactory.getScriptEngine(null);
            fail("Should throw ScriptEngineException for null name");
        } catch (ScriptEngineException e) {
            assertEquals("Script engine name parameter is null. A valid script engine name must be provided.", e.getMessage());
        }
    }

    // Test getScriptEngine with non-existent name
    public void test_getScriptEngine_nonExistentName() {
        try {
            scriptEngineFactory.getScriptEngine("nonexistent");
            fail("Should throw ScriptEngineException for non-existent engine");
        } catch (ScriptEngineException e) {
            assertEquals("nonexistent is not found.", e.getMessage());
        }
    }

    // Test getScriptEngine with empty string
    public void test_getScriptEngine_emptyString() {
        try {
            scriptEngineFactory.getScriptEngine("");
            fail("Should throw ScriptEngineException for empty string");
        } catch (ScriptEngineException e) {
            assertEquals(" is not found.", e.getMessage());
        }
    }

    // Test getScriptEngine by class name
    public void test_getScriptEngine_byClassName() {
        TestScriptEngine engine = new TestScriptEngine();
        scriptEngineFactory.add("myName", engine);

        // Should be accessible by class simple name
        ScriptEngine retrieved = scriptEngineFactory.getScriptEngine("TestScriptEngine");
        assertNotNull(retrieved);
        assertEquals(engine, retrieved);
    }

    // Test add with special characters in name
    public void test_add_specialCharactersInName() {
        TestScriptEngine engine = new TestScriptEngine();
        scriptEngineFactory.add("test-engine.v1", engine);

        ScriptEngine retrieved = scriptEngineFactory.getScriptEngine("test-engine.v1");
        assertNotNull(retrieved);
        assertEquals(engine, retrieved);
    }

    // Test add with whitespace in name
    public void test_add_whitespaceInName() {
        TestScriptEngine engine = new TestScriptEngine();
        scriptEngineFactory.add("  test  ", engine);

        // Should be stored with lowercase and original spaces
        ScriptEngine retrieved = scriptEngineFactory.getScriptEngine("  test  ");
        assertNotNull(retrieved);
        assertEquals(engine, retrieved);
    }

    // Test case sensitivity preservation in error messages
    public void test_getScriptEngine_errorMessagePreservesCase() {
        try {
            scriptEngineFactory.getScriptEngine("MyNonExistentEngine");
            fail("Should throw ScriptEngineException");
        } catch (ScriptEngineException e) {
            assertEquals("MyNonExistentEngine is not found.", e.getMessage());
        }
    }

    // Test engine replacement by class name
    public void test_add_replacementByClassName() {
        TestScriptEngine engine1 = new TestScriptEngine("first");
        TestScriptEngine engine2 = new TestScriptEngine("second");

        scriptEngineFactory.add("name1", engine1);
        scriptEngineFactory.add("name2", engine2);

        // Both engines have same class name, so second should replace first for class name lookup
        ScriptEngine retrievedByClass = scriptEngineFactory.getScriptEngine("testscriptengine");
        assertEquals(engine2, retrievedByClass);

        // But original names should still work
        assertEquals(engine1, scriptEngineFactory.getScriptEngine("name1"));
        assertEquals(engine2, scriptEngineFactory.getScriptEngine("name2"));
    }

    // Test with engine that has very long class name
    public void test_add_longClassName() {
        VeryLongClassNameScriptEngineImplementation engine = new VeryLongClassNameScriptEngineImplementation();
        scriptEngineFactory.add("short", engine);

        // Should be accessible by long class name
        ScriptEngine retrieved = scriptEngineFactory.getScriptEngine("verylongclassnamescriptengineimplementation");
        assertNotNull(retrieved);
        assertEquals(engine, retrieved);
    }

    // Test concurrent-like scenario (sequential but simulating concurrent behavior)
    public void test_add_multipleSameNameSequential() {
        TestScriptEngine engine1 = new TestScriptEngine("v1");
        TestScriptEngine engine2 = new TestScriptEngine("v2");
        TestScriptEngine engine3 = new TestScriptEngine("v3");

        scriptEngineFactory.add("engine", engine1);
        scriptEngineFactory.add("engine", engine2);
        scriptEngineFactory.add("engine", engine3);

        // Should get the last one
        ScriptEngine retrieved = scriptEngineFactory.getScriptEngine("engine");
        assertEquals(engine3, retrieved);
    }

    // Test implementation classes
    private static class TestScriptEngine implements ScriptEngine {
        private final String identifier;

        public TestScriptEngine() {
            this("default");
        }

        public TestScriptEngine(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public Object evaluate(String template, Map<String, Object> paramMap) {
            return "TestEngine[" + identifier + "]: " + template;
        }
    }

    private static class CustomScriptEngine implements ScriptEngine {
        @Override
        public Object evaluate(String template, Map<String, Object> paramMap) {
            return "CustomEngine: " + template;
        }
    }

    private static class VeryLongClassNameScriptEngineImplementation implements ScriptEngine {
        @Override
        public Object evaluate(String template, Map<String, Object> paramMap) {
            return "LongNameEngine: " + template;
        }
    }
}