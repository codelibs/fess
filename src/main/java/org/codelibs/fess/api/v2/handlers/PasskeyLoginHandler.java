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
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.exception.PasskeyException;
import org.codelibs.fess.app.service.PasskeyService;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.app.web.base.login.PasskeyCredential;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.login.exception.LoginFailureException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tools.jackson.databind.json.JsonMapper;

/**
 * Handles {@code POST /api/v2/auth/passkey/login/options} and
 * {@code POST /api/v2/auth/passkey/login/verify}: the passkey (WebAuthn) login of a local user.
 *
 * <p>{@code options} answers the options for {@code navigator.credentials.get} and keeps their
 * challenge in the session. It takes no body. {@code verify} takes
 * {@code { "credential": <PublicKeyCredential JSON>, "return_to": "/..." }} and, once
 * {@link PasskeyService#verifyLogin} accepts the assertion, logs the user it names in through
 * {@link FessLoginAssist} and answers exactly what {@link LoginHandler} answers on success.</p>
 *
 * <p>While {@link PasskeyService#isEnabled()} is false both endpoints answer {@code 404 not_found},
 * the same as a path that is not routed at all. Both are behind the {@link LoginRateLimiter.Scope#IP}
 * bucket of the password login, with the same {@code theme.api.login.*} limits. {@code verify} is the
 * login attempt and is counted exactly as {@link LoginHandler} counts one: it takes a slot on entry,
 * keeps it when it fails and gives it back when it succeeds. {@code options} verifies nothing, so it
 * only checks the bucket and is refused while the address is locked out; counting it too would make
 * every successful passkey login cost a slot, which a successful password login does not. The user
 * is not known before the assertion verifies, so there is no USER scope here. Every failure the
 * client can see is the {@code 401} of a failed password login; the reason goes to the log only.</p>
 */
public class PasskeyLoginHandler {

    private static final Logger logger = LogManager.getLogger(PasskeyLoginHandler.class);

    /**
     * Cap of the {@code verify} body. An assertion is a few hundred bytes, and stays under 5 KiB even
     * with a credential id of the maximum 1023 bytes (sent twice, as {@code id} and {@code rawId}) and
     * an RSA-4096 signature.
     */
    private static final int MAX_BODY_BYTES = 16 * 1024;

    static final String OPTIONS_PATH = "/auth/passkey/login/options";

    static final String VERIFY_PATH = "/auth/passkey/login/verify";

    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    private final LoginRateLimiter injectedLimiter;

    private final PasskeyService injectedPasskeyService;

    /**
     * Default constructor used by the DI container. The rate limiter and the passkey service are
     * resolved on use, so DI bootstrap order is not constrained.
     */
    public PasskeyLoginHandler() {
        this(null, null);
    }

    /**
     * Test-friendly constructor. A {@code null} argument is resolved through DI on use.
     *
     * @param limiter the rate limiter to use, or {@code null}
     * @param passkeyService the passkey service to use, or {@code null}
     */
    PasskeyLoginHandler(final LoginRateLimiter limiter, final PasskeyService passkeyService) {
        this.injectedLimiter = limiter;
        this.injectedPasskeyService = passkeyService;
    }

    private LoginRateLimiter limiter() {
        return injectedLimiter != null ? injectedLimiter : ComponentUtil.getLoginRateLimiter();
    }

    private PasskeyService passkeyService() {
        return injectedPasskeyService != null ? injectedPasskeyService : ComponentUtil.getComponent(PasskeyService.class);
    }

    /**
     * Processes one {@code POST /api/v2/auth/passkey/login/options} request.
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @throws IOException if writing the envelope fails
     */
    public void options(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        final PasskeyService passkeyService = passkeyService();
        if (!isAvailable(passkeyService, OPTIONS_PATH, req, res)) {
            return;
        }
        final String clientIp = LoginHandler.resolveClientIp(req);
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!limiter().peek(LoginRateLimiter.Scope.IP, clientIp, fessConfig.getThemeApiLoginRateLimitPerIpPerMinuteAsInteger(), 60)) {
            if (logger.isDebugEnabled()) {
                logger.debug("[v2/passkey] refused the login options during the active ip lockout: clientIp={}", clientIp);
            }
            res.setHeader("Retry-After", Integer.toString(fessConfig.getThemeApiLoginLockoutSecondsAsInteger()));
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.RATE_LIMITED, "too many login attempts (ip)");
            return;
        }
        final Map<String, Object> options;
        try {
            options = passkeyService.createLoginOptions(req.getSession(true));
        } catch (final Exception e) {
            logger.warn("[v2/passkey] could not create the login options: clientIp={}, error={}", clientIp, e.getClass().getSimpleName());
            LoginHandler.writeInvalidCredentials(res);
            return;
        }
        ComponentUtil.getV2EnvelopeWriter().writeSuccess(res, options);
    }

    /**
     * Processes one {@code POST /api/v2/auth/passkey/login/verify} request.
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @throws IOException if writing the envelope or reading the body fails
     */
    public void verify(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        final PasskeyService passkeyService = passkeyService();
        if (!isAvailable(passkeyService, VERIFY_PATH, req, res)) {
            return;
        }
        final LoginRateLimiter limiter = limiter();
        final String clientIp = LoginHandler.resolveClientIp(req);
        if (!LoginHandler.takeIpSlot(limiter, clientIp, res)) {
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
        if (!(body.get("credential") instanceof Map)) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INVALID_REQUEST, "credential is required");
            return;
        }
        final String returnTo = body.get("return_to") instanceof final String s ? s : null;

        final FessLoginAssist assist;
        final String prevUserId;
        try {
            assist = ComponentUtil.getFessLoginAssist();
            final OptionalThing<FessUserBean> existingBean = assist.getSavedUserBean();
            prevUserId = existingBean.isPresent() ? existingBean.get().getUserId() : null;
        } catch (final RuntimeException e) {
            logger.warn("[v2/passkey] login failed unexpectedly: could not check the login state", e);
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INTERNAL_ERROR, "internal error");
            return;
        }

        // Only what verifyLogin returns may become a credential. Whatever goes wrong before that, a
        // rejected assertion as well as a broken index or an unreadable stored passkey, is one failure
        // to the client: anything else would tell a caller which credential ids exist.
        final String userName;
        try {
            final HttpSession session = req.getSession(false);
            if (session == null) {
                throw new PasskeyException("reason=no_session");
            }
            userName = passkeyService.verifyLogin(session, MAPPER.writeValueAsString(body.get("credential")));
        } catch (final PasskeyException e) {
            if (logger.isInfoEnabled()) {
                logger.info("[v2/passkey] login failed: clientIp={}, {}", clientIp, e.getMessage());
            }
            rejectLogin(res, OptionalThing.empty());
            return;
        } catch (final Exception e) {
            logger.warn("[v2/passkey] login failed unexpectedly: clientIp={}, error={}", clientIp, e.getClass().getSimpleName());
            rejectLogin(res, OptionalThing.empty());
            return;
        }

        final PasskeyCredential credential = PasskeyCredential.verified(userName);
        try {
            assist.login(credential, op -> {});
        } catch (final LoginFailureException e) {
            // The passkey verified but its user can no longer sign in, e.g. it was deleted meanwhile.
            if (logger.isInfoEnabled()) {
                logger.info("[v2/passkey] login failed: clientIp={}, reason=user_rejected", clientIp);
            }
            rejectLogin(res, OptionalThing.of((LoginCredential) credential));
            return;
        } catch (final RuntimeException e) {
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(res, e, logger, "/api/v2/auth/passkey/login/verify");
            return;
        }

        // Only the slot this very request took on entry is given back, as in LoginHandler.
        final Map<String, Object> payload = LoginHandler.completeLogin(req, res, assist, userName, prevUserId, returnTo,
                () -> limiter.release(LoginRateLimiter.Scope.IP, clientIp));
        if (payload == null) {
            return;
        }
        ComponentUtil.getV2EnvelopeWriter().writeSuccess(res, payload);
    }

    /**
     * Answers {@code 404 not_found} while the feature is off, worded as {@code SearchApiV2Manager}
     * words a path that is not routed, and {@code 405} to anything but {@code POST}.
     */
    private boolean isAvailable(final PasskeyService passkeyService, final String subPath, final HttpServletRequest req,
            final HttpServletResponse res) throws IOException {
        if (!passkeyService.isEnabled()) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.NOT_FOUND, "endpoint not found: " + subPath);
            return false;
        }
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            res.setHeader("Allow", "POST");
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return false;
        }
        return true;
    }

    /**
     * Records the {@code LOGIN_FAILURE} activity, as {@link LoginHandler} does for a rejected
     * password, and answers its {@code 401}. The IP slot taken on entry is kept.
     */
    private void rejectLogin(final HttpServletResponse res, final OptionalThing<LoginCredential> credential) throws IOException {
        try {
            ComponentUtil.getActivityHelper().loginFailure(credential);
        } catch (final RuntimeException e) {
            logger.warn("[v2/passkey] failed to write the LOGIN_FAILURE audit record", e);
        }
        LoginHandler.writeInvalidCredentials(res);
    }
}
