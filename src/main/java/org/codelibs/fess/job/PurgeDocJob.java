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
package org.codelibs.fess.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

/**
 * Job for purging expired documents from the search index.
 * This job removes documents that have passed their expiration time based on the expires field.
 * It helps maintain the search index by cleaning up outdated content automatically.
 */
public class PurgeDocJob {

    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(PurgeDocJob.class);

    /**
     * Default constructor for PurgeDocJob.
     * Creates a new instance of the document purging job with default settings.
     */
    public PurgeDocJob() {
        // Default constructor
    }

    /**
     * Executes the document purging job.
     * Removes all documents from the search index that have expired based on their expires field.
     *
     * @return a string containing the execution result and any error messages
     */
    public String execute() {
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        final StringBuilder resultBuf = new StringBuilder();

        // clean up
        final QueryBuilder queryBuilder = QueryBuilders.rangeQuery(fessConfig.getIndexFieldExpires()).to("now");
        try {
            searchEngineClient.deleteByQuery(fessConfig.getIndexDocumentUpdateIndex(), queryBuilder);

        } catch (final Exception e) {
            logger.error("Could not delete expired documents: {}", queryBuilder, e);
            resultBuf.append(e.getMessage()).append("\n");
        }

        return resultBuf.toString();
    }

}
