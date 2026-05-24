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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.BaseApiManager;
import org.codelibs.fess.api.v2.handlers.CacheHandler;
import org.codelibs.fess.api.v2.handlers.ChatHandler;
import org.codelibs.fess.api.v2.handlers.ChatSessionClearHandler;
import org.codelibs.fess.api.v2.handlers.ChatStreamHandler;
import org.codelibs.fess.api.v2.handlers.ClickHandler;
import org.codelibs.fess.api.v2.handlers.CsrfRequirement;
import org.codelibs.fess.api.v2.handlers.FavoriteGetHandler;
import org.codelibs.fess.api.v2.handlers.FavoritePostHandler;
import org.codelibs.fess.api.v2.handlers.LoginHandler;
import org.codelibs.fess.api.v2.handlers.LogoutHandler;
import org.codelibs.fess.api.v2.handlers.PasswordChangeHandler;
import org.codelibs.fess.api.v2.handlers.MeHandler;
import org.codelibs.fess.api.v2.handlers.ScrollSearchHandler;
import org.codelibs.fess.api.v2.handlers.SearchHandler;
import org.codelibs.fess.api.v2.handlers.UiConfigHandler;
import org.codelibs.fess.entity.PingResponse;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.PopularWordHelper;
import org.codelibs.fess.helper.SessionCsrfTokenManager;
import org.codelibs.fess.helper.SuggestHelper;
import org.codelibs.fess.helper.VirtualHostHelper;
import org.codelibs.fess.suggest.entity.SuggestItem;
import org.codelibs.fess.suggest.request.suggest.SuggestRequestBuilder;
import org.codelibs.fess.suggest.request.suggest.SuggestResponse;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Web API manager for the {@code /api/v2} surface.
 *
 * <p>This skeleton registers itself under the {@code /api/v2} path prefix and
 * dispatches by sub-path to specific handlers. The {@code /health} endpoint is
 * wired up here; later batches add search, suggest, auth, and theme endpoints.</p>
 *
 * <p>All responses use {@link V2EnvelopeWriter} so the wire shape stays uniform:
 * a top-level {@code {"response": {...}}} envelope with {@code status} and
 * {@code version} fields plus either a payload or an {@code error} object.</p>
 */
public class SearchApiV2Manager extends BaseApiManager {

    private static final Logger logger = LogManager.getLogger(SearchApiV2Manager.class);

    // SearchHandler is stateless — a single shared instance avoids per-request allocation
    // and keeps the manager small as more handler classes are extracted in later batches.
    private final SearchHandler searchHandler = new SearchHandler();

    // ScrollSearchHandler is stateless aside from the per-request Jackson mapper it
    // allocates internally; safe to share a single instance across concurrent requests.
    private final ScrollSearchHandler scrollSearchHandler = new ScrollSearchHandler();

    // FavoriteGetHandler is stateless — same singleton pattern as the other v2 handlers.
    private final FavoriteGetHandler favoriteGetHandler = new FavoriteGetHandler();

    // FavoritePostHandler is stateless — shared single instance is safe across concurrent requests.
    private final FavoritePostHandler favoritePostHandler = new FavoritePostHandler();

    // MeHandler is stateless — shared single instance is safe across concurrent requests.
    private final MeHandler meHandler = new MeHandler();

    // LogoutHandler is stateless — shared single instance is safe across concurrent requests.
    private final LogoutHandler logoutHandler = new LogoutHandler();

    // PasswordChangeHandler is stateless — shared single instance is safe across concurrent requests.
    private final PasswordChangeHandler passwordChangeHandler = new PasswordChangeHandler();

    // UiConfigHandler is stateless — shared single instance is safe across concurrent requests.
    private final UiConfigHandler uiConfigHandler = new UiConfigHandler();

    // ClickHandler is stateless — shared single instance is safe across concurrent requests.
    private final ClickHandler clickHandler = new ClickHandler();

    // ChatHandler is stateless — delegates to ComponentUtil.getChatClient() per request.
    private final ChatHandler chatHandler = new ChatHandler();

    // ChatStreamHandler is stateless — the per-request PrintWriter and SSE callback
    // are scoped to the handle() invocation, so a single shared instance is safe.
    private final ChatStreamHandler chatStreamHandler = new ChatStreamHandler();

    // ChatSessionClearHandler is stateless — shared single instance is safe across concurrent requests.
    private final ChatSessionClearHandler chatSessionClearHandler = new ChatSessionClearHandler();

    // CacheHandler is stateless — shared single instance is safe across concurrent requests.
    private final CacheHandler cacheHandler = new CacheHandler();

    // LoginHandler depends on the DI-managed LoginRateLimiter, which is not yet available
    // at field-init time. Lazy-init through loginHandler() defers the lookup to first request.
    private volatile LoginHandler loginHandler;

    private LoginHandler loginHandler() {
        LoginHandler h = loginHandler;
        if (h == null) {
            synchronized (this) {
                h = loginHandler;
                if (h == null) {
                    h = new LoginHandler(ComponentUtil.getLoginRateLimiter());
                    loginHandler = h;
                }
            }
        }
        return h;
    }

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
        // Apply the configured `api.json.response.headers` (e.g. Referrer-Policy) at the very
        // top of dispatch so EVERY v2 response carries the security-baseline headers —
        // including CSRF rejections, NDJSON / SSE streams, and error envelopes. Unlike v1
        // which routes through BaseApiManager.write(...) → writeHeaders(...), v2 writes
        // envelopes through V2EnvelopeWriter directly, so we must call writeHeaders here
        // ourselves. resetBuffer() in V2EnvelopeWriter only clears the body buffer per the
        // servlet spec; headers set here are preserved.
        writeHeaders(response);
        final String sub = subPath(request);
        if (ComponentUtil.getFessConfig().isThemeApiCsrfRequired() && CsrfRequirement.requiresCsrf(sub, request.getMethod())) {
            final HttpSession session = request.getSession(false);
            final String header = request.getHeader("X-Fess-CSRF-Token");
            final SessionCsrfTokenManager csrf = ComponentUtil.getSessionCsrfTokenManager();
            if (session == null || !csrf.verify(session, header)) {
                V2EnvelopeWriter.writeError(response, V2ErrorCode.FORBIDDEN, "invalid csrf token");
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
                V2EnvelopeWriter.writeError(response, V2ErrorCode.NOT_FOUND, "unknown action on document: " + sub);
                return;
            }
            switch (sub) {
            case "/health" -> handleHealth(request, response);
            case "/search" -> searchHandler.handle(request, response);
            case "/suggest-words" -> handleSuggestWords(request, response);
            case "/labels" -> handleLabels(request, response);
            case "/popular-words" -> handlePopularWords(request, response);
            case "/auth/me" -> meHandler.handle(request, response);
            case "/auth/login" -> loginHandler().handle(request, response);
            case "/auth/logout" -> logoutHandler.handle(request, response);
            case "/auth/password" -> passwordChangeHandler.handle(request, response);
            case "/ui/config" -> uiConfigHandler.handle(request, response);
            case "/click" -> clickHandler.handle(request, response);
            case "/chat" -> chatHandler.handle(request, response);
            case "/chat/stream" -> chatStreamHandler.handle(request, response);
            default -> V2EnvelopeWriter.writeError(response, V2ErrorCode.NOT_FOUND, "endpoint not found: " + sub);
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
            V2EnvelopeWriter.writeError(response, V2ErrorCode.INTERNAL_ERROR, "internal error");
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
            V2EnvelopeWriter.writeError(response, V2ErrorCode.INTERNAL_ERROR, "internal error");
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

    /**
     * Returns a structured snapshot of the search engine's cluster health.
     *
     * <p>Mirrors the v1 ping endpoint, but emits the v2 envelope shape and exposes
     * just the high-level fields most useful to monitoring tooling. If the engine
     * is unreachable we still emit a structured error envelope rather than letting
     * the exception escape.</p>
     *
     * @param request the incoming HTTP request
     * @param response the HTTP response to write to
     * @throws IOException if writing the envelope fails
     */
    private void handleHealth(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Allow", "GET");
            V2EnvelopeWriter.writeError(response, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        try {
            final PingResponse ping = ComponentUtil.getSearchEngineClient().ping();
            final String clusterStatus = ping.getClusterStatus();
            final Map<String, Object> engine = new LinkedHashMap<>();
            engine.put("cluster_name", ping.getClusterName());
            engine.put("status", clusterStatus);
            engine.put("ping_status", ping.getStatus());
            // Envelope invariant: envelope.status>=1 iff HTTP>=400. A red cluster maps to
            // HTTP 503, so it must emit a SERVICE_UNAVAILABLE error envelope (not the
            // success envelope used for green/yellow). The engine details are surfaced
            // through Map.of("engine", engine) in writeErrorWithDetails so monitoring
            // tooling can still parse them out of error.details.
            if ("red".equalsIgnoreCase(clusterStatus)) {
                V2EnvelopeWriter.writeErrorWithDetails(response, V2ErrorCode.SERVICE_UNAVAILABLE, "search engine cluster is red",
                        Map.of("engine", engine));
            } else {
                // yellow or green — both return 200; the engine.status field carries the detail.
                V2EnvelopeWriter.writeSuccess(response, Map.of("engine", engine));
            }
        } catch (final Exception e) {
            V2EnvelopeWriter.writeInternalError(response, e, logger, "/api/v2/health");
        }
    }

    /**
     * Handles the {@code /api/v2/suggest-words} endpoint.
     *
     * <p>Reads {@code q} (query), {@code num} (size), {@code fn} (suggest fields)
     * and {@code lang} (language) query parameters, delegates to
     * {@link SuggestHelper#suggester()} to obtain a
     * {@link SuggestRequestBuilder}, then maps each
     * {@link SuggestItem} into a snake_case payload. The wire shape is:</p>
     *
     * <pre>{@code
     * { "q": "...", "page_size": <int>, "record_count": <int>,
     *   "query_time": <long>, "suggest_words": [{"text":"...","types":[...]}] }
     * }</pre>
     *
     * <p>Non-GET requests are rejected with {@link V2ErrorCode#INVALID_REQUEST}.
     * Other failures emit {@link V2ErrorCode#INTERNAL_ERROR} so callers always
     * see a structured envelope.</p>
     *
     * @param request the incoming HTTP request
     * @param response the HTTP response to write to
     * @throws IOException if writing the envelope fails
     */
    private void handleSuggestWords(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Allow", "GET");
            V2EnvelopeWriter.writeError(response, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        try {
            final String q = request.getParameter("q");
            final String numStr = request.getParameter("num");
            final int num;
            if (StringUtil.isNotBlank(numStr) && StringUtils.isNumeric(numStr)) {
                num = Integer.parseInt(numStr);
            } else {
                num = 10;
            }
            final String[] fields = SearchRequestParams.getParamValueArray(request, "fn");
            final String[] langs = SearchRequestParams.getParamValueArray(request, "lang");
            final String[] tags = SearchRequestParams.getParamValueArray(request, "label");

            final SuggestHelper suggestHelper = ComponentUtil.getSuggestHelper();
            final SuggestRequestBuilder builder = suggestHelper.suggester().suggest();
            if (q != null) {
                builder.setQuery(q);
            }
            for (final String field : fields) {
                builder.addField(field);
            }
            ComponentUtil.getRoleQueryHelper().build(SearchRequestType.SUGGEST).forEach(builder::addRole);
            builder.setSize(num);
            for (final String lang : langs) {
                builder.addLang(lang);
            }
            for (final String tag : tags) {
                builder.addTag(tag);
            }
            final VirtualHostHelper virtualHostHelper = ComponentUtil.getVirtualHostHelper();
            final String virtualHostKey = virtualHostHelper.getVirtualHostKey();
            if (StringUtil.isNotBlank(virtualHostKey)) {
                builder.addTag(virtualHostKey);
            }
            builder.addKind(SuggestItem.Kind.USER.toString());
            if (ComponentUtil.getFessConfig().isSuggestSearchLog()) {
                builder.addKind(SuggestItem.Kind.QUERY.toString());
            }
            if (ComponentUtil.getFessConfig().isSuggestDocuments()) {
                builder.addKind(SuggestItem.Kind.DOCUMENT.toString());
            }

            final SuggestResponse suggestResponse = builder.execute().getResponse();

            final List<Map<String, Object>> items = new ArrayList<>(suggestResponse.getItems().size());
            for (final SuggestItem item : suggestResponse.getItems()) {
                final Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("text", item.getText());
                final String[] itemTags = item.getTags();
                entry.put("types", itemTags == null ? new ArrayList<String>() : List.of(itemTags));
                items.add(entry);
            }

            final Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("q", q == null ? "" : q);
            payload.put("page_size", suggestResponse.getNum());
            payload.put("record_count", suggestResponse.getTotal());
            payload.put("query_time", suggestResponse.getTookMs());
            payload.put("suggest_words", items);
            V2EnvelopeWriter.writeSuccess(response, payload);
        } catch (final Exception e) {
            V2EnvelopeWriter.writeInternalError(response, e, logger, "/api/v2/suggest-words");
        }
    }

    /**
     * Handles the {@code /api/v2/labels} endpoint.
     *
     * <p>Reads the {@code lang} parameter (only its presence in the request's
     * {@link Locale} is consumed) and returns the configured label list. The
     * wire shape is:</p>
     *
     * <pre>{@code
     * { "record_count": <int>, "labels": [{"label":"...","value":"..."}] }
     * }</pre>
     *
     * <p>The helper signature mirrors v1's {@link SearchRequestType#JSON} usage —
     * the plan's reference to {@code JSON_API} predates the enum's current shape
     * and {@code JSON_API} does not exist.</p>
     *
     * @param request the incoming HTTP request
     * @param response the HTTP response to write to
     * @throws IOException if writing the envelope fails
     */
    private void handleLabels(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Allow", "GET");
            V2EnvelopeWriter.writeError(response, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        try {
            final LabelTypeHelper labelTypeHelper = ComponentUtil.getLabelTypeHelper();
            final Locale locale = request.getLocale() == null ? Locale.ROOT : request.getLocale();
            final List<Map<String, String>> items = labelTypeHelper.getLabelTypeItemList(SearchRequestType.JSON, locale);
            final List<Map<String, Object>> labels = new ArrayList<>(items.size());
            for (final Map<String, String> item : items) {
                final Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("label", item.get(Constants.ITEM_LABEL));
                entry.put("value", item.get(Constants.ITEM_VALUE));
                labels.add(entry);
            }
            final Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("record_count", labels.size());
            payload.put("labels", labels);
            V2EnvelopeWriter.writeSuccess(response, payload);
        } catch (final Exception e) {
            V2EnvelopeWriter.writeInternalError(response, e, logger, "/api/v2/labels");
        }
    }

    /**
     * Handles the {@code /api/v2/popular-words} endpoint.
     *
     * <p>Reads {@code seed}, {@code label} (tags), and {@code field} (fields)
     * parameters, then delegates to {@link PopularWordHelper#getWordList} to
     * fetch a flat list of popular search terms. Roles are not forwarded — they
     * come from the session-bound {@code RoleQueryHelper}. The wire shape is:</p>
     *
     * <pre>{@code
     * { "record_count": <int>, "popular_words": ["..."] }
     * }</pre>
     *
     * <p>When the feature is disabled via {@code web.api.popular.word=false}
     * the response is an {@link V2ErrorCode#INVALID_REQUEST} envelope so the
     * wire contract matches v1's "unsupported operation" semantics.</p>
     *
     * @param request the incoming HTTP request
     * @param response the HTTP response to write to
     * @throws IOException if writing the envelope fails
     */
    private void handlePopularWords(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Allow", "GET");
            V2EnvelopeWriter.writeError(response, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        if (!ComponentUtil.getFessConfig().isWebApiPopularWord()) {
            V2EnvelopeWriter.writeError(response, V2ErrorCode.INVALID_REQUEST, "unsupported operation");
            return;
        }
        try {
            final String seed = request.getParameter("seed");
            // Honor v1's parameter name ("label") rather than the plan-level "tags" so
            // existing clients keep working; v2 can introduce a new alias later if needed.
            String[] tags = SearchRequestParams.getParamValueArray(request, "label");
            final String virtualHostKey = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
            if (StringUtil.isNotBlank(virtualHostKey)) {
                tags = ArrayUtils.addAll(tags, virtualHostKey);
            }
            final String[] fields = request.getParameterValues("field");
            final PopularWordHelper popularWordHelper = ComponentUtil.getPopularWordHelper();
            final List<String> words =
                    popularWordHelper.getWordList(SearchRequestType.JSON, seed, tags, null, fields, StringUtil.EMPTY_STRINGS);
            final Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("record_count", words.size());
            payload.put("popular_words", words);
            V2EnvelopeWriter.writeSuccess(response, payload);
        } catch (final Exception e) {
            V2EnvelopeWriter.writeInternalError(response, e, logger, "/api/v2/popular-words");
        }
    }

    @Override
    protected void writeHeaders(final HttpServletResponse response) {
        // Mirror v1 SearchApiManager.writeHeaders() so the configured
        // `api.json.response.headers` (default: Referrer-Policy: strict-origin-when-cross-origin)
        // apply uniformly across both v1 and v2 JSON responses. V2EnvelopeWriter only sets the
        // Content-Type / charset; security-baseline headers must be applied here so they reach
        // every response that flows through write(...) → writeHeaders(...).
        ComponentUtil.getFessConfig().getApiJsonResponseHeaderList().forEach(e -> response.setHeader(e.getFirst(), e.getSecond()));
    }
}
