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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
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

    /** Name of the theme bundled with the WAR, used as the final fallback when neither the
     *  virtual-host key nor {@code theme.default} resolves to an installed theme. */
    public static final String BUILT_IN_THEME_NAME = "bootstrap";

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
     * Remembers the unknown {@code theme.default} value already reported, so it is logged once
     * per distinct value instead of once per request.
     */
    private final AtomicReference<String> warnedUnknownDefault = new AtomicReference<>();

    /** Counts WARN emissions for an unknown default theme name. Test seam only. */
    private final AtomicInteger unknownDefaultWarnCount = new AtomicInteger();

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
            // post-shutdown). Reported on every reload that hits it.
            if (logger.isDebugEnabled()) {
                logger.warn("Servlet context unavailable while resolving themes dir; falling back to configured path={}", configured, e);
            } else {
                logger.warn("Servlet context unavailable while resolving themes dir; falling back to configured path={}: {}", configured,
                        e.getMessage());
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
     *       {@link Constants#DEFAULT_THEME_PROPERTY} system property.</li>
     *   <li>Otherwise fall back to the bundled {@link #BUILT_IN_THEME_NAME} theme, which
     *       ships inside the WAR and is refused by the delete path; this is what keeps a
     *       Fess instance with no theme configuration at all on the static UI.</li>
     * </ol>
     *
     * <p>An unknown {@code theme.default} value is an operator mistake, not a per-request
     * event, so it is logged once per distinct value rather than on every call.</p>
     *
     * @param virtualHostKey theme name derived from the request's virtual host
     *        (may be {@code null} or blank)
     * @return the resolved theme, or empty if even the bundled theme is not installed
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
            final Theme configured = snap.byName.get(def);
            if (configured != null) {
                return Optional.of(configured);
            }
            // A default that names a theme nobody installed is an operator mistake, not a
            // per-request event: report it once per distinct value so the log stays readable
            // while a fresh typo is still reported.
            if (!def.equals(warnedUnknownDefault.getAndSet(def))) {
                unknownDefaultWarnCount.incrementAndGet();
                logger.warn("The default theme '{}' is not installed; falling back to '{}'.", def, BUILT_IN_THEME_NAME);
            }
        }
        // The bundled theme ships inside the WAR and is refused by the delete path, so this is
        // the last resort: without it there is no static UI and the request falls through to
        // whatever Fess routes are left.
        return Optional.ofNullable(snap.byName.get(BUILT_IN_THEME_NAME));
    }

    private String lookupDefaultThemeName() {
        if (fessConfig == null) {
            return null;
        }
        try {
            return fessConfig.getDefaultTheme();
        } catch (final Exception e) {
            // System-property lookup can fail when the config layer is mid-init or
            // the system-properties index is unreachable. Reported on every reload that hits it.
            if (logger.isDebugEnabled()) {
                logger.warn("Failed to read default theme system property; key={}", Constants.DEFAULT_THEME_PROPERTY, e);
            } else {
                logger.warn("Failed to read default theme system property; key={}: {}", Constants.DEFAULT_THEME_PROPERTY, e.getMessage());
            }
            return null;
        }
    }

    // ---- Test seams ----
    void setThemesDirOverride(final Path p) {
        this.themesDirOverride = p;
    }

    /**
     * Returns how many times an unknown {@code theme.default} value has triggered a WARN log.
     * Test seam only.
     *
     * @return the number of distinct unknown default values warned about so far
     */
    int getUnknownDefaultWarnCountForTest() {
        return unknownDefaultWarnCount.get();
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
