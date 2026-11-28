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
package org.codelibs.fess.app.web.base;

import java.util.function.Function;

import org.codelibs.fess.app.service.AccessTokenService;
import org.codelibs.fess.app.service.PasskeyService;
import org.codelibs.fess.app.service.UserService;
import org.codelibs.fess.app.web.admin.dashboard.AdminDashboardAction;
import org.codelibs.fess.app.web.api.FessApiAction;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.app.web.go.GoAction;
import org.codelibs.fess.app.web.login.LoginAction;
import org.codelibs.fess.app.web.logout.LogoutAction;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;

/**
 * Pins the gate of {@link FessBaseAction#godHandPrologue(ActionRuntime)} for a login session opened with a password on
 * {@code password.invalid.admin.passwords}: every action but the login, the logout and the API sends it to the login
 * page, which asks for the new password.
 */
public class FessBaseActionPasswordChangeTest extends UnitFessTestCase {

    /** What the rest of the prologue answers when the gate lets the request through. */
    private static final ActionResponse PASSED = HtmlResponse.fromRedirectPathAsIs("/passed");

    private FessUserBean userBean;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        suppressBindingOf(FessLoginAssist.class);
        suppressBindingOf(UserService.class);
        suppressBindingOf(PasskeyService.class);
        suppressBindingOf(AccessTokenService.class);
        final User user = new User();
        user.setName("admin");
        userBean = new FessUserBean(user);
        userBean.setPasswordChangeRequired(true);
    }

    @Test
    public void test_anAdminScreen_sendsTheSessionToTheLoginPage() {
        final ActionResponse response = prologue(new AdminDashboardAction());

        assertTrue(response instanceof HtmlResponse);
        assertTrue(((HtmlResponse) response).isRedirectTo());
        assertEquals("/login/", ((HtmlResponse) response).getRoutingPath());
    }

    @Test
    public void test_aSearchAction_sendsTheSessionToTheLoginPage() {
        final ActionResponse response = prologue(new GoAction());

        assertEquals("/login/", ((HtmlResponse) response).getRoutingPath());
    }

    @Test
    public void test_theLoginAndTheLogout_areServed() {
        assertSame(PASSED, prologue(new LoginAction()));
        assertSame(PASSED, prologue(new LogoutAction()));
    }

    @Test
    public void test_anApiAction_isServed() {
        // authorized by its access token, not by the session
        final FessApiAction apiAction = new FessApiAction() {
            @Override
            protected boolean isAccessAllowed() {
                return true;
            }
        };
        assertSame(PASSED, prologue(apiAction));
    }

    @Test
    public void test_aSessionWithoutTheMark_isServed() {
        userBean.setPasswordChangeRequired(false);
        assertSame(PASSED, prologue(new AdminDashboardAction()));
    }

    @Test
    public void test_noSession_isServed() {
        userBean = null;
        assertSame(PASSED, prologue(new AdminDashboardAction()));
    }

    /** Runs the prologue of {@link FessBaseAction}, with the rest of it (the action hook) answering {@link #PASSED}. */
    private ActionResponse prologue(final FessBaseAction action) {
        inject(action);
        action.fessLoginAssist = new FessLoginAssist() {
            private static final long serialVersionUID = 1L;

            @Override
            public OptionalThing<FessUserBean> getSavedUserBean() {
                return OptionalThing.ofNullable(userBean, () -> {
                    throw new IllegalStateException("no user");
                });
            }
        };
        final ViewHelper viewHelper = new ViewHelper();
        viewHelper.setActionHook(new ViewHelper.ActionHook() {
            @Override
            public ActionResponse godHandPrologue(final ActionRuntime runtime, final Function<ActionRuntime, ActionResponse> func) {
                return PASSED;
            }
        });
        action.viewHelper = viewHelper;
        return action.godHandPrologue(new ActionRuntime("/admin/", null, null));
    }
}
