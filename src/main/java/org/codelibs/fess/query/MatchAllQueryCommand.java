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
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.codelibs.fess.entity.QueryContext;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

public class MatchAllQueryCommand extends QueryCommand {
    private static final Logger logger = LogManager.getLogger(MatchAllQueryCommand.class);

    @Override
    protected String getQueryClassName() {
        return MatchAllDocsQuery.class.getSimpleName();
    }

    @Override
    public QueryBuilder execute(final QueryContext context, final Query query, final float boost) {
        if (logger.isDebugEnabled()) {
            logger.debug("{}:{}", query, boost);
        }
        return QueryBuilders.matchAllQuery();
    }

}
