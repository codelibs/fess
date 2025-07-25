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

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.job.JobExecutor;
import org.codelibs.fess.util.ComponentUtil;

/**
 * This class executes a script.
 */
public class ScriptExecutor extends JobExecutor {

    /**
     * Constructor.
     */
    public ScriptExecutor() {
        super();
    }

    @Override
    public Object execute(final String scriptType, final String script) {
        final Map<String, Object> params = new HashMap<>();
        params.put("executor", this);

        return ComponentUtil.getScriptEngineFactory().getScriptEngine(scriptType).evaluate(script, params);
    }

}
