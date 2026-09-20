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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.sun.net.httpserver.HttpServer;

/**
 * Covers installing a static theme from a repository: what is fetched, what is checked before
 * anything installed is touched, and what is left behind when a step fails.
 */
public class ThemeInstallerTest {

    private static final String MANIFEST = """
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

    private String repository;

    private ByteArrayOutputStream captured;

    private PrintStream out;

    @BeforeEach
    public void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.start();
        repository = "http://127.0.0.1:" + server.getAddress().getPort() + "/themes/";
        captured = new ByteArrayOutputStream();
        out = new PrintStream(captured, true, StandardCharsets.UTF_8);
    }

    @AfterEach
    public void stopServer() {
        server.stop(0);
    }

    /** Publishes a theme archive and its checksum at the coordinates the installer asks for. */
    private void publish(final String name, final String version, final byte[] zip) {
        publish(name, version, zip, sha1Of(zip));
    }

    private void publish(final String name, final String version, final byte[] zip, final String checksum) {
        final String path = "/themes/" + name + "/" + version + "/" + name + "-" + version + ".zip";
        serve(path, zip);
        serve(path + ".sha1", checksum.getBytes(StandardCharsets.UTF_8));
    }

    private void serve(final String path, final byte[] body) {
        server.createContext(path, exchange -> {
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream stream = exchange.getResponseBody()) {
                stream.write(body);
            }
        });
    }

    private static byte[] zipOf(final Map<String, String> entries) {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(bytes)) {
            for (final Map.Entry<String, String> entry : entries.entrySet()) {
                zip.putNextEntry(new ZipEntry(entry.getKey()));
                zip.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
                zip.closeEntry();
            }
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
        return bytes.toByteArray();
    }

    private static byte[] themeZip() {
        return zipOf(Map.of(ThemeInstaller.MANIFEST, MANIFEST, "index.html", "<html></html>", "assets/app.js", "console.log(1);"));
    }

    private static String sha1Of(final byte[] body) {
        try {
            final StringBuilder hex = new StringBuilder();
            for (final byte b : MessageDigest.getInstance("SHA-1").digest(body)) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private String install(final String name, final String version) throws SetupException {
        return ThemeInstaller.install(repository, name, version, version, tempDir, out, (bytes, total) -> {});
    }

    private static List<String> dotDirs(final Path directory, final String prefix) throws IOException {
        try (var entries = Files.list(directory)) {
            return entries.map(p -> p.getFileName().toString()).filter(n -> n.startsWith(prefix)).sorted().toList();
        }
    }

    @Test
    public void test_install_extractsIntoADirectoryNamedForTheTheme() throws Exception {
        publish("docuforge", "15.9.0", themeZip());

        final String used = install("docuforge", "15.9.0");

        assertTrue(used.endsWith("/themes/docuforge/15.9.0/docuforge-15.9.0.zip"), used);
        assertTrue(Files.isDirectory(tempDir.resolve("docuforge")));
        assertTrue(Files.isRegularFile(tempDir.resolve("docuforge/theme.yml")));
        assertEquals("<html></html>", Files.readString(tempDir.resolve("docuforge/index.html")));
        assertEquals("console.log(1);", Files.readString(tempDir.resolve("docuforge/assets/app.js")));
        // The archive itself is not left in the themes directory: ThemeRegistry scans this
        // directory, and a stray zip beside the themes is clutter Fess never removes.
        assertEquals(List.of("docuforge"), ThemeInstaller.installed(tempDir));
        assertTrue(dotDirs(tempDir, ".staging-").isEmpty(), "staging directory was left behind");
    }

    @Test
    public void test_install_overAnInstalledTheme_keepsTheOldOneInTheAttic() throws Exception {
        publish("docuforge", "15.9.0", themeZip());
        install("docuforge", "15.9.0");
        Files.writeString(tempDir.resolve("docuforge/marker.txt"), "first install");

        install("docuforge", "15.9.0");

        assertFalse(Files.exists(tempDir.resolve("docuforge/marker.txt")), "the new theme was merged into the old one");
        final List<String> attic = dotDirs(tempDir, ".attic-docuforge-");
        assertEquals(1, attic.size(), attic.toString());
        assertEquals("first install", Files.readString(tempDir.resolve(attic.get(0)).resolve("marker.txt")));
    }

    @Test
    public void test_install_resetsTheAtticTimestamp_soRetentionStartsNow() throws Exception {
        publish("docuforge", "15.9.0", themeZip());
        install("docuforge", "15.9.0");
        // A theme installed long ago. Files.move keeps this timestamp, so without a reset the
        // backup would arrive in the attic already past the retention period and be swept on
        // the next start -- the one case where a backup is useless.
        final Instant old = Instant.now().minus(Duration.ofDays(400));
        Files.setLastModifiedTime(tempDir.resolve("docuforge"), FileTime.from(old));

        install("docuforge", "15.9.0");

        final Path attic = tempDir.resolve(dotDirs(tempDir, ".attic-docuforge-").get(0));
        final Instant stamped = Files.getLastModifiedTime(attic).toInstant();
        assertTrue(stamped.isAfter(Instant.now().minus(Duration.ofMinutes(5))), "attic kept the old timestamp: " + stamped);
    }

    @Test
    public void test_install_manifestNamesADifferentTheme_isRefused() throws Exception {
        publish("voicebox", "15.9.0", themeZip());

        final SetupException e = assertThrows(SetupException.class, () -> install("voicebox", "15.9.0"));

        assertTrue(e.getMessage().contains("docuforge"), e.getMessage());
        assertFalse(Files.exists(tempDir.resolve("voicebox")));
        assertTrue(dotDirs(tempDir, ".staging-").isEmpty(), "staging directory was left behind");
    }

    @Test
    public void test_install_archiveWithoutAManifest_isRefused() throws Exception {
        publish("docuforge", "15.9.0", zipOf(Map.of("index.html", "<html></html>")));

        final SetupException e = assertThrows(SetupException.class, () -> install("docuforge", "15.9.0"));

        assertTrue(e.getMessage().contains("theme.yml"), e.getMessage());
        assertFalse(Files.exists(tempDir.resolve("docuforge")));
        assertTrue(dotDirs(tempDir, ".staging-").isEmpty());
    }

    @Test
    public void test_install_badChecksum_leavesTheInstalledThemeAlone() throws Exception {
        publish("docuforge", "15.9.0", themeZip());
        install("docuforge", "15.9.0");
        Files.writeString(tempDir.resolve("docuforge/marker.txt"), "still here");
        server.removeContext("/themes/docuforge/15.9.0/docuforge-15.9.0.zip.sha1");
        serve("/themes/docuforge/15.9.0/docuforge-15.9.0.zip.sha1", "0".repeat(40).getBytes(StandardCharsets.UTF_8));

        assertThrows(SetupException.class, () -> install("docuforge", "15.9.0"));

        assertEquals("still here", Files.readString(tempDir.resolve("docuforge/marker.txt")));
        assertTrue(dotDirs(tempDir, ".attic-").isEmpty(), "a failed install moved the theme aside");
        assertTrue(dotDirs(tempDir, ".staging-").isEmpty());
    }

    @Test
    public void test_install_overASymbolicLink_isRefused() throws Exception {
        publish("docuforge", "15.9.0", themeZip());
        final Path outside = Files.createDirectory(tempDir.resolveSibling("outside-" + System.nanoTime()));
        Files.createSymbolicLink(tempDir.resolve("docuforge"), outside);

        final SetupException e = assertThrows(SetupException.class, () -> install("docuforge", "15.9.0"));

        assertTrue(e.getMessage().contains("symbolic link"), e.getMessage());
    }

    @Test
    public void test_remove_movesTheThemeIntoTheAttic() throws Exception {
        publish("docuforge", "15.9.0", themeZip());
        install("docuforge", "15.9.0");

        final Path attic = ThemeInstaller.remove(tempDir, "docuforge");

        assertNotNull(attic);
        assertFalse(Files.exists(tempDir.resolve("docuforge")));
        assertTrue(Files.isRegularFile(attic.resolve("theme.yml")));
        assertEquals(List.of(), ThemeInstaller.installed(tempDir));
    }

    @Test
    public void test_remove_themeThatIsNotInstalled_returnsNull() throws Exception {
        assertNull(ThemeInstaller.remove(tempDir, "docuforge"));
    }

    @Test
    public void test_installed_countsOnlyDirectoriesWithAManifest() throws Exception {
        Files.createDirectories(tempDir.resolve("docuforge"));
        Files.writeString(tempDir.resolve("docuforge/theme.yml"), MANIFEST);
        Files.createDirectories(tempDir.resolve("notatheme"));
        Files.createDirectories(tempDir.resolve(".attic-docuforge-1-abcdefgh"));
        Files.writeString(tempDir.resolve(".attic-docuforge-1-abcdefgh/theme.yml"), MANIFEST);

        assertEquals(List.of("docuforge"), ThemeInstaller.installed(tempDir));
        assertEquals("15.9.0", ThemeInstaller.installedVersion(tempDir, "docuforge"));
        assertNull(ThemeInstaller.installedVersion(tempDir, "notatheme"));
    }

    @Test
    public void test_readField_readsTopLevelScalars() throws Exception {
        final Path manifest = tempDir.resolve("theme.yml");
        Files.writeString(manifest, MANIFEST);

        assertEquals("docuforge", ThemeInstaller.readField(manifest, "name"));
        assertEquals("15.9.0", ThemeInstaller.readField(manifest, "version"));
        // displayName starts with a different word, so asking for "name" must not return it.
        assertEquals("DocuForge", ThemeInstaller.readField(manifest, "displayName"));
        assertNull(ThemeInstaller.readField(manifest, "entry"));
    }

    @Test
    public void test_readField_ignoresNestedKeysOfTheSameName() throws Exception {
        final Path manifest = tempDir.resolve("theme.yml");
        Files.writeString(manifest, "author:\n  name: someone\nname: docuforge\n");

        assertEquals("docuforge", ThemeInstaller.readField(manifest, "name"));
    }

    @Test
    public void test_namesFromIndex_readsOneNamePerLine() throws Exception {
        final String index = """
                docuforge
                voicebox

                docuforge
                """;

        assertEquals(List.of("docuforge", "voicebox"), ThemeInstaller.namesFromIndex(index));
    }

    @Test
    public void test_namesFromIndex_keepsTheLastLineWithoutATrailingNewline() throws Exception {
        assertEquals(List.of("docuforge", "voicebox"), ThemeInstaller.namesFromIndex("docuforge\nvoicebox"));
    }

    @Test
    public void test_namesFromIndex_refusesAnythingThatIsNotAnIndex() {
        // A proxy page or an error document answering 200 would otherwise be read as a
        // catalogue of themes whose names are fragments of HTML.
        assertThrows(SetupException.class, () -> ThemeInstaller.namesFromIndex("<html><body>nope</body></html>"));
        assertThrows(SetupException.class, () -> ThemeInstaller.namesFromIndex("docuforge\n../escape"));
        assertThrows(SetupException.class, () -> ThemeInstaller.namesFromIndex("DocuForge"));
    }

    @Test
    public void test_indexUrl_sitsBesideTheThemes() {
        assertEquals("https://example.com/themes/theme-index.txt", ThemeInstaller.indexUrl("https://example.com/themes/"));
        assertEquals("https://example.com/themes/theme-index.txt", ThemeInstaller.indexUrl("https://example.com/themes"));
    }

    @Test
    public void test_zipNameAndDirectory() {
        assertEquals("docuforge-15.9.0.zip", ThemeInstaller.zipName("docuforge", "15.9.0"));
        assertEquals(Path.of("/opt/fess", "app", "themes"), ThemeInstaller.directory("/opt/fess"));
    }
}
