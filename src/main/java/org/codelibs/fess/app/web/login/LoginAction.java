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
package org.codelibs.fess.app.web.login;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.app.service.UserService;
import org.codelibs.fess.app.web.base.FessLoginAction;
import org.codelibs.fess.app.web.base.login.LocalUserCredential;
import org.codelibs.fess.app.web.profile.ProfileAction;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.login.exception.LoginFailureException;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.validation.VaErrorHook;

public class LoginAction extends FessLoginAction {

    private static final Logger logger = LogManager.getLogger(LoginAction.class);

    private static final String INVALID_OLD_PASSWORD = "LoginAction.invalidOldPassword";

    // ===================================================================================
    // Attribute
    //
    @Resource
    private UserService userService;

    // ===================================================================================
    //                                                                       Login Execute
    //                                                                      ==============

    @Execute
    public HtmlResponse index() {
        getSession().ifPresent(session -> session.removeAttribute(INVALID_OLD_PASSWORD));
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
            final HtmlResponse loginRedirect = fessLoginAssist.loginRedirect(new LocalUserCredential(username, password), op -> {}, () -> {
                activityHelper.login(getUserBean());
                userInfoHelper.deleteUserCodeFromCookie(request);
                return getHtmlResponse();
            });
            if (ComponentUtil.getFessConfig().isValidAdminPassword(password)) {
                return loginRedirect;
            }
            getSession().ifPresent(session -> session.setAttribute(INVALID_OLD_PASSWORD, password));
            return asHtml(virtualHost(path_Login_NewpasswordJsp));
        } catch (final LoginFailureException lfe) {
            if (logger.isDebugEnabled()) {
                logger.debug("Login is failed.", lfe);
            }
            activityHelper.loginFailure(OptionalThing.of(new LocalUserCredential(username, password)));
            throwValidationError(messages -> messages.addErrorsLoginError(GLOBAL), () -> asIndexPage(form));
        }
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse changePassword(final PasswordForm form) {
        final VaErrorHook toIndexPage = () -> {
            form.clearSecurityInfo();
            return getUserBean().map(u -> asHtml(virtualHost(path_Login_NewpasswordJsp)).useForm(PasswordForm.class))
                    .orElseGet(() -> redirect(LoginAction.class));
        };
        validatePasswordForm(form, toIndexPage);
        final String username = getUserBean().map(FessUserBean::getUserId).get();
        try {
            userService.changePassword(username, form.password);
            saveInfo(messages -> messages.addSuccessChangedPassword(GLOBAL));
        } catch (final Exception e) {
            logger.warn("Failed to change newPassword for {}", username, e);
            throwValidationError(messages -> messages.addErrorsFailedToChangePassword(GLOBAL), toIndexPage);
        }
        getSession().ifPresent(session -> session.removeAttribute(INVALID_OLD_PASSWORD));
        return redirect(ProfileAction.class);
    }

    private void validatePasswordForm(final PasswordForm form, final VaErrorHook validationErrorLambda) {
        validate(form, messages -> {}, validationErrorLambda);

        if (!form.password.equals(form.confirmPassword)) {
            throwValidationError(messages -> {
                messages.addErrorsInvalidConfirmPassword(GLOBAL);
            }, validationErrorLambda);
        }

        final String oldPassword =
                getSession().map(session -> (String) session.getAttribute(INVALID_OLD_PASSWORD)).orElse(StringUtil.EMPTY);
        fessLoginAssist.findLoginUser(new LocalUserCredential(getUserBean().get().getUserId(), oldPassword)).orElseGet(() -> {
            throwValidationError(messages -> {
                messages.addErrorsNoUserForChangingPassword(GLOBAL);
            }, validationErrorLambda);
            return null;
        });
    }

    private OptionalThing<HttpSession> getSession() {
        final HttpSession session = request.getSession(false);
        if (session != null) {
            return OptionalEntity.of(session);
        }
        return OptionalEntity.empty();
    }
}