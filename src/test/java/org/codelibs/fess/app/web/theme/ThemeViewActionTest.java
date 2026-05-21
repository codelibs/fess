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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.codelibs.fess.theme.Theme;
import org.codelibs.fess.theme.ThemeManifest;
import org.codelibs.fess.theme.ThemeType;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

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
            final Theme theme = new Theme(ThemeType.STATIC, "t", tmp, manifest);

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
        assertEquals("application/javascript", invokeContentTypeFor("app.js"));
        assertEquals("text/css", invokeContentTypeFor("styles.css"));
        assertEquals("text/html; charset=UTF-8", invokeContentTypeFor("index.html"));
        assertEquals("application/json; charset=UTF-8", invokeContentTypeFor("manifest.json"));
        assertEquals("image/svg+xml", invokeContentTypeFor("icon.svg"));
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
            final Theme theme = new Theme(ThemeType.STATIC, "t", tmp, manifest);
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

    private static String invokeContentTypeFor(final String name) throws Exception {
        final Method m = ThemeViewAction.class.getDeclaredMethod("contentTypeFor", String.class);
        m.setAccessible(true);
        return (String) m.invoke(null, name);
    }
}
