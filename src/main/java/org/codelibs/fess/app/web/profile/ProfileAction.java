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

/**
 * @author Keiichi Watanabe
 */
package org.codelibs.fess.app.web.profile;

import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.app.web.base.FessSearchAction;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.app.web.login.LoginAction;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.validation.VaErrorHook;

public class ProfileAction extends FessSearchAction {

    // ===================================================================================
    // Constant
    //

    // ===================================================================================
    // Attribute
    //
    @Resource
    protected FessLoginAssist fessLoginAssist;

    // ===================================================================================
    // Hook
    // ======

    // ===================================================================================
    // Search Execute
    // ==============

    @Execute
    public HtmlResponse index() {
        if (fessLoginAssist.getSessionUserBean().isPresent()) {
            return asHtml(path_Profile_IndexJsp).useForm(ProfileForm.class);
        } else {
            return redirect(LoginAction.class);
        }
    }

    @Execute
    public HtmlResponse changePassword(final ProfileForm form) {
        validatePasswordForm(form, () -> index());
        // TODO
        return redirect(getClass());
    }

    private void validatePasswordForm(final ProfileForm form, final VaErrorHook validationErrorLambda) {
        validate(form, messages -> {}, () -> {
            form.clearSecurityInfo();
            return index();
        });
        if (StringUtil.isBlank(form.oldPassword)) {
            form.clearSecurityInfo();
            throwValidationError(messages -> {
                messages.addErrorsBlankPassword("oldPassword");
            }, validationErrorLambda);
        }
        if (StringUtil.isBlank(form.newPassword)) {
            form.newPassword = null;
            form.confirmPassword = null;
            throwValidationError(messages -> {
                messages.addErrorsBlankPassword("newPassword");
            }, validationErrorLambda);
        }
        if (form.newPassword != null && !form.newPassword.equals(form.confirmPassword)) {
            form.newPassword = null;
            form.confirmPassword = null;
            throwValidationError(messages -> {
                messages.addErrorsInvalidConfirmPassword("confirmPassword");
            }, validationErrorLambda);
        }
    }
}
