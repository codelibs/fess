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
package org.codelibs.fess.helper;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.core.timer.TimeoutTarget;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.Constants;
import org.codelibs.fess.exception.ScheduledJobException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.exbhv.JobLogBhv;
import org.codelibs.fess.opensearch.config.exbhv.ScheduledJobBhv;
import org.codelibs.fess.opensearch.config.exentity.JobLog;
import org.codelibs.fess.opensearch.config.exentity.ScheduledJob;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.job.JobManager;
import org.lastaflute.job.LaCron;
import org.lastaflute.job.LaJobRuntime;
import org.lastaflute.job.LaScheduledJob;
import org.lastaflute.job.key.LaJobUnique;
import org.lastaflute.job.subsidiary.CronParamsSupplier;

/**
 * Helper class for managing scheduled jobs within the Fess system.
 * This class provides functionality for registering, unregistering, and monitoring scheduled jobs.
 */
public class JobHelper {
    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(JobHelper.class);

    /**
     * Default constructor.
     */
    public JobHelper() {
        // Default constructor
    }

    /** Monitor interval in seconds (default: 1 hour) */
    protected int monitorInterval = 60 * 60;// 1hour

    /** Thread-local storage for job runtime information */
    protected ThreadLocal<LaJobRuntime> jobRuntimeLocal = new ThreadLocal<>();

    /**
     * Registers a scheduled job with the job manager.
     *
     * @param scheduledJob the scheduled job to register
     */
    public void register(final ScheduledJob scheduledJob) {
        final JobManager jobManager = ComponentUtil.getJobManager();
        jobManager.schedule(cron -> register(cron, scheduledJob));
    }

    /**
     * Registers a scheduled job with the specified cron scheduler.
     *
     * @param cron the cron scheduler to use
     * @param scheduledJob the scheduled job to register
     */
    public void register(final LaCron cron, final ScheduledJob scheduledJob) {
        if (scheduledJob == null) {
            throw new ScheduledJobException("No job.");
        }

        final String id = scheduledJob.getId();
        if (!Constants.T.equals(scheduledJob.getAvailable())) {
            logger.info("Inactive Job {}:{}", id, scheduledJob.getName());
            try {
                unregister(scheduledJob);
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to delete Job {}", scheduledJob, e);
                }
            }
            return;
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final CronParamsSupplier paramsOp = () -> {
            final Map<String, Object> params = new HashMap<>();
            ComponentUtil.getComponent(ScheduledJobBhv.class).selectByPK(scheduledJob.getId())
                    .ifPresent(e -> params.put(Constants.SCHEDULED_JOB, e)).orElse(() -> {
                        logger.warn("Job {} is not found.", scheduledJob.getId());
                    });
            return params;
        };
        findJobByUniqueOf(LaJobUnique.of(id)).ifPresent(job -> {
            if (!job.isUnscheduled()) {
                if (StringUtil.isNotBlank(scheduledJob.getCronExpression())) {
                    logger.info("Starting Job {}:{}", id, scheduledJob.getName());
                    final String cronExpression = scheduledJob.getCronExpression();
                    job.reschedule(cronExpression, op -> op.changeNoticeLogToDebug().params(paramsOp));
                } else {
                    logger.info("Inactive Job {}:{}", id, scheduledJob.getName());
                    job.becomeNonCron();
                }
            } else if (StringUtil.isNotBlank(scheduledJob.getCronExpression())) {
                logger.info("Starting Job {}:{}", id, scheduledJob.getName());
                final String cronExpression = scheduledJob.getCronExpression();
                job.reschedule(cronExpression, op -> op.changeNoticeLogToDebug().params(paramsOp));
            }
        }).orElse(() -> {
            if (StringUtil.isNotBlank(scheduledJob.getCronExpression())) {
                logger.info("Starting Job {}:{}", id, scheduledJob.getName());
                final String cronExpression = scheduledJob.getCronExpression();
                cron.register(cronExpression, fessConfig.getSchedulerJobClassAsClass(), fessConfig.getSchedulerConcurrentExecModeAsEnum(),
                        op -> op.uniqueBy(id).changeNoticeLogToDebug().params(paramsOp));
            } else {
                logger.info("Inactive Job {}:{}", id, scheduledJob.getName());
                cron.registerNonCron(fessConfig.getSchedulerJobClassAsClass(), fessConfig.getSchedulerConcurrentExecModeAsEnum(),
                        op -> op.uniqueBy(id).changeNoticeLogToDebug().params(paramsOp));
            }
        });
    }

    /**
     * Finds a scheduled job by its unique identifier.
     *
     * @param jobUnique the unique identifier of the job
     * @return an optional containing the scheduled job if found, empty otherwise
     */
    private OptionalThing<LaScheduledJob> findJobByUniqueOf(final LaJobUnique jobUnique) {
        final JobManager jobManager = ComponentUtil.getJobManager();
        try {
            return jobManager.findJobByUniqueOf(jobUnique);
        } catch (final Exception e) {
            return OptionalThing.empty();
        }
    }

    /**
     * Unregisters a scheduled job from the job manager.
     *
     * @param scheduledJob the scheduled job to unregister
     * @throws ScheduledJobException if the job cannot be unregistered
     */
    public void unregister(final ScheduledJob scheduledJob) {
        try {
            final JobManager jobManager = ComponentUtil.getJobManager();
            if (jobManager.isSchedulingDone()) {
                jobManager.findJobByUniqueOf(LaJobUnique.of(scheduledJob.getId())).ifPresent(job -> {
                    job.unschedule();
                }).orElse(() -> logger.debug("Job {} is not scheduled.", scheduledJob.getId()));
            }
        } catch (final Exception e) {
            throw new ScheduledJobException("Failed to delete Job: " + scheduledJob, e);
        }
    }

    /**
     * Removes a scheduled job completely from the job manager.
     *
     * @param scheduledJob the scheduled job to remove
     * @throws ScheduledJobException if the job cannot be removed
     */
    public void remove(final ScheduledJob scheduledJob) {
        try {
            final JobManager jobManager = ComponentUtil.getJobManager();
            if (jobManager.isSchedulingDone()) {
                jobManager.findJobByUniqueOf(LaJobUnique.of(scheduledJob.getId())).ifPresent(job -> {
                    job.disappear();
                }).orElse(() -> logger.debug("Job {} is not scheduled.", scheduledJob.getId()));
            }
        } catch (final Exception e) {
            throw new ScheduledJobException("Failed to delete Job: " + scheduledJob, e);
        }
    }

    /**
     * Checks if a job with the specified ID is available.
     *
     * @param id the job ID to check
     * @return true if the job is available, false otherwise
     */
    public boolean isAvailable(final String id) {
        return ComponentUtil.getComponent(ScheduledJobBhv.class).selectByPK(id).filter(e -> Boolean.TRUE.equals(e.getAvailable()))
                .isPresent();
    }

    /**
     * Stores a job log entry in the database.
     *
     * @param jobLog the job log entry to store
     */
    public void store(final JobLog jobLog) {
        ComponentUtil.getComponent(JobLogBhv.class).insertOrUpdate(jobLog, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });
    }

    /**
     * Starts a monitor task for tracking job execution.
     *
     * @param jobLog the job log to monitor
     * @return the timeout task for monitoring
     */
    public TimeoutTask startMonitorTask(final JobLog jobLog) {
        final TimeoutTarget target = new MonitorTarget(jobLog);
        return TimeoutManager.getInstance().addTimeoutTarget(target, monitorInterval, true);
    }

    /**
     * Sets the monitor interval for job monitoring.
     *
     * @param monitorInterval the monitor interval in seconds
     */
    public void setMonitorInterval(final int monitorInterval) {
        this.monitorInterval = monitorInterval;
    }

    /**
     * Inner class that implements TimeoutTarget for monitoring job execution.
     */
    static class MonitorTarget implements TimeoutTarget {

        /** The job log being monitored */
        private final JobLog jobLog;

        /**
         * Constructor for MonitorTarget.
         *
         * @param jobLog the job log to monitor
         */
        public MonitorTarget(final JobLog jobLog) {
            this.jobLog = jobLog;
        }

        /**
         * Called when the timeout expires. Updates the job log if the job is still running.
         */
        @Override
        public void expired() {
            if (jobLog.getEndTime() == null) {
                jobLog.setLastUpdated(ComponentUtil.getSystemHelper().getCurrentTimeAsLong());
                if (logger.isDebugEnabled()) {
                    logger.debug("Update {}", jobLog);
                }
                ComponentUtil.getComponent(JobLogBhv.class).insertOrUpdate(jobLog, op -> {
                    op.setRefreshPolicy(Constants.TRUE);
                });
            }
        }

    }

    /**
     * Sets the job runtime for the current thread.
     *
     * @param runtime the job runtime to set
     */
    public void setJobRuntime(final LaJobRuntime runtime) {
        jobRuntimeLocal.set(runtime);
    }

    /**
     * Gets the job runtime for the current thread.
     *
     * @return the job runtime for the current thread
     */
    public LaJobRuntime getJobRuntime() {
        return jobRuntimeLocal.get();
    }
}
