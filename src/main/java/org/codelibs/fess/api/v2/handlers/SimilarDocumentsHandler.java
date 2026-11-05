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
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.exception.SearchEngineUnavailableException;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.QueryResponseList;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles {@code GET /api/v2/documents/{docId}/similar}.
 *
 * <p>Returns the documents that have the same content signature ({@code content_minhash_bits}) as the
 * given document, that is, the same or nearly the same content, so that a theme can offer a "similar
 * documents" link next to a hit. A document whose content was edited enough to change the signature is
 * not returned. See {@link org.codelibs.fess.helper.SearchHelper#getSimilarDocuments}.</p>
 *
 * <p>Order of checks:</p>
 * <ol>
 *   <li>The HTTP method must be {@code GET}.</li>
 *   <li>{@code docId} must pass {@link DocIdValidator}, and {@code num}, when given, must be positive.</li>
 *   <li>The per-user (per-client-IP for a guest) request limit
 *       {@code api.search.similar.rate.limit.per.minute} must not be exceeded.</li>
 *   <li>The source document must exist and be visible to the caller. A document that is hidden by
 *       the caller's roles is answered exactly like one that does not exist
 *       ({@code 404 not_found}), so the endpoint cannot be used to find out which documents exist.</li>
 * </ol>
 *
 * <p>There is only one page: at most {@value #MAX_SIZE} documents, and never more than
 * {@code paging.search.page.max.size}. The documents carry the same fields as the hits of
 * {@code /api/v2/search}.</p>
 *
 * <p>Whether the caller has to be logged in is decided by the dispatcher (see
 * {@link LoginRequirement}), as for the other search endpoints.</p>
 */
public class SimilarDocumentsHandler {

    private static final Logger logger = LogManager.getLogger(SimilarDocumentsHandler.class);

    /** The number of documents returned when {@code num} is not given. */
    protected static final int DEFAULT_SIZE = 10;

    /** The largest number of documents returned, whatever {@code num} says. */
    protected static final int MAX_SIZE = 20;

    /**
     * Default constructor. The handler is stateless and intended to be
     * instantiated once by the API manager and shared across concurrent requests.
     */
    public SimilarDocumentsHandler() {
        // no-op
    }

    /**
     * Processes one {@code /api/v2/documents/{docId}/similar} request.
     *
     * @param request the incoming HTTP request
     * @param response the HTTP response to write to
     * @param docId the document id extracted from the URL path
     * @throws IOException if writing the envelope fails
     */
    public void handle(final HttpServletRequest request, final HttpServletResponse response, final String docId) throws IOException {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Allow", "GET");
            ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        if (!ComponentUtil.getV2DocIdValidator().isValid(docId)) {
            ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.INVALID_REQUEST, "invalid doc_id");
            return;
        }
        final String contextTag = "/api/v2/documents/" + docId + "/similar";
        try {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final int size = getSize(request, fessConfig);
            if (isRateLimited(request, response, fessConfig, contextTag)) {
                return;
            }
            request.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE, Constants.SEARCH_LOG_ACCESS_TYPE_JSON);
            // The caller, the fields to read and the tags to show are decided as /search does, by its handler.
            final SearchHandler searchHandler = createSearchHandler();
            final OptionalThing<FessUserBean> userBean = searchHandler.getSavedUserBean();
            final String[] responseFields = searchHandler.createRequestParams(request, fessConfig).getResponseFields();
            final SearchHelper searchHelper = ComponentUtil.getSearchHelper();
            final OptionalEntity<QueryResponseList> similar = searchHelper.getSimilarDocuments(docId, size, responseFields, userBean);
            if (!similar.isPresent()) {
                // The same answer whether the document is missing or hidden from the caller.
                ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.NOT_FOUND, "doc not found: " + docId);
                return;
            }
            final QueryResponseList documents = similar.get();
            final SearchRenderData data = new SearchRenderData();
            data.setDocumentItems(documents);
            final String userId = searchHandler.resolveUserId(userBean);
            // The metadata that a search result has and that means something here, under the same names.
            final Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("doc_id", docId);
            payload.put("exec_time", searchHelper.formatExecTime(documents.getExecTime(), Locale.ROOT));
            payload.put("query_time", documents.getQueryTime());
            payload.put("page_size", size);
            payload.put("record_count", documents.getAllRecordCount());
            payload.put("record_count_relation", documents.getAllRecordCountRelation());
            payload.put("partial", documents.isPartialResults());
            payload.put("timed_out", documents.isTimedOut());
            payload.put("shard_failed", documents.isShardFailed());
            payload.put("data", searchHandler.filterDocuments(documents, searchHandler.getVisibleTagTypes(data, userId), userId));
            ComponentUtil.getV2EnvelopeWriter().writeSuccess(response, payload);
        } catch (final InvalidRequestParameterException e) {
            ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.INVALID_REQUEST, e.getMessage());
        } catch (final SearchEngineUnavailableException e) {
            // Already logged where the search engine refused the search; the request may succeed shortly.
            ComponentUtil.getV2EnvelopeWriter().writeSearchEngineUnavailable(response);
        } catch (final Exception e) {
            // An InvalidQueryException is not the caller's doing here: the query is built by Fess.
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(response, e, logger, contextTag);
        }
    }

    /**
     * Reads {@code num}: {@value #DEFAULT_SIZE} when it is missing or not a number, a larger value
     * is cut to {@value #MAX_SIZE} or {@code paging.search.page.max.size}, whichever is smaller.
     *
     * @param request the incoming HTTP request
     * @param fessConfig the Fess configuration
     * @return the number of documents to return, at least 1
     * @throws InvalidRequestParameterException if {@code num} is zero or negative
     */
    protected int getSize(final HttpServletRequest request, final FessConfig fessConfig) {
        final int max = Math.max(1, Math.min(MAX_SIZE, fessConfig.getPagingSearchPageMaxSizeAsInteger()));
        final String num = V2ParamValidator.checkMaxLength(request.getParameter("num"), fessConfig.getApiParamMaxLengthOrDefault(), "num");
        if (StringUtil.isBlank(num)) {
            return Math.min(DEFAULT_SIZE, max);
        }
        final int requested;
        try {
            requested = Integer.parseInt(num);
        } catch (final NumberFormatException e) {
            return Math.min(DEFAULT_SIZE, max);
        }
        if (requested <= 0) {
            throw new InvalidRequestParameterException("num must be positive, got: " + requested);
        }
        return Math.min(requested, max);
    }

    /**
     * Applies the per-minute limit and writes the {@code 429} envelope when it is exceeded.
     * The key is the one the export and chat endpoints throttle by (the logged-in user, else the
     * client IP); the {@link LoginRateLimiter.Scope#SIMILAR} scope keeps the buckets apart.
     *
     * @param request the incoming HTTP request
     * @param response the HTTP response to write the error envelope to
     * @param fessConfig the active Fess configuration
     * @param contextTag the request, for the log of an internal error
     * @return {@code true} when an error envelope was written and the request must stop
     * @throws IOException if the error envelope cannot be written
     */
    private boolean isRateLimited(final HttpServletRequest request, final HttpServletResponse response, final FessConfig fessConfig,
            final String contextTag) throws IOException {
        final int limit = fessConfig.getApiSearchSimilarRateLimitPerMinuteAsInteger();
        if (limit <= 0) {
            return false;
        }
        final LoginRateLimiter limiter;
        try {
            limiter = ComponentUtil.getLoginRateLimiter();
        } catch (final RuntimeException e) {
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(response, e, logger, contextTag);
            return true;
        }
        if (!limiter.allow(LoginRateLimiter.Scope.SIMILAR, getRateLimitKey(request), limit, 60)) {
            response.setHeader("Retry-After", "60");
            ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.RATE_LIMITED, "too many similar document requests");
            return true;
        }
        return false;
    }

    /**
     * Resolves the rate-limit key: the authenticated user, else the proxy-aware client IP. Exposed as
     * a seam so unit tests can pin the key (same rationale as {@code ExportSearchHandler#getRateLimitKey}).
     *
     * @param req the incoming HTTP request
     * @return the rate-limit key (never null/blank)
     */
    protected String getRateLimitKey(final HttpServletRequest req) {
        final String username = ComponentUtil.getSystemHelper().getUsername();
        return ComponentUtil.getChatApiHelper()
                .resolveChatRateLimitKey(username, () -> ComponentUtil.getRateLimitHelper().getClientIp(req));
    }

    /**
     * Creates the handler of {@code /api/v2/search}, whose parts are reused here so that a document
     * looks the same in both. A seam so tests supply a user without the login subsystem.
     *
     * @return the search handler
     */
    protected SearchHandler createSearchHandler() {
        return new SearchHandler();
    }
}
