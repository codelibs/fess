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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.sun.net.httpserver.HttpServer;

/**
 * {@code install plugin} and {@code install theme} take several names, and a name the repository
 * does not know must fail the command before any of the others has been downloaded.
 *
 * <p>The repository is a loopback server that publishes one snapshot of the plugin
 * {@code fess-script-ognl} and of the theme {@code docuforge} and nothing under any other name, so
 * asking for a snapshot of another name fails while its version is being resolved.</p>
 */
public class InstallResolvesBeforeDownloadingTest {

    private static final String VERSION = "15.9.0-SNAPSHOT";

    private static final String METADATA = """
            <?xml version="1.0" encoding="UTF-8"?>
            <metadata>
              <versioning>
                <snapshot>
                  <timestamp>20260901.000000</timestamp>
                  <buildNumber>1</buildNumber>
                </snapshot>
              </versioning>
            </metadata>
            """;

    private static final String THEME_MANIFEST = """
            apiVersion: fess.codelibs.org/v1
            kind: StaticTheme
            name: docuforge
            displayName: "DocuForge"
            version: "15.9.0"
            minFessVersion: "15.9"
            """;

    @TempDir
    Path tempDir;

    private HttpServer server;

    private Set<String> requested;

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    @BeforeEach
    public void startServer() throws IOException {
        requested = Collections.newSetFromMap(new ConcurrentHashMap<>());
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        final byte[] jar = "jar".getBytes(StandardCharsets.UTF_8);
        final byte[] zip = themeZip();
        server.createContext("/", exchange -> {
            final String path = exchange.getRequestURI().getPath();
            requested.add(path);
            final byte[] body;
            if (path.endsWith("/maven-metadata.xml") && (path.contains("/fess-script-ognl/") || path.contains("/docuforge/"))) {
                body = METADATA.getBytes(StandardCharsets.UTF_8);
            } else if (path.equals(artifact("fess-script-ognl", "jar"))) {
                body = jar;
            } else if (path.equals(artifact("docuforge", "zip"))) {
                body = zip;
            } else {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        });
        server.start();
    }

    private static String artifact(final String name, final String extension) {
        return "/repo/" + name + "/" + VERSION + "/" + name + "-15.9.0-20260901.000000-1." + extension;
    }

    private static byte[] themeZip() throws IOException {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(bytes)) {
            zip.putNextEntry(new ZipEntry(ThemeInstaller.MANIFEST));
            zip.write(THEME_MANIFEST.getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
            zip.putNextEntry(new ZipEntry("index.html"));
            zip.write("<html></html>".getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
        }
        return bytes.toByteArray();
    }

    @AfterEach
    public void stopServer() {
        server.stop(0);
    }

    private int run(final String... args) {
        return FessSetup.run(args, new PrintStream(out, true, StandardCharsets.UTF_8), new PrintStream(err, true, StandardCharsets.UTF_8));
    }

    private String repository() {
        return "http://127.0.0.1:" + server.getAddress().getPort() + "/repo";
    }

    private List<String> installed() throws IOException {
        try (Stream<Path> files = Files.list(tempDir)) {
            return files.map(path -> path.getFileName().toString()).toList();
        }
    }

    @Test
    public void test_installPlugin_installsEveryNameWhenAllResolve() throws Exception {
        assertEquals(0, run("install", "plugin", "fess-script-ognl:" + VERSION, "--repository", repository(), "--dest", tempDir.toString()),
                err.toString(StandardCharsets.UTF_8));
        assertEquals(List.of("fess-script-ognl-15.9.0-20260901.000000-1.jar"), installed());
    }

    @Test
    public void test_installPlugin_aNameThatDoesNotResolveInstallsNothing() throws Exception {
        final int exit = run("install", "plugin", "fess-script-ognl:" + VERSION, "fess-script-ognll:" + VERSION, "--repository",
                repository(), "--dest", tempDir.toString());

        assertEquals(1, exit);
        assertTrue(err.toString(StandardCharsets.UTF_8).contains("fess-script-ognll"), err.toString(StandardCharsets.UTF_8));
        assertEquals(List.of(), installed());
        assertFalse(requested.stream().anyMatch(path -> path.endsWith(".jar")), "no jar may be fetched: " + requested);
    }

    @Test
    public void test_installTheme_installsEveryNameWhenAllResolve() throws Exception {
        assertEquals(0, run("install", "theme", "docuforge:" + VERSION, "--repository", repository(), "--dest", tempDir.toString()),
                err.toString(StandardCharsets.UTF_8));
        assertEquals(List.of("docuforge"), installed());
    }

    @Test
    public void test_installTheme_aNameThatDoesNotResolveInstallsNothing() throws Exception {
        final int exit = run("install", "theme", "docuforge:" + VERSION, "no-such-theme:" + VERSION, "--repository", repository(), "--dest",
                tempDir.toString());

        assertEquals(1, exit);
        assertTrue(err.toString(StandardCharsets.UTF_8).contains("no-such-theme"), err.toString(StandardCharsets.UTF_8));
        assertEquals(List.of(), installed());
        assertFalse(requested.stream().anyMatch(path -> path.endsWith(".zip")), "no archive may be fetched: " + requested);
    }
}
