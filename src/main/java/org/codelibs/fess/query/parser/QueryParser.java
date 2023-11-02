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
package org.codelibs.fess.query.parser;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

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

public class QueryParser {

    protected String defaultField = Constants.DEFAULT_FIELD;

    protected Analyzer analyzer = new WhitespaceAnalyzer();

    protected boolean allowLeadingWildcard = true;

    protected Operator defaultOperator = Operator.AND;

    protected List<Filter> filterList = new ArrayList<>();

    protected FilterChain filterChain;

    @PostConstruct
    public void init() {
        createFilterChain();
    }

    public Query parse(final String query) {
        return filterChain.parse(query);
    }

    protected org.apache.lucene.queryparser.classic.QueryParser createQueryParser() {
        final LuceneQueryParser parser = new LuceneQueryParser(defaultField, analyzer);
        parser.setAllowLeadingWildcard(allowLeadingWildcard);
        parser.setDefaultOperator(defaultOperator);
        return parser;
    }

    public void setDefaultField(final String defaultField) {
        this.defaultField = defaultField;
    }

    public void setAnalyzer(final Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public void setAllowLeadingWildcard(final boolean allowLeadingWildcard) {
        this.allowLeadingWildcard = allowLeadingWildcard;
    }

    public void setDefaultOperator(final Operator defaultOperator) {
        this.defaultOperator = defaultOperator;
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
        return query -> filter.parse(query, chain);
    }

    protected FilterChain createDefaultFilterChain() {
        return query -> {
            try {
                return createQueryParser().parse(query);
            } catch (final ParseException e) {
                throw new QueryParseException(e);
            }
        };
    }

    public interface Filter {
        Query parse(final String query, final FilterChain chain);
    }

    public interface FilterChain {
        Query parse(final String query);
    }

    protected static class LuceneQueryParser extends org.apache.lucene.queryparser.classic.QueryParser {

        private final String defaultField;

        /**
         * Creates a new {@link ExtendableQueryParser} instance
         *
         * @param f the default query field
         * @param a the analyzer used to find terms in a query string
         */
        public LuceneQueryParser(final String f, final Analyzer a) {
            super(f, a);
            this.defaultField = f;
        }

        @Override
        protected Query getFieldQuery(final String field, final String queryText, boolean quoted) throws ParseException {
            final org.apache.lucene.search.Query query = super.getFieldQuery(field, queryText, quoted);
            if (quoted && query instanceof TermQuery termQuery) {
                final Pair<String, String> splitField = splitField(defaultField, field);
                if (defaultField.equals(splitField.cur)) {
                    final PhraseQuery.Builder builder = new PhraseQuery.Builder();
                    builder.add(termQuery.getTerm());
                    return builder.build();
                }
            }
            return query;
        }

        protected Pair<String, String> splitField(String defaultField, String field) {
            int indexOf = field.indexOf(':');
            if (indexOf < 0)
                return new Pair<>(field, null);
            final String indexField = indexOf == 0 ? defaultField : field.substring(0, indexOf);
            final String extensionKey = field.substring(indexOf + 1);
            return new Pair<>(indexField, extensionKey);
        }
    }
}
