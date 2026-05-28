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
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.codelibs.fess.app.web.theme.ThemeViewAction;
import org.codelibs.fess.theme.Theme;
import org.codelibs.fess.theme.ThemeManifest;
import org.codelibs.fess.theme.ThemeRegistry;
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
        final StubRequest req = new StubRequest("GET", "/search");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertTrue(chain.called);
    }

    @Test
    public void test_passesThroughForAdminPath() throws Exception {
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubRequest req = new StubRequest("GET", "/admin/dashboard");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertTrue(chain.called);
    }

    @Test
    public void test_passesThroughForApi() throws Exception {
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubRequest req = new StubRequest("GET", "/api/v1/health");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertTrue(chain.called);
    }

    @Test
    public void test_passesThroughPostRequests() throws Exception {
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubRequest req = new StubRequest("POST", "/login");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertTrue(chain.called);
    }

    @Test
    public void test_forwardsIndexForUiPath() throws Exception {
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubRequest req = new StubRequest("GET", "/search");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertEquals("/theme/view/", req.forwardedTo);
        assertEquals("INDEX", req.getAttribute(ThemeViewAction.REQ_ATTR_MODE));
        // chain should NOT have been called
        assertEquals(false, chain.called);
    }

    @Test
    public void test_forwardsAssetForMatchingThemeAssetPath() throws Exception {
        final Theme staticTheme = new Theme("alpha", Paths.get("/tmp/alpha"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubRequest req = new StubRequest("GET", "/themes/alpha/assets/app.js");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertEquals("/theme/view/", req.forwardedTo);
        assertEquals("ASSET", req.getAttribute(ThemeViewAction.REQ_ATTR_MODE));
        assertEquals("assets/app.js", req.getAttribute(ThemeViewAction.REQ_ATTR_ASSET_PATH));
        assertEquals(false, chain.called);
    }

    @Test
    public void test_passesThroughForNonMatchingThemeAssetPath() throws Exception {
        // Active theme is "alpha"; request is for "beta" assets — pass through
        final Theme staticTheme = new Theme("alpha", Paths.get("/tmp/alpha"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubRequest req = new StubRequest("GET", "/themes/beta/assets/app.js");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertTrue(chain.called);
        assertNull(req.forwardedTo);
    }

    @Test
    public void test_passesThroughForJspThemeAssetPaths() throws Exception {
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        for (final String uri : new String[] { "/css/simple/style.css", "/js/simple/app.js", "/images/simple/logo.png" }) {
            final StubRequest req = new StubRequest("GET", uri);
            final StubChain chain = new StubChain();
            f.doFilter(req, new StubResponse(), chain);
            assertTrue(chain.called, "Expected pass-through for " + uri);
        }
    }

    @Test
    public void test_passesThroughForJspAccountAndErrorPaths() throws Exception {
        // /login still relies on Fess JSP forms and must always pass through.
        // /error and /profile are now forwarded to the SPA when a static theme is
        // active (see test_forwardsIndexForErrorAndProfilePaths below).
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        for (final String uri : new String[] { //
                "/login", "/login/", "/login/login" }) {
            final StubRequest req = new StubRequest("GET", uri);
            final StubChain chain = new StubChain();
            f.doFilter(req, new StubResponse(), chain);
            assertTrue(chain.called, "Expected pass-through for " + uri);
            assertNull(req.forwardedTo, "Must not forward to theme view: " + uri);
        }
    }

    @Test
    public void test_forwardsIndexForErrorAndProfilePaths() throws Exception {
        // /error/* and /profile are SPA routes when a static theme is active.
        // The SPA reads the pathname and renders the appropriate error or profile page.
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        for (final String uri : new String[] { //
                "/error/notFound", "/profile" }) {
            final StubRequest req = new StubRequest("GET", uri);
            final StubChain chain = new StubChain();
            f.doFilter(req, new StubResponse(), chain);
            org.junit.jupiter.api.Assertions.assertEquals("/theme/view/", req.forwardedTo, "Expected forward to theme view for " + uri);
            org.junit.jupiter.api.Assertions.assertEquals("INDEX", req.getAttribute(ThemeViewAction.REQ_ATTR_MODE),
                    "Expected INDEX mode for " + uri);
            assertFalse(chain.called, "Chain must not be called for SPA route: " + uri);
        }
    }

    @Test
    public void test_forwardsIndexForErrorPath_withQueryString() throws Exception {
        // Query strings (e.g. /error/notFound?url=...) must not affect routing:
        // getRequestURI() strips the query string per the Servlet spec, so a request
        // to /error/notFound?url=... still routes to the SPA index.
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubRequest req = new StubRequest("GET", "/error/notFound");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertEquals("/theme/view/", req.forwardedTo);
        assertEquals("INDEX", req.getAttribute(ThemeViewAction.REQ_ATTR_MODE));
        assertFalse(chain.called);
    }

    @Test
    public void test_passesThroughForThemeViewPath_toPreventRecursion() throws Exception {
        // /theme/view/ is the forward target itself; it must always pass through
        // to prevent infinite recursion when StaticThemeFilter intercepts the forward.
        final Theme staticTheme = new Theme("t", Paths.get("/tmp/t"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);
        final StubRequest req = new StubRequest("GET", "/theme/view/");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);
        assertTrue(chain.called, "Expected pass-through for /theme/view/ to prevent recursion");
        assertNull(req.forwardedTo, "Must not forward to theme view for /theme/view/");
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

        // A plain ServletRequest/ServletResponse that is NOT an HttpServletRequest.
        final StubChain chain = new StubChain();
        f.doFilter(new NonHttpStubRequest(), new NonHttpStubResponse(), chain);
        assertTrue(chain.called, "Non-HTTP request must pass through without ClassCastException");
    }

    // ── E-2: warn-once / firstFailure AtomicBoolean gate ─────────────────────

    @Test
    public void test_warnOnce_logsOnlyOnceWhenRegistryUnavailable() throws Exception {
        // When themeRegistry is NOT injected via setThemeRegistry() the filter falls
        // through to ComponentUtil.getThemeRegistry(), which throws in the slim test
        // harness. The firstFailure AtomicBoolean ensures exactly one WARN is emitted
        // no matter how many requests arrive.
        //
        // Capture log4j2 output via a ListAppender attached to StaticThemeFilter's logger.
        final String loggerName = StaticThemeFilter.class.getName();
        final LoggerContext ctx = (LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
        final org.apache.logging.log4j.core.config.Configuration cfg = ctx.getConfiguration();
        final org.apache.logging.log4j.core.config.LoggerConfig loggerCfg = cfg.getLoggerConfig(loggerName);
        // Temporarily set to WARN so the appender captures it.
        final Level originalLevel = loggerCfg.getLevel();
        final java.util.List<LogEvent> captured = new java.util.ArrayList<>();
        final AbstractAppender listAppender =
                new AbstractAppender("test-list-appender", null, PatternLayout.createDefaultLayout(), true, Property.EMPTY_ARRAY) {
                    @Override
                    public void append(final LogEvent event) {
                        if (event.getLevel().isMoreSpecificThan(Level.WARN)) {
                            captured.add(event.toImmutable());
                        }
                    }
                };
        listAppender.start();
        loggerCfg.addAppender(listAppender, Level.WARN, null);
        loggerCfg.setLevel(Level.WARN);
        ctx.updateLoggers();
        try {
            // Create a fresh filter with NO registry injected — ComponentUtil will throw.
            final StaticThemeFilter f = new StaticThemeFilter();
            // Send 5 GET requests. Each should pass through (registry unavailable).
            for (int i = 0; i < 5; i++) {
                final StubChain chain = new StubChain();
                f.doFilter(new StubRequest("GET", "/search"), new StubResponse(), chain);
                assertTrue(chain.called, "filter must pass through when registry is unavailable");
            }
            // Count only WARN events from StaticThemeFilter's logger about ThemeRegistry.
            final long warnCount = captured.stream()
                    .filter(e -> loggerName.equals(e.getLoggerName()))
                    .filter(e -> Level.WARN.equals(e.getLevel()))
                    .filter(e -> {
                        final String msg = e.getMessage().getFormattedMessage();
                        return msg != null && msg.contains("ThemeRegistry");
                    })
                    .count();
            org.junit.jupiter.api.Assertions.assertEquals(1L, warnCount,
                    "exactly 1 WARN must be emitted for ThemeRegistry unavailable across 5 requests; got " + warnCount);
        } finally {
            loggerCfg.removeAppender("test-list-appender");
            loggerCfg.setLevel(originalLevel);
            ctx.updateLoggers();
            listAppender.stop();
        }
    }

    // ── E-3: spaFallback=false — non-asset requests pass through ─────────────

    /**
     * Builds a minimal valid {@link ThemeManifest} YAML with {@code spaFallback: false}.
     */
    private static ThemeManifest buildManifest(final boolean spaFallback) throws Exception {
        final String yaml = String.join("\n", "apiVersion: fess.codelibs.org/v1", "kind: StaticTheme", "name: alpha",
                "displayName: Alpha Theme", "version: 1.0.0", "spaFallback: " + spaFallback);
        return ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void test_spaFallbackFalse_passesNonAssetRequestThrough() throws Exception {
        // When manifest.spaFallback=false, non-asset UI requests must pass through
        // to the original Fess routes rather than being forwarded to the SPA index.
        // A regression here would silently break themes that opt out of SPA mode.
        final ThemeManifest manifest = buildManifest(false);
        final Theme staticTheme = new Theme("alpha", Paths.get("/tmp/alpha"), manifest);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);

        // Non-asset request (no file extension match / not a /themes/{name}/... path)
        final StubRequest req = new StubRequest("GET", "/some/route");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);

        // Must pass through — not forwarded to /theme/view/.
        assertTrue(chain.called, "spaFallback=false must pass non-asset requests through to original routes");
        assertNull(req.forwardedTo, "spaFallback=false must not forward to /theme/view/ for non-asset requests");
    }

    @Test
    public void test_spaFallbackTrue_forwardsNonAssetToIndex() throws Exception {
        // Symmetric test: when manifest.spaFallback=true (explicit), non-asset UI
        // requests must be forwarded to the SPA index.
        final ThemeManifest manifest = buildManifest(true);
        final Theme staticTheme = new Theme("alpha", Paths.get("/tmp/alpha"), manifest);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);

        final StubRequest req = new StubRequest("GET", "/some/route");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);

        // Must forward to /theme/view/ with INDEX mode.
        assertFalse(chain.called, "spaFallback=true must not pass through non-asset requests");
        org.junit.jupiter.api.Assertions.assertEquals("/theme/view/", req.forwardedTo, "spaFallback=true must forward to /theme/view/");
        assertEquals("INDEX", req.getAttribute(ThemeViewAction.REQ_ATTR_MODE));
    }

    @Test
    public void test_spaFallbackFalse_assetPathStillForwardsToThemeView() throws Exception {
        // Even when spaFallback=false, asset paths (/themes/{name}/...) must still
        // be forwarded to /theme/view/ with ASSET mode — the spaFallback flag only
        // governs non-asset SPA routing, not static asset delivery.
        final ThemeManifest manifest = buildManifest(false);
        final Theme staticTheme = new Theme("alpha", Paths.get("/tmp/alpha"), manifest);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);

        final StubRequest req = new StubRequest("GET", "/themes/alpha/assets/app.js");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);

        // Asset path must be forwarded regardless of spaFallback setting.
        org.junit.jupiter.api.Assertions.assertEquals("/theme/view/", req.forwardedTo,
                "asset paths must forward to /theme/view/ even when spaFallback=false");
        assertEquals("ASSET", req.getAttribute(ThemeViewAction.REQ_ATTR_MODE));
        assertFalse(chain.called, "asset path must not pass through when active theme matches");
    }

    @Test
    public void test_spaFallbackDefault_isTrue_whenManifestIsNull() throws Exception {
        // Theme with null manifest (stub with no manifest) falls back to the
        // default spaFallback=true behavior via orElse(true) in the filter.
        final Theme staticTheme = new Theme("alpha", Paths.get("/tmp/alpha"), null);
        final StubRegistry reg = new StubRegistry(staticTheme);
        final StaticThemeFilter f = new StaticThemeFilter();
        f.setThemeRegistry(reg);

        final StubRequest req = new StubRequest("GET", "/search");
        final StubChain chain = new StubChain();
        f.doFilter(req, new StubResponse(), chain);

        // Null manifest → orElse(true) → spaFallback=true → forward to index.
        org.junit.jupiter.api.Assertions.assertEquals("/theme/view/", req.forwardedTo,
                "null manifest must default spaFallback=true and forward to /theme/view/");
        assertEquals("INDEX", req.getAttribute(ThemeViewAction.REQ_ATTR_MODE));
        assertFalse(chain.called);
    }

    // ===== Stubs =====

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
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendRedirect(final String location) {
            throw new UnsupportedOperationException();
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

        StubRequest(final String method, final String uri) {
            this.method = method;
            this.uri = uri;
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

        // Below: every other HttpServletRequest method throws UnsupportedOperationException —
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
        public String getServletPath() {
            return uri;
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
