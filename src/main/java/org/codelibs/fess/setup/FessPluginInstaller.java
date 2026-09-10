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
import java.io.PrintStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Installs Fess plugins as jars under the webapp's plugin directory.
 *
 * <p>Nothing unpacks or registers them: Fess scans {@code WEB-INF/plugin} at context start and
 * the admin UI installs them by copying the jar there, so this does the same thing from the
 * command line. Unlike an OpenSearch plugin there is no installer tool to delegate to, and
 * unlike the other components in {@code fess-setup.properties} there is no archive to extract.</p>
 *
 * <p>The jars are deliberately not inspected. A plugin that contributes classes to the webapp
 * carries a {@code Fess-WebAppJar} manifest attribute, but a data store, a theme, an ingester and
 * a thumbnail generator do not, so requiring the attribute would reject most of the catalogue.</p>
 */
public final class FessPluginInstaller {

    /** The prefixes {@code PluginHelper.ArtifactType} recognises, which is what Fess can load. */
    private static final String[] PLUGIN_PREFIXES = { "fess-ds", "fess-theme", "fess-ingest", "fess-script", "fess-webapp",
            "fess-thumbnail", "fess-crawler", "fess-llm", "fess-storage", "fess-sso" };

    private static final String JAR = ".jar";

    /** Suffix of the file a download is written to until its checksum has been checked. */
    private static final String DOWNLOAD = ".download";

    private FessPluginInstaller() {
    }

    /**
     * Downloads a plugin jar and checks it against the checksum the repository publishes.
     *
     * <p>The URLs are tried in order and the first one that has the file wins, so that a release
     * comes from its GitHub asset and falls back to the Maven repository. Only the last URL is
     * allowed to fail the install: a source that answers "no such file" is skipped silently,
     * because until every plugin publishes its jars on GitHub that is the normal case, and one
     * that fails for any other reason is reported before the next is tried.</p>
     *
     * <p>The checksum is always the Maven repository's, GitHub publishing none. A jar that does
     * not match it is deleted rather than installed; a checksum that cannot be read leaves the
     * jar in place with a warning, because a repository that has stopped serving {@code .sha1}
     * is not a reason to make {@code install plugin} unusable.</p>
     *
     * @param urls the candidate jar URLs, most preferred first
     * @param checksumUrl the URL of the jar's {@code .sha1}
     * @param jar the file to write
     * @param out the stream for warnings
     * @param progress receives download progress
     * @return the URL the jar came from
     * @throws SetupException if no source has the jar or the checksum does not match
     */
    public static String install(final List<String> urls, final String checksumUrl, final Path jar, final PrintStream out,
            final Downloader.Progress progress) throws SetupException {
        final Path download = jar.resolveSibling(jar.getFileName() + DOWNLOAD);
        try {
            final String used = fetch(urls, download, out, progress);
            // The progress line is written with a carriage return and no newline, so anything
            // printed from here on would land on top of it.
            out.println();
            verify(download, jar.getFileName().toString(), checksumUrl, out);
            move(download, jar);
            return used;
        } finally {
            discard(download);
            discard(download.resolveSibling(download.getFileName() + ".part"));
        }
    }

    private static void move(final Path download, final Path jar) throws SetupException {
        try {
            Files.move(download, jar, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            throw new SetupException("Failed to install " + jar, e);
        }
    }

    private static void discard(final Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (final IOException e) {
            // Nothing useful can be done about a leftover temporary file, and reporting it here
            // would replace the failure that is actually worth reading.
        }
    }

    private static String fetch(final List<String> urls, final Path jar, final PrintStream out, final Downloader.Progress progress)
            throws SetupException {
        if (urls.isEmpty()) {
            throw new SetupException("No download source is configured for " + jar.getFileName());
        }
        for (int i = 0; i < urls.size() - 1; i++) {
            final String url = urls.get(i);
            try {
                if (Downloader.downloadIfPresent(URI.create(url), jar, progress) != null) {
                    return url;
                }
            } catch (final SetupException e) {
                out.println("  warning: " + e.getMessage());
            }
        }
        final String last = urls.get(urls.size() - 1);
        Downloader.download(URI.create(last), jar, progress);
        return last;
    }

    private static void verify(final Path download, final String name, final String checksumUrl, final PrintStream out)
            throws SetupException {
        final String published = Downloader.readStringIfPresent(URI.create(checksumUrl));
        if (published == null) {
            out.println("  warning: no checksum is published at " + checksumUrl + ", so " + name + " was not verified");
            return;
        }
        Downloader.verifySha1(download, published);
    }

    /**
     * Returns the directory Fess loads plugins from.
     *
     * @param fessHome the Fess installation directory
     * @return the plugin directory
     */
    public static Path directory(final String fessHome) {
        return Path.of(fessHome, "app", "WEB-INF", "plugin");
    }

    /**
     * Returns the file name a plugin jar is installed under, which is the Maven layout's.
     *
     * @param artifactId the plugin name
     * @param version the plugin version
     * @return the file name
     */
    public static String jarName(final String artifactId, final String version) {
        return artifactId + "-" + version + JAR;
    }

    /**
     * Reports whether a file name is a jar of the given plugin.
     *
     * <p>A prefix test alone is not enough. {@code fess-webapp-mcp-client-1.0.jar} starts with
     * {@code fess-webapp-mcp-}, so matching on the prefix would make
     * {@code remove plugin fess-webapp-mcp} delete a different plugin. What follows the name has
     * to look like a version, which for these artifacts means it starts with a digit.</p>
     *
     * @param fileName the file name to test
     * @param artifactId the plugin name
     * @return true if the file is a jar of that plugin
     */
    public static boolean isJarOf(final String fileName, final String artifactId) {
        final String prefix = artifactId + "-";
        if (!fileName.endsWith(JAR) || !fileName.startsWith(prefix)) {
            return false;
        }
        final String version = fileName.substring(prefix.length(), fileName.length() - JAR.length());
        return !version.isEmpty() && Character.isDigit(version.charAt(0));
    }

    /**
     * Returns the version part of a plugin jar's file name.
     *
     * @param fileName the file name
     * @param artifactId the plugin name
     * @return the version, or {@code null} when the file is not a jar of that plugin
     */
    public static String versionOf(final String fileName, final String artifactId) {
        if (!isJarOf(fileName, artifactId)) {
            return null;
        }
        return fileName.substring(artifactId.length() + 1, fileName.length() - JAR.length());
    }

    /**
     * Returns the artifact name a plugin jar's file name carries.
     *
     * <p>The split is at the first hyphen followed by a digit, which is where a Maven file name
     * turns from name into version. Doing it the other way round, by trying each known prefix,
     * would cut {@code fess-webapp-mcp-client} down to {@code fess-webapp-mcp}.</p>
     *
     * @param fileName the file name
     * @return the artifact name, or {@code null} when the name does not look like a plugin jar
     */
    public static String artifactIdOf(final String fileName) {
        if (!fileName.endsWith(JAR)) {
            return null;
        }
        final String stem = fileName.substring(0, fileName.length() - JAR.length());
        for (int i = 1; i < stem.length() - 1; i++) {
            if (stem.charAt(i) == '-' && Character.isDigit(stem.charAt(i + 1))) {
                return stem.substring(0, i);
            }
        }
        return null;
    }

    /**
     * Lists the installed jars of one plugin, in file name order.
     *
     * <p>More than one is possible: nothing stops two versions of a plugin sitting side by side,
     * and both would then load.</p>
     *
     * @param directory the plugin directory
     * @param artifactId the plugin name
     * @return the jars, empty when the plugin or the directory is absent
     * @throws SetupException if the directory cannot be read
     */
    public static List<Path> installedJars(final Path directory, final String artifactId) throws SetupException {
        return list(directory, path -> isJarOf(path.getFileName().toString(), artifactId));
    }

    /**
     * Lists every installed plugin jar, in file name order.
     *
     * @param directory the plugin directory
     * @return the jars, empty when the directory is absent
     * @throws SetupException if the directory cannot be read
     */
    public static List<Path> installed(final Path directory) throws SetupException {
        return list(directory, path -> {
            final String name = path.getFileName().toString();
            if (!name.endsWith(JAR)) {
                return false;
            }
            for (final String prefix : PLUGIN_PREFIXES) {
                if (name.startsWith(prefix + "-")) {
                    return true;
                }
            }
            return false;
        });
    }

    private static List<Path> list(final Path directory, final Predicate<Path> accepted) throws SetupException {
        if (!Files.isDirectory(directory)) {
            return List.of();
        }
        try (Stream<Path> entries = Files.list(directory)) {
            return entries.filter(Files::isRegularFile)
                    .filter(accepted)
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .toList();
        } catch (final IOException e) {
            throw new SetupException("Failed to read the plugin directory " + directory, e);
        }
    }

    /**
     * Deletes every installed jar of one plugin.
     *
     * @param directory the plugin directory
     * @param artifactId the plugin name
     * @return the jars that were deleted, empty when the plugin was not installed
     * @throws SetupException if a jar cannot be deleted
     */
    public static List<Path> remove(final Path directory, final String artifactId) throws SetupException {
        final List<Path> removed = new ArrayList<>();
        for (final Path jar : installedJars(directory, artifactId)) {
            try {
                Files.delete(jar);
                removed.add(jar);
            } catch (final IOException e) {
                throw new SetupException("Failed to delete " + jar, e);
            }
        }
        return removed;
    }

    /**
     * Deletes the other installed versions of a plugin, keeping one jar.
     *
     * <p>Two versions of a plugin in the directory both load, so an install has to leave only
     * one behind. The jar to keep is named rather than derived so that this runs <em>after</em>
     * the download: deleting first would leave an installation with no plugin at all when the
     * download then fails.</p>
     *
     * @param directory the plugin directory
     * @param artifactId the plugin name
     * @param keep the jar to leave in place
     * @return the jars that were deleted
     * @throws SetupException if a jar cannot be deleted
     */
    public static List<Path> removeOtherVersions(final Path directory, final String artifactId, final Path keep) throws SetupException {
        final List<Path> removed = new ArrayList<>();
        for (final Path jar : installedJars(directory, artifactId)) {
            if (jar.equals(keep)) {
                continue;
            }
            try {
                Files.delete(jar);
                removed.add(jar);
            } catch (final IOException e) {
                throw new SetupException("Failed to delete " + jar, e);
            }
        }
        return removed;
    }

    /**
     * Reduces a Fess version to the {@code major.minor} a plugin release is published against.
     *
     * @param fessVersion the Fess version, such as {@code 15.9.0} or {@code 15.9.0-SNAPSHOT}
     * @return the major.minor part
     * @throws SetupException if the version is not in that shape
     */
    public static String productVersion(final String fessVersion) throws SetupException {
        final String[] parts = fessVersion.split("[.\\-]");
        if (parts.length >= 2 && isNumber(parts[0]) && isNumber(parts[1])) {
            return parts[0] + "." + parts[1];
        }
        throw new SetupException("Could not tell which plugin version fits Fess " + fessVersion + ". Name one with --version <version>.");
    }

    private static boolean isNumber(final String part) {
        return !part.isEmpty() && part.chars().allMatch(Character::isDigit);
    }
}
