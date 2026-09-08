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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FessPluginInstallerTest {

    @TempDir
    Path tempDir;

    @Test
    public void test_directory() {
        assertEquals(Path.of("/opt/fess", "app", "WEB-INF", "plugin"), FessPluginInstaller.directory("/opt/fess"));
    }

    @Test
    public void test_jarName() {
        assertEquals("fess-ds-git-15.9.0.jar", FessPluginInstaller.jarName("fess-ds-git", "15.9.0"));
    }

    @Test
    public void test_isJarOf() {
        assertTrue(FessPluginInstaller.isJarOf("fess-ds-git-15.9.0.jar", "fess-ds-git"));
        assertTrue(FessPluginInstaller.isJarOf("fess-ds-git-15.9.0-SNAPSHOT.jar", "fess-ds-git"));
        assertFalse(FessPluginInstaller.isJarOf("fess-ds-github-15.9.0.jar", "fess-ds-git"));
        assertFalse(FessPluginInstaller.isJarOf("fess-ds-git-15.9.0.jar.part", "fess-ds-git"));
        assertFalse(FessPluginInstaller.isJarOf("fess-ds-slack-15.9.0.jar", "fess-ds-git"));
    }

    /**
     * The name a longer artifact shares with a shorter one is the trap here: the file
     * {@code fess-webapp-mcp-client-1.0.jar} begins with {@code fess-webapp-mcp-}, so a prefix
     * test alone would let {@code remove plugin fess-webapp-mcp} delete somebody else's plugin.
     */
    @Test
    public void test_isJarOf_requiresAVersionAfterTheName() {
        assertFalse(FessPluginInstaller.isJarOf("fess-webapp-mcp-client-1.0.jar", "fess-webapp-mcp"));
        assertTrue(FessPluginInstaller.isJarOf("fess-webapp-mcp-15.9.0.jar", "fess-webapp-mcp"));
    }

    @Test
    public void test_installedJars_findsEveryVersionOfOneArtifact() throws Exception {
        touch("fess-ds-git-15.9.0.jar");
        touch("fess-ds-git-15.9.1.jar");
        touch("fess-ds-slack-15.9.0.jar");
        touch("notes.txt");

        final List<Path> jars = FessPluginInstaller.installedJars(tempDir, "fess-ds-git");
        assertEquals(List.of("fess-ds-git-15.9.0.jar", "fess-ds-git-15.9.1.jar"),
                jars.stream().map(p -> p.getFileName().toString()).toList());
    }

    @Test
    public void test_installedJars_onAMissingDirectory() throws Exception {
        assertEquals(List.of(), FessPluginInstaller.installedJars(tempDir.resolve("absent"), "fess-ds-git"));
    }

    @Test
    public void test_installed_listsEveryPluginJar() throws Exception {
        touch("fess-ds-git-15.9.0.jar");
        touch("fess-script-groovy-15.9.0.jar");
        touch("README");

        assertEquals(List.of("fess-ds-git-15.9.0.jar", "fess-script-groovy-15.9.0.jar"),
                FessPluginInstaller.installed(tempDir).stream().map(p -> p.getFileName().toString()).toList());
    }

    @Test
    public void test_remove() throws Exception {
        touch("fess-ds-git-15.9.0.jar");
        touch("fess-ds-slack-15.9.0.jar");

        final List<Path> removed = FessPluginInstaller.remove(tempDir, "fess-ds-git");

        assertEquals(List.of("fess-ds-git-15.9.0.jar"), removed.stream().map(p -> p.getFileName().toString()).toList());
        assertFalse(Files.exists(tempDir.resolve("fess-ds-git-15.9.0.jar")));
        assertTrue(Files.exists(tempDir.resolve("fess-ds-slack-15.9.0.jar")));
    }

    @Test
    public void test_remove_reportsNothingWhenTheArtifactIsNotInstalled() throws Exception {
        assertEquals(List.of(), FessPluginInstaller.remove(tempDir, "fess-ds-git"));
    }

    /**
     * The version this Fess accepts a plugin for. Plugins track the major.minor of the release
     * they were built against, so the patch level and the SNAPSHOT suffix have to come off.
     */
    @Test
    public void test_productVersion() throws Exception {
        assertEquals("15.9", FessPluginInstaller.productVersion("15.9.0"));
        assertEquals("15.9", FessPluginInstaller.productVersion("15.9.0-SNAPSHOT"));
        assertEquals("15.9", FessPluginInstaller.productVersion("15.9.10"));
    }

    @Test
    public void test_productVersion_rejectsWhatItCannotRead() {
        for (final String version : List.of("", "15", "x.y.z")) {
            try {
                FessPluginInstaller.productVersion(version);
                throw new AssertionError("expected a SetupException for: " + version);
            } catch (final SetupException expected) {
                assertTrue(expected.getMessage().contains("--version"), expected.getMessage());
            }
        }
    }

    @Test
    public void test_versionOf() {
        assertEquals("15.9.0", FessPluginInstaller.versionOf("fess-ds-git-15.9.0.jar", "fess-ds-git"));
        assertEquals("15.9.0-SNAPSHOT", FessPluginInstaller.versionOf("fess-ds-git-15.9.0-SNAPSHOT.jar", "fess-ds-git"));
        assertNull(FessPluginInstaller.versionOf("fess-ds-slack-15.9.0.jar", "fess-ds-git"));
    }

    @Test
    public void test_removeOtherVersions() throws Exception {
        touch("fess-ds-git-15.9.0.jar");
        touch("fess-ds-git-15.9.1.jar");
        touch("fess-ds-slack-15.9.0.jar");

        final Path keep = tempDir.resolve("fess-ds-git-15.9.1.jar");
        final List<Path> removed = FessPluginInstaller.removeOtherVersions(tempDir, "fess-ds-git", keep);

        assertEquals(List.of("fess-ds-git-15.9.0.jar"), removed.stream().map(p -> p.getFileName().toString()).toList());
        assertTrue(Files.exists(keep));
        assertTrue(Files.exists(tempDir.resolve("fess-ds-slack-15.9.0.jar")));
    }

    private void touch(final String name) throws IOException {
        Files.writeString(tempDir.resolve(name), "x");
    }
}
