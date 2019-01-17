/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import org.codelibs.fess.app.web.base.FessLoginAction;
import org.codelibs.fess.util.RenderDataUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.login.credential.UserPasswordCredential;
import org.lastaflute.web.login.exception.LoginFailureException;
import org.lastaflute.web.response.HtmlResponse;

public class LoginAction extends FessLoginAction {

    // ===================================================================================
    //                                                                       Login Execute
    //                                                                      ==============

    @Execute
    public HtmlResponse index() {
        return asIndexPage(null).useForm(LoginForm.class);
    }

    private HtmlResponse asIndexPage(final LoginForm form) {
        if (form != null) {
            form.clearSecurityInfo();
        }
        return asHtml(virtualHost(path_Login_IndexJsp)).renderWith(data -> {
            RenderDataUtil.register(data, "notification", fessConfig.getNotificationLogin());
            saveToken();
        });
    }

    @Execute
    public HtmlResponse login(final LoginForm form) {
        validate(form, messages -> {}, () -> asIndexPage(form));
        verifyToken(() -> asIndexPage(form));
        final String username = form.username;
        final String password = form.password;
        form.clearSecurityInfo();
        try {
            return fessLoginAssist.loginRedirect(new UserPasswordCredential(username, password), op -> {}, () -> {
                activityHelper.login(getUserBean());
                userInfoHelper.deleteUserCodeFromCookie(request);
                return getHtmlResponse();
            });
        } catch (final LoginFailureException lfe) {
            activityHelper.loginFailure(OptionalThing.of(new UserPasswordCredential(username, password)));
            throwValidationError(messages -> messages.addErrorsLoginError(GLOBAL), () -> asIndexPage(form));
        }
        return redirect(getClass());
    }

}