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
package org.codelibs.fess.setup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class FessSetupTest {

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    private int run(final String... args) {
        return FessSetup.run(args, new PrintStream(out, true, StandardCharsets.UTF_8), new PrintStream(err, true, StandardCharsets.UTF_8));
    }

    private String out() {
        return out.toString(StandardCharsets.UTF_8);
    }

    private String err() {
        return err.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void test_noArguments_printsUsage() {
        assertEquals(2, run());
        assertTrue(err().contains("Usage:"), err());
    }

    @Test
    public void test_list_showsOpensearch() {
        assertEquals(0, run("list"));
        assertTrue(out().contains("opensearch"), out());
        assertTrue(out().contains("3.8.0"), out());
    }

    @Test
    public void test_unknownComponent_isReported() {
        assertEquals(1, run("install", "nonexistent"));
        assertTrue(err().contains("nonexistent"), err());
    }

    @Test
    public void test_installPlugins_requiresOpensearchHome() {
        assertEquals(2, run("install", "plugins"));
        assertTrue(err().contains("--opensearch-home"), err());
    }

    @Test
    public void test_install_withoutTarget_printsUsage() {
        assertEquals(2, run("install"));
        assertTrue(err().contains("Usage:"), err());
    }

    @Test
    public void test_unknownCommand_printsUsage() {
        assertEquals(2, run("frobnicate"));
        assertTrue(err().contains("Usage:"), err());
    }

    @Test
    public void test_parseOptions_readsValuesAndFlags() {
        final Map<String, String> options =
                FessSetup.parseOptions(new String[] { "install", "opensearch", "--dest", "/opt", "--force" }, 2);
        assertEquals("/opt", options.get("dest"));
        assertEquals("true", options.get("force"));
    }

    @Test
    public void test_loadDefinitions_findsTheBundledFile() throws Exception {
        final Map<String, ComponentDefinition> defs = FessSetup.loadDefinitions();
        assertTrue(defs.containsKey("opensearch"), defs.keySet().toString());
        assertEquals("3.8.0", defs.get("opensearch").get("version"));
        assertEquals(4, defs.get("opensearch").list("plugin.artifacts").size());
    }
}
