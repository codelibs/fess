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
package org.codelibs.fess.timer;

import java.util.List;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.LogNotificationBuffer;
import org.codelibs.fess.util.LogNotificationBuffer.LogNotificationEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class LogNotificationTargetTest extends UnitFessTestCase {

    private LogNotificationTarget logNotificationTarget;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        LogNotificationBuffer.getInstance().drainAll(); // clear for isolation
        logNotificationTarget = new LogNotificationTarget();
    }

    @Test
    public void test_constructor() {
        LogNotificationTarget target = new LogNotificationTarget();
        assertNotNull(target);
    }

    @Test
    public void test_expired_emptyBuffer() {
        // expired() with empty buffer is no-op (no exception)
        logNotificationTarget.expired();
    }

    @Test
    public void test_flush_callsExpired() {
        // flush() delegates to expired(); with empty buffer, no-op (no exception)
        logNotificationTarget.flush();
    }

    @Test
    public void test_expired_withEvents_drainsBuffer() {
        // Add events to buffer
        LogNotificationBuffer.getInstance().offer(new LogNotificationEvent(System.currentTimeMillis(), "ERROR", "org.test", "msg1", null));
        LogNotificationBuffer.getInstance().offer(new LogNotificationEvent(System.currentTimeMillis(), "WARN", "org.test", "msg2", null));

        // expired() will drain buffer then fail on OpenSearch (not available) but should not throw
        logNotificationTarget.expired();

        // Buffer should be empty after expired() drained it
        List<LogNotificationEvent> remaining = LogNotificationBuffer.getInstance().drainAll();
        assertEquals(0, remaining.size());
    }

    @Test
    public void test_flush_withEvents_drainsBuffer() {
        LogNotificationBuffer.getInstance()
                .offer(new LogNotificationEvent(System.currentTimeMillis(), "ERROR", "org.test", "flush msg", null));

        logNotificationTarget.flush();

        List<LogNotificationEvent> remaining = LogNotificationBuffer.getInstance().drainAll();
        assertEquals(0, remaining.size());
    }

    @Test
    public void test_expired_multipleCalls() {
        logNotificationTarget.expired();
        logNotificationTarget.expired();
        logNotificationTarget.expired();
        // No exception means success
    }
}
