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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link LogOnce}. No container required.
 */
public class LogOnceTest {

    private static final Logger logger = LogManager.getLogger(LogOnceTest.class);

    @Test
    public void test_firstCall_logsAtWarnWithParamsSubstituted() {
        final LogOnce logOnce = new LogOnce();
        final LogCapturingAppender capture = LogCapturingAppender.attach(LogOnceTest.class);
        try {
            assertTrue(logOnce.warn(logger, "key", "missing {} for type={}", "plugin-a", "saml"));
            assertEquals(List.of("missing plugin-a for type=saml"), capture.warnings());
            assertEquals(1, capture.events().size());
            assertNull(capture.events().get(0).getThrown(), "no stack trace");
            assertTrue(capture.errors().isEmpty());
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_sameKeyAgain_logsNothingAndReturnsFalse() {
        final LogOnce logOnce = new LogOnce();
        final LogCapturingAppender capture = LogCapturingAppender.attach(LogOnceTest.class);
        try {
            assertTrue(logOnce.warn(logger, "key", "first {}", 1));
            assertFalse(logOnce.warn(logger, "key", "first {}", 1));
            assertFalse(logOnce.warn(logger, "key", "other message, same key"));
            assertEquals(List.of("first 1"), capture.warnings());
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_twoKeys_logOnceEach() {
        final LogOnce logOnce = new LogOnce();
        final LogCapturingAppender capture = LogCapturingAppender.attach(LogOnceTest.class);
        try {
            assertTrue(logOnce.warn(logger, "a", "line {}", "a"));
            assertTrue(logOnce.warn(logger, "b", "line {}", "b"));
            assertFalse(logOnce.warn(logger, "a", "line {}", "a"));
            assertFalse(logOnce.warn(logger, "b", "line {}", "b"));
            assertEquals(List.of("line a", "line b"), capture.warnings());
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_instancesDoNotShareKeys() {
        final LogCapturingAppender capture = LogCapturingAppender.attach(LogOnceTest.class);
        try {
            assertTrue(new LogOnce().warn(logger, "key", "from the first instance"));
            assertTrue(new LogOnce().warn(logger, "key", "from the second instance"));
            assertEquals(2, capture.warnings().size());
        } finally {
            capture.detach();
        }
    }

    /**
     * The log level can change at runtime (Admin log level), so a call made while WARN is off must
     * not use up the key: the miss would otherwise never be reported once WARN is switched on.
     */
    @Test
    public void test_warnDisabled_logsNothingAndDoesNotConsumeTheKey() {
        final LogOnce logOnce = new LogOnce();
        final LogCapturingAppender disabled = LogCapturingAppender.attach(LogOnceTest.class.getName(), Level.ERROR);
        try {
            assertFalse(logOnce.warn(logger, "key", "while off"));
            assertTrue(disabled.events().isEmpty(), disabled.renderedEvents().toString());
        } finally {
            disabled.detach();
        }

        final LogCapturingAppender enabled = LogCapturingAppender.attach(LogOnceTest.class.getName(), Level.WARN);
        try {
            assertTrue(logOnce.warn(logger, "key", "after on"));
            assertFalse(logOnce.warn(logger, "key", "after on"));
            assertEquals(List.of("after on"), enabled.warnings());
        } finally {
            enabled.detach();
        }
    }

    @Test
    public void test_concurrentCallsOnOneKey_logExactlyOnce() throws Exception {
        final int threads = 16;
        final LogOnce logOnce = new LogOnce();
        final CountDownLatch ready = new CountDownLatch(threads);
        final CountDownLatch start = new CountDownLatch(1);
        final AtomicInteger logged = new AtomicInteger();
        final ExecutorService executor = Executors.newFixedThreadPool(threads);
        final LogCapturingAppender capture = LogCapturingAppender.attach(LogOnceTest.class);
        try {
            final List<Future<?>> futures = new java.util.ArrayList<>();
            for (int i = 0; i < threads; i++) {
                futures.add(executor.submit(() -> {
                    ready.countDown();
                    start.await();
                    if (logOnce.warn(logger, "raced", "only once")) {
                        logged.incrementAndGet();
                    }
                    return null;
                }));
            }
            assertTrue(ready.await(10, TimeUnit.SECONDS));
            start.countDown();
            for (final Future<?> future : futures) {
                future.get(10, TimeUnit.SECONDS);
            }
            assertEquals(1, logged.get());
            assertEquals(List.of("only once"), capture.warnings());
        } finally {
            executor.shutdownNow();
            capture.detach();
        }
    }
}
