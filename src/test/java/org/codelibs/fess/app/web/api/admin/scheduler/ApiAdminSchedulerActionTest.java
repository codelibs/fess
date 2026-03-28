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
package org.codelibs.fess.app.web.api.admin.scheduler;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.ScheduledJobService;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiStartJobResponse;
import org.codelibs.fess.opensearch.config.exentity.ScheduledJob;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.job.JobManager;
import org.lastaflute.job.LaScheduledJob;
import org.lastaflute.job.key.LaJobUnique;
import org.lastaflute.job.subsidiary.LaunchNowOpCall;
import org.lastaflute.job.subsidiary.LaunchNowOption;
import org.lastaflute.job.subsidiary.LaunchedProcess;
import org.lastaflute.web.response.JsonResponse;

/**
 * Tests for the scheduler start API endpoint contract.
 * Verifies that {@code PUT /api/admin/scheduler/{id}/start} returns the correct
 * {@code jobLogId} based on the job's logging configuration.
 */
public class ApiAdminSchedulerActionTest extends UnitFessTestCase {

    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.register(new org.codelibs.fess.helper.SystemHelper(), "systemHelper");
    }

    // ===================================================================================
    //                                                         Logging Enabled: jobLogId
    //                                                         ===========================

    @Test
    public void test_put$start_loggingEnabled_returnsJobLogId() throws Exception {
        // Setup: job with logging enabled
        final AtomicReference<LaunchNowOption> capturedOption = new AtomicReference<>();
        final ScheduledJob scheduledJob = createScheduledJob("job-1", "Test Job", true, true);

        registerMockJobManager("job-1", opCall -> {
            final LaunchNowOption option = new LaunchNowOption();
            opCall.callback(option);
            capturedOption.set(option);
            return null;
        });

        final ApiAdminSchedulerAction action = createActionWithMockService(scheduledJob);

        // Execute the real put$start() method
        final JsonResponse<ApiResult> jsonResponse = action.put$start("job-1");

        // Verify: response contains non-null jobLogId
        assertNotNull(jsonResponse);
        final ApiResult apiResult = jsonResponse.getJsonResult();
        assertNotNull(apiResult);
        final String responseJobLogId = extractJobLogId(apiResult);
        assertNotNull(responseJobLogId);
        assertEquals(32, responseJobLogId.length());
        assertTrue(responseJobLogId.matches("[0-9a-f]{32}"));

        // Verify the pre-generated ID was passed to the job via launch params
        assertNotNull(capturedOption.get());
        assertEquals(responseJobLogId, capturedOption.get().getParameterMap().get(Constants.JOB_LOG_ID));
    }

    // ===================================================================================
    //                                                        Logging Disabled: null
    //                                                        ==========================

    @Test
    public void test_put$start_loggingDisabled_returnsNullJobLogId() throws Exception {
        // Setup: job with logging disabled
        final ScheduledJob scheduledJob = createScheduledJob("job-2", "Test Job No Log", false, true);

        registerMockJobManager("job-2", null, () -> {});

        final ApiAdminSchedulerAction action = createActionWithMockService(scheduledJob);

        // Execute the real put$start() method
        final JsonResponse<ApiResult> jsonResponse = action.put$start("job-2");

        // Verify: response contains null jobLogId
        assertNotNull(jsonResponse);
        final ApiResult apiResult = jsonResponse.getJsonResult();
        assertNotNull(apiResult);
        final String responseJobLogId = extractJobLogId(apiResult);
        assertNull(responseJobLogId);
    }

    // ===================================================================================
    //                                                                       Helpers
    //                                                                       ========

    private String extractJobLogId(final ApiResult apiResult) throws Exception {
        final Field responseField = ApiResult.class.getDeclaredField("response");
        responseField.setAccessible(true);
        final Object response = responseField.get(apiResult);
        assertTrue(response instanceof ApiStartJobResponse);

        final Field jobLogIdField = ApiStartJobResponse.class.getDeclaredField("jobLogId");
        jobLogIdField.setAccessible(true);
        return (String) jobLogIdField.get(response);
    }

    private ApiAdminSchedulerAction createActionWithMockService(final ScheduledJob scheduledJob) throws Exception {
        final ApiAdminSchedulerAction action = new ApiAdminSchedulerAction();

        // Inject mock ScheduledJobService via reflection
        final ScheduledJobService mockService = new ScheduledJobService() {
            @Override
            public OptionalEntity<ScheduledJob> getScheduledJob(final String id) {
                if (scheduledJob.getId().equals(id)) {
                    return OptionalEntity.of(scheduledJob);
                }
                return OptionalEntity.empty();
            }
        };

        final Field serviceField = ApiAdminSchedulerAction.class.getDeclaredField("scheduledJobService");
        serviceField.setAccessible(true);
        serviceField.set(action, mockService);

        return action;
    }

    private ScheduledJob createScheduledJob(final String id, final String name, final boolean loggingEnabled, final boolean enabled) {
        final ScheduledJob job = new ScheduledJob();
        job.setId(id);
        job.setName(name);
        job.setJobLogging(loggingEnabled ? Constants.T : Constants.F);
        job.setAvailable(enabled ? Constants.T : Constants.F);
        job.setScriptType("groovy");
        job.setScriptData("println 'test'");
        job.setTarget("all");
        return job;
    }

    @FunctionalInterface
    private interface LaunchNowWithParamsCallback {
        LaunchedProcess call(LaunchNowOpCall opCall);
    }

    private void registerMockJobManager(final String jobId, final LaunchNowWithParamsCallback withParamsCallback) {
        registerMockJobManager(jobId, withParamsCallback, null);
    }

    private void registerMockJobManager(final String jobId, final LaunchNowWithParamsCallback withParamsCallback,
            final Runnable noArgCallback) {
        final LaScheduledJob mockLaJob = new MockLaScheduledJob() {
            @Override
            public LaunchedProcess launchNow() {
                if (noArgCallback != null) {
                    noArgCallback.run();
                }
                return null;
            }

            @Override
            public LaunchedProcess launchNow(final LaunchNowOpCall opLambda) {
                if (withParamsCallback != null) {
                    return withParamsCallback.call(opLambda);
                }
                return null;
            }
        };

        final JobManager mockJobManager = new JobManager() {
            @Override
            public OptionalThing<LaScheduledJob> findJobByUniqueOf(final LaJobUnique jobUnique) {
                if (jobId.equals(jobUnique.value())) {
                    return OptionalThing.of(mockLaJob);
                }
                return OptionalThing.ofNullable(null, () -> {
                    throw new IllegalStateException("not found");
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
        ComponentUtil.register(mockJobManager, JobManager.class.getCanonicalName());
    }

    private abstract static class MockLaScheduledJob implements LaScheduledJob {
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
