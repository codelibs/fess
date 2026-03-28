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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.Constants;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.job.JobExecutor;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.exentity.JobLog;
import org.codelibs.fess.opensearch.config.exentity.ScheduledJob;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.job.LaJob;
import org.lastaflute.job.LaScheduledJob;
import org.lastaflute.job.JobManager;
import org.lastaflute.job.key.LaJobUnique;
import org.lastaflute.job.mock.MockJobRuntime;

public class ScriptExecutorJobTest extends UnitFessTestCase {

    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.register(new SystemHelper(), "systemHelper");
    }

    public void test_ScriptExecutorJob_implementsLaJob() {
        final ScriptExecutorJob job = new ScriptExecutorJob();
        assertTrue(job instanceof LaJob);
    }

    public void test_ScriptExecutorJob_creation() {
        final ScriptExecutorJob job = new ScriptExecutorJob();
        assertNotNull(job);
    }

    // ===================================================================================
    //                                                   process() with JOB_LOG_ID
    //                                                   ============================

    @Test
    public void test_process_withJobLogId_setsIdOnJobLog() {
        final ScheduledJob scheduledJob = createTestScheduledJob("proc-1", true);
        final AtomicReference<JobLog> storedJobLog = new AtomicReference<>();

        registerComponents(scheduledJob, storedJobLog);

        // Create runtime with JOB_LOG_ID parameter
        final Map<String, Object> params = new HashMap<>();
        params.put(Constants.SCHEDULED_JOB, scheduledJob);
        params.put(Constants.JOB_LOG_ID, "pregenerated-abc123");

        final MockJobRuntime runtime = MockJobRuntime.of(ScriptExecutorJob.class, op -> op.params(() -> params));

        // Execute the real process() method
        final TestableScriptExecutorJob job = new TestableScriptExecutorJob();
        job.process(runtime);

        // Verify the JobLog was stored with the pre-generated ID
        assertNotNull(storedJobLog.get());
        assertEquals("pregenerated-abc123", storedJobLog.get().getId());
        assertEquals(Constants.OK, storedJobLog.get().getJobStatus());
    }

    @Test
    public void test_process_withoutJobLogId_idRemainsNull() {
        final ScheduledJob scheduledJob = createTestScheduledJob("proc-2", true);
        final AtomicReference<JobLog> storedJobLog = new AtomicReference<>();

        registerComponents(scheduledJob, storedJobLog);

        // Create runtime WITHOUT JOB_LOG_ID parameter
        final Map<String, Object> params = new HashMap<>();
        params.put(Constants.SCHEDULED_JOB, scheduledJob);

        final MockJobRuntime runtime = MockJobRuntime.of(ScriptExecutorJob.class, op -> op.params(() -> params));

        final TestableScriptExecutorJob job = new TestableScriptExecutorJob();
        job.process(runtime);

        // Verify the JobLog was stored without a pre-set ID
        assertNotNull(storedJobLog.get());
        assertNull(storedJobLog.get().getId());
        assertEquals(Constants.OK, storedJobLog.get().getJobStatus());
    }

    @Test
    public void test_process_withNullJobLogId_idRemainsNull() {
        final ScheduledJob scheduledJob = createTestScheduledJob("proc-3", true);
        final AtomicReference<JobLog> storedJobLog = new AtomicReference<>();

        registerComponents(scheduledJob, storedJobLog);

        final Map<String, Object> params = new HashMap<>();
        params.put(Constants.SCHEDULED_JOB, scheduledJob);
        params.put(Constants.JOB_LOG_ID, null);

        final MockJobRuntime runtime = MockJobRuntime.of(ScriptExecutorJob.class, op -> op.params(() -> params));

        final TestableScriptExecutorJob job = new TestableScriptExecutorJob();
        job.process(runtime);

        assertNotNull(storedJobLog.get());
        assertNull(storedJobLog.get().getId());
    }

    @Test
    public void test_process_loggingDisabled_noJobLogStored() {
        final ScheduledJob scheduledJob = createTestScheduledJob("proc-4", false);
        final AtomicReference<JobLog> storedJobLog = new AtomicReference<>();

        registerComponents(scheduledJob, storedJobLog);

        final Map<String, Object> params = new HashMap<>();
        params.put(Constants.SCHEDULED_JOB, scheduledJob);
        params.put(Constants.JOB_LOG_ID, "should-not-be-stored");

        final MockJobRuntime runtime = MockJobRuntime.of(ScriptExecutorJob.class, op -> op.params(() -> params));

        final TestableScriptExecutorJob job = new TestableScriptExecutorJob();
        job.process(runtime);

        // When logging is disabled, store() is never called
        assertNull(storedJobLog.get());
    }

    // ===================================================================================
    //                                                                       Helpers
    //                                                                       ========

    private ScheduledJob createTestScheduledJob(final String id, final boolean loggingEnabled) {
        final ScheduledJob scheduledJob = new ScheduledJob();
        scheduledJob.setId(id);
        scheduledJob.setName("Test Job");
        scheduledJob.setScriptType("groovy");
        scheduledJob.setScriptData("println 'test'");
        scheduledJob.setJobLogging(loggingEnabled ? Constants.T : Constants.F);
        scheduledJob.setAvailable(Constants.T);
        scheduledJob.setTarget("all");
        return scheduledJob;
    }

    private void registerComponents(final ScheduledJob scheduledJob, final AtomicReference<JobLog> storedJobLog) {
        // FessConfig that accepts our target
        final FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public boolean isSchedulerTarget(final String target) {
                return true;
            }

            @Override
            public String getSchedulerTargetName() {
                return Constants.DEFAULT_JOB_TARGET;
            }
        };
        ComponentUtil.setFessConfig(fessConfig);

        // JobManager that finds our job
        final JobManager jobManager = new JobManager() {
            @Override
            public OptionalThing<LaScheduledJob> findJobByUniqueOf(final LaJobUnique jobUnique) {
                return OptionalThing.of(new MinimalLaScheduledJob());
            }

            @Override
            public OptionalThing<LaScheduledJob> findJobByKey(final org.lastaflute.job.key.LaJobKey jobKey) {
                return OptionalThing.empty();
            }

            @Override
            public boolean isSchedulingDone() {
                return true;
            }

            @Override
            public void schedule(final org.lastaflute.job.subsidiary.CronConsumer cron) {
            }

            @Override
            public java.util.List<LaScheduledJob> getJobList() {
                return Collections.emptyList();
            }

            @Override
            public java.util.List<org.lastaflute.job.LaJobHistory> searchJobHistoryList() {
                return Collections.emptyList();
            }

            @Override
            public void reboot() {
            }

            @Override
            public void destroy() {
            }
        };
        ComponentUtil.register(jobManager, JobManager.class.getCanonicalName());

        // JobHelper that captures stored JobLog
        final JobHelper jobHelper = new JobHelper() {
            @Override
            public boolean isAvailable(final String id) {
                return true;
            }

            @Override
            public void store(final JobLog jobLog) {
                storedJobLog.set(jobLog);
            }

            @Override
            public TimeoutTask startMonitorTask(final JobLog jobLog) {
                return null;
            }

            @Override
            public void unregister(final ScheduledJob scheduledJob) {
            }
        };
        ComponentUtil.register(jobHelper, "jobHelper");

        // JobExecutor that returns a simple result
        final JobExecutor jobExecutor = new JobExecutor() {
            @Override
            public Object execute(final String scriptType, final String script) {
                return "OK";
            }
        };
        ComponentUtil.register(jobExecutor, "scriptJobExecutor");
    }

    /**
     * Testable subclass that exposes the protected process() method.
     */
    private static class TestableScriptExecutorJob extends ScriptExecutorJob {
        @Override
        public void process(final org.lastaflute.job.LaJobRuntime runtime) {
            super.process(runtime);
        }
    }

    /**
     * Minimal LaScheduledJob stub for JobManager.findJobByUniqueOf().
     */
    private static class MinimalLaScheduledJob implements LaScheduledJob {
        @Override
        public org.lastaflute.job.subsidiary.LaunchedProcess launchNow() {
            return null;
        }

        @Override
        public org.lastaflute.job.subsidiary.LaunchedProcess launchNow(final org.lastaflute.job.subsidiary.LaunchNowOpCall opLambda) {
            return null;
        }

        @Override
        public void stopNow() {
        }

        @Override
        public void reschedule(final String cronExp, final org.lastaflute.job.subsidiary.VaryingCronOpCall opLambda) {
        }

        @Override
        public void unschedule() {
        }

        @Override
        public void disappear() {
        }

        @Override
        public void becomeNonCron() {
        }

        @Override
        public void registerNext(final org.lastaflute.job.key.LaJobKey triggeredJob) {
        }

        @Override
        public org.lastaflute.job.key.LaJobKey getJobKey() {
            return null;
        }

        @Override
        public OptionalThing<org.lastaflute.job.key.LaJobNote> getJobNote() {
            return OptionalThing.empty();
        }

        @Override
        public OptionalThing<LaJobUnique> getJobUnique() {
            return OptionalThing.empty();
        }

        @Override
        public OptionalThing<String> getCronExp() {
            return OptionalThing.empty();
        }

        @Override
        public Class<? extends LaJob> getJobType() {
            return null;
        }

        @Override
        public OptionalThing<org.lastaflute.job.subsidiary.CronParamsSupplier> getParamsSupplier() {
            return OptionalThing.empty();
        }

        @Override
        public org.lastaflute.job.log.JobNoticeLogLevel getNoticeLogLevel() {
            return org.lastaflute.job.log.JobNoticeLogLevel.INFO;
        }

        @Override
        public org.lastaflute.job.subsidiary.JobConcurrentExec getConcurrentExec() {
            return null;
        }

        @Override
        public boolean isOutlawParallelGranted() {
            return false;
        }

        @Override
        public boolean isUnscheduled() {
            return false;
        }

        @Override
        public boolean isDisappeared() {
            return false;
        }

        @Override
        public boolean isNonCron() {
            return false;
        }

        @Override
        public boolean isExecutingNow() {
            return false;
        }

        @Override
        public <RESULT> OptionalThing<RESULT> mapExecutingNow(
                final java.util.function.Function<org.lastaflute.job.subsidiary.SnapshotExecState, RESULT> oneArgLambda) {
            return OptionalThing.empty();
        }

        @Override
        public org.dbflute.optional.OptionalThingIfPresentAfter ifExecutingNow(
                final java.util.function.Consumer<org.lastaflute.job.subsidiary.SnapshotExecState> oneArgLambda) {
            return processor -> processor.process();
        }

        @Override
        public java.util.Set<org.lastaflute.job.key.LaJobKey> getTriggeredJobKeySet() {
            return Collections.emptySet();
        }

        @Override
        public java.util.List<org.lastaflute.job.subsidiary.NeighborConcurrentGroup> getNeighborConcurrentGroupList() {
            return Collections.emptyList();
        }

        @Override
        public java.util.Map<String, org.lastaflute.job.subsidiary.NeighborConcurrentGroup> getNeighborConcurrentGroupMap() {
            return Collections.emptyMap();
        }

        @Override
        public String toIdentityDisp() {
            return "MockJob";
        }

        @Override
        public org.lastaflute.job.subsidiary.JobExecutingSnapshot takeSnapshotNow() {
            return null;
        }
    }
}
