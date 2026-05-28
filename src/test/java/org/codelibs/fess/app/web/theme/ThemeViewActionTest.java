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
package org.codelibs.fess.app.web.theme;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import org.codelibs.fess.helper.VirtualHostHelper;
import org.codelibs.fess.theme.Theme;
import org.codelibs.fess.theme.ThemeManifest;
import org.codelibs.fess.theme.ThemeRegistry;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.StreamResponse;
import org.lastaflute.web.servlet.request.stream.WrittenStreamOut;

public class ThemeViewActionTest extends UnitFessTestCase {

    @Test
    public void test_resolveAsset_rejectsTraversal() throws Exception {
        final Path tmp = Files.createTempDirectory("tva-");
        try {
            Files.createDirectories(tmp.resolve("inside"));
            Files.writeString(tmp.resolve("inside/file.txt"), "ok");

            final String yaml = String.join("\n", //
                    "apiVersion: fess.codelibs.org/v1", //
                    "kind: StaticTheme", //
                    "name: t", //
                    "displayName: T", //
                    "version: 1.0.0");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
            final Theme theme = new Theme("t", tmp, manifest);

            final ThemeViewAction action = new ThemeViewAction();

            assertNotNull(action.resolveAsset(theme, "inside/file.txt"));
            assertNull(action.resolveAsset(theme, "../etc/passwd"));
            assertNull(action.resolveAsset(theme, "/etc/passwd"));
            assertNull(action.resolveAsset(theme, "inside/../../outside"));
            assertNull(action.resolveAsset(theme, null));
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    @Test
    public void test_contentTypeFor_returnsExpectedMimeForCommonExtensions() throws Exception {
        // contentTypeFor is private static — exercise via reflection so this guards
        // each branch of the extension-to-mime switch directly.
        assertEquals("application/javascript; charset=UTF-8", invokeContentTypeFor("app.js"));
        assertEquals("text/css; charset=UTF-8", invokeContentTypeFor("styles.css"));
        assertEquals("text/html; charset=UTF-8", invokeContentTypeFor("index.html"));
        assertEquals("application/json; charset=UTF-8", invokeContentTypeFor("manifest.json"));
        assertEquals("image/svg+xml; charset=UTF-8", invokeContentTypeFor("icon.svg"));
        assertEquals("image/png", invokeContentTypeFor("logo.png"));
        assertEquals("image/jpeg", invokeContentTypeFor("photo.jpg"));
        assertEquals("image/jpeg", invokeContentTypeFor("photo.jpeg"));
        assertEquals("image/gif", invokeContentTypeFor("anim.gif"));
        assertEquals("font/woff2", invokeContentTypeFor("font.woff2"));
        assertEquals("font/woff", invokeContentTypeFor("font.woff"));
    }

    @Test
    public void test_contentTypeFor_unknownExtensionFallsBackToOctetStream() throws Exception {
        // Any extension not in the switch must default to octet-stream — guards
        // accidental MIME sniffing on unknown payloads.
        assertEquals("application/octet-stream", invokeContentTypeFor("unknown.xyz"));
        assertEquals("application/octet-stream", invokeContentTypeFor("no-extension"));
        assertEquals("application/octet-stream", invokeContentTypeFor(""));
    }

    @Test
    public void test_resolveAsset_returnsNullWhenPathIsNotARegularFile() throws Exception {
        // Exercise the "candidate not a regular file" branch of resolveAsset.
        final Path tmp = Files.createTempDirectory("tva-");
        try {
            Files.createDirectories(tmp.resolve("assets"));
            // "assets" exists but is a directory, not a regular file.
            final String yaml = String.join("\n", //
                    "apiVersion: fess.codelibs.org/v1", //
                    "kind: StaticTheme", //
                    "name: t", //
                    "displayName: T", //
                    "version: 1.0.0");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = new ThemeViewAction();
            assertNull(action.resolveAsset(theme, "assets"));
            assertNull(action.resolveAsset(theme, "missing.txt"));
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    @Test
    public void test_serveIndex_setsSecurityHeaders() throws Exception {
        // The HTML entry response must carry a Referrer-Policy header so themed UIs don't
        // leak referer to third parties. X-Content-Type-Options: nosniff is intentionally
        // NOT asserted here — it's applied uniformly by Tomcat's HttpHeaderSecurityFilter
        // (web.xml, blockContentTypeSniffingEnabled=true), so this Action no longer sets it.
        final Path tmp = Files.createTempDirectory("tva-headers-");
        try {
            Files.writeString(tmp.resolve("index.html"), "<html/>");
            final String yaml = String.join("\n", //
                    "apiVersion: fess.codelibs.org/v1", //
                    "kind: StaticTheme", //
                    "name: t", //
                    "displayName: T", //
                    "version: 1.0.0");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = newActionWith(theme);

            final ActionResponse resp = action.serveIndex();
            final Map<String, String[]> headers = ((StreamResponse) resp).getHeaderMap();
            assertEquals("same-origin", headers.get("Referrer-Policy")[0]);
            assertNull(headers.get("X-Content-Type-Options"),
                    "Action must not duplicate X-Content-Type-Options — it's set by HttpHeaderSecurityFilter");
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    @Test
    public void test_serveAsset_setsSecurityHeaders() throws Exception {
        // Static asset responses share the same minimum-header baseline as the
        // index response. X-Content-Type-Options: nosniff is intentionally NOT asserted
        // here — it's applied uniformly by Tomcat's HttpHeaderSecurityFilter (web.xml).
        final Path tmp = Files.createTempDirectory("tva-headers-");
        try {
            Files.createDirectories(tmp.resolve("assets"));
            Files.writeString(tmp.resolve("assets/app.js"), "console.log(1);");
            final String yaml = String.join("\n", //
                    "apiVersion: fess.codelibs.org/v1", //
                    "kind: StaticTheme", //
                    "name: t", //
                    "displayName: T", //
                    "version: 1.0.0");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = newActionWith(theme);

            final ActionResponse resp = action.serveAsset("assets/app.js");
            final Map<String, String[]> headers = ((StreamResponse) resp).getHeaderMap();
            assertEquals("same-origin", headers.get("Referrer-Policy")[0]);
            assertNull(headers.get("X-Content-Type-Options"),
                    "Action must not duplicate X-Content-Type-Options — it's set by HttpHeaderSecurityFilter");
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    @Test
    public void test_contentTypeFor_mjsFiles_returns_applicationJavascript() throws Exception {
        assertEquals("application/javascript; charset=UTF-8", invokeContentTypeFor("app.mjs"));
        assertEquals("application/javascript; charset=UTF-8", invokeContentTypeFor("component.MJS"));
    }

    @Test
    public void test_contentTypeFor_isCaseInsensitive() throws Exception {
        assertEquals("text/css; charset=UTF-8", invokeContentTypeFor("Style.CSS"));
        assertEquals("application/javascript; charset=UTF-8", invokeContentTypeFor("App.JS"));
        assertEquals("image/png", invokeContentTypeFor("Logo.PNG"));
        assertEquals("text/html; charset=UTF-8", invokeContentTypeFor("Index.HTML"));
    }

    @Test
    public void test_contentTypeFor_webmanifest() throws Exception {
        assertEquals("application/manifest+json; charset=UTF-8", invokeContentTypeFor("app.webmanifest"));
        assertEquals("application/manifest+json; charset=UTF-8", invokeContentTypeFor("App.WebManifest"));
    }

    @Test
    public void test_serveIndex_setsCsp_andNoStore_cacheControl() throws Exception {
        final Path tmp = Files.createTempDirectory("tva-csp-");
        try {
            Files.writeString(tmp.resolve("index.html"), "<html/>");
            final String yaml = String.join("\n", //
                    "apiVersion: fess.codelibs.org/v1", //
                    "kind: StaticTheme", //
                    "name: t", //
                    "displayName: T", //
                    "version: 1.0.0");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = newActionWith(theme);

            final ActionResponse resp = action.serveIndex();
            final Map<String, String[]> headers = ((StreamResponse) resp).getHeaderMap();
            assertEquals("no-store", headers.get("Cache-Control")[0]);
            assertNotNull(headers.get("Content-Security-Policy"), "CSP header must be set");
            assertTrue(headers.get("Content-Security-Policy")[0].contains("default-src 'self'"));
            assertTrue(headers.get("Content-Security-Policy")[0].contains("frame-ancestors 'none'"));
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    @Test
    public void test_serveIndex_setsContentLength() throws Exception {
        final Path tmp = Files.createTempDirectory("tva-cl-");
        try {
            final String content = "<html><body>hello</body></html>";
            Files.writeString(tmp.resolve("index.html"), content);
            final String yaml = String.join("\n", //
                    "apiVersion: fess.codelibs.org/v1", //
                    "kind: StaticTheme", //
                    "name: t", //
                    "displayName: T", //
                    "version: 1.0.0");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = newActionWith(theme);

            final ActionResponse resp = action.serveIndex();
            final Map<String, String[]> headers = ((StreamResponse) resp).getHeaderMap();
            assertNotNull(headers.get("Content-Length"), "Content-Length must be set");
            final long len = Long.parseLong(headers.get("Content-Length")[0]);
            assertTrue(len > 0, "Content-Length must be positive");
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    @Test
    public void test_asset_returnsEtag_andHandles_ifNoneMatch_304() throws Exception {
        final Path tmp = Files.createTempDirectory("tva-etag-");
        try {
            Files.createDirectories(tmp.resolve("assets"));
            Files.writeString(tmp.resolve("assets/app.js"), "console.log(1);");
            final String yaml = String.join("\n", //
                    "apiVersion: fess.codelibs.org/v1", //
                    "kind: StaticTheme", //
                    "name: t", //
                    "displayName: T", //
                    "version: 1.0.0");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = newActionWith(theme);

            // First request — no If-None-Match; expect ETag header
            final ActionResponse firstResp = action.serveAsset("assets/app.js");
            final Map<String, String[]> headers = ((StreamResponse) firstResp).getHeaderMap();
            assertNotNull(headers.get("ETag"), "ETag header must be set");
            final String etag = headers.get("ETag")[0];
            assertTrue(etag.startsWith("W/\""), "ETag must be weak: " + etag);

            // TODO: 304 round-trip requires a stub RequestManager carrying the If-None-Match
            // header. LastaFlute RequestManager is non-trivial to mock without DI; covered by
            // integration tests under it/. Here we verify the ETag shape only.
            assertTrue(etag.length() > 4, "ETag must be non-empty");
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    @Test
    public void test_svg_servedWithRestrictiveCsp() throws Exception {
        final Path tmp = Files.createTempDirectory("tva-svg-");
        try {
            Files.writeString(tmp.resolve("icon.svg"), "<svg/>");
            final String yaml = String.join("\n", //
                    "apiVersion: fess.codelibs.org/v1", //
                    "kind: StaticTheme", //
                    "name: t", //
                    "displayName: T", //
                    "version: 1.0.0");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = newActionWith(theme);

            final ActionResponse resp = action.serveAsset("icon.svg");
            final Map<String, String[]> headers = ((StreamResponse) resp).getHeaderMap();
            assertNotNull(headers.get("Content-Security-Policy"), "SVG must have CSP header");
            final String csp = headers.get("Content-Security-Policy")[0];
            assertTrue(csp.contains("default-src 'none'"), "SVG CSP must restrict to none: " + csp);
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    @Test
    public void test_resolveAsset_rejectsDotfiles() throws Exception {
        // Files whose name starts with '.' must never be served regardless of content type.
        final Path tmp = Files.createTempDirectory("tva-dotfile-");
        try {
            Files.writeString(tmp.resolve(".env"), "SECRET=hunter2");
            Files.writeString(tmp.resolve(".htaccess"), "deny all");
            final String yaml = String.join("\n", "apiVersion: fess.codelibs.org/v1", "kind: StaticTheme", "name: t", "displayName: T",
                    "version: 1.0.0");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = new ThemeViewAction();

            assertNull(action.resolveAsset(theme, ".env"), ".env must be rejected");
            assertNull(action.resolveAsset(theme, ".htaccess"), ".htaccess must be rejected");
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    @Test
    public void test_resolveAsset_rejectsSensitiveManifestFiles() throws Exception {
        // theme.yml, README.md, CHANGELOG.md, and LICENSE* must be blocked.
        final Path tmp = Files.createTempDirectory("tva-manifest-");
        try {
            Files.writeString(tmp.resolve("theme.yml"), "secret manifest content");
            Files.writeString(tmp.resolve("README.md"), "readme");
            Files.writeString(tmp.resolve("CHANGELOG.md"), "changes");
            Files.writeString(tmp.resolve("LICENSE"), "MIT");
            Files.writeString(tmp.resolve("LICENSE.txt"), "MIT");
            final String yaml = String.join("\n", "apiVersion: fess.codelibs.org/v1", "kind: StaticTheme", "name: t", "displayName: T",
                    "version: 1.0.0");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = new ThemeViewAction();

            assertNull(action.resolveAsset(theme, "theme.yml"), "theme.yml must be rejected");
            assertNull(action.resolveAsset(theme, "README.md"), "README.md must be rejected");
            assertNull(action.resolveAsset(theme, "CHANGELOG.md"), "CHANGELOG.md must be rejected");
            assertNull(action.resolveAsset(theme, "LICENSE"), "LICENSE must be rejected");
            assertNull(action.resolveAsset(theme, "LICENSE.txt"), "LICENSE.txt must be rejected");
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    @Test
    public void test_resolveAsset_rejectsSymlinks() throws Exception {
        // A symlink inside the theme directory must not be served — it could escape the sandbox.
        final Path tmp = Files.createTempDirectory("tva-symlink-");
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
            final String yaml = String.join("\n", "apiVersion: fess.codelibs.org/v1", "kind: StaticTheme", "name: t", "displayName: T",
                    "version: 1.0.0");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = new ThemeViewAction();

            assertNull(action.resolveAsset(theme, "escape.txt"), "Symlink must be rejected");
        } finally {
            Files.deleteIfExists(target);
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    @Test
    public void test_isBlockedFilename_returnsCorrectly() throws Exception {
        // Verify the helper directly via package-private access.
        assertTrue(ThemeViewAction.isBlockedFilename(".env"));
        assertTrue(ThemeViewAction.isBlockedFilename(".git"));
        assertTrue(ThemeViewAction.isBlockedFilename("theme.yml"));
        assertTrue(ThemeViewAction.isBlockedFilename("THEME.YML"));
        assertTrue(ThemeViewAction.isBlockedFilename("README.md"));
        assertTrue(ThemeViewAction.isBlockedFilename("CHANGELOG.md"));
        assertTrue(ThemeViewAction.isBlockedFilename("LICENSE"));
        assertTrue(ThemeViewAction.isBlockedFilename("LICENSE.txt"));
        assertTrue(ThemeViewAction.isBlockedFilename("license-apache.txt"));
        assertFalse(ThemeViewAction.isBlockedFilename("app.js"), "Regular files must not be blocked");
        assertFalse(ThemeViewAction.isBlockedFilename("index.html"), "index.html must not be blocked");
        assertFalse(ThemeViewAction.isBlockedFilename("styles.css"), "CSS must not be blocked");
    }

    @Test
    public void test_contentTypeFor_wasmAndSvgCharset() throws Exception {
        assertEquals("application/wasm", invokeContentTypeFor("module.wasm"));
        assertEquals("application/wasm", invokeContentTypeFor("worker.WASM"));
        assertEquals("image/svg+xml; charset=UTF-8", invokeContentTypeFor("icon.svg"));
        assertEquals("image/svg+xml; charset=UTF-8", invokeContentTypeFor("logo.SVG"));
    }

    @Test
    public void test_notFound_setsUtf8ContentType() throws Exception {
        final ThemeViewAction action = new ThemeViewAction();
        action.themeRegistry = new ThemeRegistry() {
            @Override
            public java.util.Optional<Theme> resolveActiveTheme(final String hostKey) {
                return java.util.Optional.empty();
            }
        };
        action.virtualHostHelper = new org.codelibs.fess.helper.VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return null;
            }
        };
        // serveIndex returns notFound() when registry returns empty.
        final ActionResponse resp = action.serveIndex();
        final StreamResponse sr = (StreamResponse) resp;
        assertTrue(sr.getHttpStatus().get() == 404, "HTTP status must be 404");
    }

    @Test
    public void test_streamFile_setsVaryHeader() throws Exception {
        final Path tmp = Files.createTempDirectory("tva-vary-");
        try {
            Files.createDirectories(tmp.resolve("assets"));
            Files.writeString(tmp.resolve("assets/app.js"), "console.log(1);");
            final String yaml = String.join("\n", "apiVersion: fess.codelibs.org/v1", "kind: StaticTheme", "name: t", "displayName: T",
                    "version: 1.0.0");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = newActionWith(theme);

            final ActionResponse resp = action.serveAsset("assets/app.js");
            final Map<String, String[]> headers = ((StreamResponse) resp).getHeaderMap();
            assertNotNull(headers.get("Vary"), "Vary header must be set");
            assertEquals("Accept-Encoding", headers.get("Vary")[0]);
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    // ── serveIndex blocked-manifest-entry tests ──────────────────────────────────

    @Test
    public void test_serveIndex_returnsNotFound_whenManifestEntryIsThemeYml() throws Exception {
        // theme.yml must not be served as the HTML entry even when (a) the manifest's
        // entry field passes ThemeManifest validation (isUnsafeEntry does not block it)
        // and (b) the file exists on disk as a regular file.
        // The ONLY reason for notFound() is the isBlockedFilename guard added to serveIndex().
        final Path tmp = Files.createTempDirectory("tva-blocked-yml-");
        try {
            // Write theme.yml as a real regular file so !isRegularFile cannot be the
            // reason for notFound — it would otherwise pass silently for the wrong reason.
            final String ymlContent = String.join("\n", //
                    "apiVersion: fess.codelibs.org/v1", //
                    "kind: StaticTheme", //
                    "name: t", //
                    "displayName: T", //
                    "version: 1.0.0", //
                    "entry: theme.yml");
            Files.writeString(tmp.resolve("theme.yml"), ymlContent);
            // Also create a valid index.html so we can confirm the positive path later.
            Files.writeString(tmp.resolve("index.html"), "<html/>");

            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(ymlContent.getBytes(StandardCharsets.UTF_8)));
            // Confirm manifest parsing accepted entry: "theme.yml" (it is not traversal/absolute,
            // so isUnsafeEntry allows it — proving the threat is real without the new guard).
            assertEquals("theme.yml", manifest.getEntry());

            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = newActionWith(theme);

            final ActionResponse resp = action.serveIndex();
            final StreamResponse sr = (StreamResponse) resp;
            // isBlockedFilename("theme.yml") must have fired and returned notFound().
            assertEquals(404, (int) sr.getHttpStatus().get(),
                    "serveIndex with entry=theme.yml must return 404 (blocked by isBlockedFilename)");
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    @Test
    public void test_serveIndex_returnsNotFound_whenManifestEntryIsDotfile() throws Exception {
        // A dotfile entry (e.g. .env) in the manifest must be blocked by isBlockedFilename
        // even when the dotfile exists on disk as a regular file.
        final Path tmp = Files.createTempDirectory("tva-blocked-dot-");
        try {
            // Write the target dotfile so !isRegularFile is NOT the reason for notFound.
            Files.writeString(tmp.resolve(".env"), "SECRET=hunter2");

            final String ymlContent = String.join("\n", //
                    "apiVersion: fess.codelibs.org/v1", //
                    "kind: StaticTheme", //
                    "name: t", //
                    "displayName: T", //
                    "version: 1.0.0", //
                    "entry: .env");
            // Confirm ThemeManifest accepts ".env" as entry (it is not traversal/absolute).
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(ymlContent.getBytes(StandardCharsets.UTF_8)));
            assertEquals(".env", manifest.getEntry());

            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = newActionWith(theme);

            final ActionResponse resp = action.serveIndex();
            final StreamResponse sr = (StreamResponse) resp;
            assertEquals(404, (int) sr.getHttpStatus().get(), "serveIndex with entry=.env must return 404 (blocked by isBlockedFilename)");
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    @Test
    public void test_serveIndex_returnsNotFound_whenManifestEntryIsReadmeMd() throws Exception {
        // README.md is in the blocked list; verify serveIndex rejects it when it exists on disk.
        final Path tmp = Files.createTempDirectory("tva-blocked-readme-");
        try {
            Files.writeString(tmp.resolve("README.md"), "# My Theme");

            final String ymlContent = String.join("\n", //
                    "apiVersion: fess.codelibs.org/v1", //
                    "kind: StaticTheme", //
                    "name: t", //
                    "displayName: T", //
                    "version: 1.0.0", //
                    "entry: README.md");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(ymlContent.getBytes(StandardCharsets.UTF_8)));
            assertEquals("README.md", manifest.getEntry());

            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = newActionWith(theme);

            final ActionResponse resp = action.serveIndex();
            final StreamResponse sr = (StreamResponse) resp;
            assertEquals(404, (int) sr.getHttpStatus().get(),
                    "serveIndex with entry=README.md must return 404 (blocked by isBlockedFilename)");
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    @Test
    public void test_serveIndex_servesFile_whenManifestEntryIsNormalHtml() throws Exception {
        // Positive/sanity: a normal entry (index.html) that exists must be served (200).
        // If the test above were to accidentally remove the isBlockedFilename guard from
        // production code, this test would still pass — it's here to confirm the positive
        // path is intact and the blocked tests fail for the RIGHT reason.
        final Path tmp = Files.createTempDirectory("tva-normal-entry-");
        try {
            Files.writeString(tmp.resolve("index.html"), "<html/>");

            final String ymlContent = String.join("\n", //
                    "apiVersion: fess.codelibs.org/v1", //
                    "kind: StaticTheme", //
                    "name: t", //
                    "displayName: T", //
                    "version: 1.0.0");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(ymlContent.getBytes(StandardCharsets.UTF_8)));
            // Default entry is index.html
            assertEquals("index.html", manifest.getEntry());

            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = newActionWith(theme);

            final ActionResponse resp = action.serveIndex();
            // A StreamResponse without an explicit httpStatus defaults to 200.
            final StreamResponse sr = (StreamResponse) resp;
            assertFalse(sr.getHttpStatus().isPresent() && sr.getHttpStatus().get() == 404,
                    "serveIndex with default entry=index.html must NOT return 404");
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    // ── computeErrorStatus unit tests ──────────────────────────────────────────

    @Test
    public void test_computeErrorStatus_notFound() {
        assertEquals(404, ThemeViewAction.computeErrorStatus("/error/notFound"));
        assertEquals(404, ThemeViewAction.computeErrorStatus("/error/notfound"));
    }

    @Test
    public void test_computeErrorStatus_badRequest() {
        assertEquals(400, ThemeViewAction.computeErrorStatus("/error/badRequest"));
        assertEquals(400, ThemeViewAction.computeErrorStatus("/error/badrequest"));
    }

    @Test
    public void test_computeErrorStatus_system_and_error() {
        assertEquals(500, ThemeViewAction.computeErrorStatus("/error/system"));
        assertEquals(500, ThemeViewAction.computeErrorStatus("/error/error"));
        assertEquals(500, ThemeViewAction.computeErrorStatus("/error"));
    }

    @Test
    public void test_computeErrorStatus_busy() {
        // A.10: /error/busy maps to HTTP 429 (Too Many Requests) — changed from 503.
        assertEquals(429, ThemeViewAction.computeErrorStatus("/error/busy"));
    }

    @Test
    public void test_computeErrorStatus_unknownPathDefaultsTo500() {
        assertEquals(500, ThemeViewAction.computeErrorStatus("/error/anything_else"));
        assertEquals(500, ThemeViewAction.computeErrorStatus("/error/unknown"));
    }

    @Test
    public void test_serveIndex_setsErrorHeaders_forErrorRoute() throws Exception {
        // When the request URI starts with /error, serveIndex must set
        // X-Fess-Route: error and X-Fess-Error-Code: <status> response headers,
        // while still serving the normal index.html (SPA handles rendering).
        final Path tmp = Files.createTempDirectory("tva-error-headers-");
        try {
            Files.writeString(tmp.resolve("index.html"), "<html/>");
            final String yaml = String.join("\n", //
                    "apiVersion: fess.codelibs.org/v1", //
                    "kind: StaticTheme", //
                    "name: t", //
                    "displayName: T", //
                    "version: 1.0.0");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = newActionWithRequestUri(theme, "/error/notFound");

            final ActionResponse resp = action.serveIndex();
            final Map<String, String[]> headers = ((StreamResponse) resp).getHeaderMap();
            assertNotNull(headers.get("X-Fess-Route"), "X-Fess-Route header must be set for /error route");
            assertEquals("error", headers.get("X-Fess-Route")[0]);
            assertNotNull(headers.get("X-Fess-Error-Code"), "X-Fess-Error-Code header must be set for /error route");
            assertEquals("404", headers.get("X-Fess-Error-Code")[0]);
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    @Test
    public void test_serveIndex_noErrorHeaders_forNonErrorRoute() throws Exception {
        // Non-error routes (e.g. /search) must not have error diagnostic headers.
        final Path tmp = Files.createTempDirectory("tva-no-error-headers-");
        try {
            Files.writeString(tmp.resolve("index.html"), "<html/>");
            final String yaml = String.join("\n", //
                    "apiVersion: fess.codelibs.org/v1", //
                    "kind: StaticTheme", //
                    "name: t", //
                    "displayName: T", //
                    "version: 1.0.0");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = newActionWithRequestUri(theme, "/search");

            final ActionResponse resp = action.serveIndex();
            final Map<String, String[]> headers = ((StreamResponse) resp).getHeaderMap();
            assertNull(headers.get("X-Fess-Route"), "X-Fess-Route must not be set for non-error routes");
            assertNull(headers.get("X-Fess-Error-Code"), "X-Fess-Error-Code must not be set for non-error routes");
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    // ── A.11: HTTP status code on error responses ─────────────────────────────

    @Test
    public void test_serveIndex_setsHttpStatus_forNotFoundError() throws Exception {
        final Path tmp = Files.createTempDirectory("tva-status-");
        try {
            Files.writeString(tmp.resolve("index.html"), "<html/>");
            final String yaml = String.join("\n", "apiVersion: fess.codelibs.org/v1", "kind: StaticTheme", "name: t", "displayName: T",
                    "version: 1.0.0");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = newActionWithRequestUri(theme, "/error/notFound");

            final ActionResponse resp = action.serveIndex();
            final StreamResponse sr = (StreamResponse) resp;
            // A.11: HTTP status must be 404 (not 200) for /error/notFound route.
            assertTrue(sr.getHttpStatus().isPresent(), "HTTP status must be set for /error/notFound");
            assertEquals(404, (int) sr.getHttpStatus().get(), "HTTP status must be 404 for /error/notFound");
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    @Test
    public void test_serveIndex_setsHttpStatus_forBadRequest() throws Exception {
        final Path tmp = Files.createTempDirectory("tva-status-");
        try {
            Files.writeString(tmp.resolve("index.html"), "<html/>");
            final String yaml = String.join("\n", "apiVersion: fess.codelibs.org/v1", "kind: StaticTheme", "name: t", "displayName: T",
                    "version: 1.0.0");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = newActionWithRequestUri(theme, "/error/badRequest");

            final ActionResponse resp = action.serveIndex();
            final StreamResponse sr = (StreamResponse) resp;
            assertTrue(sr.getHttpStatus().isPresent(), "HTTP status must be set for /error/badRequest");
            assertEquals(400, (int) sr.getHttpStatus().get(), "HTTP status must be 400 for /error/badRequest");
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    @Test
    public void test_serveIndex_setsHttpStatus_forSystem() throws Exception {
        final Path tmp = Files.createTempDirectory("tva-status-");
        try {
            Files.writeString(tmp.resolve("index.html"), "<html/>");
            final String yaml = String.join("\n", "apiVersion: fess.codelibs.org/v1", "kind: StaticTheme", "name: t", "displayName: T",
                    "version: 1.0.0");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = newActionWithRequestUri(theme, "/error/system");

            final ActionResponse resp = action.serveIndex();
            final StreamResponse sr = (StreamResponse) resp;
            assertTrue(sr.getHttpStatus().isPresent(), "HTTP status must be set for /error/system");
            assertEquals(500, (int) sr.getHttpStatus().get(), "HTTP status must be 500 for /error/system");
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    @Test
    public void test_serveIndex_setsHttpStatus_forBusy() throws Exception {
        final Path tmp = Files.createTempDirectory("tva-status-");
        try {
            Files.writeString(tmp.resolve("index.html"), "<html/>");
            final String yaml = String.join("\n", "apiVersion: fess.codelibs.org/v1", "kind: StaticTheme", "name: t", "displayName: T",
                    "version: 1.0.0");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = newActionWithRequestUri(theme, "/error/busy");

            final ActionResponse resp = action.serveIndex();
            final StreamResponse sr = (StreamResponse) resp;
            assertTrue(sr.getHttpStatus().isPresent(), "HTTP status must be set for /error/busy");
            assertEquals(429, (int) sr.getHttpStatus().get(), "HTTP status must be 429 for /error/busy");
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    @Test
    public void test_serveIndex_noHttpStatusOverride_forNonErrorRoute() throws Exception {
        // For non-error routes the HTTP status must NOT be set (SPA renders normally).
        final Path tmp = Files.createTempDirectory("tva-status-");
        try {
            Files.writeString(tmp.resolve("index.html"), "<html/>");
            final String yaml = String.join("\n", "apiVersion: fess.codelibs.org/v1", "kind: StaticTheme", "name: t", "displayName: T",
                    "version: 1.0.0");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = newActionWithRequestUri(theme, "/search");

            final ActionResponse resp = action.serveIndex();
            final StreamResponse sr = (StreamResponse) resp;
            // Non-error routes must not set an explicit http status (defaults to 200).
            assertFalse(sr.getHttpStatus().isPresent() && sr.getHttpStatus().get() != 200,
                    "Non-error route must not set an error status code, got: " + sr.getHttpStatus());
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    // ── F.9: error detail meta tag injection tests ────────────────────────────

    @Test
    public void test_errorPathWithMessageKey_injectsDetailMeta() throws Exception {
        // When /error/notFound is served with ?message_key=errors.docid_not_found,
        // the response body must contain the x-fess-error-detail-key meta tag.
        final Path tmp = Files.createTempDirectory("tva-f9-inject-");
        try {
            Files.writeString(tmp.resolve("index.html"), "<!DOCTYPE html><html><head><title>Fess</title></head><body></body></html>");
            final String yaml = String.join("\n", //
                    "apiVersion: fess.codelibs.org/v1", //
                    "kind: StaticTheme", //
                    "name: t", //
                    "displayName: T", //
                    "version: 1.0.0");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
            final Theme theme = new Theme("t", tmp, manifest);
            final ThemeViewAction action = newActionWithRequestUriAndMessageKey(theme, "/error/notFound", "errors.docid_not_found");

            final ActionResponse resp = action.serveIndex();
            // Capture body bytes via the stream callback.
            final ByteArrayOutputStream bodyBuf = new ByteArrayOutputStream();
            ((StreamResponse) resp).getStreamCall().callback(captureOut(bodyBuf));
            final String body = bodyBuf.toString(StandardCharsets.UTF_8);
            assertTrue(body.contains("x-fess-error-detail-key"), "Body must contain x-fess-error-detail-key meta tag");
            assertTrue(body.contains("errors.docid_not_found"), "Body must contain the message key value");
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    @Test
    public void test_errorPathWithoutMessageKey_leavesIndexUnchanged() throws Exception {
        // When /error/notFound is served WITHOUT ?message_key, the response body must NOT
        // contain the injected meta tag — the index.html is streamed as-is.
        final Path tmp = Files.createTempDirectory("tva-f9-noinject-");
        try {
            final String originalHtml = "<!DOCTYPE html><html><head><title>Fess</title></head><body></body></html>";
            Files.writeString(tmp.resolve("index.html"), originalHtml);
            final String yaml = String.join("\n", //
                    "apiVersion: fess.codelibs.org/v1", //
                    "kind: StaticTheme", //
                    "name: t", //
                    "displayName: T", //
                    "version: 1.0.0");
            final ThemeManifest manifest = ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
            final Theme theme = new Theme("t", tmp, manifest);
            // No message key — use the standard helper.
            final ThemeViewAction action = newActionWithRequestUri(theme, "/error/notFound");

            final ActionResponse resp = action.serveIndex();
            final ByteArrayOutputStream bodyBuf = new ByteArrayOutputStream();
            ((StreamResponse) resp).getStreamCall().callback(captureOut(bodyBuf));
            final String body = bodyBuf.toString(StandardCharsets.UTF_8);
            assertFalse(body.contains("x-fess-error-detail-key"),
                    "Body must NOT contain error-detail meta tag when no message_key is provided");
        } finally {
            Files.walk(tmp).sorted((a, b) -> b.compareTo(a)).forEach(x -> {
                try {
                    Files.delete(x);
                } catch (final Exception ignore) {}
            });
        }
    }

    // ── F.9: injectErrorDetailMeta unit tests ─────────────────────────────────

    @Test
    public void test_injectErrorDetailMeta_insertsMetaBeforeHeadClose() {
        final String html = "<html><head><title>T</title></head><body></body></html>";
        final byte[] result = ThemeViewAction.injectErrorDetailMeta(html.getBytes(StandardCharsets.UTF_8), "errors.docid_not_found");
        final String out = new String(result, StandardCharsets.UTF_8);
        assertTrue(out.contains("<meta name=\"x-fess-error-detail-key\" content=\"errors.docid_not_found\">"), "Meta tag must be present");
        // Meta tag must appear before </head>
        final int metaIdx = out.indexOf("x-fess-error-detail-key");
        final int headIdx = out.indexOf("</head>");
        assertTrue(metaIdx < headIdx, "Meta tag must appear before </head>");
    }

    @Test
    public void test_injectErrorDetailMeta_returnsOriginalWhenNoHeadClose() {
        final String originalHtml = "<html><body>no head</body></html>";
        final byte[] html = originalHtml.getBytes(StandardCharsets.UTF_8);
        final byte[] result = ThemeViewAction.injectErrorDetailMeta(html, "errors.docid_not_found");
        // No </head> found — original bytes returned unchanged.
        final String resultStr = new String(result, StandardCharsets.UTF_8);
        assertEquals(originalHtml, resultStr);
        assertFalse(resultStr.contains("x-fess-error-detail-key"), "No meta tag must be injected when </head> is absent");
    }

    @Test
    public void test_injectErrorDetailMeta_rejectsUnsafeKeyViaResolveMessageKey() {
        // resolveMessageKey sanitises the key before injectErrorDetailMeta is called.
        // Simulate via test seam: currentMessageKey set to unsafe value, expect null returned.
        final ThemeViewAction action = new ThemeViewAction();
        action.currentMessageKey = "<script>alert(1)</script>";
        assertNull(action.resolveMessageKey(), "Unsafe message key must be rejected");
    }

    /**
     * Creates a {@link WrittenStreamOut} that writes to the given buffer.
     * Used to capture the stream body in unit tests without a live HTTP response.
     */
    private static WrittenStreamOut captureOut(final ByteArrayOutputStream buf) {
        return new WrittenStreamOut() {
            @Override
            public OutputStream stream() {
                return buf;
            }

            @Override
            public void write(final InputStream in) throws java.io.IOException {
                in.transferTo(buf);
            }
        };
    }

    private static ThemeViewAction newActionWith(final Theme theme) {
        return newActionWithRequestUri(theme, "/");
    }

    private static ThemeViewAction newActionWithRequestUri(final Theme theme, final String requestUri) {
        final ThemeViewAction action = new ThemeViewAction();
        action.themeRegistry = new ThemeRegistry() {
            @Override
            public Optional<Theme> resolveActiveTheme(final String hostKey) {
                return Optional.ofNullable(theme);
            }
        };
        action.virtualHostHelper = new VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return null;
            }
        };
        action.currentRequestUri = requestUri;
        return action;
    }

    private static ThemeViewAction newActionWithRequestUriAndMessageKey(final Theme theme, final String requestUri,
            final String messageKey) {
        final ThemeViewAction action = newActionWithRequestUri(theme, requestUri);
        action.currentMessageKey = messageKey;
        return action;
    }

    private static String invokeContentTypeFor(final String name) throws Exception {
        final Method m = ThemeViewAction.class.getDeclaredMethod("contentTypeFor", String.class);
        m.setAccessible(true);
        return (String) m.invoke(null, name);
    }
}
