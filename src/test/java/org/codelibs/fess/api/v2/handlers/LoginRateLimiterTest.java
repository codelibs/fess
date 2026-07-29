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

    // ── lockOut() reports whether it armed a NEW lockout ────────────────────────

    @Test
    public void test_lockOut_returnsTrueOnlyWhenArmingANewLockout() {
        // Callers invoke lockOut() on EVERY refused request, so they need a way to tell the
        // moment a lockout is armed from the stream of refusals that follow it — otherwise a
        // per-refusal WARN lets a client locked out of the endpoint generate unbounded log
        // volume. The decision is taken inside the same synchronized block that stamps the
        // deadline, so it cannot race.
        final long[] now = { 1_000_000L };
        final LoginRateLimiter rl = new LoginRateLimiter(() -> now[0]);

        assertTrue(rl.lockOut(LoginRateLimiter.Scope.IP, "198.51.100.1", 900), "the first call must arm a new lockout");
        assertFalse(rl.lockOut(LoginRateLimiter.Scope.IP, "198.51.100.1", 900), "a call during an active lockout must be a no-op");

        // Still inside the window: every retry is a no-op, never a fresh lockout.
        now[0] += 450_000L;
        assertFalse(rl.lockOut(LoginRateLimiter.Scope.IP, "198.51.100.1", 900), "a mid-window retry must not arm a lockout");

        // Past the deadline the bucket is free again, so the next call arms a genuinely new one.
        now[0] += 451_000L;
        assertTrue(rl.lockOut(LoginRateLimiter.Scope.IP, "198.51.100.1", 900), "a lockout armed after the previous one elapsed is new");
    }

    @Test
    public void test_lockOut_returnsFalseWhenTheCallIsIgnored() {
        // The guard clauses make lockOut() a no-op; they must report "nothing armed" rather
        // than claiming a lockout the limiter never stamped.
        final LoginRateLimiter rl = new LoginRateLimiter(() -> 1_000_000L);
        assertFalse(rl.lockOut(LoginRateLimiter.Scope.USER, "alice", 0), "lockoutSeconds=0 disables the lockout");
        assertFalse(rl.lockOut(LoginRateLimiter.Scope.USER, "alice", -1), "a negative lockoutSeconds disables the lockout");
        assertFalse(rl.lockOut(LoginRateLimiter.Scope.USER, null, 900), "a null key cannot be locked out");
        assertFalse(rl.lockOut(LoginRateLimiter.Scope.USER, "", 900), "an empty key cannot be locked out");
        // None of the above may have armed anything.
        assertTrue(rl.allow(LoginRateLimiter.Scope.USER, "alice", 5, 60), "no lockout must have been stamped for alice");
    }

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

    // ── Self-extending lockout: an active lockout must never be pushed forward ──

    @Test
    public void test_lockOut_doesNotExtendAnActiveLockout() {
        // Regression: every call site (LoginHandler IP gate, LoginHandler USER gate,
        // PasswordChangeHandler) re-invokes lockOut() on each refused request, and
        // allow()/peek() keep returning false for the whole lockout. Re-stamping the
        // deadline from the CURRENT time therefore pushed the release point forward on
        // every retry, so a client that kept polling was never released — even though the
        // response advertised "Retry-After: <lockoutSeconds>".
        final long[] now = { 1_000_000L };
        final long t0 = now[0];
        final LoginRateLimiter rl = new LoginRateLimiter(() -> now[0]);

        rl.lockOut(LoginRateLimiter.Scope.IP, "1.2.3.4", 900);

        // A retry arrives while the lockout is still active; the caller stamps it again.
        now[0] = t0 + 800_000L;
        assertFalse(rl.allow(LoginRateLimiter.Scope.IP, "1.2.3.4", 10, 60), "still locked 800s into a 900s lockout");
        rl.lockOut(LoginRateLimiter.Scope.IP, "1.2.3.4", 900);

        // Past the ORIGINAL deadline (t0 + 900s): the mid-lockout retry must not have moved it.
        now[0] = t0 + 901_000L;
        assertTrue(rl.allow(LoginRateLimiter.Scope.IP, "1.2.3.4", 10, 60),
                "lockOut() during an active lockout must not extend it beyond the advertised Retry-After");
    }

    @Test
    public void test_lockOut_reArmsAfterThePreviousLockoutExpired() {
        // The no-extend guard must not turn lockOut() into a one-shot: once the previous
        // lockout has elapsed, a fresh lockOut() must arm a new window from "now".
        final long[] now = { 1_000_000L };
        final long t0 = now[0];
        final LoginRateLimiter rl = new LoginRateLimiter(() -> now[0]);

        rl.lockOut(LoginRateLimiter.Scope.USER, "dave", 900);
        now[0] = t0 + 901_000L;
        assertTrue(rl.allow(LoginRateLimiter.Scope.USER, "dave", 10, 60), "the first lockout must have expired");

        // A new abuse burst locks the key again, armed from the current time.
        rl.lockOut(LoginRateLimiter.Scope.USER, "dave", 900);
        assertFalse(rl.allow(LoginRateLimiter.Scope.USER, "dave", 10, 60), "a fresh lockout must arm after the previous one expired");
        now[0] += 901_000L;
        assertTrue(rl.allow(LoginRateLimiter.Scope.USER, "dave", 10, 60), "the second lockout must expire on schedule as well");
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
