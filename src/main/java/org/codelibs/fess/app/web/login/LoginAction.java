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
import org.codelibs.fess.api.v2.handlers.LoginRateLimiter;
import org.codelibs.fess.app.service.UserService;
import org.codelibs.fess.app.web.base.FessLoginAction;
import org.codelibs.fess.app.web.base.login.LocalUserCredential;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.LogUtil;
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
    }

    private static final Logger logger = LogManager.getLogger(LoginAction.class);

    private static final String INVALID_OLD_PASSWORD = "LoginAction.invalidOldPassword";

    /** Joins the address and the user name in a USER-scope key; the same separator as {@code LoginHandler}. */
    private static final String USER_SCOPE_KEY_SEP = "\u0000";

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
        return asHtml(path_AdminLogin_IndexJsp).renderWith(data -> {
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
        // The same per-address and per-user allowances as /api/v2/auth/login, on the same buckets: this form
        // checks the same credentials, so it must not be a way around the limit.
        final LoginRateLimiter limiter = ComponentUtil.getLoginRateLimiter();
        final String clientIp = ComponentUtil.getRateLimitHelper().getClientIp(request);
        if (isThrottled(limiter, clientIp, username)) {
            throwValidationError(messages -> messages.addErrorsLoginError(GLOBAL), () -> asIndexPage(form));
        }
        try {
            final HtmlResponse loginRedirect = fessLoginAssist.loginRedirect(new LocalUserCredential(username, password), op -> {}, () -> {
                activityHelper.login(getUserBean());
                userInfoHelper.deleteUserCodeFromCookie(request);
                return getHtmlResponse();
            });
            // Give back only what this attempt took: the user's own failures, and the one address slot it consumed.
            forgiveAttempt(limiter, clientIp, username);
            if (ComponentUtil.getFessConfig().isValidAdminPassword(password)) {
                return loginRedirect;
            }
            getSession().ifPresent(session -> session.setAttribute(INVALID_OLD_PASSWORD, password));
            return asHtml(path_AdminLogin_NewpasswordJsp);
        } catch (final LoginFailureException lfe) {
            if (logger.isInfoEnabled()) {
                logger.info("Login failed for user: username={}, reason={}", LogUtil.sanitize(username),
                        LogUtil.sanitize(lfe.getMessage()));
            }
            activityHelper.loginFailure(OptionalThing.of(new LocalUserCredential(username, password)));
            recordFailure(limiter, clientIp, username);
            throwValidationError(messages -> messages.addErrorsLoginError(GLOBAL), () -> asIndexPage(form));
        }
        return redirect(getClass());
    }

    /**
     * Takes the address slot of this attempt and checks the per-user allowance. The user slot is taken
     * only when the credentials are rejected (see {@link #recordFailure}).
     *
     * @param limiter the limiter shared with the v2 login
     * @param clientIp the client address
     * @param username the submitted user name
     * @return true if the attempt is refused
     */
    protected boolean isThrottled(final LoginRateLimiter limiter, final String clientIp, final String username) {
        final int ipLimit = fessConfig.getThemeApiLoginRateLimitPerIpPerMinuteAsInteger();
        final int userLimit = fessConfig.getThemeApiLoginRateLimitPerUserPerMinuteAsInteger();
        final int lockoutSec = fessConfig.getThemeApiLoginLockoutSecondsAsInteger();
        if (!limiter.allow(LoginRateLimiter.Scope.IP, clientIp, ipLimit, 60)) {
            if (limiter.lockOut(LoginRateLimiter.Scope.IP, clientIp, lockoutSec)) {
                logger.warn("[login] ip rate limit exceeded; locking out for {}s: clientIp={}, limit={}/min", lockoutSec, clientIp,
                        ipLimit);
            }
            return true;
        }
        if (!limiter.peek(LoginRateLimiter.Scope.USER, userKey(clientIp, username), userLimit, 60)) {
            if (limiter.lockOut(LoginRateLimiter.Scope.USER, userKey(clientIp, username), lockoutSec)) {
                logger.warn("[login] user rate limit exceeded; locking out for {}s: username={}, clientIp={}, limit={}/min", lockoutSec,
                        LogUtil.sanitize(username), clientIp, userLimit);
            }
            return true;
        }
        return false;
    }

    /**
     * Counts a rejected credential against the user.
     *
     * @param limiter the limiter shared with the v2 login
     * @param clientIp the client address
     * @param username the submitted user name
     */
    protected void recordFailure(final LoginRateLimiter limiter, final String clientIp, final String username) {
        final String key = userKey(clientIp, username);
        if (!limiter.allow(LoginRateLimiter.Scope.USER, key, fessConfig.getThemeApiLoginRateLimitPerUserPerMinuteAsInteger(), 60)) {
            limiter.lockOut(LoginRateLimiter.Scope.USER, key, fessConfig.getThemeApiLoginLockoutSecondsAsInteger());
        }
    }

    /**
     * Gives back what a successful login took: the user's own failures and the one address slot the attempt
     * consumed. The address bucket is not cleared, because it also counts the guesses made at other users.
     *
     * @param limiter the limiter shared with the v2 login
     * @param clientIp the client address
     * @param username the submitted user name
     */
    protected void forgiveAttempt(final LoginRateLimiter limiter, final String clientIp, final String username) {
        limiter.clear(LoginRateLimiter.Scope.USER, userKey(clientIp, username));
        limiter.release(LoginRateLimiter.Scope.IP, clientIp);
    }

    private static String userKey(final String clientIp, final String username) {
        return clientIp + USER_SCOPE_KEY_SEP + username;
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
            return getUserBean().map(u -> asHtml(path_AdminLogin_NewpasswordJsp).useForm(PasswordForm.class))
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
        // The JSP profile page this used to land on is gone: go where a login lands instead.
        return redirectByUser(getUserBean().get());
    }

    private void validatePasswordForm(final PasswordForm form, final VaErrorHook validationErrorLambda) {
        validate(form, messages -> {}, validationErrorLambda);

        if (!form.password.equals(form.confirmPassword)) {
            throwValidationError(messages -> {
                messages.addErrorsInvalidConfirmPassword(GLOBAL);
            }, validationErrorLambda);
        }

        final String validationError = ComponentUtil.getSystemHelper().validatePassword(form.password);
        if (StringUtil.isNotBlank(validationError)) {
            throwValidationError(messages -> {
                addPasswordValidationError(messages, validationError);
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

    /**
     * Adds a password validation error message to the messages object based on the error key.
     *
     * @param messages the FessMessages object to add the error to
     * @param errorKey the error key identifying the type of password validation error
     */
    protected void addPasswordValidationError(final FessMessages messages, final String errorKey) {
        switch (errorKey) {
        case "errors.password_length" -> messages.addErrorsPasswordLength(GLOBAL,
                String.valueOf(ComponentUtil.getFessConfig().getPasswordMinLengthAsInteger()));
        case "errors.password_no_uppercase" -> messages.addErrorsPasswordNoUppercase(GLOBAL);
        case "errors.password_no_lowercase" -> messages.addErrorsPasswordNoLowercase(GLOBAL);
        case "errors.password_no_digit" -> messages.addErrorsPasswordNoDigit(GLOBAL);
        case "errors.password_no_special_char" -> messages.addErrorsPasswordNoSpecialChar(GLOBAL);
        case "errors.password_is_blacklisted" -> messages.addErrorsPasswordIsBlacklisted(GLOBAL);
        default -> messages.addErrorsBlankPassword(GLOBAL);
        }
    }

    private OptionalThing<HttpSession> getSession() {
        final HttpSession session = request.getSession(false);
        if (session != null) {
            return OptionalEntity.of(session);
        }
        return OptionalEntity.empty();
    }
}