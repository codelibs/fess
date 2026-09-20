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
package org.codelibs.fess.servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Tuple3;
import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.theme.StaticThemeResponder;
import org.codelibs.fess.theme.Theme;
import org.codelibs.fess.theme.ThemeRegistry;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.WebApiUtil;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Renders the container's error pages.
 *
 * <p>Registered in {@code web.xml} as the location of every {@code <error-page>}, so it runs on the
 * container's ERROR dispatch: none of LastaFlute's filters are on the stack (they are mapped
 * REQUEST/FORWARD/INCLUDE only), which is what makes it able to report a failure that happened
 * inside LastaFlute itself. It is also the show-errors forward target for search-side actions, and
 * then sees a FORWARD dispatch with no error attributes.</p>
 *
 * <p>The response depends on the client. An API request (by URI) or a client that does not accept
 * HTML gets the status and a one-line {@code text/plain} body. A browser gets the active theme's
 * {@code index.html} at the failing URL with the real status, so the address bar keeps the URL the
 * user asked for. If no theme can answer — the bundled theme was removed, the DI container is not
 * up, the entry file is unreadable — a minimal, script-free HTML page is written here.</p>
 *
 * <p>This class never calls {@code sendError}: it is already inside the error dispatch, and
 * re-entering it would loop.</p>
 */
public class ErrorPageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(ErrorPageServlet.class);

    /** Path this servlet is mapped to in {@code web.xml}; also the show-errors forward path. */
    public static final String SERVLET_PATH = "/error-page";

    /** Overridable registry reference; production code falls back to the container lookup. */
    private ThemeRegistry themeRegistry;

    /** Overridable responder reference; production code falls back to the container lookup. */
    private StaticThemeResponder staticThemeResponder;

    /**
     * Default constructor.
     */
    public ErrorPageServlet() {
        // Default constructor
    }

    @Override
    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final DispatcherType dispatcherType = request.getDispatcherType();
        if (dispatcherType != DispatcherType.ERROR && dispatcherType != DispatcherType.FORWARD) {
            // Nothing failed: a client asked for the error page itself. It is not a page.
            writeText(response, HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        final int status = resolveStatus(request, response, dispatcherType);
        // A FORWARD dispatch (and some containers, even an ERROR one) may carry no
        // jakarta.servlet.error.request_uri attribute; fall back to the request's own URI. On a
        // FORWARD/ERROR dispatch that fallback URI is the dispatch target (this servlet's own
        // path), not the URL that failed, so it cannot by itself identify an API client -- the
        // Accept-header check inside isNonHtmlClient below is what actually catches one.
        final Object errorUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        final String requestUri = errorUri instanceof final String uri ? uri : request.getRequestURI();
        if (isNonHtmlClient(request, requestUri)) {
            writeText(response, status);
            return;
        }
        final int displayCode = displayCode(status);
        try {
            final Theme theme = resolveTheme(request);
            if (theme != null) {
                // Tomcat's HttpHeaderSecurityFilter normally adds this header, but only on a
                // REQUEST dispatch; this page is written in place on an ERROR/FORWARD dispatch,
                // so that filter never runs here and the header must be set explicitly.
                response.setHeader("X-Content-Type-Options", "nosniff");
                if (resolveResponder().serveErrorPage(request, response, theme, status, displayCode, resolveDetailKey(request, status))) {
                    return;
                }
            }
        } catch (final Throwable t) {
            // Catches Throwable, not just Exception: a broken DI container or a theme class
            // that fails to load can surface as NoClassDefFoundError or
            // ExceptionInInitializerError rather than an Exception, and this is the last place
            // in the stack able to answer the visitor with something other than the
            // container's own default error page -- swallowing it here is what the class
            // javadoc promises.
            logger.warn("Failed to render the theme error page for status {}.", status, t);
        }
        if (response.isCommitted()) {
            // A partial theme response is already on the wire; appending would corrupt it.
            return;
        }
        // Discard any partial theme attempt -- status, headers (Content-Disposition,
        // X-Frame-Options, X-Fess-Route, Referrer-Policy, ...) and any buffered-but-unflushed
        // body -- so the fallback page below starts from a clean response instead of mixing its
        // own headers with stale ones from the failed attempt. This also drops any header set
        // earlier in the REQUEST chain before the error occurred (a freshly issued session
        // cookie, CORS headers): an acceptable trade here, since the visitor is getting the
        // no-theme-available fallback page, not the response those headers were meant for.
        response.reset();
        writeMinimalHtml(response, status, displayCode);
    }

    /**
     * Resolves the HTTP status to report.
     *
     * @param request the request being answered
     * @param response the response being answered; consulted for a FORWARD dispatch (or an
     *        ERROR dispatch with a missing/invalid status attribute), neither of which carries
     *        a usable {@code jakarta.servlet.error.status_code} attribute of its own
     * @param dispatcherType how this servlet was reached
     * @return the container's error status; otherwise a status already set on the response when
     *         it is in the 400..599 range; otherwise 500
     */
    static int resolveStatus(final HttpServletRequest request, final HttpServletResponse response, final DispatcherType dispatcherType) {
        if (dispatcherType == DispatcherType.ERROR
                && request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) instanceof final Integer code && code >= 400 && code <= 599) {
            return code;
        }
        // A show-errors forward carries no error attributes: honour a status the action already
        // set on the response (e.g. a validation failure that called response.setStatus(400)
        // before forwarding here), so it is not reported as a server error.
        final int responseStatus = response.getStatus();
        return responseStatus >= 400 && responseStatus <= 599 ? responseStatus : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

    /**
     * Decides whether the client wants a page or a status with a short body.
     *
     * @param request the request being answered
     * @param requestUri the original request URI, as recorded by the container
     * @return true when the caller is an API client or does not accept HTML
     */
    static boolean isNonHtmlClient(final HttpServletRequest request, final String requestUri) {
        if (WebApiUtil.isApiRequestUri(requestUri, request.getContextPath())) {
            return true;
        }
        final String accept = request.getHeader("Accept");
        return accept == null || !accept.toLowerCase(Locale.ROOT).contains("text/html");
    }

    /**
     * Maps the HTTP status to the code the error page displays.
     *
     * @param status the HTTP status being sent
     * @return 400, 403, 404, 429, 500 or 503
     */
    static int displayCode(final int status) {
        return switch (status) {
        case 400, 403, 404, 429, 500, 503 -> status;
        // The SPA has no 401 page, and a browser reaching this point already failed the
        // challenge; 403 is what it means to the visitor. The HTTP status stays 401.
        case 401 -> HttpServletResponse.SC_FORBIDDEN;
        default -> status < 500 ? HttpServletResponse.SC_BAD_REQUEST : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        };
    }

    /**
     * The one-line body sent to a client that does not take HTML.
     *
     * @param status the HTTP status being sent
     * @return the message text, without a trailing newline
     */
    static String reasonText(final int status) {
        return switch (status) {
        case 400 -> "Bad Request.";
        // The wording API clients have received for a 401 since fess#571; keep it verbatim.
        case 401 -> "Bad Authentication.";
        case 403 -> "Forbidden.";
        case 404 -> "Not Found.";
        case 408 -> "Request Timeout.";
        case 429 -> "Too Many Requests.";
        case 503 -> "Service Unavailable.";
        default -> status < 500 ? "Bad Request." : "System Error.";
        };
    }

    /**
     * Reads the error detail key an action stored for this page.
     *
     * <p>When none was stored and the status is 401, defaults to {@code errors.bad_authentication}
     * -- the key the removed {@code redirect.jsp} sent browsers for a 401, which every i18n bundle
     * still translates as {@code error.detail_bad_authentication}. Without this default a browser
     * 401 (e.g. a challenge failure with no action-set attribute) shows no detail line at all.</p>
     *
     * @param request the request being answered
     * @param status the HTTP status being reported
     * @return the key, or null when absent, not in the allowed shape, and not a 401
     */
    static String resolveDetailKey(final HttpServletRequest request, final int status) {
        if (request.getAttribute(Constants.ERROR_DETAIL_KEY) instanceof final String key && StaticThemeResponder.isSafeMessageKey(key)) {
            return key;
        }
        return status == HttpServletResponse.SC_UNAUTHORIZED ? "errors.bad_authentication" : null;
    }

    /**
     * Resolves the active theme for the request, using the request's virtual host key.
     *
     * @param request the request being answered
     * @return the resolved theme, or null when the registry has none (not even the bundled theme)
     */
    Theme resolveTheme(final HttpServletRequest request) {
        final ThemeRegistry registry = themeRegistry != null ? themeRegistry : ComponentUtil.getThemeRegistry();
        final String hostKey = resolveVirtualHostKey(request);
        return registry.resolveActiveTheme(hostKey).orElse(null);
    }

    /**
     * Resolves the virtual host key for a request without going through LastaFlute.
     *
     * <p>{@code VirtualHostHelper.getVirtualHostKey()} reads the current request through
     * {@code LaRequestUtil}, which is only bound while one of LastaFlute's filters is on the
     * stack; the container's ERROR dispatch runs none of them. This method duplicates that
     * helper's header matching here, in the servlet layer, rather than adding a request-taking
     * overload to {@code VirtualHostHelper} itself: that class is a LastaDi component named in
     * {@code fess.xml}, which the crawler, thumbnail, suggest and chunk child processes load to
     * build their own DI container on a classpath that has no servlet API -- a servlet-typed
     * public method there fails every one of those jobs at container startup.</p>
     *
     * @param request the request to match against the configured virtual hosts; may be null
     * @return the matched virtual host key, or an empty string when nothing matches
     */
    static String resolveVirtualHostKey(final HttpServletRequest request) {
        if (request == null) {
            return StringUtil.EMPTY;
        }
        final Object cached = request.getAttribute(FessConfig.VIRTUAL_HOST_VALUE);
        if (cached instanceof final String key) {
            return key;
        }
        final String value = matchVirtualHost(request);
        request.setAttribute(FessConfig.VIRTUAL_HOST_VALUE, value);
        return value;
    }

    /**
     * Matches the request's headers against the configured virtual hosts.
     *
     * @param request the request to inspect
     * @return the matched key, or an empty string
     */
    static String matchVirtualHost(final HttpServletRequest request) {
        for (final Tuple3<String, String, String> host : ComponentUtil.getFessConfig().getVirtualHosts()) {
            if (host.getValue2().equalsIgnoreCase(request.getHeader(host.getValue1()))) {
                return host.getValue3();
            }
        }
        return StringUtil.EMPTY;
    }

    /**
     * Resolves the responder used to render the theme entry as an error page.
     *
     * @return the responder
     */
    StaticThemeResponder resolveResponder() {
        return staticThemeResponder != null ? staticThemeResponder : ComponentUtil.getStaticThemeResponder();
    }

    /**
     * Writes a short, status-only response for a client that does not want the HTML page.
     *
     * @param response the response to write to
     * @param status the HTTP status to send
     * @throws IOException if writing the response fails
     */
    static void writeText(final HttpServletResponse response, final int status) throws IOException {
        final byte[] body = reasonText(status).getBytes(StandardCharsets.UTF_8);
        response.setStatus(status);
        response.setContentType("text/plain; charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setContentLength(body.length);
        response.getOutputStream().write(body);
    }

    /**
     * Writes the minimal, script-free HTML fallback page used when no theme can answer.
     *
     * <p>Carries no script, stylesheet or link, and locks the CSP to {@code default-src 'none'}, so
     * it renders even when the theme, the registry or the DI container itself is broken.</p>
     *
     * @param response the response to write to
     * @param status the HTTP status to send
     * @param displayCode the error code the page shows
     * @throws IOException if writing the response fails
     */
    static void writeMinimalHtml(final HttpServletResponse response, final int status, final int displayCode) throws IOException {
        // Paired with reasonText(displayCode), not reasonText(status): the page's own numeral is
        // displayCode (e.g. 403), so its text must be the meaning of that same number (e.g.
        // "Forbidden."), not the real status's (a 401 would otherwise render "403 Bad
        // Authentication.").
        final String html = "<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n<meta charset=\"utf-8\">\n<title>" + displayCode + " "
                + reasonText(displayCode) + "</title>\n</head>\n<body>\n<h1>" + displayCode + " " + reasonText(displayCode)
                + "</h1>\n</body>\n</html>\n";
        final byte[] body = html.getBytes(StandardCharsets.UTF_8);
        response.setStatus(status);
        response.setContentType("text/html; charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("Content-Security-Policy", "default-src 'none'");
        response.setHeader("X-Fess-Error-Code", String.valueOf(displayCode));
        response.setContentLength(body.length);
        response.getOutputStream().write(body);
    }

    // ---- Test seams ----

    /**
     * Overrides the theme registry lookup for tests.
     *
     * @param registry the registry to use, or null to fall back to the container
     */
    void setThemeRegistry(final ThemeRegistry registry) {
        this.themeRegistry = registry;
    }

    /**
     * Overrides the static theme responder lookup for tests.
     *
     * @param responder the responder to use, or null to fall back to the container
     */
    void setStaticThemeResponder(final StaticThemeResponder responder) {
        this.staticThemeResponder = responder;
    }
}
