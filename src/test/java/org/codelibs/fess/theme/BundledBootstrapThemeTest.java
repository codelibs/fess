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
        // The redesign migrates the geo controls into the shared #searchOptions drawer
        // (#geoSearchOptionFieldset) and drops the dedicated geo-apply button: geo is applied
        // via the drawer's main Search button and cleared via #searchOptionsClearButton.
        assertTrue(html.contains("id=\"geoSearchOptionFieldset\""));
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
            // The redesign drops the separate geo apply/clear actions (geo is applied via the
            // shared #searchOptions drawer's Search/Clear buttons), so geo_apply/geo_clear keys
            // are no longer required.
            for (final String k : new String[] { "search.geo", "search.geo_lat", "search.geo_lon", "search.geo_distance" }) {
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
        // The redesign unifies the home and results options into a single shared
        // #searchOptions collapse drawer (parity with searchOptions.jsp). The home view
        // carries only the toggle button that opens that shared drawer.
        assertTrue(html.contains("id=\"home-options-toggle\""), "home view must have an Options toggle button");
        assertTrue(html.contains("data-bs-target=\"#searchOptions\""), "home options toggle must open the shared #searchOptions drawer");
        assertTrue(html.contains("id=\"sortSearchOption\""), "shared search-options drawer must include a sort select");
        assertTrue(html.contains("id=\"numSearchOption\""), "shared search-options drawer must include a num select");
        assertTrue(html.contains("id=\"langSearchOption\""), "shared search-options drawer must include a lang select");
    }

    @Test
    public void test_searchJs_populatesLabelOption() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        // The home and results views share one #searchOptions drawer; search.js dynamically
        // populates its label multi-select (#labelSearchOption) from the config label options
        // (renderLabelOptions). The sort/num/lang selects are static in index.html.
        assertTrue(js.contains("labelSearchOption"), "search.js must populate the shared label option control");
    }

    @Test
    public void test_searchJs_facetQueryViewShowsNotFound() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        // The redesign keeps the group's title header (li.list-group-item.text-uppercase)
        // even when every query count is zero, matching the JSP sidebar which never drops
        // an empty group's header.
        assertTrue(js.contains("list-group-item text-uppercase"),
                "search.js must always render the facet-query group title header even when all counts are zero (JSP parity)");
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
        assertTrue(js.contains("getElementById(\"searchButton\")"),
                "search.js must wire the 3s disable to the header submit button (#searchButton)");
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
        // The fallback highlight param must be &hq= (v2 CacheHandler only reads hq).
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
        // Home and results share one #searchOptions drawer; the label multi-select is
        // the shared #labelSearchOption (parity with searchOptions.jsp label multi-select).
        assertTrue(html.contains("id=\"labelSearchOption\""),
                "index.html must contain the shared #labelSearchOption for parity with searchOptions.jsp label multi-select (parity-r3 Task 2 #3)");
    }

    /** R3-T2 lang: home-lang-select must carry the multiple attribute (parity with searchOptions.jsp:75-84). */
    @Test
    public void test_indexHtml_homeLangSelectIsMultiple() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        // The shared #langSearchOption element must declare multiple on the same element.
        final int langIdx = html.indexOf("id=\"langSearchOption\"");
        assertTrue(langIdx >= 0, "index.html must contain the shared #langSearchOption (parity-r3 Task 2)");
        final String langLine = html.substring(Math.max(0, langIdx - 120), Math.min(html.length(), langIdx + 40));
        assertTrue(langLine.contains("multiple"), "shared lang select must carry the multiple attribute (parity searchOptions.jsp:75-84)");
        // Verify search.js no longer strips multiple from the lang select.
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertFalse(js.contains("homeLang.removeAttribute(\"multiple\")"),
                "search.js must not strip multiple from the lang select (parity-r3 Task 2)");
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

    /** R3-T5 #7: chat.js must create the chat-progress-message element dynamically. */
    @Test
    public void test_chatJs_hasChatProgressMessageMarker() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("chat-progress-message"),
                "chat.js must create the chat-progress-message element dynamically (parity-r3 T5 #7)");
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
        // The chat nav is a data-spa link with the i18n label on its inner <span>.
        assertTrue(html.contains("id=\"chat-nav-link\" data-spa"), "chat nav link must be a data-spa link (parity #4)");
        assertTrue(html.contains("data-i18n=\"nav.chat_ai_mode\""), "chat nav markup must use nav.chat_ai_mode (parity #4)");
    }

    @Test
    public void test_appJs_chatHeaderSearchLink() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/app.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("header-search-link"), "app.js must swap the chat nav into a Search link on the chat route (parity #F)");
        assertTrue(js.contains("setChatNavSearchMode(true)"), "app.js chat route must enable Search-link mode (parity #F)");
        assertTrue(js.contains("setChatNavSearchMode(false)"), "app.js non-chat routes must restore the chat label (parity #F)");
    }

    @Test
    public void test_i18n_hasNavSearchKey() throws Exception {
        try (java.util.stream.Stream<java.nio.file.Path> files = Files.list(THEME_DIR.resolve("i18n"))) {
            files.filter(p -> p.getFileName().toString().startsWith("messages.") && p.getFileName().toString().endsWith(".json"))
                    .forEach(p -> {
                        try {
                            assertTrue(Files.readString(p, StandardCharsets.UTF_8).contains("\"nav.search\""),
                                    "bundle " + p.getFileName() + " must contain nav.search (parity #F)");
                        } catch (final Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    @Test
    public void test_homeSuggestWired() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        final String js = Files.readString(THEME_DIR.resolve("assets/app.js"), StandardCharsets.UTF_8);
        assertTrue(html.contains("id=\"home-suggest-dropdown\""), "index.html must contain the home suggest dropdown (parity #1)");
        assertTrue(js.contains("search.attachSuggest(input, homeSuggest"), "app.js must attach suggest to the home input (parity #1)");
    }

    @Test
    public void test_homeOptionsClearButton() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        // Home and results share the single #searchOptions drawer, so its clear control is
        // the shared #searchOptionsClearButton, wired in search.js attach() (not app.js).
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(html.contains("id=\"searchOptionsClearButton\""), "shared search-options drawer must have a clear button (parity #5)");
        assertTrue(js.contains("searchOptionsClearButton"), "search.js must wire the shared search-options clear button (parity #5)");
    }

    @Test
    public void test_appJs_wiresHomeFlash() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/app.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("home-flash-query-message"), "app.js attachHomeView must surface a query-param flash message (parity #A)");
        assertTrue(js.contains("renderHomeFlash(t(\"flash."), "app.js must call renderHomeFlash with a flash.* i18n key (parity #A)");
    }

    @Test
    public void test_i18n_hasFlashKeys() throws Exception {
        try (java.util.stream.Stream<java.nio.file.Path> files = Files.list(THEME_DIR.resolve("i18n"))) {
            files.filter(p -> p.getFileName().toString().startsWith("messages.") && p.getFileName().toString().endsWith(".json"))
                    .forEach(p -> {
                        try {
                            final String s = Files.readString(p, StandardCharsets.UTF_8);
                            assertTrue(s.contains("\"flash.login_required\""),
                                    "bundle " + p.getFileName() + " must contain flash.login_required (parity #A)");
                            assertTrue(s.contains("\"flash.session_expired\""),
                                    "bundle " + p.getFileName() + " must contain flash.session_expired (parity #A)");
                        } catch (final Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    @Test
    public void test_searchJs_favoritePostsQueryId() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("toggleFavorite(docId, btn, li.dataset.queryId"),
                "favorite click must forward the card queryId (parity #3)");
        assertTrue(js.contains("{ query_id: queryId || \"\" }"), "favorite POST body must include query_id (parity #3)");
    }

    @Test
    public void test_chatJs_inlineProgressMessage() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("chat-inline-progress-message"), "chat.js inline panel must build a progress message element (parity #2)");
        assertTrue(js.contains("progressMessageEl: inlineProgressMessageEl"),
                "chat.js inline refs must pass progressMessageEl (parity #2)");
    }

    @Test
    public void test_chatJs_phaseKeywordScope() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("? t(\"labels.chat_phase_\" + phase, [data.keywords])"),
                "phase narration must only substitute keywords when present (parity #6)");
        assertFalse(js.contains("? data.keywords : question"), "phase narration must not fall back to the full question (parity #6)");
    }

    @Test
    public void test_chatJs_errorClearsBubble() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("activeBubble.wrap.remove()"), "chat.js must remove the assistant bubble on error, banner only (parity #D)");
        assertFalse(js.contains("activeBubble.bubble.textContent = msg"),
                "chat.js must not leave the error text in the assistant bubble (parity #D)");
    }

    @Test
    public void test_chatJs_hidesProgressOnDone() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("chat-progress-hide-done"), "chat.js must hide the progress strip on ready/error (parity #C)");
        assertFalse(js.contains("Keep strip visible for user inspection after completion."),
                "chat.js must no longer keep the strip visible after completion (parity #C)");
    }

    @Test
    public void test_markdownJs_h1ThroughH6() throws Exception {
        final String md = Files.readString(THEME_DIR.resolve("assets/markdown.js"), StandardCharsets.UTF_8);
        assertTrue(md.contains("/^(#{1,6}) (.+)$/"), "markdown.js HEADING_RE must accept # (h1) through ###### (h6) (parity #E)");
        assertTrue(md.contains("Math.min(hm[1].length, 6)"), "markdown.js must clamp heading level to 6 (parity #E)");
        assertFalse(md.contains("/^(#{2,4}) (.+)$/"), "markdown.js must no longer restrict headings to h2-h4 (parity #E)");
    }

    // ── Parity Round 4 regression tests ────────────────────────────────────────

    /**
     * R4-1: index.html header must be dark, fixed-top, and hidden on print.
     * The old navbar-expand-lg bg-body-tertiary pattern must be gone.
     */
    @Test
    public void test_indexHtml_headerIsDarkFixedTopPrintNone() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        // Positive assertions — required Bootstrap/Fess classes on the header element
        assertTrue(html.contains("fixed-top"), "header must carry fixed-top class (parity R4 GAP-A)");
        assertTrue(html.contains("bg-dark"), "header must carry bg-dark class (parity R4 GAP-A)");
        assertTrue(html.contains("navbar-expand-md"), "header must carry navbar-expand-md class (parity R4 GAP-A)");
        assertTrue(html.contains("d-print-none"), "header must carry d-print-none to hide on print (parity R4 GAP-A)");
        // data-bs-theme="dark" on the header element so child controls inherit dark palette
        assertTrue(html.contains("data-bs-theme=\"dark\""), "header must set data-bs-theme=\"dark\" (parity R4 GAP-A)");
        // Negative assertions — the old classes must no longer appear on the header
        assertFalse(html.contains("navbar-expand-lg bg-body-tertiary"),
                "header must not use old navbar-expand-lg bg-body-tertiary classes (parity R4 GAP-A)");
    }

    /**
     * R4-2: styles.css must offset body top-padding for the fixed header,
     * and override to 0 in print media so no blank space appears at top of printout.
     */
    @Test
    public void test_stylesCss_bodyPaddingClearsFixedHeader() throws Exception {
        final String css = Files.readString(THEME_DIR.resolve("assets/styles.css"), StandardCharsets.UTF_8);
        // Body must carry a non-zero padding-top as a fallback for the JS-calculated offset
        assertTrue(css.contains("padding-top"), "styles.css must set body padding-top as fixed-header offset (parity R4 GAP-A)");
        // Print media query must zero it out
        assertTrue(css.contains("@media print"), "styles.css must contain a @media print block (parity R4 GAP-A)");
        assertTrue(css.contains("padding-top: 0 !important"),
                "styles.css @media print must reset padding-top to 0 !important (parity R4 GAP-A)");
    }

    /**
     * R4-3: app.js must define syncHeaderOffset() which reads the live header
     * offsetHeight and applies it as body paddingTop (keeps content clear of
     * the fixed-top bar on all viewport sizes and orientations).
     */
    @Test
    public void test_appJs_syncsHeaderOffset() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/app.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("syncHeaderOffset"), "app.js must define/call syncHeaderOffset (parity R4 GAP-A)");
        assertTrue(js.contains("offsetHeight"), "app.js syncHeaderOffset must read header.offsetHeight (parity R4 GAP-A)");
    }

    /**
     * R4-4: index.html suggest-dropdown must carry data-bs-theme="light" so
     * autocomplete suggestions remain readable regardless of the header's dark theme.
     */
    @Test
    public void test_indexHtml_suggestDropdownForcedLight() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        // The suggest-dropdown element must declare a forced light theme
        assertTrue(html.contains("id=\"suggest-dropdown\""), "index.html must contain #suggest-dropdown");
        // Both id and data-bs-theme="light" must appear in close proximity (same element)
        final int idx = html.indexOf("id=\"suggest-dropdown\"");
        assertTrue(idx >= 0, "suggest-dropdown element not found");
        // Check a window around the element for the attribute (element is on a single line)
        final String line = html.substring(Math.max(0, idx - 200), Math.min(html.length(), idx + 300));
        assertTrue(line.contains("data-bs-theme=\"light\""),
                "suggest-dropdown element must carry data-bs-theme=\"light\" (parity R4 GAP-A)");
    }

    /**
     * R4-5: index.html facet panel must use Bootstrap's offcanvas-md pattern so it
     * is accessible as a drawer on small screens and as an inline sidebar on ≥md.
     * Preserved: facet-body and facet-clear must still be present.
     * i18n key: facet.filter_button must be on the mobile toggle button.
     */
    @Test
    public void test_indexHtml_facetsUseResponsiveOffcanvas() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        // The redesign uses a Bootstrap offcanvas drawer (#facetOffcanvas) shown only on
        // small screens (d-md-none) alongside an inline desktop sidebar (#facet-body on >=md),
        // rather than the single offcanvas-md element. The mobile body mirrors the desktop one.
        assertTrue(html.contains("offcanvas-end"),
                "index.html must use an offcanvas drawer for the responsive facet panel (parity R4 GAP-B)");
        // Offcanvas id
        assertTrue(html.contains("id=\"facetOffcanvas\""), "index.html must have id=\"facetOffcanvas\" (parity R4 GAP-B)");
        // Toggle button wiring
        assertTrue(html.contains("data-bs-toggle=\"offcanvas\""),
                "index.html must contain data-bs-toggle=\"offcanvas\" for the mobile facet toggle (parity R4 GAP-B)");
        assertTrue(html.contains("data-bs-target=\"#facetOffcanvas\""),
                "index.html must contain data-bs-target=\"#facetOffcanvas\" on the toggle button (parity R4 GAP-B)");
        assertTrue(html.contains("aria-controls=\"facetOffcanvas\""),
                "index.html must contain aria-controls=\"facetOffcanvas\" (parity R4 GAP-B)");
        // Preserved inner elements: the desktop sidebar body and the mobile offcanvas body.
        assertTrue(html.contains("id=\"facet-body\""), "index.html must keep the desktop facet body (parity R4 GAP-B)");
        assertTrue(html.contains("id=\"facet-body-mobile\""), "index.html must have the mobile offcanvas facet body (parity R4 GAP-B)");
        // i18n key on the filter button
        assertTrue(html.contains("facet.filter_button"),
                "index.html filter toggle must use the facet.filter_button i18n key (parity R4 GAP-B)");
    }

    /**
     * R4-6: index.html search options are behind a collapse toggle so the results
     * page is not cluttered by default; the sub-controls (sort, num, lang, label,
     * geo) must still be present inside the collapse container.
     */
    @Test
    public void test_indexHtml_searchOptionsBehindCollapseToggle() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        // The shared options collapse (#searchOptions) is toggled by badge links rendered into
        // #options-bar by search.js renderOptionsBar() (each carries data-bs-toggle="collapse" +
        // href="#searchOptions"), not a static index.html button.
        final String searchJs = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(searchJs.contains("\"#searchOptions\""),
                "search.js options-bar must toggle the #searchOptions collapse (parity R4 GAP-C)");
        // The collapse container itself
        assertTrue(html.contains("id=\"searchOptions\""), "index.html must have id=\"searchOptions\" collapse container (parity R4 GAP-C)");
        // The collapse class must appear on or near the searchOptions element
        final int idx = html.indexOf("id=\"searchOptions\"");
        assertTrue(idx >= 0, "#searchOptions element not found");
        final String surroundingMarkup = html.substring(Math.max(0, idx - 50), Math.min(html.length(), idx + 50));
        assertTrue(surroundingMarkup.contains("collapse"), "#searchOptions element must carry the collapse class (parity R4 GAP-C)");
        // Sub-controls must still exist inside the collapsed panel
        assertTrue(html.contains("id=\"sortSearchOption\""), "index.html must preserve id=\"sortSearchOption\" (parity R4 GAP-C)");
        assertTrue(html.contains("id=\"numSearchOption\""), "index.html must preserve id=\"numSearchOption\" (parity R4 GAP-C)");
        assertTrue(html.contains("id=\"langSearchOption\""), "index.html must preserve id=\"langSearchOption\" (parity R4 GAP-C)");
        assertTrue(html.contains("id=\"labelSearchOption\""), "index.html must preserve id=\"labelSearchOption\" (parity R4 GAP-C)");
        assertTrue(html.contains("id=\"geoSearchOptionFieldset\""),
                "index.html must preserve id=\"geoSearchOptionFieldset\" (parity R4 GAP-C)");
    }

    /**
     * R4-7: markdown.js must handle horizontal rules (<hr>) and angle-bracket autolinks.
     * HR_RE is the horizontal-rule line regex; the autolink regex operates on post-escape
     * form (&lt;https?:…&gt;).
     */
    @Test
    public void test_markdownJs_horizontalRuleAndAutolink() throws Exception {
        final String md = Files.readString(THEME_DIR.resolve("assets/markdown.js"), StandardCharsets.UTF_8);
        // Horizontal rule output
        assertTrue(md.contains("<hr>"), "markdown.js must render horizontal rules as <hr> (parity R4 GAP-D2)");
        // HR_RE constant name
        assertTrue(md.contains("HR_RE"), "markdown.js must define HR_RE for horizontal-rule detection (parity R4 GAP-D2)");
        // Autolink marker: the regex matches the post-escape form &lt;https?:
        assertTrue(md.contains("&lt;(https?:"), "markdown.js autolink regex must match post-escape &lt;(https?: (parity R4 GAP-D2)");
    }

    /**
     * R4-8: format.js ALLOWED_TAGS whitelist must include "HR" so sanitizeHtml
     * does not strip horizontal rules produced by the markdown renderer.
     */
    @Test
    public void test_formatJs_sanitizerAllowsHr() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/format.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("\"HR\""), "format.js ALLOWED_TAGS must contain \"HR\" (parity R4 GAP-D2)");
    }

    /**
     * R4-9: advance.js per-page fallback list must match JSP parity ([10,20,30,40,50,100]).
     * The shorter legacy list ([10,20,50,100]) must not appear as the fallback.
     */
    @Test
    public void test_advanceJs_perPageFallbackMatchesJsp() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/advance.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("[10, 20, 30, 40, 50, 100]"),
                "advance.js num fallback must be [10, 20, 30, 40, 50, 100] matching JSP parity (parity R4 GAP-E)");
        assertFalse(js.contains("[10, 20, 50, 100]"),
                "advance.js must not use the short legacy num list [10, 20, 50, 100] (parity R4 GAP-E)");
    }

    /**
     * R4-10: search.js copy-url button and cache link must carry d-print-none so
     * they are hidden in print layouts (print-only result hygiene, JSP parity).
     */
    @Test
    public void test_searchJs_copyAndCacheArePrintHidden() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        // Copy-URL button: className string must contain both the base class and d-print-none
        assertTrue(js.contains("url-copy d-print-none"), "search.js copy-url button className must include d-print-none (parity R4 GAP-G)");
        // Cache link: className string must contain d-print-none
        assertTrue(js.contains("cache d-print-none"), "search.js cache link className must include d-print-none (parity R4 GAP-G)");
    }

    /**
     * R4-11: every i18n message bundle must contain the "facet.filter_button" key
     * (added in parity GAP-H so the mobile facet filter toggle has translated text).
     */
    @Test
    public void test_i18n_hasFacetFilterButtonKey() throws Exception {
        try (Stream<Path> files = Files.list(THEME_DIR.resolve("i18n"))) {
            files.filter(p -> p.getFileName().toString().startsWith("messages.") && p.getFileName().toString().endsWith(".json"))
                    .forEach(p -> {
                        try {
                            final String json = Files.readString(p, StandardCharsets.UTF_8);
                            assertTrue(json.contains("\"facet.filter_button\""),
                                    "i18n bundle " + p.getFileName() + " must contain \"facet.filter_button\" (parity R4 GAP-H)");
                        } catch (final Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    /**
     * R4-12: no help JSON file may contain the placeholder string
     * "machine translation pending" — all help files must be fully translated.
     */
    @Test
    public void test_help_noMachineTranslationPending() throws Exception {
        try (Stream<Path> files = Files.list(THEME_DIR.resolve("help"))) {
            files.filter(p -> p.getFileName().toString().endsWith(".json")).forEach(p -> {
                try {
                    final String json = Files.readString(p, StandardCharsets.UTF_8);
                    assertFalse(json.contains("machine translation pending"), "help file " + p.getFileName()
                            + " must not contain 'machine translation pending' placeholder (parity R4 GAP-F)");
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    // ── Home suggest submit-on-select fix ──────────────────────────────────────

    /**
     * search.js attachSuggest() must support an opt-in submitOnSelect option so that
     * callers can request the form to be submitted when a suggestion is chosen.
     * This mirrors the default-JSP suggestor.js and the results-page header suggest
     * (search.js ~line 1298: form.dispatchEvent(new Event("submit"))).
     * Advanced search must NOT opt in — it stays fill-only.
     */
    @Test
    public void test_searchJs_homeSuggestSubmitsOnSelectOptIn() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("submitOnSelect"),
                "search.js attachSuggest() must support the submitOnSelect option so callers can opt in to submitting on suggestion click");
        assertTrue(js.contains("opts.submitOnSelect"),
                "search.js choose() must check opts.submitOnSelect to decide whether to submit the form");
        assertTrue(js.contains("input.form"),
                "search.js choose() must resolve the form via input.form (with fallback) for the submitOnSelect path");
    }

    /**
     * app.js home wiring must enable submitOnSelect: true so that clicking a suggestion
     * on the home page runs the search (matches default-JSP / results-page behavior).
     */
    @Test
    public void test_appJs_homeSuggestEnablesSubmitOnSelect() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/app.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("submitOnSelect: true"),
                "app.js home attachSuggest call must pass submitOnSelect: true so suggestion clicks submit the home search form");
    }

    /**
     * advance.js must NOT pass submitOnSelect — it must remain fill-only so that
     * clicking a suggestion only fills the all-words field without prematurely
     * submitting the advanced search form (other fields would be empty).
     */
    @Test
    public void test_advanceJs_keepsSuggestFillOnly() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/advance.js"), StandardCharsets.UTF_8);
        assertFalse(js.contains("submitOnSelect"),
                "advance.js must NOT pass submitOnSelect — advanced search suggestion clicks must remain fill-only");
    }

    // ── sort/num/lang apply on Search-button press, not on select change ──────────

    /**
     * The sort / num / lang drawer selects must NOT auto-run a search on change.
     * The default JSP search screen only applies these options when a Search button is
     * pressed, so the SPA must do the same: no {@code change -> runSearch()} wiring on the
     * selects, and the Clear button must not dispatch synthetic change events to re-search.
     */
    @Test
    public void test_searchJs_optionSelectsDoNotAutoSearchOnChange() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertFalse(js.contains("sortSelect.addEventListener(\"change\""),
                "search.js must not auto-run a search when the sort select changes");
        assertFalse(js.contains("numSelect.addEventListener(\"change\""),
                "search.js must not auto-run a search when the num select changes");
        assertFalse(js.contains("langSelect.addEventListener(\"change\""),
                "search.js must not auto-run a search when the lang select changes");
        // The Clear button must not dispatch synthetic change events to trigger a re-search.
        assertFalse(js.contains("new Event(\"change\""), "search.js Clear button must not dispatch change events to re-run the search");
    }

    /**
     * The header form (#search-form) submit must apply the current sort / num / lang
     * drawer selections so a manual change to a select is honoured when the Search button is
     * pressed (since the selects no longer auto-run a search on change).
     */
    @Test
    public void test_searchJs_headerSubmitAppliesOptionSelects() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        // Sort is set when chosen and cleared when the placeholder is selected.
        assertTrue(js.contains("params.delete(\"sort\")"),
                "search.js header submit must clear the sort param when the sort select is unset");
        // Lang params are rebuilt from the current multi-select selection.
        assertTrue(js.contains("params.delete(\"lang\")"), "search.js header submit must rebuild lang params from the lang select");
    }

    /**
     * The home form (home-search-form) submit must carry the shared drawer's
     * sort / num / lang selections into the first search.
     */
    @Test
    public void test_appJs_homeSubmitCarriesOptionSelects() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/app.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("getElementById(\"sortSearchOption\")"), "app.js home submit must read the sort select");
        assertTrue(js.contains("getElementById(\"numSearchOption\")"), "app.js home submit must read the num select");
        assertTrue(js.contains("getElementById(\"langSearchOption\")"), "app.js home submit must read the lang select");
    }

    /**
     * runFromUrl() must re-sync the drawer option selects to the URL-derived state on
     * every navigation. attach() renders the selects only once, so without a re-render
     * inside runFromUrl() a navigation (link / back-forward / facet submit) leaves the
     * selects showing stale values. Now that the selects are applied on the Search
     * button, a stale displayed value would be written back into the URL on the next
     * submit, silently reverting the user's actual sort / num / lang.
     */
    @Test
    public void test_searchJs_runFromUrlResyncsOptionSelects() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        final int start = js.indexOf("export function runFromUrl()");
        assertTrue(start >= 0, "search.js must define runFromUrl()");
        // Bound the search to the runFromUrl body (up to the next top-level function).
        final int end = js.indexOf("\nfunction ", start);
        final String body = end > start ? js.substring(start, end) : js.substring(start);
        assertTrue(body.contains("renderSearchOptions()"),
                "runFromUrl() must call renderSearchOptions() so the drawer selects reflect the URL after navigation");
    }

    // ── Functional review fixes: visible failure feedback, loading indicator, data-i18n-alt ──

    /**
     * Fix 1: search.js must surface network/server/auth failures in the VISIBLE
     * #search-error banner — previously these wrote only to the screen-reader-only
     * #results-meta sink, leaving a sighted user with no feedback when a search failed
     * with a 500 or a dropped connection.
     */
    @Test
    public void test_searchJs_surfacesNetworkAndServerErrorsInVisibleBanner() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("t(\"error.network\")"), "search.js must map NetworkError to error.network");
        assertTrue(js.contains("t(\"error.server\")"), "search.js must map server failures to error.server");
        // The resolved message must be written to the visible banner (errBox), not only #results-meta.
        assertTrue(js.contains("errBox.textContent = msg"),
                "search.js must write network/server/auth failures to the visible #search-error banner (not only #results-meta)");
    }

    /**
     * Fix 2: the results view must show a visible loading indicator while a /search
     * request is in flight (cache and chat already had one; search did not).
     */
    @Test
    public void test_indexHtml_hasSearchLoadingIndicator() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        assertTrue(html.contains("id=\"search-loading\""), "index.html must contain the #search-loading indicator");
        assertTrue(html.contains("data-i18n=\"labels.search_loading\""),
                "the loading indicator must use the labels.search_loading i18n key");
    }

    /** Fix 2: search.js must toggle the loading indicator at the start and end of a search. */
    @Test
    public void test_searchJs_togglesLoadingIndicator() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("function showSearchLoading("), "search.js must define showSearchLoading()");
        assertTrue(js.contains("showSearchLoading(true)"), "search.js must show the loading indicator when a search starts");
        assertTrue(js.contains("showSearchLoading(false)"), "search.js must hide the loading indicator when a search settles");
    }

    /** Fix 2: every i18n bundle must contain the labels.search_loading key. */
    @Test
    public void test_i18n_hasSearchLoadingKey() throws Exception {
        try (Stream<Path> files = Files.list(THEME_DIR.resolve("i18n"))) {
            files.filter(p -> p.getFileName().toString().startsWith("messages.") && p.getFileName().toString().endsWith(".json"))
                    .forEach(p -> {
                        try {
                            assertTrue(Files.readString(p, StandardCharsets.UTF_8).contains("\"labels.search_loading\""),
                                    "i18n bundle " + p.getFileName() + " must contain labels.search_loading");
                        } catch (final Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    /**
     * Fix 3: i18n.js applyDom must localize the alt attribute via data-i18n-alt
     * (previously the attribute was present on the logos but never processed).
     */
    @Test
    public void test_i18nJs_appliesDataI18nAlt() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/i18n.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("data-i18n-alt"), "i18n.js applyDom must process data-i18n-alt attributes");
        assertTrue(js.contains("setAttribute(\"alt\""), "i18n.js must set the alt attribute from the data-i18n-alt key");
    }

    /**
     * Fix 3: both theme logos must reference real i18n keys for their localized alt text,
     * so the rendered alt is never the raw key string. The previously-referenced but
     * never-defined labels.index_title key must be gone.
     */
    @Test
    public void test_indexHtml_logoAltKeysResolve() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        final String en = Files.readString(THEME_DIR.resolve("i18n/messages.en.json"), StandardCharsets.UTF_8);
        assertTrue(html.contains("data-i18n-alt=\"labels.header_brand_name\""),
                "header brand logo must localize alt via labels.header_brand_name");
        assertTrue(html.contains("data-i18n-alt=\"page.heading\""), "home logo must localize alt via page.heading");
        assertTrue(en.contains("\"labels.header_brand_name\""), "labels.header_brand_name must exist in en bundle");
        assertTrue(en.contains("\"page.heading\""), "page.heading must exist in en bundle");
        assertFalse(html.contains("data-i18n-alt=\"labels.index_title\""),
                "home logo must not reference the undefined labels.index_title key");
    }
}
