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
}
