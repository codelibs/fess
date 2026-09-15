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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Applies the OpenSearch-side configuration Fess needs.
 *
 * <p>Two settings are appended to {@code opensearch.yml}, each only when the file does not
 * have it yet:</p>
 * <ul>
 * <li>{@code configsync.config_path} tells the configsync plugin where to keep the dictionaries
 * Fess pushes over {@code /_configsync/file}. It has to agree with the
 * {@code FESS_DICTIONARY_PATH} Fess is started with: the analyzers in the index settings carry
 * that prefix in their {@code *_path} values, so a mismatch means the analyzer looks for a file
 * configsync wrote somewhere else, and index creation fails.</li>
 * <li>{@code plugins.security.disabled: true}, because the OpenSearch bundle ships the security
 * plugin enabled and a node without the TLS material it expects refuses to start
 * ({@code No SSL configuration found}). Fess talks plain HTTP to {@code localhost:9200} by
 * default, and OpenSearch binds to the loopback address unless {@code network.host} says
 * otherwise. The setting is not added when the file already has any
 * {@code plugins.security.*} setting: an explicit {@code plugins.security.disabled: false}, or a
 * security plugin someone has configured, is left as it is.</li>
 * </ul>
 *
 * <p>A setting counts as present when a line starts with its flat key ({@code a.b.c:}), the form
 * OpenSearch's own tools write; commented-out lines do not count.</p>
 */
public final class OpenSearchConfigurer {

    private static final String CONFIGSYNC_KEY = "configsync.config_path";

    private static final String SECURITY_PREFIX = "plugins.security.";

    private static final String SECURITY_DISABLED = "plugins.security.disabled: true";

    private OpenSearchConfigurer() {
    }

    /**
     * Returns the dictionary directory of an OpenSearch installation. This is the value Fess
     * must be given as {@code FESS_DICTIONARY_PATH}.
     *
     * @param opensearchHome the OpenSearch home directory
     * @return the dictionary directory
     */
    public static Path dictionaryPath(final Path opensearchHome) {
        return opensearchHome.resolve("config").resolve("dictionary");
    }

    /**
     * Returns the dictionary directory as it is written to {@code configsync.config_path}:
     * absolute, with forward slashes. {@code FESS_DICTIONARY_PATH} needs the same spelling, since
     * Fess substitutes it into the index settings with {@code String#replaceAll}, which drops a
     * backslash.
     *
     * @param opensearchHome the OpenSearch home directory
     * @return the dictionary directory as a setting value
     */
    public static String dictionaryPathValue(final Path opensearchHome) {
        return dictionaryPath(opensearchHome).toAbsolutePath().toString().replace('\\', '/');
    }

    /**
     * Adds the settings Fess needs to {@code opensearch.yml} and creates the dictionary
     * directory. Running it again on an already-configured installation changes nothing.
     *
     * @param opensearchHome the OpenSearch home directory
     * @return the settings appended, as written; empty when the file already had them all
     * @throws SetupException if the configuration cannot be written
     */
    public static List<String> configure(final Path opensearchHome) throws SetupException {
        final Path yml = opensearchHome.resolve("config").resolve("opensearch.yml");
        if (!Files.isRegularFile(yml)) {
            throw new SetupException("opensearch.yml was not found at " + yml + ". Is " + opensearchHome + " an OpenSearch home?");
        }
        try {
            Files.createDirectories(dictionaryPath(opensearchHome));
            final List<String> lines = Files.readAllLines(yml, StandardCharsets.UTF_8);
            final List<String> added = new ArrayList<>();
            final StringBuilder appended = new StringBuilder();
            if (!hasSetting(lines, CONFIGSYNC_KEY)) {
                final String setting = CONFIGSYNC_KEY + ": " + dictionaryPathValue(opensearchHome);
                append(appended, "where configsync keeps the dictionaries Fess pushes.", setting);
                added.add(setting);
            }
            if (!hasSetting(lines, SECURITY_PREFIX)) {
                append(appended, "Fess connects over plain HTTP. Configure the security plugin instead"
                        + " before OpenSearch listens on anything but localhost.", SECURITY_DISABLED);
                added.add(SECURITY_DISABLED);
            }
            if (added.isEmpty()) {
                return added;
            }
            if (!lines.isEmpty() && !lines.get(lines.size() - 1).isEmpty()) {
                appended.insert(0, System.lineSeparator());
            }
            Files.writeString(yml, appended.toString(), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            return added;
        } catch (final IOException e) {
            throw new SetupException("Failed to configure " + yml, e);
        }
    }

    /**
     * Tells whether {@code bin/fess.in.sh} ({@code bin\fess.in.bat} on Windows) derives
     * {@code FESS_DICTIONARY_PATH} for this installation without being told. The launcher looks
     * for {@code <fess home>/opensearch/<dir>/config/dictionary} and uses it only when exactly
     * one directory there has one: with two, it cannot know which of them Fess talks to.
     *
     * @param fessHome the Fess home directory
     * @param opensearchHome the OpenSearch home directory
     * @return true if Fess finds the dictionary directory on its own
     * @throws SetupException if the directory holding the installations cannot be listed
     */
    public static boolean isFoundByLauncher(final Path fessHome, final Path opensearchHome) throws SetupException {
        final Path parent = fessHome.toAbsolutePath().normalize().resolve("opensearch");
        final Path home = opensearchHome.toAbsolutePath().normalize();
        if (!parent.equals(home.getParent()) || !Files.isDirectory(dictionaryPath(home))) {
            return false;
        }
        try (Stream<Path> children = Files.list(parent)) {
            return children.filter(child -> Files.isDirectory(dictionaryPath(child))).count() == 1;
        } catch (final IOException e) {
            throw new SetupException("Failed to list " + parent, e);
        }
    }

    private static boolean hasSetting(final List<String> lines, final String key) {
        return lines.stream().anyMatch(line -> line.trim().startsWith(key));
    }

    private static void append(final StringBuilder appended, final String comment, final String setting) {
        appended.append("# Added by fess-setup: ")
                .append(comment)
                .append(System.lineSeparator())
                .append(setting)
                .append(System.lineSeparator());
    }
}
