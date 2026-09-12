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

import java.util.List;

import org.junit.jupiter.api.Test;

public class PluginRepositoryTest {

    private static final String METADATA = """
            <?xml version="1.0" encoding="UTF-8"?>
            <metadata>
              <groupId>org.codelibs.fess</groupId>
              <artifactId>fess-ds-git</artifactId>
              <versioning>
                <latest>15.8.0</latest>
                <release>15.8.0</release>
                <versions>
                  <version>14.19.0</version>
                  <version>15.7.0</version>
                  <version>15.9.0</version>
                  <version>15.9.1</version>
                </versions>
              </versioning>
            </metadata>
            """;

    private static final String SNAPSHOT_METADATA = """
            <?xml version="1.0" encoding="UTF-8"?>
            <metadata modelVersion="1.1.0">
              <groupId>org.codelibs.fess</groupId>
              <artifactId>fess-ds-git</artifactId>
              <versioning>
                <lastUpdated>20260903015513</lastUpdated>
                <snapshot>
                  <timestamp>20260903.015513</timestamp>
                  <buildNumber>6</buildNumber>
                </snapshot>
                <snapshotVersions>
                  <snapshotVersion>
                    <extension>jar</extension>
                    <value>15.9.0-20260903.015513-6</value>
                  </snapshotVersion>
                </snapshotVersions>
              </versioning>
            </metadata>
            """;

    @Test
    public void test_versionsFromMetadata() throws Exception {
        final List<String> versions = PluginRepository.versionsFromMetadata(METADATA);
        assertEquals(List.of("14.19.0", "15.7.0", "15.9.0", "15.9.1"), versions);
    }

    @Test
    public void test_selectVersion_picksTheHighestMatchingTheProductVersion() throws Exception {
        assertEquals("15.9.1", PluginRepository.selectVersion(PluginRepository.versionsFromMetadata(METADATA), "15.9", false));
    }

    @Test
    public void test_selectVersion_reportsWhenNothingMatches() {
        final SetupException e =
                assertThrows(SetupException.class, () -> PluginRepository.selectVersion(List.of("14.19.0", "15.7.0"), "15.9", false));
        assertTrue(e.getMessage().contains("15.9"), e.getMessage());
        assertTrue(e.getMessage().contains("14.19.0"), e.getMessage());
    }

    @Test
    public void test_selectVersion_ordersNumerically() throws Exception {
        assertEquals("15.9.10", PluginRepository.selectVersion(List.of("15.9.9", "15.9.10", "15.9.2"), "15.9", false));
    }

    @Test
    public void test_jarUrl() {
        assertEquals("https://maven.codelibs.org/release/org/codelibs/fess/fess-ds-git/15.9.0/fess-ds-git-15.9.0.jar",
                PluginRepository.jarUrl("https://maven.codelibs.org/release/org/codelibs/fess/", "fess-ds-git", "15.9.0", "15.9.0"));
    }

    @Test
    public void test_jarUrl_addsTheMissingSlash() {
        assertEquals("https://example.org/m2/fess-ds-git/15.9.0/fess-ds-git-15.9.0.jar",
                PluginRepository.jarUrl("https://example.org/m2", "fess-ds-git", "15.9.0", "15.9.0"));
    }

    @Test
    public void test_metadataUrl() {
        assertEquals("https://example.org/m2/fess-ds-git/maven-metadata.xml",
                PluginRepository.metadataUrl("https://example.org/m2", "fess-ds-git"));
    }

    @Test
    public void test_namesFromListing() {
        final String html = """
                <html><body>
                <a href="../">../</a>
                <a href="fess-ds-git/">fess-ds-git/</a>
                <a href="fess-ds-slack/">fess-ds-slack/</a>
                <a href="fess-script-groovy/">fess-script-groovy/</a>
                <a href="fess-theme-classic/">fess-theme-classic/</a>
                <a href="fess-crawler-playwright/">fess-crawler-playwright/</a>
                <a href="fess/">fess/</a>
                </body></html>
                """;
        final List<String> names = PluginRepository.namesFromListing(html);
        assertTrue(names.contains("fess-ds-git"), names.toString());
        assertTrue(names.contains("fess-script-groovy"), names.toString());
        assertTrue(names.contains("fess-theme-classic"), names.toString());
        assertTrue(names.contains("fess-crawler-playwright"), names.toString());
    }

    @Test
    public void test_namesFromListing_includesStoragePlugins() {
        final String html = "<a href=\"fess-storage-gcs/\">x</a><a href=\"fess-storage-s3/\">x</a><a href=\"fess-parent/\">x</a>";
        assertEquals(List.of("fess-storage-gcs", "fess-storage-s3"), PluginRepository.namesFromListing(html));
    }

    @Test
    public void test_namesFromListing_skipsNonPluginArtifacts() {
        final String html = "<a href=\"fess/\">fess/</a><a href=\"fess-parent/\">fess-parent/</a><a href=\"fess-ds-git/\">x</a>";
        final List<String> names = PluginRepository.namesFromListing(html);
        assertEquals(List.of("fess-ds-git"), names);
    }

    @Test
    public void test_namesFromListing_excludesCrawlerInternals() {
        // PluginHelper hides these from the admin list; fess-setup must not offer them either.
        // fess-crawler-playwright is not one of them: it is a plugin, and has to stay offerable.
        final String html = "<a href=\"fess-crawler-opensearch/\">x</a><a href=\"fess-crawler-lasta/\">x</a>"
                + "<a href=\"fess-crawler-playwright/\">x</a>" + "<a href=\"fess-ds-git/\">x</a>";
        assertEquals(List.of("fess-crawler-playwright", "fess-ds-git"), PluginRepository.namesFromListing(html));
    }

    @Test
    public void test_isSnapshot() throws Exception {
        assertTrue(PluginRepository.isSnapshot("15.9.0-SNAPSHOT"));
        assertFalse(PluginRepository.isSnapshot("15.9.0"));
        assertFalse(PluginRepository.isSnapshot(""));
    }

    @Test
    public void test_selectVersion_prefersASnapshotOnASnapshotBuild() throws Exception {
        final List<String> versions = List.of("15.9.0-SNAPSHOT", "15.9.0", "15.8.0");
        assertEquals("15.9.0-SNAPSHOT", PluginRepository.selectVersion(versions, "15.9", true));
    }

    @Test
    public void test_selectVersion_takesTheHighestSnapshotOnASnapshotBuild() throws Exception {
        final List<String> versions = List.of("15.9.0-SNAPSHOT", "15.9.2-SNAPSHOT", "15.9.10-SNAPSHOT");
        assertEquals("15.9.10-SNAPSHOT", PluginRepository.selectVersion(versions, "15.9", true));
    }

    @Test
    public void test_selectVersion_fallsBackToAReleaseWhenNoSnapshotIsPublished() throws Exception {
        final List<String> versions = List.of("15.9.0", "15.9.1");
        assertEquals("15.9.1", PluginRepository.selectVersion(versions, "15.9", true));
    }

    @Test
    public void test_selectVersion_ignoresSnapshotsOnAReleaseBuild() throws Exception {
        final List<String> versions = List.of("15.9.0", "15.9.1-SNAPSHOT");
        assertEquals("15.9.0", PluginRepository.selectVersion(versions, "15.9", false));
    }

    @Test
    public void test_selectVersion_refusesASnapshotOnAReleaseBuild() throws Exception {
        final List<String> versions = List.of("15.9.1-SNAPSHOT");
        final SetupException e = assertThrows(SetupException.class, () -> PluginRepository.selectVersion(versions, "15.9", false));
        assertTrue(e.getMessage().contains("15.9.1-SNAPSHOT"), e.getMessage());
    }

    @Test
    public void test_snapshotFileVersion() throws Exception {
        assertEquals("15.9.0-20260903.015513-6", PluginRepository.snapshotFileVersion(SNAPSHOT_METADATA, "15.9.0-SNAPSHOT"));
    }

    @Test
    public void test_snapshotFileVersion_reportsMetadataWithoutASnapshotBuild() throws Exception {
        final SetupException e =
                assertThrows(SetupException.class, () -> PluginRepository.snapshotFileVersion(METADATA, "15.9.0-SNAPSHOT"));
        assertTrue(e.getMessage().contains("15.9.0-SNAPSHOT"));
    }

    @Test
    public void test_versionMetadataUrl() throws Exception {
        assertEquals("https://example.org/m2/fess-ds-git/15.9.0-SNAPSHOT/maven-metadata.xml",
                PluginRepository.versionMetadataUrl("https://example.org/m2", "fess-ds-git", "15.9.0-SNAPSHOT"));
    }

    @Test
    public void test_jarUrl_namesTheTimestampedFileInsideTheSnapshotDirectory() throws Exception {
        assertEquals(
                "https://maven.codelibs.org/snapshot/org/codelibs/fess/fess-ds-git/15.9.0-SNAPSHOT/"
                        + "fess-ds-git-15.9.0-20260903.015513-6.jar",
                PluginRepository.jarUrl("https://maven.codelibs.org/snapshot/org/codelibs/fess/", "fess-ds-git", "15.9.0-SNAPSHOT",
                        "15.9.0-20260903.015513-6"));
    }

    @Test
    public void test_githubJarUrl() throws Exception {
        assertEquals("https://github.com/codelibs/fess-ds-git/releases/download/fess-ds-git-15.9.0/fess-ds-git-15.9.0.jar",
                PluginRepository.githubJarUrl("https://github.com/codelibs", "fess-ds-git", "15.9.0"));
    }

    @Test
    public void test_githubJarUrl_addsTheMissingSlash() throws Exception {
        assertEquals("https://github.com/codelibs/fess-ds-git/releases/download/fess-ds-git-15.9.0/fess-ds-git-15.9.0.jar",
                PluginRepository.githubJarUrl("https://github.com/codelibs/", "fess-ds-git", "15.9.0"));
    }

    @Test
    public void test_checksumUrl() throws Exception {
        assertEquals("https://example.org/m2/fess-ds-git/15.9.0/fess-ds-git-15.9.0.jar.sha1",
                PluginRepository.checksumUrl("https://example.org/m2/fess-ds-git/15.9.0/fess-ds-git-15.9.0.jar"));
    }

    @Test
    public void test_mergeNames_sortsAndDropsDuplicates() throws Exception {
        assertEquals(List.of("fess-ds-git", "fess-ds-slack", "fess-script-groovy"),
                PluginRepository.mergeNames(List.of("fess-ds-slack", "fess-ds-git"), List.of("fess-ds-git", "fess-script-groovy")));
    }

    private static final PluginSources SOURCES = new PluginSources("https://maven.codelibs.org/release/org/codelibs/fess/",
            "https://maven.codelibs.org/snapshot/org/codelibs/fess/", "https://github.com/codelibs");

    @Test
    public void test_jarUrls_triesGithubBeforeTheRepositoryForARelease() throws Exception {
        assertEquals(
                List.of("https://github.com/codelibs/fess-ds-git/releases/download/fess-ds-git-15.9.0/fess-ds-git-15.9.0.jar",
                        "https://maven.codelibs.org/release/org/codelibs/fess/fess-ds-git/15.9.0/fess-ds-git-15.9.0.jar"),
                PluginRepository.jarUrls(SOURCES, "fess-ds-git", "15.9.0", "15.9.0"));
    }

    @Test
    public void test_jarUrls_usesOnlyTheSnapshotRepositoryForASnapshot() throws Exception {
        assertEquals(
                List.of("https://maven.codelibs.org/snapshot/org/codelibs/fess/fess-ds-git/15.9.0-SNAPSHOT/"
                        + "fess-ds-git-15.9.0-20260903.015513-6.jar"),
                PluginRepository.jarUrls(SOURCES, "fess-ds-git", "15.9.0-SNAPSHOT", "15.9.0-20260903.015513-6"));
    }

    @Test
    public void test_jarUrls_skipsGithubWhenTheDefinitionNamesNone() throws Exception {
        final PluginSources sources = new PluginSources("https://example.org/m2", "https://example.org/snapshot", "  ");
        assertEquals(List.of("https://example.org/m2/fess-ds-git/15.9.0/fess-ds-git-15.9.0.jar"),
                PluginRepository.jarUrls(sources, "fess-ds-git", "15.9.0", "15.9.0"));
    }

    @Test
    public void test_repositoryJarUrl_choosesTheRepositoryThatMatchesTheVersion() throws Exception {
        assertEquals("https://maven.codelibs.org/release/org/codelibs/fess/fess-ds-git/15.9.0/fess-ds-git-15.9.0.jar",
                PluginRepository.repositoryJarUrl(SOURCES, "fess-ds-git", "15.9.0", "15.9.0"));
        assertEquals(
                "https://maven.codelibs.org/snapshot/org/codelibs/fess/fess-ds-git/15.9.0-SNAPSHOT/"
                        + "fess-ds-git-15.9.0-20260903.015513-6.jar",
                PluginRepository.repositoryJarUrl(SOURCES, "fess-ds-git", "15.9.0-SNAPSHOT", "15.9.0-20260903.015513-6"));
    }

    @Test
    public void test_repositoryJarUrl_reportsASnapshotRepositoryTheDefinitionLeftEmpty() throws Exception {
        final PluginSources sources = new PluginSources("https://example.org/m2", "", "https://github.com/codelibs");
        final SetupException e = assertThrows(SetupException.class,
                () -> PluginRepository.repositoryJarUrl(sources, "fess-ds-git", "15.9.0-SNAPSHOT", "15.9.0-20260903.015513-6"));
        assertTrue(e.getMessage().contains("snapshot.repository"), e.getMessage());
    }

    @Test
    public void test_repositoryJarUrl_reportsAReleaseRepositoryTheDefinitionLeftEmpty() throws Exception {
        final PluginSources sources = new PluginSources(null, "https://example.org/snapshot", "https://github.com/codelibs");
        final SetupException e =
                assertThrows(SetupException.class, () -> PluginRepository.repositoryJarUrl(sources, "fess-ds-git", "15.9.0", "15.9.0"));
        assertTrue(e.getMessage().contains("plugin.repository"), e.getMessage());
    }
}
