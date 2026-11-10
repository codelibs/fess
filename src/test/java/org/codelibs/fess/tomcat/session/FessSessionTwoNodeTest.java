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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.util.StandardSessionIdGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Runs two real embedded Tomcat nodes that share one fake store, driven over HTTP with a single cookie.
 */
public class FessSessionTwoNodeTest {

    private final FakeSessionStore store = new FakeSessionStore();

    private Tomcat nodeA;

    private Tomcat nodeB;

    private File baseDir;

    private Tomcat start(final String name) throws Exception {
        final File dir = new File(baseDir, name);
        dir.mkdirs();
        final Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(dir.getAbsolutePath());
        tomcat.setPort(0);
        tomcat.getConnector();
        final Context context = tomcat.addContext("", dir.getAbsolutePath());
        final FessSessionManager manager = new FessSessionManager();
        manager.setSessionIdGenerator(new StandardSessionIdGenerator());
        context.setManager(manager);
        context.getPipeline().addValve(new FessSessionValve());
        Tomcat.addServlet(context, "s", new HttpServlet() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
                final String op = req.getParameter("op");
                final HttpSession session = req.getSession(!"read".equals(op));
                String body = "none";
                if (session != null) {
                    if ("set".equals(op)) {
                        session.setAttribute("user", req.getParameter("v"));
                    } else if ("logout".equals(op)) {
                        session.invalidate();
                        body = "bye";
                    } else if ("regenerate".equals(op)) {
                        final java.util.Map<String, Object> copy = new java.util.HashMap<>();
                        java.util.Collections.list(session.getAttributeNames()).forEach(n -> copy.put(n, session.getAttribute(n)));
                        session.invalidate();
                        final HttpSession renewed = req.getSession(true);
                        copy.forEach(renewed::setAttribute);
                        body = String.valueOf(renewed.getAttribute("user"));
                        resp.setContentType("text/plain");
                        resp.getWriter().print(body);
                        return;
                    } else if ("rotate".equals(op)) {
                        req.changeSessionId();
                    }
                    if (!"logout".equals(op)) {
                        body = String.valueOf(session.getAttribute("user"));
                    }
                }
                resp.setContentType("text/plain");
                resp.getWriter().print(body);
            }
        });
        context.addServletMappingDecoded("/*", "s");
        tomcat.start();
        context.getServletContext().setAttribute(FessSessionManager.BRIDGE_ATTRIBUTE, store.bridge());
        return tomcat;
    }

    @BeforeEach
    public void setUp() throws Exception {
        baseDir = Files.createTempDirectory("fess-two-node").toFile();
        nodeA = start("a");
        nodeB = start("b");
    }

    @AfterEach
    public void tearDown() throws Exception {
        nodeA.stop();
        nodeA.destroy();
        nodeB.stop();
        nodeB.destroy();
    }

    private String[] get(final Tomcat node, final String query, final String cookie) throws IOException {
        final int port = node.getConnector().getLocalPort();
        final HttpURLConnection con = (HttpURLConnection) URI.create("http://localhost:" + port + "/?" + query).toURL().openConnection();
        if (cookie != null) {
            con.setRequestProperty("Cookie", cookie);
        }
        final String body = new String(con.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String set = null;
        for (final String header : con.getHeaderFields().getOrDefault("Set-Cookie", java.util.List.of())) {
            if (header.startsWith("JSESSIONID=")) {
                set = header.substring(0, header.indexOf(';'));
            }
        }
        return new String[] { body, set };
    }

    @Test
    public void test_loginOnOneNode_isSeenOnTheOther_andLogoutPropagates() throws Exception {
        final String[] login = get(nodeA, "op=set&v=alice", null);
        assertEquals("alice", login[0]);
        final String cookie = login[1];
        assertTrue(cookie != null && cookie.startsWith("JSESSIONID="));

        assertEquals("alice", get(nodeB, "op=read", cookie)[0]);

        assertEquals("bye", get(nodeB, "op=logout", cookie)[0]);
        assertEquals("none", get(nodeA, "op=read", cookie)[0]);
        assertEquals("none", get(nodeB, "op=read", cookie)[0]);
    }

    @Test
    public void test_changeOnOneNode_reachesTheOther() throws Exception {
        final String cookie = get(nodeA, "op=set&v=alice", null)[1];
        assertEquals("alice", get(nodeB, "op=read", cookie)[0]);
        assertEquals("bob", get(nodeB, "op=set&v=bob", cookie)[0]);
        assertEquals("bob", get(nodeA, "op=read", cookie)[0]);
    }

    @Test
    public void test_changeSessionId_movesTheSessionAndDropsTheOldId() throws Exception {
        final String cookie = get(nodeA, "op=set&v=alice", null)[1];
        final String[] rotated = get(nodeA, "op=rotate", cookie);
        assertEquals("alice", rotated[0]);
        assertTrue(rotated[1] != null && !rotated[1].equals(cookie));
        assertEquals("alice", get(nodeB, "op=read", rotated[1])[0]);
        assertEquals("none", get(nodeB, "op=read", cookie)[0]);
    }

    @Test
    public void test_regenerateSessionId_movesTheSessionAndDropsTheOldId() throws Exception {
        final String cookie = get(nodeA, "op=set&v=alice", null)[1];
        final String[] renewed = get(nodeB, "op=regenerate", cookie);
        assertEquals("alice", renewed[0]);
        assertTrue(renewed[1] != null && !renewed[1].equals(cookie));
        assertEquals("alice", get(nodeA, "op=read", renewed[1])[0]);
        assertEquals("none", get(nodeA, "op=read", cookie)[0]);
        assertEquals(1, store.docs.size());
    }
}
