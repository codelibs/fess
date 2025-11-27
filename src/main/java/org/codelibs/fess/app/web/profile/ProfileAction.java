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
/**
 */
package org.codelibs.fess.app.web.profile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.service.UserService;
import org.codelibs.fess.app.web.base.FessSearchAction;
import org.codelibs.fess.app.web.base.login.LocalUserCredential;
import org.codelibs.fess.app.web.login.LoginAction;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.validation.VaErrorHook;

import jakarta.annotation.Resource;

/**
 * Action for user profile operations.
 */
public class ProfileAction extends FessSearchAction {

    /**
     * Default constructor.
     */
    public ProfileAction() {
        super();
    }

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

    /**
     * Displays the profile index page.
     *
     * @return the HTML response
     */
    @Execute
    public HtmlResponse index() {
        return asIndexHtml();
    }

    /**
     * Changes the user password.
     *
     * @param form the profile form
     * @return the HTML response
     */
    @Execute
    public HtmlResponse changePassword(final ProfileForm form) {
        final VaErrorHook toIndexPage = () -> {
            form.clearSecurityInfo();
            return asIndexHtml();
        };
        validatePasswordForm(form, toIndexPage);
        if (!getUserBean().isPresent()) {
            logger.warn("User session not found during password change");
            return redirect(LoginAction.class);
        }
        final String username = getUserBean().get().getUserId();
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

        getUserBean().ifPresent(user -> {
            final String userId = user.getUserId();
            fessLoginAssist.findLoginUser(new LocalUserCredential(userId, form.oldPassword)).orElseGet(() -> {
                throwValidationError(messages -> {
                    messages.addErrorsNoUserForChangingPassword(GLOBAL);
                }, validationErrorLambda);
                return null;
            });
        }).orElse(() -> {
            throwValidationError(messages -> {
                messages.addErrorsLoginError(GLOBAL);
            }, validationErrorLambda);
        });
    }

    /**
     * Returns the index HTML response.
     *
     * @return the HTML response
     */
    protected HtmlResponse asIndexHtml() {
        return getUserBean().map(u -> asHtml(virtualHost(path_Profile_IndexJsp)).useForm(ProfileForm.class))
                .orElseGet(() -> redirect(LoginAction.class));
    }
}
