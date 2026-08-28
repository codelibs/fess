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
package org.codelibs.fess.mylasta.di;

import java.util.HashMap;

import javax.script.ScriptEngine;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.lastaflute.di.core.LastaDiProperties;
import org.lastaflute.di.core.expression.ScriptingExpression;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;

public class FessJavaScriptExpressionEngineTest extends UnitFessTestCase {

    @Test
    public void test_registeredInLastaDiProperties() {
        assertEquals(FessJavaScriptExpressionEngine.class, LastaDiProperties.getInstance().getDiXmlScriptExpressionEngineType());
    }

    @Test
    public void test_comeOnScriptEngine_es6() throws Exception {
        final ScriptEngine engine = new FessJavaScriptExpressionEngine().comeOnScriptEngine("dummy", null);
        // destructuring, for-of, an arrow function and a template literal are all ES6 only
        assertEquals("6", engine.eval("const [a, b, c] = [1, 2, 3]; let s = 0; for (const v of [a, b, c]) { s += v; } `${s}`"));
        assertEquals("42", engine.eval("const f = x => x * 2; `${f(21)}`"));
    }

    @Test
    public void test_comeOnScriptEngine_es5() throws Exception {
        final ScriptEngine engine = new FessJavaScriptExpressionEngine().comeOnScriptEngine("dummy", null);
        assertEquals("ok", engine.eval("var v = 'ok'; v"));
    }

    @Test
    public void test_evaluatedAsDiXmlExpression() {
        // the path a Di xml expression actually takes
        final ScriptingExpression expression = new ScriptingExpression("`sum=${((x, y) => x + y)(1, 2)}`");
        final Object evaluated = expression.evaluate(new HashMap<>(), SingletonLaContainerFactory.getContainer(), Object.class);
        assertEquals("sum=3", evaluated);
    }
}
