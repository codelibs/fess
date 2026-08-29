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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.script.Compilable;
import javax.script.ScriptException;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ShippedScriptCompilationTest extends UnitFessTestCase {

    /**
     * Compiles a template the way JavaScriptEngine does: as an expression first, then as
     * statements. Throws ScriptException if neither shape parses.
     */
    private void assertCompiles(final String script) throws ScriptException {
        final Compilable compilable = (Compilable) JavaScriptEngine.ENGINE_FACTORY.getScriptEngine(JavaScriptEngine.ENGINE_ARGS);
        try {
            compilable.compile("(function(){ return (" + script + "\n); })()");
        } catch (final ScriptException asExpression) {
            compilable.compile("(function(){ " + script + "\n})()");
        }
    }

    @Test
    public void test_shippedScheduledJobsCompile() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final List<String> scripts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("fess_indices/fess_config.scheduled_job/scheduled_job.bulk"),
                StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                final JsonNode node = mapper.readTree(line);
                if (node.has("scriptData")) {
                    assertEquals("javascript", node.get("scriptType").asText());
                    scripts.add(node.get("scriptData").asText());
                }
            }
        }
        assertEquals(14, scripts.size());
        for (final String script : scripts) {
            assertCompiles(script);
        }
    }

    @Test
    public void test_jobTemplateScriptCompiles() throws Exception {
        final Properties properties = new Properties();
        try (InputStreamReader reader =
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream("fess_config.properties"), StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        final String template = properties.getProperty("job.template.script");
        assertNotNull(template);
        assertCompiles(MessageFormat.format(template, "\"W1\"", "", "", "job_id"));
    }
}
