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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class PluginInstallerTest {

    @TempDir
    Path tempDir;

    @Test
    public void test_zipUrl() throws Exception {
        assertEquals(
                "https://repo1.maven.org/maven2/org/codelibs/opensearch/opensearch-analysis-fess/3.8.0/opensearch-analysis-fess-3.8.0.zip",
                PluginInstaller.zipUrl("https://repo1.maven.org/maven2", "org.codelibs.opensearch:opensearch-analysis-fess", "3.8.0"));
    }

    @Test
    public void test_zipUrl_trailingSlashOnRepository() throws Exception {
        assertEquals("https://example.org/m2/org/codelibs/opensearch/opensearch-minhash/3.8.0/opensearch-minhash-3.8.0.zip",
                PluginInstaller.zipUrl("https://example.org/m2/", "org.codelibs.opensearch:opensearch-minhash", "3.8.0"));
    }

    @Test
    public void test_zipUrl_rejectsMalformedCoordinate() {
        final SetupException e =
                assertThrows(SetupException.class, () -> PluginInstaller.zipUrl("https://example.org/m2", "no-colon-here", "3.8.0"));
        assertTrue(e.getMessage().contains("no-colon-here"), e.getMessage());
    }

    @Test
    public void test_zipUrl_rejectsEmptyArtifactId() {
        assertThrows(SetupException.class, () -> PluginInstaller.zipUrl("https://example.org/m2", "org.codelibs.opensearch:", "3.8.0"));
    }

    @Test
    public void test_pluginCommand_perOs() {
        final Path home = Path.of("/opt/opensearch");
        assertEquals(home.resolve("bin").resolve("opensearch-plugin.bat"), PluginInstaller.pluginCommand(home, Platform.Os.WINDOWS));
        assertEquals(home.resolve("bin").resolve("opensearch-plugin"), PluginInstaller.pluginCommand(home, Platform.Os.LINUX));
    }

    @Test
    public void test_install_reportsMissingPluginTool() {
        final SetupException e = assertThrows(SetupException.class, () -> PluginInstaller.install(tempDir, "https://example.org/a.zip"));
        assertTrue(e.getMessage().contains("opensearch-plugin"), e.getMessage());
        assertTrue(e.getMessage().contains("--opensearch-home"), e.getMessage());
    }
}
