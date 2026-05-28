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
 * Immutable value object describing a registered static theme.
 *
 * <p>A {@code Theme} is backed by a {@code theme.yml} manifest under the
 * configured themes directory. The {@link #getManifest() manifest} may be
 * absent when unit tests construct synthetic themes without a real bundle.</p>
 */
public final class Theme {

    private final String name;
    private final Path basePath;
    private final ThemeManifest manifest;

    /**
     * Constructs a new theme descriptor.
     *
     * @param name theme directory name
     * @param basePath filesystem location of the theme bundle
     * @param manifest parsed manifest, or {@code null} for test fixtures without one
     */
    public Theme(final String name, final Path basePath, final ThemeManifest manifest) {
        this.name = Objects.requireNonNull(name);
        this.basePath = Objects.requireNonNull(basePath);
        this.manifest = manifest;
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
     * Returns the parsed manifest, if any.
     *
     * @return the optional manifest
     */
    public Optional<ThemeManifest> getManifest() {
        return Optional.ofNullable(manifest);
    }
}
