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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link LogUtil}. No container required.
 */
public class LogUtilTest {

    private static final Logger logger = LogManager.getLogger(LogUtilTest.class);

    /** A second logger, to show that the same key under another logger is another condition. */
    private static final Logger otherLogger = LogManager.getLogger(LogUtilTest.class.getName() + "Other");

    @BeforeEach
    public void clearWarnOnce() {
        LogUtil.resetWarnOnce();
    }

    @AfterEach
    public void leaveWarnOnceClean() {
        LogUtil.resetWarnOnce();
    }

    @Test
    public void test_nullStaysNull() {
        assertNull(LogUtil.sanitize(null));
    }

    @Test
    public void test_ordinaryValueIsUnchanged() {
        assertEquals("alice", LogUtil.sanitize("alice"));
        assertEquals("山田 太郎@EXAMPLE.COM", LogUtil.sanitize("山田 太郎@EXAMPLE.COM"));
    }

    @Test
    public void test_lineBreaksAndOtherControlCharactersAreReplaced() {
        assertEquals("a?b", LogUtil.sanitize("a\nb"));
        assertEquals("a?b", LogUtil.sanitize("a\rb"));
        assertEquals("a??b", LogUtil.sanitize("a\r\nb"));
        assertEquals("a?b", LogUtil.sanitize("a\tb"));
        assertEquals("a?b", LogUtil.sanitize("a\u0000b"));
        assertEquals("a?b", LogUtil.sanitize("a\u007fb"));
    }

    @Test
    public void test_unicodeLineSeparatorsAreReplaced() {
        // \p{Cntrl} is ASCII-only: NEL and the line and paragraph separators need their own entry,
        // as a consumer that splits on them would otherwise still see a second line.
        assertEquals("a?b", LogUtil.sanitize("a\u0085b"));
        assertEquals("a?b", LogUtil.sanitize("a\u2028b"));
        assertEquals("a?b", LogUtil.sanitize("a\u2029b"));
    }

    @Test
    public void test_longValueIsCutAndMarked() {
        final String longValue = "x".repeat(LogUtil.MAX_LOGGED_LENGTH + 50);
        assertEquals("x".repeat(LogUtil.MAX_LOGGED_LENGTH) + "...", LogUtil.sanitize(longValue));
        assertEquals("x".repeat(LogUtil.MAX_LOGGED_LENGTH), LogUtil.sanitize("x".repeat(LogUtil.MAX_LOGGED_LENGTH)));
    }

    // ---- warnOnce ----

    @Test
    public void test_warnOnce_firstCallLogsAtWarnWithParamsSubstituted() {
        final LogCapturingAppender capture = LogCapturingAppender.attach(LogUtilTest.class);
        try {
            assertTrue(LogUtil.warnOnce(logger, "key", "missing {} for type={}", "plugin-a", "saml"));
            assertEquals(List.of("missing plugin-a for type=saml"), capture.warnings());
            assertEquals(1, capture.events().size());
            assertNull(capture.events().get(0).getThrown(), "no stack trace");
            assertTrue(capture.errors().isEmpty());
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_warnOnce_sameKeyAgainLogsNothingAndReturnsFalse() {
        final LogCapturingAppender capture = LogCapturingAppender.attach(LogUtilTest.class);
        try {
            assertTrue(LogUtil.warnOnce(logger, "key", "first {}", 1));
            assertFalse(LogUtil.warnOnce(logger, "key", "first {}", 1));
            assertFalse(LogUtil.warnOnce(logger, "key", "other message, same key"));
            assertEquals(List.of("first 1"), capture.warnings());
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_warnOnce_eachOfTwoKeysIsLoggedOnce() {
        final LogCapturingAppender capture = LogCapturingAppender.attach(LogUtilTest.class);
        try {
            assertTrue(LogUtil.warnOnce(logger, "a", "line {}", "a"));
            assertTrue(LogUtil.warnOnce(logger, "b", "line {}", "b"));
            assertFalse(LogUtil.warnOnce(logger, "a", "line {}", "a"));
            assertFalse(LogUtil.warnOnce(logger, "b", "line {}", "b"));
            assertEquals(List.of("line a", "line b"), capture.warnings());
        } finally {
            capture.detach();
        }
    }

    /** The state is shared by every caller, so the logger name is what keeps two classes' keys apart. */
    @Test
    public void test_warnOnce_sameKeyUnderTwoLoggersIsIndependent() {
        final LogCapturingAppender capture = LogCapturingAppender.attach(LogUtilTest.class);
        final LogCapturingAppender otherCapture = LogCapturingAppender.attach(otherLogger.getName());
        try {
            assertTrue(LogUtil.warnOnce(logger, "key", "from the first logger"));
            assertTrue(LogUtil.warnOnce(otherLogger, "key", "from the second logger"));
            assertFalse(LogUtil.warnOnce(logger, "key", "from the first logger"));
            assertFalse(LogUtil.warnOnce(otherLogger, "key", "from the second logger"));
            assertEquals(List.of("from the first logger"), capture.warnings());
            assertEquals(List.of("from the second logger"), otherCapture.warnings());
        } finally {
            otherCapture.detach();
            capture.detach();
        }
    }

    /**
     * The log level can change at runtime (Admin log level), so a call made while WARN is off must
     * not use up the key: the miss would otherwise never be reported once WARN is switched on.
     */
    @Test
    public void test_warnOnce_warnDisabledLogsNothingAndDoesNotConsumeTheKey() {
        final LogCapturingAppender disabled = LogCapturingAppender.attach(LogUtilTest.class.getName(), Level.ERROR);
        try {
            assertFalse(LogUtil.warnOnce(logger, "key", "while off"));
            assertTrue(disabled.events().isEmpty(), disabled.renderedEvents().toString());
        } finally {
            disabled.detach();
        }

        final LogCapturingAppender enabled = LogCapturingAppender.attach(LogUtilTest.class.getName(), Level.WARN);
        try {
            assertTrue(LogUtil.warnOnce(logger, "key", "after on"));
            assertFalse(LogUtil.warnOnce(logger, "key", "after on"));
            assertEquals(List.of("after on"), enabled.warnings());
        } finally {
            enabled.detach();
        }
    }

    @Test
    public void test_warnOnce_concurrentCallsOnOneKeyLogExactlyOnce() throws Exception {
        final int threads = 16;
        final CountDownLatch ready = new CountDownLatch(threads);
        final CountDownLatch start = new CountDownLatch(1);
        final AtomicInteger logged = new AtomicInteger();
        final ExecutorService executor = Executors.newFixedThreadPool(threads);
        final LogCapturingAppender capture = LogCapturingAppender.attach(LogUtilTest.class);
        try {
            final List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                futures.add(executor.submit(() -> {
                    ready.countDown();
                    start.await();
                    if (LogUtil.warnOnce(logger, "raced", "only once")) {
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

    @Test
    public void test_resetWarnOnce_reportsEveryKeyAgain() {
        final LogCapturingAppender capture = LogCapturingAppender.attach(LogUtilTest.class);
        try {
            assertTrue(LogUtil.warnOnce(logger, "a", "line"));
            assertTrue(LogUtil.warnOnce(logger, "b", "line"));
            assertFalse(LogUtil.warnOnce(logger, "a", "line"));

            LogUtil.resetWarnOnce();

            assertTrue(LogUtil.warnOnce(logger, "a", "line"));
            assertTrue(LogUtil.warnOnce(logger, "b", "line"));
            assertEquals(4, capture.warnings().size());
        } finally {
            capture.detach();
        }
    }

    /** A trailing Throwable that no placeholder consumes is logged as the exception, as with {@link Logger#warn(String, Object...)}. */
    @Test
    public void test_warnOnce_trailingThrowableIsAttachedAsTheException() {
        final LogCapturingAppender capture = LogCapturingAppender.attach(LogUtilTest.class);
        try {
            final IllegalStateException cause = new IllegalStateException("boom");
            assertTrue(LogUtil.warnOnce(logger, "withCause", "failed for {}", "x", cause));
            assertTrue(LogUtil.warnOnce(logger, "withCauseOnly", "failed", cause));
            assertEquals(List.of("failed for x", "failed"), capture.warnings());
            assertNotNull(capture.events().get(0).getThrown());
            assertSame(cause, capture.events().get(0).getThrown());
            assertSame(cause, capture.events().get(1).getThrown());
        } finally {
            capture.detach();
        }
    }
}
