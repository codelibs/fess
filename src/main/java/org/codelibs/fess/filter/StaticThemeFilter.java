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

import org.codelibs.fess.theme.ThemeManifest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.web.theme.ThemeViewAction;
import org.codelibs.fess.helper.VirtualHostHelper;
import org.codelibs.fess.theme.Theme;
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
 * Servlet filter that routes GET requests to {@link ThemeViewAction} when a static theme is
 * active for the current virtual host.
 *
 * <p>Behavior summary:
 * <ul>
 *   <li>Non-GET requests pass through unchanged.</li>
 *   <li>Pre-declared infrastructure prefixes (admin, API, legacy JSP-theme assets, etc.) pass through.</li>
 *   <li>If no active theme is resolved, or the active theme is JSP, pass through.</li>
 *   <li>For {@code /themes/{name}/...} that matches the active static theme's name, the filter sets
 *       {@link ThemeViewAction#REQ_ATTR_MODE} to {@code "ASSET"} and
 *       {@link ThemeViewAction#REQ_ATTR_ASSET_PATH} to the path after {@code {name}/}, then forwards
 *       to {@value #THEME_VIEW_PATH}.</li>
 *   <li>For any other UI path, the filter sets {@link ThemeViewAction#REQ_ATTR_MODE} to
 *       {@code "INDEX"} and forwards to {@value #THEME_VIEW_PATH}.</li>
 * </ul>
 */
public class StaticThemeFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(StaticThemeFilter.class);

    /** Forwarded URL. Must NOT itself be intercepted to avoid infinite recursion. */
    static final String THEME_VIEW_PATH = "/theme-view";

    /** URI prefixes that bypass the filter even when a static theme is active. */
    private static final List<String> PASS_THROUGH_PREFIXES = List.of(//
            "/admin/", "/admin", //
            "/api/", //
            "/css/", "/js/", "/images/", //
            "/go", "/thumbnail", "/osdd", //
            "/sso/", "/logout", "/cache", //
            "/favicon.ico", //
            "/robots.txt", //
            "/sitemap.xml", //
            "/manifest.webmanifest", //
            "/.well-known", //
            THEME_VIEW_PATH);

    /** Overridable registry reference; production code uses the container lookup. */
    private ThemeRegistry themeRegistry;

    /**
     * Latches on the first failed {@link ThemeRegistry} lookup so the operator
     * sees a single WARN instead of one per request when the DI container is not
     * yet ready (or has been torn down). Subsequent failures degrade to DEBUG.
     * Instance field (not static) so redeploys re-emit the WARN.
     */
    private final AtomicBoolean firstFailure = new AtomicBoolean(false);

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

        // Always pass through any pre-declared infrastructure paths
        if (isPassThrough(uri)) {
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
        if (theme == null || !theme.isStatic()) {
            chain.doFilter(request, response);
            return;
        }

        // Asset path: /themes/{name}/...
        if (uri.startsWith("/themes/")) {
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
            req.setAttribute(ThemeViewAction.REQ_ATTR_MODE, "ASSET");
            req.setAttribute(ThemeViewAction.REQ_ATTR_ASSET_PATH, assetPath);
            req.getRequestDispatcher(THEME_VIEW_PATH).forward(req, res);
            return;
        }

        // If the theme manifest has spaFallback=false, do not forward non-asset
        // requests to the theme view — let the original Fess routes handle them.
        final boolean spaFallback = theme.getManifest().map(ThemeManifest::isSpaFallback).orElse(true);
        if (!spaFallback) {
            chain.doFilter(request, response);
            return;
        }

        // UI path -> index.html (SPA fallback mode)
        req.setAttribute(ThemeViewAction.REQ_ATTR_MODE, "INDEX");
        req.getRequestDispatcher(THEME_VIEW_PATH).forward(req, res);
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

    private static boolean isPassThrough(final String uri) {
        for (final String p : PASS_THROUGH_PREFIXES) {
            if (p.endsWith("/")) {
                if (uri.startsWith(p)) {
                    return true;
                }
            } else if (uri.equals(p) || uri.startsWith(p + "/")) {
                // Note: getRequestURI() never includes the query string per the
                // Servlet spec, so the dead uri.startsWith(p + "?") branch is omitted.
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
            return ComponentUtil.getComponent(ThemeRegistry.class);
        } catch (final Exception e) {
            if (firstFailure.compareAndSet(false, true)) {
                logger.warn("ThemeRegistry not available; static-theme routing disabled", e);
            } else if (logger.isDebugEnabled()) {
                logger.debug("ThemeRegistry not available", e);
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
            if (logger.isDebugEnabled()) {
                logger.debug("VirtualHostHelper not available; using null host key", e);
            }
        }
        return null;
    }

    // ---- Test seam ----
    void setThemeRegistry(final ThemeRegistry r) {
        this.themeRegistry = r;
    }
}
