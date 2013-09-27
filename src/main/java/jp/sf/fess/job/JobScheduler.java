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

package jp.sf.fess.job;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.ArrayList;
import java.util.List;

import jp.sf.fess.Constants;
import jp.sf.fess.db.cbean.ScheduledJobCB;
import jp.sf.fess.db.exbhv.ScheduledJobBhv;
import jp.sf.fess.db.exentity.ScheduledJob;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.container.annotation.tiger.DestroyMethod;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobScheduler {
    private static final Logger logger = LoggerFactory
            .getLogger(JobScheduler.class);

    private static final String TRIGGER_ID_PREFIX = "trigger";

    private Scheduler scheduler;

    public Class<? extends Job> jobClass = TriggeredJob.class;

    public List<String> targetList;

    @InitMethod
    public void init() {
        final SchedulerFactory sf = new StdSchedulerFactory();
        try {
            scheduler = sf.getScheduler();
            scheduler.start();
        } catch (final SchedulerException e) {
            throw new ScheduledJobException("Failed to start a scheduler.", e);
        }

        final List<String> list = new ArrayList<String>();
        list.add(Constants.DEFAULT_JOB_TARGET);
        if (targetList != null) {
            list.addAll(targetList);
        }
        final ScheduledJobCB cb = new ScheduledJobCB();
        if (targetList != null) {
            cb.query().setTarget_InScope(targetList);
        }
        cb.query().setDeletedBy_IsNull();
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_Name_Asc();
        final List<ScheduledJob> scheduledJobList = SingletonS2Container
                .getComponent(ScheduledJobBhv.class).selectList(cb);
        for (final ScheduledJob scheduledJob : scheduledJobList) {
            register(scheduledJob);
        }
    }

    @DestroyMethod
    public void destroy() {
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

        final String target = scheduledJob.getTarget();
        if (targetList != null) {
            if (!Constants.DEFAULT_JOB_TARGET.equals(target)
                    && !targetList.contains(target)) {
                logger.warn("Ignore Job " + scheduledJob.getId()
                        + " because of not target: " + scheduledJob.getTarget());
                return;
            }
        } else {
            if (!Constants.DEFAULT_JOB_TARGET.equals(target)) {
                logger.warn("Ignore Job " + scheduledJob.getId()
                        + " because of not target: " + scheduledJob.getTarget());
                return;
            }
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
        final JobDetail jobDetail = newJob(jobClass).withIdentity(jobId)
                .usingJobData(jobDataMap).build();

        final Trigger trigger = newTrigger().withIdentity(triggerId)
                .withSchedule(cronSchedule(scheduledJob.getCronExpression()))
                .startNow().build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (final SchedulerException e) {
            throw new ScheduledJobException("Failed to add Job: "
                    + scheduledJob, e);
        }
    }

    public void unregister(final ScheduledJob scheduledJob) {
        final String jobId = Constants.JOB_ID_PREFIX + scheduledJob.getId();
        try {
            scheduler.deleteJob(jobKey(jobId));
        } catch (final SchedulerException e) {
            throw new ScheduledJobException("Failed to delete Job: "
                    + scheduledJob, e);
        }
    }
}
