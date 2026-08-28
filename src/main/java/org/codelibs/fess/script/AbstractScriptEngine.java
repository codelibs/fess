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

import org.codelibs.fess.util.ComponentUtil;

/**
 * The abstract class for ScriptEngine.
 */
public abstract class AbstractScriptEngine implements ScriptEngine {

    /**
     * Default constructor.
     */
    public AbstractScriptEngine() {
        // nothing
    }

    /**
     * Register this script engine under its name and all of its aliases.
     */
    public void register() {
        final ScriptEngineFactory factory = ComponentUtil.getScriptEngineFactory();
        factory.add(getName(), this);
        for (final String alias : getAliases()) {
            factory.add(alias, this);
        }
    }

    /**
     * Get the name of this script engine.
     * @return The name of this script engine.
     */
    protected abstract String getName();

    /**
     * Get the alternative names this script engine is also registered under.
     * @return The aliases. Empty by default.
     */
    protected String[] getAliases() {
        return new String[0];
    }
}
