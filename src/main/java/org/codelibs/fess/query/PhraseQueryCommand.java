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

import static org.codelibs.core.stream.StreamUtil.stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.core.message.UserMessages;
import org.opensearch.index.query.QueryBuilder;

public class PhraseQueryCommand extends QueryCommand {
    private static final Logger logger = LogManager.getLogger(PhraseQueryCommand.class);

    @Override
    protected String getQueryClassName() {
        return PhraseQuery.class.getSimpleName();
    }

    @Override
    public QueryBuilder execute(final QueryContext context, final Query query, final float boost) {
        if (query instanceof final PhraseQuery phraseQuery) {
            if (logger.isDebugEnabled()) {
                logger.debug("{}:{}", query, boost);
            }
            return convertPhraseQuery(context, phraseQuery, boost);
        }
        throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY),
                "Unknown q: " + query.getClass() + " => " + query);
    }

    protected QueryBuilder convertPhraseQuery(final QueryContext context, final PhraseQuery phraseQuery, final float boost) {
        final Term[] terms = phraseQuery.getTerms();
        if (terms.length == 0) {
            throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY),
                    "Unknown phrase query: " + phraseQuery);
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String field = terms[0].field();
        final String[] texts = stream(terms).get(stream -> stream.map(Term::text).toArray(n -> new String[n]));

        return convertPhraseQuery(fessConfig, context, phraseQuery, boost, field, texts);
    }

    protected QueryBuilder convertPhraseQuery(final FessConfig fessConfig, final QueryContext context, final PhraseQuery phraseQuery,
            final float boost, final String field, final String[] texts) {
        final String text = String.join(" ", texts);

        if (Constants.DEFAULT_FIELD.equals(field)) {
            context.addFieldLog(field, text);
            stream(texts).of(stream -> stream.forEach(t -> context.addHighlightedQuery(t)));
            return buildDefaultQueryBuilder(fessConfig, context, (f, b) -> buildMatchPhraseQuery(f, text).boost(b * boost));
        }

        if (isSearchField(field)) {
            context.addFieldLog(field, text);
            stream(texts).of(stream -> stream.forEach(t -> context.addHighlightedQuery(t)));
            return buildMatchPhraseQuery(field, text);
        }

        context.addFieldLog(Constants.DEFAULT_FIELD, text);
        return buildDefaultQueryBuilder(fessConfig, context, (f, b) -> buildMatchPhraseQuery(f, text).boost(b * boost));
    }

}
