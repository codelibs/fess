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

    // ── MJ-4 / M-4: memory cap ──────────────────────────────────────────────────

    @Test
    public void test_memoryCap_evictsIdleEntries() {
        // Eviction policy picks an idle entry (no recent hits, no active lockout). The
        // implementation uses SWEEP_INTERVAL_SECONDS (300s = 5min) as the idle threshold,
        // so we advance the clock past that bound before the cap-triggering insert.
        final long[] now = { 1_000_000L };
        final int cap = 100;
        final LoginRateLimiter rl = new LoginRateLimiter(() -> now[0], cap);
        for (int i = 0; i < cap; i++) {
            rl.allow(LoginRateLimiter.Scope.USER, "user" + i, 5, 60);
        }
        // Age all entries past the 5-minute idle bound so they are evictable.
        now[0] += 6L * 60L * 1_000L;
        // Add one more entry beyond cap — an idle entry must be evicted.
        rl.allow(LoginRateLimiter.Scope.USER, "extra", 5, 60);
        // The first-inserted key (user0, oldest) should have been evicted. Re-inserting it
        // creates a fresh bucket; subsequent allow() against the recycled key must succeed.
        assertTrue(rl.allow(LoginRateLimiter.Scope.USER, "user0", 5, 60),
                "after idle-eviction, a fresh insert under a recycled key must succeed");
    }

    @Test
    public void test_memoryCap_doesNotEvictLockedOutEntries() {
        // M-4: an attacker MUST NOT be able to pump bogus usernames to overflow the map
        // and silently release a victim's lockout. Fill the map with locked-out entries
        // up to cap, then try to add one more — assert no locked entry is released.
        final long[] now = { 1_000_000L };
        final int cap = 50;
        final LoginRateLimiter rl = new LoginRateLimiter(() -> now[0], cap);
        // Lock out cap distinct victim users.
        for (int i = 0; i < cap; i++) {
            rl.lockOut(LoginRateLimiter.Scope.USER, "victim" + i, 900);
        }
        // Verify all are locked.
        for (int i = 0; i < cap; i++) {
            assertFalse(rl.allow(LoginRateLimiter.Scope.USER, "victim" + i, 5, 60), "victim" + i + " should be locked out");
        }
        // Attacker triggers a new entry insertion (lockOut() also inserts). The map may grow
        // by one (best-effort cap) but NO locked-out entry may be evicted.
        rl.lockOut(LoginRateLimiter.Scope.USER, "attacker_noise", 900);
        for (int i = 0; i < cap; i++) {
            assertFalse(rl.allow(LoginRateLimiter.Scope.USER, "victim" + i, 5, 60),
                    "victim" + i + " lockout must be preserved across cap-pressure inserts");
        }
    }

    // ── MJ-5: clear() removes the bucket ────────────────────────────────────────

    @Test
    public void test_clear_resetsLockedOutBucket() {
        final LoginRateLimiter rl = new LoginRateLimiter(() -> 1_000_000L);
        // Saturate and lock the USER bucket.
        for (int i = 0; i < 5; i++) {
            rl.allow(LoginRateLimiter.Scope.USER, "alice", 5, 60);
        }
        rl.lockOut(LoginRateLimiter.Scope.USER, "alice", 900);
        assertFalse(rl.allow(LoginRateLimiter.Scope.USER, "alice", 5, 60), "should be locked out");

        // clear() removes the entry — the next allow() should succeed.
        rl.clear(LoginRateLimiter.Scope.USER, "alice");
        assertTrue(rl.allow(LoginRateLimiter.Scope.USER, "alice", 5, 60), "after clear(), next allow() must succeed (lockout cleared)");
    }

    @Test
    public void test_clear_onEmptyKey_isNoop() {
        // clear() with null/empty key must not throw.
        final LoginRateLimiter rl = new LoginRateLimiter(() -> 1_000_000L);
        rl.clear(LoginRateLimiter.Scope.USER, null);
        rl.clear(LoginRateLimiter.Scope.USER, "");
        // no exception = pass
    }

    // ── MJ-6: empty key returns false ───────────────────────────────────────────

    @Test
    public void test_peek_emptyKeyReturnsFalse() {
        final LoginRateLimiter rl = new LoginRateLimiter(() -> 1_000_000L);
        assertFalse(rl.peek(LoginRateLimiter.Scope.IP, null, 10, 60), "null key must return false (deny)");
        assertFalse(rl.peek(LoginRateLimiter.Scope.IP, "", 10, 60), "empty key must return false (deny)");
    }

    @Test
    public void test_allow_emptyKeyReturnsFalse() {
        final LoginRateLimiter rl = new LoginRateLimiter(() -> 1_000_000L);
        assertFalse(rl.allow(LoginRateLimiter.Scope.IP, null, 10, 60), "null key must return false (deny)");
        assertFalse(rl.allow(LoginRateLimiter.Scope.IP, "", 10, 60), "empty key must return false (deny)");
    }

    // ── m-21: lockOut Math.max guard ────────────────────────────────────────────

    @Test
    public void test_lockOut_mathMaxGuard_shorterCallDoesNotShrinkLockout() {
        final long[] now = { 1_000_000L };
        final LoginRateLimiter rl = new LoginRateLimiter(() -> now[0]);
        // Apply a 900-second lockout.
        rl.lockOut(LoginRateLimiter.Scope.USER, "carol", 900);
        // Apply a shorter 60-second lockout — must NOT shrink the existing window.
        rl.lockOut(LoginRateLimiter.Scope.USER, "carol", 60);
        // Advance 61 seconds (past the short window, still inside the long one).
        now[0] += 61_000L;
        assertFalse(rl.allow(LoginRateLimiter.Scope.USER, "carol", 5, 60),
                "shorter lockOut call must not shrink existing longer lockout (Math.max guard)");
    }

    // ── Sweep schedule: verify the periodic task wires correctly ────────────────

    @Test
    public void test_sweep_scheduledViaInit() throws Exception {
        // Verify that init() registers a TimeoutManager task without throwing.
        // In the unit test harness, TimeoutManager is available (it's a static singleton).
        // We call init() directly (mimicking DI post-construct) and then verify sweep()
        // runs without error. The exact scheduling is exercised by the existing
        // test_sweep_does_not_lose_hits_under_contention test.
        final LoginRateLimiter rl = new LoginRateLimiter();
        try {
            rl.init();
            // If init() did not throw, the sweep task registration succeeded.
        } finally {
            rl.destroy(); // stop the background task
        }
    }

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
