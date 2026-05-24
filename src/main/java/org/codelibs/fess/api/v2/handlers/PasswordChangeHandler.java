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
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
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
 * {@code ProfileAction.changePassword}.</p>
 *
 * <p>C-3: although the caller is already authenticated, a stolen-session attacker
 * could otherwise brute-force {@code current_password} unbounded. The handler
 * therefore consults the {@link LoginRateLimiter} per-user bucket (same
 * configuration as {@code /api/v2/auth/login}): a {@link LoginRateLimiter#peek}
 * gates entry, an {@link LoginRateLimiter#allow} consumes a slot on wrong-password,
 * and {@link LoginRateLimiter#clear} resets the bucket on success — mirroring
 * {@link LoginHandler}. When the user bucket is exhausted, the handler returns
 * {@code 429 rate_limited} with a {@code Retry-After} header.</p>
 *
 * <p>M-3 session contract: After a successful password change the current HTTP session
 * is invalidated ({@link HttpSession#invalidate()}) so any session token that was
 * exfiltrated before the change cannot be reused. The response includes
 * {@code re_login_required: true} so the SPA can redirect to the login page.
 * Note that {@code csrf_token} is NOT returned in the success response because the
 * session that held the old token has already been destroyed; the SPA must obtain a
 * fresh token after re-authenticating. Invalidating other concurrent sessions for the
 * same user is a follow-up item (requires a Fess-wide session registry, not available
 * in this release).</p>
 *
 * <p>MJ-30 i18n contract: {@code error.message} values in this handler are
 * developer-facing English strings. Clients MUST use {@code error.code}
 * (the V2ErrorCode token) for user-facing i18n.</p>
 */
public class PasswordChangeHandler {

    private static final Logger logger = LogManager.getLogger(PasswordChangeHandler.class);

    private static final int MAX_BODY_BYTES = 4 * 1024;

    private final LoginRateLimiter limiter;

    /**
     * Default constructor used by the DI container. The handler resolves the
     * shared {@link LoginRateLimiter} lazily so DI bootstrap order is not
     * constrained. The handler holds no per-request state and is safe to share
     * across concurrent requests.
     */
    public PasswordChangeHandler() {
        this(null);
    }

    /**
     * Test-friendly constructor allowing the caller to inject a specific
     * {@link LoginRateLimiter} instance. Passing {@code null} causes the
     * handler to resolve the DI-managed singleton via
     * {@link ComponentUtil#getLoginRateLimiter()} on first use, with a
     * graceful fallback (no-op limiter) when the component is not registered
     * — e.g. in slim test harnesses.
     *
     * @param limiter the rate limiter to use, or {@code null} to resolve via DI
     */
    PasswordChangeHandler(final LoginRateLimiter limiter) {
        this.limiter = limiter;
    }

    private LoginRateLimiter limiter() {
        if (limiter != null) {
            return limiter;
        }
        try {
            return ComponentUtil.getLoginRateLimiter();
        } catch (final RuntimeException e) {
            // Slim test harness without the limiter registered: degrade to a
            // no-op limiter so the password-change flow itself stays testable.
            return null;
        }
    }

    /**
     * Processes one {@code POST /api/v2/auth/password} request.
     *
     * <p>Requires an authenticated session, then re-verifies the supplied
     * {@code current_password} via {@link FessLoginAssist#findLoginUser} before
     * updating the stored hash. On success the current HTTP session is invalidated
     * (M-3) and the response includes {@code re_login_required: true}. Because
     * the session is destroyed the response does not carry a rotated CSRF token;
     * the SPA must obtain a fresh token after re-authenticating.</p>
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @throws IOException if writing the envelope or reading the body fails
     */
    public void handle(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            res.setHeader("Allow", "POST");
            V2EnvelopeWriter.writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        // M-17: split DI-binding/lookup failure from "no user bean present". A genuine
        // anonymous caller still produces AUTH_REQUIRED (401); a system-level lookup
        // failure produces INTERNAL_ERROR (500) so the user does not see a misleading
        // "please log in again" when the real issue is server-side.
        final FessLoginAssist assist;
        try {
            assist = ComponentUtil.getComponent(FessLoginAssist.class);
        } catch (final RuntimeException e) {
            logger.warn("/api/v2/auth/password: could not acquire FessLoginAssist", e);
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INTERNAL_ERROR, "internal error");
            return;
        }
        final OptionalThing<FessUserBean> userBean;
        try {
            userBean = assist.getSavedUserBean();
        } catch (final RuntimeException e) {
            logger.warn("/api/v2/auth/password: getSavedUserBean failed", e);
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INTERNAL_ERROR, "internal error");
            return;
        }
        if (!userBean.isPresent()) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.AUTH_REQUIRED, "login required");
            return;
        }
        final Map<String, Object> body;
        try {
            body = V2JsonBody.read(req, MAX_BODY_BYTES);
        } catch (final V2JsonBody.PayloadTooLargeException e) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.PAYLOAD_TOO_LARGE, e.getMessage());
            return;
        } catch (final V2JsonBody.UnsupportedMediaTypeException e) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.UNSUPPORTED_MEDIA_TYPE, e.getMessage());
            return;
        } catch (final V2JsonBody.MalformedJsonException e) {
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

        // C-3: rate-limit the current_password verification on a per-user bucket. We use the
        // same FessConfig thresholds as /api/v2/auth/login so an operator can tune one set
        // of knobs. peek() short-circuits when the bucket is already exhausted; allow() is
        // called only on the failure path so a system error does not count against the
        // legitimate user's quota.
        final LoginRateLimiter rate = limiter();
        int userLimit = 0;
        int lockoutSec = 0;
        if (rate != null) {
            try {
                final FessConfig fessConfig = ComponentUtil.getFessConfig();
                userLimit = fessConfig.getThemeApiLoginRateLimitPerUserPerMinuteAsInteger();
                lockoutSec = fessConfig.getThemeApiLoginLockoutSecondsAsInteger();
            } catch (final RuntimeException e) {
                // FessConfig unavailable in a slim test harness: keep sentinels (0,0) which
                // makes the limiter calls below into no-ops rather than blocking the flow.
                logger.warn("/api/v2/auth/password: could not read rate-limit config; gate disabled", e);
            }
        }
        if (rate != null && userLimit > 0 && !rate.peek(LoginRateLimiter.Scope.USER, userId, userLimit, 60)) {
            rate.lockOut(LoginRateLimiter.Scope.USER, userId, lockoutSec);
            res.setHeader("Retry-After", Integer.toString(Math.max(lockoutSec, 1)));
            V2EnvelopeWriter.writeError(res, V2ErrorCode.RATE_LIMITED, "too many password-change attempts");
            return;
        }

        // Re-authenticate the current user with the supplied current_password. We mirror
        // ProfileAction.validatePasswordForm — findLoginUser performs the same hash compare
        // that the login flow uses, so we get an early reject for wrong-password without
        // leaking timing differences vs. a hand-rolled compare.
        final OptionalEntity<? extends FessUser> verified;
        try {
            verified = assist.findLoginUser(new LocalUserCredential(userId, currentPw));
        } catch (final RuntimeException e) {
            logger.warn("/api/v2/auth/password: findLoginUser failed", e);
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INTERNAL_ERROR, "failed to change password");
            return;
        }
        if (verified == null || !verified.isPresent()) {
            // Wrong current_password: consume one user-bucket slot. If the bucket has just
            // been exhausted, escalate to 429 with Retry-After (same pattern as LoginHandler).
            if (rate != null && userLimit > 0 && !rate.allow(LoginRateLimiter.Scope.USER, userId, userLimit, 60)) {
                rate.lockOut(LoginRateLimiter.Scope.USER, userId, lockoutSec);
                res.setHeader("Retry-After", Integer.toString(Math.max(lockoutSec, 1)));
                V2EnvelopeWriter.writeError(res, V2ErrorCode.RATE_LIMITED, "too many password-change attempts");
                return;
            }
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
        // C-3: successful change resets the per-user bucket so any earlier failed attempts
        // in the same window are forgiven — mirrors LoginHandler's MJ-5 reset.
        if (rate != null) {
            rate.clear(LoginRateLimiter.Scope.USER, userId);
        }
        // M-3: invalidate the current session so any exfiltrated session token is
        // immediately revoked. This must happen BEFORE writing the response because the
        // servlet container may flush cookie-clearing headers together with the response.
        // We do NOT rotate-and-return a CSRF token here: the session is destroyed, so any
        // token would belong to a non-existent session and be useless to the SPA.
        //
        // re_login_required:true signals the SPA that the password has changed and it
        // must redirect to the login page to obtain a fresh session and CSRF token.
        // Other concurrent sessions for the same user are NOT invalidated — this is a
        // known limitation (requires a Fess-wide session registry, follow-up item).
        //
        // MINOR-4: call assist.logout() first so that remember-me cookies are cleared and
        // any audit events are recorded — matching the LogoutHandler pattern. Failures are
        // swallowed so that session.invalidate() still runs regardless.
        try {
            assist.logout();
        } catch (final Exception e) {
            // logout is best-effort: unavailable session manager or already-logged-out state
            // must not prevent the session invalidation that follows.
            logger.debug("/api/v2/auth/password: assist.logout() failed for userId={}; continuing session invalidation", userId);
        }
        boolean sessionInvalidated = false;
        final HttpSession session = req.getSession(false);
        if (session != null) {
            try {
                session.invalidate();
                sessionInvalidated = true;
            } catch (final IllegalStateException e) {
                // Already invalidated by another thread, the container, or assist.logout() — safe to ignore.
                logger.debug("/api/v2/auth/password: session was already invalidated for userId={}", userId);
                sessionInvalidated = true;
            }
        }
        logger.info("/api/v2/auth/password: password changed; userId={}, sessionInvalidated={}", userId, sessionInvalidated);
        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("ok", true);
        payload.put("re_login_required", true);
        V2EnvelopeWriter.writeSuccess(res, payload);
    }

    /**
     * M-10: coerce a JSON value to a String only when it actually is a String. A
     * numeric {@code current_password} (e.g. {@code 12345}) would have been silently
     * stringified by {@code v.toString()} and could mask bugs in callers; treat any
     * non-string value as missing instead.
     */
    private static String stringOrNull(final Object v) {
        return v instanceof final String s ? s : null;
    }
}
