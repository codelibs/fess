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
package org.codelibs.fess.script.groovy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.exception.JobProcessingException;
import org.codelibs.fess.script.AbstractScriptEngine;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;

/**
 * Groovy script engine implementation that extends AbstractScriptEngine.
 * This class provides support for executing Groovy scripts with parameter binding
 * and DI container integration.
 *
 * <p>Thread Safety: This class is thread-safe. Each evaluate() call creates
 * a new GroovyShell instance to ensure thread isolation.</p>
 *
 * <p>Resource Management: GroovyClassLoader instances are properly managed
 * and cleaned up after script evaluation to prevent memory leaks.</p>
 */
public class GroovyEngine extends AbstractScriptEngine {
    private static final Logger logger = LogManager.getLogger(GroovyEngine.class);

    /**
     * Default constructor for GroovyEngine.
     */
    public GroovyEngine() {
        super();
    }

    /**
     * Evaluates a Groovy script template with the provided parameters.
     *
     * <p>This method creates a new GroovyShell instance for each evaluation to ensure
     * thread safety. The DI container is automatically injected into the binding map
     * as "container" for script access.</p>
     *
     * @param template the Groovy script to evaluate (null-safe, returns null if empty)
     * @param paramMap the parameters to bind to the script (null-safe, treated as empty map if null)
     * @return the result of script evaluation, or null if the template is empty or evaluation fails
     * @throws JobProcessingException if the script explicitly throws this exception
     *         (allows scripts to signal job-specific errors that should propagate)
     */
    @Override
    public Object evaluate(final String template, final Map<String, Object> paramMap) {
        // Null-safety: return null for blank templates
        // Early return is safe here as no resources have been allocated yet
        if (StringUtil.isBlank(template)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Template is blank, returning null");
            }
            return null;
        }

        // Null-safety: use empty map if paramMap is null
        final Map<String, Object> safeParamMap = paramMap != null ? paramMap : Collections.emptyMap();

        // Prepare binding map with parameters and DI container
        final Map<String, Object> bindingMap = new HashMap<>(safeParamMap);
        bindingMap.put("container", SingletonLaContainerFactory.getContainer());

        // Create GroovyShell with custom class loader for proper resource management
        GroovyClassLoader classLoader = null;
        try {
            // Get parent class loader with fallback to ensure robustness across threading contexts
            ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
            if (parentClassLoader == null) {
                parentClassLoader = GroovyEngine.class.getClassLoader();
            }
            classLoader = new GroovyClassLoader(parentClassLoader);
            final GroovyShell groovyShell = new GroovyShell(classLoader, new Binding(bindingMap));

            if (logger.isDebugEnabled()) {
                logger.debug("Evaluating Groovy script: template={}", template);
            }

            return groovyShell.evaluate(template);
        } catch (final JobProcessingException e) {
            // Rethrow JobProcessingException to allow scripts to signal job-specific errors
            // that should be handled by the job framework
            if (logger.isDebugEnabled()) {
                logger.debug("Script raised JobProcessingException", e);
            }
            throw e;
        } catch (final Exception e) {
            // Log and return null for other exceptions to maintain backward compatibility
            logger.warn("Failed to evaluate Groovy script: template={}, parameters={}", template, safeParamMap, e);
            return null;
        } finally {
            // Properly clean up GroovyClassLoader resources
            if (classLoader != null) {
                try {
                    classLoader.clearCache();
                    classLoader.close();
                } catch (final Exception e) {
                    logger.warn("Failed to close GroovyClassLoader", e);
                }
            }
        }
    }

    /**
     * Returns the name identifier for this script engine.
     *
     * @return "groovy" - the identifier used to register and retrieve this engine
     */
    @Override
    protected String getName() {
        return "groovy";
    }

}
