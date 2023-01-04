/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.web.RootAction;
import org.codelibs.fess.app.web.base.FessLoginAction;
import org.codelibs.fess.app.web.base.login.ActionResponseCredential;
import org.codelibs.fess.app.web.login.LoginAction;
import org.codelibs.fess.exception.SsoMessageException;
import org.codelibs.fess.sso.SsoManager;
import org.codelibs.fess.sso.SsoResponseType;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.login.exception.LoginFailureException;
import org.lastaflute.web.response.ActionResponse;

public class SsoAction extends FessLoginAction {
    // ===================================================================================
    //                                                                            Constant
    //
    private static final Logger logger = LogManager.getLogger(SsoAction.class);

    // ===================================================================================
    //                                                                       Login Execute
    //                                                                      ==============

    @Execute
    public ActionResponse index() {
        if (fessLoginAssist.getSavedUserBean().isPresent()) {
            return redirect(RootAction.class);
        }
        final SsoManager ssoManager = ComponentUtil.getSsoManager();
        final LoginCredential loginCredential = ssoManager.getLoginCredential();
        if (loginCredential == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("No user in SSO request.");
            }
            if (ssoManager.available()) {
                saveError(messages -> messages.addErrorsSsoLoginError(GLOBAL));
            }
            return redirect(LoginAction.class);
        }
        if (loginCredential instanceof ActionResponseCredential) {
            return ((ActionResponseCredential) loginCredential).execute();
        }
        try {
            return fessLoginAssist.loginRedirect(loginCredential, op -> {}, () -> {
                activityHelper.login(getUserBean());
                userInfoHelper.deleteUserCodeFromCookie(request);
                return getHtmlResponse();
            });
        } catch (final LoginFailureException lfe) {
            if (logger.isDebugEnabled()) {
                logger.debug("SSO login failure.", lfe);
            }
            if (ssoManager.available()) {
                saveError(messages -> messages.addErrorsSsoLoginError(GLOBAL));
            }
            activityHelper.loginFailure(OptionalThing.of(loginCredential));
            return redirect(LoginAction.class);
        }
    }

    @Execute
    public ActionResponse metadata() {
        final SsoManager ssoManager = ComponentUtil.getSsoManager();
        try {
            final ActionResponse actionResponse = ssoManager.getResponse(SsoResponseType.METADATA);
            if (actionResponse == null) {
                throw responseManager.new400("Unsupported request type.");
            }
            return actionResponse;
        } catch (final SsoMessageException e) {
            if (e.getCause() == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Metadata response.", e);
                }
                saveInfo(e.getMessageCode());
            } else {
                logger.warn("Failed to process metadata.", e);
                saveError(e.getMessageCode());
            }
            return redirect(LoginAction.class);
        }
    }

    @Execute
    public ActionResponse logout() {
        final SsoManager ssoManager = ComponentUtil.getSsoManager();
        try {
            final ActionResponse actionResponse = ssoManager.getResponse(SsoResponseType.LOGOUT);
            if (actionResponse == null) {
                throw responseManager.new400("Unsupported request type.");
            }
            return actionResponse;
        } catch (final SsoMessageException e) {
            if (e.getCause() == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Logout response.", e);
                }
                saveInfo(e.getMessageCode());
            } else {
                logger.warn("Failed to log out.", e);
                saveError(e.getMessageCode());
            }
            return redirect(LoginAction.class);
        }
    }
}