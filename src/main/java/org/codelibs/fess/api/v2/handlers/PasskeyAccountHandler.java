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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.exception.PasskeyException;
import org.codelibs.fess.app.service.PasskeyService;
import org.codelibs.fess.app.service.UserService;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.app.web.base.login.LocalUserCredential;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.user.exentity.Passkey;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tools.jackson.databind.json.JsonMapper;

/**
 * Handles the passkey (WebAuthn) endpoints of the signed-in local user's own account:
 * {@code POST /api/v2/auth/passkey/register/options}, {@code POST /api/v2/auth/passkey/register/verify},
 * {@code GET /api/v2/auth/passkey} and {@code DELETE /api/v2/auth/passkey/{id}}.
 *
 * <p>While {@link PasskeyService#isEnabled()} is false every endpoint answers {@code 404 not_found},
 * the same as a path that is not routed at all. Every endpoint needs a login session
 * ({@code 401 auth_required} otherwise).</p>
 *
 * <p>Only a user who signed in as a {@code fess_user.user} row ({@link User}) has passkeys. Any other
 * session (a directory user, an SSO user) cannot register one and is answered as an anonymous caller
 * is; its list is empty and every delete answers {@code 404}, even when a local user of the same name
 * exists.</p>
 *
 * <p>{@code register/options} takes {@code { "current_password": "..." }} and re-authenticates the
 * user before any challenge is made, as {@link PasswordChangeHandler} does: through
 * {@link FessLoginAssist#findLoginUser}, on the {@link LoginRateLimiter.Scope#USER} bucket of the
 * password login (checked on entry, a slot taken by a wrong password, cleared on success). A password
 * the directory accepted instead of the local one does not count as verified. {@code register/verify}
 * takes {@code { "label": "...", "credential": <PublicKeyCredential JSON> }}; the label is required,
 * at most {@value PasskeyService#MAX_LABEL_LENGTH} code points, and has no control or bidirectional
 * formatting character ({@code reason} {@code label_required}, {@code label_too_long} or
 * {@code label_invalid_characters}). A user has at most {@value PasskeyService#MAX_PASSKEYS_PER_USER}
 * passkeys: {@code register/options} answers {@code 400} with {@code reason=too_many_passkeys} beyond
 * that. Whatever else makes the registration fail is one {@code 400 invalid_request} with
 * {@code reason=registration_failed}; the cause is logged only.</p>
 *
 * <p>The list answers {@code { "passkeys": [ { "id", "label", "created_at", "last_used_at" } ], "can_register": true }}
 * and never the key material. {@code can_register} is false for a session that cannot register (see
 * above), so a client shows no registration UI for it. Deleting a passkey that is not the caller's answers exactly as deleting one
 * that does not exist.</p>
 */
public class PasskeyAccountHandler {

    private static final Logger logger = LogManager.getLogger(PasskeyAccountHandler.class);

    /** Cap of the {@code register/options} body, as for the password change. */
    private static final int MAX_OPTIONS_BODY_BYTES = 4 * 1024;

    /**
     * Cap of the {@code register/verify} body. A registration response with attestation {@code none}
     * is a few hundred bytes, and stays well under it even with the longest credential id accepted.
     */
    private static final int MAX_VERIFY_BODY_BYTES = 16 * 1024;

    /**
     * A credential id accepted is 1 to {@value PasskeyService#MAX_CREDENTIAL_ID_BYTES} bytes, i.e. at
     * most {@value PasskeyService#MAX_CREDENTIAL_ID_LENGTH} characters of unpadded Base64url, the
     * longest document id the search engine takes.
     */
    private static final Pattern CREDENTIAL_ID_PATTERN =
            Pattern.compile("[A-Za-z0-9_-]{1," + PasskeyService.MAX_CREDENTIAL_ID_LENGTH + "}");

    static final String LIST_PATH = "/auth/passkey";

    static final String OPTIONS_PATH = "/auth/passkey/register/options";

    static final String VERIFY_PATH = "/auth/passkey/register/verify";

    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    private final LoginRateLimiter injectedLimiter;

    private final PasskeyService injectedPasskeyService;

    /**
     * Default constructor used by the DI container. The rate limiter and the passkey service are
     * resolved on use, so DI bootstrap order is not constrained.
     */
    public PasskeyAccountHandler() {
        this(null, null);
    }

    /**
     * Test-friendly constructor. A {@code null} argument is resolved through DI on use.
     *
     * @param limiter the rate limiter to use, or {@code null}
     * @param passkeyService the passkey service to use, or {@code null}
     */
    PasskeyAccountHandler(final LoginRateLimiter limiter, final PasskeyService passkeyService) {
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
     * Looks up the {@code fess_user.user} row of a user.
     *
     * @param userName the name of the user
     * @return the local user, or empty when there is none
     */
    protected OptionalEntity<User> findLocalUser(final String userName) {
        return ComponentUtil.getComponent(UserService.class).getUserByName(userName);
    }

    /**
     * Ends the login sessions of the user other than the current one.
     *
     * @param userName the name of the user
     */
    protected void endOtherSessions(final String userName) {
        ComponentUtil.getComponent(UserService.class).endOtherSessions(userName);
    }

    /**
     * Processes one {@code POST /api/v2/auth/passkey/register/options} request.
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @throws IOException if writing the envelope or reading the body fails
     */
    public void registerOptions(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        final PasskeyService passkeyService = passkeyService();
        if (!isAvailable(passkeyService, OPTIONS_PATH, "POST", req, res)) {
            return;
        }
        final FessLoginAssist assist = ComponentUtil.getFessLoginAssist();
        final String userName = localSessionUserName(assist, res);
        if (userName == null) {
            return;
        }
        final Map<String, Object> body = readBody(req, res, MAX_OPTIONS_BODY_BYTES);
        if (body == null) {
            return;
        }
        final String currentPw = body.get("current_password") instanceof final String s ? s : null;
        if (StringUtil.isBlank(currentPw)) {
            ComponentUtil.getV2EnvelopeWriter()
                    .writeErrorWithDetails(res, V2ErrorCode.INVALID_REQUEST, "current_password is required",
                            Map.of("reason", "current_password_required"));
            return;
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int maxPwLen = fessConfig.getPasswordMaxLengthOrDefault();
        if (currentPw.length() > maxPwLen) {
            ComponentUtil.getV2EnvelopeWriter()
                    .writeError(res, V2ErrorCode.INVALID_REQUEST, "current_password exceeds the maximum length of " + maxPwLen);
            return;
        }

        // The USER bucket of the password login and the password change: a stolen session must not
        // make this a way around their limits.
        final LoginRateLimiter rate = limiter();
        int userLimit = 0;
        int lockoutSec = 0;
        try {
            final Integer limit = fessConfig.getThemeApiLoginRateLimitPerUserPerMinuteAsInteger();
            final Integer lockout = fessConfig.getThemeApiLoginLockoutSecondsAsInteger();
            userLimit = limit == null ? 0 : limit;
            lockoutSec = lockout == null ? 0 : lockout;
        } catch (final RuntimeException e) {
            // as PasswordChangeHandler: a broken setting disables the gate rather than every registration
            logger.warn("[v2/passkey] could not read the rate-limit settings; gate disabled", e);
        }
        if (userLimit > 0 && !rate.peek(LoginRateLimiter.Scope.USER, userName, userLimit, 60)) {
            if (rate.lockOut(LoginRateLimiter.Scope.USER, userName, lockoutSec)) {
                logger.warn("[v2/passkey] rate limit exceeded; locking out: userId={}, lockoutSec={}, limit={}/min", userName, lockoutSec,
                        userLimit);
            } else if (logger.isDebugEnabled()) {
                logger.debug("[v2/passkey] refused a registration during the active lockout: userId={}", userName);
            }
            writeRateLimited(res, lockoutSec);
            return;
        }

        final OptionalEntity<? extends FessUser> verified;
        try {
            verified = assist.findLoginUser(new LocalUserCredential(userName, currentPw));
        } catch (final RuntimeException e) {
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(res, e, logger, "/api/v2" + OPTIONS_PATH + " findLoginUser");
            return;
        }
        // findLoginUser asks the directory first: a password it accepted is not the local one.
        final User user =
                verified != null && verified.isPresent() && verified.get() instanceof final User u && userName.equals(u.getName()) ? u
                        : null;
        if (user == null) {
            if (userLimit > 0 && !rate.allow(LoginRateLimiter.Scope.USER, userName, userLimit, 60)) {
                if (rate.lockOut(LoginRateLimiter.Scope.USER, userName, lockoutSec)) {
                    logger.warn("[v2/passkey] rate limit exhausted; locking out: userId={}, lockoutSec={}, limit={}/min", userName,
                            lockoutSec, userLimit);
                }
                writeRateLimited(res, lockoutSec);
                return;
            }
            if (logger.isInfoEnabled()) {
                logger.info("[v2/passkey] registration refused: userId={}, reason=invalid_current_password", userName);
            }
            ComponentUtil.getV2EnvelopeWriter()
                    .writeErrorWithDetails(res, V2ErrorCode.AUTH_REQUIRED, "invalid current password",
                            Map.of("reason", "invalid_current_password"));
            return;
        }
        rate.clear(LoginRateLimiter.Scope.USER, userName);

        final HttpSession session = req.getSession(false);
        if (session == null) {
            writeLoginRequired(res);
            return;
        }
        final Map<String, Object> options;
        try {
            options = passkeyService.createRegistrationOptions(session, user);
        } catch (final PasskeyException e) {
            if (e.getMessage() == null || !e.getMessage().startsWith("reason=too_many_passkeys")) {
                ComponentUtil.getV2EnvelopeWriter().writeInternalError(res, e, logger, "/api/v2" + OPTIONS_PATH);
                return;
            }
            if (logger.isInfoEnabled()) {
                logger.info("[v2/passkey] registration refused: userId={}, {}", userName, e.getMessage());
            }
            ComponentUtil.getV2EnvelopeWriter()
                    .writeErrorWithDetails(res, V2ErrorCode.INVALID_REQUEST,
                            "a user can register at most " + PasskeyService.MAX_PASSKEYS_PER_USER + " passkeys",
                            Map.of("reason", "too_many_passkeys", "max_passkeys", PasskeyService.MAX_PASSKEYS_PER_USER));
            return;
        } catch (final RuntimeException e) {
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(res, e, logger, "/api/v2" + OPTIONS_PATH);
            return;
        }
        ComponentUtil.getV2EnvelopeWriter().writeSuccess(res, options);
    }

    /**
     * Processes one {@code POST /api/v2/auth/passkey/register/verify} request.
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @throws IOException if writing the envelope or reading the body fails
     */
    public void registerVerify(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        final PasskeyService passkeyService = passkeyService();
        if (!isAvailable(passkeyService, VERIFY_PATH, "POST", req, res)) {
            return;
        }
        final String userName = localSessionUserName(ComponentUtil.getFessLoginAssist(), res);
        if (userName == null) {
            return;
        }
        final Map<String, Object> body = readBody(req, res, MAX_VERIFY_BODY_BYTES);
        if (body == null) {
            return;
        }
        // Checked before the service runs, because it consumes the challenge whatever the outcome.
        final String label = body.get("label") instanceof final String s ? s.trim() : null;
        final String labelProblem = PasskeyService.labelProblem(label);
        if ("label_required".equals(labelProblem)) {
            ComponentUtil.getV2EnvelopeWriter()
                    .writeErrorWithDetails(res, V2ErrorCode.INVALID_REQUEST, "label is required", Map.of("reason", "label_required"));
            return;
        }
        if ("label_too_long".equals(labelProblem)) {
            ComponentUtil.getV2EnvelopeWriter()
                    .writeErrorWithDetails(res, V2ErrorCode.INVALID_REQUEST,
                            "label exceeds the maximum length of " + PasskeyService.MAX_LABEL_LENGTH,
                            Map.of("reason", "label_too_long", "max_length", PasskeyService.MAX_LABEL_LENGTH));
            return;
        }
        if (labelProblem != null) {
            ComponentUtil.getV2EnvelopeWriter()
                    .writeErrorWithDetails(res, V2ErrorCode.INVALID_REQUEST, "label contains a control or bidirectional character",
                            Map.of("reason", "label_invalid_characters"));
            return;
        }
        if (!(body.get("credential") instanceof Map)) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INVALID_REQUEST, "credential is required");
            return;
        }

        final OptionalEntity<User> user;
        try {
            user = findLocalUser(userName);
        } catch (final RuntimeException e) {
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(res, e, logger, "/api/v2" + VERIFY_PATH + " findLocalUser");
            return;
        }
        if (user == null || !user.isPresent()) {
            writeLoginRequired(res);
            return;
        }

        final Passkey passkey;
        try {
            final HttpSession session = req.getSession(false);
            if (session == null) {
                throw new PasskeyException("reason=no_session");
            }
            passkey = passkeyService.verifyRegistration(session, user.get(), label, MAPPER.writeValueAsString(body.get("credential")));
        } catch (final PasskeyException e) {
            if (logger.isInfoEnabled()) {
                logger.info("[v2/passkey] registration failed: userId={}, {}", userName, e.getMessage());
            }
            writeRegistrationFailed(res);
            return;
        } catch (final Exception e) {
            logger.warn("[v2/passkey] registration failed unexpectedly: userId={}, error={}", userName, e.getClass().getSimpleName());
            writeRegistrationFailed(res);
            return;
        }
        if (logger.isInfoEnabled()) {
            logger.info("[v2/passkey] registered a passkey: userId={}, credentialId={}", userName, abbreviate(passkey.getId()));
        }
        ComponentUtil.getActivityHelper()
                .passkeyRegistered(ComponentUtil.getFessLoginAssist().getSavedUserBean(), userName, abbreviate(passkey.getId()));
        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("passkey", toPayload(passkey));
        ComponentUtil.getV2EnvelopeWriter().writeSuccess(res, payload);
    }

    /**
     * Processes one {@code GET /api/v2/auth/passkey} request.
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @throws IOException if writing the envelope fails
     */
    public void list(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        final PasskeyService passkeyService = passkeyService();
        if (!isAvailable(passkeyService, LIST_PATH, "GET", req, res)) {
            return;
        }
        final FessUserBean userBean = sessionUserBean(ComponentUtil.getFessLoginAssist(), res);
        if (userBean == null) {
            return;
        }
        final List<Map<String, Object>> passkeys = new ArrayList<>();
        final boolean canRegister = userBean.getFessUser() instanceof User;
        if (canRegister) {
            try {
                for (final Passkey passkey : passkeyService.getPasskeys(userBean.getUserId())) {
                    passkeys.add(toPayload(passkey));
                }
            } catch (final RuntimeException e) {
                ComponentUtil.getV2EnvelopeWriter().writeInternalError(res, e, logger, "/api/v2" + LIST_PATH);
                return;
            }
        }
        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("passkeys", passkeys);
        payload.put("can_register", canRegister);
        ComponentUtil.getV2EnvelopeWriter().writeSuccess(res, payload);
    }

    /**
     * Processes one {@code DELETE /api/v2/auth/passkey/{id}} request.
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @param credentialId the credential id from the path (Base64url)
     * @throws IOException if writing the envelope fails
     */
    public void delete(final HttpServletRequest req, final HttpServletResponse res, final String credentialId) throws IOException {
        final PasskeyService passkeyService = passkeyService();
        if (!isAvailable(passkeyService, LIST_PATH + "/" + credentialId, "DELETE", req, res)) {
            return;
        }
        final FessUserBean userBean = sessionUserBean(ComponentUtil.getFessLoginAssist(), res);
        if (userBean == null) {
            return;
        }
        // A malformed id, an id nobody has, another user's passkey and a session without local
        // passkeys are all answered the same.
        if (credentialId == null || !CREDENTIAL_ID_PATTERN.matcher(credentialId).matches() || !(userBean.getFessUser() instanceof User)) {
            writePasskeyNotFound(res);
            return;
        }
        final String userName = userBean.getUserId();
        final boolean deleted;
        try {
            deleted = passkeyService.delete(userName, credentialId);
        } catch (final RuntimeException e) {
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(res, e, logger, "/api/v2" + LIST_PATH + " delete");
            return;
        }
        if (!deleted) {
            if (logger.isDebugEnabled()) {
                logger.debug("[v2/passkey] no such passkey of the user: userId={}, credentialId={}", userName, abbreviate(credentialId));
            }
            writePasskeyNotFound(res);
            return;
        }
        if (logger.isInfoEnabled()) {
            logger.info("[v2/passkey] deleted a passkey: userId={}, credentialId={}", userName, abbreviate(credentialId));
        }
        ComponentUtil.getActivityHelper()
                .passkeyDeleted(ComponentUtil.getFessLoginAssist().getSavedUserBean(), userName, abbreviate(credentialId));
        // a session opened with the removed passkey must not outlive it; this one stays
        try {
            endOtherSessions(userName);
        } catch (final RuntimeException e) {
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(res, e, logger, "/api/v2" + LIST_PATH + " endOtherSessions");
            return;
        }
        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("ok", true);
        ComponentUtil.getV2EnvelopeWriter().writeSuccess(res, payload);
    }

    // ===================================================================================
    //                                                                              Gates
    //                                                                              =====
    /**
     * Answers {@code 404 not_found} while the feature is off, worded as {@code SearchApiV2Manager}
     * words a path that is not routed, and {@code 405} to any other method than {@code method}.
     */
    private boolean isAvailable(final PasskeyService passkeyService, final String subPath, final String method,
            final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        if (!passkeyService.isEnabled()) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.NOT_FOUND, "endpoint not found: " + subPath);
            return false;
        }
        if (!method.equalsIgnoreCase(req.getMethod())) {
            res.setHeader("Allow", method);
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return false;
        }
        return true;
    }

    /**
     * Returns the user bean of the session, or answers {@code 401} and returns {@code null}.
     */
    private FessUserBean sessionUserBean(final FessLoginAssist assist, final HttpServletResponse res) throws IOException {
        final OptionalThing<FessUserBean> userBean;
        try {
            userBean = assist.getSavedUserBean();
        } catch (final RuntimeException e) {
            logger.warn("[v2/passkey] could not read the login state", e);
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INTERNAL_ERROR, "internal error");
            return null;
        }
        if (userBean == null || !userBean.isPresent() || StringUtil.isBlank(userBean.get().getUserId())) {
            writeLoginRequired(res);
            return null;
        }
        return userBean.get();
    }

    /**
     * Returns the name of the session's user when it signed in as a local user, or answers
     * {@code 401} and returns {@code null}. Any other kind of session is answered as no session is.
     */
    private String localSessionUserName(final FessLoginAssist assist, final HttpServletResponse res) throws IOException {
        final FessUserBean userBean = sessionUserBean(assist, res);
        if (userBean == null) {
            return null;
        }
        if (!(userBean.getFessUser() instanceof User)) {
            if (logger.isDebugEnabled()) {
                logger.debug("[v2/passkey] registration refused: userId={}, reason=not_a_local_user", userBean.getUserId());
            }
            writeLoginRequired(res);
            return null;
        }
        return userBean.getUserId();
    }

    private Map<String, Object> readBody(final HttpServletRequest req, final HttpServletResponse res, final int maxBytes)
            throws IOException {
        try {
            return ComponentUtil.getV2JsonBody().read(req, maxBytes);
        } catch (final V2JsonBody.PayloadTooLargeException e) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.PAYLOAD_TOO_LARGE, e.getMessage());
        } catch (final V2JsonBody.UnsupportedMediaTypeException e) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.UNSUPPORTED_MEDIA_TYPE, e.getMessage());
        } catch (final V2JsonBody.MalformedJsonException e) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INVALID_REQUEST, e.getMessage());
        }
        return null;
    }

    // ===================================================================================
    //                                                                            Answers
    //                                                                            =======
    private static Map<String, Object> toPayload(final Passkey passkey) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", passkey.getId());
        map.put("label", passkey.getLabel());
        map.put("created_at", passkey.getCreatedAt());
        map.put("last_used_at", passkey.getLastUsedAt());
        return map;
    }

    private static void writeLoginRequired(final HttpServletResponse res) throws IOException {
        ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.AUTH_REQUIRED, "login required");
    }

    private static void writeRateLimited(final HttpServletResponse res, final int lockoutSec) throws IOException {
        res.setHeader("Retry-After", Integer.toString(Math.max(lockoutSec, 1)));
        ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.RATE_LIMITED, "too many passkey registration attempts");
    }

    private static void writeRegistrationFailed(final HttpServletResponse res) throws IOException {
        ComponentUtil.getV2EnvelopeWriter()
                .writeErrorWithDetails(res, V2ErrorCode.INVALID_REQUEST, "passkey registration failed",
                        Map.of("reason", "registration_failed"));
    }

    private static void writePasskeyNotFound(final HttpServletResponse res) throws IOException {
        ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.NOT_FOUND, "passkey not found");
    }

    private static String abbreviate(final String credentialId) {
        return PasskeyService.abbreviate(credentialId);
    }
}
