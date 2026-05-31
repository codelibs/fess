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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class ThemeManifestTest extends UnitFessTestCase {

    @Test
    public void test_parse_validMinimalManifest() throws Exception {
        final String yaml = String.join("\n", //
                "apiVersion: fess.codelibs.org/v1", //
                "kind: StaticTheme", //
                "name: my-theme", //
                "displayName: \"My Theme\"", //
                "version: \"1.0.0\"");
        final ThemeManifest m = ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
        assertEquals("my-theme", m.getName());
        assertEquals("My Theme", m.getDisplayName());
        assertEquals("1.0.0", m.getVersion());
        assertEquals("index.html", m.getEntry());
        assertTrue(m.isSpaFallback());
    }

    @Test
    public void test_parse_rejectsMissingApiVersion() {
        final String yaml = "name: a\nkind: StaticTheme\ndisplayName: x\nversion: 1.0.0";
        final ThemeManifestException e =
                assertThrows(ThemeManifestException.class, () -> ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes())));
        assertTrue(e.getMessage().contains("apiVersion"));
    }

    @Test
    public void test_parse_rejectsBadNameRegex() {
        final String yaml = "apiVersion: fess.codelibs.org/v1\nkind: StaticTheme\nname: Bad_NAME\ndisplayName: x\nversion: 1.0.0";
        assertThrows(ThemeManifestException.class, () -> ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes())));
    }

    @Test
    public void test_parse_rejectsBadSemver() {
        final String yaml = "apiVersion: fess.codelibs.org/v1\nkind: StaticTheme\nname: t\ndisplayName: x\nversion: v1";
        assertThrows(ThemeManifestException.class, () -> ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes())));
    }

    @Test
    public void test_parse_rejectsEntryPathTraversal() {
        final String yaml =
                "apiVersion: fess.codelibs.org/v1\nkind: StaticTheme\nname: t\ndisplayName: x\nversion: 1.0.0\nentry: \"../etc/passwd\"";
        assertThrows(ThemeManifestException.class, () -> ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes())));
    }

    @Test
    public void test_parse_emptyInput() {
        assertThrows(ThemeManifestException.class, () -> ThemeManifest.parse(new ByteArrayInputStream(new byte[0])));
    }

    @Test
    public void test_parse_rejectsOversizedDisplayName() {
        // displayName now has field-length check; a value > 4096 chars must be rejected.
        final String huge = "A".repeat(5000);
        final String yaml = "apiVersion: fess.codelibs.org/v1\nkind: StaticTheme\nname: t\ndisplayName: \"" + huge + "\"\nversion: 1.0.0";
        final ThemeManifestException ex =
                assertThrows(ThemeManifestException.class, () -> ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes())));
        assertTrue(ex.getMessage().contains("displayName"), "Error must identify the 'displayName' field");
    }

    @Test
    public void test_parse_rejectsOversizedLicense() {
        final String huge = "X".repeat(5000);
        final String yaml =
                "apiVersion: fess.codelibs.org/v1\nkind: StaticTheme\nname: t\ndisplayName: T\nversion: 1.0.0\nlicense: \"" + huge + "\"";
        final ThemeManifestException ex =
                assertThrows(ThemeManifestException.class, () -> ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes())));
        assertTrue(ex.getMessage().contains("license"), "Error must identify the 'license' field");
    }

    @Test
    public void test_parse_rejectsOversizedMinFessVersion() {
        final String huge = "1".repeat(5000);
        final String yaml =
                "apiVersion: fess.codelibs.org/v1\nkind: StaticTheme\nname: t\ndisplayName: T\nversion: 1.0.0\nminFessVersion: \"" + huge
                        + "\"";
        final ThemeManifestException ex =
                assertThrows(ThemeManifestException.class, () -> ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes())));
        assertTrue(ex.getMessage().contains("minFessVersion"), "Error must identify the 'minFessVersion' field");
    }

    @Test
    public void test_parse_rejectsOversizedEntry() {
        final String huge = "a/".repeat(2500);
        final String yaml = "apiVersion: fess.codelibs.org/v1\nkind: StaticTheme\nname: t\ndisplayName: T\nversion: 1.0.0\nentry: \"" + huge
                + "index.html\"";
        final ThemeManifestException ex =
                assertThrows(ThemeManifestException.class, () -> ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes())));
        assertTrue(ex.getMessage().contains("entry"), "Error must identify the 'entry' field");
    }

    @Test
    public void test_parse_rejectsOversizedSupportedLocale() {
        final String huge = "e".repeat(5000);
        final String yaml =
                "apiVersion: fess.codelibs.org/v1\nkind: StaticTheme\nname: t\ndisplayName: T\nversion: 1.0.0\nsupportedLocales:\n  - \""
                        + huge + "\"";
        final ThemeManifestException ex =
                assertThrows(ThemeManifestException.class, () -> ThemeManifest.parse(new ByteArrayInputStream(yaml.getBytes())));
        assertTrue(ex.getMessage().contains("supportedLocales"), "Error must identify the 'supportedLocales' field");
    }
}
