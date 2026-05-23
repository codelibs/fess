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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.v2.V2EnvelopeWriter;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.exception.ResultOffsetExceededException;
import org.codelibs.fess.helper.RelatedContentHelper;
import org.codelibs.fess.helper.RelatedQueryHelper;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.query.QueryFieldConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.FacetResponse;
import org.codelibs.fess.util.FacetResponse.Field;
import org.dbflute.optional.OptionalThing;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles the {@code /api/v2/search} endpoint.
 *
 * <p>Mirrors v1's {@code SearchApiManager#processSearchRequest} (the original
 * 180-line method) but emits the v2 envelope through {@link V2EnvelopeWriter}
 * instead of hand-rolling JSON via {@code StringBuilder}. Wire-level field names
 * are preserved (snake_case) so existing v1 clients can upgrade with minimal
 * changes; the only structural difference is the outer envelope shape.</p>
 *
 * <p>This class is intentionally stateless so the manager can hold a single
 * instance and dispatch concurrent requests through it without locking.</p>
 */
public class SearchHandler {

    private static final Logger logger = LogManager.getLogger(SearchHandler.class);

    /**
     * Processes one {@code /api/v2/search} request.
     *
     * <p>Rejects non-{@code GET} methods with {@link V2ErrorCode#INVALID_REQUEST}.
     * {@link InvalidQueryException} and {@link ResultOffsetExceededException}
     * surface as {@code invalid_request} (400) so client SDKs can distinguish
     * user errors from {@code internal_error} (500) — matching v1's split
     * between {@code SC_BAD_REQUEST} and {@code SC_INTERNAL_SERVER_ERROR}.</p>
     *
     * @param request the incoming HTTP request
     * @param response the HTTP response to write to
     * @throws IOException if writing the envelope fails
     */
    public void handle(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            V2EnvelopeWriter.writeError(response, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        request.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE, Constants.SEARCH_LOG_ACCESS_TYPE_JSON);
        try {
            // Resolve helpers inside the try so DI failures (e.g. searchHelper not registered)
            // surface as a structured envelope rather than an uncaught ComponentNotFound.
            final SearchHelper searchHelper = ComponentUtil.getSearchHelper();
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final SearchRenderData data = new SearchRenderData();
            final V2JsonRequestParams params = new V2JsonRequestParams(request, fessConfig);
            searchHelper.search(params, data, OptionalThing.empty());
            V2EnvelopeWriter.writeSuccess(response, buildPayload(params.getQuery(), data));
        } catch (final InvalidQueryException | ResultOffsetExceededException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("invalid /api/v2/search request", e);
            }
            V2EnvelopeWriter.writeError(response, V2ErrorCode.INVALID_REQUEST, e.getMessage());
        } catch (final Exception e) {
            V2EnvelopeWriter.writeInternalError(response, e, logger, "/api/v2/search");
        }
    }

    /**
     * Builds the snake_case payload merged into the v2 envelope.
     *
     * <p>Keys (and order) intentionally mirror the v1 wire shape so client SDKs
     * just see a new outer envelope, not a new search response. Numeric fields
     * stay as numbers (no string conversion), {@code record_count} is preserved
     * as {@code long}, {@code page_numbers} is forwarded as a list of strings,
     * and document field filtering goes through {@link QueryFieldConfig#isApiResponseField}
     * to keep internal fields off the wire.</p>
     *
     * @param query the original {@code q} parameter (may be {@code null})
     * @param data the populated render data returned by the search helper
     * @return the ordered payload map ready for envelope serialization
     */
    private Map<String, Object> buildPayload(final String query, final SearchRenderData data) {
        final RelatedQueryHelper relatedQueryHelper = ComponentUtil.getRelatedQueryHelper();
        final RelatedContentHelper relatedContentHelper = ComponentUtil.getRelatedContentHelper();

        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("q", query);
        payload.put("query_id", data.getQueryId());
        payload.put("exec_time", data.getExecTime());
        payload.put("query_time", data.getQueryTime());
        payload.put("page_size", data.getPageSize());
        payload.put("page_number", data.getCurrentPageNumber());
        payload.put("record_count", data.getAllRecordCount());
        payload.put("record_count_relation", data.getAllRecordCountRelation());
        payload.put("page_count", data.getAllPageCount());
        payload.put("highlight_params", data.getAppendHighlightParams());
        payload.put("next_page", data.isExistNextPage());
        payload.put("prev_page", data.isExistPrevPage());
        payload.put("start_record_number", data.getCurrentStartRecordNumber());
        payload.put("end_record_number", data.getCurrentEndRecordNumber());
        payload.put("page_numbers", data.getPageNumberList());
        payload.put("partial", data.isPartialResults());
        payload.put("search_query", data.getSearchQuery());
        payload.put("requested_time", data.getRequestedTime());
        payload.put("related_query", relatedQueryHelper.getRelatedQueries(query));
        payload.put("related_contents", relatedContentHelper.getRelatedContents(query));
        payload.put("data", filterDocuments(data.getDocumentItems()));

        final FacetResponse facetResponse = data.getFacetResponse();
        if (facetResponse != null && facetResponse.hasFacetResponse()) {
            payload.put("facet_field", buildFacetField(facetResponse));
            payload.put("facet_query", buildFacetQuery(facetResponse));
        }
        return payload;
    }

    /**
     * Filters each document down to fields that {@link QueryFieldConfig#isApiResponseField}
     * marks as safe for API exposure.
     *
     * <p>Drops blank keys and {@code null} values — matching v1's behavior so the
     * wire payload size stays stable across versions.</p>
     *
     * @param docs the raw document items from {@link SearchRenderData}
     * @return a new list of filtered, order-preserved document maps
     */
    private List<Map<String, Object>> filterDocuments(final List<Map<String, Object>> docs) {
        if (docs == null || docs.isEmpty()) {
            return new ArrayList<>(0);
        }
        final QueryFieldConfig cfg = ComponentUtil.getQueryFieldConfig();
        final List<Map<String, Object>> out = new ArrayList<>(docs.size());
        for (final Map<String, Object> doc : docs) {
            final Map<String, Object> filtered = new LinkedHashMap<>();
            for (final Map.Entry<String, Object> e : doc.entrySet()) {
                final String name = e.getKey();
                if (StringUtil.isNotBlank(name) && e.getValue() != null && cfg.isApiResponseField(name)) {
                    filtered.put(name, e.getValue());
                }
            }
            out.add(filtered);
        }
        return out;
    }

    /**
     * Builds the {@code facet_field} array — one entry per facet field with the
     * field name and the value/count pairs nested inside a {@code result} array.
     *
     * @param facetResponse the populated facet response (never {@code null})
     * @return a list of {@code {name, result:[{value, count}]}} maps
     */
    private List<Map<String, Object>> buildFacetField(final FacetResponse facetResponse) {
        final List<Field> fields = facetResponse.getFieldList();
        if (fields == null || fields.isEmpty()) {
            return new ArrayList<>(0);
        }
        final List<Map<String, Object>> out = new ArrayList<>(fields.size());
        for (final Field field : fields) {
            final Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("name", field.getName());
            final List<Map<String, Object>> results = new ArrayList<>();
            for (final Map.Entry<String, Long> vc : field.getValueCountMap().entrySet()) {
                final Map<String, Object> result = new LinkedHashMap<>();
                result.put("value", vc.getKey());
                result.put("count", vc.getValue());
                results.add(result);
            }
            entry.put("result", results);
            out.add(entry);
        }
        return out;
    }

    /**
     * Builds the {@code facet_query} array — a flat list of value/count pairs
     * for each configured facet query.
     *
     * @param facetResponse the populated facet response (never {@code null})
     * @return a list of {@code {value, count}} maps
     */
    private List<Map<String, Object>> buildFacetQuery(final FacetResponse facetResponse) {
        final Map<String, Long> qc = facetResponse.getQueryCountMap();
        if (qc == null || qc.isEmpty()) {
            return new ArrayList<>(0);
        }
        final List<Map<String, Object>> out = new ArrayList<>(qc.size());
        for (final Map.Entry<String, Long> entry : qc.entrySet()) {
            final Map<String, Object> e = new LinkedHashMap<>();
            e.put("value", entry.getKey());
            e.put("count", entry.getValue());
            out.add(e);
        }
        return out;
    }
}
