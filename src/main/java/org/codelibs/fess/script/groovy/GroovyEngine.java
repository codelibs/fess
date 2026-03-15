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

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.exception.JobProcessingException;
import org.codelibs.fess.opensearch.config.exentity.ScheduledJob;
import org.codelibs.fess.script.AbstractScriptEngine;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.lastaflute.job.LaJobRuntime;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Groovy script engine implementation that extends AbstractScriptEngine.
 * This class provides support for executing Groovy scripts with parameter binding
 * and DI container integration.
 *
 * <p>Thread Safety: This class is thread-safe. Each cached entry holds its own
 * GroovyClassLoader. The cache uses Guava Cache with segment-based locking for
 * lock-free concurrent reads. Each evaluate() call creates a new Script instance
 * to ensure thread isolation of bindings.</p>
 *
 * <p>Note on class-level isolation: Compiled Script classes are cached and reused.
 * Class-level state (static fields, metaclass mutations) persists across evaluations
 * of the same script. In Fess, scripts are short expressions configured by
 * administrators (e.g., "data1 &gt; 10", "10 * boost1 + boost2") and do not use
 * static state, so this is acceptable.</p>
 *
 * <p>Resource Management: Each cached entry's GroovyClassLoader is closed on
 * eviction via RemovalListener. All remaining entries are cleaned up via close() (@PreDestroy).</p>
 */
public class GroovyEngine extends AbstractScriptEngine {
    private static final Logger logger = LogManager.getLogger(GroovyEngine.class);

    /** Maximum number of compiled scripts to cache. Configurable via DI. */
    protected int scriptCacheSize = 1000;

    /** Maximum length of script text included in warning log messages. Configurable via DI. */
    protected int maxScriptLogLength = 200;

    private Cache<String, CachedScript> scriptCache;

    /**
     * Default constructor for GroovyEngine.
     */
    public GroovyEngine() {
        super();
        buildScriptCache();
    }

    /**
     * Rebuilds the script cache after DI injection.
     * Called by the DI container after property injection.
     */
    @PostConstruct
    public void init() {
        buildScriptCache();
    }

    private void buildScriptCache() {
        final Cache<String, CachedScript> oldCache = scriptCache;
        scriptCache = CacheBuilder.newBuilder()
                .maximumSize(scriptCacheSize)
                .removalListener((final RemovalNotification<String, CachedScript> notification) -> {
                    notification.getValue().close();
                })
                .build();
        if (oldCache != null) {
            oldCache.invalidateAll();
        }
    }

    /**
     * Sets the maximum number of compiled scripts to cache.
     *
     * @param scriptCacheSize the cache size
     */
    public void setScriptCacheSize(final int scriptCacheSize) {
        this.scriptCacheSize = scriptCacheSize;
    }

    /**
     * Sets the maximum length of script text included in warning log messages.
     *
     * @param maxScriptLogLength the max length
     */
    public void setMaxScriptLogLength(final int maxScriptLogLength) {
        this.maxScriptLogLength = maxScriptLogLength;
    }

    /**
     * Evaluates a Groovy script template with the provided parameters.
     *
     * <p>This method caches compiled Script classes per script text.
     * Each evaluation creates a new Script instance to ensure thread-safe binding isolation.
     * The DI container is automatically injected into the binding map as "container".</p>
     *
     * @param template the Groovy script to evaluate (null-safe, returns null if empty)
     * @param paramMap the parameters to bind to the script (null-safe, treated as empty map if null)
     * @return the result of script evaluation, or null if the template is empty or evaluation fails
     * @throws JobProcessingException if the script explicitly throws this exception
     *         (allows scripts to signal job-specific errors that should propagate)
     */
    @Override
    public Object evaluate(final String template, final Map<String, Object> paramMap) {
        if (StringUtil.isBlank(template)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Template is blank, returning null");
            }
            return null;
        }

        final Map<String, Object> safeParamMap = paramMap != null ? paramMap : Collections.emptyMap();

        final Map<String, Object> bindingMap = new HashMap<>(safeParamMap);
        bindingMap.put("container", SingletonLaContainerFactory.getContainer());

        try {
            final CachedScript cached = getOrCompile(template);
            final Script script = cached.scriptClass.getDeclaredConstructor().newInstance();
            script.setBinding(new Binding(bindingMap));

            if (logger.isDebugEnabled()) {
                logger.debug("Evaluating Groovy script: template={}", template);
            }

            final Object result = script.run();
            logScriptExecution(template, "success");
            return result;
        } catch (final JobProcessingException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Script raised JobProcessingException", e);
            }
            logScriptExecution(template, "failure:" + e.getClass().getSimpleName());
            throw e;
        } catch (final Exception e) {
            final String truncatedScript =
                    template.length() > maxScriptLogLength ? template.substring(0, maxScriptLogLength) + "..." : template;
            logger.warn("Failed to evaluate Groovy script: script(length={})={}, parameterKeys={}", template.length(), truncatedScript,
                    safeParamMap.keySet(), e);
            logScriptExecution(template, "failure:" + e.getClass().getSimpleName());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private CachedScript getOrCompile(final String template) {
        try {
            return scriptCache.get(template, () -> {
                ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
                if (parentClassLoader == null) {
                    parentClassLoader = GroovyEngine.class.getClassLoader();
                }
                final GroovyClassLoader classLoader = new GroovyClassLoader(parentClassLoader);
                try {
                    final Class<? extends Script> scriptClass = (Class<? extends Script>) classLoader.parseClass(template);
                    return new CachedScript(scriptClass, classLoader);
                } catch (final Exception e) {
                    try {
                        classLoader.clearCache();
                        classLoader.close();
                    } catch (final IOException closeEx) {
                        logger.warn("Failed to close GroovyClassLoader after compilation failure", closeEx);
                    }
                    throw e;
                }
            });
        } catch (final ExecutionException e) {
            throw (RuntimeException) e.getCause();
        }
    }

    /**
     * Closes all cached GroovyClassLoaders and clears the script cache.
     * Called by the DI container on shutdown.
     */
    @PreDestroy
    public void close() {
        scriptCache.invalidateAll();
        scriptCache.cleanUp();
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

    /**
     * Gets the current scheduled job from the thread-local job runtime.
     *
     * @return the scheduled job if available, null otherwise
     */
    protected ScheduledJob getCurrentScheduledJob() {
        try {
            final LaJobRuntime runtime = ComponentUtil.getJobHelper().getJobRuntime();
            if (runtime != null) {
                final Object job = runtime.getParameterMap().get(Constants.SCHEDULED_JOB);
                if (job instanceof ScheduledJob) {
                    return (ScheduledJob) job;
                }
            }
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to get scheduled job from thread local", e);
            }
        }
        return null;
    }

    /**
     * Logs script execution to the audit log.
     *
     * @param script the script content that was executed
     * @param result the execution result (e.g., "success" or "failure:ExceptionType")
     */
    protected void logScriptExecution(final String script, final String result) {
        try {
            final ScheduledJob job = getCurrentScheduledJob();
            String source = "unknown";
            String user = "system";

            if (job != null) {
                source = "scheduler:" + job.getName();
                if (job.getCreatedBy() != null) {
                    user = job.getCreatedBy();
                }
            } else {
                try {
                    user = ComponentUtil.getSystemHelper().getUsername();
                } catch (final Exception e) {
                    // Ignore - background job context
                }
            }

            ComponentUtil.getActivityHelper().scriptExecution(getName(), script, source, user, result);
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to log script execution", e);
            }
        }
    }

    /**
     * Holds a compiled Script class and its associated GroovyClassLoader.
     * When evicted from the cache, close() releases the class loader resources.
     */
    private static class CachedScript {
        final Class<? extends Script> scriptClass;
        private final GroovyClassLoader classLoader;

        CachedScript(final Class<? extends Script> scriptClass, final GroovyClassLoader classLoader) {
            this.scriptClass = scriptClass;
            this.classLoader = classLoader;
        }

        void close() {
            try {
                classLoader.clearCache();
                classLoader.close();
            } catch (final IOException e) {
                LogManager.getLogger(GroovyEngine.class).warn("Failed to close GroovyClassLoader", e);
            }
        }
    }

}
