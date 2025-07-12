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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.SimpleMessage;
import org.codelibs.fess.unit.UnitFessTestCase;

public class ErrorToWarnRewritePolicyTest extends UnitFessTestCase {

    public void test_constructor() {
        String[] loggerNames = { "test.logger", "another.logger" };
        ErrorToWarnRewritePolicy policy = new ErrorToWarnRewritePolicy(loggerNames);

        assertNotNull(policy);
    }

    public void test_rewrite_with_null_logger_name() {
        String[] loggerNames = { "test.logger" };
        ErrorToWarnRewritePolicy policy = new ErrorToWarnRewritePolicy(loggerNames);

        LogEvent event =
                Log4jLogEvent.newBuilder().setLoggerName(null).setLevel(Level.ERROR).setMessage(new SimpleMessage("test message")).build();

        LogEvent result = policy.rewrite(event);
        assertSame(event, result);
    }

    public void test_rewrite_error_to_warn_matching_logger() {
        String[] loggerNames = { "test.logger" };
        ErrorToWarnRewritePolicy policy = new ErrorToWarnRewritePolicy(loggerNames);

        LogEvent event = Log4jLogEvent.newBuilder().setLoggerName("test.logger.sublogger").setLevel(Level.ERROR)
                .setMessage(new SimpleMessage("test error message")).build();

        LogEvent result = policy.rewrite(event);

        assertNotSame(event, result);
        assertEquals(Level.WARN, result.getLevel());
        assertEquals("test.logger.sublogger", result.getLoggerName());
        assertEquals("test error message", result.getMessage().getFormattedMessage());
    }

    public void test_rewrite_error_to_warn_exact_logger_match() {
        String[] loggerNames = { "test.logger" };
        ErrorToWarnRewritePolicy policy = new ErrorToWarnRewritePolicy(loggerNames);

        LogEvent event = Log4jLogEvent.newBuilder().setLoggerName("test.logger").setLevel(Level.ERROR)
                .setMessage(new SimpleMessage("test error message")).build();

        LogEvent result = policy.rewrite(event);

        assertNotSame(event, result);
        assertEquals(Level.WARN, result.getLevel());
        assertEquals("test.logger", result.getLoggerName());
    }

    public void test_rewrite_non_matching_logger() {
        String[] loggerNames = { "test.logger" };
        ErrorToWarnRewritePolicy policy = new ErrorToWarnRewritePolicy(loggerNames);

        LogEvent event = Log4jLogEvent.newBuilder().setLoggerName("different.logger").setLevel(Level.ERROR)
                .setMessage(new SimpleMessage("test error message")).build();

        LogEvent result = policy.rewrite(event);

        assertSame(event, result);
        assertEquals(Level.ERROR, result.getLevel());
    }

    public void test_rewrite_non_error_level() {
        String[] loggerNames = { "test.logger" };
        ErrorToWarnRewritePolicy policy = new ErrorToWarnRewritePolicy(loggerNames);

        LogEvent warnEvent = Log4jLogEvent.newBuilder().setLoggerName("test.logger").setLevel(Level.WARN)
                .setMessage(new SimpleMessage("test warn message")).build();

        LogEvent result = policy.rewrite(warnEvent);

        assertSame(warnEvent, result);
        assertEquals(Level.WARN, result.getLevel());
    }

    public void test_rewrite_info_level() {
        String[] loggerNames = { "test.logger" };
        ErrorToWarnRewritePolicy policy = new ErrorToWarnRewritePolicy(loggerNames);

        LogEvent infoEvent = Log4jLogEvent.newBuilder().setLoggerName("test.logger").setLevel(Level.INFO)
                .setMessage(new SimpleMessage("test info message")).build();

        LogEvent result = policy.rewrite(infoEvent);

        assertSame(infoEvent, result);
        assertEquals(Level.INFO, result.getLevel());
    }

    public void test_rewrite_debug_level() {
        String[] loggerNames = { "test.logger" };
        ErrorToWarnRewritePolicy policy = new ErrorToWarnRewritePolicy(loggerNames);

        LogEvent debugEvent = Log4jLogEvent.newBuilder().setLoggerName("test.logger").setLevel(Level.DEBUG)
                .setMessage(new SimpleMessage("test debug message")).build();

        LogEvent result = policy.rewrite(debugEvent);

        assertSame(debugEvent, result);
        assertEquals(Level.DEBUG, result.getLevel());
    }

    public void test_rewrite_multiple_logger_names() {
        String[] loggerNames = { "test.logger", "another.logger", "third.logger" };
        ErrorToWarnRewritePolicy policy = new ErrorToWarnRewritePolicy(loggerNames);

        LogEvent event1 = Log4jLogEvent.newBuilder().setLoggerName("test.logger.sub").setLevel(Level.ERROR)
                .setMessage(new SimpleMessage("test message 1")).build();

        LogEvent event2 = Log4jLogEvent.newBuilder().setLoggerName("another.logger.sub").setLevel(Level.ERROR)
                .setMessage(new SimpleMessage("test message 2")).build();

        LogEvent event3 = Log4jLogEvent.newBuilder().setLoggerName("third.logger").setLevel(Level.ERROR)
                .setMessage(new SimpleMessage("test message 3")).build();

        LogEvent result1 = policy.rewrite(event1);
        LogEvent result2 = policy.rewrite(event2);
        LogEvent result3 = policy.rewrite(event3);

        assertEquals(Level.WARN, result1.getLevel());
        assertEquals(Level.WARN, result2.getLevel());
        assertEquals(Level.WARN, result3.getLevel());
    }

    public void test_rewrite_partial_match_should_not_rewrite() {
        String[] loggerNames = { "test.logger" };
        ErrorToWarnRewritePolicy policy = new ErrorToWarnRewritePolicy(loggerNames);

        LogEvent event = Log4jLogEvent.newBuilder().setLoggerName("prefix.test.logger").setLevel(Level.ERROR)
                .setMessage(new SimpleMessage("test message")).build();

        LogEvent result = policy.rewrite(event);

        assertSame(event, result);
        assertEquals(Level.ERROR, result.getLevel());
    }

    public void test_rewrite_empty_logger_names() {
        String[] loggerNames = {};
        ErrorToWarnRewritePolicy policy = new ErrorToWarnRewritePolicy(loggerNames);

        LogEvent event = Log4jLogEvent.newBuilder().setLoggerName("any.logger").setLevel(Level.ERROR)
                .setMessage(new SimpleMessage("test message")).build();

        LogEvent result = policy.rewrite(event);

        assertSame(event, result);
        assertEquals(Level.ERROR, result.getLevel());
    }

    public void test_createPolicy_with_null_logger_name_prefix() {
        ErrorToWarnRewritePolicy policy = ErrorToWarnRewritePolicy.createPolicy(null);

        assertNotNull(policy);

        LogEvent event = Log4jLogEvent.newBuilder().setLoggerName("any.logger").setLevel(Level.ERROR)
                .setMessage(new SimpleMessage("test message")).build();

        LogEvent result = policy.rewrite(event);

        assertSame(event, result);
        assertEquals(Level.ERROR, result.getLevel());
    }

    public void test_createPolicy_with_empty_logger_name_prefix() {
        ErrorToWarnRewritePolicy policy = ErrorToWarnRewritePolicy.createPolicy("");

        assertNotNull(policy);

        LogEvent event = Log4jLogEvent.newBuilder().setLoggerName("any.logger").setLevel(Level.ERROR)
                .setMessage(new SimpleMessage("test message")).build();

        LogEvent result = policy.rewrite(event);

        assertSame(event, result);
    }

    public void test_createPolicy_with_single_logger_name() {
        ErrorToWarnRewritePolicy policy = ErrorToWarnRewritePolicy.createPolicy("test.logger");

        assertNotNull(policy);

        LogEvent event = Log4jLogEvent.newBuilder().setLoggerName("test.logger").setLevel(Level.ERROR)
                .setMessage(new SimpleMessage("test message")).build();

        LogEvent result = policy.rewrite(event);

        assertEquals(Level.WARN, result.getLevel());
    }

    public void test_createPolicy_with_multiple_logger_names() {
        ErrorToWarnRewritePolicy policy = ErrorToWarnRewritePolicy.createPolicy("test.logger,another.logger,third.logger");

        assertNotNull(policy);

        LogEvent event1 = Log4jLogEvent.newBuilder().setLoggerName("test.logger").setLevel(Level.ERROR)
                .setMessage(new SimpleMessage("test message 1")).build();

        LogEvent event2 = Log4jLogEvent.newBuilder().setLoggerName("another.logger").setLevel(Level.ERROR)
                .setMessage(new SimpleMessage("test message 2")).build();

        LogEvent result1 = policy.rewrite(event1);
        LogEvent result2 = policy.rewrite(event2);

        assertEquals(Level.WARN, result1.getLevel());
        assertEquals(Level.WARN, result2.getLevel());
    }

    public void test_createPolicy_with_spaces_in_logger_names() {
        ErrorToWarnRewritePolicy policy = ErrorToWarnRewritePolicy.createPolicy("  test.logger  ,  another.logger  ,  third.logger  ");

        assertNotNull(policy);

        LogEvent event = Log4jLogEvent.newBuilder().setLoggerName("test.logger").setLevel(Level.ERROR)
                .setMessage(new SimpleMessage("test message")).build();

        LogEvent result = policy.rewrite(event);

        assertEquals(Level.WARN, result.getLevel());
    }

    public void test_createPolicy_with_empty_entries() {
        ErrorToWarnRewritePolicy policy = ErrorToWarnRewritePolicy.createPolicy("test.logger,,another.logger,  ,third.logger");

        assertNotNull(policy);

        LogEvent event = Log4jLogEvent.newBuilder().setLoggerName("test.logger").setLevel(Level.ERROR)
                .setMessage(new SimpleMessage("test message")).build();

        LogEvent result = policy.rewrite(event);

        assertEquals(Level.WARN, result.getLevel());
    }

    public void test_rewrite_preserves_other_event_properties() {
        String[] loggerNames = { "test.logger" };
        ErrorToWarnRewritePolicy policy = new ErrorToWarnRewritePolicy(loggerNames);

        LogEvent event = Log4jLogEvent.newBuilder().setLoggerName("test.logger").setLevel(Level.ERROR)
                .setMessage(new SimpleMessage("test message")).setLoggerFqcn("test.class").setThreadName("test-thread").build();

        LogEvent result = policy.rewrite(event);

        assertEquals(Level.WARN, result.getLevel());
        assertEquals("test.logger", result.getLoggerName());
        assertEquals("test message", result.getMessage().getFormattedMessage());
        assertEquals("test.class", result.getLoggerFqcn());
        assertEquals("test-thread", result.getThreadName());
    }
}