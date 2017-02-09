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

import org.codelibs.fess.Constants;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.lastaflute.web.util.LaRequestUtil;

public class QueryContext {
    private QueryBuilder queryBuilder;

    private final List<SortBuilder> sortBuilderList = new ArrayList<>();

    private final String queryString;

    private Set<String> highlightedQuerySet = null;

    private Map<String, List<String>> fieldLogMap = null;

    private boolean disableRoleQuery = false;

    @SuppressWarnings("unchecked")
    public QueryContext(final String queryString, final boolean isQuery) {
        this.queryString = queryString;
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

    public void addFunctionScore(final Consumer<List<FilterFunctionBuilder>> functionScoreQuery) {
        final List<FilterFunctionBuilder> list = new ArrayList<>();
        functionScoreQuery.accept(list);
        queryBuilder = QueryBuilders.functionScoreQuery(queryBuilder, list.toArray(new FilterFunctionBuilder[list.size()]));
    }

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

    public void setQueryBuilder(final QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public void addSorts(final SortBuilder... sortBuilders) {
        stream(sortBuilders).of(stream -> stream.forEach(sortBuilder -> sortBuilderList.add(sortBuilder)));
    }

    public boolean hasSorts() {
        return !sortBuilderList.isEmpty();
    }

    public List<SortBuilder> sortBuilders() {
        return sortBuilderList;
    }

    public QueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

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

    public List<String> getDefaultKeyword() {
        if (fieldLogMap != null) {
            return fieldLogMap.getOrDefault(Constants.DEFAULT_FIELD, Collections.emptyList());
        }
        return Collections.emptyList();
    }

    public void addHighlightedQuery(final String text) {
        if (highlightedQuerySet != null) {
            highlightedQuerySet.add(text);
        }
    }

    public String getQueryString() {
        return queryString;
    }

    public boolean roleQueryEnabled() {
        return !disableRoleQuery;
    }

    public void skipRoleQuery() {
        disableRoleQuery = true;
    }
}
