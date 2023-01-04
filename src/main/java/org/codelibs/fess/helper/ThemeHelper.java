/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.helper;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.fess.exception.ThemeException;
import org.codelibs.fess.helper.PluginHelper.Artifact;
import org.codelibs.fess.helper.PluginHelper.ArtifactType;
import org.codelibs.fess.util.ResourceUtil;

public class ThemeHelper {
    private static final Logger logger = LogManager.getLogger(ThemeHelper.class);

    public void install(final Artifact artifact) {
        final Path jarPath = getJarFile(artifact);
        final String themeName = getThemeName(artifact);
        if (logger.isDebugEnabled()) {
            logger.debug("Theme: {}", themeName);
        }
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(jarPath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    final String[] names = StreamUtil.split(entry.getName(), "/")
                            .get(stream -> stream.filter(s -> !"..".equals(s)).toArray(n -> new String[n]));
                    if (names.length < 2) {
                        continue;
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("Loading {}", entry.getName());
                    }
                    if ("view".equals(names[0])) {
                        names[0] = themeName;
                        final Path path = ResourceUtil.getViewTemplatePath(names);
                        Files.createDirectories(path.getParent());
                        Files.copy(zis, path, StandardCopyOption.REPLACE_EXISTING);
                    } else if ("css".equals(names[0])) {
                        names[0] = themeName;
                        final Path path = ResourceUtil.getCssPath(names);
                        Files.createDirectories(path.getParent());
                        Files.copy(zis, path, StandardCopyOption.REPLACE_EXISTING);
                    } else if ("js".equals(names[0])) {
                        names[0] = themeName;
                        final Path path = ResourceUtil.getJavaScriptPath(names);
                        Files.createDirectories(path.getParent());
                        Files.copy(zis, path, StandardCopyOption.REPLACE_EXISTING);
                    } else if ("images".equals(names[0])) {
                        names[0] = themeName;
                        final Path path = ResourceUtil.getImagePath(names);
                        Files.createDirectories(path.getParent());
                        Files.copy(zis, path, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        } catch (final IOException e) {
            throw new ThemeException("Failed to install " + artifact, e);
        }
    }

    public void uninstall(final Artifact artifact) {
        final String themeName = getThemeName(artifact);

        final Path viewPath = ResourceUtil.getViewTemplatePath(themeName);
        closeQuietly(viewPath);
        final Path imagePath = ResourceUtil.getImagePath(themeName);
        closeQuietly(imagePath);
        final Path cssPath = ResourceUtil.getCssPath(themeName);
        closeQuietly(cssPath);
        final Path jsPath = ResourceUtil.getJavaScriptPath(themeName);
        closeQuietly(jsPath);
    }

    protected String getThemeName(final Artifact artifact) {
        final String themeName = artifact.getName().substring(ArtifactType.THEME.getId().length() + 1);
        if (StringUtil.isBlank(themeName)) {
            throw new ThemeException("Theme name is empty: " + artifact);
        }
        return themeName;
    }

    protected void closeQuietly(final Path dir) {
        if (Files.notExists(dir)) {
            if (logger.isDebugEnabled()) {
                logger.debug("{} does not exists.", dir);
            }
            return;
        }
        try (Stream<Path> walk = Files.walk(dir, FileVisitOption.FOLLOW_LINKS)) {
            walk.sorted(Comparator.reverseOrder()).forEach(f -> {
                if (logger.isDebugEnabled()) {
                    logger.debug("Deleting {}", f);
                }
                try {
                    Files.delete(f);
                } catch (final IOException e) {
                    logger.warn("Failed to delete {}", f, e);
                }
            });
            Files.deleteIfExists(dir);
        } catch (final IOException e) {
            logger.warn("Failed to delete {}", dir, e);
        }
    }

    protected Path getJarFile(final Artifact artifact) {
        final Path jarPath = ResourceUtil.getPluginPath(artifact.getFileName());
        if (!Files.exists(jarPath)) {
            throw new ThemeException(artifact.getFileName() + " does not exist.");
        }
        return jarPath;
    }

}
