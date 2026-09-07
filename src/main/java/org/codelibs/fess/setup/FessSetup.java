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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    private static final int EXIT_OK = 0;

    private static final int EXIT_FAILED = 1;

    private static final int EXIT_USAGE = 2;

    private static final String USAGE = """
            Usage: fess-setup <command> [options]

            Commands:
              install opensearch [--dest <dir>] [--version <version>]
                  Download OpenSearch, install the Fess plugins into it and configure it.
                  Official builds exist for Linux and Windows only.

              install plugins --opensearch-home <dir> [--version <version>]
                  Install the Fess plugins into an OpenSearch you already have.

              list
                  Show the components this build knows how to install.
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
            case "list" -> list(definitions, out);
            case "install" -> install(args, definitions, out, err);
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

    private static int list(final Map<String, ComponentDefinition> definitions, final PrintStream out) {
        definitions.forEach((name, definition) -> {
            final String version = definition.get("version");
            out.println(version == null ? name : name + "  " + version);
        });
        return EXIT_OK;
    }

    private static int install(final String[] args, final Map<String, ComponentDefinition> definitions, final PrintStream out,
            final PrintStream err) throws SetupException {
        if (args.length < 2) {
            err.println(USAGE);
            return EXIT_USAGE;
        }
        final String target = args[1];
        final Map<String, String> options = parseOptions(args, 2);
        if ("plugins".equals(target)) {
            final String home = options.get("opensearch-home");
            if (home == null) {
                err.println("error: install plugins requires --opensearch-home <dir>");
                err.println(USAGE);
                return EXIT_USAGE;
            }
            installPlugins(definitions.get("opensearch"), Path.of(home), options, out);
            return EXIT_OK;
        }
        final ComponentDefinition definition = definitions.get(target);
        if (definition == null) {
            err.println("error: unknown component: " + target);
            return EXIT_FAILED;
        }
        return installComponent(definition, options, out, err);
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
            final String url = PluginInstaller.zipUrl(repository, coordinate, version);
            out.println("Installing " + coordinate + ":" + version);
            PluginInstaller.install(home, url);
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
            out.printf("\r  %d%% (%d/%d MiB)", percent, bytes >> 20, total >> 20);
        } else {
            final int megabytes = (int) (bytes >> 20);
            if (megabytes == lastPercent[0]) {
                return;
            }
            lastPercent[0] = megabytes;
            out.printf("\r  %d MiB", megabytes);
        }
    }
}
