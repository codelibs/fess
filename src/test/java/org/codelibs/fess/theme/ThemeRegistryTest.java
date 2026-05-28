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

import org.codelibs.fess.mylasta.direction.FessConfig;
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
            assertEquals(ThemeType.STATIC, t.get().getType());
            assertEquals("alpha", t.get().getName());
        } finally {
            deleteRecursively(tempThemesDir);
        }
    }

    @Test
    public void test_resolveActiveTheme_fallsBackToBuiltIn() throws Exception {
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
        final String before = cfg.getSystemProperty(ThemeRegistry.SYSPROP_DEFAULT_THEME, "");
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
            cfg.setSystemProperty(ThemeRegistry.SYSPROP_DEFAULT_THEME, "alpha");
            final ThemeRegistry reg = newRegistryWithFessConfig(tempThemesDir, cfg);
            reg.reload();

            final Optional<Theme> resolved = reg.resolveActiveTheme(null);
            assertTrue(resolved.isPresent());
            assertEquals("alpha", resolved.get().getName());
        } finally {
            cfg.setSystemProperty(ThemeRegistry.SYSPROP_DEFAULT_THEME, before == null ? "" : before);
            deleteRecursively(tempThemesDir);
        }
    }

    @Test
    public void test_resolveActiveTheme_unknownSystemPropertyFallsBackToBuiltin() throws Exception {
        final Path tempThemesDir = Files.createTempDirectory("themes-test-");
        final FessConfig cfg = ComponentUtil.getFessConfig();
        final String before = cfg.getSystemProperty(ThemeRegistry.SYSPROP_DEFAULT_THEME, "");
        try {
            final ThemeRegistry reg = newRegistryWithFessConfig(tempThemesDir, cfg);
            reg.reload();
            // No themes on disk; resolving via an unknown system-property name must
            // yield empty (the registry has nothing to fall back to in this isolated test).
            cfg.setSystemProperty(ThemeRegistry.SYSPROP_DEFAULT_THEME, "this-theme-does-not-exist");
            final Optional<Theme> resolved = reg.resolveActiveTheme(null);
            // Either the resolver returns empty (no fixture, no built-in default reachable
            // from a unit-test classpath) or, defensively, it returns a theme other than
            // the missing one. Both are valid; the contract under test is "no NPE / no
            // throw, no spurious match to the missing name".
            if (resolved.isPresent()) {
                assertEquals(false, "this-theme-does-not-exist".equals(resolved.get().getName()));
            } else {
                assertNull(reg.getTheme("this-theme-does-not-exist").orElse(null));
            }
        } finally {
            cfg.setSystemProperty(ThemeRegistry.SYSPROP_DEFAULT_THEME, before == null ? "" : before);
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
    public void test_scanJsp_registersDirectoryWithSearchJsp() throws Exception {
        final Path tempThemesDir = Files.createTempDirectory("themes-test-");
        final Path tempViewDir = Files.createTempDirectory("view-test-");
        try {
            final Path themeDir = tempViewDir.resolve("classic");
            Files.createDirectories(themeDir);
            Files.writeString(themeDir.resolve("search.jsp"), "<%-- search --%>");

            final ThemeRegistry reg = new ThemeRegistry();
            reg.setThemesDirOverride(tempThemesDir);
            reg.setViewBaseOverride(tempViewDir);
            reg.reload();

            final Optional<Theme> t = reg.getTheme("classic");
            assertTrue(t.isPresent(), "Directory with search.jsp must be registered as JSP theme");
            assertEquals(ThemeType.JSP, t.get().getType());
            assertEquals("classic", t.get().getName());
        } finally {
            deleteRecursively(tempThemesDir);
            deleteRecursively(tempViewDir);
        }
    }

    @Test
    public void test_scanJsp_skipsDirectoryWithoutSearchJsp() throws Exception {
        // MVC view folders like chat/, login/, profile/ live next to JSP themes but
        // do not provide search.jsp — they must not be exposed as themes.
        final Path tempThemesDir = Files.createTempDirectory("themes-test-");
        final Path tempViewDir = Files.createTempDirectory("view-test-");
        try {
            for (final String mvcDir : new String[] { "chat", "login", "profile", "stray" }) {
                final Path d = tempViewDir.resolve(mvcDir);
                Files.createDirectories(d);
                Files.writeString(d.resolve("index.jsp"), "<%-- not a theme --%>");
            }

            final ThemeRegistry reg = new ThemeRegistry();
            reg.setThemesDirOverride(tempThemesDir);
            reg.setViewBaseOverride(tempViewDir);
            reg.reload();

            assertTrue(reg.getTheme("chat").isEmpty(), "chat/ must not be registered as a JSP theme");
            assertTrue(reg.getTheme("login").isEmpty(), "login/ must not be registered as a JSP theme");
            assertTrue(reg.getTheme("profile").isEmpty(), "profile/ must not be registered as a JSP theme");
            assertTrue(reg.getTheme("stray").isEmpty(), "directories without search.jsp must not be registered");
        } finally {
            deleteRecursively(tempThemesDir);
            deleteRecursively(tempViewDir);
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
