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
package org.codelibs.fess.app.web.login;

import org.codelibs.fess.api.v2.handlers.LoginRateLimiter;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class LoginActionTest extends UnitFessTestCase {

    private LoginAction action;
    private LoginRateLimiter limiter;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        limiter = new LoginRateLimiter();
        limiter.init();
        action = new LoginAction() {
            {
                fessConfig = ComponentUtil.getFessConfig();
            }
        };
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        limiter.destroy();
        super.tearDown(testInfo);
    }

    @Test
    public void test_aGuessingRunIsStoppedAtThePerAddressAllowance() {
        final int ipLimit = ComponentUtil.getFessConfig().getThemeApiLoginRateLimitPerIpPerMinuteAsInteger();
        int refused = 0;
        for (int i = 0; i < ipLimit * 4; i++) {
            if (action.isThrottled(limiter, "198.51.100.1", "user" + i)) {
                refused++;
            } else {
                action.recordFailure(limiter, "198.51.100.1", "user" + i);
            }
        }
        assertEquals(ipLimit * 3, refused);
    }

    @Test
    public void test_aSuccessGivesBackOnlyItsOwnAddressSlot() {
        final int ipLimit = ComponentUtil.getFessConfig().getThemeApiLoginRateLimitPerIpPerMinuteAsInteger();
        int admitted = 0;
        for (int round = 0; round < 3; round++) {
            for (int i = 0; i < ipLimit - 1; i++) {
                if (!action.isThrottled(limiter, "198.51.100.2", "spray" + round + "x" + i)) {
                    admitted++;
                }
            }
            // one real login in between: it takes a slot and gives the same slot back
            if (!action.isThrottled(limiter, "198.51.100.2", "real")) {
                action.forgiveAttempt(limiter, "198.51.100.2", "real");
            }
        }
        assertTrue(admitted <= ipLimit);
    }

    @Test
    public void test_theUserAllowanceIsPerAddressAndName() {
        final int userLimit = ComponentUtil.getFessConfig().getThemeApiLoginRateLimitPerUserPerMinuteAsInteger();
        for (int i = 0; i < userLimit; i++) {
            assertFalse(action.isThrottled(limiter, "198.51.100.3", "admin"));
            action.recordFailure(limiter, "198.51.100.3", "admin");
        }
        assertTrue(action.isThrottled(limiter, "198.51.100.3", "admin"));
        // another address is not affected, so the name cannot be locked out from elsewhere
        assertFalse(action.isThrottled(limiter, "198.51.100.4", "admin"));
    }

    @Test
    public void test_theUserAllowanceIgnoresTheCaseOfTheName() {
        final int userLimit = ComponentUtil.getFessConfig().getThemeApiLoginRateLimitPerUserPerMinuteAsInteger();
        for (int i = 0; i < userLimit; i++) {
            assertFalse(action.isThrottled(limiter, "198.51.100.6", "alice"));
            action.recordFailure(limiter, "198.51.100.6", "alice");
        }
        assertTrue(action.isThrottled(limiter, "198.51.100.6", "Alice"));
        assertTrue(action.isThrottled(limiter, "198.51.100.6", "ALICE"));
        // the same bucket as the v2 login uses for the name
        assertTrue(action.isThrottled(limiter, "198.51.100.6", "aLiCe"));
        assertFalse(action.isThrottled(limiter, "198.51.100.7", "Alice"));
    }

    @Test
    public void test_aCorrectLoginUnderTheLimitStillPasses() {
        assertFalse(action.isThrottled(limiter, "198.51.100.5", "alice"));
        action.forgiveAttempt(limiter, "198.51.100.5", "alice");
        assertFalse(action.isThrottled(limiter, "198.51.100.5", "alice"));
    }
}
