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

import javax.script.ScriptEngine;

import org.codelibs.sai.api.scripting.SaiScriptEngineFactory;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.expression.engine.JavaScriptExpressionEngine;

/**
 * The expression engine of Di xml, which evaluates expressions as ECMAScript 6.
 *
 * <p>
 * Sai parses a script as ECMAScript 5.1 unless <code>--language=es6</code> is given, and
 * {@link JavaScriptExpressionEngine} looks the engine up by name via
 * <code>ScriptEngineManager</code>, which has no way to pass such an option. So this class builds
 * the Sai engine from its factory instead, where the option can be specified.
 * </p>
 *
 * @author shinsuke
 */
public class FessJavaScriptExpressionEngine extends JavaScriptExpressionEngine {

    /** The arguments for the Sai engine. "-doe" is the default of SaiScriptEngineFactory#getScriptEngine(). */
    protected static final String[] ENGINE_ARGS = { "-doe", "--language=es6" };

    /** The factory of the Sai engine. It is thread-safe and holds no state. */
    protected static final SaiScriptEngineFactory ENGINE_FACTORY = new SaiScriptEngineFactory();

    @Override
    protected ScriptEngine comeOnScriptEngine(final String exp, final LaContainer container) {
        // a script engine is not thread safe, so it is prepared per execution as the super class does
        return ENGINE_FACTORY.getScriptEngine(ENGINE_ARGS);
    }
}
