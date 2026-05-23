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
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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
 */
public class LoginHandler {

    private static final Logger logger = LogManager.getLogger(LoginHandler.class);

    private static final int MAX_BODY_BYTES = 4 * 1024;

    /** One-shot warning flag so we log only the first IP-resolve failure per JVM lifetime. */
    private static final AtomicBoolean ipResolveWarned = new AtomicBoolean(false);

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

        final OptionalThing<FessUserBean> userBean = assist.getSavedUserBean();
        final Map<String, Object> payload = new LinkedHashMap<>();
        if (userBean.isPresent()) {
            final FessUserBean u = userBean.get();
            final Map<String, Object> userMap = new LinkedHashMap<>();
            userMap.put("user_id", u.getUserId());
            userMap.put("roles", arrayOrEmpty(u.getRoles()));
            userMap.put("groups", arrayOrEmpty(u.getGroups()));
            userMap.put("permissions", arrayOrEmpty(u.getPermissions()));
            userMap.put("editable", u.isEditable());
            payload.put("user", userMap);
        }
        payload.put("csrf_token", token);
        if (returnTo != null && returnTo.startsWith("/") && !returnTo.startsWith("//") && !returnTo.contains("\\")
                && !returnTo.contains("\0")) {
            payload.put("return_to", returnTo);
        }
        V2EnvelopeWriter.writeSuccess(res, payload);
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

    private static List<String> arrayOrEmpty(final String[] arr) {
        return arr == null ? List.of() : List.of(arr);
    }
}
