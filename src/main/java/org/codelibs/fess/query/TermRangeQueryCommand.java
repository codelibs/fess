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
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.BytesRef;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.core.message.UserMessages;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.RangeQueryBuilder;

public class TermRangeQueryCommand extends QueryCommand {
    private static final Logger logger = LogManager.getLogger(TermRangeQueryCommand.class);

    @Override
    protected String getQueryClassName() {
        return TermRangeQuery.class.getSimpleName();
    }

    @Override
    public QueryBuilder execute(final QueryContext context, final Query query, final float boost) {
        if (query instanceof final TermRangeQuery termRangeQuery) {
            if (logger.isDebugEnabled()) {
                logger.debug("{}:{}", query, boost);
            }
            return convertTermRangeQuery(context, termRangeQuery, boost);
        }
        throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY),
                "Unknown q: " + query.getClass() + " => " + query);
    }

    protected QueryBuilder convertTermRangeQuery(final QueryContext context, final TermRangeQuery termRangeQuery, final float boost) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String field = getSearchField(context.getDefaultField(), termRangeQuery.getField());

        if (!isSearchField(field)) {
            final StringBuilder queryBuf = new StringBuilder();
            queryBuf.append(termRangeQuery.includesLower() ? '[' : '{');
            final BytesRef lowerTerm = termRangeQuery.getLowerTerm();
            queryBuf.append(lowerTerm != null ? ("*".equals(Term.toString(lowerTerm)) ? "\\*" : Term.toString(lowerTerm)) : "*");
            queryBuf.append(" TO ");
            final BytesRef upperTerm = termRangeQuery.getUpperTerm();
            queryBuf.append(upperTerm != null ? ("*".equals(Term.toString(upperTerm)) ? "\\*" : Term.toString(upperTerm)) : "*");
            queryBuf.append(termRangeQuery.includesUpper() ? ']' : '}');
            final String origQuery = queryBuf.toString();
            context.addFieldLog(Constants.DEFAULT_FIELD, origQuery);
            context.addHighlightedQuery(origQuery);
            return buildDefaultQueryBuilder(fessConfig, context, (f, b) -> QueryBuilders.matchPhraseQuery(f, origQuery).boost(b));
        }

        context.addFieldLog(field, termRangeQuery.toString(field));
        final RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(field);
        final BytesRef min = termRangeQuery.getLowerTerm();
        if (min != null) {
            if (termRangeQuery.includesLower()) {
                rangeQuery.gte(min.utf8ToString());
            } else {
                rangeQuery.gt(min.utf8ToString());
            }
        }
        final BytesRef max = termRangeQuery.getUpperTerm();
        if (max != null) {
            if (termRangeQuery.includesUpper()) {
                rangeQuery.lte(max.utf8ToString());
            } else {
                rangeQuery.lt(max.utf8ToString());
            }
        }
        rangeQuery.boost(boost);
        return rangeQuery;
    }

}
