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
package org.codelibs.fess.setup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

/**
 * Covers which repositories a build of Fess reads, and which version it then picks.
 *
 * <p>The repositories are served from a loopback server so that the test can assert on what was
 * <em>not</em> requested: a released Fess must not so much as ask the snapshot repository.</p>
 */
public class PluginVersionResolutionTest {

    private static final String RELEASE_METADATA = """
            <?xml version="1.0" encoding="UTF-8"?>
            <metadata>
              <versioning>
                <versions>
                  <version>15.8.0</version>
                  <version>15.9.0</version>
                </versions>
              </versioning>
            </metadata>
            """;

    private static final String SNAPSHOT_METADATA = """
            <?xml version="1.0" encoding="UTF-8"?>
            <metadata>
              <versioning>
                <versions>
                  <version>15.9.0-SNAPSHOT</version>
                  <version>15.9.1-SNAPSHOT</version>
                </versions>
              </versioning>
            </metadata>
            """;

    private HttpServer server;

    private Set<String> requested;

    @BeforeEach
    public void startServer() throws IOException {
        requested = Collections.newSetFromMap(new ConcurrentHashMap<>());
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            final String path = exchange.getRequestURI().getPath();
            requested.add(path);
            final String body = switch (path) {
            case "/release/fess-ds-git/maven-metadata.xml" -> RELEASE_METADATA;
            case "/snapshot/fess-ds-git/maven-metadata.xml" -> SNAPSHOT_METADATA;
            default -> null;
            };
            if (body == null) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }
            final byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(bytes);
            }
        });
        server.start();
    }

    @AfterEach
    public void stopServer() {
        server.stop(0);
    }

    private PluginSources sources(final String releasePath, final String snapshotPath) {
        final String base = "http://127.0.0.1:" + server.getAddress().getPort();
        return new PluginSources(base + releasePath, base + snapshotPath, "");
    }

    @Test
    public void test_aDevelopmentBuildPrefersTheSnapshotOfItsOwnLine() throws Exception {
        assertEquals("15.9.1-SNAPSHOT", FessSetup.resolveVersion(sources("/release/", "/snapshot/"), "fess-ds-git", "15.9", true));
        assertTrue(requested.contains("/snapshot/fess-ds-git/maven-metadata.xml"), requested.toString());
    }

    @Test
    public void test_aReleasedBuildNeverAsksTheSnapshotRepository() throws Exception {
        assertEquals("15.9.0", FessSetup.resolveVersion(sources("/release/", "/snapshot/"), "fess-ds-git", "15.9", false));
        assertTrue(requested.contains("/release/fess-ds-git/maven-metadata.xml"), requested.toString());
        assertFalse(requested.contains("/snapshot/fess-ds-git/maven-metadata.xml"),
                "a released Fess must not read the snapshot repository at all: " + requested);
    }

    @Test
    public void test_aDevelopmentBuildInstallsAPluginThatOnlyTheSnapshotRepositoryHas() throws Exception {
        assertEquals("15.9.1-SNAPSHOT", FessSetup.resolveVersion(sources("/empty/", "/snapshot/"), "fess-ds-git", "15.9", true));
    }

    @Test
    public void test_aDevelopmentBuildFallsBackToAReleaseWhenThereIsNoSnapshot() throws Exception {
        assertEquals("15.9.0", FessSetup.resolveVersion(sources("/release/", "/empty/"), "fess-ds-git", "15.9", true));
    }

    @Test
    public void test_bothRepositoriesFailing_reportsEachOfThem() throws Exception {
        final SetupException e = assertThrows(SetupException.class,
                () -> FessSetup.resolveVersion(sources("/empty/", "/nowhere/"), "fess-ds-git", "15.9", true));
        assertTrue(e.getMessage().contains("/empty/fess-ds-git/maven-metadata.xml"), e.getMessage());
        assertTrue(e.getMessage().contains("/nowhere/fess-ds-git/maven-metadata.xml"), e.getMessage());
    }

    @Test
    public void test_aRepositoryTheDefinitionLeftEmptyIsSkippedRatherThanRequested() throws Exception {
        final String base = "http://127.0.0.1:" + server.getAddress().getPort();
        assertEquals("15.9.0", FessSetup.resolveVersion(new PluginSources(base + "/release/", "", ""), "fess-ds-git", "15.9", true));
        assertEquals(List.of("/release/fess-ds-git/maven-metadata.xml"), List.copyOf(requested));
    }
}
