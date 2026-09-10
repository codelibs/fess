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
import java.io.PrintStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Installs the artifacts Fess needs but does not bundle.
 *
 * <p>Runs from {@code bin/fess-setup} before Fess itself does, so it depends on nothing but
 * the JDK: its only classpath is its own jar.</p>
 */
public final class FessSetup {

    private static final String DEFINITION_RESOURCE = "/fess-setup.properties";

    /** Post-install hook that installs the Fess plugins and writes configsync.config_path. */
    private static final String POST_OPENSEARCH = "opensearch";

    /** The definition holding where Fess plugins come from and go, rather than a download. */
    private static final String PLUGIN_COMPONENT = "plugin";

    /** What a plugin name and a plugin version may be made of, which is a Maven coordinate. */
    private static final Pattern COORDINATE = Pattern.compile("[A-Za-z0-9._-]+");

    private static final int EXIT_OK = 0;

    private static final int EXIT_FAILED = 1;

    private static final int EXIT_USAGE = 2;

    private static final String USAGE = """
            Usage: fess-setup <command> [options]

            Commands:
              install opensearch [--dest <dir>] [--version <version>]
                  Download OpenSearch, install the Fess plugins into it and configure it.
                  Official builds exist for Linux and Windows only.

              install opensearch-plugins --opensearch-home <dir> [--version <version>]
                  Install the Fess plugins into an OpenSearch you already have, in place of
                  running its bin/opensearch-plugin four times by hand.

              install nodejs [--dest <dir>] [--version <version>]
                  Download Node.js, which the Playwright crawler needs.

              install plugin <name>[:<version>]... [--version <version>] [--repository <url>]
                  Install Fess plugins, for example fess-script-groovy or fess-ds-git.
                  A name with no version is resolved against the repository, which picks
                  the newest one built for this Fess. Name one instead to pin it, either
                  per plugin as fess-script-groovy:15.9.0 or, for every plugin that has
                  no version of its own, with --version.
                  A release comes from the plugin's GitHub release, or from the Maven
                  repository when GitHub has no such asset. A development build of Fess
                  installs the snapshots of its own line as well, and prefers them.
                  Every jar is checked against the SHA-1 the Maven repository publishes.
                  --repository takes every version, jar and checksum from one repository,
                  GitHub included.

              remove plugin <name>...
                  Delete installed Fess plugins.

              list
                  Show the components this build knows how to install.

              list plugins [--repository <url>]
                  Show the Fess plugins published for this version, and which are installed.
                  A development build lists the snapshot repository too.

              list installed
                  Show the installed Fess plugins, without asking the repository.

              upgrade plugins [--repository <url>]
                  Re-install every installed Fess plugin at the version that fits this Fess.

              check [--url <engine url>] [--playwright]
                  Report on the installation: the engine, its plugins, and the installed
                  plugins. Exit 0 when everything is in order, 1 when something is wrong.
            """;

    private FessSetup() {
    }

    /**
     * Entry point.
     *
     * @param args the command line
     */
    public static void main(final String[] args) {
        System.exit(run(args, System.out, System.err));
    }

    /**
     * Runs one command.
     *
     * @param args the command line
     * @param out the stream for normal output
     * @param err the stream for errors
     * @return 0 on success, 1 on a setup failure, 2 on a usage error
     */
    public static int run(final String[] args, final PrintStream out, final PrintStream err) {
        if (args.length == 0) {
            err.println(USAGE);
            return EXIT_USAGE;
        }
        try {
            final Map<String, ComponentDefinition> definitions = loadDefinitions();
            return switch (args[0]) {
            case "list" -> list(args, definitions, out, err);
            case "install" -> install(args, definitions, out, err);
            case "remove" -> remove(args, definitions, out, err);
            case "upgrade" -> upgrade(args, definitions, out, err);
            case "check" -> check(parseOptions(args, 1), definitions, out, err);
            default -> {
                err.println(USAGE);
                yield EXIT_USAGE;
            }
            };
        } catch (final SetupException e) {
            err.println("error: " + e.getMessage());
            return EXIT_FAILED;
        } catch (final IOException e) {
            err.println("error: failed to read the setup definition: " + e.getMessage());
            return EXIT_FAILED;
        }
    }

    /**
     * Loads the definition file bundled in this jar.
     *
     * @return the components, keyed by name
     * @throws IOException if the resource is missing or unreadable
     */
    static Map<String, ComponentDefinition> loadDefinitions() throws IOException {
        try (InputStream in = FessSetup.class.getResourceAsStream(DEFINITION_RESOURCE)) {
            if (in == null) {
                throw new IOException(DEFINITION_RESOURCE + " is missing from the jar");
            }
            return DefinitionLoader.load(in);
        }
    }

    private static int list(final String[] args, final Map<String, ComponentDefinition> definitions, final PrintStream out,
            final PrintStream err) throws SetupException {
        if (args.length > 1) {
            if ("installed".equals(args[1])) {
                return listInstalled(definitions, parseOptions(args, 2), out);
            }
            if (!"plugins".equals(args[1])) {
                err.println("error: unknown list target: " + args[1]);
                err.println(USAGE);
                return EXIT_USAGE;
            }
            return listPlugins(definitions, parseOptions(args, 2), out);
        }
        definitions.forEach((name, definition) -> {
            // A definition without a URL describes where something comes from rather than a
            // download of its own: the plugin definition is one, and `install plugin` uses it.
            if (definition.get("url") == null) {
                return;
            }
            final String version = definition.get("version");
            out.println(version == null ? name : name + "  " + version);
        });
        return EXIT_OK;
    }

    /**
     * Prints the plugins the repository publishes for this Fess, marking the installed ones.
     *
     * @param definitions the setup definition
     * @param options the command line options
     * @param out the stream for normal output
     * @return 0
     * @throws SetupException if the repository or the plugin directory cannot be read
     */
    private static int listPlugins(final Map<String, ComponentDefinition> definitions, final Map<String, String> options,
            final PrintStream out) throws SetupException {
        final ComponentDefinition definition = definitions.get(PLUGIN_COMPONENT);
        final PluginSources sources = pluginSources(definition, options);
        final Path directory = pluginDirectory(definition, options);
        final String release = sources.releaseRepository();
        List<String> published = PluginRepository.namesFromListing(Downloader.readString(URI.create(release)));
        final StringBuilder consulted = new StringBuilder(release);
        if (developmentBuild() && sources.hasSnapshot()) {
            // A plugin whose line has no release yet is only in the snapshot repository, and
            // leaving it out would hide exactly the plugins a development build can install.
            try {
                published = PluginRepository.mergeNames(published,
                        PluginRepository.namesFromListing(Downloader.readString(URI.create(sources.snapshot()))));
                consulted.append(" and ").append(sources.snapshot());
            } catch (final SetupException e) {
                out.println("warning: " + e.getMessage());
            }
        }
        for (final String name : published) {
            final String versions = installedVersions(directory, name);
            out.println(versions == null ? "  " + name : "  " + name + "  (installed: " + versions + ")");
        }

        // A plugin built locally, or one published somewhere else, is installed but not listed
        // above. Saying nothing about it would make `list plugins` look as though it had lost it.
        final List<String> unlisted = new ArrayList<>();
        for (final Path jar : FessPluginInstaller.installed(directory)) {
            if (published.stream().noneMatch(name -> FessPluginInstaller.isJarOf(jar.getFileName().toString(), name))) {
                unlisted.add(jar.getFileName().toString());
            }
        }
        if (!unlisted.isEmpty()) {
            out.println();
            out.println("Installed but not published by " + consulted + ":");
            unlisted.forEach(name -> out.println("  " + name));
        }
        return EXIT_OK;
    }

    /**
     * Returns the installed versions of a plugin as a comma-separated string.
     *
     * @param directory the plugin directory
     * @param artifactId the plugin name
     * @return the versions, or {@code null} when the plugin is not installed
     * @throws SetupException if the plugin directory cannot be read
     */
    private static String installedVersions(final Path directory, final String artifactId) throws SetupException {
        final List<Path> jars = FessPluginInstaller.installedJars(directory, artifactId);
        if (jars.isEmpty()) {
            return null;
        }
        return String.join(", ",
                jars.stream().map(jar -> FessPluginInstaller.versionOf(jar.getFileName().toString(), artifactId)).toList());
    }

    private static int install(final String[] args, final Map<String, ComponentDefinition> definitions, final PrintStream out,
            final PrintStream err) throws SetupException {
        if (args.length < 2) {
            err.println(USAGE);
            return EXIT_USAGE;
        }
        final String target = args[1];
        final Map<String, String> options = parseOptions(args, 2);
        if ("opensearch-plugins".equals(target)) {
            final String home = options.get("opensearch-home");
            if (home == null) {
                err.println("error: install opensearch-plugins requires --opensearch-home <dir>");
                err.println(USAGE);
                return EXIT_USAGE;
            }
            installPlugins(definitions.get("opensearch"), Path.of(home), options, out);
            return EXIT_OK;
        }
        if (PLUGIN_COMPONENT.equals(target)) {
            return installFessPlugins(args, definitions, options, out, err);
        }
        final ComponentDefinition definition = definitions.get(target);
        if (definition == null) {
            err.println("error: unknown component: " + target);
            return EXIT_FAILED;
        }
        return installComponent(definition, options, out, err);
    }

    /**
     * Installs Fess plugins into the webapp's plugin directory.
     *
     * <p>The version is resolved from the repository unless the caller names one, so that
     * {@code install plugin fess-ds-git} picks the latest release built for this Fess rather
     * than whatever is newest. A caller that needs a reproducible install -- a Dockerfile,
     * say -- names the version instead, per plugin, because the plugins of one Fess line are
     * released separately and need not all be at the same one.</p>
     *
     * <p>Every argument is parsed before the first download starts: a typo in the last of
     * seven plugin names should not leave six of them installed.</p>
     *
     * @param args the command line
     * @param definitions the setup definition
     * @param options the parsed options
     * @param out the stream for normal output
     * @param err the stream for errors
     * @return 0 on success, 2 when no plugin was named or one was named badly
     * @throws SetupException if a download or a file operation fails
     */
    private static int installFessPlugins(final String[] args, final Map<String, ComponentDefinition> definitions,
            final Map<String, String> options, final PrintStream out, final PrintStream err) throws SetupException {
        final List<String> arguments = positionals(args, 2);
        if (arguments.isEmpty()) {
            err.println("error: install plugin requires at least one plugin name, for example:");
            err.println("  fess-setup install plugin fess-script-groovy");
            err.println("Run `fess-setup list plugins` to see what is published.");
            return EXIT_USAGE;
        }
        final List<PluginSpec> specs = new ArrayList<>();
        for (final String argument : arguments) {
            try {
                specs.add(parsePluginSpec(argument, options.get("version")));
            } catch (final SetupException e) {
                err.println("error: " + e.getMessage());
                return EXIT_USAGE;
            }
        }
        final ComponentDefinition definition = definitions.get(PLUGIN_COMPONENT);
        final PluginSources sources = pluginSources(definition, options);
        final Path directory = pluginDirectory(definition, options);
        for (final PluginSpec spec : specs) {
            installOne(sources, spec.artifactId(), resolve(sources, spec.artifactId(), spec.version()), directory, out);
        }
        out.println();
        out.println("Restart Fess to load " + (specs.size() == 1 ? "it." : "them."));
        return EXIT_OK;
    }

    /**
     * A plugin named on the command line: which plugin, and which version of it to install.
     *
     * @param artifactId the plugin name
     * @param version the version to install, or {@code null} to let the repository decide
     */
    record PluginSpec(String artifactId, String version) {
    }

    /**
     * Parses one {@code install plugin} argument, which is a plugin name and may carry a
     * version after a colon.
     *
     * <p>The colon wins over {@code --version}, which is the default for the arguments that
     * do not carry one, so that a mostly uniform set of plugins can name the odd one out.</p>
     *
     * <p>Both parts are checked against the characters a Maven coordinate is made of. This is
     * not pedantry about version syntax: the name and the version become the jar's file name,
     * which is resolved against the plugin directory, so a separator in either would write the
     * jar somewhere else entirely.</p>
     *
     * @param argument the argument, either {@code <name>} or {@code <name>:<version>}
     * @param defaultVersion the version {@code --version} named, or null
     * @return the plugin and its version
     * @throws SetupException if the argument or the default version is not in that shape
     */
    static PluginSpec parsePluginSpec(final String argument, final String defaultVersion) throws SetupException {
        final int colon = argument.indexOf(':');
        if (colon < 0) {
            if (defaultVersion != null && !COORDINATE.matcher(defaultVersion).matches()) {
                throw new SetupException("--version " + defaultVersion + " is not a version.");
            }
            if (!COORDINATE.matcher(argument).matches()) {
                throw new SetupException("'" + argument + "' is not a plugin name.");
            }
            return new PluginSpec(argument, defaultVersion);
        }
        final String artifactId = argument.substring(0, colon);
        final String version = argument.substring(colon + 1);
        if (!COORDINATE.matcher(artifactId).matches() || !COORDINATE.matcher(version).matches()) {
            throw new SetupException("'" + argument + "' is not a plugin, which is written as <name> or <name>:<version>, "
                    + "for example fess-script-groovy or fess-script-groovy:15.9.0.");
        }
        return new PluginSpec(artifactId, version);
    }

    /**
     * Downloads one plugin jar into the plugin directory and removes the other versions of it.
     *
     * <p>The removal happens after the download on purpose: doing it first would leave an
     * installation with no plugin at all when the download then fails.</p>
     *
     * @param sources where plugins are published
     * @param artifactId the plugin name
     * @param resolved the version to install
     * @param directory the plugin directory
     * @param out the stream for normal output
     * @throws SetupException if the download or a file operation fails
     */
    private static void installOne(final PluginSources sources, final String artifactId, final Resolved resolved, final Path directory,
            final PrintStream out) throws SetupException {
        final Path jar = directory.resolve(FessPluginInstaller.jarName(artifactId, resolved.fileVersion()));
        out.println("Downloading " + artifactId + " " + resolved.version());
        final int[] lastPercent = { -1 };
        final String used =
                FessPluginInstaller.install(PluginRepository.jarUrls(sources, artifactId, resolved.version(), resolved.fileVersion()),
                        PluginRepository.checksumUrl(
                                PluginRepository.repositoryJarUrl(sources, artifactId, resolved.version(), resolved.fileVersion())),
                        jar, out, (bytes, total) -> reportProgress(out, bytes, total, lastPercent));
        out.println("  from " + used);
        for (final Path previous : FessPluginInstaller.removeOtherVersions(directory, artifactId, jar)) {
            out.println("Removed the previous " + previous.getFileName());
        }
        out.println("Installed " + jar);
    }

    /**
     * Deletes installed Fess plugins.
     *
     * @param args the command line
     * @param definitions the setup definition
     * @param out the stream for normal output
     * @param err the stream for errors
     * @return 0 on success, 2 on a usage error
     * @throws SetupException if a jar cannot be deleted
     */
    private static int remove(final String[] args, final Map<String, ComponentDefinition> definitions, final PrintStream out,
            final PrintStream err) throws SetupException {
        if (args.length < 2 || !PLUGIN_COMPONENT.equals(args[1])) {
            err.println(USAGE);
            return EXIT_USAGE;
        }
        final List<String> artifactIds = positionals(args, 2);
        if (artifactIds.isEmpty()) {
            err.println("error: remove plugin requires at least one plugin name");
            return EXIT_USAGE;
        }
        final Path directory = pluginDirectory(definitions.get(PLUGIN_COMPONENT), parseOptions(args, 2));
        boolean removedAny = false;
        for (final String artifactId : artifactIds) {
            final List<Path> removed = FessPluginInstaller.remove(directory, artifactId);
            if (removed.isEmpty()) {
                out.println(artifactId + " is not installed.");
            } else {
                removed.forEach(jar -> out.println("Removed " + jar));
                removedAny = true;
            }
        }
        if (removedAny) {
            out.println();
            out.println("Restart Fess for the change to take effect.");
        }
        return EXIT_OK;
    }

    /**
     * Prints the installed plugins without asking the repository.
     *
     * <p>The first question about a broken installation is what is installed, and the answer
     * must not depend on the machine having a route to the repository.</p>
     *
     * @param definitions the setup definition
     * @param options the command line options
     * @param out the stream for normal output
     * @return 0
     * @throws SetupException if the plugin directory cannot be read
     */
    private static int listInstalled(final Map<String, ComponentDefinition> definitions, final Map<String, String> options,
            final PrintStream out) throws SetupException {
        final Path directory = pluginDirectory(definitions.get(PLUGIN_COMPONENT), options);
        final List<String> artifactIds = Diagnostics.installedArtifactIds(directory);
        if (artifactIds.isEmpty()) {
            out.println("No plugins are installed in " + directory);
            return EXIT_OK;
        }
        for (final String artifactId : artifactIds) {
            out.println("  " + artifactId + "  " + installedVersions(directory, artifactId));
        }
        return EXIT_OK;
    }

    /**
     * Re-installs every installed plugin at the version that fits this Fess.
     *
     * <p>This is the chore a Fess upgrade creates: the plugins in WEB-INF/plugin were built
     * against the previous release and stay behind until each is reinstalled by hand.</p>
     *
     * @param args the command line
     * @param definitions the setup definition
     * @param out the stream for normal output
     * @param err the stream for errors
     * @return 0 on success, 2 on a usage error
     * @throws SetupException if a download or a file operation fails
     */
    private static int upgrade(final String[] args, final Map<String, ComponentDefinition> definitions, final PrintStream out,
            final PrintStream err) throws SetupException {
        if (args.length < 2 || !"plugins".equals(args[1])) {
            err.println(USAGE);
            return EXIT_USAGE;
        }
        final Map<String, String> options = parseOptions(args, 2);
        final ComponentDefinition definition = definitions.get(PLUGIN_COMPONENT);
        final PluginSources sources = pluginSources(definition, options);
        final Path directory = pluginDirectory(definition, options);
        final List<String> artifactIds = Diagnostics.installedArtifactIds(directory);
        if (artifactIds.isEmpty()) {
            out.println("No plugins are installed in " + directory);
            return EXIT_OK;
        }
        int upgraded = 0;
        for (final String artifactId : artifactIds) {
            final String current = installedVersions(directory, artifactId);
            final Resolved wanted = resolve(sources, artifactId, null);
            // The file version, not the version, because a snapshot is on disk under the build
            // it came from: comparing 15.9.0-SNAPSHOT with the installed timestamp would either
            // re-download every time or never notice a newer build.
            if (wanted.fileVersion().equals(current)) {
                out.println(artifactId + " is already at " + current);
                continue;
            }
            out.println(artifactId + " " + current + " -> " + wanted.fileVersion());
            installOne(sources, artifactId, wanted, directory, out);
            upgraded++;
        }
        out.println();
        if (upgraded == 0) {
            out.println("Nothing to do.");
        } else {
            out.println("Restart Fess to load the new versions.");
        }
        return EXIT_OK;
    }

    /**
     * Reports on the installation: the engine, its plugins, and the installed Fess plugins.
     *
     * @param options the command line options
     * @param definitions the setup definition
     * @param out the stream for normal output
     * @param err the stream for errors
     * @return 0 when nothing failed, 1 otherwise
     * @throws SetupException if the plugin directory cannot be read
     */
    private static int check(final Map<String, String> options, final Map<String, ComponentDefinition> definitions, final PrintStream out,
            final PrintStream err) throws SetupException {
        final List<Diagnostics.Check> checks = new ArrayList<>();
        final String url = engineUrl(options);

        out.println("Engine  " + url);
        try {
            Downloader.readString(URI.create(url));
            checks.add(new Diagnostics.Check(Diagnostics.Status.OK, "reachable", "yes"));
            checks.add(
                    Diagnostics.engineVersion(Diagnostics.distinctLines(Downloader.readString(URI.create(url + "/_cat/nodes?h=version")))));
            final ComponentDefinition opensearch = definitions.get("opensearch");
            final List<String> required = opensearch.list("plugin.artifacts").stream().map(FessSetup::artifactIdOf).toList();
            checks.add(Diagnostics.enginePlugins(required,
                    Diagnostics.distinctLines(Downloader.readString(URI.create(url + "/_cat/plugins?h=component")))));
            checks.add(configsync(url));
        } catch (final SetupException e) {
            // Every later engine check would repeat this one failure, so stop after saying it.
            checks.add(new Diagnostics.Check(Diagnostics.Status.FAIL, "reachable",
                    e.getMessage() + " (set SEARCH_ENGINE_HTTP_URL, or start the engine)"));
        }
        checks.forEach(check -> print(out, check));

        final Path directory = pluginDirectory(definitions.get(PLUGIN_COMPONENT), options);
        out.println();
        out.println("Fess    " + fessHome());
        final List<Diagnostics.Check> local = new ArrayList<>();
        local.add(pluginDirectoryCheck(directory));
        local.addAll(Diagnostics.installedPlugins(directory, productVersionOrNull()));
        local.add(Diagnostics.nodejs(Path.of(fessHome()), options.containsKey("playwright")));
        local.forEach(check -> print(out, check));

        checks.addAll(local);
        final Diagnostics.Status worst = Diagnostics.worst(checks);
        out.println();
        if (worst == Diagnostics.Status.FAIL) {
            err.println("Something is wrong. See the FAIL lines above.");
            return EXIT_FAILED;
        }
        out.println(worst == Diagnostics.Status.WARN ? "Usable, with warnings." : "Everything checks out.");
        return EXIT_OK;
    }

    private static Diagnostics.Check configsync(final String url) {
        try {
            Downloader.readString(URI.create(url + "/_configsync/file"));
            return new Diagnostics.Check(Diagnostics.Status.OK, "configsync", "responding");
        } catch (final SetupException e) {
            return new Diagnostics.Check(Diagnostics.Status.FAIL, "configsync",
                    "not responding; dictionaries will not reach the engine (" + e.getMessage() + ")");
        }
    }

    private static Diagnostics.Check pluginDirectoryCheck(final Path directory) {
        if (!Files.isDirectory(directory)) {
            return new Diagnostics.Check(Diagnostics.Status.FAIL, "plugin directory", directory + " does not exist");
        }
        if (!Files.isWritable(directory)) {
            return new Diagnostics.Check(Diagnostics.Status.FAIL, "plugin directory", directory + " is not writable");
        }
        return new Diagnostics.Check(Diagnostics.Status.OK, "plugin directory", directory.toString());
    }

    private static void print(final PrintStream out, final Diagnostics.Check check) {
        out.printf("  %-4s  %-28s  %s%n", check.status(), check.name(), check.detail());
    }

    /**
     * Returns the engine URL: {@code --url}, else the environment variable the launcher sets,
     * else the default the shipped configuration uses.
     *
     * @param options the command line options
     * @return the URL, without a trailing slash
     */
    static String engineUrl(final Map<String, String> options) {
        String url = options.get("url");
        if (url == null) {
            url = System.getenv("SEARCH_ENGINE_HTTP_URL");
        }
        if (url == null || url.isBlank()) {
            url = "http://localhost:9200";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    /**
     * Returns the artifact name of a {@code groupId:artifactId} coordinate.
     *
     * @param coordinate the coordinate
     * @return the artifact name
     */
    static String artifactIdOf(final String coordinate) {
        final int separator = coordinate.indexOf(':');
        return separator < 0 ? coordinate : coordinate.substring(separator + 1);
    }

    /**
     * Returns the plugin version this build accepts, or {@code null} when it cannot be told,
     * which is the case when the jar has no manifest.
     *
     * @return the major.minor, or null
     */
    private static String productVersionOrNull() {
        try {
            return FessPluginInstaller.productVersion(fessVersion());
        } catch (final SetupException e) {
            return null;
        }
    }

    /**
     * A plugin version, in the two forms a Maven repository gives it: the version itself, which
     * names the directory, and the version the jar's file name carries. They differ for a
     * snapshot, where {@code 15.9.0-SNAPSHOT} is published as {@code 15.9.0-20260903.015513-6}.
     *
     * @param version the version
     * @param fileVersion the version the file name carries
     */
    private record Resolved(String version, String fileVersion) {
    }

    /**
     * Reports whether this is a development build of Fess, which is what decides that the
     * snapshot repository is read at all.
     *
     * @return true when this build's version is a snapshot
     */
    private static boolean developmentBuild() {
        return PluginRepository.isSnapshot(fessVersion());
    }

    /**
     * Works out which version of a plugin to install, and where its jar is published.
     *
     * @param sources where plugins are published
     * @param artifactId the plugin name
     * @param named the version the caller asked for, or null to pick one
     * @return the resolved version
     * @throws SetupException if the metadata cannot be read or holds no matching version
     */
    private static Resolved resolve(final PluginSources sources, final String artifactId, final String named) throws SetupException {
        final String version = named != null ? named : resolveVersion(sources, artifactId);
        if (!PluginRepository.isSnapshot(version)) {
            return new Resolved(version, version);
        }
        final String metadata =
                Downloader.readString(URI.create(PluginRepository.versionMetadataUrl(sources.repositoryOf(version), artifactId, version)));
        return new Resolved(version, PluginRepository.snapshotFileVersion(metadata, version));
    }

    /**
     * Picks the plugin version that fits this Fess, reading the artifact's Maven metadata.
     *
     * <p>A development build reads the snapshot repository as well as the release one and
     * prefers a snapshot, so that plugins can be installed on a 15.9.0-SNAPSHOT Fess before the
     * 15.9 line has released any. A released Fess never reads the snapshot repository, so a
     * snapshot is neither listed nor installed there.</p>
     *
     * @param sources where plugins are published
     * @param artifactId the plugin name
     * @return the version to install
     * @throws SetupException if the metadata cannot be read or holds no matching version
     */
    private static String resolveVersion(final PluginSources sources, final String artifactId) throws SetupException {
        return resolveVersion(sources, artifactId, FessPluginInstaller.productVersion(fessVersion()), developmentBuild());
    }

    /**
     * Picks the plugin version that fits a given Fess.
     *
     * @param sources where plugins are published
     * @param artifactId the plugin name
     * @param productVersion the major.minor that Fess accepts, for example {@code 15.9}
     * @param development whether this is a snapshot build of Fess
     * @return the version to install
     * @throws SetupException if no repository could be read or none holds a matching version
     */
    static String resolveVersion(final PluginSources sources, final String artifactId, final String productVersion,
            final boolean development) throws SetupException {
        final List<String> problems = new ArrayList<>();
        final List<String> versions = new ArrayList<>();
        if (sources.hasRelease()) {
            versions.addAll(readVersions(sources.release(), artifactId, problems));
        } else {
            problems.add("no release repository is configured");
        }
        if (development) {
            if (sources.hasSnapshot()) {
                versions.addAll(readVersions(sources.snapshot(), artifactId, problems));
            } else {
                problems.add("no snapshot repository is configured");
            }
        }
        if (versions.isEmpty()) {
            throw new SetupException("Failed to read the published versions of " + artifactId + ": " + String.join("; ", problems));
        }
        return PluginRepository.selectVersion(versions, productVersion, development);
    }

    /**
     * Reads the versions one repository publishes, keeping the reason when it has none.
     *
     * <p>A miss is not a failure while another repository is still to be read: a plugin is
     * routinely absent from the release repository before its line ships, and absent from the
     * snapshot repository once it stops being built there.</p>
     *
     * @param repository the repository URL
     * @param artifactId the plugin name
     * @param problems collects the reason a repository returned nothing
     * @return the versions, empty when the metadata cannot be read
     */
    private static List<String> readVersions(final String repository, final String artifactId, final List<String> problems) {
        try {
            return PluginRepository
                    .versionsFromMetadata(Downloader.readString(URI.create(PluginRepository.metadataUrl(repository, artifactId))));
        } catch (final SetupException e) {
            problems.add(e.getMessage());
            return List.of();
        }
    }

    /**
     * Returns where plugins are published.
     *
     * <p>{@code --repository} replaces every source rather than only the release one: it names
     * the repository the caller wants the plugins to come from -- a mirror, or a group
     * repository that serves both trees -- and honouring it for the version list while still
     * downloading the jar from github.com would defeat the point of naming it.</p>
     *
     * @param definition the plugin definition
     * @param options the command line options
     * @return the sources
     */
    static PluginSources pluginSources(final ComponentDefinition definition, final Map<String, String> options) {
        final String named = options.get("repository");
        if (named != null) {
            return new PluginSources(named, named, "");
        }
        return new PluginSources(definition.get("repository"), definition.get("snapshot.repository"), definition.get("github"));
    }

    /**
     * Returns the directory Fess loads plugins from, which {@code --dest} overrides.
     *
     * @param definition the plugin definition
     * @param options the command line options
     * @return the plugin directory
     */
    private static Path pluginDirectory(final ComponentDefinition definition, final Map<String, String> options) {
        final String dest = options.get("dest");
        if (dest != null) {
            return Path.of(dest);
        }
        final String configured = definition.resolve("dest", Map.of("fess.home", fessHome()));
        return configured != null ? Path.of(configured) : FessPluginInstaller.directory(fessHome());
    }

    /**
     * Returns this build's version, which the jar's manifest carries.
     *
     * <p>Empty when there is no manifest, which is how the class runs under test and from an
     * IDE. {@link FessPluginInstaller#productVersion} then reports that {@code --version} is
     * needed rather than guessing one.</p>
     *
     * @return the version, or an empty string
     */
    static String fessVersion() {
        final String version = FessSetup.class.getPackage().getImplementationVersion();
        return version == null ? "" : version;
    }

    /**
     * Collects the arguments that are neither an option nor an option's value.
     *
     * @param args the command line
     * @param from the index to start at
     * @return the positional arguments, in order
     */
    static List<String> positionals(final String[] args, final int from) {
        final List<String> values = new ArrayList<>();
        for (int i = from; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                    i++;
                }
                continue;
            }
            values.add(args[i]);
        }
        return values;
    }

    /**
     * Parses {@code --key value} and {@code --flag} arguments.
     *
     * @param args the command line
     * @param from the index to start at
     * @return the options, in the order they appeared
     */
    static Map<String, String> parseOptions(final String[] args, final int from) {
        final Map<String, String> options = new LinkedHashMap<>();
        for (int i = from; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                final String key = args[i].substring(2);
                final String value = i + 1 < args.length && !args[i + 1].startsWith("--") ? args[++i] : "true";
                options.put(key, value);
            }
        }
        return options;
    }

    private static int installComponent(final ComponentDefinition definition, final Map<String, String> options, final PrintStream out,
            final PrintStream err) throws SetupException {
        final Platform.Os os = Platform.current();
        final String osToken = definition.osToken(os);
        if (osToken == null) {
            err.println("error: " + definition.name() + " publishes no official build for this platform.");
            final String hint = definition.get("hint.unavailable");
            if (hint != null) {
                hint.lines().forEach(err::println);
            }
            return EXIT_FAILED;
        }
        final String arch = Platform.archFrom(System.getProperty("os.arch"));
        if (arch == null) {
            err.println("error: unsupported CPU architecture: " + System.getProperty("os.arch"));
            return EXIT_FAILED;
        }
        final String version = options.getOrDefault("version", definition.get("version"));
        final Map<String, String> vars = new HashMap<>();
        vars.put("os", osToken);
        vars.put("arch", arch);
        vars.put("archive.ext", Platform.archiveExt(os));
        vars.put("version", version);
        vars.put("fess.home", fessHome());

        final String url = definition.resolve("url", vars);
        final Path dest = Path.of(options.getOrDefault("dest", definition.resolve("dest", vars)));
        final Path home = dest.resolve(definition.resolve("dir", vars));

        if (Files.isDirectory(home)) {
            out.println(home + " already exists, skipping the download.");
        } else {
            final Path archive = dest.resolve(url.substring(url.lastIndexOf('/') + 1));
            out.println("Downloading " + url);
            final int[] lastPercent = { -1 };
            Downloader.download(URI.create(url), archive, (bytes, total) -> reportProgress(out, bytes, total, lastPercent));
            out.println();
            out.println("Extracting " + archive.getFileName());
            Archiver.extract(archive, dest);
        }

        if (POST_OPENSEARCH.equals(definition.get("post"))) {
            installPlugins(definition, home, options, out);
            out.println("Configuring " + home);
            OpenSearchConfigurer.configure(home);
            out.println();
            out.println("Done. Start OpenSearch, then tell Fess where it is:");
            out.println("  SEARCH_ENGINE_HTTP_URL=http://localhost:9200");
            out.println("  FESS_DICTIONARY_PATH=" + OpenSearchConfigurer.dictionaryPath(home));
            return EXIT_OK;
        }

        out.println();
        out.println("Done. Installed to " + home);
        reportExecutable(definition, home, os, out);
        return EXIT_OK;
    }

    /**
     * Prints how to point Fess at a component's executable, when the definition names one.
     *
     * @param definition the component
     * @param home the extracted directory
     * @param os the operating system
     * @param out the stream for normal output
     */
    private static void reportExecutable(final ComponentDefinition definition, final Path home, final Platform.Os os,
            final PrintStream out) {
        final String executable = definition.executable(os);
        final String envName = definition.get("env");
        if (executable == null || envName == null) {
            return;
        }
        final Path path = home.resolve(executable).toAbsolutePath().normalize();
        final Path fessHomePath = Path.of(fessHome()).toAbsolutePath().normalize();
        out.println();
        if (path.startsWith(fessHomePath)) {
            out.println("bin/fess.in.sh finds this on its own, so there is nothing else to do.");
            out.println("  " + envName + "=" + path);
        } else {
            out.println("It is outside the Fess directory, so bin/fess.in.sh will not find it. Add this");
            out.println("to bin/fess.in.sh (bin\\fess.in.bat on Windows) so Fess and its crawler processes can:");
            out.println("  export " + envName + "=" + path);
        }
    }

    private static void installPlugins(final ComponentDefinition definition, final Path home, final Map<String, String> options,
            final PrintStream out) throws SetupException {
        final String repository = definition.get("plugin.repository");
        final String version = options.getOrDefault("version", definition.get("plugin.version"));
        final List<String> artifacts = definition.list("plugin.artifacts");
        for (final String coordinate : artifacts) {
            final String url = OpenSearchPluginInstaller.zipUrl(repository, coordinate, version);
            out.println("Installing " + coordinate + ":" + version);
            OpenSearchPluginInstaller.install(home, url);
        }
    }

    /**
     * Returns the Fess installation directory, which the launcher passes in.
     *
     * <p>Falls back to the working directory so the jar stays usable when invoked directly with
     * {@code java -jar}, but the launcher always sets it: the default destinations are written
     * relative to it, and bin/fess.in.sh looks for an installed Node.js under it.</p>
     *
     * @return the Fess home directory
     */
    static String fessHome() {
        final String home = System.getProperty("fess.home");
        return home == null || home.isBlank() ? Path.of("").toAbsolutePath().toString() : home;
    }

    /**
     * Reports download progress, one line rewrite per completed percent.
     *
     * <p>The carriage return keeps a terminal on one line. Redrawing on every buffer instead
     * would emit tens of thousands of updates for a gigabyte, which is invisible on a terminal
     * but fills a log file when the output is redirected.</p>
     *
     * @param out the stream for normal output
     * @param bytes bytes written so far
     * @param total the total size, or -1 when unknown
     * @param lastPercent single-element holder of the last percentage reported
     */
    private static void reportProgress(final PrintStream out, final long bytes, final long total, final int[] lastPercent) {
        if (total > 0) {
            final int percent = (int) (bytes * 100 / total);
            if (percent == lastPercent[0]) {
                return;
            }
            lastPercent[0] = percent;
            out.printf("\r  %d%% (%s)", percent, sizes(bytes, total));
        } else {
            final int megabytes = (int) (bytes >> 20);
            if (megabytes == lastPercent[0]) {
                return;
            }
            lastPercent[0] = megabytes;
            out.printf("\r  %s", size(bytes));
        }
    }

    /**
     * Formats a byte count in the largest unit that does not round it to zero.
     *
     * @param bytes the count
     * @return the formatted size
     */
    static String size(final long bytes) {
        if (bytes >= 1L << 20) {
            return (bytes >> 20) + " MiB";
        }
        if (bytes >= 1L << 10) {
            return (bytes >> 10) + " KiB";
        }
        return bytes + " B";
    }

    /**
     * Formats a progress pair, both figures in the unit that suits the total.
     *
     * <p>Choosing the unit per figure would print "0 B/9 KiB" at the start of a small download,
     * and choosing MiB always -- which is what the OpenSearch bundle needs -- prints "0/0 MiB"
     * for a plugin jar of a few kilobytes.</p>
     *
     * @param bytes bytes so far
     * @param total the total size
     * @return the formatted pair
     */
    static String sizes(final long bytes, final long total) {
        if (total >= 1L << 20) {
            return (bytes >> 20) + "/" + (total >> 20) + " MiB";
        }
        if (total >= 1L << 10) {
            return (bytes >> 10) + "/" + (total >> 10) + " KiB";
        }
        return bytes + "/" + total + " B";
    }
}
