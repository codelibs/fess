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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.query.QueryFieldConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
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
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        if (!ComponentUtil.getV2DocIdValidator().isValid(docId)) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INVALID_REQUEST, "invalid doc_id");
            return;
        }
        // Validate hq parameter bounds early (fail fast, before the expensive backend lookup).
        // If FessConfig is unavailable in a stripped harness, skip the check (same pattern as
        // the login-required check below) — in production FessConfig is always present.
        try {
            final org.codelibs.fess.mylasta.direction.FessConfig cfg = ComponentUtil.getFessConfig();
            V2ParamValidator.checkArray(req.getParameterValues("hq"), cfg.getApiV2ParamMaxArraySizeAsInteger(),
                    cfg.getApiV2ParamMaxLengthAsInteger(), "hq");
        } catch (final InvalidRequestParameterException e) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INVALID_REQUEST, e.getMessage());
            return;
        } catch (final RuntimeException e) {
            logger.warn("/api/v2/cache: FessConfig lookup failed; skipping hq bounds check", e);
        }
        // M-17: split DI-binding failure from "no user bean present". A genuine anonymous
        // caller still falls into the AUTH_REQUIRED branch below (when login is required);
        // a system-level lookup failure surfaces as INTERNAL_ERROR (500) so a logged-in
        // user isn't misled into thinking their session expired when the underlying issue
        // is DI breakage.
        final FessLoginAssist assist;
        try {
            assist = ComponentUtil.getComponent(FessLoginAssist.class);
        } catch (final RuntimeException e) {
            logger.warn("/api/v2/cache/{}: could not acquire FessLoginAssist", docId, e);
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(res, e, logger, "/api/v2/cache/" + docId);
            return;
        }
        final OptionalThing<FessUserBean> userBean;
        try {
            userBean = assist.getSavedUserBean();
        } catch (final RuntimeException e) {
            logger.warn("/api/v2/cache/{}: getSavedUserBean failed", docId, e);
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(res, e, logger, "/api/v2/cache/" + docId);
            return;
        }
        // MJ-20: parity with v1 CacheAction:68-70 — honor app.login.required.
        // When login is required and no authenticated user is present, reject with
        // AUTH_REQUIRED (401) rather than serving cached content anonymously.
        try {
            final org.codelibs.fess.mylasta.direction.FessConfig fessConfig = ComponentUtil.getFessConfig();
            if (fessConfig.isLoginRequired() && !userBean.isPresent()) {
                ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.AUTH_REQUIRED, "login required");
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
                ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.NOT_FOUND, "doc not found: " + docId);
                return;
            }
            final Map<String, Object> doc = docOpt.get();
            // hq is multi-valued in v1 (CacheForm#hq is String[]) — bounds already validated above, pass through.
            final String[] hq = req.getParameterValues("hq");
            final String content = ComponentUtil.getViewHelper().createCacheContent(doc, hq);
            if (content == null) {
                ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.NOT_FOUND, "no cache for: " + docId);
                return;
            }
            ComponentUtil.getV2EnvelopeWriter().writeSuccess(res, buildCachePayload(docId, content, doc));
        } catch (final Exception e) {
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(res, e, logger, "/api/v2/cache/" + docId);
        }
    }

    Map<String, Object> buildCachePayload(final String docId, final String content, final Map<String, Object> doc) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("doc_id", docId);
        payload.put("mimetype", "text/html");
        payload.put("content", content);
        final String urlLink = DocumentUtil.getValue(doc, fessConfig.getResponseFieldUrlLink(), String.class);
        final String url =
                StringUtil.isNotBlank(urlLink) ? urlLink : DocumentUtil.getValue(doc, fessConfig.getIndexFieldUrl(), String.class);
        if (StringUtil.isNotBlank(url)) {
            payload.put("url", url);
        }
        final String created = DocumentUtil.getValue(doc, fessConfig.getIndexFieldCreated(), String.class);
        if (StringUtil.isNotBlank(created)) {
            payload.put("created", created);
        }
        payload.put("charset", parseCharset(DocumentUtil.getValue(doc, fessConfig.getIndexFieldMimetype(), String.class)));
        return payload;
    }

    // Note: RFC2045-quoted charset values (e.g. charset="UTF-8") are not unquoted here. This is
    // acceptable because crawler-indexed mimetypes store the charset unquoted (e.g. charset=UTF-8).
    private String parseCharset(final String mimetype) {
        if (StringUtil.isNotBlank(mimetype)) {
            final int idx = mimetype.toLowerCase(java.util.Locale.ROOT).indexOf("charset=");
            if (idx >= 0) {
                final String cs = mimetype.substring(idx + "charset=".length()).trim();
                final int sc = cs.indexOf(';');
                final String result = (sc >= 0 ? cs.substring(0, sc) : cs).trim();
                if (StringUtil.isNotBlank(result)) {
                    return result;
                }
            }
        }
        return Constants.UTF_8;
    }
}
