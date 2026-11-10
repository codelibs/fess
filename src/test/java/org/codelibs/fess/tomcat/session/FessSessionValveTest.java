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
package org.codelibs.fess.tomcat.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.util.StandardSessionIdGenerator;
import org.apache.catalina.valves.ValveBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.ServletException;

public class FessSessionValveTest {

    private File baseDir;

    private FakeSessionStore store;

    private StandardContext context;

    private FessSessionManager manager;

    private FessSessionValve valve;

    @BeforeEach
    public void setUp() throws Exception {
        baseDir = Files.createTempDirectory("fess-valve-test").toFile();
        final Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(baseDir.getAbsolutePath());
        context = (StandardContext) tomcat.addContext("", baseDir.getAbsolutePath());
        store = new FakeSessionStore();
        manager = new FessSessionManager();
        manager.setSessionIdGenerator(new StandardSessionIdGenerator());
        context.setManager(manager);
        context.getServletContext().setAttribute(FessSessionManager.BRIDGE_ATTRIBUTE, store.bridge());
        valve = new FessSessionValve();
    }

    @AfterEach
    public void tearDown() {
        baseDir.delete();
    }

    private Request newRequest() {
        final Request request = new Request(new Connector(), new org.apache.coyote.Request());
        request.getMappingData().context = context;
        return request;
    }

    private void invoke(final Request request, final ValveBase next) throws IOException, ServletException {
        valve.setNext(next);
        valve.invoke(request, new Response(new org.apache.coyote.Response()));
    }

    private static ValveBase chain(final ThrowingRunnable body) {
        return new ValveBase() {
            @Override
            public void invoke(final Request request, final Response response) throws IOException, ServletException {
                body.run();
            }
        };
    }

    interface ThrowingRunnable {
        void run() throws IOException, ServletException;
    }

    @Test
    public void test_supportsAsync() {
        assertTrue(valve.isAsyncSupported());
    }

    @Test
    public void test_savesTouchedSessionAfterChain() throws Exception {
        final String[] id = new String[1];
        invoke(newRequest(), chain(() -> {
            final FessSession session = (FessSession) manager.createSession(null);
            session.access();
            session.setAttribute("user", "alice");
            id[0] = session.getId();
            assertTrue(store.docs.isEmpty(), "nothing is written while the chain runs");
        }));
        assertTrue(store.docs.containsKey(id[0]));
    }

    @Test
    public void test_requestWithoutSessionUse_doesNotCallStore() throws Exception {
        invoke(newRequest(), chain(() -> {}));
        assertTrue(store.calls.isEmpty());
    }

    @Test
    public void test_savesEvenWhenChainThrows() throws Exception {
        final String[] id = new String[1];
        assertThrows(ServletException.class, () -> invoke(newRequest(), chain(() -> {
            final FessSession session = (FessSession) manager.createSession(null);
            session.access();
            session.setAttribute("k", "v");
            id[0] = session.getId();
            throw new ServletException("boom");
        })));
        assertTrue(store.docs.containsKey(id[0]));
    }

    @Test
    public void test_storeFailure_doesNotBreakResponse() throws Exception {
        store.down = true;
        invoke(newRequest(), chain(() -> {
            final FessSession session = (FessSession) manager.createSession(null);
            session.access();
            session.setAttribute("k", "v");
        }));
        assertTrue(store.docs.isEmpty());
    }

    @Test
    public void test_invalidatedSession_isNotWrittenBack() throws Exception {
        final String[] id = new String[1];
        invoke(newRequest(), chain(() -> {
            final FessSession session = (FessSession) manager.createSession(null);
            session.access();
            session.setAttribute("k", "v");
            id[0] = session.getId();
        }));
        assertTrue(store.docs.containsKey(id[0]));
        invoke(newRequest(), chain(() -> {
            try {
                final FessSession session = (FessSession) manager.findSession(id[0]);
                session.access();
                session.invalidate();
            } catch (final IOException e) {
                throw new IllegalStateException(e);
            }
        }));
        assertFalse(store.docs.containsKey(id[0]));
    }

    @Test
    public void test_otherManager_isPassedThrough() throws Exception {
        final StandardContext plain = (StandardContext) new Tomcat().addContext("/plain", baseDir.getAbsolutePath());
        final Request request = new Request(new Connector(), new org.apache.coyote.Request());
        request.getMappingData().context = plain;
        final boolean[] called = new boolean[1];
        invoke(request, chain(() -> called[0] = true));
        assertTrue(called[0]);
        assertEquals(0, store.calls.size());
    }
}
