/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.task;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.Resource;

import jp.sf.fess.Constants;
import jp.sf.fess.helper.HotSearchWordHelper;
import jp.sf.fess.service.CrawlingSessionService;
import jp.sf.fess.service.SearchLogService;
import jp.sf.fess.service.UserInfoService;

import org.codelibs.core.util.DynamicProperties;
import org.seasar.chronos.core.TaskTrigger;
import org.seasar.chronos.core.annotation.task.Task;
import org.seasar.chronos.core.trigger.CCronTrigger;
import org.seasar.framework.container.SingletonS2Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Task
public class DailyTask implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory
            .getLogger(DailyTask.class);

    @Resource
    protected DynamicProperties crawlerProperties;

    @Resource
    protected CrawlingSessionService crawlingSessionService;

    @Resource
    protected SearchLogService searchLogService;

    @Resource
    protected UserInfoService userInfoService;

    private CCronTrigger trigger;

    public TaskTrigger getTrigger() {
        if (trigger == null) {
            trigger = new CCronTrigger(crawlerProperties.getProperty(
                    Constants.DAILY_CRON_EXPRESSION_PROPERTY,
                    Constants.DEFAULT_DAILY_CRON_EXPRESSION));
        } else {
            trigger.setExpression(crawlerProperties.getProperty(
                    Constants.DAILY_CRON_EXPRESSION_PROPERTY,
                    Constants.DEFAULT_DAILY_CRON_EXPRESSION));
        }
        return trigger;
    }

    public void doExecute() {
        // hot words
        try {
            SingletonS2Container.getComponent(HotSearchWordHelper.class)
                    .reload();
        } catch (final Exception e) {
            logger.error("Failed to store a search log.", e);
        }

        // purge crawling sessions
        try {
            crawlingSessionService.deleteBefore(new Date());
        } catch (final Exception e) {
            logger.error("Failed to purge crawling sessions.", e);
        }

        // purge search logs
        try {
            final String value = crawlerProperties.getProperty(
                    Constants.PURGE_SERCH_LOG_DAY_PROPERTY,
                    Constants.DEFAULT_PURGE_DAY);
            final int days = Integer.parseInt(value);
            searchLogService.deleteBefore(days);
        } catch (final Exception e) {
            logger.error("Failed to purge search logs.", e);
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
        }
    }

    public void catchException(final Exception e) {
        logger.error("Failed to execute search log task.", e);
    }
}
