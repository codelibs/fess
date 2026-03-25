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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.LogNotificationBuffer.LogNotificationEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class LogNotificationJobTest extends UnitFessTestCase {

    private LogNotificationJob logNotificationJob;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        logNotificationJob = new LogNotificationJob();
    }

    @Test
    public void test_constructor() {
        LogNotificationJob job = new LogNotificationJob();
        assertNotNull(job);
    }

    @Test
    public void test_execute_disabled() {
        TestFessConfig fessConfig = new TestFessConfig();
        fessConfig.logNotificationEnabled = false;
        ComponentUtil.setFessConfig(fessConfig);

        String result = logNotificationJob.execute();

        assertEquals("Log notification disabled.", result);
    }

    @Test
    public void test_execute_noNotificationTargets() {
        TestFessConfig fessConfig = new TestFessConfig();
        fessConfig.logNotificationEnabled = true;
        fessConfig.hasNotification = false;
        ComponentUtil.setFessConfig(fessConfig);

        String result = logNotificationJob.execute();

        assertEquals("No notification targets configured.", result);
    }

    @Test
    public void test_formatDetails() {
        TestableLogNotificationJob testableJob = new TestableLogNotificationJob();

        long timestamp = 1700000000000L; // fixed timestamp for predictable output
        List<LogNotificationEvent> events = new ArrayList<>();
        events.add(new LogNotificationEvent(timestamp, "ERROR", "org.codelibs.fess.TestClass", "Something went wrong", null));

        String details = testableJob.testFormatDetails(events);

        assertNotNull(details);
        assertTrue(details.contains("Total: 1 event(s)"));
        assertTrue(details.contains("ERROR"));
        assertTrue(details.contains("org.codelibs.fess.TestClass"));
        assertTrue(details.contains("Something went wrong"));
    }

    @Test
    public void test_formatDetails_truncation() {
        TestableLogNotificationJob testableJob = new TestableLogNotificationJob();

        long timestamp = 1700000000000L;
        List<LogNotificationEvent> events = new ArrayList<>();
        // Create many events with long messages
        for (int i = 0; i < 100; i++) {
            String longMessage = "A".repeat(300) + " event " + i;
            events.add(new LogNotificationEvent(timestamp, "ERROR", "org.codelibs.fess.TestClass", longMessage, null));
        }

        String details = testableJob.testFormatDetails(events);

        assertNotNull(details);
        // Max 50 events displayed, message truncated to 200 chars
        assertTrue(details.contains("Total: 100 event(s) (showing 50)"));
        assertTrue(details.contains("... and 50 more"));
        // Long messages should be truncated with "..."
        assertTrue(details.contains("..."));
    }

    @Test
    public void test_formatDetails_withThrowable() {
        TestableLogNotificationJob testableJob = new TestableLogNotificationJob();

        long timestamp = 1700000000000L;
        List<LogNotificationEvent> events = new ArrayList<>();
        events.add(new LogNotificationEvent(timestamp, "ERROR", "org.codelibs.fess.TestClass", "Something went wrong",
                "java.lang.NullPointerException: null\n\tat org.codelibs.fess.TestClass.doSomething(TestClass.java:42)"));

        String details = testableJob.testFormatDetails(events);

        assertNotNull(details);
        assertTrue(details.contains("Total: 1 event(s)"));
        assertTrue(details.contains("Something went wrong"));
        // Throwable is no longer included in summary-oriented format
        // Only log message is shown per event line
    }

    @Test
    public void test_formatDetails_emptyList() {
        TestableLogNotificationJob testableJob = new TestableLogNotificationJob();

        String details = testableJob.testFormatDetails(Collections.emptyList());

        assertNotNull(details);
        assertTrue(details.contains("Total: 0 event(s)"));
    }

    @Test
    public void test_formatDetails_multipleEventsOrdered() {
        TestableLogNotificationJob testableJob = new TestableLogNotificationJob();
        long base = 1700000000000L;
        List<LogNotificationEvent> events = new ArrayList<>();
        events.add(new LogNotificationEvent(base, "ERROR", "org.test.A", "first", null));
        events.add(new LogNotificationEvent(base + 1000, "WARN", "org.test.B", "second", null));
        events.add(new LogNotificationEvent(base + 2000, "ERROR", "org.test.C", "third", null));

        String details = testableJob.testFormatDetails(events);

        int posFirst = details.indexOf("first");
        int posSecond = details.indexOf("second");
        int posThird = details.indexOf("third");
        assertTrue(posFirst < posSecond);
        assertTrue(posSecond < posThird);
    }

    @Test
    public void test_formatDetails_mixedLevels() {
        TestableLogNotificationJob testableJob = new TestableLogNotificationJob();
        long timestamp = 1700000000000L;
        List<LogNotificationEvent> events = new ArrayList<>();
        events.add(new LogNotificationEvent(timestamp, "ERROR", "org.test", "error msg", null));
        events.add(new LogNotificationEvent(timestamp, "WARN", "org.test", "warn msg", null));

        String details = testableJob.testFormatDetails(events);

        assertTrue(details.contains("ERROR"));
        assertTrue(details.contains("WARN"));
        assertTrue(details.contains("error msg"));
        assertTrue(details.contains("warn msg"));
    }

    @Test
    public void test_formatDetails_singleCharMessage() {
        TestableLogNotificationJob testableJob = new TestableLogNotificationJob();
        List<LogNotificationEvent> events = new ArrayList<>();
        events.add(new LogNotificationEvent(1700000000000L, "ERROR", "a", "x", null));

        String details = testableJob.testFormatDetails(events);

        assertNotNull(details);
        assertTrue(details.contains("ERROR"));
        assertTrue(details.contains("x"));
    }

    @Test
    public void test_formatDetails_messageTruncation() {
        TestableLogNotificationJob testableJob = new TestableLogNotificationJob();
        List<LogNotificationEvent> events = new ArrayList<>();
        String longMessage = "X".repeat(250);
        events.add(new LogNotificationEvent(1700000000000L, "ERROR", "org.test", longMessage, null));

        String details = testableJob.testFormatDetails(events);

        // Message should be truncated to 200 chars + "..."
        assertFalse(details.contains("X".repeat(250)));
        assertTrue(details.contains("X".repeat(200) + "..."));
    }

    @Test
    public void test_formatDetails_displayLimit() {
        TestableLogNotificationJob testableJob = new TestableLogNotificationJob();
        List<LogNotificationEvent> events = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            events.add(new LogNotificationEvent(1700000000000L, "ERROR", "org.test", "msg" + i, null));
        }

        String details = testableJob.testFormatDetails(events);

        assertTrue(details.contains("Total: 60 event(s) (showing 50)"));
        assertTrue(details.contains("... and 10 more"));
        // msg49 should be present (50th event, index 49), msg50 should not
        assertTrue(details.contains("msg49"));
        assertFalse(details.contains("msg50"));
    }

    private static class TestableLogNotificationJob extends LogNotificationJob {
        public String testFormatDetails(List<LogNotificationEvent> events) {
            return formatDetails(events);
        }
    }

    private static class TestFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;
        private boolean logNotificationEnabled = true;
        private boolean hasNotification = true;
        private String notificationTo = "admin@example.com";

        @Override
        public boolean isLogNotificationEnabled() {
            return logNotificationEnabled;
        }

        @Override
        public boolean hasNotification() {
            return hasNotification;
        }

        @Override
        public String getNotificationTo() {
            return notificationTo;
        }

        @Override
        public int getLogNotificationIntervalAsInteger() {
            return 300;
        }

        @Override
        public String getMailFromAddress() {
            return "fess@example.com";
        }

        @Override
        public String getMailFromName() {
            return "Fess System";
        }

        @Override
        public String getMailReturnPath() {
            return "bounce@example.com";
        }
    }
}
