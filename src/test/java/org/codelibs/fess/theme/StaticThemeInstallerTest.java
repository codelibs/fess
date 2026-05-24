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
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Assumptions;
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
    public void test_install_rejectsDenylistedDotfileEntries() throws Exception {
        // Files whose path starts with '.' (e.g. .env, .htaccess) must be rejected so a
        // crafted archive cannot smuggle hidden files into a theme directory.
        final Path themesDir = Files.createTempDirectory("themes-installer-");
        try {
            final StaticThemeInstaller installer = newInstaller(themesDir);
            final ByteArrayOutputStream bao = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(bao)) {
                // Use a denylisted dotfile (.DS_Store) — under the new policy, only the
                // denylist set (.git, .svn, .hg, __MACOSX, .DS_Store) is rejected; arbitrary
                // dotfiles like .well-known are allowed.
                final String yml = String.join("\n", "apiVersion: fess.codelibs.org/v1", "kind: StaticTheme", "name: dotfile",
                        "displayName: \"dotfile\"", "version: 1.0.0");
                putEntry(zos, "theme.yml", yml.getBytes(StandardCharsets.UTF_8));
                putEntry(zos, ".DS_Store", new byte[16]);
            }
            final StaticThemeInstaller.InstallException ex = assertThrows(StaticThemeInstaller.InstallException.class,
                    () -> installer.installZip(new ByteArrayInputStream(bao.toByteArray())));
            assertTrue(ex.getMessage().contains("Denied"));
            assertTrue(!Files.exists(themesDir.resolve("dotfile")));
        } finally {
            deleteRecursively(themesDir);
        }
    }

    @Test
    public void test_install_rejectsDenylistedDirectories() throws Exception {
        // Denylist policy: .git, .svn, .hg, __MACOSX, .DS_Store path segments are rejected
        // anywhere in the tree. Other dotfiles (e.g. .well-known) are allowed by design.
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
            assertTrue(ex.getMessage().contains("Denied"));
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
            installer.setZipRatioMax(50);
            installer.setZipRatioCheckThresholdBytes(65_536L);

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

    @Test
    public void test_init_fallsBackToDefaultsWhenFessConfigAbsent() throws Exception {
        // When fessConfig is null (unit test without DI), @PostConstruct must not throw
        // and the hardcoded defaults must be preserved.
        final Path themesDir = Files.createTempDirectory("themes-installer-nocfg-");
        try {
            final StaticThemeInstaller installer = new StaticThemeInstaller();
            installer.setThemesDirOverride(themesDir);
            // fessConfig deliberately NOT set (stays null)
            installer.init(); // must not throw
            // Verify defaults still allow a normal install.
            installer.installZip(new ByteArrayInputStream(buildValidZip("defaults")));
            assertTrue(Files.exists(themesDir.resolve("defaults/theme.yml")));
        } finally {
            deleteRecursively(themesDir);
        }
    }

    @Test
    public void test_init_sweepsOrphanStagingDirOnStartup() throws Exception {
        // Simulate a JVM-crash orphan: a .staging-* dir older than 1 hour.
        final Path themesDir = Files.createTempDirectory("themes-installer-sweep-");
        try {
            // Create an orphan staging directory with an old mtime.
            final Path orphan = themesDir.resolve(".staging-orphan-00000000-0000-0000-0000-000000000000");
            Files.createDirectory(orphan);
            // Set mtime to 2 hours ago so it is past the 1-hour sweep threshold.
            final java.nio.file.attribute.FileTime oldMtime =
                    java.nio.file.attribute.FileTime.from(java.time.Instant.now().minusSeconds(7200));
            Files.setLastModifiedTime(orphan, oldMtime);

            final StaticThemeInstaller installer = new StaticThemeInstaller();
            installer.setThemesDirOverride(themesDir);
            installer.init(); // @PostConstruct triggers cleanup sweep

            assertFalse(Files.exists(orphan), "Orphan staging dir should have been swept on init");
        } finally {
            deleteRecursively(themesDir);
        }
    }

    @Test
    public void test_init_doesNotSweepRecentStagingDir() throws Exception {
        // A staging dir younger than 1 hour must NOT be deleted.
        final Path themesDir = Files.createTempDirectory("themes-installer-nosweep-");
        try {
            final Path recent = themesDir.resolve(".staging-recent-00000000-0000-0000-0000-000000000001");
            Files.createDirectory(recent);
            // mtime is "now" (default) — well within the 1-hour window.

            final StaticThemeInstaller installer = new StaticThemeInstaller();
            installer.setThemesDirOverride(themesDir);
            installer.init();

            assertTrue(Files.exists(recent), "Recent staging dir must NOT be swept");
        } finally {
            deleteRecursively(themesDir);
        }
    }

    @Test
    public void test_install_resetsAtticMtimeSoRetentionStartsAtInstall() throws Exception {
        // C-4 regression guard: when a theme's source dir has an old mtime (e.g. installed
        // long ago, or restored from backup), Files.move preserves that mtime onto the
        // attic backup — which would then be culled instantly by cleanupOldAtticDirs.
        // After the move we must reset the attic's mtime to "now" and the same-call
        // cleanup must keep the attic intact.
        final Path themesDir = Files.createTempDirectory("themes-installer-mtime-");
        try {
            final StaticThemeInstaller installer = newInstaller(themesDir);
            // Install v1, then age its directory's mtime so the next install's attic
            // would inherit a stale mtime if we didn't reset it.
            installer.installZip(new ByteArrayInputStream(buildValidZipWithIndex("aging", "<html>v1</html>")));
            final Path themeDir = themesDir.resolve("aging");
            final Instant aged = Instant.now().minusSeconds(30L * 24L * 3600L); // 30 days ago
            Files.setLastModifiedTime(themeDir, FileTime.from(aged));

            final Instant beforeInstall = Instant.now();
            // Install v2 of the same name — this will atticize the v1 directory.
            installer.installZip(new ByteArrayInputStream(buildValidZipWithIndex("aging", "<html>v2</html>")));

            // Locate the resulting attic directory.
            final List<Path> attics;
            try (Stream<Path> s = Files.list(themesDir)) {
                attics = s.filter(p -> p.getFileName().toString().startsWith(".attic-aging-")).collect(Collectors.toList());
            }
            assertEquals("expected exactly one attic for aging theme", 1, attics.size());
            final Path attic = attics.get(0);

            // Attic mtime must be "now" (within 5 seconds), NOT the source's 30-day-old mtime.
            final Instant atticMtime = Files.getLastModifiedTime(attic).toInstant();
            final long secondsSinceInstall = Math.abs(atticMtime.getEpochSecond() - beforeInstall.getEpochSecond());
            assertTrue(secondsSinceInstall <= 5L,
                    "attic mtime should be within 5s of install moment but was " + secondsSinceInstall + "s away");

            // The cleanup that ran at the end of installZip must not have deleted the attic.
            assertTrue(Files.exists(attic), "attic must not be culled immediately after install");
        } finally {
            deleteRecursively(themesDir);
        }
    }

    @Test
    public void test_install_retainsPreviousAtticPerRetentionPolicy() throws Exception {
        // C-5 regression guard: after a successful replacement install, the attic of the
        // previous version must NOT be deleted eagerly. It is retained until the
        // configured retention window elapses, at which point cleanupOldAtticDirs ages
        // it out. We use a negative retention override to deterministically force the
        // cutoff into the future and exercise the cleanup path.
        final Path themesDir = Files.createTempDirectory("themes-installer-retain-");
        try {
            final StaticThemeInstaller installer = newInstaller(themesDir);
            installer.installZip(new ByteArrayInputStream(buildValidZipWithIndex("retained", "<html>v1</html>")));
            installer.installZip(new ByteArrayInputStream(buildValidZipWithIndex("retained", "<html>v2</html>")));

            // v2 must be present; the attic for v1 must still exist (retention not exceeded).
            assertTrue(Files.exists(themesDir.resolve("retained/theme.yml")));
            final List<Path> atticsAfterInstall;
            try (Stream<Path> s = Files.list(themesDir)) {
                atticsAfterInstall = s.filter(p -> p.getFileName().toString().startsWith(".attic-retained-")).collect(Collectors.toList());
            }
            assertEquals("v1 attic must be retained after successful v2 install", 1, atticsAfterInstall.size());
            final Path attic = atticsAfterInstall.get(0);
            assertTrue(Files.exists(attic));

            // Now collapse the retention window to the past so cleanup deletes the attic.
            installer.setAtticRetentionDaysOverride(Integer.valueOf(-1));
            installer.runCleanupOldAtticDirsForTest();
            assertFalse(Files.exists(attic), "attic must be deleted once retention window has elapsed");
        } finally {
            deleteRecursively(themesDir);
        }
    }

    @Test
    public void test_install_rejectsCumulativeZipBombRatio() throws Exception {
        // Multiple entries each with a modest individual ratio can combine into a
        // cumulative ratio that exceeds the incremental zip-bomb limit.
        final Path themesDir = Files.createTempDirectory("themes-installer-zipbomb-");
        try {
            final StaticThemeInstaller installer = newInstaller(themesDir);
            // Allow absolute extracted size and per-entry ratio to be high so only the
            // incremental cumulative ratio guard fires.
            installer.setMaxExtractedSize(512L * 1024L * 1024L);
            installer.setMaxCompressionRatio(Integer.MAX_VALUE);
            // Set a low threshold (1 byte) so ratio check triggers immediately after the
            // first entry, and a tight ratio (5:1) so 1 MB of zeros (which compresses
            // to ~1 KB) exceeds the limit.
            installer.setZipRatioMax(5);
            installer.setZipRatioCheckThresholdBytes(1L);

            final ByteArrayOutputStream bao = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(bao)) {
                final String yml = String.join("\n", "apiVersion: fess.codelibs.org/v1", "kind: StaticTheme", "name: bombtheme",
                        "displayName: \"bombtheme\"", "version: 1.0.0");
                putEntry(zos, "theme.yml", yml.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                // 1 MB of zero bytes compresses extremely well — cumulative ratio >> 5.
                putEntry(zos, "bomb.bin", new byte[1024 * 1024]);
            }

            final StaticThemeInstaller.InstallException ex = assertThrows(StaticThemeInstaller.InstallException.class,
                    () -> installer.installZip(new ByteArrayInputStream(bao.toByteArray())));
            assertEquals(StaticThemeInstaller.InstallException.Code.ZIP_BOMB_RATIO, ex.code());
            assertFalse(Files.exists(themesDir.resolve("bombtheme")));
        } finally {
            deleteRecursively(themesDir);
        }
    }

    @Test
    public void test_install_allowsNormalZipBelowRatioThreshold() throws Exception {
        // A ZIP whose cumulative compressed bytes never exceeds the check threshold must
        // never be rejected by the incremental ratio guard — even if the ratio would
        // otherwise be high (FP suppression for tiny archives).
        final Path themesDir = Files.createTempDirectory("themes-installer-zipbomb-fp-");
        try {
            final StaticThemeInstaller installer = newInstaller(themesDir);
            // Strict ratio limit, but threshold is very high so check never activates.
            installer.setZipRatioMax(1);
            installer.setZipRatioCheckThresholdBytes(Long.MAX_VALUE);
            // Also relax per-entry ratio so that guard does not fire either.
            installer.setMaxCompressionRatio(Integer.MAX_VALUE);

            final byte[] zip = buildValidZip("safetheme");
            // Must succeed without exception.
            installer.installZip(new ByteArrayInputStream(zip));
            assertTrue(Files.exists(themesDir.resolve("safetheme/theme.yml")));
        } finally {
            deleteRecursively(themesDir);
        }
    }

    @Test
    public void test_install_allowsLargeZipWithLowRatio() throws Exception {
        // A ZIP that exceeds the check threshold but whose cumulative ratio is within
        // the allowed limit must install successfully (no false positive).
        final Path themesDir = Files.createTempDirectory("themes-installer-zipbomb-ok-");
        try {
            final StaticThemeInstaller installer = newInstaller(themesDir);
            // Allow large size cap, generous ratio (200:1), threshold 1 byte so check
            // activates immediately.
            installer.setMaxExtractedSize(512L * 1024L * 1024L);
            installer.setMaxCompressionRatio(Integer.MAX_VALUE);
            installer.setZipRatioMax(5);
            installer.setZipRatioCheckThresholdBytes(1L);

            final ByteArrayOutputStream bao = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(bao)) {
                final String yml = String.join("\n", "apiVersion: fess.codelibs.org/v1", "kind: StaticTheme", "name: oktheme",
                        "displayName: \"oktheme\"", "version: 1.0.0");
                putEntry(zos, "theme.yml", yml.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                // 1 MB of pseudo-random bytes barely compresses (ratio ~ 1.0:1 << 5:1).
                final byte[] payload = new byte[1024 * 1024];
                new java.util.Random(42L).nextBytes(payload);
                putEntry(zos, "data.bin", payload);
            }
            // Must succeed without exception.
            installer.installZip(new ByteArrayInputStream(bao.toByteArray()));
            assertTrue(Files.exists(themesDir.resolve("oktheme/theme.yml")));
        } finally {
            deleteRecursively(themesDir);
        }
    }

    // ── A-3: symlink rejection ────────────────────────────────────────────────────

    @Test
    public void test_install_rejectsWhenTargetIsSymlink() throws Exception {
        // A-3: if themes/<name> is a symbolic link the installer must refuse rather than
        // following the link and potentially moving content outside the themes directory.
        Assumptions.assumeTrue(!System.getProperty("os.name", "").toLowerCase().contains("win"), "Symlink test skipped on Windows");
        final Path themesDir = Files.createTempDirectory("themes-installer-symlink-");
        final Path linkTarget = Files.createTempDirectory("themes-symlink-target-");
        try {
            // Create themes/alpha as a symlink pointing to linkTarget.
            final Path symlinkPath = themesDir.resolve("alpha");
            try {
                Files.createSymbolicLink(symlinkPath, linkTarget);
            } catch (final UnsupportedOperationException e) {
                Assumptions.abort("Filesystem does not support symbolic links; skipping test");
                return;
            }
            final StaticThemeInstaller installer = newInstaller(themesDir);
            final StaticThemeInstaller.InstallException ex = assertThrows(StaticThemeInstaller.InstallException.class,
                    () -> installer.installZip(new ByteArrayInputStream(buildValidZip("alpha"))));
            assertTrue(ex.getMessage().contains("symbolic link") || ex.getMessage().contains("symlink"),
                    "install must refuse with a symlink-related message: " + ex.getMessage());
            // linkTarget must not have been touched.
            assertTrue(Files.exists(linkTarget), "symlink target must not be deleted by the installer");
        } finally {
            deleteRecursively(linkTarget);
            deleteRecursively(themesDir);
        }
    }

    @Test
    public void test_delete_rejectsWhenTargetIsSymlink() throws Exception {
        // A-3: if themes/<name> is a symbolic link the delete method must refuse rather
        // than atomically renaming the link target to an attic directory.
        Assumptions.assumeTrue(!System.getProperty("os.name", "").toLowerCase().contains("win"), "Symlink test skipped on Windows");
        final Path themesDir = Files.createTempDirectory("themes-installer-del-symlink-");
        final Path linkTarget = Files.createTempDirectory("themes-del-symlink-target-");
        try {
            final Path symlinkPath = themesDir.resolve("alpha");
            try {
                Files.createSymbolicLink(symlinkPath, linkTarget);
            } catch (final UnsupportedOperationException e) {
                Assumptions.abort("Filesystem does not support symbolic links; skipping test");
                return;
            }
            final StaticThemeInstaller installer = newInstaller(themesDir);
            final StaticThemeInstaller.InstallException ex =
                    assertThrows(StaticThemeInstaller.InstallException.class, () -> installer.delete("alpha"));
            assertTrue(ex.getMessage().contains("symbolic link") || ex.getMessage().contains("symlink"),
                    "delete must refuse with a symlink-related message: " + ex.getMessage());
            // linkTarget must still exist (not moved to an attic).
            assertTrue(Files.exists(linkTarget), "symlink target must not be moved by the delete operation");
        } finally {
            deleteRecursively(linkTarget);
            deleteRecursively(themesDir);
        }
    }

    private static StaticThemeInstaller newInstaller(final Path themesDir) {
        final StaticThemeInstaller installer = new StaticThemeInstaller();
        installer.setThemesDirOverride(themesDir);
        installer.setMaxEntries(100);
        installer.setMaxExtractedSize(1024L * 1024L);
        installer.setMaxCompressionRatio(100);
        // Production defaults for incremental zip-bomb ratio check.
        installer.setZipRatioMax(50);
        installer.setZipRatioCheckThresholdBytes(65_536L);
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
