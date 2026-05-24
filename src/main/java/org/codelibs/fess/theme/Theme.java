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
package org.codelibs.fess.theme;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable value object describing a registered theme.
 *
 * <p>A {@code Theme} is either a {@link ThemeType#STATIC static theme} backed by
 * a {@code theme.yml} manifest under the configured themes directory, or a
 * legacy {@link ThemeType#JSP JSP theme} consisting of JSP fragments under
 * {@code WEB-INF/view/}. The {@link #getManifest() manifest} is present only
 * for static themes.</p>
 */
public final class Theme {

    private final ThemeType type;
    private final String name;
    private final Path basePath;
    private final ThemeManifest manifest; // null for JSP themes

    /**
     * Constructs a new theme descriptor.
     *
     * @param type theme type (static or JSP)
     * @param name theme directory name
     * @param basePath filesystem location of the theme bundle
     * @param manifest parsed manifest for static themes, or {@code null} for JSP themes
     */
    public Theme(final ThemeType type, final String name, final Path basePath, final ThemeManifest manifest) {
        this.type = Objects.requireNonNull(type);
        this.name = Objects.requireNonNull(name);
        this.basePath = Objects.requireNonNull(basePath);
        this.manifest = manifest;
    }

    /**
     * Returns the theme type.
     *
     * @return the theme type
     */
    public ThemeType getType() {
        return type;
    }

    /**
     * Returns the theme name.
     *
     * @return the theme name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the filesystem base path of the theme bundle.
     *
     * @return the base path
     */
    public Path getBasePath() {
        return basePath;
    }

    /**
     * Returns the parsed manifest, empty for JSP themes.
     *
     * @return the optional manifest
     */
    public Optional<ThemeManifest> getManifest() {
        return Optional.ofNullable(manifest);
    }

    /**
     * Returns {@code true} when this theme is a static theme.
     *
     * @return {@code true} for static themes
     */
    public boolean isStatic() {
        return type == ThemeType.STATIC;
    }
}
