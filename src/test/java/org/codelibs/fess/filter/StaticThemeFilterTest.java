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
package org.codelibs.fess.filter;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.codelibs.fess.theme.StaticThemeResponder;
import org.codelibs.fess.theme.Theme;
import org.codelibs.fess.theme.ThemeManifest;
import org.codelibs.fess.theme.ThemeRegistry;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
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

public class StaticThemeFilterTest extends UnitFessTestCase {

    @Test
    public void test_passesThroughWhenNoActiveStaticTheme() throws Exception {
        final StubRegistry reg = new StubRegistry(null);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubResponder stub = new StubResponder();
        f.setStaticThemeResponder(stub);
        final StubRequest req = new StubRequest("GET", "/search");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertTrue(chain.called);
        assertFalse(stub.servedIndex);
        assertFalse(stub.servedAsset);
    }

    @Test
    public void test_passesThroughForAdminPath() throws Exception {
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubResponder stub = new StubResponder();
        f.setStaticThemeResponder(stub);
        final StubRequest req = new StubRequest("GET", "/admin/dashboard");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertTrue(chain.called);
        assertFalse(stub.servedIndex);
        assertFalse(stub.servedAsset);
    }

    @Test
    public void test_passesThroughForApi() throws Exception {
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubResponder stub = new StubResponder();
        f.setStaticThemeResponder(stub);
        final StubRequest req = new StubRequest("GET", "/api/v1/health");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertTrue(chain.called);
        assertFalse(stub.servedIndex);
        assertFalse(stub.servedAsset);
    }

    @Test
    public void test_doFilter_answersServerSidePageUnderThemesWith404() throws Exception {
        // A static theme is plain files, so a JSP under /themes/ must never reach the
        // container's JSP servlet: not for the active theme, not for another theme, not for
        // any method, and not when no static theme is active.
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        for (final Theme active : new Theme[] { staticTheme, null }) {
            for (final String method : new String[] { "GET", "HEAD", "POST" }) {
                for (final String path : new String[] { "/themes/t/x.jsp", "/themes/other/x.jsp", "/themes/other/sub/x.JSPX",
                        "/themes/other/x.jspf" }) {
                    final StaticThemeFilter f = new StaticThemeFilter();
                    f.setThemeRegistry(new StubRegistry(active));
                    final StubResponder stub = new StubResponder();
                    f.setStaticThemeResponder(stub);
                    final StubResponse res = new StubResponse();
                    final StubChain chain = new StubChain();
                    f.doFilter(new StubRequest(method, path), res, chain);
                    final String label = method + " " + path + " active=" + (active == null ? null : active.getName());
                    assertFalse(chain.called, label + " must not pass through to the container");
                    assertFalse(stub.servedAsset, label + " must not be served as an asset");
                    assertEquals(HttpServletResponse.SC_NOT_FOUND, res.errorStatus, label);
                }
            }
        }
    }

    @Test
    public void test_doFilter_matchesServerSidePageOnTheDecodedPath() throws Exception {
        // The container maps the decoded path to a servlet, so the check must use it rather
        // than the raw request URI (an encoded or ;param-suffixed URI decodes to x.jsp).
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(new StubRegistry(null));
        f.setStaticThemeResponder(new StubResponder());
        final StubResponse res = new StubResponse();
        final StubChain chain = new StubChain();
        f.doFilter(new StubRequest("GET", "/%74hemes/other/x.jsp;a=b").withServletPath("/themes/other/x.jsp"), res, chain);
        assertFalse(chain.called);
        assertEquals(HttpServletResponse.SC_NOT_FOUND, res.errorStatus);
    }

    @Test
    public void test_doFilter_servesTheActiveThemeUnderEverySpellingOfItsAssetPath() throws Exception {
        // The container's default servlet resolves every one of these raw URIs to
        // /themes/alpha/assets/app.js. Matched on the raw URI they fell through the filter to it,
        // skipping the responder's denylist and its asset headers.
        final String resolved = "/themes/alpha/assets/app.js";
        for (final String raw : new String[] { "/themes//alpha/assets/app.js", "/themes/./alpha/assets/app.js",
                "/x/../themes/alpha/assets/app.js", "/themes/../themes/alpha/assets/app.js", "/themes/alpha;x=1/assets/app.js",
                "/themes/%61lpha/assets/app.js", "//themes/alpha/assets/app.js" }) {
            final Theme staticTheme = new Theme("alpha", Paths.get("/tmp/alpha"), null);
            final StaticThemeFilter f = new StaticThemeFilter();
            f.setThemeRegistry(new StubRegistry(staticTheme));
            final StubResponder stub = new StubResponder();
            f.setStaticThemeResponder(stub);
            final StubChain chain = new StubChain();
            f.doFilter(new StubRequest("GET", raw).withServletPath(resolved), new StubResponse(), chain);
            assertTrue(stub.servedAsset, raw + " must be served by the responder");
            assertEquals(raw, "assets/app.js", stub.lastAssetPath);
            assertSame(staticTheme, stub.lastTheme, raw);
            assertFalse(chain.called, raw + " must not reach the container");
        }
    }

    @Test
    public void test_doFilter_servesAssetUnderAContextPathOnTheResolvedPath() throws Exception {
        final Theme staticTheme = new Theme("alpha", Paths.get("/tmp/alpha"), null);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(new StubRegistry(staticTheme));
        final StubResponder stub = new StubResponder();
        f.setStaticThemeResponder(stub);
        final StubChain chain = new StubChain();
        f.doFilter(new StubRequest("GET", "/fess/themes//alpha/assets/app.js").withContextPath("/fess")
                .withServletPath("/themes/alpha/assets/app.js"), new StubResponse(), chain);
        assertTrue(stub.servedAsset);
        assertEquals("assets/app.js", stub.lastAssetPath);
        assertFalse(chain.called);
    }

    @Test
    public void test_doFilter_servesUiPathOnTheResolvedPath() throws Exception {
        // A path parameter or an encoded character does not make /search another route.
        for (final String raw : new String[] { "/search;jsessionid=abc", "/%73earch", "/x/../search" }) {
            final StaticThemeFilter f = new StaticThemeFilter();
            f.setThemeRegistry(new StubRegistry(new Theme("t", Paths.get("/tmp/t"), null)));
            final StubResponder stub = new StubResponder();
            f.setStaticThemeResponder(stub);
            final StubChain chain = new StubChain();
            f.doFilter(new StubRequest("GET", raw).withServletPath("/search"), new StubResponse(), chain);
            assertTrue(stub.servedIndex, raw);
            assertEquals(raw, "/search", stub.lastRequestPath);
            assertFalse(chain.called, raw);
        }
    }

    @Test
    public void test_doFilter_answersPrivateThemeFileWith404UnderEverySpelling() throws Exception {
        // theme.yml, README.md, dotfiles, ... are refused whatever the spelling of the URI, the
        // method, and whether the theme is the active one, an inactive one or there is none.
        final Theme staticTheme = new Theme("alpha", Paths.get("/tmp/alpha"), null);
        final String[][] cases = { //
                { "/themes/alpha/theme.yml", "/themes/alpha/theme.yml" }, //
                { "/themes//alpha/theme.yml", "/themes/alpha/theme.yml" }, //
                { "/themes/./alpha/theme.yml", "/themes/alpha/theme.yml" }, //
                { "/x/../themes/alpha/theme.yml", "/themes/alpha/theme.yml" }, //
                { "/themes/../themes/alpha/README.md", "/themes/alpha/README.md" }, //
                { "/themes/%61lpha/theme.yml", "/themes/alpha/theme.yml" }, //
                { "/themes/alpha;x=1/theme.yml", "/themes/alpha/theme.yml" }, //
                { "/themes/beta/theme.yml", "/themes/beta/theme.yml" }, //
                { "/themes/beta/README.md", "/themes/beta/README.md" }, //
                { "/themes/beta/CHANGELOG.md", "/themes/beta/CHANGELOG.md" }, //
                { "/themes/beta/sub/LICENSE.txt", "/themes/beta/sub/LICENSE.txt" }, //
                { "/themes/beta/.env", "/themes/beta/.env" }, //
                { "/themes/.attic-alpha-1760000000000-1a2b3c4d/index.html", "/themes/.attic-alpha-1760000000000-1a2b3c4d/index.html" }, //
                { "/themes/.attic-alpha-1760000000000-1a2b3c4d/assets/app.js",
                        "/themes/.attic-alpha-1760000000000-1a2b3c4d/assets/app.js" }, //
                { "/themes/.attic-alpha-1760000000000-1a2b3c4d/", "/themes/.attic-alpha-1760000000000-1a2b3c4d/" }, //
                { "/themes/.staging-0a1b2c3d/content/index.html", "/themes/.staging-0a1b2c3d/content/index.html" }, //
                { "/themes/%2eattic-alpha-1-1a2b3c4d/index.html", "/themes/.attic-alpha-1-1a2b3c4d/index.html" }, //
                { "/themes/beta/.git/config", "/themes/beta/.git/config" }, //
                { "/Themes/beta/Theme.YML", "/Themes/beta/Theme.YML" } };
        for (final Theme active : new Theme[] { staticTheme, null }) {
            for (final String method : new String[] { "GET", "HEAD", "POST" }) {
                for (final String[] c : cases) {
                    final StaticThemeFilter f = new StaticThemeFilter();
                    f.setThemeRegistry(new StubRegistry(active));
                    final StubResponder stub = new StubResponder();
                    f.setStaticThemeResponder(stub);
                    final StubResponse res = new StubResponse();
                    final StubChain chain = new StubChain();
                    f.doFilter(new StubRequest(method, c[0]).withServletPath(c[1]), res, chain);
                    final String label = method + " " + c[0] + " active=" + (active == null ? null : active.getName());
                    assertFalse(chain.called, label + " must not pass through to the container");
                    assertFalse(stub.servedAsset, label);
                    assertEquals(HttpServletResponse.SC_NOT_FOUND, res.errorStatus, label);
                }
            }
        }
    }

    @Test
    public void test_doFilter_privateFileCheckLeavesOtherPathsAlone() throws Exception {
        // Negative control: the same file names outside /themes/, and ordinary files inside it,
        // are not refused by this check.
        final Theme staticTheme = new Theme("alpha", Paths.get("/tmp/alpha"), null);
        for (final String path : new String[] { "/docs/README.md", "/admin/theme.yml", "/docs/.attic-x/index.html",
                "/themes/beta/assets/app.js", "/themes/beta/assets/v1.2/app.min.js", "/themes/beta/dot.dir/a..b.js", "/themes/beta/",
                "/themes/beta" }) {
            final StaticThemeFilter f = new StaticThemeFilter();
            f.setThemeRegistry(new StubRegistry(staticTheme));
            f.setStaticThemeResponder(new StubResponder());
            final StubResponse res = new StubResponse();
            final StubChain chain = new StubChain();
            f.doFilter(new StubRequest("GET", path), res, chain);
            assertTrue(chain.called, path + " must pass through");
            assertEquals(0, res.errorStatus, path);
        }
    }

    @Test
    public void test_resolveRequestPath() {
        assertEquals("/themes/t/x.js",
                StaticThemeFilter.resolveRequestPath(new StubRequest("GET", "/themes//t/./x.js").withServletPath("/themes/t/x.js")));
        assertEquals("/themes/t/x.js",
                StaticThemeFilter.resolveRequestPath(new StubRequest("GET", "/fess/themes/t/x.js").withContextPath("/fess")));
        // No servlet path from the container: the raw URI without the context path.
        assertEquals("/themes/t/x.js", StaticThemeFilter
                .resolveRequestPath(new StubRequest("GET", "/fess/themes/t/x.js").withContextPath("/fess").withServletPath("")));
    }

    @Test
    public void test_isPrivateThemeFile() {
        assertTrue(StaticThemeFilter.isPrivateThemeFile("/themes/t/theme.yml"));
        assertTrue(StaticThemeFilter.isPrivateThemeFile("/themes/t/README.md"));
        assertTrue(StaticThemeFilter.isPrivateThemeFile("/themes/t/a/b/.env"));
        assertTrue(StaticThemeFilter.isPrivateThemeFile("/THEMES/t/LICENSE"));
        // A file below a directory whose name starts with a dot is private too: the .attic- and
        // .staging- directories the theme installers keep next to the themes.
        assertTrue(StaticThemeFilter.isPrivateThemeFile("/themes/.attic-t-1760000000000-1a2b3c4d/index.html"));
        assertTrue(StaticThemeFilter.isPrivateThemeFile("/themes/.attic-t-1760000000000-1a2b3c4d/"));
        assertTrue(StaticThemeFilter.isPrivateThemeFile("/themes/.attic-t-1760000000000-1a2b3c4d"));
        assertTrue(StaticThemeFilter.isPrivateThemeFile("/themes/.staging-0a1b2c3d/content/assets/app.js"));
        assertTrue(StaticThemeFilter.isPrivateThemeFile("/themes/t/a/.hidden/b/app.js"));
        assertTrue(StaticThemeFilter.isPrivateThemeFile("/THEMES/.ATTIC-t/index.html"));
        assertFalse(StaticThemeFilter.isPrivateThemeFile("/themes/t/assets/app.js"));
        assertFalse(StaticThemeFilter.isPrivateThemeFile("/themes/t/"));
        assertFalse(StaticThemeFilter.isPrivateThemeFile("/themes/"));
        assertFalse(StaticThemeFilter.isPrivateThemeFile("/themes/t/assets/v1.2/app.min.js"));
        assertFalse(StaticThemeFilter.isPrivateThemeFile("/themes/t/dot.dir/a..b.js"));
        assertFalse(StaticThemeFilter.isPrivateThemeFile("/docs/.attic-t/index.html"));
        assertFalse(StaticThemeFilter.isPrivateThemeFile("/docs/README.md"));
        assertFalse(StaticThemeFilter.isPrivateThemeFile("/theme.yml"));
        assertFalse(StaticThemeFilter.isPrivateThemeFile(""));
    }

    @Test
    public void test_doFilter_passesThroughStaticFileOfInactiveTheme() throws Exception {
        // Only server-side pages are stopped; a plain file of a theme that is not active
        // still passes through to the container as before.
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(new StubRegistry(staticTheme));
        final StubResponder stub = new StubResponder();
        f.setStaticThemeResponder(stub);
        final StubResponse res = new StubResponse();
        final StubChain chain = new StubChain();
        f.doFilter(new StubRequest("GET", "/themes/other/app.js"), res, chain);
        assertTrue(chain.called);
        assertEquals(0, res.errorStatus);
    }

    @Test
    public void test_doFilter_servesHeadLikeGet() throws Exception {
        // HEAD must be served exactly like GET: same allowlist match, same serveIndex call.
        // The container (Tomcat) is responsible for discarding the response body for HEAD --
        // this unit test only proves the filter itself does not special-case HEAD as pass-through.
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubResponder stub = new StubResponder();
        f.setStaticThemeResponder(stub);
        final StubRequest req = new StubRequest("HEAD", "/search");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertFalse(chain.called, "HEAD must be answered by the filter, not passed through");
        assertTrue(stub.servedIndex);
        assertEquals("/search", stub.lastRequestPath);
        assertNull(req.forwardedTo);
    }

    @Test
    public void test_doFilter_servesAdvancePath() throws Exception {
        // /advance joins THEME_UI_PREFIXES: GET /advance is served by the theme (the SPA routes
        // it); only /search/advance was previously allowlisted via the /search prefix.
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubResponder stub = new StubResponder();
        f.setStaticThemeResponder(stub);
        final StubRequest req = new StubRequest("GET", "/advance");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertTrue(stub.servedIndex, "/advance must be served as the SPA index");
        assertEquals("/advance", stub.lastRequestPath);
        assertNull(req.forwardedTo);
        assertFalse(chain.called);
    }

    @Test
    public void test_doFilter_servesUiPathEvenWhenSpaFallbackIsFalse() throws Exception {
        // The manifest flag is deprecated and ignored: even spaFallback=false must still serve
        // the allowlisted UI paths as the SPA entry, since there is no JSP UI left behind them.
        // Use /help (rather than /search, already covered by the renamed
        // test_doFilter_ignoresSpaFallbackForUiPaths below) to broaden path coverage.
        final ThemeManifest manifest = buildManifest(false);
        final Theme staticTheme = new Theme("alpha", Paths.get("/tmp/alpha"), manifest);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubResponder stub = new StubResponder();
        f.setStaticThemeResponder(stub);
        final StubRequest req = new StubRequest("GET", "/help");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertTrue(stub.servedIndex, "spaFallback=false must not prevent serving the SPA index");
        assertEquals("/help", stub.lastRequestPath);
        assertNull(req.forwardedTo);
        assertFalse(chain.called);
    }

    @Test
    public void test_passesThroughPostRequests() throws Exception {
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubResponder stub = new StubResponder();
        f.setStaticThemeResponder(stub);
        final StubRequest req = new StubRequest("POST", "/login");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertTrue(chain.called);
        assertFalse(stub.servedIndex);
        assertFalse(stub.servedAsset);
    }

    @Test
    public void test_servesIndexForUiPath() throws Exception {
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubResponder stub = new StubResponder();
        f.setStaticThemeResponder(stub);
        final StubRequest req = new StubRequest("GET", "/search");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        // The filter serves the SPA index directly in place (no forward).
        assertTrue(stub.servedIndex);
        assertEquals("/search", stub.lastRequestPath);
        assertSame(staticTheme, stub.lastTheme);
        assertNull(req.forwardedTo, "Must not forward; serve index in place");
        // chain should NOT have been called
        assertEquals(false, chain.called);
    }

    @Test
    public void test_servesIndexForRootPath() throws Exception {
        // Regression: GET "/" with an active static theme (default spaFallback) must be
        // served in place (serveIndex), NOT redirected/forwarded. This was the originally
        // reported bug -- the root path must keep its URI and render the SPA entry.
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubResponder stub = new StubResponder();
        f.setStaticThemeResponder(stub);
        final StubRequest req = new StubRequest("GET", "/");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertTrue(stub.servedIndex, "/ must be served as the SPA index");
        assertEquals("/", stub.lastRequestPath);
        assertSame(staticTheme, stub.lastTheme);
        assertNull(req.forwardedTo, "/ must not be forwarded");
        assertFalse(chain.called, "chain must not be called for /");
    }

    @Test
    public void test_servesAssetForMatchingThemeAssetPath() throws Exception {
        final Theme staticTheme = new Theme("alpha", Paths.get("/tmp/alpha"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubResponder stub = new StubResponder();
        f.setStaticThemeResponder(stub);
        final StubRequest req = new StubRequest("GET", "/themes/alpha/assets/app.js");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertTrue(stub.servedAsset);
        assertEquals("assets/app.js", stub.lastAssetPath);
        assertSame(staticTheme, stub.lastTheme);
        assertNull(req.forwardedTo, "Must not forward; serve asset in place");
        assertEquals(false, chain.called);
    }

    @Test
    public void test_passesThroughForNonMatchingThemeAssetPath() throws Exception {
        // Active theme is "alpha"; request is for "beta" assets -- pass through
        final Theme staticTheme = new Theme("alpha", Paths.get("/tmp/alpha"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubResponder stub = new StubResponder();
        f.setStaticThemeResponder(stub);
        final StubRequest req = new StubRequest("GET", "/themes/beta/assets/app.js");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertTrue(chain.called);
        assertFalse(stub.servedIndex);
        assertFalse(stub.servedAsset);
        assertNull(req.forwardedTo);
    }

    @Test
    public void test_passesThroughForJspThemeAssetPaths() throws Exception {
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubResponder stub = new StubResponder();
        f.setStaticThemeResponder(stub);
        for (final String uri : new String[] { "/css/simple/style.css", "/js/simple/app.js", "/images/simple/logo.png" }) {
            final StubRequest req = new StubRequest("GET", uri);
            final StubChain chain = new StubChain();
            f.doFilter(req, new StubResponse(), chain);
            assertTrue(chain.called, "Expected pass-through for " + uri);
        }
        assertFalse(stub.servedIndex);
        assertFalse(stub.servedAsset);
    }

    @Test
    public void test_passesThroughForJspAccountAndErrorPaths() throws Exception {
        // /login still relies on Fess JSP forms and must always pass through.
        // /error, /help, /profile and /chat are now served as the SPA index when a static
        // theme is active (see test_servesIndexForErrorHelpProfileAndChatPaths below).
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubResponder stub = new StubResponder();
        f.setStaticThemeResponder(stub);
        for (final String uri : new String[] { //
                "/login", "/login/", "/login/login" }) {
            final StubRequest req = new StubRequest("GET", uri);
            final StubChain chain = new StubChain();
            f.doFilter(req, new StubResponse(), chain);
            assertTrue(chain.called, "Expected pass-through for " + uri);
            assertNull(req.forwardedTo, "Must not forward to theme view: " + uri);
        }
        assertFalse(stub.servedIndex);
        assertFalse(stub.servedAsset);
    }

    @Test
    public void test_servesIndexForErrorHelpProfileAndChatPaths() throws Exception {
        // /error/*, /help, /profile and /chat are SPA routes when a static theme is active.
        // The SPA reads the pathname and renders the appropriate error, help, profile or chat page.
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        for (final String uri : new String[] { //
                "/error/notFound", "/help", "/profile", "/chat" }) {
            final StubResponder stub = new StubResponder();
            f.setStaticThemeResponder(stub);
            final StubRequest req = new StubRequest("GET", uri);
            final StubChain chain = new StubChain();
            f.doFilter(req, new StubResponse(), chain);
            assertTrue(stub.servedIndex, "Expected index served for " + uri);
            assertEquals("Expected requestPath for " + uri, uri, stub.lastRequestPath);
            assertNull(req.forwardedTo, "Must not forward for " + uri);
            assertFalse(chain.called, "Chain must not be called for SPA route: " + uri);
        }
    }

    @Test
    public void test_servesIndexForErrorPath_withQueryString() throws Exception {
        // Query strings (e.g. /error/notFound?url=...) must not affect routing:
        // getRequestURI() strips the query string per the Servlet spec, so a request
        // to /error/notFound?url=... still serves the SPA index.
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubResponder stub = new StubResponder();
        f.setStaticThemeResponder(stub);
        final StubRequest req = new StubRequest("GET", "/error/notFound");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertTrue(stub.servedIndex);
        assertEquals("/error/notFound", stub.lastRequestPath);
        assertNull(req.forwardedTo);
        assertFalse(chain.called);
    }

    @Test
    public void test_nonHttpRequestPassesThroughWithoutClassCastException() throws Exception {
        // Guard: non-HttpServletRequest must be forwarded to the chain without
        // triggering a ClassCastException. This exercises the defensive cast guard
        // added for cross-context or non-HTTP dispatch scenarios.
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubResponder stub = new StubResponder();
        f.setStaticThemeResponder(stub);

        // A plain ServletRequest/ServletResponse that is NOT an HttpServletRequest.
        final StubChain chain = new StubChain();
        f.doFilter(new NonHttpStubRequest(), new NonHttpStubResponse(), chain);
        assertTrue(chain.called, "Non-HTTP request must pass through without ClassCastException");
        assertFalse(stub.servedIndex);
        assertFalse(stub.servedAsset);
    }

    // --- E-2: a component lookup failure is WARNed once per component, then degrades to DEBUG ---

    private static List<LogEvent> eventsAbout(final LogCapturingAppender appender, final Level level, final String component) {
        return appender.eventsAt(level).stream().filter(e -> e.getMessage().getFormattedMessage().contains(component)).toList();
    }

    @Test
    public void test_registryUnavailable_warnsOnlyOnce() throws Exception {
        // When themeRegistry is NOT injected via setThemeRegistry() the filter falls
        // through to ComponentUtil.getThemeRegistry(), which throws in the slim test
        // harness. The first failure is a WARN carrying the exception; later requests that
        // hit the same failure add nothing at WARN no matter how many arrive.
        final LogCapturingAppender appender = LogCapturingAppender.attach(StaticThemeFilter.class.getName(), Level.INFO);
        try {
            final StaticThemeFilter f = new StaticThemeFilter();
            for (int i = 0; i < 5; i++) {
                final StubChain chain = new StubChain();
                f.doFilter(new StubRequest("GET", "/search"), new StubResponse(), chain);
                assertTrue(chain.called, "filter must pass through when registry is unavailable");
            }
            final List<LogEvent> warns = eventsAbout(appender, Level.WARN, "ThemeRegistry");
            org.junit.jupiter.api.Assertions.assertEquals(1, warns.size(),
                    "exactly 1 WARN must be emitted for ThemeRegistry unavailable across 5 requests; got " + warns.size());
            assertNotNull(warns.get(0).getThrown(), "the first failure carries the exception");

            // What was reported is remembered JVM-wide (a redeploy reloads the class and starts
            // over), so another filter instance does not report the same failure again.
            new StaticThemeFilter().doFilter(new StubRequest("GET", "/search"), new StubResponse(), new StubChain());
            org.junit.jupiter.api.Assertions.assertEquals(1, eventsAbout(appender, Level.WARN, "ThemeRegistry").size());
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_registryUnavailable_laterFailuresDegradeToDebug() throws Exception {
        final LogCapturingAppender appender = LogCapturingAppender.attach(StaticThemeFilter.class.getName(), Level.DEBUG);
        try {
            final StaticThemeFilter f = new StaticThemeFilter();
            for (int i = 0; i < 3; i++) {
                f.doFilter(new StubRequest("GET", "/search"), new StubResponse(), new StubChain());
            }
            org.junit.jupiter.api.Assertions.assertEquals(1, eventsAbout(appender, Level.WARN, "ThemeRegistry").size());
            final List<LogEvent> debugs = eventsAbout(appender, Level.DEBUG, "ThemeRegistry not available");
            org.junit.jupiter.api.Assertions.assertEquals(2, debugs.size(), "the two later failures are logged at DEBUG");
            debugs.forEach(e -> assertNotNull(e.getThrown(), "the DEBUG line carries the exception"));
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_eachComponentLookupFailureIsWarnedOnceIndependently() throws Exception {
        final LogCapturingAppender appender = LogCapturingAppender.attach(StaticThemeFilter.class.getName(), Level.INFO);
        try {
            final StaticThemeFilter f = new StaticThemeFilter();
            // Nothing injected: only the ThemeRegistry lookup is reached, and it fails.
            for (int i = 0; i < 3; i++) {
                f.doFilter(new StubRequest("GET", "/search"), new StubResponse(), new StubChain());
            }
            // An active theme but no responder: the VirtualHostHelper and StaticThemeResponder
            // lookups are reached, and fail in the slim test harness. The ThemeRegistry latch
            // having been used up must not silence them.
            f.setThemeRegistry(new StubRegistry(new Theme("t", Paths.get("/tmp/t"), null)));
            for (int i = 0; i < 3; i++) {
                final StubChain chain = new StubChain();
                f.doFilter(new StubRequest("GET", "/search"), new StubResponse(), chain);
                assertTrue(chain.called, "filter must pass through when the responder is unavailable");
            }
            for (final String component : new String[] { "ThemeRegistry", "VirtualHostHelper", "StaticThemeResponder" }) {
                final List<LogEvent> warns = eventsAbout(appender, Level.WARN, component);
                org.junit.jupiter.api.Assertions.assertEquals(1, warns.size(),
                        component + " must WARN exactly once: " + appender.warnings());
                assertNotNull(warns.get(0).getThrown(), component + ": the first failure carries the exception");
            }
        } finally {
            appender.detach();
        }
    }

    // --- E-3: spaFallback=false -- non-asset requests pass through ---

    /**
     * Builds a minimal valid {@link ThemeManifest} YAML with {@code spaFallback: false}.
     */
    private static ThemeManifest buildManifest(final boolean spaFallback) throws Exception {
        final String yaml = String.join("\n", "apiVersion: fess.codelibs.org/v1", "kind: StaticTheme", "name: alpha",
                "displayName: Alpha Theme", "version: 1.0.0", "spaFallback: " + spaFallback);
        return ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void test_doFilter_ignoresSpaFallbackForUiPaths() throws Exception {
        // Pins the NEW contract: theme.yml's spaFallback flag is deprecated and no longer
        // consulted by the filter, so a theme cannot opt out of serving the allowlisted UI
        // paths -- there is no JSP UI left behind them to fall back to.
        // (Previously, spaFallback=false made the filter pass allowlisted UI requests through
        // to the original Fess routes; that behavior is gone.)
        final ThemeManifest manifest = buildManifest(false);
        final Theme staticTheme = new Theme("alpha", Paths.get("/tmp/alpha"), manifest);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubResponder stub = new StubResponder();
        f.setStaticThemeResponder(stub);

        // Allowlisted UI request; spaFallback=false is ignored.
        final StubRequest req = new StubRequest("GET", "/search");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);

        // Must be served as the SPA index, not passed through.
        assertFalse(chain.called, "spaFallback=false must not stop the filter from serving UI paths");
        assertTrue(stub.servedIndex, "spaFallback=false must still serve the SPA index for UI requests");
        assertEquals("/search", stub.lastRequestPath);
        assertNull(req.forwardedTo);
    }

    @Test
    public void test_spaFallbackTrue_servesUiAsIndex() throws Exception {
        // Symmetric test: when manifest.spaFallback=true (explicit), an allowlisted UI
        // request must be served the SPA index.
        final ThemeManifest manifest = buildManifest(true);
        final Theme staticTheme = new Theme("alpha", Paths.get("/tmp/alpha"), manifest);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubResponder stub = new StubResponder();
        f.setStaticThemeResponder(stub);

        final StubRequest req = new StubRequest("GET", "/search");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);

        // Must serve the SPA index in place.
        assertFalse(chain.called, "spaFallback=true must not pass through allowlisted UI requests");
        assertTrue(stub.servedIndex, "spaFallback=true must serve the SPA index");
        assertEquals("/search", stub.lastRequestPath);
        assertNull(req.forwardedTo);
    }

    @Test
    public void test_passesThroughUnlistedUiPath() throws Exception {
        // Key allowlist behavior: an arbitrary, non-allowlisted UI path must pass through
        // to the standard Fess routes even when a static theme is active and spaFallback
        // is on. Only the allowlisted SPA paths (/, /search, /help, /error, /profile,
        // /cache, /chat) are served as the SPA entry; everything else (e.g. /foo, /bar)
        // is a Fess route.
        final ThemeManifest manifest = buildManifest(true);
        final Theme staticTheme = new Theme("alpha", Paths.get("/tmp/alpha"), manifest);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubResponder stub = new StubResponder();
        f.setStaticThemeResponder(stub);

        for (final String uri : new String[] { "/foo", "/bar" }) {
            final StubRequest req = new StubRequest("GET", uri);
            final StubChain chain = new StubChain();
            f.doFilter(req, new StubResponse(), chain);
            assertTrue(chain.called, "Unlisted UI path must pass through: " + uri);
            assertFalse(stub.servedIndex, "Unlisted UI path must not be served as the SPA index: " + uri);
            assertFalse(stub.servedAsset, "Unlisted UI path must not be served as an asset: " + uri);
            assertNull(req.forwardedTo, "Unlisted UI path must not be forwarded: " + uri);
        }
    }

    @Test
    public void test_spaFallbackFalse_assetPathStillServedAsAsset() throws Exception {
        // Even when spaFallback=false, asset paths (/themes/{name}/...) must still
        // be served as assets -- the spaFallback flag only governs non-asset SPA
        // routing, not static asset delivery.
        final ThemeManifest manifest = buildManifest(false);
        final Theme staticTheme = new Theme("alpha", Paths.get("/tmp/alpha"), manifest);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubResponder stub = new StubResponder();
        f.setStaticThemeResponder(stub);

        final StubRequest req = new StubRequest("GET", "/themes/alpha/assets/app.js");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);

        // Asset path must be served regardless of spaFallback setting.
        assertTrue(stub.servedAsset, "asset paths must be served even when spaFallback=false");
        assertEquals("assets/app.js", stub.lastAssetPath);
        assertNull(req.forwardedTo);
        assertFalse(chain.called, "asset path must not pass through when active theme matches");
    }

    @Test
    public void test_servesIndexForCachePath() throws Exception {
        // /cache/?docId=abc is now an SPA route when a static theme is active.
        // It must be served as the SPA index, not passed through to the legacy
        // CacheAction. The /api/v2/cache/ REST endpoint is unaffected because /api/
        // is not an allowlisted UI path and therefore passes through to Fess.
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        for (final String uri : new String[] { "/cache/", "/cache" }) {
            final StubResponder stub = new StubResponder();
            f.setStaticThemeResponder(stub);
            final StubRequest req = new StubRequest("GET", uri);
            final StubChain chain = new StubChain();
            f.doFilter(req, new StubResponse(), chain);
            assertTrue(stub.servedIndex, "Expected index served for " + uri);
            assertEquals("Expected requestPath for " + uri, uri, stub.lastRequestPath);
            assertNull(req.forwardedTo);
            assertFalse(chain.called, "Chain must not be called for SPA cache route: " + uri);
        }
    }

    @Test
    public void test_spaFallbackDefault_isTrue_whenManifestIsNull() throws Exception {
        // Theme with null manifest (stub with no manifest) falls back to the
        // default spaFallback=true behavior via orElse(true) in the filter.
        final Theme staticTheme = new Theme("alpha", Paths.get("/tmp/alpha"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubResponder stub = new StubResponder();
        f.setStaticThemeResponder(stub);

        final StubRequest req = new StubRequest("GET", "/search");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);

        // Null manifest -> orElse(true) -> spaFallback=true -> serve index.
        assertTrue(stub.servedIndex, "null manifest must default spaFallback=true and serve the SPA index");
        assertEquals("/search", stub.lastRequestPath);
        assertNull(req.forwardedTo);
        assertFalse(chain.called);
    }

    @Test
    public void test_legacyNextPage_redirectsToSearchWithStart() throws Exception {
        final StaticThemeFilter f = filterWithActiveTheme(null);
        final StubResponse res = new StubResponse();
        final StubChain chain = new StubChain();
        f.doFilter(new StubRequest("GET", "/search/next").withQuery("q=fess&pn=2&num=20&sdh=abc"), res, chain);
        assertEquals("/search?q=fess&num=20&sdh=abc&start=40", res.redirectLocation);
        assertFalse(chain.called);
    }

    @Test
    public void test_legacyPaging_followsSearchActionDoMove() {
        final StaticThemeFilter f = new StaticThemeFilter();
        assertEquals("/search?q=a&start=0",
                f.resolveLegacyRedirect(new StubRequest("GET", "/search/prev").withQuery("q=a&pn=1"), "/search/prev"));
        assertEquals("/search?q=a&num=20&start=20",
                f.resolveLegacyRedirect(new StubRequest("GET", "/search/prev").withQuery("q=a&pn=3&num=20"), "/search/prev"));
        assertEquals("/search?q=a&num=20&start=40",
                f.resolveLegacyRedirect(new StubRequest("GET", "/search/move/").withQuery("q=a&pn=3&num=20"), "/search/move/"));
        // No pn: paging.search.page.start, and a stale start is dropped.
        assertEquals("/search?q=a&start=0",
                f.resolveLegacyRedirect(new StubRequest("GET", "/search/next").withQuery("q=a&start=90"), "/search/next"));
        // num 0 or above the max is the max (paging.search.page.max.size=100).
        assertEquals("/search?q=a&num=0&start=100",
                f.resolveLegacyRedirect(new StubRequest("GET", "/search/next").withQuery("q=a&pn=1&num=0"), "/search/next"));
    }

    @Test
    public void test_legacyPaging_keepsOtherParametersAsSent() {
        final StaticThemeFilter f = new StaticThemeFilter();
        assertEquals("/search?q=%E6%A4%9C%E7%B4%A2&fields.label=a&fields.label=b&start=10",
                f.resolveLegacyRedirect(
                        new StubRequest("GET", "/search/next").withQuery("q=%E6%A4%9C%E7%B4%A2&pn=1&fields.label=a&fields.label=b"),
                        "/search/next"));
    }

    @Test
    public void test_legacySearchAndChatClear() {
        final StaticThemeFilter f = new StaticThemeFilter();
        assertEquals("/search?q=a&num=20",
                f.resolveLegacyRedirect(new StubRequest("GET", "/search/search").withQuery("q=a&num=20"), "/search/search"));
        assertEquals("/search", f.resolveLegacyRedirect(new StubRequest("GET", "/search/search/"), "/search/search/"));
        assertEquals("/chat", f.resolveLegacyRedirect(new StubRequest("GET", "/chat/clear").withQuery("sessionId=x"), "/chat/clear"));
        assertNull(f.resolveLegacyRedirect(new StubRequest("GET", "/search"), "/search"));
        assertNull(f.resolveLegacyRedirect(new StubRequest("GET", "/search/index"), "/search/index"));
    }

    @Test
    public void test_legacyRedirect_keepsTheContextPath() throws Exception {
        final StaticThemeFilter f = filterWithActiveTheme(null);
        final StubResponse res = new StubResponse();
        f.doFilter(new StubRequest("GET", "/fess/search/move").withContextPath("/fess").withQuery("q=a&pn=2"), res, new StubChain());
        assertEquals("/fess/search?q=a&start=10", res.redirectLocation);
    }

    @Test
    public void test_legacyUrl_notRedirectedWithoutAnActiveTheme() throws Exception {
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(new StubRegistry(null));
        f.setStaticThemeResponder(new StubResponder());
        final StubResponse res = new StubResponse();
        final StubChain chain = new StubChain();
        f.doFilter(new StubRequest("GET", "/search/next").withQuery("q=a&pn=2"), res, chain);
        assertNull(res.redirectLocation, "JSP mode keeps serving /search/next itself");
        assertTrue(chain.called);
    }

    @Test
    public void test_legacyUrl_redirectsEvenWhenSpaFallbackIsOff() throws Exception {
        // Pins the NEW contract: the legacy-URL redirect is no longer gated by
        // manifest.spaFallback (that check was removed), so it fires the same way whether
        // spaFallback is true or false. buildManifest(false) is the existing helper
        // test_doFilter_ignoresSpaFallbackForUiPaths uses.
        // (Previously, spaFallback=false made the filter pass /search/next through to the
        // original Fess route instead of redirecting; that behavior is gone.)
        final StaticThemeFilter f = filterWithActiveTheme(buildManifest(false));
        final StubResponse res = new StubResponse();
        final StubChain chain = new StubChain();
        f.doFilter(new StubRequest("GET", "/search/next").withQuery("q=a&pn=2"), res, chain);
        assertEquals("/search?q=a&start=20", res.redirectLocation);
        assertFalse(chain.called);
    }

    private static StaticThemeFilter filterWithActiveTheme(final ThemeManifest manifest) {
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(new StubRegistry(new Theme("t", Paths.get("/tmp/t"), manifest)));
        f.setStaticThemeResponder(new StubResponder());
        return f;
    }

    // ===== Stubs =====

    /**
     * A {@link StaticThemeResponder} stub that records calls instead of writing to the response.
     */
    static class StubResponder extends StaticThemeResponder {
        boolean servedIndex = false;
        boolean servedAsset = false;
        Theme lastTheme;
        String lastRequestPath;
        String lastAssetPath;

        @Override
        public void serveIndex(final HttpServletRequest req, final HttpServletResponse res, final Theme theme, final String requestPath) {
            this.servedIndex = true;
            this.lastTheme = theme;
            this.lastRequestPath = requestPath;
        }

        @Override
        public void serveAsset(final HttpServletRequest req, final HttpServletResponse res, final Theme theme, final String assetPath) {
            this.servedAsset = true;
            this.lastTheme = theme;
            this.lastAssetPath = assetPath;
        }
    }

    static class StubRegistry extends ThemeRegistry {
        private final Theme theme;

        StubRegistry(final Theme theme) {
            this.theme = theme;
        }

        @Override
        public Optional<Theme> resolveActiveTheme(final String hostKey) {
            return Optional.ofNullable(theme);
        }
    }

    static class StubChain implements FilterChain {
        boolean called = false;

        @Override
        public void doFilter(final ServletRequest req, final ServletResponse res) {
            called = true;
        }
    }

    static class StubResponse implements HttpServletResponse {
        // Implement the bare minimum; methods we don't call throw UnsupportedOperationException.
        String redirectLocation;
        int errorStatus;

        @Override
        public String getCharacterEncoding() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getContentType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.ServletOutputStream getOutputStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.io.PrintWriter getWriter() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCharacterEncoding(final String s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setContentLength(final int len) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setContentLengthLong(final long len) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setContentType(final String type) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setBufferSize(final int size) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getBufferSize() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void flushBuffer() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void resetBuffer() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isCommitted() {
            return false;
        }

        @Override
        public void reset() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setLocale(final java.util.Locale loc) {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.util.Locale getLocale() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addCookie(final jakarta.servlet.http.Cookie cookie) {
            throw new UnsupportedOperationException();
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
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendError(final int sc) {
            this.errorStatus = sc;
        }

        @Override
        public void sendRedirect(final String location) {
            this.redirectLocation = location;
        }

        @Override
        public void sendRedirect(final String location, final int sc, final boolean clearBuffer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendRedirect(final String location, final int sc) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendRedirect(final String location, final boolean clearBuffer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setDateHeader(final String name, final long date) {
            /* no-op */
        }

        @Override
        public void addDateHeader(final String name, final long date) {
            /* no-op */
        }

        @Override
        public void setHeader(final String name, final String value) {
            /* no-op */
        }

        @Override
        public void addHeader(final String name, final String value) {
            /* no-op */
        }

        @Override
        public void setIntHeader(final String name, final int value) {
            /* no-op */
        }

        @Override
        public void addIntHeader(final String name, final int value) {
            /* no-op */
        }

        @Override
        public void setStatus(final int sc) {
            /* no-op */
        }

        @Override
        public int getStatus() {
            return 200;
        }

        @Override
        public String getHeader(final String name) {
            return null;
        }

        @Override
        public java.util.Collection<String> getHeaders(final String name) {
            return Collections.emptyList();
        }

        @Override
        public java.util.Collection<String> getHeaderNames() {
            return Collections.emptyList();
        }
    }

    static class StubRequest implements HttpServletRequest {
        private final String method;
        private final String uri;
        private final Map<String, Object> attrs = new HashMap<>();
        String forwardedTo;
        private String contextPath = "";
        private String queryString;
        private final Map<String, String[]> params = new HashMap<>();
        private String servletPath;

        StubRequest(final String method, final String uri) {
            this.method = method;
            this.uri = uri;
        }

        /** Sets the container-decoded servlet path when it differs from the raw request URI. */
        StubRequest withServletPath(final String path) {
            this.servletPath = path;
            return this;
        }

        StubRequest withContextPath(final String ctx) {
            this.contextPath = ctx;
            return this;
        }

        StubRequest withQuery(final String qs) {
            this.queryString = qs;
            for (final String pair : qs.split("&")) {
                if (pair.isEmpty()) {
                    continue;
                }
                final int eq = pair.indexOf('=');
                final String name = java.net.URLDecoder.decode(eq < 0 ? pair : pair.substring(0, eq), StandardCharsets.UTF_8);
                final String value = eq < 0 ? "" : java.net.URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);
                final String[] old = params.get(name);
                final String[] values = old == null ? new String[1] : java.util.Arrays.copyOf(old, old.length + 1);
                values[values.length - 1] = value;
                params.put(name, values);
            }
            return this;
        }

        @Override
        public String getMethod() {
            return method;
        }

        @Override
        public String getRequestURI() {
            return uri;
        }

        @Override
        public String getContextPath() {
            return contextPath;
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
            return new RequestDispatcher() {
                @Override
                public void forward(final ServletRequest request, final ServletResponse response) {
                    forwardedTo = path;
                }

                @Override
                public void include(final ServletRequest request, final ServletResponse response) {
                    /* no-op */
                }
            };
        }

        // Below: every other HttpServletRequest method throws UnsupportedOperationException --
        // narrow the surface to what the filter actually calls. If a new test fails because
        // the filter calls something here, implement it then.
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
            return queryString;
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
        public String getServletPath() {
            if (servletPath != null) {
                return servletPath;
            }
            // Like a container: the request path without the context path.
            return !contextPath.isEmpty() && uri.startsWith(contextPath) ? uri.substring(contextPath.length()) : uri;
        }

        @Override
        public HttpSession getSession(final boolean create) {
            return null;
        }

        @Override
        public HttpSession getSession() {
            return null;
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
            final String[] values = params.get(name);
            return values == null || values.length == 0 ? null : values[0];
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

        public String getRealPath(final String path) {
            return path;
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
     * A plain {@link jakarta.servlet.ServletRequest} that does NOT implement
     * {@link HttpServletRequest}, used to exercise the defensive cast guard in
     * {@link StaticThemeFilter#doFilter}.
     */
    static class NonHttpStubRequest implements jakarta.servlet.ServletRequest {
        @Override
        public Object getAttribute(final String name) {
            return null;
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return Collections.emptyEnumeration();
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
        public jakarta.servlet.ServletInputStream getInputStream() {
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
        public java.util.Map<String, String[]> getParameterMap() {
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
        public void setAttribute(final String name, final Object o) {
        }

        @Override
        public void removeAttribute(final String name) {
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
        public RequestDispatcher getRequestDispatcher(final String path) {
            return null;
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
            return null;
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
     * A plain {@link jakarta.servlet.ServletResponse} that does NOT implement
     * {@link HttpServletResponse}, used alongside {@link NonHttpStubRequest}.
     */
    static class NonHttpStubResponse implements jakarta.servlet.ServletResponse {
        @Override
        public String getCharacterEncoding() {
            return "UTF-8";
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public jakarta.servlet.ServletOutputStream getOutputStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.io.PrintWriter getWriter() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCharacterEncoding(final String charset) {
        }

        @Override
        public void setContentLength(final int len) {
        }

        @Override
        public void setContentLengthLong(final long len) {
        }

        @Override
        public void setContentType(final String type) {
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
    }
}
