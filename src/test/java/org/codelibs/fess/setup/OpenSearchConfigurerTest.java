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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class OpenSearchConfigurerTest {

    @TempDir
    Path tempDir;

    private Path prepareHome() throws Exception {
        return prepareHome("cluster.name: fess\n");
    }

    private Path prepareHome(final String yml) throws Exception {
        final Path home = tempDir.resolve("opensearch-3.8.0");
        Files.createDirectories(home.resolve("config"));
        Files.writeString(home.resolve("config").resolve("opensearch.yml"), yml);
        return home;
    }

    /** An installation the way install opensearch leaves it, under {@code <fessHome>/opensearch}. */
    private static Path install(final Path fessHome, final String dir) throws Exception {
        final Path home = fessHome.resolve("opensearch").resolve(dir);
        Files.createDirectories(home.resolve("config"));
        Files.writeString(home.resolve("config").resolve("opensearch.yml"), "cluster.name: fess\n");
        OpenSearchConfigurer.configure(home);
        return home;
    }

    private static String yml(final Path home) throws Exception {
        return Files.readString(home.resolve("config").resolve("opensearch.yml"));
    }

    private static long settingLines(final String yml, final String key) {
        return yml.lines().filter(line -> line.trim().startsWith(key)).count();
    }

    @Test
    public void test_configure_appendsConfigsyncPath() throws Exception {
        final Path home = prepareHome();
        OpenSearchConfigurer.configure(home);
        final String yml = yml(home);
        assertTrue(yml.contains("cluster.name: fess"), yml);
        assertTrue(yml.contains("configsync.config_path: " + OpenSearchConfigurer.dictionaryPathValue(home)), yml);
        assertTrue(Files.isDirectory(home.resolve("config").resolve("dictionary")), "the dictionary directory must exist");
    }

    @Test
    public void test_configure_disablesSecurityPlugin() throws Exception {
        final Path home = prepareHome();
        OpenSearchConfigurer.configure(home);
        final String yml = yml(home);
        // The OpenSearch bundle ships opensearch-security enabled, and without TLS material it
        // refuses to start: "No SSL configuration found".
        assertEquals(1L, yml.lines().filter(line -> line.trim().equals("plugins.security.disabled: true")).count(), yml);
    }

    @Test
    public void test_configure_returnsWhatItAppended() throws Exception {
        final Path home = prepareHome();
        assertEquals(
                List.of("configsync.config_path: " + OpenSearchConfigurer.dictionaryPathValue(home), "plugins.security.disabled: true"),
                OpenSearchConfigurer.configure(home));
        assertEquals(List.of(), OpenSearchConfigurer.configure(home));
    }

    @Test
    public void test_configure_isIdempotent() throws Exception {
        final Path home = prepareHome();
        OpenSearchConfigurer.configure(home);
        final String first = yml(home);
        OpenSearchConfigurer.configure(home);
        final String yml = yml(home);
        assertEquals(first, yml);
        assertEquals(1L, settingLines(yml, "configsync.config_path:"), yml);
        assertEquals(1L, settingLines(yml, "plugins.security.disabled:"), yml);
    }

    @Test
    public void test_configure_keepsExplicitSecurityDisabledSetting() throws Exception {
        final Path home = prepareHome("cluster.name: fess\nplugins.security.disabled: false\n");
        final List<String> added = OpenSearchConfigurer.configure(home);
        final String yml = yml(home);
        assertEquals(1L, settingLines(yml, "plugins.security.disabled:"), yml);
        assertTrue(yml.contains("plugins.security.disabled: false"), yml);
        assertEquals(List.of("configsync.config_path: " + OpenSearchConfigurer.dictionaryPathValue(home)), added);
    }

    @Test
    public void test_configure_leavesConfiguredSecurityPluginAlone() throws Exception {
        final Path home = prepareHome("cluster.name: fess\nplugins.security.ssl.http.enabled: true\n");
        OpenSearchConfigurer.configure(home);
        final String yml = yml(home);
        assertEquals(0L, settingLines(yml, "plugins.security.disabled"), yml);
        assertTrue(yml.contains("plugins.security.ssl.http.enabled: true"), yml);
    }

    @Test
    public void test_configure_ignoresCommentedOutSecuritySetting() throws Exception {
        final Path home = prepareHome("cluster.name: fess\n#plugins.security.disabled: false\n");
        OpenSearchConfigurer.configure(home);
        final String yml = yml(home);
        assertEquals(1L, yml.lines().filter(line -> line.equals("plugins.security.disabled: true")).count(), yml);
    }

    @Test
    public void test_configure_completesAnInstallationConfiguredWithoutSecuritySetting() throws Exception {
        // What bin/fess-setup install opensearch left behind before it disabled the security plugin:
        // running it again has to add the missing setting without repeating configsync.config_path.
        final Path home = prepareHome("cluster.name: fess\nconfigsync.config_path: /somewhere/else\n");
        final List<String> added = OpenSearchConfigurer.configure(home);
        final String yml = yml(home);
        assertEquals(1L, settingLines(yml, "configsync.config_path:"), yml);
        assertTrue(yml.contains("configsync.config_path: /somewhere/else"), yml);
        assertEquals(1L, settingLines(yml, "plugins.security.disabled: true"), yml);
        assertEquals(List.of("plugins.security.disabled: true"), added);
    }

    @Test
    public void test_configure_reportsMissingYml() throws Exception {
        final Path home = tempDir.resolve("not-opensearch");
        Files.createDirectories(home);
        final SetupException e = assertThrows(SetupException.class, () -> OpenSearchConfigurer.configure(home));
        assertTrue(e.getMessage().contains("opensearch.yml"), e.getMessage());
    }

    @Test
    public void test_dictionaryPath_isWhatFessMustBeToldAbout() throws Exception {
        final Path home = prepareHome();
        assertEquals(home.resolve("config").resolve("dictionary"), OpenSearchConfigurer.dictionaryPath(home));
    }

    @Test
    public void test_dictionaryPathValue_isAbsoluteWithForwardSlashes() throws Exception {
        final Path home = prepareHome();
        final String value = OpenSearchConfigurer.dictionaryPathValue(home);
        assertEquals(home.resolve("config").resolve("dictionary").toAbsolutePath().toString().replace('\\', '/'), value);
        assertFalse(value.contains("\\"), value);
    }

    @Test
    public void test_isFoundByLauncher_theOnlyInstallationUnderFessHome() throws Exception {
        final Path fessHome = tempDir.resolve("fess");
        final Path home = install(fessHome, "opensearch-3.8.0");
        // the archive install opensearch downloaded stays next to it
        Files.writeString(fessHome.resolve("opensearch").resolve("opensearch-3.8.0-linux-x64.tar.gz"), "");
        // a directory that was never configured is not a candidate
        Files.createDirectories(fessHome.resolve("opensearch").resolve("opensearch-3.7.0").resolve("config"));
        assertTrue(OpenSearchConfigurer.isFoundByLauncher(fessHome, home));
    }

    @Test
    public void test_isFoundByLauncher_notOutsideFessHome() throws Exception {
        final Path fessHome = tempDir.resolve("fess");
        Files.createDirectories(fessHome);
        final Path home = install(tempDir.resolve("elsewhere"), "opensearch-3.8.0");
        assertFalse(OpenSearchConfigurer.isFoundByLauncher(fessHome, home));
    }

    @Test
    public void test_isFoundByLauncher_notWhenTwoInstallationsCompete() throws Exception {
        final Path fessHome = tempDir.resolve("fess");
        final Path older = install(fessHome, "opensearch-3.8.0");
        final Path newer = install(fessHome, "opensearch-3.9.0");
        assertFalse(OpenSearchConfigurer.isFoundByLauncher(fessHome, older));
        assertFalse(OpenSearchConfigurer.isFoundByLauncher(fessHome, newer));
    }
}
