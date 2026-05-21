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
}
