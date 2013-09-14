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

import javax.annotation.Resource;

import jp.sf.fess.Constants;
import jp.sf.fess.service.SearchFieldLogService;

import org.codelibs.core.util.DynamicProperties;
import org.seasar.chronos.core.TaskTrigger;
import org.seasar.chronos.core.annotation.task.Task;
import org.seasar.chronos.core.trigger.CCronTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Task
public class HourlyTask implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory
            .getLogger(HourlyTask.class);

    @Resource
    protected DynamicProperties crawlerProperties;

    @Resource
    protected SearchFieldLogService searchFieldLogService;

    private CCronTrigger trigger;

    public TaskTrigger getTrigger() {
        if (trigger == null) {
            trigger = new CCronTrigger(crawlerProperties.getProperty(
                    Constants.HOURLY_CRON_EXPRESSION_PROPERTY,
                    Constants.DEFAULT_HOURLY_CRON_EXPRESSION));
        } else {
            trigger.setExpression(crawlerProperties.getProperty(
                    Constants.HOURLY_CRON_EXPRESSION_PROPERTY,
                    Constants.DEFAULT_HOURLY_CRON_EXPRESSION));
        }
        return trigger;
    }

    public void doExecute() {
        // update stats fields
        searchFieldLogService.updateFieldLabels();
    }

    public void catchException(final Exception e) {
        logger.error("Failed to execute search log task.", e);
    }
}
