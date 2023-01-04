/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.Constants;
import org.codelibs.fess.es.config.exentity.JobLog;
import org.codelibs.fess.es.config.exentity.ScheduledJob;
import org.codelibs.fess.exception.ScheduledJobException;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.job.JobExecutor;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.job.JobManager;
import org.lastaflute.job.LaJob;
import org.lastaflute.job.LaJobRuntime;
import org.lastaflute.job.key.LaJobUnique;

public class ScriptExecutorJob implements LaJob {
    private static final Logger logger = LogManager.getLogger(ScriptExecutorJob.class);

    @Override
    public void run(final LaJobRuntime runtime) {
        final JobHelper jobHelper = ComponentUtil.getJobHelper();
        try {
            jobHelper.setJobRuntime(runtime);
            process(runtime);
        } finally {
            jobHelper.setJobRuntime(null);
        }
    }

    protected void process(final LaJobRuntime runtime) {
        if (!runtime.getParameterMap().containsKey(Constants.SCHEDULED_JOB)) {
            logger.warn("{} is empty.", Constants.SCHEDULED_JOB);
            return;
        }
        runtime.stopIfNeeds();

        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final JobManager jobManager = ComponentUtil.getJobManager();
        final ScheduledJob scheduledJob = (ScheduledJob) runtime.getParameterMap().get(Constants.SCHEDULED_JOB);
        final String id = scheduledJob.getId();
        final String target = scheduledJob.getTarget();
        if (!ComponentUtil.getFessConfig().isSchedulerTarget(target)) {
            logger.info("Ignore Job {}:{} because of not target: {}", scheduledJob.getName(), id, scheduledJob.getTarget());
            return;
        }

        final JobHelper jobHelper = ComponentUtil.getJobHelper();
        if (!jobHelper.isAvailable(id)) {
            logger.info("Job {} is unavailable. Unregistering this job.", id);
            jobHelper.unregister(scheduledJob);
            return;
        }

        final JobLog jobLog = new JobLog(scheduledJob);
        final String scriptType = scheduledJob.getScriptType();
        final String script = scheduledJob.getScriptData();

        final JobExecutor jobExecutor = ComponentUtil.getJobExecutor(scriptType);
        if (jobExecutor == null) {
            throw new ScheduledJobException("No jobExecutor: " + scriptType);
        }

        if (!jobManager.findJobByUniqueOf(LaJobUnique.of(id)).isPresent()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Job {} is running.", id);
            }
            return;
        }

        TimeoutTask task = null;
        try {
            if (scheduledJob.isLoggingEnabled()) {
                jobHelper.store(jobLog);
                task = jobHelper.startMonitorTask(jobLog);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Starting Job {}. scriptType: {}, script: {}", id, scriptType, script);
            } else if (scheduledJob.isLoggingEnabled() && logger.isInfoEnabled()) {
                logger.info("Starting Job {}.", id);
            }

            final Object ret = jobExecutor.execute(Constants.DEFAULT_SCRIPT, script);
            if (ret == null) {
                if (scheduledJob.isLoggingEnabled() && logger.isInfoEnabled()) {
                    logger.info("Finished Job {}.", id);
                }
            } else {
                if (scheduledJob.isLoggingEnabled() && logger.isInfoEnabled()) {
                    logger.info("Finished Job {}. The return value is:\n{}", id, ret);
                }
                jobLog.setScriptResult(ret.toString());
            }
            jobLog.setJobStatus(Constants.OK);
        } catch (final Throwable t) {
            logger.warn("Failed to execute {}: {}", id, script, t);
            jobLog.setJobStatus(Constants.FAIL);
            jobLog.setScriptResult(systemHelper.abbreviateLongText(t.getLocalizedMessage()));
        } finally {
            if (task != null) {
                try {
                    task.stop();
                } catch (final Exception e) {
                    logger.warn("Failed to stop {}", jobLog, e);
                }
            }
            jobLog.setEndTime(ComponentUtil.getSystemHelper().getCurrentTimeAsLong());
            if (logger.isDebugEnabled()) {
                logger.debug("jobLog: {}", jobLog);
            }
            if (scheduledJob.isLoggingEnabled()) {
                jobHelper.store(jobLog);
            }
        }
    }

}
