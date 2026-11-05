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

import java.util.List;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class ThemeSettingsUtilTest extends UnitFessTestCase {

    private static final String LF = String.valueOf((char) 10);

    private static final String CR = String.valueOf((char) 13);

    private static final String SP = String.valueOf((char) 32);

    private static ThemeSettingsUtil.Parsed parse(final String value) {
        return ThemeSettingsUtil.parse(value);
    }

    private static String text(final Map<String, String> settings) {
        return ThemeSettingsUtil.toText(settings);
    }

    private static final String LINES_VALID = SP + "b.c-d_e" + SP + "=" + SP + "two words" + SP + CR + LF + "a=1" + LF + "flag=" + LF;

    private static final String EQ = String.valueOf((char) 61);

    @Test
    public void test_emptyValues() {
        assertTrue(parse(null).settings().isEmpty());
        assertTrue(parse(StringUtil.EMPTY).settings().isEmpty());
        assertTrue(parse(SP + CR + LF + SP + LF + LF).settings().isEmpty());
        assertEquals(0, parse(SP + LF + SP).invalid());
        assertTrue(parse(null).isAcceptable());
        assertTrue(ThemeSettingsUtil.isAcceptable(SP + LF));
    }

    @Test
    public void test_validLines_trimmedAndOrdered() {
        final ThemeSettingsUtil.Parsed parsed = parse(LINES_VALID);
        assertEquals(List.of("b.c-d_e", "a", "flag"), List.copyOf(parsed.settings().keySet()));
        assertEquals("two words", parsed.settings().get("b.c-d_e"));
        assertEquals("1", parsed.settings().get("a"));
        assertEquals(StringUtil.EMPTY, parsed.settings().get("flag"));
        assertEquals(0, parsed.invalid());
    }

    @Test
    public void test_valueMayContainTheSeparator() {
        assertEquals("a" + EQ + "b", parse("u" + EQ + "a" + EQ + "b").settings().get("u"));
    }

    @Test
    public void test_repeatedKeyLaterLineWins() {
        final ThemeSettingsUtil.Parsed parsed = parse("k" + EQ + "1" + LF + "other" + EQ + "x" + LF + "k" + EQ + "2");
        assertEquals("k" + EQ + "2" + LF + "other" + EQ + "x", text(parsed.settings()));
        assertTrue(parsed.isAcceptable());
    }

    @Test
    public void test_invalidKeysAreRejected() {
        assertRejected("no separator");
        assertRejected(EQ + "v");
        assertBadKey("-a");
        assertBadKey("_a");
        assertBadKey("a b");
        assertBadKey("a/b");
        assertBadKey("ключ");
    }

    @Test
    public void test_reservedAndTooLongKeysAreRejected() {
        assertBadKey("__proto__");
        assertBadKey("constructor");
        assertBadKey("prototype");
        assertBadKey(filler(65));
        assertEquals(1, parse(filler(64) + EQ + "1").settings().size());
        assertEquals(1, parse("0" + EQ + "1").settings().size());
    }

    @Test
    public void test_valueLengthLimit() {
        assertEquals(ThemeSettingsUtil.MAX_VALUE_LENGTH, parse("k" + EQ + filler(1000)).settings().get("k").length());
        assertRejected("k" + EQ + filler(1001));
    }

    @Test
    public void test_controlCharactersAreRejected() {
        List.of(0, 7, 9, 11, 12, 127, 133).forEach(this::assertControlRejected);
    }

    @Test
    public void test_entryCountLimit() {
        assertTrue(parse(lines(ThemeSettingsUtil.MAX_ENTRIES)).isAcceptable());
        assertFalse(parse(lines(ThemeSettingsUtil.MAX_ENTRIES + 1)).isAcceptable());
        assertEquals(ThemeSettingsUtil.MAX_ENTRIES + 1, parse(lines(ThemeSettingsUtil.MAX_ENTRIES + 1)).settings().size());
    }

    @Test
    public void test_sanitizeDropsInvalidLinesAndCaps() {
        final Map<String, String> settings =
                ThemeSettingsUtil.sanitize("bad line" + LF + "ok" + EQ + "1" + LF + "constructor" + EQ + "x" + LF + "k" + EQ + "v");
        assertEquals("ok" + EQ + "1" + LF + "k" + EQ + "v", text(settings));
        assertTrue(ThemeSettingsUtil.sanitize(null).isEmpty());
        assertTrue(ThemeSettingsUtil.sanitize(SP + LF).isEmpty());
        assertEquals(ThemeSettingsUtil.MAX_ENTRIES, ThemeSettingsUtil.sanitize(lines(ThemeSettingsUtil.MAX_ENTRIES + 10)).size());
    }

    @Test
    public void test_toStoredValue() {
        assertEquals("a" + EQ + "2" + LF + "b" + EQ + "x",
                ThemeSettingsUtil.toStoredValue(SP + "a" + SP + EQ + SP + "1" + LF + "a" + EQ + "2" + LF + "b" + EQ + "x"));
        assertNull(ThemeSettingsUtil.toStoredValue(null));
        assertNull(ThemeSettingsUtil.toStoredValue(SP + LF + SP));
        assertEquals(StringUtil.EMPTY, text(Map.of()));
    }

    private void assertRejected(final String value) {
        final ThemeSettingsUtil.Parsed parsed = parse(value);
        assertTrue(parsed.settings().isEmpty(), value);
        assertEquals(value, 1, parsed.invalid());
        assertFalse(parsed.isAcceptable(), value);
    }

    private void assertBadKey(final String key) {
        assertRejected(key + EQ + "1");
    }

    private void assertControlRejected(final int code) {
        assertRejected("k" + EQ + "a" + new String(Character.toChars(code)) + "b");
    }

    private static String filler(final int count) {
        return "x".repeat(count);
    }

    private static String lines(final int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> "k" + i + EQ + "v")
                .collect(java.util.stream.Collectors.joining(LF));
    }
}
