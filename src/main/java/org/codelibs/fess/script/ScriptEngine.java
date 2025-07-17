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

import java.util.Map;

/**
 * Interface for script engines that can evaluate templates with parameters.
 * This interface provides a contract for different script engine implementations
 * to process template strings with parameter substitution.
 */
public interface ScriptEngine {

    /**
     * Evaluates a template string with the provided parameter map.
     * The template is processed using the script engine's templating mechanism,
     * with parameters from the paramMap substituted into the template.
     *
     * @param template the template string to evaluate
     * @param paramMap the map of parameters to substitute into the template
     * @return the result of evaluating the template, or null if evaluation fails
     */
    Object evaluate(final String template, final Map<String, Object> paramMap);
}
