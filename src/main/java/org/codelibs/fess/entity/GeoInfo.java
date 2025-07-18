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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.core.message.UserMessages;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Entity class that handles geographic information for search queries.
 * This class processes geographic query parameters from HTTP requests and converts them
 * into OpenSearch geo-distance queries for location-based search functionality.
 *
 */
public class GeoInfo {

    /** The OpenSearch query builder for geographic queries. */
    private QueryBuilder builder;

    /**
     * Constructs a GeoInfo instance by parsing geographic parameters from the HTTP request.
     * Extracts geo.{field}.point and geo.{field}.distance parameters to create
     * geo-distance queries for each configured geographic field.
     *
     * @param request the HTTP servlet request containing geographic parameters
     * @throws InvalidQueryException if geo point format is invalid or parsing fails
     */
    public GeoInfo(final HttpServletRequest request) {

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String[] geoFields = fessConfig.getQueryGeoFieldsAsArray();
        final Map<String, List<QueryBuilder>> geoMap = new HashMap<>();

        StreamUtil.stream(request.getParameterMap())
                .of(stream -> stream.filter(e -> e.getKey().startsWith("geo.") && e.getKey().endsWith(".point")).forEach(e -> {
                    final String key = e.getKey();
                    for (final String geoField : geoFields) {
                        if (key.startsWith("geo." + geoField + ".")) {
                            final String distanceKey = key.replaceFirst(".point$", ".distance");
                            final String distance = request.getParameter(distanceKey);
                            if (StringUtil.isNotBlank(distance)) {
                                StreamUtil.stream(e.getValue()).of(s -> s.forEach(pt -> {
                                    List<QueryBuilder> list = geoMap.get(geoField);
                                    if (list == null) {
                                        list = new ArrayList<>();
                                        geoMap.put(geoField, list);
                                    }
                                    final String[] values = pt.split(",");
                                    if (values.length != 2) {
                                        throw new InvalidQueryException(
                                                messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY),
                                                "Invalid geo point: " + pt);
                                    }
                                    try {
                                        final double lat = Double.parseDouble(values[0]);
                                        final double lon = Double.parseDouble(values[1]);
                                        list.add(QueryBuilders.geoDistanceQuery(geoField).distance(distance).point(lat, lon));
                                    } catch (final Exception ex) {
                                        throw new InvalidQueryException(
                                                messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY),
                                                ex.getLocalizedMessage(), ex);
                                    }
                                }));
                            }
                            break;
                        }
                    }
                }));

        final QueryBuilder[] queryBuilders = geoMap.values().stream().map(list -> {
            if (list.size() == 1) {
                return list.get(0);
            }
            if (list.size() > 1) {
                final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                list.forEach(boolQuery::should);
                return boolQuery;
            }
            return null;
        }).filter(q -> q != null).toArray(n -> new QueryBuilder[n]);

        if (queryBuilders.length == 1) {
            builder = queryBuilders[0];
        } else if (queryBuilders.length > 1) {
            final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            StreamUtil.stream(queryBuilders).of(stream -> stream.forEach(boolQuery::must));
            builder = boolQuery;
        }

    }

    /**
     * Returns the OpenSearch QueryBuilder for geographic queries.
     *
     * @return the QueryBuilder containing geo-distance queries, or null if no valid geo parameters were found
     */
    public QueryBuilder toQueryBuilder() {
        return builder;
    }

}
