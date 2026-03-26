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
package org.codelibs.fess.helper;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.helper.LogNotificationHelper.LogNotificationEvent;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class LogNotificationHelperTest extends UnitFessTestCase {

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }

    @Test
    public void test_constructor() {
        LogNotificationHelper helper = new LogNotificationHelper();
        assertNotNull(helper);
    }

    @Test
    public void test_init_and_destroy() {
        LogNotificationHelper helper = new LogNotificationHelper();
        // init() registers a TimeoutTarget with TimeoutManager
        helper.init();
        // destroy() cancels the task and flushes; should not throw
        helper.destroy();
    }

    @Test
    public void test_destroy_beforeInit() {
        LogNotificationHelper helper = new LogNotificationHelper();
        // destroy() without init() should not throw NPE because of null checks
        helper.destroy();
    }

    @Test
    public void test_init_destroy_multipleRounds() {
        LogNotificationHelper helper = new LogNotificationHelper();
        helper.init();
        helper.destroy();
        // Second round
        helper.init();
        helper.destroy();
    }

    @Test
    public void test_offer_and_drainAll() {
        LogNotificationHelper helper = new LogNotificationHelper();
        helper.offer(new LogNotificationEvent(1000L, "ERROR", "com.example.Foo", "message1", "throwable1"));
        helper.offer(new LogNotificationEvent(2000L, "WARN", "com.example.Bar", "message2", null));

        List<LogNotificationEvent> events = helper.drainAll();
        assertEquals(2, events.size());
        assertEquals("message1", events.get(0).getMessage());
        assertEquals("message2", events.get(1).getMessage());
    }

    @Test
    public void test_drainAll_empty() {
        LogNotificationHelper helper = new LogNotificationHelper();
        List<LogNotificationEvent> events = helper.drainAll();
        assertNotNull(events);
        assertTrue(events.isEmpty());
    }

    @Test
    public void test_drainAll_clears() {
        LogNotificationHelper helper = new LogNotificationHelper();
        helper.offer(new LogNotificationEvent(1000L, "ERROR", "org.test", "msg", null));
        List<LogNotificationEvent> first = helper.drainAll();
        assertEquals(1, first.size());
        List<LogNotificationEvent> second = helper.drainAll();
        assertTrue(second.isEmpty());
    }

    @Test
    public void test_offer_bufferCapacity() {
        LogNotificationHelper helper = new LogNotificationHelper();
        // Default buffer size is 1000; offer more than that
        for (int i = 0; i < 1100; i++) {
            helper.offer(new LogNotificationEvent(i, "ERROR", "org.test", "msg" + i, null));
        }
        List<LogNotificationEvent> events = helper.drainAll();
        // Should be capped at buffer size (1000)
        assertTrue(events.size() <= 1000);
        assertTrue(events.size() > 0);
    }

    @Test
    public void test_offer_bufferCapacity_dropsOldest() {
        LogNotificationHelper helper = new LogNotificationHelper();
        // Fill beyond default capacity
        for (int i = 0; i < 1100; i++) {
            helper.offer(new LogNotificationEvent(i, "ERROR", "org.test", "msg" + i, null));
        }
        List<LogNotificationEvent> events = helper.drainAll();
        // The newest events should be retained; oldest dropped
        // Last event should be msg1099
        LogNotificationEvent last = events.get(events.size() - 1);
        assertEquals("msg1099", last.getMessage());
    }

    @Test
    public void test_concurrent_offerAndDrain() throws Exception {
        LogNotificationHelper helper = new LogNotificationHelper();
        int numThreads = 8;
        int eventsPerThread = 200;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numThreads);
        AtomicInteger totalDrained = new AtomicInteger(0);

        for (int t = 0; t < numThreads; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int i = 0; i < eventsPerThread; i++) {
                        helper.offer(new LogNotificationEvent(i, "ERROR", "org.test", "t" + threadId + "-" + i, null));
                        // Periodically drain
                        if (i % 50 == 49) {
                            List<LogNotificationEvent> drained = helper.drainAll();
                            totalDrained.addAndGet(drained.size());
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(doneLatch.await(30, TimeUnit.SECONDS));
        executor.shutdown();

        // Drain remaining
        totalDrained.addAndGet(helper.drainAll().size());

        // Total drained should be <= total offered (some may be dropped at capacity)
        int totalOffered = numThreads * eventsPerThread;
        assertTrue(totalDrained.get() > 0);
        assertTrue(totalDrained.get() <= totalOffered);
    }
}
