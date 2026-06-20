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
package org.codelibs.fess.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link OriginUtil#canonicalize}: origin normalization and
 * rejection of malformed/unusable inputs. Pure function; no container required.
 */
public class OriginUtilTest {

    // normalization

    @Test
    public void test_simpleOriginIsPreserved() {
        assertEquals("https://example.com", OriginUtil.canonicalize("https://example.com"));
    }

    @Test
    public void test_hostIsLowerCased() {
        assertEquals("https://app.example.com", OriginUtil.canonicalize("https://App.Example.COM"));
    }

    @Test
    public void test_schemeIsLowerCased() {
        assertEquals("https://example.com", OriginUtil.canonicalize("HTTPS://example.com"));
    }

    @Test
    public void test_defaultHttpsPortIsStripped() {
        assertEquals("https://example.com", OriginUtil.canonicalize("https://example.com:443"));
    }

    @Test
    public void test_defaultHttpPortIsStripped() {
        assertEquals("http://example.com", OriginUtil.canonicalize("http://example.com:80"));
    }

    @Test
    public void test_nonDefaultPortIsKept() {
        assertEquals("https://example.com:8443", OriginUtil.canonicalize("https://example.com:8443"));
        assertEquals("http://example.com:8080", OriginUtil.canonicalize("http://example.com:8080"));
    }

    @Test
    public void test_pathQueryFragmentAreStripped() {
        assertEquals("https://example.com", OriginUtil.canonicalize("https://example.com/path/to?x=1&y=2#frag"));
    }

    @Test
    public void test_fullUrlWithEverythingNormalized() {
        // lower-cased host, default port stripped, path/query discarded
        assertEquals("https://app.example.com", OriginUtil.canonicalize("https://App.Example.com:443/path?x"));
    }

    @Test
    public void test_leadingTrailingWhitespaceIsTrimmedButInnerRejected() {
        assertEquals("https://example.com", OriginUtil.canonicalize("  https://example.com  "));
    }

    // invalid inputs -> null

    @Test
    public void test_nullInput() {
        assertNull(OriginUtil.canonicalize(null));
    }

    @Test
    public void test_emptyInput() {
        assertNull(OriginUtil.canonicalize(""));
    }

    @Test
    public void test_blankInput() {
        assertNull(OriginUtil.canonicalize("   "));
    }

    @Test
    public void test_literalNullStringInput() {
        assertNull(OriginUtil.canonicalize("null"));
        assertNull(OriginUtil.canonicalize("NULL"));
    }

    @Test
    public void test_multipleSpaceSeparatedValues() {
        assertNull(OriginUtil.canonicalize("https://a.example.com https://b.example.com"));
    }

    @Test
    public void test_carriageReturnRejected() {
        assertNull(OriginUtil.canonicalize("https://example.com\r"));
    }

    @Test
    public void test_lineFeedRejected() {
        assertNull(OriginUtil.canonicalize("https://example.com\n"));
    }

    @Test
    public void test_crlfInjectionRejected() {
        assertNull(OriginUtil.canonicalize("https://example.com\r\nSet-Cookie: x=1"));
    }

    @Test
    public void test_tabRejected() {
        assertNull(OriginUtil.canonicalize("https://example.com\t"));
    }

    @Test
    public void test_innerControlCharRejected() {
        assertNull(OriginUtil.canonicalize("https://exa\u0000mple.com"));
    }

    @Test
    public void test_missingSchemeRejected() {
        assertNull(OriginUtil.canonicalize("example.com"));
        assertNull(OriginUtil.canonicalize("//example.com"));
    }

    @Test
    public void test_missingHostRejected() {
        // A scheme with no authority component yields no host.
        assertNull(OriginUtil.canonicalize("https:///path"));
        assertNull(OriginUtil.canonicalize("mailto:user@example.com"));
    }

    @Test
    public void test_unparseableRejected() {
        assertNull(OriginUtil.canonicalize("httpsexample"));
        assertNull(OriginUtil.canonicalize("http://[invalid"));
    }
}
