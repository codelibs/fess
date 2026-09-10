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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FessSetupTest {

    @TempDir
    Path tempDir;

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
    public void test_installOpensearchPlugins_requiresOpensearchHome() {
        assertEquals(2, run("install", "opensearch-plugins"));
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

    @Test
    public void test_loadDefinitions_opensearchPlatforms() throws Exception {
        final ComponentDefinition opensearch = FessSetup.loadDefinitions().get("opensearch");
        assertEquals("linux", opensearch.osToken(Platform.Os.LINUX));
        assertEquals("windows", opensearch.osToken(Platform.Os.WINDOWS));
        assertNull(opensearch.osToken(Platform.Os.MACOS), "OpenSearch publishes no macOS build");
        assertNotNull(opensearch.get("hint.unavailable"), "macOS users need a way forward");
        assertTrue(opensearch.get("hint.unavailable").contains("brew"), opensearch.get("hint.unavailable"));
    }

    @Test
    public void test_loadDefinitions_nodejsForPlaywright() throws Exception {
        final Map<String, ComponentDefinition> defs = FessSetup.loadDefinitions();
        assertTrue(defs.containsKey("nodejs"), defs.keySet().toString());
        final ComponentDefinition nodejs = defs.get("nodejs");
        assertEquals("linux", nodejs.osToken(Platform.Os.LINUX));
        assertEquals("darwin", nodejs.osToken(Platform.Os.MACOS));
        assertEquals("win", nodejs.osToken(Platform.Os.WINDOWS));
        assertEquals("bin/node", nodejs.executable(Platform.Os.LINUX));
        assertEquals("node.exe", nodejs.executable(Platform.Os.WINDOWS));
        assertEquals("PLAYWRIGHT_NODEJS_PATH", nodejs.get("env"));
    }

    @Test
    public void test_loadDefinitions_nodejsUrlResolves() throws Exception {
        final ComponentDefinition nodejs = FessSetup.loadDefinitions().get("nodejs");
        final String url =
                nodejs.resolve("url", Map.of("os", "darwin", "arch", "arm64", "archive.ext", "tar.gz", "version", nodejs.get("version")));
        assertTrue(url.startsWith("https://nodejs.org/dist/v"), url);
        assertTrue(url.endsWith("-darwin-arm64.tar.gz"), url);
    }

    @Test
    public void test_list_showsBothComponents() {
        assertEquals(0, run("list"));
        assertTrue(out().contains("opensearch"), out());
        assertTrue(out().contains("nodejs"), out());
    }

    /**
     * The plugin definition holds a repository rather than a download of its own, so
     * {@code install plugin} uses it but {@code list} must not offer it as a component:
     * {@code install plugin} is the command, and there is nothing to download without a name.
     */
    @Test
    public void test_list_omitsThePluginDefinition() {
        assertEquals(0, run("list"));
        for (final String line : out().split("\n")) {
            assertTrue(!"plugin".equals(line.trim()), out());
        }
    }

    @Test
    public void test_loadDefinitions_pluginRepository() throws Exception {
        final ComponentDefinition plugin = FessSetup.loadDefinitions().get("plugin");
        assertNotNull(plugin, "install plugin needs a repository to read");
        assertTrue(plugin.get("repository").startsWith("https://"), plugin.get("repository"));
        assertNull(plugin.get("url"), "a plugin is named on the command line, not by the definition");
        assertEquals("/opt/fess/app/WEB-INF/plugin", plugin.resolve("dest", Map.of("fess.home", "/opt/fess")));
    }

    @Test
    public void test_installPlugin_withoutAName_saysSo() {
        assertEquals(2, run("install", "plugin"));
        assertTrue(err().contains("list plugins"), err());
    }

    @Test
    public void test_removePlugin_withoutAName_saysSo() {
        assertEquals(2, run("remove", "plugin"));
        assertTrue(err().contains("plugin name"), err());
    }

    @Test
    public void test_remove_withoutTarget_printsUsage() {
        assertEquals(2, run("remove"));
        assertTrue(err().contains("Usage:"), err());
    }

    @Test
    public void test_removePlugin_reportsAPluginThatIsNotInstalled() {
        assertEquals(0, run("remove", "plugin", "fess-ds-git", "--dest", tempDir.toString()));
        assertTrue(out().contains("fess-ds-git is not installed"), out());
    }

    @Test
    public void test_removePlugin_deletesTheJarAndAsksForARestart() throws Exception {
        java.nio.file.Files.writeString(tempDir.resolve("fess-ds-git-15.9.0.jar"), "x");
        java.nio.file.Files.writeString(tempDir.resolve("fess-ds-slack-15.9.0.jar"), "x");

        assertEquals(0, run("remove", "plugin", "fess-ds-git", "--dest", tempDir.toString()));

        assertTrue(out().contains("fess-ds-git-15.9.0.jar"), out());
        assertTrue(out().contains("Restart Fess"), out());
        assertTrue(!java.nio.file.Files.exists(tempDir.resolve("fess-ds-git-15.9.0.jar")));
        assertTrue(java.nio.file.Files.exists(tempDir.resolve("fess-ds-slack-15.9.0.jar")));
    }

    @Test
    public void test_list_rejectsAnUnknownTarget() {
        assertEquals(2, run("list", "widgets"));
        assertTrue(err().contains("widgets"), err());
    }

    @Test
    public void test_engineUrl_prefersTheOptionThenTheEnvironment() {
        assertEquals("http://es:9200", FessSetup.engineUrl(Map.of("url", "http://es:9200")));
        assertEquals("http://es:9200", FessSetup.engineUrl(Map.of("url", "http://es:9200/")), "a trailing slash would double up");
        // With neither option nor environment variable, the default matches the shipped config.
        if (System.getenv("SEARCH_ENGINE_HTTP_URL") == null) {
            assertEquals("http://localhost:9200", FessSetup.engineUrl(Map.of()));
        }
    }

    @Test
    public void test_artifactIdOf_dropsTheGroup() {
        assertEquals("opensearch-analysis-fess", FessSetup.artifactIdOf("org.codelibs.opensearch:opensearch-analysis-fess"));
        assertEquals("already-bare", FessSetup.artifactIdOf("already-bare"));
    }

    @Test
    public void test_listInstalled_onAnEmptyDirectory() {
        assertEquals(0, run("list", "installed", "--dest", tempDir.toString()));
        assertTrue(out().contains("No plugins are installed"), out());
    }

    @Test
    public void test_listInstalled_namesEachPluginOnce() throws Exception {
        java.nio.file.Files.writeString(tempDir.resolve("fess-ds-git-15.9.0.jar"), "x");
        java.nio.file.Files.writeString(tempDir.resolve("fess-script-groovy-15.9.0.jar"), "x");

        assertEquals(0, run("list", "installed", "--dest", tempDir.toString()));
        assertTrue(out().contains("fess-ds-git  15.9.0"), out());
        assertTrue(out().contains("fess-script-groovy  15.9.0"), out());
    }

    @Test
    public void test_upgrade_withoutTarget_printsUsage() {
        assertEquals(2, run("upgrade"));
        assertTrue(err().contains("Usage:"), err());
    }

    @Test
    public void test_upgrade_withNothingInstalled_saysSo() {
        assertEquals(0, run("upgrade", "plugins", "--dest", tempDir.toString()));
        assertTrue(out().contains("No plugins are installed"), out());
    }

    @Test
    public void test_sizes_useTheUnitThatSuitsTheTotal() {
        assertEquals("512/1024 MiB", FessSetup.sizes(512L << 20, 1024L << 20));
        assertEquals("4/9 KiB", FessSetup.sizes(4096L, 9728L));
        assertEquals("100/512 B", FessSetup.sizes(100L, 512L));
    }

    @Test
    public void test_size_picksTheLargestUnitThatDoesNotRoundToZero() {
        assertEquals("2 MiB", FessSetup.size(2L << 20));
        assertEquals("3 KiB", FessSetup.size(3072L));
        assertEquals("7 B", FessSetup.size(7L));
    }

    @Test
    public void test_positionals_skipOptionsAndTheirValues() {
        assertEquals(java.util.List.of("fess-ds-git", "fess-ds-slack"), FessSetup
                .positionals(new String[] { "install", "plugin", "fess-ds-git", "--version", "15.9.0", "fess-ds-slack", "--force" }, 2));
    }

    @Test
    public void test_parsePluginSpec_takesTheVersionAfterTheColon() throws Exception {
        final FessSetup.PluginSpec spec = FessSetup.parsePluginSpec("fess-ds-git:15.9.0", null);
        assertEquals("fess-ds-git", spec.artifactId());
        assertEquals("15.9.0", spec.version());
    }

    @Test
    public void test_parsePluginSpec_fallsBackToTheOptionVersion() throws Exception {
        final FessSetup.PluginSpec spec = FessSetup.parsePluginSpec("fess-ds-git", "15.9.0");
        assertEquals("fess-ds-git", spec.artifactId());
        assertEquals("15.9.0", spec.version());
    }

    @Test
    public void test_parsePluginSpec_leavesTheVersionUnresolvedWhenNoneIsGiven() throws Exception {
        final FessSetup.PluginSpec spec = FessSetup.parsePluginSpec("fess-ds-git", null);
        assertEquals("fess-ds-git", spec.artifactId());
        assertNull(spec.version());
    }

    @Test
    public void test_parsePluginSpec_prefersTheColonOverTheOption() throws Exception {
        assertEquals("15.9.1", FessSetup.parsePluginSpec("fess-ds-git:15.9.1", "15.9.0").version());
    }

    @Test
    public void test_parsePluginSpec_rejectsAnEmptyName() {
        final SetupException e = assertThrows(SetupException.class, () -> FessSetup.parsePluginSpec(":15.9.0", null));
        assertTrue(e.getMessage().contains(":15.9.0"), e.getMessage());
    }

    @Test
    public void test_parsePluginSpec_rejectsAnEmptyVersion() {
        final SetupException e = assertThrows(SetupException.class, () -> FessSetup.parsePluginSpec("fess-ds-git:", null));
        assertTrue(e.getMessage().contains("fess-ds-git:"), e.getMessage());
    }

    @Test
    public void test_parsePluginSpec_rejectsAVersionThatCouldEscapeThePluginDirectory() {
        // The version becomes the jar's file name, which is resolved against the plugin
        // directory, so a separator in it would write outside that directory.
        assertThrows(SetupException.class, () -> FessSetup.parsePluginSpec("fess-ds-git:../../evil", null));
        assertThrows(SetupException.class, () -> FessSetup.parsePluginSpec("fess-ds-git", "../../evil"));
    }

    @Test
    public void test_installPlugin_reportsAMalformedSpecAsAUsageError() {
        assertEquals(2, run("install", "plugin", "fess-ds-git:"));
        assertTrue(err().contains("fess-ds-git:"), err());
    }

    @Test
    public void test_fessVersion_isEmptyWithoutAManifest() {
        // Under test the class comes from target/classes, which has no manifest. The install
        // path then reports that --version is needed rather than guessing a plugin version.
        final SetupException e = assertThrows(SetupException.class, () -> FessPluginInstaller.productVersion(FessSetup.fessVersion()));
        assertTrue(e.getMessage().contains("--version"), e.getMessage());
    }

    @Test
    public void test_fessHome_comesFromTheLauncher() {
        final String original = System.getProperty("fess.home");
        try {
            System.setProperty("fess.home", "/opt/fess");
            assertEquals("/opt/fess", FessSetup.fessHome());
        } finally {
            if (original == null) {
                System.clearProperty("fess.home");
            } else {
                System.setProperty("fess.home", original);
            }
        }
    }

    @Test
    public void test_fessHome_fallsBackToTheWorkingDirectory() {
        final String original = System.getProperty("fess.home");
        try {
            System.clearProperty("fess.home");
            final String home = FessSetup.fessHome();
            assertNotNull(home);
            assertTrue(Path.of(home).isAbsolute(), home);
        } finally {
            if (original != null) {
                System.setProperty("fess.home", original);
            }
        }
    }

    @Test
    public void test_destinationsAreRelativeToFessHome() throws Exception {
        final Map<String, ComponentDefinition> defs = FessSetup.loadDefinitions();
        for (final String name : new String[] { "opensearch", "nodejs" }) {
            final String dest = defs.get(name).resolve("dest", Map.of("fess.home", "/opt/fess"));
            assertTrue(dest.startsWith("/opt/fess/"), name + " -> " + dest);
        }
    }

    @Test
    public void test_loadDefinitions_pluginSnapshotRepository() throws Exception {
        final ComponentDefinition plugin = FessSetup.loadDefinitions().get("plugin");
        final String snapshot = plugin.get("snapshot.repository");
        assertNotNull(snapshot, "a development build installs plugins from the snapshot repository");
        assertTrue(snapshot.startsWith("https://"), snapshot);
        assertTrue(snapshot.contains("snapshot"), snapshot);
    }

    @Test
    public void test_loadDefinitions_pluginGithub() throws Exception {
        final ComponentDefinition plugin = FessSetup.loadDefinitions().get("plugin");
        final String github = plugin.get("github");
        assertNotNull(github, "a release is downloaded from the plugin's GitHub release");
        assertTrue(github.startsWith("https://github.com/"), github);
    }

    @Test
    public void test_pluginSources_comeFromTheDefinition() throws Exception {
        final ComponentDefinition plugin = FessSetup.loadDefinitions().get("plugin");
        final PluginSources sources = FessSetup.pluginSources(plugin, Map.<String, String> of());
        assertEquals(plugin.get("repository"), sources.release());
        assertEquals(plugin.get("snapshot.repository"), sources.snapshot());
        assertEquals(plugin.get("github"), sources.github());
    }

    @Test
    public void test_pluginSources_repositoryOptionReplacesEverySource() throws Exception {
        final ComponentDefinition plugin = FessSetup.loadDefinitions().get("plugin");
        final PluginSources sources = FessSetup.pluginSources(plugin, Map.of("repository", "https://example.org/m2"));
        assertEquals("https://example.org/m2", sources.release());
        assertEquals("https://example.org/m2", sources.snapshot(), "--repository names where every jar comes from");
        assertFalse(sources.hasGithub(), "--repository must not be quietly ignored in favour of github.com");
    }
}
