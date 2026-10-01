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
package org.codelibs.fess.chat;

import java.util.Arrays;
import java.util.List;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DocumentPartSplitterTest extends UnitFessTestCase {

    private void assertAllWithin(final List<String> parts, final int maxChars) {
        for (final String part : parts) {
            assertTrue(part.length() <= maxChars, "part longer than " + maxChars + ": " + part.length());
            assertFalse(part.isBlank(), "blank part");
        }
    }

    // ===================================================================================
    //                                                                          splitText
    //                                                                          =========

    @Test
    public void test_splitText_emptyBlankAndNullYieldNoParts() {
        assertTrue(DocumentPartSplitter.splitText("", 10).isEmpty());
        assertTrue(DocumentPartSplitter.splitText("  \n\t ", 10).isEmpty());
        assertTrue(DocumentPartSplitter.splitText(null, 10).isEmpty());
    }

    @Test
    public void test_splitText_exactFitIsOnePart() {
        final String text = "a".repeat(10);
        assertEquals(List.of(text), DocumentPartSplitter.splitText(text, 10));
    }

    @Test
    public void test_splitText_oneOverTheLimitIsHardCut() {
        assertEquals(List.of("a".repeat(10), "a"), DocumentPartSplitter.splitText("a".repeat(11), 10));
    }

    @Test
    public void test_splitText_hardCutWhenThereIsNoBoundary() {
        assertEquals(List.of("a".repeat(10), "a".repeat(10), "a".repeat(5)), DocumentPartSplitter.splitText("a".repeat(25), 10));
    }

    @Test
    public void test_splitText_prefersTheLastNewline() {
        final List<String> parts = DocumentPartSplitter.splitText("line one is here\nline two is here too", 30);
        assertEquals(List.of("line one is here", "line two is here too"), parts);
    }

    @Test
    public void test_splitText_prefersSentenceEndOverWhitespace() {
        final List<String> parts = DocumentPartSplitter.splitText("Hello world. This is a test", 20);
        assertEquals(List.of("Hello world.", "This is a test"), parts);
    }

    @Test
    public void test_splitText_asciiSentenceEndNeedsFollowingWhitespace() {
        // "3.14" must not be cut after the dot; the last whitespace is used instead.
        final List<String> parts = DocumentPartSplitter.splitText("value is 3.14159265 and more", 16);
        assertEquals("value is", parts.get(0));
        assertAllWithin(parts, 16);
    }

    @Test
    public void test_splitText_cjkSentenceEndNeedsNoWhitespace() {
        final List<String> parts = DocumentPartSplitter.splitText("これは文です。次の文です。さらに続く文です。", 12);
        assertEquals("これは文です。", parts.get(0));
        assertAllWithin(parts, 12);
    }

    @Test
    public void test_splitText_fallsBackToWhitespace() {
        assertEquals(List.of("aaaa bbbb", "cccc dddd"), DocumentPartSplitter.splitText("aaaa bbbb cccc dddd", 12));
    }

    @Test
    public void test_splitText_ignoresBoundariesInTheFirstHalf() {
        // The only newline is near the start; cutting there would waste most of the part.
        final String text = "ab\n" + "c".repeat(30);
        final List<String> parts = DocumentPartSplitter.splitText(text, 20);
        assertEquals("ab\n" + "c".repeat(17), parts.get(0));
        assertAllWithin(parts, 20);
    }

    @Test
    public void test_splitText_neverSplitsASurrogatePair() {
        final String emoji = new String(Character.toChars(0x1F600));
        final List<String> parts = DocumentPartSplitter.splitText(emoji.repeat(6), 5);
        assertAllWithin(parts, 5);
        for (final String part : parts) {
            assertEquals(0, part.length() % 2, "a surrogate pair was split: " + part.length());
        }
        assertEquals(emoji.repeat(6), String.join("", parts));
    }

    @Test
    public void test_splitText_keepsAllNonWhitespaceTextInOrder() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            sb.append("word").append(i).append(i % 7 == 0 ? ".\n" : i % 5 == 0 ? ". " : " ");
        }
        final String text = sb.toString();
        for (final int max : new int[] { 1, 7, 50, 333 }) {
            final List<String> parts = DocumentPartSplitter.splitText(text, max);
            assertAllWithin(parts, max);
            assertEquals(text.replaceAll("\\s", ""), String.join("", parts).replaceAll("\\s", ""));
        }
    }

    @Test
    public void test_splitText_rejectsNonPositiveLimit() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> DocumentPartSplitter.splitText("abc", 0));
    }

    // ===================================================================================
    //                                                                        splitChunks
    //                                                                        ===========

    @Test
    public void test_splitChunks_packsConsecutiveChunks() {
        final List<String> parts = DocumentPartSplitter.splitChunks(List.of("aaaa", "bbbb", "cccc"), 10);
        assertEquals(List.of("aaaa\n\nbbbb", "cccc"), parts);
    }

    @Test
    public void test_splitChunks_exactFitAndOneOver() {
        assertEquals(List.of("a\n\nb"), DocumentPartSplitter.splitChunks(List.of("a", "b"), 4));
        assertEquals(List.of("a", "b"), DocumentPartSplitter.splitChunks(List.of("a", "b"), 3));
    }

    @Test
    public void test_splitChunks_splitsAChunkLongerThanTheLimit() {
        final List<String> parts = DocumentPartSplitter.splitChunks(Arrays.asList("aa", "x".repeat(25), "bb"), 10);
        assertEquals(List.of("aa", "x".repeat(10), "x".repeat(10), "x".repeat(5), "bb"), parts);
    }

    @Test
    public void test_splitChunks_skipsBlankAndNullChunks() {
        assertEquals(List.of("a\n\nb"), DocumentPartSplitter.splitChunks(Arrays.asList("a", null, "  ", "", "b"), 100));
    }

    @Test
    public void test_splitChunks_emptyYieldsNoParts() {
        assertTrue(DocumentPartSplitter.splitChunks(List.of(), 10).isEmpty());
    }

    @Test
    public void test_splitChunks_everyPartWithinTheLimit() {
        final List<String> chunks = java.util.stream.IntStream.range(0, 40).mapToObj(i -> "c".repeat(1 + (i * 7) % 23)).toList();
        final List<String> parts = DocumentPartSplitter.splitChunks(chunks, 30);
        assertAllWithin(parts, 30);
        assertEquals(String.join("", chunks), String.join("", parts).replace("\n", ""));
    }
}
