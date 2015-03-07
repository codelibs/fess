/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.ArrayList;
import java.util.List;

import org.codelibs.core.util.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.db.exbhv.ScheduledJobBhv;
import org.codelibs.fess.db.exentity.ScheduledJob;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.seasar.framework.container.annotation.tiger.DestroyMethod;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobScheduler {
    private static final Logger logger = LoggerFactory.getLogger(JobScheduler.class);

    private static final String TRIGGER_ID_PREFIX = "trigger";

    private Scheduler scheduler;

    public Class<? extends Job> jobClass = TriggeredJob.class;

    public List<String> targetList = new ArrayList<String>();

    @InitMethod
    public void init() {
        final SchedulerFactory sf = new StdSchedulerFactory();
        try {
            scheduler = sf.getScheduler();
            scheduler.start();
        } catch (final SchedulerException e) {
            throw new ScheduledJobException("Failed to start a scheduler.", e);
        }

        ComponentUtil.getComponent(ScheduledJobBhv.class).selectCursor(cb -> {
            cb.query().setAvailable_Equal(Constants.T);
            cb.query().setDeletedBy_IsNull();
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
        }, scheduledJob -> register(scheduledJob));
    }

    @DestroyMethod
    public void destroy() {
        final JobHelper jobHelper = ComponentUtil.getJobHelper();
        for (final String sessionId : jobHelper.getRunningSessionIdSet()) {
            jobHelper.destroyCrawlerProcess(sessionId);
        }
        try {
            scheduler.shutdown(true);
        } catch (final SchedulerException e) {
            logger.error("Failed to shutdown the scheduler.", e);
        }
    }

    public void register(final ScheduledJob scheduledJob) {
        if (scheduledJob == null) {
            throw new ScheduledJobException("No job.");
        }

        if (!Constants.T.equals(scheduledJob.getAvailable())) {
            logger.info("Inactive Job " + scheduledJob.getId() + ":" + scheduledJob.getName());
            try {
                unregister(scheduledJob);
            } catch (final Exception e) {
                // ignore
            }
            return;
        }

        final String target = scheduledJob.getTarget();
        if (!isTarget(target)) {
            logger.info("Ignore Job " + scheduledJob.getId() + ":" + scheduledJob.getName() + " because of not target: "
                    + scheduledJob.getTarget());
            return;
        }

        String scriptType = scheduledJob.getScriptType();
        if (scriptType == null) {
            scriptType = "groovy";
        }

        final Long id = scheduledJob.getId();
        final String jobId = Constants.JOB_ID_PREFIX + id;
        final String triggerId = TRIGGER_ID_PREFIX + id;

        final JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(Constants.SCHEDULED_JOB, scheduledJob);
        jobDataMap.put(Constants.JOB_EXECUTOR_TYPE, scriptType);
        final JobDetail jobDetail = newJob(jobClass).withIdentity(jobId).usingJobData(jobDataMap).build();

        final Trigger trigger =
                newTrigger().withIdentity(triggerId).withSchedule(cronSchedule(scheduledJob.getCronExpression())).startNow().build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (final SchedulerException e) {
            throw new ScheduledJobException("Failed to add Job: " + scheduledJob, e);
        }

        logger.info("Starting Job " + scheduledJob.getId() + ":" + scheduledJob.getName());

    }

    public void unregister(final ScheduledJob scheduledJob) {
        final String jobId = Constants.JOB_ID_PREFIX + scheduledJob.getId();
        try {
            scheduler.deleteJob(jobKey(jobId));
        } catch (final SchedulerException e) {
            throw new ScheduledJobException("Failed to delete Job: " + scheduledJob, e);
        }
    }

    protected boolean isTarget(final String target) {
        if (StringUtil.isBlank(target)) {
            return true;
        }

        final String[] targets = target.split(",");
        for (String name : targets) {
            name = name.trim();
            if (Constants.DEFAULT_JOB_TARGET.equals(name) || targetList.contains(name)) {
                return true;
            }
        }
        return false;
    }
}
