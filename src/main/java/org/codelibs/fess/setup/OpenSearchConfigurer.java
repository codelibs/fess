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
import java.util.List;

/**
 * Applies the OpenSearch-side configuration Fess needs.
 *
 * <p>Only {@code configsync.config_path} is written. It tells the configsync plugin where to
 * keep the dictionaries Fess pushes over {@code /_configsync/file}, and it has to agree with
 * the {@code FESS_DICTIONARY_PATH} Fess is started with: the analyzers in the index settings
 * carry that prefix in their {@code *_path} values, so a mismatch means the analyzer looks for
 * a file configsync wrote somewhere else, and index creation fails.</p>
 */
public final class OpenSearchConfigurer {

    private static final String CONFIGSYNC_KEY = "configsync.config_path";

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
     * Adds {@code configsync.config_path} to {@code opensearch.yml} and creates the dictionary
     * directory. Running it again on an already-configured installation changes nothing.
     *
     * @param opensearchHome the OpenSearch home directory
     * @throws SetupException if the configuration cannot be written
     */
    public static void configure(final Path opensearchHome) throws SetupException {
        final Path yml = opensearchHome.resolve("config").resolve("opensearch.yml");
        if (!Files.isRegularFile(yml)) {
            throw new SetupException("opensearch.yml was not found at " + yml + ". Is " + opensearchHome + " an OpenSearch home?");
        }
        final Path dictionary = dictionaryPath(opensearchHome);
        try {
            Files.createDirectories(dictionary);
            final List<String> lines = Files.readAllLines(yml, StandardCharsets.UTF_8);
            for (final String line : lines) {
                if (line.trim().startsWith(CONFIGSYNC_KEY)) {
                    return;
                }
            }
            final StringBuilder appended = new StringBuilder();
            if (!lines.isEmpty() && !lines.get(lines.size() - 1).isEmpty()) {
                appended.append(System.lineSeparator());
            }
            appended.append("# Added by fess-setup: where configsync keeps the dictionaries Fess pushes.")
                    .append(System.lineSeparator())
                    .append(CONFIGSYNC_KEY)
                    .append(": ")
                    .append(dictionary.toAbsolutePath().toString().replace('\\', '/'))
                    .append(System.lineSeparator());
            Files.writeString(yml, appended.toString(), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (final IOException e) {
            throw new SetupException("Failed to configure " + yml, e);
        }
    }
}
