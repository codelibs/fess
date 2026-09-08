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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class DefinitionLoaderTest {

    private Map<String, ComponentDefinition> load(final String text) throws Exception {
        return DefinitionLoader.load(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void test_load_groupsByComponentName() throws Exception {
        final Map<String, ComponentDefinition> defs =
                load("component.opensearch.version=3.8.0\ncomponent.opensearch.dest=opensearch\ncomponent.other.version=1.0\n");
        assertEquals(2, defs.size());
        assertEquals("3.8.0", defs.get("opensearch").get("version"));
        assertEquals("opensearch", defs.get("opensearch").get("dest"));
        assertEquals("1.0", defs.get("other").get("version"));
    }

    @Test
    public void test_load_keepsDottedAttributeNames() throws Exception {
        final Map<String, ComponentDefinition> defs = load("component.opensearch.plugin.repository=https://example.org/m2\n");
        assertEquals("https://example.org/m2", defs.get("opensearch").get("plugin.repository"));
    }

    @Test
    public void test_load_ignoresUnrelatedKeys() throws Exception {
        final Map<String, ComponentDefinition> defs = load("unrelated.key=x\ncomponent.opensearch.version=3.8.0\n");
        assertEquals(1, defs.size());
        assertTrue(defs.containsKey("opensearch"));
    }

    @Test
    public void test_resolve_expandsVariables() throws Exception {
        final Map<String, ComponentDefinition> defs = load("component.opensearch.version=3.8.0\n"
                + "component.opensearch.url=https://example.org/opensearch-${version}-${os}-${arch}.${archive.ext}\n");
        final String url = defs.get("opensearch").resolve("url", Map.of("os", "linux", "arch", "x64", "archive.ext", "tar.gz"));
        assertEquals("https://example.org/opensearch-3.8.0-linux-x64.tar.gz", url);
    }

    @Test
    public void test_resolve_ownAttributesAreVariables() throws Exception {
        final Map<String, ComponentDefinition> defs =
                load("component.opensearch.version=3.8.0\ncomponent.opensearch.dir=opensearch-${version}\n");
        assertEquals("opensearch-3.8.0", defs.get("opensearch").resolve("dir", Map.of()));
    }

    @Test
    public void test_resolve_leavesUnknownPlaceholderAsIs() throws Exception {
        final Map<String, ComponentDefinition> defs = load("component.opensearch.url=https://example.org/${nope}\n");
        assertEquals("https://example.org/${nope}", defs.get("opensearch").resolve("url", Map.of()));
    }

    @Test
    public void test_list_splitsOnComma() throws Exception {
        final Map<String, ComponentDefinition> defs = load("component.p.artifacts=org.codelibs.opensearch:a, org.codelibs.opensearch:b\n");
        final List<String> artifacts = defs.get("p").list("artifacts");
        assertEquals(List.of("org.codelibs.opensearch:a", "org.codelibs.opensearch:b"), artifacts);
    }

    @Test
    public void test_list_absentAttributeIsEmpty() throws Exception {
        final Map<String, ComponentDefinition> defs = load("component.p.version=1\n");
        assertTrue(defs.get("p").list("artifacts").isEmpty());
    }

    @Test
    public void test_osToken_comesFromTheDefinition() throws Exception {
        final Map<String, ComponentDefinition> defs =
                load("component.nodejs.os.linux=linux\ncomponent.nodejs.os.macos=darwin\ncomponent.nodejs.os.windows=win\n");
        final ComponentDefinition nodejs = defs.get("nodejs");
        assertEquals("linux", nodejs.osToken(Platform.Os.LINUX));
        assertEquals("darwin", nodejs.osToken(Platform.Os.MACOS));
        assertEquals("win", nodejs.osToken(Platform.Os.WINDOWS));
    }

    @Test
    public void test_osToken_absentMeansNoBuildForThatPlatform() throws Exception {
        final Map<String, ComponentDefinition> defs =
                load("component.opensearch.os.linux=linux\ncomponent.opensearch.os.windows=windows\n");
        final ComponentDefinition opensearch = defs.get("opensearch");
        assertEquals("linux", opensearch.osToken(Platform.Os.LINUX));
        assertEquals("windows", opensearch.osToken(Platform.Os.WINDOWS));
        assertNull(opensearch.osToken(Platform.Os.MACOS), "OpenSearch publishes no macOS build");
        assertNull(opensearch.osToken(Platform.Os.UNKNOWN));
    }

    @Test
    public void test_executable_perOsOverride() throws Exception {
        final Map<String, ComponentDefinition> defs =
                load("component.nodejs.executable=bin/node\ncomponent.nodejs.executable.windows=node.exe\n");
        final ComponentDefinition nodejs = defs.get("nodejs");
        assertEquals("bin/node", nodejs.executable(Platform.Os.LINUX));
        assertEquals("bin/node", nodejs.executable(Platform.Os.MACOS));
        assertEquals("node.exe", nodejs.executable(Platform.Os.WINDOWS));
    }

    @Test
    public void test_executable_absentIsNull() throws Exception {
        final Map<String, ComponentDefinition> defs = load("component.opensearch.version=3.8.0\n");
        assertNull(defs.get("opensearch").executable(Platform.Os.LINUX));
    }

}
