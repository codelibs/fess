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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

/**
 * The checks behind {@code fess-setup check}.
 *
 * <p>Now that OpenSearch, Node.js and the plugins are installed separately, an installation can
 * be wrong in ways Fess only reports once a crawl or a search has already failed: an engine
 * missing one of its plugins, a plugin jar built for another release, two versions of the same
 * plugin loading at once. Each of those is cheap to observe from outside, which is what this
 * does.</p>
 *
 * <p>The parts that read the engine take text rather than making the request, so the reasoning
 * is testable without a cluster. The {@code _cat} APIs answer in plain text, which is why they
 * are used here in preference to the JSON endpoints. The index settings have no {@code _cat}
 * form, so those are read with {@link JsonReader}; picking fields out of JSON with a regular
 * expression is how a health check starts lying.</p>
 */
public final class Diagnostics {

    /** How a single check came out. */
    public enum Status {
        /** Nothing to do. */
        OK,
        /** Works, but not as intended. */
        WARN,
        /** Something is broken or missing. */
        FAIL
    }

    /**
     * One line of the report.
     *
     * @param status how it came out
     * @param name what was checked
     * @param detail what was found
     */
    public record Check(Status status, String name, String detail) {
    }

    private Diagnostics() {
    }

    /**
     * Splits a {@code _cat} response into distinct non-empty lines, in first-seen order.
     *
     * <p>{@code _cat} answers per node, so a three-node cluster repeats every plugin and every
     * version three times.</p>
     *
     * @param text the response body
     * @return the distinct lines
     */
    public static List<String> distinctLines(final String text) {
        final Set<String> lines = new LinkedHashSet<>();
        for (final String line : text.split("\n")) {
            final String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                lines.add(trimmed);
            }
        }
        return new ArrayList<>(lines);
    }

    /**
     * Reports the engine version, and whether the cluster agrees on one.
     *
     * @param versions the distinct versions the nodes report
     * @return the check
     */
    public static Check engineVersion(final List<String> versions) {
        if (versions.isEmpty()) {
            return new Check(Status.FAIL, "engine version", "no node reported a version");
        }
        if (versions.size() > 1) {
            // Fess talks to whichever node answers, so a mixed cluster is not a detail.
            return new Check(Status.WARN, "engine version", "nodes disagree: " + String.join(", ", versions));
        }
        return new Check(Status.OK, "engine version", versions.get(0));
    }

    /**
     * Reports which of the plugins Fess needs are installed in the engine.
     *
     * @param required the plugin names Fess needs
     * @param installed the plugin names the engine reports
     * @return the check
     */
    public static Check enginePlugins(final List<String> required, final List<String> installed) {
        final List<String> missing = required.stream().filter(name -> !isInstalled(name, installed)).toList();
        if (missing.isEmpty()) {
            return new Check(Status.OK, "engine plugins", String.join(", ", required));
        }
        return new Check(Status.FAIL, "engine plugins", "missing: " + String.join(", ", missing)
                + " (install with: fess-setup install opensearch-plugins --opensearch-home <dir>)");
    }

    /**
     * Reports whether one required plugin is among the installed ones.
     *
     * <p>The two lists are named differently on purpose. {@code plugin.artifacts} holds Maven
     * artifact names, while {@code _cat/plugins} reports the name in each plugin's
     * plugin-descriptor.properties, and for the CodeLibs plugins those differ by the
     * {@code opensearch-} prefix: the artifact {@code opensearch-analysis-fess} installs as the
     * component {@code analysis-fess}. Comparing the two verbatim reports every plugin missing
     * on a correctly installed engine.</p>
     *
     * @param artifactId the Maven artifact name
     * @param installed the component names the engine reports
     * @return true if the plugin is installed
     */
    static boolean isInstalled(final String artifactId, final List<String> installed) {
        if (installed.contains(artifactId)) {
            return true;
        }
        final String prefix = "opensearch-";
        return artifactId.startsWith(prefix) && installed.contains(artifactId.substring(prefix.length()));
    }

    /** The reason OpenSearch gives when an analyzer names a file outside its config directory. */
    static final String OUTSIDE_CONFIG = "Resource path must be inside config directory";

    private static final String DICTIONARY_PATHS = "index dictionary paths";

    /**
     * Returns the indices with a shard that cannot be allocated because an analyzer names a file
     * outside the engine's config directory.
     *
     * @param catShards the answer of {@code _cat/shards?h=index,unassigned.details}
     * @return the index names, in first-seen order
     */
    public static List<String> indicesOutsideConfig(final String catShards) {
        final Set<String> indices = new LinkedHashSet<>();
        for (final String line : distinctLines(catShards)) {
            final String[] columns = line.split("\\s+", 2);
            if (columns.length == 2 && columns[1].contains(OUTSIDE_CONFIG)) {
                indices.add(columns[0]);
            }
        }
        return new ArrayList<>(indices);
    }

    /**
     * Reports the indices whose analyzers name a dictionary file by an absolute path.
     *
     * <p>OpenSearch resolves a relative {@code *_path} against its config directory when it opens
     * the index, and refuses an absolute one that is not inside that directory. Fess 15.9 and
     * earlier created their indices with an absolute {@code FESS_DICTIONARY_PATH}, so those
     * indices name the config directory of the engine they were created on. Once the engine runs
     * from another directory -- {@code fess-setup install opensearch} puts a newer version beside
     * the old one -- the shards of such an index cannot be allocated and the cluster turns red.
     * The setting cannot be changed on an existing index, which is why the remedy is a new index,
     * created while the engine is still where the old paths point.</p>
     *
     * @param settingsJson the answer of {@code _settings/index.analysis.*?flat_settings=true}
     * @param outsideConfig the indices that already cannot be allocated for that reason, from
     *            {@link #indicesOutsideConfig(String)}
     * @return one OK check when every dictionary path is relative, otherwise one check per index
     *         at fault: FAIL when it cannot be allocated, WARN when it can but would not after a move
     * @throws SetupException if the settings are not JSON
     */
    public static List<Check> dictionaryPaths(final String settingsJson, final List<String> outsideConfig) throws SetupException {
        final Map<String, List<String>> absolute = new LinkedHashMap<>();
        int pathSettings = 0;
        int indicesWithPaths = 0;
        if (!(JsonReader.parse(settingsJson) instanceof final Map<?, ?> indices)) {
            throw new SetupException("The index settings are not a JSON object");
        }
        for (final Map.Entry<?, ?> index : indices.entrySet()) {
            if (!(index.getValue() instanceof final Map<?, ?> body) || !(body.get("settings") instanceof final Map<?, ?> settings)) {
                continue;
            }
            final List<String> paths = new ArrayList<>();
            int count = 0;
            for (final Map.Entry<?, ?> setting : settings.entrySet()) {
                if (setting.getValue() instanceof final String value && isPathSetting(setting.getKey().toString())) {
                    count++;
                    if (isAbsolutePath(value)) {
                        paths.add(value);
                    }
                }
            }
            pathSettings += count;
            indicesWithPaths += count > 0 ? 1 : 0;
            if (!paths.isEmpty()) {
                absolute.put(index.getKey().toString(), paths);
            }
        }

        final Set<String> atFault = new LinkedHashSet<>(outsideConfig);
        atFault.addAll(absolute.keySet());
        final List<Check> checks = new ArrayList<>();
        for (final String index : atFault) {
            final List<String> paths = absolute.getOrDefault(index, List.of());
            final String found = paths.isEmpty() ? "an analyzer names a file outside the engine's config directory"
                    : paths.size() + " analyzer setting(s) name an absolute path, such as " + paths.get(0);
            // Only the document index has a re-index in the admin screen; the remedy for any other
            // index is the same in substance, a new index created while the old paths still resolve.
            final String recreate = "re-create it, so the new index uses dictionary/"
                    + " (the document index: re-index from Admin > Maintenance, then delete the old index)";
            if (outsideConfig.contains(index)) {
                checks.add(new Check(Status.FAIL, DICTIONARY_PATHS, index + " cannot be allocated: " + found
                        + " (start the engine from the directory those paths name, then " + recreate + ")"));
            } else {
                checks.add(new Check(Status.WARN, DICTIONARY_PATHS,
                        index + ": " + found + "; the index will not open if the engine moves to another directory (before moving it, "
                                + recreate + "; or keep the engine where it is)"));
            }
        }
        if (checks.isEmpty()) {
            checks.add(new Check(Status.OK, DICTIONARY_PATHS, pathSettings == 0 ? "no index names a dictionary file"
                    : pathSettings + " path setting(s) in " + indicesWithPaths + " index(es), all relative"));
        }
        return checks;
    }

    /**
     * Returns whether a flat analysis setting names a file: every {@code *_path} setting, and
     * kuromoji's {@code user_dictionary}, which is a path without saying so.
     *
     * @param key the flat setting name
     * @return true if its value is a path
     */
    static boolean isPathSetting(final String key) {
        final String name = key.substring(key.lastIndexOf('.') + 1);
        return name.endsWith("_path") || "user_dictionary".equals(name);
    }

    /**
     * Returns whether a path is absolute on any platform the engine may run on.
     *
     * <p>{@link Path#isAbsolute()} answers for the machine running fess-setup, which is not
     * necessarily the engine's: {@code C:\opensearch\...} is relative on Linux.</p>
     *
     * @param path the path
     * @return true for {@code /...}, {@code \...} (including UNC) and {@code C:\...} or {@code C:/...}
     */
    static boolean isAbsolutePath(final String path) {
        if (path.startsWith("/") || path.startsWith("\\")) {
            return true;
        }
        if (path.length() < 3 || path.charAt(1) != ':' || path.charAt(2) != '\\' && path.charAt(2) != '/') {
            return false;
        }
        final char drive = Character.toUpperCase(path.charAt(0));
        return drive >= 'A' && drive <= 'Z';
    }

    /**
     * Reports the installed Fess plugins, one check each.
     *
     * @param directory the plugin directory
     * @param productVersion the major.minor this Fess accepts plugins for
     * @return one check per installed plugin, in name order
     * @throws SetupException if the directory cannot be read
     */
    public static List<Check> installedPlugins(final Path directory, final String productVersion) throws SetupException {
        final List<Check> checks = new ArrayList<>();
        for (final String artifactId : installedArtifactIds(directory)) {
            final List<Path> jars = FessPluginInstaller.installedJars(directory, artifactId);
            final List<String> versions =
                    jars.stream().map(jar -> FessPluginInstaller.versionOf(jar.getFileName().toString(), artifactId)).toList();
            if (versions.size() > 1) {
                // Both jars load and which one wins is undefined, so this is not a warning.
                checks.add(new Check(Status.FAIL, "plugin " + artifactId, "installed twice: " + String.join(", ", versions)
                        + " (remove one with: fess-setup remove plugin " + artifactId + ", then install the version you want)"));
                continue;
            }
            final String unreadable = unreadableReason(jars.get(0));
            if (unreadable != null) {
                // Fess starts and the other plugins work, but this one does not load.
                checks.add(new Check(Status.FAIL, "plugin " + artifactId,
                        unreadable + " (remove it with: fess-setup remove plugin " + artifactId + ", then install it again)"));
                continue;
            }
            final String version = versions.get(0);
            if (productVersion != null && !version.equals(productVersion) && !version.startsWith(productVersion + ".")) {
                // A failure, not a warning. A plugin is compiled against one Fess release, and the war
                // it runs in changes under it between releases: 15.9 moved the OpenSearch classes into
                // another package and stopped shipping libraries that 15.8 plugins took from it. A
                // plugin jar left over from the previous release can then stop Fess from starting at
                // all -- fess-webapp-mcp 15.8.0 in a 15.9 war fails the DI container and every page
                // answers 404 -- while this check said "Usable, with warnings" and exited 0.
                checks.add(new Check(Status.FAIL, "plugin " + artifactId, version + " was built for another release (this Fess is "
                        + productVersion + "; replace it with: fess-setup upgrade plugins)"));
                continue;
            }
            checks.add(new Check(Status.OK, "plugin " + artifactId, version));
        }
        return checks;
    }

    /**
     * Explains why a plugin jar cannot be opened.
     *
     * <p>The file name alone says nothing about the content: a download that stopped half way,
     * an empty file and a text file renamed to {@code .jar} all look like an installed plugin.
     * The servlet container skips such a file, so Fess starts without the plugin.</p>
     *
     * @param jar the plugin jar
     * @return the file name and the reason, or {@code null} when the jar opens as a zip archive
     */
    private static String unreadableReason(final Path jar) {
        try (ZipFile zip = new ZipFile(jar.toFile())) {
            return null;
        } catch (final IOException e) {
            return jar.getFileName() + " cannot be opened as a jar (" + e.getMessage() + ")";
        }
    }

    /**
     * Returns the artifact names installed in the plugin directory, in name order.
     *
     * @param directory the plugin directory
     * @return the artifact names
     * @throws SetupException if the directory cannot be read
     */
    public static List<String> installedArtifactIds(final Path directory) throws SetupException {
        final Set<String> names = new LinkedHashSet<>();
        for (final Path jar : FessPluginInstaller.installed(directory)) {
            final String artifactId = FessPluginInstaller.artifactIdOf(jar.getFileName().toString());
            if (artifactId != null) {
                names.add(artifactId);
            }
        }
        return new ArrayList<>(names);
    }

    /**
     * Reports whether Node.js is installed, which only matters to the Playwright plugins (the crawler and the thumbnail generator).
     *
     * @param fessHome the Fess installation directory
     * @param playwrightRequired whether the caller knows this installation uses a Playwright plugin
     * @return the check
     * @throws SetupException if the Node.js directory cannot be read
     */
    public static Check nodejs(final Path fessHome, final boolean playwrightRequired) throws SetupException {
        final Path root = fessHome.resolve("nodejs");
        final String found = firstInstallation(root);
        if (found != null) {
            return new Check(Status.OK, "Node.js", found);
        }
        // Whether a crawl actually uses the Playwright client lives in the engine, and a thumbnail
        // plugin is not looked for here, so this does not claim to know: it reports the fact and
        // says who needs it. --playwright turns that into a failure for an installation that uses
        // either.
        final String detail =
                "not installed; only the Playwright plugins (crawler and thumbnail) need it (install with: fess-setup install nodejs)";
        return new Check(playwrightRequired ? Status.FAIL : Status.OK, "Node.js", detail);
    }

    private static String firstInstallation(final Path root) throws SetupException {
        if (!Files.isDirectory(root)) {
            return null;
        }
        try (Stream<Path> entries = Files.list(root)) {
            return entries.filter(Files::isDirectory)
                    .filter(dir -> Files.isRegularFile(dir.resolve("bin/node")) || Files.isRegularFile(dir.resolve("node.exe")))
                    .map(dir -> dir.getFileName().toString())
                    .sorted()
                    .findFirst()
                    .orElse(null);
        } catch (final IOException e) {
            throw new SetupException("Failed to read " + root, e);
        }
    }

    /**
     * Returns the worst status in a set of checks, which is the exit status of the report.
     *
     * @param checks the checks
     * @return the worst status, OK when there are none
     */
    public static Status worst(final List<Check> checks) {
        Status worst = Status.OK;
        for (final Check check : checks) {
            if (check.status().ordinal() > worst.ordinal()) {
                worst = check.status();
            }
        }
        return worst;
    }
}
