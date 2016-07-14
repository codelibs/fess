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
package org.codelibs.fess.app.web.sso;

import org.codelibs.fess.app.web.base.FessLoginAction;
import org.codelibs.fess.app.web.base.login.EmptyLoginCredential;
import org.codelibs.fess.app.web.base.login.LoginCredential;
import org.codelibs.fess.app.web.login.LoginAction;
import org.codelibs.fess.sso.SsoAuthenticator;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.login.exception.LoginFailureException;
import org.lastaflute.web.response.ActionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SsoAction extends FessLoginAction {
    // ===================================================================================
    //                                                                            Constant
    //
    private static final Logger logger = LoggerFactory.getLogger(SsoAction.class);

    // ===================================================================================
    //                                                                       Login Execute
    //                                                                      ==============

    @Execute
    public ActionResponse index() {
        final SsoAuthenticator authenticator = ComponentUtil.getSsoAuthenticator();
        LoginCredential loginCredential = authenticator.getLoginCredential();
        if (loginCredential == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("No user in SSO request.");
            }
            saveError(messages -> messages.addErrorsSsoLoginError(GLOBAL));
            return redirect(LoginAction.class);
        } else if (loginCredential instanceof EmptyLoginCredential) {
            return null;
        }
        try {
            return fessLoginAssist.loginRedirect(loginCredential, op -> {}, () -> {
                activityHelper.login(getUserBean());
                return getHtmlResponse();
            });
        } catch (final LoginFailureException lfe) {
            if (logger.isDebugEnabled()) {
                logger.debug("SSO login failure.", lfe);
            }
            saveError(messages -> messages.addErrorsSsoLoginError(GLOBAL));
            return redirect(LoginAction.class);
        }
    }

}