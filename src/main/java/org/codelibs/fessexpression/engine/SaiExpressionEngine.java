/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
package org.codelibs.fessexpression.engine;

import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.expression.engine.JavaScriptExpressionEngine;

public class SaiExpressionEngine extends JavaScriptExpressionEngine {
    @Override
    protected Object actuallyEvaluate(String exp, Map<String, ? extends Object> contextMap, LaContainer container, String firstName,
            Object firstComponent) {
        final ScriptEngine engine = prepareScriptEngineManager().getEngineByName("sai");
        if (firstName != null) {
            engine.put(firstName, firstComponent);
        }
        try {
            return engine.eval(exp);
        } catch (ScriptException | RuntimeException e) {
            throwJavaScriptExpressionException(exp, contextMap, container, e);
            return null; // unreachable
        }
    }
}
