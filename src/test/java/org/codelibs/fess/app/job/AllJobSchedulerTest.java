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

import org.codelibs.fess.unit.UnitFessTestCase;
import org.lastaflute.job.LaJob;
import org.lastaflute.job.LaJobScheduler;

public class AllJobSchedulerTest extends UnitFessTestCase {

    public void test_AllJobScheduler_implementsLaJobScheduler() {
        final AllJobScheduler scheduler = new AllJobScheduler();
        assertTrue(scheduler instanceof LaJobScheduler);
    }

    public void test_AllJobScheduler_creation() {
        final AllJobScheduler scheduler = new AllJobScheduler();
        assertNotNull(scheduler);
    }

    public void test_setJobClass() {
        final AllJobScheduler scheduler = new AllJobScheduler();
        scheduler.setJobClass(TestJob.class);
        // No exception means success
    }

    // Test job class for testing setJobClass
    public static class TestJob implements LaJob {
        @Override
        public void run(final org.lastaflute.job.LaJobRuntime runtime) {
            // Empty implementation for testing
        }
    }
}
