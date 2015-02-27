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

package jp.sf.fess.job;

import java.sql.Timestamp;

import jp.sf.fess.Constants;
import jp.sf.fess.db.exentity.JobLog;
import jp.sf.fess.db.exentity.ScheduledJob;
import jp.sf.fess.helper.JobHelper;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.service.JobLogService;
import jp.sf.fess.util.ComponentUtil;

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

    @Override
    public void execute(final JobExecutionContext context)
            throws JobExecutionException {
        final JobDataMap data = context.getMergedJobDataMap();
        final ScheduledJob scheduledJob = (ScheduledJob) data
                .get(Constants.SCHEDULED_JOB);

        execute(scheduledJob);
    }

    public void execute(final ScheduledJob scheduledJob) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final JobHelper jobHelper = ComponentUtil.getJobHelper();
        final JobLog jobLog = new JobLog(scheduledJob);
        final String scriptType = scheduledJob.getScriptType();
        final String script = scheduledJob.getScriptData();
        final Long id = scheduledJob.getId();
        final String jobId = Constants.JOB_ID_PREFIX + id;
        final JobExecutor jobExecutor = ComponentUtil
                .getJobExecutor(scriptType);
        if (jobExecutor == null) {
            throw new ScheduledJobException("No jobExecutor: " + scriptType);
        }

        if (jobHelper.startJobExecutoer(id, jobExecutor) != null) {
            if (logger.isDebugEnabled()) {
                logger.debug(jobId + " is running.");
            }
            return;
        }

        try {
            if (scheduledJob.isLoggingEnabled()) {
                storeJobLog(jobLog);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Starting Job " + jobId + ". scriptType: "
                        + scriptType + ", script: " + script);
            } else if (scheduledJob.isLoggingEnabled()
                    && logger.isInfoEnabled()) {
                logger.info("Starting Job " + jobId + ".");
            }

            final Object ret = jobExecutor.execute(script);
            if (ret == null) {
                if (scheduledJob.isLoggingEnabled() && logger.isInfoEnabled()) {
                    logger.info("Finished Job " + jobId + ".");
                }
            } else {
                if (scheduledJob.isLoggingEnabled() && logger.isInfoEnabled()) {
                    logger.info("Finished Job " + jobId
                            + ". The return value is:\n" + ret);
                }
                jobLog.setScriptResult(ret.toString());
            }
            jobLog.setJobStatus(Constants.OK);
        } catch (final Throwable t) { // NOPMD
            logger.error("Failed to execute " + jobId + ": " + script, t);
            jobLog.setJobStatus(Constants.FAIL);
            jobLog.setScriptResult(systemHelper.abbreviateLongText(t
                    .getLocalizedMessage()));
        } finally {
            jobHelper.finishJobExecutoer(id);
            jobLog.setEndTime(new Timestamp(System.currentTimeMillis()));
            if (logger.isDebugEnabled()) {
                logger.debug("jobLog: " + jobLog);
            }
            if (scheduledJob.isLoggingEnabled()) {
                storeJobLog(jobLog);
            }
        }
    }

    private void storeJobLog(final JobLog jobLog) {
        final JobLogService jobLogService = SingletonS2Container
                .getComponent(JobLogService.class);
        jobLogService.store(jobLog);
    }

}
