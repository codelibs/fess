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
package org.codelibs.fess.indexer;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.Constants;
import org.codelibs.fess.exception.JobProcessingException;
import org.codelibs.fess.script.AbstractScriptEngine;
import org.codelibs.fess.script.ScriptEngineFactory;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;

public class DocBoostMatcherTest extends UnitFessTestCase {

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ScriptEngineFactory scriptEngineFactory = new ScriptEngineFactory();
        ComponentUtil.register(scriptEngineFactory, "scriptEngineFactory");
        new AbstractScriptEngine() {

            @Override
            public Object evaluate(String template, Map<String, Object> paramMap) {
                final Map<String, Object> bindingMap = new HashMap<>(paramMap);
                bindingMap.put("container", SingletonLaContainerFactory.getContainer());
                final GroovyShell groovyShell = new GroovyShell(new Binding(bindingMap));
                try {
                    return groovyShell.evaluate(template);
                } catch (final JobProcessingException e) {
                    throw e;
                } catch (final Exception e) {
                    return null;
                } finally {
                    final GroovyClassLoader loader = groovyShell.getClassLoader();
                    loader.clearCache();
                }
            }

            @Override
            protected String getName() {
                return Constants.DEFAULT_SCRIPT;
            }
        }.register();
    }

    @Test
    public void test_integer() {
        final DocBoostMatcher docBoostMatcher = new DocBoostMatcher();
        docBoostMatcher.setBoostExpression("10");
        docBoostMatcher.setMatchExpression("data1 > 10");

        final Map<String, Object> map = new HashMap<String, Object>();

        assertTrue(0.0f == docBoostMatcher.getValue(map));

        map.put("data1", 20);
        assertTrue(docBoostMatcher.match(map));

        map.put("data1", 5);
        assertFalse(docBoostMatcher.match(map));

        map.remove("data1");
        assertFalse(docBoostMatcher.match(map));

        map.put("data2", 5);
        assertFalse(docBoostMatcher.match(map));
    }

    @Test
    public void test_string() {
        final DocBoostMatcher docBoostMatcher = new DocBoostMatcher();
        docBoostMatcher.setBoostExpression("10");
        docBoostMatcher.setMatchExpression("data1 != null && data1.matches(\"test\")");

        final Map<String, Object> map = new HashMap<String, Object>();

        map.put("data1", "test");
        assertTrue(docBoostMatcher.match(map));

        map.put("data1", "aaa test bbb");
        assertFalse(docBoostMatcher.match(map));

        map.put("data1", "hoge");
        assertFalse(docBoostMatcher.match(map));

        map.remove("data1");
        assertFalse(docBoostMatcher.match(map));

        map.put("data2", "hoge");
        assertFalse(docBoostMatcher.match(map));

        docBoostMatcher.setMatchExpression("data1.matches(\".*test.*\")");
        map.put("data1", "aaa test bbb");
        assertTrue(docBoostMatcher.match(map));
    }

    @Test
    public void test_boost_params() {
        final DocBoostMatcher docBoostMatcher = new DocBoostMatcher();
        docBoostMatcher.setBoostExpression("10 * boost1 + boost2");
        docBoostMatcher.setMatchExpression("data1 > 10");

        final Map<String, Object> map = new HashMap<String, Object>();

        map.put("boost1", 0);
        map.put("boost2", 0);
        assertTrue(0.0f == docBoostMatcher.getValue(map));

        map.put("boost1", 1);
        map.put("boost2", 0);
        assertTrue(10.0f == docBoostMatcher.getValue(map));

        map.put("boost1", 1);
        map.put("boost2", 2);
        assertTrue(12.0f == docBoostMatcher.getValue(map));
    }

    @Test
    public void test_getValue_floatReturn() {
        final DocBoostMatcher docBoostMatcher = new DocBoostMatcher();
        docBoostMatcher.setBoostExpression("1.5f");

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("data1", 1);
        assertEquals(1.5f, docBoostMatcher.getValue(map));
    }

    @Test
    public void test_getValue_doubleReturn() {
        final DocBoostMatcher docBoostMatcher = new DocBoostMatcher();
        docBoostMatcher.setBoostExpression("2.5d");

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("data1", 1);
        assertEquals(2.5f, docBoostMatcher.getValue(map));
    }

    @Test
    public void test_getValue_longReturn() {
        final DocBoostMatcher docBoostMatcher = new DocBoostMatcher();
        docBoostMatcher.setBoostExpression("100L");

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("data1", 1);
        assertEquals(100.0f, docBoostMatcher.getValue(map));
    }

    @Test
    public void test_getValue_numericStringReturn() {
        final DocBoostMatcher docBoostMatcher = new DocBoostMatcher();
        docBoostMatcher.setBoostExpression("'3.14'");

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("data1", 1);
        assertTrue(Math.abs(3.14f - docBoostMatcher.getValue(map)) < 0.001f);
    }

    @Test
    public void test_getValue_emptyStringReturn() {
        final DocBoostMatcher docBoostMatcher = new DocBoostMatcher();
        docBoostMatcher.setBoostExpression("''");

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("data1", 1);
        assertEquals(0.0f, docBoostMatcher.getValue(map));
    }

    @Test
    public void test_getValue_nullReturn() {
        final DocBoostMatcher docBoostMatcher = new DocBoostMatcher();
        docBoostMatcher.setBoostExpression("null");

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("data1", 1);
        assertEquals(0.0f, docBoostMatcher.getValue(map));
    }

    @Test
    public void test_match_nullMap() {
        final DocBoostMatcher docBoostMatcher = new DocBoostMatcher();
        docBoostMatcher.setMatchExpression("true");
        assertFalse(docBoostMatcher.match(null));
    }

    @Test
    public void test_match_emptyMap() {
        final DocBoostMatcher docBoostMatcher = new DocBoostMatcher();
        docBoostMatcher.setMatchExpression("true");
        assertFalse(docBoostMatcher.match(new HashMap<>()));
    }

    @Test
    public void test_match_nullExpression() {
        final DocBoostMatcher docBoostMatcher = new DocBoostMatcher();
        // matchExpression is null by default
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("data1", 1);
        assertFalse(docBoostMatcher.match(map));
    }

    @Test
    public void test_match_nonBooleanReturn() {
        final DocBoostMatcher docBoostMatcher = new DocBoostMatcher();
        docBoostMatcher.setMatchExpression("'string_value'");

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("data1", 1);
        assertFalse(docBoostMatcher.match(map));
    }

    @Test
    public void test_getValue_nonNumericString() {
        final DocBoostMatcher docBoostMatcher = new DocBoostMatcher();
        docBoostMatcher.setBoostExpression("'not_a_number'");
        docBoostMatcher.setMatchExpression("true");

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("data1", 1);
        assertEquals(0.0f, docBoostMatcher.getValue(map));
    }

    @Test
    public void test_getValue_nullMap() {
        final DocBoostMatcher docBoostMatcher = new DocBoostMatcher();
        docBoostMatcher.setBoostExpression("10");
        assertEquals(0.0f, docBoostMatcher.getValue(null));
    }

    @Test
    public void test_getValue_emptyMap() {
        final DocBoostMatcher docBoostMatcher = new DocBoostMatcher();
        docBoostMatcher.setBoostExpression("10");
        assertEquals(0.0f, docBoostMatcher.getValue(new HashMap<>()));
    }
}
