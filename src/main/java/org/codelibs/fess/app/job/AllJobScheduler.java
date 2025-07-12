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
package org.codelibs.fess.app.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.logic.AccessContextLogic;
import org.codelibs.fess.app.service.ScheduledJobService;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.exbhv.JobLogBhv;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.core.time.TimeManager;
import org.lastaflute.job.LaCron;
import org.lastaflute.job.LaJob;
import org.lastaflute.job.LaJobRunner;
import org.lastaflute.job.LaJobScheduler;

import jakarta.annotation.Resource;

/**
 * Job scheduler for managing all scheduled jobs in Fess.
 * Implements LaJobScheduler to handle job scheduling and execution.
 */
public class AllJobScheduler implements LaJobScheduler {

    private static final Logger logger = LogManager.getLogger(AllJobScheduler.class);

    /** Application type identifier for job context */
    protected static final String APP_TYPE = "JOB";

    /**
     * Default constructor.
     */
    public AllJobScheduler() {
        // Default constructor
    }

    @Resource
    private TimeManager timeManager;

    @Resource
    private FessConfig fessConfig;

    @Resource
    private AccessContextLogic accessContextLogic;

    @Resource
    private ScheduledJobService scheduledJobService;

    @Resource
    private SystemHelper systemHelper;

    @Resource
    private JobHelper jobHelper;

    /** The job class to be executed by this scheduler */
    protected Class<? extends LaJob> jobClass = ScriptExecutorJob.class;

    /** The timestamp when the scheduler was last updated */
    protected long schedulerTime;

    @Override
    public void schedule(final LaCron cron) {
        schedulerTime = systemHelper.getCurrentTimeAsLong();
        scheduledJobService.start(cron);

        final String myName = fessConfig.getSchedulerTargetName();
        if (StringUtil.isNotBlank(myName)) {
            ComponentUtil.getComponent(JobLogBhv.class).queryDelete(cb -> {
                cb.query().setJobStatus_Equal(Constants.RUNNING);
                cb.query().setTarget_Equal(myName);
            });
        }

        TimeoutManager.getInstance().addTimeoutTarget(() -> {
            if (logger.isDebugEnabled()) {
                logger.debug("Updating scheduled jobs. time:{}", schedulerTime);
            }
            final long now = systemHelper.getCurrentTimeAsLong();
            scheduledJobService.getScheduledJobListAfter(schedulerTime).forEach(scheduledJob -> {
                if (logger.isDebugEnabled()) {
                    logger.debug("Updating job schedule:{}", scheduledJob.getName());
                }
                try {
                    jobHelper.register(scheduledJob);
                } catch (final Exception e) {
                    logger.warn("Failed to update schdule {}", scheduledJob, e);
                }
            });
            schedulerTime = now;
        }, fessConfig.getSchedulerMonitorIntervalAsInteger(), true);
    }

    @Override
    public LaJobRunner createRunner() {
        return new LaJobRunner().useAccessContext(
                resource -> accessContextLogic.create(resource, OptionalThing::empty, OptionalThing::empty, () -> APP_TYPE));
    }

    /**
     * Sets the job class to be executed by this scheduler.
     *
     * @param jobClass the job class to set
     */
    public void setJobClass(final Class<? extends LaJob> jobClass) {
        this.jobClass = jobClass;
    }
}
