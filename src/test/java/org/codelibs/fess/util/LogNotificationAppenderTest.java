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
package org.codelibs.fess.util;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.SimpleMessage;
import org.codelibs.fess.helper.LogNotificationHelper;
import org.codelibs.fess.helper.LogNotificationHelper.LogNotificationEvent;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class LogNotificationAppenderTest extends UnitFessTestCase {

    private LogNotificationAppender appender;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.register(new LogNotificationHelper(), "logNotificationHelper");
        ComponentUtil.getLogNotificationHelper().drainAll();
        ComponentUtil.getFessConfig().setLogNotificationEnabled(true);
        appender = LogNotificationAppender.createAppender("test", "ERROR", null, null);
    }

    private LogEvent createLogEvent(final Level level, final String loggerName, final String message) {
        return Log4jLogEvent.newBuilder() //
                .setLoggerName(loggerName) //
                .setLevel(level) //
                .setMessage(new SimpleMessage(message)) //
                .setTimeMillis(System.currentTimeMillis()) //
                .build();
    }

    @Test
    public void test_createAppender() {
        final LogNotificationAppender result = LogNotificationAppender.createAppender("testAppender", "ERROR", null, null);
        assertNotNull(result);
    }

    @Test
    public void test_createAppender_nullName() {
        final LogNotificationAppender result = LogNotificationAppender.createAppender(null, "ERROR", null, null);
        assertNull(result);
    }

    @Test
    public void test_createAppender_defaultLevel() {
        final LogNotificationAppender result = LogNotificationAppender.createAppender("testAppender", null, null, null);
        assertNotNull(result);
    }

    @Test
    public void test_append_errorEvent() {
        final LogEvent event = createLogEvent(Level.ERROR, "org.codelibs.fess.test", "test error message");
        appender.append(event);

        final List<LogNotificationEvent> events = ComponentUtil.getLogNotificationHelper().drainAll();
        assertEquals(1, events.size());
        assertEquals("ERROR", events.get(0).getLevel());
        assertEquals("org.codelibs.fess.test", events.get(0).getLoggerName());
        assertEquals("test error message", events.get(0).getMessage());
        assertNull(events.get(0).getThrowable());
    }

    @Test
    public void test_append_warnEvent_withErrorLevel() {
        final LogEvent event = createLogEvent(Level.WARN, "org.codelibs.fess.test", "test warn message");
        appender.append(event);

        final List<LogNotificationEvent> events = ComponentUtil.getLogNotificationHelper().drainAll();
        assertEquals(0, events.size());
    }

    @Test
    public void test_append_warnEvent_withWarnLevel() {
        final LogNotificationAppender warnAppender = LogNotificationAppender.createAppender("testWarn", "WARN", null, null);
        final LogEvent event = createLogEvent(Level.WARN, "org.codelibs.fess.test", "test warn message");
        warnAppender.append(event);

        final List<LogNotificationEvent> events = ComponentUtil.getLogNotificationHelper().drainAll();
        assertEquals(1, events.size());
        assertEquals("WARN", events.get(0).getLevel());
        assertEquals("test warn message", events.get(0).getMessage());
    }

    @Test
    public void test_append_infoEvent() {
        final LogEvent event = createLogEvent(Level.INFO, "org.codelibs.fess.test", "test info message");
        appender.append(event);

        final List<LogNotificationEvent> events = ComponentUtil.getLogNotificationHelper().drainAll();
        assertEquals(0, events.size());
    }

    @Test
    public void test_append_excludedLogger_notificationHelper() {
        final LogEvent event = createLogEvent(Level.ERROR, "org.codelibs.fess.helper.NotificationHelper", "should be excluded");
        appender.append(event);

        final List<LogNotificationEvent> events = ComponentUtil.getLogNotificationHelper().drainAll();
        assertEquals(0, events.size());
    }

    @Test
    public void test_append_excludedLogger_curl() {
        final LogEvent event = createLogEvent(Level.ERROR, "org.codelibs.curl.CurlRequest", "should be excluded");
        appender.append(event);

        final List<LogNotificationEvent> events = ComponentUtil.getLogNotificationHelper().drainAll();
        assertEquals(0, events.size());
    }

    @Test
    public void test_append_excludedLogger_logNotificationJob() {
        final LogEvent event = createLogEvent(Level.ERROR, "org.codelibs.fess.job.LogNotificationJob", "should be excluded");
        appender.append(event);

        final List<LogNotificationEvent> events = ComponentUtil.getLogNotificationHelper().drainAll();
        assertEquals(0, events.size());
    }

    @Test
    public void test_append_nonExcludedLogger() {
        final LogEvent event = createLogEvent(Level.ERROR, "org.codelibs.fess.helper.SystemHelper", "not excluded");
        appender.append(event);

        final List<LogNotificationEvent> events = ComponentUtil.getLogNotificationHelper().drainAll();
        assertEquals(1, events.size());
        assertEquals("org.codelibs.fess.helper.SystemHelper", events.get(0).getLoggerName());
        assertEquals("not excluded", events.get(0).getMessage());
    }

    @Test
    public void test_append_withThrowable() {
        final LogEvent event = Log4jLogEvent.newBuilder() //
                .setLoggerName("org.codelibs.fess.test") //
                .setLevel(Level.ERROR) //
                .setMessage(new SimpleMessage("test error")) //
                .setThrown(new RuntimeException("test exception")) //
                .setTimeMillis(System.currentTimeMillis()) //
                .build();
        appender.append(event);

        final List<LogNotificationEvent> events = ComponentUtil.getLogNotificationHelper().drainAll();
        assertEquals(1, events.size());
        assertNotNull(events.get(0).getThrowable());
        assertTrue(events.get(0).getThrowable().contains("RuntimeException"));
        assertTrue(events.get(0).getThrowable().contains("test exception"));
        assertTrue(events.get(0).getThrowable().length() <= 500);
    }

    @Test
    public void test_append_nullLoggerName() {
        final LogEvent event = createLogEvent(Level.ERROR, null, "null logger message");
        appender.append(event);

        final List<LogNotificationEvent> events = ComponentUtil.getLogNotificationHelper().drainAll();
        assertEquals(1, events.size());
        assertNull(events.get(0).getLoggerName());
        assertEquals("null logger message", events.get(0).getMessage());
    }

    @Test
    public void test_append_excludedLogger_logNotificationTarget() {
        final LogEvent event = createLogEvent(Level.ERROR, "org.codelibs.fess.timer.LogNotificationTarget", "should be excluded");
        appender.append(event);
        final List<LogNotificationEvent> events = ComponentUtil.getLogNotificationHelper().drainAll();
        assertEquals(0, events.size());
    }

    @Test
    public void test_append_excludedLogger_logNotificationHelper() {
        final LogEvent event = createLogEvent(Level.ERROR, "org.codelibs.fess.helper.LogNotificationHelper", "should be excluded");
        appender.append(event);
        final List<LogNotificationEvent> events = ComponentUtil.getLogNotificationHelper().drainAll();
        assertEquals(0, events.size());
    }

    @Test
    public void test_append_fatalEvent() {
        final LogEvent event = createLogEvent(Level.FATAL, "org.codelibs.fess.test", "fatal error");
        appender.append(event);
        final List<LogNotificationEvent> events = ComponentUtil.getLogNotificationHelper().drainAll();
        assertEquals(1, events.size());
        assertEquals("FATAL", events.get(0).getLevel());
    }

    @Test
    public void test_append_multipleEvents() {
        appender.append(createLogEvent(Level.ERROR, "org.codelibs.fess.a", "error 1"));
        appender.append(createLogEvent(Level.ERROR, "org.codelibs.fess.b", "error 2"));
        appender.append(createLogEvent(Level.ERROR, "org.codelibs.fess.c", "error 3"));
        final List<LogNotificationEvent> events = ComponentUtil.getLogNotificationHelper().drainAll();
        assertEquals(3, events.size());
        assertEquals("error 1", events.get(0).getMessage());
        assertEquals("error 2", events.get(1).getMessage());
        assertEquals("error 3", events.get(2).getMessage());
    }

    @Test
    public void test_append_warnAppender_errorEvent() {
        final LogNotificationAppender warnAppender = LogNotificationAppender.createAppender("testWarn", "WARN", null, null);
        final LogEvent event = createLogEvent(Level.ERROR, "org.codelibs.fess.test", "error captured by warn appender");
        warnAppender.append(event);
        final List<LogNotificationEvent> events = ComponentUtil.getLogNotificationHelper().drainAll();
        assertEquals(1, events.size());
        assertEquals("ERROR", events.get(0).getLevel());
    }

    @Test
    public void test_append_excludedLogger_subpackage() {
        final LogEvent event = createLogEvent(Level.ERROR, "org.codelibs.curl.CurlResponse", "should be excluded");
        appender.append(event);
        final List<LogNotificationEvent> events = ComponentUtil.getLogNotificationHelper().drainAll();
        assertEquals(0, events.size());
    }

    @Test
    public void test_append_eventTimestamp() {
        final long now = System.currentTimeMillis();
        final LogEvent event = Log4jLogEvent.newBuilder()
                .setLoggerName("org.codelibs.fess.test")
                .setLevel(Level.ERROR)
                .setMessage(new SimpleMessage("timestamp test"))
                .setTimeMillis(now)
                .build();
        appender.append(event);
        final List<LogNotificationEvent> events = ComponentUtil.getLogNotificationHelper().drainAll();
        assertEquals(1, events.size());
        assertEquals(now, events.get(0).getTimestamp());
    }
}
