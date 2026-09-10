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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.sun.net.httpserver.HttpServer;

/**
 * Covers the download half of installing a plugin jar: which source is used, what happens when
 * one of them does not have the file, and the checksum.
 */
public class PluginJarInstallTest {

    private static final byte[] GITHUB_JAR = "jar-from-github".getBytes(StandardCharsets.UTF_8);

    private static final byte[] MAVEN_JAR = "jar-from-maven".getBytes(StandardCharsets.UTF_8);

    @TempDir
    Path tempDir;

    private HttpServer server;

    private ByteArrayOutputStream captured;

    private PrintStream out;

    @BeforeEach
    public void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        serve("/github.jar", GITHUB_JAR);
        serve("/maven.jar", MAVEN_JAR);
        // The same jar as the repository's, but publishing no .sha1 of its own -- which is what
        // a GitHub release asset is.
        server.createContext("/mirror.jar", exchange -> {
            exchange.sendResponseHeaders(200, MAVEN_JAR.length);
            try (OutputStream stream = exchange.getResponseBody()) {
                stream.write(MAVEN_JAR);
            }
        });
        server.createContext("/missing", exchange -> {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        });
        server.createContext("/broken", exchange -> {
            exchange.sendResponseHeaders(500, -1);
            exchange.close();
        });
        server.start();
        captured = new ByteArrayOutputStream();
        out = new PrintStream(captured, true, StandardCharsets.UTF_8);
    }

    @AfterEach
    public void stopServer() {
        server.stop(0);
    }

    private void serve(final String path, final byte[] body) {
        server.createContext(path, exchange -> {
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream stream = exchange.getResponseBody()) {
                stream.write(body);
            }
        });
        final String sha1 = sha1Of(body);
        server.createContext(path + ".sha1", exchange -> {
            final byte[] digest = sha1.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, digest.length);
            try (OutputStream stream = exchange.getResponseBody()) {
                stream.write(digest);
            }
        });
    }

    private static String sha1Of(final byte[] body) {
        try {
            final Path file = Files.createTempFile("sha1", ".bin");
            try {
                Files.write(file, body);
                return Downloader.sha1(file);
            } finally {
                Files.deleteIfExists(file);
            }
        } catch (final IOException | SetupException e) {
            throw new IllegalStateException(e);
        }
    }

    private String url(final String path) {
        return "http://127.0.0.1:" + server.getAddress().getPort() + path;
    }

    private String out() {
        return captured.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void test_install_takesTheFirstSourceThatHasTheJarAndChecksItAgainstTheRepository() throws Exception {
        final Path jar = tempDir.resolve("fess-ds-git-15.9.0.jar");
        final String used = FessPluginInstaller.install(List.of(url("/mirror.jar"), url("/maven.jar")), url("/maven.jar.sha1"), jar, out,
                (bytes, total) -> {
                    // progress is not reported
                });
        assertEquals(url("/mirror.jar"), used);
        assertEquals("jar-from-maven", Files.readString(jar));
        assertFalse(out().contains("warning"),
                "the checksum has to be the repository's: the source that served the jar publishes none. " + out());
    }

    @Test
    public void test_install_fallsBackWhenTheFirstSourceDoesNotHaveIt() throws Exception {
        final Path jar = tempDir.resolve("fess-ds-git-15.9.0.jar");
        final String used = FessPluginInstaller.install(List.of(url("/missing"), url("/maven.jar")), url("/maven.jar.sha1"), jar, out,
                (bytes, total) -> {
                    // progress is not reported
                });
        assertEquals(url("/maven.jar"), used);
        assertEquals("jar-from-maven", Files.readString(jar));
    }

    @Test
    public void test_install_reportsASourceThatFailedForAnotherReason() throws Exception {
        final Path jar = tempDir.resolve("fess-ds-git-15.9.0.jar");
        FessPluginInstaller.install(List.of(url("/broken"), url("/maven.jar")), url("/maven.jar.sha1"), jar, out, (bytes, total) -> {
            // progress is not reported
        });
        assertEquals("jar-from-maven", Files.readString(jar));
        assertTrue(out().contains("500"), out());
    }

    @Test
    public void test_install_failsWhenNoSourceHasTheJar() throws Exception {
        final Path jar = tempDir.resolve("fess-ds-git-15.9.0.jar");
        final SetupException e = assertThrows(SetupException.class, () -> FessPluginInstaller
                .install(List.of(url("/missing/first"), url("/missing/last")), url("/maven.jar.sha1"), jar, out, (bytes, total) -> {
                    // progress is not reported
                }));
        assertTrue(e.getMessage().contains(url("/missing/last")), "the failure has to name the source that was tried last: " + e);
        assertFalse(Files.exists(jar));
    }

    @Test
    public void test_install_rejectsAJarThatDoesNotMatchThePublishedChecksum() throws Exception {
        final Path jar = tempDir.resolve("fess-ds-git-15.9.0.jar");
        final SetupException e = assertThrows(SetupException.class,
                () -> FessPluginInstaller.install(List.of(url("/github.jar")), url("/maven.jar.sha1"), jar, out, (bytes, total) -> {
                    // progress is not reported
                }));
        assertTrue(e.getMessage().contains("Checksum mismatch"), e.getMessage());
        assertFalse(Files.exists(jar), "a jar that failed its checksum must not stay installed");
    }

    @Test
    public void test_install_warnsButKeepsGoingWhenNoChecksumIsPublished() throws Exception {
        final Path jar = tempDir.resolve("fess-ds-git-15.9.0.jar");
        FessPluginInstaller.install(List.of(url("/maven.jar")), url("/missing"), jar, out, (bytes, total) -> {
            // progress is not reported
        });
        assertEquals("jar-from-maven", Files.readString(jar));
        assertTrue(out().contains("was not verified"), out());
    }

    @Test
    public void test_install_leavesTheInstalledJarAloneWhenTheDownloadFailsItsChecksum() throws Exception {
        final Path jar = tempDir.resolve("fess-ds-git-15.9.0.jar");
        Files.writeString(jar, "the jar that is already installed");
        assertThrows(SetupException.class,
                () -> FessPluginInstaller.install(List.of(url("/github.jar")), url("/maven.jar.sha1"), jar, out, (bytes, total) -> {
                    // progress is not reported
                }));
        assertEquals("the jar that is already installed", Files.readString(jar),
                "a download that fails its checksum must not replace the plugin that was working");
    }

    @Test
    public void test_install_leavesNoTemporaryFileBehindWhenTheChecksumFails() throws Exception {
        final Path jar = tempDir.resolve("fess-ds-git-15.9.0.jar");
        assertThrows(SetupException.class,
                () -> FessPluginInstaller.install(List.of(url("/github.jar")), url("/maven.jar.sha1"), jar, out, (bytes, total) -> {
                    // progress is not reported
                }));
        try (Stream<Path> entries = Files.list(tempDir)) {
            assertEquals(List.of(), entries.map(path -> path.getFileName().toString()).sorted().toList());
        }
    }

    @Test
    public void test_install_failsWhenTheChecksumCannotBeRead() throws Exception {
        final Path jar = tempDir.resolve("fess-ds-git-15.9.0.jar");
        final SetupException e = assertThrows(SetupException.class,
                () -> FessPluginInstaller.install(List.of(url("/maven.jar")), url("/broken"), jar, out, (bytes, total) -> {
                    // progress is not reported
                }));
        assertTrue(e.getMessage().contains("500"), e.getMessage());
        assertFalse(Files.exists(jar), "only a checksum that is not published may be skipped, not one that failed to load");
    }

    @Test
    public void test_install_startsItsWarningsOnALineOfTheirOwn() throws Exception {
        final Path jar = tempDir.resolve("fess-ds-git-15.9.0.jar");
        FessPluginInstaller.install(List.of(url("/maven.jar")), url("/missing"), jar, out, (bytes, total) -> {
            // progress is not reported
        });
        assertTrue(out().contains("\n  warning:"), "the progress line has to be terminated first: " + out());
    }
}
