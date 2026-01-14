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
package org.codelibs.fess.job.impl;

import java.util.Map;

import org.codelibs.fess.exception.ScriptEngineException;
import org.codelibs.fess.job.JobExecutor;
import org.codelibs.fess.script.ScriptEngine;
import org.codelibs.fess.script.ScriptEngineFactory;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class ScriptExecutorTest extends UnitFessTestCase {

    private ScriptExecutor scriptExecutor;
    private ScriptEngineFactory scriptEngineFactory;
    private TestScriptEngine testScriptEngine;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        scriptExecutor = new ScriptExecutor();
        scriptEngineFactory = new ScriptEngineFactory();
        testScriptEngine = new TestScriptEngine();
        ComponentUtil.register(scriptEngineFactory, "scriptEngineFactory");
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        ComponentUtil.register(null, "scriptEngineFactory");
        super.tearDown(testInfo);
    }

    @Test
    public void test_constructor() {
        // Test that constructor creates an instance
        ScriptExecutor executor = new ScriptExecutor();
        assertNotNull(executor);
        assertTrue(executor instanceof JobExecutor);
    }

    @Test
    public void test_execute_withValidScriptType() {
        // Setup test script engine
        scriptEngineFactory.add("test", testScriptEngine);

        // Execute script
        String scriptType = "test";
        String script = "test script content";
        Object result = scriptExecutor.execute(scriptType, script);

        // Verify result
        assertEquals("processed: test script content", result);

        // Verify executor was passed in params
        Map<String, Object> lastParams = testScriptEngine.getLastParams();
        assertNotNull(lastParams);
        assertEquals(scriptExecutor, lastParams.get("executor"));
    }

    @Test
    public void test_execute_withMultipleEngines() {
        // Setup multiple script engines
        TestScriptEngine engine1 = new TestScriptEngine("engine1");
        TestScriptEngine engine2 = new TestScriptEngine("engine2");
        scriptEngineFactory.add("type1", engine1);
        scriptEngineFactory.add("type2", engine2);

        // Execute with first engine
        Object result1 = scriptExecutor.execute("type1", "script for engine1");
        assertEquals("engine1:processed: script for engine1", result1);

        // Execute with second engine
        Object result2 = scriptExecutor.execute("type2", "script for engine2");
        assertEquals("engine2:processed: script for engine2", result2);

        // Verify correct engine was called each time
        assertEquals("script for engine1", engine1.getLastScript());
        assertEquals("script for engine2", engine2.getLastScript());
    }

    @Test
    public void test_execute_withInvalidScriptType() {
        // Try to execute with non-existent script type
        try {
            scriptExecutor.execute("nonexistent", "some script");
            fail("Expected ScriptEngineException");
        } catch (ScriptEngineException e) {
            assertTrue(e.getMessage().contains("nonexistent"));
        }
    }

    @Test
    public void test_execute_withNullScriptType() {
        // Try to execute with null script type
        try {
            scriptExecutor.execute(null, "some script");
            fail("Expected ScriptEngineException");
        } catch (ScriptEngineException e) {
            assertTrue(e.getMessage().contains("null"));
        }
    }

    @Test
    public void test_execute_withNullScript() {
        // Setup test script engine
        scriptEngineFactory.add("test", testScriptEngine);

        // Execute with null script
        Object result = scriptExecutor.execute("test", null);

        // Verify null was passed through
        assertEquals("processed: null", result);
        assertNull(testScriptEngine.getLastScript());
    }

    @Test
    public void test_execute_withEmptyScript() {
        // Setup test script engine
        scriptEngineFactory.add("test", testScriptEngine);

        // Execute with empty script
        Object result = scriptExecutor.execute("test", "");

        // Verify empty string was processed
        assertEquals("processed: ", result);
        assertEquals("", testScriptEngine.getLastScript());
    }

    @Test
    public void test_execute_scriptEngineReturnsNull() {
        // Setup engine that returns null
        scriptEngineFactory.add("nullEngine", new ScriptEngine() {
            @Override
            public Object evaluate(String template, Map<String, Object> paramMap) {
                return null;
            }
        });

        // Execute and verify null result
        Object result = scriptExecutor.execute("nullEngine", "test");
        assertNull(result);
    }

    @Test
    public void test_execute_scriptEngineThrowsException() {
        // Setup engine that throws exception
        scriptEngineFactory.add("errorEngine", new ScriptEngine() {
            @Override
            public Object evaluate(String template, Map<String, Object> paramMap) {
                throw new RuntimeException("Script evaluation failed");
            }
        });

        // Execute and verify exception is propagated
        try {
            scriptExecutor.execute("errorEngine", "test");
            fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            assertEquals("Script evaluation failed", e.getMessage());
        }
    }

    @Test
    public void test_execute_caseInsensitiveScriptType() {
        // Setup test script engine
        scriptEngineFactory.add("TestEngine", testScriptEngine);

        // Execute with different case variations
        Object result1 = scriptExecutor.execute("testengine", "script1");
        assertEquals("processed: script1", result1);

        Object result2 = scriptExecutor.execute("TESTENGINE", "script2");
        assertEquals("processed: script2", result2);

        Object result3 = scriptExecutor.execute("TestEngine", "script3");
        assertEquals("processed: script3", result3);
    }

    @Test
    public void test_execute_verifyParamsNotModifiable() {
        // Setup engine that tries to modify params
        scriptEngineFactory.add("modifyEngine", new ScriptEngine() {
            @Override
            public Object evaluate(String template, Map<String, Object> paramMap) {
                // Try to add new param
                paramMap.put("newKey", "newValue");
                return paramMap.size();
            }
        });

        // Execute first time
        Object result1 = scriptExecutor.execute("modifyEngine", "script1");
        assertEquals(2, result1); // executor + newKey

        // Execute second time - params should be fresh
        Object result2 = scriptExecutor.execute("modifyEngine", "script2");
        assertEquals(2, result2); // Should still be 2, not accumulating
    }

    @Test
    public void test_shutdown_withListener() {
        // Add shutdown listener
        TestShutdownListener listener = new TestShutdownListener();
        scriptExecutor.addShutdownListener(listener);

        // Verify listener not called yet
        assertFalse(listener.wasShutdownCalled());

        // Call shutdown
        scriptExecutor.shutdown();

        // Verify listener was called
        assertTrue(listener.wasShutdownCalled());
    }

    @Test
    public void test_shutdown_withoutListener() {
        // Call shutdown without setting listener
        try {
            scriptExecutor.shutdown();
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected behavior when no listener is set
        }
    }

    @Test
    public void test_addShutdownListener_replacesExisting() {
        // Add first listener
        TestShutdownListener listener1 = new TestShutdownListener();
        scriptExecutor.addShutdownListener(listener1);

        // Add second listener (should replace first)
        TestShutdownListener listener2 = new TestShutdownListener();
        scriptExecutor.addShutdownListener(listener2);

        // Call shutdown
        scriptExecutor.shutdown();

        // Verify only second listener was called
        assertFalse(listener1.wasShutdownCalled());
        assertTrue(listener2.wasShutdownCalled());
    }

    @Test
    public void test_addShutdownListener_null() {
        // Add null listener
        scriptExecutor.addShutdownListener(null);

        // Try to shutdown
        try {
            scriptExecutor.shutdown();
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected when null listener is set
        }
    }

    // Test helper classes
    private static class TestScriptEngine implements ScriptEngine {
        private String lastScript;
        private Map<String, Object> lastParams;
        private final String prefix;

        public TestScriptEngine() {
            this.prefix = "";
        }

        public TestScriptEngine(String prefix) {
            this.prefix = prefix + ":";
        }

        @Override
        public Object evaluate(String template, Map<String, Object> paramMap) {
            this.lastScript = template;
            this.lastParams = paramMap;
            return prefix + "processed: " + template;
        }

        public String getLastScript() {
            return lastScript;
        }

        public Map<String, Object> getLastParams() {
            return lastParams;
        }
    }

    private static class TestShutdownListener implements JobExecutor.ShutdownListener {
        private boolean shutdownCalled = false;

        @Override
        public void onShutdown() {
            shutdownCalled = true;
        }

        public boolean wasShutdownCalled() {
            return shutdownCalled;
        }
    }
}