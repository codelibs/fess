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
package org.codelibs.fess.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.Query;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.exception.InvalidQueryException;
import org.lastaflute.core.message.UserMessages;
import org.opensearch.index.query.QueryBuilder;

public class QueryProcessor {
    private static final Logger logger = LogManager.getLogger(QueryProcessor.class);

    protected Map<String, QueryCommand> queryCommandMap = new HashMap<>();

    protected List<Filter> filterList = new ArrayList<>();

    protected FilterChain filterChain;

    @PostConstruct
    public void init() {
        createFilterChain();
    }

    public QueryBuilder execute(final QueryContext context, final Query query, final float boost) {
        return filterChain.execute(context, query, boost);
    }

    public void add(final String name, final QueryCommand queryCommand) {
        if (name == null || queryCommand == null) {
            throw new IllegalArgumentException("name or queryCommand is null.");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Loaded {}", name);
        }
        queryCommandMap.put(name, queryCommand);
    }

    public void addFilter(final Filter filter) {
        filterList.add(filter);
        createFilterChain();
    }

    protected void createFilterChain() {
        FilterChain chain = createDefaultFilterChain();
        for (final Filter element : filterList) {
            chain = appendFilterChain(element, chain);
        }
        filterChain = chain;
    }

    protected FilterChain appendFilterChain(final Filter filter, final FilterChain chain) {
        return (context, query, boost) -> filter.execute(context, query, boost, chain);
    }

    protected FilterChain createDefaultFilterChain() {
        return (context, query, boost) -> {
            final QueryCommand queryCommand = queryCommandMap.get(query.getClass().getSimpleName());
            if (queryCommand != null) {
                return queryCommand.execute(context, query, boost);
            }
            throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY),
                    "Unknown q: " + query.getClass() + " => " + query);
        };
    }

    public interface Filter {
        QueryBuilder execute(final QueryContext context, final Query query, final float boost, final FilterChain chain);
    }

    public interface FilterChain {
        QueryBuilder execute(final QueryContext context, final Query query, final float boost);
    }
}
