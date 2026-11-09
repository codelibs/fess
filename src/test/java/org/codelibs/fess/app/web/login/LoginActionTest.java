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
import org.codelibs.fess.app.service.UserService;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
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

    /**
     * The directory ignores the blanks around a uid, so " alice" signs in as alice. Its failures have to be counted
     * against alice's allowance, not against a second bucket of their own.
     */
    @Test
    public void test_theUserAllowanceIgnoresBlanksAroundTheName() {
        final int userLimit = ComponentUtil.getFessConfig().getThemeApiLoginRateLimitPerUserPerMinuteAsInteger();
        for (int i = 0; i < userLimit; i++) {
            assertFalse(action.isThrottled(limiter, "198.51.100.8", " alice"));
            action.recordFailure(limiter, "198.51.100.8", " alice");
        }
        assertTrue(action.isThrottled(limiter, "198.51.100.8", "alice"));
        assertTrue(action.isThrottled(limiter, "198.51.100.8", "alice "));
        assertTrue(action.isThrottled(limiter, "198.51.100.8", "\tAlice\t"));
        // a blank inside the name is a different name
        assertFalse(action.isThrottled(limiter, "198.51.100.8", "al ice"));
    }

    @Test
    public void test_aCorrectLoginUnderTheLimitStillPasses() {
        assertFalse(action.isThrottled(limiter, "198.51.100.5", "alice"));
        action.forgiveAttempt(limiter, "198.51.100.5", "alice");
        assertFalse(action.isThrottled(limiter, "198.51.100.5", "alice"));
    }

    @Test
    public void test_changePassword_withoutTheOldPasswordInTheSessionIsAValidationError() throws Exception {
        // /login/ clears the old password that the forced password change keeps in the session, so a
        // POST to /login/changePassword after it arrives with no old password to look the user up by.
        suppressBindingOf(FessLoginAssist.class);
        suppressBindingOf(UserService.class);
        final LoginAction changeAction = new LoginAction() {
            {
                fessConfig = ComponentUtil.getFessConfig();
            }

            @Override
            protected OptionalThing<FessUserBean> getUserBean() {
                return OptionalThing.of(new FessUserBean(new FessUser() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String getName() {
                        return "admin";
                    }

                    @Override
                    public String[] getRoleNames() {
                        return new String[0];
                    }

                    @Override
                    public String[] getGroupNames() {
                        return new String[0];
                    }

                    @Override
                    public String[] getPermissions() {
                        return new String[0];
                    }
                }));
            }
        };
        inject(changeAction);
        final java.lang.reflect.Field sysField = org.codelibs.fess.app.web.base.FessBaseAction.class.getDeclaredField("systemHelper");
        sysField.setAccessible(true);
        final SystemHelper systemHelper = new SystemHelper();
        sysField.set(changeAction, systemHelper);
        ComponentUtil.register(systemHelper, "systemHelper");
        final PasswordForm form = new PasswordForm();
        form.password = "Abcdefg1234";
        form.confirmPassword = "Abcdefg1234";

        assertValidationError(() -> changeAction.changePassword(form)).handle(data -> {
            data.requiredMessageOf("_global", "errors.no_user_for_changing_password");
        });
    }

    /** Registers a config with the given directory, the given name limit and the reserved name admin. */
    @SuppressWarnings("serial")
    private LoginAction actionWithDirectory(final String providerUrl, final int maxUsernameLength) {
        final FessConfig config = new FessConfig.SimpleImpl() {
            @Override
            public String getLdapProviderUrl() {
                return providerUrl;
            }

            @Override
            public Integer getLdapMaxUsernameLengthAsInteger() {
                return Integer.valueOf(maxUsernameLength);
            }

            @Override
            public String getAuthenticationAdminUsers() {
                return "admin";
            }

            @Override
            public String getAuthenticationAdminUsersIgnoreCase() {
                return "false";
            }

            @Override
            public Integer getThemeApiLoginRateLimitPerIpPerMinuteAsInteger() {
                return Integer.valueOf(1000);
            }

            @Override
            public Integer getThemeApiLoginRateLimitPerUserPerMinuteAsInteger() {
                return Integer.valueOf(3);
            }

            @Override
            public Integer getThemeApiLoginLockoutSecondsAsInteger() {
                return Integer.valueOf(60);
            }
        };
        ComponentUtil.setFessConfig(config);
        return new LoginAction() {
            {
                fessConfig = config;
            }
        };
    }

    /**
     * ldap.max.username.length makes the directory treat alice1, alice2 ... as alice, so the guesses at alice's password
     * must share one allowance. The v2 login must count them in the same bucket.
     */
    @Test
    public void test_theUserAllowanceFollowsTheNameTheDirectoryIsAsked() {
        final LoginAction ldapAction = actionWithDirectory("ldap://localhost:389", 5);
        final String ip = "198.51.100.20";
        final String[] spellings = { "alice1", "ALICE2", " alice3", "alice4 ", "alice" };
        for (int i = 0; i < 3; i++) {
            assertFalse(ldapAction.isThrottled(limiter, ip, spellings[i]));
            ldapAction.recordFailure(limiter, ip, spellings[i]);
        }
        for (final String spelling : spellings) {
            assertTrue(spelling, ldapAction.isThrottled(limiter, ip, spelling));
        }
        // another address and another name are not affected
        assertFalse(ldapAction.isThrottled(limiter, "198.51.100.21", "alice1"));
        assertFalse(ldapAction.isThrottled(limiter, ip, "bob1"));
        // only the part inside the limit is shared
        assertFalse(ldapAction.isThrottled(limiter, ip, "carol1"));
    }

    @Test
    public void test_theUserAllowanceKeepsWholeNamesWhenTheNameLimitIsOff() {
        final LoginAction ldapAction = actionWithDirectory("ldap://localhost:389", -1);
        final String ip = "198.51.100.22";
        for (int i = 0; i < 3; i++) {
            ldapAction.recordFailure(limiter, ip, "alice1");
        }
        assertTrue(ldapAction.isThrottled(limiter, ip, "alice1"));
        assertFalse(ldapAction.isThrottled(limiter, ip, "alice2"));
        assertFalse(ldapAction.isThrottled(limiter, ip, "alice"));
    }

    /** Without a directory nothing is cut, whatever the limit says: local users are told apart by their whole names. */
    @Test
    public void test_theUserAllowanceKeepsWholeNamesWithoutADirectory() {
        final LoginAction localAction = actionWithDirectory("", 5);
        final String ip = "198.51.100.23";
        for (int i = 0; i < 3; i++) {
            localAction.recordFailure(limiter, ip, "alice1");
        }
        assertTrue(localAction.isThrottled(limiter, ip, "alice1"));
        assertFalse(localAction.isThrottled(limiter, ip, "alice2"));
        assertFalse(localAction.isThrottled(limiter, ip, "alice"));
    }

    /** A reserved name never goes to the directory, so it is not cut: adminxyz is not admin's bucket. */
    @Test
    public void test_theUserAllowanceDoesNotCutAReservedName() {
        final LoginAction ldapAction = actionWithDirectory("ldap://localhost:389", 5);
        final String ip = "198.51.100.24";
        for (int i = 0; i < 3; i++) {
            ldapAction.recordFailure(limiter, ip, "admin");
        }
        assertTrue(ldapAction.isThrottled(limiter, ip, "admin"));
        assertTrue(ldapAction.isThrottled(limiter, ip, " admin "));
        assertFalse(ldapAction.isThrottled(limiter, ip, "adminxyz"));
        // and the other way round: a failure for adminxyz does not count for admin
        for (int i = 0; i < 3; i++) {
            ldapAction.recordFailure(limiter, "198.51.100.25", "adminxyz");
        }
        assertFalse(ldapAction.isThrottled(limiter, "198.51.100.25", "admin"));
    }
}
