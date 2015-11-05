/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.codelibs.fess.Constants;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.lastaflute.web.util.LaRequestUtil;

public class QueryContext {
    private QueryBuilder queryBuilder;

    private final List<SortBuilder> sortBuilderList = new ArrayList<>();

    private final String queryString;

    private final Set<String> highlightedQuerySet = new HashSet<>();

    private Map<String, List<String>> fieldLogMap;

    public QueryContext(final String queryString) {
        this.queryString = queryString;
        LaRequestUtil.getOptionalRequest().ifPresent(request -> {
            request.setAttribute(Constants.HIGHLIGHT_QUERIES, highlightedQuerySet);
            @SuppressWarnings("unchecked")
            final Map<String, List<String>> existFieldLogMap = (Map<String, List<String>>) request.getAttribute(Constants.FIELD_LOGS);
            if (existFieldLogMap != null) {
                fieldLogMap = existFieldLogMap;
            } else {
                fieldLogMap = new HashMap<>();
            }
            request.setAttribute(Constants.FIELD_LOGS, fieldLogMap);
        });
    }

    public void addFilter(final FilterBuilder filterBuilder) {
        queryBuilder = QueryBuilders.filteredQuery(queryBuilder, filterBuilder);
    }

    public void addQuery(final Consumer<BoolQueryBuilder> boolQuery) {
        BoolQueryBuilder builder;
        if (queryBuilder instanceof BoolQueryBuilder) {
            builder = (BoolQueryBuilder) queryBuilder;
        } else if (queryBuilder instanceof MatchAllQueryBuilder) {
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
        Stream.of(sortBuilders).forEach(sortBuilder -> sortBuilderList.add(sortBuilder));
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

    public void addFieldLog(String field, String text) {
        List<String> list = fieldLogMap.get(field);
        if (list == null) {
            list = new ArrayList<>();
            fieldLogMap.put(field, list);
        }
        list.add(text);
    }

    public void addHighlightedQuery(String text) {
        highlightedQuerySet.add(text);
    }

}
