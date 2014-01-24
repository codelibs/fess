/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.job;

import java.util.Date;

import jp.sf.fess.Constants;
import jp.sf.fess.helper.HotSearchWordHelper;
import jp.sf.fess.service.CrawlingSessionService;
import jp.sf.fess.service.JobLogService;
import jp.sf.fess.service.SearchLogService;
import jp.sf.fess.service.UserInfoService;
import jp.sf.fess.util.ComponentUtil;

import org.codelibs.core.util.DynamicProperties;
import org.seasar.framework.container.SingletonS2Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DailyJob {

    private static final Logger logger = LoggerFactory
            .getLogger(DailyJob.class);

    public String execute() {
        final CrawlingSessionService crawlingSessionService = SingletonS2Container
                .getComponent(CrawlingSessionService.class);
        final SearchLogService searchLogService = SingletonS2Container
                .getComponent(SearchLogService.class);
        final JobLogService jobLogService = SingletonS2Container
                .getComponent(JobLogService.class);
        final UserInfoService userInfoService = SingletonS2Container
                .getComponent(UserInfoService.class);
        final HotSearchWordHelper hotSearchWordHelper = ComponentUtil
                .getHotSearchWordHelper();
        final DynamicProperties crawlerProperties = ComponentUtil
                .getCrawlerProperties();

        final StringBuilder resultBuf = new StringBuilder();

        // hot words
        try {
            hotSearchWordHelper.reload();
        } catch (final Exception e) {
            logger.error("Failed to store a search log.", e);
            resultBuf.append(e.getMessage()).append("\n");
        }

        // purge crawling sessions
        try {
            crawlingSessionService.deleteBefore(new Date());
        } catch (final Exception e) {
            logger.error("Failed to purge crawling sessions.", e);
            resultBuf.append(e.getMessage()).append("\n");
        }

        // purge search logs
        try {
            final String value = crawlerProperties.getProperty(
                    Constants.PURGE_SEARCH_LOG_DAY_PROPERTY,
                    Constants.DEFAULT_PURGE_DAY);
            final int days = Integer.parseInt(value);
            searchLogService.deleteBefore(days);
        } catch (final Exception e) {
            logger.error("Failed to purge search logs.", e);
            resultBuf.append(e.getMessage()).append("\n");
        }

        // purge job logs
        try {
            final String value = crawlerProperties.getProperty(
                    Constants.PURGE_JOB_LOG_DAY_PROPERTY,
                    Constants.DEFAULT_PURGE_DAY);
            final int days = Integer.parseInt(value);
            jobLogService.deleteBefore(days);
        } catch (final Exception e) {
            logger.error("Failed to purge job logs.", e);
            resultBuf.append(e.getMessage()).append("\n");
        }

        // purge user info
        try {
            final String value = crawlerProperties.getProperty(
                    Constants.PURGE_USER_INFO_DAY_PROPERTY,
                    Constants.DEFAULT_PURGE_DAY);
            final int days = Integer.parseInt(value);
            userInfoService.deleteBefore(days);
        } catch (final Exception e) {
            logger.error("Failed to purge user info.", e);
            resultBuf.append(e.getMessage()).append("\n");
        }

        return resultBuf.toString();
    }

}
