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

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.exception.ScriptEngineException;

/**
 * This class is a factory for script engines.
 */
public class ScriptEngineFactory {
    /**
     * Constructor.
     */
    public ScriptEngineFactory() {
        super();
    }

    private static final Logger logger = LogManager.getLogger(ScriptEngineFactory.class);

    /**
     * A map of script engines.
     */
    protected Map<String, ScriptEngine> scriptEngineMap = new LinkedHashMap<>();

    /**
     * Adds a script engine.
     * @param name The name of the script engine.
     * @param scriptEngine The script engine.
     */
    public void add(final String name, final ScriptEngine scriptEngine) {
        if (name == null || scriptEngine == null) {
            throw new IllegalArgumentException("name or scriptEngine is null.");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Loaded {}", name);
        }
        scriptEngineMap.put(name.toLowerCase(Locale.ROOT), scriptEngine);
        scriptEngineMap.put(scriptEngine.getClass().getSimpleName().toLowerCase(Locale.ROOT), scriptEngine);
    }

    /**
     * Gets a script engine.
     * @param name The name of the script engine.
     * @return The script engine.
     */
    public ScriptEngine getScriptEngine(final String name) {
        if (name == null) {
            throw new ScriptEngineException("script name is null.");
        }
        final ScriptEngine scriptEngine = scriptEngineMap.get(name.toLowerCase(Locale.ROOT));
        if (scriptEngine != null) {
            return scriptEngine;
        }
        throw new ScriptEngineException(name + " is not found.");
    }
}
