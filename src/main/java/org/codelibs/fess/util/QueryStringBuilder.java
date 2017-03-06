/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;

public class QueryStringBuilder {
    private String query;

    private String[] extraQueries;

    private Map<String, String[]> fieldMap;

    public String build() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final StringBuilder queryBuf = new StringBuilder(255);
        if (StringUtil.isNotBlank(query)) {
            queryBuf.append('(').append(query).append(')');
        }
        stream(extraQueries).of(
                stream -> stream.filter(q -> StringUtil.isNotBlank(q) && q.length() <= fessConfig.getQueryMaxLengthAsInteger().intValue())
                        .forEach(q -> queryBuf.append(' ').append(q)));
        stream(fieldMap).of(stream -> stream.forEach(entry -> {
            final String key = entry.getKey();
            final String[] values = entry.getValue();
            if (values == null) {
                // nothing
            } else if (values.length == 1) {
                queryBuf.append(' ').append(key).append(":\"").append(values[0]).append('\"');
            } else if (values.length > 1) {
                boolean first = true;
                queryBuf.append(" (");
                for (final String value : values) {
                    if (first) {
                        first = false;
                    } else {
                        queryBuf.append(" OR ");
                    }
                    queryBuf.append(key).append(":\"").append(value).append('\"');
                }
                queryBuf.append(')');
            }
        }));
        return queryBuf.toString();
    }

    public static QueryStringBuilder query(final String query) {
        final QueryStringBuilder builder = new QueryStringBuilder();
        builder.query = query;
        return builder;
    }

    public QueryStringBuilder extraQueries(final String[] extraQueries) {
        this.extraQueries = extraQueries;
        return this;
    }

    public QueryStringBuilder fields(final Map<String, String[]> fieldMap) {
        this.fieldMap = fieldMap;
        return this;
    }
}
