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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.helper.VirtualHostHelper;
import org.codelibs.fess.theme.StaticThemeResponder;
import org.codelibs.fess.theme.Theme;
import org.codelibs.fess.theme.ThemeManifest;
import org.codelibs.fess.theme.ThemeRegistry;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet filter that serves a static theme's files directly when a static theme is
 * active for the current virtual host.
 *
 * <p>The filter writes the response in place via {@link StaticThemeResponder} (no
 * {@code RequestDispatcher} forward), so the browser address bar keeps the original
 * request URI.
 *
 * <p>Matching is an <em>allowlist</em>: a request is handled by the theme only when it
 * targets a theme asset or one of the SPA-owned UI paths (see {@link #THEME_UI_PREFIXES}).
 * Every other request passes through to the standard Fess routes. This is the inverse of a
 * denylist of infrastructure prefixes: it keeps Fess's own endpoints (admin, API, login,
 * crawler/system endpoints, etc.) working by default, and a newly-added Fess route is never
 * accidentally swallowed by the SPA. The trade-off is that a UI route the SPA expects to
 * own must be added to {@link #THEME_UI_PREFIXES}; an unlisted path is not served as the
 * SPA entry.
 *
 * <p>Behavior summary:
 * <ul>
 *   <li>Non-GET requests pass through unchanged.</li>
 *   <li>Requests that are neither a {@code /themes/...} asset nor an allowlisted UI path
 *       pass through unchanged (without even resolving the active theme).</li>
 *   <li>If no active static theme is resolved, pass through.</li>
 *   <li>For {@code /themes/{name}/...} that matches the active static theme's name, the filter calls
 *       {@link StaticThemeResponder#serveAsset} with the path after {@code {name}/}.</li>
 *   <li>For an allowlisted UI path (when {@code spaFallback} is enabled), the filter calls
 *       {@link StaticThemeResponder#serveIndex} to serve the SPA entry HTML in place.</li>
 * </ul>
 */
public class StaticThemeFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(StaticThemeFilter.class);

    /**
     * SPA-owned UI path prefixes served as the theme entry ({@code index.html}) when a static
     * theme is active and {@code spaFallback} is enabled. The root path {@code "/"} is matched
     * exactly (handled in {@link #isThemeUiPath(String)}); each prefix below matches the bare
     * path and any sub-path (e.g. {@code /cache} and {@code /cache/}).
     *
     * <p>This set mirrors the public search UI routes that the SPA replaces:
     * <ul>
     *   <li>{@code /} — search top (root).</li>
     *   <li>{@code /search} — search results.</li>
     *   <li>{@code /help} — the search help page.</li>
     *   <li>{@code /error} — error pages; {@link StaticThemeResponder} detects the
     *       {@code /error} prefix and sets the appropriate HTTP status and diagnostic
     *       response headers before serving the SPA entry.</li>
     *   <li>{@code /profile} — the user profile page.</li>
     *   <li>{@code /cache} — the cached-document viewer ({@code /cache/?docId=...}); the
     *       underlying {@code /api/v2/cache/{docId}} data endpoint is served by Fess
     *       because {@code /api/} is not an allowlisted UI path.</li>
     *   <li>{@code /chat} — the chat UI page.</li>
     * </ul>
     *
     * <p>Add a prefix here when the SPA gains a new top-level UI route that must be served
     * as the entry HTML rather than handled by Fess.
     */
    private static final List<String> THEME_UI_PREFIXES = List.of(//
            "/search", //
            "/help", //
            "/error", //
            "/profile", //
            "/cache", //
            "/chat");

    /** Overridable registry reference; production code uses the container lookup. */
    private ThemeRegistry themeRegistry;

    /** Overridable responder reference; production code uses the container lookup. */
    private StaticThemeResponder staticThemeResponder;

    /**
     * Latches on the first failed {@link ThemeRegistry} lookup so the operator
     * sees a single WARN instead of one per request when the DI container is not
     * yet ready (or has been torn down). Subsequent failures degrade to DEBUG.
     * Instance field (not static) so redeploys re-emit the WARN.
     */
    private final AtomicBoolean firstFailure = new AtomicBoolean(false);

    /**
     * Latches on the first {@link VirtualHostHelper} lookup failure so the
     * operator sees a single WARN instead of one per request when the helper
     * cannot be resolved. Subsequent failures degrade to DEBUG. Mirrors
     * {@link #firstFailure} but tracks the virtual-host helper independently.
     */
    private final AtomicBoolean hostKeyFirstFailure = new AtomicBoolean(false);

    /**
     * Latches on the first {@link StaticThemeResponder} lookup failure so the operator sees a
     * single WARN instead of one per request. Subsequent failures degrade to DEBUG.
     * Mirrors {@link #firstFailure} but tracks the responder component independently.
     */
    private final AtomicBoolean responderFirstFailure = new AtomicBoolean(false);

    /**
     * Default constructor.
     */
    public StaticThemeFilter() {
        // Default constructor
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        // Helpers are resolved lazily via ComponentUtil per request.
    }

    @Override
    public void destroy() {
        // no-op
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        // Guard against non-HTTP invocations (e.g. cross-context dispatches in Tomcat).
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }
        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse res = (HttpServletResponse) response;

        // Only intercept GET requests
        if (!"GET".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        final String uri = stripContextPath(req);

        // Allowlist: handle only theme assets and SPA-owned UI paths. Everything else is a
        // Fess route and passes through without even resolving the active theme.
        final boolean assetCandidate = uri.startsWith("/themes/");
        final boolean uiCandidate = isThemeUiPath(uri);
        if (!assetCandidate && !uiCandidate) {
            chain.doFilter(request, response);
            return;
        }

        final ThemeRegistry reg = resolveRegistry();
        if (reg == null) {
            chain.doFilter(request, response);
            return;
        }
        final String hostKey = resolveHostKey();
        final Theme theme = reg.resolveActiveTheme(hostKey).orElse(null);
        if (theme == null) {
            chain.doFilter(request, response);
            return;
        }

        final StaticThemeResponder responder = resolveResponder();
        if (responder == null) {
            chain.doFilter(request, response);
            return;
        }

        // Asset path: /themes/{name}/...
        if (assetCandidate) {
            final String rest = uri.substring("/themes/".length()); // {name}/path...
            final int slash = rest.indexOf('/');
            if (slash <= 0) {
                chain.doFilter(request, response);
                return;
            }
            final String requestedThemeName = rest.substring(0, slash);
            final String assetPath = rest.substring(slash + 1);
            if (!theme.getName().equals(requestedThemeName) || assetPath.isEmpty()) {
                // The asset path references a different theme (or none) — pass through.
                chain.doFilter(request, response);
                return;
            }
            responder.serveAsset(req, res, theme, assetPath);
            return;
        }

        // If the theme manifest has spaFallback=false, do not serve the theme entry for
        // UI requests — let the original Fess routes handle them.
        final boolean spaFallback = theme.getManifest().map(ThemeManifest::isSpaFallback).orElse(true);
        if (!spaFallback) {
            chain.doFilter(request, response);
            return;
        }

        // Allowlisted UI path -> serve index.html directly in place.
        responder.serveIndex(req, res, theme, uri);
    }

    private static String stripContextPath(final HttpServletRequest req) {
        final String ctx = req.getContextPath() == null ? "" : req.getContextPath();
        String uri = req.getRequestURI();
        if (uri == null) {
            return "";
        }
        if (!ctx.isEmpty() && uri.startsWith(ctx)) {
            uri = uri.substring(ctx.length());
        }
        return uri;
    }

    private static boolean isThemeUiPath(final String uri) {
        if ("/".equals(uri)) {
            return true;
        }
        for (final String p : THEME_UI_PREFIXES) {
            // Note: getRequestURI() never includes the query string per the Servlet spec,
            // so matching the bare path and the "{p}/" sub-path covers all cases.
            if (uri.equals(p) || uri.startsWith(p + "/")) {
                return true;
            }
        }
        return false;
    }

    private ThemeRegistry resolveRegistry() {
        if (themeRegistry != null) {
            return themeRegistry;
        }
        try {
            return ComponentUtil.getThemeRegistry();
        } catch (final Exception e) {
            if (firstFailure.compareAndSet(false, true)) {
                logger.warn("ThemeRegistry not available; static-theme routing disabled", e);
            } else if (logger.isDebugEnabled()) {
                logger.debug("ThemeRegistry not available", e);
            }
            return null;
        }
    }

    private StaticThemeResponder resolveResponder() {
        if (staticThemeResponder != null) {
            return staticThemeResponder;
        }
        try {
            return ComponentUtil.getStaticThemeResponder();
        } catch (final Exception e) {
            if (responderFirstFailure.compareAndSet(false, true)) {
                logger.warn("StaticThemeResponder not available; static-theme routing disabled", e);
            } else if (logger.isDebugEnabled()) {
                logger.debug("StaticThemeResponder not available", e);
            }
            return null;
        }
    }

    private String resolveHostKey() {
        try {
            final VirtualHostHelper h = ComponentUtil.getVirtualHostHelper();
            if (h != null) {
                return h.getVirtualHostKey();
            }
        } catch (final Exception e) {
            if (hostKeyFirstFailure.compareAndSet(false, true)) {
                logger.warn("VirtualHostHelper not available; using null host key for static-theme routing", e);
            } else if (logger.isDebugEnabled()) {
                logger.debug("VirtualHostHelper not available; using null host key", e);
            }
        }
        return null;
    }

    // ---- Test seam ----
    void setThemeRegistry(final ThemeRegistry r) {
        this.themeRegistry = r;
    }

    void setStaticThemeResponder(final StaticThemeResponder r) {
        this.staticThemeResponder = r;
    }
}
