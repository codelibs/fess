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
import org.codelibs.fess.app.service.CrawlingInfoService;
import org.codelibs.fess.app.service.JobLogService;
import org.codelibs.fess.app.service.SearchLogService;
import org.codelibs.fess.app.service.UserInfoService;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Job for purging old log entries from the system.
 * This job removes old crawling sessions, search logs, job logs, and user info logs
 * based on configured retention periods. It helps maintain system performance by
 * preventing log tables from growing too large.
 */
public class PurgeLogJob {

    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(PurgeLogJob.class);

    /**
     * Default constructor for PurgeLogJob.
     * Creates a new instance of the log purging job with default settings.
     */
    public PurgeLogJob() {
        // Default constructor
    }

    /**
     * Executes the log purging job.
     * Performs the following cleanup operations:
     * - Purges old crawling sessions
     * - Purges search logs older than configured days
     * - Purges job logs older than configured days
     * - Purges user info logs older than configured days
     * - Updates job log status
     *
     * @return a string containing the execution result and any error messages
     */
    public String execute() {
        final CrawlingInfoService crawlingInfoService = ComponentUtil.getComponent(CrawlingInfoService.class);
        final SearchLogService searchLogService = ComponentUtil.getComponent(SearchLogService.class);
        final JobLogService jobLogService = ComponentUtil.getComponent(JobLogService.class);
        final UserInfoService userInfoService = ComponentUtil.getComponent(UserInfoService.class);
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();

        final StringBuilder resultBuf = new StringBuilder();

        // purge crawling sessions
        try {
            crawlingInfoService.deleteBefore(systemHelper.getCurrentTimeAsLong());
        } catch (final Exception e) {
            logger.error("Failed to purge crawling sessions.", e);
            resultBuf.append(e.getMessage()).append("\n");
        }

        // purge search logs
        try {
            final int days = ComponentUtil.getFessConfig().getPurgeSearchLogDay();
            if (days >= 0) {
                searchLogService.deleteBefore(days);
            } else {
                resultBuf.append("Skipped to purge search logs.\n");
            }
        } catch (final Exception e) {
            logger.error("Failed to purge search logs.", e);
            resultBuf.append(e.getMessage()).append("\n");
        }

        // purge job logs
        try {
            final int days = ComponentUtil.getFessConfig().getPurgeJobLogDay();
            if (days >= 0) {
                jobLogService.deleteBefore(days);
            } else {
                resultBuf.append("Skipped to purge job logs.\n");
            }
        } catch (final Exception e) {
            logger.error("Failed to purge job logs.", e);
            resultBuf.append(e.getMessage()).append("\n");
        }

        // purge user info
        try {
            final int days = ComponentUtil.getFessConfig().getPurgeUserInfoDay();
            if (days >= 0) {
                userInfoService.deleteBefore(days);
            } else {
                resultBuf.append("Skipped to purge user info logs.\n");
            }
        } catch (final Exception e) {
            logger.error("Failed to purge user info.", e);
            resultBuf.append(e.getMessage()).append("\n");
        }

        // update job logs
        try {
            jobLogService.updateStatus();
        } catch (final Exception e) {
            logger.error("Failed to purge job logs.", e);
            resultBuf.append(e.getMessage()).append("\n");
        }

        return resultBuf.toString();
    }

}
