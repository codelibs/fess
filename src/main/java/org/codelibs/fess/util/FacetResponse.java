/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

public class FacetResponse {
    protected Map<String, Long> queryCountMap = new LinkedHashMap<>();

    protected List<Field> fieldList = new ArrayList<>();

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

    public boolean hasFacetResponse() {
        return queryCountMap != null || fieldList != null;
    }

    public static class Field {
        protected Map<String, Long> valueCountMap = new LinkedHashMap<>();

        protected String name;

        public Field(final Terms termFacet) {
            final String encodedField = termFacet.getName().substring(Constants.FACET_FIELD_PREFIX.length());
            name = new String(BaseEncoding.base64().decode(encodedField), StandardCharsets.UTF_8);
            for (final Terms.Bucket tfEntry : termFacet.getBuckets()) {
                valueCountMap.put(tfEntry.getKeyAsString(), tfEntry.getDocCount());
            }
        }

        /**
         * @return the valueCountMap
         */
        public Map<String, Long> getValueCountMap() {
            return valueCountMap;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

    }

    /**
     * @return the queryCountMap
     */
    public Map<String, Long> getQueryCountMap() {
        return queryCountMap;
    }

    /**
     * @return the fieldList
     */
    public List<Field> getFieldList() {
        return fieldList;
    }

    @Override
    public String toString() {
        return "FacetResponse [queryCountMap=" + queryCountMap + ", fieldList=" + fieldList + "]";
    }

}
