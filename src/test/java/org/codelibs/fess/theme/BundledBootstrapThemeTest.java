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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Regression coverage for the bundled {@code bootstrap} static theme.
 *
 * <p>These assertions run directly against the source tree (no servlet
 * container), so they verify the bundled artifact remains coherent even when
 * the WAR has not yet been packaged.</p>
 */
public class BundledBootstrapThemeTest {

    private static final Path THEME_DIR = Paths.get("src/main/webapp/themes/bootstrap");

    @Test
    public void test_themeDirectoryExists() {
        assertTrue(Files.isDirectory(THEME_DIR), "Bundled bootstrap theme directory missing: " + THEME_DIR.toAbsolutePath());
    }

    @Test
    public void test_themeYmlIsPresentAndParsesWithExpectedFields() throws Exception {
        final Path yml = THEME_DIR.resolve("theme.yml");
        assertTrue(Files.isRegularFile(yml), "theme.yml missing");
        try (InputStream in = Files.newInputStream(yml)) {
            final ThemeManifest m = ThemeManifest.parse(in);
            assertEquals("bootstrap", m.getName());
            assertEquals("fess.codelibs.org/v1", m.getApiVersion());
            assertEquals("StaticTheme", m.getKind());
            assertEquals("index.html", m.getEntry());
            assertTrue(m.isSpaFallback());
            assertTrue(m.getSupportedLocales().contains("en"));
            assertTrue(m.getSupportedLocales().contains("ja"));
            assertNotNull(m.getVersion());
            assertNotNull(m.getDisplayName());
        }
    }

    @Test
    public void test_indexHtmlIsPresent() {
        assertTrue(Files.isRegularFile(THEME_DIR.resolve("index.html")), "index.html missing");
    }

    @Test
    public void test_coreAssetFilesArePresent() {
        assertTrue(Files.isRegularFile(THEME_DIR.resolve("assets/app.js")), "assets/app.js missing");
        assertTrue(Files.isRegularFile(THEME_DIR.resolve("assets/api.js")), "assets/api.js missing");
        assertTrue(Files.isRegularFile(THEME_DIR.resolve("assets/i18n.js")), "assets/i18n.js missing");
        assertTrue(Files.isRegularFile(THEME_DIR.resolve("assets/auth.js")), "assets/auth.js missing");
        assertTrue(Files.isRegularFile(THEME_DIR.resolve("assets/search.js")), "assets/search.js missing");
        assertTrue(Files.isRegularFile(THEME_DIR.resolve("assets/chat.js")), "assets/chat.js missing");
        assertTrue(Files.isRegularFile(THEME_DIR.resolve("assets/styles.css")), "assets/styles.css missing");
    }

    @Test
    public void test_i18nBundlesArePresent() {
        assertTrue(Files.isRegularFile(THEME_DIR.resolve("i18n/messages.en.json")), "messages.en.json missing");
        assertTrue(Files.isRegularFile(THEME_DIR.resolve("i18n/messages.ja.json")), "messages.ja.json missing");
    }

    @Test
    public void test_themeYml_versionIsValidSemverAndApiVersionMatches() throws Exception {
        final Path yml = THEME_DIR.resolve("theme.yml");
        try (InputStream in = Files.newInputStream(yml)) {
            final ThemeManifest m = ThemeManifest.parse(in);
            // version must be a valid semver string, optionally with pre-release/build metadata.
            final Pattern semver = Pattern.compile("\\d+\\.\\d+\\.\\d+(?:[-+].*)?");
            assertTrue(semver.matcher(m.getVersion()).matches(), "version is not valid semver: " + m.getVersion());
            // apiVersion must match the canonical constant defined by ThemeManifest.
            assertEquals(ThemeManifest.CURRENT_API_VERSION, m.getApiVersion());
        }
    }

    @Test
    public void test_i18nBundles_haveMatchingTopLevelKeysAcrossLocales() throws Exception {
        final Path enPath = THEME_DIR.resolve("i18n/messages.en.json");
        final Path jaPath = THEME_DIR.resolve("i18n/messages.ja.json");
        final ObjectMapper mapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        final Map<String, Object> en = mapper.readValue(Files.readAllBytes(enPath), LinkedHashMap.class);
        @SuppressWarnings("unchecked")
        final Map<String, Object> ja = mapper.readValue(Files.readAllBytes(jaPath), LinkedHashMap.class);
        // Compare top-level key sets — guards against drift between locales as new
        // strings are added.
        final Set<String> enKeys = new TreeSet<>(en.keySet());
        final Set<String> jaKeys = new TreeSet<>(ja.keySet());
        assertEquals(enKeys, jaKeys, "i18n top-level keys differ between en and ja bundles");
    }

    @Test
    public void test_jsModules_containNoBackslashEscapedPaths() throws Exception {
        // Guard against accidental \-escaped path strings sneaking into source —
        // these often come from over-zealous JSON string interpolation and break URL
        // resolution at runtime.
        try (Stream<Path> walk = Files.walk(THEME_DIR.resolve("assets"))) {
            walk.filter(p -> p.toString().endsWith(".js")).forEach(p -> {
                final String text;
                try {
                    text = Files.readString(p, StandardCharsets.UTF_8);
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
                assertFalse(text.contains("\\\\"), "literal backslash sequence found in " + p);
                assertFalse(text.contains("\\/themes\\/"), "escaped /themes/ path found in " + p);
            });
        }
    }

    @Test
    public void test_themeRegistryListsBundledThemeAfterReload() throws Exception {
        final ThemeRegistry reg = new ThemeRegistry();
        // resolve to the source-tree webapp/themes dir as the test seam input
        reg.setThemesDirOverride(Paths.get("src/main/webapp/themes"));
        reg.reload();
        assertTrue(reg.getAllThemes().containsKey("bootstrap"),
                "ThemeRegistry did not discover bundled bootstrap theme; got: " + reg.getAllThemes().keySet());
        final Theme t = reg.getAllThemes().get("bootstrap");
        assertEquals("bootstrap", t.getName());
        assertTrue(t.getManifest().isPresent());
        assertEquals("Bootstrap", t.getManifest().map(ThemeManifest::getDisplayName).orElse(null));
    }

    @Test
    public void test_searchJs_thumbnailUsesThumbnailEndpoint() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("/thumbnail/?docId="));
        assertTrue(js.contains("&queryId="));
        assertFalse(js.contains("safeHref(d.thumbnail)"));
    }

    @Test
    public void test_searchJs_usesHighlightedContentTitle() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("renderHighlightedSnippet(d.content_title)"));
        assertTrue(js.contains("a.textContent = d.title || d.url"));
    }
}
