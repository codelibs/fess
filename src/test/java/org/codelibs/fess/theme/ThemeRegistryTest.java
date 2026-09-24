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

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;

public class ThemeRegistryTest extends UnitFessTestCase {

    @Test
    public void test_scan_discoversStaticTheme() throws Exception {
        final Path tempThemesDir = Files.createTempDirectory("themes-test-");
        try {
            final Path themeDir = tempThemesDir.resolve("alpha");
            Files.createDirectories(themeDir);
            Files.writeString(themeDir.resolve("theme.yml"), String.join("\n", //
                    "apiVersion: fess.codelibs.org/v1", //
                    "kind: StaticTheme", //
                    "name: alpha", //
                    "displayName: Alpha", //
                    "version: 1.0.0"));
            Files.writeString(themeDir.resolve("index.html"), "<html></html>");

            final ThemeRegistry reg = new ThemeRegistry();
            reg.setThemesDirOverride(tempThemesDir);
            reg.reload();

            final Optional<Theme> t = reg.getTheme("alpha");
            assertTrue(t.isPresent());
            assertEquals("alpha", t.get().getName());
        } finally {
            deleteRecursively(tempThemesDir);
        }
    }

    @Test
    public void test_resolveActiveTheme_emptyWhenBuiltInIsMissing() throws Exception {
        // No themes on disk at all, not even the bundled "bootstrap" theme: resolution has
        // nothing left to fall back to and must return empty rather than throw or invent one.
        final Path tempThemesDir = Files.createTempDirectory("themes-test-");
        try {
            final ThemeRegistry reg = new ThemeRegistry();
            reg.setThemesDirOverride(tempThemesDir);
            reg.reload();
            assertTrue(reg.resolveActiveTheme(null).isEmpty());
            assertTrue(reg.resolveActiveTheme("nonexistent").isEmpty());
        } finally {
            deleteRecursively(tempThemesDir);
        }
    }

    @Test
    public void test_resolveActiveTheme_fallsBackToBuiltInWhenNoDefaultConfigured() throws Exception {
        final Path tempThemesDir = Files.createTempDirectory("themes-test-");
        try {
            writeTheme(tempThemesDir, ThemeRegistry.BUILT_IN_THEME_NAME);
            writeTheme(tempThemesDir, "docsearch");

            final ThemeRegistry reg = new ThemeRegistry();
            reg.setThemesDirOverride(tempThemesDir);
            reg.reload();

            // No virtual host key, no theme.default configured: the bundled theme wins.
            final Optional<Theme> resolved = reg.resolveActiveTheme(null);
            assertTrue(resolved.isPresent());
            assertEquals(ThemeRegistry.BUILT_IN_THEME_NAME, resolved.get().getName());
        } finally {
            deleteRecursively(tempThemesDir);
        }
    }

    @Test
    public void test_scan_skipsInvalidManifest() throws Exception {
        final Path tempThemesDir = Files.createTempDirectory("themes-test-");
        try {
            final Path themeDir = tempThemesDir.resolve("broken");
            Files.createDirectories(themeDir);
            Files.writeString(themeDir.resolve("theme.yml"), "not valid yaml: : :");

            final ThemeRegistry reg = new ThemeRegistry();
            reg.setThemesDirOverride(tempThemesDir);
            reg.reload();

            assertTrue(reg.getTheme("broken").isEmpty());
        } finally {
            deleteRecursively(tempThemesDir);
        }
    }

    @Test
    public void test_resolveActiveTheme_systemPropertyOverridesBuiltinDefault() throws Exception {
        final Path tempThemesDir = Files.createTempDirectory("themes-test-");
        final FessConfig cfg = ComponentUtil.getFessConfig();
        final String before = cfg.getDefaultTheme();
        try {
            // Materialise a fixture theme on disk.
            final Path themeDir = tempThemesDir.resolve("alpha");
            Files.createDirectories(themeDir);
            Files.writeString(themeDir.resolve("theme.yml"), String.join("\n", //
                    "apiVersion: fess.codelibs.org/v1", //
                    "kind: StaticTheme", //
                    "name: alpha", //
                    "displayName: Alpha", //
                    "version: 1.0.0"));

            // Point the system property at our fixture BEFORE reload — the registry caches
            // the resolved default into its snapshot at reload time (so per-read does not
            // dip into FessConfig). Property changes therefore require a fresh reload to
            // take effect, mirroring how the admin UI calls reload() after setdefault.
            cfg.setDefaultTheme("alpha");
            final ThemeRegistry reg = newRegistryWithFessConfig(tempThemesDir, cfg);
            reg.reload();

            final Optional<Theme> resolved = reg.resolveActiveTheme(null);
            assertTrue(resolved.isPresent());
            assertEquals("alpha", resolved.get().getName());
        } finally {
            cfg.setDefaultTheme(before == null ? "" : before);
            deleteRecursively(tempThemesDir);
        }
    }

    @Test
    public void test_resolveActiveTheme_fallsBackToBuiltInWhenDefaultIsNotInstalled() throws Exception {
        final Path tempThemesDir = Files.createTempDirectory("themes-test-");
        final FessConfig cfg = ComponentUtil.getFessConfig();
        final String before = cfg.getDefaultTheme();
        try {
            writeTheme(tempThemesDir, ThemeRegistry.BUILT_IN_THEME_NAME);

            // Point the system property at a theme nobody installed BEFORE reload — the
            // registry caches the resolved default into its snapshot at reload time, so a
            // property change after reload would not be observed.
            cfg.setDefaultTheme("this-theme-does-not-exist");
            final ThemeRegistry reg = newRegistryWithFessConfig(tempThemesDir, cfg);
            reg.reload();

            final Optional<Theme> resolved = reg.resolveActiveTheme(null);
            assertTrue(resolved.isPresent());
            assertEquals(ThemeRegistry.BUILT_IN_THEME_NAME, resolved.get().getName());
        } finally {
            cfg.setDefaultTheme(before == null ? "" : before);
            deleteRecursively(tempThemesDir);
        }
    }

    @Test
    public void test_resolveActiveTheme_warnsOncePerUnknownDefault() throws Exception {
        final Path tempThemesDir = Files.createTempDirectory("themes-test-");
        final FessConfig cfg = ComponentUtil.getFessConfig();
        final String before = cfg.getDefaultTheme();
        try {
            writeTheme(tempThemesDir, ThemeRegistry.BUILT_IN_THEME_NAME);
            cfg.setDefaultTheme("ghost");
            final ThemeRegistry reg = newRegistryWithFessConfig(tempThemesDir, cfg);
            reg.reload();

            reg.resolveActiveTheme(null);
            reg.resolveActiveTheme(null);

            assertEquals(1, reg.getUnknownDefaultWarnCountForTest());

            // A second, DIFFERENT unknown default must warn again: this is what distinguishes
            // "warn once per distinct value" from a naive "warn once ever" latch, which would
            // still report 1 here. theme.default is cached into the snapshot at reload time
            // (see test_resolveActiveTheme_fallsBackToBuiltInWhenDefaultIsNotInstalled), so the
            // property must be changed BEFORE reload() for the new value to take effect.
            cfg.setDefaultTheme("ghost2");
            reg.reload();
            reg.resolveActiveTheme(null);
            reg.resolveActiveTheme(null);

            assertEquals(2, reg.getUnknownDefaultWarnCountForTest());
        } finally {
            cfg.setDefaultTheme(before == null ? "" : before);
            deleteRecursively(tempThemesDir);
        }
    }

    @Test
    public void test_reload_reportsDefaultThemeLookupFailureOnEveryReload() throws Exception {
        final Path tempThemesDir = Files.createTempDirectory("themes-test-");
        final FessConfig failing = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getDefaultTheme() {
                throw new IllegalStateException("config layer not ready");
            }
        };
        final LogCapturingAppender appender = LogCapturingAppender.attach(ThemeRegistry.class.getName(), Level.INFO);
        try {
            final ThemeRegistry reg = newRegistryWithFessConfig(tempThemesDir, failing);
            reg.reload();
            reg.reload();
            final List<LogEvent> warns = appender.eventsAt(Level.WARN)
                    .stream()
                    .filter(e -> e.getMessage().getFormattedMessage().contains("Failed to read default theme"))
                    .toList();
            assertEquals(2, warns.size());
            warns.forEach(e -> {
                assertTrue(e.getMessage().getFormattedMessage().contains("config layer not ready"));
                assertNull(e.getThrown(), "the stack trace is only for DEBUG");
            });
        } finally {
            appender.detach();
            deleteRecursively(tempThemesDir);
        }
    }

    @Test
    public void test_reload_defaultThemeLookupFailureCarriesStackTraceAtDebug() throws Exception {
        final Path tempThemesDir = Files.createTempDirectory("themes-test-");
        final FessConfig failing = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getDefaultTheme() {
                throw new IllegalStateException("config layer not ready");
            }
        };
        final LogCapturingAppender appender = LogCapturingAppender.attach(ThemeRegistry.class.getName(), Level.DEBUG);
        try {
            newRegistryWithFessConfig(tempThemesDir, failing).reload();
            final List<LogEvent> warns = appender.eventsAt(Level.WARN)
                    .stream()
                    .filter(e -> e.getMessage().getFormattedMessage().contains("Failed to read default theme"))
                    .toList();
            assertEquals(1, warns.size());
            assertNotNull(warns.get(0).getThrown());
        } finally {
            appender.detach();
            deleteRecursively(tempThemesDir);
        }
    }

    @Test
    public void test_reload_isThreadSafeUnderConcurrentReaders() throws Exception {
        final Path tempThemesDir = Files.createTempDirectory("themes-test-");
        try {
            // Seed the directory with several themes so readers have something to walk.
            for (final String n : new String[] { "alpha", "beta", "gamma" }) {
                final Path d = tempThemesDir.resolve(n);
                Files.createDirectories(d);
                Files.writeString(d.resolve("theme.yml"), String.join("\n", //
                        "apiVersion: fess.codelibs.org/v1", //
                        "kind: StaticTheme", //
                        "name: " + n, //
                        "displayName: " + n, //
                        "version: 1.0.0"));
            }

            final ThemeRegistry reg = new ThemeRegistry();
            reg.setThemesDirOverride(tempThemesDir);
            reg.reload();

            final int readerCount = 8;
            final int iterations = 200;
            final ExecutorService pool = Executors.newFixedThreadPool(readerCount);
            final CountDownLatch ready = new CountDownLatch(readerCount);
            final CountDownLatch go = new CountDownLatch(1);
            final List<Throwable> failures = new ArrayList<>();
            final AtomicBoolean stop = new AtomicBoolean(false);
            try {
                for (int i = 0; i < readerCount; i++) {
                    pool.submit(() -> {
                        ready.countDown();
                        try {
                            go.await();
                            for (int j = 0; j < iterations && !stop.get(); j++) {
                                reg.getAllThemes().size();
                                reg.resolveActiveTheme(null);
                                reg.getTheme("alpha");
                            }
                        } catch (final Throwable t) {
                            synchronized (failures) {
                                failures.add(t);
                            }
                            stop.set(true);
                        }
                    });
                }
                // Wait for all readers to be parked at the gate, then start them.
                ready.await(5, TimeUnit.SECONDS);
                go.countDown();
                // While readers spin, trigger two reloads from the main thread.
                reg.reload();
                Thread.yield();
                reg.reload();
                pool.shutdown();
                final boolean done = pool.awaitTermination(15, TimeUnit.SECONDS);
                assertTrue(done, "Concurrent readers did not terminate within timeout");
                synchronized (failures) {
                    if (!failures.isEmpty()) {
                        final Throwable first = failures.get(0);
                        throw new AssertionError("Concurrent reader threw " + first.getClass().getSimpleName() + ": " + first.getMessage(),
                                first);
                    }
                }
            } finally {
                if (!pool.isTerminated()) {
                    pool.shutdownNow();
                }
            }
        } finally {
            deleteRecursively(tempThemesDir);
        }
    }

    @Test
    public void test_scan_doesNotFollowSymlinkedDirectory() throws Exception {
        // A symlinked directory inside the themes root must NOT be registered as a theme,
        // because following it could point outside the sandbox.
        final Path realTheme = Files.createTempDirectory("theme-real-");
        final Path tempThemesDir = Files.createTempDirectory("themes-test-symlink-");
        try {
            // Materialise a valid theme in a directory outside the themes root.
            Files.writeString(realTheme.resolve("theme.yml"), String.join("\n", //
                    "apiVersion: fess.codelibs.org/v1", //
                    "kind: StaticTheme", //
                    "name: outsider", //
                    "displayName: Outsider", //
                    "version: 1.0.0"));
            Files.writeString(realTheme.resolve("index.html"), "<html/>");

            // Create a symlink inside the themes dir pointing to the real theme.
            final Path symlink = tempThemesDir.resolve("outsider");
            try {
                Files.createSymbolicLink(symlink, realTheme);
            } catch (final java.io.IOException | UnsupportedOperationException e) {
                // Symlinks not supported on this platform — skip the test gracefully.
                return;
            }

            final ThemeRegistry reg = new ThemeRegistry();
            reg.setThemesDirOverride(tempThemesDir);
            reg.reload();

            // The symlinked "theme" must NOT be registered.
            assertTrue(reg.getTheme("outsider").isEmpty(), "Symlinked theme directory must not be registered");
        } finally {
            deleteRecursively(realTheme);
            deleteRecursively(tempThemesDir);
        }
    }

    /**
     * Constructs a {@link ThemeRegistry} with the supplied {@link FessConfig}
     * injected into the protected {@code fessConfig} field via reflection. The
     * field is declared in the same package but the test seam does not expose a
     * setter, so reflection is the least-invasive way to wire it for tests that
     * exercise system-property paths.
     */
    private static ThemeRegistry newRegistryWithFessConfig(final Path themesDirOverride, final FessConfig cfg) throws Exception {
        final ThemeRegistry reg = new ThemeRegistry();
        reg.setThemesDirOverride(themesDirOverride);
        final Field f = ThemeRegistry.class.getDeclaredField("fessConfig");
        f.setAccessible(true);
        f.set(reg, cfg);
        return reg;
    }

    /**
     * Materialises a minimal, valid theme fixture named {@code name} under {@code themesDir}.
     */
    private static void writeTheme(final Path themesDir, final String name) throws Exception {
        final Path themeDir = themesDir.resolve(name);
        Files.createDirectories(themeDir);
        Files.writeString(themeDir.resolve("theme.yml"), String.join("\n", //
                "apiVersion: fess.codelibs.org/v1", //
                "kind: StaticTheme", //
                "name: " + name, //
                "displayName: " + name, //
                "version: 1.0.0"));
    }

    private static void deleteRecursively(final Path p) throws Exception {
        if (!Files.exists(p)) {
            return;
        }
        Files.walk(p).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
            try {
                Files.delete(x);
            } catch (final Exception ignore) {
                // ignore
            }
        });
    }
}
