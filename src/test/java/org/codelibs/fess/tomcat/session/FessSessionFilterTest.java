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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;

public class FessSessionFilterTest {

    private File baseDir;

    private FakeSessionStore store;

    private StandardContext context;

    private FessSessionManager manager;

    private FessSessionValve valve;

    private FessSessionFilter filter;

    /** What the client has received, and whether the session was already in the store when each part arrived. */
    private final List<String> sent = new ArrayList<>();

    private BooleanSupplier stored = () -> false;

    @BeforeEach
    public void setUp() throws Exception {
        baseDir = Files.createTempDirectory("fess-filter-test").toFile();
        final Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(baseDir.getAbsolutePath());
        context = (StandardContext) tomcat.addContext("", baseDir.getAbsolutePath());
        store = new FakeSessionStore();
        manager = new FessSessionManager();
        manager.setSessionIdGenerator(new StandardSessionIdGenerator());
        context.setManager(manager);
        context.getServletContext().setAttribute(FessSessionManager.BRIDGE_ATTRIBUTE, store.bridge());
        valve = new FessSessionValve();
        filter = new FessSessionFilter(manager);
    }

    @AfterEach
    public void tearDown() {
        baseDir.delete();
    }

    /** A response that records, for everything the client would receive, whether the session was stored by then. */
    private HttpServletResponse clientSide() {
        final Writer sink = new Writer() {
            @Override
            public void write(final char[] buf, final int off, final int len) {
                sent.add("write:" + stored.getAsBoolean());
            }

            @Override
            public void flush() {
                sent.add("flush:" + stored.getAsBoolean());
            }

            @Override
            public void close() {
                sent.add("close:" + stored.getAsBoolean());
            }
        };
        final PrintWriter writer = new PrintWriter(sink);
        final ServletOutputStream out = new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(final WriteListener writeListener) {
            }

            @Override
            public void write(final int b) {
                sent.add("bytes:" + stored.getAsBoolean());
            }
        };
        return (HttpServletResponse) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { HttpServletResponse.class },
                (proxy, method, args) -> {
                    switch (method.getName()) {
                    case "getWriter":
                        return writer;
                    case "getOutputStream":
                        return out;
                    case "flushBuffer", "sendRedirect", "sendError":
                        sent.add(method.getName() + ":" + stored.getAsBoolean());
                        return null;
                    default:
                        return method.getReturnType() == boolean.class ? Boolean.FALSE : null;
                    }
                });
    }

    private interface Handler {
        void handle(HttpServletResponse response, FessSession session) throws IOException;
    }

    /** Runs the valve around the filter around a handler that touches a new session and sets an attribute. */
    private String run(final Handler handler) throws IOException, ServletException {
        final String[] id = new String[1];
        final FilterChain chain = (req, res) -> {
            final FessSession session = (FessSession) manager.createSession(null);
            session.access();
            session.setAttribute("user", "alice");
            id[0] = session.getId();
            stored = () -> store.docs.containsKey(id[0]);
            handler.handle((HttpServletResponse) res, session);
        };
        final Request request = new Request(new Connector(), new org.apache.coyote.Request());
        request.getMappingData().context = context;
        valve.setNext(new ValveBase() {
            @Override
            public void invoke(final Request req, final Response res) throws IOException, ServletException {
                filter.doFilter(req, clientSide(), chain);
            }
        });
        valve.invoke(request, new Response(new org.apache.coyote.Response()));
        return id[0];
    }

    @Test
    public void test_writer_savesBeforeTheFirstByteIsSent() throws Exception {
        final String id = run((res, session) -> {
            res.getWriter().write("{}");
            res.getWriter().write("more");
        });
        assertEquals(List.of("write:true", "write:true"), sent);
        assertTrue(store.docs.containsKey(id));
    }

    @Test
    public void test_writer_closeAloneAlsoSaves() throws Exception {
        run((res, session) -> res.getWriter().close());
        assertEquals(List.of("close:true"), sent);
    }

    @Test
    public void test_writer_printAndPrintlnSave() throws Exception {
        run((res, session) -> res.getWriter().println());
        assertEquals("write:true", sent.get(0));
        sent.clear();
        store.docs.clear();
        run((res, session) -> res.getWriter().print("x"));
        assertEquals("write:true", sent.get(0));
    }

    @Test
    public void test_outputStream_savesBeforeTheFirstByteIsSent() throws Exception {
        run((res, session) -> res.getOutputStream().write(new byte[] { 1, 2, 3 }));
        assertTrue(sent.get(0).endsWith(":true"));
        assertTrue(sent.stream().allMatch(s -> s.equals("bytes:true")));
    }

    @Test
    public void test_flushBuffer_redirect_andError_save() throws Exception {
        run((res, session) -> res.flushBuffer());
        assertEquals(List.of("flushBuffer:true"), sent);
        sent.clear();
        run((res, session) -> res.sendRedirect("/x"));
        assertEquals(List.of("sendRedirect:true"), sent);
        sent.clear();
        run((res, session) -> res.sendError(404));
        assertEquals(List.of("sendError:true"), sent);
        sent.clear();
        run((res, session) -> res.sendError(500, "boom"));
        assertEquals(List.of("sendError:true"), sent);
    }

    @Test
    public void test_savesOnlyOncePerRequest() throws Exception {
        run((res, session) -> {
            res.getWriter().write("a");
            res.getWriter().write("b");
            res.flushBuffer();
        });
        // one save before the response, one by the valve after it (the session is unchanged, so no write)
        assertEquals(2, store.count("save"));
        assertEquals(1, store.fullWrites);
        assertEquals(0, store.touches);
    }

    @Test
    public void test_changeAfterTheResponseIsSavedByTheValve() throws Exception {
        final String id = run((res, session) -> {
            res.getWriter().write("a");
            session.setAttribute("late", "value");
        });
        assertTrue(store.docs.containsKey(id));
        assertEquals("value", ((java.util.Map<?, ?>) store.docs.get(id).get("attributes")).get("late"));
    }

    @Test
    public void test_requestWithoutASession_doesNotCallTheStore() throws Exception {
        final Request request = new Request(new Connector(), new org.apache.coyote.Request());
        request.getMappingData().context = context;
        valve.setNext(new ValveBase() {
            @Override
            public void invoke(final Request req, final Response res) throws IOException, ServletException {
                filter.doFilter(req, clientSide(), (rq, rs) -> {
                    rs.getWriter().write("x");
                    rs.getWriter().close();
                });
            }
        });
        valve.invoke(request, new Response(new org.apache.coyote.Response()));
        assertFalse(sent.isEmpty());
        assertTrue(store.calls.isEmpty());
    }

    @Test
    public void test_storeFailure_doesNotStopTheResponse() throws Exception {
        store.down = true;
        run((res, session) -> res.getWriter().write("x"));
        assertEquals(List.of("write:false"), sent);
    }
}
