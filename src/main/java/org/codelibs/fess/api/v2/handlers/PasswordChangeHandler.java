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
package org.codelibs.fess.api.v2.handlers;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.api.v2.V2EnvelopeWriter;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.app.service.UserService;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.app.web.base.login.LocalUserCredential;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.helper.SessionCsrfTokenManager;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Handles {@code POST /api/v2/auth/password}.
 *
 * <p>Changes the current user's password. The handler requires an active
 * session ({@code 401 auth_required} otherwise) and the caller's
 * {@code current_password} — when blank the response is
 * {@code 400 invalid_request}, when wrong the response is
 * {@code 401 auth_required}. The new password is validated against the
 * system's password policy ({@code SystemHelper#validatePassword}), and the
 * actual store is delegated to {@link UserService#changePassword}.
 * Confirm-password mismatch and a blank new password both yield
 * {@code 400 invalid_request}.</p>
 *
 * <p>Request body shape:</p>
 * <pre>{@code
 * { "current_password": "...", "new_password": "...", "confirm_password": "..." }
 * }</pre>
 *
 * <p>All three fields are required. The {@code current_password} check follows the
 * same {@link FessLoginAssist#findLoginUser} pattern used by
 * {@code ProfileAction.changePassword}; it intentionally does <em>not</em>
 * consume a rate-limit slot because the user is already authenticated.</p>
 *
 * <p>MJ-7 session contract: This handler does NOT force-invalidate the current
 * session after a password change (SPA UX choice — avoid interrupting the
 * user mid-workflow). Instead, the response includes {@code re_login_required: true}
 * so the SPA can redirect to the login page at its own discretion. The CSRF token
 * is rotated in the current session as a minimum security measure. Invalidating
 * other concurrent sessions for the same user is a follow-up item (requires a
 * Fess-wide session registry, not available in this release).</p>
 *
 * <p>MJ-30 i18n contract: {@code error.message} values in this handler are
 * developer-facing English strings. Clients MUST use {@code error.code}
 * (the V2ErrorCode token) for user-facing i18n.</p>
 */
public class PasswordChangeHandler {

    private static final Logger logger = LogManager.getLogger(PasswordChangeHandler.class);

    private static final int MAX_BODY_BYTES = 4 * 1024;

    public void handle(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        OptionalThing<FessUserBean> userBean;
        try {
            userBean = ComponentUtil.getComponent(FessLoginAssist.class).getSavedUserBean();
        } catch (final Exception e) {
            // Login subsystem not fully wired (e.g. unit test harness without DBFlute
            // behaviors). Treat as anonymous — production callers always have it resolvable.
            logger.warn("/api/v2/auth/password: login subsystem lookup failed; treating as anonymous", e);
            userBean = OptionalThing.empty();
        }
        if (!userBean.isPresent()) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.AUTH_REQUIRED, "login required");
            return;
        }
        final Map<String, Object> body;
        try {
            body = V2JsonBody.read(req, MAX_BODY_BYTES);
        } catch (final V2JsonBody.PayloadTooLargeException | V2JsonBody.MalformedJsonException
                | V2JsonBody.UnsupportedMediaTypeException e) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, e.getMessage());
            return;
        }
        final String currentPw = stringOrNull(body.get("current_password"));
        final String newPw = stringOrNull(body.get("new_password"));
        final String confirm = stringOrNull(body.get("confirm_password"));
        if (StringUtil.isBlank(currentPw)) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, "current_password is required");
            return;
        }
        if (newPw == null || newPw.isBlank()) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, "new_password is required");
            return;
        }
        if (!newPw.equals(confirm)) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, "passwords do not match");
            return;
        }
        final String userId = userBean.get().getUserId();
        // Re-authenticate the current user with the supplied current_password. We mirror
        // ProfileAction.validatePasswordForm — findLoginUser performs the same hash compare
        // that the login flow uses, so we get an early reject for wrong-password without
        // leaking timing differences vs. a hand-rolled compare.
        final OptionalEntity<? extends FessUser> verified;
        try {
            verified = ComponentUtil.getComponent(FessLoginAssist.class).findLoginUser(new LocalUserCredential(userId, currentPw));
        } catch (final Exception e) {
            logger.warn("/api/v2/auth/password: findLoginUser failed", e);
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INTERNAL_ERROR, "failed to change password");
            return;
        }
        if (verified == null || !verified.isPresent()) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.AUTH_REQUIRED, "invalid current password");
            return;
        }
        final String validationError = ComponentUtil.getSystemHelper().validatePassword(newPw);
        if (StringUtil.isNotBlank(validationError)) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, "weak password: " + validationError);
            return;
        }
        try {
            ComponentUtil.getComponent(UserService.class).changePassword(userId, newPw);
        } catch (final Exception e) {
            logger.warn("/api/v2/auth/password: changePassword failed", e);
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INTERNAL_ERROR, "failed to change password");
            return;
        }
        // M-03: rotate the CSRF token so any previously exfiltrated token is immediately
        // invalidated, then issue a fresh one for the SPA to use on subsequent requests.
        //
        // MJ-7: re_login_required:true signals the SPA that the password has changed and it
        // should redirect to the login page. We intentionally do NOT invalidate the current
        // session here to preserve SPA UX (the user can decide when to re-login). Other
        // concurrent sessions for this user are NOT invalidated — this is a known limitation
        // documented in the class-level JavaDoc (requires a session registry, follow-up item).
        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("ok", true);
        payload.put("re_login_required", true);
        final HttpSession session = req.getSession(false);
        if (session != null) {
            try {
                final SessionCsrfTokenManager csrf = ComponentUtil.getComponent(SessionCsrfTokenManager.class);
                csrf.rotate(session);
                payload.put("csrf_token", csrf.issue(session));
            } catch (final Exception e) {
                // CSRF manager not wired (e.g. slim test harness) — omit the token field
                // rather than failing a successful password change.
                logger.warn("/api/v2/auth/password: CSRF rotation failed; omitting csrf_token from response", e);
            }
        }
        V2EnvelopeWriter.writeSuccess(res, payload);
    }

    private static String stringOrNull(final Object v) {
        return v == null ? null : v.toString();
    }
}
