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
package org.codelibs.fess.theme;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.codelibs.fess.theme.StaticThemeInstaller.InstallException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StaticThemeInstallerDeleteTest {

    private Path tempDir;
    private StaticThemeInstaller installer;
    private ThemeRegistry registry;

    @BeforeEach
    public void setUp() throws Exception {
        tempDir = Files.createTempDirectory("admin-theme-delete-test-");
        installer = new StaticThemeInstaller();
        installer.setThemesDirOverride(tempDir);
        registry = new ThemeRegistry();
        registry.setThemesDirOverride(tempDir);
        installer.setThemeRegistry(registry);
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (tempDir == null || !Files.exists(tempDir)) {
            return;
        }
        try (Stream<Path> s = Files.walk(tempDir)) {
            s.sorted((a, b) -> b.compareTo(a)).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (final Exception ignore) {
                    // best effort cleanup
                }
            });
        }
    }

    @Test
    public void test_deleteRemovesTheStaticThemeDirectoryAndAtticizesIt() throws Exception {
        seedStaticTheme("mytheme");
        registry.reload();
        installer.setActiveDefaultProbe(() -> null);
        installer.delete("mytheme");
        assertFalse(Files.exists(tempDir.resolve("mytheme")));
        try (Stream<Path> s = Files.list(tempDir)) {
            assertTrue(s.anyMatch(p -> p.getFileName().toString().startsWith(".attic-mytheme-")));
        }
    }

    @Test
    public void test_deleteRejectsInvalidName() {
        installer.setActiveDefaultProbe(() -> null);
        assertThrows(InstallException.class, () -> installer.delete("../etc/passwd"));
        assertThrows(InstallException.class, () -> installer.delete(""));
        assertThrows(InstallException.class, () -> installer.delete(null));
        assertThrows(InstallException.class, () -> installer.delete(".staging-foo"));
    }

    @Test
    public void test_deleteRejectsWhenThemeIsActiveDefault() throws Exception {
        seedStaticTheme("mytheme");
        registry.reload();
        installer.setActiveDefaultProbe(() -> "mytheme");
        final InstallException ex = assertThrows(InstallException.class, () -> installer.delete("mytheme"));
        assertTrue(ex.getMessage().contains("active"));
        assertTrue(Files.exists(tempDir.resolve("mytheme")));
    }

    @Test
    public void test_deleteThrowsWhenDirectoryAlreadyAbsent() {
        installer.setActiveDefaultProbe(() -> null);
        final InstallException ex = assertThrows(InstallException.class, () -> installer.delete("never-installed"));
        assertTrue(ex.getMessage().toLowerCase().contains("not found"));
    }

    private void seedStaticTheme(final String name) throws Exception {
        final Path d = tempDir.resolve(name);
        Files.createDirectories(d);
        Files.writeString(d.resolve("theme.yml"), "apiVersion: fess.codelibs.org/v1\n" + "kind: StaticTheme\n" + "name: " + name + "\n"
                + "displayName: \"" + name + "\"\n" + "version: \"1.0.0\"\n");
        Files.writeString(d.resolve("index.html"), "<html></html>");
    }
}
