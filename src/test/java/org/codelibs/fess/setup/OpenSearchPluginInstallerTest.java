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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

public class OpenSearchPluginInstallerTest {

    @TempDir
    Path tempDir;

    @Test
    public void test_zipUrl() throws Exception {
        assertEquals(
                "https://repo1.maven.org/maven2/org/codelibs/opensearch/opensearch-analysis-fess/3.8.0/opensearch-analysis-fess-3.8.0.zip",
                OpenSearchPluginInstaller.zipUrl("https://repo1.maven.org/maven2", "org.codelibs.opensearch:opensearch-analysis-fess",
                        "3.8.0"));
    }

    @Test
    public void test_zipUrl_trailingSlashOnRepository() throws Exception {
        assertEquals("https://example.org/m2/org/codelibs/opensearch/opensearch-minhash/3.8.0/opensearch-minhash-3.8.0.zip",
                OpenSearchPluginInstaller.zipUrl("https://example.org/m2/", "org.codelibs.opensearch:opensearch-minhash", "3.8.0"));
    }

    @Test
    public void test_zipUrl_rejectsMalformedCoordinate() {
        final SetupException e = assertThrows(SetupException.class,
                () -> OpenSearchPluginInstaller.zipUrl("https://example.org/m2", "no-colon-here", "3.8.0"));
        assertTrue(e.getMessage().contains("no-colon-here"), e.getMessage());
    }

    @Test
    public void test_zipUrl_rejectsEmptyArtifactId() {
        assertThrows(SetupException.class,
                () -> OpenSearchPluginInstaller.zipUrl("https://example.org/m2", "org.codelibs.opensearch:", "3.8.0"));
    }

    @Test
    public void test_pluginCommand_perOs() {
        final Path home = Path.of("/opt/opensearch");
        assertEquals(home.resolve("bin").resolve("opensearch-plugin.bat"),
                OpenSearchPluginInstaller.pluginCommand(home, Platform.Os.WINDOWS));
        assertEquals(home.resolve("bin").resolve("opensearch-plugin"), OpenSearchPluginInstaller.pluginCommand(home, Platform.Os.LINUX));
    }

    @Test
    public void test_install_reportsMissingPluginTool() {
        final SetupException e =
                assertThrows(SetupException.class, () -> OpenSearchPluginInstaller.install(tempDir, "https://example.org/a.zip"));
        assertTrue(e.getMessage().contains("opensearch-plugin"), e.getMessage());
        assertTrue(e.getMessage().contains("--opensearch-home"), e.getMessage());
    }

    @Test
    public void test_remove_skipsAPluginThatIsNotInstalled() throws Exception {
        // No bin/opensearch-plugin either: a plugin the bundle does not ship never runs the tool.
        assertFalse(OpenSearchPluginInstaller.remove(tempDir, "opensearch-performance-analyzer"));
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void test_remove_runsThePluginTool() throws Exception {
        final Path log = fakePluginTool("""
                echo "$@" > "$(dirname "$0")/args"
                rm -rf "$(dirname "$0")/../plugins/$2"
                """);
        Files.createDirectories(tempDir.resolve("plugins").resolve("opensearch-security-analytics"));

        assertTrue(OpenSearchPluginInstaller.remove(tempDir, "opensearch-security-analytics"));
        assertEquals("remove opensearch-security-analytics", Files.readString(log, StandardCharsets.UTF_8).trim());
        assertFalse(Files.exists(tempDir.resolve("plugins").resolve("opensearch-security-analytics")));
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void test_remove_reportsAFailingPluginTool() throws Exception {
        fakePluginTool("""
                echo 'ERROR: boom'
                exit 74
                """);
        Files.createDirectories(tempDir.resolve("plugins").resolve("opensearch-security-analytics"));

        final SetupException e =
                assertThrows(SetupException.class, () -> OpenSearchPluginInstaller.remove(tempDir, "opensearch-security-analytics"));
        assertTrue(e.getMessage().contains("opensearch-security-analytics"), e.getMessage());
        assertTrue(e.getMessage().contains("74"), e.getMessage());
        assertTrue(e.getMessage().contains("boom"), e.getMessage());
    }

    private Path fakePluginTool(final String body) throws Exception {
        final Path command = OpenSearchPluginInstaller.pluginCommand(tempDir, Platform.Os.LINUX);
        Files.createDirectories(command.getParent());
        Files.writeString(command, "#!/bin/sh\n" + body, StandardCharsets.UTF_8);
        assertTrue(command.toFile().setExecutable(true));
        return command.getParent().resolve("args");
    }
}
