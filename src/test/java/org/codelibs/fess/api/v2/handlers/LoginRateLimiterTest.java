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
package org.codelibs.fess.api.v2.handlers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class LoginRateLimiterTest extends UnitFessTestCase {

    @Test
    public void test_allowsUpToLimitThenBlocks_perIp() {
        final LoginRateLimiter rl = new LoginRateLimiter(/* clock */ () -> 1_000_000L);
        for (int i = 0; i < 10; i++) {
            assertTrue(rl.allow(LoginRateLimiter.Scope.IP, "1.2.3.4", 10, 60));
        }
        assertFalse(rl.allow(LoginRateLimiter.Scope.IP, "1.2.3.4", 10, 60));
    }

    @Test
    public void test_separateKeysHaveSeparateBuckets() {
        final LoginRateLimiter rl = new LoginRateLimiter(() -> 1_000_000L);
        for (int i = 0; i < 5; i++) {
            assertTrue(rl.allow(LoginRateLimiter.Scope.USER, "alice", 5, 60));
        }
        assertFalse(rl.allow(LoginRateLimiter.Scope.USER, "alice", 5, 60));
        assertTrue(rl.allow(LoginRateLimiter.Scope.USER, "bob", 5, 60));
    }

    @Test
    public void test_windowSlidesAfterDuration() {
        final long[] now = { 1_000_000L };
        final LoginRateLimiter rl = new LoginRateLimiter(() -> now[0]);
        for (int i = 0; i < 10; i++) {
            assertTrue(rl.allow(LoginRateLimiter.Scope.IP, "1.2.3.4", 10, 60));
        }
        assertFalse(rl.allow(LoginRateLimiter.Scope.IP, "1.2.3.4", 10, 60));
        now[0] += 61_000L;
        assertTrue(rl.allow(LoginRateLimiter.Scope.IP, "1.2.3.4", 10, 60));
    }

    @Test
    public void test_lockoutPreventsAttemptsEvenAfterWindowExpires() {
        final long[] now = { 1_000_000L };
        final LoginRateLimiter rl = new LoginRateLimiter(() -> now[0]);
        for (int i = 0; i < 10; i++) {
            rl.allow(LoginRateLimiter.Scope.IP, "1.2.3.4", 10, 60);
        }
        rl.lockOut(LoginRateLimiter.Scope.IP, "1.2.3.4", 900);
        now[0] += 61_000L;
        assertFalse(rl.allow(LoginRateLimiter.Scope.IP, "1.2.3.4", 10, 60));
        now[0] += 900_000L;
        assertTrue(rl.allow(LoginRateLimiter.Scope.IP, "1.2.3.4", 10, 60));
    }

    @Test
    public void test_zeroThresholdDisablesLimit() {
        final LoginRateLimiter rl = new LoginRateLimiter(() -> 1_000_000L);
        for (int i = 0; i < 1000; i++) {
            assertTrue(rl.allow(LoginRateLimiter.Scope.IP, "1.2.3.4", 0, 60));
        }
    }

    @Test
    public void test_sweep_does_not_lose_hits_under_contention() throws Exception {
        // Use a moving clock so hits stay within window for the duration of the test.
        final long[] now = { 1_000_000L };
        final LoginRateLimiter rl = new LoginRateLimiter(() -> now[0]);
        final int threadCount = 8;
        final int attemptsPerThread = 200;
        // Large window and limit so every allow() succeeds during the test window.
        final int maxPerWindow = threadCount * attemptsPerThread + 1000;
        final int windowSeconds = 3600;

        final CountDownLatch start = new CountDownLatch(1);
        final AtomicInteger admitted = new AtomicInteger(0);
        final ExecutorService exec = Executors.newFixedThreadPool(threadCount + 1);
        final List<Future<?>> futures = new ArrayList<>();

        // Threads calling allow() concurrently.
        for (int t = 0; t < threadCount; t++) {
            futures.add(exec.submit(() -> {
                try {
                    start.await();
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                for (int i = 0; i < attemptsPerThread; i++) {
                    if (rl.allow(LoginRateLimiter.Scope.IP, "10.0.0.1", maxPerWindow, windowSeconds)) {
                        admitted.incrementAndGet();
                    }
                }
                return null;
            }));
        }

        // One thread spins sweep().
        final AtomicInteger sweepCount = new AtomicInteger(0);
        futures.add(exec.submit(() -> {
            try {
                start.await();
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            for (int i = 0; i < 500; i++) {
                rl.sweep();
                sweepCount.incrementAndGet();
            }
            return null;
        }));

        start.countDown();
        exec.shutdown();
        assertTrue(exec.awaitTermination(30, TimeUnit.SECONDS), "test timed out");

        // All admits should equal the total attempts because the window is large enough.
        // We accept a tolerance of 0 here because the limit is set much higher than the
        // number of attempts (no false rejections from bucket exhaustion).
        final int expected = threadCount * attemptsPerThread;
        assertEquals("expected all " + expected + " attempts to be admitted; got " + admitted.get() + " sweeps=" + sweepCount.get(),
                expected, admitted.get());
    }

    @Test
    public void test_allow_atomicity_under_contention() throws Exception {
        // 20 threads all call allow() with limit=10. The total true results must be exactly 10.
        final LoginRateLimiter rl = new LoginRateLimiter(() -> 1_000_000L);
        final int limit = 10;
        final int threads = 20;

        final CountDownLatch start = new CountDownLatch(1);
        final AtomicInteger admitted = new AtomicInteger(0);
        final ExecutorService exec = Executors.newFixedThreadPool(threads);
        final List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            futures.add(exec.submit(() -> {
                try {
                    start.await();
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (rl.allow(LoginRateLimiter.Scope.USER, "testuser", limit, 60)) {
                    admitted.incrementAndGet();
                }
                return null;
            }));
        }
        start.countDown();
        exec.shutdown();
        assertTrue(exec.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals("expected exactly " + limit + " admissions under contention, got " + admitted.get(), limit, admitted.get());
    }
}
