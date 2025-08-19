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

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.util.LaRequestUtil;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.MatchAllQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder;
import org.opensearch.search.sort.SortBuilder;

/**
 * Context object that holds query-related information and state during search processing.
 * Contains the query string, query builder, sort criteria, and various metadata.
 */
public class QueryContext {

    /** Prefix for queries that search only in URL fields. */
    protected static final String ALLINURL_FIELD_PREFIX = "allinurl:";

    /** Prefix for queries that search only in title fields. */
    protected static final String ALLINTITLE_FIELD_PREFIX = "allintitle:";

    /** The OpenSearch query builder used for executing the search. */
    protected QueryBuilder queryBuilder;

    /** List of sort builders to apply to the search query. */
    protected final List<SortBuilder<?>> sortBuilderList = new ArrayList<>();

    /** The original query string provided by the user. */
    protected String queryString;

    /** Set of query terms that should be highlighted in search results. */
    protected Set<String> highlightedQuerySet = null;

    /** Map storing field names and their associated query terms for logging. */
    protected Map<String, List<String>> fieldLogMap = null;

    /** Flag indicating whether role-based query filtering should be disabled. */
    protected boolean disableRoleQuery = false;

    /** The default field to search in when no specific field is specified. */
    protected String defaultField = null;

    /**
     * Constructs a new QueryContext with the specified query string.
     * Processes special query prefixes (allinurl:, allintitle:) and initializes
     * request-scoped attributes for highlighting and field logging.
     * @param queryString The query string to process.
     * @param isQuery Whether this is a search query (enables highlighting and logging).
     */
    @SuppressWarnings("unchecked")
    public QueryContext(final String queryString, final boolean isQuery) {
        if (queryString != null) {
            if (queryString.startsWith(ALLINURL_FIELD_PREFIX)) {
                defaultField = ComponentUtil.getFessConfig().getIndexFieldUrl();
                this.queryString = queryString.substring(ALLINURL_FIELD_PREFIX.length());
            } else if (queryString.startsWith(ALLINTITLE_FIELD_PREFIX)) {
                defaultField = ComponentUtil.getFessConfig().getIndexFieldTitle();
                this.queryString = queryString.substring(ALLINTITLE_FIELD_PREFIX.length());
            } else {
                this.queryString = queryString;
            }
        } else {
            this.queryString = queryString;
        }
        if (StringUtil.isBlank(this.queryString)) {
            this.queryString = "*";
        }
        if (isQuery) {
            LaRequestUtil.getOptionalRequest().ifPresent(request -> {
                highlightedQuerySet = new HashSet<>();
                request.setAttribute(Constants.HIGHLIGHT_QUERIES, highlightedQuerySet);
                fieldLogMap = (Map<String, List<String>>) request.getAttribute(Constants.FIELD_LOGS);
                if (fieldLogMap == null) {
                    fieldLogMap = new HashMap<>();
                    request.setAttribute(Constants.FIELD_LOGS, fieldLogMap);
                }
            });
        }
    }

    /**
     * Adds function score configuration to the query builder.
     * @param functionScoreQuery Consumer that configures the function score filters.
     */
    public void addFunctionScore(final Consumer<List<FilterFunctionBuilder>> functionScoreQuery) {
        final List<FilterFunctionBuilder> list = new ArrayList<>();
        functionScoreQuery.accept(list);
        queryBuilder = QueryBuilders.functionScoreQuery(queryBuilder, list.toArray(new FilterFunctionBuilder[list.size()]));
    }

    /**
     * Adds additional query clauses using a boolean query builder.
     * @param boolQuery Consumer that configures the boolean query.
     */
    public void addQuery(final Consumer<BoolQueryBuilder> boolQuery) {
        BoolQueryBuilder builder;
        if (queryBuilder instanceof MatchAllQueryBuilder) {
            builder = QueryBuilders.boolQuery();
        } else {
            builder = QueryBuilders.boolQuery().must(queryBuilder);
        }
        boolQuery.accept(builder);
        if (builder.hasClauses()) {
            queryBuilder = builder;
        }
    }

    /**
     * Sets the query builder for this context.
     * @param queryBuilder The query builder to use.
     */
    public void setQueryBuilder(final QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    /**
     * Adds sort builders to the query context.
     * @param sortBuilders Variable number of sort builders to add.
     */
    public void addSorts(final SortBuilder<?>... sortBuilders) {
        stream(sortBuilders).of(stream -> stream.forEach(sortBuilder -> sortBuilderList.add(sortBuilder)));
    }

    /**
     * Checks if any sort builders have been added to this context.
     * @return True if sort builders are present, false otherwise.
     */
    public boolean hasSorts() {
        return !sortBuilderList.isEmpty();
    }

    /**
     * Gets the list of sort builders for this query context.
     * @return The list of sort builders.
     */
    public List<SortBuilder<?>> sortBuilders() {
        return sortBuilderList;
    }

    /**
     * Gets the query builder for this context.
     * @return The query builder.
     */
    public QueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    /**
     * Adds a field and text pair to the field log for tracking query terms.
     * @param field The field name.
     * @param text The query text for this field.
     */
    public void addFieldLog(final String field, final String text) {
        if (fieldLogMap == null) {
            return;
        }

        List<String> list = fieldLogMap.get(field);
        if (list == null) {
            list = new ArrayList<>();
            fieldLogMap.put(field, list);
        }
        list.add(text);
    }

    /**
     * Gets the default field keywords from the field log.
     * @return List of keywords for the default field, or empty list if none.
     */
    public List<String> getDefaultKeyword() {
        if (fieldLogMap != null) {
            return fieldLogMap.getOrDefault(Constants.DEFAULT_FIELD, Collections.emptyList());
        }
        return Collections.emptyList();
    }

    /**
     * Adds a query term to the highlighted query set.
     * @param text The query text to highlight.
     */
    public void addHighlightedQuery(final String text) {
        if (highlightedQuerySet != null) {
            highlightedQuerySet.add(text);
        }
    }

    /**
     * Gets the processed query string.
     * @return The query string.
     */
    public String getQueryString() {
        return queryString;
    }

    /**
     * Checks if role-based query filtering is enabled.
     * @return True if role query is enabled, false otherwise.
     */
    public boolean roleQueryEnabled() {
        return !disableRoleQuery;
    }

    /**
     * Disables role-based query filtering for this context.
     */
    public void skipRoleQuery() {
        disableRoleQuery = true;
    }

    /**
     * Gets the default field for this query context.
     * @return The default field name, or null if not set.
     */
    public String getDefaultField() {
        return defaultField;
    }

    /**
     * Sets the default field for this query context.
     * @param defaultField The default field name to set.
     */
    public void setDefaultField(final String defaultField) {
        this.defaultField = defaultField;
    }

    /**
     * Gets the set of highlighted query terms.
     * @return The set of highlighted query terms, or empty set if not initialized.
     */
    public Set<String> getHighlightedQuerySet() {
        return highlightedQuerySet != null ? highlightedQuerySet : new HashSet<>();
    }

    /**
     * Gets the field log map containing field names and their associated query terms.
     * @return The field log map, or empty map if not initialized.
     */
    public Map<String, List<String>> getFieldLogMap() {
        return fieldLogMap != null ? fieldLogMap : new HashMap<>();
    }
}
