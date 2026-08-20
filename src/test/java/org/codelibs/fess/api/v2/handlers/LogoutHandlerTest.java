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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.helper.ActivityHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.sso.SsoManager;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
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
 * Unit tests for {@link LogoutHandler}.
 *
 * <p>Extends {@link UnitFessTestCase} so {@code FessLoginAssist} can be
 * resolved through Lasta DI. The handler swallows exceptions from
 * {@code logout()} on purpose — the contract is "idempotent ok" — so the
 * unit harness's incomplete DI graph still produces a clean 200.</p>
 */
public class LogoutHandlerTest extends UnitFessTestCase {

    @Test
    public void test_returnsOkTrue() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new LogoutHandler().handle(new StubRequest("POST", "/api/v2/auth/logout"), res);
        assertEquals(200, res.status);
        assertTrue(res.body().contains("\"ok\":true"), res.body());
    }

    @Test
    public void test_rejectsGet_returns405WithAllowHeader() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new LogoutHandler().handle(new StubRequest("GET", "/api/v2/auth/logout"), res);
        assertEquals(405, res.status);
        assertTrue(res.body().contains("\"code\":\"method_not_allowed\""), res.body());
        assertEquals("POST", res.getHeader("Allow"));
    }

    @Test
    public void test_sessionPresentAndInvalidateSucceeds() throws Exception {
        // When the session exists and invalidate() succeeds normally the handler
        // must still return ok:true with HTTP 200.
        final CapturingResponse res = new CapturingResponse();
        new LogoutHandler().handle(new StubRequestWithSession("POST", "/api/v2/auth/logout", false), res);
        assertEquals(200, res.status);
        assertTrue(res.body().contains("\"ok\":true"), res.body());
    }

    @Test
    public void test_sessionAlreadyInvalidated_illegalStateSwallowed() throws Exception {
        // FessLoginAssist.logout() may already have invalidated the session internally.
        // session.invalidate() then throws IllegalStateException. The handler must swallow
        // it and still return ok:true — the contract is "idempotent ok".
        final CapturingResponse res = new CapturingResponse();
        new LogoutHandler().handle(new StubRequestWithSession("POST", "/api/v2/auth/logout", true), res);
        assertEquals(200, res.status);
        assertTrue(res.body().contains("\"ok\":true"), res.body());
    }

    // ── audit.log: v2 logout must leave the same activity record as LogoutAction ──

    @Test
    public void logout_writesLogoutRecordToAuditLog() throws Exception {
        // Regression: POST /api/v2/auth/logout never called ActivityHelper, so the SPA logout
        // left no audit.log line while the classic flow writes one from LogoutAction.index()
        // (activityHelper.logout(userBean)). The record must also be written BEFORE
        // FessLoginAssist.logout() drops the user bean — otherwise it would degrade to "user:-".
        final List<String> audit = new ArrayList<>();
        ComponentUtil.register(recordingActivityHelper(audit), "activityHelper");
        ComponentUtil.register(new StubLoginAssist("carol"), FessLoginAssist.class.getCanonicalName());
        final CapturingResponse res = new CapturingResponse();
        new LogoutHandler().handle(new StubRequest("POST", "/api/v2/auth/logout"), res);
        assertEquals(200, res.status, res.body());
        org.junit.jupiter.api.Assertions.assertEquals(List.of("action:LOGOUT\tuser:carol\tpermissions:-"), audit,
                "a v2 logout must write the same LOGOUT activity record as LogoutAction, with the user still resolved");
    }

    @Test
    public void logout_writesNoAuditRecordWhenNobodyWasLoggedIn() throws Exception {
        // The endpoint is idempotent and reachable without a session, so an unconditional write
        // let a caller append "action:LOGOUT user:-" once per request for as long as it cared to.
        // A logout that ended no session is not an event.
        final List<String> audit = new ArrayList<>();
        ComponentUtil.register(recordingActivityHelper(audit), "activityHelper");
        ComponentUtil.register(new StubLoginAssist(null), FessLoginAssist.class.getCanonicalName());
        final CapturingResponse res = new CapturingResponse();
        new LogoutHandler().handle(new StubRequest("POST", "/api/v2/auth/logout"), res);
        assertEquals(200, res.status, res.body());
        org.junit.jupiter.api.Assertions.assertEquals(List.of(), audit,
                "a logout that ended no session must not name one in the audit log");
    }

    @Test
    public void logout_auditFailureStillLogsOutAndReturnsOk() throws Exception {
        // A broken audit sink must not prevent the actual logout: the endpoint stays idempotent
        // and FessLoginAssist.logout() must still run.
        ComponentUtil.register(new ActivityHelper() {
            @Override
            public void logout(final OptionalThing<FessUserBean> user) {
                throw new IllegalStateException("audit sink is down");
            }
        }, "activityHelper");
        final StubLoginAssist assist = new StubLoginAssist("carol");
        ComponentUtil.register(assist, FessLoginAssist.class.getCanonicalName());
        final CapturingResponse res = new CapturingResponse();
        new LogoutHandler().handle(new StubRequest("POST", "/api/v2/auth/logout"), res);
        assertEquals(200, res.status, res.body());
        assertTrue(res.body().contains("\"ok\":true"), res.body());
        assertEquals(1, assist.logoutCount, "logout must still be performed when the audit write fails");
    }

    // ── SSO: the v2 logout must tear the session down at the identity provider too ──

    @Test
    public void logout_runsTheSsoLogoutForTheBoundUser() throws Exception {
        // Regression: the handler called FessLoginAssist.logout() but never
        // SsoManager.logout(user), which LogoutAction.index() does. For Entra ID that call is the
        // only thing that takes the account out of the MSAL4J token cache shared by the whole
        // application (EntraIdAuthenticator.logout -> removeAccount), and msal4j's TokenCache
        // evicts nothing by itself -- so the tokens of everyone who logged out through the v2 API
        // stayed resident for the life of the JVM.
        final List<String> ssoLogouts = new ArrayList<>();
        ComponentUtil.register(new SsoManager() {
            @Override
            public String logout(final FessUserBean user) {
                ssoLogouts.add(user.getUserId());
                // A real SLO URL. The v2 API has no redirect semantics, so it must be ignored.
                return "https://login.microsoftonline.com/common/oauth2/v2.0/logout";
            }
        }, "ssoManager");
        final StubLoginAssist assist = new StubLoginAssist("carol");
        ComponentUtil.register(assist, FessLoginAssist.class.getCanonicalName());

        final CapturingResponse res = new CapturingResponse();
        new LogoutHandler().handle(new StubRequest("POST", "/api/v2/auth/logout"), res);

        assertEquals(200, res.status, res.body());
        org.junit.jupiter.api.Assertions.assertEquals(List.of("carol"), ssoLogouts,
                "the v2 logout must run the SSO logout for the user still bound to the session");
        assertEquals(1, assist.logoutCount, "the local logout must still run");
        // The single-logout URL is discarded: the envelope stays the plain idempotent success.
        assertTrue(res.body().contains("\"ok\":true"), res.body());
    }

    @Test
    public void logout_ssoFailureStillLogsOutAndReturnsOk() throws Exception {
        // An unreachable identity provider must not strand the caller in a logged-in session:
        // the local logout has to run anyway and the endpoint has to stay idempotent.
        ComponentUtil.register(new SsoManager() {
            @Override
            public String logout(final FessUserBean user) {
                throw new IllegalStateException("the identity provider is unreachable");
            }
        }, "ssoManager");
        final StubLoginAssist assist = new StubLoginAssist("carol");
        ComponentUtil.register(assist, FessLoginAssist.class.getCanonicalName());

        final CapturingResponse res = new CapturingResponse();
        new LogoutHandler().handle(new StubRequest("POST", "/api/v2/auth/logout"), res);

        assertEquals(200, res.status, res.body());
        assertTrue(res.body().contains("\"ok\":true"), res.body());
        assertEquals(1, assist.logoutCount, "logout must still be performed when the SSO logout fails");
    }

    @Test
    public void logout_withoutAUserBean_doesNotTouchTheSsoManager() throws Exception {
        // The endpoint is fire-and-forget, so it is routinely called with no session at all.
        final List<String> ssoLogouts = new ArrayList<>();
        ComponentUtil.register(new SsoManager() {
            @Override
            public String logout(final FessUserBean user) {
                ssoLogouts.add(String.valueOf(user));
                return null;
            }
        }, "ssoManager");
        final StubLoginAssist assist = new StubLoginAssist("carol");
        assist.logout();
        assist.logoutCount = 0;
        ComponentUtil.register(assist, FessLoginAssist.class.getCanonicalName());

        final CapturingResponse res = new CapturingResponse();
        new LogoutHandler().handle(new StubRequest("POST", "/api/v2/auth/logout"), res);

        assertEquals(200, res.status, res.body());
        assertTrue(ssoLogouts.isEmpty(), "an absent user bean must not reach SsoManager.logout: " + ssoLogouts);
    }

    /**
     * Builds an {@link ActivityHelper} that renders real LTSV records into {@code sink} instead
     * of writing to the audit logger. {@code time}/{@code ip} are stripped so the expectation is
     * deterministic — same approach as {@code ActivityHelperTest}.
     */
    private static ActivityHelper recordingActivityHelper(final List<String> sink) {
        return new ActivityHelper() {
            @Override
            protected void printByLtsv(final Map<String, String> valueMap) {
                valueMap.remove("time");
                valueMap.remove("ip");
                super.printByLtsv(valueMap);
            }

            @Override
            protected void printLog(final String message) {
                sink.add(message);
            }

            @Override
            protected String getClientIp() {
                return "";
            }
        };
    }

    /**
     * {@link FessLoginAssist} stub registered under its canonical name so
     * {@code ComponentUtil.getComponent(FessLoginAssist.class)} resolves it (the real component
     * fails auto-binding in the slim test DI graph, which makes {@code componentMap} the
     * effective lookup). {@code logout()} clears the bound user bean, mirroring production.
     */
    private static class StubLoginAssist extends FessLoginAssist {
        private FessUserBean bound;

        int logoutCount;

        StubLoginAssist(final String userId) {
            bound = userId == null ? null : new FessUserBean(new StubFessUser(userId));
        }

        @Override
        public OptionalThing<FessUserBean> getSavedUserBean() {
            return bound == null ? OptionalThing.empty() : OptionalThing.of(bound);
        }

        @Override
        public void logout() {
            logoutCount++;
            bound = null;
        }
    }

    /** Minimal {@link FessUser} with no roles, groups or permissions. */
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

    /** Minimal HttpServletResponse stub — captures status, content type, headers and body. */
    private static class CapturingResponse implements HttpServletResponse {
        final StringWriter sw = new StringWriter();
        final PrintWriter writer = new PrintWriter(sw);
        final Map<String, String> headers = new HashMap<>();
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
            headers.put(name, value);
        }

        @Override
        public void addHeader(final String name, final String value) {
            headers.put(name, value);
        }

        @Override
        public void setIntHeader(final String name, final int value) {
            headers.put(name, Integer.toString(value));
        }

        @Override
        public void addIntHeader(final String name, final int value) {
            headers.put(name, Integer.toString(value));
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
     * Minimal HttpServletRequest stub. {@code getSession(false)} returns {@code null}
     * — the basic ok-path test exercises the no-session branch where the handler
     * skips token rotation and just emits the success envelope.
     */
    private static class StubRequest implements HttpServletRequest {
        private final String method;
        private final String uri;
        private final Map<String, Object> attrs = new HashMap<>();

        StubRequest(final String method, final String uri) {
            this.method = method;
            this.uri = uri;
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
            return 0;
        }

        @Override
        public long getContentLengthLong() {
            return 0;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public ServletInputStream getInputStream() {
            throw new UnsupportedOperationException();
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
     * StubRequest variant that returns a {@link HttpSession} from {@code getSession(false)}.
     * When {@code throwOnInvalidate} is {@code true} the session's {@code invalidate()} throws
     * {@link IllegalStateException} — simulating the case where {@link FessLoginAssist#logout()}
     * already invalidated it.
     */
    private static class StubRequestWithSession extends StubRequest {
        private final HttpSession session;

        StubRequestWithSession(final String method, final String uri, final boolean throwOnInvalidate) {
            super(method, uri);
            this.session = new HttpSession() {
                @Override
                public void invalidate() {
                    if (throwOnInvalidate) {
                        throw new IllegalStateException("already invalidated");
                    }
                }

                @Override
                public long getCreationTime() {
                    return System.currentTimeMillis();
                }

                @Override
                public String getId() {
                    return "stub-session-id";
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
                    return 1800;
                }

                @Override
                public Object getAttribute(final String name) {
                    return null;
                }

                @Override
                public Enumeration<String> getAttributeNames() {
                    return Collections.emptyEnumeration();
                }

                @Override
                public void setAttribute(final String name, final Object value) {
                }

                @Override
                public void removeAttribute(final String name) {
                }

                @Override
                public boolean isNew() {
                    return false;
                }
            };
        }

        @Override
        public HttpSession getSession(final boolean create) {
            return session;
        }

        @Override
        public HttpSession getSession() {
            return session;
        }
    }
}
