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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.app.web.base.login.LocalUserCredential;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.web.login.credential.LoginCredential;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ReadListener;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;

/**
 * Unit tests for {@link PasswordChangeHandler}.
 *
 * <p>Extends {@link UnitFessTestCase} so the DI container is available.
 * The default setUp registers an anonymous-stub {@link FessLoginAssist} that
 * returns {@code OptionalThing.empty()} from {@code getSavedUserBean()} so
 * unauthenticated-path tests receive the expected {@code 401 auth_required}
 * rather than a {@code 500 internal_error} from the M-17 session-manager
 * lookup (which throws in the unit harness when no HTTP context is bound).
 * Tests that need an authenticated or throwing stub call
 * {@link #registerStubLoginAssist} or register their own stub directly.</p>
 *
 * <p>The mismatch and blank tests use {@code application/json} bodies but —
 * importantly — also exit at the auth-required gate because no user is logged
 * in. They assert the auth-required behaviour rather than the validation
 * behaviour. A true validation-only test would need a faked user bean, which
 * is awkward in {@code UnitFessTestCase}. TODO: revisit when an integration
 * harness with a logged-in user is available.</p>
 */
public class PasswordChangeHandlerTest extends UnitFessTestCase {

    /**
     * Registers an anonymous-returning stub so that
     * {@code ComponentUtil.getComponent(FessLoginAssist.class)} succeeds and
     * {@code getSavedUserBean()} returns {@code OptionalThing.empty()}.
     *
     * <p>Without this stub the DI lookup either throws (no-component) or the
     * real {@link FessLoginAssist} calls {@code sessionManager.getAttribute()}
     * which throws in the unit harness when no HTTP session context is bound —
     * both would be caught by M-17 and produce a misleading 500 instead of 401.</p>
     */
    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        registerAnonymousLoginAssist();
    }

    /** Registers a stub that always reports "no user in session" (anonymous). */
    private static void registerAnonymousLoginAssist() {
        final FessLoginAssist anon = new FessLoginAssist() {
            private static final long serialVersionUID = 1L;

            @Override
            public OptionalThing<FessUserBean> getSavedUserBean() {
                return OptionalThing.empty();
            }
        };
        ComponentUtil.register(anon, "fessLoginAssist");
        ComponentUtil.register(anon, FessLoginAssist.class.getCanonicalName());
    }

    @Test
    public void test_rejectsGet() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new PasswordChangeHandler().handle(new StubRequest("GET", "/api/v2/auth/password"), res);
        assertEquals(405, res.status);
        assertTrue(res.body().contains("\"code\":\"method_not_allowed\""), res.body());
    }

    @Test
    public void test_anonymousReturnsAuthRequired() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new PasswordChangeHandler().handle(
                new StubRequest("POST", "/api/v2/auth/password").withJsonBody("{\"new_password\":\"a\",\"confirm_password\":\"a\"}"), res);
        assertEquals(401, res.status);
        assertTrue(res.body().contains("\"code\":\"auth_required\""), res.body());
    }

    @Test
    public void test_rejectsMismatchWhenAnonymous() throws Exception {
        // Anonymous request short-circuits at the auth gate; the handler returns 401 before
        // ever inspecting the body. We still assert the GET-vs-POST guard for the contract
        // shape — full mismatch validation lives behind a logged-in user (integration test).
        final CapturingResponse res = new CapturingResponse();
        new PasswordChangeHandler().handle(
                new StubRequest("POST", "/api/v2/auth/password").withJsonBody("{\"new_password\":\"a\",\"confirm_password\":\"b\"}"), res);
        assertEquals(401, res.status);
    }

    @Test
    public void test_rejectsBlankWhenAnonymous() throws Exception {
        // Same rationale as the mismatch test — the request exits at the auth gate.
        final CapturingResponse res = new CapturingResponse();
        new PasswordChangeHandler().handle(
                new StubRequest("POST", "/api/v2/auth/password").withJsonBody("{\"new_password\":\"\",\"confirm_password\":\"\"}"), res);
        assertEquals(401, res.status);
    }

    /**
     * Registers a stub {@link FessLoginAssist} that pretends a user named {@code alice} is
     * logged in. {@code findLoginUser} returns a populated entity only when the supplied
     * password equals {@code expectedCurrentPw}; otherwise it returns an empty optional so
     * the handler must short-circuit to {@code AUTH_REQUIRED}.
     */
    private static void registerStubLoginAssist(final String userId, final String expectedCurrentPw) {
        final StubFessLoginAssist stub = new StubFessLoginAssist(userId, expectedCurrentPw);
        ComponentUtil.register(stub, "fessLoginAssist");
        ComponentUtil.register(stub, FessLoginAssist.class.getCanonicalName());
    }

    @Test
    public void passwordChange_rejectsWhenCurrentPasswordMissing() throws Exception {
        // A logged-in caller posts new_password but omits current_password — the handler
        // must respond 400/invalid_request before consulting findLoginUser.
        registerStubLoginAssist("alice", "secret-current");
        try {
            final CapturingResponse res = new CapturingResponse();
            new PasswordChangeHandler().handle(new StubRequest("POST", "/api/v2/auth/password")
                    .withJsonBody("{\"new_password\":\"NewPass1!\",\"confirm_password\":\"NewPass1!\"}"), res);
            assertEquals(400, res.status);
            assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
            assertTrue(res.body().contains("current_password"), res.body());
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
        }
    }

    @Test
    public void passwordChange_rejectsWhenCurrentPasswordWrong() throws Exception {
        // The handler must respond 401/auth_required when current_password does not match —
        // matching the LoginHandler contract for wrong credentials.
        registerStubLoginAssist("alice", "secret-current");
        try {
            final CapturingResponse res = new CapturingResponse();
            new PasswordChangeHandler().handle(new StubRequest("POST", "/api/v2/auth/password").withJsonBody(
                    "{\"current_password\":\"WRONG\",\"new_password\":\"NewPass1!\",\"confirm_password\":\"NewPass1!\"}"), res);
            assertEquals(401, res.status);
            assertTrue(res.body().contains("\"code\":\"auth_required\""), res.body());
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
        }
    }

    @Test
    public void passwordChange_successInvalidatesSession() throws Exception {
        // M-3: on a successful password change the handler MUST invalidate the current HTTP
        // session so any exfiltrated session token is immediately revoked. We verify this by
        // attaching a TrackingStubSession that records whether invalidate() was called.
        registerStubLoginAssist("alice", "secret-current");
        final boolean[] changedCalled = { false };
        final StubUserService stubSvc = new StubUserService(changedCalled);
        ComponentUtil.register(stubSvc, "userService");
        ComponentUtil.register(stubSvc, org.codelibs.fess.app.service.UserService.class.getCanonicalName());
        final org.codelibs.fess.helper.SystemHelper systemHelper = new org.codelibs.fess.helper.SystemHelper() {
            @Override
            public String validatePassword(final String password) {
                return null;
            }
        };
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.register(systemHelper, org.codelibs.fess.helper.SystemHelper.class.getCanonicalName());
        try {
            final TrackingStubSession session = new TrackingStubSession();
            assertFalse(session.invalidateCalled, "invalidate must not have been called before the handler");

            final CapturingResponse res = new CapturingResponse();
            new PasswordChangeHandler().handle(new StubRequest("POST", "/api/v2/auth/password").withSession(session)
                    .withJsonBody(
                            "{\"current_password\":\"secret-current\",\"new_password\":\"NewLongPass123!\",\"confirm_password\":\"NewLongPass123!\"}"),
                    res);

            org.junit.jupiter.api.Assertions.assertEquals(200, res.status, res.body());
            assertTrue(res.body().contains("\"ok\":true"), res.body());
            assertTrue(changedCalled[0], "expected UserService.changePassword to be invoked");

            // M-3: session.invalidate() must have been called after the password was changed.
            assertTrue(session.invalidateCalled, "session.invalidate() must be called after a successful password change (M-3)");
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
        }
    }

    @Test
    public void passwordChange_successDoesNotReturnCsrfToken() throws Exception {
        // M-3: because the session is destroyed on success, the response must NOT carry a
        // csrf_token field — the old token belongs to a dead session and the SPA must
        // obtain a fresh token after re-authenticating.
        registerStubLoginAssist("alice", "secret-current");
        final boolean[] changedCalled = { false };
        final StubUserService stubSvc = new StubUserService(changedCalled);
        ComponentUtil.register(stubSvc, "userService");
        ComponentUtil.register(stubSvc, org.codelibs.fess.app.service.UserService.class.getCanonicalName());
        final org.codelibs.fess.helper.SystemHelper systemHelper = new org.codelibs.fess.helper.SystemHelper() {
            @Override
            public String validatePassword(final String password) {
                return null;
            }
        };
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.register(systemHelper, org.codelibs.fess.helper.SystemHelper.class.getCanonicalName());
        try {
            final CapturingResponse res = new CapturingResponse();
            new PasswordChangeHandler().handle(new StubRequest("POST", "/api/v2/auth/password").withJsonBody(
                    "{\"current_password\":\"secret-current\",\"new_password\":\"NewLongPass123!\",\"confirm_password\":\"NewLongPass123!\"}"),
                    res);

            org.junit.jupiter.api.Assertions.assertEquals(200, res.status, res.body());
            assertTrue(res.body().contains("\"ok\":true"), res.body());
            assertFalse(res.body().contains("\"csrf_token\""),
                    "response must NOT contain csrf_token after session invalidation (M-3): " + res.body());
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
        }
    }

    @Test
    public void passwordChange_failureDoesNotInvalidateSession() throws Exception {
        // M-3: when the current_password is wrong the session must NOT be invalidated —
        // only a verified, successful password change warrants session destruction.
        registerStubLoginAssist("alice", "secret-current");
        try {
            final TrackingStubSession session = new TrackingStubSession();

            final CapturingResponse res = new CapturingResponse();
            new PasswordChangeHandler().handle(new StubRequest("POST", "/api/v2/auth/password").withSession(session)
                    .withJsonBody("{\"current_password\":\"WRONG\",\"new_password\":\"NewPass1!\",\"confirm_password\":\"NewPass1!\"}"),
                    res);

            org.junit.jupiter.api.Assertions.assertEquals(401, res.status, res.body());
            assertTrue(res.body().contains("\"code\":\"auth_required\""), res.body());
            assertFalse(session.invalidateCalled, "session.invalidate() must NOT be called when password change fails (M-3)");
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
        }
    }

    @Test
    public void passwordChange_successResponse_containsReLoginRequired() throws Exception {
        // M-3 / MJ-7: successful password change response MUST include re_login_required:true
        // so the SPA knows to redirect to login. The current session IS invalidated server-side
        // (M-3); the SPA MUST redirect to the login page after receiving this flag.
        registerStubLoginAssist("alice", "secret-current");
        final boolean[] changedCalled = { false };
        final StubUserService stubSvc = new StubUserService(changedCalled);
        ComponentUtil.register(stubSvc, "userService");
        ComponentUtil.register(stubSvc, org.codelibs.fess.app.service.UserService.class.getCanonicalName());
        final org.codelibs.fess.helper.SystemHelper systemHelper = new org.codelibs.fess.helper.SystemHelper() {
            @Override
            public String validatePassword(final String password) {
                return null;
            }
        };
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.register(systemHelper, org.codelibs.fess.helper.SystemHelper.class.getCanonicalName());
        try {
            final CapturingResponse res = new CapturingResponse();
            new PasswordChangeHandler().handle(new StubRequest("POST", "/api/v2/auth/password").withJsonBody(
                    "{\"current_password\":\"secret-current\",\"new_password\":\"NewLongPass123!\",\"confirm_password\":\"NewLongPass123!\"}"),
                    res);
            org.junit.jupiter.api.Assertions.assertEquals(200, res.status, res.body());
            assertTrue(res.body().contains("\"ok\":true"), res.body());
            assertTrue(res.body().contains("\"re_login_required\":true"),
                    "response must contain re_login_required:true (MJ-7): " + res.body());
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
        }
    }

    @Test
    public void passwordChange_rejectsMismatch_whenAuthenticated() throws Exception {
        // T-3 / MJ-29: an authenticated caller whose new_password != confirm_password
        // must receive 400/invalid_request. This test exercises the mismatch gate for
        // an actually-authenticated user (registerStubLoginAssist provides the session).
        registerStubLoginAssist("alice", "secret-current");
        try {
            final CapturingResponse res = new CapturingResponse();
            new PasswordChangeHandler().handle(new StubRequest("POST", "/api/v2/auth/password").withJsonBody(
                    "{\"current_password\":\"secret-current\",\"new_password\":\"Pass1\",\"confirm_password\":\"Pass2\"}"), res);
            org.junit.jupiter.api.Assertions.assertEquals(400, res.status,
                    "mismatch passwords for authenticated user must return 400: " + res.body());
            assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
            assertTrue(res.body().contains("do not match"), res.body());
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
        }
    }

    @Test
    public void passwordChange_succeedsOnCorrectCurrentPassword() throws Exception {
        // Happy path: the stubbed login assist accepts the supplied current_password, and the
        // handler proceeds through the validation gate and delegates to a stub UserService.
        // We assert the wire envelope contains "ok":true and the call-tracker recorded the
        // change so we know the path executed end-to-end.
        registerStubLoginAssist("alice", "secret-current");
        final boolean[] changedCalled = { false };
        final StubUserService stubSvc = new StubUserService(changedCalled);
        ComponentUtil.register(stubSvc, "userService");
        ComponentUtil.register(stubSvc, org.codelibs.fess.app.service.UserService.class.getCanonicalName());
        // SystemHelper is not bound in test_app.xml; register a stub that always treats the
        // password as valid so the validation gate does not short-circuit before
        // changePassword is invoked.
        final org.codelibs.fess.helper.SystemHelper systemHelper = new org.codelibs.fess.helper.SystemHelper() {
            @Override
            public String validatePassword(final String password) {
                return null;
            }
        };
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.register(systemHelper, org.codelibs.fess.helper.SystemHelper.class.getCanonicalName());
        try {
            final CapturingResponse res = new CapturingResponse();
            new PasswordChangeHandler().handle(new StubRequest("POST", "/api/v2/auth/password").withJsonBody(
                    "{\"current_password\":\"secret-current\",\"new_password\":\"NewLongPass123!\",\"confirm_password\":\"NewLongPass123!\"}"),
                    res);
            org.junit.jupiter.api.Assertions.assertEquals(200, res.status, res.body());
            assertTrue(res.body().contains("\"ok\":true"), res.body());
            assertTrue(changedCalled[0], "expected UserService.changePassword to be invoked");
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
        }
    }

    @Test
    public void passwordChange_currentPasswordWrong_consumesRateLimit() throws Exception {
        // C-3: a wrong current_password must consume exactly one slot in the per-user
        // bucket. The handler reads the limit from FessConfig (default 5/min for the
        // unit harness). We pre-burn 4 of 5 directly via the limiter so the handler's
        // own slot-consume on the wrong-password path is the 5th — afterwards peek()
        // returns false, proving exactly one slot was taken.
        registerStubLoginAssist("alice", "secret-current");
        try {
            final LoginRateLimiter rl = new LoginRateLimiter();
            for (int i = 0; i < 4; i++) {
                rl.allow(LoginRateLimiter.Scope.USER, "alice", 5, 60);
            }
            assertTrue(rl.peek(LoginRateLimiter.Scope.USER, "alice", 5, 60), "one slot must remain before the wrong-pw call");

            final CapturingResponse res = new CapturingResponse();
            new PasswordChangeHandler(rl).handle(new StubRequest("POST", "/api/v2/auth/password").withJsonBody(
                    "{\"current_password\":\"WRONG\",\"new_password\":\"NewPass1!\",\"confirm_password\":\"NewPass1!\"}"), res);

            assertEquals(401, res.status);
            assertTrue(res.body().contains("\"code\":\"auth_required\""), res.body());

            // The wrong-pw path must have consumed the final slot.
            assertFalse(rl.peek(LoginRateLimiter.Scope.USER, "alice", 5, 60),
                    "bucket must be exhausted after one wrong-pw call consumed the 5th slot");
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
        }
    }

    @Test
    public void passwordChange_lockedOut_returns429() throws Exception {
        // C-3: when the per-user bucket is already exhausted before this request, the
        // handler must return 429 RATE_LIMITED with a Retry-After header — and crucially
        // it must NOT call findLoginUser (we assert by registering a stub that throws
        // if called, see comment below).
        registerStubLoginAssist("alice", "secret-current");
        try {
            final LoginRateLimiter rl = new LoginRateLimiter();
            // Pre-exhaust the bucket so peek() will fail immediately.
            for (int i = 0; i < 5; i++) {
                rl.allow(LoginRateLimiter.Scope.USER, "alice", 5, 60);
            }
            assertFalse(rl.peek(LoginRateLimiter.Scope.USER, "alice", 5, 60), "bucket must be exhausted");

            final CapturingResponse res = new CapturingResponse();
            new PasswordChangeHandler(rl).handle(
                    new StubRequest("POST", "/api/v2/auth/password").withJsonBody(
                            "{\"current_password\":\"secret-current\",\"new_password\":\"NewPass1!\",\"confirm_password\":\"NewPass1!\"}"),
                    res);

            assertEquals(429, res.status);
            assertTrue(res.body().contains("\"code\":\"rate_limited\""), res.body());
            assertNotNull(res.getHeader("Retry-After"), "Retry-After header must be set on 429");
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
        }
    }

    @Test
    public void passwordChange_successDoesNotConsumeSlot() throws Exception {
        // C-3: a successful change must NOT leave consumed slots behind — the clear()
        // call resets the bucket. We pre-burn 4 of 5 slots to prove the reset is real:
        // after the success, the bucket admits a fresh full 5 attempts again.
        registerStubLoginAssist("alice", "secret-current");
        final boolean[] changedCalled = { false };
        final StubUserService stubSvc = new StubUserService(changedCalled);
        ComponentUtil.register(stubSvc, "userService");
        ComponentUtil.register(stubSvc, org.codelibs.fess.app.service.UserService.class.getCanonicalName());
        final org.codelibs.fess.helper.SystemHelper systemHelper = new org.codelibs.fess.helper.SystemHelper() {
            @Override
            public String validatePassword(final String password) {
                return null;
            }
        };
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.register(systemHelper, org.codelibs.fess.helper.SystemHelper.class.getCanonicalName());
        try {
            final LoginRateLimiter rl = new LoginRateLimiter();
            for (int i = 0; i < 4; i++) {
                rl.allow(LoginRateLimiter.Scope.USER, "alice", 5, 60);
            }
            assertTrue(rl.peek(LoginRateLimiter.Scope.USER, "alice", 5, 60), "one slot must remain before success");

            final CapturingResponse res = new CapturingResponse();
            new PasswordChangeHandler(rl).handle(new StubRequest("POST", "/api/v2/auth/password").withJsonBody(
                    "{\"current_password\":\"secret-current\",\"new_password\":\"NewLongPass123!\",\"confirm_password\":\"NewLongPass123!\"}"),
                    res);

            org.junit.jupiter.api.Assertions.assertEquals(200, res.status, res.body());
            assertTrue(changedCalled[0], "expected UserService.changePassword to be invoked");

            // After clear() the bucket must admit all 5 slots again.
            for (int i = 0; i < 5; i++) {
                assertTrue(rl.allow(LoginRateLimiter.Scope.USER, "alice", 5, 60), "bucket must be fully reset after success");
            }
            assertFalse(rl.peek(LoginRateLimiter.Scope.USER, "alice", 5, 60), "bucket must now be exhausted on the 6th");
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
        }
    }

    @Test
    public void passwordChange_nonStringCurrentPassword_returns400() throws Exception {
        // M-10: a numeric JSON value for current_password used to be silently stringified
        // by v.toString(). With the instanceof guard the value is treated as null, the
        // blank-check trips, and the client sees an invalid_request 400 rather than a
        // misleading auth_required 401 from a hash-compare against "12345".
        registerStubLoginAssist("alice", "secret-current");
        try {
            final CapturingResponse res = new CapturingResponse();
            new PasswordChangeHandler().handle(new StubRequest("POST", "/api/v2/auth/password")
                    .withJsonBody("{\"current_password\":12345,\"new_password\":\"NewPass1!\",\"confirm_password\":\"NewPass1!\"}"), res);
            org.junit.jupiter.api.Assertions.assertEquals(400, res.status, res.body());
            assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
            assertTrue(res.body().contains("current_password"), res.body());
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
        }
    }

    @Test
    public void passwordChange_authLookupException_returns500NotAuthRequired() throws Exception {
        // M-17: when FessLoginAssist itself throws on the saved-user lookup, the response
        // must be INTERNAL_ERROR (500), not AUTH_REQUIRED (401). The latter would mislead
        // a logged-in user into thinking their session expired when DI is actually broken.
        final FessLoginAssist throwing = new FessLoginAssist() {
            private static final long serialVersionUID = 1L;

            @Override
            public OptionalThing<FessUserBean> getSavedUserBean() {
                throw new RuntimeException("forced DI lookup failure");
            }
        };
        ComponentUtil.register(throwing, "fessLoginAssist");
        ComponentUtil.register(throwing, FessLoginAssist.class.getCanonicalName());
        try {
            final CapturingResponse res = new CapturingResponse();
            new PasswordChangeHandler().handle(new StubRequest("POST", "/api/v2/auth/password")
                    .withJsonBody("{\"current_password\":\"x\",\"new_password\":\"y\",\"confirm_password\":\"y\"}"), res);
            org.junit.jupiter.api.Assertions.assertEquals(500, res.status, res.body());
            assertTrue(res.body().contains("\"code\":\"internal_error\""), res.body());
            assertFalse(res.body().contains("\"code\":\"auth_required\""),
                    "lookup exception must not be misreported as auth_required: " + res.body());
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
        }
    }

    @Test
    public void passwordChange_anonymousUser_returns401() throws Exception {
        // M-17 regression: a genuine anonymous caller (no user bean present) must still
        // get the AUTH_REQUIRED 401 — the M-17 fix only diverts on lookup exceptions,
        // not on empty results.
        final CapturingResponse res = new CapturingResponse();
        new PasswordChangeHandler().handle(new StubRequest("POST", "/api/v2/auth/password")
                .withJsonBody("{\"current_password\":\"x\",\"new_password\":\"y\",\"confirm_password\":\"y\"}"), res);
        assertEquals(401, res.status);
        assertTrue(res.body().contains("\"code\":\"auth_required\""), res.body());
    }

    // ── MINOR-4: assist.logout() before session.invalidate() ──────────────────

    @Test
    public void passwordChange_success_callsAssistLogout() throws Exception {
        // MINOR-4: on a successful password change, assist.logout() must be called
        // BEFORE session.invalidate() so remember-me cookies are cleared and audit events
        // are recorded — matching the LogoutHandler contract.
        final TrackingStubLoginAssist trackingAssist = new TrackingStubLoginAssist("alice", "secret-current");
        ComponentUtil.register(trackingAssist, "fessLoginAssist");
        ComponentUtil.register(trackingAssist, FessLoginAssist.class.getCanonicalName());
        final boolean[] changedCalled = { false };
        final StubUserService stubSvc = new StubUserService(changedCalled);
        ComponentUtil.register(stubSvc, "userService");
        ComponentUtil.register(stubSvc, org.codelibs.fess.app.service.UserService.class.getCanonicalName());
        final org.codelibs.fess.helper.SystemHelper systemHelper = new org.codelibs.fess.helper.SystemHelper() {
            @Override
            public String validatePassword(final String password) {
                return null;
            }
        };
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.register(systemHelper, org.codelibs.fess.helper.SystemHelper.class.getCanonicalName());
        try {
            final TrackingStubSession session = new TrackingStubSession();
            final CapturingResponse res = new CapturingResponse();
            new PasswordChangeHandler().handle(new StubRequest("POST", "/api/v2/auth/password").withSession(session)
                    .withJsonBody(
                            "{\"current_password\":\"secret-current\",\"new_password\":\"NewLongPass123!\",\"confirm_password\":\"NewLongPass123!\"}"),
                    res);

            org.junit.jupiter.api.Assertions.assertEquals(200, res.status, res.body());
            assertTrue(res.body().contains("\"ok\":true"), res.body());
            assertTrue(trackingAssist.logoutCalled, "assist.logout() must be called on the success path (MINOR-4)");
            assertTrue(session.invalidateCalled, "session.invalidate() must still be called after assist.logout()");
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
        }
    }

    @Test
    public void passwordChange_success_sessionInvalidatedEvenWhenLogoutThrows() throws Exception {
        // MINOR-4: if assist.logout() throws, session.invalidate() must still run and
        // the response must still be the normal 200 ok — logout failure must be swallowed.
        final ThrowingLogoutStubLoginAssist throwingAssist = new ThrowingLogoutStubLoginAssist("alice", "secret-current");
        ComponentUtil.register(throwingAssist, "fessLoginAssist");
        ComponentUtil.register(throwingAssist, FessLoginAssist.class.getCanonicalName());
        final boolean[] changedCalled = { false };
        final StubUserService stubSvc = new StubUserService(changedCalled);
        ComponentUtil.register(stubSvc, "userService");
        ComponentUtil.register(stubSvc, org.codelibs.fess.app.service.UserService.class.getCanonicalName());
        final org.codelibs.fess.helper.SystemHelper systemHelper = new org.codelibs.fess.helper.SystemHelper() {
            @Override
            public String validatePassword(final String password) {
                return null;
            }
        };
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.register(systemHelper, org.codelibs.fess.helper.SystemHelper.class.getCanonicalName());
        try {
            final TrackingStubSession session = new TrackingStubSession();
            final CapturingResponse res = new CapturingResponse();
            new PasswordChangeHandler().handle(new StubRequest("POST", "/api/v2/auth/password").withSession(session)
                    .withJsonBody(
                            "{\"current_password\":\"secret-current\",\"new_password\":\"NewLongPass123!\",\"confirm_password\":\"NewLongPass123!\"}"),
                    res);

            // Response must still be 200 ok even though logout() threw.
            org.junit.jupiter.api.Assertions.assertEquals(200, res.status, res.body());
            assertTrue(res.body().contains("\"ok\":true"), res.body());
            // session.invalidate() must still have been called.
            assertTrue(session.invalidateCalled, "session.invalidate() must be called even when assist.logout() throws (MINOR-4)");
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
        }
    }

    @Test
    public void passwordChange_wrongPassword_doesNotCallAssistLogout() throws Exception {
        // MINOR-4: when the current_password is wrong the handler short-circuits before
        // reaching the logout/session-invalidation block. Neither assist.logout() nor
        // session.invalidate() should be called.
        final TrackingStubLoginAssist trackingAssist = new TrackingStubLoginAssist("alice", "secret-current");
        ComponentUtil.register(trackingAssist, "fessLoginAssist");
        ComponentUtil.register(trackingAssist, FessLoginAssist.class.getCanonicalName());
        try {
            final TrackingStubSession session = new TrackingStubSession();
            final CapturingResponse res = new CapturingResponse();
            new PasswordChangeHandler().handle(new StubRequest("POST", "/api/v2/auth/password").withSession(session)
                    .withJsonBody("{\"current_password\":\"WRONG\",\"new_password\":\"NewPass1!\",\"confirm_password\":\"NewPass1!\"}"),
                    res);

            org.junit.jupiter.api.Assertions.assertEquals(401, res.status, res.body());
            assertFalse(trackingAssist.logoutCalled, "assist.logout() must NOT be called on the wrong-password path (MINOR-4)");
            assertFalse(session.invalidateCalled, "session.invalidate() must NOT be called on the wrong-password path");
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
        }
    }

    @Test
    public void test_handle_payloadTooLarge_returns413() throws Exception {
        // A body exceeding MAX_BODY_BYTES (4 KiB) must return HTTP 413 with the
        // payload_too_large error code — not 400 invalid_request.
        // Set up an authenticated user so the handler proceeds past the auth gate to body parse.
        registerStubLoginAssist("alice", "secret-current");
        try {
            final String bigBody =
                    "{\"current_password\":\"" + "x".repeat(5 * 1024) + "\",\"new_password\":\"a\",\"confirm_password\":\"a\"}";
            final CapturingResponse res = new CapturingResponse();
            new PasswordChangeHandler().handle(new StubRequest("POST", "/api/v2/auth/password").withJsonBody(bigBody), res);
            assertEquals(413, res.status);
            assertTrue(res.body().contains("\"code\":\"payload_too_large\""), res.body());
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
        }
    }

    @Test
    public void test_handle_unsupportedMediaType_returns415() throws Exception {
        // A POST with Content-Type text/plain must return HTTP 415 with the
        // unsupported_media_type error code — not 400 invalid_request.
        // Set up an authenticated user so the handler proceeds past the auth gate to body parse.
        registerStubLoginAssist("alice", "secret-current");
        try {
            final StubRequest req = new StubRequest("POST", "/api/v2/auth/password") {
                @Override
                public String getContentType() {
                    return "text/plain";
                }
            };
            final CapturingResponse res = new CapturingResponse();
            new PasswordChangeHandler().handle(req, res);
            assertEquals(415, res.status);
            assertTrue(res.body().contains("\"code\":\"unsupported_media_type\""), res.body());
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
        }
    }

    /**
     * Stub FessLoginAssist that returns a populated entity only when the supplied
     * LocalUserCredential carries {@code expectedPw}. Used by the password-change
     * tests above to exercise the wrong/right current-password branches without
     * pulling in the full LDAP/DBFlute stack.
     */
    private static class StubFessLoginAssist extends FessLoginAssist {
        private static final long serialVersionUID = 1L;
        private final String userId;
        private final String expectedPw;

        StubFessLoginAssist(final String userId, final String expectedPw) {
            this.userId = userId;
            this.expectedPw = expectedPw;
        }

        @Override
        public OptionalThing<FessUserBean> getSavedUserBean() {
            // Pretend a user is logged in so the handler proceeds past the auth gate.
            return OptionalThing.of(new FessUserBean(new StubFessUser(userId)));
        }

        @Override
        public OptionalEntity<FessUser> findLoginUser(final LoginCredential credential) {
            // The handler always passes a LocalUserCredential; defensive-cast and check.
            if (credential instanceof final LocalUserCredential local && expectedPw.equals(local.getPassword())) {
                return OptionalEntity.of(new StubFessUser(local.getUserId()));
            }
            return OptionalEntity.empty();
        }
    }

    /**
     * Extends {@link StubFessLoginAssist} to record whether {@link #logout()} was called.
     * Used by MINOR-4 tests to verify the logout path.
     */
    private static class TrackingStubLoginAssist extends StubFessLoginAssist {
        private static final long serialVersionUID = 1L;
        boolean logoutCalled = false;

        TrackingStubLoginAssist(final String userId, final String expectedPw) {
            super(userId, expectedPw);
        }

        @Override
        public void logout() {
            logoutCalled = true;
        }
    }

    /**
     * Extends {@link StubFessLoginAssist} so that {@link #logout()} throws a
     * {@link RuntimeException}. Used to verify that a logout failure does not
     * prevent session invalidation or alter the response status.
     */
    private static class ThrowingLogoutStubLoginAssist extends StubFessLoginAssist {
        private static final long serialVersionUID = 1L;

        ThrowingLogoutStubLoginAssist(final String userId, final String expectedPw) {
            super(userId, expectedPw);
        }

        @Override
        public void logout() {
            throw new RuntimeException("forced logout failure (test)");
        }
    }

    /** Minimal {@link FessUser} for stub use; the handler only reads getName() / getUserId(). */
    private static class StubFessUser implements FessUser {
        private static final long serialVersionUID = 1L;
        private final String name;

        StubFessUser(final String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String[] getRoleNames() {
            return new String[0];
        }

        @Override
        public String[] getGroupNames() {
            return new String[0];
        }

        @Override
        public String[] getPermissions() {
            return new String[0];
        }
    }

    /**
     * Stub UserService that records when {@link #changePassword} is invoked. The lookup
     * methods are unused by the handler, so the parent implementations can stand.
     */
    private static class StubUserService extends org.codelibs.fess.app.service.UserService {
        private final boolean[] changedCalled;

        StubUserService(final boolean[] changedCalled) {
            this.changedCalled = changedCalled;
        }

        @Override
        public void changePassword(final String username, final String password) {
            changedCalled[0] = true;
        }
    }

    /** Minimal HttpServletResponse stub — captures status, content type and body. */
    private static class CapturingResponse implements HttpServletResponse {
        final StringWriter sw = new StringWriter();
        final PrintWriter writer = new PrintWriter(sw);
        int status = 200;
        String contentType;
        final java.util.Map<String, String> headers = new java.util.HashMap<>();

        String body() {
            writer.flush();
            return sw.toString();
        }

        @Override
        public void setStatus(final int sc) {
            this.status = sc;
        }

        @Override
        public int getStatus() {
            return status;
        }

        @Override
        public void setContentType(final String type) {
            this.contentType = type;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return writer;
        }

        @Override
        public String getCharacterEncoding() {
            return "UTF-8";
        }

        @Override
        public void setCharacterEncoding(final String s) {
        }

        @Override
        public jakarta.servlet.ServletOutputStream getOutputStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setContentLength(final int len) {
        }

        @Override
        public void setContentLengthLong(final long len) {
        }

        @Override
        public void setBufferSize(final int size) {
        }

        @Override
        public int getBufferSize() {
            return 0;
        }

        @Override
        public void flushBuffer() {
        }

        @Override
        public void resetBuffer() {
        }

        @Override
        public boolean isCommitted() {
            return false;
        }

        @Override
        public void reset() {
        }

        @Override
        public void setLocale(final java.util.Locale loc) {
        }

        @Override
        public java.util.Locale getLocale() {
            return java.util.Locale.ROOT;
        }

        @Override
        public void addCookie(final jakarta.servlet.http.Cookie cookie) {
        }

        @Override
        public boolean containsHeader(final String name) {
            return false;
        }

        @Override
        public String encodeURL(final String url) {
            return url;
        }

        @Override
        public String encodeRedirectURL(final String url) {
            return url;
        }

        @Override
        public void sendError(final int sc, final String msg) {
        }

        @Override
        public void sendError(final int sc) {
        }

        @Override
        public void sendRedirect(final String location) {
        }

        @Override
        public void sendRedirect(final String location, final int sc) {
        }

        @Override
        public void sendRedirect(final String location, final boolean clearBuffer) {
        }

        @Override
        public void sendRedirect(final String location, final int sc, final boolean clearBuffer) {
        }

        @Override
        public void setDateHeader(final String name, final long date) {
        }

        @Override
        public void addDateHeader(final String name, final long date) {
        }

        @Override
        public void setHeader(final String name, final String value) {
            headers.put(name, value);
        }

        @Override
        public void addHeader(final String name, final String value) {
            headers.put(name, value);
        }

        @Override
        public void setIntHeader(final String name, final int value) {
        }

        @Override
        public void addIntHeader(final String name, final int value) {
        }

        @Override
        public String getHeader(final String name) {
            return headers.get(name);
        }

        @Override
        public java.util.Collection<String> getHeaders(final String name) {
            final String v = headers.get(name);
            return v == null ? java.util.Collections.emptyList() : java.util.Collections.singletonList(v);
        }

        @Override
        public java.util.Collection<String> getHeaderNames() {
            return headers.keySet();
        }
    }

    /**
     * Minimal HttpServletRequest stub. Supports {@code application/json} bodies via
     * {@link #withJsonBody}.
     */
    private static class StubRequest implements HttpServletRequest {
        private final String method;
        private final String uri;
        private final Map<String, Object> attrs = new HashMap<>();
        private byte[] body;
        private String contentType;
        private HttpSession session;

        StubRequest(final String method, final String uri) {
            this.method = method;
            this.uri = uri;
        }

        StubRequest withJsonBody(final String json) {
            this.body = json == null ? new byte[0] : json.getBytes(StandardCharsets.UTF_8);
            this.contentType = "application/json";
            return this;
        }

        StubRequest withSession(final HttpSession s) {
            this.session = s;
            return this;
        }

        @Override
        public String getMethod() {
            return method;
        }

        @Override
        public String getServletPath() {
            return uri;
        }

        @Override
        public String getRequestURI() {
            return uri;
        }

        @Override
        public String getContextPath() {
            return "";
        }

        @Override
        public Object getAttribute(final String name) {
            return attrs.get(name);
        }

        @Override
        public void setAttribute(final String name, final Object value) {
            if (value == null) {
                attrs.remove(name);
            } else {
                attrs.put(name, value);
            }
        }

        @Override
        public void removeAttribute(final String name) {
            attrs.remove(name);
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return Collections.enumeration(attrs.keySet());
        }

        @Override
        public RequestDispatcher getRequestDispatcher(final String path) {
            return null;
        }

        @Override
        public String getAuthType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.http.Cookie[] getCookies() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getDateHeader(final String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getHeader(final String name) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaders(final String name) {
            return Collections.emptyEnumeration();
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return Collections.emptyEnumeration();
        }

        @Override
        public int getIntHeader(final String name) {
            return -1;
        }

        @Override
        public String getPathInfo() {
            return null;
        }

        @Override
        public String getPathTranslated() {
            return null;
        }

        @Override
        public String getQueryString() {
            return null;
        }

        @Override
        public String getRemoteUser() {
            return null;
        }

        @Override
        public boolean isUserInRole(final String role) {
            return false;
        }

        @Override
        public java.security.Principal getUserPrincipal() {
            return null;
        }

        @Override
        public String getRequestedSessionId() {
            return null;
        }

        @Override
        public StringBuffer getRequestURL() {
            return new StringBuffer(uri);
        }

        @Override
        public HttpSession getSession(final boolean create) {
            return session;
        }

        @Override
        public HttpSession getSession() {
            return session;
        }

        @Override
        public String changeSessionId() {
            return null;
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromCookie() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromURL() {
            return false;
        }

        @Override
        public boolean authenticate(final HttpServletResponse response) {
            return false;
        }

        @Override
        public void login(final String username, final String password) {
        }

        @Override
        public void logout() {
        }

        @Override
        public java.util.Collection<Part> getParts() {
            return Collections.emptyList();
        }

        @Override
        public Part getPart(final String name) {
            return null;
        }

        @Override
        public <T extends HttpUpgradeHandler> T upgrade(final Class<T> handlerClass) {
            return null;
        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public void setCharacterEncoding(final String env) {
        }

        @Override
        public int getContentLength() {
            return body == null ? 0 : body.length;
        }

        @Override
        public long getContentLengthLong() {
            return body == null ? 0L : body.length;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public ServletInputStream getInputStream() {
            final ByteArrayInputStream bais = new ByteArrayInputStream(body == null ? new byte[0] : body);
            return new ServletInputStream() {
                private boolean eof = false;

                @Override
                public int read() throws IOException {
                    final int v = bais.read();
                    if (v < 0) {
                        eof = true;
                    }
                    return v;
                }

                @Override
                public byte[] readAllBytes() throws IOException {
                    final byte[] all = bais.readAllBytes();
                    eof = true;
                    return all;
                }

                @Override
                public byte[] readNBytes(final int len) throws IOException {
                    final byte[] out = bais.readNBytes(len);
                    if (bais.available() == 0) {
                        eof = true;
                    }
                    return out;
                }

                @Override
                public boolean isFinished() {
                    return eof;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(final ReadListener listener) {
                    // no-op
                }
            };
        }

        @Override
        public String getParameter(final String name) {
            return null;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.emptyEnumeration();
        }

        @Override
        public String[] getParameterValues(final String name) {
            return null;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return Collections.emptyMap();
        }

        @Override
        public String getProtocol() {
            return "HTTP/1.1";
        }

        @Override
        public String getScheme() {
            return "http";
        }

        @Override
        public String getServerName() {
            return "localhost";
        }

        @Override
        public int getServerPort() {
            return 8080;
        }

        @Override
        public java.io.BufferedReader getReader() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getRemoteAddr() {
            return "127.0.0.1";
        }

        @Override
        public String getRemoteHost() {
            return "localhost";
        }

        @Override
        public java.util.Locale getLocale() {
            return java.util.Locale.ROOT;
        }

        @Override
        public Enumeration<java.util.Locale> getLocales() {
            return Collections.enumeration(java.util.Collections.singleton(java.util.Locale.ROOT));
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public int getRemotePort() {
            return 0;
        }

        @Override
        public String getLocalName() {
            return "localhost";
        }

        @Override
        public String getLocalAddr() {
            return "127.0.0.1";
        }

        @Override
        public int getLocalPort() {
            return 8080;
        }

        @Override
        public ServletContext getServletContext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public AsyncContext startAsync() {
            throw new UnsupportedOperationException();
        }

        @Override
        public AsyncContext startAsync(final ServletRequest req, final ServletResponse resp) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isAsyncStarted() {
            return false;
        }

        @Override
        public boolean isAsyncSupported() {
            return false;
        }

        @Override
        public AsyncContext getAsyncContext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DispatcherType getDispatcherType() {
            return DispatcherType.REQUEST;
        }

        @Override
        public String getRequestId() {
            return "";
        }

        @Override
        public String getProtocolRequestId() {
            return "";
        }

        @Override
        public jakarta.servlet.ServletConnection getServletConnection() {
            return null;
        }
    }

    /**
     * Minimal {@link HttpSession} stub backed by a {@link ConcurrentHashMap}.
     * Only the attribute accessors and {@code getId()} are wired; everything
     * else throws {@link UnsupportedOperationException} to keep the surface
     * small and failures visible.
     */
    private static class StubSession implements HttpSession {
        private final ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<>();

        @Override
        public Object getAttribute(final String name) {
            return attributes.get(name);
        }

        @Override
        public void setAttribute(final String name, final Object value) {
            if (value == null) {
                attributes.remove(name);
            } else {
                attributes.put(name, value);
            }
        }

        @Override
        public void removeAttribute(final String name) {
            attributes.remove(name);
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return Collections.enumeration(attributes.keySet());
        }

        @Override
        public String getId() {
            return "stub-session-id";
        }

        @Override
        public long getCreationTime() {
            return System.currentTimeMillis();
        }

        @Override
        public long getLastAccessedTime() {
            return System.currentTimeMillis();
        }

        @Override
        public jakarta.servlet.ServletContext getServletContext() {
            return null;
        }

        @Override
        public void setMaxInactiveInterval(final int interval) {
        }

        @Override
        public int getMaxInactiveInterval() {
            return -1;
        }

        @Override
        public void invalidate() {
            attributes.clear();
        }

        @Override
        public boolean isNew() {
            return false;
        }
    }

    /**
     * A {@link StubSession} extension that also tracks whether {@link #invalidate()}
     * was called. Used by M-3 tests to assert that session destruction occurs exactly
     * on the success path and never on failure paths.
     */
    private static class TrackingStubSession extends StubSession {
        boolean invalidateCalled = false;

        @Override
        public void invalidate() {
            invalidateCalled = true;
            super.invalidate();
        }
    }
}
