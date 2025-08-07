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

import static org.codelibs.core.stream.StreamUtil.split;
import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.Map;
import java.util.stream.Collectors;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.helper.RelatedQueryHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;

/**
 * A utility class for building query strings with proper escaping and parameters.
 * This class provides methods to construct search queries from various parameters,
 * handle special characters, and format the final query string for search operations.
 */
public class QueryStringBuilder {

    private static final String OR_ALT = " || ";

    private static final String OR = " OR ";

    private static final String SPACE = " ";

    private SearchRequestParams params;

    private boolean escape = false;

    private String sortField;

    /**
     * Default constructor for QueryStringBuilder.
     * Initializes a new instance with default settings for escape and sortField.
     */
    public QueryStringBuilder() {
        // Default constructor
    }

    /**
     * Quotes a string value if it contains spaces.
     * Multi-word values are wrapped in double quotes with internal quotes replaced by spaces.
     *
     * @param value the string value to quote
     * @return the quoted string if it contains spaces, otherwise the original value
     */
    protected String quote(final String value) {
        if (value.split("\\s").length > 1) {
            return new StringBuilder().append('"').append(value.replace('"', ' ')).append('"').toString();
        }
        return value;
    }

    /**
     * Escapes special characters in a query string if escaping is enabled.
     * Replaces reserved characters with their escaped equivalents based on the Constants.RESERVED array.
     *
     * @param value the query string to escape
     * @return the escaped query string, or the original value if escaping is disabled
     */
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

    /**
     * Builds the complete query string from the configured parameters.
     * Combines base query, extra queries, field filters, and sort field into a single query string.
     *
     * @return the complete formatted query string
     */
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

    /**
     * Appends a query string to the query buffer with proper formatting.
     * Handles OR operators and wraps complex queries in parentheses when necessary.
     *
     * @param queryBuf the StringBuilder to append to
     * @param query the query string to append
     */
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

    /**
     * Builds the base query string from search parameters.
     * Handles both condition-based queries and simple text queries, including related query expansion.
     *
     * @return the base query string
     */
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

    /**
     * Appends various search conditions to the query buffer.
     * Processes advanced search parameters like occurrence, phrases, OR queries, NOT queries,
     * file types, site searches, and timestamp filters.
     *
     * @param queryBuf the StringBuilder to append conditions to
     * @param conditions a map of condition types to their values
     */
    protected void appendConditions(final StringBuilder queryBuf, final Map<String, String[]> conditions) {
        if (conditions == null) {
            return;
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int maxQueryLength = fessConfig.getQueryMaxLengthAsInteger();

        stream(conditions.get(SearchRequestParams.AS_OCCURRENCE))
                .of(stream -> stream.filter(this::isOccurrence).findFirst().ifPresent(q -> queryBuf.insert(0, q + ":")));

        stream(conditions.get(SearchRequestParams.AS_Q))
                .of(stream -> stream.filter(q -> StringUtil.isNotBlank(q) && q.length() <= maxQueryLength)
                        .forEach(q -> queryBuf.append(' ').append(q)));
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

    /**
     * Checks if a value represents an occurrence-based search modifier.
     * Currently supports "allintitle" and "allinurl" modifiers.
     *
     * @param value the value to check
     * @return true if the value is an occurrence modifier, false otherwise
     */
    protected boolean isOccurrence(final String value) {
        return "allintitle".equals(value) || "allinurl".equals(value);
    }

    /**
     * Escapes specific characters in a query string.
     * Replaces each specified character with its escaped version (prefixed with backslash).
     *
     * @param q the query string to escape
     * @param values the characters to escape
     * @return the escaped query string
     */
    protected String escape(final String q, final String... values) {
        String value = q;
        for (final String s : values) {
            value = value.replace(s, "\\" + s);
        }
        return value;
    }

    /**
     * Sets the search request parameters for this builder.
     * This method follows the builder pattern for method chaining.
     *
     * @param params the search request parameters to use
     * @return this QueryStringBuilder instance for method chaining
     */
    public QueryStringBuilder params(final SearchRequestParams params) {
        this.params = params;
        return this;
    }

    /**
     * Sets the sort field for the query.
     * This method follows the builder pattern for method chaining.
     *
     * @param sortField the field name to sort by
     * @return this QueryStringBuilder instance for method chaining
     */
    public QueryStringBuilder sortField(final String sortField) {
        this.sortField = sortField;
        return this;
    }

    /**
     * Sets whether to escape special characters in queries.
     * This method follows the builder pattern for method chaining.
     *
     * @param escape true to enable escaping, false to disable
     * @return this QueryStringBuilder instance for method chaining
     */
    public QueryStringBuilder escape(final boolean escape) {
        this.escape = escape;
        return this;
    }
}
