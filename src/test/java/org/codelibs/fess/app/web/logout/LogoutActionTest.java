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
package org.codelibs.fess.app.web.logout;

import java.util.ArrayList;
import java.util.List;

import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.helper.ActivityHelper;
import org.codelibs.fess.helper.UserInfoHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.sso.SsoManager;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.response.HtmlResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * What {@code /logout/} records.
 *
 * <p>The endpoint is reachable without a session -- it has to be, since a caller whose session
 * has already expired still arrives here -- so whatever one request writes is what an unbounded
 * loop of them writes.</p>
 */
public class LogoutActionTest extends UnitFessTestCase {

    /** Marker returned instead of the real redirect, which needs the action path resolver. */
    private static final HtmlResponse REDIRECT_TO_LOGIN = HtmlResponse.fromRedirectPathAsIs("/login/");

    /** Records what would have reached audit.log. */
    private static ActivityHelper recordingActivityHelper(final List<String> audit) {
        ComponentUtil.register(new SsoManager(), "ssoManager");
        return new ActivityHelper() {
            @Override
            public void logout(final OptionalThing<FessUserBean> user) {
                audit.add("action:LOGOUT\tuser:" + user.map(FessUserBean::getUserId).orElse("-"));
            }
        };
    }

    @Test
    public void test_index_recordsTheLogoutOfALoggedInUser() {
        final List<String> audit = new ArrayList<>();
        final TestableLogoutAction action = new TestableLogoutAction("carol", recordingActivityHelper(audit));

        assertSame(REDIRECT_TO_LOGIN, action.index());
        assertEquals(List.of("action:LOGOUT\tuser:carol"), audit);
    }

    @Test
    public void test_index_recordsNothingWhenNobodyWasLoggedIn() {
        // An anonymous caller used to append "action:LOGOUT user:-" per request: a line that names
        // nobody, in the log an operator reads to find out who did what.
        final List<String> audit = new ArrayList<>();
        final TestableLogoutAction action = new TestableLogoutAction(null, recordingActivityHelper(audit));

        assertSame(REDIRECT_TO_LOGIN, action.index());
        assertEquals(List.of(), audit);
    }

    @Test
    public void test_index_endsTheSessionEitherWay() {
        // The record is the only thing conditional on a user being present; the logout itself
        // must still run, or a half-established session would survive it.
        final List<String> audit = new ArrayList<>();
        final TestableLogoutAction loggedIn = new TestableLogoutAction("carol", recordingActivityHelper(audit));
        loggedIn.index();
        assertEquals(1, loggedIn.logoutCount);

        final TestableLogoutAction anonymous = new TestableLogoutAction(null, recordingActivityHelper(audit));
        anonymous.index();
        assertEquals(1, anonymous.logoutCount);
    }

    /**
     * LogoutAction with the seams a plain unit test cannot provide: the user bean, the login
     * assist, the user-code cookie and the action path resolver.
     */
    private static class TestableLogoutAction extends LogoutAction {
        private final OptionalThing<FessUserBean> userBean;

        int logoutCount;

        TestableLogoutAction(final String userId, final ActivityHelper helper) {
            userBean = userId == null ? OptionalThing.empty() : OptionalThing.of(new FessUserBean(new StubFessUser(userId)));
            activityHelper = helper;
            fessLoginAssist = new org.codelibs.fess.app.web.base.login.FessLoginAssist() {
                @Override
                public void logout() {
                    logoutCount++;
                }
            };
            userInfoHelper = new UserInfoHelper() {
                @Override
                public void deleteUserCodeFromCookie(final HttpServletRequest request) {
                    // no cookie store in a unit test
                }
            };
        }

        @Override
        public OptionalThing<FessUserBean> getUserBean() {
            return userBean;
        }

        @Override
        protected HtmlResponse redirect(final Class<?> actionType) {
            return REDIRECT_TO_LOGIN;
        }
    }

    /** Minimal {@link FessUser} with no roles, groups or permissions. */
    private static class StubFessUser implements FessUser {
        private static final long serialVersionUID = 1L;

        private final String name;

        StubFessUser(final String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
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
    }
}
