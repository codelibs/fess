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
import org.codelibs.fess.api.v2.handlers.CsrfRequirement;
import org.codelibs.fess.api.v2.handlers.FavoriteGetHandler;
import org.codelibs.fess.api.v2.handlers.LoginHandler;
import org.codelibs.fess.api.v2.handlers.LogoutHandler;
import org.codelibs.fess.api.v2.handlers.PasswordChangeHandler;
import org.codelibs.fess.api.v2.handlers.MeHandler;
import org.codelibs.fess.api.v2.handlers.ScrollSearchHandler;
import org.codelibs.fess.api.v2.handlers.SearchHandler;
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

    // MeHandler is stateless — shared single instance is safe across concurrent requests.
    private final MeHandler meHandler = new MeHandler();

    // LogoutHandler is stateless — shared single instance is safe across concurrent requests.
    private final LogoutHandler logoutHandler = new LogoutHandler();

    // PasswordChangeHandler is stateless — shared single instance is safe across concurrent requests.
    private final PasswordChangeHandler passwordChangeHandler = new PasswordChangeHandler();

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
        return servletPath != null && servletPath.startsWith(pathPrefix);
    }

    @Override
    public void process(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final String sub = subPath(request);
        if (ComponentUtil.getFessConfig().isThemeApiCsrfRequired() && CsrfRequirement.requiresCsrf(sub, request.getMethod())) {
            final HttpSession session = request.getSession(false);
            final String header = request.getHeader("X-Fess-CSRF-Token");
            final SessionCsrfTokenManager csrf = ComponentUtil.getComponent(SessionCsrfTokenManager.class);
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
                favoriteGetHandler.handle(request, response, docId);
                return;
            }
            switch (sub) {
            case "/health" -> handleHealth(response);
            case "/search" -> searchHandler.handle(request, response);
            case "/suggest-words" -> handleSuggestWords(request, response);
            case "/labels" -> handleLabels(request, response);
            case "/popular-words" -> handlePopularWords(request, response);
            case "/auth/me" -> meHandler.handle(request, response);
            case "/auth/login" -> loginHandler().handle(request, response);
            case "/auth/logout" -> logoutHandler.handle(request, response);
            case "/auth/password" -> passwordChangeHandler.handle(request, response);
            default -> V2EnvelopeWriter.writeError(response, V2ErrorCode.NOT_FOUND, "endpoint not found: " + sub);
            }
        } catch (final Exception e) {
            logger.warn("/api/v2 handler failed for {}", sub, e);
            V2EnvelopeWriter.writeError(response, V2ErrorCode.INTERNAL_ERROR, e.getMessage());
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
        return p.length() > pathPrefix.length() ? p.substring(pathPrefix.length()) : "";
    }

    /**
     * Returns a structured snapshot of the search engine's cluster health.
     *
     * <p>Mirrors the v1 ping endpoint, but emits the v2 envelope shape and exposes
     * just the high-level fields most useful to monitoring tooling. If the engine
     * is unreachable we still emit a structured error envelope rather than letting
     * the exception escape.</p>
     *
     * @param response the HTTP response to write to
     * @throws IOException if writing the envelope fails
     */
    private void handleHealth(final HttpServletResponse response) throws IOException {
        try {
            final PingResponse ping = ComponentUtil.getSearchEngineClient().ping();
            final Map<String, Object> engine = new LinkedHashMap<>();
            engine.put("cluster_name", ping.getClusterName());
            engine.put("status", ping.getClusterStatus());
            engine.put("ping_status", ping.getStatus());
            V2EnvelopeWriter.writeSuccess(response, Map.of("engine", engine));
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process /api/v2/health.", e);
            }
            V2EnvelopeWriter.writeError(response, V2ErrorCode.INTERNAL_ERROR, "engine unreachable: " + e.getMessage());
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
            V2EnvelopeWriter.writeError(response, V2ErrorCode.INVALID_REQUEST, "method not allowed");
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
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process /api/v2/suggest-words.", e);
            }
            V2EnvelopeWriter.writeError(response, V2ErrorCode.INTERNAL_ERROR, e.getMessage());
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
            V2EnvelopeWriter.writeError(response, V2ErrorCode.INVALID_REQUEST, "method not allowed");
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
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process /api/v2/labels.", e);
            }
            V2EnvelopeWriter.writeError(response, V2ErrorCode.INTERNAL_ERROR, e.getMessage());
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
            V2EnvelopeWriter.writeError(response, V2ErrorCode.INVALID_REQUEST, "method not allowed");
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
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process /api/v2/popular-words.", e);
            }
            V2EnvelopeWriter.writeError(response, V2ErrorCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    @Override
    protected void writeHeaders(final HttpServletResponse response) {
        // v2 envelopes are written through V2EnvelopeWriter which sets its own Content-Type;
        // no additional response headers are required here for the skeleton.
    }
}
