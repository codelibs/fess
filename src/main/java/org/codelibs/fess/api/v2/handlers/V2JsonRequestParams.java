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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Search request parameters for the {@code /api/v2} surface.
 *
 * <p>Mirrors v1's {@code SearchApiManager.JsonRequestParams} inner class so the
 * underlying {@link org.codelibs.fess.helper.SearchHelper#search} contract stays
 * stable across versions. Promoted to a top-level class so individual v2
 * handlers (search, scroll, …) can construct it without depending on the
 * monolithic manager. The wire-level parameter names match v1 exactly
 * ({@code q}, {@code start}, {@code num}, {@code offset}, {@code ex_q},
 * {@code fields.*}, {@code as.*}, {@code lang}, {@code sort}, {@code sdh}) so
 * existing clients keep working.</p>
 */
public class V2JsonRequestParams extends SearchRequestParams {

    private final HttpServletRequest request;

    private final FessConfig fessConfig;

    private int startPosition;

    private boolean startPositionInitialized;

    private int offset;

    private boolean offsetInitialized;

    private int pageSize;

    private boolean pageSizeInitialized;

    /**
     * Constructs a v2 request-params adapter over an incoming HTTP request.
     *
     * @param request the incoming HTTP request whose parameters are read on demand
     * @param fessConfig the Fess configuration used to source default paging values
     */
    public V2JsonRequestParams(final HttpServletRequest request, final FessConfig fessConfig) {
        this.request = request;
        this.fessConfig = fessConfig;
    }

    @Override
    public String getTrackTotalHits() {
        return V2ParamValidator.checkMaxLength(request.getParameter(Constants.TRACK_TOTAL_HITS), 100, "track_total_hits");
    }

    @Override
    public String getQuery() {
        return V2ParamValidator.checkMaxLength(request.getParameter("q"), fessConfig.getApiV2ParamMaxLengthAsInteger(), "q");
    }

    @Override
    public String[] getExtraQueries() {
        V2ParamValidator.checkArray(request.getParameterValues("ex_q"), fessConfig.getApiV2ParamMaxArraySizeAsInteger(),
                fessConfig.getApiV2ParamMaxLengthAsInteger(), "ex_q");
        return getParamValueArray(request, "ex_q");
    }

    @Override
    public Map<String, String[]> getFields() {
        final Map<String, String[]> fields = new HashMap<>();
        for (final Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            final String key = entry.getKey();
            if (key.startsWith("fields.")) {
                final String[] value = simplifyArray(entry.getValue());
                fields.put(key.substring("fields.".length()), value);
            }
        }
        if (fields.size() > fessConfig.getApiV2ParamMaxArraySizeAsInteger()) {
            throw new InvalidRequestParameterException(
                    "fields exceeds the maximum number of distinct names: " + fessConfig.getApiV2ParamMaxArraySizeAsInteger());
        }
        return fields;
    }

    @Override
    public Map<String, String[]> getConditions() {
        final Map<String, String[]> conditions = new HashMap<>();
        for (final Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            final String key = entry.getKey();
            if (key.startsWith("as.")) {
                final String[] value = simplifyArray(entry.getValue());
                conditions.put(key.substring("as.".length()), value);
            }
        }
        if (conditions.size() > fessConfig.getApiV2ParamMaxArraySizeAsInteger()) {
            throw new InvalidRequestParameterException(
                    "conditions exceeds the maximum number of distinct names: " + fessConfig.getApiV2ParamMaxArraySizeAsInteger());
        }
        return conditions;
    }

    @Override
    public String[] getLanguages() {
        V2ParamValidator.checkArray(request.getParameterValues("lang"), fessConfig.getApiV2ParamMaxArraySizeAsInteger(),
                fessConfig.getApiV2ParamMaxLengthAsInteger(), "lang");
        return getParamValueArray(request, "lang");
    }

    @Override
    public GeoInfo getGeoInfo() {
        return createGeoInfo(request);
    }

    @Override
    public FacetInfo getFacetInfo() {
        final int maxItems = fessConfig.getApiV2ParamMaxArraySizeAsInteger();
        final int maxLen = fessConfig.getApiV2ParamMaxLengthAsInteger();
        V2ParamValidator.checkArray(request.getParameterValues("facet.field"), maxItems, maxLen, "facet.field");
        V2ParamValidator.checkArray(request.getParameterValues("facet.query"), maxItems, maxLen, "facet.query");
        return createFacetInfo(request);
    }

    @Override
    public String getSort() {
        return V2ParamValidator.checkMaxLength(request.getParameter("sort"), fessConfig.getApiV2ParamMaxLengthAsInteger(), "sort");
    }

    /**
     * Returns the {@code start} parameter from the request, defaulting to the configured
     * {@code paging.search.page.start} when missing or malformed.
     *
     * <p>The parsed value is cached after the first call so concurrent paging fields can
     * read it without re-parsing. A negative {@code start} is rejected with
     * {@link InvalidOffsetException} — historically the {@code -1} sentinel meant
     * "uninitialised", which collided with a client legitimately requesting {@code -1};
     * the sentinel is now an explicit {@link #startPositionInitialized} flag.</p>
     *
     * @return the validated, non-negative start position
     * @throws InvalidOffsetException if {@code start} is present and {@code < 0}
     */
    @Override
    public int getStartPosition() {
        if (startPositionInitialized) {
            return startPosition;
        }

        final String start = request.getParameter("start");
        if (StringUtil.isBlank(start)) {
            startPosition = fessConfig.getPagingSearchPageStartAsInteger();
        } else {
            try {
                final int parsed = Integer.parseInt(start);
                if (parsed < 0) {
                    // Reject negative start rather than passing -1 to the search backend.
                    // The handler should map InvalidOffsetException to INVALID_REQUEST.
                    throw new InvalidOffsetException("start must be non-negative, got: " + parsed);
                }
                startPosition = parsed;
            } catch (final NumberFormatException e) {
                startPosition = fessConfig.getPagingSearchPageStartAsInteger();
            }
        }
        startPositionInitialized = true;
        return startPosition;
    }

    /**
     * Returns the {@code offset} parameter from the request, defaulting to {@code 0}
     * when missing or non-numeric.
     *
     * <p>The parsed value is cached after the first call. A negative {@code offset} is
     * rejected with {@link InvalidOffsetException} — historically the {@code -1}
     * sentinel meant "uninitialised", which collided with a client legitimately
     * passing {@code -1}. Caching is now driven by an explicit
     * {@link #offsetInitialized} flag.</p>
     *
     * @return the validated, non-negative offset
     * @throws InvalidOffsetException if {@code offset} is present and {@code < 0}
     */
    @Override
    public int getOffset() {
        if (offsetInitialized) {
            return offset;
        }

        final String value = request.getParameter("offset");
        if (StringUtil.isBlank(value)) {
            offset = 0;
        } else {
            try {
                final int parsed = Integer.parseInt(value);
                if (parsed < 0) {
                    // Reject negative offset rather than passing -1 to the search backend.
                    throw new InvalidOffsetException("offset must be non-negative, got: " + parsed);
                }
                offset = parsed;
            } catch (final NumberFormatException e) {
                offset = 0;
            }
        }
        offsetInitialized = true;
        return offset;
    }

    /**
     * Returns the requested page size, validated and bounded by the Fess configuration.
     *
     * <p>Contract:</p>
     * <ul>
     *   <li>Missing or blank {@code num}: returns the configured default page size.</li>
     *   <li>Non-numeric {@code num}: returns the configured default page size.</li>
     *   <li>{@code num <= 0}: throws {@link InvalidPageSizeException} — zero and negative
     *       values are not meaningful and likely indicate a client bug; rejecting them early
     *       prevents silent amplification to the configured maximum.</li>
     *   <li>{@code num > max}: clamps silently to the configured maximum. Clients can
     *       detect clamping by comparing the request {@code num} with the
     *       {@code page_size} field returned in the response envelope.</li>
     *   <li>{@code 1 <= num <= max}: returned unchanged.</li>
     * </ul>
     *
     * @return validated page size in the range {@code [1, configuredMax]}
     * @throws InvalidPageSizeException if {@code num} is present and {@code <= 0}
     */
    @Override
    public int getPageSize() {
        if (pageSizeInitialized) {
            return pageSize;
        }

        final String num = request.getParameter("num");
        if (StringUtil.isBlank(num)) {
            pageSize = fessConfig.getPagingSearchPageSizeAsInteger();
        } else {
            try {
                final int requested = Integer.parseInt(num);
                if (requested <= 0) {
                    // Zero and negative values are invalid — reject so the caller can return
                    // INVALID_REQUEST rather than silently serving the maximum page.
                    throw new InvalidPageSizeException("num must be positive, got: " + requested);
                }
                final int max = fessConfig.getPagingSearchPageMaxSizeAsInteger().intValue();
                if (requested > max) {
                    pageSize = max;
                } else {
                    pageSize = requested;
                }
            } catch (final NumberFormatException e) {
                pageSize = fessConfig.getPagingSearchPageSizeAsInteger();
            }
        }
        pageSizeInitialized = true;
        return pageSize;
    }

    /**
     * Thrown by {@link #getPageSize()} when the {@code num} parameter is present but
     * {@code <= 0}. Handlers should catch this and map it to
     * {@link org.codelibs.fess.api.v2.V2ErrorCode#INVALID_REQUEST}.
     */
    public static class InvalidPageSizeException extends InvalidRequestParameterException {
        private static final long serialVersionUID = 1L;

        /**
         * Creates the exception with a diagnostic message.
         *
         * @param message human-readable description of the invalid page size
         */
        public InvalidPageSizeException(final String message) {
            super(message);
        }
    }

    /**
     * Thrown by {@link #getOffset()} or {@link #getStartPosition()} when the
     * {@code offset} / {@code start} parameter is present but negative. Negative paging
     * positions are not meaningful and previously collided with the {@code -1}
     * "uninitialised" sentinel — handlers should catch this and map it to
     * {@link org.codelibs.fess.api.v2.V2ErrorCode#INVALID_REQUEST}.
     *
     * <p>Extends {@link InvalidPageSizeException} so existing handler catch blocks
     * that already map invalid paging input to {@code INVALID_REQUEST} also cover
     * the offset variant without per-handler edits. Test code that asserts
     * specifically on {@link InvalidOffsetException} keeps working because
     * {@code instanceof InvalidOffsetException} narrows beyond the parent type.</p>
     */
    public static class InvalidOffsetException extends InvalidPageSizeException {
        private static final long serialVersionUID = 1L;

        /**
         * Creates the exception with a diagnostic message.
         *
         * @param message human-readable description of the invalid offset
         */
        public InvalidOffsetException(final String message) {
            super(message);
        }
    }

    @Override
    public Object getAttribute(final String name) {
        return request.getAttribute(name);
    }

    @Override
    public Locale getLocale() {
        return Locale.ROOT;
    }

    @Override
    public SearchRequestType getType() {
        return SearchRequestType.JSON;
    }

    @Override
    public String getSimilarDocHash() {
        return V2ParamValidator.checkMaxLength(request.getParameter("sdh"), fessConfig.getApiV2ParamMaxLengthAsInteger(), "sdh");
    }

    @Override
    public HighlightInfo getHighlightInfo() {
        return ComponentUtil.getViewHelper().createHighlightInfo();
    }
}
