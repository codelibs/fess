/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package jp.sf.fess.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;

public class FacetResponse {
    protected Map<String, Long> queryCountMap;

    protected List<Field> fieldList;

    public FacetResponse(final List<FacetField> facetFields,
            final Map<String, Integer> facetQueryMap) {
        if (facetFields != null && !facetFields.isEmpty()) {
            fieldList = new ArrayList<FacetResponse.Field>();
            for (final FacetField facetField : facetFields) {
                if (facetField.getValues() != null) {
                    fieldList.add(new Field(facetField));
                }
            }
        }
        if (facetQueryMap != null && !facetQueryMap.isEmpty()) {
            queryCountMap = new LinkedHashMap<String, Long>();
            for (final Map.Entry<String, Integer> entry : facetQueryMap
                    .entrySet()) {
                queryCountMap.put(entry.getKey(), entry.getValue().longValue());
            }
        }
    }

    public boolean hasFacetResponse() {
        return queryCountMap != null || fieldList != null;
    }

    public static class Field {
        protected Map<String, Long> valueCountMap;

        protected String name;

        public Field(final FacetField facetField) {
            name = facetField.getName();
            valueCountMap = new LinkedHashMap<String, Long>();
            for (final Count count : facetField.getValues()) {
                valueCountMap.put(count.getName(), count.getCount());
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

}
