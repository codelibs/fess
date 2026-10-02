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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.v2.V2EnvelopeWriter;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.exception.ResultOffsetExceededException;
import org.codelibs.fess.helper.LabelTypeHelper.LabelTypeItem;
import org.codelibs.fess.helper.RelatedContentHelper;
import org.codelibs.fess.helper.RelatedQueryHelper;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.query.QueryFieldConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.FacetResponse;
import org.codelibs.fess.util.FacetResponse.Field;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.core.message.UserMessages;

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
     * Default constructor. The handler is stateless and intended to be
     * instantiated once by the API manager and shared across concurrent requests.
     */
    public SearchHandler() {
        // no-op
    }

    /**
     * Processes one {@code /api/v2/search} request.
     *
     * <p>Rejects non-{@code GET} methods with {@link V2ErrorCode#METHOD_NOT_ALLOWED}.
     * {@link InvalidQueryException} and {@link ResultOffsetExceededException}
     * surface as {@code invalid_request} (400) so client SDKs can distinguish
     * user errors from {@code internal_error} (500) — matching v1's split
     * between {@code SC_BAD_REQUEST} and {@code SC_INTERNAL_SERVER_ERROR}.</p>
     *
     * <p><strong>Referer allowlist (MJ-21):</strong> v1's {@code SearchApiManager}
     * enforced {@code isAcceptedSearchReferer} (~line 262/860) as a browser-driven
     * scraping defence. v2 omits this check deliberately: state-changing endpoints
     * are protected by the CSRF token enforced by {@link CsrfRequirement},
     * and idempotent GETs carry no side-effects that require Referer gating. If the
     * deployment needs Referer-based rate-limiting, add it in a filter or gateway
     * layer rather than in the handler.</p>
     *
     * @param request the incoming HTTP request
     * @param response the HTTP response to write to
     * @throws IOException if writing the envelope fails
     */
    public void handle(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Allow", "GET");
            ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
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
            params.enableRedirect();
            searchHelper.search(params, data, OptionalThing.empty());
            // A search request rewriter can send the search elsewhere (e.g. a bang to another
            // search engine); the client is told where instead of being given results.
            if (data.getRedirectUrl() != null) {
                final Map<String, Object> payload = new LinkedHashMap<>();
                payload.put("q", params.getQuery());
                payload.put("redirect_url", data.getRedirectUrl());
                ComponentUtil.getV2EnvelopeWriter().writeSuccess(response, payload);
                return;
            }
            final OptionalThing<FessUserBean> userBean = getSavedUserBean();
            final Map<String, Object> payload =
                    buildPayload(params.getQuery(), data, getTagItemMap(request), userBean.map(FessUserBean::getUserId).orElse(null));
            // Evaluated per search: a user whose group and role permissions are still loading or
            // failed sees fewer results.
            payload.put("permission_state",
                    ComponentUtil.getV2UserPayloads().permissionState(userBean.isPresent() ? userBean.get() : null));
            ComponentUtil.getV2EnvelopeWriter().writeSuccess(response, payload);
        } catch (final InvalidRequestParameterException e) {
            ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.INVALID_REQUEST, e.getMessage());
        } catch (final InvalidQueryException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("invalid /api/v2/search request", e);
            }
            ComponentUtil.getV2EnvelopeWriter()
                    .writeUserMessageError(response, V2ErrorCode.INVALID_REQUEST, request.getLocale(), e.getMessageCode());
        } catch (final ResultOffsetExceededException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("invalid /api/v2/search request", e);
            }
            ComponentUtil.getV2EnvelopeWriter()
                    .writeUserMessageError(response, V2ErrorCode.INVALID_REQUEST, request.getLocale(),
                            messages -> messages.addErrorsResultSizeExceeded(UserMessages.GLOBAL_PROPERTY_KEY));
        } catch (final Exception e) {
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(response, e, logger, "/api/v2/search");
        }
    }

    /**
     * Resolves the logged-in user. A seam so tests supply a user without the login subsystem;
     * a failed lookup counts as a guest.
     *
     * @return the saved user bean, or empty for a guest
     */
    protected OptionalThing<FessUserBean> getSavedUserBean() {
        try {
            return ComponentUtil.getFessLoginAssist().getSavedUserBean();
        } catch (final Exception e) {
            logger.debug("login subsystem lookup failed; treating the caller as a guest", e);
            return OptionalThing.empty();
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
     * @param tagItemMap the tags that the caller can see, by value
     * @param userId the logged-in user, or null
     * @return the ordered payload map ready for envelope serialization
     */
    private Map<String, Object> buildPayload(final String query, final SearchRenderData data, final Map<String, LabelTypeItem> tagItemMap,
            final String userId) {
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
        payload.put("timed_out", data.isTimedOut());
        payload.put("shard_failed", data.isShardFailed());
        payload.put("search_query", data.getSearchQuery());
        payload.put("requested_time", data.getRequestedTime());
        payload.put("related_query", relatedQueryHelper.getRelatedQueries(query));
        payload.put("related_contents", relatedContentHelper.getRelatedContents(query));
        payload.put("data", filterDocuments(data.getDocumentItems(), tagItemMap, userId));

        final FacetResponse facetResponse = data.getFacetResponse();
        if (facetResponse != null && facetResponse.hasFacetResponse()) {
            payload.put("facet_field", buildFacetField(facetResponse, tagItemMap));
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
     * <p>The {@code tag} field is not an API response field: the tags of the label types that the caller
     * can see are returned as {@code tags}, a list of {@code {value, name, mine}}.</p>
     *
     * @param docs the raw document items from {@link SearchRenderData}
     * @param tagItemMap the tags that the caller can see, by value
     * @param userId the logged-in user, or null
     * @return a new list of filtered, order-preserved document maps
     */
    List<Map<String, Object>> filterDocuments(final List<Map<String, Object>> docs, final Map<String, LabelTypeItem> tagItemMap,
            final String userId) {
        if (docs == null || docs.isEmpty()) {
            return new ArrayList<>(0);
        }
        final QueryFieldConfig cfg = ComponentUtil.getQueryFieldConfig();
        // never returned as is, even when query.additional.api.response.fields lists it
        final String tagField = ComponentUtil.getFessConfig().getIndexFieldTag();
        final List<Map<String, Object>> out = new ArrayList<>(docs.size());
        for (final Map<String, Object> doc : docs) {
            final Map<String, Object> filtered = new LinkedHashMap<>();
            for (final Map.Entry<String, Object> e : doc.entrySet()) {
                final String name = e.getKey();
                if (StringUtil.isNotBlank(name) && e.getValue() != null && cfg.isApiResponseField(name) && !tagField.equals(name)) {
                    filtered.put(name, e.getValue());
                }
            }
            if (!tagItemMap.isEmpty()) {
                final List<Map<String, Object>> tags = new ArrayList<>();
                for (final String value : toStringList(doc.get(tagField))) {
                    final LabelTypeItem item = tagItemMap.get(value);
                    if (item != null) {
                        final Map<String, Object> tag = new LinkedHashMap<>();
                        tag.put("value", value);
                        tag.put("name", item.getLabel());
                        tag.put("mine", ComponentUtil.getTagHelper().isMine(item, userId));
                        tags.add(tag);
                    }
                }
                if (!tags.isEmpty()) {
                    filtered.put("tags", tags);
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
     * <p>Package-private to allow shape assertions in {@code SearchHandlerTest}
     * (Phase B dependency: the SPA facet renderer must not diverge from this contract).</p>
     *
     * @param facetResponse the populated facet response (never {@code null})
     * @return a list of {@code {name, result:[{value, count}]}} maps
     */
    List<Map<String, Object>> buildFacetField(final FacetResponse facetResponse) {
        return buildFacetField(facetResponse, Collections.emptyMap());
    }

    /**
     * Builds the {@code facet_field} array. The values of the {@code tag} facet are limited to the tags that the
     * caller can see, so a tag is not revealed by its count, and each carries the tag name as {@code label}.
     *
     * @param facetResponse the populated facet response (never {@code null})
     * @param tagItemMap the tags that the caller can see, by value
     * @return a list of {@code {name, result:[{value, count}]}} maps
     */
    List<Map<String, Object>> buildFacetField(final FacetResponse facetResponse, final Map<String, LabelTypeItem> tagItemMap) {
        final List<Field> fields = facetResponse.getFieldList();
        if (fields == null || fields.isEmpty()) {
            return new ArrayList<>(0);
        }
        final List<Map<String, Object>> out = new ArrayList<>(fields.size());
        for (final Field field : fields) {
            final Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("name", field.getName());
            final List<Map<String, Object>> results = new ArrayList<>();
            final boolean tagField = ComponentUtil.getFessConfig().getIndexFieldTag().equals(field.getName());
            for (final Map.Entry<String, Long> vc : field.getValueCountMap().entrySet()) {
                final LabelTypeItem tagItem = tagField ? tagItemMap.get(vc.getKey()) : null;
                if (tagField && tagItem == null) {
                    continue;
                }
                final Map<String, Object> result = new LinkedHashMap<>();
                result.put("value", vc.getKey());
                result.put("count", vc.getValue());
                if (tagItem != null) {
                    result.put("label", tagItem.getLabel());
                }
                results.add(result);
            }
            entry.put("result", results);
            out.add(entry);
        }
        return out;
    }

    /**
     * Returns the tags that the caller can see by value, or an empty map while user tags are disabled.
     * Exposed as a seam for unit tests.
     *
     * @param request the request
     * @return the visible tags by value
     */
    protected Map<String, LabelTypeItem> getTagItemMap(final HttpServletRequest request) {
        try {
            if (!ComponentUtil.getFessConfig().isUserTagEnabled()) {
                return Collections.emptyMap();
            }
            final Locale locale = request.getLocale() == null ? Locale.ROOT : request.getLocale();
            final Map<String, LabelTypeItem> map = new LinkedHashMap<>();
            ComponentUtil.getLabelTypeHelper()
                    .getTagItemList(SearchRequestType.JSON, locale)
                    .forEach(item -> map.put(item.getValue(), item));
            return map;
        } catch (final Exception e) {
            logger.debug("Failed to resolve the tags; no tags are returned.", e);
            return Collections.emptyMap();
        }
    }

    private static List<String> toStringList(final Object value) {
        final List<String> list = new ArrayList<>();
        if (value instanceof final Collection<?> collection) {
            collection.forEach(v -> list.add(String.valueOf(v)));
        } else if (value instanceof final Object[] array) {
            for (final Object v : array) {
                list.add(String.valueOf(v));
            }
        } else if (value != null) {
            list.add(value.toString());
        }
        return list;
    }

    /**
     * Builds the {@code facet_query} array — a flat list of value/count pairs
     * for each configured facet query.
     *
     * <p>Package-private to allow shape assertions in {@code SearchHandlerTest}
     * (Phase B dependency: the SPA facet renderer must not diverge from this contract).</p>
     *
     * @param facetResponse the populated facet response (never {@code null})
     * @return a list of {@code {value, count}} maps
     */
    List<Map<String, Object>> buildFacetQuery(final FacetResponse facetResponse) {
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
