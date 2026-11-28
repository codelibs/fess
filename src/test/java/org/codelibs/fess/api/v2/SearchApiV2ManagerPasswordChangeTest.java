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
package org.codelibs.fess.api.v2;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.codelibs.fess.api.v2.handlers.LoginHandler;
import org.codelibs.fess.api.v2.handlers.LoginRequirement;
import org.codelibs.fess.api.v2.handlers.LogoutHandler;
import org.codelibs.fess.api.v2.handlers.MeHandler;
import org.codelibs.fess.api.v2.handlers.PasswordChangeHandler;
import org.codelibs.fess.api.v2.handlers.SearchHandler;
import org.codelibs.fess.api.v2.handlers.UiConfigHandler;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Pins the gate of {@code SearchApiV2Manager} for a login session opened with a password on
 * {@code password.invalid.admin.passwords}: it reaches what an anonymous caller reaches under
 * {@code login.required} and the password change, and every other endpoint answers
 * {@code 403 password_change_required}.
 */
public class SearchApiV2ManagerPasswordChangeTest extends UnitFessTestCase {

    private FessUserBean userBean;

    private boolean hasSession;

    private final List<String> handled = new ArrayList<>();

    @BeforeEach
    public void registerComponents() {
        final LoginRequirement requirement = new LoginRequirement();
        ComponentUtil.register(requirement, "v2LoginRequirement");
        ComponentUtil.register(requirement, LoginRequirement.class.getCanonicalName());
        final FessLoginAssist assist = new FessLoginAssist() {
            private static final long serialVersionUID = 1L;

            @Override
            public OptionalThing<FessUserBean> getSavedUserBean() {
                return OptionalThing.ofNullable(userBean, () -> {
                    throw new IllegalStateException("no user");
                });
            }
        };
        ComponentUtil.register(assist, "fessLoginAssist");
        ComponentUtil.setFessLoginAssist(assist);
        final User user = new User();
        user.setName("admin");
        userBean = new FessUserBean(user);
        userBean.setPasswordChangeRequired(true);
        hasSession = true;
        handled.clear();
    }

    @Test
    public void test_otherEndpoints_areRefusedUntilThePasswordIsChanged() throws Exception {
        for (final String path : new String[] { "/api/v2/search", "/api/v2/documents/all", "/api/v2/documents/export",
                "/api/v2/documents/abc/similar", "/api/v2/documents/abc/favorite", "/api/v2/suggest-words", "/api/v2/labels",
                "/api/v2/popular-words", "/api/v2/favorites", "/api/v2/search-history", "/api/v2/tags", "/api/v2/chat", "/api/v2/cache/abc",
                "/api/v2/auth/passkey", "/api/v2/auth/passkey/register/options", "/api/v2/no-such-endpoint" }) {
            final Captured res = process(path);
            assertEquals(403, res.status, path);
            assertTrue(res.body().contains("\"code\":\"password_change_required\""), path + " -> " + res.body());
            assertTrue(handled.isEmpty(), path + " reached " + handled);
        }
    }

    @Test
    public void test_theChangeAndTheLoginFlow_stayReachable() throws Exception {
        for (final String path : new String[] { "/api/v2/auth/password", "/api/v2/auth/me", "/api/v2/auth/login", "/api/v2/auth/logout",
                "/api/v2/ui/config" }) {
            handled.clear();
            final Captured res = process(path);
            assertEquals(res.body(), List.of(path), handled);
            assertFalse(res.body().contains("password_change_required"), path + " -> " + res.body());
        }
    }

    @Test
    public void test_aSessionWithoutTheMark_isServed() throws Exception {
        userBean.setPasswordChangeRequired(false);
        final Captured res = process("/api/v2/search");
        assertEquals(res.body(), List.of("/api/v2/search"), handled);
    }

    @Test
    public void test_aRequestWithoutASession_isServed() throws Exception {
        hasSession = false;
        final Captured res = process("/api/v2/search");
        assertEquals(res.body(), List.of("/api/v2/search"), handled);
    }

    @Test
    public void test_aCallerThatCannotBeResolved_isAnInternalError() throws Exception {
        final FessLoginAssist broken = new FessLoginAssist() {
            private static final long serialVersionUID = 1L;

            @Override
            public OptionalThing<FessUserBean> getSavedUserBean() {
                throw new IllegalStateException("broken");
            }
        };
        ComponentUtil.setFessLoginAssist(broken);
        final Captured res = process("/api/v2/search");
        assertEquals(500, res.status, res.body());
        assertTrue(handled.isEmpty(), handled.toString());
    }

    // ---------------------------------------------------------------------
    // Fakes
    // ---------------------------------------------------------------------

    private Captured process(final String servletPath) throws Exception {
        final SearchApiV2Manager manager = SearchApiV2ManagerTestSupport.newManagerWithHandlers();
        manager.searchHandler = new SearchHandler() {
            @Override
            public void handle(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
                handled.add(request.getServletPath());
            }
        };
        manager.passwordChangeHandler = new PasswordChangeHandler() {
            @Override
            public void handle(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
                handled.add(request.getServletPath());
            }
        };
        manager.meHandler = new MeHandler() {
            @Override
            public void handle(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
                handled.add(request.getServletPath());
            }
        };
        manager.loginHandler = new LoginHandler() {
            @Override
            public void handle(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
                handled.add(request.getServletPath());
            }
        };
        manager.logoutHandler = new LogoutHandler() {
            @Override
            public void handle(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
                handled.add(request.getServletPath());
            }
        };
        manager.uiConfigHandler = new UiConfigHandler() {
            @Override
            public void handle(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
                handled.add(request.getServletPath());
            }
        };
        final Captured res = new Captured();
        manager.process(request(servletPath), res.proxy(), (FilterChain) (req, resp) -> {});
        return res;
    }

    private HttpServletRequest request(final String servletPath) {
        final HttpSession session = (HttpSession) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { HttpSession.class },
                (proxy, method, args) -> defaultValue(method.getReturnType()));
        final InvocationHandler h = (proxy, method, args) -> switch (method.getName()) {
        case "getServletPath", "getRequestURI" -> servletPath;
        case "getMethod" -> "GET";
        case "getSession" -> hasSession ? session : null;
        case "getParameterMap" -> java.util.Collections.emptyMap();
        case "getParameterNames" -> java.util.Collections.enumeration(java.util.Collections.emptyList());
        case "getRemoteAddr" -> "127.0.0.1";
        case "toString" -> "StubRequest[" + servletPath + "]";
        case "hashCode" -> System.identityHashCode(proxy);
        case "equals" -> proxy == args[0];
        default -> defaultValue(method.getReturnType());
        };
        return (HttpServletRequest) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { HttpServletRequest.class }, h);
    }

    private static Object defaultValue(final Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == boolean.class) {
            return Boolean.FALSE;
        }
        if (type == int.class) {
            return Integer.valueOf(0);
        }
        if (type == long.class) {
            return Long.valueOf(0L);
        }
        return null;
    }

    /** Records the status and the body that the envelope writer emits. */
    private static class Captured {
        int status = 200;

        private final StringWriter writer = new StringWriter();

        private final PrintWriter printWriter = new PrintWriter(writer);

        String body() {
            printWriter.flush();
            return writer.toString();
        }

        HttpServletResponse proxy() {
            return (HttpServletResponse) Proxy.newProxyInstance(Captured.class.getClassLoader(),
                    new Class<?>[] { HttpServletResponse.class }, (p, method, args) -> switch (method.getName()) {
                    case "setStatus", "sendError" -> {
                        status = (Integer) args[0];
                        yield null;
                    }
                    case "getStatus" -> status;
                    case "getWriter" -> printWriter;
                    case "getCharacterEncoding" -> "UTF-8";
                    case "hashCode" -> System.identityHashCode(p);
                    case "equals" -> p == args[0];
                    default -> defaultValue(method.getReturnType());
                    });
        }
    }
}
