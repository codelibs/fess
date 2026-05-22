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

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
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

    @Test
    public void test_install_rejectsExceedingMaxExtractedSize() throws Exception {
        final Path themesDir = Files.createTempDirectory("themes-installer-");
        try {
            final StaticThemeInstaller installer = newInstaller(themesDir);
            // Set a small cap so we can exceed it without huge payloads.
            installer.setMaxExtractedSize(4096L);
            // Use a high allowed compression ratio so this test is isolated to size, not ratio.
            installer.setMaxCompressionRatio(Integer.MAX_VALUE);
            final ByteArrayOutputStream bao = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(bao)) {
                // a minimal manifest first so an early entry doesn't trip ratio
                final String yml = String.join("\n", "apiVersion: fess.codelibs.org/v1", "kind: StaticTheme", "name: bigtheme",
                        "displayName: \"bigtheme\"", "version: 1.0.0");
                putEntry(zos, "theme.yml", yml.getBytes(StandardCharsets.UTF_8));
                // a 16KB random-ish payload — well over the 4KB extracted-size cap
                final byte[] payload = new byte[16 * 1024];
                for (int i = 0; i < payload.length; i++) {
                    payload[i] = (byte) (i & 0xFF);
                }
                putEntry(zos, "big.bin", payload);
            }
            assertThrows(StaticThemeInstaller.InstallException.class,
                    () -> installer.installZip(new ByteArrayInputStream(bao.toByteArray())));
            // No theme directory should have been promoted under themes dir.
            assertTrue(!Files.exists(themesDir.resolve("bigtheme")));
        } finally {
            deleteRecursively(themesDir);
        }
    }

    @Test
    public void test_install_rejectsExceedingCompressionRatio() throws Exception {
        final Path themesDir = Files.createTempDirectory("themes-installer-");
        try {
            final StaticThemeInstaller installer = newInstaller(themesDir);
            // Allow the absolute extracted size; constrain only the ratio.
            installer.setMaxExtractedSize(64L * 1024L * 1024L);
            installer.setMaxCompressionRatio(10);
            final ByteArrayOutputStream bao = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(bao)) {
                final String yml = String.join("\n", "apiVersion: fess.codelibs.org/v1", "kind: StaticTheme", "name: ratiotheme",
                        "displayName: \"ratiotheme\"", "version: 1.0.0");
                putEntry(zos, "theme.yml", yml.getBytes(StandardCharsets.UTF_8));
                // 1MB of zero bytes will compress extremely well — well over a 10:1 ratio.
                final byte[] payload = new byte[1024 * 1024];
                putEntry(zos, "bomb.bin", payload);
            }
            assertThrows(StaticThemeInstaller.InstallException.class,
                    () -> installer.installZip(new ByteArrayInputStream(bao.toByteArray())));
            assertTrue(!Files.exists(themesDir.resolve("ratiotheme")));
        } finally {
            deleteRecursively(themesDir);
        }
    }

    @Test
    public void test_install_corruptZipDoesNotCorruptPreviousVersion() throws Exception {
        final Path themesDir = Files.createTempDirectory("themes-installer-");
        try {
            final StaticThemeInstaller installer = newInstaller(themesDir);
            // Install a clean v1 first.
            installer.installZip(new ByteArrayInputStream(buildValidZipWithIndex("rollbackme", "<html>v1</html>")));
            final Path themeDir = themesDir.resolve("rollbackme");
            assertTrue(Files.exists(themeDir.resolve("theme.yml")));
            final String manifestBefore = Files.readString(themeDir.resolve("theme.yml"));
            assertTrue(manifestBefore.contains("version: 1.0.0"));

            // Attempt to "install" a ZIP missing its manifest — installer must abort cleanly
            // without overwriting the previous directory. This exercises the same staging
            // failure path that a corrupt archive would, but in a deterministic way.
            final ByteArrayOutputStream noManifest = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(noManifest)) {
                putEntry(zos, "index.html", "<html>v2</html>".getBytes(StandardCharsets.UTF_8));
            }
            assertThrows(StaticThemeInstaller.InstallException.class,
                    () -> installer.installZip(new ByteArrayInputStream(noManifest.toByteArray())));

            // The previous v1 directory and manifest must still be intact.
            assertTrue(Files.exists(themeDir.resolve("theme.yml")));
            final String manifestAfter = Files.readString(themeDir.resolve("theme.yml"));
            assertEquals(manifestBefore, manifestAfter);
        } finally {
            deleteRecursively(themesDir);
        }
    }

    @Test
    public void test_install_rejectsDotfileEntries() throws Exception {
        // Files whose path starts with '.' (e.g. .env, .htaccess) must be rejected so a
        // crafted archive cannot smuggle hidden files into a theme directory.
        final Path themesDir = Files.createTempDirectory("themes-installer-");
        try {
            final StaticThemeInstaller installer = newInstaller(themesDir);
            final ByteArrayOutputStream bao = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(bao)) {
                final String yml = String.join("\n", "apiVersion: fess.codelibs.org/v1", "kind: StaticTheme", "name: dotfile",
                        "displayName: \"dotfile\"", "version: 1.0.0");
                putEntry(zos, "theme.yml", yml.getBytes(StandardCharsets.UTF_8));
                putEntry(zos, ".env", "SECRET=1".getBytes(StandardCharsets.UTF_8));
            }
            final StaticThemeInstaller.InstallException ex = assertThrows(StaticThemeInstaller.InstallException.class,
                    () -> installer.installZip(new ByteArrayInputStream(bao.toByteArray())));
            assertTrue(ex.getMessage().contains("Hidden"));
            assertTrue(!Files.exists(themesDir.resolve("dotfile")));
        } finally {
            deleteRecursively(themesDir);
        }
    }

    @Test
    public void test_install_rejectsDotPrefixedDirectories() throws Exception {
        // Any path segment that begins with '.' is rejected — guards against archives
        // that embed a hidden directory such as .git/HEAD anywhere in the tree.
        final Path themesDir = Files.createTempDirectory("themes-installer-");
        try {
            final StaticThemeInstaller installer = newInstaller(themesDir);
            final ByteArrayOutputStream bao = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(bao)) {
                final String yml = String.join("\n", "apiVersion: fess.codelibs.org/v1", "kind: StaticTheme", "name: dotdir",
                        "displayName: \"dotdir\"", "version: 1.0.0");
                putEntry(zos, "theme.yml", yml.getBytes(StandardCharsets.UTF_8));
                putEntry(zos, ".git/HEAD", "ref: refs/heads/main".getBytes(StandardCharsets.UTF_8));
            }
            final StaticThemeInstaller.InstallException ex = assertThrows(StaticThemeInstaller.InstallException.class,
                    () -> installer.installZip(new ByteArrayInputStream(bao.toByteArray())));
            assertTrue(ex.getMessage().contains("Hidden"));
            assertTrue(!Files.exists(themesDir.resolve("dotdir")));
        } finally {
            deleteRecursively(themesDir);
        }
    }

    @Test
    public void test_install_chainsRollbackExceptionAsSuppressed() throws Exception {
        // Force BOTH the staging->target move and the attic->target rollback to fail,
        // and assert the rollback IOException is chained onto the original failure
        // via addSuppressed so operators don't lose the rollback diagnostic.
        final Path themesDir = Files.createTempDirectory("themes-installer-");
        try {
            // Pre-install v1 so a target directory exists; the next install path will
            // exercise the attic-create + promote sequence we want to break.
            final StaticThemeInstaller seed = newInstaller(themesDir);
            seed.installZip(new ByteArrayInputStream(buildValidZipWithIndex("rollback", "<html>v1</html>")));
            assertTrue(Files.exists(themesDir.resolve("rollback/theme.yml")));

            final AtomicInteger calls = new AtomicInteger();
            final StaticThemeInstaller installer = new StaticThemeInstaller() {
                @Override
                protected void moveDir(final Path source, final Path dest) throws IOException {
                    final int n = calls.incrementAndGet();
                    // 1st call: target -> attic (must succeed so attic != null).
                    // 2nd call: staging -> target (force the initial move failure).
                    // 3rd call: attic -> target (force the rollback failure).
                    if (n == 1) {
                        super.moveDir(source, dest);
                        return;
                    }
                    throw new IOException("forced failure #" + n);
                }
            };
            installer.setThemesDirOverride(themesDir);
            installer.setMaxEntries(100);
            installer.setMaxExtractedSize(1024L * 1024L);
            installer.setMaxCompressionRatio(100);

            final StaticThemeInstaller.InstallException ex = assertThrows(StaticThemeInstaller.InstallException.class,
                    () -> installer.installZip(new ByteArrayInputStream(buildValidZipWithIndex("rollback", "<html>v2</html>"))));
            final Throwable cause = ex.getCause();
            assertNotNull(cause);
            assertTrue(cause.getSuppressed().length >= 1, "expected rollback IOException to be chained via addSuppressed");
            assertTrue(cause.getSuppressed()[0] instanceof IOException);
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
