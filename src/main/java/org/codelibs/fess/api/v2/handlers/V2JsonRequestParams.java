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

    private int startPosition = -1;

    private int offset = -1;

    private int pageSize = -1;

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
        return request.getParameter(Constants.TRACK_TOTAL_HITS);
    }

    @Override
    public String getQuery() {
        return request.getParameter("q");
    }

    @Override
    public String[] getExtraQueries() {
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
        return conditions;
    }

    @Override
    public String[] getLanguages() {
        return getParamValueArray(request, "lang");
    }

    @Override
    public GeoInfo getGeoInfo() {
        return createGeoInfo(request);
    }

    @Override
    public FacetInfo getFacetInfo() {
        return createFacetInfo(request);
    }

    @Override
    public String getSort() {
        return request.getParameter("sort");
    }

    @Override
    public int getStartPosition() {
        if (startPosition != -1) {
            return startPosition;
        }

        final String start = request.getParameter("start");
        if (StringUtil.isBlank(start)) {
            startPosition = fessConfig.getPagingSearchPageStartAsInteger();
        } else {
            try {
                startPosition = Integer.parseInt(start);
            } catch (final NumberFormatException e) {
                startPosition = fessConfig.getPagingSearchPageStartAsInteger();
            }
        }
        return startPosition;
    }

    @Override
    public int getOffset() {
        if (offset != -1) {
            return offset;
        }

        final String value = request.getParameter("offset");
        if (StringUtil.isBlank(value)) {
            offset = 0;
        } else {
            try {
                offset = Integer.parseInt(value);
            } catch (final NumberFormatException e) {
                offset = 0;
            }
        }
        return offset;
    }

    @Override
    public int getPageSize() {
        if (pageSize != -1) {
            return pageSize;
        }

        final String num = request.getParameter("num");
        if (StringUtil.isBlank(num)) {
            pageSize = fessConfig.getPagingSearchPageSizeAsInteger();
        } else {
            try {
                pageSize = Integer.parseInt(num);
                if (pageSize > fessConfig.getPagingSearchPageMaxSizeAsInteger().intValue() || pageSize <= 0) {
                    pageSize = fessConfig.getPagingSearchPageMaxSizeAsInteger();
                }
            } catch (final NumberFormatException e) {
                pageSize = fessConfig.getPagingSearchPageSizeAsInteger();
            }
        }
        return pageSize;
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
        return request.getParameter("sdh");
    }

    @Override
    public HighlightInfo getHighlightInfo() {
        return ComponentUtil.getViewHelper().createHighlightInfo();
    }
}
