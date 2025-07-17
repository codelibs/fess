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

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.search.aggregations.BucketOrder;

import jakarta.annotation.PostConstruct;

/**
 * Entity class representing facet configuration information for search results.
 * This class holds configuration settings for faceted search including field facets,
 * query facets, and various parameters that control facet behavior.
 */
public class FacetInfo {
    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(FacetInfo.class);

    /** Array of field names to create facets for */
    public String[] field;

    /** Array of query strings to create query facets for */
    public String[] query;

    /** Maximum number of facet values to return */
    public Integer size;

    /** Minimum document count required for a facet value to be included */
    public Long minDocCount;

    /** Sort order for facet values (e.g., "count.desc", "term.asc") */
    public String sort;

    /** Value to use for documents that don't have the facet field */
    public String missing;

    /**
     * Default constructor for FacetInfo.
     */
    public FacetInfo() {
        // Default constructor
    }

    /**
     * Initializes the facet configuration from Fess configuration properties.
     * This method is called after dependency injection to load default facet settings.
     */
    @PostConstruct
    public void init() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (StringUtil.isNotBlank(fessConfig.getQueryFacetFields())) {
            field = StreamUtil.split(fessConfig.getQueryFacetFields(), ",")
                    .get(stream -> stream.map(String::trim).filter(StringUtil::isNotEmpty).distinct().toArray(n -> new String[n]));
        }
        if (StringUtil.isNotBlank(fessConfig.getQueryFacetFieldsSize())) {
            size = fessConfig.getQueryFacetFieldsSizeAsInteger();
        }
        if (StringUtil.isNotBlank(fessConfig.getQueryFacetFieldsMinDocCount())) {
            minDocCount = Long.parseLong(fessConfig.getQueryFacetFieldsMinDocCount());
        }
        if (StringUtil.isNotBlank(fessConfig.getQueryFacetFieldsSort())) {
            sort = fessConfig.getQueryFacetFieldsSort();
        }
        if (StringUtil.isNotBlank(fessConfig.getQueryFacetFieldsMissing())) {
            missing = fessConfig.getQueryFacetFieldsMissing();
        }
    }

    /**
     * Converts the sort string into a BucketOrder object for OpenSearch aggregations.
     * Parses sort configuration like "count.desc" or "term.asc" into appropriate bucket ordering.
     *
     * @return the BucketOrder instance representing the sort configuration
     */
    public BucketOrder getBucketOrder() {
        if (StringUtil.isNotBlank(sort)) {
            final String[] values = sort.split("\\.");
            final boolean asc;
            if (values.length > 1) {
                asc = !"desc".equalsIgnoreCase(values[1]);
            } else {
                asc = true;
            }
            if (values.length > 0) {
                if ("term".equals(values[0]) || "key".equals(values[0])) {
                    return BucketOrder.key(asc);
                }
                if ("count".equals(values[0])) {
                    return BucketOrder.count(asc);
                }
            }
        }
        return BucketOrder.count(false);
    }

    /**
     * Adds a query facet to the existing query array.
     * If no queries exist, creates a new array with the provided query.
     *
     * @param s the query string to add as a facet
     */
    public void addQuery(final String s) {
        if (query == null) {
            query = new String[] { s };
        } else {
            final String[] newQuery = Arrays.copyOf(query, query.length + 1);
            newQuery[query.length] = s;
            query = newQuery;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("loaded facet query: {}", s);
        }
    }

    /**
     * Returns a string representation of this FacetInfo object.
     * Includes all field values in the format useful for debugging.
     *
     * @return string representation of this FacetInfo instance
     */
    @Override
    public String toString() {
        return "FacetInfo [field=" + Arrays.toString(field) + ", query=" + Arrays.toString(query) + ", size=" + size + ", minDocCount="
                + minDocCount + ", sort=" + sort + ", missing=" + missing + "]";
    }
}
