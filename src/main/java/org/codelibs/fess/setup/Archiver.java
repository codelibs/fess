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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Extracts a downloaded archive.
 *
 * <p>A {@code .tar.gz} goes to the platform {@code tar} command, which restores the POSIX
 * permissions and symbolic links the OpenSearch bundle relies on -- notably the executable bit
 * on {@code bin/opensearch} and the bundled JDK. A {@code .zip} is handled in Java, so Windows,
 * where OpenSearch publishes zip only, needs no external tool.</p>
 */
public final class Archiver {

    private Archiver() {
    }

    /**
     * Extracts {@code archive} into {@code destDir}, creating the directory when absent.
     *
     * @param archive the archive file
     * @param destDir the destination directory
     * @throws SetupException if the format is unsupported or extraction fails
     */
    public static void extract(final Path archive, final Path destDir) throws SetupException {
        final String name = archive.getFileName().toString().toLowerCase(Locale.ROOT);
        try {
            Files.createDirectories(destDir);
        } catch (final IOException e) {
            throw new SetupException("Failed to create " + destDir, e);
        }
        if (name.endsWith(".zip")) {
            extractZip(archive, destDir);
        } else if (name.endsWith(".tar.gz") || name.endsWith(".tgz")) {
            extractTar(archive, destDir);
        } else {
            throw new SetupException("Unsupported archive format: " + archive.getFileName());
        }
    }

    /**
     * Extracts a zip archive, rejecting entries that would land outside {@code destDir}.
     *
     * @param archive the zip file
     * @param destDir the destination directory
     * @throws SetupException if an entry escapes the destination or extraction fails
     */
    static void extractZip(final Path archive, final Path destDir) throws SetupException {
        final Path root = destDir.toAbsolutePath().normalize();
        try (InputStream in = Files.newInputStream(archive); ZipInputStream zis = new ZipInputStream(in)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                final Path target = resolveEntry(root, entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(target);
                } else {
                    final Path parent = target.getParent();
                    if (parent != null) {
                        Files.createDirectories(parent);
                    }
                    Files.copy(zis, target, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        } catch (final IOException e) {
            throw new SetupException("Failed to extract " + archive, e);
        }
    }

    private static Path resolveEntry(final Path root, final String entryName) throws SetupException {
        final Path target;
        try {
            target = root.resolve(entryName).normalize();
        } catch (final InvalidPathException e) {
            throw new SetupException("Archive entry has an invalid name: " + entryName, e);
        }
        if (!target.startsWith(root)) {
            throw new SetupException("Archive entry escapes the destination directory: " + entryName);
        }
        return target;
    }

    /**
     * Extracts a gzipped tar by delegating to the platform {@code tar} command.
     *
     * @param archive the tar.gz file
     * @param destDir the destination directory
     * @throws SetupException if tar is unavailable or reports an error
     */
    static void extractTar(final Path archive, final Path destDir) throws SetupException {
        final ProcessBuilder builder =
                new ProcessBuilder("tar", "-xzf", archive.toAbsolutePath().toString(), "-C", destDir.toAbsolutePath().toString());
        builder.redirectErrorStream(true);
        try {
            final Process process = builder.start();
            final String output = new String(process.getInputStream().readAllBytes());
            final int exit = process.waitFor();
            if (exit != 0) {
                throw new SetupException("tar exited with code " + exit + " while extracting " + archive + ": " + output);
            }
        } catch (final IOException e) {
            throw new SetupException("Failed to run tar for " + archive + ". Install tar, or extract the archive by hand.", e);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SetupException("Interrupted while extracting " + archive, e);
        }
    }
}
