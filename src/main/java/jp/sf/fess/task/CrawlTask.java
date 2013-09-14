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
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import jp.sf.fess.Constants;
import jp.sf.fess.helper.CrawlingSessionHelper;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.service.CrawlingSessionService;

import org.codelibs.core.util.DynamicProperties;
import org.seasar.chronos.core.TaskTrigger;
import org.seasar.chronos.core.annotation.task.Task;
import org.seasar.chronos.core.trigger.CCronTrigger;
import org.seasar.framework.container.SingletonS2Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Task
public class CrawlTask implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory
            .getLogger(CrawlTask.class);

    @Resource
    protected DynamicProperties crawlerProperties;

    @Resource
    protected CrawlingSessionService crawlingSessionService;

    private CCronTrigger trigger;

    public TaskTrigger getTrigger() {
        if (trigger == null) {
            trigger = new CCronTrigger(crawlerProperties.getProperty(
                    Constants.CRON_EXPRESSION_PROPERTY,
                    Constants.DEFAULT_CRON_EXPRESSION));
        } else {
            trigger.setExpression(crawlerProperties.getProperty(
                    Constants.CRON_EXPRESSION_PROPERTY,
                    Constants.DEFAULT_CRON_EXPRESSION));
        }
        return trigger;
    }

    public void setExpression(final String cronExpression) {
        if (trigger != null) {
            trigger.setExpression(cronExpression);
        }
    }

    public String getExpression() {
        if (trigger != null) {
            trigger.getCronExpression();
        }
        return null;
    }

    public void doExecute() {
        final SystemHelper systemHelper = SingletonS2Container
                .getComponent("systemHelper");
        if (systemHelper.readyCrawlProcess()) {
            systemHelper.setForceStop(false);

            // create session id
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            final String sessionId = sdf.format(new Date());
            systemHelper.setSessionId(sessionId);
            // store crawling session
            final CrawlingSessionHelper crawlingSessionHelper = SingletonS2Container
                    .getComponent("crawlingSessionHelper");
            try {
                crawlingSessionHelper.store(sessionId);
                SingletonS2Container.getComponent(SystemHelper.class)
                        .executeCrawler(sessionId);
            } finally {
                systemHelper.finishCrawlProcess();
            }
        } else {
            logger.warn("Crawler is running now.");
        }
    }

    public void catchException(final Exception e) {
        logger.error("Failed to execute crawl task.", e);
    }

}
