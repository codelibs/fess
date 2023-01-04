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

public class FuzzyQueryCommand extends QueryCommand {
    private static final Logger logger = LogManager.getLogger(FuzzyQueryCommand.class);

    @Override
    protected String getQueryClassName() {
        return FuzzyQuery.class.getSimpleName();
    }

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

    protected QueryBuilder convertFuzzyQuery(final QueryContext context, final FuzzyQuery fuzzyQuery, final float boost) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Term term = fuzzyQuery.getTerm();
        final String field = getSearchField(context.getDefaultField(), term.field());

        if (Constants.DEFAULT_FIELD.equals(field)) {
            final String text = term.text();
            context.addFieldLog(field, text);
            context.addHighlightedQuery(text);
            return buildDefaultQueryBuilder(fessConfig, context,
                    (f, b) -> QueryBuilders.fuzzyQuery(f, text).fuzziness(Fuzziness.fromEdits(fuzzyQuery.getMaxEdits())).boost(b * boost)
                            .maxExpansions(fessConfig.getQueryFuzzyExpansionsAsInteger())
                            .prefixLength(fessConfig.getQueryFuzzyPrefixLengthAsInteger())
                            .transpositions(Constants.TRUE.equalsIgnoreCase(fessConfig.getQueryFuzzyTranspositions())));
        }

        if (isSearchField(field)) {
            final String text = term.text();
            context.addFieldLog(field, text);
            context.addHighlightedQuery(text);
            return QueryBuilders.fuzzyQuery(field, text).boost(boost).fuzziness(Fuzziness.fromEdits(fuzzyQuery.getMaxEdits()))
                    .maxExpansions(fessConfig.getQueryFuzzyExpansionsAsInteger())
                    .prefixLength(fessConfig.getQueryFuzzyPrefixLengthAsInteger())
                    .transpositions(Constants.TRUE.equalsIgnoreCase(fessConfig.getQueryFuzzyTranspositions()));
        }

        final String origQuery = term.toString();
        context.addFieldLog(Constants.DEFAULT_FIELD, origQuery);
        context.addHighlightedQuery(origQuery);
        return buildDefaultQueryBuilder(fessConfig, context,
                (f, b) -> QueryBuilders.fuzzyQuery(f, origQuery).fuzziness(Fuzziness.fromEdits(fuzzyQuery.getMaxEdits())).boost(b * boost)
                        .maxExpansions(fessConfig.getQueryFuzzyExpansionsAsInteger())
                        .prefixLength(fessConfig.getQueryFuzzyPrefixLengthAsInteger())
                        .transpositions(Constants.TRUE.equalsIgnoreCase(fessConfig.getQueryFuzzyTranspositions())));
    }
}
