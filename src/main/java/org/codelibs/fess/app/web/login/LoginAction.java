/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
import org.lastaflute.web.Execute;
import org.lastaflute.web.login.exception.LoginFailureException;
import org.lastaflute.web.response.HtmlResponse;

public class LoginAction extends FessLoginAction {

    // ===================================================================================
    //                                                                       Login Execute
    //                                                                      ==============

    @Execute
    public HtmlResponse index() {
        return asHtml(path_Login_IndexJsp).renderWith(data -> {
            RenderDataUtil.register(data, "notification", fessConfig.getNotificationLogin());
        }).useForm(LoginForm.class);
    }

    @Execute
    public HtmlResponse login(final LoginForm form) {
        validate(form, messages -> {}, () -> {
            form.clearSecurityInfo();
            return asHtml(path_Login_IndexJsp);
        });
        final String username = form.username;
        final String password = form.password;
        form.clearSecurityInfo();
        try {
            return fessLoginAssist.loginRedirect(username, password, op -> {}, () -> {
                activityHelper.login(getUserBean());
                return getHtmlResponse();
            });
        } catch (final LoginFailureException lfe) {
            throwValidationError(messages -> messages.addErrorsLoginError(GLOBAL), () -> {
                return asHtml(path_Login_IndexJsp);
            });
        }
        return redirect(getClass());
    }

}