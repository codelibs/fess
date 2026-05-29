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
    public void test_indexHtml_cspAllowsBlobFrameForCacheViewer() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        assertTrue(html.contains("frame-src blob:"), "index.html CSP must allow blob: frames for cache viewer");
        assertTrue(html.contains("child-src blob:"), "index.html CSP must allow blob: child-src for cache viewer");
        assertTrue(html.contains("frame-ancestors 'none'"), "index.html CSP must deny framing of this page");
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

    @Test
    public void test_searchJs_labelFacetFromResponseWithZeroSuppress() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("f.name === \"label\""));
        assertTrue(js.contains("Number(r.count) > 0"));
    }

    @Test
    public void test_searchJs_facetViewsFromServerConfig() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("cfg.facet_views"));
        assertTrue(js.contains("env.facet_query"));
        assertFalse(js.contains("const TIMESTAMP_RANGES"));
        assertFalse(js.contains("const SIZE_RANGES"));
    }

    @Test
    public void test_indexHtml_hasInlineSearchErrorRegion() throws Exception {
        assertTrue(Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8).contains("id=\"search-error\""));
    }

    @Test
    public void test_searchJs_surfacesInvalidRequestMessage() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("e.code === \"invalid_request\""));
        assertTrue(js.contains("e.code === \"INVALID_REQUEST\""));
        assertTrue(js.contains("errBox.textContent = e.message"));
    }

    @Test
    public void test_searchJs_suggestForwardsLangAndLabel() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("suggestParams.lang = state.lang"));
        assertTrue(js.contains("suggestParams.label = labelFilters"));
    }

    @Test
    public void test_searchJs_setsPerQueryDocumentTitle() throws Exception {
        assertTrue(Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8)
                .contains("document.title = state.q ? t(\"page.search_title\")"));
    }

    @Test
    public void test_indexHtml_noStaticOsddLink() throws Exception {
        assertFalse(Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8).contains("rel=\"search\""));
    }

    @Test
    public void test_searchJs_injectsOsddLinkGatedOnConfig() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("function ensureOsddLink()"));
        assertTrue(js.contains("application/opensearchdescription+xml"));
    }

    @Test
    public void test_searchJs_statusLineUsesBoldNodes() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("document.createElement(\"b\")"));
    }

    @Test
    public void test_i18n_statusTemplatesUseBoldTokens() throws Exception {
        for (final String loc : new String[] { "en", "ja" }) {
            final String json = Files.readString(THEME_DIR.resolve("i18n/messages." + loc + ".json"), StandardCharsets.UTF_8);
            assertTrue(json.contains("{b0}") && json.contains("{bq}"));
        }
    }

    @Test
    public void test_indexHtml_hasGeoControls() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        assertTrue(html.contains("id=\"geo-lat\"") && html.contains("id=\"geo-lon\"") && html.contains("id=\"geo-distance\""));
        assertTrue(html.contains("id=\"geo-apply\""));
    }

    @Test
    public void test_searchJs_emitsAndHydratesGeoParams() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("\"geo.location.point\""));
        assertTrue(js.contains("\"geo.location.distance\""));
        assertTrue(js.contains("params.get(\"geo.location.point\")"));
    }

    @Test
    public void test_i18n_hasGeoKeys() throws Exception {
        for (final String loc : new String[] { "en", "ja" }) {
            final String json = Files.readString(THEME_DIR.resolve("i18n/messages." + loc + ".json"), StandardCharsets.UTF_8);
            for (final String k : new String[] { "search.geo", "search.geo_lat", "search.geo_lon", "search.geo_distance",
                    "search.geo_apply", "search.geo_clear" }) {
                assertTrue(json.contains("\"" + k + "\""), "missing " + k + " in " + loc);
            }
        }
    }

    @Test
    public void test_advanceJs_filetypeCanonicalValues() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/advance.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("word") && js.contains("excel") && js.contains("powerpoint"));
        assertFalse(js.contains("msword"));
        assertFalse(js.contains("msexcel"));
        assertFalse(js.contains("mspowerpoint"));
        assertTrue(js.contains("filetype_options"));
    }

    @Test
    public void test_searchJs_runFromUrlHonorsAdvancedParams() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("getAll(\"lang\")"));
        assertTrue(js.contains("getAll(\"ex_q\")"));
    }

    @Test
    public void test_advanceJs_noneOfUsesNot() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/advance.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("\"NOT \" + w"));
        assertFalse(js.contains("\"-\" + w"));
    }

    @Test
    public void test_advanceSuggestWired() throws Exception {
        final String s = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        final String a = Files.readString(THEME_DIR.resolve("assets/advance.js"), StandardCharsets.UTF_8);
        assertTrue(s.contains("export function attachSuggest"));
        assertTrue(a.contains("attachSuggest(") && a.contains("from \"./search.js\""));
    }

    @Test
    public void test_advanceJs_preservesParams() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/advance.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("new URLSearchParams(location.search)"));
        assertTrue(js.contains("startsWith(\"fields.\")"));
    }

    @Test
    public void test_advanceJs_sortGatedByConfig() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/advance.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("search_log_enabled") && js.contains("user_favorite"));
        assertTrue(js.contains("click_count.desc") && js.contains("favorite_count.desc"));
    }

    @Test
    public void test_chatRequestBodyContract() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("message: question"));
        assertTrue(js.contains("fields = { label:"));
        assertTrue(js.contains("extra_queries"));
        assertFalse(js.contains("body = { question"));
    }

    @Test
    public void test_chatImeGuard() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("ev.isComposing"));
        assertTrue(js.contains("compositionstart") && js.contains("compositionend"));
    }

    @Test
    public void test_chatSourcesPrecedence() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("src.go_url || src.url_link || src.url"));
        assertTrue(js.contains("sourceIcon(") && js.contains("sourceTypeLabel("));
    }

    @Test
    public void test_chatPhaseStatusModel() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("data.status"));
        assertTrue(js.contains("status === \"complete\""));
        assertTrue(js.contains("phaseStrip.complete("));
        assertTrue(js.contains("data.hit_count"));
    }

    @Test
    public void test_chat5Features() throws Exception {
        final String chat = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
        final String api = Files.readString(THEME_DIR.resolve("assets/api.js"), StandardCharsets.UTF_8);
        final String en = Files.readString(THEME_DIR.resolve("i18n/messages.en.json"), StandardCharsets.UTF_8);
        // D5a: retry interpolation
        assertTrue(chat.contains("data.sleep_ms"));
        assertTrue(en.contains("{attempt}") && en.contains("{max}") && en.contains("{seconds}"));
        // D5c: auto-resize
        assertTrue(chat.contains("scrollHeight, 150"));
        // D5d: eof event in api.js and handled in chat.js
        assertTrue(api.contains("type: \"eof\"") && chat.contains("\"eof\""));
        // D5e: per-group filter search
        assertTrue(chat.contains("chat-filter-search"));
    }

    @Test
    public void test_markdownTablesBlockquote() throws Exception {
        final String md = Files.readString(THEME_DIR.resolve("assets/markdown.js"), StandardCharsets.UTF_8);
        assertTrue(md.contains("<table") && md.contains("<thead>") && md.contains("<tbody>"));
        assertTrue(md.contains("<blockquote>") && md.contains("TABLE_DELIM_RE"));
    }

    @Test
    public void test_cacheJs_rendersBannerAndBaseAndCharset() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/cache.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("labels.search_cache_msg"));
        assertTrue(js.contains("env.charset"));
        assertTrue(js.contains("<base href=\""));
    }

    @Test
    public void test_i18n_hasCacheKeys() throws Exception {
        for (final String loc : new String[] { "en", "ja" }) {
            final String j = Files.readString(THEME_DIR.resolve("i18n/messages." + loc + ".json"), StandardCharsets.UTF_8);
            assertTrue(j.contains("\"labels.search_result_cache\"") && j.contains("\"labels.search_cache_msg\"")
                    && j.contains("\"labels.search_unknown\""));
        }
    }

    @Test
    public void test_helpEn_rangesDocumentsExclusiveBounds() throws Exception {
        final String j = Files.readString(THEME_DIR.resolve("help/en.json"), StandardCharsets.UTF_8);
        assertTrue(j.contains("{ }") || j.contains("{1024 TO 10240}"));
    }

    @Test
    public void test_indexHtml_noResultScreenHasIconAndMessages() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        assertTrue(html.contains("id=\"empty-did-not-match\""), "no-result screen must have a did-not-match element");
        assertTrue(html.contains("fa fa-search fa-3x"), "no-result screen must show a search icon");
        assertTrue(html.contains("data-i18n=\"search.did_not_match_suggestion\""), "no-result screen must show the suggestion line");
    }

    @Test
    public void test_searchJs_noResultRendersInterpolatedQuery() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("search.did_not_match"), "search.js must render the did-not-match message with the query");
    }

    @Test
    public void test_indexHtml_searchInputHasMaxLength() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        assertTrue(html.contains("maxlength=\"1000\""), "search inputs must cap length at 1000 (JSP parity)");
    }

    @Test
    public void test_appJs_footerUsesI18nYearNotClientClock() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/app.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("footer.copyright_year"));
        assertFalse(js.contains("new Date().getFullYear()"));
    }

    @Test
    public void test_indexHtml_homeViewHasLogoImage() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        assertTrue(html.contains("home-logo"), "home view must render the product logo image, not a text heading");
        assertFalse(html.contains("<h2 class=\"display-4 mb-4\">Fess</h2>"), "home view text heading must be replaced by the logo");
    }

    @Test
    public void test_indexHtml_homeViewHasOptionsPanel() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        assertTrue(html.contains("id=\"home-options\""), "home view must have a collapsible options panel");
        assertTrue(html.contains("id=\"home-options-toggle\""), "home view must have an Options toggle button");
        assertTrue(html.contains("id=\"home-sort-select\""), "home options must include a sort select");
        assertTrue(html.contains("id=\"home-num-select\""), "home options must include a num select");
        assertTrue(html.contains("id=\"home-lang-select\""), "home options must include a lang select");
    }

    @Test
    public void test_searchJs_populatesHomeOptions() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("home-sort-select"), "search.js must populate the home option controls");
    }

    @Test
    public void test_searchJs_facetQueryViewShowsNotFound() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("facet.not_found"),
                "search.js must render a 'not found' message for a facet-query group with all-zero counts");
    }

    @Test
    public void test_advanceJs_prefillsFromQueryParam() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/advance.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("location.search") && js.contains("fAll.input.value"),
                "advance.js must prefill the must-contain-words field (fAll) from the incoming q param");
    }

    @Test
    public void test_searchJs_disablesSubmitTemporarily() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("3000"), "search.js must re-enable the submit button after a 3s disable (JSP parity)");
        assertTrue(js.contains("disableSubmitBriefly"), "search.js must define/use the disableSubmitBriefly helper");
        assertTrue(js.contains("search-submit"), "search.js must wire the 3s disable to the header submit button");
    }

    @Test
    public void test_appJs_disablesHomeSubmitTemporarily() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/app.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("disableSubmitBriefly"),
                "app.js must call the shared disableSubmitBriefly helper for the home form (JSP parity)");
    }

    @Test
    public void test_advanceJs_disablesSubmitTemporarily() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/advance.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("disableSubmitBriefly"),
                "advance.js must call the shared disableSubmitBriefly helper after navigate (JSP parity)");
    }

    // ── Parity Round 3 / Task #11 — error-code meta + codeFromPath default ──────

    /**
     * #11 client: error.js must read the x-fess-error-code meta tag as the authoritative
     * error code, falling back to codeFromPath when the meta is absent.
     */
    @Test
    public void test_errorJs_readsErrorCodeMeta() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/error.js"), StandardCharsets.UTF_8);
        // Must define a function that queries the x-fess-error-code meta
        assertTrue(js.contains("x-fess-error-code"), "error.js must reference x-fess-error-code meta (#11)");
        assertTrue(js.contains("readErrorCodeMeta"), "error.js must define readErrorCodeMeta() function (#11)");
        // attach() must prefer meta over path: readErrorCodeMeta() || codeFromPath(...)
        assertTrue(js.contains("readErrorCodeMeta() || codeFromPath("),
                "error.js attach() must prefer readErrorCodeMeta() over codeFromPath() with || fallback (#11)");
    }

    /**
     * #11 client: codeFromPath default must be "500" (matches computeErrorStatus else→500),
     * not "404".
     */
    @Test
    public void test_errorJs_codeFromPathDefaultIs500() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/error.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("return \"500\";"), "error.js codeFromPath default must return \"500\" (#11)");
        assertFalse(js.contains("return \"404\";"), "error.js codeFromPath must not default to \"404\" (#11)");
    }

    // ── Parity Round 3: Task #10, A5 — cache hq passthrough, banner dedup, date format ──

    /**
     * R3-#10 / A5: search.js cache link must use &hq= (not &hl.q=) for the fallback
     * highlight param, since the v2 CacheHandler only honours the "hq" parameter.
     */
    @Test
    public void test_searchJs_cacheLinkUsesHqNotHlQ() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        // The fallback param must be &hq= (marker comment + code)
        assertTrue(js.contains("cache-link-hq"), "search.js must contain [marker: cache-link-hq] comment (R3-#10/A5)");
        assertTrue(js.contains("\"&hq=\" + encodeURIComponent(state.q"),
                "search.js cache link fallback must use &hq= not &hl.q= (v2 CacheHandler only reads hq)");
        // The wrong param must not appear in cache link construction
        assertFalse(js.contains("\"&hl.q=\" + encodeURIComponent(state.q"),
                "search.js cache link fallback must not use &hl.q= (wrong param name for v2 CacheHandler)");
    }

    /**
     * R3-#10: cache.js must parse hq from location.search and forward them to the
     * GET /api/v2/cache/{docId}?hq=…&hq=… request (multi-valued).
     */
    @Test
    public void test_cacheJs_forwardsHqParamsToApi() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/cache.js"), StandardCharsets.UTF_8);
        // Marker comment asserts the intent
        assertTrue(js.contains("cache-hq-forward"), "cache.js must contain [marker: cache-hq-forward] comment (R3-#10)");
        // Must read hq from current URL params
        assertTrue(js.contains("getAll(\"hq\")"), "cache.js must call params.getAll(\"hq\") to collect multi-valued hq (R3-#10)");
        // Must pass hq as an api.get params object (not in the path)
        assertTrue(js.contains("{ hq: hqValues }"), "cache.js must forward hq as api.get params object (R3-#10)");
        assertTrue(js.contains("api.get(\"/cache/\" + encodeURIComponent(docId), cacheApiParams)"),
                "cache.js must pass cacheApiParams to api.get for hq forwarding (R3-#10)");
    }

    /**
     * R3-#10 / A5 (banner dedup): cache.js must suppress the SPA's own out-of-iframe
     * banner since the API response content already contains one rendered by cache.hbs.
     * The metadata block and back-to-results link are kept (they are not duplicates).
     */
    @Test
    public void test_cacheJs_bannerSuppressed() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/cache.js"), StandardCharsets.UTF_8);
        // Suppression marker must be present
        assertTrue(js.contains("cache-banner-suppressed"), "cache.js must contain [marker: cache-banner-suppressed] (R3-A5 dedup)");
        // The SPA must NOT construct the out-of-iframe alert div with cache-banner class
        assertFalse(js.contains("className = \"alert alert-secondary cache-banner\""),
                "cache.js must not render the SPA out-of-iframe cache banner (would be a duplicate of the in-content banner)");
        // Back-to-results link must still be present
        assertTrue(js.contains("labels.cache_back_to_results"), "cache.js must still render the back-to-results link");
        // Metadata block must still be present
        assertTrue(js.contains("cache-meta"), "cache.js must still render the metadata block");
    }

    /**
     * R3-#10 (created date): cache.js must use the formatDate helper from format.js
     * to format the created date, with fallback to the raw string for unparseable values.
     */
    @Test
    public void test_cacheJs_usesFormatDateForCreated() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/cache.js"), StandardCharsets.UTF_8);
        // Marker comment
        assertTrue(js.contains("cache-created-formatdate"), "cache.js must contain [marker: cache-created-formatdate] (R3-#10)");
        // Must import formatDate from format.js
        assertTrue(js.contains("import { formatDate } from \"./format.js\""), "cache.js must import formatDate from format.js (R3-#10)");
        // Must call formatDate with the raw created value
        assertTrue(js.contains("formatDate(rawCreated)"), "cache.js must call formatDate(rawCreated) for display (R3-#10)");
        // Must fall back to raw string when formatDate returns ""
        assertTrue(js.contains("formatDate(rawCreated) || rawCreated"),
                "cache.js must fall back to raw created string when formatDate returns empty (R3-#10)");
    }

    // ── Parity Round 3: Tasks #1, A2, A7 ──────────────────────────────────────

    /** R3-#1 / A7: index.html must contain the results-header popular-words slot. */
    @Test
    public void test_indexHtml_hasResultsPopularWordsSlot() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        assertTrue(html.contains("id=\"results-popular-words\""),
                "index.html must contain #results-popular-words slot for non-empty results page (parity-r3 #1)");
    }

    /** R3-A7: search.js must define the unified renderPopularWords(words, targetEl) renderer. */
    @Test
    public void test_searchJs_hasUnifiedRenderPopularWords() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("export function renderPopularWords("),
                "search.js must export a unified renderPopularWords(words, targetEl) function (parity-r3 A7)");
        // JSP parity: first 3 always-visible, remainder d-sm-inline-block (hidden on xs).
        assertTrue(js.contains("d-sm-inline-block"), "renderPopularWords must use d-sm-inline-block for words beyond index 2 (JSP parity)");
        // No hard slice-to-5 in the unified renderer.
        assertFalse(js.contains("words.slice(0, 5)"), "unified renderer must not hard-slice to 5 words (parity-r3 A7)");
    }

    /** R3-#1: search.js must load popular words into the results-header slot when features.popular_word is set. */
    @Test
    public void test_searchJs_loadsResultsPopularWordsGatedOnFeature() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("results-popular-words"),
                "search.js must reference #results-popular-words for non-empty results page (parity-r3 #1)");
        assertTrue(js.contains("features.popular_word"),
                "search.js must gate results popular-words on features.popular_word flag (parity-r3 #1)");
    }

    /** R3-A7: app.js must delegate to the shared renderPopularWords (no more inline slice-to-5). */
    @Test
    public void test_appJs_delegatesToSharedRenderPopularWords() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/app.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("search.renderPopularWords("),
                "app.js renderHomePopularWords must delegate to shared search.renderPopularWords (parity-r3 A7)");
        assertFalse(js.contains(".slice(0, 5)"), "app.js must not hard-slice popular words to 5 after A7 unification (parity-r3 A7)");
    }

    /**
     * R3-A2: the zero-result branch of renderResults must NOT synchronously clear related
     * queries/content — doing so would race against the async loadRelated() call and wipe
     * its output.  Assert the guard comment is present and the clearing calls are absent
     * from that specific branch.
     */
    @Test
    public void test_searchJs_zeroResultBranchDoesNotClearRelated() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        // The guard comment introduced by parity-r3 A2 must be present.
        assertTrue(js.contains("parity-r3 A2"), "search.js zero-result branch must contain the parity-r3 A2 guard comment");
        // The async loadRelated() call in runSearch() must still exist (it populates related on zero-result too).
        assertTrue(js.contains("loadRelated(state.q"),
                "search.js must still call loadRelated() after renderResults (populates related on zero-result)");
    }

    /** R3: All 16 i18n bundles must contain the labels.search_popular_word_word key. */
    @Test
    public void test_i18n_hasSearchPopularWordWordKey() throws Exception {
        try (Stream<Path> files = Files.list(THEME_DIR.resolve("i18n"))) {
            files.filter(p -> p.getFileName().toString().startsWith("messages.") && p.getFileName().toString().endsWith(".json"))
                    .forEach(p -> {
                        try {
                            final String json = Files.readString(p, StandardCharsets.UTF_8);
                            assertTrue(json.contains("\"labels.search_popular_word_word\""),
                                    "i18n bundle " + p.getFileName() + " must contain labels.search_popular_word_word (parity-r3 A7)");
                        } catch (final Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    // ── Parity Round 3: Task 2 — Home options: label/lang/flash/advance-link ──

    /** R3-T2 #3: index.html must contain a home label select element (id="home-label-select"). */
    @Test
    public void test_indexHtml_hasHomeLabelSelect() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        assertTrue(html.contains("id=\"home-label-select\""),
                "index.html must contain #home-label-select for parity with searchOptions.jsp label multi-select (parity-r3 Task 2 #3)");
    }

    /** R3-T2 lang: home-lang-select must carry the multiple attribute (parity with searchOptions.jsp:75-84). */
    @Test
    public void test_indexHtml_homeLangSelectIsMultiple() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        // The home-lang-select element must declare multiple on the same element.
        // A simple substring check: the element line contains both home-lang-select and multiple.
        assertTrue(html.contains("id=\"home-lang-select\""), "index.html must contain #home-lang-select (parity-r3 Task 2)");
        // Verify search.js no longer strips multiple from the home lang select.
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertFalse(js.contains("homeLang.removeAttribute(\"multiple\")"),
                "search.js must not strip multiple from the home lang select (parity-r3 Task 2)");
    }

    /** R3-T2 flash: index.html must contain the #home-flash aria-live region. */
    @Test
    public void test_indexHtml_hasHomeFlashRegion() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        assertTrue(html.contains("id=\"home-flash\""), "index.html must contain #home-flash aria-live region (parity-r3 Task 2 minor)");
        assertTrue(html.contains("aria-live"), "home-flash must be an aria-live region");
    }

    /** R3-T2 A8: app.js advance-link forwarding must include paging/state params (num, sort, lang, fields.label). */
    @Test
    public void test_appJs_advanceLinkForwardsPagingParams() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/app.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("advParams.set(\"num\""),
                "app.js updateAdvanceLinks must forward num param onto advance link (parity-r3 A8)");
        assertTrue(js.contains("advParams.set(\"sort\""),
                "app.js updateAdvanceLinks must forward sort param onto advance link (parity-r3 A8)");
        assertTrue(js.contains("advParams.append(\"lang\""),
                "app.js updateAdvanceLinks must forward lang params onto advance link (parity-r3 A8)");
        assertTrue(js.contains("advParams.append(\"fields.label\""),
                "app.js updateAdvanceLinks must forward fields.label params onto advance link (parity-r3 A8)");
    }

    // ── Parity Round 3: Tasks #4 (A4), #6, #5 (A3) ───────────────────────────

    /**
     * R3-A4: advance.js must use the field "timestamp" with exact JSP date-math values
     * (advance.jsp:242-253 / QueryStringBuilder:242-244), not "last_modified".
     * The timestamp condition must be appended to q, not emitted as ex_q.
     */
    @Test
    public void test_advanceJs_timestampFieldAndExactDateMath() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/advance.js"), StandardCharsets.UTF_8);
        // Correct field + exact JSP values
        assertTrue(js.contains("timestamp:[now-1d/d TO *]"), "advance.js must emit timestamp:[now-1d/d TO *] (JSP parity, parity-r3 A4)");
        assertTrue(js.contains("timestamp:[now-1w/d TO *]"), "advance.js must emit timestamp:[now-1w/d TO *] (JSP parity, parity-r3 A4)");
        assertTrue(js.contains("timestamp:[now-1M/d TO *]"), "advance.js must emit timestamp:[now-1M/d TO *] (JSP parity, parity-r3 A4)");
        assertTrue(js.contains("timestamp:[now-1y/d TO *]"), "advance.js must emit timestamp:[now-1y/d TO *] (JSP parity, parity-r3 A4)");
        // Wrong field must not appear in the query-composition path
        assertFalse(js.contains("last_modified:[now"), "advance.js must not emit last_modified date-math (wrong field, parity-r3 A4)");
        // Timestamp is appended to q, not ex_q
        assertFalse(js.contains("params.set(\"ex_q\", TIME_RANGE_QUERY"), "advance.js must not forward timestamp via ex_q (parity-r3 A4)");
    }

    /**
     * R3-A4: advance.js TIME_RANGES must contain exactly four options (day/week/month/year);
     * the 3month and 6month options present in the old code must be gone.
     */
    @Test
    public void test_advanceJs_timestampOptionSetMatchesJsp() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/advance.js"), StandardCharsets.UTF_8);
        // Must not contain 3month or 6month option values
        assertFalse(js.contains("\"3month\""), "advance.js must not contain 3month option (not in JSP, parity-r3 A4)");
        assertFalse(js.contains("\"6month\""), "advance.js must not contain 6month option (not in JSP, parity-r3 A4)");
        // The four correct label keys must be referenced
        assertTrue(js.contains("labels.facet_timestamp_1day"), "advance.js must use labels.facet_timestamp_1day (parity-r3 A4)");
        assertTrue(js.contains("labels.facet_timestamp_1week"), "advance.js must use labels.facet_timestamp_1week (parity-r3 A4)");
        assertTrue(js.contains("labels.facet_timestamp_1month"), "advance.js must use labels.facet_timestamp_1month (parity-r3 A4)");
        assertTrue(js.contains("labels.facet_timestamp_1year"), "advance.js must use labels.facet_timestamp_1year (parity-r3 A4)");
    }

    /**
     * R3-#6: advance.js compose() must emit filetype:"value" (quoted, matching
     * QueryStringBuilder:236-238) and split none-words on whitespace so each token
     * becomes NOT &lt;token&gt; (matching QueryStringBuilder:230-235).
     */
    @Test
    public void test_advanceJs_filetypeQuotedAndNoneWordsWhitespaceSplit() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/advance.js"), StandardCharsets.UTF_8);
        // filetype emission must include a literal quote after the colon
        assertTrue(js.contains("filetype:\""), "advance.js compose() must emit filetype:\"<value>\" with quotes (parity-r3 #6)");
        // none-words must split on whitespace (split(/\s+/)), not use the quote-aware tokenizer
        assertTrue(js.contains("split(/\\s+/)"), "advance.js none-words handler must split on whitespace (parity-r3 #6)");
    }

    /**
     * R3-A3 (#5): advance.js must implement best-effort prefill from the incoming URL:
     * site:, filetype:, NOT tokens, quoted phrase (exact), occt prefix, and the
     * non-q params (lang, sort, num, fields.label).
     */
    @Test
    public void test_advanceJs_prefillBestEffortFromUrl() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/advance.js"), StandardCharsets.UTF_8);
        // site recovery
        assertTrue(js.contains("fSite.input.value = siteVal"),
                "advance.js prefill must recover site:<v> into the site field (parity-r3 A3)");
        // filetype recovery
        assertTrue(js.contains("fFiletype.input.value = filetypeVal"),
                "advance.js prefill must recover filetype:\"<v>\" into the filetype select (parity-r3 A3)");
        // exact-phrase recovery
        assertTrue(js.contains("fExact.input.value = exactPhrase"),
                "advance.js prefill must recover quoted phrase into the exact field (parity-r3 A3)");
        // NOT-tokens recovery
        assertTrue(js.contains("fNone.input.value = notTokens"),
                "advance.js prefill must recover NOT tokens into the none field (parity-r3 A3)");
        // all-words fallback (bare tokens)
        assertTrue(js.contains("fAll.input.value = finalBare"),
                "advance.js prefill must dump remaining bare tokens into all-words (parity-r3 A3)");
        // forward lang, sort, num, fields.label
        assertTrue(js.contains("urlParams.getAll(\"lang\")"), "advance.js prefill must forward lang params (parity-r3 A3)");
        assertTrue(js.contains("urlParams.getAll(\"fields.label\")"), "advance.js prefill must forward fields.label params (parity-r3 A3)");
        assertTrue(js.contains("urlParams.get(\"num\")"), "advance.js prefill must forward num param (parity-r3 A3)");
        assertTrue(js.contains("urlParams.get(\"sort\")"), "advance.js prefill must forward sort param (parity-r3 A3)");
    }

    // ── Parity Round 3: Task 5 — Chat parity (#7, #8, #9, minor) ─────────────

    /** R3-T5 #7: index.html must contain the chat-progress-message marker (created dynamically by chat.js). */
    @Test
    public void test_indexHtml_hasChatProgressMessageMarker() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        assertTrue(html.contains("chat-progress-message"),
                "index.html must reference chat-progress-message (created dynamically by chat.js, parity-r3 T5 #7)");
    }

    /** R3-T5 #7: chat.js must reference labels.chat_phase_search and render it into the progress message element. */
    @Test
    public void test_chatJs_phaseNarration() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
        // Phase key is built dynamically: t("labels.chat_phase_" + phase, ...)
        assertTrue(js.contains("labels.chat_phase_"),
                "chat.js must reference labels.chat_phase_ prefix for phase narration (parity-r3 T5 #7)");
        assertTrue(js.contains("progressMessageEl"), "chat.js must render phase narration into progressMessageEl (parity-r3 T5 #7)");
        // The key is constructed dynamically in the phase SSE handler
        assertTrue(js.contains("\"labels.chat_phase_\" + phase"),
                "chat.js must build the phase key dynamically to render per-phase messages (parity-r3 T5 #7)");
    }

    /** R3-T5 #8: chat.js must key the fallback message off data.reason (no_results / no_relevant_results). */
    @Test
    public void test_chatJs_fallbackReasons() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("chat_fallback_no_results"), "chat.js must use labels.chat_fallback_no_results (parity-r3 T5 #8)");
        assertTrue(js.contains("chat_fallback_no_relevant_results"),
                "chat.js must use labels.chat_fallback_no_relevant_results keyed on reason (parity-r3 T5 #8)");
        assertTrue(js.contains("data.reason"), "chat.js fallback handler must key message off data.reason (parity-r3 T5 #8)");
    }

    /** R3-T5 #9: chat.js must restore focus to the chat input on the done SSE event. */
    @Test
    public void test_chatJs_focusRestoreOnDone() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("inputEl.focus()"),
                "chat.js must call inputEl.focus() on the done event to restore focus (parity-r3 T5 #9)");
    }

    /** R3-T5 minor: chat.js must render per-group filter count badges (updateGroupBadge parity). */
    @Test
    public void test_chatJs_perGroupFilterBadge() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("chat-filter-group-badge"),
                "chat.js must render per-group filter count badges (parity-r3 T5 minor updateGroupBadge)");
        assertTrue(js.contains("updateGroupBadge"),
                "chat.js must define/call updateGroupBadge for per-group count badges (parity-r3 T5 minor)");
    }

    /** R3-T5 minor: chat.js must reset active filters on new chat (resetFilters parity). */
    @Test
    public void test_chatJs_resetFiltersOnNewChat() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("resetFilters()"), "chat.js new-chat handler must call resetFilters() (parity-r3 T5 minor resetFilters)");
    }

    /** R3-T5 minor: markdown.js must support nested lists and nested blockquotes. */
    @Test
    public void test_markdownJs_nestedListAndBlockquoteHandling() throws Exception {
        final String md = Files.readString(THEME_DIR.resolve("assets/markdown.js"), StandardCharsets.UTF_8);
        assertTrue(md.contains("renderList"), "markdown.js must define renderList for nested-list support (parity-r3 T5 minor)");
        assertTrue(md.contains("indentDepth"),
                "markdown.js must define indentDepth for nested-list nesting detection (parity-r3 T5 minor)");
        assertTrue(md.contains("renderBlockquote"),
                "markdown.js must define renderBlockquote for nested blockquote support (parity-r3 T5 minor)");
    }

    /** R3-T5: all 7 new chat keys (5 phase + 2 fallback) must exist in messages.en.json. */
    @Test
    public void test_i18n_hasChatPhaseAndFallbackKeys() throws Exception {
        final String en = Files.readString(THEME_DIR.resolve("i18n/messages.en.json"), StandardCharsets.UTF_8);
        for (final String key : new String[] { "labels.chat_phase_intent", "labels.chat_phase_search", "labels.chat_phase_evaluate",
                "labels.chat_phase_fetch", "labels.chat_phase_answer", "labels.chat_fallback_no_results",
                "labels.chat_fallback_no_relevant_results" }) {
            assertTrue(en.contains("\"" + key + "\""), "messages.en.json must contain key: " + key + " (parity-r3 T5)");
        }
    }

    @Test
    public void test_errorJs_codeFromPathDocSays500() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/error.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("Returns \"500\" as the default when no segment matches"),
                "error.js codeFromPath JSDoc must state 500 default (#7)");
        assertFalse(js.contains("Returns \"404\" as the default"), "error.js codeFromPath JSDoc must not state 404 default (#7)");
    }

    @Test
    public void test_errorJs_503ReservedComment() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/error.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("503 path segments are reserved"), "error.js must mark the 503 path mappings as reserved (#8)");
    }

    @Test
    public void test_chatNavUsesAiMode() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/app.js"), StandardCharsets.UTF_8);
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        assertTrue(js.contains("t(\"nav.chat_ai_mode\")"), "renderChatNavLink must use nav.chat_ai_mode (parity #4)");
        assertTrue(html.contains("id=\"chat-nav-link\" data-spa data-i18n=\"nav.chat_ai_mode\""),
                "chat nav markup must use nav.chat_ai_mode (parity #4)");
    }
}
