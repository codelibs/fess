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

import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.core.message.UserMessages;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

public class PrefixQueryCommand extends QueryCommand {
    private static final Logger logger = LogManager.getLogger(PrefixQueryCommand.class);

    protected boolean lowercaseWildcard = true;

    @Override
    protected String getQueryClassName() {
        return PrefixQuery.class.getSimpleName();
    }

    @Override
    public QueryBuilder execute(final QueryContext context, final Query query, final float boost) {
        if (query instanceof final PrefixQuery prefixQuery) {
            if (logger.isDebugEnabled()) {
                logger.debug("{}:{}", query, boost);
            }
            return convertPrefixQuery(context, prefixQuery, boost);
        }
        throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY),
                "Unknown q: " + query.getClass() + " => " + query);
    }

    protected QueryBuilder convertPrefixQuery(final QueryContext context, final PrefixQuery prefixQuery, final float boost) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String field = getSearchField(context.getDefaultField(), prefixQuery.getField());
        final String text = prefixQuery.getPrefix().text();

        if (Constants.DEFAULT_FIELD.equals(field)) {
            context.addFieldLog(field, text + "*");
            context.addHighlightedQuery(text);
            return buildDefaultQueryBuilder(fessConfig, context,
                    (f, b) -> QueryBuilders.matchPhrasePrefixQuery(f, toLowercaseWildcard(text)).boost(b * boost)
                            .maxExpansions(fessConfig.getQueryPrefixExpansionsAsInteger()).slop(fessConfig.getQueryPrefixSlopAsInteger()));
        }

        if (!isSearchField(field)) {
            final String query = prefixQuery.getPrefix().toString();
            final String origQuery = toLowercaseWildcard(query);
            context.addFieldLog(Constants.DEFAULT_FIELD, query + "*");
            context.addHighlightedQuery(origQuery);
            return buildDefaultQueryBuilder(fessConfig, context,
                    (f, b) -> QueryBuilders.matchPhrasePrefixQuery(f, origQuery).boost(b * boost)
                            .maxExpansions(fessConfig.getQueryPrefixExpansionsAsInteger()).slop(fessConfig.getQueryPrefixSlopAsInteger()));
        }

        context.addFieldLog(field, text + "*");
        context.addHighlightedQuery(text);
        if (getQueryFieldConfig().notAnalyzedFieldSet.contains(field)) {
            return QueryBuilders.prefixQuery(field, toLowercaseWildcard(text)).boost(boost);
        }
        return QueryBuilders.matchPhrasePrefixQuery(field, toLowercaseWildcard(text)).boost(boost)
                .maxExpansions(fessConfig.getQueryPrefixExpansionsAsInteger()).slop(fessConfig.getQueryPrefixSlopAsInteger());
    }

    protected String toLowercaseWildcard(final String value) {
        if (lowercaseWildcard) {
            return value.toLowerCase(Locale.ROOT);
        }
        return value;
    }

    public void setLowercaseWildcard(final boolean lowercaseWildcard) {
        this.lowercaseWildcard = lowercaseWildcard;
    }
}
