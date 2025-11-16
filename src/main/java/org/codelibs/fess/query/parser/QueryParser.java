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
package org.codelibs.fess.query.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.queryparser.ext.ExtendableQueryParser;
import org.apache.lucene.queryparser.ext.Extensions.Pair;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.codelibs.fess.Constants;
import org.codelibs.fess.exception.QueryParseException;
import org.lastaflute.web.util.LaRequestUtil;

import jakarta.annotation.PostConstruct;

/**
 * A query parser that processes search queries and converts them to Lucene Query objects.
 * This class provides a flexible architecture using a chain of filters to process and transform
 * queries before they are parsed by the underlying Lucene query parser.
 *
 * <p>The parser supports configuration of default field, analyzer, wildcard settings,
 * and default operator. It also allows adding custom filters to modify query behavior.</p>
 *
 */
public class QueryParser {

    /**
     * Default constructor.
     */
    public QueryParser() {
        // Default constructor
    }

    /** The default field to search in when no field is specified in the query */
    protected String defaultField = Constants.DEFAULT_FIELD;

    /** The analyzer used to analyze query terms */
    protected Analyzer analyzer = new WhitespaceAnalyzer();

    /** Whether to allow leading wildcards in query terms */
    protected boolean allowLeadingWildcard = true;

    /** The default operator to use between query terms */
    protected Operator defaultOperator = Operator.AND;

    /** List of filters to apply to queries */
    protected List<Filter> filterList = new ArrayList<>();

    /** The filter chain used to process queries */
    protected FilterChain filterChain;

    /**
     * Initializes the query parser by creating the filter chain.
     * This method is called automatically after construction.
     */
    @PostConstruct
    public void init() {
        createFilterChain();
    }

    /**
     * Parses the given query string and returns a Lucene Query object.
     * The query is processed through the filter chain before being parsed.
     *
     * @param query the query string to parse
     * @return the parsed Query object
     * @throws QueryParseException if the query cannot be parsed
     */
    public Query parse(final String query) {
        return filterChain.parse(query);
    }

    /**
     * Creates a new Lucene query parser with the current configuration.
     * The parser is configured with the default field, analyzer, wildcard settings,
     * and default operator.
     *
     * @return a configured Lucene query parser
     */
    protected org.apache.lucene.queryparser.classic.QueryParser createQueryParser() {
        final LuceneQueryParser parser = new LuceneQueryParser(defaultField, analyzer);
        parser.setAllowLeadingWildcard(allowLeadingWildcard);
        LaRequestUtil.getOptionalRequest().ifPresent(req -> {
            if (req.getAttribute(Constants.DEFAULT_QUERY_OPERATOR) instanceof final String op) {
                parser.setDefaultOperator(Operator.valueOf(op));
            } else {
                parser.setDefaultOperator(defaultOperator);
            }
        }).orElse(() -> {
            parser.setDefaultOperator(defaultOperator);
        });
        return parser;
    }

    /**
     * Sets the default field to search in when no field is specified in the query.
     *
     * @param defaultField the default field name
     */
    public void setDefaultField(final String defaultField) {
        this.defaultField = defaultField;
    }

    /**
     * Sets the analyzer used to analyze query terms.
     *
     * @param analyzer the analyzer to use
     */
    public void setAnalyzer(final Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    /**
     * Sets whether to allow leading wildcards in query terms.
     *
     * @param allowLeadingWildcard true to allow leading wildcards, false otherwise
     */
    public void setAllowLeadingWildcard(final boolean allowLeadingWildcard) {
        this.allowLeadingWildcard = allowLeadingWildcard;
    }

    /**
     * Sets the default operator to use between query terms.
     *
     * @param defaultOperator the default operator (AND or OR)
     */
    public void setDefaultOperator(final Operator defaultOperator) {
        this.defaultOperator = defaultOperator;
    }

    /**
     * Adds a filter to the query processing chain.
     * The filter chain is recreated after adding the filter.
     * This method is synchronized to ensure thread-safe filter chain creation.
     *
     * @param filter the filter to add
     */
    public synchronized void addFilter(final Filter filter) {
        filterList.add(filter);
        createFilterChain();
    }

    /**
     * Creates the filter chain by combining all registered filters.
     * The chain starts with the default filter chain and appends each registered filter.
     * This method is synchronized to ensure thread-safe filter chain creation.
     */
    protected synchronized void createFilterChain() {
        FilterChain chain = createDefaultFilterChain();
        for (final Filter element : filterList) {
            chain = appendFilterChain(element, chain);
        }
        filterChain = chain;
    }

    /**
     * Appends a filter to the existing filter chain.
     *
     * @param filter the filter to append
     * @param chain the existing filter chain
     * @return a new filter chain with the filter appended
     */
    protected FilterChain appendFilterChain(final Filter filter, final FilterChain chain) {
        return query -> filter.parse(query, chain);
    }

    /**
     * Creates the default filter chain that performs the actual query parsing.
     * This chain uses the Lucene query parser to parse the query string.
     *
     * @return the default filter chain
     */
    protected FilterChain createDefaultFilterChain() {
        return query -> {
            try {
                return createQueryParser().parse(query);
            } catch (final ParseException e) {
                throw new QueryParseException(e);
            }
        };
    }

    /**
     * Interface for query filters that can modify or transform queries.
     * Filters are applied in the order they are added to the parser.
     */
    public interface Filter {
        /**
         * Parses and potentially modifies the query string.
         *
         * @param query the query string to process
         * @param chain the next filter chain to invoke
         * @return the processed Query object
         */
        Query parse(final String query, final FilterChain chain);
    }

    /**
     * Interface for the filter chain that processes queries.
     * Each filter in the chain can invoke the next filter or terminate the chain.
     */
    public interface FilterChain {
        /**
         * Parses the query string and returns a Query object.
         *
         * @param query the query string to parse
         * @return the parsed Query object
         */
        Query parse(final String query);
    }

    /**
     * Custom Lucene query parser that extends the standard QueryParser
     * to provide additional functionality for quoted queries.
     */
    protected static class LuceneQueryParser extends org.apache.lucene.queryparser.classic.QueryParser {

        /** The default field for queries */
        private final String defaultField;

        /**
         * Creates a new {@link ExtendableQueryParser} instance
         *
         * @param f the default query field
         * @param a the analyzer used to find terms in a query string
         */
        public LuceneQueryParser(final String f, final Analyzer a) {
            super(f, a);
            defaultField = f;
        }

        /**
         * Overrides the field query creation to handle quoted queries specially.
         * For quoted queries on the default field, creates a phrase query instead of a term query.
         *
         * @param field the field to query
         * @param queryText the query text
         * @param quoted whether the query is quoted
         * @return the created Query object
         * @throws ParseException if the query cannot be parsed
         */
        @Override
        protected Query getFieldQuery(final String field, final String queryText, final boolean quoted) throws ParseException {
            final org.apache.lucene.search.Query query = super.getFieldQuery(field, queryText, quoted);
            if (quoted && query instanceof final TermQuery termQuery) {
                final Pair<String, String> splitField = splitField(defaultField, field);
                if (defaultField.equals(splitField.cud())) {
                    final PhraseQuery.Builder builder = new PhraseQuery.Builder();
                    builder.add(termQuery.getTerm());
                    return builder.build();
                }
            }
            return query;
        }

        /**
         * Splits a field name into its components.
         *
         * @param defaultField the default field name
         * @param field the field name to split
         * @return a Pair containing the field name and extension key
         */
        protected Pair<String, String> splitField(final String defaultField, final String field) {
            final int indexOf = field.indexOf(':');
            if (indexOf < 0) {
                return new Pair<>(field, null);
            }
            final String indexField = indexOf == 0 ? defaultField : field.substring(0, indexOf);
            final String extensionKey = field.substring(indexOf + 1);
            return new Pair<>(indexField, extensionKey);
        }
    }
}
