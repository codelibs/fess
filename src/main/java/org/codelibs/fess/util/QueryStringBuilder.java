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

import static org.codelibs.core.stream.StreamUtil.split;
import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.Map;
import java.util.stream.Collectors;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.helper.RelatedQueryHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;

public class QueryStringBuilder {

    private static final String OR_ALT = " || ";

    private static final String OR = " OR ";

    private static final String SPACE = " ";

    private SearchRequestParams params;

    private boolean escape = false;

    private String sortField;

    protected String quote(final String value) {
        if (value.split("\\s").length > 1) {
            return new StringBuilder().append('"').append(value.replace('"', ' ')).append('"').toString();
        }
        return value;
    }

    protected String escapeQuery(final String value) {
        if (!escape) {
            return value;
        }

        String newValue = value;
        for (final String element : Constants.RESERVED) {
            final String replacement = element.replaceAll("(.)", "\\\\$1");
            newValue = newValue.replace(element, replacement);
        }
        return newValue;
    }

    public String build() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int maxQueryLength = fessConfig.getQueryMaxLengthAsInteger();
        final StringBuilder queryBuf = new StringBuilder(255);

        final String query = buildBaseQuery();
        if (StringUtil.isNotBlank(query)) {
            queryBuf.append(escapeQuery(query));
        }

        stream(params.getExtraQueries())
                .of(stream -> stream.filter(q -> StringUtil.isNotBlank(q) && q.length() <= maxQueryLength).forEach(q -> {
                    appendQuery(queryBuf, q);
                }));

        stream(params.getFields()).of(stream -> stream.forEach(entry -> {
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
                        queryBuf.append(OR);
                    }
                    queryBuf.append(key).append(":\"").append(value).append('\"');
                }
                queryBuf.append(')');
            }
        }));

        final String baseQuery = queryBuf.toString().trim();
        if (StringUtil.isBlank(sortField)) {
            return baseQuery;
        }
        return baseQuery + " sort:" + sortField;
    }

    protected void appendQuery(final StringBuilder queryBuf, final String query) {
        String q = query;
        for (final String s : ComponentUtil.getFessConfig().getCrawlerDocumentSpaces()) {
            q = q.replace(s, SPACE);
        }
        final boolean exists = q.indexOf(OR) != -1 || q.indexOf(OR_ALT) != -1;
        queryBuf.append(' ');
        if (exists) {
            queryBuf.append('(');
        }
        queryBuf.append(query);
        if (exists) {
            queryBuf.append(')');
        }
    }

    protected String buildBaseQuery() {
        final StringBuilder queryBuf = new StringBuilder(255);
        if (params.hasConditionQuery()) {
            appendConditions(queryBuf, params.getConditions());
        } else {
            final String query = params.getQuery();
            if (StringUtil.isNotBlank(query)) {
                if (ComponentUtil.hasRelatedQueryHelper()) {
                    final RelatedQueryHelper relatedQueryHelper = ComponentUtil.getRelatedQueryHelper();
                    final String[] relatedQueries = relatedQueryHelper.getRelatedQueries(query);
                    if (relatedQueries.length == 0) {
                        appendQuery(queryBuf, query);
                    } else {
                        queryBuf.append('(');
                        queryBuf.append(quote(query));
                        for (final String s : relatedQueries) {
                            queryBuf.append(OR);
                            queryBuf.append(quote(s));
                        }
                        queryBuf.append(')');
                    }
                } else {
                    appendQuery(queryBuf, query);
                }
            }
        }
        return queryBuf.toString().trim();
    }

    protected void appendConditions(final StringBuilder queryBuf, final Map<String, String[]> conditions) {
        if (conditions == null) {
            return;
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int maxQueryLength = fessConfig.getQueryMaxLengthAsInteger();

        stream(conditions.get(SearchRequestParams.AS_OCCURRENCE))
                .of(stream -> stream.filter(this::isOccurrence).findFirst().ifPresent(q -> queryBuf.insert(0, q + ":")));

        stream(conditions.get(SearchRequestParams.AS_Q)).of(stream -> stream
                .filter(q -> StringUtil.isNotBlank(q) && q.length() <= maxQueryLength).forEach(q -> queryBuf.append(' ').append(q)));
        stream(conditions.get(SearchRequestParams.AS_EPQ))
                .of(stream -> stream.filter(q -> StringUtil.isNotBlank(q) && q.length() <= maxQueryLength)
                        .forEach(q -> queryBuf.append(" \"").append(escape(q, "\"")).append('"')));
        stream(conditions.get(SearchRequestParams.AS_OQ)).of(stream -> stream
                .filter(q -> StringUtil.isNotBlank(q) && q.length() <= maxQueryLength)
                .forEach(oq -> split(oq, " ")
                        .get(s -> s.filter(StringUtil::isNotBlank).reduce((q1, q2) -> escape(q1, "(", ")") + OR + escape(q2, "(", ")")))
                        .ifPresent(q -> {
                            appendQuery(queryBuf, q);
                        })));
        stream(conditions.get(SearchRequestParams.AS_NQ))
                .of(stream -> stream.filter(q -> StringUtil.isNotBlank(q) && q.length() <= maxQueryLength).forEach(eq -> {
                    final String nq =
                            split(eq, " ").get(s -> s.filter(StringUtil::isNotBlank).map(q -> "NOT " + q).collect(Collectors.joining(" ")));
                    queryBuf.append(' ').append(nq);
                }));
        stream(conditions.get(SearchRequestParams.AS_FILETYPE))
                .of(stream -> stream.filter(q -> StringUtil.isNotBlank(q) && q.length() <= maxQueryLength)
                        .forEach(q -> queryBuf.append(" filetype:\"").append(q.trim()).append('"')));
        stream(conditions.get(SearchRequestParams.AS_SITESEARCH))
                .of(stream -> stream.filter(q -> StringUtil.isNotBlank(q) && q.length() <= maxQueryLength)
                        .forEach(q -> queryBuf.append(" site:").append(q.trim())));
        stream(conditions.get(SearchRequestParams.AS_TIMESTAMP))
                .of(stream -> stream.filter(q -> StringUtil.isNotBlank(q) && q.length() <= maxQueryLength)
                        .forEach(q -> queryBuf.append(" timestamp:").append(q.trim())));
    }

    protected boolean isOccurrence(final String value) {
        return "allintitle".equals(value) || "allinurl".equals(value);
    }

    protected String escape(final String q, final String... values) {
        String value = q;
        for (final String s : values) {
            value = value.replace(s, "\\" + s);
        }
        return value;
    }

    public QueryStringBuilder params(final SearchRequestParams params) {
        this.params = params;
        return this;
    }

    public QueryStringBuilder sortField(final String sortField) {
        this.sortField = sortField;
        return this;
    }

    public QueryStringBuilder escape(final boolean escape) {
        this.escape = escape;
        return this;
    }
}
