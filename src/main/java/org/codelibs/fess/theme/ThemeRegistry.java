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
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.lastaflute.web.util.LaServletContextUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * Registry of static themes available to the running Fess instance.
 *
 * <p>Loads themes from the directory configured via
 * {@code theme.directory.path} (default {@code themes/}). Legacy JSP themes
 * installed as plugins are rendered via LastaFlute's view resolution and the
 * {@link org.codelibs.fess.helper.VirtualHostHelper}; they are intentionally
 * not tracked here.</p>
 *
 * <p>{@link #reload()} produces an immutable snapshot under a synchronized
 * lock; readers ({@link #getTheme(String)}, {@link #getAllThemes()},
 * {@link #resolveActiveTheme(String)}) observe the latest snapshot without
 * locking.</p>
 */
public class ThemeRegistry {

    /**
     * Default constructor. The registry is wired by the DI container; static
     * themes are loaded lazily via {@link #init()}.
     */
    public ThemeRegistry() {
        // default constructor for DI
    }

    private static final Logger logger = LogManager.getLogger(ThemeRegistry.class);

    /** System property key for the global default theme name. */
    public static final String SYSPROP_DEFAULT_THEME = "theme.default";

    /** Injected Fess configuration used to resolve the themes directory and default theme. */
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

    /**
     * Latches on the first servlet-context lookup failure inside
     * {@link #resolveThemesDir()} so operators see a single WARN instead of one per
     * reload when the container is not yet ready (or has been torn down).
     * Subsequent failures degrade to DEBUG. Instance field (not static) so a
     * redeploy re-emits the WARN.
     */
    private final AtomicBoolean themesDirFirstFailure = new AtomicBoolean(false);

    /**
     * Latches on the first system-property lookup failure inside
     * {@link #lookupDefaultThemeName()} so operators see a single WARN instead of
     * one per reload when the config layer is unavailable. Subsequent failures
     * degrade to DEBUG.
     */
    private final AtomicBoolean defaultThemeFirstFailure = new AtomicBoolean(false);

    /**
     * Initialises the registry by performing the first scan. Failures are
     * logged and swallowed so a misconfigured themes directory does not break
     * application start-up.
     */
    @PostConstruct
    public void init() {
        try {
            reload();
        } catch (final Exception e) {
            logger.warn("Failed to load themes at startup", e);
        }
    }

    /**
     * Rescans the themes directory, replacing the in-memory snapshot atomically.
     */
    public synchronized void reload() {
        final Map<String, Theme> next = new HashMap<>();
        scanStatic(next);
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
            // NOFOLLOW_LINKS: a symlinked directory could point outside the themes root,
            // violating the sandbox invariant for static-theme assets.
            entries.filter(p -> Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS)).forEach(dir -> {
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
                    next.put(name, new Theme(name, dir, m));
                } catch (final Exception e) {
                    logger.warn("Skipping malformed theme at {}: {}", dir, e.getMessage(), e);
                }
            });
        } catch (final Exception e) {
            logger.warn("Failed to scan static themes at {}", base, e);
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
        } catch (final Exception e) {
            // Servlet context not available (e.g. unit tests, very early startup, or
            // post-shutdown). Warn once so operators notice a real misconfiguration
            // but avoid flooding the log on every reload.
            if (themesDirFirstFailure.compareAndSet(false, true)) {
                logger.warn("Servlet context unavailable while resolving themes dir; falling back to configured path={}", configured, e);
            } else if (logger.isDebugEnabled()) {
                logger.debug("Servlet context unavailable while resolving themes dir; configured={}", configured, e);
            }
        }
        return p;
    }

    /**
     * Looks up a theme by name.
     *
     * @param name theme name (blank values yield empty)
     * @return the matching theme, or empty when no theme with that name is registered
     */
    public Optional<Theme> getTheme(final String name) {
        if (StringUtil.isBlank(name)) {
            return Optional.empty();
        }
        return Optional.ofNullable(snapshot.byName.get(name));
    }

    /**
     * Returns an immutable view of every registered theme keyed by name.
     *
     * @return unmodifiable map of registered themes
     */
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
            // Theme names are enforced lowercase by NAME_PATTERN (^[a-z0-9][a-z0-9_-]{0,63}$);
            // the lowercase here is for resilience against case-preserving virtual-host
            // configurations that may supply mixed-case keys.
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
        } catch (final Exception e) {
            // System-property lookup can fail when the config layer is mid-init or
            // the system-properties index is unreachable. Warn once so the
            // condition is visible without spamming the log on every reload.
            if (defaultThemeFirstFailure.compareAndSet(false, true)) {
                logger.warn("Failed to read default theme system property; key={}", SYSPROP_DEFAULT_THEME, e);
            } else if (logger.isDebugEnabled()) {
                logger.debug("Failed to read default theme system property; key={}", SYSPROP_DEFAULT_THEME, e);
            }
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
     * populate the registry without materialising real fixtures.
     */
    synchronized void injectThemeForTest(final Theme theme) {
        final Map<String, Theme> next = new HashMap<>(snapshot.byName);
        next.put(theme.getName(), theme);
        snapshot = new Snapshot(Collections.unmodifiableMap(next), snapshot.defaultThemeName);
    }
}
