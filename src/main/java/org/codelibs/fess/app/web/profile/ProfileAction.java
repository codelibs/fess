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
/**
 * @author Keiichi Watanabe
 * @author shinsuke
 */
package org.codelibs.fess.app.web.profile;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.service.UserService;
import org.codelibs.fess.app.web.base.FessSearchAction;
import org.codelibs.fess.app.web.base.login.LocalUserCredential;
import org.codelibs.fess.app.web.login.LoginAction;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.validation.VaErrorHook;

public class ProfileAction extends FessSearchAction {

    private static final Logger logger = LogManager.getLogger(ProfileAction.class);

    // ===================================================================================
    // Constant
    //

    // ===================================================================================
    // Attribute
    //
    @Resource
    private UserService userService;

    // ===================================================================================
    // Hook
    // ======

    // ===================================================================================
    // Search Execute
    // ==============

    @Execute
    public HtmlResponse index() {
        return asIndexHtml();
    }

    @Execute
    public HtmlResponse changePassword(final ProfileForm form) {
        final VaErrorHook toIndexPage = () -> {
            form.clearSecurityInfo();
            return asIndexHtml();
        };
        validatePasswordForm(form, toIndexPage);
        final String username = getUserBean().map(FessUserBean::getUserId).get();
        try {
            userService.changePassword(username, form.newPassword);
            saveInfo(messages -> messages.addSuccessChangedPassword(GLOBAL));
        } catch (final Exception e) {
            logger.warn("Failed to change password for {}", username, e);
            throwValidationError(messages -> messages.addErrorsFailedToChangePassword(GLOBAL), toIndexPage);
        }
        return redirect(getClass());
    }

    private void validatePasswordForm(final ProfileForm form, final VaErrorHook validationErrorLambda) {
        validate(form, messages -> {}, validationErrorLambda);

        if (!form.newPassword.equals(form.confirmNewPassword)) {
            form.newPassword = null;
            form.confirmNewPassword = null;
            throwValidationError(messages -> {
                messages.addErrorsInvalidConfirmPassword(GLOBAL);
            }, validationErrorLambda);
        }

        fessLoginAssist.findLoginUser(new LocalUserCredential(getUserBean().get().getUserId(), form.oldPassword)).orElseGet(() -> {
            throwValidationError(messages -> {
                messages.addErrorsNoUserForChangingPassword(GLOBAL);
            }, validationErrorLambda);
            return null;
        });
    }

    protected HtmlResponse asIndexHtml() {
        return getUserBean().map(u -> asHtml(virtualHost(path_Profile_IndexJsp)).useForm(ProfileForm.class))
                .orElseGet(() -> redirect(LoginAction.class));
    }
}
