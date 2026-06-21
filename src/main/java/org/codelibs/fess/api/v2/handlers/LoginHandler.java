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
import org.codelibs.fess.api.v2.SessionCsrfTokenManager;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.app.web.base.login.LocalUserCredential;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.login.exception.LoginFailureException;

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

    private final LoginRateLimiter injectedLimiter;

    /**
     * Default constructor used by the DI container. The handler resolves the
     * shared {@link LoginRateLimiter} lazily via {@link #limiter()} so DI
     * bootstrap order is not constrained. The handler holds no per-request
     * state and is safe to share across concurrent requests.
     */
    public LoginHandler() {
        this(null);
    }

    /**
     * Test-friendly constructor allowing the caller to inject a specific
     * {@link LoginRateLimiter} instance (e.g. with a controllable clock).
     * Passing {@code null} causes the handler to resolve the DI-managed
     * singleton via {@link ComponentUtil#getLoginRateLimiter()} on first use.
     *
     * @param limiter the rate limiter to use, or {@code null} to resolve via DI
     */
    public LoginHandler(final LoginRateLimiter limiter) {
        this.injectedLimiter = limiter;
    }

    /**
     * Resolves the rate limiter: the constructor-injected instance when present
     * (tests), otherwise the DI-managed singleton. Login rate limiting is
     * security-critical, so an unavailable limiter is surfaced (not silently
     * skipped) by {@link ComponentUtil#getLoginRateLimiter()}.
     *
     * @return the rate limiter to use for this request
     */
    private LoginRateLimiter limiter() {
        return injectedLimiter != null ? injectedLimiter : ComponentUtil.getLoginRateLimiter();
    }

    /**
     * Processes one {@code POST /api/v2/auth/login} request.
     *
     * <p>Applies a tiered set of cheap-first gates (IP rate-limit, body parse,
     * presence checks, per-user rate-limit) before invoking
     * {@link FessLoginAssist} for credential verification. On success the
     * response carries the freshly issued CSRF token and an echo of the safe
     * {@code return_to} value; failures map to v2 error codes
     * ({@link V2ErrorCode#RATE_LIMITED}, {@link V2ErrorCode#INVALID_REQUEST},
     * {@link V2ErrorCode#AUTH_REQUIRED}, {@link V2ErrorCode#INTERNAL_ERROR}).</p>
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @throws IOException if writing the envelope or reading the body fails
     */
    public void handle(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            res.setHeader("Allow", "POST");
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        final LoginRateLimiter limiter = limiter();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int ipLimit = fessConfig.getThemeApiLoginRateLimitPerIpPerMinuteAsInteger();
        final int userLimit = fessConfig.getThemeApiLoginRateLimitPerUserPerMinuteAsInteger();
        final int lockoutSec = fessConfig.getThemeApiLoginLockoutSecondsAsInteger();
        final String clientIp = resolveClientIp(req);

        if (!limiter.allow(LoginRateLimiter.Scope.IP, clientIp, ipLimit, 60)) {
            limiter.lockOut(LoginRateLimiter.Scope.IP, clientIp, lockoutSec);
            res.setHeader("Retry-After", Integer.toString(lockoutSec));
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.RATE_LIMITED, "too many login attempts (ip)");
            return;
        }

        final Map<String, Object> body;
        try {
            body = ComponentUtil.getV2JsonBody().read(req, MAX_BODY_BYTES);
        } catch (final V2JsonBody.PayloadTooLargeException e) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INVALID_REQUEST, "payload too large");
            return;
        } catch (final V2JsonBody.MalformedJsonException | V2JsonBody.UnsupportedMediaTypeException e) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INVALID_REQUEST, e.getMessage());
            return;
        }
        final String username = stringOrNull(body.get("username"));
        final String password = stringOrNull(body.get("password"));
        final String returnTo = stringOrNull(body.get("return_to"));
        if (StringUtil.isBlank(username) || StringUtil.isBlank(password)) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INVALID_REQUEST, "username and password are required");
            return;
        }
        final String userKey = userScopeKey(clientIp, username);

        // USER-scope pre-validation uses peek() so the bucket is NOT consumed yet. The slot is
        // taken only on credential failure below, so a system-error path (e.g. login subsystem
        // down) does not count against the legitimate user's bucket. The USER bucket is keyed by
        // (clientIp, username) so an attacker cannot lock a victim's account from a different IP.
        if (!limiter.peek(LoginRateLimiter.Scope.USER, userKey, userLimit, 60)) {
            // Keep stamping the lockout internally (protects this (IP,user) pair), but do
            // NOT disclose the per-user rate-limit state. Return a generic 401 identical to a
            // credential rejection, with no Retry-After, so the response cannot be used to probe
            // a per-user counter. The IP-scope gate above still returns 429 + Retry-After.
            limiter.lockOut(LoginRateLimiter.Scope.USER, userKey, lockoutSec);
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.AUTH_REQUIRED, "invalid credentials");
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
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INTERNAL_ERROR, "internal error");
            return;
        }

        // C-2: POST /auth/login MUST always verify the supplied password. We previously had a
        // "same-user fast path" that returned a fresh CSRF token when the existing session was
        // already bound to the same userId — that branch skipped credential verification AND
        // rate-limiting AND session-id rotation, letting any holder of a valid session cookie
        // confirm session validity and rotate CSRF tokens without proving the password. The
        // intended "refresh CSRF token while keeping session" use case belongs to
        // GET /api/v2/ui/config, not the login endpoint. So we always go through assist.login()
        // regardless of existing session state; the post-success changeSessionId() handles
        // session fixation, and an account-switch is just the natural outcome of a fresh login.
        // Capture the previous user ID BEFORE authentication so it is available for the
        // post-success audit log, without logging anything here (A-1: avoid false-positive
        // audit lines when credentials are subsequently rejected).
        final String prevUserId;
        try {
            final OptionalThing<FessUserBean> existingBean = assist.getSavedUserBean();
            prevUserId = existingBean.isPresent() ? existingBean.get().getUserId() : null;
        } catch (final RuntimeException e) {
            logger.warn("login: could not check existing session state", e);
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INTERNAL_ERROR, "internal error");
            return;
        }

        try {
            assist.login(new LocalUserCredential(username, password), op -> {});
        } catch (final LoginFailureException e) {
            // Credential rejection consumes the USER slot exactly once, on the failure path
            // (keyed by (clientIp, username)). When the window is exhausted we also stamp a
            // lockUntil so subsequent requests from this (IP,user) are refused even after the
            // sliding window expires. The response is a generic 401 in BOTH cases (exhausted
            // or not), with no Retry-After, so the per-user counter state never leaks.
            if (!limiter.allow(LoginRateLimiter.Scope.USER, userKey, userLimit, 60)) {
                limiter.lockOut(LoginRateLimiter.Scope.USER, userKey, lockoutSec);
            }
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.AUTH_REQUIRED, "invalid credentials");
            return;
        } catch (final RuntimeException e) {
            // Anything else (DI binding failure, transient lookup error, etc.) is a system
            // error and must NOT count against the user bucket — otherwise a misconfigured
            // server would lock real users out. We surface INTERNAL_ERROR with a generic
            // message; the exception detail goes to the log only.
            logger.warn("login failed unexpectedly", e);
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INTERNAL_ERROR, "internal error");
            return;
        }

        // A-1: emit account-switch audit log AFTER successful credential verification so
        // that an unauthenticated attacker with a stolen session cookie cannot trigger a
        // false-positive audit line by posting bad credentials for a different username.
        if (prevUserId != null && !username.equals(prevUserId)) {
            logger.info("[v2/login] account switch: prevUserId={}, newUserId={}", prevUserId, username);
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
        // their next attempt after providing the correct credentials. The USER bucket is the
        // (clientIp, username) composite.
        limiter.clear(LoginRateLimiter.Scope.USER, userKey);
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
        ComponentUtil.getV2EnvelopeWriter().writeSuccess(res, payload);
    }

    /**
     * Builds the standard success payload for a logged-in user.
     * Uses {@link UserPayloads#toJson(FessUserBean)} to guarantee shape parity
     * with MeHandler (MJ-28).
     */
    private Map<String, Object> buildUserPayload(final FessUserBean u, final String token, final String returnTo) {
        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("user", ComponentUtil.getV2UserPayloads().toJson(u));
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
    private void addReturnTo(final Map<String, Object> payload, final String returnTo) {
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

    /**
     * Strictly coerces a JSON value to {@code String}. Non-string types (numbers,
     * booleans, arrays, objects, null) all return {@code null} — matching the
     * pattern used by other v2 handlers (e.g. ClickHandler). Calling
     * {@code Object.toString()} here would silently accept e.g. {@code 42} as a
     * username and let the auth flow proceed with a stringified non-string, which
     * is not what the wire contract intends.
     */
    private String stringOrNull(final Object v) {
        return v instanceof String s ? s : null;
    }

    /** Separator for USER-scope composite keys. NUL cannot appear in a resolved IP. */
    static final String USER_SCOPE_KEY_SEP = "\u0000";

    /**
     * Builds the USER-scope rate-limit key scoped to the originating client IP so that an
     * unauthenticated attacker cannot lock a victim's account by name from a different IP.
     * The limiter treats the key as opaque, so the (IP,user) scoping lives entirely
     * here in the handler — {@code LoginRateLimiter.Scope.USER} keeps its plain-key contract
     * (also used by PasswordChangeHandler with a bare userId).
     *
     * @param clientIp resolved client IP (see {@link #resolveClientIp})
     * @param username submitted username
     * @return composite key {@code clientIp + NUL + username}
     */
    String userScopeKey(final String clientIp, final String username) {
        return clientIp + USER_SCOPE_KEY_SEP + username;
    }

    /**
     * Resolves the client IP via {@code RateLimitHelper} (honours proxy headers),
     * falling back to {@link HttpServletRequest#getRemoteAddr()} when the helper
     * is not available — e.g. in slim test DI graphs.
     */
    private String resolveClientIp(final HttpServletRequest req) {
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
