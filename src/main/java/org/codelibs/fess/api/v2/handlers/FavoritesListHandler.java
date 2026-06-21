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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.app.service.FavoriteLogService;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.helper.UserInfoHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.dbflute.optional.OptionalThing;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles {@code GET /api/v2/favorites}.
 *
 * <p>Returns the document IDs in a previously-issued search result set that the
 * calling user has favorited. Mirrors the v1
 * {@code SearchApiManager#processFavoritesRequest} contract but emits the v2
 * envelope and uses snake_case keys throughout.</p>
 *
 * <p>The {@code query_id} query parameter is required and must correspond to a
 * search executed in the current session (the same opaque id echoed by
 * {@code /api/v2/search}). The handler resolves the result document ids via
 * {@link UserInfoHelper#getResultDocIds(String)}, looks up the bound URLs, then
 * filters them against the user's favorite log to produce the intersection.</p>
 *
 * <p>The response payload is:</p>
 *
 * <pre>{@code
 * { "record_count": <int>, "data": [{ "doc_id": "<id>" }, ...] }
 * }</pre>
 *
 * <p>Order of checks:</p>
 * <ol>
 *   <li>HTTP method must be {@code GET}.</li>
 *   <li>{@code user.favorite} feature flag must be enabled.</li>
 *   <li>The caller must have a non-blank {@code userCode} (a session-bound
 *       identifier produced by {@link UserInfoHelper}).</li>
 *   <li>{@code query_id} parameter must be non-blank.</li>
 * </ol>
 *
 * <p>MJ-30 i18n contract: {@code error.message} values are developer-facing
 * English strings. Clients MUST use {@code error.code} for user-facing i18n.</p>
 */
public class FavoritesListHandler {

    private static final Logger logger = LogManager.getLogger(FavoritesListHandler.class);

    /**
     * Default constructor. The handler is stateless and intended to be
     * instantiated once by the API manager and shared across concurrent requests.
     */
    public FavoritesListHandler() {
        // no-op
    }

    /**
     * Processes one {@code GET /api/v2/favorites} request.
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @throws IOException if writing the envelope fails
     */
    public void handle(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        if (!"GET".equalsIgnoreCase(req.getMethod())) {
            res.setHeader("Allow", "GET");
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isUserFavorite()) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INVALID_REQUEST, "favorite feature is not available");
            return;
        }
        // query_id arrives as a query parameter; treat it as opaque (do not URL-decode).
        // It is the same identifier returned by /api/v2/search and must match a result
        // set previously cached in the session by the search handler.
        final String queryId = req.getParameter("query_id");
        if (StringUtil.isBlank(queryId)) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INVALID_REQUEST, "query_id is required");
            return;
        }
        // Resolve the session-bound user code BEFORE doing any expensive work — anonymous
        // callers should fail fast with auth_required rather than hitting the search backend.
        final UserInfoHelper userInfoHelper = ComponentUtil.getUserInfoHelper();
        final String userCode = userInfoHelper == null ? null : userInfoHelper.getUserCode();
        if (StringUtil.isBlank(userCode)) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.AUTH_REQUIRED, "no user session");
            return;
        }
        try {
            final String[] docIds = userInfoHelper.getResultDocIds(queryId);
            if (docIds == null || docIds.length == 0) {
                // No cached search result for this query_id — return an empty list rather
                // than failing. Matches v1 behaviour where getResultDocIds returns an empty
                // array for unknown queryIds and the loop below produces an empty data list.
                writeEmpty(res);
                return;
            }
            final SearchHelper searchHelper = ComponentUtil.getSearchHelper();
            final List<Map<String, Object>> docList = searchHelper.getDocumentListByDocIds(docIds, new String[] {
                    fessConfig.getIndexFieldUrl(), fessConfig.getIndexFieldDocId(), fessConfig.getIndexFieldFavoriteCount() },
                    OptionalThing.empty(), SearchRequestType.JSON);
            final List<String> urlList = new ArrayList<>(docList.size());
            for (final Map<String, Object> doc : docList) {
                final String url = DocumentUtil.getValue(doc, fessConfig.getIndexFieldUrl(), String.class);
                if (url != null) {
                    urlList.add(url);
                }
            }
            final FavoriteLogService favoriteLogService = ComponentUtil.getComponent(FavoriteLogService.class);
            final Set<String> favoriteUrls = new HashSet<>(favoriteLogService.getUrlList(userCode, urlList));
            final List<Map<String, Object>> data = new ArrayList<>(favoriteUrls.size());
            // Preserve the result-set ordering (docList order matches docIds order) so the
            // wire response is deterministic; using a Set above only deduplicates the URL
            // membership check.
            for (final Map<String, Object> doc : docList) {
                final String url = DocumentUtil.getValue(doc, fessConfig.getIndexFieldUrl(), String.class);
                if (url == null || !favoriteUrls.contains(url)) {
                    continue;
                }
                final String docId = DocumentUtil.getValue(doc, fessConfig.getIndexFieldDocId(), String.class);
                if (docId == null) {
                    continue;
                }
                final Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("doc_id", docId);
                data.add(entry);
            }
            final Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("record_count", data.size());
            payload.put("data", data);
            ComponentUtil.getV2EnvelopeWriter().writeSuccess(res, payload);
        } catch (final Exception e) {
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(res, e, logger, "/api/v2/favorites GET");
        }
    }

    /**
     * Writes a success envelope with an empty {@code data} array and
     * {@code record_count: 0}. Used when the {@code query_id} matches no cached
     * result set in the session.
     */
    private void writeEmpty(final HttpServletResponse res) throws IOException {
        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("record_count", 0);
        payload.put("data", new ArrayList<Map<String, Object>>());
        ComponentUtil.getV2EnvelopeWriter().writeSuccess(res, payload);
    }
}
