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
 * Unit tests for {@link LogUtil#sanitize}. Pure function; no container required.
 */
public class LogUtilTest {

    @Test
    public void test_nullStaysNull() {
        assertNull(LogUtil.sanitize(null));
    }

    @Test
    public void test_ordinaryValueIsUnchanged() {
        assertEquals("alice", LogUtil.sanitize("alice"));
        assertEquals("山田 太郎@EXAMPLE.COM", LogUtil.sanitize("山田 太郎@EXAMPLE.COM"));
    }

    @Test
    public void test_lineBreaksAndOtherControlCharactersAreReplaced() {
        assertEquals("a?b", LogUtil.sanitize("a\nb"));
        assertEquals("a?b", LogUtil.sanitize("a\rb"));
        assertEquals("a??b", LogUtil.sanitize("a\r\nb"));
        assertEquals("a?b", LogUtil.sanitize("a\tb"));
        assertEquals("a?b", LogUtil.sanitize("a\u0000b"));
        assertEquals("a?b", LogUtil.sanitize("a\u007fb"));
    }

    @Test
    public void test_unicodeLineSeparatorsAreReplaced() {
        // \p{Cntrl} is ASCII-only: NEL and the line and paragraph separators need their own entry,
        // as a consumer that splits on them would otherwise still see a second line.
        assertEquals("a?b", LogUtil.sanitize("a\u0085b"));
        assertEquals("a?b", LogUtil.sanitize("a\u2028b"));
        assertEquals("a?b", LogUtil.sanitize("a\u2029b"));
    }

    @Test
    public void test_longValueIsCutAndMarked() {
        final String longValue = "x".repeat(LogUtil.MAX_LOGGED_LENGTH + 50);
        assertEquals("x".repeat(LogUtil.MAX_LOGGED_LENGTH) + "...", LogUtil.sanitize(longValue));
        assertEquals("x".repeat(LogUtil.MAX_LOGGED_LENGTH), LogUtil.sanitize("x".repeat(LogUtil.MAX_LOGGED_LENGTH)));
    }
}
