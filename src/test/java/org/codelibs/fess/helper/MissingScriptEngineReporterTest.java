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
import java.util.Map;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.mylasta.direction.FessProp;
import org.codelibs.fess.opensearch.config.exentity.BoostDocumentRule;
import org.codelibs.fess.opensearch.config.exentity.DataConfig;
import org.codelibs.fess.opensearch.config.exentity.FileConfig;
import org.codelibs.fess.opensearch.config.exentity.PathMapping;
import org.codelibs.fess.opensearch.config.exentity.ScheduledJob;
import org.codelibs.fess.opensearch.config.exentity.WebConfig;
import org.codelibs.fess.script.ScriptEngineFactory;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class MissingScriptEngineReporterTest extends UnitFessTestCase {

    private ScriptEngineFactory scriptEngineFactory;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        FessProp.propMap.clear();
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getAppEncryptPropertyPattern() {
                return ".*password|.*key";
            }
        });
        scriptEngineFactory = new ScriptEngineFactory();
        scriptEngineFactory.add("javascript", (template, paramMap) -> null);
        ComponentUtil.register(scriptEngineFactory, "scriptEngineFactory");
    }

    @Test
    public void test_collect_findsSettingsWhoseEngineIsNotRegistered() {
        final StubReporter reporter = new StubReporter();
        reporter.jobs.add(job("Default Crawler", "groovy"));
        reporter.jobs.add(job("Unset Job", null));
        reporter.jobs.add(job("JavaScript Job", "javascript"));
        reporter.webConfigs.add(webConfig("legacy-web", "field.xpath.title=//TITLE\nfield.script.title=def t = value; return t"));
        reporter.webConfigs.add(webConfig("no-script-web", "field.xpath.title=//TITLE"));
        reporter.webConfigs.add(webConfig("js-web", "config.script.type=javascript\nfield.script.title=value"));
        reporter.fileConfigs.add(fileConfig("legacy-file", "config.script.type=groovy\nfield.script.title=value"));
        reporter.dataConfigs.add(dataConfig("legacy-data", null, "url=\"http://example.com/\" + id"));
        reporter.dataConfigs.add(dataConfig("js-data", "script_type=javascript", "url=\"http://example.com/\" + id"));
        reporter.dataConfigs.add(dataConfig("no-script-data", null, null));
        reporter.boostRules.add(boostRule("rule1", null));
        reporter.boostRules.add(boostRule("rule2", "javascript"));
        reporter.pathMappings.add(pathMapping("map1", "groovy:\"http://mapped.invalid/${matcher.group(1)}\""));
        reporter.pathMappings.add(pathMapping("map2", "https://example.net/$1"));
        reporter.pathMappings.add(pathMapping("map3", "javascript:url"));
        reporter.pathMappings.add(pathMapping("map4", "function:encodeUrl"));
        reporter.jobDefaultScript = "groovy";

        final Map<String, List<String>> expected = new LinkedHashMap<>();
        expected.put("scheduled jobs", List.of("Default Crawler", "Unset Job"));
        expected.put("web configs", List.of("legacy-web"));
        expected.put("file configs", List.of("legacy-file"));
        expected.put("data configs", List.of("legacy-data"));
        expected.put("document boost rules", List.of("rule1"));
        expected.put("path mappings", List.of("map1"));
        expected.put("properties", List.of("job.default.script"));
        assertEquals(Map.of("groovy", expected), reporter.collect(scriptEngineFactory));
    }

    @Test
    public void test_collect_isEmptyWhenEveryEngineIsRegistered() {
        scriptEngineFactory.add("groovy", (template, paramMap) -> null);
        final StubReporter reporter = new StubReporter();
        reporter.jobs.add(job("Default Crawler", "groovy"));
        reporter.webConfigs.add(webConfig("legacy-web", "field.script.title=value"));
        reporter.boostRules.add(boostRule("rule1", ""));
        reporter.pathMappings.add(pathMapping("map1", "groovy:url"));
        reporter.jobDefaultScript = "groovy";

        assertTrue(reporter.collect(scriptEngineFactory).isEmpty());
    }

    @Test
    public void test_collect_groupsByEngineName() {
        final StubReporter reporter = new StubReporter();
        reporter.jobs.add(job("A", "ognl"));
        reporter.jobs.add(job("B", "Groovy"));
        reporter.jobs.add(job("C", "groovy"));

        final Map<String, Map<String, List<String>>> usage = reporter.collect(scriptEngineFactory);
        assertEquals(List.of("groovy", "ognl"), new ArrayList<>(usage.keySet()));
        assertEquals(List.of("B", "C"), usage.get("groovy").get("scheduled jobs"));
        assertEquals(List.of("A"), usage.get("ognl").get("scheduled jobs"));
    }

    @Test
    public void test_summarize() {
        final Map<String, List<String>> settings = new LinkedHashMap<>();
        settings.put("scheduled jobs", List.of("Default Crawler", "Suggest Indexer", "Log Aggregator", "Doc Purger", "Log Purger"));
        settings.put("web configs", List.of("legacy-web"));
        settings.put("properties", List.of("job.default.script"));

        assertEquals("scheduled jobs=5 [Default Crawler, Suggest Indexer, Log Aggregator, ...], web configs=1 [legacy-web],"
                + " properties=1 [job.default.script]", new MissingScriptEngineReporter().summarize(settings));
    }

    @Test
    public void test_report_warnsOncePerEngine() {
        final StubReporter reporter = new StubReporter();
        reporter.jobs.add(job("Default Crawler", "groovy"));
        reporter.jobs.add(job("Suggest Indexer", "groovy"));
        reporter.jobs.add(job("OGNL Job", "ognl"));
        reporter.boostRules.add(boostRule("rule1", null));

        final LogCapturingAppender appender = LogCapturingAppender.attach(MissingScriptEngineReporter.class);
        try {
            reporter.report();
            final List<String> warnings = appender.warnings();
            assertEquals(2, warnings.size());
            assertTrue(warnings.get(0).startsWith("Settings use the script engine groovy, which is not registered,"), warnings.get(0));
            assertTrue(warnings.get(0).contains("scheduled jobs=2 [Default Crawler, Suggest Indexer], document boost rules=1 [rule1]."),
                    warnings.get(0));
            assertTrue(warnings.get(0).contains("A plugin may be missing, such as fess-script-groovy for groovy."), warnings.get(0));
            assertTrue(warnings.get(0).contains("set javascript as its script type"), warnings.get(0));
            assertTrue(warnings.get(1).startsWith("Settings use the script engine ognl, which is not registered,"), warnings.get(1));
            assertTrue(warnings.get(1).contains("scheduled jobs=1 [OGNL Job]."), warnings.get(1));
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_report_isSilentWhenNothingIsMissing() {
        final StubReporter reporter = new StubReporter();
        reporter.jobs.add(job("Default Crawler", "javascript"));
        reporter.webConfigs.add(webConfig("legacy-web", "field.xpath.title=//TITLE"));

        final LogCapturingAppender appender = LogCapturingAppender.attach(MissingScriptEngineReporter.class);
        try {
            reporter.report();
            assertTrue(appender.warnings().isEmpty(), appender.renderedEvents().toString());
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_report_skipsSettingsThatCannotBeLoaded() {
        final StubReporter reporter = new StubReporter() {
            @Override
            protected List<WebConfig> loadWebConfigs() {
                throw new IllegalStateException("fess_config.web_config is unavailable");
            }
        };
        reporter.jobs.add(job("Default Crawler", "groovy"));

        final LogCapturingAppender appender = LogCapturingAppender.attach(MissingScriptEngineReporter.class);
        try {
            reporter.report();
            assertEquals(1, appender.warnings().size());
            assertTrue(appender.warnings().get(0).contains("scheduled jobs=1 [Default Crawler]."), appender.warnings().get(0));
            assertFalse(appender.warnings().get(0).contains("web configs"), appender.warnings().get(0));
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_report_neverThrows() {
        ComponentUtil.register(new ScriptEngineFactory() {
            @Override
            public boolean hasScriptEngine(final String name) {
                throw new IllegalStateException("no script engine registry");
            }
        }, "scriptEngineFactory");
        final StubReporter reporter = new StubReporter();
        reporter.jobs.add(job("Default Crawler", "groovy"));

        final LogCapturingAppender appender = LogCapturingAppender.attach(MissingScriptEngineReporter.class);
        try {
            reporter.report();
            assertTrue(appender.warnings().isEmpty(), appender.renderedEvents().toString());
        } finally {
            appender.detach();
        }
    }

    private static ScheduledJob job(final String name, final String scriptType) {
        final ScheduledJob job = new ScheduledJob();
        job.setName(name);
        job.setScriptType(scriptType);
        return job;
    }

    private static WebConfig webConfig(final String name, final String configParameter) {
        final WebConfig config = new WebConfig();
        config.setName(name);
        config.setConfigParameter(configParameter);
        return config;
    }

    private static FileConfig fileConfig(final String name, final String configParameter) {
        final FileConfig config = new FileConfig();
        config.setName(name);
        config.setConfigParameter(configParameter);
        return config;
    }

    private static DataConfig dataConfig(final String name, final String handlerParameter, final String handlerScript) {
        final DataConfig config = new DataConfig();
        config.setName(name);
        config.setHandlerParameter(handlerParameter);
        config.setHandlerScript(handlerScript);
        return config;
    }

    private static BoostDocumentRule boostRule(final String id, final String scriptType) {
        final BoostDocumentRule rule = new BoostDocumentRule();
        rule.setId(id);
        rule.setUrlExpr("url.matches(\".*/docs/ja/.*\")");
        rule.setBoostExpr("7.0");
        rule.setScriptType(scriptType);
        return rule;
    }

    private static PathMapping pathMapping(final String id, final String replacement) {
        final PathMapping pathMapping = new PathMapping();
        pathMapping.setId(id);
        pathMapping.setRegex("http://localhost/docs/(.*)");
        pathMapping.setReplacement(replacement);
        return pathMapping;
    }

    private static class StubReporter extends MissingScriptEngineReporter {
        final List<ScheduledJob> jobs = new ArrayList<>();
        final List<WebConfig> webConfigs = new ArrayList<>();
        final List<FileConfig> fileConfigs = new ArrayList<>();
        final List<DataConfig> dataConfigs = new ArrayList<>();
        final List<BoostDocumentRule> boostRules = new ArrayList<>();
        final List<PathMapping> pathMappings = new ArrayList<>();
        String jobDefaultScript = "javascript";

        @Override
        protected List<ScheduledJob> loadScheduledJobs() {
            return jobs;
        }

        @Override
        protected List<WebConfig> loadWebConfigs() {
            return webConfigs;
        }

        @Override
        protected List<FileConfig> loadFileConfigs() {
            return fileConfigs;
        }

        @Override
        protected List<DataConfig> loadDataConfigs() {
            return dataConfigs;
        }

        @Override
        protected List<BoostDocumentRule> loadBoostDocumentRules() {
            return boostRules;
        }

        @Override
        protected List<PathMapping> loadPathMappings() {
            return pathMappings;
        }

        @Override
        protected String loadJobDefaultScript() {
            return jobDefaultScript;
        }
    }
}
