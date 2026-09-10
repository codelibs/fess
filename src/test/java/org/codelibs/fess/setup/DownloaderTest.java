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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.sun.net.httpserver.HttpServer;

public class DownloaderTest {

    @TempDir
    Path tempDir;

    private HttpServer server;

    private static final Downloader.Progress NO_PROGRESS = (bytes, total) -> {
        // progress is not reported
    };

    private static final byte[] BODY = "opensearch-payload".getBytes(StandardCharsets.UTF_8);

    @BeforeEach
    public void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/ok", exchange -> {
            exchange.sendResponseHeaders(200, BODY.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(BODY);
            }
        });
        server.createContext("/broken", exchange -> {
            exchange.sendResponseHeaders(500, -1);
            exchange.close();
        });
        server.createContext("/missing", exchange -> {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        });
        server.start();
    }

    @AfterEach
    public void stopServer() {
        server.stop(0);
    }

    private URI uri(final String path) {
        return URI.create("http://127.0.0.1:" + server.getAddress().getPort() + path);
    }

    @Test
    public void test_download_writesBody() throws Exception {
        final Path dest = tempDir.resolve("payload.bin");
        Downloader.download(uri("/ok"), dest);
        assertArrayEquals(BODY, Files.readAllBytes(dest));
    }

    @Test
    public void test_download_createsMissingParentDirectories() throws Exception {
        final Path dest = tempDir.resolve("nested").resolve("deeper").resolve("payload.bin");
        Downloader.download(uri("/ok"), dest);
        assertArrayEquals(BODY, Files.readAllBytes(dest));
    }

    @Test
    public void test_download_reportsProgress() throws Exception {
        final Path dest = tempDir.resolve("payload.bin");
        final long[] last = { -1L, -1L };
        Downloader.download(uri("/ok"), dest, (bytes, total) -> {
            last[0] = bytes;
            last[1] = total;
        });
        assertEquals(BODY.length, last[0]);
        assertEquals(BODY.length, last[1]);
    }

    @Test
    public void test_download_skipsWhenSizeMatches() throws Exception {
        final Path dest = tempDir.resolve("payload.bin");
        Files.write(dest, BODY);
        final long before = Files.getLastModifiedTime(dest).toMillis();
        Downloader.download(uri("/ok"), dest);
        assertEquals(before, Files.getLastModifiedTime(dest).toMillis());
    }

    @Test
    public void test_download_redownloadsWhenSizeDiffers() throws Exception {
        final Path dest = tempDir.resolve("payload.bin");
        Files.write(dest, "stale".getBytes(StandardCharsets.UTF_8));
        Downloader.download(uri("/ok"), dest);
        assertArrayEquals(BODY, Files.readAllBytes(dest));
    }

    @Test
    public void test_download_failsOnHttpError() {
        final Path dest = tempDir.resolve("payload.bin");
        final SetupException e = assertThrows(SetupException.class, () -> Downloader.download(uri("/missing"), dest));
        assertTrue(e.getMessage().contains("404"), e.getMessage());
    }

    @Test
    public void test_download_leavesNoPartFileBehind() throws Exception {
        final Path dest = tempDir.resolve("payload.bin");
        Downloader.download(uri("/ok"), dest);
        assertFalse(Files.exists(tempDir.resolve("payload.bin.part")), "the .part file must be renamed away");
    }

    @Test
    public void test_sha1_hashesTheFileContent() throws Exception {
        final Path file = tempDir.resolve("payload.bin");
        Files.writeString(file, "abc");
        assertEquals("a9993e364706816aba3e25717850c26c9cd0d89d", Downloader.sha1(file));
    }

    @Test
    public void test_verifySha1_acceptsTheDigestAsPublished() throws Exception {
        final Path file = tempDir.resolve("payload.bin");
        Files.writeString(file, "abc");
        Downloader.verifySha1(file, "a9993e364706816aba3e25717850c26c9cd0d89d");
    }

    @Test
    public void test_verifySha1_acceptsADigestFollowedByAFileName() throws Exception {
        final Path file = tempDir.resolve("payload.bin");
        Files.writeString(file, "abc");
        Downloader.verifySha1(file, "A9993E364706816ABA3E25717850C26C9CD0D89D  payload.bin\n");
    }

    @Test
    public void test_verifySha1_rejectsAMismatch() throws Exception {
        final Path file = tempDir.resolve("payload.bin");
        Files.writeString(file, "abc");
        final SetupException e =
                assertThrows(SetupException.class, () -> Downloader.verifySha1(file, "0000000000000000000000000000000000000000"));
        assertTrue(e.getMessage().contains("a9993e364706816aba3e25717850c26c9cd0d89d"));
        assertTrue(e.getMessage().contains("0000000000000000000000000000000000000000"));
    }

    @Test
    public void test_verifySha1_rejectsAChecksumThatIsNotADigest() throws Exception {
        final Path file = tempDir.resolve("payload.bin");
        Files.writeString(file, "abc");
        assertThrows(SetupException.class, () -> Downloader.verifySha1(file, "  "));
    }

    @Test
    public void test_downloadIfPresent_writesTheBody() throws Exception {
        final Path dest = tempDir.resolve("payload.bin");
        assertEquals(dest, Downloader.downloadIfPresent(uri("/ok"), dest, NO_PROGRESS));
        assertArrayEquals(BODY, Files.readAllBytes(dest));
    }

    @Test
    public void test_downloadIfPresent_returnsNullWhenTheServerHasNoSuchFile() throws Exception {
        final Path dest = tempDir.resolve("payload.bin");
        assertNull(Downloader.downloadIfPresent(uri("/missing"), dest, NO_PROGRESS));
        assertFalse(Files.exists(dest));
    }

    @Test
    public void test_downloadIfPresent_stillFailsOnAnErrorThatIsNotAMissingFile() throws Exception {
        final Path dest = tempDir.resolve("payload.bin");
        final SetupException e = assertThrows(SetupException.class, () -> Downloader.downloadIfPresent(uri("/broken"), dest, NO_PROGRESS));
        assertTrue(e.getMessage().contains("500"), e.getMessage());
    }

    @Test
    public void test_readStringIfPresent_readsTheBody() throws Exception {
        assertEquals("opensearch-payload", Downloader.readStringIfPresent(uri("/ok")));
    }

    @Test
    public void test_readStringIfPresent_returnsNullWhenThereIsNoSuchFile() throws Exception {
        assertNull(Downloader.readStringIfPresent(uri("/missing")));
    }

    @Test
    public void test_readStringIfPresent_stillFailsOnAnErrorThatIsNotAMissingFile() throws Exception {
        final SetupException e = assertThrows(SetupException.class, () -> Downloader.readStringIfPresent(uri("/broken")));
        assertTrue(e.getMessage().contains("500"), e.getMessage());
    }
}
