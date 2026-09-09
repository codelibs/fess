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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

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
 * are used here in preference to the JSON endpoints: this jar carries no JSON parser, and
 * picking fields out of JSON with a regular expression is how a health check starts lying.</p>
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
            final String version = versions.get(0);
            if (productVersion != null && !version.equals(productVersion) && !version.startsWith(productVersion + ".")) {
                checks.add(new Check(Status.WARN, "plugin " + artifactId,
                        version + " was built for another release (this Fess is " + productVersion + ")"));
                continue;
            }
            checks.add(new Check(Status.OK, "plugin " + artifactId, version));
        }
        return checks;
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
     * Reports whether Node.js is installed, which only matters to the Playwright crawler.
     *
     * @param fessHome the Fess installation directory
     * @param playwrightRequired whether the caller knows a crawl uses the Playwright client
     * @return the check
     * @throws SetupException if the Node.js directory cannot be read
     */
    public static Check nodejs(final Path fessHome, final boolean playwrightRequired) throws SetupException {
        final Path root = fessHome.resolve("nodejs");
        final String found = firstInstallation(root);
        if (found != null) {
            return new Check(Status.OK, "Node.js", found);
        }
        // Whether a crawl actually uses the Playwright client lives in the engine, not on disk,
        // so this does not claim to know: it reports the fact and says who needs it.
        // --playwright turns that into a failure for an installation that does use it.
        final String detail = "not installed; only the Playwright crawler needs it (install with: fess-setup install nodejs)";
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
