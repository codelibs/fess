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
package org.codelibs.fess.entity;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.Locale;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The search request parameters.
 */
public abstract class SearchRequestParams {

    /**
     * Default constructor.
     */
    protected SearchRequestParams() {
        // Default constructor
    }

    /** The parameter for negative query. */
    public static final String AS_NQ = "nq";

    /** The parameter for OR query. */
    public static final String AS_OQ = "oq";

    /** The parameter for exact phrase query. */
    public static final String AS_EPQ = "epq";

    /** The parameter for query. */
    public static final String AS_Q = "q";

    /** The parameter for filetype. */
    public static final String AS_FILETYPE = "filetype";

    /** The parameter for sitesearch. */
    public static final String AS_SITESEARCH = "sitesearch";

    /** The parameter for occurrence. */
    public static final String AS_OCCURRENCE = "occt";

    /** The parameter for timestamp. */
    public static final String AS_TIMESTAMP = "timestamp";

    /**
     * Returns the query.
     *
     * @return The query.
     */
    public abstract String getQuery();

    /**
     * Returns the fields.
     *
     * @return The fields.
     */
    public abstract Map<String, String[]> getFields();

    /**
     * Returns the conditions.
     *
     * @return The conditions.
     */
    public abstract Map<String, String[]> getConditions();

    /**
     * Returns the languages.
     *
     * @return The languages.
     */
    public abstract String[] getLanguages();

    /**
     * Returns the geo info.
     *
     * @return The geo info.
     */
    public abstract GeoInfo getGeoInfo();

    /**
     * Returns the facet info.
     *
     * @return The facet info.
     */
    public abstract FacetInfo getFacetInfo();

    /**
     * Returns the highlight info.
     *
     * @return The highlight info.
     */
    public abstract HighlightInfo getHighlightInfo();

    /**
     * Returns the sort.
     *
     * @return The sort.
     */
    public abstract String getSort();

    /**
     * Returns the start position.
     *
     * @return The start position.
     */
    public abstract int getStartPosition();

    /**
     * Returns the page size.
     *
     * @return The page size.
     */
    public abstract int getPageSize();

    /**
     * Returns the offset.
     *
     * @return The offset.
     */
    public abstract int getOffset();

    /**
     * Returns the extra queries.
     *
     * @return The extra queries.
     */
    public abstract String[] getExtraQueries();

    /**
     * Returns the attribute.
     *
     * @param name The name of the attribute.
     * @return The attribute.
     */
    public abstract Object getAttribute(String name);

    /**
     * Returns the locale.
     *
     * @return The locale.
     */
    public abstract Locale getLocale();

    /**
     * Returns the search request type.
     *
     * @return The search request type.
     */
    public abstract SearchRequestType getType();

    /**
     * Returns the similar document hash.
     *
     * @return The similar document hash.
     */
    public abstract String getSimilarDocHash();

    /**
     * Returns the track total hits.
     *
     * @return The track total hits.
     */
    public String getTrackTotalHits() {
        return null;
    }

    /**
     * Returns the min score.
     *
     * @return The min score.
     */
    public Float getMinScore() {
        return null;
    }

    /**
     * Returns true if the request has a condition query, otherwise false.
     *
     * @return True if the request has a condition query, otherwise false.
     */
    public boolean hasConditionQuery() {
        final Map<String, String[]> conditions = getConditions();
        return !isEmptyArray(conditions.get(AS_Q))//
                || !isEmptyArray(conditions.get(AS_EPQ))//
                || !isEmptyArray(conditions.get(AS_OQ))//
                || !isEmptyArray(conditions.get(AS_NQ))//
                || !isEmptyArray(conditions.get(AS_TIMESTAMP))//
                || !isEmptyArray(conditions.get(AS_SITESEARCH))//
                || !isEmptyArray(conditions.get(AS_FILETYPE));
    }

    /**
     * Returns true if the array is empty, otherwise false.
     *
     * @param values The array.
     * @return True if the array is empty, otherwise false.
     */
    protected boolean isEmptyArray(final String[] values) {
        if (values == null || values.length == 0) {
            return true;
        }
        return stream(values).get(stream -> stream.allMatch(StringUtil::isBlank));
    }

    /**
     * Simplifies the array.
     *
     * @param values The array.
     * @return The simplified array.
     */
    protected static String[] simplifyArray(final String[] values) {
        return stream(values).get(stream -> stream.filter(StringUtil::isNotBlank).distinct().toArray(n -> new String[n]));
    }

    /**
     * Returns the parameter value array.
     *
     * @param request The request.
     * @param param The parameter.
     * @return The parameter value array.
     */
    public static String[] getParamValueArray(final HttpServletRequest request, final String param) {
        return simplifyArray(request.getParameterValues(param));
    }

    /**
     * Creates a facet info.
     *
     * @param request The request.
     * @return The facet info.
     */
    protected FacetInfo createFacetInfo(final HttpServletRequest request) {
        final String[] fields = getParamValueArray(request, "facet.field");
        final String[] queries = getParamValueArray(request, "facet.query");
        if (fields.length == 0 && queries.length == 0) {
            return null;
        }
        final FacetInfo facetInfo = new FacetInfo();
        facetInfo.field = fields;
        facetInfo.query = queries;
        final String sizeStr = request.getParameter("facet.size");
        if (StringUtil.isNotBlank(sizeStr)) {
            facetInfo.size = Integer.parseInt(sizeStr);
        }
        final String minDocCountStr = request.getParameter("facet.minDocCount");
        if (StringUtil.isNotBlank(minDocCountStr)) {
            facetInfo.minDocCount = Long.parseLong(minDocCountStr);
        }
        final String sort = request.getParameter("facet.sort");
        if (StringUtil.isNotBlank(sort)) {
            facetInfo.sort = sort;
        }
        final String missing = request.getParameter("facet.missing");
        if (StringUtil.isNotBlank(missing)) {
            facetInfo.missing = missing;
        }
        return facetInfo;
    }

    /**
     * Creates a geo info.
     *
     * @param request The request.
     * @return The geo info.
     */
    protected GeoInfo createGeoInfo(final HttpServletRequest request) {
        return new GeoInfo(request);
    }

    /**
     * The search request type.
     */
    public enum SearchRequestType {
        /** Search request type. */
        SEARCH,
        /** Admin search request type. */
        ADMIN_SEARCH,
        /** JSON request type. */
        JSON,
        /** GSA request type. */
        GSA,
        /** Suggest request type. */
        SUGGEST;
    }

    /**
     * Returns the response fields.
     *
     * @return The response fields.
     */
    public String[] getResponseFields() {
        return ComponentUtil.getQueryFieldConfig().getResponseFields();
    }
}
