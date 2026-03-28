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
package org.codelibs.fess.opensearch.config.exentity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.fess.Constants;
import org.codelibs.fess.exception.JobNotFoundException;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.job.JobManager;
import org.lastaflute.job.LaScheduledJob;
import org.lastaflute.job.key.LaJobUnique;
import org.lastaflute.job.subsidiary.LaunchNowOpCall;
import org.lastaflute.job.subsidiary.LaunchNowOption;
import org.lastaflute.job.subsidiary.LaunchedProcess;

public class ScheduledJobTest extends UnitFessTestCase {

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    // ===================================================================================
    //                                                                       Entity State
    //                                                                       =============

    @Test
    public void test_isLoggingEnabled_true() {
        final ScheduledJob job = new ScheduledJob();
        job.setJobLogging(Constants.T);
        assertTrue(job.isLoggingEnabled());
    }

    @Test
    public void test_isLoggingEnabled_false() {
        final ScheduledJob job = new ScheduledJob();
        job.setJobLogging(Constants.F);
        assertFalse(job.isLoggingEnabled());
    }

    @Test
    public void test_isLoggingEnabled_null() {
        final ScheduledJob job = new ScheduledJob();
        assertFalse(job.isLoggingEnabled());
    }

    @Test
    public void test_isEnabled_true() {
        final ScheduledJob job = new ScheduledJob();
        job.setAvailable(Constants.T);
        assertTrue(job.isEnabled());
    }

    @Test
    public void test_isEnabled_false() {
        final ScheduledJob job = new ScheduledJob();
        job.setAvailable(Constants.F);
        assertFalse(job.isEnabled());
    }

    @Test
    public void test_isCrawlerJob_true() {
        final ScheduledJob job = new ScheduledJob();
        job.setCrawler(Constants.T);
        assertTrue(job.isCrawlerJob());
    }

    @Test
    public void test_isCrawlerJob_false() {
        final ScheduledJob job = new ScheduledJob();
        job.setCrawler(Constants.F);
        assertFalse(job.isCrawlerJob());
    }

    @Test
    public void test_getScriptType_default() {
        final ScheduledJob job = new ScheduledJob();
        assertEquals("groovy", job.getScriptType());
    }

    @Test
    public void test_getScriptType_custom() {
        final ScheduledJob job = new ScheduledJob();
        job.setScriptType("python");
        assertEquals("python", job.getScriptType());
    }

    @Test
    public void test_idGetterSetter() {
        final ScheduledJob job = new ScheduledJob();
        job.setId("test-id-123");
        assertEquals("test-id-123", job.getId());
    }

    // ===================================================================================
    //                                                                    start(Map) Tests
    //                                                                    ================

    @Test
    public void test_startWithParams_passesParamsToLaunchNow() {
        final AtomicReference<LaunchNowOption> capturedOption = new AtomicReference<>();
        final AtomicBoolean launchNowCalled = new AtomicBoolean(false);

        final LaScheduledJob mockLaJob = createMockLaScheduledJob(opCall -> {
            final LaunchNowOption option = new LaunchNowOption();
            opCall.callback(option);
            capturedOption.set(option);
            launchNowCalled.set(true);
            return null;
        });

        final JobManager mockJobManager = createMockJobManager("job-1", mockLaJob);
        ComponentUtil.register(mockJobManager, JobManager.class.getCanonicalName());

        final ScheduledJob scheduledJob = new ScheduledJob();
        scheduledJob.setId("job-1");

        final Map<String, Object> params = new HashMap<>();
        params.put(Constants.JOB_LOG_ID, "test-log-id-abc");
        scheduledJob.start(params);

        assertTrue(launchNowCalled.get());
        assertNotNull(capturedOption.get());
        assertEquals("test-log-id-abc", capturedOption.get().getParameterMap().get(Constants.JOB_LOG_ID));
    }

    @Test
    public void test_startWithEmptyParams_callsLaunchNowWithoutParams() {
        final AtomicBoolean launchNowNoArgCalled = new AtomicBoolean(false);

        final LaScheduledJob mockLaJob = createMockLaScheduledJob(null);
        // Override the no-arg launchNow behavior
        final LaScheduledJob wrappedJob = new DelegatingLaScheduledJob(mockLaJob) {
            @Override
            public LaunchedProcess launchNow() {
                launchNowNoArgCalled.set(true);
                return null;
            }
        };

        final JobManager mockJobManager = createMockJobManager("job-2", wrappedJob);
        ComponentUtil.register(mockJobManager, JobManager.class.getCanonicalName());

        final ScheduledJob scheduledJob = new ScheduledJob();
        scheduledJob.setId("job-2");
        scheduledJob.start(Collections.emptyMap());

        assertTrue(launchNowNoArgCalled.get());
    }

    @Test
    public void test_startWithNullParams_callsLaunchNowWithoutParams() {
        final AtomicBoolean launchNowNoArgCalled = new AtomicBoolean(false);

        final LaScheduledJob mockLaJob = new DelegatingLaScheduledJob(createMockLaScheduledJob(null)) {
            @Override
            public LaunchedProcess launchNow() {
                launchNowNoArgCalled.set(true);
                return null;
            }
        };

        final JobManager mockJobManager = createMockJobManager("job-3", mockLaJob);
        ComponentUtil.register(mockJobManager, JobManager.class.getCanonicalName());

        final ScheduledJob scheduledJob = new ScheduledJob();
        scheduledJob.setId("job-3");
        scheduledJob.start(null);

        assertTrue(launchNowNoArgCalled.get());
    }

    @Test
    public void test_startWithParams_jobNotFound_throwsException() {
        final JobManager mockJobManager = createMockJobManager("other-id", null);
        ComponentUtil.register(mockJobManager, JobManager.class.getCanonicalName());

        final ScheduledJob scheduledJob = new ScheduledJob();
        scheduledJob.setId("nonexistent-job");

        try {
            scheduledJob.start(Map.of(Constants.JOB_LOG_ID, "some-id"));
            fail("Expected JobNotFoundException");
        } catch (final JobNotFoundException e) {
            // expected
        }
    }

    @Test
    public void test_startNoArg_callsLaunchNowWithoutParams() {
        final AtomicBoolean launchNowNoArgCalled = new AtomicBoolean(false);

        final LaScheduledJob mockLaJob = new DelegatingLaScheduledJob(createMockLaScheduledJob(null)) {
            @Override
            public LaunchedProcess launchNow() {
                launchNowNoArgCalled.set(true);
                return null;
            }
        };

        final JobManager mockJobManager = createMockJobManager("job-4", mockLaJob);
        ComponentUtil.register(mockJobManager, JobManager.class.getCanonicalName());

        final ScheduledJob scheduledJob = new ScheduledJob();
        scheduledJob.setId("job-4");
        scheduledJob.start();

        assertTrue(launchNowNoArgCalled.get());
    }

    @Test
    public void test_startWithMultipleParams() {
        final AtomicReference<LaunchNowOption> capturedOption = new AtomicReference<>();

        final LaScheduledJob mockLaJob = createMockLaScheduledJob(opCall -> {
            final LaunchNowOption option = new LaunchNowOption();
            opCall.callback(option);
            capturedOption.set(option);
            return null;
        });

        final JobManager mockJobManager = createMockJobManager("job-5", mockLaJob);
        ComponentUtil.register(mockJobManager, JobManager.class.getCanonicalName());

        final ScheduledJob scheduledJob = new ScheduledJob();
        scheduledJob.setId("job-5");

        final Map<String, Object> params = new HashMap<>();
        params.put(Constants.JOB_LOG_ID, "log-id-xyz");
        params.put("customKey", "customValue");
        scheduledJob.start(params);

        assertNotNull(capturedOption.get());
        final Map<String, Object> paramMap = capturedOption.get().getParameterMap();
        assertEquals("log-id-xyz", paramMap.get(Constants.JOB_LOG_ID));
        assertEquals("customValue", paramMap.get("customKey"));
    }

    // ===================================================================================
    //                                                                       Helper Methods
    //                                                                       ==============

    @FunctionalInterface
    private interface LaunchNowCallback {
        LaunchedProcess call(LaunchNowOpCall opCall);
    }

    private LaScheduledJob createMockLaScheduledJob(final LaunchNowCallback launchNowWithParamsCallback) {
        return new DelegatingLaScheduledJob(null) {
            @Override
            public LaunchedProcess launchNow() {
                return null;
            }

            @Override
            public LaunchedProcess launchNow(final LaunchNowOpCall opLambda) {
                if (launchNowWithParamsCallback != null) {
                    return launchNowWithParamsCallback.call(opLambda);
                }
                return null;
            }
        };
    }

    private JobManager createMockJobManager(final String expectedJobId, final LaScheduledJob laScheduledJob) {
        return new JobManager() {
            @Override
            public OptionalThing<LaScheduledJob> findJobByUniqueOf(final LaJobUnique jobUnique) {
                if (expectedJobId.equals(jobUnique.value()) && laScheduledJob != null) {
                    return OptionalThing.of(laScheduledJob);
                }
                return OptionalThing.ofNullable(null, () -> {
                    throw new IllegalStateException("Job not found: " + jobUnique);
                });
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
    }

    private abstract static class DelegatingLaScheduledJob implements LaScheduledJob {
        protected final LaScheduledJob delegate;

        DelegatingLaScheduledJob(final LaScheduledJob delegate) {
            this.delegate = delegate;
        }

        @Override
        public LaunchedProcess launchNow() {
            return delegate != null ? delegate.launchNow() : null;
        }

        @Override
        public LaunchedProcess launchNow(final LaunchNowOpCall opLambda) {
            return delegate != null ? delegate.launchNow(opLambda) : null;
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
        public Class<? extends org.lastaflute.job.LaJob> getJobType() {
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
