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
package org.codelibs.fess.script.javascript;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.exception.JobProcessingException;
import org.codelibs.fess.opensearch.config.exentity.ScheduledJob;
import org.codelibs.fess.script.AbstractScriptEngine;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.sai.api.scripting.SaiScriptEngineFactory;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.lastaflute.job.LaJobRuntime;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * JavaScript script engine backed by Sai, the Nashorn fork maintained by CodeLibs.
 *
 * <p>Scripts are evaluated as ECMAScript 6. Sai parses ECMAScript 5.1 unless
 * <code>--language=es6</code> is given, and the option can only be passed through the factory,
 * which is why the engine is built from {@link SaiScriptEngineFactory} rather than looked up
 * through <code>ScriptEngineManager</code>. This matches
 * <code>FessJavaScriptExpressionEngine</code>, which does the same for Di xml expressions.</p>
 *
 * <p><b>Two-phase compilation.</b> A Fess script is written in one of two shapes. Scheduler jobs
 * and crawler config scripts are statements starting with <code>return</code>, which is a syntax
 * error at the top level of a JavaScript program. Data store field scripts and document boost
 * expressions are bare expressions such as <code>content.length()</code>, whose value must be
 * returned. So the template is first compiled as an expression, and only if that fails to parse
 * is it compiled as a statement block. The decision is made at compile time and cached, so
 * evaluation stays deterministic.</p>
 *
 * <p><b>Thread safety.</b> One engine instance is shared and a fresh {@link Bindings} is created
 * for every evaluation. Sai gives each Bindings its own global scope, so evaluations do not see
 * each other. Building an engine per evaluation costs about a millisecond and would also defeat
 * the compiled-script cache.</p>
 */
public class JavaScriptEngine extends AbstractScriptEngine {

    private static final Logger logger = LogManager.getLogger(JavaScriptEngine.class);

    /** The arguments for the Sai engine. "-doe" is the default of SaiScriptEngineFactory#getScriptEngine(). */
    protected static final String[] ENGINE_ARGS = { "-doe", "--language=es6" };

    /** The factory of the Sai engine. It is thread-safe and holds no state. */
    protected static final SaiScriptEngineFactory ENGINE_FACTORY = new SaiScriptEngineFactory();

    /** Maximum number of compiled scripts to cache. Configurable via DI. */
    protected int scriptCacheSize = 1000;

    /** Maximum length of script text included in warning log messages. Configurable via DI. */
    protected int maxScriptLogLength = 200;

    /** Whether to log script execution details for auditing purposes. Configurable via DI. */
    protected boolean scriptAuditLogEnabled;

    private final javax.script.ScriptEngine engine = ENGINE_FACTORY.getScriptEngine(ENGINE_ARGS);

    private Cache<String, CompiledScript> scriptCache;

    /**
     * Default constructor.
     */
    public JavaScriptEngine() {
        buildScriptCache();
    }

    /**
     * Rebuilds the script cache after DI injection.
     */
    @PostConstruct
    public void init() {
        buildScriptCache();
        scriptAuditLogEnabled = ComponentUtil.available() && ComponentUtil.getFessConfig().isScriptAuditLogEnabled()
                && ComponentUtil.hasComponent("activityHelper");
    }

    private void buildScriptCache() {
        final Cache<String, CompiledScript> oldCache = scriptCache;
        scriptCache = CacheBuilder.newBuilder().maximumSize(scriptCacheSize).build();
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

    @Override
    public Object evaluate(final String template, final Map<String, Object> paramMap) {
        if (StringUtil.isBlank(template)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Template is blank, returning null");
            }
            return null;
        }

        final Map<String, Object> safeParamMap = paramMap != null ? paramMap : Collections.emptyMap();

        try {
            final CompiledScript compiledScript = getOrCompile(template);
            final Bindings bindings = engine.createBindings();
            bindings.putAll(safeParamMap);
            bindings.put("container", SingletonLaContainerFactory.getContainer());

            if (logger.isDebugEnabled()) {
                logger.debug("Evaluating JavaScript: template={}", template);
            }

            final Object result = compiledScript.eval(bindings);
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
            logger.warn("Failed to evaluate JavaScript: script(length={})={}, parameterKeys={}", template.length(), truncatedScript,
                    safeParamMap.keySet(), e);
            logScriptExecution(template, "failure:" + e.getClass().getSimpleName());
            return null;
        }
    }

    /**
     * Compiles the template, first as an expression and then as a statement block.
     *
     * @param template the script text
     * @return the compiled script
     */
    protected CompiledScript getOrCompile(final String template) {
        try {
            return scriptCache.get(template, () -> {
                final Compilable compilable = (Compilable) engine;
                try {
                    return compilable.compile("(function(){ return (" + template + "\n); })()");
                } catch (final ScriptException asExpression) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Not an expression, compiling as statements: {}", asExpression.getMessage());
                    }
                    return compilable.compile("(function(){ " + template + "\n})()");
                }
            });
        } catch (final ExecutionException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new IllegalStateException("Failed to compile the script.", cause);
        }
    }

    /**
     * Clears the compiled script cache.
     */
    @PreDestroy
    public void close() {
        scriptCache.invalidateAll();
        scriptCache.cleanUp();
    }

    @Override
    protected String getName() {
        return "javascript";
    }

    @Override
    protected String[] getAliases() {
        return new String[] { "js", "sai" };
    }

    /**
     * Gets the current scheduled job from the thread-local job runtime.
     *
     * @return the scheduled job if available, null otherwise
     */
    protected ScheduledJob getCurrentScheduledJob() {
        try {
            if (!ComponentUtil.hasComponent("jobHelper")) {
                return null;
            }
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
     * @param result the execution result
     */
    protected void logScriptExecution(final String script, final String result) {
        if (!scriptAuditLogEnabled) {
            return;
        }
        try {
            String source = "unknown";
            String user = "system";

            final ScheduledJob job = getCurrentScheduledJob();
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
}
