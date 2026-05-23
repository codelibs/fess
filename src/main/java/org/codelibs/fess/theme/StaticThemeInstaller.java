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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.lastaflute.web.util.LaServletContextUtil;

import jakarta.annotation.Resource;

/**
 * Installs static themes from uploaded ZIP archives.
 *
 * <p>The installer enforces security guards against ZipSlip, oversized
 * extractions, excessive entry counts, and compression-ratio bombs. Successful
 * extractions are promoted into the active themes directory via an atomic
 * directory rename so partial installs never become visible to readers.</p>
 *
 * <p>Lifecycle:</p>
 * <ol>
 *   <li>Stream the archive into a staging directory under
 *       {@code themes/.staging-&lt;uuid&gt;}.</li>
 *   <li>Validate the embedded {@code theme.yml} via {@link ThemeManifest}.</li>
 *   <li>If a theme with the same name exists, rename it to an {@code .attic-}
 *       backup so a failed promotion can roll back.</li>
 *   <li>Atomically rename the staging directory to {@code themes/&lt;name&gt;}
 *       and reload the {@link ThemeRegistry}.</li>
 * </ol>
 */
public class StaticThemeInstaller {

    private static final Logger logger = LogManager.getLogger(StaticThemeInstaller.class);

    @Resource
    protected FessConfig fessConfig;

    @Resource
    protected ThemeRegistry themeRegistry;

    private Path themesDirOverride;
    private long maxExtractedSize = 209_715_200L;
    private int maxEntries = 1000;
    private int maxCompressionRatio = 100;

    /** Pattern from spec §4.2: theme names must match this regex. */
    private static final Pattern NAME_RE = Pattern.compile("^[a-z0-9][a-z0-9_-]{0,63}$");

    /**
     * Denylist of path segments that must never appear in a ZIP entry name.
     * Note: only these exact segment names are blocked; segments merely starting
     * with '.' are now permitted (e.g. .well-known).
     */
    private static final Set<String> DENIED_SEGMENTS = Set.of(".git", ".svn", ".hg", "__MACOSX", ".DS_Store");

    /** Retention duration in days for attic dirs (used when fessConfig is absent). */
    private static final int DEFAULT_ATTIC_RETENTION_DAYS = 7;

    /** Staging dirs older than this (orphaned from JVM crashes) are cleaned up. */
    private static final long ORPHAN_STAGING_MAX_AGE_HOURS = 1L;

    /** Test seam: returns the currently active default theme name, or {@code null}. */
    private Supplier<String> activeDefaultProbe;

    /** Raised on any failure during ZIP validation or extraction. */
    public static class InstallException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        /** Structured error codes for callers that need to dispatch on the cause. */
        public enum Code {
            INVALID_NAME, ACTIVE_DEFAULT, JSP_TYPE, NOT_FOUND, EXTRACT_FAILED, MOVE_FAILED, MANIFEST_INVALID, SIZE_LIMIT, ENTRY_LIMIT, RATIO_LIMIT, OTHER
        }

        private final Code code;

        public InstallException(final String msg) {
            super(msg);
            this.code = Code.OTHER;
        }

        public InstallException(final String msg, final Throwable cause) {
            super(msg, cause);
            this.code = Code.OTHER;
        }

        public InstallException(final Code code, final String msg) {
            super(msg);
            this.code = code;
        }

        public InstallException(final Code code, final String msg, final Throwable cause) {
            super(msg, cause);
            this.code = code;
        }

        public Code code() {
            return code;
        }
    }

    /**
     * Installs a theme from the supplied ZIP stream.
     *
     * @param zipStream the archive content; the caller retains ownership of
     *        closing the underlying stream
     * @throws InstallException if validation, extraction, or promotion fails
     */
    public void installZip(final InputStream zipStream) {
        final Path themesDir = resolveThemesDir();
        try {
            Files.createDirectories(themesDir);
        } catch (final IOException e) {
            throw new InstallException(InstallException.Code.OTHER, "Failed to create themes dir " + themesDir, e);
        }
        final Path staging = themesDir.resolve(".staging-" + UUID.randomUUID());
        try {
            Files.createDirectory(staging);
            extract(zipStream, staging);
            final Path manifestPath = staging.resolve("theme.yml");
            if (!Files.isRegularFile(manifestPath)) {
                throw new InstallException(InstallException.Code.MANIFEST_INVALID, "theme.yml missing");
            }
            final ThemeManifest m;
            try (InputStream in = Files.newInputStream(manifestPath)) {
                try {
                    m = ThemeManifest.parse(in);
                } catch (final ThemeManifestException tme) {
                    throw new InstallException(InstallException.Code.MANIFEST_INVALID, "Invalid theme.yml: " + tme.getMessage(), tme);
                }
            }
            final Path target = themesDir.resolve(m.getName());
            Path attic = null;
            if (Files.exists(target)) {
                attic = themesDir.resolve(
                        ".attic-" + m.getName() + "-" + Instant.now().toEpochMilli() + "-" + UUID.randomUUID().toString().substring(0, 8));
                moveDir(target, attic);
            }
            try {
                moveDir(staging, target);
            } catch (final IOException moveErr) {
                if (attic != null) {
                    try {
                        moveDir(attic, target);
                    } catch (final IOException rollbackErr) {
                        moveErr.addSuppressed(rollbackErr);
                        logger.error("Failed to roll back attic to target after move failure; "
                                + "manual recovery required: attic={}, target={}", attic, target, rollbackErr);
                    }
                }
                throw new InstallException(InstallException.Code.MOVE_FAILED, "Failed to finalize install", moveErr);
            }
            if (attic != null && !deleteRecursivelyBestEffort(attic) && logger.isWarnEnabled()) {
                logger.warn(
                        "Failed to clean up previous attic directory after successful install; " + "manual cleanup recommended: attic={}",
                        attic);
            }
            if (themeRegistry != null) {
                themeRegistry.reload();
            }
            if (logger.isInfoEnabled()) {
                logger.info("Installed static theme name={} target={}", m.getName(), target);
            }
            cleanupOldAtticDirs(themesDir);
        } catch (final InstallException ex) {
            deleteRecursivelyQuiet(staging);
            throw ex;
        } catch (final Exception ex) {
            deleteRecursivelyQuiet(staging);
            throw new InstallException(InstallException.Code.OTHER, "Install failed: " + ex.getMessage(), ex);
        }
    }

    /**
     * Removes the static-theme directory atomically.
     *
     * <p>Refuses when (a) the name fails the spec §4.2 regex
     * ({@code ^[a-z0-9][a-z0-9_-]{0,63}$}), (b) the theme is the currently
     * active default (per the configured probe or
     * {@link ThemeRegistry#resolveActiveTheme(String)}), (c) the resolved
     * registry entry is a {@link ThemeType#JSP JSP} theme (deletion only
     * applies to static themes the installer owns), or (d) the directory does
     * not exist under the themes root.</p>
     *
     * <p>Successful deletion atomically renames the directory to
     * {@code themes/.attic-<name>-<timestamp>/} — preserving the §4.4 7-day
     * retention semantics — and reloads the {@link ThemeRegistry}.</p>
     *
     * @param name the theme directory name; must match
     *        {@code ^[a-z0-9][a-z0-9_-]{0,63}$}
     * @throws InstallException on validation, lookup, or rename failure
     */
    public void delete(final String name) {
        if (name == null || !NAME_RE.matcher(name).matches()) {
            throw new InstallException(InstallException.Code.INVALID_NAME, "Invalid theme name: " + name);
        }
        final String active = activeDefaultProbe != null ? activeDefaultProbe.get()
                : (themeRegistry == null ? null : themeRegistry.resolveActiveTheme(null).map(Theme::getName).orElse(null));
        if (name.equals(active)) {
            throw new InstallException(InstallException.Code.ACTIVE_DEFAULT, "Cannot delete active default theme: " + name);
        }
        if (themeRegistry != null) {
            final Theme t = themeRegistry.getAllThemes().get(name);
            if (t != null && t.getType() == ThemeType.JSP) {
                throw new InstallException(InstallException.Code.JSP_TYPE, "Refusing to delete JSP theme via static installer: " + name);
            }
        }
        final Path themesDir = resolveThemesDir();
        final Path target = themesDir.resolve(name);
        if (!Files.isDirectory(target)) {
            throw new InstallException(InstallException.Code.NOT_FOUND, "Theme not found: " + name);
        }
        final Path attic = themesDir
                .resolve(".attic-" + name + "-" + Instant.now().toEpochMilli() + "-" + UUID.randomUUID().toString().substring(0, 8));
        try {
            moveDir(target, attic);
        } catch (final IOException e) {
            throw new InstallException(InstallException.Code.MOVE_FAILED, "Failed to atticize theme " + name, e);
        }
        if (themeRegistry != null) {
            themeRegistry.reload();
        }
        if (logger.isInfoEnabled()) {
            logger.info("Deleted static theme name={} attic={}", name, attic);
        }
        cleanupOldAtticDirs(themesDir);
    }

    private void extract(final InputStream in, final Path target) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(in)) {
            ZipEntry entry;
            int entries = 0;
            long total = 0;
            final byte[] buffer = new byte[8192];
            while ((entry = zis.getNextEntry()) != null) {
                if (++entries > maxEntries) {
                    throw new InstallException(InstallException.Code.ENTRY_LIMIT, "Too many entries (>" + maxEntries + ")");
                }
                final String name = entry.getName();
                // Reject null bytes, absolute paths, and backslashes
                if (name.contains("\0") || name.startsWith("/") || name.startsWith("\\") || name.contains("\\")) {
                    throw new InstallException(InstallException.Code.EXTRACT_FAILED, "Unsafe entry name: " + name);
                }
                // Per-segment checks: reject '..' segments exactly, and denylist segments
                for (final String seg : name.split("/", -1)) {
                    if ("..".equals(seg)) {
                        throw new InstallException(InstallException.Code.EXTRACT_FAILED, "Path traversal blocked: " + name);
                    }
                    if (DENIED_SEGMENTS.contains(seg)) {
                        throw new InstallException(InstallException.Code.EXTRACT_FAILED, "Denied entry segment '" + seg + "' in: " + name);
                    }
                }
                final Path resolved = target.resolve(name).normalize();
                if (!resolved.startsWith(target)) {
                    throw new InstallException(InstallException.Code.EXTRACT_FAILED, "ZipSlip blocked: " + name);
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(resolved);
                    zis.closeEntry();
                    continue;
                }
                final Path parent = resolved.getParent();
                if (parent != null) {
                    Files.createDirectories(parent);
                }
                long entrySize = 0;
                try (OutputStream os = Files.newOutputStream(resolved)) {
                    int read;
                    while ((read = zis.read(buffer)) > 0) {
                        entrySize += read;
                        total += read;
                        if (total > maxExtractedSize) {
                            throw new InstallException(InstallException.Code.SIZE_LIMIT, "Extracted size exceeds limit");
                        }
                        os.write(buffer, 0, read);
                    }
                }
                final long compressed = entry.getCompressedSize();
                if (compressed > 0 && entrySize > 0) {
                    final long ratio = entrySize / Math.max(compressed, 1);
                    if (ratio > maxCompressionRatio) {
                        throw new InstallException(InstallException.Code.RATIO_LIMIT, "Compression ratio for " + name + " exceeds limit");
                    }
                }
                zis.closeEntry();
            }
        }
    }

    /**
     * Atomically moves {@code source} to {@code dest}. Falls back to a plain
     * replacing move on filesystems that do not support {@code ATOMIC_MOVE}.
     *
     * <p>Exposed as an instance method (rather than a {@code static} helper) so
     * unit tests can subclass and inject failure modes to exercise the
     * promotion/rollback paths in {@link #installZip(InputStream)}.</p>
     *
     * @param source path to move
     * @param dest destination path
     * @throws IOException if the move fails on the underlying filesystem
     */
    protected void moveDir(final Path source, final Path dest) throws IOException {
        try {
            Files.move(source, dest, StandardCopyOption.ATOMIC_MOVE);
        } catch (final java.nio.file.AtomicMoveNotSupportedException ex) {
            // Some filesystems (e.g. tmpfs across mount boundaries) don't
            // support ATOMIC_MOVE; fall back to a plain move.
            Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private Path resolveThemesDir() {
        if (themesDirOverride != null) {
            return themesDirOverride;
        }
        final String configured = fessConfig == null ? "themes" : fessConfig.getThemeDirectoryPath();
        final Path p = Paths.get(configured);
        if (p.isAbsolute()) {
            return p;
        }
        try {
            final String realPath = LaServletContextUtil.getServletContext().getRealPath("/" + configured);
            if (realPath != null) {
                return Paths.get(realPath);
            }
        } catch (final Exception ignore) {
            // servlet context not available (unit test)
        }
        return p;
    }

    private static void deleteRecursivelyQuiet(final Path p) {
        if (p == null || !Files.exists(p)) {
            return;
        }
        try {
            Files.walk(p).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {
                    // best effort cleanup
                }
            });
        } catch (final Exception ignore) {
            // best effort cleanup
        }
    }

    /**
     * Recursively deletes {@code p} on a best-effort basis, returning whether the
     * tree is fully gone afterwards. Used by the success path of {@link #installZip}
     * so the caller can log a WARN if the previous attic could not be cleaned.
     *
     * @param p path to remove; {@code null} or non-existent paths are treated as
     *        success ({@code true})
     * @return {@code true} if {@code p} no longer exists, {@code false} otherwise
     */
    private static boolean deleteRecursivelyBestEffort(final Path p) {
        if (p == null || !Files.exists(p)) {
            return true;
        }
        deleteRecursivelyQuiet(p);
        return !Files.exists(p);
    }

    /**
     * Scans the themes directory for old {@code .attic-*} and orphaned
     * {@code .staging-*} directories and removes them on a best-effort basis.
     *
     * <ul>
     *   <li>{@code .attic-*} older than the configured retention period (default
     *       {@value #DEFAULT_ATTIC_RETENTION_DAYS} days) are deleted.</li>
     *   <li>{@code .staging-*} older than {@value #ORPHAN_STAGING_MAX_AGE_HOURS}
     *       hour(s) are considered JVM-crash orphans and are deleted.</li>
     * </ul>
     *
     * @param themesDir the themes root directory to scan
     */
    private void cleanupOldAtticDirs(final Path themesDir) {
        if (themesDir == null || !Files.isDirectory(themesDir)) {
            return;
        }
        // TODO: extract to FessConfig — using DEFAULT_ATTIC_RETENTION_DAYS for now
        final int retentionDays = DEFAULT_ATTIC_RETENTION_DAYS;
        final Instant atticCutoff = Instant.now().minus(retentionDays, ChronoUnit.DAYS);
        final Instant stagingCutoff = Instant.now().minus(ORPHAN_STAGING_MAX_AGE_HOURS, ChronoUnit.HOURS);
        try (Stream<Path> entries = Files.list(themesDir)) {
            entries.filter(Files::isDirectory).forEach(p -> {
                final String fname = p.getFileName().toString();
                final boolean isAttic = fname.startsWith(".attic-");
                final boolean isStaging = fname.startsWith(".staging-");
                if (!isAttic && !isStaging) {
                    return;
                }
                final Instant cutoff = isAttic ? atticCutoff : stagingCutoff;
                try {
                    final FileTime mtime = Files.getLastModifiedTime(p);
                    if (mtime.toInstant().isBefore(cutoff)) {
                        if (!deleteRecursivelyBestEffort(p)) {
                            logger.warn("Failed to clean up old directory: path={}", p);
                        } else if (logger.isInfoEnabled()) {
                            logger.info("Cleaned up old directory: path={}", p);
                        }
                    }
                } catch (final IOException e) {
                    logger.warn("Failed to check mtime for cleanup: path={}", p, e);
                }
            });
        } catch (final Exception e) {
            logger.warn("Failed to scan themes dir for cleanup: themesDir={}", themesDir, e);
        }
    }

    // ---- Test seams ----
    void setThemesDirOverride(final Path p) {
        this.themesDirOverride = p;
    }

    void setMaxExtractedSize(final long v) {
        this.maxExtractedSize = v;
    }

    void setMaxEntries(final int v) {
        this.maxEntries = v;
    }

    void setMaxCompressionRatio(final int v) {
        this.maxCompressionRatio = v;
    }

    void setThemeRegistry(final ThemeRegistry r) {
        this.themeRegistry = r;
    }

    void setActiveDefaultProbe(final Supplier<String> probe) {
        this.activeDefaultProbe = probe;
    }
}
