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
package org.codelibs.fess.job;

import org.codelibs.fess.app.service.CrawlingInfoService;
import org.codelibs.fess.app.service.JobLogService;
import org.codelibs.fess.app.service.SearchLogService;
import org.codelibs.fess.app.service.UserInfoService;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class PurgeLogJobTest extends UnitFessTestCase {

    private PurgeLogJob purgeLogJob;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        purgeLogJob = new PurgeLogJob();
    }

    // Test all services execute successfully
    @Test
    public void test_execute_allSuccess() {
        // Setup tracking variables
        final boolean[] deleteCrawlingInfoCalled = { false };
        final boolean[] deleteSearchLogCalled = { false };
        final boolean[] deleteJobLogCalled = { false };
        final boolean[] deleteUserInfoCalled = { false };
        final boolean[] updateJobLogStatusCalled = { false };
        final long[] crawlingInfoDeletedBeforeTime = { 0 };
        final int[] searchLogDeletedDays = { 0 };
        final int[] jobLogDeletedDays = { 0 };
        final int[] userInfoDeletedDays = { 0 };
        final long[] expectedTime = { 0 };

        // Create mock services
        CrawlingInfoService crawlingInfoService = new CrawlingInfoService() {
            @Override
            public void deleteBefore(long time) {
                deleteCrawlingInfoCalled[0] = true;
                crawlingInfoDeletedBeforeTime[0] = time;
            }
        };

        SearchLogService searchLogService = new SearchLogService() {
            @Override
            public void deleteBefore(int days) {
                deleteSearchLogCalled[0] = true;
                searchLogDeletedDays[0] = days;
            }
        };

        JobLogService jobLogService = new JobLogService() {
            @Override
            public void deleteBefore(int days) {
                deleteJobLogCalled[0] = true;
                jobLogDeletedDays[0] = days;
            }

            @Override
            public void updateStatus() {
                updateJobLogStatusCalled[0] = true;
            }
        };

        UserInfoService userInfoService = new UserInfoService() {
            @Override
            public void deleteBefore(int days) {
                deleteUserInfoCalled[0] = true;
                userInfoDeletedDays[0] = days;
            }
        };

        SystemHelper systemHelper = new SystemHelper() {
            @Override
            public long getCurrentTimeAsLong() {
                // Return consistent time for testing
                if (expectedTime[0] == 0) {
                    expectedTime[0] = System.currentTimeMillis();
                }
                return expectedTime[0];
            }
        };

        FessConfig fessConfig = new TestFessConfig() {
            @Override
            public int getPurgeSearchLogDay() {
                return 30;
            }

            @Override
            public int getPurgeJobLogDay() {
                return 14;
            }

            @Override
            public int getPurgeUserInfoDay() {
                return 7;
            }
        };

        // Register components
        ComponentUtil.register(crawlingInfoService, CrawlingInfoService.class.getCanonicalName());
        ComponentUtil.register(searchLogService, SearchLogService.class.getCanonicalName());
        ComponentUtil.register(jobLogService, JobLogService.class.getCanonicalName());
        ComponentUtil.register(userInfoService, UserInfoService.class.getCanonicalName());
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.setFessConfig(fessConfig);

        // Execute
        String result = purgeLogJob.execute();

        // Assert all services were called
        assertTrue(deleteCrawlingInfoCalled[0]);
        assertTrue(deleteSearchLogCalled[0]);
        assertTrue(deleteJobLogCalled[0]);
        assertTrue(deleteUserInfoCalled[0]);
        assertTrue(updateJobLogStatusCalled[0]);

        // Assert correct parameters were passed - verify that time passed is greater than 0
        assertTrue(crawlingInfoDeletedBeforeTime[0] > 0);
        assertEquals(30, searchLogDeletedDays[0]);
        assertEquals(14, jobLogDeletedDays[0]);
        assertEquals(7, userInfoDeletedDays[0]);

        // Assert result is empty when all succeed
        assertEquals("", result);
    }

    // Test with negative days to skip purging
    @Test
    public void test_execute_negativeDays() {
        // Setup tracking variables
        final boolean[] deleteCrawlingInfoCalled = { false };
        final boolean[] deleteSearchLogCalled = { false };
        final boolean[] deleteJobLogCalled = { false };
        final boolean[] deleteUserInfoCalled = { false };
        final boolean[] updateJobLogStatusCalled = { false };

        // Create mock services
        CrawlingInfoService crawlingInfoService = new CrawlingInfoService() {
            @Override
            public void deleteBefore(long time) {
                deleteCrawlingInfoCalled[0] = true;
            }
        };

        SearchLogService searchLogService = new SearchLogService() {
            @Override
            public void deleteBefore(int days) {
                deleteSearchLogCalled[0] = true;
            }
        };

        JobLogService jobLogService = new JobLogService() {
            @Override
            public void deleteBefore(int days) {
                deleteJobLogCalled[0] = true;
            }

            @Override
            public void updateStatus() {
                updateJobLogStatusCalled[0] = true;
            }
        };

        UserInfoService userInfoService = new UserInfoService() {
            @Override
            public void deleteBefore(int days) {
                deleteUserInfoCalled[0] = true;
            }
        };

        SystemHelper systemHelper = new SystemHelper() {
            @Override
            public long getCurrentTimeAsLong() {
                return 1234567890L;
            }
        };

        FessConfig fessConfig = new TestFessConfig() {
            @Override
            public int getPurgeSearchLogDay() {
                return -1;
            }

            @Override
            public int getPurgeJobLogDay() {
                return -1;
            }

            @Override
            public int getPurgeUserInfoDay() {
                return -1;
            }
        };

        // Register components
        ComponentUtil.register(crawlingInfoService, CrawlingInfoService.class.getCanonicalName());
        ComponentUtil.register(searchLogService, SearchLogService.class.getCanonicalName());
        ComponentUtil.register(jobLogService, JobLogService.class.getCanonicalName());
        ComponentUtil.register(userInfoService, UserInfoService.class.getCanonicalName());
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.setFessConfig(fessConfig);

        // Execute
        String result = purgeLogJob.execute();

        // Assert crawling info and status update were called
        assertTrue(deleteCrawlingInfoCalled[0]);
        assertTrue(updateJobLogStatusCalled[0]);

        // Assert other services were not called
        assertFalse(deleteSearchLogCalled[0]);
        assertFalse(deleteJobLogCalled[0]);
        assertFalse(deleteUserInfoCalled[0]);

        // Assert result contains skip messages
        assertTrue(result.contains("Skipped to purge search logs"));
        assertTrue(result.contains("Skipped to purge job logs"));
        assertTrue(result.contains("Skipped to purge user info logs"));
    }

    // Test crawlingInfoService exception handling
    @Test
    public void test_execute_crawlingInfoException() {
        // Setup tracking variables
        final boolean[] deleteSearchLogCalled = { false };
        final boolean[] deleteJobLogCalled = { false };
        final boolean[] deleteUserInfoCalled = { false };
        final boolean[] updateJobLogStatusCalled = { false };

        // Create mock services
        CrawlingInfoService crawlingInfoService = new CrawlingInfoService() {
            @Override
            public void deleteBefore(long time) {
                throw new RuntimeException("Crawling info deletion failed");
            }
        };

        SearchLogService searchLogService = new SearchLogService() {
            @Override
            public void deleteBefore(int days) {
                deleteSearchLogCalled[0] = true;
            }
        };

        JobLogService jobLogService = new JobLogService() {
            @Override
            public void deleteBefore(int days) {
                deleteJobLogCalled[0] = true;
            }

            @Override
            public void updateStatus() {
                updateJobLogStatusCalled[0] = true;
            }
        };

        UserInfoService userInfoService = new UserInfoService() {
            @Override
            public void deleteBefore(int days) {
                deleteUserInfoCalled[0] = true;
            }
        };

        SystemHelper systemHelper = new SystemHelper() {
            @Override
            public long getCurrentTimeAsLong() {
                return 1234567890L;
            }
        };

        FessConfig fessConfig = new TestFessConfig() {
            @Override
            public int getPurgeSearchLogDay() {
                return 30;
            }

            @Override
            public int getPurgeJobLogDay() {
                return 14;
            }

            @Override
            public int getPurgeUserInfoDay() {
                return 7;
            }
        };

        // Register components
        ComponentUtil.register(crawlingInfoService, CrawlingInfoService.class.getCanonicalName());
        ComponentUtil.register(searchLogService, SearchLogService.class.getCanonicalName());
        ComponentUtil.register(jobLogService, JobLogService.class.getCanonicalName());
        ComponentUtil.register(userInfoService, UserInfoService.class.getCanonicalName());
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.setFessConfig(fessConfig);

        // Execute
        String result = purgeLogJob.execute();

        // Assert other services were still called
        assertTrue(deleteSearchLogCalled[0]);
        assertTrue(deleteJobLogCalled[0]);
        assertTrue(deleteUserInfoCalled[0]);
        assertTrue(updateJobLogStatusCalled[0]);

        // Assert error message in result
        assertTrue(result.contains("Crawling info deletion failed"));
    }

    // Test with zero days (should still execute)
    @Test
    public void test_execute_zeroDays() {
        // Setup tracking variables
        final boolean[] deleteCrawlingInfoCalled = { false };
        final boolean[] deleteSearchLogCalled = { false };
        final boolean[] deleteJobLogCalled = { false };
        final boolean[] deleteUserInfoCalled = { false };
        final boolean[] updateJobLogStatusCalled = { false };
        final int[] searchLogDeletedDays = { -1 };
        final int[] jobLogDeletedDays = { -1 };
        final int[] userInfoDeletedDays = { -1 };

        // Create mock services
        CrawlingInfoService crawlingInfoService = new CrawlingInfoService() {
            @Override
            public void deleteBefore(long time) {
                deleteCrawlingInfoCalled[0] = true;
            }
        };

        SearchLogService searchLogService = new SearchLogService() {
            @Override
            public void deleteBefore(int days) {
                deleteSearchLogCalled[0] = true;
                searchLogDeletedDays[0] = days;
            }
        };

        JobLogService jobLogService = new JobLogService() {
            @Override
            public void deleteBefore(int days) {
                deleteJobLogCalled[0] = true;
                jobLogDeletedDays[0] = days;
            }

            @Override
            public void updateStatus() {
                updateJobLogStatusCalled[0] = true;
            }
        };

        UserInfoService userInfoService = new UserInfoService() {
            @Override
            public void deleteBefore(int days) {
                deleteUserInfoCalled[0] = true;
                userInfoDeletedDays[0] = days;
            }
        };

        SystemHelper systemHelper = new SystemHelper() {
            @Override
            public long getCurrentTimeAsLong() {
                return 1234567890L;
            }
        };

        FessConfig fessConfig = new TestFessConfig() {
            @Override
            public int getPurgeSearchLogDay() {
                return 0;
            }

            @Override
            public int getPurgeJobLogDay() {
                return 0;
            }

            @Override
            public int getPurgeUserInfoDay() {
                return 0;
            }
        };

        // Register components
        ComponentUtil.register(crawlingInfoService, CrawlingInfoService.class.getCanonicalName());
        ComponentUtil.register(searchLogService, SearchLogService.class.getCanonicalName());
        ComponentUtil.register(jobLogService, JobLogService.class.getCanonicalName());
        ComponentUtil.register(userInfoService, UserInfoService.class.getCanonicalName());
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.setFessConfig(fessConfig);

        // Execute
        String result = purgeLogJob.execute();

        // Assert all services were called with zero days
        assertTrue(deleteCrawlingInfoCalled[0]);
        assertTrue(deleteSearchLogCalled[0]);
        assertTrue(deleteJobLogCalled[0]);
        assertTrue(deleteUserInfoCalled[0]);
        assertTrue(updateJobLogStatusCalled[0]);

        assertEquals(0, searchLogDeletedDays[0]);
        assertEquals(0, jobLogDeletedDays[0]);
        assertEquals(0, userInfoDeletedDays[0]);

        // Assert result is empty when all succeed
        assertEquals("", result);
    }

    // Test constructor
    @Test
    public void test_constructor() {
        // Test that constructor creates instance without error
        PurgeLogJob job = new PurgeLogJob();
        assertNotNull(job);
    }

    // Test multiple exceptions occur
    @Test
    public void test_execute_multipleExceptions() {
        // Create mock services with exceptions
        CrawlingInfoService crawlingInfoService = new CrawlingInfoService() {
            @Override
            public void deleteBefore(long time) {
                throw new RuntimeException("Crawling error");
            }
        };

        SearchLogService searchLogService = new SearchLogService() {
            @Override
            public void deleteBefore(int days) {
                throw new RuntimeException("Search error");
            }
        };

        JobLogService jobLogService = new JobLogService() {
            @Override
            public void deleteBefore(int days) {
                throw new RuntimeException("Job error");
            }

            @Override
            public void updateStatus() {
                throw new RuntimeException("Status error");
            }
        };

        UserInfoService userInfoService = new UserInfoService() {
            @Override
            public void deleteBefore(int days) {
                // This one succeeds
            }
        };

        SystemHelper systemHelper = new SystemHelper() {
            @Override
            public long getCurrentTimeAsLong() {
                return 1234567890L;
            }
        };

        FessConfig fessConfig = new TestFessConfig() {
            @Override
            public int getPurgeSearchLogDay() {
                return 30;
            }

            @Override
            public int getPurgeJobLogDay() {
                return 14;
            }

            @Override
            public int getPurgeUserInfoDay() {
                return 7;
            }
        };

        // Register components
        ComponentUtil.register(crawlingInfoService, CrawlingInfoService.class.getCanonicalName());
        ComponentUtil.register(searchLogService, SearchLogService.class.getCanonicalName());
        ComponentUtil.register(jobLogService, JobLogService.class.getCanonicalName());
        ComponentUtil.register(userInfoService, UserInfoService.class.getCanonicalName());
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.setFessConfig(fessConfig);

        // Execute
        String result = purgeLogJob.execute();

        // Assert all error messages in result
        assertTrue(result.contains("Crawling error"));
        assertTrue(result.contains("Search error"));
        assertTrue(result.contains("Job error"));
        assertTrue(result.contains("Status error"));
    }

    // Test mixed success and skip
    @Test
    public void test_execute_mixedSuccessAndSkip() {
        // Setup tracking variables
        final boolean[] deleteCrawlingInfoCalled = { false };
        final boolean[] deleteSearchLogCalled = { false };
        final boolean[] deleteJobLogCalled = { false };
        final boolean[] deleteUserInfoCalled = { false };
        final boolean[] updateJobLogStatusCalled = { false };
        final int[] searchLogDeletedDays = { -1 };
        final int[] userInfoDeletedDays = { -1 };

        // Create mock services
        CrawlingInfoService crawlingInfoService = new CrawlingInfoService() {
            @Override
            public void deleteBefore(long time) {
                deleteCrawlingInfoCalled[0] = true;
            }
        };

        SearchLogService searchLogService = new SearchLogService() {
            @Override
            public void deleteBefore(int days) {
                deleteSearchLogCalled[0] = true;
                searchLogDeletedDays[0] = days;
            }
        };

        JobLogService jobLogService = new JobLogService() {
            @Override
            public void deleteBefore(int days) {
                deleteJobLogCalled[0] = true;
            }

            @Override
            public void updateStatus() {
                updateJobLogStatusCalled[0] = true;
            }
        };

        UserInfoService userInfoService = new UserInfoService() {
            @Override
            public void deleteBefore(int days) {
                deleteUserInfoCalled[0] = true;
                userInfoDeletedDays[0] = days;
            }
        };

        SystemHelper systemHelper = new SystemHelper() {
            @Override
            public long getCurrentTimeAsLong() {
                return 1234567890L;
            }
        };

        FessConfig fessConfig = new TestFessConfig() {
            @Override
            public int getPurgeSearchLogDay() {
                return 10;
            }

            @Override
            public int getPurgeJobLogDay() {
                return -1;
            }

            @Override
            public int getPurgeUserInfoDay() {
                return 5;
            }
        };

        // Register components
        ComponentUtil.register(crawlingInfoService, CrawlingInfoService.class.getCanonicalName());
        ComponentUtil.register(searchLogService, SearchLogService.class.getCanonicalName());
        ComponentUtil.register(jobLogService, JobLogService.class.getCanonicalName());
        ComponentUtil.register(userInfoService, UserInfoService.class.getCanonicalName());
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.setFessConfig(fessConfig);

        // Execute
        String result = purgeLogJob.execute();

        // Assert services were called appropriately
        assertTrue(deleteCrawlingInfoCalled[0]);
        assertTrue(deleteSearchLogCalled[0]);
        assertFalse(deleteJobLogCalled[0]);
        assertTrue(deleteUserInfoCalled[0]);
        assertTrue(updateJobLogStatusCalled[0]);

        // Assert correct days were passed
        assertEquals(10, searchLogDeletedDays[0]);
        assertEquals(5, userInfoDeletedDays[0]);

        // Assert skip message for job logs only
        assertTrue(result.contains("Skipped to purge job logs"));
        assertFalse(result.contains("Skipped to purge search logs"));
        assertFalse(result.contains("Skipped to purge user info logs"));
    }

    // Test exception in searchLogService
    @Test
    public void test_execute_searchLogException() {
        // Setup tracking variables
        final boolean[] deleteCrawlingInfoCalled = { false };
        final boolean[] deleteJobLogCalled = { false };
        final boolean[] deleteUserInfoCalled = { false };
        final boolean[] updateJobLogStatusCalled = { false };

        // Create mock services
        CrawlingInfoService crawlingInfoService = new CrawlingInfoService() {
            @Override
            public void deleteBefore(long time) {
                deleteCrawlingInfoCalled[0] = true;
            }
        };

        SearchLogService searchLogService = new SearchLogService() {
            @Override
            public void deleteBefore(int days) {
                throw new RuntimeException("Search log deletion failed");
            }
        };

        JobLogService jobLogService = new JobLogService() {
            @Override
            public void deleteBefore(int days) {
                deleteJobLogCalled[0] = true;
            }

            @Override
            public void updateStatus() {
                updateJobLogStatusCalled[0] = true;
            }
        };

        UserInfoService userInfoService = new UserInfoService() {
            @Override
            public void deleteBefore(int days) {
                deleteUserInfoCalled[0] = true;
            }
        };

        SystemHelper systemHelper = new SystemHelper() {
            @Override
            public long getCurrentTimeAsLong() {
                return 1234567890L;
            }
        };

        FessConfig fessConfig = new TestFessConfig() {
            @Override
            public int getPurgeSearchLogDay() {
                return 30;
            }

            @Override
            public int getPurgeJobLogDay() {
                return 14;
            }

            @Override
            public int getPurgeUserInfoDay() {
                return 7;
            }
        };

        // Register components
        ComponentUtil.register(crawlingInfoService, CrawlingInfoService.class.getCanonicalName());
        ComponentUtil.register(searchLogService, SearchLogService.class.getCanonicalName());
        ComponentUtil.register(jobLogService, JobLogService.class.getCanonicalName());
        ComponentUtil.register(userInfoService, UserInfoService.class.getCanonicalName());
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.setFessConfig(fessConfig);

        // Execute
        String result = purgeLogJob.execute();

        // Assert other services were still called
        assertTrue(deleteCrawlingInfoCalled[0]);
        assertTrue(deleteJobLogCalled[0]);
        assertTrue(deleteUserInfoCalled[0]);
        assertTrue(updateJobLogStatusCalled[0]);

        // Assert error message in result
        assertTrue(result.contains("Search log deletion failed"));
    }

    // Test exception in jobLogService delete
    @Test
    public void test_execute_jobLogException() {
        // Setup tracking variables
        final boolean[] deleteCrawlingInfoCalled = { false };
        final boolean[] deleteSearchLogCalled = { false };
        final boolean[] deleteUserInfoCalled = { false };
        final boolean[] updateJobLogStatusCalled = { false };

        // Create mock services
        CrawlingInfoService crawlingInfoService = new CrawlingInfoService() {
            @Override
            public void deleteBefore(long time) {
                deleteCrawlingInfoCalled[0] = true;
            }
        };

        SearchLogService searchLogService = new SearchLogService() {
            @Override
            public void deleteBefore(int days) {
                deleteSearchLogCalled[0] = true;
            }
        };

        JobLogService jobLogService = new JobLogService() {
            @Override
            public void deleteBefore(int days) {
                throw new RuntimeException("Job log deletion failed");
            }

            @Override
            public void updateStatus() {
                updateJobLogStatusCalled[0] = true;
            }
        };

        UserInfoService userInfoService = new UserInfoService() {
            @Override
            public void deleteBefore(int days) {
                deleteUserInfoCalled[0] = true;
            }
        };

        SystemHelper systemHelper = new SystemHelper() {
            @Override
            public long getCurrentTimeAsLong() {
                return 1234567890L;
            }
        };

        FessConfig fessConfig = new TestFessConfig() {
            @Override
            public int getPurgeSearchLogDay() {
                return 30;
            }

            @Override
            public int getPurgeJobLogDay() {
                return 14;
            }

            @Override
            public int getPurgeUserInfoDay() {
                return 7;
            }
        };

        // Register components
        ComponentUtil.register(crawlingInfoService, CrawlingInfoService.class.getCanonicalName());
        ComponentUtil.register(searchLogService, SearchLogService.class.getCanonicalName());
        ComponentUtil.register(jobLogService, JobLogService.class.getCanonicalName());
        ComponentUtil.register(userInfoService, UserInfoService.class.getCanonicalName());
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.setFessConfig(fessConfig);

        // Execute
        String result = purgeLogJob.execute();

        // Assert other services were still called
        assertTrue(deleteCrawlingInfoCalled[0]);
        assertTrue(deleteSearchLogCalled[0]);
        assertTrue(deleteUserInfoCalled[0]);
        assertTrue(updateJobLogStatusCalled[0]);

        // Assert error message in result
        assertTrue(result.contains("Job log deletion failed"));
    }

    // Test exception in userInfoService
    @Test
    public void test_execute_userInfoException() {
        // Setup tracking variables
        final boolean[] deleteCrawlingInfoCalled = { false };
        final boolean[] deleteSearchLogCalled = { false };
        final boolean[] deleteJobLogCalled = { false };
        final boolean[] updateJobLogStatusCalled = { false };

        // Create mock services
        CrawlingInfoService crawlingInfoService = new CrawlingInfoService() {
            @Override
            public void deleteBefore(long time) {
                deleteCrawlingInfoCalled[0] = true;
            }
        };

        SearchLogService searchLogService = new SearchLogService() {
            @Override
            public void deleteBefore(int days) {
                deleteSearchLogCalled[0] = true;
            }
        };

        JobLogService jobLogService = new JobLogService() {
            @Override
            public void deleteBefore(int days) {
                deleteJobLogCalled[0] = true;
            }

            @Override
            public void updateStatus() {
                updateJobLogStatusCalled[0] = true;
            }
        };

        UserInfoService userInfoService = new UserInfoService() {
            @Override
            public void deleteBefore(int days) {
                throw new RuntimeException("User info deletion failed");
            }
        };

        SystemHelper systemHelper = new SystemHelper() {
            @Override
            public long getCurrentTimeAsLong() {
                return 1234567890L;
            }
        };

        FessConfig fessConfig = new TestFessConfig() {
            @Override
            public int getPurgeSearchLogDay() {
                return 30;
            }

            @Override
            public int getPurgeJobLogDay() {
                return 14;
            }

            @Override
            public int getPurgeUserInfoDay() {
                return 7;
            }
        };

        // Register components
        ComponentUtil.register(crawlingInfoService, CrawlingInfoService.class.getCanonicalName());
        ComponentUtil.register(searchLogService, SearchLogService.class.getCanonicalName());
        ComponentUtil.register(jobLogService, JobLogService.class.getCanonicalName());
        ComponentUtil.register(userInfoService, UserInfoService.class.getCanonicalName());
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.setFessConfig(fessConfig);

        // Execute
        String result = purgeLogJob.execute();

        // Assert other services were still called
        assertTrue(deleteCrawlingInfoCalled[0]);
        assertTrue(deleteSearchLogCalled[0]);
        assertTrue(deleteJobLogCalled[0]);
        assertTrue(updateJobLogStatusCalled[0]);

        // Assert error message in result
        assertTrue(result.contains("User info deletion failed"));
    }

    // Test exception in jobLogService updateStatus
    @Test
    public void test_execute_updateStatusException() {
        // Setup tracking variables
        final boolean[] deleteCrawlingInfoCalled = { false };
        final boolean[] deleteSearchLogCalled = { false };
        final boolean[] deleteJobLogCalled = { false };
        final boolean[] deleteUserInfoCalled = { false };

        // Create mock services
        CrawlingInfoService crawlingInfoService = new CrawlingInfoService() {
            @Override
            public void deleteBefore(long time) {
                deleteCrawlingInfoCalled[0] = true;
            }
        };

        SearchLogService searchLogService = new SearchLogService() {
            @Override
            public void deleteBefore(int days) {
                deleteSearchLogCalled[0] = true;
            }
        };

        JobLogService jobLogService = new JobLogService() {
            @Override
            public void deleteBefore(int days) {
                deleteJobLogCalled[0] = true;
            }

            @Override
            public void updateStatus() {
                throw new RuntimeException("Status update failed");
            }
        };

        UserInfoService userInfoService = new UserInfoService() {
            @Override
            public void deleteBefore(int days) {
                deleteUserInfoCalled[0] = true;
            }
        };

        SystemHelper systemHelper = new SystemHelper() {
            @Override
            public long getCurrentTimeAsLong() {
                return 1234567890L;
            }
        };

        FessConfig fessConfig = new TestFessConfig() {
            @Override
            public int getPurgeSearchLogDay() {
                return 30;
            }

            @Override
            public int getPurgeJobLogDay() {
                return 14;
            }

            @Override
            public int getPurgeUserInfoDay() {
                return 7;
            }
        };

        // Register components
        ComponentUtil.register(crawlingInfoService, CrawlingInfoService.class.getCanonicalName());
        ComponentUtil.register(searchLogService, SearchLogService.class.getCanonicalName());
        ComponentUtil.register(jobLogService, JobLogService.class.getCanonicalName());
        ComponentUtil.register(userInfoService, UserInfoService.class.getCanonicalName());
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.setFessConfig(fessConfig);

        // Execute
        String result = purgeLogJob.execute();

        // Assert all delete services were called
        assertTrue(deleteCrawlingInfoCalled[0]);
        assertTrue(deleteSearchLogCalled[0]);
        assertTrue(deleteJobLogCalled[0]);
        assertTrue(deleteUserInfoCalled[0]);

        // Assert error message in result
        assertTrue(result.contains("Status update failed"));
    }

    // Test configuration class extending FessConfig.SimpleImpl
    private static class TestFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;

        @Override
        public int getPurgeSearchLogDay() {
            return 30;
        }

        @Override
        public int getPurgeJobLogDay() {
            return 14;
        }

        @Override
        public int getPurgeUserInfoDay() {
            return 7;
        }
    }
}