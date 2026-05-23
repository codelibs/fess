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

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ResourceUtil;
import org.lastaflute.web.util.LaServletContextUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * Registry of themes available to the running Fess instance.
 *
 * <p>Loads static themes from the directory configured via
 * {@code theme.directory.path} (default {@code themes/}) and complements the
 * snapshot with legacy JSP themes discovered under {@code WEB-INF/view/}.
 * Static themes take precedence over JSP themes that share the same name.</p>
 *
 * <p>{@link #reload()} produces an immutable snapshot under a synchronized
 * lock; readers ({@link #getTheme(String)}, {@link #getAllThemes()},
 * {@link #resolveActiveTheme(String)}) observe the latest snapshot without
 * locking.</p>
 */
public class ThemeRegistry {

    private static final Logger logger = LogManager.getLogger(ThemeRegistry.class);

    /** System property key for the global default theme name. */
    public static final String SYSPROP_DEFAULT_THEME = "theme.default";

    @Resource
    protected FessConfig fessConfig;

    /** Immutable snapshot of themes + the resolved default theme name. */
    private static final class Snapshot {
        final Map<String, Theme> byName;
        final String defaultThemeName;

        Snapshot(final Map<String, Theme> byName, final String defaultThemeName) {
            this.byName = byName;
            this.defaultThemeName = defaultThemeName;
        }
    }

    private volatile Snapshot snapshot = new Snapshot(Map.of(), null);
    private Path themesDirOverride; // test seam

    @PostConstruct
    public void init() {
        try {
            reload();
        } catch (final Exception e) {
            logger.warn("Failed to load themes at startup", e);
        }
    }

    public synchronized void reload() {
        final Map<String, Theme> next = new HashMap<>();
        scanStatic(next);
        scanJsp(next);
        final Map<String, Theme> immutable = Collections.unmodifiableMap(next);
        final String defaultThemeName = lookupDefaultThemeName();
        snapshot = new Snapshot(immutable, defaultThemeName);
        if (logger.isInfoEnabled()) {
            logger.info("ThemeRegistry reloaded; {} themes registered", immutable.size());
        }
    }

    private void scanStatic(final Map<String, Theme> next) {
        final Path base = resolveThemesDir();
        if (base == null || !Files.isDirectory(base)) {
            return;
        }
        try (Stream<Path> entries = Files.list(base)) {
            entries.filter(Files::isDirectory).forEach(dir -> {
                final String name = dir.getFileName().toString();
                if (name.startsWith(".")) {
                    return;
                }
                final Path manifest = dir.resolve("theme.yml");
                if (!Files.isRegularFile(manifest)) {
                    return;
                }
                try (InputStream in = Files.newInputStream(manifest)) {
                    final ThemeManifest m = ThemeManifest.parse(in);
                    if (!name.equals(m.getName())) {
                        logger.warn("Theme dir '{}' name mismatch with manifest '{}'; skipping", name, m.getName());
                        return;
                    }
                    next.put(name, new Theme(ThemeType.STATIC, name, dir, m));
                } catch (final Exception e) {
                    logger.warn("Skipping malformed theme at {}: {}", dir, e.getMessage(), e);
                }
            });
        } catch (final Exception e) {
            logger.warn("Failed to scan static themes at {}", base, e);
        }
    }

    private void scanJsp(final Map<String, Theme> next) {
        final Path viewBase;
        try {
            viewBase = ResourceUtil.getViewTemplatePath();
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("View template path unavailable; skipping JSP theme scan", e);
            }
            return;
        }
        if (viewBase == null || !Files.isDirectory(viewBase)) {
            return;
        }
        try (Stream<Path> entries = Files.list(viewBase)) {
            entries.filter(Files::isDirectory).forEach(dir -> {
                final String name = dir.getFileName().toString();
                if (next.containsKey(name)) {
                    return; // static takes precedence
                }
                if ("admin".equals(name) || "common".equals(name) || "error".equals(name)) {
                    return;
                }
                next.put(name, new Theme(ThemeType.JSP, name, dir, null));
            });
        } catch (final Exception e) {
            logger.warn("Failed to scan JSP themes", e);
        }
    }

    private Path resolveThemesDir() {
        if (themesDirOverride != null) {
            return themesDirOverride;
        }
        final String configured = fessConfig == null ? "themes" : fessConfig.getThemeDirectoryPath();
        final Path p = Paths.get(configured);
        if (p.isAbsolute()) {
            return p;
        }
        try {
            final String realPath = LaServletContextUtil.getServletContext().getRealPath("/" + configured);
            if (realPath != null) {
                return Paths.get(realPath);
            }
        } catch (final Exception ignore) {
            // servlet context not available (unit test)
        }
        return p;
    }

    public Optional<Theme> getTheme(final String name) {
        if (StringUtil.isBlank(name)) {
            return Optional.empty();
        }
        return Optional.ofNullable(snapshot.byName.get(name));
    }

    public Map<String, Theme> getAllThemes() {
        return snapshot.byName;
    }

    /**
     * Resolves the theme to apply for the current request.
     *
     * <p>Selection order:</p>
     * <ol>
     *   <li>If {@code virtualHostKey} resolves to a known theme, use it.</li>
     *   <li>Otherwise fall back to the global default theme stored under the
     *       {@code theme.default} system property.</li>
     *   <li>Return empty when neither lookup succeeds.</li>
     * </ol>
     *
     * @param virtualHostKey theme name derived from the request's virtual host
     *        (may be {@code null} or blank)
     * @return the resolved theme, or empty if none configured
     */
    public Optional<Theme> resolveActiveTheme(final String virtualHostKey) {
        final Snapshot snap = snapshot;
        if (StringUtil.isNotBlank(virtualHostKey)) {
            final String key = virtualHostKey.toLowerCase(Locale.ROOT);
            final Theme t = snap.byName.get(key);
            if (t != null) {
                return Optional.of(t);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Virtual host key '{}' did not resolve to a known theme; falling back", virtualHostKey);
            }
        }
        final String def = snap.defaultThemeName;
        if (StringUtil.isNotBlank(def)) {
            return Optional.ofNullable(snap.byName.get(def));
        }
        return Optional.empty();
    }

    private String lookupDefaultThemeName() {
        if (fessConfig == null) {
            return null;
        }
        try {
            return fessConfig.getSystemProperty(SYSPROP_DEFAULT_THEME, null);
        } catch (final Exception ignore) {
            return null;
        }
    }

    // ---- Test seams ----
    void setThemesDirOverride(final Path p) {
        this.themesDirOverride = p;
    }

    /**
     * Inserts (or replaces) a single theme entry in the current snapshot
     * without scanning the filesystem. Intended for unit tests that need to
     * exercise type-specific branches (e.g. JSP-vs-static deletion guards)
     * without materialising real fixtures.
     */
    synchronized void injectThemeForTest(final Theme theme) {
        final Map<String, Theme> next = new HashMap<>(snapshot.byName);
        next.put(theme.getName(), theme);
        snapshot = new Snapshot(Collections.unmodifiableMap(next), snapshot.defaultThemeName);
    }
}
