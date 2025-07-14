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
package org.codelibs.fess.util;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.Constants;
import org.opensearch.search.aggregations.Aggregations;
import org.opensearch.search.aggregations.bucket.filter.Filter;
import org.opensearch.search.aggregations.bucket.terms.Terms;

import com.google.common.io.BaseEncoding;

/**
 * Response object for faceted search results containing query counts and field facets.
 * This class processes OpenSearch aggregations to provide structured facet information
 * for search result filtering and navigation.
 */
public class FacetResponse {
    /**
     * Map containing query facet counts, where keys are decoded query strings
     * and values are document counts for each query.
     */
    protected Map<String, Long> queryCountMap = new LinkedHashMap<>();

    /**
     * List of field facets containing aggregated field values and their counts.
     */
    protected List<Field> fieldList = new ArrayList<>();

    /**
     * Constructs a FacetResponse from OpenSearch aggregations.
     * Processes both field facets and query facets from the aggregation results.
     *
     * @param aggregations the OpenSearch aggregations containing facet data
     */
    public FacetResponse(final Aggregations aggregations) {
        aggregations.forEach(aggregation -> {
            if (aggregation.getName().startsWith(Constants.FACET_FIELD_PREFIX)) {
                final Terms termFacet = (Terms) aggregation;
                fieldList.add(new Field(termFacet));
            } else if (aggregation.getName().startsWith(Constants.FACET_QUERY_PREFIX)) {
                final Filter queryFacet = (Filter) aggregation;
                final String encodedQuery = queryFacet.getName().substring(Constants.FACET_QUERY_PREFIX.length());
                queryCountMap.put(new String(BaseEncoding.base64().decode(encodedQuery), StandardCharsets.UTF_8), queryFacet.getDocCount());
            }

        });
    }

    /**
     * Checks if this response contains any facet information.
     *
     * @return true if either query count map or field list is not null
     */
    public boolean hasFacetResponse() {
        return queryCountMap != null || fieldList != null;
    }

    /**
     * Represents a field facet with its name and value counts.
     * Each field facet contains multiple values with their respective document counts.
     */
    public static class Field {
        /**
         * Map containing field values and their document counts.
         * Keys are field values as strings, values are document counts.
         */
        protected Map<String, Long> valueCountMap = new LinkedHashMap<>();

        /**
         * The decoded name of the field.
         */
        protected String name;

        /**
         * Constructs a Field from OpenSearch Terms aggregation.
         * Decodes the field name and processes all term buckets to extract
         * field values and their document counts.
         *
         * @param termFacet the OpenSearch Terms aggregation containing field facet data
         */
        public Field(final Terms termFacet) {
            final String encodedField = termFacet.getName().substring(Constants.FACET_FIELD_PREFIX.length());
            name = new String(BaseEncoding.base64().decode(encodedField), StandardCharsets.UTF_8);
            for (final Terms.Bucket tfEntry : termFacet.getBuckets()) {
                valueCountMap.put(tfEntry.getKeyAsString(), tfEntry.getDocCount());
            }
        }

        /**
         * Gets the map of field values and their document counts.
         *
         * @return the valueCountMap containing field values and counts
         */
        public Map<String, Long> getValueCountMap() {
            return valueCountMap;
        }

        /**
         * Gets the decoded name of this field facet.
         *
         * @return the field name
         */
        public String getName() {
            return name;
        }

    }

    /**
     * Gets the map of query facet counts.
     *
     * @return the queryCountMap containing decoded query strings and their counts
     */
    public Map<String, Long> getQueryCountMap() {
        return queryCountMap;
    }

    /**
     * Gets the list of field facets.
     *
     * @return the fieldList containing all field facet information
     */
    public List<Field> getFieldList() {
        return fieldList;
    }

    @Override
    public String toString() {
        return "FacetResponse [queryCountMap=" + queryCountMap + ", fieldList=" + fieldList + "]";
    }

}
