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
package org.codelibs.fess.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.Query;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.exception.InvalidQueryException;
import org.lastaflute.core.message.UserMessages;
import org.opensearch.index.query.QueryBuilder;

import jakarta.annotation.PostConstruct;

/**
 * Query processor component that handles query filters and commands.
 * This class provides a pipeline for processing Lucene queries by applying
 * a chain of filters and executing registered query commands.
 *
 * <p>The processor maintains a map of query commands indexed by query class names
 * and a list of filters that are applied in order during query processing.</p>
 */
public class QueryProcessor {

    /**
     * Default constructor.
     */
    public QueryProcessor() {
        // Default constructor
    }

    private static final Logger logger = LogManager.getLogger(QueryProcessor.class);

    /**
     * Map of query commands indexed by query class simple names.
     * Used to lookup appropriate command handlers for different query types.
     */
    protected Map<String, QueryCommand> queryCommandMap = new HashMap<>();

    /**
     * List of filters that will be applied during query processing.
     * Filters are applied in the order they are added to this list.
     */
    protected List<Filter> filterList = new ArrayList<>();

    /**
     * The filter chain that processes queries through all registered filters
     * before executing the appropriate query command.
     */
    protected FilterChain filterChain;

    /**
     * Initializes the query processor after construction.
     * This method creates the initial filter chain from the registered filters.
     * Called automatically by the DI container after bean construction.
     */
    @PostConstruct
    public void init() {
        createFilterChain();
    }

    /**
     * Executes query processing through the filter chain.
     *
     * @param context the query context containing search parameters and state
     * @param query the Lucene query to be processed
     * @param boost the boost factor to apply to the query
     * @return the processed OpenSearch QueryBuilder
     */
    public QueryBuilder execute(final QueryContext context, final Query query, final float boost) {
        return filterChain.execute(context, query, boost);
    }

    /**
     * Adds a query command to the processor.
     *
     * @param name the name to associate with the command (typically the query class simple name)
     * @param queryCommand the query command implementation to add
     * @throws IllegalArgumentException if name or queryCommand is null
     */
    public void add(final String name, final QueryCommand queryCommand) {
        if (name == null || queryCommand == null) {
            throw new IllegalArgumentException(
                    "Both name and queryCommand parameters are required. name: " + name + ", queryCommand: " + queryCommand);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Loaded QueryCommand: {}", name);
        }
        queryCommandMap.put(name, queryCommand);
    }

    /**
     * Adds a filter to the processing pipeline.
     * After adding a filter, the filter chain is recreated to include the new filter.
     *
     * @param filter the filter to add to the processing pipeline
     */
    public void addFilter(final Filter filter) {
        filterList.add(filter);
        createFilterChain();
    }

    /**
     * Creates the filter chain from the registered filters.
     * The chain starts with the default filter chain and appends each registered filter.
     */
    protected void createFilterChain() {
        FilterChain chain = createDefaultFilterChain();
        for (final Filter element : filterList) {
            chain = appendFilterChain(element, chain);
        }
        filterChain = chain;
    }

    /**
     * Appends a filter to an existing filter chain.
     *
     * @param filter the filter to append
     * @param chain the existing filter chain to append to
     * @return a new filter chain that includes the appended filter
     */
    protected FilterChain appendFilterChain(final Filter filter, final FilterChain chain) {
        return (context, query, boost) -> filter.execute(context, query, boost, chain);
    }

    /**
     * Creates the default filter chain that executes query commands.
     * This chain looks up the appropriate query command based on the query class name
     * and executes it. If no command is found, throws an InvalidQueryException.
     *
     * @return the default filter chain implementation
     */
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

    /**
     * Interface for query processing filters.
     * Filters can modify, validate, or enhance queries before they are executed.
     */
    public interface Filter {
        /**
         * Executes the filter logic on the given query.
         *
         * @param context the query context containing search parameters and state
         * @param query the Lucene query to be processed
         * @param boost the boost factor to apply to the query
         * @param chain the next filter chain to execute after this filter
         * @return the processed OpenSearch QueryBuilder
         */
        QueryBuilder execute(final QueryContext context, final Query query, final float boost, final FilterChain chain);
    }

    /**
     * Interface for filter chains that process queries through a sequence of filters.
     * This follows the Chain of Responsibility pattern for query processing.
     */
    public interface FilterChain {
        /**
         * Executes the filter chain on the given query.
         *
         * @param context the query context containing search parameters and state
         * @param query the Lucene query to be processed
         * @param boost the boost factor to apply to the query
         * @return the processed OpenSearch QueryBuilder
         */
        QueryBuilder execute(final QueryContext context, final Query query, final float boost);
    }
}
