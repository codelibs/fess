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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.api.v2.V2EnvelopeWriter;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.app.web.base.login.LocalUserCredential;
import org.codelibs.fess.helper.SessionCsrfTokenManager;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.login.exception.LoginFailureException;
import org.dbflute.optional.OptionalThing;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Handles {@code POST /api/v2/auth/login}.
 *
 * <p>Authentication entry point for the static theme SPA. Performs a tiered set
 * of cheap-first gates: IP rate-limit, body parsing, presence checks,
 * per-user rate-limit, then finally credential verification via
 * {@link FessLoginAssist}. On success the response carries the freshly issued
 * CSRF token and an echo of {@code return_to} (only when it is a relative
 * path — protocol-relative URLs are rejected to prevent open-redirect).</p>
 *
 * <p>Constructor-injected {@link LoginRateLimiter} keeps the handler easy to
 * test: production wires the DI-managed singleton via
 * {@link ComponentUtil#getLoginRateLimiter()}, tests pass a fresh instance.</p>
 *
 * <p>MJ-30 i18n contract: {@code error.message} values in this handler are
 * developer-facing English strings. Clients MUST use {@code error.code}
 * (the V2ErrorCode token) for user-facing i18n. This is intentional — the
 * v2 wire spec explicitly separates machine-readable codes from human messages.</p>
 */
public class LoginHandler {

    private static final Logger logger = LogManager.getLogger(LoginHandler.class);

    private static final int MAX_BODY_BYTES = 4 * 1024;

    /** One-shot warning flag so we log only the first IP-resolve failure per JVM lifetime. */
    private static final AtomicBoolean ipResolveWarned = new AtomicBoolean(false);

    /**
     * MJ-8: Allowlist pattern for return_to values.
     * Accepts only relative paths (starts with '/') containing URL-safe characters.
     * Rejects protocol-relative URLs (//), backslashes, NUL bytes, and any
     * ASCII control character (0x00–0x1F, 0x7F).
     */
    private static final Pattern SAFE_RETURN_TO = Pattern.compile("^/[A-Za-z0-9_\\-/.?&=%:@+~#*!,;]*$");

    private final LoginRateLimiter limiter;

    public LoginHandler(final LoginRateLimiter limiter) {
        this.limiter = limiter;
    }

    public void handle(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int ipLimit = fessConfig.getThemeApiLoginRateLimitPerIpPerMinuteAsInteger();
        final int userLimit = fessConfig.getThemeApiLoginRateLimitPerUserPerMinuteAsInteger();
        final int lockoutSec = fessConfig.getThemeApiLoginLockoutSecondsAsInteger();
        final String clientIp = resolveClientIp(req);

        if (!limiter.allow(LoginRateLimiter.Scope.IP, clientIp, ipLimit, 60)) {
            limiter.lockOut(LoginRateLimiter.Scope.IP, clientIp, lockoutSec);
            res.setHeader("Retry-After", Integer.toString(lockoutSec));
            V2EnvelopeWriter.writeError(res, V2ErrorCode.RATE_LIMITED, "too many login attempts (ip)");
            return;
        }

        final Map<String, Object> body;
        try {
            body = V2JsonBody.read(req, MAX_BODY_BYTES);
        } catch (final V2JsonBody.PayloadTooLargeException e) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, "payload too large");
            return;
        } catch (final V2JsonBody.MalformedJsonException | V2JsonBody.UnsupportedMediaTypeException e) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, e.getMessage());
            return;
        }
        final String username = stringOrNull(body.get("username"));
        final String password = stringOrNull(body.get("password"));
        final String returnTo = stringOrNull(body.get("return_to"));
        if (StringUtil.isBlank(username) || StringUtil.isBlank(password)) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, "username and password are required");
            return;
        }

        // USER-scope pre-validation uses peek() so the bucket is NOT consumed yet. The slot is
        // taken only on credential failure below, so that a system-error path (e.g. login
        // subsystem down) does not count against the legitimate user's bucket. IP-scope
        // check above stays pre-validation because it is the cheap bot/scanner gate.
        if (!limiter.peek(LoginRateLimiter.Scope.USER, username, userLimit, 60)) {
            limiter.lockOut(LoginRateLimiter.Scope.USER, username, lockoutSec);
            res.setHeader("Retry-After", Integer.toString(lockoutSec));
            V2EnvelopeWriter.writeError(res, V2ErrorCode.RATE_LIMITED, "too many login attempts (user)");
            return;
        }

        // Acquire the login assist component INSIDE the try so an early DI binding failure
        // (which only happens in slim test harnesses, but defense in depth) is treated as a
        // system error rather than a credential failure — the user bucket must not be
        // consumed for DI breakage.
        final FessLoginAssist assist;
        try {
            assist = ComponentUtil.getComponent(FessLoginAssist.class);
        } catch (final RuntimeException e) {
            logger.warn("login failed unexpectedly: could not acquire FessLoginAssist", e);
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INTERNAL_ERROR, "internal error");
            return;
        }

        // MJ-27: Already-authenticated path — check before calling assist.login().
        // If the session already has a valid user bean, avoid unnecessary re-auth:
        //  - same username: return early with existing bean + current CSRF token (no re-auth).
        //  - different username: this is an explicit account-switch; log at INFO for audit
        //    visibility, then proceed with full re-auth (no forced logout here — SPA UX).
        final OptionalThing<FessUserBean> existingBean;
        try {
            existingBean = assist.getSavedUserBean();
        } catch (final RuntimeException e) {
            logger.warn("login: could not check existing session state", e);
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INTERNAL_ERROR, "internal error");
            return;
        }
        if (existingBean.isPresent()) {
            final FessUserBean existing = existingBean.get();
            if (username.equals(existing.getUserId())) {
                // Same user is already authenticated — return success without re-auth so
                // the SPA can refresh its token without triggering a redundant credential check.
                final HttpSession existingSession = req.getSession(false);
                final SessionCsrfTokenManager csrf = ComponentUtil.getComponent(SessionCsrfTokenManager.class);
                final String existingToken;
                if (existingSession != null) {
                    existingToken = csrf.issue(existingSession);
                } else {
                    existingToken = csrf.issue(req.getSession(true));
                }
                final Map<String, Object> payload = buildUserPayload(existing, existingToken, returnTo);
                V2EnvelopeWriter.writeSuccess(res, payload);
                return;
            }
            // Different username: operator-visible audit log for account-switch path.
            logger.info("[v2/login] account switch: prevUserId={}, newUserId={}", existing.getUserId(), username);
        }

        try {
            assist.login(new LocalUserCredential(username, password), op -> {});
        } catch (final LoginFailureException e) {
            // Credential rejection consumes the USER slot exactly once, on the failure path.
            // The post-consume check decides whether to also lock the bucket — when the user
            // has just exhausted the window, we stamp a lockUntil so subsequent requests are
            // refused even after the sliding window expires (parity with the existing IP
            // gate's lockOut behavior).
            if (!limiter.allow(LoginRateLimiter.Scope.USER, username, userLimit, 60)) {
                limiter.lockOut(LoginRateLimiter.Scope.USER, username, lockoutSec);
                res.setHeader("Retry-After", Integer.toString(lockoutSec));
                V2EnvelopeWriter.writeError(res, V2ErrorCode.RATE_LIMITED, "too many login attempts (user)");
                return;
            }
            V2EnvelopeWriter.writeError(res, V2ErrorCode.AUTH_REQUIRED, "invalid credentials");
            return;
        } catch (final RuntimeException e) {
            // Anything else (DI binding failure, transient lookup error, etc.) is a system
            // error and must NOT count against the user bucket — otherwise a misconfigured
            // server would lock real users out. We surface INTERNAL_ERROR with a generic
            // message; the exception detail goes to the log only.
            logger.warn("login failed unexpectedly", e);
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INTERNAL_ERROR, "internal error");
            return;
        }

        // Successful login — rotate the session id to defeat session fixation, then issue
        // a fresh CSRF token. changeSessionId() throws IllegalStateException if no session
        // exists yet; for the v2 API path that can happen when the SPA POSTs without first
        // making a GET — the catch swallows it because the subsequent getSession(true) will
        // create a fresh session anyway.
        try {
            req.changeSessionId();
        } catch (final IllegalStateException ignore) {
            // no session was associated with the request yet — getSession(true) below
            // will create a new one, which is itself unfixable from the outside.
        }
        final HttpSession session = req.getSession(true);
        final SessionCsrfTokenManager csrf = ComponentUtil.getComponent(SessionCsrfTokenManager.class);
        csrf.rotate(session);
        final String token = csrf.issue(session);

        // MJ-5: clear both USER and IP rate-limit buckets on successful login so that a
        // legitimate user who exhausted the window (e.g. typo run) is not penalized on
        // their next attempt after providing the correct credentials.
        limiter.clear(LoginRateLimiter.Scope.USER, username);
        limiter.clear(LoginRateLimiter.Scope.IP, clientIp);

        final OptionalThing<FessUserBean> userBean = assist.getSavedUserBean();
        final Map<String, Object> payload;
        if (userBean.isPresent()) {
            payload = buildUserPayload(userBean.get(), token, returnTo);
        } else {
            payload = new LinkedHashMap<>();
            payload.put("csrf_token", token);
            addReturnTo(payload, returnTo);
        }
        V2EnvelopeWriter.writeSuccess(res, payload);
    }

    /**
     * Builds the standard success payload for a logged-in user.
     * Uses {@link UserPayloads#toJson(FessUserBean)} to guarantee shape parity
     * with MeHandler (MJ-28).
     */
    private static Map<String, Object> buildUserPayload(final FessUserBean u, final String token, final String returnTo) {
        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("user", UserPayloads.toJson(u));
        payload.put("csrf_token", token);
        addReturnTo(payload, returnTo);
        return payload;
    }

    /**
     * MJ-8: Validates and adds return_to to the payload only when it passes the
     * allowlist pattern. Rejects protocol-relative URLs, backslashes, NUL, and
     * any ASCII control character. The pattern requires a leading '/' so relative
     * paths below the app root are always safe.
     */
    private static void addReturnTo(final Map<String, Object> payload, final String returnTo) {
        if (returnTo == null) {
            return;
        }
        // Reject if it contains any ASCII control character (includes \r, \n, \t, NUL).
        for (int i = 0; i < returnTo.length(); i++) {
            final char c = returnTo.charAt(i);
            if (c < 0x20 || c == 0x7F) {
                return;
            }
        }
        // Reject protocol-relative (//) and non-relative paths; apply character allowlist.
        if (!returnTo.startsWith("/") || returnTo.startsWith("//") || !SAFE_RETURN_TO.matcher(returnTo).matches()) {
            return;
        }
        payload.put("return_to", returnTo);
    }

    private static String stringOrNull(final Object v) {
        return v == null ? null : v.toString();
    }

    /**
     * Resolves the client IP via {@code RateLimitHelper} (honours proxy headers),
     * falling back to {@link HttpServletRequest#getRemoteAddr()} when the helper
     * is not available — e.g. in slim test DI graphs.
     */
    private static String resolveClientIp(final HttpServletRequest req) {
        try {
            return ComponentUtil.getRateLimitHelper().getClientIp(req);
        } catch (final RuntimeException e) {
            if (ipResolveWarned.compareAndSet(false, true)) {
                logger.warn("RateLimitHelper.getClientIp unavailable; falling back to getRemoteAddr", e);
            }
            return req.getRemoteAddr();
        }
    }

}
