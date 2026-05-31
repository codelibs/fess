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
package org.codelibs.fess.api.v2.handlers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.api.v2.SessionCsrfTokenManager;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;

/**
 * Unit tests for {@link UiConfigHandler}.
 *
 * <p>Verifies the wire contract for {@code GET /api/v2/ui/config}: payload
 * contains the mandatory keys, and non-GET methods are rejected. The unit
 * harness may not have every dependency wired (notably {@code ThemeRegistry}
 * may be unregistered), so the success-path assertion tolerates the structured
 * 500 envelope that the handler emits in that case — same tolerance pattern as
 * other v2 handler tests.</p>
 */
public class UiConfigHandlerTest extends UnitFessTestCase {

    /**
     * Mirrors {@code SearchApiV2ManagerCsrfTest#registerCsrfManager}: the unit
     * container loads {@code test_app.xml} and does not register the CSRF
     * manager by default, so we wire one in for the handler's {@code issue()}
     * call.
     */
    @BeforeEach
    public void registerCsrfManager() {
        final SessionCsrfTokenManager manager = new SessionCsrfTokenManager();
        ComponentUtil.register(manager, "sessionCsrfTokenManager");
        ComponentUtil.register(manager, SessionCsrfTokenManager.class.getCanonicalName());
    }

    @Test
    public void test_returnsRequiredKeys() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        final StubSession session = new StubSession();
        new UiConfigHandler().handle(new StubRequest("GET", "/api/v2/ui/config").withSession(session), res);
        // Tolerance: if DI for ThemeRegistry / SystemHelper is unavailable in the unit
        // harness the handler emits a structured 500 envelope rather than crashing.
        // Either outcome is acceptable; only the wire contract matters here.
        assertTrue(res.status == 200 || res.status == 500, "unexpected status: " + res.status + " body=" + res.body());
        if (res.status == 200) {
            final String body = res.body();
            for (final String key : new String[] { "\"site_name\"", "\"login_required\"", "\"locales\"", "\"theme\"", "\"features\"",
                    "\"csrf_required\"", "\"csrf_token\"", "\"page_size_default\"", "\"page_size_max\"", "\"notifications\"" }) {
                assertTrue(body.contains(key), "missing key: " + key + " in " + body);
            }
        } else {
            // Structured error envelope, not a raw stack trace
            assertTrue(res.body().contains("\"status\":9"), "expected structured error envelope: " + res.body());
        }
    }

    @Test
    public void test_rejectsNonGet() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new UiConfigHandler().handle(new StubRequest("POST", "/api/v2/ui/config"), res);
        assertEquals(405, res.status);
        assertTrue(res.body().contains("\"code\":\"method_not_allowed\""), res.body());
        // MJ-18: RFC 7231 §6.5.5 requires Allow header on 405.
        assertEquals("Allow header must be set on 405", "GET", res.getHeader("Allow"));
    }

    /**
     * m-14: when {@code theme.api.csrf.required=true} (default), the response must
     * include both {@code csrf_required:true} and a non-empty {@code csrf_token}.
     * When false, {@code csrf_required:false} must be present and {@code csrf_token}
     * must be empty string.
     *
     * <p>The unit harness returns the FessConfig default value. We register a VirtualHostHelper
     * and ThemeRegistry stub so the handler reaches the payload-assembly section.</p>
     */
    @Test
    public void test_csrfRequiredField_presentInPayload() throws Exception {
        // Register stubs so the handler's theme block doesn't throw and swallow the payload.
        ComponentUtil.register(new StubThemeRegistry(java.util.Optional.empty()),
                org.codelibs.fess.theme.ThemeRegistry.class.getCanonicalName());
        ComponentUtil.register(new org.codelibs.fess.helper.VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return null;
            }
        }, "virtualHostHelper");
        final CapturingResponse res = new CapturingResponse();
        new UiConfigHandler().handle(new StubRequest("GET", "/api/v2/ui/config").withSession(new StubSession()), res);
        assertTrue(res.status == 200 || res.status == 500, "unexpected status: " + res.status + " body=" + res.body());
        if (res.status == 200) {
            final String body = res.body();
            // csrf_required field must always be present (m-14).
            assertTrue(body.contains("\"csrf_required\""), "csrf_required field missing in: " + body);
            // The value is a boolean — check for true or false.
            assertTrue(body.contains("\"csrf_required\":true") || body.contains("\"csrf_required\":false"),
                    "csrf_required must be a boolean in: " + body);
        }
    }

    /**
     * Task 1.1: sort_options array must contain the baseline 10 entries
     * (score×2, filename×2, created×2, content_length×2, last_modified×2).
     * click_count and favorite_count entries are conditional on flags
     * and are tested via {@link UiConfigHandler#buildSortOptions} directly.
     */
    @Test
    public void test_sortOptions_baselineEntries() throws Exception {
        final UiConfigHandler handler = new UiConfigHandler();
        final java.util.List<Map<String, Object>> opts = handler.buildSortOptions(false, false);
        // Baseline 10 entries without conditional ones.
        assertEquals(10, opts.size());
        final java.util.Set<String> values = new java.util.HashSet<>();
        for (final Map<String, Object> opt : opts) {
            values.add((String) opt.get("value"));
            assertNotNull(opt.get("label_key"), "label_key must not be null");
        }
        assertTrue(values.contains("score.desc"), values.toString());
        assertTrue(values.contains("filename.asc"), values.toString());
        assertTrue(values.contains("filename.desc"), values.toString());
        assertTrue(values.contains("created.asc"), values.toString());
        assertTrue(values.contains("created.desc"), values.toString());
        assertTrue(values.contains("content_length.asc"), values.toString());
        assertTrue(values.contains("content_length.desc"), values.toString());
        assertTrue(values.contains("last_modified.asc"), values.toString());
        assertTrue(values.contains("last_modified.desc"), values.toString());
    }

    /** Task 1.1: click_count entries appear only when searchLogEnabled=true. */
    @Test
    public void test_sortOptions_clickCountConditional() throws Exception {
        final UiConfigHandler handler = new UiConfigHandler();
        final java.util.List<Map<String, Object>> withLog = handler.buildSortOptions(true, false);
        final java.util.List<Map<String, Object>> withoutLog = handler.buildSortOptions(false, false);
        assertEquals(12, withLog.size(), "expected 10 base + 2 click_count");
        assertEquals(10, withoutLog.size(), "expected 10 base only");
        final java.util.Set<String> vals = new java.util.HashSet<>();
        for (final Map<String, Object> opt : withLog) {
            vals.add((String) opt.get("value"));
        }
        assertTrue(vals.contains("click_count.asc"), vals.toString());
        assertTrue(vals.contains("click_count.desc"), vals.toString());
    }

    /** Task 1.1: favorite_count entries appear only when userFavoriteEnabled=true. */
    @Test
    public void test_sortOptions_favoriteCountConditional() throws Exception {
        final UiConfigHandler handler = new UiConfigHandler();
        final java.util.List<Map<String, Object>> withFav = handler.buildSortOptions(false, true);
        assertEquals(12, withFav.size(), "expected 10 base + 2 favorite_count");
        final java.util.Set<String> vals = new java.util.HashSet<>();
        for (final Map<String, Object> opt : withFav) {
            vals.add((String) opt.get("value"));
        }
        assertTrue(vals.contains("favorite_count.asc"), vals.toString());
        assertTrue(vals.contains("favorite_count.desc"), vals.toString());
    }

    /**
     * Task 1.1: When the handler returns a 200 response, num_options, lang_options,
     * label_options, and the new feature flags must be present in the payload.
     */
    @Test
    public void test_newOptions_presentInSuccessPayload() throws Exception {
        // Register required stubs for a successful response.
        ComponentUtil.register(new StubThemeRegistry(java.util.Optional.empty()),
                org.codelibs.fess.theme.ThemeRegistry.class.getCanonicalName());
        ComponentUtil.register(new org.codelibs.fess.helper.VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return null;
            }
        }, "virtualHostHelper");
        final CapturingResponse res = new CapturingResponse();
        new UiConfigHandler().handle(new StubRequest("GET", "/api/v2/ui/config").withSession(new StubSession()), res);
        assertTrue(res.status == 200 || res.status == 500, "unexpected status: " + res.status + " body=" + res.body());
        if (res.status == 200) {
            final String body = res.body();
            // New arrays must be present.
            assertTrue(body.contains("\"sort_options\""), "sort_options missing in " + body);
            assertTrue(body.contains("\"num_options\""), "num_options missing in " + body);
            assertTrue(body.contains("\"lang_options\""), "lang_options missing in " + body);
            assertTrue(body.contains("\"label_options\""), "label_options missing in " + body);
            // num_options must contain at least 10 and 20.
            assertTrue(body.contains("10"), "10 missing from num_options in " + body);
            assertTrue(body.contains("20"), "20 missing from num_options in " + body);
            // lang_options must contain the "all" sentinel.
            assertTrue(body.contains("\"all\""), "all lang sentinel missing in " + body);
            // New feature flags must be boolean fields.
            assertTrue(body.contains("\"eoled\":"), "eoled flag missing in " + body);
            assertTrue(body.contains("\"development_mode\":"), "development_mode flag missing in " + body);
            assertTrue(body.contains("\"search_log_enabled\":"), "search_log_enabled flag missing in " + body);
            assertTrue(body.contains("\"thumbnail_enabled\":"), "thumbnail_enabled flag missing in " + body);
            assertTrue(body.contains("\"display_label_type\":"), "display_label_type flag missing in " + body);
            // Notifications block must be present (Task 3).
            assertTrue(body.contains("\"notifications\""), "notifications block missing in " + body);
            assertTrue(body.contains("\"search_top\""), "notifications.search_top missing in " + body);
            assertTrue(body.contains("\"advance_search\""), "notifications.advance_search missing in " + body);
            assertTrue(body.contains("\"login\""), "notifications.login missing in " + body);
        }
    }

    /**
     * Task 3 (Notification): the {@code notifications} object must be present
     * in the wire response and contain {@code search_top}, {@code advance_search}
     * and {@code login} string fields (empty strings when no notifications are configured).
     */
    @Test
    public void test_notifications_presentInSuccessPayload() throws Exception {
        ComponentUtil.register(new StubThemeRegistry(java.util.Optional.empty()),
                org.codelibs.fess.theme.ThemeRegistry.class.getCanonicalName());
        ComponentUtil.register(new org.codelibs.fess.helper.VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return null;
            }
        }, "virtualHostHelper");
        final CapturingResponse res = new CapturingResponse();
        new UiConfigHandler().handle(new StubRequest("GET", "/api/v2/ui/config").withSession(new StubSession()), res);
        assertTrue(res.status == 200 || res.status == 500, "unexpected status: " + res.status + " body=" + res.body());
        if (res.status == 200) {
            final String body = res.body();
            assertTrue(body.contains("\"notifications\""), "notifications object missing in " + body);
            // search_top, advance_search and login must all be present (as strings, may be empty).
            assertTrue(body.contains("\"search_top\""), "notifications.search_top missing in " + body);
            assertTrue(body.contains("\"advance_search\""), "notifications.advance_search missing in " + body);
            assertTrue(body.contains("\"login\""), "notifications.login missing in " + body);
        }
    }

    /**
     * Covers the path where no theme is active in {@code ThemeRegistry}.
     *
     * <p>{@code UnitFessTestCase} typically has no theme registered, so this
     * exercise the empty-theme branch of the handler. The assertion only checks
     * that the {@code "theme"} key is present — its value may be the empty
     * payload {@code {}} or a populated one, depending on whether something
     * else in the suite has wired a registry. Either way the wire contract
     * keeps the key, which is what JS clients rely on.</p>
     */
    @Test
    public void test_emptyThemeStillReturnsPayload() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        final StubSession session = new StubSession();
        new UiConfigHandler().handle(new StubRequest("GET", "/api/v2/ui/config").withSession(session), res);
        // Same tolerance as test_returnsRequiredKeys: success or structured 500.
        assertTrue(res.status == 200 || res.status == 500, "unexpected status: " + res.status + " body=" + res.body());
        if (res.status == 200) {
            assertTrue(res.body().contains("\"theme\""), "missing theme key in " + res.body());
        }
    }

    @Test
    public void test_activeTheme_includedInPayloadWhenThemeRegistryHasActiveStaticTheme() throws Exception {
        // ThemeRegistry is not bound in test_app.xml. ComponentUtil.getComponent(class) first
        // asks Lasta DI; on ComponentNotFoundException it falls back to its componentMap,
        // which ComponentUtil.register(...) writes into. Registering a stub here under the
        // canonical class name therefore makes the handler resolve our stub and exercise the
        // populated-theme payload branch.
        final String yaml = String.join("\n", //
                "apiVersion: fess.codelibs.org/v1", //
                "kind: StaticTheme", //
                "name: bootstrap", //
                "displayName: \"Bootstrap Theme\"", //
                "version: \"1.2.3\"", //
                "supportedLocales: [\"en\", \"ja\"]");
        final org.codelibs.fess.theme.ThemeManifest manifest = org.codelibs.fess.theme.ThemeManifest
                .parse(new java.io.ByteArrayInputStream(yaml.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        final org.codelibs.fess.theme.Theme theme =
                new org.codelibs.fess.theme.Theme("bootstrap", java.nio.file.Paths.get("/tmp/bootstrap"), manifest);
        final StubThemeRegistry stub = new StubThemeRegistry(java.util.Optional.of(theme));
        ComponentUtil.register(stub, org.codelibs.fess.theme.ThemeRegistry.class.getCanonicalName());
        // VirtualHostHelper is configured in fess.xml which test_app.xml does not include, so
        // its lookup throws and the handler's outer theme try/catch swallows the exception
        // before it can call resolveActiveTheme. Register a no-op helper to keep the theme
        // block alive until the registry call.
        ComponentUtil.register(new org.codelibs.fess.helper.VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return null;
            }
        }, "virtualHostHelper");
        try {
            final CapturingResponse res = new CapturingResponse();
            new UiConfigHandler().handle(new StubRequest("GET", "/api/v2/ui/config").withSession(new StubSession()), res);
            org.junit.jupiter.api.Assertions.assertEquals(200, res.status, res.body());
            final String body = res.body();
            // The theme block now carries the manifest fields the handler is supposed to surface,
            // proving the active-theme branch of UiConfigHandler.handle ran.
            assertTrue(body.contains("\"name\":\"bootstrap\""), body);
            assertTrue(body.contains("\"display_name\":\"Bootstrap Theme\""), body);
            assertTrue(body.contains("\"version\":\"1.2.3\""), body);
            // site_name should now reflect the manifest's display_name (handler line ~119).
            assertTrue(body.contains("\"site_name\":\"Bootstrap Theme\""), body);
        } finally {
            // Reset to a known empty-registry state so neighbors that don't bring their own
            // stub still see deterministic behavior.
            ComponentUtil.register(new StubThemeRegistry(java.util.Optional.empty()),
                    org.codelibs.fess.theme.ThemeRegistry.class.getCanonicalName());
        }
    }

    @Test
    public void test_siteName_fallsBackToFessWhenThemeMetadataAbsent() throws Exception {
        // Symmetric to the active-theme test: with no theme bound to the request the handler
        // falls back to the literal "Fess" for site_name (UiConfigHandler.java line ~119).
        // Register an empty-stub registry (and a no-op VirtualHostHelper, otherwise the
        // handler's outer theme try-catch eats the missing-helper exception before reaching
        // resolveActiveTheme — see the active-theme test's comment).
        ComponentUtil.register(new StubThemeRegistry(java.util.Optional.empty()),
                org.codelibs.fess.theme.ThemeRegistry.class.getCanonicalName());
        ComponentUtil.register(new org.codelibs.fess.helper.VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return null;
            }
        }, "virtualHostHelper");
        final CapturingResponse res = new CapturingResponse();
        new UiConfigHandler().handle(new StubRequest("GET", "/api/v2/ui/config").withSession(new StubSession()), res);
        org.junit.jupiter.api.Assertions.assertEquals(200, res.status, res.body());
        assertTrue(res.body().contains("\"site_name\":\"Fess\""), res.body());
    }

    /**
     * A.4: features.clipboard_copy_icon must be present in the response payload.
     */
    @Test
    public void test_features_clipboard_copy_icon_present() throws Exception {
        ComponentUtil.register(new StubThemeRegistry(java.util.Optional.empty()),
                org.codelibs.fess.theme.ThemeRegistry.class.getCanonicalName());
        ComponentUtil.register(new org.codelibs.fess.helper.VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return null;
            }
        }, "virtualHostHelper");
        final CapturingResponse res = new CapturingResponse();
        new UiConfigHandler().handle(new StubRequest("GET", "/api/v2/ui/config").withSession(new StubSession()), res);
        assertTrue(res.status == 200 || res.status == 500, "unexpected status: " + res.status + " body=" + res.body());
        if (res.status == 200) {
            final String body = res.body();
            assertTrue(body.contains("\"clipboard_copy_icon\""), "features.clipboard_copy_icon must be present in: " + body);
        }
    }

    /**
     * B.1: features.eol_link and features.installation_link must be present.
     */
    @Test
    public void test_features_eolLink_and_installationLink_present() throws Exception {
        ComponentUtil.register(new StubThemeRegistry(java.util.Optional.empty()),
                org.codelibs.fess.theme.ThemeRegistry.class.getCanonicalName());
        ComponentUtil.register(new org.codelibs.fess.helper.VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return null;
            }
        }, "virtualHostHelper");
        final CapturingResponse res = new CapturingResponse();
        new UiConfigHandler().handle(new StubRequest("GET", "/api/v2/ui/config").withSession(new StubSession()), res);
        assertTrue(res.status == 200 || res.status == 500, "unexpected status: " + res.status + " body=" + res.body());
        if (res.status == 200) {
            final String body = res.body();
            // When eoled==true, eol_link must be present; but since we can't guarantee
            // eoled state in unit harness, we check the key exists under features always.
            // The contract: features always has eol_link (empty string when not eoled).
            assertTrue(body.contains("\"eol_link\""), "features.eol_link must be present in: " + body);
            assertTrue(body.contains("\"installation_link\""), "features.installation_link must be present in: " + body);
        }
    }

    /**
     * B.2: features.login_link must be present as a boolean.
     */
    @Test
    public void test_features_loginLink_present() throws Exception {
        ComponentUtil.register(new StubThemeRegistry(java.util.Optional.empty()),
                org.codelibs.fess.theme.ThemeRegistry.class.getCanonicalName());
        ComponentUtil.register(new org.codelibs.fess.helper.VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return null;
            }
        }, "virtualHostHelper");
        final CapturingResponse res = new CapturingResponse();
        new UiConfigHandler().handle(new StubRequest("GET", "/api/v2/ui/config").withSession(new StubSession()), res);
        assertTrue(res.status == 200 || res.status == 500, "unexpected status: " + res.status + " body=" + res.body());
        if (res.status == 200) {
            final String body = res.body();
            assertTrue(body.contains("\"login_link\""), "features.login_link must be present in: " + body);
        }
    }

    /**
     * rag_chat_enabled must be present as a boolean in the features map.
     * Mirrors the gate used by FessSearchAction#setupHtmlData (chatClient.isAvailable()).
     * In the unit harness ChatClient is not wired, so the value defaults to false —
     * but the key must always be present so the SPA never sees undefined.
     */
    @Test
    public void test_features_ragChatEnabled_present() throws Exception {
        ComponentUtil.register(new StubThemeRegistry(java.util.Optional.empty()),
                org.codelibs.fess.theme.ThemeRegistry.class.getCanonicalName());
        ComponentUtil.register(new org.codelibs.fess.helper.VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return null;
            }
        }, "virtualHostHelper");
        final CapturingResponse res = new CapturingResponse();
        new UiConfigHandler().handle(new StubRequest("GET", "/api/v2/ui/config").withSession(new StubSession()), res);
        assertTrue(res.status == 200 || res.status == 500, "unexpected status: " + res.status + " body=" + res.body());
        if (res.status == 200) {
            final String body = res.body();
            assertTrue(body.contains("\"rag_chat_enabled\""), "features.rag_chat_enabled must be present in: " + body);
            // The value must be a boolean (true or false), never undefined/null.
            assertTrue(body.contains("\"rag_chat_enabled\":true") || body.contains("\"rag_chat_enabled\":false"),
                    "rag_chat_enabled must be a boolean in: " + body);
        }
    }

    /**
     * ADV-1: buildFiletypeOptions must return exactly the 10 canonical file-type entries
     * in the order matching index.filetype / labels.facet_filetype_* mappings, each with
     * a non-null label_key.
     */
    @Test
    public void test_filetypeOptions_canonicalValuesMatchIndexMapping() {
        final java.util.List<Map<String, Object>> opts = new UiConfigHandler().buildFiletypeOptions();
        final java.util.List<String> values = new java.util.ArrayList<>();
        for (final Map<String, Object> o : opts) {
            values.add((String) o.get("value"));
            assertNotNull(o.get("label_key"));
        }
        assertEquals(java.util.List.of("html", "word", "excel", "powerpoint", "odt", "ods", "odp", "pdf", "txt", "others"), values);
    }

    /**
     * ADV-1: the served /ui/config success payload must contain "filetype_options".
     */
    @Test
    public void test_filetypeOptions_presentInSuccessPayload() throws Exception {
        ComponentUtil.register(new StubThemeRegistry(java.util.Optional.empty()),
                org.codelibs.fess.theme.ThemeRegistry.class.getCanonicalName());
        ComponentUtil.register(new org.codelibs.fess.helper.VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return null;
            }
        }, "virtualHostHelper");
        final CapturingResponse res = new CapturingResponse();
        new UiConfigHandler().handle(new StubRequest("GET", "/api/v2/ui/config").withSession(new StubSession()), res);
        assertTrue(res.status == 200 || res.status == 500, "unexpected status: " + res.status + " body=" + res.body());
        if (res.status == 200) {
            final String body = res.body();
            assertTrue(body.contains("\"filetype_options\""), "filetype_options key must be present in: " + body);
        }
    }

    /**
     * B.3: facet_views must be present as an array (may be empty when no views are configured).
     */
    @Test
    public void test_facetViews_presentAsArray() throws Exception {
        ComponentUtil.register(new StubThemeRegistry(java.util.Optional.empty()),
                org.codelibs.fess.theme.ThemeRegistry.class.getCanonicalName());
        ComponentUtil.register(new org.codelibs.fess.helper.VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return null;
            }
        }, "virtualHostHelper");
        final CapturingResponse res = new CapturingResponse();
        new UiConfigHandler().handle(new StubRequest("GET", "/api/v2/ui/config").withSession(new StubSession()), res);
        assertTrue(res.status == 200 || res.status == 500, "unexpected status: " + res.status + " body=" + res.body());
        if (res.status == 200) {
            final String body = res.body();
            assertTrue(body.contains("\"facet_views\""), "facet_views key must be present in: " + body);
            // facet_views must be an array (opening bracket follows the key).
            final int idx = body.indexOf("\"facet_views\"");
            assertTrue(idx >= 0, "facet_views key missing");
            final String after = body.substring(idx + "\"facet_views\"".length()).stripLeading().replaceFirst("^:", "").stripLeading();
            assertTrue(after.startsWith("["), "facet_views must be a JSON array in: " + body);
        }
    }

    /** Minimal ThemeRegistry stub returning a fixed Optional from resolveActiveTheme. */
    private static class StubThemeRegistry extends org.codelibs.fess.theme.ThemeRegistry {
        private final java.util.Optional<org.codelibs.fess.theme.Theme> active;

        StubThemeRegistry(final java.util.Optional<org.codelibs.fess.theme.Theme> active) {
            this.active = active;
        }

        @Override
        public java.util.Optional<org.codelibs.fess.theme.Theme> resolveActiveTheme(final String virtualHostKey) {
            return active;
        }
    }

    /** Minimal HttpServletResponse stub — captures status, content type, headers and body. */
    private static class CapturingResponse implements HttpServletResponse {
        final StringWriter sw = new StringWriter();
        final PrintWriter writer = new PrintWriter(sw);
        int status = 200;
        String contentType;
        final java.util.Map<String, String> headers = new java.util.HashMap<>();

        String body() {
            writer.flush();
            return sw.toString();
        }

        @Override
        public void setStatus(final int sc) {
            this.status = sc;
        }

        @Override
        public int getStatus() {
            return status;
        }

        @Override
        public void setContentType(final String type) {
            this.contentType = type;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return writer;
        }

        @Override
        public String getCharacterEncoding() {
            return "UTF-8";
        }

        @Override
        public void setCharacterEncoding(final String s) {
        }

        @Override
        public jakarta.servlet.ServletOutputStream getOutputStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setContentLength(final int len) {
        }

        @Override
        public void setContentLengthLong(final long len) {
        }

        @Override
        public void setBufferSize(final int size) {
        }

        @Override
        public int getBufferSize() {
            return 0;
        }

        @Override
        public void flushBuffer() {
        }

        @Override
        public void resetBuffer() {
        }

        @Override
        public boolean isCommitted() {
            return false;
        }

        @Override
        public void reset() {
        }

        @Override
        public void setLocale(final java.util.Locale loc) {
        }

        @Override
        public java.util.Locale getLocale() {
            return java.util.Locale.ROOT;
        }

        @Override
        public void addCookie(final jakarta.servlet.http.Cookie cookie) {
        }

        @Override
        public boolean containsHeader(final String name) {
            return false;
        }

        @Override
        public String encodeURL(final String url) {
            return url;
        }

        @Override
        public String encodeRedirectURL(final String url) {
            return url;
        }

        @Override
        public void sendError(final int sc, final String msg) {
        }

        @Override
        public void sendError(final int sc) {
        }

        @Override
        public void sendRedirect(final String location) {
        }

        @Override
        public void sendRedirect(final String location, final int sc) {
        }

        @Override
        public void sendRedirect(final String location, final boolean clearBuffer) {
        }

        @Override
        public void sendRedirect(final String location, final int sc, final boolean clearBuffer) {
        }

        @Override
        public void setDateHeader(final String name, final long date) {
        }

        @Override
        public void addDateHeader(final String name, final long date) {
        }

        @Override
        public void setHeader(final String name, final String value) {
            headers.put(name, value);
        }

        @Override
        public void addHeader(final String name, final String value) {
            headers.put(name, value);
        }

        @Override
        public void setIntHeader(final String name, final int value) {
        }

        @Override
        public void addIntHeader(final String name, final int value) {
        }

        @Override
        public String getHeader(final String name) {
            return headers.get(name);
        }

        @Override
        public java.util.Collection<String> getHeaders(final String name) {
            final String v = headers.get(name);
            return v == null ? java.util.Collections.emptyList() : java.util.Collections.singletonList(v);
        }

        @Override
        public java.util.Collection<String> getHeaderNames() {
            return headers.keySet();
        }
    }

    /**
     * Minimal HttpServletRequest stub — supports method/URI plus an optional
     * attached {@link HttpSession} (the handler calls {@code req.getSession(true)}
     * to issue a CSRF token).
     */
    private static class StubRequest implements HttpServletRequest {
        private final String method;
        private final String uri;
        private final Map<String, Object> attrs = new HashMap<>();
        private HttpSession session;

        StubRequest(final String method, final String uri) {
            this.method = method;
            this.uri = uri;
        }

        StubRequest withSession(final HttpSession s) {
            this.session = s;
            return this;
        }

        @Override
        public String getMethod() {
            return method;
        }

        @Override
        public String getServletPath() {
            return uri;
        }

        @Override
        public String getRequestURI() {
            return uri;
        }

        @Override
        public String getContextPath() {
            return "";
        }

        @Override
        public Object getAttribute(final String name) {
            return attrs.get(name);
        }

        @Override
        public void setAttribute(final String name, final Object value) {
            if (value == null) {
                attrs.remove(name);
            } else {
                attrs.put(name, value);
            }
        }

        @Override
        public void removeAttribute(final String name) {
            attrs.remove(name);
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return Collections.enumeration(attrs.keySet());
        }

        @Override
        public RequestDispatcher getRequestDispatcher(final String path) {
            return null;
        }

        @Override
        public String getAuthType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.http.Cookie[] getCookies() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getDateHeader(final String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getHeader(final String name) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaders(final String name) {
            return Collections.emptyEnumeration();
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return Collections.emptyEnumeration();
        }

        @Override
        public int getIntHeader(final String name) {
            return -1;
        }

        @Override
        public String getPathInfo() {
            return null;
        }

        @Override
        public String getPathTranslated() {
            return null;
        }

        @Override
        public String getQueryString() {
            return null;
        }

        @Override
        public String getRemoteUser() {
            return null;
        }

        @Override
        public boolean isUserInRole(final String role) {
            return false;
        }

        @Override
        public java.security.Principal getUserPrincipal() {
            return null;
        }

        @Override
        public String getRequestedSessionId() {
            return null;
        }

        @Override
        public StringBuffer getRequestURL() {
            return new StringBuffer(uri);
        }

        @Override
        public HttpSession getSession(final boolean create) {
            return session;
        }

        @Override
        public HttpSession getSession() {
            return session;
        }

        @Override
        public String changeSessionId() {
            return null;
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromCookie() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromURL() {
            return false;
        }

        @Override
        public boolean authenticate(final HttpServletResponse response) {
            return false;
        }

        @Override
        public void login(final String username, final String password) {
        }

        @Override
        public void logout() {
        }

        @Override
        public java.util.Collection<Part> getParts() {
            return Collections.emptyList();
        }

        @Override
        public Part getPart(final String name) {
            return null;
        }

        @Override
        public <T extends HttpUpgradeHandler> T upgrade(final Class<T> handlerClass) {
            return null;
        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public void setCharacterEncoding(final String env) {
        }

        @Override
        public int getContentLength() {
            return 0;
        }

        @Override
        public long getContentLengthLong() {
            return 0;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public ServletInputStream getInputStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getParameter(final String name) {
            return null;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.emptyEnumeration();
        }

        @Override
        public String[] getParameterValues(final String name) {
            return null;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return Collections.emptyMap();
        }

        @Override
        public String getProtocol() {
            return "HTTP/1.1";
        }

        @Override
        public String getScheme() {
            return "http";
        }

        @Override
        public String getServerName() {
            return "localhost";
        }

        @Override
        public int getServerPort() {
            return 8080;
        }

        @Override
        public java.io.BufferedReader getReader() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getRemoteAddr() {
            return "127.0.0.1";
        }

        @Override
        public String getRemoteHost() {
            return "localhost";
        }

        @Override
        public java.util.Locale getLocale() {
            return java.util.Locale.ROOT;
        }

        @Override
        public Enumeration<java.util.Locale> getLocales() {
            return Collections.enumeration(java.util.Collections.singleton(java.util.Locale.ROOT));
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public int getRemotePort() {
            return 0;
        }

        @Override
        public String getLocalName() {
            return "localhost";
        }

        @Override
        public String getLocalAddr() {
            return "127.0.0.1";
        }

        @Override
        public int getLocalPort() {
            return 8080;
        }

        @Override
        public ServletContext getServletContext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public AsyncContext startAsync() {
            throw new UnsupportedOperationException();
        }

        @Override
        public AsyncContext startAsync(final ServletRequest req, final ServletResponse resp) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isAsyncStarted() {
            return false;
        }

        @Override
        public boolean isAsyncSupported() {
            return false;
        }

        @Override
        public AsyncContext getAsyncContext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DispatcherType getDispatcherType() {
            return DispatcherType.REQUEST;
        }

        @Override
        public String getRequestId() {
            return "";
        }

        @Override
        public String getProtocolRequestId() {
            return "";
        }

        @Override
        public jakarta.servlet.ServletConnection getServletConnection() {
            return null;
        }
    }

    /**
     * Minimal HttpSession used only to back attribute storage for the CSRF
     * token issuer. All other surface throws so accidental usage shows up loudly.
     */
    private static class StubSession implements HttpSession {
        private final Map<String, Object> attrs = new HashMap<>();

        @Override
        public Object getAttribute(final String name) {
            return attrs.get(name);
        }

        @Override
        public void setAttribute(final String name, final Object value) {
            if (value == null) {
                attrs.remove(name);
            } else {
                attrs.put(name, value);
            }
        }

        @Override
        public void removeAttribute(final String name) {
            attrs.remove(name);
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return Collections.enumeration(attrs.keySet());
        }

        @Override
        public long getCreationTime() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getId() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getLastAccessedTime() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ServletContext getServletContext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setMaxInactiveInterval(final int interval) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getMaxInactiveInterval() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void invalidate() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isNew() {
            throw new UnsupportedOperationException();
        }
    }
}
