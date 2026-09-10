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

    @Test
    public void test_versionsFromMetadata() throws Exception {
        final List<String> versions = PluginRepository.versionsFromMetadata(METADATA);
        assertEquals(List.of("14.19.0", "15.7.0", "15.9.0", "15.9.1"), versions);
    }

    @Test
    public void test_selectVersion_picksTheHighestMatchingTheProductVersion() throws Exception {
        assertEquals("15.9.1", PluginRepository.selectVersion(PluginRepository.versionsFromMetadata(METADATA), "15.9"));
    }

    @Test
    public void test_selectVersion_reportsWhenNothingMatches() {
        final SetupException e =
                assertThrows(SetupException.class, () -> PluginRepository.selectVersion(List.of("14.19.0", "15.7.0"), "15.9"));
        assertTrue(e.getMessage().contains("15.9"), e.getMessage());
        assertTrue(e.getMessage().contains("14.19.0"), e.getMessage());
    }

    @Test
    public void test_selectVersion_ordersNumerically() throws Exception {
        assertEquals("15.9.10", PluginRepository.selectVersion(List.of("15.9.9", "15.9.10", "15.9.2"), "15.9"));
    }

    @Test
    public void test_jarUrl() {
        assertEquals("https://maven.codelibs.org/release/org/codelibs/fess/fess-ds-git/15.9.0/fess-ds-git-15.9.0.jar",
                PluginRepository.jarUrl("https://maven.codelibs.org/release/org/codelibs/fess/", "fess-ds-git", "15.9.0"));
    }

    @Test
    public void test_jarUrl_addsTheMissingSlash() {
        assertEquals("https://example.org/m2/fess-ds-git/15.9.0/fess-ds-git-15.9.0.jar",
                PluginRepository.jarUrl("https://example.org/m2", "fess-ds-git", "15.9.0"));
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
        final String html =
                "<a href=\"fess-crawler-playwright/\">x</a><a href=\"fess-crawler-lasta/\">x</a>" + "<a href=\"fess-ds-git/\">x</a>";
        assertEquals(List.of("fess-ds-git"), PluginRepository.namesFromListing(html));
    }
}
