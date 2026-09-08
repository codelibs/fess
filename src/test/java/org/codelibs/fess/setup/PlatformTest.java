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
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class PlatformTest {

    @Test
    public void test_osFrom() {
        assertEquals(Platform.Os.LINUX, Platform.osFrom("Linux"));
        assertEquals(Platform.Os.WINDOWS, Platform.osFrom("Windows Server 2022"));
        assertEquals(Platform.Os.MACOS, Platform.osFrom("Mac OS X"));
        assertEquals(Platform.Os.UNKNOWN, Platform.osFrom("SunOS"));
        assertEquals(Platform.Os.UNKNOWN, Platform.osFrom(null));
    }

    @Test
    public void test_archFrom() {
        assertEquals("x64", Platform.archFrom("amd64"));
        assertEquals("x64", Platform.archFrom("x86_64"));
        assertEquals("arm64", Platform.archFrom("aarch64"));
        assertEquals("arm64", Platform.archFrom("arm64"));
        assertNull(Platform.archFrom("x86"));
        assertNull(Platform.archFrom(null));
    }

    @Test
    public void test_archiveExt() {
        assertEquals("tar.gz", Platform.archiveExt(Platform.Os.LINUX));
        assertEquals("zip", Platform.archiveExt(Platform.Os.WINDOWS));
    }
}
