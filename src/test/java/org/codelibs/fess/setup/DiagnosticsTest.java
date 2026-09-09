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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.codelibs.fess.setup.Diagnostics.Check;
import org.codelibs.fess.setup.Diagnostics.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DiagnosticsTest {

    @TempDir
    Path tempDir;

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
        Files.writeString(tempDir.resolve("fess-ds-git-15.9.0.jar"), "x");
        Files.writeString(tempDir.resolve("fess-ds-git-15.9.1.jar"), "x");

        final List<Check> checks = Diagnostics.installedPlugins(tempDir, "15.9");
        assertEquals(1, checks.size());
        assertEquals(Status.FAIL, checks.get(0).status());
        assertTrue(checks.get(0).detail().contains("15.9.0"), checks.get(0).detail());
        assertTrue(checks.get(0).detail().contains("15.9.1"), checks.get(0).detail());
    }

    @Test
    public void test_installedPlugins_warnsWhenAPluginIsForAnotherRelease() throws Exception {
        Files.writeString(tempDir.resolve("fess-ds-git-15.8.0.jar"), "x");
        Files.writeString(tempDir.resolve("fess-ds-slack-15.9.0.jar"), "x");

        final List<Check> checks = Diagnostics.installedPlugins(tempDir, "15.9");
        assertEquals(2, checks.size());
        final Check git = checks.stream().filter(c -> c.name().contains("fess-ds-git")).findFirst().orElseThrow();
        assertEquals(Status.WARN, git.status());
        assertTrue(git.detail().contains("15.8.0"), git.detail());
        final Check slack = checks.stream().filter(c -> c.name().contains("fess-ds-slack")).findFirst().orElseThrow();
        assertEquals(Status.OK, slack.status());
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
