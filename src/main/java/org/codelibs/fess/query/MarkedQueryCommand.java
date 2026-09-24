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
import org.apache.lucene.search.Query;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.exception.InvalidQueryException;
import org.lastaflute.core.message.UserMessages;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;

/**
 * Command class that hands a {@link MarkedQuery} to the {@link QueryMarker} that marked it.
 */
public class MarkedQueryCommand extends QueryCommand {
    private static final Logger logger = LogManager.getLogger(MarkedQueryCommand.class);

    /**
     * Default constructor.
     */
    public MarkedQueryCommand() {
    }

    @Override
    protected String getQueryClassName() {
        return MarkedQuery.class.getSimpleName();
    }

    @Override
    public QueryBuilder execute(final QueryContext context, final Query query, final float boost) {
        if (query instanceof final MarkedQuery markedQuery) {
            if (logger.isDebugEnabled()) {
                logger.debug("MarkedQuery: query={}, marker={}, boost={}", query, markedQuery.getMarker().getClass().getSimpleName(),
                        boost);
            }
            return markedQuery.getMarker().execute(context, markedQuery, boost);
        }
        throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY),
                "Unknown q: " + query.getClass() + " => " + query);
    }
}
