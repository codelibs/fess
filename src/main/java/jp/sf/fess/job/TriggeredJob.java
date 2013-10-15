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

import java.sql.Timestamp;

import jp.sf.fess.Constants;
import jp.sf.fess.db.exentity.JobLog;
import jp.sf.fess.db.exentity.ScheduledJob;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.service.JobLogService;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.seasar.framework.container.SingletonS2Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TriggeredJob implements Job {
    private static final Logger logger = LoggerFactory
            .getLogger(TriggeredJob.class);

    private static final String JOB_EXECUTOR_SUFFIX = "JobExecutor";

    @Override
    public void execute(final JobExecutionContext context)
            throws JobExecutionException {
        final JobDataMap data = context.getMergedJobDataMap();
        final ScheduledJob scheduledJob = (ScheduledJob) data
                .get(Constants.SCHEDULED_JOB);

        execute(scheduledJob);
    }

    public void execute(final ScheduledJob scheduledJob) {
        final SystemHelper systemHelper = SingletonS2Container
                .getComponent(SystemHelper.class);
        final JobLog jobLog = new JobLog(scheduledJob);
        final String scriptType = scheduledJob.getScriptType();
        final String script = scheduledJob.getScriptData();
        final Long id = scheduledJob.getId();
        final String jobId = Constants.JOB_ID_PREFIX + id;
        final JobExecutor jobExecutor = SingletonS2Container
                .getComponent(scriptType + JOB_EXECUTOR_SUFFIX);
        if (jobExecutor == null) {
            throw new ScheduledJobException("No jobExecutor: " + scriptType
                    + JOB_EXECUTOR_SUFFIX);
        }

        if (systemHelper.startJobExecutoer(id, jobExecutor) != null) {
            logger.info(jobId + " is running.");
            return;
        }

        final JobLogService jobLogService = SingletonS2Container
                .getComponent(JobLogService.class);
        try {
            if (scheduledJob.isLoggingEnabled()) {
                jobLogService.store(jobLog);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Job " + jobId + "/" + scriptType
                        + " is executing: " + script);
            }

            final Object ret = jobExecutor.execute(script);
            if (ret == null) {
                logger.info("Job " + jobId + " is no response.");
            } else {
                logger.info("Job " + jobId + " returns " + ret);
                jobLog.setScriptResult(ret.toString());
            }
            jobLog.setJobStatus(Constants.OK);
        } catch (final Throwable e) {
            logger.error("Failed to execute " + jobId + ": " + script, e);
            jobLog.setJobStatus(Constants.FAIL);
            jobLog.setScriptResult(e.getLocalizedMessage());
        } finally {
            systemHelper.finishJobExecutoer(id);
            jobLog.setEndTime(new Timestamp(System.currentTimeMillis()));
            if (logger.isDebugEnabled()) {
                logger.debug("jobLog: " + jobLog);
            }
            if (scheduledJob.isLoggingEnabled()) {
                jobLogService.store(jobLog);
            }
        }
    }

}
