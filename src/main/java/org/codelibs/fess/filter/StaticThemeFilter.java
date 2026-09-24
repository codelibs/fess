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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.helper.VirtualHostHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.theme.StaticThemeInstaller;
import org.codelibs.fess.theme.StaticThemeResponder;
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
 *   <li>A request for a JSP (or similar server-side page) under {@code /themes/} is answered
 *       with 404, whatever the method or active theme: a static theme contains plain files only.</li>
 *   <li>Requests other than GET and HEAD pass through unchanged.</li>
 *   <li>Requests that are neither a {@code /themes/...} asset nor an allowlisted UI path
 *       pass through unchanged (without even resolving the active theme).</li>
 *   <li>If no active static theme is resolved, pass through.</li>
 *   <li>For {@code /themes/{name}/...} that matches the active static theme's name, the filter calls
 *       {@link StaticThemeResponder#serveAsset} with the path after {@code {name}/}.</li>
 *   <li>For an allowlisted UI path, the filter calls {@link StaticThemeResponder#serveIndex}
 *       to serve the SPA entry HTML in place.</li>
 * </ul>
 */
public class StaticThemeFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(StaticThemeFilter.class);

    /**
     * SPA-owned UI path prefixes served as the theme entry ({@code index.html}) when a static
     * theme is active. (The {@code theme.yml} {@code spaFallback} flag that used to gate this is
     * deprecated and no longer consulted -- see the removed check this class used to make.) The
     * root path {@code "/"} is matched exactly (handled in {@link #isThemeUiPath(String)}); each
     * prefix below matches the bare path and any sub-path (e.g. {@code /cache} and {@code
     * /cache/}).
     *
     * <p>This set mirrors the public search UI routes that the SPA replaces:
     * <ul>
     *   <li>{@code /} — search top (root).</li>
     *   <li>{@code /search} — search results.</li>
     *   <li>{@code /advance} — the advanced search page; {@code /search/advance} is already
     *       covered by the {@code /search} prefix.</li>
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
            "/advance", //
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
        if (!(request instanceof final HttpServletRequest req) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }
        final HttpServletResponse res = (HttpServletResponse) response;

        // A static theme is plain files. A JSP (or similar) page under /themes/ is never
        // part of one, so it is not handed to the servlet container, whatever the method or
        // active theme. Checked on the container-decoded path, which is what it maps to a servlet.
        if (isServerSideThemePage(req)) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // GET and HEAD are the SPA's read paths. A HEAD response is built exactly like the GET
        // one — the container drops the body and keeps the headers, which is what a HEAD client
        // asks for. Everything else is a Fess route.
        final String method = req.getMethod();
        if (!"GET".equalsIgnoreCase(method) && !"HEAD".equalsIgnoreCase(method)) {
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

        // theme.yml's spaFallback flag is deprecated and no longer consulted: now that there is
        // no JSP UI behind the allowlisted UI paths, a theme cannot opt out of serving them.

        // A JSP search URL the SPA does not route (paging, /search/search, /chat/clear) is sent to
        // the SPA URL showing the same page. Only here, where the SPA would serve the path: in JSP
        // mode the JSP actions still handle these URLs themselves.
        final String legacyTarget = resolveLegacyRedirect(req, uri);
        if (legacyTarget != null) {
            res.sendRedirect((req.getContextPath() == null ? "" : req.getContextPath()) + legacyTarget);
            return;
        }

        // Allowlisted UI path -> serve index.html directly in place.
        responder.serveIndex(req, res, theme, uri);
    }

    /**
     * Maps a JSP search URL the SPA does not route to the SPA URL showing the same page.
     *
     * <ul>
     *   <li>{@code /search/prev}, {@code /search/next}, {@code /search/move}: {@code /search} with
     *       {@code start} computed from {@code pn} and {@code num} as {@code SearchAction.doMove}
     *       does. {@code pn} and any {@code start} are dropped; the other parameters are kept as
     *       sent.</li>
     *   <li>{@code /search/search}: {@code /search} with the query string as sent.</li>
     *   <li>{@code /chat/clear}: {@code /chat}; the SPA keeps its own chat sessions.</li>
     * </ul>
     *
     * @param req the request
     * @param uri the request path without the context path
     * @return the target path and query without the context path, or null when the path is not one of these
     */
    String resolveLegacyRedirect(final HttpServletRequest req, final String uri) {
        final String path = uri.length() > 1 && uri.endsWith("/") ? uri.substring(0, uri.length() - 1) : uri;
        final int move;
        switch (path) {
        case "/search/search":
            return StringUtil.isEmpty(req.getQueryString()) ? "/search" : "/search?" + req.getQueryString();
        case "/chat/clear":
            return "/chat";
        case "/search/prev":
            move = -1;
            break;
        case "/search/next":
            move = 1;
            break;
        case "/search/move":
            move = 0;
            break;
        default:
            return null;
        }
        final int start = computeLegacyStart(req.getParameter("pn"), req.getParameter("num"), move);
        final String kept = removeParameters(req.getQueryString(), "pn", "start");
        return "/search?" + (kept.isEmpty() ? "" : kept + "&") + "start=" + start;
    }

    /**
     * Computes the result offset of a JSP paging link as {@code SearchAction.doMove} does.
     *
     * @param pn the page number the link was rendered on; ignored unless a positive integer
     * @param num the page size; absent or unparsable means {@code paging.search.page.size}, zero or
     *        below or above {@code paging.search.page.max.size} means that maximum
     * @param move -1 for the previous page, 1 for the next page, 0 for the page itself
     * @return the offset
     */
    static int computeLegacyStart(final String pn, final String num, final int move) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Integer pageNumber = parseInteger(pn);
        if (pageNumber == null || pageNumber <= 0) {
            return fessConfig.getPagingSearchPageStartAsInteger();
        }
        final int maxSize = fessConfig.getPagingSearchPageMaxSizeAsInteger();
        final Integer requestedSize = parseInteger(num);
        final int pageSize;
        if (requestedSize == null) {
            pageSize = fessConfig.getPagingSearchPageSizeAsInteger();
        } else if (requestedSize <= 0 || requestedSize > maxSize) {
            pageSize = maxSize;
        } else {
            pageSize = requestedSize;
        }
        return (Math.max(1, pageNumber + move) - 1) * pageSize;
    }

    /**
     * Removes the named parameters from a raw query string, keeping every other pair, and its order
     * and encoding, exactly as sent.
     *
     * @param queryString the raw query string, may be null
     * @param names the parameter names to remove
     * @return the remaining query string; empty when nothing remains
     */
    static String removeParameters(final String queryString, final String... names) {
        if (StringUtil.isEmpty(queryString)) {
            return "";
        }
        final Set<String> removed = Set.of(names);
        return Arrays.stream(queryString.split("&")).filter(pair -> !pair.isEmpty()).filter(pair -> {
            final int eq = pair.indexOf('=');
            final String name = eq < 0 ? pair : pair.substring(0, eq);
            try {
                return !removed.contains(URLDecoder.decode(name, StandardCharsets.UTF_8));
            } catch (final IllegalArgumentException e) {
                return !removed.contains(name);
            }
        }).collect(Collectors.joining("&"));
    }

    private static Integer parseInteger(final String value) {
        if (StringUtil.isBlank(value)) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (final NumberFormatException e) {
            return null;
        }
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

    /**
     * Returns whether the request targets a server-side page (JSP and the like) under
     * {@code /themes/}. Uses the servlet path and path info, which the container has already
     * decoded and normalized, rather than the raw request URI.
     *
     * @param req the request
     * @return true when the request should not reach a servlet
     */
    static boolean isServerSideThemePage(final HttpServletRequest req) {
        final String servletPath = req.getServletPath() == null ? "" : req.getServletPath();
        final String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();
        final String path = servletPath + pathInfo;
        return path.toLowerCase(Locale.ROOT).startsWith("/themes/") && StaticThemeInstaller.isServerSidePage(path);
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

    /**
     * Logs a component lookup failure at WARN on every occurrence: the message and the cause's
     * message on one line, with the stack trace only when DEBUG is enabled.
     *
     * @param msg the message
     * @param e the cause
     */
    private void warnLookupFailure(final String msg, final Exception e) {
        if (logger.isDebugEnabled()) {
            logger.warn(msg, e);
        } else {
            logger.warn("{}: {}", msg, e.getMessage());
        }
    }

    private ThemeRegistry resolveRegistry() {
        if (themeRegistry != null) {
            return themeRegistry;
        }
        try {
            return ComponentUtil.getThemeRegistry();
        } catch (final Exception e) {
            warnLookupFailure("ThemeRegistry not available; static-theme routing disabled", e);
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
            warnLookupFailure("StaticThemeResponder not available; static-theme routing disabled", e);
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
            warnLookupFailure("VirtualHostHelper not available; using null host key for static-theme routing", e);
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
