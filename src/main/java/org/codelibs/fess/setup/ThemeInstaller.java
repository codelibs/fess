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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Installs static themes into the webapp's themes directory.
 *
 * <p>A theme is published as a ZIP whose root holds {@code theme.yml}, so unlike a plugin it is
 * extracted rather than dropped in place, and the directory it is extracted into has to be named
 * for the theme: {@code ThemeRegistry} skips a directory whose name disagrees with its
 * manifest.</p>
 *
 * <p>The {@code .staging-} and {@code .attic-} prefixes are the ones the admin screen's installer
 * uses. They are not a private convention: {@code ThemeRegistry} skips every dot-directory when
 * it scans, and Fess sweeps stale ones of both kinds at startup, so a theme replaced from the
 * command line is kept for the same retention period as one replaced from the admin screen, and
 * an interrupted run leaves nothing Fess will not clean up.</p>
 */
public final class ThemeInstaller {

    /** The manifest every static theme carries at the root of its archive. */
    static final String MANIFEST = "theme.yml";

    private static final String STAGING_PREFIX = ".staging-";

    private static final String ATTIC_PREFIX = ".attic-";

    /** The file naming every published theme, one per line, beside the themes themselves. */
    static final String INDEX_NAME = "theme-index.txt";

    /**
     * What a theme name may be, from {@code ThemeManifest.NAME_PATTERN} in Fess. Every line of
     * the index is checked against it, which is also what tells a real index from a proxy error
     * page that happens to answer 200.
     */
    private static final Pattern NAME = Pattern.compile("[a-z0-9][a-z0-9_-]{0,63}");

    private ThemeInstaller() {
    }

    /**
     * Returns the directory Fess loads static themes from.
     *
     * @param fessHome the Fess installation directory
     * @return the themes directory
     */
    public static Path directory(final String fessHome) {
        return Path.of(fessHome, "app", "themes");
    }

    /**
     * Returns the file name a theme archive is published under.
     *
     * @param name the theme name
     * @param version the version the file name carries
     * @return the archive file name
     */
    public static String zipName(final String name, final String version) {
        return name + "-" + version + ".zip";
    }

    /**
     * Downloads a theme, checks it against the published checksum and installs it.
     *
     * <p>Everything happens inside a staging directory next to the destination, so the download,
     * the extraction and the manifest check all take place before anything installed is touched:
     * a theme that is already there survives a failure at any of those steps. The download and
     * its checksum go through the plugin installer, which is not jar-specific -- it fetches a
     * file and verifies the {@code .sha1} the Maven repository publishes beside it.</p>
     *
     * @param repository the group directory URL themes are published under
     * @param name the theme name
     * @param version the version, which names the directory in the repository
     * @param fileVersion the version the file name carries
     * @param themesDir the themes directory
     * @param out the stream for warnings and progress
     * @param progress receives download progress
     * @return the URL the archive came from
     * @throws SetupException if the download, the checksum, the archive or the manifest is bad
     */
    public static String install(final String repository, final String name, final String version, final String fileVersion,
            final Path themesDir, final PrintStream out, final Downloader.Progress progress) throws SetupException {
        createDirectory(themesDir);
        final Path target = themesDir.resolve(name);
        // A symlink here would redirect the move below onto whatever it points at, outside the
        // themes directory. The admin screen's installer refuses the same thing.
        if (Files.isSymbolicLink(target)) {
            throw new SetupException("Refusing to install over a symbolic link: " + target);
        }
        final Path staging = themesDir.resolve(STAGING_PREFIX + UUID.randomUUID());
        try {
            createDirectory(staging);
            final String url = PluginRepository.artifactUrl(repository, name, version, fileVersion, "zip");
            final Path archive = staging.resolve(zipName(name, fileVersion));
            final String used = FessPluginInstaller.install(List.of(url), PluginRepository.checksumUrl(url), archive, out, progress);
            final Path content = staging.resolve("content");
            createDirectory(content);
            Archiver.extractZip(archive, content);
            verifyManifest(content, name);
            promote(themesDir, name, content, target);
            return used;
        } finally {
            deleteRecursivelyQuiet(staging);
        }
    }

    /**
     * Checks that what was extracted is the theme that was asked for.
     *
     * @param content the extracted archive
     * @param name the theme name
     * @throws SetupException if the manifest is missing or names a different theme
     */
    private static void verifyManifest(final Path content, final String name) throws SetupException {
        final Path manifest = content.resolve(MANIFEST);
        if (!Files.isRegularFile(manifest)) {
            throw new SetupException("The archive has no " + MANIFEST + " at its root, so it is not a static theme.");
        }
        final String declared = readField(manifest, "name");
        if (!name.equals(declared)) {
            // Installing it anyway would put it in a directory ThemeRegistry then skips, so the
            // theme would download, extract, and never appear.
            throw new SetupException("The archive is not the " + name + " theme: its " + MANIFEST + " names '" + declared + "'.");
        }
    }

    /**
     * Moves an installed theme aside and puts the new one in its place.
     *
     * @param themesDir the themes directory
     * @param name the theme name
     * @param content the extracted theme
     * @param target where the theme belongs
     * @throws SetupException if either move fails
     */
    private static void promote(final Path themesDir, final String name, final Path content, final Path target) throws SetupException {
        if (Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
            atticize(themesDir, name, target);
        }
        try {
            Files.move(content, target);
        } catch (final IOException e) {
            throw new SetupException("Failed to install " + target, e);
        }
    }

    /**
     * Moves a theme into the attic, where Fess keeps it for the retention period.
     *
     * @param themesDir the themes directory
     * @param name the theme name
     * @param source the installed theme
     * @return the attic directory
     * @throws SetupException if the move fails
     */
    static Path atticize(final Path themesDir, final String name, final Path source) throws SetupException {
        final Path attic = themesDir
                .resolve(ATTIC_PREFIX + name + "-" + Instant.now().toEpochMilli() + "-" + UUID.randomUUID().toString().substring(0, 8));
        try {
            Files.move(source, attic);
        } catch (final IOException e) {
            throw new SetupException("Failed to move " + source + " aside", e);
        }
        try {
            // Files.move keeps the source's timestamp, so a theme installed months ago would
            // arrive in the attic already older than the retention period and be swept on the
            // next start. The window has to begin now.
            Files.setLastModifiedTime(attic, FileTime.from(Instant.now()));
        } catch (final IOException e) {
            // Best effort. Losing the backup early is not a reason to fail an install that has
            // otherwise succeeded.
        }
        return attic;
    }

    /**
     * Moves an installed theme into the attic.
     *
     * @param themesDir the themes directory
     * @param name the theme name
     * @return the attic directory, or {@code null} when the theme is not installed
     * @throws SetupException if the theme is a symbolic link or the move fails
     */
    public static Path remove(final Path themesDir, final String name) throws SetupException {
        final Path target = themesDir.resolve(name);
        if (Files.isSymbolicLink(target)) {
            throw new SetupException("Refusing to remove a symbolic link: " + target);
        }
        if (!Files.isDirectory(target)) {
            return null;
        }
        return atticize(themesDir, name, target);
    }

    /**
     * Returns the themes installed in a directory, in name order.
     *
     * <p>A directory counts as a theme when it carries a manifest, which is the same test
     * {@code ThemeRegistry} applies, so a half-extracted directory left by something else is not
     * reported as installed. Dot-directories are the staging and attic ones.</p>
     *
     * @param themesDir the themes directory
     * @return the theme names
     * @throws SetupException if the directory cannot be read
     */
    public static List<String> installed(final Path themesDir) throws SetupException {
        if (!Files.isDirectory(themesDir)) {
            return List.of();
        }
        try (Stream<Path> entries = Files.list(themesDir)) {
            return entries.filter(p -> Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS))
                    .filter(p -> !p.getFileName().toString().startsWith("."))
                    .filter(p -> Files.isRegularFile(p.resolve(MANIFEST)))
                    .map(p -> p.getFileName().toString())
                    .sorted()
                    .toList();
        } catch (final IOException e) {
            throw new SetupException("Failed to read " + themesDir, e);
        }
    }

    /**
     * Returns the version an installed theme declares.
     *
     * @param themesDir the themes directory
     * @param name the theme name
     * @return the version, or {@code null} when the theme or the field is absent
     * @throws SetupException if the manifest cannot be read
     */
    public static String installedVersion(final Path themesDir, final String name) throws SetupException {
        final Path manifest = themesDir.resolve(name).resolve(MANIFEST);
        if (!Files.isRegularFile(manifest)) {
            return null;
        }
        return readField(manifest, "version");
    }

    /**
     * Reads one top-level scalar from a theme manifest.
     *
     * <p>Deliberately not a YAML parser. {@code fess-setup} runs with the JDK as its whole
     * classpath, and the two fields read here -- the name and the version -- are plain scalars
     * at the top level of a file Fess itself validates properly once it loads the theme.</p>
     *
     * @param manifest the manifest file
     * @param key the field name
     * @return the value, or {@code null} when the field is absent or empty
     * @throws SetupException if the file cannot be read
     */
    static String readField(final Path manifest, final String key) throws SetupException {
        final String prefix = key + ":";
        try (BufferedReader reader = Files.newBufferedReader(manifest, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                // No trim first, on purpose: an indented line of the same name belongs to a
                // nested block and is not the top-level field being asked for.
                if (!line.startsWith(prefix)) {
                    continue;
                }
                final String value = unquote(line.substring(prefix.length()).trim());
                return value.isEmpty() ? null : value;
            }
            return null;
        } catch (final IOException e) {
            throw new SetupException("Failed to read " + manifest, e);
        }
    }

    private static String unquote(final String value) {
        if (value.length() >= 2 && (value.charAt(0) == '"' && value.endsWith("\"") || value.charAt(0) == '\'' && value.endsWith("'"))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    /**
     * Returns the URL of the file naming the published themes.
     *
     * @param repository the group directory URL themes are published under
     * @return the index URL
     */
    public static String indexUrl(final String repository) {
        return PluginRepository.base(repository) + INDEX_NAME;
    }

    /**
     * Extracts the theme names from the published index.
     *
     * <p>Not from a directory listing: the server generates those on a schedule, so the themes
     * tree answers 403 until it has, and the listing for the parent group is already behind. A
     * catalogue built on it reports that no themes exist.</p>
     *
     * <p>Every line has to be a theme name. That is what tells an index from a proxy page or an
     * error document that answers 200, which would otherwise be read as a list of themes.</p>
     *
     * @param text the index
     * @return the theme names, in file order, without duplicates
     * @throws SetupException if any line is not a theme name
     */
    public static List<String> namesFromIndex(final String text) throws SetupException {
        final Set<String> names = new LinkedHashSet<>();
        for (final String raw : text.split("\n")) {
            final String name = raw.strip();
            if (name.isEmpty()) {
                continue;
            }
            if (!NAME.matcher(name).matches()) {
                throw new SetupException("Not a theme index: it has a line that is not a theme name.");
            }
            names.add(name);
        }
        return new ArrayList<>(names);
    }

    private static void createDirectory(final Path directory) throws SetupException {
        try {
            Files.createDirectories(directory);
        } catch (final IOException e) {
            throw new SetupException("Failed to create " + directory, e);
        }
    }

    private static void deleteRecursivelyQuiet(final Path path) {
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (final IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (final IOException | UncheckedIOException e) {
            // A leftover staging directory is swept by Fess at startup, and reporting it here
            // would replace the failure that is actually worth reading.
        }
    }
}
