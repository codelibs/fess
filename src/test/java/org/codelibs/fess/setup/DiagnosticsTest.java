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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.codelibs.fess.setup.Diagnostics.Check;
import org.codelibs.fess.setup.Diagnostics.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DiagnosticsTest {

    @TempDir
    Path tempDir;

    private Path writeJar(final String name) throws IOException {
        final Path jar = tempDir.resolve(name);
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(jar))) {
            zip.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
            zip.write("Manifest-Version: 1.0\n".getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
        }
        return jar;
    }

    /**
     * _cat returns one line per node, so a three-node cluster reports every plugin three
     * times and its version three times.
     */
    @Test
    public void test_distinctLines() {
        assertEquals(List.of("3.8.0"), Diagnostics.distinctLines("3.8.0\n3.8.0\n3.8.0\n"));
        assertEquals(List.of("opensearch-configsync", "opensearch-minhash"),
                Diagnostics.distinctLines("opensearch-configsync\nopensearch-minhash\nopensearch-configsync\n"));
        assertEquals(List.of(), Diagnostics.distinctLines("  \n\n"));
    }

    /**
     * A cluster running two versions at once is a rolling upgrade, or a mistake. Either way
     * Fess talks to whichever node answers, so it must not be reported as fine.
     */
    @Test
    public void test_engineVersion_reportsAMixedCluster() {
        final Check ok = Diagnostics.engineVersion(List.of("3.8.0"));
        assertEquals(Status.OK, ok.status());
        assertTrue(ok.detail().contains("3.8.0"), ok.detail());

        final Check mixed = Diagnostics.engineVersion(List.of("3.8.0", "2.19.1"));
        assertEquals(Status.WARN, mixed.status());
        assertTrue(mixed.detail().contains("2.19.1"), mixed.detail());

        assertEquals(Status.FAIL, Diagnostics.engineVersion(List.of()).status());
    }

    @Test
    public void test_enginePlugins_namesTheMissingOnes() {
        final List<String> required = List.of("opensearch-analysis-fess", "opensearch-configsync", "opensearch-minhash");

        final Check all = Diagnostics.enginePlugins(required,
                List.of("opensearch-minhash", "opensearch-analysis-fess", "opensearch-configsync", "opensearch-knn"));
        assertEquals(Status.OK, all.status());

        final Check some = Diagnostics.enginePlugins(required, List.of("opensearch-analysis-fess"));
        assertEquals(Status.FAIL, some.status());
        assertTrue(some.detail().contains("opensearch-configsync"), some.detail());
        assertTrue(some.detail().contains("opensearch-minhash"), some.detail());
        assertTrue(!some.detail().contains("opensearch-analysis-fess"), some.detail());
    }

    /**
     * The names on the two sides differ. plugin.artifacts holds Maven artifact names; _cat
     * reports the name from each plugin's descriptor, and for the CodeLibs plugins those drop
     * the opensearch- prefix. This list is what a real fess-opensearch 3.8.0 image reports.
     */
    @Test
    public void test_enginePlugins_matchesDescriptorNamesAgainstArtifactNames() {
        final List<String> required =
                List.of("opensearch-analysis-fess", "opensearch-analysis-extension", "opensearch-minhash", "opensearch-configsync");
        final List<String> reported =
                List.of("analysis-extension", "analysis-fess", "configsync", "minhash", "opensearch-knn", "opensearch-security");

        final Check check = Diagnostics.enginePlugins(required, reported);
        assertEquals(Status.OK, check.status(), check.detail());
    }

    @Test
    public void test_enginePlugins_stillNoticesAGenuinelyMissingPlugin() {
        final Check check =
                Diagnostics.enginePlugins(List.of("opensearch-configsync", "opensearch-minhash"), List.of("configsync", "opensearch-knn"));
        assertEquals(Status.FAIL, check.status());
        assertTrue(check.detail().contains("opensearch-minhash"), check.detail());
        assertTrue(!check.detail().contains("configsync"), check.detail());
    }

    /**
     * Two jars of one plugin both load, and which one wins is not defined. That is a failure,
     * not a warning: the installation behaves differently from the one the operator described.
     */
    @Test
    public void test_installedPlugins_reportsTwoVersionsOfOnePlugin() throws Exception {
        writeJar("fess-ds-git-15.9.0.jar");
        writeJar("fess-ds-git-15.9.1.jar");

        final List<Check> checks = Diagnostics.installedPlugins(tempDir, "15.9");
        assertEquals(1, checks.size());
        assertEquals(Status.FAIL, checks.get(0).status());
        assertTrue(checks.get(0).detail().contains("15.9.0"), checks.get(0).detail());
        assertTrue(checks.get(0).detail().contains("15.9.1"), checks.get(0).detail());
    }

    /**
     * A plugin jar left over from another release is a failure: the war changes under a plugin
     * between releases, and such a jar can stop Fess from starting at all.
     */
    @Test
    public void test_installedPlugins_failsWhenAPluginIsForAnotherRelease() throws Exception {
        writeJar("fess-ds-git-15.8.0.jar");
        writeJar("fess-ds-slack-15.9.0.jar");

        final List<Check> checks = Diagnostics.installedPlugins(tempDir, "15.9");
        assertEquals(2, checks.size());
        final Check git = checks.stream().filter(c -> c.name().contains("fess-ds-git")).findFirst().orElseThrow();
        assertEquals(Status.FAIL, git.status());
        assertTrue(git.detail().contains("15.8.0"), git.detail());
        assertTrue(git.detail().contains("fess-setup upgrade plugins"), "the remedy is named: " + git.detail());
        final Check slack = checks.stream().filter(c -> c.name().contains("fess-ds-slack")).findFirst().orElseThrow();
        assertEquals(Status.OK, slack.status());
    }

    @Test
    public void test_installedPlugins_acceptsASnapshotOfThisRelease() throws Exception {
        writeJar("fess-webapp-mcp-15.9.0-20260914.212412-2.jar");

        final List<Check> checks = Diagnostics.installedPlugins(tempDir, "15.9");
        assertEquals(1, checks.size());
        assertEquals(Status.OK, checks.get(0).status(), checks.get(0).detail());
    }

    /**
     * An empty, truncated or non-zip file named like a plugin jar is skipped by the servlet
     * container, so the plugin does not load; the report must not call it installed.
     */
    @Test
    public void test_installedPlugins_failsOnAJarThatIsNotAZipArchive() throws Exception {
        Files.write(tempDir.resolve("fess-ds-zero-15.9.0.jar"), new byte[0]);
        Files.writeString(tempDir.resolve("fess-ds-text-15.9.0.jar"), "this is not a jar");
        final Path good = writeJar("fess-ds-good-15.9.0.jar");
        final byte[] bytes = Files.readAllBytes(good);
        Files.write(tempDir.resolve("fess-ds-trunc-15.9.0.jar"), Arrays.copyOf(bytes, bytes.length / 2));

        final List<Check> checks = Diagnostics.installedPlugins(tempDir, "15.9");
        assertEquals(4, checks.size());
        assertEquals(Status.OK, checks.stream().filter(c -> c.name().contains("fess-ds-good")).findFirst().orElseThrow().status());
        for (final String name : List.of("fess-ds-zero", "fess-ds-text", "fess-ds-trunc")) {
            final Check check = checks.stream().filter(c -> c.name().contains(name)).findFirst().orElseThrow();
            assertEquals(Status.FAIL, check.status(), check.detail());
            assertTrue(check.detail().contains(name + "-15.9.0.jar"), "the file is named: " + check.detail());
            assertTrue(check.detail().contains("fess-setup remove plugin " + name), "the remedy is named: " + check.detail());
        }
        assertEquals(Status.FAIL, Diagnostics.worst(checks));
    }

    @Test
    public void test_installedPlugins_onAnEmptyDirectory() throws Exception {
        assertEquals(List.of(), Diagnostics.installedPlugins(tempDir, "15.9"));
    }

    /**
     * Node.js is only needed by the Playwright crawler, so a missing one is reported without
     * being called a failure -- until the caller says this installation does use Playwright.
     */
    @Test
    public void test_nodejs_isOnlyAFailureWhenTheCallerSaysItIsNeeded() throws Exception {
        final Check absent = Diagnostics.nodejs(tempDir, false);
        assertEquals(Status.OK, absent.status());
        assertTrue(absent.detail().contains("Playwright"), absent.detail());
        assertEquals(Status.FAIL, Diagnostics.nodejs(tempDir, true).status());

        final Path node = tempDir.resolve("nodejs/node-v24.20.0-darwin-arm64/bin/node");
        Files.createDirectories(node.getParent());
        Files.writeString(node, "#!/bin/sh\n");
        final Check found = Diagnostics.nodejs(tempDir, true);
        assertEquals(Status.OK, found.status());
        assertTrue(found.detail().contains("node-v24.20.0"), found.detail());
    }

    /**
     * The answer of _settings/index.analysis.*?flat_settings=true, as OpenSearch writes it.
     */
    private static final String RELATIVE_SETTINGS = "{\"relative_ctl\":{\"settings\":{"
            + "\"index.analysis.analyzer.a.char_filter\":[\"m\"],\"index.analysis.analyzer.a.tokenizer\":\"standard\","
            + "\"index.analysis.char_filter.m.mappings_path\":\"dictionary/ja/mapping.txt\","
            + "\"index.analysis.filter.s.synonyms_path\":\"dictionary/synonym.txt\",\"index.analysis.filter.s.type\":\"synonym\"}}}";

    /**
     * An index created by Fess 15.9 or earlier: every dictionary path names the config directory
     * of the engine it was created on.
     */
    private static final String ABSOLUTE_SETTINGS = "{\"fess.20250101\":{\"settings\":{"
            + "\"index.analysis.char_filter.m.mappings_path\":\"/opt/opensearch-3.9.0/config/dictionary/ja/mapping.txt\","
            + "\"index.analysis.filter.s.synonyms_path\":\"/opt/opensearch-3.9.0/config/dictionary/synonym.txt\","
            + "\"index.analysis.filter.st.stopwords\":[\"a\",\"the\"],\"index.analysis.filter.st.type\":\"stop\"}}}";

    @Test
    public void test_isAbsolutePath() {
        assertTrue(Diagnostics.isAbsolutePath("/usr/share/opensearch/config/dictionary/synonym.txt"));
        assertTrue(Diagnostics.isAbsolutePath("C:\\opensearch\\config\\dictionary\\synonym.txt"));
        assertTrue(Diagnostics.isAbsolutePath("c:/opensearch/config/dictionary/synonym.txt"));
        assertTrue(Diagnostics.isAbsolutePath("\\\\server\\share\\dictionary\\synonym.txt"));
        assertFalse(Diagnostics.isAbsolutePath("dictionary/synonym.txt"));
        assertFalse(Diagnostics.isAbsolutePath("dictionary\\ja\\mapping.txt"));
        assertFalse(Diagnostics.isAbsolutePath("C:dictionary\\synonym.txt"));
        assertFalse(Diagnostics.isAbsolutePath("1:\\dictionary"));
        assertFalse(Diagnostics.isAbsolutePath(""));
    }

    @Test
    public void test_isPathSetting() {
        assertTrue(Diagnostics.isPathSetting("index.analysis.filter.s.synonyms_path"));
        assertTrue(Diagnostics.isPathSetting("index.analysis.char_filter.m.mappings_path"));
        assertTrue(Diagnostics.isPathSetting("index.analysis.tokenizer.ko.user_dict_path"));
        assertTrue(Diagnostics.isPathSetting("index.analysis.tokenizer.ja.user_dictionary"));
        assertFalse(Diagnostics.isPathSetting("index.analysis.tokenizer.ja.user_dictionary_rules"));
        assertFalse(Diagnostics.isPathSetting("index.analysis.filter.s.type"));
        assertFalse(Diagnostics.isPathSetting("index.analysis.filter.path_hierarchy.type"));
    }

    @Test
    public void test_dictionaryPaths_relativeIsOk() throws Exception {
        final List<Check> checks = Diagnostics.dictionaryPaths(RELATIVE_SETTINGS, List.of());
        assertEquals(1, checks.size());
        assertEquals(Status.OK, checks.get(0).status(), checks.get(0).detail());
        assertEquals("2 path setting(s) in 1 index(es), all relative", checks.get(0).detail());
    }

    @Test
    public void test_dictionaryPaths_noAnalysisSettings() throws Exception {
        for (final String json : new String[] { "{}", "{\"configsync\":{\"settings\":{}}}" }) {
            final List<Check> checks = Diagnostics.dictionaryPaths(json, List.of());
            assertEquals(1, checks.size());
            assertEquals(Status.OK, checks.get(0).status());
            assertEquals("no index names a dictionary file", checks.get(0).detail());
        }
    }

    /**
     * The index still opens on the engine it was created on, so this is a warning about the
     * next move, naming the index and how many settings are affected.
     */
    @Test
    public void test_dictionaryPaths_absoluteWarns() throws Exception {
        final List<Check> checks = Diagnostics.dictionaryPaths(ABSOLUTE_SETTINGS, List.of());
        assertEquals(1, checks.size());
        final Check check = checks.get(0);
        assertEquals(Status.WARN, check.status(), check.detail());
        assertTrue(check.detail().startsWith("fess.20250101: 2 analyzer setting(s)"), check.detail());
        assertTrue(check.detail().contains("/opt/opensearch-3.9.0/config/dictionary/ja/mapping.txt"), check.detail());
        assertTrue(check.detail().contains("set FESS_DICTIONARY_PATH=dictionary/ and re-create the index"), check.detail());
        assertTrue(check.detail().contains("keep the engine where it is"), check.detail());
    }

    @Test
    public void test_dictionaryPaths_mixedReportsOnlyTheAbsoluteIndex() throws Exception {
        final String json = RELATIVE_SETTINGS.substring(0, RELATIVE_SETTINGS.length() - 1) + "," + ABSOLUTE_SETTINGS.substring(1);
        final List<Check> checks = Diagnostics.dictionaryPaths(json, List.of());
        assertEquals(1, checks.size());
        assertEquals(Status.WARN, checks.get(0).status());
        assertTrue(checks.get(0).detail().startsWith("fess.20250101:"), checks.get(0).detail());
    }

    /**
     * A Windows path reaches the JSON with its backslashes escaped; reading it with anything
     * less than a JSON reader leaves C:\\ in place and misses the drive letter. This is the
     * layout of a Windows ZIP install, whose engine directory carries the version.
     */
    @Test
    public void test_dictionaryPaths_windowsPathsThroughJson() throws Exception {
        final String json = "{\"fess_suggest_analyzer\":{\"settings\":{" + "\"index.analysis.tokenizer.ja.user_dictionary\":"
                + "\"C:\\\\fess-15.9.0\\\\opensearch\\\\opensearch-3.9.0\\\\config\\\\dictionary\\\\ja\\\\kuromoji.txt\","
                + "\"index.analysis.filter.s.stopwords_path\":\"dictionary/stopwords.txt\"}}}";
        final List<Check> checks = Diagnostics.dictionaryPaths(json, List.of());
        assertEquals(1, checks.size());
        assertEquals(Status.WARN, checks.get(0).status(), checks.get(0).detail());
        assertTrue(checks.get(0).detail().contains("1 analyzer setting(s)"), checks.get(0).detail());
        assertTrue(checks.get(0).detail().contains("C:\\fess-15.9.0\\opensearch\\opensearch-3.9.0\\config\\dictionary\\ja\\kuromoji.txt"),
                checks.get(0).detail());
    }

    /**
     * The escaped form of a Windows path without a version in it is still read as an absolute
     * path -- it is counted as one -- but it does not move with an upgrade.
     */
    @Test
    public void test_dictionaryPaths_windowsFixedPathThroughJson() throws Exception {
        final String json = "{\"fess.20250101\":{\"settings\":{"
                + "\"index.analysis.filter.s.synonyms_path\":\"C:\\\\opensearch\\\\config\\\\dictionary\\\\x.txt\"}}}";
        final List<Check> checks = Diagnostics.dictionaryPaths(json, List.of());
        assertEquals(1, checks.size());
        assertEquals(Status.OK, checks.get(0).status(), checks.get(0).detail());
        assertEquals("1 path setting(s) in 1 index(es), 1 of them absolute into a directory that does not change with the engine version",
                checks.get(0).detail());
    }

    /**
     * The fess-opensearch image and the Compose files use /usr/share/opensearch/config/dictionary,
     * which every version of the image keeps, so an index created there is not a warning.
     */
    @Test
    public void test_dictionaryPaths_dockerPathIsOk() throws Exception {
        final String json = "{\"fess.20250101\":{\"settings\":{"
                + "\"index.analysis.char_filter.m.mappings_path\":\"/usr/share/opensearch/config/dictionary/ja/mapping.txt\","
                + "\"index.analysis.filter.s.synonyms_path\":\"/usr/share/opensearch/config/dictionary/synonym.txt\"}}}";
        final List<Check> checks = Diagnostics.dictionaryPaths(json, List.of());
        assertEquals(1, checks.size());
        assertEquals(Status.OK, checks.get(0).status(), checks.get(0).detail());
        assertTrue(checks.get(0).detail().contains("2 of them absolute"), checks.get(0).detail());
    }

    /**
     * The RPM/DEB packages keep the dictionaries under /etc/opensearch/dictionary (and
     * /var/lib/opensearch/config before that), which a package upgrade does not change.
     */
    @Test
    public void test_dictionaryPaths_packagePathIsOk() throws Exception {
        final String json = "{\"fess.20250101\":{\"settings\":{"
                + "\"index.analysis.char_filter.m.mappings_path\":\"/etc/opensearch/dictionary/ja/mapping.txt\"}},"
                + "\"fess_suggest_analyzer\":{\"settings\":{"
                + "\"index.analysis.filter.s.stopwords_path\":\"/var/lib/opensearch/config/stopwords.txt\"}}}";
        final List<Check> checks = Diagnostics.dictionaryPaths(json, List.of());
        assertEquals(1, checks.size());
        assertEquals(Status.OK, checks.get(0).status(), checks.get(0).detail());
    }

    /**
     * Even a path that is not expected to move fails once the engine refuses it.
     */
    @Test
    public void test_dictionaryPaths_unallocatableFixedPathStillFails() throws Exception {
        final String json = "{\"fess.20250101\":{\"settings\":{"
                + "\"index.analysis.filter.s.synonyms_path\":\"/usr/share/opensearch/config/dictionary/synonym.txt\"}}}";
        final List<Check> checks = Diagnostics.dictionaryPaths(json, List.of("fess.20250101"));
        assertEquals(1, checks.size());
        assertEquals(Status.FAIL, checks.get(0).status(), checks.get(0).detail());
    }

    @Test
    public void test_isVersionedEnginePath() {
        assertTrue(Diagnostics.isVersionedEnginePath("/opt/fess-15.9.0/opensearch/opensearch-3.9.0/config/dictionary/synonym.txt"));
        assertTrue(Diagnostics.isVersionedEnginePath("/opt/opensearch-2.19.1/config/dictionary/synonym.txt"));
        assertTrue(Diagnostics.isVersionedEnginePath("C:\\fess\\opensearch\\OpenSearch-3.9.0\\config\\dictionary\\synonym.txt"));
        assertFalse(Diagnostics.isVersionedEnginePath("/usr/share/opensearch/config/dictionary/synonym.txt"));
        assertFalse(Diagnostics.isVersionedEnginePath("/etc/opensearch/dictionary/synonym.txt"));
        assertFalse(Diagnostics.isVersionedEnginePath("/var/lib/opensearch/config/synonym.txt"));
        assertFalse(Diagnostics.isVersionedEnginePath("/opt/opensearch-data/config/dictionary/synonym.txt"));
        assertFalse(Diagnostics.isVersionedEnginePath("/opt/opensearch-/config/dictionary/synonym.txt"));
    }

    /**
     * Once the engine runs from another directory, the shards of such an index cannot be
     * allocated and the cluster is red: that is a failure, not a warning.
     */
    @Test
    public void test_dictionaryPaths_unallocatableFails() throws Exception {
        final String catShards = "configsync    \n" + "fess.20250101 failed shard on node [n1]: failed to create index, failure "
                + "IllegalArgumentException[Resource path must be inside config directory: "
                + "/opt/opensearch-3.9.0/config/dictionary/ja/mapping.txt]\n" + "relative_ctl  \n";
        final List<String> outside = Diagnostics.indicesOutsideConfig(catShards);
        assertEquals(List.of("fess.20250101"), outside);

        final List<Check> checks = Diagnostics.dictionaryPaths(ABSOLUTE_SETTINGS, outside);
        assertEquals(1, checks.size());
        assertEquals(Status.FAIL, checks.get(0).status(), checks.get(0).detail());
        assertTrue(checks.get(0).detail().startsWith("fess.20250101 cannot be allocated"), checks.get(0).detail());
    }

    @Test
    public void test_indicesOutsideConfig_ignoresOtherFailures() {
        assertEquals(List.of(), Diagnostics.indicesOutsideConfig(""));
        assertEquals(List.of(), Diagnostics.indicesOutsideConfig("relative_ctl failed shard on node [n1]: failed to create index, failure "
                + "IllegalArgumentException[IOException while reading mappings_path: file not readable]\n"));
    }

    @Test
    public void test_dictionaryPaths_rejectsWhatIsNotJson() {
        assertThrows(SetupException.class, () -> Diagnostics.dictionaryPaths("<html>", List.of()));
        assertThrows(SetupException.class, () -> Diagnostics.dictionaryPaths("{\"a\":\"unterminated}", List.of()));
        assertThrows(SetupException.class, () -> Diagnostics.dictionaryPaths("[]", List.of()));
        assertThrows(SetupException.class, () -> Diagnostics.dictionaryPaths("{} trailing", List.of()));
    }

    @Test
    public void test_jsonReader() throws Exception {
        assertEquals(List.of("a\"b", "c\\d", "e/f", "\u00e9", "1", "true"),
                JsonReader.parse(" [\"a\\\"b\", \"c\\\\d\", \"e\\/f\", \"\\u00e9\", 1, true] "));
        assertEquals(Map.of("k", List.of()), JsonReader.parse("{\"k\":[]}"));
    }

    @Test
    public void test_worst() {
        assertEquals(Status.OK, Diagnostics.worst(List.of(check(Status.OK), check(Status.OK))));
        assertEquals(Status.WARN, Diagnostics.worst(List.of(check(Status.OK), check(Status.WARN))));
        assertEquals(Status.FAIL, Diagnostics.worst(List.of(check(Status.WARN), check(Status.FAIL), check(Status.OK))));
        assertEquals(Status.OK, Diagnostics.worst(List.of()));
    }

    private static Check check(final Status status) {
        return new Check(status, "n", "d");
    }
}
