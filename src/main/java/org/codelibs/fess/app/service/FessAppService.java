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
package org.codelibs.fess.app.service;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.automaton.Operations;
import org.apache.lucene.util.automaton.TooComplexToDeterminizeException;
import org.codelibs.fess.exception.InvalidQueryException;
import org.lastaflute.core.message.UserMessages;

/**
 * Abstract base class for Fess application services.
 * This class provides common functionality and utilities for service implementations
 * throughout the Fess search application.
 */
public abstract class FessAppService {

    /**
     * Default constructor.
     */
    public FessAppService() {
        // Default constructor
    }

    /**
     * Wraps a query string with wildcard characters to enable partial matching.
     * This method ensures that the query string is surrounded by asterisks (*)
     * to support prefix and suffix matching in search operations.
     *
     * <p>
     * A pattern that the search engine cannot run (for example {@code *xxx...*} of 315 or more repeated characters, or
     * {@code *a?a?...*}) is refused by {@link #checkWildcard(String)} as an {@link InvalidQueryException}, which the
     * admin API reports as an invalid request (HTTP 400) instead of "all shards failed" (HTTP 500).
     * </p>
     *
     * @param query the query string to wrap with wildcards
     * @return the wrapped query string with leading and trailing asterisks
     * @throws InvalidQueryException if the search engine could not run the wildcard pattern
     */
    protected static String wrapQuery(final String query) {
        final StringBuilder sb = new StringBuilder();
        if (!query.startsWith("*")) {
            sb.append("*");
        }
        sb.append(query);
        if (!query.endsWith("*")) {
            sb.append("*");
        }
        return checkWildcard(sb.toString());
    }

    /**
     * Checks that the search engine can run a wildcard pattern, and returns it.
     * <p>
     * The search engine turns a wildcard pattern into an automaton and refuses one that it cannot determinize
     * within {@link Operations#DEFAULT_DETERMINIZE_WORK_LIMIT} (the limit of the bundled Lucene, which the search
     * engine uses by default), and the whole search fails with "all shards failed". This runs the same check.
     * </p>
     *
     * @param pattern the wildcard pattern of a filter
     * @return the pattern
     * @throws InvalidQueryException if the search engine could not run the pattern
     */
    protected static String checkWildcard(final String pattern) {
        try {
            Operations.determinize(WildcardQuery.toAutomaton(new Term("f", pattern), Operations.DEFAULT_DETERMINIZE_WORK_LIMIT),
                    Operations.DEFAULT_DETERMINIZE_WORK_LIMIT);
        } catch (final TooComplexToDeterminizeException e) {
            throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryParseError(UserMessages.GLOBAL_PROPERTY_KEY),
                    "The wildcard pattern of a filter is too complex: length=" + pattern.length(), e);
        }
        return pattern;
    }

}
