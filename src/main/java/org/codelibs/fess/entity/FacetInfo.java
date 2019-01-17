/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import org.codelibs.core.lang.StringUtil;
import org.elasticsearch.search.aggregations.BucketOrder;

public class FacetInfo {
    public String[] field;

    public String[] query;

    public Integer size;

    public Long minDocCount;

    public String sort;

    public String missing;

    public BucketOrder getBucketOrder() {
        if (StringUtil.isNotBlank(sort)) {
            final String[] values = sort.split("\\.");
            final boolean asc;
            if (values.length > 1) {
                asc = !values[1].equalsIgnoreCase("desc");
            } else {
                asc = true;
            }
            if (values.length > 0) {
                if ("term".equals(values[0]) || "key".equals(values[0])) {
                    return BucketOrder.key(asc);
                } else if ("count".equals(values[0])) {
                    return BucketOrder.count(asc);
                }
            }
        }
        return BucketOrder.count(false);
    }

    @Override
    public String toString() {
        return "FacetInfo [field=" + Arrays.toString(field) + ", query=" + Arrays.toString(query) + ", size=" + size + ", minDocCount="
                + minDocCount + ", sort=" + sort + ", missing=" + missing + "]";
    }

}
