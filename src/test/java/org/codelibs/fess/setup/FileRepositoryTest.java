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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Covers {@code --repository file:///...}: a copy of the Maven repository brought onto a machine
 * with no route to the Internet, laid out the way the real one is.
 */
public class FileRepositoryTest {

    private static final String METADATA = """
            <?xml version="1.0" encoding="UTF-8"?>
            <metadata>
              <groupId>org.codelibs.fess</groupId>
              <artifactId>fess-ds-git</artifactId>
              <versioning>
                <versions>
                  <version>15.8.0</version>
                  <version>15.9.0</version>
                  <version>15.9.1</version>
                </versions>
              </versioning>
            </metadata>
            """;

    private static final byte[] JAR = "fess-ds-git-jar".getBytes(StandardCharsets.UTF_8);

    @TempDir
    Path tempDir;

    private Path group;

    private Path plugins;

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    @BeforeEach
    public void createRepository() throws Exception {
        group = Files.createDirectories(tempDir.resolve("repository").resolve("org").resolve("codelibs").resolve("fess"));
        final Path artifact = Files.createDirectories(group.resolve("fess-ds-git"));
        Files.writeString(artifact.resolve("maven-metadata.xml"), METADATA);
        final Path jar = Files.write(Files.createDirectories(artifact.resolve("15.9.0")).resolve("fess-ds-git-15.9.0.jar"), JAR);
        Files.writeString(jar.resolveSibling("fess-ds-git-15.9.0.jar.sha1"), Downloader.sha1(jar));
        Files.createDirectories(group.resolve("fess-parent"));
        plugins = tempDir.resolve("plugin");
    }

    private String repositoryUrl() {
        return group.toUri().toString();
    }

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
    public void test_installPlugin_takesTheJarAndItsChecksumFromTheFileRepository() throws Exception {
        assertEquals(0, run("install", "plugin", "fess-ds-git:15.9.0", "--repository", repositoryUrl(), "--dest", plugins.toString()),
                err());

        assertEquals("fess-ds-git-jar", Files.readString(plugins.resolve("fess-ds-git-15.9.0.jar")));
        assertTrue(out().contains("from " + repositoryUrl()), out());
        assertFalse(out().contains("warning"), out());
        assertEquals("", err());
    }

    @Test
    public void test_installPlugin_refusesAJarThatDoesNotMatchItsChecksum() throws Exception {
        Files.writeString(group.resolve("fess-ds-git").resolve("15.9.0").resolve("fess-ds-git-15.9.0.jar.sha1"),
                "0000000000000000000000000000000000000000");

        assertEquals(1, run("install", "plugin", "fess-ds-git:15.9.0", "--repository", repositoryUrl(), "--dest", plugins.toString()));

        assertTrue(err().startsWith("error: Checksum mismatch"), err());
        assertFalse(Files.exists(plugins.resolve("fess-ds-git-15.9.0.jar")));
    }

    @Test
    public void test_installPlugin_reportsAMissingVersionOnOneLine() {
        assertEquals(1, run("install", "plugin", "fess-ds-git:15.9.9", "--repository", repositoryUrl(), "--dest", plugins.toString()));

        assertEquals(1, err().strip().lines().count(), err());
        assertTrue(err().startsWith("error: Failed to download " + repositoryUrl()), err());
        assertTrue(err().contains("no such file"), err());
    }

    @Test
    public void test_installPlugin_reportsAnUnsupportedSchemeOnOneLine() {
        assertEquals(1,
                run("install", "plugin", "fess-ds-git:15.9.0", "--repository", "ftp://mirror.example/fess/", "--dest", plugins.toString()));

        assertEquals(1, err().strip().lines().count(), err());
        assertTrue(err().startsWith("error: Cannot read ftp://mirror.example/fess/"), err());
    }

    @Test
    public void test_resolveVersion_readsTheMetadataFromTheFileRepository() throws Exception {
        final PluginSources sources = FessSetup.pluginSources(null, java.util.Map.of("repository", repositoryUrl()));
        assertEquals("15.9.1", FessSetup.resolveVersion(sources, "fess-ds-git", "15.9", false));
    }

    @Test
    public void test_listPlugins_readsTheFileRepositoryDirectory() {
        assertEquals(0, run("list", "plugins", "--repository", repositoryUrl(), "--dest", plugins.toString()), err());

        assertTrue(out().contains("  fess-ds-git"), out());
        assertFalse(out().contains("fess-parent"), out());
    }
}
