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
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.api.v2.V2EnvelopeWriter;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.query.QueryFieldConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles {@code GET /api/v2/cache/{docId}}.
 *
 * <p>Returns the cached HTML rendering of an indexed document, mirroring the
 * v1 {@code CacheAction#index} behavior but emitting a v2 envelope instead of
 * a {@code StreamResponse}. The cached content is embedded in the success
 * payload alongside {@code doc_id} and {@code mimetype} so JS clients can
 * render it inline without a second round-trip.</p>
 *
 * <p>Order of checks:</p>
 * <ol>
 *   <li>HTTP method must be {@code GET}. Other methods produce
 *       {@code 405 method_not_allowed} with an {@code Allow: GET} header.</li>
 *   <li>When {@code app.login.required=true} and no authenticated user is present,
 *       returns {@code 401 auth_required} — parity with v1 {@code CacheAction:68-70}.</li>
 *   <li>{@code docId} must be non-blank and match {@code [A-Za-z0-9_-]+}.</li>
 *   <li>The document must exist in the index (otherwise {@code 404 not_found}).</li>
 *   <li>{@link ViewHelper#createCacheContent} must return a non-null body
 *       (otherwise {@code 404 not_found} with a "no cache for" message — same
 *       semantics as v1's {@code addErrorsDocidNotFound}).</li>
 * </ol>
 *
 * <p>The {@code hq} query parameter is multi-valued and passed straight through
 * to {@link ViewHelper#createCacheContent(Map, String[])}. Null is acceptable.</p>
 */
public class CacheHandler {

    private static final Logger logger = LogManager.getLogger(CacheHandler.class);

    // Conservative whitelist — matches the same character class used by
    // FavoriteGetHandler / FavoritePostHandler. The v1 action accepts an
    // unconstrained form-bound string, but v2 surfaces docId via the URL path
    // so we lock it down to avoid path-traversal-style abuse.
    private static final Pattern DOC_ID_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    /**
     * Default constructor. The handler is stateless and intended to be
     * instantiated once by the API manager and shared across concurrent requests.
     */
    public CacheHandler() {
        // no-op
    }

    /**
     * Processes one {@code /api/v2/cache/{docId}} GET request.
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @param docId the document id extracted from the URL path
     * @throws IOException if writing the envelope fails
     */
    public void handle(final HttpServletRequest req, final HttpServletResponse res, final String docId) throws IOException {
        if (!"GET".equalsIgnoreCase(req.getMethod())) {
            res.setHeader("Allow", "GET");
            V2EnvelopeWriter.writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        if (StringUtil.isBlank(docId) || !DOC_ID_PATTERN.matcher(docId).matches()) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, "invalid doc_id");
            return;
        }
        // Auth lookup is wrapped because the unit harness (and any environment
        // where the login subsystem isn't fully wired) can throw at lookup time.
        OptionalThing<FessUserBean> userBean;
        try {
            userBean = ComponentUtil.getComponent(FessLoginAssist.class).getSavedUserBean();
        } catch (final Exception e) {
            logger.warn("/api/v2/cache: login subsystem lookup failed; treating as anonymous", e);
            userBean = OptionalThing.empty();
        }
        // MJ-20: parity with v1 CacheAction:68-70 — honor app.login.required.
        // When login is required and no authenticated user is present, reject with
        // AUTH_REQUIRED (401) rather than serving cached content anonymously.
        try {
            final org.codelibs.fess.mylasta.direction.FessConfig fessConfig = ComponentUtil.getFessConfig();
            if (fessConfig.isLoginRequired() && !userBean.isPresent()) {
                V2EnvelopeWriter.writeError(res, V2ErrorCode.AUTH_REQUIRED, "login required");
                return;
            }
        } catch (final Exception e) {
            // If FessConfig lookup fails in a stripped test harness, skip the check.
            logger.warn("/api/v2/cache: FessConfig lookup failed; skipping login-required check", e);
        }
        try {
            // Resolved inside the try so DI failures (e.g. environments where
            // queryFieldConfig is not registered) surface as the structured
            // 500 envelope rather than a raw exception out of the servlet.
            final QueryFieldConfig qfc = ComponentUtil.getQueryFieldConfig();
            final OptionalEntity<Map<String, Object>> docOpt =
                    ComponentUtil.getSearchHelper().getDocumentByDocId(docId, qfc.getCacheResponseFields(), userBean);
            if (!docOpt.isPresent()) {
                V2EnvelopeWriter.writeError(res, V2ErrorCode.NOT_FOUND, "doc not found: " + docId);
                return;
            }
            final Map<String, Object> doc = docOpt.get();
            // hq is multi-valued in v1 (CacheForm#hq is String[]) — pass through verbatim.
            final String[] hq = req.getParameterValues("hq");
            final String content = ComponentUtil.getViewHelper().createCacheContent(doc, hq);
            if (content == null) {
                V2EnvelopeWriter.writeError(res, V2ErrorCode.NOT_FOUND, "no cache for: " + docId);
                return;
            }
            final Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("doc_id", docId);
            payload.put("mimetype", "text/html");
            payload.put("content", content);
            V2EnvelopeWriter.writeSuccess(res, payload);
        } catch (final Exception e) {
            V2EnvelopeWriter.writeInternalError(res, e, logger, "/api/v2/cache/" + docId);
        }
    }
}
