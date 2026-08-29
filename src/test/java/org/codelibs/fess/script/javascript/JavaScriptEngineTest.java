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
package org.codelibs.fess.script.javascript;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.List;

import org.codelibs.fess.exception.JobProcessingException;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class JavaScriptEngineTest extends UnitFessTestCase {

    private JavaScriptEngine javaScriptEngine;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        javaScriptEngine = new JavaScriptEngine();
        javaScriptEngine.init();
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        if (javaScriptEngine != null) {
            javaScriptEngine.close();
        }
        super.tearDown(testInfo);
    }

    @Test
    public void test_evaluate_blankTemplate() {
        final Map<String, Object> params = new HashMap<>();
        assertNull(javaScriptEngine.evaluate(null, params));
        assertNull(javaScriptEngine.evaluate("", params));
        assertNull(javaScriptEngine.evaluate("   ", params));
    }

    @Test
    public void test_evaluate_expression() {
        final Map<String, Object> params = new HashMap<>();
        params.put("content", "hello world");
        params.put("boost1", Integer.valueOf(3));
        params.put("boost2", Integer.valueOf(4));

        assertEquals(11, javaScriptEngine.evaluate("content.length()", params));
        assertEquals(34.0d, javaScriptEngine.evaluate("10 * boost1 + boost2", params));
        assertEquals("hello world", javaScriptEngine.evaluate("content", params));
        assertEquals("literal", javaScriptEngine.evaluate("\"literal\"", params));
    }

    @Test
    public void test_evaluate_statementsWithReturn() {
        final Map<String, Object> params = new HashMap<>();
        params.put("value", Integer.valueOf(2));

        assertEquals(2, javaScriptEngine.evaluate("return value;", params));
        assertEquals(4.0d, javaScriptEngine.evaluate("var v = value * 2; return v;", params));
    }

    @Test
    public void test_evaluate_trailingLineComment() {
        final Map<String, Object> params = new HashMap<>();
        params.put("content", "abc");

        assertEquals(3, javaScriptEngine.evaluate("content.length() // trailing", params));
        assertEquals(1, javaScriptEngine.evaluate("return 1; // trailing", params));
    }

    @Test
    public void test_evaluate_es6() {
        final Map<String, Object> params = new HashMap<>();
        assertEquals("6", javaScriptEngine.evaluate("const [a, b, c] = [1, 2, 3]; return `${a + b + c}`;", params));
        assertEquals(42, javaScriptEngine.evaluate("const f = x => x * 2; return f(21);", params));
    }

    @Test
    public void test_evaluate_nullParamMap() {
        assertEquals(2, javaScriptEngine.evaluate("1 + 1", null));
    }

    @Test
    public void test_evaluate_containerIsBound() {
        final Map<String, Object> params = new HashMap<>();
        assertNotNull(javaScriptEngine.evaluate("return container;", params));
    }

    @Test
    public void test_evaluate_compilationFailureReturnsNull() {
        final Map<String, Object> params = new HashMap<>();
        assertNull(javaScriptEngine.evaluate("this is not ( valid javascript", params));
    }

    @Test
    public void test_evaluate_runtimeFailureReturnsNull() {
        final Map<String, Object> params = new HashMap<>();
        assertNull(javaScriptEngine.evaluate("undefinedVariable", params));
    }

    @Test
    public void test_evaluate_jobProcessingExceptionPropagates() {
        final Map<String, Object> params = new HashMap<>();
        params.put("thrower", new Runnable() {
            @Override
            public void run() {
                throw new JobProcessingException("boom");
            }
        });
        try {
            javaScriptEngine.evaluate("thrower.run(); return 1;", params);
            fail("JobProcessingException should be thrown");
        } catch (final JobProcessingException e) {
            assertTrue(e.getMessage().contains("boom"));
        }
    }

    @Test
    public void test_evaluate_concurrent_sharedTemplateCacheHit() throws Exception {
        // All 500 tasks evaluate the identical template, so the compiled-script cache is
        // populated once and the remaining tasks exercise concurrent eval() with fresh
        // bindings on a cache hit.
        final ExecutorService executor = Executors.newFixedThreadPool(8);
        final List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            final int n = i;
            futures.add(executor.submit(() -> {
                final Map<String, Object> params = new HashMap<>();
                params.put("n", Integer.valueOf(n));
                params.put("name", "job" + n);
                return String.valueOf(javaScriptEngine.evaluate("return name + ':' + (n + 1);", params));
            }));
        }
        try {
            for (int i = 0; i < futures.size(); i++) {
                assertEquals("job" + i + ":" + (i + 1), futures.get(i).get());
            }
        } finally {
            executor.shutdown();
        }
    }

    @Test
    public void test_evaluate_concurrent_distinctTemplatesCompileUnderContention() throws Exception {
        // Each task's template text embeds its own index, so every task misses the
        // compiled-script cache and compiles on the shared engine while the other threads
        // are compiling their own distinct templates at the same time. The expected string
        // combines a value baked into the template text (the index) with a value that can
        // only come from that task's own bindings (name and n), so cross-talk between
        // threads - whether from the cache or from bindings - would fail the assertion.
        final ExecutorService executor = Executors.newFixedThreadPool(8);
        final List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            final int n = i;
            futures.add(executor.submit(() -> {
                final Map<String, Object> params = new HashMap<>();
                params.put("n", Integer.valueOf(n));
                params.put("name", "job" + n);
                final String template = "return name + '#" + n + ":' + n;";
                return String.valueOf(javaScriptEngine.evaluate(template, params));
            }));
        }
        try {
            for (int i = 0; i < futures.size(); i++) {
                assertEquals("job" + i + "#" + i + ":" + i, futures.get(i).get());
            }
        } finally {
            executor.shutdown();
        }
    }

    @Test
    public void test_getNameAndAliases() {
        assertEquals("javascript", javaScriptEngine.getName());
        final String[] aliases = javaScriptEngine.getAliases();
        assertEquals(2, aliases.length);
        assertEquals("js", aliases[0]);
        assertEquals("sai", aliases[1]);
    }
}
