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
package org.codelibs.fess.theme;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;

public class StaticThemeResponderTest extends UnitFessTestCase {

    private static final String YAML = String.join("\n", //
            "apiVersion: fess.codelibs.org/v1", //
            "kind: StaticTheme", //
            "name: t", //
            "displayName: T", //
            "version: 1.0.0");

    private static ThemeManifest manifest() {
        return ThemeManifest.parse(new ByteArrayInputStream(YAML.getBytes(StandardCharsets.UTF_8)));
    }

    private static void deleteTree(final Path tmp) {
        try {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        } catch (final Exception ignore) {}
    }

    // --- serveIndex: normal path -----------------------

    @Test
    public void test_serveIndex_normal() throws Exception {
        final Path tmp = Files.createTempDirectory("tv-index-");
        try {
            final String content = "<html><body>hello</body></html>";
            Files.writeString(tmp.resolve("index.html"), content);
            final Theme theme = new Theme("t", tmp, manifest());
            final StubRequest req = new StubRequest();
            final CapturingResponse res = new CapturingResponse();

            new StaticThemeResponder().serveIndex(req, res, theme, "/");

            assertEquals(200, res.status);
            assertEquals("text/html; charset=UTF-8", res.contentType);
            assertEquals("no-store", res.headers.get("Cache-Control"));
            assertEquals(StaticThemeResponder.INDEX_CSP, res.headers.get("Content-Security-Policy"));
            assertEquals("same-origin", res.headers.get("Referrer-Policy"));
            assertEquals("inline; filename=\"index.html\"", res.headers.get("Content-Disposition"));
            assertEquals(content, new String(res.body(), StandardCharsets.UTF_8));
            // Content-Length should equal the file size (set via setContentLengthLong).
            assertEquals(content.getBytes(StandardCharsets.UTF_8).length, (int) res.contentLength);
        } finally {
            deleteTree(tmp);
        }
    }

    @Test
    public void test_serveIndex_missingEntry_returns404() throws Exception {
        final Path tmp = Files.createTempDirectory("tv-index-missing-");
        try {
            // No index.html created.
            final Theme theme = new Theme("t", tmp, manifest());
            final StubRequest req = new StubRequest();
            final CapturingResponse res = new CapturingResponse();

            new StaticThemeResponder().serveIndex(req, res, theme, "/");

            assertEquals(404, res.status);
            assertEquals("text/plain; charset=UTF-8", res.contentType);
            assertEquals("Not Found", new String(res.body(), StandardCharsets.UTF_8));
        } finally {
            deleteTree(tmp);
        }
    }

    @Test
    public void test_serveIndex_blockedEntryThemeYml_returns404() throws Exception {
        final Path tmp = Files.createTempDirectory("tv-index-blocked-yml-");
        try {
            final String ymlContent = String.join("\n", //
                    "apiVersion: fess.codelibs.org/v1", "kind: StaticTheme", "name: t", "displayName: T", "version: 1.0.0",
                    "entry: theme.yml");
            Files.writeString(tmp.resolve("theme.yml"), ymlContent);
            Files.writeString(tmp.resolve("index.html"), "<html/>");
            final ThemeManifest m = ThemeManifest.parse(new ByteArrayInputStream(ymlContent.getBytes(StandardCharsets.UTF_8)));
            assertEquals("theme.yml", m.getEntry());
            final Theme theme = new Theme("t", tmp, m);
            final StubRequest req = new StubRequest();
            final CapturingResponse res = new CapturingResponse();

            new StaticThemeResponder().serveIndex(req, res, theme, "/");

            assertEquals(404, res.status);
            assertEquals("Not Found", new String(res.body(), StandardCharsets.UTF_8));
        } finally {
            deleteTree(tmp);
        }
    }

    @Test
    public void test_serveIndex_blockedEntryDotfile_returns404() throws Exception {
        final Path tmp = Files.createTempDirectory("tv-index-blocked-dot-");
        try {
            Files.writeString(tmp.resolve(".env"), "SECRET=hunter2");
            final String ymlContent = String.join("\n", //
                    "apiVersion: fess.codelibs.org/v1", "kind: StaticTheme", "name: t", "displayName: T", "version: 1.0.0", "entry: .env");
            final ThemeManifest m = ThemeManifest.parse(new ByteArrayInputStream(ymlContent.getBytes(StandardCharsets.UTF_8)));
            assertEquals(".env", m.getEntry());
            final Theme theme = new Theme("t", tmp, m);
            final CapturingResponse res = new CapturingResponse();

            new StaticThemeResponder().serveIndex(new StubRequest(), res, theme, "/");

            assertEquals(404, res.status);
        } finally {
            deleteTree(tmp);
        }
    }

    @Test
    public void test_serveIndex_blockedEntryReadme_returns404() throws Exception {
        final Path tmp = Files.createTempDirectory("tv-index-blocked-readme-");
        try {
            Files.writeString(tmp.resolve("README.md"), "# My Theme");
            final String ymlContent = String.join("\n", //
                    "apiVersion: fess.codelibs.org/v1", "kind: StaticTheme", "name: t", "displayName: T", "version: 1.0.0",
                    "entry: README.md");
            final ThemeManifest m = ThemeManifest.parse(new ByteArrayInputStream(ymlContent.getBytes(StandardCharsets.UTF_8)));
            assertEquals("README.md", m.getEntry());
            final Theme theme = new Theme("t", tmp, m);
            final CapturingResponse res = new CapturingResponse();

            new StaticThemeResponder().serveIndex(new StubRequest(), res, theme, "/");

            assertEquals(404, res.status);
        } finally {
            deleteTree(tmp);
        }
    }

    // --- serveIndex: error routes -----------------------

    @Test
    public void test_serveIndex_errorRoute_setsStatusAndHeadersAndMeta() throws Exception {
        final Path tmp = Files.createTempDirectory("tv-index-error-");
        try {
            Files.writeString(tmp.resolve("index.html"), "<!DOCTYPE html><html><head><title>Fess</title></head><body></body></html>");
            final Theme theme = new Theme("t", tmp, manifest());
            final CapturingResponse res = new CapturingResponse();

            new StaticThemeResponder().serveIndex(new StubRequest(), res, theme, "/error/notFound");

            assertEquals(404, res.status);
            assertEquals("text/html; charset=UTF-8", res.contentType);
            assertEquals("error", res.headers.get("X-Fess-Route"));
            assertEquals("404", res.headers.get("X-Fess-Error-Code"));
            assertEquals("no-store", res.headers.get("Cache-Control"));
            assertEquals(StaticThemeResponder.INDEX_CSP, res.headers.get("Content-Security-Policy"));
            final String body = new String(res.body(), StandardCharsets.UTF_8);
            assertTrue(body.contains("<meta name=\"x-fess-error-code\" content=\"404\">"), body);
            // No message_key supplied -> no detail meta.
            assertFalse(body.contains("x-fess-error-detail-key"));
        } finally {
            deleteTree(tmp);
        }
    }

    @Test
    public void test_serveIndex_errorRoute_statusMapping() throws Exception {
        final Path tmp = Files.createTempDirectory("tv-index-error-map-");
        try {
            Files.writeString(tmp.resolve("index.html"), "<!DOCTYPE html><html><head></head><body></body></html>");
            final Theme theme = new Theme("t", tmp, manifest());
            assertErrorStatus(theme, "/error", 500);
            assertErrorStatus(theme, "/error/notFound", 404);
            assertErrorStatus(theme, "/error/badRequest", 400);
            assertErrorStatus(theme, "/error/busy", 429);
            assertErrorStatus(theme, "/error/system", 500);
            assertErrorStatus(theme, "/error/anything_else", 500);
        } finally {
            deleteTree(tmp);
        }
    }

    private void assertErrorStatus(final Theme theme, final String path, final int expected) throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new StaticThemeResponder().serveIndex(new StubRequest(), res, theme, path);
        assertEquals("status for " + path, expected, res.status);
        assertEquals("X-Fess-Error-Code for " + path, String.valueOf(expected), res.headers.get("X-Fess-Error-Code"));
        final String body = new String(res.body(), StandardCharsets.UTF_8);
        assertTrue(body.contains("content=\"" + expected + "\""), "meta code for " + path + ": " + body);
    }

    @Test
    public void test_serveIndex_errorRoute_withMessageKey_injectsDetailMeta() throws Exception {
        final Path tmp = Files.createTempDirectory("tv-index-msgkey-");
        try {
            Files.writeString(tmp.resolve("index.html"), "<!DOCTYPE html><html><head><title>Fess</title></head><body></body></html>");
            final Theme theme = new Theme("t", tmp, manifest());
            final StubRequest req = new StubRequest();
            req.messageKey = "errors.docid_not_found";
            final CapturingResponse res = new CapturingResponse();

            new StaticThemeResponder().serveIndex(req, res, theme, "/error/notFound");

            assertEquals(404, res.status);
            final String body = new String(res.body(), StandardCharsets.UTF_8);
            assertTrue(body.contains("x-fess-error-detail-key"), body);
            assertTrue(body.contains("content=\"errors.docid_not_found\""), body);
        } finally {
            deleteTree(tmp);
        }
    }

    @Test
    public void test_serveIndex_errorRoute_invalidMessageKey_notInjected() throws Exception {
        final Path tmp = Files.createTempDirectory("tv-index-badkey-");
        try {
            Files.writeString(tmp.resolve("index.html"), "<!DOCTYPE html><html><head><title>Fess</title></head><body></body></html>");
            final Theme theme = new Theme("t", tmp, manifest());
            final StubRequest req = new StubRequest();
            req.messageKey = "<script>alert(1)</script>";
            final CapturingResponse res = new CapturingResponse();

            new StaticThemeResponder().serveIndex(req, res, theme, "/error/notFound");

            final String body = new String(res.body(), StandardCharsets.UTF_8);
            assertFalse(body.contains("x-fess-error-detail-key"), "Unsafe key must not be injected: " + body);
        } finally {
            deleteTree(tmp);
        }
    }

    @Test
    public void test_serveIndex_nonErrorRoute_noErrorHeaders() throws Exception {
        final Path tmp = Files.createTempDirectory("tv-index-noerr-");
        try {
            Files.writeString(tmp.resolve("index.html"), "<html/>");
            final Theme theme = new Theme("t", tmp, manifest());
            final CapturingResponse res = new CapturingResponse();

            new StaticThemeResponder().serveIndex(new StubRequest(), res, theme, "/search");

            assertEquals(200, res.status);
            assertNull(res.headers.get("X-Fess-Route"));
            assertNull(res.headers.get("X-Fess-Error-Code"));
        } finally {
            deleteTree(tmp);
        }
    }

    // --- serveAsset: content types -----------------------

    @Test
    public void test_serveAsset_js() throws Exception {
        assertAssetContentType("assets/app.js", "console.log(1);", "application/javascript; charset=UTF-8", "inline");
    }

    @Test
    public void test_serveAsset_css() throws Exception {
        assertAssetContentType("assets/style.css", "body{}", "text/css; charset=UTF-8", "inline");
    }

    @Test
    public void test_serveAsset_png() throws Exception {
        assertAssetContentType("assets/logo.png", "PNGDATA", "image/png", "inline");
    }

    @Test
    public void test_serveAsset_json() throws Exception {
        assertAssetContentType("assets/data.json", "{}", "application/json; charset=UTF-8", "inline");
    }

    @Test
    public void test_serveAsset_unknownExtension_octetStream() throws Exception {
        assertAssetContentType("assets/file.xyz", "data", "application/octet-stream", "inline");
    }

    @Test
    public void test_serveAsset_html_attachmentDisposition() throws Exception {
        assertAssetContentType("page.html", "<html/>", "text/html; charset=UTF-8", "attachment");
    }

    private void assertAssetContentType(final String assetPath, final String content, final String expectedType,
            final String expectedDispositionType) throws Exception {
        final Path tmp = Files.createTempDirectory("tv-asset-");
        try {
            final Path file = tmp.resolve(assetPath);
            Files.createDirectories(file.getParent());
            Files.writeString(file, content);
            final Theme theme = new Theme("t", tmp, manifest());
            final CapturingResponse res = new CapturingResponse();

            new StaticThemeResponder().serveAsset(new StubRequest(), res, theme, assetPath);

            assertEquals(200, res.status);
            assertEquals(expectedType, res.contentType);
            assertEquals("public, max-age=86400", res.headers.get("Cache-Control"));
            assertEquals("Accept-Encoding", res.headers.get("Vary"));
            assertEquals("same-origin", res.headers.get("Referrer-Policy"));
            assertNotNull(res.headers.get("ETag"));
            assertTrue(res.headers.get("ETag").startsWith("W/\""), res.headers.get("ETag"));
            assertNotNull(res.headers.get("Last-Modified"));
            final String fname = file.getFileName().toString();
            assertEquals(expectedDispositionType + "; filename=\"" + fname + "\"", res.headers.get("Content-Disposition"));
            assertEquals(content, new String(res.body(), StandardCharsets.UTF_8));
        } finally {
            deleteTree(tmp);
        }
    }

    @Test
    public void test_serveAsset_svg_setsSvgCsp() throws Exception {
        final Path tmp = Files.createTempDirectory("tv-asset-svg-");
        try {
            Files.writeString(tmp.resolve("icon.svg"), "<svg/>");
            final Theme theme = new Theme("t", tmp, manifest());
            final CapturingResponse res = new CapturingResponse();

            new StaticThemeResponder().serveAsset(new StubRequest(), res, theme, "icon.svg");

            assertEquals(200, res.status);
            assertEquals("image/svg+xml; charset=UTF-8", res.contentType);
            assertEquals(StaticThemeResponder.SVG_CSP, res.headers.get("Content-Security-Policy"));
            assertEquals("inline; filename=\"icon.svg\"", res.headers.get("Content-Disposition"));
        } finally {
            deleteTree(tmp);
        }
    }

    // --- serveAsset: conditional GET ---------------------

    @Test
    public void test_serveAsset_ifNoneMatch_returns304() throws Exception {
        final Path tmp = Files.createTempDirectory("tv-asset-inm-");
        try {
            final Path file = tmp.resolve("app.js");
            Files.writeString(file, "console.log(1);");
            final Theme theme = new Theme("t", tmp, manifest());
            final BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
            final String etag = "W/\"" + attrs.size() + "-" + attrs.lastModifiedTime().toMillis() + "\"";

            final StubRequest req = new StubRequest();
            req.headers.put("If-None-Match", etag);
            final CapturingResponse res = new CapturingResponse();

            new StaticThemeResponder().serveAsset(req, res, theme, "app.js");

            assertEquals(304, res.status);
            assertEquals(0, res.body().length, "304 must have no body");
        } finally {
            deleteTree(tmp);
        }
    }

    @Test
    public void test_serveAsset_ifModifiedSince_returns304() throws Exception {
        final Path tmp = Files.createTempDirectory("tv-asset-ims-");
        try {
            final Path file = tmp.resolve("app.js");
            Files.writeString(file, "console.log(1);");
            final Theme theme = new Theme("t", tmp, manifest());
            final BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
            final long mtime = attrs.lastModifiedTime().toMillis();
            // Use a client time 60s in the future so (mtime/1000) <= (clientMtime/1000) holds.
            final String ifModSince = DateTimeFormatter.RFC_1123_DATE_TIME
                    .format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(mtime + 60_000), ZoneOffset.UTC));

            final StubRequest req = new StubRequest();
            req.headers.put("If-Modified-Since", ifModSince);
            final CapturingResponse res = new CapturingResponse();

            new StaticThemeResponder().serveAsset(req, res, theme, "app.js");

            assertEquals(304, res.status);
            assertEquals(0, res.body().length, "304 must have no body");
        } finally {
            deleteTree(tmp);
        }
    }

    // --- serveAsset: path traversal / rejection -------------------

    @Test
    public void test_serveAsset_pathTraversal_returns404() throws Exception {
        final Path tmp = Files.createTempDirectory("tv-asset-trav-");
        try {
            Files.createDirectories(tmp.resolve("inside"));
            Files.writeString(tmp.resolve("inside/file.txt"), "ok");
            final Theme theme = new Theme("t", tmp, manifest());

            assert404(theme, "../etc/passwd");
            assert404(theme, "/etc/passwd");
            assert404(theme, "inside/../../outside");
            assert404(theme, "back\\slash.txt");
            assert404(theme, "null byte.txt");
        } finally {
            deleteTree(tmp);
        }
    }

    @Test
    public void test_serveAsset_blockedFilenames_returns404() throws Exception {
        final Path tmp = Files.createTempDirectory("tv-asset-blocked-");
        try {
            Files.writeString(tmp.resolve(".env"), "SECRET");
            Files.writeString(tmp.resolve("theme.yml"), "x");
            Files.writeString(tmp.resolve("README.md"), "x");
            Files.writeString(tmp.resolve("LICENSE"), "x");
            Files.writeString(tmp.resolve("LICENSE.txt"), "x");
            final Theme theme = new Theme("t", tmp, manifest());

            assert404(theme, ".env");
            assert404(theme, "theme.yml");
            assert404(theme, "README.md");
            assert404(theme, "LICENSE");
            assert404(theme, "LICENSE.txt");
        } finally {
            deleteTree(tmp);
        }
    }

    @Test
    public void test_serveAsset_missingFile_returns404() throws Exception {
        final Path tmp = Files.createTempDirectory("tv-asset-missing-");
        try {
            final Theme theme = new Theme("t", tmp, manifest());
            assert404(theme, "missing.txt");
        } finally {
            deleteTree(tmp);
        }
    }

    private void assert404(final Theme theme, final String assetPath) throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new StaticThemeResponder().serveAsset(new StubRequest(), res, theme, assetPath);
        assertEquals("expected 404 for " + assetPath, 404, res.status);
        assertEquals("404 content type for " + assetPath, "text/plain; charset=UTF-8", res.contentType);
        assertEquals("404 body for " + assetPath, "Not Found", new String(res.body(), StandardCharsets.UTF_8));
    }

    // --- resolveAsset (instance helper) -----------------------

    @Test
    public void test_resolveAsset_rejectsTraversalAndAcceptsValid() throws Exception {
        final Path tmp = Files.createTempDirectory("tv-resolve-");
        try {
            Files.createDirectories(tmp.resolve("inside"));
            Files.writeString(tmp.resolve("inside/file.txt"), "ok");
            final Theme theme = new Theme("t", tmp, manifest());
            final StaticThemeResponder viewer = new StaticThemeResponder();

            assertNotNull(viewer.resolveAsset(theme, "inside/file.txt"));
            assertNull(viewer.resolveAsset(theme, "../etc/passwd"));
            assertNull(viewer.resolveAsset(theme, "/etc/passwd"));
            assertNull(viewer.resolveAsset(theme, "inside/../../outside"));
            assertNull(viewer.resolveAsset(theme, null));
        } finally {
            deleteTree(tmp);
        }
    }

    @Test
    public void test_resolveAsset_rejectsSymlink() throws Exception {
        final Path tmp = Files.createTempDirectory("tv-symlink-");
        final Path target = Files.createTempFile("outside-", ".txt");
        try {
            Files.writeString(target, "secret");
            final Path link = tmp.resolve("escape.txt");
            try {
                Files.createSymbolicLink(link, target);
            } catch (final java.io.IOException | UnsupportedOperationException e) {
                // Symlinks may not be supported on all test environments; skip gracefully.
                return;
            }
            final Theme theme = new Theme("t", tmp, manifest());
            assertNull(new StaticThemeResponder().resolveAsset(theme, "escape.txt"), "Symlink must be rejected");
        } finally {
            Files.deleteIfExists(target);
            deleteTree(tmp);
        }
    }

    @Test
    public void test_resolveAsset_directoryNotRegularFile_returnsNull() throws Exception {
        final Path tmp = Files.createTempDirectory("tv-resolve-dir-");
        try {
            Files.createDirectories(tmp.resolve("assets"));
            final Theme theme = new Theme("t", tmp, manifest());
            assertNull(new StaticThemeResponder().resolveAsset(theme, "assets"));
            assertNull(new StaticThemeResponder().resolveAsset(theme, "missing.txt"));
        } finally {
            deleteTree(tmp);
        }
    }

    // --- static / instance helper unit tests -----------------------

    @Test
    public void test_computeErrorStatus() {
        assertEquals(404, StaticThemeResponder.computeErrorStatus("/error/notFound"));
        assertEquals(404, StaticThemeResponder.computeErrorStatus("/error/notfound"));
        assertEquals(400, StaticThemeResponder.computeErrorStatus("/error/badRequest"));
        assertEquals(400, StaticThemeResponder.computeErrorStatus("/error/badrequest"));
        assertEquals(500, StaticThemeResponder.computeErrorStatus("/error/system"));
        assertEquals(500, StaticThemeResponder.computeErrorStatus("/error/error"));
        assertEquals(500, StaticThemeResponder.computeErrorStatus("/error"));
        assertEquals(429, StaticThemeResponder.computeErrorStatus("/error/busy"));
        assertEquals(500, StaticThemeResponder.computeErrorStatus("/error/anything_else"));
        assertEquals(500, StaticThemeResponder.computeErrorStatus("/error/unknown"));
        assertEquals(500, StaticThemeResponder.computeErrorStatus(null));
    }

    @Test
    public void test_isBlockedFilename() {
        assertTrue(StaticThemeResponder.isBlockedFilename(".env"));
        assertTrue(StaticThemeResponder.isBlockedFilename(".git"));
        assertTrue(StaticThemeResponder.isBlockedFilename("theme.yml"));
        assertTrue(StaticThemeResponder.isBlockedFilename("THEME.YML"));
        assertTrue(StaticThemeResponder.isBlockedFilename("README.md"));
        assertTrue(StaticThemeResponder.isBlockedFilename("CHANGELOG.md"));
        assertTrue(StaticThemeResponder.isBlockedFilename("LICENSE"));
        assertTrue(StaticThemeResponder.isBlockedFilename("LICENSE.txt"));
        assertTrue(StaticThemeResponder.isBlockedFilename("license-apache.txt"));
        assertTrue(StaticThemeResponder.isBlockedFilename(null));
        assertTrue(StaticThemeResponder.isBlockedFilename(""));
        assertFalse(StaticThemeResponder.isBlockedFilename("app.js"));
        assertFalse(StaticThemeResponder.isBlockedFilename("index.html"));
        assertFalse(StaticThemeResponder.isBlockedFilename("styles.css"));
    }

    @Test
    public void test_contentTypeFor_commonExtensions() {
        assertEquals("application/javascript; charset=UTF-8", StaticThemeResponder.contentTypeFor("app.js"));
        assertEquals("application/javascript; charset=UTF-8", StaticThemeResponder.contentTypeFor("app.mjs"));
        assertEquals("application/javascript; charset=UTF-8", StaticThemeResponder.contentTypeFor("App.JS"));
        assertEquals("text/css; charset=UTF-8", StaticThemeResponder.contentTypeFor("styles.css"));
        assertEquals("text/css; charset=UTF-8", StaticThemeResponder.contentTypeFor("Style.CSS"));
        assertEquals("text/html; charset=UTF-8", StaticThemeResponder.contentTypeFor("index.html"));
        assertEquals("application/json; charset=UTF-8", StaticThemeResponder.contentTypeFor("manifest.json"));
        assertEquals("application/manifest+json; charset=UTF-8", StaticThemeResponder.contentTypeFor("app.webmanifest"));
        assertEquals("image/svg+xml; charset=UTF-8", StaticThemeResponder.contentTypeFor("icon.svg"));
        assertEquals("image/svg+xml; charset=UTF-8", StaticThemeResponder.contentTypeFor("logo.SVG"));
        assertEquals("image/png", StaticThemeResponder.contentTypeFor("logo.png"));
        assertEquals("image/jpeg", StaticThemeResponder.contentTypeFor("photo.jpg"));
        assertEquals("image/jpeg", StaticThemeResponder.contentTypeFor("photo.jpeg"));
        assertEquals("image/gif", StaticThemeResponder.contentTypeFor("anim.gif"));
        assertEquals("font/woff2", StaticThemeResponder.contentTypeFor("font.woff2"));
        assertEquals("font/woff", StaticThemeResponder.contentTypeFor("font.woff"));
        assertEquals("application/wasm", StaticThemeResponder.contentTypeFor("module.wasm"));
    }

    @Test
    public void test_contentTypeFor_unknownFallsBackToOctetStream() {
        assertEquals("application/octet-stream", StaticThemeResponder.contentTypeFor("unknown.xyz"));
        assertEquals("application/octet-stream", StaticThemeResponder.contentTypeFor("no-extension"));
        assertEquals("application/octet-stream", StaticThemeResponder.contentTypeFor(""));
        assertEquals("application/octet-stream", StaticThemeResponder.contentTypeFor(null));
    }

    @Test
    public void test_injectErrorCodeMeta_insertsBeforeHeadClose() {
        final String html = "<html><head><title>T</title></head><body></body></html>";
        final byte[] result = StaticThemeResponder.injectErrorCodeMeta(html.getBytes(StandardCharsets.UTF_8), 404);
        final String out = new String(result, StandardCharsets.UTF_8);
        assertTrue(out.contains("<meta name=\"x-fess-error-code\" content=\"404\">"));
        assertTrue(out.indexOf("x-fess-error-code") < out.indexOf("</head>"));
    }

    @Test
    public void test_injectErrorCodeMeta_returnsOriginalWhenNoHeadClose() {
        final String originalHtml = "<html><body>no head</body></html>";
        final byte[] result = StaticThemeResponder.injectErrorCodeMeta(originalHtml.getBytes(StandardCharsets.UTF_8), 500);
        final String resultStr = new String(result, StandardCharsets.UTF_8);
        assertEquals(originalHtml, resultStr);
        assertFalse(resultStr.contains("x-fess-error-code"));
    }

    @Test
    public void test_injectErrorDetailMeta_insertsBeforeHeadClose() {
        final String html = "<html><head><title>T</title></head><body></body></html>";
        final byte[] result = StaticThemeResponder.injectErrorDetailMeta(html.getBytes(StandardCharsets.UTF_8), "errors.docid_not_found");
        final String out = new String(result, StandardCharsets.UTF_8);
        assertTrue(out.contains("<meta name=\"x-fess-error-detail-key\" content=\"errors.docid_not_found\">"));
        assertTrue(out.indexOf("x-fess-error-detail-key") < out.indexOf("</head>"));
    }

    @Test
    public void test_injectErrorDetailMeta_returnsOriginalWhenNoHeadClose() {
        final String originalHtml = "<html><body>no head</body></html>";
        final byte[] result =
                StaticThemeResponder.injectErrorDetailMeta(originalHtml.getBytes(StandardCharsets.UTF_8), "errors.docid_not_found");
        final String resultStr = new String(result, StandardCharsets.UTF_8);
        assertEquals(originalHtml, resultStr);
        assertFalse(resultStr.contains("x-fess-error-detail-key"));
    }

    @Test
    public void test_resolveMessageKey_acceptsSafeAndRejectsUnsafe() {
        final StaticThemeResponder viewer = new StaticThemeResponder();
        final StubRequest safe = new StubRequest();
        safe.messageKey = "errors.docid_not_found";
        assertEquals("errors.docid_not_found", viewer.resolveMessageKey(safe));

        final StubRequest unsafe = new StubRequest();
        unsafe.messageKey = "<script>alert(1)</script>";
        assertNull(viewer.resolveMessageKey(unsafe));

        final StubRequest absent = new StubRequest();
        assertNull(viewer.resolveMessageKey(absent));

        assertNull(viewer.resolveMessageKey(null));
    }

    // ===== Stubs =====

    /**
     * Capturing {@link HttpServletResponse} stub: records status/content-type/headers and
     * buffers the body via a {@link ByteArrayOutputStream}-backed {@link ServletOutputStream}.
     */
    static class CapturingResponse implements HttpServletResponse {
        int status = 200;
        String contentType;
        long contentLength = -1;
        final Map<String, String> headers = new HashMap<>();
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        private final ServletOutputStream out = new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(final WriteListener writeListener) {
            }

            @Override
            public void write(final int b) {
                buffer.write(b);
            }

            @Override
            public void write(final byte[] b, final int off, final int len) {
                buffer.write(b, off, len);
            }
        };

        byte[] body() {
            return buffer.toByteArray();
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
        public void setContentLength(final int len) {
            this.contentLength = len;
        }

        @Override
        public void setContentLengthLong(final long len) {
            this.contentLength = len;
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
        public ServletOutputStream getOutputStream() {
            return out;
        }

        @Override
        public boolean containsHeader(final String name) {
            return headers.containsKey(name);
        }

        @Override
        public String getHeader(final String name) {
            return headers.get(name);
        }

        @Override
        public java.util.Collection<String> getHeaders(final String name) {
            final String v = headers.get(name);
            return v == null ? Collections.emptyList() : java.util.List.of(v);
        }

        @Override
        public java.util.Collection<String> getHeaderNames() {
            return headers.keySet();
        }

        // --- remaining methods: unused ---
        @Override
        public String getCharacterEncoding() {
            return "UTF-8";
        }

        @Override
        public java.io.PrintWriter getWriter() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCharacterEncoding(final String s) {
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
        public String encodeURL(final String url) {
            return url;
        }

        @Override
        public String encodeRedirectURL(final String url) {
            return url;
        }

        @Override
        public void sendError(final int sc, final String msg) {
            this.status = sc;
        }

        @Override
        public void sendError(final int sc) {
            this.status = sc;
        }

        @Override
        public void sendRedirect(final String location) {
        }

        @Override
        public void sendRedirect(final String location, final int sc, final boolean clearBuffer) {
        }

        @Override
        public void sendRedirect(final String location, final int sc) {
        }

        @Override
        public void sendRedirect(final String location, final boolean clearBuffer) {
        }

        @Override
        public void setDateHeader(final String name, final long date) {
        }

        @Override
        public void addDateHeader(final String name, final long date) {
        }

        @Override
        public void setIntHeader(final String name, final int value) {
            headers.put(name, String.valueOf(value));
        }

        @Override
        public void addIntHeader(final String name, final int value) {
            headers.put(name, String.valueOf(value));
        }
    }

    /**
     * Minimal {@link HttpServletRequest} stub exposing header lookups and the
     * {@code message_key} parameter.
     */
    static class StubRequest implements HttpServletRequest {
        final Map<String, String> headers = new HashMap<>();
        String messageKey;

        @Override
        public String getHeader(final String name) {
            return headers.get(name);
        }

        @Override
        public String getParameter(final String name) {
            if ("message_key".equals(name)) {
                return messageKey;
            }
            return null;
        }

        @Override
        public String getRequestURI() {
            return "/";
        }

        @Override
        public String getContextPath() {
            return "";
        }

        // --- remaining methods: unused ---
        @Override
        public String getAuthType() {
            return null;
        }

        @Override
        public jakarta.servlet.http.Cookie[] getCookies() {
            return null;
        }

        @Override
        public long getDateHeader(final String name) {
            return -1;
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
            return new StringBuffer("/");
        }

        @Override
        public String getServletPath() {
            return "/";
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
        public String getMethod() {
            return "GET";
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
        public void setAttribute(final String name, final Object o) {
        }

        @Override
        public void removeAttribute(final String name) {
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
        public RequestDispatcher getRequestDispatcher(final String path) {
            return null;
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
            return null;
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
}
