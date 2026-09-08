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

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class OpenSearchConfigurerTest {

    @TempDir
    Path tempDir;

    private Path prepareHome() throws Exception {
        final Path home = tempDir.resolve("opensearch-3.8.0");
        Files.createDirectories(home.resolve("config"));
        Files.writeString(home.resolve("config").resolve("opensearch.yml"), "cluster.name: fess\n");
        return home;
    }

    @Test
    public void test_configure_appendsConfigsyncPath() throws Exception {
        final Path home = prepareHome();
        OpenSearchConfigurer.configure(home);
        final String yml = Files.readString(home.resolve("config").resolve("opensearch.yml"));
        assertTrue(yml.contains("cluster.name: fess"), yml);
        assertTrue(yml.contains("configsync.config_path:"), yml);
        assertTrue(Files.isDirectory(home.resolve("config").resolve("dictionary")), "the dictionary directory must exist");
    }

    @Test
    public void test_configure_isIdempotent() throws Exception {
        final Path home = prepareHome();
        OpenSearchConfigurer.configure(home);
        OpenSearchConfigurer.configure(home);
        final String yml = Files.readString(home.resolve("config").resolve("opensearch.yml"));
        final long occurrences = yml.lines().filter(line -> line.trim().startsWith("configsync.config_path:")).count();
        assertEquals(1L, occurrences, yml);
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
}
