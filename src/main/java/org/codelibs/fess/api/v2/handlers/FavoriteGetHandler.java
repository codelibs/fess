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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.api.v2.V2EnvelopeWriter;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.app.service.FavoriteLogService;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.helper.UserInfoHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles {@code GET /api/v2/documents/{docId}/favorite}.
 *
 * <p>Returns whether the calling user has favorited the document plus the
 * document's current favorite count. The {@code docId} is extracted from the
 * path by the manager; this handler validates it against a conservative
 * {@code [A-Za-z0-9_-]+} regex so a malformed value (path traversal attempt,
 * empty segment, …) surfaces as a structured {@link V2ErrorCode#INVALID_REQUEST}
 * envelope rather than reaching the search backend.</p>
 *
 * <p>The response payload is:</p>
 *
 * <pre>{@code
 * { "doc_id": "<id>", "favorite": <bool>, "count": <long> }
 * }</pre>
 *
 * <p>The {@code count} field is always present (defaults to {@code 0}) so
 * client SDKs can rely on a stable shape rather than branching on its presence.
 * POST/DELETE handling lives in Plan 3 — this handler only services GET.</p>
 */
public class FavoriteGetHandler {

    private static final Logger logger = LogManager.getLogger(FavoriteGetHandler.class);

    /**
     * Default constructor. The handler is stateless and intended to be
     * instantiated once by the API manager and shared across concurrent requests.
     */
    public FavoriteGetHandler() {
        // no-op
    }

    /**
     * Processes one {@code /api/v2/documents/{docId}/favorite} GET request.
     *
     * <p>Order of checks:</p>
     * <ol>
     *   <li>HTTP method must be {@code GET}.</li>
     *   <li>{@code docId} must be non-blank and match the safety regex.</li>
     *   <li>{@code user.favorite} feature flag must be enabled.</li>
     *   <li>Document must exist (or 404).</li>
     * </ol>
     *
     * <p>When the user code is blank (anonymous caller) the {@code favorite}
     * flag is reported as {@code false} but the {@code count} still reflects
     * the stored value, matching v1's read-only semantics for unauthenticated
     * favorite queries.</p>
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @param docId the document id extracted from the URL path (already trimmed of surrounding path segments)
     * @throws IOException if writing the envelope fails
     */
    public void handle(final HttpServletRequest req, final HttpServletResponse res, final String docId) throws IOException {
        if (!"GET".equalsIgnoreCase(req.getMethod())) {
            res.setHeader("Allow", "GET");
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        if (!ComponentUtil.getV2DocIdValidator().isValid(docId)) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INVALID_REQUEST, "invalid doc_id");
            return;
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isUserFavorite()) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INVALID_REQUEST, "favorite feature is not available");
            return;
        }
        try {
            final SearchHelper searchHelper = ComponentUtil.getSearchHelper();
            // Restrict the fetch to just the two fields we need so the helper does not
            // return the entire document over the wire — saves a round-trip-worth of bytes
            // on the common case where the document is large.
            final OptionalEntity<Map<String, Object>> docOpt = searchHelper.getDocumentByDocId(docId,
                    new String[] { fessConfig.getIndexFieldUrl(), fessConfig.getIndexFieldFavoriteCount() }, OptionalThing.empty());
            if (!docOpt.isPresent()) {
                ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.NOT_FOUND, "doc not found: " + docId);
                return;
            }
            final Map<String, Object> doc = docOpt.get();
            final String url = DocumentUtil.getValue(doc, fessConfig.getIndexFieldUrl(), String.class);
            final Long countValue = DocumentUtil.getValue(doc, fessConfig.getIndexFieldFavoriteCount(), Long.class);
            final long count = countValue == null ? 0L : countValue.longValue();

            boolean favorite = false;
            final UserInfoHelper userInfoHelper = ComponentUtil.getUserInfoHelper();
            final String userCode = userInfoHelper == null ? null : userInfoHelper.getUserCode();
            if (StringUtil.isNotBlank(userCode) && StringUtil.isNotBlank(url)) {
                final FavoriteLogService favoriteLogService = ComponentUtil.getComponent(FavoriteLogService.class);
                favorite = !favoriteLogService.getUrlList(userCode, List.of(url)).isEmpty();
            }

            final Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("doc_id", docId);
            payload.put("favorite", favorite);
            payload.put("count", count);
            ComponentUtil.getV2EnvelopeWriter().writeSuccess(res, payload);
        } catch (final Exception e) {
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(res, e, logger, "/api/v2/documents/" + docId + "/favorite GET");
        }
    }
}
