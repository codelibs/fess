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

import org.codelibs.fess.Constants;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class JobLogTest extends UnitFessTestCase {

    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.register(new SystemHelper(), "systemHelper");
    }

    @Test
    public void test_defaultConstructor() {
        final JobLog jobLog = new JobLog();
        assertNotNull(jobLog);
        assertNull(jobLog.getId());
    }

    @Test
    public void test_constructorWithScheduledJob() {
        final ScheduledJob scheduledJob = createTestScheduledJob();

        final JobLog jobLog = new JobLog(scheduledJob);

        assertEquals("Test Job", jobLog.getJobName());
        assertEquals("groovy", jobLog.getScriptType());
        assertEquals("println 'hello'", jobLog.getScriptData());
        assertEquals(Constants.RUNNING, jobLog.getJobStatus());
        assertNotNull(jobLog.getStartTime());
        assertNotNull(jobLog.getLastUpdated());
        assertSame(scheduledJob, jobLog.getScheduledJob());
    }

    @Test
    public void test_setId_preGeneratedId() {
        final JobLog jobLog = new JobLog();
        jobLog.setId("pre-generated-id-123");
        assertEquals("pre-generated-id-123", jobLog.getId());
    }

    @Test
    public void test_setId_overwriteExisting() {
        final JobLog jobLog = new JobLog();
        jobLog.setId("first-id");
        assertEquals("first-id", jobLog.getId());

        jobLog.setId("second-id");
        assertEquals("second-id", jobLog.getId());
    }

    @Test
    public void test_setId_withUuidFormat() {
        final JobLog jobLog = new JobLog();
        final String uuidNoHyphens = "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6";
        jobLog.setId(uuidNoHyphens);
        assertEquals(uuidNoHyphens, jobLog.getId());
    }

    @Test
    public void test_versionNo() {
        final JobLog jobLog = new JobLog();
        jobLog.setVersionNo(5L);
        assertEquals(Long.valueOf(5L), jobLog.getVersionNo());
    }

    @Test
    public void test_toString_containsFields() {
        final ScheduledJob scheduledJob = createTestScheduledJob();
        final JobLog jobLog = new JobLog(scheduledJob);

        final String str = jobLog.toString();
        assertTrue(str.contains("Test Job"));
        assertTrue(str.contains(Constants.RUNNING));
    }

    private ScheduledJob createTestScheduledJob() {
        final ScheduledJob scheduledJob = new ScheduledJob();
        scheduledJob.setName("Test Job");
        scheduledJob.setScriptType("groovy");
        scheduledJob.setScriptData("println 'hello'");
        scheduledJob.setJobLogging(Constants.T);
        scheduledJob.setAvailable(Constants.T);
        scheduledJob.setCrawler(Constants.F);
        scheduledJob.setTarget("all");
        return scheduledJob;
    }
}
