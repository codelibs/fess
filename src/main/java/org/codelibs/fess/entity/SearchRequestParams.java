/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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

import javax.servlet.http.HttpServletRequest;

import org.codelibs.core.lang.StringUtil;

public interface SearchRequestParams {

    String AS_NQ = "nq";

    String AS_OQ = "oq";

    String AS_EPQ = "epq";

    String AS_Q = "q";

    String AS_FILETYPE = "filetype";

    String AS_SITESEARCH = "sitesearch";

    String AS_OCCURRENCE = "occt";

    String AS_TIMESTAMP = "timestamp";

    String getQuery();

    Map<String, String[]> getFields();

    Map<String, String[]> getConditions();

    String[] getLanguages();

    GeoInfo getGeoInfo();

    FacetInfo getFacetInfo();

    String getSort();

    int getStartPosition();

    int getPageSize();

    String[] getExtraQueries();

    Object getAttribute(String name);

    Locale getLocale();

    SearchRequestType getType();

    String getSimilarDocHash();

    public default boolean hasConditionQuery() {
        final Map<String, String[]> conditions = getConditions();
        return !isEmptyArray(conditions.get(AS_Q))//
                || !isEmptyArray(conditions.get(AS_EPQ))//
                || !isEmptyArray(conditions.get(AS_OQ))//
                || !isEmptyArray(conditions.get(AS_NQ))//
                || !isEmptyArray(conditions.get(AS_TIMESTAMP))//
                || !isEmptyArray(conditions.get(AS_SITESEARCH))//
                || !isEmptyArray(conditions.get(AS_FILETYPE));
    }

    public default boolean isEmptyArray(String[] values) {
        if (values == null || values.length == 0) {
            return true;
        }
        return stream(values).get(stream -> stream.allMatch(StringUtil::isBlank));
    }

    public default String[] simplifyArray(final String[] values) {
        return stream(values).get(stream -> stream.filter(StringUtil::isNotBlank).distinct().toArray(n -> new String[n]));
    }

    public default String[] getParamValueArray(final HttpServletRequest request, final String param) {
        return simplifyArray(request.getParameterValues(param));
    }

    public default FacetInfo createFacetInfo(final HttpServletRequest request) {
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

    public default GeoInfo createGeoInfo(final HttpServletRequest request) {
        return new GeoInfo(request);
    }

    public enum SearchRequestType {
        SEARCH, ADMIN_SEARCH, JSON, GSA, SUGGEST;
    }
}
