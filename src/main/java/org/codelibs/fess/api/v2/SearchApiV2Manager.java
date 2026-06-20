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
package org.codelibs.fess.api.v2;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.api.BaseApiManager;
import org.codelibs.fess.api.v2.handlers.CacheHandler;
import org.codelibs.fess.api.v2.handlers.ChatHandler;
import org.codelibs.fess.api.v2.handlers.ChatSessionClearHandler;
import org.codelibs.fess.api.v2.handlers.ChatStreamHandler;
import org.codelibs.fess.api.v2.handlers.ClickHandler;
import org.codelibs.fess.api.v2.handlers.FavoriteGetHandler;
import org.codelibs.fess.api.v2.handlers.FavoritePostHandler;
import org.codelibs.fess.api.v2.handlers.FavoritesListHandler;
import org.codelibs.fess.api.v2.handlers.HealthHandler;
import org.codelibs.fess.api.v2.handlers.LabelsHandler;
import org.codelibs.fess.api.v2.handlers.LoginHandler;
import org.codelibs.fess.api.v2.handlers.LogoutHandler;
import org.codelibs.fess.api.v2.handlers.MeHandler;
import org.codelibs.fess.api.v2.handlers.PasswordChangeHandler;
import org.codelibs.fess.api.v2.handlers.PopularWordsHandler;
import org.codelibs.fess.api.v2.handlers.RelatedContentHandler;
import org.codelibs.fess.api.v2.handlers.RelatedQueriesHandler;
import org.codelibs.fess.api.v2.handlers.ScrollSearchHandler;
import org.codelibs.fess.api.v2.handlers.SearchHandler;
import org.codelibs.fess.api.v2.handlers.SuggestWordsHandler;
import org.codelibs.fess.api.v2.handlers.UiConfigHandler;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Web API manager for the {@code /api/v2} surface.
 *
 * <p>This manager registers itself under the {@code /api/v2} path prefix and
 * dispatches by sub-path to DI-managed handlers.</p>
 *
 * <p>All responses use {@link V2EnvelopeWriter} so the wire shape stays uniform:
 * a top-level {@code {"response": {...}}} envelope with {@code status} plus
 * either a payload or an {@code error} object.</p>
 */
public class SearchApiV2Manager extends BaseApiManager {

    private static final Logger logger = LogManager.getLogger(SearchApiV2Manager.class);

    // v2 handlers are DI singletons (registered in fess_api.xml) injected by field name.
    // All handlers are stateless and safe to share across concurrent requests; they resolve
    // their per-request collaborators via ComponentUtil. The container injects these fields
    // after construction; unit tests that instantiate this manager directly (e.g.
    // {@code new SearchApiV2Manager()}) must set the handler fields they exercise.

    /** Handles {@code POST /api/v2/search}. */
    @Resource
    protected SearchHandler searchHandler;

    /** Handles {@code GET /api/v2/documents/all} (scroll over all matching documents). */
    @Resource
    protected ScrollSearchHandler scrollSearchHandler;

    /** Handles {@code GET /api/v2/documents/{id}/favorite} (read favorite state). */
    @Resource
    protected FavoriteGetHandler favoriteGetHandler;

    /** Handles {@code POST /api/v2/documents/{id}/favorite} (toggle favorite state). */
    @Resource
    protected FavoritePostHandler favoritePostHandler;

    /** Handles {@code GET /api/v2/favorites} (list favorited doc ids in a previously issued search result). */
    @Resource
    protected FavoritesListHandler favoritesListHandler;

    /** Handles {@code GET /api/v2/auth/me} (current authenticated user). */
    @Resource
    protected MeHandler meHandler;

    /** Handles {@code POST /api/v2/auth/logout}. */
    @Resource
    protected LogoutHandler logoutHandler;

    /** Handles {@code POST /api/v2/auth/password} (change the current user's password). */
    @Resource
    protected PasswordChangeHandler passwordChangeHandler;

    /** Handles {@code GET /api/v2/ui/config} (SPA bootstrap configuration). */
    @Resource
    protected UiConfigHandler uiConfigHandler;

    /** Handles {@code POST /api/v2/click} (click-through logging). */
    @Resource
    protected ClickHandler clickHandler;

    /** Handles {@code POST /api/v2/chat} (single-shot chat completion). */
    @Resource
    protected ChatHandler chatHandler;

    /** Handles {@code POST /api/v2/chat/stream} (server-sent-event chat stream). */
    @Resource
    protected ChatStreamHandler chatStreamHandler;

    /** Handles {@code DELETE /api/v2/chat/sessions/{session_id}} (clear a chat session). */
    @Resource
    protected ChatSessionClearHandler chatSessionClearHandler;

    /** Handles {@code GET /api/v2/cache/{id}} (cached document content). */
    @Resource
    protected CacheHandler cacheHandler;

    /** Handles {@code GET /api/v2/health}. */
    @Resource
    protected HealthHandler healthHandler;

    /** Handles {@code GET /api/v2/suggest-words}. */
    @Resource
    protected SuggestWordsHandler suggestWordsHandler;

    /** Handles {@code GET /api/v2/labels}. */
    @Resource
    protected LabelsHandler labelsHandler;

    /** Handles {@code GET /api/v2/popular-words}. */
    @Resource
    protected PopularWordsHandler popularWordsHandler;

    /** Handles {@code POST /api/v2/auth/login}. */
    @Resource
    protected LoginHandler loginHandler;

    /** Handles {@code GET /api/v2/related-queries} (related query suggestions). */
    @Resource
    protected RelatedQueriesHandler relatedQueriesHandler;

    /** Handles {@code GET /api/v2/related-content} (related HTML content). */
    @Resource
    protected RelatedContentHandler relatedContentHandler;

    /**
     * Constructor — pins the path prefix to {@code /api/v2}.
     */
    public SearchApiV2Manager() {
        setPathPrefix("/api/v2");
    }

    /**
     * Registers this manager with the global {@link org.codelibs.fess.api.WebApiManagerFactory}
     * once Lasta DI finishes injecting dependencies.
     */
    @PostConstruct
    public void register() {
        if (logger.isInfoEnabled()) {
            logger.info("Loaded {}", this.getClass().getSimpleName());
        }
        ComponentUtil.getWebApiManagerFactory().add(this);
    }

    @Override
    public boolean matches(final HttpServletRequest request) {
        if (!ComponentUtil.getFessConfig().isWebApiJson()) {
            return false;
        }
        final String servletPath = request.getServletPath();
        return servletPath != null && (servletPath.equals(pathPrefix) || servletPath.startsWith(pathPrefix + "/"));
    }

    @Override
    public void process(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        // Default every v2 response to `no-store`: success envelopes can carry the session
        // CSRF token (/ui/config), user identity (/auth/me, /auth/login) or role-filtered
        // hits (/search, /favorites), none of which a shared cache may ever store. Without
        // an explicit Cache-Control a 200 GET response is eligible for HTTP heuristic
        // caching. Set BEFORE writeHeaders so an operator-configured Cache-Control in
        // `api.json.response.headers` deliberately overrides this default; the error path
        // (ComponentUtil.getV2EnvelopeWriter().writeErrorWithDetails) re-asserts `no-store` independently.
        response.setHeader("Cache-Control", "no-store");
        // Apply the configured `api.json.response.headers` (e.g. Referrer-Policy) at the very
        // top of dispatch so EVERY v2 response carries the security-baseline headers —
        // including CSRF rejections, NDJSON / SSE streams, and error envelopes. Unlike v1
        // which routes through BaseApiManager.write(...) → writeHeaders(...), v2 writes
        // envelopes through V2EnvelopeWriter directly, so we must call writeHeaders here
        // ourselves. resetBuffer() in V2EnvelopeWriter only clears the body buffer per the
        // servlet spec; headers set here are preserved.
        writeHeaders(response);
        final String sub = subPath(request);
        if (ComponentUtil.getFessConfig().isThemeApiCsrfRequired()
                && ComponentUtil.getV2CsrfRequirement().requiresCsrf(sub, request.getMethod())) {
            final HttpSession session = request.getSession(false);
            final String header = request.getHeader("X-Fess-CSRF-Token");
            final SessionCsrfTokenManager csrf = ComponentUtil.getSessionCsrfTokenManager();
            if (session == null || !csrf.verify(session, header)) {
                ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.FORBIDDEN, "invalid csrf token");
                return;
            }
        }
        try {
            // Path components that vary (docId) are matched before the static switch so the
            // exhaustive switch below can keep its readable shape. Order is important:
            // /documents/all takes precedence over the generic /documents/{id}/favorite
            // pattern because "all" would otherwise be interpreted as a doc id.
            if ("/documents/all".equals(sub)) {
                scrollSearchHandler.handle(request, response);
                return;
            }
            if (sub.startsWith("/documents/") && sub.endsWith("/favorite")) {
                final String docId = sub.substring("/documents/".length(), sub.length() - "/favorite".length());
                if ("POST".equalsIgnoreCase(request.getMethod())) {
                    favoritePostHandler.handle(request, response, docId);
                } else {
                    favoriteGetHandler.handle(request, response, docId);
                }
                return;
            }
            if (sub.startsWith("/cache/")) {
                final String docId = sub.substring("/cache/".length());
                cacheHandler.handle(request, response, docId);
                return;
            }
            // DELETE /api/v2/chat/sessions/{session_id}
            if (sub.startsWith("/chat/sessions/")) {
                final String sessionId = sub.substring("/chat/sessions/".length());
                chatSessionClearHandler.handle(request, response, sessionId);
                return;
            }
            // Unknown sub-path under /documents/{id}/... — return a clearer not_found message
            // than the generic "endpoint not found" emitted by the switch default arm. This
            // closes the dispatch hole where /documents/abc/foo silently fell through.
            if (sub.startsWith("/documents/")) {
                ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.NOT_FOUND, "unknown action on document: " + sub);
                return;
            }
            switch (sub) {
            case "/health" -> healthHandler.handle(request, response);
            case "/search" -> searchHandler.handle(request, response);
            case "/favorites" -> favoritesListHandler.handle(request, response);
            case "/suggest-words" -> suggestWordsHandler.handle(request, response);
            case "/labels" -> labelsHandler.handle(request, response);
            case "/popular-words" -> popularWordsHandler.handle(request, response);
            case "/auth/me" -> meHandler.handle(request, response);
            case "/auth/login" -> loginHandler.handle(request, response);
            case "/auth/logout" -> logoutHandler.handle(request, response);
            case "/auth/password" -> passwordChangeHandler.handle(request, response);
            case "/ui/config" -> uiConfigHandler.handle(request, response);
            case "/click" -> clickHandler.handle(request, response);
            case "/chat" -> chatHandler.handle(request, response);
            case "/chat/stream" -> chatStreamHandler.handle(request, response);
            case "/related-queries" -> relatedQueriesHandler.handle(request, response);
            case "/related-content" -> relatedContentHandler.handle(request, response);
            default -> ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.NOT_FOUND, "endpoint not found: " + sub);
            }
        } catch (final IOException e) {
            // Streaming handlers can disconnect mid-write (broken pipe, client abort). Once
            // the response is committed there's no envelope to write — re-throw so the
            // container can terminate the connection cleanly. If we are NOT committed yet
            // the IOException came from somewhere before the first byte; log it like any
            // other failure and emit the standard internal-error envelope.
            if (response.isCommitted()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("/api/v2 handler IO failure after commit for {}", sub, e);
                } else if (logger.isInfoEnabled()) {
                    logger.info("/api/v2 handler IO failure after commit for sub={}", sub);
                }
                throw e;
            }
            logger.warn("/api/v2 handler failed for {}", sub, e);
            ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.INTERNAL_ERROR, "internal error");
        } catch (final Exception e) {
            if (response.isCommitted()) {
                // SSE / NDJSON handlers have already started flushing; the v2 envelope can no
                // longer be written without producing a malformed wire frame. Do not attempt
                // a second write — log and return so the in-flight body is left intact.
                logger.warn("/api/v2 handler failed after commit for {}", sub, e);
                return;
            }
            logger.warn("/api/v2 handler failed for {}", sub, e);
            // Do not leak e.getMessage() to the wire — message content from upstream
            // libraries can include connection strings, stack-trace fragments, or other
            // information the SPA must not see.
            ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.INTERNAL_ERROR, "internal error");
        }
    }

    /**
     * Extracts the portion of the servlet path after the {@code /api/v2} prefix.
     *
     * @param request the incoming HTTP request
     * @return the sub-path (e.g. {@code "/health"}) or an empty string when the
     *         request targets the prefix root itself
     */
    String subPath(final HttpServletRequest request) {
        final String p = request.getServletPath();
        if (p == null) {
            return "";
        }
        String sub = p.length() > pathPrefix.length() ? p.substring(pathPrefix.length()) : "";
        // Strip a trailing slash so that /api/v2/health/ and /api/v2/health dispatch to
        // the same switch arm. Preserve "/" alone (sub == "/") since that still maps to
        // the empty prefix root case in practice — the switch default handles it.
        if (sub.length() > 1 && sub.endsWith("/")) {
            sub = sub.substring(0, sub.length() - 1);
        }
        return sub;
    }

    @Override
    protected void writeHeaders(final HttpServletResponse response) {
        // Mirror v1 SearchApiManager.writeHeaders() so the configured
        // `api.json.response.headers` (default: Referrer-Policy: strict-origin-when-cross-origin)
        // apply uniformly across both v1 and v2 JSON responses. V2EnvelopeWriter only sets the
        // Content-Type / charset; security-baseline headers must be applied here so they reach
        // every response that flows through write(...) → writeHeaders(...).
        // Vary is merged (addHeader) so it does not clobber CorsFilter's `Vary: Origin`.
        ComponentUtil.getFessConfig().getApiJsonResponseHeaderList().forEach(e -> {
            if ("Vary".equalsIgnoreCase(e.getFirst())) {
                response.addHeader(e.getFirst(), e.getSecond());
            } else {
                response.setHeader(e.getFirst(), e.getSecond());
            }
        });
    }
}
