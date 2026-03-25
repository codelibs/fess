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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.LogNotificationBuffer.LogNotificationEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class LogNotificationBufferTest extends UnitFessTestCase {

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        LogNotificationBuffer.getInstance().drainAll(); // clear buffer for test isolation
    }

    @Test
    public void test_offer_and_drainAll() {
        final LogNotificationBuffer buffer = LogNotificationBuffer.getInstance();

        buffer.offer(new LogNotificationEvent(1000L, "ERROR", "com.example.Foo", "message1", "throwable1"));
        buffer.offer(new LogNotificationEvent(2000L, "WARN", "com.example.Bar", "message2", "throwable2"));
        buffer.offer(new LogNotificationEvent(3000L, "ERROR", "com.example.Baz", "message3", "throwable3"));

        final List<LogNotificationEvent> events = buffer.drainAll();
        assertEquals(3, events.size());
        assertEquals("message1", events.get(0).getMessage());
        assertEquals("message2", events.get(1).getMessage());
        assertEquals("message3", events.get(2).getMessage());
    }

    @Test
    public void test_drainAll_empty() {
        final LogNotificationBuffer buffer = LogNotificationBuffer.getInstance();

        final List<LogNotificationEvent> events = buffer.drainAll();
        assertNotNull(events);
        assertTrue(events.isEmpty());
    }

    @Test
    public void test_drainAll_clears_buffer() {
        final LogNotificationBuffer buffer = LogNotificationBuffer.getInstance();

        buffer.offer(new LogNotificationEvent(1000L, "ERROR", "com.example.Foo", "message1", null));
        buffer.offer(new LogNotificationEvent(2000L, "WARN", "com.example.Bar", "message2", null));

        final List<LogNotificationEvent> firstDrain = buffer.drainAll();
        assertEquals(2, firstDrain.size());

        final List<LogNotificationEvent> secondDrain = buffer.drainAll();
        assertNotNull(secondDrain);
        assertTrue(secondDrain.isEmpty());
    }

    @Test
    public void test_offer_overflow() {
        final LogNotificationBuffer buffer = LogNotificationBuffer.getInstance();

        for (int i = 0; i < 1100; i++) {
            buffer.offer(new LogNotificationEvent(i, "ERROR", "com.example.Test", "message" + i, null));
        }

        final List<LogNotificationEvent> events = buffer.drainAll();
        assertEquals(1000, events.size());
    }

    @Test
    public void test_event_fields() {
        final LogNotificationEvent event =
                new LogNotificationEvent(12345L, "ERROR", "com.example.MyClass", "something failed", "java.lang.NullPointerException");

        assertEquals(12345L, event.getTimestamp());
        assertEquals("ERROR", event.getLevel());
        assertEquals("com.example.MyClass", event.getLoggerName());
        assertEquals("something failed", event.getMessage());
        assertEquals("java.lang.NullPointerException", event.getThrowable());
    }

    @Test
    public void test_event_null_throwable() {
        final LogNotificationEvent event = new LogNotificationEvent(99999L, "WARN", "com.example.Service", "warning message", null);

        assertEquals(99999L, event.getTimestamp());
        assertEquals("WARN", event.getLevel());
        assertEquals("com.example.Service", event.getLoggerName());
        assertEquals("warning message", event.getMessage());
        assertNull(event.getThrowable());
    }

    @Test
    public void test_singleton() {
        final LogNotificationBuffer instance1 = LogNotificationBuffer.getInstance();
        final LogNotificationBuffer instance2 = LogNotificationBuffer.getInstance();

        assertNotNull(instance1);
        assertSame(instance1, instance2);
    }

    @Test
    public void test_offer_concurrent() throws Exception {
        final LogNotificationBuffer buffer = LogNotificationBuffer.getInstance();
        final int threadCount = 8;
        final int eventsPerThread = 100;
        final ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(threadCount);

        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int i = 0; i < eventsPerThread; i++) {
                        buffer.offer(new LogNotificationEvent(System.currentTimeMillis(), "ERROR", "thread-" + threadId, "msg-" + i, null));
                    }
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(doneLatch.await(10, TimeUnit.SECONDS));
        executor.shutdown();

        final List<LogNotificationEvent> events = buffer.drainAll();
        assertNotNull(events);
        final int totalOffered = threadCount * eventsPerThread;
        assertTrue("Expected at most " + totalOffered + " events but got " + events.size(), events.size() <= totalOffered);
        assertTrue("Expected at least 1 event but got " + events.size(), events.size() > 0);
    }
}
