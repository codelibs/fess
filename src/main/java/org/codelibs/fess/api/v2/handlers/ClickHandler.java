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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.api.v2.V2EnvelopeWriter;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.helper.SearchLogHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.log.exentity.ClickLog;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles {@code POST /api/v2/click}.
 *
 * <p>Records a click on a search result. The request body is a small JSON
 * document with {@code doc_id}, {@code query_id}, {@code rank} (the 1-based
 * result position) and an optional {@code rt} (epoch millis of the originating
 * search). The handler validates {@code doc_id} against a conservative
 * {@code [A-Za-z0-9_-]+} regex (no dots / slashes — open-redirect and
 * path-traversal mitigation), looks the document up via {@link SearchHelper},
 * then appends a {@link ClickLog} via {@link SearchLogHelper}.</p>
 *
 * <p>Behaviour matches v1's {@code GoAction} click-logging block but without
 * the redirect — v2 clients handle navigation themselves. When the search log
 * feature is disabled the handler succeeds with {@code logged:false} so the
 * client doesn't need to branch on configuration. When the user has no session
 * (anonymous), the handler also returns {@code logged:false} because a click
 * log without a session id is meaningless.</p>
 *
 * <p><strong>Order of checks (m-15):</strong> HTTP method → feature flag →
 * session check (anonymous short-circuits <em>before</em> body parse) → body
 * parse → doc_id validation. This avoids wasting CPU on JSON parsing for
 * anonymous callers or when the search-log feature is disabled.</p>
 */
public class ClickHandler {

    private static final Logger logger = LogManager.getLogger(ClickHandler.class);

    // Click payloads are tiny — 2 KiB is generous enough for any reasonable
    // client and small enough to make payload-bomb attacks pointless.
    private static final int MAX_BODY_BYTES = 2 * 1024;

    // Conservative whitelist — see FavoriteGetHandler for rationale.
    private static final Pattern DOC_ID_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    /**
     * Default constructor. The handler is stateless and intended to be
     * instantiated once by the API manager and shared across concurrent requests.
     */
    public ClickHandler() {
        // no-op
    }

    private static String stringOrNull(final Map<String, Object> body, final String key) {
        final Object v = body.get(key);
        if (v == null) {
            return null;
        }
        if (!(v instanceof String)) {
            return null;
        }
        return (String) v;
    }

    /**
     * Processes one {@code /api/v2/click} request.
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @throws IOException if writing the envelope fails
     */
    public void handle(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            res.setHeader("Allow", "POST");
            V2EnvelopeWriter.writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        final FessConfig cfg = ComponentUtil.getFessConfig();
        if (!cfg.isSearchLog()) {
            // Feature disabled — succeed without doing anything (fire-and-forget contract).
            V2EnvelopeWriter.writeSuccess(res, Map.of("ok", true, "logged", false));
            return;
        }
        // m-15: check session BEFORE parsing the body so anonymous callers short-circuit
        // immediately and malformed-body errors are not reported for anonymous requests.
        // Defensive against missing UserInfoHelper in slim test DI graphs — treat as anonymous.
        String userSessionId = null;
        try {
            userSessionId = ComponentUtil.getUserInfoHelper().getUserCode();
        } catch (final RuntimeException e) {
            // UserInfoHelper unavailable (e.g. unit harness): behave as anonymous.
        }
        if (userSessionId == null) {
            // Anonymous caller: a click log without a session id is meaningless,
            // so report success with logged:false rather than failing the request.
            V2EnvelopeWriter.writeSuccess(res, Map.of("ok", true, "logged", false));
            return;
        }
        final Map<String, Object> body;
        try {
            body = V2JsonBody.read(req, MAX_BODY_BYTES);
        } catch (final V2JsonBody.PayloadTooLargeException | V2JsonBody.MalformedJsonException
                | V2JsonBody.UnsupportedMediaTypeException e) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, e.getMessage());
            return;
        }
        final String docId = stringOrNull(body, "doc_id");
        final String queryId = stringOrNull(body, "query_id");
        if (StringUtil.isBlank(docId) || !DOC_ID_PATTERN.matcher(docId).matches()) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, "invalid doc_id");
            return;
        }
        try {
            final SearchHelper searchHelper = ComponentUtil.getSearchHelper();
            final OptionalEntity<Map<String, Object>> docOpt = searchHelper.getDocumentByDocId(docId,
                    new String[] { cfg.getIndexFieldId(), cfg.getIndexFieldUrl() }, OptionalThing.empty());
            if (!docOpt.isPresent()) {
                V2EnvelopeWriter.writeError(res, V2ErrorCode.NOT_FOUND, "doc not found: " + docId);
                return;
            }
            final Map<String, Object> doc = docOpt.get();
            final String url = DocumentUtil.getValue(doc, cfg.getIndexFieldUrl(), String.class);
            final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
            final ClickLog clickLog = new ClickLog();
            clickLog.setUrlId((String) doc.get(cfg.getIndexFieldId()));
            clickLog.setUrl(url);
            clickLog.setRequestedAt(systemHelper.getCurrentTimeAsLocalDateTime());
            final Object rt = body.get("rt");
            if (rt instanceof Number) {
                // Convert epoch millis -> LocalDateTime via java.time to stay independent of
                // DBFlute's util layer. Same result as DfTypeUtil.toLocalDateTime(long) but
                // expressed with the JDK's standard API.
                clickLog.setQueryRequestedAt(
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(((Number) rt).longValue()), ZoneId.systemDefault()));
            } else {
                clickLog.setQueryRequestedAt(systemHelper.getCurrentTimeAsLocalDateTime());
            }
            clickLog.setUserSessionId(userSessionId);
            clickLog.setDocId(docId);
            clickLog.setQueryId(queryId);
            final Object rank = body.get("rank");
            if (rank instanceof Number) {
                clickLog.setOrder(((Number) rank).intValue());
            }
            ComponentUtil.getSearchLogHelper().addClickLog(clickLog);
            if (logger.isDebugEnabled()) {
                logger.debug("logged click: docId={}, queryId={}, url={}", docId, queryId, url);
            }
            V2EnvelopeWriter.writeSuccess(res, Map.of("ok", true, "logged", true));
        } catch (final Exception e) {
            V2EnvelopeWriter.writeInternalError(res, e, logger, "/api/v2/click");
        }
    }
}
