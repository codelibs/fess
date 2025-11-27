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
package org.codelibs.fess.app.web.login;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.app.service.UserService;
import org.codelibs.fess.app.web.base.FessLoginAction;
import org.codelibs.fess.app.web.base.login.LocalUserCredential;
import org.codelibs.fess.app.web.profile.ProfileAction;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.login.exception.LoginFailureException;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.validation.VaErrorHook;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;

/**
 * The login action.
 */
public class LoginAction extends FessLoginAction {

    /**
     * Default constructor.
     */
    public LoginAction() {
        super();
    }

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

    /**
     * Displays the login page.
     *
     * @return the HTML response for the login page
     */
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

    /**
     * Handles user login with the provided credentials.
     *
     * @param form the login form containing username and password
     * @return the HTML response after login attempt
     */
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
            if (logger.isInfoEnabled()) {
                logger.info("Login failed for user: username={}, reason={}", username, lfe.getMessage());
            }
            activityHelper.loginFailure(OptionalThing.of(new LocalUserCredential(username, password)));
            throwValidationError(messages -> messages.addErrorsLoginError(GLOBAL), () -> asIndexPage(form));
        }
        return redirect(getClass());
    }

    /**
     * Handles password change for the current user.
     *
     * @param form the password form containing new password and confirmation
     * @return the HTML response after password change attempt
     */
    @Execute
    public HtmlResponse changePassword(final PasswordForm form) {
        final VaErrorHook toIndexPage = () -> {
            form.clearSecurityInfo();
            return getUserBean().map(u -> asHtml(virtualHost(path_Login_NewpasswordJsp)).useForm(PasswordForm.class))
                    .orElseGet(() -> redirect(LoginAction.class));
        };
        validatePasswordForm(form, toIndexPage);
        if (!getUserBean().isPresent()) {
            logger.warn("User session not found during password change - potential session timeout or security issue");
            return redirect(LoginAction.class);
        }
        final String username = getUserBean().get().getUserId();
        try {
            userService.changePassword(username, form.password);
            saveInfo(messages -> messages.addSuccessChangedPassword(GLOBAL));
        } catch (final Exception e) {
            logger.warn("Failed to change password for user: username={}, error={}", username, e.getMessage(), e);
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
        getUserBean().ifPresent(user -> {
            final String userId = user.getUserId();
            fessLoginAssist.findLoginUser(new LocalUserCredential(userId, oldPassword)).orElseGet(() -> {
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

    private OptionalThing<HttpSession> getSession() {
        final HttpSession session = request.getSession(false);
        if (session != null) {
            return OptionalEntity.of(session);
        }
        return OptionalEntity.empty();
    }
}