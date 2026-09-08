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

/**
 * Installs OpenSearch plugins through the distribution's own {@code opensearch-plugin} tool.
 *
 * <p>Plugins are passed as zip URLs rather than Maven coordinates. The coordinate form requires
 * a direct connection to Maven Central and fails behind a proxy or an internal repository, and
 * the CodeLibs plugins are not always published there -- 3.8.1 is absent from both Central and
 * maven.codelibs.org at the time of writing -- so the URL has to stay overridable.</p>
 */
public final class PluginInstaller {

    private PluginInstaller() {
    }

    /**
     * Builds the download URL of a plugin zip in a Maven repository layout.
     *
     * @param repository the repository base URL
     * @param coordinate the plugin as {@code groupId:artifactId}
     * @param version the plugin version
     * @return the zip URL
     * @throws SetupException if the coordinate is malformed
     */
    public static String zipUrl(final String repository, final String coordinate, final String version) throws SetupException {
        final int separator = coordinate.indexOf(':');
        if (separator <= 0 || separator == coordinate.length() - 1) {
            throw new SetupException("Malformed plugin coordinate, expected groupId:artifactId but got: " + coordinate);
        }
        final String groupPath = coordinate.substring(0, separator).replace('.', '/');
        final String artifactId = coordinate.substring(separator + 1);
        final String base = repository.endsWith("/") ? repository.substring(0, repository.length() - 1) : repository;
        return base + "/" + groupPath + "/" + artifactId + "/" + version + "/" + artifactId + "-" + version + ".zip";
    }

    /**
     * Returns the {@code opensearch-plugin} launcher path for the running platform.
     *
     * @param opensearchHome the OpenSearch home directory
     * @return the launcher path
     */
    public static Path pluginCommand(final Path opensearchHome) {
        return pluginCommand(opensearchHome, Platform.current());
    }

    /**
     * Returns the {@code opensearch-plugin} launcher path for the given platform.
     *
     * @param opensearchHome the OpenSearch home directory
     * @param os the operating system
     * @return the launcher path
     */
    public static Path pluginCommand(final Path opensearchHome, final Platform.Os os) {
        final String name = os == Platform.Os.WINDOWS ? "opensearch-plugin.bat" : "opensearch-plugin";
        return opensearchHome.resolve("bin").resolve(name);
    }

    /**
     * Installs one plugin from a zip URL.
     *
     * <p>A plugin that is already installed is treated as success rather than a failure, so
     * running setup again on a working installation is harmless.</p>
     *
     * @param opensearchHome the OpenSearch home directory
     * @param url the plugin zip URL
     * @throws SetupException if the plugin tool is missing or reports an error
     */
    public static void install(final Path opensearchHome, final String url) throws SetupException {
        final Path command = pluginCommand(opensearchHome);
        if (!Files.isRegularFile(command)) {
            throw new SetupException("opensearch-plugin was not found at " + command
                    + ". Point --opensearch-home at an OpenSearch installation, or install OpenSearch first.");
        }
        final ProcessBuilder builder = new ProcessBuilder(command.toAbsolutePath().toString(), "install", "--batch", url);
        builder.redirectErrorStream(true);
        try {
            final Process process = builder.start();
            final String output = new String(process.getInputStream().readAllBytes());
            final int exit = process.waitFor();
            if (exit != 0) {
                if (output.contains("already exists")) {
                    return;
                }
                throw new SetupException("Failed to install " + url + ", opensearch-plugin exited with code " + exit + ": " + output);
            }
        } catch (final IOException e) {
            throw new SetupException("Failed to run " + command, e);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SetupException("Interrupted while installing " + url, e);
        }
    }
}
