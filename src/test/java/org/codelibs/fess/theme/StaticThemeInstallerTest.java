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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class StaticThemeInstallerTest extends UnitFessTestCase {

    @Test
    public void test_install_minimalZip() throws Exception {
        final Path themesDir = Files.createTempDirectory("themes-installer-");
        try {
            final StaticThemeInstaller installer = newInstaller(themesDir);
            final byte[] zip = buildValidZip("alpha");
            installer.installZip(new ByteArrayInputStream(zip));
            assertTrue(Files.exists(themesDir.resolve("alpha/theme.yml")));
            assertTrue(Files.exists(themesDir.resolve("alpha/index.html")));
        } finally {
            deleteRecursively(themesDir);
        }
    }

    @Test
    public void test_install_rejectsZipSlip() throws Exception {
        final Path themesDir = Files.createTempDirectory("themes-installer-");
        try {
            final StaticThemeInstaller installer = newInstaller(themesDir);
            final ByteArrayOutputStream bao = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(bao)) {
                putEntry(zos, "../evil.txt", "x".getBytes());
            }
            assertThrows(StaticThemeInstaller.InstallException.class,
                    () -> installer.installZip(new ByteArrayInputStream(bao.toByteArray())));
        } finally {
            deleteRecursively(themesDir);
        }
    }

    @Test
    public void test_install_rejectsTooManyEntries() throws Exception {
        final Path themesDir = Files.createTempDirectory("themes-installer-");
        try {
            final StaticThemeInstaller installer = newInstaller(themesDir);
            installer.setMaxEntries(2);
            final ByteArrayOutputStream bao = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(bao)) {
                for (int i = 0; i < 3; i++) {
                    putEntry(zos, "file" + i + ".txt", "x".getBytes());
                }
            }
            assertThrows(StaticThemeInstaller.InstallException.class,
                    () -> installer.installZip(new ByteArrayInputStream(bao.toByteArray())));
        } finally {
            deleteRecursively(themesDir);
        }
    }

    @Test
    public void test_install_rejectsMissingManifest() throws Exception {
        final Path themesDir = Files.createTempDirectory("themes-installer-");
        try {
            final StaticThemeInstaller installer = newInstaller(themesDir);
            final ByteArrayOutputStream bao = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(bao)) {
                putEntry(zos, "index.html", "<html/>".getBytes());
            }
            assertThrows(StaticThemeInstaller.InstallException.class,
                    () -> installer.installZip(new ByteArrayInputStream(bao.toByteArray())));
        } finally {
            deleteRecursively(themesDir);
        }
    }

    @Test
    public void test_install_atomicReplace() throws Exception {
        final Path themesDir = Files.createTempDirectory("themes-installer-");
        try {
            final StaticThemeInstaller installer = newInstaller(themesDir);
            installer.installZip(new ByteArrayInputStream(buildValidZip("alpha")));
            final String firstContent = Files.readString(themesDir.resolve("alpha/index.html"));
            installer.installZip(new ByteArrayInputStream(buildValidZipWithIndex("alpha", "<html>v2</html>")));
            final String secondContent = Files.readString(themesDir.resolve("alpha/index.html"));
            assertNotEquals(firstContent, secondContent);
            assertTrue(secondContent.contains("v2"));
        } finally {
            deleteRecursively(themesDir);
        }
    }

    private static StaticThemeInstaller newInstaller(final Path themesDir) {
        final StaticThemeInstaller installer = new StaticThemeInstaller();
        installer.setThemesDirOverride(themesDir);
        installer.setMaxEntries(100);
        installer.setMaxExtractedSize(1024L * 1024L);
        installer.setMaxCompressionRatio(100);
        return installer;
    }

    private byte[] buildValidZip(final String name) throws IOException {
        return buildValidZipWithIndex(name, "<html></html>");
    }

    private byte[] buildValidZipWithIndex(final String name, final String indexBody) throws IOException {
        final ByteArrayOutputStream bao = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(bao)) {
            final String yml = String.join("\n", "apiVersion: fess.codelibs.org/v1", "kind: StaticTheme", "name: " + name,
                    "displayName: \"" + name + "\"", "version: 1.0.0");
            putEntry(zos, "theme.yml", yml.getBytes(StandardCharsets.UTF_8));
            putEntry(zos, "index.html", indexBody.getBytes(StandardCharsets.UTF_8));
        }
        return bao.toByteArray();
    }

    private static void putEntry(final ZipOutputStream zos, final String name, final byte[] data) throws IOException {
        zos.putNextEntry(new ZipEntry(name));
        zos.write(data);
        zos.closeEntry();
    }

    private static void deleteRecursively(final Path p) throws Exception {
        if (!Files.exists(p)) {
            return;
        }
        Files.walk(p).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
            try {
                Files.delete(x);
            } catch (final Exception ignore) {}
        });
    }
}
