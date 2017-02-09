/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
package org.codelibs.fess.job;

import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PurgeDocJob {

    private static final Logger logger = LoggerFactory.getLogger(PurgeDocJob.class);

    public String execute() {
        final FessEsClient fessEsClient = ComponentUtil.getFessEsClient();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        final StringBuilder resultBuf = new StringBuilder();

        // clean up
        final QueryBuilder queryBuilder = QueryBuilders.rangeQuery(fessConfig.getIndexFieldExpires()).to("now");
        try {
            fessEsClient.deleteByQuery(fessConfig.getIndexDocumentUpdateIndex(), fessConfig.getIndexDocumentType(), queryBuilder);

        } catch (final Exception e) {
            logger.error("Could not delete expired documents: " + queryBuilder.toString(), e);
            resultBuf.append(e.getMessage()).append("\n");
        }

        return resultBuf.toString();
    }

}
