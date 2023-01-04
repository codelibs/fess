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
package org.codelibs.fess.entity;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.search.aggregations.BucketOrder;

public class FacetInfo {
    private static final Logger logger = LogManager.getLogger(FacetInfo.class);

    public String[] field;

    public String[] query;

    public Integer size;

    public Long minDocCount;

    public String sort;

    public String missing;

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

    @Override
    public String toString() {
        return "FacetInfo [field=" + Arrays.toString(field) + ", query=" + Arrays.toString(query) + ", size=" + size + ", minDocCount="
                + minDocCount + ", sort=" + sort + ", missing=" + missing + "]";
    }
}
