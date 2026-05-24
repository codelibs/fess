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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.v2.V2EnvelopeWriter;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.app.service.FavoriteLogService;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.UserInfoHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.dbflute.optional.OptionalThing;
import org.opensearch.script.Script;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles {@code POST /api/v2/documents/{docId}/favorite}.
 *
 * <p>Marks a document as a favorite for the calling user and bumps the
 * document's favorite counter. Mirrors v1's
 * {@code SearchApiManager#processFavoriteRequest} but with the v2 envelope and
 * a JSON body (the {@code query_id} that v1 read from a query parameter now
 * lives in the request body).</p>
 *
 * <p>Order of checks:</p>
 * <ol>
 *   <li>HTTP method must be {@code POST}.</li>
 *   <li>{@code docId} must be non-blank and match {@code [A-Za-z0-9_-]+}.</li>
 *   <li>Caller must be authenticated (saved {@link FessUserBean} present) — the
 *       login lookup is wrapped in try/catch so unit harnesses without DBFlute
 *       behaviors are treated as anonymous (consistent with {@code MeHandler}).</li>
 *   <li>{@code user.favorite} feature flag must be enabled.</li>
 *   <li>Body must parse and contain {@code query_id}.</li>
 *   <li>The {@code docId} must appear in the user's last result set.</li>
 *   <li>The document must exist in the index.</li>
 * </ol>
 *
 * <p>The {@code ifPresent / orElse(Runnable)} shape and the exception-as-
 * control-flow inside the lambda are kept verbatim from v1 so reviewers can
 * diff the two paths side-by-side. The outer {@code try/catch} converts any
 * lambda-thrown {@link RuntimeException} into a structured v2 error envelope
 * rather than letting it bubble up as a 500 HTML page.</p>
 */
public class FavoritePostHandler {

    private static final Logger logger = LogManager.getLogger(FavoritePostHandler.class);

    /**
     * Default constructor used by the DI container. The handler holds no
     * per-request state and is safe to share across concurrent requests.
     */
    public FavoritePostHandler() {
        // no-op
    }

    // Favorite payloads are tiny — only a query_id field is consumed. 1 KiB is
    // ample headroom while making payload-bomb attacks pointless.
    private static final int MAX_BODY_BYTES = 1024;

    // Conservative whitelist — see FavoriteGetHandler for rationale.
    private static final Pattern DOC_ID_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    /**
     * Processes one {@code /api/v2/documents/{docId}/favorite} POST request.
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @param docId the document id extracted from the URL path
     * @throws IOException if writing the envelope fails
     */
    /** Thrown when the document does not exist in the search index. */
    private static class DocNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        DocNotFoundException(final String msg) {
            super(msg);
        }
    }

    /** Thrown when the favorite URL could not be added to the log store. */
    private static class FavoriteAddFailedException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        FavoriteAddFailedException(final String msg) {
            super(msg);
        }
    }

    /**
     * M-9 signal exception: thrown from inside the search-helper lambda when
     * {@link FavoriteLogService#addUrl} reports the URL was not added (typically
     * because it already exists for the calling user). The catching outer block
     * treats this as a no-op success rather than a 500 — a repeated POST of the
     * same favorite is a logical idempotent retry, not an error.
     */
    private static class AlreadyFavoritedException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        AlreadyFavoritedException() {
            super("already favorited");
        }
    }

    /**
     * Processes one {@code POST /api/v2/documents/{docId}/favorite} request.
     *
     * <p>Validates the HTTP method, document id format, authenticated session,
     * favorite feature flag and request body. On success a favorite is recorded
     * for the user and a success envelope is returned. Failures map to v2 error
     * codes ({@link V2ErrorCode#AUTH_REQUIRED}, {@link V2ErrorCode#INVALID_REQUEST},
     * {@link V2ErrorCode#NOT_FOUND}, {@link V2ErrorCode#INTERNAL_ERROR}).</p>
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @param docId the document id extracted from the URL path
     * @throws IOException if writing the envelope or reading the body fails
     */
    public void handle(final HttpServletRequest req, final HttpServletResponse res, final String docId) throws IOException {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            res.setHeader("Allow", "POST");
            V2EnvelopeWriter.writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        if (StringUtil.isBlank(docId) || !DOC_ID_PATTERN.matcher(docId).matches()) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, "invalid doc_id");
            return;
        }
        // Auth check before body parsing — avoids spending CPU on the body for
        // unauthenticated requests and gives the caller a more accurate error.
        //
        // M-17: split DI-binding failure from "no user bean present". Genuine anonymous
        // callers still see AUTH_REQUIRED (401); a system-level lookup failure surfaces
        // as INTERNAL_ERROR (500) so we do not mislead a logged-in user into thinking
        // their session expired when DI is broken.
        final FessLoginAssist assist;
        try {
            assist = ComponentUtil.getComponent(FessLoginAssist.class);
        } catch (final RuntimeException e) {
            logger.warn("/api/v2/documents/{}/favorite POST: could not acquire FessLoginAssist", docId, e);
            V2EnvelopeWriter.writeInternalError(res, e, logger, "/api/v2/documents/" + docId + "/favorite POST");
            return;
        }
        final OptionalThing<FessUserBean> userBean;
        try {
            userBean = assist.getSavedUserBean();
        } catch (final RuntimeException e) {
            logger.warn("/api/v2/documents/{}/favorite POST: getSavedUserBean failed", docId, e);
            V2EnvelopeWriter.writeInternalError(res, e, logger, "/api/v2/documents/" + docId + "/favorite POST");
            return;
        }
        if (!userBean.isPresent()) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.AUTH_REQUIRED, "login required");
            return;
        }
        final FessConfig cfg = ComponentUtil.getFessConfig();
        if (!cfg.isUserFavorite()) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, "favorite feature is not available");
            return;
        }
        final UserInfoHelper userInfoHelper = ComponentUtil.getUserInfoHelper();
        final String userCode = userInfoHelper.getUserCode();
        if (StringUtil.isBlank(userCode)) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.AUTH_REQUIRED, "no user session");
            return;
        }
        final Map<String, Object> body;
        try {
            body = V2JsonBody.read(req, MAX_BODY_BYTES);
        } catch (final V2JsonBody.PayloadTooLargeException e) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.PAYLOAD_TOO_LARGE, e.getMessage());
            return;
        } catch (final V2JsonBody.UnsupportedMediaTypeException e) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.UNSUPPORTED_MEDIA_TYPE, e.getMessage());
            return;
        } catch (final V2JsonBody.MalformedJsonException e) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, e.getMessage());
            return;
        }
        final String queryId = (String) body.get("query_id");
        if (StringUtil.isBlank(queryId)) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, "query_id is required");
            return;
        }

        // MJ-23: query_id arrives in a JSON body — it is already plain text (not
        // URL-encoded). Passing it through URLDecoder.decode would silently mangle
        // values that legitimately contain '+' or '%xx' characters. The wire contract
        // is that query_id is whatever the /search endpoint returned in its query_id
        // field, treated as opaque. Pass it directly to getResultDocIds.
        final String[] docIds;
        try {
            docIds = userInfoHelper.getResultDocIds(queryId);
        } catch (final Exception e) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, "invalid query_id");
            return;
        }
        if (docIds == null) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, "no searched urls");
            return;
        }
        if (!ArrayUtils.contains(docIds, docId)) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.NOT_FOUND, "doc not in search result");
            return;
        }

        final SearchHelper searchHelper = ComponentUtil.getSearchHelper();
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final FavoriteLogService favoriteLogService = ComponentUtil.getComponent(FavoriteLogService.class);

        try {
            searchHelper.getDocumentByDocId(docId, new String[] { cfg.getIndexFieldUrl(), cfg.getIndexFieldLang() }, OptionalThing.empty())
                    .ifPresent(doc -> {
                        final String favoriteUrl = DocumentUtil.getValue(doc, cfg.getIndexFieldUrl(), String.class);
                        if (StringUtil.isBlank(favoriteUrl)) {
                            throw new FavoriteAddFailedException("URL is null");
                        }
                        // M-9: addUrl returns false when the (user, url) pair already exists
                        // (or the user info cannot be resolved). Treat the duplicate case as
                        // an idempotent no-op success — re-POSTing the same favorite should
                        // not be a 500. We signal "already favorited" via a dedicated marker
                        // exception so the outer catch can short-circuit the favorite-count
                        // bump and emit a 200 success envelope.
                        if (!favoriteLogService.addUrl(userCode, (userInfo, favoriteLog) -> {
                            favoriteLog.setUserInfoId(userInfo.getId());
                            favoriteLog.setUrl(favoriteUrl);
                            favoriteLog.setDocId(docId);
                            favoriteLog.setQueryId(queryId);
                            favoriteLog.setCreatedAt(systemHelper.getCurrentTimeAsLocalDateTime());
                        })) {
                            throw new AlreadyFavoritedException();
                        }
                        final String id = DocumentUtil.getValue(doc, cfg.getIndexFieldId(), String.class);
                        searchHelper.update(id, builder -> {
                            final Script script = ComponentUtil.getLanguageHelper()
                                    .createScript(doc, "ctx._source." + cfg.getIndexFieldFavoriteCount() + "+=1");
                            builder.setScript(script);
                            final Map<String, Object> upsertMap = new HashMap<>();
                            upsertMap.put(cfg.getIndexFieldFavoriteCount(), 1);
                            builder.setUpsert(upsertMap);
                            builder.setRefreshPolicy(Constants.TRUE);
                        });
                    })
                    .orElse(() -> {
                        throw new DocNotFoundException("doc not found: " + docId);
                    });
        } catch (final DocNotFoundException e) {
            logger.warn("/api/v2/documents/{}/favorite POST: doc not found", docId, e);
            V2EnvelopeWriter.writeError(res, V2ErrorCode.NOT_FOUND, "doc not found: " + docId);
            return;
        } catch (final AlreadyFavoritedException e) {
            // M-9: idempotent re-POST — return 200 with the same payload shape plus an
            // optional already_existed:true marker so clients can distinguish "freshly
            // marked" from "already marked" without breaking the documented schema (the
            // OpenAPI FavoritePostResponse does not declare additionalProperties:false).
            if (logger.isDebugEnabled()) {
                logger.debug("/api/v2/documents/{}/favorite POST: already favorited (idempotent no-op)", docId);
            }
            final Map<String, Object> idempotentPayload = new java.util.LinkedHashMap<>();
            idempotentPayload.put("doc_id", docId);
            idempotentPayload.put("ok", true);
            idempotentPayload.put("already_existed", true);
            V2EnvelopeWriter.writeSuccess(res, idempotentPayload);
            return;
        } catch (final FavoriteAddFailedException e) {
            logger.warn("/api/v2/documents/{}/favorite POST: add failed", docId, e);
            V2EnvelopeWriter.writeInternalError(res, e, logger, "/api/v2/documents/" + docId + "/favorite POST");
            return;
        } catch (final RuntimeException e) {
            V2EnvelopeWriter.writeInternalError(res, e, logger, "/api/v2/documents/" + docId + "/favorite POST");
            return;
        }

        V2EnvelopeWriter.writeSuccess(res, Map.of("doc_id", docId, "ok", true));
    }
}
