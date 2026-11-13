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

import org.codelibs.fess.api.v2.SessionCsrfTokenManager;
import org.codelibs.fess.exception.PasskeyException;
import org.codelibs.fess.app.service.PasskeyService;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.app.web.base.login.LocalUserCredential;
import org.codelibs.fess.app.web.base.login.PasskeyCredential;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.helper.ActivityHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.login.exception.LoginFailureException;
import org.lastaflute.web.login.option.LoginOpCall;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Unit tests for {@link PasskeyLoginHandler}. The assertion check itself is {@code PasskeyServiceTest}'s;
 * here the service is a stub and the handler's own contract is pinned: the feature gate, the IP bucket,
 * the failure shape shared with the password login, and the post-login steps shared with it.
 */
public class PasskeyLoginHandlerTest extends UnitFessTestCase {

    private static final String IP = "192.0.2.10";

    private static final String VALID_BODY = "{\"credential\":{\"id\":\"abc\",\"type\":\"public-key\"},\"return_to\":\"/search?q=x\"}";

    private final List<String> audit = new ArrayList<>();

    // ---------------------------------------------------------------- feature gate

    @Test
    public void test_options_disabled_returns404() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(false);
        final Response res = new Response();
        new PasskeyLoginHandler(new LoginRateLimiter(), service).options(new Request("POST").withSession().proxy(), res.proxy());
        assertEquals(404, res.status);
        assertTrue(res.body().contains("\"code\":\"not_found\""), res.body());
        assertTrue(res.body().contains("endpoint not found: /auth/passkey/login/options"), res.body());
        assertEquals(0, service.optionsCalls);
    }

    @Test
    public void test_verify_disabled_returns404() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(false);
        final LoginRateLimiter rl = new LoginRateLimiter();
        final Response res = new Response();
        new PasskeyLoginHandler(rl, service).verify(new Request("POST").withSession().withBody(VALID_BODY).proxy(), res.proxy());
        assertEquals(404, res.status);
        assertTrue(res.body().contains("\"code\":\"not_found\""), res.body());
        assertTrue(res.body().contains("endpoint not found: /auth/passkey/login/verify"), res.body());
        assertEquals(0, service.verifyCalls);
        // A disabled feature does not touch the bucket either.
        assertTrue(rl.peek(LoginRateLimiter.Scope.IP, IP, 1, 60));
    }

    @Test
    public void test_rejectsGet() throws Exception {
        final Response options = new Response();
        new PasskeyLoginHandler(new LoginRateLimiter(), new StubPasskeyService(true)).options(new Request("GET").proxy(), options.proxy());
        assertEquals(405, options.status);
        final Response verify = new Response();
        new PasskeyLoginHandler(new LoginRateLimiter(), new StubPasskeyService(true)).verify(new Request("GET").proxy(), verify.proxy());
        assertEquals(405, verify.status);
    }

    // ---------------------------------------------------------------- options

    @Test
    public void test_options_returnsChallengeAndEmptyAllowCredentials() throws Exception {
        ComponentUtil.register(new org.codelibs.fess.helper.SystemHelper(), "systemHelper");
        // The real createLoginOptions, so the wire shape is the service's own.
        final PasskeyService service = new PasskeyService() {
            {
                fessConfig = ComponentUtil.getFessConfig();
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };
        final LoginRateLimiter rl = new LoginRateLimiter();
        final Request req = new Request("POST").withSession();
        final Response res = new Response();
        new PasskeyLoginHandler(rl, service).options(req.proxy(), res.proxy());
        assertEquals(200, res.status, res.body());
        assertTrue(res.body().matches(".*\"challenge\":\"[A-Za-z0-9_-]{43}\".*"), res.body());
        assertTrue(res.body().contains("\"allowCredentials\":[]"), res.body());
        assertTrue(res.body().contains("\"userVerification\":\"required\""), res.body());
        assertFalse(req.sessionAttributes.isEmpty(), "the challenge is kept in the session");
        // options verifies nothing, so it does not count against the address.
        assertTrue(rl.peek(LoginRateLimiter.Scope.IP, IP, 1, 60));
    }

    @Test
    public void test_options_ipLockedOut_returns429() throws Exception {
        final LoginRateLimiter rl = new LoginRateLimiter();
        rl.lockOut(LoginRateLimiter.Scope.IP, IP, 60);
        final StubPasskeyService service = new StubPasskeyService(true);
        final Response res = new Response();
        new PasskeyLoginHandler(rl, service).options(new Request("POST").withSession().proxy(), res.proxy());
        assertEquals(429, res.status);
        assertTrue(res.body().contains("\"code\":\"rate_limited\""), res.body());
        assertNotNull(res.headers.get("Retry-After"), "an ip refusal carries Retry-After");
        assertEquals(0, service.optionsCalls);
    }

    @Test
    public void test_options_serviceFailure_isTheGenericFailure() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        service.optionsFailure = new IllegalStateException("boom");
        final Response res = new Response();
        new PasskeyLoginHandler(new LoginRateLimiter(), service).options(new Request("POST").withSession().proxy(), res.proxy());
        assertEquals(passwordFailure().body(), res.body());
        assertEquals(401, res.status);
    }

    // ---------------------------------------------------------------- verify

    @Test
    public void test_verify_success_logsInAndRotatesSessionAndCsrf() throws Exception {
        registerLoginComponents(new StubLoginAssist(false));
        final StubPasskeyService service = new StubPasskeyService(true);
        service.userName = "alice";
        final LoginRateLimiter rl = new LoginRateLimiter();
        final Request req = new Request("POST").withSession().withBody(VALID_BODY);
        final HttpSession session = req.session;
        final String before = ComponentUtil.getComponent(SessionCsrfTokenManager.class).issue(session);
        final Response res = new Response();

        new PasskeyLoginHandler(rl, service).verify(req.proxy(), res.proxy());

        assertEquals(200, res.status, res.body());
        assertTrue(res.body().contains("\"user_id\":\"alice\""), res.body());
        assertTrue(res.body().contains("\"return_to\":\"/search?q=x\""), res.body());
        final String token = ComponentUtil.getComponent(SessionCsrfTokenManager.class).issue(session);
        assertTrue(res.body().contains("\"csrf_token\":\"" + token + "\""), res.body());
        assertFalse(before.equals(token), "the CSRF token is rotated");
        assertEquals(1, req.changeSessionIdCalls);
        // the credential field, as it was posted, is what the service verified
        assertEquals("{\"id\":\"abc\",\"type\":\"public-key\"}", service.lastResponseJson);
        final StubLoginAssist assist = (StubLoginAssist) ComponentUtil.getFessLoginAssist();
        assertTrue(assist.lastCredential instanceof PasskeyCredential);
        assertEquals("alice", ((PasskeyCredential) assist.lastCredential).getUserId());
        assertEquals(List.of("action:LOGIN\tuser:alice\tpermissions:-"), audit);
        // the slot this request took is given back
        assertTrue(rl.peek(LoginRateLimiter.Scope.IP, IP, 1, 60));
    }

    @Test
    public void test_verify_failure_hasSameShapeAsPasswordFailure() throws Exception {
        final Response password = passwordFailure();
        audit.clear();
        registerLoginComponents(new StubLoginAssist(false));
        final StubPasskeyService service = new StubPasskeyService(true);
        service.verifyFailure = new PasskeyException("reason=login_rejected");
        final Response res = new Response();
        new PasskeyLoginHandler(new LoginRateLimiter(), service).verify(new Request("POST").withSession().withBody(VALID_BODY).proxy(),
                res.proxy());

        assertEquals(password.status, res.status);
        assertEquals(password.body(), res.body());
        assertEquals(password.headers, res.headers);
        assertFalse(res.body().contains("login_rejected"), res.body());
        assertEquals(List.of("action:LOGIN_FAILURE"), audit);
        assertNull(((StubLoginAssist) ComponentUtil.getFessLoginAssist()).lastCredential, "no one is logged in");
    }

    @Test
    public void test_verify_anyServiceException_isTheSameFailure() throws Exception {
        final Response password = passwordFailure();
        for (final RuntimeException failure : new RuntimeException[] { new IllegalArgumentException("Illegal base64 character"),
                new IllegalStateException("search engine is unreachable"), new NullPointerException() }) {
            audit.clear();
            registerLoginComponents(new StubLoginAssist(false));
            final StubPasskeyService service = new StubPasskeyService(true);
            service.verifyFailure = failure;
            final Response res = new Response();
            new PasskeyLoginHandler(new LoginRateLimiter(), service).verify(new Request("POST").withSession().withBody(VALID_BODY).proxy(),
                    res.proxy());
            assertEquals(1, service.verifyCalls, "the failure came from the service");
            assertEquals(password.status, res.status);
            assertEquals(password.body(), res.body());
            assertEquals(List.of("action:LOGIN_FAILURE"), audit);
        }
    }

    @Test
    public void test_verify_noSession_failsWithoutCallingTheService() throws Exception {
        registerLoginComponents(new StubLoginAssist(false));
        final StubPasskeyService service = new StubPasskeyService(true);
        final Response res = new Response();
        final Request req = new Request("POST").withBody(VALID_BODY);
        new PasskeyLoginHandler(new LoginRateLimiter(), service).verify(req.proxy(), res.proxy());
        assertEquals(passwordFailure().body(), res.body());
        assertEquals(0, service.verifyCalls);
        assertFalse(req.sessionCreated, "verify never creates a session");
    }

    @Test
    public void test_verify_userRejectedAfterVerification_isTheSameFailure() throws Exception {
        final Response password = passwordFailure();
        audit.clear();
        registerLoginComponents(new StubLoginAssist(true));
        final StubPasskeyService service = new StubPasskeyService(true);
        service.userName = "alice";
        final LoginRateLimiter rl = new LoginRateLimiter();
        final Response res = new Response();
        new PasskeyLoginHandler(rl, service).verify(new Request("POST").withSession().withBody(VALID_BODY).proxy(), res.proxy());
        assertEquals(password.body(), res.body());
        assertEquals(List.of("action:LOGIN_FAILURE\tclass:PasskeyCredential\tuser:alice"), audit);
        assertFalse(rl.peek(LoginRateLimiter.Scope.IP, IP, 1, 60), "the failure keeps its slot");
    }

    @Test
    public void test_verify_missingCredential_returnsInvalidRequest() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        for (final String json : new String[] { "{}", "{\"credential\":\"text\"}", "{\"credential\":[]}" }) {
            final Response res = new Response();
            new PasskeyLoginHandler(new LoginRateLimiter(), service).verify(new Request("POST").withSession().withBody(json).proxy(),
                    res.proxy());
            assertEquals(400, res.status, json);
            assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
        }
        assertEquals(0, service.verifyCalls);
    }

    @Test
    public void test_verify_oversizedBody_returnsInvalidRequest() throws Exception {
        final StubPasskeyService service = new StubPasskeyService(true);
        final String big = "{\"credential\":{\"id\":\"" + "a".repeat(16 * 1024) + "\"}}";
        final Response res = new Response();
        new PasskeyLoginHandler(new LoginRateLimiter(), service).verify(new Request("POST").withSession().withBody(big).proxy(),
                res.proxy());
        assertEquals(400, res.status);
        assertTrue(res.body().contains("payload too large"), res.body());
        assertEquals(0, service.verifyCalls);
    }

    @Test
    public void test_verify_ipRateLimited_returns429() throws Exception {
        final LoginRateLimiter rl = new LoginRateLimiter();
        rl.lockOut(LoginRateLimiter.Scope.IP, IP, 60);
        final StubPasskeyService service = new StubPasskeyService(true);
        final Response res = new Response();
        new PasskeyLoginHandler(rl, service).verify(new Request("POST").withSession().withBody(VALID_BODY).proxy(), res.proxy());
        assertEquals(429, res.status);
        assertTrue(res.body().contains("\"code\":\"rate_limited\""), res.body());
        assertNotNull(res.headers.get("Retry-After"), "an ip refusal carries Retry-After");
        assertEquals(0, service.verifyCalls);
    }

    @Test
    public void test_verify_failureConsumesIpSlot() throws Exception {
        registerLoginComponents(new StubLoginAssist(false));
        final int ipLimit = ComponentUtil.getFessConfig().getThemeApiLoginRateLimitPerIpPerMinuteAsInteger();
        final StubPasskeyService service = new StubPasskeyService(true);
        service.verifyFailure = new PasskeyException("reason=login_rejected");
        final LoginRateLimiter rl = new LoginRateLimiter();
        final PasskeyLoginHandler handler = new PasskeyLoginHandler(rl, service);
        for (int i = 0; i < ipLimit; i++) {
            final Response res = new Response();
            handler.verify(new Request("POST").withSession().withBody(VALID_BODY).proxy(), res.proxy());
            assertEquals(401, res.status, "attempt " + i);
        }
        final Response refused = new Response();
        handler.verify(new Request("POST").withSession().withBody(VALID_BODY).proxy(), refused.proxy());
        assertEquals(429, refused.status, refused.body());
        assertEquals(ipLimit, service.verifyCalls);
        // and the address is locked out of the password login too: one bucket
        final Response password = new Response();
        new LoginHandler(rl).handle(new Request("POST").withBody("{\"username\":\"u\",\"password\":\"p\"}").proxy(), password.proxy());
        assertEquals(429, password.status, password.body());
    }

    @Test
    public void test_verify_successAfterFailures_keepsTheFailuresCounted() throws Exception {
        registerLoginComponents(new StubLoginAssist(false));
        final StubPasskeyService service = new StubPasskeyService(true);
        service.verifyFailure = new PasskeyException("reason=login_rejected");
        final LoginRateLimiter rl = new LoginRateLimiter();
        final PasskeyLoginHandler handler = new PasskeyLoginHandler(rl, service);
        handler.verify(new Request("POST").withSession().withBody(VALID_BODY).proxy(), new Response().proxy());
        service.verifyFailure = null;
        service.userName = "alice";
        final Response ok = new Response();
        handler.verify(new Request("POST").withSession().withBody(VALID_BODY).proxy(), ok.proxy());
        assertEquals(200, ok.status, ok.body());
        // one failure is still in the bucket: a success gives back only its own slot
        assertFalse(rl.peek(LoginRateLimiter.Scope.IP, IP, 1, 60));
        assertTrue(rl.peek(LoginRateLimiter.Scope.IP, IP, 2, 60));
    }

    // ---------------------------------------------------------------- helpers

    /** The answer of {@link LoginHandler} to a wrong password, which every passkey failure must equal. */
    private Response passwordFailure() throws Exception {
        final FessLoginAssist previous = registerLoginComponents(new StubLoginAssist(true));
        final Response res = new Response();
        new LoginHandler(new LoginRateLimiter()).handle(new Request("POST").withBody("{\"username\":\"u\",\"password\":\"p\"}").proxy(),
                res.proxy());
        assertEquals(401, res.status, res.body());
        assertTrue(audit.contains("action:LOGIN_FAILURE\tclass:" + LocalUserCredential.class.getSimpleName() + "\tuser:u"),
                audit.toString());
        ComponentUtil.setFessLoginAssist(previous);
        return res;
    }

    private FessLoginAssist registerLoginComponents(final StubLoginAssist assist) {
        ComponentUtil.register(new ActivityHelper() {
            @Override
            protected void printByLtsv(final Map<String, String> valueMap) {
                valueMap.remove("time");
                valueMap.remove("ip");
                super.printByLtsv(valueMap);
            }

            @Override
            protected void printLog(final String message) {
                audit.add(message);
            }

            @Override
            protected String getClientIp() {
                return "";
            }
        }, "activityHelper");
        ComponentUtil.register(new SessionCsrfTokenManager(), SessionCsrfTokenManager.class.getCanonicalName());
        ComponentUtil.setFessLoginAssist(assist);
        return assist;
    }

    private static class StubPasskeyService extends PasskeyService {
        private final boolean enabled;
        String userName;
        RuntimeException verifyFailure;
        RuntimeException optionsFailure;
        String lastResponseJson;
        int verifyCalls;
        int optionsCalls;

        StubPasskeyService(final boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public Map<String, Object> createLoginOptions(final HttpSession session) {
            optionsCalls++;
            if (optionsFailure != null) {
                throw optionsFailure;
            }
            return new HashMap<>(Map.of("challenge", "c"));
        }

        @Override
        public String verifyLogin(final HttpSession session, final String responseJson) {
            verifyCalls++;
            lastResponseJson = responseJson;
            if (verifyFailure != null) {
                throw verifyFailure;
            }
            return userName;
        }
    }

    private static class StubLoginAssist extends FessLoginAssist {
        private final boolean reject;
        LoginCredential lastCredential;
        private FessUserBean bound;

        StubLoginAssist(final boolean reject) {
            this.reject = reject;
        }

        @Override
        public void login(final LoginCredential credential, final LoginOpCall opLambda) {
            if (reject) {
                throw new LoginFailureException("stubbed rejection");
            }
            lastCredential = credential;
            final String name = ((org.codelibs.fess.app.web.base.login.FessCredential) credential).getUserId();
            bound = new FessUserBean(new FessUser() {
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
            });
        }

        @Override
        public OptionalThing<FessUserBean> getSavedUserBean() {
            return bound == null ? OptionalThing.empty() : OptionalThing.of(bound);
        }

        @Override
        public void logout() {
            bound = null;
        }
    }

    /** A request built from a dynamic proxy: only what the handlers read is answered. */
    private static class Request implements java.lang.reflect.InvocationHandler {
        private final String method;
        private byte[] body = new byte[0];
        final Map<String, Object> sessionAttributes = new HashMap<>();
        HttpSession session;
        boolean sessionCreated;
        int changeSessionIdCalls;

        Request(final String method) {
            this.method = method;
        }

        Request withSession() {
            session = newSession();
            return this;
        }

        Request withBody(final String json) {
            body = json.getBytes(StandardCharsets.UTF_8);
            return this;
        }

        private HttpSession newSession() {
            return (HttpSession) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { HttpSession.class },
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
        }

        @Override
        public Object invoke(final Object proxy, final java.lang.reflect.Method m, final Object[] a) {
            return switch (m.getName()) {
            case "getMethod" -> method;
            case "getRemoteAddr" -> IP;
            case "getContentType" -> "application/json";
            case "getServletPath", "getRequestURI" -> "/api/v2/auth/passkey/login/verify";
            case "getContextPath" -> "";
            case "getInputStream" -> new StubInputStream(body);
            case "getSession" -> {
                if (session == null && (a == null || (Boolean) a[0])) {
                    session = newSession();
                    sessionCreated = true;
                }
                yield session;
            }
            case "changeSessionId" -> {
                if (session == null) {
                    throw new IllegalStateException("no session");
                }
                changeSessionIdCalls++;
                yield "rotated";
            }
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
