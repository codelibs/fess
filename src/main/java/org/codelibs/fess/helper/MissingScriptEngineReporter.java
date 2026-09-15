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
package org.codelibs.fess.helper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.ScheduledJobService;
import org.codelibs.fess.indexer.DocBoostMatcher;
import org.codelibs.fess.opensearch.config.exbhv.BoostDocumentRuleBhv;
import org.codelibs.fess.opensearch.config.exbhv.PathMappingBhv;
import org.codelibs.fess.opensearch.config.exentity.BoostDocumentRule;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.opensearch.config.exentity.DataConfig;
import org.codelibs.fess.opensearch.config.exentity.FileConfig;
import org.codelibs.fess.opensearch.config.exentity.PathMapping;
import org.codelibs.fess.opensearch.config.exentity.ScheduledJob;
import org.codelibs.fess.opensearch.config.exentity.WebConfig;
import org.codelibs.fess.script.ScriptEngineFactory;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Warns about stored settings that name a script engine no plugin has registered.
 *
 * <p>An upgrade does not rewrite what is stored in the index, and a script type that was never
 * set still means groovy. Since Groovy moved to the fess-script-groovy plugin, a 15.8 installation
 * upgraded without it keeps groovy on every scheduled job - the bundled ones too, because the
 * startup bulk load only creates documents that do not exist yet - and on every crawl config,
 * data config and document boost rule saved before 15.9. Each of those fails only when it runs,
 * and mostly where nobody looks first: a job with job logging off leaves a warning in fess.log and
 * nothing in the job log, and a crawl config whose field scripts cannot be evaluated indexes no
 * documents while its job reports ok. One warning per engine at startup names them together.</p>
 */
public class MissingScriptEngineReporter {

    private static final Logger logger = LogManager.getLogger(MissingScriptEngineReporter.class);

    /** How many names of one kind of setting a warning lists. */
    protected static final int MAX_LISTED_NAMES = 3;

    /** The handler parameter a data config names its script type with; see AbstractDataStore#getScriptType. */
    protected static final String DATA_CONFIG_SCRIPT_TYPE = "script_type";

    /**
     * Default constructor.
     */
    public MissingScriptEngineReporter() {
        // nothing
    }

    /**
     * Logs one warning for each script engine that stored settings use and no plugin registers.
     *
     * <p>This runs while the application starts, so it never throws: a setting that cannot be
     * read is skipped and the failure is logged at debug.</p>
     */
    public void report() {
        try {
            final ScriptEngineFactory scriptEngineFactory = ComponentUtil.getScriptEngineFactory();
            collect(scriptEngineFactory).forEach((engine, settings) -> {
                logger.warn("Settings use the script engine {}, which is not registered, so their scripts cannot run: {}."
                        + " A setting without a script type uses groovy. A plugin may be missing, such as fess-script-groovy for groovy."
                        + " To use JavaScript instead, rewrite each script and set javascript as its script type: the Execution Method"
                        + " of a scheduled job, the Script Type of a document boost rule, config.script.type=javascript in the"
                        + " Config Parameter of a web or file config, script_type=javascript in the Parameter of a data config,"
                        + " the javascript: prefix of a path mapping Replacement, and job.default.script in fess_config.properties.",
                        engine, summarize(settings));
            });
        } catch (final Exception e) {
            logger.debug("Failed to check the script engines used by stored settings.", e);
        }
    }

    /**
     * Finds the stored settings whose script type has no registered engine.
     *
     * @param scriptEngineFactory the factory the engines are registered with
     * @return the names of those settings, by lower-cased engine name and then by kind of setting
     */
    protected Map<String, Map<String, List<String>>> collect(final ScriptEngineFactory scriptEngineFactory) {
        final Map<String, Map<String, List<String>>> usage = new TreeMap<>();
        collect(usage, scriptEngineFactory, "scheduled jobs", this::loadScheduledJobs, ScheduledJob::getScriptType, ScheduledJob::getName);
        collect(usage, scriptEngineFactory, "web configs", this::loadWebConfigs, this::getFieldScriptType, WebConfig::getName);
        collect(usage, scriptEngineFactory, "file configs", this::loadFileConfigs, this::getFieldScriptType, FileConfig::getName);
        collect(usage, scriptEngineFactory, "data configs", this::loadDataConfigs, this::getHandlerScriptType, DataConfig::getName);
        collect(usage, scriptEngineFactory, "document boost rules", this::loadBoostDocumentRules,
                rule -> new DocBoostMatcher(rule).getScriptType(), BoostDocumentRule::getId);
        collect(usage, scriptEngineFactory, "path mappings", this::loadPathMappings, this::getReplacementScriptType, PathMapping::getId);
        collect(usage, scriptEngineFactory, "properties", () -> List.of("job.default.script"), key -> loadJobDefaultScript(),
                Function.identity());
        return usage;
    }

    /**
     * Adds the settings of one kind whose script type has no registered engine.
     *
     * @param <T> the type of setting
     * @param usage the result to add to
     * @param scriptEngineFactory the factory the engines are registered with
     * @param kind the kind of setting, as the warning names it
     * @param loader loads the settings
     * @param scriptTypeOf the script type a setting runs its scripts with, or null when it has none
     * @param nameOf the name a setting is listed by
     */
    protected <T> void collect(final Map<String, Map<String, List<String>>> usage, final ScriptEngineFactory scriptEngineFactory,
            final String kind, final Supplier<List<T>> loader, final Function<T, String> scriptTypeOf, final Function<T, String> nameOf) {
        try {
            for (final T setting : loader.get()) {
                final String scriptType = scriptTypeOf.apply(setting);
                if (StringUtil.isBlank(scriptType) || scriptEngineFactory.hasScriptEngine(scriptType)) {
                    continue;
                }
                usage.computeIfAbsent(scriptType.toLowerCase(Locale.ROOT), k -> new LinkedHashMap<>())
                        .computeIfAbsent(kind, k -> new ArrayList<>())
                        .add(nameOf.apply(setting));
            }
        } catch (final Exception e) {
            logger.debug("Failed to check the script engines used by {}.", kind, e);
        }
    }

    /**
     * Formats the settings of one engine as {@code kind=count [name, name, name, ...]}.
     *
     * @param settings the names of the settings by kind
     * @return the summary
     */
    protected String summarize(final Map<String, List<String>> settings) {
        return settings.entrySet().stream().map(e -> {
            final List<String> names = e.getValue();
            final String listed = names.stream().limit(MAX_LISTED_NAMES).collect(Collectors.joining(", "));
            return e.getKey() + "=" + names.size() + " [" + listed + (names.size() > MAX_LISTED_NAMES ? ", ..." : "") + "]";
        }).collect(Collectors.joining(", "));
    }

    /**
     * Gets the script type a web or file config evaluates its field scripts with.
     *
     * @param config the crawl config
     * @return the script type, or null when the config has no field script
     */
    protected String getFieldScriptType(final CrawlingConfig config) {
        if (config.getConfigParameterMap(ConfigName.SCRIPT).isEmpty()) {
            return null;
        }
        return config.getScriptType();
    }

    /**
     * Gets the script type a data config evaluates its handler script with.
     *
     * @param config the data config
     * @return the script type, or null when the config has no handler script
     */
    protected String getHandlerScriptType(final DataConfig config) {
        if (config.getHandlerScriptMap().isEmpty()) {
            return null;
        }
        final String scriptType = config.getHandlerParameterMap().get(DATA_CONFIG_SCRIPT_TYPE);
        return StringUtil.isBlank(scriptType) ? Constants.LEGACY_SCRIPT : scriptType;
    }

    /**
     * Gets the script type a path mapping replacement names with its prefix.
     *
     * @param pathMapping the path mapping
     * @return the script type, or null when the replacement is not a script
     */
    protected String getReplacementScriptType(final PathMapping pathMapping) {
        final String replacement = pathMapping.getReplacement();
        if (replacement == null) {
            return null;
        }
        final int separatorIndex = replacement.indexOf(':');
        if (separatorIndex <= 0) {
            return null;
        }
        final String prefix = replacement.substring(0, separatorIndex);
        // Any other prefix, such as https:, is an ordinary replacement; see PathMappingHelper.
        return PathMappingHelper.SCRIPT_ENGINE_NAMES.contains(prefix.toLowerCase(Locale.ROOT)) ? prefix : null;
    }

    /**
     * Loads the scheduled jobs.
     *
     * @return the scheduled jobs
     */
    protected List<ScheduledJob> loadScheduledJobs() {
        return ComponentUtil.getComponent(ScheduledJobService.class).getScheduledJobList();
    }

    /**
     * Loads the web configs, including disabled ones.
     *
     * @return the web configs
     */
    protected List<WebConfig> loadWebConfigs() {
        return ComponentUtil.getCrawlingConfigHelper().getAllWebConfigList(false, false, false, null);
    }

    /**
     * Loads the file configs, including disabled ones.
     *
     * @return the file configs
     */
    protected List<FileConfig> loadFileConfigs() {
        return ComponentUtil.getCrawlingConfigHelper().getAllFileConfigList(false, false, false, null);
    }

    /**
     * Loads the data configs, including disabled ones.
     *
     * @return the data configs
     */
    protected List<DataConfig> loadDataConfigs() {
        return ComponentUtil.getCrawlingConfigHelper().getAllDataConfigList(false, false, false, null);
    }

    /**
     * Loads the document boost rules.
     *
     * @return the document boost rules
     */
    protected List<BoostDocumentRule> loadBoostDocumentRules() {
        return ComponentUtil.getComponent(BoostDocumentRuleBhv.class).selectList(cb -> {
            cb.query().matchAll();
            cb.fetchFirst(ComponentUtil.getFessConfig().getPageDocboostMaxFetchSizeAsInteger());
        });
    }

    /**
     * Loads the path mappings of every process type.
     *
     * @return the path mappings
     */
    protected List<PathMapping> loadPathMappings() {
        return ComponentUtil.getComponent(PathMappingBhv.class).selectList(cb -> {
            cb.query().matchAll();
            cb.fetchFirst(ComponentUtil.getFessConfig().getPagePathMappingMaxFetchSizeAsInteger());
        });
    }

    /**
     * Loads the script type the scheduler's create form offers a new job.
     *
     * @return the value of job.default.script
     */
    protected String loadJobDefaultScript() {
        return ComponentUtil.getFessConfig().getJobDefaultScript();
    }
}
