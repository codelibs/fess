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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.WildcardQuery;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.core.message.UserMessages;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

public class WildcardQueryCommand extends QueryCommand {
    private static final Logger logger = LogManager.getLogger(WildcardQueryCommand.class);

    protected boolean lowercaseWildcard = true;

    @Override
    protected String getQueryClassName() {
        return WildcardQuery.class.getSimpleName();
    }

    @Override
    public QueryBuilder execute(final QueryContext context, final Query query, final float boost) {
        if (query instanceof final WildcardQuery wildcardQuery) {
            if (logger.isDebugEnabled()) {
                logger.debug("{}:{}", query, boost);
            }
            return convertWildcardQuery(context, wildcardQuery, boost);
        }
        throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY),
                "Unknown q: " + query.getClass() + " => " + query);
    }

    protected QueryBuilder convertWildcardQuery(final QueryContext context, final WildcardQuery wildcardQuery, final float boost) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String field = getSearchField(context.getDefaultField(), wildcardQuery.getField());
        if (Constants.DEFAULT_FIELD.equals(field)) {
            final String text = wildcardQuery.getTerm().text();
            context.addFieldLog(field, text);
            context.addHighlightedQuery(StringUtils.strip(text, "*"));
            return buildDefaultQueryBuilder(fessConfig, context,
                    (f, b) -> QueryBuilders.wildcardQuery(f, toLowercaseWildcard(text)).boost(b * boost));
        }

        if (isSearchField(field)) {
            final String text = wildcardQuery.getTerm().text();
            context.addFieldLog(field, text);
            context.addHighlightedQuery(StringUtils.strip(text, "*"));
            return QueryBuilders.wildcardQuery(field, toLowercaseWildcard(text)).boost(boost);
        }

        final String query = wildcardQuery.getTerm().toString();
        final StringBuilder queryBuf = new StringBuilder(query.length() + 2);
        if (!query.startsWith("*")) {
            queryBuf.append('*');
        }
        queryBuf.append(toLowercaseWildcard(query));
        if (!query.endsWith("*")) {
            queryBuf.append('*');
        }
        final String origQuery = queryBuf.toString();
        context.addFieldLog(Constants.DEFAULT_FIELD, origQuery);
        context.addHighlightedQuery(StringUtils.strip(query, "*"));
        return buildDefaultQueryBuilder(fessConfig, context, (f, b) -> QueryBuilders.wildcardQuery(f, origQuery).boost(b * boost));
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
