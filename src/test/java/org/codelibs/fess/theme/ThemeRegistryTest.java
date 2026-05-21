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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.codelibs.fess.unit.UnitFessTestCase;
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
