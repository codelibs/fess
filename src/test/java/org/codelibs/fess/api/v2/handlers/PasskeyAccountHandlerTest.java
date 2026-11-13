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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.app.service.PasskeyException;
import org.codelibs.fess.app.service.PasskeyService;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.app.web.base.login.LocalUserCredential;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.ldap.LdapUser;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.user.exentity.Passkey;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.login.credential.LoginCredential;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Unit tests for {@link PasskeyAccountHandler}. The registration check itself is
 * {@code PasskeyServiceTest}'s; here the service is a stub and the handler's own contract is pinned:
 * the feature gate, the login and local-user gates, the password re-authentication and its USER
 * bucket, and the wire shape of the list.
 */
public class PasskeyAccountHandlerTest extends UnitFessTestCase {

    private static final String PASSWORD = "s3cret";

    private static final String OPTIONS_BODY = "{\"current_password\":\"" + PASSWORD + "\"}";

    private static final String VERIFY_BODY = "{\"label\":\"Work laptop\",\"credential\":{\"id\":\"abc\",\"type\":\"public-key\"}}";

    private static final String CREDENTIAL_ID = "AAECAwQFBgcICQoLDA0ODw";

    // ---------------------------------------------------------------- feature gate

    @Test
    public void test_registerOptions_disabled_returns404() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(false);
        final StubLoginAssist assist = register(new StubLoginAssist(localUser("alice")));
        final Response res = new Response();
        handler(new LoginRateLimiter(), service).registerOptions(new Request("POST").withSession().withBody(OPTIONS_BODY).proxy(),
                res.proxy());
        assertNotFound(res, "/auth/passkey/register/options");
        assertEquals(0, assist.findLoginUserCalls);
        assertEquals(0, service.optionsCalls);
    }

    @Test
    public void test_registerVerify_disabled_returns404() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(false);
        register(new StubLoginAssist(localUser("alice")));
        final Response res = new Response();
        handler(new LoginRateLimiter(), service).registerVerify(new Request("POST").withSession().withBody(VERIFY_BODY).proxy(),
                res.proxy());
        assertNotFound(res, "/auth/passkey/register/verify");
        assertEquals(0, service.verifyCalls);
    }

    @Test
    public void test_list_disabled_returns404() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(false);
        register(new StubLoginAssist(localUser("alice")));
        final Response res = new Response();
        handler(new LoginRateLimiter(), service).list(new Request("GET").withSession().proxy(), res.proxy());
        assertNotFound(res, "/auth/passkey");
        assertEquals(0, service.listCalls);
    }

    @Test
    public void test_delete_disabled_returns404() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(false);
        register(new StubLoginAssist(localUser("alice")));
        final Response res = new Response();
        handler(new LoginRateLimiter(), service).delete(new Request("DELETE").withSession().proxy(), res.proxy(), CREDENTIAL_ID);
        assertNotFound(res, "/auth/passkey/" + CREDENTIAL_ID);
        assertEquals(0, service.deleteCalls);
    }

    // ---------------------------------------------------------------- login gate

    @Test
    public void test_registerOptions_unauthenticated_returns401() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        final StubLoginAssist assist = register(new StubLoginAssist(null));
        final Response res = new Response();
        handler(new LoginRateLimiter(), service).registerOptions(new Request("POST").withSession().withBody(OPTIONS_BODY).proxy(),
                res.proxy());
        assertAuthRequired(res);
        assertEquals(0, assist.findLoginUserCalls);
        assertEquals(0, service.optionsCalls);
    }

    @Test
    public void test_registerVerify_unauthenticated_returns401() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        register(new StubLoginAssist(null));
        final Response res = new Response();
        handler(new LoginRateLimiter(), service).registerVerify(new Request("POST").withSession().withBody(VERIFY_BODY).proxy(),
                res.proxy());
        assertAuthRequired(res);
        assertEquals(0, service.verifyCalls);
    }

    @Test
    public void test_list_unauthenticated_returns401() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        register(new StubLoginAssist(null));
        final Response res = new Response();
        handler(new LoginRateLimiter(), service).list(new Request("GET").withSession().proxy(), res.proxy());
        assertAuthRequired(res);
        assertEquals(0, service.listCalls);
    }

    @Test
    public void test_delete_unauthenticated_returns401() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        register(new StubLoginAssist(null));
        final Response res = new Response();
        handler(new LoginRateLimiter(), service).delete(new Request("DELETE").withSession().proxy(), res.proxy(), CREDENTIAL_ID);
        assertAuthRequired(res);
        assertEquals(0, service.deleteCalls);
    }

    @Test
    public void test_rejectsOtherMethods() throws Exception {
        register(new StubLoginAssist(localUser("alice")));
        final PasskeyAccountHandler handler = handler(new LoginRateLimiter(), new StubPasskeyService(true));
        final Response options = new Response();
        handler.registerOptions(new Request("GET").withSession().proxy(), options.proxy());
        assertEquals(405, options.status);
        assertEquals("POST", options.headers.get("Allow"));
        final Response verify = new Response();
        handler.registerVerify(new Request("GET").withSession().proxy(), verify.proxy());
        assertEquals(405, verify.status);
        final Response list = new Response();
        handler.list(new Request("POST").withSession().proxy(), list.proxy());
        assertEquals(405, list.status);
        assertEquals("GET", list.headers.get("Allow"));
        final Response delete = new Response();
        handler.delete(new Request("GET").withSession().proxy(), delete.proxy(), CREDENTIAL_ID);
        assertEquals(405, delete.status);
        assertEquals("DELETE", delete.headers.get("Allow"));
    }

    // ---------------------------------------------------------------- register/options

    @Test
    public void test_registerOptions_correctPassword_returnsOptions() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        final StubLoginAssist assist = register(new StubLoginAssist(localUser("alice")));
        final LoginRateLimiter rl = new LoginRateLimiter();
        final int userLimit = ComponentUtil.getFessConfig().getThemeApiLoginRateLimitPerUserPerMinuteAsInteger();
        // an earlier wrong password is forgiven by the success
        rl.allow(LoginRateLimiter.Scope.USER, "alice", userLimit, 60);
        final Response res = new Response();
        handler(rl, service).registerOptions(new Request("POST").withSession().withBody(OPTIONS_BODY).proxy(), res.proxy());
        assertEquals(200, res.status, res.body());
        assertTrue(res.body().contains("\"challenge\":\"c\""), res.body());
        assertEquals(1, service.optionsCalls);
        assertEquals("alice", service.lastUser.getName());
        assertTrue(assist.lastCredential instanceof LocalUserCredential);
        assertEquals("alice", ((LocalUserCredential) assist.lastCredential).getUser());
        assertEquals(PASSWORD, ((LocalUserCredential) assist.lastCredential).getPassword());
        assertTrue(rl.peek(LoginRateLimiter.Scope.USER, "alice", 1, 60), "a success clears the user bucket");
    }

    @Test
    public void test_registerOptions_wrongPassword_returns401AndCountsUserSlot() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        register(new StubLoginAssist(localUser("alice")));
        final LoginRateLimiter rl = new LoginRateLimiter();
        final Response res = new Response();
        handler(rl, service).registerOptions(new Request("POST").withSession().withBody("{\"current_password\":\"wrong\"}").proxy(),
                res.proxy());
        assertEquals(401, res.status, res.body());
        assertTrue(res.body().contains("\"code\":\"auth_required\""), res.body());
        assertTrue(res.body().contains("\"reason\":\"invalid_current_password\""), res.body());
        assertEquals(0, service.optionsCalls, "a wrong password creates no challenge");
        assertFalse(rl.peek(LoginRateLimiter.Scope.USER, "alice", 1, 60), "the failure took a user slot");
        assertTrue(rl.peek(LoginRateLimiter.Scope.USER, "alice", 2, 60), "exactly one slot");
    }

    @Test
    public void test_registerOptions_userRateLimited_returns429() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        final StubLoginAssist assist = register(new StubLoginAssist(localUser("alice")));
        final LoginRateLimiter rl = new LoginRateLimiter();
        final PasskeyAccountHandler handler = handler(rl, service);
        final int userLimit = ComponentUtil.getFessConfig().getThemeApiLoginRateLimitPerUserPerMinuteAsInteger();
        for (int i = 0; i < userLimit; i++) {
            final Response res = new Response();
            handler.registerOptions(new Request("POST").withSession().withBody("{\"current_password\":\"wrong\"}").proxy(), res.proxy());
            assertEquals(401, res.status, "attempt " + i);
        }
        final int calls = assist.findLoginUserCalls;
        assertEquals(userLimit, calls);
        // the bucket is spent: the next attempt is refused before its password is checked
        final Response exhausted = new Response();
        handler.registerOptions(new Request("POST").withSession().withBody("{\"current_password\":\"wrong\"}").proxy(), exhausted.proxy());
        assertEquals(429, exhausted.status, exhausted.body());
        // and even the right password is refused during the lockout, without being checked
        final Response refused = new Response();
        handler.registerOptions(new Request("POST").withSession().withBody(OPTIONS_BODY).proxy(), refused.proxy());
        assertEquals(429, refused.status, refused.body());
        assertTrue(refused.body().contains("\"code\":\"rate_limited\""), refused.body());
        assertNotNull(refused.headers.get("Retry-After"));
        assertEquals(calls, assist.findLoginUserCalls);
        assertEquals(0, service.optionsCalls);
    }

    @Test
    public void test_registerOptions_ldapUserSession_rejected() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        // a directory user whose name is also a local user's
        final StubLoginAssist assist = register(new StubLoginAssist(ldapUser("alice")));
        assist.verifiedUser = localUser("alice");
        final LoginRateLimiter rl = new LoginRateLimiter();
        final Request req = new Request("POST").withSession().withBody(OPTIONS_BODY);
        final Response res = new Response();
        handler(rl, service).registerOptions(req.proxy(), res.proxy());
        assertAuthRequired(res);
        assertEquals(0, assist.findLoginUserCalls, "no password oracle for a non-local session");
        assertEquals(0, service.optionsCalls);
        assertTrue(req.sessionAttributes.isEmpty(), "no challenge");
        assertTrue(rl.peek(LoginRateLimiter.Scope.USER, "alice", 1, 60));
    }

    @Test
    public void test_registerOptions_otherFessUserSession_rejected() throws Exception {
        // an SSO plugin's own user type is not a local user either
        final StubPasskeyService service = new StubPasskeyService(true);
        final StubLoginAssist assist = register(new StubLoginAssist(plainFessUser("alice")));
        final Response res = new Response();
        handler(new LoginRateLimiter(), service).registerOptions(new Request("POST").withSession().withBody(OPTIONS_BODY).proxy(),
                res.proxy());
        assertAuthRequired(res);
        assertEquals(0, assist.findLoginUserCalls);
        assertEquals(0, service.optionsCalls);
    }

    @Test
    public void test_registerOptions_passwordVerifiedByDirectory_rejected() throws Exception {
        // a local session whose password the directory accepted: not a local password check
        final StubPasskeyService service = new StubPasskeyService(true);
        final StubLoginAssist assist = register(new StubLoginAssist(localUser("alice")));
        assist.verifiedUser = ldapUser("alice");
        final LoginRateLimiter rl = new LoginRateLimiter();
        final Response res = new Response();
        handler(rl, service).registerOptions(new Request("POST").withSession().withBody(OPTIONS_BODY).proxy(), res.proxy());
        assertEquals(401, res.status, res.body());
        assertTrue(res.body().contains("\"reason\":\"invalid_current_password\""), res.body());
        assertEquals(0, service.optionsCalls);
        assertFalse(rl.peek(LoginRateLimiter.Scope.USER, "alice", 1, 60));
    }

    @Test
    public void test_registerOptions_passwordOfAnotherLocalUser_rejected() throws Exception {
        // findLoginUser answers a local user, but not the one of the session: not this user's password
        final StubPasskeyService service = new StubPasskeyService(true);
        final StubLoginAssist assist = register(new StubLoginAssist(localUser("alice")));
        assist.verifiedUser = localUser("mallory");
        final LoginRateLimiter rl = new LoginRateLimiter();
        final Response res = new Response();
        handler(rl, service).registerOptions(new Request("POST").withSession().withBody(OPTIONS_BODY).proxy(), res.proxy());
        assertEquals(401, res.status, res.body());
        assertTrue(res.body().contains("\"reason\":\"invalid_current_password\""), res.body());
        assertEquals(0, service.optionsCalls);
        assertFalse(rl.peek(LoginRateLimiter.Scope.USER, "alice", 1, 60), "the refusal took a user slot");
    }

    @Test
    public void test_registerOptions_atThePasskeyLimit_returns400() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        service.optionsFailure = new PasskeyException("reason=too_many_passkeys, count=20");
        register(new StubLoginAssist(localUser("alice")));
        final Response res = new Response();
        handler(new LoginRateLimiter(), service).registerOptions(new Request("POST").withSession().withBody(OPTIONS_BODY).proxy(),
                res.proxy());
        assertEquals(400, res.status, res.body());
        assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
        assertTrue(res.body().contains("\"reason\":\"too_many_passkeys\""), res.body());
        assertTrue(res.body().contains("\"max_passkeys\":20"), res.body());
    }

    @Test
    public void test_registerOptions_otherPasskeyException_isInternalError() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        service.optionsFailure = new PasskeyException("reason=something_else");
        register(new StubLoginAssist(localUser("alice")));
        final Response res = new Response();
        handler(new LoginRateLimiter(), service).registerOptions(new Request("POST").withSession().withBody(OPTIONS_BODY).proxy(),
                res.proxy());
        assertEquals(500, res.status, res.body());
        assertFalse(res.body().contains("something_else"), res.body());
    }

    @Test
    public void test_registerOptions_unsetRateLimitSettings_doNotBreakTheRegistration() throws Exception {
        final FessConfig original = ComponentUtil.getFessConfig();
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public int getPasswordMaxLengthOrDefault() {
                return original.getPasswordMaxLengthOrDefault();
            }

            @Override
            public Integer getThemeApiLoginRateLimitPerUserPerMinuteAsInteger() {
                return null;
            }

            @Override
            public Integer getThemeApiLoginLockoutSecondsAsInteger() {
                return null;
            }
        });
        try {
            final StubPasskeyService service = new StubPasskeyService(true);
            register(new StubLoginAssist(localUser("alice")));
            final Response res = new Response();
            handler(new LoginRateLimiter(), service).registerOptions(new Request("POST").withSession().withBody(OPTIONS_BODY).proxy(),
                    res.proxy());
            assertEquals(200, res.status, res.body());
            assertEquals(1, service.optionsCalls);
        } finally {
            ComponentUtil.setFessConfig(original);
        }
    }

    @Test
    public void test_registerOptions_blankPassword_returns400() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        final StubLoginAssist assist = register(new StubLoginAssist(localUser("alice")));
        for (final String json : new String[] { "{}", "{\"current_password\":\"\"}", "{\"current_password\":123}" }) {
            final Response res = new Response();
            handler(new LoginRateLimiter(), service).registerOptions(new Request("POST").withSession().withBody(json).proxy(), res.proxy());
            assertEquals(400, res.status, json);
            assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
        }
        assertEquals(0, assist.findLoginUserCalls);
    }

    @Test
    public void test_registerOptions_serviceFailure_isInternalError() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        service.optionsFailure = new IllegalStateException("search engine at 10.0.0.1 is down");
        register(new StubLoginAssist(localUser("alice")));
        final Response res = new Response();
        handler(new LoginRateLimiter(), service).registerOptions(new Request("POST").withSession().withBody(OPTIONS_BODY).proxy(),
                res.proxy());
        assertEquals(500, res.status, res.body());
        assertFalse(res.body().contains("10.0.0.1"), res.body());
    }

    // ---------------------------------------------------------------- register/verify

    @Test
    public void test_registerVerify_storesCredentialForSessionUser() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        register(new StubLoginAssist(localUser("alice")));
        final Response res = new Response();
        handler(new LoginRateLimiter(), service).registerVerify(new Request("POST").withSession().withBody(VERIFY_BODY).proxy(),
                res.proxy());
        assertEquals(200, res.status, res.body());
        assertEquals(1, service.verifyCalls);
        assertEquals("alice", service.lastUser.getName());
        assertEquals("Work laptop", service.lastLabel);
        assertEquals("{\"id\":\"abc\",\"type\":\"public-key\"}", service.lastResponseJson);
        assertTrue(res.body().contains("\"id\":\"" + CREDENTIAL_ID + "\""), res.body());
        assertTrue(res.body().contains("\"label\":\"Work laptop\""), res.body());
        assertNoKeyMaterial(res.body());
    }

    @Test
    public void test_registerVerify_ldapUserSession_rejected() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        register(new StubLoginAssist(ldapUser("alice")));
        final Response res = new Response();
        handler(new LoginRateLimiter(), service).registerVerify(new Request("POST").withSession().withBody(VERIFY_BODY).proxy(),
                res.proxy());
        assertAuthRequired(res);
        assertEquals(0, service.verifyCalls);
    }

    @Test
    public void test_registerVerify_localUserGone_rejected() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        register(new StubLoginAssist(localUser("alice")));
        final PasskeyAccountHandler handler = new PasskeyAccountHandler(new LoginRateLimiter(), service) {
            @Override
            protected OptionalEntity<User> findLocalUser(final String userName) {
                return OptionalEntity.empty();
            }
        };
        final Response res = new Response();
        handler.registerVerify(new Request("POST").withSession().withBody(VERIFY_BODY).proxy(), res.proxy());
        assertAuthRequired(res);
        assertEquals(0, service.verifyCalls);
    }

    @Test
    public void test_registerVerify_invalidBody_returns400() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        register(new StubLoginAssist(localUser("alice")));
        for (final String json : new String[] { "{\"label\":\"x\"}", "{\"label\":\"x\",\"credential\":\"text\"}",
                "{\"credential\":{\"id\":\"abc\"}}", "{\"label\":\"  \",\"credential\":{\"id\":\"abc\"}}",
                "{\"label\":7,\"credential\":{\"id\":\"abc\"}}",
                "{\"label\":\"" + "x".repeat(65) + "\",\"credential\":{\"id\":\"abc\"}}" }) {
            final Response res = new Response();
            handler(new LoginRateLimiter(), service).registerVerify(new Request("POST").withSession().withBody(json).proxy(), res.proxy());
            assertEquals(400, res.status, json);
            assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
        }
        assertEquals(0, service.verifyCalls, "the challenge is not consumed by a malformed request");
    }

    @Test
    public void test_registerVerify_labelAtLimit_isAccepted() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        register(new StubLoginAssist(localUser("alice")));
        final Response res = new Response();
        final String label = "x".repeat(64);
        handler(new LoginRateLimiter(), service).registerVerify(
                new Request("POST").withSession().withBody("{\"label\":\" " + label + " \",\"credential\":{\"id\":\"abc\"}}").proxy(),
                res.proxy());
        assertEquals(200, res.status, res.body());
        assertEquals(label, service.lastLabel);
    }

    @Test
    public void test_registerVerify_labelCountsCodePoints() throws Exception {
        // 64 characters outside the BMP are 128 UTF-16 units, and still within the limit
        final StubPasskeyService service = new StubPasskeyService(true);
        register(new StubLoginAssist(localUser("alice")));
        final String label = "\uD83D\uDD11".repeat(64);
        final Response res = new Response();
        handler(new LoginRateLimiter(), service).registerVerify(
                new Request("POST").withSession().withBody("{\"label\":\"" + label + "\",\"credential\":{\"id\":\"abc\"}}").proxy(),
                res.proxy());
        assertEquals(200, res.status, res.body());
        assertEquals(label, service.lastLabel);
    }

    @Test
    public void test_registerVerify_controlOrBidiCharacterInLabel_returns400() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        register(new StubLoginAssist(localUser("alice")));
        for (final String label : new String[] { "a\\u0000b", "a\\u0007b", "a\\nb", "invoice\\u202Etxt.exe", "a\\u2066b", "a\\u200Fb" }) {
            final Response res = new Response();
            handler(new LoginRateLimiter(), service).registerVerify(
                    new Request("POST").withSession().withBody("{\"label\":\"" + label + "\",\"credential\":{\"id\":\"abc\"}}").proxy(),
                    res.proxy());
            assertEquals(400, res.status, label);
            assertTrue(res.body().contains("\"reason\":\"label_invalid_characters\""), res.body());
        }
        assertEquals(0, service.verifyCalls, "the challenge is not consumed by a refused label");
    }

    @Test
    public void test_registerVerify_oversizedBody_isRejected() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        register(new StubLoginAssist(localUser("alice")));
        final String big = "{\"label\":\"x\",\"credential\":{\"id\":\"" + "a".repeat(16 * 1024) + "\"}}";
        final Response res = new Response();
        handler(new LoginRateLimiter(), service).registerVerify(new Request("POST").withSession().withBody(big).proxy(), res.proxy());
        assertEquals(413, res.status, res.body());
        assertEquals(0, service.verifyCalls);
    }

    @Test
    public void test_registerVerify_anyServiceFailure_isOneGenericError() throws Exception {
        Response first = null;
        for (final RuntimeException failure : new RuntimeException[] {
                new PasskeyException("reason=duplicate_credential credentialId=AAEC"),
                new IllegalStateException("search engine at 10.0.0.1 is down"), new NullPointerException() }) {
            final StubPasskeyService service = new StubPasskeyService(true);
            service.verifyFailure = failure;
            register(new StubLoginAssist(localUser("alice")));
            final Response res = new Response();
            handler(new LoginRateLimiter(), service).registerVerify(new Request("POST").withSession().withBody(VERIFY_BODY).proxy(),
                    res.proxy());
            assertEquals(400, res.status, res.body());
            assertTrue(res.body().contains("\"reason\":\"registration_failed\""), res.body());
            assertFalse(res.body().contains("duplicate"), res.body());
            assertFalse(res.body().contains("10.0.0.1"), res.body());
            if (first == null) {
                first = res;
            } else {
                assertEquals(first.body(), res.body());
            }
        }
    }

    // ---------------------------------------------------------------- list

    @Test
    public void test_list_onlyOwnCredentials_noKeyMaterial() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        service.stored.add(passkey(CREDENTIAL_ID, "alice", "Work laptop", 1000L, 2000L));
        service.stored.add(passkey("BBBB", "bob", "Bob's key", 3000L, null));
        register(new StubLoginAssist(localUser("alice")));
        final Response res = new Response();
        handler(new LoginRateLimiter(), service).list(new Request("GET").withSession().proxy(), res.proxy());
        assertEquals(200, res.status, res.body());
        assertEquals(List.of("alice"), service.listedUsers);
        final String body = res.body();
        assertTrue(body.contains(
                "\"passkeys\":[{\"id\":\"" + CREDENTIAL_ID + "\",\"label\":\"Work laptop\",\"created_at\":1000,\"last_used_at\":2000}]"),
                body);
        assertFalse(body.contains("Bob"), body);
        assertTrue(body.contains("\"can_register\":true"), body);
        assertNoKeyMaterial(body);
    }

    @Test
    public void test_list_ldapUserSession_isEmpty() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        service.stored.add(passkey(CREDENTIAL_ID, "alice", "Work laptop", 1000L, 2000L));
        register(new StubLoginAssist(ldapUser("alice")));
        final Response res = new Response();
        handler(new LoginRateLimiter(), service).list(new Request("GET").withSession().proxy(), res.proxy());
        assertEquals(200, res.status, res.body());
        assertTrue(res.body().contains("\"passkeys\":[]"), res.body());
        assertTrue(res.body().contains("\"can_register\":false"), res.body());
        assertEquals(0, service.listCalls, "the local user of the same name is not looked up");
    }

    // ---------------------------------------------------------------- delete

    @Test
    public void test_delete_ownCredential_deletes() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        service.stored.add(passkey(CREDENTIAL_ID, "alice", "Work laptop", 1000L, 2000L));
        register(new StubLoginAssist(localUser("alice")));
        final Response res = new Response();
        handler(new LoginRateLimiter(), service).delete(new Request("DELETE").withSession().proxy(), res.proxy(), CREDENTIAL_ID);
        assertEquals(200, res.status, res.body());
        assertTrue(service.stored.isEmpty());
    }

    @Test
    public void test_delete_otherUsersCredential_returns404() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        service.stored.add(passkey(CREDENTIAL_ID, "bob", "Bob's key", 1000L, null));
        register(new StubLoginAssist(localUser("alice")));
        final PasskeyAccountHandler handler = handler(new LoginRateLimiter(), service);
        final Response others = new Response();
        handler.delete(new Request("DELETE").withSession().proxy(), others.proxy(), CREDENTIAL_ID);
        assertEquals(404, others.status, others.body());
        assertEquals(1, service.stored.size(), "bob's passkey is kept");
        // the same answer as an id nobody has
        service.stored.clear();
        final Response none = new Response();
        handler.delete(new Request("DELETE").withSession().proxy(), none.proxy(), CREDENTIAL_ID);
        assertEquals(others.body(), none.body());
        assertEquals(404, none.status);
    }

    @Test
    public void test_delete_ldapUserSession_returns404() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        service.stored.add(passkey(CREDENTIAL_ID, "alice", "Work laptop", 1000L, null));
        register(new StubLoginAssist(ldapUser("alice")));
        final PasskeyAccountHandler handler = handler(new LoginRateLimiter(), service);
        final Response res = new Response();
        handler.delete(new Request("DELETE").withSession().proxy(), res.proxy(), CREDENTIAL_ID);
        assertEquals(404, res.status, res.body());
        assertEquals(1, service.stored.size(), "the local user's passkey of the same name is kept");
        assertEquals(0, service.deleteCalls);
        assertEquals(notFoundBody(handler, "ZZZZ"), res.body());
    }

    @Test
    public void test_delete_malformedId_returns404WithoutLookup() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        register(new StubLoginAssist(localUser("alice")));
        final PasskeyAccountHandler handler = handler(new LoginRateLimiter(), service);
        final String expected = notFoundBody(handler, "ZZZZ");
        final int before = service.deleteCalls;
        for (final String id : new String[] { "", "a+b", "a/b", "a=b", "a%2Fb", "x".repeat(513) }) {
            final Response res = new Response();
            handler.delete(new Request("DELETE").withSession().proxy(), res.proxy(), id);
            assertEquals(404, res.status, id);
            assertEquals(expected, res.body());
        }
        assertEquals(before, service.deleteCalls);
        // the longest credential id accepted (384 bytes, 512 characters, the longest document id) is still looked up
        handler.delete(new Request("DELETE").withSession().proxy(), new Response().proxy(), "x".repeat(512));
        assertEquals(before + 1, service.deleteCalls);
    }

    // ---------------------------------------------------------------- helpers

    private String notFoundBody(final PasskeyAccountHandler handler, final String id) throws Exception {
        final Response res = new Response();
        handler.delete(new Request("DELETE").withSession().proxy(), res.proxy(), id);
        assertEquals(404, res.status);
        return res.body();
    }

    private void assertNotFound(final Response res, final String subPath) {
        assertEquals(404, res.status, res.body());
        assertTrue(res.body().contains("\"code\":\"not_found\""), res.body());
        assertTrue(res.body().contains("endpoint not found: " + subPath), res.body());
    }

    private void assertAuthRequired(final Response res) {
        assertEquals(401, res.status, res.body());
        assertTrue(res.body().contains("\"code\":\"auth_required\""), res.body());
        assertTrue(res.body().contains("\"message\":\"login required\""), res.body());
    }

    private void assertNoKeyMaterial(final String body) {
        for (final String key : new String[] { "publicKey", "public_key", "userHandle", "user_handle", "signCount", "sign_count",
                "transports", "PUBKEY", "HANDLE" }) {
            assertFalse(body.contains(key), key + " in " + body);
        }
    }

    private static Passkey passkey(final String id, final String userName, final String label, final Long createdAt,
            final Long lastUsedAt) {
        final Passkey passkey = new Passkey();
        passkey.setId(id);
        passkey.setUserName(userName);
        passkey.setLabel(label);
        passkey.setCreatedAt(createdAt);
        passkey.setLastUsedAt(lastUsedAt);
        passkey.setPublicKey("PUBKEY");
        passkey.setUserHandle("HANDLE");
        passkey.setSignCount(5L);
        passkey.setTransports(new String[] { "internal" });
        return passkey;
    }

    private static User localUser(final String name) {
        final User user = new User();
        user.setName(name);
        return user;
    }

    private static LdapUser ldapUser(final String name) {
        return new LdapUser(name, false, new String[0], FessUser.PermissionState.RESOLVED);
    }

    private static FessUser plainFessUser(final String name) {
        return new FessUser() {
            private static final long serialVersionUID = 1L;

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
        };
    }

    private static PasskeyAccountHandler handler(final LoginRateLimiter rl, final PasskeyService service) {
        return new PasskeyAccountHandler(rl, service) {
            @Override
            protected OptionalEntity<User> findLocalUser(final String userName) {
                return OptionalEntity.of(localUser(userName));
            }
        };
    }

    private static StubLoginAssist register(final StubLoginAssist assist) {
        ComponentUtil.setFessLoginAssist(assist);
        return assist;
    }

    private static class StubPasskeyService extends PasskeyService {
        private final boolean enabled;
        final List<Passkey> stored = new ArrayList<>();
        final List<String> listedUsers = new ArrayList<>();
        RuntimeException verifyFailure;
        RuntimeException optionsFailure;
        User lastUser;
        String lastLabel;
        String lastResponseJson;
        int optionsCalls;
        int verifyCalls;
        int listCalls;
        int deleteCalls;

        StubPasskeyService(final boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public Map<String, Object> createRegistrationOptions(final HttpSession session, final User user) {
            optionsCalls++;
            if (optionsFailure != null) {
                throw optionsFailure;
            }
            lastUser = user;
            session.setAttribute("challenge", "c");
            return new HashMap<>(Map.of("challenge", "c"));
        }

        @Override
        public Passkey verifyRegistration(final HttpSession session, final User user, final String label, final String responseJson) {
            verifyCalls++;
            if (verifyFailure != null) {
                throw verifyFailure;
            }
            lastUser = user;
            lastLabel = label;
            lastResponseJson = responseJson;
            return passkey(CREDENTIAL_ID, user.getName(), label, 1000L, null);
        }

        @Override
        public List<Passkey> getPasskeys(final String userName) {
            listCalls++;
            listedUsers.add(userName);
            return stored.stream().filter(p -> userName.equals(p.getUserName())).toList();
        }

        @Override
        public boolean delete(final String userName, final String credentialId) {
            deleteCalls++;
            return stored.removeIf(p -> userName.equals(p.getUserName()) && credentialId.equals(p.getId()));
        }
    }

    /** A login assist whose session holds {@code sessionUser} and whose password check accepts {@link #PASSWORD}. */
    private static class StubLoginAssist extends FessLoginAssist {
        private final FessUser sessionUser;
        FessUser verifiedUser;
        LoginCredential lastCredential;
        int findLoginUserCalls;

        StubLoginAssist(final FessUser sessionUser) {
            this.sessionUser = sessionUser;
            this.verifiedUser = sessionUser;
        }

        @Override
        public OptionalThing<FessUserBean> getSavedUserBean() {
            return sessionUser == null ? OptionalThing.empty() : OptionalThing.of(new FessUserBean(sessionUser));
        }

        @Override
        public OptionalEntity<FessUser> findLoginUser(final LoginCredential credential) {
            findLoginUserCalls++;
            lastCredential = credential;
            if (credential instanceof final LocalUserCredential local && PASSWORD.equals(local.getPassword())) {
                return OptionalEntity.of(verifiedUser);
            }
            return OptionalEntity.empty();
        }
    }

    /** A request built from a dynamic proxy: only what the handler reads is answered. */
    private static class Request implements java.lang.reflect.InvocationHandler {
        private final String method;
        private byte[] body = new byte[0];
        final Map<String, Object> sessionAttributes = new HashMap<>();
        HttpSession session;

        Request(final String method) {
            this.method = method;
        }

        Request withSession() {
            session = (HttpSession) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { HttpSession.class },
                    (p, m, a) -> switch (m.getName()) {
                    case "getAttribute" -> sessionAttributes.get(a[0]);
                    case "setAttribute" -> sessionAttributes.put((String) a[0], a[1]);
                    case "removeAttribute" -> sessionAttributes.remove(a[0]);
                    case "getAttributeNames" -> Collections.enumeration(sessionAttributes.keySet());
                    case "getId" -> "stub-session";
                    case "hashCode" -> System.identityHashCode(p);
                    case "equals" -> p == a[0];
                    default -> defaultValue(m.getReturnType());
                    });
            return this;
        }

        Request withBody(final String json) {
            body = json.getBytes(StandardCharsets.UTF_8);
            return this;
        }

        @Override
        public Object invoke(final Object proxy, final java.lang.reflect.Method m, final Object[] a) {
            return switch (m.getName()) {
            case "getMethod" -> method;
            case "getRemoteAddr" -> "192.0.2.10";
            case "getContentType" -> "application/json";
            case "getContextPath" -> "";
            case "getInputStream" -> new StubInputStream(body);
            case "getSession" -> session;
            case "getHeaders", "getHeaderNames", "getParameterNames", "getAttributeNames" -> Collections.emptyEnumeration();
            case "getParameterMap" -> Collections.emptyMap();
            case "hashCode" -> System.identityHashCode(proxy);
            case "equals" -> proxy == a[0];
            default -> defaultValue(m.getReturnType());
            };
        }

        HttpServletRequest proxy() {
            return (HttpServletRequest) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { HttpServletRequest.class },
                    this);
        }
    }

    /** A response built from a dynamic proxy, capturing status, headers and body. */
    private static class Response implements java.lang.reflect.InvocationHandler {
        private final StringWriter sw = new StringWriter();
        private final PrintWriter writer = new PrintWriter(sw);
        final Map<String, String> headers = new HashMap<>();
        int status = 200;

        String body() {
            writer.flush();
            return sw.toString();
        }

        @Override
        public Object invoke(final Object proxy, final java.lang.reflect.Method m, final Object[] a) {
            switch (m.getName()) {
            case "setStatus" -> status = (Integer) a[0];
            case "getStatus" -> {
                return status;
            }
            case "setHeader" -> headers.put((String) a[0], (String) a[1]);
            case "getHeader" -> {
                return headers.get(a[0]);
            }
            case "getWriter" -> {
                return writer;
            }
            case "isCommitted" -> {
                return false;
            }
            case "resetBuffer" -> {
                writer.flush();
                sw.getBuffer().setLength(0);
            }
            case "hashCode" -> {
                return System.identityHashCode(proxy);
            }
            case "equals" -> {
                return proxy == a[0];
            }
            default -> {
                // setContentType, setCharacterEncoding and the like: nothing to capture
            }
            }
            return defaultValue(m.getReturnType());
        }

        HttpServletResponse proxy() {
            return (HttpServletResponse) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { HttpServletResponse.class },
                    this);
        }
    }

    private static Object defaultValue(final Class<?> type) {
        if (type == boolean.class) {
            return Boolean.FALSE;
        }
        if (type == int.class) {
            return 0;
        }
        if (type == long.class) {
            return 0L;
        }
        return null;
    }

    private static class StubInputStream extends ServletInputStream {
        private final ByteArrayInputStream in;

        StubInputStream(final byte[] body) {
            in = new ByteArrayInputStream(body);
        }

        @Override
        public int read() {
            return in.read();
        }

        @Override
        public boolean isFinished() {
            return in.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(final ReadListener readListener) {
            // not used
        }
    }
}
