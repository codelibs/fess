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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.core.message.UserMessages;
import org.opensearch.common.unit.Fuzziness;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

/**
 * Query command implementation for handling fuzzy search queries.
 * This class converts Lucene FuzzyQuery objects into OpenSearch fuzzy query builders,
 * supporting configurable fuzzy matching parameters like edit distance and expansions.
 *
 */
public class FuzzyQueryCommand extends QueryCommand {
    private static final Logger logger = LogManager.getLogger(FuzzyQueryCommand.class);

    /**
     * Default constructor.
     */
    public FuzzyQueryCommand() {
        super();
    }

    @Override
    protected String getQueryClassName() {
        return FuzzyQuery.class.getSimpleName();
    }

    /**
     * Executes the fuzzy query command to convert a Lucene FuzzyQuery into an OpenSearch QueryBuilder.
     *
     * @param context the query context containing search configuration
     * @param query the Lucene query to convert (must be a FuzzyQuery)
     * @param boost the boost factor to apply to the query
     * @return OpenSearch QueryBuilder for fuzzy matching
     * @throws InvalidQueryException if the query is not a FuzzyQuery
     */
    @Override
    public QueryBuilder execute(final QueryContext context, final Query query, final float boost) {
        if (query instanceof final FuzzyQuery fuzzyQuery) {
            if (logger.isDebugEnabled()) {
                logger.debug("{}:{}", query, boost);
            }
            return convertFuzzyQuery(context, fuzzyQuery, boost);
        }
        throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY),
                "Unknown q: " + query.getClass() + " => " + query);
    }

    /**
     * Converts a Lucene FuzzyQuery into an OpenSearch fuzzy query builder.
     * Applies fuzzy matching configuration including edit distance, expansions, and prefix length.
     *
     * @param context the query context containing field mappings
     * @param fuzzyQuery the Lucene FuzzyQuery to convert
     * @param boost the boost factor to apply
     * @return OpenSearch QueryBuilder configured for fuzzy matching
     */
    protected QueryBuilder convertFuzzyQuery(final QueryContext context, final FuzzyQuery fuzzyQuery, final float boost) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Term term = fuzzyQuery.getTerm();
        final String field = getSearchField(context.getDefaultField(), term.field());

        if (Constants.DEFAULT_FIELD.equals(field)) {
            final String text = term.text();
            context.addFieldLog(field, text);
            context.addHighlightedQuery(text);
            return buildDefaultQueryBuilder(fessConfig, context,
                    (f, b) -> QueryBuilders.fuzzyQuery(f, text)
                            .fuzziness(Fuzziness.fromEdits(fuzzyQuery.getMaxEdits()))
                            .boost(b * boost)
                            .maxExpansions(fessConfig.getQueryFuzzyExpansionsAsInteger())
                            .prefixLength(fessConfig.getQueryFuzzyPrefixLengthAsInteger())
                            .transpositions(Constants.TRUE.equalsIgnoreCase(fessConfig.getQueryFuzzyTranspositions())));
        }

        if (isSearchField(field)) {
            final String text = term.text();
            context.addFieldLog(field, text);
            context.addHighlightedQuery(text);
            return QueryBuilders.fuzzyQuery(field, text)
                    .boost(boost)
                    .fuzziness(Fuzziness.fromEdits(fuzzyQuery.getMaxEdits()))
                    .maxExpansions(fessConfig.getQueryFuzzyExpansionsAsInteger())
                    .prefixLength(fessConfig.getQueryFuzzyPrefixLengthAsInteger())
                    .transpositions(Constants.TRUE.equalsIgnoreCase(fessConfig.getQueryFuzzyTranspositions()));
        }

        final String origQuery = term.toString();
        context.addFieldLog(Constants.DEFAULT_FIELD, origQuery);
        context.addHighlightedQuery(origQuery);
        return buildDefaultQueryBuilder(fessConfig, context,
                (f, b) -> QueryBuilders.fuzzyQuery(f, origQuery)
                        .fuzziness(Fuzziness.fromEdits(fuzzyQuery.getMaxEdits()))
                        .boost(b * boost)
                        .maxExpansions(fessConfig.getQueryFuzzyExpansionsAsInteger())
                        .prefixLength(fessConfig.getQueryFuzzyPrefixLengthAsInteger())
                        .transpositions(Constants.TRUE.equalsIgnoreCase(fessConfig.getQueryFuzzyTranspositions())));
    }
}
