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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.app.web.base.login.LocalUserCredential;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
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
 * <p>Extends {@link UnitFessTestCase} so {@code FessLoginAssist} resolves via
 * Lasta DI. In the unit harness no session is bound, so
 * {@code getSavedUserBean()} returns an empty {@code OptionalThing} — that
 * gives us the auth-required branch for free. The pre-auth validation tests
 * (GET method) exit before the auth check.</p>
 *
 * <p>The mismatch and blank tests use {@code application/json} bodies but —
 * importantly — also exit at the auth-required gate because no user is logged
 * in. They assert the auth-required behaviour rather than the validation
 * behaviour. A true validation-only test would need a faked user bean, which
 * is awkward in {@code UnitFessTestCase}. TODO: revisit when an integration
 * harness with a logged-in user is available.</p>
 */
public class PasswordChangeHandlerTest extends UnitFessTestCase {

    @Test
    public void test_rejectsGet() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new PasswordChangeHandler().handle(new StubRequest("GET", "/api/v2/auth/password"), res);
        assertEquals(400, res.status);
        assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
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
        }

        @Override
        public void addHeader(final String name, final String value) {
        }

        @Override
        public void setIntHeader(final String name, final int value) {
        }

        @Override
        public void addIntHeader(final String name, final int value) {
        }

        @Override
        public String getHeader(final String name) {
            return null;
        }

        @Override
        public java.util.Collection<String> getHeaders(final String name) {
            return java.util.Collections.emptyList();
        }

        @Override
        public java.util.Collection<String> getHeaderNames() {
            return java.util.Collections.emptyList();
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

        StubRequest(final String method, final String uri) {
            this.method = method;
            this.uri = uri;
        }

        StubRequest withJsonBody(final String json) {
            this.body = json == null ? new byte[0] : json.getBytes(StandardCharsets.UTF_8);
            this.contentType = "application/json";
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
            return null;
        }

        @Override
        public HttpSession getSession() {
            return null;
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
}
