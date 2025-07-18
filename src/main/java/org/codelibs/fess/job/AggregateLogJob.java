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
import org.codelibs.fess.helper.SearchLogHelper;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Job for aggregating and storing search logs.
 * This job processes search logs and stores them in the search log repository.
 */
public class AggregateLogJob {

    private static final Logger logger = LogManager.getLogger(AggregateLogJob.class);

    /**
     * Default constructor.
     */
    public AggregateLogJob() {
        // Default constructor
    }

    /**
     * Executes the search log aggregation job.
     * Stores search logs using the SearchLogHelper and returns execution results.
     *
     * @return execution result message, empty if successful or error message if failed
     */
    public String execute() {
        final SearchLogHelper searchLogHelper = ComponentUtil.getSearchLogHelper();

        final StringBuilder resultBuf = new StringBuilder();

        try {
            searchLogHelper.storeSearchLog();
        } catch (final Exception e) {
            logger.error("Failed to store a search log.", e);
            resultBuf.append(e.getMessage()).append("\n");
        }

        return resultBuf.toString();
    }

}
