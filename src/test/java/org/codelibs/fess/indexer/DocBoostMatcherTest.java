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
import org.codelibs.fess.opensearch.config.exentity.BoostDocumentRule;
import org.codelibs.fess.script.ScriptEngineFactory;
import org.codelibs.fess.script.javascript.JavaScriptEngine;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class DocBoostMatcherTest extends UnitFessTestCase {

    /** Name of the stub engine used where no JavaScript literal can produce the value under test. */
    private static final String FIXED_VALUE_ENGINE = "fixed-value-stub";

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.register(new ScriptEngineFactory(), "scriptEngineFactory");
        // The real engine, not a stub: a matcher with no rule runs on Constants.DEFAULT_SCRIPT,
        // and these tests are about what that engine makes of the expressions.
        new JavaScriptEngine().register();
    }

    /**
     * A matcher whose engine returns the given object whatever the expression. JavaScript numbers
     * are all Double, so the Float and Long branches of getValue have no literal to reach them.
     *
     * @param value the value the engine returns
     * @return a matcher bound to the stub engine
     */
    private DocBoostMatcher fixedValueMatcher(final Object value) {
        ComponentUtil.getScriptEngineFactory().add(FIXED_VALUE_ENGINE, (template, paramMap) -> value);
        final BoostDocumentRule rule = new BoostDocumentRule();
        rule.setBoostExpr("value");
        rule.setScriptType(FIXED_VALUE_ENGINE);
        return new DocBoostMatcher(rule);
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
        docBoostMatcher.setMatchExpression("data1 != null && /^test$/.test(data1)");

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

        docBoostMatcher.setMatchExpression("/.*test.*/.test(data1)");
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
        final DocBoostMatcher docBoostMatcher = fixedValueMatcher(Float.valueOf(1.5f));

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("data1", 1);
        assertEquals(1.5f, docBoostMatcher.getValue(map));
    }

    @Test
    public void test_getValue_doubleReturn() {
        final DocBoostMatcher docBoostMatcher = new DocBoostMatcher();
        docBoostMatcher.setBoostExpression("2.5");

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("data1", 1);
        assertEquals(2.5f, docBoostMatcher.getValue(map));
    }

    @Test
    public void test_getValue_longReturn() {
        final DocBoostMatcher docBoostMatcher = fixedValueMatcher(Long.valueOf(100L));

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

    @Test
    public void test_scriptTypeFromRule() {
        final ScriptEngineFactory factory = new ScriptEngineFactory();
        factory.add("javascript", (template, paramMap) -> "from-javascript");
        factory.add("groovy", (template, paramMap) -> "from-groovy");
        ComponentUtil.register(factory, "scriptEngineFactory");

        final BoostDocumentRule explicit = new BoostDocumentRule();
        explicit.setUrlExpr("url != null");
        explicit.setBoostExpr("1");
        explicit.setScriptType("javascript");
        assertEquals("javascript", new DocBoostMatcher(explicit).getScriptType());

        final BoostDocumentRule legacy = new BoostDocumentRule();
        legacy.setUrlExpr("url != null");
        legacy.setBoostExpr("1");
        assertEquals(Constants.LEGACY_SCRIPT, new DocBoostMatcher(legacy).getScriptType());
    }
}
