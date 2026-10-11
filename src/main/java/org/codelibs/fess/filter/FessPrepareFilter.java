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
package org.codelibs.fess.filter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.helper.PluginHelper.ArtifactType;
import org.codelibs.fess.mylasta.direction.sponsor.FessMessageResources;
import org.lastaflute.web.ruts.message.MessageResources;
import org.lastaflute.web.servlet.filter.LastaPrepareFilter;

import jakarta.servlet.ServletContext;

/**
 * The filter that prepares the LastaFlute container, with the message resources of Fess.
 *
 * @see FessMessageResources
 */
public class FessPrepareFilter extends LastaPrepareFilter {

    private static final Logger logger = LogManager.getLogger(FessPrepareFilter.class);

    /** A plugin jar name: the artifact id runs up to the first hyphen followed by a digit, where the version starts. */
    private static final Pattern PLUGIN_JAR = Pattern.compile("(.+?)-\\d.*\\.jar");

    /**
     * Creates the filter.
     */
    public FessPrepareFilter() {
        super();
    }

    /**
     * Initializes the container, after saying which plugin jars are installed twice.
     *
     * <p>Two versions of one plugin are both loaded, and when the older one comes first the
     * container fails to initialize with an error about a class of the plugin that never says
     * which jar it came from.</p>
     */
    @Override
    protected void initializeContainer(final ServletContext servletContext) {
        final String pluginDir = servletContext.getRealPath("/WEB-INF/plugin");
        if (pluginDir != null) {
            duplicatedPlugins(Path.of(pluginDir)).forEach(logger::error);
        }
        super.initializeContainer(servletContext);
    }

    /**
     * Describes each plugin that has more than one jar in the plugin directory.
     *
     * <p>This runs before the container, from a filter that is in the war, so it cannot use
     * {@code org.codelibs.fess.setup}: {@code packagingExcludes} in pom.xml keeps that package
     * out of the war. The plugin names follow the same rule as {@code FessPluginInstaller}.</p>
     *
     * @param directory the plugin directory
     * @return one message per duplicated plugin, empty when there is none or the directory cannot be read
     */
    static List<String> duplicatedPlugins(final Path directory) {
        final Map<String, List<String>> jarsByPlugin = new TreeMap<>();
        if (Files.isDirectory(directory)) {
            try (Stream<Path> files = Files.list(directory)) {
                files.filter(Files::isRegularFile).map(file -> file.getFileName().toString()).sorted().forEach(name -> {
                    final Matcher matcher = PLUGIN_JAR.matcher(name);
                    if (matcher.matches() && isPluginName(matcher.group(1))) {
                        jarsByPlugin.computeIfAbsent(matcher.group(1), k -> new ArrayList<>()).add(name);
                    }
                });
            } catch (final IOException | UncheckedIOException e) {
                logger.debug("Failed to check the plugin directory: {}", directory, e);
                return List.of();
            }
        }
        final List<String> messages = new ArrayList<>();
        jarsByPlugin.forEach((artifactId, jars) -> {
            if (jars.size() > 1) {
                messages.add("The plugin " + artifactId + " is installed more than once in " + directory + ": " + String.join(", ", jars)
                        + ". Keep one jar; fess-setup remove plugin " + artifactId + " removes them all, and fess-setup install plugin "
                        + artifactId + " installs the version of this release.");
            }
        });
        return messages;
    }

    private static boolean isPluginName(final String artifactId) {
        return Arrays.stream(ArtifactType.values())
                .filter(type -> type != ArtifactType.UNKNOWN)
                .anyMatch(type -> artifactId.startsWith(type.getId() + "-"));
    }

    @Override
    protected MessageResources newMessageResources() {
        return new FessMessageResources();
    }
}
